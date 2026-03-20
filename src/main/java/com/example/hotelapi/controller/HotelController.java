package com.example.hotelapi.controller;

import com.example.hotelapi.dto.HistogramResponseDto;
import com.example.hotelapi.dto.HotelDto;
import com.example.hotelapi.dto.HotelShortDto;
import com.example.hotelapi.service.HotelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/property-view")
@RequiredArgsConstructor
@Tag(name = "Hotel API", description = "RESTful API for hotel management")
public class HotelController {

    private final HotelService hotelService;

    @GetMapping("/hotels")
    @Operation(summary = "Get all hotels", description = "Returns a list of all hotels with short information")
    public ResponseEntity<List<HotelShortDto>> getAllHotels() {
        return ResponseEntity.ok(hotelService.getAllHotels());
    }

    @GetMapping("/hotels/{id}")
    @Operation(summary = "Get hotel by ID", description = "Returns detailed information about a specific hotel")
    public ResponseEntity<HotelDto> getHotelById(
            @Parameter(description = "Hotel ID") @PathVariable Long id) {
        return ResponseEntity.ok(hotelService.getHotelById(id));
    }

    @GetMapping("/search")
    @Operation(summary = "Search hotels", description = "Search hotels by name, brand, city, country, or amenities")
    public ResponseEntity<List<HotelShortDto>> searchHotels(
            @Parameter(description = "Hotel name") @RequestParam(required = false) String name,
            @Parameter(description = "Hotel brand") @RequestParam(required = false) String brand,
            @Parameter(description = "City") @RequestParam(required = false) String city,
            @Parameter(description = "Country") @RequestParam(required = false) String country,
            @Parameter(description = "Amenities") @RequestParam(required = false) List<String> amenities) {
        return ResponseEntity.ok(hotelService.searchHotels(name, brand, city, country, amenities));
    }

    @PostMapping("/hotels")
    @Operation(summary = "Create a new hotel", description = "Creates a new hotel with the provided information")
    public ResponseEntity<HotelShortDto> createHotel(
            @Valid @RequestBody HotelDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(hotelService.createHotel(request));
    }

    @PostMapping("/hotels/{id}/amenities")
    @Operation(summary = "Add amenities to hotel", description = "Adds a list of amenities to an existing hotel")
    public ResponseEntity<HotelDto> addAmenities(
            @Parameter(description = "Hotel ID") @PathVariable Long id,
            @RequestBody List<String> amenities) {
        return ResponseEntity.ok(hotelService.addAmenities(id, amenities));
    }

    @GetMapping("/histogram/{param}")
    @Operation(summary = "Get histogram data", description = "Returns hotel counts grouped by the specified parameter")
    public ResponseEntity<HistogramResponseDto> getHistogram(
            @Parameter(description = "Parameter to group by: brand, city, country, or amenities") 
            @PathVariable String param) {
        
        Map<String, Long> data = switch (param.toLowerCase()) {
            case "brand" -> hotelService.getHistogramByBrand();
            case "city" -> hotelService.getHistogramByCity();
            case "country" -> hotelService.getHistogramByCountry();
            case "amenities" -> hotelService.getHistogramByAmenities();
            default -> new HashMap<>();
        };

        return ResponseEntity.ok(new HistogramResponseDto(data));
    }
}
