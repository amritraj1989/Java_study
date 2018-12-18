package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
*
* @author sibley
*/
@Entity
@Table(name = "RESOURCE_PERMISSIONS")
public class ResourcePermission implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="RESP_SEQ")    
    @SequenceGenerator(name="RESP_SEQ", sequenceName="RESP_SEQ", allocationSize=1)  
    @NotNull
    @Column(name = "RESP_ID")
    private Long respId;
    
    @JoinColumn(name = "RES_RES_ID", referencedColumnName = "RES_ID")
    @ManyToOne(optional = false)
    private AppResource resource;
    
    @JoinColumn(name = "PRS_PRS_ID", referencedColumnName = "PRS_ID")
    @ManyToOne(optional = false)
    private PermissionSet permissionSet;

    public ResourcePermission() {
    }

    public ResourcePermission(Long respId) {
        this.respId = respId;
    }

    public Long getRespId() {
        return respId;
    }

    public void setRespId(Long respId) {
        this.respId = respId;
    }


    public AppResource getResource() {
		return resource;
	}

	public void setResource(AppResource resource) {
		this.resource = resource;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (respId != null ? respId.hashCode() : 0);
        return hash;
    }

    public PermissionSet getPermissionSet() {
		return permissionSet;
	}

	public void setPermissionSet(PermissionSet permissionSet) {
		this.permissionSet = permissionSet;
	}

	@Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ResourcePermission)) {
            return false;
        }
        ResourcePermission other = (ResourcePermission) object;
        if ((this.respId == null && other.respId != null) || (this.respId != null && !this.respId.equals(other.respId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mikealbert.entity.ResourcePermissions[ respId=" + respId + " ]";
    }    
}
