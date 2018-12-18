package com.mikealbert.service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.hibernate.Hibernate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.common.CommonCalculations;
import com.mikealbert.common.MalConstants;
import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.dao.CapEleParameterDAO;
import com.mikealbert.data.dao.ContractLineDAO;
import com.mikealbert.data.dao.DocDAO;
import com.mikealbert.data.dao.DriverGradeGroupCodeDAO;
import com.mikealbert.data.dao.EarlyTermQuoteDAO;
import com.mikealbert.data.dao.ExternalAccountDAO;
import com.mikealbert.data.dao.FinanceParameterDAO;
import com.mikealbert.data.dao.FleetMasterDAO;
import com.mikealbert.data.dao.HirePeriodDistanceDAO;
import com.mikealbert.data.dao.QuotationCapitalElementDAO;
import com.mikealbert.data.dao.QuotationDAO;
import com.mikealbert.data.dao.QuotationDealerAccessoryDAO;
import com.mikealbert.data.dao.QuotationElementDAO;
import com.mikealbert.data.dao.QuotationElementStepDAO;
import com.mikealbert.data.dao.QuotationModelDAO;
import com.mikealbert.data.dao.QuotationProfileDAO;
import com.mikealbert.data.dao.QuotationProfitabilityDAO;
import com.mikealbert.data.dao.QuotationStandardAccessoryDAO;
import com.mikealbert.data.dao.QuotationStepStructureDAO;
import com.mikealbert.data.dao.QuoteElementParameterDAO;
import com.mikealbert.data.dao.QuoteRejectCodeDAO;
import com.mikealbert.data.dao.ReportDAO;
import com.mikealbert.data.dao.ResidualTableDAO;
import com.mikealbert.data.dao.TimePeriodDAO;
import com.mikealbert.data.dao.VrbDiscountDAO;
import com.mikealbert.data.dao.WillowEntityDefaultDAO;
import com.mikealbert.data.entity.CapEleParameter;
import com.mikealbert.data.entity.CapitalElement;
import com.mikealbert.data.entity.Contract;
import com.mikealbert.data.entity.ContractLine;
import com.mikealbert.data.entity.Doc;
import com.mikealbert.data.entity.EarlyTermQuote;
import com.mikealbert.data.entity.ExtAccAffiliate;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.ExternalAccountPK;
import com.mikealbert.data.entity.FinanceParameter;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.Product;
import com.mikealbert.data.entity.Quotation;
import com.mikealbert.data.entity.QuotationCapitalElement;
import com.mikealbert.data.entity.QuotationDealerAccessory;
import com.mikealbert.data.entity.QuotationElement;
import com.mikealbert.data.entity.QuotationElementStep;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.QuotationStepStructure;
import com.mikealbert.data.entity.QuoteModelPropertyValue;
import com.mikealbert.data.entity.QuoteProfileProgram;
import com.mikealbert.data.entity.QuoteRejectCode;
import com.mikealbert.data.entity.Report;
import com.mikealbert.data.entity.ReportsSpool;
import com.mikealbert.data.entity.ReportsSpoolPK;
import com.mikealbert.data.entity.ResidualTable;
import com.mikealbert.data.entity.TimePeriod;
import com.mikealbert.data.entity.VrbDiscount;
import com.mikealbert.data.entity.WillowEntityDefault;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.data.enumeration.QuoteModelPropertyEnum;
import com.mikealbert.data.enumeration.VehicleStatus;
import com.mikealbert.data.vo.ActiveQuoteVO;
import com.mikealbert.data.vo.CnbvVO;
import com.mikealbert.data.vo.DbProcParamsVO;
import com.mikealbert.data.vo.QuotationSearchVO;
import com.mikealbert.data.vo.QuotationStepStructureVO;
import com.mikealbert.data.vo.QuoteOEVO;
import com.mikealbert.data.vo.RevisionVO;
import com.mikealbert.data.vo.WillowReportParamVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;
import com.mikealbert.service.comparator.QuotationElementStepComparator;
import com.mikealbert.service.vo.FormalClosedEndQuoteInputVO;
import com.mikealbert.service.vo.FormalOpenEndQuoteInputVO;
import com.mikealbert.service.vo.FormalQuoteInput;
import com.mikealbert.service.vo.FormalQuoteStepInputVO;
import com.mikealbert.util.MALUtilities;

/**
 * Implementation of {@link com.mikealbert.vision.service.QuotationService}
 */
@Service("quotationService")
@Transactional
public class QuotationServiceImpl implements QuotationService {
	private MalLogger logger = MalLoggerFactory.getLogger(this.getClass());
	
	@Resource FleetMasterService fleetMasterService;	
	@Resource ContractService contractService;
	@Resource QuotationModelDAO quotationModelDAO;		
	@Resource QuotationDAO quotationDAO;	
	@Resource QuotationProfitabilityDAO	quotationProfitabilityDAO;
	@Resource QuotationElementDAO quotationElementDAO;
	@Resource QuotationStepStructureDAO quotationStepStructureDAO;
	@Resource VrbDiscountDAO vrbDiscountDAO;
	@Resource QuotationCapitalElementDAO quotationCapitalElementDAO;
	@Resource CapEleParameterDAO capEleParameterDAO;
	@Resource FinanceParameterDAO financeParameterDAO;
	@Resource DocDAO docDAO;
	@Resource ExternalAccountDAO externalAccountDAO;
	@Resource QuotationDealerAccessoryDAO quotationDealerAccessoryDAO;
	@Resource QuoteElementParameterDAO quoteElementParameterDAO;
	@Resource QuotationElementStepDAO quotationElementStepDAO;
	@Resource QuotationStandardAccessoryDAO quotationStandardAccessoryDAO;
	@Resource DriverGradeGroupCodeDAO driverGradeGroupCodeDAO;
	@Resource ContractLineDAO contractLineDAO;
	@Resource WillowConfigService willowConfigService;
	@Resource TimePeriodDAO timePeriodDAO;
	@Resource WillowEntityDefaultDAO willowEntityDefaultDAO;
	@Resource FleetMasterDAO fleetMasterDAO;
	@Resource ReportDAO reportDAO;
	@Resource ReportsSpoolService reportsSpoolService;
	@Resource HirePeriodDistanceDAO hirePeriodDistanceDAO;
	@Resource ProfitabilityService profitabilityService;
	@Resource QuotationProfileService quotationProfileService;
	@Resource EarlyTermQuoteDAO earlyTermQuoteDAO;
	@Resource QuoteRejectCodeDAO quoteRejectCodeDAO;
	@Resource QuotationProfileDAO quotationProfileDAO;
	@Resource RentalCalculationService rentalCalculationService;
	@Resource QuoteModelPropertyValueService quoteModelPropertyValueService;
	@Resource ResidualTableDAO residualTableDAO;
	
	private static final int DEPRECIATION_FACTOR_SCALE = 7;	//TODO factor this out	
					
	/**
	 * Returns original quotation model if exists for quotation model id passed.
	 * @param	qmdId qmdId of quotation model whose original quotation model is needed 
	 * @return  original quotation model if exists else null
	 */
	public QuotationModel  getOriginalQuoteModel(Long qmdId){
		
		Long origQuoteModelId = null;
		QuotationModel quoteModel = null; 
		
		if(qmdId != null) {
			origQuoteModelId = quotationModelDAO.getOriginalQuoteModelId(qmdId);
		}
		if(origQuoteModelId != null) {
			 quoteModel = quotationModelDAO.findById(origQuoteModelId).orElse(null);
		}
		
		return quoteModel;
	}
	
	public Long getOriginalQmdIdOnCurrentContract(Long qmdId){
		return quotationModelDAO.getOriginalQmdIdOnCurrentContract(qmdId);
	}

	/**
	 * Looks product code of quotation and returns true if its product code is short term product else false.
	 * @param	quoId, quotation id of quotation
	 */
	@Override
	public boolean isQuotationForShortTerm(Long quoId) throws MalBusinessException{
		
		boolean isQuotationForShortTerm = false;
				
		Quotation quotation 	 = quotationDAO.findById(quoId).orElse(null);
		if(quotation == null){
			throw new MalBusinessException("service.validation", new String[]{ "No quotation found for id "+quoId});	
		}
		
		if(quotation.getQuotationProfile().getPrdProductCode().equals(SHORT_TERM_PRODUCT_CODE)){
			isQuotationForShortTerm = true;
		}			
		
		return isQuotationForShortTerm;
	}
	
	/**
	 * Returns lease type of quotation model.
	 * @param	qmdId, qmdId of quotation model 
	 */
	@Override
	public String getLeaseType(Long qmdId){
		return quotationModelDAO.getLeaseType(qmdId);
	}
	
	/**
	 * Looks for quote status of quotation model.
	 * Returns true if quote status is either 8, 15, 18 or 19 else false.
	 * @throws MalBusinessException
	 */
	public	boolean isStandardQuoteModel(Long qmdId) throws MalBusinessException {
		
		QuotationModel quotationModel = quotationModelDAO.findById(qmdId).orElse(null);
		if(quotationModel == null){
			throw new MalBusinessException("service.validation", new String[]{ "No quotation model found for id "+qmdId});	
		}
		 boolean 	idStandardQuoteModel= false;
			if(quotationModel != null){
				if( quotationModel.getQuoteStatus() == QuotationService.STATUS_STANDARD_ORDER 
						|| quotationModel.getQuoteStatus() == QuotationService.STATUS_STANDARD_ORDER_REVISION
						//|| quotationModel.getQuoteStatus() == QuotationService.STATUS_STANDARD_ORDER_REJECTED
						//Commented above condition for RC-1145. Wrong quote status is used to check for a standard order
						|| quotationModel.getQuoteStatus() == QuotationService.STATUS_INACTIVE_STANDARD_ORDER_REVISION) {
					
					idStandardQuoteModel = true;
				}	
			}
			
			return idStandardQuoteModel;
			
	}
	
	/**
	 * Returns quotation if exists or throws service error message.
	 * @param	quoId, quotation id to look for
	 * @throws MalBusinessException
	 */
	public	Quotation getQuote(Long quoId) throws MalBusinessException {
		
		Quotation Quotation = null;
		if(quoId != null){
			Quotation =  quotationDAO.findById(quoId).orElse(null);
		}else{
			throw new MalBusinessException("service.validation", new String[]{ "Quotation id must be not null."});
		}
		return Quotation;
	}
	/**
	 * Returns quotation model if exists or throws service error message.
	 * @param	qmdId, quotation model id to look for
	 * @throws MalBusinessException
	 */
	public	QuotationModel getQuotationModel(Long qmdId) throws MalBusinessException {
		
		QuotationModel quotationModel = null;
		if(qmdId != null){
			quotationModel =  quotationModelDAO.findById(qmdId).orElse(null);
		}else{
			throw new MalBusinessException("service.validation", new String[]{ "Quotation model id must be not null."});
		}
		return quotationModel;
	}
	
