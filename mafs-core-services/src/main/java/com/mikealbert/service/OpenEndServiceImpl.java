package com.mikealbert.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.dao.ExternalAccountDAO;
import com.mikealbert.data.dao.QuotationModelDAO;
import com.mikealbert.data.entity.ContractLine;
import com.mikealbert.data.entity.Product;
import com.mikealbert.data.entity.QuotationElement;
import com.mikealbert.data.entity.QuotationElementStep;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.QuotationStepStructure;
import com.mikealbert.data.entity.QuoteModelPropertyValue;
import com.mikealbert.data.util.DisplayFormatHelper;
import com.mikealbert.data.vo.QuotationStepStructureVO;
import com.mikealbert.data.vo.QuoteOEVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.comparator.QuotationElementStepComparator;
import com.mikealbert.service.vo.QuoteCost;
import com.mikealbert.service.vo.QuoteVO;
import com.mikealbert.util.MALUtilities;


@Service("OpenEndService")
@Transactional (readOnly = true)
public class OpenEndServiceImpl implements OpenEndService {
	
	@Resource ProfitabilityService profitabilityService;
	@Resource RentalCalculationService rentalCalculationService;
	@Resource QuotationService quotationService;
	@Resource FleetMasterService fleetMasterService;
	@Resource ContractService contractService;
	@Resource ExternalAccountDAO externalAccountDAO;
	@Resource QuotationModelDAO quotationModelDAO;
	@Resource CapitalCostService capitalCostService;
	
	
	private static final int DEPRECIATION_FACTOR_SCALE = 7;
	private static final String RPT_LTD_LOGO_NAME = "report_ltd_logo.png";
	private static final String RPT_LEASING_LOGO_NAME = "report_leasing_logo.png";
	private static final String ADDRESS_TYPE_POST = "POST";	
	private static final String RPT_NEW_LINE_CHAR = "<br/>";
	private static final String IN_RATE_TYPE = "IN_RATE";
	private static final String OE_CONTRACT_REVISION = "R";	
	
	public MalLogger logger = MalLoggerFactory.getLogger(this.getClass());

	public BigDecimal getCalculatedDepreciationFactor(QuoteOEVO quoteOEVO) {
		BigDecimal depreciationFactor = profitabilityService.getDepreciationFactor(quoteOEVO.getCustomerCost(),
				quoteOEVO.getFinalNBV(), new BigDecimal(quoteOEVO.getQuotationModel().getContractPeriod()));
		depreciationFactor = depreciationFactor.movePointRight(2);
		depreciationFactor = depreciationFactor.setScale(DEPRECIATION_FACTOR_SCALE, RoundingMode.HALF_UP);
		return depreciationFactor;
	}
	
	public BigDecimal getModifiedNBV(BigDecimal nbv) {
		BigDecimal thresholdAmount = BigDecimal.ZERO;
		if (nbv.abs().compareTo(thresholdAmount) < 0) {
			return BigDecimal.ZERO;
		} else {
			return nbv;
		}
	}	

	public List<QuotationStepStructureVO> loadSteps(QuoteOEVO quoteOEVO) {
		List<QuotationStepStructure> quotationStepStructureList = quoteOEVO.getQuotationModel().getQuotationStepStructure();

		return quotationService.getCalculateQuotationStepStructure(quotationStepStructureList, quoteOEVO.getQuotationModel(), quoteOEVO.getDepreciationFactor(), 
				quoteOEVO.getAdminFactor(), quoteOEVO.getCustomerCost());
	}

	
	public String getLogoName(Product product) {
		String logoName;

		if (product.getProductCode().equals("OE_LTD")) {
			logoName = RPT_LTD_LOGO_NAME;
		} else {
			logoName = RPT_LEASING_LOGO_NAME;
		}

		return logoName;
	}
	
