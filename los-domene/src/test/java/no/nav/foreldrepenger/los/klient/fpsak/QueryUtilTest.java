package no.nav.foreldrepenger.los.klient.fpsak;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class QueryUtilTest {

    @Test
    void testQuery() {
        var q = QueryUtil.split("a=3");
        assertEquals("3", q.get(0).getValue());
    }

}
