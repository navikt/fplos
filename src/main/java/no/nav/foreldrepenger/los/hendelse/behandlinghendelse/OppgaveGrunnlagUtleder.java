package no.nav.foreldrepenger.los.hendelse.behandlinghendelse;

import java.util.List;

import no.nav.foreldrepenger.los.domene.typer.Saksnummer;
import no.nav.foreldrepenger.los.domene.typer.aktør.AktørId;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;
import no.nav.vedtak.hendelser.behandling.Aksjonspunkttype;
import no.nav.vedtak.hendelser.behandling.Behandlingsårsak;
import no.nav.vedtak.hendelser.behandling.Kildesystem;
import no.nav.vedtak.hendelser.behandling.Ytelse;
import no.nav.vedtak.hendelser.behandling.los.LosBehandlingDto;
import no.nav.vedtak.hendelser.behandling.los.LosFagsakEgenskaperDto;

class OppgaveGrunnlagUtleder {

    private static final String KONTROLLER_TERMINBEKREFTELSE_KODE = "5001";
    private static final String AUTOMATISK_MARKERING_SOM_UTLAND = "5068";
    private static final String ARBEID_INNTEKT = "5085";
    private static final String VURDER_FORMKRAV_KODE = "5082";
    private static final List<String> RELEVANT_NÆRING = List.of("5039", "5049", "5058", "5046", "5051", "5089", "5082", "5035");

    private OppgaveGrunnlagUtleder() {
    }

    static OppgaveGrunnlag lagGrunnlag(LosBehandlingDto dto, LosFagsakEgenskaperDto fagsakEgenskaperDto) {
        if (dto.kildesystem().equals(Kildesystem.FPSAK)) {
            return mapFraFpsak(dto, fagsakEgenskaperDto);
        } else {
            return mapFraFpTilbake(dto, fagsakEgenskaperDto);
        }
    }

    private static OppgaveGrunnlag mapFraFpsak(LosBehandlingDto dto, LosFagsakEgenskaperDto fagsakEgenskaperDto) {
        var aksjonspunkter = mapFpsakAksjonspunkt(dto);
        var behandlingsårsaker = mapBehandlingsårsaker(dto);
        var saksegenskaper = mapFagsakEgenskaper(fagsakEgenskaperDto.saksegenskaper());
        var behandlingsegenskaper = dto.behandlingsegenskaper().stream().map(OppgaveGrunnlag.Behandlingsegenskap::valueOf).toList();
        return new OppgaveGrunnlag(dto.behandlingUuid(), new Saksnummer(dto.saksnummer()), map(dto.ytelse()), new AktørId(dto.aktørId().getAktørId()),
            mapBehandlingType(dto), dto.opprettetTidspunkt(), dto.behandlendeEnhetId(), dto.behandlingsfrist(), dto.ansvarligSaksbehandlerIdent(),
            aksjonspunkter, behandlingsårsaker, dto.faresignaler(), dto.refusjonskrav(), saksegenskaper,
            dto.foreldrepengerDto() == null ? null : dto.foreldrepengerDto().førsteUttakDato(), behandlingsegenskaper, mapStatus(dto), null, null);
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

    private static List<OppgaveGrunnlag.Behandlingsårsak> mapBehandlingsårsaker(LosBehandlingDto dto) {
        return dto.behandlingsårsaker().stream().map(OppgaveGrunnlagUtleder::map).toList();
    }

    private static List<OppgaveGrunnlag.Aksjonspunkt> mapFpsakAksjonspunkt(LosBehandlingDto dto) {
        return dto.aksjonspunkt().stream().map(ap -> new OppgaveGrunnlag.Aksjonspunkt(mapFraFpsak(ap), ap.status(), ap.fristTid())).toList();
    }

    private static List<OppgaveGrunnlag.Aksjonspunkt> mapFptilbakeAksjonspunkt(LosBehandlingDto dto) {
        return dto.aksjonspunkt().stream().map(ap -> new OppgaveGrunnlag.Aksjonspunkt(mapFraFpTilbake(ap), ap.status(), ap.fristTid())).toList();
    }

    private static FagsakYtelseType map(Ytelse ytelse) {
        return switch (ytelse) {
            case ENGANGSTØNAD -> FagsakYtelseType.ENGANGSTØNAD;
            case FORELDREPENGER -> FagsakYtelseType.FORELDREPENGER;
            case SVANGERSKAPSPENGER -> FagsakYtelseType.SVANGERSKAPSPENGER;
        };
    }

    private static OppgaveGrunnlag.Behandlingsårsak map(Behandlingsårsak å) {
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

    private static OppgaveGrunnlag.AksjonspunktType mapFraFpsak(LosBehandlingDto.LosAksjonspunktDto aksjonspunktDto) {
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

    private static OppgaveGrunnlag mapFraFpTilbake(LosBehandlingDto behandlingDto, LosFagsakEgenskaperDto losFagsakEgenskaperDto) {
        var aksjonspunkter = mapFptilbakeAksjonspunkt(behandlingDto);
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
            behandlingDto.foreldrepengerDto() == null ? null : behandlingDto.foreldrepengerDto().førsteUttakDato(), behandlingsegenskaper,
            mapStatus(behandlingDto), behandlingDto.tilbakeDto().førsteFeilutbetalingDato().atStartOfDay(), behandlingDto.tilbakeDto().feilutbetaltBeløp());
    }

    private static OppgaveGrunnlag.AksjonspunktType mapFraFpTilbake(LosBehandlingDto.LosAksjonspunktDto aksjonspunktDto) {
        if (aksjonspunktDto.type() == Aksjonspunkttype.VENT) {
            return OppgaveGrunnlag.AksjonspunktType.PÅ_VENT;
        }
        if (aksjonspunktDto.type() == Aksjonspunkttype.BESLUTTER) {
            return OppgaveGrunnlag.AksjonspunktType.TIL_BESLUTTER;
        }
        return OppgaveGrunnlag.AksjonspunktType.ANNET;
    }

    private static OppgaveGrunnlag.BehandlingStatus mapStatus(LosBehandlingDto behandlingDto) {
        return switch (behandlingDto.behandlingsstatus()) {
            case OPPRETTET -> OppgaveGrunnlag.BehandlingStatus.OPPRETTET;
            case UTREDES -> OppgaveGrunnlag.BehandlingStatus.UTREDES;
            case FATTER_VEDTAK -> OppgaveGrunnlag.BehandlingStatus.FATTER_VEDTAK;
            case IVERKSETTER_VEDTAK -> OppgaveGrunnlag.BehandlingStatus.IVERKSETTER_VEDTAK;
            case AVSLUTTET -> OppgaveGrunnlag.BehandlingStatus.AVSLUTTET;
        };
    }

    private static List<OppgaveGrunnlag.Saksegenskap> mapFagsakEgenskaper(List<String> saksegenskaper) {
        return saksegenskaper.stream().map(OppgaveGrunnlag.Saksegenskap::valueOf).toList();
    }
}
