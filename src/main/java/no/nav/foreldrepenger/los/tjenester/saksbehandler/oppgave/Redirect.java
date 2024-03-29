package no.nav.foreldrepenger.los.tjenester.saksbehandler.oppgave;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.los.tjenester.felles.dto.AsyncPollingStatus;
import no.nav.foreldrepenger.los.tjenester.felles.dto.SakslisteIdDto;
import no.nav.foreldrepenger.los.tjenester.saksbehandler.oppgave.dto.OppgaveIderDto;

public final class Redirect {
    private static final Logger log = LoggerFactory.getLogger(Redirect.class);

    private static final int POLL_INTERVAL_MILLIS = 1000;
    private static final String SAKSLISTE_ID = "sakslisteId";

    private Redirect() {
        // no ctor
    }

    public static Response sendTilPolling(HttpServletRequest request,
                                          SakslisteIdDto sakslisteId,
                                          OppgaveIderDto oppgaveIder) throws URISyntaxException {
        var uriBuilder = getUriBuilder(request).path(OppgaveRestTjeneste.OPPGAVER_BASE_PATH + OppgaveRestTjeneste.OPPGAVER_STATUS_PATH)
            .queryParam(SAKSLISTE_ID, sakslisteId.getVerdi());
        Optional.ofNullable(oppgaveIder).map(OppgaveIderDto::getVerdi).ifPresent(o -> uriBuilder.queryParam("oppgaveIder", o));
        var uri = honorXForwardedProto(request, uriBuilder.build());
        var status = new AsyncPollingStatus(AsyncPollingStatus.Status.PENDING, "", POLL_INTERVAL_MILLIS);
        status.setLocation(uri);
        return Response.status(status.getStatus().getHttpStatus()).entity(status).build();
    }

    public static Response sendTilResultat(HttpServletRequest request, SakslisteIdDto sakslisteId) throws URISyntaxException {
        var uriBuilder = getUriBuilder(request).path(OppgaveRestTjeneste.OPPGAVER_BASE_PATH + OppgaveRestTjeneste.OPPGAVER_RESULTAT_PATH)
            .queryParam(SAKSLISTE_ID, sakslisteId.getVerdi());
        var uri = honorXForwardedProto(request, uriBuilder.build());
        return Response.seeOther(uri).build();
    }

    public static Response sendTilStatus(HttpServletRequest request,
                                         SakslisteIdDto sakslisteId,
                                         OppgaveIderDto oppgaveIder) throws URISyntaxException {
        var uriBuilder = getUriBuilder(request).path(OppgaveRestTjeneste.OPPGAVER_BASE_PATH + OppgaveRestTjeneste.OPPGAVER_STATUS_PATH)
            .queryParam(SAKSLISTE_ID, sakslisteId.getVerdi());
        Optional.ofNullable(oppgaveIder).map(OppgaveIderDto::getVerdi).ifPresent(o -> uriBuilder.queryParam("oppgaveIder", o));
        var uri = honorXForwardedProto(request, uriBuilder.build());
        return Response.accepted().location(uri).build();
    }

    private static UriBuilder getUriBuilder(HttpServletRequest request) {
        var uriBuilder =
            request == null || request.getContextPath() == null ? UriBuilder.fromUri("") : UriBuilder.fromUri(URI.create(request.getContextPath()));
        Optional.ofNullable(request).map(HttpServletRequest::getServletPath).ifPresent(uriBuilder::path);
        return uriBuilder;
    }

    /**
     * @see URI#create(String)
     */
    private static URI honorXForwardedProto(HttpServletRequest request, URI location) throws URISyntaxException {
        URI newLocation = null;
        if (relativLocationAndRequestAvailable(location)) {
            String xForwardedProto = getXForwardedProtoHeader(request);

            if (mismatchedScheme(xForwardedProto, request)) {
                String path = location.toString();
                if (path.startsWith("/")) { // NOSONAR
                    path = path.substring(1); // NOSONAR
                }
                URI baseUri = new URI(request.getRequestURI());
                try {
                    URI rewritten = new URI(xForwardedProto, baseUri.getSchemeSpecificPart(), baseUri.getFragment()).resolve(path);
                    log.debug("Rewrote URI from '{}' to '{}'", location, rewritten);
                    newLocation = rewritten;
                } catch (URISyntaxException e) {
                    throw new IllegalArgumentException(e.getMessage(), e);
                }
            }
        }
        return newLocation != null ? newLocation : leggTilBaseUri(location);
    }

    private static boolean relativLocationAndRequestAvailable(URI location) {
        return location != null && !location.isAbsolute();
    }

    /**
     * @return http, https or null
     */
    private static String getXForwardedProtoHeader(HttpServletRequest httpRequest) {
        var xForwardedProto = httpRequest.getHeader("X-Forwarded-Proto");
        if ("https".equalsIgnoreCase(xForwardedProto) || "http".equalsIgnoreCase(xForwardedProto)) {
            return xForwardedProto;
        }
        return null;
    }

    private static boolean mismatchedScheme(String xForwardedProto, HttpServletRequest httpRequest) {
        return xForwardedProto != null && !xForwardedProto.equalsIgnoreCase(httpRequest.getScheme());
    }

    @SuppressWarnings("resource")
    private static URI leggTilBaseUri(URI resultatUri) {
        // tvinger resultatUri til å være en absolutt URI (passer med Location Header og Location felt når kommer i payload)
        var response = Response.noContent().location(resultatUri).build();
        return response.getLocation();
    }
}
