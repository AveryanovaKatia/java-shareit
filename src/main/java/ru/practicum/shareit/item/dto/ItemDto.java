package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.group.CreateGroup;

@Data
@EqualsAndHashCode(exclude = {"id"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {

    Integer id;

    @NotBlank(groups = {CreateGroup.class})
    String name;

    @NotBlank(groups = {CreateGroup.class})
    String description;

    @NotNull(groups = {CreateGroup.class})
    Boolean available;

    Integer requestId;
}
