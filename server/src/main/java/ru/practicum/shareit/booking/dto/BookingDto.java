package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

@Getter
@Setter
public class BookingDto {
    private Long id;
    private Item item;
    private User booker;
    private Long bookerId;
    private String start;
    private String end;
    private String status;
}
