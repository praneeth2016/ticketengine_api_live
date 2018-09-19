package dto;

public class LayoutDetailsAgent {
	public String getService_num() {
		return service_num;
	}
	public void setService_num(String service_num) {
		this.service_num = service_num;
	}
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
	public Origin getOrigin() {
		return origin;
	}
	public void setOrigin(Origin origin) {
		this.origin = origin;
	}
	public Destination getDestination() {
		return destination;
	}
	public void setDestination(Destination destination) {
		this.destination = destination;
	}
	public String getTravel_date() {
		return travel_date;
	}
	public void setTravel_date(String travel_date) {
		this.travel_date = travel_date;
	}
	public String getStart_time() {
		return start_time;
	}
	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public String getBus_type() {
		return bus_type;
	}
	public void setBus_type(String bus_type) {
		this.bus_type = bus_type;
	}
	public String getBus_model() {
		return bus_model;
	}
	public void setBus_model(String bus_model) {
		this.bus_model = bus_model;
	}
	public String getFares() {
		return fares;
	}
	public void setFares(String fares) {
		this.fares = fares;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public CoachLayoutAgent getCoach_layout() {
		return coach_layout;
	}
	public void setCoach_layout(CoachLayoutAgent coach_layout) {
		this.coach_layout = coach_layout;
	}
	
	private String service_num;
	private int travel_id;
	private String travel_name;
	private Origin origin;
	private Destination destination;
	private String travel_date;
	private String start_time;
	private String duration;
	private String bus_type;
	private String bus_model;
	private String fares;
	private String status;
	private CoachLayoutAgent coach_layout;

	
	/**optional
		private ArrayList<CancellationTerms> cancellation_policies;
	 **/
	

}
