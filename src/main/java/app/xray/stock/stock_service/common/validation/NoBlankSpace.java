package app.xray.stock.stock_service.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {})
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Pattern(regexp = "^\\S*$", message = "Must not contain any whitespace characters.")
public @interface NoBlankSpace {
    String message() default "Must not contain any whitespace characters.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
