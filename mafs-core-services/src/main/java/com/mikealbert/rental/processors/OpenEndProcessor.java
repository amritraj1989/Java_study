/**
 * OpenEndProcessor.java
 * rental_calcs
 * Jun 18, 2013
 * 2:48:18 PM
 */
package com.mikealbert.rental.processors;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.mikealbert.common.CommonCalculations;
import com.mikealbert.common.MalConstants;
import com.mikealbert.data.dao.PaymentHeaderDAO;
import com.mikealbert.data.dao.QuotationCapitalElementDAO;
import com.mikealbert.data.dao.QuotationElementStepDAO;
import com.mikealbert.data.dao.WillowConfigDAO;
import com.mikealbert.data.entity.PaymentHeader;
import com.mikealbert.data.entity.QuotationCapitalElement;
import com.mikealbert.data.entity.QuotationElement;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.WillowConfig;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.rental.calculations.QuoteCapitalCosts;
import com.mikealbert.rental.processors.inputoutput.OpenEndProcessorInput;
import com.mikealbert.rental.processors.inputoutput.ProcessorInputType;
import com.mikealbert.rental.processors.inputoutput.ProcessorOutput;
import com.mikealbert.rental.processors.inputoutput.ProcessorOutputType;
import com.mikealbert.service.QuotationService;

/**
 * @author anand.mohan
 * 
 */
@Component("openEndProcessor")
public class OpenEndProcessor extends BaseProcessor {
	@Resource
	private QuotationElementStepDAO quotationElementStepDAO;
	@Resource
	private PaymentHeaderDAO paymentHeaderDAO;
	@Resource
	private QuotationService quotationService;
	@Resource
	private WillowConfigDAO willowConfigDAO;
	
    @Resource  private QuotationCapitalElementDAO quotationCapitalElementDAO;
    
	@Resource(name="capitalClientInvoiceCosts") private QuoteCapitalCosts capitalCostsCalc;
	
