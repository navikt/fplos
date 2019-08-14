package no.nav.foreldrepenger.los.web.app.validering;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE_USE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {KodeverkTabellValidator.class, KodelisteValidator.class})
@Documented
public @interface ValidKodeverk {

    String message() default "kodeverk kode feilet validering";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}