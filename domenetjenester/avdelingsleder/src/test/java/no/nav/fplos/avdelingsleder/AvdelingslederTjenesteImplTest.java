package no.nav.fplos.avdelingsleder;

import static no.nav.foreldrepenger.dbstoette.DBTestUtil.hentAlle;
import static no.nav.foreldrepenger.loslager.organisasjon.Avdeling.AVDELING_DRAMMEN_ENHET;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import no.nav.foreldrepenger.extensions.EntityManagerFPLosAwareExtension;
import no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;
import no.nav.foreldrepenger.loslager.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.loslager.oppgave.KøSortering;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveFiltrering;
import no.nav.foreldrepenger.loslager.organisasjon.Avdeling;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepository;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepositoryImpl;
import no.nav.foreldrepenger.loslager.repository.OrganisasjonRepositoryImpl;

@ExtendWith(EntityManagerFPLosAwareExtension.class)
public class AvdelingslederTjenesteImplTest {

    private OppgaveRepository oppgaveRepository;
    private AvdelingslederTjeneste avdelingslederTjeneste;
    private EntityManager entityManager;

    @BeforeEach
    public void setup(EntityManager entityManager){
        this.entityManager = entityManager;
        oppgaveRepository = new OppgaveRepositoryImpl(entityManager);
        var organisasjonRepository = new OrganisasjonRepositoryImpl(entityManager);
        avdelingslederTjeneste = new AvdelingslederTjenesteImpl(oppgaveRepository, organisasjonRepository);
    }

    @Test
    public void testLagNyListe(){
        avdelingslederTjeneste.lagNyOppgaveFiltrering(AVDELING_DRAMMEN_ENHET);
        List<OppgaveFiltrering> oppgaveFiltreringer = oppgaveRepository.hentAlleFiltreringer(avdelingDrammen().getId());
        assertThat(oppgaveFiltreringer).isNotNull();
        assertThat(oppgaveFiltreringer.get(0).getId()).isNotNull();
        assertThat(oppgaveFiltreringer.get(0).getNavn()).isEqualTo("Ny liste");
        assertThat(oppgaveFiltreringer.get(0).getAvdeling()).isEqualTo(avdelingDrammen());
    }

    private Avdeling avdelingDrammen() {
        return hentAlle(entityManager, Avdeling.class).stream()
                .filter(avdeling -> AVDELING_DRAMMEN_ENHET.equals(avdeling.getAvdelingEnhet())).findFirst()
                .orElseThrow();
    }

    @Test
    public void testSettNyttNavnPåListe(){
        OppgaveFiltrering oppgaveFiltrering = OppgaveFiltrering.nyTomOppgaveFiltrering(avdelingDrammen());
        persistAndFlush(oppgaveFiltrering);
        String NYTT_NAVN = "Nytt navn";
        avdelingslederTjeneste.giListeNyttNavn(oppgaveFiltrering.getId(), NYTT_NAVN);
        entityManager.refresh(oppgaveFiltrering);
        assertThat(oppgaveFiltrering.getNavn()).isEqualTo(NYTT_NAVN);
    }

    @Test
    public void testSlettListe()throws IllegalArgumentException {
        OppgaveFiltrering liste = OppgaveFiltrering.nyTomOppgaveFiltrering(avdelingDrammen());
        persistAndFlush(liste);
        avdelingslederTjeneste.slettOppgaveFiltrering(liste.getId());
        entityManager.flush();
        assertThat(oppgaveRepository.hentAlleFiltreringer(avdelingDrammen().getId())).isEmpty();
    }

    @Test
    public void testSettSorteringPåListe() {
        OppgaveFiltrering liste = OppgaveFiltrering.nyTomOppgaveFiltrering(avdelingDrammen());
        persistAndFlush(liste);
        avdelingslederTjeneste.settSortering(liste.getId(), KøSortering.BEHANDLINGSFRIST);
        entityManager.refresh(liste);
        assertThat(liste.getSortering()).isEqualTo(KøSortering.BEHANDLINGSFRIST);
    }

