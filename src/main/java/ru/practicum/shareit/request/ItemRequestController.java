package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@RestController
@RequestMapping(path = "/requests")
@Slf4j
public class ItemRequestController {
    private static final String USER_ID_REQUEST_HEADER = "X-Sharer-User-Id";
    private final ItemRequestService requestService;

    @Autowired
    public ItemRequestController(ItemRequestService requestService){
        this.requestService = requestService;
    }

    @PostMapping
    ItemRequestDto addNewRequest(@RequestHeader(value = USER_ID_REQUEST_HEADER) Long userId,
                                 @RequestBody ItemRequestDto requestDto) {
        return requestService.addItemRequest(userId, requestDto);
    }

    @GetMapping
    ItemRequestDto getAllRequestsOfUser(@RequestHeader(value = USER_ID_REQUEST_HEADER) Long userId) {
        return requestService.getAllRequestsOfUser(userId);
    }
}
