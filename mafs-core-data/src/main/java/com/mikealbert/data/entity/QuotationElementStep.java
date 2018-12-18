package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;

import java.math.BigDecimal;


/**
 * The persistent class for the QUOTATION_ELEMENT_STEPS database table.
 * 
 */
@Entity
@Table(name="QUOTATION_ELEMENT_STEPS")
public class QuotationElementStep extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="qes_seq")    
	@SequenceGenerator(name="qes_seq", sequenceName="qes_seq", allocationSize=1)  
	@Column(name="QES_ID")
	private Long qesId;

	@Column(name="CONTRACT_CHANGE_RENTAL")
	private BigDecimal contractChangeRental;

	@Column(name="END_CAPITAL")
	private BigDecimal endCapital;

	@Column(name="FROM_PERIOD")
	private BigDecimal fromPeriod;

	@Column(name="MANUAL_RENTAL")
	private BigDecimal manualRental;

	@Column(name="RENTAL_VALUE")
	private BigDecimal rentalValue;

	@Column(name="START_CAPITAL")
	private BigDecimal startCapital;

	@Column(name="TO_PERIOD")
	private BigDecimal toPeriod;

	//bi-directional many-to-one association to QuotationElement
    @ManyToOne
	@JoinColumn(name="QEL_QEL_ID")
	private QuotationElement quotationElement;

    public QuotationElementStep() {
    }

	public long getQesId() {
		return this.qesId;
	}

	public void setQesId(long qesId) {
		this.qesId = qesId;
	}

	public BigDecimal getContractChangeRental() {
		return this.contractChangeRental;
	}

	public void setContractChangeRental(BigDecimal contractChangeRental) {
		this.contractChangeRental = contractChangeRental;
	}

	public BigDecimal getEndCapital() {
		return this.endCapital;
	}

	public void setEndCapital(BigDecimal endCapital) {
		this.endCapital = endCapital;
	}

	public BigDecimal getFromPeriod() {
		return this.fromPeriod;
	}

	public void setFromPeriod(BigDecimal fromPeriod) {
		this.fromPeriod = fromPeriod;
	}

	public BigDecimal getManualRental() {
		return this.manualRental;
	}

	public void setManualRental(BigDecimal manualRental) {
		this.manualRental = manualRental;
	}

	public BigDecimal getRentalValue() {
		return this.rentalValue;
	}

	public void setRentalValue(BigDecimal rentalValue) {
		this.rentalValue = rentalValue;
	}

	public BigDecimal getStartCapital() {
		return this.startCapital;
	}

	public void setStartCapital(BigDecimal startCapital) {
		this.startCapital = startCapital;
	}

	public BigDecimal getToPeriod() {
		return this.toPeriod;
	}

	public void setToPeriod(BigDecimal toPeriod) {
		this.toPeriod = toPeriod;
	}

	public QuotationElement getQuotationElement() {
		return this.quotationElement;
	}

	public void setQuotationElement(QuotationElement quotationElement) {
		this.quotationElement = quotationElement;
	}
	
}