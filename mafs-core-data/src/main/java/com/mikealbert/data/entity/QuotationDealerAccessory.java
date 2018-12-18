package com.mikealbert.data.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
* The persistent class for the QUOTATION_DEALER_ACCESSORIES database table.
* NOTE: Not all columns are mapped.
* @author sibley
*/
@Entity
@Table(name = "QUOTATION_DEALER_ACCESSORIES")
public class QuotationDealerAccessory extends BaseEntity implements Serializable{
    private static final long serialVersionUID = 1L;    
    
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="QDA_SEQ")    
    @SequenceGenerator(name="QDA_SEQ", sequenceName="QDA_SEQ", allocationSize=1)    
    @Column(name = "QDA_ID")
    private Long qdaId;
             
    @NotNull
    @Column(name = "RECHARGE_AMOUNT")
    private BigDecimal rechargeAmount;
    
    @NotNull
    @Column(name = "TOTAL_PRICE")
    private BigDecimal totalPrice;
    
    @Column(name = "DISC_PRICE")
    private BigDecimal discPrice;
    
    @Column(name = "RESIDUAL_AMT")
    private BigDecimal residualAmt;
    
    @Column(name = "RENTAL_AMT")
    private BigDecimal rentalAmt;
    
    @Column(name = "BASIC_PRICE", nullable=false)
    private BigDecimal basicPrice;
    
    @Column(name = "VAT_AMOUNT", nullable=false)
    private BigDecimal vatAmount;
    
    @Column(name = "ACC_QTY", nullable=false)
    private Integer accQty;
    
    @Column(name = "FREE_OF_CHARGE_YN", nullable=false)
    private String freeOfChargeYn;
    
    @Column(name = "CAPITALISED_YN", nullable=false)
    private String capitalisedYn;
    
    @Column(name = "DRIVER_RECHARGE_YN", nullable=false)
    private String driverRechargeYn;
    
    @Column(name = "REQUIRED_YN", nullable=false)
    private String requiredYn;
    
    @Column(name="EXTERNAL_REFERENCE_NO")
    private String externalReferenceNo;
    
    @JoinColumn(name = "QMD_QMD_ID", referencedColumnName = "QMD_ID")
    @ManyToOne(optional = false)
    private QuotationModel quotationModel;
    
    @JoinColumn(name = "DAC_DAC_ID", referencedColumnName = "DAC_ID")
    @ManyToOne(optional = false)
    private DealerAccessory dealerAccessory;
    
    @JoinColumns({
        @JoinColumn(name = "C_ID", referencedColumnName = "C_ID"),
        @JoinColumn(name = "ACCOUNT_TYPE", referencedColumnName = "ACCOUNT_TYPE"),
        @JoinColumn(name = "ACCOUNT_CODE", referencedColumnName = "ACCOUNT_CODE")})
    @ManyToOne
    private ExternalAccount vendorAccount;

    public QuotationDealerAccessory() {}
    
    public Long getQdaId() {
        return qdaId;
    }

    public void setQdaId(Long qdaId) {
        this.qdaId = qdaId;
    }

    public BigDecimal getRechargeAmount() {
        return rechargeAmount;
    }

    public void setRechargeAmount(BigDecimal rechargeAmount) {
        this.rechargeAmount = rechargeAmount;
    }
    
    public BigDecimal getTotalPrice() {
	    return totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
	     this.totalPrice = totalPrice;
	}
	
    public QuotationModel getQuotationModel() {
		return quotationModel;
	}

	public void setQuotationModel(QuotationModel quotationModel) {
		this.quotationModel = quotationModel;
	}

	public DealerAccessory getDealerAccessory() {
		return dealerAccessory;
	}

	public void setDealerAccessory(DealerAccessory dealerAccessory) {
		this.dealerAccessory = dealerAccessory;
	}
	
	

	/**
	 * Getter method for diskPrice
	 * @return the discPrice
	 */
	public BigDecimal getDiscPrice() {
		return discPrice;
	}

	/**
	 * Setter method for diskPrice
	 * @param discPrice the diskPrice to set
	 */
	public void setDiscPrice(BigDecimal discPrice) {
		this.discPrice = discPrice;
	}

	/**
	 * Getter method for residualAmt
	 * @return the residualAmt
	 */
	public BigDecimal getResidualAmt() {
		return residualAmt;
	}

	/**
	 * Setter method for residualAmt
	 * @param residualAmt the residualAmt to set
	 */
	public void setResidualAmt(BigDecimal residualAmt) {
		this.residualAmt = residualAmt;
	}
	
	

	public BigDecimal getRentalAmt() {
		return rentalAmt;
	}

	public void setRentalAmt(BigDecimal rentalAmt) {
		this.rentalAmt = rentalAmt;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (qdaId != null ? qdaId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof QuotationDealerAccessory)) {
            return false;
        }
        QuotationDealerAccessory other = (QuotationDealerAccessory) object;
        if ((this.qdaId == null && other.qdaId != null) || (this.qdaId != null && !this.qdaId.equals(other.qdaId))) {
            return false;
        }
        return true;
    }
    
    public BigDecimal getBasicPrice() {
		return basicPrice;
	}

	public void setBasicPrice(BigDecimal basicPrice) {
		this.basicPrice = basicPrice;
	}    

	public String getFreeOfChargeYn() {
		return freeOfChargeYn;
	}

	public void setFreeOfChargeYn(String freeOfChargeYn) {
		this.freeOfChargeYn = freeOfChargeYn;
	}
	
    public String getExternalReferenceNo() {
		return externalReferenceNo;
	}

	public void setExternalReferenceNo(String externalReferenceNo) {
		this.externalReferenceNo = externalReferenceNo;
	}

	public ExternalAccount getVendorAccount() {
		return vendorAccount;
	}

	public void setVendorAccount(ExternalAccount vendorAccount) {
		this.vendorAccount = vendorAccount;
	}

	public BigDecimal getVatAmount() {
		return vatAmount;
	}

	public void setVatAmount(BigDecimal vatAmount) {
		this.vatAmount = vatAmount;
	}

	public Integer getAccQty() {
		return accQty;
	}

	public void setAccQty(Integer accQty) {
		this.accQty = accQty;
	}

	public String getCapitalisedYn() {
		return capitalisedYn;
	}

	public void setCapitalisedYn(String capitalisedYn) {
		this.capitalisedYn = capitalisedYn;
	}

	public String getDriverRechargeYn() {
		return driverRechargeYn;
	}

	public void setDriverRechargeYn(String driverRechargeYn) {
		this.driverRechargeYn = driverRechargeYn;
	}

	public String getRequiredYn() {
		return requiredYn;
	}

	public void setRequiredYn(String requiredYn) {
		this.requiredYn = requiredYn;
	}

	@Override
    public String toString() {
        return "com.mikealbert.data.entity.QuotationDealerAccessories[ qdaId=" + qdaId + " ]";
    }

	
}
