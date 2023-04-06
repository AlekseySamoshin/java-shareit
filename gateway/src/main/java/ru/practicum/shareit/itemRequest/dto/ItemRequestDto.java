package ru.practicum.shareit.itemRequest.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.Item;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class ItemRequestDto {

    private Long id;

    private Long requestorId;

    @NotNull
    private String description;

    private String created;

    private List<Item> items;
}
