package com.puresoftware.ticketing.web;

import com.puresoftware.ticketing.dto.EventDto;
import com.puresoftware.ticketing.dto.SeatDto;
import com.puresoftware.ticketing.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<EventDto>> search(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        List<EventDto> out = eventService.search(city, category, from, to)
                .stream().map(EventDto::from).collect(Collectors.toList());
        return ResponseEntity.ok(out);
    }

    @GetMapping("/{eventId}/seats")
    public ResponseEntity<List<SeatDto>> seats(@PathVariable Long eventId) {
        List<SeatDto> out = eventService.getSeats(eventId)
                .stream().map(SeatDto::from).collect(Collectors.toList());
        return ResponseEntity.ok(out);
    }
}
