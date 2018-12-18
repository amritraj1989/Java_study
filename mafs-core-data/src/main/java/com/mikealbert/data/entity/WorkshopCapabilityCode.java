package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "WORKSHOP_CAPABILITY_CODES")
public class WorkshopCapabilityCode implements Serializable {
	private static final long serialVersionUID = 2328759158023077763L;

	@Id
    @NotNull
    @Column(name = "WORKSHOP_CAPABILITY")    
    private String workshopCapability;

	@NotNull
    @Column(name = "DESCRIPTION")
    private String description;

	public String getWorkshopCapability() {
		return workshopCapability;
	}

	public void setWorkshopCapability(String workshopCapability) {
		this.workshopCapability = workshopCapability;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
