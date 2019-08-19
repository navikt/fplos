package no.nav.fplos.avdelingsleder;

import java.util.List;
import java.util.Optional;

import no.nav.foreldrepenger.loslager.aktør.OrganisasjonsEnhet;
import no.nav.foreldrepenger.loslager.organisasjon.Saksbehandler;

public interface AvdelingslederSaksbehandlerTjeneste {
    List<Saksbehandler> hentAvdelingensSaksbehandlere(String avdelingEnhet);

    void leggTilSaksbehandler(String verdi, String avdelingEnhet);

    void slettSaksbehandler(String verdi, String avdelingEnhet);

    Optional<String> hentSaksbehandlerNavn(String saksbehandlerIdent);

    List<OrganisasjonsEnhet> hentSaksbehandlersAvdelinger(String saksbehandlerIdent);
}
