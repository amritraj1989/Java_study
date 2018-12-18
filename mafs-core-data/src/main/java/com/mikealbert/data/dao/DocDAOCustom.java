package com.mikealbert.data.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.mikealbert.data.entity.Doc;
import com.mikealbert.data.vo.ArCreationVO;
import com.mikealbert.data.vo.VehicleOrderStatusSearchCriteriaVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;

public interface DocDAOCustom {
	public List<Doc> findInvoiceHeaderForPoNo(String poNumber);
	
	public Doc	findByDocNoAndDocTypeAndSourceCodeAndStatusForInvoiceEntry(String docNo, String docType,List<String> sourceCode, String Status);
	
	public boolean postInvoice(Long docId, String opCode)throws MalBusinessException;
	public Long getMissingInvoiceLineCount(Long invoiceDocId, Long poDocId);
	public Long getModelInvoiceLineCount(Long invoiceDocId);
	public Doc searchMainPurchaseOrder(VehicleOrderStatusSearchCriteriaVO searchCriteria) throws MalBusinessException;
	public Doc getMainPODocOfStockUnit(Long fmsId) throws MalException;
	
	public String getOptionalAccessories(Long docId) throws MalException;
	
	public Long createInvoiceAR(ArCreationVO arVO) throws MalException;
	
	public Long getUnitPurchaseOrderDocIdFromQmdId(Long qmdId, String statuses);
	public List<Long> getUpfitPurchaseOrderDocIdsFromQmdIdAndAccount(Long qmdId, List<String> statuses, Long cId, String accountCode, String accountType, boolean includeMainPO) throws Exception;
	public List<Long> getUnitUpfitPurchaseOrderDocIdsFromQmdId(Long qmdId, List<String> statuses);	
	public BigDecimal getCdFeeUnitCost(Long docId);
	public void rechargeCapitalContribution(long cId, long contractLineId, long quotationModelId, Date date, String userName) throws MalException;	
}

