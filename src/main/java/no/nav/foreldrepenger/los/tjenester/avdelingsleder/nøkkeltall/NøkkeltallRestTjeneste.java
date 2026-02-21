package no.nav.foreldrepenger.los.tjenester.avdelingsleder.nøkkeltall;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import no.nav.foreldrepenger.los.statistikk.KøStatistikkDto;
import no.nav.foreldrepenger.los.statistikk.StatistikkRepository;
import no.nav.foreldrepenger.los.statistikk.kø.StatistikkOppgaveFilter;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.dto.AvdelingEnhetDto;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.nøkkeltall.dto.NøkkeltallBehandlingFørsteUttakDto;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.nøkkeltall.dto.NøkkeltallBehandlingVentefristUtløperDto;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.nøkkeltall.dto.OppgaveEndringForAvdelingPerDato;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.nøkkeltall.dto.OppgaverForAvdeling;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.nøkkeltall.dto.OppgaverForAvdelingPerDato;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.nøkkeltall.dto.OppgaverForFørsteStønadsdagUkeMåned;
import no.nav.foreldrepenger.los.tjenester.felles.dto.SakslisteIdDto;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ActionType;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ResourceType;

@Path("/avdelingsleder/nøkkeltall")
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
@Transactional
public class NøkkeltallRestTjeneste {

    private NøkkeltallRepository nøkkeltallRepository;
    private StatistikkRepository statistikkRepository;

    public NøkkeltallRestTjeneste() {
        // For Rest-CDI
    }

    @Inject
    public NøkkeltallRestTjeneste(NøkkeltallRepository nøkkeltallRepository, StatistikkRepository statistikkRepository) {
        this.nøkkeltallRepository = nøkkeltallRepository;
        this.statistikkRepository = statistikkRepository;
    }

    @GET
    @Path("/behandlinger-under-arbeid")
    @Operation(description = "UnderArbeid", tags = "AvdelingslederTall")
    @BeskyttetRessurs(actionType = ActionType.READ, resourceType = ResourceType.OPPGAVESTYRING_AVDELINGENHET, sporingslogg = false)
    public List<OppgaverForAvdeling> getAlleOppgaverForAvdeling(@NotNull @QueryParam("avdelingEnhet") @Valid AvdelingEnhetDto avdelingEnhet) {
        return nøkkeltallRepository.hentAlleOppgaverForAvdeling(avdelingEnhet.getAvdelingEnhet());
    }

    @GET
    @Path("/behandlinger-under-arbeid-historikk")
    @Operation(description = "UA Historikk", tags = "AvdelingslederTall")
    @BeskyttetRessurs(actionType = ActionType.READ, resourceType = ResourceType.OPPGAVESTYRING_AVDELINGENHET, sporingslogg = false)
    public List<OppgaverForAvdelingPerDato> getAntallOppgaverForAvdelingPerDato(@NotNull @QueryParam("avdelingEnhet") @Valid AvdelingEnhetDto avdelingEnhet) {
        var eldre = statistikkRepository.hentStatistikkForEnhetFomDato(avdelingEnhet.getAvdelingEnhet(), LocalDate.now().minusWeeks(4)).stream()
            .map(s -> new OppgaverForAvdelingPerDato(s.getFagsakYtelseType(), s.getBehandlingType(),
                s.getStatistikkDato(), s.getStatistikkDato(), Long.valueOf(s.getAntallAktive())));
        var dagens = statistikkRepository.hentÅpneOppgaverPerEnhetYtelseBehandling().stream()
            .filter(tall -> Objects.equals(tall.enhet(), avdelingEnhet.getAvdelingEnhet()))
            .map(tall -> new OppgaverForAvdelingPerDato(tall.fagsakYtelseType(), tall.behandlingType(),
                LocalDate.now(), LocalDate.now(), tall.antall()));
        return Stream.concat(eldre, dagens).toList();
    }

