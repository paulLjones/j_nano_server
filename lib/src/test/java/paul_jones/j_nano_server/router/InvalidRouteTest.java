package paul_jones.j_nano_server.router;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.URI;
import java.util.TreeMap;

import org.junit.jupiter.api.Test;

import paul_jones.j_nano_server.requests.Request;
import paul_jones.j_nano_server.requests.Verb;
import paul_jones.j_nano_server.requests.VerbURIProtocol;
import paul_jones.j_nano_server.requests.body.RawBody;
import paul_jones.j_nano_server.responses.ByteResponse;
import paul_jones.j_nano_server.responses.StatusCode;

public class InvalidRouteTest {

    @Test
    public void throwsWhen_paramNotClosed() {
        assertThrows(InvalidRouteFormatException.class, () -> {
            var router = new Router();
            router.mapGet("/bad-route/{", (request) -> new ByteResponse(
                    StatusCode.Ok,
                    new TreeMap<>(),
                    new byte[0]));

            var request = new Request(
                    new VerbURIProtocol(Verb.GET, new URI("/bad-route"),
                            "HTTP/1.1"),
                    new TreeMap<>(),
                    new RawBody(null));

            router.handle(request);
        });
    }

    @Test
    public void throwsWhen_paramNotOpened() {
        assertThrows(InvalidRouteFormatException.class, () -> {
            var router = new Router();
            router.mapGet("/bad-route/}", (request) -> new ByteResponse(
                    StatusCode.Ok,
                    new TreeMap<>(),
                    new byte[0]));

            var request = new Request(
                    new VerbURIProtocol(Verb.GET, new URI("/bad-route"),
                            "HTTP/1.1"),
                    new TreeMap<>(),
                    new RawBody(null));

            router.handle(request);
        });
    }

    @Test
    public void throwsWhen_pramIsEmpty() {
        assertThrows(InvalidRouteFormatException.class, () -> {
            var router = new Router();
            router.mapGet("/bad-route/{}", (request) -> new ByteResponse(
                    StatusCode.Ok,
                    new TreeMap<>(),
                    new byte[0]));

            var request = new Request(
                    new VerbURIProtocol(Verb.GET, new URI("/bad-route"),
                            "HTTP/1.1"),
                    new TreeMap<>(),
                    new RawBody(null));

            router.handle(request);
        });
    }

    @Test
    public void throwsWhen_paramNameInvalid() {
        assertThrows(InvalidRouteFormatException.class, () -> {
            var router = new Router();
            router.mapGet("/bad{/}", (request) -> new ByteResponse(
                    StatusCode.Ok,
                    new TreeMap<>(),
                    new byte[0]));

            var request = new Request(
                    new VerbURIProtocol(Verb.GET, new URI("/bad-route"),
                            "HTTP/1.1"),
                    new TreeMap<>(),
                    new RawBody(null));

            router.handle(request);
        });
    }

    @Test
    public void throwsWhen_paramsAdjacent() {
        assertThrows(InvalidRouteFormatException.class, () -> {
            var router = new Router();
            router.mapGet("/bad/{a}{b}", (request) -> new ByteResponse(
                    StatusCode.Ok,
                    new TreeMap<>(),
                    new byte[0]));

            var request = new Request(
                    new VerbURIProtocol(Verb.GET, new URI("/bad/route"),
                            "HTTP/1.1"),
                    new TreeMap<>(),
                    new RawBody(null));

            router.handle(request);
        });
    }

    @Test
    public void throwsWhen_bracketsInWrongOrder() {
        assertThrows(InvalidRouteFormatException.class, () -> {
            var router = new Router();
            router.mapGet("/bad/}a{", (request) -> new ByteResponse(
                    StatusCode.Ok,
                    new TreeMap<>(),
                    new byte[0]));

            var request = new Request(
                    new VerbURIProtocol(Verb.GET, new URI("/bad/route"),
                            "HTTP/1.1"),
                    new TreeMap<>(),
                    new RawBody(null));

            router.handle(request);
        });
    }

}
