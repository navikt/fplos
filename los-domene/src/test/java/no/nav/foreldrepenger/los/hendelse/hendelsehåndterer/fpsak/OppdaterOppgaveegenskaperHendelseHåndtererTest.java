package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak;

import no.nav.foreldrepenger.dbstoette.DBTestUtil;
import no.nav.foreldrepenger.extensions.EntityManagerFPLosAwareExtension;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.OppgaveEgenskapHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.håndterere.GenerellOpprettOppgaveHendelseHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.håndterere.OppdaterOppgaveegenskaperHendelseHåndterer;
import no.nav.foreldrepenger.los.klient.fpsak.BehandlingFpsak;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepositoryImpl;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonTjenesteImpl;
import no.nav.foreldrepenger.los.statistikk.statistikk_ny.OppgaveStatistikk;
import no.nav.foreldrepenger.los.statistikk.statistikk_ny.OppgaveknytningerFørEtterOppdatering;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityManager;
import java.time.temporal.ChronoUnit;

import static no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.OppgaveTestUtil.behandlingFpsak;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
@ExtendWith(EntityManagerFPLosAwareExtension.class)
class OppdaterOppgaveegenskaperHendelseHåndtererTest {

    private final OppgaveStatistikk oppgaveStatistikk = mock(OppgaveStatistikk.class);
    private EntityManager entityManager;
    private OppgaveRepository oppgaveRepository;
    private OppgaveEgenskapHåndterer oppgaveEgenskapHåndterer;
    private ReservasjonTjenesteImpl reservasjonTjeneste;

    @BeforeEach
    private void setUp(EntityManager entityManager) {
        this.entityManager = entityManager;
        oppgaveRepository = new OppgaveRepositoryImpl(entityManager);
        oppgaveEgenskapHåndterer = new OppgaveEgenskapHåndterer(oppgaveRepository);
        reservasjonTjeneste = new ReservasjonTjenesteImpl(oppgaveRepository);
    }

    @Test
    public void skalVidereføreReservasjonVedOppdateringer() {
        // arrange
        var behandlingFpsak = behandlingFpsak();
        new GenerellOpprettOppgaveHendelseHåndterer(oppgaveRepository, oppgaveEgenskapHåndterer, oppgaveStatistikk, behandlingFpsak).håndter();
        var oppgaveId = DBTestUtil.hentUnik(entityManager, Oppgave.class).getId();
        var reservasjon = reservasjonTjeneste.reserverOppgave(oppgaveId);
        var reservertTil = reservasjon.getReservertTil().truncatedTo(ChronoUnit.SECONDS);

        // act
        new OppdaterOppgaveegenskaperHendelseHåndterer(oppgaveRepository, oppgaveEgenskapHåndterer, oppgaveStatistikk, behandlingFpsak).håndter();

        // assert
        var oppgave = DBTestUtil.hentUnik(entityManager, Oppgave.class);
        assertTrue(oppgave.getAktiv());
        assertTrue(oppgave.harAktivReservasjon());
        var reservertTilEtterOppdatering = reservasjon.getReservertTil().truncatedTo(ChronoUnit.SECONDS);
        assertThat(reservertTilEtterOppdatering).isEqualTo(reservertTil.plusHours(2));
    }

    @Test
    public void skalFjerneReservasjonDersomNyEnhet() {
        // arrange
        var behandlingFpsak = lagBehandlingFpsakMedEksisterendeOppgave();

        // act
        new OppdaterOppgaveegenskaperHendelseHåndterer(oppgaveRepository, oppgaveEgenskapHåndterer, oppgaveStatistikk, behandlingFpsak).håndter();

        //assert
        var oppgave = DBTestUtil.hentUnik(entityManager, Oppgave.class);
        assertTrue(oppgave.getAktiv());
        assertFalse(oppgave.harAktivReservasjon());
    }

    @Test
    public void skalKasteFeilDersomIkkeEksistererAktivOppgave() {
        var håndterer = new OppdaterOppgaveegenskaperHendelseHåndterer(oppgaveRepository, oppgaveEgenskapHåndterer, oppgaveStatistikk, behandlingFpsak());
        assertThrows(IllegalStateException.class, håndterer::håndter);
    }

    @Test
    public void skalLagreOppgaveStatistikk() {
        var behandlingFpsak = lagBehandlingFpsakMedEksisterendeOppgave();
        new OppdaterOppgaveegenskaperHendelseHåndterer(oppgaveRepository, oppgaveEgenskapHåndterer, oppgaveStatistikk, behandlingFpsak).håndter();
        verify(oppgaveStatistikk).lagre(any(OppgaveknytningerFørEtterOppdatering.class));
    }

    private BehandlingFpsak lagBehandlingFpsakMedEksisterendeOppgave() {
        var behandlingFpsak = behandlingFpsak();
        var eksisterendeOppgave = Oppgave.builder()
                .dummyOppgave("1111")
                .medAktiv(true)
                .medBehandlingId(behandlingFpsak.getBehandlingId()).build();
        oppgaveRepository.lagre(eksisterendeOppgave);
        reservasjonTjeneste.reserverOppgave(eksisterendeOppgave.getId());
        return behandlingFpsak;
    }


}
