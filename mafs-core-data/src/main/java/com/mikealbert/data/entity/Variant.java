package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;



/**
 * The persistent class for the VARIANT database table.
 * 
 */
@Entity
@Table(name="VARIANTS")
public class Variant implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="VARIANT_CODE")
	private String variantCode;

	@Column(name="VARIANT_DESC")
	private String variantDesc;

	public Variant() {
	}

	public String getVariantCode() {
		return variantCode;
	}

	public void setVariantCode(String variantCode) {
		this.variantCode = variantCode;
	}

	public String getVariantDesc() {
		return variantDesc;
	}

	public void setVariantDesc(String variantDesc) {
		this.variantDesc = variantDesc;
	}

}