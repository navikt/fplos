package no.nav.foreldrepenger.los.hendelse.behandlinghendelse;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.domene.typer.Fagsystem;
import no.nav.foreldrepenger.los.domene.typer.aktør.AktørId;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.Beskyttelsesbehov;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.LokalFagsakEgenskap;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.tilbakekreving.TilbakekrevingOppgaveEgenskapFinner;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveEgenskap;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.reservasjon.Reservasjon;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonKonstanter;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonTjeneste;
import no.nav.vedtak.hendelser.behandling.Aksjonspunktstatus;
import no.nav.vedtak.hendelser.behandling.Behandlingstype;
import no.nav.vedtak.hendelser.behandling.Behandlingsårsak;
import no.nav.vedtak.hendelser.behandling.Ytelse;
import no.nav.vedtak.hendelser.behandling.los.LosBehandlingDto;

import no.nav.vedtak.hendelser.behandling.los.LosFagsakEgenskaperDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.los.domene.typer.Saksnummer;
import no.nav.vedtak.felles.prosesstask.api.CommonTaskProperties;
import no.nav.vedtak.felles.prosesstask.api.ProsessTask;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHandler;
import no.nav.vedtak.hendelser.behandling.Kildesystem;

import static java.util.Arrays.asList;
import static no.nav.foreldrepenger.los.hendelse.behandlinghendelse.Behandling.*;

@Dependent
@ProsessTask(value = "håndter.behandlinghendelse", firstDelay = 10, thenDelay = 10)
public class BehandlingHendelseTask2 implements ProsessTaskHandler {

    private static final Logger LOG = LoggerFactory.getLogger(BehandlingHendelseTask2.class);

    private static final String MANUELT_SATT_PÅ_VENT_KODE = "7001";
    private static final String PÅ_VENT_KODEGRUPPE_STARTS_WITH = "7";
    private static final String TIL_BESLUTTER_KODE = "5016";

    private static final String KONTROLLER_TERMINBEKREFTELSE_KODE = "5001";
    private static final String AUTOMATISK_MARKERING_SOM_UTLAND = "5068";
    private static final String ARBEID_INNTEKT = "5085";
    private static final List<String> REGISTRER_PAPIRSØKNAD_KODE = asList("5012", "5040", "5057", "5096");
    private static final String VURDER_FORMKRAV_KODE = "5082";
    private static final List<String> RELEVANT_NÆRING = List.of("5039", "5049", "5058", "5046", "5051", "5089", "5082", "5035");

    public static final String BEHANDLING_UUID = CommonTaskProperties.BEHANDLING_UUID;
    public static final String KILDE = "kildesystem";

    private final BehandlingKlient fpsakKlient;
    private final BehandlingKlient fptilbakeKlient;

    private final OppgaveRepository oppgaveRepository;
    private final Beskyttelsesbehov beskyttelsesbehov;

    @Inject
    public BehandlingHendelseTask2(FpsakBehandlingKlient fpsakKlient,
                                   FptilbakeBehandlingKlient fptilbakeKlient,
                                   OppgaveRepository oppgaveRepository,
                                   Beskyttelsesbehov beskyttelsesbehov) {
        this.fpsakKlient = fpsakKlient;
        this.fptilbakeKlient = fptilbakeKlient;
        this.oppgaveRepository = oppgaveRepository;
        this.beskyttelsesbehov = beskyttelsesbehov;
    }

    @Override
    public void doTask(ProsessTaskData prosessTaskData) {
        var behandlingUuid = UUID.fromString(prosessTaskData.getPropertyValue(BEHANDLING_UUID));
        var kilde = Kildesystem.valueOf(prosessTaskData.getPropertyValue(KILDE));

        var behandlingDto = hentBehandlingDto(behandlingUuid, kilde, new Saksnummer(prosessTaskData.getSaksnummer()));

        var eksisterendeOppgave = finnEksisterendeOppgave(behandlingDto.behandlingUuid());
        eksisterendeOppgave.ifPresent(
            Oppgave::avsluttOppgave); //NB! avslutte reservasjon??? avkorter ikke reservasjonen nå. Sjekk queries og frontend

        var skalLageOppgave = skalLageOppgave(behandlingDto);
        if (skalLageOppgave) {
            var oppgave = opprettOppgave(behandlingDto);
            opprettReservasjon(oppgave, eksisterendeOppgave);
        }

    }

