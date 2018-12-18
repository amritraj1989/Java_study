package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "SUPPLIER_CATEGORY_CODES")
public class SupplierCategoryCode implements Serializable {
	private static final long serialVersionUID = -7228743193768635908L;

	@Id
    @NotNull
    @Column(name = "SUPPLIER_CATEGORY")
	private String supplierCategory;

	@NotNull
    @Column(name = "DESCRIPTION")
    private String description;

	public String getSupplierCategory() {
		return supplierCategory;
	}

	public void setSupplierCategory(String supplierCategory) {
		this.supplierCategory = supplierCategory;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
