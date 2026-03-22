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
import org.springframework.data.jpa.domain.Specification;

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
        assertEquals("Test Hotel", result.getFirst().getName());
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

    @Test
    @SuppressWarnings("unchecked")
    void searchHotels_byName() {
        Hotel hotel1 = Hotel.builder()
                .id(1L)
                .name("DoubleTree by Hilton Minsk")
                .description("Description 1")
                .address(com.example.hotelapi.model.Address.builder()
                        .city("Minsk")
                        .country("Belarus")
                        .build())
                .contacts(com.example.hotelapi.model.Contacts.builder()
                        .phone("+375 17 309-80-00")
                        .build())
                .build();

        when(hotelRepository.findAll(any(Specification.class))).thenReturn(List.of(hotel1));

        List<HotelShortDto> result = hotelService.searchHotels("doubletree", null, null, null, null);

        assertEquals(1, result.size());
        assertEquals("DoubleTree by Hilton Minsk", result.getFirst().getName());
    }

    @Test
    @SuppressWarnings("unchecked")
    void searchHotels_byBrand() {
        Hotel hotel1 = Hotel.builder()
                .id(1L)
                .name("DoubleTree by Hilton Minsk")
                .brand("Hilton")
                .address(com.example.hotelapi.model.Address.builder()
                        .city("Minsk")
                        .country("Belarus")
                        .build())
                .contacts(com.example.hotelapi.model.Contacts.builder()
                        .phone("+375 17 309-80-00")
                        .build())
                .build();

        when(hotelRepository.findAll(any(Specification.class))).thenReturn(List.of(hotel1));

        List<HotelShortDto> result = hotelService.searchHotels(null, "hilton", null, null, null);

        assertEquals(1, result.size());
    }

    @Test
    @SuppressWarnings("unchecked")
    void searchHotels_byCity() {
        Hotel hotel1 = Hotel.builder()
                .id(1L)
                .name("DoubleTree by Hilton Minsk")
                .brand("Hilton")
                .address(com.example.hotelapi.model.Address.builder()
                        .city("Minsk")
                        .country("Belarus")
                        .build())
                .contacts(com.example.hotelapi.model.Contacts.builder()
                        .phone("+375 17 309-80-00")
                        .build())
                .build();

        when(hotelRepository.findAll(any(Specification.class))).thenReturn(List.of(hotel1));

        List<HotelShortDto> result = hotelService.searchHotels(null, null, "minsk", null, null);

        assertEquals(1, result.size());
        assertTrue(result.getFirst().getAddress().contains("Minsk"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void searchHotels_byCountry() {
        Hotel hotel1 = Hotel.builder()
                .id(1L)
                .name("DoubleTree by Hilton Minsk")
                .brand("Hilton")
                .address(com.example.hotelapi.model.Address.builder()
                        .city("Minsk")
                        .country("Belarus")
                        .build())
                .contacts(com.example.hotelapi.model.Contacts.builder()
                        .phone("+375 17 309-80-00")
                        .build())
                .build();

        when(hotelRepository.findAll(any(Specification.class))).thenReturn(List.of(hotel1));

        List<HotelShortDto> result = hotelService.searchHotels(null, null, null, "belarus", null);

        assertEquals(1, result.size());
        assertTrue(result.getFirst().getAddress().contains("Belarus"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void searchHotels_byAmenities() {
        Hotel hotel1 = Hotel.builder()
                .id(1L)
                .name("Hotel with WiFi")
                .brand("Hilton")
                .address(com.example.hotelapi.model.Address.builder()
                        .city("Minsk")
                        .country("Belarus")
                        .build())
                .contacts(com.example.hotelapi.model.Contacts.builder()
                        .phone("+375 17 309-80-00")
                        .build())
                .amenities(List.of("WiFi", "Parking"))
                .build();

        when(hotelRepository.findAll(any(Specification.class))).thenReturn(List.of(hotel1));

        List<HotelShortDto> result = hotelService.searchHotels(null, null, null, null, List.of("WiFi"));

        assertEquals(1, result.size());
        assertTrue(result.getFirst().getName().contains("WiFi"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void searchHotels_multipleParameters() {
        Hotel hotel1 = Hotel.builder()
                .id(1L)
                .name("DoubleTree by Hilton Minsk")
                .brand("Hilton")
                .address(com.example.hotelapi.model.Address.builder()
                        .city("Minsk")
                        .country("Belarus")
                        .build())
                .contacts(com.example.hotelapi.model.Contacts.builder()
                        .phone("+375 17 309-80-00")
                        .build())
                .amenities(List.of("WiFi", "Parking"))
                .build();

        when(hotelRepository.findAll(any(Specification.class))).thenReturn(List.of(hotel1));

        List<HotelShortDto> result = hotelService.searchHotels(
                "doubletree", "hilton", "minsk", "belarus", List.of("WiFi"));

        assertEquals(1, result.size());
        assertEquals("DoubleTree by Hilton Minsk", result.getFirst().getName());
    }

    @Test
    @SuppressWarnings("unchecked")
    void searchHotels_emptyResult() {
        when(hotelRepository.findAll(any(Specification.class))).thenReturn(List.of());

        List<HotelShortDto> result = hotelService.searchHotels("nonexistent", null, null, null, null);

        assertTrue(result.isEmpty());
    }

    @Test
    void createHotel_withNullRequest() {
        assertThrows(IllegalArgumentException.class, () -> hotelService.createHotel(null));
    }

    @Test
    void createHotel_withoutAmenities() {
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

        HotelDto request = HotelDto.builder()
                .name("Test Hotel")
                .description("Test Description")
                .brand("Test Brand")
                .address(addressDto)
                .contacts(contactsDto)
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
                .amenities(new ArrayList<>())
                .build();

        when(hotelRepository.save(any(Hotel.class))).thenReturn(savedHotel);

        HotelShortDto result = hotelService.createHotel(request);

        assertNotNull(result);
        assertEquals("Test Hotel", result.getName());
        assertNotNull(result.getAddress());
    }

    @Test
    void createHotel_withAmenities() {
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

        HotelDto request = HotelDto.builder()
                .name("Test Hotel")
                .description("Test Description")
                .brand("Test Brand")
                .address(addressDto)
                .contacts(contactsDto)
                .amenities(List.of("WiFi", "Pool"))
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
                .amenities(new ArrayList<>(List.of("WiFi", "Pool")))
                .build();

        when(hotelRepository.save(any(Hotel.class))).thenReturn(savedHotel);

        HotelShortDto result = hotelService.createHotel(request);

        assertNotNull(result);
        assertEquals("Test Hotel", result.getName());
    }

    @Test
    void addAmenities_withNullList() {
        Hotel hotel = Hotel.builder()
                .id(1L)
                .name("Test Hotel")
                .amenities(new ArrayList<>())
                .build();

        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));

        assertThrows(IllegalArgumentException.class, () -> hotelService.addAmenities(1L, null));
    }

    @Test
    void addAmenities_withEmptyList() {
        Hotel hotel = Hotel.builder()
                .id(1L)
                .name("Test Hotel")
                .amenities(new ArrayList<>())
                .build();

        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));

        assertThrows(IllegalArgumentException.class, () -> hotelService.addAmenities(1L, List.of()));
    }

    @Test
    void addAmenities_hotelNotFound() {
        when(hotelRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(jakarta.persistence.EntityNotFoundException.class,
                () -> hotelService.addAmenities(999L, List.of("WiFi")));
    }

    @Test
    void addAmenities_duplicateValues() {
        Hotel hotel = Hotel.builder()
                .id(1L)
                .name("Test Hotel")
                .amenities(new ArrayList<>(List.of("WiFi")))
                .build();

        List<String> newAmenities = List.of("WiFi", "Parking");

        Hotel updatedHotel = Hotel.builder()
                .id(1L)
                .name("Test Hotel")
                .amenities(new ArrayList<>(List.of("WiFi", "Parking")))
                .build();

        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
        when(hotelRepository.save(any(Hotel.class))).thenReturn(updatedHotel);

        HotelDto result = hotelService.addAmenities(1L, newAmenities);

        assertEquals(2, result.getAmenities().size());
        verify(hotelRepository).save(hotel);
    }

    @Test
    void getHistogramByBrand_emptyList() {
        when(hotelRepository.findAll()).thenReturn(List.of());

        Map<String, Long> result = hotelService.getHistogramByBrand();

        assertTrue(result.isEmpty());
    }

    @Test
    void getHistogramByBrand_withNullBrand() {
        Hotel hotel1 = Hotel.builder()
                .brand("Hilton")
                .build();

        Hotel hotel2 = Hotel.builder()
                .brand(null)
                .build();

        Hotel hotel3 = Hotel.builder()
                .brand("")
                .build();

        when(hotelRepository.findAll()).thenReturn(List.of(hotel1, hotel2, hotel3));

        Map<String, Long> result = hotelService.getHistogramByBrand();

        assertEquals(1, result.size());
        assertEquals(1L, result.get("Hilton"));
    }

    @Test
    void getHistogramByCity_emptyList() {
        when(hotelRepository.findAll()).thenReturn(List.of());

        Map<String, Long> result = hotelService.getHistogramByCity();

        assertTrue(result.isEmpty());
    }

    @Test
    void getHistogramByCity_withNullAddress() {
        Hotel hotel1 = Hotel.builder()
                .address(com.example.hotelapi.model.Address.builder()
                        .city("Minsk")
                        .build())
                .build();

        Hotel hotel2 = Hotel.builder()
                .address(null)
                .build();

        when(hotelRepository.findAll()).thenReturn(List.of(hotel1, hotel2));

        Map<String, Long> result = hotelService.getHistogramByCity();

        assertEquals(1, result.size());
        assertEquals(1L, result.get("Minsk"));
    }

    @Test
    void getHistogramByCountry_emptyList() {
        when(hotelRepository.findAll()).thenReturn(List.of());

        Map<String, Long> result = hotelService.getHistogramByCountry();

        assertTrue(result.isEmpty());
    }

    @Test
    void getHistogramByAmenities_emptyList() {
        when(hotelRepository.findAll()).thenReturn(List.of());

        Map<String, Long> result = hotelService.getHistogramByAmenities();

        assertTrue(result.isEmpty());
    }

    @Test
    void getHistogramByAmenities_withNullAmenities() {
        Hotel hotel1 = Hotel.builder()
                .amenities(List.of("WiFi"))
                .build();

        Hotel hotel2 = Hotel.builder()
                .amenities(null)
                .build();

        Hotel hotel3 = Hotel.builder()
                .amenities(List.of())
                .build();

        when(hotelRepository.findAll()).thenReturn(List.of(hotel1, hotel2, hotel3));

        Map<String, Long> result = hotelService.getHistogramByAmenities();

        assertEquals(1, result.size());
        assertEquals(1L, result.get("WiFi"));
    }
}
