package no.nav.fplos.verdikjedetester;

import no.nav.foreldrepenger.dbstoette.UnittestRepositoryRule;
import no.nav.foreldrepenger.domene.typer.AktørId;
import no.nav.foreldrepenger.domene.typer.PersonIdent;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.AvdelingslederRestTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.dto.AvdelingDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.dto.AvdelingEnhetDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.dto.SaksbehandlerOgAvdelingDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksbehandler.AvdelingslederSaksbehandlerRestTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.AvdelingslederSakslisteRestTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.dto.SakslisteBehandlingstypeDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.dto.SakslisteFagsakYtelseTypeDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.dto.SakslisteSaksbehandlerDto;
import no.nav.foreldrepenger.los.web.app.tjenester.fagsak.app.FagsakApplikasjonTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.OppgaveDto;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SaksbehandlerBrukerIdentDto;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SakslisteIdDto;
import no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.OppgaveRestTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.dto.OppgaveIdDto;
import no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.dto.OppgaveOpphevingDto;
import no.nav.foreldrepenger.loslager.aktør.TpsPersonDto;
import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;
import no.nav.foreldrepenger.loslager.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.loslager.oppgave.Reservasjon;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepository;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepositoryImpl;
import no.nav.foreldrepenger.loslager.repository.OrganisasjonRepository;
import no.nav.foreldrepenger.loslager.repository.OrganisasjonRepositoryImpl;
import no.nav.fplos.ansatt.AnsattTjeneste;
import no.nav.fplos.avdelingsleder.AvdelingslederSaksbehandlerTjenesteImpl;
import no.nav.fplos.avdelingsleder.AvdelingslederTjeneste;
import no.nav.fplos.avdelingsleder.AvdelingslederTjenesteImpl;
import no.nav.fplos.domene.organisasjonsinformasjon.organisasjonressursenhet.impl.OrganisasjonRessursEnhetTjenesteImpl;
import no.nav.fplos.foreldrepengerbehandling.Aksjonspunkt;
import no.nav.fplos.foreldrepengerbehandling.BehandlingFpsak;
import no.nav.fplos.foreldrepengerbehandling.ForeldrepengerBehandlingRestKlient;
import no.nav.fplos.kafkatjenester.AksjonspunktMeldingConsumer;
import no.nav.fplos.kafkatjenester.FpsakEventHandler;
import no.nav.fplos.kafkatjenester.KafkaReader;
import no.nav.fplos.kafkatjenester.OppgaveEgenskapHandler;
import no.nav.fplos.kafkatjenester.TilbakekrevingEventHandler;
import no.nav.fplos.oppgave.OppgaveTjenesteImpl;
import no.nav.fplos.person.api.TpsTjeneste;
import no.nav.fplos.verdikjedetester.mock.AksjonspunkteventTestInfo;
import no.nav.fplos.verdikjedetester.mock.MockEventKafkaMessages;
import no.nav.fplos.verdikjedetester.mock.MockKafkaMessages;
import no.nav.vedtak.felles.testutilities.cdi.CdiRunner;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static java.time.temporal.ChronoUnit.MINUTES;
import static no.nav.foreldrepenger.loslager.BaseEntitet.BRUKERNAVN_NÅR_SIKKERHETSKONTEKST_IKKE_FINNES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(CdiRunner.class)
public class VerdikjedetestEventhåndteringSaksbehandlerTest {

