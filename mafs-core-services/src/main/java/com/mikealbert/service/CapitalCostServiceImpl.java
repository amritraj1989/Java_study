package com.mikealbert.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.common.MalConstants;
import com.mikealbert.data.dao.CapitalElementDAO;
import com.mikealbert.data.dao.CapitalElementDAOCustom;
import com.mikealbert.data.dao.DealerAccessoryDAO;
import com.mikealbert.data.dao.DistDAO;
import com.mikealbert.data.dao.DocDAO;
import com.mikealbert.data.dao.DocLinkDAO;
import com.mikealbert.data.dao.DoclDAO;
import com.mikealbert.data.dao.ExternalAccountDAO;
import com.mikealbert.data.dao.FleetMasterDAO;
import com.mikealbert.data.dao.MaintenanceRequestTaskDAO;
import com.mikealbert.data.dao.MalCapitalCostDAO;
import com.mikealbert.data.dao.OptionalAccessoryDAO;
import com.mikealbert.data.dao.ProductDAO;
import com.mikealbert.data.dao.ProductElementDAO;
import com.mikealbert.data.dao.QuotationCapitalElementDAO;
import com.mikealbert.data.dao.QuotationModelDAO;
import com.mikealbert.data.entity.CapitalElement;
import com.mikealbert.data.entity.DealerAccessory;
import com.mikealbert.data.entity.Dist;
import com.mikealbert.data.entity.Doc;
import com.mikealbert.data.entity.DocLink;
import com.mikealbert.data.entity.Docl;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.ExternalAccountPK;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.LeaseElement;
import com.mikealbert.data.entity.MalCapitalCost;
import com.mikealbert.data.entity.OptionalAccessory;
import com.mikealbert.data.entity.Product;
import com.mikealbert.data.entity.ProductElement;
import com.mikealbert.data.entity.QuotationCapitalElement;
import com.mikealbert.data.entity.QuotationCapitalElementBackup;
import com.mikealbert.data.entity.QuotationDealerAccessory;
import com.mikealbert.data.entity.QuotationElement;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.QuotationModelAccessory;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.rental.calculations.QuoteCapitalCosts;
import com.mikealbert.rental.processors.LeaseElementProcessor;
import com.mikealbert.service.vo.QuoteCost;
import com.mikealbert.service.vo.QuoteCostElementVO;
import com.mikealbert.service.vo.QuoteVO;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.util.SpringAppContext;
import com.mikealbert.service.vo.CapitalCostModeValuesVO;
import com.mikealbert.service.enumeration.NonCapitalElementEnum;
import com.mikealbert.service.CapitalCostElementService;

@Service("capitalCostService")
@Transactional(readOnly = true)
public class CapitalCostServiceImpl implements CapitalCostService {

	@Resource QuotationModelDAO quotationModelDAO;
	@Resource CapitalElementDAO capitalElementDAO;
	@Resource OptionalAccessoryDAO optionalAccessoryDAO;
	@Resource DealerAccessoryDAO dealerAccessoryDAO;
	@Resource DocDAO docDAO;
	@Resource DistDAO distDAO;
	@Resource DoclDAO doclDAO;
	@Resource CapitalCostElementService capitalCostElementService;
	@Resource QuotationService quotationService;
	@Resource FleetMasterDAO fleetMasterDAO;
	@Resource QuotationCapitalElementDAO quotationCapitalElementDAO;	
	@Resource CapitalCostService capitalCostService;
	@Resource FinanceParameterService	financeParameterService;
	@Resource MaintenanceRequestTaskDAO maintenanceRequestTaskDAO;
	@Resource ExternalAccountDAO externalAccountDAO;
	@Resource DocLinkDAO docLinkDao;
	@Resource MalCapitalCostDAO malCapitalCostDAO;
	@Resource ProductDAO productDAO;
	@Resource ProductElementDAO productElementDAO;
	
	
	/**
	 * This method returns the amount recharged for the CD Fee capital element for CE quote.
	 * 
	 * @param qelId
	 *            return CDFeeRechargedAmount
	 */
	public BigDecimal	getCDFeeRechargedAmountOnQuoteCapitalForCE(Long fmsId){
		BigDecimal amount = BigDecimal.ZERO;
		String	lineDesc = "Delivery charge%";
		List<Docl> list = doclDAO.findInvoiceLineForQuoteElementByUserDef2AndLineTypeAndSourceAndDesc(fmsId.toString(),  "INVOICEAR", "FLTRANS",lineDesc);
		if (list != null && list.size() > 0)
			amount = list.get(0).getTotalPrice();
		return amount;
	}
	
	/**
	 * This method returns the amount recharged for the CD Fee capital element.
	 * 
	 * @param qceId
	 *            return CDFeeRechargedAmount
	 */
	public BigDecimal	getCDFeeRechargedAmountOnQuoteCapital(Long qceId){
		BigDecimal amount = BigDecimal.ZERO;
		List<Docl> list = doclDAO.findInvoiceLineForQuoteCapitalByUserDef1AndLineTypeAndSourceCode(qceId, "CAPITAL ELEMENT", "INVOICEAR", "FLRECHARGE");
		if (list != null && list.size() > 0)
			amount = list.get(0).getTotalPrice();
		return amount;
	}

	/**
	 * This method return sum of all the Invoices amount for a given qmdId and
	 * userDef. The valid value of userDef can be MODEL , FACTORY, DEALER etc..
	 * 
	 * @param qmdId
	 * @param user
	 *            def
	 * @return user def InvoiceLine Cost
	 */

	public BigDecimal getUserDefInvoiceLineCost(Long qmdId, String userDef , List<Doc> docList) {

		BigDecimal cost = BigDecimal.ZERO;
		
		if (docList != null && userDef != null) {
			for (Doc doc : docList) {
				List<Docl> dollList = doc.getDocls();
				for (Docl docl : dollList) {
					String userDef4 = docl.getUserDef4();
					if (userDef4 != null) {
						if (userDef4.equals(userDef)) {
							cost = cost.add(docl.getUnitCost());
						}
					}
				}
			}
		}

		return cost;

	}

	/**
	 * This method return sum of all the Invoices amount for a given qmdId and
	 * extId (Used Capital Element ID).
	 * 
	 * @param qmdId
	 * @param extId
	 */
	private BigDecimal getInvoicedCapitalElementCost(Long extId, List<Doc> docList) {

		BigDecimal cost = BigDecimal.ZERO;
		if (docList != null && extId != null) {
			for (Doc doc : docList) {
				List<Docl> dollList = doc.getDocls();
				for (Docl docl : dollList) {
					Long genericExtId = docl.getGenericExtId();
					if (genericExtId != null) {
						if (genericExtId.equals(extId)) {
							cost = docl.getUnitCost();
							break;
						}
					}
				}
			}

		}

		return cost;

	}