    private void opprettReservasjon(Oppgave oppgave, Optional<Oppgave> eksisterendeOppgave) {
        var reservasjon = utledReservasjon(oppgave, eksisterendeOppgave);
        reservasjon.ifPresent(this::lagreReservasjon);
    }

    private void lagreReservasjon(Reservasjon reservasjon) {

    }

    private Optional<Reservasjon> utledReservasjon(Oppgave nyOppgave, Optional<Oppgave> eo, Behandling behandling) {

        return eo.map(eksisterendeOppgave -> {
            if (!eksisterendeOppgave.getBehandlendeEnhet().equals(behandling.behandlendeEnhetId())) {
                return Optional.empty();
            }
            if (eksisterendeOppgave.harKriterie(AndreKriterierType.PAPIRSØKNAD)) {
                return nyOppgave.harKriterie(AndreKriterierType.PAPIRSØKNAD) ? Reservasjon.fraEksisterende(eksisterendeOppgave.getReservasjon()) : Optional.empty();
            }
            if (eksisterendeOppgave.harKriterie(AndreKriterierType.TIL_BESLUTTER)) {
                return nyOppgave.harKriterie(AndreKriterierType.RETURNERT_FRA_BESLUTTER) ? ReservasjonTjeneste.opprettReservasjon(nyOppgave,
                    behandling.ansvarligSaksbehandlerIdent(), ReservasjonKonstanter.RETUR_FRA_BESLUTTER) : Optional.empty();
            }


        }).orElseGet(() -> {
            return behandling.ansvarligSaksbehandlerIdent() == null ? Optional.empty() : ReservasjonTjeneste.opprettReservasjon(nyOppgave,
                behandling.ansvarligSaksbehandlerIdent(), null);
        });

        //Eks reservasjon til vanlig oppgave -> arve, logg hvis avvik fra dto?
        //Eks reservasjon til beslutter -> ikke arve, kanskje lete etter forrige beslutter
        //Eks reservasjon papir til vanlig -> ikke arve
        //
        //Beslutter tilbake til saksbehandler -> opprett reservasjon fra behandlingdto
        //Ingen eks reservasjon -> opprett reservasjon fra behandlingdto dersom revurdering manuell behandling
        //Endret enhet -> ikke viderefør
        return null;
    }

    private Oppgave opprettOppgave(Behandling behandling) {
        var kriterier = utledKriterier(behandling);

        var oppgaveEgenskaper = kriterier.stream()
            .map(k -> new OppgaveEgenskap.Builder().medAndreKriterierType(k)
                .medSisteSaksbehandlerForTotrinn(k == AndreKriterierType.TIL_BESLUTTER ? behandling.ansvarligSaksbehandlerIdent() : null)
                .build())
            .collect(Collectors.toSet());

        var oppgave = Oppgave.builder()
            .medSystem(Fagsystem.FPSAK)
            .medSaksnummer(behandling.saksnummer())
            .medAktørId(behandling.aktørId())
            .medBehandlendeEnhet(behandling.behandlendeEnhetId())
            .medBehandlingType(behandling.behandlingstype())
            .medFagsakYtelseType(behandling.ytelse())
            .medAktiv(true)
            .medBehandlingOpprettet(behandling.opprettetTidspunkt())
            .medBehandlingId(new BehandlingId(behandling.behandlingUuid()))
            .medFørsteStønadsdag(behandling.førsteUttaksdatoForeldrepenger())
            .medBehandlingsfrist(behandling.behandlingsfrist() != null ? behandling.behandlingsfrist().atStartOfDay() : null)
            .medKriterier(oppgaveEgenskaper)
            .build();

        oppgaveRepository.opprettOppgave(oppgave);
        return oppgave;
    }

