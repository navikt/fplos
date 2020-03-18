package no.nav.fplos.kafkatjenester;

import static no.nav.fplos.kafkatjenester.AksjonspunktTest.Builder.aksjonspunktTestBuilder;
import static no.nav.fplos.kafkatjenester.TestUtil.behandlingBuilderMal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import no.nav.foreldrepenger.loslager.oppgave.BehandlingStatus;
import org.junit.Rule;
import org.junit.Test;

import no.nav.foreldrepenger.dbstoette.UnittestRepositoryRule;
import no.nav.foreldrepenger.loslager.BehandlingId;
import no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;
import no.nav.foreldrepenger.loslager.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEgenskap;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventLogg;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventType;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepository;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepositoryImpl;
import no.nav.fplos.foreldrepengerbehandling.Aksjonspunkt;
import no.nav.fplos.foreldrepengerbehandling.BehandlingFpsak;
import no.nav.fplos.foreldrepengerbehandling.ForeldrepengerBehandlingRestKlient;
import no.nav.vedtak.felles.integrasjon.kafka.EventHendelse;
import no.nav.vedtak.felles.integrasjon.kafka.Fagsystem;

public class ForeldrepengerEventHåndtererTest {

    @Rule
    public UnittestRepositoryRule repoRule = new UnittestRepositoryRule();
    private EntityManager entityManager = repoRule.getEntityManager();
    private OppgaveRepository oppgaveRepository = new OppgaveRepositoryImpl(entityManager);
    private OppgaveEgenskapHandler oppgaveEgenskapHandler = new OppgaveEgenskapHandler(oppgaveRepository);
    private ForeldrepengerBehandlingRestKlient fpsak = mock(ForeldrepengerBehandlingRestKlient.class);
    private ForeldrepengerEventHåndterer handler = new ForeldrepengerEventHåndterer(oppgaveRepository, fpsak, oppgaveEgenskapHandler);
    private static BehandlingId behandlingId =  BehandlingId.random();

    private LocalDateTime aksjonspunktFrist = null;

    private AksjonspunktTest skalHaOppgave5015 = aksjonspunktTestBuilder().medOpprettet(5015).build();
    private AksjonspunktTest skalLukkeOppgave5015UTFO= aksjonspunktTestBuilder().medUtført(5015).build();

    Map<String, String> aksjonspunktKoderSkalHaOppgave = new HashMap<>(){{put("5015","OPPR");}};
    private Map<String, String> aksjonspunktKoderSkalLukkeOppgave = new HashMap<>(){{put("5015","UTFO");}};
    private Map<String, String> aksjonspunktKoderPåVent = new HashMap<>(){{put("5015","OPPR");put("7002","OPPR");}};
    private Map<String, String> aksjonspunktKoderTilBeslutter = new HashMap<>(){{put("5016","OPPR");}};
    private Map<String, String> aksjonspunktKoderPapirsøknadES = new HashMap<>(){{put("5012","OPPR");put("5010","OPPR");put("5005","UTFO");}};
    private Map<String, String> aksjonspunktKoderPapirsøknadFP = new HashMap<>(){{put("5040","OPPR");put("5012","AVBR");}};
    private Map<String, String> aksjonspunktKoderPapirsøknadEndringFP = new HashMap<>(){{put("5057","OPPR");}};
    private Map<String, String> aksjonspunktKoderSkalPåManueltVent = new HashMap<>(){{put("5012","OPPR");put("7001","OPPR");}};
    private Map<String, String> aksjonspunktKoderSkalPåVent = new HashMap<>(){{put("5012","AVBR");put("7002","OPPR");}};
    private Map<String, String> aksjonspunktKoderUtland = new HashMap<>(){{put("5068","OPPR");}};

