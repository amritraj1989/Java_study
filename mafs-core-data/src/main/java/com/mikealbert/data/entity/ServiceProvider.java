package com.mikealbert.data.entity;

import java.io.Serializable;
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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 * Mapped to SUPPLIERS table
 * @author Disbrow
 */
@Entity
@Table(name = "SUPPLIERS")
public class ServiceProvider extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SUP_SEQ")    
    @SequenceGenerator(name="SUP_SEQ", sequenceName="SUP_SEQ", allocationSize=1)  
    @Column(name = "SUP_ID")
    private Long serviceProviderId;
    
    
    @Column(name = "SUPPLIER_CODE")
    private String serviceProviderNumber;

    @Column(name = "SUPPLIER_NAME")
    private String serviceProviderName;
    
    @Size(max = 1)
    @Column(name = "NETWORK_VENDOR")
    private String networkVendor;  
        
    @JoinColumns({
        @JoinColumn(name = "EA_C_ID", referencedColumnName = "C_ID"),
        @JoinColumn(name = "EA_ACCOUNT_TYPE", referencedColumnName = "ACCOUNT_TYPE"),
        @JoinColumn(name = "EA_ACCOUNT_CODE", referencedColumnName = "ACCOUNT_CODE")})
    @ManyToOne
    private ExternalAccount payeeAccount;
    
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "serviceProvider")
    private List<ServiceProviderAddress> serviceProviderAddresses;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "serviceProvider")
    private List<ServiceProviderWorkshop> supplierWorkshopCodes;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "serviceProvider")
    private List<ServiceProviderProfBody> serviceProviderProfBodies;

	@JoinColumn(name="SUP_SUP_ID", referencedColumnName = "SUP_ID")
    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    private ServiceProvider parentServiceProvider;
    
    @OneToMany(mappedBy = "serviceProvider", fetch = FetchType.LAZY)
    private List<MaintenanceRequest> maintenanceRequest;
    
    //bi-directional many-to-one association to SupplierDiscount
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy="serviceProvider")
    private List<ServiceProviderDiscount> serviceProviderDiscounts;
    
    //bi-directional many-to-one association to ServiceProviderFranchise
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy="serviceProvider")
    private List<ServiceProviderFranchise> serviceProviderFranchises;

    @Column(name = "TELEPHONE_NUMBER")
    private String telephoneNo;
    
    @Column(name = "FAX_NUMBER")
    private String faxNo;

	@Column(name = "EMAIL_ADDRESS")
    private String emailAddress;
    
    
    @Column(name="SSTC_SERVICE_TYPE_CODE")
    private String serviceTypeCode;
    
    @Column(name="SUPPLIER_CATEGORY")
    private String serviceProviderCategory;
    
    
    @Column(name = "DETAILS_LAST_UPDATED_DATE")
    @Temporal(TemporalType.DATE)
	private Date lastUpdateDate;

	@NotNull
    @Size(max = 25)   
    @Column(name = "DETAILS_ENTERED_BY")
	private String enteredBy;
    
	//TODO: every entry is the DB is in feet; we did not want to pay Binks to re-label but this is not really meters
	// we should determine the impact of changing this column in the db and it's label.
    @Column(name="WKSHP_HT_CLRNCE_METRES")
    private String workshopClearanceFeet;
    
    @Column(name="NO_OF_BAYS")
    private Long noOfBays;

	@Column(name="NORMAL_HRS_SAT")
	private String normalHoursSat;
    
    @Column(name="NORMAL_HRS_WEEK")
    private String normalHoursWeek;
    
	@Column(name="NORMAL_HRS_SUN")
	private String normalHoursSun;

	@Column(name="NORMAL_HRS_HOLIDAY")
	private String normalHoursHoliday;

	@NotNull
    @Size(max = 1)   
    @Column(name = "INACTIVE_IND")
    private String inactiveInd;

	public ServiceProvider() {
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (getServiceProviderId() != null ? getServiceProviderId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ServiceProvider)) {
            return false;
        }
        ServiceProvider other = (ServiceProvider) object;
        if ((this.serviceProviderId == null && other.serviceProviderId != null) || (this.serviceProviderId != null && !this.serviceProviderId.equals(other.serviceProviderId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mikealbert.data.entity.ServicerProvider[ supId=" + serviceProviderId + " ]";
    }


	public Long getServiceProviderId() {
		return serviceProviderId;
	}


	public void setServiceProviderId(Long serviceProviderId) {
		this.serviceProviderId = serviceProviderId;
	}


	public String getServiceProviderNumber() {
		return serviceProviderNumber;
	}


	public void setServiceProviderNumber(String serviceProviderNumber) {
		this.serviceProviderNumber = serviceProviderNumber;
	}


	public String getServiceProviderName() {
		return serviceProviderName;
	}


	public void setServiceProviderName(String serviceProviderName) {
		this.serviceProviderName = serviceProviderName;
	}


	public List<ServiceProviderDiscount> getServiceProviderDiscounts() {
		return serviceProviderDiscounts;
	}


	public void setServiceProviderDiscounts(
			List<ServiceProviderDiscount> serviceProviderDiscounts) {
		this.serviceProviderDiscounts = serviceProviderDiscounts;
	}


	public ExternalAccount getPayeeAccount() {
		return payeeAccount;
	}


	public void setPayeeAccount(ExternalAccount payeeAccount) {
		this.payeeAccount = payeeAccount;
	}


	public List<ServiceProviderAddress> getServiceProviderAddresses() {
		return serviceProviderAddresses;
	}


	public void setServiceProviderAddresses(
			List<ServiceProviderAddress> serviceProviderAddresses) {
		this.serviceProviderAddresses = serviceProviderAddresses;
	}

    public List<ServiceProviderWorkshop> getSupplierWorkshopCodes() {
		return supplierWorkshopCodes;
	}

	public void setSupplierWorkshopCodes(List<ServiceProviderWorkshop> supplierWorkshopCodes) {
		this.supplierWorkshopCodes = supplierWorkshopCodes;
	}
    
	public List<ServiceProviderProfBody> getServiceProviderProfBodies() {
		return serviceProviderProfBodies;
	}

	public void setServiceProviderProfBodies(List<ServiceProviderProfBody> serviceProviderProfBodies) {
		this.serviceProviderProfBodies = serviceProviderProfBodies;
	}

	public List<MaintenanceRequest> getMaintenanceRequest() {
		return maintenanceRequest;
	}

	public void setMaintenanceRequest(List<MaintenanceRequest> maintenanceRequest) {
		this.maintenanceRequest = maintenanceRequest;
	}


	public String getTelephoneNo() {
		return telephoneNo;
	}


	public void setTelephoneNo(String telephoneNo) {
		this.telephoneNo = telephoneNo;
	}


	public String getNetworkVendor() {
		return networkVendor;
	}


	public void setNetworkVendor(String networkVendor) {
		this.networkVendor = networkVendor;
	}
	
	public ServiceProvider getParentServiceProvider() {
		return parentServiceProvider;
	}

	public void setParentServiceProvider(ServiceProvider parentServiceProvider) {
		this.parentServiceProvider = parentServiceProvider;
	}


	public String getServiceTypeCode() {
		return serviceTypeCode;
	}


	public void setServiceTypeCode(String serviceTypeCode) {
		this.serviceTypeCode = serviceTypeCode;
	}

    public String getServiceProviderCategory() {
		return serviceProviderCategory;
	}


	public void setServiceProviderCategory(String serviceProviderCategory) {
		this.serviceProviderCategory = serviceProviderCategory;
	}

    
    public Date getLastUpdateDate() {
		return lastUpdateDate;
	}


	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}


	public String getEnteredBy() {
		return enteredBy;
	}


	public void setEnteredBy(String enteredBy) {
		this.enteredBy = enteredBy;
	}
	
	
	public String getWorkshopClearanceFeet() {
		return workshopClearanceFeet;
	}

	
	public void setWorkshopClearanceFeet(String workshopClearanceFeet) {
		this.workshopClearanceFeet = workshopClearanceFeet;
	}

	
	public Long getNoOfBays() {
		return noOfBays;
	}

	
	public void setNoOfBays(Long noOfBays) {
		this.noOfBays = noOfBays;
	}
	
    public String getNormalHoursSat() {
		return normalHoursSat;
	}

	public void setNormalHoursSat(String normalHoursSat) {
		this.normalHoursSat = normalHoursSat;
	}

	public String getNormalHoursWeek() {
		return normalHoursWeek;
	}

	public void setNormalHoursWeek(String normalHoursWeek) {
		this.normalHoursWeek = normalHoursWeek;
	}
	
	public String getNormalHoursSun() {
		return normalHoursSun;
	}

	public void setNormalHoursSun(String normalHoursSun) {
		this.normalHoursSun = normalHoursSun;
	}

	public String getNormalHoursHoliday() {
		return normalHoursHoliday;
	}

	public void setNormalHoursHoliday(String normalHoursHoliday) {
		this.normalHoursHoliday = normalHoursHoliday;
	}
	
    public String getFaxNo() {
		return faxNo;
	}

	public void setFaxNo(String faxNo) {
		this.faxNo = faxNo;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getInactiveInd() {
		return inactiveInd;
	}

	public void setInactiveInd(String inactiveInd) {
		this.inactiveInd = inactiveInd;
	}

	public List<ServiceProviderFranchise> getServiceProviderFranchises() {
		return serviceProviderFranchises;
	}

	public void setServiceProviderFranchises(List<ServiceProviderFranchise> serviceProviderFranchises) {
		this.serviceProviderFranchises = serviceProviderFranchises;
	}
}
