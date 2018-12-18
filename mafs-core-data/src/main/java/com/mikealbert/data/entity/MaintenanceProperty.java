package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.Size;


/**
 * The persistent class for the VISION.MAINTENANCE_PROPERTIES database table.
 * @author sibley
 */
@Entity
@Table(name="MAINTENANCE_PROPERTIES")
public class MaintenanceProperty extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="MPR_SEQ")    
    @SequenceGenerator(name="MPR_SEQ", sequenceName="MPR_SEQ", allocationSize=1)      
    @Column(name = "MPR_ID", nullable=false)
    private Long mprId;
    
    @Size(min = 1, max = 40)
    @Column(name = "CODE")
    private String code;
    
    @Size(min = 1, max = 40)
    @Column(name = "NAME")
    private String name;
    
    @Size(min = 1, max = 800)
    @Column(name = "DESCRIPTION")
    private String description;    
    
    @Size(min = 1, max = 1)
    @Column(name = "REQUIRED_IND")
    private String requiredIndicator;
    
    @Size(min = 1, max = 20)
    @Column(name = "CONTROL_TYPE")
    private String controlType; 
    
    @OneToMany(mappedBy = "maintenanceProperty")
    private List<MaintenanceCategoryProperty> maintenanceCategoryProperties;    
    

    public MaintenanceProperty() {}


	public Long getMprId() {
		return mprId;
	}


	public void setMprId(Long mprId) {
		this.mprId = mprId;
	}


	public String getCode() {
		return code;
	}


	public void setCode(String code) {
		this.code = code;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public String getRequiredIndicator() {
		return requiredIndicator;
	}


	public void setRequiredIndicator(String requiredIndicator) {
		this.requiredIndicator = requiredIndicator;
	}


	public String getControlType() {
		return controlType;
	}


	public void setControlType(String controlType) {
		this.controlType = controlType;
	}


	public List<MaintenanceCategoryProperty> getMaintenanceCategoryProperties() {
		return maintenanceCategoryProperties;
	}


	public void setMaintenanceCategoryProperties(
			List<MaintenanceCategoryProperty> maintenanceCategoryProperties) {
		this.maintenanceCategoryProperties = maintenanceCategoryProperties;
	}


	@Override
    public String toString() {
        return "com.mikealbert.data.entity.MaintenanceProperty[ mpvId=" + getMprId() + " ]";
    }
  	
}