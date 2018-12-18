package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "FINANCE_PARAMETER_CATEGORIES")
public class FinanceParameterCategory extends BaseEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "FPC_ID")
	private long fpcId;
	
	@Column(name = "CATEGORY_NAME")
	private String categoryName;
	
	@Column(name = "DESCRIPTION")
	private String description;

	public long getFpcId() {
		return fpcId;
	}

	public void setFpcId(long fpcId) {
		this.fpcId = fpcId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
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
        hash += (getCategoryName() != null ? getCategoryName().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof FinanceParameterCategory)) {
            return false;
        }
        FinanceParameterCategory other = (FinanceParameterCategory) object;
        if ((this.getCategoryName() == null && other.getCategoryName() != null) || (this.getCategoryName() != null && !this.getCategoryName().equals(other.getCategoryName()))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mikealbert.data.entity.FinanceParameterCategory[ category=" + getCategoryName() + " ]";
    }	
	
}
