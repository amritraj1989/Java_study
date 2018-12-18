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
 * The persistent class for the QUOTE_REQUEST_TYPES database table.
 * 
 */
@Entity
@Table(name="QUOTE_REQUEST_TYPES")
public class QuoteRequestType implements Serializable {
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="QRT_SEQ")    
    @SequenceGenerator(name="QRT_SEQ", sequenceName="QRT_SEQ", allocationSize=1)
    @Column(name = "QRT_ID")
    private Long qrtId;
    
    @Column(name = "NAME")
	private String name;

    @Column(name = "CODE")
	private String code;

	public Long getQrtId() {
		return qrtId;
	}

	public void setQrtId(Long qrtId) {
		this.qrtId = qrtId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((qrtId == null) ? 0 : qrtId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof QuoteRequestType))
			return false;
		QuoteRequestType other = (QuoteRequestType) obj;
		if (qrtId == null) {
			if (other.qrtId != null)
				return false;
		} else if (!qrtId.equals(other.qrtId))
			return false;
		return true;
	}

	
}