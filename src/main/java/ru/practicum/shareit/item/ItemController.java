package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {
    private static final String USER_ID_REQUEST_HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader(value = USER_ID_REQUEST_HEADER) Long userId) {
        log.info("Запрос на получение списка вещей");
        return itemService.getItemsByUserId(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(
            @RequestHeader(value = USER_ID_REQUEST_HEADER) Long userId,
            @PathVariable Long itemId) {
        log.info("Запрос на получение вещи id=" + itemId);
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItemsByText(
            @RequestHeader(value = USER_ID_REQUEST_HEADER) Long userId,
            @RequestParam String text) {
        log.info("Запрос на поиск вещи. Текст запроса: " + text);
        return itemService.searchItemsByText(userId, text);
    }

    @PostMapping
    public ItemDto addItem(
            @RequestHeader(value = USER_ID_REQUEST_HEADER) Long userId,
            @RequestBody ItemDto itemDto) {
        log.info("Запрос на добавление вещи пользователя id=" + userId);
        return itemService.addItem(userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(value = USER_ID_REQUEST_HEADER) Long userId,
                               @PathVariable Long itemId,
                               @RequestBody CommentDto commentDto) {

    return itemService.addNewComment(userId, itemId, commentDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(
            @RequestHeader(value = USER_ID_REQUEST_HEADER) Long userId,
            @PathVariable Long itemId,
            @RequestBody ItemDto itemDto) {
        log.info("Запрос на обновление вещи id=" + itemId + " от пользователя id=" + userId);
        return itemService.updateItem(userId, itemId, itemDto);
    }
}
