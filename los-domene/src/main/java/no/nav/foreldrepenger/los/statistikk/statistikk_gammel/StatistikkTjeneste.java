package no.nav.foreldrepenger.los.statistikk.statistikk_gammel;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class StatistikkTjeneste {

    private StatistikkRepository statisikkRepository;

    StatistikkTjeneste() {
        // for CDI proxy
    }

    @Inject
    public StatistikkTjeneste(StatistikkRepository statistikkRepository) {
        statisikkRepository = statistikkRepository;
    }

    public List<OppgaverForAvdeling> hentAlleOppgaverForAvdeling(String avdeling) {
        return statisikkRepository.hentAlleOppgaverForAvdeling(avdeling);
    }

    public List<OppgaverForAvdelingPerDato> hentAntallOppgaverForAvdelingPerDato(String avdeling) {
        return statisikkRepository.hentAlleOppgaverForAvdelingPerDato(avdeling);
    }

    public List<OppgaverForAvdelingSattManueltPåVent> hentAntallOppgaverForAvdelingSattManueltPåVent(String avdeling) {
        return statisikkRepository.hentAntallOppgaverForAvdelingSattManueltPåVent(avdeling);
    }

    public List<OppgaverForFørsteStønadsdag> hentOppgaverPerFørsteStønadsdag(String avdeling) {
        return statisikkRepository.hentOppgaverPerFørsteStønadsdag(avdeling);
    }
}
