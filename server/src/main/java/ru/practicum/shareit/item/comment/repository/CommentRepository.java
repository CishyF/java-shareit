package ru.practicum.shareit.item.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.comment.entity.Comment;
import ru.practicum.shareit.item.entity.Item;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    List<Comment> findAllByItem(Item item);
}
