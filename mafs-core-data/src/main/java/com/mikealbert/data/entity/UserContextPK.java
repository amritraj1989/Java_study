package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Composite Key on UserContext
 */
@Embeddable
public class UserContextPK implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name="C_ID", unique=true, nullable=false, precision=12)
	private long cId;
	
	@Column(name="USERNAME", nullable=false, length=30)
	private String username;

	public UserContextPK(long cId, String userName){
		this.cId = cId;
		this.username = userName;
	}
	
	public UserContextPK(){
		
	}
	
	public long getcId() {
		return cId;
	}

	public void setcId(long cId) {
		this.cId = cId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
}
