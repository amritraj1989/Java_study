package com.mikealbert.data.vo;

public class ReportContactVO {

	private Long cntId;
	private Long drvId;	
	private String firstName;
	private String lastName;
	private String contactType;
	private String corporateContactType;
	private String deliveryMethod;
	private String emailAddres;
	private String faxNumber;
	
	public ReportContactVO(){}
	
	public Long getCntId() {
		return cntId;
	}
	
	public void setCntId(Long cntId) {
		this.cntId = cntId;
	}
	
	public Long getDrvId() {
		return drvId;
	}
	
	public void setDrvId(Long drvId) {
		this.drvId = drvId;
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public String getContactType() {
		return contactType;
	}
	
	public void setContactType(String contactType) {
		this.contactType = contactType;
	}
	
	public String getCorporateContactType() {
		return corporateContactType;
	}
	
	public void setCorporateContactType(String corporateContactType) {
		this.corporateContactType = corporateContactType;
	}
	
	public String getDeliveryMethod() {
		return deliveryMethod;
	}
	
	public void setDeliveryMethod(String deliveryMethod) {
		this.deliveryMethod = deliveryMethod;
	}
	
	public String getEmailAddres() {
		return emailAddres;
	}
	
	public void setEmailAddres(String emailAddres) {
		this.emailAddres = emailAddres;
	}
	
	public String getFaxNumber() {
		return faxNumber;
	}
	
	public void setFaxNumber(String faxNumber) {
		this.faxNumber = faxNumber;
	}		
	
}