	public String getClientAddress(QuoteOEVO quoteOEVO){
		String clientAddress = "";
		Object[] record = null;
		

		List<Object[]> addressList = externalAccountDAO.getAccountAddressByType(quoteOEVO.getQuotationModel().getQuotation().getExternalAccount().getExternalAccountPK().getcId(),
				quoteOEVO.getQuotationModel().getQuotation().getExternalAccount().getExternalAccountPK().getAccountType(),
				quoteOEVO.getQuotationModel().getQuotation().getExternalAccount().getExternalAccountPK().getAccountCode(), 
				ADDRESS_TYPE_POST);
		
		if (!MALUtilities.isEmpty(addressList)) {
			int j = 0;
			record = addressList.get(0);
			String clientName = ((String) record[j]);
			String clientFedralId = record[j += 1] != null ? (String) record[j] : "";
			String addressLine1 = record[j += 1] != null ? (String) record[j] : "";
			String addressLine2 = record[j += 1] != null ? (String) record[j] : "";
			String city = record[j += 1] != null ? (String) record[j] : "";
			String state = record[j += 1] != null ? (String) record[j] : "";
			String zip = record[j += 1] != null ? (String) record[j] : "";
			
			clientAddress = DisplayFormatHelper.formatAddressForTable(null, addressLine1, addressLine2,
					null, null, city, state, zip, RPT_NEW_LINE_CHAR);
		}
		
		return clientAddress;	
	}
	
	public  QuoteCost getQuoteCost(QuotationModel qm) throws MalBusinessException {
		QuoteCost quoteCost = new QuoteCost(); 
		
		try {
			Long finalizedQmdId = quotationService.getFinalizeQmd(qm.getQmdId());
			if( finalizedQmdId != null && finalizedQmdId <= qm.getQmdId() ) {
			quoteCost = capitalCostService.getTotalCostsForQuote(qm);				
			} else {
				quoteCost.setCustomerCost(BigDecimal.ZERO);
			}
			
			if(quoteCost.getCustomerCost().compareTo(BigDecimal.ZERO) > 0) {
				return quoteCost;
			} else {
				QuoteVO targetQuote = capitalCostService.resolveAndCalcCapitalCosts(qm);
	        	quoteCost.setCustomerCost(BigDecimal.ZERO);
	        	quoteCost.setDealCost(BigDecimal.ZERO);
	        
	        	if (targetQuote != null) {        
	        	    quoteCost.setDealCost(targetQuote.getTotalCostToPlaceInServiceDeal());
	        	    quoteCost.setCustomerCost(targetQuote.getTotalCostToPlaceInServiceCustomer());
	        	}
	        	
	        return quoteCost;
			}
		} catch (Exception ex) {
			throw new MalBusinessException("generic.error.occured.while", new String[] { "getting total cost for quote" }, ex);
		}
	}
	
	public BigDecimal getRentalPeriods(QuotationElement qe, QuoteOEVO quoteOEVO) {
		BigDecimal rentalPeriods;
		
		if(qe.getNoRentals() != null && !qe.getNoRentals().equals(BigDecimal.ZERO)) {
			rentalPeriods = qe.getNoRentals();
		} else {
			rentalPeriods = new BigDecimal(quoteOEVO.getQuotationModel().getContractPeriod()); 
		}
		
		return rentalPeriods;
	}	
	
	public long getContractChangeEventPeriod(QuoteOEVO revisionOEQuoteVO, ContractLine contractLine) {
		Calendar stCal = Calendar.getInstance();
		stCal.setTime(contractLine.getStartDate());
		Calendar endCal = Calendar.getInstance();
		
		Date amendmentEffectiveDate = revisionOEQuoteVO.getQuotationModel().getAmendmentEffectiveDate();
		if(amendmentEffectiveDate == null) {
			amendmentEffectiveDate = new Date();
		}		

		endCal.setTime(amendmentEffectiveDate);
		return (long)Math.ceil(quotationService.monthsBetween(endCal, stCal)) + 1;

	}
	
