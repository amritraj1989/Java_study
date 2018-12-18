package com.mikealbert.data.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
*
* @author sibley
*/
@Entity
@Table(name = "QUOTATION_MODEL_ACCESSORIES")
public class QuotationModelAccessory extends BaseEntity{
    private static final long serialVersionUID = 1L;
    
    @Id
    @NotNull
    @Column(name = "QMA_ID")
    private Long qmaId;
    
    @NotNull
    @Column(name = "BASIC_PRICE")
    private BigDecimal basicPrice;
    
    @Column(name = "DISC_PRICE")
    private BigDecimal discPrice;
    
    @NotNull
    @Column(name = "VAT_AMOUNT")
    private BigDecimal vatAmount;
    
    @NotNull
    @Column(name = "TOTAL_PRICE")
    private BigDecimal totalPrice;

    @NotNull
    @Column(name = "ACC_QTY")
    private short accQty;
    
    @Size(min = 1, max = 1)
    @Column(name = "CAPITALISED_YN")
    private String capitalisedYn;
    
    @Size(min = 1, max = 1)
    @Column(name = "DRIVER_RECHARGE_YN")
    private String driverRechargeYn;

    @Size(min = 1, max = 1)
    @Column(name = "FREE_OF_CHARGE_YN")
    private String freeOfChargeYn;
    
    @Size(min = 1, max = 1)
    @Column(name = "REQUIRED_YN")
    private String requiredYn;
    
    @Size(max = 10)
    @Column(name = "CURRENCY_CODE")
    private String currencyCode;
    
    @Column(name = "EXCHANGE_RATE")
    private BigDecimal exchangeRate;
    
    @Size(max = 10)
    @Column(name = "EXCHANGE_RATE_TYPE")
    private String exchangeRateType;
    
    @Size(max = 25)
    @Column(name = "LAST_AMENDED_USER")
    private String lastAmendedUser;
    
    @Column(name = "LAST_AMENDED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastAmendedDate;
    
    @Column(name = "MAINTENANCE_AMT")
    private BigDecimal maintenanceAmt;
    
    @Column(name = "RENTAL_AMT")
    private BigDecimal rentalAmt;
    
    @Column(name = "RESIDUAL_AMT")
    private BigDecimal residualAmt;
    
    @Column(name = "RECHARGE_AMOUNT")
    private BigDecimal rechargeAmount;
    
    @JoinColumn(name = "QMD_QMD_ID", referencedColumnName = "QMD_ID")
    @ManyToOne(optional = false)
    private QuotationModel quotationModel;
    
    @JoinColumn(name = "OAC_OAC_ID", referencedColumnName = "OAC_ID")
    @ManyToOne(optional = false)
    private OptionalAccessory optionalAccessory;

    public QuotationModelAccessory() {}

    public Long getQmaId() {
        return qmaId;
    }

    public void setQmaId(Long qmaId) {
        this.qmaId = qmaId;
    }

    public BigDecimal getBasicPrice() {
        return basicPrice;
    }

    public void setBasicPrice(BigDecimal basicPrice) {
        this.basicPrice = basicPrice;
    }

    public BigDecimal getDiscPrice() {
        return discPrice;
    }

    public void setDiscPrice(BigDecimal discPrice) {
        this.discPrice = discPrice;
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

    public short getAccQty() {
        return accQty;
    }

    public void setAccQty(short accQty) {
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

    public String getFreeOfChargeYn() {
        return freeOfChargeYn;
    }

    public void setFreeOfChargeYn(String freeOfChargeYn) {
        this.freeOfChargeYn = freeOfChargeYn;
    }

    public String getRequiredYn() {
        return requiredYn;
    }

    public void setRequiredYn(String requiredYn) {
        this.requiredYn = requiredYn;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public String getExchangeRateType() {
        return exchangeRateType;
    }

    public void setExchangeRateType(String exchangeRateType) {
        this.exchangeRateType = exchangeRateType;
    }

    public String getLastAmendedUser() {
        return lastAmendedUser;
    }

    public void setLastAmendedUser(String lastAmendedUser) {
        this.lastAmendedUser = lastAmendedUser;
    }

    public Date getLastAmendedDate() {
        return lastAmendedDate;
    }

    public void setLastAmendedDate(Date lastAmendedDate) {
        this.lastAmendedDate = lastAmendedDate;
    }

    public BigDecimal getMaintenanceAmt() {
        return maintenanceAmt;
    }

    public void setMaintenanceAmt(BigDecimal maintenanceAmt) {
        this.maintenanceAmt = maintenanceAmt;
    }

    public BigDecimal getRentalAmt() {
        return rentalAmt;
    }

    public void setRentalAmt(BigDecimal rentalAmt) {
        this.rentalAmt = rentalAmt;
    }

    public BigDecimal getResidualAmt() {
        return residualAmt;
    }

    public void setResidualAmt(BigDecimal residualAmt) {
        this.residualAmt = residualAmt;
    }

    public BigDecimal getRechargeAmount() {
        return rechargeAmount;
    }

    public void setRechargeAmount(BigDecimal rechargeAmount) {
        this.rechargeAmount = rechargeAmount;
    }

    public QuotationModel getQuotationModel() {
		return quotationModel;
	}

	public void setQuotationModel(QuotationModel quotationModel) {
		this.quotationModel = quotationModel;
	}

    public OptionalAccessory getOptionalAccessory() {
		return optionalAccessory;
	}

	public void setOptionalAccessory(OptionalAccessory optionalAccessory) {
		this.optionalAccessory = optionalAccessory;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (qmaId != null ? qmaId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof QuotationModelAccessory)) {
            return false;
        }
        QuotationModelAccessory other = (QuotationModelAccessory) object;
        if ((this.qmaId == null && other.qmaId != null) || (this.qmaId != null && !this.qmaId.equals(other.qmaId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mikealbert.data.entity.QuotationModelAccessories[ qmaId=" + qmaId + " ]";
    }
}