	@Override
	public QuoteCost getTotalCostsForQuote(QuotationModel quotationModel) throws MalBusinessException {
		QuoteCost quoteCost = new QuoteCost();
		
		quoteCost.setCustomerCost(getLeaseElementCostByType(quotationModel, MalConstants.FINANCE_ELEMENT));
		
		BigDecimal malCost = BigDecimal.ZERO;
		
		List<MalCapitalCost> list = malCapitalCostDAO.findMalCapitalCostByFinalizeQuote(quotationModel.getQmdId());
		if(list != null && list.size() > 0) {
			for(MalCapitalCost mcc : list) {
				malCost = malCost.add(mcc.getTotalPrice());
			}
		} else {
			if(quotationModel.getBasePrice() != null) {
				malCost = malCost.add(quotationModel.getBasePrice());
			}
			
			for(QuotationCapitalElement qce : quotationModel.getQuotationCapitalElements()) {
				if(qce.getValue() != null) {
					if(qce.getQuoteCapital().equalsIgnoreCase("Y")) {
						malCost = malCost.add(qce.getValue());
					}
				}
			}

			malCost = malCost.add(getDealerAccessoryCost(quotationModel));
			malCost = malCost.add(getFactoryEquipmentCost(quotationModel));
		}
		
		//OER-1174 changes .. Read  OE Capital Contribution form revised quote as well as fist finalze quote() 
		malCost = malCost.subtract(quotationModelDAO.getApplicableCapitalContribution(quotationModel.getQmdId()));
		

		quoteCost.setDealCost(malCost);
		
		
		return quoteCost;
	}
	
	
	
	public List<Doc> getInvoiceForCapitalElementByQuote(Long qmdId, String docType, String sourceType, boolean postedOnlyFlag) {
		List<Doc> rawDocList = docDAO.findInvoiceForQuoteByDocTypeAndSourceCode(qmdId, docType, sourceType);
		List<Product> productList = productDAO.findAll();
		List<Doc> finalDocList = new ArrayList<Doc>();
		for(Doc d : rawDocList) {
			if( (postedOnlyFlag && d.getDocStatus().equalsIgnoreCase(DOC_STATUS_POSTED)) || (!postedOnlyFlag) ) {
				if(d.getUpdateControlCode() != null) {
					for (Product product : productList) {
						if (product.getProductCode().equalsIgnoreCase(d.getUpdateControlCode()) || product.getDtTranType().equalsIgnoreCase(d.getUpdateControlCode()) )  {
							finalDocList.add(d);
							break;
						}
					}
				}
			}
		}
		return finalDocList;
	}

	
	
	
	/**
	 * This method provides detail of equipment cost breakdown for a particular
	 * quotation model. It returns a map of all equipment and their cost as key
	 * value pair.
	 */
	public Map<String, Double> getInvoicedEquipmentDetail(long qmdId, String equipmentType, String docType, String sourceType ) throws MalBusinessException {

		Map<String, Double> costDetailMap = new HashMap<String, Double>();

		List<Doc> docList = getInvoiceForCapitalElementByQuote(qmdId,docType, sourceType, true);
		if (docList != null && equipmentType != null) {
			for (Doc doc : docList) {
				List<Docl> dollList = doc.getDocls();
				for (Docl docl : dollList) {
					String userDef4 = docl.getUserDef4();
					if (userDef4 != null && userDef4.equals(equipmentType)) {

						if (equipmentType.equals(MalConstants.FACTORY_EQUIPMENT_INV)) {
							if (docl.getUnitCost().doubleValue() != 0) {
								OptionalAccessory optionalAccessory = optionalAccessoryDAO.findById(docl.getGenericExtId()).orElse(null);

								String key = optionalAccessory.getAccessoryCode().getDescription();
								Double value = docl.getUnitCost().doubleValue();
								costDetailMap.put(key, value);
							}
						} else if (equipmentType.equals(MalConstants.AFTER_MARKET_DEALER_INV)) {
							if (docl.getUnitCost().doubleValue() != 0) {

								DealerAccessory dealerAccessory = dealerAccessoryDAO.findById(docl.getGenericExtId()).orElse(null);

								String key = dealerAccessory.getDealerAccessoryCode().getDescription();
								Double value = docl.getUnitCost().doubleValue();
								costDetailMap.put(key, value);
							}
						}
					}
				}
			}
		}

		return costDetailMap;
	}
	




	public BigDecimal getLeaseElementCostByType(QuotationModel quotationModel, String type) {
		BigDecimal cost = BigDecimal.ZERO;

		if (quotationModel != null) {
			List<QuotationElement> quotationElementList = quotationModel.getQuotationElements();

			for (QuotationElement quotationElement : quotationElementList) {
				LeaseElement leaseElement = quotationElement.getLeaseElement();
				if (leaseElement != null && quotationElement.getIncludeYn().equalsIgnoreCase("Y") && leaseElement.getElementType().equalsIgnoreCase(type)) {
					if(quotationElement.getCapitalCost() != null) {
						cost = cost.add(quotationElement.getCapitalCost());
					}
				}
			}
		}

		return cost;
	}

	
	private QuoteCapitalCosts findQuoteCapitalCostsCalc(QuotationModel quotationModel){
		QuoteCapitalCosts capitalCostCalc = null;
		String processorName = null;
		
		QuotationElement mainElement = quotationService.getMainQuoteElement(quotationModel.getQmdId());
		if(!MALUtilities.isEmpty(mainElement)){
			processorName = mainElement.getLeaseElement().getProcessorName();

		}else{
			ProductElement prodEle = productElementDAO.findProductElementByProductCode(quotationModel.getQuotation().getQuotationProfile().getPrdProductCode());
			processorName = prodEle.getLeaseElement().getProcessorName();
		}

		if(MALUtilities.isNotEmptyString(processorName)){
			LeaseElementProcessor leaseElementProcessor = (LeaseElementProcessor) SpringAppContext.getBean(processorName);
			capitalCostCalc = leaseElementProcessor.getCapitalCostsCalc();
		}
		
		return capitalCostCalc;
	}
	
	public String findQuoteCapitalCostsCalcType(QuotationModel quotationModel){
		QuoteCapitalCosts capitalCostCalc = this.findQuoteCapitalCostsCalc(quotationModel);
		return capitalCostCalc.getClass().getSimpleName();
	}
	
	
	private QuoteCostElementVO findQuoteCostElementByQuoteCapitalElement(List<QuoteCostElementVO> costElementList, QuotationCapitalElement quoteCapitalElement){
		QuoteCostElementVO costElement = null;
		for (QuoteCostElementVO quoteCostElementVO : costElementList) {
			if(quoteCostElementVO.getElementId()!= null ){
				if(quoteCostElementVO.getElementId().longValue() == quoteCapitalElement.getCapitalElement().getCelId()){
					costElement = quoteCostElementVO;
					break;
				}
			}
		}
		
		return costElement;
	}
	
	private QuotationModelAccessory findModelAccessoryByCostElement(QuotationModel quotationModel, QuoteCostElementVO costElement){
		QuotationModelAccessory retVal = null;
		for (QuotationModelAccessory modelAccessory : quotationModel.getQuotationModelAccessories()) {
			if( modelAccessory.getOptionalAccessory().getOacId().longValue() == costElement.getElementId().longValue() ){
				retVal = modelAccessory;
				break;	
			}					
		}
		
		return retVal;
	}
	
