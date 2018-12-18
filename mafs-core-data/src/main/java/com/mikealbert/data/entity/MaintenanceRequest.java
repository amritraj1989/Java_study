package com.mikealbert.data.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.mikealbert.data.beanvalidation.MANotNull;
import com.mikealbert.data.beanvalidation.MASize;
import com.mikealbert.util.MALUtilities;

/**
 * Mapped to MAINTENANCE_REQUEST Table
 * @author sibley
 */
@Entity
@Table(name = "MAINTENANCE_REQUESTS")
public class MaintenanceRequest extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="MRQ_SEQ")    
    @SequenceGenerator(name="MRQ_SEQ", sequenceName="MRQ_SEQ", allocationSize=1)
    @NotNull
    @Column(name = "MRQ_ID")
    private Long mrqId;
    
    @Column(name = "MRQ_MRQ_ID")
    private Long mrqMrqId;    
    
    @NotNull
    @Column(name = "MAINT_REQ_DATE")
    @Temporal(TemporalType.TIMESTAMP)    
    private Date maintReqDate;
    
    @MANotNull(label = "Odo")
    @Column(name = "CURRENT_ODO")
    private Long currentOdo;
    
    @MANotNull(label = "PO Status")
    @Column(name = "MAINT_REQUEST_STATUS")
    private String maintReqStatus;
    
    @MANotNull(label = "PO Type")
    @Column(name = "MAINT_REUEST_TYPE")
    private String maintReqType;
    
    @Column(name = "PLANNED_START_DATE")
    private Date plannedStartDate;
    
    @MANotNull(label = "Planned End Date")
    @Column(name = "PLANNED_END_DATE")
    private Date plannedEndDate;
    
    @MANotNull(label = "Actual Start Date")
    @Column(name = "ACTUAL_START_DATE")
    private Date actualStartDate;
    
    @Column(name = "ACTUAL_END_DATE")
    private Date actualEndDate;
    
    @Size(max = 30)
    @Column(name = "SUP_CONTACT_NAME")
    private String serviceProviderContactName;
    
    @MASize(label = "Job No", min = 1, max = 25)
    @Column(name = "JOB_NO")
    private String jobNo;  
    
    @Size(max = 25)
    @Column(name = "CLIENT_PO_NUMBER")
    private String clientPoNo; 
    
    @Size(max = 25)
    @NotNull
    @Column(name = "AUTH_BY")
    private String authBy;
    
    @Size(max = 25)
    @NotNull
    @Column(name = "CREATED_BY")
    private String createdBy;    
    
    @Size(max = 25)
    @NotNull
    @Column(name = "LAST_CHANGED_BY")
    private String lastChangedBy;      
    
    @Column(name = "LAST_CHANGED_DATE")
    private Date lastChangedDate;      
    
	@Size(max = 120)
    @NotNull
    @Column(name = "AUTHN_MESSAGE")
    private String authMessage;    
    
    @Size(max = 100)    
    @Column(name = "PAYEE_INVOICE_NO")
    private String payeeInvoiceNumber;  
        
    @Size(max = 25)
    @Column(name = "COUPON_BOOK_REF")
    private String couponBookReference; 
    
    @Size(max = 80)
    @Column(name = "AUTH_REFERENCE")
    private String authReference; 
    
    @Column(name = "MARK_UP_AMOUNT")
    private BigDecimal markUpAmount; 
    
    @Size(max = 10)
    @Column(name = "MAINT_REQUEST_CLASS")
    private String requestClass; 
    
    @Size(max = 25)
    @Column(name = "REPLACEMENT_UNIT_NO")
    private String replacementUnitNo;
    
    @Column(name = "REPLACEMENT_UNIT_DATE")
    private Date replacementUnitDate; 
    
    @Size(max = 40)
    @Column(name = "REPLACEMENT_UNIT_DATE_TYPE")
    private String replacementUnitDateType;    
    
    @Size(max = 10)
    @Column(name = "MAINT_REVISION_STATUS")
    private String revisionStatus;
           
    @Size(max = 30)
    @Column(name = "ORIG_CONTROLLER")
    private String origController; 
    
    @Column(name = "AUTHN_CREATED")
    private Date authnCreated;  

    @Size(max = 1)
    @Column(name = "SUPPLIER_NETWORK_MARKUP_IND")
    private String serviceProviderMarkupInd; 
    
    @Transient 
    private boolean goodwillIndicator;
    

    @Column(name = "C_ID",insertable=false, updatable=false)
    private Long cId;
    
    @Column(name = "ACCOUNT_TYPE",insertable=false, updatable=false)
    private String accountType;
    
    @Column(name = "ACCOUNT_CODE",insertable=false, updatable=false)
    private String accountCode;
    
    @JoinColumns({
        @JoinColumn(name = "C_ID", referencedColumnName = "C_ID"),
        @JoinColumn(name = "ACCOUNT_TYPE", referencedColumnName = "ACCOUNT_TYPE"),
        @JoinColumn(name = "ACCOUNT_CODE", referencedColumnName = "ACCOUNT_CODE")})
    @ManyToOne(fetch = FetchType.LAZY)
    private ExternalAccount payeeAccount;
    
    @JoinColumn(name = "FMS_FMS_ID", referencedColumnName = "FMS_ID")
    @ManyToOne(optional = false)
    private FleetMaster fleetMaster;
    
    @JoinColumn(name = "ODO_UOM_CODE", referencedColumnName = "UOM_CODE")
    @ManyToOne(optional = false)
    private UomCode unitofMeasureCode;
    
    @JoinColumn(name="SUP_SUP_ID", referencedColumnName = "SUP_ID")
    @ManyToOne(optional = false)
    private ServiceProvider serviceProvider;  
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "maintenanceRequest", fetch = FetchType.EAGER, orphanRemoval = true)    
    @OrderBy("mrtId ASC")
    private List<MaintenanceRequestTask> maintenanceRequestTasks;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "maintenanceRequest", fetch = FetchType.LAZY)
    private List<AccessoryMaintJobActivation> accessoryMaintJobActivations;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "maintenanceRequest", fetch = FetchType.LAZY)
    private List<ServicesDue> servicesDues;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "maintenanceRequest", fetch = FetchType.LAZY)
    private List<DiaryEntry> diaries;
    
    @OrderBy("fnoId DESC")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "maintenanceRequest", fetch = FetchType.LAZY)
    private List<FleetNotes> fleetNotes;
    
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "maintenanceRequest")
    private List<MaintenanceRequestUser> maintRequestUsers;
    
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "maintenanceRequest")
    private List<OdometerReading> odometerReadings;
    
    @Size(max = 800)
    @Column(name = "SUPPLIER_CONTACT_INFO", nullable = true)
    private String serviceProviderContactInfo;
    
    @Size(max = 40)
    @Column(name = "REASON_FOR_SERVICE", nullable = true)
    private String reasonForService;

    public MaintenanceRequest() {}

    public Long getMrqId() {
		return mrqId;
	}

	public void setMrqId(Long mrqId) {
		this.mrqId = mrqId;
	}

	public Long getMrqMrqId() {
		return mrqMrqId;
	}

	public void setMrqMrqId(Long mrqMrqId) {
		this.mrqMrqId = mrqMrqId;
	}

	public Date getMaintReqDate() {
		return maintReqDate;
	}

	public void setMaintReqDate(Date maintReqDate) {
		this.maintReqDate = maintReqDate;
	}

	public Long getCurrentOdo() {
		return currentOdo;
	}

	public void setCurrentOdo(Long currentOdo) {
		this.currentOdo = currentOdo;
	}

	public String getJobNo() {
		return jobNo;
	}

	public void setJobNo(String jobNo) {
		this.jobNo = jobNo;
	}

	public String getClientPoNo() {
		return clientPoNo;
	}

	public void setClientPoNo(String clientPoNo) {
		this.clientPoNo = clientPoNo;
	}

	public String getAuthBy() {
		return authBy;
	}

	public void setAuthBy(String authBy) {
		this.authBy = authBy;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getLastChangedBy() {
		return lastChangedBy;
	}

	public void setLastChangedBy(String lastChangedBy) {
		this.lastChangedBy = lastChangedBy;
	}
	
    public Date getLastChangedDate() {
		return lastChangedDate;
	}

	public void setLastChangedDate(Date lastChangedDate) {
		this.lastChangedDate = lastChangedDate;
	}

	public String getAuthMessage() {
		return authMessage;
	}

	public void setAuthMessage(String authMessage) {
		this.authMessage = authMessage;
	}

	public ExternalAccount getPayeeAccount() {
		return payeeAccount;
	}

	public void setPayeeAccount(ExternalAccount payeeAccount) {
		this.payeeAccount = payeeAccount;
	}

	public FleetMaster getFleetMaster() {
		return fleetMaster;
	}

	public void setFleetMaster(FleetMaster fleetMaster) {
		this.fleetMaster = fleetMaster;
	}
	
	public String getMaintReqStatus() {
		return maintReqStatus;
	}

	public void setMaintReqStatus(String maintReqStatus) {
		this.maintReqStatus = maintReqStatus;
	}

	public String getMaintReqType() {
		return maintReqType;
	}

	public void setMaintReqType(String maintReqType) {
		this.maintReqType = maintReqType;
	}

	public Date getPlannedStartDate() {
		return plannedStartDate;
	}

	public void setPlannedStartDate(Date plannedStartDate) {
		this.plannedStartDate = plannedStartDate;
	}

	public Date getPlannedEndDate() {
		return plannedEndDate;
	}

	public void setPlannedEndDate(Date plannedEndDate) {
		this.plannedEndDate = plannedEndDate;
	}

	public Date getActualStartDate() {
		return actualStartDate;
	}

	public void setActualStartDate(Date actualStartDate) {
		this.actualStartDate = actualStartDate;
	}

	public Date getActualEndDate() {
		return actualEndDate;
	}

	public void setActualEndDate(Date actualEndDate) {
		this.actualEndDate = actualEndDate;
	}

	public UomCode getUnitofMeasureCode() {
		return unitofMeasureCode;
	}

	public void setUnitofMeasureCode(UomCode unitofMeasureCode) {
		this.unitofMeasureCode = unitofMeasureCode;
	}

	public ServiceProvider getServiceProvider() {
		return serviceProvider;
	}

	public void setServiceProvider(ServiceProvider serviceProvider) {
		this.serviceProvider = serviceProvider;
	}
	
	public String getServiceProviderContactName() {
		return serviceProviderContactName;
	}

	public void setServiceProviderContactName(String serviceProviderContactName) {
		this.serviceProviderContactName = serviceProviderContactName;
	}

	public List<MaintenanceRequestTask> getMaintenanceRequestTasks() {
		return maintenanceRequestTasks;
	}

	public void setMaintenanceRequestTasks(List<MaintenanceRequestTask> maintenanceRequestTasks) {
		this.maintenanceRequestTasks = maintenanceRequestTasks;
	}

	public String getPayeeInvoiceNumber() {
		return payeeInvoiceNumber;
	}

	public void setPayeeInvoiceNumber(String payeeInvoiceNumber) {
		this.payeeInvoiceNumber = payeeInvoiceNumber;
	}

	public String getCouponBookReference() {
		return couponBookReference;
	}

	public void setCouponBookReference(String couponBookReference) {
		this.couponBookReference = couponBookReference;
	}
	
	public String getAuthReference() {
		return authReference;
	}

	public void setAuthReference(String authReference) {
		this.authReference = authReference;
	}

	public BigDecimal getMarkUpAmount() {
		return markUpAmount;
	}

	public void setMarkUpAmount(BigDecimal markUpAmount) {
		this.markUpAmount = markUpAmount;
	}

	public String getRequestClass() {
		return requestClass;
	}

	public void setRequestClass(String requestClass) {
		this.requestClass = requestClass;
	}

	public String getReplacementUnitNo() {
		return replacementUnitNo;
	}

	public void setReplacementUnitNo(String replacementUnitNo) {
		this.replacementUnitNo = replacementUnitNo;
	}

	public Date getReplacementUnitDate() {
		return replacementUnitDate;
	}

	public void setReplacementUnitDate(Date replacementUnitDate) {
		this.replacementUnitDate = replacementUnitDate;
	}

	public String getReplacementUnitDateType() {
		return replacementUnitDateType;
	}

	public void setReplacementUnitDateType(String replacementUnitDateType) {
		this.replacementUnitDateType = replacementUnitDateType;
	}

	public String getRevisionStatus() {
		return revisionStatus;
	}

	public void setRevisionStatus(String revisionStatus) {
		this.revisionStatus = revisionStatus;
	}

	public String getOrigController() {
		return origController;
	}

	public void setOrigController(String origController) {
		this.origController = origController;
	}

	public Date getAuthnCreated() {
		return authnCreated;
	}

	public void setAuthnCreated(Date authnCreated) {
		this.authnCreated = authnCreated;
	}

	public String getServiceProviderMarkupInd() {
		return serviceProviderMarkupInd;
	}

	public void setServiceProviderMarkupInd(String serviceProviderMarkupInd) {
		this.serviceProviderMarkupInd = serviceProviderMarkupInd;
	}

	public boolean isGoodwillIndicator() {
		return goodwillIndicator;
	}

	public void setGoodwillIndicator(boolean goodwillIndicator) {
		this.goodwillIndicator = goodwillIndicator;
	}

	public String getServiceProviderContactInfo() {
		return serviceProviderContactInfo;
	}

	public void setServiceProviderContactInfo(String serviceProviderContactInfo) {
		this.serviceProviderContactInfo = serviceProviderContactInfo;
	}

	public String getReasonForService() {
		return reasonForService;
	}

	public void setReasonForService(String reasonForService) {
		this.reasonForService = reasonForService;
	}	
	
	public List<AccessoryMaintJobActivation> getAccessoryMaintJobActivations() {
		return accessoryMaintJobActivations;
	}

	public void setAccessoryMaintJobActivations(
			List<AccessoryMaintJobActivation> accessoryMaintJobActivations) {
		this.accessoryMaintJobActivations = accessoryMaintJobActivations;
	}
	
	public List<ServicesDue> getServicesDues() {
		return servicesDues;
	}

	public void setServicesDues(List<ServicesDue> servicesDues) {
		this.servicesDues = servicesDues;
	}

	public List<DiaryEntry> getDiaries() {
		return diaries;
	}

	public void setDiaries(List<DiaryEntry> diaries) {
		this.diaries = diaries;
	}

	public List<FleetNotes> getFleetNotes() {
		return fleetNotes;
	}

	public void setFleetNotes(List<FleetNotes> fleetNotes) {
		this.fleetNotes = fleetNotes;
	}

	public List<MaintenanceRequestUser> getMaintRequestUsers() {
		return maintRequestUsers;
	}

	public void setMaintRequestUsers(List<MaintenanceRequestUser> maintRequestUsers) {
		this.maintRequestUsers = maintRequestUsers;
	}

	public List<OdometerReading> getOdometerReadings() {
		return odometerReadings;
	}

	public void setOdometerReadings(List<OdometerReading> odometerReadings) {
		this.odometerReadings = odometerReadings;
	}

	public Long getcId() {
		return cId;
	}

	public void setcId(Long cId) {
		this.cId = cId;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public String getAccountCode() {
		return accountCode;
	}

	public void setAccountCode(String accountCode) {
		this.accountCode = accountCode;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (getMrqId() != null ? getMrqId().hashCode() : 0);
        return hash;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MaintenanceRequest)) {
            return false;
        }
        MaintenanceRequest other = (MaintenanceRequest) object;
        String blankValue = "";
        if ((this.getMrqId() == null && other.getMrqId() != null) || (this.getMrqId() != null && !this.getMrqId().equals(other.getMrqId()))) {
            return false;
        }
        
        //if (!this.getServiceProvider().getServiceProviderNumber().equals(other.getServiceProvider().getServiceProviderNumber())) {
        if ((this.getServiceProvider() == null && other.getServiceProvider() != null) || (other.getServiceProvider() == null && this.getServiceProvider() == null )
        		|| (this.getServiceProvider() != null && this.getServiceProvider().getServiceProviderId() != null && !this.getServiceProvider().getServiceProviderId().equals(other.getServiceProvider().getServiceProviderId()))) {
        	return false;
        }
        //
        if(MALUtilities.isEmpty(this.getServiceProvider().getServiceProviderNumber()) &&  !MALUtilities.isEmpty(other.getServiceProvider().getServiceProviderNumber())
        		||
        		!MALUtilities.isEmpty(this.getServiceProvider().getServiceProviderNumber()) &&  MALUtilities.isEmpty(other.getServiceProvider().getServiceProviderNumber())
        		){
        	return false;
        }
        if(!this.getServiceProvider().getServiceProviderNumber().equals(other.getServiceProvider().getServiceProviderNumber())){
        	return false;
        }
        if (this.getServiceProviderContactInfo() == null || this.getServiceProviderContactInfo().equals(blankValue) ) {
        	if (other.getServiceProviderContactInfo() != null && !other.getServiceProviderContactInfo().equals(blankValue)){
        		return false;
        	}
        }else {
        	if(!this.getServiceProviderContactInfo().equals(other.getServiceProviderContactInfo())){
        		return false;
        	}
        }
        
        if (this.getServiceProviderContactName() == null || this.getServiceProviderContactName().equals(blankValue)) {
        	if (other.getServiceProviderContactName() != null && !other.getServiceProviderContactName().equals(blankValue)) {
        		return false;
        	}
        } else {
        	if (!this.getServiceProviderContactName().equals(other.getServiceProviderContactName())) {
        		return false;
        	}
        }
        
        if (this.getPayeeInvoiceNumber() == null || this.getPayeeInvoiceNumber().equals(blankValue)) {
        	if (other.getPayeeInvoiceNumber() != null && !other.getPayeeInvoiceNumber().equals(blankValue)) {
        		return false;
        	}
        } else {
        	if (!this.getPayeeInvoiceNumber().equals(other.getPayeeInvoiceNumber())) {
        		return false;
        	}
        }
        
        if ((this.getMaintReqStatus() == null && other.getMaintReqStatus() != null ) || (this.getMaintReqStatus() != null && !this.getMaintReqStatus().equals(other.getMaintReqStatus())) ){ 
        	return false;
        }
        
        if ((this.getMaintReqType() == null && other.getMaintReqType() != null ) || (this.getMaintReqType() != null && !this.getMaintReqType().equals(other.getMaintReqType())) ){ 
        	return false;
        }
        
        if (other.getCurrentOdo() == null || (this.getCurrentOdo() == 0 && other.getCurrentOdo() != 0 ) || ( this.getCurrentOdo() != 0 && this.getCurrentOdo().longValue() != other.getCurrentOdo().longValue())) { 
        	return false;
        }
        
        if ((this.getActualStartDate() == null && other.getActualStartDate() != null ) || (this.getActualStartDate() != null && MALUtilities.compareDates(MALUtilities.getNullSafeDatetoString(this.getActualStartDate()), MALUtilities.getNullSafeDatetoString(other.getActualStartDate())) != 0) ){ 
        	return false;
        }
        
        if ((this.getPlannedEndDate() == null && other.getPlannedEndDate() != null ) || (this.getPlannedEndDate() != null && MALUtilities.compareDates(MALUtilities.getNullSafeDatetoString(this.getPlannedEndDate()), MALUtilities.getNullSafeDatetoString(other.getPlannedEndDate())) != 0) ){ 
        	return false;
        }
        
        if (this.getCouponBookReference() == null || this.getCouponBookReference().equals(blankValue)) {
        	if (other.getCouponBookReference() != null && !other.getCouponBookReference().equals(blankValue)) {
        		return false;
        	}
        } else {
        	if (!this.getCouponBookReference().equals(other.getCouponBookReference())) {
        		return false;
        	}
        }
        
        if (this.getClientPoNo() == null || this.getClientPoNo().equals(blankValue)) {
        	if (other.getClientPoNo() != null && !other.getClientPoNo().equals(blankValue)) {
        		return false;
        	}
        } else {
        	if (!this.getClientPoNo().equals(other.getClientPoNo())) {
        		return false;
        	}
        }        
        
        if (this.getAuthReference() == null || this.getAuthReference().equals(blankValue)) {
        	if (other.getAuthReference() != null && !other.getAuthReference().equals(blankValue)) {
        		return false;
        	}
        } else {
        	if (!this.getAuthReference().equals(other.getAuthReference())) {
        		return false;
        	}
        }
        
        if (this.getReasonForService() == null || this.getReasonForService().equals(blankValue)) {
        	if (other.getReasonForService() != null && !other.getReasonForService().equals(blankValue)) {
        		return false;
        	}
        } else {
        	if (!this.getReasonForService().equals(other.getReasonForService())) {
        		return false;
        	}
        }
        
        return true;
    }

    @Override
    public String toString() {
        return "com.mikealbert.data.entity.MaintenanceRequest[ mrqId=" + getMrqId() + " ]";
    }
    
}
