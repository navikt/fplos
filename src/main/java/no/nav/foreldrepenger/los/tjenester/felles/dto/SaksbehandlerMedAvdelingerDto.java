package no.nav.foreldrepenger.los.tjenester.felles.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class SaksbehandlerMedAvdelingerDto {

    private SaksbehandlerDto saksbehandlerDto;
    private List<String> avdelingsnavn;

    public SaksbehandlerMedAvdelingerDto(SaksbehandlerDto saksbehandlerDto, List<String> avdelingsnavn) {
        this.saksbehandlerDto = saksbehandlerDto;
        this.avdelingsnavn = avdelingsnavn;
    }

    @JsonProperty("brukerIdent")
    public String getBrukerIdent() {
        return saksbehandlerDto.brukerIdent().getVerdi();
    }

    @JsonProperty("navn")
    public String getNavn() {
        return saksbehandlerDto.navn();
    }

    @JsonProperty("avdelingsnavn")
    public List<String> getAvdelingsnavn() {
        return avdelingsnavn;
    }

    @JsonProperty("ansattAvdeling")
    public String getAnsattAvdeling() {
        return saksbehandlerDto.ansattAvdeling();
    }
}
