package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.*;

import java.math.BigDecimal;


@Entity
@Table(name="MAL_CAPITAL_COST")
public class MalCapitalCost extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MCAP_SEQ")
    @SequenceGenerator(name = "MCAP_SEQ", sequenceName = "MCAP_SEQ", allocationSize = 1)
	@Column(name="MAL_ID")
	private Long malId;

	@Column(name="ELEMENT_ID")
	private Long elementId;

	@Column(name="ELEMENT_TYPE")
	private String elementType;

	@Column(name="DOCL_DOC_ID")
	private Long doclDocId;

	@Column(name="DOCL_LINE_ID")
	private Long doclLineId;

	@Column(name="FMS_FMS_ID")
	private Long fmsFmsId;

	@Column(name="QMD_QMD_ID")
	private Long qmdQmdId;

	@Column(name="TOTAL_PRICE")
	private BigDecimal totalPrice;

	@Column(name="UNIT_COST")
	private BigDecimal unitCost;

	@Column(name="UNIT_DISC")
	private BigDecimal unitDisc;

	@Column(name="UNIT_PRICE")
	private BigDecimal unitPrice;

	@Column(name="UNIT_TAX")
	private BigDecimal unitTax;
	
	@Column(name="OP_CODE")
	private String opCode;

	public MalCapitalCost() {
	}

	public long getMalId() {
		return this.malId;
	}

	public void setMalId(long malId) {
		this.malId = malId;
	}

	public Long getElementId() {
		return this.elementId;
	}

	public void setElementId(Long elementId) {
		this.elementId = elementId;
	}

	public String getElementType() {
		return this.elementType;
	}

	public void setElementType(String elementType) {
		this.elementType = elementType;
	}

	public Long getDoclDocId() {
		return this.doclDocId;
	}

	public void setDoclDocId(Long doclDocId) {
		this.doclDocId = doclDocId;
	}

	public Long getDoclLineId() {
		return this.doclLineId;
	}

	public void setDoclLineId(Long doclLineId) {
		this.doclLineId = doclLineId;
	}

	public Long getFmsFmsId() {
		return this.fmsFmsId;
	}

	public void setFmsFmsId(Long fmsFmsId) {
		this.fmsFmsId = fmsFmsId;
	}

	public Long getQmdQmdId() {
		return this.qmdQmdId;
	}

	public void setQmdQmdId(Long qmdQmdId) {
		this.qmdQmdId = qmdQmdId;
	}

	public BigDecimal getTotalPrice() {
		return this.totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}

	public BigDecimal getUnitCost() {
		return this.unitCost;
	}

	public void setUnitCost(BigDecimal unitCost) {
		this.unitCost = unitCost;
	}

	public BigDecimal getUnitDisc() {
		return this.unitDisc;
	}

	public void setUnitDisc(BigDecimal unitDisc) {
		this.unitDisc = unitDisc;
	}

	public BigDecimal getUnitPrice() {
		return this.unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	public BigDecimal getUnitTax() {
		return this.unitTax;
	}

	public void setUnitTax(BigDecimal unitTax) {
		this.unitTax = unitTax;
	}
	
	public String getOpCode() {
		return opCode;
	}

	public void setOpCode(String opCode) {
		this.opCode = opCode;
	}

	
	@Override
	public String toString() {
		return "MalCapitalCost [malId=" + malId + "]";
	}
	
	

}