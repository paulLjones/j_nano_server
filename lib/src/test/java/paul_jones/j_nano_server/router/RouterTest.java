package paul_jones.j_nano_server.router;

import org.junit.jupiter.api.Test;

import paul_jones.j_nano_server.requests.Request;
import paul_jones.j_nano_server.requests.Verb;
import paul_jones.j_nano_server.requests.VerbURIProtocol;
import paul_jones.j_nano_server.requests.body.RawBody;
import paul_jones.j_nano_server.responses.ByteResponse;
import paul_jones.j_nano_server.responses.EmptyResponse;
import paul_jones.j_nano_server.responses.StatusCode;
import paul_jones.j_nano_server.responses.StringResponse;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.util.TreeMap;

public class RouterTest {
        @Test
        public void returns_NotFoundResponse_when_noRoutesDefined() {
                try {
                        var router = new Router();
                        var request = new Request(
                                        new VerbURIProtocol(Verb.GET, new URI("/"), "HTTP/1.1"),
                                        new TreeMap<>(),
                                        new RawBody(null));

                        var response = router.handle(request);

                        assertEquals(StatusCode.NotFound, response.getStatusCode());
                        assertEquals(404, StatusCode.NotFound.getCode());
                } catch (Exception e) {
                        throw new RuntimeException(e);
                }
        }

        @Test
        public void runs_matchingRouteAndMethod() {
                try {
                        var router = new Router();
                        var headers = new TreeMap<String, String>();
                        var body = new byte[0];

                        router.mapGet("/", (request) -> new ByteResponse(StatusCode.Ok, headers, body));

                        var request = new Request(
                                        new VerbURIProtocol(Verb.GET, new URI("/"), "HTTP/1.1"),
                                        new TreeMap<>(),
                                        new RawBody(null));

                        var response = router.handle(request);

                        assertEquals(StatusCode.Ok, response.getStatusCode());
                        assertSame(headers, response.getHeaders());
                } catch (Exception e) {
                        throw new RuntimeException(e);
                }
        }

        @Test
        public void doesntRun_matchingRoute_but_differentMethod() {
                try {
                        var router = new Router();
                        var headers = new TreeMap<String, String>();
                        var body = new byte[0];

                        router.mapGet("/", (request) -> new ByteResponse(StatusCode.Ok, headers, body));

                        var request = new Request(
                                        new VerbURIProtocol(Verb.POST, new URI("/"), "HTTP/1.1"),
                                        new TreeMap<>(),
                                        new RawBody(null));

                        var response = router.handle(request);

                        assertEquals(StatusCode.NotFound, response.getStatusCode());
                } catch (Exception e) {
                        throw new RuntimeException(e);
                }
        }

        @Test
        public void matchesCorrectRoute_when_multipleRoutesBound() {
                try {
                        var router = new Router();
                        var headers = new TreeMap<String, String>();

                        router.mapGet("/", (request) -> new StringResponse(StatusCode.Ok, headers, ""));

                        router.mapGet("/other", (request) -> new StringResponse(StatusCode.Ok, headers, "other"));

                        var request = new Request(
                                        new VerbURIProtocol(Verb.GET, new URI("/other"), "HTTP/1.1"),
                                        new TreeMap<>(),
                                        new RawBody(null));

                        var response = (StringResponse) router.handle(request);

                        assertEquals(StatusCode.Ok, response.getStatusCode());
                        assertEquals("other", response.body);
                } catch (Exception e) {
                        throw new RuntimeException(e);
                }
        }

        @Test
        public void matchesAndExtracts_RouteParams() {
                try {
                        var router = new Router();
                        var headers = new TreeMap<String, String>();

                        final String NAME = "Dave";
                        final String BODY_TEMPLATE = "Hi: %s";

                        var expectedResponse = new StringResponse(StatusCode.Ok, new TreeMap<>(),
                                        String.format(BODY_TEMPLATE, NAME));

                        router.mapGet("/say-hi/{name}", (request) -> new StringResponse(
                                        StatusCode.Ok,
                                        headers,
                                        String.format(BODY_TEMPLATE, request.getRouteParam("name"))));

                        var request = new Request(
                                        new VerbURIProtocol(Verb.GET, new URI(String.format("/say-hi/%s", NAME)),
                                                        "HTTP/1.1"),
                                        new TreeMap<>(),
                                        new RawBody(null));

                        var response = (StringResponse) router.handle(request);

                        assertEquals(StatusCode.Ok, response.getStatusCode());
                        assertEquals(expectedResponse.getHeaders(), response.getHeaders());

                        assertEquals(expectedResponse.body, response.body);
                } catch (Exception e) {
                        throw new RuntimeException(e);
                }
        }

