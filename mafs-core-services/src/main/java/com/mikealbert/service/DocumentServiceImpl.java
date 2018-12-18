package com.mikealbert.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.data.dao.DocDAO;
import com.mikealbert.data.dao.ExternalAccountDAO;
import com.mikealbert.data.dao.SupplierDAO;
import com.mikealbert.data.entity.Doc;
import com.mikealbert.data.entity.DocSupplier;
import com.mikealbert.data.entity.DocumentTransactionType;
import com.mikealbert.data.entity.Supplier;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.data.enumeration.DocumentType;
import com.mikealbert.data.vo.VehicleOrderStatusSearchCriteriaVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;
import com.mikealbert.util.MALUtilities;

@Service("documentService")
public class DocumentServiceImpl implements DocumentService {
	
	@Resource LookupCacheService lookupCacheService;
	@Resource DocDAO docDAO;
	@Resource ExternalAccountDAO externalAccountDAO;
	@Resource SupplierDAO supplierDAO;
	
	public DocumentTransactionType convertDocumentTransactionTypeCode(CorporateEntity corporateEntity, DocumentType documentType, String transactionTypeCode){
		List<DocumentTransactionType> docTxnTypes = lookupCacheService.getDocumentTransactionTypes();
		DocumentTransactionType docTxnType = null;
		
		for(DocumentTransactionType dtp : docTxnTypes){
			if(dtp.getDocumentTransactionTypePK().getCId().equals(corporateEntity.getCorpId())
					&& dtp.getDocumentTransactionTypePK().getDocumentType().equals(documentType.getdocumentType())
					&& dtp.getDocumentTransactionTypePK().getTransactionType().equals(transactionTypeCode)){
				docTxnType = dtp;
				break;
			}
		}
		
		return docTxnType;		
	}
	
	public Doc searchMainPurchaseOrder(VehicleOrderStatusSearchCriteriaVO searchCriteria) throws MalBusinessException{
		return docDAO.searchMainPurchaseOrder(searchCriteria) ;	
	}
	
	public Doc getMainPODocOfStockUnit(Long fmsId) throws MalBusinessException{
		return docDAO.getMainPODocOfStockUnit(fmsId);
	}
	
	public Doc getDocumentByDocId(Long docId) {
		return docDAO.findById(docId).orElse(null);
	}
	
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void saveUpdateDoc(Doc doc) {
		docDAO.save(doc);
	}
	
	/**
	 * Retrieves the recipient's (Ordering Dealer) email address for a given main purchase order.
	 * Retrieval is based on the business logic defined by JIRA OTD-4820.
	 * 
	 * Note: Copied this logic from PurchaseOrderService.getMainPurchaseOrderDetails
	 * 
	 * @param Doc Main Purchase Order
	 */	
	@Transactional(readOnly=true)
	public String getMainPOEmail(Doc purchaseOrder) throws Exception{
		List<Object[]> vendorContactsList;
		Doc po = getDocumentByDocId(purchaseOrder.getDocId());
		DocSupplier docSupplier;
		String email = null;
				
		try {
			docSupplier = po.getDocSupplier(PurchaseOrderService.WORKSHOP_CAPABILITY_ORDERING);			
			vendorContactsList = externalAccountDAO.getVendorContactDetailsList(1L, "S", docSupplier.getSupplier().getEaAccountCode());
			if(vendorContactsList != null && vendorContactsList.size() > 0) {
				Object[] vendorContactDetails = vendorContactsList.get(0);
				email = (String) (vendorContactDetails[3] != null ? vendorContactDetails[3] : vendorContactDetails[4]);
			}
		} catch (Exception e) {
			throw new MalBusinessException("Error while getting Vendor/Supplier Contact Info for Purchase Order No:  " + purchaseOrder.getDocNo() + " Account No " + po.getAccountCode(), e);
		}
		
		return email;
	}	
	
	/**
	 * Retrieves the recipient's email address for a given third party purchase order.
	 * Retrieval is based on the business logic defined by JIRA OTD-4889.
	 * 
	 * TODO: Business needs to decide on how to handle scenario where there are many suppliers.
	 *       For now, the logic will return null.
	 * @param Doc Third Party Purchase Order
	 */
	public String getThirdPartyPOEmail(Doc purchaseOrder) {
		String email = null;
		List<Supplier> suppliers;
		List<Object[]> vendorContactsList;		
		
		try {		
			vendorContactsList = externalAccountDAO.getVendorContactDetailsList(purchaseOrder.getCId(), purchaseOrder.getAccountType(), purchaseOrder.getAccountCode());
			if(vendorContactsList != null && vendorContactsList.size() > 0) { 
				Object[] vendorContactDetails = vendorContactsList.get(0);			
				email = (String) (vendorContactDetails[3] != null ? vendorContactDetails[3] : vendorContactDetails[4]);
			} else {
				suppliers = supplierDAO.getSuppliersByCidAndAccountCode(purchaseOrder.getCId(), purchaseOrder.getAccountCode());
				if(!MALUtilities.isEmpty(suppliers) && suppliers.size() == 1) {
					email = suppliers.get(0).getEmailAddress();
				}				
			}

		} catch (Exception e) {
			throw new MalException("Error while getting Vendor/Supplier Contact Info for account " + purchaseOrder.getAccountCode() + " on Purchase Order No: " + purchaseOrder.getDocNo(), e);

		}
		
		return email;
				
	}	
}
