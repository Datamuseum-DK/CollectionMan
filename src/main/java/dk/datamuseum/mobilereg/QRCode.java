package dk.datamuseum.mobilereg;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Example of annotation for validation.
 */
@Target({ FIELD })
@Retention(RUNTIME)
@Constraint(validatedBy = QRCodeValidator.class)
@Documented
public @interface QRCode {

  String message() default "Invalid QR Code";

  Class<?>[] groups() default { };

  Class<? extends Payload>[] payload() default { };

}
