package paul_jones.j_nano_server.requests;

import org.junit.jupiter.api.Test;

import paul_jones.j_nano_server.requests.body.FormUrlEncodedBody;
import paul_jones.j_nano_server.requests.body.MultipartFormDataBody;
import paul_jones.j_nano_server.requests.body.form_data.TextFormData;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.TreeMap;

public class RequestParserTest {
    @Test
    void correctlyParses_basicRequest() {
        assertDoesNotThrow(() -> {
            var requestRaw = "GET / HTTP/1.1\r\n";
            var requestAsBytes = requestRaw.getBytes();

            var inputStream = new ByteArrayInputStream(requestAsBytes);

            var parsedRequest = RequestParser.parse(inputStream);

            assertEquals(Verb.GET, parsedRequest.verb);
            assertEquals(new URI("/"), parsedRequest.uri);
            assertEquals(new TreeMap<String, String>(), parsedRequest.headers);
            assertNull(parsedRequest.body);
        });
    }

    @Test
    void correctlyParses_requestWithHeaders() {
        assertDoesNotThrow(() -> {
            var headers = new TreeMap<String, String>();
            headers.put("Referrer", "localhost");
            headers.put("Accepts", "application/json");

            var requestRaw = "GET / HTTP/1.1\r\n";

            for (var entry : headers.entrySet()) {
                requestRaw += String.format("%s: %s\r\n", entry.getKey(), entry.getValue());
            }

            requestRaw += "\r\n";

            var requestAsBytes = requestRaw.getBytes();

            var inputStream = new ByteArrayInputStream(requestAsBytes);

            var parsedRequest = RequestParser.parse(inputStream);

            assertEquals(Verb.GET, parsedRequest.verb);
            assertEquals(new URI("/"), parsedRequest.uri);
            assertEquals(headers, parsedRequest.headers);
            assertNull(parsedRequest.body);
        });
    }

    @Test
    void correctlyParses_requestWithUrlEncodedBody() {
        assertDoesNotThrow(() -> {
            var bodyArgs = new TreeMap<String, String>();
            bodyArgs.put("myName", "John Cena");
            bodyArgs.put("the_answer", "42");

            var bodyEncoded = "";

            {
                int i = 1;

                for (var entry : bodyArgs.entrySet()) {
                    bodyEncoded += String.format("%s=%s", entry.getKey(), entry.getValue());

                    if (i != bodyArgs.size()) {
                        bodyEncoded += "&";
                    }

                    i++;
                }
            }

            var headers = new TreeMap<String, String>();
            headers.put("Content-Type", "application/x-www-form-urlencoded");
            headers.put("Content-Length", String.valueOf(bodyEncoded.length()));

            var requestRaw = "POST / HTTP/1.1\r\n";

            for (var entry : headers.entrySet()) {
                requestRaw += String.format("%s: %s\r\n", entry.getKey(), entry.getValue());
            }

            requestRaw += "\r\n";

            requestRaw += bodyEncoded;

            requestRaw += "\r\n";

            var requestAsBytes = requestRaw.getBytes();

            var inputStream = new ByteArrayInputStream(requestAsBytes);

            var parsedRequest = RequestParser.parse(inputStream);

            assertEquals(Verb.POST, parsedRequest.verb);
            assertEquals(new URI("/"), parsedRequest.uri);
            assertEquals(headers, parsedRequest.headers);
            assertEquals(bodyArgs, ((FormUrlEncodedBody) parsedRequest.body).getData());
        });
    }

    @Test
    void correctlyParses_requestWithMultipartEncodedBody() {
        assertDoesNotThrow(() -> {
            var bodyArgs = new TreeMap<String, TextFormData>();
            bodyArgs.put("myName", new TextFormData("John Cena"));
            bodyArgs.put("the_answer", new TextFormData("42"));

            var bodyEncoded = RequestParserTestHelpers.convertDataToMultipartFormData(bodyArgs, "--1234");

            var headers = new TreeMap<String, String>();
            headers.put("Content-Type", "multipart/form-data; boundary=--1234");
            headers.put("Content-Length", String.valueOf(bodyEncoded.length()));

            var requestRaw = "POST / HTTP/1.1\r\n";

            for (var entry : headers.entrySet()) {
                requestRaw += String.format("%s: %s\r\n", entry.getKey(), entry.getValue());
            }

            requestRaw += "\r\n";

            requestRaw += bodyEncoded;

            requestRaw += "\r\n";

            var requestAsBytes = requestRaw.getBytes();

            var inputStream = new ByteArrayInputStream(requestAsBytes);

            var parsedRequest = RequestParser.parse(inputStream);

            assertEquals(Verb.POST, parsedRequest.verb);
            assertEquals(new URI("/"), parsedRequest.uri);
            assertEquals(headers, parsedRequest.headers);
            assertEquals(bodyArgs, ((MultipartFormDataBody) parsedRequest.body).getData());
        });
    }
}

class RequestParserTestHelpers {
    static String convertDataToMultipartFormData(TreeMap<String, TextFormData> data, String boundary) {
        StringBuilder builder = new StringBuilder();

        for (var entry : data.entrySet()) {
            builder.append("--" + boundary + "\r\n");

            builder.append(String.format(
                    "Content-Disposition: form-data; name=\"%s\"\r\n" + "\r\n%s\r\n",
                    entry.getKey(),
                    entry.getValue().value()));
        }

        builder.append("--" + boundary + "--");

        return builder.toString();
    }
}