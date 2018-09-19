package dto;

public class BalanceResponse {
	
	public ResponseCodes getResponseCodes() {
		return response;
	}
	public void setResponseCodes(ResponseCodes response) {
		this.response = response;
	}
	private ResponseCodes response;
	public ResponseCodes getResponse() {
		return response;
	}
	public void setResponse(ResponseCodes response) {
		this.response = response;
	}
    private AgentBalance balance_details;

	public AgentBalance getBalance_details() {
		return balance_details;
	}

	public void setBalance_details(AgentBalance balance_details) {
		this.balance_details = balance_details;
	}
	

}
