package no.nav.foreldrepenger.los.hendelse.behandlinghendelse;

import static no.nav.foreldrepenger.los.hendelse.behandlinghendelse.OppgaveGrunnlag.Aksjonspunkt;
import static no.nav.foreldrepenger.los.hendelse.behandlinghendelse.OppgaveGrunnlag.AksjonspunktType;
import static no.nav.foreldrepenger.los.hendelse.behandlinghendelse.OppgaveGrunnlag.Behandlingsegenskap;
import static no.nav.foreldrepenger.los.hendelse.behandlinghendelse.OppgaveGrunnlag.Saksegenskap;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.domene.typer.Fagsystem;
import no.nav.foreldrepenger.los.domene.typer.Saksnummer;
import no.nav.foreldrepenger.los.domene.typer.aktør.AktørId;
import no.nav.foreldrepenger.los.hendelse.behandlinghendelse.OppgaveGrunnlag.BehandlingStatus;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.BehandlingTjeneste;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveEgenskap;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.reservasjon.Reservasjon;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonRepository;
import no.nav.vedtak.felles.prosesstask.api.CommonTaskProperties;
import no.nav.vedtak.felles.prosesstask.api.ProsessTask;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHandler;
import no.nav.vedtak.hendelser.behandling.Aksjonspunktstatus;
import no.nav.vedtak.hendelser.behandling.Aksjonspunkttype;
import no.nav.vedtak.hendelser.behandling.Behandlingsårsak;
import no.nav.vedtak.hendelser.behandling.Kildesystem;
import no.nav.vedtak.hendelser.behandling.Ytelse;
import no.nav.vedtak.hendelser.behandling.los.LosBehandlingDto;
import no.nav.vedtak.hendelser.behandling.los.LosFagsakEgenskaperDto;

@Dependent
@ProsessTask(value = "håndter.behandlinghendelse2", firstDelay = 10, thenDelay = 10)
class BehandlingHendelseTask2 implements ProsessTaskHandler {

    private static final String KONTROLLER_TERMINBEKREFTELSE_KODE = "5001";
    private static final String AUTOMATISK_MARKERING_SOM_UTLAND = "5068";
    private static final String ARBEID_INNTEKT = "5085";
    private static final String VURDER_FORMKRAV_KODE = "5082";
    private static final List<String> RELEVANT_NÆRING = List.of("5039", "5049", "5058", "5046", "5051", "5089", "5082", "5035");

    static final String BEHANDLING_UUID = CommonTaskProperties.BEHANDLING_UUID;
    static final String KILDE = "kildesystem";

    private final BehandlingKlient fpsakKlient;
    private final BehandlingKlient fptilbakeKlient;

    private final BehandlingTjeneste behandlingTjeneste;

    private final OppgaveRepository oppgaveRepository;
    private final KriterieUtleder kriterieUtleder;
    private final ReservasjonRepository reservasjonRepository;
    private final ReservasjonUtleder reservasjonUtleder;

    @Inject
    BehandlingHendelseTask2(FpsakBehandlingKlient fpsakKlient,
                                   FptilbakeBehandlingKlient fptilbakeKlient,
                                   BehandlingTjeneste behandlingTjeneste,
                                   OppgaveRepository oppgaveRepository,
                                   ReservasjonRepository reservasjonRepository,
                                   KriterieUtleder kriterieUtleder,
                                   ReservasjonUtleder reservasjonUtleder) {
        this.fpsakKlient = fpsakKlient;
        this.fptilbakeKlient = fptilbakeKlient;
        this.behandlingTjeneste = behandlingTjeneste;
        this.oppgaveRepository = oppgaveRepository;
        this.kriterieUtleder = kriterieUtleder;
        this.reservasjonRepository = reservasjonRepository;
        this.reservasjonUtleder = reservasjonUtleder;
    }

