package com.example.superdive.backend.dto.Request;

/* Body of PATCH /api/orders/{id}/payment-status — the only field the order
 * preview lets the user edit. */
public class PaymentStatusRequestDTO {

	private String paymentStatus;

	public PaymentStatusRequestDTO() {
	}

	public PaymentStatusRequestDTO(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public String getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}
}
