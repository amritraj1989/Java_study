package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the GL_CODE database table.
 * 
 */
@Embeddable
public class AnalysisCodePK implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Column(name="CATEGORY_ID")
	private Long categoryId;
	
	@Column(name="ANALYSIS_CODE")
	private String analysisCode;

	public AnalysisCodePK(){}	
	
	public AnalysisCodePK(Long categoryId, String analysisCode) {
		this.categoryId = categoryId;
		this.analysisCode = analysisCode;
	}
	
	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public String getAnalysisCode() {
		return analysisCode;
	}

	public void setAnalysisCode(String analysisCode) {
		this.analysisCode = analysisCode;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((analysisCode == null) ? 0 : analysisCode.hashCode());
		result = prime * result
				+ ((categoryId == null) ? 0 : categoryId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AnalysisCodePK other = (AnalysisCodePK) obj;
		if (analysisCode == null) {
			if (other.analysisCode != null)
				return false;
		} else if (!analysisCode.equals(other.analysisCode))
			return false;
		if (categoryId == null) {
			if (other.categoryId != null)
				return false;
		} else if (!categoryId.equals(other.categoryId))
			return false;
		return true;
	}

}
