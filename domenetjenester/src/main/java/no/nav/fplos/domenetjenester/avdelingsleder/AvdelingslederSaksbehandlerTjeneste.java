package no.nav.fplos.domenetjenester.avdelingsleder;

import java.util.List;

import no.nav.foreldrepenger.loslager.organisasjon.Saksbehandler;

public interface AvdelingslederSaksbehandlerTjeneste {

    List<Saksbehandler> hentAvdelingensSaksbehandlere(String avdelingEnhet);

    void leggSaksbehandlerTilAvdeling(String verdi, String avdelingEnhet);

    void fjernSaksbehandlerFraAvdeling(String verdi, String avdelingEnhet);
}
