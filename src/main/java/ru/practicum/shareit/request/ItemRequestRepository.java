package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long>, ItemRequestRepositoryCustom {
    @Query("select i from ItemRequest i where i.requestorId = ?1 order by i.created desc")
    List<ItemRequest> findAllByUserId(Long userId);

    @Query("select i from ItemRequest i where i.requestorId = ?1 order by i.created desc")
    List<ItemRequest> findAllByUserId(Long userId, Pageable pageable);
}
