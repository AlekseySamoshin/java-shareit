package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface BookingRepository extends JpaRepository<Booking, Long>, BookingRepositoryCustom {

    @Query("select b from Booking b where b.booker.id = ?1 order by b.start desc")
    List<Booking> findAllByUserId(Long userId);

    @Query("select b from Booking b where b.booker.id = ?1 order by b.start desc")
    List<Booking> findAllByUserId(Long userId, Pageable pageable);

    @Query("select b from Booking b " +
            "where b.booker.id = ?1 " +
            "and (b.start < current_timestamp and b.end > current_timestamp) " +
            "order by b.end desc")
    List<Booking> findAllCurrentByUserId(Long userId);

    @Query("select b from Booking b " +
            "where b.booker.id = ?1 " +
            "and (b.start < current_timestamp and b.end > current_timestamp) " +
            "order by b.end desc")
    List<Booking> findAllCurrentByUserId(Long userId, Pageable pageable);

    @Query("select b from Booking b " +
            "where b.booker.id = ?1 " +
            "and (b.end < current_timestamp) " +
            "order by b.start desc")
    List<Booking> findAllPastByUserId(Long userId);

    @Query("select b from Booking b " +
            "where b.booker.id = ?1 " +
            "and (b.end < current_timestamp) " +
            "order by b.start desc")
    List<Booking> findAllPastByUserId(Long userId, Pageable pageable);

    @Query("select b from Booking b " +
            "where b.booker.id = ?1 " +
            "and b.start > current_timestamp " +
            "order by b.start desc")
    List<Booking> findAllFutureByUserId(Long userId);

    @Query("select b from Booking b " +
            "where b.booker.id = ?1 " +
            "and b.start > current_timestamp " +
            "order by b.start desc")
    List<Booking> findAllFutureByUserId(Long userId, Pageable pageable);

    @Query("select b from Booking b where b.booker.id = ?1 and b.status = 'WAITING' order by b.end desc")
    List<Booking> findAllWaitingByUserId(Long userId);

    @Query("select b from Booking b where b.booker.id = ?1 and b.status = 'WAITING' order by b.end desc")
    List<Booking> findAllWaitingByUserId(Long userId, Pageable pageable);

    @Query("select b from Booking b where b.booker.id = ?1 and b.status = 'REJECTED' order by b.end desc")
    List<Booking> findAllRejectedByUserId(Long userId);

    @Query("select b from Booking b where b.booker.id = ?1 and b.status = 'REJECTED' order by b.end desc")
    List<Booking> findAllRejectedByUserId(Long userId, Pageable pageable);

    @Query("select b from Booking b " +
            "where b.item.id in (?1) " +
            "and b.status = ?2 " +
            "order by b.start desc")
    List<Booking> findBookingsForItemsWithState(List<Long> itemIds, String state);

    @Query("select b from Booking b where b.item.id in (?1) order by b.end desc")
    List<Booking> findAllBookingsForItems(List<Long> itemIds);

    @Query("select b from Booking b where b.item.id in (?1)" +
            "and (b.start < current_timestamp and b.end > current_timestamp) " +
            "order by b.start desc")
    List<Booking> findCurrentBookingsForItems(List<Long> itemIds);

    @Query("select b from Booking b where b.item.id in (?1)" +
            "and (b.start < current_timestamp and b.end < current_timestamp) " +
            "order by b.start desc")
    List<Booking> findPastBookingsForItems(List<Long> itemIds);

    @Query("select b from Booking b where b.item.id in (?1)" +
            "and (b.start > current_timestamp and b.end > current_timestamp) " +
            "order by b.start desc")
    List<Booking> findFutureBookingsForItems(List<Long> itemIds);

    @Query("select b from Booking b where b.item.id in (?1) and b.status = 'WAITING' order by b.end desc")
    List<Booking> findWaititngBookingsForItems(List<Long> itemIds);

    @Query("select b from Booking b where b.item.id in (?1) and b.status = 'REJECTED' order by b.end desc")
    List<Booking> findRejectedBookingsForItems(List<Long> itemIds);

    @Query(value = "select * from bookings b " +
            "where b.item_id = ?1 and start_date > current_timestamp " +
            "and b.status <> 'REJECTED' " +
            "order by b.start_date asc " +
            "limit 1", nativeQuery = true)
    Optional<Booking> findNextBookingForItem(Long itemId);

    @Query(value = "select * from bookings b " +
            "where b.item_id = ?1 and start_date < current_timestamp " +
            "and b.status <> 'REJECTED' " +
            "order by b.start_date desc " +
            "limit 1", nativeQuery = true)
    Optional<Booking> findLastBookingForItem(Long itemId);
}
