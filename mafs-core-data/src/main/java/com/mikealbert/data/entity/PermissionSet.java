package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Mapped to PERMISSION_SETS table
 */
@Entity
@Table(name = "PERMISSION_SETS")
public class PermissionSet implements Serializable {
	private static final long serialVersionUID = -4153330105790213688L;

    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "PRS_ID")
    private Long id;
	
    @Column(name = "PERMISSION_SET", nullable=false, insertable=false, updatable=false, length=40)
    private String permissionSet;
    
    @Column(name = "DESCRIPTION", nullable=false, insertable=false, updatable=false, length=2000)
    private String description;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "permissionSet")
    private List<ResourcePermission> resourcePermissions;    
    
	public String getPermissionSetName() {
		return permissionSet;
	}

	public void getPermissionSetName(String permissionSet) {
		this.permissionSet = permissionSet;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public Long getId() {
		return id;
	}

	public List<ResourcePermission> getResourcePermissions() {
		return resourcePermissions;
	}

	public void setResourcePermissions(List<ResourcePermission> resourcePermissions) {
		this.resourcePermissions = resourcePermissions;
	}
	
}
