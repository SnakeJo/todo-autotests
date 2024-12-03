# Todo Application Test Framework

This project is a test framework for the Todo Application, designed to ensure the functionality and reliability of the application's features. The framework is built using JUnit 5, Spring Boot, Lombok, RestAssured providing a robust framework for testing the application's API endpoints.

## Features
- Automated tests for creating, receiving, updated and deleted todos
- Utilizes Rest Assured for API testing
- Logging is available for HTTP and WebSocket

## Prerequisites
- docker or podman
- Java 17
- Maven

## Setup Instructions

1. **Build the Project**
   ```bash
   mvn clean install
   ```

2. **Run the Todo Application**
   ```bash
   docker run -e VERBOSE=1 -p 8080:4242  todo-app
   ```

3. **Run the Tests**
   Execute the following command to run the tests:
   ```bash
   mvn test
   ```

## Description of Key Project Directories
- src/main/* - contains the main application logic
- src/test/* - contains tests for the application

- assembling - contains classes for assembling structures used in tests
- config - contains classes for configuring various service components
- domain - contains classes for data representation
- service - contains classes for interacting with the tested service
- utils - contains classes for utility operations


## Note
- In TODO: added indications of found errors and deficiencies for the todo-app
-  As well as additional unimplemented tests
