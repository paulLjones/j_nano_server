package paul_jones.j_nano_server.responses;

import java.io.IOException;
import java.io.OutputStream;
import java.util.TreeMap;
import java.util.Map;

public class ByteResponse extends Response {
    public StatusCode statusCode;
    public TreeMap<String, String> headers;
    public byte[] body;

    public ByteResponse(StatusCode statusCode, TreeMap<String, String> headers, byte[] body) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.body = body;
    }

    @Override
    public void outputTo(OutputStream outputStream) throws IOException {
        super.outputTo(outputStream);

        if (body != null) {
            outputStream.write(body);
            outputStream.write("\r\n".getBytes());
        }

        outputStream.flush();
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
