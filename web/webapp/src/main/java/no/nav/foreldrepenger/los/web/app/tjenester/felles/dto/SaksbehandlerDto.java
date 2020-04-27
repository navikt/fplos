package no.nav.foreldrepenger.los.web.app.tjenester.felles.dto;

public class SaksbehandlerDto {

    private SaksbehandlerBrukerIdentDto brukerIdent;
    private String navn;

    SaksbehandlerDto(SaksbehandlerBrukerIdentDto brukerIdent, String navn) {
        this.brukerIdent = brukerIdent;
        this.navn = navn;
    }

    public SaksbehandlerBrukerIdentDto getBrukerIdent() {
        return brukerIdent;
    }

    public String getNavn() {
        return navn;
    }
}
