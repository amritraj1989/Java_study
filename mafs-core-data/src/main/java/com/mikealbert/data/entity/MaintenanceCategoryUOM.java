package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.Size;


/**
 * The persistent class for the VISION.MAINTENANCE_UOM_CODES database table.
 * @author sibley
 */
@Entity
@Table(name="MAINTENANCE_UOM_CODES")
public class MaintenanceCategoryUOM extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="MUC_SEQ")    
    @SequenceGenerator(name="MUC_SEQ", sequenceName="MUC_SEQ", allocationSize=1)      
    @Column(name = "MUC_ID", nullable=false)
    private Long mucId;
          
    @JoinColumn(name = "MAINT_CAT_CODE", referencedColumnName = "MAINT_CAT_CODE")
    @ManyToOne(optional = false)
    private MaintenanceCategory maintenanceCategory;  
    
    @JoinColumn(name = "UOM_CODE", referencedColumnName = "UOM_CODE")
    @ManyToOne(optional = false)
    private UomCode uomCode;  
    
    public MaintenanceCategoryUOM() {}


	public Long getMucId() {
		return mucId;
	}


	public void setMucId(Long mucId) {
		this.mucId = mucId;
	}


	public MaintenanceCategory getMaintenanceCategory() {
		return maintenanceCategory;
	}


	public void setMaintenanceCategory(MaintenanceCategory maintenanceCategory) {
		this.maintenanceCategory = maintenanceCategory;
	}


	public UomCode getUomCode() {
		return uomCode;
	}


	public void setUomCode(UomCode uomCode) {
		this.uomCode = uomCode;
	}


	@Override
    public String toString() {
        return "com.mikealbert.data.entity.MaintenanceUOMCode[ mpvId=" + getMucId() + " ]";
    }
  	
}