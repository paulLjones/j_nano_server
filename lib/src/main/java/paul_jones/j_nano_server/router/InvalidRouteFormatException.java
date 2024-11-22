package paul_jones.j_nano_server.router;

public class InvalidRouteFormatException extends RuntimeException {
    private final String format;

    InvalidRouteFormatException(String format) {
        this.format = format;
    }

    InvalidRouteFormatException(String format, Throwable cause) {
        super(cause);
        this.format = format;
    }

    public String toString() {
        return String.format("Invalid Format: '%s'", format);
    }
}
