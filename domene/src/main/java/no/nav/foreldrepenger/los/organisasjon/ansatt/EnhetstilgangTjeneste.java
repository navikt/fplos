package no.nav.foreldrepenger.los.organisasjon.ansatt;

import no.nav.foreldrepenger.los.domene.typer.aktør.OrganisasjonsEnhet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class EnhetstilgangTjeneste {

    private static final Logger LOG = LoggerFactory.getLogger(EnhetstilgangTjeneste.class);
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
            LOG.info("EnhetstilgangTjeneste deaktivert, returnerer tom liste.");
            return Collections.emptyList();
        }
        return connection.hentEnhetstilganger(ident)
            .map(EnhetstilgangResponse::getEnheter)
            .orElse(Collections.emptyList())
            .stream()
            .filter(OrganisasjonsEnhet::kanBehandleForeldrepenger)
            .toList();
    }

}
