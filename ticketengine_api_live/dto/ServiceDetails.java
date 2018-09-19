package dto;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class ServiceDetails {
	@SerializedName("service_details")
	private ArrayList<GetServicesRequest> service_details;

	public ArrayList<GetServicesRequest> getService_details() {
		return service_details;
	}

	public void setService_details(ArrayList<GetServicesRequest> service_details) {
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
