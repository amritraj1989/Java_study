package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Composite Key on VehicleOdometerReadingsV
 */

@Embeddable
public class VehicleOdometerReadingsVPK implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Column(name = "FMS_ID")
	private Long fmsId;
	
    @Column(name = "ODOR_ID")
    private Long odorId;

	public Long getFmsId() {
		return fmsId;
	}

	public void setFmsId(Long fmsId) {
		this.fmsId = fmsId;
	}

	public Long getOdorId() {
		return odorId;
	}

	public void setOdorId(Long odorId) {
		this.odorId = odorId;
	}
	
}
