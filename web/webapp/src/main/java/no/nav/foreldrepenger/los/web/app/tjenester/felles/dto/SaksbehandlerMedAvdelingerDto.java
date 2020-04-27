package no.nav.foreldrepenger.los.web.app.tjenester.felles.dto;

import java.util.List;

public class SaksbehandlerMedAvdelingerDto {

    private SaksbehandlerDto saksbehandlerDto;
    private List<String> avdelingsnavn;

    SaksbehandlerMedAvdelingerDto(SaksbehandlerDto saksbehandlerDto, List<String> avdelingsnavn) {
        this.saksbehandlerDto = saksbehandlerDto;
        this.avdelingsnavn = avdelingsnavn;
    }

    public String getBrukerIdent() {
        return saksbehandlerDto.getBrukerIdent().getVerdi();
    }

    public String getNavn() {
        return saksbehandlerDto.getNavn();
    }

    public List<String> getAvdelingsnavn() {
        return avdelingsnavn;
    }
}
