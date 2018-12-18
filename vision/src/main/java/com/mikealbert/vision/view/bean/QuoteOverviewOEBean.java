package com.mikealbert.vision.view.bean;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
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
import com.mikealbert.data.entity.Product;
import com.mikealbert.data.entity.ProductTypeCode;
import com.mikealbert.data.entity.QuotationElement;
import com.mikealbert.data.entity.QuotationElementStep;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.QuotationModelFinances;
import com.mikealbert.data.entity.QuotationProfitability;
import com.mikealbert.data.entity.QuotationStepStructure;
import com.mikealbert.data.entity.User;
import com.mikealbert.data.entity.WorkClassAuthority;
import com.mikealbert.data.vo.QuotationStepStructureVO;
import com.mikealbert.data.vo.ServiceElementsVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;
import com.mikealbert.service.FinanceParameterService;
import com.mikealbert.service.FleetMasterService;
import com.mikealbert.service.OpenEndService;
import com.mikealbert.service.ProfitabilityService;
import com.mikealbert.service.QuotationService;
import com.mikealbert.service.RentalCalculationService;
import com.mikealbert.service.ServiceElementService;
import com.mikealbert.service.vo.QuoteCost;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.service.CapitalCostOverviewService;
import com.mikealbert.vision.view.ViewConstants;
import com.mikealbert.vision.vo.QuoteOverviewOEVO;

@Component
@Scope("view")
public class QuoteOverviewOEBean extends StatefulBaseBean {

	private static final long serialVersionUID = 2437933046906999010L;
	private MalLogger logger = MalLoggerFactory.getLogger(this.getClass());

	@Resource
	QuotationService quotationService;
	@Resource
	FleetMasterService fleetMasterService;
	@Resource
	ProductTypeCodeDAO productTypeCodeDAO;
	@Resource
	ProfitabilityService profitabilityService;
	@Resource
	RentalCalculationService rentalCalculationService;
	@Resource
	CapitalCostOverviewService capitalCostOverviewService;
	@Resource
	QuotationModelDAO quotationModelDAO;
	@Resource
	FinanceParameterService financeParameterService;
	@Resource
	QuotationModelFinancesDAO quotationModelFinancesDAO;
	@Resource 
	ServiceElementService serviceElementService;
	@Resource OpenEndService openEndService;
	@Resource
	private 	ProductDAO	productDAO;
	
	private Long qmdId;
	private boolean forwaredPage = false;
	private QuotationModel quotationModel;
	private FleetMaster fleetMaster;
	private Long qmReplacementFmsId;
	private QuoteOverviewOEVO quoteOverviewOEVO;

	private final String COST_FIXED = "Fixed";
	private final String COST_VARIABLE = "Variable";
	private final String FACTORY = "Factory Order";
	private final String LOCATE = "Locate Order";
	private final int DECIMAL_DIG_PRECISION_THREE = 3;
	private final int DEPRECIATION_FACTOR_SCALE = 7;
	private static int MAX_STEPS = 5;

	private boolean disableCalculateAndSave = false;
	private boolean includeUnCalculatedCost = false;
	private boolean recalculateAndSave = false;
	private boolean isNewQuote = false;

	private boolean isQuoteCalculated = false;

	private QuotationProfitability quotationProfitability;
	private BigDecimal grdDeliveryRechargeAmount;
	private String grdDeliveryRechargeAmountLabel;
	private QuotationModel calcQuotationModel;
	private List<QuotationStepStructureVO> quoteSteps;
	private List<QuotationStepStructureVO> definitionSteps;
	private boolean hideInvoiceAdjustment = false;

	Map<String, String> inputFieldIdMaps = new HashMap<String, String>();
	private String splMsgforStep;
	//private BigDecimal oldDepreciationFactor;
	private boolean invalidAccessPath = false;
	private final String ORDER_TYPE_LOCATE = "L";
	
	final static String GROWL_UI_ID = "growl";
	private boolean serviceElementChanged = false;
	private List<ServiceElementsVO> availableElements;
	private boolean recalculatedOnUI; // added for bug 16476
	
	
	@PostConstruct
	public void init() {
		invalidAccessPath = false;
		super.openPage();
		try {
			logger.debug("init is called");
			inputFieldIdMaps.put(MalConstants.FIN_PARAM_ADMIN_FACT, "adminFactor");
			inputFieldIdMaps.put(MalConstants.FIN_PARAM_INT_ADJ, "interestAdj");
			inputFieldIdMaps.put(MalConstants.FIN_PARAM_OE_INV_ADJ, "invoiceAdj");
			inputFieldIdMaps.put(MalConstants.FIN_PARAM_GRD_DEL_RECHARGE, "grdDelRcAmt");
			inputFieldIdMaps.put("DEPRECIATION_FACTOR", "deprFactor");
			inputFieldIdMaps.put("FINAL_NBV", "finalNBV");
			inputFieldIdMaps.put("APPROVED_MIN_IRR_LBL_ID", "appMinIrrLabelId");
			inputFieldIdMaps.put("APPROV_MIN_IRR", "appMinIrr");
			inputFieldIdMaps.put("TOTAL_INTEREST", "interestTotalRate");
			inputFieldIdMaps.put("CAPITAL_CONTRIBUTION", "capitalContribution");

			if (qmdId != null) {
				quoteOverviewOEVO = new QuoteOverviewOEVO();
				quotationModel = quotationService.getQuotationModelWithCostAndAccessories(qmdId);
				QuotationElement mainQuoteElement = rentalCalculationService
						.getMainQuotationModelElement(quotationModel);
				Product	product	= productDAO.findById(quotationModel.getQuotation().getQuotationProfile().getPrdProductCode()).orElse(null);
				quoteOverviewOEVO.setProductName(product.getProductName());
				
				if (mainQuoteElement == null) {
					disableCalculateAndSave = false;
					isNewQuote = true;
					includeUnCalculatedCost = true;
					
					//check for formal extension
					if ((!rentalCalculationService.isFormalExtension(quotationModel)) &&  (!rentalCalculationService.isQuoteForPartialCalculation(quotationModel))){
						calcQuotationModel = rentalCalculationService.getCalculatedQuotationModel(qmdId, true,quoteOverviewOEVO.getCapitalContribution());
					}else{
						calcQuotationModel = rentalCalculationService.getCalculatedQuotationModel(qmdId, false,quoteOverviewOEVO.getCapitalContribution());
					}
					
					//OER-541 Capturing the capital cost of the main element so that it can be used in re-calc. 
					mainQuoteElement = rentalCalculationService.getMainQuotationModelElement(calcQuotationModel);										
				} else {					
					disableCalculateAndSave = rentalCalculationService.isQuoteEditable(quotationModel) ? false : true;
					quotationModel = quotationService.changeProfileOnQuotationModels(quotationModel);
					if(!disableCalculateAndSave) {
						// calculated quote
						//for RC-1287
					 	Boolean isCapitalCostChange = false;
						if (quotationModel.getOrderType() != null
								&& !quotationModel.getOrderType().equals(ORDER_TYPE_LOCATE)) {
						 	isCapitalCostChange = quotationService
									.updateVrbDiscountValues(quotationModel);						
						 	if(isCapitalCostChange){
							 	quotationModel = quotationService.getQuotationModelWithCostAndAccessories(qmdId);
						 	}
						}
						if ("Y".equals(quotationModel.getReCalcNeeded())|| isCapitalCostChange) {
							//check for formal extension
							if ((!rentalCalculationService.isFormalExtension(quotationModel)) &&  (!rentalCalculationService.isQuoteForPartialCalculation(quotationModel))){
								serviceElementChanged = hasServiceElementsChanged(quotationModel);
								calcQuotationModel = rentalCalculationService.getCalculatedQuotationModel(qmdId, true,quoteOverviewOEVO.getCapitalContribution());
							}else{
								calcQuotationModel = rentalCalculationService.getCalculatedQuotationModel(qmdId, false,quoteOverviewOEVO.getCapitalContribution());
							}
						
							recalculateAndSave = true;
						}

					}

				}
				
				//OER-541 Capturing the capital cost of the main element so that it can be used in re-calc.				
				quoteOverviewOEVO.setMainElementAdjustedCost(mainQuoteElement.getCapitalCost());
				
				//for RC-1655 		
				if(quotationModel.getDepreciationFactor() != null){ 
					quoteOverviewOEVO.setDepreciationFactor(quotationModel.getDepreciationFactor());
				}
				loadStaticData();
				loadDynamicCalculatedData();
				setEnableDisableStatusFinParam();
				setCompareFieldValues();
				
				if(serviceElementChanged){
					FacesContext context = FacesContext.getCurrentInstance();
				    context.addMessage("warnings", new FacesMessage(FacesMessage.SEVERITY_WARN, "Services details have changed.", "The services details for this quote have changed. Please click the 'Service Elements' link to view the results."));
				    RequestContext.getCurrentInstance().update(GROWL_UI_ID);
				    serviceElementChanged = false;
				}
				
				logger.info("init:Qmd Id=" + qmdId);
			} else {
				super.addErrorMessage("generic.error",
						"This link is not available through VISION directly. Please use Willow to get quote overview.");
				// forwardToURL("/view/notImplemented");
				invalidAccessPath = true;
			}
		} catch (Exception ex) {
			handleException("generic.error", new String[]{"loading"}, ex, "init");
			logger.error(ex);
		}
	}

