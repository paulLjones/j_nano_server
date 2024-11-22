package paul_jones.j_nano_server.requests.body.form_data;

public abstract sealed class FormData permits TextFormData, FileFormData {
    public abstract FormDataType type();
}