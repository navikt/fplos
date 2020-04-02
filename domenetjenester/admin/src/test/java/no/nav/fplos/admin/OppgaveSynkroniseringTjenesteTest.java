package no.nav.fplos.admin;

import no.nav.foreldrepenger.dbstoette.UnittestRepositoryRule;
import no.nav.foreldrepenger.loslager.BehandlingId;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEgenskap;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepository;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepositoryImpl;
import no.nav.fplos.foreldrepengerbehandling.BehandlingFpsak;
import no.nav.fplos.foreldrepengerbehandling.ForeldrepengerBehandlingRestKlient;
import org.junit.Rule;
import org.junit.Test;

import javax.persistence.EntityManager;

import java.util.List;

import static no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OppgaveSynkroniseringTjenesteTest {

    @Rule
    public UnittestRepositoryRule repoRule = new UnittestRepositoryRule();
    private EntityManager entityManager = repoRule.getEntityManager();
    private OppgaveRepository oppgaveRepository = new OppgaveRepositoryImpl(entityManager);
    private ForeldrepengerBehandlingRestKlient fpsakKlient = mock(ForeldrepengerBehandlingRestKlient.class);
    private OppgaveSynkroniseringTjeneste synkroniseringTjeneste = new OppgaveSynkroniseringTjeneste(fpsakKlient, oppgaveRepository);

    @Test
    public void skalOppretteBerørtBehandlingEgenskap() {
        mockErBerørtBehandling();
        opprettOgLagreOppgave();
        synkroniseringTjeneste.leggTilBerørtBehandlingEgenskap();
        verifiserAktiv();
    }

    @Test
    public void skalReaktivereEksisterendeEgenskap() {
        Oppgave oppgave = oppgave();
        OppgaveEgenskap egenskap = new OppgaveEgenskap(oppgave, BERØRT_BEHANDLING);
        egenskap.deaktiverOppgaveEgenskap();
        oppgaveRepository.lagre(oppgave);
        oppgaveRepository.lagre(egenskap);
        verifiserInaktiv();

        mockErBerørtBehandling();
        synkroniseringTjeneste.leggTilBerørtBehandlingEgenskap();
        verifiserAktiv();
    }

    @Test
    public void inaktiveOppgaverSkalIkkeSynkroniseres() {
        // Det er ikke behov for å synke inaktive oppgaver.
        Oppgave oppgave = oppgave();
        oppgave.deaktiverOppgave();
        oppgaveRepository.lagre(oppgave);
        
        mockErBerørtBehandling();
        synkroniseringTjeneste.leggTilBerørtBehandlingEgenskap();
        assertThat(repoRule.getRepository().hentAlle(OppgaveEgenskap.class)).isEmpty();
    }

    private void verifiserAktiv() {
        verifiserEgenskap(true);
    }

    private void verifiserInaktiv() {
        verifiserEgenskap(false);
    }

    private void verifiserEgenskap(boolean aktiv) {
        List<OppgaveEgenskap> oppgaveEgenskaper = repoRule.getRepository().hentAlle(OppgaveEgenskap.class);
        assertThat(oppgaveEgenskaper.size()).isEqualTo(1);
        assertThat(oppgaveEgenskaper.get(0).getAktiv()).isEqualTo(aktiv);
    }

    private void mockErBerørtBehandling() {
        mockErBerørtBehandling(true);
    }

    private void mockErBerørtBehandling(boolean erBerørt) {
        when(fpsakKlient.getBehandling(any(BehandlingId.class))).thenReturn(lagBehandling(erBerørt));
    }

    private void opprettOgLagreOppgave() {
        oppgaveRepository.lagre(Oppgave.builder().dummyOppgave("4404").medSystem("FPSAK").build());
    }

    private static Oppgave oppgave() {
        return Oppgave.builder().dummyOppgave("0001").medSystem("FPSAK").build();
    }

    private static BehandlingFpsak lagBehandling(boolean erBerørt) {
        return BehandlingFpsak.builder()
                .medErBerørtBehandling(erBerørt)
                .build();
    }

}
