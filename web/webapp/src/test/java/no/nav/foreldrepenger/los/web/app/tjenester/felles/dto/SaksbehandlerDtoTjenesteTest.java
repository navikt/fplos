package no.nav.foreldrepenger.los.web.app.tjenester.felles.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import no.nav.foreldrepenger.dbstoette.UnittestRepositoryRule;
import no.nav.foreldrepenger.loslager.oppgave.KøSortering;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveFiltrering;
import no.nav.foreldrepenger.loslager.organisasjon.Avdeling;
import no.nav.foreldrepenger.loslager.organisasjon.Saksbehandler;
import no.nav.fplos.avdelingsleder.AvdelingslederTjeneste;
import no.nav.vedtak.felles.testutilities.cdi.CdiRunner;

@RunWith(CdiRunner.class)
public class SaksbehandlerDtoTjenesteTest {

    private static final String AVDELING_DRAMMEN_ENHET = "4806";
    @Rule
    public final UnittestRepositoryRule repoRule = new UnittestRepositoryRule();
    private final EntityManager entityManager = repoRule.getEntityManager();

    @Inject
    private AvdelingslederTjeneste avdelingslederTjeneste;

    @Inject
    private SaksbehandlerDtoTjeneste saksbehandlerDtoTjeneste;

    @Test
    public void testHentSaksbehandlerNavnOgAvdelinger(){
        String saksbehandler1Ident = "1234567";
        String saksbehandler2Ident = "9876543";
        String saksbehandler3Ident = "1234";

        Saksbehandler saksbehandler1 = new Saksbehandler(saksbehandler1Ident);
        Saksbehandler saksbehandler2 = new Saksbehandler(saksbehandler2Ident);
        entityManager.persist(saksbehandler1);
        entityManager.persist(saksbehandler2);
        entityManager.flush();

        List<OppgaveFiltrering> lagtInnLister = leggInnEtSettMedLister(1);

        avdelingslederTjeneste.leggSaksbehandlerTilListe(lagtInnLister.get(0).getId(), saksbehandler1.getSaksbehandlerIdent());
        entityManager.refresh(saksbehandler1);

        assertThat(saksbehandlerDtoTjeneste.hentSaksbehandlerTilknyttetMinstEnKø(saksbehandler3Ident)).isEmpty();
        assertThat(saksbehandlerDtoTjeneste.hentSaksbehandlerTilknyttetMinstEnKø(saksbehandler2Ident)).isEmpty();
    }

    private List<OppgaveFiltrering> leggInnEtSettMedLister(int antallLister){
        List<OppgaveFiltrering> filtre = new ArrayList<>();

        List<Avdeling> avdelings = avdelingslederTjeneste.hentAvdelinger();
        var avdelingDrammen = avdelings.stream().filter(avdeling -> AVDELING_DRAMMEN_ENHET.equals(avdeling.getAvdelingEnhet())).findFirst().orElseThrow();
        for(int i = 0; i< antallLister; i++) {
            OppgaveFiltrering oppgaveFiltrering = OppgaveFiltrering.builder().medNavn("Test " + i).medSortering(KøSortering.BEHANDLINGSFRIST).medAvdeling(avdelingDrammen).build();
            entityManager.persist(oppgaveFiltrering);
            filtre.add(oppgaveFiltrering);
        }
        entityManager.flush();
        return filtre;
    }

}
