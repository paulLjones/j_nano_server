package paul_jones.j_nano_server.requests;

import java.net.URI;
import java.util.TreeMap;

import paul_jones.j_nano_server.requests.body.Body;

public class Request {
    public Verb verb;
    public URI uri;
    public TreeMap<String, String> headers;
    public Body body;

    public Request(VerbURIProtocol verbUriProtocol, TreeMap<String, String> headers, Body body) {
        this.verb = verbUriProtocol.verb();
        this.uri = verbUriProtocol.uri();
        this.headers = headers;
        this.body = body;
    }
}