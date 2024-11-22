package paul_jones.j_nano_server.requests.body;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.TreeMap;

class FormUrlEncodedBodyTest {
    @Test
    public void correctlyDecodes_urlEncodedData() {
        var data = new TreeMap<String, String>();
        data.put("meaning_of_life", "42");
        data.put("is_form_encoded", "yes");
        data.put("my_name", "THE ONE AND ONLY @ MASTER");

        var urlBody = FormUrlEncodedBodyTestHelpers.convertMapToUrlBody(data);

        var body = new FormUrlEncodedBody(urlBody.getBytes());
        var parsedData = body.getData();

        assertEquals(data, parsedData);
    }
}

class FormUrlEncodedBodyTestHelpers {
    static String convertMapToUrlBody(TreeMap<String, String> data) {
        StringBuilder builder = new StringBuilder();

        for (var entry : data.entrySet()) {
            builder.append(String.format(
                    "%s=%s&",
                    URLEncoder.encode(entry.getKey(), Charset.defaultCharset()),
                    URLEncoder.encode(entry.getValue(), Charset.defaultCharset())));
        }
        // Remove trailing &
        builder.deleteCharAt(builder.length() - 1);

        return builder.toString();
    }
}
