package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@Entity
@Table(name = "PRE_COLLECTION_CHECKS")
public class PreCollectionCheck  extends BaseEntity implements Serializable {
	private static final long serialVersionUID = -8028641530814451618L;

	@Id
	@Column(name="PCC_ID")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PCC_SEQ")
    @SequenceGenerator(name = "PCC_SEQ", sequenceName = "PCC_SEQ", allocationSize = 1)
	private Long pccId;

	@JoinColumn(name = "FMS_FMS_ID", referencedColumnName = "FMS_ID", nullable = false)
    @OneToOne(fetch = FetchType.LAZY)
	private FleetMaster fleetMaster;
	
	@Column(name="PCC_DATE")
	@Temporal( TemporalType.DATE)
	private Date pccDate;
	
	@Column(name = "VRR_VEHICLE_RETURN_REASON")
    private String vrrVehicleReturnReason;
	
	public PreCollectionCheck() {
	}

	public Long getPccId() {
		return pccId;
	}

	public void setPccId(Long pccId) {
		this.pccId = pccId;
	}

	public FleetMaster getFleetMaster() {
		return fleetMaster;
	}

	public void setFleetMaster(FleetMaster fleetMaster) {
		this.fleetMaster = fleetMaster;
	}

	public Date getPccDate() {
		return pccDate;
	}

	public void setPccDate(Date pccDate) {
		this.pccDate = pccDate;
	}

	public String getVrrVehicleReturnReason() {
		return vrrVehicleReturnReason;
	}

	public void setVrrVehicleReturnReason(String vrrVehicleReturnReason) {
		this.vrrVehicleReturnReason = vrrVehicleReturnReason;
	}

	@Override
	public int hashCode() {
		int hash = 0;
        hash += (pccId != null ? pccId.hashCode() : 0);
        return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof PreCollectionCheck)) {
            return false;
        }
        PreCollectionCheck other = (PreCollectionCheck) object;
        if ((this.pccId == null && other.pccId != null) || (this.pccId != null && !this.pccId.equals(other.pccId))) {
            return false;
        }
        return true;
	}

}