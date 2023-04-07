package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    ItemClient itemClient;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader(value = USER_ID_HEADER) Long userId,
                                           @RequestParam(name = "from", required = false) Integer pageNum,
                                           @RequestParam(name = "size", required = false) Integer pageSize) {
        log.info("Запрос на получение списка вещей");
        return itemClient.getItemsByUserId(userId, pageNum, pageSize);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(
            @RequestHeader(value = USER_ID_HEADER) Long userId,
            @PathVariable Long itemId) {
        log.info("Запрос на получение вещи id=" + itemId);
        return itemClient.getItemById(itemId, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItemsByText(
            @RequestHeader(value = USER_ID_HEADER) Long userId,
            @RequestParam String text,
            @RequestParam(name = "from", required = false) Integer pageNum,
            @RequestParam(name = "size", required = false) Integer pageSize) {
        log.info("Запрос на поиск вещи. Текст запроса: " + text);
        return itemClient.searchItemsByText(userId, text, pageNum, pageSize);
    }

    @PostMapping
    public ResponseEntity<Object> addItem(
            @RequestHeader(value = USER_ID_HEADER) Long userId,
            @RequestBody ItemDto itemDto) {
        log.info("Запрос на добавление вещи пользователя id=" + userId);
        return itemClient.addItem(userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(value = USER_ID_HEADER) Long userId,
                                 @PathVariable Long itemId,
                                 @RequestBody CommentDto commentDto) {
        return itemClient.addNewComment(userId, itemId, commentDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object>  updateItem(
            @RequestHeader(value = USER_ID_HEADER) Long userId,
            @PathVariable Long itemId,
            @RequestBody ItemDto itemDto) {
        log.info("Запрос на обновление вещи id=" + itemId + " от пользователя id=" + userId);
        return itemClient.updateItem(userId, itemId, itemDto);
    }
}
