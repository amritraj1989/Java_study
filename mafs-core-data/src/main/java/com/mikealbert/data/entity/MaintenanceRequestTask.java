package com.mikealbert.data.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 * Mapped to MAINTENANCE_REQUEST_TASKS Table
 * @author sibley
 */
@Entity
@Table(name = "MAINTENANCE_REQUEST_TASKS")
public class MaintenanceRequestTask extends BaseEntity implements Serializable, Cloneable  {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="MRT_SEQ")    
    @SequenceGenerator(name="MRT_SEQ", sequenceName="MRT_SEQ", allocationSize=1)
    @NotNull
    @Column(name = "MRT_ID")
    private Long mrtId;
        
    @Column(name = "TASK_QTY")
    private BigDecimal taskQty;
    
    /**
     * Historically we have had 2 columns to keep track of the description of the line in the PO
     * WORK_TO_BE_DONE and VENDOR_CODE_DESC. Going forward we only need one and VENDOR_CODE_DESC
     * is the preferred column for storage and retrieval.
     */
    @Deprecated
    @Column(name = "WORK_TO_BE_DONE")
    private String workToBeDone;
    
    @Column(name = "LABOUR_TIME")
    private Long laborTime;
    
    @Column(name = "TASK_UNIT_COST")
    private BigDecimal unitCost;
    
    @Column(name = "TASK_TOTAL_COST")
    private BigDecimal totalCost; 
    
    @Column(name = "RECH_QTY")
    private BigDecimal rechargeQty; 
    
    @Column(name = "RECH_UNIT_COST")
    private BigDecimal rechargeUnitCost;      
    
    @Column(name = "RECH_TOTAL_COST")
    private BigDecimal rechargeTotalCost;  
    
    @NotNull
    @Column(name = "RECHARGE_FLAG")
    private String rechargeFlag;
    
    @Size(max = 10)
    @Column(name = "RECHARGE_CODE")
    private String rechargeCode; 
     
    @Column(name = "RECH_UPLIFT")
    private BigDecimal markUpAmount;     
    
    @Column(name = "MCG_MAINT_CAT_CODE")
    private String maintCatCode;
    
    @JoinColumn(name = "MAINT_CODE", referencedColumnName = "MAINT_CODE")
    @ManyToOne(optional = true)
    private MaintenanceCode maintenanceCode;  
          
    @Size(max=1)
    @Column(name = "DISCOUNT_IND")
    private String discountFlag; 
    
    @Column(name = "MAINT_REQ_LINES")
    private Long lineNumber;      
    
    @Size(min=1, max=30)
    @Column(name = "AUTHORIZE_PERSON")
    private String authorizePerson;
    
    @Column(name = "AUTHORIZE_DATE")
    private Date authorizeDate;
        
    @NotNull
    @Column(name = "OUTSTANDING", length = 1)
    private String outstanding;

    @NotNull
    @Column(name = "WAS_OUTSTANDING", length = 1)
    private String wasOutstanding;    
    
    @Column(name = "VENDOR_CODE_DESC")
    private String maintenanceCodeDesc;
    
    @Size(max=25)
    @Column(name = "ACCOUNT_CODE")
    private String payeeAccountCode;  
    
    @Size(max=1)
    @Column(name = "ACCOUNT_TYPE")
    private String payeeAccountType;  
    
    @Column(name = "C_ID")
    private Long payeeCorporateId;   
    
    @Column(name = "PLANNED_DATE")
    private Date plannedDate; 
    
    @Size(max=10)
    @Column(name = "COST_AVOIDANCE_CODE")
    private String costAvoidanceCode;  
    
    @Size(max=80)
    @Column(name = "COST_AVOIDANCE_DESC")
    private String costAvoidanceDescription; 
    
    @Column(name = "INT_COST")
    private BigDecimal costAvoidanceAmount;     
        
    @Size(max=80)
    @Column(name = "GOODWILL_REASON")
    private String goodwillReason;     

    @Column(name = "GOODWILL_COST")
    private BigDecimal goodwillCost; 
    
    @Column(name = "GOODWILL_PERC")
    private BigDecimal goodwillPercent; 
        
    @Column(name = "FMS_FMS_ID")
    private Long fmsFmsId;
    
    @Size(max=10)
    @Column(name = "MAINT_REPAIR_REASON_CODE")
    private String maintenanceRepairReasonCode;      
    
    @Column(name = "ACT_TASK_COST")
    private BigDecimal actualTaskCost; 
    
    @Column(name = "ACT_RECH_COST")
    private BigDecimal actualRechargeCost;     
    
    @Column(name = "DAC_DAC_ID")
    private Long dacDacId;
    
    @Transient
    private boolean costAvoidanceIndicator;

	@Transient
    private int index;
	
	@Transient
	private String serviceProviderCodeLookup;
	    
    @JoinColumn(name = "MRQ_MRQ_ID", referencedColumnName = "MRQ_ID")
    @ManyToOne(optional = false)
    private MaintenanceRequest maintenanceRequest;	
	
    @JoinColumn(name = "SML_SML_ID", referencedColumnName = "SML_ID")
    @ManyToOne(optional = true)
    private ServiceProviderMaintenanceCode serviceProviderMaintenanceCode;   
    
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "maintenanceRequestTask", cascade=CascadeType.ALL)
    private List<MaintenanceCategoryPropertyValue> maintenanceCategoryPropertyValues;    
    
    // Added for Bug 16387
    @Transient
    private BigDecimal actualInvoiceAmount;
    
    public MaintenanceRequestTask() {}
    
    public Long getMrtId() {
		return mrtId;
	}


	public void setMrtId(Long mrtId) {
		this.mrtId = mrtId;
	}

	public BigDecimal getTaskQty() {
		return taskQty;
	}

	public void setTaskQty(BigDecimal taskQty) {
		this.taskQty = taskQty;
	}

	@Deprecated
	public String getWorkToBeDone() {
		return workToBeDone;
	}

    @Deprecated
	public void setWorkToBeDone(String workToBeDone) {
		this.workToBeDone = workToBeDone;
	}


	public Long getLaborTime() {
		return laborTime;
	}


	public void setLaborTime(Long laborTime) {
		this.laborTime = laborTime;
	}


	public BigDecimal getUnitCost() {
		return unitCost;
	}


	public void setUnitCost(BigDecimal unitCost) {
		this.unitCost = unitCost;
	}


	public BigDecimal getTotalCost() {
		return totalCost;
	}


	public void setTotalCost(BigDecimal totalCost) {
		this.totalCost = totalCost;
	}

	public BigDecimal getRechargeQty() {
		return rechargeQty;
	}

	public void setRechargeQty(BigDecimal rechargeQty) {
		this.rechargeQty = rechargeQty;
	}

	public BigDecimal getRechargeUnitCost() {
		return rechargeUnitCost;
	}

	public void setRechargeUnitCost(BigDecimal rechargeUnitCost) {
		this.rechargeUnitCost = rechargeUnitCost;
	}

	public String getMaintCatCode() {
		return maintCatCode;
	}


	public void setMaintCatCode(String maintCatCode) {
		this.maintCatCode = maintCatCode;
	}


	public MaintenanceRequest getMaintenanceRequest() {
		return maintenanceRequest;
	}


	public void setMaintenanceRequest(MaintenanceRequest maintenanceRequest) {
		this.maintenanceRequest = maintenanceRequest;
	}

	public MaintenanceCode getMaintenanceCode() {
		return maintenanceCode;
	}


	public void setMaintenanceCode(MaintenanceCode maintenanceCode) {
		this.maintenanceCode = maintenanceCode;
	}

	public BigDecimal getRechargeTotalCost() {
		return rechargeTotalCost;
	}


	public void setRechargeTotalCost(BigDecimal rechargeTotalCost) {
		this.rechargeTotalCost = rechargeTotalCost;
	}

	public String getRechargeFlag() {
		return rechargeFlag;
	}

	public void setRechargeFlag(String rechargeFlag) {
		this.rechargeFlag = rechargeFlag;
	}


	public String getRechargeCode() {
		return rechargeCode;
	}


	public void setRechargeCode(String rechargeCode) {
		this.rechargeCode = rechargeCode;
	}
	
	

	public BigDecimal getMarkUpAmount() {
		return markUpAmount;
	}


	public void setMarkUpAmount(BigDecimal markUpAmount) {
		this.markUpAmount = markUpAmount;
	}


	public String getDiscountFlag() {
		return discountFlag;
	}


	public void setDiscountFlag(String discountFlag) {
		this.discountFlag = discountFlag;
	}


	public Long getLineNumber() {
		return lineNumber;
	}


	public void setLineNumber(Long lineNumber) {
		this.lineNumber = lineNumber;
	}



	public String getAuthorizePerson() {
		return authorizePerson;
	}


	public void setAuthorizePerson(String authorizePerson) {
		this.authorizePerson = authorizePerson;
	}


	public Date getAuthorizeDate() {
		return authorizeDate;
	}

	public void setAuthorizeDate(Date authorizeDate) {
		this.authorizeDate = authorizeDate;
	}

	public String getOutstanding() {
		return outstanding;
	}


	public void setOutstanding(String outstanding) {
		this.outstanding = outstanding;
	}


	public String getWasOutstanding() {
		return wasOutstanding;
	}


	public void setWasOutstanding(String wasOutstanding) {
		this.wasOutstanding = wasOutstanding;
	}

	public int getIndex() {
		return index;
	}


	public void setIndex(int index) {
		this.index = index;
	}

    public String getMaintenanceCodeDesc() {
		return maintenanceCodeDesc;
	}

	public void setMaintenanceCodeDesc(String maintenanceCodeDesc) {
		this.maintenanceCodeDesc = maintenanceCodeDesc;
		this.workToBeDone = maintenanceCodeDesc;
	}

	public String getPayeeAccountCode() {
		return payeeAccountCode;
	}

    public void setPayeeAccountCode(String payeeAccountCode) {
		this.payeeAccountCode = payeeAccountCode;
	}

    public String getPayeeAccountType() {
		return payeeAccountType;
	}

    public void setPayeeAccountType(String payeeAccountType) {
		this.payeeAccountType = payeeAccountType;
	}

	public Long getPayeeCorporateId() {
		return payeeCorporateId;
	}

	public void setPayeeCorporateId(Long payeeCorporateId) {
		this.payeeCorporateId = payeeCorporateId;
	}

	public Date getPlannedDate() {
		return plannedDate;
	}

	public void setPlannedDate(Date plannedDate) {
		this.plannedDate = plannedDate;
	}

	public String getCostAvoidanceCode() {
		return costAvoidanceCode;
	}

	public void setCostAvoidanceCode(String costAvoidanceCode) {
		this.costAvoidanceCode = costAvoidanceCode;
	}

	public String getCostAvoidanceDescription() {
		return costAvoidanceDescription;
	}

	public void setCostAvoidanceDescription(String costAvoidanceDescription) {
		this.costAvoidanceDescription = costAvoidanceDescription;
	}

	public BigDecimal getCostAvoidanceAmount() {
		return costAvoidanceAmount;
	}

	public void setCostAvoidanceAmount(BigDecimal costAvoidanceAmount) {
		this.costAvoidanceAmount = costAvoidanceAmount;
	}

	public String getGoodwillReason() {
		return goodwillReason;
	}

	public void setGoodwillReason(String goodwillReason) {
		this.goodwillReason = goodwillReason;
	}

	public BigDecimal getGoodwillCost() {
		return goodwillCost;
	}

	public void setGoodwillCost(BigDecimal goodwillCost) {
		this.goodwillCost = goodwillCost;
	}

	public BigDecimal getGoodwillPercent() {
		return goodwillPercent;
	}

	public void setGoodwillPercent(BigDecimal goodwillPercent) {
		this.goodwillPercent = goodwillPercent;
	}

	public Long getFmsFmsId() {
		return fmsFmsId;
	}

	public void setFmsFmsId(Long fmsFmsId) {
		this.fmsFmsId = fmsFmsId;
	}

	public String getMaintenanceRepairReasonCode() {
		return maintenanceRepairReasonCode;
	}

	public void setMaintenanceRepairReasonCode(
			String maintenanceRepairReasonCode) {
		this.maintenanceRepairReasonCode = maintenanceRepairReasonCode;
	}

	public BigDecimal getActualTaskCost() {
		return actualTaskCost;
	}

	public void setActualTaskCost(BigDecimal actualTaskCost) {
		this.actualTaskCost = actualTaskCost;
	}

	public BigDecimal getActualRechargeCost() {
		return actualRechargeCost;
	}

	public void setActualRechargeCost(BigDecimal actualRechargeCost) {
		this.actualRechargeCost = actualRechargeCost;
	}

	public boolean isCostAvoidanceIndicator() {
		return costAvoidanceIndicator;
	}

	public void setCostAvoidanceIndicator(boolean costAvoidanceIndicator) {
		this.costAvoidanceIndicator = costAvoidanceIndicator;
	}

	public ServiceProviderMaintenanceCode getServiceProviderMaintenanceCode() {
		return serviceProviderMaintenanceCode;
	}

	public void setServiceProviderMaintenanceCode(
			ServiceProviderMaintenanceCode serviceProviderMaintenanceCode) {
		this.serviceProviderMaintenanceCode = serviceProviderMaintenanceCode;
	}

	public List<MaintenanceCategoryPropertyValue> getMaintenanceCategoryPropertyValues() {
		return maintenanceCategoryPropertyValues;
	}

	public void setMaintenanceCategoryPropertyValues(
			List<MaintenanceCategoryPropertyValue> maintenanceCategoryPropertyValues) {
		this.maintenanceCategoryPropertyValues = maintenanceCategoryPropertyValues;
	}

	public String getServiceProviderCodeLookup() {
		return serviceProviderCodeLookup;
	}

	public void setServiceProviderCodeLookup(String serviceProviderCodeLookup) {
		this.serviceProviderCodeLookup = serviceProviderCodeLookup;
	}


    public Long getDacDacId() {
		return dacDacId;
	}

	public void setDacDacId(Long dacDacId) {
		this.dacDacId = dacDacId;
	}
    // Added for Bug 16387
	public BigDecimal getActualInvoiceAmount() {
		return actualInvoiceAmount;
	}

	public void setActualInvoiceAmount(BigDecimal actualInvoiceAmount) {
		this.actualInvoiceAmount = actualInvoiceAmount;
	}
    
	@Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MaintenanceRequestTask)) {
            return false;
        }
        MaintenanceRequestTask other = (MaintenanceRequestTask) object;
        if ((this.getMrtId() == null && other.getMrtId() != null) || (this.getMrtId() != null && other.getMrtId() == null) || (this.getMrtId() != null && other.getMrtId() != null && !this.getMrtId().equals(other.getMrtId()))) {
            return false;
        }        
        if ((this.getDiscountFlag() == null && other.getDiscountFlag() != null ) || ( this.getDiscountFlag() != null && !this.getDiscountFlag().equals(other.getDiscountFlag())) ){ 
        	return false;
        }
        if ((this.getMaintCatCode() == null && other.getMaintCatCode() != null ) || ( this.getMaintCatCode() != null && !this.getMaintCatCode().equals(other.getMaintCatCode())) ){ 
        	return false;
        }    
        if ((this.getMaintenanceCode() == null && other.getMaintenanceCode() != null ) || ( this.getMaintenanceCode() != null && !this.getMaintenanceCode().equals(other.getMaintenanceCode())) ){ 
        	return false;
        }
        if ((this.getRechargeCode() == null && other.getRechargeCode() != null ) || ( this.getRechargeCode() != null && !this.getRechargeCode().equals(other.getRechargeCode())) ){ 
        	return false;
        }
        if ((this.getRechargeFlag() == null && other.getRechargeFlag() != null ) || ( this.getRechargeFlag() != null && !this.getRechargeFlag().equals(other.getRechargeFlag())) ){ 
        	return false;
        }
        if ((this.getRechargeTotalCost() == null && other.getRechargeTotalCost() != null ) || (this.getRechargeTotalCost() != null && other.getRechargeTotalCost() == null ) || ( this.getRechargeTotalCost() != null && other.getRechargeTotalCost() != null && (this.getRechargeTotalCost().compareTo(other.getRechargeTotalCost()) != 0))){ 
        	return false;
        } 
        if ((this.getMarkUpAmount() == null && other.getMarkUpAmount() != null ) || (this.getMarkUpAmount() != null && other.getMarkUpAmount() == null ) || ( this.getMarkUpAmount() != null && other.getMarkUpAmount() != null && (this.getMarkUpAmount().compareTo(other.getMarkUpAmount()) != 0))){ 
        	return false;
        }        
        if (this.getTaskQty().compareTo(other.getTaskQty()) != 0){ 
        	return false;
        }
        if ((this.getUnitCost() == null && other.getUnitCost() != null ) || (this.getUnitCost() != null && other.getUnitCost() == null ) || ( this.getUnitCost() != null && other.getUnitCost() != null && (this.getUnitCost().compareTo(other.getUnitCost()) != 0 ))){ 
        	return false;
        }
        if ((this.getTotalCost() == null && other.getTotalCost() != null ) || (this.getTotalCost() != null && other.getTotalCost() == null ) || ( this.getTotalCost() != null && other.getTotalCost() != null && (this.getTotalCost().compareTo(other.getTotalCost()) != 0))){ 
        	return false;
        }  
        
        if ((this.getWorkToBeDone() == null && other.getWorkToBeDone() != null ) || ( this.getWorkToBeDone() != null && !this.getWorkToBeDone().equals(other.getWorkToBeDone())) ){ 
        	return false;
        } 
        if ((this.getMaintenanceCodeDesc() == null && other.getMaintenanceCodeDesc() != null ) || ( this.getMaintenanceCodeDesc() != null && !this.getMaintenanceCodeDesc().equals(other.getMaintenanceCodeDesc())) ){ 
        	return false;
        }
        if ((this.getCostAvoidanceCode() == null && other.getCostAvoidanceCode() != null ) || ( this.getCostAvoidanceCode() != null && !this.getCostAvoidanceCode().equals(other.getCostAvoidanceCode())) ){ 
        	return false;
        }  
        if ((this.getCostAvoidanceAmount() == null && other.getCostAvoidanceAmount() != null ) || ( this.getCostAvoidanceAmount() != null && other.getCostAvoidanceAmount() == null ) || ( this.getCostAvoidanceAmount() != null && other.getCostAvoidanceAmount() != null && this.getCostAvoidanceAmount().compareTo(other.getCostAvoidanceAmount()) != 0) ){ 
        	return false;
        }  
                
        if ((this.getGoodwillReason() == null && other.getGoodwillReason() != null ) || ( (this.getGoodwillReason() != null && this.getGoodwillReason().length() != 0)  && !this.getGoodwillReason().equals(other.getGoodwillReason())) ){ 
        	return false;
        }  
        if ((this.getGoodwillCost() == null && other.getGoodwillCost() != null ) || ( this.getGoodwillCost() != null && !this.getGoodwillCost().equals(other.getGoodwillCost())) ){ 
        	return false;
        }           
        if ((this.getGoodwillPercent() == null && other.getGoodwillPercent() != null ) || ( this.getGoodwillPercent() != null && !this.getGoodwillPercent().equals(other.getGoodwillPercent())) ){ 
        	return false;
        } 
        if ((this.getMaintenanceRepairReasonCode() == null && other.getMaintenanceRepairReasonCode() != null ) || ( this.getMaintenanceRepairReasonCode() != null && !this.getMaintenanceRepairReasonCode().equals(other.getMaintenanceRepairReasonCode())) ){ 
        	return false;
        }        

        return true;
    }
	@Override
	public int hashCode(){
		
		int hash = 0;
		
		hash += (getMrtId() != null)? getMrtId().intValue() : 0 ;
		hash += (getDiscountFlag() != null)? getDiscountFlag().hashCode() : 0 ;
		hash += (getMaintCatCode() != null)? getMaintCatCode().hashCode() : 0 ;
		
		hash += (getMaintenanceCode() != null)? getMaintenanceCode().hashCode() : 0 ;
		hash += (getRechargeCode() != null)? getRechargeCode().hashCode() : 0 ;
		hash += (getRechargeFlag() != null)? getRechargeFlag().hashCode() : 0 ;
		hash += (getRechargeTotalCost() != null)? getRechargeTotalCost().hashCode() : 0 ;
		hash += getTaskQty().intValue();
		hash += (getUnitCost() != null)? getUnitCost().hashCode() : 0 ;
		hash += (getTotalCost() != null)? getTotalCost().hashCode() : 0 ;
		
		hash += (getWorkToBeDone() != null)? getWorkToBeDone().hashCode() : 0 ;
		hash += (getMaintenanceCodeDesc() != null)? getMaintenanceCodeDesc().hashCode() : 0 ;
		
		hash += (getMaintenanceRepairReasonCode() != null)? getMaintenanceRepairReasonCode().hashCode() : 0 ;
		
		return hash;
	}
	
    @Override
    public String toString() {
        return "com.mikealbert.data.entity.MaintenanceRequestTask[ mrtId=" + getMrtId() + " ]";
    }
}
