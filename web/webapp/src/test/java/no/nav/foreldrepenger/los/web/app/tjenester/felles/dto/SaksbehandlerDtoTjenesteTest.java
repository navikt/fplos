package no.nav.foreldrepenger.los.web.app.tjenester.felles.dto;

import static no.nav.foreldrepenger.loslager.organisasjon.Avdeling.AVDELING_DRAMMEN_ENHET;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import no.nav.foreldrepenger.extensions.EntityManagerFPLosAwareExtension;
import no.nav.foreldrepenger.loslager.oppgave.KøSortering;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveFiltrering;
import no.nav.foreldrepenger.loslager.organisasjon.Avdeling;
import no.nav.foreldrepenger.loslager.organisasjon.Saksbehandler;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepositoryImpl;
import no.nav.foreldrepenger.loslager.repository.OrganisasjonRepositoryImpl;
import no.nav.fplos.ansatt.AnsattTjeneste;
import no.nav.fplos.avdelingsleder.AvdelingslederTjeneste;
import no.nav.fplos.avdelingsleder.AvdelingslederTjenesteImpl;
import no.nav.fplos.oppgave.OppgaveTjenesteImpl;
import no.nav.vedtak.felles.testutilities.db.EntityManagerAwareTest;

@ExtendWith(EntityManagerFPLosAwareExtension.class)
public class SaksbehandlerDtoTjenesteTest extends EntityManagerAwareTest {

    private AvdelingslederTjeneste avdelingslederTjeneste;
    private SaksbehandlerDtoTjeneste saksbehandlerDtoTjeneste;

    @BeforeEach
    void setUp() {
        var entityManager = getEntityManager();
        var oppgaveRepository = new OppgaveRepositoryImpl(entityManager);
        var organisasjonRepository = new OrganisasjonRepositoryImpl(entityManager);
        avdelingslederTjeneste = new AvdelingslederTjenesteImpl(oppgaveRepository, organisasjonRepository);
        saksbehandlerDtoTjeneste = new SaksbehandlerDtoTjeneste(organisasjonRepository, avdelingslederTjeneste,
                mock(AnsattTjeneste.class), new OppgaveTjenesteImpl(oppgaveRepository, organisasjonRepository));
    }

    @Test
    public void testHentSaksbehandlerNavnOgAvdelinger(){
        String saksbehandler1Ident = "1234567";
        String saksbehandler2Ident = "9876543";
        String saksbehandler3Ident = "1234";

        Saksbehandler saksbehandler1 = new Saksbehandler(saksbehandler1Ident);
        Saksbehandler saksbehandler2 = new Saksbehandler(saksbehandler2Ident);
        var entityManager = getEntityManager();
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
            getEntityManager().persist(oppgaveFiltrering);
            filtre.add(oppgaveFiltrering);
        }
        getEntityManager().flush();
        return filtre;
    }

}