	private QuotationDealerAccessory findDealerAccessoryByCostElement(QuotationModel quotationModel, QuoteCostElementVO costElement){
		QuotationDealerAccessory retVal = null;
		for (QuotationDealerAccessory dealerAccessory : quotationModel.getQuotationDealerAccessories()) {
			if( dealerAccessory.getDealerAccessory().getDacId().longValue() == costElement.getElementId().longValue() ){
				retVal = dealerAccessory;
				break;	
			}					
		}
		
		return retVal;
	}
	
	
	/**
	 * This will return an object holding all the appropriate capital cost
	 * values for a given qmdId Mary Beth had said this is a future story
	 */
	@Override
	public QuoteVO getQuoteCapitalCosts(QuotationModel quotationModel, Boolean finalized,QuoteVO priorQuote, Boolean isStockOrder) throws MalBusinessException {
		QuoteVO quote = new QuoteVO();
		quote.setQuotationModel(quotationModel);

		QuoteCostElementVO calcedElement = null;
		
		if (quotationModel != null) {
			String leaseType = quotationService.getLeaseType(quotationModel.getQmdId());
			
			List<QuoteCostElementVO> elementList = capitalCostElementService.getCapitalCostElementList(quotationModel.getQmdId());
			List<QuoteCostElementVO> calculatedList = new ArrayList<QuoteCostElementVO>();

			// *find the correct capital calculation based upon the main lease element for this quote
			QuoteCapitalCosts capitalCostCalc = this.findQuoteCapitalCostsCalc(quotationModel);
					
			// load up a map with the quotation capital elements for that quote keyed off of the capital element id (1 per capital element)
			// also load up a "backup map"?
			// as a "side-effect" also find any cost element and update it with the quote capital element id
			Map<Long, QuotationCapitalElement> qceMap = new HashMap<Long, QuotationCapitalElement>();
			for (QuotationCapitalElement qce : quotationModel.getQuotationCapitalElements()) {
				qceMap.put(qce.getCapitalElement().getCelId(), qce);
				//For RC-32, set quotation capital element id
				QuoteCostElementVO costElement = this.findQuoteCostElementByQuoteCapitalElement(elementList, qce);
				if(!MALUtilities.isEmpty(costElement)){
					costElement.setQuotationCapitalElementId(qce.getQceId());
				}
			}
						
			// for each element in elementList calculate it's Deal and Client costs
			for (QuoteCostElementVO element : elementList) {
				if (element.isCapitalElements()) {
					QuotationCapitalElement qce = qceMap.get(element.getElementId());
					if(MALUtilities.isEmpty(qce)){
						// this is a "placeholder" entry in this flow to get errors from happening.
						element.setDealCost(BigDecimal.ZERO);
						element.setCustomerCost(BigDecimal.ZERO);
						calcedElement = element;
					}else{
						calcedElement = capitalCostCalc.calcQuoteCapitalElement(qce);
					}

					//this is a 1-off condition for OE Lease; unfortunately it needs to stay.
					if (calcedElement.getElementcode().equals("CD_FEE")) {
						BigDecimal rechargeAmount = BigDecimal.ZERO;
						if (leaseType.equals(QuotationService.OPEN_END_LEASE)) {
							rechargeAmount = getCDFeeRechargedAmountOnQuoteCapital(calcedElement.getQuotationCapitalElementId());
						}
						if (rechargeAmount.compareTo(BigDecimal.ZERO) > 0) {
							calcedElement.setRechargeAmt(rechargeAmount);
						}
					}
				} else {
					if(element.isModelAccessories()){
						QuotationModelAccessory modelAccessory = this.findModelAccessoryByCostElement(quotationModel, element);
						calcedElement = capitalCostCalc.calcModelAccessoryElement(modelAccessory);
					}else if(element.isDealerAccessories()){
						QuotationDealerAccessory dealerAccessory = this.findDealerAccessoryByCostElement(quotationModel, element);
						calcedElement = capitalCostCalc.calcDealerAccessoryElement(dealerAccessory);
					}else{
						calcedElement = capitalCostCalc.calcCapitalCostOther(quotationModel, element);
					}
				}
				
				// TODO: these blocks of code exist to "fix" 1-off conditions; try to remove or move them if we can
				if (quotationModel.getPreContractFixedCost().equals("F")) {
					//Added below code for Bug 16456 to fix the Client capital cost which was incorrect while doing the Revise Order
					int quoteStatus = quotationModel.getQuoteStatus();
					if(!(quoteStatus == 12) && !(quoteStatus == 13)){
						if (priorQuote != null) {
							calcedElement.setCustomerCost(getElementCostOnQuote(priorQuote, calcedElement, false));// sending false to fix RC-1223 story
						} else {
							//change for RC-1234, quote was only re computed on PO026 but not completed and still in revised state
							if(quotationModel.getOriginalQmdId() != null){
								QuotationModel originalQuoteModel = quotationModelDAO.findById(quotationModel.getOriginalQmdId()).orElse(null);
								QuoteVO priorAcceptedQuote = capitalCostService.getQuoteCapitalCosts(originalQuoteModel, false, null,false);
								if(priorAcceptedQuote != null){
									calcedElement.setCustomerCost(getElementCostOnQuote(priorAcceptedQuote, calcedElement, false));
								}
							}
						}
					}
				}
				
				// added this to handle amendments without trying to touch the rest of the code and cause lots of regression testing at this  
				//    late point in the release.  This should be refactored.
				if (isAmendment(quotationModel) && element.getElementName().equalsIgnoreCase(
						NonCapitalElementEnum.AFTER_MARKET_EQUIPMENT_ELEMENT.getElementName())) {
					calcedElement.setCustomerCost(calcedElement.getDealCost());
				}
				

				calculatedList.add(calcedElement);
				
			}

			// then add it to the quote costs at the end
			quote.setCostElements(calculatedList);
			
    		// also sum everything together to get the totals
    	    for (QuoteCostElementVO element : calculatedList) {
    	    	quote.setTotalCostToPlaceInServiceDeal(quote.getTotalCostToPlaceInServiceDeal().add(element.getDealCost()));
    	    	quote.setTotalCostToPlaceInServiceCustomer(quote.getTotalCostToPlaceInServiceCustomer().add(element.getCustomerCost()));
    	    }
		}
		
		return quote;
		
	}

	
	
	
	
	
	public QuoteVO getQuoteCapitalCosts2(QuotationModel quotationModel, Boolean finalized,QuoteVO priorQuote, Boolean isStockOrder) throws MalBusinessException {

		List<QuoteCostElementVO> elementList = capitalCostElementService.getCapitalCostElementList(quotationModel.getQmdId());

		QuoteVO quote = new QuoteVO();
		quote.setQuotationModel(quotationModel);
		quote.setCostElements(elementList);
		String leaseType = quotationService.getLeaseType(quotationModel.getQmdId());
		String quoteCapitalFlag = null;
		String onInvoiceFlag = null;
		List<MalCapitalCost> malCapitalCostList =  null;
		if (quotationModel != null) {
			
			Map<Long, QuotationCapitalElement> qceMap = new HashMap<Long, QuotationCapitalElement>();
			for (QuotationCapitalElement qce : quotationModel.getQuotationCapitalElements()) {
				qceMap.put(qce.getCapitalElement().getCelId(), qce);
				//For RC-32, set quotation capital element id
				for (QuoteCostElementVO quoteCostElementVO : elementList) {
					if(quoteCostElementVO.getElementId()!= null ){
						if(quoteCostElementVO.getElementId().longValue() == qce.getCapitalElement().getCelId()){
							quoteCostElementVO.setQuotationCapitalElementId(qce.getQceId());
							break;
						}
					}
					
				}
			}
			Map<Long, QuotationCapitalElementBackup> qceMapBackup = new HashMap<Long, QuotationCapitalElementBackup>();
			for (QuotationCapitalElementBackup qce : quotationModel.getQuotationCapitalElementsBackup()) {
				qceMapBackup.put(qce.getCapitalElement().getCelId(), qce);
			}
			if(finalized) {
				malCapitalCostList =  malCapitalCostDAO.findMalCapitalCostByFinalizeQuote(quote.getQuotationModel().getQmdId());
			}
			for (QuoteCostElementVO element : elementList) {

				BigDecimal cost = BigDecimal.ZERO;
				BigDecimal zero = BigDecimal.ZERO;
				quoteCapitalFlag = null;
				onInvoiceFlag = null;

				if (element.isCapitalElements()) {
					QuotationCapitalElement qce = qceMap.get(element.getElementId());
					QuotationCapitalElementBackup qceBackup = qceMapBackup.get(element.getElementId());

					if (qce != null) { // checking to see if this capital element applies to this type of quote
						if (finalized) {
							cost = qce.getValue();
						} else {
							if (qceBackup == null) {
								cost = qce.getValue();
							} else {
								cost = qceBackup.getValue();
							}
						}
						quoteCapitalFlag = getOverriddenQuoteCapitalFlag(element.getElementId(), qce);
						onInvoiceFlag = getOverriddenOninvoiceFlag(element.getElementId(), qce);

						cost = getOverriddenCost(cost, quoteCapitalFlag, onInvoiceFlag, leaseType);

						if (element.getElementcode().equals("CD_FEE")) {
							BigDecimal rechargeAmount = BigDecimal.ZERO;
							if (leaseType.equals(QuotationService.OPEN_END_LEASE)) {
								rechargeAmount = getCDFeeRechargedAmountOnQuoteCapital(element.getQuotationCapitalElementId());
							}
							if (rechargeAmount.compareTo(zero) > 0) {
								element.setRechargeAmt(rechargeAmount);
							}
						}
					} else {
						cost = zero;
					}

				} else {
					
				
					if( element.isModelAccessories() || element.isDealerAccessories()){
						cost = getAccessoryCost(quotationModel, element);
						element.setRechargeAmt(getAccessoryRechargeAmount(quotationModel, element));
					}else{
						cost = getNonCapitalElementCost(quotationModel, element);
					}
					
				}

				element.setDealCost(cost);
				
				
				BigDecimal realCost = cost;
				
				if (finalized) {
					//TODO We  need to revisit code, Once we will populate cost in MAL_CAPITAL_COST table for all finalize quote,  
					cost = getDealCostOfFinalizeQuote(element ,malCapitalCostList);
					element.setDealCost(cost);
					if (isStockOrder) {
						element.setCustomerCost(getElementCostOnQuote(priorQuote, element, false));
					} else {
						element.setCustomerCost(getCustomerCostFinalized(quotationModel, element, priorQuote,onInvoiceFlag, realCost));
					}
				} else {
					element.setCustomerCost(getCustomerCost(quotationModel, element, leaseType, onInvoiceFlag));
				}

				if (quoteCapitalFlag != null && quoteCapitalFlag.equals("N")
						&& leaseType.equals(QuotationService.OPEN_END_LEASE)) {
					element.setDealCost(zero);
				}

				if (quotationModel.getPreContractFixedCost().equals("F")) {
					//Added below code for Bug 16456 to fix the Client capital cost which was incorrect while doing the Revise Order
					int quoteStatus = quotationModel.getQuoteStatus();
					if(!(quoteStatus == 12) && !(quoteStatus == 13)){
						if (priorQuote != null) {
							element.setCustomerCost(getElementCostOnQuote(priorQuote, element, false));// sending false to fix RC-1223 story
						} else {
							//change for RC-1234, quote was only re computed on PO026 but not completed and still in revised state
							if(quotationModel.getOriginalQmdId() != null){
								QuotationModel originalQuoteModel = quotationModelDAO.findById(quotationModel.getOriginalQmdId()).orElse(null);
								QuoteVO priorAcceptedQuote = capitalCostService.getQuoteCapitalCosts(originalQuoteModel, false, null,false);
								if(priorAcceptedQuote != null){
									element.setCustomerCost(getElementCostOnQuote(priorAcceptedQuote, element, false));
								}
							}
						}
					}
				}
				
				// added this to handle amendments without trying to touch the rest of the code and cause lots of regression testing at this  
				//    late point in the release.  This should be refactored.
				if (isAmendment(quotationModel) && element.getElementName().equalsIgnoreCase(
						NonCapitalElementEnum.AFTER_MARKET_EQUIPMENT_ELEMENT.getElementName())) {
					element.setCustomerCost(realCost);
				}
				
			}
		}
		
		
		return quote;
		
		
		

	}	
		
