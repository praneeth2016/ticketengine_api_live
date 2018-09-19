package dto;

public class CancelTicketResponse {
	public ResponseCodes getResponseCodes() {
		return response;
	}
	public void setResponseCodes(ResponseCodes response) {
		this.response = response;
	}
	private ResponseCodes response;
	private CancellationDetails cancel_details;
	public CancellationDetails getCancel_details() {
		return cancel_details;
	}
	public void setCancel_details(CancellationDetails cancel_details) {
		this.cancel_details = cancel_details;
	}

}
