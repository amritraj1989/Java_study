package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * The persistent class for the SUPPLIERS database table.
 * @author Singh
 */
@Entity
@Table(name = "SUPPLIERS")
public class Supplier extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "SUP_ID")
	private long supId;// Add SequenceGenerator and other field mapping as needed

	@Column(name = "SUPPLIER_CODE")
	private String supplierCode;

	@Column(name = "SUPPLIER_NAME")
	private String supplierName;

	@Column(name = "SUPPLIER_CATEGORY")
	private String supplierCategory;

	@Column(name = "EA_C_ID")
	private Long eaCId;

	@Column(name = "EA_ACCOUNT_TYPE")
	private String eaAccountType;

	@Column(name = "EA_ACCOUNT_CODE")
	private String eaAccountCode;

	@Column(name = "SSTC_SERVICE_TYPE_CODE")
	private String sstcServiceTypeCode;

	@Column(name = "DETAILS_ENTERED_BY")
	private String detailsEnteredBy;

	@Temporal(TemporalType.DATE)
	@Column(name = "DETAILS_LAST_UPDATED_DATE")
	private Date detailsLastUpdatedDate;

	@Column(name = "INACTIVE_IND")
	private String inactiveInd;

	@Column(name = "INS_APPROVED_IND")
	private String insApprovedInd;

	@Column(name = "TELEPHONE_NUMBER")
	private String telephoneNumber;

	@Column(name = "EMAIL_ADDRESS")
	private String emailAddress;

	@Column(name = "SUP_SUP_ID")
	private String supSupId;
	
	@OneToMany(mappedBy = "supplier", fetch = FetchType.LAZY)
	private List<SupplierWorkshops> supplierWorkShops;

	@Transient
	private boolean orderingWorkShopCapability; 
	
	public long getSupId() {
		return supId;
	}

	public void setSupId(long supId) {
		this.supId = supId;
	}

	public String getSupplierCode() {
		return supplierCode;
	}

	public void setSupplierCode(String supplierCode) {
		this.supplierCode = supplierCode;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public String getSupplierCategory() {
		return supplierCategory;
	}

	public void setSupplierCategory(String supplierCategory) {
		this.supplierCategory = supplierCategory;
	}

	public Long getEaCId() {
		return eaCId;
	}

	public void setEaCId(Long eaCId) {
		this.eaCId = eaCId;
	}

	public String getEaAccountType() {
		return eaAccountType;
	}

	public void setEaAccountType(String eaAccountType) {
		this.eaAccountType = eaAccountType;
	}

	public String getEaAccountCode() {
		return eaAccountCode;
	}

	public void setEaAccountCode(String eaAccountCode) {
		this.eaAccountCode = eaAccountCode;
	}

	public String getSstcServiceTypeCode() {
		return sstcServiceTypeCode;
	}

	public void setSstcServiceTypeCode(String sstcServiceTypeCode) {
		this.sstcServiceTypeCode = sstcServiceTypeCode;
	}

	public String getDetailsEnteredBy() {
		return detailsEnteredBy;
	}

	public void setDetailsEnteredBy(String detailsEnteredBy) {
		this.detailsEnteredBy = detailsEnteredBy;
	}

	public Date getDetailsLastUpdatedDate() {
		return detailsLastUpdatedDate;
	}

	public void setDetailsLastUpdatedDate(Date detailsLastUpdatedDate) {
		this.detailsLastUpdatedDate = detailsLastUpdatedDate;
	}

	public String getInactiveInd() {
		return inactiveInd;
	}

	public void setInactiveInd(String inactiveInd) {
		this.inactiveInd = inactiveInd;
	}

	public String getInsApprovedInd() {
		return insApprovedInd;
	}

	public void setInsApprovedInd(String insApprovedInd) {
		this.insApprovedInd = insApprovedInd;
	}

	public String getTelephoneNumber() {
		return telephoneNumber;
	}

	public void setTelephoneNumber(String telephoneNumber) {
		this.telephoneNumber = telephoneNumber;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getSupSupId() {
		return supSupId;
	}

	public void setSupSupId(String supSupId) {
		this.supSupId = supSupId;
	}

	public List<SupplierWorkshops> getSupplierWorkShops() {
		return supplierWorkShops;
	}

	public void setSupplierWorkShops(List<SupplierWorkshops> supplierWorkShops) {
		this.supplierWorkShops = supplierWorkShops;
	}

	public boolean isOrderingWorkShopCapability() {
		return orderingWorkShopCapability;
	}

	public void setOrderingWorkShopCapability(boolean orderingWorkShopCapability) {
		this.orderingWorkShopCapability = orderingWorkShopCapability;
	}
	

	@Override
	public String toString() {
		return "Supplier [supId=" + supId + ", supplierCode=" + supplierCode
				+ ", supplierName=" + supplierName + ", supplierCategory="
				+ supplierCategory + ", eaAccountCode=" + eaAccountCode + "]";
	}

	
}