    private Set<AndreKriterierType> utledKriterier(Behandling behandling) {
        var kriterier = new HashSet<AndreKriterierType>();

        var aktiveAksjonspunkt = behandling.aksjonspunkt().stream()
            .filter(a -> a.status() == Aksjonspunktstatus.OPPRETTET)
            .map(Aksjonspunkt::type).collect(Collectors.toSet());
        if (aktiveAksjonspunkt.contains(AksjonspunktType.PAPIRSØKNAD)) {
            kriterier.add(AndreKriterierType.PAPIRSØKNAD);
        }
        if (aktiveAksjonspunkt.contains(AksjonspunktType.TIL_BESLUTTER)) {
            kriterier.add(AndreKriterierType.TIL_BESLUTTER);
        }
        if (aktiveAksjonspunkt.contains(AksjonspunktType.KONTROLLER_TERMINBEKREFTELSE)) {
            kriterier.add(AndreKriterierType.TERMINBEKREFTELSE);
        }
        if (aktiveAksjonspunkt.contains(AksjonspunktType.ARBEID_OG_INNTEKT)) {
            kriterier.add(AndreKriterierType.ARBEID_INNTEKT);
        }
        if (aktiveAksjonspunkt.contains(AksjonspunktType.VURDER_FORMKRAV)) {
            kriterier.add(AndreKriterierType.VURDER_FORMKRAV);
        }

        var saksegenskaper = behandling.saksegenskaper();
        if (aktiveAksjonspunkt.contains(AksjonspunktType.AUTOMATISK_MARKERING_SOM_UTLAND) && (saksegenskaper.contains(Saksegenskap.BOSATT_UTLAND)
            || saksegenskaper.contains(Saksegenskap.EØS_BOSATT_NORGE))) {
            kriterier.add(AndreKriterierType.VURDER_EØS_OPPTJENING);
        }
        if (aktiveAksjonspunkt.contains(AksjonspunktType.VURDER_NÆRING) && saksegenskaper.contains(Saksegenskap.NÆRING)) {
            kriterier.add(AndreKriterierType.NÆRING);
        }
        if (saksegenskaper.contains(Saksegenskap.PRAKSIS_UTSETTELSE)) {
            kriterier.add(AndreKriterierType.PRAKSIS_UTSETTELSE);
        }
        if (saksegenskaper.contains(Saksegenskap.EØS_BOSATT_NORGE)) {
            kriterier.add(AndreKriterierType.EØS_SAK);
        }
        if (saksegenskaper.contains(Saksegenskap.BOSATT_UTLAND)) {
            kriterier.add(AndreKriterierType.UTLANDSSAK);
        }
        if (saksegenskaper.contains(Saksegenskap.SAMMENSATT_KONTROLL)) {
            kriterier.add(AndreKriterierType.SAMMENSATT_KONTROLL);
        }
        if (saksegenskaper.contains(Saksegenskap.DØD)) {
            kriterier.add(AndreKriterierType.DØD);
        }
        if (saksegenskaper.contains(Saksegenskap.BARE_FAR_RETT)) {
            kriterier.add(AndreKriterierType.BARE_FAR_RETT);
        }
        if (saksegenskaper.contains(Saksegenskap.HASTER)) {
            kriterier.add(AndreKriterierType.HASTER);
        }

        var behandlingsegenskaper = behandling.behandlingsegenskaper();
        if (behandlingsegenskaper.contains(Behandlingsegenskap.MOR_UKJENT_UTLAND)) {
            kriterier.add(AndreKriterierType.MOR_UKJENT_UTLAND);
        }
        if (behandlingsegenskaper.contains(Behandlingsegenskap.SYKDOMSVURDERING)) {
            kriterier.add(AndreKriterierType.VURDER_SYKDOM);
        }
        if (behandlingsegenskaper.contains(Behandlingsegenskap.TILBAKEKREVING_OVER_FIRE_RETTSGEBYR)) {
            kriterier.add(AndreKriterierType.OVER_FIRE_RETTSGEBYR);
        }
        if (behandling.behandlingstype().gjelderTilbakebetaling() && !behandlingsegenskaper.isEmpty()
            && !behandlingsegenskaper.contains(Behandlingsegenskap.TILBAKEKREVING_SENDT_VARSEL)) {
            kriterier.add(AndreKriterierType.IKKE_VARSLET);
        }
        if (!behandling.refusjonskrav() || behandlingsegenskaper.contains(Behandlingsegenskap.DIREKTE_UTBETALING)) {
            kriterier.add(AndreKriterierType.UTBETALING_TIL_BRUKER);
        }
        if (behandling.faresignaler() || behandlingsegenskaper.contains(Behandlingsegenskap.FARESIGNALER)) {
            kriterier.add(AndreKriterierType.VURDER_FARESIGNALER);
        }

        var behandlingsårsaker = behandling.behandlingsårsaker();
        if (behandlingsårsaker.contains(Behandling.Behandlingsårsak.PLEIEPENGER)) {
            kriterier.add(AndreKriterierType.PLEIEPENGER);
        }
        if (behandlingsårsaker.contains(Behandling.Behandlingsårsak.UTSATT_START)) {
            kriterier.add(AndreKriterierType.UTSATT_START);
        }
        if (behandlingsårsaker.contains(Behandling.Behandlingsårsak.OPPHØR_NY_SAK)) {
            kriterier.add(AndreKriterierType.NYTT_VEDTAK);
        }
        if (behandlingsårsaker.contains(Behandling.Behandlingsårsak.BERØRT)) {
            kriterier.add(AndreKriterierType.BERØRT_BEHANDLING);
        }
        if (behandlingsårsaker.contains(Behandling.Behandlingsårsak.KLAGE_TILBAKEBETALING)) {
            kriterier.add(AndreKriterierType.KLAGE_PÅ_TILBAKEBETALING);
        }
        if (behandling.ytelse() == FagsakYtelseType.FORELDREPENGER && behandling.behandlingstype() == BehandlingType.REVURDERING
            && behandlingsårsaker.contains(Behandling.Behandlingsårsak.SØKNAD)) {
            kriterier.add(AndreKriterierType.ENDRINGSSØKNAD);
        }
        if (behandling.behandlingstype() == BehandlingType.REVURDERING && behandlingsårsaker.contains(Behandling.Behandlingsårsak.INNTEKTSMELDING)
            && behandlingsårsaker.size() == 1) {
            kriterier.add(AndreKriterierType.REVURDERING_INNTEKTSMELDING);
        }

        if (behandling.aksjonspunkt().stream().anyMatch(a -> a.type() == AksjonspunktType.TIL_BESLUTTER && a.status() == Aksjonspunktstatus.AVBRUTT)) {
            kriterier.add(AndreKriterierType.RETURNERT_FRA_BESLUTTER);
        }

        kriterier.addAll(beskyttelsesbehov.getBeskyttelsesKriterier(behandling.saksnummer()));

        return kriterier;
    }

