package escuela.edu.co;

import java.io.BufferedReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Request {
    private final String method;
    private final String path;
    private final Map<String, String> queryParams;
    private final Map<String, String> headers;
    private final BufferedReader bodyReader;

    public Request(String method, String path, Map<String, String> queryParams,
                   Map<String, String> headers, BufferedReader bodyReader) {
        this.method = method;
        this.path = path;
        this.queryParams = queryParams != null ? queryParams : new HashMap<>();
        this.headers = headers != null ? headers : new HashMap<>();
        this.bodyReader = bodyReader;
    }

    public String getMethod() { return method; }

    public String getPath() { return path; }

    /**
     * Obtiene el valor de un parámetro de consulta.
     * Devuelve cadena vacía si no existe.
     */
    public String getValues(String key) {
        if (key == null) return "";
        return queryParams.getOrDefault(key, "");
    }

    public Map<String, String> getQueryParams() {
        return Collections.unmodifiableMap(queryParams);
    }

    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }

    /**
     * Lee el cuerpo (body) si existe. Retorna null si no hay body o un error.
     */
    public String readBody() {
        if (bodyReader == null) return null;
        try {
            StringBuilder sb = new StringBuilder();
            while (bodyReader.ready()) {
                int c = bodyReader.read();
                if (c == -1) break;
                sb.append((char) c);
            }
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }
}
