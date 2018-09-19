package dto;

import java.util.ArrayList;

public class Cities {

	private ArrayList<GetCitiesRequest> cities;

	public ArrayList<GetCitiesRequest> getCities() {
		return cities;
	}

	public void setCities(ArrayList<GetCitiesRequest> cities) {
		this.cities = cities;
	}
	public ResponseCodes getResponseCodes() {
		return response;
	}
	public void setResponseCodes(ResponseCodes response) {
		this.response = response;
	}
	private ResponseCodes response;

	
}
