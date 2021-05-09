package no.nav.foreldrepenger.los.statistikk.statistikk_ny;

import no.nav.foreldrepenger.dbstøtte.DBTestUtil;
import no.nav.foreldrepenger.extensions.EntityManagerFPLosAwareExtension;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveFiltreringKnytning;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveKøTjeneste;
import no.nav.foreldrepenger.los.organisasjon.OrganisasjonRepository;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgavekø.KøSortering;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveEgenskap;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveFiltrering;
import no.nav.foreldrepenger.los.organisasjon.Avdeling;
import no.nav.foreldrepenger.los.avdelingsleder.AvdelingslederTjeneste;
import no.nav.foreldrepenger.los.oppgave.OppgaveTjeneste;
import no.nav.foreldrepenger.los.statistikk.statistikk_gammel.NyeOgFerdigstilteOppgaver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

import static no.nav.foreldrepenger.los.organisasjon.Avdeling.AVDELING_DRAMMEN_ENHET;
import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(EntityManagerFPLosAwareExtension.class)
class OppgaveStatistikkTest {

    private EntityManager entityManager;
    private AvdelingslederTjeneste avdelingslederTjeneste;
    private OppgaveStatistikk oppgaveStatistikk;

    @BeforeEach
    public void setUp(EntityManager entityManager) {
        this.entityManager = entityManager;
        var oppgaveRepository = new OppgaveRepository(entityManager);
        var organisasjonRepository = new OrganisasjonRepository(entityManager);
        this.avdelingslederTjeneste = new AvdelingslederTjeneste(oppgaveRepository, organisasjonRepository);
        var oppgaveKøTjeneste = new OppgaveKøTjeneste(oppgaveRepository, organisasjonRepository);
        var statistikkRepository = new NyOpppgaveStatistikkRepository(entityManager);
        var oppgaveTjeneste = new OppgaveTjeneste(oppgaveRepository);
        oppgaveStatistikk = new OppgaveStatistikk(oppgaveKøTjeneste, oppgaveTjeneste, statistikkRepository);
    }

    @Test
    public void skalFinneStatistikkPåKøerSomMatcherOppgave() {
        var avdeling = avdelingForDrammen();
        var oppgave = Oppgave.builder().dummyOppgave(avdeling.getAvdelingEnhet()).build();
        var egenskap = new OppgaveEgenskap(oppgave, AndreKriterierType.BERØRT_BEHANDLING);
        var køMedTreff = kø(avdeling);
        var køUtenTreff = kø(avdeling);

        entityManager.persist(oppgave);
        entityManager.persist(egenskap);
        entityManager.persist(køMedTreff);
        entityManager.persist(køUtenTreff);
        entityManager.flush();

        avdelingslederTjeneste.endreFiltreringAndreKriterierType(køUtenTreff.getId(), AndreKriterierType.BERØRT_BEHANDLING, true, false);

        oppgaveStatistikk.lagre(oppgave, KøOppgaveHendelse.LUKKET_OPPGAVE);

        var stats = oppgaveStatistikk.hentStatistikk(køMedTreff.getId());
        var forventetKøStatistikk = new NyeOgFerdigstilteOppgaver(LocalDate.now(), oppgave.getBehandlingType(), 0L, 1L);
        //assertThat(stats).containsExactly(forventetKøStatistikk);
        assertThat(stats.get(0).behandlingType()).isEqualTo(oppgave.getBehandlingType());
        assertThat(stats.get(0).antallNye()).isEqualTo(0L);
        assertThat(stats.get(0).antallFerdigstilte()).isEqualTo(1L);
    }

    @Test
    public void skalLagreStatistikkFørEtterOppdatering() {
        var knytninger = new OppgaveknytningerFørEtterOppdatering();
        var førOppdatering = List.of(
                new OppgaveFiltreringKnytning(1L, 1L, BehandlingType.FØRSTEGANGSSØKNAD),
                new OppgaveFiltreringKnytning(1L, 2L, BehandlingType.FØRSTEGANGSSØKNAD));

        var etterOppdatering = List.of(
                new OppgaveFiltreringKnytning(1L, 1L, BehandlingType.FØRSTEGANGSSØKNAD),
                new OppgaveFiltreringKnytning(1L, 3L, BehandlingType.FØRSTEGANGSSØKNAD));
        knytninger.setKnytningerFørOppdatering(førOppdatering);
        knytninger.setKnytningerEtterOppdatering(etterOppdatering);
        oppgaveStatistikk.lagre(knytninger);

        var kø2 = oppgaveStatistikk.hentStatistikk(2L);
        //assertThat(kø2).containsExactly(new NyeOgFerdigstilteOppgaver(LocalDate.now(), BehandlingType.FØRSTEGANGSSØKNAD, 0L, 1L));
        assertThat(kø2.get(0).behandlingType()).isEqualTo(BehandlingType.FØRSTEGANGSSØKNAD);
        assertThat(kø2.get(0).antallNye()).isEqualTo(0L);
        assertThat(kø2.get(0).antallFerdigstilte()).isEqualTo(1L);
        var kø1 = oppgaveStatistikk.hentStatistikk(1L);
        assertThat(kø1).isEmpty();
        var kø3 = oppgaveStatistikk.hentStatistikk(3L);
        //assertThat(kø3).containsExactly(new NyeOgFerdigstilteOppgaver(LocalDate.now(), BehandlingType.FØRSTEGANGSSØKNAD, 1L, 0L));
        assertThat(kø3.get(0).behandlingType()).isEqualTo(BehandlingType.FØRSTEGANGSSØKNAD);
        assertThat(kø3.get(0).antallNye()).isEqualTo(1L);
        assertThat(kø3.get(0).antallFerdigstilte()).isEqualTo(0L);
    }

    private OppgaveFiltrering kø(Avdeling avdeling) {
        return OppgaveFiltrering.builder().medNavn("OPPRETTET")
                .medSortering(KøSortering.OPPRETT_BEHANDLING)
                .medAvdeling(avdeling).build();
    }

    private Avdeling avdelingForDrammen() {
        return DBTestUtil.hentAlle(entityManager, Avdeling.class).stream()
                .filter(a -> a.getAvdelingEnhet().equals(AVDELING_DRAMMEN_ENHET))
                .findAny().orElseThrow();
    }

}
