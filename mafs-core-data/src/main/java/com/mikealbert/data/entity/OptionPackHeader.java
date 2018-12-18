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
import javax.validation.constraints.NotNull;

/**
 * The persistent class for the OPTION_PACK_HEADER database table.
 * @author sibley
 */
@Entity
@Table(name="OPTION_PACK_HEADER")
public class OptionPackHeader extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 6254874864214801901L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="OPH_SEQ")    
    @SequenceGenerator(name="OPH_SEQ", sequenceName="OPH_SEQ", allocationSize=1)	
    @NotNull
    @Column(name = "OPH_ID")
    private Long ophId;
    	
    @JoinColumn(name = "MDL_MDL_ID", referencedColumnName = "MDL_ID")
    @ManyToOne
    private Model model; 
    
    @OneToMany(mappedBy="optionPackHeader")
    private List<QuoteModelOptionPack> quoteModelOptionPacks;
    
    @JoinColumn(name = "OPTC_OPTC_ID", referencedColumnName = "OPTC_ID")
    @ManyToOne
    private OptionPackTypeCode optionPackTypeCode; 
    
    @OneToMany(mappedBy = "optionPackHeader", cascade = CascadeType.ALL)    
    private List<OptionPackCost> optionPackCosts;  
    
    @OneToMany(mappedBy = "optionPackHeader")    
    private List<OptionalAccessory> optionalAccessories;    
    
    @OneToMany(mappedBy = "optionPackHeader")    
    private List<OptionPackDetail> optionPackDetail;      
    
    public OptionPackHeader() {}

	public Long getOphId() {
		return ophId;
	}

	public void setOphId(Long ophId) {
		this.ophId = ophId;
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public List<QuoteModelOptionPack> getQuoteModelOptionPacks() {
		return this.quoteModelOptionPacks;
	}

	public void setQuoteModelOptionPacks(List<QuoteModelOptionPack> quoteModelOptionPacks) {
		this.quoteModelOptionPacks = quoteModelOptionPacks;
	}
	
	public OptionPackTypeCode getOptionPackTypeCode() {
		return optionPackTypeCode;
	}

	public void setOptionPackTypeCode(OptionPackTypeCode optionPackTypeCode) {
		this.optionPackTypeCode = optionPackTypeCode;
	}

	public List<OptionPackCost> getOptionPackCosts() {
		return optionPackCosts;
	}

	public void setOptionPackCosts(List<OptionPackCost> optionPackCosts) {
		this.optionPackCosts = optionPackCosts;
	}

	public List<OptionalAccessory> getOptionalAccessories() {
		return optionalAccessories;
	}

	public void setOptionalAccessories(List<OptionalAccessory> optionalAccessories) {
		this.optionalAccessories = optionalAccessories;
	}

	public List<OptionPackDetail> getOptionPackDetail() {
		return optionPackDetail;
	}

	public void setOptionPackDetail(List<OptionPackDetail> optionPackDetail) {
		this.optionPackDetail = optionPackDetail;
	}
	
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof OptionPackHeader)) {
            return false;
        }
        OptionPackHeader other = (OptionPackHeader) object;
        if ((this.ophId == null && other.ophId != null) || (this.ophId != null && !this.ophId.equals(other.ophId))) {
            return false;
        }
        return true;
    }	

	@Override
    public String toString() {
        return "com.mikealbert.data.entity.OptionPackHeader[ ophId=" + this.getOphId() + " ]";
    }
	
}