
/**
 * SupplierServiceTypeCodes.java
 * mafs-core-data
 * Oct 7, 2013
 * 2:27:54 PM
 */
package com.mikealbert.data.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author anand.mohan
 *
 */
@Entity
@Table(name= "SUPPLIER_SERVICE_TYPE_CODES")
public class SupplierServiceTypeCodes {
	@Id
	@Column(name= "SUPP_SERVICE_TYPE")
	private String	suppServiceTypeCode;
	
	@Column(name= "DESCRIPTION")
	private String	description;
	
	public String getSuppServiceTypeCode() {
		return suppServiceTypeCode;
	}
	public void setSuppServiceTypeCode(String suppServiceTypeCode) {
		this.suppServiceTypeCode = suppServiceTypeCode;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
}