	/**
	 * Updates quotation model and commits changes.
	 * Roll back changes if any exception occurs
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public	void updateQuotationModel(QuotationModel quotationModel) {
		quotationModelDAO.save(quotationModel);		
	}
	/**
	 * Updates quotations and commits changes.
	 * Roll back changes if any exception occurs
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public	void updateQuotation(Quotation quotation) {
		quotationDAO.save(quotation);
	}	
	/**
	 * Updates quotation element and commits changes.
	 * Roll back changes if any exception occurs
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public	void updateQuotationElement(QuotationElement quotationElement) {
		quotationElementDAO.save(quotationElement);		
	}
	
	/**
	 * Updates quotation elements and commits changes.
	 * Roll back changes if any exception occurs
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public	void updateQuotationElements(List<QuotationElement> quotationElements) {
		quotationElementDAO.saveAll(quotationElements);	
	}
	
	/**
	 * Finds quotation model and returns quote program description of quotation profile program.
	 * @param	qmdId, qmdId of quotation model
	 */
	public	String  getQuoteProgramDescription(Long qmdId) throws MalBusinessException{
	
		String  quoteProgramDescription = null;
		if(qmdId != null){
			QuotationModel	quotationModel =  quotationModelDAO.findById(qmdId).orElse(null);
			if(quotationModel != null){
				if (quotationModel.getQuotation().getQuotationProfile().getQuoteProfilePrograms()!= null && quotationModel.getQuotation().getQuotationProfile().getQuoteProfilePrograms().size() > 0){
					quoteProgramDescription = quotationModel.getQuotation().getQuotationProfile().getQuoteProfilePrograms().iterator().next().getEtXsmProgram().getProgramDescription();
				}
				
			}else{
				throw new MalBusinessException("service.validation", new String[]{ "Quotation model id is not valid."});
			}
		}else{
			throw new MalBusinessException("service.validation", new String[]{ "Quotation model id must be not null."});
		}
		return quoteProgramDescription;
	}
	
	/**
	 * Returns profitability amount of quotation model configured in database.
	 * @param	qmdId
	 * @param	profitType
	 * @param	profitSource
	 * @return profit amount
	 * @throws MalBusinessException
	 */
	public Double getProfitability(Long qmdID, String profitType, String profitSource) throws MalBusinessException{
		
		if(qmdID == null){
			throw new MalBusinessException("service.validation", new String[]{ " Quotation Model is required."});	
		}
		if(profitType == null){
			throw new MalBusinessException("service.validation", new String[]{ "Profit Type is required."});	
		}
		if(profitSource == null){
			throw new MalBusinessException("service.validation", new String[]{ "Profit Source is required. "});	
		}
		
		return	quotationModelDAO.getProfitability(qmdID, profitType, profitSource);
		
	}
	
	/**
	 * Returns maximum profit amount of quotation model configured in database.
	 * @param	qmdId
	 * @param	profitType
	 * @param	profitSource
	 * @return profit amount
	 * @throws MalBusinessException
	 */
	public Double getMinimumProfit(Long qmdID, String profitType, String profitSource) throws MalBusinessException{
		
		if(qmdID == null){
			throw new MalBusinessException("service.validation", new String[]{ " Quotation Model is required."});	
		}
		if(profitType == null){
			throw new MalBusinessException("service.validation", new String[]{ "Profit Type is required."});	
		}
		if(profitSource == null){
			throw new MalBusinessException("service.validation", new String[]{ "Profit Source is required. "});	
		}
		
		return	quotationModelDAO.getMinimumProfit(qmdID, profitType, profitSource);
		
	}
	
	/**
	 * Returns configured finance parameter value.
	 * @param	parameterKey, configuration name
	 * @param	qmdId, qmdId of quotation model
	 * @param	qprId, quotation profile id
	 * @return	value of finance parameter
	 * @throws	MalBusinessException
	 * 
	 */
	public Double getFinanceParam(String  parameterkey, Long qmdId, Long qprId) throws MalBusinessException{
		return this.getFinanceParam(parameterkey, qmdId, qprId, new Date(), false, null);
	}
	
	/**
	 * Returns configured finance parameter value.
	 * @param	parameterKey, configuration name
	 * @param	qmdId, qmdId of quotation model
	 * @param	qprId, quotation profile id
	 * @param	effectiveDate, effective date for Finance Parameter
	 * @return	value of finance parameter
	 * @throws	MalBusinessException
	 * 
	 */
	public Double getFinanceParam(String  parameterkey, Long qmdId, Long qprId, Date effectiveDate) throws MalBusinessException{
		return this.getFinanceParam(parameterkey, qmdId, qprId, effectiveDate, false, null);
	}
	
	
	/**
	 * Returns configured finance parameter value.
	 * @param	parameterKey, configuration name
	 * @param	qmdId, qmdId of quotation model
	 * @param	qprId, quotation profile id
	 * @param	effectiveDate, effective date for Finance Parameter
	 * @param   skipQuoteLvlCheck, conditionally skip getting information for Quote_Model_Finances
	 * @return	value of finance parameter
	 * @throws	MalBusinessException
	 * 
	 */
	public Double getFinanceParam(String parameterkey, Long qmdId, Long qprId, Date effectiveDate, boolean skipQuoteLvlCheck) throws MalBusinessException {
		return this.getFinanceParam(parameterkey, qmdId, qprId, effectiveDate,skipQuoteLvlCheck,null);
	}
	
	/**
	 * Returns configured finance parameter value.
	 * @param	parameterKey, configuration name
	 * @param	qmdId, qmdId of quotation model
	 * @param	qprId, quotation profile id
	 * @param	effectiveDate, effective date for Finance Parameter
	 * @param   skipQuoteLvlCheck, conditionally skip getting information for Quote_Model_Finances
	 * @param   extAccount, conditionally use a different external account (not the one associated with the qmdId)
	 * @return	value of finance parameter
	 * @throws	MalBusinessException
	 * 
	 */
	@Override
	public Double getFinanceParam(String parameterkey, Long qmdId, Long qprId,
			Date effectiveDate, boolean skipQuoteLvlCheck, ExternalAccount extAcct)
			throws MalBusinessException {

		if(parameterkey == null){
			throw new MalBusinessException("service.validation", new String[]{ " Parameter key is required."});	
		}
		if(qmdId == null){
			throw new MalBusinessException("service.validation", new String[]{ "Quotation Model Id is required."});	
		}
		if(qprId == null){
			throw new MalBusinessException("service.validation", new String[]{ "Quotation Profile Id is required. "});	
		}
		if(MALUtilities.isEmpty(extAcct)){
			return	quotationModelDAO.getFinanceParam(parameterkey, qmdId, qprId, effectiveDate, skipQuoteLvlCheck);
		}else{
			return	quotationModelDAO.getFinanceParam(parameterkey, qmdId, qprId, effectiveDate, skipQuoteLvlCheck, extAcct.getExternalAccountPK().getCId(), extAcct.getExternalAccountPK().getAccountType(), extAcct.getExternalAccountPK().getAccountCode());
		}
		
		
	}	
	

	
	/**
	 * Returns applicable interest rate for quotation model.
	 * @param	qmdId, quotation model id of quotation model
	 * @return interest rate
	 * @throws	MalBusinessException
	 */
	public BigDecimal getInterestRate(Long qmdId) throws MalBusinessException{
		
		QuotationModel quotationModel  = getQuotationModel(qmdId);
		
		return quotationModelDAO.getInterestRate(qmdId, quotationModel.getQuotation().getQuotationProfile().getQprId(), quotationModel.getRevisionDate(), quotationModel.getContractPeriod());
		
	}
	
	/*
	 * This method will return interest rate for supplied interest type and other parameters
	 */
	public BigDecimal getInterestRateByType(Date effectiveDate, Long contractPeriod, String interestType, String reviewFrequency, String fixedOrFloat){
	   return quotationModelDAO.getInterestRateByType(effectiveDate, contractPeriod, interestType, reviewFrequency, fixedOrFloat);
	}
	
	/*
	 * This method will return interest type name applicable for quote
	 */
	public String getInterestType(Long qmdId ,Long lelId, Date revisionDate){ // added lelid for HD-215
	   return quotationModelDAO.getInterestType(qmdId ,lelId, revisionDate);
	}
	
	
	
	
	/**
	 * Return main quotation element of quotation model where element type is Finance and 
	 * there is no dealer and after market id assigned to it.
	 * @param qmdId, quotation model id
	 */
	public QuotationElement getMainQuoteElement(Long qmdId) {
		return quotationElementDAO.findMainQuoteElement(qmdId);
	}
	/**
	 * Returns interest of quotation model based on quotation profile, effective date and contract period.
	 * @param	qmdId, quotation model id of quotation model
	 * @param	qprId, quotation profile id of quotation profile
	 * @param	effective date
	 * @param	contract period
	 * 	
	 */
	public BigDecimal getInterestRate(Long qmdId, Long qprId,Date effectiveDate,Long contractPeriod){
		return quotationModelDAO.getInterestRate(qmdId, qprId,effectiveDate,contractPeriod);
	}

	/**
	 * Returns the global base interest rate base don effective date, contract period, and interest type.
	 * @param	effective date
	 * @param	contract period
	 * @param   interest type, e.g. SWAP, PRIME, etc.
	 * 	
	 */
	public BigDecimal getGlobalInterestRate(Date effectiveDate, Long contractPeriod, String interestType){
		return quotationModelDAO.getGlobalBaseInterestRate(effectiveDate, contractPeriod, interestType);
	}
	
	/**
	 * Retrieves the qmdId of the quote model record that is associated with the   
	 * unit's contract line.
	 *       
	 * @param unitNo Unit Number 
	 * @return QmdId or -1 if one does not exist
	 */
	@Override
	public Long getQmdIdFromUnitNo(String unitNo) {
		Long qmd = -1l;
		FleetMaster fleetMaster  = fleetMasterService.findByUnitNo(unitNo);
		List <QuotationModel> quotationModels;
		if(fleetMaster != null) {
			ContractLine contractLine = contractService.getOriginalContractLine(fleetMaster);
			if(contractLine != null) {
				QuotationModel quotationModel = contractLine.getQuotationModel();
				qmd = quotationModel.getQmdId();
			} else {
				quotationModels = quotationModelDAO.findByUnitNo(unitNo);
				if(quotationModels.size() > 0){
					qmd = quotationModelDAO.getOriginalQuoteModelId(quotationModels.get(0).getQmdId());
				}
			}
		}
		return qmd;
	}
	
	/** 
	 * Get a latest quotation model for given Unit No 
	 * Used in "Vehicle Order Status" 
	 */
	@Override
	public QuotationModel getQuotationModelFromUnitNo(String unitNo) {
		QuotationModel quotationModel = null;
		List <QuotationModel> quotationModels = quotationModelDAO.findByUnitNo(unitNo);
		if(!quotationModels.isEmpty()){
			Collections.sort(quotationModels, new Comparator<QuotationModel>() {
				@Override
				public int compare(QuotationModel o1, QuotationModel o2) {
					return o1.getQuotation().getQuoteDate().compareTo(o2.getQuotation().getQuoteDate()) * -1;
				}});
			
			quotationModel =  quotationModels.get(0);
		}
		return quotationModel;
	}
	
