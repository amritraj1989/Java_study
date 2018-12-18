package com.mikealbert.data.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
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
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
* Mapped to FLEET_MASTERS table.
* 
* Note: Not all columns and associations have been mapped.
* 
* @author sibley
*/
@Entity
@Table(name = "FLEET_MASTERS")
public class FleetMaster extends BaseEntity implements Serializable  {
	private static final long serialVersionUID = 1L;
	
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="FMS_SEQ")    
    @SequenceGenerator(name="FMS_SEQ", sequenceName="FMS_SEQ", allocationSize=1)      
    @Basic(optional = false)
    @NotNull
    @Column(name = "FMS_ID")
    private Long fmsId;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "CC_C_ID")
    private Long cId;   
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "MDL_MDL_ID")
    private Long mdlMdlId;  
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "ENGINE_TYPE")    
    private String engineType;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "FLEET_CATEGORY")    
    private String fleetCategory; 
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "DESIGNATION_CODE")    
    private String designationCode;   
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "MANUAL_FL_STATUS")    
    private String manualFlStatus;     
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "DATE_CREATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;    
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "VAT_TYPE")
    private String vatType;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "VIP_FLAG")
    private String vipFlag;  
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "VEH_CHECKED_IND")
    private String vehCheckedInd; 
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 25)
    @Column(name = "UNIT_NO")
    private String unitNo;  
    
    @Size(max = 25)    
    @Column(name = "VIN")    
    private String vin;
    
    @Size(max = 10)    
    @Column(name = "REG_NO")    
    private String regNo; 
    
    @Size(max = 10)    
    @Column(name = "SUB_STATUS")    
    private String subStatus;     
    
    @Column(name = "REG_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date regDate;
    
    @Size(max = 10)    
    @Column(name = "REF_NO")    
    private String refNo;  

    @Size(max = 25)    
    @Column(name = "VEHICLE_COST_CENTRE")    
    private String fleetReferenceNumber;
    
    @Column(name = "GROSS_VEH_WT ")
    private BigDecimal	grossVehicleWeight;
    
    @Column(name = "KERB_WEIGHT ")
    private String	kerbWeight;
    
    @Column(name = "RETAIL_PRICE ")
    private BigDecimal	retailPrice;

    @JoinColumn(name = "COLOUR_CODE", referencedColumnName = "COLOUR_CODE")
    @OneToOne(fetch = FetchType.LAZY)
    private ColourCodes colourCode;
    
	@JoinColumn(name = "TRIM_COLOUR", referencedColumnName = "TRIM_CODE")
    @OneToOne(fetch = FetchType.LAZY)
    private TrimCodes trimColour;
    
    @Transient
    private ContractLine contractLine;
        
    @OneToMany(mappedBy = "fleetMaster", fetch = FetchType.LAZY)
    private List<DriverAllocation> driverAllocationList; 
    
    @OneToMany(mappedBy = "fleetMaster", fetch = FetchType.LAZY)
    private List<ContractLine> contractLineList;        
    
    @JoinColumn(name = "MDL_MDL_ID", referencedColumnName = "MDL_ID", insertable=false, updatable=false)
    @OneToOne(fetch = FetchType.EAGER)
    private Model model;
    
    @JoinColumn(name = "FMS_ID", referencedColumnName = "FMS_ID", insertable=false, updatable=false)
    @OneToOne(fetch=FetchType.LAZY)
    private VehicleStatusV vehicleStatusV;
    
    @JoinColumn(name = "FMS_ID", referencedColumnName = "FMS_ID", insertable=false, updatable=false)
    @OneToOne(fetch=FetchType.LAZY)
    private LatestOdometerReadingV latestOdometerReading;   
    
    @OneToOne(optional = true, fetch = FetchType.EAGER,mappedBy = "fleetMaster")
    private FleetMasterVinDetails vehicleVinDetails;   
    
    @OneToMany(mappedBy="fleetMaster",fetch = FetchType.LAZY)
    @OrderBy("odoReadingDate DESC")
    private List<VehicleOdometerReadingsV> vehicleOdometerReadings;
    
    @JoinColumns({
        @JoinColumn(name = "WS_C_ID", referencedColumnName = "C_ID"),
        @JoinColumn(name = "WS_ACCOUNT_TYPE", referencedColumnName = "ACCOUNT_TYPE"),
        @JoinColumn(name = "WS_ACCOUNT_CODE", referencedColumnName = "ACCOUNT_CODE")})
    @ManyToOne(optional = true, fetch= FetchType.LAZY)
    private ExternalAccount externalAccount; 
    
    @JoinColumn(name = "DR_DR_CODE", referencedColumnName = "DR_CODE")
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    private DelayReason delayReason;

    @OneToMany(mappedBy = "fleetMaster", fetch = FetchType.LAZY)
    private List<Odometer> odometerList;  
    
    @OneToMany(mappedBy = "fleetMaster")
    private List<MaintenanceRequest> maintenanceRequests; 
    
    @Column(name = "ADJUSTMENT_VALUE")
    private BigDecimal	adjustment;
    
    @Transient
    private	Long	latestOdoMeterReading;
    
    @Transient
    private String	highMileage;
    
    
	public FleetMaster() {}

    public FleetMaster(Long fmsId) {
        this.fmsId = fmsId;
    }

    public FleetMaster(Long fmsId, Long ccCId, Long mdlMdlId, String engineType, 
    		String fleetCategory, String designationCode, String manualFlStatus, 
    		Date dateCreated, String vatType, String vipFlag, String vehCheckedInd, String unitNo) {
        this.setFmsId(fmsId);
        this.setcId(ccCId);
        this.setMdlMdlId(mdlMdlId);
        this.setEngineType(engineType);
        this.setFleetCategory(fleetCategory);
        this.setDesignationCode(designationCode);
        this.setManualFlStatus(manualFlStatus);
        this.setDateCreated(dateCreated);
        this.setVatType(vatType);
        this.setVipFlag(vipFlag);
        this.setVehCheckedInd(vehCheckedInd);
        this.setUnitNo(unitNo);
    }

    public Long getFmsId() {
        return fmsId;
    }

    public void setFmsId(Long fmsId) {
        this.fmsId = fmsId;
    }

	public String getUnitNo() {
		return unitNo;
	}

	public void setUnitNo(String unitNo) {
		this.unitNo = unitNo;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public String getVatType() {
		return vatType;
	}

	public void setVatType(String vatType) {
		this.vatType = vatType;
	}

	public String getVipFlag() {
		return vipFlag;
	}

	public void setVipFlag(String vipFlag) {
		this.vipFlag = vipFlag;
	}

	public String getVehCheckedInd() {
		return vehCheckedInd;
	}

	public void setVehCheckedInd(String vehCheckedInd) {
		this.vehCheckedInd = vehCheckedInd;
	}

	public List<DriverAllocation> getDriverAllocationList() {
		return driverAllocationList;
	}

	public void setDriverAllocationList(List<DriverAllocation> driverAllocationList) {
		this.driverAllocationList = driverAllocationList;
	}
	
    public Long getcId() {
		return cId;
	}

	public void setcId(Long cId) {
		this.cId = cId;
	}

	public Long getMdlMdlId() {
		return mdlMdlId;
	}

	public void setMdlMdlId(Long mdlMdlId) {
		this.mdlMdlId = mdlMdlId;
	}

	public String getEngineType() {
		return engineType;
	}

	public void setEngineType(String engineType) {
		this.engineType = engineType;
	}

	public String getFleetCategory() {
		return fleetCategory;
	}

	public void setFleetCategory(String fleetCategory) {
		this.fleetCategory = fleetCategory;
	}

	public String getDesignationCode() {
		return designationCode;
	}

	public void setDesignationCode(String designationCode) {
		this.designationCode = designationCode;
	}

	public String getManualFlStatus() {
		return manualFlStatus;
	}

	public void setManualFlStatus(String manualFlStatus) {
		this.manualFlStatus = manualFlStatus;
	}

	public String getVin() {
		return vin;
	}

	public void setVin(String vin) {
		this.vin = vin;
	}

	public String getRegNo() {
		return regNo;
	}

	public void setRegNo(String regNo) {
		this.regNo = regNo;
	}

	public String getSubStatus() {
		return subStatus;
	}

	public void setSubStatus(String subStatus) {
		this.subStatus = subStatus;
	}

	public String getRefNo() {
		return refNo;
	}

	public void setRefNo(String refNo) {
		this.refNo = refNo;
	}

	public List<ContractLine> getContractLineList() {
		return contractLineList;
	}

	public void setContractLineList(List<ContractLine> contractLineList) {
		this.contractLineList = contractLineList;
	}

	public ExternalAccount getExternalAccount() {
		return externalAccount;
	}

	public void setExternalAccount(ExternalAccount externalAccount) {
		this.externalAccount = externalAccount;
	}

	public ContractLine getContractLine() {
		return contractLine;
	}

	public void setContractLine(ContractLine contractLine) {
		this.contractLine = contractLine;
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public String getFleetReferenceNumber() {
		return fleetReferenceNumber;
	}

	public void setFleetReferenceNumber(String fleetReferenceNumber) {
		this.fleetReferenceNumber = fleetReferenceNumber;
	}	

	public List<Odometer> getOdometerList() {
		return odometerList;
	}

	public void setOdometerList(List<Odometer> odometerList) {
		this.odometerList = odometerList;
	}

	public List<MaintenanceRequest> getMaintenanceRequests() {
		return maintenanceRequests;
	}

	public void setMaintenanceRequests(List<MaintenanceRequest> maintenanceRequests) {
		this.maintenanceRequests = maintenanceRequests;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (fmsId != null ? fmsId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof FleetMaster)) {
            return false;
        }
        FleetMaster other = (FleetMaster) object;
        if ((this.fmsId == null && other.fmsId != null) || (this.fmsId != null && !this.fmsId.equals(other.fmsId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mikealbert.entity.FleetMasters[ fmsId=" + fmsId + " ]";
    }

	public VehicleStatusV getVehicleStatusV() {
		return vehicleStatusV;
	}

	public void setVehicleStatusV(VehicleStatusV vehicleStatusV) {
		this.vehicleStatusV = vehicleStatusV;
	}

	public LatestOdometerReadingV getLatestOdometerReading() {
		return latestOdometerReading;
	}

	public void setLatestOdometerReading(LatestOdometerReadingV latestOdometerReading) {
		this.latestOdometerReading = latestOdometerReading;
	}

	public List<VehicleOdometerReadingsV> getVehicleOdometerReadings() {
		return vehicleOdometerReadings;
	}

	public void setVehicleOdometerReadings(List<VehicleOdometerReadingsV> vehicleOdometerReadings) {
		this.vehicleOdometerReadings = vehicleOdometerReadings;
	}

	public BigDecimal getGrossVehicleWeight() {
		return grossVehicleWeight;
	}

	public void setGrossVehicleWeight(BigDecimal grossVehicleWeight) {
		this.grossVehicleWeight = grossVehicleWeight;
	}

	public String getKerbWeight() {
		return kerbWeight;
	}

	public void setKerbWeight(String kerbWeight) {
		this.kerbWeight = kerbWeight;
	}

	public BigDecimal getRetailPrice() {
		return retailPrice;
	}

	public void setRetailPrice(BigDecimal retailPrice) {
		this.retailPrice = retailPrice;
	}

	public Date getRegDate() {
		return regDate;
	}

	public void setRegDate(Date regDate) {
		this.regDate = regDate;
	}

	public Long getLatestOdoMeterReading() {
		return latestOdoMeterReading;
	}

	public void setLatestOdoMeterReading(Long latestOdoMeterReading) {
		this.latestOdoMeterReading = latestOdoMeterReading;
	}

	public String getHighMileage() {
		return highMileage;
	}

	public void setHighMileage(String highMileage) {
		this.highMileage = highMileage;
	}

	public FleetMasterVinDetails getVehicleVinDetails() {
		return vehicleVinDetails;
	}

	public void setVehicleVinDetails(FleetMasterVinDetails vehicleVinDetails) {
		this.vehicleVinDetails = vehicleVinDetails;
	}

	public DelayReason getDelayReason() {
		return delayReason;
	}

	public void setDelayReason(DelayReason delayReason) {
		this.delayReason = delayReason;
	}
	
    public ColourCodes getColourCode() {
		return colourCode;
	}

	public void setColourCode(ColourCodes colourCode) {
		this.colourCode = colourCode;
	}

	public TrimCodes getTrimColour() {
		return trimColour;
	}

	public void setTrimColour(TrimCodes trimColour) {
		this.trimColour = trimColour;
	}

	public BigDecimal getAdjustment() {
		return adjustment;
	}

	public void setAdjustment(BigDecimal adjustment) {
		this.adjustment = adjustment;
	}





}
