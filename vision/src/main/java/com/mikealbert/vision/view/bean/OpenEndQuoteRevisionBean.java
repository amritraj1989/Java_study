package com.mikealbert.vision.view.bean;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.primefaces.context.RequestContext;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.mikealbert.common.CommonCalculations;
import com.mikealbert.common.MalConstants;
import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.dao.LeaseElementDAO;
import com.mikealbert.data.dao.ProductDAO;
import com.mikealbert.data.dao.QuotationCapitalElementDAO;
import com.mikealbert.data.dao.QuotationProfileDAO;
import com.mikealbert.data.dao.UserContextDAO;
import com.mikealbert.data.entity.ContractLine;
import com.mikealbert.data.entity.DriverAddress;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.LeaseElement;
import com.mikealbert.data.entity.ProcessStageObject;
import com.mikealbert.data.entity.Product;
import com.mikealbert.data.entity.QuotationCapitalElement;
import com.mikealbert.data.entity.QuotationElement;
import com.mikealbert.data.entity.QuotationElementStep;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.QuotationModelFinances;
import com.mikealbert.data.entity.QuotationProfitability;
import com.mikealbert.data.entity.QuoteModelProperty;
import com.mikealbert.data.entity.QuoteModelPropertyValue;
import com.mikealbert.data.entity.QuoteRejectCode;
import com.mikealbert.data.entity.User;
import com.mikealbert.data.entity.WorkClassAuthority;
import com.mikealbert.data.enumeration.OrderToDeliveryProcessStageEnum;
import com.mikealbert.data.enumeration.QuoteModelPropertyEnum;
import com.mikealbert.data.util.DisplayFormatHelper;
import com.mikealbert.data.vo.QuotationStepStructureVO;
import com.mikealbert.data.vo.QuoteElementStepVO;
import com.mikealbert.data.vo.QuoteOEVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.CapitalCostService;
import com.mikealbert.service.CapitalElementService;
import com.mikealbert.service.ContractService;
import com.mikealbert.service.DriverService;
import com.mikealbert.service.FinanceParameterService;
import com.mikealbert.service.FleetMasterService;
import com.mikealbert.service.OpenEndService;
import com.mikealbert.service.ProcessStageService;
import com.mikealbert.service.ProfitabilityService;
import com.mikealbert.service.QuotationElementService;
import com.mikealbert.service.QuotationService;
import com.mikealbert.service.QuoteModelPropertyValueService;
import com.mikealbert.service.RentalCalculationService;
import com.mikealbert.service.ServiceElementService;
import com.mikealbert.service.reporting.JasperReportService;
import com.mikealbert.service.util.email.Email;
import com.mikealbert.service.util.email.EmailAddress;
import com.mikealbert.service.vo.QuoteCost;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.service.HelpDeskEmailService;
import com.mikealbert.vision.view.ViewConstants;

import net.sf.jasperreports.engine.JasperPrint;

@Component
@Scope("view")
public class OpenEndQuoteRevisionBean extends StatefulBaseBean {

	private static final long serialVersionUID = 2437933046906999010L;
	private MalLogger logger = MalLoggerFactory.getLogger(this.getClass());

	@Resource QuotationService quotationService;
	@Resource ProductDAO productDAO;
	@Resource RentalCalculationService rentalCalculationService;
	@Resource ServiceElementService serviceElementService;
	@Resource ProfitabilityService profitabilityService;
	@Resource CapitalCostService capitalCostService;
	@Resource FleetMasterService fleetMasterService;
	@Resource ContractService contractService;
	@Resource FinanceParameterService financeParameterService;
	@Resource QuoteModelPropertyValueService  quoteModelPropertyValueService;
	@Resource HelpDeskEmailService helpDeskEmailService;
	@Resource UserContextDAO userContextDAO;
	@Resource LeaseElementDAO leaseElementDAO;
	@Resource DriverService driverService;
	@Resource QuotationElementService quotationElementService;
	@Resource ProcessStageService processStageService;
	@Resource QuotationCapitalElementDAO quotationCapitalElementDAO;
	@Resource JasperReportService jasperReportService;
	@Resource JasperReportBean jasperReportBean;
	@Resource OpenEndService openEndService;
	@Resource QuotationProfileDAO quotationProfileDAO;



	private final String COST_FIXED = "Fixed";
	private final String COST_VARIABLE = "Variable";
	private final int DECIMAL_PRECISION_THREE = 3;
	private final int DEPRECIATION_FACTOR_SCALE = 7;
	private final String OE_REV_ASSMT = "OE_REV_ASSMT";
	private final String OE_REV_INT_ADJ = "OE_REV_INT_ADJ";
	private final String IN_RATE_TYPE = "IN_RATE";
	private final String ONE_TIME_CHARGE_TYPE = "ONE_TIME_CHARGE";
	private final String OE_REV_ASSMT_INRATE_YN = "OE_REV_ASSMT_INRATE_YN";
	private final String OE_REV_INT_ADJ_INRATE_YN = "OE_REV_INT_ADJ_INRATE_YN";
	private final String OER_ACCEPTANCE_TYPE = "OER";
	private final String OE_REVISION_FEE = "OE_REV_FEE";
	private final int DECIMAL_DIG_PRECISION_THREE = 3;
	
	private static final String EMAIL_SUBJECT = "Error in client capital cost calculation. Please investigate.";
	private static final String EMAIL_BODY = "Error in client capital cost calculation. Please investigate. <br /> <br /> Quote#: {0} <br /> <br /> Unit#: {1} ";
	private final String CAP_COST_MIS_MATCH = "There is a problem with the capital cost on the quote which needs to be corrected before a revision contract can be created. Help Desk has been notified.";

	private static int MAX_NEW_STEPS = 5;


	
	private QuoteOEVO currentOEQuoteVO;
	private QuoteOEVO revisionOEQuoteVO = new QuoteOEVO();
	private FleetMaster fleetMaster ;
	private DriverAddress driverAddress;
	private Long currentQmdId;
	private long revisionQmdId;
	private String revisionStatus;
	private QuotationProfitability quotationProfitability;
	private boolean newQuote;	
	private boolean isQuoteEditable;
	private boolean isRevQuoteWasCalculatedAndSaved = false;
	private ContractLine latestContractLine;

	private QuotationModel revisionQuote;
	private boolean isQuoteCalculated = false;
	private boolean inRateElement = false;

	private List<QuotationStepStructureVO> currentSteps = new ArrayList<QuotationStepStructureVO>();
	private List<QuotationStepStructureVO> revisionSteps = new ArrayList<QuotationStepStructureVO>();
	private List<QuotationStepStructureVO> revStepsForUi = new ArrayList<QuotationStepStructureVO>();
	private List<QuotationStepStructureVO> definitionSteps;
	private String splMsgforStep;
	
	
	private List<QuoteRejectCode> rejectReasonList= new ArrayList<QuoteRejectCode>();
	private QuoteRejectCode selectedRejectReason;
	private Map<String, String> inputFieldIdMaps = new HashMap<String, String>();
	private Product	product;
	private long conChangeEventPeriod;	
	private long reviseContractRemainingPeriod;
	private long conCompletedBillingPeriod;	
	private BigDecimal currentCustomerCost;	
	private String isOEQuoteRevision = "Y";
	private boolean printBtnDisabled;
	private boolean forwardPage = false;
	private List <LeaseElement>inRateLeaseElements = new ArrayList <LeaseElement>();
	private Map<Long, BigDecimal> eventPeriodOneTimeCharge = new HashMap<Long, BigDecimal>();
	private Map<Long, BigDecimal> eventPeriodAmendmentChargeAfterLastRev = new HashMap<Long, BigDecimal>();//  maintain amendment charge done after last revision
	private BigDecimal firstContarctMAFSCost = BigDecimal.ZERO;// original/first ContarctMAFSCost
	private String newIRRLimit;
	private String newQuoteProfileId;
	
	@PostConstruct
	public void init() throws MalBusinessException {
		super.openPage();
		inputFieldIdMaps.put("DEPRECIATION_FACTOR", "deprFactor");
		inputFieldIdMaps.put("FINAL_NBV", "finalNBV");
		inputFieldIdMaps.put("REV_ASSMT", "revAssessment");
		inputFieldIdMaps.put("REV_INT_ADJ", "revIntAdj");
		inputFieldIdMaps.put("REV_CAP_CONTR", "capitalContribution");
		
		if (revisionQmdId == 0){
			newQuote = true;
		}else{
			 newQuote = false;
		}
		
		try {
			loadCurrentQuote();
			setRejectReasonList(quotationService.getAllQuoteRejectReasons());
			
			if(validCurrentQuote()){
				if(newQuote) {
					createAndLoadNewRevision();
					thisPage.getInputValues().put(ViewConstants.VIEW_REV_PARAM_QUOTE_MODEL_ID, revisionQmdId);
				} else {
					loadExistingRevision();
				}
				
				isQuoteEditable = getEditStatus();
				setCompareFieldValues();
			} else {
				addSimplErrorMessage(CAP_COST_MIS_MATCH);
				sendHelpDeskEmail();
			}
			checkForInRateElements();
			setMinimumIrrLimit();	

		} catch(Exception e) {
			handleException("generic.error", new String[]{"loading"}, e, "init");
			logger.error(e);
		}	
	}
	
	
	private boolean checkForInRateElements(){

		inRateLeaseElements = leaseElementDAO.findInRateLeaseElements();

		for(QuotationElement qe : revisionOEQuoteVO.getQuotationModel().getQuotationElements()){

			if(qe.getRental() == null){
				inRateElement = true;
				break;
			}
			if(qe.getRental().compareTo(BigDecimal.ZERO) > 0){
				if(isInRate(qe.getLeaseElement().getLelId())){
					inRateElement = true;
					break;
				}


			}
		}
		return false;

	}

	private boolean isInRate(Long id){
		for(LeaseElement le : inRateLeaseElements){
			if(le.getLelId() ==  id){
				return true;
			}
		}
		return false;
		
	}
	
	private void setMinimumIrrLimit(){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String workclass = ((User) authentication.getPrincipal()).getWorkclass();
		BigDecimal irrApprovedMinLimit = financeParameterService.getWorkclassLowerLimit(
				MalConstants.FIN_PARAM_OE_MIN_IRR_PROFIT, workclass, MalConstants.MODULE_VISION_QL);
		revisionOEQuoteVO.setIrrApprovedMinLimit(irrApprovedMinLimit);
	}
	
	
	private void sendHelpDeskEmail(){
		String body = MessageFormat.format(EMAIL_BODY, currentOEQuoteVO.getQuote(),currentOEQuoteVO.getUnitNo());
		User user = this.getLoggedInUser();
		String fromEmail = userContextDAO.getEmailAddress(user.getEmployeeNo().toUpperCase(), 1l);
		
		Email email = new Email();
		email.setHtmlFmt(true);
		email.setFrom(new EmailAddress(fromEmail));
		email.setSubject(EMAIL_SUBJECT);
		email.setMessage(body);
		
		try {
			helpDeskEmailService.sendEmailToHelpDesk(email);
		} catch (Exception e) {
			addSimplErrorMessage("Could not send E-mail to Help Desk");
			logger.error(e);
		}
		
	}
	
	private boolean validCurrentQuote(){
		boolean isValid = false;
		BigDecimal sum = BigDecimal.ZERO;
		for (QuotationElement quotationElement : currentOEQuoteVO.getQuotationModel().getQuotationElements()){
			if(!(quotationElement.getCapitalCost() == null)){
			sum = sum.add(quotationElement.getCapitalCost());
		}
		}
		if(currentCustomerCost.compareTo(sum)==0){
			isValid = true;
		}
		return isValid;
	}
	
