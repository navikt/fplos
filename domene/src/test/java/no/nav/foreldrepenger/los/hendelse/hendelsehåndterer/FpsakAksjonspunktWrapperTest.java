package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.FpsakAksjonspunktWrapper;
import no.nav.foreldrepenger.los.klient.fpsak.Aksjonspunkt;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;

class FpsakAksjonspunktWrapperTest {

    private final Aksjonspunkt apOverstyrtTilNasjonal = Aksjonspunkt.builder()
        .medStatus("OPPR")
        .medDefinisjon("6068")
        .medBegrunnelse("NASJONAL")
        .build();
    private final Aksjonspunkt apOverstyrtTilBosattUtland = Aksjonspunkt.builder()
        .medStatus("OPPR")
        .medDefinisjon("6068")
        .medBegrunnelse("BOSATT_UTLAND")
        .build();
    private final Aksjonspunkt apOverstyrtTilEØSBosattNorge = Aksjonspunkt.builder()
        .medStatus("OPPR")
        .medDefinisjon("6068")
        .medBegrunnelse("EØS_BOSATT_NORGE")
        .build();
    private final Aksjonspunkt apVurderSed = Aksjonspunkt.builder().medStatus("OPPR").medDefinisjon("5068").medBegrunnelse("").build();

    @Test
    void skalMappeKoderTilAndreKriterierTyper() {
        var cases = new HashMap<String, AndreKriterierType>();
        cases.put("5083", AndreKriterierType.VURDER_FORMKRAV);
        cases.put("5082", AndreKriterierType.VURDER_FORMKRAV);
        cases.put("5016", AndreKriterierType.TIL_BESLUTTER);
        cases.put("5012", AndreKriterierType.PAPIRSØKNAD); // finnes flere i gruppen
        cases.put("5068", AndreKriterierType.UTLANDSSAK); // finnes flere i gruppen

        cases.forEach((k, v) -> {
            var ap = åpentAksjonspunkt(k);
            var result = result(ap);
            assertThat(result).isEqualTo(List.of(v));
        });
    }

    @Test
    void skalIkkeReturnereDuplikateAndreKriterierTyper() {
        var ap = åpentAksjonspunkt("5082");
        var apISammeGruppe = åpentAksjonspunkt("5083");
        var result = result(ap, apISammeGruppe);
        assertThat(result).isEqualTo(List.of(AndreKriterierType.VURDER_FORMKRAV));
    }

    @Test
    void skalIkkeMappeInaktiveAksjonspunktTilAndreKriterierTyper() {
        var ap = Aksjonspunkt.builder().medStatus("AVBR").medDefinisjon("5082").medBegrunnelse("").build();
        var result = result(ap);
        assertThat(result).isEmpty();
    }

    @Test
    void overstyrtTilIkkeUtlandSkalIkkeGiUtlandOppgaveegenskap() {
        var result = result(apOverstyrtTilNasjonal, apVurderSed);
        assertThat(result).isEmpty();
    }

    @Test
    void overstyrtTilBosattUtlandSkalGiUtlandOppgaveegenskap() {
        var result = result(apOverstyrtTilBosattUtland);
        assertThat(result).contains(AndreKriterierType.UTLANDSSAK);
    }

    @Test
    void overstyrtTilEøsBosattNorgeSkalGiUtlandOppgaveegenskap() {
        var result = result(apOverstyrtTilEØSBosattNorge);
        assertThat(result).contains(AndreKriterierType.UTLANDSSAK);
    }

    private static List<AndreKriterierType> result(Aksjonspunkt... aksjonspunkt) {
        return FpsakAksjonspunktWrapper.getKriterier(List.of(aksjonspunkt), null);
    }

    private static Aksjonspunkt åpentAksjonspunkt(String definisjonKode) {
        return Aksjonspunkt.builder().medBegrunnelse("begrunnelse").medDefinisjon(definisjonKode).medStatus("OPPR").build();
    }

}
