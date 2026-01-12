package no.nav.foreldrepenger.los.hendelse.behandlinghendelse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
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
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.Beskyttelsesbehov;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.BehandlingTjeneste;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveEgenskap;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.reservasjon.Reservasjon;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonKonstanter;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonRepository;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonTjeneste;
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
public class BehandlingHendelseTask2 implements ProsessTaskHandler {

    private static final String KONTROLLER_TERMINBEKREFTELSE_KODE = "5001";
    private static final String AUTOMATISK_MARKERING_SOM_UTLAND = "5068";
    private static final String ARBEID_INNTEKT = "5085";
    private static final String VURDER_FORMKRAV_KODE = "5082";
    private static final List<String> RELEVANT_NÆRING = List.of("5039", "5049", "5058", "5046", "5051", "5089", "5082", "5035");

    public static final String BEHANDLING_UUID = CommonTaskProperties.BEHANDLING_UUID;
    public static final String KILDE = "kildesystem";

    private final BehandlingKlient fpsakKlient;
    private final BehandlingKlient fptilbakeKlient;

    private final BehandlingTjeneste behandlingTjeneste;

    private final OppgaveRepository oppgaveRepository;
    private final Beskyttelsesbehov beskyttelsesbehov;
    private final ReservasjonRepository reservasjonRepository;

    @Inject
    public BehandlingHendelseTask2(FpsakBehandlingKlient fpsakKlient,
                                   FptilbakeBehandlingKlient fptilbakeKlient,
                                   BehandlingTjeneste behandlingTjeneste,
                                   OppgaveRepository oppgaveRepository,
                                   Beskyttelsesbehov beskyttelsesbehov,
                                   ReservasjonRepository reservasjonRepository) {
        this.fpsakKlient = fpsakKlient;
        this.fptilbakeKlient = fptilbakeKlient;
        this.behandlingTjeneste = behandlingTjeneste;
        this.oppgaveRepository = oppgaveRepository;
        this.beskyttelsesbehov = beskyttelsesbehov;
        this.reservasjonRepository = reservasjonRepository;
    }

    @Override
    public void doTask(ProsessTaskData prosessTaskData) {
        var behandlingUuid = UUID.fromString(prosessTaskData.getPropertyValue(BEHANDLING_UUID));
        var kilde = Kildesystem.valueOf(prosessTaskData.getPropertyValue(KILDE));

        var dto = hentDto(behandlingUuid, kilde);
        var oppgaveGrunnlag = mapTilOppgaveGrunnlag(behandlingUuid, new Saksnummer(prosessTaskData.getSaksnummer()), dto);

        //TODO Se på om lagret behandling aksjonspunkt er lik ny tilstand, hvis dette er tilfellet så behold oppgave?
        var eksisterendeOppgave = finnEksisterendeOppgave(oppgaveGrunnlag.behandlingUuid());
        eksisterendeOppgave.ifPresent(
            Oppgave::avsluttOppgave); //NB! avslutte reservasjon??? avkorter ikke reservasjonen nå. Sjekk queries og frontend

        var skalLageOppgave = skalLageOppgave(oppgaveGrunnlag);
        if (skalLageOppgave) {
            var oppgave = opprettOppgave(oppgaveGrunnlag);
            opprettReservasjon(oppgave, eksisterendeOppgave, oppgaveGrunnlag);
        }

        behandlingTjeneste.safeLagreBehandling(dto, Fagsystem.FPSAK);
    }

    private void opprettReservasjon(Oppgave oppgave, Optional<Oppgave> eksisterendeOppgave, OppgaveGrunnlag oppgaveGrunnlag) {
        var reservasjon = utledReservasjon(oppgave, eksisterendeOppgave, oppgaveGrunnlag);
        reservasjon.ifPresent(reservasjonRepository::lagre);
    }

