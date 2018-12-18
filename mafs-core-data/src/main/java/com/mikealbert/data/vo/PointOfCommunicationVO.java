package com.mikealbert.data.vo;

import java.io.Serializable;

import com.mikealbert.data.entity.ClientContact;
import com.mikealbert.data.entity.ClientPoint;
import com.mikealbert.data.entity.ClientPointAccount;
import com.mikealbert.util.MALUtilities;

public class PointOfCommunicationVO implements Serializable {
	private static final long serialVersionUID = -5480911325571799579L;

	private String name;
	private String description;
	private ClientPoint poc;
	private ClientPointAccount clientPOC;
	private boolean driverAssignable;
	private boolean contactAssignable;
	private boolean multipleRecipientsAssignable;
	private boolean deliveryMethodMailUpdatable;
	private boolean deliveryMethodPhoneUpdatable;		
	private boolean deliveryMethodEmailUpdatable;
	private boolean defaultEmail;
	private boolean defaultMail;
	private boolean defaultPhone;
	private boolean mailRequired;
	private boolean phoneRequired;
	private boolean emailRequired;
	private boolean poBoxAllowed;
	private boolean contactRequired;
	
	public PointOfCommunicationVO(){}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ClientPoint getPoc() {
		return poc;
	}

	public void setPoc(ClientPoint poc) {
		this.poc = poc;
	}

	public ClientPointAccount getClientPOC() {
		return clientPOC;
	}

	public void setClientPOC(ClientPointAccount clientPOC) {
		this.clientPOC = clientPOC;
	}

	public boolean isDriverAssignable() {
		return driverAssignable;
	}

	public void setDriverAssignable(boolean driverAssignable) {
		this.driverAssignable = driverAssignable;
	}
	
	public boolean isDeliveryMethodMailUpdatable() {
		return deliveryMethodMailUpdatable;
	}

	public void setDeliveryMethodMailUpdatable(boolean deliveryMethodMailUpdatable) {
		this.deliveryMethodMailUpdatable = deliveryMethodMailUpdatable;
	}

	public boolean isDeliveryMethodEmailUpdatable() {
		return deliveryMethodEmailUpdatable;
	}

	public void setDeliveryMethodEmailUpdatable(boolean deliveryMethodEmailUpdatable) {
		this.deliveryMethodEmailUpdatable = deliveryMethodEmailUpdatable;
	}

	public boolean isDriverAssigned(){
		boolean isAssigned = false;
		
		if( !(MALUtilities.isEmpty(getClientPOC()) 
				|| MALUtilities.isEmpty(getClientPOC().getClientContacts())
				|| getClientPOC().getClientContacts().size() == 0) ){
			for(ClientContact cc : getClientPOC().getClientContacts()){
				if(MALUtilities.convertYNToBoolean(cc.getDrvInd()) && MALUtilities.isEmpty(cc.getCostCentreCode())){
					isAssigned = true;
				}
			}			
		}

		return isAssigned;
	}

	public boolean isContactAssignable() {
		return contactAssignable;
	}

	public void setContactAssignable(boolean contactAssignable) {
		this.contactAssignable = contactAssignable;
	}

	public boolean isMultipleRecipientsAssignable() {
		return multipleRecipientsAssignable;
	}

	public void setMultipleRecipientsAssignable(boolean multipleRecipientsAssignable) {
		this.multipleRecipientsAssignable = multipleRecipientsAssignable;
	}

	public boolean isDefaultEmail() {
		return defaultEmail;
	}

	public void setDefaultEmail(boolean defaultEmail) {
		this.defaultEmail = defaultEmail;
	}

	public boolean isDefaultMail() {
		return defaultMail;
	}

	public void setDefaultMail(boolean defaultMail) {
		this.defaultMail = defaultMail;
	}

	public boolean isDeliveryMethodPhoneUpdatable() {
		return deliveryMethodPhoneUpdatable;
	}

	public void setDeliveryMethodPhoneUpdatable(boolean deliveryMethodPhoneUpdatable) {
		this.deliveryMethodPhoneUpdatable = deliveryMethodPhoneUpdatable;
	}

	public boolean isDefaultPhone() {
		return defaultPhone;
	}

	public void setDefaultPhone(boolean defaultPhone) {
		this.defaultPhone = defaultPhone;
	}

	public boolean isMailRequired() {
		return mailRequired;
	}

	public void setMailRequired(boolean mailRequired) {
		this.mailRequired = mailRequired;
	}

	public boolean isPhoneRequired() {
		return phoneRequired;
	}

	public void setPhoneRequired(boolean phoneRequired) {
		this.phoneRequired = phoneRequired;
	}

	public boolean isEmailRequired() {
		return emailRequired;
	}

	public void setEmailRequired(boolean emailRequired) {
		this.emailRequired = emailRequired;
	}

	public boolean isPoBoxAllowed() {
		return poBoxAllowed;
	}

	public void setPoBoxAllowed(boolean poBoxAllowed) {
		this.poBoxAllowed = poBoxAllowed;
	}

	public boolean isContactRequired() {
		return contactRequired;
	}

	public void setContactRequired(boolean contactRequired) {
		this.contactRequired = contactRequired;
	}

}
