# Fit-coach CRM System

This project is a REST-based application designed to manage trainee and trainer profiles, as well as training sessions,
within a fit-coach(gym) CRM system. It features profile registration, login functionality, and various profile
management operations. The system includes JWT authentication, and advanced logging for transactions and REST calls.

## Table of Contents

- [Project Overview](#project-overview)
- [Features](#features)
- [Technologies Used](#technologies-used)
- [Logging](#logging)
- [Authentication](#authentication)
- [Setup Instructions](#setup-instructions)
- [Running the Application](#running-the-application)
- [Health Checks and Metrics](#health-checks-and-metrics)
- [Testing](#testing)
- [CI Workflow with GitHub Actions](#ci-workflow-with-github-actions)
- [Security](#security)

## Project Overview

The Fit-Coach(gym) CRM system provides the following key features:

- Registration of **trainee** and **trainer** profiles.
- Login functionality using JWT-based authentication.
- Profile management, including updates and deletions, with strict role-based validation.
- Management of training sessions, such as adding, listing, and filtering based on date, type, or participant details.

The application is designed with REST architecture and makes extensive use of Spring Framework features, including AOP (
Aspect-Oriented Programming) for transaction logging and interceptors for REST call and authentication logging.

### Key Services

- **TrainerService**: Handles creating, updating, and retrieving trainer profiles.
- **TraineeService**: Manages creation, update, and deletion of trainee profiles.
- **TrainingService**: Adds and retrieves training sessions based on filtering criteria.

Most services require authentication except for profile creation.

## Features

### Profile Management

- **Trainee Registration** (POST): Register new trainees with optional details such as date of birth and address.
- **Trainer Registration** (POST): Register new trainers with specialization.
- **Profile Updates** (PUT): Update trainee or trainer details.
- **Profile Deletion** (DELETE): Hard deletion of trainee profiles with cascading deletion of relevant trainings.

### Authentication and Security

- **Login** (GET): JWT-based login, issuing tokens upon successful authentication.
- **JWT Token Authentication**: All actions (except registration) require a valid JWT token, which is handled by a
  custom authentication interceptor.
- **Profile Activation/Deactivation** (PATCH): Change the active status of trainee or trainer profiles (non-idempotent).

### Training Management

- **Add Training** (POST): Schedule new training sessions.
- **Retrieve Training List** (GET): Get training sessions filtered by trainee/trainer name, date range, or training
  type.
- **Trainee-Trainer Management**: Assign or remove trainers from a trainee’s profile.

### Training Types Management

- **Fixed Training Types**: A fixed list of training types is provided, which cannot be updated from the application.

## Technologies and tools used

- **Gradle** For building the project and managing dependencies
- **Spring Framework**: Core dependency, RESTful APIs, transaction management, validation, actuator and AOP.
- **Hibernate**: ORM for managing database entities.
- **MySQL**: Database used for development and testing.
- **Liquibase** For managing and automating database migrations.
- **JWT**: For secure token-based authentication.
- **JUnit & Mockito**: For unit testing and mocking dependencies.
- **Docker** For containerization and environment consistency.
- **Swagger** For API documentation and testing.

## Logging

Two levels of logging have been implemented:

### 1. Transaction Logging

Transaction-level logging is handled using AOP with a custom `@Aspect` class. Each transaction is assigned a
unique `transactionId`, allowing tracking of all operations related to the transaction, including persistence and
service-layer operations.

The logging captures:

- The start and end of each transaction.
- Any exceptions that occur during the transaction.

### 2. REST Logging

REST logging is managed via a custom interceptor. The interceptor logs details of each incoming request, including:

- The endpoint that was called.
- The request payload.
- The response status and message (either `200 OK` or the error message).

Both general logging and specialized logs for transactions and REST requests are stored in separate log files.

## Authentication

Authentication is handled via JWT tokens. A custom authentication interceptor validates the token for all REST calls
except for the registration of new profiles and the login endpoint.

### How Authentication Works:

1. During login, a valid JWT token is issued and sent back to the client.
2. For subsequent requests, this token must be provided in the `Authorization` header (using the `Bearer` schema).
3. The custom interceptor verifies the validity of the token for each request before allowing further processing.

## Setup Instructions

### Clone the Repository

```bash
git clone https://github.com/Tabernol/fit-coach
cd fit-coach
```

## Running the Application

### 1. Running the Application Locally

To run the application locally, follow these steps:

1. Ensure you have MySQL installed and running on your local machine.

2. Configure your MySQL connection in the application-local.yaml file with the correct credentials.

3. Run the application with the local profile by using the following command:

```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

The application will now start and connect to the local MySQL database.

### 2. Running the Application with Docker (Development Profile)

To run the application using Docker with the dev profile:

1. Build the application JAR using:

```bash
./gradlew clean build
```

2. Run the application using docker-compose.yml:

```bash
docker-compose up --build
```

This command will start the application and any required services (e.g., MySQL) using Docker, with the dev profile
active.

## Health Checks and Metrics

The application includes custom health checks and metrics to monitor both system availability and performance.

You can now view using http://localhost:8080/actuator

#### If you're using Docker, you can use them for monitoring

- Access Prometheus at http://localhost:9090 to verify it's running and collecting data from the application's
  /actuator/prometheus endpoint.

- Access Grafana at http://localhost:3000 with the default credentials:

- Username: admin
- Password: admin

Once logged into Grafana, add Prometheus as a data source:

- Navigate to Configuration > Data Sources.
- Select Add data source, choose Prometheus, and set the URL to http://localhost:9090.
- Click Save & Test to confirm the connection.

### Health Checks

These health checks and metrics can be monitored via Prometheus and visualized in Grafana by adding the
/actuator/prometheus endpoint as a Prometheus data source.

Also, available at http://localhost:8080/actuator/health

1. Transaction Count Health Check (transactionCount):
   This health check tracks the number of transactions processed by the system today, allowing you to monitor daily
   transaction volume.

2. Remote Service Availability Health Check (remoteService):
   This health check monitors the availability of a specific external service, ensuring that the system can interact
   with required remote services.

### Metrics

Also, available at http://localhost:8080/actuator/prometheus

1. Trainee Creation Requests (api_trainee_create):
   This metric tracks how many times the application has received a request to create a trainee profile, providing
   insight into usage patterns.

2. Trainer Creation Requests (api_trainer_create):
   This metric tracks the number of requests to create a trainer profile, allowing you to monitor how frequently trainer
   profiles are being created.

3. Training Lookup Duration (service_trainings_find):
   This metric measures how much time the application takes to find all training sessions associated with a specific
   username, helping to assess the performance of the training search functionality.
## Testing
   Unit Testing with JUnit
   This project uses JUnit for unit testing. Unit tests focus on verifying individual components in isolation, using Mockito for mocking dependencies. These tests ensure that the logic inside services, controllers, and utilities works as expected without external interference.

### Running Unit Tests
To run the unit tests, use the following command:

```bash
./gradlew test
```
Unit tests are located in src/test/java and are executed automatically during the build process.

### Integration Testing with Testcontainers and MySQL
Integration tests ensure that multiple components (e.g., services, repositories, and controllers) work together as expected. These tests run against a real MySQL database container managed by Testcontainers, which spins up a MySQL instance inside Docker.

Prerequisites 
- Docker must be installed and running.
- Testcontainers will automatically manage the lifecycle of the MySQL container during the tests.
### Running Integration Tests
To run the integration tests, use the following command:

```bash
./gradlew integrationTest
```
Integration tests are located in src/integration_test/java. Testcontainers handles starting and stopping the MySQL container before and after the tests.

## CI Workflow with GitHub Actions
This project uses GitHub Actions for Continuous Integration (CI). The workflow automatically runs on every push and pull request to the main branch, ensuring that the codebase is tested and validated after every change.

### Workflow Overview
- Platform: The CI process runs on ubuntu-latest as the operating system.
- Database: A MySQL 8 Docker service is started for running the tests, with Liquibase used to apply migrations before running tests.
- Java Setup: The workflow uses JDK 17 for building the project and running the tests.
- Build Tool: Gradle is used for compiling the project, running Liquibase migrations, and executing tests.

### Security
The application uses JWT-based authentication to secure all endpoints except for registration and login. Key security features include:

- Login: Users receive a JWT token upon successful authentication.
- Logout: When a user logs out, their token is 'blacklisted' and no longer valid
- Brute Force protector: Block user for 5 minutes on 3 unsuccessful logins.
- Token Validation: A custom filter verifies the JWT token for each request.
- Custom Filter: A custom filter checks the authenticated user and username in the path and denies access if they do not match
- Role-based Access Control: Profile and training management operations are restricted based on user roles (trainee, trainer).
- Password Management: Passwords are securely stored using hashing, with endpoints for changing passwords.
- CORS Configuration: Cross-Origin Resource Sharing (CORS) is configured to allow requests from specific origins (e.g., http://localhost:3000) with allowed methods such as GET, POST, PUT, and DELETE, and credentials like cookies are permitted.