	/**
	 * Calculates and populate data in quotation element which involves open end formula.
	 * Also creates quotation element steps based on quotation step structure. If it is stepped
	 * ,multiple quotation steps will be created else one.
	 * The  quotation step structure must be inserted  in database before its execution.
	 * @param	ProcessorInputType, populated with quotation element, model accessory id and dealer accessory id.
	 * @return	ProcessorOutputType,populated with updated quotation element.
	 */
	@Override
	public ProcessorOutputType process(ProcessorInputType processorInputType) throws MalBusinessException {
		ProcessorOutput processorOutput = new ProcessorOutput();
		final String QUOTE_TYPE_A = "A";
		if (!(processorInputType instanceof OpenEndProcessorInput))
			throw new MalBusinessException("not_a_valid_argument_for", new String[] { "OpenEndProcessor" });
		OpenEndProcessorInput openEndProcessorInput = (OpenEndProcessorInput) processorInputType;
		// variables
		Long piQelId = openEndProcessorInput.getQelId();
		Long piQmaId = openEndProcessorInput.getQmaId();
		Long piQdaId = openEndProcessorInput.getQdaId();
		BigDecimal vCapCost;
		BigDecimal vResValue;
		Long vQmdId;
		Long vNoPeriods = new Long(0);
		BigDecimal vProfitAmount = null;
		String vConfigValue;
		String vQuoteType = null;
		Long vOldContractPeriod = null;
		Long vQdaId;
		Long vOldQdaId;
		String vDealerAddOn = MalConstants.FLAG_N;
		// Stepped period loop variables.
		Long vStartPeriod;
		Long vEndPeriod;
		Long vEventPeriod;
		// Clear out any previous quotation_element_steps
		quotationElementStepDAO.deleteByQuoteElementId(piQelId);
		// get quotation element
		QuotationElement quotationElement = openEndProcessorInput.getQuotationElement();
		QuotationModel quotationModel = quotationElement.getQuotationModel();
		PaymentHeader paymentHeader = paymentHeaderDAO.findById(quotationModel.getPaymentId()).orElse(null);
		vCapCost = quotationModel.getQuoteCapital();
		vResValue = quotationModel.getResidualValue();
		vQmdId = quotationModel.getQmdId();
		BigDecimal cpTemp1 = new BigDecimal(quotationModel.getContractPeriod()).divide(new BigDecimal(paymentHeader.getPmtPaymentFrequencyCode()),MC);
		BigDecimal	cpFinal = cpTemp1.multiply(new BigDecimal(paymentHeader
				.getAnnualPeriods()), MC);
		vNoPeriods = cpFinal.longValue();
		// vInterestRate = quotationModel.getInterestRate();
		vQdaId = quotationElement.getQuotationDealerAccessory() != null ? quotationElement.getQuotationDealerAccessory().getQdaId():null;
		
		BigDecimal basicPrice = new BigDecimal(0);
		BigDecimal capitalContribution = new BigDecimal(0);
		
		if (vQdaId != null) {			
			if (QUOTE_TYPE_A.equals(vQuoteType)) {
			    	QuotationModel origionalQuotationModel = quotationService.getOriginalQuoteModel(vQmdId);
				// get dealer accessory
				vOldQdaId = quotationService.getDealerAcc(origionalQuotationModel.getQmdId(), vQmdId, vQdaId);
				if (vOldQdaId != null) {
					vDealerAddOn = MalConstants.FLAG_Y;
					// Now need to determine the start period of the amendment
					vOldContractPeriod = origionalQuotationModel.getContractPeriod();
					if (vOldContractPeriod == null) {
						throw new MalBusinessException("no_prev_contract_period");
					}
					vEventPeriod = quotationModel.getContractChangeEventPeriod();
					if (vEventPeriod == null) {
						throw new MalBusinessException("no_contract_change_period");
					}
				}
			}
		}
		if (piQmaId != null && piQmaId.longValue() != 0) {
			if (quotationElement.getQuotationModelAccessory() == null) {
				throw new MalBusinessException("no_qma");
			}
			
			BigDecimal qmsResidualAmt = quotationElement.getQuotationModelAccessory().getResidualAmt();
			
			vCapCost = this.capitalCostsCalc.calcModelAccessoryElement(quotationElement.getQuotationModelAccessory()).getCustomerCost();
		    
			vResValue = qmsResidualAmt != null ? qmsResidualAmt : BigDecimal.ZERO;

		} else if (piQdaId != null && piQdaId.longValue() != 0) {
			if (quotationElement.getQuotationDealerAccessory() == null) {
				throw new MalBusinessException("no_qda");
			}
			
			BigDecimal qmsResidualAmt = quotationElement.getQuotationDealerAccessory().getResidualAmt();

			vCapCost = this.capitalCostsCalc.calcDealerAccessoryElement(quotationElement.getQuotationDealerAccessory()).getCustomerCost();
		    
			vResValue = qmsResidualAmt != null ? qmsResidualAmt : BigDecimal.ZERO;
			
		} else {
			// It should definitely be the base vehicle now.
			// Need to differentiate the on-invoice capital elements from the
			// off-invoice capital elements
		
			basicPrice = quotationModel.getBasePrice();
		    capitalContribution = quotationModel.getCapitalContribution();
		    List<QuotationCapitalElement> qceList = quotationCapitalElementDAO.findByQmdID(quotationModel.getQmdId());
		    
		    vCapCost = this.capitalCostsCalc.calcQuotationCost(basicPrice, capitalContribution, qceList).getCustomerCost();			
		}
		// Need to determine the admin factor
		vConfigValue = getWillowConfigValue("OPEN_END_ADMIN_FACTOR");
		if (vConfigValue == null || vConfigValue.equals("NOVALUEFOUND")) {
			throw new MalBusinessException("set_willow_config", new String[] { "OPEN_END_ADMIN_FACTOR" });
		}
		vStartPeriod = new Long(1);
		if (MalConstants.FLAG_Y.equals(vDealerAddOn)) {
			vEndPeriod = vOldContractPeriod;
			vNoPeriods = vEndPeriod - vStartPeriod + 1;
		} else {
			vEndPeriod = vNoPeriods;
		}
		
		quotationElement.setOverheadAmt(BigDecimal.ZERO);
		quotationElement.setProfitAmt(vProfitAmount);
		//quotationElement.setElementCost(vRental.multiply(new BigDecimal(vNoPeriods), CommonCalculations.MC));
		quotationElement.setCapitalCost(vCapCost);
		quotationElement.setResidualValue(vResValue);
		quotationElement.setDepreciation(vCapCost.subtract(vResValue, CommonCalculations.MC));
		quotationElement.setNoRentals(new BigDecimal(vNoPeriods));
		//quotationElement.setInterest(vAvgInt);
		quotationElement.setFinalPayment(vResValue);
		quotationElement.setApr(BigDecimal.ZERO);
			

		
		processorOutput.setQuotationElement(quotationElement);
		return processorOutput;
	}

	/**
	 * Returns value of configuration from willow config
	 * 
	 * @param configName
	 * @return value of willow config
	 * @throws MalBusinessException
	 */
	public String getWillowConfigValue(String configName) throws MalBusinessException {
		String configValue = null;

		if (configName != null) {
			WillowConfig willowConfig = willowConfigDAO.findById(configName).orElse(null);
			configValue = willowConfig.getConfigValue();
		}

		return configValue;
	}

	@Override
	public QuoteCapitalCosts getCapitalCostsCalc() {
		return capitalCostsCalc;
	}
}
