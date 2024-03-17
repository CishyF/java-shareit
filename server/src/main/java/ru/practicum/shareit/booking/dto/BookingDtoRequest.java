package ru.practicum.shareit.booking.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class BookingDtoRequest {

    @NotNull
    private LocalDateTime start;
    @NotNull
    private LocalDateTime end;
    private Integer itemId;
}
