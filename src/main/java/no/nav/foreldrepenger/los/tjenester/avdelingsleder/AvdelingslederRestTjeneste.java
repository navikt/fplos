package no.nav.foreldrepenger.los.tjenester.avdelingsleder;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.MediaType;
import no.nav.foreldrepenger.los.avdelingsleder.AvdelingslederTjeneste;
import no.nav.foreldrepenger.los.avdelingsleder.innlogget.AnsattInfoKlient;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.dto.AvdelingDto;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.dto.InitLinksDto;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ActionType;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ResourceType;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import no.nav.vedtak.sikkerhet.kontekst.AnsattGruppe;

import java.util.Comparator;
import java.util.List;

@Path("/avdelingsleder")
@ApplicationScoped
@Transactional
public class AvdelingslederRestTjeneste {

    private AnsattInfoKlient ansattInfoKlient;
    private AvdelingslederTjeneste avdelingslederTjeneste;

    public AvdelingslederRestTjeneste() {
        //NOSONAR
    }

    @Inject
    public AvdelingslederRestTjeneste(AvdelingslederTjeneste avdelingslederTjeneste, AnsattInfoKlient ansattInfoKlient) {
        this.avdelingslederTjeneste = avdelingslederTjeneste;
        this.ansattInfoKlient = ansattInfoKlient;
    }

    @GET
    @Path("/avdelinger")
    @Produces("application/json")
    @Operation(description = "Henter alle avdelinger", tags = "AvdelingslederTopp")
    @BeskyttetRessurs(actionType = ActionType.READ, resourceType = ResourceType.OPPGAVESTYRING, sporingslogg = false)
    public List<AvdelingDto> hentAvdelinger() {
        // erstattes av init-fetch når frontend er over på ny app
        return avdelingslederTjeneste.hentAvdelinger()
            .stream()
            .map(avdeling -> new AvdelingDto(avdeling.getId(), avdeling.getAvdelingEnhet(), avdeling.getNavn(), avdeling.getKreverKode6()))
            .sorted(Comparator.comparing(AvdelingDto::getKreverKode6).thenComparing(a -> Long.valueOf(a.getAvdelingEnhet())))
            .toList();
    }

    @GET
    @Path("/init-fetch")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(description = "Returnerer ", tags = "init-fetch")
    @BeskyttetRessurs(actionType = ActionType.READ, resourceType = ResourceType.APPLIKASJON, sporingslogg = false)
    public InitLinksDto hentInitielleRessurser() {
        var harStrengtFortroligTilgang = ansattInfoKlient.medlemAvAnsattGruppe(AnsattGruppe.STRENGTFORTROLIG);
        var avdelinger = avdelingslederTjeneste.hentAvdelinger()
            .stream()
            .filter(avd -> !avd.getKreverKode6() || harStrengtFortroligTilgang)
            .map(avdeling -> new AvdelingDto(avdeling.getId(), avdeling.getAvdelingEnhet(), avdeling.getNavn(), avdeling.getKreverKode6()))
            .sorted(Comparator.comparing(AvdelingDto::getKreverKode6).thenComparing(a -> Long.valueOf(a.getAvdelingEnhet())))
            .toList();
        return new InitLinksDto(ansattInfoKlient.innloggetBruker(), avdelinger);
    }
}
