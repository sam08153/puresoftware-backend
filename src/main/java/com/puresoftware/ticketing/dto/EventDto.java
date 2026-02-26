package com.puresoftware.ticketing.dto;

import com.puresoftware.ticketing.domain.Event;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class EventDto {
    private Long id;
    private String title;
    private String category;
    private String city;
    private LocalDateTime eventDateTime;

    public static EventDto from(Event e) {
        return new EventDto(e.getId(), e.getTitle(), e.getCategory(), e.getCity(), e.getEventDateTime());
    }
}

