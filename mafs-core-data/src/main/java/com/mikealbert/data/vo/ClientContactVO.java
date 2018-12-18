package com.mikealbert.data.vo;

import java.io.Serializable;

import com.mikealbert.data.util.DisplayFormatHelper;
import com.mikealbert.util.MALUtilities;

public class ClientContactVO implements Serializable {
	private static final long serialVersionUID = -7749714468066786322L;
	private Long contactId;
	private Long clientContactId;
	private Long driverId;
	private boolean driver;
	private boolean defaultIndicator;
	private String contactType;
	private String costCenterCode;
	private boolean parentAccountContact;
	private Long parentAccountCid;
	private String parentAccountType;
	private String parentAccountCode;
	private String parentAccountName;
	private String firstName;
	private String lastName;
	private String jobTitle;
	private String email;
	private String workAreaCode;
	private String workNumber;
	private String workNumberExtension;
	private String homeAreaCode;
	private String homeNumber;
	private String homeNumberExtension;
	private	String	preferredNumber;
	private String cellAreaCode;
	private String cellNumber;
	private String streetNo;
	private String addressLine1;
	private String addressLine2;
	private String addressLine3;
	private String addressLine4;		
	private String businessAddressLine;
	private String postCode;
	private String county;
	private String country;
	private String city;
	private String state;
	private String methodToUse;
	private boolean contactMethodMail;
	private boolean contactMethodPhone;
	private boolean contactMethodEmail;
	private boolean assigned;
	private boolean assignable;
	private boolean addressAvaialble;
	private boolean phoneAvailable;
	private boolean emailAvailable;	
	private String message;
	private boolean poBoxAvailable;
	private boolean markAssigned;
	private boolean contactMethodMailMarked;
	private boolean contactMethodPhoneMarked;
	private boolean contactMethodEmailMarked;
	private String grdAddressLine1;
	private String grdAddressLine2;

	public ClientContactVO(){}
	
	public String formattedName(){
		StringBuilder formattedName;
		
		formattedName = new StringBuilder();
		formattedName.append(MALUtilities.isEmpty(getLastName()) ? "" : getLastName());
		formattedName.append(MALUtilities.isEmpty(getFirstName()) ? "" : ", " + getFirstName());
		
		return formattedName.toString();
	}
	public Long getContactId() {
		return contactId;
	}

	public void setContactId(Long contactId) {
		this.contactId = contactId;
	}

	public Long getClientContactId() {
		return clientContactId;
	}

	public void setClientContactId(Long clientContactId) {
		this.clientContactId = clientContactId;
	}

	public Long getDriverId() {
		return driverId;
	}

	public void setDriverId(Long driverId) {
		this.driverId = driverId;
	}

	public boolean isDriver() {
		return driver;
	}

	public void setDriver(boolean driver) {
		this.driver = driver;
	}

	public boolean isDefaultIndicator() {
		return defaultIndicator;
	}

	public void setDefaultIndicator(boolean defaultIndicator) {
		this.defaultIndicator = defaultIndicator;
	}

	public String getContactType() {
		return contactType;
	}

	public void setContactType(String contactType) {
		this.contactType = contactType;
	}

	public String getCostCenterCode() {
		return costCenterCode;
	}

	public void setCostCenterCode(String costCenterCode) {
		this.costCenterCode = costCenterCode;
	}

	public boolean isParentAccountContact() {
		return parentAccountContact;
	}

	public void setParentAccountContact(boolean parentAccountContact) {
		this.parentAccountContact = parentAccountContact;
	}

	public Long getParentAccountCid() {
		return parentAccountCid;
	}

	public void setParentAccountCid(Long parentAccountCid) {
		this.parentAccountCid = parentAccountCid;
	}

	public String getParentAccountType() {
		return parentAccountType;
	}

	public void setParentAccountType(String parentAccountType) {
		this.parentAccountType = parentAccountType;
	}

	public String getParentAccountCode() {
		return parentAccountCode;
	}

	public void setParentAccountCode(String parentAccountCode) {
		this.parentAccountCode = parentAccountCode;
	}

	public String getParentAccountName() {
		return parentAccountName;
	}

	public void setParentAccountName(String parentAccountName) {
		this.parentAccountName = parentAccountName;
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

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getWorkAreaCode() {
		return workAreaCode;
	}

	public void setWorkAreaCode(String workAreaCode) {
		this.workAreaCode = workAreaCode;
	}

	public String getWorkNumber() {
		return workNumber;
	}

	public void setWorkNumber(String workNumber) {
		this.workNumber = workNumber;
	}

	public String getWorkNumberExtension() {
		return workNumberExtension;
	}

	public void setWorkNumberExtension(String workNumberExtension) {
		this.workNumberExtension = workNumberExtension;
	}

	public String getCellAreaCode() {
		return cellAreaCode;
	}

	public void setCellAreaCode(String cellAreaCode) {
		this.cellAreaCode = cellAreaCode;
	}

	public String getCellNumber() {
		return cellNumber;
	}

	public void setCellNumber(String cellNumber) {
		this.cellNumber = cellNumber;
	}

	public String getStreetNo() {
		return streetNo;
	}

	public void setStreetNo(String streetNo) {
		this.streetNo = streetNo;
	}

	public String getAddressLine1() {
		return addressLine1;
	}

	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	public String getAddressLine2() {
		return addressLine2;
	}

	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}

	public String getAddressLine3() {
		return addressLine3;
	}

	public void setAddressLine3(String addressLine3) {
		this.addressLine3 = addressLine3;
	}

