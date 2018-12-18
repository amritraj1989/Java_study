package com.mikealbert.data.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * The persistent class for the OPTION_PRICE_LISTS database table.
 * @author sibley
 */
@Entity
@Table(name="OPTION_PRICE_LISTS")
public class OptionPrice extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 6254874864214801901L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="OPL_SEQ")    
    @SequenceGenerator(name="OPL_SEQ", sequenceName="OPL_SEQ", allocationSize=1)	
    @NotNull
    @Column(name = "OPL_ID")
    private Long oplId;
    
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
    
    @JoinColumn(name = "OAC_OAC_ID", referencedColumnName = "OAC_ID")
    @ManyToOne
    private OptionalAccessory optionalAccessory;  
    
    public OptionPrice() {}

	public Long getOplId() {
		return oplId;
	}

	public void setOplId(Long oplId) {
		this.oplId = oplId;
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

	public OptionalAccessory getOptionalAccessory() {
		return optionalAccessory;
	}

	public void setOptionalAccessory(OptionalAccessory optionalAccessory) {
		this.optionalAccessory = optionalAccessory;
	}

	@Override
    public String toString() {
        return "com.mikealbert.data.entity.OptionPrice[ oplId=" + this.getOplId() + " ]";
    }
	
}