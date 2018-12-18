package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "ACC_TRANS_PARAMETER_SETS")
public class AccTransParameterSet implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @NotNull
    @Column(name = "ACC_TRANSACTION")
    private String accTransaction;

    @Column(name = "SOURCE_CODE")
    private String sourceCode;

    @Column(name = "TRANS_TYPE")
    private String transType;

    @Column(name = "COST_DB_CODE")
    private String costDbCode;

    @Column(name = "CATEGORY_TYPE")
    private String categoryType;

    @Column(name = "ANALYSIS_CATEGORY")
    private String analysisCategory;

    @Column(name = "ANALYSIS_CODE")
    private String analysisCode;

    @Column(name = "CHARGE_CODE")
    private String chargeCode;

    public AccTransParameterSet() {
    }

	public String getAccTransaction() {
		return accTransaction;
	}

	public void setAccTransaction(String accTransaction) {
		this.accTransaction = accTransaction;
	}

	public String getSourceCode() {
		return sourceCode;
	}

	public void setSourceCode(String sourceCode) {
		this.sourceCode = sourceCode;
	}

	public String getTransType() {
		return transType;
	}

	public void setTransType(String transType) {
		this.transType = transType;
	}

	public String getCostDbCode() {
		return costDbCode;
	}

	public void setCostDbCode(String costDbCode) {
		this.costDbCode = costDbCode;
	}

	public String getCategoryType() {
		return categoryType;
	}

	public void setCategoryType(String categoryType) {
		this.categoryType = categoryType;
	}

	public String getAnalysisCategory() {
		return analysisCategory;
	}

	public void setAnalysisCategory(String analysisCategory) {
		this.analysisCategory = analysisCategory;
	}

	public String getAnalysisCode() {
		return analysisCode;
	}

	public void setAnalysisCode(String analysisCode) {
		this.analysisCode = analysisCode;
	}

	public String getChargeCode() {
		return chargeCode;
	}

	public void setChargeCode(String chargeCode) {
		this.chargeCode = chargeCode;
	}

}