	private BigDecimal getDealCostOfFinalizeQuote(QuoteCostElementVO costElement , List<MalCapitalCost> malCapitalCostList ) {
		
		BigDecimal cost = costElement.getDealCost();
		
		if(malCapitalCostList != null && malCapitalCostList.size() > 0){
			
				for (MalCapitalCost malCapitalCost : malCapitalCostList) {
					if(malCapitalCost.getElementId() != null  && costElement.getElementId() != null){
						if(malCapitalCost.getElementId().longValue() == costElement.getElementId().longValue())
							cost = malCapitalCost.getTotalPrice();
					}else if(costElement.getElementName().equals("Base Vehicle") && malCapitalCost.getElementType().equals("MODEL")){
						cost =  malCapitalCost.getTotalPrice();
						}
					}
			
		}
		
		return cost;
	}

	/**
	 * This will return an object holding all the appropriate invoice cost
	 * values for a given qmdId
	 * 
	 */

	@Override
	public QuoteVO getInvoiceCapitalCosts(QuotationModel quotationModel, List<QuoteCostElementVO> allCostElement , Boolean isStockOrder, boolean postedOnlyFlag ) throws MalBusinessException {

		
		QuoteVO quote = new QuoteVO();	
		quote.setCostElements(allCostElement);
		
		List<Doc> docList = getInvoiceForCapitalElementByQuote(quotationModel.getQmdId(), "INVOICEAP", "POINV", postedOnlyFlag);
		
		for (QuoteCostElementVO costElementVO : allCostElement) {
			
			if(isStockOrder){			
				costElementVO.setDealCost(BigDecimal.ZERO);
			} else {									
			
				if (costElementVO.isCapitalElements()) {
					costElementVO.setDealCost(getInvoicedCapitalElementCost(costElementVO.getElementId(), docList));
				} else if (costElementVO.getElementName().equals(NonCapitalElementEnum.BASE_VEHICLE_ELEMENT.getElementName())) {
					costElementVO.setDealCost( getUserDefInvoiceLineCost(quotationModel.getQmdId(), "MODEL", docList));
				} else if (costElementVO.isModelAccessories()) {
					costElementVO.setDealCost(getInvoicedEquipmentElementCost( costElementVO.getElementId() ,MalConstants.FACTORY_EQUIPMENT_INV, docList));
				} else if (costElementVO.isDealerAccessories()) {
					costElementVO.setDealCost(getInvoicedEquipmentElementCost(costElementVO.getElementId(), MalConstants.AFTER_MARKET_DEALER_INV, docList));
				}
	
			}
		}
		
		return quote;
	}
	