    private Optional<Reservasjon> utledReservasjon(Oppgave nyOppgave, Optional<Oppgave> eo, OppgaveGrunnlag oppgaveGrunnlag) {
        if (eo.isPresent()) {
            var eksisterendeOppgave = eo.get();
            if (harEndretEnhet(oppgaveGrunnlag, eksisterendeOppgave)) {
                return Optional.empty();
            }
            if (erReturFraBeslutter(nyOppgave, eksisterendeOppgave)) {
                return Optional.of(ReservasjonTjeneste.opprettReservasjon(nyOppgave, oppgaveGrunnlag.ansvarligSaksbehandlerIdent(),
                    ReservasjonKonstanter.RETUR_FRA_BESLUTTER));
            }
            if (eksisterendeOppgave.harAktivReservasjon()) {
                if (eksisterendeOppgave.harKriterie(AndreKriterierType.PAPIRSØKNAD) && !nyOppgave.harKriterie(AndreKriterierType.PAPIRSØKNAD)) {
                    return Optional.empty();
                }
                return Optional.of(nyReservasjon(nyOppgave, eksisterendeOppgave.getReservasjon()));
            }
            return Optional.empty();
        }
        var lagretBehandling = oppgaveRepository.finnBehandling(oppgaveGrunnlag.behandlingUuid());
        if (oppgaveGrunnlag.ansvarligSaksbehandlerIdent() != null && (erNyManuellRevurdering(oppgaveGrunnlag, lagretBehandling) || erPåVent(
            lagretBehandling))) {
            return Optional.of(ReservasjonTjeneste.opprettReservasjon(nyOppgave, oppgaveGrunnlag.ansvarligSaksbehandlerIdent(), null));
        }
        return Optional.empty();
    }

    private boolean erPåVent(Optional<no.nav.foreldrepenger.los.oppgave.Behandling> lagretBehandling) {
        return lagretBehandling.stream().anyMatch(this::erPåVent);
    }

    private static boolean erNyManuellRevurdering(OppgaveGrunnlag oppgaveGrunnlag,
                                                  Optional<no.nav.foreldrepenger.los.oppgave.Behandling> lagretBehandling) {
        return lagretBehandling.isEmpty() && erManuellRevurdering(oppgaveGrunnlag);
    }

    private boolean erPåVent(no.nav.foreldrepenger.los.oppgave.Behandling behandling) {
        //TODO blir feil når det kommer en ny venttilstand i fpsak
        return behandling.getBehandlingTilstand().erPåVent();
    }

    private static boolean erManuellRevurdering(OppgaveGrunnlag oppgaveGrunnlag) {
        return oppgaveGrunnlag.behandlingstype() == BehandlingType.REVURDERING && oppgaveGrunnlag.behandlingsårsaker()
            .contains(OppgaveGrunnlag.Behandlingsårsak.MANUELL);
    }

    private static boolean erReturFraBeslutter(Oppgave nyOppgave, Oppgave eksisterendeOppgave) {
        return eksisterendeOppgave.harKriterie(AndreKriterierType.TIL_BESLUTTER) && nyOppgave.harKriterie(AndreKriterierType.RETURNERT_FRA_BESLUTTER);
    }

    private static boolean harEndretEnhet(OppgaveGrunnlag oppgaveGrunnlag, Oppgave eksisterendeOppgave) {
        return !eksisterendeOppgave.getBehandlendeEnhet().equals(oppgaveGrunnlag.behandlendeEnhetId());
    }

    private static Reservasjon nyReservasjon(Oppgave nyOppgave, Reservasjon eksisterendeReservasjon) {
        var reservasjon = new Reservasjon(nyOppgave);
        reservasjon.setReservertTil(eksisterendeReservasjon.getReservertTil());
        reservasjon.setReservertAv(eksisterendeReservasjon.getReservertAv());
        reservasjon.setFlyttetAv(eksisterendeReservasjon.getFlyttetAv());
        reservasjon.setBegrunnelse(eksisterendeReservasjon.getBegrunnelse());
        reservasjon.setFlyttetTidspunkt(eksisterendeReservasjon.getFlyttetTidspunkt());
        return reservasjon;
    }

