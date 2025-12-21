# ADR 001: Synchronous REST vs Asynchronous Messaging

## Context

The Distributed University Event Booking System has three microservices: Event Service, Booking Service, and Payment Service. They need to coordinate seat reservation, booking creation, and payment processing. The design must handle partial failures, slow downstream services, and provide a good user experience.

Two main integration styles were considered: synchronous REST calls and asynchronous messaging using RabbitMQ.

## Options

1. Pure synchronous REST between all services.
2. Pure asynchronous messaging for all interactions.
3. Hybrid: synchronous REST where immediate consistency is required, asynchronous messaging where decoupling and resilience are more important.

## Decision

We selected the hybrid approach.

Booking Service calls Event Service synchronously over REST to reserve a seat. This requires immediate feedback to the user on whether an event still has available seats, so a synchronous call is appropriate.

Once the booking is created, Booking Service publishes a booking.created message to RabbitMQ. Payment Service consumes this message and handles payment asynchronously. This decouples payment processing from the user facing request, so payment failures do not block booking creation.

## Consequences

Positive consequences:

- Users receive immediate feedback when an event is full, because seat reservation uses synchronous REST.
- Payment processing can be retried and delayed without blocking the booking API, because it is handled asynchronously.
- Messaging based integration reduces direct coupling between Booking Service and Payment Service.

Negative consequences:

- The system becomes eventually consistent for payment status, because payment completion happens after the initial booking response.
- There are two integration mechanisms to configure and monitor (REST and RabbitMQ) instead of one.
