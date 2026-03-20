package com.example.hotelapi.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelDto {

    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    private String brand;

    @Valid
    @NotNull(message = "Address is required")
    private AddressDto address;

    @Valid
    @NotNull(message = "Contacts are required")
    private ContactsDto contacts;

    private ArrivalTimeDto arrivalTime;

    private List<String> amenities;
}
