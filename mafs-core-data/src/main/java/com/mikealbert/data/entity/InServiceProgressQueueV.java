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

/**
* Mapped to MANUFACTURER_PROGRESS_QUEUE_V view.
* @author Raj
*/
@Entity
@Table(name="IN_SERVICE_QUEUE_V")
public class InServiceProgressQueueV implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="PSO_ID")
	private Long psoId;

	@Column(name="STOCK_YN")
	private String stockYN;

	@Column(name="DRIVER_NAME")
	private String driverName;

	@Column(name="DRV_ID")
	private Long drv_id;
	
	@Column(name="ACCOUNT_NAME")
	private String accountName;	
	
	@Column(name="ACCOUNT_CODE")
	private String accountCode;	
	
	@Column(name="UNIT_NO")
	private String unitNo;
	
	@Column(name="VIN")
	private String vin;	
	
	@Column(name="DOC_ID")
	private Long docId;	
	
	@Column(name="QMD_ID")
	private Long qmdId;		
	
	@Column(name="QUOTE_STATUS")
	private String quoteStatus;
	
	@Column(name="USED_VEHICLE")
	private String usedVehicle;		

	@Column(name="ORDER_TYPE")
	private String orderType;	
	
	@Column(name="LIC_PLATE_NO")
	private String licensePlateNumber;
	
	@Column(name="REPLACEMENT_FOR_FMS_ID")
	private Long replacementForFmsId;
	
	@Column(name="OLD_UNIT_NO")
	private String oldUnitNo;
	
	@Column(name="DELIVERY_DEALER_NAME")
	private String deliveryDealerName;
	
	@Column(name="DELIVERY_DEALER_CODE")
	private String deliveryDealerCode;		
	
	@Column(name="PLATE_EXPIRY_DATE")
	private String plateExpiryDate;			
	
	@Temporal(TemporalType.DATE)
	@Column(name="INVOICE_PROCESSED_DATE")
	private Date invoiceProcessedDate;

	@Temporal(TemporalType.DATE)
	@Column(name="DEALER_RECVD_DATE")
	private Date dealerReceivedDate;

	@Temporal(TemporalType.DATE)
	@Column(name="CLIENT_ETA_DATE")
	private Date clientETADate;
	
	@Temporal(TemporalType.DATE)
	@Column(name="VEHICLE_READY_DATE")
	private Date vehicleReadyDate;
	
	@Temporal(TemporalType.DATE)
	@Column(name="FOLLOW_UP_DATE")
	private Date followUpDate;
	
	@Column(name="REQD_BY")
	private String requiredBy;
	
	@Column(name="MODEL_DESC")
	private String modelDescription;	
	
	@Column(name="LEAD_TIME")
	private Long leadTime;	

	@Column(name="DR_DESCRIPTION")
	private String drDescription;

	public Long getPsoId() {
		return psoId;
	}

	public void setPsoId(Long psoId) {
		this.psoId = psoId;
	}

	public String getStockYN() {
		return stockYN;
	}

	public void setStockYN(String stockYN) {
		this.stockYN = stockYN;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public Long getDrv_id() {
		return drv_id;
	}

	public void setDrv_id(Long drv_id) {
		this.drv_id = drv_id;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getAccountCode() {
		return accountCode;
	}

	public void setAccountCode(String accountCode) {
		this.accountCode = accountCode;
	}

	public String getUnitNo() {
		return unitNo;
	}

	public void setUnitNo(String unitNo) {
		this.unitNo = unitNo;
	}

	public String getVin() {
		return vin;
	}

	public void setVin(String vin) {
		this.vin = vin;
	}

	public Long getDocId() {
		return docId;
	}

	public void setDocId(Long docId) {
		this.docId = docId;
	}

	public Long getQmdId() {
		return qmdId;
	}

	public void setQmdId(Long qmdId) {
		this.qmdId = qmdId;
	}

	public String getQuoteStatus() {
		return quoteStatus;
	}

	public void setQuoteStatus(String quoteStatus) {
		this.quoteStatus = quoteStatus;
	}

	public String getUsedVehicle() {
		return usedVehicle;
	}

	public void setUsedVehicle(String usedVehicle) {
		this.usedVehicle = usedVehicle;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public String getLicensePlateNumber() {
		return licensePlateNumber;
	}

	public void setLicensePlateNumber(String licensePlateNumber) {
		this.licensePlateNumber = licensePlateNumber;
	}

	public Long getReplacementForFmsId() {
		return replacementForFmsId;
	}

	public void setReplacementForFmsId(Long replacementForFmsId) {
		this.replacementForFmsId = replacementForFmsId;
	}

	public String getOldUnitNo() {
		return oldUnitNo;
	}

	public void setOldUnitNo(String oldUnitNo) {
		this.oldUnitNo = oldUnitNo;
	}

	public String getDeliveryDealerName() {
		return deliveryDealerName;
	}

	public void setDeliveryDealerName(String deliveryDealerName) {
		this.deliveryDealerName = deliveryDealerName;
	}

	public String getDeliveryDealerCode() {
		return deliveryDealerCode;
	}

	public void setDeliveryDealerCode(String deliveryDealerCode) {
		this.deliveryDealerCode = deliveryDealerCode;
	}

	public String getPlateExpiryDate() {
		return plateExpiryDate;
	}

	public void setPlateExpiryDate(String plateExpiryDate) {
		this.plateExpiryDate = plateExpiryDate;
	}

	public Date getInvoiceProcessedDate() {
		return invoiceProcessedDate;
	}

	public void setInvoiceProcessedDate(Date invoiceProcessedDate) {
		this.invoiceProcessedDate = invoiceProcessedDate;
	}

	public Date getDealerReceivedDate() {
		return dealerReceivedDate;
	}

	public void setDealerReceivedDate(Date dealerReceivedDate) {
		this.dealerReceivedDate = dealerReceivedDate;
	}

	public Date getClientETADate() {
		return clientETADate;
	}

	public void setClientETADate(Date clientETADate) {
		this.clientETADate = clientETADate;
	}

	public Date getVehicleReadyDate() {
		return vehicleReadyDate;
	}

	public void setVehicleReadyDate(Date vehicleReadyDate) {
		this.vehicleReadyDate = vehicleReadyDate;
	}

	public Date getFollowUpDate() {
		return followUpDate;
	}

	public void setFollowUpDate(Date followUpDate) {
		this.followUpDate = followUpDate;
	}

	public String getRequiredBy() {
		return requiredBy;
	}

	public void setRequiredBy(String requiredBy) {
		this.requiredBy = requiredBy;
	}

	public String getModelDescription() {
		return modelDescription;
	}

	public void setModelDescription(String modelDescription) {
		this.modelDescription = modelDescription;
	}

	public Long getLeadTime() {
		return leadTime;
	}

	public void setLeadTime(Long leadTime) {
		this.leadTime = leadTime;
	}

	public String getDrDescription() {
		return drDescription;
	}

	public void setDrDescription(String drDescription) {
		this.drDescription = drDescription;
	}

	@Override
	public String toString() {
		return "InServiceProgressQueueV [psoId=" + this.getPsoId() + "]";
	}
}