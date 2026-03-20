package com.example.hotelapi.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hotels")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "brand")
    private String brand;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "houseNumber", column = @Column(name = "house_number")),
        @AttributeOverride(name = "street", column = @Column(name = "street")),
        @AttributeOverride(name = "city", column = @Column(name = "city")),
        @AttributeOverride(name = "country", column = @Column(name = "country")),
        @AttributeOverride(name = "postCode", column = @Column(name = "post_code"))
    })
    private Address address;

    @Embedded
    private Contacts contacts;

    @Embedded
    private ArrivalTime arrivalTime;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "hotel_amenities",
        joinColumns = @JoinColumn(name = "hotel_id")
    )
    @Column(name = "amenity")
    private List<String> amenities = new ArrayList<>();

    public String getFullAddress() {
        return address != null ? address.getFullAddress() : null;
    }

    public String getPhone() {
        return contacts != null ? contacts.getPhone() : null;
    }

    public String getEmail() {
        return contacts != null ? contacts.getEmail() : null;
    }
}
