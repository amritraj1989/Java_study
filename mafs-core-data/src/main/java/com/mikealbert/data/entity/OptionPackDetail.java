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
import javax.validation.constraints.NotNull;

/**
 * The persistent class for the OPTIONAL_ACCESSORIES database table.
 * @author sibley
 */
@Entity
@Table(name="OPTION_PACK_DETAIL")
public class OptionPackDetail extends BaseEntity implements Serializable {
	private static final long serialVersionUID = -560709511327132701L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="OPD_SEQ")    
    @SequenceGenerator(name="OPD_SEQ", sequenceName="OPD_SEQ", allocationSize=1)	
    @NotNull
    @Column(name = "OPD_ID")
    private Long opdId;
         
    @JoinColumn(name = "OAC_OAC_ID", referencedColumnName = "OAC_ID")
    @ManyToOne(optional=true)
    private OptionalAccessory optionalAccessory;   
    
    @JoinColumn(name = "OPH_OPH_ID", referencedColumnName = "OPH_ID")
    @ManyToOne
    private OptionPackHeader optionPackHeader; 
    
    @JoinColumn(name = "CHILD_OPH_ID", referencedColumnName = "OPH_ID")
    @ManyToOne
    private OptionPackHeader childOptionPackHeader;       

    public OptionPackDetail() {}

    public Long getOpdId() {
		return opdId;
	}

	public void setOpdId(Long opdId) {
		this.opdId = opdId;
	}

	public OptionalAccessory getOptionalAccessory() {
		return optionalAccessory;
	}

	public void setOptionalAccessory(OptionalAccessory optionalAccessory) {
		this.optionalAccessory = optionalAccessory;
	}

	public OptionPackHeader getOptionPackHeader() {
		return optionPackHeader;
	}

	public void setOptionPackHeader(OptionPackHeader optionPackHeader) {
		this.optionPackHeader = optionPackHeader;
	}

	public OptionPackHeader getChildOptionPackHeader() {
		return childOptionPackHeader;
	}

	public void setChildOptionPackHeader(OptionPackHeader childOptionPackHeader) {
		this.childOptionPackHeader = childOptionPackHeader;
	}

	@Override
    public String toString() {
        return "com.mikealbert.data.entity.OptionPackDetail[ opdId=" + this.getOpdId() + " ]";
    }
	
}