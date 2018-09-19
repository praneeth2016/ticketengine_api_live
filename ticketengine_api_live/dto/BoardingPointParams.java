package dto;

public class BoardingPointParams {
	public String getCity_name() {
		return city_name;
	}
	public void setCity_name(String city_name) {
		this.city_name = city_name;
	}
	public String getVan_pickup() {
		return van_pickup;
	}
	public void setVan_pickup(String van_pickup) {
		this.van_pickup = van_pickup;
	}
	public String getPickup_point() {
		return pickup_point;
	}
	public void setPickup_point(String pickup_point) {
		this.pickup_point = pickup_point;
	}
	public String getLandmark() {
		return landmark;
	}
	public void setLandmark(String landmark) {
		this.landmark = landmark;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getBpid() {
		return bpid;
	}
	public void setBpid(String bpid) {
		this.bpid = bpid;
	}
	
	public int getCity_id() {
		return city_id;
	}
	public void setCity_id(int city_id) {
		this.city_id = city_id;
	}
	private String city_name;
	private int city_id;
	private String type;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	private String van_pickup;
	private String pickup_point;
	private String landmark;
	private String time;
	private String bpid;


}
