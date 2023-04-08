package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.WrongDataException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class BookingServiceTest {

    @Mock
    BookingRepository bookingRepository;

    @Mock
    BookingDtoMapper bookingDtoMapper;

    @Mock
    UserRepository userRepository;

    @Mock
    ItemRepository itemRepository;

    @InjectMocks
    BookingService bookingService;

    private Booking currentBooking;
    private Booking futureBooking;
    private Booking pastBooking;
    private BookingDto currentBookingDto = new BookingDto();
    private BookingDto futureBookingDto = new BookingDto();
    private BookingShortDto currentBookingShortDto = new BookingShortDto();
    private BookingShortDto futureBookingShortDto = new BookingShortDto();
    private User owner = new User();
    private User booker = new User();
    private Item item1 = new Item();

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        futureBooking = new Booking();
        futureBooking.setId(1L);
        futureBooking.setBooker(booker);
        futureBooking.setItem(item1);
        futureBooking.setStart(now.plus(1, ChronoUnit.DAYS));
        futureBooking.setEnd(now.plus(2, ChronoUnit.DAYS));

        currentBooking = new Booking();
        currentBooking.setId(2L);
        currentBooking.setStatus(BookingStatus.APPROVED);
        currentBooking.setBooker(booker);
        currentBooking.setItem(item1);
        currentBooking.setStart(now.minus(1, ChronoUnit.DAYS));
        currentBooking.setEnd(now.plus(1, ChronoUnit.DAYS));

        pastBooking = new Booking();
        pastBooking = new Booking();
        pastBooking.setId(3L);
        pastBooking.setBooker(booker);
        pastBooking.setItem(item1);
        pastBooking.setStart(now.minus(3, ChronoUnit.DAYS));
        pastBooking.setEnd(now.minus(2, ChronoUnit.DAYS));
        pastBooking.setStatus(BookingStatus.REJECTED);


        futureBookingDto.setId(1L);
        futureBookingDto.setBooker(booker);
        futureBookingDto.setItem(item1);
        futureBookingDto.setStart(now.plus(1, ChronoUnit.DAYS).toString());
        futureBookingDto.setEnd(now.plus(2, ChronoUnit.DAYS).toString());

        currentBookingDto.setId(2L);
        currentBookingDto.setStatus(BookingStatus.APPROVED.toString());
        currentBookingDto.setBooker(booker);
        currentBookingDto.setItem(item1);
        currentBookingDto.setStart(now.minus(1, ChronoUnit.DAYS).toString());
        currentBookingDto.setEnd(now.plus(1, ChronoUnit.DAYS).toString());
        futureBookingShortDto.setItemId(1L);
        futureBookingShortDto.setStart(now.plus(1, ChronoUnit.DAYS).toString());
        futureBookingShortDto.setEnd(now.plus(2, ChronoUnit.DAYS).toString());

        currentBookingShortDto.setItemId(1L);
        currentBookingShortDto.setStart(now.minus(1, ChronoUnit.DAYS).toString());
        currentBookingShortDto.setEnd(now.plus(1, ChronoUnit.DAYS).toString());


        item1.setId(1L);
        item1.setName("item1");
        item1.setDescription("description1");
        item1.setOwnerId(1L);
        item1.setAvailable(true);

        owner.setId(1L);
        owner.setName("owner");
        owner.setEmail("owner@email.com");

        booker.setId(2L);
        booker.setName("booker");
        booker.setEmail("booker@email.com");
    }

    @Test
    void addNewBooking() {
        when(userRepository.findById(eq(2L))).thenReturn(Optional.of(booker));
        when(itemRepository.findById(eq(1L))).thenReturn(Optional.of(item1));
        when(bookingDtoMapper.toDto(any())).then(Mockito.CALLS_REAL_METHODS);
        when(bookingDtoMapper.toBooking(any(), any(), any())).thenReturn(futureBooking);
        BookingDto testBookingDto = bookingService.addBooking(2L, futureBookingShortDto);
        assertEquals(futureBooking.getId(), testBookingDto.getId());
    }

    @Test
    void addNewBookingWhetItemIsUnavailable() {
        when(userRepository.findById(eq(2L))).thenReturn(Optional.of(booker));
        when(itemRepository.findById(eq(1L))).thenReturn(Optional.of(item1));
        item1.setAvailable(false);
        Exception exception = assertThrows(
                WrongDataException.class,
                () -> bookingService.addBooking(booker.getId(), futureBookingShortDto));
        assertEquals("Вешь с id=" + futureBookingShortDto.getItemId() + " недоступна для аренды!", exception.getMessage());
    }

    @Test
    void addNewBookingWrongRequest() {
        currentBookingShortDto.setItemId(null);
        currentBookingShortDto.setStart(null);
        currentBookingShortDto.setEnd(null);
        Exception exceptionWrongValidation = assertThrows(WrongDataException.class,
                () -> bookingService.addBooking(booker.getId(), currentBookingShortDto));
        assertEquals("Ошибка валидации бронирования: " +
                "Не указан id вещи! " +
                "Не указана дата начала бронирования! " +
                "Не указана дата окончания бронирования! ", exceptionWrongValidation.getMessage());
    }

    @Test
    void addBookingWrongDate() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));
        when(bookingRepository.save(any())).then(Mockito.CALLS_REAL_METHODS);
        when(bookingDtoMapper.toDto(any())).then(Mockito.CALLS_REAL_METHODS);
        when(bookingDtoMapper.toBooking(any(), any(), any())).thenReturn(currentBooking);
        Exception exception = assertThrows(WrongDataException.class, () -> bookingService.addBooking(2L, currentBookingShortDto));
        assertEquals("Дата начала аренды уже прошла", exception.getMessage());

        currentBookingShortDto.setEnd(LocalDateTime.now().minus(1, ChronoUnit.DAYS).toString());
        currentBookingShortDto.setStart(LocalDateTime.now().plus(2, ChronoUnit.DAYS).toString());
        Exception exceptionWrongStartDate = assertThrows(WrongDataException.class,
                () -> bookingService.addBooking(2L, currentBookingShortDto));
        assertEquals("Дата начала аренды уже прошла", exceptionWrongStartDate.getMessage());
    }

    @Test
    void addBookingFromOwner() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));
        when(bookingRepository.save(any())).then(Mockito.CALLS_REAL_METHODS);
        when(bookingDtoMapper.toDto(any())).then(Mockito.CALLS_REAL_METHODS);
        when(bookingDtoMapper.toBooking(any(), any(), any())).thenReturn(futureBooking);
        Exception exception = assertThrows(NotFoundException.class, () -> bookingService.addBooking(1L, futureBookingShortDto));
        assertEquals(exception.getMessage(), "Забронировать собственную вещь нельзя! ");
    }

    @Test
    void updateBooking() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(futureBooking));
        when(bookingDtoMapper.toDto(any())).then(Mockito.CALLS_REAL_METHODS);
        BookingDto testBookingDto = bookingService.updateBooking(1L, 1L, true);
        assertEquals(BookingStatus.APPROVED.toString(), testBookingDto.getStatus());

        Exception exception = assertThrows(WrongDataException.class,
                () -> bookingService.updateBooking(3L, 1L, true));
        assertEquals("Статус аренды уже подтвержден", exception.getMessage());
    }

    @Test
    void updateBookingWrongUser() throws Exception {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(futureBooking));
        when(bookingDtoMapper.toDto(any())).then(Mockito.CALLS_REAL_METHODS);
        Exception exception = assertThrows(
                NotFoundException.class, () -> bookingService.updateBooking(1L, 2L, true));
        assertEquals("Пользователь id=2 не владелец вещи id=" + futureBooking.getItem().getId()
                + ". Подтвердить или отклонить аренду нельзя!", exception.getMessage());
    }

    @Test
    void getBooking() {
        when(userRepository.findById(eq(owner.getId()))).thenReturn(Optional.of(owner));
        when(itemRepository.findById(eq(item1.getId()))).thenReturn(Optional.of(item1));
        when(bookingRepository.findById(eq(currentBooking.getId()))).thenReturn(Optional.of(currentBooking));
        when(bookingDtoMapper.toDto(any())).thenCallRealMethod();
        BookingDto testBooking = bookingService.getBooking(1L, 2L);
        assertEquals(currentBooking.getId(), testBooking.getId());
    }

    @Test
    void getBookingsOfUserWitStateNullWithoutPaging() {
        when(userRepository.findById(eq(owner.getId()))).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerId(owner.getId())).thenReturn(List.of(item1));
        when(bookingRepository.findAllByUserId(any())).thenReturn(List.of(pastBooking));
        when(bookingDtoMapper.toDto(any())).then(CALLS_REAL_METHODS);
        List<BookingDto> testAllBookings = bookingService.getBookingsOfUser(owner.getId(), null, 1, null);
        assertEquals(pastBooking.getId(), testAllBookings.get(0).getId());
    }

    @Test
    void getBookingsOfUserWitStateAllWithoutPaging() {
        when(userRepository.findById(eq(owner.getId()))).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerId(owner.getId())).thenReturn(List.of(item1));
        when(bookingRepository.findAllByUserId(any())).thenReturn(List.of(pastBooking));
        when(bookingDtoMapper.toDto(any())).then(CALLS_REAL_METHODS);
        List<BookingDto> testAllBookings = bookingService.getBookingsOfUser(owner.getId(), "ALL", null, 10);
        assertEquals(pastBooking.getId(), testAllBookings.get(0).getId());
    }

    @Test
    void getBookingsOfUserWitStatePastWithoutPaging() {
        when(userRepository.findById(eq(owner.getId()))).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerId(owner.getId())).thenReturn(List.of(item1));
        when(bookingRepository.findAllPastByUserId(any())).thenReturn(List.of(pastBooking));
        when(bookingDtoMapper.toDto(any())).then(CALLS_REAL_METHODS);
        List<BookingDto> testPastBookings = bookingService.getBookingsOfUser(owner.getId(), "PAST", null, null);
        assertEquals(pastBooking.getId(), testPastBookings.get(0).getId());
    }

    @Test
    void getBookingsOfUserWitStateRejectedWithoutPaging() {
        when(userRepository.findById(eq(owner.getId()))).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerId(owner.getId())).thenReturn(List.of(item1));
        when(bookingRepository.findAllRejectedByUserId(any())).thenReturn(List.of(pastBooking));
        when(bookingDtoMapper.toDto(any())).then(CALLS_REAL_METHODS);
        List<BookingDto> testRejectedBookings = bookingService.getBookingsOfUser(owner.getId(), "REJECTED", null, null);
        assertEquals(pastBooking.getId(), testRejectedBookings.get(0).getId());
    }

    @Test
    void getBookingsOfUserWitStateFutureWithoutPaging() {
        when(userRepository.findById(eq(owner.getId()))).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerId(owner.getId())).thenReturn(List.of(item1));
        when(bookingRepository.findAllFutureByUserId(any())).thenReturn(List.of(futureBooking));
        when(bookingDtoMapper.toDto(any())).then(CALLS_REAL_METHODS);
        futureBooking.setStatus(BookingStatus.APPROVED);
        List<BookingDto> testFutureBookings = bookingService.getBookingsOfUser(owner.getId(), "FUTURE", null, null);
        assertEquals(futureBooking.getId(), testFutureBookings.get(0).getId());
    }

    @Test
    void getBookingsOfUserWitStateCurrentWithoutPaging() {
        when(userRepository.findById(eq(owner.getId()))).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerId(owner.getId())).thenReturn(List.of(item1));
        when(bookingRepository.findAllCurrentByUserId(any())).thenReturn(List.of(currentBooking));
        when(bookingDtoMapper.toDto(any())).then(CALLS_REAL_METHODS);
        List<BookingDto> testCurrentBookings = bookingService.getBookingsOfUser(owner.getId(), "CURRENT", null, null);
        assertEquals(currentBooking.getId(), testCurrentBookings.get(0).getId());
    }

    @Test
    void getBookingsOfUserWitStateWaitingWithoutPaging() {
        when(userRepository.findById(eq(owner.getId()))).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerId(owner.getId())).thenReturn(List.of(item1));
        when(bookingRepository.findAllWaitingByUserId(any())).thenReturn(List.of(futureBooking));
        when(bookingDtoMapper.toDto(any())).thenReturn(futureBookingDto);
        List<BookingDto> testWaitingBookings = bookingService.getBookingsOfUser(owner.getId(), "WAITING", null, null);
        assertEquals(futureBooking.getId(), testWaitingBookings.get(0).getId());
    }

    @Test
    void getBookingsOfUserWitStateNullWithPaging() {
        when(userRepository.findById(eq(owner.getId()))).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerId(owner.getId())).thenReturn(List.of(item1));
        when(bookingRepository.findAllByUserId(any(), eq(PageRequest.of(1 / 2, 2)))).thenReturn(List.of(pastBooking));
        when(bookingDtoMapper.toDto(any())).then(CALLS_REAL_METHODS);
        List<BookingDto> testAllBookingsPaged = bookingService.getBookingsOfUser(owner.getId(), null, 1, 2);
        assertEquals(pastBooking.getId(), testAllBookingsPaged.get(0).getId());
    }

    @Test
    void getBookingsOfUserWitStateAllWithPaging() {
        when(userRepository.findById(eq(owner.getId()))).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerId(owner.getId())).thenReturn(List.of(item1));
        when(bookingRepository.findAllByUserId(any(), eq(PageRequest.of(1 / 2, 2)))).thenReturn(List.of(pastBooking));
        when(bookingDtoMapper.toDto(any())).then(CALLS_REAL_METHODS);
        List<BookingDto> testAllBookingsPaged = bookingService.getBookingsOfUser(owner.getId(), "ALL", 1, 2);
        assertEquals(pastBooking.getId(), testAllBookingsPaged.get(0).getId());
    }

    @Test
    void getBookingsOfUserWitStatePastWithPaging() {
        when(userRepository.findById(eq(owner.getId()))).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerId(owner.getId())).thenReturn(List.of(item1));
        when(bookingRepository.findAllPastByUserId(any(), eq(PageRequest.of(1 / 2, 2)))).thenReturn(List.of(pastBooking));
        when(bookingDtoMapper.toDto(any())).then(CALLS_REAL_METHODS);
        List<BookingDto> testPastBookingsPaged = bookingService.getBookingsOfUser(owner.getId(), "PAST", 1, 2);
        assertEquals(pastBooking.getId(), testPastBookingsPaged.get(0).getId());
    }

    @Test
    void getBookingsOfUserWitStateRejectedWithPaging() {
        when(userRepository.findById(eq(owner.getId()))).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerId(owner.getId())).thenReturn(List.of(item1));
        when(bookingRepository.findAllRejectedByUserId(any(), eq(PageRequest.of(1 / 2, 2)))).thenReturn(List.of(pastBooking));
        when(bookingDtoMapper.toDto(any())).then(CALLS_REAL_METHODS);
        List<BookingDto> testRejectedBookingsPaged = bookingService.getBookingsOfUser(owner.getId(), "REJECTED", 1, 2);
        assertEquals(pastBooking.getId(), testRejectedBookingsPaged.get(0).getId());
    }

    @Test
    void getBookingsOfUserWitStateFutureWithPaging() {
        when(userRepository.findById(eq(owner.getId()))).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerId(owner.getId())).thenReturn(List.of(item1));
        when(bookingRepository.findAllFutureByUserId(any(), eq(PageRequest.of(1 / 2, 2)))).thenReturn(List.of(futureBooking));
        when(bookingDtoMapper.toDto(any())).then(CALLS_REAL_METHODS);
        futureBooking.setStatus(BookingStatus.APPROVED);
        List<BookingDto> testFutureBookingsPaged = bookingService.getBookingsOfUser(owner.getId(), "FUTURE", 1, 2);
        assertEquals(futureBooking.getId(), testFutureBookingsPaged.get(0).getId());
    }

    @Test
    void getBookingsOfUserWitStateCurrentWithPaging() {
        when(userRepository.findById(eq(owner.getId()))).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerId(owner.getId())).thenReturn(List.of(item1));
        when(bookingRepository.findAllCurrentByUserId(any(), eq(PageRequest.of(1 / 2, 2)))).thenReturn(List.of(currentBooking));
        when(bookingDtoMapper.toDto(any())).then(CALLS_REAL_METHODS);
        List<BookingDto> testCurrentBookingsPaged = bookingService.getBookingsOfUser(owner.getId(), "CURRENT", 1, 2);
        assertEquals(currentBooking.getId(), testCurrentBookingsPaged.get(0).getId());
    }

    @Test
    void getBookingsOfUserWitStateWaitingWithPaging() {
        when(userRepository.findById(eq(owner.getId()))).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerId(owner.getId())).thenReturn(List.of(item1));
        when(bookingRepository.findAllWaitingByUserId(any(), eq(PageRequest.of(1 / 2, 2)))).thenReturn(List.of(futureBooking));
        when(bookingDtoMapper.toDto(any())).thenReturn(futureBookingDto);
        List<BookingDto> testWaitingBookingsPaged = bookingService.getBookingsOfUser(owner.getId(), "WAITING", 1, 2);
        assertEquals(futureBooking.getId(), testWaitingBookingsPaged.get(0).getId());
    }

    @Test
    void getBookingsOfUserWrongState() {
        when(userRepository.findById(eq(owner.getId()))).thenReturn(Optional.of(owner));
        Exception exceptionNoPagedMethod = assertThrows(WrongDataException.class,
                () -> bookingService.getBookingsOfUser(owner.getId(), "UNKNOWN", null, null));
        assertEquals("Unknown state: UNSUPPORTED_STATUS", exceptionNoPagedMethod.getMessage());
    }

    @Test
    void getBookingsOfUserWrongStateWithPaging() {
        when(userRepository.findById(eq(owner.getId()))).thenReturn(Optional.of(owner));
        Exception exceptionPagedMethod = assertThrows(WrongDataException.class,
                () -> bookingService.getBookingsOfUser(owner.getId(), "UNKNOWN", 1, 2));
        assertEquals("Unknown state: UNSUPPORTED_STATUS", exceptionPagedMethod.getMessage());
    }

    @Test
    void getBookingsOfUserWrongPaging() {
    when(userRepository.findById(eq(owner.getId()))).thenReturn(Optional.of(owner));
    Exception exceptionWrongPage = assertThrows(WrongDataException.class,
            () -> bookingService.getBookingsOfUser(owner.getId(), "ALL", 0, 1));
    assertEquals("Ошибка: неверно указан начальный индекс или размер страницы", exceptionWrongPage.getMessage());
    }

    @Test
    void getAllBookingsOfOwnerItems() {
        when(userRepository.findById(eq(owner.getId()))).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerId(owner.getId())).thenReturn(List.of(item1));
        when(bookingRepository.findAllBookingsForItems(any())).thenReturn(List.of(pastBooking));
        when(bookingDtoMapper.toDto(any())).then(CALLS_REAL_METHODS);
        List<BookingDto> testAllBookings = bookingService.getBookingsOfOwnerItems(owner.getId(), "ALL", null, null);
        assertEquals(pastBooking.getId(), testAllBookings.get(0).getId());
    }

    @Test
    void getPastBookingsOfOwnerItems() {
        when(userRepository.findById(eq(owner.getId()))).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerId(owner.getId())).thenReturn(List.of(item1));
        when(bookingRepository.findPastBookingsForItems(any())).thenReturn(List.of(pastBooking));
        when(bookingDtoMapper.toDto(any())).then(CALLS_REAL_METHODS);
        List<BookingDto> testPastBookings = bookingService.getBookingsOfOwnerItems(owner.getId(), "PAST", null, null);
        assertEquals(pastBooking.getId(), testPastBookings.get(0).getId());
    }

    @Test
    void getRejectedBookingsOfOwnerItems() {
        when(userRepository.findById(eq(owner.getId()))).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerId(owner.getId())).thenReturn(List.of(item1));
        when(bookingRepository.findRejectedBookingsForItems(any())).thenReturn(List.of(pastBooking));
        when(bookingDtoMapper.toDto(any())).then(CALLS_REAL_METHODS);
        List<BookingDto> testRejectedBookings = bookingService.getBookingsOfOwnerItems(owner.getId(), "REJECTED", null, null);
        assertEquals(pastBooking.getId(), testRejectedBookings.get(0).getId());
    }

    @Test
    void getFutureBookingsOfOwnerItems() {
        when(userRepository.findById(eq(owner.getId()))).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerId(owner.getId())).thenReturn(List.of(item1));
        when(bookingRepository.findFutureBookingsForItems(any())).thenReturn(List.of(futureBooking));
        when(bookingDtoMapper.toDto(any())).then(CALLS_REAL_METHODS);
        futureBooking.setStatus(BookingStatus.APPROVED);
        List<BookingDto> testFutureBookings = bookingService.getBookingsOfOwnerItems(owner.getId(), "FUTURE", null, null);
        assertEquals(futureBooking.getId(), testFutureBookings.get(0).getId());
    }

    @Test
    void getCurrentBookingsOfOwnerItems() {
        when(userRepository.findById(eq(owner.getId()))).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerId(owner.getId())).thenReturn(List.of(item1));
        when(bookingRepository.findCurrentBookingsForItems(any())).thenReturn(List.of(currentBooking));
        when(bookingDtoMapper.toDto(any())).then(CALLS_REAL_METHODS);
        List<BookingDto> testCurrentBookings = bookingService.getBookingsOfOwnerItems(owner.getId(), "CURRENT", null, null);
        assertEquals(currentBooking.getId(), testCurrentBookings.get(0).getId());
    }

    @Test
    void getWaitingBookingsOfOwnerItems() {
        when(userRepository.findById(eq(owner.getId()))).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerId(owner.getId())).thenReturn(List.of(item1));
        when(bookingRepository.findWaititngBookingsForItems(any())).thenReturn(List.of(futureBooking));
        when(bookingDtoMapper.toDto(any())).thenReturn(futureBookingDto);
        List<BookingDto> testWaitingBookings = bookingService.getBookingsOfOwnerItems(owner.getId(), "WAITING", null, null);
        assertEquals(futureBooking.getId(), testWaitingBookings.get(0).getId());
    }

    @Test
    void getAllBookingsOfOwnerItemsWithPaging() {
        when(userRepository.findById(eq(owner.getId()))).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerId(owner.getId())).thenReturn(List.of(item1));
        when(bookingRepository.findAllBookingsForItems(any(), eq(PageRequest.of(1, 2)))).thenReturn(List.of(pastBooking));
        when(bookingDtoMapper.toDto(any())).then(CALLS_REAL_METHODS);
        List<BookingDto> testAllBookingsPaged = bookingService.getBookingsOfOwnerItems(owner.getId(), "ALL", 1, 2);
        assertEquals(pastBooking.getId(), testAllBookingsPaged.get(0).getId());
    }

    @Test
    void getPastBookingsOfOwnerItemsWithPaging() {
        when(userRepository.findById(eq(owner.getId()))).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerId(owner.getId())).thenReturn(List.of(item1));
        when(bookingRepository.findPastBookingsForItems(any(), eq(PageRequest.of(1, 2)))).thenReturn(List.of(pastBooking));
        when(bookingDtoMapper.toDto(any())).then(CALLS_REAL_METHODS);
        List<BookingDto> testPastBookingsPaged = bookingService.getBookingsOfOwnerItems(owner.getId(), "PAST", 1, 2);
        assertEquals(pastBooking.getId(), testPastBookingsPaged.get(0).getId());
    }

    @Test
    void getRefectedBookingsOfOwnerItemsWithPaging() {
        when(userRepository.findById(eq(owner.getId()))).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerId(owner.getId())).thenReturn(List.of(item1));
        when(bookingRepository.findRejectedBookingsForItems(any(), eq(PageRequest.of(1, 2)))).thenReturn(List.of(pastBooking));
        when(bookingDtoMapper.toDto(any())).then(CALLS_REAL_METHODS);
        List<BookingDto> testRejectedBookingsPaged = bookingService.getBookingsOfOwnerItems(owner.getId(), "REJECTED", 1, 2);
        assertEquals(pastBooking.getId(), testRejectedBookingsPaged.get(0).getId());
    }

    @Test
    void getFutureBookingsOfOwnerItemsWithPaging() {
        when(userRepository.findById(eq(owner.getId()))).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerId(owner.getId())).thenReturn(List.of(item1));
        when(bookingRepository.findFutureBookingsForItems(any(), eq(PageRequest.of(1, 2)))).thenReturn(List.of(futureBooking));
        when(bookingDtoMapper.toDto(any())).then(CALLS_REAL_METHODS);
        futureBooking.setStatus(BookingStatus.APPROVED);
        List<BookingDto> testFutureBookingsPaged = bookingService.getBookingsOfOwnerItems(owner.getId(), "FUTURE", 1, 2);
        assertEquals(futureBooking.getId(), testFutureBookingsPaged.get(0).getId());
    }

    @Test
    void getCurrentBookingsOfOwnerItemsWithPaging() {
        when(userRepository.findById(eq(owner.getId()))).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerId(owner.getId())).thenReturn(List.of(item1));
        when(bookingRepository.findCurrentBookingsForItems(any(), eq(PageRequest.of(1, 2)))).thenReturn(List.of(currentBooking));
        when(bookingDtoMapper.toDto(any())).then(CALLS_REAL_METHODS);
        List<BookingDto> testCurrentBookingsPaged = bookingService.getBookingsOfOwnerItems(owner.getId(), "CURRENT", 1, 2);
        assertEquals(currentBooking.getId(), testCurrentBookingsPaged.get(0).getId());
    }

    @Test
    void getBookingsOfOwnerItems() {
        when(userRepository.findById(eq(owner.getId()))).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerId(owner.getId())).thenReturn(List.of(item1));
        when(bookingRepository.findWaititngBookingsForItems(any(), eq(PageRequest.of(1, 2)))).thenReturn(List.of(futureBooking));
        when(bookingDtoMapper.toDto(any())).thenReturn(futureBookingDto);
        List<BookingDto> testWaitingBookingsPaged = bookingService.getBookingsOfOwnerItems(owner.getId(), "WAITING", 1, 2);
        assertEquals(futureBooking.getId(), testWaitingBookingsPaged.get(0).getId());
    }

    @Test
    void getBookingsOfOwnerItemsWithWrongState() {
        when(userRepository.findById(eq(owner.getId()))).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerId(owner.getId())).thenReturn(List.of(item1));
        when(bookingDtoMapper.toDto(any())).then(CALLS_REAL_METHODS);
        Exception exceptionNoPagedMethod = assertThrows(WrongDataException.class,
                () -> bookingService.getBookingsOfOwnerItems(owner.getId(), "UNKNOWN", null, null));
        assertEquals("Unknown state: UNSUPPORTED_STATUS", exceptionNoPagedMethod.getMessage());
    }

    @Test
    void getBookingsOfOwnerItemsWithWrongStateWithPaging() {
        when(userRepository.findById(eq(owner.getId()))).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerId(owner.getId())).thenReturn(List.of(item1));
        Exception exceptionPagedMethod = assertThrows(WrongDataException.class,
                () -> bookingService.getBookingsOfOwnerItems(owner.getId(), "UNKNOWN", 1, 2));
        assertEquals("Unknown state: UNSUPPORTED_STATUS", exceptionPagedMethod.getMessage());

    }

    @Test
    void getBookingsOfOwnerItemsWrongPaging() {
        when(userRepository.findById(eq(owner.getId()))).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerId(owner.getId())).thenReturn(List.of(item1));
        Exception exceptionWrongPage = assertThrows(WrongDataException.class,
                () -> bookingService.getBookingsOfOwnerItems(owner.getId(), "ALL", 0, 1));
        assertEquals("Ошибка: неверно указан начальный индекс или размер страницы", exceptionWrongPage.getMessage());
    }
}