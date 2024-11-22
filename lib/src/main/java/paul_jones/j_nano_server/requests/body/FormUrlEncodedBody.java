package paul_jones.j_nano_server.requests.body;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.TreeMap;

public final class FormUrlEncodedBody extends Body {

    private final TreeMap<String, String> data = new TreeMap<>();

    public FormUrlEncodedBody(byte[] rawData) {
        var items = new String(rawData).trim().split("&");

        ArrayList<String> assignments = new ArrayList<>(items.length);

        for (var item : items) {
            if (item.length() > 0) {
                assignments.add(item);
            }
        }

        for (var assignment : assignments) {
            var parts = assignment.split("=");

            if (parts.length != 2) {
                throw new RuntimeException(String.format("Invalid form data line: '%s'", assignment));
            }

            String key = URLDecoder.decode(parts[0], Charset.defaultCharset());
            String value = URLDecoder.decode(parts[1], Charset.defaultCharset());

            data.put(key, value);
        }
    }

    @Override
    public TreeMap<String, String> getData() {
        return data;
    }

}