	private void loadCurrentQuote() throws Exception {
		
		currentOEQuoteVO = loadQuote(quotationService.getQuotationModelWithCostAndAccessories(currentQmdId));		
		currentCustomerCost = currentOEQuoteVO.getCustomerCost();
		currentSteps =  quotationService.getAllQuoteSteps(currentQmdId);
		
		for (QuotationStepStructureVO stepStructureVO : currentSteps) {//this may have multiple quote's steps
			if(stepStructureVO.getAssociatedQmdId().equals(currentQmdId)){
				currentOEQuoteVO.setFinalResidual(currentSteps.get(currentSteps.size()-1).getNetBookValue());
				currentOEQuoteVO.setFinalNBV(currentSteps.get(currentSteps.size()-1).getNetBookValue());
				currentOEQuoteVO.setActualLeaseRate(stepStructureVO.getLeaseRate());
				break;
			}
		}
		
		/*
		 *  Logic to populate contract event changes period with One Time Charge Map for any past revised quote.
		 *  Also populating first contract MAFS cost to calculate IRR.( MAFS cost may change during revision due to capital contribution)
		 */
		List<Long> processedQmd = new ArrayList<Long>();
		
		for (QuotationStepStructureVO stepStructureVO : currentSteps) {
			
			
			if(! processedQmd.contains(stepStructureVO.getAssociatedQmdId())){
				
				processedQmd.add(stepStructureVO.getAssociatedQmdId());
				
				if(stepStructureVO.getFromPeriod() == 1){
					QuoteCost quoteCost = capitalCostService.getTotalCostsForQuote(quotationService.getQuotationModel(stepStructureVO.getAssociatedQmdId()));
					firstContarctMAFSCost = quoteCost.getDealCost();
				}
				
				BigDecimal totalOneTimeChanrge = BigDecimal.ZERO;					
				Map<String,String> qmPropertyNameValuePair = quoteModelPropertyValueService.findPropertyNameValuePair(stepStructureVO.getAssociatedQmdId());
			
				if( "R".equalsIgnoreCase(qmPropertyNameValuePair.get(QuoteModelPropertyEnum.QUOTE_TYPE.getName())) ){
					  
					 if(MalConstants.FLAG_N.equalsIgnoreCase(qmPropertyNameValuePair.get(QuoteModelPropertyEnum.OE_REV_ASSMT_INRATE_YN.getName()))){
						  String revAssmnt = qmPropertyNameValuePair.get(QuoteModelPropertyEnum.OE_REV_ASSMT.getName());
						  totalOneTimeChanrge = totalOneTimeChanrge.add(new BigDecimal(MALUtilities.isEmpty(revAssmnt) ? "0"  :  revAssmnt));  
					  }
					  
					  if(MalConstants.FLAG_N.equalsIgnoreCase(qmPropertyNameValuePair.get(QuoteModelPropertyEnum.OE_REV_INT_ADJ_INRATE_YN.getName()))){
						 	String revIntAdj = qmPropertyNameValuePair.get(QuoteModelPropertyEnum.OE_REV_INT_ADJ.getName()) ;
						 	totalOneTimeChanrge = totalOneTimeChanrge.add(new BigDecimal(MALUtilities.isEmpty(revIntAdj) ? "0"  :  revIntAdj )); 
					  }
						
					eventPeriodOneTimeCharge.put(stepStructureVO.getFromPeriod() , totalOneTimeChanrge);
				}
			}
		}
		
		this.fleetMaster = fleetMasterService.findByUnitNo(currentOEQuoteVO.getUnitNo());
		this.eventPeriodAmendmentChargeAfterLastRev = rentalCalculationService.getEventPeriodAndAmendedAccessCostAfterLastRev(currentQmdId , fleetMaster.getFmsId());
	}

	private void createAndLoadNewRevision() throws Exception {
		
		revisionQmdId = quotationService.createConractRevisionQuote(currentQmdId, "R", getLoggedInUser().getEmployeeNo());

		revisionQuote = quotationService.getQuotationModelWithCostAndAccessories(revisionQmdId);
		long cId = revisionQuote.getQuotation().getExternalAccount().getExternalAccountPK().getCId();
		revisionQuote.setAmendmentEffectiveDate(quotationService.getContractEffectiveDate(cId));
		revisionQuote.setRevisionExpDate(quotationService.getQuoteExpirationDate(cId, revisionQuote.getAmendmentEffectiveDate()));	
		setContractChangeEventPeriod();
		currentOEQuoteVO.setEffDateNBV(calculateEffDateNBV(currentOEQuoteVO));
		revisionQuote = rentalCalculationService.setupDataForNewOERevQuote(revisionQuote, 
																			new BigDecimal(conCompletedBillingPeriod),
																			currentOEQuoteVO.getDepreciationFactor() ,
																			currentOEQuoteVO.getEffDateNBV());
		revisionQuote = quotationService.getQuotationModelWithCostAndAccessories(revisionQmdId);
		
		if(revisionQuote != null) {
			revisionOEQuoteVO = loadQuote(revisionQuote);
			revisionQuote = revisionOEQuoteVO.getQuotationModel();
			revisionQuote.setQuoteModelPropertyValues(quoteModelPropertyValueService.findAllByQmdId(revisionQuote.getQmdId()));
			revisionOEQuoteVO.setRevisionInterestAdjustment(quotationService.getRevisionInterestAdjustment(currentOEQuoteVO, revisionOEQuoteVO, product, isOEQuoteRevision));
			Double amt = quotationService.getFinanceParam(OE_REVISION_FEE, revisionOEQuoteVO.getQuotationModel().getQmdId(), 
					new Long(revisionOEQuoteVO.getQuotationModel().getQuotation().getQuotationProfile().getQprId()), new Date());
			revisionOEQuoteVO.setRevisedAssessment(new BigDecimal(amt));
			revisionOEQuoteVO.setRevisedAssessmentType(ONE_TIME_CHARGE_TYPE);	
			revisionOEQuoteVO.setRevisedIntAdjType(IN_RATE_TYPE);
			
			revisionSteps = populateRevisionStepStructure(revisionOEQuoteVO);
			revisionOEQuoteVO.setActualIrrValue(null);
			revisionOEQuoteVO.setHideInvoiceAdjustment(true);
			revisionOEQuoteVO.setIrrApprovedLimit(null);
			revisionOEQuoteVO.setIrrApprovedDate(null);
			revisionOEQuoteVO.setIrrApprovedUser(null);
			revisionOEQuoteVO.setCapitalContribution(BigDecimal.ZERO);
			revisionOEQuoteVO.setInvoiceAdjustment(BigDecimal.ZERO);
			
			revisionOEQuoteVO.setCustomerCost(getRevCustomerCostTotal());			
			revisionOEQuoteVO.setFinalNBV(currentOEQuoteVO.getFinalNBV());
			
			updateDepFactor();
			
			populateInterestRateForNewQuote();
			populateaAdminFactForNewQuote();
			
			newIRRLimit = CommonCalculations.getRoundedValue(revisionOEQuoteVO.getMinimumIrr(), DECIMAL_DIG_PRECISION_THREE).toPlainString() + "%"; 
			
			
		}		
		
		
	}

	private void setContractChangeEventPeriod() {		
		latestContractLine = contractService.getCurrentContractLine(fleetMaster, new Date());
		Calendar stCal = Calendar.getInstance();
		stCal.setTime(latestContractLine.getStartDate());
		Calendar endCal = Calendar.getInstance();
		// temp fix until revision/amendment effective date is being saved on the quote model record
		if(revisionQuote.getAmendmentEffectiveDate() == null) {
			revisionQuote.setAmendmentEffectiveDate(new Date());
		}
		endCal.setTime(revisionQuote.getAmendmentEffectiveDate());
		conChangeEventPeriod = (long)Math.ceil(quotationService.monthsBetween(endCal, stCal)) + 1;
		reviseContractRemainingPeriod = revisionQuote.getContractPeriod() - conChangeEventPeriod + 1; 
		
		QuotationElement mainQuotationElement = rentalCalculationService.getMainQuotationModelElement(currentOEQuoteVO.getQuotationModel());
		List<QuotationElementStep> qes = mainQuotationElement.getQuotationElementSteps();
		Collections.sort(qes, quotationElementStepComparator);
		conCompletedBillingPeriod = conChangeEventPeriod - qes.get(0).getFromPeriod().longValue()  ;
	}
	
	private void loadExistingRevision() throws Exception {
		revisionOEQuoteVO = loadQuote(quotationService.getQuotationModelWithCostAndAccessories(revisionQmdId));
		revisionQuote = revisionOEQuoteVO.getQuotationModel();	
		revisionQuote.setQuoteModelPropertyValues(quoteModelPropertyValueService.findAllByQmdId(revisionQuote.getQmdId()));
	
		setContractChangeEventPeriod();
		currentOEQuoteVO.setEffDateNBV(calculateEffDateNBV(currentOEQuoteVO));

		for (QuoteModelPropertyValue quoteModelPropertyValue : revisionQuote.getQuoteModelPropertyValues()) {			
			String propertyName = quoteModelPropertyValue.getQuoteModelProperty().getName();			
			if(propertyName.equals(OE_REV_ASSMT)){		
				isRevQuoteWasCalculatedAndSaved = true;
				revisionOEQuoteVO.setRevisedAssessment(new BigDecimal(quoteModelPropertyValue.getPropertyValue()));					
			}else if(propertyName.equals(OE_REV_INT_ADJ)){				
				revisionOEQuoteVO.setRevisionInterestAdjustment(new BigDecimal(quoteModelPropertyValue.getPropertyValue()));					
			}else if(propertyName.equals(OE_REV_ASSMT_INRATE_YN)){				
				revisionOEQuoteVO.setRevisedAssessmentType(quoteModelPropertyValue.getPropertyValue().equals("Y") ? IN_RATE_TYPE  : ONE_TIME_CHARGE_TYPE);	
			}else if(propertyName.equals(OE_REV_INT_ADJ_INRATE_YN)){
				revisionOEQuoteVO.setRevisedIntAdjType(quoteModelPropertyValue.getPropertyValue().equals("Y") ? IN_RATE_TYPE  : ONE_TIME_CHARGE_TYPE);
			}
		}
		
	
		if (revisionOEQuoteVO.getRevisionInterestAdjustment() == null){
			revisionOEQuoteVO.setRevisionInterestAdjustment(BigDecimal.ZERO);
		}
		if(revisionOEQuoteVO.getRevisedAssessmentType() == null){//quote is not saved 
			revisionOEQuoteVO.setRevisedAssessmentType(ONE_TIME_CHARGE_TYPE);	
		}
		if(revisionOEQuoteVO.getRevisedIntAdjType() == null){//quote is not saved 
			revisionOEQuoteVO.setRevisedIntAdjType(IN_RATE_TYPE);
		}
		
		if(! isRevQuoteWasCalculatedAndSaved){

			revisionOEQuoteVO.setHideInvoiceAdjustment(currentOEQuoteVO.isHideInvoiceAdjustment());
			revisionOEQuoteVO.setFinalNBV(currentOEQuoteVO.getFinalNBV());
			
			populateInterestRateForNewQuote();
			Double amt = quotationService.getFinanceParam(OE_REVISION_FEE, revisionOEQuoteVO.getQuotationModel().getQmdId(), 
					new Long(revisionOEQuoteVO.getQuotationModel().getQuotation().getQuotationProfile().getQprId()), new Date());
			revisionOEQuoteVO.setRevisedAssessment(new BigDecimal(amt));
			revisionOEQuoteVO.setRevisedAssessmentType(ONE_TIME_CHARGE_TYPE);	
			revisionOEQuoteVO.setRevisionInterestAdjustment(quotationService.getRevisionInterestAdjustment(currentOEQuoteVO, revisionOEQuoteVO, product, isOEQuoteRevision));
			revisionOEQuoteVO.setCustomerCost(getRevCustomerCostTotal());
			revisionOEQuoteVO.setActualIrrValue(null);
			revisionOEQuoteVO.setInvoiceAdjustment(BigDecimal.ZERO);
			updateDepFactor();
			revisionOEQuoteVO.setIrrApprovedLimit(null);
			newIRRLimit = CommonCalculations.getRoundedValue(revisionOEQuoteVO.getMinimumIrr(), DECIMAL_DIG_PRECISION_THREE).toPlainString() + "%"; 

		}else{
			revisionOEQuoteVO.setCustomerCost(getRevCustomerCostTotal());
			newIRRLimit = revisionOEQuoteVO.getIrrApprovedLimit();
		}
		
		populateRevisionStepStructure(revisionOEQuoteVO);
	}
	
