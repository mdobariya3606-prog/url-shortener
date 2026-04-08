# URL Shortener API

Backend service that converts long URLs into short unique links and redirects users efficiently.

## Features

- Generate short URL
- Redirect to original URL
- Unique key generation
- Database storage for URL mapping
- REST API design

## Tech Stack

- Java
- Spring Boot
- Spring Data JPA
- MySQL
- REST APIs
- Maven

## API Endpoints

POST /shorten
Request body:
{
  "url": "https://example.com/very-long-link"
}

Response:
{
  "shortUrl": "http://localhost:8080/abc123"
}

GET /{shortKey}

Redirects to original URL.

## How it Works

1. User sends long URL
2. System generates unique short key
3. Mapping stored in database
4. Short link redirects to original URL

## Future Improvements

- expiration time for links
- click tracking analytics
- custom short links
