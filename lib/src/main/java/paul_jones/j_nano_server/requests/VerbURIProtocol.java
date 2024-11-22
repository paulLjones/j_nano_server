package paul_jones.j_nano_server.requests;

import java.net.URI;

public record VerbURIProtocol(Verb verb, URI uri, String protocol) {
    static VerbURIProtocol parseFromLine(String line) {
        final var parts = line.split(" ", 3);

        if (parts.length != 3) {
            throw new RuntimeException(String.format("Expected '<verb> <uri> <protocol>', got '%s'", line));
        }

        var verbString = parts[0];
        var uriString = parts[1];
        var protocol = parts[2];

        return new VerbURIProtocol(Verb.valueOf(verbString), URI.create(uriString), protocol);
    }
}
