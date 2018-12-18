package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


/**
 * The persistent class for the VEHICLE_CONFIG_GROUPINGS database table.
 * 
 */
@Entity
@Table(name="VEHICLE_CONFIG_GROUPINGS")
public class VehicleConfigGrouping extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 955129842755388342L;

	@Id
	@SequenceGenerator(name="VCGID_GENERATOR", sequenceName="VCG_SEQ", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="VCGID_GENERATOR")
	@Column(name="VCG_ID")
	private Long vcgId;

	@Column(name="DESCRIPTION")
	private String description;

	@Column(name="NAME")
	private String name;

	//bi-directional many-to-one association to VehicleConfiguration
	@JoinColumn(name="VCF_VCF_ID", referencedColumnName = "VCF_ID")
	@ManyToOne(optional = false)
	private VehicleConfiguration vehicleConfiguration;

	//bi-directional many-to-one association to VehicleConfigUpfitQuote
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "vehicleConfigGrouping")
	private List<VehicleConfigUpfitQuote> vehicleConfigUpfitQuotes;
	
	public VehicleConfigGrouping() {
	}

	public Long getVcgId() {
		return this.vcgId;
	}

	public void setVcgId(Long vcgId) {
		this.vcgId = vcgId;
	}


	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public VehicleConfiguration getVehicleConfiguration() {
		return this.vehicleConfiguration;
	}

	public void setVehicleConfiguration(VehicleConfiguration vehicleConfiguration) {
		this.vehicleConfiguration = vehicleConfiguration;
	}

	public List<VehicleConfigUpfitQuote> getVehicleConfigUpfitQuotes() {
		return this.vehicleConfigUpfitQuotes;
	}

	public void setVehicleConfigUpfitQuotes(List<VehicleConfigUpfitQuote> vehicleConfigUpfitQuotes) {
		this.vehicleConfigUpfitQuotes = vehicleConfigUpfitQuotes;
	}

}