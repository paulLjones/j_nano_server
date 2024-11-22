package paul_jones.j_nano_server.router;

import paul_jones.j_nano_server.responses.Response;

public interface Handler {
    Response apply(MatchedRequest request);
}