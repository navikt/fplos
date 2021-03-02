package no.nav.fplos.domenetjenester.statistikk_ny;

import no.nav.foreldrepenger.dbstoette.DBTestUtil;
import no.nav.foreldrepenger.extensions.EntityManagerFPLosAwareExtension;
import no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.loslager.oppgave.KøSortering;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEgenskap;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveFiltrering;
import no.nav.foreldrepenger.loslager.organisasjon.Avdeling;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepositoryImpl;
import no.nav.foreldrepenger.loslager.repository.OrganisasjonRepositoryImpl;
import no.nav.foreldrepenger.loslager.repository.oppgavestatistikk.KøOppgaveHendelse;
import no.nav.foreldrepenger.loslager.repository.oppgavestatistikk.KøStatistikk;
import no.nav.foreldrepenger.loslager.repository.oppgavestatistikk.NyOpppgaveStatistikkRepository;
import no.nav.fplos.domenetjenester.avdelingsleder.AvdelingslederTjeneste;
import no.nav.fplos.domenetjenester.kø.OppgaveKøTjeneste;
import no.nav.fplos.domenetjenester.oppgave.OppgaveTjenesteImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.persistence.EntityManager;
import java.time.LocalDate;

import static no.nav.foreldrepenger.loslager.organisasjon.Avdeling.AVDELING_DRAMMEN_ENHET;
import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(EntityManagerFPLosAwareExtension.class)
class OppgaveStatistikkTest {

    private EntityManager entityManager;
    private AvdelingslederTjeneste avdelingslederTjeneste;
    private OppgaveStatistikk oppgaveStatistikk;

    @BeforeEach
    public void setUp(EntityManager entityManager) {
        this.entityManager = entityManager;
        var oppgaveRepository = new OppgaveRepositoryImpl(entityManager);
        var organisasjonRepository = new OrganisasjonRepositoryImpl(entityManager);
        this.avdelingslederTjeneste = new AvdelingslederTjeneste(oppgaveRepository, organisasjonRepository);
        var oppgaveKøTjeneste = new OppgaveKøTjeneste(oppgaveRepository, organisasjonRepository);
        var statistikkRepository = new NyOpppgaveStatistikkRepository(entityManager);
        var oppgaveTjeneste = new OppgaveTjenesteImpl(oppgaveRepository);
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
        var forventetKøStatistikk = new KøStatistikk(LocalDate.now(), oppgave.getBehandlingType(), KøOppgaveHendelse.LUKKET_OPPGAVE, 1L);
        assertThat(stats).containsExactly(forventetKøStatistikk);
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
