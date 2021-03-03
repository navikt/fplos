package no.nav.foreldrepenger.los.admin.driftsmelding;

import java.util.List;

public interface DriftsmeldingTjeneste {

    void opprettDriftsmelding(Driftsmelding driftsmelding);

    List<Driftsmelding> hentAlleDriftsmeldinger();

    List<Driftsmelding> hentAktiveDriftsmeldinger();

    void deaktiverDriftsmeldinger();

}
