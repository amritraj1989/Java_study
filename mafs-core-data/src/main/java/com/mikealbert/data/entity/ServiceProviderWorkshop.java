package com.mikealbert.data.entity;

import java.io.Serializable;

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
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "SUPPLIER_WORKSHOPS")
public class ServiceProviderWorkshop implements Serializable {
	private static final long serialVersionUID = -4868391326114831619L;
	
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SWK_SEQ")
    @SequenceGenerator(name="SWK_SEQ", sequenceName="SWK_SEQ", allocationSize=1)
    @NotNull
    @Column(name = "SWK_ID")
	private Long swkId;

	@NotNull
    @Column(name = "SUP_SUP_ID")
    private Long supId;

    @NotNull
    @Column(name = "WORKSHOP_CAPABILITY")
    private String workshopCapability;
    
    @JoinColumn(name = "SUP_SUP_ID", referencedColumnName = "SUP_ID", insertable = false, updatable = false)
    @ManyToOne(optional = true)
    private ServiceProvider serviceProvider;

    @JoinColumn(name = "WORKSHOP_CAPABILITY", referencedColumnName = "WORKSHOP_CAPABILITY", insertable = false, updatable = false)
    @OneToOne(optional = true, fetch = FetchType.EAGER)
    private WorkshopCapabilityCode workshopCapabilityCode;

    public Long getSwkId() {
		return swkId;
	}

	public void setSwkId(Long swkId) {
		this.swkId = swkId;
	}

	public Long getSupId() {
		return supId;
	}

	public void setSupId(Long supId) {
		this.supId = supId;
	}

	public String getWorkshopCapability() {
		return workshopCapability;
	}

	public void setWorkshopCapability(String workshopCapability) {
		this.workshopCapability = workshopCapability;
	}

	public ServiceProvider getServiceProvider() {
		return serviceProvider;
	}

	public void setServiceProvider(ServiceProvider serviceProvider) {
		this.serviceProvider = serviceProvider;
	}

	public WorkshopCapabilityCode getWorkshopCapabilityCode() {
		return workshopCapabilityCode;
	}

	public void setWorkshopCapabilityCode(
			WorkshopCapabilityCode workshopCapabilityCode) {
		this.workshopCapabilityCode = workshopCapabilityCode;
	}
}
