package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

/**
 * Mapped to UPFIT_ASSESSMENT_ANSWERS table
 */

@Entity
@Table(name = "UPFIT_ASSESSMENT_ANSWERS")
public class UpfitAssessmentAnswer extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="UAA_SEQ")    
    @SequenceGenerator(name="UAA_SEQ", sequenceName="UAA_SEQ", allocationSize=1)
    @Basic(optional = false)
    @NotNull
    @Column(name = "UAA_ID")
    private Long uaaId;

    @Column(name = "ANSWER")
    private String answer;
    
    @ManyToOne
	@JoinColumn(name="UAQ_UAQ_ID", referencedColumnName="UAQ_ID")
	private UpfitAssessmentQuestion upfitAssessmentQuestion;
    
	@ManyToOne
	@JoinColumn(name="VCF_VCF_ID", referencedColumnName="VCF_ID")
	private VehicleConfiguration vehicleConfiguration;
	
	@Column(name = "LAST_UPDATED_BY")
    private String lastUpdatedBy;
	
	@Column(name = "LAST_UPDATED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date lastUpdatedDate;

	public Long getUaaId() {
		return uaaId;
	}

	public void setUaaId(Long uaaId) {
		this.uaaId = uaaId;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public UpfitAssessmentQuestion getUpfitAssessmentQuestion() {
		return upfitAssessmentQuestion;
	}

	public void setUpfitAssessmentQuestion(
			UpfitAssessmentQuestion upfitAssessmentQuestion) {
		this.upfitAssessmentQuestion = upfitAssessmentQuestion;
	}

	public VehicleConfiguration getVehicleConfiguration() {
		return vehicleConfiguration;
	}

	public void setVehicleConfiguration(VehicleConfiguration vehicleConfiguration) {
		this.vehicleConfiguration = vehicleConfiguration;
	}

	public String getLastUpdatedBy() {
		return lastUpdatedBy;
	}

	public void setLastUpdatedBy(String lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}

	public Date getLastUpdatedDate() {
		return lastUpdatedDate;
	}

	public void setLastUpdatedDate(Date lastUpdatedDate) {
		this.lastUpdatedDate = lastUpdatedDate;
	}
	
}
