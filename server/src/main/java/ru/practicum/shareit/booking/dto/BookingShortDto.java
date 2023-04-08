package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.Booking;

@Getter
@Setter
public class BookingShortDto {
    private Booking booking = new Booking();
    private Long id;
    private Long itemId;
    private String start;
    private String end;
    private BookingStatus status;
}
