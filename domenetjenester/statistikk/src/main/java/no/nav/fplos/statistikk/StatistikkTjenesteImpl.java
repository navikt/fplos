package no.nav.fplos.statistikk;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.loslager.repository.OppgaveRepositoryProvider;
import no.nav.foreldrepenger.loslager.repository.StatistikkRepository;
import no.nav.fplos.kodeverk.KodeverkRepository;

@ApplicationScoped
public class StatistikkTjenesteImpl implements StatistikkTjeneste {

    private KodeverkRepository kodeverkRepository;
    private StatistikkRepository statisikkRepository;

    StatistikkTjenesteImpl() {
        // for CDI proxy
    }

    @Inject
    public StatistikkTjenesteImpl(OppgaveRepositoryProvider oppgaveRepositoryProvider) {
        statisikkRepository = oppgaveRepositoryProvider.getStatisikkRepository();
        kodeverkRepository = oppgaveRepositoryProvider.getKodeverkRepository();
    }

    @Override
    public List<OppgaverForAvdeling>  hentAlleOppgaverForAvdeling(String avdeling) {
        return (List<OppgaverForAvdeling>) statisikkRepository.hentAlleOppgaverForAvdeling(avdeling).stream() // NOSONAR
                .map(resultat -> new OppgaverForAvdeling((Object[])resultat, kodeverkRepository))
                .collect(Collectors.toList()); // NOSONAR
    }

    @Override
    public List<OppgaverForAvdelingPerDato>  hentAntallOppgaverForAvdelingPerDato(String avdeling) {
        return (List<OppgaverForAvdelingPerDato>) statisikkRepository.hentAlleOppgaverForAvdelingPerDato(avdeling).stream() // NOSONAR
                .map(resultat -> new OppgaverForAvdelingPerDato((Object[])resultat, kodeverkRepository))
                .collect(Collectors.toList()); // NOSONAR
    }

    @Override
    public List<OppgaverForAvdelingSattManueltPaaVent> hentAntallOppgaverForAvdelingSattManueltPåVent(String avdeling) {
        return (List<OppgaverForAvdelingSattManueltPaaVent>) statisikkRepository.hentAntallOppgaverForAvdelingSattManueltPåVent(avdeling).stream()
                .map(result -> new OppgaverForAvdelingSattManueltPaaVent((Object[]) result))
                .collect(Collectors.toList());
    }

    @Override
    public List<NyeOgFerdigstilteOppgaver> hentNyeOgFerdigstilteOppgaver(Long sakslisteId) {
        return (List<NyeOgFerdigstilteOppgaver>)statisikkRepository.hentNyeOgFerdigstilteOppgaver(sakslisteId).stream() // NOSONAR
                .map(result -> new NyeOgFerdigstilteOppgaver((Object[])result, kodeverkRepository))
                .collect(Collectors.toList()); // NOSONAR
    }

    @Override
    public List<OppgaverForFørsteStønadsdag> hentOppgaverPerFørsteStønadsdag(String avdeling) {
        return (List<OppgaverForFørsteStønadsdag>)statisikkRepository.hentOppgaverPerFørsteStønadsdag(avdeling).stream() // NOSONAR
                .map(result -> new OppgaverForFørsteStønadsdag((Object[])result))
                .collect(Collectors.toList()); // NOSONAR
    }
}
