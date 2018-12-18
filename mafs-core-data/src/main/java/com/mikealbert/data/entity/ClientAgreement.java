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
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Mapped to CLIENT_AGREEMENTS table
 */

@Entity
@Table(name = "CLIENT_AGREEMENTS")
public class ClientAgreement extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CA_SEQ")    
    @SequenceGenerator(name="CA_SEQ", sequenceName="CA_SEQ", allocationSize=1)
    @Basic(optional = false)
    @NotNull
	@Column(name = "CA_ID")
	private Long clientAgreementId;
	
	@Size(min = 1, max = 25)
	@Column(name = "AGREEMENT_NO")
    private String agreementNumber;
	
	@Column(name = "AGREEMENT_DATE")
	private Date agreementDate;
	
	@Column(name = "ACTIVE_IND")
	private String activeInd;
    
    @ManyToOne
	@JoinColumn(name="AGREEMENT_CODE", referencedColumnName = "AGREEMENT_CODE")
	private ContractAgreement contractAgreement;
    
    @JoinColumns({
        @JoinColumn(name = "EA_C_ID", referencedColumnName = "C_ID"),
        @JoinColumn(name = "EA_ACCOUNT_TYPE", referencedColumnName = "ACCOUNT_TYPE"),
        @JoinColumn(name = "EA_ACCOUNT_CODE", referencedColumnName = "ACCOUNT_CODE")})
    @ManyToOne(optional = true)
    private ExternalAccount externalAccount;
    
    public ClientAgreement(){
    }

	public Long getClientAgreementId() {
		return clientAgreementId;
	}

	public void setClientAgreementId(Long clientAgreementId) {
		this.clientAgreementId = clientAgreementId;
	}

	public String getAgreementNumber() {
		return agreementNumber;
	}

	public void setAgreementNumber(String agreementNumber) {
		this.agreementNumber = agreementNumber;
	}

	public ContractAgreement getContractAgreement() {
		return contractAgreement;
	}

	public void setContractAgreement(ContractAgreement contractAgreement) {
		this.contractAgreement = contractAgreement;
	}

	public ExternalAccount getExternalAccount() {
		return externalAccount;
	}

	public void setExternalAccount(ExternalAccount externalAccount) {
		this.externalAccount = externalAccount;
	}

	public Date getAgreementDate() {
		return agreementDate;
	}

	public void setAgreementDate(Date agreementDate) {
		this.agreementDate = agreementDate;
	}
	
	public String getActiveInd() {
		return activeInd;
	}

	public void setActiveInd(String activeInd) {
		this.activeInd = activeInd;
	}

	@Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ClientAgreement)) {
            return false;
        }
        ClientAgreement other = (ClientAgreement) object;
        if ((this.clientAgreementId == null && other.clientAgreementId != null) || (this.clientAgreementId != null && !this.clientAgreementId.equals(other.clientAgreementId))) {
            return false;
        }
        return true;
    }
}
