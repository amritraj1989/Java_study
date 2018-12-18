package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.mikealbert.util.MALUtilities;

/**
* Mapped to PURCHASE_ORDER_RELEASE_QUEUE_V view.
* @author Amritraj
*/
@Entity
@Table(name="PURCHASE_ORDER_RELEASE_QUEUE_V")
public class PurchaseOrderReleaseQueueV implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
    @Column(name = "QMD_QMD_ID")
    private Long qmdId;

	@Column(name="PSG_ID")
	private Long psgId;

	@Column(name="PSO_ID")
	private Long psoId;
	
	@Column(name="DOC_ID")
	private Long docId;
	
	@Column(name="MAKE")
	private String make;
	
	@Column(name = "ORDER_TYPE")
    private String orderType;

	@Column(name="UNIT_NO")
	private String unitNo;
	
	@Column(name="\"TRIM\"")
	private String trim;
	
	@Column(name = "QUO_QUO_ID")
	private Long quoId;
	    
	@Column(name = "QUOTE_NUMBER")
	private String quoteNumber;
	
	@Column(name="CLIENT_ACCOUNT_CODE")
	private String clientAccountCode;

	@Column(name="CLIENT_ACCOUNT_NAME")
	private String clientAccountName;

	@Column(name="CLIENT_ACCOUNT_C_ID")
	private Long clientAccountCId;

	@Column(name="CLIENT_ACCOUNT_TYPE")
	private String clientAccountType;

	@Column(name="QUOTE_USERNAME")
	private String quoteCreator;

	@Column(name="QUOTE_REQUIRED_DATE")
	private String quoteRequiredDate;
	
	@Column(name="PO_STATUS")
	private String poStatus;
	
	@Column(name="PO_NUMBER")
	private String poNumber;
	
	@Column(name = "PO_RELEASED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date poReleaseDate;
	
	@Column(name="VENDOR_NAME")
	private String vendorName;
	
	@Column(name="TOLERANCE_YN")
	private String toleranceYN;
	
	@Column(name="TOLERANCE_MESSAGE")
	private String toleranceMessage;	
	
	@Column(name="ACQUISITION_TYPE")
	private String acquisitionType;

	@Transient
	private String rowKey;
	
	@Override
	public String toString() {
		return "PurchaseOrderReleaseQueueV [unitNo=" + unitNo + "]";
	}

	public Long getQmdId() {
		return qmdId;
	}

	public void setQmdId(Long qmdId) {
		this.qmdId = qmdId;
	}

	public Long getPsgId() {
		return psgId;
	}

	public void setPsgId(Long psgId) {
		this.psgId = psgId;
	}

	public Long getPsoId() {
		return psoId;
	}

	public void setPsoId(Long psoId) {
		this.psoId = psoId;
	}

	public Long getDocId() {
		return docId;
	}

	public void setDocId(Long docId) {
		this.docId = docId;
	}

	public String getMake() {
		return make;
	}

	public void setMake(String make) {
		this.make = make;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public String getUnitNo() {
		return unitNo;
	}

	public void setUnitNo(String unitNo) {
		this.unitNo = unitNo;
	}

	public String getTrim() {
		return trim;
	}

	public void setTrim(String trim) {
		this.trim = trim;
	}

	public Long getQuoId() {
		return quoId;
	}

	public void setQuoId(Long quoId) {
		this.quoId = quoId;
	}

	public String getQuoteNumber() {
		return quoteNumber;
	}

	public void setQuoteNumber(String quoteNumber) {
		this.quoteNumber = quoteNumber;
	}

	public String getClientAccountCode() {
		return clientAccountCode;
	}

	public void setClientAccountCode(String clientAccountCode) {
		this.clientAccountCode = clientAccountCode;
	}

	public String getClientAccountName() {
		return clientAccountName;
	}

	public void setClientAccountName(String clientAccountName) {
		this.clientAccountName = clientAccountName;
	}

	public Long getClientAccountCId() {
		return clientAccountCId;
	}

	public void setClientAccountCId(Long clientAccountCId) {
		this.clientAccountCId = clientAccountCId;
	}

	public String getClientAccountType() {
		return clientAccountType;
	}

	public void setClientAccountType(String clientAccountType) {
		this.clientAccountType = clientAccountType;
	}

	public String getQuoteCreator() {
		return quoteCreator;
	}

	public void setQuoteCreator(String quoteCreator) {
		this.quoteCreator = quoteCreator;
	}

	public String getQuoteRequiredDate() {
		return quoteRequiredDate;
	}

	public void setQuoteRequiredDate(String quoteRequiredDate) {
		this.quoteRequiredDate = quoteRequiredDate;
	}

	public String getPoStatus() {
		return poStatus;
	}

	public void setPoStatus(String poStatus) {
		this.poStatus = poStatus;
	}

	public String getPoNumber() {
		return poNumber;
	}

	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
	}

	public Date getPoReleaseDate() {
		return poReleaseDate;
	}

	public void setPoReleaseDate(Date poReleaseDate) {
		this.poReleaseDate = poReleaseDate;
	}

	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}

	public String getToleranceYN() {
		return toleranceYN;
	}

	public void setToleranceYN(String toleranceYN) {
		this.toleranceYN = toleranceYN;
	}

	public String getToleranceMessage() {
		return toleranceMessage;
	}

	public void setToleranceMessage(String toleranceMessage) {
		this.toleranceMessage = toleranceMessage;
	}

	public String getRowKey() {
		if(!MALUtilities.isEmpty(poStatus)){
			return unitNo + "-" + poStatus;
		}else{
			return unitNo;
		}
	}

	public void setRowKey(String rowKey) {
		this.rowKey = rowKey;
	}

	public String getAcquisitionType() {
		return acquisitionType;
	}

	public void setAcquisitionType(String acquisitionType) {
		this.acquisitionType = acquisitionType;
	}

}