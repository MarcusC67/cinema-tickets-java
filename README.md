# Cinema Tickets Service

A Java service for handling cinema ticket purchases with various validation rules and business logic.

## Ticket Prices

- Adult tickets: £25
- Child tickets: £15
- Infant tickets: £0 (free)

## Business Rules

1. Maximum 25 tickets can be purchased at a time
2. At least one adult ticket must be purchased
3. Cannot have more infants than adults
4. Infant tickets are free and do not require a seat
5. Child tickets require a seat
6. Adult tickets require a seat

## Technical Details

- Java 21
- JUnit 4 for testing
- Mockito for mocking dependencies
- Maven for build management

## Running Tests

To run the tests, use:
```bash
mvn test
```

## Dependencies

- JUnit 4.11
- Mockito 4.0.0

## Assumptions

1. `TicketPaymentService` is an external provider with no defects
2. `SeatReservationService` is an external provider with no defects
3. Account IDs must be greater than 0
4. All ticket requests must be valid (non-null and positive quantity) 