package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * The persistent class for the PERSONNEL_BASE database table.
 * 
 */
@Entity
@Table(name = "PERSONNEL_BASE")
public class PersonnelBase implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@NotNull
	@Column(name = "EMPLOYEE_NO", updatable=false, insertable=false)
	private String employeeNo;

	@Size(min = 1, max = 10)
	@Column(name = "FIRST_NAME", updatable=false, insertable=false)
	private String firstName;

	@Size(min = 1, max = 80)
	@Column(name = "LAST_NAME", updatable=false, insertable=false)
	private String lastName;
	
	@Column(name = "EMAIL", updatable=false, insertable=false)
	private String email;

	public String getEmployeeNo() {
		return employeeNo;
	}

	public void setEmployeeNo(String employeeNo) {
		this.employeeNo = employeeNo;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}