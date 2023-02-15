package ru.practicum.shareit.item.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {
    private final String userIdRequestHeader = "X-Sharer-User-Id";
    ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader(value = userIdRequestHeader) Long userId) {
        log.info("Запрос на получение списка вещей");
        return itemService.getItemsByUserId(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(
            @RequestHeader(value = userIdRequestHeader) Long userId,
            @PathVariable Long itemId) {
        log.info("Запрос на получение вещи id=" + itemId);
        return itemService.getItemById(itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItemsByText(
            @RequestHeader(value = userIdRequestHeader) Long userId,
            @RequestParam String text) {
        log.info("Запрос на поиск вещи. Текст запроса: " + text);
        return itemService.searchItemsByText(userId, text);
    }

    @PostMapping
    public ItemDto addItem(
            @RequestHeader(value = userIdRequestHeader) Long userId,
            @RequestBody ItemDto itemDto) {
        log.info("Запрос на добавление вещи пользователя id=" + userId);
        return itemService.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(
            @RequestHeader(value = userIdRequestHeader) Long userId,
            @PathVariable Long itemId,
            @RequestBody ItemDto itemDto) {
        log.info("Запрос на обновление вещи id=" + itemId + " от пользователя id=" + userId);
        return itemService.updateItem(userId, itemId, itemDto);
    }
}
