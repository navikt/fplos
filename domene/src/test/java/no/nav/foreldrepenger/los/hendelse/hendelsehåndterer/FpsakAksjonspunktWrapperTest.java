package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;

import no.nav.vedtak.hendelser.behandling.los.LosFagsakEgenskaperDto;

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

        cases.forEach((k, v) -> {
            var ap = åpentAksjonspunkt(k);
            var result = utledKriterier(ap);
            assertThat(result).isEqualTo(List.of(v));
        });
    }

    @Test
    void skalIkkeReturnereDuplikateAndreKriterierTyper() {
        var ap = åpentAksjonspunkt("5082");
        var apISammeGruppe = åpentAksjonspunkt("5083");
        var result = utledKriterier(ap, apISammeGruppe);
        assertThat(result).isEqualTo(List.of(AndreKriterierType.VURDER_FORMKRAV));
    }

    @Test
    void skalIkkeMappeInaktiveAksjonspunktTilAndreKriterierTyper() {
        var ap = Aksjonspunkt.builder().medStatus("AVBR").medDefinisjon("5082").medBegrunnelse("").build();
        var result = utledKriterier(ap);
        assertThat(result).isEmpty();
    }

    @Test
    void overstyrtTilIkkeUtlandSkalIkkeGiUtlandOppgaveegenskap() {
        var result = utledKriterier(apOverstyrtTilNasjonal, apVurderSed);
        assertThat(result).isEmpty();
    }

    @Test
    void overstyrtTilBosattUtlandSkalGiUtlandOppgaveegenskap() {
        var result = utledKriterier(apOverstyrtTilBosattUtland);
        assertThat(result).contains(AndreKriterierType.UTLANDSSAK);
    }

    @Test
    void overstyrtTilEøsBosattNorgeSkalGiUtlandOppgaveegenskap() {
        var result = utledKriterier(apOverstyrtTilEØSBosattNorge);
        assertThat(result).contains(AndreKriterierType.UTLANDSSAK);
    }

    @Test
    void aktiv5068GirVurderSed() {
        var aktiv5068 = åpentAksjonspunkt("5068");
        var fagsakEgenskaper = new LosFagsakEgenskaperDto(Boolean.TRUE, null);
        var result = utledKriterier(fagsakEgenskaper, aktiv5068);
        assertThat(result).contains(AndreKriterierType.VURDER_EØS_OPPTJENING);
    }

    @Test
    void utførtEllerAvbrutt5068GirIkkeVurderSed() {
        var fagsakEgenskaper = new LosFagsakEgenskaperDto(Boolean.TRUE, LosFagsakEgenskaperDto.UtlandMarkering.EØS_BOSATT_NORGE);
        var uført5068 = new Aksjonspunkt("5068", "UTFO", "");
        var utført5068Resultat = utledKriterier(fagsakEgenskaper, uført5068);
        assertThat(utført5068Resultat).doesNotContain(AndreKriterierType.VURDER_EØS_OPPTJENING);

        var avbrutt5068 = new Aksjonspunkt("5068", "AVBR", "");
        var avbrutt5068Resultat = utledKriterier(fagsakEgenskaper, avbrutt5068);
        assertThat(avbrutt5068Resultat).doesNotContain(AndreKriterierType.VURDER_EØS_OPPTJENING);
    }

    @Test
    void skalIkkeHaEgenskapVurderSedNårNasjonalSak() {
        var fagsakEgenskaper = new LosFagsakEgenskaperDto(Boolean.TRUE, LosFagsakEgenskaperDto.UtlandMarkering.NASJONAL);
        var aktiv5068 = new Aksjonspunkt("5068", "OPPR", "");
        var resultat = utledKriterier(fagsakEgenskaper, aktiv5068);
        assertThat(resultat).doesNotContain(AndreKriterierType.VURDER_EØS_OPPTJENING);
    }

    private static List<AndreKriterierType> utledKriterier(Aksjonspunkt... aksjonspunkt) {
        return utledKriterier(null, aksjonspunkt);
    }

    private static List<AndreKriterierType> utledKriterier(LosFagsakEgenskaperDto sakDto, Aksjonspunkt... aksjonspunkter) {
        return FpsakAksjonspunktWrapper.getKriterier(List.of(aksjonspunkter), sakDto);
    }

    private static Aksjonspunkt åpentAksjonspunkt(String definisjonKode) {
        return Aksjonspunkt.builder().medBegrunnelse("begrunnelse").medDefinisjon(definisjonKode).medStatus("OPPR").build();
    }

}
