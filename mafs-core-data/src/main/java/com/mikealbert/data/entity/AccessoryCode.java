package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;


/**
 * The persistent class for the ACCESSORY_CODES database table.
 * @author Singh
 */
@Entity
@Table(name="ACCESSORY_CODES")
public class AccessoryCode extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @NotNull
    @Column(name = "ASSC_ID")
    private Long asscId;
    
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
    
    @OneToMany(mappedBy = "accessoryCode")
    private List<OptionalAccessory> optionalAccessories;
    
    @JoinColumn(name = "MTP_MTP_ID", referencedColumnName = "MTP_ID")
    @ManyToOne
    private ModelType modelType;     

    public AccessoryCode() {
    }

    public AccessoryCode(Long asscId) {
        this.asscId = asscId;
    }

    public AccessoryCode(Long asscId, String accessoryCode, String description, String combAccInd, String newAccessoryCode) {
        this.asscId = asscId;
        this.accessoryCode = accessoryCode;
        this.description = description;
        this.combAccInd = combAccInd;
        this.newAccessoryCode = newAccessoryCode;
    }

    public Long getAsscId() {
        return asscId;
    }

    public void setAsscId(Long asscId) {
        this.asscId = asscId;
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

    public List<OptionalAccessory> getOptionalAccessories() {
		return optionalAccessories;
	}

	public void setOptionalAccessories(List<OptionalAccessory> optionalAccessories) {
		this.optionalAccessories = optionalAccessories;
	}

	public ModelType getModelType() {
		return modelType;
	}

	public void setModelType(ModelType modelType) {
		this.modelType = modelType;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (asscId != null ? asscId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AccessoryCode)) {
            return false;
        }
        AccessoryCode other = (AccessoryCode) object;
        if ((this.asscId == null && other.asscId != null) || (this.asscId != null && !this.asscId.equals(other.asscId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mikealbert.data.entity.AccessoryCodes[ asscId=" + asscId + " ]";
    }
	
}