	public BigDecimal calculateEffDateNBV(QuoteOEVO currentOEQuoteVO, QuoteOEVO revisionOEQuoteVO, ContractLine contractLine){
		BigDecimal effNBV = BigDecimal.ZERO;
		long conChangeEventPeriod = getContractChangeEventPeriod(revisionOEQuoteVO, contractLine);
		QuotationElement mainQuotationElement = rentalCalculationService.getMainQuotationModelElement(currentOEQuoteVO.getQuotationModel());
		List<QuotationElementStep> qes = mainQuotationElement.getQuotationElementSteps();
		Collections.sort(qes, new QuotationElementStepComparator());
		long currentContractQuoteElementStepStartPeriod = qes.get(0).getFromPeriod().longValue();
		
		effNBV = profitabilityService.getFinalNBV(currentOEQuoteVO.getCustomerCost(), currentOEQuoteVO.getDepreciationFactor(), new BigDecimal(conChangeEventPeriod - currentContractQuoteElementStepStartPeriod) );
		
		return effNBV;
	}
	
	public List<QuotationStepStructureVO>  getCurrentContractStepsforRevisionSteps(QuoteOEVO currentOEQuoteVO, QuoteOEVO revisionOEQuoteVO, ContractLine contractLine) throws Exception{
		List<QuotationStepStructureVO> steps = new ArrayList<QuotationStepStructureVO>();
		boolean revised = false;
		//Check whether revisionOEQuoteVO is derived from Amended Quote or Revised Quote
		for (QuoteModelPropertyValue quoteModelPropertyValue : revisionOEQuoteVO.getQuotationModel().getQuoteModelPropertyValues()) {
			if(quoteModelPropertyValue.getPropertyValue().equals(OE_CONTRACT_REVISION)){		
				revised = true;
			}
		}
		long conChangeEventPeriod;
		if ((!revised) && (!MALUtilities.isEmpty(currentOEQuoteVO.getQuotationModel().getContractChangeEventPeriod()))) {//Amended
			conChangeEventPeriod = currentOEQuoteVO.getQuotationModel().getContractChangeEventPeriod(); 
		}else {
			conChangeEventPeriod = getContractChangeEventPeriod(revisionOEQuoteVO, contractLine);	
		}
		
		List<QuotationStepStructureVO> currentSteps = quotationService.getAllQuoteSteps(currentOEQuoteVO.getQuotationModel().getQmdId());
		
		for(QuotationStepStructureVO step : currentSteps){
			if(step.getToPeriod() < conChangeEventPeriod){
				steps.add(step);
			}
			else if(step.getFromPeriod() < conChangeEventPeriod && step.getToPeriod() >= conChangeEventPeriod){
				QuotationStepStructureVO modifiedStep  = new QuotationStepStructureVO();
				modifiedStep.setFromPeriod(step.getFromPeriod());
				modifiedStep.setLeaseFactor(step.getLeaseFactor());
				modifiedStep.setLeaseRate(step.getLeaseRate());
				modifiedStep.setNetBookValue(revisionOEQuoteVO.getEffDateNBV());
				modifiedStep.setToPeriod(conChangeEventPeriod - 1);
				steps.add(modifiedStep);
			}
		}
		
		return  steps;
	}
	
	public BigDecimal  getRevCustomerCostTotal(QuoteOEVO revisionOEQuoteVO){
		BigDecimal revCustomerCostTotal = revisionOEQuoteVO.getEffDateNBV().subtract(revisionOEQuoteVO.getCapitalContribution()).add(revisionOEQuoteVO.getInvoiceAdjustment());		
		
		if(revisionOEQuoteVO.getRevisedAssessmentType() != null && revisionOEQuoteVO.getRevisedAssessmentType().equalsIgnoreCase(IN_RATE_TYPE)){
			revCustomerCostTotal =  revCustomerCostTotal.add(revisionOEQuoteVO.getRevisedAssessment());
		}	
		if(revisionOEQuoteVO.getRevisedIntAdjType() != null && revisionOEQuoteVO.getRevisedIntAdjType().equalsIgnoreCase(IN_RATE_TYPE)){
			revCustomerCostTotal = revCustomerCostTotal.add(revisionOEQuoteVO.getRevisionInterestAdjustment());
		}
		
		return revCustomerCostTotal;
	}

	public String compareProfilesForMigration(long oldQprId, long newQprId) throws MalBusinessException {
		String returnMsg = new String();
		try {
			returnMsg = quotationModelDAO.compareProfiles(oldQprId, newQprId);
			
		} catch (MalBusinessException mbe) {
			throw new MalBusinessException(mbe.getMessage());
		}
		return returnMsg;
	}	
}
