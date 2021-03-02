package no.nav.foreldrepenger.loslager.validering;

import no.nav.foreldrepenger.loslager.oppgave.Kodeverdi;

import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

public class KodeverdiValidator extends KodeverkValidator<Kodeverdi> {

    @Override
    public boolean isValid(Kodeverdi kodeliste, ConstraintValidatorContext context) {
        if (Objects.equals(null, kodeliste)) {
            return true;
        }
        boolean ok = true;

        if (!gyldigKode(kodeliste.getKode())) {
            context.buildConstraintViolationWithTemplate(invKode);
            ok = false;
        }

        if (!gyldigKodeverk(kodeliste.getKodeverk())) {
            context.buildConstraintViolationWithTemplate(invNavn);
            ok = false;
        }

        return ok;
    }
}