	private void populateInterestRateForNewQuote(){
		
		revisionOEQuoteVO.setInterestBaseRate(getBaseInterestRate(revisionQuote , revisionOEQuoteVO.getTerm()));
		revisionOEQuoteVO.setBaseIrr(CommonCalculations.getRoundedValue(rentalCalculationService.getHurdleRate(revisionOEQuoteVO.getQuotationModel()),RentalCalculationService.IRR_CALC_DECIMALS));
		revisionOEQuoteVO.setProfitAdj(CommonCalculations.getRoundedValue(rentalCalculationService.getProfitAdj(revisionOEQuoteVO.getQuotationModel()),RentalCalculationService.IRR_CALC_DECIMALS));						
		
		BigDecimal interestAdjustment;
		try {
			interestAdjustment = new BigDecimal(quotationService.getFinanceParam(MalConstants.FIN_PARAM_INT_ADJ,
					revisionOEQuoteVO.getQuotationModel().getQmdId(), revisionOEQuoteVO.getQuotationModel().getQuotation().getQuotationProfile().getQprId(),
					new Date(), true)).setScale(DECIMAL_PRECISION_THREE, RoundingMode.HALF_UP);
		} catch (Exception e) {
			interestAdjustment = BigDecimal.ZERO;
		}
		revisionOEQuoteVO.setInterestAdjustment(interestAdjustment);
		
		
		if(revisionOEQuoteVO.getInterestAdjustment() == null) {
			revisionOEQuoteVO.setInterestAdjustment(BigDecimal.ZERO);
		}
		revisionOEQuoteVO.setInterestTotalRate(revisionOEQuoteVO.getInterestAdjustment().add(revisionOEQuoteVO.getInterestBaseRate(),CommonCalculations.MC));
		
	} 
	
	
	private BigDecimal getBaseInterestRate(QuotationModel quotationModel, long term){
		
		BigDecimal baseRate = quotationService.getProfileBaseRateByTerm(quotationModel, term);
		if (baseRate == null) {
			baseRate = BigDecimal.ZERO;
		}
		baseRate = baseRate.setScale(DECIMAL_PRECISION_THREE, RoundingMode.HALF_UP);
		
		return baseRate;
	} 
	
	
	
	private void populateaAdminFactForNewQuote(){
		BigDecimal adminFactorFinV;
		try {
			adminFactorFinV = new BigDecimal(quotationService.getFinanceParam(MalConstants.FIN_PARAM_ADMIN_FACT,
					revisionOEQuoteVO.getQuotationModel().getQmdId(), revisionOEQuoteVO.getQuotationModel().getQuotation().getQuotationProfile().getQprId(),new Date(), true));
		} catch (MalBusinessException e) {
			adminFactorFinV = BigDecimal.ZERO;
		}

		revisionOEQuoteVO.setAdminFactor(adminFactorFinV);
	}
	
	
	
	
	private QuoteOEVO loadQuote(QuotationModel quotationModel) throws Exception {
		
		QuoteOEVO quoteOEVO = new QuoteOEVO();
		
		if (quotationModel.getQmdId().compareTo(revisionQmdId) == 0 ) {
			quotationModel = quotationService.changeProfileOnQuotationModels(quotationModel);
		}		
		quoteOEVO.setQuotationModel(quotationModel);
		product	= productDAO.findById(quotationModel.getQuotation().getQuotationProfile().getPrdProductCode()).orElse(null);
		quoteOEVO.setProductName(product.getProductName());

		quoteOEVO.setAccountCode(quotationModel.getQuotation().getExternalAccount().getExternalAccountPK()
				.getAccountCode());
	
		quoteOEVO.setAccountName(quotationModel.getQuotation().getExternalAccount().getAccountName());
		quoteOEVO.setQuote(Long.toString(quotationModel.getQuotation().getQuoId()) + "/"
				+ Long.toString(quotationModel.getQuoteNo()) + "/" + Long.toString(quotationModel.getRevisionNo()));
		quoteOEVO.setUnitNo(quotationModel.getUnitNo());
		quoteOEVO.setUnitDesc(quotationModel.getModel().getModelDescription());

		quoteOEVO.setTerm(quotationModel.getContractPeriod());
		quoteOEVO.setDistance(quotationModel.getContractDistance());
		quoteOEVO.setProjectedReplacementMonths(quotationModel.getProjectedMonths());
		
		
		quoteOEVO.setQuoteProfileDesc(quotationModel.getQuotation().getQuotationProfile().getDescription());
		quoteOEVO.setCostTreatment(quotationModel.getPreContractFixedCost().equals("F") ? COST_FIXED : COST_VARIABLE);
		quoteOEVO.setInterestTreatment(quotationModel.getPreContractFixedInterest().equals("F") ? COST_FIXED : COST_VARIABLE);
		quoteOEVO.setInterestIndex(quotationModel.getQuotation().getQuotationProfile().getItcInterestType());
		quoteOEVO.setFloatType(quotationModel.getQuotation().getQuotationProfile().getVariableRate().equals("V") ? "Float" : "Non-Float");

		
		
		BigDecimal interestAdjustment = new BigDecimal(quotationService.getFinanceParam(MalConstants.FIN_PARAM_INT_ADJ,
				quotationModel.getQmdId(), quotationModel.getQuotation().getQuotationProfile().getQprId())).setScale(DECIMAL_PRECISION_THREE, RoundingMode.HALF_UP);
		BigDecimal bdInterestAdjustment = interestAdjustment;
		quoteOEVO.setInterestAdjustment(bdInterestAdjustment);

		
		quoteOEVO.setInterestTotalRate(quotationModel.getInterestRate());
		quoteOEVO.setInterestBaseRate(quotationModel.getInterestRate().subtract(bdInterestAdjustment)
				.setScale(DECIMAL_PRECISION_THREE, RoundingMode.HALF_UP));

		Double invoiceAdjustment = quotationService.getFinanceParam(MalConstants.FIN_PARAM_OE_INV_ADJ, quotationModel.getQmdId(),
				quotationModel.getQuotation().getQuotationProfile().getQprId());
		quoteOEVO.setInvoiceAdjustment(BigDecimal.valueOf(invoiceAdjustment));
		quoteOEVO.setRevisedAssessment(BigDecimal.ZERO);

		
		BigDecimal finalResidual = BigDecimal.ZERO;
		BigDecimal mainElementResidual = quotationModel.getResidualValue();
		BigDecimal equipmentResidual = rentalCalculationService.getEquipmentResidual(quotationModel);
		finalResidual = mainElementResidual.add(equipmentResidual);

		quoteOEVO.setMainElementResidual(mainElementResidual);

		finalResidual = openEndService.getModifiedNBV(finalResidual);
		quoteOEVO.setFinalResidual(finalResidual);
		quoteOEVO.setFinalNBV(finalResidual);

		
		
		
		QuotationModelFinances qmf = financeParameterService.getQuotationModelFinances(quotationModel.getQmdId(), MalConstants.FIN_PARAM_OE_INV_ADJ);
		if (qmf != null) {
			quoteOEVO.setHideInvoiceAdjustment(rentalCalculationService.isReportsHidden(qmf));
		} else {
			quoteOEVO.setHideInvoiceAdjustment(false);
		}

		Double adminFactorFinV = quotationService.getFinanceParam(MalConstants.FIN_PARAM_ADMIN_FACT,
				quotationModel.getQmdId(), quotationModel.getQuotation().getQuotationProfile().getQprId());

		quoteOEVO.setAdminFactor(BigDecimal.valueOf(adminFactorFinV));

		

		quoteOEVO.setServiceElementRate(serviceElementService.getServiceElementMonthlyCost(quotationModel));
		
		quotationProfitability = profitabilityService.getQuotationProfitability(quotationModel);
		if (quotationProfitability != null) {
			quoteOEVO.setBaseIrr(CommonCalculations.getRoundedValue(quotationProfitability.getProfitBase(),	RentalCalculationService.IRR_CALC_DECIMALS));
			quoteOEVO.setProfitAdj(CommonCalculations.getRoundedValue(quotationProfitability.getProfitAdjustment(), RentalCalculationService.IRR_CALC_DECIMALS));
			quoteOEVO.setIrrApprovedUser(quotationProfitability.getIrrApprovedUser());
			quoteOEVO.setIrrApprovedDate(quotationProfitability.getIrrApprovedDate());
			quoteOEVO.setActualIrrValue(quotationProfitability.getProfitAmount() != null ? CommonCalculations
					.getRoundedValue(quotationProfitability.getProfitAmount(), RentalCalculationService.IRR_CALC_DECIMALS) : null);
			quoteOEVO.setIrrApprovedLimit(quotationProfitability.getIrrApprovedLimit() != null ? String
					.valueOf(CommonCalculations.getRoundedValue(quotationProfitability.getIrrApprovedLimit() != null ? quotationProfitability
									.getIrrApprovedLimit() : null, DECIMAL_PRECISION_THREE)) : null);
			if(quoteOEVO.getIrrApprovedLimit() != null) {
				quoteOEVO.setIrrApprovedLimit(quoteOEVO.getIrrApprovedLimit()+"%");
			}
		}
		
		quoteOEVO.setCapitalContribution(quotationModel.getCapitalContribution() == null ? BigDecimal.ZERO
				: quotationModel.getCapitalContribution());

		QuoteCost quoteCost = capitalCostService.getTotalCostsForQuote(quotationModel);
		quoteOEVO.setDealCost(quoteCost.getDealCost());
		quoteOEVO.setCustomerCost(quoteCost.getCustomerCost());

		if(quotationModel.getDepreciationFactor() != null){ 
			quoteOEVO.setDepreciationFactor(quotationModel.getDepreciationFactor().setScale(DEPRECIATION_FACTOR_SCALE, RoundingMode.HALF_UP));
		} else {
			quoteOEVO.setDepreciationFactor(openEndService.getCalculatedDepreciationFactor(quoteOEVO));
		}

		
		return quoteOEVO;
	}
	
	private BigDecimal calculateEffDateNBV(QuoteOEVO currentOEQuoteVO){	
		
		BigDecimal effNBV = profitabilityService.getFinalNBV(currentOEQuoteVO.getCustomerCost(), currentOEQuoteVO.getDepreciationFactor(), new BigDecimal(conCompletedBillingPeriod));
		
		return effNBV;
		
	}
	
	Comparator<QuotationElementStep> quotationElementStepComparator = new Comparator<QuotationElementStep>() {
		public int compare(QuotationElementStep r1, QuotationElementStep r2) {
			Long fromPeriod1 = r1.getFromPeriod().longValue();
			Long fromPeriod2 = r2.getFromPeriod().longValue();
			return fromPeriod1.compareTo(fromPeriod2);
		}
	};
	
/*	private BigDecimal getModifiedNBV(BigDecimal
 *  nbv) {
		BigDecimal thresholdAmount = BigDecimal.ZERO;
		if (nbv.abs().compareTo(thresholdAmount) < 0) {
			return BigDecimal.ZERO;
		} else {
			return nbv;
		}
	}*/

/*	private BigDecimal getCalculatedDepreciationFactor(QuoteOEVO quoteOEVO) {
		BigDecimal depreciationFactor = profitabilityService.getDepreciationFactor(quoteOEVO.getCustomerCost(),
				quoteOEVO.getFinalNBV(), new BigDecimal(quoteOEVO.getQuotationModel().getContractPeriod()));
		depreciationFactor = depreciationFactor.movePointRight(2);
		depreciationFactor = depreciationFactor.setScale(DEPRECIATION_FACTOR_SCALE, RoundingMode.HALF_UP);
		return depreciationFactor;
	}*/

/*	
	private List<QuotationStepStructureVO> loadSteps(QuoteOEVO quoteOEVO) {
		List<QuotationStepStructure> quotationStepStructureList = quotationService.getQuotationModelStepStructure(quoteOEVO.getQuotationModel().getQmdId());
		return quotationService.getCalculateQuotationStepStructure(quotationStepStructureList, quoteOEVO.getQuotationModel(), quoteOEVO.getDepreciationFactor(), 
				quoteOEVO.getAdminFactor(), quoteOEVO.getCustomerCost());

	}*/

