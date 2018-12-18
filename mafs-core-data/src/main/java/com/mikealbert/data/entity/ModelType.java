package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;


/**
 * Mapped to MODEL_TYPES table
 * @author Raj
 */
@Entity
@Table(name="MODEL_TYPES")
public class ModelType implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="MTP_ID")
	private long mtpId;
	
	@Column(name="MODEL_TYPE_DESC")
	@Size(max=80)
	private String description;

	//bi-directional many-to-one association to Make
	@OneToMany(mappedBy="modelType")
	private List<Make> makes;

	//bi-directional many-to-one association to MakeModelRange
	@OneToMany(mappedBy="modelType")
	private List<MakeModelRange> makeModelRanges;

	//bi-directional many-to-one association to SupplierDiscount
	@OneToMany(mappedBy="modelType")
	private List<ServiceProviderDiscount> serviceProviderDiscounts;
	
	//bi-directional many-to-one association to SupplierDiscount
	@OneToMany(mappedBy="modelType")
	private List<ModelMarkYear> modelMarkYears;	
	
	//bi-directional many-to-one association to SupplierDiscount
	@OneToMany(mappedBy="modelType")
	private List<AccessoryCode> accessoryCodes;	
	
	//bi-directional many-to-one association to VehicleConfigModel
	@OneToMany(mappedBy="modelType")
	private List<VehicleConfigModel> vehicleConfigModels;
	
	//bi-directional many-to-one association to ServiceProviderFranchise
	@OneToMany(mappedBy="modelType")
	private List<ServiceProviderFranchise> serviceProviderFranchises;

	public long getMtpId() {
		return mtpId;
	}

	public void setMtpId(long mtpId) {
		this.mtpId = mtpId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Make> getMakes() {
		return makes;
	}

	public void setMakes(List<Make> makes) {
		this.makes = makes;
	}

	public List<MakeModelRange> getMakeModelRanges() {
		return makeModelRanges;
	}

	public void setMakeModelRanges(List<MakeModelRange> makeModelRanges) {
		this.makeModelRanges = makeModelRanges;
	}

	public List<ServiceProviderDiscount> getServiceProviderDiscounts() {
		return serviceProviderDiscounts;
	}

	public void setServiceProviderDiscounts(List<ServiceProviderDiscount> serviceProviderDiscounts) {
		this.serviceProviderDiscounts = serviceProviderDiscounts;
	}

	public List<ModelMarkYear> getModelMarkYears() {
		return modelMarkYears;
	}

	public void setModelMarkYears(List<ModelMarkYear> modelMarkYears) {
		this.modelMarkYears = modelMarkYears;
	}

	public List<AccessoryCode> getAccessoryCodes() {
		return accessoryCodes;
	}

	public void setAccessoryCodes(List<AccessoryCode> accessoryCodes) {
		this.accessoryCodes = accessoryCodes;
	}

	public List<VehicleConfigModel> getVehicleConfigModels() {
		return vehicleConfigModels;
	}

	public void setVehicleConfigModels(List<VehicleConfigModel> vehicleConfigModels) {
		this.vehicleConfigModels = vehicleConfigModels;
	}

	public List<ServiceProviderFranchise> getServiceProviderFranchises() {
		return serviceProviderFranchises;
	}

	public void setServiceProviderFranchises(List<ServiceProviderFranchise> serviceProviderFranchises) {
		this.serviceProviderFranchises = serviceProviderFranchises;
	}
}