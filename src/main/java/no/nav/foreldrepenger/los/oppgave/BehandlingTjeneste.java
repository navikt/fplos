package no.nav.foreldrepenger.los.oppgave;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.los.domene.typer.Fagsystem;
import no.nav.foreldrepenger.los.domene.typer.Saksnummer;
import no.nav.foreldrepenger.los.domene.typer.aktør.AktørId;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.Aksjonspunkt;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.OppgaveUtil;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.tilbakekreving.TilbakekrevingOppgaveEgenskapFinner;
import no.nav.vedtak.hendelser.behandling.Aksjonspunktstatus;
import no.nav.vedtak.hendelser.behandling.Behandlingsstatus;
import no.nav.vedtak.hendelser.behandling.los.LosBehandlingDto;

@ApplicationScoped
public class BehandlingTjeneste {

    private static final Logger LOG = LoggerFactory.getLogger(BehandlingTjeneste.class);

    private OppgaveRepository oppgaveRepository;

    @Inject
    public BehandlingTjeneste(OppgaveRepository oppgaveRepository) {
        this.oppgaveRepository = oppgaveRepository;
    }

    BehandlingTjeneste() {
    }

    public void safeLagreBehandling(LosBehandlingDto dto, Fagsystem kildeSystem) {
        try {
            lagreBehandling(dto, kildeSystem);
        } catch (Exception e) {
            LOG.info("Feil ved lagring av behandling fra {} med uuid {}: {}", kildeSystem, dto.behandlingUuid(), e.getMessage());
        }
    }

    public void lagreBehandling(LosBehandlingDto dto, Fagsystem kildeSystem) {

        var builder = Behandling.builder(oppgaveRepository.finnBehandling(dto.behandlingUuid()))
            .medId(dto.behandlingUuid())
            .medSaksnummer(new Saksnummer(dto.saksnummer()))
            .medAktørId(new AktørId(dto.aktørId().getAktørId()))
            .medBehandlendeEnhet(dto.behandlendeEnhetId())
            .medKildeSystem(kildeSystem)
            .medFagsakYtelseType(OppgaveUtil.mapYtelse(dto.ytelse()))
            .medBehandlingType(OppgaveUtil.mapBehandlingstype(dto.behandlingstype()))
            .medBehandlingTilstand(mapBehandlingTilstand(dto, kildeSystem))
            .medAktiveAksjonspunkt(mapAktiveAksjonspunkt(dto))
            .medVentefrist(mapTidligsteVentefrist(dto))
            .medOpprettet(dto.opprettetTidspunkt())
            .medAvsluttet(Behandlingsstatus.AVSLUTTET.equals(dto.behandlingsstatus()) ? LocalDateTime.now() : null)
            .medBehandlingsfrist(dto.behandlingsfrist())
            .medFørsteStønadsdag(Optional.ofNullable(dto.foreldrepengerDto()).map(LosBehandlingDto.LosForeldrepengerDto::førsteUttakDato).orElse(null))
            .medFeilutbetalingBelop(Optional.ofNullable(dto.tilbakeDto()).map(LosBehandlingDto.LosTilbakeDto::feilutbetaltBeløp).orElse(null))
            .medFeilutbetalingStart(Optional.ofNullable(dto.tilbakeDto()).map(LosBehandlingDto.LosTilbakeDto::førsteFeilutbetalingDato).orElse(null))
            ;
        oppgaveRepository.lagreBehandling(builder.build());
    }


    private BehandlingTilstand mapBehandlingTilstand(LosBehandlingDto dto, Fagsystem kildeSystem) {
        return switch (dto.behandlingsstatus()) {
            case OPPRETTET -> BehandlingTilstand.OPPRETTET;
            case UTREDES -> utledFraAksjonspunkt(dto, kildeSystem);
            case FATTER_VEDTAK -> BehandlingTilstand.BESLUTTER;
            case IVERKSETTER_VEDTAK -> BehandlingTilstand.AVSLUTTET;
            case AVSLUTTET -> BehandlingTilstand.AVSLUTTET;
        };
    }

