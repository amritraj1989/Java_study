package com.mikealbert.data.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.vo.ActiveQuoteVO;
import com.mikealbert.data.vo.CnbvVO;
import com.mikealbert.data.vo.DbProcParamsVO;
import com.mikealbert.data.vo.ProcessQueueResultVO;
import com.mikealbert.data.vo.QuotationSearchVO;
import com.mikealbert.data.vo.RevisionVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;


public interface QuotationModelDAOCustom  {	
	
	public Long getOriginalQuoteModelId(Long qmdId);
	public String getLeaseType(Long qmdId);
	public Double getProfitability(Long qmdId, String profitType, String profitSource);
	public Double getMinimumProfit(Long qmdId, String profitType, String profitSource);
	public Double getFinanceParam(String  parameterkey, Long qmdId, Long qprId, Date effectiveDate);
	public Double getFinanceParam(String  parameterkey, Long qmdId, Long qprId, Date effectiveDate, boolean skipQuoteLevelOverrides);
	public Double getFinanceParam(String parameterkey, Long qmdId, Long qprId, Date effectiveDate, boolean skipQuoteLevelOverrides, Long cId, String accountType, String accountCode);
	public BigDecimal getInterestRate(Long qmdId, Long qprId,Date effectiveDate,Long contractPeriod);
	public BigDecimal getGlobalBaseInterestRate(Date effectiveDate, Long contractPeriod, String interestType);	
	public BigDecimal getInterestRateByType(Date effectiveDate, Long contractPeriod, String interestType, String reviewFrequency, String fixedOrFloat);
	public String getInterestType(Long qmdId ,Long lelId, Date revisionDate); // added lelid for HD-215
	public BigDecimal fetchMaintValue(Long mtbId, Long period,Long distance) throws MalBusinessException;
	public BigDecimal getBaseRate(long qmdId) throws MalBusinessException ;
	public BigDecimal getProfileBaseRateByTerm(long qmdId, long term) throws MalBusinessException ;
	public BigDecimal getHurdleRate(Long qmdId, Long term) throws MalBusinessException ;
	public BigDecimal getRevisionInterestAdjustment(Long cId, Long clnId, BigDecimal monthUsed, String productType, int contractPeriod, BigDecimal marketValue, String effectiveDate, String isOEQuoteRevision) throws MalBusinessException;
	public List<Long> getBaseCapitalElementList(long qmdId) throws MalBusinessException;
	public String getVQRefNo(Long qmdId, String currRefNo) ;
	public BigDecimal getMafsAuthorizationLimit(Long corpId, String accountType, String accountCode, String unitNo) throws MalBusinessException;
	public BigDecimal getDriverAuthorizationLimit(Long corpId, String accountType, String accountCode, String unitNo) throws MalBusinessException;
	public Long	getDealerAcc(Long oldQmdId, Long newQmdId, Long newQdaId);
	public BigDecimal	getCustInvPrice(Long qmdId);
	public  List<ProcessQueueResultVO> getUnitsToFinalizeQueueResults(String unitNo, String vin6,Pageable page, Sort sort);
	public void saveWithLock(QuotationModel model);
	public Long getQmdIdFromFmsId(long fmsId, Date date);

	public  List<ProcessQueueResultVO> getReleaseOnlyPoResults(String unitNo, String vin6,Pageable page, Sort sort);
	public Long	getCourtesyDeliveryElementId();
	public String	getGrdSupplierCode(Long cId);
	public Integer	getCountOfReleasedOnlyPoResults(String unitNo, String vin6);
	public Long createRevisedQuote(Long acceptedQmdId, String opCode);
	public Long getRevisedQmd(long acceptedQmd);

	public Long getPriorAcceptedQmd(long qmdId);
	public boolean hasPriorAcceptedQuoteModel(long qmdId);
	
	public QuotationModel	getDetachedQuotationModel(QuotationModel quotationModel);
	public boolean deleteQuotationModel(Long qmdId) throws MalBusinessException;
	public String getFleetStatus(Long fmsId);
	
	public BigDecimal getResidualAmount(Long qmdId, Long qdaId);
	public BigDecimal getResidualAmount(Long rtbId, Long period, Long distance) throws MalBusinessException;
	public String getProductType(Long qmdId);
	public Long getQutationModelForTransportByFmsId(Long fmsId);
	public Long getQuoteAccessoryLeadTimeByQdaId(Long qdaId);
	public String getClientRequestTypeValue(Long qmdId);
	public boolean stockValidityCheck(Long qmdId, Long fmsId) throws MalBusinessException;
	public void stockFinalAccept(Long qmdId, Long fmsId, Date contractStartDate, Long odoMeterReading, Date odoReadingDate, String odoReadingType, String employeeNo) throws MalException;
	public DbProcParamsVO acceptQuote(Long qmdId, String employeeNo) throws MalException;
	public String getPlateTypeCodeDescription(String plateTypeCode);
	public List<QuotationSearchVO> searchQuotations(Long corpId, String accountType, String accountCodeOrName, List<String> productTypeCodes, String unitNo, String vin6, Long projectedMonths, Pageable page, Sort sort) throws MalBusinessException;
	public int searchQuotationsCount(Long corpId, String accountType, String accountCodeOrName, List<String> productTypeCodes, String unitNo, String vin6, Long projectedMonths);
	public boolean hasPendingInformalAmendment(Long qmdId);
	public List<ActiveQuoteVO> getActiveQuoteVOs(Long fmsId);
	public String getExcessMileage(Long qmdId);
		
	public String getReportNameByModuleAndProductCode(String moduleName, String productCode) throws MalBusinessException;
	
	public BigDecimal getInterestRateByQuotationProfile(Long qprId,Date effectiveDate,Long term) throws MalBusinessException;
	
	public Long generateCfgId();
	
	public Long createContractRevisionQuote( Long oldQmdId, String contractRevType, String employeeNo);
	public List<RevisionVO> getListByQmdAndStatus(Long qmdId, List<String> quoteStatusCodes);
	public String generateWillowReportUrl(String moduleName, String paramString) throws MalBusinessException;
	public List<Long> getPreviousQMDForSteps(long qmdId) ;
	public BigDecimal getApplicableCapitalContribution(Long qmdId);
	public Long getOriginalQmdIdOnCurrentContract(Long qmdId);	
	public String compareProfiles(Long oldQprId, Long newQprId) throws MalBusinessException;
	public List<CnbvVO> getCnbv(Long lastestQmdId, Long anyOldQmdId);
}
