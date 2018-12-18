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

/**
 * Mapped to VEHICLE_TECH_DATA table
 * @author Raj
 */
@Entity
@Table(name = "VEHICLE_TECH_DATA")
public class VehicleTechnicalInfo extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="VTD_SEQ")    
    @SequenceGenerator(name="VTD_SEQ", sequenceName="VTD_SEQ", allocationSize=1)  
    @Column(name = "VTD_ID")
    private Long vehicleTehnicalDataId;
    
    @Column(name = "POWERTRAIN_WARRANTY_MILEAGE")
    private Long vehicleWarrantyMileage;
    
    @Column(name = "POWERTRAIN_WARRANTY_YEAR")
    private Long vehicleWarrantyMonths;
    
    @JoinColumn(name = "MDL_MDL_ID", referencedColumnName = "MDL_ID", insertable=false, updatable=false)
    @OneToOne(fetch = FetchType.EAGER)
    private Model model;

	public Long getVehicleTehnicalDataId() {
		return vehicleTehnicalDataId;
	}

	public void setVehicleTehnicalDataId(Long vehicleTehnicalDataId) {
		this.vehicleTehnicalDataId = vehicleTehnicalDataId;
	}

	public Long getVehicleWarrantyMileage() {
		return vehicleWarrantyMileage;
	}

	public void setVehicleWarrantyMileage(Long vehicleWarrantyMileage) {
		this.vehicleWarrantyMileage = vehicleWarrantyMileage;
	}

	public Long getVehicleWarrantyMonths() {
		return vehicleWarrantyMonths;
	}

	public void setVehicleWarrantyMonths(Long vehicleWarrantyMonths) {
		this.vehicleWarrantyMonths = vehicleWarrantyMonths;
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}
}
