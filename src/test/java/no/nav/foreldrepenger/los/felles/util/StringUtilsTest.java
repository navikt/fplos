package no.nav.foreldrepenger.los.felles.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringUtilsTest {

    @Test
    void testNullInput() {
        assertNull(StringUtils.formaterMedStoreOgSmåBokstaver(null));
    }

    @Test
    void testEmptyString() {
        assertNull(StringUtils.formaterMedStoreOgSmåBokstaver(""));
    }

    @Test
    void testNoPunctuation() {
        assertEquals("Text", StringUtils.formaterMedStoreOgSmåBokstaver("text"));
    }

    @Test
    void testLeadingPunctuation() {
        assertEquals(".Text", StringUtils.formaterMedStoreOgSmåBokstaver(".text"));
    }

    @Test
    void testTrailingPunctuation() {
        assertEquals("Text.", StringUtils.formaterMedStoreOgSmåBokstaver("text."));
    }

    @Test
    void testMixedCasing() {
        assertEquals("Text", StringUtils.formaterMedStoreOgSmåBokstaver("TeXT"));
    }

    @Test
    void testMultipleSpaces() {
        assertEquals("Text  With Multiple  Spaces", StringUtils.formaterMedStoreOgSmåBokstaver("Text  with multiple  spaces"));
    }

    @Test
    void testSpecialCharacters() {
        assertEquals("Text@123", StringUtils.formaterMedStoreOgSmåBokstaver("TeXT@123"));
    }

    @Test
    void testOnlyPunctuation() {
        assertEquals("...!!!", StringUtils.formaterMedStoreOgSmåBokstaver("...!!!"));
    }

    @Test
    void testMultiplePunctuationMarks() {
        assertEquals(".-.Text", StringUtils.formaterMedStoreOgSmåBokstaver(".-.text"));
    }

}
