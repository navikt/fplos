package no.nav.foreldrepenger.los.web.app.sikkerhetsfilter;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class SimpelHvitvaskerTest {
    private String input_UfarligTekst = "Helt ufarilig string med diverse noe < > ping plongæøåÆØÅ";
    private String input_scriptTekst = "<Script kiddi=pingo> Noe helt greit <script/>";
    private String input_TagTekst = "<tag> tagging </tag>";

    //skal brukes som default vasking
    private String resultatAvKunBokstaverHvitvasking_UfarligTekst = "Helt ufarilig string med diverse noe _ _ ping plongæøåÆØÅ";
    private String resultatAvKunBokstaverHvitvasking_ScriptTekst = "_Script kiddi_pingo_ Noe helt greit _script__";
    private String resultatAvKunBokstaverHvitvasking_TagTekst = "_tag_ tagging __tag_";

    //skal brukes for å vaske query string.
    private String resultatAvBokstaverOgVanligeTegn_UfarligTekst = "Helt ufarilig string med diverse noe _ _ ping plongæøåÆØÅ";
    private String resultatAvBokstaverOgVanligeTegn_ScriptTekst = "_Script kiddi=pingo_ Noe helt greit _script__";
    private String resultatAvBokstaverOgVanligeTegn_TagTekst = "_tag_ tagging __tag_";

    //skal brukes for å vaske cookie.
    private String resultatAvCookie_UfarligTekst = "Helt_ufarilig_string_med_diverse_noe_<_>_ping_plong______";
    private String resultatAvCookie_ScriptTekst = "<Script_kiddi=pingo>_Noe_helt_greit_<script/>";
    private String resultatAvCookie_TagTekst = "<tag>_tagging_</tag>";

    @Test
    public void testRestriktivHvitvaskUfarligTekst() {
        String sanitizedString = SimpelHvitvasker.hvitvaskKunBokstaver(input_UfarligTekst);
        assertThat(resultatAvKunBokstaverHvitvasking_UfarligTekst).isEqualTo(sanitizedString);
    }

    @Test
    public void testRestriktivHvitvaskingScriptTekst() {
        String sanitizedString = SimpelHvitvasker.hvitvaskKunBokstaver(input_scriptTekst);
        assertThat(resultatAvKunBokstaverHvitvasking_ScriptTekst).isEqualTo(sanitizedString);
    }

    @Test
    public void testRestriktivHvitvaskTagTekst() {
        String sanitizedString = SimpelHvitvasker.hvitvaskKunBokstaver(input_TagTekst);
        assertThat(resultatAvKunBokstaverHvitvasking_TagTekst).isEqualTo(sanitizedString);
    }

    @Test
    public void testBokstaverOgVanligeTegnHvitvaskUfarligTekst() {
        String sanitizedString = SimpelHvitvasker.hvitvaskBokstaverOgVanligeTegn(input_UfarligTekst);
        assertThat(resultatAvBokstaverOgVanligeTegn_UfarligTekst).isEqualTo(sanitizedString);
    }

    @Test
    public void testBokstaverOgVanligeTegnHvitvaskingScriptTekst() {
        String sanitizedString = SimpelHvitvasker.hvitvaskBokstaverOgVanligeTegn(input_scriptTekst);
        assertThat(resultatAvBokstaverOgVanligeTegn_ScriptTekst).isEqualTo(sanitizedString);
    }

    @Test
    public void testBokstaverOgVanligeTegnHvitvaskTagTekst() {
        String sanitizedString = SimpelHvitvasker.hvitvaskBokstaverOgVanligeTegn(input_TagTekst);
        assertThat(resultatAvBokstaverOgVanligeTegn_TagTekst).isEqualTo(sanitizedString);
    }

    @Test
    public void testCookieHvitvaskUfarligTekst() {
        String sanitizedString = SimpelHvitvasker.hvitvaskCookie(input_UfarligTekst);
        assertThat(resultatAvCookie_UfarligTekst).isEqualTo(sanitizedString);
    }

    @Test
    public void testCookieHvitvaskingScriptTekst() {
        String sanitizedString = SimpelHvitvasker.hvitvaskCookie(input_scriptTekst);
        assertThat(resultatAvCookie_ScriptTekst).isEqualTo(sanitizedString);
    }

    @Test
    public void testCookieHvitvaskTagTekst() {
        String sanitizedString = SimpelHvitvasker.hvitvaskCookie(input_TagTekst);
        assertThat(resultatAvCookie_TagTekst).isEqualTo(sanitizedString);
    }

}
