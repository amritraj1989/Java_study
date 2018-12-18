package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.mikealbert.data.beanvalidation.MANotNull;

/**
 * Mapped to ODO_READING_TYPE_CODES table
 */
@Entity
@Table(name = "ODO_READING_TYPE_CODES")
public class OdometerReadingCode extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "ODO_READING_TYPE")
    private String code;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 80)
    @Column(name = "DESCRIPTION")
    private String description;
    
    @Size(max = 1)
    @Column(name = "STATEMENT_IND")
    private String statementInd;
       
    public OdometerReadingCode() {}

    public OdometerReadingCode(String odoReadingType) {
        this.setCode(odoReadingType);
    }

    public OdometerReadingCode(String odoReadingType, String description) {
        this.setCode(odoReadingType);
        this.description = description;
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

    public String getStatementInd() {
        return statementInd;
    }

    public void setStatementInd(String statementInd) {
        this.statementInd = statementInd;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (getCode() != null ? getCode().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof OdometerReadingCode)) {
            return false;
        }
        OdometerReadingCode other = (OdometerReadingCode) object;
        if ((this.getCode() == null && other.getCode() != null) || (this.getCode() != null && !this.getCode().equals(other.getCode()))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mikealbert.entity.OdometerReadingCode[ code=" + getCode() + " ]";
    }
        
}
