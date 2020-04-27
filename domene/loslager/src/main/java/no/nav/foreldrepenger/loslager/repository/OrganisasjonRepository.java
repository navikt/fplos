package no.nav.foreldrepenger.loslager.repository;

import java.util.List;
import java.util.Optional;

import no.nav.foreldrepenger.loslager.organisasjon.Avdeling;
import no.nav.foreldrepenger.loslager.organisasjon.Saksbehandler;

public interface OrganisasjonRepository {

    List<Saksbehandler> hentAvdelingensSaksbehandlere(String avdelingEnhet);

    void lagre(Saksbehandler saksbehandler);

    Saksbehandler hentSaksbehandler(String saksbehandlerIdent);

    Avdeling hentAvdelingFraEnhet(String avdelingEnhet);

    Optional<Saksbehandler> hentMuligSaksbehandler(String saksbehandlerIdent);

    void lagre(Avdeling avdeling);

    void refresh(Avdeling avdeling);

    List<Avdeling> hentAvdelinger();

    List<Saksbehandler> hentAlleSaksbehandlere();

}
