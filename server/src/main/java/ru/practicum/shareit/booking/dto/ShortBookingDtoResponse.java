package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.shareit.booking.entity.BookingStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ShortBookingDtoResponse {

    private Integer id;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private LocalDateTime start;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private LocalDateTime end;
    private BookingStatus status;
    private Integer bookerId;
}
