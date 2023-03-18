package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

@Getter
@Setter
public class BookingDto {
    Long id;
    Item item;
    User booker;
    Long bookerId;
    String start;
    String end;
    String status;
}
