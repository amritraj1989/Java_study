package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

@Embeddable
public class ServiceProviderProfBodyPK implements Serializable{
	private static final long serialVersionUID = 5436072207185221425L;

	@NotNull
	@Column(name = "PBC_CODE")
	private String pbcCode;

	@NotNull
	@Column(name = "SUP_ID")
	private String supId;
	
	public String getPbcCode() {
		return pbcCode;
	}

	public void setPbcCode(String pbcCode) {
		this.pbcCode = pbcCode;
	}

	public String getSupId() {
		return supId;
	}

	public void setSupId(String supId) {
		this.supId = supId;
	}
}
