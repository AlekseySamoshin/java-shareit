package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@Slf4j
public class BookingController {
    private static final String USER_ID_REQUEST_HEADER = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService, BookingRepository bookingRepository) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto addBooking(@RequestHeader(value = USER_ID_REQUEST_HEADER) Long userId,
                                 @RequestBody BookingShortDto bookingShortDto) {
        log.info("Получен запрос на добавление аренды от пользователя id=" + userId);
        return bookingService.addBooking(userId, bookingShortDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBooking(@RequestHeader(value = USER_ID_REQUEST_HEADER) Long userId,
                                         @PathVariable Long bookingId,
                                         @RequestParam Boolean approved) {
        log.info("Получен запрос на изменение статуса аренды id=" + bookingId + " от пользователя id=" + bookingId);
        return bookingService.updateBooking(bookingId, userId, approved);
    }

    @GetMapping()
    public List<BookingDto> getBookingsOfUser(@RequestHeader(value = USER_ID_REQUEST_HEADER) Long userId,
                                              @RequestParam(required = false) String state,
                                              @RequestParam(name = "from", required = false) Integer from,
                                              @RequestParam(name = "size", required = false) Integer pageSize) {
        log.info("Запрос на получение заявок на аренду пользователя id=" + userId);
        return bookingService.getBookingsOfUser(userId, state, from, pageSize);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsOfOwnersItems(@RequestHeader(value = USER_ID_REQUEST_HEADER) Long userId,
                                                     @RequestParam(required = false) String state,
                                                     @RequestParam(name = "from", required = false) Integer pageNum,
                                                     @RequestParam(name = "size", required = false) Integer pageSize) {
        log.info("Запрос на получение заявок на аренду всех вещей пользователя id=" + userId);
        return bookingService.getBookingsOfOwnerItems(userId, state, pageNum, pageSize);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader(value = USER_ID_REQUEST_HEADER) Long userId,
                                      @PathVariable Long bookingId) {
        log.info("Запрос на получение информации об аренде id=" + bookingId);
        return bookingService.getBooking(userId, bookingId);
    }
}
