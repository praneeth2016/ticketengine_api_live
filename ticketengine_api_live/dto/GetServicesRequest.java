package dto;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class GetServicesRequest {
	public int getTravel_id() {
		return travel_id;
	}
	public void setTravel_id(int travel_id) {
		this.travel_id = travel_id;
	}
	public String getTravel_name() {
		return travel_name;
	}
	public void setTravel_name(String travel_name) {
		this.travel_name = travel_name;
	}
	public String getService_number() {
		return service_number;
	}
	public void setService_number(String service_number) {
		this.service_number = service_number;
	}
	public String getFrom_name() {
		return from_name;
	}
	public void setFrom_name(String from_name) {
		this.from_name = from_name;
	}
	public String getTo_name() {
		return to_name;
	}
	public void setTo_name(String to_name) {
		this.to_name = to_name;
	}
	public int getFrom_id() {
		return from_id;
	}
	public void setFrom_id(int from_id) {
		this.from_id = from_id;
	}
	public int getTo_id() {
		return to_id;
	}
	public void setTo_id(int to_id) {
		this.to_id = to_id;
	}
	public String getBus_model() {
		return bus_model;
	}
	public void setBus_model(String bus_model) {
		this.bus_model = bus_model;
	}
	public int getAvailable_seats() {
		return available_seats;
	}
	public void setAvailable_seats(int available_seats) {
		this.available_seats = available_seats;
	}
	public String getBus_type() {
		return bus_type;
	}
	public void setBus_type(String bus_type) {
		this.bus_type = bus_type;
	}
	public String getDep_time() {
		return dep_time;
	}
	public void setDep_time(String dep_time) {
		this.dep_time = dep_time;
	}
	public String getJourney_time() {
		return getJourney_time();
	}
	public void setJourney_time(String journey_time) {
		this.journey_time = journey_time;
	}
	

	public String getArrival_time() {
		return arrival_time;
	}
	public void setArrival_time(String arrival_time) {
		this.arrival_time = arrival_time;
	}
	public ArrayList<BoardingPoints> getBoarding_points() {
		return boarding_points;
	}
	public void setBoarding_points(ArrayList<BoardingPoints> list) {
		this.boarding_points = list;
	}
	public String getDropping_points() {
		return dropping_points;
	}
	public void setDropping_points(String dropping_points) {
		this.dropping_points = dropping_points;
	}
	public ArrayList<CancellationTerms> getCanc_terms() {
		return canc_terms;
	}
	public void setCanc_terms(ArrayList<CancellationTerms> canc_terms) {
		this.canc_terms = canc_terms;
	}
	
	private String travel_name;
	private int travel_id;
	private String service_number;
	private String status;
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	private int from_id;
	private String from_name;
	private int to_id;
	private String to_name;
	private String journey_date;
	private String bus_type;
	private String bus_model;
	private String dep_time;
	private String journey_time;
	private String arrival_time;
	private int total_seats;
	private int available_seats;
	private String seat_fare;
	private String lb_fare;
	private String ub_fare;
	private String convenience_charge;
	private String currency;
	/*
	 * this fileds are not required
	 * private int lowerdeck_total_seats;
	private int upperdeck_total_seats;
	private int lowerdeck_available_seats;
	private int upperdeck_available_seats;*/
	
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getJourney_date() {
		return journey_date;
	}
	public void setJourney_date(String journey_date) {
		this.journey_date = journey_date;
	}
   public int getTotal_seats() {
		return total_seats;
	}
	public void setTotal_seats(int total_seats) {
		this.total_seats = total_seats;
	}
 /*	
  * 
  * public int getLowerdeck_total_seats() {
		return lowerdeck_total_seats;
	}
	public void setLowerdeck_total_seats(int lowerdeck_total_seats) {
		this.lowerdeck_total_seats = lowerdeck_total_seats;
	}
	public int getUpperdeck_total_seats() {
		return upperdeck_total_seats;
	}
	public void setUpperdeck_total_seats(int upperdeck_total_seats) {
		this.upperdeck_total_seats = upperdeck_total_seats;
	}
  

	public int getLowerdeck_available_seats() {
		return lowerdeck_available_seats;
	}
	public void setLowerdeck_available_seats(int lowerdeck_available_seats) {
		this.lowerdeck_available_seats = lowerdeck_available_seats;
	}
	public int getUpperdeck_available_seats() {
		return upperdeck_available_seats;
	}
	public void setUpperdeck_available_seats(int upperdeck_available_seats) {
		this.upperdeck_available_seats = upperdeck_available_seats;
	}
*/
	
	public String getSeat_fare() {
		return seat_fare;
	}
	public void setSeat_fare(String seat_fare) {
		this.seat_fare = seat_fare;
	}
	public String getLb_fare() {
		return lb_fare;
	}
	public void setLb_fare(String lb_fare) {
		this.lb_fare = lb_fare;
	}
	public String getUb_fare() {
		return ub_fare;
	}
	public void setUb_fare(String ub_fare) {
		this.ub_fare = ub_fare;
	}
	
	public String getconvenience_charge() {
		return convenience_charge;
	}
	public void setconvenience_charge(String convenience_charge) {
		this.convenience_charge = convenience_charge;
	}
	
	@SerializedName("boarding_points")
	private ArrayList<BoardingPoints> boarding_points;
	private String dropping_points;
	@SerializedName("cancellation_policies")
	private ArrayList<CancellationTerms> canc_terms;
	

	


}
