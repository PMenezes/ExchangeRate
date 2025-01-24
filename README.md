# Exchange Rate API

This project is a Java Spring Boot application that fetches exchange rates from one or more publicly available APIs (e.g., ExchangeRate.host) and performs currency conversion calculations. It includes mechanisms for caching and rate limiting.

## Features

- **Fetch Exchange Rates**: Retrieves exchange rates from ExchangeRate.host.
- **Currency Conversion**: Converts amounts from one currency to others based on the fetched rates.

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

### Get All Exchange Rates
**GET /api/exchange-rate/all?from={currencyA}**
- **Description**: Fetches all exchange rates for the given base currency.
- **Parameters**:
  - `from`: Base currency code (e.g., `USD`)
- **Response**:
  ```json
  {
    "rates": {
        "USDEUR": 0.85,
        "USDGBP": 0.75,
        "USDJPY": 110.53
    }
  }
  ```
  
### Convert Currency
**GET /api/exchange-rate/convert?from={currencyA}&to={currencyB}&amount={amount}**
- **Description**: Converts one currency to another given an amount.
- **Parameters**:
    - `from`: Source currency code (e.g., `USD`)
    - `to`: Target currency code (e.g., `EUR`)
    - `amount`: Amount to convert (e.g., 100)
- **Response**:
  ```json
  {
    "rate": 85.0
  }
  ```
  
### Convert Multiple Currency
**GET /api/exchange-rate/convert-multiple?from={currencyA}&toCurrencies={currencies}&amount={amount}**
- **Description**: Converts one currency to another given an amount.
- **Parameters**:
    - `from`: Source currency code (e.g., `USD`)
    - `toCurrencies`: Target currencies codes, comma separated (e.g., `EUR,JPY`)
    - `amount`: Amount to convert (e.g., 100)
- **Response**:
  ```json
  {
    "rates": {
        "USDEUR": 85,
        "USDGBP": 75,
        "USDJPY": 10.5
    }
  }
  ```

## GraphQL Queries

The following GraphQL queries are available in the API:

### 1. `exchangeRate`
Fetches the exchange rate from a given `fromCurrency` to a `toCurrency`.

**Query Example:**
```graphql
query {
  exchangeRate(fromCurrency: "USD", toCurrency: "EUR")
}
```

**Response Example:**
```json
{
  "data": {
    "exchangeRate": 0.85
  }
}
```

### 2. `convertCurrency`
Converts a given `amount` of one currency (`fromCurrency`) to another (`toCurrency`).

**Query Example:**
```graphql
query {
  convertCurrency(fromCurrency: "USD", toCurrency: "EUR", amount: 100)
}
```

**Response Example:**
```json
{
  "data": {
    "convertCurrency": 85.0
  }
}
```

### 3. `getAllExchangeRates`
Fetches all exchange rates for a given `fromCurrency` in a list of supported currencies.

**Query Example:**
```graphql
query {
  getAllExchangeRates(fromCurrency: "USD") {
    currency
    rate
  }
}
```

**Response Example:**
```json
{
  "data": {
    "getAllExchangeRates": [
      { "currency": "USDEUR", "rate": 0.85 },
      { "currency": "USDGBP", "rate": 0.75 },
      { "currency": "USDJPY", "rate": 110.0 }
    ]
  }
}
```

### 4. `convertCurrencyToMultiple`
Converts a given `amount` of one currency (`fromCurrency`) to multiple other currencies (`toCurrencies`).

**Query Example:**
```graphql
query {
  convertCurrencyToMultiple(fromCurrency: "USD", toCurrencies: "EUR,GBP,JPY", amount: 100) {
    currency
    rate
  }
}
```

**Response Example:**
```json
{
  "data": {
    "convertCurrencyToMultiple": [
      { "currency": "EUR", "rate": 85.0 },
      { "currency": "GBP", "rate": 75.0 },
      { "currency": "JPY", "rate": 11000.0 }
    ]
  }
}
```

## How to Run

### 1. Install Dependencies
Ensure you have all necessary dependencies installed for GraphQL:
- `graphql-java`
- `graphql-spring-boot-starter`
- `graphql-java-tools`

### 2. Run the Application
To run the Spring Boot application with GraphQL functionality, execute:

```bash
mvn spring-boot:run
```

The API will be accessible at:

- REST endpoints: `/api/exchange-rate/...`
- GraphQL endpoint: `/graphql`

### 3. Access GraphiQL UI (Optional)
You can interact with the GraphQL API using the built-in GraphiQL UI provided by `graphql-spring-boot-starter`. It can be accessed at:

```bash
http://localhost:8080/graphiql
```

Here, you can test the various GraphQL queries mentioned above.

## Caching

All methods exposed through GraphQL (like `getExchangeRate`, `convertCurrency`, `convertCurrencyToMultiple`) are cached using Spring's `@Cacheable` annotation. The cache is keyed by the method parameters and will return the cached data if the same request is made within a short period.

### Cache Keys:
- `getExchangeRate`: Keyed by `fromCurrency + toCurrency`
- `convertCurrency`: Keyed by `fromCurrency + toCurrency + amount`
- `convertCurrencyToMultiple`: Keyed by `fromCurrency + toCurrencies + amount`

## Error Handling

In case of a failure to fetch exchange rates or conversions, the API will throw an appropriate error. For example, if a currency pair is invalid or the external API fails, a `RuntimeException` will be thrown.

**Example:**
```json
{
  "errors": [
    {
      "message": "Failed to fetch conversion for EUR"
    }
  ]
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

