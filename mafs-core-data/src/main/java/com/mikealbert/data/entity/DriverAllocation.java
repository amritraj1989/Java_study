package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.mikealbert.data.beanvalidation.MADate;
import com.mikealbert.data.beanvalidation.MANotNull;

/**
 * Mapped to DRIVER_ALLOCATIONS table
 * @author sibley
 */
@Entity
@Table(name = "DRIVER_ALLOCATIONS")
public class DriverAllocation extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="DAL_SEQ")    
    @SequenceGenerator(name="DAL_SEQ", sequenceName="DAL_SEQ", allocationSize=1)    
    @Basic(optional = false)
    @NotNull
    @Column(name = "DAL_ID")
    private Long dalId;
    
    @Size(max = 1)
    @Column(name = "FUEL_IND_FLAG")
    private String fuelIndFlag;
    
    @Column(name = "FROM_ODO_READING")
    private Long fromOdoReading;
    
    @Basic(optional = false)
    @MANotNull(label = "Allocation Date")
    @MADate(label = "Allocation Date")
    @Column(name = "FROM_DATE")
    @Temporal(TemporalType.DATE)
    private Date allocationDate;
    
    @MADate(label = "Deallocation Date")
    @Column(name = "TO_DATE")
    @Temporal(TemporalType.DATE)
    private Date deallocationDate;
    
    @Size(max = 25)
    @Column(name = "OP_CODE")
    private String opCode;
    
    @Size(max = 100)
    @Column(name = "LAST_ACTION_USER")
    private String lastActionUser;
    
    @Column(name = "LAST_ACTION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastActionDate;
    
    @Size(max = 10)   
    @Column(name = "ODO_UOM")    
    private String odoUom;
          
    @JoinColumn(name = "FMS_FMS_ID", referencedColumnName = "FMS_ID")
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private FleetMaster fleetMaster;
    
    @JoinColumn(name = "DRV_DRV_ID", referencedColumnName = "DRV_ID")          
    @ManyToOne(optional = false, fetch = FetchType.EAGER)  
    private Driver driver;    

    @JoinColumn(name = "ALLOC_TYPE", referencedColumnName = "ALLOC_TYPE")      
    @ManyToOne(optional = true)
    private DriverAllocCode driverAllocCode;  
    
    public DriverAllocation() {}

    public DriverAllocation(Long dalId) {
        this.dalId = dalId;
    }

    public DriverAllocation(Long dalId, Date fromDate) {
        this.dalId = dalId;
        this.allocationDate = fromDate;
    }

    public DriverAllocation(Driver driver, FleetMaster fleetMaster, Date fromDate) {
        this.driver = driver;
        this.fleetMaster = fleetMaster;
        this.allocationDate = fromDate;
    }
    
    public Long getDalId() {
        return dalId;
    }

    public void setDalId(Long dalId) {
        this.dalId = dalId;
    }

    public String getFuelIndFlag() {
        return fuelIndFlag;
    }

    public void setFuelIndFlag(String fuelIndFlag) {
        this.fuelIndFlag = fuelIndFlag;
    }

    public Long getFromOdoReading() {
        return fromOdoReading;
    }

    public void setFromOdoReading(Long fromOdoReading) {
        this.fromOdoReading = fromOdoReading;
    }

    public Date getAllocationDate() {
        return allocationDate;
    }

    public void setAllocationDate(Date fromDate) {
        this.allocationDate = fromDate;
    }

    public Date getDeallocationDate() {
        return deallocationDate;
    }

    public void setDeallocationDate(Date toDate) {
        this.deallocationDate = toDate;
    }

    public String getOpCode() {
        return opCode;
    }

    public void setOpCode(String opCode) {
        this.opCode = opCode;
    }

    public String getLastActionUser() {
        return lastActionUser;
    }

    public void setLastActionUser(String lastActionUser) {
        this.lastActionUser = lastActionUser;
    }

    public Date getLastActionDate() {
        return lastActionDate;
    }

    public void setLastActionDate(Date lastActionDate) {
        this.lastActionDate = lastActionDate;
    }

	public String getOdoUom() {
		return odoUom;
	}

	public void setOdoUom(String odoUom) {
		this.odoUom = odoUom;
	}

	public FleetMaster getFleetMaster() {
		return fleetMaster;
	}

	public void setFleetMaster(FleetMaster fleetMaster) {
		this.fleetMaster = fleetMaster;
	}
	
    public Driver getDriver() {
		return driver;
	}

	public void setDriver(Driver driver) {
		this.driver = driver;
	}

	public DriverAllocCode getDriverAllocCode() {
		return driverAllocCode;
	}

	public void setDriverAllocCode(DriverAllocCode driverAllocCode) {
		this.driverAllocCode = driverAllocCode;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (dalId != null ? dalId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DriverAllocation)) {
            return false;
        }
        DriverAllocation other = (DriverAllocation) object;
        if ((this.dalId == null && other.dalId != null) || (this.dalId != null && !this.dalId.equals(other.dalId))) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return "com.mikealbert.entity.DriverAllocation[ dalId=" + dalId + " ]";
    }
    
}
