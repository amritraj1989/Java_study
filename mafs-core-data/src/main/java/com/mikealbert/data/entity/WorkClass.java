package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
/**
 * Mapped to WORK_CLASSES table
 */
@Entity
@Table(name = "WORK_CLASSES")
public class WorkClass implements Serializable {
	private static final long serialVersionUID = 2095548721330304388L;

	@EmbeddedId
	private WorkClassPK id;
	
	@Column(name="DESCRIPTION", nullable=true, insertable=false, updatable=false, length=240)
	private String description;
	    
	public WorkClassPK getId() {
		return id;
	}
	public void setId(WorkClassPK id) {
		this.id = id;
	} 
	public String getDescription() {
		return description;
	}
	/** helper method so a caller does not have to use the PK class just to get the name of the Work Class
	 * 
	 * @return the WorkClass (name) which is what is commonly displayed, used and referred to in the system and in the business
	 */
	public String getWorkClassName() {
		return this.id.getWorkClass();
	}
}