package no.nav.foreldrepenger.los.admin;

import static no.nav.foreldrepenger.los.oppgave.AndreKriterierType.BERØRT_BEHANDLING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import no.nav.foreldrepenger.dbstoette.DBTestUtil;
import no.nav.foreldrepenger.extensions.EntityManagerFPLosAwareExtension;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.vedtak.felles.prosesstask.impl.ProsessTaskEventPubliserer;
import no.nav.vedtak.felles.prosesstask.impl.ProsessTaskRepositoryImpl;

@ExtendWith(EntityManagerFPLosAwareExtension.class)
public class OppgaveSynkroniseringTaskOppretterTjenesteTest {

    private OppgaveRepository oppgaveRepository;
    private OppgaveSynkroniseringTaskOppretterTjeneste synkroniseringTjeneste;
    private EntityManager entityManager;

    @BeforeEach
    void setUp(EntityManager entityManager) {
        this.entityManager = entityManager;
        oppgaveRepository = new OppgaveRepository(entityManager);
        synkroniseringTjeneste = new OppgaveSynkroniseringTaskOppretterTjeneste(oppgaveRepository,
                new ProsessTaskRepositoryImpl(entityManager, () -> "user",
                mock(ProsessTaskEventPubliserer.class)));
    }

    @Test
    public void skalOppretteTaskForAktivOppgave() {
        opprettOgLagreOppgave();
        opprettOgVerifiserForventetAntallTasker(1);
    }

    @Test
    public void skalIkkeOppretteTaskForInaktivOppgave() {
        opprettOgLagreOppgave();
        var oppgave = hentOppgave().get(0);
        oppgave.avsluttOppgave();
        oppgaveRepository.lagre(oppgave);
        opprettOgVerifiserForventetAntallTasker(0);
    }

    @Test
    public void skalOppretteTaskAlleAktiveOppgaver() {
        opprettOgLagreOppgave();
        var oppgave = hentOppgave().get(0);
        oppgave.avsluttOppgave();
        oppgaveRepository.lagre(oppgave);

        opprettOgLagreOppgave();
        opprettOgLagreOppgave();
        opprettOgLagreOppgave();
        opprettOgVerifiserForventetAntallTasker(3);
    }

    private void opprettOgVerifiserForventetAntallTasker(int antallForventet) {
        var antallRapport = synkroniseringTjeneste.opprettOppgaveEgenskapOppdatererTask(BERØRT_BEHANDLING.getKode());
        assertThat(antallRapport).isEqualTo(OppgaveEgenskapOppdatererTask.TASKTYPE + "-" + antallForventet);
    }

    private void opprettOgLagreOppgave() {
        oppgaveRepository.lagre(Oppgave.builder().dummyOppgave("4404").medSystem("FPSAK").build());
    }

    private List<Oppgave> hentOppgave() {
        return DBTestUtil.hentAlle(entityManager, Oppgave.class);
    }

}
