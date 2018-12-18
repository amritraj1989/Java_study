package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "PROFESSIONAL_BODY_CODES")
public class ProfessionalBodyCode implements Serializable {
	@Id
	@NotNull
	@Column(name = "PROFESSIONAL_BODY")
	private String professionalBody;
	
	@NotNull
	@Column(name = "DESCRIPTION")
	private String description;

	public String getProfessionalBody() {
		return professionalBody;
	}

	public void setProfessionalBody(String professionalBody) {
		this.professionalBody = professionalBody;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
