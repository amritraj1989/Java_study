package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.List;
import javax.persistence.*;


/**
 * The persistent class for the VISION.MAINTENANCE_CAT_PROPERTIES database table.
 * @author sibley
 */
@Entity
@Table(name="MAINTENANCE_CAT_PROPERTIES")
public class MaintenanceCategoryProperty extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="MCP_SEQ")    
    @SequenceGenerator(name="MCP_SEQ", sequenceName="MCP_SEQ", allocationSize=1)      
    @Column(name = "MCP_ID", nullable=false)
    private Long mcpId;
    
    @Column(name = "SORT_ORDER", nullable=false)    
    private Long sortOrder;        
        
    @OneToMany(mappedBy = "maintenanceCategoryProperty")
    private List<MaintenanceCategoryPropertyValue> maintenanceCategoryPropertyValues;
    
    @JoinColumn(name = "MPR_MPR_ID", referencedColumnName = "MPR_ID")
    @ManyToOne(optional = false)
    private MaintenanceProperty maintenanceProperty;  
    
    @JoinColumn(name = "MAINT_CAT_CODE", referencedColumnName = "MAINT_CAT_CODE")
    @ManyToOne(optional = false)
    private MaintenanceCategory maintenanceCategory;      

    public MaintenanceCategoryProperty() {}


	public Long getMcpId() {
		return mcpId;
	}


	public void setMcpId(Long mcpId) {
		this.mcpId = mcpId;
	}



	public Long getSortOrder() {
		return sortOrder;
	}


	public void setSortOrder(Long sortOrder) {
		this.sortOrder = sortOrder;
	}


	public List<MaintenanceCategoryPropertyValue> getMaintenanceCategoryPropertyValues() {
		return maintenanceCategoryPropertyValues;
	}


	public void setMaintenanceCategoryPropertyValues(
			List<MaintenanceCategoryPropertyValue> maintenanceCategoryPropertyValues) {
		this.maintenanceCategoryPropertyValues = maintenanceCategoryPropertyValues;
	}


	public MaintenanceProperty getMaintenanceProperty() {
		return maintenanceProperty;
	}


	public void setMaintenanceProperty(MaintenanceProperty maintenanceProperty) {
		this.maintenanceProperty = maintenanceProperty;
	}


	public MaintenanceCategory getMaintenanceCategory() {
		return maintenanceCategory;
	}


	public void setMaintenanceCategory(MaintenanceCategory maintenanceCategory) {
		this.maintenanceCategory = maintenanceCategory;
	}


	@Override
    public String toString() {
        return "com.mikealbert.data.entity.MaintenanceCategoryProperty[ mpvId=" + getMcpId() + " ]";
    }
  	
}