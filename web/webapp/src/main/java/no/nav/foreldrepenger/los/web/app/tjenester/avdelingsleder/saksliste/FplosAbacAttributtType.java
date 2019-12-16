package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste;

import no.nav.vedtak.sikkerhet.abac.AbacAttributtType;

public enum FplosAbacAttributtType implements AbacAttributtType {

    OPPGAVESTYRING_ENHET,
    SAKER_MED_FNR;

    private final String sporingsloggEksternKode;

    FplosAbacAttributtType() {
        sporingsloggEksternKode = "UNDEFINED_" + name();
    }

    @Override
    public boolean getMaskerOutput() {
        return false;
    }

    @Override
    public String getSporingsloggKode() {
        return sporingsloggEksternKode;
    }
}
