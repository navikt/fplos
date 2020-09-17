package no.nav.fplos.admin;

import no.nav.foreldrepenger.dbstoette.UnittestRepositoryRule;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepository;
import no.nav.vedtak.felles.testutilities.cdi.CdiRunner;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.List;

import static no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType.BERØRT_BEHANDLING;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(CdiRunner.class)
public class OppgaveSynkroniseringTaskOppretterTjenesteTest {
    @Rule
    public UnittestRepositoryRule repoRule = new UnittestRepositoryRule();
    @Inject
    private OppgaveRepository oppgaveRepository;
    @Inject
    private OppgaveSynkroniseringTaskOppretterTjeneste synkroniseringTjeneste;

    @Test
    public void skalOppretteTaskForAktivOppgave() {
        opprettOgLagreOppgave();
        opprettOgVerifiserForventetAntallTasker(1);
    }

    @Test
    public void skalIkkeOppretteTaskForInaktivOppgave() {
        opprettOgLagreOppgave();
        Oppgave oppgave = hentOppgave().get(0);
        oppgave.avsluttOppgave();
        oppgaveRepository.lagre(oppgave);
        opprettOgVerifiserForventetAntallTasker(0);
    }

    @Test
    public void skalOppretteTaskAlleAktiveOppgaver() {
        opprettOgLagreOppgave();
        Oppgave oppgave = hentOppgave().get(0);
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
        return repoRule.getRepository().hentAlle(Oppgave.class);
    }

}
