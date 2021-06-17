package no.nav.foreldrepenger.los.klient.fpsak;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.http.client.utils.URIBuilder;

import no.nav.foreldrepenger.los.klient.fpsak.dto.SøkefeltDto;
import no.nav.foreldrepenger.los.klient.fpsak.dto.fagsak.FagsakDto;
import no.nav.vedtak.felles.integrasjon.rest.OidcRestClient;
import no.nav.vedtak.konfig.KonfigVerdi;

@ApplicationScoped
public class ForeldrepengerFagsakKlient implements Fagsaker {

    private static final String FAGSAK_SØK = "/fpsak/api/fagsak/sok";
    private OidcRestClient oidcRestClient;
    private String baseUrl;

    @Inject
    public ForeldrepengerFagsakKlient(OidcRestClient oidcRestClient,
            @KonfigVerdi(value = "fpsak.url", defaultVerdi = "http://fpsak") String baseUrl) {
        this.oidcRestClient = oidcRestClient;
        this.baseUrl = baseUrl;
    }

    public ForeldrepengerFagsakKlient() {
        // CDI
    }

    @Override
    public List<FagsakDto> finnFagsaker(String søkestreng) {
        var uri = URI.create(baseUrl + FAGSAK_SØK);
        var søkefeltDto = new SøkefeltDto(søkestreng);
        var fagsakDtoer = oidcRestClient.post(uri, søkefeltDto, FagsakDto[].class);
        return Arrays.asList(fagsakDtoer);
    }

    @Override
    public <T> T get(URI href, Class<T> cls) {
        try {
            var uriBuilder = new URIBuilder(baseUrl);
            uriBuilder.setPath(href.getPath());
            uriBuilder.setCustomQuery(href.getQuery());
            var uri = uriBuilder.build();
            return oidcRestClient.get(uri, cls);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Konstruksjon av uri til endepunkt som henter " + cls.getSimpleName() + " feiler", e);
        }
    }

}
