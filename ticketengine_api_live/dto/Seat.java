package dto;

public class Seat {
	private String number;
	private String type;
	private String fare;
	private boolean available;
	private boolean is_ladies_seat;
	private int row_id;
	private int col_id;
	private float discount_amount;
	private float service_tax_amount;
	private float base_fare;
	private float convenience_charge;
	/*private float cgst;
	private float sgst;*/	
	
	public float getBase_fare() {
		return base_fare;
	}
	public void setBase_fare(float base_fare) {
		this.base_fare = base_fare;
	}
	public float getService_tax_amount() {
		return service_tax_amount;
	}
	public void setService_tax_amount(float service_tax_amount) {
		this.service_tax_amount = service_tax_amount;
	}
	public float getDiscount_amount() {
		return discount_amount;
	}
	public void setDiscount_amount(float discount_amount) {
		this.discount_amount = discount_amount;
	}
	public float getconvenience_charge() {
		return convenience_charge;
	}
	public void setconvenience_charge(float convenience_charge) {
		this.convenience_charge = convenience_charge;
	}
	
	/*public float getcgst() {
		return cgst;
	}
	public void setcgst(float cgst) {
		this.cgst = cgst;
	}
	
	
	public float getsgst() {
		return sgst;
	}
	public void setsgst(float sgst) {
		this.sgst = sgst;
	}*/
	
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getFare() {
		return fare;
	}
	public void setFare(String fare) {
		this.fare = fare;
	}
	public boolean isAvailable() {
		return available;
	}
	public void setAvailable(boolean available) {
		this.available = available;
	}
	public boolean isIs_ladies_seat() {
		return is_ladies_seat;
	}
	public void setIs_ladies_seat(boolean is_ladies_seat) {
		this.is_ladies_seat = is_ladies_seat;
	}
	public int getRow_id() {
		return row_id;
	}
	public void setRow_id(int row_id) {
		this.row_id = row_id;
	}
	public int getCol_id() {
		return col_id;
	}
	public void setCol_id(int col_id) {
		this.col_id = col_id;
	}
	
}
