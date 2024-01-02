package no.nav.foreldrepenger.los.felles.util;


import java.util.Locale;

public final class StringUtils {

    private StringUtils() { }

    public static String formaterMedStoreOgSmåBokstaver(String tekst) {
        if (tekst == null || tekst.trim().isEmpty()) {
            return null;
        }
        var skilletegnPattern = "[\\s().,\\-]";
        var tegn = tekst.trim().toLowerCase(Locale.getDefault()).toCharArray();
        var nesteSkalHaStorBokstav = true;
        for (var i = 0; i < tegn.length; i++) {
            var erSkilletegn = String.valueOf(tegn[i]).matches(skilletegnPattern);
            if (!erSkilletegn && nesteSkalHaStorBokstav) {
                tegn[i] = Character.toTitleCase(tegn[i]);
            }
            nesteSkalHaStorBokstav = erSkilletegn;
        }
        return new String(tegn);
    }

}
