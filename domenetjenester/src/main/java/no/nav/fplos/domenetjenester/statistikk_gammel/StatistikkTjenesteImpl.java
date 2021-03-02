package no.nav.fplos.domenetjenester.statistikk_gammel;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.loslager.repository.StatistikkRepository;
import no.nav.foreldrepenger.loslager.repository.oppgavestatistikk.NyOpppgaveStatistikkRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class StatistikkTjenesteImpl implements StatistikkTjeneste {
    private static final Logger log = LoggerFactory.getLogger(StatistikkTjenesteImpl.class);

    private StatistikkRepository statisikkRepository;
    private NyOpppgaveStatistikkRepository nyOpppgaveStatistikkRepository;

    StatistikkTjenesteImpl() {
        // for CDI proxy
    }

    @Inject
    public StatistikkTjenesteImpl(StatistikkRepository statistikkRepository, NyOpppgaveStatistikkRepository nyOpppgaveStatistikkRepository) {
        statisikkRepository = statistikkRepository;
        this.nyOpppgaveStatistikkRepository = nyOpppgaveStatistikkRepository;
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
    public List<NyeOgFerdigstilteOppgaver> hentNyeOgFerdigstilteOppgaver(Long sakslisteId) {
        var stats = (List<NyeOgFerdigstilteOppgaver>) statisikkRepository.hentNyeOgFerdigstilteOppgaver(sakslisteId).stream() // NOSONAR
                .map(result -> new NyeOgFerdigstilteOppgaver((Object[]) result))
                .collect(Collectors.toList()); // NOSONAR
        log.info("henter statistikk for oppgaveFilterSettId {}", sakslisteId);
        var nyStats = nyOpppgaveStatistikkRepository.hentStatistikk(sakslisteId);
        nyStats.forEach(ks -> log.info("Ny statistikk viser {}", ks));
        return stats;
    }

    @Override
    public List<OppgaverForFørsteStønadsdag> hentOppgaverPerFørsteStønadsdag(String avdeling) {
        return (List<OppgaverForFørsteStønadsdag>) statisikkRepository.hentOppgaverPerFørsteStønadsdag(avdeling).stream() // NOSONAR
                .map(result -> new OppgaverForFørsteStønadsdag((Object[]) result))
                .collect(Collectors.toList()); // NOSONAR
    }
}
