package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    public static final String API_PREFIX = "/items";

    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getItems(Long userId, Integer pageNum, Integer pageSize) {
        Map<String, Object> parameters = Map.of(
                "from", pageNum,
                "size", pageSize
        );
        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getItemById (Long userId, Long itemId, Integer pageNum, Integer pageSize) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> searchItemsByText (Long userId,
                                                     @NotBlank String text,
                                                     @PositiveOrZero Integer from,
                                                     @Positive Integer size) {
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );
        return get("/search?text={text}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> addItem(Long userId, @Valid ItemDto itemDto) {
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> addComment (Long userId, Long itemId, CommentDto commentDto) {
        return post("/" + itemId + "/comments", userId, null, commentDto);
    }

    public ResponseEntity<Object> updateItem(Long userId, Long itemId, ItemDto itemDto) {
        return patch("/" + itemId, userId, itemDto);
    }

//
//    public List<ItemDto> getItems(@RequestHeader(value = USER_ID_REQUEST_HEADER) Long userId,
//                                  @RequestParam(name = "from", required = false) Integer pageNum,
//                                  @RequestParam(name = "size", required = false) Integer pageSize) {
//        log.info("Запрос на получение списка вещей");
//        return itemService.getItemsByUserId(userId, pageNum, pageSize);
//    }
//
//    @GetMapping("/{itemId}")
//    public ItemDto getItemById(
//            @RequestHeader(value = USER_ID_REQUEST_HEADER) Long userId,
//            @PathVariable Long itemId) {
//        log.info("Запрос на получение вещи id=" + itemId);
//        return itemService.getItemById(itemId, userId);
//    }
//
//    @GetMapping("/search")
//    public List<ItemDto> searchItemsByText(
//            @RequestHeader(value = USER_ID_REQUEST_HEADER) Long userId,
//            @RequestParam String text,
//            @RequestParam(name = "from", required = false) Integer pageNum,
//            @RequestParam(name = "size", required = false) Integer pageSize) {
//        log.info("Запрос на поиск вещи. Текст запроса: " + text);
//        return itemService.searchItemsByText(userId, text, pageNum, pageSize);
//    }
//
//    @PostMapping
//    public ItemDto addItem(
//            @RequestHeader(value = USER_ID_REQUEST_HEADER) Long userId,
//            @RequestBody ItemDto itemDto) {
//        log.info("Запрос на добавление вещи пользователя id=" + userId);
//        return itemService.addItem(userId, itemDto);
//    }
//
//    @PostMapping("/{itemId}/comment")
//    public CommentDto addComment(@RequestHeader(value = USER_ID_REQUEST_HEADER) Long userId,
//                                 @PathVariable Long itemId,
//                                 @RequestBody CommentDto commentDto) {
//        return itemService.addNewComment(userId, itemId, commentDto);
//    }
//
//    @PatchMapping("/{itemId}")
//    public ItemDto updateItem(
//            @RequestHeader(value = USER_ID_REQUEST_HEADER) Long userId,
//            @PathVariable Long itemId,
//            @RequestBody ItemDto itemDto) {
//        log.info("Запрос на обновление вещи id=" + itemId + " от пользователя id=" + userId);
//        return itemService.updateItem(userId, itemId, itemDto);
//    }

}
