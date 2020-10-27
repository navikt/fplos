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
            var builder = new URIBuilder(baseUrl);
            builder.setPath(href.getPath());
            builder.setCustomQuery(oe(href.getQuery()));
            URI uri = builder.build();
            log.info(String.valueOf(uri));
            return oidcRestClient.get(uri, AktoerInfoDto.class);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Feil i bygging av URI", e);
        }
    }

    private String oe(String query) {
        // kompenserer for feil i aktoer-info lenke fra fpsak
        return query.replace("ø", "oe");
    }

}