    private String mapAktiveAksjonspunkt(LosBehandlingDto dto) {
        return Optional.ofNullable(dto.aksjonspunkt()).orElseGet(List::of).stream()
            .filter(a -> Aksjonspunktstatus.OPPRETTET.equals(a.status()))
            .map(LosBehandlingDto.LosAksjonspunktDto::definisjon)
            .sorted(Comparator.naturalOrder())
            .collect(Collectors.joining(";"));
    }

    private LocalDateTime mapTidligsteVentefrist(LosBehandlingDto dto) {
        return Optional.ofNullable(dto.aksjonspunkt()).orElseGet(List::of).stream()
            .filter(a -> Aksjonspunktstatus.OPPRETTET.equals(a.status()))
            .map(LosBehandlingDto.LosAksjonspunktDto::fristTid)
            .filter(Objects::nonNull)
            .min(Comparator.naturalOrder())
            .orElse(null);
    }

    private BehandlingTilstand utledFraAksjonspunkt(LosBehandlingDto dto, Fagsystem kildeSystem) {
        if (dto.aksjonspunkt() == null || dto.aksjonspunkt().isEmpty()) {
            return BehandlingTilstand.INGEN;
        }
        if (Fagsystem.FPTILBAKE.equals(kildeSystem)) {
            if (TilbakekrevingOppgaveEgenskapFinner.aktivVentBruker(dto.aksjonspunkt()) ){
                return BehandlingTilstand.VENT_MANUELL;
            } else if (TilbakekrevingOppgaveEgenskapFinner.aktivVentKrav(dto.aksjonspunkt())) {
                return BehandlingTilstand.VENT_REGISTERDATA;
            } else if (TilbakekrevingOppgaveEgenskapFinner.aktivtBeslutterAp(dto.aksjonspunkt())) {
                return BehandlingTilstand.BESLUTTER;
            } else if (!TilbakekrevingOppgaveEgenskapFinner.aktiveApForutenBeslutterEllerVent(dto.aksjonspunkt())) {
                return BehandlingTilstand.INGEN;
            } else if (TilbakekrevingOppgaveEgenskapFinner.avbruttBeslutterAp(dto.aksjonspunkt())) {
                return BehandlingTilstand.RETUR;
            } else {
                return BehandlingTilstand.AKSJONSPUNKT;
            }
        } else {
            var retur = dto.aksjonspunkt().stream()
                .map(Aksjonspunkt::aksjonspunktFra)
                .anyMatch(Aksjonspunkt::erReturnertFraBeslutter);
            var aktive = dto.aksjonspunkt().stream()
                .filter(a -> Aksjonspunktstatus.OPPRETTET.equals(a.status()))
                .map(Aksjonspunkt::aksjonspunktFra)
                .toList();
            if (aktive.isEmpty()) {
                return BehandlingTilstand.INGEN;
            } else if (aktive.stream().anyMatch(Aksjonspunkt::erVentManuell)) {
                return BehandlingTilstand.VENT_MANUELL;
            } else if (aktive.stream().anyMatch(Aksjonspunkt::erVentTidlig)) {
                return BehandlingTilstand.VENT_TIDLIG;
            } else if (aktive.stream().anyMatch(Aksjonspunkt::erVentKø)) {
                return BehandlingTilstand.VENT_KØ;
            } else if (aktive.stream().anyMatch(Aksjonspunkt::erVentKomplett)) {
                return BehandlingTilstand.VENT_KOMPLETT;
            } else if (aktive.stream().anyMatch(Aksjonspunkt::erVentKlage)) {
                return BehandlingTilstand.VENT_KLAGEINSTANS;
            } else if (aktive.stream().anyMatch(Aksjonspunkt::erPåVent)) {
                return BehandlingTilstand.VENT_REGISTERDATA;
            } else if (aktive.stream().anyMatch(Aksjonspunkt::erRegistrerPapirSøknad)) {
                return BehandlingTilstand.PAPIRSØKNAD;
            } else if (aktive.stream().anyMatch(Aksjonspunkt::erTilBeslutter)) {
                return BehandlingTilstand.BESLUTTER;
            } else if (retur) {
                return BehandlingTilstand.RETUR;
            } else {
                return BehandlingTilstand.AKSJONSPUNKT;
            }
        }
    }
}
