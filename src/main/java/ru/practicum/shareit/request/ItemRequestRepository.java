package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.Booking;

import java.util.Collection;
import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long>, ItemRequestRepositoryCustom {
    @Query("select i from ItemRequest i where i.requestorId = ?1")
    List<ItemRequest> findAllByUserId(Long userId);
}
