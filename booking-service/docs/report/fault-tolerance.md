## Fault tolerance: retries and circuit breakers

Fault tolerance in the Booking Service is implemented with Resilience4j. Two outbound integrations are protected: the call to the Event Service to reserve seats and the call to the Payment Service to notify it about a new booking. Both integrations use circuit breaker and retry patterns.

The circuit breaker is configured with a count based sliding window and a failure rate threshold. When too many recent calls fail, the breaker opens and blocks further calls for a fixed period. This prevents the Booking Service from repeatedly calling an unhealthy downstream service and reduces pressure on the failing component. While the breaker is open, fallback methods run and the service returns controlled error responses instead of low level exceptions.

Retries are used for short lived failures such as transient network errors. For each protected call, the service can attempt the operation multiple times with a wait duration and optional exponential backoff. This increases the chance of success when the downstream service is temporarily slow or unstable, without requiring the client to retry manually. Combining circuit breakers, retries, and asynchronous messaging allows the Booking Service to remain responsive and predictable in the presence of partial failures.
