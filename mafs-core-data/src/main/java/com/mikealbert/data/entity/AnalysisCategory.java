package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "ANALYSIS_CATEGORIES")
public class AnalysisCategory implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @NotNull
    @Column(name = "CATEGORY_ID")
    private Long categoryId;

    @Column(name = "ANALYSIS_CATEGORY")
    private String analysisCategory;

    @Column(name = "CATEGORY_TYPE")
    private String categoryType;

    @Column(name = "DESCRIPTION")
    private String description;
            
    @Column(name = "LENGTH")
    private Long length;

    @Column(name = "SYS_IND")
    private String sysInd;

    @Column(name = "DEBIT_CREDIT_MEMO_IND")
    private String debitCreditMemoInd;

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public String getAnalysisCategory() {
		return analysisCategory;
	}

	public void setAnalysisCategory(String analysisCategory) {
		this.analysisCategory = analysisCategory;
	}

	public String getCategoryType() {
		return categoryType;
	}

	public void setCategoryType(String categoryType) {
		this.categoryType = categoryType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getLength() {
		return length;
	}

	public void setLength(Long length) {
		this.length = length;
	}

	public String getSysInd() {
		return sysInd;
	}

	public void setSysInd(String sysInd) {
		this.sysInd = sysInd;
	}

	public String getDebitCreditMemoInd() {
		return debitCreditMemoInd;
	}

	public void setDebitCreditMemoInd(String debitCreditMemoInd) {
		this.debitCreditMemoInd = debitCreditMemoInd;
	}
}
