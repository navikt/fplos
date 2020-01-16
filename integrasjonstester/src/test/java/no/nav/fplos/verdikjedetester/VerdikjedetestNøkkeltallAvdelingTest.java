package no.nav.fplos.verdikjedetester;

import no.nav.foreldrepenger.dbstoette.UnittestRepositoryRule;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.dto.AvdelingEnhetDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.nøkkeltall.NøkkeltallRestTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.nøkkeltall.dto.OppgaverForAvdelingSattManueltPaaVentDto;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepository;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepositoryImpl;
import no.nav.foreldrepenger.loslager.repository.StatistikkRepository;
import no.nav.foreldrepenger.loslager.repository.StatistikkRepositoryImpl;
import no.nav.fplos.foreldrepengerbehandling.Aksjonspunkt;
import no.nav.fplos.foreldrepengerbehandling.BehandlingFpsak;
import no.nav.fplos.foreldrepengerbehandling.ForeldrepengerBehandlingRestKlient;
import no.nav.fplos.kafkatjenester.AksjonspunktMeldingConsumer;
import no.nav.fplos.kafkatjenester.FpsakEventHandler;
import no.nav.fplos.kafkatjenester.KafkaReader;
import no.nav.fplos.kafkatjenester.TilbakekrevingEventHandler;
import no.nav.fplos.statistikk.StatistikkTjeneste;
import no.nav.fplos.statistikk.StatistikkTjenesteImpl;
import no.nav.fplos.verdikjedetester.mock.AksjonspunkteventTestInfo;
import no.nav.fplos.verdikjedetester.mock.MockEventKafkaMessages;
import no.nav.vedtak.felles.testutilities.cdi.CdiRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static no.nav.fplos.verdikjedetester.mock.MockEventKafkaMessages.BEHANDLENDE_ENHET;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(CdiRunner.class)
public class VerdikjedetestNøkkeltallAvdelingTest {

    private static final LocalDateTime NOW = LocalDateTime.now();
    @Rule
    public UnittestRepositoryRule repoRule = new UnittestRepositoryRule();
    private EntityManager entityManager = repoRule.getEntityManager();
    private final OppgaveRepository oppgaveRepository = new OppgaveRepositoryImpl(entityManager);
    private final StatistikkRepository statisikkRepository = new StatistikkRepositoryImpl(entityManager);
    private StatistikkTjeneste statistikkTjeneste = new StatistikkTjenesteImpl(statisikkRepository);
    private NøkkeltallRestTjeneste nøkkeltallRestTjeneste = new NøkkeltallRestTjeneste(statistikkTjeneste);
    private ForeldrepengerBehandlingRestKlient foreldrepengerBehandlingRestKlient = mock(ForeldrepengerBehandlingRestKlient.class);
    @Inject
    private AksjonspunktMeldingConsumer meldingConsumer;
    private KafkaReader kafkaReader = null;

    private LocalDateTime aksjonspunktFristTom = null;

    @Before
    public void before() {
        kafkaReader = new KafkaReader(meldingConsumer,
                new FpsakEventHandler(oppgaveRepository, foreldrepengerBehandlingRestKlient),
                new TilbakekrevingEventHandler(oppgaveRepository),
                oppgaveRepository);
    }

