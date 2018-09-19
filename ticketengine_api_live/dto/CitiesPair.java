package dto;

import java.util.ArrayList;

public class CitiesPair {
	private ArrayList<GetCitiesPairRequest> citiespair;

	public ArrayList<GetCitiesPairRequest> getCitiespair() {
		return citiespair;
	}

	public void setCitiespair(ArrayList<GetCitiesPairRequest> citiespair) {
		this.citiespair = citiespair;
	}
	public ResponseCodes getResponseCodes() {
		return response;
	}
	public void setResponseCodes(ResponseCodes response) {
		this.response = response;
	}
	private ResponseCodes response;

}
