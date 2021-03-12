package no.nav.foreldrepenger.los.web.app.sikkerhetsfilter;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class SimpelHvitvaskerTest {
    private String input_UfarligTekst = "Helt ufarilig string med diverse noe < > ping plongæøåÆØÅ";
    private String input_scriptTekst = "<Script kiddi=pingo> Noe helt greit <script/>";
    private String input_TagTekst = "<tag> tagging </tag>;";

    //skal brukes som default vasking
    private String resultatAvKunBokstaverHvitvasking_UfarligTekst = "Helt ufarilig string med diverse noe _ _ ping plongæøåÆØÅ";
    private String resultatAvKunBokstaverHvitvasking_ScriptTekst = "_Script kiddi_pingo_ Noe helt greit _script__";
    private String resultatAvKunBokstaverHvitvasking_TagTekst = "_tag_ tagging __tag__";

    //skal brukes for å vaske query string.
    private String resultatAvBokstaverOgVanligeTegn_UfarligTekst = "Helt ufarilig string med diverse noe _ _ ping plongæøåÆØÅ";
    private String resultatAvBokstaverOgVanligeTegn_ScriptTekst = "_Script kiddi=pingo_ Noe helt greit _script__";
    private String resultatAvBokstaverOgVanligeTegn_TagTekst = "_tag_ tagging __tag__";

    //skal brukes for å vaske cookie.
    private String resultatAvCookie_UfarligTekst = "Helt_ufarilig_string_med_diverse_noe_<_>_ping_plong______";
    private String resultatAvCookie_ScriptTekst = "<Script_kiddi=pingo>_Noe_helt_greit_<script/>";
    private String resultatAvCookie_TagTekst = "<tag>_tagging_</tag>_";

    @Test
    public void testRestriktivHvitvaskUfarligTekst() {
        var sanitizedString = SimpelHvitvasker.hvitvaskKunBokstaver(input_UfarligTekst);
        assertThat(sanitizedString).isEqualTo(resultatAvKunBokstaverHvitvasking_UfarligTekst);
    }

    @Test
    public void testRestriktivHvitvaskingScriptTekst() {
        var sanitizedString = SimpelHvitvasker.hvitvaskKunBokstaver(input_scriptTekst);
        assertThat(sanitizedString).isEqualTo(resultatAvKunBokstaverHvitvasking_ScriptTekst);
    }

    @Test
    public void testRestriktivHvitvaskTagTekst() {
        var sanitizedString = SimpelHvitvasker.hvitvaskKunBokstaver(input_TagTekst);
        assertThat(sanitizedString).isEqualTo(resultatAvKunBokstaverHvitvasking_TagTekst);
    }

    @Test
    public void testBokstaverOgVanligeTegnHvitvaskUfarligTekst() {
        var sanitizedString = SimpelHvitvasker.hvitvaskBokstaverOgVanligeTegn(input_UfarligTekst);
        assertThat(sanitizedString).isEqualTo(resultatAvBokstaverOgVanligeTegn_UfarligTekst);
    }

    @Test
    public void testBokstaverOgVanligeTegnHvitvaskingScriptTekst() {
        var sanitizedString = SimpelHvitvasker.hvitvaskBokstaverOgVanligeTegn(input_scriptTekst);
        assertThat(sanitizedString).isEqualTo(resultatAvBokstaverOgVanligeTegn_ScriptTekst);
    }

    @Test
    public void testBokstaverOgVanligeTegnHvitvaskTagTekst() {
        var sanitizedString = SimpelHvitvasker.hvitvaskBokstaverOgVanligeTegn(input_TagTekst);
        assertThat(sanitizedString).isEqualTo(resultatAvBokstaverOgVanligeTegn_TagTekst);
    }

    @Test
    public void testCookieHvitvaskUfarligTekst() {
        var sanitizedString = SimpelHvitvasker.hvitvaskCookie(input_UfarligTekst);
        assertThat(sanitizedString).isEqualTo(resultatAvCookie_UfarligTekst);
    }

    @Test
    public void testCookieHvitvaskingScriptTekst() {
        var sanitizedString = SimpelHvitvasker.hvitvaskCookie(input_scriptTekst);
        assertThat(sanitizedString).isEqualTo(resultatAvCookie_ScriptTekst);
    }

    @Test
    public void testCookieHvitvaskTagTekst() {
        var sanitizedString = SimpelHvitvasker.hvitvaskCookie(input_TagTekst);
        assertThat(sanitizedString).isEqualTo(resultatAvCookie_TagTekst);
    }

}
