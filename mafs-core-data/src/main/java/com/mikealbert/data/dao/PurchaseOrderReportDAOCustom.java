package com.mikealbert.data.dao;

import java.util.Date;
import java.util.List;

import com.mikealbert.data.enumeration.ReportNameEnum;
import com.mikealbert.data.vo.ReportContactVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;


/**
* Custom DAO for PurchaseOrderReport 
* @author ravresh
*/

public interface PurchaseOrderReportDAOCustom {
	
	public List<Object[]> getPurchaseOrderDetailForReport(Long docId, String stockYn); 
	public List<Object[]> getAccountAddressInfo(String accountCode) throws MalException;
	public List<Object[]> getVendorInfo(Long parentDocId) throws MalException;	
	public List<Object[]> getVendorInfo(Long parentDocId , Long qmdId) throws MalException;
	public List<Object[]> getDealerContactDetailsList(Long cId, String accountType, String accountCode) throws MalException;
	public List<Object[]> getVendorContactDetailsList(Long cId, String accountType, String accountCode) throws MalException;
	public List<Object> getTypedInstalledAccessories(Long qmdId, String categoryCode) throws MalException;
	public List<Object> getTypedInstalledAccessoriesPO(Long docId, String accessoryType, String categoryCode) throws MalException;
	public List<Object> getStandardAccessories(Long qmdId) throws MalException;
	public List<Object> getModelAccessories(Long qmdId) throws MalException ;
	public List<Object> getDealerAccessories(Long qmdId) throws MalException ;
	public Long getMainDocIdForNonStock(Long thpDocId) throws MalException;
	public List<Object> getDealerAccessoriesForPO(Long docId) throws MalException ;
	public List<Object[]> getThirdPartyPODetails(Long tptDocId) throws MalException;
	public List<Object> getVendorQuoteNumbers(Long qmdId, Long tptDocId) throws MalException;
	public List<Object> getPowertrainInfo(Long qmdId) throws MalException;
	public List<Object> getPowertrainInfoForDoc(Long mainPODocId) throws MalException;
	public String[] getReportEmailRecipients(ReportNameEnum reportName, Long cId, String accountType, String accountCode, Long fmsId, Long qmdId);
	public List<ReportContactVO> getReportEmailContacts(ReportNameEnum reportName, Long cId, String accountType, String accountCode, Long fmsId, Long qmdId);	
	public String getReportEmailSender(ReportNameEnum reportName);
	public String getReportEmailSubject(ReportNameEnum reportName);
	public String getReportEmailBody(ReportNameEnum reportName);	
	public String getReportOverridenLogoContext(String productCode) throws MalException;	
	public String getDocNarratives(Long docId ,String noteType) throws MalException;	
	public String getDoclNarratives(Long docId ,Long lineId) throws MalException;
	public Date getFirstPOPostedDate(String docNo)  throws MalException;
	public boolean isPostProductionIsPortInstalledForMake(Long mdlId) throws MalException;
	public String getReportText(Long docId, String reportName, String textLocation) throws MalBusinessException;
	public String getBailmentIndicatorByQmdIdAndAccountInfo(Long qmdId, String accountCode, String accountType, Long cId);
}
