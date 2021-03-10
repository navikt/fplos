package no.nav.foreldrepenger.los.web.app.sikkerhetsfilter;

import static no.nav.vedtak.log.util.LoggerUtils.removeLineBreaks;

import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpelHvitvasker {

    private static final Logger log = LoggerFactory.getLogger(SimpelHvitvasker.class);

    private static final String TEGN = "a-zA-Z";
    private static final String TALL = "0-9";
    private static final String SPESJELLE = "æøåÆØÅAaÁáBbCcČčDdĐđEeFfGgHhIiJjKkLlMmNnŊŋOoPpRrSsŠšTtŦŧUuVvZzŽžéôèÉöüäÖÜÄ";

    private static final String KUN_BOKSTAVER = "[^" + TEGN + TALL + SPESJELLE + " ,.:\\-]";
    private static final Pattern KUN_BOKSTAVER_PATTERN = Pattern.compile(KUN_BOKSTAVER);

    private static final String KUN_BOKSTAVER_OG_VANLIGE_TEGN = "[^" + TEGN + TALL + SPESJELLE + " \\-._=%&*]";
    private static final Pattern KUN_BOKSTAVER_OG_VANLIGE_TEGN_PATTERN = Pattern.compile(KUN_BOKSTAVER_OG_VANLIGE_TEGN);

    private static final String ASCII_TEGN = "[^\\p{ASCII}&&[^\\s,;]]";
    private static final Pattern KUN_ASCII_TEGN_PATTERN = Pattern.compile(ASCII_TEGN);

    private SimpelHvitvasker() {
    }

    /**
     * Hvitvasker for alt som ikke er bokstaver
     * Legg merke til at det brukes negativen av matchingen pågrunn av bruk av replace istedet for retain.
     *
     * @param uvasketTekst Tekst som skal vaskes
     * @return ferdig vasket tekst
     */
    public static String hvitvaskKunBokstaver(String uvasketTekst) {
        if (uvasketTekst == null || uvasketTekst.isEmpty()) return uvasketTekst;

        var matcher = KUN_BOKSTAVER_PATTERN.matcher(uvasketTekst);
        if (matcher.find()) {
            String rensetTekst = matcher.replaceAll("_");

            if (log.isTraceEnabled()) {
                log.trace(removeLineBreaks("Hvitvasking av kun bokstav tekst: fra '{}' til '{}'"),
                    removeLineBreaks(uvasketTekst), removeLineBreaks(rensetTekst));
            }
            return rensetTekst;
        }
        return uvasketTekst;
    }

    /**
     * Hvitvasker som trolig skal brukes for queryparams og cookies
     * Legg merke til at det brukes negativen av matchingen pågrunn av bruk av replace istedet for retain.
     *
     * @param uvasketTekst Tekst som skal vaskes
     * @return ferdig vasket tekst
     */
    public static String hvitvaskBokstaverOgVanligeTegn(String uvasketTekst) {
        if (uvasketTekst == null || uvasketTekst.isEmpty()) return uvasketTekst;

        var matcher = KUN_BOKSTAVER_OG_VANLIGE_TEGN_PATTERN.matcher(uvasketTekst);
        if (matcher.find()) {
            String rensetTekst = matcher.replaceAll("_");
            if (log.isTraceEnabled()) {
                log.trace(removeLineBreaks("Hvitvasking av kunbokstaver og vanlige tegn: fra '{}' til '{}'"),
                    removeLineBreaks(uvasketTekst), removeLineBreaks(rensetTekst));
            }
            return rensetTekst;
        }
        return uvasketTekst;
    }

    /**
     * Hvitvasker som trolig skal brukes for queryparams og cookies
     * Legg merke til at det brukes negativen av matchingen pågrunn av bruk av replace istedet for retain.
     *
     * @param uvasketTekst Tekst som skal vaskes
     * @return ferdig vasket tekst
     */
    public static String hvitvaskCookie(String uvasketTekst) {
        if (uvasketTekst == null || uvasketTekst.isEmpty()) return uvasketTekst;

        var matcher = KUN_ASCII_TEGN_PATTERN.matcher(uvasketTekst);
        if (matcher.find()) {
            var rensetTekst = matcher.replaceAll("_");
            if (log.isTraceEnabled()) {
                log.trace(removeLineBreaks("Hvitvasking av kunbokstaver og vanlige tegn: fra '{}' til '{}'"),
                    removeLineBreaks(uvasketTekst), removeLineBreaks(rensetTekst));
            }
            return rensetTekst;
        }
        return uvasketTekst;
    }

}
