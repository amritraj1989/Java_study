package com.mikealbert.data.entity;
import java.io.Serializable;
import javax.persistence.*;

import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the QUOTATION_PROFITABILITY database table.
 * 
 */
@Entity
@Table(name="QUOTATION_PROFITABILITY")
public class QuotationProfitability extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="qpy_seq")    
	@SequenceGenerator(name="qpy_seq", sequenceName="qpy_seq", allocationSize=1)  
	@Column(name="QPY_ID")
	private Long qpyId;

	@Column(name="PROFIT_AMOUNT")
	private BigDecimal profitAmount;

	@Column(name="PROFIT_SOURCE")
	private String profitSource;

	@Column(name="PROFIT_TYPE")
	private String profitType;

	@Column(name="PROFIT_BASE")
	private BigDecimal profitBase;

	@Column(name="PROFIT_ADJUSTMENT")
	private BigDecimal profitAdjustment;

	 @Column(name = "MIN_LEASE_RATE", precision = 30, scale = 5)
	 private BigDecimal minLeaseRate;
	    
	 @Column(name = "IRR_APPROVED_LIMIT", precision = 30, scale = 5)
	 private BigDecimal irrApprovedLimit;

	 @Temporal( TemporalType.DATE)
	 @Column(name="IRR_APPROVED_DATE")
	 private Date irrApprovedDate;
	 
	 @Column(name="IRR_APPROVED_USER")
		private String irrApprovedUser;
	 
	@ManyToOne
	@JoinColumn(name = "QMD_QMD_ID", referencedColumnName = "QMD_ID", nullable=false)
	private QuotationModel quotationModel;
	
	
	public QuotationProfitability() {}

	public long getQpyId() {
		return this.qpyId;
	}

	public void setQpyId(long qpyId) {
		this.qpyId = qpyId;
	}

	public BigDecimal getProfitAmount() {
		return this.profitAmount;
	}

	public void setProfitAmount(BigDecimal profitAmount) {
		this.profitAmount = profitAmount;
	}

	public String getProfitSource() {
		return this.profitSource;
	}

	public void setProfitSource(String profitSource) {
		this.profitSource = profitSource;
	}

	public String getProfitType() {
		return this.profitType;
	}

	public void setProfitType(String profitType) {
		this.profitType = profitType;
	}

	public BigDecimal getProfitBase() {
		return this.profitBase;
	}

	public void setProfitBase(BigDecimal profitBase) {
		this.profitBase = profitBase;
	}

	public BigDecimal getProfitAdjustment() {
		return this.profitAdjustment;
	}

	public void setProfitAdjustment(BigDecimal profitAdjustment) {
		this.profitAdjustment = profitAdjustment;
	}

	public QuotationModel getQuotationModel() {
	    return quotationModel;
	}

	public void setQuotationModel(QuotationModel quotationModel) {
	    this.quotationModel = quotationModel;
	}
	
	@Override
        public String toString() {
          return "com.mikealbert.vision.entity.QuotationProfitability[ qpyId=" + qpyId + " ]";
        }

	public BigDecimal getMinLeaseRate() {
		return minLeaseRate;
	}

	public void setMinLeaseRate(BigDecimal minLeaseRate) {
		this.minLeaseRate = minLeaseRate;
	}

	public BigDecimal getIrrApprovedLimit() {
		return irrApprovedLimit;
	}

	public void setIrrApprovedLimit(BigDecimal irrApprovedLimit) {
		this.irrApprovedLimit = irrApprovedLimit;
	}

	public Date getIrrApprovedDate() {
		return irrApprovedDate;
	}

	public void setIrrApprovedDate(Date irrApprovedDate) {
		this.irrApprovedDate = irrApprovedDate;
	}

	public String getIrrApprovedUser() {
		return irrApprovedUser;
	}

	public void setIrrApprovedUser(String irrApprovedUser) {
		this.irrApprovedUser = irrApprovedUser;
	}


}