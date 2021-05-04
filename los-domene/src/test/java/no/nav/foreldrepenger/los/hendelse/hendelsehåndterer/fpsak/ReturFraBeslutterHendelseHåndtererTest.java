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

import no.nav.foreldrepenger.dbstøtte.DBTestUtil;
import no.nav.foreldrepenger.extensions.EntityManagerFPLosAwareExtension;
import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.OppgaveEgenskapHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.håndterere.ReturFraBeslutterHendelseHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventLogg;
import no.nav.foreldrepenger.los.klient.fpsak.BehandlingFpsak;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.statistikk.statistikk_ny.KøOppgaveHendelse;
import no.nav.foreldrepenger.los.statistikk.statistikk_ny.OppgaveStatistikk;


@ExtendWith(MockitoExtension.class)
@ExtendWith(EntityManagerFPLosAwareExtension.class)
class ReturFraBeslutterHendelseHåndtererTest {
    private final OppgaveStatistikk oppgaveStatistikk = mock(OppgaveStatistikk.class);
    private EntityManager entityManager;
    private OppgaveRepository oppgaveRepository;
    private OppgaveEgenskapHåndterer oppgaveEgenskapHåndterer;
    private BehandlingFpsak behandlingFpsak;

    @BeforeEach
    private void setUp(EntityManager entityManager) {
        this.entityManager = entityManager;
        oppgaveRepository = new OppgaveRepository(entityManager);
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
        new ReturFraBeslutterHendelseHåndterer(oppgaveRepository, oppgaveEgenskapHåndterer, oppgaveStatistikk, behandlingFpsak).håndter();
        var oppgaver = DBTestUtil.hentAlle(entityManager, Oppgave.class);
        var inaktivOppgave = oppgaver.stream().filter(o -> !o.getAktiv()).findFirst().orElseThrow();
        var aktivOppgave = oppgaver.stream().filter(Oppgave::getAktiv).findFirst().orElseThrow();
        assertThat(oppgaver).hasSize(2);
        assertThat(inaktivOppgave).isNotNull();
        assertThat(aktivOppgave).isNotNull();
    }

    @Test
    public void skalOppretteOppgaveStatistikkForBeggeOppgaver() {
        new ReturFraBeslutterHendelseHåndterer(oppgaveRepository, oppgaveEgenskapHåndterer, oppgaveStatistikk, behandlingFpsak).håndter();
        verify(oppgaveStatistikk).lagre(any(BehandlingId.class), eq(KøOppgaveHendelse.LUKKET_OPPGAVE));
        verify(oppgaveStatistikk).lagre(any(Oppgave.class), eq(KøOppgaveHendelse.ÅPNET_OPPGAVE));
    }

    @Test
    public void skalOppretteOppgaveEventLoggForBeggeOppgaver() {
        new ReturFraBeslutterHendelseHåndterer(oppgaveRepository, oppgaveEgenskapHåndterer, oppgaveStatistikk, behandlingFpsak).håndter();
        var oel = DBTestUtil.hentAlle(entityManager, OppgaveEventLogg.class);
        assertThat(oel).hasSize(2);
    }
}
