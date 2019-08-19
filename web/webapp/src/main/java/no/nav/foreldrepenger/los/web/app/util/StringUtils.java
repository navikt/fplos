package no.nav.foreldrepenger.los.web.app.util;

public class StringUtils {

    private StringUtils() {}

    public static boolean erIkkeTom(String str) {
        return (str != null) && (str.length() > 0);
    }

    public static boolean erTom(String str) {
        return !StringUtils.erIkkeTom(str);
    }

    public static String capitalizeNavn(String str) {
        if (str != null && str.length() > 0) {
            String current = str.substring(0,1).toUpperCase();
            return current + recursiveCap(str.substring(1).toLowerCase(), current);
        }
        return str;
    }

    private static String recursiveCap(String str, String previous) {
        if (str.length() > 0) {
            String current = str.substring(0,1);
            if (shouldUppercase(previous)) {
                return current.toUpperCase() + recursiveCap(str.substring(1), current);
            }
            return current + recursiveCap(str.substring(1), current);
        }
        return str;
    }

    private static boolean shouldUppercase(String previous) {
        return previous.equals(" ") || previous.equals("-");
    }

}
