package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


/**
 * The persistent class for the VEHICLE_ACQUISITION_TYPES database table.
 * 
 */
@Entity
@Table(name="VEHICLE_ACQUISITION_TYPES")
public class VehicleAcquisitionType implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="VAT_SEQ")    
    @SequenceGenerator(name="VAT_SEQ", sequenceName="VAT_SEQ", allocationSize=1)
    @Column(name = "VAT_ID")
    private Long vatId;
    
    @Column(name = "NAME")
	private String name;

    @Column(name = "CODE")
	private String code;

	public Long getVatId() {
		return vatId;
	}

	public void setVatId(Long vatId) {
		this.vatId = vatId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}