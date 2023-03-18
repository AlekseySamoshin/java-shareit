package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;

@Getter
@Setter
public class BookingShortDto {
    Booking booking = new Booking();
    Long id;
    Long itemId;
    String start;
    String end;
    BookingStatus status;
}