	private List<QuotationStepStructureVO> populateRevisionStepStructure(QuoteOEVO quoteOEVO) {
	
		revisionSteps.clear();
		revStepsForUi = getCurrentContractStepsforRevisionSteps(revStepsForUi);
		
		if( isRevQuoteWasCalculatedAndSaved){
			
			revisionSteps = openEndService.loadSteps(revisionOEQuoteVO);
			revisionOEQuoteVO.setActualLeaseRate(revisionSteps.get(0).getLeaseRate());
			
		}else{
			// Build the last step
			QuotationStepStructureVO qssVO = new QuotationStepStructureVO();
			qssVO.setFromPeriod(conChangeEventPeriod);
			if (MALUtilities.isEmpty(quoteOEVO.getTerm())) {
				qssVO.setToPeriod(quoteOEVO.getQuotationModel().getContractPeriod());
			} else {
				qssVO.setToPeriod(quoteOEVO.getTerm());
			}
			revisionSteps.add(qssVO);			
		}
		
		revStepsForUi.addAll(revisionSteps);
		return revisionSteps;
	}
	
	public void cancelSaveAppIrrMin() {
		if(revisionOEQuoteVO.getIrrApprovedLimit() != null) {
			newIRRLimit = revisionOEQuoteVO.getIrrApprovedLimit();			
		} else {
			
		}
    }
	
	public void saveAppIrrMin() {
    	
    	if (validateApprovedMinIrr()) {
    		revisionOEQuoteVO.setIrrApprovedUser(getLoggedInUser().getEmployeeNo());
    		revisionOEQuoteVO.setIrrApprovedDate(new Date());
    		revisionOEQuoteVO.setIrrApprovedLimit(newIRRLimit);
    		
    		
    		profitabilityService.saveQuotationProfitability(getUpdatedProfitabiltyObject(true));
	    	quotationProfitability = profitabilityService.getQuotationProfitability(revisionQuote);
	    	if(revisionOEQuoteVO.getIrrApprovedLimit() != null){
	    		revisionOEQuoteVO.setIrrApprovedUser(quotationProfitability.getIrrApprovedUser());
	    		revisionOEQuoteVO.setIrrApprovedDate(quotationProfitability.getIrrApprovedDate());
	        }else{
	        	revisionOEQuoteVO.setIrrApprovedUser(null);
	        	revisionOEQuoteVO.setIrrApprovedDate(null);   
	        }
	    	
	    	addSuccessMessage("saved.success", "Approved Minimum IRR");
    	} else {
    	    RequestContext context = RequestContext.getCurrentInstance();
    	    super.addErrorMessageSummary("custom.message", talMessage.getMessage("generic.error.occured.while", "Approved Minimum IRR"));
    	    context.addCallbackParam("failure", true);    		
    	}
    }
	
	
	private boolean validateApprovedMinIrr() {
		
		BigDecimal newIRRLimitValue = MALUtilities.isEmpty(newIRRLimit) ? null:new BigDecimal( newIRRLimit.replaceAll("%", ""));
		// handling negative value,using relative value to validate
		if (!MALUtilities.isEmpty(newIRRLimitValue)  
				&& revisionOEQuoteVO.getIrrApprovedMinLimit() != null) {
			BigDecimal temp = revisionOEQuoteVO.getMinimumIrr().add(revisionOEQuoteVO.getIrrApprovedMinLimit());

			if (newIRRLimitValue.compareTo(temp) < 0) {
				super.addErrorMessageSummary("custom.message",
						talMessage.getMessage("approved.min.irr.range", temp.toPlainString()));
				return false;
			}
		}
		return true;
	}
	
	private List<QuotationStepStructureVO>  getCurrentContractStepsforRevisionSteps(List<QuotationStepStructureVO> steps){
		
		steps.clear();
		
		for(QuotationStepStructureVO step : currentSteps){
			if(step.getToPeriod() < conChangeEventPeriod){
				steps.add(step);
			}
			else if(step.getFromPeriod() < conChangeEventPeriod && step.getToPeriod() >= conChangeEventPeriod){
				QuotationStepStructureVO modifiedStep  = new QuotationStepStructureVO();
				modifiedStep.setFromPeriod(step.getFromPeriod());
				modifiedStep.setLeaseFactor(step.getLeaseFactor());
				modifiedStep.setLeaseRate(step.getLeaseRate());
				modifiedStep.setNetBookValue(currentOEQuoteVO.getEffDateNBV());
				modifiedStep.setToPeriod(conChangeEventPeriod - 1);
				steps.add(modifiedStep);
			}
		}
		
		return  steps ;
	}
	
	
	public void calc() throws MalBusinessException {
		setSplMsgforStep(null);
		if(validToCalc()) {
		
			try {
				logger.debug("OpenEndQuoteRevisionBean:Calc:Start");
				
				isQuoteCalculated = false;
				reviseContractRemainingPeriod = revisionOEQuoteVO.getTerm() - conChangeEventPeriod + 1;
				revisionOEQuoteVO.setCustomerCost(getRevCustomerCostTotal());
				boolean isUpdateSuccess = updateInterestTotalRate();
				if(! isUpdateSuccess){
					return;
				}
				
				if(revisionOEQuoteVO.getTerm().compareTo(revisionOEQuoteVO.getTermToCompare()) != 0){
					//OER-1483 Roshan      
					BigDecimal hurdleRate = rentalCalculationService.getHurdleRateByTerm(revisionOEQuoteVO.getQuotationModel(), revisionOEQuoteVO.getTerm());
					if (!MALUtilities.isEmpty(hurdleRate)) {
						revisionOEQuoteVO.setBaseIrr(hurdleRate);
					}
					
					revisionOEQuoteVO.setInterestBaseRate(getBaseInterestRate(revisionQuote , revisionOEQuoteVO.getTerm()));
					revisionOEQuoteVO.setInterestTotalRate(revisionOEQuoteVO.getInterestAdjustment().add(revisionOEQuoteVO.getInterestBaseRate(),CommonCalculations.MC));
					
					Iterator<QuotationStepStructureVO> stepItr = revisionSteps.iterator();
					while (stepItr.hasNext()) {
						QuotationStepStructureVO step = stepItr.next(); 
						if(step.getFromPeriod() > revisionOEQuoteVO.getTerm() ){
							stepItr.remove();	
						}
					}
					
					revisionSteps.get(revisionSteps.size()-1).setToPeriod(revisionOEQuoteVO.getTerm());	
				}
				
				if (revisionOEQuoteVO.getDepreciationFactor().compareTo(revisionOEQuoteVO.getDepreciationFactorToCompare()) != 0) {				
					if(validateDeprFactor()){
						updateFinalNBV();
						if (!validateFinalNBV())
							return;
					}else{
						return;
					}
				} else if(revisionOEQuoteVO.getCustomerCost().compareTo(revisionOEQuoteVO.getCustomerCostToCompare()) != 0
							|| revisionOEQuoteVO.getFinalNBV().compareTo(revisionOEQuoteVO.getFinalNBVToCompare()) != 0 
							|| revisionOEQuoteVO.getTerm().compareTo(revisionOEQuoteVO.getTermToCompare()) != 0 ) {
						
						if (validateFinalNBV()){
							updateDepFactor();
							if (!validateDeprFactor())
								return;
						}else{
							return;
						}						
				}
				
							
				if (!validateClientCost()) {
					return;
				}				
				
				calculateQuote();
				setCompareFieldValues();
				setDirtyData(true);
				printBtnDisabled = true;
	
				if (!validateFinalNBV()) {
					isQuoteCalculated = false;
				} else {
					addInfoMessage("quote.calculation.success");
				}
			
				logger.debug("OpenEndQuoteRevisionBean:Calc:End");
			} catch (Exception ex) {
				handleException("generic.error.occured.while", new String[] { "calculating quote " }, ex, null);
			}
		}

	}
	
	private void calculateQuote() throws MalBusinessException {
		
		BigDecimal addOnCost = BigDecimal.ZERO;
		if(revisionOEQuoteVO.getRevisedAssessmentType().equalsIgnoreCase(IN_RATE_TYPE)){
			addOnCost = addOnCost.add(revisionOEQuoteVO.getRevisedAssessment());
		}	
		if(revisionOEQuoteVO.getRevisedIntAdjType().equalsIgnoreCase(IN_RATE_TYPE)){
			addOnCost = addOnCost.add(revisionOEQuoteVO.getRevisionInterestAdjustment());
		}		
		if(revisionOEQuoteVO.getCapitalContribution() != null){
			addOnCost = addOnCost.subtract(revisionOEQuoteVO.getCapitalContribution());
		}
		
		if(revisionOEQuoteVO.getInvoiceAdjustment() != null){
			addOnCost = addOnCost.add(revisionOEQuoteVO.getInvoiceAdjustment());
		}
		
		
		BigDecimal adminFactor = revisionOEQuoteVO.getAdminFactor();
		BigDecimal interestRate = revisionOEQuoteVO.getInterestTotalRate();
		interestRate = interestRate.divide(new BigDecimal(1200), CommonCalculations.MC);
		BigDecimal depreciationFactor = revisionOEQuoteVO.getDepreciationFactor();
		depreciationFactor = depreciationFactor.divide(new BigDecimal(100), CommonCalculations.MC);
	
		List<QuotationCapitalElement>  qceList = quotationCapitalElementDAO.findByQmdID(revisionQuote.getQmdId());
		
		for (QuotationElement quotationElement : revisionQuote.getQuotationElements()) {				
			if (quotationElement.getLeaseElement().getElementType().equals(MalConstants.FINANCE_ELEMENT)
					&& MalConstants.FLAG_Y.equals(quotationElement.getIncludeYn())) {
				
				
				if(quotationElement.getQuotationModelAccessory() == null  && quotationElement.getQuotationDealerAccessory() == null){
					
					BigDecimal depreciatedCost =  quotationElementService.getCalculatedQuotationElement(quotationElement, revisionQuote, false).getCapitalCost();
					//Add last save cap contribution  and subtract oe inv adj or any in rate values to get original value.
					depreciatedCost = depreciatedCost.add(revisionQuote.getCapitalContribution());
					
					
					for (QuotationCapitalElement quotationCapitalElement : qceList) {						
						if(quotationCapitalElement.getCapitalElement().getCode().equals(CapitalElementService.OE_INV_ADJ)
								|| quotationCapitalElement.getCapitalElement().getCode().equals(CapitalElementService.OE_REV_ASSMNT)
								|| quotationCapitalElement.getCapitalElement().getCode().equals(CapitalElementService.OE_REV_INT_ADJ)){
							
							depreciatedCost = depreciatedCost.subtract(quotationCapitalElement.getValue());
						}
					}
					
					depreciatedCost = depreciatedCost.add(addOnCost);//update main element cost by changed client cost
					quotationElement.setCapitalCost(depreciatedCost);
				}	
			}				
		} 
		
		for (QuotationStepStructureVO quotationStepStructure : revisionSteps) {				
			List<QuoteElementStepVO> quoteElementStepVOs = new ArrayList<>();
			for (QuotationElement quotationElement : revisionQuote.getQuotationElements()) {				
				if (quotationElement.getLeaseElement().getElementType().equals(MalConstants.FINANCE_ELEMENT)
						&& MalConstants.FLAG_Y.equals(quotationElement.getIncludeYn())) {
					
					BigDecimal elementCost = quotationElement.getCapitalCost();
					
					quoteElementStepVOs.add( new QuoteElementStepVO(elementCost, elementCost, elementCost, elementCost)) ;
					quotationStepStructure.setQuoteElementStepVOs(quoteElementStepVOs);	
					
				}
			}
		}
		
		
		List<QuotationStepStructureVO> quotationStepResponseList = profitabilityService.calculateOEStepLease(depreciationFactor, revisionOEQuoteVO.getFinalNBV() , interestRate, adminFactor, this.revisionSteps);		
		
		
		//this is needed otherwise save will not work 
		for (QuotationStepStructureVO quotationStepStructureVO : quotationStepResponseList) {
			for (QuoteElementStepVO quoteElementStepVO : quotationStepStructureVO.getQuoteElementStepVOs()) {				
				quoteElementStepVO.setOriginalCost(quoteElementStepVO.getElementOriginalCost());
			}			
		}
		
		 for (QuoteElementStepVO quoteElementStepVO : quotationStepResponseList.get(quotationStepResponseList.size() -1).getQuoteElementStepVOs()) {
			 for (QuotationElement quotationElement : revisionQuote.getQuotationElements()) {
				 if (quotationElement.getLeaseElement().getElementType().equals(MalConstants.FINANCE_ELEMENT)
						 			&& MalConstants.FLAG_Y.equals(quotationElement.getIncludeYn())) {
					 if(quotationElement.getCapitalCost().compareTo(quoteElementStepVO.getOriginalCost()) == 0){
						 quotationElement.setFinalPayment(quoteElementStepVO.getEndCapital());
					 }
				 }else{
						if(quotationElement.getRental() != null && quotationElement.getRental().compareTo(BigDecimal.ZERO) != 0){
							BigDecimal currentMonthyRental = quotationElement.getRental().divide(quotationElement.getNoRentals(), CommonCalculations.MC);
							BigDecimal totalRental = currentMonthyRental.multiply(new BigDecimal(reviseContractRemainingPeriod), CommonCalculations.MC);
							quotationElement.setRental(CommonCalculations.getRoundedValue(totalRental, RentalCalculationService.CURRENCY_DECIMALS));
					 }
				 }
				
				 quotationElement.setNoRentals(new BigDecimal(reviseContractRemainingPeriod));
			 }
		 }
		
		BigDecimal adminFee = profitabilityService.getFinanceParameter(MalConstants.CLOSED_END_LEASE_ADMIN, revisionQuote.getQmdId(), revisionQuote.getQuotation().getQuotationProfile().getQprId());
		
		revisionSteps = quotationStepResponseList;
		revisionOEQuoteVO.setActualLeaseRate(revisionSteps.get(0).getLeaseRate());
		// revisionOEQuoteVO.setFinalNBV(quoteSteps.get(quoteSteps.size()
		// -1).getNetBookValue());
		revisionSteps.get(revisionSteps.size() - 1).setNetBookValue(revisionOEQuoteVO.getFinalNBV());

		for (QuotationStepStructureVO qssVO : revisionSteps) {
			qssVO.setLeaseFactor(getLeaseFactor(qssVO));
		}
		
		// replace last step with calculated step
		revStepsForUi = getCurrentContractStepsforRevisionSteps(revStepsForUi);
		revStepsForUi.addAll(revisionSteps);
		
		BigDecimal calculatedIrr = profitabilityService.calculateRevIrrFromOEStep(revStepsForUi,  firstContarctMAFSCost, adminFee , eventPeriodAmendmentChargeAfterLastRev,  getUpdatedContractEventPeriodAndOneTimeChargeMap() );
		calculatedIrr = calculatedIrr != null ? CommonCalculations.getRoundedValue(calculatedIrr, RentalCalculationService.IRR_CALC_DECIMALS) : null;
		revisionOEQuoteVO.setActualIrrValue(calculatedIrr);
		
		calculateInRateServiceElement();
		revisionOEQuoteVO.setServiceElementRate(serviceElementService.getServiceElementMonthlyCost(revisionQuote));
		
		isQuoteCalculated = true;
	}
	
