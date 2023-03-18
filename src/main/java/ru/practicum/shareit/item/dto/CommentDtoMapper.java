package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.Comment;

import java.time.LocalDateTime;

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

    public Comment mapToComment(CommentDto dto) {
        Comment comment = new Comment();
        comment.setId(dto.getId());
        comment.setItemId(dto.getItemId());
        comment.setAuthorId(dto.getAuthorId());
        comment.setAuthorName(dto.getAuthorName());
        comment.setText(dto.getText());
        comment.setCreated(LocalDateTime.parse(dto.getCreated()));
        return comment;
    }
}
