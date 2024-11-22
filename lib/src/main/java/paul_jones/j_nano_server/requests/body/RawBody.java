package paul_jones.j_nano_server.requests.body;

public final class RawBody extends Body {

    private final byte[] body;

    public RawBody(byte[] body) {
        this.body = body;
    }

    @Override
    public byte[] getData() {
        return body;
    }

}
