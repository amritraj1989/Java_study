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
@Table(name = "VEH_MOVEMENT_ADDR_LINKS")
public class VehicleMovementAddrLink  extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 537113769768803519L;

	@Id
	@Column(name="VMAL_ID")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "VMAL_SEQ")
    @SequenceGenerator(name = "VMAL_SEQ", sequenceName = "VMAL_SEQ", allocationSize = 1)
	private Long vmalId;

//	@Column(name="EAA_EAA_ID")
//	private Long eaaEaaId;
	
	@JoinColumn(name = "EAA_EAA_ID", referencedColumnName = "EAA_ID")
    @OneToOne(fetch = FetchType.LAZY)
	private ExtAccAddress extAccAddress;
	
	
	public VehicleMovementAddrLink() {
	}

	public Long getVmalId() {
		return vmalId;
	}

	public void setVmalId(Long vmalId) {
		this.vmalId = vmalId;
	}
/*
	public Long getEaaEaaId() {
		return eaaEaaId;
	}

	public void setEaaEaaId(Long eaaEaaId) {
		this.eaaEaaId = eaaEaaId;
	}
*/
	public ExtAccAddress getExtAccAddress() {
		return extAccAddress;
	}

	public void setExtAccAddress(ExtAccAddress extAccAddress) {
		this.extAccAddress = extAccAddress;
	}

	@Override
	public int hashCode() {
		int hash = 0;
        hash += (vmalId != null ? vmalId.hashCode() : 0);
        return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof VehicleMovementAddrLink)) {
            return false;
        }
        VehicleMovementAddrLink other = (VehicleMovementAddrLink) object;
        if ((this.vmalId == null && other.vmalId != null) || (this.vmalId != null && !this.vmalId.equals(other.vmalId))) {
            return false;
        }
        return true;
	}

}