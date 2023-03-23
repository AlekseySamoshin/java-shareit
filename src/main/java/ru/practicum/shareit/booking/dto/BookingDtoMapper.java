package ru.practicum.shareit.booking.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Component
public class BookingDtoMapper {
    public Booking toBooking(BookingShortDto dto, Item item, User booker) {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(LocalDateTime.parse(dto.getStart()));
        booking.setEnd(LocalDateTime.parse(dto.getEnd()));
        booking.setStatus(dto.getStatus());
        return booking;
    }

    public BookingDto toDto(Booking booking) {
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setItem(booking.getItem());
        dto.setBooker(booking.getBooker());
        dto.setBookerId(booking.getBooker().getId());
        dto.setStart(booking.getStart().toString());
        dto.setEnd(booking.getEnd().toString());
        dto.setStatus(booking.getStatus().toString());
        return dto;
    }
}
