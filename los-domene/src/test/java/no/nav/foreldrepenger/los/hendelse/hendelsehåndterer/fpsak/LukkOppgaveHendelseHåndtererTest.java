package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak;

import static no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.OppgaveTestUtil.behandlingFpsak;
import static no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.OppgaveUtil.oppgave;
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

import no.nav.foreldrepenger.dbstoette.DBTestUtil;
import no.nav.foreldrepenger.extensions.EntityManagerFPLosAwareExtension;
import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.håndterere.LukkOppgaveHendelseHåndterer;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.statistikk.statistikk_ny.KøOppgaveHendelse;
import no.nav.foreldrepenger.los.statistikk.statistikk_ny.OppgaveStatistikk;


@ExtendWith(MockitoExtension.class)
@ExtendWith(EntityManagerFPLosAwareExtension.class)
class LukkOppgaveHendelseHåndtererTest {
    private final OppgaveStatistikk oppgaveStatistikk = mock(OppgaveStatistikk.class);
    private EntityManager entityManager;
    private OppgaveRepository oppgaveRepository;

    @BeforeEach
    private void setUp(EntityManager entityManager) {
        this.entityManager = entityManager;
        oppgaveRepository = new OppgaveRepository(entityManager);
    }

    @Test
    public void skalLukkeÅpenOppgave() {
        var behandlingFpsak = behandlingFpsak();
        oppgaveRepository.lagre(oppgave(behandlingFpsak));

        new LukkOppgaveHendelseHåndterer(oppgaveRepository, oppgaveStatistikk, behandlingFpsak).håndter();

        var oppgave = DBTestUtil.hentUnik(entityManager, Oppgave.class);
        assertThat(oppgave.getAktiv()).isFalse();
    }

    @Test
    public void skalOppdatereOppgavestatistikk() {
        var behandlingFpsak = behandlingFpsak();
        oppgaveRepository.lagre(oppgave(behandlingFpsak));

        new LukkOppgaveHendelseHåndterer(oppgaveRepository, oppgaveStatistikk, behandlingFpsak).håndter();

        // NB tester ikke rekkefølge på kall
        verify(oppgaveStatistikk).lagre(any(BehandlingId.class), eq(KøOppgaveHendelse.LUKKET_OPPGAVE));
    }

}
