package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;

import java.math.BigDecimal;


/**
 * The persistent class for the MT_IF_DETAIL database table.
 * 
 */
@Entity
@Table(name="MT_IF_DETAIL")
public class ServiceProviderInvoiceDetail implements Serializable {
	private static final long serialVersionUID = 4254526049215876938L;

	@EmbeddedId
	private ServiceProviderInvoiceDetailPK id;
	
	@Column(name="DESCRIPTION")
	private String description;
	@Column(name="DISC_RB_AMT")
	private BigDecimal discRbAmt;
	@Column(name="EXCESS_QTY")	
	private BigDecimal excessQty;
	@Column(name="EXCISE_TAX")
	private BigDecimal exciseTax;
	
	@Column(name="LINE_TYPE")
	private String lineType = "R";
	
	@Column(name="MRT_ID")	
	private Long mrtId;
	@Column(name="QUANTITY")
	private BigDecimal quantity;
	
	@Column(name="RECORD_TYPE")
	private String recordType = "D";
	
	@Column(name="SECOND_MRT_ID")
	private Long secondMrtId;
	@Column(name="TAX_AMOUNT")
	private BigDecimal taxAmount;
	@Column(name="TOTAL_COST")
	private BigDecimal totalCost;
	@Column(name="UNIT_COST")
	private BigDecimal unitCost;
	@Column(name="VENDOR_CODE")
	private String vendorCode;

	@MapsId("recordId")
	@ManyToOne
	@JoinColumn(name = "record_id", referencedColumnName = "record_id")
  	private ServiceProviderInvoiceHeader header;
	
	public ServiceProviderInvoiceDetail() {
	}

	public ServiceProviderInvoiceHeader getHeader() {
		return header;
	}
	
	public void setHeader(ServiceProviderInvoiceHeader header) {
		this.header = header;
	}

	public ServiceProviderInvoiceDetailPK getId() {
		return this.id;
	}

	public void setId(ServiceProviderInvoiceDetailPK id) {
		this.id = id;
	}


	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getDiscRbAmt() {
		return this.discRbAmt;
	}

	public void setDiscRbAmt(BigDecimal discRbAmt) {
		this.discRbAmt = discRbAmt;
	}



	public BigDecimal getExcessQty() {
		return this.excessQty;
	}

	public void setExcessQty(BigDecimal excessQty) {
		this.excessQty = excessQty;
	}



	public BigDecimal getExciseTax() {
		return this.exciseTax;
	}

	public void setExciseTax(BigDecimal exciseTax) {
		this.exciseTax = exciseTax;
	}


	public String getLineType() {
		return this.lineType;
	}
	


	public void setLineType(String lineType) {
		this.lineType = lineType;
	}

	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}

	public Long getMrtId() {
		return this.mrtId;
	}

	public void setMrtId(Long mrtId) {
		this.mrtId = mrtId;
	}


	public BigDecimal getQuantity() {
		return this.quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public String getRecordType() {
		return this.recordType;
	}


	public Long getSecondMrtId() {
		return this.secondMrtId;
	}

	public void setSecondMrtId(Long secondMrtId) {
		this.secondMrtId = secondMrtId;
	}



	public BigDecimal getTaxAmount() {
		return this.taxAmount;
	}

	public void setTaxAmount(BigDecimal taxAmount) {
		this.taxAmount = taxAmount;
	}



	public BigDecimal getTotalCost() {
		return this.totalCost;
	}

	public void setTotalCost(BigDecimal totalCost) {
		this.totalCost = totalCost;
	}


	
	public BigDecimal getUnitCost() {
		return this.unitCost;
	}

	public void setUnitCost(BigDecimal unitCost) {
		this.unitCost = unitCost;
	}


	
	public String getVendorCode() {
		return this.vendorCode;
	}

	public void setVendorCode(String vendorCode) {
		this.vendorCode = vendorCode;
	}

	
}