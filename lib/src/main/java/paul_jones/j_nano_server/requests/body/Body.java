package paul_jones.j_nano_server.requests.body;

public sealed abstract class Body permits RawBody, FormUrlEncodedBody, MultipartFormDataBody {
    public abstract Object getData();
}
