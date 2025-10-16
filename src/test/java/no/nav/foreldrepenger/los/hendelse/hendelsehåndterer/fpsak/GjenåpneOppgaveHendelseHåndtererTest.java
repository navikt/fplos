package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;

import java.time.LocalDateTime;
import java.util.Comparator;
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
import no.nav.foreldrepenger.los.felles.BaseEntitet;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.Beskyttelsesbehov;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.OppgaveEgenskapHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.håndterere.GjenåpneOppgaveOppgavetransisjonHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventLogg;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventType;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveHistorikk;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.oppgave.OppgaveTjeneste;
import no.nav.foreldrepenger.los.oppgave.util.OppgaveAssert;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonRepository;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonTjeneste;
import no.nav.vedtak.hendelser.behandling.los.LosBehandlingDto;


@ExtendWith(MockitoExtension.class)
@ExtendWith(JpaExtension.class)
class GjenåpneOppgaveHendelseHåndtererTest {
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
        oppgaveEgenskapHåndterer = new OppgaveEgenskapHåndterer(Mockito.mock(Beskyttelsesbehov.class));
        reservasjonTjeneste = new ReservasjonTjeneste(oppgaveRepository, new ReservasjonRepository(entityManager));
        oppgaveTjeneste = new OppgaveTjeneste(oppgaveRepository, reservasjonTjeneste);

        behandlingFpsak = OppgaveTestUtil.behandlingFpsak();
        behandlingId = new BehandlingId(behandlingFpsak.behandlingUuid());
        eksisterendeOppgave = OppgaveUtil.oppgave(behandlingId, behandlingFpsak);
        kopiAvEksisterendeOppgave = OppgaveUtil.oppgave(behandlingId, behandlingFpsak);
        oppgaveRepository.lagre(eksisterendeOppgave);

        reservasjonTjeneste.reserverOppgave(eksisterendeOppgave);
        gjenåpneOppgaveHåndterer = new GjenåpneOppgaveOppgavetransisjonHåndterer(oppgaveRepository, oppgaveEgenskapHåndterer, reservasjonTjeneste);
    }

    @Test
    void skalVidereføreNyligUtløptReservasjon() {
        // arrange
        reservasjonTjeneste.reserverOppgave(eksisterendeOppgave);
        var behandling = behandlingFpsak;
        oppgaveTjeneste.avsluttOppgaveUtenEventLoggAvsluttTilknyttetReservasjon(behandlingId);

        // act
        gjenåpneOppgaveHåndterer.håndter(behandlingId, behandling, new OppgaveHistorikk(List.of()));

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
        oppgaveRepository.lagre(eksisterendeOppgave);
        var b = behandlingFpsak;
        new OppgaveTjeneste(oppgaveRepository, reservasjonTjeneste).avsluttOppgaveUtenEventLoggAvsluttTilknyttetReservasjon(behandlingId);
        var nyEnhetBehandlingFpsak = new LosBehandlingDto(b.behandlingUuid(), b.kildesystem(), b.saksnummer(), b.ytelse(), b.aktørId(),
            b.behandlingstype(), b.behandlingsstatus(), b.opprettetTidspunkt(), "1000", b.behandlingsfrist(), b.ansvarligSaksbehandlerIdent(),
            b.aksjonspunkt(), b.behandlingsårsaker(), b.faresignaler(), b.refusjonskrav(), List.of(), b.foreldrepengerDto(), List.of(), b.tilbakeDto());

        // act
        gjenåpneOppgaveHåndterer.håndter(behandlingId, nyEnhetBehandlingFpsak, new OppgaveHistorikk(List.of()));

        // assert
        var oppgaver = DBTestUtil.hentAlle(entityManager, Oppgave.class);
        var oppgave = oppgaver.get(1);
        assertThat(oppgave.getReservasjon()).isNull();
    }

    @Test
    void skalOppretteNyOppgave() {
        var behandlingFpsakBygget = behandlingFpsak;
        oppgaveTjeneste.avsluttOppgaveUtenEventLoggAvsluttTilknyttetReservasjon(behandlingId);

        gjenåpneOppgaveHåndterer.håndter(behandlingId, behandlingFpsakBygget, new OppgaveHistorikk(List.of()));

        var oppgave = DBTestUtil.hentAlle(entityManager, Oppgave.class);
        var sisteOppgave = oppgave.stream().max(Comparator.comparing(BaseEntitet::getOpprettetTidspunkt)).orElseGet(() -> fail("Fant ikke oppgave"));
        OppgaveAssert.assertThatOppgave(sisteOppgave).harAktiv(true)
                     .harBehandlingId(behandlingId)
                     .harBehandlendeEnhet(kopiAvEksisterendeOppgave.getBehandlendeEnhet());
    }

    @Test
    void skalKasteExceptionVedEksisterendeOppgave() {
        assertThatThrownBy(() -> gjenåpneOppgaveHåndterer.håndter(behandlingId, behandlingFpsak, new OppgaveHistorikk(List.of()))).isInstanceOf(IllegalStateException.class)
            .hasMessageStartingWith("Fant eksisterende oppgave");
    }

    @Test
    void skalOppretteOppgaveEventLogg() {
        var behandlingFpsakBygget = behandlingFpsak;
        oppgaveTjeneste.avsluttOppgaveUtenEventLoggAvsluttTilknyttetReservasjon(behandlingId);

        gjenåpneOppgaveHåndterer.håndter(behandlingId, behandlingFpsakBygget, new OppgaveHistorikk(List.of()));

        var oel = DBTestUtil.hentUnik(entityManager, OppgaveEventLogg.class);
        assertThat(oel.getBehandlingId().toUUID()).isEqualTo(behandlingFpsakBygget.behandlingUuid());
        assertThat(oel.getAndreKriterierType()).isNull();
        assertThat(oel.getBehandlendeEnhet()).isEqualTo(behandlingFpsakBygget.behandlendeEnhetId());
        assertThat(oel.getEventType()).isEqualTo(OppgaveEventType.GJENAPNET);
    }

}
