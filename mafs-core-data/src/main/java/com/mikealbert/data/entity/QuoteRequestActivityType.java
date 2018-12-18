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
 * The persistent class for the QUOTE_REQUEST_ACTIVITY_TYPES database table.
 * 
 */
@Entity
@Table(name="QUOTE_REQUEST_ACTIVITY_TYPES")
public class QuoteRequestActivityType extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="QRAT_SEQ")    
    @SequenceGenerator(name="QRAT_SEQ", sequenceName="QRAT_SEQ", allocationSize=1)
    @Column(name = "QRAT_ID")
    private Long qratId;
    
    @Column(name = "NAME")
	private String name;

    @Column(name = "CODE")
	private String code;


	public Long getQratId() {
		return qratId;
	}

	public void setQratId(Long qratId) {
		this.qratId = qratId;
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
		result = prime * result + ((getQratId() == null) ? 0 : getQratId().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof QuoteRequestActivityType))
			return false;
		QuoteRequestActivityType other = (QuoteRequestActivityType) obj;
		if (getQratId() == null) {
			if (other.getQratId() != null)
				return false;
		} else if (!getQratId().equals(other.getQratId()))
			return false;
		return true;
	}

	
}