package com.mikealbert.data.vo;

import java.util.Date;

public class ManufacturerProgressQueueVO {

	private Long docId;
	private Long clientAccountCId;
	private String clientAccountCode;
	private String clientAccountName;
	private String clientAccountType;
	private Date desiredToDealer;
	private Date expectedDate;
	private Long leadTime;
	private String make;
	private Date poDate;
	private Long psgId;
	private Long psoId;
	private String toleranceMessage;
	private String toleranceYn;
	private String trim;
	private String unitNo;
	private Date followUpDate;
	private Long pdiLeadTime;
	private Long fmsId;
	private Date factoryShipDate;
	private Date dealerReceivedDate;
	private Date vehicleReadyDate;
	private Date dlrRecdDate;
	private Date interimDealerDate;
	private Date prodDate;
	private boolean stockOrder;
	private Date invProcDate;

	public Long getDocId() {
		return docId;
	}

	public void setDocId(Long docId) {
		this.docId = docId;
	}

	public Long getClientAccountCId() {
		return clientAccountCId;
	}

	public void setClientAccountCId(Long clientAccountCId) {
		this.clientAccountCId = clientAccountCId;
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

	public String getClientAccountType() {
		return clientAccountType;
	}

	public void setClientAccountType(String clientAccountType) {
		this.clientAccountType = clientAccountType;
	}

	public Date getDesiredToDealer() {
		return desiredToDealer;
	}

	public void setDesiredToDealer(Date desiredToDealer) {
		this.desiredToDealer = desiredToDealer;
	}

	public Date getExpectedDate() {
		return expectedDate;
	}

	public void setExpectedDate(Date expectedDate) {
		this.expectedDate = expectedDate;
	}

	public Long getLeadTime() {
		return leadTime;
	}

	public void setLeadTime(Long leadTime) {
		this.leadTime = leadTime;
	}

	public String getMake() {
		return make;
	}

	public void setMake(String make) {
		this.make = make;
	}

	public Date getPoDate() {
		return poDate;
	}

	public void setPoDate(Date poDate) {
		this.poDate = poDate;
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

	public String getToleranceMessage() {
		return toleranceMessage;
	}

	public void setToleranceMessage(String toleranceMessage) {
		this.toleranceMessage = toleranceMessage;
	}

	public String getToleranceYn() {
		return toleranceYn;
	}

	public void setToleranceYn(String toleranceYn) {
		this.toleranceYn = toleranceYn;
	}

	public String getTrim() {
		return trim;
	}

	public void setTrim(String trim) {
		this.trim = trim;
	}

	public String getUnitNo() {
		return unitNo;
	}

	public void setUnitNo(String unitNo) {
		this.unitNo = unitNo;
	}
	
	public Date getFollowUpDate() {
		return followUpDate;
	}

	public void setFollowUpDate(Date followUpDate) {
		this.followUpDate = followUpDate;
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

	public Date getVehicleReadyDate() {
		return vehicleReadyDate;
	}

	public void setVehicleReadyDate(Date vehicleReadyDate) {
		this.vehicleReadyDate = vehicleReadyDate;
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

	public Long getPdiLeadTime() {
		return pdiLeadTime;
	}

	public void setPdiLeadTime(Long pdiLeadTime) {
		this.pdiLeadTime = pdiLeadTime;
	}
	
	public boolean isStockOrder() {
		return stockOrder;
	}

	public void setStockOrder(boolean stockOrder) {
		this.stockOrder = stockOrder;
	}

	@Override
	public String toString() {
		return "ManufacturerProgressQueueVO [unitNo=" + unitNo + "]";
	}

	public Date getInvProcDate() {
		return invProcDate;
	}

	public void setInvProcDate(Date invProcDate) {
		this.invProcDate = invProcDate;
	}

	public Long getFmsId() {
		return fmsId;
	}

	public void setFmsId(Long fmsId) {
		this.fmsId = fmsId;
	}

}