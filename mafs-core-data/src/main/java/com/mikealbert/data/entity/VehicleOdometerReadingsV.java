package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;
import javax.validation.constraints.Size;

/**
* Mapped to VEHICLE_ODOMETER_READINGS_V view.
*/

@Entity
@Table(name="VEHICLE_ODOMETER_READINGS_V")
public class VehicleOdometerReadingsV implements Serializable  {
	private static final long serialVersionUID = 1L;
	
	@EmbeddedId
	private VehicleOdometerReadingsVPK id;
   
    @Column(name = "ODO_ID")
    private Long odoId;
    
    @Column(name = "ODO_READING")
    private Long odoReading;
    
    @Column(name = "ODO_READING_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date odoReadingDate;
    
    @Size(max = 10)
    @Column(name = "ODO_READING_TYPE")
    private String odoReadingType;
    
    @Size(max = 10)
    @Column(name = "ODO_SRC")
    private String odoSrc;
 
	@Column(name = "ODO_PRIOR")
    private Long odoPrior;
    
    @Column(name = "CREATE_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;   
    
   @JoinColumn(name = "FMS_ID", referencedColumnName = "FMS_ID", insertable=false, updatable=false)
   @ManyToOne(optional = true)
   private FleetMaster fleetMaster;     
    
    public VehicleOdometerReadingsVPK getId() {
		return id;
	}

	public void setId(VehicleOdometerReadingsVPK id) {
		this.id = id;
	}

	public FleetMaster getFleetMaster() {
		return fleetMaster;
	}

	public void setFleetMaster(FleetMaster fleetMaster) {
		this.fleetMaster = fleetMaster;
	}

	public Long getOdoId() {
		return odoId;
	}

	public void setOdoId(Long odoId) {
		this.odoId = odoId;
	}

	public Long getOdoReading() {
		return odoReading;
	}

	public void setOdoReading(Long odoReading) {
		this.odoReading = odoReading;
	}

	public Date getOdoReadingDate() {
		return odoReadingDate;
	}

	public void setOdoReadingDate(Date odoReadingDate) {
		this.odoReadingDate = odoReadingDate;
	}

	public String getOdoReadingType() {
		return odoReadingType;
	}

	public void setOdoReadingType(String odoReadingType) {
		this.odoReadingType = odoReadingType;
	}

	public String getOdoSrc() {
		return odoSrc;
	}

	public void setOdoSrc(String odoSrc) {
		this.odoSrc = odoSrc;
	}

	public Long getOdoPrior() {
		return odoPrior;
	}

	public void setOdoPrior(Long odoPrior) {
		this.odoPrior = odoPrior;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
}
