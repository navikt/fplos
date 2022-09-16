package no.nav.foreldrepenger.los.klient.fpsak;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.los.klient.fpsak.dto.SøkefeltDto;
import no.nav.foreldrepenger.los.klient.fpsak.dto.fagsak.FagsakDto;
import no.nav.vedtak.felles.integrasjon.rest.NativeClient;
import no.nav.vedtak.felles.integrasjon.rest.RestClientConfig;
import no.nav.vedtak.felles.integrasjon.rest.RestCompact;
import no.nav.vedtak.felles.integrasjon.rest.RestConfig;

@Dependent
@NativeClient
@RestClientConfig(endpointProperty = "fpsak.url", endpointDefault = "http://fpsak") // TODO - ønsker bruke appliaction = men ressurslenkene begynner med /<fpapp>/api/...
public class NativeForeldrepengerFagsaker implements ForeldrepengerFagsaker {

    private static final Logger LOG = LoggerFactory.getLogger(NativeForeldrepengerFagsaker.class);

    private final RestCompact klient;
    private final URI baseUri;
    private final URI søkURI;

    @Inject
    public NativeForeldrepengerFagsaker(RestCompact klient) {
        this.klient = klient;
        this.baseUri = RestConfig.endpointFromAnnotation(NativeForeldrepengerFagsaker.class);
        this.søkURI = URI.create(baseUri + ForeldrepengerFagsaker.FAGSAK_SØK);
    }

    @Override
    public List<FagsakDto> finnFagsaker(String søkestreng) {
        var respons = klient.postValue(NativeForeldrepengerFagsaker.class, søkURI, new SøkefeltDto(søkestreng), FagsakDto[].class);
        return Arrays.asList(respons);
    }

    @Override
    public <T> T get(URI href, Class<T> clazz) {
        var target = UriBuilder.fromUri(baseUri).path(href.getRawPath());
        target = QueryUtil.addQueryParams(href, target);
        return klient.getValue(NativeForeldrepengerFagsaker.class, target.build(), clazz);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [baseUri=" + baseUri + "]";
    }
}
