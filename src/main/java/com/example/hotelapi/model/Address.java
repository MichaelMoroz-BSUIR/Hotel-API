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
public class Address {

    @Column(name = "house_number")
    private Integer houseNumber;

    @Column(name = "street")
    private String street;

    @Column(name = "city")
    private String city;

    @Column(name = "country")
    private String country;

    @Column(name = "post_code")
    private String postCode;

    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        if (houseNumber != null) {
            sb.append(houseNumber).append(" ");
        }
        if (street != null) {
            sb.append(street);
        }
        if (city != null) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(city);
        }
        if (postCode != null) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(postCode);
        }
        if (country != null) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(country);
        }
        return sb.toString();
    }
}
