package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * The persistent class for the MODEL_COLOURS database table.
 * @author sibley
 */
@Entity
@Table(name="MODEL_COLOURS")
public class ModelColour implements Serializable {
	private static final long serialVersionUID = -7267011421752791923L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="MDC_SEQ")    
    @SequenceGenerator(name="MDC_SEQ", sequenceName="MDC_SEQ", allocationSize=1)	
    @NotNull
    @Column(name = "MDC_ID")
    private Long mdcId;
	        
    @JoinColumn(name = "MDL_MDL_ID", referencedColumnName = "MDL_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Model model;  
    
    @JoinColumn(name = "COLOUR_CODE", referencedColumnName = "COLOUR_CODE")
    @ManyToOne(fetch = FetchType.LAZY)
    private ColourCodes colourCode;       

    public ModelColour() {}

	public Long getMdcId() {
		return mdcId;
	}

	public void setMdcId(Long mdcId) {
		this.mdcId = mdcId;
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public ColourCodes getColourCode() {
		return colourCode;
	}

	public void setColourCode(ColourCodes colourCode) {
		this.colourCode = colourCode;
	}
	
}