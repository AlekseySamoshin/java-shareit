package ru.practicum.shareit.request.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.ItemRequest;

@Component
public class ItemRequestDtoMapper {
    public ItemRequest toRequest(ItemRequestDto dto) {
        ItemRequest request = new ItemRequest();
        return request;
    }

    public ItemRequestDto toDto(ItemRequest request) {
        ItemRequestDto dto = new ItemRequestDto();
        return dto;
    }
}
