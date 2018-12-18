package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;

/**
 * Mapped to MODEL_MARK_YEARS table
 * @author sibley
 */
@Entity
@Table(name="MODEL_MARK_YEARS")
@NamedQuery(name = "ModelMarkYear.findAll", query = "SELECT m FROM ModelMarkYear m")
public class ModelMarkYear implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="MMY_SEQ")    
    @SequenceGenerator(name="MMY_SEQ", sequenceName="MMY_SEQ", allocationSize=1)	
	@Column(name="MMY_ID")
	private Long mmyId;
	
	@Column(name = "GENERIC_FLAG")
	private String genericFlag;
		
	@Column(name = "MODEL_MARK_YEAR_CODE")
	private String modelMarkYearCode;
	
	@Column(name = "MODEL_MARK_YEAR_DESC")
	private String modelMarkYearDesc;
	
	@ManyToOne
	@JoinColumn(name="MTP_MTP_ID")
	private ModelType modelType;
	
	@OneToMany(mappedBy="modelMarkYear")
	private List<Model> models;	
		
	public ModelMarkYear(){}
	

	public Long getMmyId() {
		return mmyId;
	}

	public void setMmyId(Long mmyId) {
		this.mmyId = mmyId;
	}

	public String getGenericFlag() {
		return this.genericFlag;
	}

	public void setGenericFlag(String genericFlag) {
		this.genericFlag = genericFlag;
	}

	public String getModelMarkYearCode() {
		return this.modelMarkYearCode;
	}

	public void setModelMarkYearCode(String modelMarkYearCode) {
		this.modelMarkYearCode = modelMarkYearCode;
	}

	public String getModelMarkYearDesc() {
		return this.modelMarkYearDesc;
	}

	public void setModelMarkYearDesc(String modelMarkYearDesc) {
		this.modelMarkYearDesc = modelMarkYearDesc;
	}

	public ModelType getModelType() {
		return modelType;
	}

	public void setModelType(ModelType modelType) {
		this.modelType = modelType;
	}

	public List<Model> getModels() {
		return models;
	}

	public void setModels(List<Model> models) {
		this.models = models;
	}
	
}