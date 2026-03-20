package com.example.hotelapi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArrivalTimeDto {

    @NotBlank(message = "Check-in time is required")
    private String checkIn;

    private String checkOut;
}
