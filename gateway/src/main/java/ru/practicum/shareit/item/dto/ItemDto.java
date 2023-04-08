package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class ItemDto {

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    private Boolean available;

    private Long requestId;
}
