package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.entity.BookingStatus;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.entity.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findBookingsByItemIn(Collection<Item> items, Sort sort);

    List<Booking> findBookingsByBooker(User booker, Sort sort);

    List<Booking> findBookingsByBooker(User booker);

    @Query("SELECT b FROM Booking b WHERE b.item IN ?1 AND b.start <= now() AND b.end >= now()")
    List<Booking> findCurrentBookingsByItemIn(Collection<Item> items, Sort sort);

    @Query("SELECT b FROM Booking b WHERE b.booker = ?1 AND b.start <= now() AND b.end >= now()")
    List<Booking> findCurrentBookingsByBooker(User booker, Sort sort);

    List<Booking> findBookingsByItemInAndEndIsBefore(Collection<Item> items, LocalDateTime dateTime, Sort sort);

    List<Booking> findBookingsByBookerAndEndIsBefore(User booker, LocalDateTime dateTime, Sort sort);

    List<Booking> findBookingsByItemInAndStartIsAfter(Collection<Item> items, LocalDateTime dateTime, Sort sort);

    List<Booking> findBookingsByBookerAndStartIsAfter(User booker, LocalDateTime dateTime, Sort sort);

    List<Booking> findBookingsByItemInAndStatus(Collection<Item> items, BookingStatus status, Sort sort);

    List<Booking> findBookingsByBookerAndStatus(User booker, BookingStatus status, Sort sort);

    @Query("SELECT b FROM Booking b WHERE b.item = ?1 AND b.start <= now() AND b.status < 2 ORDER BY b.start DESC NULLS LAST")
    Page<Booking> findLastBookingByItem(Item item, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.item = ?1 AND b.start >= now() AND b.status < 2 ORDER BY b.start ASC NULLS LAST")
    Page<Booking> findNextBookingByItem(Item item, Pageable pageable);
}
