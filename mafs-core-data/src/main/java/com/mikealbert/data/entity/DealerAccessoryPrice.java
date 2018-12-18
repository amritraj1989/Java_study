package com.mikealbert.data.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * The persistent class for the DEALER_PRICE_LISTS database table.
 * @author sibley
 */
@Entity
@Table(name="DEALER_PRICE_LISTS")
public class DealerAccessoryPrice extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 6254874864214801901L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="DPL_SEQ")    
    @SequenceGenerator(name="DPL_SEQ", sequenceName="DPL_SEQ", allocationSize=1)	
    @NotNull
    @Column(name = "DPL_ID")
    private Long dplId;
    
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
    
    @Column(name = "LEAD_TIME")
    private Long leadTime;
    
    @JoinColumn(name = "DAC_DAC_ID", referencedColumnName = "DAC_ID")
    @ManyToOne
    private DealerAccessory dealerAccessory; 
    
    @JoinColumns({
        @JoinColumn(name = "C_ID", referencedColumnName = "C_ID"),
        @JoinColumn(name = "ACCOUNT_TYPE", referencedColumnName = "ACCOUNT_TYPE"),
        @JoinColumn(name = "ACCOUNT_CODE", referencedColumnName = "ACCOUNT_CODE")})
    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    private ExternalAccount payeeAccount;  
    
    @JoinColumn(name = "UFQ_UFQ_ID", referencedColumnName = "UFQ_ID")
    @ManyToOne(optional = true)
    private UpfitterQuote upfitterQuote;     
    
    public DealerAccessoryPrice() {}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (this.dplId != null ? this.dplId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DealerAccessoryPrice)) {
            return false;
        }
        
        DealerAccessoryPrice other = (DealerAccessoryPrice) object;
        if ((this.getDplId() == null && other.getDplId() != null) || (this.getDplId() != null && !this.getDplId().equals(other.getDplId()))) {
            return false;
        }
        
        return true;
    } 
    
	public Long getDplId() {
		return dplId;
	}

	public void setDplId(Long dplId) {
		this.dplId = dplId;
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

	public Long getLeadTime() {
		return leadTime;
	}

	public void setLeadTime(Long leadTime) {
		this.leadTime = leadTime;
	}

	public DealerAccessory getDealerAccessory() {
		return dealerAccessory;
	}

	public void setDealerAccessory(DealerAccessory dealerAccessory) {
		this.dealerAccessory = dealerAccessory;
	}

	public ExternalAccount getPayeeAccount() {
		return payeeAccount;
	}

	public void setPayeeAccount(ExternalAccount payeeAccount) {
		this.payeeAccount = payeeAccount;
	}

	public UpfitterQuote getUpfitterQuote() {
		return upfitterQuote;
	}

	public void setUpfitterQuote(UpfitterQuote upfitterQuote) {
		this.upfitterQuote = upfitterQuote;
	}

	@Override
    public String toString() {
        return "com.mikealbert.data.entity.DealerAccessoryPrice[ dplId=" + this.getDplId() + " ]";
    }
	
}