package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.Size;

import java.util.List;


/**
 * Mapped to MAKE_MODEL_RANGES table
 * @author Raj
 */
@Entity
@Table(name="MAKE_MODEL_RANGES")
public class MakeModelRange implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="MRG_ID")
	private long mrgId;
	
	@Column(name="MAKE_MODEL_RANGE_CODE")
	private String modelCode;
	
	@Column(name="MAKE_MODEL_DESC")
	@Size(max=80)
	private String description;

	//bi-directional many-to-one association to Make
	@ManyToOne
	@JoinColumn(name="MAK_MAK_ID")
	private Make make;

	//bi-directional many-to-one association to ModelType
	@ManyToOne
	@JoinColumn(name="MTP_MTP_ID")
	private ModelType modelType;

	//bi-directional many-to-one association to SupplierDiscount
	@OneToMany(mappedBy="makeModelRange")
	private List<ServiceProviderDiscount> serviceProviderDiscounts;
	
	@OneToMany(mappedBy="makeModelRange")
	private List<Model> models;		
	
	//bi-directional many-to-one association to VehicleConfigModel
	@OneToMany(mappedBy="makeModelRange")
	private List<VehicleConfigModel> vehicleConfigModels;


	public long getMrgId() {
		return mrgId;
	}

	public void setMrgId(long mrgId) {
		this.mrgId = mrgId;
	}

	public String getModelCode() {
		return modelCode;
	}

	public void setModelCode(String modelCode) {
		this.modelCode = modelCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Make getMake() {
		return make;
	}

	public void setMake(Make make) {
		this.make = make;
	}

	public ModelType getModelType() {
		return modelType;
	}

	public void setModelType(ModelType modelType) {
		this.modelType = modelType;
	}

	public List<ServiceProviderDiscount> getServiceProviderDiscounts() {
		return serviceProviderDiscounts;
	}

	public void setServiceProviderDiscounts(List<ServiceProviderDiscount> serviceProviderDiscounts) {
		this.serviceProviderDiscounts = serviceProviderDiscounts;
	}

	public List<Model> getModels() {
		return models;
	}

	public void setModels(List<Model> models) {
		this.models = models;
	}

	public List<VehicleConfigModel> getVehicleConfigModels() {
		return vehicleConfigModels;
	}

	public void setVehicleConfigModels(List<VehicleConfigModel> vehicleConfigModels) {
		this.vehicleConfigModels = vehicleConfigModels;
	}
}