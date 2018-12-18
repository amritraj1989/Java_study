package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


import com.mikealbert.data.beanvalidation.MASize;

/**
 * Mapped to SUPP_MAINT_LINK Table
 * @author sibley
 */
@Entity
@Table(name = "SUPP_MAINT_LINK")
public class ServiceProviderMaintenanceCode extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SML_SEQ")    
    @SequenceGenerator(name="SML_SEQ", sequenceName="SML_SEQ", allocationSize=1)
    @Column(name = "SML_ID")
    private Long smlId;
    
   
    @Column(name = "VENDOR_CODE")
    private String code;
    
    @MASize(label = "Service Provider Maint Code Desc", min = 1, max = 80)
    @Column(name = "DESCRIPTION")
    private String description;   
        
    @JoinColumn(name="SUP_SUP_ID", referencedColumnName = "SUP_ID")
    @ManyToOne(optional = false)
    private ServiceProvider serviceProvider;
    
    @JoinColumn(name="MCO_MCO_ID", referencedColumnName = "MCO_ID")
    @ManyToOne(optional = true)
    private MaintenanceCode maintenanceCode;
    
    @Column(name="APPROVED_DATE")
    private Date approvedDate;
    
    @Column(name="APPROVED_BY")
    private String approvedBy;

    @Column(name="DISCOUNT_FLAG")
    private String discountFlag = "Y"; //Added default value of Y for MSS-2076. Saket 06/28/2016
   
    public ServiceProviderMaintenanceCode() {}
    
    public ServiceProviderMaintenanceCode(ServiceProviderMaintenanceCode serviceProviderMaintenanceCode) {
    	this.smlId = serviceProviderMaintenanceCode.getSmlId();
    	this.code = serviceProviderMaintenanceCode.getCode();
    	this.description = serviceProviderMaintenanceCode.getDescription();
    	this.serviceProvider = serviceProviderMaintenanceCode.getServiceProvider();
    	this.maintenanceCode = new MaintenanceCode(serviceProviderMaintenanceCode.getMaintenanceCode());
    	this.versionts = serviceProviderMaintenanceCode.getVersionts();
    }
    
	public Long getSmlId() {
		return smlId;
	}

	public void setSmlId(Long smlId) {
		this.smlId = smlId;
	}


	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ServiceProvider getServiceProvider() {
		return serviceProvider;
	}

	public void setServiceProvider(ServiceProvider serviceProvider) {
		this.serviceProvider = serviceProvider;
	}

	public MaintenanceCode getMaintenanceCode() {
		return maintenanceCode;
	}

	public void setMaintenanceCode(MaintenanceCode maintenanceCode) {
		this.maintenanceCode = maintenanceCode;
	}

	public Date getApprovedDate() {
		return approvedDate;
	}

	public void setApprovedDate(Date approvedDate) {
		this.approvedDate = approvedDate;
	}

	public String getApprovedBy() {
		return approvedBy;
	}

	public void setApprovedBy(String approvedBy) {
		this.approvedBy = approvedBy;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (getSmlId() != null ? getSmlId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ServiceProviderMaintenanceCode)) {
            return false;
        }
        ServiceProviderMaintenanceCode other = (ServiceProviderMaintenanceCode) object;
        if ((this.getSmlId() == null && other.getSmlId() != null) || (this.getSmlId() != null && !this.getSmlId().equals(other.getSmlId()))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mikealbert.data.entity.ServiceProviderMaintenanceCode[ smlId=" + getSmlId() + " ]";
    }

	public String getDiscountFlag() {
		return discountFlag;
	}

	public void setDiscountFlag(String discountFlag) {
		this.discountFlag = discountFlag;
	}
    
}
