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
@Table(name = "VEHICLE_MOVEMENT_NOTES")
public class VehicleMovementNote  extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 6845602146974819397L;

	@Id
	@Column(name="VMN_ID")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "VMN_SEQ")
    @SequenceGenerator(name = "VMN_SEQ", sequenceName = "VMN_SEQ", allocationSize = 1)
	private Long vmnId;

	@Column(name="NOTE_TYPE")
	private String noteType;
	
	@Column(name="NOTES")
	private String notes;
	
	@Column(name="ENTERED_BY")
	private String enteredBy;
	
	@Column(name="DATE_ENTERED")
	@Temporal( TemporalType.DATE)
	private Date dateEntered;
	
	@Column(name = "NON_RUNNING_NOTE_IND")
    private String nonRunningNnoteInd;
	
	@JoinColumn(name = "VMOV_VMOV_ID", referencedColumnName = "VMOV_ID")
    @OneToOne(fetch = FetchType.EAGER)
	private VehicleMovement vehicleMovement;
	
	public VehicleMovementNote() {
	}

	public Long getVmnId() {
		return vmnId;
	}

	public void setVmnId(Long vmnId) {
		this.vmnId = vmnId;
	}

	public String getNoteType() {
		return noteType;
	}

	public void setNoteType(String noteType) {
		this.noteType = noteType;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getEnteredBy() {
		return enteredBy;
	}

	public void setEnteredBy(String enteredBy) {
		this.enteredBy = enteredBy;
	}

	public Date getDateentered() {
		return dateEntered;
	}

	public void setDateEntered(Date dateEntered) {
		this.dateEntered = dateEntered;
	}

	public String getNonRunningNnoteInd() {
		return nonRunningNnoteInd;
	}

	public void setNonRunningNnoteInd(String nonRunningNnoteInd) {
		this.nonRunningNnoteInd = nonRunningNnoteInd;
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
        hash += (vmnId != null ? vmnId.hashCode() : 0);
        return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof VehicleMovementNote)) {
            return false;
        }
        VehicleMovementNote other = (VehicleMovementNote) object;
        if ((this.vmnId == null && other.vmnId != null) || (this.vmnId != null && !this.vmnId.equals(other.vmnId))) {
            return false;
        }
        return true;
	}

}