    @Rule
    public UnittestRepositoryRule repoRule = new UnittestRepositoryRule();
    private EntityManager entityManager = repoRule.getEntityManager();
    private OppgaveRepository oppgaveRepository = new OppgaveRepositoryImpl(entityManager);
    private OppgaveEgenskapHandler oppgaveEgenskapHandler = new OppgaveEgenskapHandler(oppgaveRepository);
    private OrganisasjonRepository organisasjonRepository = new OrganisasjonRepositoryImpl(entityManager);
    private TpsTjeneste tpsTjeneste = mock(TpsTjeneste.class);
    private AvdelingslederTjeneste avdelingslederTjeneste = mock(AvdelingslederTjeneste.class);
    private AnsattTjeneste ansattTjeneste = mock(AnsattTjeneste.class);
    private FagsakApplikasjonTjeneste fagsakApplikasjonTjeneste = mock(FagsakApplikasjonTjeneste.class);
    private OppgaveRestTjeneste oppgaveRestTjeneste = new OppgaveRestTjeneste(new OppgaveTjenesteImpl(oppgaveRepository, organisasjonRepository, tpsTjeneste, avdelingslederTjeneste, ansattTjeneste), fagsakApplikasjonTjeneste);
    private AvdelingslederRestTjeneste avdelingslederRestTjeneste = new AvdelingslederRestTjeneste(new AvdelingslederTjenesteImpl(oppgaveRepository, organisasjonRepository));
    private AvdelingslederSakslisteRestTjeneste avdelingslederSakslisteRestTjeneste = new AvdelingslederSakslisteRestTjeneste(
            new AvdelingslederTjenesteImpl(oppgaveRepository, organisasjonRepository), new OppgaveTjenesteImpl(oppgaveRepository, organisasjonRepository, tpsTjeneste, avdelingslederTjeneste, ansattTjeneste));
    private AvdelingslederSaksbehandlerRestTjeneste avdelingslederSaksbehandlerRestTjeneste =
            new AvdelingslederSaksbehandlerRestTjeneste(new AvdelingslederSaksbehandlerTjenesteImpl(oppgaveRepository, organisasjonRepository, new OrganisasjonRessursEnhetTjenesteImpl()));
    private ForeldrepengerBehandlingRestKlient foreldrepengerBehandlingRestKlient = mock(ForeldrepengerBehandlingRestKlient.class);


    @Inject
    private AksjonspunktMeldingConsumer meldingConsumer;
    private KafkaReader kafkaReader = null;
    private AvdelingDto avdelingDrammen = null;
    private String AVDELING_DRAMMEN = "4806";
    private String SAKSBEHANDLER_IDENT = BRUKERNAVN_NÅR_SIKKERHETSKONTEKST_IKKE_FINNES;
    private SakslisteIdDto sakslisteDrammenFPFørstegangsIdDto;
    private LocalDateTime aksjonspunktFrist = null;

    @Before
    public void before(){
        kafkaReader = new KafkaReader(meldingConsumer,
                new FpsakEventHandler(oppgaveRepository, foreldrepengerBehandlingRestKlient, oppgaveEgenskapHandler),
                new TilbakekrevingEventHandler(oppgaveRepository),
                oppgaveRepository);
        avdelingDrammen = avdelingslederRestTjeneste.hentAvdelinger().stream()
                .filter(avdeling -> AVDELING_DRAMMEN.equals(avdeling.getAvdelingEnhet()))
                .findFirst().orElseThrow();
        sakslisteDrammenFPFørstegangsIdDto = avdelingslederSakslisteRestTjeneste.opprettNySaksliste(new AvdelingEnhetDto(avdelingDrammen.getAvdelingEnhet()));
        avdelingslederSakslisteRestTjeneste.lagreFagsakYtelseType(new SakslisteFagsakYtelseTypeDto(sakslisteDrammenFPFørstegangsIdDto, FagsakYtelseType.FORELDREPENGER,new AvdelingEnhetDto(avdelingDrammen.getAvdelingEnhet())));
        avdelingslederSakslisteRestTjeneste.lagreBehandlingstype(new SakslisteBehandlingstypeDto(sakslisteDrammenFPFørstegangsIdDto, BehandlingType.FØRSTEGANGSSØKNAD, true,new AvdelingEnhetDto(avdelingDrammen.getAvdelingEnhet())));
        avdelingslederSaksbehandlerRestTjeneste.leggTilNySaksbehandler(new SaksbehandlerOgAvdelingDto(new SaksbehandlerBrukerIdentDto(SAKSBEHANDLER_IDENT), new AvdelingEnhetDto(avdelingDrammen.getAvdelingEnhet())));
        avdelingslederSakslisteRestTjeneste.leggSaksbehandlerTilSaksliste(new SakslisteSaksbehandlerDto(sakslisteDrammenFPFørstegangsIdDto, new SaksbehandlerBrukerIdentDto(SAKSBEHANDLER_IDENT),true, new AvdelingEnhetDto(avdelingDrammen.getAvdelingEnhet())));

        lagEnkeltMockResultatForTpsTjenesten();
        MockKafkaMessages.clearMessages();
        MockEventKafkaMessages.clearMessages();
    }

