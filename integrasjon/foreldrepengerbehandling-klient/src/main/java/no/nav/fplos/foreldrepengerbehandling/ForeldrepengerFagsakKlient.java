package no.nav.fplos.foreldrepengerbehandling;

import no.nav.fplos.foreldrepengerbehandling.dto.SokefeltDto;
import no.nav.fplos.foreldrepengerbehandling.dto.fagsak.AktoerInfoDto;
import no.nav.fplos.foreldrepengerbehandling.dto.fagsak.FagsakDto;
import no.nav.vedtak.felles.integrasjon.rest.OidcRestClient;
import no.nav.vedtak.konfig.KonfigVerdi;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

@ApplicationScoped
public class ForeldrepengerFagsakKlient {

    private static final Logger log = LoggerFactory.getLogger(ForeldrepengerFagsakKlient.class);


    private static final String FAGSAK_SOK = "/fpsak/api/fagsak/sok";
    private static final String AKTOER_INFO = "/fpsak/api/aktoer-info";
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

    public List<FagsakDto> finnFagsaker(String søkestreng) {
        var uri = URI.create(baseUrl + FAGSAK_SOK);
        var sokefeltDto = new SokefeltDto(søkestreng);
        var fagsakDtoer = oidcRestClient.post(uri, sokefeltDto, FagsakDto[].class);
        return Arrays.asList(fagsakDtoer);
    }

    public AktoerInfoDto hentAktoerInfo(URI href) {
        try {
            var uriBuilder = new URIBuilder(baseUrl);
            uriBuilder.setPath(href.getPath());
            uriBuilder.setCustomQuery(href.getQuery());
            URI uri = uriBuilder.build();
            log.info(String.valueOf(uri));
            return oidcRestClient.get(uri, AktoerInfoDto.class);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Oppslag av aktørinformasjon feilet.", e);
        }
    }

    private URI uri(String path) {
        return URI.create(baseUrl + path);
    }
}