    private List<Aksjonspunkt> aksjonspunktKoderSkalHaOppgaveDto = Collections.singletonList(aksjonspunktDtoFra("5015","OPPR",aksjonspunktFrist));
    private List<Aksjonspunkt> aksjonspunktKoderSkalLukkeOppgaveDto = Collections.singletonList(aksjonspunktDtoFra("5015","UTFO",aksjonspunktFrist));
    private List<Aksjonspunkt> aksjonspunktKoderPåVentDto = Arrays.asList(aksjonspunktDtoFra("5015","OPPR",aksjonspunktFrist), aksjonspunktDtoFra("7002","OPPR",aksjonspunktFrist));
    private List<Aksjonspunkt> aksjonspunktKoderTilBeslutterDto = Collections.singletonList(aksjonspunktDtoFra("5016","OPPR",aksjonspunktFrist));
    private List<Aksjonspunkt> aksjonspunktKoderPapirsøknadESDto = Arrays.asList(aksjonspunktDtoFra("5012","OPPR",aksjonspunktFrist), aksjonspunktDtoFra("5010","OPPR",aksjonspunktFrist), aksjonspunktDtoFra("5005","UTFO",aksjonspunktFrist));
    private List<Aksjonspunkt> aksjonspunktKoderPapirsøknadFPDto = Arrays.asList(aksjonspunktDtoFra("5040","OPPR",aksjonspunktFrist), aksjonspunktDtoFra("5012","AVBR",aksjonspunktFrist));
    private List<Aksjonspunkt> aksjonspunktKoderPapirsøknadEndringFPDto = Collections.singletonList(aksjonspunktDtoFra("5057","OPPR", aksjonspunktFrist));
    private List<Aksjonspunkt> aksjonspunktKoderSkalPåManueltVentDto = Arrays.asList(aksjonspunktDtoFra("5012","OPPR",aksjonspunktFrist), aksjonspunktDtoFra("7001","OPPR",aksjonspunktFrist));
    private List<Aksjonspunkt> aksjonspunktKoderSkalPåVentDto = Arrays.asList(aksjonspunktDtoFra("5012","AVBR",aksjonspunktFrist), aksjonspunktDtoFra("7002","OPPR",aksjonspunktFrist));
    private List<Aksjonspunkt> aksjonspunktKoderUtlandAutomatiskDto = Collections.singletonList(aksjonspunktMedBegrunnelseDtoFra("5068","OPPR",aksjonspunktFrist,"BOSATT_UTLAND"));
    private List<Aksjonspunkt> aksjonspunktKoderUtlandManuellDto = Collections.singletonList(aksjonspunktMedBegrunnelseDtoFra("6068","OPPR",aksjonspunktFrist, "BOSATT_UTLAND"));
    private final AksjonspunktTest beslutter = aksjonspunktTestBuilder().medOpprettet(5016).build();
    private final AksjonspunktTest beslutterUtfort = aksjonspunktTestBuilder().medUtført(5016).build();
    private final AksjonspunktTest beslutterUtfortMedAnnetÅpentPunkt = aksjonspunktTestBuilder().medUtført(5016).medOpprettet(5010).build();

    // 7003 oppr -> utfo

    FpsakBehandlingProsessEventDto eventDrammenFra(Map<String, String> aksjonspunktmap){
        return (FpsakBehandlingProsessEventDto) prosessBuilderFra(aksjonspunktmap)
                .medBehandlendeEnhet("4802")
                .medBehandlingStatus(BehandlingStatus.UTREDES.getKode())
                .build();
    }

    FpsakBehandlingProsessEventDto eventDrammenMedEndredeFelterFra(Map<String, String> aksjonspunktmap) {
        return (FpsakBehandlingProsessEventDto) prosessBuilderFra(aksjonspunktmap)
                .medBehandlendeEnhet("4802")
                .medBehandlingStatus(BehandlingStatus.FATTER_VEDTAK.getKode())
                .build();
    }

    private FpsakBehandlingProsessEventDto eventStordFra(Map<String, String> aksjonspunktmap){
        return (FpsakBehandlingProsessEventDto) prosessBuilderFra(aksjonspunktmap)
                .medBehandlendeEnhet("4842")
                .medBehandlingStatus(BehandlingStatus.UTREDES.getKode())
                .build();
    }

    private Aksjonspunkt aksjonspunktMedBegrunnelseDtoFra(String aksjonspunkKode, String status, LocalDateTime aksjonspunktFrist, String begrunnelse){
        return Aksjonspunkt.builder()
                .medDefinisjon(aksjonspunkKode)
                .medStatus(status)
                .medFristTid(aksjonspunktFrist)
                .medBegrunnelse("BOSATT_UTLAND")
                .build();
    }

