package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak;

import static no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.OppgaveTestUtil.behandlingFpsak;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import javax.persistence.EntityManager;

import no.nav.foreldrepenger.los.oppgave.OppgaveTjeneste;
import no.nav.foreldrepenger.los.reservasjon.Reservasjon;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonRepository;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonTjeneste;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.dbstøtte.DBTestUtil;
import no.nav.foreldrepenger.extensions.JpaExtension;
import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.OppgaveEgenskapHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.håndterere.ReturFraBeslutterHendelseHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventLogg;
import no.nav.foreldrepenger.los.klient.fpsak.BehandlingFpsak;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.statistikk.kø.KøOppgaveHendelse;
import no.nav.foreldrepenger.los.statistikk.kø.KøStatistikkTjeneste;


@ExtendWith(MockitoExtension.class)
@ExtendWith(JpaExtension.class)
class ReturFraBeslutterHendelseHåndtererTest {
    private final KøStatistikkTjeneste køStatistikk = mock(KøStatistikkTjeneste.class);
    private EntityManager entityManager;
    private OppgaveRepository oppgaveRepository;
    private OppgaveEgenskapHåndterer oppgaveEgenskapHåndterer;
    private BehandlingFpsak behandlingFpsak;
    private OppgaveTjeneste oppgaveTjeneste;
    private ReservasjonTjeneste reservasjonTjeneste;

    @BeforeEach
    private void setUp(EntityManager entityManager) {
        this.entityManager = entityManager;
        oppgaveRepository = new OppgaveRepository(entityManager);
        reservasjonTjeneste = new ReservasjonTjeneste(oppgaveRepository, new ReservasjonRepository(entityManager));
        oppgaveTjeneste = new OppgaveTjeneste(oppgaveRepository, reservasjonTjeneste); //mock(ReservasjonTjeneste.class));
        oppgaveEgenskapHåndterer = new OppgaveEgenskapHåndterer(oppgaveRepository);
        behandlingFpsak = behandlingFpsak();
        var eksisterendeOppgave = Oppgave.builder()
                .dummyOppgave("1111")
                .medAktiv(true)
                .medBehandlingId(behandlingFpsak.getBehandlingId())
                .build();
        oppgaveRepository.lagre(eksisterendeOppgave);
    }

    @Test
    public void skalAvslutteBeslutterOppgave() {
        new ReturFraBeslutterHendelseHåndterer(oppgaveTjeneste, oppgaveEgenskapHåndterer, reservasjonTjeneste, køStatistikk, behandlingFpsak).håndter();
        var oppgaver = DBTestUtil.hentAlle(entityManager, Oppgave.class);
        var inaktivOppgave = oppgaver.stream().filter(o -> !o.getAktiv()).findFirst().orElseThrow();
        var aktivOppgave = oppgaver.stream().filter(Oppgave::getAktiv).findFirst().orElseThrow();
        assertThat(oppgaver).hasSize(2);
        assertThat(inaktivOppgave).isNotNull();
        assertThat(aktivOppgave).isNotNull();
    }

    @Test
    public void skalOppretteOppgaveStatistikkForBeggeOppgaver() {
        new ReturFraBeslutterHendelseHåndterer(oppgaveTjeneste, oppgaveEgenskapHåndterer, reservasjonTjeneste, køStatistikk, behandlingFpsak).håndter();
        verify(køStatistikk).lagre(any(BehandlingId.class), eq(KøOppgaveHendelse.LUKKET_OPPGAVE));
        verify(køStatistikk).lagre(any(Oppgave.class), eq(KøOppgaveHendelse.ÅPNET_OPPGAVE));
    }

    @Test
    public void skalOppretteOppgaveEventLoggForBeggeOppgaver() {
        new ReturFraBeslutterHendelseHåndterer(oppgaveTjeneste, oppgaveEgenskapHåndterer, reservasjonTjeneste, køStatistikk, behandlingFpsak).håndter();
        var oel = DBTestUtil.hentAlle(entityManager, OppgaveEventLogg.class);
        assertThat(oel).hasSize(2);
    }

    @Test
    public void skalOppretteReservasjonTilSaksbehandler() {
        new ReturFraBeslutterHendelseHåndterer(oppgaveTjeneste, oppgaveEgenskapHåndterer, reservasjonTjeneste, køStatistikk, behandlingFpsak).håndter();
        var reservasjoner = DBTestUtil.hentAlle(entityManager, Reservasjon.class);
        assertThat(reservasjoner).hasSize(1);

    }
}
