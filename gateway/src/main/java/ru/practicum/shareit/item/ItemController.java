package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Null;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/items")
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    public ItemController(ItemClient itemClient) {
        this.itemClient = itemClient;
    }

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader(value = USER_ID_HEADER) Long userId,
                                           @PositiveOrZero @Null @RequestParam(name = "from", required = false) Integer pageNum,
                                           @Positive @Null @RequestParam(name = "size", required = false) Integer pageSize) {
        log.info("Getting all items");
        return itemClient.getItemsByUserId(userId, pageNum, pageSize);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(
            @RequestHeader(value = USER_ID_HEADER) Long userId,
            @PathVariable Long itemId) {
        log.info("Getting item id=" + itemId);
        return itemClient.getItemById(itemId, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItemsByText(
            @RequestHeader(value = USER_ID_HEADER) Long userId,
            @RequestParam String text,
            @PositiveOrZero @Null @RequestParam(name = "from", required = false) Integer pageNum,
            @Positive @Null @RequestParam(name = "size", required = false) Integer pageSize) {
        log.info("Searching item by text: " + text);
        return itemClient.searchItemsByText(userId, text, pageNum, pageSize);
    }

    @PostMapping
    public ResponseEntity<Object> addItem(
            @RequestHeader(value = USER_ID_HEADER) Long userId,
            @RequestBody ItemDto itemDto) {
        log.info("Creating new item of user id=" + userId);
        return itemClient.addItem(userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(value = USER_ID_HEADER) Long userId,
                                             @PathVariable Long itemId,
                                             @RequestBody @Valid CommentDto commentDto) {
        log.info("Creating new comment from user id=" + userId + " about item id=" + itemId);
        return itemClient.addNewComment(userId, itemId, commentDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(
            @RequestHeader(value = USER_ID_HEADER) Long userId,
            @PathVariable Long itemId,
            @RequestBody ItemDto itemDto) {
        log.info("Updating item id=" + itemId + " from user id=" + userId);
        return itemClient.updateItem(userId, itemId, itemDto);
    }
}
