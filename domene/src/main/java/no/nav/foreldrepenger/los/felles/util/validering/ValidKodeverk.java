package no.nav.foreldrepenger.los.felles.util.validering;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ElementType.TYPE_USE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {KodeverdiValidator.class})
@Documented
public @interface ValidKodeverk {

    String message() default "kodeverk kode feilet validering";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
