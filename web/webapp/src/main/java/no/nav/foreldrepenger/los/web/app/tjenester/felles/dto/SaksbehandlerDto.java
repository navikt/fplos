package no.nav.foreldrepenger.los.web.app.tjenester.felles.dto;

public record SaksbehandlerDto(SaksbehandlerBrukerIdentDto brukerIdent, String navn) {

    @Override
    public String toString() {
        return "SaksbehandlerDto{" + "brukerIdent=" + brukerIdent + '}';
    }
}
