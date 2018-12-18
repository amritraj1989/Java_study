package com.mikealbert.data.vo;

import java.math.BigDecimal;

public class PurchaseOrderVO {

	private Long mainPoId;
	private Long thPoId;
	private String stockYn;
	
	private String POReleaseDate;
	private String PONO;// includes doc no and rev no both
	private String docNo;
	private Integer revNo;
	private String vendorName;
	private String docContext;
	private String client;
	private String clientCode;
	private String deliveringDealerCode;
	private String FINOrFANNo;
	private String competitiveFleetProgram;
	private String incentiveProgram;
	private String fleetRefNo;
	private String unitNo;
	private String factoryOrderNo;
	private String vin;
	private String desiredArrivalDateToDealerOrVendor;
	private String clientRequestedInfo;
	private Long qmdId;
	private Long drvId;
	private Long mdlId;
	private String orderType;
	private String productCode;
	private String quoteNotes;
	private Long replacementFmsId;
	private String modelDesc;
	private String bodyColor;
	private String interiorColor;
	private String leaseExpirationDate;
	private BigDecimal poPurchasePrice;
	private String leaseType;

	public String getPOReleaseDate() {
		return POReleaseDate;
	}

	public void setPOReleaseDate(String pOReleaseDate) {
		POReleaseDate = pOReleaseDate;
	}

	public String getPONO() {
		return PONO;
	}

	public void setPONO(String pONO) {
		PONO = pONO;
	}

	public String getDocNo() {
		return docNo;
	}

	public void setDocNo(String docNo) {
		this.docNo = docNo;
	}

	public Integer getRevNo() {
		return revNo;
	}

	public void setRevNo(Integer revNo) {
		this.revNo = revNo;
	}

	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}

	public String getFINOrFANNo() {
		return FINOrFANNo;
	}

	public void setFINOrFANNo(String fINOrFANNo) {
		FINOrFANNo = fINOrFANNo;
	}

	public String getCompetitiveFleetProgram() {
		return competitiveFleetProgram;
	}

	public void setCompetitiveFleetProgram(String competitiveFleetProgram) {
		this.competitiveFleetProgram = competitiveFleetProgram;
	}

	public String getIncentiveProgram() {
		return incentiveProgram;
	}

	public void setIncentiveProgram(String incentiveProgram) {
		this.incentiveProgram = incentiveProgram;
	}

	public String getFleetRefNo() {
		return fleetRefNo;
	}

	public void setFleetRefNo(String fleetRefNo) {
		this.fleetRefNo = fleetRefNo;
	}

	public String getUnitNo() {
		return unitNo;
	}

	public void setUnitNo(String unitNo) {
		this.unitNo = unitNo;
	}

	public String getFactoryOrderNo() {
		return factoryOrderNo;
	}

	public void setFactoryOrderNo(String factoryOrderNo) {
		this.factoryOrderNo = factoryOrderNo;
	}

	public String getVin() {
		return vin;
	}

	public void setVin(String vin) {
		this.vin = vin;
	}

	public String getDesiredArrivalDateToDealerOrVendor() {
		return desiredArrivalDateToDealerOrVendor;
	}

	public void setDesiredArrivalDateToDealerOrVendor(
			String desiredArrivalDateToDealerOrVendor) {
		this.desiredArrivalDateToDealerOrVendor = desiredArrivalDateToDealerOrVendor;
	}

	public String getClientRequestedInfo() {
		return clientRequestedInfo;
	}

	public void setClientRequestedInfo(String clientRequestedInfo) {
		this.clientRequestedInfo = clientRequestedInfo;
	}

	public String getQuoteNotes() {
		return quoteNotes;
	}

	public void setQuoteNotes(String quoteNotes) {
		this.quoteNotes = quoteNotes;
	}

	public Long getQmdId() {
		return qmdId;
	}

	public void setQmdId(Long qmdId) {
		this.qmdId = qmdId;
	}

	public Long getDrvId() {
		return drvId;
	}

	public void setDrvId(Long drvId) {
		this.drvId = drvId;
	}

	public Long getMdlId() {
		return mdlId;
	}

	public void setMdlId(Long mdlId) {
		this.mdlId = mdlId;
	}

	public Long getReplacementFmsId() {
		return replacementFmsId;
	}

	public void setReplacementFmsId(Long replacementFmsId) {
		this.replacementFmsId = replacementFmsId;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public String getDeliveringDealerCode() {
		return deliveringDealerCode;
	}

	public void setDeliveringDealerCode(String deliveringDealerCode) {
		this.deliveringDealerCode = deliveringDealerCode;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getModelDesc() {
		return modelDesc;
	}

	public void setModelDesc(String modelDesc) {
		this.modelDesc = modelDesc;
	}

	public String getBodyColor() {
		return bodyColor;
	}

	public void setBodyColor(String bodyColor) {
		this.bodyColor = bodyColor;
	}

	public String getInteriorColor() {
		return interiorColor;
	}

	public void setInteriorColor(String interiorColor) {
		this.interiorColor = interiorColor;
	}

	public Long getThPoId() {
		return thPoId;
	}

	public void setThPoId(Long thPoId) {
		this.thPoId = thPoId;
	}

	public Long getMainPoId() {
		return mainPoId;
	}

	public void setMainPoId(Long mainPoId) {
		this.mainPoId = mainPoId;
	}

	public String getStockYn() {
		return stockYn;
	}

	public void setStockYn(String stockYn) {
		this.stockYn = stockYn;
	}

	public String getClientCode() {
		return clientCode;
	}

	public void setClientCode(String clientCode) {
		this.clientCode = clientCode;
	}

	public String getLeaseExpirationDate() {
		return leaseExpirationDate;
	}

	public void setLeaseExpirationDate(String leaseExpirationDate) {
		this.leaseExpirationDate = leaseExpirationDate;
	}

	public BigDecimal getPoPurchasePrice() {
		return poPurchasePrice;
	}

	public void setPoPurchasePrice(BigDecimal poPurchasePrice) {
		this.poPurchasePrice = poPurchasePrice;
	}

	public String getDocContext() {
		return docContext;
	}

	public void setDocContext(String docContext) {
		this.docContext = docContext;
	}

	public String getLeaseType() {
		return leaseType;
	}

	public void setLeaseType(String leaseType) {
		this.leaseType = leaseType;
	}

}