    public static Aksjonspunkt aksjonspunktDtoFra(String aksjonspunkKode, String status, LocalDateTime aksjonspunktFrist){
        return Aksjonspunkt.builder()
                .medDefinisjon(aksjonspunkKode)
                .medStatus(status)
                .medFristTid(aksjonspunktFrist)
                .build();
    }

    private FpsakBehandlingProsessEventDto.Builder prosessBuilderFra(Map<String, String> aksjonspunktmap){
        return FpsakBehandlingProsessEventDto.builder()
                .medEksternId(behandlingId.toUUID())
                .medFagsystem(Fagsystem.FPSAK)
                .medSaksnummer("135701264")
                .medAktørId("9000000030703")
                .medEventHendelse(EventHendelse.AKSJONSPUNKT_OPPRETTET)
                .medBehandlingSteg("STEG")
                .medYtelseTypeKode(FagsakYtelseType.FORELDREPENGER.getKode())
                .medBehandlingTypeKode(BehandlingType.FØRSTEGANGSSØKNAD.getKode())
                .medOpprettetBehandling(LocalDateTime.now())
                .medAksjonspunktKoderMedStatusListe(aksjonspunktmap);
    }

    private static BehandlingFpsak behandlingDtoFra(List<Aksjonspunkt> aksjonspunkter) {
        return behandlingBuilderMal()
                .medBehandlingstidFrist(LocalDate.now())
                .medBehandlingId(behandlingId)
                .medAksjonspunkter(aksjonspunkter)
                .build();
    }

    private static BehandlingFpsak behandlingDtoMedManueltMarkertUtlandsakFra(List<Aksjonspunkt> aksjonspunkter){
        return behandlingBuilderMal()
                .medBehandlingstidFrist(LocalDate.now())
                .medBehandlingId(behandlingId)
                .medAksjonspunkter(aksjonspunkter)
                .build();
    }

    private BehandlingFpsak lagBehandlingDtoMedHarGradering(List<Aksjonspunkt> aksjonspunkter){
        return behandlingBuilderMal()
                .medBehandlingstidFrist(LocalDate.now())
                .medBehandlingId(behandlingId)
                .medHarGradering(true)
                .medAksjonspunkter(aksjonspunkter)
                .build();
    }

    private BehandlingFpsak lagBehandlingDtoMedEndretBehandlingstidFrist(List<Aksjonspunkt> aksjonspunkter) {
        return behandlingBuilderMal()
                .medBehandlingstidFrist(LocalDate.now().plusDays(100))
                .medBehandlingId(behandlingId)
                .medAksjonspunkter(aksjonspunkter)
                .build();
    }

    @Test
    public void testEnkelOppgave() {
        behandle(skalHaOppgave5015);
        verifiserAtAntallOppgaverEr(1);
        verifiserEnAktivOppgave();
        verifiserOppgaveEgenskaperTilsvarer(Collections.emptyList());
        verifiserOppgaveEventAntallEr(1);
        sjekkAktivOppgaveEksisterer(true);
    }

    @Test
    public void skalLukkeOppgavenVedOversendelseTilBehandler() {
        behandle(skalHaOppgave5015);
        behandle(skalLukkeOppgave5015UTFO);
        behandle(beslutter);
        behandle(beslutterUtfort);
        verifiserAtAntallOppgaverEr(2);
        verifiserOppgaveEgenskaperTilsvarer(List.of(AndreKriterierType.TIL_BESLUTTER));
        verifiserOppgaveEventAntallEr(4);
    }

    @Test
    public void skalOppretteNyOppgaveVedReturFraBehandler() {
        behandle(skalHaOppgave5015);
        behandle(skalLukkeOppgave5015UTFO);
        behandle(beslutter);
        verifiserOppgaveEgenskaperTilsvarer(List.of(AndreKriterierType.TIL_BESLUTTER));
        behandle(beslutterUtfortMedAnnetÅpentPunkt);
        verifiserAtAntallOppgaverEr(3);
        verifiserOppgaveEventLoggTilsvarer(List.of(
                OppgaveEventType.OPPRETTET,
                OppgaveEventType.LUKKET,
                OppgaveEventType.OPPRETTET,
                OppgaveEventType.LUKKET,
                OppgaveEventType.OPPRETTET));
    }

