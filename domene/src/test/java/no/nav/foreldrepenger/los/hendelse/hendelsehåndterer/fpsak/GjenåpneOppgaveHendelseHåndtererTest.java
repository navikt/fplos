package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak;

import static no.nav.foreldrepenger.los.oppgave.util.OppgaveAssert.assertThatOppgave;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.mock;

import java.time.LocalDateTime;
import java.util.Comparator;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.extensions.JpaExtension;
import no.nav.foreldrepenger.los.DBTestUtil;
import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.felles.BaseEntitet;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.OppgaveEgenskapHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.håndterere.GjenåpneOppgaveOppgavetransisjonHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventLogg;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventType;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.oppgave.OppgaveTjeneste;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonRepository;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonTjeneste;
import no.nav.foreldrepenger.los.statistikk.kø.KøStatistikkTjeneste;
import no.nav.vedtak.hendelser.behandling.los.LosBehandlingDto;


@ExtendWith(MockitoExtension.class)
@ExtendWith(JpaExtension.class)
class GjenåpneOppgaveHendelseHåndtererTest {
    private final KøStatistikkTjeneste køStatistikk = mock(KøStatistikkTjeneste.class);
    private EntityManager entityManager;
    private OppgaveRepository oppgaveRepository;
    private OppgaveEgenskapHåndterer oppgaveEgenskapHåndterer;
    private ReservasjonTjeneste reservasjonTjeneste;
    private LosBehandlingDto behandlingFpsak;
    private BehandlingId behandlingId;
    private Oppgave kopiAvEksisterendeOppgave;
    private Oppgave eksisterendeOppgave;
    private OppgaveTjeneste oppgaveTjeneste;
    private GjenåpneOppgaveOppgavetransisjonHåndterer gjenåpneOppgaveHåndterer;

    @BeforeEach
    void setUp(EntityManager entityManager) {
        this.entityManager = entityManager;
        oppgaveRepository = new OppgaveRepository(entityManager);
        oppgaveEgenskapHåndterer = new OppgaveEgenskapHåndterer(oppgaveRepository);
        reservasjonTjeneste = new ReservasjonTjeneste(oppgaveRepository, new ReservasjonRepository(entityManager));
        oppgaveTjeneste = new OppgaveTjeneste(oppgaveRepository, reservasjonTjeneste);

        behandlingFpsak = OppgaveTestUtil.behandlingFpsak();
        behandlingId = new BehandlingId(behandlingFpsak.behandlingUuid());
        eksisterendeOppgave = OppgaveUtil.oppgave(behandlingId, behandlingFpsak);
        kopiAvEksisterendeOppgave = OppgaveUtil.oppgave(behandlingId, behandlingFpsak);
        oppgaveRepository.lagre(eksisterendeOppgave);

        reservasjonTjeneste.reserverOppgave(eksisterendeOppgave);
        gjenåpneOppgaveHåndterer = new GjenåpneOppgaveOppgavetransisjonHåndterer(oppgaveRepository, oppgaveEgenskapHåndterer, køStatistikk, reservasjonTjeneste);
    }

    @Test
    void skalVidereføreNyligUtløptReservasjon() {
        // arrange
        reservasjonTjeneste.reserverOppgave(eksisterendeOppgave);
        var behandling = behandlingFpsak;
        oppgaveTjeneste.avsluttOppgaveUtenEventLoggAvsluttTilknyttetReservasjon(behandlingId);

        // act
        gjenåpneOppgaveHåndterer.håndter(behandlingId, behandling);

        // assert
        var oppgaver = DBTestUtil.hentAlle(entityManager, Oppgave.class);
        var oppgave = oppgaver.get(1);
        entityManager.refresh(oppgave);
        assertThat(oppgave.getReservasjon()).isNotNull();
        assertThat(oppgave.getReservasjon().getReservertTil()).isAfter(LocalDateTime.now());
    }

    @Test
    void skalIkkeVidereReservasjonVedNyEnhet() {
        // arrange
        reservasjonTjeneste.reserverOppgave(eksisterendeOppgave);
        eksisterendeOppgave.setAktiv(true);
        oppgaveRepository.lagre(eksisterendeOppgave);
        var b = behandlingFpsak;
        new OppgaveTjeneste(oppgaveRepository, reservasjonTjeneste).avsluttOppgaveUtenEventLoggAvsluttTilknyttetReservasjon(behandlingId);
        var nyEnhetBehandlingFpsak = new LosBehandlingDto(b.behandlingUuid(), b.kildesystem(), b.saksnummer(), b.ytelse(),
                b.aktørId(), b.behandlingstype(), b.behandlingsstatus(), b.opprettetTidspunkt(), "1000",
                b.behandlingsfrist(), b.ansvarligSaksbehandlerIdent(), b.aksjonspunkt(), b.behandlingsårsaker(),
                b.faresignaler(), b.refusjonskrav(), b.foreldrepengerDto(), b.tilbakeDto());

        // act
        gjenåpneOppgaveHåndterer.håndter(behandlingId, nyEnhetBehandlingFpsak);

        // assert
        var oppgaver = DBTestUtil.hentAlle(entityManager, Oppgave.class);
        var oppgave = oppgaver.get(1);
        assertThat(oppgave.getReservasjon()).isNull();
    }

    @Test
    void skalOppretteNyOppgave() {
        var behandlingFpsakBygget = behandlingFpsak;
        oppgaveTjeneste.avsluttOppgaveUtenEventLoggAvsluttTilknyttetReservasjon(behandlingId);

        gjenåpneOppgaveHåndterer.håndter(behandlingId, behandlingFpsakBygget);

        var oppgave = DBTestUtil.hentAlle(entityManager, Oppgave.class);
        var sisteOppgave = oppgave.stream().max(Comparator.comparing(BaseEntitet::getOpprettetTidspunkt))
                .orElseGet(() -> fail("Fant ikke oppgave"));
        assertThatOppgave(sisteOppgave)
                .harAktiv(true)
                .harBehandlingId(behandlingId)
                .harBehandlendeEnhet(kopiAvEksisterendeOppgave.getBehandlendeEnhet());
    }

    @Test
    void skalKasteExceptionVedEksisterendeOppgave() {
        assertThatThrownBy(() -> gjenåpneOppgaveHåndterer.håndter(behandlingId, behandlingFpsak))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageStartingWith("Fant eksisterende oppgave");
    }

    @Test
    void skalOppretteOppgaveEventLogg() {
        var behandlingFpsakBygget = behandlingFpsak;
        oppgaveTjeneste.avsluttOppgaveUtenEventLoggAvsluttTilknyttetReservasjon(behandlingId);

        gjenåpneOppgaveHåndterer.håndter(behandlingId, behandlingFpsakBygget);

        var oel = DBTestUtil.hentUnik(entityManager, OppgaveEventLogg.class);
        assertThat(oel.getBehandlingId().toUUID()).isEqualTo(behandlingFpsakBygget.behandlingUuid());
        assertThat(oel.getAndreKriterierType()).isNull();
        assertThat(oel.getBehandlendeEnhet()).isEqualTo(behandlingFpsakBygget.behandlendeEnhetId());
        assertThat(oel.getEventType()).isEqualTo(OppgaveEventType.GJENAPNET);
    }

}
