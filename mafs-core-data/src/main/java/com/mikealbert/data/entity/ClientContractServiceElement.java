package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Mapped to CLIENT_CONTRACT_SERVICE_ELE table
 */

@Entity
@Table(name = "CLIENT_CONTRACT_SERVICE_ELE")
public class ClientContractServiceElement extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CCSE_SEQ")    
    @SequenceGenerator(name="CCSE_SEQ", sequenceName="CCSE_SEQ", allocationSize=1)
    @Basic(optional = false)
    @NotNull
	@Column(name = "CCSE_ID")
	private Long clientContractServiceElementId;
	
    @ManyToOne
	@JoinColumn(name="LEL_LEL_ID", referencedColumnName = "LEL_ID")
	private LeaseElement leaseElement;
    
    @ManyToOne
	@JoinColumn(name="CA_CA_ID", referencedColumnName = "CA_ID")
	private ClientAgreement clientAgreement;
    
    public ClientContractServiceElement(){
    }

	public Long getClientContractServiceElementId() {
		return clientContractServiceElementId;
	}

	public void setClientContractServiceElementId(
			Long clientContractServiceElementId) {
		this.clientContractServiceElementId = clientContractServiceElementId;
	}

	public LeaseElement getLeaseElement() {
		return leaseElement;
	}

	public void setLeaseElement(LeaseElement leaseElement) {
		this.leaseElement = leaseElement;
	}

	@Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ClientContractServiceElement)) {
            return false;
        }
        ClientContractServiceElement other = (ClientContractServiceElement) object;
        if ((this.clientContractServiceElementId == null && other.clientContractServiceElementId != null) || (this.clientContractServiceElementId != null && !this.clientContractServiceElementId.equals(other.clientContractServiceElementId))) {
            return false;
        }
        return true;
    }

	public ClientAgreement getClientAgreement() {
		return clientAgreement;
	}

	public void setClientAgreement(ClientAgreement clientAgreement) {
		this.clientAgreement = clientAgreement;
	}
}
