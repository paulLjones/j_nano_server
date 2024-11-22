package paul_jones.j_nano_server;

import paul_jones.j_nano_server.requests.RequestParser;
import paul_jones.j_nano_server.router.Router;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.concurrent.Executors;

public class Server {
    private final Router router;

    public Server(Router router) {
        this.router = router;
    }

    public void serve(String hostname, int port) throws IOException {
        final var serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress(hostname, port));

        try (serverSocket) {
            try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {

                System.out.printf("Now serving on %s:%d\n", hostname, port);

                while (true) {
                    final var socket = serverSocket.accept();

                    executor.submit(() -> {
                        try (socket) {
                            var request = RequestParser.parse(socket.getInputStream());

                            var response = router.handle(request);

                            response.outputTo(socket.getOutputStream());
                            socket.getOutputStream().flush();
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        } finally {
                            try {
                                socket.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        }
    }
}