	private List<QuotationElement> calculateInRateServiceElement() throws MalBusinessException{
		
		List<QuotationElement> updatedServiceElement = new ArrayList<QuotationElement>();
		
		for (QuotationElement quotationElement : revisionQuote.getQuotationElements()) {
			if (!quotationElement.getLeaseElement().getElementType().equals(MalConstants.FINANCE_ELEMENT)
				 && quotationElement.getLeaseElement() != null
				 && quotationElement.getLeaseElement().getInRateTreatmentYn().equals("Y")) {
				
					updatedServiceElement.add(quotationElementService.getCalculatedQuotationElement(quotationElement, revisionQuote, false));
			}
		}	
		
		return updatedServiceElement;
	}
	
	private void setCompareFieldValues() {
		if(revisionOEQuoteVO != null){
			revisionOEQuoteVO.setFinalNBVToCompare(revisionOEQuoteVO.getFinalNBV());
			revisionOEQuoteVO.setDepreciationFactorToCompare(revisionOEQuoteVO.getDepreciationFactor());
			revisionOEQuoteVO.setCapitalContributionToCompare(revisionOEQuoteVO.getCapitalContribution());
			revisionOEQuoteVO.setInvoiceAdjustmentToCompare(revisionOEQuoteVO.getInvoiceAdjustment());
			revisionOEQuoteVO.setRevisedAssessmentToCompare(revisionOEQuoteVO.getRevisedAssessment() != null ? revisionOEQuoteVO.getRevisedAssessment() : BigDecimal.ZERO);
			revisionOEQuoteVO.setRevisionInterestAdjustmentToCompare(revisionOEQuoteVO.getRevisionInterestAdjustment() != null ? revisionOEQuoteVO.getRevisionInterestAdjustment() : BigDecimal.ZERO);
			revisionOEQuoteVO.setRevisedAssessmentTypeToCompare(revisionOEQuoteVO.getRevisedAssessmentType());
			revisionOEQuoteVO.setRevisedIntAdjTypeToCompare(revisionOEQuoteVO.getRevisedIntAdjType());
			revisionOEQuoteVO.setTermToCompare(revisionOEQuoteVO.getTerm());
			revisionOEQuoteVO.setCustomerCostToCompare(revisionOEQuoteVO.getCustomerCost());
		}
	}
	
	private Map<Long, BigDecimal>   getUpdatedContractEventPeriodAndOneTimeChargeMap(){
		
		BigDecimal totalOneTimeCharge = BigDecimal.ZERO;		
		if(revisionOEQuoteVO.getRevisedAssessmentType().equalsIgnoreCase(ONE_TIME_CHARGE_TYPE)){
			totalOneTimeCharge = totalOneTimeCharge.add(revisionOEQuoteVO.getRevisedAssessment());
		}	
		if(revisionOEQuoteVO.getRevisedIntAdjType().equalsIgnoreCase(ONE_TIME_CHARGE_TYPE)){
			totalOneTimeCharge = totalOneTimeCharge.add(revisionOEQuoteVO.getRevisionInterestAdjustment());
		}

		eventPeriodOneTimeCharge.put(conChangeEventPeriod , totalOneTimeCharge);			
	
		return eventPeriodOneTimeCharge;
	}
	
	private BigDecimal  getRevCustomerCostTotal(){
		
		BigDecimal revCustomerCostTotal = BigDecimal.ZERO;
		
		revCustomerCostTotal = currentOEQuoteVO.getEffDateNBV().subtract(revisionOEQuoteVO.getCapitalContribution()).add(revisionOEQuoteVO.getInvoiceAdjustment());		
		
		if(revisionOEQuoteVO.getRevisedAssessmentType().equalsIgnoreCase(IN_RATE_TYPE)){
			revCustomerCostTotal =  revCustomerCostTotal.add(revisionOEQuoteVO.getRevisedAssessment());
		}	
		if(revisionOEQuoteVO.getRevisedIntAdjType().equalsIgnoreCase(IN_RATE_TYPE)){
			revCustomerCostTotal = revCustomerCostTotal.add(revisionOEQuoteVO.getRevisionInterestAdjustment());
		}
		return revCustomerCostTotal;
	}

	private BigDecimal getLeaseFactor(QuotationStepStructureVO step) {
		return profitabilityService.getLeaseFactor(step.getLeaseRate(), revisionOEQuoteVO.getDepreciationFactor(),
				revisionOEQuoteVO.getAdminFactor(), revisionOEQuoteVO.getCustomerCost());
	}
	
	private boolean updateInterestTotalRate() {
		revisionOEQuoteVO.setInterestAdjustment(revisionOEQuoteVO.getInterestAdjustment() != null ? revisionOEQuoteVO
				.getInterestAdjustment() : BigDecimal.ZERO);
		BigDecimal temp = revisionOEQuoteVO.getInterestAdjustment().add(revisionOEQuoteVO.getInterestBaseRate(),
				CommonCalculations.MC);
		revisionOEQuoteVO.setInterestTotalRate(temp);
		return validateTotalInterestRate();
	}

	private boolean validateTotalInterestRate() {		
		if (revisionOEQuoteVO.getInterestTotalRate() != null
				&& revisionOEQuoteVO.getInterestTotalRate().compareTo(BigDecimal.ZERO) < 0) {
			addErrorMessage("custom.message", talMessage.getMessage("negative.notallowed", "Interest Rate"));
			return false;
		}
		return true;
	}
	
	public void updateFinalNBV() {
		BigDecimal finalNBV = profitabilityService.getFinalNBV(revisionOEQuoteVO.getCustomerCost(),
				revisionOEQuoteVO.getDepreciationFactor(), new BigDecimal(getReviseContractRemainingPeriod()));
		revisionOEQuoteVO.setFinalNBV(finalNBV);
		revisionOEQuoteVO.setFinalNBVToCompare(revisionOEQuoteVO.getFinalNBV());
		revisionOEQuoteVO.setDepreciationFactorToCompare(revisionOEQuoteVO.getDepreciationFactor());
	}

	
	private boolean validateFinalNBV() {
		if (revisionOEQuoteVO.getFinalNBV() != null) {
			if (revisionOEQuoteVO.getFinalNBV().compareTo(new BigDecimal(revisionOEQuoteVO.getCustomerCost().toString())) >= 0) {
				// error for residual more than cap cost
				addErrorMessage("custom.message","Final NBV must be less than Client's Total Cost ");
				return false;
			}

			if (revisionOEQuoteVO.getFinalNBV().compareTo(BigDecimal.ZERO) < 0) {
				// error for residual is negative
				addErrorMessage("custom.message",talMessage.getMessage("negative.notallowed", "Final NBV"));
				return false;
			}
			return true;
		}
		return true;
	}
	
	private boolean validateDeprFactor() {
		if (revisionOEQuoteVO.getDepreciationFactor() != null) {
			if (revisionOEQuoteVO.getDepreciationFactor().compareTo(BigDecimal.ZERO) <= 0) {
				addErrorMessage("custom.message","Depreciation factor must be greater than 0");
				return false;
			}
			
			return true;
		}
		return true;
	}

	
	public void updateDepFactor() {
		BigDecimal depreciationFactor = profitabilityService.getDepreciationFactor(revisionOEQuoteVO.getCustomerCost(),
				revisionOEQuoteVO.getFinalNBV(), new BigDecimal(getReviseContractRemainingPeriod()));
		depreciationFactor = depreciationFactor.movePointRight(2);
		depreciationFactor = depreciationFactor.setScale(DEPRECIATION_FACTOR_SCALE, RoundingMode.HALF_UP);
		revisionOEQuoteVO.setDepreciationFactor(depreciationFactor);
		revisionOEQuoteVO.setDepreciationFactorToCompare(depreciationFactor);
	}
	
	private boolean validateClientCost() {
		boolean isValid = true;

		if(revisionOEQuoteVO.getRevisedAssessment() == null){
			addErrorMessage("custom.message", "Revision Assessment should be equal to or greater than zero.");
			isValid = false;
		}
		if(revisionOEQuoteVO.getRevisionInterestAdjustment() == null){
			addErrorMessage("custom.message", "Revision Interest Adj should be equal to or greater than zero.");
			isValid = false;
		}
		
		if(revisionOEQuoteVO.getCapitalContribution() == null){
			addErrorMessage("custom.message", "Revised Capital Contribution should be equal to or greater than zero.");
			isValid = false;
		}
				
		if(isValid && (revisionOEQuoteVO.getCapitalContribution().doubleValue() > currentOEQuoteVO.getEffDateNBV().doubleValue() )){
			addErrorMessage("custom.message", "Rev. Capital Contribution should be less than NBV as of Eff. Date");
			isValid = false;
		}
	
		return isValid;
	}
	
	
	public void save() {
		try {
			setSplMsgforStep(null);
			logger.debug("quoteOverviewOEBean:Save:Start");
			boolean isSaveSuccessfull = saveQuote();
			if (!isSaveSuccessfull) {
				return;
			}
			printBtnDisabled = false;
			addSuccessMessage("process.success", "Save");
			logger.debug("quoteOverviewOEBean:Save:End");
		} catch (Exception ex) {
			logger.error(ex);
			handleException("generic.error.occured.while", new String[] { "saving  rental." }, ex, "save");
		}
	}
	
