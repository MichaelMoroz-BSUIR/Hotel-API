package com.example.hotelapi.service;

import com.example.hotelapi.dto.*;
import com.example.hotelapi.model.Hotel;
import com.example.hotelapi.repository.HotelRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HotelServiceTest {

    @Mock
    private HotelRepository hotelRepository;

    @InjectMocks
    private HotelService hotelService;

    @Test
    void getAllHotels_emptyList() {
        when(hotelRepository.findAll()).thenReturn(new ArrayList<>());

        List<HotelShortDto> result = hotelService.getAllHotels();

        assertTrue(result.isEmpty());
        verify(hotelRepository).findAll();
    }

    @Test
    void getAllHotels_withData() {
        Hotel hotel = Hotel.builder()
                .id(1L)
                .name("Test Hotel")
                .description("Test Description")
                .address(com.example.hotelapi.model.Address.builder()
                        .houseNumber(123)
                        .street("Test Street")
                        .city("Test City")
                        .country("Test Country")
                        .postCode("12345")
                        .build())
                .contacts(com.example.hotelapi.model.Contacts.builder()
                        .phone("+1234567890")
                        .build())
                .build();

        when(hotelRepository.findAll()).thenReturn(List.of(hotel));

        List<HotelShortDto> result = hotelService.getAllHotels();

        assertEquals(1, result.size());
        assertEquals("Test Hotel", result.get(0).getName());
        verify(hotelRepository).findAll();
    }

    @Test
    void getHotelById_success() {
        Hotel hotel = Hotel.builder()
                .id(1L)
                .name("Test Hotel")
                .description("Test Description")
                .brand("Test Brand")
                .address(com.example.hotelapi.model.Address.builder()
                        .houseNumber(123)
                        .street("Test Street")
                        .city("Test City")
                        .country("Test Country")
                        .postCode("12345")
                        .build())
                .contacts(com.example.hotelapi.model.Contacts.builder()
                        .phone("+1234567890")
                        .email("test@example.com")
                        .build())
                .arrivalTime(com.example.hotelapi.model.ArrivalTime.builder()
                        .checkIn("14:00")
                        .checkOut("12:00")
                        .build())
                .amenities(List.of("WiFi", "Parking"))
                .build();

        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));

        HotelDto result = hotelService.getHotelById(1L);

        assertEquals("Test Hotel", result.getName());
        assertEquals("Test Brand", result.getBrand());
        assertEquals(2, result.getAmenities().size());
        verify(hotelRepository).findById(1L);
    }

    @Test
    void getHotelById_notFound() {
        when(hotelRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> hotelService.getHotelById(999L));
        verify(hotelRepository).findById(999L);
    }

    @Test
    void createHotel_success() {
        AddressDto addressDto = AddressDto.builder()
                .houseNumber(123)
                .street("Test Street")
                .city("Test City")
                .country("Test Country")
                .postCode("12345")
                .build();

        ContactsDto contactsDto = ContactsDto.builder()
                .phone("+1234567890")
                .email("test@example.com")
                .build();

        ArrivalTimeDto arrivalTimeDto = ArrivalTimeDto.builder()
                .checkIn("14:00")
                .checkOut("12:00")
                .build();

        HotelDto request = HotelDto.builder()
                .name("Test Hotel")
                .description("Test Description")
                .brand("Test Brand")
                .address(addressDto)
                .contacts(contactsDto)
                .arrivalTime(arrivalTimeDto)
                .build();

        Hotel savedHotel = Hotel.builder()
                .id(1L)
                .name("Test Hotel")
                .description("Test Description")
                .brand("Test Brand")
                .address(com.example.hotelapi.model.Address.builder()
                        .houseNumber(123)
                        .street("Test Street")
                        .city("Test City")
                        .country("Test Country")
                        .postCode("12345")
                        .build())
                .contacts(com.example.hotelapi.model.Contacts.builder()
                        .phone("+1234567890")
                        .email("test@example.com")
                        .build())
                .arrivalTime(com.example.hotelapi.model.ArrivalTime.builder()
                        .checkIn("14:00")
                        .checkOut("12:00")
                        .build())
                .build();

        when(hotelRepository.save(any(Hotel.class))).thenReturn(savedHotel);

        HotelShortDto result = hotelService.createHotel(request);

        assertNotNull(result);
        assertEquals("Test Hotel", result.getName());
        verify(hotelRepository).save(any(Hotel.class));
    }

    @Test
    void addAmenities_success() {
        Hotel hotel = Hotel.builder()
                .id(1L)
                .name("Test Hotel")
                .amenities(new ArrayList<>())
                .build();

        List<String> newAmenities = List.of("WiFi", "Parking");

        Hotel updatedHotel = Hotel.builder()
                .id(1L)
                .name("Test Hotel")
                .amenities(new ArrayList<>(newAmenities))
                .build();

        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
        when(hotelRepository.save(any(Hotel.class))).thenReturn(updatedHotel);

        HotelDto result = hotelService.addAmenities(1L, newAmenities);

        assertEquals(2, result.getAmenities().size());
        verify(hotelRepository).findById(1L);
        verify(hotelRepository).save(hotel);
    }

    @Test
    void getHistogramByBrand() {
        Hotel hotel1 = Hotel.builder()
                .brand("Hilton")
                .address(com.example.hotelapi.model.Address.builder().city("Minsk").country("Belarus").build())
                .build();

        Hotel hotel2 = Hotel.builder()
                .brand("Hilton")
                .address(com.example.hotelapi.model.Address.builder().city("Moscow").country("Russia").build())
                .build();

        Hotel hotel3 = Hotel.builder()
                .brand("Marriott")
                .address(com.example.hotelapi.model.Address.builder().city("Minsk").country("Belarus").build())
                .build();

        when(hotelRepository.findAll()).thenReturn(List.of(hotel1, hotel2, hotel3));

        Map<String, Long> result = hotelService.getHistogramByBrand();

        assertEquals(2, result.get("Hilton"));
        assertEquals(1, result.get("Marriott"));
    }

    @Test
    void getHistogramByCity() {
        Hotel hotel1 = Hotel.builder()
                .brand("Hilton")
                .address(com.example.hotelapi.model.Address.builder().city("Minsk").country("Belarus").build())
                .build();

        Hotel hotel2 = Hotel.builder()
                .brand("Hilton")
                .address(com.example.hotelapi.model.Address.builder().city("Minsk").country("Russia").build())
                .build();

        Hotel hotel3 = Hotel.builder()
                .brand("Marriott")
                .address(com.example.hotelapi.model.Address.builder().city("Moscow").country("Russia").build())
                .build();

        when(hotelRepository.findAll()).thenReturn(List.of(hotel1, hotel2, hotel3));

        Map<String, Long> result = hotelService.getHistogramByCity();

        assertEquals(2, result.get("Minsk"));
        assertEquals(1, result.get("Moscow"));
    }

    @Test
    void getHistogramByCountry() {
        Hotel hotel1 = Hotel.builder()
                .brand("Hilton")
                .address(com.example.hotelapi.model.Address.builder().city("Minsk").country("Belarus").build())
                .build();

        Hotel hotel2 = Hotel.builder()
                .brand("Hilton")
                .address(com.example.hotelapi.model.Address.builder().city("Moscow").country("Russia").build())
                .build();

        Hotel hotel3 = Hotel.builder()
                .brand("Marriott")
                .address(com.example.hotelapi.model.Address.builder().city("Gomel").country("Belarus").build())
                .build();

        when(hotelRepository.findAll()).thenReturn(List.of(hotel1, hotel2, hotel3));

        Map<String, Long> result = hotelService.getHistogramByCountry();

        assertEquals(2, result.get("Belarus"));
        assertEquals(1, result.get("Russia"));
    }

    @Test
    void getHistogramByAmenities() {
        Hotel hotel1 = Hotel.builder()
                .amenities(List.of("WiFi", "Parking"))
                .build();

        Hotel hotel2 = Hotel.builder()
                .amenities(List.of("WiFi", "Pool"))
                .build();

        when(hotelRepository.findAll()).thenReturn(List.of(hotel1, hotel2));

        Map<String, Long> result = hotelService.getHistogramByAmenities();

        assertEquals(2, result.get("WiFi"));
        assertEquals(1, result.get("Parking"));
        assertEquals(1, result.get("Pool"));
    }
}