	public String getAddressLine4() {
		return addressLine4;
	}

	public void setAddressLine4(String addressLine4) {
		this.addressLine4 = addressLine4;
	}

	public String getBusinessAddressLine() {
		return businessAddressLine;
	}

	public void setBusinessAddressLine(String businessAddressLine) {
		this.businessAddressLine = businessAddressLine;
	}

	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public String getCounty() {
		return county;
	}

	public void setCounty(String county) {
		this.county = county;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public String getMethodToUse() {
		return methodToUse;
	}

	public void setMethodToUse(String methodToUse) {
		this.methodToUse = methodToUse;
	}

	public void setState(String state) {
		this.state = state;
	}

	public boolean isContactMethodMail() {
		return contactMethodMail;
	}

	public void setContactMethodMail(boolean contactMethodMail) {
		this.contactMethodMail = contactMethodMail;
	}

	public boolean isContactMethodPhone() {
		return contactMethodPhone;
	}

	public void setContactMethodPhone(boolean contactMethodPhone) {
		this.contactMethodPhone = contactMethodPhone;
	}

	public boolean isContactMethodEmail() {
		return contactMethodEmail;
	}

	public void setContactMethodEmail(boolean contactMethodEmail) {
		this.contactMethodEmail = contactMethodEmail;
	}

	public boolean isAssigned() {
		return MALUtilities.isEmpty(getClientContactId()) ? false : true;
	}

	public void setAssigned(boolean assigned) {
		this.assigned = assigned;
	}

	public boolean isAssignable() {
		return assignable;
	}

	public void setAssignable(boolean assignable) {
		this.assignable = assignable;
	}

	public boolean isAddressAvaialble() {
		return addressAvaialble;
	}

	public void setAddressAvaialble(boolean addressAvaialble) {
		this.addressAvaialble = addressAvaialble;
	}

	public boolean isPhoneAvailable() {
		return phoneAvailable;
	}

	public void setPhoneAvailable(boolean phoneAvailable) {
		this.phoneAvailable = phoneAvailable;
	}

	public boolean isEmailAvailable() {
		return emailAvailable;
	}

	public void setEmailAvailable(boolean emailAvailable) {
		this.emailAvailable = emailAvailable;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public boolean isPoBoxAvailable() {
		return poBoxAvailable;
	}

	public void setPoBoxAvailable(boolean poBoxAvailable) {
		this.poBoxAvailable = poBoxAvailable;
	}

	public String getAddressDisplay() {
		return DisplayFormatHelper.formatAddressForTable(getBusinessAddressLine(), getAddressLine1(), 
				getAddressLine2(), getAddressLine3(), getAddressLine4(), getCity(), getState(), getPostCode(), "<br/>");
	}
	
	public String getPhoneWorkDisplay() {
		StringBuilder display = new StringBuilder();
		
		if(!MALUtilities.isEmpty(getWorkNumber())){
			display.append(DisplayFormatHelper.formatPhoneNumberForTable(getWorkAreaCode(), getWorkNumber(), getWorkNumberExtension(), "<br/>"));
		}
		return display.toString();
	}
	
	public String getPhoneCellDisplay() {
		StringBuilder display = new StringBuilder();

		if(!MALUtilities.isEmpty(getCellNumber())){
			display.append(DisplayFormatHelper.formatPhoneNumberForTable(getCellAreaCode(), getCellNumber(), null, "<br/>"));
		}		
		
		return display.toString();
	}

	public boolean isMarkAssigned() {
		return markAssigned;
	}
	
	public void setMarkAssigned(boolean markAssigned) {
		this.markAssigned = markAssigned;
	}
	
	public boolean isContactMethodMailMarked() {
		return contactMethodMailMarked;
	}

	public void setContactMethodMailMarked(boolean contactMethodMailMarked) {
		this.contactMethodMailMarked = contactMethodMailMarked;
	}

	public boolean isContactMethodPhoneMarked() {
		return contactMethodPhoneMarked;
	}

	public void setContactMethodPhoneMarked(boolean contactMethodPhoneMarked) {
		this.contactMethodPhoneMarked = contactMethodPhoneMarked;
	}

	public boolean isContactMethodEmailMarked() {
		return contactMethodEmailMarked;
	}

	public void setContactMethodEmailMarked(boolean contactMethodEmailMarked) {
		this.contactMethodEmailMarked = contactMethodEmailMarked;
	}

	public String getGrdAddressLine1() {
		return grdAddressLine1;
	}

	public void setGrdAddressLine1(String grdAddressLine1) {
		this.grdAddressLine1 = grdAddressLine1;
	}

	public String getGrdAddressLine2() {
		return grdAddressLine2;
	}

	public void setGrdAddressLine2(String grdAddressLine2) {
		this.grdAddressLine2 = grdAddressLine2;
	}

	public String getHomeAreaCode() {
		return homeAreaCode;
	}

	public void setHomeAreaCode(String homeAreaCode) {
		this.homeAreaCode = homeAreaCode;
	}

	public String getHomeNumber() {
		return homeNumber;
	}

	public void setHomeNumber(String homeNumber) {
		this.homeNumber = homeNumber;
	}

	public String getHomeNumberExtension() {
		return homeNumberExtension;
	}

	public void setHomeNumberExtension(String homeNumberExtension) {
		this.homeNumberExtension = homeNumberExtension;
	}

	public String getPreferredNumber() {
		return preferredNumber;
	}

	public void setPreferredNumber(String preferredNumber) {
		this.preferredNumber = preferredNumber;
	}
	
}
