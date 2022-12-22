package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak;

import static no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.OppgaveTestUtil.behandlingFpsak;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.extensions.JpaExtension;
import no.nav.foreldrepenger.los.DBTestUtil;
import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.OppgaveEgenskapHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.håndterere.ReturFraBeslutterOppgavetransisjonHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventLogg;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.oppgave.OppgaveTjeneste;
import no.nav.foreldrepenger.los.reservasjon.Reservasjon;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonRepository;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonTjeneste;
import no.nav.foreldrepenger.los.statistikk.kø.KøOppgaveHendelse;
import no.nav.foreldrepenger.los.statistikk.kø.KøStatistikkTjeneste;
import no.nav.vedtak.hendelser.behandling.los.LosBehandlingDto;


@ExtendWith(MockitoExtension.class)
@ExtendWith(JpaExtension.class)
class ReturFraBeslutterHendelseHåndtererTest {
    private final KøStatistikkTjeneste køStatistikk = mock(KøStatistikkTjeneste.class);
    private EntityManager entityManager;
    private OppgaveRepository oppgaveRepository;
    private OppgaveEgenskapHåndterer oppgaveEgenskapHåndterer;
    private LosBehandlingDto behandlingFpsak;
    private BehandlingId behandlingId;
    private OppgaveTjeneste oppgaveTjeneste;
    private ReservasjonTjeneste reservasjonTjeneste;
    private ReturFraBeslutterOppgavetransisjonHåndterer returFraBeslutterHåndterer;

    @BeforeEach
    void setUp(EntityManager entityManager) {
        this.entityManager = entityManager;
        oppgaveRepository = new OppgaveRepository(entityManager);
        reservasjonTjeneste = new ReservasjonTjeneste(oppgaveRepository, new ReservasjonRepository(entityManager));
        oppgaveTjeneste = new OppgaveTjeneste(oppgaveRepository, reservasjonTjeneste); //mock(ReservasjonTjeneste.class));
        oppgaveEgenskapHåndterer = new OppgaveEgenskapHåndterer(oppgaveRepository);
        behandlingFpsak = behandlingFpsak();
        behandlingId = new BehandlingId(behandlingFpsak.behandlingUuid());
        var eksisterendeOppgave = Oppgave.builder()
                .dummyOppgave("1111")
                .medAktiv(true)
                .medBehandlingId(behandlingId)
                .build();
        oppgaveRepository.lagre(eksisterendeOppgave);
        returFraBeslutterHåndterer = new ReturFraBeslutterOppgavetransisjonHåndterer(oppgaveTjeneste, oppgaveEgenskapHåndterer, reservasjonTjeneste, køStatistikk);
    }

    @Test
    public void skalAvslutteBeslutterOppgave() {
        returFraBeslutterHåndterer.håndter(behandlingId, behandlingFpsak);
        var oppgaver = DBTestUtil.hentAlle(entityManager, Oppgave.class);
        var inaktivOppgave = oppgaver.stream().filter(o -> !o.getAktiv()).findFirst().orElseThrow();
        var aktivOppgave = oppgaver.stream().filter(Oppgave::getAktiv).findFirst().orElseThrow();
        assertThat(oppgaver).hasSize(2);
        assertThat(inaktivOppgave).isNotNull();
        assertThat(aktivOppgave).isNotNull();
    }

    @Test
    public void skalOppretteOppgaveStatistikkForBeggeOppgaver() {
        returFraBeslutterHåndterer.håndter(behandlingId, behandlingFpsak);
        verify(køStatistikk).lagre(any(BehandlingId.class), eq(KøOppgaveHendelse.LUKKET_OPPGAVE));
        verify(køStatistikk).lagre(any(Oppgave.class), eq(KøOppgaveHendelse.ÅPNET_OPPGAVE));
    }

    @Test
    public void skalOppretteOppgaveEventLoggForBeggeOppgaver() {
        returFraBeslutterHåndterer.håndter(behandlingId, behandlingFpsak);
        var oel = DBTestUtil.hentAlle(entityManager, OppgaveEventLogg.class);
        assertThat(oel).hasSize(2);
    }

    @Test
    public void skalOppretteReservasjonTilSaksbehandler() {
        returFraBeslutterHåndterer.håndter(behandlingId, behandlingFpsak);
        var reservasjoner = DBTestUtil.hentAlle(entityManager, Reservasjon.class);
        assertThat(reservasjoner).hasSize(1);

    }
}
