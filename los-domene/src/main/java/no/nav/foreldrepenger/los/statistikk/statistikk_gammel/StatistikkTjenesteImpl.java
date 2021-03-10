package no.nav.foreldrepenger.los.statistikk.statistikk_gammel;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class StatistikkTjenesteImpl implements StatistikkTjeneste {

    private StatistikkRepository statisikkRepository;

    StatistikkTjenesteImpl() {
        // for CDI proxy
    }

    @Inject
    public StatistikkTjenesteImpl(StatistikkRepository statistikkRepository) {
        statisikkRepository = statistikkRepository;
    }

    @Override
    public List<OppgaverForAvdeling> hentAlleOppgaverForAvdeling(String avdeling) {
        return (List<OppgaverForAvdeling>) statisikkRepository.hentAlleOppgaverForAvdeling(avdeling).stream() // NOSONAR
                .map(resultat -> new OppgaverForAvdeling((Object[]) resultat))
                .collect(Collectors.toList()); // NOSONAR
    }

    @Override
    public List<OppgaverForAvdelingPerDato> hentAntallOppgaverForAvdelingPerDato(String avdeling) {
        return (List<OppgaverForAvdelingPerDato>) statisikkRepository.hentAlleOppgaverForAvdelingPerDato(avdeling).stream() // NOSONAR
                .map(resultat -> new OppgaverForAvdelingPerDato((Object[]) resultat))
                .collect(Collectors.toList()); // NOSONAR
    }

    @Override
    public List<OppgaverForAvdelingSattManueltPaaVent> hentAntallOppgaverForAvdelingSattManueltPåVent(String avdeling) {
        var stats = (List<OppgaverForAvdelingSattManueltPaaVent>) statisikkRepository.hentAntallOppgaverForAvdelingSattManueltPåVent(avdeling).stream()
                .map(result -> new OppgaverForAvdelingSattManueltPaaVent((Object[]) result))
                .collect(Collectors.toList());
        return stats;
    }

    @Override
    public List<OppgaverForFørsteStønadsdag> hentOppgaverPerFørsteStønadsdag(String avdeling) {
        return (List<OppgaverForFørsteStønadsdag>) statisikkRepository.hentOppgaverPerFørsteStønadsdag(avdeling).stream() // NOSONAR
                .map(result -> new OppgaverForFørsteStønadsdag((Object[]) result))
                .collect(Collectors.toList()); // NOSONAR
    }
}
