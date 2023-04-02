package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;
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
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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

    private Booking currentBooking = new Booking();
    private Booking futureBooking = new Booking();
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
        futureBooking.setId(1L);
//        futureBooking.setStatus(BookingStatus.APPROVED);
        futureBooking.setBooker(booker);
        futureBooking.setItem(item1);
        futureBooking.setStart(now.plus(1, ChronoUnit.DAYS));
        futureBooking.setEnd(now.plus(2, ChronoUnit.DAYS));

        currentBooking.setId(2L);
        currentBooking.setStatus(BookingStatus.APPROVED);
        currentBooking.setBooker(booker);
        currentBooking.setItem(item1);
        currentBooking.setStart(now.minus(1, ChronoUnit.DAYS));
        currentBooking.setEnd(now.plus(1, ChronoUnit.DAYS));

        futureBookingDto.setId(1L);
//        futureBookingDto.setStatus(BookingStatus.APPROVED.toString());
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
//        bookingShortDto.setId(1L);
//        bookingShortDto.setStatus(BookingStatus.APPROVED);
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
    void addBooking() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));
        when(bookingRepository.save(any())).thenReturn(futureBooking);
        when(bookingDtoMapper.toDto(any())).then(Mockito.CALLS_REAL_METHODS);
        when(bookingDtoMapper.toBooking(any(), any(), any())).thenReturn(futureBooking);
        BookingDto testBookingDto = bookingService.addBooking(2L, futureBookingShortDto);
        assertEquals(testBookingDto.getId(), futureBooking.getId());
    }

    @Test
    void addBookingWrongDate() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));
        when(bookingRepository.save(any())).then(Mockito.CALLS_REAL_METHODS);
        when(bookingDtoMapper.toDto(any())).then(Mockito.CALLS_REAL_METHODS);
        when(bookingDtoMapper.toBooking(any(), any(), any())).thenReturn(currentBooking);
        Exception exception = assertThrows(WrongDataException.class, () -> bookingService.addBooking(2L, currentBookingShortDto));
        assertEquals(exception.getMessage(), "Дата начала аренды уже прошла");
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
    }

    @Test
    void getBooking() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));
        when(bookingRepository.findById(any())).thenReturn(Optional.of(currentBooking));
        when(bookingDtoMapper.toDto(any())).thenCallRealMethod();
        BookingDto testBooking = bookingService.getBooking(1L, 2L);
        assertEquals(currentBooking.getId(), testBooking.getId());
    }

    @Test
    void getBookingsOfUser() {
    }

    @Test
    void getBookingsOfOwnerItems() {
    }
}