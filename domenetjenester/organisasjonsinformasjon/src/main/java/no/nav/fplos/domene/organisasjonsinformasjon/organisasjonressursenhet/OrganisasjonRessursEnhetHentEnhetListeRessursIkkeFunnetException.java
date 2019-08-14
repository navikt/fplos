package no.nav.fplos.domene.organisasjonsinformasjon.organisasjonressursenhet;

import no.nav.vedtak.exception.IntegrasjonException;
import no.nav.vedtak.feil.Feil;

public class OrganisasjonRessursEnhetHentEnhetListeRessursIkkeFunnetException extends IntegrasjonException {
    public OrganisasjonRessursEnhetHentEnhetListeRessursIkkeFunnetException(Feil feil) {
        super(feil);
    }
}
