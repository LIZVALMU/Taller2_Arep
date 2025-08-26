package escuela.edu.co;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;


public class HttpServer {

    private static String STATIC_ROOT = "src/main/resources/static";
    private static final int SERVER_PORT = 35000;
    private boolean running = true;


     private static final Map<String, RouteHandler> GET_ROUTES = new HashMap<>();

    public static void main(String[] args) throws IOException {
        staticfiles("/static"); 
        get("/App/hello", (req, resp) -> {
            String name = req.getValues("name");
            if (name == null || name.isEmpty()) name = "Mundo";
            return String.format("{\"message\": \"Hello %s\", \"timestamp\": \"%s\"}",
                    name, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        });
        get("/App/pi", (req, resp) -> String.valueOf(Math.PI));

        // Inicia servidor
        new HttpServer();
    }
    /**
     * Initializes and starts the HTTP server on the specified port.
     * <p>
     * The server listens for incoming client connections and handles each connection
     * in a separate thread. If an I/O error occurs during server startup or while
     * accepting connections, the exception is printed to the standard error stream.
     * </p>
     */
    public HttpServer() {
        try {
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            System.out.println("Servidor HTTP iniciado en http://localhost:" + SERVER_PORT);

            while (running) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes and starts an HTTP server on the specified port.
     * The server listens for incoming client connections and handles each connection in a separate thread.
     *
     * @param port the port number on which the server will listen for incoming connections
     */
    public HttpServer(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor HTTP iniciado en http://localhost:" + port);
            while (running) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Stops the HTTP server by setting the running flag to false.
     * This method should be called to gracefully shut down the server loop.
     */
    private void stop() {
        running = false;
    }

    public static void staticfiles(String relativeOrAbsolute) {
        // intenta resolver a carpeta target/classes + relativeOrAbsolute si existe
        String candidate = "target/classes" + relativeOrAbsolute;

        File f = new File(candidate);
        if (f.exists() && f.isDirectory()) {
            STATIC_ROOT = candidate;
        } else {
            // si se pasa ruta absoluta, tomarla; sino fallback a la ruta pasada
            STATIC_ROOT = relativeOrAbsolute.startsWith(File.separator) ? relativeOrAbsolute : "src/main/resources" + relativeOrAbsolute;
        }
        System.out.println("Archivos estáticos en: " + STATIC_ROOT);
    }

    /**
     * Registra una ruta GET con su handler.
     */
    public static void get(String path, RouteHandler handler) {
        GET_ROUTES.put(path, handler);
    }


    private static void handleClient(Socket clientSocket) {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            OutputStream out = clientSocket.getOutputStream()
        ) {
            String requestLine = in.readLine();
            if (requestLine == null || requestLine.isEmpty()) {
                return;
            }

            Map<String, String> headers = readHeaders(in);

            String[] parts = requestLine.split(" ");
            if (parts.length < 2) {
                send400(out);
                return;
            }

            String method = parts[0];
            String fullPath = parts[1];

            String path;
            String queryString = "";
            int queryIndex = fullPath.indexOf('?');
            if (queryIndex != -1) {
                path = fullPath.substring(0, queryIndex);
                queryString = fullPath.substring(queryIndex + 1);
            } else {
                path = fullPath;
            }

            Map<String, String> queryParams = parseQueryString(queryString);

            if (path.startsWith("/app/")) {
                handleApiRequest(method, path, queryParams, headers, in, out);
                return;
            }

            if (!"GET".equals(method)) {
                send405(out);
                return;
            }

            if ("/".equals(path)) {
                path = "/index.html";
            }

            File file = new File(STATIC_ROOT + path);

            if (file.exists() && !file.isDirectory()) {
                sendFileResponse(out, file, path);
            } else {
                send404(out, path);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static Map<String, String> readHeaders(BufferedReader in) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String headerLine;
        while ((headerLine = in.readLine()) != null && !headerLine.isEmpty()) {
            int sep = headerLine.indexOf(": ");
            if (sep != -1) {
                String key = headerLine.substring(0, sep).toLowerCase();
                String value = headerLine.substring(sep + 2);
                headers.put(key, value);
            }
        }
        return headers;
    }

    private static void sendFileResponse(OutputStream out, File file, String path) throws IOException {
        String mimeType = Files.probeContentType(file.toPath());
        if (mimeType == null) {
            if (path.endsWith(".html")) mimeType = "text/html";
            else if (path.endsWith(".css")) mimeType = "text/css";
            else if (path.endsWith(".js")) mimeType = "application/javascript";
            else if (path.endsWith(".png")) mimeType = "image/png";
            else if (path.endsWith(".jpg") || path.endsWith(".jpeg")) mimeType = "image/jpeg";
            else mimeType = "application/octet-stream";
        }

        byte[] content = Files.readAllBytes(file.toPath());
        String response = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: " + mimeType + "\r\n" +
                "Content-Length: " + content.length + "\r\n" +
                "Connection: close\r\n\r\n";
        out.write(response.getBytes());
        out.write(content);
    }

    private static void handleApiRequest(String method, String path, Map<String, String> queryParams,
            Map<String, String> headers, BufferedReader in, OutputStream out) throws IOException {

        String jsonResponse = "";

        switch (path) {
            case "/app/hello":
                if (method.equals("GET") || method.equals("POST")) {
                    String name = queryParams.getOrDefault("name", "Mundo");
                    if (name.isEmpty())
                        name = "Mundo";
                    jsonResponse = String.format("{\"message\": \"¡Hola, %s!\", \"timestamp\": \"%s\"}",
                            name, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                } else {
                    send405(out);
                    return;
                }
                break;

            case "/app/time":
                if (method.equals("GET")) {
                    LocalDateTime now = LocalDateTime.now();
                    jsonResponse = String.format("{\"current_time\": \"%s\", \"formatted_time\": \"%s\"}",
                            now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                            now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
                } else {
                    send405(out);
                    return;
                }
                break;

            case "/app/sum":
                if (method.equals("GET")) {
                    try {
                        double a = Double.parseDouble(queryParams.getOrDefault("a", "0"));
                        double b = Double.parseDouble(queryParams.getOrDefault("b", "0"));
                        double result = a + b;
                        jsonResponse = String.format("{\"a\": %.2f, \"b\": %.2f, \"sum\": %.2f}", a, b, result);
                    } catch (NumberFormatException e) {
                        jsonResponse = "{\"error\": \"Parámetros inválidos. Use números válidos para a y b.\"}";
                    }
                } else {
                    send405(out);
                    return;
                }
                break;

            default:
                send404(out, path);
                return;
        }

        sendJsonResponse(out, jsonResponse);
    }

    /**
     * Parses a URL query string into a map of parameter names and values.
     *
     * <p>This method splits the input query string by '&' to separate key-value pairs,
     * then splits each pair by '=' to extract the key and value. Both key and value
     * are URL-decoded using UTF-8 encoding before being added to the resulting map.
     * If the query string is null or empty, an empty map is returned.
     *
     * @param queryString the URL query string to parse (e.g., "name=John&age=30")
     * @return a map containing parameter names as keys and their corresponding values
     */
    private static Map<String, String> parseQueryString(String queryString) {
        Map<String, String> params = new HashMap<>();
        if (queryString == null || queryString.isEmpty()) {
            return params;
        }

        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                try {
                    String key = URLDecoder.decode(keyValue[0], "UTF-8");
                    String value = URLDecoder.decode(keyValue[1], "UTF-8");
                    params.put(key, value);
                } catch (UnsupportedEncodingException e) {

                }
            }
        }
        return params;
    }

    /**
     * Sends an HTTP response with a JSON payload to the specified output stream.
     * The response includes appropriate headers for content type, length, CORS, and connection closure.
     *
     * @param out  the OutputStream to which the HTTP response will be written
     * @param json the JSON string to be sent as the response body
     * @throws IOException if an I/O error occurs while writing to the output stream
     */
    private static void sendJsonResponse(OutputStream out, String json) throws IOException {
        String response = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: application/json\r\n" +
                "Content-Length: " + json.getBytes().length + "\r\n" +
                "Access-Control-Allow-Origin: *\r\n" +
                "Connection: close\r\n\r\n" + json;
        out.write(response.getBytes());
    }

    /**
     * Sends a 400 Bad Request HTTP response to the client.
     *
     * The response includes a simple HTML message indicating that the request was incorrect.
     *
     * @param out the OutputStream to write the HTTP response to
     * @throws IOException if an I/O error occurs while writing to the output stream
     */
    private static void send400(OutputStream out) throws IOException {
        String response = "HTTP/1.1 400 Bad Request\r\n" +
                "Content-Type: text/html\r\n\r\n" +
                "<h1>400 - Solicitud Incorrecta</h1>";
        out.write(response.getBytes());
    }

    /**
     * Sends a 405 Method Not Allowed HTTP response to the client.
     *
     * This method writes an HTTP response with status code 405 and a simple HTML message
     * indicating that the requested method is not allowed. The response is sent through
     * the provided OutputStream.
     *
     * @param out the OutputStream to write the HTTP response to
     * @throws IOException if an I/O error occurs while writing to the stream
     */
    private static void send405(OutputStream out) throws IOException {
        String response = "HTTP/1.1 405 Method Not Allowed\r\n" +
                "Content-Type: text/html\r\n\r\n" +
                "<h1>405 - Método no permitido</h1>";
        out.write(response.getBytes());
    }

    /**
     * Sends a 404 Not Found HTTP response to the client.
     *
     * @param out  the OutputStream to write the response to
     * @param path the requested resource path that was not found
     * @throws IOException if an I/O error occurs while writing the response
     */
    private static void send404(OutputStream out, String path) throws IOException {
        String response = "HTTP/1.1 404 Not Found\r\n" +
                "Content-Type: text/html\r\n\r\n" +
                "<h1>404 - No encontrado</h1>" +
                "<p>Recurso no encontrado: " + path + "</p>" +
                "<a href=\"/\">Volver al inicio</a>";
        out.write(response.getBytes());
    }
}