    @Test
    public void saksbehandlerRutingReservasjon(){
        Map<Long, AksjonspunkteventTestInfo> melding = MockEventKafkaMessages.defaultførstegangsbehandlingMelding;
        MockEventKafkaMessages.sendNyeOppgaver(melding);

        Aksjonspunkt.Builder builder = Aksjonspunkt.builder();
        Aksjonspunkt aksjonspunktDto = builder.medDefinisjon("5025").medStatus("OPPR").medFristTid(aksjonspunktFrist).build();

        initialiserForeldrepengerBehandlingRestKlient(aksjonspunktDto);
        //when(foreldrepengerBehandlingRestKlient.getBehandling(anyLong())).thenReturn(lagBehandlingDto(Collections.singletonList(aksjonspunktDto)));
        kafkaReader.hentOgLagreMeldingene();

        OppgaveDto oppgaveDto = verifiserFinnesISaksliste(melding, sakslisteDrammenFPFørstegangsIdDto);
        verifiserAtReservertErTom();

        oppgaveRestTjeneste.reserverOppgave(new OppgaveIdDto(oppgaveDto.getId()));

        verifiserAtSakslisteErTom(sakslisteDrammenFPFørstegangsIdDto);
        assertThat(verifiserAtErReservert(melding).getStatus().getReservertTilTidspunkt().until(LocalDateTime.now().plusHours(2), MINUTES)).isLessThan(2L);

        oppgaveRestTjeneste.forlengOppgaveReservasjon(new OppgaveIdDto(oppgaveDto.getId()));

        assertThat(verifiserAtErReservert(melding).getStatus().getReservertTilTidspunkt().until(LocalDateTime.now().plusHours(24), MINUTES)).isLessThan(2L);

        oppgaveRestTjeneste.opphevOppgaveReservasjon(new OppgaveOpphevingDto(new OppgaveIdDto(oppgaveDto.getId()),"Begrunnelse"));

        verifiserFinnesISaksliste(melding, sakslisteDrammenFPFørstegangsIdDto);
        verifiserAtReservertErTom();
    }

    @Test
    public void saksbehandlerForskjelligeReservasjonslister(){
        Map<Long, AksjonspunkteventTestInfo> melding = MockEventKafkaMessages.defaultførstegangsbehandlingMelding;
        MockEventKafkaMessages.sendNyeOppgaver(melding);
        Aksjonspunkt.Builder builder = Aksjonspunkt.builder();
        Aksjonspunkt aksjonspunktDto = builder.medDefinisjon("5025").medStatus("OPPR").medFristTid(aksjonspunktFrist).build();
        initialiserForeldrepengerBehandlingRestKlient(aksjonspunktDto);
        //when(foreldrepengerBehandlingRestKlient.getBehandling(anyLong())).thenReturn(lagBehandlingDto(Collections.singletonList(aksjonspunktDto)));
        kafkaReader.hentOgLagreMeldingene();

        OppgaveDto oppgaveDto = verifiserFinnesISaksliste(melding, sakslisteDrammenFPFørstegangsIdDto);
        oppgaveRestTjeneste.reserverOppgave(new OppgaveIdDto(oppgaveDto.getId()));

        verifiserAtErReservert(melding);

        //Forandrer alle reservasjonene til ANNEN_IDENT
        entityManager.createNativeQuery("UPDATE RESERVASJON r set r.RESERVERT_AV ='ANNEN_IDENT'").executeUpdate();

        verifiserAtReservertErTom();
        verifiserAtSakslisteErTom(sakslisteDrammenFPFørstegangsIdDto);
    }

    @Ignore
    @Test
    public void saksbehandlerIkkeFårOppSinEgenOppgaveNårTilBeslutter(){
        Map<Long, AksjonspunkteventTestInfo> melding = MockEventKafkaMessages.defaultførstegangsbehandlingMelding;
        MockEventKafkaMessages.sendNyeOppgaver(melding);
        MockEventKafkaMessages.sendNyeOppgaver(MockEventKafkaMessages.tilBeslutter);

        initialiserForeldrepengerBehandlingRestKlient(null);
        //when(foreldrepengerBehandlingRestKlient.getBehandling(anyLong())).thenReturn(lagBehandlingDto(Collections.singletonList(null)));

        kafkaReader.hentOgLagreMeldingene();

        verifiserFinnesISaksliste(melding, sakslisteDrammenFPFørstegangsIdDto);
    }


