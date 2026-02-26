package com.puresoftware.ticketing.service;

import com.puresoftware.ticketing.domain.Booking;
import com.puresoftware.ticketing.domain.Event;
import com.puresoftware.ticketing.domain.Seat;
import com.puresoftware.ticketing.domain.User;
import com.puresoftware.ticketing.dto.ReserveRequest;
import com.puresoftware.ticketing.dto.ReserveResponse;
import com.puresoftware.ticketing.repository.BookingRepository;
import com.puresoftware.ticketing.repository.EventRepository;
import com.puresoftware.ticketing.repository.SeatRepository;
import com.puresoftware.ticketing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final SeatRepository seatRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final StringRedisTemplate redis;

    @Value("${app.booking.holdSeconds:600}")
    private long holdSeconds;

    @Transactional
    public ReserveResponse reserveSeats(ReserveRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        Set<Long> seatIds = new HashSet<>(request.getSeatIds());
        List<Seat> seats = seatRepository.lockAllByIds(seatIds);
        if (seats.size() != seatIds.size()) {
            throw new IllegalArgumentException("One or more seats not found");
        }
        for (Seat s : seats) {
            if (!Objects.equals(s.getEvent().getId(), event.getId())) {
                throw new IllegalArgumentException("Seat does not belong to event");
            }
            if (s.getStatus() != Seat.Status.AVAILABLE) {
                throw new IllegalStateException("Seat not available: " + s.getId());
            }
        }

        String reservationId = UUID.randomUUID().toString();
        Duration ttl = Duration.ofSeconds(holdSeconds);

        List<String> acquiredKeys = new ArrayList<>();
        try {
            for (Seat s : seats) {
                String key = holdKey(event.getId(), s.getId());
                Boolean set = redis.opsForValue().setIfAbsent(key, reservationId, ttl);
                if (Boolean.FALSE.equals(set)) {
                    throw new IllegalStateException("Seat currently held: " + s.getId());
                }
                acquiredKeys.add(key);
            }

            for (Seat s : seats) {
                s.setStatus(Seat.Status.HELD);
            }
            seatRepository.saveAll(seats);

            String resvKey = reservationKey(reservationId);
            String seatsCsv = seats.stream().map(seat -> seat.getId().toString()).collect(Collectors.joining(","));
            String payload = event.getId() + "|" + user.getId() + "|" + seatsCsv;
            redis.opsForValue().set(resvKey, payload, ttl);

            return new ReserveResponse(reservationId, Instant.now().plusSeconds(holdSeconds));
        } catch (OptimisticLockingFailureException e) {
            releaseKeys(acquiredKeys);
            throw new IllegalStateException("Concurrent update detected", e);
        } catch (RuntimeException e) {
            releaseKeys(acquiredKeys);
            throw e;
        }
    }

    @Transactional
    public Long confirmReservation(String reservationId, Long userId, String paymentRef) {
        String resvKey = reservationKey(reservationId);
        String payload = redis.opsForValue().get(resvKey);
        if (payload == null) {
            throw new IllegalStateException("Reservation expired or not found");
        }
        String[] parts = payload.split("\\|");
        Long eventId = Long.parseLong(parts[0]);
        Long holderUserId = Long.parseLong(parts[1]);
        if (!Objects.equals(holderUserId, userId)) {
            throw new IllegalArgumentException("Reservation does not belong to user");
        }
        Set<Long> seatIds = Arrays.stream(parts[2].split(","))
                .filter(s -> !s.isBlank())
                .map(Long::parseLong)
                .collect(Collectors.toSet());

        List<Seat> seats = seatRepository.lockAllByIds(seatIds);
        for (Seat s : seats) {
            String key = holdKey(eventId, s.getId());
            String val = redis.opsForValue().get(key);
            if (!reservationId.equals(val)) {
                throw new IllegalStateException("Seat hold missing or mismatched for seat " + s.getId());
            }
            if (s.getStatus() != Seat.Status.HELD) {
                throw new IllegalStateException("Seat not in HELD status " + s.getId());
            }
            s.setStatus(Seat.Status.BOOKED);
        }
        seatRepository.saveAll(seats);

        User user = userRepository.findById(userId).orElseThrow();
        Event event = eventRepository.findById(eventId).orElseThrow();
        Booking booking = Booking.builder()
                .user(user)
                .event(event)
                .seats(new HashSet<>(seats))
                .bookedAt(LocalDateTime.now())
                .status("CONFIRMED")
                .build();
        Booking saved = bookingRepository.save(booking);

        for (Seat s : seats) {
            redis.delete(holdKey(eventId, s.getId()));
        }
        redis.delete(resvKey);
        return saved.getId();
    }

    private void releaseKeys(List<String> keys) {
        if (!keys.isEmpty()) {
            redis.delete(keys);
        }
    }

    private String holdKey(Long eventId, Long seatId) {
        return "hold:" + eventId + ":" + seatId;
        }

    private String reservationKey(String reservationId) {
        return "resv:" + reservationId;
    }
}

