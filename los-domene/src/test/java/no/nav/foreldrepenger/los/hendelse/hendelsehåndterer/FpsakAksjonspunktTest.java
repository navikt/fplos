package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer;

import no.nav.foreldrepenger.los.klient.fpsak.Aksjonspunkt;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

import static no.nav.foreldrepenger.los.oppgave.AndreKriterierType.PAPIRSØKNAD;
import static no.nav.foreldrepenger.los.oppgave.AndreKriterierType.TIL_BESLUTTER;
import static no.nav.foreldrepenger.los.oppgave.AndreKriterierType.UTLANDSSAK;
import static no.nav.foreldrepenger.los.oppgave.AndreKriterierType.VURDER_FARESIGNALER;
import static no.nav.foreldrepenger.los.oppgave.AndreKriterierType.VURDER_FORMKRAV;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FpsakAksjonspunktTest {

    @Test
    public void skalMappeKoderTilAndreKriterierTyper() {
        var cases = new HashMap<String, AndreKriterierType>();
        cases.put("5083", VURDER_FORMKRAV);
        cases.put("5082", VURDER_FORMKRAV);
        cases.put("5016", TIL_BESLUTTER);
        cases.put("5012", PAPIRSØKNAD); // finnes flere i gruppen
        cases.put("5068", UTLANDSSAK); // finnes flere i gruppen
        cases.put("5095", VURDER_FARESIGNALER);

        cases.forEach((k, v) -> {
            var ap = åpentAksjonspunkt(k);
            var result = new FpsakAksjonspunkt(List.of(ap)).getKriterier();
            assertThat(result).isEqualTo(List.of(v));
        });
    }

    @Test
    public void skalIkkeReturnereDuplikateAndreKriterierTyper() {
        var sammeGruppeAksjonspunkter = List.of(åpentAksjonspunkt("5082"),
                åpentAksjonspunkt("5083"));
        var result = new FpsakAksjonspunkt(sammeGruppeAksjonspunkter).getKriterier();
        assertThat(result).isEqualTo(List.of(VURDER_FORMKRAV));
    }

    @Test
    public void skalIkkeMappeInaktiveAksjonspunktTilAndreKriterierTyper() {
        var ap = Aksjonspunkt.builder().medStatus("AVBR").medDefinisjon("5082").medBegrunnelse("").build();
        var result = new FpsakAksjonspunkt(List.of(ap)).getKriterier();
        assertTrue(result.isEmpty());
    }

    private static Aksjonspunkt åpentAksjonspunkt(String definisjonKode) {
        return Aksjonspunkt.builder()
                .medBegrunnelse("begrunnelse")
                .medDefinisjon(definisjonKode)
                .medStatus("OPPR")
                .build();
    }

}