    @Test
    public void saksbehandlerFårOppAndresOppgaveNårTilBeslutter(){
        Map<Long, AksjonspunkteventTestInfo> melding = MockEventKafkaMessages.tilBeslutter;
        MockEventKafkaMessages.sendNyeOppgaver(melding);

        Aksjonspunkt.Builder builder = Aksjonspunkt.builder();
        Aksjonspunkt aksjonspunktDto = builder.medDefinisjon("5025").medStatus("OPPR").medFristTid(aksjonspunktFrist).build();
        initialiserForeldrepengerBehandlingRestKlientMedAnnenbruker(aksjonspunktDto);
        //when(foreldrepengerBehandlingRestKlient.getBehandling(anyLong())).thenReturn(lagBehandlingAnnenBrukerDto(Collections.singletonList(aksjonspunktDto)));
        kafkaReader.hentOgLagreMeldingene();

        verifiserFinnesISaksliste(melding, sakslisteDrammenFPFørstegangsIdDto);
    }

    @Test
    public void sakbehandlerBytteListe() {
        //Prepare
        SakslisteIdDto sakslisteInnsynIdDto = avdelingslederSakslisteRestTjeneste.opprettNySaksliste(new AvdelingEnhetDto(avdelingDrammen.getAvdelingEnhet()));
        avdelingslederSakslisteRestTjeneste.lagreBehandlingstype(new SakslisteBehandlingstypeDto(sakslisteInnsynIdDto, BehandlingType.INNSYN, true,new AvdelingEnhetDto(avdelingDrammen.getAvdelingEnhet())));

        //Act
        MockEventKafkaMessages.sendNyeOppgaver(MockEventKafkaMessages.førstegangsbehandlingMeldinger);
        MockEventKafkaMessages.sendNyeOppgaver(MockEventKafkaMessages.innsynMeldinger);
        Aksjonspunkt.Builder builder = Aksjonspunkt.builder();
        Aksjonspunkt aksjonspunktDto = builder.medDefinisjon("5025").medStatus("OPPR").medFristTid(aksjonspunktFrist).build();
        initialiserForeldrepengerBehandlingRestKlient(aksjonspunktDto);
        //when(foreldrepengerBehandlingRestKlient.getBehandling(anyLong())).thenReturn(lagBehandlingDto(Collections.singletonList(aksjonspunktDto)));
        kafkaReader.hentOgLagreMeldingene();

        verifiserFinnesISaksliste(MockEventKafkaMessages.førstegangsbehandlingMeldinger, sakslisteDrammenFPFørstegangsIdDto);
        verifiserFinnesISaksliste(MockEventKafkaMessages.innsynMeldinger, sakslisteInnsynIdDto);
    }

    @Test
    public void sakbehandlerInntillTiSisteBehandledeOppgaver() {
        SakslisteIdDto sakslisteIdDto = avdelingslederSakslisteRestTjeneste.opprettNySaksliste(new AvdelingEnhetDto(avdelingDrammen.getAvdelingEnhet()));
        MockEventKafkaMessages.sendNyeOppgaver(MockEventKafkaMessages.førstegangsbehandlingMeldinger);
        MockEventKafkaMessages.sendNyeOppgaver(MockEventKafkaMessages.innsynMeldinger);
        Aksjonspunkt.Builder builder = Aksjonspunkt.builder();
        Aksjonspunkt aksjonspunktDto = builder.medDefinisjon("5025").medStatus("OPPR").medFristTid(aksjonspunktFrist).build();
        initialiserForeldrepengerBehandlingRestKlient(aksjonspunktDto);
        //when(foreldrepengerBehandlingRestKlient.getBehandling(anyLong())).thenReturn(lagBehandlingDto(Collections.singletonList(aksjonspunktDto)));
        kafkaReader.hentOgLagreMeldingene();
        assertThat(oppgaveRestTjeneste.getBehandledeOppgaver()).hasSize(0);

        //Reserverer 3 oppgaver
        List<OppgaveDto> oppgaverTilBehandling = oppgaveRestTjeneste.getOppgaverTilBehandling(sakslisteIdDto);
        oppgaverTilBehandling.forEach(oppgaveDto -> oppgaveRestTjeneste.reserverOppgave(new OppgaveIdDto(oppgaveDto.getId())));
        assertThat(oppgaveRestTjeneste.getBehandledeOppgaver()).hasSize(3);
        oppgaverTilBehandling.forEach(oppgaveDto -> assertThat(oppgaveDto.getErTilSaksbehandling()));

        oppgaverTilBehandling.containsAll(oppgaveRestTjeneste.getReserverteOppgaver());

        //for å ungå samtidighetsproblemer med opprettettidspunkt (flytter reservasjonen 1 time tidligere).
        entityManager.flush();
        entityManager.createNativeQuery("UPDATE RESERVASJON res SET res.OPPRETTET_TID = SYSDATE-(1/24)").executeUpdate();
        entityManager.flush();
        repoRule.getRepository().hentAlle(Reservasjon.class).forEach(reservasjon -> entityManager.refresh(reservasjon));

        oppgaverTilBehandling.containsAll(oppgaveRestTjeneste.getReserverteOppgaver());

        //Henter ut oppgaver som ikke enda er reserverte
        OppgaveDto oppgaveDto = oppgaveRestTjeneste.getOppgaverTilBehandling(sakslisteIdDto).get(0);
        oppgaveRestTjeneste.reserverOppgave(new OppgaveIdDto(oppgaveDto.getId()));


        //sjekker at det forsatt bare er tre oppgaver i tre siste oppgaver og den nyest reserverte er på toppen
        assertThat(oppgaveRestTjeneste.getBehandledeOppgaver()).hasSize(4);
        List<OppgaveDto> behandledeOppgaver = oppgaveRestTjeneste.getBehandledeOppgaver();
        assertThat(behandledeOppgaver.get(0).getId()).isEqualTo(oppgaveDto.getId());
    }

