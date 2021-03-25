package no.nav.foreldrepenger.los.web.app.exceptions;

import java.util.stream.Collectors;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.spi.ApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import no.nav.foreldrepenger.los.klient.fpsak.InternIdMappingException;
import no.nav.vedtak.exception.FunksjonellException;
import no.nav.vedtak.exception.ManglerTilgangException;
import no.nav.vedtak.exception.VLException;
import no.nav.vedtak.felles.jpa.TomtResultatException;
import no.nav.vedtak.log.mdc.MDCOperations;
import no.nav.vedtak.log.util.LoggerUtils;

@Provider
public class GeneralRestExceptionMapper implements ExceptionMapper<ApplicationException> {

    private static final Logger LOG = LoggerFactory.getLogger(GeneralRestExceptionMapper.class);

    @Override
    public Response toResponse(ApplicationException exception) {
        var cause = exception.getCause();

        if (cause instanceof Valideringsfeil) {
            return handleValideringsfeil((Valideringsfeil) cause);
        }
        if (cause instanceof TomtResultatException) {
            return handleTomtResultatFeil((TomtResultatException) cause);
        }

        loggTilApplikasjonslogg(cause);
        var callId = MDCOperations.getCallId();

        if (cause instanceof VLException) {
            return handleVLException((VLException) cause, callId);
        }

        return handleGenerellFeil(cause, callId);
    }

    private Response handleTomtResultatFeil(TomtResultatException tomtResultatException) {
        return Response
            .status(Response.Status.NOT_FOUND)
            .entity(new FeilDto(FeilType.TOMT_RESULTAT_FEIL, tomtResultatException.getMessage()))
            .type(MediaType.APPLICATION_JSON)
            .build();
    }

    private Response handleValideringsfeil(Valideringsfeil valideringsfeil) {
        var feltNavn = valideringsfeil.getFeltfeil().stream()
                .map(FeltFeilDto::navn)
                .collect(Collectors.toList());
        return Response
            .status(Response.Status.BAD_REQUEST)
            .entity(new FeilDto(
                FeltValideringFeil.feltverdiKanIkkeValideres(feltNavn).getMessage(),
                valideringsfeil.getFeltfeil()))
            .type(MediaType.APPLICATION_JSON)
            .build();
    }

    private Response handleVLException(VLException vlException, String callId) {
        if (vlException instanceof ManglerTilgangException) {
            return ikkeTilgang(vlException);
        }
        return serverError(callId, vlException);
    }

    private Response serverError(String callId, VLException feil) {
        var feilmelding = getVLExceptionFeilmelding(callId, feil);
        var feilType = FeilType.GENERELL_FEIL;
        return Response.serverError()
            .entity(new FeilDto(feilType, feilmelding))
            .type(MediaType.APPLICATION_JSON)
            .build();
    }

    private Response ikkeTilgang(VLException exception) {
        final FeilType feilType;
        if (exception instanceof InternIdMappingException) {
            //Må returnere generell for å få rød feilmelding i front. Frontend har problemer med å vise manglende tilgang
            feilType = FeilType.GENERELL_FEIL;
        } else {
            feilType = FeilType.MANGLER_TILGANG_FEIL;
        }
        var feilmelding = exception.getMessage();
        return Response.status(Response.Status.FORBIDDEN)
            .entity(new FeilDto(feilType, feilmelding))
            .type(MediaType.APPLICATION_JSON)
            .build();
    }

    private static String getVLExceptionFeilmelding(String callId, VLException feil) {
        var feilbeskrivelse = feil.getMessage();
        if (feil instanceof FunksjonellException) {
            var løsningsforslag = ((FunksjonellException) feil).getLøsningsforslag();
            return String.format("Det oppstod en feil: %s - %s. Referanse-id: %s", feilbeskrivelse, løsningsforslag, callId);
        }
        return String.format("Det oppstod en serverfeil: %s. Meld til support med referanse-id: %s", feilbeskrivelse, callId);
    }

    private Response handleGenerellFeil(Throwable cause, String callId) {
        var generellFeilmelding = "Det oppstod en serverfeil: " + cause.getMessage() + ". Meld til support med referanse-id: " + callId;
        return Response.serverError()
            .entity(new FeilDto(FeilType.GENERELL_FEIL, generellFeilmelding))
            .type(MediaType.APPLICATION_JSON)
            .build();
    }

    private void loggTilApplikasjonslogg(Throwable cause) {
        if (cause instanceof VLException) {
            LOG.warn(cause.getMessage());
        } else {
            var message = cause.getMessage() != null ? LoggerUtils.removeLineBreaks(cause.getMessage()) : "";
            LOG.error("Fikk uventet feil:" + message, cause); //NOSONAR //$NON-NLS-1$
        }

        // key for å tracke prosess -- nullstill denne
        MDC.remove("prosess");  //$NON-NLS-1$
    }

}
