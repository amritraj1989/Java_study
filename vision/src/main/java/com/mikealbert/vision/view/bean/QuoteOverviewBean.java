package com.mikealbert.vision.view.bean;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.mikealbert.common.CommonCalculations;
import com.mikealbert.common.MalConstants;
import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.dao.ProductDAO;
import com.mikealbert.data.dao.ProductTypeCodeDAO;
import com.mikealbert.data.dao.QuotationModelDAO;
import com.mikealbert.data.dao.QuotationModelFinancesDAO;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.LeaseElement;
import com.mikealbert.data.entity.Product;
import com.mikealbert.data.entity.ProductTypeCode;
import com.mikealbert.data.entity.QuotationElement;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.QuotationModelFinances;
import com.mikealbert.data.entity.QuotationProfitability;
import com.mikealbert.data.entity.User;
import com.mikealbert.data.entity.WorkClassAuthority;
import com.mikealbert.data.vo.ServiceElementsVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;
import com.mikealbert.service.FinanceParameterService;
import com.mikealbert.service.FleetMasterService;
import com.mikealbert.service.ProfitabilityService;
import com.mikealbert.service.QuotationService;
import com.mikealbert.service.RentalCalculationService;
import com.mikealbert.service.ServiceElementService;
import com.mikealbert.service.vo.QuoteCost;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.service.CapitalCostOverviewService;
import com.mikealbert.vision.view.ViewConstants;
import com.mikealbert.vision.vo.QuoteOverviewVO;

@Component
@Scope("view")
public class QuoteOverviewBean extends StatefulBaseBean {

	private static final long serialVersionUID = 2437933046906999010L;
	private MalLogger logger = MalLoggerFactory.getLogger(this.getClass());

	@Resource
	private QuotationService quotationService;
	@Resource
	private FleetMasterService fleetMasterService;
	@Resource
	private ProductTypeCodeDAO productTypeCodeDAO;
	@Resource
	private ProfitabilityService profitabilityService;
	@Resource
	private RentalCalculationService rentalCalculationService;
	@Resource
	private QuotationModelDAO quotationModelDAO;
	@Resource
	private CapitalCostOverviewService capitalCostOverviewService;
	@Resource
	private FinanceParameterService financeParameterService;
	@Resource
	private ServiceElementService serviceElementService;
	@Resource
	private ProductDAO	productDAO;
	@Resource
	QuotationModelFinancesDAO quotationModelFinancesDAO;

	private final String COST_FIXED = "Fixed";
	private final String COST_VARIABLE = "Variable";
	private final String FACTORY = "Factory Order";
	private final String LOCATE = "Locate Order";
	private final int DECIMAL_DIG_PRECISION_THREE = 3;
	private final int DECIMAL_DIG_PRECISION_TWO = 2;
	
	private Long qmdId;
	private boolean forwaredPage = false;

	private QuotationModel quotationModel;
	private QuotationModel calcQuotationModel;
	private QuotationProfitability quotationProfitability;
	private FleetMaster fleetMaster;
	private Long qmReplacementFmsId;
	private QuoteOverviewVO quoteOverviewVO;
	
	private int baseResTextSize;
	private int leaseRateTextSize;
	
	private boolean reCalculated = false;	
	private boolean isRentalCalculationApplicable = false;
	private boolean disableCalculateAndSave = false;
	private boolean includeTempEquipmentsOnCapCost = false;
	private boolean allowSave = true;
	private boolean recalculateAndSave = false;
	private boolean isContractPeriodUpdated = false;
	
	private String productCode;
	
	Map<String, String> inputFieldIdMaps = new HashMap<String, String>();
	Map<String, String> finParamDescMap = new HashMap<String, String>();;
	
	private	boolean	invalidAccessPath=	false; 
	private final String ORDER_TYPE_LOCATE = "L";
	
	final static String GROWL_UI_ID = "growl";
	private boolean serviceElementChanged = false;
	private List<ServiceElementsVO> availableElements;
	// determines whether this adjustments area is shown on theUI.
	private boolean hideAdjustmentsArea = false;
	// tracks whether the invoice adjustments is hidden or show on the reports
	private boolean hideInvoiceAdjustment = false;
	private boolean isNewQuote = false;
	private boolean zeroRentalFlag = false; //HD-475 
	
