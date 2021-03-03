package no.nav.foreldrepenger.los.felles.util.validering;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE_USE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = { KodeverdiValidator.class })
@Documented
public @interface ValidKodeliste {

    String message() default "kodeverk kode feilet validering";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
