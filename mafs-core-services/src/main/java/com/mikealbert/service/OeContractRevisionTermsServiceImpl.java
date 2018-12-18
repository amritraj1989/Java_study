package com.mikealbert.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.common.MalConstants;
import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.dao.ExternalAccountDAO;
import com.mikealbert.data.dao.FleetMasterDAO;
import com.mikealbert.data.dao.InterestDisclosureDAO;
import com.mikealbert.data.dao.ProductDAO;
import com.mikealbert.data.entity.ContractLine;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.InterestDisclosure;
import com.mikealbert.data.entity.Product;
import com.mikealbert.data.entity.QuotationElement;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.QuotationStepStructure;
import com.mikealbert.data.entity.QuoteModelPropertyValue;
import com.mikealbert.data.vo.OeConRevTermsVO;
import com.mikealbert.data.vo.QuotationStepStructureVO;
import com.mikealbert.data.vo.QuoteOEVO;
import com.mikealbert.data.vo.ServicesLeaseRateByPeriodVO;
import com.mikealbert.data.vo.VehicleInformationVO;
import com.mikealbert.exception.MalException;
import com.mikealbert.service.vo.QuoteCost;
import com.mikealbert.util.MALUtilities;

@Service("oeContractRevisionTermsService")
@Transactional
public class OeContractRevisionTermsServiceImpl implements OeContractRevisionTermsService {
	
	@Resource QuotationService quotationService;
	@Resource ContractService contractService;
	@Resource ProfitabilityService profitabilityService;
	@Resource RentalCalculationService rentalCalculationService;
	@Resource CapitalCostService capitalCostService;
	@Resource ProductDAO productDAO;
	@Resource FleetMasterDAO fleetMasterDAO;
	@Resource ExternalAccountDAO externalAccountDAO;
	@Resource InterestDisclosureDAO interestDisclosureDAO;
	@Resource OpenEndService openEndService;
	@Resource ServiceElementService serviceElementService;
	
	private static final String leaseType = "O";
	private static final String formalExt = "N";
	private static final int DEPRECIATION_FACTOR_SCALE = 7;
	private static final int DECIMAL_PRECISION_THREE = 3;
	private static final String OE_REV_ASSMT = "OE_REV_ASSMT";
	private static final String OE_REV_INT_ADJ = "OE_REV_INT_ADJ";
	private static final String IN_RATE_TYPE = "IN_RATE";
	private static final String ONE_TIME_CHARGE_TYPE = "ONE_TIME_CHARGE";
	private static final String OE_REV_ASSMT_INRATE_YN = "OE_REV_ASSMT_INRATE_YN";
	private static final String OE_REV_INT_ADJ_INRATE_YN = "OE_REV_INT_ADJ_INRATE_YN";		
	
	private MalLogger logger = MalLoggerFactory.getLogger(this.getClass());

	@Override
	@Transactional(readOnly = true)
	public List<OeConRevTermsVO> getOeConRevTermsReportVO(Long currentQmdId, Long revisionQmdId) throws MalException {
		List<OeConRevTermsVO> oeConRevTermsVOList = new ArrayList<OeConRevTermsVO>();
		oeConRevTermsVOList.add(populateOeConRevTerms(currentQmdId, revisionQmdId));		
		return oeConRevTermsVOList;
	}
	
