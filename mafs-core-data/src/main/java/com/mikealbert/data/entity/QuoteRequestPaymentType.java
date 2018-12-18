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
 * The persistent class for the QUOTE_REQUEST_PAYMENT_TYPES database table.
 * @author Raj
 */
@Entity
@Table(name="QUOTE_REQUEST_PAYMENT_TYPES")
public class QuoteRequestPaymentType extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="QRPT_SEQ")    
    @SequenceGenerator(name="QRPT_SEQ", sequenceName="QRPT_SEQ", allocationSize=1)  
    @Basic(optional = false)
    @Column(name = "QRPT_ID")
    private Long quoteRequestPaymentTypeId;
    
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

	public Long getQuoteRequestPaymentTypeId() {
		return quoteRequestPaymentTypeId;
	}

	public void setQuoteRequestPaymentTypeId(Long quoteRequestPaymentTypeId) {
		this.quoteRequestPaymentTypeId = quoteRequestPaymentTypeId;
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
        return "com.mikealbert.vision.entity.QUOTE_REQUEST_PAYMENT_TYPES[ quoteRequestPaymentTypeId=" + quoteRequestPaymentTypeId + " ]";
    }   
}