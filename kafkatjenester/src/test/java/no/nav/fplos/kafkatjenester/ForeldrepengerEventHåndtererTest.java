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
import no.nav.fplos.kafkatjenester.AksjonspunktTest.KodeStatus;
import no.nav.vedtak.felles.integrasjon.kafka.EventHendelse;
import no.nav.vedtak.felles.integrasjon.kafka.Fagsystem;
import org.junit.Rule;
import org.junit.Test;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static no.nav.fplos.kafkatjenester.TestUtil.behandlingBuilderMal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ForeldrepengerEventHåndtererTest {

    @Rule
    public UnittestRepositoryRule repoRule = new UnittestRepositoryRule();
    private EntityManager entityManager = repoRule.getEntityManager();
    private OppgaveRepository oppgaveRepository = new OppgaveRepositoryImpl(entityManager);
    private OppgaveEgenskapHandler oppgaveEgenskapHandler = new OppgaveEgenskapHandler(oppgaveRepository);
    private ForeldrepengerBehandlingRestKlient fpsak = mock(ForeldrepengerBehandlingRestKlient.class);
    private ForeldrepengerEventHåndterer handler = new ForeldrepengerEventHåndterer(oppgaveRepository, fpsak, oppgaveEgenskapHandler);
    private static Long behandlingId = 1073051L;
    private static UUID uuid = UUID.fromString("027961C0-1DA9-1D46-AFAA-0BBAE024758C"); //UUID.nameUUIDFromBytes("TEST".getBytes());

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

        // 7003 oppr -> utfo

    FpsakBehandlingProsessEventDto eventDrammenFra(Map<String, String> aksjonspunktmap){
        return (FpsakBehandlingProsessEventDto) prosessBuilderFra(aksjonspunktmap)
                .medEksternId(uuid)
                .medBehandlendeEnhet("4802")
                .build();
    }

    private FpsakBehandlingProsessEventDto eventStordFra(Map<String, String> aksjonspunktmap){
        return (FpsakBehandlingProsessEventDto) prosessBuilderFra(aksjonspunktmap)
                .medEksternId(uuid)
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

    public static Aksjonspunkt aksjonspunktDtoFra(String aksjonspunkKode, String status, LocalDateTime aksjonspunktFrist){
        return Aksjonspunkt.builder()
                .medDefinisjon(aksjonspunkKode)
                .medStatus(status)
                .medFristTid(aksjonspunktFrist)
                .build();
    }

    private FpsakBehandlingProsessEventDto.Builder prosessBuilderFra(Map<String, String> aksjonspunktmap){
        return FpsakBehandlingProsessEventDto.builder()
                .medFagsystem(Fagsystem.FPSAK)
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
                .medUuid(uuid)
                .medAksjonspunkter(aksjonspunkter)
                .build();
    }

    private static BehandlingFpsak behandlingDtoMedManueltMarkertUtlandsakFra(List<Aksjonspunkt> aksjonspunkter){
        return behandlingBuilderMal()
                .medUuid(uuid)
                .medAksjonspunkter(aksjonspunkter)
                .build();
    }

    private BehandlingFpsak lagBehandlingDtoMedHarGradering(List<Aksjonspunkt> aksjonspunkter){
        return behandlingBuilderMal()
                .medUuid(uuid)
                .medHarGradering(true)
                .medAksjonspunkter(aksjonspunkter)
                .build();
    }

    @Test
    public void testEnkelOppgave(){
        when(fpsak.getBehandling(anyLong())).thenReturn(behandlingDtoFra(aksjonspunktKoderSkalHaOppgaveDto));
        handler.håndterEvent(eventDrammenFra(aksjonspunktKoderSkalHaOppgave));
        assertThat(repoRule.getRepository().hentAlle(Oppgave.class)).hasSize(1);
        Oppgave oppgave = repoRule.getRepository().hentAlle(Oppgave.class).get(0);
        assertThat(oppgave.getAktiv()).isTrue();
        assertThat(repoRule.getRepository().hentAlle(OppgaveEgenskap.class)).hasSize(0);
        assertThat(repoRule.getRepository().hentAlle(OppgaveEventLogg.class)).hasSize(1);
    }

    @Test
    public void opprettingOgAvsluttingOverTilBehandlerTest(){
        when(fpsak.getBehandling(anyLong())).thenReturn(behandlingDtoFra(aksjonspunktKoderSkalHaOppgaveDto));
        handler.håndterEvent(eventDrammenFra(aksjonspunktKoderSkalHaOppgave));
        when(fpsak.getBehandling(anyLong())).thenReturn(behandlingDtoFra(aksjonspunktKoderSkalLukkeOppgaveDto));
        handler.håndterEvent(eventDrammenFra(aksjonspunktKoderSkalLukkeOppgave));
        when(fpsak.getBehandling(anyLong())).thenReturn(behandlingDtoFra(aksjonspunktKoderTilBeslutterDto));
        handler.håndterEvent(eventDrammenFra(aksjonspunktKoderTilBeslutter));
        when(fpsak.getBehandling(anyLong())).thenReturn(behandlingDtoFra(aksjonspunktKoderSkalLukkeOppgaveDto));
        handler.håndterEvent(eventDrammenFra(aksjonspunktKoderSkalLukkeOppgave));
        assertThat(repoRule.getRepository().hentAlle(Oppgave.class)).hasSize(2);
        assertThat(repoRule.getRepository().hentAlle(OppgaveEgenskap.class)).hasSize(1);
        var alle = repoRule.getRepository().hentAlle(OppgaveEventLogg.class);
        assertThat(repoRule.getRepository().hentAlle(OppgaveEventLogg.class)).hasSize(4);
    }

    @Test
    public void opprettingOgOverTilBehandlerTest(){
        when(fpsak.getBehandling(anyLong())).thenReturn(behandlingDtoFra(aksjonspunktKoderSkalHaOppgaveDto));
        handler.håndterEvent(eventDrammenFra(aksjonspunktKoderSkalHaOppgave));
        when(fpsak.getBehandling(anyLong())).thenReturn(behandlingDtoFra(aksjonspunktKoderTilBeslutterDto));
        handler.håndterEvent(eventDrammenFra(aksjonspunktKoderTilBeslutter));

        List<Oppgave> oppgaver = repoRule.getRepository().hentAlle(Oppgave.class);
        assertThat(oppgaver).hasSize(2);

        //Sjekke at det er behandleroppgaven som er aktiv
        OppgaveEgenskap oppgaveEgenskap = repoRule.getRepository().hentAlle(OppgaveEgenskap.class).get(0);
        assertThat(oppgaveEgenskap.getOppgave().getAktiv()).isTrue();

        //Sjekker at det siste eventet er å opprette og at det rett før er lukker.
        List<OppgaveEventLogg> oppgaveEventLogg = oppgaveRepository.hentOppgaveEventer(uuid);

        /*Optional<EksternIdentifikator> eksternId = oppgaveRepositoryProvider.getEksternIdentifikatorRepository().finnIdentifikator("FPSAK", behandlingId.toString());
        List<OppgaveEventLogg> oppgaveEventLogg = oppgaveRepositoryProvider.getOppgaveRepository().hentEventerForEksternId(eksternId.get().getId());*/
        assertThat(oppgaveEventLogg.get(0).getEventType()).isEqualTo(OppgaveEventType.OPPRETTET);
        assertThat(oppgaveEventLogg.get(1).getEventType()).isEqualTo(OppgaveEventType.LUKKET);
    }

    @Test
    public void opprettingOgAvsluttingTest(){
        when(fpsak.getBehandling(anyLong())).thenReturn(behandlingDtoFra(aksjonspunktKoderSkalHaOppgaveDto));
        handler.håndterEvent(eventDrammenFra(aksjonspunktKoderSkalHaOppgave));
        when(fpsak.getBehandling(anyLong())).thenReturn(behandlingDtoFra(aksjonspunktKoderSkalLukkeOppgaveDto));
        handler.håndterEvent(eventDrammenFra(aksjonspunktKoderSkalLukkeOppgave));
        when(fpsak.getBehandling(anyLong())).thenReturn(behandlingDtoFra(aksjonspunktKoderSkalHaOppgaveDto));
        handler.håndterEvent(eventDrammenFra(aksjonspunktKoderSkalHaOppgave));
        when(fpsak.getBehandling(anyLong())).thenReturn(behandlingDtoFra(aksjonspunktKoderSkalLukkeOppgaveDto));
        handler.håndterEvent(eventDrammenFra(aksjonspunktKoderSkalLukkeOppgave));
        assertThat(repoRule.getRepository().hentAlle(Oppgave.class)).hasSize(1);
        assertThat(repoRule.getRepository().hentAlle(OppgaveEgenskap.class)).hasSize(0);
    }

    @Test
    public void opprettingOPåVentTest(){
        when(fpsak.getBehandling(anyLong())).thenReturn(behandlingDtoFra(aksjonspunktKoderSkalHaOppgaveDto));
        handler.håndterEvent(eventDrammenFra(aksjonspunktKoderSkalHaOppgave));
        when(fpsak.getBehandling(anyLong())).thenReturn(behandlingDtoFra(aksjonspunktKoderPåVentDto));
        handler.håndterEvent(eventDrammenFra(aksjonspunktKoderPåVent));
        assertThat(repoRule.getRepository().hentAlle(Oppgave.class)).hasSize(1);
        Oppgave oppgave = repoRule.getRepository().hentAlle(Oppgave.class).get(0);
        assertThat(oppgave.getAktiv()).isFalse();
    }

    @Test
    public void flyttingAvBehandlendeEnhetTest(){
        when(fpsak.getBehandling(anyLong())).thenReturn(behandlingDtoFra(aksjonspunktKoderSkalHaOppgaveDto));
        handler.håndterEvent(eventDrammenFra(aksjonspunktKoderSkalHaOppgave));
        when(fpsak.getBehandling(anyLong())).thenReturn(behandlingDtoFra(aksjonspunktKoderSkalHaOppgaveDto));
        handler.håndterEvent(eventStordFra(aksjonspunktKoderSkalHaOppgave));
        assertThat(repoRule.getRepository().hentAlle(Oppgave.class)).hasSize(2);
    }

    @Test
    public void opprettingOppgaveMedEgenskapTest(){
        when(fpsak.getBehandling(anyLong())).thenReturn(behandlingDtoFra(aksjonspunktKoderTilBeslutterDto));
        handler.håndterEvent(eventDrammenFra(aksjonspunktKoderTilBeslutter));
        assertThat(repoRule.getRepository().hentAlle(Oppgave.class)).hasSize(1);
        sjekkForEnOppgaveOgEgenskap(AndreKriterierType.TIL_BESLUTTER);
        sjekkEventLoggInneholder(uuid, OppgaveEventType.OPPRETTET, AndreKriterierType.TIL_BESLUTTER);
    }


    @Test
    public void opprettingOppgaveMedEgenskapPapirsøknadESTest(){
        when(fpsak.getBehandling(anyLong())).thenReturn(behandlingDtoFra(aksjonspunktKoderPapirsøknadESDto));
        handler.håndterEvent(eventDrammenFra(aksjonspunktKoderPapirsøknadES));
        sjekkForEnOppgaveOgEgenskap(AndreKriterierType.PAPIRSØKNAD);
        sjekkEventLoggInneholder(uuid, OppgaveEventType.OPPRETTET, AndreKriterierType.PAPIRSØKNAD);
    }

    @Test
    public void opprettingOppgaveMedEgenskapPapirsøknadFPTest(){
        when(fpsak.getBehandling(anyLong())).thenReturn(behandlingDtoFra(aksjonspunktKoderPapirsøknadFPDto));
        handler.håndterEvent(eventDrammenFra(aksjonspunktKoderPapirsøknadFP));
        sjekkForEnOppgaveOgEgenskap(AndreKriterierType.PAPIRSØKNAD);
        sjekkEventLoggInneholder(uuid, OppgaveEventType.OPPRETTET, AndreKriterierType.PAPIRSØKNAD);
    }

    @Test
    public void opprettingOppgaveMedEgenskapPapirsøknadEndringFPTest(){
        when(fpsak.getBehandling(anyLong()))
                .thenReturn(behandlingDtoFra(aksjonspunktKoderPapirsøknadEndringFPDto));
        handler.håndterEvent(eventDrammenFra(aksjonspunktKoderPapirsøknadEndringFP));
        sjekkForEnOppgaveOgEgenskap(AndreKriterierType.PAPIRSØKNAD);
        sjekkEventLoggInneholder(uuid, OppgaveEventType.OPPRETTET, AndreKriterierType.PAPIRSØKNAD);
    }

//    @Test
//    public void opprettingOppgaveMedEgenskapSelvstendigFrilanserFPTest() {
//        when(foreldrepengerBehandlingRestKlient.getBehandling(anyLong())).thenReturn(behandlingDtoFra(aksjonspunktKoderSelvstendigFrilanserFPDto));
//        fpsakEventHandler.prosesser(eventDrammenFra(aksjonspunktKoderSelvstendigFrilanserFP));
//        sjekkForEnOppgaveOgEgenskap(AndreKriterierType.SELVSTENDIG_FRILANSER);
//    }

    @Test
    public void opprettingOppgavemedEgenskapHarGraderingFPTest() {
        when(fpsak.getBehandling(anyLong())).thenReturn(lagBehandlingDtoMedHarGradering(aksjonspunktKoderPapirsøknadEndringFPDto));
        handler.håndterEvent(eventDrammenFra(aksjonspunktKoderPapirsøknadEndringFP));
        sjekkForEnOppgaveOgEgenskap(AndreKriterierType.SOKT_GRADERING,1,2);
    }

    @Test
    public void opprettingOppgaveMedEgenskapUtlandAutomatiskMarkertTest(){
        when(fpsak.getBehandling(anyLong())).thenReturn(behandlingDtoFra(aksjonspunktKoderUtlandAutomatiskDto));
        handler.håndterEvent(eventDrammenFra(aksjonspunktKoderUtland));
        sjekkForEnOppgaveOgEgenskap(AndreKriterierType.UTLANDSSAK);
    }

    @Test
    public void opprettingOppgaveMedEgenskapUtlandManueltMarkertTest(){
        when(fpsak.getBehandling(anyLong())).thenReturn(behandlingDtoMedManueltMarkertUtlandsakFra(aksjonspunktKoderUtlandManuellDto));
        handler.håndterEvent(eventDrammenFra(aksjonspunktKoderSkalHaOppgave));
        sjekkForEnOppgaveOgEgenskap(AndreKriterierType.UTLANDSSAK);
    }

    @Test
    public void opprettingOppgaveSkalIkkeKommeOppForSaksbehandlerSomSendteTilBeslutterTest(){
        when(fpsak.getBehandling(anyLong())).thenReturn(behandlingDtoFra(aksjonspunktKoderSkalHaOppgaveDto));
        handler.håndterEvent(eventDrammenFra(aksjonspunktKoderSkalHaOppgave));
        when(fpsak.getBehandling(anyLong())).thenReturn(behandlingDtoFra(aksjonspunktKoderTilBeslutterDto));
        handler.håndterEvent(eventDrammenFra(aksjonspunktKoderTilBeslutter));

        OppgaveEgenskap oppgaveEgenskap = repoRule.getRepository().hentAlle(OppgaveEgenskap.class).get(0);
        assertThat(oppgaveEgenskap.getSisteSaksbehandlerForTotrinn()).isEqualTo("VLLOS");
    }

    @Test
    public void lukkingOppgavePgaPåVentBeslutterTest(){
        when(fpsak.getBehandling(anyLong())).thenReturn(behandlingDtoFra(aksjonspunktKoderSkalPåVentDto));
        handler.håndterEvent(eventDrammenFra(aksjonspunktKoderSkalPåVent));
        OppgaveEventLogg oppgaveEventLogg = repoRule.getRepository().hentAlle(OppgaveEventLogg.class).get(0);
        assertThat(oppgaveEventLogg.getEventType()).isEqualTo(OppgaveEventType.VENT);
    }

    @Test
    public void lukkingOppgavePgaManueltPåVentBeslutterTest(){
        when(fpsak.getBehandling(anyLong())).thenReturn(behandlingDtoFra(aksjonspunktKoderSkalPåManueltVentDto));
        handler.håndterEvent(eventDrammenFra(aksjonspunktKoderSkalPåManueltVent));
        OppgaveEventLogg oppgaveEventLogg = repoRule.getRepository().hentAlle(OppgaveEventLogg.class).get(0);
        assertThat(oppgaveEventLogg.getEventType()).isEqualTo(OppgaveEventType.MANU_VENT);
    }

    @Test
    public void skalKunVæreEnÅpenOppgavePerBehandling() {
        var første = aksjonspunkt("7003", KodeStatus.OPPR);
        behandle(første);

        var andre = aksjonspunkt("7003", KodeStatus.UTFO);
        behandle(andre);

        var tredje = aksjonspunkt("5058", KodeStatus.OPPR);
        tredje.addKode("7003", KodeStatus.UTFO);
        behandle(tredje);

        var fjerde = aksjonspunkt("5038", KodeStatus.OPPR);
        fjerde.addKode("5058", KodeStatus.UTFO);
        fjerde.addKode("7003", KodeStatus.UTFO);
        behandle(fjerde);

        sjekkKunEnAktivOppgave();
    }

    private void behandle(AksjonspunktTest aksjonspunktTest) {
        konfigFpsakMock(aksjonspunktTest.getAksjonspunkt());
        handler.håndterEvent(eventDrammenFra(aksjonspunktTest.getDto()));
    }

    private void konfigFpsakMock(List<Aksjonspunkt> aksjonspunkt) {
        when(fpsak.getBehandling(anyLong())).thenReturn(behandlingDtoFra(aksjonspunkt));
    }

    private void sjekkAntallOppgaver(int antall) {
        assertThat(repoRule.getRepository().hentAlle(Oppgave.class)).hasSize(antall);
    }

    private void sjekkAktivOppgaveEksisterer(boolean aktiv) {
        List<Oppgave> oppgave = repoRule.getRepository().hentAlle(Oppgave.class);
        assertThat(oppgave.get(0).getAktiv()).isEqualTo(aktiv);
        int antallAktive = (int) oppgave.stream().filter(Oppgave::getAktiv).count();
        assertThat(antallAktive).isEqualTo(aktiv ? 1 : 0);
    }

    private void sjekkKunEnAktivOppgave() {
        List<Oppgave> oppgave = repoRule.getRepository().hentAlle(Oppgave.class);
        long antallAktive = oppgave.stream().filter(Oppgave::getAktiv).count();
        assertThat(antallAktive).isEqualTo(1L);
    }

    private void sjekkOppgaveEventAntallEr(int antall) {
        var eventer = repoRule.getRepository().hentAlle(OppgaveEventLogg.class);
        assertThat(eventer).hasSize(antall);
    }

    private void sjekkEventLoggInneholder(UUID uuid, OppgaveEventType eventType, AndreKriterierType kriterierType) {
        List<OppgaveEventLogg> oppgaveEventLoggs = oppgaveRepository.hentOppgaveEventer(uuid);

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

    public static AksjonspunktTest aksjonspunkt(String kode, KodeStatus status) {
        return new AksjonspunktTest(kode, status);
    }
}