	private OeConRevTermsVO populateOeConRevTerms(Long currentQmdId, Long revisionQmdId) throws MalException {
		OeConRevTermsVO oeConRevTermsVO = new OeConRevTermsVO(); 
		try {
			QuoteOEVO currentOEQuoteVO = loadCurrentQuote(currentQmdId);
			FleetMaster fleetMaster = fleetMasterDAO.findByUnitNo(currentOEQuoteVO.getUnitNo());
			ContractLine contractLine = contractService.getCurrentContractLine(fleetMaster, new Date());
			oeConRevTermsVO.setCurrentInterestComponent(getInterestDisclosureString(getInterestDisclosure(currentOEQuoteVO), currentOEQuoteVO));			
			QuoteOEVO revisionOEQuoteVO = loadExistingRevision(revisionQmdId, currentOEQuoteVO, contractLine);			
			oeConRevTermsVO.setRevisionInterestComponent(getInterestDisclosureString(getInterestDisclosure(revisionOEQuoteVO), revisionOEQuoteVO));	
			if (revisionOEQuoteVO.getRevisedAssessmentType().equals(IN_RATE_TYPE)){
				oeConRevTermsVO.setInRateRevisionAssessment(revisionOEQuoteVO.getRevisedAssessment());
				oeConRevTermsVO.setOneTimeRevisionAssessment(BigDecimal.ZERO);
			} else {
				oeConRevTermsVO.setInRateRevisionAssessment(BigDecimal.ZERO);
				oeConRevTermsVO.setOneTimeRevisionAssessment(revisionOEQuoteVO.getRevisedAssessment());
			}
			
			if (revisionOEQuoteVO.getRevisedIntAdjType().equals(IN_RATE_TYPE)){
				oeConRevTermsVO.setInRateRevisionInterestAdjustment(revisionOEQuoteVO.getRevisionInterestAdjustment());
				oeConRevTermsVO.setOneTimeRevisionInterestAdjustment(BigDecimal.ZERO);
			} else {
				oeConRevTermsVO.setInRateRevisionInterestAdjustment(BigDecimal.ZERO);
				oeConRevTermsVO.setOneTimeRevisionInterestAdjustment(revisionOEQuoteVO.getRevisionInterestAdjustment());
			}
			
			currentOEQuoteVO.setEffDateNBV(openEndService.calculateEffDateNBV(currentOEQuoteVO, revisionOEQuoteVO, contractLine));
			
			oeConRevTermsVO.setCurrentOEQuoteVO(currentOEQuoteVO);
			
			Product product	= productDAO.findById(currentOEQuoteVO.getQuotationModel().getQuotation().getQuotationProfile().getPrdProductCode()).orElse(null);

			oeConRevTermsVO.setLogo(openEndService.getLogoName(product));		
			oeConRevTermsVO.setOeContractRevisionTitle("Open-End Contract Revision Contract Amendment - Terms");			
							
			oeConRevTermsVO.setVehicleInformationVO(getVehicleInformation(fleetMaster, contractLine));
			oeConRevTermsVO.getCurrentOEQuoteVO().getQuotationModel().setContractLine(contractLine);
			oeConRevTermsVO.setAddress(openEndService.getClientAddress(currentOEQuoteVO));
	
			
			oeConRevTermsVO.setRevisionOEQuoteVO(revisionOEQuoteVO);

			oeConRevTermsVO.setMonthsCompleted(openEndService.getContractChangeEventPeriod(revisionOEQuoteVO, contractLine) - 1);			
			oeConRevTermsVO.setMonthsRemaining(revisionOEQuoteVO.getQuotationModel().getContractPeriod() - openEndService.getContractChangeEventPeriod(revisionOEQuoteVO, contractLine) + 1);
			oeConRevTermsVO.setRevisionStartDate(contractLine.getStartDate());
				
			Calendar stCal2 = Calendar.getInstance();
			stCal2.setTime(contractLine.getStartDate());
			Calendar endCal2 = Calendar.getInstance();
			endCal2.setTime(oeConRevTermsVO.getRevisionStartDate());
			endCal2.add(Calendar.MONTH,revisionOEQuoteVO.getQuotationModel().getContractPeriod().intValue() -1);
			endCal2.set(Calendar.DATE, endCal2.getActualMaximum(Calendar.DATE));
			oeConRevTermsVO.setRevisionEndDate(endCal2.getTime());		

			
			List<QuotationStepStructureVO> adjustedSteps = openEndService.getCurrentContractStepsforRevisionSteps(currentOEQuoteVO, revisionOEQuoteVO, contractLine);			
			oeConRevTermsVO.setCurrentQuotationStepStructureVOList(adjustedSteps);	

			QuotationStepStructureVO lastStep = null;
			if (!adjustedSteps.isEmpty()){
				lastStep = adjustedSteps.get(adjustedSteps.size() - 1);
			}			
			oeConRevTermsVO.setRevisionInterestAdjustmentMonths("Revision Interest Adjustment Months " + lastStep.getFromPeriod() + "-" + lastStep.getToPeriod() + ":");			
			
			
			List<QuotationStepStructureVO> revisionSteps = getRevisionStepStructure(revisionOEQuoteVO);
			oeConRevTermsVO.setCurrentServicesLeaseRateByPeriodVOList(getServicesLeaseRateByPeriod(currentOEQuoteVO,adjustedSteps, currentOEQuoteVO.getQuotationModel().getContractPeriod()));
			oeConRevTermsVO.setRevisionServicesLeaseRateByPeriodVOList(getServicesLeaseRateByPeriod(revisionOEQuoteVO,revisionSteps, currentOEQuoteVO.getQuotationModel().getContractPeriod()));
			
			oeConRevTermsVO.setRevisionQuotationStepStructureVOList(revisionSteps);		

			oeConRevTermsVO.setTotalOneTimeCharges(oeConRevTermsVO.getOneTimeRevisionAssessment().add(oeConRevTermsVO.getOneTimeRevisionInterestAdjustment()));

			calculateFinalMonthlyLeaseRate(oeConRevTermsVO);
			
			oeConRevTermsVO.setSignatureSection(
					"Reference is hereby made to the Schedule A (the \"Identified Document\") related to the Vehicle identified above, Schedule A <br/>" +
					"Reference #" + revisionOEQuoteVO.getQuote() + ".  Upon execution by a Permitted Signer of Lessee or its Authorized Affiliate, the Amendment shall <br/>" +
					"modify the provisions of the Identified Document as specified above, effective as of the Amended Effective Date set forth above <br/>" +
					"(or, if this Amendment is not executed prior to the Amended Effective Date, as of the first day of the month following the date of <br/>" +
					"execution).  For the avoidance of doubt, upon execution this Amendment modifies the Identified Document with respect to the <br/>" +
					"identified Vehicle only, and neither the Master Lease Agreement between Lessee and Lessor nor the Services Agreement or <br/>" +
					"Telematics Agreement between Client and Mike Albert Leasing, Inc. is otherwise amended or modified hereby.<br/>" +
					"<p>" +
					"Capitalized terms used but not defined in this Amendment have meanings set forth in the Identified Document.<br/>" +
					"<p>" +
					"In order to make the Amendment effective, please complete the following details and return by fax to:  (513) 569-9316, Attention:<br/>" +
					"Monica Smith.<br/>" +
					"<p>" +
					"The undersigned hereby acknowledges acceptance of the terms of this Amendment with respect to the Identified Document and <br/>" +
					"related vehicle.<br/> " +
					"<p>" +
					"<p>");
					
			oeConRevTermsVO.setSignatureLine(				
					"Signature of Permitted Signer of Lessee or its authorized Affiliate");
			
		} catch (Exception ex) {
			logger.error(ex);
			throw new MalException("generic.error.occured.while", new String[] { "getting Open End Contract Revision report data" }, ex);
		}			
		return oeConRevTermsVO;	
	}

