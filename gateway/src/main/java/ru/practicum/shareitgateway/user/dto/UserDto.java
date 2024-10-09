package ru.practicum.shareitgateway.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareitgateway.group.CreateGroup;

@Data
@EqualsAndHashCode(exclude = {"id"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {

    Integer id;

    @NotBlank(groups = {CreateGroup.class})
    String name;

    @NotBlank(groups = {CreateGroup.class})
    @Email(groups = {CreateGroup.class})
    @Email(message = "Емейл должен содержать @ и наименование")
    String email;
}
