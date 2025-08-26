package escuela.edu.co;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class Response {

    private final OutputStream out;

    public Response(OutputStream out) {
        this.out = out;
    }

    /**
     * Envía una respuesta de texto plano con código 200.
     */
    public void sendText(String body) throws IOException {
        if (body == null) body = "";
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        String headers = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/plain; charset=utf-8\r\n" +
                "Content-Length: " + bytes.length + "\r\n" +
                "Connection: close\r\n\r\n";
        out.write(headers.getBytes(StandardCharsets.UTF_8));
        out.write(bytes);
    }

    /**
     * Envía una respuesta JSON con código 200.
     */
    public void sendJson(String json) throws IOException {
        if (json == null) json = "{}";
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        String headers = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: application/json; charset=utf-8\r\n" +
                "Content-Length: " + bytes.length + "\r\n" +
                "Access-Control-Allow-Origin: *\r\n" +
                "Connection: close\r\n\r\n";
        out.write(headers.getBytes(StandardCharsets.UTF_8));
        out.write(bytes);
    }

    /**
     * Envía cualquier texto con Content-Type personalizado.
     */
    public void sendWithContentType(String body, String contentType) throws IOException {
        if (body == null) body = "";
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        String headers = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: " + contentType + "\r\n" +
                "Content-Length: " + bytes.length + "\r\n" +
                "Connection: close\r\n\r\n";
        out.write(headers.getBytes(StandardCharsets.UTF_8));
        out.write(bytes);
    }

    /**
     * Envía respuesta 404.
     */
    public void send404(String path) throws IOException {
        String body = "<h1>404 - No encontrado</h1><p>Recurso no encontrado: " + path + "</p>";
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        String headers = "HTTP/1.1 404 Not Found\r\n" +
                "Content-Type: text/html; charset=utf-8\r\n" +
                "Content-Length: " + bytes.length + "\r\n" +
                "Connection: close\r\n\r\n";
        out.write(headers.getBytes(StandardCharsets.UTF_8));
        out.write(bytes);
    }

    /**
     * Envía 405 Method Not Allowed
     */
    public void send405() throws IOException {
        String body = "<h1>405 - Método no permitido</h1>";
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        String headers = "HTTP/1.1 405 Method Not Allowed\r\n" +
                "Content-Type: text/html; charset=utf-8\r\n" +
                "Content-Length: " + bytes.length + "\r\n" +
                "Connection: close\r\n\r\n";
        out.write(headers.getBytes(StandardCharsets.UTF_8));
        out.write(bytes);
    }
}
