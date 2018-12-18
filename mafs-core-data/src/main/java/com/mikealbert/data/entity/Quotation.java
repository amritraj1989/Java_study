package com.mikealbert.data.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the QUOTATIONS database table.
 * @author Singh
 */
@Entity
@Table(name="QUOTATIONS")
public class Quotation extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="QUO_ID", unique=true, nullable=false, precision=12)
	private long quoId;

	@Column(name="BENCHMARK_YN", length=1)
	private String benchmarkYn;

	@Column(name="CAPITAL_CONTRIBUTION", precision=30, scale=5)
	private BigDecimal capitalContribution;

	@Column(name="CC_CODE", length=25)
	private String ccCode;

	@Column(name="CURRENCY_CODE", nullable=false, length=10)
	private String currencyCode;

    @Temporal( TemporalType.DATE)
	@Column(name="DESIRED_START_DT")
	private Date desiredStartDt;

	@Column(name="DRIVER_GRADE_GROUP", length=10)
	private String driverGradeGroup;

	@Column(name="DRV_DRV_ID")
	private Long drvDrvId;

	@Column(name="HPD_HPD_ID")
	private Long hpdHpdId;

	@Column(name="MUL_QUOTE", length=1)
	private String mulQuote;

	@Column(name="PAYMENT_ID", precision=12)
	private Long paymentId;

	@Column(name="PERIOD_ID", precision=12)
	private BigDecimal periodId;

    @Temporal( TemporalType.DATE)
	@Column(name="QUOTE_DATE", nullable=false)
	private Date quoteDate;

	@Column(name="QUOTE_DIST_UOM", length=1)
	private String quoteDistUom;

    @Temporal( TemporalType.DATE)
	@Column(name="QUOTE_EXP_DATE", nullable=false)
	private Date quoteExpDate;

	@Column(name="SHD_SHD_ID", precision=12)
	private BigDecimal shdShdId;

	@Column(name="UOM_CODE", length=10)
	private String uomCode;

	@Column(nullable=false, length=30)
	private String username;

	@Column(name="VEH_POL", length=1)
	private String vehPol;
		
	@Column(name="QRO_QRO_ID", nullable=false)
	private Long qroQroId;
	
	@OneToMany(mappedBy="quotation", fetch=FetchType.LAZY)
	private List<QuotationModel> quotationModels;
	
	@OneToMany(mappedBy="quotation", fetch=FetchType.LAZY)
	private List<MulQuoteEle> mulQuoteElems;
	   
    @JoinColumns({
        @JoinColumn(name = "C_ID", referencedColumnName = "C_ID"),
        @JoinColumn(name = "ACCOUNT_TYPE", referencedColumnName = "ACCOUNT_TYPE"),
        @JoinColumn(name = "ACCOUNT_CODE", referencedColumnName = "ACCOUNT_CODE")})
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private ExternalAccount externalAccount;
	
    @ManyToOne
	@JoinColumn(name="QPR_QPR_ID", nullable=false)
	private QuotationProfile quotationProfile;
    
    public Quotation() {
    }

	public long getQuoId() {
		return this.quoId;
	}

	public void setQuoId(long quoId) {
		this.quoId = quoId;
	}	

	public String getBenchmarkYn() {
		return this.benchmarkYn;
	}

	public void setBenchmarkYn(String benchmarkYn) {
		this.benchmarkYn = benchmarkYn;
	}	

	public BigDecimal getCapitalContribution() {
		return this.capitalContribution;
	}

	public void setCapitalContribution(BigDecimal capitalContribution) {
		this.capitalContribution = capitalContribution;
	}

	public String getCcCode() {
		return this.ccCode;
	}

	public void setCcCode(String ccCode) {
		this.ccCode = ccCode;
	}

	public String getCurrencyCode() {
		return this.currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public Date getDesiredStartDt() {
		return this.desiredStartDt;
	}

	public void setDesiredStartDt(Date desiredStartDt) {
		this.desiredStartDt = desiredStartDt;
	}

	public String getDriverGradeGroup() {
		return this.driverGradeGroup;
	}

	public void setDriverGradeGroup(String driverGradeGroup) {
		this.driverGradeGroup = driverGradeGroup;
	}

	public Long getDrvDrvId() {
		return this.drvDrvId;
	}

	public void setDrvDrvId(Long drvDrvId) {
		this.drvDrvId = drvDrvId;
	}

	public Long getHpdHpdId() {
		return this.hpdHpdId;
	}

	public void setHpdHpdId(Long hpdHpdId) {
		this.hpdHpdId = hpdHpdId;
	}

	public String getMulQuote() {
		return this.mulQuote;
	}

	public void setMulQuote(String mulQuote) {
		this.mulQuote = mulQuote;
	}

	public Long getPaymentId() {
		return this.paymentId;
	}

	public void setPaymentId(Long paymentId) {
		this.paymentId = paymentId;
	}

	public BigDecimal getPeriodId() {
		return this.periodId;
	}

	public void setPeriodId(BigDecimal periodId) {
		this.periodId = periodId;
	}

	public Date getQuoteDate() {
		return this.quoteDate;
	}

	public void setQuoteDate(Date quoteDate) {
		this.quoteDate = quoteDate;
	}

	public String getQuoteDistUom() {
		return this.quoteDistUom;
	}

	public void setQuoteDistUom(String quoteDistUom) {
		this.quoteDistUom = quoteDistUom;
	}

	public Date getQuoteExpDate() {
		return this.quoteExpDate;
	}

	public void setQuoteExpDate(Date quoteExpDate) {
		this.quoteExpDate = quoteExpDate;
	}

	public BigDecimal getShdShdId() {
		return this.shdShdId;
	}

	public void setShdShdId(BigDecimal shdShdId) {
		this.shdShdId = shdShdId;
	}

	public String getUomCode() {
		return this.uomCode;
	}

	public void setUomCode(String uomCode) {
		this.uomCode = uomCode;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getVehPol() {
		return this.vehPol;
	}

	public void setVehPol(String vehPol) {
		this.vehPol = vehPol;
	}

	public List<MulQuoteEle> getMulQuoteElems() {
		return mulQuoteElems;
	}

	public void setMulQuoteElems(List<MulQuoteEle> mulQuoteElems) {
		this.mulQuoteElems = mulQuoteElems;
	}

	public List<QuotationModel> getQuotationModels() {
		return this.quotationModels;
	}

	public void setQuotationModels(List<QuotationModel> quotationModels) {
		this.quotationModels = quotationModels;
	}


	public Long getQroQroId() {
		return qroQroId;
	}

	public void setQroQroId(Long qroQroId) {
		this.qroQroId = qroQroId;
	}

	public ExternalAccount getExternalAccount() {
		return externalAccount;
	}

	public void setExternalAccount(ExternalAccount externalAccount) {
		this.externalAccount = externalAccount;
	}
	
	public QuotationProfile getQuotationProfile() {
		return this.quotationProfile;
	}

	public void setQuotationProfile(QuotationProfile quotationProfile) {
		this.quotationProfile = quotationProfile;
	}
	
	@Override
    public String toString() {
        return "com.mikealbert.vision.entity.Quotation[ quoId=" + quoId + " ]";
    }
	
}