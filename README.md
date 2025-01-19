# Exchange Rates API

This repository contains a Java Spring-based API designed to fetch exchange rates from one or more publicly available APIs and use them for conversion calculations. The API provides endpoints for retrieving exchange rates and performing currency conversions.

## Features

- Fetch exchange rates from multiple public APIs.
- Calculate conversions between currencies using the latest rates.
- Handle fallback mechanisms in case one API fails.
- Designed with extensibility and scalability in mind.

## Technologies Used

- **Java**
- **Spring Boot**
- **REST API**
- **Maven**

## Setup and Installation

### Prerequisites

1. **Java Development Kit (JDK)**: Ensure you have JDK 11 or higher installed.
2. **Maven**: Install Maven for dependency management.

### Clone the Repository

```bash
$ git clone https://github.com/your-username/exchange-rates-api.git
$ cd exchange-rates-api
```

### Configure the Application

1. Create an `application.properties` file in the `src/main/resources` directory.
2. Add the required configuration:

```properties
# Example configuration
api.key=<your-api-key>
api.urls=https://api1.example.com,https://api2.example.com
server.port=8080
```

Replace `<your-api-key>` with your API key(s) and `https://api1.example.com` with the base URLs of the exchange rate APIs you intend to use.

### Build and Run the Application

Build the project using Maven:

```bash
$ mvn clean install
```

Run the application:

```bash
$ mvn spring-boot:run
```

The API will be available at `http://localhost:8080` by default.

## Usage

### Endpoints

#### 1. Fetch Exchange Rates

**GET** `/api/v1/exchange-rates`

- **Description**: Retrieve the latest exchange rates.
- **Response Example**:

```json
{
  "baseCurrency": "USD",
  "rates": {
    "EUR": 0.91,
    "JPY": 136.5
  }
}
```

#### 2. Convert Currency

**POST** `/api/v1/convert`

- **Description**: Convert an amount from one currency to another.
- **Request Example**:

```json
{
  "from": "USD",
  "to": "EUR",
  "amount": 100
}
```

- **Response Example**:

```json
{
  "from": "USD",
  "to": "EUR",
  "amount": 100,
  "convertedAmount": 91
}
```

### Swagger Documentation

Visit `http://localhost:8080/swagger-ui.html` to explore the API documentation and test endpoints interactively.

## Contact

For questions or support, please contact [pedro.brt.menezes@gmail.com](mailto:pedro.brt.menezes@gmail.com).