    private boolean skalLageOppgave(Behandling behandlingDto) {
        return behandlingDto.aksjonspunkt().stream().anyMatch(a -> a.status().equals(Aksjonspunktstatus.OPPRETTET));
    }

    private Optional<Oppgave> finnEksisterendeOppgave(UUID behandlingUuid) {
        return oppgaveRepository.hentAktivOppgave(new BehandlingId(behandlingUuid));
    }

    private Behandling hentBehandlingDto(UUID behandlingUuid, Kildesystem kilde, Saksnummer saksnummer) {
        if (kilde.equals(Kildesystem.FPSAK)) {
            return mapFraFpsak(fpsakKlient.hentLosBehandlingDto(behandlingUuid));
        }
        var losFagsakEgenskaperDto = fpsakKlient.hentLosFagsakEgenskaperDto(saksnummer);
        var losBehandlingDto = fptilbakeKlient.hentLosBehandlingDto(behandlingUuid);

        return mapFraFpTilbake(losBehandlingDto, losFagsakEgenskaperDto);
    }

    private Behandling mapFraFpsak(LosBehandlingDto dto) {
        var aksjonspunkter = mapAksjonspunkt(dto);
        var behandlingsårsaker = mapBehandlingsårsaker(dto);
        var saksegenskaper = mapFagsakEgenskaper(dto.saksegenskaper());
        var behandlingsegenskaper = dto.behandlingsegenskaper().stream().map(Behandlingsegenskap::valueOf).toList();
        return new Behandling(dto.behandlingUuid(), new Saksnummer(dto.saksnummer()), map(dto.ytelse()), new AktørId(dto.aktørId().getAktørId()),
            BehandlingType.valueOf(dto.behandlingstype().name()), Behandlingsstatus.valueOf(dto.behandlingsstatus().name()),
            dto.opprettetTidspunkt(), dto.behandlendeEnhetId(), dto.behandlingsfrist(), dto.ansvarligSaksbehandlerIdent(), aksjonspunkter,
            behandlingsårsaker, dto.faresignaler(), dto.refusjonskrav(), saksegenskaper,
            dto.foreldrepengerDto() == null ? null : dto.foreldrepengerDto().førsteUttakDato(), behandlingsegenskaper, null);
    }

