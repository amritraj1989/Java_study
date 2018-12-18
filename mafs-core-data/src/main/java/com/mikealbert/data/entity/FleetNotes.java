package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
* Mapped to FLEET_NOTES table.
* 
* @author ravi sah
*/
@Entity
@Table(name = "FLEET_NOTES")
public class FleetNotes extends BaseEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="FNO_SEQ")    
    @SequenceGenerator(name="FNO_SEQ", sequenceName="FNO_SEQ", allocationSize=1)      
    @Basic(optional = false)
    @NotNull
    @Column(name = "FNO_ID")
    private Long fnoId;
    
    @JoinColumn(name = "FMS_FMS_ID", referencedColumnName = "FMS_ID")
    @ManyToOne(optional = false)
    private FleetMaster fleetMaster;
    
    @JoinColumn(name = "MRQ_MRQ_ID", referencedColumnName = "MRQ_ID")
    @ManyToOne(fetch=FetchType.LAZY, optional = true)
    private MaintenanceRequest maintenanceRequest;
    
    @Size(max = 2000)
    @Column(name = "NOTE")
    private String note;

	public Long getFnoId() {
		return fnoId;
	}

	public void setFnoId(Long fnoId) {
		this.fnoId = fnoId;
	}

	public FleetMaster getFleetMaster() {
		return fleetMaster;
	}

	public void setFleetMaster(FleetMaster fleetMaster) {
		this.fleetMaster = fleetMaster;
	}

	public MaintenanceRequest getMaintenanceRequest() {
		return maintenanceRequest;
	}

	public void setMaintenanceRequest(MaintenanceRequest maintenanceRequest) {
		this.maintenanceRequest = maintenanceRequest;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
    
}
