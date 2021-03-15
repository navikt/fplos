package no.nav.foreldrepenger.los.admin.driftsmelding;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class DriftsmeldingTjeneste {

    private DriftsmeldingRepository driftsmeldingRepository;

    @Inject
    public DriftsmeldingTjeneste(DriftsmeldingRepository driftsmeldingRepository) {
        this.driftsmeldingRepository = driftsmeldingRepository;
    }

    public DriftsmeldingTjeneste() {
    }

    public void opprettDriftsmelding(Driftsmelding driftsmelding) {
        driftsmeldingRepository.lagre(driftsmelding);
    }

    public List<Driftsmelding> hentAlleDriftsmeldinger() {
        return driftsmeldingRepository.hentMeldinger();
    }

    public List<Driftsmelding> hentAktiveDriftsmeldinger() {
        return hentAlleDriftsmeldinger().stream()
                .filter(Driftsmelding::erAktiv)
                .collect(Collectors.toList());
    }

    public void deaktiverDriftsmeldinger() {
        driftsmeldingRepository.deaktiverDriftsmeldinger();

    }
}
