package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak;

import static no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.OppgaveTestUtil.behandlingFpsak;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.time.temporal.ChronoUnit;

import javax.persistence.EntityManager;

import no.nav.foreldrepenger.los.oppgave.OppgaveTjeneste;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.dbstøtte.DBTestUtil;
import no.nav.foreldrepenger.extensions.JpaExtension;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.OppgaveEgenskapHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.håndterere.GenerellOpprettOppgaveHendelseHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.håndterere.OppdaterOppgaveegenskaperHendelseHåndterer;
import no.nav.foreldrepenger.los.klient.fpsak.BehandlingFpsak;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonTjeneste;
import no.nav.foreldrepenger.los.statistikk.kø.KøStatistikkTjeneste;
import no.nav.foreldrepenger.los.statistikk.kø.OppgaveknytningerFørEtterOppdatering;


@ExtendWith(MockitoExtension.class)
@ExtendWith(JpaExtension.class)
class OppdaterOppgaveegenskaperHendelseHåndtererTest {

    private final KøStatistikkTjeneste køStatistikk = mock(KøStatistikkTjeneste.class);
    private EntityManager entityManager;
    private OppgaveRepository oppgaveRepository;
    private OppgaveEgenskapHåndterer oppgaveEgenskapHåndterer;
    private ReservasjonTjeneste reservasjonTjeneste;
    private OppgaveTjeneste oppgaveTjeneste;

    @BeforeEach
    private void setUp(EntityManager entityManager) {
        this.entityManager = entityManager;
        oppgaveRepository = new OppgaveRepository(entityManager);
        ReservasjonRepository reservasjonRepository = new ReservasjonRepository(entityManager);
        oppgaveEgenskapHåndterer = new OppgaveEgenskapHåndterer(oppgaveRepository);
        oppgaveTjeneste = new OppgaveTjeneste(oppgaveRepository, reservasjonTjeneste);
        reservasjonTjeneste = new ReservasjonTjeneste(oppgaveRepository, reservasjonRepository);
    }

    @Test
    public void skalVidereføreReservasjonVedOppdateringer() {
        // arrange
        var behandlingFpsak = behandlingFpsak();
        new GenerellOpprettOppgaveHendelseHåndterer(oppgaveTjeneste, oppgaveEgenskapHåndterer, køStatistikk, behandlingFpsak).håndter();
        var oppgave = DBTestUtil.hentUnik(entityManager, Oppgave.class);
        var reservasjon = reservasjonTjeneste.reserverOppgave(oppgave);
        var reservertTil = reservasjon.getReservertTil().truncatedTo(ChronoUnit.SECONDS);

        // act
        new OppdaterOppgaveegenskaperHendelseHåndterer(oppgaveRepository, oppgaveEgenskapHåndterer, køStatistikk, behandlingFpsak).håndter();

        // assert
        assertThat(oppgave.getAktiv()).isTrue();
        assertThat(oppgave.harAktivReservasjon()).isTrue();
        var reservertTilEtterOppdatering = reservasjon.getReservertTil().truncatedTo(ChronoUnit.SECONDS);
        assertThat(reservertTilEtterOppdatering).isEqualTo(reservertTil.plusHours(2));
    }

    @Test
    public void skalFjerneReservasjonDersomNyEnhet() {
        // arrange
        var behandlingFpsak = lagBehandlingFpsakMedEksisterendeOppgave();

        // act
        new OppdaterOppgaveegenskaperHendelseHåndterer(oppgaveRepository, oppgaveEgenskapHåndterer, køStatistikk, behandlingFpsak).håndter();

        //assert
        var oppgave = DBTestUtil.hentUnik(entityManager, Oppgave.class);
        assertThat(oppgave.getAktiv()).isTrue();
        assertThat(oppgave.harAktivReservasjon()).isFalse();
    }

    @Test
    public void skalKasteFeilDersomIkkeEksistererAktivOppgave() {
        var håndterer = new OppdaterOppgaveegenskaperHendelseHåndterer(oppgaveRepository, oppgaveEgenskapHåndterer, køStatistikk, behandlingFpsak());
        assertThrows(IllegalStateException.class, håndterer::håndter);
    }

    @Test
    public void skalLagreOppgaveStatistikk() {
        var behandlingFpsak = lagBehandlingFpsakMedEksisterendeOppgave();
        new OppdaterOppgaveegenskaperHendelseHåndterer(oppgaveRepository, oppgaveEgenskapHåndterer, køStatistikk, behandlingFpsak).håndter();
        verify(køStatistikk).lagre(any(OppgaveknytningerFørEtterOppdatering.class));
    }

    private BehandlingFpsak lagBehandlingFpsakMedEksisterendeOppgave() {
        var behandlingFpsak = behandlingFpsak();
        var eksisterendeOppgave = Oppgave.builder()
                .dummyOppgave("1111")
                .medAktiv(true)
                .medBehandlingId(behandlingFpsak.getBehandlingId()).build();
        oppgaveRepository.lagre(eksisterendeOppgave);
        reservasjonTjeneste.reserverOppgave(eksisterendeOppgave);
        return behandlingFpsak;
    }


}
