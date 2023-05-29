package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.extensions.JpaExtension;
import no.nav.foreldrepenger.los.DBTestUtil;
import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.håndterere.LukkOppgaveOppgavetransisjonHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveHistorikk;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.oppgave.OppgaveTjeneste;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonTjeneste;
import no.nav.foreldrepenger.los.statistikk.kø.KøOppgaveHendelse;
import no.nav.foreldrepenger.los.statistikk.kø.KøStatistikkTjeneste;


@ExtendWith(MockitoExtension.class)
@ExtendWith(JpaExtension.class)
class LukkOppgaveHendelseHåndtererTest {
    private final KøStatistikkTjeneste køStatistikk = mock(KøStatistikkTjeneste.class);
    private final ReservasjonTjeneste reservasjonTjeneste = mock(ReservasjonTjeneste.class);
    private EntityManager entityManager;
    private OppgaveTjeneste oppgaveTjeneste;
    private LukkOppgaveOppgavetransisjonHåndterer lukkOppgave;

    @BeforeEach
    void setUp(EntityManager entityManager) {
        this.entityManager = entityManager;
        oppgaveTjeneste = new OppgaveTjeneste(new OppgaveRepository(entityManager), reservasjonTjeneste);
        lukkOppgave = new LukkOppgaveOppgavetransisjonHåndterer(oppgaveTjeneste, køStatistikk);
    }

    @Test
    void skalLukkeÅpenOppgave() {
        var behandlingFpsak = OppgaveTestUtil.behandlingFpsak();
        var behandlingId = new BehandlingId(behandlingFpsak.behandlingUuid());
        oppgaveTjeneste.lagre(OppgaveUtil.oppgave(behandlingId, behandlingFpsak));

        lukkOppgave.håndter(behandlingId, behandlingFpsak, new OppgaveHistorikk(List.of()));


        var oppgave = DBTestUtil.hentUnik(entityManager, Oppgave.class);
        assertThat(oppgave.getAktiv()).isFalse();
    }

    @Test
    void skalOppdatereOppgavestatistikk() {
        var behandlingFpsak = OppgaveTestUtil.behandlingFpsak();
        var behandlingId = new BehandlingId(behandlingFpsak.behandlingUuid());
        oppgaveTjeneste.lagre(OppgaveUtil.oppgave(behandlingId, behandlingFpsak));

        lukkOppgave.håndter(behandlingId, behandlingFpsak, new OppgaveHistorikk(List.of()));

        // NB tester ikke rekkefølge på kall
        verify(køStatistikk).lagre(any(BehandlingId.class), eq(KøOppgaveHendelse.LUKKET_OPPGAVE));
    }

}