    @Test
    public void manuellSattPåVentVisesRiktigeDatoer(){
        /*TODO: Må fikse denne etter vi har UUID fra BehandlingProsessEventDto*/

        Aksjonspunkt.Builder builder1 = Aksjonspunkt.builder();
        Aksjonspunkt aksjonspunkt = builder1.medDefinisjon("5025").medStatus("OPPR").build();
        initialiserForeldrepengerBehandlingRestKlient(aksjonspunkt);
        //when(foreldrepengerBehandlingRestKlient.getBehandling(anyLong())).thenReturn(lagBehandlingDto(Collections.singletonList(aksjonspunkt)));
        Map<Long, AksjonspunkteventTestInfo> melding = MockEventKafkaMessages.førstegangsbehandlingMeldinger;
        MockEventKafkaMessages.sendNyeOppgaver(melding);
        kafkaReader.hentOgLagreMeldingene();

        Aksjonspunkt aksjonspunktDtoMedManuellSattPaaVentUtenFrist = Aksjonspunkt.builder()
                .medDefinisjon("7001")
                .medStatus("OPPR")
                .medFristTid(aksjonspunktFristTom)
                .build();
        Aksjonspunkt aksjonspunktDtoMedManuellSattPaaVentOgFrist = Aksjonspunkt.builder()
                .medDefinisjon("7001")
                .medStatus("OPPR")
                .medFristTid(NOW.plusDays(10))
                .build();
        //initialiserForeldrepengerBehandlingRestKlient(aksjonspunktDtoMedManuellSattPaaVentUtenFrist);
        when(foreldrepengerBehandlingRestKlient.getBehandling(MockEventKafkaMessages.BEHANDLING_ID_1))
                .thenReturn(lagBehandlingDto(Collections.singletonList(aksjonspunktDtoMedManuellSattPaaVentOgFrist),UUID.nameUUIDFromBytes("1".getBytes())));
        when(foreldrepengerBehandlingRestKlient.getBehandling(MockEventKafkaMessages.BEHANDLING_ID_2))
                .thenReturn(lagBehandlingDto(Collections.singletonList(aksjonspunktDtoMedManuellSattPaaVentUtenFrist),UUID.nameUUIDFromBytes("2".getBytes())));

        Map<Long, AksjonspunkteventTestInfo> meldingSattPaaVent = MockEventKafkaMessages.førstegangsbehandlingMeldinger;
        MockEventKafkaMessages.sendNyeOppgaver(meldingSattPaaVent);
        kafkaReader.hentOgLagreMeldingene();

        List<OppgaverForAvdelingSattManueltPaaVentDto> antallOppgaverSattPåManuellVentForAvdeling = nøkkeltallRestTjeneste.getAntallOppgaverSattPåManuellVentForAvdeling(new AvdelingEnhetDto(BEHANDLENDE_ENHET));
        assertThat(antallOppgaverSattPåManuellVentForAvdeling).extracting(OppgaverForAvdelingSattManueltPaaVentDto::getBehandlingFrist)
                .containsExactlyInAnyOrder(NOW.toLocalDate().plusDays(10), NOW.toLocalDate().plusDays(28));
    }
    private void initialiserForeldrepengerBehandlingRestKlient(Aksjonspunkt aksjonspunktDto) {
        doReturn(lagBehandlingDto(Collections.singletonList(aksjonspunktDto), UUID.nameUUIDFromBytes("1".getBytes()))).when(foreldrepengerBehandlingRestKlient).getBehandling(1L);
        doReturn(lagBehandlingDto(Collections.singletonList(aksjonspunktDto),UUID.nameUUIDFromBytes("2".getBytes()))).when(foreldrepengerBehandlingRestKlient).getBehandling(2L);
        doReturn(lagBehandlingDto(Collections.singletonList(aksjonspunktDto),UUID.nameUUIDFromBytes("3".getBytes()))).when(foreldrepengerBehandlingRestKlient).getBehandling(3L);
        doReturn(lagBehandlingDto(Collections.singletonList(aksjonspunktDto),UUID.nameUUIDFromBytes("4".getBytes()))).when(foreldrepengerBehandlingRestKlient).getBehandling(4L);
        doReturn(lagBehandlingDto(Collections.singletonList(aksjonspunktDto),UUID.nameUUIDFromBytes("5".getBytes()))).when(foreldrepengerBehandlingRestKlient).getBehandling(5L);
        doReturn(lagBehandlingDto(Collections.singletonList(aksjonspunktDto),UUID.nameUUIDFromBytes("6".getBytes()))).when(foreldrepengerBehandlingRestKlient).getBehandling(6L);
        doReturn(lagBehandlingDto(Collections.singletonList(aksjonspunktDto),UUID.nameUUIDFromBytes("7".getBytes()))).when(foreldrepengerBehandlingRestKlient).getBehandling(7L);
        doReturn(lagBehandlingDto(Collections.singletonList(aksjonspunktDto),UUID.nameUUIDFromBytes("8".getBytes()))).when(foreldrepengerBehandlingRestKlient).getBehandling(8L);
    }

    private BehandlingFpsak lagBehandlingDto(List<Aksjonspunkt> aksjonspunkter, UUID uuid){
        return BehandlingFpsak.builder()
                .medUuid(uuid)
                .medBehandlendeEnhetNavn("NAV")
                .medAnsvarligSaksbehandler("VLLOS")
                .medStatus("-")
                .medHarRefusjonskravFraArbeidsgiver(false)
                .medAksjonspunkter(aksjonspunkter)
                .build();
    }
}