	@PostConstruct
	public void init() {
		invalidAccessPath	= false;
		super.openPage();
		try {
			logger.debug("init is called");
			// UI fields maps; used as helper code to relate a back end value to a UI field
			inputFieldIdMaps.put(MalConstants.FIN_PARAM_GRD_DEL_RECHARGE, "grdDelRcAmt");
			inputFieldIdMaps.put(MalConstants.FIN_PARAM_AM_ADJ, "invoiceAdj");
			inputFieldIdMaps.put("APPROVED_MIN_IRR_LBL_ID", "appMinIrrLabelId");
			inputFieldIdMaps.put("APPROV_MIN_IRR", "appMinIrr");
			inputFieldIdMaps.put("IRR", "irr");
			inputFieldIdMaps.put("BASE_RESIDUAL", "baseResidual");
			inputFieldIdMaps.put("LEASE_RATE", "rate");
			inputFieldIdMaps.put("CAPITAL_CONTRIBUTION", "capitalContribution");
			// finance parameter Maps used to map a finance parameter to a Description for error messages			
			finParamDescMap.put(MalConstants.FIN_PARAM_GRD_DEL_RECHARGE, "Delivery Recharge Amt");
			finParamDescMap.put(MalConstants.FIN_PARAM_AM_ADJ, "Purch Inv Adj");

			if (qmdId != null) {
				baseResTextSize	= 12;
				leaseRateTextSize = 12;
				quoteOverviewVO = new QuoteOverviewVO();
				quotationModel = quotationService.getQuotationModelWithCostAndAccessories(qmdId);
				String leaseType = quotationService.getLeaseType(qmdId);
				
				// Following if condition has been added for PD-655 to restrict any calculation and save for Locate Order Type when quoting AM.
				if(!(!MALUtilities.isEmpty(quotationModel) 
						&& quotationModel.getOrderType().equals(ORDER_TYPE_LOCATE) 
						&& (leaseType.equalsIgnoreCase("PUR")))){
					/* Here we are trying to find out based on formulae in lease element
					** weather the Rental need to be calculate or not 
					*/
					for (QuotationElement quoEle : quotationModel.getQuotationElements()) {
						LeaseElement leaseElement = quoEle.getLeaseElement() ;
						if(leaseElement != null  && RentalCalculationService.VMP_FORMULAE.equalsIgnoreCase(leaseElement.getFormulae())){
							zeroRentalFlag = true;
						}		
					}		
					
					quotationProfitability = profitabilityService.getQuotationProfitability(quotationModel);
					QuotationElement	mainQuoteElement = rentalCalculationService.getMainQuotationModelElement(quotationModel);
					setProductCode(quotationModel.getQuotation().getQuotationProfile().getPrdProductCode());
					
					if (mainQuoteElement == null ) {
						isNewQuote = true;
						quoteOverviewVO.setBaseIrr(CommonCalculations.getRoundedValue(getHurdleRate(quotationModel),RentalCalculationService.IRR_CALC_DECIMALS));
						quoteOverviewVO.setProfitAdj(CommonCalculations.getRoundedValue(getProfitAdj(quotationModel),RentalCalculationService.IRR_CALC_DECIMALS));
						quoteOverviewVO.setActualIrrValue(quoteOverviewVO.getMinimumIrr());
						logger.debug("new quote is loading .." + qmdId);
						
						// load either the default or the stored data.
						loadAdjustmentsData();
						
						// save adjustments (finance parameter and capital element) so they are there for the calculation, if needed
						// but only if this quote type has adjustments
						if(!hideAdjustmentsArea){
							rentalCalculationService.saveFinanceParamOnQuote(quoteOverviewVO.getInvoiceAdjustment(), MalConstants.FIN_PARAM_AM_ADJ,
				        			quotationModel);
						}
	
						// In memory calculation, not saved in database yet
						//check for formal extension
						if ((!rentalCalculationService.isFormalExtension(quotationModel)) && (!rentalCalculationService.isQuoteForPartialCalculation(quotationModel))){
							//supposedly a call to availableElements will do nothing as all of the elements are already setup in Willow for a new quote; but we are
							//seeing differences so I am going to always "fix" them here.
							// use quotationModel not calcQuotationModel because we want to detect changes /vs what is from the DB not what we are calculating
							availableElements = serviceElementService.determineElementsWithChanges(quotationModel);
							calcQuotationModel = rentalCalculationService.getCalculatedQuotationModel(qmdId, true,quoteOverviewVO.getCapitalContribution());
						}else{
							calcQuotationModel = rentalCalculationService.getCalculatedQuotationModel(qmdId, false,quoteOverviewVO.getCapitalContribution());
						}
	
						// See if rental calculation is allowed
						isRentalCalculationApplicable = rentalCalculationService.isCalcRentalCalculationApplicable(calcQuotationModel);
						disableCalculateAndSave = rentalCalculationService.isQuoteEditable(quotationModel) ? false : true;
						
						BigDecimal monthlyRental = profitabilityService.calculateMonthlyRental(calcQuotationModel,
								calcQuotationModel.getCalculatedCapCost(), calcQuotationModel.getResidualValue(),
								quoteOverviewVO.getActualIrrValue());
						if( isRentalCalculationApplicable == false){
						    try {
	    						BigDecimal calculatedIRR = profitabilityService.calculateIRR(calcQuotationModel,calcQuotationModel.getCalculatedCapCost(),
	    									 calcQuotationModel.getResidualValue(), monthlyRental);
	    						quoteOverviewVO.setActualIrrValue(calculatedIRR);
						    } catch (Exception e) {
						    	quoteOverviewVO.setActualIrrValue(BigDecimal.ZERO);
						    }
	    						
						}
						calcQuotationModel.setCalculatedMontlyRental(monthlyRental);
						
						//check for formal extension
						if ((!rentalCalculationService.isFormalExtension(quotationModel)) &&  (!rentalCalculationService.isQuoteForPartialCalculation(quotationModel))){
							// should not been needed for a new quote; but we are seeing differences
							calcQuotationModel.getQuotation().setMulQuoteElems(null);
							
							if(serviceElementService.elementsHaveChanges(availableElements)){
								serviceElementService.saveOrUpdateServiceElements(calcQuotationModel, availableElements);
							}
						}
						
						rentalCalculationService.saveCalculatedQuote(quotationModel,calcQuotationModel, quotationModel.getResidualValue(),
								getUpdatedProfitabiltyObject(false), true);
						
						// save the flag for hiding invoice adjustments from client facing documents 
						QuotationModelFinances qmf = getQuotationModelFinancesForInvoiceAdjustment();
						if (qmf != null) {
							rentalCalculationService.saveHideInvoiceAdjustment(qmf, hideInvoiceAdjustment);
						}					
						
						quotationModel = quotationService.getQuotationModelWithCostAndAccessories(qmdId);
						quotationProfitability = profitabilityService.getQuotationProfitability(quotationModel);
						calcQuotationModel = null;
						addInfoMessage("new.quote.calculation.success");
					} else {	
						isNewQuote = false;
						disableCalculateAndSave = rentalCalculationService.isQuoteEditable(quotationModel) ? false : true;
						isRentalCalculationApplicable = rentalCalculationService.isCalcRentalCalculationApplicable(quotationModel);
						
						// load either the default or the stored data.
						loadAdjustmentsData();
						
						if(!hideAdjustmentsArea){
							rentalCalculationService.saveFinanceParamOnQuote(quoteOverviewVO.getInvoiceAdjustment(), MalConstants.FIN_PARAM_AM_ADJ,
				        			quotationModel);
						}
						
						QuotationModelFinances qmf = getQuotationModelFinancesForInvoiceAdjustment();
						if (qmf != null) {
							rentalCalculationService.saveHideInvoiceAdjustment(qmf, hideInvoiceAdjustment);
						}
						
						if (!disableCalculateAndSave) {	
							// if it's in a status where you can save it
							// see if service elements have changed and if so set a flag
							if ((!rentalCalculationService.isFormalExtension(quotationModel)) &&  (!rentalCalculationService.isQuoteForPartialCalculation(quotationModel))){
								serviceElementChanged = hasServiceElementsChanged(quotationModel);
							}
							
							//for RC-740, if there is change in terms, recalculate with new interest rates
							if(quotationModel.getContractPeriod().longValue() != mainQuoteElement.getNoRentals().longValue()){
								isContractPeriodUpdated	= true;
							}
							//For RC-1283/1287
							 Boolean isCapitalCostChange = false;
							if (quotationModel.getOrderType() != null
									&& !quotationModel.getOrderType().equals(ORDER_TYPE_LOCATE)) {
								isCapitalCostChange = quotationService.updateVrbDiscountValues(quotationModel);
								if(isCapitalCostChange){
									 quotationModel = quotationService.getQuotationModelWithCostAndAccessories(qmdId);
								 }
							}
	
							if("Y".equals(quotationModel.getReCalcNeeded())|| isContractPeriodUpdated || isCapitalCostChange || serviceElementChanged){
								//check for formal extension
								if ((!rentalCalculationService.isFormalExtension(quotationModel)) &&  (!rentalCalculationService.isQuoteForPartialCalculation(quotationModel))){
									calcQuotationModel = rentalCalculationService.getCalculatedQuotationModel(qmdId, true,quoteOverviewVO.getCapitalContribution());
								}else{
									calcQuotationModel = rentalCalculationService.getCalculatedQuotationModel(qmdId, false,quoteOverviewVO.getCapitalContribution());
								}
								recalculateAndSave = true;
							}
							
							
						}
					}
					
					loadStaticData();
					loadDynamicCalculatedData();
					setEnableDisableStatusFinParam();
					resizeLeaseRateInput();
					
					if(serviceElementChanged){
						FacesContext context = FacesContext.getCurrentInstance();
					    context.addMessage("warnings", new FacesMessage(FacesMessage.SEVERITY_WARN, "Services details have changed.", "The services details for this quote have changed. Please click the 'Service Elements' link to view the results."));
					    RequestContext.getCurrentInstance().update(GROWL_UI_ID);
					    serviceElementChanged = false;
					}
					
				}else{
					disableCalculateAndSave = true;
					addInfoMessage("custom.message", "Locate Orders cannot be placed for a Product of Type Purchase at this time.");
				}
				logger.info("init:Qmd Id=" + qmdId);
			} else {
				super.addErrorMessage("generic.error",
						"This link is not available through VISION directly. Please use Willow to get quote overview.");
				//forwardToURL("/view/notImplemented");
				invalidAccessPath	= true;
				
				
			}
		} catch (Exception ex) {
			handleException("generic.error", new String[]{"loading"}, ex, "init");
			logger.error(ex);
		
		}
	}
	
