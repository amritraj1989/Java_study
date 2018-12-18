package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import com.mikealbert.data.beanvalidation.MASize;


/**
 * The persistent class for the QUOTE_REQUEST_TYPES database table.
 * 
 */
@Entity
@Table(name="QUOTE_REQUEST_CLOSE_REASONS")
public class QuoteRequestCloseReason extends BaseEntity implements Serializable{
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="QRCR_SEQ")    
    @SequenceGenerator(name="QRCR_SEQ", sequenceName="QRCR_SEQ", allocationSize=1)
    @Column(name = "QRCR_ID")
    private Long qrcrId;
    
    @Column(name = "NAME")
	private String name;

    @Column(name = "DESCRIPTION")
	private String description;
    
    @Size(min=1, max=1)
    @Column(name = "INPUT_REQUIRED_IND")
	private String inputRequiredInd;   
    
    public QuoteRequestCloseReason(){}

	public Long getQrcrId() {
		return qrcrId;
	}

	public void setQrcrId(Long qrcrId) {
		this.qrcrId = qrcrId;
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

	public String getInputRequiredInd() {
		return inputRequiredInd;
	}

	public void setInputRequiredInd(String inputRequiredInd) {
		this.inputRequiredInd = inputRequiredInd;
	}
	
}