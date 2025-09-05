package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak;

import static no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.OppgaveTestUtil.behandlingFpsak;
import static org.assertj.core.api.Assertions.assertThat;

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
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.håndterere.ReturFraBeslutterOppgavetransisjonHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventLogg;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveHistorikk;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.oppgave.OppgaveTjeneste;
import no.nav.foreldrepenger.los.reservasjon.Reservasjon;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonRepository;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonTjeneste;
import no.nav.vedtak.hendelser.behandling.los.LosBehandlingDto;


@ExtendWith(MockitoExtension.class)
@ExtendWith(JpaExtension.class)
class ReturFraBeslutterHendelseHåndtererTest {
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
        oppgaveEgenskapHåndterer = new OppgaveEgenskapHåndterer(oppgaveRepository, Mockito.mock(Beskyttelsesbehov.class));
        behandlingFpsak = behandlingFpsak();
        behandlingId = new BehandlingId(behandlingFpsak.behandlingUuid());
        var eksisterendeOppgave = Oppgave.builder().dummyOppgave("1111").medAktiv(true).medBehandlingId(behandlingId).build();
        oppgaveRepository.lagre(eksisterendeOppgave);
        returFraBeslutterHåndterer = new ReturFraBeslutterOppgavetransisjonHåndterer(oppgaveTjeneste, oppgaveEgenskapHåndterer, reservasjonTjeneste);
    }

    @Test
    void skalAvslutteBeslutterOppgave() {
        returFraBeslutterHåndterer.håndter(behandlingId, behandlingFpsak, new OppgaveHistorikk(List.of()));
        var oppgaver = DBTestUtil.hentAlle(entityManager, Oppgave.class);
        var inaktivOppgave = oppgaver.stream().filter(o -> !o.getAktiv()).findFirst().orElseThrow();
        var aktivOppgave = oppgaver.stream().filter(Oppgave::getAktiv).findFirst().orElseThrow();
        assertThat(oppgaver).hasSize(2);
        assertThat(inaktivOppgave).isNotNull();
        assertThat(aktivOppgave).isNotNull();
    }

    @Test
    void skalOppretteOppgaveEventLoggForBeggeOppgaver() {
        returFraBeslutterHåndterer.håndter(behandlingId, behandlingFpsak, new OppgaveHistorikk(List.of()));
        var oel = DBTestUtil.hentAlle(entityManager, OppgaveEventLogg.class);
        assertThat(oel).hasSize(2);
    }

    @Test
    void skalOppretteReservasjonTilSaksbehandler() {
        returFraBeslutterHåndterer.håndter(behandlingId, behandlingFpsak, new OppgaveHistorikk(List.of()));
        var reservasjoner = DBTestUtil.hentAlle(entityManager, Reservasjon.class);
        assertThat(reservasjoner).hasSize(1);

    }
}
