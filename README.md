# URL Shortener API

Backend service that converts long URLs into short unique links and redirects users efficiently.  
Includes link expiration, click tracking, and basic usage statistics.

---

## Features

- Generate short URL from long URL
- Redirect short URL to original link
- Unique short code generation (6 characters)
- URL validation (format + reachable check)
- Expiration time (1 hour)
- Click tracking
- Last accessed tracking (today / yesterday / X days ago)
- Statistics API
- REST API architecture
- MySQL database storage

---

## Tech Stack

- Java
- Spring Boot
- Spring Data JPA
- MySQL
- Maven
- REST APIs

---

## Project Structure

url-shortener  
│  
├── Controller  
│   └── UrlController.java
│  
├── Service  
│   └── UrlService.java 
│  
├── Repository  
│   └── UrlRepo.java 
│  
├── Model  
│   └── UrlInfo.java 
│  
├── DTO  
│   ├── UrlDto.java 
│   ├── ResponseDTO.java 
│   └── StatisticsDTO.java 

---

## Database Design

Table: url_info

| Column | Description |
|--------|-------------|
| urlId | Primary key |
| originalUrl | Long URL |
| shortCode | Unique short identifier |
| createdAt | URL creation time |
| expireAt | Expiration time (1 hour) |
| lastAccessed | Last access date |
| clicks | Total number of clicks |

---

## API Endpoints

### 1. Create Short URL

POST /api/shorten

Request Body

{
  "url": "https://example.com/very-long-link"
}

Response

{
  "response": "http://localhost:8080/api/aB12xY"
}

---

### 2. Redirect to Original URL

GET /api/{shortCode}

Example

http://localhost:8080/api/aB12xY

Behavior

- Redirects to original URL
- Checks if URL exists
- Checks expiration time
- Increases click count
- Updates last accessed date

Response Status

302 → redirect to original URL  
404 → short code not found  
410 → link expired  

---

### 3. Get URL Statistics

GET /api/statistics/{shortCode}

Example Response

{
  "totalClicks": 5,
  "createdAt": "2026-04-15",
  "lastAccessed": "today"
}

Statistics include

- total clicks
- creation date
- last accessed info

---

## How It Works

1. User sends long URL using POST request.
2. Service validates URL:
   - must start with http or https
   - must be valid format
   - must be reachable
3. System checks if URL already exists in database.
4. If exists → return existing short code.
5. If not exists → generate new 6-character short code.
6. Store mapping in MySQL database.
7. Set expiration time to 1 hour.
8. Return short URL.

When short URL is accessed:

1. System finds short code in database.
2. Checks expiration time.
3. If expired → delete record and return error.
4. Increase click count.
5. Update last accessed date.
6. Redirect to original URL.

Statistics API calculates:

- number of clicks
- creation date
- last accessed relative time

(today / yesterday / X days ago)

---

## Validation Rules

URL must:

- not be empty
- be less than 2048 characters
- start with http:// or https://
- be valid URL format
- be reachable (HTTP status 200-399)

---

## How to Run

Clone repository

git clone <repository-url>

Configure MySQL in application.properties

spring.datasource.url=jdbc:mysql://localhost:3306/url_shortener  
spring.datasource.username=root  
spring.datasource.password=your_password  

Run application

mvn spring-boot:run

Test APIs using Postman or browser.

---

## Future Improvements

- custom short codes
- QR code generation
- link expiration customization
- analytics dashboard
- Redis caching
- Docker deployment
- authentication for managing URLs
- tracking IP address
- tracking device/browser

---

## Learning Outcomes

- REST API design
- Spring Boot architecture
- JPA database mapping
- URL validation techniques
- HTTP redirect handling
- expiration logic implementation
- analytics tracking basics
- layered backend structure
