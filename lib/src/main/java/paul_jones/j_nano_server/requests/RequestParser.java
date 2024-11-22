package paul_jones.j_nano_server.requests;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.regex.Pattern;

import paul_jones.j_nano_server.requests.body.Body;
import paul_jones.j_nano_server.requests.body.FormUrlEncodedBody;
import paul_jones.j_nano_server.requests.body.MultipartFormDataBody;
import paul_jones.j_nano_server.requests.body.RawBody;

public final class RequestParser {
    static final Pattern multipartPattern = Pattern.compile("multipart/form\\-data; boundary=(.+)");

    private RequestParser() {
    }

    public static Request parse(InputStream inputStream) throws IOException {
        var bytesRead = new ArrayList<Byte>();

        {
            byte[] lastRead = inputStream.readNBytes(1);
            var toSearch = (byte) '\r';

            while (lastRead.length > 0 && lastRead[0] != toSearch) {
                bytesRead.add(lastRead[0]);
                lastRead = inputStream.readNBytes(1);
            }
        }

        // Consume \n
        inputStream.read();

        final var buffer = ByteBuffer.allocate(bytesRead.size());
        bytesRead.forEach((b) -> buffer.put(b));

        bytesRead.clear();

        final var verbPathProtocol = VerbURIProtocol
                .parseFromLine(new String(buffer.array()));

        {
            byte[] lastRead = inputStream.readNBytes(1);
            while (lastRead.length > 0) {
                if (lastRead[0] == (byte) '\n' && bytesRead.size() >= 4) {
                    var view = bytesRead.reversed();

                    if (view.get(0) == '\r' && view.get(1) == '\n' && view.get(2) == '\r') {
                        for (byte b : lastRead) {
                            bytesRead.add(b);
                        }
                        break;
                    }
                }

                for (byte b : lastRead) {
                    bytesRead.add(b);
                }

                lastRead = inputStream.readNBytes(1);
            }
        }

        final var buffer2 = ByteBuffer.allocate(bytesRead.size());
        bytesRead.forEach((b) -> buffer2.put(b));

        bytesRead.clear();

        final TreeMap<String, String> headers = parseHeaders(
                new BufferedReader(new InputStreamReader(new ByteArrayInputStream(buffer2.array()))));
        Body body = null;
        String contentType = headers.get("Content-Type");
        String contentLength = headers.get("Content-Length");

        if (verbPathProtocol.verb().hasBody() && contentType != null && contentLength != null) {
            int expectedLength = Integer.parseInt(contentLength);

            var rawData = new byte[expectedLength];
            int read = 0;
            do {
                read += inputStream.read(rawData, read, expectedLength - read);
            } while (read < expectedLength);

            if (contentType.equals("application/x-www-form-urlencoded")) {
                body = new FormUrlEncodedBody(rawData);
            } else if (contentType.startsWith("multipart/form-data;")) {
                var result = multipartPattern.matcher(contentType);

                result.find();

                var boundary = result.group(1);
                body = new MultipartFormDataBody(rawData, boundary);

            } else {
                body = new RawBody(rawData);
            }
        }

        return new Request(verbPathProtocol, headers, body);
    }

    private static TreeMap<String, String> parseHeaders(BufferedReader reader) throws IOException {
        var headers = new TreeMap<String, String>();

        String line = reader.readLine();

        while (line != null && line.length() != 0) {
            var parts = line.strip().split(":", 2);

            if (parts.length != 2) {
                throw new RuntimeException(String.format("Expected '<header>: value' found: '%s'", line));
            }

            headers.put(parts[0].strip(), parts[1].strip());

            line = reader.readLine();
        }

        return headers;
    }
}
