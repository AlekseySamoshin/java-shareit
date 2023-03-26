package ru.practicum.shareit.request.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.ItemRequest;

import java.time.LocalDateTime;

@Component
public class ItemRequestDtoMapper {
    public ItemRequest toRequest(ItemRequestDto dto) {
        ItemRequest request = new ItemRequest();
        request.setId(dto.getId());
        request.setDescription(dto.getDescription());
        request.setRequestorId(dto.getRequestorId());
        request.setCreated(LocalDateTime.parse(dto.getCreated()));
        return request;
    }

    public ItemRequestDto toDto(ItemRequest request) {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(request.getId());
        dto.setDescription(request.getDescription());
        dto.setRequestorId(request.getRequestorId());
        dto.setCreated(request.getCreated().toString());
        return dto;
    }
}
