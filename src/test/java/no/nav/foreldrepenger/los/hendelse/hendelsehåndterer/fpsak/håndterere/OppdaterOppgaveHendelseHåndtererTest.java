package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.håndterere;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityManager;
import no.nav.foreldrepenger.los.DBTestUtil;
import no.nav.foreldrepenger.los.JpaExtension;
import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.Beskyttelsesbehov;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.OppgaveEgenskapHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.OppgaveTestUtil;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.OppgaveUtil;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveHistorikk;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveEgenskap;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.oppgave.OppgaveTjeneste;
import no.nav.foreldrepenger.los.oppgave.util.OppgaveAssert;
import no.nav.foreldrepenger.los.reservasjon.Reservasjon;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonRepository;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonTjeneste;
import no.nav.vedtak.hendelser.behandling.los.LosBehandlingDto;

@ExtendWith(MockitoExtension.class)
@ExtendWith(JpaExtension.class)
class OppdaterOppgaveHendelseHåndtererTest {

    private EntityManager entityManager;
    private ReservasjonTjeneste reservasjonTjeneste;
    private LosBehandlingDto behandlingFpsak = OppgaveTestUtil.behandlingFpsak(true);
    private BehandlingId behandlingId;
    private OppgaveTjeneste oppgaveTjeneste;
    private OppgaveEgenskapHåndterer oppgaveEgenskapHåndterer;
    private OppdaterOppgaveOppgavetransisjonHåndterer oppgaveOppdaterer;

    @BeforeEach
    void setUp(EntityManager entityManager) {
        this.entityManager = entityManager;
        var oppgaveRepository = new OppgaveRepository(entityManager);
        reservasjonTjeneste = new ReservasjonTjeneste(oppgaveRepository, new ReservasjonRepository(entityManager));
        oppgaveTjeneste = new OppgaveTjeneste(oppgaveRepository, reservasjonTjeneste);
        oppgaveEgenskapHåndterer = new OppgaveEgenskapHåndterer(oppgaveRepository, Mockito.mock(Beskyttelsesbehov.class));
        behandlingId = new BehandlingId(behandlingFpsak.behandlingUuid());
        oppgaveRepository.lagre(OppgaveUtil.oppgave(behandlingId, behandlingFpsak));
        oppgaveOppdaterer = new OppdaterOppgaveOppgavetransisjonHåndterer(oppgaveTjeneste, reservasjonTjeneste, oppgaveEgenskapHåndterer);
    }

    @Test
    void skalErstatteGammelOppgaveMedNy() {
        oppgaveOppdaterer.håndter(behandlingId, behandlingFpsak, new OppgaveHistorikk(List.of()));
        var oppgaver = DBTestUtil.hentAlle(entityManager, Oppgave.class);
        assertThat(oppgaver).hasSize(2);
        var gammelOppgave = oppgaver.get(0);
        var nyOppgave = oppgaver.get(1);
        OppgaveAssert.assertThatOppgave(gammelOppgave).harAktiv(false);
        OppgaveAssert.assertThatOppgave(nyOppgave).harAktiv(true);
        OppgaveAssert.assertThatOppgave(nyOppgave).harBehandlingId(gammelOppgave.getBehandlingId());
    }

    @Test
    void gammelReservasjonVidereføresPåNyOppgave() {
        reservasjonTjeneste.reserverOppgave(DBTestUtil.hentUnik(entityManager, Oppgave.class));
        oppgaveOppdaterer.håndter(behandlingId, behandlingFpsak, new OppgaveHistorikk(List.of()));
        oppgaveOppdaterer.håndter(behandlingId, behandlingFpsak, new OppgaveHistorikk(List.of()));
        var oppgaver = DBTestUtil.hentAlle(entityManager, Oppgave.class);
        OppgaveAssert.assertThatOppgave(oppgaver.get(0)).harAktiv(false);
        OppgaveAssert.assertThatOppgave(oppgaver.get(1)).harAktiv(false);
        OppgaveAssert.assertThatOppgave(oppgaver.get(2)).harAktiv(true);
        var gammelReservasjon = oppgaver.get(0).getReservasjon();
        assertThat(gammelReservasjon.getReservertTil()).isBefore(LocalDateTime.now());
        var nyReservasjon = oppgaver.get(2).getReservasjon();
        assertThat(nyReservasjon.getReservertTil()).isAfter(LocalDateTime.now());
    }

    @Test
    void skalIkkeFlytteInaktivReservasjon() {
        reservasjonTjeneste.reserverOppgave(DBTestUtil.hentUnik(entityManager, Oppgave.class));
        reservasjonTjeneste.slettReservasjon(DBTestUtil.hentUnik(entityManager, Reservasjon.class));
        oppgaveOppdaterer.håndter(behandlingId, behandlingFpsak, new OppgaveHistorikk(List.of()));
        var oppgaver = DBTestUtil.hentAlle(entityManager, Oppgave.class);
        var gammelOppgave = oppgaver.get(0);
        var nyOppgave = oppgaver.get(1);
        assertThat(gammelOppgave.getReservasjon()).isNotNull();
        assertThat(nyOppgave.getReservasjon()).isNull();
    }


    @Test
    void oppgaveSkalFåOppgaveegenskaperSatt() {
        oppgaveOppdaterer.håndter(behandlingId, behandlingFpsak, new OppgaveHistorikk(List.of()));
        var oppgave = DBTestUtil.hentAlle(entityManager, Oppgave.class).get(1);
        var oe = DBTestUtil.hentAlle(entityManager, OppgaveEgenskap.class);
        assertThat(oppgave.getOppgaveEgenskaper()).isNotEmpty();
    }

}
