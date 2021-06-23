package no.nav.foreldrepenger.los.klient.fpsak;

import static javax.ws.rs.client.Entity.json;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static no.nav.vedtak.util.env.ConfidentialMarkerFilter.CONFIDENTIAL;

import java.net.URI;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.ws.rs.core.GenericType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.konfig.KonfigVerdi;
import no.nav.foreldrepenger.los.klient.fpsak.dto.SøkefeltDto;
import no.nav.foreldrepenger.los.klient.fpsak.dto.fagsak.FagsakDto;
import no.nav.vedtak.felles.integrasjon.rest.jersey.AbstractJerseyOidcRestClient;
import no.nav.vedtak.felles.integrasjon.rest.jersey.Jersey;

@Dependent
@Jersey
public class JerseyForeldrepengerFagsaker extends AbstractJerseyOidcRestClient implements ForeldrepengerFagsaker {

    private static final Logger LOG = LoggerFactory.getLogger(JerseyForeldrepengerFagsaker.class);

    private final URI baseUri;

    @Inject
    public JerseyForeldrepengerFagsaker(@KonfigVerdi(value = "fpsak.url", defaultVerdi = "http://fpsak") URI baseUri) {
        this.baseUri = baseUri;
    }

    @Override
    public List<FagsakDto> finnFagsaker(String søkestreng) {
        LOG.trace(CONFIDENTIAL, "Finner fagsaker for {}", søkestreng);
        var saker = invoke(client.target(baseUri)
                .path(FAGSAK_SØK)
                .request(APPLICATION_JSON_TYPE)
                .buildPost(json(new SøkefeltDto(søkestreng))), new GenericType<List<FagsakDto>>() {
                });
        LOG.info("Fant {} fagsaker", saker.size());
        return saker;
    }

    @Override
    public <T> T get(URI href, Class<T> clazz) {
        var target = client.target(baseUri)
                .path(href.getRawPath());
        QueryUtil.split(href.getQuery())
                .stream()
                .forEach(qp -> target.queryParam(qp.getName(), qp.getValue()));
        LOG.trace(CONFIDENTIAL, "Get for {}", target.getUri());
        var res = invoke(target
                .request(APPLICATION_JSON_TYPE)
                .buildGet(), clazz);
        LOG.info(CONFIDENTIAL, "Get for  {} OK", target.getUri());
        return res;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [baseUri=" + baseUri + "]";
    }
}
