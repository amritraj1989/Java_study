package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.mikealbert.data.beanvalidation.MASize;


/**
 * The persistent class for the QUOTE_MODEL_PROPERTIES database table.
 * 
 */
@Entity
@Table(name="QUOTE_MODEL_PROPERTIES")
public class QuoteModelProperty extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="QMP_SEQ")    
    @SequenceGenerator(name="QMP_SEQ", sequenceName="QMP_SEQ", allocationSize=1)
    @Column(name = "QMP_ID")
    private Long qmpId;
    
    @MASize(label = "Property Name", min = 1, max = 40)
    @Column(name = "NAME")
	private String name;

	private String description;

	public Long getQmpId() {
		return qmpId;
	}

	public void setQmpId(Long qmpId) {
		this.qmpId = qmpId;
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