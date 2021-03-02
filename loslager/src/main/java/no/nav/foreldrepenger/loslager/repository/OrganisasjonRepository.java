package no.nav.foreldrepenger.loslager.repository;

import java.util.List;
import java.util.Optional;

import no.nav.foreldrepenger.loslager.organisasjon.Avdeling;
import no.nav.foreldrepenger.loslager.organisasjon.Saksbehandler;

public interface OrganisasjonRepository {

    void lagre(Saksbehandler saksbehandler);

    Saksbehandler hentSaksbehandler(String saksbehandlerIdent);

    Optional<Avdeling> hentAvdelingFraEnhet(String avdelingEnhet);

    Optional<Saksbehandler> hentSaksbehandlerHvisEksisterer(String saksbehandlerIdent);

    void refresh(Avdeling avdeling);

    List<Avdeling> hentAvdelinger();

}
