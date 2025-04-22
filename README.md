# Cinema Tickets Service

A Java service for handling cinema ticket purchases with various validation rules and business logic.

## Project Instructions

### Overview

There are 3 types of tickets:
- Infant
- Child
- Adult

The ticket prices are based on the type of ticket (see table below).

| Ticket Type | Price |
|-------------|-------|
| INFANT      | £0    |
| CHILD       | £15   |
| ADULT       | £25   |

The ticket purchaser declares how many and what type of tickets they want to buy.

Multiple tickets can be purchased at any given time.

### Rules

- Only a maximum of 25 tickets can be purchased at a time.
- Infants do not pay for a ticket and are not allocated a seat. They will be sitting on an Adult's lap.
- Child and Infant tickets cannot be purchased without purchasing an Adult ticket.

### Services

- There is an existing `TicketPaymentService` responsible for taking payments.
- There is an existing `SeatReservationService` responsible for reserving seats.

### Constraints

- The TicketService interface CANNOT be modified.
- The code in the thirdparty.* packages CANNOT be modified.
- The `TicketTypeRequest` SHOULD be an immutable object.

### Assumptions

You can assume:

- All accounts with an id greater than zero are valid. They also have sufficient funds to pay for any number of tickets.
- The `TicketPaymentService` implementation is an external provider with no defects. You do not need to worry about how the actual payment happens.
- The payment will always go through once a payment request has been made to the `TicketPaymentService`.
- The `SeatReservationService` implementation is an external provider with no defects. You do not need to worry about how the seat reservation algorithm works.
- The seat will always be reserved once a reservation request has been made to the `SeatReservationService`.

### Task

Provide a working implementation of a `TicketService` that:

1. Considers the above objective, business rules, constraints & assumptions.
2. Calculates the correct amount for the requested tickets and makes a payment request to the `TicketPaymentService`.
3. Calculates the correct number of seats to reserve and makes a seat reservation request to the `SeatReservationService`.
4. Rejects any invalid ticket purchase requests. It is up to you to identify what should be deemed as an invalid purchase request.

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