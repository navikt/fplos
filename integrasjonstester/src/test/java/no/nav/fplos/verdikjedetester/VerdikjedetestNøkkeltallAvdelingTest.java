package no.nav.fplos.verdikjedetester;

import no.nav.foreldrepenger.dbstoette.UnittestRepositoryRule;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.dto.AvdelingEnhetDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.nøkkeltall.NøkkeltallRestTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.nøkkeltall.dto.OppgaverForAvdelingSattManueltPaaVentDto;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepositoryProvider;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepositoryProviderImpl;
import no.nav.fplos.foreldrepengerbehandling.BehandlingFpsak;
import no.nav.fplos.foreldrepengerbehandling.ForeldrepengerBehandlingRestKlient;
import no.nav.fplos.foreldrepengerbehandling.dto.aksjonspunkt.AksjonspunktDto;

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

import static no.nav.fplos.verdikjedetester.mock.MockEventKafkaMessages.BEHANDLENDE_ENHET;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(CdiRunner.class)
public class VerdikjedetestNøkkeltallAvdelingTest {

    private static final LocalDateTime NOW = LocalDateTime.now();
    @Rule
    public UnittestRepositoryRule repoRule = new UnittestRepositoryRule();
    private EntityManager entityManager = repoRule.getEntityManager();
    private OppgaveRepositoryProvider oppgaveRepositoryProvider = new OppgaveRepositoryProviderImpl(entityManager );
    private StatistikkTjeneste statistikkTjeneste = new StatistikkTjenesteImpl(oppgaveRepositoryProvider);
    private NøkkeltallRestTjeneste nøkkeltallRestTjeneste = new NøkkeltallRestTjeneste(statistikkTjeneste);
    private ForeldrepengerBehandlingRestKlient foreldrepengerBehandlingRestKlient = mock(ForeldrepengerBehandlingRestKlient.class);
    @Inject
    private AksjonspunktMeldingConsumer meldingConsumer;
    private KafkaReader kafkaReader = null;

    private LocalDateTime aksjonspunktFristTom = null;

    @Before
    public void before() {
        kafkaReader = new KafkaReader(meldingConsumer,
                new FpsakEventHandler(oppgaveRepositoryProvider, foreldrepengerBehandlingRestKlient),
                new TilbakekrevingEventHandler(oppgaveRepositoryProvider),
                oppgaveRepositoryProvider);
    }

    @Test
    public void manuellSattPåVentVisesRiktigeDatoer(){
        AksjonspunktDto.Builder builder1 = new AksjonspunktDto.Builder();
        AksjonspunktDto aksjonspunktDto = builder1.medDefinisjon("5025").medStatus("OPPR").build();
        when(foreldrepengerBehandlingRestKlient.getBehandling(anyLong())).thenReturn(lagBehandlingDto(Collections.singletonList(aksjonspunktDto)));
        Map<Long, AksjonspunkteventTestInfo> melding = MockEventKafkaMessages.førstegangsbehandlingMeldinger;
        MockEventKafkaMessages.sendNyeOppgaver(melding);
        kafkaReader.hentOgLagreMeldingene();

        AksjonspunktDto aksjonspunktDtoMedManuellSattPaaVentUtenFrist = new AksjonspunktDto.Builder().medDefinisjon("7001")
                .medStatus("OPPR").medFristTid(aksjonspunktFristTom).build();
        AksjonspunktDto aksjonspunktDtoMedManuellSattPaaVentOgFrist = new AksjonspunktDto.Builder().medDefinisjon("7001")
                .medStatus("OPPR").medFristTid(NOW.plusDays(10)).build();
        when(foreldrepengerBehandlingRestKlient.getBehandling(MockEventKafkaMessages.BEHANDLING_ID_1))
                .thenReturn(lagBehandlingDto(Collections.singletonList(aksjonspunktDtoMedManuellSattPaaVentOgFrist)));
        when(foreldrepengerBehandlingRestKlient.getBehandling(MockEventKafkaMessages.BEHANDLING_ID_2))
                .thenReturn(lagBehandlingDto(Collections.singletonList(aksjonspunktDtoMedManuellSattPaaVentUtenFrist)));

        Map<Long, AksjonspunkteventTestInfo> meldingSattPaaVent = MockEventKafkaMessages.førstegangsbehandlingMeldinger;
        MockEventKafkaMessages.sendNyeOppgaver(meldingSattPaaVent);
        kafkaReader.hentOgLagreMeldingene();

        List<OppgaverForAvdelingSattManueltPaaVentDto> antallOppgaverSattPåManuellVentForAvdeling = nøkkeltallRestTjeneste.getAntallOppgaverSattPåManuellVentForAvdeling(new AvdelingEnhetDto(BEHANDLENDE_ENHET));
        assertThat(antallOppgaverSattPåManuellVentForAvdeling).extracting(OppgaverForAvdelingSattManueltPaaVentDto::getBehandlingFrist)
                .containsExactlyInAnyOrder(NOW.toLocalDate().plusDays(10), NOW.toLocalDate().plusDays(28));
    }

    private BehandlingFpsak lagBehandlingDto(List<AksjonspunktDto> aksjonspunktDtoer){
        return BehandlingFpsak.builder()
                .medBehandlendeEnhetNavn("NAV")
                .medAnsvarligSaksbehandler("VLLOS")
                .medStatus("-")
                .medHarRefusjonskrav(false)
                .medAksjonspunkter(aksjonspunktDtoer)
                .build();
    }
}
