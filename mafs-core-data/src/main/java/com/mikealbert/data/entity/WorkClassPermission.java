package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Mapped to WORK_CLASS_PERMISSIONS table
 */
@Entity
@Table(name = "WORK_CLASS_PERMISSIONS")
public class WorkClassPermission implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="WPS_SEQ")    
	@SequenceGenerator(name="WPS_SEQ", sequenceName="WPS_SEQ", allocationSize=1)   
	@NotNull
	@Column(name = "WPS_ID")
	private Long id;

    @JoinColumns({
        @JoinColumn(name = "C_ID", referencedColumnName = "C_ID"),
        @JoinColumn(name = "WORK_CLASS", referencedColumnName = "WORK_CLASS")})
    @ManyToOne
    private WorkClass workClass;
	
	@JoinColumn(name = "PRS_PRS_ID", referencedColumnName = "PRS_ID")
	@ManyToOne(optional = false)
	private PermissionSet permissionSet;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public WorkClass getWorkClass() {
		return workClass;
	}

	public void setWorkClass(WorkClass workClass) {
		this.workClass = workClass;
	}

	public PermissionSet getPermissionSet() {
		return permissionSet;
	}

	public void setPermissionSet(PermissionSet permissionSet) {
		this.permissionSet = permissionSet;
	}


}
