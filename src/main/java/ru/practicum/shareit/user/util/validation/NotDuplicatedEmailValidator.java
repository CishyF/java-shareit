package ru.practicum.shareit.user.util.validation;

import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.exception.EntityAlreadyExistsException;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
public class NotDuplicatedEmailValidator implements ConstraintValidator<NotDuplicatedEmail, String> {

    private final UserService userService;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email != null && userService.isEmailExists(email)) {
            throw new EntityAlreadyExistsException("Попытка создать пользователя с уже использованной почтой");
        }
        return true;
    }
}
