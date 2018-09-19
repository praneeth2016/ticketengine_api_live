package dto;

public class SeatBlockingResponse {
	public ResponseCodes getResponseCodes() {
		return response;
	}
	public void setResponseCodes(ResponseCodes response) {
		this.response = response;
	}
	private ResponseCodes response;
	private SendPNR pnrDetails;
	public ResponseCodes getResponse() {
		return response;
	}
	public void setResponse(ResponseCodes response) {
		this.response = response;
	}
	public SendPNR getPnrDetails() {
		return pnrDetails;
	}
	public void setPnrDetails(SendPNR pnrDetails) {
		this.pnrDetails = pnrDetails;
	}
	

}
