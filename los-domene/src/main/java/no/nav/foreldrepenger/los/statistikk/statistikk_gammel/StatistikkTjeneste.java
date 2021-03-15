package no.nav.foreldrepenger.los.statistikk.statistikk_gammel;

import java.util.List;
import java.util.stream.Collectors;

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
        return statisikkRepository.hentAlleOppgaverForAvdeling(avdeling).stream() // NOSONAR
                .map(resultat -> new OppgaverForAvdeling((Object[]) resultat))
                .collect(Collectors.toList()); // NOSONAR
    }

    public List<OppgaverForAvdelingPerDato> hentAntallOppgaverForAvdelingPerDato(String avdeling) {
        return (List<OppgaverForAvdelingPerDato>) statisikkRepository.hentAlleOppgaverForAvdelingPerDato(avdeling).stream() // NOSONAR
                .map(resultat -> new OppgaverForAvdelingPerDato((Object[]) resultat))
                .collect(Collectors.toList()); // NOSONAR
    }

    public List<OppgaverForAvdelingSattManueltPaaVent> hentAntallOppgaverForAvdelingSattManueltPåVent(String avdeling) {
        return (List<OppgaverForAvdelingSattManueltPaaVent>) statisikkRepository.hentAntallOppgaverForAvdelingSattManueltPåVent(avdeling).stream()
                .map(result -> new OppgaverForAvdelingSattManueltPaaVent((Object[]) result))
                .collect(Collectors.toList());
    }

    public List<OppgaverForFørsteStønadsdag> hentOppgaverPerFørsteStønadsdag(String avdeling) {
        return (List<OppgaverForFørsteStønadsdag>) statisikkRepository.hentOppgaverPerFørsteStønadsdag(avdeling).stream() // NOSONAR
                .map(result -> new OppgaverForFørsteStønadsdag((Object[]) result))
                .collect(Collectors.toList()); // NOSONAR
    }
}
