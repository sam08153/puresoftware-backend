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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BookingServiceTest {

    private SeatRepository seatRepository;
    private BookingRepository bookingRepository;
    private UserRepository userRepository;
    private EventRepository eventRepository;
    private StringRedisTemplate redis;
    private ValueOperations<String, String> valueOps;
    private BookingService service;

    @BeforeEach
    void setup() throws Exception {
        seatRepository = mock(SeatRepository.class);
        bookingRepository = mock(BookingRepository.class);
        userRepository = mock(UserRepository.class);
        eventRepository = mock(EventRepository.class);
        redis = mock(StringRedisTemplate.class);
        valueOps = mock(ValueOperations.class);
        when(redis.opsForValue()).thenReturn(valueOps);

        service = new BookingService(seatRepository, bookingRepository, userRepository, eventRepository, redis);
        Field f = BookingService.class.getDeclaredField("holdSeconds");
        f.setAccessible(true);
        f.set(service, 600L);
    }

    @Test
    void reserveSeats_success() {
        User user = User.builder().id(1L).email("a@b.com").name("A").build();
        Event event = Event.builder().id(10L).title("Show").category("Music").city("NYC").eventDateTime(LocalDateTime.now()).build();
        Seat s1 = Seat.builder().id(100L).event(event).rowLabel("A").seatNumber(1).tier("VIP").price(new BigDecimal("100.00")).status(Seat.Status.AVAILABLE).build();
        Seat s2 = Seat.builder().id(101L).event(event).rowLabel("A").seatNumber(2).tier("VIP").price(new BigDecimal("100.00")).status(Seat.Status.AVAILABLE).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));
        when(seatRepository.lockAllByIds(new HashSet<>(Arrays.asList(100L, 101L)))).thenReturn(List.of(s1, s2));

        when(valueOps.setIfAbsent(anyString(), anyString(), any())).thenReturn(true);

        ReserveRequest req = new ReserveRequest();
        req.setUserId(1L);
        req.setEventId(10L);
        req.setSeatIds(new HashSet<>(Arrays.asList(100L, 101L)));

        ReserveResponse resp = service.reserveSeats(req);
        assertNotNull(resp.getReservationId());

        verify(seatRepository).saveAll(anyList());
        verify(valueOps, atLeast(2)).setIfAbsent(startsWith("hold:"), anyString(), any());
        verify(valueOps).set(startsWith("resv:"), contains("10|1|100,101"), any());
    }

    @Test
    void confirmReservation_success() {
        User user = User.builder().id(1L).email("a@b.com").name("A").build();
        Event event = Event.builder().id(10L).title("Show").category("Music").city("NYC").eventDateTime(LocalDateTime.now()).build();
        Seat s1 = Seat.builder().id(100L).event(event).rowLabel("A").seatNumber(1).tier("VIP").price(new BigDecimal("100.00")).status(Seat.Status.HELD).build();
        Seat s2 = Seat.builder().id(101L).event(event).rowLabel("A").seatNumber(2).tier("VIP").price(new BigDecimal("100.00")).status(Seat.Status.HELD).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));
        when(seatRepository.lockAllByIds(new HashSet<>(Arrays.asList(100L, 101L)))).thenReturn(List.of(s1, s2));

        when(redis.opsForValue().get("resv:abc")).thenReturn("10|1|100,101");
        when(redis.opsForValue().get("hold:10:100")).thenReturn("abc");
        when(redis.opsForValue().get("hold:10:101")).thenReturn("abc");

        Booking saved = Booking.builder().id(999L).build();
        when(bookingRepository.save(any())).thenReturn(saved);

        Long id = service.confirmReservation("abc", 1L, "PAY123");
        assertEquals(999L, id);
        assertEquals(Seat.Status.BOOKED, s1.getStatus());
        assertEquals(Seat.Status.BOOKED, s2.getStatus());
    }
}
