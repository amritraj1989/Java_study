package com.mikealbert.rental.processors;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.mikealbert.common.MalConstants;
import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.dao.MaintenanceTableDAO;
import com.mikealbert.data.dao.QuotationElementDAO;
import com.mikealbert.data.dao.QuotationModelDAO;
import com.mikealbert.data.dao.QuotationScheduleDAO;
import com.mikealbert.data.dao.QuoteProfileAdjDAO;
import com.mikealbert.data.entity.MaintenanceTable;
import com.mikealbert.data.entity.QuotationElement;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.QuoteProfileAdj;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.rental.calculations.QuoteCapitalCosts;
import com.mikealbert.rental.processors.inputoutput.MaintenanceProcessorInput;
import com.mikealbert.rental.processors.inputoutput.ProcessorInputType;
import com.mikealbert.rental.processors.inputoutput.ProcessorOutput;
import com.mikealbert.rental.processors.inputoutput.ProcessorOutputType;
import com.mikealbert.service.QuotationService;

@Service("maintenanceProcessor")
public class MaintenanceProcessor implements LeaseElementProcessor {
	private MalLogger logger = MalLoggerFactory.getLogger(this.getClass());
	@Resource
	private QuotationService quotationService;
	@Resource
	private QuotationModelDAO quotationModelDAO;

	@Resource
	private QuotationElementDAO quotationElementDAO;
	@Resource
	private QuotationScheduleDAO quotationScheduleDAO;
	@Resource
	private MaintenanceTableDAO maintenanceTableDAO;
	@Resource
	private QuoteProfileAdjDAO quoteProfileAdjDAO;
	
