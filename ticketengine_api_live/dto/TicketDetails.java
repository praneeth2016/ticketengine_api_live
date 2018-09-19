package dto;

public class TicketDetails {
	
	private String ticket_status;
	public String getTicket_status() {
		return ticket_status;
	}
	public void setTicket_status(String ticket_status) {
		this.ticket_status = ticket_status;
	}
	public String getTicket_number() {
		return ticket_number;
	}
	public void setTicket_number(String ticket_number) {
		this.ticket_number = ticket_number;
	}
	public int getPnr_number() {
		return pnr_number;
	}
	public void setPnr_number(int pnr_number) {
		this.pnr_number = pnr_number;
	}
	public int getTravel_id() {
		return travel_id;
	}
	public void setTravel_id(int travel_id) {
		this.travel_id = travel_id;
	}
	public String getOrigin() {
		return origin;
	}
	public void setOrigin(String origin) {
		this.origin = origin;
	}
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}
	public String getJourney_date() {
		return journey_date;
	}
	public void setJourney_date(String journey_date) {
		this.journey_date = journey_date;
	}
	public int getNo_of_seats() {
		return no_of_seats;
	}
	public void setNo_of_seats(int no_of_seats) {
		this.no_of_seats = no_of_seats;
	}
	public String getSeat_numbers() {
		return seat_numbers;
	}
	public void setSeat_numbers(String seat_numbers) {
		this.seat_numbers = seat_numbers;
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
	private String boarding_point;
	public String getBoarding_point() {
		return boarding_point;
	}
	public void setBoarding_point(String boarding_point) {
		this.boarding_point = boarding_point;
	}
	public String getLandmark() {
		return landmark;
	}
	public void setLandmark(String landmark) {
		this.landmark = landmark;
	}
	private String landmark;
	public Float getTotal_fare() {
		return total_fare;
	}
	public void setTotal_fare(Float total_fare) {
		this.total_fare = total_fare;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getPassenger_name() {
		return passenger_name;
	}
	public void setPassenger_name(String passenger_name) {
		this.passenger_name = passenger_name;
	}
	public String getPassenger_age() {
		return passenger_age;
	}
	public void setPassenger_age(String passenger_age) {
		this.passenger_age = passenger_age;
	}
	public String getPassenger_mobile() {
		return passenger_mobile;
	}
	public void setPassenger_mobile(String passenger_mobile) {
		this.passenger_mobile = passenger_mobile;
	}
	public String getPassenger_email() {
		return passenger_email;
	}
	public void setPassenger_email(String passenger_email) {
		this.passenger_email = passenger_email;
	}
	private String ticket_number;
	private int pnr_number;
	private int travel_id;
	private String origin;
	private String destination;
	private String journey_date;
	private int no_of_seats;
	private String seat_numbers;
	private String travel_name;
	private String service_number;
	private String bus_type;
	private String dep_time;
	private Float total_fare;
	private String gender;
	private String passenger_name;
	private String passenger_age;
	private String passenger_mobile;
	private String passenger_email;
	
	private String dropping_point;
	public String getDropping_point() {
		return dropping_point;
	}
	public void setDropping_point(String dropping_point) {
		this.dropping_point = dropping_point;
	}	

}
