package com.mikealbert.data.entity;
import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;


/**
 * The persistent class for the VRB_DISCOUNTS database table.
 * 
 */
@Entity
@Table(name="VRB_DISCOUNTS")
public class VrbDiscount extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="VRBD_ID")
	private long vrbdId;

	@Column(name="AGREEMENT_NO")
	private String agreementNo;

	@Column(name="ASSC_ASSC_ID")
	private BigDecimal asscAsscId;

	@Column(name="CAT_CODE")
	private String catCode;

	@Column(name="DELIV_DISCOUNT")
	private BigDecimal delivDiscount;

	@Column(name="DISC_APPLIES")
	private String discApplies;

	@Column(name="DISC_APPLIES_CODE")
	private String discAppliesCode;

	@Column(name="DISCOUNT_PERC")
	private BigDecimal discountPerc;

	@Column(name="DISCOUNT_VALUE")
	private BigDecimal discountValue;

	@Column(name="EA_ACCOUNT_CODE")
	private String eaAccountCode;

	@Column(name="EA_ACCOUNT_TYPE")
	private String eaAccountType;

	@Column(name="EA_C_ID")
	private BigDecimal eaCId;

    @Temporal( TemporalType.DATE)
	@Column(name="END_DATE")
	private Date endDate;

	@Column(name="ERG_ERG_ID")
	private BigDecimal ergErgId;

	@Column(name="FREE_DELIVERY")
	private String freeDelivery;

	@Column(name="FTP_FTP_ID")
	private BigDecimal ftpFtpId;

	@Column(name="INCLUDE_IND")
	private String includeInd;

	@Column(name="MAK_MAK_ID")
	private BigDecimal makMakId;

	@Column(name="MDL_MDL_ID")
	private Long mdlMdlId;

	@Column(name="MRG_MRG_ID")
	private BigDecimal mrgMrgId;

	@Column(name="MTP_MTP_ID")
	private BigDecimal mtpMtpId;

	@Column(name="OFFERED_DISC_PERC")
	private BigDecimal offeredDiscPerc;

	@Column(name="OFFERED_DISC_VALUE")
	private BigDecimal offeredDiscValue;

	@Column(name="PERIOD_OF_HIRE")
	private String periodOfHire;

	@Column(name="SPECIAL_EDITION_IND")
	private String specialEditionInd;

    @Temporal( TemporalType.DATE)
	@Column(name="START_DATE")
	private Date startDate;

	@Column(name="VRBD_DISC_SOURCE")
	private String vrbdDiscSource;

	//bi-directional many-to-one association to VrbDiscountTypeCode
    @ManyToOne
	@JoinColumn(name="VRB_TYPE")
	private VrbDiscountTypeCode vrbDiscountTypeCode;

	//bi-directional many-to-one association to VrbReclaimMethod
    @ManyToOne
	@JoinColumn(name="VRBD_RECLAIM_METHOD")
	private VrbReclaimMethod vrbReclaimMethod;

	//bi-directional many-to-one association to VrbProduct
	@OneToMany(mappedBy="vrbDiscount")
	private Set<VrbProduct> vrbProducts;
	
    @ManyToOne
	@JoinColumn(name="FTP_FTP_ID", insertable=false, updatable=false )
	private FuelType fuelType;	

    public VrbDiscount() {
    }

	public long getVrbdId() {
		return this.vrbdId;
	}

	public void setVrbdId(long vrbdId) {
		this.vrbdId = vrbdId;
	}

	public String getAgreementNo() {
		return this.agreementNo;
	}

	public void setAgreementNo(String agreementNo) {
		this.agreementNo = agreementNo;
	}

	public BigDecimal getAsscAsscId() {
		return this.asscAsscId;
	}

	public void setAsscAsscId(BigDecimal asscAsscId) {
		this.asscAsscId = asscAsscId;
	}

	public String getCatCode() {
		return this.catCode;
	}

	public void setCatCode(String catCode) {
		this.catCode = catCode;
	}

	public BigDecimal getDelivDiscount() {
		return this.delivDiscount;
	}

	public void setDelivDiscount(BigDecimal delivDiscount) {
		this.delivDiscount = delivDiscount;
	}

	public String getDiscApplies() {
		return this.discApplies;
	}

	public void setDiscApplies(String discApplies) {
		this.discApplies = discApplies;
	}

	public String getDiscAppliesCode() {
		return this.discAppliesCode;
	}

	public void setDiscAppliesCode(String discAppliesCode) {
		this.discAppliesCode = discAppliesCode;
	}

	public BigDecimal getDiscountPerc() {
		return this.discountPerc;
	}

	public void setDiscountPerc(BigDecimal discountPerc) {
		this.discountPerc = discountPerc;
	}

	public BigDecimal getDiscountValue() {
		return this.discountValue;
	}

	public void setDiscountValue(BigDecimal discountValue) {
		this.discountValue = discountValue;
	}

	public String getEaAccountCode() {
		return this.eaAccountCode;
	}

	public void setEaAccountCode(String eaAccountCode) {
		this.eaAccountCode = eaAccountCode;
	}

	public String getEaAccountType() {
		return this.eaAccountType;
	}

	public void setEaAccountType(String eaAccountType) {
		this.eaAccountType = eaAccountType;
	}

	public BigDecimal getEaCId() {
		return this.eaCId;
	}

	public void setEaCId(BigDecimal eaCId) {
		this.eaCId = eaCId;
	}

	public Date getEndDate() {
		return this.endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public BigDecimal getErgErgId() {
		return this.ergErgId;
	}

	public void setErgErgId(BigDecimal ergErgId) {
		this.ergErgId = ergErgId;
	}

	public String getFreeDelivery() {
		return this.freeDelivery;
	}

	public void setFreeDelivery(String freeDelivery) {
		this.freeDelivery = freeDelivery;
	}

	public BigDecimal getFtpFtpId() {
		return this.ftpFtpId;
	}

	public void setFtpFtpId(BigDecimal ftpFtpId) {
		this.ftpFtpId = ftpFtpId;
	}

	public String getIncludeInd() {
		return this.includeInd;
	}

	public void setIncludeInd(String includeInd) {
		this.includeInd = includeInd;
	}

	public BigDecimal getMakMakId() {
		return this.makMakId;
	}

	public void setMakMakId(BigDecimal makMakId) {
		this.makMakId = makMakId;
	}

	public Long getMdlMdlId() {
		return this.mdlMdlId;
	}

	public void setMdlMdlId(Long mdlMdlId) {
		this.mdlMdlId = mdlMdlId;
	}

	public BigDecimal getMrgMrgId() {
		return this.mrgMrgId;
	}

	public void setMrgMrgId(BigDecimal mrgMrgId) {
		this.mrgMrgId = mrgMrgId;
	}

	public BigDecimal getMtpMtpId() {
		return this.mtpMtpId;
	}

	public void setMtpMtpId(BigDecimal mtpMtpId) {
		this.mtpMtpId = mtpMtpId;
	}

	public BigDecimal getOfferedDiscPerc() {
		return this.offeredDiscPerc;
	}

	public void setOfferedDiscPerc(BigDecimal offeredDiscPerc) {
		this.offeredDiscPerc = offeredDiscPerc;
	}

	public BigDecimal getOfferedDiscValue() {
		return this.offeredDiscValue;
	}

	public void setOfferedDiscValue(BigDecimal offeredDiscValue) {
		this.offeredDiscValue = offeredDiscValue;
	}

	public String getPeriodOfHire() {
		return this.periodOfHire;
	}

	public void setPeriodOfHire(String periodOfHire) {
		this.periodOfHire = periodOfHire;
	}

	public String getSpecialEditionInd() {
		return this.specialEditionInd;
	}

	public void setSpecialEditionInd(String specialEditionInd) {
		this.specialEditionInd = specialEditionInd;
	}

	public Date getStartDate() {
		return this.startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getVrbdDiscSource() {
		return this.vrbdDiscSource;
	}

	public void setVrbdDiscSource(String vrbdDiscSource) {
		this.vrbdDiscSource = vrbdDiscSource;
	}

	public VrbDiscountTypeCode getVrbDiscountTypeCode() {
		return this.vrbDiscountTypeCode;
	}

	public void setVrbDiscountTypeCode(VrbDiscountTypeCode vrbDiscountTypeCode) {
		this.vrbDiscountTypeCode = vrbDiscountTypeCode;
	}
	
	public VrbReclaimMethod getVrbReclaimMethod() {
		return this.vrbReclaimMethod;
	}

	public void setVrbReclaimMethod(VrbReclaimMethod vrbReclaimMethod) {
		this.vrbReclaimMethod = vrbReclaimMethod;
	}
	
	public Set<VrbProduct> getVrbProducts() {
		return this.vrbProducts;
	}

	public void setVrbProducts(Set<VrbProduct> vrbProducts) {
		this.vrbProducts = vrbProducts;
	}

	public FuelType getFuelType() {
		return fuelType;
	}

	public void setFuelType(FuelType fuelType) {
		this.fuelType = fuelType;
	}
	
}