package com.mikealbert.service;


import com.mikealbert.data.entity.Doc;
import com.mikealbert.data.entity.DocumentTransactionType;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.data.enumeration.DocumentType;
import com.mikealbert.data.vo.VehicleOrderStatusSearchCriteriaVO;
import com.mikealbert.exception.MalBusinessException;


public interface DocumentService {	
	
	public DocumentTransactionType convertDocumentTransactionTypeCode(CorporateEntity corporateEntity, DocumentType documentType, String transactionTypeCode);	
	public Doc searchMainPurchaseOrder(VehicleOrderStatusSearchCriteriaVO searchCriteria) throws MalBusinessException;
	public Doc getMainPODocOfStockUnit(Long fmsId) throws MalBusinessException;
	public Doc getDocumentByDocId(Long docId);
	public void saveUpdateDoc(Doc doc);
	
	public String getMainPOEmail(Doc purchaseOrder)  throws Exception;	
	public String getThirdPartyPOEmail(Doc purchaseOrder);		
}