    @Test
    public void lukkeFerdigeAksjonspunkt(){
        /*TODO: Må fikse denne når vi har UUID fra BehandlingProsessEventDto*/

        Map<Long, AksjonspunkteventTestInfo> melding = MockEventKafkaMessages.førstegangsbehandlingMeldinger;
        MockEventKafkaMessages.sendNyeOppgaver(melding);
        Aksjonspunkt.Builder builder1 = Aksjonspunkt.builder();
        Aksjonspunkt aksjonspunktDto1 = builder1.medDefinisjon("5025").medStatus("OPPR").medFristTid(aksjonspunktFrist).build();
        initialiserForeldrepengerBehandlingRestKlient(aksjonspunktDto1);
        // when(foreldrepengerBehandlingRestKlient.get(anyLong())).thenReturn(lagBehandlingDto(Collections.singletonList(aksjonspunktDto1)));
        kafkaReader.hentOgLagreMeldingene();

        OppgaveDto oppgaveDto = verifiserFinnesISaksliste(melding, sakslisteDrammenFPFørstegangsIdDto);
        oppgaveRestTjeneste.reserverOppgave(new OppgaveIdDto(oppgaveDto.getId()));
        verifiserAtErReservert(new HashMap<>(){{put(oppgaveDto.getBehandlingId(),melding.get(oppgaveDto.getBehandlingId()));}});

        MockEventKafkaMessages.clearMessages();
        Aksjonspunkt.Builder builder2 = Aksjonspunkt.builder();
        Aksjonspunkt aksjonspunktDto2 = builder2.medDefinisjon("5025").medStatus("UTFO").medFristTid(aksjonspunktFrist).build();
        initialiserForeldrepengerBehandlingRestKlient(aksjonspunktDto2);
        //doReturn(lagBehandlingDto(Collections.singletonList(aksjonspunktDto2))).when(foreldrepengerBehandlingRestKlient).getBehandling(anyLong());
        MockEventKafkaMessages.sendNyeOppgaver(MockEventKafkaMessages.avsluttførstegangsbehandlingMeldinger);
        kafkaReader.hentOgLagreMeldingene();

        verifiserAtReservertErTom();
        verifiserAtSakslisteErTom(sakslisteDrammenFPFørstegangsIdDto);

        //Sender nye meldinger for at aksjonspunktene er åpnet igjen
        MockEventKafkaMessages.clearMessages();
        Aksjonspunkt.Builder builder3 = Aksjonspunkt.builder();
        Aksjonspunkt aksjonspunktDto3 = builder3.medDefinisjon("5025").medStatus("OPPR").medFristTid(aksjonspunktFrist).build();
        initialiserForeldrepengerBehandlingRestKlient(aksjonspunktDto3);
        //doReturn(lagBehandlingDto(Collections.singletonList(aksjonspunktDto3))).when(foreldrepengerBehandlingRestKlient).getBehandling(anyLong());
        MockEventKafkaMessages.sendNyeOppgaver(melding);
        kafkaReader.hentOgLagreMeldingene();

        verifiserAtReservertErTom();
        verifiserFinnesISaksliste(melding, sakslisteDrammenFPFørstegangsIdDto);
    }

