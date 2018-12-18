package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Composite PK for ClientFinances
 */
@Embeddable
public class ClientFinancePK implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 12)
    @Column(name = "EA_C_ID")
    private Long eaCId;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "EA_ACCOUNT_TYPE")
    private String eaAccountType;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 25)
    @Column(name = "EA_ACCOUNT_CODE")
    private String eaAccountCode;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "PARAMETER_ID")
    private String parameterId;

    public ClientFinancePK() {
    }

    public ClientFinancePK(Long eaCId, String eaAccountType, String eaAccountCode, String parameterId) {
        this.setEaCId(eaCId);
        this.setEaAccountType(eaAccountType);
        this.setEaAccountCode(eaAccountCode);
        this.setParameterId(parameterId);
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ClientFinancePK)) {
            return false;
        }
        ClientFinancePK other = (ClientFinancePK) object;
        if ((this.getEaCId() == null && other.getEaCId() != null) || (this.getEaCId() != null && !this.getEaCId().equals(other.getEaCId()))) {
            return false;
        }
        if ((this.getEaAccountType() == null && other.getEaAccountType() != null) || (this.getEaAccountType() != null && !this.getEaAccountType().equals(other.getEaAccountType()))) {
            return false;
        }
        if ((this.getEaAccountCode() == null && other.getEaAccountCode() != null) || (this.getEaAccountCode() != null && !this.getEaAccountCode().equals(other.getEaAccountCode()))) {
            return false;
        }
        if ((this.getParameterId() == null && other.getParameterId() != null) || (this.getParameterId() != null && !this.getParameterId().equals(other.getParameterId()))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mikealbert.entity.ClientFinancesPK[ eaCId=" + getEaCId() + ", eaAccountType=" + getEaAccountType() + ", eaAccountCode=" + getEaAccountCode() + ", parameterId=" + getParameterId();
    }

	public Long getEaCId() {
		return eaCId;
	}

	public void setEaCId(Long eaCId) {
		this.eaCId = eaCId;
	}

	public String getEaAccountType() {
		return eaAccountType;
	}

	public void setEaAccountType(String eaAccountType) {
		this.eaAccountType = eaAccountType;
	}

	public String getEaAccountCode() {
		return eaAccountCode;
	}

	public void setEaAccountCode(String eaAccountCode) {
		this.eaAccountCode = eaAccountCode;
	}

	public String getParameterId() {
		return parameterId;
	}

	public void setParameterId(String parameterId) {
		this.parameterId = parameterId;
	}

}