    @Test
    public void leggTilBehandlingtypeFiltrering(){
        OppgaveFiltrering oppgaveFiltrering = OppgaveFiltrering.nyTomOppgaveFiltrering(avdelingDrammen());
        persistAndFlush(oppgaveFiltrering);
        avdelingslederTjeneste.endreFiltreringBehandlingType(oppgaveFiltrering.getId(), BehandlingType.FØRSTEGANGSSØKNAD, true);
        entityManager.refresh(oppgaveFiltrering);
        assertThat(oppgaveFiltrering.getFiltreringBehandlingTyper()).isNotEmpty();
        assertThat(oppgaveFiltrering.getFiltreringBehandlingTyper().get(0).getBehandlingType()).isEqualTo(BehandlingType.FØRSTEGANGSSØKNAD);
        avdelingslederTjeneste.endreFiltreringBehandlingType(oppgaveFiltrering.getId(), BehandlingType.FØRSTEGANGSSØKNAD, false);
        entityManager.refresh(oppgaveFiltrering);
        assertThat(oppgaveFiltrering.getFiltreringBehandlingTyper()).isEmpty();
    }

    @Test
    public void leggTilYtelsetypeFiltrering(){
        OppgaveFiltrering oppgaveFiltrering = OppgaveFiltrering.nyTomOppgaveFiltrering(avdelingDrammen());
        persistAndFlush(oppgaveFiltrering);
        avdelingslederTjeneste.endreFiltreringYtelseType(oppgaveFiltrering.getId(), FagsakYtelseType.ENGANGSTØNAD);
        entityManager.refresh(oppgaveFiltrering);
        assertThat(oppgaveFiltrering.getFiltreringYtelseTyper()).isNotEmpty();
        assertThat(oppgaveFiltrering.getFiltreringYtelseTyper().get(0).getFagsakYtelseType()).isEqualTo(FagsakYtelseType.ENGANGSTØNAD);
        avdelingslederTjeneste.endreFiltreringYtelseType(oppgaveFiltrering.getId(), null);
        entityManager.refresh(oppgaveFiltrering);
        assertThat(oppgaveFiltrering.getFiltreringYtelseTyper()).isEmpty();
    }

    @Test
    public void leggTilAndreKriterierFiltrering(){
        OppgaveFiltrering oppgaveFiltrering = OppgaveFiltrering.nyTomOppgaveFiltrering(avdelingDrammen());
        persistAndFlush(oppgaveFiltrering);
        avdelingslederTjeneste.endreFiltreringAndreKriterierType(oppgaveFiltrering.getId(), AndreKriterierType.TIL_BESLUTTER, true, true);
        entityManager.refresh(oppgaveFiltrering);
        assertThat(oppgaveFiltrering.getFiltreringAndreKriterierTyper()).isNotEmpty();
        assertThat(oppgaveFiltrering.getFiltreringAndreKriterierTyper().get(0).getAndreKriterierType()).isEqualTo(AndreKriterierType.TIL_BESLUTTER);
        avdelingslederTjeneste.endreFiltreringAndreKriterierType(oppgaveFiltrering.getId(), AndreKriterierType.TIL_BESLUTTER, false, true );
        entityManager.refresh(oppgaveFiltrering);
        assertThat(oppgaveFiltrering.getFiltreringAndreKriterierTyper()).isEmpty();
    }

    @Test
    public void testHentAvdelinger(){
        List<Avdeling> avdelinger = avdelingslederTjeneste.hentAvdelinger();
        assertThat(avdelinger).isNotEmpty();
        assertThat(avdelinger.size()).isGreaterThan(1);
    }

    private void persistAndFlush(OppgaveFiltrering oppgaveFiltrering) {
        entityManager.persist(oppgaveFiltrering);
        entityManager.flush();
    }
}
