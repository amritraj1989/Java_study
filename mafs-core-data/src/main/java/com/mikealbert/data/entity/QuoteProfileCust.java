package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the QUOTE_PROFILE_CUST database table.
 * @author sibley
 */
@Entity
@Table(name="QUOTE_PROFILE_CUST")
public class QuoteProfileCust extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;
    
    @EmbeddedId
	private QuoteProfileCustPK quoteProfileCustPK;
    
	public QuoteProfileCustPK getQuoteProfileCustPK() {
		return quoteProfileCustPK;
	}

	public void setQuoteProfileCustPK(QuoteProfileCustPK quoteProfileCustPK) {
		this.quoteProfileCustPK = quoteProfileCustPK;
	}

	@Override
	public String toString() {
		return "QuoteProfileCust [quoteProfileCustPK=" + quoteProfileCustPK + "]";
	}	
	
}