package com.example.hotelapi.repository;

import com.example.hotelapi.model.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long>, JpaSpecificationExecutor<Hotel> {

    List<Hotel> findByBrandContainingIgnoreCase(String brand);

    List<Hotel> findByAddress_CityContainingIgnoreCase(String city);

    List<Hotel> findByAddress_CountryContainingIgnoreCase(String country);

    List<Hotel> findByNameContainingIgnoreCase(String name);
}
