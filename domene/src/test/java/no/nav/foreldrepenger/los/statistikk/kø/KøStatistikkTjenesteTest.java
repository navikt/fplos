package no.nav.foreldrepenger.los.statistikk.kø;

import static no.nav.foreldrepenger.los.organisasjon.Avdeling.AVDELING_DRAMMEN_ENHET;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import no.nav.foreldrepenger.extensions.JpaExtension;
import no.nav.foreldrepenger.los.DBTestUtil;
import no.nav.foreldrepenger.los.avdelingsleder.AvdelingslederTjeneste;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveEgenskap;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.oppgave.OppgaveTjeneste;
import no.nav.foreldrepenger.los.oppgavekø.KøSortering;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveFiltrering;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveKøTjeneste;
import no.nav.foreldrepenger.los.organisasjon.Avdeling;
import no.nav.foreldrepenger.los.organisasjon.OrganisasjonRepository;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonRepository;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonTjeneste;
import no.nav.foreldrepenger.los.statistikk.oppgavebeholdning.NyeOgFerdigstilteOppgaver;


@ExtendWith(JpaExtension.class)
class KøStatistikkTjenesteTest {

    private EntityManager entityManager;
    private AvdelingslederTjeneste avdelingslederTjeneste;
    private KøStatistikkTjeneste køStatistikk;

    @BeforeEach
    void setUp(EntityManager entityManager) {
        this.entityManager = entityManager;
        var oppgaveRepository = new OppgaveRepository(entityManager);
        var organisasjonRepository = new OrganisasjonRepository(entityManager);
        this.avdelingslederTjeneste = new AvdelingslederTjeneste(oppgaveRepository, organisasjonRepository);
        var oppgaveKøTjeneste = new OppgaveKøTjeneste(oppgaveRepository, organisasjonRepository);
        var statistikkRepository = new KøStatistikkRepository(entityManager);
        var reservasjonRepository = new ReservasjonRepository(entityManager);
        var reservasjonTjeneste = new ReservasjonTjeneste(oppgaveRepository, reservasjonRepository);
        var oppgaveTjeneste = new OppgaveTjeneste(oppgaveRepository, reservasjonTjeneste);
        køStatistikk = new KøStatistikkTjeneste(oppgaveKøTjeneste, oppgaveTjeneste, statistikkRepository);
    }

    @Test
    void skalFinneStatistikkPåKøerSomMatcherOppgave() {
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

        køStatistikk.lagre(oppgave, KøOppgaveHendelse.LUKKET_OPPGAVE);

        var stats = køStatistikk.hentStatistikk(køMedTreff.getId());
        var forventetKøStatistikk = new NyeOgFerdigstilteOppgaver(LocalDate.now(), oppgave.getBehandlingType(), 0L, 1L);
        //assertThat(stats).containsExactly(forventetKøStatistikk);
        assertThat(stats.get(0).behandlingType()).isEqualTo(oppgave.getBehandlingType());
        assertThat(stats.get(0).antallNye()).isZero();
        assertThat(stats.get(0).antallFerdigstilte()).isEqualTo(1L);
    }

    private OppgaveFiltrering kø(Avdeling avdeling) {
        return OppgaveFiltrering.builder().medNavn("OPPRETTET").medSortering(KøSortering.OPPRETT_BEHANDLING).medAvdeling(avdeling).build();
    }

    private Avdeling avdelingForDrammen() {
        return DBTestUtil.hentAlle(entityManager, Avdeling.class)
            .stream()
            .filter(a -> a.getAvdelingEnhet().equals(AVDELING_DRAMMEN_ENHET))
            .findAny()
            .orElseThrow();
    }

}
