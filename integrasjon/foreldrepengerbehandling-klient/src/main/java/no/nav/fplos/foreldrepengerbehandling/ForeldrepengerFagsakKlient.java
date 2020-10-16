package no.nav.fplos.foreldrepengerbehandling;

import no.nav.foreldrepenger.loslager.aktør.Fødselsnummer;
import no.nav.fplos.foreldrepengerbehandling.dto.SokefeltDto;
import no.nav.fplos.foreldrepengerbehandling.dto.fagsak.FagsakDto;
import no.nav.vedtak.felles.integrasjon.rest.OidcRestClient;
import no.nav.vedtak.konfig.KonfigVerdi;
import org.apache.http.client.utils.URIBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class ForeldrepengerFagsakKlient {

    private static final String FPSAK_FAGSAK_FNR = "/fpsak/api/fagsak/sok";
    private static final String FPSAK_FAGSAK_SAKSNUMMER = "/fpsak/api/fagsak";
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

    public List<FagsakDto> getFagsakFraSaksnummer(String saksnummer) {
        URIBuilder uriBuilder = new URIBuilder(URI.create(baseUrl + FPSAK_FAGSAK_SAKSNUMMER));
        uriBuilder.setParameter("saksnummer", saksnummer);
        try {
            FagsakDto fagsakDtos = oidcRestClient.get(uriBuilder.build(), FagsakDto.class);
            return Collections.singletonList(fagsakDtos);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Ugyldig uri for" + FPSAK_FAGSAK_SAKSNUMMER, e);
        }
    }

    public List<FagsakDto> getFagsakFraFnr(Fødselsnummer fødselsnummer) {
        var uri = URI.create(baseUrl + FPSAK_FAGSAK_FNR);
        var sokefeltDto = new SokefeltDto(fødselsnummer.asValue());
        var fagsakDtoer = oidcRestClient.post(uri, sokefeltDto, FagsakDto[].class);
        return Arrays.asList(fagsakDtoer);
    }
}
