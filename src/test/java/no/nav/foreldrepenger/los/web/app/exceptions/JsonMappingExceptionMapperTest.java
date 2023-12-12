package no.nav.foreldrepenger.los.web.app.exceptions;

import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JsonMappingExceptionMapperTest {

    @Test
    void skal_mappe_InvalidTypeIdException() {
        var mapper = new JsonMappingExceptionMapper();
        var resultat = mapper.toResponse(new InvalidTypeIdException(null, "Ukjent type-kode", null, "23525"));
        var dto = (FeilDto) resultat.getEntity();
        assertThat(dto.feilmelding()).contains("JSON-mapping feil");
        assertThat(dto.feltFeil()).isEmpty();
    }
}
