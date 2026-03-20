package com.example.hotelapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelShortDto {
    private Long id;
    private String name;
    private String description;
    private String address;
    private String phone;
}
