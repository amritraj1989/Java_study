package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Mapped to DRIVER_ADDRESSES table
 * @author sibley
 */
@Entity
@Table(name = "DRIVER_ADDRESSES")
public class DriverAddress extends Address implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="DRA_SEQ")    
    @SequenceGenerator(name="DRA_SEQ", sequenceName="DRA_SEQ", allocationSize=1)    
    @Basic(optional = false)
    @NotNull
    @Column(name = "DRA_ID")
    private Long draId;

    @Size(max = 1)
    @Column(name = "DEFAULT_IND")
    private String defaultInd;
   
    @Size(max = 25)
    @Column(name = "USER_NAME")
    private String userName;
    
	@Column(name = "INPUT_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date inputDate;

    @JoinColumn(name = "DRV_DRV_ID", referencedColumnName = "DRV_ID")
    @ManyToOne(optional = false)
    private Driver driver;

    @JoinColumn(name = "DRA_ID", referencedColumnName = "DRA_DRA_ID")
    @OneToOne(optional = true, fetch = FetchType.LAZY)
    private DriverAddressGeolocation driverAddressGeolocation;

    @Size(max = 1)
    @Column(name = "BUSINESS_IND")
    private String businessInd;
    
	@Size(max = 80)
    @Column(name = "BUSINESS_ADDRESS_LINE")
    private String businessAddressLine ;
	
    public DriverAddress() {
    }

    public DriverAddress(Long draId) {
        this.draId = draId;
    }

    public Long getDraId() {
        return draId;
    }

    public void setDraId(Long draId) {
        this.draId = draId;
    }

    public String getDefaultInd() {
        return defaultInd;
    }

    public void setDefaultInd(String defaultInd) {
        this.defaultInd = defaultInd;
    }

     public Date getInputDate() {
        return inputDate;
    }

    public void setInputDate(Date inputDate) {
        this.inputDate = inputDate;
    }

    public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
    
    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }	
    
    public DriverAddressGeolocation getDriverAddressGeolocation() {
    	return this.driverAddressGeolocation;
    }
    
    public void setDriverAddressGeolocation(DriverAddressGeolocation driverAddressGeolocation) {
    	this.driverAddressGeolocation = driverAddressGeolocation;
    }
    
    public String getBusinessInd() {
		return businessInd;
	}

	public void setBusinessInd(String businessInd) {
		this.businessInd = businessInd;
	}

	public String getBusinessAddressLine() {
		return businessAddressLine;
	}

	public void setBusinessAddressLine(String businessAddressLine) {
		this.businessAddressLine = businessAddressLine;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (this.getDraId() != null ? this.getDraId().hashCode() : 0);        
        hash += (this.getBusinessInd() != null ? this.getBusinessInd().hashCode() : 0);
        hash += (this.getDefaultInd() != null ? this.getDefaultInd().hashCode() : 0);
        hash += (super.getAddressType() != null ? super.getAddressType().hashCode() : 0);  
        hash += (super.getStreetNo() != null ? super.getStreetNo().hashCode() : 0);   
        hash += (this.getBusinessAddressLine() != null ? this.getBusinessAddressLine().hashCode() : 0);
        hash += (super.getAddressLine1() != null ? super.getAddressLine1().hashCode() : 0);
        hash += (super.getAddressLine2() != null ? super.getAddressLine2().hashCode() : 0);
        hash += (super.getTownCityCode() != null ? super.getTownCityCode().hashCode() : 0);  
        hash += (super.getPostcode() != null ? super.getPostcode().hashCode() : 0);       
        hash += (super.getGeoCode() != null ? super.getGeoCode().hashCode() : 0);   
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DriverAddress)) {
            return false;
        }
        DriverAddress other = (DriverAddress) object;
        if ((this.draId == null && other.draId != null) || (this.draId != null && !this.draId.equals(other.draId))) {
            return false;
        }
        else if(this.draId == null && other.draId == null){
        	if(this.getAddressType() == other.getAddressType()){
        		return true;
        	}
        	else{
        		return false;
        	}
        }
        return true;
    }
    
    @Override
    public String toString() {
        return "com.mikealbert.entity.DriverAddresses[ draId=" + draId + " ]";
    }
}