    private List<Behandling.Behandlingsårsak> mapBehandlingsårsaker(LosBehandlingDto dto) {
        return dto.behandlingsårsaker().stream().map(this::map).toList();
    }

    private List<Aksjonspunkt> mapAksjonspunkt(LosBehandlingDto dto) {
        return dto.aksjonspunkt().stream().map(ap -> new Aksjonspunkt(mapFraFpsak(ap.definisjon()), ap.status(), ap.fristTid())).toList();
    }

    private static FagsakYtelseType map(Ytelse ytelse) {
        return switch (ytelse) {
            case ENGANGSTØNAD -> FagsakYtelseType.ENGANGSTØNAD;
            case FORELDREPENGER -> FagsakYtelseType.FORELDREPENGER;
            case SVANGERSKAPSPENGER -> FagsakYtelseType.SVANGERSKAPSPENGER;
        };
    }

    private Behandling.Behandlingsårsak map(Behandlingsårsak å) {
        return switch (å) {
            case SØKNAD -> Behandling.Behandlingsårsak.SØKNAD;
            case INNTEKTSMELDING -> Behandling.Behandlingsårsak.INNTEKTSMELDING;
            case FOLKEREGISTER -> Behandling.Behandlingsårsak.FOLKEREGISTER;
            case PLEIEPENGER -> Behandling.Behandlingsårsak.PLEIEPENGER;
            case ETTERKONTROLL -> Behandling.Behandlingsårsak.ETTERKONTROLL;
            case MANUELL -> Behandling.Behandlingsårsak.MANUELL;
            case BERØRT -> Behandling.Behandlingsårsak.BERØRT;
            case UTSATT_START -> Behandling.Behandlingsårsak.UTSATT_START;
            case OPPHØR_NY_SAK -> Behandling.Behandlingsårsak.OPPHØR_NY_SAK;
            case REGULERING -> Behandling.Behandlingsårsak.REGULERING;
            case KLAGE_OMGJØRING -> Behandling.Behandlingsårsak.KLAGE_OMGJØRING;
            case KLAGE_TILBAKEBETALING -> Behandling.Behandlingsårsak.KLAGE_TILBAKEBETALING;
            case ANNET -> Behandling.Behandlingsårsak.ANNET;
        };
    }

