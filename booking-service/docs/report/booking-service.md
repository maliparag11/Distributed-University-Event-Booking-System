## Booking Service

The Booking Service is responsible for handling student bookings. It exposes a REST API at `/bookings` for creating bookings and `/bookings/{id}/payment-status` for updating the payment status of an existing booking.

When a booking request arrives, the service first calls the Event Service synchronously to reserve a seat for the selected event. This call is wrapped in a Resilience4j circuit breaker and retry so that short term failures and slow responses from the Event Service do not immediately break the booking flow. If the seat reservation fails or the circuit breaker is open, the service returns a clear error to the client.

If seat reservation succeeds, the Booking Service creates a new Booking entity in its own PostgreSQL database. Each booking is stored with a user identifier, event identifier, and a payment status that initially is set to PENDING. After persisting the booking, the service publishes a `booking.created` message to RabbitMQ so that downstream consumers such as the Payment Service can react to the new booking in an asynchronous and decoupled way.