    @Test
    public void skalLagreBehandlingfristOgFørstestønadsdag() {
        behandle(skalHaOppgave5015);
        verifiserAtAntallOppgaverEr(1);
        Oppgave oppgave = hentOppgave();
        assertThat(oppgave.getBehandlingsfrist()).isNotNull();
        assertThat(oppgave.getForsteStonadsdag()).isNotNull();
    }

    @Test
    public void skalOppdatereOppgaveVedNyeEventer() {
        behandle(skalHaOppgave5015);
        var førsteBehandlingsfrist = hentOppgave().getBehandlingsfrist();

        // neste melding har en endret behandlingstidFrist
        when(fpsak.getBehandling(any(BehandlingId.class))).thenReturn(
                lagBehandlingDtoMedEndretBehandlingstidFrist(skalHaOppgave5015.getAksjonspunkt()));
        handler.håndterEvent(eventDrammenFra(skalHaOppgave5015.getDto()));
        var andreBehandlingsfrist = hentOppgave().getBehandlingsfrist();

        assertThat(førsteBehandlingsfrist).isBefore(andreBehandlingsfrist);
        verifiserOppgaveEventLoggTilsvarer(List.of(OppgaveEventType.OPPRETTET, OppgaveEventType.GJENAPNET));
    }


    @Test
    public void opprettingOgOverTilBehandlerTest(){
        when(fpsak.getBehandling(any(BehandlingId.class))).thenReturn(behandlingDtoFra(aksjonspunktKoderSkalHaOppgaveDto));
        handler.håndterEvent(eventDrammenFra(aksjonspunktKoderSkalHaOppgave));
        when(fpsak.getBehandling(any(BehandlingId.class))).thenReturn(behandlingDtoFra(aksjonspunktKoderTilBeslutterDto));
        handler.håndterEvent(eventDrammenFra(aksjonspunktKoderTilBeslutter));

        List<Oppgave> oppgaver = repoRule.getRepository().hentAlle(Oppgave.class);
        assertThat(oppgaver).hasSize(2);

        //Sjekke at det er behandleroppgaven som er aktiv
        OppgaveEgenskap oppgaveEgenskap = repoRule.getRepository().hentAlle(OppgaveEgenskap.class).get(0);
        assertThat(oppgaveEgenskap.getOppgave().getAktiv()).isTrue();

        //Sjekker at det siste eventet er å opprette og at det rett før er lukker.
        List<OppgaveEventLogg> oppgaveEventLogg = oppgaveRepository.hentOppgaveEventer(behandlingId);

        assertThat(oppgaveEventLogg.get(0).getEventType()).isEqualTo(OppgaveEventType.OPPRETTET);
        assertThat(oppgaveEventLogg.get(1).getEventType()).isEqualTo(OppgaveEventType.LUKKET);
    }

    @Test
    public void opprettingOgAvsluttingTest(){
        when(fpsak.getBehandling(any(BehandlingId.class))).thenReturn(behandlingDtoFra(aksjonspunktKoderSkalHaOppgaveDto));
        handler.håndterEvent(eventDrammenFra(aksjonspunktKoderSkalHaOppgave));
        when(fpsak.getBehandling(any(BehandlingId.class))).thenReturn(behandlingDtoFra(aksjonspunktKoderSkalLukkeOppgaveDto));
        handler.håndterEvent(eventDrammenFra(aksjonspunktKoderSkalLukkeOppgave));
        when(fpsak.getBehandling(any(BehandlingId.class))).thenReturn(behandlingDtoFra(aksjonspunktKoderSkalHaOppgaveDto));
        handler.håndterEvent(eventDrammenFra(aksjonspunktKoderSkalHaOppgave));
        when(fpsak.getBehandling(any(BehandlingId.class))).thenReturn(behandlingDtoFra(aksjonspunktKoderSkalLukkeOppgaveDto));
        handler.håndterEvent(eventDrammenFra(aksjonspunktKoderSkalLukkeOppgave));
        assertThat(repoRule.getRepository().hentAlle(Oppgave.class)).hasSize(1);
        assertThat(repoRule.getRepository().hentAlle(OppgaveEgenskap.class)).hasSize(0);
    }

