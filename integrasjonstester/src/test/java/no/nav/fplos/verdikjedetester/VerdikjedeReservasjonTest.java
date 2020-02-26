package no.nav.fplos.verdikjedetester;

import no.nav.foreldrepenger.dbstoette.UnittestRepositoryRule;
import no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;
import no.nav.foreldrepenger.loslager.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventLogg;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventType;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepository;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepositoryImpl;
import no.nav.foreldrepenger.loslager.repository.OrganisasjonRepository;
import no.nav.fplos.ansatt.AnsattTjeneste;
import no.nav.fplos.avdelingsleder.AvdelingslederTjeneste;
import no.nav.fplos.foreldrepengerbehandling.Aksjonspunkt;
import no.nav.fplos.foreldrepengerbehandling.BehandlingFpsak;
import no.nav.fplos.foreldrepengerbehandling.ForeldrepengerBehandlingRestKlient;
import no.nav.fplos.kafkatjenester.FpsakBehandlingProsessEventDto;
import no.nav.fplos.kafkatjenester.ForeldrepengerEventHåndterer;
import no.nav.fplos.kafkatjenester.OppgaveEgenskapHandler;
import no.nav.fplos.oppgave.OppgaveTjeneste;
import no.nav.fplos.oppgave.OppgaveTjenesteImpl;
import no.nav.fplos.person.api.TpsTjeneste;
import no.nav.vedtak.felles.integrasjon.kafka.EventHendelse;
import no.nav.vedtak.felles.integrasjon.kafka.Fagsystem;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VerdikjedeReservasjonTest {
    @Rule
    public UnittestRepositoryRule repoRule = new UnittestRepositoryRule();
    private EntityManager entityManager = repoRule.getEntityManager();
    private OppgaveRepository oppgaveRepository = new OppgaveRepositoryImpl(entityManager);

    private OppgaveEgenskapHandler oppgaveEgenskapHandler = new OppgaveEgenskapHandler(oppgaveRepository);
    private ForeldrepengerBehandlingRestKlient foreldrepengerBehandlingRestKlient = mock(ForeldrepengerBehandlingRestKlient.class);
    private ForeldrepengerEventHåndterer foreldrepengerEventHåndterer = new ForeldrepengerEventHåndterer(oppgaveRepository, foreldrepengerBehandlingRestKlient, oppgaveEgenskapHandler);
    private static Long behandlingId = 1073051L;
    private static UUID uuid = UUID.nameUUIDFromBytes("TEST".getBytes());//UUID.fromString("027961C0-1DA9-1D46-AFAA-0BBAE024758C");
    private static String fagsystem = "FPSAK";


    private OrganisasjonRepository organisasjonRepository = mock(OrganisasjonRepository.class);
    private TpsTjeneste tpsTjeneste = mock(TpsTjeneste.class);
    private AvdelingslederTjeneste avdelingslederTjeneste = mock(AvdelingslederTjeneste.class);
    private AnsattTjeneste ansattTjeneste = mock(AnsattTjeneste.class);
    private OppgaveTjeneste oppgaveTjeneste = new OppgaveTjenesteImpl(oppgaveRepository, organisasjonRepository,
            tpsTjeneste, avdelingslederTjeneste, ansattTjeneste);

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

    private Aksjonspunkt aksjonspunktDtoFra(String aksjonspunkKode, String status, LocalDateTime aksjonspunktFrist){
        return Aksjonspunkt.builder()
                .medDefinisjon(aksjonspunkKode)
                .medStatus(status)
                .medFristTid(aksjonspunktFrist)
                .build();
    }

    private FpsakBehandlingProsessEventDto.Builder prosessBuilderFra(Map<String, String> aksjonspunktmap){
        return FpsakBehandlingProsessEventDto.builder()
                .medFagsystem(Fagsystem.FPSAK)
                .medEksternId(UUID.nameUUIDFromBytes(behandlingId.toString().getBytes()))
                .medEksternId(uuid)
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

    @Ignore //TODO: skriv om funksjonalitet for å finne brukernavn i reservasjon for å kunne teste dette ordentlig
    @Test
    public void reservasjonFjernesVedOpprettelseAvBeslutterOppgave() {
        when(foreldrepengerBehandlingRestKlient.getBehandling(anyLong())).thenReturn(behandlingDtoFra(aksjonspunktKoderTilBeslutterDto));
        foreldrepengerEventHåndterer.prosesser(eventDrammenFra(aksjonspunktKoderTilBeslutter));
        OppgaveEventLogg førsteEvent = repoRule.getRepository().hentAlle(OppgaveEventLogg.class).get(0);

        //Reservasjon førsteReservasjon = repoRule.getRepository().hentAlle(Reservasjon.class).get(0);
        assertThat(førsteEvent.getEventType()).isEqualTo(OppgaveEventType.OPPRETTET);
        assertThat(førsteEvent.getAndreKriterierType()).isEqualTo(AndreKriterierType.TIL_BESLUTTER);


        when(foreldrepengerBehandlingRestKlient.getBehandling(anyLong())).thenReturn(behandlingDtoFra(aksjonspunktKoderUtlandAutomatiskDto));
        foreldrepengerEventHåndterer.prosesser(eventDrammenFra(aksjonspunktKoderUtland));
        List<OppgaveEventLogg> eventer = repoRule.getRepository().hentAlle(OppgaveEventLogg.class);
        OppgaveEventLogg andreEvent = repoRule.getRepository().hentAlle(OppgaveEventLogg.class).stream()
                .max(Comparator.comparing(OppgaveEventLogg::getOpprettetTidspunkt)).get();
        assertThat(andreEvent.getEventType()).isEqualTo(OppgaveEventType.OPPRETTET);
        assertThat(andreEvent.getAndreKriterierType()).isNull();
    }

    static BehandlingFpsak.Builder behandlingBuilderMal() {
        return BehandlingFpsak.builder()
                .medUuid(UUID.nameUUIDFromBytes("TEST".getBytes()))
                .medBehandlendeEnhetNavn("NAV")
                .medAnsvarligSaksbehandler("VLLOS")
                .medStatus("-")
                .medHarGradering(null)
                .medHarRefusjonskravFraArbeidsgiver(null);
    }
}
