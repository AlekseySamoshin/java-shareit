package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookingService {
    BookingRepository bookingRepository;
    BookingDtoMapper bookingDtoMapper;
    ItemRepository itemRepository;
    UserRepository userRepository;

    @Autowired
    public BookingService(BookingRepository bookingRepository,
                          BookingDtoMapper bookingDtoMapper,
                          ItemRepository itemRepository,
                          UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.bookingDtoMapper = bookingDtoMapper;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    public BookingDto addBooking(Long userId, BookingShortDto bookingShortDto) {
        validateBookingDto(bookingShortDto);
        User booker = userRepository.findById(userId).get();
        Optional<Item> item = itemRepository.findById(bookingShortDto.getItemId());
        if (item.isEmpty()) {
            throw new NotFoundException("Вешь с id=" + bookingShortDto.getItemId() + " не найдена!");
        }
        if (!item.get().isAvailable()) {
            throw new WrongDataException("Вешь с id=" + bookingShortDto.getItemId() + " недоступна для аренды!");
        }
        Booking booking = bookingDtoMapper.toBooking(bookingShortDto, item.get(), booker);
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
            throw new NotFoundException("Пользователь id=" + userId + " не владелец вещи id=" + booking.getItem().getId()
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

    public List<BookingDto> getBookingsOfUser(Long userId, String state) {
        if (state == null) {
            state = "ALL";
        }
        findUserByIdIfExists(userId);
        List<Booking> bookings = findBookigsWithState(userId, state);
        return bookings.stream()
                .map(bookingDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<BookingDto> getBookingsOfOwnerItems(Long ownerId, String state) {
        findUserByIdIfExists(ownerId);
        List<Long> itemIds = itemRepository.findAllByOwnerId(ownerId).stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        if (itemIds.isEmpty()) {
            throw new NotFoundException("У пользователя id=" + ownerId + " не найдено вещей");
        }
        List<Booking> bookings = findBookingsOfItemsWithState(itemIds, state);
        return bookings.stream()
                .map(bookingDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    private User findUserByIdIfExists(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
        return user.get();
    }

    private void setBookingStatus(Long userId, Booking booking, Boolean approved) {
        if (!(booking.getStatus() == null) && booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new WrongDataException("Статус аренды уже подтвержден");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
    }

    private Booking findBookingById(Long bookingId) {
        Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);
        if (bookingOptional.isEmpty()) {
            throw new NotFoundException("Бронирование id=" + bookingId + "не найдено!");
        }
        return bookingOptional.get();
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
            throw new WrongDataException("Ошибка валидации бронирования: " + message.toString());
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
}
