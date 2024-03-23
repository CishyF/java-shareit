package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.entity.User;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {

    List<Item> findItemsByOwnerOrderById(User owner);

    Page<Item> findItemsByOwnerOrderById(User owner, Pageable pageable);

    @Query("SELECT i FROM Item i WHERE " +
            "(lower(i.name) LIKE concat('%', lower(:text), '%') OR lower(i.description) LIKE concat('%', lower(:text), '%')) " +
            "AND (i.available = true)")
    Page<Item> findItemsContainingTextAndAvailable(@Param("text") String text, Pageable pageable);
}
