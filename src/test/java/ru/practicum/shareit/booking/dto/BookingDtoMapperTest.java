package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
class BookingDtoMapperTest {

    Booking booking;
    BookingShortDto bookingShortDto;
    BookingDtoMapper bookingDtoMapper;
    User booker;
    Item item;

    @BeforeEach
    void setUp() {
        bookingDtoMapper = new BookingDtoMapper();

        booker = new User();
        booker.setId(1L);
        booker.setName("Booker");
        booker.setEmail("booker@email.com");

        item = new Item();
        item.setId(1L);
        item.setName("item");
        item.setOwnerId(booker.getId());
        item.setDescription("item description");
        item.setAvailable(true);

        bookingShortDto = new BookingShortDto();
        bookingShortDto.setId(1L);
        bookingShortDto.setStatus(BookingStatus.WAITING);
        bookingShortDto.setStart(LocalDateTime.now().minus(1, ChronoUnit.DAYS).toString());
        bookingShortDto.setEnd(LocalDateTime.now().plus(1, ChronoUnit.DAYS).toString());

        booking = new Booking();
        booking.setId(1L);
        booking.setStatus(BookingStatus.WAITING);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(LocalDateTime.now().minus(1, ChronoUnit.DAYS));
        booking.setEnd(LocalDateTime.now().plus(1, ChronoUnit.DAYS));
    }

    @Test
    void toBooking() {
        Booking newBooking = bookingDtoMapper.toBooking(bookingShortDto,item, booker);
        assertEquals(bookingShortDto.getStatus(), newBooking.getStatus());
    }

    @Test
    void toDto() {
        BookingDto newBookingDto = bookingDtoMapper.toDto(booking);
        assertEquals(booking.getBooker().getName(), newBookingDto.getBooker().getName());
    }
}