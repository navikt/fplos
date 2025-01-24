package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.vedtak.hendelser.behandling.Aksjonspunktstatus;
import no.nav.vedtak.hendelser.behandling.AktørId;
import no.nav.vedtak.hendelser.behandling.Behandlingsstatus;
import no.nav.vedtak.hendelser.behandling.Behandlingstype;
import no.nav.vedtak.hendelser.behandling.Behandlingsårsak;
import no.nav.vedtak.hendelser.behandling.Kildesystem;
import no.nav.vedtak.hendelser.behandling.Ytelse;
import no.nav.vedtak.hendelser.behandling.los.LosBehandlingDto;
import no.nav.vedtak.hendelser.behandling.los.LosBehandlingDto.LosAksjonspunktDto;

class FpsakOppgaveEgenskapFinnerTest {

    @Test
    void skalHaKlagePåTilbakebetalingEgenskapVedÅrsakKlageTilbakebetaling() {
        var dto = lagLosBehandlingDto(List.of(), List.of(Behandlingsårsak.KLAGE_TILBAKEBETALING));
        var fpsakEgenskaper = new FpsakOppgaveEgenskapFinner(dto);
        Assertions.assertThat(fpsakEgenskaper.getAndreKriterier()).contains(AndreKriterierType.KLAGE_PÅ_TILBAKEBETALING);
    }

    @Test
    void skalFåEgenskapEøsSakNårMarkertSomEøs() {
        var eøsBehandlingDto = lagLosBehandlingDto(List.of("EØS_BOSATT_NORGE"), null);
        var eøsFpsakEgenskaper = new FpsakOppgaveEgenskapFinner(eøsBehandlingDto);
        Assertions.assertThat(eøsFpsakEgenskaper.getAndreKriterier()).contains(AndreKriterierType.EØS_SAK);

        var ikkeEøsBehandlingDto = lagLosBehandlingDto(List.of("BOSATT_UTLAND"), null);
        var ikkeEøsFpsakEgenskaper = new FpsakOppgaveEgenskapFinner(ikkeEøsBehandlingDto);
        Assertions.assertThat(ikkeEøsFpsakEgenskaper.getAndreKriterier()).isNotEmpty().doesNotContain(AndreKriterierType.EØS_SAK);
    }

    @Test
    void skalFåEgenskapUtlandVedBosattUtland() {
        var eøsBehandling = lagLosBehandlingDto(List.of("EØS_BOSATT_NORGE"),null);
        var eøsOppgaveKriterier = new FpsakOppgaveEgenskapFinner(eøsBehandling).getAndreKriterier();
        Assertions.assertThat(eøsOppgaveKriterier)
                  .isNotEmpty()
                  .doesNotContain(AndreKriterierType.UTLANDSSAK);

        var bosattUtlandOppgaveKriterier = new FpsakOppgaveEgenskapFinner(lagLosBehandlingDto(List.of("BOSATT_UTLAND"), null)).getAndreKriterier();
        Assertions.assertThat(bosattUtlandOppgaveKriterier).contains(AndreKriterierType.UTLANDSSAK);

        var nasjonalOppgaveKriterier = new FpsakOppgaveEgenskapFinner(lagLosBehandlingDto(List.of(), null)).getAndreKriterier();
        Assertions.assertThat(nasjonalOppgaveKriterier).isEmpty();
    }

    @Test
    void testAksjonspunkterTilAndreKriterierTyperMapping() {
        var cases = new HashMap<String, AndreKriterierType>();
        cases.put("5082", AndreKriterierType.VURDER_FORMKRAV);
        cases.put("5016", AndreKriterierType.TIL_BESLUTTER);
        cases.put("5012", AndreKriterierType.PAPIRSØKNAD);

        cases.forEach((k, v) -> {
            var ap = new LosAksjonspunktDto(k, Aksjonspunktstatus.OPPRETTET, null);
            var resultat = new FpsakOppgaveEgenskapFinner(lagLosBehandlingDto(List.of(), null, ap));
            Assertions.assertThat(resultat.getAndreKriterier()).isEqualTo(List.of(v));
        });
    }

