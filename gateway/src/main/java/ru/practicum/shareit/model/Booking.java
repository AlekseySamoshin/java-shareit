package ru.practicum.shareit.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.model.User;
import ru.practicum.shareit.model.Item;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class Booking {
    private Long id;
    private Item item;
    private User booker;
    private BookingState status;
    private LocalDateTime start;
    private LocalDateTime end;
}