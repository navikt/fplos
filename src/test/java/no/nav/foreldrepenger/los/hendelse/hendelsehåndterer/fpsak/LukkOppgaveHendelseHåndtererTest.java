package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak;

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
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.håndterere.LukkOppgaveOppgavetransisjonHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveHistorikk;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.oppgave.OppgaveTjeneste;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonTjeneste;


@ExtendWith(MockitoExtension.class)
@ExtendWith(JpaExtension.class)
class LukkOppgaveHendelseHåndtererTest {
    private final ReservasjonTjeneste reservasjonTjeneste = Mockito.mock(ReservasjonTjeneste.class);
    private EntityManager entityManager;
    private OppgaveTjeneste oppgaveTjeneste;
    private LukkOppgaveOppgavetransisjonHåndterer lukkOppgave;

    @BeforeEach
    void setUp(EntityManager entityManager) {
        this.entityManager = entityManager;
        oppgaveTjeneste = new OppgaveTjeneste(new OppgaveRepository(entityManager), reservasjonTjeneste);
        lukkOppgave = new LukkOppgaveOppgavetransisjonHåndterer(oppgaveTjeneste);
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

}
