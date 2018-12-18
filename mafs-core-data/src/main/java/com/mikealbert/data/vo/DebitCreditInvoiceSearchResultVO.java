package com.mikealbert.data.vo;

import java.math.BigDecimal;
import java.util.Date;

import com.mikealbert.util.MALUtilities;

public class DebitCreditInvoiceSearchResultVO {
	
		private Date invoiceDate;
	    private String contractNo;
	    private String docNo;
	    private String sourceCode;
	    private Long categoryId;
	    private String categoryCode;
	    private String analysisCode;
	    private String analysisCodeDesc;
	    private BigDecimal invoiceAmount;
	    private BigDecimal invoiceTax; 
	    private BigDecimal invoiceTotalAmount;
	    private Date lineDocDate;  
	    private Long billingPeriodKey;
	    private Date billingPeriodDate; 
	    @SuppressWarnings("unused")	   
		private String lineDocDateStr;
	    private String lineDescription;
	    private BigDecimal lineAmount;
	    private BigDecimal lineTax; 
	    private BigDecimal lineTotalAmount;	    
	    private String rowKey;
		public Date getInvoiceDate() {
			return invoiceDate;
		}
		public void setInvoiceDate(Date invoiceDate) {
			this.invoiceDate = invoiceDate;
		}	
		public String getContractNo() {
			return contractNo;
		}
		public void setContractNo(String contractNo) {
			this.contractNo = contractNo;
		}
		public String getDocNo() {
			return docNo;
		}
		public void setDocNo(String docNo) {
			this.docNo = docNo;
		}
		public String getSourceCode() {
			return sourceCode;
		}
		public void setSourceCode(String sourceCode) {
			this.sourceCode = sourceCode;
		}
		public Long getCategoryId() {
			return categoryId;
		}
		public void setCategoryId(Long categoryId) {
			this.categoryId = categoryId;
		}
		public String getCategoryCode() {
			return categoryCode;
		}
		public void setCategoryCode(String categoryCode) {
			this.categoryCode = categoryCode;
		}
		public String getAnalysisCode() {
			return analysisCode;
		}
		public void setAnalysisCode(String analysisCode) {
			this.analysisCode = analysisCode;
		}
		public String getAnalysisCodeDesc() {
			return analysisCodeDesc;
		}
		public void setAnalysisCodeDesc(String analysisCodeDesc) {
			this.analysisCodeDesc = analysisCodeDesc;
		}
		public BigDecimal getInvoiceAmount() {
			return invoiceAmount;
		}
		public void setInvoiceAmount(BigDecimal invoiceAmount) {
			this.invoiceAmount = invoiceAmount;
		}
		public BigDecimal getInvoiceTax() {
			return invoiceTax;
		}
		public void setInvoiceTax(BigDecimal invoiceTax) {
			this.invoiceTax = invoiceTax;
		}
		public BigDecimal getInvoiceTotalAmount() {
			return invoiceTotalAmount;
		}
		public void setInvoiceTotalAmount(BigDecimal invoiceTotalAmount) {
			this.invoiceTotalAmount = invoiceTotalAmount;
		}
		public Date getLineDocDate() {
			return lineDocDate;
		}
		public void setLineDocDate(Date lineDocDate) {
			this.lineDocDate = lineDocDate;
		}
		public String getLineDocDateStr() {			
			return  MALUtilities.getNullSafeDatetoString(lineDocDate);
		}		
		public void setLineDocDateStr(String lineDocDateStr) {
			this.lineDocDateStr = lineDocDateStr;
		}
		public String getLineDescription() {
			return lineDescription;
		}
		public void setLineDescription(String lineDescription) {
			this.lineDescription = lineDescription;
		}
		public BigDecimal getLineAmount() {
			return lineAmount;
		}
		public void setLineAmount(BigDecimal lineAmount) {
			this.lineAmount = lineAmount;
		}
		public BigDecimal getLineTax() {
			return lineTax;
		}
		public void setLineTax(BigDecimal lineTax) {
			this.lineTax = lineTax;
		}
		public BigDecimal getLineTotalAmount() {
			return lineTotalAmount;
		}
		public void setLineTotalAmount(BigDecimal lineTotalAmount) {
			this.lineTotalAmount = lineTotalAmount;
		}
		public String getRowKey() {
			return rowKey;
		}
		public void setRowKey(String rowKey) {
			this.rowKey = rowKey;
		}
		
		public Long getBillingPeriodKey() {
			return billingPeriodKey;
		}
		public void setBillingPeriodKey(Long billingPeriodKey) {
			this.billingPeriodKey = billingPeriodKey;
		}
		
		
		public Date getBillingPeriodDate() {
			return billingPeriodDate;
		}
		public void setBillingPeriodDate(Date billingPeriodDate) {
			this.billingPeriodDate = billingPeriodDate;
		}
		
