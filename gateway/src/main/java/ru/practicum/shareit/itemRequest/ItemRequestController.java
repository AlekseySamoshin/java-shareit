package ru.practicum.shareit.itemRequest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.itemRequest.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    public ItemRequestController(ItemRequestClient itemRequestClient) {
        this.itemRequestClient = itemRequestClient;
    }

    @PostMapping
    public ResponseEntity<Object> addNewRequest(@RequestHeader(USER_ID_HEADER) Long userId,
                                                @Valid @RequestBody ItemRequestDto requestDto) {
        log.info("Creating request, userId={}", userId);
        return itemRequestClient.addItemRequest(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllRequestsOfUser(@RequestHeader(value = USER_ID_HEADER) Long userId) {
        log.info("Getting all requests of user id=" + userId);
        return itemRequestClient.getAllRequestsOfUser(userId);
    }

    @GetMapping(path = "/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader(value = USER_ID_HEADER) Long userId,
                                                 @PositiveOrZero @RequestParam(name = "from", required = false) Integer from,
                                                 @Positive @RequestParam(name = "size", required = false) Integer pageSize) {
        log.info("Getting all requests pageable");
        return itemRequestClient.getAllRequests(userId, from, pageSize);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader(value = USER_ID_HEADER) Long userId,
                                                 @Positive @PathVariable Long requestId) {
        log.info("Getting request id=" + requestId);
        return itemRequestClient.getRequestById(userId, requestId);
    }
}
