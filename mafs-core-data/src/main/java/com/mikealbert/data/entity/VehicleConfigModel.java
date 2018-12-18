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
 * The persistent class for the VEHICLE_CONFIG_MODELS database table.
 * 
 */
@Entity
@Table(name = "VEHICLE_CONFIG_MODELS")
public class VehicleConfigModel extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "VCMID_GENERATOR", sequenceName = "VCM_SEQ", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "VCMID_GENERATOR")
	@Column(name = "VCM_ID")
	private Long vcmId;
	
	@Column(name = "MFG_CODE")
	private String mfgCode;	
	
	@Column(name = "YEAR")
	private String year;	
	
	@Column(name = "MAKE")
	private String make;	

	@Temporal(TemporalType.DATE)
	@Column(name = "LAST_UPDATED_DATE")
	private Date lastUpdatedDate;

	@Column(name = "LAST_UPDATED_USER")
	private String lastUpdatedUser;
	
	@Column(name = "OBSOLETE_YN", length = 1)
	private String obsoleteYn;

	// bi-directional many-to-one association to VehicleConfigGrouping
	@JoinColumn(name = "MDL_MDL_ID", referencedColumnName = "MDL_ID")
	@ManyToOne
	private Model model;
	
	//bi-directional many-to-one association to ModelType
	@JoinColumn(name="MTP_MTP_ID", referencedColumnName = "MTP_ID")
	@ManyToOne
	private ModelType modelType;
	
	//bi-directional many-to-one association to MakeModelRange
	@JoinColumn(name="MRG_MRG_ID", referencedColumnName = "MRG_ID")
	@ManyToOne
	private MakeModelRange makeModelRange;
	
	// bi-directional many-to-one association to VehicleConfiguration
	@JoinColumn(name="VCF_VCF_ID", referencedColumnName = "VCF_ID")
	@ManyToOne(optional = false)
	private VehicleConfiguration vehicleConfiguration;

	public VehicleConfigModel() {}

	public Long getVcmId() {
		return this.vcmId;
	}

	public void setVcmId(Long vcmId) {
		this.vcmId = vcmId;
	}

	public String getMfgCode() {
		return mfgCode;
	}

	public void setMfgCode(String mfgCode) {
		this.mfgCode = mfgCode;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getMake() {
		return make;
	}

	public void setMake(String make) {
		this.make = make;
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

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public ModelType getModelType() {
		return modelType;
	}

	public void setModelType(ModelType modelType) {
		this.modelType = modelType;
	}

	public MakeModelRange getMakeModelRange() {
		return makeModelRange;
	}

	public void setMakeModelRange(MakeModelRange makeModelRange) {
		this.makeModelRange = makeModelRange;
	}

	public VehicleConfiguration getVehicleConfiguration() {
		return vehicleConfiguration;
	}

	public void setVehicleConfiguration(VehicleConfiguration vehicleConfiguration) {
		this.vehicleConfiguration = vehicleConfiguration;
	}

}