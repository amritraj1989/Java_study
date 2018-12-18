package com.mikealbert.data.enumeration;

public enum DocumentType {

	ACCOUNTS_RECEIVABLE("INVOICEAR"), 
	ACCOUNTS_PAYABLE("INVOICEAP"), 
	PURCHASE_ORDER("PORDER");
	
	private String documentType;
	
	private DocumentType(String documentType){
		this.documentType = documentType;
	}
	
	public String getdocumentType(){
		return this.documentType;
	}
	
	
}
