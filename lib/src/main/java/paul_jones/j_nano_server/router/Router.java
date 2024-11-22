package paul_jones.j_nano_server.router;

import paul_jones.j_nano_server.requests.Request;
import paul_jones.j_nano_server.requests.Verb;
import paul_jones.j_nano_server.responses.EmptyResponse;
import paul_jones.j_nano_server.responses.Response;
import paul_jones.j_nano_server.responses.StatusCode;
import paul_jones.j_nano_server.responses.StringResponse;

import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Routes request to their handlers
 */
public class Router {
    private final TreeMap<Verb, ArrayList<RouteBinding>> routesForVerb = new TreeMap<>();

    public void mapGet(String path, Handler handler) {
        var routes = routesForVerb.get(Verb.GET);

        if (routes == null) {
            routes = new ArrayList<>();
        }

        routes.add(new RouteBinding(path, handler));

        routesForVerb.putIfAbsent(Verb.GET, routes);
    }

    public void mapPost(String path, Handler handler) {
        var routes = routesForVerb.get(Verb.POST);

        if (routes == null) {
            routes = new ArrayList<>();
        }

        routes.add(new RouteBinding(path, handler));

        routesForVerb.putIfAbsent(Verb.POST, routes);
    }

    public void mapPut(String path, Handler handler) {
        var routes = routesForVerb.get(Verb.PUT);

        if (routes == null) {
            routes = new ArrayList<>();
        }

        routes.add(new RouteBinding(path, handler));

        routesForVerb.putIfAbsent(Verb.PUT, routes);
    }

    public void mapPatch(String path, Handler handler) {
        var routes = routesForVerb.get(Verb.PATCH);

        if (routes == null) {
            routes = new ArrayList<>();
        }

        routes.add(new RouteBinding(path, handler));

        routesForVerb.putIfAbsent(Verb.PATCH, routes);
    }

    public void mapDelete(String path, Handler handler) {
        var routes = routesForVerb.get(Verb.DELETE);

        if (routes == null) {
            routes = new ArrayList<>();
        }

        routes.add(new RouteBinding(path, handler));

        routesForVerb.putIfAbsent(Verb.DELETE, routes);
    }

    public Response handle(Request request) throws Exception {
        var routes = routesForVerb.get(request.verb);

        if (routes == null) {
            return new EmptyResponse(StatusCode.NotFound, new TreeMap<>());
        }

        try {
            for (RouteBinding route : routes) {
                var matched = route.parse(request);

                if (matched != null) {
                    return route.handler.apply(matched);
                }
            }

            return new EmptyResponse(StatusCode.NotFound, new TreeMap<>());
        } catch (InvalidRouteFormatException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();

            final String errorMessage = "Error 500: Internal Server Error";

            var headers = new TreeMap<String, String>();

            return new StringResponse(
                    StatusCode.InternalServerError,
                    headers,
                    errorMessage);
        }
    }
}