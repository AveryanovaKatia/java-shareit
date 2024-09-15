package ru.practicum.shareit.booking.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponce;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingServiceImpl implements BookingService {

    BookingRepository bookingRepository;

    BookingMapper bookingMapper;

    UserRepository userRepository;

    UserMapper userMapper;

    ItemRepository itemRepository;

    ItemMapper itemMapper;

    final Sort sort = Sort.by(Sort.Direction.DESC, "start");

    @Override
    @Transactional
    public BookingResponce saveRequest(final BookingRequest bookingRequest, final Integer userId) {
        log.info("Запрос на бронирование вещи с id " + bookingRequest.getItemId());
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id = {} нет." + userId));
        final Item item = itemRepository.findById(bookingRequest.getItemId())
                .orElseThrow(() -> new NotFoundException("Пользователя с id = {} нет." + bookingRequest.getItemId()));
        if (!item.getAvailable()) {
            throw new ValidationException("Вещь не доступена для бронирования.");
        }
        if (bookingRequest.getStart().isAfter(bookingRequest.getEnd())
                || bookingRequest.getStart().isEqual(bookingRequest.getEnd())) {
            throw new ValidationException("Дата окончания бронирования не может быть раньше или равна дате начала");
        }
        final Booking booking = bookingRepository.save(bookingMapper.toBooking(bookingRequest, user, item));
        log.info("Запрос на бронирование вещи с id {} успешно сохранен", bookingRequest.getItemId());
        return bookingMapper.toBookingResponce(booking, userMapper.toUserDto(user), itemMapper.toItemDto(item));
    }

    @Override
    @Transactional
    public BookingResponce approved(final Integer ownerId, final Integer bookingId, final boolean approved) {
        log.info("Запрос на подтверждение бронирование вещи с id " + bookingId);
        final Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Такого запроса на бронирование не было"));
        if (!ownerId.equals(booking.getItem().getOwner().getId())) {
            throw new ValidationException("Подтвердить бронирование может только владелец вещи");
        }
        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new ValidationException("Нельзя подтверждать бронирование" +
                    " если оно не находится в ожидании подтвержления");
        }
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        final Booking updateBooking = bookingRepository.save(booking);
        log.info("Выполнен запрос на подтверждение бронирование вещи с id " + bookingId);
        return bookingMapper.toBookingResponce(updateBooking,
                userMapper.toUserDto(booking.getBooker()), itemMapper.toItemDto(booking.getItem()));
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponce findById(final Integer userId, final Integer bookingId) {
        log.info("Запрос на получение информации о бронировании вещи с id " + bookingId);
        final Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Такого запроса на бронирование не было"));
        if (!booking.getBooker().getId().equals(userId)
                && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new ValidationException("Посмотреть бронирование может только владелец вещи" +
                    "или человек, забронировавший ее");
        }
        return bookingMapper.toBookingResponce(booking,
                userMapper.toUserDto(booking.getBooker()), itemMapper.toItemDto(booking.getItem()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponce> findAllByUserId(final Integer userId, final String state) {
        log.info("Запрос на получение всех бронирований пользователя с id " + userId);
        userRepository.findById(userId);
        final List<Booking> bookings = switch (state) {
            case "all" -> bookingRepository.findAllByBookerId(userId, sort);
            case "CURRENT" -> bookingRepository.findAllByBookerIdAndEndBeforeAndStartAfter(userId,
                    LocalDateTime.now(), LocalDateTime.now(), sort);
            case "PAST" -> bookingRepository.findAllByBookerIdAndEndBefore(userId, LocalDateTime.now(), sort);
            case "FUTURE" -> bookingRepository.findAllByBookerIdAndStartAfter(userId, LocalDateTime.now(), sort);
            case "WAITING" -> bookingRepository.findAllByBookerIdAndStatusIs(userId, "waiting", sort);
            case "REJECTED" -> bookingRepository.findAllByBookerIdAndStatusIs(userId, "rejected", sort);
            default -> throw new ValidationException("Неверно передан параметр state");
        };
        return bookings.stream()
                .map(booking -> bookingMapper.toBookingResponce(booking,
                        userMapper.toUserDto(booking.getBooker()), itemMapper.toItemDto(booking.getItem())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponce> findAllByOwnerId(final Integer ownerId, final String state) {
        log.info("Запрос на получение всех забронированных вещей пользователя с id " + ownerId);
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id = {} нет." + ownerId));
        if (itemRepository.findAllByOwnerId(ownerId).isEmpty()) {
            throw new ValidationException("у пользователя пока нет вещей");
        }
        final List<Booking> bookings = switch (state) {
            case "all" -> bookingRepository.findAllByItemOwnerId(ownerId, sort);
            case "CURRENT" -> bookingRepository.findAllByItemOwnerIdAndEndBeforeAndStartAfter(ownerId,
                    LocalDateTime.now(), LocalDateTime.now(), sort);
            case "PAST" -> bookingRepository.findAllByItemOwnerIdAndEndBefore(ownerId, LocalDateTime.now(), sort);
            case "FUTURE" -> bookingRepository.findAllByItemOwnerIdAndStartAfter(ownerId, LocalDateTime.now(), sort);
            case "WAITING" -> bookingRepository.findAllByItemOwnerIdAndStatusIs(ownerId, "waiting", sort);
            case "REJECTED" -> bookingRepository.findAllByItemOwnerIdAndStatusIs(ownerId, "rejected", sort);
            default -> throw new ValidationException("Неверно передан параметр state");
        };
        return bookings.stream()
                .map(booking -> bookingMapper.toBookingResponce(booking,
                        userMapper.toUserDto(booking.getBooker()), itemMapper.toItemDto(booking.getItem())))
                .collect(Collectors.toList());
    }
}
