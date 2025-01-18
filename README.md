# Multi-Module Spring Boot Microservices Project

## Overview

This project consists of multiple Spring Boot microservices working together to build a fitness coaching application. 
Each microservice serves a specific purpose, and they interact with each other via REST APIs and are managed by Eureka Server for service discovery. 
JWT-based security is implemented for authentication and authorization. The project is built with Gradle and Java 17.

## Modules

#### 1. Eureka Server

The Eureka Server module is responsible for service discovery. It registers all the microservices and keeps track of their availability.
All microservices in the project communicate through this central registry, making it easier to scale and manage services dynamically.

- Technology: Spring Cloud Netflix Eureka

- Port: 8761

#### 2. Gateway

The Gateway module serves as the entry point for all client requests. It routes incoming requests to the appropriate microservice based on the path. 
The gateway also handles JWT-based authorization and authentication, validating tokens before forwarding requests to downstream services.

- Technology: Spring Cloud Gateway, JWT

- Port: 8765

#### 3. Fit-Coach Microservice

This is the main business logic microservice responsible for handling fitness coaching tasks such as managing training sessions, trainer schedules, and client interactions.
All training-related requests are processed here.

- Technology: Spring Boot, JPA, Hibernate, MySQL, ActiveMQ,

Endpoints:

- /api/v1/fit-coach/trainees/**
- /api/v1/fit-coach/trainers/**
- /api/v1/fit-coach/trainings/**
- /api/v1/fit-coach/training-types/**

#### 4. Security Microservice

The Security microservice is responsible for user authentication and authorization. 
It handles user registration, login, and logout, and provides JWT tokens for secured interactions across the system. 
The security service was separated from the Fit-Coach service to centralize the handling of user credentials and tokens.

- Technology: Spring Boot, Spring Security, Spring Validation, Spring JPA, JWT, MySQL

Endpoints:

- /api/v1/fit-coach/authn/sign-up/trainee
- /api/v1/fit-coach/authn/sign-up/trainer
- /api/v1/fit-coach/authn/login
- /api/v1/fit-coach/authn/logout
- /api/v1/fit-coach/authn/pass/change

#### 5. Report Microservice

This microservice handles reporting-related functionality. 
It stores all the training session data and generates reports based on training sessions added or deleted from the Fit-Coach microservice. 
Reports can be generated for trainers working hours and performance data.
Communication between fit-coach and reporting microservices configured using ActiveMQ message Broker.

- Technology: Spring Boot, MongoDB, ActiveMQ

Endpoints:

- /api/v1/fit-coach/reports/generate/{username}

## Interactions Between Microservices

The microservices interact with each other via REST API calls and message broker (ActiveMQ). 
For example, the Fit-Coach microservice communicates with the Report microservice by sending messages to ActiveMQ to add or remove training sessions.
Security module communicate with Fit-Coach microservice using API calls with SERVICE role in JWT token.
JWT tokens are used to secure communications between the services.

## Prerequisites

- Java 17
- Gradle
- MySQL
- ActiveMQ (via Docker)
- MongoDB

## Running the Project

To run the project, follow these steps:

### Clone the repository
```bash
git clone https://github.com/Tabernol/gym-microservice
cd gym-microservice
```

### Start ActiveMQ
If you don't already have ActiveMQ running, use Docker to start it:
```bash
docker run -p 61616:61616 -p 8161:8161 rmohr/activemq:5.14.3
```

### Start MongoDB
You can install and start MongoDB locally using instructions:
https://www.mongodb.com/docs/manual/installation/

### Start Eureka Server
Navigate to the eureka-server directory and run the application:

```bash
./gradlew bootRun
```

### Start Gateway, Fit-Coach, Security, and Report Services
Navigate to each service directory and run the applications:

```bash
./gradlew bootRun
```

### Access the Eureka Dashboard
Open a browser and navigate to http://localhost:8761 to see the status of registered microservices.

### Access the ActiveMQ Web Console
Once ActiveMQ is running, you can monitor queues and topics by navigating to the ActiveMQ web console at http://localhost:8161.
The default login credentials are:

- Username: admin
- Password: admin

## Security

The Security microservice provides JWT tokens to authenticate users. 
The token is added to the request headers and is validated by the Gateway before routing the request to the intended microservice. 
Ensure that your token has not expired, as the Gateway will block unauthorized requests.

## Future Improvements

Add integration tests.

Containerize the project using Docker for easier deployment.

Improve security with OAuth 2.0 for third-party integrations.
