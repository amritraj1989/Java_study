package com.mikealbert.data.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


/**
 * Mapped to SUPPLIER_DISCOUNTS table
 * @author Scholle
 */
@Entity
@Table(name="SUPPLIER_FRANCHISES")
public class ServiceProviderFranchise implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SFR_SEQ")
	@SequenceGenerator(name="SFR_SEQ", sequenceName="SFR_SEQ", allocationSize=1)
	@Column(name="SFR_ID", unique=true, nullable=false, precision=12)
	private Long serviceProviderFranchiseId;

	@Column(name="DELIVERY_FEES")
	private BigDecimal deliveryFees;

	@Column(name="MAKE_CODE")
	private String makeCode;

	//bi-directional many-to-one association to ModelType
	@ManyToOne
	@JoinColumn(name="MTP_MTP_ID", referencedColumnName="MTP_ID", nullable=true)
	private ModelType modelType;

	//bi-directional many-to-one association to Make
	@ManyToOne
	@JoinColumn(name="MAK_ID", referencedColumnName="MAK_ID", nullable=true)
	private Make make;
	
	@JoinColumn(name="SUP_SUP_ID", referencedColumnName = "SUP_ID", nullable=false)
	@ManyToOne(optional = true)
	private ServiceProvider serviceProvider;

	public Long getServiceProviderFranchiseId() {
		return serviceProviderFranchiseId;
	}

	public void setServiceProviderFranchiseId(Long serviceProviderFranchiseId) {
		this.serviceProviderFranchiseId = serviceProviderFranchiseId;
	}

	public BigDecimal getDeliveryFees() {
		return deliveryFees;
	}

	public void setDeliveryFees(BigDecimal deliveryFees) {
		this.deliveryFees = deliveryFees;
	}

	public String getMakeCode() {
		return makeCode;
	}

	public void setMakeCode(String makeCode) {
		this.makeCode = makeCode;
	}

	public ModelType getModelType() {
		return modelType;
	}

	public void setModelType(ModelType modelType) {
		this.modelType = modelType;
	}

	public Make getMake() {
		return make;
	}

	public void setMake(Make make) {
		this.make = make;
	}

	public ServiceProvider getServiceProvider() {
		return serviceProvider;
	}

	public void setServiceProvider(ServiceProvider serviceProvider) {
		this.serviceProvider = serviceProvider;
	}
}