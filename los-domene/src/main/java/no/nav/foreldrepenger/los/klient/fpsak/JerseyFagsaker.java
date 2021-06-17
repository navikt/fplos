package no.nav.foreldrepenger.los.klient.fpsak;

import java.net.URI;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import no.nav.foreldrepenger.konfig.KonfigVerdi;
import no.nav.foreldrepenger.los.klient.fpsak.dto.fagsak.FagsakDto;
import no.nav.vedtak.felles.integrasjon.rest.jersey.AbstractJerseyOidcRestClient;
import no.nav.vedtak.felles.integrasjon.rest.jersey.Jersey;

@Dependent
@Jersey
public class JerseyFagsaker extends AbstractJerseyOidcRestClient implements Fagsaker {

    private URI baseUri;

    @Inject
    public JerseyFagsaker(@KonfigVerdi(value = "fpsak.url", defaultVerdi = "http://fpsak") URI baseUrl) {
        this.baseUri = baseUri;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [baseUri=" + baseUri + "]";
    }

    @Override
    public List<FagsakDto> finnFagsaker(String søkestreng) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> T get(URI href, Class<T> cls) {
        return null;
        /*
         * client.target(baseUri).path(href.getPath().toString()). try { var uriBuilder
         * = new URIBuilder(href.getPath().toString());
         * uriBuilder.setPath(href.getPath());
         * uriBuilder.setCustomQuery(href.getQuery()); var uri = uriBuilder.build();
         * return oidcRestClient.get(uri, cls); } catch (URISyntaxException e) { throw
         * new IllegalArgumentException("Konstruksjon av uri til endepunkt som henter "
         * + cls.getSimpleName() + " feiler", e); }
         */
    }

}
