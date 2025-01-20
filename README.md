# Exchange Rate API

This project is a Java Spring Boot application that fetches exchange rates from one or more publicly available APIs (e.g., ExchangeRate.host) and performs currency conversion calculations. It includes mechanisms for caching and rate limiting.

## Features

- **Fetch Exchange Rates**: Retrieves exchange rates from ExchangeRate.host.
- **Currency Conversion**: Converts amounts from one currency to another based on the fetched rates.
- **Caching**: Implements a caching mechanism to reduce external API calls, allowing up to a 1-minute delay for non-real-time data.
- **Rate Limiting**: Protects the API from abuse by limiting the number of requests per user.
- **Unit Testing**: Includes unit tests to ensure the reliability of API operations.

## Prerequisites

- **Java**: JDK 17 or later
- **Maven**: Version 3.8.1 or later
- **Docker** (optional): For running the application in a containerized environment

## Getting Started

### Clone the Repository
```bash
git clone https://github.com/your-repo/exchange-rate-api.git
cd exchange-rate-api
```

### Build the Project
```bash
mvn clean install
```

### Run the Application
```bash
mvn spring-boot:run
```

The application will start at `http://localhost:8080` by default.

### Run with Docker
1. Build the Docker image:
   ```bash
   docker build -t exchange-rate-api .
   ```
2. Run the Docker container:
   ```bash
   docker run -p 8080:8080 exchange-rate-api
   ```

## API Endpoints

### Exchange Rate Retrieval
**GET /api/exchange-rate?from={currencyA}&to={currencyB}**
- **Description**: Fetches the exchange rate from `currencyA` to `currencyB`.
- **Parameters**:
  - `from`: Source currency code (e.g., `USD`)
  - `to`: Target currency code (e.g., `EUR`)
- **Response**:
  ```json
  {
    "rate": 0.85
  }
  ```

### Rate Limiting
- The API limits the number of requests per minute for each user.
- Customizable via `application.properties`.

## Externalized Properties
The application uses `application.properties` for configuration. Key properties include:

```properties
# API Configuration
external.api.url=https://api.exchangerate.host/latest
external.api.cache.ttl=60 # seconds

# Rate Limiting
rate.limit.requests=100
rate.limit.time=60 # seconds

# Server Configuration
server.port=8080
```

## Swagger API Documentation
```
`http://localhost:8080/api-docs` to explore the API documentation and test endpoints interactively.
```

## Testing

Run unit tests using:
```bash
mvn test
```

### Example Unit Test
Unit tests are written for core functionalities like fetching exchange rates and conversions. Example:
```java
@Test
void testGetExchangeRate() {
    double rate = exchangeRateService.getExchangeRate("USD", "EUR");
    assertEquals(0.85, rate);
}
```

## Contact

For questions or support, please contact [pedro.brt.menezes@gmail.com](mailto:pedro.brt.menezes@gmail.com).

