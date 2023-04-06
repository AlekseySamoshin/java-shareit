package ru.practicum.shareit.item.dto;

import javax.validation.constraints.NotBlank;

public class CommentDto {
    private Long id;

    @NotBlank
    private String text;

//    @NotNull
    private Long itemId;

    private Long authorId;
    private String authorName;
    private String created;
}
