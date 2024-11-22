package paul_jones.j_nano_server.responses;

import java.util.Map;
import java.util.TreeMap;

public class EmptyResponse extends Response {
    public StatusCode statusCode;
    public TreeMap<String, String> headers;

    public EmptyResponse(StatusCode statusCode, TreeMap<String, String> headers) {
        this.statusCode = statusCode;
        this.headers = headers;
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
