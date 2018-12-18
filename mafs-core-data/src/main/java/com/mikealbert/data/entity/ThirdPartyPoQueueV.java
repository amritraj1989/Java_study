package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.mikealbert.data.vo.VendorInfoVO;

/**
* Mapped to THIRD_PARTY_PO_QUEUE_V view.
* @author Amritraj
*/
@Entity
@Table(name="THIRD_PARTY_PO_QUEUE_V")
public class ThirdPartyPoQueueV implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="DOC_ID")
	private Long docId;
	
	@Column(name="PSO_ID")
	private Long psoId;

	@Column(name = "QMD_ID")
    private Long qmdId;

	@Column(name="PSG_ID")
	private Long psgId;
	
	@Column(name = "FMS_ID")
    private Long fmsId;

	@Column(name="UNIT_NO")
	private String unitNo;
	
	@Column(name="CLIENT_ACCOUNT_CODE")
	private String clientAccountCode;

	@Column(name="CLIENT_ACCOUNT_NAME")
	private String clientAccountName;

	@Column(name="CLIENT_ACCOUNT_C_ID")
	private Long clientAccountCId;

	@Column(name="CLIENT_ACCOUNT_TYPE")
	private String clientAccountType;

	@Column(name = "ORDER_TYPE")
    private String orderType;
	
	@Column(name = "ACQUISITION_TYPE")
    private String acquisitionType;
	
	@Column(name="TOLERANCE_YN")
	private String toleranceYN;
	
	@Column(name="TOLERANCE_MESSAGE")
	private String toleranceMessage;
	
	@Column(name="STOCK_YN")
	private String stockYN;

	@Transient
	private String rowKey;
	
	@Transient
	private List<VendorInfoVO> vendorInfoVOList = new ArrayList<VendorInfoVO>();
	
	@Override
	public String toString() {
		return "ThirdPartyPoReleaseQueueV [unitNo=" + unitNo + "]";
	}

	public Long getQmdId() {
		return qmdId;
	}


	public void setQmdId(Long qmdId) {
		this.qmdId = qmdId;
	}


	public Long getDocId() {
		return docId;
	}

	public void setDocId(Long docId) {
		this.docId = docId;
	}
	
    public Long getPsoId() {
		return psoId;
	}

	public void setPsoId(Long psoId) {
		this.psoId = psoId;
	}

	public String getUnitNo() {
		return unitNo;
	}


	public void setUnitNo(String unitNo) {
		this.unitNo = unitNo;
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


	public String getOrderType() {
		return orderType;
	}


	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}


	public String getAcquisitionType() {
		return acquisitionType;
	}


	public void setAcquisitionType(String acquisitionType) {
		this.acquisitionType = acquisitionType;
	}


	public String getRowKey() {
		return unitNo;
	}

	public void setRowKey(String rowKey) {
		this.rowKey = rowKey;
	}

	public String getToleranceYN() {
		return toleranceYN;
	}


	public void setToleranceYN(String toleranceYN) {
		this.toleranceYN = toleranceYN;
	}


	public String getStockYN() {
		return stockYN;
	}

	public void setStockYN(String stockYN) {
		this.stockYN = stockYN;
	}

	public String getToleranceMessage() {
		return toleranceMessage;
	}


	public void setToleranceMessage(String toleranceMessage) {
		this.toleranceMessage = toleranceMessage;
	}

	public List<VendorInfoVO> getVendorInfoVOList() {
		return vendorInfoVOList;
	}

	public void setVendorInfoVOList(List<VendorInfoVO> vendorInfoVOList) {
		this.vendorInfoVOList = vendorInfoVOList;
	}

	public Long getFmsId() {
		return fmsId;
	}

	public void setFmsId(Long fmsId) {
		this.fmsId = fmsId;
	}
}