    @Test
    public void opprettingOPåVentTest(){
        when(fpsak.getBehandling(any(BehandlingId.class))).thenReturn(behandlingDtoFra(aksjonspunktKoderSkalHaOppgaveDto));
        handler.håndterEvent(eventDrammenFra(aksjonspunktKoderSkalHaOppgave));
        when(fpsak.getBehandling(any(BehandlingId.class))).thenReturn(behandlingDtoFra(aksjonspunktKoderPåVentDto));
        handler.håndterEvent(eventDrammenFra(aksjonspunktKoderPåVent));
        assertThat(repoRule.getRepository().hentAlle(Oppgave.class)).hasSize(1);
        Oppgave oppgave = repoRule.getRepository().hentAlle(Oppgave.class).get(0);
        assertThat(oppgave.getAktiv()).isFalse();
    }

    @Test
    public void flyttingAvBehandlendeEnhetTest(){
        when(fpsak.getBehandling(any(BehandlingId.class))).thenReturn(behandlingDtoFra(aksjonspunktKoderSkalHaOppgaveDto));
        handler.håndterEvent(eventDrammenFra(aksjonspunktKoderSkalHaOppgave));
        when(fpsak.getBehandling(any(BehandlingId.class))).thenReturn(behandlingDtoFra(aksjonspunktKoderSkalHaOppgaveDto));
        handler.håndterEvent(eventStordFra(aksjonspunktKoderSkalHaOppgave));
        assertThat(repoRule.getRepository().hentAlle(Oppgave.class)).hasSize(2);
    }

    @Test
    public void opprettingOppgaveMedEgenskapTest(){
        when(fpsak.getBehandling(any(BehandlingId.class))).thenReturn(behandlingDtoFra(aksjonspunktKoderTilBeslutterDto));
        handler.håndterEvent(eventDrammenFra(aksjonspunktKoderTilBeslutter));
        assertThat(repoRule.getRepository().hentAlle(Oppgave.class)).hasSize(1);
        sjekkForEnOppgaveOgEgenskap(AndreKriterierType.TIL_BESLUTTER);
        sjekkEventLoggInneholder(behandlingId, OppgaveEventType.OPPRETTET, AndreKriterierType.TIL_BESLUTTER);
    }


    @Test
    public void opprettingOppgaveMedEgenskapPapirsøknadESTest(){
        when(fpsak.getBehandling(any(BehandlingId.class))).thenReturn(behandlingDtoFra(aksjonspunktKoderPapirsøknadESDto));
        handler.håndterEvent(eventDrammenFra(aksjonspunktKoderPapirsøknadES));
        sjekkForEnOppgaveOgEgenskap(AndreKriterierType.PAPIRSØKNAD);
        sjekkEventLoggInneholder(behandlingId, OppgaveEventType.OPPRETTET, AndreKriterierType.PAPIRSØKNAD);
    }

    @Test
    public void opprettingOppgaveMedEgenskapPapirsøknadFPTest(){
        when(fpsak.getBehandling(any(BehandlingId.class))).thenReturn(behandlingDtoFra(aksjonspunktKoderPapirsøknadFPDto));
        handler.håndterEvent(eventDrammenFra(aksjonspunktKoderPapirsøknadFP));
        sjekkForEnOppgaveOgEgenskap(AndreKriterierType.PAPIRSØKNAD);
        sjekkEventLoggInneholder(behandlingId, OppgaveEventType.OPPRETTET, AndreKriterierType.PAPIRSØKNAD);
    }

    @Test
    public void opprettingOppgaveMedEgenskapPapirsøknadEndringFPTest(){
        when(fpsak.getBehandling(any(BehandlingId.class)))
                .thenReturn(behandlingDtoFra(aksjonspunktKoderPapirsøknadEndringFPDto));
        handler.håndterEvent(eventDrammenFra(aksjonspunktKoderPapirsøknadEndringFP));
        sjekkForEnOppgaveOgEgenskap(AndreKriterierType.PAPIRSØKNAD);
        sjekkEventLoggInneholder(behandlingId, OppgaveEventType.OPPRETTET, AndreKriterierType.PAPIRSØKNAD);
    }

