package com.mikealbert.vision.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.common.MalConstants;
import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.dao.ContractLineDAO;
import com.mikealbert.data.dao.DoclDAO;
import com.mikealbert.data.dao.MalCapitalCostDAO;
import com.mikealbert.data.dao.QuotationCapitalElementDAO;
import com.mikealbert.data.dao.QuotationDealerAccessoryDAO;
import com.mikealbert.data.dao.QuotationModelAccessoryDAO;
import com.mikealbert.data.dao.QuotationModelDAO;
import com.mikealbert.data.entity.Docl;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.MalCapitalCost;
import com.mikealbert.data.entity.QuotationCapitalElement;
import com.mikealbert.data.entity.QuotationDealerAccessory;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.QuotationModelAccessory;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;
import com.mikealbert.service.CapitalCostService;
import com.mikealbert.service.ContractService;
import com.mikealbert.service.FleetMasterService;
import com.mikealbert.service.QuotationService;
import com.mikealbert.service.vo.CapitalCostModeValuesVO;
import com.mikealbert.service.vo.QuoteCost;
import com.mikealbert.service.vo.QuoteCostElementVO;
import com.mikealbert.service.vo.QuoteVO;
import com.mikealbert.vision.vo.CapitalCostsRowVO;
import com.mikealbert.vision.vo.InvoiceLineVO;

@Service("capitalCostOverviewService")
@Transactional(readOnly = true)
public class CapitalCostOverviewServiceImpl implements CapitalCostOverviewService{
	
	private MalLogger logger = MalLoggerFactory.getLogger(this.getClass());
	
	@Resource private CapitalCostService capitalCostService;
	@Resource private QuotationService quotationService;
	@Resource private FleetMasterService fleetMasterService;
	@Resource private ContractService contractService;
	@Resource QuotationModelDAO quotationModelDAO;	
	@Resource MalCapitalCostDAO malCapitalCostDAO;
	@Resource  InvoiceEntryService invoiceEntryService;
	@Resource DoclDAO doclDAO;
	@Resource QuotationModelAccessoryDAO modelAccessoryDAO;
	@Resource QuotationDealerAccessoryDAO dealerAccessoryDAO1;
	@Resource QuotationCapitalElementDAO quotationCapitalElementDAO;
	@Resource ContractLineDAO contractLineDAO;
	
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
			logger.error(ex);
			throw new MalBusinessException("generic.error.occured.while", new String[] { "getting total cost for quote" }, ex);
		}
		
	}
	
	@Override
	public QuoteCost getQuoteCost2(QuotationModel qm)
			throws MalBusinessException {
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
				CapitalCostModeValuesVO modeValuesVO = capitalCostService.getModeValues(qm);
	        	String mode = modeValuesVO.getMode();
	        	quoteCost.setCustomerCost(BigDecimal.ZERO);
	        	quoteCost.setDealCost(BigDecimal.ZERO);
	        	
	        	QuoteVO targetQuote = null;
	        	if (mode.equals(CapitalCostService.STANDARD_ORDER_MODE)) {
	        	    targetQuote = capitalCostService.getQuoteCapitalCosts2(modeValuesVO.getStandardOrderQuoteModel(), false, null, modeValuesVO.getIsStockOrder());
	        	} else if (mode.equals(CapitalCostService.FIRST_MODE)) {
	        	    targetQuote = capitalCostService.getQuoteCapitalCosts2(modeValuesVO.getFirstQuoteModel(), false, null, modeValuesVO.getIsStockOrder());
	        	} else if (mode.equals(CapitalCostService.FINALIZED_MODE)) {
	        		if(quotationService.isFormalExtension(qm)) {
	        			targetQuote = capitalCostService.getQuoteCapitalCosts2(qm, false, null, modeValuesVO.getIsStockOrder());
	        		} else {
	            	    QuoteVO priorQuote = capitalCostService.getQuoteCapitalCosts2(modeValuesVO.getFirstQuoteModel(), false, null, modeValuesVO.getIsStockOrder());
	            	    targetQuote = capitalCostService.getQuoteCapitalCosts2(modeValuesVO.getFinalQuoteModel(), true, priorQuote,modeValuesVO.getIsStockOrder());
	        		}
	        	}
	        
	        	if (targetQuote != null) {        
	        	    List<QuoteCostElementVO> costElements = targetQuote.getCostElements();
	        		
	        	    for (QuoteCostElementVO element : costElements) {
	        	    	quoteCost.setDealCost(quoteCost.getDealCost().add(element.getDealCost()));
	        	    	quoteCost.setCustomerCost(quoteCost.getCustomerCost().add(element.getCustomerCost()));
	        	    }
	        	}
	        return quoteCost;
			}
		} catch (Exception ex) {
			logger.error(ex);
			throw new MalBusinessException("generic.error.occured.while", new String[] { "getting total cost for quote" }, ex);
		}
	}

	public List<MalCapitalCost>   getMalCapitalCostByDoc(Long  invoiceHeaderId){		
		return malCapitalCostDAO.findMalCapitalCostByDoc(invoiceHeaderId);
	}	
	
	public List<MalCapitalCost>   getMalCapitalCostByFinalizeQuote(Long  qmdId){		
		return malCapitalCostDAO.findMalCapitalCostByFinalizeQuote(qmdId);
	}
	
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void saveCapitalCost(QuotationModel finalizeQuotationModel ,  Long invoiceHeaderId , List<CapitalCostsRowVO> capitalCostsRowVOList,  boolean finalizeMode ) throws MalBusinessException ,MalException{
			
			if(finalizeMode == false){
				invoiceEntryService.updateInvoiceLineItems(getInvoiceLineVOForUpdate(capitalCostsRowVOList ,invoiceHeaderId));
				malCapitalCostDAO.saveAll(getMALFinalizedCostVO(finalizeQuotationModel ,capitalCostsRowVOList ,invoiceHeaderId));
			}
			
			modelAccessoryDAO.saveAll(getFactoryAccessoriesForUpdate(finalizeQuotationModel, capitalCostsRowVOList));
			dealerAccessoryDAO1.saveAll(getDealerAccessoriesForUpdate(finalizeQuotationModel,  capitalCostsRowVOList));
			quotationCapitalElementDAO.saveAll(getQuotationCapitalElementForUpdate(finalizeQuotationModel, capitalCostsRowVOList));
			quotationModelDAO.save(getQuotationModelForUpdate(finalizeQuotationModel, capitalCostsRowVOList));
			
		
	}
	
