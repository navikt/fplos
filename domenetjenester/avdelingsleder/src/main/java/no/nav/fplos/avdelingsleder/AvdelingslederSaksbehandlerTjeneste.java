package no.nav.fplos.avdelingsleder;

import java.util.List;

import no.nav.foreldrepenger.loslager.organisasjon.Saksbehandler;

public interface AvdelingslederSaksbehandlerTjeneste {

    List<Saksbehandler> hentAvdelingensSaksbehandlere(String avdelingEnhet);

    void leggTilSaksbehandler(String verdi, String avdelingEnhet);

    void slettSaksbehandler(String verdi, String avdelingEnhet);
}
