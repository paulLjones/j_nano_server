package paul_jones.j_nano_server.requests.body.form_data;

public final class TextFormData extends FormData {
    private final String value;

    public TextFormData(String value) {
        this.value = value;
    }

    @Override
    public FormDataType type() {
        return FormDataType.TEXT;
    }

    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("TextFormData: \"%s\"", this.value);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof TextFormData d) {
            return d.value.equals(this.value);
        }

        return false;
    }
}