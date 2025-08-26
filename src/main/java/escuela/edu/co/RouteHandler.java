package escuela.edu.co;

@FunctionalInterface
public interface RouteHandler {
    String handle(Request req, Response res) throws Exception;
}
