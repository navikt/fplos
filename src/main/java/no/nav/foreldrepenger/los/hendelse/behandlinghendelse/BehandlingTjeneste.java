package no.nav.foreldrepenger.los.hendelse.behandlinghendelse;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.domene.typer.Fagsystem;
import no.nav.foreldrepenger.los.domene.typer.Saksnummer;
import no.nav.foreldrepenger.los.domene.typer.aktør.AktørId;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.Aksjonspunkt;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.OppgaveUtil;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.Behandling;
import no.nav.foreldrepenger.los.oppgave.BehandlingTilstand;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.vedtak.hendelser.behandling.Aksjonspunktstatus;
import no.nav.vedtak.hendelser.behandling.Aksjonspunkttype;
import no.nav.vedtak.hendelser.behandling.Behandlingsstatus;
import no.nav.vedtak.hendelser.behandling.los.LosBehandlingDto;
import no.nav.vedtak.hendelser.behandling.los.LosFagsakEgenskaperDto;

@ApplicationScoped
public class BehandlingTjeneste {

    private OppgaveRepository oppgaveRepository;

    @Inject
    public BehandlingTjeneste(OppgaveRepository oppgaveRepository) {
        this.oppgaveRepository = oppgaveRepository;
    }

    BehandlingTjeneste() {
    }

    public List<Behandling> hentBehandlinger(Set<BehandlingId> behandlingIder) {
        return oppgaveRepository.finnBehandlinger(behandlingIder.stream().map(BehandlingId::getValue).collect(Collectors.toSet()));
    }

    public void mottaBehandlingMigrering(LosBehandlingDto dto, LosFagsakEgenskaperDto egenskaper,
                                         Fagsystem kildeSystem, Set<AndreKriterierType> beskyttelseKriterier) {
        var eksisterendeBehandling = oppgaveRepository.finnBehandling(dto.behandlingUuid());
        var grunnlag = OppgaveGrunnlagUtleder.lagGrunnlag(dto, egenskaper);
        var nyeKriterier = KriterieUtleder.utledKriterier(grunnlag, beskyttelseKriterier);
        lagreBehandling(dto, kildeSystem, eksisterendeBehandling, nyeKriterier);
    }

    public void lagreBehandling(LosBehandlingDto dto, Fagsystem kildeSystem,
                                Optional<Behandling> eksisterendeBehandling,
                                Set<AndreKriterierType> nyeKriterier) {
        var tilstand = mapBehandlingTilstand(dto, kildeSystem);
        if (BehandlingTilstand.VENT_SØKNAD.equals(tilstand) && eksisterendeBehandling.isEmpty()) {
            return;
        }
        var eksisterendeKriterier = oppgaveRepository.finnBehandlingKriterier(dto.behandlingUuid());
        var kriterierEndret = eksisterendeKriterier.size() != nyeKriterier.size() || !eksisterendeKriterier.containsAll(nyeKriterier);
        var builder = Behandling.builder(eksisterendeBehandling)
            .medId(dto.behandlingUuid())
            .medSaksnummer(new Saksnummer(dto.saksnummer()))
            .medAktørId(new AktørId(dto.aktørId().getAktørId()))
            .medBehandlendeEnhet(dto.behandlendeEnhetId())
            .medKildeSystem(kildeSystem)
            .medFagsakYtelseType(OppgaveUtil.mapYtelse(dto.ytelse()))
            .medBehandlingType(OppgaveUtil.mapBehandlingstype(dto.behandlingstype()))
            .medBehandlingTilstand(tilstand)
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
        if (kriterierEndret) {
            var fjernes = eksisterendeKriterier.isEmpty() ? EnumSet.noneOf(AndreKriterierType.class) : EnumSet.copyOf(eksisterendeKriterier);
            fjernes.removeAll(nyeKriterier);
            oppgaveRepository.fjernBehandlingEgenskaper(dto.behandlingUuid(), fjernes);
            var leggesTil = nyeKriterier.isEmpty() ? EnumSet.noneOf(AndreKriterierType.class) : EnumSet.copyOf(nyeKriterier);
            leggesTil.removeAll(eksisterendeKriterier);
            oppgaveRepository.nyeBehandlingEgenskaper(dto.behandlingUuid(), leggesTil);
        }
    }

