package no.nav.foreldrepenger.los.admin.driftsmelding;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class DriftsmeldingTjenesteImpl implements DriftsmeldingTjeneste {

    private DriftsmeldingRepository driftsmeldingRepository;

    @Inject
    public DriftsmeldingTjenesteImpl(DriftsmeldingRepository driftsmeldingRepository) {
        this.driftsmeldingRepository = driftsmeldingRepository;
    }

    public DriftsmeldingTjenesteImpl() {
    }

    @Override
    public void opprettDriftsmelding(Driftsmelding driftsmelding) {
        driftsmeldingRepository.lagre(driftsmelding);
    }

    @Override
    public List<Driftsmelding> hentAlleDriftsmeldinger() {
        return driftsmeldingRepository.hentMeldinger();
    }

    @Override
    public List<Driftsmelding> hentAktiveDriftsmeldinger() {
        return hentAlleDriftsmeldinger().stream()
                .filter(Driftsmelding::erAktiv)
                .collect(Collectors.toList());
    }

    @Override
    public void deaktiverDriftsmeldinger() {
        driftsmeldingRepository.deaktiverDriftsmeldinger();

    }
}