    @Override
    public void doTask(ProsessTaskData prosessTaskData) {
        var behandlingUuid = UUID.fromString(prosessTaskData.getPropertyValue(BEHANDLING_UUID));
        var kilde = Kildesystem.valueOf(prosessTaskData.getPropertyValue(KILDE));

        var dto = hentDto(behandlingUuid, kilde);
        var oppgaveGrunnlag = mapTilOppgaveGrunnlag(behandlingUuid, new Saksnummer(prosessTaskData.getSaksnummer()), dto);

        var eksisterendeOppgave = finnEksisterendeOppgave(oppgaveGrunnlag.behandlingUuid());

        var skalLageOppgave = skalLageOppgave(oppgaveGrunnlag);
        if (skalLageOppgave) {
            var oppgave = opprettOppgave(oppgaveGrunnlag);
            opprettReservasjon(oppgave, eksisterendeOppgave, oppgaveGrunnlag);
        }

        eksisterendeOppgave.ifPresent(o -> {
            o.avsluttOppgave();
            if (o.harAktivReservasjon()) {
                avsluttReservasjon(o.getReservasjon());
            }
        });

        behandlingTjeneste.safeLagreBehandling(dto, switch (kilde) {
            case FPSAK -> Fagsystem.FPSAK;
            case FPTILBAKE -> Fagsystem.FPTILBAKE;
        });
    }

    private void avsluttReservasjon(Reservasjon reservasjon) {
        reservasjon.setReservertTil(LocalDateTime.now().minusSeconds(1));
        reservasjonRepository.lagre(reservasjon);
    }

    private void opprettReservasjon(Oppgave oppgave, Optional<Oppgave> eksisterendeOppgave, OppgaveGrunnlag oppgaveGrunnlag) {
        var reservasjon = reservasjonUtleder.utledReservasjon(oppgave, eksisterendeOppgave, oppgaveGrunnlag);
        reservasjon.ifPresent(reservasjonRepository::lagre);
    }

    private Oppgave opprettOppgave(OppgaveGrunnlag oppgaveGrunnlag) {
        var kriterier = kriterieUtleder.utledKriterier(oppgaveGrunnlag);

        var oppgaveEgenskaper = kriterier.stream()
            .map(k -> new OppgaveEgenskap.Builder().medAndreKriterierType(k)
                .medSisteSaksbehandlerForTotrinn(k == AndreKriterierType.TIL_BESLUTTER ? oppgaveGrunnlag.ansvarligSaksbehandlerIdent() : null)
                .build())
            .collect(Collectors.toSet());

        var oppgave = Oppgave.builder()
            .medSystem(Fagsystem.FPSAK)
            .medSaksnummer(oppgaveGrunnlag.saksnummer())
            .medAktørId(oppgaveGrunnlag.aktørId())
            .medBehandlendeEnhet(oppgaveGrunnlag.behandlendeEnhetId())
            .medBehandlingType(oppgaveGrunnlag.behandlingstype())
            .medFagsakYtelseType(oppgaveGrunnlag.ytelse())
            .medAktiv(true)
            .medBehandlingOpprettet(oppgaveGrunnlag.opprettetTidspunkt())
            .medBehandlingId(new BehandlingId(oppgaveGrunnlag.behandlingUuid()))
            .medFørsteStønadsdag(oppgaveGrunnlag.førsteUttaksdatoForeldrepenger())
            .medBehandlingsfrist(oppgaveGrunnlag.behandlingsfrist() != null ? oppgaveGrunnlag.behandlingsfrist().atStartOfDay() : null)
            .medKriterier(oppgaveEgenskaper)
            .build();

        oppgaveRepository.opprettOppgave(oppgave);
        return oppgave;
    }

    private boolean skalLageOppgave(OppgaveGrunnlag oppgaveGrunnlag) {
        var erPåVent = oppgaveGrunnlag.aksjonspunkt().stream()
            .filter(aksjonspunkt -> aksjonspunkt.status() == Aksjonspunktstatus.OPPRETTET)
            .anyMatch(a -> a.type() == AksjonspunktType.PÅ_VENT);
        var underBehandlingStatus = Set.of(BehandlingStatus.OPPRETTET, BehandlingStatus.UTREDES,
            BehandlingStatus.FATTER_VEDTAK).contains(oppgaveGrunnlag.behandlingStatus());
        var harOpprettetAksjonspunkt = oppgaveGrunnlag.aksjonspunkt().stream().anyMatch(a -> a.status().equals(Aksjonspunktstatus.OPPRETTET));
        return !erPåVent && underBehandlingStatus && harOpprettetAksjonspunkt;
    }

    private Optional<Oppgave> finnEksisterendeOppgave(UUID behandlingUuid) {
        return oppgaveRepository.hentAktivOppgave(new BehandlingId(behandlingUuid));
    }