	public QuoteVO populateReclaimCosts(QuotationModel firstQuote, QuotationModel finalizedQuoteModel, QuoteVO invoiceQuoteVO ) throws MalBusinessException {
		
		String unitNo = null;
		Long fmsId =  null;
		if(finalizedQuoteModel != null){
			unitNo = finalizedQuoteModel.getUnitNo();
		}else{
			unitNo = firstQuote.getUnitNo();
		}
		if(unitNo != null){
			FleetMaster fm  = fleetMasterDAO.findByUnitNo(unitNo);		
			if(fm != null){
				fmsId =  fm.getFmsId();
			}
		}
		
		if(fmsId == null )
			return invoiceQuoteVO;
		
		List<Dist> distList = null;
		for (QuoteCostElementVO costElementVO : invoiceQuoteVO.getCostElements()) {	
			
			if((MALUtilities.isEmpty(costElementVO.getDealCost()) ||  costElementVO.getDealCost().equals(BigDecimal.ZERO)) 
					&& costElementVO.isCapitalElements() &&  fmsId != null){
				
				if(distList == null)
					 distList = distDAO.findDistByCDBCode1And4(String.valueOf(fmsId), "RECLAIMS");
					
				 QuotationCapitalElement  currentQuotationCapitalElement  = null;
				 List<QuotationCapitalElement>   quotationCapitalElementList  = firstQuote.getQuotationCapitalElements();
				 for (QuotationCapitalElement quotationCapitalElement : quotationCapitalElementList) {
					
					if( costElementVO.getElementId() != null &&  costElementVO.getElementId() == quotationCapitalElement.getCapitalElement().getCelId() ){
						currentQuotationCapitalElement = quotationCapitalElement;							
						break;
					}
				}
				 if(currentQuotationCapitalElement != null)	 {
					for (Dist dist : distList) {
						Docl docl= dist.getDocl();
						  if(docl != null && docl.getLineType() != null && docl.getLineType().equals("INVOICEAR") 
								  && docl.getSourceCode() != null && docl.getSourceCode().equals("FLRECLAIM")
								  && docl.getUserDef5() != null && String.valueOf(currentQuotationCapitalElement.getQceId()).equals(docl.getUserDef5())){
							 
							  if(docl.getUnitPrice() != null){
								  costElementVO.setDealCost(docl.getUnitPrice().negate());
								  costElementVO.setReclaim(true);
							  }
						  }
					}
			   
				}
			}

		}
		
		return invoiceQuoteVO;
	}
	
	public BigDecimal getInvoicedEquipmentElementCost(long elementId, String equipmentType ,List<Doc> docList) throws MalBusinessException {
		
		BigDecimal  cost = BigDecimal.ZERO;
		
		if (docList != null && equipmentType != null) {
			for (Doc doc : docList) {
				List<Docl> dollList = doc.getDocls();
				for (Docl docl : dollList) {
					String userDef4 = docl.getUserDef4();
					if (userDef4 != null && userDef4.equals(equipmentType)) {

						if (docl.getUnitCost().doubleValue() != 0) {
							if(elementId == docl.getGenericExtId().longValue())
								cost = cost.add(docl.getUnitCost());
							}
						
					}
				}
			}
		}

		return cost;
	}
	
	
	private BigDecimal getNonCapitalElementCost(QuotationModel quotationModel, QuoteCostElementVO targetElement) {

		if (targetElement.getElementName().equalsIgnoreCase(NonCapitalElementEnum.BASE_VEHICLE_ELEMENT.getElementName())) {
			return quotationModel.getBasePrice();
		} else if (targetElement.getElementName().equalsIgnoreCase(
				NonCapitalElementEnum.CAPITAL_CONTRIBUTION_ELEMENT.getElementName())) {
			BigDecimal capitalContribution = quotationModel.getCapitalContribution();
			if (capitalContribution != null) {
				return capitalContribution.multiply(new BigDecimal(-1));
			}
		}
		return BigDecimal.ZERO;
	}
	
	private BigDecimal getAccessoryCost(QuotationModel quotationModel, QuoteCostElementVO targetElement) {
		
		BigDecimal cost =  BigDecimal.ZERO;
			
		 if (targetElement.isModelAccessories()) {
				
			for (QuotationModelAccessory modelAccessory : quotationModel.getQuotationModelAccessories()) {
				if( modelAccessory.getOptionalAccessory().getOacId().longValue() == targetElement.getElementId().longValue() ){
					if ( modelAccessory.getDiscPrice() != null) {
						cost = modelAccessory.getDiscPrice() ;
					}
					if ( modelAccessory.getRechargeAmount() != null) {
						cost = cost.subtract(modelAccessory.getRechargeAmount());						
					}
					
					break;	
				}					
			}
			 
					
		} else if (targetElement.isDealerAccessories()) {
				
			for (QuotationDealerAccessory dealerAccessory : quotationModel.getQuotationDealerAccessories()) {
				if( dealerAccessory.getDealerAccessory().getDacId().longValue() == targetElement.getElementId().longValue() ){
					if ( dealerAccessory.getDiscPrice() != null) {
						cost = dealerAccessory.getDiscPrice() ;
					}
					if ( dealerAccessory.getRechargeAmount() != null) {
						cost = cost.subtract(dealerAccessory.getRechargeAmount());						
					}					
					break;	
				}					
			}		 
					
		}
		
		return cost;
	}
	
	private BigDecimal getAccessoryRechargeAmount(QuotationModel quotationModel, QuoteCostElementVO targetElement) {
		
		BigDecimal rechargeAmount =  BigDecimal.ZERO;
			
		 if (targetElement.isModelAccessories()) {
				
			for (QuotationModelAccessory modelAccessory : quotationModel.getQuotationModelAccessories()) {
				if( modelAccessory.getOptionalAccessory().getOacId().longValue() == targetElement.getElementId().longValue() ){	
					rechargeAmount = modelAccessory.getRechargeAmount();
					break;		
				}					
			}
			 
					
		} else if (targetElement.isDealerAccessories()) {
				
			for (QuotationDealerAccessory dealerAccessory : quotationModel.getQuotationDealerAccessories()) {
				if( dealerAccessory.getDealerAccessory().getDacId().longValue() == targetElement.getElementId().longValue() ){
					rechargeAmount  = dealerAccessory.getRechargeAmount();
					break;	
				}					
				
				}					
			}		
		
		return rechargeAmount;
	}

