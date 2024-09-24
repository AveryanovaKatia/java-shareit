package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findAllByBookerId(final Integer userId, Sort sort);

    List<Booking> findAllByBookerIdAndEndBeforeAndStartAfter(final Integer userId, final LocalDateTime time1,
                                                             final LocalDateTime time2, Sort sort);

    List<Booking> findAllByBookerIdAndEndBefore(final Integer userId, final LocalDateTime time, Sort sort);

    List<Booking> findAllByBookerIdAndStartAfter(final Integer userId, final LocalDateTime time, Sort sort);

    List<Booking> findAllByBookerIdAndStatusIs(final Integer userId, final String status, Sort sort);

    List<Booking> findAllByItemOwnerId(final Integer userId, Sort sort);

    List<Booking> findAllByItemOwnerIdAndEndBeforeAndStartAfter(final Integer userId, final LocalDateTime time1,
                                                            final LocalDateTime time2, Sort sort);

    List<Booking> findAllByItemOwnerIdAndEndBefore(final Integer userId, final LocalDateTime time, Sort sort);

    List<Booking> findAllByItemOwnerIdAndStartAfter(final Integer userId, final LocalDateTime time, Sort sort);

    List<Booking> findAllByItemOwnerIdAndStatusIs(final Integer userId, final String status, Sort sort);

    List<Booking> findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(final Integer userId,
                                                                          final Integer itemId, final Status status,
                                                                          final LocalDateTime time);

    Optional<Booking> findTopByItemIdAndEndBeforeAndStatusInOrderByEndDesc(final Integer itemId,
                                                         final LocalDateTime time, final List<Status> status);

    Optional<Booking> findTopByItemIdAndStartAfterAndStatusInOrderByStartAsc(final Integer itemId,
                                                                   final LocalDateTime time, final List<Status> status);

}
