package no.nav.foreldrepenger.los.avdelingsleder;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;

import no.nav.foreldrepenger.los.DBTestUtil;

import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.los.oppgavekø.FiltreringAndreKriterierType;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import no.nav.foreldrepenger.los.JpaExtension;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.oppgavekø.KøSortering;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveFiltrering;
import no.nav.foreldrepenger.los.organisasjon.Avdeling;
import no.nav.foreldrepenger.los.organisasjon.OrganisasjonRepository;

@ExtendWith(JpaExtension.class)
class AvdelingslederTjenesteTest {

    private OppgaveRepository oppgaveRepository;
    private AvdelingslederTjeneste avdelingslederTjeneste;
    private EntityManager entityManager;

    @BeforeEach
    void setup(EntityManager entityManager) {
        this.entityManager = entityManager;
        oppgaveRepository = new OppgaveRepository(entityManager);
        var organisasjonRepository = new OrganisasjonRepository(entityManager);
        avdelingslederTjeneste = new AvdelingslederTjeneste(oppgaveRepository, organisasjonRepository);
    }

    @Test
    void testLagNyListe() {
        avdelingslederTjeneste.lagNyOppgaveFiltrering(Avdeling.AVDELING_DRAMMEN_ENHET);
        var oppgaveFiltreringer = oppgaveRepository.hentAlleOppgaveFilterSettTilknyttetAvdeling(avdelingDrammen().getId());
        assertThat(oppgaveFiltreringer).isNotNull();
        assertThat(oppgaveFiltreringer.get(0).getId()).isNotNull();
        assertThat(oppgaveFiltreringer.get(0).getNavn()).isEqualTo("Ny liste");
        assertThat(oppgaveFiltreringer.get(0).getAvdeling()).isEqualTo(avdelingDrammen());
    }

    private Avdeling avdelingDrammen() {
        return DBTestUtil.hentAlle(entityManager, Avdeling.class)
                         .stream()
                         .filter(avdeling -> Avdeling.AVDELING_DRAMMEN_ENHET.equals(avdeling.getAvdelingEnhet()))
                         .findFirst()
                         .orElseThrow();
    }

    @Test
    void testSettNyttNavnPåListe() {
        var oppgaveFiltrering = OppgaveFiltrering.nyTomOppgaveFiltrering(avdelingDrammen());
        persistAndFlush(oppgaveFiltrering);
        var NYTT_NAVN = "Nytt navn";
        avdelingslederTjeneste.giListeNyttNavn(oppgaveFiltrering.getId(), NYTT_NAVN);
        entityManager.refresh(oppgaveFiltrering);
        assertThat(oppgaveFiltrering.getNavn()).isEqualTo(NYTT_NAVN);
    }

    @Test
    void testSlettListe() throws IllegalArgumentException {
        var liste = OppgaveFiltrering.nyTomOppgaveFiltrering(avdelingDrammen());
        persistAndFlush(liste);
        avdelingslederTjeneste.slettOppgaveFiltrering(liste.getId());
        entityManager.flush();
        assertThat(oppgaveRepository.hentAlleOppgaveFilterSettTilknyttetAvdeling(avdelingDrammen().getId())).isEmpty();
    }

    @Test
    void testSettSorteringPåListe() {
        var liste = OppgaveFiltrering.nyTomOppgaveFiltrering(avdelingDrammen());
        persistAndFlush(liste);
        avdelingslederTjeneste.settSortering(liste.getId(), KøSortering.FØRSTE_STØNADSDAG);
        entityManager.refresh(liste);
        assertThat(liste.getSortering()).isEqualTo(KøSortering.FØRSTE_STØNADSDAG);
    }

