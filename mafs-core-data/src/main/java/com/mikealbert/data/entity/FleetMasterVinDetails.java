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
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
* Mapped to FLEET_MASTER_VIN_DETAILS table.
* 
* @author duncan
*/
@Entity
@Table(name = "FLEET_MASTER_VIN_DETAILS")
public class FleetMasterVinDetails extends BaseEntity implements Serializable{

	private static final long serialVersionUID = -7404877940955926921L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="FVD_SEQ")    
    @SequenceGenerator(name="FVD_SEQ", sequenceName="FVD_SEQ", allocationSize=1)      
    @Basic(optional = false)
    @NotNull
    @Column(name = "FVD_ID")
    private Long fvdId;
    
    @OneToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "FMS_FMS_ID", referencedColumnName = "FMS_ID")
    private FleetMaster fleetMaster;

    @Size(max = 30)
    @Column(name = "VIN")
    private String vin;

    @Size(max = 10)
    @Column(name = "YEAR")
    private String year;
    
    @Size(max = 80)
    @Column(name = "MAKE_DESC")
    private String makeDesc;
    
    @Size(max = 240)
    @Column(name = "MODEL_DESC")
    private String modelDesc;
    
    @Size(max = 80)
    @Column(name = "MODEL_TYPE_DESC")
    private String modelTypeDesc;
    
    @Size(max = 80)
    @Column(name = "ENGINE_DESC")
    private String engineDesc;
    
    @Size(max = 80)
    @Column(name = "FUEL_TYPE")
    private String fuelType;
    
    @Size(max = 200)
    @Column(name = "STYLE_DESC")
    private String styleDesc;
    
    @Size(max = 200)
    @Column(name = "TRIM_DESC")
    private String trimDesc;
        
	public Long getFvdId() {
		return fvdId;
	}

	public void setFvdId(Long fvdId) {
		this.fvdId = fvdId;
	}

	public FleetMaster getFleetMaster() {
		return fleetMaster;
	}

	public void setFleetMaster(FleetMaster fleetMaster) {
		this.fleetMaster = fleetMaster;
	}

	public String getVin() {
		return vin;
	}

	public void setVin(String vin) {
		this.vin = vin;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getMakeDesc() {
		return makeDesc;
	}

	public void setMakeDesc(String makeDesc) {
		this.makeDesc = makeDesc;
	}

	public String getModelDesc() {
		return modelDesc;
	}

	public void setModelDesc(String modelDesc) {
		this.modelDesc = modelDesc;
	}

	public String getModelTypeDesc() {
		return modelTypeDesc;
	}

	public void setModelTypeDesc(String modelTypeDesc) {
		this.modelTypeDesc = modelTypeDesc;
	}

	public String getEngineDesc() {
		return engineDesc;
	}

	public void setEngineDesc(String engineDesc) {
		this.engineDesc = engineDesc;
	}

	public String getFuelType() {
		return fuelType;
	}

	public void setFuelType(String fuelType) {
		this.fuelType = fuelType;
	}

	public String getStyleDesc() {
		return styleDesc;
	}

	public void setStyleDesc(String styleDesc) {
		this.styleDesc = styleDesc;
	}

	public String getTrimDesc() {
		return trimDesc;
	}

	public void setTrimDesc(String trimDesc) {
		this.trimDesc = trimDesc;
	}

    
}
