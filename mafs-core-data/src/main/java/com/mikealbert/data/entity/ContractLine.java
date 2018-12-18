package com.mikealbert.data.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.mikealbert.data.beanvalidation.MANotNull;

/**
* Mapped to CONTRACT_LINES table.
* 
* Note: Not all associations have been mapped.
* 
* @author sibley
*/
@Entity
@Table(name = "CONTRACT_LINES")
public class ContractLine extends BaseEntity implements Serializable  {
	private static final long serialVersionUID = 1L;
	
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CLN_SEQ")    
    @SequenceGenerator(name="CLN_SEQ", sequenceName="CLN_SEQ", allocationSize=1)  
    @Basic(optional = false)
    @NotNull
    @Column(name = "CLN_ID")
    private Long clnId;  
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "POOL_IND")
    private String poolInd;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "REV_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date revDate;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "REV_NO")
    private Long revNo;
    
    @Column(name = "START_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;
    
    @Column(name = "END_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;
    
    @Column(name = "ACCEPTED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date acceptedDate;
    
    @Column(name = "ACTIVE_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date activeDate;
    
    @Column(name = "ACTUAL_END_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date actualEndDate;
    
    @Column(name = "ESTIMATED_END_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date estimatedEndDate;
    
    @Size(max = 7)
    @Column(name = "RECHARGE_CODE")
    private String rechargeCode;
    
    @Size(max = 1)
    @Column(name = "CUST_SELL_IND")
    private String custSellInd;
    
    @Column(name = "LAST_REVIEW_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastReviewDate;
    
    @Size(max = 1)
    @Column(name = "PRE_TERMINATED_IND")
    private String preTerminatedInd;
    
    @Column(name = "OUT_OF_SERVICE_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date outOfServiceDate;
    
    @Column(name = "STOP_BILL_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date stopBillDate;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "PRINTED_IND")
    private String printedInd;
    
    @Column(name = "CHECK_IN_COMPLETE_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date checkInCompleteDate;
    
    @Column(name = "IN_SERV_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date inServDate;
    
    @Column(name = "CHECK_IN_ENTRY_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date checkInEntryDate;
    
    @Size(max = 1)
    @Column(name = "BILLED_TO_ACTUAL_END")
    private String billedToActualEnd;
    
    @Size(max = 1)
    @Column(name = "SYS_STOP_BILL_DATE")
    private String sysStopBillDate;
    
    @Size(max = 10)
    @Column(name = "INVOICE_METHOD")
    private String invoiceMethod;
        
    @JoinColumn(name = "FMS_FMS_ID", referencedColumnName = "FMS_ID")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private FleetMaster fleetMaster; 
    
    @JoinColumn(name = "CON_CON_ID", referencedColumnName = "CON_ID")
    @ManyToOne(optional = false)
    private Contract contract;    
    
    @JoinColumn(name = "DRV_DRV_ID", referencedColumnName = "DRV_ID")
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    private Driver driver; 
    
    @JoinColumn(name = "QMD_QMD_ID", referencedColumnName = "QMD_ID")
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    private QuotationModel quotationModel;    
    
	@OneToMany(mappedBy = "contractLine", fetch = FetchType.LAZY)
    private List<QuotationModel> quotationModelList;   
	
	@Column(name = "ETQ_ETQ_ID")
	private Long	earlyTermQuoteId;
    
    public ContractLine() {}

    public ContractLine(Long clnId) {
        this.setClnId(clnId);
    }

    public ContractLine(Long clnId, String poolInd, Date revDate, Long revNo, String printedInd) {
        this.setClnId(clnId);
        this.setPoolInd(poolInd);
        this.setRevDate(revDate);
        this.setRevNo(revNo);
        this.setPrintedInd(printedInd);
    }

	public Long getClnId() {
		return clnId;
	}

	public void setClnId(Long clnId) {
		this.clnId = clnId;
	}

	public String getPoolInd() {
		return poolInd;
	}

	public void setPoolInd(String poolInd) {
		this.poolInd = poolInd;
	}

	public Date getRevDate() {
		return revDate;
	}

	public void setRevDate(Date revDate) {
		this.revDate = revDate;
	}

	public Long getRevNo() {
		return revNo;
	}

	public void setRevNo(Long revNo) {
		this.revNo = revNo;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getAcceptedDate() {
		return acceptedDate;
	}

	public void setAcceptedDate(Date acceptedDate) {
		this.acceptedDate = acceptedDate;
	}

	public Date getActualEndDate() {
		return actualEndDate;
	}

	public void setActualEndDate(Date actualEndDate) {
		this.actualEndDate = actualEndDate;
	}

	public Date getActiveDate() {
		return activeDate;
	}

	public void setActiveDate(Date activeDate) {
		this.activeDate = activeDate;
	}

	public Date getEstimatedEndDate() {
		return estimatedEndDate;
	}

	public void setEstimatedEndDate(Date estimatedEndDate) {
		this.estimatedEndDate = estimatedEndDate;
	}

	public String getRechargeCode() {
		return rechargeCode;
	}

	public void setRechargeCode(String rechargeCode) {
		this.rechargeCode = rechargeCode;
	}

	public String getCustSellInd() {
		return custSellInd;
	}

	public void setCustSellInd(String custSellInd) {
		this.custSellInd = custSellInd;
	}

	public Date getLastReviewDate() {
		return lastReviewDate;
	}

	public void setLastReviewDate(Date lastReviewDate) {
		this.lastReviewDate = lastReviewDate;
	}

	public String getPreTerminatedInd() {
		return preTerminatedInd;
	}

	public void setPreTerminatedInd(String preTerminatedInd) {
		this.preTerminatedInd = preTerminatedInd;
	}

	public Date getOutOfServiceDate() {
		return outOfServiceDate;
	}

	public void setOutOfServiceDate(Date outOfServiceDate) {
		this.outOfServiceDate = outOfServiceDate;
	}

	public Date getStopBillDate() {
		return stopBillDate;
	}

	public void setStopBillDate(Date stopBillDate) {
		this.stopBillDate = stopBillDate;
	}

	public String getPrintedInd() {
		return printedInd;
	}

	public void setPrintedInd(String printedInd) {
		this.printedInd = printedInd;
	}

	public Date getCheckInCompleteDate() {
		return checkInCompleteDate;
	}

	public void setCheckInCompleteDate(Date checkInCompleteDate) {
		this.checkInCompleteDate = checkInCompleteDate;
	}

	public Date getInServDate() {
		return inServDate;
	}

	public void setInServDate(Date inServDate) {
		this.inServDate = inServDate;
	}

	public Date getCheckInEntryDate() {
		return checkInEntryDate;
	}

	public void setCheckInEntryDate(Date checkInEntryDate) {
		this.checkInEntryDate = checkInEntryDate;
	}

	public String getBilledToActualEnd() {
		return billedToActualEnd;
	}

	public void setBilledToActualEnd(String billedToActualEnd) {
		this.billedToActualEnd = billedToActualEnd;
	}

	public String getSysStopBillDate() {
		return sysStopBillDate;
	}

	public void setSysStopBillDate(String sysStopBillDate) {
		this.sysStopBillDate = sysStopBillDate;
	}

	public String getInvoiceMethod() {
		return invoiceMethod;
	}

	public void setInvoiceMethod(String invoiceMethod) {
		this.invoiceMethod = invoiceMethod;
	}

	public FleetMaster getFleetMaster() {
		return fleetMaster;
	}

	public void setFleetMaster(FleetMaster fleetMaster) {
		this.fleetMaster = fleetMaster;
	}

	public Contract getContract() {
		return contract;
	}

	public void setContract(Contract contract) {
		this.contract = contract;
	}
    
    public Driver getDriver() {
		return driver;
	}

	public void setDriver(Driver driver) {
		this.driver = driver;
	}

	public QuotationModel getQuotationModel() {
		return quotationModel;
	}

	public void setQuotationModel(QuotationModel quotationModel) {
		this.quotationModel = quotationModel;
	}

	public List<QuotationModel> getQuotationModelList() {
		return quotationModelList;
	}

	public void setQuotationModelList(List<QuotationModel> quotationModelList) {
		this.quotationModelList = quotationModelList;
	}
	

	public Long getEarlyTermQuoteId() {
		return earlyTermQuoteId;
	}

	public void setEarlyTermQuoteId(Long earlyTermQuoteId) {
		this.earlyTermQuoteId = earlyTermQuoteId;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (clnId != null ? clnId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ContractLine)) {
            return false;
        }
        ContractLine other = (ContractLine) object;
        if ((this.clnId == null && other.clnId != null) || (this.clnId != null && !this.clnId.equals(other.clnId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mikealbert.entity.ContractLines[ clnId=" + clnId + " ]";
    }    
}
