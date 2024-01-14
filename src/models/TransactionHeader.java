package models;

public class TransactionHeader {
	private String TransactionId;
	private String userId;
	private String transactionDate;
	private Integer deliveryInsurance;
	private String courierType;

	private transient String userEmail;

	public TransactionHeader(String transactionId, String userId, String transactionDate, Integer deliveryInsurance,
			String courierType) {
		super();
		TransactionId = transactionId;
		this.userId = userId;
		this.transactionDate = transactionDate;
		this.deliveryInsurance = deliveryInsurance;
		this.courierType = courierType;
	}

	public String getTransactionId() {
		return TransactionId;
	}

	public void setTransactionId(String transactionId) {
		TransactionId = transactionId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
	}

	public Integer getDeliveryInsurance() {
		return deliveryInsurance;
	}

	public void setDeliveryInsurance(Integer deliveryInsurance) {
		this.deliveryInsurance = deliveryInsurance;
	}

	public String getCourierType() {
		return courierType;
	}

	public void setCourierType(String courierType) {
		this.courierType = courierType;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

}
