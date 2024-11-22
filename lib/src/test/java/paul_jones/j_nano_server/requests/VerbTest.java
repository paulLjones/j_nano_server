package paul_jones.j_nano_server.requests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VerbTest {
    @Test
    public void verbsReturnExpectedResult_for_hasBody() {
        assertTrue(Verb.POST.hasBody());
        assertTrue(Verb.PUT.hasBody());
        assertTrue(Verb.PATCH.hasBody());

        assertFalse(Verb.GET.hasBody());
        assertFalse(Verb.HEAD.hasBody());
        assertFalse(Verb.DELETE.hasBody());
        assertFalse(Verb.TRACE.hasBody());
        assertFalse(Verb.OPTIONS.hasBody());
        assertFalse(Verb.CONNECT.hasBody());
    }
}
