package com.example.hotelapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArrivalTime {

    @Column(name = "check_in_time")
    private String checkIn;

    @Column(name = "check_out_time")
    private String checkOut;
}
