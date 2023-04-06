package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private BookingClient bookingClient;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @GetMapping
    ResponseEntity<Object> getBookings(@RequestHeader(USER_ID_HEADER) Long userId,
                                       @RequestParam(name = "state") String state,
                                       @PositiveOrZero @RequestParam(name = "from") Integer from,
                                       @PositiveOrZero @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState bookingState = BookingState.from(state)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));
        log.info("Get bookings request. User id={}", userId);
        return bookingClient.getBookings(userId, bookingState, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> addBooking(@RequestHeader(USER_ID_HEADER) long userId,
                                           @RequestBody @Valid BookingDto bookingDto) {
        log.info("Creating booking, userId={}", userId);
        return bookingClient.addBooking(userId, bookingDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(USER_ID_HEADER) long userId,
                                             @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBookingById(userId, bookingId);
    }
}
