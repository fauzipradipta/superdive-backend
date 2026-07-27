package com.example.superdive.backend.enums;

public enum PaymentStatus {
	UNPAID("Unpaid"),
	PARTIAL("Partial"),
	PAID("Paid"),
	OVERDUE("Overdue"),
	REFUNDED("Refunded");

	private final String label;

	PaymentStatus(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	/* Accepts the enum name ("UNPAID") or the label the UI shows ("Unpaid"), so the
	 * frontend can send back whichever value it is holding. */
	public static PaymentStatus from(String value) {
		if (value == null) {
			return null;
		}
		String needle = value.trim();
		for (PaymentStatus status : values()) {
			if (status.name().equalsIgnoreCase(needle) || status.label.equalsIgnoreCase(needle)) {
				return status;
			}
		}
		return null;
	}
}
