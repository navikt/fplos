package no.nav.foreldrepenger.los.klient.fpsak;

import static no.nav.foreldrepenger.los.klient.fpsak.dto.behandling.ResourceLink.HttpMethod.POST;

import java.net.URI;
import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.ws.rs.core.UriBuilder;

import no.nav.foreldrepenger.los.klient.fpsak.dto.behandling.BehandlingDto;
import no.nav.foreldrepenger.los.klient.fpsak.dto.behandling.ResourceLink;
import no.nav.vedtak.felles.integrasjon.rest.NativeClient;
import no.nav.vedtak.felles.integrasjon.rest.RestClient;
import no.nav.vedtak.felles.integrasjon.rest.RestClientConfig;
import no.nav.vedtak.felles.integrasjon.rest.RestConfig;
import no.nav.vedtak.felles.integrasjon.rest.RestRequest;

@Dependent
@NativeClient
@RestClientConfig(endpointProperty = "fpsak.url", endpointDefault = "http://fpsak") // TODO - ønsker bruke appliaction = men ressurslenkene begynner med /<fpapp>/api/...
public class NativeForeldrepengerBehandling implements ForeldrepengerBehandling {

    private final RestClient klient;
    private final URI baseUri;

    @Inject
    public NativeForeldrepengerBehandling(RestClient klient) {
        this.klient = klient;
        this.baseUri = RestConfig.endpointFromAnnotation(NativeForeldrepengerBehandling.class);
    }

    @Override
    public BehandlingDto hentUtvidetBehandlingDto(String id) {
        LOG.trace("Henter behandling for {}", id);
        var target = UriBuilder.fromUri(baseUri).path(FPSAK_BEHANDLINGER).queryParam(BEHANDLING_ID, id).build();
        var res = klient.send(RestRequest.newGET(target, NativeForeldrepengerBehandling.class), BehandlingDto.class);
        LOG.info("Hentet behandling for {} OK", id);
        return res;
    }

    @Override
    public <T> Optional<T> hentFraResourceLink(ResourceLink link, Class<T> clazz) {
        var target = UriBuilder.fromUri(baseUri).path(link.getHref().getRawPath());
        target = QueryUtil.addQueryParams(link.getHref(), target);

        if (POST.equals(link.getType())) {
            var request = RestRequest.newPOSTJson(link.getRequestPayload(), target.build(), NativeForeldrepengerBehandling.class);
            return Optional.ofNullable(klient.send(request, clazz));
        }
        return Optional.ofNullable(klient.send(RestRequest.newGET(target.build(), NativeForeldrepengerBehandling.class), clazz));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [baseUri=" + baseUri + "]";
    }

}
