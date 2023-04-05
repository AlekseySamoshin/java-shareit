package ru.practicum.shareit.request.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.Item;

import java.util.List;

@Getter
@Setter
public class ItemRequestDto {
    private Long id;
    private Long requestorId;
    private String description;
    private String created;
    private List<Item> items;
}