    private BehandlingTilstand mapBehandlingTilstand(LosBehandlingDto dto, Fagsystem kildeSystem) {
        return switch (dto.behandlingsstatus()) {
            case OPPRETTET, UTREDES -> utledFraAksjonspunkt(dto, kildeSystem);
            case FATTER_VEDTAK -> BehandlingTilstand.BESLUTTER;
            case IVERKSETTER_VEDTAK, AVSLUTTET -> BehandlingTilstand.AVSLUTTET;
        };
    }

    private String mapAktiveAksjonspunkt(LosBehandlingDto dto) {
        return Optional.ofNullable(dto.aksjonspunkt()).orElseGet(List::of).stream()
            .filter(a -> Aksjonspunktstatus.OPPRETTET.equals(a.status()))
            .filter(a -> !Aksjonspunkttype.VENT.equals(a.type()))
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
            return utledTilstandTilbake(dto);
        } else {
            return utledTilstandFpsak(dto);
        }
    }

    private static BehandlingTilstand utledTilstandTilbake(LosBehandlingDto dto) {
        var aksjonspunkt = Optional.ofNullable(dto.aksjonspunkt()).orElseGet(List::of);
        if (aktivVentBruker(aksjonspunkt)) {
            return BehandlingTilstand.VENT_MANUELL;
        } else if (aktivVentKrav(aksjonspunkt)) {
            return BehandlingTilstand.VENT_REGISTERDATA;
        } else if (aktivtBeslutterAp(aksjonspunkt)) {
            return BehandlingTilstand.BESLUTTER;
        } else if (!aktiveApForutenBeslutterEllerVent(aksjonspunkt)) {
            return BehandlingTilstand.INGEN;
        } else {
            return BehandlingTilstand.AKSJONSPUNKT;
        }
    }

    private static boolean aktivVentBruker(List<LosBehandlingDto.LosAksjonspunktDto> aksjonspunkter) {
        return aksjonspunkter.stream()
            .anyMatch(a -> "7001".equals(a.definisjon()) && Aksjonspunktstatus.OPPRETTET.equals(a.status()));
    }

    private static boolean aktivVentKrav(List<LosBehandlingDto.LosAksjonspunktDto> aksjonspunkter) {
        return aksjonspunkter.stream()
            .anyMatch(a -> "7002".equals(a.definisjon()) && Aksjonspunktstatus.OPPRETTET.equals(a.status()));
    }

    private static boolean aktivtBeslutterAp(List<LosBehandlingDto.LosAksjonspunktDto> aksjonspunkter) {
        return aksjonspunkter.stream()
            .anyMatch(a -> Aksjonspunkttype.BESLUTTER.equals(a.type()) && Aksjonspunktstatus.OPPRETTET.equals(a.status()));
    }

    private static boolean aktiveApForutenBeslutterEllerVent(List<LosBehandlingDto.LosAksjonspunktDto> aksjonspunkter) {
        return aksjonspunkter.stream()
            .anyMatch(a -> !Set.of(Aksjonspunkttype.BESLUTTER, Aksjonspunkttype.VENT).contains(a.type()) && Aksjonspunktstatus.OPPRETTET.equals(a.status()));
    }

    private static BehandlingTilstand utledTilstandFpsak(LosBehandlingDto dto) {
        var aktive = Optional.ofNullable(dto.aksjonspunkt()).orElseGet(List::of).stream()
            .filter(a -> Aksjonspunktstatus.OPPRETTET.equals(a.status()))
            .map(Aksjonspunkt::aksjonspunktFra)
            .toList();
        if (aktive.isEmpty()) {
            return BehandlingTilstand.INGEN;
        } else if (aktive.stream().anyMatch(Aksjonspunkt::erVentSøknad)) {
            return BehandlingTilstand.VENT_SØKNAD;
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
        } else {
            return BehandlingTilstand.AKSJONSPUNKT;
        }
    }
}
