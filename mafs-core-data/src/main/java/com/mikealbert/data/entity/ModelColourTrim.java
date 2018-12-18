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
 * The persistent class for the MODEL_COLOUR_TRIMS database table.
 * @author sibley
 */
@Entity
@Table(name="MODEL_COLOUR_TRIMS")
public class ModelColourTrim implements Serializable {
	private static final long serialVersionUID = 7305566253800020272L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="MCT_SEQ")    
    @SequenceGenerator(name="MCT_SEQ", sequenceName="MCT_SEQ", allocationSize=1)	
    @NotNull
    @Column(name = "MCT_ID")
    private Long mctId;
	        
    @JoinColumn(name = "MDL_MDL_ID", referencedColumnName = "MDL_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Model model;  
    
    @JoinColumn(name = "TRC_TRC_ID", referencedColumnName = "TRC_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private TrimCodes trimCode;       

    public ModelColourTrim() {}

	public Long getMctId() {
		return mctId;
	}

	public void setMctId(Long mctId) {
		this.mctId = mctId;
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public TrimCodes getTrimCode() {
		return trimCode;
	}

	public void setTrimCode(TrimCodes trimCode) {
		this.trimCode = trimCode;
	}
	
}