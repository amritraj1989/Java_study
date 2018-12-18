package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * Mapped to VSESSION table
 */
@Entity
@Table(name="VSESSION")
public class OraSession implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="AUDSID")
	private long sessionId;
	
	@Column(name="USERNAME", nullable=false, length=30)
	private String username;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public long getSessionId() {
		return sessionId;
	}

	public void setSessionId(long sessionId) {
		this.sessionId = sessionId;
	}
	
}