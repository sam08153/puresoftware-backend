package com.puresoftware.ticketing.service;

import com.puresoftware.ticketing.domain.Event;
import com.puresoftware.ticketing.domain.Seat;
import com.puresoftware.ticketing.repository.EventRepository;
import com.puresoftware.ticketing.repository.SeatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class EventServiceTest {
    private EventRepository eventRepository;
    private SeatRepository seatRepository;
    private EventService service;

    @BeforeEach
    void setup() {
        eventRepository = mock(EventRepository.class);
        seatRepository = mock(SeatRepository.class);
        service = new EventService(eventRepository, seatRepository);
    }

    @Test
    void search_filtersByCityCategoryAndDateRange() {
        Event e1 = Event.builder().id(1L).title("A").category("Music").city("Pune")
                .eventDateTime(LocalDateTime.of(2030, Month.JANUARY, 5, 19, 0)).build();
        Event e2 = Event.builder().id(2L).title("B").category("Theatre").city("NYC")
                .eventDateTime(LocalDateTime.of(2030, Month.JANUARY, 10, 19, 0)).build();
        when(eventRepository.findAll()).thenReturn(List.of(e1, e2));

        List<Event> res = service.search("Pune", "Music", LocalDate.of(2030, 1, 1), LocalDate.of(2030, 1, 6));
        assertEquals(1, res.size());
        assertEquals(1L, res.get(0).getId());
    }

    @Test
    void getSeats_returnsList() {
        Seat s = Seat.builder().id(100L).rowLabel("A").seatNumber(1).tier("VIP").price(new BigDecimal("10.00")).build();
        when(seatRepository.findByEventId(10L)).thenReturn(List.of(s));
        List<Seat> out = service.getSeats(10L);
        assertEquals(1, out.size());
        assertEquals(100L, out.get(0).getId());
        verify(seatRepository).findByEventId(10L);
    }
}
