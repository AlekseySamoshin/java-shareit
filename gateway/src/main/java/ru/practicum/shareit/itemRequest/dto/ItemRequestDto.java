package ru.practicum.shareit.itemRequest.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ItemRequestDto {

    @NotNull
    private String description;

}
