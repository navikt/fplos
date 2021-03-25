package no.nav.foreldrepenger.los.web.app.exceptions;

import java.io.Serializable;

public record FeltFeilDto(String navn, String melding) implements Serializable {

}
