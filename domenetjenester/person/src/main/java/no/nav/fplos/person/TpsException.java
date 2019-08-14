package no.nav.fplos.person;

import no.nav.vedtak.exception.IntegrasjonException;
import no.nav.vedtak.feil.Feil;

public class TpsException extends IntegrasjonException {

    public TpsException(Feil feil) {
        super(feil);
    }

}
