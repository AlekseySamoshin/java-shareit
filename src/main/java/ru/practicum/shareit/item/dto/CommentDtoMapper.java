package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.Comment;

@Component
public class CommentDtoMapper {
    public CommentDto mapToDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setItemId(comment.getItemId());
        dto.setAuthorId(comment.getAuthorId());
        dto.setAuthorName(comment.getAuthorName());
        dto.setText(comment.getText());
        dto.setCreated(comment.getCreated().toString());
        return dto;
    }
}