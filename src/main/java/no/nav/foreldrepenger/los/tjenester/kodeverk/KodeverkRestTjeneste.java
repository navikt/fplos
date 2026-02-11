package no.nav.foreldrepenger.los.tjenester.kodeverk;

import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import no.nav.foreldrepenger.los.felles.Kodeverdi;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakStatus;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.los.oppgavekø.KøSortering;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.nøkkeltall.dto.BehandlingVenteStatus;
import no.nav.foreldrepenger.los.tjenester.felles.dto.OppgaveBehandlingStatus;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ActionType;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ResourceType;

@Path("/kodeverk")
@ApplicationScoped
public class KodeverkRestTjeneste {

    private static final Map<String, List<KodeverdiMedNavnDto>> KODEVERDIER = Map.ofEntries(lagEnumEntry(AndreKriterierType.class),
        lagEnumEntry(BehandlingType.class), lagEnumEntry(BehandlingVenteStatus.class), lagEnumEntry(FagsakStatus.class),
        lagEnumEntry(FagsakYtelseType.class), lagEnumEntry(KøSortering.class), lagEnumEntry(OppgaveBehandlingStatus.class));

    KodeverkRestTjeneste() {
        // for cdi
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Henter kodeliste", tags = "Kodeverk")
    @BeskyttetRessurs(actionType = ActionType.READ, resourceType = ResourceType.APPLIKASJON, sporingslogg = false)
    public Response hentGruppertKodeliste() {
        return Response.ok().entity(KODEVERDIER).build();
    }

    @GET
    @Path("/kriterie-filter")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Henter filter for kriterietyper", tags = "Kodeverk")
    @BeskyttetRessurs(actionType = ActionType.READ, resourceType = ResourceType.APPLIKASJON, sporingslogg = false)
    public Map<AndreKriterierType, KriterieFilterDto> hentKriterieFilter() {
        return Arrays.stream(AndreKriterierType.values())
            .collect(toMap(k -> k, k -> new KriterieFilterDto(k,
                k.getValgbarForBehandlingTyper(), k.getValgbarForYtelseTyper())));
    }

    private static Map.Entry<String, List<KodeverdiMedNavnDto>> lagEnumEntry(Class<? extends Kodeverdi> kodeverkClass) {
        if (!Enum.class.isAssignableFrom(kodeverkClass)) {
            throw new IllegalArgumentException("Ikke enum: " + kodeverkClass.getSimpleName());
        }
        var dtos = Arrays.stream(kodeverkClass.getEnumConstants())
            .map(k -> new KodeverdiMedNavnDto(k.getKode(), k.getNavn()))
            .sorted(Comparator.comparing(KodeverdiMedNavnDto::navn))
            .toList();
        return Map.entry(kodeverkClass.getSimpleName(), dtos);
    }


    public record KriterieFilterDto(AndreKriterierType andreKriterierType,
                                    Set<BehandlingType> valgbarForBehandlingTyper,
                                    Set<FagsakYtelseType> valgbarForYtelseTyper) {
    }
}