    @GET
    @Path("/behandlinger-opprettet-avsluttet")
    @Operation(description = "UA Historikk", tags = "AvdelingslederTall")
    @BeskyttetRessurs(actionType = ActionType.READ, resourceType = ResourceType.OPPGAVESTYRING_AVDELINGENHET, sporingslogg = false)
    public List<OppgaveEndringForAvdelingPerDato> getAntallOppgaverEndretForAvdelingPerDato(@NotNull @QueryParam("avdelingEnhet") @Valid AvdelingEnhetDto avdelingEnhet) {
        return statistikkRepository.hentStatistikkForEnhetFomDato(avdelingEnhet.getAvdelingEnhet(), LocalDate.now().minusWeeks(4)).stream()
            .map(s -> new OppgaveEndringForAvdelingPerDato(s.getFagsakYtelseType(), s.getBehandlingType(),
                s.getStatistikkDato(), s.getAntallOpprettet(), s.getAntallAvsluttet()))
            .toList();
    }

    @GET
    @Path("/behandlinger-første-stønadsdag-mnd")
    @Operation(description = "Første stønadsdag pr måned", tags = "AvdelingslederTall")
    @BeskyttetRessurs(actionType = ActionType.READ, resourceType = ResourceType.OPPGAVESTYRING_AVDELINGENHET, sporingslogg = false)
    public List<OppgaverForFørsteStønadsdagUkeMåned> getOppgaverPerFørsteStønadsdagMåned(@NotNull @QueryParam("avdelingEnhet") @Valid AvdelingEnhetDto avdelingEnhet) {
        return nøkkeltallRepository.hentOppgaverPerFørsteStønadsdagMåned(avdelingEnhet.getAvdelingEnhet());
    }

    @GET
    @Path("/åpne-behandlinger")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Åpne behandlinger", tags = "AvdelingslederTall")
    @BeskyttetRessurs(actionType = ActionType.READ, resourceType = ResourceType.OPPGAVESTYRING_AVDELINGENHET, sporingslogg = false)
    public List<NøkkeltallBehandlingFørsteUttakDto> getAlleBehandlingerForAvdeling(@NotNull @QueryParam("avdelingEnhet") @Valid AvdelingEnhetDto avdelingEnhet) {
        return nøkkeltallRepository.hentBehandlingMånedsfordeltStønadsdato(avdelingEnhet.getAvdelingEnhet());
    }

    @GET
    @Path("/frist-utløp")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Førstegangsbehandlinger på vent pr enhet, ytelse og ventefrist", tags = "AvdelingslederTall")
    @BeskyttetRessurs(actionType = ActionType.READ, resourceType = ResourceType.OPPGAVESTYRING_AVDELINGENHET, sporingslogg = false)
    public List<NøkkeltallBehandlingVentefristUtløperDto> getAlleVentefristerForAvdeling(@NotNull @QueryParam("avdelingEnhet") @Valid AvdelingEnhetDto avdelingEnhet) {
        return nøkkeltallRepository.hentVentefristUkefordelt(avdelingEnhet.getAvdelingEnhet());
    }

    @GET
    @Path("/statistikk-oppgave-filter")
    @Operation(description = "Hent statistikk for kø den siste måneden")
    @BeskyttetRessurs(actionType = ActionType.READ, resourceType = ResourceType.OPPGAVESTYRING_AVDELINGENHET, sporingslogg = false)
    public List<KøStatistikkDto> aktiveOgTilgjengligeOppgaverStatistikkForKø(@QueryParam("sakslisteId") @NotNull @Valid SakslisteIdDto sakslisteId) {
        return statistikkRepository.hentStatistikkOppgaveFilterFraFom(sakslisteId.getVerdi(), LocalDate.now().minusMonths(1)).stream()
            .map(NøkkeltallRestTjeneste::tilDto)
            .sorted(Comparator.comparing(KøStatistikkDto::tidspunkt))
            .toList();
    }

    public static KøStatistikkDto tilDto(StatistikkOppgaveFilter s) {
        var tid = Instant.ofEpochMilli(s.getTidsstempel()).atZone(ZoneId.systemDefault()).toLocalDateTime();
        return new KøStatistikkDto(tid, s.getAntallAktive(), s.getAntallTilgjengelige(), s.getAntallVentende(), s.getAntallAvsluttet());
    }
}
