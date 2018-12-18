package com.mikealbert.service.task;

import java.io.Serializable;


public class LeaseFinalizationDocumentArchiveTask extends AbstractTask implements Serializable{

	private static final long serialVersionUID = 7486264177431645396L;
	
	private Long qmdId;
	private String fileName;	
	private String documentName;
	private String deliveryMethod;
	private String user;
	private Long activeContractQmdId;//applicable for revision 
	private String quoteType;

	public Long getQmdId() {
		return qmdId;		
	}

	public void setQmdId(Long qmdId) {
		this.qmdId = qmdId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public String getDeliveryMethod() {
		return deliveryMethod;
	}

	public void setDeliveryMethod(String deliveryMethod) {
		this.deliveryMethod = deliveryMethod;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getQuoteType() {
		return quoteType;
	}

	public void setQuoteType(String quoteType) {
		this.quoteType = quoteType;
	}

	public Long getActiveContractQmdId() {
		return activeContractQmdId;
	}

	public LeaseFinalizationDocumentArchiveTask setActiveContractQmdId(Long activeContractQmdId) {
		this.activeContractQmdId = activeContractQmdId;
		return this;
	}

	
	
}
