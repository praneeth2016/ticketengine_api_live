package dto;

public class CancellationDetails {
	private float refund_amount;
	public float getRefund_amount() {
		return refund_amount;
	}
	public void setRefund_amount(float refund_amount) {
		this.refund_amount = refund_amount;
	}
	public float getCancellation_amount() {
		return cancellation_amount;
	}
	public void setCancellation_amount(float cancellation_amount) {
		this.cancellation_amount = cancellation_amount;
	}
	
	public String getCancellation_charges() {
		return cancellation_charges;
	}
	public void setCancellation_charges(String cancellation_charges) {
		this.cancellation_charges = cancellation_charges;
	}
	private float cancellation_amount;
	private String cancellation_charges;
	private String seat_numbers;
	public String getSeat_numbers() {
		return seat_numbers;
	}
	public void setSeat_numbers(String seat_numbers) {
		this.seat_numbers = seat_numbers;
	}
	private String status;
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
