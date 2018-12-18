package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Primary key on DOC_NOS table
 * @author sibley
 */
@Embeddable
public class DocumentNumberPK implements Serializable {
   
    @NotNull
    @Column(name = "C_ID")
    private long cId;
    
    @Size(min = 1, max = 10)
    @Column(name = "DOMAIN")
    private String domain;

    public DocumentNumberPK() {}

    public DocumentNumberPK(long cId, String domain) {
        this.cId = cId;
        this.domain = domain;
    }

    public long getCId() {
        return cId;
    }

    public void setCId(long cId) {
        this.cId = cId;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) cId;
        hash += (domain != null ? domain.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DocumentNumberPK)) {
            return false;
        }
        DocumentNumberPK other = (DocumentNumberPK) object;
        if (this.cId != other.cId) {
            return false;
        }
        if ((this.domain == null && other.domain != null) || (this.domain != null && !this.domain.equals(other.domain))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mikealbert.data.entity.DocNosPK[ cId=" + cId + ", domain=" + domain + " ]";
    }    
}
