package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the VEHICLE_CONFIG_UPFIT_QUOTES database table.
 * 
 */
@Entity
@Table(name = "VEHICLE_CONFIG_UPFIT_QUOTES")
public class VehicleConfigUpfitQuote extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "VUQID_GENERATOR", sequenceName = "VUQ_SEQ", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "VUQID_GENERATOR")
	@Column(name = "VUQ_ID")
	private Long vuqId;

	@Temporal(TemporalType.DATE)
	@Column(name = "LAST_UPDATED_DATE", nullable = false)
	private Date lastUpdatedDate;

	@Column(name = "LAST_UPDATED_USER")
	private String lastUpdatedUser;
	
	@Column(name = "OBSOLETE_YN", length = 1)
	private String obsoleteYn;

	// bi-directional many-to-one association to VehicleConfigGrouping
	@JoinColumn(name = "VCG_VCG_ID", referencedColumnName = "VCG_ID")
	@ManyToOne(optional = false)
	private VehicleConfigGrouping vehicleConfigGrouping;
	
	@JoinColumn(name = "UFQ_UFQ_ID", referencedColumnName = "UFQ_ID")
    @ManyToOne(optional = false)
    private UpfitterQuote upfitterQuote;

	public VehicleConfigUpfitQuote() {}

	public Long getVuqId() {
		return this.vuqId;
	}

	public void setVuqId(Long vuqId) {
		this.vuqId = vuqId;
	}

	public Date getLastUpdatedDate() {
		return this.lastUpdatedDate;
	}

	public void setLastUpdatedDate(Date lastUpdatedDate) {
		this.lastUpdatedDate = lastUpdatedDate;
	}

	public String getLastUpdatedUser() {
		return this.lastUpdatedUser;
	}

	public void setLastUpdatedUser(String lastUpdatedUser) {
		this.lastUpdatedUser = lastUpdatedUser;
	}

	public String getObsoleteYn() {
		return obsoleteYn;
	}

	public void setObsoleteYn(String obsoleteYn) {
		this.obsoleteYn = obsoleteYn;
	}

	public VehicleConfigGrouping getVehicleConfigGrouping() {
		return this.vehicleConfigGrouping;
	}

	public void setVehicleConfigGrouping(VehicleConfigGrouping vehicleConfigGrouping) {
		this.vehicleConfigGrouping = vehicleConfigGrouping;
	}

	public UpfitterQuote getUpfitterQuote() {
		return upfitterQuote;
	}

	public void setUpfitterQuote(UpfitterQuote upfitterQuote) {
		this.upfitterQuote = upfitterQuote;
	}

}