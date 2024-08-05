package no.nav.foreldrepenger.los.tjenester.felles.dto;

public record SaksbehandlerDto(SaksbehandlerBrukerIdentDto brukerIdent, String navn, String epostAdresse, String ansattAvdeling) {

    @Override
    public String toString() {
        return "SaksbehandlerDto{" + "brukerIdent=" + brukerIdent + '}';
    }
}
