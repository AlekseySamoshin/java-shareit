package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * TODO Sprint add-controllers.
 */
@Getter
@Setter
public class ItemDto {
    public Long id;
    public String name;
    public String description;
    public Boolean available;
}
