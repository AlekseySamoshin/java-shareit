package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long>, ItemRepositoryCustom {
    @Query("select i from Item i where i.ownerId = ?1 order by i.id asc")
    List<Item> findAllByOwnerId(Long userId);

    @Query("select i from Item i where i.ownerId = ?1 order by i.id asc")
    List<Item> findAllByOwnerId(Long userId, Pageable pageable);

    @Query("select i from Item i where i.ownerId = ?1 and id = ?2")
    Optional<Item> findByIdAndOwnerId(Long userId, Long itemId);

    @Query(" select i from Item i " +
            "where upper(i.name) like upper(concat('%', ?1, '%')) " +
            " or upper(i.description) like upper(concat('%', ?1, '%'))" +
            " and i.available = true")
    List<Item> findByText(String text);

    @Query(" select i from Item i " +
            "where upper(i.name) like upper(concat('%', ?1, '%')) " +
            " or upper(i.description) like upper(concat('%', ?1, '%'))" +
            " and i.available = true")
    List<Item> findByTextPageable(String text, Pageable pageable);
}
