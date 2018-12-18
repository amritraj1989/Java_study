package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;

/**
 * The persistent class for the ET_XSM_PROGRAMS database table.
 * 
 */
@Entity
@Table(name = "ET_XSM_PROGRAMS")
public class EtXsmProgram extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "EXP_ID")
	private long expId;

	@Column(name = "ET_ELIGIBILITY_CALC")
	private String etEligibilityCalc;

	@Column(name = "PROGRAM_CODE")
	private String programCode;

	@Column(name = "PROGRAM_DESCRIPTION")
	private String programDescription;

	@Column(name = "XSM_ELIGIBILITY_CALC")
	private String xsmEligibilityCalc;

	// bi-directional many-to-one association to QuoteProfileProgram
	@OneToMany(mappedBy = "etXsmProgram")
	private List<QuoteProfileProgram> quoteProfilePrograms;

	public EtXsmProgram() {
	}

	public long getExpId() {
		return this.expId;
	}

	public void setExpId(long expId) {
		this.expId = expId;
	}

	public String getEtEligibilityCalc() {
		return this.etEligibilityCalc;
	}

	public void setEtEligibilityCalc(String etEligibilityCalc) {
		this.etEligibilityCalc = etEligibilityCalc;
	}

	public String getProgramCode() {
		return this.programCode;
	}

	public void setProgramCode(String programCode) {
		this.programCode = programCode;
	}

	public String getProgramDescription() {
		return this.programDescription;
	}

	public void setProgramDescription(String programDescription) {
		this.programDescription = programDescription;
	}

	public String getXsmEligibilityCalc() {
		return this.xsmEligibilityCalc;
	}

	public void setXsmEligibilityCalc(String xsmEligibilityCalc) {
		this.xsmEligibilityCalc = xsmEligibilityCalc;
	}

	public List<QuoteProfileProgram> getQuoteProfilePrograms() {
		return this.quoteProfilePrograms;
	}

	public void setQuoteProfilePrograms(
			List<QuoteProfileProgram> quoteProfilePrograms) {
		// this.quoteProfilePrograms = quoteProfilePrograms;
	}

	@Override
	public String toString() {
		return "com.mikealbert.vision.entity.EtXsmProgram[ expId=" + expId
				+ " ]";
	}
}