package com.example.hotelapi.service;

import com.example.hotelapi.dto.*;
import com.example.hotelapi.model.Hotel;
import com.example.hotelapi.repository.HotelRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HotelService {

    private final HotelRepository hotelRepository;

    public List<HotelShortDto> getAllHotels() {
        return hotelRepository.findAll().stream()
                .map(this::toShortDto)
                .toList();
    }

    public HotelDto getHotelById(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Hotel not found with id: " + id));
        return toFullDto(hotel);
    }

    public List<HotelShortDto> searchHotels(String name, String brand, String city, String country, List<String> amenities) {
        Specification<Hotel> spec = Specification.where((root, query, cb) -> cb.conjunction());

        if (name != null && !name.isBlank()) {
            spec = spec.and((root, query, cb) ->
                cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
        }

        if (brand != null && !brand.isBlank()) {
            spec = spec.and((root, query, cb) ->
                cb.like(cb.lower(root.get("brand")), "%" + brand.toLowerCase() + "%"));
        }

        if (city != null && !city.isBlank()) {
            spec = spec.and((root, query, cb) ->
                cb.like(cb.lower(root.get("address").get("city")), "%" + city.toLowerCase() + "%"));
        }

        if (country != null && !country.isBlank()) {
            spec = spec.and((root, query, cb) ->
                cb.like(cb.lower(root.get("address").get("country")), "%" + country.toLowerCase() + "%"));
        }

        if (amenities != null && !amenities.isEmpty()) {
            spec = spec.and((root, query, cb) -> {
                jakarta.persistence.criteria.Join<Hotel, String> join = root.join("amenities");
                return cb.lower(join).in(amenities.stream().map(String::toLowerCase).toList());
            });
        }

        return hotelRepository.findAll(spec).stream()
                .map(this::toShortDto)
                .toList();
    }

    @Transactional
    public HotelShortDto createHotel(HotelDto request) {
        if (request == null) {
            throw new IllegalArgumentException("Hotel request cannot be null");
        }

        Hotel hotel = Hotel.builder()
                .name(request.getName())
                .description(request.getDescription())
                .brand(request.getBrand())
                .address(toAddress(request.getAddress()))
                .contacts(toContacts(request.getContacts()))
                .arrivalTime(request.getArrivalTime() != null ? toArrivalTime(request.getArrivalTime()) : null)
                .amenities(request.getAmenities() != null ? new ArrayList<>(request.getAmenities()) : new ArrayList<>())
                .build();

        Hotel saved = hotelRepository.save(hotel);
        return toShortDto(saved);
    }

    @Transactional
    public HotelDto addAmenities(Long hotelId, List<String> amenities) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new EntityNotFoundException("Hotel not found with id: " + hotelId));

        if (amenities == null || amenities.isEmpty()) {
            throw new IllegalArgumentException("Amenities list cannot be null or empty");
        }

        if (hotel.getAmenities() == null) {
            hotel.setAmenities(new ArrayList<>());
        }

        for (String amenity : amenities) {
            if (!hotel.getAmenities().contains(amenity)) {
                hotel.getAmenities().add(amenity);
            }
        }

        Hotel saved = hotelRepository.save(hotel);
        return toFullDto(saved);
    }

    public Map<String, Long> getHistogramByBrand() {
        List<Hotel> hotels = hotelRepository.findAll();
        return hotels.stream()
                .filter(h -> h.getBrand() != null && !h.getBrand().isBlank())
                .collect(Collectors.groupingBy(Hotel::getBrand, Collectors.counting()));
    }

    public Map<String, Long> getHistogramByCity() {
        List<Hotel> hotels = hotelRepository.findAll();
        return hotels.stream()
                .filter(h -> h.getAddress() != null && h.getAddress().getCity() != null)
                .collect(Collectors.groupingBy(h -> h.getAddress().getCity(), Collectors.counting()));
    }

    public Map<String, Long> getHistogramByCountry() {
        List<Hotel> hotels = hotelRepository.findAll();
        return hotels.stream()
                .filter(h -> h.getAddress() != null && h.getAddress().getCountry() != null)
                .collect(Collectors.groupingBy(h -> h.getAddress().getCountry(), Collectors.counting()));
    }

    public Map<String, Long> getHistogramByAmenities() {
        List<Hotel> hotels = hotelRepository.findAll();
        return hotels.stream()
                .filter(h -> h.getAmenities() != null && !h.getAmenities().isEmpty())
                .flatMap(h -> h.getAmenities().stream())
                .collect(Collectors.groupingBy(a -> a, Collectors.counting()));
    }

    private HotelShortDto toShortDto(Hotel hotel) {
        return HotelShortDto.builder()
                .id(hotel.getId())
                .name(hotel.getName())
                .description(hotel.getDescription())
                .address(hotel.getFullAddress())
                .phone(hotel.getPhone())
                .build();
    }

    private HotelDto toFullDto(Hotel hotel) {
        AddressDto addressDto = hotel.getAddress() != null ? AddressDto.builder()
                .houseNumber(hotel.getAddress().getHouseNumber())
                .street(hotel.getAddress().getStreet())
                .city(hotel.getAddress().getCity())
                .country(hotel.getAddress().getCountry())
                .postCode(hotel.getAddress().getPostCode())
                .build() : null;

        ContactsDto contactsDto = hotel.getContacts() != null ? ContactsDto.builder()
                .phone(hotel.getContacts().getPhone())
                .email(hotel.getContacts().getEmail())
                .build() : null;

        ArrivalTimeDto arrivalTimeDto = hotel.getArrivalTime() != null ? ArrivalTimeDto.builder()
                .checkIn(hotel.getArrivalTime().getCheckIn())
                .checkOut(hotel.getArrivalTime().getCheckOut())
                .build() : null;

        return HotelDto.builder()
                .id(hotel.getId())
                .name(hotel.getName())
                .description(hotel.getDescription())
                .brand(hotel.getBrand())
                .address(addressDto)
                .contacts(contactsDto)
                .arrivalTime(arrivalTimeDto)
                .amenities(hotel.getAmenities() != null ? new ArrayList<>(hotel.getAmenities()) : new ArrayList<>())
                .build();
    }

    private com.example.hotelapi.model.Address toAddress(AddressDto dto) {
        return com.example.hotelapi.model.Address.builder()
                .houseNumber(dto.getHouseNumber())
                .street(dto.getStreet())
                .city(dto.getCity())
                .country(dto.getCountry())
                .postCode(dto.getPostCode())
                .build();
    }

    private com.example.hotelapi.model.Contacts toContacts(ContactsDto dto) {
        return com.example.hotelapi.model.Contacts.builder()
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .build();
    }

    private com.example.hotelapi.model.ArrivalTime toArrivalTime(ArrivalTimeDto dto) {
        return com.example.hotelapi.model.ArrivalTime.builder()
                .checkIn(dto.getCheckIn())
                .checkOut(dto.getCheckOut())
                .build();
    }
}
