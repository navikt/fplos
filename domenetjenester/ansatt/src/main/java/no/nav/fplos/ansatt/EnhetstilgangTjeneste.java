package no.nav.fplos.ansatt;

import no.nav.foreldrepenger.loslager.aktør.OrganisasjonsEnhet;
import no.nav.foreldrepenger.loslager.organisasjon.Saksbehandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class EnhetstilgangTjeneste {

    private static final Logger log = LoggerFactory.getLogger(EnhetstilgangTjeneste.class);
    private EnhetstilgangConnection connection;

    @Inject
    public EnhetstilgangTjeneste(EnhetstilgangConnection connection) {
        this.connection = connection;
    }

    public EnhetstilgangTjeneste() {
        // CDI
    }

    public List<OrganisasjonsEnhet> hentEnhetstilganger(String ident) {
        if (!connection.isEnabled()) {
            log.info("EnhetstilgangTjeneste deaktivert, returnerer tom liste.");
            return Collections.emptyList();
        }
        return connection.hentEnhetstilganger(ident)
                .map(EnhetstilgangResponse::getEnheter)
                .orElse(Collections.emptyList())
                .stream()
                .peek(this::loggfiltrerteEnheter)
                .filter(OrganisasjonsEnhet::kanBehandleForeldrepenger)
                .collect(Collectors.toList());
    }

    private void loggfiltrerteEnheter(OrganisasjonsEnhet organisasjonsEnhet) {
        // greit å logge mens vi sammenlikner med norgtjenesten
        if (!organisasjonsEnhet.kanBehandleForeldrepenger()) {
            log.info("Fjerner enhet uten fagområde FOR");
        }
    }

}