	private boolean hasServiceElementsChanged(QuotationModel quoteModel) throws MalBusinessException{
		boolean retVal = false;
		// if the quote is enabled and there are elements (the quote is not new)
		if(!disableCalculateAndSave && quoteModel.getQuotationElements().size() > 0){
			availableElements = serviceElementService.determineElementsWithChanges(quoteModel);
			retVal = serviceElementService.elementsHaveChanges(availableElements);
		}
		return retVal;
	}
	
	
	@Override
	protected void loadNewPage() {
		thisPage.setPageDisplayName("Quote Overview");
		thisPage.setPageUrl(ViewConstants.QUOTE_OVERVIEW);
		
		if (thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_FORWARDED_PAGE) != null) {
			forwaredPage = (Boolean) thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_FORWARDED_PAGE);
		}
		if (thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_QUOTE_MODEL_ID) != null) {
			qmdId = (Long) thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_QUOTE_MODEL_ID);
		} else {
			String paramQmdId = getRequestParameter("qmdId");
			if (paramQmdId != null){
				qmdId = Long.valueOf(paramQmdId);
			}
	
		}
	}

	@Override
	protected void restoreOldPage() {
		qmdId = (Long) thisPage.getRestoreStateValues().get(ViewConstants.VIEW_PARAM_QUOTE_MODEL_ID);
		if (thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_FORWARDED_PAGE) != null) {
			forwaredPage = (Boolean) thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_FORWARDED_PAGE);
		}
	}
	
	private void setEnableDisableStatusFinParam() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String workclass = ((User) authentication.getPrincipal()).getWorkclass();
		WorkClassAuthority workClassAuthority = null;

		workClassAuthority = financeParameterService.getWorkClassAuthority(MalConstants.FIN_PARAM_AM_ADJ,
				workclass, MalConstants.MODULE_VISION_QL);
		if (workClassAuthority != null) {
			if (!disableCalculateAndSave) {
				quoteOverviewVO.setDisableInvoiceAdjustment(false);
			} else {
				quoteOverviewVO.setDisableInvoiceAdjustment(true);
			}

		} else {
			quoteOverviewVO.setDisableInvoiceAdjustment(true);
		}

	}

	private void loadAdjustmentsData() throws MalBusinessException {
		String leaseType = quotationService.getLeaseType(qmdId);
		
		// load the invoice adjustment values
		if(!leaseType.equalsIgnoreCase("PUR")){
			this.hideAdjustmentsArea = true;
		// setup adjustments for AM least types
		}else{
			QuotationModelFinances invoiceAdjustmentFromDB = this.getQuotationModelFinancesForInvoiceAdjustment();
			
			Double invoiceAdjustment = invoiceAdjustmentFromDB != null ?  Double.valueOf(invoiceAdjustmentFromDB.getnValue().doubleValue()) : quotationService.getFinanceParam(MalConstants.FIN_PARAM_AM_ADJ, quotationModel.getQmdId(),
					quotationModel.getQuotation().getQuotationProfile().getQprId());
			
			if(!MALUtilities.isEmpty(invoiceAdjustment)){
				quoteOverviewVO.setInvoiceAdjustment(BigDecimal.valueOf(invoiceAdjustment));
				quoteOverviewVO.setInvoiceAdjustmentToCompare(quoteOverviewVO.getInvoiceAdjustment());
			}
			
			// default to hide if the quote is new (first time a user came in)
			if (isNewQuote) {
				hideInvoiceAdjustment = true;
			} else {
				if (invoiceAdjustmentFromDB != null) {
					//TODO: update the method below for AM Product Types or create something new
					hideInvoiceAdjustment = rentalCalculationService.isReportsHidden(invoiceAdjustmentFromDB);
				} else {
					hideInvoiceAdjustment = true;
				}
			}
		}
	}

	private void loadStaticData() throws MalBusinessException {

		logger.debug("QuoteOverviewBean:loadStaticData:Start");
		if (recalculateAndSave) {
			QuoteCost quoteCost = capitalCostOverviewService.getQuoteCost(quotationModel);
			quoteOverviewVO.setDealCost(quoteCost.getDealCost());
			quoteOverviewVO.setCustomerCost(quoteCost.getCustomerCost());
		} else {
			QuoteCost quoteCost = capitalCostOverviewService.getQuoteCost(quotationModel);
			quoteOverviewVO.setDealCost(quoteCost.getDealCost());
			quoteOverviewVO.setCustomerCost(quoteCost.getCustomerCost());
		}
		quoteOverviewVO.setCapitalContribution(quotationModel.getCapitalContribution() == null ? BigDecimal.ZERO : quotationModel.getCapitalContribution());
		quoteOverviewVO.setCapitalContributionToCompare(quoteOverviewVO.getCapitalContribution());
		
		quoteOverviewVO.setAccountCode(quotationModel.getQuotation().getExternalAccount().getExternalAccountPK()
				.getAccountCode());
		quoteOverviewVO.setAccountName(quotationModel.getQuotation().getExternalAccount().getAccountName());
		quoteOverviewVO.setQuote(Long.toString(quotationModel.getQuotation().getQuoId()) + "/"
				+ Long.toString(quotationModel.getQuoteNo()) + "/" + Long.toString(quotationModel.getRevisionNo()));
		quoteOverviewVO.setUnitNo(quotationModel.getUnitNo());
		quoteOverviewVO.setUnitDesc(quotationModel.getModel().getModelDescription());

		// replacement for unit
		qmReplacementFmsId = quotationModel.getReplacementFmsId();
		if (qmReplacementFmsId != null) {
			fleetMaster = fleetMasterService.getFleetMasterByFmsId(qmReplacementFmsId);
			quoteOverviewVO.setUnitNoReplacement(fleetMaster.getUnitNo());
		}

		// excess mile band
		quoteOverviewVO.setExcessMileBand(quotationService.getExcessMileBand(qmdId));

		String leaseType = quotationService.getLeaseType(qmdId);
		if (leaseType != null) {
			ProductTypeCode productTypeCode = productTypeCodeDAO.findById(leaseType).orElse(null);
			quoteOverviewVO.setProductDesc(productTypeCode == null ? "" : productTypeCode.getDescription());
		}
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String workclass = ((User)authentication.getPrincipal()).getWorkclass();
		WorkClassAuthority workClassAuthority = financeParameterService.getWorkClassAuthority(MalConstants.FIN_PARAM_GRD_DEL_RECHARGE,workclass , MalConstants.MODULE_VISION_QL);
		if(workClassAuthority != null){
			if(!disableCalculateAndSave){
				quoteOverviewVO.setDisableGrdDelivery (false);
			}else{
				quoteOverviewVO.setDisableGrdDelivery (true);
			}
			
		}else{
			quoteOverviewVO.setDisableGrdDelivery (true);
		}
		QuotationModelFinances quotationModelFinancesInDb = financeParameterService.getQuotationModelFinances(quotationModel.getQmdId(), MalConstants.FIN_PARAM_GRD_DEL_RECHARGE);
		quoteOverviewVO.setGrdDeliveryRechargeAmount(quotationModelFinancesInDb != null ? quotationModelFinancesInDb.getnValue():null);
		if(quoteOverviewVO.getGrdDeliveryRechargeAmount() != null){
		    quoteOverviewVO.setGrdDeliveryRechargeAmount(CommonCalculations.getRoundedValue(quoteOverviewVO.getGrdDeliveryRechargeAmount(), 2)) ;
		    quoteOverviewVO.setGrdDeliveryRechargeAmountLabel(DecimalFormat.getCurrencyInstance(Locale.US).format(quoteOverviewVO.getGrdDeliveryRechargeAmount()));
		    //NumberFormat.getCurrencyInstance(Locale.US).format(number, toAppendTo, pos)
		}
		
		quoteOverviewVO.setGrdDeliveryRechargeAmount( quoteOverviewVO.getGrdDeliveryRechargeAmount() != null ? CommonCalculations.getRoundedValue(quoteOverviewVO.getGrdDeliveryRechargeAmount(),DECIMAL_DIG_PRECISION_TWO):null);
    			
		quoteOverviewVO.setQuoteProfileDesc(quotationModel.getQuotation().getQuotationProfile().getDescription());
		quoteOverviewVO.setCostTreatment(quotationModel.getPreContractFixedCost().equals("F") ? COST_FIXED : COST_VARIABLE);
		quoteOverviewVO.setInterestTreatment(quotationModel.getPreContractFixedInterest().equals("F") ? COST_FIXED
				: COST_VARIABLE);
		//quoteOverviewVO.setMileageProgramDesc(quotationService.getQuoteProgramDescription(qmdId));
		quoteOverviewVO.setMileageProgramDesc(quotationService.getApplicableMilageProgram(qmdId));
		quoteOverviewVO.setTerm(quotationModel.getContractPeriod());
		quoteOverviewVO.setDistance(quotationModel.getContractDistance());
		quoteOverviewVO.setOrderType(quotationModel.getOrderType().equals("F") ? FACTORY : LOCATE);
		//Plate Type Code Desc
		if (quotationModel.getPlateTypeCode() != null) {
			quoteOverviewVO.setPlateTypeCodeDescription(quotationService.getPlateTypeDescription(quotationModel.getPlateTypeCode()));
		}
		//Driver Grade Group Description
		if (quotationModel.getQuotation().getDriverGradeGroup() != null) {
			quoteOverviewVO.setGradeGroupDescription(quotationService.getDriverGradeGroupDescription(quotationModel.getQuotation().getDriverGradeGroup()));
		}
		
		Product	product	= productDAO.findById(getProductCode()).orElse(null);
		quoteOverviewVO.setProductName(product.getProductName());
	
		logger.debug("loadStaticData:End");

	}

	private QuotationModelFinances getQuotationModelFinancesForInvoiceAdjustment() {
		return quotationModelFinancesDAO.findByQmdIdAndParameterKey(quotationModel.getQmdId(),
				MalConstants.FIN_PARAM_AM_ADJ);
	}
	

	private void loadDynamicCalculatedData() throws MalBusinessException , Exception {
		logger.debug("QuoteOverviewBean:loadDynamicCalculatedData:Start");
		quoteOverviewVO.setEquipmentResidual(rentalCalculationService.getEquipmentResidual(quotationModel));
		quoteOverviewVO.setBaseResidual(quotationModel.getResidualValue());
		//quoteOverviewVO.setBaseResidualToCompare(quotationModel.getResidualValue());		
		
		if(isContractPeriodUpdated){
			logger.debug("quote is loading from config as contract changed.." + qmdId);
			quoteOverviewVO.setBaseIrr(CommonCalculations.getRoundedValue(getHurdleRate(quotationModel),RentalCalculationService.IRR_CALC_DECIMALS));
			quoteOverviewVO.setProfitAdj(CommonCalculations.getRoundedValue(getProfitAdj(quotationModel),RentalCalculationService.IRR_CALC_DECIMALS));
		}else{
			logger.debug("quote is loading from profitability.." + qmdId);
			quoteOverviewVO.setBaseIrr(CommonCalculations.getRoundedValue(quotationProfitability.getProfitBase(),RentalCalculationService.IRR_CALC_DECIMALS));
			quoteOverviewVO.setProfitAdj(CommonCalculations.getRoundedValue(quotationProfitability.getProfitAdjustment(),RentalCalculationService.IRR_CALC_DECIMALS));			
		}
		quoteOverviewVO.setActualIrrValue(CommonCalculations.getRoundedValue(quotationProfitability.getProfitAmount(), RentalCalculationService.IRR_CALC_DECIMALS) );
		quoteOverviewVO.setActualIrrToCompare(CommonCalculations.getRoundedValue(quotationProfitability.getProfitAmount(), RentalCalculationService.IRR_CALC_DECIMALS));
		quoteOverviewVO.setIrrApprovedLimit(quotationProfitability.getIrrApprovedLimit() != null ?String.valueOf(CommonCalculations.getRoundedValue(
				quotationProfitability.getIrrApprovedLimit() != null ? quotationProfitability.getIrrApprovedLimit() : null, 3)):null);
		if(quoteOverviewVO.getIrrApprovedLimit() != null )
			quoteOverviewVO.setIrrApprovedLimit(quoteOverviewVO.getIrrApprovedLimit()+"%");
		quoteOverviewVO.setIrrApprovedUser(quotationProfitability.getIrrApprovedUser());
		quoteOverviewVO.setIrrApprovedDate(quotationProfitability.getIrrApprovedDate());
		quotationProfitability.setQuotationModel(quotationModel);
		
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String workclass = ((User)authentication.getPrincipal()).getWorkclass();
		BigDecimal irrApprovedMinLimit =  financeParameterService.getWorkclassLowerLimit(MalConstants.FIN_PARAM_CE_MIN_IRR_PROFIT,workclass , MalConstants.MODULE_VISION_QL);
		quoteOverviewVO.setIrrApprovedMinLimit(irrApprovedMinLimit);
		
		/*
		 * Both these flag (isRentalCalculationApplicable & zeroRentalFlag) mentioned below have a same purpose to make sure if the quote is zero rental 
		 * (i.e weather we have to calculate rental for this quote or not) then we do not have to calculate minimum lease rate. "isRentalCalculationApplicable"
		 * verify based on Processor Name (vmpProcesor) now if we observe in the database we have 3 different Processor Name with same Formulae ("mal_rental_calcs.vmp_calc")
		 * Now to verify other 2 we have used common ground i.e Formulae and we have created a separate flag "zeroRentalFlag" based on this formulae verification. 
		 * Better approach would be to see the difference in all the three java Processors and in case we do not have any difference then we should use only one java processor
		 * instead of three in this way we will not have 2 flags and as far as Processor Name & Formulae is concern we will have consistency as well.
		 */
		if(isRentalCalculationApplicable && !zeroRentalFlag){
		    BigDecimal minimumLeaseRate = profitabilityService.calculateMonthlyRentalConsolidated(quotationModel,
						quoteOverviewVO.getDealCost(), quoteOverviewVO.getTotalResidual(), quoteOverviewVO.getMinimumIrr());
		    quoteOverviewVO.setMinimumLeaseRate(CommonCalculations.getRoundedValue(minimumLeaseRate, DECIMAL_DIG_PRECISION_TWO));
		}else{
		    quoteOverviewVO.setMinimumLeaseRate(BigDecimal.ZERO);
		}
		
		BigDecimal bdPeriod = BigDecimal.valueOf(quotationModel.getContractPeriod());
		BigDecimal financeValue = null;
		if (!recalculateAndSave) {
			financeValue = rentalCalculationService.getFinanceLeaseElementCostForCE(quotationModel);
			//No need to divide  from period, it is already coming as divided by its own no of rentals value
		} else {
			if (isContractPeriodUpdated) {
				// use new interest rates from setup
				financeValue = profitabilityService.calculateMonthlyRental(calcQuotationModel, quoteOverviewVO.getDealCost(),
						quoteOverviewVO.getTotalResidual(), quoteOverviewVO.getActualIrrValue());
			} else {
				// use existing interest rates from profitability
				financeValue = profitabilityService.calculateMonthlyRental(calcQuotationModel, quoteOverviewVO.getDealCost(),
						quoteOverviewVO.getTotalResidual(), quotationProfitability.getProfitAmount());
			}

		}
		

		quoteOverviewVO.setActualLeaseRate(CommonCalculations.getRoundedValue(financeValue, DECIMAL_DIG_PRECISION_TWO));
		quoteOverviewVO.setActualLeaseRateToCompare(CommonCalculations.getRoundedValue(financeValue, DECIMAL_DIG_PRECISION_TWO));
		BigDecimal serviceValue = serviceElementService.getServiceElementMonthlyCost(quotationModel);
		quoteOverviewVO.setServiceElementRate(serviceValue);
		
		if(recalculateAndSave){
		    	serviceValue = serviceElementService.getServiceElementCost(calcQuotationModel).divide(bdPeriod,CommonCalculations.MC);
		    	quoteOverviewVO.setServiceElementRate(serviceValue);
		    	calcQuotationModel.setCalculatedMontlyRental(quoteOverviewVO.getActualLeaseRate());
		    	
			//TODO: ideally the MulQuoteElem object should be saved with the rest of the Quotation Model
			// but most of the code has been tested to do this using the saveOrUpdateServiceElements method
			// and there is not enough time and too much risk to do this the right way this late into testing.
			// So, we are using the tested code that is also shared with the amendments screen instead.
		    
			//check for formal extension
			if ((!rentalCalculationService.isFormalExtension(quotationModel)) &&  (!rentalCalculationService.isQuoteForPartialCalculation(quotationModel))){	
			    calcQuotationModel.getQuotation().setMulQuoteElems(null);
			    if(serviceElementService.elementsHaveChanges(availableElements)){
					serviceElementService.saveOrUpdateServiceElements(calcQuotationModel, availableElements);
				}
			}
			
			rentalCalculationService.saveCalculatedQuote(quotationModel, calcQuotationModel, quotationModel.getResidualValue(),
								     getUpdatedProfitabiltyObject(false), true);
			
			// save the flag for hiding invoice adjustments from client facing documents 
			QuotationModelFinances qmf = getQuotationModelFinancesForInvoiceAdjustment();
			if (qmf != null) {
				rentalCalculationService.saveHideInvoiceAdjustment(qmf, hideInvoiceAdjustment);
			}			
			
			quotationModel = quotationService.getQuotationModelWithCostAndAccessories(qmdId);
			quotationProfitability = profitabilityService.getQuotationProfitability(quotationModel);
			calcQuotationModel = null;
			recalculateAndSave	= false;
			addInfoMessage("quote.pricechnage.recalculate.equip");
		}
		logger.debug("QuoteOverviewBean:loadDynamicCalculatedData:End");
	}

	

	public void reCalculateRental() {
		try {
			logger.debug("QuoteOverviewBean:calculateRental:Start");
			resizeLeaseRateInput();
			if (!validateAllInput()) {
				this.setReCalculated(false);
				allowSave = false;
				return;
			}else{
				allowSave = true;
			}
			
			//TODO: there is concern that the adjustment values are going to be needed in the quotationModel; but they are not saved yet.
			
			//For RC-1283
			Boolean isVrbDiscountValuesChanged	=	false;
			if(quotationModel.getOrderType() != null && !quotationModel.getOrderType().equals(ORDER_TYPE_LOCATE)){
				//see for VRB discounts(QM075) and update quotation capital elements
				isVrbDiscountValuesChanged = quotationService.updateVrbDiscountValues(quotationModel);
				if(isVrbDiscountValuesChanged){
					quotationModel = quotationService.getQuotationModelWithCostAndAccessories(qmdId);
					QuoteCost quoteCost = capitalCostOverviewService.getQuoteCost(quotationModel);
					quoteOverviewVO.setDealCost(quoteCost.getDealCost());
					quoteOverviewVO.setCustomerCost(quoteCost.getCustomerCost());
				}
				
			}
			
			if(calcQuotationModel == null){
				//check for formal extension
				if ((!rentalCalculationService.isFormalExtension(quotationModel)) &&  (!rentalCalculationService.isQuoteForPartialCalculation(quotationModel))){
					serviceElementChanged = hasServiceElementsChanged(quotationModel);
					calcQuotationModel = rentalCalculationService.getCalculatedQuotationModel(qmdId, true,quoteOverviewVO.getCapitalContribution());
				}else{
					calcQuotationModel = rentalCalculationService.getCalculatedQuotationModel(qmdId, false,quoteOverviewVO.getCapitalContribution());
				}
			}
			if (calcQuotationModel != null) {
				BigDecimal bdPeriod = BigDecimal.valueOf(quotationModel.getContractPeriod());
				BigDecimal serviceValue = serviceElementService.getServiceElementCost(quotationModel).divide(bdPeriod,
						CommonCalculations.MC);
				quoteOverviewVO.setServiceElementRate(serviceValue);
			}
			
			// adjust invoice adjustment
			BigDecimal customerCost = quoteOverviewVO.getCustomerCost();
			BigDecimal quoteCapital = calcQuotationModel.getQuoteCapital();
			if(quoteOverviewVO.getInvoiceAdjustment() != null){
				customerCost = customerCost.add(quoteOverviewVO.getInvoiceAdjustment());
				quoteCapital = quoteCapital.add(quoteOverviewVO.getInvoiceAdjustment());
			}
			if(quoteOverviewVO.getInvoiceAdjustmentToCompare() != null){
				customerCost = customerCost.subtract(quoteOverviewVO.getInvoiceAdjustmentToCompare());
				quoteCapital = quoteCapital.subtract(quoteOverviewVO.getInvoiceAdjustmentToCompare());
			}
			calcQuotationModel.setQuoteCapital(quoteCapital);
			quoteOverviewVO.setCustomerCost(customerCost);// We need to update UI for client cost
			quoteOverviewVO.setInvoiceAdjustmentToCompare(quoteOverviewVO.getInvoiceAdjustment());
			
			// adjust capital contribution
			if (quoteOverviewVO.getCapitalContribution().compareTo(quoteOverviewVO.getCapitalContributionToCompare()) != 0) {
			    	quoteOverviewVO.setDealCost(quoteOverviewVO.getDealCost().subtract(quoteOverviewVO.getCapitalContribution()).add(quoteOverviewVO.getCapitalContributionToCompare()));
			    	quoteOverviewVO.setCustomerCost(quoteOverviewVO.getCustomerCost().subtract(quoteOverviewVO.getCapitalContribution()).add(quoteOverviewVO.getCapitalContributionToCompare()));
				quoteOverviewVO.setCapitalContributionToCompare(quoteOverviewVO.getCapitalContribution());
				//For RC-1783,update capital contribution of quotation model
				quotationModel.setCapitalContribution(quoteOverviewVO.getCapitalContribution());
			}
			
			
        	if (quoteOverviewVO.getActualLeaseRate().doubleValue() != quoteOverviewVO.getActualLeaseRateToCompare().doubleValue() 
        			|| isRentalCalculationApplicable == false) {//calc irr
        		
        		BigDecimal calculatedIRR = BigDecimal.ZERO;
        		try {                	
        			calculatedIRR = profitabilityService.calculateIRR(calcQuotationModel, quoteOverviewVO.getDealCost(),
        												quoteOverviewVO.getTotalResidual(), quoteOverviewVO.getActualLeaseRate());
        		} catch (Exception e) {
        			logger.error(e);
        		}
        		BigDecimal roundedCalculatedIRR = CommonCalculations.getRoundedValue(calculatedIRR, RentalCalculationService.IRR_CALC_DECIMALS);
        		quotationModel.setInterestRate(quoteOverviewVO.getMinimumIrr());
        		quoteOverviewVO.setActualIrrValue(roundedCalculatedIRR);
        		quoteOverviewVO.setActualIrrToCompare(roundedCalculatedIRR);
        		quoteOverviewVO.setActualLeaseRateToCompare(quoteOverviewVO.getActualLeaseRate());
        		calcQuotationModel.setCalculatedMontlyRental(quoteOverviewVO.getActualLeaseRate());
        		
        	 } 
            //always calculate monthly rental by irr for rental distribution per line  
    		BigDecimal monthlyRental = profitabilityService.calculateMonthlyRental(calcQuotationModel, quoteOverviewVO.getDealCost(),
    			quoteOverviewVO.getTotalResidual(), quoteOverviewVO.getActualIrrValue());
    		quoteOverviewVO.setActualLeaseRate(CommonCalculations.getRoundedValue(monthlyRental, DECIMAL_DIG_PRECISION_TWO));
    		quoteOverviewVO.setActualLeaseRateToCompare(CommonCalculations.getRoundedValue(monthlyRental, DECIMAL_DIG_PRECISION_TWO));
    		quoteOverviewVO.setActualIrrToCompare(quoteOverviewVO.getActualIrrValue());
    		calcQuotationModel.setCalculatedMontlyRental(CommonCalculations.getRoundedValue(monthlyRental, DECIMAL_DIG_PRECISION_TWO));
    		quotationModel.setInterestRate(quoteOverviewVO.getMinimumIrr());
                	
			
			if(isRentalCalculationApplicable && !zeroRentalFlag){
			    BigDecimal minimumLeaseRate = profitabilityService.calculateMonthlyRentalConsolidated(quotationModel,
							quoteOverviewVO.getDealCost(), quoteOverviewVO.getTotalResidual(), quoteOverviewVO.getMinimumIrr());
			    quoteOverviewVO.setMinimumLeaseRate(CommonCalculations.getRoundedValue(minimumLeaseRate, DECIMAL_DIG_PRECISION_TWO));
			}
			quotationModel.setInterestRate(quoteOverviewVO.getMinimumIrr());
			if(isVrbDiscountValuesChanged){
				//no need to save as of now,may be possible in releases
				addInfoMessage("quote.calculation.success.after.vrbupdates");
			}else{
				addInfoMessage("quote.calculation.success");
			}

			this.setReCalculated(true);
			this.setDirtyData(true);
			
			if(serviceElementChanged){
				FacesContext context = FacesContext.getCurrentInstance();
			    context.addMessage("warnings", new FacesMessage(FacesMessage.SEVERITY_WARN, "Services details have changed.", "The services details for this quote have changed. Please click the 'Service Elements' link to view the results."));
			    RequestContext.getCurrentInstance().update(GROWL_UI_ID);
			    serviceElementChanged = false;
			}
			
			logger.debug("QuoteOverviewBean:calculateRental:End");
		} catch (Exception e) {
			logger.error(e);
			handleException("generic.error.occured.while", new String[] { "calculating rental." }, e,
					"calculateClosedEndRental");
		}

	}
	
	private boolean validateApprovedMinIrr(){
		//handling negative value,using relative value to validate
		if(!MALUtilities.isEmpty(quoteOverviewVO.getIrrApprovedLimitValue()) && quoteOverviewVO.getIrrApprovedMinLimit() !=  null){
			BigDecimal temp = quoteOverviewVO.getMinimumIrr().add(quoteOverviewVO.getIrrApprovedMinLimit());
			
			if(quoteOverviewVO.getIrrApprovedLimitValue().compareTo(temp) < 0){
				addErrorMessage(inputFieldIdMaps.get("APPROV_MIN_IRR"),"custom.message",
						talMessage.getMessage("approved.min.irr.range",temp.toPlainString() ));
				return false;
			}			
		}
		return true;
	}
	
	public String cancel() {	
		 return super.cancelPage(); 
	}
	
	public void save() {
		try {
			logger.debug("QuoteOverviewBean:Save:Start");
			if(!allowSave){
				return;
			}
			// Here we are setting value in calcQuotationModel because we are
			// doing clean process
			
			boolean isSaveSuccessfull = saveQuote();
			if(!isSaveSuccessfull){
				return;
			}
			addSuccessMessage("process.success", "Save");
			
			logger.debug("QuoteOverviewBean:Save:End");
		} catch (Exception ex) {
			logger.error(ex);
			handleException("generic.error.occured.while", new String[] { "saving  rental." }, ex, "save");
		}
	}
	
	private boolean saveQuote() throws Exception {
		Map<String, Object> finParamMap = new HashMap<String, Object>();
		// validate adjustments on save
		// but only if this quote type has adjustments
		if(!hideAdjustmentsArea){	
			finParamMap.put(MalConstants.FIN_PARAM_AM_ADJ, quoteOverviewVO.getInvoiceAdjustment());
			if (!(validateFinanceParameters(finParamMap))) {
	
				return false;
			}
		}
		
		if (reCalculated) {
			// save adjustments; if they changed so they are there for the calculation, if needed
			// but only if this quote type has adjustments
			if(!hideAdjustmentsArea){
				rentalCalculationService.saveFinanceParamOnQuote(quoteOverviewVO.getInvoiceAdjustment(), MalConstants.FIN_PARAM_AM_ADJ,
		        		quotationModel);
			}
			
			if (calcQuotationModel == null) {				
				this.loadAdjustmentsData();
				calcQuotationModel = rentalCalculationService.getCalculatedQuotationModel(qmdId, true,quoteOverviewVO.getCapitalContribution());
			}
			calcQuotationModel.setCalculatedMontlyRental(quoteOverviewVO.getActualLeaseRate());
			calcQuotationModel.setCapitalContribution(quoteOverviewVO.getCapitalContribution());
			
			//TODO: ideally the MulQuoteElem object should be saved with the rest of the Quotation Model
			// but most of the code has been tested to do this using the saveOrUpdateServiceElements method
			// and there is not enough time and too much risk to do this the right way this late into testing.
			// So, we are using the tested code that is also shared with the amendments screen instead.
			//check for formal extension
			if ((!rentalCalculationService.isFormalExtension(quotationModel)) &&  (!rentalCalculationService.isQuoteForPartialCalculation(quotationModel))){
				calcQuotationModel.getQuotation().setMulQuoteElems(null);
				if(serviceElementService.elementsHaveChanges(availableElements)){
					serviceElementService.saveOrUpdateServiceElements(calcQuotationModel, availableElements);
				}
			}
			// save the calculated quote model
			rentalCalculationService.saveCalculatedQuote(quotationModel,calcQuotationModel, quoteOverviewVO.getBaseResidual(),
					getUpdatedProfitabiltyObject(false), true);
			
			// save the flag for hiding invoice adjustments from client facing documents 
			QuotationModelFinances qmf = getQuotationModelFinancesForInvoiceAdjustment();
			if (qmf != null) {
				rentalCalculationService.saveHideInvoiceAdjustment(qmf, hideInvoiceAdjustment);
			}
			
			quotationModel = quotationService.getQuotationModelWithCostAndAccessories(qmdId);

			calcQuotationModel = null;
		}

		quotationProfitability = profitabilityService.getQuotationProfitability(quotationModel);

		this.setReCalculated(false);
		this.setDirtyData(false);
		includeTempEquipmentsOnCapCost = true;
		return true;

	}

        public void saveGrdDelRecharge() {
        	quoteOverviewVO.setGrdDeliveryRechargeAmountLabel(quoteOverviewVO.getGrdDeliveryRechargeAmountLabel().trim().length() == 0 ? null : quoteOverviewVO.getGrdDeliveryRechargeAmountLabel());
        	BigDecimal tempDeliveryRecharge = quoteOverviewVO.getGrdDeliveryRechargeAmountLabel() == null ? null : new BigDecimal(quoteOverviewVO.getGrdDeliveryRechargeAmountLabel()
        		.replace("$", "").replaceAll(",", ""));
        	boolean success = true;
        	//add the parameter and the value into a Map for use in centralized validation
        	Map<String, Object> finParamMap = new HashMap<String, Object>();
        	finParamMap.put(MalConstants.FIN_PARAM_GRD_DEL_RECHARGE, tempDeliveryRecharge);
        	if (!(validateFinanceParameters(finParamMap))) {
        	     success = false;
        	}
        	try {
        	    if (success) {
        		rentalCalculationService.saveFinanceParamOnQuote(tempDeliveryRecharge, MalConstants.FIN_PARAM_GRD_DEL_RECHARGE,
        			quotationModel);
        		quoteOverviewVO.setGrdDeliveryRechargeAmount(tempDeliveryRecharge);
        		addSuccessMessage("saved.success", "Delivery Recharge Amount");
        	    }
        	} catch (Exception e) {
        	    success = false;
        	    super.addErrorMessage(inputFieldIdMaps.get(MalConstants.FIN_PARAM_GRD_DEL_RECHARGE), "generic.error.occured.while",
        		    " Saving Delivery Recharge Amount");
        	}
        
        	if (success == false) {
        	    RequestContext context = RequestContext.getCurrentInstance();
        	    context.addCallbackParam("failure", true);
        	}
        }
        
        public void saveAppIrrMin() {
        	
        	
        	quoteOverviewVO.setIrrApprovedLimit(quoteOverviewVO.getIrrApprovedLimit().trim().length() == 0 ? null : quoteOverviewVO.getIrrApprovedLimit());
       
        
        	boolean success = true;
        	if (!validateApprovedMinIrr()) {
        	     success = false;
        	}
        	try {
        	    if (success) {
        	    	profitabilityService.saveQuotationProfitability(getUpdatedProfitabiltyObject(true));
        	    	quotationProfitability = profitabilityService.getQuotationProfitability(quotationModel);
        	    	if(quoteOverviewVO.getIrrApprovedLimit() != null){
        	    	   quoteOverviewVO.setIrrApprovedUser(quotationProfitability.getIrrApprovedUser());
        			   quoteOverviewVO.setIrrApprovedDate(quotationProfitability.getIrrApprovedDate());
        	        }else{
        	           quoteOverviewVO.setIrrApprovedUser(null);
        			   quoteOverviewVO.setIrrApprovedDate(null);   
        	        }
        			   addSuccessMessage("saved.success", "Approved Minimum IRR");
        	    }
        	} catch (Exception e) {
        	    success = false;
        	    super.addErrorMessage(inputFieldIdMaps.get("APPROVED_MIN_IRR_LBL_ID"), "generic.error.occured.while", " Approved Minimum IRR");
        	}
        
        	if (success == false) {
        	    RequestContext context = RequestContext.getCurrentInstance();
        	    context.addCallbackParam("failure", true);
        	}
        }
        public void cancelSaveGrdDelRecharge() {
            
            if(quoteOverviewVO.getGrdDeliveryRechargeAmount() != null){
        	quoteOverviewVO.setGrdDeliveryRechargeAmountLabel(DecimalFormat.getCurrencyInstance(Locale.US).format(quoteOverviewVO.getGrdDeliveryRechargeAmount()));
            }else{
        	quoteOverviewVO.setGrdDeliveryRechargeAmountLabel(null); 
            }
            
        }
        
        
        public void cancelSaveAppIrrMin() {
        	
            if (quotationProfitability.getIrrApprovedLimit() != null) {
            	quoteOverviewVO.setIrrApprovedLimit(quotationProfitability.getIrrApprovedLimit() != null ?String.valueOf(CommonCalculations.getRoundedValue(
        				quotationProfitability.getIrrApprovedLimit() != null ? quotationProfitability.getIrrApprovedLimit() : null,DECIMAL_DIG_PRECISION_THREE)+"%"):null);
            }else{            	
            	quoteOverviewVO.setIrrApprovedLimit(null);
            }
            
        }
        
	public void viewCapitalCosts() {
		logger.debug("QuoteOverviewBean:viewCapitalCosts:Start");
		saveRestoreStateValues(getCurrentPageRestoreStateValuesMap());
		Map<String, Object> nextPageValues = new HashMap<String, Object>();
		nextPageValues.put(ViewConstants.VIEW_PARAM_QUOTE_MODEL_ID, qmdId);
		nextPageValues.put(ViewConstants.VIEW_PARAM_INCLUDE_TEMP_EQUIPMENT, includeTempEquipmentsOnCapCost);
		saveNextPageInitStateValues(nextPageValues);
		logger.debug("QuoteOverviewBean:viewCapitalCosts:End");
		forwardToURL(ViewConstants.CAPITAL_COSTS);
	}
	
	public void editCapitalCosts() {
		logger.debug("QuoteOverviewBean:editCapitalCosts:Start");
		saveRestoreStateValues(getCurrentPageRestoreStateValuesMap());
		Map<String, Object> nextPageValues = new HashMap<String, Object>();
		nextPageValues.put(ViewConstants.VIEW_PARAM_QUOTE_MODEL_ID, qmdId);
		nextPageValues.put(ViewConstants.VIEW_PARAM_INCLUDE_TEMP_EQUIPMENT, includeTempEquipmentsOnCapCost);
		saveNextPageInitStateValues(nextPageValues);
		logger.debug("QuoteOverviewBean:editCapitalCosts:End");
		forwardToURL("/view/rentalCalc/capitalCostEntry");
	}

	public void viewServiceElements() {
		logger.debug("QuoteOverviewBean:viewServiceElements:Start");
		saveRestoreStateValues(getCurrentPageRestoreStateValuesMap());
		Map<String, Object> nextPageValues = new HashMap<String, Object>();
		nextPageValues.put(ViewConstants.VIEW_PARAM_QUOTE_MODEL_ID, qmdId);
		saveNextPageInitStateValues(nextPageValues);
		logger.debug("QuoteOverviewBean:viewServiceElements:End");
		forwardToURL(ViewConstants.SERVICE_ELEMENTS);
	}

	public void updateTotalResidual() {
		if (!MALUtilities.isEmpty(quoteOverviewVO.getBaseResidual())) {
			String str = quoteOverviewVO.getBaseResidual().toPlainString();
			if(str.length()> 8){
				baseResTextSize	=1+ str.length()+ str.length()/3;
			}else{
				baseResTextSize = 12;
			}
			
		}else{
			baseResTextSize = 12;
		}
		if (validateResidual()) {
			// We have calculated total dynamically
			allowSave = true;
		}else{
			allowSave = false;
		}

	}
	public void resizeLeaseRateInput(){
		String str = quoteOverviewVO.getActualLeaseRate() != null ? quoteOverviewVO.getActualLeaseRate().toPlainString(): null;
		if(!MALUtilities.isEmpty(str)){
			if(str.length()> 8){
				leaseRateTextSize	=1+ str.length()+ str.length()/3;
			}else{
				leaseRateTextSize = 12;
			}
		}else{
			leaseRateTextSize = 12;
		}
		
	}

	
	private BigDecimal getHurdleRate(QuotationModel quotationModel) throws MalException {
		BigDecimal baseIRR = new BigDecimal("0.00");
		try {
			logger.debug("QuoteOverviewBean:getIRR:Qmd Id=" + quotationModel.getQmdId());
			baseIRR = rentalCalculationService.getHurdleRate(quotationModel);
			logger.debug("QuoteOverviewBean:baseIRR:baseIRR=" + baseIRR);
		} catch (Exception ex) {
			logger.error(ex);
			throw new MalException("generic.error.occured.while", new String[] { "getting global hurdle IRR" });
		}
		return baseIRR;
	}
	private BigDecimal getProfitAdj(QuotationModel quotationModel) {
		Double profitAdj = -1D;
		try {
			logger.debug("QuoteOverviewBean:getProfitAdj:Qmd Id=" + quotationModel.getQmdId());
			profitAdj = quotationService.getFinanceParam(MalConstants.CLIENT_PROFIT_ADJ, qmdId, quotationModel
					.getQuotation().getQuotationProfile().getQprId());
			logger.debug("QuoteOverviewBean:getProfitAdj:profitAdj=" + profitAdj);
			if(profitAdj == null){
				profitAdj = -1D;
			}
		} catch (Exception ex) {
			logger.error(ex);
			throw new MalException("generic.error.occured.while", new String[] { "getting Profit Adjustment" });
		}
		return BigDecimal.valueOf(profitAdj);
	}

	private Map<String, Object> getCurrentPageRestoreStateValuesMap() {
		Map<String, Object> restoreStateValues = new HashMap<String, Object>();
		restoreStateValues.put(ViewConstants.VIEW_PARAM_QUOTE_MODEL_ID, qmdId);
		restoreStateValues.put(ViewConstants.VIEW_PARAM_FORWARDED_PAGE, forwaredPage);
		return restoreStateValues;
	}

	private boolean validateAllInput() {
		return validateResidual() && validateIrr() && validateLease() && validateCapitalContribution();
			
	}

	private boolean validateResidual() {
	     	
	    	if(isRentalCalculationApplicable == false || quoteOverviewVO.getDealCost().compareTo(BigDecimal.ZERO) == 0){
	     	    return true;
	     	}
	    
		if (MALUtilities.isEmpty(quoteOverviewVO.getBaseResidual())) {
			// error message for empty
			addErrorMessage(inputFieldIdMaps.get("BASE_RESIDUAL"),"custom.message", talMessage.getMessage("required.field", "Base Residual"));
			return false;
		} else {
			if (quoteOverviewVO.getBaseResidual().compareTo(new BigDecimal(0)) < 0) {
				// error for negative number
				addErrorMessage(inputFieldIdMaps.get("BASE_RESIDUAL"),"custom.message",talMessage.getMessage("negative.notallowed", "Base Residual"));
				return false;
			} else {
				if (quoteOverviewVO.getTotalResidual().compareTo(quoteOverviewVO.getDealCost()) >= 0) {
					// error for residual more than cap cost
					addErrorMessage(inputFieldIdMaps.get("BASE_RESIDUAL"),"custom.message",talMessage.getMessage("residualCostvalidation"));
					return false;
				}
			}
		}
		return true;
	}
	
        private boolean validateCapitalContribution() {
            	
            	if(isRentalCalculationApplicable == false  || quoteOverviewVO.getDealCost().compareTo(BigDecimal.ZERO) == 0 ){
	     	    return true;
	     	}
        	BigDecimal netDealCost = null;
        	netDealCost = quoteOverviewVO.getDealCost().subtract(quoteOverviewVO.getCapitalContribution()).add(quoteOverviewVO.getCapitalContributionToCompare());
        
        	if (netDealCost.doubleValue() <= 0) {
        	    addErrorMessage(inputFieldIdMaps.get("CAPITAL_CONTRIBUTION"), "custom.message",talMessage.getMessage("costRelatedValidation","Capital Contribution"));
        	    return false;
        	}
        
        	return true;
        }
 

	private boolean validateIrr() {
		if (MALUtilities.isEmpty(quoteOverviewVO.getActualIrrValue())) {
			// error message for empty
			addErrorMessage(inputFieldIdMaps.get("IRR"),"custom.message", talMessage.getMessage("required.field", "IRR"));
			return false;
		} else {

			if (!MALUtilities.isNumeric(quoteOverviewVO.getActualIrrValue().toPlainString())) {
				addErrorMessage(inputFieldIdMaps.get("IRR"),"custom.message",talMessage.getMessage("invalid.field", "IRR"));
				return false;
			} 
			return true;
		}
	}

	private boolean validateLease() {
		if (MALUtilities.isEmpty(quoteOverviewVO.getActualLeaseRate())) {
			// error message for empty
			addErrorMessage(inputFieldIdMaps.get("LEASE_RATE"),"custom.message", talMessage.getMessage("required.field", "Lease Rate"));
			return false;
		}
		return true;
	}
	
	
	private boolean validateFinanceParameters(Map<String, Object> finParamMap) {
		if (finParamMap != null && !finParamMap.isEmpty()) {
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
	
	

	public QuoteOverviewVO getQuoteOverviewVO() {
		return quoteOverviewVO;
	}

	public boolean isReCalculated() {
		return reCalculated;
	}

	public void setReCalculated(boolean reCalculated) {
		this.reCalculated = reCalculated;
	}


	public boolean isRentalCalculationApplicable() {
		return isRentalCalculationApplicable;
	}

	public void setRentalCalculationApplicable(boolean isRentalCalculationApplicable) {
		this.isRentalCalculationApplicable = isRentalCalculationApplicable;
	}

	public boolean isDisableCalculateAndSave() {
		return disableCalculateAndSave;
	}

	public void setDisableCalculateAndSave(boolean disableCalculateAndSave) {
		this.disableCalculateAndSave = disableCalculateAndSave;
	}
	
	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public int getBaseResTextSize() {
		return baseResTextSize;
	}

	public void setBaseResTextSize(int baseResTextSize) {
		this.baseResTextSize = baseResTextSize;
	}

	public int getLeaseRateTextSize() {
		return leaseRateTextSize;
	}


	public void setLeaseRateTextSize(int leaseRateTextSize) {
		this.leaseRateTextSize = leaseRateTextSize;
	}

        private QuotationProfitability getUpdatedProfitabiltyObject(boolean saveApprovedMin) {
    
        	if (quotationProfitability == null) {
        	    quotationProfitability = new QuotationProfitability();
        	    quotationProfitability.setQuotationModel(quotationModel);
        	    quotationProfitability.setProfitBase(quoteOverviewVO.getBaseIrr());
        	    quotationProfitability.setProfitAdjustment(quoteOverviewVO.getProfitAdj());
        	    quotationProfitability.setProfitAmount(quoteOverviewVO.getActualIrrValue());
        	    quotationProfitability.setProfitSource("N");
        	    quotationProfitability.setProfitType("P");
        	    
        
        	} else {
	        		if(saveApprovedMin == false){
	        			
		        	    quotationProfitability.setProfitBase(quoteOverviewVO.getBaseIrr());
		        	    quotationProfitability.setProfitAdjustment(quoteOverviewVO.getProfitAdj());
		        	    quotationProfitability.setProfitAmount(quoteOverviewVO.getActualIrrValue());
	        		}else{
	        
	        	    if (quoteOverviewVO.getIrrApprovedLimitValue() != null) {
		        		if (quotationProfitability.getIrrApprovedLimit() == null
		        			|| (quotationProfitability.getIrrApprovedLimit() != null 
		        				 && quoteOverviewVO.getIrrApprovedLimitValue().compareTo(quotationProfitability.getIrrApprovedLimit()) != 0)) {
		        		    quotationProfitability.setIrrApprovedLimit(quoteOverviewVO.getIrrApprovedLimitValue());
		        		    String employeeNo = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEmployeeNo();
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
        
		public boolean isInvalidAccessPath() {
			return invalidAccessPath;
		}
		
		public void setInvalidAccessPath(boolean invalidAccessPath) {
			this.invalidAccessPath = invalidAccessPath;
		}
		public boolean isForwaredPage() {
			return forwaredPage;
		}

		public void setForwaredPage(boolean forwaredPage) {
			this.forwaredPage = forwaredPage;
		}
		
		public boolean isHideInvoiceAdjustment() {
			return hideInvoiceAdjustment;
		}

		public void setHideInvoiceAdjustment(boolean hideInvoiceAdjustment) {
			this.hideInvoiceAdjustment = hideInvoiceAdjustment;
		}

		public boolean isHideAdjustmentsArea() {
			return hideAdjustmentsArea;
		}

		public void setHideAdjustmentsArea(boolean hideAdjustmentsArea) {
			this.hideAdjustmentsArea = hideAdjustmentsArea;
		}
		
		



}