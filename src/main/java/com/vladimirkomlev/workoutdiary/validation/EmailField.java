package com.vladimirkomlev.workoutdiary.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@NotBlank(message = "should not be blank")
@Pattern(regexp = ".+@.+", message = "must have valid email format")
@Constraint(validatedBy = {})
@Target(FIELD)
@Retention(RUNTIME)
public @interface EmailField {
    String message() default "";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
