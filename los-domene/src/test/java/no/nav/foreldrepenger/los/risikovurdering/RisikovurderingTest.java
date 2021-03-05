package no.nav.foreldrepenger.los.risikovurdering;

import no.nav.foreldrepenger.dbstoette.DBTestUtil;
import no.nav.foreldrepenger.extensions.EntityManagerFPLosAwareExtension;
import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.klient.fpsak.BehandlingFpsak;
import no.nav.foreldrepenger.los.klient.fpsak.Lazy;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveEgenskap;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepositoryImpl;
import no.nav.foreldrepenger.los.oppgave.OppgaveTjenesteImpl;
import no.nav.foreldrepenger.los.oppgave.oppgaveegenskap.AktuelleOppgaveEgenskaperTjeneste;
import no.nav.foreldrepenger.los.oppgave.oppgaveegenskap.OppgaveEgenskapTjeneste;
import no.nav.foreldrepenger.los.risikovurdering.modell.Kontrollresultat;
import no.nav.foreldrepenger.los.risikovurdering.modell.KontrollresultatWrapper;
import no.nav.foreldrepenger.los.risikovurdering.modell.RisikoklassifiseringEntitet;
import no.nav.foreldrepenger.los.risikovurdering.modell.RisikoklassifiseringRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(EntityManagerFPLosAwareExtension.class)
public class RisikovurderingTest {

    private EntityManager entityManager;
    private RisikovurderingTjeneste risikovurderingTjeneste;
    private OppgaveRepositoryImpl oppgaveRepository;

    @BeforeEach
    public void setup(EntityManager entityManager) {
        this.entityManager = entityManager;
        oppgaveRepository = new OppgaveRepositoryImpl(entityManager);
        var oppgaveTjeneste = new OppgaveTjenesteImpl(oppgaveRepository);
        var oppgaveEgenskapTjeneste = new OppgaveEgenskapTjeneste(oppgaveRepository);
        var risikoklassifiseringRepository = new RisikoklassifiseringRepository(entityManager);
        risikovurderingTjeneste = new RisikovurderingTjeneste(risikoklassifiseringRepository, oppgaveTjeneste, oppgaveEgenskapTjeneste);
    }

    @Test
    public void skal_lagre_risikovurdering_på_behandlingId() {
        var behandlingId = BehandlingId.random();
        var kontrollResultatWrapper = new KontrollresultatWrapper(behandlingId, Kontrollresultat.IKKE_HØY);
        risikovurderingTjeneste.lagreKontrollresultat(kontrollResultatWrapper);
        var risikoklassifiseringEntitet = DBTestUtil.hentUnik(entityManager, RisikoklassifiseringEntitet.class);
        assertThat(risikoklassifiseringEntitet.getBehandlingId()).isEqualTo(behandlingId);
        assertThat(risikoklassifiseringEntitet.getKontrollresultat()).isEqualTo(Kontrollresultat.IKKE_HØY);
    }

    @Test
    public void skal_opprette_oppgaveEgenskap_ved_høy_risiko() {
        var oppgaveEgenskaper = behandleNyttKlassifiseringresultatOgHentOppgavekriterier(Kontrollresultat.HØY);
        assertThat(oppgaveEgenskaper).containsExactly(AndreKriterierType.VURDER_FARESIGNALER);
    }

    @Test
    public void skal_ikke_opprette_oppgaveEgenskap_ved_lav_risiko() {
        var oppgaveEgenskaper = behandleNyttKlassifiseringresultatOgHentOppgavekriterier(Kontrollresultat.IKKE_HØY);
        assertThat(oppgaveEgenskaper).isEmpty();
    }

    @Test
    public void FLYTT_TIL_AKTUELLE_OPPGAVE_EGENSKAPERTEST_skal_få_vurder_faresignaler_oppgaveegenskap_ved_høy_risiko() {
        var behandlingId = BehandlingId.random();
        risikovurderingTjeneste.lagreKontrollresultat(lagWrapper(behandlingId, Kontrollresultat.HØY));
        var aktuelleOppgaveEgenskaperTjeneste = new AktuelleOppgaveEgenskaperTjeneste(risikovurderingTjeneste);
        var fpsakBehandling = behandlingFpsak(behandlingId);
        var aktuelleOppgaveegenskaper = aktuelleOppgaveEgenskaperTjeneste.egenskaperForFpsak(fpsakBehandling);
        assertThat(aktuelleOppgaveegenskaper.getAndreKriterierTyper()).containsExactly(AndreKriterierType.VURDER_FARESIGNALER);
    }

    @Test
    public void FLYTT_TIL_AKTUELLE_OPPGAVE_EGENSKAPERTEST_skal_ikke_få_vurder_faresignaler_oppgaveegenskap_ved_lav_risiko() {
        var behandlingId = BehandlingId.random();
        risikovurderingTjeneste.lagreKontrollresultat(lagWrapper(behandlingId, Kontrollresultat.IKKE_HØY));
        var aktuelleOppgaveEgenskaperTjeneste = new AktuelleOppgaveEgenskaperTjeneste(risikovurderingTjeneste);
        var fpsakBehandling = behandlingFpsak(behandlingId);
        var aktuelleOppgaveegenskaper = aktuelleOppgaveEgenskaperTjeneste.egenskaperForFpsak(fpsakBehandling);
        assertThat(aktuelleOppgaveegenskaper.getAndreKriterierTyper()).isEmpty();
    }


    private List<AndreKriterierType> behandleNyttKlassifiseringresultatOgHentOppgavekriterier(Kontrollresultat ikkeHøy) {
        var oppgave = Oppgave.builder().dummyOppgave("4406").build();
        var behandlingId = oppgave.getBehandlingId();
        oppgaveRepository.lagre(oppgave);
        risikovurderingTjeneste.lagreKontrollresultat(lagWrapper(behandlingId, ikkeHøy));
        return DBTestUtil.hentAlle(entityManager, OppgaveEgenskap.class).stream()
                .filter(oe -> oe.getOppgave().getId().equals(oppgave.getId()))
                .filter(OppgaveEgenskap::getAktiv)
                .map(OppgaveEgenskap::getAndreKriterierType)
                .collect(Collectors.toList());
    }


    private static BehandlingFpsak behandlingFpsak(BehandlingId behandlingId) {
        return BehandlingFpsak.builder()
                .medBehandlingId(behandlingId)
                .medHarRefusjonskravFraArbeidsgiver(new Lazy<Boolean>(() -> null))
                .build();
    }

    private static KontrollresultatWrapper lagWrapper(BehandlingId behandlingId, Kontrollresultat resultat) {
        return new KontrollresultatWrapper(behandlingId, resultat);
    }

}