    private OppgaveGrunnlag mapTilOppgaveGrunnlag(UUID behandlingUuid, Saksnummer saksnummer, LosBehandlingDto dto) {
        if (dto.kildesystem().equals(Kildesystem.FPSAK)) {
            return mapFraFpsak(fpsakKlient.hentLosBehandlingDto(behandlingUuid));
        }
        var losFagsakEgenskaperDto = fpsakKlient.hentLosFagsakEgenskaperDto(saksnummer);
        var losBehandlingDto = fptilbakeKlient.hentLosBehandlingDto(behandlingUuid);

        return mapFraFpTilbake(losBehandlingDto, losFagsakEgenskaperDto);
    }

    private LosBehandlingDto hentDto(UUID behandlingUuid, Kildesystem kilde) {
        return kilde.equals(Kildesystem.FPSAK) ? fpsakKlient.hentLosBehandlingDto(behandlingUuid) : fptilbakeKlient.hentLosBehandlingDto(
            behandlingUuid);
    }

    private OppgaveGrunnlag mapFraFpsak(LosBehandlingDto dto) {
        var aksjonspunkter = mapAksjonspunkt(dto);
        var behandlingsårsaker = mapBehandlingsårsaker(dto);
        var saksegenskaper = mapFagsakEgenskaper(dto.saksegenskaper());
        var behandlingsegenskaper = dto.behandlingsegenskaper().stream().map(Behandlingsegenskap::valueOf).toList();
        return new OppgaveGrunnlag(dto.behandlingUuid(), new Saksnummer(dto.saksnummer()), map(dto.ytelse()), new AktørId(dto.aktørId().getAktørId()),
            mapBehandlingType(dto), dto.opprettetTidspunkt(), dto.behandlendeEnhetId(), dto.behandlingsfrist(), dto.ansvarligSaksbehandlerIdent(),
            aksjonspunkter, behandlingsårsaker, dto.faresignaler(), dto.refusjonskrav(), saksegenskaper,
            dto.foreldrepengerDto() == null ? null : dto.foreldrepengerDto().førsteUttakDato(), behandlingsegenskaper, mapStatus(dto));
    }

    private static BehandlingType mapBehandlingType(LosBehandlingDto dto) {
        return switch (dto.behandlingstype()) {
            case FØRSTEGANGS -> BehandlingType.FØRSTEGANGSSØKNAD;
            case REVURDERING -> BehandlingType.REVURDERING;
            case TILBAKEBETALING -> BehandlingType.TILBAKEBETALING;
            case TILBAKEBETALING_REVURDERING -> BehandlingType.TILBAKEBETALING_REVURDERING;
            case KLAGE -> BehandlingType.KLAGE;
            case ANKE -> BehandlingType.ANKE;
            case INNSYN -> BehandlingType.INNSYN;
        };
    }

    private List<OppgaveGrunnlag.Behandlingsårsak> mapBehandlingsårsaker(LosBehandlingDto dto) {
        return dto.behandlingsårsaker().stream().map(this::map).toList();
    }

    private List<Aksjonspunkt> mapAksjonspunkt(LosBehandlingDto dto) {
        return dto.aksjonspunkt().stream().map(ap -> new Aksjonspunkt(mapFraFpsak(ap), ap.status(), ap.fristTid())).toList();
    }

    private static FagsakYtelseType map(Ytelse ytelse) {
        return switch (ytelse) {
            case ENGANGSTØNAD -> FagsakYtelseType.ENGANGSTØNAD;
            case FORELDREPENGER -> FagsakYtelseType.FORELDREPENGER;
            case SVANGERSKAPSPENGER -> FagsakYtelseType.SVANGERSKAPSPENGER;
        };
    }

    private OppgaveGrunnlag.Behandlingsårsak map(Behandlingsårsak å) {
        return switch (å) {
            case SØKNAD -> OppgaveGrunnlag.Behandlingsårsak.SØKNAD;
            case INNTEKTSMELDING -> OppgaveGrunnlag.Behandlingsårsak.INNTEKTSMELDING;
            case FOLKEREGISTER -> OppgaveGrunnlag.Behandlingsårsak.FOLKEREGISTER;
            case PLEIEPENGER -> OppgaveGrunnlag.Behandlingsårsak.PLEIEPENGER;
            case ETTERKONTROLL -> OppgaveGrunnlag.Behandlingsårsak.ETTERKONTROLL;
            case MANUELL -> OppgaveGrunnlag.Behandlingsårsak.MANUELL;
            case BERØRT -> OppgaveGrunnlag.Behandlingsårsak.BERØRT;
            case UTSATT_START -> OppgaveGrunnlag.Behandlingsårsak.UTSATT_START;
            case OPPHØR_NY_SAK -> OppgaveGrunnlag.Behandlingsårsak.OPPHØR_NY_SAK;
            case REGULERING -> OppgaveGrunnlag.Behandlingsårsak.REGULERING;
            case KLAGE_OMGJØRING -> OppgaveGrunnlag.Behandlingsårsak.KLAGE_OMGJØRING;
            case KLAGE_TILBAKEBETALING -> OppgaveGrunnlag.Behandlingsårsak.KLAGE_TILBAKEBETALING;
            case ANNET -> OppgaveGrunnlag.Behandlingsårsak.ANNET;
        };
    }