	@Override
	public Long getQmdIdFromFmsId(Long fmsId, Date date){
		return quotationModelDAO.getQmdIdFromFmsId(fmsId, date);
	}

	/**
	 * Returns quotation model along with capital cost elements if exists or throws service error message.
	 * @param	qmdId, quotation model id to look for
	 * @throws MalBusinessException
	 */
	@Override
	public	QuotationModel getQuotationModelWithCapitalCosts(Long qmdId) throws MalBusinessException {
		QuotationModel quotationModel = null;
		if(qmdId != null){
			quotationModel =  quotationModelDAO.findById(qmdId).orElse(null);
			Hibernate.initialize(quotationModel.getQuotationCapitalElements());
			Hibernate.initialize(quotationModel.getQuotationModelFinances());
		}else{
			throw new MalBusinessException("service.validation", new String[]{ "Quotation model id must be not null."});
		}
		return quotationModel;
	}
	
	public QuotationModel getQuotationModelWithCostAndAccessories(Long qmdId)throws MalBusinessException{
		
		QuotationModel quotationModel = null;
		if(qmdId != null){
			quotationModel =  quotationModelDAO.findById(qmdId).orElse(null);
			Hibernate.initialize(quotationModel.getQuotationCapitalElements());
			Hibernate.initialize(quotationModel.getQuotationCapitalElementsBackup());
			Hibernate.initialize(quotationModel.getQuotationDealerAccessories());
			Hibernate.initialize(quotationModel.getQuotationModelAccessories());
			Hibernate.initialize(quotationModel.getQuotationModelFinances());
			Hibernate.initialize(quotationModel.getQuoteModelPropertyValues());
			Hibernate.initialize(quotationModel.getQuotationStepStructure());
		}else{
			throw new MalBusinessException("service.validation", new String[]{ "Quotation model id must be not null."});
		}
		return quotationModel;
	}
	
	/**
	 * Returns MAFS authorization limit based on account code and unit no.
	 * @param	corpId, c_id of external_accounts
	 * @param	accountType, account_type of external_accounts
	 * @param	accountCode, account_code of external_accounts
	 * @param	unitNo, unit_no of fleet_masters
	 */
	public BigDecimal getMafsAuthorizationLimit(Long corpId, String accountType, String accountCode, String unitNo) throws MalBusinessException {
		return quotationModelDAO.getMafsAuthorizationLimit(corpId, accountType, accountCode, unitNo);
	}	
	
	/**
	 * Returns Driver authorization limit based on account code and unit no.
	 * @param	corpId, c_id of external_accounts
	 * @param	accountType, account_type of external_accounts
	 * @param	accountCode, account_code of external_accounts
	 * @param	unitNo, unit_no of fleet_masters
	 */
	public BigDecimal getDriverAuthorizationLimit(Long corpId, String accountType, String accountCode, String unitNo) throws MalBusinessException {
		return quotationModelDAO.getDriverAuthorizationLimit(corpId, accountType, accountCode, unitNo);
	}
	
	public Long	getDealerAcc(Long oldQmdId, Long newQmdId, Long newQdaId){
		return quotationModelDAO.getDealerAcc(oldQmdId, newQmdId, newQdaId);
	}
	
	public BigDecimal	getCustInvPrice(Long qmdId){
		return quotationModelDAO.getCustInvPrice(qmdId);
	}
	
	/**
	 * Returns List<QuotationStepStructure> or throws service error message if quote model is not exist.
	 * @param qmdId, quotation model id to look for
	 * @throws MalBusinessException if qmdId is null
	 */
	
        public List<QuotationStepStructure> getQuotationModelStepStructure(Long qmdId)  {
    
        	List<QuotationStepStructure> quotationStepStructureList = null;
        	quotationStepStructureList = quotationStepStructureDAO.findByQmdId(qmdId);
        
        	return quotationStepStructureList;
        }
	
    	@Transactional(propagation = Propagation.REQUIRED, rollbackFor= Exception.class)
    	public boolean updateVrbDiscountValues(QuotationModel quotationModel) {
    		Boolean	isVrbDiscountChangesFound = false;
    		List<QuotationCapitalElement> qceListToUpdate = new ArrayList<QuotationCapitalElement>();
    		List<QuotationCapitalElement> qceList = quotationCapitalElementDAO
    				.findByQmdID(quotationModel.getQmdId());
    		if (qceList != null) {
    			List<VrbDiscount> vrbDiscountList = vrbDiscountDAO
    					.findByAccountCodeAndModel(quotationModel.getQuotation()
    							.getExternalAccount().getExternalAccountPK()
    							.getAccountCode(), quotationModel.getModel()
    							.getModelId());
    			if (vrbDiscountList != null && !vrbDiscountList.isEmpty()) {
    				for (QuotationCapitalElement qce : qceList) {
    					if(!"Y".equalsIgnoreCase(qce.getQuoteCapital())){
    						continue;
    					}
    					String vrbTypeCodeInQuote = null;
    					CapitalElement capElement = qce.getCapitalElement();
    					//get cap element parameter
    					List<CapEleParameter> capEleParameterList = capEleParameterDAO.findByCelIdAndParameterType(capElement.getCelId(),"C");
    					//get finance parameter
    					if(capEleParameterList == null || capEleParameterList.isEmpty()){
    						continue;
    					}
    					CapEleParameter capEleParameter = capEleParameterList.get(0);
    					//get finance parameter cvalue
    					List<FinanceParameter> financeParameterList = financeParameterDAO.findByParameterKeyAndStatus(capEleParameter.getParameterName(), "A");
    					if(financeParameterList == null || financeParameterList.isEmpty()){
    						continue;
    					}
    					FinanceParameter financeParameter = financeParameterList.get(0);
    					//compare cvalue and vrb discount type code
    					vrbTypeCodeInQuote	= financeParameter.getCvalue();
    					for (VrbDiscount vrbDiscount : vrbDiscountList) {
    						String vrbTypeCode = vrbDiscount.getVrbDiscountTypeCode().getVrbTypeCode();
    						if (vrbTypeCode.equals(vrbTypeCodeInQuote)) {
    							if (vrbDiscount.getEndDate() == null
    									|| !vrbDiscount.getEndDate().before(
    											new Date())) {
    								BigDecimal qceValue =qce.getValue()!= null ? qce.getValue():BigDecimal.ZERO; 
    								BigDecimal vrbValue =vrbDiscount.getDiscountValue()!= null ? vrbDiscount.getDiscountValue().negate():BigDecimal.ZERO;
    								if(qceValue.compareTo(vrbValue) != 0){
    									qce.setValue(vrbDiscount.getDiscountValue().negate());
    									qceListToUpdate.add(qce);
    									isVrbDiscountChangesFound	= true;
    								}
    								
    								break;
    							}

    						}
    					}

    				}
    			}
    		}
    		if(isVrbDiscountChangesFound){
    			quotationCapitalElementDAO.saveAll(qceListToUpdate);
    		}
    		return isVrbDiscountChangesFound;
    	}

	/*
	 * This method will return the doc record for the main PO
	 */
    @Override    	
	public Doc findMainPurchaseOrderByQmdIdAndStatus(Long qmdId, String docStatus) {
	   return docDAO.findMainPurchaseOrderByQmdIdAndStatus(qmdId, docStatus);
	}

	/*
	 * This method will return the delivering dealer for the quote
	 */
    @Override    	
	public ExternalAccount getDeliveringDealer(Long qmdId) {
		Doc doc = docDAO.findMainPurchaseOrderByQmdIdAndStatus(qmdId, "R");
    	if(doc != null) {
    		if (doc.getSubAccCode() != null) {
    			return getDealer(doc.getSubAccCId().longValue(),doc.getSubAccType(), doc.getSubAccCode());
			} else {
				return null;
			}    		
    	}
    	return null;
	}
    
	/*
	 * This method will return the ordering dealer for the quote
	 */
    @Override    	
	public ExternalAccount getOrderingDealer(Long qmdId) {
		Doc doc = docDAO.findMainPurchaseOrderByQmdIdAndStatus(qmdId, "R");
    	if(doc != null) {
    		if (doc.getAccountCode() != null) {
    			return getDealer(doc.getCId().longValue(), doc.getAccountType(), doc.getAccountCode() );
			} else {
				return null;
			}    		
    	}
    	return null;
	}

	private ExternalAccount getDealer(Long cId, String accountType, String accountCode) {
		ExternalAccountPK id = new ExternalAccountPK();
		id.setCId(cId);
		id.setAccountType(accountType);
		id.setAccountCode(accountCode);
		return externalAccountDAO.findById(id).orElse(null);
	}
	
	public Long createRevisedQuote(Long acceptedQmdId, String opCode){	
		
		Long revisedQmd  = quotationModelDAO.getRevisedQmd(acceptedQmdId);
		if(revisedQmd == null ){
			revisedQmd =  quotationModelDAO.createRevisedQuote(acceptedQmdId,  opCode);
		}
		
		return revisedQmd;
	}
		
	public Long getRevisedQmd(long acceptedQmd){		
		return quotationModelDAO.getRevisedQmd(acceptedQmd);
	}
	
	public QuotationModel getAcceptedQuoteFromRevision(QuotationModel revisedQuoteModel) throws MalBusinessException{
		QuotationModel acceptedQuote = null;
		
		for(QuotationModel quoteModel : this.getQuote(revisedQuoteModel.getQuotation().getQuoId()).getQuotationModels()){
			if(quoteModel.getQuoteStatus() == QuotationService.STATUS_ACCEPTED){
				acceptedQuote = quoteModel;
				break;
			}
		}
		return acceptedQuote;
	}

	@Transactional
	public String getApplicableMilageProgram( Long qmdId) {
		QuotationModel	quotationModel = quotationModelDAO.findById(qmdId).orElse(null);
		List<QuoteProfileProgram> qppList = quotationModel.getQuotation().getQuotationProfile().getQuoteProfilePrograms();
		Date quoteDate = quotationModel.getQuotation().getQuoteDate();
		if (qppList != null) {
			QuoteProfileProgram pastQuoteProfileProgram	=	null;
			
			for (QuoteProfileProgram quoteProfileProgram : qppList) {
				Date effectiveFromDate = quoteProfileProgram.getId().getEffectiveFrom();
				int daysDiff = (int) ((quoteDate.getTime() - effectiveFromDate.getTime()) / (1000 * 60 * 60 * 24));
				
				int tempDaysInPast = 0;
				if (daysDiff > 0) {
					// effective from date in past
					if (tempDaysInPast == 0) {
						tempDaysInPast = daysDiff;
						pastQuoteProfileProgram = quoteProfileProgram;
					}
					if (daysDiff < tempDaysInPast) {
						tempDaysInPast = daysDiff; 
						pastQuoteProfileProgram = quoteProfileProgram;
					}
				} else if (daysDiff < 0) {
					// effective from date in future
					/*if (tempDaysInPast == 0) {
						tempDaysInFuture = daysDiff;
					}
					if (daysDiff > tempDaysInFuture){
						tempDaysInFuture = daysDiff;
						futureQuoteProfileProgram = quoteProfileProgram;
					}*/
						
				} else {
					// effective from date on quote date itself
					return quoteProfileProgram.getEtXsmProgram().getProgramDescription();
				}

			}
			if(pastQuoteProfileProgram != null){
				return pastQuoteProfileProgram.getEtXsmProgram().getProgramDescription();
			}
			
		}
		return null;
	}
	
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void saveOrUpdateQuotationDealerAccessory(QuotationDealerAccessory quotationDealerAccessory) throws MalBusinessException, Exception {

		try {
			quotationDealerAccessory.setResidualAmt(getResidualAmount(quotationDealerAccessory));
			quotationDealerAccessoryDAO.saveAndFlush(quotationDealerAccessory);			

			Long qmdId = quotationDealerAccessory.getQuotationModel().getQmdId();
			updateRecalcNeededFlag(qmdId, true);

		} catch(Exception e) {
			logger.error(e);
			if (e instanceof MalBusinessException) {
				throw new MalBusinessException("generic.error.occured.while", new String[] { "saving a quotation dealer accessory" }, e);
			}
			throw e;
			
		}
	}
	
