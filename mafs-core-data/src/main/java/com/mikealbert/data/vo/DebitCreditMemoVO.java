package com.mikealbert.data.vo;

import java.util.List;

import com.mikealbert.data.entity.AnalysisCategory;
import com.mikealbert.data.entity.AnalysisCode;
import com.mikealbert.data.entity.DebitCreditMemoTransaction;
import com.mikealbert.data.entity.DebitCreditMemoType;
import com.mikealbert.data.entity.PersonnelBase;

public class DebitCreditMemoVO {
	private DebitCreditMemoTransaction debitCreditMemoTransaction;
	private List<DebitCreditMemoType> debitCreditMemoTypes; 
	private List<AnalysisCategory> analysisCategories; 
	private List<AnalysisCode> analysisCodes; 
	private List<PersonnelBase> approverNamesList;

	public DebitCreditMemoTransaction getDebitCreditMemoTransaction() {
		return debitCreditMemoTransaction;
	}

	public void setDebitCreditMemoTransaction(DebitCreditMemoTransaction debitCreditMemoTransaction) {
		this.debitCreditMemoTransaction = debitCreditMemoTransaction;
	}

	public List<DebitCreditMemoType> getDebitCreditMemoTypes() {
		return debitCreditMemoTypes;
	}

	public void setDebitCreditMemoTypes(List<DebitCreditMemoType> debitCreditMemoTypes) {
		this.debitCreditMemoTypes = debitCreditMemoTypes;
	}

	public List<AnalysisCategory> getAnalysisCategories() {
		return analysisCategories;
	}

	public void setAnalysisCategories(List<AnalysisCategory> analysisCategories) {
		this.analysisCategories = analysisCategories;
	}

	public List<AnalysisCode> getAnalysisCodes() {
		return analysisCodes;
	}

	public void setAnalysisCodes(List<AnalysisCode> analysisCodes) {
		this.analysisCodes = analysisCodes;
	}

	public List<PersonnelBase> getApproverNamesList() {
		return approverNamesList;
	}

	public void setApproverNamesList(List<PersonnelBase> approverNamesList) {
		this.approverNamesList = approverNamesList;
	}

}