    @Test
    public void opprettingOppgavemedEgenskapHarGraderingFPTest() {
        when(fpsak.getBehandling(any(BehandlingId.class))).thenReturn(lagBehandlingDtoMedHarGradering(aksjonspunktKoderPapirsøknadEndringFPDto));
        handler.håndterEvent(eventDrammenFra(aksjonspunktKoderPapirsøknadEndringFP));
        sjekkOppgaveOgOppgaveEgenskaper(AndreKriterierType.SOKT_GRADERING,1,2);
    }

    @Test
    public void opprettingOppgaveMedEgenskapUtlandAutomatiskMarkertTest(){
        when(fpsak.getBehandling(any(BehandlingId.class))).thenReturn(behandlingDtoFra(aksjonspunktKoderUtlandAutomatiskDto));
        handler.håndterEvent(eventDrammenFra(aksjonspunktKoderUtland));
        sjekkForEnOppgaveOgEgenskap(AndreKriterierType.UTLANDSSAK);
    }

    @Test
    public void opprettingOppgaveMedEgenskapUtlandManueltMarkertTest(){
        when(fpsak.getBehandling(any(BehandlingId.class))).thenReturn(behandlingDtoMedManueltMarkertUtlandsakFra(aksjonspunktKoderUtlandManuellDto));
        handler.håndterEvent(eventDrammenFra(aksjonspunktKoderSkalHaOppgave));
        sjekkForEnOppgaveOgEgenskap(AndreKriterierType.UTLANDSSAK);
    }

    @Test
    public void opprettingOppgaveSkalIkkeKommeOppForSaksbehandlerSomSendteTilBeslutterTest(){
        when(fpsak.getBehandling(any(BehandlingId.class))).thenReturn(behandlingDtoFra(aksjonspunktKoderSkalHaOppgaveDto));
        handler.håndterEvent(eventDrammenFra(aksjonspunktKoderSkalHaOppgave));
        when(fpsak.getBehandling(any(BehandlingId.class))).thenReturn(behandlingDtoFra(aksjonspunktKoderTilBeslutterDto));
        handler.håndterEvent(eventDrammenFra(aksjonspunktKoderTilBeslutter));

        OppgaveEgenskap oppgaveEgenskap = repoRule.getRepository().hentAlle(OppgaveEgenskap.class).get(0);
        assertThat(oppgaveEgenskap.getSisteSaksbehandlerForTotrinn()).isEqualTo("VLLOS");
    }

    @Test
    public void lukkingOppgavePgaPåVentBeslutterTest(){
        when(fpsak.getBehandling(any(BehandlingId.class))).thenReturn(behandlingDtoFra(aksjonspunktKoderSkalPåVentDto));
        handler.håndterEvent(eventDrammenFra(aksjonspunktKoderSkalPåVent));
        OppgaveEventLogg oppgaveEventLogg = repoRule.getRepository().hentAlle(OppgaveEventLogg.class).get(0);
        assertThat(oppgaveEventLogg.getEventType()).isEqualTo(OppgaveEventType.VENT);
    }

    @Test
    public void lukkingOppgavePgaManueltPåVentBeslutterTest(){
        when(fpsak.getBehandling(any(BehandlingId.class))).thenReturn(behandlingDtoFra(aksjonspunktKoderSkalPåManueltVentDto));
        handler.håndterEvent(eventDrammenFra(aksjonspunktKoderSkalPåManueltVent));
        OppgaveEventLogg oppgaveEventLogg = repoRule.getRepository().hentAlle(OppgaveEventLogg.class).get(0);
        assertThat(oppgaveEventLogg.getEventType()).isEqualTo(OppgaveEventType.MANU_VENT);
    }

    @Test
    public void skalKunVæreEnÅpenOppgavePerBehandling() {
        var første = aksjonspunkt();
        første.addOpprettet(7003);
        behandle(første);

        var andre = aksjonspunkt();
        andre.addUtført(7003);
        behandle(andre);

        var tredje = aksjonspunkt();
        tredje.addOpprettet(5058);
        tredje.addUtført(7003);
        behandle(tredje);

        var fjerde = aksjonspunkt();
        fjerde.addOpprettet(5038);
        fjerde.addUtført(5058);
        fjerde.addUtført(7003);
        behandle(fjerde);

        verifiserEnAktivOppgave();
    }

