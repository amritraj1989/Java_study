package com.mikealbert.data.vo;

import java.io.Serializable;

import com.mikealbert.data.entity.ExternalAccount;

public class GlCodeProcParamsVO implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private long cId;
	private String documentType;
	private String transType;
	private String sourceCode;
	private String extAccGroupCode;
	private String analysisCode;
	private long clnId;
	private long lelId;
	private ExternalAccount externalAccount;
	private String analysisCategory;
	
	public GlCodeProcParamsVO(){}
	
	public long getcId() {
		return cId;
	}
	
	public void setcId(long cId) {
		this.cId = cId;
	}
	
	public String getDocumentType() {
		return documentType;
	}
	
	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}
	
	public String getTransType() {
		return transType;
	}
	
	public void setTransType(String transType) {
		this.transType = transType;
	}
	
	public String getSourceCode() {
		return sourceCode;
	}
	
	public void setSourceCode(String sourceCode) {
		this.sourceCode = sourceCode;
	}
	
	public String getExtAccGroupCode() {
		return extAccGroupCode;
	}
	
	public void setExtAccGroupCode(String extAccGroupCode) {
		this.extAccGroupCode = extAccGroupCode;
	}
	
	public String getAnalysisCode() {
		return analysisCode;
	}
	
	public void setAnalysisCode(String analysisCode) {
		this.analysisCode = analysisCode;
	}
	
	public long getClnId() {
		return clnId;
	}
	
	public void setClnId(long clnId) {
		this.clnId = clnId;
	}
	
	public long getLelId() {
		return lelId;
	}
	
	public void setLelId(long lelId) {
		this.lelId = lelId;
	}
	
	public ExternalAccount getExternalAccount() {
		return externalAccount;
	}
	
	public void setExternalAccount(ExternalAccount externalAccount) {
		this.externalAccount = externalAccount;
	}
	
	public String getAnalysisCategory() {
		return analysisCategory;
	}
	
	public void setAnalysisCategory(String analysisCategory) {
		this.analysisCategory = analysisCategory;
	}

}
