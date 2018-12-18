package com.mikealbert.data.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


public  class ProcessQueueResultVO implements Serializable {
		
		private static final long serialVersionUID = 1L;

		private String unitNumber;
		private String vin;
		private String unitDescription;
		private String vendorCode;
		private String vendorName;
		private String accountCode;
		private String accountName;
		private String managerReviewStatus;
		private Date lastUpdate;
		private BigDecimal poAmount;
		private String poNumber;
		
		private Long fmsId;
		private Long qmdId;
		private String id;
		private	String poSourceCode;
		private String subAccountCode; //added for Bug 16467
		
		
		
		public ProcessQueueResultVO(){}

		// added this method to provide access to the real ID value that is held in this VO
		//     see the getter for id below to understand why
		public String getDocId() {
			return id;
		}
		
		
		public String getUnitNumber() {
			return unitNumber;
		}

		public void setUnitNumber(String unitNumber) {
			this.unitNumber = unitNumber;
		}
		
		public String getVin() {
			return vin;
		}
		
		public void setVin(String vin) {
			this.vin = vin;
		}
		
		public Long getFmsId() {
			return fmsId;
		}

		public void setFmsId(Long fmsId) {
			this.fmsId = fmsId;
		}

		public Long getQmdId() {
			return qmdId;
		}

		public void setQmdId(Long qmdId) {
			this.qmdId = qmdId;
		}


		public String getUnitDescription() {
			return unitDescription;
		}


		public void setUnitDescription(String unitDescription) {
			this.unitDescription = unitDescription;
		}


		public String getVendorCode() {
			return vendorCode;
		}


		public void setVendorCode(String vendorCode) {
			this.vendorCode = vendorCode;
		}


		public String getVendorName() {
			return vendorName;
		}


		public void setVendorName(String vendorName) {
			this.vendorName = vendorName;
		}

		public String getManagerReviewStatus() {
			return managerReviewStatus;
		}


		public void setManagerReviewStatus(String managerReviewStatus) {
			this.managerReviewStatus = managerReviewStatus;
		}


		public Date getLastUpdate() {
			return lastUpdate;
		}


		public void setLastUpdate(Date lastUpdate) {
			this.lastUpdate = lastUpdate;
		}


		public BigDecimal getPoAmount() {
			return poAmount;
		}


		public void setPoAmount(BigDecimal poAmount) {
			this.poAmount = poAmount;
		}


		public String getId() {
			return unitNumber+vendorCode+poNumber;
		}


		public void setId(String id) {
			this.id = id;
		}


		public String getPoNumber() {
			return poNumber;
		}


		public void setPoNumber(String poNumber) {
			this.poNumber = poNumber;
		}


		public String getAccountCode() {
			return accountCode;
		}


		public void setAccountCode(String accountCode) {
			this.accountCode = accountCode;
		}


		public String getAccountName() {
			return accountName;
		}


		public void setAccountName(String accountName) {
			this.accountName = accountName;
		}


		public String getPoSourceCode() {
			return poSourceCode;
		}


		public void setPoSourceCode(String poSourceCode) {
			this.poSourceCode = poSourceCode;
		}

		public String getSubAccountCode() {
			return subAccountCode; //added for Bug 16467
		}


		public void setSubAccountCode(String subAccountCode) {
			this.subAccountCode = subAccountCode; //added for Bug 16467
		}
		
		
}