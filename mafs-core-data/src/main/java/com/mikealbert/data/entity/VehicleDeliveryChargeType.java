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
 * The persistent class for the VEHICLE_DELIVERY_CHARGE_TYPES database table.
 * 
 */
@Entity
@Table(name="VEHICLE_DELIVERY_CHARGE_TYPES")
public class VehicleDeliveryChargeType extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="VDCT_SEQ")    
    @SequenceGenerator(name="VDCT_SEQ", sequenceName="VDCT_SEQ", allocationSize=1)
    @Column(name = "VDCT_ID")
    private Long vdctId;
    
    @Column(name = "NAME")
	private String name;

    @Column(name = "CODE")
	private String code;

    @Column(name = "DESCRIPTION")
	private String description;

	public Long getVdctId() {
		return vdctId;
	}

	public void setVdctId(Long vdctId) {
		this.vdctId = vdctId;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((vdctId == null) ? 0 : vdctId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VehicleDeliveryChargeType other = (VehicleDeliveryChargeType) obj;
		if (vdctId == null) {
			if (other.vdctId != null)
				return false;
		} else if (!vdctId.equals(other.vdctId))
			return false;
		return true;
	}
    
	
}