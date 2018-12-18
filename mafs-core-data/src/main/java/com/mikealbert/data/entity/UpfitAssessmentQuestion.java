package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Mapped to UPFIT_ASSESSMENT_QUESTIONS table
 */

@Entity
@Table(name = "UPFIT_ASSESSMENT_QUESTIONS")
public class UpfitAssessmentQuestion extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="UAQ_SEQ")    
    @SequenceGenerator(name="UAQ_SEQ", sequenceName="UAQ_SEQ", allocationSize=1)
    @Basic(optional = false)
    @NotNull
    @Column(name = "UAQ_ID")
    private Long uaqId;

    @Column(name = "QUESTION")
    private String question;
        
    @Column(name = "GROUP_POSITION")
    private Long groupPosition;

    @Column(name = "LINE_POSITION")
    private Long linePosition;
    
    @Column(name = "OBSOLETE_IND", length = 1)
	private String obsoleteYN;

	public Long getUaqId() {
		return uaqId;
	}

	public void setUaqId(Long uaqId) {
		this.uaqId = uaqId;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public Long getGroupPosition() {
		return groupPosition;
	}

	public void setGroupPosition(Long groupPosition) {
		this.groupPosition = groupPosition;
	}

	public Long getLinePosition() {
		return linePosition;
	}

	public void setLinePosition(Long linePosition) {
		this.linePosition = linePosition;
	}

	public String getObsoleteYN() {
		return obsoleteYN;
	}

	public void setObsoleteYN(String obsoleteYN) {
		this.obsoleteYN = obsoleteYN;
	}
}