    private Oppgave opprettOppgave(OppgaveGrunnlag oppgaveGrunnlag) {
        var kriterier = utledKriterier(oppgaveGrunnlag);

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

    private Set<AndreKriterierType> utledKriterier(OppgaveGrunnlag oppgaveGrunnlag) {
        var kriterier = new HashSet<AndreKriterierType>();

        var aktiveAksjonspunkt = oppgaveGrunnlag.aksjonspunkt()
            .stream()
            .filter(a -> a.status() == Aksjonspunktstatus.OPPRETTET)
            .map(OppgaveGrunnlag.Aksjonspunkt::type)
            .collect(Collectors.toSet());
        if (aktiveAksjonspunkt.contains(OppgaveGrunnlag.AksjonspunktType.PAPIRSØKNAD)) {
            kriterier.add(AndreKriterierType.PAPIRSØKNAD);
        }
        if (aktiveAksjonspunkt.contains(OppgaveGrunnlag.AksjonspunktType.TIL_BESLUTTER)) {
            kriterier.add(AndreKriterierType.TIL_BESLUTTER);
        }
        if (aktiveAksjonspunkt.contains(OppgaveGrunnlag.AksjonspunktType.KONTROLLER_TERMINBEKREFTELSE)) {
            kriterier.add(AndreKriterierType.TERMINBEKREFTELSE);
        }
        if (aktiveAksjonspunkt.contains(OppgaveGrunnlag.AksjonspunktType.ARBEID_OG_INNTEKT)) {
            kriterier.add(AndreKriterierType.ARBEID_INNTEKT);
        }
        if (aktiveAksjonspunkt.contains(OppgaveGrunnlag.AksjonspunktType.VURDER_FORMKRAV)) {
            kriterier.add(AndreKriterierType.VURDER_FORMKRAV);
        }

        var saksegenskaper = oppgaveGrunnlag.saksegenskaper();
        if (aktiveAksjonspunkt.contains(OppgaveGrunnlag.AksjonspunktType.AUTOMATISK_MARKERING_SOM_UTLAND) && (
            saksegenskaper.contains(OppgaveGrunnlag.Saksegenskap.BOSATT_UTLAND) || saksegenskaper.contains(
                OppgaveGrunnlag.Saksegenskap.EØS_BOSATT_NORGE))) {
            kriterier.add(AndreKriterierType.VURDER_EØS_OPPTJENING);
        }
        if (aktiveAksjonspunkt.contains(OppgaveGrunnlag.AksjonspunktType.VURDER_NÆRING) && saksegenskaper.contains(
            OppgaveGrunnlag.Saksegenskap.NÆRING)) {
            kriterier.add(AndreKriterierType.NÆRING);
        }
        if (saksegenskaper.contains(OppgaveGrunnlag.Saksegenskap.PRAKSIS_UTSETTELSE)) {
            kriterier.add(AndreKriterierType.PRAKSIS_UTSETTELSE);
        }
        if (saksegenskaper.contains(OppgaveGrunnlag.Saksegenskap.EØS_BOSATT_NORGE)) {
            kriterier.add(AndreKriterierType.EØS_SAK);
        }
        if (saksegenskaper.contains(OppgaveGrunnlag.Saksegenskap.BOSATT_UTLAND)) {
            kriterier.add(AndreKriterierType.UTLANDSSAK);
        }
        if (saksegenskaper.contains(OppgaveGrunnlag.Saksegenskap.SAMMENSATT_KONTROLL)) {
            kriterier.add(AndreKriterierType.SAMMENSATT_KONTROLL);
        }
        if (saksegenskaper.contains(OppgaveGrunnlag.Saksegenskap.DØD)) {
            kriterier.add(AndreKriterierType.DØD);
        }
        if (saksegenskaper.contains(OppgaveGrunnlag.Saksegenskap.BARE_FAR_RETT)) {
            kriterier.add(AndreKriterierType.BARE_FAR_RETT);
        }
        if (saksegenskaper.contains(OppgaveGrunnlag.Saksegenskap.HASTER)) {
            kriterier.add(AndreKriterierType.HASTER);
        }

        var behandlingsegenskaper = oppgaveGrunnlag.behandlingsegenskaper();
        if (behandlingsegenskaper.contains(OppgaveGrunnlag.Behandlingsegenskap.MOR_UKJENT_UTLAND)) {
            kriterier.add(AndreKriterierType.MOR_UKJENT_UTLAND);
        }
        if (behandlingsegenskaper.contains(OppgaveGrunnlag.Behandlingsegenskap.SYKDOMSVURDERING)) {
            kriterier.add(AndreKriterierType.VURDER_SYKDOM);
        }
        if (behandlingsegenskaper.contains(OppgaveGrunnlag.Behandlingsegenskap.TILBAKEKREVING_OVER_FIRE_RETTSGEBYR)) {
            kriterier.add(AndreKriterierType.OVER_FIRE_RETTSGEBYR);
        }
        if (oppgaveGrunnlag.behandlingstype().gjelderTilbakebetaling() && !behandlingsegenskaper.isEmpty() && !behandlingsegenskaper.contains(
            OppgaveGrunnlag.Behandlingsegenskap.TILBAKEKREVING_SENDT_VARSEL)) {
            kriterier.add(AndreKriterierType.IKKE_VARSLET);
        }
        if (!oppgaveGrunnlag.refusjonskrav() || behandlingsegenskaper.contains(OppgaveGrunnlag.Behandlingsegenskap.DIREKTE_UTBETALING)) {
            kriterier.add(AndreKriterierType.UTBETALING_TIL_BRUKER);
        }
        if (oppgaveGrunnlag.faresignaler() || behandlingsegenskaper.contains(OppgaveGrunnlag.Behandlingsegenskap.FARESIGNALER)) {
            kriterier.add(AndreKriterierType.VURDER_FARESIGNALER);
        }

        var behandlingsårsaker = oppgaveGrunnlag.behandlingsårsaker();
        if (behandlingsårsaker.contains(OppgaveGrunnlag.Behandlingsårsak.PLEIEPENGER)) {
            kriterier.add(AndreKriterierType.PLEIEPENGER);
        }
        if (behandlingsårsaker.contains(OppgaveGrunnlag.Behandlingsårsak.UTSATT_START)) {
            kriterier.add(AndreKriterierType.UTSATT_START);
        }
        if (behandlingsårsaker.contains(OppgaveGrunnlag.Behandlingsårsak.OPPHØR_NY_SAK)) {
            kriterier.add(AndreKriterierType.NYTT_VEDTAK);
        }
        if (behandlingsårsaker.contains(OppgaveGrunnlag.Behandlingsårsak.BERØRT)) {
            kriterier.add(AndreKriterierType.BERØRT_BEHANDLING);
        }
        if (behandlingsårsaker.contains(OppgaveGrunnlag.Behandlingsårsak.KLAGE_TILBAKEBETALING)) {
            kriterier.add(AndreKriterierType.KLAGE_PÅ_TILBAKEBETALING);
        }
        if (oppgaveGrunnlag.ytelse() == FagsakYtelseType.FORELDREPENGER && oppgaveGrunnlag.behandlingstype() == BehandlingType.REVURDERING
            && behandlingsårsaker.contains(OppgaveGrunnlag.Behandlingsårsak.SØKNAD)) {
            kriterier.add(AndreKriterierType.ENDRINGSSØKNAD);
        }
        if (oppgaveGrunnlag.behandlingstype() == BehandlingType.REVURDERING && behandlingsårsaker.contains(
            OppgaveGrunnlag.Behandlingsårsak.INNTEKTSMELDING) && behandlingsårsaker.size() == 1) {
            kriterier.add(AndreKriterierType.REVURDERING_INNTEKTSMELDING);
        }

        if (oppgaveGrunnlag.aksjonspunkt()
            .stream()
            .anyMatch(a -> a.type() == OppgaveGrunnlag.AksjonspunktType.TIL_BESLUTTER && a.status() == Aksjonspunktstatus.AVBRUTT)) {
            kriterier.add(AndreKriterierType.RETURNERT_FRA_BESLUTTER);
        }

        kriterier.addAll(beskyttelsesbehov.getBeskyttelsesKriterier(oppgaveGrunnlag.saksnummer()));

        return kriterier;
    }

    private boolean skalLageOppgave(OppgaveGrunnlag oppgaveGrunnlag) {
        return oppgaveGrunnlag.aksjonspunkt()
            .stream()
            .filter(a -> a.type() != OppgaveGrunnlag.AksjonspunktType.PÅ_VENT)
            .anyMatch(a -> a.status().equals(Aksjonspunktstatus.OPPRETTET));
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
        var behandlingsegenskaper = dto.behandlingsegenskaper().stream().map(OppgaveGrunnlag.Behandlingsegenskap::valueOf).toList();
        return new OppgaveGrunnlag(dto.behandlingUuid(), new Saksnummer(dto.saksnummer()), map(dto.ytelse()), new AktørId(dto.aktørId().getAktørId()),
            mapBehandlingType(dto), dto.opprettetTidspunkt(), dto.behandlendeEnhetId(), dto.behandlingsfrist(), dto.ansvarligSaksbehandlerIdent(),
            aksjonspunkter, behandlingsårsaker, dto.faresignaler(), dto.refusjonskrav(), saksegenskaper,
            dto.foreldrepengerDto() == null ? null : dto.foreldrepengerDto().førsteUttakDato(), behandlingsegenskaper);
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

    private List<OppgaveGrunnlag.Aksjonspunkt> mapAksjonspunkt(LosBehandlingDto dto) {
        return dto.aksjonspunkt().stream().map(ap -> new OppgaveGrunnlag.Aksjonspunkt(mapFraFpsak(ap), ap.status(), ap.fristTid())).toList();
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

    private OppgaveGrunnlag.AksjonspunktType mapFraFpsak(LosBehandlingDto.LosAksjonspunktDto aksjonspunktDto) {
        if (aksjonspunktDto.type() == Aksjonspunkttype.VENT) {
            return OppgaveGrunnlag.AksjonspunktType.PÅ_VENT;
        }
        if (aksjonspunktDto.type() == Aksjonspunkttype.BESLUTTER) {
            return OppgaveGrunnlag.AksjonspunktType.TIL_BESLUTTER;
        }
        if (aksjonspunktDto.type() == Aksjonspunkttype.PAPIRSØKNAD) {
            return OppgaveGrunnlag.AksjonspunktType.PAPIRSØKNAD;
        }
        return switch (aksjonspunktDto.definisjon()) {
            case AUTOMATISK_MARKERING_SOM_UTLAND -> OppgaveGrunnlag.AksjonspunktType.AUTOMATISK_MARKERING_SOM_UTLAND;
            case ARBEID_INNTEKT -> OppgaveGrunnlag.AksjonspunktType.ARBEID_OG_INNTEKT;
            case VURDER_FORMKRAV_KODE -> OppgaveGrunnlag.AksjonspunktType.VURDER_FORMKRAV;
            case KONTROLLER_TERMINBEKREFTELSE_KODE -> OppgaveGrunnlag.AksjonspunktType.KONTROLLER_TERMINBEKREFTELSE;
            case String kode when RELEVANT_NÆRING.contains(kode) -> OppgaveGrunnlag.AksjonspunktType.VURDER_NÆRING;
            default -> OppgaveGrunnlag.AksjonspunktType.ANNET;
        };
    }

    private OppgaveGrunnlag mapFraFpTilbake(LosBehandlingDto behandlingDto, LosFagsakEgenskaperDto losFagsakEgenskaperDto) {
        var aksjonspunkter = mapAksjonspunkt(behandlingDto);
        var behandlingsårsaker = mapBehandlingsårsaker(behandlingDto);

        var behandlingsegenskaper = behandlingDto.behandlingsegenskaper().stream().map(egenskap -> switch (egenskap.toUpperCase()) {
            case "VARSLET" -> OppgaveGrunnlag.Behandlingsegenskap.TILBAKEKREVING_SENDT_VARSEL;
            case "OVER_FIRE_RETTSGEBYR" -> OppgaveGrunnlag.Behandlingsegenskap.TILBAKEKREVING_OVER_FIRE_RETTSGEBYR;
            default -> throw new IllegalStateException("Unexpected value: " + egenskap);
        }).toList();
        return new OppgaveGrunnlag(behandlingDto.behandlingUuid(), new Saksnummer(behandlingDto.saksnummer()), map(behandlingDto.ytelse()),
            new AktørId(behandlingDto.aktørId().getAktørId()), mapBehandlingType(behandlingDto), behandlingDto.opprettetTidspunkt(),
            behandlingDto.behandlendeEnhetId(), behandlingDto.behandlingsfrist(), behandlingDto.ansvarligSaksbehandlerIdent(), aksjonspunkter,
            behandlingsårsaker, behandlingDto.faresignaler(), behandlingDto.refusjonskrav(),
            mapFagsakEgenskaper(losFagsakEgenskaperDto.saksegenskaper()),
            behandlingDto.foreldrepengerDto() == null ? null : behandlingDto.foreldrepengerDto().førsteUttakDato(), behandlingsegenskaper);
    }

    private static List<OppgaveGrunnlag.Saksegenskap> mapFagsakEgenskaper(List<String> saksegenskaper) {
        return saksegenskaper.stream().map(OppgaveGrunnlag.Saksegenskap::valueOf).toList();
    }

    private record OppgaveGrunnlag(UUID behandlingUuid, Saksnummer saksnummer, FagsakYtelseType ytelse, AktørId aktørId,
                                   BehandlingType behandlingstype, LocalDateTime opprettetTidspunkt, String behandlendeEnhetId,
                                   LocalDate behandlingsfrist, String ansvarligSaksbehandlerIdent, List<Aksjonspunkt> aksjonspunkt,
                                   List<Behandlingsårsak> behandlingsårsaker, boolean faresignaler, boolean refusjonskrav,
                                   List<Saksegenskap> saksegenskaper, LocalDate førsteUttaksdatoForeldrepenger, //null hvis ES og SVP
                                   List<Behandlingsegenskap> behandlingsegenskaper) {

        public enum Saksegenskap {
            EØS_BOSATT_NORGE,
            BOSATT_UTLAND,
            SAMMENSATT_KONTROLL,
            DØD,
            NÆRING,
            BARE_FAR_RETT,
            PRAKSIS_UTSETTELSE,
            HASTER,
        }

        public enum Behandlingsegenskap {
            SYKDOMSVURDERING,
            MOR_UKJENT_UTLAND,
            FARESIGNALER,
            DIREKTE_UTBETALING,
            REFUSJONSKRAV,
            TILBAKEKREVING_SENDT_VARSEL,
            TILBAKEKREVING_OVER_FIRE_RETTSGEBYR
        }

        public record Aksjonspunkt(AksjonspunktType type, Aksjonspunktstatus status, LocalDateTime fristTidt) {
        }

        public enum Behandlingsårsak {
            SØKNAD,
            INNTEKTSMELDING,
            FOLKEREGISTER,
            PLEIEPENGER,
            ETTERKONTROLL,
            MANUELL,
            BERØRT,
            UTSATT_START,
            OPPHØR_NY_SAK,
            REGULERING,
            KLAGE_OMGJØRING,
            KLAGE_TILBAKEBETALING,
            ANNET
        }

        public enum AksjonspunktType {
            TIL_BESLUTTER,
            ANNET,
            KONTROLLER_TERMINBEKREFTELSE,
            AUTOMATISK_MARKERING_SOM_UTLAND,
            ARBEID_OG_INNTEKT,
            VURDER_FORMKRAV,
            PÅ_VENT,
            VURDER_NÆRING,
            PAPIRSØKNAD
        }
    }
}
