package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the REVIEW_FREQUENCY_CODES database table.
 * 
 */
@Entity
@Table(name="REVIEW_FREQUENCY_CODES")
public class ReviewFrequencyCode implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="REVIEW_FREQUENCY")
	private String reviewFrequency;

	@Column(name="DESCRIPTION")
	private String description;

	@Column(name="NO_MONTHS")
	private Long noOfMonths;

	public String getReviewFrequency() {
		return reviewFrequency;
	}

	public void setReviewFrequency(String reviewFrequency) {
		this.reviewFrequency = reviewFrequency;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getNoOfMonths() {
		return noOfMonths;
	}

	public void setNoOfMonths(Long noOfMonths) {
		this.noOfMonths = noOfMonths;
	}

}