	@Deprecated
	private BigDecimal getCustomerCost(QuotationModel quotationModel, QuoteCostElementVO element, String leaseType,
			String onInvoiceFlag) {

		if (element.isCapitalElements() == false
				|| leaseType.equals(QuotationService.CLOSE_END_LEASE)
				|| (leaseType.equals(QuotationService.OPEN_END_LEASE) && onInvoiceFlag != null && onInvoiceFlag
						.equals(MalConstants.FLAG_Y))) {
			return element.getDealCost();
		}
		return BigDecimal.ZERO;
	}

	private BigDecimal getCustomerCostFinalized(QuotationModel quotationModel, QuoteCostElementVO element,
			QuoteVO acceptedQuote, String onInvoiceFlag, BigDecimal realCost) {

		BigDecimal cost = BigDecimal.ZERO;
		String leaseType = quotationService.getLeaseType(quotationModel.getQmdId());

		Boolean lfMarginOnly = false;
		if (element.getLfMarginOnly() != null) {
			if (element.getLfMarginOnly().equals("Y")) {
				lfMarginOnly = true;
			}
		}
		if (leaseType.equals(QuotationService.CLOSE_END_LEASE)) {
			if (lfMarginOnly) {
				return getElementCostOnQuote(acceptedQuote, element, false);
			} else {
				return realCost;
			}
		}
		if (leaseType.equals(QuotationService.OPEN_END_LEASE)) {
			if (onInvoiceFlag == null || onInvoiceFlag.equals("Y")) {
				return realCost;
			}
		}

		return cost;
	}

	@Deprecated
	private BigDecimal getElementCostOnQuote(QuoteVO quote, QuoteCostElementVO targetElement, Boolean isDeal) {

		BigDecimal cost = BigDecimal.ZERO;
		for (QuoteCostElementVO element : quote.getCostElements()) {
			if (element.getElementName().equals(targetElement.getElementName())) {
				if (isDeal) {
					return element.getDealCost();
				} else {
					return element.getCustomerCost();
				}
			}
		}
		return cost;
	}
	
	@Deprecated
	private String getOverriddenOninvoiceFlag(Long celId, QuotationCapitalElement quotationCapitalElement) {

		String onInvoiceFlag = quotationCapitalElement.getOnInvoice();
		if (onInvoiceFlag == null && celId != null) {
			CapitalElement capitalElement = capitalElementDAO.findById(celId).orElse(null);
			onInvoiceFlag = capitalElement.getOnInvoice();
		}
		return onInvoiceFlag;
	}
	
	@Deprecated
	private String getOverriddenQuoteCapitalFlag(Long celId, QuotationCapitalElement quotationCapitalElement) {

		String quoteCapitalFlag = quotationCapitalElement.getQuoteCapital();
		if (quoteCapitalFlag == null && celId != null) {
			CapitalElement capitalElement = capitalElementDAO.findById(celId).orElse(null);
			quoteCapitalFlag = capitalElement.getQuoteCapital();
		}
		return quoteCapitalFlag;
	}
	
	@Deprecated
	private BigDecimal getOverriddenCost(BigDecimal cost, String capitalElementFlag, String onInvoiceFlag, String leaseType) {

		BigDecimal zero = BigDecimal.ZERO;

		if (capitalElementFlag.equals("Y")) {
			return cost;
		} else {
			if (leaseType.equals(QuotationService.CLOSE_END_LEASE)) {
				return zero;
			} else if (leaseType.equals(QuotationService.OPEN_END_LEASE)) {
				if (onInvoiceFlag.equals("N")) {
					return zero;
				} else {
					return cost;
				}
			} else { // not CE or OE lease
				return cost;
			}
		}
	}
	

	public List<CapitalElement> getCapitalElementByProductCode(String productCode) {
		return capitalElementDAO.getCapitalElementsByProductCode(productCode);
	}
	
