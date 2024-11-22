package paul_jones.j_nano_server.router;

import paul_jones.j_nano_server.requests.Request;

import java.util.ArrayList;
import java.util.TreeMap;

class RouteBinding {
    public String path;
    public Handler handler;

    // Every other part (odd index) is a paramter
    private final ArrayList<String> parts;

    RouteBinding(String path, Handler handler) {
        validatePath(path);

        this.path = path;
        this.handler = handler;

        this.parts = parsePath(path);
    }

    public MatchedRequest parse(Request request) {
        var requestPath = request.uri.getPath();
        int startIndex = 0;
        var routeParams = new TreeMap<String, String>();

        try {
            for (int i = 0; i < parts.size(); i++) {
                boolean isParam = i % 2 == 1;
                var part = parts.get(i);

                if (isParam) {
                    int endIndex = requestPath.indexOf('/', startIndex);

                    if (endIndex == -1) {
                        endIndex = requestPath.length();
                    }

                    if (startIndex == endIndex) {
                        return null;
                    }

                    routeParams.put(part, requestPath.substring(startIndex, endIndex));

                    startIndex = endIndex;
                    continue;
                }

                if (requestPath.length() >= startIndex + part.length()
                        && part.equals(requestPath.substring(startIndex, startIndex + part.length()))) {
                    startIndex += part.length();
                    continue;
                } else {
                    return null;
                }
            }
        } catch (Exception e) {
            throw new InvalidRouteFormatException(path, e);
        }

        if (startIndex != requestPath.length()) {
            return null;
        }

        return new MatchedRequest(request, routeParams);
    }

    private static ArrayList<String> parsePath(String path) {
        var parts = new ArrayList<String>();
        int index = 0;
        boolean inParam = false;

        while (index < path.length()) {
            int paramIndex = path.indexOf(inParam ? '}' : '{', index);

            if (paramIndex == -1) {
                if (inParam) {
                    throw new InvalidRouteFormatException(path);
                }

                parts.add(path.substring(index));

                break;
            }

            parts.add(path.substring(index, paramIndex));

            if (inParam && parts.getLast().contains("/")) {
                throw new InvalidRouteFormatException(path);
            }

            inParam = !inParam;
            index = paramIndex + 1;
        }

        if (inParam) {
            throw new InvalidRouteFormatException(path);
        }

        return parts;
    }

    private void validatePath(String path) {
        record ParamDef(int index, char c) {
        }

        final var paramStarts = new ArrayList<ParamDef>();
        final var paramEnds = new ArrayList<ParamDef>();

        for (int i = 0; i < path.length(); i++) {
            char c = path.charAt(i);

            if (c == '{') {
                var def = new ParamDef(i, c);
                paramStarts.add(def);
            } else if (c == '}') {
                var def = new ParamDef(i, c);
                paramEnds.add(def);
            }
        }

        var paramStartCount = paramStarts.size();
        var paramEndCount = paramEnds.size();

        if (paramStartCount != paramEndCount) {
            throw new InvalidRouteFormatException(path);
        }

        for (int i = 0; i < paramStarts.size(); i++) {
            var start = paramStarts.get(i);
            var end = paramEnds.get(i);

            if (end.index <= start.index) {
                throw new InvalidRouteFormatException(path);
            }

            // If not last
            if (i < paramStarts.size() - 1) {
                if (paramStarts.get(i + 1).index - end.index <= 1) {
                    throw new InvalidRouteFormatException(path);
                }
            }

            var paramName = path.substring(start.index + 1, end.index);

            if (!paramName.matches("\\w+")) {
                throw new InvalidRouteFormatException(path);
            }
        }
    }
}