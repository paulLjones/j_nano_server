package paul_jones.j_nano_server.requests.body;

import org.junit.jupiter.api.Test;

import paul_jones.j_nano_server.requests.body.form_data.FileFormData;
import paul_jones.j_nano_server.requests.body.form_data.TextFormData;

import static org.junit.jupiter.api.Assertions.*;

import java.util.TreeMap;

// TODO Add tests to test for long files / files with dashes being corrupted (POSSIBLY request parser's content-length interpretation is wrong...)
// TODO Add tests for file uploads (e.g. binary data & multiple headers)
public class MultipartFormDataBodyTest {
    @Test
    public void correctlyDecodes_multipartFormData() {
        var data = new TreeMap<String, TextFormData>();
        data.put("meaning_of_life", new TextFormData("42"));
        data.put("is_form_encoded", new TextFormData("yes"));
        data.put("my_name", new TextFormData("THE ONE AND ONLY @ MASTER"));

        var boundary = "--boundary-string-1234";

        var urlBody = MultipartFormDataBodyTestHelpers.convertDataToMultipartFormData(data, boundary);

        var body = new MultipartFormDataBody(urlBody.getBytes(), boundary);
        var parsedData = body.getData();

        assertEquals(data, parsedData);
    }

    @Test
    public void correctlyDecodes_files() {
        var data = new TreeMap<String, FileFormData>();
        data.put("meaning_of_life", new FileFormData("meaning_of_life", "42".getBytes()));

        var boundary = "--boundary-string-1234";

        var strBody = MultipartFormDataBodyTestHelpers.convertFileToMultipartFormData(data, boundary);

        var body = new MultipartFormDataBody(strBody.getBytes(), boundary);
        var parsedData = body.getData();

        assertEquals(data, parsedData);
    }
}

class MultipartFormDataBodyTestHelpers {

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

    static String convertFileToMultipartFormData(TreeMap<String, FileFormData> data, String boundary) {
        StringBuilder builder = new StringBuilder();

        for (var entry : data.entrySet()) {
            builder.append("--" + boundary + "\r\n");

            builder.append(String.format(
                    "Content-Disposition: form-data; name=\"%s\"; filename=\"%s\"\r\n" +
                            "Content-Type: application/octet-stream\r\n" +
                            "\r\n%s\r\n",
                    entry.getKey(),
                    entry.getKey(),
                    new String(entry.getValue().bytes())));
        }

        builder.append("--" + boundary + "--");

        return builder.toString();
    }
}