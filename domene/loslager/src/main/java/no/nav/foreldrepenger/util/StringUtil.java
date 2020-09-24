package no.nav.foreldrepenger.util;

public final class StringUtil {
    public StringUtil() {
    }

    public static String mask(String value) {
        return (value != null) && (value.length() == 11) ? value.substring(0, 6) + "*****" : value;
    }
}
