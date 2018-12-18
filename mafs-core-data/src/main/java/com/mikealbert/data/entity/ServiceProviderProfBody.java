package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "SUPPL_PROF_BODIES")
public class ServiceProviderProfBody implements Serializable {
	@EmbeddedId
	private ServiceProviderProfBodyPK serviceProviderProfBodyPk;
	
	@Column(name = "MEMBERSHIP_NO")
	private String membershopNo;

    @JoinColumn(name = "PBC_CODE", referencedColumnName = "PROFESSIONAL_BODY", insertable = false, updatable = false)
    @OneToOne(optional = true, fetch = FetchType.EAGER)
    private ProfessionalBodyCode professionalBodyCode;

    @JoinColumn(name = "SUP_ID", referencedColumnName = "SUP_ID", insertable = false, updatable = false)
    @ManyToOne(optional = true)
    private ServiceProvider serviceProvider;

	public String getMembershopNo() {
		return membershopNo;
	}

	public void setServiceProviderProfBodyPk(ServiceProviderProfBodyPK serviceProviderProfBodyPk) {
		this.serviceProviderProfBodyPk = serviceProviderProfBodyPk;
	}

	public ServiceProviderProfBodyPK getServiceProviderProfBodyPk() {
		return serviceProviderProfBodyPk;
	}

	public void setMembershopNo(String membershopNo) {
		this.membershopNo = membershopNo;
	}

	public ProfessionalBodyCode getProfessionalBodyCode() {
		return professionalBodyCode;
	}

	public void setProfessionalBodyCode(ProfessionalBodyCode professionalBodyCode) {
		this.professionalBodyCode = professionalBodyCode;
	}    
}
