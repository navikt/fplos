package no.nav.foreldrepenger.los.persontjeneste;


import no.nav.vedtak.felles.integrasjon.person.PdlException;

public class IkkeTilgangPåPersonException extends RuntimeException {
    public IkkeTilgangPåPersonException(PdlException cause) {
        super(cause);
    }
}