private List<InvoiceLineVO>  getInvoiceLineVOForUpdate(List<CapitalCostsRowVO> capitalCostsRowVOList, Long invoiceHeaderId){
		
		List<InvoiceLineVO> invoiceLineVOList =  new ArrayList<InvoiceLineVO>();
		List<Docl> invoiceHeaderDocls =  doclDAO.findByDocId(invoiceHeaderId);
		for (CapitalCostsRowVO capitalCostsRowVO : capitalCostsRowVOList) {
			if(capitalCostsRowVO.getIsFooter())
				continue;
			
				for (Docl docl : invoiceHeaderDocls) {
					
					InvoiceLineVO invoiceLineVO  = null;
					if(capitalCostsRowVO.getId() != null ){	
						if(docl.getGenericExtId().longValue() == capitalCostsRowVO.getId().longValue()
								&& ( docl.getUserDef4().equals("CAPITAL")  
										||docl.getUserDef4().equals("FACTORY") 
										||docl.getUserDef4().equals("DEALER") )){
						
								invoiceLineVO  = new InvoiceLineVO();						
								invoiceLineVO.setDocId(invoiceHeaderId);
								invoiceLineVO.setLineCost(capitalCostsRowVO.getInvoiceCost());
								invoiceLineVO.setReclaimable(capitalCostsRowVO.isPossibleReclaim());
								invoiceLineVO.setReclaimLineId(capitalCostsRowVO.getReclaimLineId());
								invoiceLineVO.setLineId(docl.getId().getLineId());
						
						}
					}else if(capitalCostsRowVO.getName().equals("Base Vehicle") && docl.getUserDef4().equals("MODEL")){

						invoiceLineVO  = new InvoiceLineVO();						
						invoiceLineVO.setDocId(invoiceHeaderId);
						invoiceLineVO.setLineCost(capitalCostsRowVO.getInvoiceCost());
						invoiceLineVO.setReclaimable(capitalCostsRowVO.isPossibleReclaim());
						invoiceLineVO.setReclaimLineId(capitalCostsRowVO.getReclaimLineId());
						invoiceLineVO.setLineId(docl.getId().getLineId());
					}
					
					if(invoiceLineVO != null)
						invoiceLineVOList.add(invoiceLineVO);
				}
			}
			
		return invoiceLineVOList;
	}
	
	private List<MalCapitalCost>  getMALFinalizedCostVO(QuotationModel finalizeQuotationModel , List<CapitalCostsRowVO> capitalCostsRowVOList, Long invoiceHeaderId ){ 
		
		FleetMaster fleetMaster = fleetMasterService.findByUnitNo(finalizeQuotationModel.getUnitNo());
		
		List<MalCapitalCost> malCapitalCostList =  malCapitalCostDAO.findMalCapitalCostByFinalizeQuote(finalizeQuotationModel.getQmdId());;
		List<MalCapitalCost> alllCapitalCostList =  new ArrayList<MalCapitalCost>();
		alllCapitalCostList.addAll(malCapitalCostList);
		
		for (CapitalCostsRowVO capitalCostsRowVO : capitalCostsRowVOList) {
			if(capitalCostsRowVO.getIsFooter() || capitalCostsRowVO.getName().equals("Capital Contribution"))
				continue;
			BigDecimal cost  = capitalCostsRowVO.getFinalizedQuoteDealCost();	
			boolean alreadyExistCapElement = false;
			for (MalCapitalCost malCapitalCost : malCapitalCostList) {
				
				if(capitalCostsRowVO.getId() != null){
						if(malCapitalCost.getElementId().longValue() == capitalCostsRowVO.getId().longValue()){
							//	&& cost.compareTo(malCapitalCost.getTotalPrice()) != 0 ){
							
							malCapitalCost.setTotalPrice(cost);
							malCapitalCost.setUnitCost(cost);
							malCapitalCost.setUnitPrice(cost);
							alreadyExistCapElement = true;
						}
					}else{
						
						if(capitalCostsRowVO.getName().equals("Base Vehicle") && malCapitalCost.getElementType().equals("MODEL")){
								//&&	cost.compareTo(malCapitalCost.getTotalPrice()) != 0 ){
							malCapitalCost.setTotalPrice(cost);
							malCapitalCost.setUnitCost(cost);
							malCapitalCost.setUnitPrice(cost);
							alreadyExistCapElement = true;
						}
				}
			}
			if(alreadyExistCapElement == false){
				
				MalCapitalCost malCapitalCost =  new MalCapitalCost();
				
				malCapitalCost.setElementId(capitalCostsRowVO.getId());
				malCapitalCost.setFmsFmsId(fleetMaster.getFmsId());
				malCapitalCost.setQmdQmdId(finalizeQuotationModel.getQmdId());
				malCapitalCost.setUnitCost(capitalCostsRowVO.getFinalizedQuoteDealCost());
				malCapitalCost.setUnitPrice(capitalCostsRowVO.getFinalizedQuoteDealCost());
				malCapitalCost.setUnitDisc(capitalCostsRowVO.getFinalizedQuoteDealCost());
				malCapitalCost.setTotalPrice(capitalCostsRowVO.getFinalizedQuoteDealCost());
				
				if(capitalCostsRowVO.isModelAccessories()){
					malCapitalCost.setElementType("FACTORY");
				}else if(capitalCostsRowVO.isDealerAccessories()){
					malCapitalCost.setElementType("DEALER");
				}else if(capitalCostsRowVO.isCapitalElement()){
					malCapitalCost.setElementType("CAPITAL");
				}else if(capitalCostsRowVO.getName().equals("Base Vehicle") ){
					malCapitalCost.setElementType("MODEL");
					malCapitalCost.setElementId(finalizeQuotationModel.getModel().getModelId());
				}
					
			
				
				alllCapitalCostList.add(malCapitalCost);
			}
		}
		
		return alllCapitalCostList;
	}
	
	private List<QuotationModelAccessory>  getFactoryAccessoriesForUpdate(QuotationModel finalizeQuotationModel, List<CapitalCostsRowVO> capitalCostsRowVOList ){
		
		
		for (CapitalCostsRowVO capitalCostsRowVO : capitalCostsRowVOList) {
			if(capitalCostsRowVO.getId() == null ||  capitalCostsRowVO.getIsFooter())
				continue;
			
			 for (QuotationModelAccessory modelAccessory : finalizeQuotationModel.getQuotationModelAccessories()) {
				
				 if(modelAccessory.getOptionalAccessory().getOacId().longValue() == capitalCostsRowVO.getId().longValue()){
					 modelAccessory.setRechargeAmount(capitalCostsRowVO.getRechargeAmt());	
					 modelAccessory.setDiscPrice(capitalCostsRowVO.getFinalizedQuoteCustomerCost().add(capitalCostsRowVO.getRechargeAmt()));	
					 modelAccessory.setTotalPrice(modelAccessory.getDiscPrice().subtract(modelAccessory.getRechargeAmount()));
					 modelAccessory.setBasicPrice(modelAccessory.getDiscPrice());
				 }
				 
				 if(modelAccessory.getBasicPrice().compareTo(BigDecimal.ZERO) != 0 ){
					 modelAccessory.setFreeOfChargeYn(MalConstants.FLAG_N);
				 }else{
					 modelAccessory.setFreeOfChargeYn(MalConstants.FLAG_Y);
				 }
			 }
			 
		}
			
		return finalizeQuotationModel.getQuotationModelAccessories();
	}
	
	private List<QuotationDealerAccessory>  getDealerAccessoriesForUpdate(QuotationModel finalizeQuotationModel , List<CapitalCostsRowVO> capitalCostsRowVOList ){
		
	
		for (CapitalCostsRowVO capitalCostsRowVO : capitalCostsRowVOList) {
			if(capitalCostsRowVO.getId() == null ||  capitalCostsRowVO.getIsFooter())
				continue;
			
			 for (QuotationDealerAccessory dealerAccessory : finalizeQuotationModel.getQuotationDealerAccessories()) {
				 if(dealerAccessory.getDealerAccessory().getDacId().longValue() == capitalCostsRowVO.getId().longValue()){
					 dealerAccessory.setRechargeAmount(capitalCostsRowVO.getRechargeAmt());	
					 dealerAccessory.setDiscPrice(capitalCostsRowVO.getFinalizedQuoteCustomerCost().add(capitalCostsRowVO.getRechargeAmt()));	
					 dealerAccessory.setTotalPrice(dealerAccessory.getDiscPrice().subtract(dealerAccessory.getRechargeAmount()));
					 dealerAccessory.setBasicPrice(dealerAccessory.getDiscPrice());
				 }
			 
				 if(dealerAccessory.getBasicPrice().compareTo(BigDecimal.ZERO) != 0 ){
					 dealerAccessory.setFreeOfChargeYn(MalConstants.FLAG_N);
				 }else{
					 dealerAccessory.setFreeOfChargeYn(MalConstants.FLAG_Y);
				 }
			 }
		}
			
		return finalizeQuotationModel.getQuotationDealerAccessories();
	}
	
	private List<QuotationCapitalElement>  getQuotationCapitalElementForUpdate(QuotationModel finalizeQuotationModel , List<CapitalCostsRowVO> capitalCostsRowVOList ){
		
	
		for (CapitalCostsRowVO capitalCostsRowVO : capitalCostsRowVOList) {
			if(capitalCostsRowVO.getId() == null ||  capitalCostsRowVO.getIsFooter())
				continue;
			
			 for (QuotationCapitalElement quotationCapitalElement : finalizeQuotationModel.getQuotationCapitalElements()) {
				 if(quotationCapitalElement.getCapitalElement().getCelId() == capitalCostsRowVO.getId().longValue()){
					 quotationCapitalElement.setValue(capitalCostsRowVO.getFinalizedQuoteCustomerCost());	
				 }
			 }
			 
			}
			
		return finalizeQuotationModel.getQuotationCapitalElements();
	}
	
	private QuotationModel getQuotationModelForUpdate(QuotationModel finalizeQuotationModel, List<CapitalCostsRowVO> capitalCostsRowVOList) {

		for (CapitalCostsRowVO capitalCostsRowVO : capitalCostsRowVOList) {
			if (capitalCostsRowVO.getIsFooter())
				continue;

			if (capitalCostsRowVO.getName().equals("Base Vehicle")
					&& capitalCostsRowVO.getFinalizedQuoteCustomerCost().compareTo(finalizeQuotationModel.getBasePrice()) != 0) {
				finalizeQuotationModel.setBasePrice(capitalCostsRowVO.getFinalizedQuoteCustomerCost());
			} else if (capitalCostsRowVO.getName().equals("Capital Contribution")) {
				BigDecimal ccInFinal = finalizeQuotationModel.getCapitalContribution() != null ? finalizeQuotationModel
						.getCapitalContribution() : BigDecimal.ZERO;
				if (capitalCostsRowVO.getFinalizedQuoteCustomerCost().compareTo(ccInFinal) != 0) {
					finalizeQuotationModel.setCapitalContribution(capitalCostsRowVO.getFinalizedQuoteCustomerCost().abs());

				}

			}

		}

		return finalizeQuotationModel;
	}	
}