    @Test
    void settStandardSorteringNårTilbakebetalingfilterIkkeAktivt() {
        var liste = OppgaveFiltrering.nyTomOppgaveFiltrering(avdelingDrammen());
        liste.leggTilFilter(BehandlingType.TILBAKEBETALING);
        persistAndFlush(liste);
        avdelingslederTjeneste.settSortering(liste.getId(), KøSortering.BELØP);
        avdelingslederTjeneste.settSorteringNumeriskIntervall(liste.getId(), 100L, 200L);
        entityManager.refresh(liste);
        assertThat(liste)
            .matches(d -> d.getFra().equals(100L))
            .matches(d -> d.getTil().equals(200L))
            .matches(OppgaveFiltrering::getErDynamiskPeriode)
            .matches(d -> d.getSortering() == KøSortering.BELØP);

        // sett standard sortering når det ikke lenger er filter på behandlingtype tilbakebetaling
        avdelingslederTjeneste.endreFiltreringBehandlingType(liste.getId(), BehandlingType.TILBAKEBETALING, false);
        entityManager.refresh(liste);
        assertThat(liste)
            .matches(d -> d.getFra() == null)
            .matches(d -> d.getTil() == null)
            .matches(d -> !d.getErDynamiskPeriode())
            .matches(d -> d.getSortering() == KøSortering.BEHANDLINGSFRIST);
    }

    @Test
    void leggTilBehandlingtypeFiltrering() {
        var oppgaveFiltrering = OppgaveFiltrering.nyTomOppgaveFiltrering(avdelingDrammen());
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
    void leggTilYtelsetypeFiltrering() {
        var oppgaveFiltrering = OppgaveFiltrering.nyTomOppgaveFiltrering(avdelingDrammen());
        persistAndFlush(oppgaveFiltrering);
        avdelingslederTjeneste.endreFagsakYtelseType(oppgaveFiltrering.getId(), FagsakYtelseType.ENGANGSTØNAD, true);
        entityManager.refresh(oppgaveFiltrering);
        assertThat(oppgaveFiltrering.getFagsakYtelseTyper()).isNotEmpty();
        assertThat(oppgaveFiltrering.getFagsakYtelseTyper()).first().isEqualTo(FagsakYtelseType.ENGANGSTØNAD);
        avdelingslederTjeneste.endreFagsakYtelseType(oppgaveFiltrering.getId(), FagsakYtelseType.ENGANGSTØNAD, false);
        entityManager.refresh(oppgaveFiltrering);
        assertThat(oppgaveFiltrering.getFagsakYtelseTyper()).isEmpty();
    }

    @Test
    void leggTilAndreKriterierFiltrering() {
        var oppgaveFiltrering = OppgaveFiltrering.nyTomOppgaveFiltrering(avdelingDrammen());
        persistAndFlush(oppgaveFiltrering);
        avdelingslederTjeneste.endreFiltreringAndreKriterierType(oppgaveFiltrering.getId(), AndreKriterierType.TIL_BESLUTTER, true, true);
        entityManager.refresh(oppgaveFiltrering);
        assertThat(oppgaveFiltrering.getFiltreringAndreKriterierTyper()).isNotEmpty();
        assertThat(oppgaveFiltrering.getFiltreringAndreKriterierTyper()).first()
            .extracting(FiltreringAndreKriterierType::getAndreKriterierType).isEqualTo(AndreKriterierType.TIL_BESLUTTER);
        avdelingslederTjeneste.endreFiltreringAndreKriterierType(oppgaveFiltrering.getId(), AndreKriterierType.TIL_BESLUTTER, false, true);
        entityManager.refresh(oppgaveFiltrering);
        assertThat(oppgaveFiltrering.getFiltreringAndreKriterierTyper()).isEmpty();
    }

    @Test
    void testHentAvdelinger() {
        var avdelinger = avdelingslederTjeneste.hentAvdelinger();
        Assertions.assertThat(avdelinger).isNotEmpty().hasSizeGreaterThan(1);
    }

    private void persistAndFlush(OppgaveFiltrering oppgaveFiltrering) {
        entityManager.persist(oppgaveFiltrering);
        entityManager.flush();
    }
}
