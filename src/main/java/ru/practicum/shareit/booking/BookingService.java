package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.WrongDataException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final BookingDtoMapper bookingDtoMapper;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public BookingDto addBooking(Long userId, BookingShortDto bookingShortDto) {
        validateBookingDto(bookingShortDto);
        User booker = userRepository.findById(userId).get();
        Item item = itemRepository.findById(bookingShortDto.getItemId()).orElseThrow(
                () -> new NotFoundException("Вешь с id=" + bookingShortDto.getItemId() + " не найдена!")
        );
        if (!item.isAvailable()) {
            throw new WrongDataException("Вешь с id=" + bookingShortDto.getItemId() + " недоступна для аренды!");
        }
        Booking booking = bookingDtoMapper.toBooking(bookingShortDto, item, booker);
        if (userId.equals(booking.getItem().getOwnerId())) {
            throw new NotFoundException("Забронировать собственную вещь нельзя! ");
        }
        validateDatesOfBooking(booking);
        if (booking.getStatus() == null) {
            booking.setStatus(BookingStatus.WAITING);
        }
        bookingRepository.save(booking);
        return bookingDtoMapper.toDto(booking);
    }

    public BookingDto updateBooking(Long bookingId, Long userId, Boolean approved) {
        findUserByIdIfExists(userId);
        Booking booking = findBookingById(bookingId);
        Item item = booking.getItem();
        if (!userId.equals(item.getOwnerId())) {
            throw new NotFoundException("Пользователь id=" + userId + " не владелец вещи id=" + item.getId()
                                         + ". Подтвердить или отклонить аренду нельзя!");
        }
        setBookingStatus(userId, booking, approved);
        bookingRepository.save(booking);
        return bookingDtoMapper.toDto(booking);
    }

    public BookingDto getBooking(Long userId, Long bookingId) {
        findUserByIdIfExists(userId);
        Booking booking = findBookingById(bookingId);
        if (!booking.getItem().getOwnerId().equals(userId) && !booking.getBooker().getId().equals(userId)) {
            throw new NotFoundException("Пользователь id=" + userId + " не является арендатором или владельцем вещи");
        }
        return bookingDtoMapper.toDto(booking);
    }

    public List<BookingDto> getBookingsOfUser(Long userId, String state, Integer from, Integer pageSize) {
        if (state == null) {
            state = "ALL";
        }
        findUserByIdIfExists(userId);
        if (from == null || pageSize == null) {
            List<Booking> bookings = findBookigsWithState(userId, state);
            return bookings.stream()
                    .map(bookingDtoMapper::toDto)
                    .collect(Collectors.toList());
        }
        validatePagesRequest(from, pageSize);
        List<Booking> bookings = findBookigsWithStatePageable(userId, state, PageRequest.of(from / pageSize, pageSize));
        return bookings.stream()
                .map(bookingDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<BookingDto> getBookingsOfOwnerItems(Long ownerId, String state, Integer pageNum, Integer pageSize) {
        findUserByIdIfExists(ownerId);
        List<Long> itemIds = itemRepository.findAllByOwnerId(ownerId).stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        if (itemIds.isEmpty()) {
            throw new NotFoundException("У пользователя id=" + ownerId + " не найдено вещей");
        }
        List<Booking> bookings;
        if (pageNum == null || pageSize == null) {
            bookings = findBookingsOfItemsWithState(itemIds, state);
        } else {
            validatePagesRequest(pageNum, pageSize);
            bookings = findBookingsOfItemsWithState(itemIds, state, PageRequest.of(pageNum, pageSize));
        }

        return bookings.stream()
                .map(bookingDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    private User findUserByIdIfExists(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
    }

    private void setBookingStatus(Long userId, Booking booking, Boolean approved) {
        if (BookingStatus.APPROVED.equals(booking.getStatus())) {
            throw new WrongDataException("Статус аренды уже подтвержден");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
    }

    private Booking findBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotFoundException("Бронирование id=" + bookingId + "не найдено!"));
    }

    private void validateBookingDto(BookingShortDto bookingShortDto) {
        StringBuilder message = new StringBuilder();
        if (bookingShortDto.getItemId() == null) {
            message.append("Не указан id вещи! ");
        }
        if (bookingShortDto.getStart() == null) {
            message.append("Не указана дата начала бронирования! ");
        }
        if (bookingShortDto.getEnd() == null) {
            message.append("Не указана дата окончания бронирования! ");
        }
        if (!message.toString().isBlank()) {
            throw new WrongDataException("Ошибка валидации бронирования: " + message);
        }
    }

    private void validateDatesOfBooking(Booking booking) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        if (booking.getEnd().isBefore(booking.getStart()) || booking.getStart().isEqual(booking.getEnd())) {
            throw new WrongDataException("Неверно указаны даты начала и окончания аренды");
        }
        if (booking.getStart().isBefore(currentDateTime)) {
            throw new WrongDataException("Дата начала аренды уже прошла");
        }
        if (booking.getEnd().isBefore(currentDateTime)) {
            throw new WrongDataException("Дата окончания аренды уже прошла");
        }
    }

    private void validatePagesRequest(Integer pageNum, Integer pageSize) {
        if (pageNum <= 0 || pageSize <= 0) {
            throw new WrongDataException("Ошибка: неверно указан начальный индекс или размер страницы");
        }
    }

    private List<Booking> findBookigsWithState(Long userId, String state) {
        switch (state.toUpperCase()) {
            case "ALL":
                return bookingRepository.findAllByUserId(userId);
            case "CURRENT":
                return bookingRepository.findAllCurrentByUserId(userId);
            case "PAST":
                return bookingRepository.findAllPastByUserId(userId);
            case "FUTURE":
                return bookingRepository.findAllFutureByUserId(userId);
            case "WAITING":
                return bookingRepository.findAllWaitingByUserId(userId);
            case "REJECTED":
                return bookingRepository.findAllRejectedByUserId(userId);
            default:
                throw new WrongDataException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    private List<Booking> findBookigsWithStatePageable(Long userId, String state, Pageable page) {
        switch (state.toUpperCase()) {
            case "ALL":
                return bookingRepository.findAllByUserId(userId, page);
            case "CURRENT":
                return bookingRepository.findAllCurrentByUserId(userId, page);
            case "PAST":
                return bookingRepository.findAllPastByUserId(userId, page);
            case "FUTURE":
                return bookingRepository.findAllFutureByUserId(userId, page);
            case "WAITING":
                return bookingRepository.findAllWaitingByUserId(userId, page);
            case "REJECTED":
                return bookingRepository.findAllRejectedByUserId(userId, page);
            default:
                throw new WrongDataException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    private List<Booking> findBookingsOfItemsWithState(List<Long> itemIds, String state) {
        if (state == null) {
            state = "ALL";
        }
        switch (state.toUpperCase()) {
            case "ALL":
                return bookingRepository.findAllBookingsForItems(itemIds);
            case "CURRENT":
                return bookingRepository.findCurrentBookingsForItems(itemIds);
            case "PAST":
                return bookingRepository.findPastBookingsForItems(itemIds);
            case "FUTURE":
                return bookingRepository.findFutureBookingsForItems(itemIds);
            case "WAITING":
                return bookingRepository.findWaititngBookingsForItems(itemIds);
            case "REJECTED":
                return bookingRepository.findRejectedBookingsForItems(itemIds);
            default:
                throw new WrongDataException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    private List<Booking> findBookingsOfItemsWithState(List<Long> itemIds, String state, Pageable page) {
        if (state == null) {
            state = "ALL";
        }
        switch (state.toUpperCase()) {
            case "ALL":
                return bookingRepository.findAllBookingsForItems(itemIds, page);
            case "CURRENT":
                return bookingRepository.findCurrentBookingsForItems(itemIds, page);
            case "PAST":
                return bookingRepository.findPastBookingsForItems(itemIds, page);
            case "FUTURE":
                return bookingRepository.findFutureBookingsForItems(itemIds, page);
            case "WAITING":
                return bookingRepository.findWaititngBookingsForItems(itemIds, page);
            case "REJECTED":
                return bookingRepository.findRejectedBookingsForItems(itemIds, page);
            default:
                throw new WrongDataException("Unknown state: UNSUPPORTED_STATUS");
        }

    }
}
