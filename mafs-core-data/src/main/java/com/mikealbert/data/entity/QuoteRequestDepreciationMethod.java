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
import javax.validation.constraints.Size;


/**
 * The persistent class for the QUOTE_REQUEST_DEPR_METHODS database table.
 * @author Raj
 */
@Entity
@Table(name="QUOTE_REQUEST_DEPR_METHODS")
public class QuoteRequestDepreciationMethod extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="QRDM_SEQ")    
    @SequenceGenerator(name="QRDM_SEQ", sequenceName="QRDM_SEQ", allocationSize=1)  
    @Basic(optional = false)
    @Column(name = "QRDM_ID")
    private Long quoteRequestDepreciationMethodId;
    
    @Size(min = 1, max = 20)
    @NotNull
    @Column(name = "CODE")
    private String code;
    
    @Size(min = 1, max = 25)
    @NotNull
    @Column(name = "NAME")
    private String name;
    
    @Size(min = 1, max = 80)
    @NotNull
    @Column(name = "DESCRIPTION")
    private String description;

	public Long getQuoteRequestDepreciationMethodId() {
		return quoteRequestDepreciationMethodId;
	}

	public void setQuoteRequestDepreciationMethodId(Long quoteRequestDepreciationMethodId) {
		this.quoteRequestDepreciationMethodId = quoteRequestDepreciationMethodId;
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
	
	@Override
    public String toString() {
        return "com.mikealbert.vision.entity.QUOTE_REQUEST_DEPR_METHODS[ quoteRequestDepreciationMethodId=" + quoteRequestDepreciationMethodId + " ]";
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((quoteRequestDepreciationMethodId == null) ? 0 : quoteRequestDepreciationMethodId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof QuoteRequestDepreciationMethod))
			return false;
		QuoteRequestDepreciationMethod other = (QuoteRequestDepreciationMethod) obj;
		if (quoteRequestDepreciationMethodId == null) {
			if (other.quoteRequestDepreciationMethodId != null)
				return false;
		} else if (!quoteRequestDepreciationMethodId.equals(other.quoteRequestDepreciationMethodId))
			return false;
		return true;
	}
   
	
}