    private void initialiserForeldrepengerBehandlingRestKlient(Aksjonspunkt aksjonspunktDto1) {
        doReturn(lagBehandlingDto(Collections.singletonList(aksjonspunktDto1),UUID.nameUUIDFromBytes("1".getBytes()))).when(foreldrepengerBehandlingRestKlient).getBehandling(1L);
        doReturn(lagBehandlingDto(Collections.singletonList(aksjonspunktDto1),UUID.nameUUIDFromBytes("2".getBytes()))).when(foreldrepengerBehandlingRestKlient).getBehandling(2L);
        doReturn(lagBehandlingDto(Collections.singletonList(aksjonspunktDto1),UUID.nameUUIDFromBytes("3".getBytes()))).when(foreldrepengerBehandlingRestKlient).getBehandling(3L);
        doReturn(lagBehandlingDto(Collections.singletonList(aksjonspunktDto1),UUID.nameUUIDFromBytes("4".getBytes()))).when(foreldrepengerBehandlingRestKlient).getBehandling(4L);
        doReturn(lagBehandlingDto(Collections.singletonList(aksjonspunktDto1),UUID.nameUUIDFromBytes("5".getBytes()))).when(foreldrepengerBehandlingRestKlient).getBehandling(5L);
        doReturn(lagBehandlingDto(Collections.singletonList(aksjonspunktDto1),UUID.nameUUIDFromBytes("6".getBytes()))).when(foreldrepengerBehandlingRestKlient).getBehandling(6L);
        doReturn(lagBehandlingDto(Collections.singletonList(aksjonspunktDto1),UUID.nameUUIDFromBytes("7".getBytes()))).when(foreldrepengerBehandlingRestKlient).getBehandling(7L);
        doReturn(lagBehandlingDto(Collections.singletonList(aksjonspunktDto1),UUID.nameUUIDFromBytes("8".getBytes()))).when(foreldrepengerBehandlingRestKlient).getBehandling(8L);
    }

    private void initialiserForeldrepengerBehandlingRestKlientMedAnnenbruker(Aksjonspunkt aksjonspunktDto1) {
        doReturn(lagBehandlingAnnenBrukerDto(Collections.singletonList(aksjonspunktDto1),UUID.nameUUIDFromBytes("1".getBytes()))).when(foreldrepengerBehandlingRestKlient).getBehandling(1L);
        doReturn(lagBehandlingAnnenBrukerDto(Collections.singletonList(aksjonspunktDto1),UUID.nameUUIDFromBytes("2".getBytes()))).when(foreldrepengerBehandlingRestKlient).getBehandling(2L);
        doReturn(lagBehandlingAnnenBrukerDto(Collections.singletonList(aksjonspunktDto1),UUID.nameUUIDFromBytes("3".getBytes()))).when(foreldrepengerBehandlingRestKlient).getBehandling(3L);
        doReturn(lagBehandlingAnnenBrukerDto(Collections.singletonList(aksjonspunktDto1),UUID.nameUUIDFromBytes("4".getBytes()))).when(foreldrepengerBehandlingRestKlient).getBehandling(4L);
        doReturn(lagBehandlingAnnenBrukerDto(Collections.singletonList(aksjonspunktDto1),UUID.nameUUIDFromBytes("5".getBytes()))).when(foreldrepengerBehandlingRestKlient).getBehandling(5L);
        doReturn(lagBehandlingAnnenBrukerDto(Collections.singletonList(aksjonspunktDto1),UUID.nameUUIDFromBytes("6".getBytes()))).when(foreldrepengerBehandlingRestKlient).getBehandling(6L);
        doReturn(lagBehandlingAnnenBrukerDto(Collections.singletonList(aksjonspunktDto1),UUID.nameUUIDFromBytes("7".getBytes()))).when(foreldrepengerBehandlingRestKlient).getBehandling(7L);
        doReturn(lagBehandlingAnnenBrukerDto(Collections.singletonList(aksjonspunktDto1),UUID.nameUUIDFromBytes("8".getBytes()))).when(foreldrepengerBehandlingRestKlient).getBehandling(8L);
    }