    private void behandle(AksjonspunktTest aksjonspunktTest) {
        konfigFpsakMock(aksjonspunktTest.getAksjonspunkt());
        handler.håndterEvent(eventDrammenFra(aksjonspunktTest.getDto()));
    }

    private void konfigFpsakMock(List<Aksjonspunkt> aksjonspunkt) {
        when(fpsak.getBehandling(any(BehandlingId.class))).thenReturn(behandlingDtoFra(aksjonspunkt));
    }

    private void verifiserAtAntallOppgaverEr(int antall) {
        assertThat(repoRule.getRepository().hentAlle(Oppgave.class)).hasSize(antall);
    }

    private Oppgave hentOppgave() {
        return repoRule.getRepository().hentAlle(Oppgave.class).stream().findFirst().orElse(null);
    }

    private void sjekkAktivOppgaveEksisterer(boolean aktiv) {
        List<Oppgave> oppgave = repoRule.getRepository().hentAlle(Oppgave.class);
        assertThat(oppgave.get(0).getAktiv()).isEqualTo(aktiv);
        int antallAktive = (int) oppgave.stream().filter(Oppgave::getAktiv).count();
        assertThat(antallAktive).isEqualTo(aktiv ? 1 : 0);
    }

    private void verifiserEnAktivOppgave() {
        List<Oppgave> oppgave = repoRule.getRepository().hentAlle(Oppgave.class);
        long antallAktive = oppgave.stream().filter(Oppgave::getAktiv).count();
        assertThat(antallAktive).isEqualTo(1L);
    }

    private void verifiserOppgaveEgenskaperTilsvarer(List<AndreKriterierType> kriterier) {
        List<AndreKriterierType> kriterieLagret = repoRule.getRepository().hentAlle(OppgaveEgenskap.class).stream()
                .map(OppgaveEgenskap::getAndreKriterierType).collect(Collectors.toList());
        assertThat(kriterieLagret).containsExactlyElementsOf(kriterier);
    }

    private void verifiserOppgaveEventAntallEr(int antall) {
        var eventer = repoRule.getRepository().hentAlle(OppgaveEventLogg.class);
        assertThat(eventer).hasSize(antall);
    }

    private void verifiserOppgaveEventLoggTilsvarer(List<OppgaveEventType> eventer) {
        var eventTyper = repoRule.getRepository().hentAlle(OppgaveEventLogg.class).stream()
                .map(OppgaveEventLogg::getEventType).collect(Collectors.toList());
        assertThat(eventTyper).containsExactlyElementsOf(eventer);
    }

    private void sjekkEventLoggInneholder(BehandlingId behandlingId, OppgaveEventType eventType, AndreKriterierType kriterierType) {
        List<OppgaveEventLogg> oppgaveEventLoggs = oppgaveRepository.hentOppgaveEventer(behandlingId);

        assertThat(oppgaveEventLoggs.get(0).getEventType()).isEqualTo(eventType);
        if (kriterierType != null) {
            assertThat(oppgaveEventLoggs.get(0).getAndreKriterierType()).isEqualTo(kriterierType);
        }
    }

    private void sjekkForEnOppgaveOgEgenskap(AndreKriterierType egenskap) {
        sjekkOppgaveOgOppgaveEgenskaper(egenskap, 1, 1);
    }

    private void sjekkOppgaveOgOppgaveEgenskaper(AndreKriterierType egenskap, int antallOppgaver, int antallEgenskaper) {
        assertThat(repoRule.getRepository().hentAlle(Oppgave.class)).hasSize(antallOppgaver);
        assertThat(repoRule.getRepository().hentAlle(OppgaveEgenskap.class)).hasSize(antallEgenskaper);
        assertThat(repoRule.getRepository().hentAlle(OppgaveEgenskap.class))
                .extracting(OppgaveEgenskap::getAndreKriterierType).contains(egenskap);
    }

    public static AksjonspunktTest aksjonspunkt() {
        return new AksjonspunktTest();
    }
}
