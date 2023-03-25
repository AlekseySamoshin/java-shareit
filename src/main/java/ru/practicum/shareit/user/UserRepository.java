package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select * from item_requests where requester_id = ?1")
    List<ItemRequest> findAllByUserId(Long userId);
}
