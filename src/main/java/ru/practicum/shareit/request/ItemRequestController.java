package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@Slf4j
public class ItemRequestController {
    private static final String USER_ID_REQUEST_HEADER = "X-Sharer-User-Id";
    private final ItemRequestService requestService;

    @Autowired
    public ItemRequestController(ItemRequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    public ItemRequestDto addNewRequest(@RequestHeader(value = USER_ID_REQUEST_HEADER) Long userId,
                                 @RequestBody ItemRequestDto requestDto) {
        return requestService.addItemRequest(userId, requestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getAllRequestsOfUser(@RequestHeader(value = USER_ID_REQUEST_HEADER) Long userId) {
        return requestService.getAllRequestsOfUser(userId);
    }

    @GetMapping(path = "/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader(value = USER_ID_REQUEST_HEADER) Long userId,
                                        @RequestParam(name = "from", required = false) Integer pageNum,
                                        @RequestParam(name = "size", required = false) Integer pageSize) {
        return requestService.getAllRequests(userId, pageNum, pageSize);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader(value = USER_ID_REQUEST_HEADER) Long userId,
                                  @PathVariable Long requestId) {
        return requestService.getRequestById(userId, requestId);
    }
}