    private AksjonspunktType mapFraFpsak(String aksjonspunktKode) {
        return switch (aksjonspunktKode) {
            case MANUELT_SATT_PÅ_VENT_KODE -> AksjonspunktType.MANUELT_SATT_PÅ_VENT;
            case AUTOMATISK_MARKERING_SOM_UTLAND -> AksjonspunktType.AUTOMATISK_MARKERING_SOM_UTLAND;
            case ARBEID_INNTEKT -> AksjonspunktType.ARBEID_OG_INNTEKT;
            case VURDER_FORMKRAV_KODE -> AksjonspunktType.VURDER_FORMKRAV;
            case TIL_BESLUTTER_KODE -> AksjonspunktType.TIL_BESLUTTER;
            case KONTROLLER_TERMINBEKREFTELSE_KODE -> AksjonspunktType.KONTROLLER_TERMINBEKREFTELSE;
            case String kode when RELEVANT_NÆRING.contains(kode) -> AksjonspunktType.VURDER_NÆRING;
            case String kode when kode.startsWith(PÅ_VENT_KODEGRUPPE_STARTS_WITH) -> AksjonspunktType.PÅ_VENT;
            case String kode when REGISTRER_PAPIRSØKNAD_KODE.contains(kode) -> AksjonspunktType.PAPIRSØKNAD;
            default -> AksjonspunktType.ANNET;
        };
    }

    private Behandling mapFraFpTilbake(LosBehandlingDto behandlingDto, LosFagsakEgenskaperDto losFagsakEgenskaperDto) {
        var aksjonspunkter = mapAksjonspunkt(behandlingDto);
        var behandlingsårsaker = mapBehandlingsårsaker(behandlingDto);
        var tilbakekreving = behandlingDto.tilbakeDto() != null ? new Tilbakekreving(behandlingDto.tilbakeDto().feilutbetaltBeløp(),
            behandlingDto.tilbakeDto().førsteFeilutbetalingDato()) : null;

        var behandlingsegenskaper = behandlingDto.behandlingsegenskaper().stream().map(egenskap -> switch (egenskap.toUpperCase()) {
            case "VARSLET" -> Behandlingsegenskap.TILBAKEKREVING_SENDT_VARSEL;
            case "OVER_FIRE_RETTSGEBYR" -> Behandlingsegenskap.TILBAKEKREVING_OVER_FIRE_RETTSGEBYR;
            default -> throw new IllegalStateException("Unexpected value: " + egenskap);
        }).toList();
        return new Behandling(behandlingDto.behandlingUuid(), new Saksnummer(behandlingDto.saksnummer()), map(behandlingDto.ytelse()),
            new AktørId(behandlingDto.aktørId().getAktørId()), BehandlingType.valueOf(behandlingDto.behandlingstype().name()),
            Behandlingsstatus.valueOf(behandlingDto.behandlingsstatus().name()), behandlingDto.opprettetTidspunkt(),
            behandlingDto.behandlendeEnhetId(), behandlingDto.behandlingsfrist(), behandlingDto.ansvarligSaksbehandlerIdent(), aksjonspunkter,
            behandlingsårsaker, behandlingDto.faresignaler(), behandlingDto.refusjonskrav(),
            mapFagsakEgenskaper(losFagsakEgenskaperDto.saksegenskaper()),
            behandlingDto.foreldrepengerDto() == null ? null : behandlingDto.foreldrepengerDto().førsteUttakDato(), behandlingsegenskaper,
            tilbakekreving);
    }

    private static List<Saksegenskap> mapFagsakEgenskaper(List<String> saksegenskaper) {
        return saksegenskaper.stream().map(Saksegenskap::valueOf).toList(); //TODO logge hvis ny verdi?
    }

}
