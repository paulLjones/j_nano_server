package paul_jones.j_nano_example;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Random;
import java.util.TreeMap;
import java.util.function.Supplier;

import paul_jones.j_nano_server.Server;
import paul_jones.j_nano_server.requests.body.FormUrlEncodedBody;
import paul_jones.j_nano_server.requests.body.MultipartFormDataBody;
import paul_jones.j_nano_server.requests.body.RawBody;
import paul_jones.j_nano_server.requests.body.form_data.FileFormData;
import paul_jones.j_nano_server.requests.body.form_data.TextFormData;
import paul_jones.j_nano_server.responses.EmptyResponse;
import paul_jones.j_nano_server.responses.StringResponse;
import paul_jones.j_nano_server.responses.StatusCode;
import paul_jones.j_nano_server.router.MatchedRequest;
import paul_jones.j_nano_server.router.Router;

public class Main {
    private static Path storageDir = Path.of(System.getProperty("user.dir"), "storage");

    public static void main(String[] args) {
        File dir = storageDir.toFile();

        if (dir.exists() && dir.isFile()) {
            throw new Error("Expected empty or missing folder named `storage` in current working directory");
        } else if (!dir.exists()) {
            dir.mkdirs();
        }

        var router = new Router();

        mapBasicRoutes(router);

        mapTodoRoutes(router);

        var server = new Server(router);

        try {
            server.serve("localhost", 8002);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void mapBasicRoutes(Router router) {
        router.mapGet("/", Main::indexHandler);

        router.mapGet("/json", (request) -> {
            return responseFromString("application/json", "{ \"this_is\": \"json\" }");
        });

        router.mapGet("/say-hi/{name}", (request) -> responseFromString("text", request.getRouteParam("name")));

        router.mapGet("/paginated", (request) -> {
            var queryParams = request.queryParams();

            int perPage = 25;

            {
                String perPageQuery = queryParams.get("perPage");
                if (perPageQuery != null) {
                    perPage = Integer.parseInt(perPageQuery);
                }
            }

            int page = 1;

            {
                String pageQuery = queryParams.get("page");
                if (pageQuery != null) {
                    page = Integer.parseInt(pageQuery);
                }
            }

            int first = ((page - 1) * perPage);
            int last = first + perPage;

            return responseFromString("application/json",
                    String.format("""
                            {
                                "page": %d,
                                "from": %d,
                                "to": %d,
                                "per_page": %d
                            }
                            """,
                            page,
                            first,
                            last,
                            perPage));
        });

        router.mapGet("/form-data", (request) -> {
            return responseFromString("text/html", """
                        <!DOCTYPE HTML>
                        <html>
                        <head>
                        </head>
                        <body>
                        <h1> Form </h1>
                        <form method="POST" enctype="multipart/form-data">
                            <input name="name">
                            <input name="age" type="number">
                            <input name="file" type="file">

                            <button>Submit</button>
                        </form>
                        </body>
                        </html>
                    """);
        });

        router.mapPost("/form-data", (MatchedRequest request) -> {
            var body = (MultipartFormDataBody) request.body();

            var data = body.getData();

            var inputValues = new StringBuilder();

            for (var entry : data.entrySet()) {
                var value = entry.getValue();

                if (value instanceof TextFormData v) {
                    inputValues.append(
                            String.format("<li>%s: %s</li>\n", entry.getKey(), v.value().replace("\n", "<br>")));
                } else if (value instanceof FileFormData v) {
                    var path = storageDir.resolve(new Random().nextInt() + v.filename());

                    File f = path.toFile();

                    try (var outputStream = new FileOutputStream(f)) {
                        outputStream.write(v.bytes());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    inputValues.append(
                            String.format("<li>%s: %s</li>\n", entry.getKey(),
                                    String.format(
                                            "File named: \"%s\"<br>",
                                            path,
                                            v.filename())));
                }
            }

            return responseFromString("text/html", """
                    <!DOCTYPE HTML>
                        <html>
                        <head>
                        </head>
                        <body>
                        <h1> Results </h1>
                        <ul>"""
                    +
                    inputValues.toString() +
                    """
                                </ul>

                                <a href="/form-data"> Back 2 Form </a>
                                </body>
                                </html>
                            """);
        });

    }

    private static void mapTodoRoutes(Router router) {
        var todos = new ArrayList<String>();

        Supplier<String> renderTodos = () -> {
            var stringBuilder = new StringBuilder();

            stringBuilder.append("<ul>");

            for (int i = 0; i < todos.size(); i++) {
                var todo = todos.get(i);

                stringBuilder.append("<li>");

                stringBuilder.append("<p>").append(todo).append("</p>");

                stringBuilder
                        .append("<form onsubmit=\"return confirm('Are you sure?')\" method=\"POST\" action=\"/todos/")
                        .append(i).append("/delete\">")
                        .append("<button type=\"submit\"> Delete </button>")
                        .append("</form>");

                stringBuilder.append("</li>");
            }

            stringBuilder.append("</ul>");

            return stringBuilder.toString();
        };

        router.mapGet("/todos", (request) -> {
            return responseFromString("text/html", """
                                        <!DOCTYPE html>
                                        <html>
                                            <head>
                                                <meta name="charset" content="utf-8" />
                                                <meta name="viewport" content="width=device-width, initial-scale=1" />
                                                <title> Todos </title>
                                            </head>
                                            <body>
                                                <h1> Todos </h1>

                                                <form action="todos" method="POST">
                                                    <label for="todo">
                                                    <input name="todo" required minlength="1">

                                                    <button type="submit">
                                                        Create
                                                    </button>
                                                </form>
                    """
                    +
                    (todos.size() <= 0
                            ? "<p> No todos set </p>"
                            : renderTodos.get())
                    +
                    """
                                </body>
                            </html>
                            """);
        });

        router.mapPost("/todos", (request) -> {
            var body = request.body();

            if (body instanceof FormUrlEncodedBody b) {
                String todo = b.getData().get("todo");

                if (todo != null) {
                    todos.add(todo);
                }
            }

            var headers = new TreeMap<String, String>();
            headers.put("Location", "/todos");

            return new EmptyResponse(StatusCode.Found, headers);
        });

        router.mapPost("/todos/{id}/delete", (request) -> {
            int index = Integer.parseInt(request.getRouteParam("id"));

            if (index >= 0 && index < todos.size()) {
                todos.remove(index);
            }

            var headers = new TreeMap<String, String>();
            headers.put("Location", "/todos");

            return new EmptyResponse(StatusCode.Found, headers);
        });
    }

    private static StringResponse indexHandler(MatchedRequest request) {
        var builder = new StringBuilder();

        builder.append("<p>The headers you sent are:</p>\n");

        builder.append("<ul>\n");

        for (var entry : request.headers().entrySet()) {
            builder.append(String.format("<li>%s: %s</li>\n", entry.getKey(), entry.getValue()));
        }

        builder.append("</ul>\n");

        var body = request.body();

        if (body != null) {
            builder.append("<p>The request body is:</p>\n");

            switch (body) {
                case FormUrlEncodedBody b:
                    builder.append("<ul>\n");
                    for (var entry : b.getData().entrySet()) {
                        builder.append(String.format("<li>%s: %s</li>\n", entry.getKey(), entry.getValue()));
                    }
                    builder.append("</ul>\n");
                    break;

                case RawBody b:
                    builder.append(String.format("Raw data of length: %d", b.getData().length));
                    break;

                default:
                    builder.append("Unrecognised format: " + body.getClass().getSimpleName());
                    break;
            }
        }

        return responseFromString("text/html", builder.toString());
    }

    private static StringResponse responseFromString(String contentType, String string) {
        var headers = new TreeMap<String, String>();

        headers.put("Content-Type", contentType);

        return new StringResponse(StatusCode.Ok, headers, string);
    }
}
