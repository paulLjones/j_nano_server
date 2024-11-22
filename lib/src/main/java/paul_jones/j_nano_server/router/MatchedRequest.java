package paul_jones.j_nano_server.router;

import java.util.Map;
import java.util.TreeMap;
import java.net.URI;

import paul_jones.j_nano_server.requests.Request;
import paul_jones.j_nano_server.requests.body.Body;

public class MatchedRequest {
    Request sourceRequest;
    Map<String, String> routeParams;

    MatchedRequest(Request sourceRequest, Map<String, String> routeParams) {
        this.sourceRequest = sourceRequest;
        this.routeParams = routeParams;
    }

    public URI uri() {
        return sourceRequest.uri;
    }

    public String getRouteParam(String param) {
        return routeParams.get(param);
    }

    public Map<String, String> headers() {
        return sourceRequest.headers;
    }

    public Body body() {
        return sourceRequest.body;
    }

    public Map<String, String> queryParams() {
        var map = new TreeMap<String, String>();

        var query = sourceRequest.uri.getQuery();

        if (query != null) {
            var pairs = query.split("&");

            for (String pair : pairs) {
                var parsed = pair.split("=");

                if (parsed.length != 2) {
                    continue;
                }

                var key = parsed[0];
                var value = parsed[1];

                map.put(key, value);
            }
        }

        return map;
    }
}
