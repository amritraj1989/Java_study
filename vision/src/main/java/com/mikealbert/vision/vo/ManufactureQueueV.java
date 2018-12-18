package com.mikealbert.vision.vo;

import java.io.Serializable;
import java.util.Date;
//TODO This need to move in core data project once view got created. 
public class ManufactureQueueV implements Serializable {

	private static final long serialVersionUID = -5679055910751292814L;

	private Long psoId;
	private String make;
	private String unitNo;
	private String trim;
	private String clientName;
	private String clientCode;
	private String orderingDealerName;
	private String orderingDealerCode;
	private Date poPostedDate;
	private String leadTime;
	private Date desiredToDlr;
	private Date expectedDate;
	private String releasedBy;
	private Date factoryShipDate;
	private Date dealerReceivedDate;
	private Date vehicleReadyDate;
	private Date dlrRecdDate;
	private Date interimDealerDate;
	private Date prodDate;
	private Date followUpDate;
	private String reason;
	private Long docId;
	private boolean isStockUnit;
	
	private Long cId;
	private String accountCode;
	private String accountName;
	private String accountType;

	public Long getPsoId() {
		return psoId;
	}

	public void setPsoId(Long psoId) {
		this.psoId = psoId;
	}

	public String getMake() {
		return make;
	}

	public void setMake(String make) {
		this.make = make;
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

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getClientCode() {
		return clientCode;
	}

	public void setClientCode(String clientCode) {
		this.clientCode = clientCode;
	}

	public String getOrderingDealerName() {
		return orderingDealerName;
	}

	public void setOrderingDealerName(String orderingDealerName) {
		this.orderingDealerName = orderingDealerName;
	}

	public String getOrderingDealerCode() {
		return orderingDealerCode;
	}

	public void setOrderingDealerCode(String orderingDealerCode) {
		this.orderingDealerCode = orderingDealerCode;
	}

	public Date getPoPostedDate() {
		return poPostedDate;
	}

	public void setPoPostedDate(Date poPostedDate) {
		this.poPostedDate = poPostedDate;
	}

	public String getLeadTime() {
		return leadTime;
	}

	public void setLeadTime(String leadTime) {
		this.leadTime = leadTime;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Long getCId() {
		return cId;
	}

	public void setCId(Long cId) {
		this.cId = cId;
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

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public Long getDocId() {
		return docId;
	}

	public void setDocId(Long docId) {
		this.docId = docId;
	}

	public boolean isStockUnit() {
		return isStockUnit;
	}

	public void setStockUnit(boolean isStockUnit) {
		this.isStockUnit = isStockUnit;
	}

	public Date getVehicleReadyDate() {
		return vehicleReadyDate;
	}

	public void setVehicleReadyDate(Date vehicleReadyDate) {
		this.vehicleReadyDate = vehicleReadyDate;
	}

	public Date getFactoryShipDate() {
		return factoryShipDate;
	}

	public void setFactoryShipDate(Date factoryShipDate) {
		this.factoryShipDate = factoryShipDate;
	}

	public Date getDealerReceivedDate() {
		return dealerReceivedDate;
	}

	public void setDealerReceivedDate(Date dealerReceivedDate) {
		this.dealerReceivedDate = dealerReceivedDate;
	}

	public Date getDlrRecdDate() {
		return dlrRecdDate;
	}

	public void setDlrRecdDate(Date dlrRecdDate) {
		this.dlrRecdDate = dlrRecdDate;
	}

	public Date getInterimDealerDate() {
		return interimDealerDate;
	}

	public void setInterimDealerDate(Date interimDealerDate) {
		this.interimDealerDate = interimDealerDate;
	}

	public Date getProdDate() {
		return prodDate;
	}

	public void setProdDate(Date prodDate) {
		this.prodDate = prodDate;
	}

	public Date getDesiredToDlr() {
		return desiredToDlr;
	}

	public void setDesiredToDlr(Date desiredToDlr) {
		this.desiredToDlr = desiredToDlr;
	}

	public Date getExpectedDate() {
		return expectedDate;
	}

	public void setExpectedDate(Date expectedDate) {
		this.expectedDate = expectedDate;
	}

	public String getReleasedBy() {
		return releasedBy;
	}

	public void setReleasedBy(String releasedBy) {
		this.releasedBy = releasedBy;
	}

	public Date getFollowUpDate() {
		return followUpDate;
	}

	public void setFollowUpDate(Date followUpDate) {
		this.followUpDate = followUpDate;
	}

}
