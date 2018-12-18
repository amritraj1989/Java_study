package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.List;
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
 * The persistent class for the STANDARD_ACCESSORIES database table.
 * @author sibley
 */
@Entity
@Table(name="STANDARD_ACCESSORIES")
public class StandardAccessory extends BaseEntity implements Serializable {
	private static final long serialVersionUID = -5172359638139737461L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SAC_SEQ")    
    @SequenceGenerator(name="SAC_SEQ", sequenceName="SAC_SEQ", allocationSize=1)	
    @NotNull
    @Column(name = "SAC_ID")
    private Long sacId;
        
    @JoinColumn(name = "MDL_MDL_ID", referencedColumnName = "MDL_ID")
    @ManyToOne
    private Model model;  
    
    @OneToMany(mappedBy = "standardAccessory")
    private List<QuotationStandardAccessory> quotationStandardAccessories;
    
    @JoinColumn(name = "SACC_SACC_ID", referencedColumnName = "SACC_ID")
    @ManyToOne
    private StandardAccessoryCode standardAccessoryCode;       

    public StandardAccessory() {}

	public Long getSacId() {
		return sacId;
	}

	public void setSacId(Long sacId) {
		this.sacId = sacId;
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

    public List<QuotationStandardAccessory> getQuotationStandardAccessories() {
		return quotationStandardAccessories;
	}

	public void setQuotationStyandardAccessories(List<QuotationStandardAccessory> quotationStandardAccessories) {
		this.quotationStandardAccessories = quotationStandardAccessories;
	}

	public StandardAccessoryCode getStandardAccessoryCode() {
		return standardAccessoryCode;
	}

	public void setStandardAccessoryCode(StandardAccessoryCode standardAccessoryCode) {
		this.standardAccessoryCode = standardAccessoryCode;
	}

	@Override
    public String toString() {
        return "com.mikealbert.data.entity.StandardAccessory[ sacId=" + this.getSacId() + " ]";
    }
	
}