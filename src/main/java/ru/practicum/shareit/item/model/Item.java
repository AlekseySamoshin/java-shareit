package ru.practicum.shareit.item.model;

import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class Item {
    public Long id;
    public String name;
    public String description;
    public boolean available;

}
