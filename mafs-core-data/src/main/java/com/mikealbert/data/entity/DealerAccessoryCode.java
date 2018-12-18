package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.mikealbert.data.beanvalidation.MANotNull;
import com.mikealbert.data.beanvalidation.MASize;

import java.util.List;


/**
 * The persistent class for the DEALER_ACCESSORY_CODES database table.
 * NOTE: Not all columns are mapped.
 * @author Singh
 */
@Entity
@Table(name = "DEALER_ACCESSORY_CODES")
public class DealerAccessoryCode extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @MASize(min =1, max = 80, label = "Code")
    @Column(name = "ACCESSORY_CODE")
    private String accessoryCode;
    
    @MANotNull(label = "Description")
    @Column(name = "DESCRIPTION")
    private String description;    
    
    @MASize(min =1, max = 80, label = "Code")
    @Column(name = "NEW_ACCESSORY_CODE")
    private String newAccessoryCode;
    
    @Column(name="LEAD_TIME")
    private Long leadTime;
    
    @NotNull
    @Size(max=1)
    @Column(name="COMMON_IND")
    private String commonInd;
    
   	@OneToMany(mappedBy = "dealerAccessoryCode")
    private List<DealerAccessory> dealerAccessories;
    
    @JoinColumn(name = "CATEGORY_CODE", referencedColumnName = "OPT_ACC_CAT_CODE")
    @ManyToOne(optional = true)
    private OptionAccessoryCategory optionAccessoryCategory;       

    public DealerAccessoryCode() {}

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

    public String getNewAccessoryCode() {
        return newAccessoryCode;
    }

    public void setNewAccessoryCode(String newAccessoryCode) {
        this.newAccessoryCode = newAccessoryCode;
    }

	public Long getLeadTime() {
		return leadTime;
	}

	public void setLeadTime(Long leadTime) {
		this.leadTime = leadTime;
	}

	public String getCommonInd() {
		return commonInd;
	}

	public void setCommonInd(String commonInd) {
		this.commonInd = commonInd;
	}

	public List<DealerAccessory> getDealerAccessories() {
		return dealerAccessories;
	}

	public void setDealerAccessories(List<DealerAccessory> dealerAccessories) {
		this.dealerAccessories = dealerAccessories;
	}

    public OptionAccessoryCategory getOptionAccessoryCategory() {
		return optionAccessoryCategory;
	}

	public void setOptionAccessoryCategory(OptionAccessoryCategory optionAccessoryCategory) {
		this.optionAccessoryCategory = optionAccessoryCategory;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (accessoryCode != null ? accessoryCode.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DealerAccessoryCode)) {
            return false;
        }
        DealerAccessoryCode other = (DealerAccessoryCode) object;
        if ((this.accessoryCode == null && other.accessoryCode != null) || (this.accessoryCode != null && !this.accessoryCode.equals(other.accessoryCode))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mikealbert.data.entity.DealerAccessoryCodes[ accessoryCode=" + accessoryCode + " ]";
    }
	
}