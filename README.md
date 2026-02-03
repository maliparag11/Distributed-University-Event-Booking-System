# Distributed University Event Booking and Payment System

## Module
COMP41720 – Distributed Systems

## Project Type
Group Project

## Project Description
This project implements a Distributed University Event Booking and Payment System using a microservices-based architecture.  
The system allows students to browse university events, create bookings for events with limited seats, and complete payments.

The main focus of this project is on architectural design, correctness, and fault tolerance rather than only implementation.  
The system ensures that event capacity is never exceeded and that bookings are confirmed only after successful payment.

---

## System Architecture Overview
The system is designed using a microservices architecture where each core business capability is implemented as an independent service.

The system consists of the following services:
- API Gateway
- Event Service
- Booking Service
- Payment Service

Services communicate using synchronous REST APIs over HTTP and exchange data in JSON format.

---

## Services Description

### API Gateway
- Acts as the single entry point for all client requests
- Routes requests to appropriate backend services
- Hides internal service topology from clients

### Event Service
- Manages event creation and retrieval
- Enforces seat availability
- Acts as the single source of truth for event capacity
- Prevents overbooking under concurrent requests

### Booking Service
- Manages booking creation, confirmation, and cancellation
- Coordinates seat reservation with the Event Service
- Uses retries and circuit breakers for fault tolerance

### Payment Service
- Handles payment creation and payment status updates
- Confirms bookings only after successful payment
- Ensures unpaid bookings are never confirmed

---

## Core Workflows

### Booking Flow
1. Client sends a booking request via the API Gateway
2. Booking Service requests seat reservation from the Event Service
3. If seats are available, a booking is created in PENDING_PAYMENT state
4. If seats are unavailable, the booking request is rejected

### Payment and Confirmation Flow
1. Client initiates payment for a booking
2. Payment Service processes the payment
3. On payment success, the Booking Service is notified to confirm the booking
4. On payment failure, the booking remains unconfirmed

---

## Fault Tolerance and Reliability
- Retries are used to handle transient failures
- Circuit breakers prevent cascading failures
- The system fails safely by prioritising correctness over availability
- Overbooking is prevented even during partial failures

---

## Technology Stack
- Java 17
- Spring Boot
- Spring Cloud Gateway
- REST APIs (HTTP and JSON)
- Docker and Docker Compose
- PostgreSQL
- Postman (for testing)

---

## Setup and Execution

### Prerequisites
- Docker
- Docker Compose
- Java 17

### Steps to Run
1. Clone the repository:
  git clone https://github.com/maliparag11/Distributed-University-Event-Booking-System.git

2. Navigate to the project root directory:
   cd Distributed-University-Event-Booking-System

3. Start all services using Docker Compose:
  docker compose up --build

4. Verify services using:
- `/actuator/health` endpoints
- Postman or browser-based testing

---

## Testing
- End-to-end testing performed using Postman
- Concurrent booking scenarios tested
- Service failure and recovery scenarios tested
- Correctness verified by ensuring no overbooking and payment-gated confirmation

---

## Group Members and Contributions

### 25201715 – Rutuja Yogesh Mahajan
- Event Service design and implementation
- Event and seat management logic
- Integration testing and documentation support

### 25201859 – Parag Manoj Mali
- Payment Service design and implementation
- Payment workflow and booking confirmation logic
- Testing payment-related edge cases

### 25202112 – Geet Prashant Bhute
- Booking Service design and implementation
- Service coordination and resilience mechanisms
- Failure handling and recovery tests

All group members contributed to architectural design, Docker setup, testing, and report preparation.

---

## Conclusion
This project demonstrates the design and implementation of a distributed microservices system for university event booking and payments.  
The system prioritises correctness, fault tolerance, and clear architectural trade-offs, aligning with the learning objectives of COMP41720.

---

## Repository Link
https://github.com/maliparag11/Distributed-University-Event-Booking-System
