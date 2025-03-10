package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class TicketServiceImpl implements TicketService {
    private final TicketPaymentService ticketPaymentService;
    private final SeatReservationService seatReservationService;
    private static final int MAX_TICKETS = 25;

    public TicketServiceImpl(TicketPaymentService ticketPaymentService, SeatReservationService seatReservationService) {
        this.ticketPaymentService = ticketPaymentService;
        this.seatReservationService = seatReservationService;
    }

    /**
     * Should only have private methods other than the one below.
     */

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        // Validate account ID
        if (accountId == null || accountId <= 0) {
            throw new InvalidPurchaseException("Account ID must be greater than 0");
        }

        // Validate ticket requests
        if (ticketTypeRequests == null || ticketTypeRequests.length == 0) {
            throw new InvalidPurchaseException("At least one ticket must be requested");
        }

        // Count total tickets
        int totalTickets = 0;
        for (TicketTypeRequest request : ticketTypeRequests) {
            if (request == null) {
                throw new InvalidPurchaseException("Ticket request cannot be null");
            }
            if (request.getNoOfTickets() <= 0) {
                throw new InvalidPurchaseException("Number of tickets must be greater than 0");
            }
            totalTickets += request.getNoOfTickets();
        }

        // Check maximum tickets
        if (totalTickets > MAX_TICKETS) {
            throw new InvalidPurchaseException("Cannot purchase more than " + MAX_TICKETS + " tickets at a time");
        }

        // Count tickets by type
        int adultCount = 0;
        int infantCount = 0;
        for (TicketTypeRequest request : ticketTypeRequests) {
            switch (request.getTicketType()) {
                case ADULT:
                    adultCount += request.getNoOfTickets();
                    break;
                case INFANT:
                    infantCount += request.getNoOfTickets();
                    break;
            }
        }

        // Validate business rules
        if (adultCount == 0) {
            throw new InvalidPurchaseException("At least one adult ticket must be purchased");
        }
        if (infantCount > adultCount) {
            throw new InvalidPurchaseException("Cannot have more infants than adults");
        }

        // Process the purchase
        int totalAmount = calculateTotalAmount(ticketTypeRequests);
        int totalSeats = calculateTotalSeats(ticketTypeRequests);

        ticketPaymentService.makePayment(accountId, totalAmount);
        seatReservationService.reserveSeat(accountId, totalSeats);
    }

    private int calculateTotalAmount(TicketTypeRequest... ticketTypeRequests) {
        int total = 0;
        for (TicketTypeRequest request : ticketTypeRequests) {
            total += request.getNoOfTickets() * getTicketPrice(request.getTicketType());
        }
        return total;
    }

    private int calculateTotalSeats(TicketTypeRequest... ticketTypeRequests) {
        int total = 0;
        for (TicketTypeRequest request : ticketTypeRequests) {
            if (request.getTicketType() != TicketTypeRequest.Type.INFANT) {
                total += request.getNoOfTickets();
            }
        }
        return total;
    }

    private int getTicketPrice(TicketTypeRequest.Type type) {
        switch (type) {
            case ADULT:
                return 25;
            case CHILD:
                return 15;
            case INFANT:
                return 0;
            default:
                return 0;
        }
    }
}
