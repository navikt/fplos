package no.nav.foreldrepenger.los.statistikk.statistikk_gammel;

import java.util.List;

public interface StatistikkTjeneste {
    List<OppgaverForAvdeling> hentAlleOppgaverForAvdeling(String avdeling);

    List<OppgaverForAvdelingPerDato> hentAntallOppgaverForAvdelingPerDato(String avdeling);

    List<OppgaverForAvdelingSattManueltPaaVent> hentAntallOppgaverForAvdelingSattManueltPåVent(String aveling);

    List<OppgaverForFørsteStønadsdag> hentOppgaverPerFørsteStønadsdag(String avdeling);
}
