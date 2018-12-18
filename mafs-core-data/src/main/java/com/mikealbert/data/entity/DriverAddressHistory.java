package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Mapped to DRIVER_ADDRESSES_HISTORY table. Using the entity to replicate
 * Willow's management of address changes today. This will enable us to deploy
 * the first release without impact to existing subsystems that rely on the data
 * in the DRIVER_ADDRESSES_HISTORY table. 
 * 
 * NOTE: A thought, is to remove the table and maintain the history in the 
 * DRIVER_ADDRESS table instead. This approach will have to be investigated 
 * further as it affects Web CA, BI, and Fleet Tax processing.
 * 
 * @author sibley
 */
@Entity
@Table(name = "DRIVER_ADDRESSES_HISTORY")
public class DriverAddressHistory extends Address implements Serializable {
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="DAH_SEQ")    
    @SequenceGenerator(name="DAH_SEQ", sequenceName="DAH_SEQ", allocationSize=1)     
    @Basic(optional = false)
    @NotNull
    @Column(name = "DAH_ID")
    private Long dahId;

    @Size(max = 1)
    @Column(name = "DEFAULT_IND")
    private String defaultInd;
                
    @Column(name = "INPUT_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date inputDate;   
    
    @Size(max = 25)
    @Column(name = "USER_NAME")
    private String userName;    
            
    @JoinColumn(name = "DRV_DRV_ID", referencedColumnName = "DRV_ID")
    @ManyToOne(optional = false)
    private Driver driver;
    
    @Size(max = 1)
    @Column(name = "BUSINESS_IND")
    private String businessInd;
    
	@Size(max = 80)
    @Column(name = "BUSINESS_ADDRESS_LINE")
    private String businessAddressLine ;
	
    @Transient
    private Date startDate;
    
    @Transient
    private Date endDate;

    public DriverAddressHistory() {}

    public DriverAddressHistory(Long dahId) {
        this.dahId = dahId;
    }

    public Long getDahId() {
        return dahId;
    }

    public void setDahId(Long dahId) {
        this.dahId = dahId;
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

    @Override
    public String toString() {
        return "com.mikealbert.entity.DriverAddressesHistory[ dahId=" + dahId + " ]";
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
}