	/*
	 * Delete Quotation dealer Accessory and related Quotation elements from Quote
	 */
	@Transactional(rollbackFor=MalBusinessException.class)
	public void deleteQuotationDealerAccessoryFromQuote(Long qmdId, QuotationDealerAccessory quotationDealerAccessory) throws MalBusinessException, Exception {
		try {
			QuotationModel dbQuotationModel = getQuotationModelWithCostAndAccessories(qmdId);
			
			for(Iterator<QuotationElement> iterator = dbQuotationModel.getQuotationElements().iterator(); iterator.hasNext();) {
				QuotationElement quotationElement = iterator.next();
				if(quotationDealerAccessory.equals(quotationElement.getQuotationDealerAccessory())) {
					iterator.remove();
				}
				
			}
			
			for(Iterator<QuotationDealerAccessory> iterator = dbQuotationModel.getQuotationDealerAccessories().iterator(); iterator.hasNext();) {
				QuotationDealerAccessory quotationDealerAccsry = iterator.next();
				if(quotationDealerAccessory.equals(quotationDealerAccsry)) {
					iterator.remove();
				}
				
			}
			
			dbQuotationModel.setReCalcNeeded("Y");
			quotationModelDAO.save(dbQuotationModel);
		} catch (Exception ex) {
			logger.error(ex);
			if (ex instanceof MalBusinessException) {
				throw new MalBusinessException("generic.error.occured.while", new String[] { "deleting a quotation dealer accessory" }, ex);
			}
			throw ex;
		}
		
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void saveDealerAccessories(List<QuotationDealerAccessory> quotationDealerAccessories) throws MalBusinessException, Exception {

		try {
			for(QuotationDealerAccessory quotationDealerAccessory : quotationDealerAccessories) {
				saveOrUpdateQuotationDealerAccessory(quotationDealerAccessory);
			}
			
		} catch (Exception e) {
			logger.error(e);
			if (e instanceof MalBusinessException) {
				throw new MalBusinessException("generic.error.occured.while", new String[] { "saving a quotation dealer accessory" }, e);
			}
			throw e;			
		}
	}
	
	
	/**
	 * Retrieves/initializes the original quote model from the contract line    
	 * of the new quote model for a formal extension.
	 *       
	 * @param qmdId  
	 * @return QuotationModel 
	 */
	public QuotationModel getOriginalQuoteModelOnContractLine(Long qmdId) throws MalBusinessException{

		QuotationModel quotationModel = null;
		if(qmdId != null){
			quotationModel =  quotationModelDAO.findById(qmdId).orElse(null);
			Hibernate.initialize(quotationModel.getContractLine().getQuotationModel());
		}else{
			throw new MalBusinessException("service.validation", new String[]{ "Quotation Model Id must be not null."});
		}
		return quotationModel;
	}

	/**
	 * Used to safely set the flag for recalculation to "Y" or "N" with no risk of changing anything 
	 * else on the quotation model
	 *       
	 * @param qmdId - qmdId of the quote to set the flag upon
	 * @param boolean flag - true or false 
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor= Exception.class)
	public void updateRecalcNeededFlag(Long qmdId, boolean flag) {
		QuotationModel quotationModel = null;
		quotationModel =  quotationModelDAO.findById(qmdId).orElse(null);
		
		if(flag){
			quotationModel.setReCalcNeeded("Y");
		}else{
			quotationModel.setReCalcNeeded("N");	
		}
		
		quotationModelDAO.save(quotationModel);	
		
	}	
	
	public Long getQuoteAccessoryLeadTimeByQdaId(Long qdaId) {
		Long leadTime = 0L;
		if(qdaId != null ){
			leadTime =  quotationModelDAO.getQuoteAccessoryLeadTimeByQdaId(qdaId);
		}
		
		return leadTime;
	}
	
	public String getClientRequestTypeValue(Long qmdId) {
		return quotationModelDAO.getClientRequestTypeValue(qmdId);
	}
	
	public String acceptQuotation(Long qmdId, String loggedInUser) throws MalBusinessException {
		DbProcParamsVO parameters	= new DbProcParamsVO();
		logger.info("Accept Quote qmdId=" + qmdId + " User=" + loggedInUser );
		parameters = quotationModelDAO.acceptQuote(qmdId, loggedInUser);
		if (parameters.getSuccessTrueFalse().equals("TRUE")) {
			return parameters.getMessage();						
		} else {
			throw new MalBusinessException("plain.message", new String[]{parameters.getMessage()});
		}
	}
	
	@Transactional
	public List<String> getStandardAccessories(Long qmdId) {
		return quotationStandardAccessoryDAO.getStandardAccessoriesDesc(qmdId);
	}

	@Override
	public String getPlateTypeDescription(String plateTypeCode) {
		return quotationModelDAO.getPlateTypeCodeDescription(plateTypeCode);
	}

	@Override
	public String getDriverGradeGroupDescription(String gradeGroupCode) {
		return driverGradeGroupCodeDAO.getDriverGradeGroupDesc(gradeGroupCode);
	}

	@Override
	public List<QuotationSearchVO> searchQuotations(CorporateEntity corporateEntity, String clientString, List<String> productTypeCodes, String unitNo, String vin6, Long projectedMonths, Pageable pageable , Sort sort) throws MalBusinessException {
		return quotationModelDAO.searchQuotations(corporateEntity.getCorpId(), ExternalAccountDAO.ACCOUNT_TYPE_CUSTOMER, clientString, productTypeCodes, unitNo, vin6, projectedMonths, pageable, sort);
	}
	
	@Override
	public int searchQuotationsCount(CorporateEntity corporateEntity, String clientString, List<String> productTypeCodes, String unitNo, String vin6, Long projectedMonths) {
		return quotationModelDAO.searchQuotationsCount(corporateEntity.getCorpId(), ExternalAccountDAO.ACCOUNT_TYPE_CUSTOMER, clientString, productTypeCodes, unitNo, vin6, projectedMonths);
	}
	
	@Override
	public List<ActiveQuoteVO> getAllActiveQuotesByFmsId(Long fmsId) {
		return quotationModelDAO.getActiveQuoteVOs(fmsId);
		
	}
	
	public boolean isQuoteStatusExistsInQmd(String unitNo) {
		List<QuotationModel> quotationModels = null;
		quotationModels = quotationModelDAO.findByUnitNo(unitNo);
		//Check existence of Pending Amendment, Pending Contract Revision, Pending Formal Extensions Quotes
		for (QuotationModel qmd : quotationModels) {
			if (qmd.getQuoteStatus() == QuotationService.STATUS_AMENDED 
				|| qmd.getQuoteStatus() == QuotationService.STATUS_CONTRACT_REVISION 
				|| (qmd.getQuoteStatus() == QuotationService.STATUS_ACCEPTED && !MALUtilities.isEmpty(qmd.getContractLine()))
				|| (qmd.getQuoteStatus() == QuotationService.STATUS_ON_OFFER && !MALUtilities.isEmpty(qmd.getContractLine()))) {
				return true;
			}
			//Pending Informal Amendment
			if (qmd.getQuoteStatus() == QuotationService.STATUS_ON_CONTRACT && quotationModelDAO.hasPendingInformalAmendment(qmd.getQmdId())) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public String getExcessMileBand(Long qmdId) {
		return quotationModelDAO.getExcessMileage(qmdId);
	}

	//TODO: as much as possible this should be in-memory
	@Override
	public Long getFinalizeQmd(long incomingQmdId) {
		
		Long finalizeQMD = null;
		
		QuotationModel quotationModel = quotationModelDAO.findById(incomingQmdId).orElse(null);
		String  unitNo  = quotationModel.getUnitNo();
		if(unitNo != null){
			
			FleetMaster fleetMaster  = fleetMasterService.findByUnitNo(unitNo);
			if(fleetMaster != null) {	
				
				Contract initialContract = null;	
				// below is login to get current quote model contract , which might be original or formal extend	
				List<ContractLine> currentContractLineList = quotationModel.getContractLineList();
				if(currentContractLineList != null && currentContractLineList.size() > 0){
				  initialContract  = quotationModel.getContractLineList().get(0).getContract();
				}
				if(MALUtilities.isEmpty(initialContract )){
					return null;
				}
				// below is  get original quote model of original finalize contract	
				ContractLine contractLine = quotationModel.getContractLine();			
				if (contractLine != null &&  initialContract.getDescription().startsWith(FORMAL_EXT)) {					
							
						List<Contract> contractList = new ArrayList<Contract>() ;
						
						List<ContractLine> contractLineList = fleetMasterDAO.getContractLineList(fleetMaster.getFmsId());
						for(ContractLine cl : contractLineList){
							if(!contractList.contains(cl.getContract()))
								if(cl.getContract().getRevDate().before(initialContract.getRevDate())){
									contractList.add(cl.getContract());
								}
						}
						Collections.sort(contractList, new Comparator<Contract>() { 
							public int compare(Contract c1, Contract c2) { 
								return c2.getRevDate().compareTo(c1.getRevDate()); 
							}
						});
					
						for (Contract contract : contractList) {//loop  through higher revision date  to lower
							if(! contract.getDescription().startsWith(FORMAL_EXT)){
								initialContract = contract; 
								break;
							}
						}
					}
					
					if (initialContract != null) {
						for (ContractLine targetContractLine : initialContract	.getContractLineList()) {
							if (targetContractLine.getRevNo() == 1) {								
								if (targetContractLine.getStartDate() != null) {
									finalizeQMD = targetContractLine.getQuotationModel().getQmdId();
									break;
								}	
							}
						}
	
					}
					
				}
		}
		
		return finalizeQMD;
	}
	
	public boolean isFormalExtension(QuotationModel qm) {
		List<ContractLine> contractLineList = contractLineDAO.findByQmdId(qm.getQmdId());
		if(contractLineList != null && contractLineList.size() > 0) {
			if(contractLineList.get(0).getContract().getDescription().startsWith(FORMAL_EXT)) {
				return true;				
			}
		}
		return false;
	}

	@Override
	public boolean hasFinalizedQuote(long qmdId) {
		if(MALUtilities.isEmpty(this.getFinalizeQmd(qmdId))){
			return false;
		}else{
			return true;
		}
	}
	
	public boolean isFinalizedQuote(long qmdId) {
		Long finalQmdId = this.getFinalizeQmd(qmdId);
		
		if(!MALUtilities.isEmpty(finalQmdId) && finalQmdId.equals(qmdId)){
			return true;
		}else{
			return false;
		}
	}
	
	@Override
	@Transactional
	public void updateQmdProjectedMonth(Long quoId, Long qmdId, Long projectedMonth) throws MalBusinessException{
		quotationModelDAO.updateProjectedMonths(quoId, qmdId, projectedMonth);
	}

	@Override
	public boolean hasPriorAcceptedQuoteModel(long qmdId) {
		return quotationModelDAO.hasPriorAcceptedQuoteModel(qmdId);
	}

	public boolean hasLeaseElementInQuote(QuotationModel quotationModel, String leaseElementName){        
		boolean autoTagPresent = false;          
		for (QuotationElement quotationElement : quotationModel.getQuotationElements()) {
			if(quotationElement.getLeaseElement().getElementName().equalsIgnoreCase(leaseElementName)){
				autoTagPresent = true;
			}
		}

		return  autoTagPresent;           
	}

	public boolean hasLeaseElementInQuote(Long qmdId, String leaseElementName){            
		QuotationModel quotationModel = quotationModelDAO.findById(qmdId).orElse(null);

		return  hasLeaseElementInQuote(quotationModel ,leaseElementName);         
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor= Exception.class)
	public void saveOrUpdateAccountAffiliate(QuotationModel quoteModel,
			ExtAccAffiliate externalAccountAffiliate) throws MalBusinessException {
		QuotationModel quotationModel = null;
		quotationModel =  quotationModelDAO.findById(quoteModel.getQmdId()).orElse(null);
		
		if(MALUtilities.isEmpty(quotationModel)){
			throw new MalBusinessException("service.validation", new String[]{ "Quotation Model Id must be not null."});
		}else{
			quotationModel.setExtAccAffiliate(externalAccountAffiliate);
		}
		
		quotationModelDAO.save(quotationModel);			
	}

	@Override
	public boolean hasBeenSumittedForAcceptance(QuotationModel quoteModel) {
		if(MALUtilities.isNotEmptyString(quoteModel.getRequestForAcceptanceBy()) || (!MALUtilities.isEmpty(quoteModel.getRequestForAcceptanceDate())) || (!MALUtilities.isEmpty(quoteModel.getRequestForAcceptanceYn()) && "Y".equalsIgnoreCase(quoteModel.getRequestForAcceptanceYn()))){
			return true;
		}else{
			return false;
		}
	}
	
	@Override
	@Transactional
	public Long createConractRevisionQuote(Long accptedQmdId, String revisionType, String opUser) throws MalBusinessException {
		
		//Create Contract Revision Quote
		Long contractRevQmdId = quotationModelDAO.createContractRevisionQuote(accptedQmdId, revisionType, opUser);
		logger.debug("Contract Revision QMD_ID = " + contractRevQmdId);
		
//		return quotationModelDAO.findById(contractRevQmdId);
		return contractRevQmdId;
	}
		
	@Override
	public Date getQuoteExpirationDate(Long cId, Date revisionEffectiveDate) {
		Date quoteExpirationDate = timePeriodDAO.findPreviousBillingDate(cId, revisionEffectiveDate);
		return quoteExpirationDate;
	}

	@Override
	public Date getContractEffectiveDate(Long cId) {

		WillowEntityDefault willowEntityDefault = willowEntityDefaultDAO.findByContextId(cId);

		if (!MALUtilities.isEmpty(willowEntityDefault)) {
			TimePeriod timePeriod = timePeriodDAO.findBySequenceNo(willowEntityDefault.getCurrentTpSeqAr().longValue());

			if (!MALUtilities.isEmpty(timePeriod)) {
				Date sysDate = new Date();
				Calendar calender = Calendar.getInstance();
				calender.setTime(timePeriod.getBillingDate());
				/*
				 * If the system date is less than or equal to the Current Billing Date, then the OE contract revision effective date
				 * would be the Start Date (first day of the month) of the FOLLOWING Billing Period.
				 * 
				 * If the system date is greater than the Current Billing Date, then the change is effective for the Start Date 
				 * (first day of the month) of the NEXT FOLLOWING Billing Period.
				 */
				if (sysDate.equals(timePeriod.getBillingDate()) || sysDate.before(timePeriod.getBillingDate())) {
					calender.add(Calendar.MONTH, 1);
				} else {
					calender.add(Calendar.MONTH, 2);
				}
				calender.set(Calendar.DAY_OF_MONTH, calender.getActualMinimum(Calendar.DAY_OF_MONTH));
				return calender.getTime();
			}
		}

		return null;
	}
	
	public List<QuotationStepStructureVO> getCalculateQuotationStepStructure(List<QuotationStepStructure> quotationStepStructureList, QuotationModel quotationModel, 
							BigDecimal depreciationFactor, BigDecimal adminFactor, BigDecimal customerCost) {
		
		List<QuotationStepStructureVO> steps = new ArrayList<QuotationStepStructureVO>();

		int stepSize = quotationStepStructureList.size() == 0 ? 1 : quotationStepStructureList.size();

		BigDecimal totalLeaseRate = BigDecimal.ZERO;
		BigDecimal totalNBV = BigDecimal.ZERO;
		BigDecimal fromPeriod = BigDecimal.ZERO;
		BigDecimal toPeriod = BigDecimal.ZERO;

		for (int i = 0; i < stepSize; i++) {
			totalLeaseRate = BigDecimal.ZERO;
			totalNBV = BigDecimal.ZERO;
			for (QuotationElement qe : quotationModel.getQuotationElements()) {
				if (qe.getQuotationElementSteps() != null) {
					Collections.sort(qe.getQuotationElementSteps(), new QuotationElementStepComparator());
					if (qe.getQuotationElementSteps().size() > i) {
						QuotationElementStep qes = qe.getQuotationElementSteps().get(i);
						if (qes != null) {
							totalLeaseRate = qes.getRentalValue() != null ? totalLeaseRate
									.add(qes.getRentalValue()) : BigDecimal.ZERO;
							totalNBV = totalNBV.add(qes.getEndCapital() != null ? qes.getEndCapital()
									: BigDecimal.ZERO);
							fromPeriod = qes.getFromPeriod();
							toPeriod = qes.getToPeriod();
						}
					}
				}

			}
			totalLeaseRate = CommonCalculations.getRoundedValue(totalLeaseRate,
					RentalCalculationService.CURRENCY_DECIMALS);
			totalNBV = CommonCalculations.getRoundedValue(totalNBV, RentalCalculationService.CURRENCY_DECIMALS);
			QuotationStepStructureVO qssVO = new QuotationStepStructureVO();
			qssVO.setLeaseRate(totalLeaseRate);
			qssVO.setNetBookValue(totalNBV);
			qssVO.setFromPeriod(fromPeriod.longValue());
			qssVO.setToPeriod(toPeriod.longValue());
			qssVO.setAssociatedQmdId(quotationModel.getQmdId());
			qssVO.setLeaseFactor(profitabilityService.getLeaseFactor(qssVO.getLeaseRate(), depreciationFactor, adminFactor, customerCost));
			steps.add(qssVO);

		}
		
		return steps;
	}

	@Override
	public boolean isQuoteEligibleForRevision(Long accptedQmdId, Long fmsId, String revisionType, String opUser) throws MalBusinessException {
		
		String leaseType = getLeaseType(accptedQmdId);
    	if(leaseType.equalsIgnoreCase(CLOSE_END_LEASE)){
    		throw new MalBusinessException("service.validation", new String[]{ "This unit is on a closed end contract and cannot be revised in Vision."});
    	}else if(!leaseType.equalsIgnoreCase(OPEN_END_LEASE)){
    		throw new MalBusinessException("service.validation", new String[]{ "This contract type cannot be revised."});
    	}
		
		String fleetStatus = fleetMasterDAO.getFleetStatus(fmsId);
		if(!fleetStatus.equalsIgnoreCase(VehicleStatus.STATUS_ON_CONTRACT.getCode())){
			throw new MalBusinessException("service.validation", new String[]{ "Unit is not on contract and cannot be revised."});
		}
		
		FleetMaster fleetMaster = fleetMasterDAO.findById(fmsId).orElse(null);
		ContractLine latestContractLine = contractService.getCurrentContractLine(fleetMaster, new Date());
		if(!MALUtilities.isEmpty(latestContractLine)){
			Date endDate = latestContractLine.getEndDate();
			
			if(endDate.before(new Date())){
				throw new MalBusinessException("service.validation", new String[]{ "This unit is on an informal contract and cannot be revised."});
			}
			
			Calendar cal = Calendar.getInstance(); 
			cal.setTime(endDate); 
			cal.add(Calendar.MONTH, -3);
			
			if(cal.getTime().before(new Date())){
				throw new MalBusinessException("service.validation", new String[]{ "This unit is 90 days or less away from its contract end date and cannot be revised."});
			}
		}
		
		
		
		if (!MALUtilities.isEmpty(fmsId)) {
			String checkVehicleStatus = fleetMasterService.checkVehicleStatus(fmsId);
			if (!MALUtilities.isEmpty(checkVehicleStatus)) {
				throw new MalBusinessException("service.validation", new String[]{checkVehicleStatus});
			}
		}
		
		return true;
	}

	public BigDecimal getRevisionInterestAdjustment(QuoteOEVO quoteOEVO, QuoteOEVO revisionOEQuoteVO, Product product, String isOEQuoteRevision) throws MalBusinessException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		BigDecimal revIntAdjustment = new BigDecimal("0.00");
		
		try {
			logger.debug("OpenEndQuoteRevisionBean:getRevisionInterestAdjustment:Qmd Id=" + quoteOEVO.getQuotationModel().getQmdId());
			
			FleetMaster fleetMaster  = fleetMasterService.findByUnitNo(quoteOEVO.getUnitNo());
			ContractLine latestContractLine = contractService.getCurrentContractLine(fleetMaster, new Date());
			String effectiveDate = formatter.format(revisionOEQuoteVO.getQuotationModel().getAmendmentEffectiveDate());
			Calendar stCal = Calendar.getInstance();
			stCal.setTime(latestContractLine.getStartDate());
			Calendar endCal = Calendar.getInstance();
			endCal.setTime(revisionOEQuoteVO.getQuotationModel().getAmendmentEffectiveDate());
			BigDecimal conChangeEventPeriod = new BigDecimal(Math.ceil(monthsBetween(endCal, stCal)));
			
			if(!MALUtilities.isEmpty(latestContractLine)){
				revIntAdjustment = quotationModelDAO.getRevisionInterestAdjustment(
						quoteOEVO.getQuotationModel().getQuotation().getExternalAccount().getExternalAccountPK().getCId(), 
						latestContractLine.getClnId(), 
						conChangeEventPeriod,
						product.getProductType(), 
						quoteOEVO.getQuotationModel().getContractPeriod().intValue(), 
						quoteOEVO.getFinalNBV(), 
						effectiveDate,
						isOEQuoteRevision);
//TODO need to determine what should happen here			
			} else {
				revIntAdjustment = BigDecimal.ZERO;
			}
			logger.debug("OpenEndQuoteRevisionBean:getRevisionInterestAdjustment=" + revIntAdjustment);
		} catch (Exception ex) {
			logger.error(ex);
			throw new MalException("generic.error.occured.while", new String[] { "getting revision interest adjustment" });
		}
		return revIntAdjustment;
	}	
 
	//replicate what is done in fl_contract.mal_actual_months_between in order to include fractional months
	public BigDecimal malActualMonthsBetween(Date inputStartDate, Date inputEndDate){
		BigDecimal monthsBetween = BigDecimal.ZERO; 
		BigDecimal fractionStart = BigDecimal.ZERO;
		BigDecimal fractionLast = BigDecimal.ZERO;
		BigDecimal wholeMonth = BigDecimal.ZERO;
		Date startDate;
		Date endDate;
	  
		Calendar cal = Calendar.getInstance();
		cal.setTime(inputStartDate);
		 
		if(cal.get(Calendar.DATE) == 1){
			fractionStart = BigDecimal.ZERO;
			startDate = inputStartDate;
		}else{
			fractionStart = new BigDecimal(cal.getActualMaximum(Calendar.DATE) - cal.get(Calendar.DATE) + 1).divide(new BigDecimal(cal.getActualMaximum(Calendar.DATE)), 40 , RoundingMode.HALF_UP);
			cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
			cal.add(Calendar.DATE, 1);
			startDate = cal.getTime();
		}
	  
		cal.setTime(inputEndDate);
		if(cal.getActualMaximum(Calendar.DATE) == cal.get(Calendar.DATE)){
			fractionLast = BigDecimal.ZERO;
			cal.add(Calendar.DATE, 1);
			endDate = cal.getTime();
		}else{
			fractionLast = new BigDecimal(cal.get(Calendar.DATE)).divide(new BigDecimal(cal.getActualMaximum(Calendar.DATE)), 40 , RoundingMode.HALF_UP);
			cal.set(Calendar.DATE, cal.getMinimum(Calendar.DATE));
			endDate = cal.getTime();
		}
	  
		Calendar stCal = Calendar.getInstance();
		stCal.setTime(startDate);
		Calendar endCal = Calendar.getInstance();
		endCal.setTime(endDate);
	  
		wholeMonth = new BigDecimal(monthsBetween( endCal, stCal));
	  
		monthsBetween = (fractionStart.add(fractionLast).add(wholeMonth)).setScale(2, RoundingMode.HALF_UP); 
	  
		return monthsBetween;
  	}	
	
			
	@Override
	public Long generateQuoId() {
		Long quoId;
		
		quoId = quotationDAO.generateId();
		
		return quoId;
	}
	
	@Override
	@Transactional
	public Quotation createQuote(FormalQuoteInput formalQuoteInput) {
		Quotation quote;
		ExternalAccountPK clientPK;
		Long quoId;
		
		clientPK = formalQuoteInput.getClient().getExternalAccountPK();
		
		quoId = quotationDAO.createQuotation(clientPK.getCId(), clientPK.getAccountCode(), formalQuoteInput.getProfile().getQprId(), 
				formalQuoteInput.getGradeGroupCode().getDriverGradeGroup(), formalQuoteInput.getTerm(), 
				formalQuoteInput.getDistance(), formalQuoteInput.getCfgId(), formalQuoteInput.getOrderType().getCode(), 
				formalQuoteInput.getUnitNo(), formalQuoteInput.getOdoReading(), formalQuoteInput.getEmployeeNo(), 
				formalQuoteInput.getDesiredQuoId());
		
		quote = quotationDAO.findById(quoId).orElse(null);
		
		return quote;
	}

	@Override
	public String getWillowReportUrl(List<WillowReportParamVO> params) throws MalBusinessException {
		String paramString = "";
		String moduleName = null;

		if (!MALUtilities.isEmpty(params)) {
			for (WillowReportParamVO parameter : params) {
				if (!MALUtilities.isEmpty(parameter.getParamName()) && !MALUtilities.isEmpty(parameter.getParamValue())) {
					paramString = paramString + "+" + parameter.getParamName() + "=" + parameter.getParamValue();
					// Get Module Name i.e. RDF file name
					if (parameter.getParamName().equalsIgnoreCase("MODULE") && MALUtilities.isEmpty(moduleName)) {
						moduleName = parameter.getParamValue();
					}
				}
			}
		}

		if (!MALUtilities.isEmpty(moduleName)) {
			return quotationModelDAO.generateWillowReportUrl(moduleName, paramString);			
		} else {
			return null;
		}		
	}

	@Override
	public byte[] getVLOReport(QuotationModel quotationModel) throws MalBusinessException {
		
		List<WillowReportParamVO> params = new ArrayList<WillowReportParamVO>();
		byte[] buffer = null;
		
		if(!MALUtilities.isEmpty(quotationModel)){
			
			Report report = reportDAO.findByReportName(quotationModel.getQuotation().getQuotationProfile().getReportName3());
			if(!MALUtilities.isEmpty(report)){
				String reportName = quotationModelDAO.getReportNameByModuleAndProductCode(report.getReportExec(), quotationModel.getQuotation().getQuotationProfile().getPrdProductCode());
				
				if(!MALUtilities.isEmpty(reportName)){
					params.add(new WillowReportParamVO("QUOID", String.valueOf(quotationModel.getQuotation().getQuoId())));
					params.add(new WillowReportParamVO("QMDID", String.valueOf(quotationModel.getQmdId())));
					params.add(new WillowReportParamVO("CID", String.valueOf(quotationModel.getQuotation().getExternalAccount().getExternalAccountPK().getCId())));
	//				params.add(new WillowReportParamVO("DEALER_DISC_PERC", " "));
					params.add(new WillowReportParamVO("QPRID", String.valueOf(quotationModel.getQuotation().getQuotationProfile().getQprId())));
					
					params.add(new WillowReportParamVO("REPORT_NAME", reportName));
					params.add(new WillowReportParamVO("EFFECT_FROM_DATE", new SimpleDateFormat("dd-MMM-yyyy").format(new Date())));
					
					params.add(new WillowReportParamVO("MODULE", report.getReportExec()));
					
					String reportURL = getWillowReportUrl(params);
					if(!MALUtilities.isEmpty(reportURL)){
						buffer = readFileToBuffer(reportURL);					
						saveOrUpdateVLOPrintIndicator(quotationModel);						
					}
				}
			}
		}
	    return buffer;
	}
	
	@Transactional
	@Override
	public byte[] getVQReport(QuotationModel quotationModel) throws MalBusinessException {
		
		List<WillowReportParamVO> params = new ArrayList<WillowReportParamVO>();
		byte[] buffer = null;
		
		if(!MALUtilities.isEmpty(quotationModel)){
			Report report = reportDAO.findByReportName(quotationModel.getQuotation().getQuotationProfile().getReportName());
			
			if(!MALUtilities.isEmpty(report)){
				String reportName = quotationModelDAO.getReportNameByModuleAndProductCode(report.getReportExec(), quotationModel.getQuotation().getQuotationProfile().getPrdProductCode());
				
				if(!MALUtilities.isEmpty(reportName)){
					params.add(new WillowReportParamVO("QUOID", String.valueOf(quotationModel.getQuotation().getQuoId())));
					params.add(new WillowReportParamVO("QMDID", String.valueOf(quotationModel.getQmdId())));
					params.add(new WillowReportParamVO("CID", String.valueOf(quotationModel.getQuotation().getExternalAccount().getExternalAccountPK().getCId())));
			//		params.add(new WillowReportParamVO("DEALER_DISC_PERC", ""));
					params.add(new WillowReportParamVO("QPRID", String.valueOf(quotationModel.getQuotation().getQuotationProfile().getQprId())));
					
					params.add(new WillowReportParamVO("REPORT_NAME", reportName));
					params.add(new WillowReportParamVO("EFFECT_FROM_DATE", new SimpleDateFormat("dd-MMM-yyyy").format(new Date())));
					
					params.add(new WillowReportParamVO("MODULE", report.getReportExec()));
					
					String reportURL = getWillowReportUrl(params);
					if(!MALUtilities.isEmpty(reportURL)){
						buffer = readFileToBuffer(reportURL);
						saveOrUpdateVQPrintIndicator(quotationModel);						
					}
				}
			}
		}
	    return buffer;
	}

	private byte[] readFileToBuffer(String reportURL) {
		byte[] buffer = null;
		try{
			URL u = new URL(reportURL);
	        URLConnection conn = u.openConnection();
	        conn.setConnectTimeout(TIME_OUT);
	        conn.setReadTimeout(TIME_OUT);
	        conn.connect(); 
	
	        InputStream is = conn.getInputStream();
	        ByteArrayOutputStream os = new ByteArrayOutputStream();
	        int b;
	        while ((b = is.read()) != -1)
	            os.write(b);
	        
	        buffer = os.toByteArray();
		}
		catch(Exception ex){
			logger.error(ex);
			throw new MalException(ex.getMessage());
		}
		return buffer;
	}

	@Override
	public byte[] getVQSummaryReport(List<QuotationModel> quotationModelList) throws MalBusinessException {
		
		List<WillowReportParamVO> params = new ArrayList<WillowReportParamVO>();
		byte[] buffer = null;
		
		if(!MALUtilities.isEmpty(quotationModelList) && quotationModelList.size() > 0){
			String moduleName = "FLQMR540";
			
			String reportName = quotationModelDAO.getReportNameByModuleAndProductCode(moduleName, quotationModelList.get(0).getQuotation().getQuotationProfile().getPrdProductCode());
			
			if(!MALUtilities.isEmpty(reportName)){
				long runId = reportsSpoolService.generateId();
				
				saveReportSpool(quotationModelList, moduleName, runId);
				
				params.add(new WillowReportParamVO("QPRID", String.valueOf(quotationModelList.get(0).getQuotation().getQuotationProfile().getQprId())));
				params.add(new WillowReportParamVO("REPORT_NAME", reportName));
				params.add(new WillowReportParamVO("RUN_ID", String.valueOf(runId)));
				params.add(new WillowReportParamVO("QMDID", ""));
				params.add(new WillowReportParamVO("EFFECT_FROM_DATE", new SimpleDateFormat("dd-MMM-yyyy").format(quotationModelList.get(0).getQuotation().getQuotationProfile().getEffectiveFrom())));
				
				params.add(new WillowReportParamVO("MODULE", moduleName));
				
				String reportURL = getWillowReportUrl(params);
				if(!MALUtilities.isEmpty(reportURL)){
					buffer = readFileToBuffer(reportURL);
				}
			}
		}
	    return buffer;
	}


	private void saveReportSpool(List<QuotationModel> quotationModelList, String moduleName, long runId) throws MalBusinessException {
		List<ReportsSpool> reportsSpoolList = new ArrayList<ReportsSpool>();
		
		ReportsSpool reportSpool = null;
		for(int i = 1; i <= quotationModelList.size(); i++){
			reportSpool = new ReportsSpool();
			reportSpool.setReportsSpoolPK(new ReportsSpoolPK(runId, i));
			reportSpool.setExternalAccount(quotationModelList.get(i-1).getQuotation().getExternalAccount());
			reportSpool.setQmdId(quotationModelList.get(i - 1).getQmdId());
			reportSpool.setDeliveryMethod("P");
			reportSpool.setModuleName(moduleName);
			
			reportsSpoolList.add(reportSpool);
		}
		
		if(reportsSpoolList.size() > 0){
			reportsSpoolService.addReportsSpool(reportsSpoolList);
		}
	}

	@Override
	public List<String> validateVehicleQuotationBeforePrinting(QuotationModel quotationModel) throws MalBusinessException {
		double monthsDiff = 0; 
		List<String> validationErrorMessages = new ArrayList<String>();
		boolean hasMaintTypeElement = false;
		BigDecimal lastOdoReading;
		
		
		if(quotationModel.getQuotation().getQuotationProfile().getPrdProductCode().equals(SHORT_TERM_PRODUCT_CODE)
				|| quotationModel.getQuotation().getQuotationProfile().getPrdProductCode().equals(MAX_FLEET_SERVICES)
				|| quotationModel.getQuotation().getQuotationProfile().getPrdProductCode().equals(DEMO_FLEET_SERVICES)){
			
			validationErrorMessages.add("VQ/VLO cannot be generated for this product code. Please submit a task to the appropriate team/person for completion.");
			return validationErrorMessages;
		}
		
		List<QuotationDealerAccessory> qdaList = getQuotationDealerAccessoriesByQmdId(quotationModel.getQmdId());
		long leadTime = 0;
		for(QuotationDealerAccessory qda :  qdaList){
			if(qda.getDealerAccessory() != null){
				leadTime = leadTime + getQuoteAccessoryLeadTimeByQdaId(qda.getQdaId());
			}
		}
		
		if(leadTime > 0) {
			validationErrorMessages.add("Upfit exists on the vehicle being quoted, therefore VQ cannot be printed. Please submit a task to the appropriate team/person for completion.");
			return validationErrorMessages;
		}
		
		boolean hasFinanceElements = false;
		List<String> serviceElements = new ArrayList<>();
		for(QuotationElement qe : quotationModel.getQuotationElements()){
			if(!MALUtilities.convertYNToBoolean(qe.getLeaseElement().getPrintVqInd()) ){
				serviceElements.add(qe.getLeaseElement().getElementName());
			}
			
			if(!hasMaintTypeElement && qe.getLeaseElement().getElementType().equalsIgnoreCase("MAINT")){
				hasMaintTypeElement = true;
			}
			
			if(qe.getLeaseElement().getElementType().equals(MalConstants.FINANCE_ELEMENT)){
				hasFinanceElements = true;
			}
		}
		
		if(!serviceElements.isEmpty()) {
			validationErrorMessages.add("Additional action is required due to Service Element(s) " + serviceElements.toString() +  
					" Please submit a task to the appropriate team/person for completion. Once complete you will receive a new e-mail containing the requested VQ.");
		}
		
		
		if(!MALUtilities.convertYNToBoolean(quotationModel.getUsedVehicle()) && hasMaintTypeElement){
			validationErrorMessages.clear();
		}
		
		if(!hasFinanceElements){
			validationErrorMessages.add("The Quote does not have any finance elements associated with it, therefore VQ cannot be generated. Please submit a task to the appropriate team/person for completion.");
		}
		return validationErrorMessages;
	}
	
	/**
	 * Retrieves the quotation models for the passed in quote
	 * 
	 * @param quotation 
	 * @return List of quotation models
	 */
	public List<QuotationModel> getQuotationModels(Quotation quotation) {
		List<QuotationModel> qmds;
		qmds = quotationModelDAO.findByQuoteId(quotation.getQuoId());
		return qmds;
	}
	
	/**
	 * When VQ has been printed for the first time, as a business rule the print
	 * indicator flags on the quote need to be set
	 * 
	 * @param qmd Quotation Model
	 * @return Persisted Quotation Model with the print indicator flags set
	 */
	private QuotationModel saveOrUpdateVQPrintIndicator(QuotationModel qmd){
		QuotationModel dbQmd;
		
		dbQmd = quotationModelDAO.findById(qmd.getQmdId()).orElse(null);
		
		if(MALUtilities.isEmpty(dbQmd.getPrintedInd()) || dbQmd.getPrintedInd().equals(MalConstants.FLAG_N)) {
			dbQmd.setPrintedDate(new Date());
			dbQmd.setPrintedInd(MalConstants.FLAG_Y);			
			dbQmd = quotationModelDAO.save(dbQmd);
		}
				
		return dbQmd;
		
	}
	
	/**
	 * When VLO has been printed for the first time, as a business rule the print
	 * indicator flags on the quote need to be set
	 * 
	 * @param qmd Quotation Model
	 * @return Persisted Quotation Model with the print indicator flags set
	 */
	private QuotationModel saveOrUpdateVLOPrintIndicator(QuotationModel qmd){
		QuotationModel dbQmd;
		
		dbQmd = quotationModelDAO.findById(qmd.getQmdId()).orElse(null);
		
		if(MALUtilities.isEmpty(dbQmd.getVloPrintedDate())) {
			dbQmd.setVloPrintedDate(new Date());			
			dbQmd = quotationModelDAO.save(dbQmd);
		}
				
		return dbQmd;
		
	}	
	
	public double monthsBetween(Calendar date1, Calendar date2){
        double monthsBetween = 0;
        //difference in month for years
        monthsBetween = (date1.get(Calendar.YEAR)-date2.get(Calendar.YEAR))*12;
        //difference in month for months
        monthsBetween += date1.get(Calendar.MONTH)-date2.get(Calendar.MONTH);
        //difference in month for days
        monthsBetween += ((date1.get(Calendar.DAY_OF_MONTH)-date2.get(Calendar.DAY_OF_MONTH))/31d);

        return monthsBetween;
    }

	@Override
	public BigDecimal getInterestRateByQuotationProfile(Long qprId, Date effectiveDate, Long term) throws MalBusinessException {

		return quotationModelDAO.getInterestRateByQuotationProfile(qprId, effectiveDate, term);
	}
	
	@Override
	public Long generateCfgId() {
		Long cfgId;
		
		cfgId = quotationModelDAO.generateCfgId();
		
		return cfgId;
	}

	@Override
	public List<QuotationDealerAccessory> getQuotationDealerAccessoriesByQmdId(Long qmdId) {
		
		return quotationDealerAccessoryDAO.findByQmdId(qmdId);
	}

	@Override
	public long getCountOfTermAndMiles(Long distance, Long hpdDistId) {

		return hirePeriodDistanceDAO.getCountOfTermAndMiles(distance, hpdDistId);
	}

	@Override
	public List<String> validateVehicleQuotationBeforePrinting(FormalQuoteInput formalQuoteInput)
			throws MalBusinessException {
		
		BigDecimal calcMonthlyPayment  = BigDecimal.ZERO, adminFee, disposalFee, delta, tolerance  = new BigDecimal("5.00");
		String key;		
		Double value;		
		BigDecimal dbLeaseRate = BigDecimal.ZERO;
		List<String> validationErrorMessages = new ArrayList<String>();
		String leaseType = quotationProfileService.getProductTypeByProfile(formalQuoteInput.getProfile().getQprId());
		
		// TODO: Assuming the quote created via IQMT will be quoid/1/1 
		Long qmdId = quotationModelDAO.findQmdIdByQuoteNumber(formalQuoteInput.getDesiredQuoId(), 1l, 1l);
		QuotationModel quotationModel = quotationModelDAO.findById(qmdId).orElse(null);
		
		if(!MALUtilities.isEmpty(quotationModel)){
			if(!leaseType.equalsIgnoreCase("OE")){
				if(formalQuoteInput instanceof FormalClosedEndQuoteInputVO){
					FormalClosedEndQuoteInputVO vo = (FormalClosedEndQuoteInputVO)formalQuoteInput;
					
					try {	
						adminFee = vo.getAdminFee() != null ? vo.getAdminFee() : BigDecimal.ZERO;		
						disposalFee = vo.getDisposalFee() != null ? vo.getDisposalFee() : BigDecimal.ZERO;
						
						calcMonthlyPayment = BigDecimal.valueOf(CommonCalculations.calculateRentalfromIRR(formalQuoteInput.getIrr().doubleValue(), 
								formalQuoteInput.getTerm().intValue(), formalQuoteInput.getClientCapCost().doubleValue(), adminFee.doubleValue(), 
								vo.getResidual().doubleValue(), disposalFee.doubleValue()));
						
						calcMonthlyPayment = CommonCalculations.getRoundedValue(calcMonthlyPayment , RentalCalculationService.CURRENCY_DECIMALS);	
						
						for(QuotationElement qe : quotationModel.getQuotationElements()){
							if(qe.getLeaseElement().getElementType().equalsIgnoreCase("FINANCE")){
								for(QuotationElementStep step : qe.getQuotationElementSteps()){
									dbLeaseRate = dbLeaseRate.add(step.getRentalValue());
								}
							}
						}
					} catch(Exception e) {
						validationErrorMessages.add(e.getMessage());			
					}
					
					delta = calcMonthlyPayment.subtract(dbLeaseRate);
					if(delta.compareTo(tolerance) > 0 || delta.compareTo(tolerance.negate()) < 0 ) {
						validationErrorMessages.add("Incorrect lease rate detected. Expected " + calcMonthlyPayment + " but received " + dbLeaseRate);
					}
				}
			}else{
				
				if(formalQuoteInput instanceof FormalOpenEndQuoteInputVO){
					FormalOpenEndQuoteInputVO vo = (FormalOpenEndQuoteInputVO)formalQuoteInput;
					
					adminFee = vo.getAdminFactor() != null ? new BigDecimal(vo.getAdminFactor().doubleValue() * formalQuoteInput.getClientCapCost().doubleValue()/1000 ): BigDecimal.ZERO;
		
					Map<Integer, BigDecimal> calculatedMonthlyRental = new HashMap<Integer, BigDecimal>();
					Map<Integer, BigDecimal> dbMonthlyRental = new HashMap<Integer, BigDecimal>();
					BigDecimal calculateRentalfromIRRValue;
					BigDecimal startCapital = formalQuoteInput.getClientCapCost();
					BigDecimal convertedDepreciationFactor = vo.getDepreciationFactor().movePointLeft(2).setScale(DEPRECIATION_FACTOR_SCALE, RoundingMode.HALF_UP); 
					
					
					int stepEndPeriod = 0;
					for(FormalQuoteStepInputVO stepVO : vo.getSteps()){
						stepEndPeriod = stepEndPeriod + stepVO.getNumberOfMonths();
						
						//TODO NBV is not right here
						//BigDecimal nbv = profitabilityService.getFinalNBV(startCapital, vo.getDepreciationFactor(), new BigDecimal(stepEndPeriod));
						
						//TODO: Need to centralize this formula. It is different than the finalNBV.
						BigDecimal nbv = startCapital.subtract((formalQuoteInput.getClientCapCost().multiply(convertedDepreciationFactor, CommonCalculations.MC).multiply(new BigDecimal(stepVO.getNumberOfMonths()), CommonCalculations.MC)));
						nbv = nbv.setScale(2, BigDecimal.ROUND_HALF_UP);							
						
						calculateRentalfromIRRValue = BigDecimal.valueOf(CommonCalculations.calculateRentalfromIRR(vo.calucateInterestRate().doubleValue(), stepVO.getNumberOfMonths(),
								startCapital.doubleValue(), adminFee.doubleValue(), nbv.doubleValue(), BigDecimal.ZERO.doubleValue()));
						
						calculateRentalfromIRRValue = CommonCalculations.getRoundedValue(calculateRentalfromIRRValue, RentalCalculationService.CURRENCY_DECIMALS);
						
						startCapital = nbv;
						
						calculatedMonthlyRental.put(stepEndPeriod, calculateRentalfromIRRValue);
					}
					
					List<QuotationElementStep> qelSteps = new ArrayList<>();
					for (QuotationElement quotationElement : quotationModel.getQuotationElements()) {
						if (quotationElement.getLeaseElement().getElementType().equals(MalConstants.FINANCE_ELEMENT)
//								&& quotationElement.getQuotationDealerAccessory() == null && quotationElement.getQuotationModelAccessory() == null
								&& MalConstants.FLAG_Y.equals(quotationElement.getIncludeYn())) {
							qelSteps.addAll(quotationElement.getQuotationElementSteps());
						}
					}
					
					for(QuotationElementStep qelStep : qelSteps){
						BigDecimal rentalValue = qelStep.getRentalValue();
						
						if(dbMonthlyRental.containsKey(qelStep.getToPeriod().intValue())) {
							rentalValue = rentalValue.add(dbMonthlyRental.get(qelStep.getToPeriod().intValue()));
						}
						
						dbMonthlyRental.put(qelStep.getToPeriod().intValue(), rentalValue);
					}
					
					for (Map.Entry<Integer, BigDecimal> entry : calculatedMonthlyRental.entrySet()) {
						if(dbMonthlyRental.containsKey(entry.getKey())){
							
							if( entry.getValue().subtract(dbMonthlyRental.get(entry.getKey())).compareTo(tolerance) > 0 
									|| entry.getValue().subtract(dbMonthlyRental.get(entry.getKey())).compareTo(tolerance.negate()) < 0){
								validationErrorMessages.add("Incorrect lease rate detected. Expected " + entry.getValue() + " but received " + dbMonthlyRental.get(entry.getKey()));
							}
						}else{
							validationErrorMessages.add("Steps do not match");
							return validationErrorMessages;
						}
					}
				}
			}
			
		}else{
			validationErrorMessages.add("Unable to find quote in Willow");
		}
		return validationErrorMessages;
	}
	
	
	public List<RevisionVO> getQuotationModelsByQuoteStatus(Long qmdId, List<String> quoteStatusCodes) {
		return quotationModelDAO.getListByQmdAndStatus(qmdId, quoteStatusCodes);
	}
	
	public EarlyTermQuote getEarlyTermQuote(Long etqId) throws MalBusinessException {
		
		EarlyTermQuote etQuote = null;
		if(etqId != null){
			etQuote =  earlyTermQuoteDAO.findById(etqId).orElse(null);
		}else{
			throw new MalBusinessException("service.validation", new String[]{ "ETQ id must be not null."});
		}
		return etQuote;
	}	
	
	public List<EarlyTermQuote> getUnacceptedEtQuotesByFmsId(Long fmsId){
		return earlyTermQuoteDAO.findUnacceptedEtQuotesByFmsId(fmsId);
	}
	
	/**
	 * Updates early term quote and commits changes.
	 * Roll back changes if any exception occurs
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateEarlyTermQuote(EarlyTermQuote etQuote) {
		earlyTermQuoteDAO.save(etQuote);		
	}		
	
	public BigDecimal getBaseRate(QuotationModel quotationModel) throws MalException {
		BigDecimal baseIRR = new BigDecimal("0.00");
		try {
			logger.debug("QuotationService:getIRR:Qmd Id=" + quotationModel.getQmdId());
			baseIRR = quotationModelDAO.getBaseRate(quotationModel.getQmdId());
			logger.debug("QuotationService:baseIRR:baseIRR=" + baseIRR);
		} catch (Exception ex) {
			logger.error(ex);
			throw new MalException("generic.error.occured.while", new String[] { "getting Base IRR" });
		}
		return baseIRR;
	}

	@Override
	public BigDecimal getProfileBaseRateByTerm(QuotationModel quotationModel, long term)
			throws MalException {
		BigDecimal baseIRR = new BigDecimal("0.00");
		try {
			baseIRR = quotationModelDAO.getProfileBaseRateByTerm(quotationModel.getQmdId(), term);
		} catch (Exception ex) {
			logger.error(ex);
			throw new MalException(ex.getMessage());
		}
		return baseIRR;
	}

	@Override
	public List<QuoteRejectCode> getAllQuoteRejectReasons() {
		return quoteRejectCodeDAO.getQuoteRejectReasons();
	}

	@Override
	public void rejectQuoteWithReason(long qmdId, String rejectReason) throws MalBusinessException {
			QuotationModel qmd = quotationModelDAO.findById(qmdId).orElse(null);
			qmd.setRejectReason(rejectReason);
			qmd.setQuoteStatus(STATUS_REJECTED);
			updateQuotationModel(qmd);
		
	}
	

	/*
	 *  This method provide Step Structure applicable for incoming quote by looking it previous revise contract .
	 *  The step and term in revised quote may changed.This function should return steps starting from 1 to n-1 where n is  contract change event period. 
	 */

	public List<QuotationStepStructureVO> getAllQuoteSteps(Long inComingQmd) throws MalBusinessException {
		
		List<Long> quoteForStepDisplay = quotationModelDAO.getPreviousQMDForSteps(inComingQmd);// this will give us in descending order based on rev no
		
		if(quoteForStepDisplay == null || quoteForStepDisplay.isEmpty()){
			quoteForStepDisplay.add(inComingQmd);
			
		}
	
		List<QuotationStepStructureVO> finalizeStepVO = new ArrayList<QuotationStepStructureVO>();			
	
		for(Long qmdId : quoteForStepDisplay){
			QuotationModel  quotationModel  = getQuotationModel(qmdId);
			BigDecimal depreciationFactor = quotationModel.getDepreciationFactor();
			BigDecimal customerCost = rentalCalculationService.getCapitalCostAmount(quotationModel);			
			BigDecimal adminFactor = BigDecimal.valueOf(getFinanceParam(MalConstants.FIN_PARAM_ADMIN_FACT,
					quotationModel.getQmdId(), quotationModel.getQuotation().getQuotationProfile().getQprId()));
			if(depreciationFactor == null){// this should not happen,  may be very old quote				
				BigDecimal finalResidual = BigDecimal.valueOf(profitabilityService.getResidualAmount(quotationModel));				
				depreciationFactor = profitabilityService.getDepreciationFactor(customerCost, finalResidual , new BigDecimal(quotationModel.getContractPeriod()));
				depreciationFactor = depreciationFactor.movePointRight(2);
				depreciationFactor = depreciationFactor.setScale(7, RoundingMode.HALF_UP);
			}
				
			List<QuotationStepStructure> quotationStepStructureList = quotationModel.getQuotationStepStructure();
			List<QuotationStepStructureVO> qssVOs  = getCalculateQuotationStepStructure(quotationStepStructureList,  quotationModel,  depreciationFactor,  adminFactor,  customerCost);
			if(finalizeStepVO.isEmpty()){				
				finalizeStepVO = qssVOs;
			}else{
				int addPos = 0;
				long prevQmdFromPeriod = finalizeStepVO.get(0).getFromPeriod();		
				for (QuotationStepStructureVO quotationStepStructureVO : qssVOs) {					
					if(quotationStepStructureVO.getFromPeriod() < prevQmdFromPeriod ){//will be true for OE revision case only
						if(prevQmdFromPeriod <= quotationStepStructureVO.getToPeriod()){
							quotationStepStructureVO.setToPeriod(prevQmdFromPeriod -1);		
							BigDecimal periods =  new BigDecimal(quotationStepStructureVO.getToPeriod() - qssVOs.get(0).getFromPeriod() + 1);
							quotationStepStructureVO.setNetBookValue(profitabilityService.getFinalNBV(customerCost, depreciationFactor, periods));
						}
						
						finalizeStepVO.add(addPos++ ,quotationStepStructureVO);
					}
				}
			}	
		}		

		return finalizeStepVO;
	}
	
	public BigDecimal getApplicableCapitalContribution(Long qmdId){
		return  quotationModelDAO.getApplicableCapitalContribution(qmdId);
	}	

	public List<CnbvVO> getCnbv(Long qmdId) {
		return quotationModelDAO.getCnbv(qmdId, null);
	}

	public QuotationModel changeProfileOnQuotationModels(QuotationModel quotationModel) {

		QuoteModelPropertyValue qmPropertyValue = quoteModelPropertyValueService.findByNameAndQmdId(QuoteModelPropertyEnum.QUOTE_PROFILE.getName(), quotationModel.getQmdId());
		
		if (!MALUtilities.isEmpty(qmPropertyValue)) {
			quotationModel.getQuotation().setQuotationProfile(quotationProfileDAO.findById(Long.valueOf(qmPropertyValue.getPropertyValue())).orElse(null));
		}
		
		return quotationModel;
	}

	@Override
	public List<CnbvVO> getCnbvForQuotationModel(Long LastestQmdId, Long anyOldQmdId) {
		return quotationModelDAO.getCnbv(LastestQmdId, anyOldQmdId);
	}
	
	
	public BigDecimal getResidualAmount(QuotationDealerAccessory quotationDealerAccessory) throws Exception {

		BigDecimal amt = BigDecimal.ZERO;
		
		List<ResidualTable> list = residualTableDAO.getActiveResidualTables(
				quotationDealerAccessory.getQuotationModel().getQuotation().getQuotationProfile().getTableCode1(),
				"L", new BigDecimal(quotationDealerAccessory.getQuotationModel().getModel().getModelId()), 
				new BigDecimal(quotationDealerAccessory.getDealerAccessory().getDacId()), null, null);

		if(list != null && list.size() > 0) {
			amt = quotationModelDAO.getResidualAmount(list.get(0).getRtbId(), quotationDealerAccessory.getQuotationModel().getContractPeriod(), 
					quotationDealerAccessory.getQuotationModel().getContractDistance());
		}
		return amt;		
	}

}
