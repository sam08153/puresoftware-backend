package com.puresoftware.ticketing.config;

import com.puresoftware.ticketing.domain.Event;
import com.puresoftware.ticketing.domain.Seat;
import com.puresoftware.ticketing.domain.User;
import com.puresoftware.ticketing.domain.Venue;
import com.puresoftware.ticketing.repository.EventRepository;
import com.puresoftware.ticketing.repository.SeatRepository;
import com.puresoftware.ticketing.repository.UserRepository;
import com.puresoftware.ticketing.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
    private final UserRepository userRepository;
    private final VenueRepository venueRepository;
    private final EventRepository eventRepository;
    private final SeatRepository seatRepository;

    @Override
    public void run(String... args) {
        if (eventRepository.count() > 0) return;

        User u = User.builder().email("demo@user.com").name("Demo User").build();
        userRepository.save(u);

        Venue v = Venue.builder().name("Grand Hall").city("Pune").build();
        venueRepository.save(v);

        Event e = Event.builder()
                .venue(v)
                .title("Rock Night")
                .category("Music")
                .city("Pune")
                .eventDateTime(LocalDateTime.now().plusDays(7))
                .build();
        eventRepository.save(e);

        List<Seat> seats = new ArrayList<>();
        for (char row = 'A'; row <= 'B'; row++) {
            for (int n = 1; n <= 20; n++) {
                seats.add(Seat.builder()
                        .event(e)
                        .rowLabel(String.valueOf(row))
                        .seatNumber(n)
                        .tier(row == 'A' ? "VIP" : "REGULAR")
                        .price(row == 'A' ? new BigDecimal("1500.00") : new BigDecimal("800.00"))
                        .status(Seat.Status.AVAILABLE)
                        .build());
            }
        }
        seatRepository.saveAll(seats);
    }
}
