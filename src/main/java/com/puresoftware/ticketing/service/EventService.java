package com.puresoftware.ticketing.service;

import com.puresoftware.ticketing.domain.Event;
import com.puresoftware.ticketing.domain.Seat;
import com.puresoftware.ticketing.repository.EventRepository;
import com.puresoftware.ticketing.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final SeatRepository seatRepository;

    public List<Event> search(String city, String category, LocalDate from, LocalDate to) {
        LocalDateTime fromDt = from != null ? from.atStartOfDay() : null;
        LocalDateTime toDt = to != null ? to.atTime(LocalTime.MAX) : null;
        return eventRepository.findAll().stream()
                .filter(e -> city == null || Objects.equals(e.getCity(), city))
                .filter(e -> category == null || Objects.equals(e.getCategory(), category))
                .filter(e -> fromDt == null || !e.getEventDateTime().isBefore(fromDt))
                .filter(e -> toDt == null || !e.getEventDateTime().isAfter(toDt))
                .collect(Collectors.toList());
    }

    public List<Seat> getSeats(Long eventId) {
        return seatRepository.findByEventId(eventId);
    }
}
