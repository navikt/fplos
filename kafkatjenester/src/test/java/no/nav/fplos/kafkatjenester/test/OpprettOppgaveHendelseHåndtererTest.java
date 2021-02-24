package no.nav.fplos.kafkatjenester.test;

import no.nav.foreldrepenger.domene.typer.AktørId;
import no.nav.foreldrepenger.loslager.BehandlingId;
import no.nav.foreldrepenger.loslager.oppgave.BehandlingStatus;
import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;
import no.nav.foreldrepenger.loslager.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepository;
import no.nav.fplos.foreldrepengerbehandling.Aksjonspunkt;
import no.nav.fplos.foreldrepengerbehandling.BehandlingFpsak;
import no.nav.fplos.foreldrepengerbehandling.Lazy;
import no.nav.fplos.kafkatjenester.OppgaveEgenskapHandler;
import no.nav.fplos.oppgavestatistikk.OppgaveStatistikk;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(MockitoExtension.class)
class OpprettOppgaveHendelseHåndtererTest {
    private final OppgaveRepository oppgaveRepository = mock(OppgaveRepository.class);
    private final OppgaveStatistikk oppgaveStatistikk = mock(OppgaveStatistikk.class);
    private final OppgaveEgenskapHandler oppgaveEgenskapHandler = new OppgaveEgenskapHandler(oppgaveRepository);

    @Test
    public void skalOppretteOppgave() {
        var behandlingId = BehandlingId.random();

        var behandlingstidFrist = LocalDate.now().plusDays(10);
        var behandlingOpprettet = LocalDateTime.now();
        var aktørId = AktørId.dummy();
        var behandlingFpsak = BehandlingFpsak.builder()
                .medBehandlingOpprettet(behandlingOpprettet)
                .medBehandlingId(behandlingId)
                .medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
                .medFørsteUttaksdag(new Lazy<>(OpprettOppgaveHendelseHåndtererTest::førsteUttaksDag))
                .medErBerørtBehandling(false)
                .medErEndringssøknad(false)
                .medBehandlingstidFrist(behandlingstidFrist)
                .medAksjonspunkter(new Lazy<>(this::aksjonspunkter))
                .medStatus("OPPRE")
                .build();

        behandlingFpsak.setSaksnummer("1234");
        behandlingFpsak.setAktørId(aktørId.getId());
        behandlingFpsak.setYtelseType(FagsakYtelseType.FORELDREPENGER);

        var opprettOppgaveHåndterer = new OpprettOppgaveHendelseHåndterer(oppgaveRepository, oppgaveEgenskapHandler, oppgaveStatistikk, behandlingFpsak);
        opprettOppgaveHåndterer.håndter();
        ArgumentCaptor<Oppgave> oppgaveArgument = ArgumentCaptor.forClass(Oppgave.class);
        verify(oppgaveRepository, times(1)).lagre(oppgaveArgument.capture());

        Oppgave opprettetOppgave = oppgaveArgument.getValue();
        assertThat(opprettetOppgave.getBehandlingOpprettet()).isEqualTo(behandlingFpsak.getBehandlingOpprettet());
        assertThat(opprettetOppgave.getAktiv()).isTrue();
        assertThat(opprettetOppgave.getBehandlingId()).isEqualTo(behandlingId);
        assertThat(opprettetOppgave.getBehandlingType()).isEqualTo(BehandlingType.FØRSTEGANGSSØKNAD);
        assertThat(opprettetOppgave.getBehandlingsfrist()).isEqualTo(behandlingstidFrist.atStartOfDay());
        assertThat(opprettetOppgave.getAktorId()).isEqualTo(aktørId);
        assertThat(opprettetOppgave.getForsteStonadsdag()).isEqualTo(førsteUttaksDag());
        assertThat(opprettetOppgave.getHref()).isNull();
        assertThat(opprettetOppgave.getFagsakSaksnummer()).isEqualTo(Long.valueOf(behandlingFpsak.getSaksnummer()));
        assertThat(opprettetOppgave.getOppgaveAvsluttet()).isNull();
        assertThat(opprettetOppgave.getBehandlingStatus()).isEqualTo(BehandlingStatus.fraKode(behandlingFpsak.getStatus()));
        assertThat(opprettetOppgave.getBehandlendeEnhet()).isEqualTo(behandlingFpsak.getBehandlendeEnhetNavn());
        assertThat(opprettetOppgave.getSystem()).isEqualTo("FPSAK");
        assertThat(opprettetOppgave.getFagsakYtelseType()).isEqualTo(behandlingFpsak.getYtelseType());
    }

    private static LocalDate førsteUttaksDag() {
        return LocalDate.of(2021, 3, 1);
    }

    private List<Aksjonspunkt> aksjonspunkter() {
        var aksjonspunkt = Aksjonspunkt.builder()
                .medDefinisjon("7001")
                .medBegrunnelse("Testbegrunnelse")
                .medFristTid(null)
                .medStatus("OPPR")
                .build();
        return List.of(aksjonspunkt);
    }

}
