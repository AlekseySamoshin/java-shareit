package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.Booking;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long>, ItemRequestRepositoryCustom {
}
