package no.nav.foreldrepenger.los.web.app.exceptions;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;

import javax.ws.rs.WebApplicationException;

import org.junit.jupiter.api.Test;

import no.nav.vedtak.exception.FunksjonellException;
import no.nav.vedtak.exception.ManglerTilgangException;
import no.nav.vedtak.exception.TekniskException;
import no.nav.vedtak.exception.VLException;

public class GeneralRestExceptionMapperTest {

    private final GeneralRestExceptionMapper generalRestExceptionMapper = new GeneralRestExceptionMapper();

    @Test
    public void skalMappeValideringsfeil() {
        var feltFeilDto = new FeltFeilDto("Et feltnavn", "En feilmelding");
        var valideringsfeil = new Valideringsfeil(Collections.singleton(feltFeilDto));

        var response = generalRestExceptionMapper.toResponse(new WebApplicationException(valideringsfeil));

        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getEntity()).isInstanceOf(FeilDto.class);
        var feilDto = (FeilDto) response.getEntity();

        assertThat(feilDto.getFeilmelding()).contains("Det oppstod en valideringsfeil på felt [Et feltnavn]. Vennligst kontroller at alle feltverdier er korrekte.");
        assertThat(feilDto.getFeltFeil()).hasSize(1);
        assertThat(feilDto.getFeltFeil().iterator().next()).isEqualTo(feltFeilDto);
    }

    @Test
    public void skalMappeManglerTilgangFeil() {
        var manglerTilgangFeil = TestFeil.manglerTilgangFeil();

        var response = generalRestExceptionMapper.toResponse(new WebApplicationException(manglerTilgangFeil));

        assertThat(response.getStatus()).isEqualTo(403);
        assertThat(response.getEntity()).isInstanceOf(FeilDto.class);
        var feilDto = (FeilDto) response.getEntity();

        assertThat(feilDto.getType()).isEqualTo(FeilType.MANGLER_TILGANG_FEIL);
        assertThat(feilDto.getFeilmelding()).contains("ManglerTilgangFeilmeldingKode");
    }

    @Test
    public void skalMappeFunksjonellFeil() {
        var funksjonellFeil = TestFeil.funksjonellFeil();

        var response = generalRestExceptionMapper.toResponse(new WebApplicationException(funksjonellFeil));

        assertThat(response.getEntity()).isInstanceOf(FeilDto.class);
        var feilDto = (FeilDto) response.getEntity();

        assertThat(feilDto.getFeilmelding()).contains("FUNK_FEIL");
        assertThat(feilDto.getFeilmelding()).contains("en funksjonell feilmelding");
        assertThat(feilDto.getFeilmelding()).contains("et løsningsforslag");
    }

    @Test
    public void skalMappeVLException() {
        VLException vlException = TestFeil.tekniskFeil();

        var response = generalRestExceptionMapper.toResponse(new WebApplicationException(vlException));

        assertThat(response.getEntity()).isInstanceOf(FeilDto.class);
        var feilDto = (FeilDto) response.getEntity();

        assertThat(feilDto.getFeilmelding()).contains("TEK_FEIL");
        assertThat(feilDto.getFeilmelding()).contains("en teknisk feilmelding");
    }

    @Test
    public void skalMappeGenerellFeil() {
        var feilmelding = "en helt generell feil";
        var generellFeil = new RuntimeException(feilmelding);

        var response = generalRestExceptionMapper.toResponse(new WebApplicationException(generellFeil));

        assertThat(response.getStatus()).isEqualTo(500);
        assertThat(response.getEntity()).isInstanceOf(FeilDto.class);
        var feilDto = (FeilDto) response.getEntity();

        assertThat(feilDto.getFeilmelding()).contains(feilmelding);
    }

    private static class TestFeil {

        static FunksjonellException funksjonellFeil() {
            return new FunksjonellException("FUNK_FEIL", "en funksjonell feilmelding", "et løsningsforslag");
        }

        static TekniskException tekniskFeil() {
            return new TekniskException("TEK_FEIL", "en teknisk feilmelding");
        }

        static ManglerTilgangException manglerTilgangFeil() {
            return new ManglerTilgangException("MANGLER_TILGANG_FEIL", "ManglerTilgangFeilmeldingKode");
        }
    }
}
