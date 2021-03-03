package no.nav.foreldrepenger.los.avdelingsleder;

import no.nav.foreldrepenger.dbstoette.DBTestUtil;
import no.nav.foreldrepenger.extensions.EntityManagerFPLosAwareExtension;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepositoryImpl;
import no.nav.foreldrepenger.los.organisasjon.OrganisasjonRepositoryImpl;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.los.oppgavekø.KøSortering;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveFiltrering;
import no.nav.foreldrepenger.los.organisasjon.Avdeling;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(EntityManagerFPLosAwareExtension.class)
public class AvdelingslederTjenesteTest {

    private OppgaveRepository oppgaveRepository;
    private AvdelingslederTjeneste avdelingslederTjeneste;
    private EntityManager entityManager;

    @BeforeEach
    public void setup(EntityManager entityManager){
        this.entityManager = entityManager;
        oppgaveRepository = new OppgaveRepositoryImpl(entityManager);
        var organisasjonRepository = new OrganisasjonRepositoryImpl(entityManager);
        avdelingslederTjeneste = new AvdelingslederTjeneste(oppgaveRepository, organisasjonRepository);
    }

    @Test
    public void testLagNyListe(){
        avdelingslederTjeneste.lagNyOppgaveFiltrering(Avdeling.AVDELING_DRAMMEN_ENHET);
        List<OppgaveFiltrering> oppgaveFiltreringer = oppgaveRepository.hentAlleOppgaveFilterSettTilknyttetAvdeling(avdelingDrammen().getId());
        Assertions.assertThat(oppgaveFiltreringer).isNotNull();
        Assertions.assertThat(oppgaveFiltreringer.get(0).getId()).isNotNull();
        Assertions.assertThat(oppgaveFiltreringer.get(0).getNavn()).isEqualTo("Ny liste");
        Assertions.assertThat(oppgaveFiltreringer.get(0).getAvdeling()).isEqualTo(avdelingDrammen());
    }

    private Avdeling avdelingDrammen() {
        return DBTestUtil.hentAlle(entityManager, Avdeling.class).stream()
                .filter(avdeling -> Avdeling.AVDELING_DRAMMEN_ENHET.equals(avdeling.getAvdelingEnhet())).findFirst()
                .orElseThrow();
    }

    @Test
    public void testSettNyttNavnPåListe(){
        OppgaveFiltrering oppgaveFiltrering = OppgaveFiltrering.nyTomOppgaveFiltrering(avdelingDrammen());
        persistAndFlush(oppgaveFiltrering);
        String NYTT_NAVN = "Nytt navn";
        avdelingslederTjeneste.giListeNyttNavn(oppgaveFiltrering.getId(), NYTT_NAVN);
        entityManager.refresh(oppgaveFiltrering);
        Assertions.assertThat(oppgaveFiltrering.getNavn()).isEqualTo(NYTT_NAVN);
    }

    @Test
    public void testSlettListe()throws IllegalArgumentException {
        OppgaveFiltrering liste = OppgaveFiltrering.nyTomOppgaveFiltrering(avdelingDrammen());
        persistAndFlush(liste);
        avdelingslederTjeneste.slettOppgaveFiltrering(liste.getId());
        entityManager.flush();
        Assertions.assertThat(oppgaveRepository.hentAlleOppgaveFilterSettTilknyttetAvdeling(avdelingDrammen().getId())).isEmpty();
    }

    @Test
    public void testSettSorteringPåListe() {
        OppgaveFiltrering liste = OppgaveFiltrering.nyTomOppgaveFiltrering(avdelingDrammen());
        persistAndFlush(liste);
        avdelingslederTjeneste.settSortering(liste.getId(), KøSortering.BEHANDLINGSFRIST);
        entityManager.refresh(liste);
        Assertions.assertThat(liste.getSortering()).isEqualTo(KøSortering.BEHANDLINGSFRIST);
    }

