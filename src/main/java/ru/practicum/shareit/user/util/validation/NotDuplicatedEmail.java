package ru.practicum.shareit.user.util.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Deprecated
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotDuplicatedEmailValidator.class)
@Documented
public @interface NotDuplicatedEmail {
    String message() default "{ru.practicum.shareit.user.util.validation.NotDuplicatedEmail.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
