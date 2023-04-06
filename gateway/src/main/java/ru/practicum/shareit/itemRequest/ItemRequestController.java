package ru.practicum.shareit.itemRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.itemRequest.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private ItemRequestClient itemRequestClient;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @GetMapping
    ResponseEntity<Object> getBookings(@RequestHeader(USER_ID_HEADER) Long userId,
                                       @RequestParam(name = "state") String state,
                                       @PositiveOrZero @RequestParam(name = "from") Integer from,
                                       @PositiveOrZero @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState bookingState = BookingState.from(state)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));
        log.info("Get item request. User id={}", userId);
        return itemRequestClient.getBookings(userId, bookingState, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> addBooking(@RequestHeader(USER_ID_HEADER) long userId,
                                           @RequestBody @Valid BookingDto bookingDto) {
        log.info("Creating booking, userId={}", userId);
        return itemRequestClient.addBooking(userId, bookingDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(USER_ID_HEADER) long userId,
                                             @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return itemRequestClient.getBookingById(userId, bookingId);
    }







    @PostMapping
    public ResponseEntity<Object> addNewRequest(@RequestHeader(USER_ID_HEADER) Long userId,
                                                @Valid @RequestBody ItemRequestDto requestDto) {
        log.info("Creating request, userId={}", userId);
        return itemRequestClient.addItemRequest(userId, requestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getAllRequestsOfUser(@RequestHeader(value = USER_ID_HEADER) Long userId) {
        return itemRequestClient.getAllRequestsOfUser(userId);
    }

    @GetMapping(path = "/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader(value = USER_ID_HEADER) Long userId,
                                               @PositiveOrZero @RequestParam(name = "from", required = false) Integer pageNum,
                                               @Positive @RequestParam(name = "size", required = false) Integer pageSize) {
        return itemRequestClient.getAllRequests(userId, pageNum, pageSize);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader(value = USER_ID_HEADER) Long userId,
                                         @PathVariable Long requestId) {
        return itemRequestClient.getRequestById(userId, requestId);
    }
}
