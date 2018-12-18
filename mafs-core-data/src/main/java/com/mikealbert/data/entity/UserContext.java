package com.mikealbert.data.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.Size;

import com.mikealbert.data.entity.BaseEntity;

/**
 * Mapped to USER_CONTEXTS table
 */
@Entity
@Table(name = "USER_CONTEXTS")
public class UserContext extends BaseEntity
{
	@EmbeddedId
	private UserContextPK id;
	
	@Column(name="WORK_CLASS", nullable=false, insertable=false, updatable=false, length=15)
	private String workClass;
	@Column(name="EMPLOYEE_NO", nullable=false, insertable=false, updatable=false, length=25)
	private String employeeNo;
	
	@Column(name="USERNAME", nullable=false, insertable=false, updatable=false, length=25)
	private String username;
	
	@Size(max=40)
	@Column(name="AD_USERNAME")
	private String adUsername;
	    
	public UserContextPK getId() {
		return id;
	}
	public void setId(UserContextPK id) {
		this.id = id;
	} 
	public String getWorkClass() {
		return workClass;
	}
	public String getEmployeeNo() {
		return employeeNo;
	}
	public String getUsername() {
		return username;
	}
	public String getAdUsername() {
		return adUsername;
	}
	public void setAdUsername(String adUsername) {
		this.adUsername = adUsername;
	}
}