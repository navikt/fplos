package no.nav.fplos.foreldrepengerbehandling.dto;

public enum UttakPeriodeVurderingType {
    PERIODE_OK, PERIODE_OK_ENDRET, PERIODE_KAN_IKKE_AVKLARES, PERIODE_IKKE_VURDERT;

    boolean erOmsøktOgIkkeAvklart() {
        return this.equals(PERIODE_IKKE_VURDERT);
    }
}
