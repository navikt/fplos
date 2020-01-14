package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste;

import no.nav.vedtak.sikkerhet.abac.AbacAttributtType;

public enum FplosAbacAttributtType implements AbacAttributtType {

    OPPGAVESTYRING_ENHET,
    SAKER_MED_FNR;

    public static final String SUBJECT_FELLES_ENHETIDLISTE = "no.nav.abac.attributter.subject.felles.enhetidliste";
    public static final String RESOURCE_FORELDREPENGER_SAK_BEHANDLINGSSTATUS = "no.nav.abac.attributter.resource.foreldrepenger.sak.behandlingsstatus";
    public static final String RESOURCE_FORELDREPENGER_SAK_SAKSSTATUS = "no.nav.abac.attributter.resource.foreldrepenger.sak.saksstatus";
    public static final String RESOURCE_FORELDREPENGER_SAK_AKSJONSPUNKT_TYPE = "no.nav.abac.attributter.resource.foreldrepenger.sak.aksjonspunkt_type";
    public static final String RESOURCE_FORELDREPENGER_SAK_ANSVARLIG_SAKSBEHANDLER = "no.nav.abac.attributter.resource.foreldrepenger.sak.ansvarlig_saksbehandler";

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
