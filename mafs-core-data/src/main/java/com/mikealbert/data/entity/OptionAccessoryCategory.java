package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;

/**
 * The persistent class for the OPT_ACC_CAT database table.
 * @author Sibley
 */
@Entity
@Table(name = "OPT_ACC_CAT")
public class OptionAccessoryCategory extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 7762900249501513365L;

	@Id    
    @Size(min=1, max=10)
    @Column(name = "OPT_ACC_CAT_CODE")
    private String code;
    
    @Size(min=1, max=10)
    @Column(name = "OPT_ACC_CAT_DESC")
    private String description;
    
    @Size(min=1, max=200)
    @Column(name = "INTERNAL_DESCRIPTION")
    private String mafsDescription;
    
    @OneToMany(mappedBy = "optionAccessoryCategory")
    private List<DealerAccessoryCode> dealerAccessoryCodes;    
    
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<DealerAccessoryCode> getDealerAccessoryCodes() {
		return dealerAccessoryCodes;
	}

	public void setDealerAccessoryCodes(List<DealerAccessoryCode> dealerAccessoryCodes) {
		this.dealerAccessoryCodes = dealerAccessoryCodes;
	}

	
	@Override
    public String toString() {
        return "com.mikealbert.data.entity.OptionAccessoryCategory[ code=" + getCode() + " ]";
    }

	/*
	 * This method is needed as we are using jsf converter to map  OptionAccessoryCategory list
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		return result;
	}

	/*
	 * This method is needed as we are using jsf converter to map  OptionAccessoryCategory list
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OptionAccessoryCategory other = (OptionAccessoryCategory) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		return true;
	}

	public String getMafsDescription() {
		return mafsDescription;
	}

	public void setMafsDescription(String mafsDescription) {
		this.mafsDescription = mafsDescription;
	}
}