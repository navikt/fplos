package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.håndterere;

import no.nav.foreldrepenger.dbstøtte.DBTestUtil;
import no.nav.foreldrepenger.extensions.JpaExtension;

import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.OppgaveEgenskapHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.OppgaveTestUtil;
import no.nav.foreldrepenger.los.klient.fpsak.BehandlingFpsak;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveEgenskap;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.oppgave.OppgaveTjeneste;
import no.nav.foreldrepenger.los.reservasjon.Reservasjon;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonRepository;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonTjeneste;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityManager;

import java.time.LocalDateTime;

import static no.nav.foreldrepenger.dbstøtte.DBTestUtil.hentAlle;
import static no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.OppgaveUtil.oppgave;
import static no.nav.foreldrepenger.los.oppgave.util.OppgaveAssert.assertThatOppgave;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
@ExtendWith(JpaExtension.class)
class OppdaterOppgaveHendelseHåndtererTest {

    private EntityManager entityManager;
    private ReservasjonTjeneste reservasjonTjeneste;
    private BehandlingFpsak.Builder behandlingFpsakBuilder = OppgaveTestUtil.behandlingFpsakBuilder().medErBerørtBehandling(true);
    private BehandlingFpsak behandlingFpsak = behandlingFpsakBuilder.build();
    private OppgaveTjeneste oppgaveTjeneste;
    private OppgaveEgenskapHåndterer oppgaveEgenskapHåndterer;
    private OppdaterOppgaveOppgavetransisjonHåndterer oppgaveOppdaterer;

    @BeforeEach
    private void setUp(EntityManager entityManager) {
        this.entityManager = entityManager;
        var oppgaveRepository = new OppgaveRepository(entityManager);
        reservasjonTjeneste = new ReservasjonTjeneste(oppgaveRepository, new ReservasjonRepository(entityManager));
        oppgaveTjeneste = new OppgaveTjeneste(oppgaveRepository, reservasjonTjeneste);
        oppgaveEgenskapHåndterer = new OppgaveEgenskapHåndterer(oppgaveRepository);
        oppgaveRepository.lagre(oppgave(behandlingFpsak));
        oppgaveOppdaterer = new OppdaterOppgaveOppgavetransisjonHåndterer(oppgaveTjeneste, reservasjonTjeneste, oppgaveEgenskapHåndterer);
    }

    @Test
    public void skalErstatteGammelOppgaveMedNy() {
        oppgaveOppdaterer.håndter(behandlingFpsak);
        var oppgaver = hentAlle(entityManager, Oppgave.class);
        assertThat(oppgaver.stream().count()).isEqualTo(2);
        var gammelOppgave = oppgaver.get(0);
        var nyOppgave = oppgaver.get(1);
        assertThatOppgave(gammelOppgave).harAktiv(false);
        assertThatOppgave(nyOppgave).harAktiv(true);
        assertThatOppgave(nyOppgave).harBehandlingId(gammelOppgave.getBehandlingId());
    }

    @Test
    public void gammelReservasjonVidereføresPåNyOppgave() {
        reservasjonTjeneste.reserverOppgave(DBTestUtil.hentUnik(entityManager, Oppgave.class));
        oppgaveOppdaterer.håndter(behandlingFpsak);
        oppgaveOppdaterer.håndter(behandlingFpsak);
        var oppgaver = hentAlle(entityManager, Oppgave.class);
        assertThatOppgave(oppgaver.get(0)).harAktiv(false);
        assertThatOppgave(oppgaver.get(1)).harAktiv(false);
        assertThatOppgave(oppgaver.get(2)).harAktiv(true);
        var gammelReservasjon = oppgaver.get(0).getReservasjon();
        assertThat(gammelReservasjon.getReservertTil()).isBefore(LocalDateTime.now());
        var nyReservasjon = oppgaver.get(2).getReservasjon();
        assertThat(nyReservasjon.getReservertTil()).isAfter(LocalDateTime.now());
    }

    @Test
    public void skalIkkeFlytteInaktivReservasjon() {
        reservasjonTjeneste.reserverOppgave(DBTestUtil.hentUnik(entityManager, Oppgave.class));
        reservasjonTjeneste.slettReservasjonMedEventLogg(DBTestUtil.hentUnik(entityManager, Reservasjon.class), "slettet");
        oppgaveOppdaterer.håndter(behandlingFpsak);
        var oppgaver = hentAlle(entityManager, Oppgave.class);
        var gammelOppgave = oppgaver.get(0);
        var nyOppgave = oppgaver.get(1);
        assertThat(gammelOppgave.getReservasjon()).isNotNull();
        assertThat(nyOppgave.getReservasjon()).isNull();
    }

    @Test
    public void skalIkkeFlytteReservasjonVedNyEnhet() {
        var nyEnhetBehandlingFpsak = behandlingFpsakBuilder.medBehandlendeEnhetId("0000").build();
        assertThat(nyEnhetBehandlingFpsak.getBehandlendeEnhetId()).isNotEqualTo(behandlingFpsak.getBehandlendeEnhetId());

        reservasjonTjeneste.reserverOppgave(DBTestUtil.hentUnik(entityManager, Oppgave.class));
        oppgaveOppdaterer.håndter(nyEnhetBehandlingFpsak);

        var oppgaver = hentAlle(entityManager, Oppgave.class);
        assertThat(oppgaver.get(0).getReservasjon().erAktiv()).isFalse();
        assertThat(oppgaver.get(1).getReservasjon()).isNull();
    }

    @Test
    public void oppgaveSkalFåOppgaveegenskaperSatt() {
        oppgaveOppdaterer.håndter(behandlingFpsak);
        var oppgave = hentAlle(entityManager, Oppgave.class).get(1);
        var oe = hentAlle(entityManager, OppgaveEgenskap.class);
        assertThat(oppgave.getOppgaveEgenskaper().isEmpty()).isFalse();
    }

}