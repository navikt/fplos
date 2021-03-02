package no.nav.foreldrepenger.loslager.repository;

import java.util.List;

public interface StatistikkRepository {
    List hentAlleOppgaverForAvdeling(String avdeling);

    List hentAlleOppgaverForAvdelingPerDato(String avdeling);

    List hentAntallOppgaverForAvdelingSattManueltPåVent(String avdeling);

    List hentNyeOgFerdigstilteOppgaver(Long sakslisteId);

    List hentOppgaverPerFørsteStønadsdag(String avdeling);

}
