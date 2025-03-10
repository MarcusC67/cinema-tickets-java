package uk.gov.dwp.uc.pairtest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;

public class TicketServiceTest {

    private TicketService ticketService;

    @Mock
    private TicketPaymentService ticketPaymentService;

    @Mock
    private SeatReservationService seatReservationService;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        ticketService = new TicketServiceImpl(ticketPaymentService, seatReservationService);
    }

    @Test
    public void shouldPurchaseTicketsSuccessfully() {
        System.out.println("\n=== Testing Single Adult Ticket Purchase ===");
        
        // Test successful ticket purchase
        TicketTypeRequest request = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
        System.out.println("Purchasing 1 adult ticket for account 1");
        
        ticketService.purchaseTickets(1L, request);

        // Verify payment service was called with correct amount
        verify(ticketPaymentService).makePayment(1L, 25); // 1 adult ticket at £25
        System.out.println("✓ Payment verified: £25 for 1 adult ticket");
        
        // Verify seat reservation service was called with correct number of seats
        verify(seatReservationService).reserveSeat(1L, 1); // 1 seat for adult
        System.out.println("✓ Seat reservation verified: 1 seat reserved");
    }

    @Test
    public void shouldCalculateCorrectAmountForMixedTickets() {
        System.out.println("\n=== Testing Mixed Ticket Purchase ===");
        
        // Test purchase with different ticket types
        TicketTypeRequest adultRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);
        TicketTypeRequest childRequest = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1);
        TicketTypeRequest infantRequest = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1);
        
        System.out.println("Purchasing tickets:");
        System.out.println("- 2 adult tickets (£25 each)");
        System.out.println("- 1 child ticket (£15)");
        System.out.println("- 1 infant ticket (£0)");
        
        ticketService.purchaseTickets(1L, adultRequest, childRequest, infantRequest);

        // Verify payment service was called with correct amount
        verify(ticketPaymentService).makePayment(1L, 65); // (2 × £25) + £15 + £0 = £65
        System.out.println("✓ Payment verified: £65 total");
        
        // Verify seat reservation service was called with correct number of seats
        verify(seatReservationService).reserveSeat(1L, 3);
        System.out.println("✓ Seat reservation verified: 3 seats reserved (2 adults + 1 child)");
    }

    @Test(expected = InvalidPurchaseException.class)
    public void shouldThrowExceptionForInvalidPurchase() {
        System.out.println("\n=== Testing Invalid Purchase (0 tickets) ===");
        TicketTypeRequest request = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 0);
        System.out.println("Attempting to purchase 0 adult tickets");
        ticketService.purchaseTickets(1L, request);
    }

    @Test(expected = InvalidPurchaseException.class)
    public void shouldThrowExceptionForInvalidAccountId() {
        System.out.println("\n=== Testing Invalid Account ID ===");
        TicketTypeRequest request = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
        System.out.println("Attempting to purchase with account ID 0");
        ticketService.purchaseTickets(0L, request);
    }

    @Test
    public void shouldAcceptValidAccountId() {
        System.out.println("\n=== Testing Multiple Valid Account IDs ===");
        TicketTypeRequest request = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
        
        System.out.println("Testing account IDs: 1, 100, 999999");
        ticketService.purchaseTickets(1L, request);
        ticketService.purchaseTickets(100L, request);
        ticketService.purchaseTickets(999999L, request);

        verify(ticketPaymentService).makePayment(1L, 25);
        verify(ticketPaymentService).makePayment(100L, 25);
        verify(ticketPaymentService).makePayment(999999L, 25);
        System.out.println("✓ All payments verified: £25 for each account");
    }

    @Test(expected = InvalidPurchaseException.class)
    public void shouldThrowExceptionForNoAdultTickets() {
        System.out.println("\n=== Testing Purchase Without Adult Tickets ===");
        TicketTypeRequest childRequest = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1);
        TicketTypeRequest infantRequest = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1);
        System.out.println("Attempting to purchase only child and infant tickets");
        ticketService.purchaseTickets(1L, childRequest, infantRequest);
    }

    @Test(expected = InvalidPurchaseException.class)
    public void shouldThrowExceptionForTooManyInfants() {
        System.out.println("\n=== Testing Too Many Infants ===");
        TicketTypeRequest adultRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
        TicketTypeRequest infantRequest = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 2);
        System.out.println("Attempting to purchase 1 adult and 2 infant tickets");
        ticketService.purchaseTickets(1L, adultRequest, infantRequest);
    }

    @Test(expected = InvalidPurchaseException.class)
    public void shouldThrowExceptionForNullTicketRequests() {
        System.out.println("\n=== Testing Null Ticket Requests ===");
        System.out.println("Attempting to purchase with null ticket requests");
        ticketService.purchaseTickets(1L, null);
    }

    @Test(expected = InvalidPurchaseException.class)
    public void shouldThrowExceptionForEmptyTicketRequests() {
        System.out.println("\n=== Testing Empty Ticket Requests ===");
        System.out.println("Attempting to purchase with no ticket requests");
        ticketService.purchaseTickets(1L);
    }

    @Test(expected = InvalidPurchaseException.class)
    public void shouldThrowExceptionForTooManyTickets() {
        System.out.println("\n=== Testing Maximum Tickets Limit ===");
        TicketTypeRequest request = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 26);
        System.out.println("Attempting to purchase 26 tickets (max is 25)");
        ticketService.purchaseTickets(1L, request);
    }
} 