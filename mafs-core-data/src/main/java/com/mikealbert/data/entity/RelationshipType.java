package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.mikealbert.data.beanvalidation.MADate;
import com.mikealbert.data.beanvalidation.MANotNull;

/**
 * Mapped to RELATIONSHIP_TYPE_CODES table
 * @author sibley
 */
@Entity
@Table(name = "RELATIONSHIP_TYPE_CODES")
public class RelationshipType extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @Basic(optional = false)
    @Size(max = 10)
    @Column(name = "RELATIONSHIP_TYPE_CODE")
    private String code;
    
    @Basic(optional = false)
    @Size(max = 80)
    @Column(name = "RELATIONSHIP_TYPE_DESCRIPTION")
    private String description;
    
    public RelationshipType() {}

    public RelationshipType(String code, String description) {
        this.setCode(code);
        this.setDescription(description);
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

	
	
	@Override
    public int hashCode() {
        int hash = 0;
        hash += (getCode() != null ? getCode().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RelationshipType)) {
            return false;
        }
        RelationshipType other = (RelationshipType) object;
        if ((this.getCode() == null && other.getCode() != null) || (this.getCode() != null && !this.getCode().equals(other.getCode()))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mikealbert.entity.RelationshipType[ relationshipType=" + getCode() + " ]";
    }
    
}
