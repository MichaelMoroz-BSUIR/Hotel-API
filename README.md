# Hotel API

A RESTful API application for managing hotels, built with Spring Boot, JPA, and Liquibase.

## Running the Application

### Using Maven

```bash
# Run the application
mvn spring-boot:run
```

The application will start on port **8092**.

## API Endpoints

All endpoints are prefixed with `/property-view`.

### 1. Get All Hotels

```http
GET /property-view/hotels
```

**Response Example:**
```json
[
  {
    "id": 1,
    "name": "DoubleTree by Hilton Minsk",
    "description": "Luxurious hotel in the heart of Minsk",
    "address": "9 Pobediteley Avenue, Minsk, 220004, Belarus",
    "phone": "+375 17 309-80-00"
  }
]
```

### 2. Get Hotel by ID

```http
GET /property-view/hotels/{id}
```

**Response Example:**
```json
{
  "id": 1,
  "name": "DoubleTree by Hilton Minsk",
  "description": "Luxurious hotel in the heart of Minsk",
  "brand": "Hilton",
  "address": {
    "houseNumber": 9,
    "street": "Pobediteley Avenue",
    "city": "Minsk",
    "country": "Belarus",
    "postCode": "220004"
  },
  "contacts": {
    "phone": "+375 17 309-80-00",
    "email": "doubletreeminsk.info@hilton.com"
  },
  "arrivalTime": {
    "checkIn": "14:00",
    "checkOut": "12:00"
  },
  "amenities": [
    "Free parking",
    "Free WiFi",
    "Non-smoking rooms",
    "Fitness center"
  ]
}
```

### 3. Search Hotels

```http
GET /property-view/search?name={name}&brand={brand}&city={city}&country={country}&amenities={amenities}
```

**Query Parameters:**
- `name` (optional) - Filter by hotel name
- `brand` (optional) - Filter by brand
- `city` (optional) - Filter by city
- `country` (optional) - Filter by country
- `amenities` (optional) - Filter by amenities (can be specified multiple times)

**Example:**
```http
GET /property-view/search?city=minsk&brand=hilton
```

### 4. Create Hotel

```http
POST /property-view/hotels
Content-Type: application/json
```

**Request Body:**
```json
{
  "name": "DoubleTree by Hilton Minsk",
  "description": "Luxurious hotel in the heart of Minsk",
  "brand": "Hilton",
  "address": {
    "houseNumber": 9,
    "street": "Pobediteley Avenue",
    "city": "Minsk",
    "country": "Belarus",
    "postCode": "220004"
  },
  "contacts": {
    "phone": "+375 17 309-80-00",
    "email": "doubletreeminsk.info@hilton.com"
  },
  "arrivalTime": {
    "checkIn": "14:00",
    "checkOut": "12:00"
  },
  "amenities": ["Free WiFi", "Parking"]
}
```

### 5. Add Amenities to Hotel

```http
POST /property-view/hotels/{id}/amenities
Content-Type: application/json
```

**Request Body:**
```json
[
  "Free parking",
  "Free WiFi",
  "Non-smoking rooms",
  "Fitness center"
]
```

### 6. Get Histogram Data

```http
GET /property-view/histogram/{param}
```

**Parameters:**
- `brand` - Group by hotel brand
- `city` - Group by city
- `country` - Group by country
- `amenities` - Group by amenities

**Example Response (city):**
```json
{
  "data": {
    "Minsk": 2,
    "Moscow": 1,
    "Gomel": 1
  }
}
```

## API Documentation

Swagger UI is available at:
```
http://localhost:8092/swagger-ui.html
```

## Database

### H2 Console

The H2 database console is available at:
```
http://localhost:8092/h2-console
```

**Connection Settings:**
- JDBC URL: `jdbc:h2:mem:hoteldb`
- Username: `sa`
- Password: (empty)

### Switching to Another Database

To switch from H2 to PostgreSQL or MySQL, update `application.yaml`:

**PostgreSQL:**
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/hoteldb
    username: postgres
    password: your_password
    driver-class-name: org.postgresql.Driver
  jpa:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
```

**MySQL:**
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/hoteldb
    username: root
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    database-platform: org.hibernate.dialect.MySQLDialect
```

Add the corresponding database driver dependency to `pom.xml`.

## Project Structure

```
src/
├── main/
│   ├── java/com/example/hotel_api/
│   │   ├── controller/       # REST controllers
│   │   ├── dto/              # Data Transfer Objects
│   │   ├── model/            # JPA entities
│   │   ├── repository/       # Data repositories
│   │   └── service/          # Business logic
│   └── resources/
│       ├── db/changelog/     # Liquibase migrations
│       └── application.yaml  # Configuration
└── test/
    └── java/com/example/hotel_api/
        ├── controller/       # Controller tests
        └── service/          # Service tests
```

## Testing

```bash
# Run all tests
mvn test

# Run with coverage
mvn clean test jacoco:report
```

## Building

```bash
# Build the application
mvn clean package

# Skip tests
mvn clean package -DskipTests
```

The JAR file will be created in `target/` directory.

## Running the JAR

```bash
java -jar target/hotel-api-0.0.1-SNAPSHOT.jar
```

## Design Patterns Used

- **Repository Pattern**: Data access abstraction
- **Service Layer Pattern**: Business logic separation
- **DTO Pattern**: Data transfer between layers
- **Dependency Injection**: Spring IoC container
