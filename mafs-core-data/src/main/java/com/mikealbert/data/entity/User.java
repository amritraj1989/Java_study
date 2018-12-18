package com.mikealbert.data.entity;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

import com.mikealbert.data.enumeration.CorporateEntity;

public class User extends org.springframework.security.core.userdetails.User {
	
	public User(String username, String password, String employeeNo, CorporateEntity userCorpEntity, boolean enabled,
			boolean accountNonExpired, boolean credentialsNonExpired,
			boolean accountNonLocked,
			Collection<? extends GrantedAuthority> authorities) {
		super(username, password, enabled, accountNonExpired, credentialsNonExpired,
				accountNonLocked, authorities);
		this.userCorpEntity = userCorpEntity;
		this.employeeNo = employeeNo;
	}
	

	public User(String username, String password, String employeeNo,String workclass ,  CorporateEntity userCorpEntity, boolean enabled,
			boolean accountNonExpired, boolean credentialsNonExpired,
			boolean accountNonLocked,
			Collection<? extends GrantedAuthority> authorities) {
		super(username, password, enabled, accountNonExpired, credentialsNonExpired,
				accountNonLocked, authorities);
		this.userCorpEntity = userCorpEntity;
		this.employeeNo = employeeNo;
		this.workclass = workclass;
	}
	public User(String username, String password, String employeeNo, CorporateEntity userCorpEntity,
			Collection<? extends GrantedAuthority> authorities) {
		super(username, password, authorities);
		this.userCorpEntity = userCorpEntity;
		this.employeeNo = employeeNo;
	}

	
	public User() {// created for Junit to Mock User Object
		
		super("test", "password", new ArrayList<GrantedAuthority>());
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 4759063521302189686L;

	private CorporateEntity userCorpEntity;
	private String employeeNo;
	private String workclass;
	

	/**
	 * Returns the corporate entity a user belongs to
	 * 
	 */
	
	public CorporateEntity getCorporateEntity() {
		return userCorpEntity;
	}

	public String getEmployeeNo() {
		return employeeNo;
	}
	
	// created for Junit to Mock User's emplyee no
	public void setEmployeeNo(String employeeNo) {
		this.employeeNo = employeeNo;
	}

	public String getWorkclass() {
	    return workclass;
	}

	public void setWorkclass(String workclass) {
	    this.workclass = workclass;
	}
}
