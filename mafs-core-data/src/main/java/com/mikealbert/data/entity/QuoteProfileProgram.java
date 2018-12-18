package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


/**
 * The persistent class for the QUOTE_PROFILE_PROGRAMS database table.
 * 
 */
@Entity
@Table(name="QUOTE_PROFILE_PROGRAMS")
public class QuoteProfileProgram extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private QuoteProfileProgramPK id;

	//bi-directional many-to-one association to EtXsmProgram
    @ManyToOne
	@JoinColumn(name="EXP_EXP_ID")
	private EtXsmProgram etXsmProgram;

    //TODO Need to remove insertable and updatable
    // This need to done here because QPR_QPR_ID field is aready mapped in QuoteProfileProgramPK
    @ManyToOne()
	@JoinColumn(name="QPR_QPR_ID", insertable=false,updatable=false)
	private QuotationProfile quotationProfile;

    public QuoteProfileProgram() {
    }

	public QuoteProfileProgramPK getId() {
		return this.id;
	}

	public void setId(QuoteProfileProgramPK id) {
		this.id = id;
	}	
	
	public EtXsmProgram getEtXsmProgram() {
		return this.etXsmProgram;
	}

	public void setEtXsmProgram(EtXsmProgram etXsmProgram) {
		this.etXsmProgram = etXsmProgram;
	}
	
	public QuotationProfile getQuotationProfile() {
		return this.quotationProfile;
	}

	public void setQuotationProfile(QuotationProfile quotationProfile) {
		this.quotationProfile = quotationProfile;
	}
	
	@Override
    public String toString() {
        return "com.mikealbert.vision.entity.QuoteProfileProgram[ id=" + id + " ]";
    }
}