    private void verifiserAtReservertErTom() {
        List<OppgaveDto> reserverteOppgaver = oppgaveRestTjeneste.getReserverteOppgaver();
        assertThat(verifiserAtEksaktFinnes(MockEventKafkaMessages.tomtMap, reserverteOppgaver)).isNull();
    }

    private void verifiserAtSakslisteErTom(SakslisteIdDto sakslisteIdDtoForSaksb) {
        List<OppgaveDto> oppgaverTilBehandling = oppgaveRestTjeneste.getOppgaverTilBehandling(sakslisteIdDtoForSaksb);
        assertThat(verifiserAtEksaktFinnes(MockEventKafkaMessages.tomtMap, oppgaverTilBehandling)).isNull();
    }

    private OppgaveDto verifiserAtErReservert(Map<Long, AksjonspunkteventTestInfo> meldinger) {
        List<OppgaveDto> reserverteOppgaver = oppgaveRestTjeneste.getReserverteOppgaver();
        return verifiserAtEksaktFinnes(meldinger, reserverteOppgaver);
    }

    private OppgaveDto verifiserFinnesISaksliste(Map<Long, AksjonspunkteventTestInfo> meldinger, SakslisteIdDto sakslisteIdDtoForSaksb) {
        List<OppgaveDto> oppgaverTilBehandling = oppgaveRestTjeneste.getOppgaverTilBehandling(sakslisteIdDtoForSaksb);
        return verifiserAtEksaktFinnes(meldinger, oppgaverTilBehandling);
    }

    private OppgaveDto verifiserAtEksaktFinnes(Map<Long, AksjonspunkteventTestInfo> meldinger, List<OppgaveDto> oppgaverTilBehandling) {
        assertThat(oppgaverTilBehandling).withFailMessage("Oppgavene til behandling har ikke samme antall som meldingene").hasSize(meldinger.size());
        for (OppgaveDto oppgave :oppgaverTilBehandling) {
            assertThat(meldinger.get(oppgave.getBehandlingId())).withFailMessage("Finner ikke oppgaven med behandlingId:"+oppgave.getBehandlingId()+" i settet funnet i databasen").isNotNull();
            meldinger.get(oppgave.getBehandlingId()).sammenligne(oppgave);
        }
        if (oppgaverTilBehandling.isEmpty())return null;
        return oppgaverTilBehandling.get(0);
    }


    private void lagEnkeltMockResultatForTpsTjenesten() {
        TpsPersonDto personDto = new TpsPersonDto.Builder()
                .medAktørId(new AktørId(3L))
                .medFnr(PersonIdent.fra("12345678901"))
                .medNavn("Test")
                .medFødselsdato(LocalDate.now())
                .medNavBrukerKjønn("NA")
                .build();
        when(tpsTjeneste.hentBrukerForAktør(new AktørId(3L))).thenReturn(Optional.of(personDto));
    }

    private BehandlingFpsak lagBehandlingDto(List<Aksjonspunkt> aksjonspunktDtoer, UUID uuid){
        return BehandlingFpsak.builder()
                //.medUuid(UUID.randomUUID())
                //.medUuid(UUID.nameUUIDFromBytes("TEST".getBytes()))
                .medUuid(uuid)
                .medBehandlendeEnhetNavn("NAV")
                .medAnsvarligSaksbehandler("VLLOS")
                .medStatus("-")
                .medHarRefusjonskravFraArbeidsgiver(false)
                .medAksjonspunkter(aksjonspunktDtoer)
                .build();
    }

    private BehandlingFpsak lagBehandlingAnnenBrukerDto(List<Aksjonspunkt> aksjonspunktDtoer, UUID uuid){
        return BehandlingFpsak.builder()
                //.medUuid(UUID.randomUUID())
                //.medUuid(UUID.nameUUIDFromBytes("TEST".getBytes()))
                .medUuid(uuid)
                .medBehandlendeEnhetNavn("NAV")
                .medAnsvarligSaksbehandler("IKKE_VLLOS")
                .medStatus("-")
                .medHarRefusjonskravFraArbeidsgiver(false)
                .medAksjonspunkter(aksjonspunktDtoer)
                .build();
    }
}
