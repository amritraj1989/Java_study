package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


/**
 * The persistent class for the VEHICLE_ACQUISITION_TYPES database table.
 * 
 */
@Entity
@Table(name="QUOTE_REQUEST_STATUSES")
public class QuoteRequestStatus implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="QRS_SEQ")    
    @SequenceGenerator(name="QRS_SEQ", sequenceName="QRS_SEQ", allocationSize=1)
    @Column(name = "QRS_ID")
    private Long qrsId;
    
	@Column(name = "CODE")
	private String code;
	
	@Column(name = "NAME")
	private String name;

    @Column(name = "DESCRIPTION")
	private String description;

	public Long getQrsId() {
		return qrsId;
	}

	public void setQrsId(Long qrsId) {
		this.qrsId = qrsId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
    
}