	public void print(){
		if(revisionOEQuoteVO != null){
			if(validIRR()) {
				revisionOEQuoteVO.getQuotationModel().setRequestForAcceptanceBy(getLoggedInUser().getEmployeeNo());
				revisionOEQuoteVO.getQuotationModel().setRequestForAcceptanceDate(new Date());
				revisionOEQuoteVO.getQuotationModel().setRequestForAcceptanceType(OER_ACCEPTANCE_TYPE);
				revisionOEQuoteVO.getQuotationModel().setRequestForAcceptanceYn("Y");
				revisionOEQuoteVO.getQuotationModel().setPrintedDate(new Date());
				revisionOEQuoteVO.getQuotationModel().setPrintedInd("Y");
				try {
					RequestContext.getCurrentInstance().execute("showDocument()");
					quotationService.updateQuotationModel(revisionOEQuoteVO.getQuotationModel());
					revisionQuote = quotationService.getQuotationModelWithCostAndAccessories(revisionQuote.getQmdId());
					revisionOEQuoteVO.setQuotationModel(revisionQuote);
					addSuccessMessage("process.success", "Print");
					isQuoteEditable = false;
					addSuccessMessage("custom.message", "Revision document generated.  Quote can no longer be updated.  Quote will be submitted for acceptance.");
				} catch (Exception ex) {
					logger.error(ex);
					handleException("generic.error.occured.while", new String[] { "printing accepted quote." }, ex, "print");
				}
			} else {
				addSimplErrorMessage("Quote cannot be printed if IRR is below minimum");
			}			
		}
			
			
	}
	
	public void showDocument() {
		try {
			JasperPrint jasperPrint;
			jasperPrint = jasperReportService.getOpenEndContractRevisionDocument(currentQmdId, revisionQmdId);
			jasperReportBean.displayPDFReport(jasperPrint);			
		} catch(Exception ex) {
			logger.error(ex);
			addSimplErrorMessage("Error printing document to screen - " + ex.getMessage());
		}		
	}

	
	public void inRateElement(){
		if (inRateElement){
			RequestContext.getCurrentInstance().execute("PF('confirmInRateVar').show();");
		}else{
			print();
		}
	}
	
	public void showInRateDialog(){
		if(revisionOEQuoteVO.getActualLeaseRate() != null && isDirtyData() == false){
			onShowInRateTaxInputDetails();
			RequestContext.getCurrentInstance().execute("PF('inRateInfoDialogVar').show();");
		}else{
			addSimplErrorMessage("Please calculate and save this quote to see required data for In Rate Sales Tax");
		}
	}
		
	private boolean saveQuote() throws MalBusinessException, Exception {
		
		Map<String, Object> finParamMap = new HashMap<String, Object>();
		finParamMap.put(MalConstants.FIN_PARAM_ADMIN_FACT, revisionOEQuoteVO.getAdminFactor());
		finParamMap.put(MalConstants.FIN_PARAM_INT_ADJ, revisionOEQuoteVO.getInterestAdjustment());
		finParamMap.put(MalConstants.FIN_PARAM_OE_INV_ADJ, revisionOEQuoteVO.getInvoiceAdjustment());

		if (!(validateFinanceParameters(finParamMap)  && validateClientCost() && validateFinalNBV())) {
			return false;
		}

		if (isQuoteCalculated) {
			
			revisionQuote.setInterestRate(revisionOEQuoteVO.getInterestBaseRate().add(revisionOEQuoteVO.getInterestAdjustment(), CommonCalculations.MC));
			revisionQuote.setCapitalContribution(revisionOEQuoteVO.getCapitalContribution());
			revisionQuote.setDepreciationFactor(revisionOEQuoteVO.getDepreciationFactor());		
			
			revisionQuote.setContractPeriod(revisionOEQuoteVO.getTerm());
			revisionQuote.setContractDistance(revisionOEQuoteVO.getDistance());
			revisionQuote.setProjectedMonths(revisionOEQuoteVO.getProjectedReplacementMonths());
			revisionQuote.setContractChangeEventPeriod(getConChangeEventPeriod());
			
			setQuoteModelProperties(revisionQuote);
			// update QuotationcapitalElements for rev assessment and Rev Int Adjustment
			for(QuotationCapitalElement qce : revisionQuote.getQuotationCapitalElements()){
				if(qce.getCapitalElement().getCode().equalsIgnoreCase("OE_REV_INT_ADJ")){
					if(revisionOEQuoteVO.getRevisedIntAdjType().equals(IN_RATE_TYPE)){
						qce.setValue(revisionOEQuoteVO.getRevisionInterestAdjustment());
					}else{
						qce.setValue(BigDecimal.ZERO);
					} 
				}
				if(qce.getCapitalElement().getCode().equalsIgnoreCase("OE_REV_ASSMNT")){
					if(revisionOEQuoteVO.getRevisedAssessmentType().equals(IN_RATE_TYPE)){
						qce.setValue(revisionOEQuoteVO.getRevisedAssessment());
					}else{
						qce.setValue(BigDecimal.ZERO);
					} 
				}
			}
			
			
			// save the calculated quote model
			rentalCalculationService.saveCalculatedRevisedQuoteOE(revisionQuote, revisionSteps, getUpdatedProfitabiltyObject(false), finParamMap);

			QuotationModelFinances qmf = getQuotationModelFinancesForInvoiceAdjustment();
			if (qmf != null) {
				rentalCalculationService.saveHideInvoiceAdjustment(qmf, revisionOEQuoteVO.isHideInvoiceAdjustment());
			}

		
			isRevQuoteWasCalculatedAndSaved = true;
			revisionQuote = quotationService.getQuotationModelWithCostAndAccessories(revisionQuote.getQmdId());
			revisionQuote.setQuoteModelPropertyValues(quoteModelPropertyValueService.findAllByQmdId(revisionQuote.getQmdId()));
			revisionOEQuoteVO.setQuotationModel(revisionQuote);
			quotationProfitability = profitabilityService.getQuotationProfitability(revisionQuote);
		
		}
		
		revisionOEQuoteVO.setIrrApprovedUser(quotationProfitability.getIrrApprovedUser());
		revisionOEQuoteVO.setIrrApprovedDate(quotationProfitability.getIrrApprovedDate());
		setCompareFieldValues();
		isQuoteCalculated = false;
		setDirtyData(false);
		return true;
	}
	
	private boolean validateFinanceParameters(Map<String, Object> finParamMap) {
		if (finParamMap != null && !finParamMap.isEmpty()) {
			Map<String, String> finParamDescMap = new HashMap<String, String>();
			finParamDescMap.put(MalConstants.FIN_PARAM_ADMIN_FACT, "Admin Factor");
			finParamDescMap.put(MalConstants.FIN_PARAM_INT_ADJ, "Interest Adj");
			finParamDescMap.put(MalConstants.FIN_PARAM_OE_INV_ADJ, "Invoice Adj");
			
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			String workclass = ((User) authentication.getPrincipal()).getWorkclass();
			Iterator<String> itr = finParamMap.keySet().iterator();
			boolean isValidationFail = false;
			while (itr.hasNext()) {
				String finKey = (String) itr.next();
				BigDecimal finValue = (BigDecimal) finParamMap.get(finKey);
				finValue = finValue != null ? finValue : BigDecimal.ZERO;
				WorkClassAuthority workClassAuthority = financeParameterService.getWorkClassAuthority(finKey,
						workclass, MalConstants.MODULE_VISION_QL);
				BigDecimal minLimit = workClassAuthority != null ? workClassAuthority.getLowerLimit() : null;
				BigDecimal maxLimit = workClassAuthority != null ? workClassAuthority.getUpperLimit() : null;
				if (minLimit != null || maxLimit != null) {
					if (minLimit != null && finValue.compareTo(minLimit) < 0 || maxLimit != null
							&& finValue.compareTo(maxLimit) > 0) {
						addErrorMessage(
								inputFieldIdMaps.get(finKey),
								"custom.message",
								talMessage.getMessage("fin_param_limit_error",
										new String[] { finParamDescMap.get(finKey) }));
						isValidationFail = true;
					}
				}
			}
			if (isValidationFail) {
				return false;
			}

		}

		return true;
	}
	
	private void setQuoteModelProperties(QuotationModel revisionQuote ){
		
		boolean revAssmtPropfound = false; 
		boolean revIntAdjPropfound = false;  
		
		if(revisionQuote.getQuoteModelPropertyValues() == null){
			revisionQuote.setQuoteModelPropertyValues(new ArrayList<QuoteModelPropertyValue>());
		}
		
		for (QuoteModelPropertyValue quoteModelPropValue : revisionQuote.getQuoteModelPropertyValues()) {			
			
			if(quoteModelPropValue.getQuoteModelProperty().getName().equals(OE_REV_ASSMT)){
				revAssmtPropfound = true;
				quoteModelPropValue.setPropertyValue(String.valueOf(revisionOEQuoteVO.getRevisedAssessment()));	
				
			}else if(quoteModelPropValue.getQuoteModelProperty().getName().equals(OE_REV_INT_ADJ)){
				revIntAdjPropfound = true;
				quoteModelPropValue.setPropertyValue(String.valueOf(revisionOEQuoteVO.getRevisionInterestAdjustment()));
				
			}else if(quoteModelPropValue.getQuoteModelProperty().getName().equals(OE_REV_ASSMT_INRATE_YN)){		
				quoteModelPropValue.setPropertyValue(revisionOEQuoteVO.getRevisedAssessmentType().equals(IN_RATE_TYPE)  ? "Y" : "N" );		
				
			}else if(quoteModelPropValue.getQuoteModelProperty().getName().equals(OE_REV_INT_ADJ_INRATE_YN)){	
				quoteModelPropValue.setPropertyValue(revisionOEQuoteVO.getRevisedIntAdjType().equals(IN_RATE_TYPE)  ? "Y" : "N" );		
			}
		}
	
		if(! revAssmtPropfound ){	

			QuoteModelProperty  revisionAssmtProp  = 	quoteModelPropertyValueService.findQuoteModelProperty(OE_REV_ASSMT);			
			QuoteModelPropertyValue quoteModelPropertyValue = new QuoteModelPropertyValue()	;
			quoteModelPropertyValue.setQuoteModelProperty(revisionAssmtProp);
			quoteModelPropertyValue.setPropertyValue(String.valueOf(revisionOEQuoteVO.getRevisedAssessment()));	
			quoteModelPropertyValue.setQuotationModel(revisionQuote);	
			revisionQuote.getQuoteModelPropertyValues().add(quoteModelPropertyValue);			
			
			QuoteModelProperty  revisionAssmtInRateYNProp  = 	quoteModelPropertyValueService.findQuoteModelProperty(OE_REV_ASSMT_INRATE_YN);			
			QuoteModelPropertyValue revisionAssmtInRateYNPropValue = new QuoteModelPropertyValue();
			revisionAssmtInRateYNPropValue.setQuoteModelProperty(revisionAssmtInRateYNProp);
			revisionAssmtInRateYNPropValue.setPropertyValue(revisionOEQuoteVO.getRevisedAssessmentType().equals(IN_RATE_TYPE)  ? "Y" : "N");		
			revisionAssmtInRateYNPropValue.setQuotationModel(revisionQuote);
			revisionQuote.getQuoteModelPropertyValues().add(revisionAssmtInRateYNPropValue);
		}
		
		if(! revIntAdjPropfound ){				
			
			QuoteModelProperty  revisedIntAdjProp  = 	quoteModelPropertyValueService.findQuoteModelProperty(OE_REV_INT_ADJ);			
			QuoteModelPropertyValue revisedIntAdjPropValue = new QuoteModelPropertyValue()	;
			revisedIntAdjPropValue.setQuoteModelProperty(revisedIntAdjProp);
			revisedIntAdjPropValue.setPropertyValue(String.valueOf(revisionOEQuoteVO.getRevisionInterestAdjustment()));	
			revisedIntAdjPropValue.setQuotationModel(revisionQuote);
			revisionQuote.getQuoteModelPropertyValues().add(revisedIntAdjPropValue);		
						
			QuoteModelProperty  revisedIntAdjInRateYNProp  = 	quoteModelPropertyValueService.findQuoteModelProperty(OE_REV_INT_ADJ_INRATE_YN);			
			QuoteModelPropertyValue revisedIntAdjInRateYNValue = new QuoteModelPropertyValue()	;
			revisedIntAdjInRateYNValue.setQuoteModelProperty(revisedIntAdjInRateYNProp);		
			revisedIntAdjInRateYNValue.setPropertyValue(revisionOEQuoteVO.getRevisedIntAdjType().equals(IN_RATE_TYPE)  ? "Y" : "N" );		
			revisedIntAdjInRateYNValue.setQuotationModel(revisionQuote);
			revisionQuote.getQuoteModelPropertyValues().add(revisedIntAdjInRateYNValue);
		}
	
	}
	
	
	public String cancel(){
    	return super.cancelPage();      	
    }

