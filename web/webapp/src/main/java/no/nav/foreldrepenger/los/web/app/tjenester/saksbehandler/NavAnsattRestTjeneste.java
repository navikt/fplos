package no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler;

import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt.READ;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.swagger.v3.oas.annotations.Operation;
import no.nav.foreldrepenger.los.web.app.AbacAttributter;
import no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.dto.InnloggetNavAnsattDto;
import no.nav.foreldrepenger.los.web.app.util.LdapUtil;
import no.nav.vedtak.felles.integrasjon.ldap.LdapBruker;
import no.nav.vedtak.felles.integrasjon.ldap.LdapBrukeroppslag;
import no.nav.foreldrepenger.konfig.KonfigVerdi;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;
import no.nav.vedtak.sikkerhet.context.SubjectHandler;

@Path("/saksbehandler")
@ApplicationScoped
@Transactional
public class NavAnsattRestTjeneste {
    private String gruppenavnSaksbehandler;
    private String gruppenavnVeileder;
    private String gruppenavnBeslutter;
    private String gruppenavnEgenAnsatt;
    private String gruppenavnKode6;
    private String gruppenavnKode7;
    private String gruppenavnOppgavestyrer;

    @Inject
    public NavAnsattRestTjeneste(
        @KonfigVerdi("bruker.gruppenavn.saksbehandler") String gruppenavnSaksbehandler,
        @KonfigVerdi("bruker.gruppenavn.veileder") String gruppenavnVeileder,
        @KonfigVerdi("bruker.gruppenavn.beslutter") String gruppenavnBeslutter,
        @KonfigVerdi("bruker.gruppenavn.egenansatt") String gruppenavnEgenAnsatt,
        @KonfigVerdi("bruker.gruppenavn.kode6") String gruppenavnKode6,
        @KonfigVerdi("bruker.gruppenavn.kode7") String gruppenavnKode7,
        @KonfigVerdi("bruker.gruppenavn.oppgavestyrer") String gruppenavnOppgavestyrer
    ) {
        this.gruppenavnSaksbehandler = gruppenavnSaksbehandler;
        this.gruppenavnVeileder = gruppenavnVeileder;
        this.gruppenavnBeslutter = gruppenavnBeslutter;
        this.gruppenavnEgenAnsatt = gruppenavnEgenAnsatt;
        this.gruppenavnKode6 = gruppenavnKode6;
        this.gruppenavnKode7 = gruppenavnKode7;
        this.gruppenavnOppgavestyrer = gruppenavnOppgavestyrer;
    }

    NavAnsattRestTjeneste() {
        //CDI
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Returnerer fullt navn for ident", tags = "SaksbehandlerIdent")
    @BeskyttetRessurs(action = READ, resource = AbacAttributter.APPLIKASJON, sporingslogg = false)
    public InnloggetNavAnsattDto innloggetBruker() {
        var ident = SubjectHandler.getSubjectHandler().getUid();
        var ldapBruker = new LdapBrukeroppslag().hentBrukerinformasjon(ident);
        return getInnloggetBrukerDto(ident, ldapBruker);
    }

    InnloggetNavAnsattDto getInnloggetBrukerDto(String ident, LdapBruker ldapBruker) {
        var navn = ldapBruker.displayName();
        var grupper = LdapUtil.filtrerGrupper(ldapBruker.groups());
        return InnloggetNavAnsattDto.builder()
                .setBrukernavn(ident)
                .setNavn(navn)
                .setKanSaksbehandle(grupper.contains(gruppenavnSaksbehandler))
                .setKanVeilede(grupper.contains(gruppenavnVeileder))
                .setKanBeslutte(grupper.contains(gruppenavnBeslutter))
                .setKanBehandleKodeEgenAnsatt(grupper.contains(gruppenavnEgenAnsatt))
                .setKanBehandleKode6(grupper.contains(gruppenavnKode6))
                .setKanBehandleKode7(grupper.contains(gruppenavnKode7))
                .setKanOppgavestyre(grupper.contains(gruppenavnOppgavestyrer))
                .create();
    }

}
