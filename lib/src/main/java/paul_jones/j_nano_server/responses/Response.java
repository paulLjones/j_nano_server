package paul_jones.j_nano_server.responses;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Map;

public abstract class Response {
    public abstract StatusCode getStatusCode();

    public abstract Map<String, String> getHeaders();

    public void outputTo(OutputStream outputStream) throws IOException {
        final var writer = new PrintWriter(outputStream);

        var statusCode = getStatusCode();
        writer.write(String.format("%s %d %s\n", "HTTP/1.1", statusCode.getCode(), statusCode));

        var headers = getHeaders();
        if (headers != null) {
            for (var entry : headers.entrySet()) {
                writer.write(String.format("%s: %s\n", entry.getKey(), entry.getValue()));
            }
        }

        writer.write("\r\n");

        writer.flush();
    }
}
