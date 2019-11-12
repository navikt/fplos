package no.nav.foreldrepenger.loslager.repository;

import no.nav.foreldrepenger.loslager.oppgave.EksternIdentifikator;

import java.util.Optional;

public interface EksternIdentifikatorRepository {

    EksternIdentifikator finnEllerOpprettEksternId(String fagsystem, String eksternRefId);

    Optional<EksternIdentifikator> finnIdentifikator(String fagsystem, String eksternRefId);

    EksternIdentifikator lagre(EksternIdentifikator eksternIdentifikator);
}
