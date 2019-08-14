package no.nav.foreldrepenger.los.web.app.tjenester.felles.dto;

import java.util.List;

public class SaksbehandlerDto {

    //FIXME Bruk PersonIdent i staden for SaksbehandlerBrukerIdentDto?
    private SaksbehandlerBrukerIdentDto brukerIdent;
    private String navn;
    private List<String> avdelingsnavn;

    public SaksbehandlerDto() {
    }

    public SaksbehandlerDto(SaksbehandlerBrukerIdentDto brukerIdent, String navn, List<String> avdelingsnavn) {
        this.brukerIdent = brukerIdent;
        this.navn = navn;
        this.avdelingsnavn = avdelingsnavn;
    }

    public String getBrukerIdent() {
        return brukerIdent.getVerdi();
    }

    public String getNavn() {
        return navn;
    }

    public List<String> getAvdelingsnavn() {
        return avdelingsnavn;
    }
}
