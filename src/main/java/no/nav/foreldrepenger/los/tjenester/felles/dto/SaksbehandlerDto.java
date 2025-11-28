package no.nav.foreldrepenger.los.tjenester.felles.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SaksbehandlerDto(SaksbehandlerBrukerIdentDto brukerIdent,
                               String navn,
                               String ansattAvdeling) {

    @Override
    public String toString() {
        return "SaksbehandlerDto{" + "brukerIdent=" + brukerIdent + '}';
    }

    @JsonProperty("brukerIdent")
    public String getBrukerIdent() {
        return brukerIdent.getVerdi();
    }

    public static SaksbehandlerDto ukjentSaksbehandler(String ident) {
        var identDto = new SaksbehandlerBrukerIdentDto(ident);
        return new SaksbehandlerDto(identDto, "Ukjent saksbehandler " + ident, null);
    }
}
