package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak;

import static no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.OppgaveTestUtil.behandlingFpsakBuilder;
import static no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.OppgaveUtil.oppgave;
import static no.nav.foreldrepenger.los.oppgave.util.OppgaveAssert.assertThatOppgave;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.mock;

import javax.persistence.EntityManager;

import no.nav.foreldrepenger.los.felles.BaseEntitet;
import no.nav.foreldrepenger.los.oppgave.OppgaveTjeneste;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonRepository;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonTjeneste;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.los.DBTestUtil;
import no.nav.foreldrepenger.extensions.JpaExtension;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.håndterere.GjenåpneOppgaveOppgavetransisjonHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventLogg;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventType;
import no.nav.foreldrepenger.los.klient.fpsak.BehandlingFpsak;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.statistikk.kø.KøStatistikkTjeneste;

import java.time.LocalDateTime;
import java.util.Comparator;


@ExtendWith(MockitoExtension.class)
@ExtendWith(JpaExtension.class)
class GjenåpneOppgaveHendelseHåndtererTest {
    private final KøStatistikkTjeneste køStatistikk = mock(KøStatistikkTjeneste.class);
    private EntityManager entityManager;
    private OppgaveRepository oppgaveRepository;
    private ReservasjonTjeneste reservasjonTjeneste;
    private BehandlingFpsak.Builder behandlingFpsak;
    private Oppgave kopiAvEksisterendeOppgave;
    private Oppgave eksisterendeOppgave;
    private OppgaveTjeneste oppgaveTjeneste;
    private GjenåpneOppgaveOppgavetransisjonHåndterer gjenåpneOppgaveHåndterer;

    @BeforeEach
    private void setUp(EntityManager entityManager) {
        this.entityManager = entityManager;
        oppgaveRepository = new OppgaveRepository(entityManager);
        reservasjonTjeneste = new ReservasjonTjeneste(oppgaveRepository, new ReservasjonRepository(entityManager));
        oppgaveTjeneste = new OppgaveTjeneste(oppgaveRepository, reservasjonTjeneste);

        behandlingFpsak = behandlingFpsakBuilder();
        eksisterendeOppgave = oppgave(behandlingFpsak.build());
        kopiAvEksisterendeOppgave = oppgave(behandlingFpsak.build());
        oppgaveRepository.lagre(eksisterendeOppgave);

        reservasjonTjeneste.reserverOppgave(eksisterendeOppgave);
        gjenåpneOppgaveHåndterer = new GjenåpneOppgaveOppgavetransisjonHåndterer(oppgaveRepository, køStatistikk, reservasjonTjeneste);
    }

    @Test
    public void skalVidereføreNyligUtløptReservasjon() {
        // arrange
        reservasjonTjeneste.reserverOppgave(eksisterendeOppgave);
        var behandling = behandlingFpsak.build();
        oppgaveTjeneste.avsluttOppgaveUtenEventLoggAvsluttTilknyttetReservasjon(behandling.getBehandlingId());

        // act
        gjenåpneOppgaveHåndterer.håndter(behandling);

        // assert
        var oppgaver = DBTestUtil.hentAlle(entityManager, Oppgave.class);
        var oppgave = oppgaver.get(1);
        entityManager.refresh(oppgave);
        assertThat(oppgave.getReservasjon()).isNotNull();
        assertThat(oppgave.getReservasjon().getReservertTil()).isAfter(LocalDateTime.now());
    }

    @Test
    public void skalIkkeVidereReservasjonVedNyEnhet() {
        // arrange
        reservasjonTjeneste.reserverOppgave(eksisterendeOppgave);
        eksisterendeOppgave.setAktiv(true);
        oppgaveRepository.lagre(eksisterendeOppgave);
        var opprinneligBehandlingFpsak = behandlingFpsak.build();
        new OppgaveTjeneste(oppgaveRepository, reservasjonTjeneste).avsluttOppgaveUtenEventLoggAvsluttTilknyttetReservasjon(opprinneligBehandlingFpsak.getBehandlingId());
        var nyEnhetBehandlingFpsak = behandlingFpsak.medBehandlendeEnhetId("1000").build();

        // act
        gjenåpneOppgaveHåndterer.håndter(nyEnhetBehandlingFpsak);

        // assert
        var oppgaver = DBTestUtil.hentAlle(entityManager, Oppgave.class);
        var oppgave = oppgaver.get(1);
        assertThat(oppgave.getReservasjon()).isNull();
    }

    @Test
    public void skalOppretteNyOppgave() {
        var behandlingFpsakBygget = behandlingFpsak.build();
        oppgaveTjeneste.avsluttOppgaveUtenEventLoggAvsluttTilknyttetReservasjon(behandlingFpsakBygget.getBehandlingId());

        gjenåpneOppgaveHåndterer.håndter(behandlingFpsakBygget);

        var oppgave = DBTestUtil.hentAlle(entityManager, Oppgave.class);
        var sisteOppgave = oppgave.stream().max(Comparator.comparing(BaseEntitet::getOpprettetTidspunkt))
                .orElseGet(() -> fail("Fant ikke oppgave"));
        assertThatOppgave(sisteOppgave)
                .harAktiv(true)
                .harBehandlingId(behandlingFpsakBygget.getBehandlingId())
                .harBehandlendeEnhet(kopiAvEksisterendeOppgave.getBehandlendeEnhet());
    }

    @Test
    public void skalKasteExceptionVedEksisterendeOppgave() {
        assertThatThrownBy(() -> gjenåpneOppgaveHåndterer.håndter(behandlingFpsak.build()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageStartingWith("Fant eksisterende oppgave");
    }

    @Test
    public void skalOppretteOppgaveEventLogg() {
        var behandlingFpsakBygget = behandlingFpsak.build();
        oppgaveTjeneste.avsluttOppgaveUtenEventLoggAvsluttTilknyttetReservasjon(behandlingFpsakBygget.getBehandlingId());

        gjenåpneOppgaveHåndterer.håndter(behandlingFpsakBygget);

        var oel = DBTestUtil.hentUnik(entityManager, OppgaveEventLogg.class);
        assertThat(oel.getBehandlingId()).isEqualTo(behandlingFpsakBygget.getBehandlingId());
        assertThat(oel.getAndreKriterierType()).isNull();
        assertThat(oel.getBehandlendeEnhet()).isEqualTo(behandlingFpsakBygget.getBehandlendeEnhetId());
        assertThat(oel.getEventType()).isEqualTo(OppgaveEventType.GJENAPNET);
    }

}
