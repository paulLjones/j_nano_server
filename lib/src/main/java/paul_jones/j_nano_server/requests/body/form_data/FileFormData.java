package paul_jones.j_nano_server.requests.body.form_data;

import java.util.Arrays;

public final class FileFormData extends FormData {
    private final byte[] bytes;
    private final String filename;

    public FileFormData(String filename, byte[] bytes) {
        this.filename = filename;
        this.bytes = bytes;
    }

    @Override
    public FormDataType type() {
        return FormDataType.FILE;
    }

    public byte[] bytes() {
        return bytes;
    }

    public String filename() {
        return filename;
    }

    @Override
    public String toString() {
        return String.format("FileFormData: \"%s\"", this.filename);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof FileFormData d) {
            return d.filename.equals(this.filename) && Arrays.equals(d.bytes, this.bytes);
        }

        return false;
    }
}