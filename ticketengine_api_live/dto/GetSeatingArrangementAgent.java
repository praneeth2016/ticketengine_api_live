package dto;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class GetSeatingArrangementAgent {
	@SerializedName("bus_seating_details")
	private ArrayList<LayoutDetailsAgent> service_details;

	public ArrayList<LayoutDetailsAgent> getService_details() {
		return service_details;
	}

	public void setService_details(ArrayList<LayoutDetailsAgent> service_details) {
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
