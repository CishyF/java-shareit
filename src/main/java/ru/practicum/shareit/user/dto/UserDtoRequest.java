package ru.practicum.shareit.user.dto;

import lombok.Data;
import ru.practicum.shareit.user.util.validation.NotDuplicatedEmail;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UserDtoRequest {

    @NotBlank
    private String name;
    @Email
    @NotNull
    @NotDuplicatedEmail
    private String email;
}
