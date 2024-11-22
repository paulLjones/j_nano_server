package paul_jones.j_nano_server.responses;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.TreeMap;
import java.util.Map;

public class StringResponse extends Response {
    public StatusCode statusCode;
    public TreeMap<String, String> headers;
    public String body;

    public StringResponse(StatusCode statusCode, TreeMap<String, String> headers, String body) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.body = body;

        if (!this.headers.containsKey("Content-Type")) {
            headers.put("Content-Type", "text");
        }

        if (!this.headers.containsKey("Content-Length")) {
            headers.put("Content-Length", String.valueOf(body.length()));
        }
    }

    @Override
    public void outputTo(OutputStream outputStream) throws IOException {
        super.outputTo(outputStream);

        var writer = new PrintWriter(outputStream);

        if (body != null) {
            writer.write(body);
            writer.write("\r\n");
        }

        writer.flush();
    }

    @Override
    public StatusCode getStatusCode() {
        return this.statusCode;
    }

    @Override
    public Map<String, String> getHeaders() {
        return this.headers;
    }
}
