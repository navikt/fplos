package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.los.klient.fpsak.BehandlingFpsak;
import no.nav.foreldrepenger.los.klient.fpsak.Lazy;
import no.nav.foreldrepenger.los.klient.fpsak.dto.Kontrollresultat;
import no.nav.foreldrepenger.los.klient.fpsak.dto.KontrollresultatDto;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;

class FpsakOppgaveEgenskapFinnerTest {

    @Test
    void skalLeggeTilFaresignaler() {
        var resultat = new FpsakOppgaveEgenskapFinner(BehandlingFpsak.builder()
                .medKontrollresultat(new Lazy<>(() -> new KontrollresultatDto(Kontrollresultat.HØY)))
                .build());
        assertThat(resultat.getAndreKriterier()).contains(AndreKriterierType.VURDER_FARESIGNALER);
    }

    @Test
    void skalIkkeLeggeTilFaresignalerHvisIkkeHøy() {
        var resultat = new FpsakOppgaveEgenskapFinner(BehandlingFpsak.builder()
                .medKontrollresultat(new Lazy<>(() -> new KontrollresultatDto(Kontrollresultat.IKKE_HØY)))
                .build());
        assertThat(resultat.getAndreKriterier()).doesNotContain(AndreKriterierType.VURDER_FARESIGNALER);
    }

    @Test
    void skalVurdereSykdomHvisPleiepenger() {
        var resultat = new FpsakOppgaveEgenskapFinner(BehandlingFpsak.builder()
                .medErPleiepengerBehandling(true)
                .build());
        assertThat(resultat.getAndreKriterier()).contains(AndreKriterierType.VURDER_SYKDOM);
    }
}
