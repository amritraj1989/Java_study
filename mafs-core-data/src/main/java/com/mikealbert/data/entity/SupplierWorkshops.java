package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The persistent class for the SUPPLIER_WORKSHOPS database table.
 * @author Amritraj
 */
@Entity
@Table(name = "SUPPLIER_WORKSHOPS")
public class SupplierWorkshops implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "SWK_ID")
	private long swkId;// Add SequenceGenerator and other field mapping as needed

	@JoinColumn(name = "SUP_SUP_ID", referencedColumnName = "SUP_ID")
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
	private Supplier supplier;

	@Column(name = "WORKSHOP_CAPABILITY")
	private String workShopCapability;

	public long getSwkId() {
		return swkId;
	}

	public void setSwkId(long swkId) {
		this.swkId = swkId;
	}

	public String getWorkShopCapability() {
		return workShopCapability;
	}

	public void setWorkShopCapability(String workShopCapability) {
		this.workShopCapability = workShopCapability;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	
}