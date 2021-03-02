package no.nav.fplos.domenetjenester.statistikk_gammel;

import java.util.List;

public interface StatistikkTjeneste {
    List<OppgaverForAvdeling> hentAlleOppgaverForAvdeling(String avdeling);

    List<OppgaverForAvdelingPerDato> hentAntallOppgaverForAvdelingPerDato(String avdeling);

    List<OppgaverForAvdelingSattManueltPaaVent> hentAntallOppgaverForAvdelingSattManueltPåVent(String aveling);

    List<NyeOgFerdigstilteOppgaver> hentNyeOgFerdigstilteOppgaver(Long sakslisteId);

    List<OppgaverForFørsteStønadsdag> hentOppgaverPerFørsteStønadsdag(String avdeling);
}
