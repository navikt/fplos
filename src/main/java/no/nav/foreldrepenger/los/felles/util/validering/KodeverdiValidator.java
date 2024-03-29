package no.nav.foreldrepenger.los.felles.util.validering;


import java.util.Objects;

import jakarta.validation.ConstraintValidatorContext;

import no.nav.foreldrepenger.los.felles.Kodeverdi;

public class KodeverdiValidator extends KodeverkValidator<Kodeverdi> {

    @Override
    public boolean isValid(Kodeverdi kodeliste, ConstraintValidatorContext context) {
        if (Objects.equals(null, kodeliste)) {
            return true;
        }
        var ok = true;

        if (!gyldigKode(kodeliste.getKode())) {
            context.buildConstraintViolationWithTemplate(invKode);
            ok = false;
        }

        return ok;
    }
}