	private void calculateFinalMonthlyLeaseRate(OeConRevTermsVO oeConRevTermsVO) {
    	//Current
		List<QuotationStepStructureVO> currentQssVOList = oeConRevTermsVO.getCurrentQuotationStepStructureVOList();
    	for(int i=0; i<currentQssVOList.size(); i++){
    		currentQssVOList.get(i).setFinalLeaseRate(currentQssVOList.get(i).getLeaseRate().add(oeConRevTermsVO.getCurrentOEQuoteVO().getServiceElementRate()));
    	} 
    	
    	oeConRevTermsVO.setCurrentQuotationStepStructureVOList(currentQssVOList);
    	
    	//Revision
    	List<QuotationStepStructureVO> revisionQssVOList = oeConRevTermsVO.getRevisionQuotationStepStructureVOList();
    	for(int i=0; i<revisionQssVOList.size(); i++){
    		revisionQssVOList.get(i).setFinalLeaseRate(revisionQssVOList.get(i).getLeaseRate().add(oeConRevTermsVO.getRevisionOEQuoteVO().getServiceElementRate()));

    	} 
    	
    	oeConRevTermsVO.setRevisionQuotationStepStructureVOList(revisionQssVOList);    	

	}

	private QuoteOEVO loadQuote(QuotationModel quotationModel) throws Exception {
		
		QuoteOEVO quoteOEVO = new QuoteOEVO();

		if (quotationModel.getQuoteStatus() == 10) {
			quotationModel = quotationService.changeProfileOnQuotationModels(quotationModel);
		}
		quoteOEVO.setQuotationModel(quotationModel);
		Product product	= productDAO.findById(quotationModel.getQuotation().getQuotationProfile().getPrdProductCode()).orElse(null);
		quoteOEVO.setProductName(product.getProductName());
		quoteOEVO.setAccountCode(quotationModel.getQuotation().getExternalAccount().getExternalAccountPK().getAccountCode());
		quoteOEVO.setAccountName(quotationModel.getQuotation().getExternalAccount().getAccountName());
		quoteOEVO.setQuote(Long.toString(quotationModel.getQuotation().getQuoId()) + "/"
				+ Long.toString(quotationModel.getQuoteNo()) + "/" + Long.toString(quotationModel.getRevisionNo()));
		quoteOEVO.setUnitNo(quotationModel.getUnitNo());
		quoteOEVO.setUnitDesc(quotationModel.getModel().getModelDescription());
		quoteOEVO.setTerm(quotationModel.getContractPeriod());
		quoteOEVO.setDistance(quotationModel.getContractDistance());
		quoteOEVO.setProjectedReplacementMonths(quotationModel.getProjectedMonths());
		quoteOEVO.setQuoteProfileDesc(quotationModel.getQuotation().getQuotationProfile().getDescription());
		quoteOEVO.setCostTreatment(quotationModel.getPreContractFixedCost());//.equals("F") ? COST_FIXED : COST_VARIABLE);
		quoteOEVO.setInterestTreatment(quotationModel.getPreContractFixedInterest());//.equals("F") ? COST_FIXED : COST_VARIABLE);
		quoteOEVO.setInterestIndex(quotationModel.getQuotation().getQuotationProfile().getItcInterestType());
		quoteOEVO.setFloatType(quotationModel.getQuotation().getQuotationProfile().getVariableRate());//.equals("V") ? "Float" : "Non-Float");
		
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

		Double adminFactorFinV = quotationService.getFinanceParam(MalConstants.FIN_PARAM_ADMIN_FACT,
				quotationModel.getQmdId(), quotationModel.getQuotation().getQuotationProfile().getQprId());

		quoteOEVO.setAdminFactor(BigDecimal.valueOf(adminFactorFinV));
		quoteOEVO.setCapitalContribution(quotationModel.getCapitalContribution() == null ? BigDecimal.ZERO
				: quotationModel.getCapitalContribution());
		
		quoteOEVO.setServiceElementRate(serviceElementService.getServiceElementMonthlyCost(quotationModel));

		QuoteCost quoteCost = openEndService.getQuoteCost(quotationModel);
		quoteOEVO.setDealCost(quoteCost.getDealCost());
		quoteOEVO.setCustomerCost(quoteCost.getCustomerCost());
		
		if(quotationModel.getDepreciationFactor() != null){ 
			quoteOEVO.setDepreciationFactor(quotationModel.getDepreciationFactor().setScale(DEPRECIATION_FACTOR_SCALE, RoundingMode.HALF_UP));
		} else {
			quoteOEVO.setDepreciationFactor(openEndService.getCalculatedDepreciationFactor(quoteOEVO));
		}		

		return quoteOEVO;
	}	
	
