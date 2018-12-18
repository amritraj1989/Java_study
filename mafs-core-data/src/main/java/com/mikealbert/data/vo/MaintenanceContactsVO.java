package com.mikealbert.data.vo;

import java.io.Serializable;
import java.util.List;

import com.mikealbert.data.entity.ClientContact;

public class MaintenanceContactsVO implements Serializable{
	private static final long serialVersionUID = 1L;
	private long contactId;
	private String contactType;
	private String firstName;
	private String lastName;
	private String email;
	private String cellAreaCode;
	private String cellNumber;
	private String cellExtension;
	private String cellCncCode;
	private	boolean cellNumberPref = false;
	private String workAreaCode;
	private String workNumber;
	private String workExtension;
	private String workCncCode;
	private	boolean workNumberPref = false;
	//mss 459
	private	String pocDescription;
	private String homeAreaCode;
	private String homeNumber;
	private String homeExtension;
	private String homeCncCode;
	private	boolean homeNumberPref = false;
	private	List<ClientContact> assignedContacts;
	private	boolean driverInd;
	
	
	public String getContactType() {
		return contactType;
	}
	public void setContactType(String contactType) {
		this.contactType = contactType;
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
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
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
	public String getCellExtension() {
		return cellExtension;
	}
	public void setCellExtension(String cellExtension) {
		this.cellExtension = cellExtension;
	}
	public String getCellCncCode() {
		return cellCncCode;
	}
	public void setCellCncCode(String cellCncCode) {
		this.cellCncCode = cellCncCode;
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
	public String getWorkExtension() {
		return workExtension;
	}
	public void setWorkExtension(String workExtension) {
		this.workExtension = workExtension;
	}
	public String getWorkCncCode() {
		return workCncCode;
	}
	public void setWorkCncCode(String workCncCode) {
		this.workCncCode = workCncCode;
	}
	public long getContactId() {
		return contactId;
	}
	public void setContactId(long contactId) {
		this.contactId = contactId;
	}
	public String getPocDescription() {
		return pocDescription;
	}
	public void setPocDescription(String pocDescription) {
		this.pocDescription = pocDescription;
	}
	public String getHomeNumber() {
		return homeNumber;
	}
	public void setHomeNumber(String homeNumber) {
		this.homeNumber = homeNumber;
	}
	public List<ClientContact> getAssignedContacts() {
		return assignedContacts;
	}
	public void setAssignedContacts(List<ClientContact> assignedContacts) {
		this.assignedContacts = assignedContacts;
	}
	public String getHomeAreaCode() {
		return homeAreaCode;
	}
	public void setHomeAreaCode(String homeAreaCode) {
		this.homeAreaCode = homeAreaCode;
	}
	public String getHomeExtension() {
		return homeExtension;
	}
	public void setHomeExtension(String homeExtension) {
		this.homeExtension = homeExtension;
	}
	public String getHomeCncCode() {
		return homeCncCode;
	}
	public void setHomeCncCode(String homeCncCode) {
		this.homeCncCode = homeCncCode;
	}
	public boolean isDriverInd() {
		return driverInd;
	}
	public void setDriverInd(boolean driverInd) {
		this.driverInd = driverInd;
	}
	public boolean isCellNumberPref() {
		return cellNumberPref;
	}
	public void setCellNumberPref(boolean cellNumberPref) {
		this.cellNumberPref = cellNumberPref;
	}
	public boolean isWorkNumberPref() {
		return workNumberPref;
	}
	public void setWorkNumberPref(boolean workNumberPref) {
		this.workNumberPref = workNumberPref;
	}
	public boolean isHomeNumberPref() {
		return homeNumberPref;
	}
	public void setHomeNumberPref(boolean homeNumberPref) {
		this.homeNumberPref = homeNumberPref;
	}	
	

}
