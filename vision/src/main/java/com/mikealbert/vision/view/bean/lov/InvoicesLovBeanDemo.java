package com.mikealbert.vision.view.bean.lov;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.vision.view.bean.BaseBean;

@Component("invoicesLovBeanDemo")
@Scope("view")
public class InvoicesLovBeanDemo extends BaseBean  {

	MalLogger logger = MalLoggerFactory.getLogger(this.getClass());

	private static final long serialVersionUID = 1L;
	
	private List<InvoiceList> invoicesList;
	
	@PostConstruct
    public void init() {
        try {
        	loadData();
        } catch (Exception ex) {
        	logger.error(ex);
        	super.addErrorMessage("generic.error", ex.getMessage());
        }
    } 	
	
	public void loadData() {
		try {	
			invoicesList = new ArrayList<InvoiceList>();
			invoicesList.add(new InvoiceList("10/02/2016","INV00547084","23810612","366.45","0.00","366.45","FLBILLING","10/31/2016","00976909 OE_LTD 2014 Nissan NV200 SV 4dr Compact Cargo Van (67214)","322.17","0.00","322.17"));
			invoicesList.add(new InvoiceList("10/02/2016","INV00547084","23810612","366.45","0.00","366.45","FLBILLING","10/31/2016","00976909 TELM_B5200 2014 Nissan NV200 SV 4dr Compact Cargo Van (67214)","44.28","0.00","44.28"));
			invoicesList.add(new InvoiceList("11/18/2016","INV00660973","23810612","212.88","0.00","212.88","FLBILLING","11/30/2016","00976909 PREV_MAINT 2014 Nissan NV200 SV 4dr Compact Cargo Van (67214)","212.88","0.00","212.88"));
		} catch (Exception e) {
			logger.error(e);
		}
	}	
	
	//  We don't need to perform explicit search. The Primeface's LazyDataModel's load method will get call automatically 
	//	and will populate data.
	public void performSearch() {
	}  	
	
	public class InvoiceList {

		public InvoiceList(String invoiceDate, String invoiceNo,
				String contractNo, String amount, String taxAmount,
				String totalAmount, String billingType,
				String detailTransactionDate, String detailLineDescription,
				String detailAmount, String detailTaxAmount,
				String detailTotalAmount) {
			super();
			this.invoiceDate = invoiceDate;
			this.invoiceNo = invoiceNo;
			this.contractNo = contractNo;
			this.amount = amount;
			this.taxAmount = taxAmount;
			this.totalAmount = totalAmount;
			this.billingType = billingType;
			this.detailTransactionDate = detailTransactionDate;
			this.detailLineDescription = detailLineDescription;
			this.detailAmount = detailAmount;
			this.detailTaxAmount = detailTaxAmount;
			this.detailTotalAmount = detailTotalAmount;
		}
		
		String invoiceDate;
		String invoiceNo;
		String contractNo;
		String amount;
		String taxAmount;
		String totalAmount;
		String billingType;
		String detailTransactionDate;
		String detailLineDescription;
		String detailAmount;
		String detailTaxAmount;
		String detailTotalAmount;
		
		public String getInvoiceDate() {
			return invoiceDate;
		}
		public void setInvoiceDate(String invoiceDate) {
			this.invoiceDate = invoiceDate;
		}
		public String getInvoiceNo() {
			return invoiceNo;
		}
		public void setInvoiceNo(String invoiceNo) {
			this.invoiceNo = invoiceNo;
		}
		public String getContractNo() {
			return contractNo;
		}
		public void setContractNo(String contractNo) {
			this.contractNo = contractNo;
		}
		public String getAmount() {
			return amount;
		}
		public void setAmount(String amount) {
			this.amount = amount;
		}
		public String getTaxAmount() {
			return taxAmount;
		}
		public void setTaxAmount(String taxAmount) {
			this.taxAmount = taxAmount;
		}
		public String getTotalAmount() {
			return totalAmount;
		}
		public void setTotalAmount(String totalAmount) {
			this.totalAmount = totalAmount;
		}
		public String getBillingType() {
			return billingType;
		}
		public void setBillingType(String billingType) {
			this.billingType = billingType;
		}
		public String getDetailTransactionDate() {
			return detailTransactionDate;
		}
		public void setDetailTransactionDate(String detailTransactionDate) {
			this.detailTransactionDate = detailTransactionDate;
		}
		public String getDetailLineDescription() {
			return detailLineDescription;
		}
		public void setDetailLineDescription(String detailLineDescription) {
			this.detailLineDescription = detailLineDescription;
		}
		public String getDetailAmount() {
			return detailAmount;
		}
		public void setDetailAmount(String detailAmount) {
			this.detailAmount = detailAmount;
		}
		public String getDetailTaxAmount() {
			return detailTaxAmount;
		}
		public void setDetailTaxAmount(String detailTaxAmount) {
			this.detailTaxAmount = detailTaxAmount;
		}
		public String getDetailTotalAmount() {
			return detailTotalAmount;
		}
		public void setDetailTotalAmount(String detailTotalAmount) {
			this.detailTotalAmount = detailTotalAmount;
		}
	}

	public List<InvoiceList> getInvoicesList() {
		return invoicesList;
	}

	public void setInvoicesList(List<InvoiceList> invoicesList) {
		this.invoicesList = invoicesList;
	}
	
	

}