	private void setCompareFieldValues() {
		quoteOverviewOEVO.setFinalNBVToCompare(quoteOverviewOEVO.getFinalNBV());
		quoteOverviewOEVO.setDepreciationFactorToCompare(quoteOverviewOEVO.getDepreciationFactor());
		quoteOverviewOEVO.setCapitalContributionToCompare(quoteOverviewOEVO.getCapitalContribution());
		quoteOverviewOEVO.setInvoiceAdjustmentToCompare(quoteOverviewOEVO.getInvoiceAdjustment());
	}

	public void updateFinalNBV() {
		setSplMsgforStep(null);
		BigDecimal finalNBV = profitabilityService.getFinalNBV(quoteOverviewOEVO.getCustomerCost(),
				quoteOverviewOEVO.getDepreciationFactor(), new BigDecimal(quotationModel.getContractPeriod()));
		quoteOverviewOEVO.setFinalNBV(finalNBV);
		quoteOverviewOEVO.setFinalNBVToCompare(quoteOverviewOEVO.getFinalNBV());
		quoteOverviewOEVO.setDepreciationFactorToCompare(quoteOverviewOEVO.getDepreciationFactor());
	}

	public void updateDepFactor() {
		setSplMsgforStep(null);
		BigDecimal depreciationFactor = profitabilityService.getDepreciationFactor(quoteOverviewOEVO.getCustomerCost(),
				quoteOverviewOEVO.getFinalNBV(), new BigDecimal(quotationModel.getContractPeriod()));
		depreciationFactor = depreciationFactor.movePointRight(2);
		depreciationFactor = depreciationFactor.setScale(DEPRECIATION_FACTOR_SCALE, RoundingMode.HALF_UP);
		quoteOverviewOEVO.setDepreciationFactor(depreciationFactor);
	}

