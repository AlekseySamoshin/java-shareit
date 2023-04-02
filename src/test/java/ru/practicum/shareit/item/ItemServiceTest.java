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
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.WrongDataException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
    private Comment comment;
    private CommentDto commentDto;

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

        comment = new Comment();
        comment.setId(1L);
        comment.setItemId(1L);
        comment.setText("comment text");
        comment.setAuthorId(owner.getId());
        comment.setAuthorName(owner.getName());
        comment.setCreated(LocalDateTime.now());
    }

    @Test
    void getItemsByUserId() {
        when(userRepository.findById(any())).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerId(anyLong())).thenReturn(List.of(item1, item2));
        when(itemRepository.findAllByOwnerId(anyLong(), any())).thenReturn(List.of(item1, item2));
        when(itemDtoMapper.mapToDto(any())).then(Mockito.CALLS_REAL_METHODS);
        when(itemDtoMapper.mapToItem(any())).then(Mockito.CALLS_REAL_METHODS);

        List<ItemDto> itemDtoList = itemService.getItemsByUserId(owner.getId());
        assertEquals(2, itemDtoList.size());
        assertEquals(item1.getId(), itemDtoList.get(0).getId());
        assertEquals(item2.getDescription(), itemDtoList.get(1).getDescription());

        List<ItemDto> itemDtoListPageable = itemService.getItemsByUserId(owner.getId(), 1, 10);
        assertEquals(2, itemDtoListPageable.size());
        assertEquals(item1.getId(), itemDtoListPageable.get(0).getId());
        assertEquals(item2.getDescription(), itemDtoListPageable.get(1).getDescription());
    }

    @Test
    void addItem() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRepository.save(any())).thenReturn(item1);
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

    }

    @Test
    void getItemById() {
        when(userRepository.findById(any())).thenReturn(Optional.of(owner));
        when(itemRepository.findById(item1.getId())).thenReturn(Optional.of(item1));
        when(itemRepository.findById(item2.getId())).thenReturn(Optional.of(item2));
        when(itemDtoMapper.mapToDto(any())).then(Mockito.CALLS_REAL_METHODS);
        when(itemDtoMapper.mapToItem(any())).then(Mockito.CALLS_REAL_METHODS);
        ItemDto itemDto1 = itemService.getItemById(item1.getId(), owner.getId());
        ItemDto itemDto2 = itemService.getItemById(item2.getId(), owner.getId());
        assertEquals(item1.getName(), itemDto1.getName());
        assertEquals(item2.getName(), itemDto2.getName());
    }

    @Test
    void getItemByIdAndOwnerId() {
        when(userRepository.findById(any())).thenReturn(Optional.of(owner));
        when(itemRepository.findByIdAndOwnerId(owner.getId(), item1.getId())).thenReturn(Optional.of(item1));
        when(itemRepository.findByIdAndOwnerId(owner.getId(), item2.getId())).thenReturn(Optional.of(item2));
        when(itemDtoMapper.mapToDto(any())).then(Mockito.CALLS_REAL_METHODS);
        when(itemDtoMapper.mapToItem(any())).then(Mockito.CALLS_REAL_METHODS);
        ItemDto itemDto1 = itemService.getItemByIdAndOwnerId(owner.getId(), item1.getId());
        ItemDto itemDto2 = itemService.getItemByIdAndOwnerId(owner.getId(), item2.getId());
        assertEquals(item1.getName(), itemDto1.getName());
        assertEquals(item2.getName(), itemDto2.getName());
    }

    @Test
    void searchItemsByText() {
        when(userRepository.findById(any())).thenReturn(Optional.of(owner));
        when(itemRepository.findByText(item1.getName())).thenReturn(List.of(item1));
        when(itemRepository.findByTextPageable(item2.getDescription(), PageRequest.of(1, 10))).thenReturn(List.of(item2));
        when(itemDtoMapper.mapToDto(any())).then(Mockito.CALLS_REAL_METHODS);
        when(itemDtoMapper.mapToItem(any())).then(Mockito.CALLS_REAL_METHODS);
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
        editedComment.setId(comment.getId());
        editedComment.setItemId(comment.getItemId());
        editedComment.setCreated(comment.getCreated());
        editedComment.setAuthorId(comment.getAuthorId());
        editedComment.setAuthorName(comment.getAuthorName());
        editedComment.setText(commentDto.getText());
        when(commentRepository.save(any())).thenReturn(editedComment);
        when(userRepository.findById(any())).thenReturn(Optional.of(owner));
        when(itemRepository.findById(item1.getId())).thenReturn(Optional.of(item1));
        when(bookingRepository.findAllByUserId(owner.getId())).thenReturn(List.of(booking));
        when(itemDtoMapper.mapToDto(any())).then(Mockito.CALLS_REAL_METHODS);
        when(itemDtoMapper.mapToItem(any())).then(Mockito.CALLS_REAL_METHODS);
        when(commentDtoMapper.mapToDto(any())).then(Mockito.CALLS_REAL_METHODS);
        CommentDto testCommentDto = itemService.addNewComment(owner.getId(), item1.getId(), commentDto);
        assertEquals(editedComment.getText(), testCommentDto.getText());
    }
}