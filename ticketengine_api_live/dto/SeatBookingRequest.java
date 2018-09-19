package dto;

public class SeatBookingRequest {
	//{"seat":"V2","name":"test1","age":"22","sex":"M","mobile_number":"345344353","email":"test@gmail.com"}
	private String seat;
	public String getSeat() {
		return seat;
	}
	public void setSeat(String seat) {
		this.seat = seat;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	private String name;
	private String age;
	private String sex;
	private String mobile;
	private String email;
	 
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
	  private String bpid;
	  public String getBpid() {
		return bpid;
	}
	public void setBpid(String bpid) {
		this.bpid = bpid;
	}
	public String getDpid() {
		return dpid;
	}
	public void setDpid(String dpid) {
		this.dpid = dpid;
	}
	private String dpid;
	private int pnr;
	public int getPnr() {
		return pnr;
	}
	public void setPnr(int pnr) {
		this.pnr = pnr;
	}
	
	private String alt_mobile;
	public String getAlt_mobile() {
		return alt_mobile;
	}
	public void setAlt_mobile(String alt_mobile) {
		this.alt_mobile = alt_mobile;
	}
	public int getId_type() {
		return id_type;
	}
	public void setId_type(int id_type) {
		this.id_type = id_type;
	}
	public String getId_number() {
		return id_number;
	}
	public void setId_number(String id_number) {
		this.id_number = id_number;
	}
	private int id_type;
	private String id_number;
	private String address;
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}

}
