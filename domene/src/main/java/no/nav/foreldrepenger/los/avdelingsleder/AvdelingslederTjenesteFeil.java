package no.nav.foreldrepenger.los.avdelingsleder;

import no.nav.vedtak.exception.TekniskException;


public final class AvdelingslederTjenesteFeil {

    private AvdelingslederTjenesteFeil() {
    }

    public static TekniskException fantIkkeOppgavekø(Long oppgaveFilterId) {
        var feilmelding = String.format("Fant ikke oppgavekø med id %s", oppgaveFilterId);
        return new TekniskException("FPLOS-AVD1", feilmelding);
    }

}