        @Test
        public void doesntMatch_missingAndRequired_RouteParams() {
                try {
                        var router = new Router();
                        var headers = new TreeMap<String, String>();

                        router.mapGet("/say-hi/{name}", (request) -> new EmptyResponse(StatusCode.Ok, headers));

                        var request = new Request(
                                        new VerbURIProtocol(Verb.GET, new URI(String.format("/say-hi/")),
                                                        "HTTP/1.1"),
                                        new TreeMap<>(),
                                        new RawBody(null));

                        var response = (EmptyResponse) router.handle(request);

                        assertEquals(StatusCode.NotFound, response.getStatusCode());
                } catch (Exception e) {
                        throw new RuntimeException(e);
                }
        }

        @Test
        public void returnsInternalServerError_whenHandlerThrows() {
                assertDoesNotThrow(() -> {
                        var router = new Router();
                        router.mapGet("/error", (request) -> {
                                throw new RuntimeException("Error");
                        });

                        var request = new Request(
                                        new VerbURIProtocol(Verb.GET, new URI("/error"),
                                                        "HTTP/1.1"),
                                        new TreeMap<>(),
                                        new RawBody(null));

                        var result = router.handle(request);

                        assertEquals(StatusCode.InternalServerError, result.getStatusCode());
                });
        }

        @Test
        public void matchesCorrectRoute_whenMultipleMethodsGiven() {
                try {
                        var headers = new TreeMap<String, String>();

                        var router = new Router();
                        router.mapGet("/", (request) -> new EmptyResponse(StatusCode.Ok, headers));
                        router.mapPost("/", (request) -> new EmptyResponse(StatusCode.Created, headers));
                        router.mapPatch("/", (request) -> new EmptyResponse(StatusCode.Accepted, headers));

                        {
                                var getRequest = new Request(
                                                new VerbURIProtocol(Verb.GET, new URI("/"),
                                                                "HTTP/1.1"),
                                                new TreeMap<>(),
                                                new RawBody(null));

                                var result = router.handle(getRequest);

                                assertEquals(StatusCode.Ok, result.getStatusCode());
                        }

                        {
                                var postRequest = new Request(
                                                new VerbURIProtocol(Verb.POST, new URI("/"),
                                                                "HTTP/1.1"),
                                                new TreeMap<>(),
                                                new RawBody(null));

                                var result = router.handle(postRequest);

                                assertEquals(StatusCode.Created, result.getStatusCode());
                        }

                        {
                                var patchRequest = new Request(
                                                new VerbURIProtocol(Verb.PATCH, new URI("/"),
                                                                "HTTP/1.1"),
                                                new TreeMap<>(),
                                                new RawBody(null));

                                var result = router.handle(patchRequest);

                                assertEquals(StatusCode.Accepted, result.getStatusCode());
                        }
                } catch (Exception e) {
                        throw new RuntimeException(e);
                }
        }

        @Test
        public void matchesNested_RouteParams() {
                try {
                        var headers = new TreeMap<String, String>();

                        var router = new Router();
                        router.mapGet("/posts/{postId}/comments/{commentId}",
                                        (request) -> new StringResponse(StatusCode.Ok, headers,
                                                        String.format("Comment %s of Post %s",
                                                                        request.getRouteParam("commentId"),
                                                                        request.getRouteParam("postId"))));

                        router.mapGet("/posts/{postId}/{commentId}",
                                        (request) -> new StringResponse(StatusCode.Ok, headers,
                                                        String.format("Alt Comment %s of Post %s",
                                                                        request.getRouteParam("commentId"),
                                                                        request.getRouteParam("postId"))));

                        {
                                var validRequest = new Request(
                                                new VerbURIProtocol(Verb.GET, new URI("/posts/42/comments/23"),
                                                                "HTTP/1.1"),
                                                new TreeMap<>(),
                                                new RawBody(null));

                                var result = (StringResponse) router.handle(validRequest);

                                assertEquals(StatusCode.Ok, result.getStatusCode());
                                assertEquals("Comment 23 of Post 42", result.body);
                        }

                        {
                                var altValidRequest = new Request(
                                                new VerbURIProtocol(Verb.GET, new URI("/posts/42/23"),
                                                                "HTTP/1.1"),
                                                new TreeMap<>(),
                                                new RawBody(null));

                                var result = (StringResponse) router.handle(altValidRequest);

                                assertEquals(StatusCode.Ok, result.getStatusCode());
                                assertEquals("Alt Comment 23 of Post 42", result.body);
                        }

                        {
                                var invalidRequest = new Request(
                                                new VerbURIProtocol(Verb.POST, new URI("/posts/42"),
                                                                "HTTP/1.1"),
                                                new TreeMap<>(),
                                                new RawBody(null));

                                var result = router.handle(invalidRequest);

                                assertEquals(StatusCode.NotFound, result.getStatusCode());
                        }
                } catch (Exception e) {
                        throw new RuntimeException(e);
                }
        }
}