	@Transactional	
	public QuotationCapitalElement saveQuotationCapitalElement(QuotationCapitalElement quotationCapitalElement) throws Exception {
		try {
			QuotationCapitalElement qce = quotationCapitalElementDAO.saveAndFlush(quotationCapitalElement);
			return qce;
		} catch (Exception ex) {
			throw  ex;
		}		
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateQuoteCapitalElement(long qmdId,  String capitalElementCode , BigDecimal  value) throws MalBusinessException{
	    
	    QuotationCapitalElement quotationCapitalElement =  quotationCapitalElementDAO.findByQmdIDAndCapitalElementCode(qmdId, capitalElementCode);
	    if(quotationCapitalElement != null){
		quotationCapitalElement.setValue(value);
		quotationCapitalElementDAO.save(quotationCapitalElement);
	    }
		
	}

	@Override	
	public BigDecimal getQuoteCapitalElementValue(Long qmdId, String capitalElementCode) throws MalBusinessException {
	  
	    BigDecimal value = null;
	    QuotationCapitalElement quotationCapitalElement = quotationCapitalElementDAO.findByQmdIDAndCapitalElementCode(qmdId, capitalElementCode);
	    if(quotationCapitalElement != null) value =  quotationCapitalElement.getValue();
	    return value;
	
	}
	
	
	public boolean isAmendment(QuotationModel qmd) {
		if(qmd.getAmendmentEffectiveDate() != null){
			return true;
		}
		return false;
	}
	
	/**
	 * This method return sum of all recharged amount against dealer
	 * Accessory(after market)
	 * 
	 * @param qmdId
	 * @return total dealer re charge amount
	 */
	@Deprecated
	public BigDecimal getDealerRechargeAmount(QuotationModel quotationModel) {

		BigDecimal cost = BigDecimal.ZERO;
		
		for (QuotationDealerAccessory quotationDealerAccessory : quotationModel.getQuotationDealerAccessories()) {
				if (quotationDealerAccessory.getRechargeAmount() != null) {
					cost = cost.add(quotationDealerAccessory.getRechargeAmount());
				}
		}

		return cost;
	}

	/**
	 * This method return sum of all recharged amount against model
	 * Accessory(Manufacturer)
	 * 
	 * @param qmdId
	 * @return total manufacturer re charge amount
	 */
	@Deprecated //not used any where 
	public BigDecimal getManufacturerRechargeAmount(QuotationModel quotationModel) {

		BigDecimal cost = BigDecimal.ZERO;
		for (QuotationModelAccessory quotationModelAccessory : quotationModel.getQuotationModelAccessories()) {
			if (quotationModelAccessory != null) {
				if (quotationModelAccessory.getRechargeAmount() != null) {
					cost = cost.add(quotationModelAccessory.getRechargeAmount());
				}

			}
		}

		return cost;
	}

	/**
	 * This method returns the amount recharged for the CD Fee capital element.
	 * 
	 * @param fmsId
	 *            return CDFeeRechargedAmount
	 */
	@Deprecated
	public BigDecimal getCDFeeRechargedAmount(Long fmsId) {

		BigDecimal amount = BigDecimal.ZERO;

		List<Docl> doclList = doclDAO.findInvoiceLineForUnitByUserDef1AndLineTypeAndSourceCode(String.valueOf(fmsId),
				"CAPITAL ELEMENT", "INVOICEAR", "FLRECHARGE");
		if (doclList != null && doclList.size() > 0)
			amount = doclList.get(0).getTotalCost();

		return amount;
	}
	
	@Deprecated
	public Map<String, Double> getInvoicedAccessoryCost( String accessoryType, QuoteCostElementVO costElementVO ,List<Doc> docList  ) throws MalBusinessException {

		Map<String, Double> costDetailMap = new HashMap<String, Double>();

		if (docList != null && accessoryType != null) {
			for (Doc doc : docList) {
				List<Docl> dollList = doc.getDocls();
				for (Docl docl : dollList) {
					String userDef4 = docl.getUserDef4();
					if (userDef4 != null && userDef4.equals(accessoryType)) {
						
						if (accessoryType.equals(MalConstants.FACTORY_EQUIPMENT_INV)) {
							if (docl.getUnitCost().doubleValue() != 0) {
								OptionalAccessory optionalAccessory = optionalAccessoryDAO.findById(docl.getGenericExtId()).orElse(null);

								String key = optionalAccessory.getAccessoryCode().getDescription();
								Double value = docl.getUnitCost().doubleValue();
								costDetailMap.put(key, value);
							}
						} else if (accessoryType.equals(MalConstants.AFTER_MARKET_DEALER_INV)) {
							if (docl.getUnitCost().doubleValue() != 0) {

								DealerAccessory dealerAccessory = dealerAccessoryDAO.findById(docl.getGenericExtId()).orElse(null);

								String key = dealerAccessory.getDealerAccessoryCode().getDescription();
								Double value = docl.getUnitCost().doubleValue();
								costDetailMap.put(key, value);
							}
						}
					}
				}
			}
		}

		return costDetailMap;
	}
	
	/**
	 * This method return sum of all the factory equipment cost(Manufacturer)
	 * from QuotationModelAccessory for a given qmdId.
	 * 
	 * @param qmdId
	 */
	
	public BigDecimal getFactoryEquipmentCost(QuotationModel quotationModel) {
		BigDecimal cost = BigDecimal.ZERO;
		
		for (QuotationModelAccessory quotationModelAccessory : quotationModel.getQuotationModelAccessories()) {
			if (quotationModelAccessory != null && quotationModelAccessory.getDiscPrice() != null) {
				cost = cost.add(quotationModelAccessory.getDiscPrice());
			}
			if(quotationModelAccessory.getRechargeAmount() != null) {
				cost = cost.subtract(quotationModelAccessory.getRechargeAmount());
			}
			
		}

		return cost;

	}
	

	/**
	 * This method return sum of all the after market equipment cost (Dealer)
	 * from QuotationDealerAccessory for a given qmdId.
	 * 
	 * @param qmdId
	 */

	public BigDecimal getDealerAccessoryCost(QuotationModel quotationModel ) {
		
		BigDecimal cost = BigDecimal.ZERO;
	
		for (QuotationDealerAccessory quotationDealerAccessory : quotationModel.getQuotationDealerAccessories()) {
			if (quotationDealerAccessory != null && quotationDealerAccessory.getDiscPrice() != null) {
				cost = cost.add(quotationDealerAccessory.getDiscPrice());
			}
			if(quotationDealerAccessory.getRechargeAmount() != null) {
				cost = cost.subtract(quotationDealerAccessory.getRechargeAmount());
			}

		}

		return cost;

	}
	
	/**
	 * This method provides detail of equipment cost breakdown for a particular
	 * quotation model. It returns a map of all equipment and their as key value
	 * pair. It also deduct the re-charge amount from total cost ,  if present for any accessory.
	 */
	@Deprecated
	public Map<String, Double> getEquipmentDetail(QuotationModel quotationModel, String equipmentType) throws MalBusinessException {

		Map<String, Double> costDetailMap = new TreeMap<String, Double>();

		if (quotationModel != null) {
			List<QuotationElement> list = quotationModel.getQuotationElements();
					
			for (QuotationElement quotationElement : list) {
			    if (equipmentType.equals(MalConstants.FACTORY_EQUIPMENT_INV)) {

				QuotationModelAccessory quotationModelAccessory = quotationElement.getQuotationModelAccessory();
				if (quotationModelAccessory != null){
				    
				    Double totalPrice = 0.0;
				    Double rechargeAmount = 0.0;
				    String key = quotationModelAccessory.getOptionalAccessory().getAccessoryCode().getDescription();
				
				    if (quotationModelAccessory.getDiscPrice() != null) {
					   totalPrice = quotationModelAccessory.getDiscPrice().doubleValue();
				    }
				    //For RC-32
				    if (quotationModelAccessory.getRechargeAmount() != null) {						
					   rechargeAmount = quotationModelAccessory.getRechargeAmount().doubleValue();
					   costDetailMap.put(key+"_rechargeAmt", rechargeAmount);
				    } //End RC-32					
				    Double accessoryCost  = totalPrice - rechargeAmount;
				    costDetailMap.put(key, accessoryCost);
				}
				

			}  else if (equipmentType.equals(MalConstants.AFTER_MARKET_DEALER_INV)) {

					QuotationDealerAccessory quotationDealerAccessory = quotationElement.getQuotationDealerAccessory();

					if (quotationDealerAccessory != null){
					    
					    Double totalPrice = 0.0;
					    Double rechargeAmount = 0.0;
					    String key = quotationDealerAccessory.getDealerAccessory().getDealerAccessoryCode().getDescription();
					
					    if (quotationDealerAccessory.getDiscPrice() != null) {
						  totalPrice = quotationDealerAccessory.getDiscPrice().doubleValue();
					    }
					    //For RC-32
					    if (quotationDealerAccessory.getRechargeAmount() != null) {						
						   rechargeAmount = quotationDealerAccessory.getRechargeAmount().doubleValue();
						   costDetailMap.put(key+"_rechargeAmt", rechargeAmount);
					    }	//End RC-32				
					    Double accessoryCost  = totalPrice - rechargeAmount;
					    costDetailMap.put(key, accessoryCost);
					}

				}
			}
		}
		return costDetailMap;
	}

	@Override
	public QuoteVO populatePODetails(QuotationModel quotationModel, QuoteVO quoteVO) {
		
		 List<QuoteCostElementVO> elementList =  quoteVO.getCostElements();
			
		List<Doc> docList  = docDAO.findInvoiceByExtIdAndDocStatusAndDocTypeAndSourceCode(quotationModel.getQmdId(), "R", "PORDER", "FLQUOTE");
		ExternalAccount parenetDocAccount = null;
		if(docList.size() > 0){
			Doc doc = docList.get(0);
			for (QuoteCostElementVO elementVO : elementList) {
				if(elementVO.getElementName().equals(NonCapitalElementEnum.BASE_VEHICLE_ELEMENT.getElementName())
						|| elementVO.isModelAccessories()){
					
					elementVO.setPoNumber(doc.getDocNo());
					elementVO.setPoRevNo(doc.getRevNo());
					elementVO.setPoOrderDate(doc.getPostedDate());
					if (parenetDocAccount == null && doc.getEaCId() != null && doc.getAccountType() != null &&  doc.getAccountCode() != null) {
						ExternalAccountPK extPK = new ExternalAccountPK(doc.getEaCId(),doc.getAccountType(),doc.getAccountCode());
						parenetDocAccount = externalAccountDAO.findById(extPK).orElse(null);
					}
					elementVO.setAccountInfo(parenetDocAccount.getExternalAccountPK().getAccountCode() + " " + parenetDocAccount.getAccountName());
				}
			}
			
			for (Docl docl : doc.getDocls()) {
				for (QuoteCostElementVO elementVO : elementList) {
				 if(elementVO.isCapitalElements() || elementVO.isDealerAccessories()){
						if(docl.getGenericExtId().equals(elementVO.getElementId()))	{
							elementVO.setPoNumber(doc.getDocNo());
							elementVO.setPoRevNo(doc.getRevNo());
							elementVO.setPoOrderDate(doc.getPostedDate());	
							if (parenetDocAccount == null && doc.getEaCId() != null && doc.getAccountType() != null &&  doc.getAccountCode() != null) {
								ExternalAccountPK extPK = new ExternalAccountPK(doc.getEaCId(),doc.getAccountType(),doc.getAccountCode());
								parenetDocAccount = externalAccountDAO.findById(extPK).orElse(null);		
							}
							elementVO.setAccountInfo(parenetDocAccount.getExternalAccountPK().getAccountCode() + " " + parenetDocAccount.getAccountName());
						}
					}
				}
			}
			
			List<Long> listDocId =  new ArrayList<Long>();
			List<DocLink> list = docLinkDao.findByParentDocId(doc.getDocId());
			for (DocLink docLink : list) {
				listDocId.add(docLink.getId().getChildDocId());				
			}
			
			
			if(listDocId.size() > 0){
				
			 List<Doc> thirdPartyDocs = docDAO.findInvoiceByDocIdAndDocStatusAndDocTypeAndSourceCode("R", "PORDER", "FLQUOTE", listDocId);
				for (Doc thirdPartyDoc : thirdPartyDocs) {
					ExternalAccount thirdPartyDocAccount = null;
					for (Docl docl : thirdPartyDoc.getDocls()) {
						Long extId = docl.getGenericExtId();
						for (QuoteCostElementVO elementVO : elementList) {
							boolean matchFound = false;
							if((elementVO.isDealerAccessories() && extId.equals(elementVO.getElementId()))){
								matchFound =  true;
							}else if(elementVO.isCapitalElements()){
								if(docl.getGenericExtId().equals(elementVO.getElementId()))	{
										matchFound =  true;
								}			
							}	
							
							if(matchFound){
								elementVO.setPoNumber(thirdPartyDoc.getDocNo());
								elementVO.setPoRevNo(thirdPartyDoc.getRevNo());
								elementVO.setPoOrderDate(thirdPartyDoc.getPostedDate());
								if (thirdPartyDocAccount == null && thirdPartyDoc.getEaCId() != null && thirdPartyDoc.getAccountType() != null &&  thirdPartyDoc.getAccountCode() != null) {
									ExternalAccountPK extPK = new ExternalAccountPK(thirdPartyDoc.getEaCId().longValue(),thirdPartyDoc.getAccountType(),thirdPartyDoc.getAccountCode());
									thirdPartyDocAccount = externalAccountDAO.findById(extPK).orElse(null);	
								}
								elementVO.setAccountInfo(thirdPartyDocAccount.getExternalAccountPK().getAccountCode() + " " + thirdPartyDocAccount.getAccountName());
								break;
							}
						}
					}
				}
			}
		}
		return quoteVO;
	}

	@Override
	public CapitalCostModeValuesVO getModeValues(QuotationModel quotationModel) {
		CapitalCostModeValuesVO capitalCostModeValuesVO = new CapitalCostModeValuesVO();
		capitalCostModeValuesVO.setIsStockOrder(false);
		
		try {
			if(quotationService.isStandardQuoteModel(quotationModel.getQmdId())) {
				capitalCostModeValuesVO.setStandardOrderQuoteModel(quotationModel);
				capitalCostModeValuesVO.setMode(CapitalCostService.STANDARD_ORDER_MODE);
				return capitalCostModeValuesVO;
			}
			if(quotationModel.getUnitNo() != null) {
				Long finalizeQmd  = quotationService.getFinalizeQmd(quotationModel.getQmdId());				
				if(finalizeQmd != null) {
					QuotationModel finalizeQuotationModel  = quotationService.getQuotationModelWithCostAndAccessories(finalizeQmd);
					capitalCostModeValuesVO.setFinalQuoteModel(finalizeQuotationModel);
					if(finalizeQuotationModel.getOriginalQmdId() != null) {
						capitalCostModeValuesVO.setFirstQuoteModel(quotationService.getQuotationModelWithCostAndAccessories(finalizeQuotationModel.getOriginalQmdId()));							
					} else {
						capitalCostModeValuesVO.setFirstQuoteModel(finalizeQuotationModel);
						capitalCostModeValuesVO.setIsStockOrder(true);
					}
					capitalCostModeValuesVO.setUnitNo(finalizeQuotationModel.getUnitNo());
					capitalCostModeValuesVO.setMode(CapitalCostService.FINALIZED_MODE);
				} else {
					capitalCostModeValuesVO.setFirstQuoteModel(quotationModel);
					capitalCostModeValuesVO.setMode(CapitalCostService.FIRST_MODE);
					capitalCostModeValuesVO.setUnitNo(quotationModel.getUnitNo());
				} 
				
			} else {
				capitalCostModeValuesVO.setFirstQuoteModel(quotationModel);
				capitalCostModeValuesVO.setMode(CapitalCostService.FIRST_MODE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	return capitalCostModeValuesVO;
	}

	/**
	 * This method is responsible for determining the "status" of a quote model 
	 * and based upon this "status" taking the steps needed to create a fully populated
	 * list of capital costs for the quote.
	 */
	@Override
	public QuoteVO resolveAndCalcCapitalCosts(QuotationModel quotationModel) throws MalBusinessException {
		CapitalCostModeValuesVO modeValuesVO = capitalCostService.getModeValues(quotationModel);
    	String mode = modeValuesVO.getMode();
		
    	QuoteVO targetQuote = null;
    	if (mode.equals(CapitalCostService.STANDARD_ORDER_MODE)) {
    	    targetQuote = capitalCostService.getQuoteCapitalCosts(modeValuesVO.getStandardOrderQuoteModel(), false, null, modeValuesVO.getIsStockOrder());
    	} else if (mode.equals(CapitalCostService.FIRST_MODE)) {
    	    targetQuote = capitalCostService.getQuoteCapitalCosts(modeValuesVO.getFirstQuoteModel(), false, null, modeValuesVO.getIsStockOrder());
    	} else if (mode.equals(CapitalCostService.FINALIZED_MODE)) {
    		if(quotationService.isFormalExtension(quotationModel)) {
    			targetQuote = capitalCostService.getQuoteCapitalCosts(quotationModel, false, null, modeValuesVO.getIsStockOrder());
    		} else {
        	    QuoteVO priorQuote = capitalCostService.getQuoteCapitalCosts(modeValuesVO.getFirstQuoteModel(), false, null, modeValuesVO.getIsStockOrder());
        	    targetQuote = capitalCostService.getQuoteCapitalCosts(modeValuesVO.getFinalQuoteModel(), true, priorQuote,modeValuesVO.getIsStockOrder());
    		}
    	}
    	return targetQuote;
	}

	
}
