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


/**
 * The persistent class for the QUOTE_REQUESTS database table.
 * 
 */
@Entity
@Table(name="QUOTE_REQUESTS")
public class QuoteRequest extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="QRQ_SEQ")    
    @SequenceGenerator(name="QRQ_SEQ", sequenceName="QRQ_SEQ", allocationSize=1)
    @Column(name = "QRQ_ID")
    private Long qrqId;
    
    @Column(name="NAME")
	private String name;

	@ManyToOne
	@JoinColumn(name="QRT_QRT_ID")
	private QuoteRequestType quoteRequestType;
	
    @JoinColumn(name = "QRS_QRS_ID", referencedColumnName = "QRS_ID")
    @ManyToOne(optional = false)
    private QuoteRequestStatus quoteRequestStatus;	
    
	@ManyToOne
	@JoinColumn(name="QRCR_QRCR_ID")
	private QuoteRequestCloseReason quoteRequestCloseReason;    
    
    @OneToMany(mappedBy = "quoteRequest", fetch = FetchType.EAGER, cascade = CascadeType.ALL ,orphanRemoval = true)
    private List<QuoteRequestVehicle> quoteRequestVehicles;
    
    @OneToMany(mappedBy = "quoteRequest", fetch = FetchType.LAZY, cascade = CascadeType.ALL ,orphanRemoval = true)
    private List<QuoteRequestQuote> quoteRequestQuotes;
    
    @OneToMany(mappedBy = "quoteRequest", cascade = CascadeType.ALL ,orphanRemoval = true)
    private List<QuoteRequestActivity> quoteRequestActivities;    
	
	@JoinColumns({
        @JoinColumn(name = "CLIENT_C_ID", referencedColumnName = "C_ID"),
        @JoinColumn(name = "CLIENT_ACCOUNT_TYPE", referencedColumnName = "ACCOUNT_TYPE"),
        @JoinColumn(name = "CLIENT_ACCOUNT_CODE", referencedColumnName = "ACCOUNT_CODE")})
	@ManyToOne(optional = false)
	private ExternalAccount clientAccount;
		
	@ManyToOne
	@JoinColumn(name="VAT_VAT_ID")
	private VehicleAcquisitionType vehicleAcquisitionType;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="DRV_DRV_ID")
	private Driver driver;
		
	@Column(name="DRIVER_ZIP_CODE")
	private String driverZipCode;
    
    @Column(name="ASSIGNED_TO")
	private String assignedTo;
    
    @Column(name="CREATED_BY")
	private String createdBy;
    
    @Temporal(TemporalType.TIMESTAMP)
	@Column(name="CREATED_DATE")
	private Date createdDate;
	
    @Column(name="SUBMITTED_BY")
	private String submittedBy;
        
    @Temporal(TemporalType.TIMESTAMP)
	@Column(name="SUBMITTED_DATE")
	private Date submittedDate;
        
    @Temporal(TemporalType.TIMESTAMP)
	@Column(name="COMPLETED_DATE")
	private Date completedDate; 
    
    @Temporal(TemporalType.TIMESTAMP)
	@Column(name="CLOSED_DATE")
	private Date closedDate;     
    
    @Temporal(TemporalType.TIMESTAMP)    
    @Column(name = "DUE_DATE")
    private Date dueDate;
    
    @Column(name="ALTERNATE_DELIVERY_ADDRESS")
   	private String deliveryAddress;
    
    @Column(name="RETURNING_FMS_FMS_ID")
    private Long returningFmsId;
    
    @Column(name="RETURNING_UNIT_IND")
    private String returningUnitYN;    

    @Column(name="MFR_INCENTIVE_IND")
    private String manufacturerIncentiveYN;    

    @Column(name="CONTACT_NAME")
    private String contactName;    

    @Column(name="CONTACT_PHONE")
    private String contactPhone;    

    @Column(name="CONTACT_EMAIL")
    private String contactEmail;    

    @Column(name="CONTACT_DRIVER_IND")
    private String contactDriverYN;    

    @Column(name="UPFIT_NOTE")
    private String upfitNote;    
    
	@OneToMany(mappedBy="quoteRequest", fetch = FetchType.LAZY, cascade = CascadeType.ALL ,orphanRemoval = true) 
	private List<QuoteRequestConfiguration> quoteRequestConfigurations;

	public QuoteRequest() {}
    
	public Long getQrqId() {
		return qrqId;
	}

	public void setQrqId(Long qrqId) {
		this.qrqId = qrqId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public QuoteRequestType getQuoteRequestType() {
		return quoteRequestType;
	}

	public void setQuoteRequestType(QuoteRequestType quoteRequestType) {
		this.quoteRequestType = quoteRequestType;
	}

	public ExternalAccount getClientAccount() {
		return clientAccount;
	}

	public void setClientAccount(ExternalAccount clientAccount) {
		this.clientAccount = clientAccount;
	}

	public VehicleAcquisitionType getVehicleAcquisitionType() {
		return vehicleAcquisitionType;
	}

	public void setVehicleAcquisitionType(
			VehicleAcquisitionType vehicleAcquisitionType) {
		this.vehicleAcquisitionType = vehicleAcquisitionType;
	}

	public Driver getDriver() {
		return driver;
	}

	public void setDriver(Driver driver) {
		this.driver = driver;
	}

	public String getDriverZipCode() {
		return driverZipCode;
	}

	public void setDriverZipCode(String driverZipCode) {
		this.driverZipCode = driverZipCode;
	}

	public String getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(String assignedTo) {
		this.assignedTo = assignedTo;
	}

	public String getSubmittedBy() {
		return submittedBy;
	}

	public void setSubmittedBy(String submittedBy) {
		this.submittedBy = submittedBy;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getSubmittedDate() {
		return submittedDate;
	}

	public void setSubmittedDate(Date submittedDate) {
		this.submittedDate = submittedDate;
	}

	public Date getCompletedDate() {
		return completedDate;
	}

	public void setCompletedDate(Date completedDate) {
		this.completedDate = completedDate;
	}

	public Date getClosedDate() {
		return closedDate;
	}

	public void setClosedDate(Date closedDate) {
		this.closedDate = closedDate;
	}

	public QuoteRequestStatus getQuoteRequestStatus() {
		return quoteRequestStatus;
	}

	public void setQuoteRequestStatus(QuoteRequestStatus quoteRequestStatus) {
		this.quoteRequestStatus = quoteRequestStatus;
	}

	public QuoteRequestCloseReason getQuoteRequestCloseReason() {
		return quoteRequestCloseReason;
	}

	public void setQuoteRequestCloseReason(QuoteRequestCloseReason quoteRequestCloseReason) {
		this.quoteRequestCloseReason = quoteRequestCloseReason;
	}

	public List<QuoteRequestVehicle> getQuoteRequestVehicles() {
		return quoteRequestVehicles;
	}

	public void setQuoteRequestVehicles(
			List<QuoteRequestVehicle> quoteRequestVehicles) {
		this.quoteRequestVehicles = quoteRequestVehicles;
	}

	public List<QuoteRequestQuote> getQuoteRequestQuotes() {
		return quoteRequestQuotes;
	}

	public void setQuoteRequestQuotes(List<QuoteRequestQuote> quoteRequestQuotes) {
		this.quoteRequestQuotes = quoteRequestQuotes;
	}

	public List<QuoteRequestActivity> getQuoteRequestActivities() {
		return quoteRequestActivities;
	}

	public void setQuoteRequestActivities(List<QuoteRequestActivity> quoteRequestActivities) {
		this.quoteRequestActivities = quoteRequestActivities;
	}

	public String getDeliveryAddress() {
		return deliveryAddress;
	}

	public void setDeliveryAddress(String deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public Long getReturningFmsId() {
		return returningFmsId;
	}

	public void setReturningFmsId(Long returningFmsId) {
		this.returningFmsId = returningFmsId;
	}

	public String getReturningUnitYN() {
		return returningUnitYN;
	}

	public void setReturningUnitYN(String returningUnitYN) {
		this.returningUnitYN = returningUnitYN;
	}

	public String getManufacturerIncentiveYN() {
		return manufacturerIncentiveYN;
	}

	public void setManufacturerIncentiveYN(String manufacturerIncentiveYN) {
		this.manufacturerIncentiveYN = manufacturerIncentiveYN;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getContactPhone() {
		return contactPhone;
	}

	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}

	public String getContactEmail() {
		return contactEmail;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	public String getContactDriverYN() {
		return contactDriverYN;
	}

	public void setContactDriverYN(String contactDriverYN) {
		this.contactDriverYN = contactDriverYN;
	}

    public String getUpfitNote() {
		return upfitNote;
	}

	public void setUpfitNote(String upfitNote) {
		this.upfitNote = upfitNote;
	}

	public List<QuoteRequestConfiguration> getQuoteRequestConfigurations() {
		return quoteRequestConfigurations;
	}

	public void setQuoteRequestConfigurations(List<QuoteRequestConfiguration> quoteRequestConfigurations) {
		this.quoteRequestConfigurations = quoteRequestConfigurations;
	}



	
}