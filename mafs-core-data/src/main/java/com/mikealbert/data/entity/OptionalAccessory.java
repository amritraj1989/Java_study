package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * The persistent class for the OPTIONAL_ACCESSORIES database table.
 * @author Singh
 */
@Entity
@Table(name="OPTIONAL_ACCESSORIES")
public class OptionalAccessory extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @NotNull
    @Column(name = "OAC_ID")
    private Long oacId;
    
    @Size(max = 1)
    @Column(name = "IGNOR_FLAG")
    private String ignorFlag;    
    
//    @JoinColumn(name = "ASSC_ASSC_ID", referencedColumnName = "ASSC_ID")
//    @ManyToOne(optional = false)
//    private AccessoryCode accessoryCode;
    
    @OneToMany(mappedBy = "optionalAccessory")
    private List<QuotationModelAccessory> quotationModelAccessories;
    
    @OneToMany(mappedBy = "optionalAccessory", cascade=CascadeType.ALL)
    private List<OptionPrice> optionPrices;    
    
//    @OneToMany(mappedBy = "optionalAccessory")
//    private List<OptionPackDetail> optionPackDetails;  
    
    @JoinColumn(name = "MDL_MDL_ID", referencedColumnName = "MDL_ID")
    @ManyToOne
    private Model model; 
    
    @JoinColumn(name = "ASSC_ASSC_ID", referencedColumnName = "ASSC_ID")
    @ManyToOne
    private AccessoryCode accessoryCode;   
    
    @JoinColumn(name = "OPH_OPH_ID", referencedColumnName = "OPH_ID")
    @ManyToOne
    private OptionPackHeader optionPackHeader;     

    public OptionalAccessory() {}

    public Long getOacId() {
        return oacId;
    }

    public void setOacId(Long oacId) {
        this.oacId = oacId;
    }

    public String getIgnorFlag() {
        return ignorFlag;
    }

    public void setIgnorFlag(String ignorFlag) {
        this.ignorFlag = ignorFlag;
    }

	public List<QuotationModelAccessory> getQuotationModelAccessories() {
		return quotationModelAccessories;
	}

	public void setQuotationModelAccessories(
			List<QuotationModelAccessory> quotationModelAccessories) {
		this.quotationModelAccessories = quotationModelAccessories;
	}
	
	public List<OptionPrice> getOptionPrices() {
		return optionPrices;
	}

	public void setOptionPrices(List<OptionPrice> optionPrices) {
		this.optionPrices = optionPrices;
	}

//	public List<OptionPackDetail> getOptionPackDetails() {
//		return optionPackDetails;
//	}
//
//	public void setOptionPackDetails(List<OptionPackDetail> optionPackDetails) {
//		this.optionPackDetails = optionPackDetails;
//	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public AccessoryCode getAccessoryCode() {
		return accessoryCode;
	}

	public void setAccessoryCode(AccessoryCode accessoryCode) {
		this.accessoryCode = accessoryCode;
	}

	public OptionPackHeader getOptionPackHeader() {
		return optionPackHeader;
	}

	public void setOptionPackHeader(OptionPackHeader optionPackHeader) {
		this.optionPackHeader = optionPackHeader;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (oacId != null ? oacId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof OptionalAccessory)) {
            return false;
        }
        OptionalAccessory other = (OptionalAccessory) object;
        if ((this.oacId == null && other.oacId != null) || (this.oacId != null && !this.oacId.equals(other.oacId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mikealbert.data.entity.OptionalAccessories[ oacId=" + oacId + " ]";
    }
	
}