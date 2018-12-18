package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;


/**
 * The persistent class for the STANDARD_ACCESSORY_CODES database table.
 * @author sibley
 */
@Entity
@Table(name="STANDARD_ACCESSORY_CODES")
public class StandardAccessoryCode extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 6963005455925115426L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SACC_SEQ")    
    @SequenceGenerator(name="SACC_SEQ", sequenceName="SACC_SEQ", allocationSize=1)	
    @NotNull
    @Column(name = "SACC_ID")
    private Long saccId;
    
    @Size(min = 1, max = 10)
    @Column(name = "ACCESSORY_CODE")
    private String accessoryCode;
    
    @Size(min = 1, max = 80)
    @Column(name = "DESCRIPTION")
    private String description;
    
    @Size(min = 1, max = 1)
    @Column(name = "COMB_ACC_IND")
    private String combAccInd;
    
    @Size(min = 1, max = 10)
    @Column(name = "NEW_ACCESSORY_CODE")
    private String newAccessoryCode;
    
    @OneToMany(mappedBy = "standardAccessoryCode")
    private List<StandardAccessory> standardAccessories;        
        
    @JoinColumn(name = "MTP_MTP_ID", referencedColumnName = "MTP_ID")
    @ManyToOne
    private ModelType modelType;  
    
    public StandardAccessoryCode() {}

    public Long getSaccId() {
		return saccId;
	}

	public void setSaccId(Long saccId) {
		this.saccId = saccId;
	}

	public String getAccessoryCode() {
        return accessoryCode;
    }

    public void setAccessoryCode(String accessoryCode) {
        this.accessoryCode = accessoryCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCombAccInd() {
        return combAccInd;
    }

    public void setCombAccInd(String combAccInd) {
        this.combAccInd = combAccInd;
    }

    public String getNewAccessoryCode() {
        return newAccessoryCode;
    }

    public void setNewAccessoryCode(String newAccessoryCode) {
        this.newAccessoryCode = newAccessoryCode;
    }

	public List<StandardAccessory> getStandardAccessories() {
		return standardAccessories;
	}

	public void setStandardAccessories(List<StandardAccessory> standardAccessories) {
		this.standardAccessories = standardAccessories;
	}

	public ModelType getModelType() {
		return modelType;
	}

	public void setModelType(ModelType modelType) {
		this.modelType = modelType;
	}

    @Override
    public String toString() {
        return "com.mikealbert.data.entity.StandardAccessoryCode[ saccId=" + this.getSaccId() + " ]";
    }
	
}