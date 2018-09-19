package dto;

public class SeatBookingResponse {
	
	private TicketDetails ticket_details;

	public TicketDetails getTicket_details() {
		return ticket_details;
	}

	public void setTicket_details(TicketDetails ticket_details) {
		this.ticket_details = ticket_details;
	}
	
	public ResponseCodes getResponseCodes() {
		return response;
	}
	public void setResponseCodes(ResponseCodes response) {
		this.response = response;
	}
	private ResponseCodes response;
	
}