    private AksjonspunktType mapFraFpsak(LosBehandlingDto.LosAksjonspunktDto aksjonspunktDto) {
        if (aksjonspunktDto.type() == Aksjonspunkttype.VENT) {
            return AksjonspunktType.PÅ_VENT;
        }
        if (aksjonspunktDto.type() == Aksjonspunkttype.BESLUTTER) {
            return AksjonspunktType.TIL_BESLUTTER;
        }
        if (aksjonspunktDto.type() == Aksjonspunkttype.PAPIRSØKNAD) {
            return AksjonspunktType.PAPIRSØKNAD;
        }
        return switch (aksjonspunktDto.definisjon()) {
            case AUTOMATISK_MARKERING_SOM_UTLAND -> AksjonspunktType.AUTOMATISK_MARKERING_SOM_UTLAND;
            case ARBEID_INNTEKT -> AksjonspunktType.ARBEID_OG_INNTEKT;
            case VURDER_FORMKRAV_KODE -> AksjonspunktType.VURDER_FORMKRAV;
            case KONTROLLER_TERMINBEKREFTELSE_KODE -> AksjonspunktType.KONTROLLER_TERMINBEKREFTELSE;
            case String kode when RELEVANT_NÆRING.contains(kode) -> AksjonspunktType.VURDER_NÆRING;
            default -> AksjonspunktType.ANNET;
        };
    }

    private OppgaveGrunnlag mapFraFpTilbake(LosBehandlingDto behandlingDto, LosFagsakEgenskaperDto losFagsakEgenskaperDto) {
        var aksjonspunkter = mapAksjonspunkt(behandlingDto);
        var behandlingsårsaker = mapBehandlingsårsaker(behandlingDto);

        var behandlingsegenskaper = behandlingDto.behandlingsegenskaper().stream().map(egenskap -> switch (egenskap.toUpperCase()) {
            case "VARSLET" -> Behandlingsegenskap.TILBAKEKREVING_SENDT_VARSEL;
            case "OVER_FIRE_RETTSGEBYR" -> Behandlingsegenskap.TILBAKEKREVING_OVER_FIRE_RETTSGEBYR;
            default -> throw new IllegalStateException("Unexpected value: " + egenskap);
        }).toList();
        return new OppgaveGrunnlag(behandlingDto.behandlingUuid(), new Saksnummer(behandlingDto.saksnummer()), map(behandlingDto.ytelse()),
            new AktørId(behandlingDto.aktørId().getAktørId()), mapBehandlingType(behandlingDto), behandlingDto.opprettetTidspunkt(),
            behandlingDto.behandlendeEnhetId(), behandlingDto.behandlingsfrist(), behandlingDto.ansvarligSaksbehandlerIdent(), aksjonspunkter,
            behandlingsårsaker, behandlingDto.faresignaler(), behandlingDto.refusjonskrav(),
            mapFagsakEgenskaper(losFagsakEgenskaperDto.saksegenskaper()),
            behandlingDto.foreldrepengerDto() == null ? null : behandlingDto.foreldrepengerDto().førsteUttakDato(), behandlingsegenskaper,
            mapStatus(behandlingDto));
    }

    private BehandlingStatus mapStatus(LosBehandlingDto behandlingDto) {
        return switch (behandlingDto.behandlingsstatus()) {
            case OPPRETTET -> BehandlingStatus.OPPRETTET;
            case UTREDES -> BehandlingStatus.UTREDES;
            case FATTER_VEDTAK -> BehandlingStatus.FATTER_VEDTAK;
            case IVERKSETTER_VEDTAK -> BehandlingStatus.IVERKSETTER_VEDTAK;
            case AVSLUTTET -> BehandlingStatus.AVSLUTTET;
        };
    }

    private static List<Saksegenskap> mapFagsakEgenskaper(List<String> saksegenskaper) {
        return saksegenskaper.stream().map(Saksegenskap::valueOf).toList();
    }
}
