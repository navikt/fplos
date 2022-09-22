package no.nav.foreldrepenger.los.klient.fpsak;

import static no.nav.foreldrepenger.los.klient.fpsak.dto.behandling.ResourceLink.HttpMethod.POST;

import java.net.URI;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.UriBuilder;

import no.nav.foreldrepenger.los.klient.fpsak.dto.behandling.BehandlingDto;
import no.nav.foreldrepenger.los.klient.fpsak.dto.behandling.ResourceLink;
import no.nav.vedtak.felles.integrasjon.rest.FpApplication;
import no.nav.vedtak.felles.integrasjon.rest.NativeClient;
import no.nav.vedtak.felles.integrasjon.rest.RestClient;
import no.nav.vedtak.felles.integrasjon.rest.RestClientConfig;
import no.nav.vedtak.felles.integrasjon.rest.RestConfig;
import no.nav.vedtak.felles.integrasjon.rest.RestRequest;
import no.nav.vedtak.felles.integrasjon.rest.TokenFlow;

@ApplicationScoped
@NativeClient
@RestClientConfig(tokenConfig = TokenFlow.ADAPTIVE, application = FpApplication.FPSAK)
public class NativeForeldrepengerBehandling implements ForeldrepengerBehandling {

    private RestClient klient;
    private URI baseUri;

    NativeForeldrepengerBehandling() {
        // CDI
    }

    @Inject
    public NativeForeldrepengerBehandling(RestClient klient) {
        this.klient = klient;
        this.baseUri = RestConfig.contextPathFromAnnotation(NativeForeldrepengerBehandling.class);
    }

    @Override
    public BehandlingDto hentUtvidetBehandlingDto(String id) {
        LOG.trace("Henter behandling for {}", id);
        var target = UriBuilder.fromUri(baseUri).path("/api/behandlinger").queryParam(BEHANDLING_ID, id).build();
        var res = klient.send(RestRequest.newGET(target, NativeForeldrepengerBehandling.class), BehandlingDto.class);
        LOG.info("Hentet behandling for {} OK", id);
        return res;
    }

    @Override
    public <T> Optional<T> hentFraResourceLink(ResourceLink link, Class<T> clazz) {
        var linkpath = link.getHref().toString();
        var path = linkpath.startsWith("/fpsak") ?  linkpath.replaceFirst("/fpsak", "") : linkpath;
        var target = URI.create(baseUri + path);

        var request = POST.equals(link.getType()) ?
                RestRequest.newPOSTJson(link.getRequestPayload(), target, NativeForeldrepengerBehandling.class) :
                RestRequest.newGET(target, NativeForeldrepengerBehandling.class);

        return klient.sendReturnOptional(request, clazz);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [baseUri=" + baseUri + "]";
    }

}
