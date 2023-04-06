package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("select c from Comment c where c.itemId = ?1")
    List<Comment> findAllByItemId(Long itemId);

    @Query("select c from Comment c where c.itemId in (?1)")
    List<Comment> findAllForItems(List<Long> itemIds);
}
