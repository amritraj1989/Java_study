package com.mikealbert.data.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


@Entity
@Table(name="FUEL_UPLOAD")
public class FuelUpload  implements Serializable{
	private static final long serialVersionUID = 1L;
	@EmbeddedId
	private FuelUploadPK id;
	
	@Column(name= "TRANS_DATE")
	private Date	transactionDate;
	@Column(name= "TRANS_TIME")
	private	String	transactionTime;
	@Column(name= "WEXVEHID")
	private	String	wexVehId;
	@Column(name= "DISC_AMT")
	private	BigDecimal	discountAmount;
	@Column(name= "SITENAME")
	private	String	merchantName;
	@Column(name= "SITEADD")
	private	String	merchantAddress;
	@Column(name= "SITECITY")
	private String	merchantCity;
	@Column(name= "SITEZIP")
	private	String	merchantZip;
	@Column(name= "SITESTATE")
	private	String	merchantState;
	@Column(name= "SITEID")
	private	String	merchantCode;
	@Column(name= "FIRSTNAME")
	private String	firstName;
	@Column(name= "LASTNAME")
	private	String	lastName;
	@Column(name= "COVEHID")
	private	String	coVehId;
	@Column(name= "VIN")
	private	String	VIN;
	@Column(name= "GALLONS")
	private	BigDecimal	productQuantity;
	@Column(name= "FUEL_TAX")
	private	BigDecimal	fuelTax;
	@Column(name= "UNICOST")
	private	BigDecimal	unitCost;
	@Column(name= "ODOMETER")
	private	Long	odometerReading;
	@Column(name= "TYPE")
	private	String	type;
	@Column(name= "PRODUCTNAME")
	private	String	productDescription;
	@Column(name= "SEQNUMBER")
	private	String	sequenceNumber;
	@Column(name= "DAY")
	private String  day;
	@Column(name="ACCTNUM")
	private String provAccNo;
	@Column(name="GROSS")
	private BigDecimal totalAmountDue;
	
	public Date getTransactionDate() {
		return transactionDate;
	}
	
	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}
	
	public String getTransactionTime() {
		return transactionTime;
	}
	
	public void setTransactionTime(String transactionTime) {
		this.transactionTime = transactionTime;
	}
	
	public String getWexVehId() {
		return wexVehId;
	}
	
	public void setWexVehId(String wexVehId) {
		this.wexVehId = wexVehId;
	}
	
	public BigDecimal getDiscountAmount() {
		return discountAmount;
	}
	
	public void setDiscountAmount(BigDecimal discountAmount) {
		this.discountAmount = discountAmount;
	}
	
	public String getMerchantName() {
		return merchantName;
	}
	
	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}
	
	public String getMerchantAddress() {
		return merchantAddress;
	}
	
	public void setMerchantAddress(String merchantAddress) {
		this.merchantAddress = merchantAddress;
	}
	
	public String getMerchantCity() {
		return merchantCity;
	}
	
	public void setMerchantCity(String merchantCity) {
		this.merchantCity = merchantCity;
	}
	
	public String getMerchantZip() {
		return merchantZip;
	}
	
	public void setMerchantZip(String merchantZip) {
		this.merchantZip = merchantZip;
	}
	
	public String getMerchantState() {
		return merchantState;
	}
	
	public void setMerchantState(String merchantState) {
		this.merchantState = merchantState;
	}
	
	public String getMerchantCode() {
		return merchantCode;
	}
	
	public void setMerchantCode(String merchantCode) {
		this.merchantCode = merchantCode;
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
	
	public String getCoVehId() {
		return coVehId;
	}
	
	public void setCoVehId(String coVehId) {
		this.coVehId = coVehId;
	}
	
	public String getVIN() {
		return VIN;
	}
	
	public void setVIN(String vIN) {
		VIN = vIN;
	}
	
	public BigDecimal getProductQuantity() {
		return productQuantity;
	}
	
	public void setProductQuantity(BigDecimal productQuantity) {
		this.productQuantity = productQuantity;
	}
	
	public BigDecimal getFuelTax() {
		return fuelTax;
	}
	
	public void setFuelTax(BigDecimal fuelTax) {
		this.fuelTax = fuelTax;
	}
	
	public BigDecimal getUnitCost() {
		return unitCost;
	}
	
	public void setUnitCost(BigDecimal unitCost) {
		this.unitCost = unitCost;
	}
	
	public Long getOdometerReading() {
		return odometerReading;
	}
	
	public void setOdometerReading(Long odometerReading) {
		this.odometerReading = odometerReading;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getProductDescription() {
		return productDescription;
	}
	
	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
	}
	
	public String getSequenceNumber() {
		return sequenceNumber;
	}
	
	public void setSequenceNumber(String sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}
	
	public String getDay() {
		return day;
	}
	
	public void setDay(String day) {
		this.day = day;
	}
	
	public FuelUploadPK getId() {
		return id;
	}
	
	public void setId(FuelUploadPK id) {
		this.id = id;
	}
	
	public BigDecimal getTotalAmountDue() {
		return totalAmountDue;
	}
	
	public void setTotalAmountDue(BigDecimal totalAmountDue) {
		this.totalAmountDue = totalAmountDue;
	}

	public String getProvAccNo() {
		return provAccNo;
	}
	
	public void setProvAccNo(String provAccNo) {
		this.provAccNo = provAccNo;
	}	
}
