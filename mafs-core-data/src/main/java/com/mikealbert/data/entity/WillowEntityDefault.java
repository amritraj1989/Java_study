package com.mikealbert.data.entity;
import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the WILLOW_ENTITY_DEFAULTS database table.
 * 
 */
@Entity
@Table(name="WILLOW_ENTITY_DEFAULTS")
public class WillowEntityDefault implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="C_ID")
	private long cId;

	@Column(name="AUTO_ISS_ALLOC_IND")
	private String autoIssAllocInd;

	@Column(name="BUDGET_CHECKING_IND")
	private String budgetCheckingInd;

	@Column(name="CAL_C_ID")
	private java.math.BigDecimal calCId;

	@Column(name="CURRENT_TP_SEQ_AP")
	private java.math.BigDecimal currentTpSeqAp;

	@Column(name="CURRENT_TP_SEQ_AR")
	private java.math.BigDecimal currentTpSeqAr;

	@Column(name="CURRENT_TP_SEQ_CB")
	private java.math.BigDecimal currentTpSeqCb;

	@Column(name="CURRENT_TP_SEQ_CD")
	private java.math.BigDecimal currentTpSeqCd;

	@Column(name="CURRENT_TP_SEQ_GL")
	private java.math.BigDecimal currentTpSeqGl;

	@Column(name="CURRENT_TP_SEQ_SO")
	private java.math.BigDecimal currentTpSeqSo;

	@Column(name="DEFAULT_COST_METHOD")
	private String defaultCostMethod;

	@Column(name="DEFAULT_EXCHANGE_RATE_TYPE")
	private String defaultExchangeRateType;

	@Column(name="FLEET_ENTITY")
	private String fleetEntity;

	@Column(name="FREIGHT_TAX_CODE")
	private java.math.BigDecimal freightTaxCode;

	@Column(name="GL_RESTRICT")
	private String glRestrict;

	@Column(name="IM_TAX_CODE")
	private java.math.BigDecimal imTaxCode;

	@Column(name="ONLINE_GL_IND")
	private String onlineGlInd;

	@Column(name="POSTING_CALENDAR")
	private String postingCalendar;

	@Column(name="PRINT_ADD_IND")
	private String printAddInd;

	@Column(name="PRODUCT_COPY_IND")
	private String productCopyInd;

	@Column(name="SELLING_CALC_METHOD")
	private String sellingCalcMethod;

	@Column(name="SO_PRICE_VAT_IND")
	private String soPriceVatInd;

	@Column(name="SYSTEM_CATEGORY_AP")
	private java.math.BigDecimal systemCategoryAp;

	@Column(name="SYSTEM_CATEGORY_AR")
	private java.math.BigDecimal systemCategoryAr;

	@Column(name="SYSTEM_CATEGORY_GL")
	private java.math.BigDecimal systemCategoryGl;

	@Column(name="SYSTEM_CATEGORY_IM")
	private java.math.BigDecimal systemCategoryIm;

	@Column(name="WAREHOUSE_IND")
	private String warehouseInd;

	//bi-directional many-to-one association to CurrencyUnit
    @ManyToOne
	@JoinColumn(name="CURRENCY_CODE")
	private CurrencyUnit currencyUnit;

    public WillowEntityDefault() {
    }

	public long getCId() {
		return this.cId;
	}

	public void setCId(long cId) {
		this.cId = cId;
	}

	public String getAutoIssAllocInd() {
		return this.autoIssAllocInd;
	}

	public void setAutoIssAllocInd(String autoIssAllocInd) {
		this.autoIssAllocInd = autoIssAllocInd;
	}

	public String getBudgetCheckingInd() {
		return this.budgetCheckingInd;
	}

	public void setBudgetCheckingInd(String budgetCheckingInd) {
		this.budgetCheckingInd = budgetCheckingInd;
	}

	public java.math.BigDecimal getCalCId() {
		return this.calCId;
	}

	public void setCalCId(java.math.BigDecimal calCId) {
		this.calCId = calCId;
	}

	public java.math.BigDecimal getCurrentTpSeqAp() {
		return this.currentTpSeqAp;
	}

	public void setCurrentTpSeqAp(java.math.BigDecimal currentTpSeqAp) {
		this.currentTpSeqAp = currentTpSeqAp;
	}

	public java.math.BigDecimal getCurrentTpSeqAr() {
		return this.currentTpSeqAr;
	}

	public void setCurrentTpSeqAr(java.math.BigDecimal currentTpSeqAr) {
		this.currentTpSeqAr = currentTpSeqAr;
	}

	public java.math.BigDecimal getCurrentTpSeqCb() {
		return this.currentTpSeqCb;
	}

	public void setCurrentTpSeqCb(java.math.BigDecimal currentTpSeqCb) {
		this.currentTpSeqCb = currentTpSeqCb;
	}

	public java.math.BigDecimal getCurrentTpSeqCd() {
		return this.currentTpSeqCd;
	}

	public void setCurrentTpSeqCd(java.math.BigDecimal currentTpSeqCd) {
		this.currentTpSeqCd = currentTpSeqCd;
	}

	public java.math.BigDecimal getCurrentTpSeqGl() {
		return this.currentTpSeqGl;
	}

	public void setCurrentTpSeqGl(java.math.BigDecimal currentTpSeqGl) {
		this.currentTpSeqGl = currentTpSeqGl;
	}

	public java.math.BigDecimal getCurrentTpSeqSo() {
		return this.currentTpSeqSo;
	}

	public void setCurrentTpSeqSo(java.math.BigDecimal currentTpSeqSo) {
		this.currentTpSeqSo = currentTpSeqSo;
	}

	public String getDefaultCostMethod() {
		return this.defaultCostMethod;
	}

	public void setDefaultCostMethod(String defaultCostMethod) {
		this.defaultCostMethod = defaultCostMethod;
	}

	public String getDefaultExchangeRateType() {
		return this.defaultExchangeRateType;
	}

	public void setDefaultExchangeRateType(String defaultExchangeRateType) {
		this.defaultExchangeRateType = defaultExchangeRateType;
	}

	public String getFleetEntity() {
		return this.fleetEntity;
	}

	public void setFleetEntity(String fleetEntity) {
		this.fleetEntity = fleetEntity;
	}

	public java.math.BigDecimal getFreightTaxCode() {
		return this.freightTaxCode;
	}

	public void setFreightTaxCode(java.math.BigDecimal freightTaxCode) {
		this.freightTaxCode = freightTaxCode;
	}

	public String getGlRestrict() {
		return this.glRestrict;
	}

	public void setGlRestrict(String glRestrict) {
		this.glRestrict = glRestrict;
	}

	public java.math.BigDecimal getImTaxCode() {
		return this.imTaxCode;
	}

	public void setImTaxCode(java.math.BigDecimal imTaxCode) {
		this.imTaxCode = imTaxCode;
	}

	public String getOnlineGlInd() {
		return this.onlineGlInd;
	}

	public void setOnlineGlInd(String onlineGlInd) {
		this.onlineGlInd = onlineGlInd;
	}

	public String getPostingCalendar() {
		return this.postingCalendar;
	}

	public void setPostingCalendar(String postingCalendar) {
		this.postingCalendar = postingCalendar;
	}

	public String getPrintAddInd() {
		return this.printAddInd;
	}

	public void setPrintAddInd(String printAddInd) {
		this.printAddInd = printAddInd;
	}

	public String getProductCopyInd() {
		return this.productCopyInd;
	}

	public void setProductCopyInd(String productCopyInd) {
		this.productCopyInd = productCopyInd;
	}

	public String getSellingCalcMethod() {
		return this.sellingCalcMethod;
	}

	public void setSellingCalcMethod(String sellingCalcMethod) {
		this.sellingCalcMethod = sellingCalcMethod;
	}

	public String getSoPriceVatInd() {
		return this.soPriceVatInd;
	}

	public void setSoPriceVatInd(String soPriceVatInd) {
		this.soPriceVatInd = soPriceVatInd;
	}

	public java.math.BigDecimal getSystemCategoryAp() {
		return this.systemCategoryAp;
	}

	public void setSystemCategoryAp(java.math.BigDecimal systemCategoryAp) {
		this.systemCategoryAp = systemCategoryAp;
	}

	public java.math.BigDecimal getSystemCategoryAr() {
		return this.systemCategoryAr;
	}

	public void setSystemCategoryAr(java.math.BigDecimal systemCategoryAr) {
		this.systemCategoryAr = systemCategoryAr;
	}

	public java.math.BigDecimal getSystemCategoryGl() {
		return this.systemCategoryGl;
	}

	public void setSystemCategoryGl(java.math.BigDecimal systemCategoryGl) {
		this.systemCategoryGl = systemCategoryGl;
	}

	public java.math.BigDecimal getSystemCategoryIm() {
		return this.systemCategoryIm;
	}

	public void setSystemCategoryIm(java.math.BigDecimal systemCategoryIm) {
		this.systemCategoryIm = systemCategoryIm;
	}

	public String getWarehouseInd() {
		return this.warehouseInd;
	}

	public void setWarehouseInd(String warehouseInd) {
		this.warehouseInd = warehouseInd;
	}

	public CurrencyUnit getCurrencyUnit() {
		return this.currencyUnit;
	}

	public void setCurrencyUnit(CurrencyUnit currencyUnit) {
		this.currencyUnit = currencyUnit;
	}
	
}