    @Test
    void skalIkkeReturnereDuplikateAndreKriterierTyper() {
        var ap = new LosAksjonspunktDto("5082", Aksjonspunktstatus.OPPRETTET, null);
        var apISammeGruppe = new LosAksjonspunktDto("5083", Aksjonspunktstatus.OPPRETTET, null);
        var resultat = new FpsakOppgaveEgenskapFinner(lagLosBehandlingDto(List.of(), null, ap, apISammeGruppe));
        Assertions.assertThat(resultat.getAndreKriterier())
                  .hasSize(1)
                  .containsExactly(AndreKriterierType.VURDER_FORMKRAV);
    }

    @Test
    void skalIkkeMappeInaktiveAksjonspunktTilAndreKriterierTyper() {
        var ap = new LosAksjonspunktDto("5082", Aksjonspunktstatus.AVBRUTT, null);
        var resultat = new FpsakOppgaveEgenskapFinner(lagLosBehandlingDto(List.of(), null, ap));
        Assertions.assertThat(resultat.getAndreKriterier()).isEmpty();
    }

    @Test
    void aktiv5068GirVurderInnhentingAvSED() {
        var aktiv5068 = new LosAksjonspunktDto("5068", Aksjonspunktstatus.OPPRETTET, null);
        var result = new FpsakOppgaveEgenskapFinner(lagLosBehandlingDto(List.of("EØS_BOSATT_NORGE"), null, aktiv5068));
        Assertions.assertThat(result.getAndreKriterier()).contains(AndreKriterierType.VURDER_EØS_OPPTJENING);
    }

    @Test
    void utførtEllerAvbrutt5068GirIkkeVurderSed() {
        var utført5068 = new LosAksjonspunktDto("5068", Aksjonspunktstatus.UTFØRT, null);
        var utført5068Resultat = new FpsakOppgaveEgenskapFinner(lagLosBehandlingDto(List.of("EØS_BOSATT_NORGE"), null, utført5068));
        Assertions.assertThat(utført5068Resultat.getAndreKriterier())
                  .isNotEmpty()
                  .doesNotContain(AndreKriterierType.VURDER_EØS_OPPTJENING);

        var avbrutt5068 = new LosAksjonspunktDto("5068", Aksjonspunktstatus.AVBRUTT, null);
        var avbrutt5068Resultat = new FpsakOppgaveEgenskapFinner(lagLosBehandlingDto(List.of("EØS_BOSATT_NORGE"), null, avbrutt5068));
        Assertions.assertThat(avbrutt5068Resultat.getAndreKriterier())
                  .isNotEmpty()
                  .doesNotContain(AndreKriterierType.VURDER_EØS_OPPTJENING);
    }

    @Test
    void aktiv5001GirKontrollerTerminbekreftelse() {
        var aktiv5001 = new LosAksjonspunktDto("5001", Aksjonspunktstatus.OPPRETTET, null);
        var result = new FpsakOppgaveEgenskapFinner(lagLosBehandlingDto(List.of(), null, aktiv5001));
        Assertions.assertThat(result.getAndreKriterier()).contains(AndreKriterierType.TERMINBEKREFTELSE);
    }

    @Test
    void skalIkkeHaEgenskapVurderSedNårNasjonalSak() {
        var aktiv5068 = new LosAksjonspunktDto("5068", Aksjonspunktstatus.OPPRETTET, null);
        var resultat = new FpsakOppgaveEgenskapFinner(lagLosBehandlingDto(List.of(), null, aktiv5068));
        Assertions.assertThat(resultat.getAndreKriterier()).isEmpty();
    }

    static LosBehandlingDto lagLosBehandlingDto(List<String> sakegenskaper, List<Behandlingsårsak> behandlingsårsaker, LosAksjonspunktDto... dto) {
        return new LosBehandlingDto(UUID.randomUUID(), Kildesystem.FPSAK, "42", Ytelse.FORELDREPENGER, new AktørId("1234"),
            Behandlingstype.KLAGE, Behandlingsstatus.UTREDES, LocalDateTime.now(), "0001", LocalDate.now(),
            "z999999", List.of(dto), Optional.ofNullable(behandlingsårsaker).orElse(List.of()), false,
            true, sakegenskaper, new LosBehandlingDto.LosForeldrepengerDto(LocalDate.now()), List.of(), null);
    }

}
