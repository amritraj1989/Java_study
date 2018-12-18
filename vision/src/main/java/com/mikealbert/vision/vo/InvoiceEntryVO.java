package com.mikealbert.vision.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.mikealbert.data.entity.KeyCode;

public class InvoiceEntryVO implements Serializable {

	private static final long serialVersionUID = 1L;
	private String invoiceNumber;
	private BigDecimal invoiceAmount;
	private Date receivedDate;
	private Date invoiceDate;
	private Date dueDate;
	private String vin;
	private BigDecimal grossVehicleWeight;
	private BigDecimal msrp;
	private String shipWeight;
	private String keyCode;
	private String keyCodeDesc;
	private Long	keyCodeId;
	private	List<KeyCode> existingKeyCodes;
	private String paymentMethod;
	private	String	paymentTermCode ;
	
	private String	poNumber;
	private	String	vendorCode;
	private	String	vendorName;
	private Long	vendorContextId;
	private	String	unitNumber;
	private String	unitDesc;
	private Long	fmsId;
	private String opCode;

	private Long	releasedPODocId;
	private Long	invoiceDocId;
	private boolean	thirdPartyPO = false;
	private Long poRevNo ;
	private String updatedVendorCode = null;

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public BigDecimal getInvoiceAmount() {
		return invoiceAmount;
	}

	public void setInvoiceAmount(BigDecimal invoiceAmount) {
		this.invoiceAmount = invoiceAmount;
	}

	public Date getReceivedDate() {
		return receivedDate;
	}

	public void setReceivedDate(Date receivedDate) {
		this.receivedDate = receivedDate;
	}

	public Date getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public String getVin() {
		return vin;
	}

	public void setVin(String vin) {
		this.vin = vin;
	}

	public BigDecimal getGrossVehicleWeight() {
		return grossVehicleWeight;
	}

	public void setGrossVehicleWeight(BigDecimal grossVehicleWeight) {
		this.grossVehicleWeight = grossVehicleWeight;
	}

	

	public BigDecimal getMsrp() {
		return msrp;
	}

	public void setMsrp(BigDecimal msrp) {
		this.msrp = msrp;
	}

	

	

	public String getShipWeight() {
		return shipWeight;
	}

	public void setShipWeight(String shipWeight) {
		this.shipWeight = shipWeight;
	}

	public String getKeyCode() {
		return keyCode;
	}

	public void setKeyCode(String keyCode) {
		this.keyCode = keyCode;
	}

	public String getPoNumber() {
		return poNumber;
	}

	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
	}

	public String getVendorCode() {
		return vendorCode;
	}

	public void setVendorCode(String vendorCode) {
		this.vendorCode = vendorCode;
	}

	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}

	public String getUnitNumber() {
		return unitNumber;
	}

	public void setUnitNumber(String unitNumber) {
		this.unitNumber = unitNumber;
	}

	public String getUnitDesc() {
		return unitDesc;
	}

	public void setUnitDesc(String unitDesc) {
		this.unitDesc = unitDesc;
	}

	public String getKeyCodeDesc() {
		return keyCodeDesc;
	}

	public void setKeyCodeDesc(String keyCodeDesc) {
		this.keyCodeDesc = keyCodeDesc;
	}

	public Long getFmsId() {
		return fmsId;
	}

	public void setFmsId(Long fmsId) {
		this.fmsId = fmsId;
	}

	

	public Long getReleasedPODocId() {
		return releasedPODocId;
	}

	public void setReleasedPODocId(Long releasedPODocId) {
		this.releasedPODocId = releasedPODocId;
	}

	public Long getInvoiceDocId() {
		return invoiceDocId;
	}

	public void setInvoiceDocId(Long invoiceDocId) {
		this.invoiceDocId = invoiceDocId;
	}

	public List<KeyCode> getExistingKeyCodes() {
		return existingKeyCodes;
	}

	public void setExistingKeyCodes(List<KeyCode> existingKeyCodes) {
		this.existingKeyCodes = existingKeyCodes;
	}

	public Long getVendorContextId() {
		return vendorContextId;
	}

	public void setVendorContextId(Long vendorContextId) {
		this.vendorContextId = vendorContextId;
	}

	public String getOpCode() {
		return opCode;
	}

	public void setOpCode(String opCode) {
		this.opCode = opCode;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public String getPaymentTermCode() {
		return paymentTermCode;
	}

	public void setPaymentTermCode(String paymentTermCode) {
		this.paymentTermCode = paymentTermCode;
	}

	public boolean isThirdPartyPO() {
		return thirdPartyPO;
	}

	public void setThirdPartyPO(boolean thirdPartyPO) {
		this.thirdPartyPO = thirdPartyPO;
	}

	public Long getKeyCodeId() {
		return keyCodeId;
	}

	public void setKeyCodeId(Long keyCodeId) {
		this.keyCodeId = keyCodeId;
	}

	public Long getPoRevNo() {
		return poRevNo;
	}

	public void setPoRevNo(Long poRevNo) {
		this.poRevNo = poRevNo;
	}

	public String getUpdatedVendorCode() {
		return updatedVendorCode;
	}

	public void setUpdatedVendorCode(String updatedVendorCode) {
		this.updatedVendorCode = updatedVendorCode;
	}
	

}
