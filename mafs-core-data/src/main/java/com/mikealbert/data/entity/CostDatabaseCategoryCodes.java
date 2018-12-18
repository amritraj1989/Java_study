package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

/**
 * Mapped to COST_DATABASE_CATEGORY_CODES Table
 */
@Entity
@Table(name = "COST_DATABASE_CATEGORY_CODES")
public class CostDatabaseCategoryCodes implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @Size(max = 10)
    @Column(name = "COST_DATABASE_CATEGORY")
    private String category;
        
    @Size(max=80)
    @Column(name = "DESCRIPTION")
    private String description;                  

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
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
        hash += (getCategory() != null ? getCategory().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CostDatabaseCategoryCodes)) {
            return false;
        }
        CostDatabaseCategoryCodes other = (CostDatabaseCategoryCodes) object;
        if ((this.getCategory() == null && other.getCategory() != null) || (this.getCategory() != null && !this.getCategory().equals(other.getCategory()))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mikealbert.data.entity.CostDatabaseCategoryCodes[ category=" + getCategory() + " ]";
    }

    
}