	protected static final MathContext MC = new MathContext(16, RoundingMode.HALF_EVEN); 
	/**
	 * MaintenanceProcessor's concept is taken from the database procedure i.e. WILLOW_RENTAL_CALCS.setup_maint from willow2k schema.<br>
	 * <br>
	 * This method sets up the maintenance element costs.<br>
	 * 
	 * Following columns of table quotation_elements being updated here for a particular Quotation Element Id (i.e. QEL_ID).<br>
	 * <OL>
	 *  <li> rental </li>
	 *  <li> overhead_amt </li>
	 *  <li> element_cost </li>
	 *  <li> profit_amt </li>
	 *  <li> no_rentals </li>
	 * </OL> 
	 *     
	 * @param  processorInputType List of the input parameters are listed at {@link com.mikealbert.rental.processors.inputoutput.MaintenanceProcessorInput}
	 * @return processorOutput Content of the processorOutput are listed at {@link com.mikealbert.rental.processors.inputoutput.ProcessorOutput}
	 */		
	@Override
	public ProcessorOutputType process(ProcessorInputType processorInputType) throws MalBusinessException {
		if (!(processorInputType instanceof MaintenanceProcessorInput))
		    throw new MalBusinessException("not_a_valid_argument_for",new String[]{"MaintenanceProcessor"} );

		MaintenanceProcessorInput maintenanceProcessorInput = (MaintenanceProcessorInput) processorInputType;
		QuotationElement quotationElement = maintenanceProcessorInput.getQuotationElement();
		BigDecimal overHead = maintenanceProcessorInput.getOverhead();
		BigDecimal overHeadProfit = maintenanceProcessorInput.getOverheadProfit();
		BigDecimal contractPeriod = new BigDecimal(maintenanceProcessorInput.getPeriod());
		Long distance = maintenanceProcessorInput.getDistance();
		Double inflation;
		BigDecimal maintValue = new BigDecimal(0);
		Long mdlId;
		BigDecimal pPmt;
		BigDecimal maintMargin = new BigDecimal(0);
		Long mtbId;
		BigDecimal maintAdjValue = new BigDecimal(0);
		BigDecimal maintAdjPercent = new BigDecimal(0);
		BigDecimal maintAdjVal = new BigDecimal(0);
		BigDecimal maintAdjPerc = new BigDecimal(0);
		BigDecimal maintAdjustment = new BigDecimal(0);
		BigDecimal maintMargVal = new BigDecimal(0);
		BigDecimal maintMargPerc = new BigDecimal(0);
		Long qmdId;
		int quoteStatus;
		Long qprId;
		String tableCode;
		String usedInd;
		BigDecimal currContractPeriod;
		BigDecimal billedToDate = new BigDecimal(0);
		BigDecimal tempBilled = new BigDecimal(0);
		BigDecimal amount = new BigDecimal(0);
		boolean gaps = false;
		boolean noHistory = true;
		String acceptedInd;
		/*Long makId;
		Long margId;*/
		ProcessorOutput processorOutput = new ProcessorOutput();
		processorOutput.setQuotationElement(quotationElement);
		try {
			acceptedInd = quotationElement.getAcceptedInd() != null ? quotationElement.getAcceptedInd() : "X";
			if (MalConstants.FLAG_Y.equals(acceptedInd)) {
				return processorOutput;
			}
			QuotationModel quotationModelMain=   quotationElement.getQuotationModel();
			qmdId = quotationModelMain.getQmdId();
			mdlId = quotationModelMain.getModel().getModelId();
			maintValue = quotationModelMain.getQuoteMaintCost() != null ?  quotationModelMain.getQuoteMaintCost(): new BigDecimal(0);
			usedInd = quotationModelMain.getUsedVehicle();
			quoteStatus = quotationModelMain.getQuoteStatus();
			qprId = quotationModelMain.getQuotation().getQuotationProfile().getQprId();
			if (quoteStatus == 9 && maintValue.compareTo(new BigDecimal(0)) != 0) {
				List<QuotationModel> prevQuoteModels = quotationModelDAO.findPrevQuotationsByUnitNo(quotationElement
						.getQuotationModel().getUnitNo(), quotationModelMain.getQmdId());
				if (prevQuoteModels == null || prevQuoteModels.size() == 0) {
					throw new MalBusinessException("service.validation",
							new String[] { "Unable to determine the previous quotation models for this vehicle for QEL ID:"
									+ quotationElement.getQelId() });
				}
				for (QuotationModel quotationModel : prevQuoteModels) {
					if (!containsElement(quotationModel.getQmdId(), quotationElement.getLeaseElement().getLelId())) {
						gaps = true;
						break;
					}
				}
				if (!gaps) {
					currContractPeriod = new BigDecimal(prevQuoteModels.get(0).getContractPeriod() != null ? prevQuoteModels
							.get(0).getContractPeriod().intValue() : 0);
				} else {
					for (QuotationModel quotationModel : prevQuoteModels) {
						if (containsElement(quotationModel.getQmdId(), quotationElement.getLeaseElement().getLelId())) {
							noHistory = false;
						}
					}
					if (noHistory) {
						currContractPeriod = contractPeriod;
					} else {
						// First calc the amount billed to date
						for (QuotationModel quotationModel : prevQuoteModels) {
							if (containsElement(quotationModel.getQmdId(), quotationElement.getLeaseElement().getLelId())) {// TODO
																															// seems
																															// some
																															// fix
																															// intract
																															// with
																															// DB
								tempBilled = quotationScheduleDAO.sumOfAmountByQuoteElementAndQmdIdAndLease(quotationElement
										.getQelId(), quotationModel.getQmdId(), quotationElement.getLeaseElement()
										.getLelId());
								billedToDate = billedToDate.add(tempBilled != null ? tempBilled : new BigDecimal(0));
							}
						}
						amount = amount.subtract(billedToDate);
						currContractPeriod = contractPeriod;
					}
				}
			}// quote status = 9 check end
			else if (quoteStatus == 10 && maintValue.compareTo(new BigDecimal(0)) != 0) {
				currContractPeriod = contractPeriod;
			} else {
				currContractPeriod = contractPeriod;
				if (MalConstants.FLAG_N.equals(usedInd)) {
					// Retrieve Maintenance Value from Table only for a New
					// Quote.
					// Maintenance on Used Quotes is entered in the form
					// directly
					tableCode = quotationModelMain.getQuotation().getQuotationProfile().getTableCode();
					List<MaintenanceTable> maintenanceTableList = maintenanceTableDAO
							.findByMdlIdTableCodeStatusAndEffectiveFrom(new BigDecimal(mdlId), tableCode, "L", new Date());
					// List to get data based on latest eff from
					MaintenanceTable tempMaintenanceTable = null;
					for (MaintenanceTable maintenanceTable : maintenanceTableList) {
						if (tempMaintenanceTable == null) {
							tempMaintenanceTable = maintenanceTable;
						}
						if (maintenanceTable.getEffectiveFrom().compareTo(tempMaintenanceTable.getEffectiveFrom()) > 0) {
							tempMaintenanceTable = maintenanceTable;
						}
					}
					
					mtbId = tempMaintenanceTable != null ? tempMaintenanceTable.getMtbId() : null;
					// Updated maintValue from quotation.fetch_maint_value
					if (mtbId != null) {
						maintValue = quotationModelDAO.fetchMaintValue(mtbId, contractPeriod.longValue(), distance);
					} else {
						maintValue = new BigDecimal(0);
					}

					/*makId = quotationModelMain.getModel().getMakId();
					margId = quotationModelMain.getModel().getMrgId();*/
				}
			}
			// Get the maintenance budget adjustment and maintenance margin
			// discount figures from Profile
			maintAdjVal = quotationModelMain.getQuotation().getQuotationProfile().getBudgAdjMaintVal();
			maintAdjVal = maintAdjVal != null ? maintAdjVal : new BigDecimal(0);

			maintAdjPerc = quotationModelMain.getQuotation().getQuotationProfile().getBudgAdjMaintPerc();
			maintAdjPerc = maintAdjPerc != null ? maintAdjPerc : new BigDecimal(0);

			maintMargVal = quotationModelMain.getQuotation().getQuotationProfile().getBudgDiscMaintVal();
			maintMargVal = maintMargVal != null ? maintMargVal : new BigDecimal(0);

			maintMargPerc = quotationModelMain.getQuotation().getQuotationProfile().getBudgDiscMaintPerc();
			maintMargPerc = maintMargPerc != null ? maintMargPerc : new BigDecimal(0);

			// Get Maintenance Value Adjustment from Adjustments Tab on Profile
			QuoteProfileAdj quoteProfileAdj = quoteProfileAdjDAO.findByQprIdAdjTypeAndGridType(qprId, MalConstants.ADJ_TYPE_STANDARD, MalConstants.GRID_TYPE_M);
			if (quoteProfileAdj != null) {
				maintAdjValue = quoteProfileAdj.getAdjValue();
				maintAdjPercent = quoteProfileAdj.getAdjPercent();

			}
			maintAdjValue = maintAdjValue != null ? maintAdjValue : new BigDecimal(0);
			maintAdjPercent = maintAdjPercent != null ? maintAdjPercent : new BigDecimal(0);

			inflation = quotationService.getFinanceParam(MalConstants.MAINT_INFLATION, qmdId, qprId);

			Double temp1 = Math.pow((1 + ((inflation / 100) / 12)), currContractPeriod.doubleValue());
			maintValue = maintValue.multiply(new BigDecimal(temp1));

			BigDecimal temp2 = maintAdjPerc.add(maintAdjPercent).divide(new BigDecimal(100));
			temp2 = new BigDecimal(1).add(temp2);
			
			BigDecimal temp3 = maintAdjVal.add(maintAdjValue).multiply(currContractPeriod);
			BigDecimal temp4 = maintValue.multiply(temp2);

			maintValue = temp4.add(temp3);

			// Get the Global maintenance profit margin rate
			pPmt = new BigDecimal(quotationService.getFinanceParam(MalConstants.FIN_PARAM_PPMT, qmdId, qprId));

			// Get the maintenance adjustment finance parameter
			maintAdjustment = new BigDecimal(quotationService.getFinanceParam(MalConstants.FIN_PARAM_MAINT_ADJ, qmdId, qprId));
			maintAdjustment = maintAdjustment != null?maintAdjustment:new BigDecimal(0);
			// Calculate the Maintenance margin
			
			BigDecimal temp5 = pPmt.divide(new BigDecimal(100),MC);
			maintMargin = maintValue.multiply(temp5);
			// apply the maintenance margin discounts to the maintenance margin
			BigDecimal temp6 = maintMargPerc.divide(new BigDecimal(100),MC);
			temp6 = new BigDecimal(1).add(temp6);
			
			BigDecimal temp7 = maintMargVal.multiply(currContractPeriod);
			
			BigDecimal temp8 = maintMargin.multiply(temp6);
			maintMargin = temp8.add(temp7).add(maintAdjustment);

			if (maintMargin.compareTo(new BigDecimal(0)) < 0) {
				maintMargin = new BigDecimal(0);
			}
			if (maintValue.compareTo(new BigDecimal(0)) < 0) {
				maintValue = new BigDecimal(0);
			}
			BigDecimal rentaltemp1 = overHead.add(overHeadProfit);
			rentaltemp1 = rentaltemp1.multiply(currContractPeriod);
			quotationElement.setRental(rentaltemp1.add(maintValue).add(maintMargin));
			quotationElement.setOverheadAmt(overHead.multiply(currContractPeriod));
			quotationElement.setProfitAmt(overHeadProfit.multiply(currContractPeriod).add(maintMargin));
			quotationElement.setElementCost(maintValue);
			quotationElement.setNoRentals(currContractPeriod);
			processorOutput.setQuotationElement(quotationElement);
		} catch (Exception ex) {
			if(ex instanceof MalBusinessException){
				throw (MalBusinessException)ex;
			}
			logger.error(ex);
			throw new MalBusinessException("service.validation",
					new String[] { "Error occured in setup maintenance" });
		}
		return processorOutput;
	}

	protected boolean containsElement(Long qmdId, Long leaseElementId) {
		List<QuotationElement> list = quotationElementDAO.findByQmdIdAndLeaseEleId(qmdId, leaseElementId);
		if (list != null && list.size() > 1) {
			return true;
		}
		return false;
	}

	/** Note: this Processor type never calculates CapitalCost based elements **/
	@Override
	public QuoteCapitalCosts getCapitalCostsCalc() {
		throw new UnsupportedOperationException();
	}

}