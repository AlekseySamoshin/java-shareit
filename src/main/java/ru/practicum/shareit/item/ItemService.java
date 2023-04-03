package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository requestRepository;
    private final ItemDtoMapper itemDtoMapper;
    private final BookingDtoMapper bookingDtoMapper;
    private final CommentDtoMapper commentDtoMapper;

    @Autowired
    public ItemService(ItemRepository itemRepository, UserRepository userRepository,
                       ItemDtoMapper itemDtoMapper, BookingRepository bookingRepository,
                       BookingDtoMapper bookingDtoMapper, CommentRepository commentRepository,
                       ItemRequestRepository requestRepository, CommentDtoMapper commentDtoMapper) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.itemDtoMapper = itemDtoMapper;
        this.bookingRepository = bookingRepository;
        this.bookingDtoMapper = bookingDtoMapper;
        this.commentRepository = commentRepository;
        this.requestRepository = requestRepository;
        this.commentDtoMapper = commentDtoMapper;
    }

    public List<ItemDto> getItemsByUserId(Long userId) {
        User user = getUserIfExists(userId);
        List<ItemDto> itemDtos = itemRepository.findAllByOwnerId(userId).stream()
                .map(itemDtoMapper::mapToDto)
                .collect(Collectors.toList());
        findCommentsForItems(itemDtos);
        return findLastAndNextBookings(itemDtos, userId);
    }

    public List<ItemDto> getItemsByUserId(Long userId, Integer pageNum, Integer pageSize) {
        getUserIfExists(userId);
        Pageable page = PageRequest.of(pageNum, pageSize);
        List<ItemDto> itemDtos = itemRepository.findAllByOwnerId(userId, page).stream()
                .map(itemDtoMapper::mapToDto)
                .collect(Collectors.toList());
        findCommentsForItems(itemDtos);
        return findLastAndNextBookings(itemDtos, userId);
    }

    public ItemDto addItem(Long userId, ItemDto itemDto) {
        ItemRequest itemRequest = null;
        validateItemDto(itemDto);
        User user = getUserIfExists(userId);
        itemDto.setOwnerId(userId);
        Item item = itemRepository.save(itemDtoMapper.mapToItem(itemDto));
        if (itemDto.getRequestId() != null) {
            itemRequest = requestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Запрос id=" + itemDto.getRequestId() + " не найден"));
            itemRequest.getItems().add(item);
            requestRepository.save(itemRequest);
        }
            return itemDtoMapper.mapToDto(item);
    }

    public ItemDto getItemById(Long itemId, Long userId) throws NotFoundException {
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isPresent()) {
            ItemDto itemDto = itemDtoMapper.mapToDto(item.get());
            findCommentsForItem(itemDto);
            return findLastAndNextBookings(itemDto, userId);
        }
        String message = "Вещь id=" + itemId + " не найдена";
        log.error(message);
        throw new NotFoundException(message);
    }

    public ItemDto getItemByIdAndOwnerId(Long userId, Long itemId) {
        User user = getUserIfExists(userId);
        Item itemOptional = itemRepository.findByIdAndOwnerId(userId, itemId).orElseThrow(
                () -> new NotFoundException("Вещь с id=" + itemId + " не найдена у пользователя id=" + userId)
        );
        ItemDto itemDto = itemDtoMapper.mapToDto(itemOptional);
        findCommentsForItem(itemDto);
        return findLastAndNextBookings(itemDto, userId);
    }

    public List<ItemDto> searchItemsByText(Long userId, String text, Integer pageNum, Integer pageSize) {
        List<Item> items;
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        if (pageNum == null && pageSize == null) {
            items = itemRepository.findByText(text.toLowerCase());
        } else {
            Pageable page = PageRequest.of(pageNum, pageSize);
            items = itemRepository.findByTextPageable(text.toLowerCase(), page);
        }
        List<ItemDto> result = items.stream()
                .map(itemDtoMapper::mapToDto)
                .collect(Collectors.toList());
        findCommentsForItems(result);
        return findLastAndNextBookings(result, userId);
    }

    public ItemDto updateItem(Long userId, Long itemId, ItemDto newItemDto) {
        ItemDto itemDto = getItemByIdAndOwnerId(userId, itemId);
        itemDto.setId(itemId);
        if (newItemDto.getName() != null) {
            itemDto.setName(newItemDto.getName());
        }
        if (newItemDto.getDescription() != null) {
            itemDto.setDescription(newItemDto.getDescription());
        }
        if (newItemDto.getAvailable() != null) {
            itemDto.setAvailable(newItemDto.getAvailable());
        }
        Item item = itemDtoMapper.mapToItem(itemDto);
        item = itemRepository.save(item);
        itemDto = itemDtoMapper.mapToDto(item);
        findCommentsForItem(itemDto);
        return findLastAndNextBookings(itemDto, userId);
    }

    public CommentDto addNewComment(Long userId, Long itemId, CommentDto commentDto) {
        if (commentDto.getText().isBlank()) {
            throw new WrongDataException("Комментарий отсутствует");
        }
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с id=" + userId + " не найден")
        );
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException("Вещь id=" + itemId + " не найдена")
        );
        if (!checkUserIsBookerOfItem(userId, itemId)) {
            throw new WrongDataException("Пользователь не брал вещь id=" + itemId + " в аренду");
        }
        Comment comment = new Comment();
        comment.setItemId(itemId);
        comment.setAuthorId(userId);
        comment.setAuthorName(user.getName());
        comment.setText(commentDto.getText());
        comment.setCreated(LocalDateTime.now());
        commentDto = commentDtoMapper.mapToDto(commentRepository.save(comment));
        return commentDto;
    }

    private User getUserIfExists(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
    }

    private ItemDto findCommentsForItem(ItemDto itemDto) {
        itemDto.setComments(new ArrayList<>());
        List<Comment> comments = commentRepository.findAllByItemId(itemDto.getId());
        if (!comments.isEmpty()) {
            for (Comment comment : comments) {
                itemDto.getComments().add(commentDtoMapper.mapToDto(comment));
            }
        }
        return itemDto;
    }

    private List<ItemDto> findCommentsForItems(List<ItemDto> items) {
        List<Long> idList = new ArrayList<>();
        for (ItemDto item : items) {
            idList.add(item.getId());
            item.setComments(new ArrayList<>());
        }
        List<Comment> comments = commentRepository.findAllForItems(idList);
        if (comments.isEmpty()) {
            return items;
        }
        for (ItemDto item : items) {
            for (Comment comment : comments) {
                if (comment.getItemId().equals(item.getId())) {
                    item.getComments().add(commentDtoMapper.mapToDto(comment));
                }
            }
        }
        return items;
    }

    private List<ItemDto> findLastAndNextBookings(List<ItemDto> itemDtos, Long userId) {
        LocalDateTime now = LocalDateTime.now();
        List<Long> itemIds = new ArrayList<>();
        for (ItemDto itemDto : itemDtos) {
            itemIds.add(itemDto.getId());
        }
        List<Booking> bookings = bookingRepository.findAllBookingsForItems(itemIds);
        if (bookings.isEmpty()) {
            return itemDtos;
        }
        for (ItemDto itemDto : itemDtos) {
            List<Booking> nextBooking = bookings.stream()
                    .filter(booking -> booking.getStart().isAfter(now))
                    .filter(booking -> booking.getItem().getId().equals(itemDto.getId()))
                    .sorted(Comparator.comparing(Booking::getStart))
                    .limit(1)
                    .collect(Collectors.toList());
            List<Booking> lastBooking = bookings.stream()
                    .filter(booking -> booking.getStart().isBefore(now))
                    .filter(booking -> booking.getItem().getId().equals(itemDto.getId()))
                    .sorted(Comparator.comparing(Booking::getStart).reversed())
                    .limit(1)
                    .collect(Collectors.toList());
            if (!nextBooking.isEmpty()) {
                itemDto.setNextBooking(bookingDtoMapper.toDto(nextBooking.get(0)));
            }
            if (!lastBooking.isEmpty()) {
                itemDto.setLastBooking(bookingDtoMapper.toDto(lastBooking.get(0)));
            }
        }
        return itemDtos;
    }

    private ItemDto findLastAndNextBookings(ItemDto itemDto, Long userId) {
        if (itemDto.getOwnerId().equals(userId)) {
            Optional<Booking> nextBooking = bookingRepository.findNextBookingForItem(itemDto.getId());
            Optional<Booking> lastBooking = bookingRepository.findLastBookingForItem(itemDto.getId());
            if (nextBooking.isPresent()) {
                itemDto.setNextBooking(bookingDtoMapper.toDto(nextBooking.get()));
            }
            if (lastBooking.isPresent()) {
                itemDto.setLastBooking(bookingDtoMapper.toDto(lastBooking.get()));
            }
            return itemDto;
        }
        itemDto.setLastBooking(null);
        itemDto.setNextBooking(null);
        return itemDto;
    }

    private void validateItemDto(ItemDto itemDto) throws WrongDataException {
        StringBuilder message = new StringBuilder();
        if (itemDto.getDescription() == null || itemDto.getName().isBlank()) {
            message.append("Не указано название. ");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            message.append("Нет описания вещи. ");
        }
        if (itemDto.getAvailable() == null) {
            message.append("Не указана доступность вещи для заказа.");
        }
        if (!message.toString().isBlank()) {
            log.warn("Ошибка валидации вещи: " + message.toString());
            throw new WrongDataException(message.toString());
        }
    }

    private Boolean checkUserIsBookerOfItem(Long userId, Long itemId) {
        List<Booking> bookings = bookingRepository.findAllByUserId(userId);
        if (bookings.isEmpty()) {
            return false;
        }
        for (Booking booking : bookings) {
            if (booking.getItem().getId().equals(itemId) && booking.getStart().isBefore(LocalDateTime.now())) return true;
        }
        return false;
    }
}
