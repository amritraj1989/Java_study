package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * The persistent class for the DEALER_ACCESSORIES database table.
 * @author Singh
 */
@Entity
@Table(name = "DEALER_ACCESSORIES")
public class DealerAccessory extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="DAC_SEQ")    
    @SequenceGenerator(name="DAC_SEQ", sequenceName="DAC_SEQ", allocationSize=1)    
    @Column(name = "DAC_ID")
    private Long dacId;
    
    @OneToMany(mappedBy = "dealerAccessory")
    private List<QuotationDealerAccessory> quotationDealerAccessories;
    
    @OneToMany(mappedBy = "dealerAccessory", cascade=CascadeType.ALL, orphanRemoval=true)
    private List<DealerAccessoryPrice> dealerAccessoryPrices;    
    
    @JoinColumn(name = "ACCESSORY_CODE", referencedColumnName = "ACCESSORY_CODE")
    @ManyToOne(optional = false, cascade=CascadeType.ALL)
    private DealerAccessoryCode dealerAccessoryCode;  
    
    @JoinColumn(name = "MDL_MDL_ID", referencedColumnName = "MDL_ID")
    @ManyToOne
    private Model model;     

    public DealerAccessory() {}

    /**
     * Helper method to retrieve the accessory pricing based on it's id.
     * @param dplId Unique identifier for the dealer accessory pricing entity
     * @return DealerAccessoryPrice found
     */
    public DealerAccessoryPrice findDealerAccessoryPrice(Long dplId){
    	DealerAccessoryPrice dealerAccessoryPrice = null;
    	for(DealerAccessoryPrice price : getDealerAccessoryPrices()){
    		if(price.getDplId().equals(dplId)){
    			dealerAccessoryPrice = price;
    			break;
    		}
    	}
    	return dealerAccessoryPrice;
    }
    
    public Long getDacId() {
        return dacId;
    }

    public void setDacId(Long dacId) {
        this.dacId = dacId;
    }

    public List<QuotationDealerAccessory> getQuotationDealerAccessories() {
		return quotationDealerAccessories;
	}

	public void setQuotationDealerAccessories(
			List<QuotationDealerAccessory> quotationDealerAccessories) {
		this.quotationDealerAccessories = quotationDealerAccessories;
	}

	public List<DealerAccessoryPrice> getDealerAccessoryPrices() {
		return dealerAccessoryPrices;
	}

	public void setDealerAccessoryPrices(List<DealerAccessoryPrice> dealerAccessoryPrices) {
		this.dealerAccessoryPrices = dealerAccessoryPrices;
	}

	public DealerAccessoryCode getDealerAccessoryCode() {
		return dealerAccessoryCode;
	}

	public void setDealerAccessoryCode(DealerAccessoryCode dealerAccessoryCode) {
		this.dealerAccessoryCode = dealerAccessoryCode;
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (dacId != null ? dacId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DealerAccessory)) {
            return false;
        }
        DealerAccessory other = (DealerAccessory) object;
        if ((this.dacId == null && other.dacId != null) || (this.dacId != null && !this.dacId.equals(other.dacId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mikealbert.data.entity.DealerAccessories[ dacId=" + dacId + " ]";
    }
    
}