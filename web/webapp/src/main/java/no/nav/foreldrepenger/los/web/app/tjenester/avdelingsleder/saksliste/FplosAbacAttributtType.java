package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste;

import no.nav.vedtak.sikkerhet.abac.AbacAttributtType;

public enum FplosAbacAttributtType implements AbacAttributtType {

    OPPGAVESTYRING_ENHET,
    OPPGAVE_ID;

    public static final String SUBJECT_FELLES_ENHETIDLISTE = "no.nav.abac.attributter.subject.felles.enhetidliste";
    public static final String RESOURCE_FORELDREPENGER_SAK_BEHANDLINGSSTATUS = "no.nav.abac.attributter.resource.foreldrepenger.sak.behandlingsstatus";
    public static final String RESOURCE_FORELDREPENGER_SAK_SAKSSTATUS = "no.nav.abac.attributter.resource.foreldrepenger.sak.saksstatus";

    private final String sporingsloggEksternKode;

    FplosAbacAttributtType() {
        sporingsloggEksternKode = "UNDEFINED_" + name();
    }

    @Override
    public boolean getMaskerOutput() {
        return false;
    }

}
