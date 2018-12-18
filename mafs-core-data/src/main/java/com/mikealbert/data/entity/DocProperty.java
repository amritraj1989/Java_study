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
 * The persistent class for the DOC_PROPERTIES database table.
 * 
 */
@Entity
@Table(name="DOC_PROPERTIES")
public class DocProperty extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="DPT_SEQ")    
    @SequenceGenerator(name="DPT_SEQ", sequenceName="DPT_SEQ", allocationSize=1)
    @Column(name = "DPT_ID")
    private Long dptId;
    
    @MASize(label = "Property Name", min = 1, max = 40)
    @Column(name = "NAME")
	private String name;

	private String description;

	public Long getDptId() {
		return dptId;
	}

	public void setDptId(Long dptId) {
		this.dptId = dptId;
	}

	public DocProperty() {
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}