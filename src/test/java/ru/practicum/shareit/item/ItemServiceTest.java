package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.WrongDataException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class ItemServiceTest {

    @Mock
    ItemRepository itemRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    BookingRepository bookingRepository;

    @Mock
    CommentRepository commentRepository;

    @Mock
    ItemRequestRepository requestRepository;

    @Mock
    ItemDtoMapper itemDtoMapper;

    @Mock
    BookingDtoMapper bookingDtoMapper;

    @Mock
    CommentDtoMapper commentDtoMapper;

    @InjectMocks
    ItemService itemService;

    private User owner;
    private Item item1;
    private Item item2;
    private ItemDto itemDto;
    private Comment comment1;
    private Comment comment2;
    private CommentDto commentDto;
    private ItemRequest itemRequest;
    private Booking lastBooking;
    private Booking nextBooking;
    private BookingDto lastBookingDto;
    private BookingDto nextBookingDto;
    private List<Comment> comments;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);
        owner.setName("owner");
        owner.setEmail("owner@email.ru");

        item1 = new Item();
        item1.setId(1L);
        item1.setOwnerId(1L);
        item1.setName("item 1");
        item1.setDescription("item 1 description");
        item1.setAvailable(true);

        item2 = new Item();
        item2.setId(2L);
        item2.setOwnerId(1L);
        item2.setName("item 2");
        item2.setDescription("item 2 description");
        item2.setAvailable(true);

        itemDto = new ItemDto();
        itemDto.setName("itemDto");
        itemDto.setDescription("dto description");
        itemDto.setAvailable(true);

        commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("comment DTO text");

        comment1 = new Comment();
        comment1.setId(1L);
        comment1.setItemId(1L);
        comment1.setText("comment text");
        comment1.setAuthorId(owner.getId());
        comment1.setAuthorName(owner.getName());
        comment1.setCreated(LocalDateTime.now());

        comment2 = new Comment();
        comment2.setId(2L);
        comment2.setItemId(1L);
        comment2.setText("comment2 text");
        comment2.setAuthorId(owner.getId());
        comment2.setAuthorName(owner.getName());
        comment2.setCreated(LocalDateTime.now());

        comments = new ArrayList<>();
        comments.add(comment1);
        comments.add(comment2);

        itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setItems(new ArrayList<Item>());
        itemRequest.setRequestorId(owner.getId());
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setDescription("item request description");

        lastBooking = new Booking();
        lastBooking.setId(1L);
        lastBooking.setItem(item1);
        lastBooking.setBooker(owner);
        lastBooking.setStatus(BookingStatus.APPROVED);
        lastBooking.setStart(LocalDateTime.now().minus(1, ChronoUnit.DAYS));
        lastBooking.setEnd(LocalDateTime.now().minus(2,ChronoUnit.DAYS));

        nextBooking = new Booking();
        nextBooking.setId(2L);
        nextBooking.setItem(item1);
        nextBooking.setBooker(owner);
        nextBooking.setStatus(BookingStatus.APPROVED);
        nextBooking.setStart(LocalDateTime.now().plus(1, ChronoUnit.DAYS));
        nextBooking.setEnd(LocalDateTime.now().plus(2, ChronoUnit.DAYS));

        lastBookingDto = new BookingDto();
        lastBookingDto.setId(1L);
        lastBookingDto.setItem(item1);
        lastBookingDto.setBooker(owner);
        lastBookingDto.setStatus(BookingStatus.APPROVED.toString());
        lastBookingDto.setStart(lastBooking.getStart().toString());
        lastBookingDto.setEnd(lastBooking.getEnd().toString());

        nextBookingDto = new BookingDto();
        nextBookingDto.setId(2L);
        nextBookingDto.setItem(item1);
        nextBookingDto.setBooker(owner);
        nextBookingDto.setStatus(BookingStatus.APPROVED.toString());
        nextBookingDto.setStart(nextBooking.getStart().toString());
        nextBookingDto.setEnd(nextBooking.getEnd().toString());
    }

    @Test
    void getItemsByUserId() {
        when(userRepository.findById(any())).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerId(anyLong())).thenReturn(List.of(item1, item2));
        when(itemDtoMapper.mapToDto(any())).then(Mockito.CALLS_REAL_METHODS);
        when(itemDtoMapper.mapToItem(any())).then(Mockito.CALLS_REAL_METHODS);
        when(bookingRepository.findNextBookingForItem(eq(1L))).thenReturn(Optional.of(nextBooking));
        when(bookingRepository.findLastBookingForItem(eq(1L))).thenReturn(Optional.of(lastBooking));
        when(commentRepository.findAllByItemId(any())).thenReturn(comments);
        List<ItemDto> itemDtoList = itemService.getItemsByUserId(owner.getId(), null, null);
        assertEquals(2, itemDtoList.size());
        assertEquals(item1.getId(), itemDtoList.get(0).getId());
        assertEquals(item2.getDescription(), itemDtoList.get(1).getDescription());
    }

    @Test
    void getItemsByUserIdWithPaging() {
        when(userRepository.findById(any())).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerId(anyLong(), any())).thenReturn(List.of(item1, item2));
        when(itemDtoMapper.mapToDto(any())).then(Mockito.CALLS_REAL_METHODS);
        when(itemDtoMapper.mapToItem(any())).then(Mockito.CALLS_REAL_METHODS);
        when(bookingRepository.findNextBookingForItem(eq(1L))).thenReturn(Optional.of(nextBooking));
        when(bookingRepository.findLastBookingForItem(eq(1L))).thenReturn(Optional.of(lastBooking));
        when(commentRepository.findAllForItems(any())).thenReturn(comments);
        List<ItemDto> itemDtoListPageable = itemService.getItemsByUserId(owner.getId(), 1, 10);
        assertEquals(2, itemDtoListPageable.size());
        assertEquals(item1.getId(), itemDtoListPageable.get(0).getId());
        assertEquals(item2.getDescription(), itemDtoListPageable.get(1).getDescription());
    }

    @Test
    void addItem() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRepository.save(any())).thenReturn(item1);
        when(requestRepository.findById(eq(1L))).thenReturn(Optional.of(itemRequest));
        when(requestRepository.save(any())).thenReturn(itemRequest);
        when(itemDtoMapper.mapToDto(any())).then(Mockito.CALLS_REAL_METHODS);
        when(itemDtoMapper.mapToItem(any())).then(Mockito.CALLS_REAL_METHODS);
        ItemDto testItemDto = itemService.addItem(owner.getId(), itemDto);
        assertEquals(item1.getId(), testItemDto.getId());
        assertEquals(item1.getDescription(), testItemDto.getDescription());

        assertThrows(NotFoundException.class, () -> itemService.addItem(99L, itemDto));

        itemDto.setName("");
        assertThrows(WrongDataException.class, () -> itemService.addItem(owner.getId(), itemDto));

        itemDto.setName("newName");
        itemDto.setDescription("");
        assertThrows(WrongDataException.class, () -> itemService.addItem(owner.getId(), itemDto));

        itemDto.setDescription("new item dto description");
        itemDto.setRequestId(1L);
        ItemDto itemDtoForRequest = itemService.addItem(owner.getId(), itemDto);
        assertEquals(item1.getName(), itemDtoForRequest.getName());
    }

    @Test
    void getItemById() {
        when(userRepository.findById(any())).thenReturn(Optional.of(owner));
        when(itemRepository.findById(item1.getId())).thenReturn(Optional.of(item1));
        when(itemRepository.findById(item2.getId())).thenReturn(Optional.of(item2));
        when(itemDtoMapper.mapToDto(any())).then(Mockito.CALLS_REAL_METHODS);
        when(itemDtoMapper.mapToItem(any())).then(Mockito.CALLS_REAL_METHODS);
        when(bookingRepository.findNextBookingForItem(eq(1L))).thenReturn(Optional.of(nextBooking));
        when(bookingRepository.findLastBookingForItem(eq(1L))).thenReturn(Optional.of(lastBooking));
        when(commentRepository.findAllByItemId(any())).thenReturn(comments);
        ItemDto itemDto1 = itemService.getItemById(item1.getId(), owner.getId());
        ItemDto itemDto2 = itemService.getItemById(item2.getId(), owner.getId());
        assertEquals(item1.getName(), itemDto1.getName());
        assertEquals(item2.getName(), itemDto2.getName());

        Exception exception = assertThrows(NotFoundException.class,
                () -> itemService.getItemById(99L, owner.getId()));
        assertEquals("Вещь id=99 не найдена", exception.getMessage());

    }

    @Test
    void getItemByIdAndOwnerId() {
        when(userRepository.findById(any())).thenReturn(Optional.of(owner));
        when(itemRepository.findByIdAndOwnerId(owner.getId(), item1.getId())).thenReturn(Optional.of(item1));
        when(itemRepository.findByIdAndOwnerId(owner.getId(), item2.getId())).thenReturn(Optional.of(item2));
        when(itemDtoMapper.mapToDto(any())).then(Mockito.CALLS_REAL_METHODS);
        when(itemDtoMapper.mapToItem(any())).then(Mockito.CALLS_REAL_METHODS);
        when(bookingRepository.findNextBookingForItem(eq(1L))).thenReturn(Optional.of(nextBooking));
        when(bookingRepository.findLastBookingForItem(eq(1L))).thenReturn(Optional.of(lastBooking));
        when(commentRepository.findAllByItemId(any())).thenReturn(comments);
        when(commentRepository.findAllForItems(any())).thenReturn(comments);
        ItemDto itemDto1 = itemService.getItemByIdAndOwnerId(owner.getId(), item1.getId());
        ItemDto itemDto2 = itemService.getItemByIdAndOwnerId(owner.getId(), item2.getId());
        assertEquals(item1.getName(), itemDto1.getName());
        assertEquals(item2.getName(), itemDto2.getName());
        Exception exception = assertThrows(NotFoundException.class,
                () -> itemService.getItemByIdAndOwnerId(owner.getId(), 99L));
        assertEquals("Вещь с id=99 не найдена у пользователя id=" + owner.getId(), exception.getMessage());
    }

    @Test
    void searchItemsByText() {
        when(userRepository.findById(any())).thenReturn(Optional.of(owner));
        when(itemRepository.findByText(item1.getName())).thenReturn(List.of(item1));
        when(itemRepository.findByTextPageable(item2.getDescription(), PageRequest.of(1, 10))).thenReturn(List.of(item2));
        when(itemDtoMapper.mapToDto(any())).then(Mockito.CALLS_REAL_METHODS);
        when(itemDtoMapper.mapToItem(any())).then(Mockito.CALLS_REAL_METHODS);
        when(bookingRepository.findNextBookingForItem(eq(1L))).thenReturn(Optional.of(nextBooking));
        when(bookingRepository.findLastBookingForItem(eq(1L))).thenReturn(Optional.of(lastBooking));
        when(commentRepository.findAllByItemId(any())).thenReturn(comments);
        when(commentRepository.findAllForItems(any())).thenReturn(comments);
        List<ItemDto> itemDtoList1 = itemService.searchItemsByText(owner.getId(), item1.getName(), null, null);
        List<ItemDto> itemDtoList2 = itemService.searchItemsByText(owner.getId(), item2.getDescription(), 1, 10);
        assertEquals(item1.getDescription(), itemDtoList1.get(0).getDescription());
        assertEquals(item2.getName(), itemDtoList2.get(0).getName());
        List<ItemDto> itemDtoListWithEmptyTextRequest = itemService.searchItemsByText(owner.getId(), "", null, null);
        assertEquals(0, itemDtoListWithEmptyTextRequest.size());
    }

    @Test
    void updateItem() {
        when(userRepository.findById(any())).thenReturn(Optional.of(owner));
        when(itemRepository.findByIdAndOwnerId(owner.getId(), item1.getId())).thenReturn(Optional.of(item1));
        when(itemRepository.save(any())).thenReturn(item1);
        when(itemDtoMapper.mapToDto(any())).then(Mockito.CALLS_REAL_METHODS);
        when(itemDtoMapper.mapToItem(any())).then(Mockito.CALLS_REAL_METHODS);
        when(bookingRepository.findNextBookingForItem(eq(1L))).thenReturn(Optional.of(nextBooking));
        when(bookingRepository.findLastBookingForItem(eq(1L))).thenReturn(Optional.of(lastBooking));
        when(commentRepository.findAllByItemId(any())).thenReturn(comments);
        when(commentRepository.findAllForItems(any())).thenReturn(comments);
        ItemDto testItemDto = itemService.updateItem(owner.getId(), item1.getId(), itemDto);
        assertEquals(item1.getName(), testItemDto.getName());
    }

    @Test
    void addNewComment() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setItem(item1);
        booking.setStart(LocalDateTime.now().minus(1, ChronoUnit.DAYS));
        Comment editedComment = new Comment();
        editedComment.setId(comment1.getId());
        editedComment.setItemId(comment1.getItemId());
        editedComment.setCreated(comment1.getCreated());
        editedComment.setAuthorId(comment1.getAuthorId());
        editedComment.setAuthorName(comment1.getAuthorName());
        editedComment.setText(commentDto.getText());
        when(commentRepository.save(any())).thenReturn(editedComment);
        when(commentRepository.findAllByItemId(any())).thenReturn(List.of(comment1));
        when(commentRepository.findAllForItems(any())).thenReturn(new ArrayList<Comment>());
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(userRepository.findById(eq(404L))).thenReturn(Optional.empty());
        when(itemRepository.findById(item1.getId())).thenReturn(Optional.of(item1));
        when(bookingRepository.findAllByUserId(owner.getId())).thenReturn(List.of(booking));
        when(bookingRepository.findAllByUserId(eq(99L))).thenReturn(new ArrayList<>());
        when(itemDtoMapper.mapToDto(any())).then(Mockito.CALLS_REAL_METHODS);
        when(itemDtoMapper.mapToItem(any())).then(Mockito.CALLS_REAL_METHODS);
        when(commentDtoMapper.mapToDto(any())).then(Mockito.CALLS_REAL_METHODS);
        when(bookingRepository.findNextBookingForItem(eq(1L))).thenReturn(Optional.of(nextBooking));
        when(bookingRepository.findLastBookingForItem(eq(1L))).thenReturn(Optional.of(lastBooking));
        CommentDto testCommentDto = itemService.addNewComment(owner.getId(), item1.getId(), commentDto);
        assertEquals(editedComment.getText(), testCommentDto.getText());

        Exception exceptionWrongUser = assertThrows(NotFoundException.class,
                () -> itemService.addNewComment(404L, item1.getId(), commentDto));
        assertEquals("Пользователь с id=404 не найден", exceptionWrongUser.getMessage());

        Exception exception = assertThrows(WrongDataException.class,
                () -> itemService.addNewComment(99L, item1.getId(), commentDto));
        assertEquals("Пользователь не брал вещь id=1 в аренду", exception.getMessage());

        Exception exceptionWrongItem = assertThrows(NotFoundException.class,
                () -> itemService.addNewComment(owner.getId(), 3L, commentDto));
        assertEquals("Вещь id=3 не найдена", exceptionWrongItem.getMessage());

        commentDto.setText("");
        Exception exceptionWrongText = assertThrows(WrongDataException.class,
                () -> itemService.addNewComment(owner.getId(), item1.getId(), commentDto));
        assertEquals("Комментарий отсутствует", exceptionWrongText.getMessage());
    }
}