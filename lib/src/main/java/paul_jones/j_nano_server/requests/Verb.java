package paul_jones.j_nano_server.requests;

public enum Verb {
    // Core
    GET,
    POST,
    PUT,
    PATCH,
    DELETE,

    // Extra
    HEAD,
    OPTIONS,
    CONNECT,
    TRACE,
    ;

    public boolean hasBody() {
        return switch (this) {
            case POST, PUT, PATCH -> true;
            default -> false;
        };
    }
}
