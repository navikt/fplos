package no.nav.foreldrepenger.los.tjenester.avdelingsleder.saksliste;

import no.nav.vedtak.sikkerhet.abac.AbacAttributtType;

public enum FplosAbacAttributtType implements AbacAttributtType {

    OPPGAVESTYRING_ENHET,
    OPPGAVE_ID;

    @Override
    public boolean getMaskerOutput() {
        return false;
    }

}
