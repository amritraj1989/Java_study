package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Mapped to CONTRACT_AGREEMENTS table
 */

@Entity
@Table(name = "CONTRACT_AGREEMENTS")
public class ContractAgreement extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@NotNull
	@Column(name = "AGREEMENT_CODE")
	private String AgreementCode;
	
	@Column(name = "AGREEMENT_DESCRIPTION")
    private String agreementDescription;
	
	@Column(name = "SIGNED_DATE_REQUIRED")
    private String signedDateRequired;
    
    public ContractAgreement(){
    }

	public String getAgreementCode() {
		return AgreementCode;
	}

	public void setAgreementCode(String agreementCode) {
		AgreementCode = agreementCode;
	}

	public String getAgreementDescription() {
		return agreementDescription;
	}

	public void setAgreementDescription(String agreementDescription) {
		this.agreementDescription = agreementDescription;
	}

	public String getSignedDateRequired() {
		return signedDateRequired;
	}

	public void setSignedDateRequired(String signedDateRequired) {
		this.signedDateRequired = signedDateRequired;
	}
	
	@Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ContractAgreement)) {
            return false;
        }
        ContractAgreement other = (ContractAgreement) object;
        if ((this.AgreementCode == null && other.AgreementCode != null) || (this.AgreementCode != null && !this.AgreementCode.equals(other.AgreementCode))) {
            return false;
        }
        return true;
    }
}
