package no.nav.foreldrepenger.los.web.app.tjenester.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import no.nav.foreldrepenger.los.web.app.AbacAttributter;
import no.nav.foreldrepenger.los.web.app.tjenester.admin.dto.DriftsmeldingDto;
import no.nav.foreldrepenger.los.web.app.tjenester.admin.dto.DriftsmeldingOpprettelseDto;
import no.nav.foreldrepenger.loslager.admin.Driftsmelding;
import no.nav.fplos.domenetjenester.admin.DriftsmeldingTjeneste;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt.CREATE;
import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt.READ;

@Path("/driftsmeldinger")
@Transactional
@ApplicationScoped
public class DriftsmeldingerRestTjeneste {
    private DriftsmeldingTjeneste driftsmeldingTjeneste;

    @Inject
    public DriftsmeldingerRestTjeneste(DriftsmeldingTjeneste driftsmeldingTjeneste) {
        this.driftsmeldingTjeneste = driftsmeldingTjeneste;
    }

    public DriftsmeldingerRestTjeneste() {
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Driftsmeldinger", tags = "admin")
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    @BeskyttetRessurs(action = READ, resource = AbacAttributter.APPLIKASJON, sporingslogg = false)
    public List<DriftsmeldingDto> hentAktiveDriftsmeldinger() {
        return driftsmeldingTjeneste.hentAktiveDriftsmeldinger().stream()
                .map(DriftsmeldingerRestTjeneste::tilDto)
                .collect(Collectors.toList());
    }

    @GET
    @Path("/alle-driftsmeldinger")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Driftsmeldinger", tags = "admin")
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    @BeskyttetRessurs(action = READ, resource = AbacAttributter.APPLIKASJON, sporingslogg = false)
    public List<DriftsmeldingDto> hentAlleDriftsmeldinger() {
        return driftsmeldingTjeneste.hentAlleDriftsmeldinger().stream()
                .map(DriftsmeldingerRestTjeneste::tilDto)
                .collect(Collectors.toList());
    }

    @POST
    @Path("/opprett")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Driftsmeldinger", tags = "admin")
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    @BeskyttetRessurs(action = CREATE, resource = AbacAttributter.DRIFT, sporingslogg = false)
    public Response opprettDriftsmelding(@Parameter(description = "jepp") @NotNull @Valid DriftsmeldingOpprettelseDto dto) {
        var melding = tilDriftsmelding(dto);
        driftsmeldingTjeneste.opprettDriftsmelding(melding);
        return Response.ok().build();
    }

    @POST
    @Path("/deaktiver")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(description = "Driftsmeldinger", tags = "admin")
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    @BeskyttetRessurs(action = CREATE, resource = AbacAttributter.DRIFT, sporingslogg = false)
    public Response deaktiverDriftsmeldinger() {
        driftsmeldingTjeneste.deaktiverDriftsmeldinger();
        return Response.ok().build();
    }

    private static DriftsmeldingDto tilDto(Driftsmelding driftsmelding) {
        return new DriftsmeldingDto(String.valueOf(driftsmelding.getId()),
                driftsmelding.getMelding(), driftsmelding.getAktivFra(), driftsmelding.getAktivTil());
    }

    private Driftsmelding tilDriftsmelding(DriftsmeldingOpprettelseDto dto) {
        return Driftsmelding.Builder.builder()
                .medMelding(dto.getMelding())
                .medAktivFra(dto.getAktivFra())
                .medAktivTil(dto.getAktivTil())
                .build();
    }
}

