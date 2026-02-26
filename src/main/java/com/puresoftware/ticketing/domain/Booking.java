package com.puresoftware.ticketing.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Event event;

    @ManyToMany
    @JoinTable(name = "booking_seats",
            joinColumns = @JoinColumn(name = "booking_id"),
            inverseJoinColumns = @JoinColumn(name = "seat_id"))
    @Builder.Default
    private Set<Seat> seats = new HashSet<>();

    @Column(nullable = false)
    private LocalDateTime bookedAt;

    @Column(nullable = false)
    private String status;
}

