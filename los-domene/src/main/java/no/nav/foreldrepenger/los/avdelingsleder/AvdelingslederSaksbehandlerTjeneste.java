package no.nav.foreldrepenger.los.avdelingsleder;

import java.util.List;

import no.nav.foreldrepenger.los.organisasjon.Saksbehandler;

public interface AvdelingslederSaksbehandlerTjeneste {

    List<Saksbehandler> hentAvdelingensSaksbehandlere(String avdelingEnhet);

    void leggSaksbehandlerTilAvdeling(String verdi, String avdelingEnhet);

    void fjernSaksbehandlerFraAvdeling(String verdi, String avdelingEnhet);
}
