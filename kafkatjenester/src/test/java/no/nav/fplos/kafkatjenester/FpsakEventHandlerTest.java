package no.nav.fplos.kafkatjenester;

import no.nav.foreldrepenger.dbstoette.UnittestRepositoryRule;
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
import no.nav.vedtak.felles.integrasjon.kafka.FpsakBehandlingProsessEventDto;
import org.junit.Rule;
import org.junit.Test;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import static no.nav.fplos.kafkatjenester.TestUtil.behandlingBuilderMal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FpsakEventHandlerTest {

    @Rule
    public UnittestRepositoryRule repoRule = new UnittestRepositoryRule();
    private EntityManager entityManager = repoRule.getEntityManager();
    private OppgaveRepository oppgaveRepository = new OppgaveRepositoryImpl(entityManager);
    private ForeldrepengerBehandlingRestKlient foreldrepengerBehandlingRestKlient = mock(ForeldrepengerBehandlingRestKlient.class);
    private FpsakEventHandler fpsakEventHandler = new FpsakEventHandler(oppgaveRepository, foreldrepengerBehandlingRestKlient);
    private static Long behandlingId = 1073051L;
    private static String fagsystem = "FPSAK";

    private LocalDateTime aksjonspunktFrist = null;

    Map<String, String> aksjonspunktKoderSkalHaOppgave = new HashMap<>(){{put("5015","OPPR");}};
    private Map<String, String> aksjonspunktKoderSkalLukkeOppgave = new HashMap<>(){{put("5015","UTFO");}};
    private Map<String, String> aksjonspunktKoderPåVent = new HashMap<>(){{put("5015","OPPR");put("7002","OPPR");}};
    private Map<String, String> aksjonspunktKoderTilBeslutter = new HashMap<>(){{put("5016","OPPR");}};
    private Map<String, String> aksjonspunktKoderPapirsøknadES = new HashMap<>(){{put("5012","OPPR");put("5010","OPPR");put("5005","UTFO");}};
    private Map<String, String> aksjonspunktKoderPapirsøknadFP = new HashMap<>(){{put("5040","OPPR");put("5012","AVBR");}};
    private Map<String, String> aksjonspunktKoderPapirsøknadEndringFP = new HashMap<>(){{put("5057","OPPR");}};
    private Map<String, String> aksjonspunktKoderSelvstendigFrilanserFP = new HashMap<>(){{put("5038","OPPR");}};
    private Map<String, String> aksjonspunktKoderSkalPåManueltVent = new HashMap<>(){{put("5012","OPPR");put("7001","OPPR");}};
    private Map<String, String> aksjonspunktKoderSkalPåVent = new HashMap<>(){{put("5012","AVBR");put("7002","OPPR");}};
    private Map<String, String> aksjonspunktKoderUtland = new HashMap<>(){{put("5068","OPPR");}};

    private List<Aksjonspunkt> aksjonspunktKoderSkalHaOppgaveDto = Collections.singletonList(aksjonspunktDtoFra("5015","OPPR",aksjonspunktFrist));
    private List<Aksjonspunkt> aksjonspunktKoderSkalLukkeOppgaveDto = Collections.singletonList(aksjonspunktDtoFra("5015","UTFO",aksjonspunktFrist));
    private List<Aksjonspunkt> aksjonspunktKoderPåVentDto = Arrays.asList(aksjonspunktDtoFra("5015","OPPR",aksjonspunktFrist), aksjonspunktDtoFra("7002","OPPR",aksjonspunktFrist));
    private List<Aksjonspunkt> aksjonspunktKoderTilBeslutterDto = Collections.singletonList(aksjonspunktDtoFra("5016","OPPR",aksjonspunktFrist));
    private List<Aksjonspunkt> aksjonspunktKoderPapirsøknadESDto = Arrays.asList(aksjonspunktDtoFra("5012","OPPR",aksjonspunktFrist), aksjonspunktDtoFra("5010","OPPR",aksjonspunktFrist), aksjonspunktDtoFra("5005","UTFO",aksjonspunktFrist));
    private List<Aksjonspunkt> aksjonspunktKoderPapirsøknadFPDto = Arrays.asList(aksjonspunktDtoFra("5040","OPPR",aksjonspunktFrist), aksjonspunktDtoFra("5012","AVBR",aksjonspunktFrist));
    private List<Aksjonspunkt> aksjonspunktKoderSelvstendigFrilanserFPDto = Arrays.asList(aksjonspunktDtoFra("5038","OPPR",aksjonspunktFrist));
    private List<Aksjonspunkt> aksjonspunktKoderPapirsøknadEndringFPDto = Collections.singletonList(aksjonspunktDtoFra("5057","OPPR", aksjonspunktFrist));
    private List<Aksjonspunkt> aksjonspunktKoderSkalPåManueltVentDto = Arrays.asList(aksjonspunktDtoFra("5012","OPPR",aksjonspunktFrist), aksjonspunktDtoFra("7001","OPPR",aksjonspunktFrist));
    private List<Aksjonspunkt> aksjonspunktKoderSkalPåVentDto = Arrays.asList(aksjonspunktDtoFra("5012","AVBR",aksjonspunktFrist), aksjonspunktDtoFra("7002","OPPR",aksjonspunktFrist));
    private List<Aksjonspunkt> aksjonspunktKoderUtlandAutomatiskDto = Collections.singletonList(aksjonspunktMedBegrunnelseDtoFra("5068","OPPR",aksjonspunktFrist,"BOSATT_UTLAND"));
    private List<Aksjonspunkt> aksjonspunktKoderUtlandManuellDto = Collections.singletonList(aksjonspunktMedBegrunnelseDtoFra("6068","OPPR",aksjonspunktFrist, "BOSATT_UTLAND"));

    static {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Oslo"));
    }

    FpsakBehandlingProsessEventDto eventDrammenFra(Map<String, String> aksjonspunktmap){
        return prosessBuilderFra(aksjonspunktmap)
                //.medId("EKSTERN_ID")
                .medEksternId(UUID.nameUUIDFromBytes(behandlingId.toString().getBytes()))
                .medBehandlendeEnhet("4802")
                .build();
    }

    private FpsakBehandlingProsessEventDto eventStordFra(Map<String, String> aksjonspunktmap){
        return prosessBuilderFra(aksjonspunktmap)
                //.medId("EKSTERN_ID")
                .medEksternId(UUID.nameUUIDFromBytes(behandlingId.toString().getBytes()))
                .medBehandlendeEnhet("4842")
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

    private Aksjonspunkt aksjonspunktDtoFra(String aksjonspunkKode, String status, LocalDateTime aksjonspunktFrist){
        return Aksjonspunkt.builder()
                .medDefinisjon(aksjonspunkKode)
                .medStatus(status)
                .medFristTid(aksjonspunktFrist)
                .build();
    }

    private FpsakBehandlingProsessEventDto.Builder prosessBuilderFra(Map<String, String> aksjonspunktmap){
        return FpsakBehandlingProsessEventDto.builder()
                .medEksternId(UUID.nameUUIDFromBytes(behandlingId.toString().getBytes()))
                .medBehandlingId(behandlingId)
                .medSaksnummer("135701264")
                .medAktørId("9000000030703")
                .medEventHendelse(EventHendelse.AKSJONSPUNKT_OPPRETTET)
                .medBehandlingStatus("STATUS")
                .medBehandlingSteg("STEG")
                .medYtelseTypeKode(FagsakYtelseType.FORELDREPENGER.getKode())
                .medBehandlingTypeKode(BehandlingType.FØRSTEGANGSSØKNAD.getKode())
                .medOpprettetBehandling(LocalDateTime.now())
                .medAksjonspunktKoderMedStatusListe(aksjonspunktmap);
    }

    private static BehandlingFpsak behandlingDtoFra(List<Aksjonspunkt> aksjonspunkter) {
        return behandlingBuilderMal()
                .medUuid(UUID.nameUUIDFromBytes(behandlingId.toString().getBytes()))
                .medAksjonspunkter(aksjonspunkter)
                .build();
    }

    private static BehandlingFpsak behandlingDtoMedManueltMarkertUtlandsakFra(List<Aksjonspunkt> aksjonspunkter){
        return behandlingBuilderMal()
                .medUuid(UUID.nameUUIDFromBytes(behandlingId.toString().getBytes()))
                .medAksjonspunkter(aksjonspunkter)
                .build();
    }

    private BehandlingFpsak lagBehandlingDtoMedHarGradering(List<Aksjonspunkt> aksjonspunkter){
        return behandlingBuilderMal()
                .medUuid(UUID.nameUUIDFromBytes(behandlingId.toString().getBytes()))
                .medHarGradering(true)
                .medAksjonspunkter(aksjonspunkter)
                .build();
    }

    @Test
    public void testEnkelOppgave(){
        when(foreldrepengerBehandlingRestKlient.getBehandling(anyLong())).thenReturn(behandlingDtoFra(aksjonspunktKoderSkalHaOppgaveDto));
        fpsakEventHandler.prosesser(eventDrammenFra(aksjonspunktKoderSkalHaOppgave));
        assertThat(repoRule.getRepository().hentAlle(Oppgave.class)).hasSize(1);
        Oppgave oppgave = repoRule.getRepository().hentAlle(Oppgave.class).get(0);
        assertThat(oppgave.getAktiv()).isTrue();
        assertThat(repoRule.getRepository().hentAlle(OppgaveEgenskap.class)).hasSize(0);
        assertThat(repoRule.getRepository().hentAlle(OppgaveEventLogg.class)).hasSize(1);
    }

    @Test
    public void opprettingOgAvsluttingOverTilBehandlerTest(){
        when(foreldrepengerBehandlingRestKlient.getBehandling(anyLong())).thenReturn(behandlingDtoFra(aksjonspunktKoderSkalHaOppgaveDto));
        fpsakEventHandler.prosesser(eventDrammenFra(aksjonspunktKoderSkalHaOppgave));
        when(foreldrepengerBehandlingRestKlient.getBehandling(anyLong())).thenReturn(behandlingDtoFra(aksjonspunktKoderSkalLukkeOppgaveDto));
        fpsakEventHandler.prosesser(eventDrammenFra(aksjonspunktKoderSkalLukkeOppgave));
        when(foreldrepengerBehandlingRestKlient.getBehandling(anyLong())).thenReturn(behandlingDtoFra(aksjonspunktKoderTilBeslutterDto));
        fpsakEventHandler.prosesser(eventDrammenFra(aksjonspunktKoderTilBeslutter));
        when(foreldrepengerBehandlingRestKlient.getBehandling(anyLong())).thenReturn(behandlingDtoFra(aksjonspunktKoderSkalLukkeOppgaveDto));
        fpsakEventHandler.prosesser(eventDrammenFra(aksjonspunktKoderSkalLukkeOppgave));
        assertThat(repoRule.getRepository().hentAlle(Oppgave.class)).hasSize(2);
        assertThat(repoRule.getRepository().hentAlle(OppgaveEgenskap.class)).hasSize(1);
        assertThat(repoRule.getRepository().hentAlle(OppgaveEventLogg.class)).hasSize(4);
    }

    @Test
    public void opprettingOgOverTilBehandlerTest(){
        when(foreldrepengerBehandlingRestKlient.getBehandling(anyLong())).thenReturn(behandlingDtoFra(aksjonspunktKoderSkalHaOppgaveDto));
        fpsakEventHandler.prosesser(eventDrammenFra(aksjonspunktKoderSkalHaOppgave));
        when(foreldrepengerBehandlingRestKlient.getBehandling(anyLong())).thenReturn(behandlingDtoFra(aksjonspunktKoderTilBeslutterDto));
        fpsakEventHandler.prosesser(eventDrammenFra(aksjonspunktKoderTilBeslutter));

        List<Oppgave> oppgaver = repoRule.getRepository().hentAlle(Oppgave.class);
        assertThat(oppgaver).hasSize(2);

        //Sjekke at det er behandleroppgaven som er aktiv
        OppgaveEgenskap oppgaveEgenskap = repoRule.getRepository().hentAlle(OppgaveEgenskap.class).get(0);
        assertThat(oppgaveEgenskap.getOppgave().getAktiv()).isTrue();

        //Sjekker at det siste eventet er å opprette og at det rett før er lukker.
        List<OppgaveEventLogg> oppgaveEventLogg = oppgaveRepository.hentEventer(behandlingId);

        /*Optional<EksternIdentifikator> eksternId = oppgaveRepositoryProvider.getEksternIdentifikatorRepository().finnIdentifikator("FPSAK", behandlingId.toString());
        List<OppgaveEventLogg> oppgaveEventLogg = oppgaveRepositoryProvider.getOppgaveRepository().hentEventerForEksternId(eksternId.get().getId());*/
        assertThat(oppgaveEventLogg.get(0).getEventType()).isEqualTo(OppgaveEventType.OPPRETTET);
        assertThat(oppgaveEventLogg.get(1).getEventType()).isEqualTo(OppgaveEventType.LUKKET);
    }

    @Test
    public void opprettingOgAvsluttingTest(){
        when(foreldrepengerBehandlingRestKlient.getBehandling(anyLong())).thenReturn(behandlingDtoFra(aksjonspunktKoderSkalHaOppgaveDto));
        fpsakEventHandler.prosesser(eventDrammenFra(aksjonspunktKoderSkalHaOppgave));
        when(foreldrepengerBehandlingRestKlient.getBehandling(anyLong())).thenReturn(behandlingDtoFra(aksjonspunktKoderSkalLukkeOppgaveDto));
        fpsakEventHandler.prosesser(eventDrammenFra(aksjonspunktKoderSkalLukkeOppgave));
        when(foreldrepengerBehandlingRestKlient.getBehandling(anyLong())).thenReturn(behandlingDtoFra(aksjonspunktKoderSkalHaOppgaveDto));
        fpsakEventHandler.prosesser(eventDrammenFra(aksjonspunktKoderSkalHaOppgave));
        when(foreldrepengerBehandlingRestKlient.getBehandling(anyLong())).thenReturn(behandlingDtoFra(aksjonspunktKoderSkalLukkeOppgaveDto));
        fpsakEventHandler.prosesser(eventDrammenFra(aksjonspunktKoderSkalLukkeOppgave));
        assertThat(repoRule.getRepository().hentAlle(Oppgave.class)).hasSize(1);
        assertThat(repoRule.getRepository().hentAlle(OppgaveEgenskap.class)).hasSize(0);
    }

    @Test
    public void opprettingOPåVentTest(){
        when(foreldrepengerBehandlingRestKlient.getBehandling(anyLong())).thenReturn(behandlingDtoFra(aksjonspunktKoderSkalHaOppgaveDto));
        fpsakEventHandler.prosesser(eventDrammenFra(aksjonspunktKoderSkalHaOppgave));
        when(foreldrepengerBehandlingRestKlient.getBehandling(anyLong())).thenReturn(behandlingDtoFra(aksjonspunktKoderPåVentDto));
        fpsakEventHandler.prosesser(eventDrammenFra(aksjonspunktKoderPåVent));
        assertThat(repoRule.getRepository().hentAlle(Oppgave.class)).hasSize(1);
        Oppgave oppgave = repoRule.getRepository().hentAlle(Oppgave.class).get(0);
        assertThat(oppgave.getAktiv()).isFalse();
    }

    @Test
    public void flyttingAvBehandlendeEnhetTest(){
        when(foreldrepengerBehandlingRestKlient.getBehandling(anyLong())).thenReturn(behandlingDtoFra(aksjonspunktKoderSkalHaOppgaveDto));
        fpsakEventHandler.prosesser(eventDrammenFra(aksjonspunktKoderSkalHaOppgave));
        when(foreldrepengerBehandlingRestKlient.getBehandling(anyLong())).thenReturn(behandlingDtoFra(aksjonspunktKoderSkalHaOppgaveDto));
        fpsakEventHandler.prosesser(eventStordFra(aksjonspunktKoderSkalHaOppgave));
        assertThat(repoRule.getRepository().hentAlle(Oppgave.class)).hasSize(2);
    }

    @Test
    public void opprettingOppgaveMedEgenskapTest(){
        when(foreldrepengerBehandlingRestKlient.getBehandling(anyLong())).thenReturn(behandlingDtoFra(aksjonspunktKoderTilBeslutterDto));
        fpsakEventHandler.prosesser(eventDrammenFra(aksjonspunktKoderTilBeslutter));
        assertThat(repoRule.getRepository().hentAlle(Oppgave.class)).hasSize(1);
        sjekkForEnOppgaveOgEgenskap(AndreKriterierType.TIL_BESLUTTER);
        sjekkEventLoggInneholder(behandlingId, OppgaveEventType.OPPRETTET, AndreKriterierType.TIL_BESLUTTER);
    }


    @Test
    public void opprettingOppgaveMedEgenskapPapirsøknadESTest(){
        when(foreldrepengerBehandlingRestKlient.getBehandling(anyLong())).thenReturn(behandlingDtoFra(aksjonspunktKoderPapirsøknadESDto));
        fpsakEventHandler.prosesser(eventDrammenFra(aksjonspunktKoderPapirsøknadES));
        sjekkForEnOppgaveOgEgenskap(AndreKriterierType.PAPIRSØKNAD);
        sjekkEventLoggInneholder(behandlingId, OppgaveEventType.OPPRETTET, AndreKriterierType.PAPIRSØKNAD);
    }

    @Test
    public void opprettingOppgaveMedEgenskapPapirsøknadFPTest(){
        when(foreldrepengerBehandlingRestKlient.getBehandling(anyLong())).thenReturn(behandlingDtoFra(aksjonspunktKoderPapirsøknadFPDto));
        fpsakEventHandler.prosesser(eventDrammenFra(aksjonspunktKoderPapirsøknadFP));
        sjekkForEnOppgaveOgEgenskap(AndreKriterierType.PAPIRSØKNAD);
        sjekkEventLoggInneholder(behandlingId, OppgaveEventType.OPPRETTET, AndreKriterierType.PAPIRSØKNAD);
    }

    @Test
    public void opprettingOppgaveMedEgenskapPapirsøknadEndringFPTest(){
        when(foreldrepengerBehandlingRestKlient.getBehandling(anyLong()))
                .thenReturn(behandlingDtoFra(aksjonspunktKoderPapirsøknadEndringFPDto));
        fpsakEventHandler.prosesser(eventDrammenFra(aksjonspunktKoderPapirsøknadEndringFP));
        sjekkForEnOppgaveOgEgenskap(AndreKriterierType.PAPIRSØKNAD);
        sjekkEventLoggInneholder(behandlingId, OppgaveEventType.OPPRETTET, AndreKriterierType.PAPIRSØKNAD);
    }

//    @Test
//    public void opprettingOppgaveMedEgenskapSelvstendigFrilanserFPTest() {
//        when(foreldrepengerBehandlingRestKlient.getBehandling(anyLong())).thenReturn(behandlingDtoFra(aksjonspunktKoderSelvstendigFrilanserFPDto));
//        fpsakEventHandler.prosesser(eventDrammenFra(aksjonspunktKoderSelvstendigFrilanserFP));
//        sjekkForEnOppgaveOgEgenskap(AndreKriterierType.SELVSTENDIG_FRILANSER);
//    }

    @Test
    public void opprettingOppgavemedEgenskapHarGraderingFPTest() {
        when(foreldrepengerBehandlingRestKlient.getBehandling(anyLong())).thenReturn(lagBehandlingDtoMedHarGradering(aksjonspunktKoderPapirsøknadEndringFPDto));
        fpsakEventHandler.prosesser(eventDrammenFra(aksjonspunktKoderPapirsøknadEndringFP));
        sjekkForEnOppgaveOgEgenskap(AndreKriterierType.SOKT_GRADERING,1,2);
    }

    @Test
    public void opprettingOppgaveMedEgenskapUtlandAutomatiskMarkertTest(){
        when(foreldrepengerBehandlingRestKlient.getBehandling(anyLong())).thenReturn(behandlingDtoFra(aksjonspunktKoderUtlandAutomatiskDto));
        fpsakEventHandler.prosesser(eventDrammenFra(aksjonspunktKoderUtland));
        sjekkForEnOppgaveOgEgenskap(AndreKriterierType.UTLANDSSAK);
    }

    @Test
    public void opprettingOppgaveMedEgenskapUtlandManueltMarkertTest(){
        when(foreldrepengerBehandlingRestKlient.getBehandling(anyLong())).thenReturn(behandlingDtoMedManueltMarkertUtlandsakFra(aksjonspunktKoderUtlandManuellDto));
        fpsakEventHandler.prosesser(eventDrammenFra(aksjonspunktKoderSkalHaOppgave));
        sjekkForEnOppgaveOgEgenskap(AndreKriterierType.UTLANDSSAK);
    }

    @Test
    public void opprettingOppgaveSkalIkkeKommeOppForSaksbehandlerSomSendteTilBeslutterTest(){
        when(foreldrepengerBehandlingRestKlient.getBehandling(anyLong())).thenReturn(behandlingDtoFra(aksjonspunktKoderSkalHaOppgaveDto));
        fpsakEventHandler.prosesser(eventDrammenFra(aksjonspunktKoderSkalHaOppgave));
        when(foreldrepengerBehandlingRestKlient.getBehandling(anyLong())).thenReturn(behandlingDtoFra(aksjonspunktKoderTilBeslutterDto));
        fpsakEventHandler.prosesser(eventDrammenFra(aksjonspunktKoderTilBeslutter));

        OppgaveEgenskap oppgaveEgenskap = repoRule.getRepository().hentAlle(OppgaveEgenskap.class).get(0);
        assertThat(oppgaveEgenskap.getSisteSaksbehandlerForTotrinn()).isEqualTo("VLLOS");
    }

    @Test
    public void lukkingOppgavePgaPåVentBeslutterTest(){
        when(foreldrepengerBehandlingRestKlient.getBehandling(anyLong())).thenReturn(behandlingDtoFra(aksjonspunktKoderSkalPåVentDto));
        fpsakEventHandler.prosesser(eventDrammenFra(aksjonspunktKoderSkalPåVent));
        OppgaveEventLogg oppgaveEventLogg = repoRule.getRepository().hentAlle(OppgaveEventLogg.class).get(0);
        assertThat(oppgaveEventLogg.getEventType()).isEqualTo(OppgaveEventType.VENT);
    }

    @Test
    public void lukkingOppgavePgaManueltPåVentBeslutterTest(){
        when(foreldrepengerBehandlingRestKlient.getBehandling(anyLong())).thenReturn(behandlingDtoFra(aksjonspunktKoderSkalPåManueltVentDto));
        fpsakEventHandler.prosesser(eventDrammenFra(aksjonspunktKoderSkalPåManueltVent));
        OppgaveEventLogg oppgaveEventLogg = repoRule.getRepository().hentAlle(OppgaveEventLogg.class).get(0);
        assertThat(oppgaveEventLogg.getEventType()).isEqualTo(OppgaveEventType.MANU_VENT);
    }


    private void sjekkEventLoggInneholder(Long behandlingId, OppgaveEventType eventType, AndreKriterierType kriterierType) {
        /*Optional<EksternIdentifikator> eksternIdentifikator = oppgaveRepositoryProvider.getEksternIdentifikatorRepository().finnIdentifikator(fagsystem, behandlingId.toString());
        List<OppgaveEventLogg> oppgaveEventLoggs = oppgaveRepositoryProvider.getOppgaveRepository().hentEventerForEksternId(eksternIdentifikator.get().getId());*/
        List<OppgaveEventLogg> oppgaveEventLoggs = oppgaveRepository.hentEventer(behandlingId);

        assertThat(oppgaveEventLoggs.get(0).getEventType()).isEqualTo(eventType);
        if (kriterierType != null) {
            assertThat(oppgaveEventLoggs.get(0).getAndreKriterierType()).isEqualTo(kriterierType);
        }
    }

    private void sjekkForEnOppgaveOgEgenskap(AndreKriterierType egenskap) {
        sjekkForEnOppgaveOgEgenskap(egenskap, 1, 1);
    }

    private void sjekkForEnOppgaveOgEgenskap(AndreKriterierType egenskap, int antallOppgaver, int antallEgenskaper) {
        assertThat(repoRule.getRepository().hentAlle(Oppgave.class)).hasSize(antallOppgaver);
        var oppgaveEgenskaper = repoRule.getRepository().hentAlle(OppgaveEgenskap.class);
        assertThat(repoRule.getRepository().hentAlle(OppgaveEgenskap.class)).hasSize(antallEgenskaper);
        assertThat(repoRule.getRepository().hentAlle(OppgaveEgenskap.class))
                .extracting(OppgaveEgenskap::getAndreKriterierType).contains(egenskap);
    }
}
