package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "VEH_MOVEMENT_CONTACTS")
public class VehicleMovementContact extends BaseEntity implements Serializable {
	private static final long serialVersionUID = -4409802896784394429L;

	@Id
	@Column(name = "VMC_ID")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "VMC_SEQ")
	@SequenceGenerator(name = "VMC_SEQ", sequenceName = "VMC_SEQ", allocationSize = 1)
	private Long vmcId;

	@JoinColumn(name = "MOVED_FROM", referencedColumnName = "VMAL_ID")
    @OneToOne(fetch = FetchType.LAZY)
	private VehicleMovementAddrLink movedFrom;
	
	@JoinColumn(name = "MOVED_TO", referencedColumnName = "VMAL_ID")
    @OneToOne(fetch = FetchType.LAZY)
	private VehicleMovementAddrLink movedTo;

	@Column(name = "CONTACT_NAME")
	private String contactName;

	@Column(name = "CONTACT_NUMBER")
	private String contactNumber;

	@JoinColumn(name = "VMOV_VMOV_ID", referencedColumnName = "VMOV_ID")
	@OneToOne(fetch = FetchType.EAGER)
	private VehicleMovement vehicleMovement;

	public VehicleMovementContact() {}

	public Long getVmcId() {
		return vmcId;
	}

	public void setVmcId(Long vmcId) {
		this.vmcId = vmcId;
	}

	public VehicleMovementAddrLink getMovedFrom() {
		return movedFrom;
	}

	public void setMovedFrom(VehicleMovementAddrLink movedFrom) {
		this.movedFrom = movedFrom;
	}

	public VehicleMovementAddrLink getMovedTo() {
		return movedTo;
	}

	public void setMovedTo(VehicleMovementAddrLink movedTo) {
		this.movedTo = movedTo;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public VehicleMovement getVehicleMovement() {
		return vehicleMovement;
	}

	public void setVehicleMovement(VehicleMovement vehicleMovement) {
		this.vehicleMovement = vehicleMovement;
	}
	
	@Override
	public int hashCode() {
		int hash = 0;
        hash += (vmcId != null ? vmcId.hashCode() : 0);
        return hash;
	}
	
	@Override
	public boolean equals(Object object) {
		if(!(object instanceof VehicleMovementContact)) {
			return false;
		}
		VehicleMovementContact other = (VehicleMovementContact) object;
		if((this.vmcId == null && other.vmcId != null) || (this.vmcId != null && !this.vmcId.equals(other.vmcId))) {
			return false;
		}
		return true;
	}

}