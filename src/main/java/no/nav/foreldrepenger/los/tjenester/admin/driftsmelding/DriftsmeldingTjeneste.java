package no.nav.foreldrepenger.los.tjenester.admin.driftsmelding;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class DriftsmeldingTjeneste {

    private DriftsmeldingRepository driftsmeldingRepository;

    @Inject
    public DriftsmeldingTjeneste(DriftsmeldingRepository driftsmeldingRepository) {
        this.driftsmeldingRepository = driftsmeldingRepository;
    }

    DriftsmeldingTjeneste() {
        //CDI
    }

    public void opprettDriftsmelding(Driftsmelding driftsmelding) {
        driftsmeldingRepository.lagre(driftsmelding);
    }

    public List<Driftsmelding> hentAlleDriftsmeldinger() {
        return driftsmeldingRepository.hentMeldinger();
    }

    public List<Driftsmelding> hentAktiveDriftsmeldinger() {
        return hentAlleDriftsmeldinger().stream().filter(Driftsmelding::erAktiv).toList();
    }

    public void deaktiverDriftsmeldinger() {
        driftsmeldingRepository.deaktiverDriftsmeldinger();

    }
}
