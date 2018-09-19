package dto;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class GetSeatingArrangement {
	
	@SerializedName("bus_seating_details")
	private ArrayList<LayoutDetails> service_details;

	public ArrayList<LayoutDetails> getService_details() {
		return service_details;
	}

	public void setService_details(ArrayList<LayoutDetails> service_details) {
		this.service_details = service_details;
	}
	public ResponseCodes getResponseCodes() {
		return response;
	}
	public void setResponseCodes(ResponseCodes response) {
		this.response = response;
	}
	private ResponseCodes response;
}
