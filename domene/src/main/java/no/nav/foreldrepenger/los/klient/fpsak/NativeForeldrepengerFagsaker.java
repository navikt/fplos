package no.nav.foreldrepenger.los.klient.fpsak;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.los.klient.fpsak.dto.SøkefeltDto;
import no.nav.foreldrepenger.los.klient.fpsak.dto.fagsak.FagsakDto;
import no.nav.vedtak.felles.integrasjon.rest.FpApplication;
import no.nav.vedtak.felles.integrasjon.rest.NativeClient;
import no.nav.vedtak.felles.integrasjon.rest.RestClient;
import no.nav.vedtak.felles.integrasjon.rest.RestClientConfig;
import no.nav.vedtak.felles.integrasjon.rest.RestConfig;
import no.nav.vedtak.felles.integrasjon.rest.RestRequest;
import no.nav.vedtak.felles.integrasjon.rest.TokenFlow;

@ApplicationScoped
@NativeClient
@RestClientConfig(tokenConfig = TokenFlow.CONTEXT, application = FpApplication.FPSAK)
public class NativeForeldrepengerFagsaker implements ForeldrepengerFagsaker {

    private static final Logger LOG = LoggerFactory.getLogger(NativeForeldrepengerFagsaker.class);

    private RestClient klient;
    private URI baseUri;
    private URI søkURI;

    NativeForeldrepengerFagsaker() {
    }

    @Inject
    public NativeForeldrepengerFagsaker(RestClient klient) {
        this.klient = klient;
        this.baseUri = RestConfig.contextPathFromAnnotation(NativeForeldrepengerFagsaker.class);
        this.søkURI = URI.create(baseUri + "/api/fagsak/sok");
    }

    @Override
    public List<FagsakDto> finnFagsaker(String søkestreng) {
        var request = RestRequest.newPOSTJson(new SøkefeltDto(søkestreng), søkURI, NativeForeldrepengerFagsaker.class);
        var respons = klient.send(request, FagsakDto[].class);
        return Arrays.asList(respons);
    }

    @Override
    public <T> T get(URI href, Class<T> clazz) {
        var linkpath = href.toString();
        var path = linkpath.startsWith("/fpsak") ?  linkpath.replaceFirst("/fpsak", "") : linkpath;
        var target = URI.create(baseUri + path);
        return klient.send(RestRequest.newRequest(RestRequest.Method.get(), target, NativeForeldrepengerFagsaker.class), clazz);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [baseUri=" + baseUri + "]";
    }
}