		//RE-1066 - Overridden in order to remove duplicate lov records for display purposes
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((analysisCode == null) ? 0 : analysisCode.hashCode());
			result = prime
					* result
					+ ((billingPeriodDate == null) ? 0 : billingPeriodDate
							.hashCode());
			result = prime * result
					+ ((categoryCode == null) ? 0 : categoryCode.hashCode());
			result = prime * result
					+ ((categoryId == null) ? 0 : categoryId.hashCode());
			result = prime * result + ((docNo == null) ? 0 : docNo.hashCode());
			result = prime * result
					+ ((invoiceAmount == null) ? 0 : invoiceAmount.hashCode());
			result = prime * result
					+ ((invoiceDate == null) ? 0 : invoiceDate.hashCode());
			result = prime * result
					+ ((invoiceTax == null) ? 0 : invoiceTax.hashCode());
			result = prime
					* result
					+ ((invoiceTotalAmount == null) ? 0 : invoiceTotalAmount
							.hashCode());
			result = prime * result
					+ ((lineAmount == null) ? 0 : lineAmount.hashCode());
			result = prime
					* result
					+ ((lineDescription == null) ? 0 : lineDescription
							.hashCode());
			result = prime * result
					+ ((lineDocDate == null) ? 0 : lineDocDate.hashCode());
			result = prime * result
					+ ((lineTax == null) ? 0 : lineTax.hashCode());
			result = prime
					* result
					+ ((lineTotalAmount == null) ? 0 : lineTotalAmount
							.hashCode());
			result = prime * result
					+ ((sourceCode == null) ? 0 : sourceCode.hashCode());
			return result;
		}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DebitCreditInvoiceSearchResultVO other = (DebitCreditInvoiceSearchResultVO) obj;
		if (analysisCode == null) {
			if (other.analysisCode != null)
				return false;
		} else if (!analysisCode.equals(other.analysisCode))
			return false;
		if (billingPeriodDate == null) {
			if (other.billingPeriodDate != null)
				return false;
		} else if (!billingPeriodDate.equals(other.billingPeriodDate))
			return false;
		if (categoryCode == null) {
			if (other.categoryCode != null)
				return false;
		} else if (!categoryCode.equals(other.categoryCode))
			return false;
		if (categoryId == null) {
			if (other.categoryId != null)
				return false;
		} else if (!categoryId.equals(other.categoryId))
			return false;
		if (docNo == null) {
			if (other.docNo != null)
				return false;
		} else if (!docNo.equals(other.docNo))
			return false;
		if (invoiceAmount == null) {
			if (other.invoiceAmount != null)
				return false;
		} else if (!invoiceAmount.equals(other.invoiceAmount))
			return false;
		if (invoiceDate == null) {
			if (other.invoiceDate != null)
				return false;
		} else if (!invoiceDate.equals(other.invoiceDate))
			return false;
		if (invoiceTax == null) {
			if (other.invoiceTax != null)
				return false;
		} else if (!invoiceTax.equals(other.invoiceTax))
			return false;
		if (invoiceTotalAmount == null) {
			if (other.invoiceTotalAmount != null)
				return false;
		} else if (!invoiceTotalAmount.equals(other.invoiceTotalAmount))
			return false;
		if (lineAmount == null) {
			if (other.lineAmount != null)
				return false;
		} else if (!lineAmount.equals(other.lineAmount))
			return false;
		if (lineDescription == null) {
			if (other.lineDescription != null)
				return false;
		} else if (!lineDescription.equals(other.lineDescription))
			return false;
		if (lineDocDate == null) {
			if (other.lineDocDate != null)
				return false;
		} else if (!lineDocDate.equals(other.lineDocDate))
			return false;
		if (lineTax == null) {
			if (other.lineTax != null)
				return false;
		} else if (!lineTax.equals(other.lineTax))
			return false;
		if (lineTotalAmount == null) {
			if (other.lineTotalAmount != null)
				return false;
		} else if (!lineTotalAmount.equals(other.lineTotalAmount))
			return false;
		if (sourceCode == null) {
			if (other.sourceCode != null)
				return false;
		} else if (!sourceCode.equals(other.sourceCode))
			return false;
		return true;
		}
		
		@Override
		public String toString() {
			return "DebitCreditInvoiceSearchResultVO [invoiceDate="
					+ invoiceDate + ", billingPeriodDate=" + billingPeriodDate + ", docNo="
					+ docNo + ", sourceCode=" + sourceCode + ", analysisCode="
					+ analysisCode + ", lineDocDate=" + lineDocDate
					+ ", lineTotalAmount=" + lineTotalAmount + "]";
		}
	
	
		
		
}
