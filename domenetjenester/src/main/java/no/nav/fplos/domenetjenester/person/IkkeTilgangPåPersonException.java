package no.nav.fplos.domenetjenester.person;

import no.nav.vedtak.felles.integrasjon.pdl.PdlException;

public class IkkeTilgangPåPersonException extends RuntimeException {
    public IkkeTilgangPåPersonException(PdlException cause) {
        super(cause);
    }
}
