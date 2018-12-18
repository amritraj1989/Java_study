package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

import com.mikealbert.data.beanvalidation.MANotNull;
import com.mikealbert.data.beanvalidation.MASize;

/**
 * Mapped to DRIVER_COST_CENTRES table.
 * @author sibley
 */
@Entity
@Table(name = "DRIVER_COST_CENTRES")
public class DriverCostCenter implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="DRCC_SEQ")    
    @SequenceGenerator(name="DRCC_SEQ", sequenceName="DRCC_SEQ", allocationSize=1)
    @Basic(optional = false)
    @NotNull
    @Column(name = "DRCC_ID")
    private Long drccId;
            
    @Basic(optional = false)
    @NotNull
    @Column(name = "DATE_ALLOCATED")
    @Temporal(TemporalType.DATE)
    private Date dateAllocated;

    @Column(name = "EFFECTIVE_TO_DATE")
    @Temporal(TemporalType.DATE)
    private Date effectiveToDate;

    
    @Basic(optional = false)
    @MANotNull(label = "Cost Center Code")
    @MASize(label = "Cost Center Code", min = 1, max = 25)
    @Column(name = "COST_CENTRE_CODE")
    private String costCenterCode;    
    
    @JoinColumns({
        @JoinColumn(name = "COCC_C_ID", referencedColumnName = "C_ID"),
        @JoinColumn(name = "COCC_ACCOUNT_TYPE", referencedColumnName = "ACCOUNT_TYPE"),
        @JoinColumn(name = "COCC_ACCOUNT_CODE", referencedColumnName = "ACCOUNT_CODE")})
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private ExternalAccount externalAccount;

    
    @Version
    @Column(name = "VERSIONTS")
    @Temporal(TemporalType.TIMESTAMP)
    private Date versionts;
    
    @JoinColumn(name = "DRV_DRV_ID", referencedColumnName = "DRV_ID")          
    @ManyToOne(optional = false, fetch = FetchType.EAGER)  
    private Driver driver;     
    
    
    public Date getDateAllocated() {
		return dateAllocated;
	}


	public void setDateAllocated(Date dateAllocated) {
		this.dateAllocated = dateAllocated;
	}


	public String getCostCenterCode() {
		return costCenterCode;
	}


	public void setCostCenterCode(String costCenterCode) {
		this.costCenterCode = costCenterCode;
	}


	public Driver getDriver() {
		return driver;
	}


	public void setDriver(Driver driver) {
		this.driver = driver;
	}


	@Override
    public String toString() {
        return "com.mikealbert.entity.CostCenter[ drccId=" + drccId + " ]";
    }


	public Date getEffectiveToDate() {
		return effectiveToDate;
	}


	public void setEffectiveToDate(Date effectiveToDate) {
		this.effectiveToDate = effectiveToDate;
	}


	public ExternalAccount getExternalAccount() {
		return externalAccount;
	}


	public void setExternalAccount(ExternalAccount externalAccount) {
		this.externalAccount = externalAccount;
	}
    
}
