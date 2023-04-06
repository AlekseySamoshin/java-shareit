package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@Setter
public class ItemDto {
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;
    private Boolean available;
    private Long ownerId;
    private BookingDto lastBooking;
    private BookingDto nextBooking;
    private List<CommentDto> comments;
    private Long requestId;
}
