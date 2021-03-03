package no.nav.foreldrepenger.los.organisasjon;

import java.util.List;
import java.util.Optional;


public interface OrganisasjonRepository {

    void lagre(Saksbehandler saksbehandler);

    Saksbehandler hentSaksbehandler(String saksbehandlerIdent);

    Optional<Avdeling> hentAvdelingFraEnhet(String avdelingEnhet);

    Optional<Saksbehandler> hentSaksbehandlerHvisEksisterer(String saksbehandlerIdent);

    void refresh(Avdeling avdeling);

    List<Avdeling> hentAvdelinger();

}