    @Test
    public void leggTilBehandlingtypeFiltrering(){
        OppgaveFiltrering oppgaveFiltrering = OppgaveFiltrering.nyTomOppgaveFiltrering(avdelingDrammen());
        persistAndFlush(oppgaveFiltrering);
        avdelingslederTjeneste.endreFiltreringBehandlingType(oppgaveFiltrering.getId(), BehandlingType.FØRSTEGANGSSØKNAD, true);
        entityManager.refresh(oppgaveFiltrering);
        Assertions.assertThat(oppgaveFiltrering.getFiltreringBehandlingTyper()).isNotEmpty();
        Assertions.assertThat(oppgaveFiltrering.getFiltreringBehandlingTyper().get(0).getBehandlingType()).isEqualTo(BehandlingType.FØRSTEGANGSSØKNAD);
        avdelingslederTjeneste.endreFiltreringBehandlingType(oppgaveFiltrering.getId(), BehandlingType.FØRSTEGANGSSØKNAD, false);
        entityManager.refresh(oppgaveFiltrering);
        Assertions.assertThat(oppgaveFiltrering.getFiltreringBehandlingTyper()).isEmpty();
    }

    @Test
    public void leggTilYtelsetypeFiltrering(){
        OppgaveFiltrering oppgaveFiltrering = OppgaveFiltrering.nyTomOppgaveFiltrering(avdelingDrammen());
        persistAndFlush(oppgaveFiltrering);
        avdelingslederTjeneste.endreFiltreringYtelseType(oppgaveFiltrering.getId(), FagsakYtelseType.ENGANGSTØNAD);
        entityManager.refresh(oppgaveFiltrering);
        Assertions.assertThat(oppgaveFiltrering.getFiltreringYtelseTyper()).isNotEmpty();
        Assertions.assertThat(oppgaveFiltrering.getFiltreringYtelseTyper().get(0).getFagsakYtelseType()).isEqualTo(FagsakYtelseType.ENGANGSTØNAD);
        avdelingslederTjeneste.endreFiltreringYtelseType(oppgaveFiltrering.getId(), null);
        entityManager.refresh(oppgaveFiltrering);
        Assertions.assertThat(oppgaveFiltrering.getFiltreringYtelseTyper()).isEmpty();
    }

    @Test
    public void leggTilAndreKriterierFiltrering(){
        OppgaveFiltrering oppgaveFiltrering = OppgaveFiltrering.nyTomOppgaveFiltrering(avdelingDrammen());
        persistAndFlush(oppgaveFiltrering);
        avdelingslederTjeneste.endreFiltreringAndreKriterierType(oppgaveFiltrering.getId(), AndreKriterierType.TIL_BESLUTTER, true, true);
        entityManager.refresh(oppgaveFiltrering);
        Assertions.assertThat(oppgaveFiltrering.getFiltreringAndreKriterierTyper()).isNotEmpty();
        Assertions.assertThat(oppgaveFiltrering.getFiltreringAndreKriterierTyper().get(0).getAndreKriterierType()).isEqualTo(AndreKriterierType.TIL_BESLUTTER);
        avdelingslederTjeneste.endreFiltreringAndreKriterierType(oppgaveFiltrering.getId(), AndreKriterierType.TIL_BESLUTTER, false, true );
        entityManager.refresh(oppgaveFiltrering);
        Assertions.assertThat(oppgaveFiltrering.getFiltreringAndreKriterierTyper()).isEmpty();
    }

    @Test
    public void testHentAvdelinger(){
        List<Avdeling> avdelinger = avdelingslederTjeneste.hentAvdelinger();
        Assertions.assertThat(avdelinger).isNotEmpty();
        assertThat(avdelinger.size()).isGreaterThan(1);
    }

    private void persistAndFlush(OppgaveFiltrering oppgaveFiltrering) {
        entityManager.persist(oppgaveFiltrering);
        entityManager.flush();
    }
}
