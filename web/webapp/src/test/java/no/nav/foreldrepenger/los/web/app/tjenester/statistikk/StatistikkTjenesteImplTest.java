package no.nav.foreldrepenger.los.web.app.tjenester.statistikk;

import no.nav.foreldrepenger.dbstoette.UnittestRepositoryRule;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.nøkkeltall.dto.OppgaverForAvdelingDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.nøkkeltall.dto.OppgaverForAvdelingPerDatoDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.nøkkeltall.dto.OppgaverForAvdelingSattManueltPaaVentDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.nøkkeltall.dto.OppgaverForFørsteStønadsdagDto;
import no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;
import no.nav.foreldrepenger.loslager.oppgave.EksternIdentifikator;
import no.nav.foreldrepenger.loslager.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEgenskap;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventLogg;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventType;
import no.nav.foreldrepenger.loslager.organisasjon.Avdeling;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepositoryProvider;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepositoryProviderImpl;
import no.nav.foreldrepenger.loslager.repository.StatistikkRepository;
import no.nav.fplos.statistikk.StatistikkTjeneste;
import no.nav.fplos.statistikk.StatistikkTjenesteImpl;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class StatistikkTjenesteImplTest {

    @Rule
    public final UnittestRepositoryRule repoRule = new UnittestRepositoryRule();
    private final EntityManager entityManager = repoRule.getEntityManager();
    private final OppgaveRepositoryProvider repositoryProvider = new OppgaveRepositoryProviderImpl(entityManager);
    private final StatistikkRepository statisikkRepository = repositoryProvider.getStatisikkRepository();
    private StatistikkTjeneste statistikkTjeneste = new StatistikkTjenesteImpl(repositoryProvider);

    private static String AVDELING_DRAMMEN_ENHET = "4806";
    private Avdeling avdelingDrammen = null;

    private Oppgave førstegangOppgave = Oppgave.builder().dummyOppgave(AVDELING_DRAMMEN_ENHET).medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD).build();
    private Oppgave førstegangOppgave2 = Oppgave.builder().dummyOppgave(AVDELING_DRAMMEN_ENHET).medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD).build();
    private Oppgave klageOppgave = Oppgave.builder().dummyOppgave(AVDELING_DRAMMEN_ENHET).medBehandlingType(BehandlingType.KLAGE).build();
    private Oppgave innsynOppgave = Oppgave.builder().dummyOppgave(AVDELING_DRAMMEN_ENHET).medBehandlingType(BehandlingType.INNSYN).build();
    private Oppgave annenAvdeling = Oppgave.builder().dummyOppgave(AVDELING_DRAMMEN_ENHET).medBehandlendeEnhet("5555").medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD).build();
    private Oppgave beslutterOppgave = Oppgave.builder().dummyOppgave(AVDELING_DRAMMEN_ENHET).medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD).build();
    private Oppgave beslutterOppgave2 = Oppgave.builder().dummyOppgave(AVDELING_DRAMMEN_ENHET).medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD).build();
    private Oppgave lukketOppgave = Oppgave.builder().dummyOppgave(AVDELING_DRAMMEN_ENHET).medAktiv(false).medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD).build();

    @Before
    public void setup() {
        List<Avdeling> avdelings = repoRule.getRepository().hentAlle(Avdeling.class);
        avdelingDrammen = avdelings.stream().filter(avdeling -> AVDELING_DRAMMEN_ENHET.equals(avdeling.getAvdelingEnhet())).findFirst().orElseThrow();
    }

    private void leggInnEttSettMedOppgaver(){
        entityManager.persist(førstegangOppgave);
        entityManager.persist(førstegangOppgave2);
        entityManager.persist(klageOppgave);
        entityManager.persist(innsynOppgave);

        entityManager.persist(beslutterOppgave);
        entityManager.persist(new OppgaveEgenskap(beslutterOppgave, AndreKriterierType.TIL_BESLUTTER));

        entityManager.persist(beslutterOppgave2);
        entityManager.persist(new OppgaveEgenskap(beslutterOppgave2, AndreKriterierType.TIL_BESLUTTER));

        entityManager.persist(lukketOppgave);

        entityManager.flush();
    }

    @Test
    public void hentAlleOppgaverForAvdelingTest(){
        leggInnEttSettMedOppgaver();
        List<OppgaverForAvdelingDto>  resultater = statistikkTjeneste.hentAlleOppgaverForAvdeling(AVDELING_DRAMMEN_ENHET)
                .stream().map(resultat -> new OppgaverForAvdelingDto(resultat)).collect(Collectors.toList());
        assertThat(resultater).hasSize(4);
        assertThat(resultater.get(0).getFagsakYtelseType()).isEqualTo(FagsakYtelseType.FORELDREPENGER);
        assertThat(resultater.get(0).getBehandlingType()).isEqualTo(BehandlingType.FØRSTEGANGSSØKNAD);
        assertThat(resultater.get(0).getTilBehandling()).isEqualTo(false);
        assertThat(resultater.get(0).getAntall()).isEqualTo(2L);

        assertThat(resultater.get(1).getFagsakYtelseType()).isEqualTo(FagsakYtelseType.FORELDREPENGER);
        assertThat(resultater.get(1).getBehandlingType()).isEqualTo(BehandlingType.FØRSTEGANGSSØKNAD);
        assertThat(resultater.get(1).getTilBehandling()).isEqualTo(true);
        assertThat(resultater.get(1).getAntall()).isEqualTo(2L);

        assertThat(resultater.get(2).getAntall()).isEqualTo(1L);
        assertThat(resultater.get(3).getAntall()).isEqualTo(1L);
    }


    @Test
    public void hentAntallOppgaverForAvdelingPerDatoTest(){
        leggInnEttSettMedOppgaver();
        List<OppgaverForAvdelingPerDatoDto> resultater = statistikkTjeneste.hentAntallOppgaverForAvdelingPerDato(AVDELING_DRAMMEN_ENHET)
                        .stream().map(resultat -> new OppgaverForAvdelingPerDatoDto(resultat)).collect(Collectors.toList());
        assertThat(resultater).hasSize(3);
        assertThat(resultater.get(0).getFagsakYtelseType()).isEqualTo(FagsakYtelseType.FORELDREPENGER);
        assertThat(resultater.get(0).getBehandlingType()).isEqualTo(BehandlingType.FØRSTEGANGSSØKNAD);
        assertThat(resultater.get(0).getOpprettetDato()).isEqualTo(LocalDate.now());
        assertThat(resultater.get(0).getAntall()).isEqualTo(4L);
        assertThat(resultater.get(1).getAntall()).isEqualTo(1L);
        assertThat(resultater.get(2).getAntall()).isEqualTo(1L);
    }

    @Test
    public void hentAntallOppgaverForAvdelingPerDatoTest2(){
        List<Oppgave> oppgaver = repoRule.getRepository().hentAlle(Oppgave.class);
        leggTilOppgave(førstegangOppgave, 27,27);
        leggTilOppgave(førstegangOppgave2, 28,28);//skal ikke komme i resultatssettet
        leggTilOppgave(annenAvdeling, 10,4);//skal ikke komme i resultatssettet
        leggTilOppgave(klageOppgave, 10, 4);
        List<OppgaverForAvdelingPerDatoDto>  resultater = statistikkTjeneste.hentAntallOppgaverForAvdelingPerDato(AVDELING_DRAMMEN_ENHET)
                        .stream().map(resultat -> new OppgaverForAvdelingPerDatoDto(resultat)).collect(Collectors.toList());
        assertThat(resultater).hasSize(8);
        assertThat(resultater.get(0).getFagsakYtelseType()).isEqualTo(FagsakYtelseType.FORELDREPENGER);
        assertThat(resultater.get(0).getOpprettetDato()).isEqualTo(LocalDate.now().minusDays(27));
        resultater.remove(0);
        resultater.forEach(resultat -> assertThat(resultat.getBehandlingType().equals(BehandlingType.KLAGE)));
    }

    @Test
    public void hentHentStatistikkForManueltPåVent(){
        leggInnEttSettMedOppgaveEventer();
        List<OppgaverForAvdelingSattManueltPaaVentDto> resultater = statistikkTjeneste.hentAntallOppgaverForAvdelingSattManueltPåVent(AVDELING_DRAMMEN_ENHET)
                .stream().map(resultat -> new OppgaverForAvdelingSattManueltPaaVentDto(resultat)).collect(Collectors.toList());
        assertThat(resultater).hasSize(2);
        OppgaverForAvdelingSattManueltPaaVentDto resultatDto = resultater.get(1);
        assertThat(resultatDto.getAntall()).isEqualTo(2L);
        assertThat(resultatDto.getBehandlingFrist()).isEqualTo(LocalDate.now().plusDays(28));
        assertThat(resultatDto.getFagsakYtelseType()).isEqualTo(FagsakYtelseType.FORELDREPENGER);
    }


    @Test
    public void hentOppgaverPerFørsteStønadsdag(){
        leggInnEttSettMedOppgaver();
        List<OppgaverForFørsteStønadsdagDto> resultater = statistikkTjeneste.hentOppgaverPerFørsteStønadsdag(AVDELING_DRAMMEN_ENHET)
                .stream().map(resultat -> new OppgaverForFørsteStønadsdagDto(resultat)).collect(Collectors.toList());
        assertThat(resultater).hasSize(1);
        assertThat(resultater.get(0).getForsteStonadsdag()).isEqualTo(LocalDate.now().plusMonths(1));
        assertThat(resultater.get(0).getAntall()).isEqualTo(6L);
    }


    private void leggInnEttSettMedOppgaveEventer() {

        EksternIdentifikator ekstId1 = repositoryProvider.getEksternIdentifikatorRepository().finnEllerOpprettEksternId("FPSAK","1");
        EksternIdentifikator ekstId2 = repositoryProvider.getEksternIdentifikatorRepository().finnEllerOpprettEksternId("FPSAK","2");
        EksternIdentifikator ekstId3 = repositoryProvider.getEksternIdentifikatorRepository().finnEllerOpprettEksternId("FPSAK","3");
        EksternIdentifikator ekstId4 = repositoryProvider.getEksternIdentifikatorRepository().finnEllerOpprettEksternId("FPSAK","4");
        EksternIdentifikator ekstId5 = repositoryProvider.getEksternIdentifikatorRepository().finnEllerOpprettEksternId("FPSAK","5");
        EksternIdentifikator ekstId6 = repositoryProvider.getEksternIdentifikatorRepository().finnEllerOpprettEksternId("FPSAK","6");
        entityManager.flush();

        entityManager.persist(Oppgave.builder().dummyOppgave(AVDELING_DRAMMEN_ENHET).medBehandlingId(1L).medSystem("FPSAK").medEksternId(ekstId1.getId()).build());
        entityManager.persist(Oppgave.builder().dummyOppgave(AVDELING_DRAMMEN_ENHET).medBehandlingId(2L).medSystem("FPSAK").medEksternId(ekstId2.getId()).build());
        entityManager.persist(Oppgave.builder().dummyOppgave(AVDELING_DRAMMEN_ENHET).medBehandlingId(3L).medSystem("FPSAK").medEksternId(ekstId3.getId()).build());
        entityManager.persist(Oppgave.builder().dummyOppgave(AVDELING_DRAMMEN_ENHET).medBehandlingId(4L).medSystem("FPSAK").medEksternId(ekstId4.getId()).build());
        entityManager.persist(Oppgave.builder().dummyOppgave(AVDELING_DRAMMEN_ENHET).medBehandlingId(5L).medSystem("FPSAK").medEksternId(ekstId5.getId()).build());
        entityManager.persist(Oppgave.builder().dummyOppgave(AVDELING_DRAMMEN_ENHET).medFagsakYtelseType(FagsakYtelseType.ENGANGSTØNAD).medBehandlingId(6L).medSystem("FPSAK").medEksternId(ekstId6.getId()).build());

        entityManager.persist(new OppgaveEventLogg(1L, ekstId1.getId(), OppgaveEventType.OPPRETTET,AndreKriterierType.UKJENT, AVDELING_DRAMMEN_ENHET));
        entityManager.persist(new OppgaveEventLogg(2L, ekstId2.getId(), OppgaveEventType.OPPRETTET,AndreKriterierType.UKJENT, AVDELING_DRAMMEN_ENHET));
        entityManager.persist(new OppgaveEventLogg(3L, ekstId3.getId(), OppgaveEventType.OPPRETTET,AndreKriterierType.UKJENT, AVDELING_DRAMMEN_ENHET));
        entityManager.persist(new OppgaveEventLogg(4L, ekstId4.getId(), OppgaveEventType.MANU_VENT,AndreKriterierType.UKJENT, AVDELING_DRAMMEN_ENHET));
        entityManager.persist(new OppgaveEventLogg(5L, ekstId5.getId(), OppgaveEventType.GJENAPNET,AndreKriterierType.UKJENT, AVDELING_DRAMMEN_ENHET));

        //for å ungå samtidighetsproblemer med opprettettidspunkt
        entityManager.flush();
        entityManager.createNativeQuery("UPDATE OPPGAVE_EVENT_LOGG oel SET oel.OPPRETTET_TID = SYSDATE-1").executeUpdate();
        entityManager.flush();
        repoRule.getRepository().hentAlle(OppgaveEventLogg.class).forEach(oppgave -> entityManager.refresh(oppgave));

        entityManager.persist(new OppgaveEventLogg(1L, ekstId1.getId(), OppgaveEventType.LUKKET,AndreKriterierType.UKJENT, AVDELING_DRAMMEN_ENHET));
        entityManager.persist(new OppgaveEventLogg(2L, ekstId2.getId(), OppgaveEventType.MANU_VENT,AndreKriterierType.UKJENT, AVDELING_DRAMMEN_ENHET));
        entityManager.persist(new OppgaveEventLogg(3L, ekstId3.getId(), OppgaveEventType.VENT,AndreKriterierType.UKJENT, AVDELING_DRAMMEN_ENHET));
        entityManager.persist(new OppgaveEventLogg(4L, ekstId4.getId(), OppgaveEventType.OPPRETTET,AndreKriterierType.UKJENT, AVDELING_DRAMMEN_ENHET));
        entityManager.persist(new OppgaveEventLogg(5L, ekstId5.getId(), OppgaveEventType.MANU_VENT,AndreKriterierType.UKJENT, AVDELING_DRAMMEN_ENHET));
        entityManager.persist(new OppgaveEventLogg(6L, ekstId6.getId(), OppgaveEventType.MANU_VENT,AndreKriterierType.UKJENT, AVDELING_DRAMMEN_ENHET));

        entityManager.flush();
    }

    private void leggTilOppgave(Oppgave oppgave, int startTilbakeITid, int sluttTilbakeITid) {
        entityManager.persist(oppgave);
        entityManager.flush();
        entityManager.createNativeQuery("UPDATE OPPGAVE " +
                "SET OPPRETTET_TID = (sysdate - "+startTilbakeITid +"), " +
                "ENDRET_TID = (sysdate - "+sluttTilbakeITid+"), " +
                "AKTIV = 'N' " +
                "WHERE ID = " + oppgave.getId()).executeUpdate();
    }
}