	private QuoteOEVO loadExistingRevision(long revisionQmdId, QuoteOEVO currentOEQuoteVO, ContractLine contractLine) throws Exception {
		QuoteOEVO revisionOEQuoteVO = loadQuote(quotationService.getQuotationModelWithCostAndAccessories(revisionQmdId));
	
		
		revisionOEQuoteVO.setEffDateNBV(openEndService.calculateEffDateNBV(currentOEQuoteVO, revisionOEQuoteVO, contractLine));

		for (QuoteModelPropertyValue quoteModelPropertyValue : revisionOEQuoteVO.getQuotationModel().getQuoteModelPropertyValues()) {			
			String propertyName = quoteModelPropertyValue.getQuoteModelProperty().getName();			
			if(propertyName.equals(OE_REV_ASSMT)){		
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
		
	
		revisionOEQuoteVO.setCustomerCost(openEndService.getRevCustomerCostTotal(revisionOEQuoteVO));
		
		if(revisionOEQuoteVO.getQuotationModel().getAmendmentEffectiveDate() == null) {
			revisionOEQuoteVO.getQuotationModel().setAmendmentEffectiveDate(new Date());
		}		
		
		revisionOEQuoteVO.setServiceElementRate(serviceElementService.getServiceElementMonthlyCost(revisionOEQuoteVO.getQuotationModel()));
	
		return revisionOEQuoteVO;
	}	
	
	private QuoteOEVO loadCurrentQuote(Long currentQmdId) throws Exception {
		QuoteOEVO currentOEQuoteVO = loadQuote(quotationService.getQuotationModelWithCostAndAccessories(currentQmdId));		
		List<QuotationStepStructureVO> currentSteps =  quotationService.getAllQuoteSteps(currentQmdId);
		
		for (QuotationStepStructureVO stepStructureVO : currentSteps) {//this may have multiple quote's steps
			if(stepStructureVO.getAssociatedQmdId().equals(currentQmdId)){
				currentOEQuoteVO.setFinalResidual(currentSteps.get(currentSteps.size()-1).getNetBookValue());
				currentOEQuoteVO.setFinalNBV(currentSteps.get(currentSteps.size()-1).getNetBookValue());
				currentOEQuoteVO.setActualLeaseRate(currentSteps.get(0).getLeaseRate());
				break;
			}
		}
		
		return currentOEQuoteVO;
	}
	
	private List<QuotationStepStructureVO> loadSteps(QuoteOEVO quoteOEVO) {
		List<QuotationStepStructure> quotationStepStructureList = quoteOEVO.getQuotationModel().getQuotationStepStructure();

		return quotationService.getCalculateQuotationStepStructure(quotationStepStructureList, quoteOEVO.getQuotationModel(), quoteOEVO.getDepreciationFactor(), 
				quoteOEVO.getAdminFactor(), quoteOEVO.getCustomerCost());
	}	
	
	private BigDecimal getRentalPeriods(QuotationElement qe, Long currentContractPeriod) {
		BigDecimal rentalPeriods;
		if(qe.getNoRentals() != null && !qe.getNoRentals().equals(BigDecimal.ZERO)) {
			rentalPeriods = qe.getNoRentals();
		} else {
			rentalPeriods = new BigDecimal(currentContractPeriod); 
		}
		
		return rentalPeriods;
	}	
	
	private List<QuotationStepStructureVO> getRevisionStepStructure(QuoteOEVO revisionOEQuoteVO) {		
		List<QuotationStepStructureVO> revisionSteps = new ArrayList<QuotationStepStructureVO>();
		revisionSteps = loadSteps(revisionOEQuoteVO);
		revisionOEQuoteVO.setActualLeaseRate(revisionSteps.get(0).getLeaseRate());

		return revisionSteps;
	}
	
	private List<ServicesLeaseRateByPeriodVO> getServicesLeaseRateByPeriod(QuoteOEVO quoteOEVO, List<QuotationStepStructureVO> steps, Long currentContractPeriod){
		List<ServicesLeaseRateByPeriodVO> servicesLeaseRateByPeriodVOList = new ArrayList<ServicesLeaseRateByPeriodVO>();
		for (QuotationStepStructureVO cs : steps){
			for (QuotationElement quotationElement : quoteOEVO.getQuotationModel().getQuotationElements()) {
				if (!quotationElement.getLeaseElement().getElementType().equals(MalConstants.FINANCE_ELEMENT)){
					ServicesLeaseRateByPeriodVO servicesLeaseRateByPeriodVO = new ServicesLeaseRateByPeriodVO();
					servicesLeaseRateByPeriodVO.setDescription(quotationElement.getLeaseElement().getDescription());
					
					if(quotationElement.getRental() != null) {
						BigDecimal cost = quotationElement.getRental().divide(getRentalPeriods(quotationElement, currentContractPeriod), 2, BigDecimal.ROUND_HALF_UP);
						servicesLeaseRateByPeriodVO.setMonthlyCost(cost);				
					}
					servicesLeaseRateByPeriodVO.setMonthlyCost(servicesLeaseRateByPeriodVO.getMonthlyCost() != null ? servicesLeaseRateByPeriodVO.getMonthlyCost() : BigDecimal.ZERO);			
					servicesLeaseRateByPeriodVO.setFromPeriod(cs.getFromPeriod());
					servicesLeaseRateByPeriodVO.setToPeriod(cs.getToPeriod());
					servicesLeaseRateByPeriodVOList.add(servicesLeaseRateByPeriodVO);
				}
			}
		}
		
		if(!MALUtilities.isEmpty(servicesLeaseRateByPeriodVOList)){
			Collections.sort(servicesLeaseRateByPeriodVOList, new Comparator<ServicesLeaseRateByPeriodVO>() {
				public int compare(ServicesLeaseRateByPeriodVO s1, ServicesLeaseRateByPeriodVO s2) {
					String desc1 = s1.getDescription();
					String desc2 = s2.getDescription();
					return desc1.toLowerCase().compareTo(desc2.toLowerCase());						
				}
			});			
		}		
		
		return servicesLeaseRateByPeriodVOList;
	}
	
	private InterestDisclosure getInterestDisclosure(QuoteOEVO quoteOEQuoteVO) {
		String variableRate = quoteOEQuoteVO.getQuotationModel().getQuotation().getQuotationProfile().getVariableRate();
		String disclosureInd = quoteOEQuoteVO.getQuotationModel().getQuotation().getQuotationProfile().getDisclosureInd();
		
		if(disclosureInd == null){
			disclosureInd = "N";
		}
		
		return interestDisclosureDAO.findInterestDisclosure(leaseType, quoteOEQuoteVO.getInterestIndex(), quoteOEQuoteVO.getInterestTreatment(), quoteOEQuoteVO.getCostTreatment(), formalExt, disclosureInd, variableRate);		
	}	

	private String getInterestDisclosureString(InterestDisclosure interestDisclosure, QuoteOEVO quoteOEVO) {
		String replacedString = null;
		replacedString = interestDisclosure.getCalculationText().replace("<<interest rate>>", quoteOEVO.getInterestIndex() + " Rate");
		replacedString = replacedString.replace("<<interest adj>>", quoteOEVO.getInterestAdjustment().toString());
		return replacedString;
	}	
	
	private VehicleInformationVO getVehicleInformation(FleetMaster fleetMaster, ContractLine contractLine){
		VehicleInformationVO vehicleInformationVO = new VehicleInformationVO();	

		vehicleInformationVO.setVin(fleetMaster.getVin());	
		vehicleInformationVO.setClientFleetReferenceNumber(fleetMaster.getFleetReferenceNumber());

		vehicleInformationVO.setDriverForeName(contractLine.getDriver().getDriverForename()); 
		vehicleInformationVO.setDriverSurname(contractLine.getDriver().getDriverSurname());

		return vehicleInformationVO;
	}

}
