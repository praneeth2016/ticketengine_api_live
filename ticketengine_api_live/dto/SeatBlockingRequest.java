package dto;

public class SeatBlockingRequest {
	private String seat;
	public String getSeat() {
		return seat;
	}
	public void setSeat(String seat) {
		this.seat = seat;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	
	private String sex;
 
	public float getFare() {
		return fare;
	}
	public void setFare(float fare) {
		this.fare = fare;
	}

private int no_of_seats;
  public int getNo_of_seats() {
	return no_of_seats;
}
public void setNo_of_seats(int no_of_seats) {
	this.no_of_seats = no_of_seats;
}

private float fare;
public String getPname() {
	return pname;
}
public void setPname(String pname) {
	this.pname = pname;
}
public String getPmobile() {
	return pmobile;
}
public void setPmobile(String pmobile) {
	this.pmobile = pmobile;
}

private String pname;
private String pmobile;


}
