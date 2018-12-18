package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.mikealbert.data.beanvalidation.MANotNull;
import com.mikealbert.data.beanvalidation.MASize;

/**
 * Composite Primary Key for EXTERNAL_ACCOUNTS table
 * @author sibley
 */
@Embeddable
public class ExternalAccountPK implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "C_ID")
    private long cId;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "ACCOUNT_TYPE")
    private String accountType;
    
    @Basic(optional = false)
    @MANotNull(label = "Account")
    @MASize(label = "Account", min = 1, max = 25)
    @Column(name = "ACCOUNT_CODE")
    private String accountCode;

    public ExternalAccountPK() {}

    public ExternalAccountPK(long cId, String accountType, String accountCode) {
        this.cId = cId;
        this.accountType = accountType;
        this.accountCode = accountCode;
    }

    public long getcId() {
		return cId;
	}

	public void setcId(long cId) {
		this.cId = cId;
	}

	public long getCId() {
        return cId;
    }

    public void setCId(long cId) {
        this.cId = cId;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getAccountCode() {
        return accountCode;
    }

    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) cId;
        hash += (accountType != null ? accountType.hashCode() : 0);
        hash += (accountCode != null ? accountCode.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ExternalAccountPK)) {
            return false;
        }
        ExternalAccountPK other = (ExternalAccountPK) object;
        if (this.getCId() != other.getCId()) {
            return false;
        }
        if ((this.getAccountType() == null && other.getAccountType() != null) || (this.getAccountType() != null && !this.getAccountType().equals(other.getAccountType()))) {
            return false;
        }
        if ((this.getAccountCode() == null && other.getAccountCode() != null) || (this.getAccountCode() != null && !this.getAccountCode().equals(other.getAccountCode()))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mikealbert.entity.ExternalAccountsPK[ cId=" + cId + ", accountType=" + accountType + ", accountCode=" + accountCode + " ]";
    }
    
}
