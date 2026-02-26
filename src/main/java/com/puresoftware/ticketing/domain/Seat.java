package com.puresoftware.ticketing.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "seats", uniqueConstraints = {
        @UniqueConstraint(name = "uk_event_row_num", columnNames = {"event_id", "rowLabel", "seatNumber"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Event event;

    @Column(nullable = false)
    private String rowLabel;

    @Column(nullable = false)
    private Integer seatNumber;

    @Column(nullable = false)
    private String tier;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Version
    private Long version;

    public enum Status {
        AVAILABLE,
        HELD,
        BOOKED
    }
}

