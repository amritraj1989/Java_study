package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.*;

import javax.validation.constraints.Size;

import java.util.List;


/**
 * Mapped to MAKES table
 * @author Raj
 */
@Entity
@Table(name="MAKES")
public class Make implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="MAK_ID")
	private Long makId;

	//bi-directional many-to-one association to ModelType
	@ManyToOne
	@JoinColumn(name="MTP_MTP_ID")
	private ModelType modelType;

	@Column(name="MAKE_CODE")
	private String makeCode;

	@Column(name="MAKE_DESC")
	private String makeDesc;
	
	//bi-directional many-to-one association to MakeModelRange
	@OneToMany(mappedBy="make")
	private List<MakeModelRange> makeModelRanges;

	//bi-directional many-to-one association to SupplierDiscount
	@OneToMany(mappedBy="make")
	private List<ServiceProviderDiscount> serviceProviderDiscounts;

	//bi-directional many-to-one association to ServiceProviderFranchise
	@OneToMany(mappedBy="make")
	private List<ServiceProviderFranchise> serviceProviderFranchises;
	
	@OneToMany(mappedBy="make")
	private List<VehicleOrderingFee> vehicleOrderingFees;	
	
	@OneToMany(mappedBy="make")
	private List<Model> models;

	public long getMakId() {
		return makId;
	}

	public void setMakId(long makId) {
		this.makId = makId;
	}

	public ModelType getModelType() {
		return modelType;
	}

	public void setModelType(ModelType modelType) {
		this.modelType = modelType;
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

	public String getMakeCode() {
		return makeCode;
	}

	public void setMakeCode(String makeCode) {
		this.makeCode = makeCode;
	}

	public String getMakeDesc() {
		return makeDesc;
	}

	public void setMakeDesc(String makeDesc) {
		this.makeDesc = makeDesc;
	}

	public void setMakId(Long makId) {
		this.makId = makId;
	}		

	public List<Model> getModels() {
		return models;
	}

	public void setModels(List<Model> models) {
		this.models = models;
	}
	
	public List<ServiceProviderFranchise> getServiceProviderFranchises() {
		return serviceProviderFranchises;
	}

	public void setServiceProviderFranchises(List<ServiceProviderFranchise> serviceProviderFranchises) {
		this.serviceProviderFranchises = serviceProviderFranchises;
	}

	public List<VehicleOrderingFee> getVehicleOrderingFees() {
		return vehicleOrderingFees;
	}

	public void setVehicleOrderingFees(List<VehicleOrderingFee> vehicleOrderingFees) {
		this.vehicleOrderingFees = vehicleOrderingFees;
	}
}