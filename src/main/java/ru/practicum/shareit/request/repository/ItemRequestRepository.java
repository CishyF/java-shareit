package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.request.entity.ItemRequest;
import ru.practicum.shareit.user.entity.User;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer> {

    List<ItemRequest> findItemRequestsByRequestor(User requestor, Sort sort);

    @Query("SELECT r FROM ItemRequest r WHERE r.requestor != :user")
    Page<ItemRequest> findItemRequestsByRequestorNotEqualUser(@Param("user") User user, Pageable pageable);
}
