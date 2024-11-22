package paul_jones.j_nano_server.router;

import org.junit.jupiter.api.Test;

import paul_jones.j_nano_server.requests.Request;
import paul_jones.j_nano_server.requests.Verb;
import paul_jones.j_nano_server.requests.VerbURIProtocol;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.util.TreeMap;

public class MatchedRequestTest {
    @Test
    public void requestExtracts_queryParams() {
        var request = new MatchedRequest(
                new Request(new VerbURIProtocol(Verb.GET, URI.create("/?str=abc&num=42"), "HTTP/1.1"), new TreeMap<>(),
                        null),
                new TreeMap<>());

        var expected = new TreeMap<String, String>();
        expected.put("str", "abc");
        expected.put("num", "42");

        assertEquals(expected, request.queryParams());
    }
}