	@Override
	protected void loadNewPage() {
		thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_OER);
		thisPage.setPageUrl(ViewConstants.OPEN_END_REVISION);

		if (thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_QUOTE_MODEL_ID) != null) {
			currentQmdId = (Long) thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_QUOTE_MODEL_ID);
		}
		if (thisPage.getInputValues().get(ViewConstants.VIEW_REV_PARAM_QUOTE_MODEL_ID) != null) {
			revisionQmdId = (Long) thisPage.getInputValues().get(ViewConstants.VIEW_REV_PARAM_QUOTE_MODEL_ID);
		} 
		if (thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_FORWARDED_PAGE) != null) {
			forwardPage = (Boolean) thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_FORWARDED_PAGE);
		}		
	}
	
	public void viewServiceElements() {
		logger.debug("OpenEndQuoteRevisionBean:viewServiceElements:Start");
		Map<String, Object> restoreStateValues = getCurrentPageRestoreStateValuesMap();
		saveRestoreStateValues(restoreStateValues);
		Map<String, Object> nextPageValues = new HashMap<String, Object>();
		nextPageValues.put(ViewConstants.VIEW_PARAM_QUOTE_MODEL_ID, revisionQmdId);
		nextPageValues.put(ViewConstants.VIEW_PARAM_PREVIOUS_PAGE, ViewConstants.DISPLAY_NAME_OER);
		nextPageValues.put(ViewConstants.CONTRACT_CHANGE_PERIOD, getConChangeEventPeriod() - 1);
		nextPageValues.put("PRINTED_IND", revisionQuote.getPrintedInd());
		
		saveNextPageInitStateValues(nextPageValues);
		logger.debug("OpenEndQuoteRevisionBean:viewServiceElements:End");
		forwardToURL(ViewConstants.SERVICE_ELEMENTS);
	}	
	
	private Map<String, Object> getCurrentPageRestoreStateValuesMap() {
		Map<String, Object> restoreStateValues = new HashMap<String, Object>();
		restoreStateValues.put(ViewConstants.VIEW_PARAM_QUOTE_MODEL_ID, currentQmdId);
		restoreStateValues.put(ViewConstants.VIEW_REV_PARAM_QUOTE_MODEL_ID, revisionQmdId);
		restoreStateValues.put(ViewConstants.VIEW_PARAM_FORWARDED_PAGE, forwardPage);
		return restoreStateValues;
	}	

	public void onLoadRejectReason() {
		for(QuoteRejectCode qrc : getRejectReasonList()){
			if(qrc.getRejectCode().equalsIgnoreCase("INCORRECT")){
				setSelectedRejectReason(qrc);
				break;
			}
		}
	}
	
	public void rejectRevision() {
		try {
			
			if (MALUtilities.isEmpty(this.getSelectedRejectReason())) {
				addErrorMessageSummary("custom.message", "Please select a reason from the list");
				RequestContext context = RequestContext.getCurrentInstance();
				context.addCallbackParam("failure", true);
			}
			quotationService.rejectQuoteWithReason(revisionQmdId, this.getSelectedRejectReason().getRejectCode());
			addSuccessMessage("custom.message", "Quote " + revisionOEQuoteVO.getQuote() + " has been rejected successfully.");
			ProcessStageObject pso = processStageService.getStagedObject(OrderToDeliveryProcessStageEnum.ACCEPTANCE, revisionOEQuoteVO.getQuotationModel().getQmdId());
			if(pso != null) {
				processStageService.excludeStagedObject(pso);				
			}
			
		} catch (Exception e) {
			handleException("generic.error.occured.while", new String[] { " rejecting the quote" }, e, null);
		}
	}

	
	private boolean validToCalc() {
		boolean valid = true;
		if(revisionOEQuoteVO.getTerm() == null || revisionOEQuoteVO.getTerm() < 1) {
			addErrorMessage("termId", "custom.message", "Term must be greater than 0");
			valid = false;			
		}
		if(revisionOEQuoteVO.getTerm() != null && revisionOEQuoteVO.getTerm() < conChangeEventPeriod) {
			addErrorMessage("termId", "custom.message", "Revision Term must be greater than the months on contract as of revision effective date");
			valid = false;			
		}
		if(revisionOEQuoteVO.getProjectedReplacementMonths() == null || revisionOEQuoteVO.getProjectedReplacementMonths() < 1) {
			addErrorMessage("projectedReplacementMonthsId", "custom.message", "Proj Repl Months must be greater than 0");
			valid = false;			
		}
		if(revisionOEQuoteVO.getDistance() == null || revisionOEQuoteVO.getDistance() < 1) {
			addErrorMessage("expectedMilesId", "custom.message", "Expected Miles must be greater than 0");
			valid = false;			
		}
		
		return valid;
	}
	
	private QuotationModelFinances getQuotationModelFinancesForInvoiceAdjustment() {
		return financeParameterService.getQuotationModelFinances(revisionQuote.getQmdId(),MalConstants.FIN_PARAM_OE_INV_ADJ);
	}
	
	private QuotationProfitability getUpdatedProfitabiltyObject(boolean saveApprovedMin) {

		if (quotationProfitability == null) {
			quotationProfitability = new QuotationProfitability();
			quotationProfitability.setQuotationModel(revisionQuote);
			quotationProfitability.setProfitBase(revisionOEQuoteVO.getBaseIrr());
			quotationProfitability.setProfitAdjustment(revisionOEQuoteVO.getProfitAdj());
			quotationProfitability.setProfitAmount(revisionOEQuoteVO.getActualIrrValue());
			quotationProfitability.setProfitSource("N");
			quotationProfitability.setProfitType("P");
		} else {
			if(saveApprovedMin == false){
				quotationProfitability.setProfitBase(revisionOEQuoteVO.getBaseIrr());
				quotationProfitability.setProfitAdjustment(revisionOEQuoteVO.getProfitAdj());
				quotationProfitability.setProfitAmount(revisionOEQuoteVO.getActualIrrValue());
			} else{

				if (revisionOEQuoteVO.getIrrApprovedLimitValue() != null) {
					if (quotationProfitability.getIrrApprovedLimit() == null
							|| (quotationProfitability.getIrrApprovedLimit() != null && revisionOEQuoteVO
									.getIrrApprovedLimitValue().compareTo(quotationProfitability.getIrrApprovedLimit()) != 0)) {
						quotationProfitability.setIrrApprovedLimit(revisionOEQuoteVO.getIrrApprovedLimitValue());
						String employeeNo = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
								.getEmployeeNo();
						quotationProfitability.setIrrApprovedUser(employeeNo);
						quotationProfitability.setIrrApprovedDate(new Date());
					}
				} else {
					quotationProfitability.setIrrApprovedLimit(null);
					quotationProfitability.setIrrApprovedUser(null);
					quotationProfitability.setIrrApprovedDate(null);
				}
				
				
				
			}
			

		}
		return quotationProfitability;
	}
	
	private boolean getEditStatus() {
		boolean editStatus = true;

		if(revisionQuote.getPrintedDate() != null || !hasPermission()) {
			editStatus=false;
		}
				
		return editStatus;
	}
	
	@Override
	protected void restoreOldPage() {
		this.currentQmdId = (Long) thisPage.getRestoreStateValues().get(ViewConstants.VIEW_PARAM_QUOTE_MODEL_ID);
		this.revisionQmdId = (Long) thisPage.getRestoreStateValues().get(ViewConstants.VIEW_REV_PARAM_QUOTE_MODEL_ID);
		if (thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_FORWARDED_PAGE) != null) {
			forwardPage = (Boolean) thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_FORWARDED_PAGE);
		}
		// We have only one case where page can be restore from service element page.Change if needed in future
		try {	
			revisionQuote = quotationService.getQuotationModelWithCostAndAccessories(revisionQmdId);
			if(revisionQuote.getReCalcNeeded().equals("Y")){
				List<QuotationElement> updatedServiceElement = calculateInRateServiceElement();	
				if(updatedServiceElement.size() > 0){
					quotationService.updateQuotationElements(updatedServiceElement);	
				}
				addInfoMessage("quote.pricechnage.recalculate.equip");
			}
		} catch (Exception ex) {
			handleException("generic.error.occured.while", new String[] { "calculate/save in rate treatment type service element " }, ex, null);
		}
		
	}

	
	private void onShowInRateTaxInputDetails() {
		if(driverAddress == null && fleetMaster != null){
			driverAddress = driverService.getDriverAddress(driverService.getActiveDriverForUnit(fleetMaster).getDrvId(), DriverService.GARAGED_ADDRESS_TYPE);
			String RPT_NEW_LINE_CHAR = "<br/>";
			String address = DisplayFormatHelper.formatAddressForTable(driverAddress.getBusinessAddressLine(),
					driverAddress.getAddressLine1(), driverAddress.getAddressLine2(), null, null,
					driverAddress.getCityDescription(),
					driverAddress.getRegionCode().getRegionCodesPK().getRegionCode(), driverAddress.getPostcode(),
					RPT_NEW_LINE_CHAR);
			currentOEQuoteVO.setDriverAddress(address);
		}
		
	}
	public void cancelStepsDefinition() {
		definitionSteps = null;
		if (!MALUtilities.isEmpty(getSplMsgforStep())){
			addInfoMessage("quote.calculation.success");
		}
	}

	public void onEditStepsDefinition() {
		setSplMsgforStep(null);
		definitionSteps = new ArrayList<QuotationStepStructureVO>();
		
		definitionSteps = getCurrentContractStepsforRevisionSteps(definitionSteps);
		for (QuotationStepStructureVO step : definitionSteps) {
			step.setEditable(false);
		}

		if(revisionSteps.size() > 1){//ui has more than one step for rev quote
			for (QuotationStepStructureVO stepStructureVO : revisionSteps) {
				stepStructureVO.setNetPeriod(stepStructureVO.getToPeriod() - stepStructureVO.getFromPeriod() + 1);	
				definitionSteps.add(stepStructureVO);
			}			
			for (int i = 0; i < MAX_NEW_STEPS - revisionSteps.size(); i++) {
				definitionSteps.add( new QuotationStepStructureVO());
			}
			
		}else{//first time edit step
			
			QuotationStepStructureVO modifiedStep = null;
			Long remainingTerm = revisionOEQuoteVO.getTerm() - conChangeEventPeriod + 1;
			Long maxRemainingStepAllowed = new Long(MAX_NEW_STEPS);
			
			//making break step due to revision
			for(QuotationStepStructureVO step : currentSteps){
				if(step.getFromPeriod() < conChangeEventPeriod && step.getToPeriod() > conChangeEventPeriod){
					modifiedStep = new QuotationStepStructureVO(); //first step of rev quote
					modifiedStep.setFromPeriod(conChangeEventPeriod);
					if(remainingTerm > step.getOrigNetPeriod()){
						modifiedStep.setToPeriod(step.getToPeriod());
					}else{
						modifiedStep.setToPeriod(modifiedStep.getFromPeriod() + remainingTerm -1);
					} 
					
					definitionSteps.add(modifiedStep);
					
					remainingTerm = remainingTerm - modifiedStep.getOrigNetPeriod() ;
					maxRemainingStepAllowed = maxRemainingStepAllowed -1;
				}
			}
			
			long DEFAULT_STEP_SIZE = 12;	
			for (int i = 1; i <= maxRemainingStepAllowed; i++) {
				modifiedStep = new QuotationStepStructureVO();
				
				if (remainingTerm > DEFAULT_STEP_SIZE && i != maxRemainingStepAllowed) {
					modifiedStep.setNetPeriod(DEFAULT_STEP_SIZE);
				} else if(remainingTerm > 0){
					modifiedStep.setNetPeriod(remainingTerm);
				}
				definitionSteps.add(modifiedStep);
				remainingTerm = remainingTerm - DEFAULT_STEP_SIZE;		
				if( remainingTerm < 0 ){
					remainingTerm = 0L ; // to add empty item
				}
			}
			
		}
		
		
		
		int count = 1;
		for (QuotationStepStructureVO stepStructureVO : definitionSteps) {
			if(stepStructureVO.getToPeriod() != null && stepStructureVO.getFromPeriod()  != null){
				stepStructureVO.setNetPeriod(stepStructureVO.getOrigNetPeriod());	
			}
			stepStructureVO.setStepCount(count++);
		}
			 
	}

	public void updateStepsAndRecalculateQuote() {
		setSplMsgforStep(null);
		boolean success = true;
		try {
			success = isStepsAreValid();
			if (success) {
				
				revisionSteps.clear();
				revStepsForUi = getCurrentContractStepsforRevisionSteps(revStepsForUi);
				
				long start = 1;
				for (QuotationStepStructureVO qssVO : definitionSteps) {
					if(start < conChangeEventPeriod){
						start = start + qssVO.getNetPeriod();
						continue;
					}

					if (qssVO.getNetPeriod() != null && qssVO.getNetPeriod() > 0) {
						qssVO.setFromPeriod(start);
						qssVO.setToPeriod(start + qssVO.getNetPeriod() - 1);
						revisionSteps.add(qssVO);
						start = start + qssVO.getNetPeriod();
					}
				}
				
				revStepsForUi.addAll(revisionSteps);
				calc();
				setSplMsgforStep(talMessage.getMessage("quote.calculation.success"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			success = false;
			super.addErrorMessageSummary("generic.error.occured.while", " calculating quote");

		}
		if (success == false) {
			RequestContext context = RequestContext.getCurrentInstance();
			context.addCallbackParam("failure", true);
		}
	}

	private boolean isStepsAreValid() {

		long totalStepPeriod = 0;
		for (QuotationStepStructureVO qssVO : definitionSteps) {
			if (qssVO.getNetPeriod() != null)
				totalStepPeriod = totalStepPeriod + qssVO.getNetPeriod();
		}

		if (totalStepPeriod != revisionOEQuoteVO.getTerm()) {
			super.addErrorMessageSummary("quote.openend.step.sum.error");
			return false;

		}

		int missingStepPos = -1;
		for (QuotationStepStructureVO qssVO : definitionSteps) {
			if (qssVO.getNetPeriod() == null)
				missingStepPos = qssVO.getStepCount();
			else {
				if (missingStepPos > 0 && missingStepPos < qssVO.getStepCount()) {
					super.addErrorMessageSummary("quote.openend.step.define.error");
					return false;

				}
			}

		}

		return true;
	}

	public boolean isPrintBtnDisabled() {
		if(revisionOEQuoteVO.getActualLeaseRate() == null){

			printBtnDisabled = true;
		}
		if(revisionQuote != null && revisionQuote.getPrintedDate() !=null) {
			printBtnDisabled = false;
		}
		
		return printBtnDisabled;
	}

	public boolean validIRR(){
		
		if (revisionOEQuoteVO.getActualIrrValue().compareTo(revisionOEQuoteVO.getMinimumIrr()) >= 0){
			return true;
		} else {
			if(revisionOEQuoteVO.getIrrApprovedLimitValue() != null){
				if (revisionOEQuoteVO.getActualIrrValue().compareTo(revisionOEQuoteVO.getIrrApprovedLimitValue()) >= 0){
					return true;
				}
			}
		}
		return false; 
	}

	public void storeNewQprAndRecalculateQuote() {
		
		if (!MALUtilities.isEmpty(revisionQuote)) {
				RequestContext context = RequestContext.getCurrentInstance();
				boolean hasError = false;
				String errMsg = new String();
	
				try {
					errMsg = openEndService.compareProfilesForMigration(currentOEQuoteVO.getQuotationModel().getQuotation().getQuotationProfile().getQprId(), Long.valueOf(newQuoteProfileId));
					if (!MALUtilities.isEmpty(errMsg)) {
						throw new Exception(errMsg);	
					}
					
					revisionQuote.getQuotation().setQuotationProfile(quotationProfileDAO.findById(Long.valueOf(newQuoteProfileId)).orElse(null));
					
					QuoteModelPropertyValue quoteModelPropertyValue = new QuoteModelPropertyValue();					
					quoteModelPropertyValue.setQuotationModel(quotationService.getQuotationModel(revisionQuote.getQmdId()));
					quoteModelPropertyValue.setQuoteModelProperty(quoteModelPropertyValueService.findQuoteModelProperty(QuoteModelPropertyEnum.QUOTE_PROFILE.getName()));
					quoteModelPropertyValue.setPropertyValue(Long.toString(revisionQuote.getQuotation().getQuotationProfile().getQprId()));						
					quoteModelPropertyValueService.populateQuoteModelPropertyValue(quoteModelPropertyValue);
					
					printBtnDisabled = true;
					loadExistingRevision();
					isQuoteEditable = getEditStatus();
					setMinimumIrrLimit();
					setCompareFieldValues();
					populateInterestRateForNewQuote();	
					calc();
					
				}  catch (Exception e) {
					logger.error(e);
					hasError = true;
					errMsg = e.getMessage();
				} finally{
					if (hasError) {
						try {
							quoteModelPropertyValueService.removeQuoteModelPropertyValue(revisionQuote.getQmdId(), QuoteModelPropertyEnum.QUOTE_PROFILE.getName());
						} catch (MalBusinessException mbex) {
							errMsg = "";
							addSimplErrorMessage("Error occured while removing stored new quotation profile from the quote -" + mbex.getMessage());
						}
						if (!MALUtilities.isEmpty(errMsg)) {
							addSimplErrorMessage("Error occured while storing new quotation profile for the quote - " + errMsg);	
						}
						context.addCallbackParam("failure", true);							
					}
				}
			}
	}
	
	public QuoteOEVO getcurrentOEQuoteVO() {
		return currentOEQuoteVO;
	}

	
	public List<QuotationStepStructureVO> getCurrentSteps() {
		return currentSteps;
	}

	public void setCurrentSteps(List<QuotationStepStructureVO> currentSteps) {
		this.currentSteps = currentSteps;
	}

	public List<QuotationStepStructureVO> getRevisionSteps() {
		return revisionSteps;
	}

	public void setRevisionSteps(List<QuotationStepStructureVO> revisionSteps) {
		this.revisionSteps = revisionSteps;
	}

	public QuoteOEVO getRevisionOEQuoteVO() {
		return revisionOEQuoteVO;
	}

	public void setRevisionOEQuoteVO(QuoteOEVO revisionOEQuoteVO) {
		this.revisionOEQuoteVO = revisionOEQuoteVO;
	}

	public String getRevisionStatus() {
		return revisionStatus;
	}

	public void setRevisionStatus(String revisionStatus) {
		this.revisionStatus = revisionStatus;
	}

	public QuotationModel getRevisionQuote() {
		return revisionQuote;
	}

	public void setRevisionQuote(QuotationModel revisionQuote) {
		this.revisionQuote = revisionQuote;
	}

	public boolean isQuoteCalculated() {
		return isQuoteCalculated;
	}

	public void setQuoteCalculated(boolean isQuoteCalculated) {
		this.isQuoteCalculated = isQuoteCalculated;
	}

	public long getReviseContractRemainingPeriod() {
		return reviseContractRemainingPeriod;
	}

	public void setReviseContractRemainingPeriod(
			long reviseContractRemainingPeriod) {
		this.reviseContractRemainingPeriod = reviseContractRemainingPeriod;
	}

	public List<QuotationStepStructureVO> getRevStepsForUi() {
		return revStepsForUi;
	}

	public void setRevStepsForUi(List<QuotationStepStructureVO> revStepsForUi) {
		this.revStepsForUi = revStepsForUi;
	}

	public boolean isQuoteEditable() {
		return isQuoteEditable;
	}

	public void setQuoteEditable(boolean isQuoteEditable) {
		this.isQuoteEditable = isQuoteEditable;
	}

	public BigDecimal getCurrentCustomerCost() {
		return currentCustomerCost;
	}

	public void setCurrentCustomerCost(BigDecimal currentCustomerCost) {
		this.currentCustomerCost = currentCustomerCost;
	}
	public boolean isRevQuoteWasCalculatedAndSaved() {
		return isRevQuoteWasCalculatedAndSaved;
	}

	public void setRevQuoteWasCalculatedAndSaved(boolean isRevQuoteWasCalculatedAndSaved) {
		this.isRevQuoteWasCalculatedAndSaved = isRevQuoteWasCalculatedAndSaved;
	}
	
	public void setPrintBtnDisabled(boolean printBtnDisabled) {
		this.printBtnDisabled = printBtnDisabled;
	}
	public String getSplMsgforStep() {
		return splMsgforStep;
	}

	public void setSplMsgforStep(String splMsgforStep) {
		this.splMsgforStep = splMsgforStep;
	}

	public List<QuotationStepStructureVO> getDefinitionSteps() {
		return definitionSteps;
	}

	public void setDefinitionSteps(List<QuotationStepStructureVO> definitionSteps) {
		this.definitionSteps = definitionSteps;
	}	
	
	public long getConChangeEventPeriod() {
		return conChangeEventPeriod;
	}

	public void setConChangeEventPeriod(long conChangeEventPeriod) {
		this.conChangeEventPeriod = conChangeEventPeriod;
	}

	public List<QuoteRejectCode> getRejectReasonList() {
		return rejectReasonList;
	}

	public void setRejectReasonList(List<QuoteRejectCode> rejectReasonList) {
		this.rejectReasonList = rejectReasonList;
	}

	public QuoteRejectCode getSelectedRejectReason() {
		return selectedRejectReason;
	}

	public void setSelectedRejectReason(QuoteRejectCode selectedRejectReason) {
		this.selectedRejectReason = selectedRejectReason;
	}

	public boolean isForwardPage() {
		return forwardPage;
	}

	public void setForwardPage(boolean forwardPage) {
		this.forwardPage = forwardPage;
	}

	public boolean isInRateElement() {
		return inRateElement;
	}

	public void setInRateElement(boolean inRateElement) {
		this.inRateElement = inRateElement;
	}

	public String getNewIRRLimit() {
		return newIRRLimit;
	}

	public void setNewIRRLimit(String newIRRLimit) {
		this.newIRRLimit = newIRRLimit;
	}

	public ContractLine getLatestContractLine() {
		return latestContractLine;
	}

	public void setLatestContractLine(ContractLine latestContractLine) {
		this.latestContractLine = latestContractLine;
	}

	public long getConCompletedBillingPeriod() {
		return conCompletedBillingPeriod;
	}

	public void setConCompletedBillingPeriod(long conCompletedBillingPeriod) {
		this.conCompletedBillingPeriod = conCompletedBillingPeriod;
	}

	public UserContextDAO getUserContextDAO() {
		return userContextDAO;
	}

	public void setUserContextDAO(UserContextDAO userContextDAO) {
		this.userContextDAO = userContextDAO;
	}
	
	public String getNewQuoteProfileId() {
		return newQuoteProfileId;
	}


	public void setNewQuoteProfileId(String newQuoteProfileId) {
		this.newQuoteProfileId = newQuoteProfileId;
	}

}