package no.nav.fplos.domenetjenester.admin;

import no.nav.foreldrepenger.loslager.admin.Driftsmelding;

import java.util.List;

public interface DriftsmeldingTjeneste {

    void opprettDriftsmelding(Driftsmelding driftsmelding);

    List<Driftsmelding> hentAlleDriftsmeldinger();

    List<Driftsmelding> hentAktiveDriftsmeldinger();

    void deaktiverDriftsmeldinger();

}
