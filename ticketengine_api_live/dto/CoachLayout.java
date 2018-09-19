package dto;

import java.util.ArrayList;

public class CoachLayout {
	private int total_seats;
	public int getTotal_seats() {
		return total_seats;
	}
	public void setTotal_seats(int total_seats) {
		this.total_seats = total_seats;
	}
	public int getAvailable_seats() {
		return available_seats;
	}
	public void setAvailable_seats(int available_seats) {
		this.available_seats = available_seats;
	}
	private int available_seats; 
	private ArrayList<SeatDetails> seat_details;
	public ArrayList<SeatDetails> getSeat_details() {
		return seat_details;
	}
	public void setSeat_details(ArrayList<SeatDetails> seat_details) {
		this.seat_details = seat_details;
	}

}
