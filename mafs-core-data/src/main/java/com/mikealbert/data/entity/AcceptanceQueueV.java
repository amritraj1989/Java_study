package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;
import javax.validation.constraints.Size;

import com.mikealbert.util.MALUtilities;

/**
* Mapped to ACCEPTANCE_QUEUE_V view.
* @author Raj
*/
@Entity
@Table(name = "ACCEPTANCE_QUEUE_V")
public class AcceptanceQueueV implements Serializable  {
	private static final long serialVersionUID = 1L;
	
	@Id
    @Column(name = "QMD_QMD_ID")
    private Long qmdId;
	
	@Column(name = "ORDER_TYPE")
    private String orderType;
    
    @Column(name = "REQUEST_FOR_ACCEPTANCE_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date requestForAcceptDate;
    
    @Column(name = "REQUEST_FOR_ACCEPTANCE_BY")
    private String requestForAcceptBy;
    
    @Column(name = "QUOTE_ACCEPTANCE_TYPE_CODE")
    private String quoteAcceptanceTypeCode;
    
    @Column(name = "REQUEST_FOR_ACCEPTANCE_TYPE")
    private String requestForAcceptType;
    
    @Column(name = "QUO_QUO_ID")
    private Long quoId;
    
	@Column(name = "QUOTE_NUMBER")
    private String quoteNumber;
	
	@Column(name = "UNIT_NO")
	private String unitNo;
    
    @Column(name = "CLIENT_ACCOUNT")
    private String clientAccount;
    
    @Column(name = "CLIENT_ACCOUNT_NAME")
    private String clientAccountName;
    
    @Column(name = "CLIENT_ACCOUNT_C_ID")
    private String clientAccountCid;
    
    @Column(name = "CLIENT_ACCOUNT_TYPE")
    private String clientAccountType;
    
    @Column(name = "DRV_DRV_ID")
    private Long driverId;
    
    @Column(name = "DRIVER_FIRST_NAME")
    private String driverFirstName;
    
    @Column(name = "DRIVER_LAST_NAME")
    private String driverLastName;

    @Column(name = "CONTRACT_PERIOD")
    private Long contractPeriod;

    @Column(name = "CONTRACT_DISTANCE")
    private Long contractDistance;

    @Column(name = "MODEL_DESCRIPTION")
    private String modelDescription;
    
    @Column(name = "QUOTE_START_ODO")
    private Long quoteStartOdo;
    
    @Column(name = "TMP_VIN_NO")
    private String tmpVinNo;
    
    @Column(name = "EXTERIOR_COLOR")
    private String exteriorColor;
    
    @Column(name = "INTERIOR_COLOR")
    private String interiorColor;
    
    @Column(name = "RETURNING_UNIT_FMS_ID")
    private Long returningUnitFmsId;
    
    @Column(name = "RETURNING_UNIT_NO")
    private String returningUnitNumber;
    
    @Column(name = "CLIENT_REQUEST_DATE")
    private String clientRequestDate;
    
    @Column(name = "PRODUCT_CODE")
    private String productCode;
    
    @Size(max = 1)
    @Column(name = "TOLERANCE_YN")
    private String toleranceYN;
    
    @Column(name = "QUOTE_STATUS")
    private String quoteStatus;
    
    @Column(name = "PRINTED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date printedDate;
    
    @Column(name = "REVISION_EXP_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date revisionExpDate;
    
    @Column(name = "REVISION_QMD_ID")
    private Long revisionQmdId;

    @Transient
    private boolean isScheduleAIsSetupForEmail ;  

	@Transient
    private boolean isAmortizationIsSetupForEmail;
    
	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public Date getRequestForAcceptDate() {
		return requestForAcceptDate;
	}

	public void setRequestForAcceptDate(Date requestForAcceptDate) {
		this.requestForAcceptDate = requestForAcceptDate;
	}

	public String getRequestForAcceptBy() {
		return requestForAcceptBy;
	}

	public void setRequestForAcceptBy(String requestForAcceptBy) {
		this.requestForAcceptBy = requestForAcceptBy;
	}

	public String getQuoteAcceptanceTypeCode() {
		return quoteAcceptanceTypeCode;
	}

	public void setQuoteAcceptanceTypeCode(String quoteAcceptanceTypeCode) {
		this.quoteAcceptanceTypeCode = quoteAcceptanceTypeCode;
	}

	public String getRequestForAcceptType() {
		return requestForAcceptType;
	}

	public void setRequestForAcceptType(String requestForAcceptType) {
		this.requestForAcceptType = requestForAcceptType;
	}

	public Long getQmdId() {
		return qmdId;
	}

	public void setQmdId(Long qmdId) {
		this.qmdId = qmdId;
	}

	public Long getQuoId() {
		return quoId;
	}

	public void setQuoId(Long quoId) {
		this.quoId = quoId;
	}

	public String getUnitNo() {
		return unitNo;
	}

	public void setUnitNo(String unitNo) {
		this.unitNo = unitNo;
	}

	public String getClientAccount() {
		return clientAccount;
	}

	public void setClientAccount(String clientAccount) {
		this.clientAccount = clientAccount;
	}

	public String getClientAccountName() {
		return clientAccountName;
	}

	public void setClientAccountName(String clientAccountName) {
		this.clientAccountName = clientAccountName;
	}

	public String getClientAccountCid() {
		return clientAccountCid;
	}

	public void setClientAccountCid(String clientAccountCid) {
		this.clientAccountCid = clientAccountCid;
	}

	public String getClientAccountType() {
		return clientAccountType;
	}

	public void setClientAccountType(String clientAccountType) {
		this.clientAccountType = clientAccountType;
	}

	public Long getDriverId() {
		return driverId;
	}

	public void setDriverId(Long driverId) {
		this.driverId = driverId;
	}

	public String getDriverFirstName() {
		return driverFirstName;
	}

	public void setDriverFirstName(String driverFirstName) {
		this.driverFirstName = driverFirstName;
	}

	public String getDriverLastName() {
		return driverLastName;
	}

	public void setDriverLastName(String driverLastName) {
		this.driverLastName = driverLastName;
	}

	public Long getContractPeriod() {
		return contractPeriod;
	}

	public void setContractPeriod(Long contractPeriod) {
		this.contractPeriod = contractPeriod;
	}

	public Long getContractDistance() {
		return contractDistance;
	}

	public void setContractDistance(Long contractDistance) {
		this.contractDistance = contractDistance;
	}

	public String getExteriorColor() {
		return exteriorColor;
	}

	public void setExteriorColor(String exteriorColor) {
		this.exteriorColor = exteriorColor;
	}

	public String getInteriorColor() {
		return interiorColor;
	}

	public void setInteriorColor(String interiorColor) {
		this.interiorColor = interiorColor;
	}

	public Long getReturningUnitFmsId() {
		return returningUnitFmsId;
	}

	public void setReturningUnitFmsId(Long returningUnitFmsId) {
		this.returningUnitFmsId = returningUnitFmsId;
	}

	public String getReturningUnitNumber() {
		return returningUnitNumber;
	}

	public void setReturningUnitNumber(String returningUnitNumber) {
		this.returningUnitNumber = returningUnitNumber;
	}

	public String getClientRequestDate() {
		return clientRequestDate;
	}

    public String getQuoteNumber() {
		return quoteNumber;
	}

	public void setQuoteNumber(String quoteNumber) {
		this.quoteNumber = quoteNumber;
	}
	
	public void setClientRequestDate(String clientRequestDate) {
		this.clientRequestDate = clientRequestDate;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getToleranceYN() {
		return toleranceYN;
	}

	public void setToleranceYN(String toleranceYN) {
		this.toleranceYN = toleranceYN;
	}
	
	@Transient
	public String getDriverName() {
		String drvName = null;
		if (!MALUtilities.isEmpty(getDriverLastName())) {
			drvName = getDriverLastName().trim();
		}
		
		if (!MALUtilities.isEmpty(getDriverFirstName())) {
			drvName = drvName + ", " + getDriverFirstName().trim();
		}
		
		return drvName;
	}

	public String getModelDescription() {
		return modelDescription;
	}

	public void setModelDescription(String modelDescription) {
		this.modelDescription = modelDescription;
	}

	public Long getQuoteStartOdo() {
		return quoteStartOdo;
	}

	public void setQuoteStartOdo(Long quoteStartOdo) {
		this.quoteStartOdo = quoteStartOdo;
	}

	public String getTmpVinNo() {
		return tmpVinNo;
	}

	public void setTmpVinNo(String tmpVinNo) {
		this.tmpVinNo = tmpVinNo;
	}

	public String getQuoteStatus() {
		return quoteStatus;
	}

	public void setQuoteStatus(String quoteStatus) {
		this.quoteStatus = quoteStatus;
	}

	public Date getPrintedDate() {
		return printedDate;
	}

	public void setPrintedDate(Date printedDate) {
		this.printedDate = printedDate;
	}

	public Date getRevisionExpDate() {
		return revisionExpDate;
	}

	public void setRevisionExpDate(Date revisionExpDate) {
		this.revisionExpDate = revisionExpDate;
	}

	public Long getRevisionQmdId() {
		return revisionQmdId;
	}

	public void setRevisionQmdId(Long revisionQmdId) {
		this.revisionQmdId = revisionQmdId;
	}
	
	public boolean isScheduleAIsSetupForEmail() {
		return isScheduleAIsSetupForEmail;
	}

	public void setScheduleAIsSetupForEmail(boolean isScheduleAIsSetupForEmail) {
		this.isScheduleAIsSetupForEmail = isScheduleAIsSetupForEmail;
	}

	public boolean isAmortizationIsSetupForEmail() {
		return isAmortizationIsSetupForEmail;
	}

	public void setAmortizationIsSetupForEmail(boolean isAmortizationIsSetupForEmail) {
		this.isAmortizationIsSetupForEmail = isAmortizationIsSetupForEmail;
	}
       
}
