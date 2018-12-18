package com.mikealbert.data.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Mapped to MODEL_PRICE_LISTS table
 */
@Entity
@Table(name = "MODEL_PRICE_LISTS")
public class ModelPrice extends BaseEntity implements Serializable {
	private static final long serialVersionUID = -5939679286203552453L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="MPL_SEQ")    
    @SequenceGenerator(name="MPL_SEQ", sequenceName="MPL_SEQ", allocationSize=1)
    @Column(name = "MPL_ID", nullable=false)
    private Long modelId;
    	
	@Column(name="EFFECTIVE_DATE", nullable=false)
    @Temporal(TemporalType.TIMESTAMP)    
    private Date effectiveDate;	
	
    @Column(name = "BASIC_PRICE", nullable=false)
    private BigDecimal basePrice;	
    
    @Column(name = "VAT_AMOUNT", nullable=false)
    private BigDecimal vatAmount;	
    
    @Column(name = "TOTAL_PRICE", nullable=false)
    private BigDecimal totalPrice;	    
    
    @Column(name = "MSRP", nullable=false)
    private BigDecimal msrp;
    
    @Column(name = "HOLDBACK")
    private BigDecimal holdback;
    
    @JoinColumn(name = "MDL_MDL_ID", referencedColumnName = "MDL_ID")
    @ManyToOne
    private Model model;       
	
	public ModelPrice(){}

	public Long getModelId() {
		return modelId;
	}

	public void setModelId(Long modelId) {
		this.modelId = modelId;
	}

	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public BigDecimal getBasePrice() {
		return basePrice;
	}

	public void setBasePrice(BigDecimal basePrice) {
		this.basePrice = basePrice;
	}

	public BigDecimal getVatAmount() {
		return vatAmount;
	}

	public void setVatAmount(BigDecimal vatAmount) {
		this.vatAmount = vatAmount;
	}

	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}

	public BigDecimal getMsrp() {
		return msrp;
	}

	public void setMsrp(BigDecimal msrp) {
		this.msrp = msrp;
	}

	public BigDecimal getHoldback() {
		return holdback;
	}

	public void setHoldback(BigDecimal holdback) {
		this.holdback = holdback;
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}
}