	@Override
	protected void loadNewPage() {
		thisPage.setPageDisplayName("Quote Overview - Open End");
		thisPage.setPageUrl(ViewConstants.QUOTE_OVERVIEW_OE);
		if (thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_FORWARDED_PAGE) != null) {
			forwaredPage = (Boolean) thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_FORWARDED_PAGE);
		}
		if (thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_QUOTE_MODEL_ID) != null) {
			qmdId = (Long) thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_QUOTE_MODEL_ID);
		} else {
			String paramQmdId = getRequestParameter("qmdId");
			if (paramQmdId != null) {
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
		/*oldDepreciationFactor = (BigDecimal) thisPage.getRestoreStateValues().get(
				ViewConstants.VIEW_PARAM_DEPRECIATION_FACTOR);*/
		
	}

	private void loadStaticData() throws MalBusinessException {

		logger.debug("quoteOverviewOEBean:loadStaticData:Start");
		
		if (isNewQuote || recalculateAndSave) {
			QuoteCost quoteCost = capitalCostOverviewService.getQuoteCost(quotationModel);
			quoteOverviewOEVO.setDealCost(quoteCost.getDealCost());
			quoteOverviewOEVO.setCustomerCost(quoteCost.getCustomerCost());
		} else {
			QuoteCost quoteCost = capitalCostOverviewService.getQuoteCost(quotationModel);
			quoteOverviewOEVO.setDealCost(quoteCost.getDealCost());
			quoteOverviewOEVO.setCustomerCost(quoteCost.getCustomerCost());
		}

		quoteOverviewOEVO.setCapitalContribution(quotationModel.getCapitalContribution() == null ? BigDecimal.ZERO
				: quotationModel.getCapitalContribution());
		quoteOverviewOEVO.setCapitalContributionToCompare(quoteOverviewOEVO.getCapitalContribution());

		quoteOverviewOEVO.setAccountCode(quotationModel.getQuotation().getExternalAccount().getExternalAccountPK()
				.getAccountCode());
	
		quoteOverviewOEVO.setAccountName(quotationModel.getQuotation().getExternalAccount().getAccountName());
		quoteOverviewOEVO.setQuote(Long.toString(quotationModel.getQuotation().getQuoId()) + "/"
				+ Long.toString(quotationModel.getQuoteNo()) + "/" + Long.toString(quotationModel.getRevisionNo()));
		quoteOverviewOEVO.setUnitNo(quotationModel.getUnitNo());
		quoteOverviewOEVO.setUnitDesc(quotationModel.getModel().getModelDescription());

		// replacement for unit
		qmReplacementFmsId = quotationModel.getReplacementFmsId();
		if (qmReplacementFmsId != null) {
			fleetMaster = fleetMasterService.getFleetMasterByFmsId(qmReplacementFmsId);
			quoteOverviewOEVO.setUnitNoReplacement(fleetMaster.getUnitNo());
		}

		String leaseType = quotationService.getLeaseType(qmdId);
		if (leaseType != null) {
			ProductTypeCode productTypeCode = productTypeCodeDAO.findById(leaseType).orElse(null);
			quoteOverviewOEVO.setProductDesc(productTypeCode == null ? "" : productTypeCode.getDescription());
		}
		
		quoteOverviewOEVO.setOrderType(quotationModel.getOrderType().equals("F") ? FACTORY : LOCATE);
		//Plate Type Code Desc
		if (quotationModel.getPlateTypeCode() != null) {
			quoteOverviewOEVO.setPlateTypeCodeDescription(quotationService.getPlateTypeDescription(quotationModel.getPlateTypeCode()));
		}
		//Driver Grade Group Description
		if (quotationModel.getQuotation().getDriverGradeGroup() != null) {
			quoteOverviewOEVO.setGradeGroupDescription(quotationService.getDriverGradeGroupDescription(quotationModel.getQuotation().getDriverGradeGroup()));
		}
		
		quoteOverviewOEVO.setQuoteProfileDesc(quotationModel.getQuotation().getQuotationProfile().getDescription());
		quoteOverviewOEVO.setCostTreatment(quotationModel.getPreContractFixedCost().equals("F") ? COST_FIXED
				: COST_VARIABLE);
		quoteOverviewOEVO.setInterestTreatment(quotationModel.getPreContractFixedInterest().equals("F") ? COST_FIXED
				: COST_VARIABLE);

		quoteOverviewOEVO.setTerm(quotationModel.getContractPeriod());
		quoteOverviewOEVO.setDistance(quotationModel.getContractDistance());

		quoteOverviewOEVO.setProjectedReplacementMonths(quotationModel.getProjectedMonths());
		String floatDescription = quotationModel.getQuotation().getQuotationProfile().getVariableRate().equals("V") ? "Float"
				: "Non-Float";
		quoteOverviewOEVO.setFloatType(floatDescription);
		String reviewFrequencyDescription = quotationModel.getQuotation().getQuotationProfile().getReviewFrequency();
		if (reviewFrequencyDescription != null && !reviewFrequencyDescription.isEmpty()) {
			reviewFrequencyDescription = "(" + reviewFrequencyDescription + ")";
		}
		quoteOverviewOEVO.setFloatFrequency(reviewFrequencyDescription);
		quoteOverviewOEVO.setInterestIndex(quotationModel.getQuotation().getQuotationProfile().getItcInterestType());
		QuotationModelFinances quotationModelFinancesInDb = financeParameterService.getQuotationModelFinances(
				quotationModel.getQmdId(), MalConstants.FIN_PARAM_GRD_DEL_RECHARGE);
		grdDeliveryRechargeAmount = quotationModelFinancesInDb != null ? quotationModelFinancesInDb.getnValue() : null;
		if (grdDeliveryRechargeAmount != null) {
			grdDeliveryRechargeAmount = CommonCalculations.getRoundedValue(grdDeliveryRechargeAmount, 2);
			grdDeliveryRechargeAmountLabel = DecimalFormat.getCurrencyInstance(Locale.US).format(
					grdDeliveryRechargeAmount);
			// NumberFormat.getCurrencyInstance(Locale.US).format(number,
			// toAppendTo, pos)
		}
		// for rc-1176
		BigDecimal finalResidual = BigDecimal.ZERO;
		BigDecimal mainElementResidual = quotationModel.getResidualValue();
		if (isNewQuote) {
			BigDecimal equipmentResidual = rentalCalculationService.getEquipmentResidualOfNewOEQuote(quotationModel
					.getQmdId());
			finalResidual = mainElementResidual.add(equipmentResidual);
		} else {

			BigDecimal equipmentResidual = rentalCalculationService.getEquipmentResidual(quotationModel);
			finalResidual = mainElementResidual.add(equipmentResidual);
			// above code is commented for RC-1200
			// final residual will be populated from last step for a calculated
			// quote

		}

		quoteOverviewOEVO.setMainElementResidual(mainElementResidual);

		finalResidual = openEndService.getModifiedNBV(finalResidual);
		quoteOverviewOEVO.setFinalResidual(finalResidual);
		quoteOverviewOEVO.setFinalNBV(finalResidual);
		Double adminFactorFinV = quotationService.getFinanceParam(MalConstants.FIN_PARAM_ADMIN_FACT,
				quotationModel.getQmdId(), quotationModel.getQuotation().getQuotationProfile().getQprId());

		quoteOverviewOEVO.setAdminFactor(BigDecimal.valueOf(adminFactorFinV));

		BigDecimal bdPeriod = BigDecimal.valueOf(quotationModel.getContractPeriod());
		
		if (recalculateAndSave) {
			BigDecimal depreciationFactor = null;
			if (quoteOverviewOEVO.getDepreciationFactor() == null) {
				// handle old quotes when new column was not introduced
				QuoteCost quoteCost = capitalCostOverviewService.getQuoteCost(quotationModel);
				BigDecimal oldCustomerCost = quoteCost.getCustomerCost();
				depreciationFactor = profitabilityService.getDepreciationFactor(oldCustomerCost, finalResidual, bdPeriod);
				depreciationFactor = depreciationFactor.movePointRight(2);
				quoteOverviewOEVO.setDepreciationFactor(depreciationFactor.setScale(DEPRECIATION_FACTOR_SCALE, RoundingMode.HALF_UP));
			} else {
				depreciationFactor = quoteOverviewOEVO.getDepreciationFactor();
			}
			BigDecimal newNetBookValue = profitabilityService.getFinalNBV(quoteOverviewOEVO.getCustomerCost(), depreciationFactor,
					new BigDecimal(quotationModel.getContractPeriod()));
			newNetBookValue = openEndService.getModifiedNBV(newNetBookValue);
			quoteOverviewOEVO.setFinalNBV(newNetBookValue);
		}else{
			if(! isNewQuote){
				if(quoteOverviewOEVO.getDepreciationFactor() == null){ //for older quotes when column was not there
					BigDecimal depreciationFactor = profitabilityService.getDepreciationFactor(
							quoteOverviewOEVO.getCustomerCost(), finalResidual, bdPeriod);
					depreciationFactor = depreciationFactor.movePointRight(2);
					depreciationFactor = depreciationFactor.setScale(DEPRECIATION_FACTOR_SCALE, RoundingMode.HALF_UP);
					quoteOverviewOEVO.setDepreciationFactor(depreciationFactor);
				}
			}else{
				if(quoteOverviewOEVO.getDepreciationFactor() != null){ 
					BigDecimal newNetBookValue = profitabilityService.getFinalNBV(quoteOverviewOEVO.getCustomerCost(),
							quoteOverviewOEVO.getDepreciationFactor(), new BigDecimal(quotationModel.getContractPeriod()));
					newNetBookValue = openEndService.getModifiedNBV(newNetBookValue);
					quoteOverviewOEVO.setFinalNBV(newNetBookValue);
				}else{
					BigDecimal depreciationFactor = profitabilityService.getDepreciationFactor(
							quoteOverviewOEVO.getCustomerCost(), finalResidual, bdPeriod);
					depreciationFactor = depreciationFactor.movePointRight(2);
					depreciationFactor = depreciationFactor.setScale(DEPRECIATION_FACTOR_SCALE, RoundingMode.HALF_UP);
					quoteOverviewOEVO.setDepreciationFactor(depreciationFactor);
				}
				
			}
			
			
		}
			
		if(!rentalCalculationService.isFormalExtension(quotationModel)){
			Double invoiceAdjustment = quotationService.getFinanceParam(MalConstants.FIN_PARAM_OE_INV_ADJ, quotationModel.getQmdId(),
					quotationModel.getQuotation().getQuotationProfile().getQprId());
			quoteOverviewOEVO.setInvoiceAdjustment(BigDecimal.valueOf(invoiceAdjustment));}
		else{
			QuotationModelFinances quotationModelFinances = financeParameterService.getQuotationModelFinances(quotationModel.getQmdId(),MalConstants.FIN_PARAM_OE_INV_ADJ);
			if(quotationModelFinances != null){
				quoteOverviewOEVO.setInvoiceAdjustment(quotationModelFinances.getnValue());
			}
			else{
				quoteOverviewOEVO.setInvoiceAdjustment(BigDecimal.ZERO);
			}
		}
		quoteOverviewOEVO.setInvoiceAdjustmentToCompare(quoteOverviewOEVO.getInvoiceAdjustment());

		if (isNewQuote) {
			hideInvoiceAdjustment = true;
		} else {
			QuotationModelFinances qmf = getQuotationModelFinancesForInvoiceAdjustment();
			if (qmf != null) {
				hideInvoiceAdjustment = rentalCalculationService.isReportsHidden(qmf);
			} else {
				hideInvoiceAdjustment = false;
			}

		}

		Double interestAdjustment = quotationService.getFinanceParam(MalConstants.FIN_PARAM_INT_ADJ,
				quotationModel.getQmdId(), quotationModel.getQuotation().getQuotationProfile().getQprId());
		BigDecimal bdInterestAdjustment = BigDecimal.valueOf(interestAdjustment);
		quoteOverviewOEVO.setInterestAdjustment(bdInterestAdjustment);

		if (isNewQuote) {
			BigDecimal baseRate = getBaseRate(quotationModel);
			if (baseRate == null) {
				baseRate = new BigDecimal(0);
			}
			baseRate = baseRate.setScale(DECIMAL_DIG_PRECISION_THREE, RoundingMode.HALF_UP);
			quoteOverviewOEVO.setInterestBaseRate(baseRate);
			quoteOverviewOEVO.setInterestTotalRate(quoteOverviewOEVO.getInterestBaseRate().add(bdInterestAdjustment)
					.setScale(DECIMAL_DIG_PRECISION_THREE, RoundingMode.HALF_UP));
		} else {
			quoteOverviewOEVO.setInterestTotalRate(quotationModel.getInterestRate());
			quoteOverviewOEVO.setInterestBaseRate(quotationModel.getInterestRate().subtract(bdInterestAdjustment)
					.setScale(DECIMAL_DIG_PRECISION_THREE, RoundingMode.HALF_UP));
		}

		logger.debug("loadStaticData:End");

	}

	private void loadDynamicCalculatedData() throws MalBusinessException, Exception {
		logger.debug("quoteOverviewOEBean:loadDynamicCalculatedData:Start");
		quotationProfitability = profitabilityService.getQuotationProfitability(quotationModel);
		if (quotationProfitability != null && isNewQuote == false) {
			logger.debug("OE quote is loading from profitability.." + qmdId);
			quoteOverviewOEVO.setBaseIrr(CommonCalculations.getRoundedValue(quotationProfitability.getProfitBase(),	RentalCalculationService.IRR_CALC_DECIMALS));
			quoteOverviewOEVO.setProfitAdj(CommonCalculations.getRoundedValue(quotationProfitability.getProfitAdjustment(), RentalCalculationService.IRR_CALC_DECIMALS));
			quoteOverviewOEVO.setIrrApprovedUser(quotationProfitability.getIrrApprovedUser());
			quoteOverviewOEVO.setIrrApprovedDate(quotationProfitability.getIrrApprovedDate());
			quoteOverviewOEVO.setActualIrrValue(quotationProfitability.getProfitAmount() != null ? CommonCalculations
					.getRoundedValue(quotationProfitability.getProfitAmount(), RentalCalculationService.IRR_CALC_DECIMALS) : null);
			quoteOverviewOEVO.setIrrApprovedLimit(quotationProfitability.getIrrApprovedLimit() != null ? String
					.valueOf(CommonCalculations.getRoundedValue(quotationProfitability.getIrrApprovedLimit() != null ? quotationProfitability
									.getIrrApprovedLimit() : null, DECIMAL_DIG_PRECISION_THREE)) : null);
			if(quoteOverviewOEVO.getIrrApprovedLimit() != null) {
				quoteOverviewOEVO.setIrrApprovedLimit(quoteOverviewOEVO.getIrrApprovedLimit()+"%");
			}
			

		} else {// it should be brand new quote copied from  non st order.
			logger.debug("new OE quote is loading .." + qmdId);
			quoteOverviewOEVO.setBaseIrr(CommonCalculations.getRoundedValue(getHurdleRate(quotationModel),RentalCalculationService.IRR_CALC_DECIMALS));
			quoteOverviewOEVO.setProfitAdj(CommonCalculations.getRoundedValue(getProfitAdj(quotationModel),RentalCalculationService.IRR_CALC_DECIMALS));
			quoteOverviewOEVO.setActualIrrValue(quoteOverviewOEVO.getMinimumIrr());
		}
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String workclass = ((User) authentication.getPrincipal()).getWorkclass();
		BigDecimal irrApprovedMinLimit = financeParameterService.getWorkclassLowerLimit(
				MalConstants.FIN_PARAM_OE_MIN_IRR_PROFIT, workclass, MalConstants.MODULE_VISION_QL);
		quoteOverviewOEVO.setIrrApprovedMinLimit(irrApprovedMinLimit);
		loadSteps();		
		Boolean isContractPeriodUpdated = checkIfContractPeriodUpdated();
		if (recalculateAndSave || isContractPeriodUpdated && !disableCalculateAndSave) {
			updateDepFactor();//HD-520 (Keep same NBV during auto calc)
			calcQuotationModel.setDepreciationFactor(quoteOverviewOEVO.getDepreciationFactor());
			if (isContractPeriodUpdated) {
				reCalculateWithDefaultSteps();
			} else {
				calculateQuote();

			}

			if (validateFinalNBV()) {
				calcQuotationModel.setInterestRate(quoteOverviewOEVO.getInterestBaseRate().add(
						quoteOverviewOEVO.getInterestAdjustment(), CommonCalculations.MC));
				
				//TODO: ideally the MulQuoteElem object should be saved with the rest of the Quotation Model
				// but most of the code has been tested to do this using the saveOrUpdateServiceElements method
				// and there is not enough time and too much risk to do this the right way this late into testing.
				// So, we are using the tested code that is also shared with the amendments screen instead.
				
				//check for formal extension
				if ((!rentalCalculationService.isFormalExtension(quotationModel)) &&  (!rentalCalculationService.isQuoteForPartialCalculation(quotationModel))){
					calcQuotationModel.getQuotation().setMulQuoteElems(null);
					//check for FE - originally was here
					if(serviceElementService.elementsHaveChanges(availableElements)){
						serviceElementService.saveOrUpdateServiceElements(calcQuotationModel, availableElements);
					}
				}
				
				rentalCalculationService.saveCalculatedQuoteOE(quotationModel, calcQuotationModel, quoteSteps,
																getUpdatedProfitabiltyObject(false), null);
				quotationModel = quotationService.getQuotationModelWithCostAndAccessories(qmdId);
				calcQuotationModel = null;
				quotationProfitability = profitabilityService.getQuotationProfitability(quotationModel);
				quoteOverviewOEVO.setInvoiceAdjustmentToCompare(quoteOverviewOEVO.getInvoiceAdjustment());
				recalculateAndSave = false;
				isQuoteCalculated = false;
				addInfoMessage("oe.quote.auto.recalculate.saved");

			}

		}
		
		setServiceElementsCost(quotationModel);
		logger.debug("quoteOverviewOEBean:loadDynamicCalculatedData:End");
	}

	private boolean checkIfContractPeriodUpdated() {
		// check if term is updated, if so recalculate steps and quote
		Long newTerm = 0L;
		for (QuotationStepStructureVO qssVO : quoteSteps) {
			newTerm = newTerm + (qssVO.getToPeriod() - qssVO.getFromPeriod() + 1);
		}
		if (newTerm.longValue() != quotationModel.getContractPeriod()) {
			return true;
		}
		return false;
	}

	private void setServiceElementsCost(QuotationModel quotationModel) {
		BigDecimal serviceValue = BigDecimal.ZERO;
		if (quotationModel != null) {
			serviceValue = serviceElementService.getServiceElementMonthlyCost(quotationModel);
		}
		quoteOverviewOEVO.setServiceElementRate(serviceValue);
	}

	private void loadSteps() throws MalBusinessException {

		quoteSteps = new ArrayList<QuotationStepStructureVO>();
	
		if (isNewQuote == false) {// not a new quote	
			quoteSteps = quotationService.getAllQuoteSteps(quotationModel.getQmdId());
			
			quoteOverviewOEVO.setFinalResidual(quoteSteps.get(quoteSteps.size() -1).getNetBookValue());
			quoteOverviewOEVO.setFinalNBV(quoteOverviewOEVO.getFinalResidual());
			for (QuotationStepStructureVO stepStructureVO : quoteSteps) {//this may have multiple quote's steps
				if(stepStructureVO.getAssociatedQmdId().equals(quotationModel.getQmdId())){
					quoteOverviewOEVO.setActualLeaseRate(stepStructureVO.getLeaseRate());
					break;
				}
			}
			

		} else {
			List<QuotationStepStructure> quotationStepStructureList = quotationService.getQuotationModelStepStructure(quotationModel.getQmdId());
			if (quotationStepStructureList != null && !quotationStepStructureList.isEmpty()) {
				for (QuotationStepStructure quotationStepStructure : quotationStepStructureList) {
					QuotationStepStructureVO qssVO = new QuotationStepStructureVO();
					qssVO.setFromPeriod(quotationStepStructure.getId().getFromPeriod());
					qssVO.setToPeriod(quotationStepStructure.getToPeriod().longValue());
					quoteSteps.add(qssVO);

				}

			} else {
				QuotationStepStructureVO qssVO = new QuotationStepStructureVO();
				qssVO.setFromPeriod(1L);
				qssVO.setToPeriod(quotationModel.getContractPeriod());

				quoteSteps.add(qssVO);
			}
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

	public void calc() throws MalBusinessException {
		try {
			logger.debug("quoteOverviewOEBean:Calc:Start");
			setSplMsgforStep(null);
			isQuoteCalculated = false;
			boolean isUpdateSuccess = updateInterestTotalRate();
			if(! isUpdateSuccess){
				return;
			}

			if (quoteOverviewOEVO.getDepreciationFactor().compareTo(quoteOverviewOEVO.getDepreciationFactorToCompare()) != 0) {
				updateFinalNBV();
				if (!validateFinalNBV())
					return;

			} else if (quoteOverviewOEVO.getFinalNBV().compareTo(quoteOverviewOEVO.getFinalNBVToCompare()) != 0) {
				if (validateFinalNBV())
					updateDepFactor();
			}

			if (!(validateTotalInterestRate() && validateFinalNBV() && validateCopitalContributionAndInvoiceAdj())) {
				return;
			}
			Boolean isVrbDiscountValuesChanged = false;
			if (quotationModel.getOrderType() != null && !quotationModel.getOrderType().equals(ORDER_TYPE_LOCATE)) {
				// see for VRB discounts(QM075) and update quotation capital
				// elements
				isVrbDiscountValuesChanged = quotationService.updateVrbDiscountValues(quotationModel);
				if (isVrbDiscountValuesChanged) {
					quotationModel = quotationService.getQuotationModelWithCostAndAccessories(qmdId);
					QuoteCost quoteCost = capitalCostOverviewService.getQuoteCost(quotationModel);
					quoteOverviewOEVO.setDealCost(quoteCost.getDealCost());
					quoteOverviewOEVO.setCustomerCost(quoteCost.getCustomerCost());
				}
			}
			calculateQuote();

			if (isNewQuote)
				setServiceElementsCost(calcQuotationModel);

			setDirtyData(true);

			if (!validateFinalNBV()) {
				isQuoteCalculated = false;
			} else {
				if (isVrbDiscountValuesChanged) {
					// Pending
					// need to save calculated quote for RC-1179
					// no need to save as of now,may be possible in releases
					addInfoMessage("quote.calculation.success.after.vrbupdates");
				} else {
					addInfoMessage("quote.calculation.success");
				}

			}
			
			if(serviceElementChanged){
				setServiceElementsCost(calcQuotationModel);
				FacesContext context = FacesContext.getCurrentInstance();
			    context.addMessage("warnings", new FacesMessage(FacesMessage.SEVERITY_WARN, "Services details have changed.", "The services details for this quote have changed. Please click the 'Service Elements' link to view the results."));
			    RequestContext.getCurrentInstance().update(GROWL_UI_ID);
			    serviceElementChanged = false;
			}
			recalculatedOnUI=true;//added for Bug 16476
			logger.debug("quoteOverviewOEBean:Calc:End");
		} catch (Exception ex) {
			handleException("generic.error.occured.while", new String[] { "calculating quote " }, ex, null);
		}

	}

	private boolean validateCopitalContributionAndInvoiceAdj() {

		BigDecimal netClientCost = BigDecimal.ZERO;
		if ((quoteOverviewOEVO.getInvoiceAdjustment().compareTo(quoteOverviewOEVO.getInvoiceAdjustmentToCompare()) != 0)
				|| (quoteOverviewOEVO.getCapitalContribution().compareTo(
						quoteOverviewOEVO.getCapitalContributionToCompare()) != 0)) {// InvoiceAdj
																						// and
																						// contribution
																						// got
																						// changed

			netClientCost = quoteOverviewOEVO.getCustomerCost().subtract(quoteOverviewOEVO.getCapitalContribution())
					.add(quoteOverviewOEVO.getCapitalContributionToCompare());
			netClientCost = netClientCost.add(quoteOverviewOEVO.getInvoiceAdjustment()).subtract(
					quoteOverviewOEVO.getInvoiceAdjustmentToCompare());

			if (netClientCost.doubleValue() <= 0) {
				// error for Capital Contribution and Invoice Adj more than cap
				// cost
				addErrorMessage(inputFieldIdMaps.get("CAPITAL_CONTRIBUTION"), "custom.message",
						talMessage.getMessage("costRelatedValidation", "Capital Contribution and Invoice Adj"));
				UIComponent comp = getComponent(inputFieldIdMaps.get(MalConstants.FIN_PARAM_OE_INV_ADJ)); // mark
																											// red
																											// to
																											// invoice
																											// also
				if (comp != null)
					((UIInput) comp).setValid(false);
				return false;
			}

		}

		return true;
	}

	private boolean validateFinalNBV() {
		if (quoteOverviewOEVO.getFinalNBV() != null) {
			if (quoteOverviewOEVO.getFinalNBV().compareTo(
					new BigDecimal(quoteOverviewOEVO.getCustomerCost().toString())) >= 0) {
				// error for residual more than cap cost
				addErrorMessage(inputFieldIdMaps.get("FINAL_NBV"), "custom.message",
						talMessage.getMessage("finalNBVValidation"));
				return false;
			}

			if (quoteOverviewOEVO.getFinalNBV().compareTo(BigDecimal.ZERO) < 0) {
				// error for residual is negative
				addErrorMessage(inputFieldIdMaps.get("FINAL_NBV"), "custom.message",
						talMessage.getMessage("negative.notallowed", "Final NBV"));
				return false;
			}
			return true;
		}
		return true;
	}

	public String cancel() {	
		 return super.cancelPage(); 
	}

	private void calculateQuote() throws MalBusinessException {

		if (calcQuotationModel == null) {
			//check for formal extension
			if ((!rentalCalculationService.isFormalExtension(quotationModel)) &&  (!rentalCalculationService.isQuoteForPartialCalculation(quotationModel))){
				// use quotationModel not calcQuotationModel because we want to detect changes /vs what is from the DB not what we are calculating
				serviceElementChanged = hasServiceElementsChanged(quotationModel);
				calcQuotationModel = rentalCalculationService.getCalculatedQuotationModel(qmdId, true,quoteOverviewOEVO.getCapitalContribution());
			}else{
				calcQuotationModel = rentalCalculationService.getCalculatedQuotationModel(qmdId, false,quoteOverviewOEVO.getCapitalContribution());
			}
			
		}else{
			//check for formal extension
			if ((!rentalCalculationService.isFormalExtension(quotationModel)) &&  (!rentalCalculationService.isQuoteForPartialCalculation(quotationModel))){
				// use quotationModel not calcQuotationModel because we want to detect changes /vs what is from the DB not what we are calculating
				availableElements = serviceElementService.determineElementsWithChanges(quotationModel);
			}
		}

		BigDecimal adminFactor = quoteOverviewOEVO.getAdminFactor();
		BigDecimal customerCost = quoteOverviewOEVO.getCustomerCost();		
		BigDecimal interestRate = quoteOverviewOEVO.getInterestTotalRate();
		interestRate = interestRate.divide(new BigDecimal(1200), CommonCalculations.MC);
		BigDecimal depreciationFactor = quoteOverviewOEVO.getDepreciationFactor();
		BigDecimal mainElementAdjustedCost = quoteOverviewOEVO.getMainElementAdjustedCost();
		
		depreciationFactor = depreciationFactor.divide(new BigDecimal(100), CommonCalculations.MC);
		boolean custCostChanged = false;
		// adjust invoice adjustment
		if (quoteOverviewOEVO.getInvoiceAdjustment().compareTo(quoteOverviewOEVO.getInvoiceAdjustmentToCompare()) != 0) {
			
			for(int i=0; i < calcQuotationModel.getQuotationElements().size(); i++) {
				QuotationElement qel = calcQuotationModel.getQuotationElements().get(i);
				if(qel.getLeaseElement().getElementType().equals(MalConstants.FINANCE_ELEMENT)
						&& MALUtilities.convertYNToBoolean(qel.getIncludeYn())) {
					if(MALUtilities.isEmpty(qel.getQuotationModelAccessory()) && MALUtilities.isEmpty(qel.getQuotationDealerAccessory())) {	
								calcQuotationModel.getQuotationElements().get(i).setCapitalCost(calcQuotationModel.getQuotationElements().get(i).getCapitalCost().add(quoteOverviewOEVO.getInvoiceAdjustment().subtract(
										quoteOverviewOEVO.getInvoiceAdjustmentToCompare())));
					}
				}
			}
			
			customerCost = customerCost.add(quoteOverviewOEVO.getInvoiceAdjustment().subtract(
					quoteOverviewOEVO.getInvoiceAdjustmentToCompare()));
			
			
			//OER-541 Capturing the capital cost of the main element so that it can be used in re-calc.			
			mainElementAdjustedCost = mainElementAdjustedCost.add(quoteOverviewOEVO.getInvoiceAdjustment().subtract(
					quoteOverviewOEVO.getInvoiceAdjustmentToCompare()));			
			quoteOverviewOEVO.setMainElementAdjustedCost(mainElementAdjustedCost);
			
			quoteOverviewOEVO.setCustomerCost(customerCost);// We need to update
															// UI for client
															// cost
			
			quoteOverviewOEVO.setInvoiceAdjustmentToCompare(quoteOverviewOEVO.getInvoiceAdjustment());
			
			custCostChanged = true;
		}

		// adjust capital contribution
		if (quoteOverviewOEVO.getCapitalContribution().compareTo(quoteOverviewOEVO.getCapitalContributionToCompare()) != 0) {
			
			for(int i=0; i < calcQuotationModel.getQuotationElements().size(); i++) {
				QuotationElement qel = calcQuotationModel.getQuotationElements().get(i);
				if(qel.getLeaseElement().getElementType().equals(MalConstants.FINANCE_ELEMENT)
						&& MALUtilities.convertYNToBoolean(qel.getIncludeYn())) {
					if(MALUtilities.isEmpty(qel.getQuotationModelAccessory()) && MALUtilities.isEmpty(qel.getQuotationDealerAccessory())) {	
								calcQuotationModel.getQuotationElements().get(i).setCapitalCost(calcQuotationModel.getQuotationElements().get(i).getCapitalCost().subtract(quoteOverviewOEVO.getCapitalContribution()).add(
										quoteOverviewOEVO.getCapitalContributionToCompare()));
					}
				}
			}
			
			customerCost = customerCost.subtract(quoteOverviewOEVO.getCapitalContribution()).add(
					quoteOverviewOEVO.getCapitalContributionToCompare());
			
			//OER-541 Capturing the capital cost of the main element so that it can be used in re-calc.			
			mainElementAdjustedCost = mainElementAdjustedCost.subtract(quoteOverviewOEVO.getCapitalContribution()).add(
					quoteOverviewOEVO.getCapitalContributionToCompare());			
			quoteOverviewOEVO.setMainElementAdjustedCost(mainElementAdjustedCost);
			
			quoteOverviewOEVO.setCustomerCost(customerCost);// We need to update
															// UI for client
															// cost
			
			quoteOverviewOEVO.setDealCost(quoteOverviewOEVO.getDealCost()
					.subtract(quoteOverviewOEVO.getCapitalContribution())
					.add(quoteOverviewOEVO.getCapitalContributionToCompare()));
			
			quoteOverviewOEVO.setCapitalContributionToCompare(quoteOverviewOEVO.getCapitalContribution());
			
			custCostChanged = true;
		}

		if (custCostChanged) {
			// if NBV is zero then again NBV should come zero irrespective of
			// cost and we are not updating it again
			if (quoteOverviewOEVO.getFinalNBV().compareTo(BigDecimal.ZERO) != 0) {

				BigDecimal newNetBookValue = profitabilityService.getFinalNBV(quoteOverviewOEVO.getCustomerCost(),
						quoteOverviewOEVO.getDepreciationFactor(), new BigDecimal(quotationModel.getContractPeriod()));
				newNetBookValue = openEndService.getModifiedNBV(newNetBookValue);
				quoteOverviewOEVO.setFinalNBV(newNetBookValue);
				quoteOverviewOEVO.setFinalNBVToCompare(newNetBookValue);
			}
		}
		
		
		//OER-254 Replaced calc method that was based on total cap cost with one that is based on cost for each element	
		//  Note: Main element is adjusted above when there are changes in invoice adj and/or capital contribution
		List<BigDecimal> elementCapCosts = new ArrayList<>();
		for(int i=0; i < calcQuotationModel.getQuotationElements().size(); i++) {
			QuotationElement qel = calcQuotationModel.getQuotationElements().get(i);
			if(qel.getLeaseElement().getElementType().equals(MalConstants.FINANCE_ELEMENT)
					&& MALUtilities.convertYNToBoolean(qel.getIncludeYn())) {
				if(MALUtilities.isEmpty(qel.getQuotationModelAccessory()) && MALUtilities.isEmpty(qel.getQuotationDealerAccessory())) {					
					elementCapCosts.add(quoteOverviewOEVO.getMainElementAdjustedCost());
				} else {
					elementCapCosts.add(qel.getCapitalCost());					
				}
			}
		}	
		this.quoteSteps = rentalCalculationService.updateQuotationStepStructureWithElementsCost(calcQuotationModel, this.quoteSteps);
		this.quoteSteps = profitabilityService.calculateOEStepLease(depreciationFactor, quoteOverviewOEVO.getFinalNBV() , interestRate, adminFactor, this.quoteSteps);		

		
		BigDecimal costToCompany = quoteOverviewOEVO.getDealCost();
		BigDecimal adminFee = profitabilityService.getFinanceParameter(MalConstants.CLOSED_END_LEASE_ADMIN, calcQuotationModel.getQmdId(), calcQuotationModel.getQuotation().getQuotationProfile().getQprId());
		BigDecimal calculatedIrr = profitabilityService.calculateIrrFromOEStep(this.quoteSteps, costToCompany,adminFee);
		calculatedIrr = calculatedIrr != null ? CommonCalculations.getRoundedValue(calculatedIrr, RentalCalculationService.IRR_CALC_DECIMALS) : null;

		quoteOverviewOEVO.setActualIrrValue(calculatedIrr);
		quoteOverviewOEVO.setActualLeaseRate(quoteSteps.get(0).getLeaseRate());
		
		for (QuotationStepStructureVO qssVO : quoteSteps) {
			qssVO.setLeaseFactor(getLeaseFactor(qssVO));
		}

		setCompareFieldValues();
		isQuoteCalculated = true;

	}

	private boolean validateFinanceParameters(Map<String, Object> finParamMap) {
		if (finParamMap != null && !finParamMap.isEmpty()) {
			Map<String, String> finParamDescMap = new HashMap<String, String>();
			finParamDescMap.put(MalConstants.FIN_PARAM_ADMIN_FACT, "Admin Factor");
			finParamDescMap.put(MalConstants.FIN_PARAM_INT_ADJ, "Interest Adj");
			finParamDescMap.put(MalConstants.FIN_PARAM_OE_INV_ADJ, "Invoice Adj");
			finParamDescMap.put(MalConstants.FIN_PARAM_GRD_DEL_RECHARGE, "Delivery Recharge Amt");

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

	private void setEnableDisableStatusFinParam() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String workclass = ((User) authentication.getPrincipal()).getWorkclass();
		WorkClassAuthority workClassAuthority = null;

		workClassAuthority = financeParameterService.getWorkClassAuthority(MalConstants.FIN_PARAM_ADMIN_FACT,
				workclass, MalConstants.MODULE_VISION_QL);
		if (workClassAuthority != null) {
			if (!disableCalculateAndSave) {
				quoteOverviewOEVO.setDisableAdminFactor(false);
			} else {
				quoteOverviewOEVO.setDisableAdminFactor(true);
			}

		} else {
			quoteOverviewOEVO.setDisableAdminFactor(true);
		}

		workClassAuthority = financeParameterService.getWorkClassAuthority(MalConstants.FIN_PARAM_INT_ADJ, workclass,
				MalConstants.MODULE_VISION_QL);
		if (workClassAuthority != null) {
			if (!disableCalculateAndSave) {
				quoteOverviewOEVO.setDisableInterestAdjustment(false);
			} else {
				quoteOverviewOEVO.setDisableInterestAdjustment(true);
			}

		} else {
			quoteOverviewOEVO.setDisableInterestAdjustment(true);
		}

		workClassAuthority = financeParameterService.getWorkClassAuthority(MalConstants.FIN_PARAM_OE_INV_ADJ,
				workclass, MalConstants.MODULE_VISION_QL);
		if (workClassAuthority != null) {
			if (!disableCalculateAndSave) {
				quoteOverviewOEVO.setDisableInvoiceAdjustment(false);
			} else {
				quoteOverviewOEVO.setDisableInvoiceAdjustment(true);
			}

		} else {
			quoteOverviewOEVO.setDisableInvoiceAdjustment(true);
		}

		workClassAuthority = financeParameterService.getWorkClassAuthority(MalConstants.FIN_PARAM_GRD_DEL_RECHARGE,
				workclass, MalConstants.MODULE_VISION_QL);
		if (workClassAuthority != null) {
			if (!disableCalculateAndSave) {
				quoteOverviewOEVO.setDisableGrdDelRecharge(false);
			} else {
				quoteOverviewOEVO.setDisableGrdDelRecharge(true);
			}

		} else {
			quoteOverviewOEVO.setDisableGrdDelRecharge(true);
		}

	}

	public void save() {
		try {
			setSplMsgforStep(null);
			logger.debug("quoteOverviewOEBean:Save:Start");
			boolean isSaveSuccessfull = saveQuote();
			if (!isSaveSuccessfull) {
				return;
			}
			addSuccessMessage("process.success", "Save");
			logger.debug("quoteOverviewOEBean:Save:End");
		} catch (Exception ex) {
			logger.error(ex);
			handleException("generic.error.occured.while", new String[] { "saving  rental." }, ex, "save");
		}
	}

	private boolean saveQuote() throws MalBusinessException, Exception {
		// Validations will be applied here
		Map<String, Object> finParamMap = new HashMap<String, Object>();
		finParamMap.put(MalConstants.FIN_PARAM_ADMIN_FACT, quoteOverviewOEVO.getAdminFactor());
		finParamMap.put(MalConstants.FIN_PARAM_INT_ADJ, quoteOverviewOEVO.getInterestAdjustment());
		finParamMap.put(MalConstants.FIN_PARAM_OE_INV_ADJ, quoteOverviewOEVO.getInvoiceAdjustment());
		finParamMap.put(MalConstants.FIN_PARAM_GRD_DEL_RECHARGE, grdDeliveryRechargeAmount);

		if (!(validateFinanceParameters(finParamMap) && validateFinalNBV() && validateCopitalContributionAndInvoiceAdj())) {

			return false;
		}

		if (isQuoteCalculated) {
			if (calcQuotationModel == null) {
				calcQuotationModel = rentalCalculationService.getCalculatedQuotationModel(qmdId, true,quoteOverviewOEVO.getCapitalContribution());
			}
			// set interest rate
			calcQuotationModel.setInterestRate(quoteOverviewOEVO.getInterestBaseRate().add(
					quoteOverviewOEVO.getInterestAdjustment(), CommonCalculations.MC));
			calcQuotationModel.setCapitalContribution(quoteOverviewOEVO.getCapitalContribution());
			
			//For RC-1655
			calcQuotationModel.setDepreciationFactor(quoteOverviewOEVO.getDepreciationFactor());
			
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
			rentalCalculationService.saveCalculatedQuoteOE(quotationModel, calcQuotationModel, quoteSteps,
					 										getUpdatedProfitabiltyObject(false), finParamMap);

			QuotationModelFinances qmf = getQuotationModelFinancesForInvoiceAdjustment();
			if (qmf != null) {
				rentalCalculationService.saveHideInvoiceAdjustment(qmf, hideInvoiceAdjustment);
			}

			calcQuotationModel = null;
			quotationModel = quotationService.getQuotationModelWithCostAndAccessories(qmdId);
			quotationProfitability = profitabilityService.getQuotationProfitability(quotationModel);
			// fetching client cost as it may change after changing invoice
			// adjustment
			QuoteCost quoteCost = capitalCostOverviewService.getQuoteCost(quotationModel);
			quoteOverviewOEVO.setCustomerCost(quoteCost.getCustomerCost());

		}
		
		quoteOverviewOEVO.setIrrApprovedUser(quotationProfitability.getIrrApprovedUser());
		quoteOverviewOEVO.setIrrApprovedDate(quotationProfitability.getIrrApprovedDate());
		setCompareFieldValues();
		isNewQuote = false;
		isQuoteCalculated = false;
		includeUnCalculatedCost = false;
		setDirtyData(false);
		return true;
	}

	public void saveGrdDelRecharge() {

		grdDeliveryRechargeAmountLabel = grdDeliveryRechargeAmountLabel.trim().length() == 0 ? null
				: grdDeliveryRechargeAmountLabel;
		BigDecimal tempDeliveryRecharge = grdDeliveryRechargeAmountLabel == null ? null : new BigDecimal(
				grdDeliveryRechargeAmountLabel.replace("$", "").replaceAll(",", ""));
		boolean success = true;
		Map<String, Object> finParamMap = new HashMap<String, Object>();
		finParamMap.put(MalConstants.FIN_PARAM_GRD_DEL_RECHARGE, tempDeliveryRecharge);
		if (!(validateFinanceParameters(finParamMap))) {
			success = false;
		}
		try {
			if (success) {
				rentalCalculationService.saveFinanceParamOnQuote(tempDeliveryRecharge,
						MalConstants.FIN_PARAM_GRD_DEL_RECHARGE, quotationModel);
				grdDeliveryRechargeAmount = tempDeliveryRecharge;
				addSuccessMessage("saved.success", "Delivery Recharge Amount");
			}
		} catch (Exception e) {
			success = false;
			super.addErrorMessage(inputFieldIdMaps.get(MalConstants.FIN_PARAM_GRD_DEL_RECHARGE),
					"generic.error.occured.while", " Saving Delivery Recharge Amount");
		}

		if (success == false) {
			RequestContext context = RequestContext.getCurrentInstance();
			context.addCallbackParam("failure", true);
		}
	}

	public void saveAppIrrMin() {
    	
    	
    	quoteOverviewOEVO.setIrrApprovedLimit(quoteOverviewOEVO.getIrrApprovedLimit().trim().length() == 0 ? null : quoteOverviewOEVO.getIrrApprovedLimit());
    	//BigDecimal tempDeliveryRecharge = quoteOverviewVO.getGrdDeliveryRechargeAmountLabel() == null ? null : new BigDecimal(quoteOverviewVO.getGrdDeliveryRechargeAmountLabel().replace("%", "").replaceAll(",", ""));
    
    	boolean success = true;
    	if (!validateApprovedMinIrr()) {
    	     success = false;
    	}
    	try {
    	    if (success) {
    	    	
    	    	profitabilityService.saveQuotationProfitability(getUpdatedProfitabiltyObject(true));
    	    	quotationProfitability = profitabilityService.getQuotationProfitability(quotationModel);
    	    	if(quoteOverviewOEVO.getIrrApprovedLimit() != null){
    	    	   quoteOverviewOEVO.setIrrApprovedUser(quotationProfitability.getIrrApprovedUser());
    			   quoteOverviewOEVO.setIrrApprovedDate(quotationProfitability.getIrrApprovedDate());
    	        }else{
    	           quoteOverviewOEVO.setIrrApprovedUser(null);
    			   quoteOverviewOEVO.setIrrApprovedDate(null);   
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
		if (grdDeliveryRechargeAmount != null) {
			grdDeliveryRechargeAmountLabel = DecimalFormat.getCurrencyInstance(Locale.US).format(
					grdDeliveryRechargeAmount);
		} else {
			grdDeliveryRechargeAmountLabel = null;
		}

	}
	
	public void cancelSaveAppIrrMin() {
		
		if (quotationProfitability.getIrrApprovedLimit() != null) {
        	quoteOverviewOEVO.setIrrApprovedLimit(quotationProfitability.getIrrApprovedLimit() != null ?String.valueOf(CommonCalculations.getRoundedValue(
    				quotationProfitability.getIrrApprovedLimit() != null ? quotationProfitability.getIrrApprovedLimit() : null,DECIMAL_DIG_PRECISION_THREE)+"%"):null);
        }else{            	
        	quoteOverviewOEVO.setIrrApprovedLimit(null);
        }
        
    }

	private boolean validateApprovedMinIrr() {
		// handling negative value,using relative value to validate
		if (!MALUtilities.isEmpty(quoteOverviewOEVO.getIrrApprovedLimitValue())  
				&& quoteOverviewOEVO.getIrrApprovedMinLimit() != null) {
			BigDecimal temp = quoteOverviewOEVO.getMinimumIrr().add(quoteOverviewOEVO.getIrrApprovedMinLimit());

			if (quoteOverviewOEVO.getIrrApprovedLimitValue().compareTo(temp) < 0) {
				super.addErrorMessage(inputFieldIdMaps.get("APPROV_MIN_IRR"), "custom.message",
						talMessage.getMessage("approved.min.irr.range", temp.toPlainString()));
				return false;
			}
		}
		return true;
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
		if (quoteSteps.size() > 1) {

			for (int i = 1; i <= MAX_STEPS; i++) {
				QuotationStepStructureVO stepStructureVO = new QuotationStepStructureVO();
				stepStructureVO.setStepCount(i);
				if (i <= quoteSteps.size()) {
					stepStructureVO.setNetPeriod(quoteSteps.get(i - 1).getToPeriod()
							- quoteSteps.get(i - 1).getFromPeriod() + 1);
					definitionSteps.add(stepStructureVO);
				} else {
					definitionSteps.add(stepStructureVO);
				}
			}
		} else {

			long DEFAULT_STEP_SIZE = 12;
			Long term = quotationModel.getContractPeriod();
			for (int i = 1; i <= MAX_STEPS; i++) {
				QuotationStepStructureVO stepStructureVO = new QuotationStepStructureVO();
				stepStructureVO.setStepCount(i);
				if (term >= DEFAULT_STEP_SIZE && i != MAX_STEPS) {
					stepStructureVO.setNetPeriod(DEFAULT_STEP_SIZE);
				} else if (term > 0) {
					stepStructureVO.setNetPeriod(term);
				}
				definitionSteps.add(stepStructureVO);
				term = term - 12;
			}

		}
	}

	public void updateStepsAndRecalculateQuote() {
		setSplMsgforStep(null);
		boolean success = true;
		try {
			success = isStepsAreValid();
			if (success) {
				quoteSteps.clear();
				long start = 1;
				for (QuotationStepStructureVO qssVO : definitionSteps) {

					if (qssVO.getNetPeriod() != null && qssVO.getNetPeriod() > 0) {
						qssVO.setFromPeriod(start);
						qssVO.setToPeriod(start + qssVO.getNetPeriod() - 1);
						quoteSteps.add(qssVO);
						start = start + qssVO.getNetPeriod();
					}
				}
				calculateQuote();
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

		if (totalStepPeriod != quotationModel.getContractPeriod()) {
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

	private void reCalculateWithDefaultSteps() {
		try {
			quoteOverviewOEVO.setInterestBaseRate(getBaseRate(quotationModel));
			quoteOverviewOEVO.setInterestTotalRate(quoteOverviewOEVO.getInterestBaseRate()
					.add(quoteOverviewOEVO.getInterestAdjustment())
					.setScale(DECIMAL_DIG_PRECISION_THREE, RoundingMode.HALF_UP));

			quoteSteps.clear();
			quoteSteps = new ArrayList<QuotationStepStructureVO>();
			QuotationStepStructureVO stepStructureVO = new QuotationStepStructureVO();
			stepStructureVO.setStepCount(1);
			stepStructureVO.setNetPeriod(quotationModel.getContractPeriod());
			stepStructureVO.setFromPeriod(1L);
			stepStructureVO.setToPeriod(stepStructureVO.getNetPeriod());
			quoteSteps.add(stepStructureVO);

			calculateQuote();
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while", new String[] { "calculating quote" }, ex);
		}
	}

	public void viewCapitalCostsOverview() {
		logger.debug("QuoteOverviewOEBean:viewCapitalCostsOverview:Start");
		saveRestoreStateValues(getCurrentPageRestoreStateValuesMap());
		Map<String, Object> nextPageValues = new HashMap<String, Object>();
		nextPageValues.put(ViewConstants.VIEW_PARAM_QUOTE_MODEL_ID, qmdId);
		nextPageValues.put(ViewConstants.VIEW_PARAM_INCLUDE_TEMP_EQUIPMENT, includeUnCalculatedCost);
		saveNextPageInitStateValues(nextPageValues);
		logger.debug("QuoteOverviewOEBean:viewCapitalCostsOverview:End");
		forwardToURL(ViewConstants.CAPITAL_COSTS);
	}

	public void viewServiceElements() {
		logger.debug("QuoteOverviewOEBean:viewServiceElements:Start");
		// add screen name to put custom message on screen on return
		Map<String, Object> restoreStateValues = getCurrentPageRestoreStateValuesMap();
		saveRestoreStateValues(restoreStateValues);
		Map<String, Object> nextPageValues = new HashMap<String, Object>();
		nextPageValues.put(ViewConstants.VIEW_PARAM_QUOTE_MODEL_ID, qmdId);
		saveNextPageInitStateValues(nextPageValues);
		logger.debug("QuoteOverviewOEBean:viewServiceElements:End");
		forwardToURL(ViewConstants.SERVICE_ELEMENTS);
	}

	public void editCapitalCostsOverview() {
		logger.debug("QuoteOverviewOEBean:editCapitalCostsOverview:Start");
		saveRestoreStateValues(getCurrentPageRestoreStateValuesMap());
		Map<String, Object> nextPageValues = new HashMap<String, Object>();
		nextPageValues.put(ViewConstants.VIEW_PARAM_QUOTE_MODEL_ID, qmdId);
		nextPageValues.put(ViewConstants.VIEW_PARAM_INCLUDE_TEMP_EQUIPMENT, includeUnCalculatedCost);
		saveNextPageInitStateValues(nextPageValues);
		logger.debug("QuoteOverviewOEBean:editCapitalCostsOverview:End");
		forwardToURL("/view/rentalCalc/capitalCostEntry");
	}

	

	private boolean updateInterestTotalRate() {
		quoteOverviewOEVO.setInterestAdjustment(quoteOverviewOEVO.getInterestAdjustment() != null ? quoteOverviewOEVO
				.getInterestAdjustment() : BigDecimal.ZERO);
		BigDecimal temp = quoteOverviewOEVO.getInterestAdjustment().add(quoteOverviewOEVO.getInterestBaseRate(),
				CommonCalculations.MC);
		quoteOverviewOEVO.setInterestTotalRate(temp);
		return validateTotalInterestRate();
	}

	private boolean validateTotalInterestRate() {
		// negative total interest rate is not allowed.RC-771
		if (quoteOverviewOEVO.getInterestTotalRate() != null
				&& quoteOverviewOEVO.getInterestTotalRate().compareTo(BigDecimal.ZERO) < 0) {
			addErrorMessage("custom.message", talMessage.getMessage("negative.notallowed", "Interest Rate"));
			return false;
		}
		return true;
	}

	private Map<String, Object> getCurrentPageRestoreStateValuesMap() {
		Map<String, Object> restoreStateValues = new HashMap<String, Object>();
		restoreStateValues.put(ViewConstants.VIEW_PARAM_QUOTE_MODEL_ID, qmdId);
		restoreStateValues.put(ViewConstants.VIEW_PARAM_FORWARDED_PAGE, forwaredPage);
		//restoreStateValues.put(ViewConstants.VIEW_PARAM_DEPRECIATION_FACTOR, quoteOverviewOEVO.getDepreciationFactor());
		return restoreStateValues;
	}

	private BigDecimal getBaseRate(QuotationModel quotationModel) throws MalException {
		BigDecimal baseIRR = new BigDecimal("0.00");
		try {
			logger.debug("QuoteOverviewBean:getIRR:Qmd Id=" + quotationModel.getQmdId());
			baseIRR = quotationModelDAO.getBaseRate(quotationModel.getQmdId());
			logger.debug("QuoteOverviewBean:baseIRR:baseIRR=" + baseIRR);
		} catch (Exception ex) {
			logger.error(ex);
			throw new MalException("generic.error.occured.while", new String[] { "getting Base IRR" });
		}
		return baseIRR;
	}

/*	private BigDecimal getModifiedNBV(BigDecimal nbv) {
		BigDecimal thresholdAmount = BigDecimal.ZERO;
		if (nbv.abs().compareTo(thresholdAmount) < 0) {
			return BigDecimal.ZERO;
		} else {
			return nbv;
		}
	}*/

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
			if (profitAdj == null) {
				profitAdj = -1D;
			}
		} catch (Exception ex) {
			logger.error(ex);
			throw new MalException("generic.error.occured.while", new String[] { "getting Profit Adjustment" });
		}
		return BigDecimal.valueOf(profitAdj);
	}

	private BigDecimal getLeaseFactor(QuotationStepStructureVO step) {
		return profitabilityService.getLeaseFactor(step.getLeaseRate(), quoteOverviewOEVO.getDepreciationFactor(),
				quoteOverviewOEVO.getAdminFactor(), quoteOverviewOEVO.getCustomerCost());
	}

	Comparator<QuotationElementStep> quotationElementStepComparator = new Comparator<QuotationElementStep>() {
		public int compare(QuotationElementStep r1, QuotationElementStep r2) {
			if (r1.getFromPeriod() == null && r2.getFromPeriod() == null) {
				return 0;
			} else if (r1.getFromPeriod() == null && r2.getFromPeriod() != null) {
				return -1;
			} else if (r1.getFromPeriod() != null && r2.getFromPeriod() == null) {
				return 1;
			} else {
				return (r1.getFromPeriod().compareTo(r2.getFromPeriod()));
			}
		}
	};
    // added for Bug 16476
	public QuotationModel getQuotationModel() {
		return quotationModel;
	}
	
	public QuoteOverviewOEVO getQuoteOverviewOEVO() {
		return quoteOverviewOEVO;
	}

	public boolean isDisableCalculateAndSave() {
		return disableCalculateAndSave;
	}

	public void setDisableCalculateAndSave(boolean disableCalculateAndSave) {
		this.disableCalculateAndSave = disableCalculateAndSave;
	}

	public List<QuotationStepStructureVO> getQuoteSteps() {
		return quoteSteps;
	}

	public void setQuoteSteps(List<QuotationStepStructureVO> quoteSteps) {
		this.quoteSteps = quoteSteps;
	}

	public List<QuotationStepStructureVO> getDefinitionSteps() {
		return definitionSteps;
	}

	public void setDefinitionSteps(List<QuotationStepStructureVO> definitionSteps) {
		this.definitionSteps = definitionSteps;
	}

	public BigDecimal getGrdDeliveryRechargeAmount() {
		return grdDeliveryRechargeAmount;
	}

	public void setGrdDeliveryRechargeAmount(BigDecimal grdDeliveryRechargeAmount) {
		this.grdDeliveryRechargeAmount = grdDeliveryRechargeAmount;
	}

	public String getGrdDeliveryRechargeAmountLabel() {
		return grdDeliveryRechargeAmountLabel;
	}

	public void setGrdDeliveryRechargeAmountLabel(String grdDeliveryRechargeAmountLabel) {

		this.grdDeliveryRechargeAmountLabel = grdDeliveryRechargeAmountLabel;
	}

	public boolean isNewQuote() {
		return isNewQuote;
	}

	public void setNewQuote(boolean isNewQuote) {
		this.isNewQuote = isNewQuote;
	}

	public boolean isNewQuoteCalculated() {
		boolean result = true;
		if (isNewQuote && isQuoteCalculated == false)
			result = false;

		return result;
	}

	public boolean isQuoteCalculated() {
		return isQuoteCalculated;
	}

	public void setQuoteCalculated(boolean isQuoteCalculated) {
		this.isQuoteCalculated = isQuoteCalculated;
	}

	private QuotationModelFinances getQuotationModelFinancesForInvoiceAdjustment() {
		return quotationModelFinancesDAO.findByQmdIdAndParameterKey(quotationModel.getQmdId(),
				MalConstants.FIN_PARAM_OE_INV_ADJ);
	}

	private QuotationProfitability getUpdatedProfitabiltyObject(boolean saveApprovedMin) {

		if (quotationProfitability == null) {
			quotationProfitability = new QuotationProfitability();
			quotationProfitability.setQuotationModel(quotationModel);
			quotationProfitability.setProfitBase(quoteOverviewOEVO.getBaseIrr());
			quotationProfitability.setProfitAdjustment(quoteOverviewOEVO.getProfitAdj());
			quotationProfitability.setProfitAmount(quoteOverviewOEVO.getActualIrrValue());
			quotationProfitability.setProfitSource("N");
			quotationProfitability.setProfitType("P");
		} else {
			if(saveApprovedMin == false){
				quotationProfitability.setProfitBase(quoteOverviewOEVO.getBaseIrr());
				quotationProfitability.setProfitAdjustment(quoteOverviewOEVO.getProfitAdj());
				quotationProfitability.setProfitAmount(quoteOverviewOEVO.getActualIrrValue());
			} else{

				if (quoteOverviewOEVO.getIrrApprovedLimitValue() != null) {
					if (quotationProfitability.getIrrApprovedLimit() == null
							|| (quotationProfitability.getIrrApprovedLimit() != null && quoteOverviewOEVO
									.getIrrApprovedLimitValue().compareTo(quotationProfitability.getIrrApprovedLimit()) != 0)) {
						quotationProfitability.setIrrApprovedLimit(quoteOverviewOEVO.getIrrApprovedLimitValue());
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

	public String getSplMsgforStep() {
		return splMsgforStep;
	}

	public void setSplMsgforStep(String splMsgforStep) {
		this.splMsgforStep = splMsgforStep;
	}

	public boolean isHideInvoiceAdjustment() {
		return hideInvoiceAdjustment;
	}

	public void setHideInvoiceAdjustment(boolean hideInvoiceAdjustment) {
		this.hideInvoiceAdjustment = hideInvoiceAdjustment;
	}

	public boolean isInvalidAccessPath() {
		return invalidAccessPath;
	}

	public void setInvalidAccessPath(boolean invalidAccessPath) {
		this.invalidAccessPath = invalidAccessPath;
	}
	public boolean isRecalculatedOnUI() {
		return recalculatedOnUI;
	}

	public void setRecalculatedOnUI(boolean recalculatedOnUI) {
		this.recalculatedOnUI = recalculatedOnUI;
	}
	
	public boolean isForwaredPage() {
		return forwaredPage;
	}
	public void setForwaredPage(boolean forwaredPage) {
		this.forwaredPage = forwaredPage;
	}
	

}