package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import com.mikealbert.util.MALUtilities;


/**
 * The persistent class for the VISION.MAINTENANCE_CAT_PROP_VALUES database table.
 * @author sibley
 */
@Entity
@Table(name="MAINTENANCE_CAT_PROP_VALUES")
public class MaintenanceCategoryPropertyValue extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="MPV_SEQ")    
    @SequenceGenerator(name="MPV_SEQ", sequenceName="MPV_SEQ", allocationSize=1)      
    @Column(name = "MPV_ID", nullable=false)
    private Long mpvId;
    
    @Size(max = 100)
    @Column(name = "VALUE")
    private String value;
    
    @JoinColumn(name = "MRT_MRT_ID", referencedColumnName = "MRT_ID")
    @ManyToOne(optional = true)
    private MaintenanceRequestTask maintenanceRequestTask;
    
    @JoinColumn(name = "MCP_MCP_ID", referencedColumnName = "MCP_ID")
    @ManyToOne(optional = false)
    private MaintenanceCategoryProperty maintenanceCategoryProperty;     
    
    public MaintenanceCategoryPropertyValue() {}

	public Long getMpvId() {
		return mpvId;
	}

	public void setMpvId(Long mpvId) {
		this.mpvId = mpvId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public MaintenanceRequestTask getMaintenanceRequestTask() {
		return maintenanceRequestTask;
	}

	public void setMaintenanceRequestTask(MaintenanceRequestTask maintenanceRequestTask) {
		this.maintenanceRequestTask = maintenanceRequestTask;
	}

	public MaintenanceCategoryProperty getMaintenanceCategoryProperty() {
		return maintenanceCategoryProperty;
	}

	public void setMaintenanceCategoryProperty(
			MaintenanceCategoryProperty maintenanceCategoryProperty) {
		this.maintenanceCategoryProperty = maintenanceCategoryProperty;
	}

	@Override
	public boolean equals(Object object){
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MaintenanceCategoryPropertyValue)) {
            return false;
        }
        
        MaintenanceCategoryPropertyValue other = (MaintenanceCategoryPropertyValue) object;
        
        if ((this.getMpvId() == null && other.getMpvId() != null) || (this.getMpvId() != null && other.getMpvId() == null) || (this.getMpvId() != null && other.getMpvId() != null && !this.getMpvId().equals(other.getMpvId()))) {
            return false;
        } 
        
        if ((MALUtilities.isEmpty(this.getValue()) && !MALUtilities.isEmpty(other.getValue())) || (!MALUtilities.isEmpty(this.getValue()) && MALUtilities.isEmpty(other.getValue())) || (!MALUtilities.isEmpty(this.getValue()) && !MALUtilities.isEmpty(other.getValue()) && !this.getValue().equals(other.getValue()))) {
            return false;
        }         
        
        return true;
		
	}
	
	@Override
    public int hashCode() {
        int hash = 0;
        hash += (getMpvId() != null ? getMpvId().hashCode() : 0);
        hash += (!MALUtilities.isEmpty(getValue()) ? getValue().hashCode() : 0);        
        return hash;
    }	
	
	@Override
    public String toString() {
        return "com.mikealbert.data.entity.MaintenanceCategoryPropertyValue[ mpvId=" + getMpvId() + " ]";
    }
  	
}