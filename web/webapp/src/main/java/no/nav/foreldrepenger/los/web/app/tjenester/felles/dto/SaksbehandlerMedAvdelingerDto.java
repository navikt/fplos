package no.nav.foreldrepenger.los.web.app.tjenester.felles.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SaksbehandlerMedAvdelingerDto {

    private SaksbehandlerDto saksbehandlerDto;
    private List<String> avdelingsnavn;

    SaksbehandlerMedAvdelingerDto(SaksbehandlerDto saksbehandlerDto, List<String> avdelingsnavn) {
        this.saksbehandlerDto = saksbehandlerDto;
        this.avdelingsnavn = avdelingsnavn;
    }

    @JsonProperty("brukerIdent")
    public String getBrukerIdent() {
        return saksbehandlerDto.getBrukerIdent().getVerdi();
    }

    @JsonProperty("navn")
    public String getNavn() {
        return saksbehandlerDto.getNavn();
    }

    public List<String> getAvdelingsnavn() {
        return avdelingsnavn;
    }
}
