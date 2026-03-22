package com.example.hotelapi.controller;

import com.example.hotelapi.dto.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@SuppressWarnings({"rawtypes", "unchecked"})
class HotelControllerIntegrationTest {

    @LocalServerPort
    private int port;

    private final RestTemplate restTemplate = new RestTemplate();

    private String getBaseUrl() {
        return "http://localhost:" + port;
    }

    @Test
    void getAllHotels_emptyList() {
        ResponseEntity<List> response = restTemplate.getForEntity(getBaseUrl() + "/property-view/hotels", List.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    void getHotelById_notFound() {
        try {
            restTemplate.getForEntity(getBaseUrl() + "/property-view/hotels/999", Map.class);
        } catch (Exception e) {
            assertThat(e.getMessage()).contains("404");
        }
    }

    @Test
    void createHotel_success() {
        AddressDto addressDto = AddressDto.builder()
                .houseNumber(9)
                .street("Pobediteley Avenue")
                .city("Minsk")
                .country("Belarus")
                .postCode("220004")
                .build();

        ContactsDto contactsDto = ContactsDto.builder()
                .phone("+375 17 309-80-00")
                .email("doubletreeminsk.info@hilton.com")
                .build();

        ArrivalTimeDto arrivalTimeDto = ArrivalTimeDto.builder()
                .checkIn("14:00")
                .checkOut("12:00")
                .build();

        HotelDto request = HotelDto.builder()
                .name("DoubleTree by Hilton Minsk")
                .description("Luxurious hotel in Minsk")
                .brand("Hilton")
                .address(addressDto)
                .contacts(contactsDto)
                .arrivalTime(arrivalTimeDto)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<HotelDto> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(getBaseUrl() + "/property-view/hotels", entity, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("id")).isNotNull();
        assertThat(response.getBody().get("name")).isEqualTo("DoubleTree by Hilton Minsk");
        assertThat(response.getBody().get("address")).isEqualTo("9 Pobediteley Avenue, Minsk, 220004, Belarus");
        assertThat(response.getBody().get("phone")).isEqualTo("+375 17 309-80-00");
    }

    @Test
    void createHotel_validationError_missingName() {
        AddressDto addressDto = AddressDto.builder()
                .houseNumber(9)
                .street("Test Street")
                .city("Minsk")
                .country("Belarus")
                .postCode("220004")
                .build();

        ContactsDto contactsDto = ContactsDto.builder()
                .phone("+375 17 309-80-00")
                .email("test@test.com")
                .build();

        HotelDto request = HotelDto.builder()
                .name("")
                .address(addressDto)
                .contacts(contactsDto)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<HotelDto> entity = new HttpEntity<>(request, headers);

        try {
            restTemplate.postForEntity(getBaseUrl() + "/property-view/hotels", entity, Map.class);
        } catch (Exception e) {
            assertThat(e.getMessage()).contains("400");
        }
    }

    @Test
    void createHotel_validationError_missingAddress() {
        ContactsDto contactsDto = ContactsDto.builder()
                .phone("+375 17 309-80-00")
                .email("test@test.com")
                .build();

        HotelDto request = HotelDto.builder()
                .name("Test Hotel")
                .contacts(contactsDto)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<HotelDto> entity = new HttpEntity<>(request, headers);

        try {
            restTemplate.postForEntity(getBaseUrl() + "/property-view/hotels", entity, Map.class);
        } catch (Exception e) {
            assertThat(e.getMessage()).contains("400");
        }
    }

    @Test
    void createHotel_validationError_missingContacts() {
        AddressDto addressDto = AddressDto.builder()
                .houseNumber(9)
                .street("Test Street")
                .city("Minsk")
                .country("Belarus")
                .postCode("220004")
                .build();

        HotelDto request = HotelDto.builder()
                .name("Test Hotel")
                .address(addressDto)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<HotelDto> entity = new HttpEntity<>(request, headers);

        try {
            restTemplate.postForEntity(getBaseUrl() + "/property-view/hotels", entity, Map.class);
        } catch (Exception e) {
            assertThat(e.getMessage()).contains("400");
        }
    }

    @Test
    void searchHotels_emptyResult() {
        ResponseEntity<List> response = restTemplate.getForEntity(getBaseUrl() + "/property-view/search?city=nonexistent", List.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    void getHistogram_byBrand_empty() {
        ResponseEntity<Map> response = restTemplate.getForEntity(getBaseUrl() + "/property-view/histogram/brand", Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsKey("data");
    }

    @Test
    void getHistogram_byCity_empty() {
        ResponseEntity<Map> response = restTemplate.getForEntity(getBaseUrl() + "/property-view/histogram/city", Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsKey("data");
    }

    @Test
    void getHistogram_byCountry_empty() {
        ResponseEntity<Map> response = restTemplate.getForEntity(getBaseUrl() + "/property-view/histogram/country", Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsKey("data");
    }

    @Test
    void getHistogram_byAmenities_empty() {
        ResponseEntity<Map> response = restTemplate.getForEntity(getBaseUrl() + "/property-view/histogram/amenities", Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsKey("data");
    }

    @Test
    void getHistogram_unknownParameter() {
        ResponseEntity<Map> response = restTemplate.getForEntity(getBaseUrl() + "/property-view/histogram/unknown", Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsKey("data");
    }

    @Test
    void fullWorkflow_createAndGetAndSearch() {
        // Create hotel
        AddressDto addressDto = AddressDto.builder()
                .houseNumber(9)
                .street("Pobediteley Avenue")
                .city("Minsk")
                .country("Belarus")
                .postCode("220004")
                .build();

        ContactsDto contactsDto = ContactsDto.builder()
                .phone("+375 17 309-80-00")
                .email("doubletreeminsk.info@hilton.com")
                .build();

        HotelDto request = HotelDto.builder()
                .name("DoubleTree by Hilton Minsk")
                .description("Luxurious hotel in Minsk")
                .brand("Hilton")
                .address(addressDto)
                .contacts(contactsDto)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<HotelDto> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Map> createResponse = restTemplate.postForEntity(getBaseUrl() + "/property-view/hotels", entity, Map.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        assert createResponse.getBody() != null;
        Number hotelId = (Number) createResponse.getBody().get("id");

        // Get hotel by ID
        ResponseEntity<Map> getResponse = restTemplate.getForEntity(getBaseUrl() + "/property-view/hotels/" + hotelId, Map.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assert getResponse.getBody() != null;
        assertThat(getResponse.getBody().get("id")).isIn(hotelId.intValue(), hotelId.longValue());
        assertThat(getResponse.getBody().get("name")).isEqualTo("DoubleTree by Hilton Minsk");
        assertThat(getResponse.getBody().get("brand")).isEqualTo("Hilton");

        // Search by city - should find at least our hotel
        ResponseEntity<List> searchByCityResponse = restTemplate.getForEntity(getBaseUrl() + "/property-view/search?city=minsk", List.class);
        assertThat(searchByCityResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(searchByCityResponse.getBody()).isNotEmpty();

        // Search by brand - should find at least our hotel
        ResponseEntity<List> searchByBrandResponse = restTemplate.getForEntity(getBaseUrl() + "/property-view/search?brand=hilton", List.class);
        assertThat(searchByBrandResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(searchByBrandResponse.getBody()).isNotEmpty();

        // Search by country - should find at least our hotel
        ResponseEntity<List> searchByCountryResponse = restTemplate.getForEntity(getBaseUrl() + "/property-view/search?country=belarus", List.class);
        assertThat(searchByCountryResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(searchByCountryResponse.getBody()).isNotEmpty();
    }

    @Test
    void addAmenities_workflow() {
        // Create hotel first
        AddressDto addressDto = AddressDto.builder()
                .houseNumber(9)
                .street("Pobediteley Avenue")
                .city("Minsk")
                .country("Belarus")
                .postCode("220004")
                .build();

        ContactsDto contactsDto = ContactsDto.builder()
                .phone("+375 17 309-80-00")
                .email("test@test.com")
                .build();

        HotelDto request = HotelDto.builder()
                .name("Test Hotel")
                .address(addressDto)
                .contacts(contactsDto)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<HotelDto> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Map> createResponse = restTemplate.postForEntity(getBaseUrl() + "/property-view/hotels", entity, Map.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        assert createResponse.getBody() != null;
        Long hotelId = ((Number) createResponse.getBody().get("id")).longValue();

        // Add amenities
        List<String> amenities = List.of("Free WiFi", "Free parking", "Fitness center");

        HttpHeaders amenityHeaders = new HttpHeaders();
        amenityHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<List<String>> amenityEntity = new HttpEntity<>(amenities, amenityHeaders);

        ResponseEntity<Map> addAmenitiesResponse = restTemplate.postForEntity(
                getBaseUrl() + "/property-view/hotels/" + hotelId + "/amenities", amenityEntity, Map.class);

        assertThat(addAmenitiesResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assert addAmenitiesResponse.getBody() != null;
        assertThat(addAmenitiesResponse.getBody().get("id")).isIn(hotelId.intValue(), hotelId);
        assertThat((List) addAmenitiesResponse.getBody().get("amenities")).hasSize(3);
        assertThat(((List) addAmenitiesResponse.getBody().get("amenities")).getFirst()).isEqualTo("Free WiFi");
    }
}
