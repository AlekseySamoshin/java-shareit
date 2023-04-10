package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    public BookingController(BookingClient bookingClient) {
        this.bookingClient = bookingClient;
    }

    @GetMapping
    ResponseEntity<Object> getBookings(@RequestHeader(USER_ID_HEADER) Long userId,
                                       @RequestParam(name = "state", defaultValue = "ALL") String state,
                                       @PositiveOrZero @Nullable @RequestParam(name = "from") Integer from,
                                       @Positive @Nullable @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get bookings request. User id={}", userId);
        return bookingClient.getBookingsOfUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    ResponseEntity<Object> getBookingsOfUsersItems(@RequestHeader(value = USER_ID_HEADER) Long userId,
                                                   @Nullable @RequestParam(name = "state", defaultValue = "ALL") String state,
                                                   @PositiveOrZero @Nullable @RequestParam(name = "from", required = false) Integer from,
                                                   @Positive @Nullable @RequestParam(name = "size", required = false) Integer pageSize) {
        log.info("Get bookings of all items of user id={}", userId);
        return bookingClient.getBookingsForItems(userId, state, from, pageSize);
    }

    @PostMapping
    public ResponseEntity<Object> addBooking(@RequestHeader(USER_ID_HEADER) long userId,
                                             @RequestBody @Valid BookingDto bookingDto) {
        log.info("Creating booking, userId={}", userId);
        System.out.println(bookingDto);
        return bookingClient.addBooking(userId, bookingDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(USER_ID_HEADER) long userId,
                                             @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBookingById(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBooking(@RequestHeader(USER_ID_HEADER) Long userId,
                                                @PathVariable Long bookingId,
                                                @RequestParam Boolean approved) {
        log.info("Update booking state 'approved' to {}. Booking id={}", approved, bookingId);
        return bookingClient.updateBooking(userId, bookingId, approved);
    }
}
