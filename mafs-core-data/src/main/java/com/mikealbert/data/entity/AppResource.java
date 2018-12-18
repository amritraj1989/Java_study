package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
*
* @author sibley
*/
@Entity
@Table(name = "RESOURCES")
public class AppResource implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="RES_SEQ")    
    @SequenceGenerator(name="RES_SEQ", sequenceName="RES_SEQ", allocationSize=1)    
    @NotNull
    @Column(name = "RES_ID")
    private Long resId;
    
    @Size(min = 1, max = 40)
    @Column(name = "RESOURCE_NAME")
    private String resourceName;
        
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "resource")
    private List<ResourcePermission> permissions;

    public AppResource() {}

    public AppResource(Long resId) {
        this.resId = resId;
    }

    public AppResource(Long resId, String resourceName) {
        this.resId = resId;
        this.resourceName = resourceName;
    }

    public Long getResId() {
        return resId;
    }

    public void setResId(Long resId) {
        this.resId = resId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }



    
    public List<ResourcePermission> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<ResourcePermission> permissions) {
		this.permissions = permissions;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (resId != null ? resId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AppResource)) {
            return false;
        }
        AppResource other = (AppResource) object;
        if ((this.resId == null && other.resId != null) || (this.resId != null && !this.resId.equals(other.resId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mikealbert.entity.Resources[ resId=" + resId + " ]";
    }
}
