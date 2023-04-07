package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class CommentDto {
    private Long id;

    @NotBlank
    @NotNull
    private String text;

    private Long itemId;

    private Long authorId;
    private String authorName;
    private String created;
}
