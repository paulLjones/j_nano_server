package paul_jones.j_nano_server.requests.body;

import paul_jones.j_nano_server.requests.body.form_data.FileFormData;
import paul_jones.j_nano_server.requests.body.form_data.FormData;
import paul_jones.j_nano_server.requests.body.form_data.TextFormData;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.TreeMap;

public final class MultipartFormDataBody extends Body {

    private final TreeMap<String, FormData> data;

    public MultipartFormDataBody(byte[] rawData, String boundary) {
        this.data = new MultipartFormDataParser(rawData, boundary).parse();
    }

    @Override
    public TreeMap<String, FormData> getData() {
        return data;
    }

}

class MultipartFormDataParser {
    private final byte[] rawData;
    private final String divider;

    int cursor = 0;

    MultipartFormDataParser(byte[] rawData, String boundary) {
        this.rawData = rawData;

        this.divider = "--" + boundary;
    }

    public TreeMap<String, FormData> parse() {

        TreeMap<String, FormData> data = new TreeMap<>();

        while (cursor < rawData.length) {
            tryParseDivider();
            cursor += divider.length() + 2;

            var headers = parseHeaders();

            String fieldName;
            var contentDispositionLine = headers.get("Content-Disposition");
            ContentDisposition contentDisposition;

            if (contentDispositionLine != null) {
                contentDisposition = parseContentDisposition(contentDispositionLine);
                fieldName = contentDisposition.name;

                if (fieldName == null) {
                    throw new RuntimeException("Missing field name");
                }
            } else {
                throw new RuntimeException("Missing Content-Disposition header");
            }

            // Should be empty line, so skip
            cursor += 2;

            int contentsStart = cursor;

            while (cannotParseDivider()) {
                cursor += dataDistanceTo(cursor, (byte) '-');
            }

            var contents = Arrays.copyOfRange(rawData, contentsStart, cursor - 2);

            if (contentDisposition.filename != null) {
                data.put(fieldName, new FileFormData(contentDisposition.filename, contents));
            } else {
                var line = new String(contents, StandardCharsets.UTF_8);
                data.put(fieldName, new TextFormData(line));
            }

            tryParseDivider();
            cursor += divider.length();

            if (parseString(rawData, cursor, "--") != -1) {
                return data;
            } else {
                // Pass and unparse divider
                cursor -= divider.length();
            }
        }

        return data;
    }

    private void tryParseDivider() {
        if (cannotParseDivider()) {
            throw new RuntimeException("Can not parse divider");
        }
    }

    private boolean cannotParseDivider() {
        return parseString(rawData, cursor, divider) == -1;
    }

    private int dataDistanceTo(int i, byte b) {
        int dist = 1;
        while ((i + dist) < rawData.length && rawData[i + dist] != b) {
            dist++;
        }

        return dist;
    }

    private TreeMap<String, String> parseHeaders() {
        var headers = new TreeMap<String, String>();

        var bytesToNewline = dataDistanceTo(cursor, (byte) '\n');
        while (bytesToNewline > 1) {
            byte[] buffer = Arrays.copyOfRange(rawData, cursor, cursor + bytesToNewline);
            var header = new String(buffer, StandardCharsets.UTF_8).split(":", 2);

            headers.put(header[0].trim(), header[1].trim());

            cursor += bytesToNewline + 1;
            bytesToNewline = dataDistanceTo(cursor, (byte) '\n');
        }

        return headers;
    }

    private ContentDisposition parseContentDisposition(String contentDisposition) {
        int startOfKeyValues = parseString(contentDisposition.getBytes(StandardCharsets.UTF_8), 0, "form-data; ");
        if (startOfKeyValues == -1) {
            throw new RuntimeException("Couldn't parse Content-Disposition key-values");
        }

        String keyValuesStr = contentDisposition.substring(startOfKeyValues);

        var keyValues = keyValuesStr.split(";");

        var keyValueMap = new TreeMap<String, String>();

        for (var pair : keyValues) {
            var parts = pair.trim().split("=", 2);

            keyValueMap.put(parts[0].trim(), parts[1].substring(1, parts[1].length() - 1));
        }

        var name = keyValueMap.get("name");
        var filename = keyValueMap.get("filename");

        return new ContentDisposition(name, filename);
    }

    /**
     * Attempts to find passed string in array from index
     *
     * @param data  - array to check
     * @param index - index to start checking from
     * @param str   - string to check for
     * @return index of last byte of match, returns -1 on failure
     */
    private int parseString(byte[] data, int index, String str) {
        final byte[] strBytes = str.getBytes(StandardCharsets.UTF_8);

        for (int i = index; i < index + strBytes.length; i++) {
            if (i >= data.length || data[i] != strBytes[i - index]) {
                return -1;
            }
        }

        return index + strBytes.length;
    }

    record ContentDisposition(String name, String filename) {
    }
}