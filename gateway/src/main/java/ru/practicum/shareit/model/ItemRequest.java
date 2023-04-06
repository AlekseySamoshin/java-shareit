package ru.practicum.shareit.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ItemRequest {
    private Long id;
    private Long requestorId;
    private String description;
    private List<Item> items;
    private LocalDateTime created;
}
