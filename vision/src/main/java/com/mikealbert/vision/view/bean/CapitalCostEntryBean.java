package com.mikealbert.vision.view.bean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mikealbert.common.MalConstants;
import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.entity.QuotationCapitalElement;
import com.mikealbert.data.entity.QuotationElement;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.CapitalCostService;
import com.mikealbert.service.QuotationService;
import com.mikealbert.service.RentalCalculationService;
import com.mikealbert.vision.view.ViewConstants;
import com.mikealbert.vision.vo.CapitalCostEntryVO;

@Component
@Scope("view")
public class CapitalCostEntryBean extends StatefulBaseBean {
	private static final long serialVersionUID = -8806821952041781659L;
	MalLogger logger = MalLoggerFactory.getLogger(this.getClass());

	@Resource QuotationService quotationService;
	@Resource CapitalCostService capitalCostService;
	@Resource RentalCalculationService rentalCalculationService;
	
	private static final BigDecimal OneHundredThousand = new BigDecimal(100000);
	private static final String LEASE_ELEMENT = "1_LEASE";
	
	
	private List<CapitalCostEntryVO> rowList;
	private List<QuotationCapitalElement> initialList;
	private Long incomingQmdId;
	private String outputQuote;
	private QuotationModel quotationModel;
	private boolean disabledSave;
	private BigDecimal baseCost;
	private BigDecimal totalCost;
	private String unitDescription;
	private BigDecimal factoryEquip;
	private BigDecimal afterMarketEquip;
	private boolean totalCost100k = false;
	private boolean openEndLease = false;
	private boolean preventCostEdit = false;
	private boolean preventBaseEdit = true;
	
	
	@PostConstruct
	public void init() {
	    	initializeDataTable(500, 500, new int[] {65, 15, 15}); 
		super.openPage();
		try {
			if(incomingQmdId != null) {
				quotationModel = quotationService.getQuotationModelWithCostAndAccessories(incomingQmdId);
				if(quotationModel != null) {
					loadData();					
				}
			}
		} catch (Exception ex) {
			logger.error(ex);
			super.addErrorMessage("generic.error", ex.getMessage());
		}
	}

	@Override
	protected void loadNewPage() {

	    	thisPage.setPageDisplayName("Edit Capital Costs");
		thisPage.setPageUrl(ViewConstants.CAPITAL_COST_ENTRY);

		if(thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_QUOTE_MODEL_ID) != null){
		    	incomingQmdId = (Long)thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_QUOTE_MODEL_ID);			
		} else {
			String paramQmdId = getRequestParameter("qmdId");
			if (paramQmdId != null)
				incomingQmdId = Long.valueOf(paramQmdId);
		}
	}

	@Override
	protected void restoreOldPage() {}
	
	public void loadData() {
		setOpenEndFlag();
		setCostEditFlag();
		setBaseEditFlag();
		outputQuote = getFormattedQuote();
		disabledSave = true;		
		try {
			loadRows();
		} catch (Exception e) {
			logger.error(e);
			 if(e  instanceof MalBusinessException){				 
				 super.addErrorMessage(e.getMessage()); 
			 }else{
				 super.addErrorMessage("generic.error.occured.while", " building screen.");
			 }
		}
	}

	
	private String getFormattedQuote() {
		return String.valueOf(quotationModel.getQuotation().getQuoId())+ "/" + 
							  quotationModel.getQuoteNo() + "/" + 
							  quotationModel.getRevisionNo();
	}
	
	private void loadRows() throws Exception {

		unitDescription = quotationModel.getModel().getModelDescription();
		baseCost = quotationModel.getBasePrice() == null ? BigDecimal.ZERO: quotationModel.getBasePrice();
		loadEquipmentCost();
		
		initialList = quotationModel.getQuotationCapitalElements();
		rowList = new ArrayList<CapitalCostEntryVO>();
		
		for(QuotationCapitalElement ce : initialList) {
			CapitalCostEntryVO c = new CapitalCostEntryVO();
			c.setQuotationCapitalElement(ce);
			c.setValue(ce.getValue());
			c.setOriginalValue(ce.getValue());
			c.setFinalClientCostFlag(calcFinalClientCostFlag(ce));
			rowList.add(c);
		}
		Collections.sort(rowList, capitalCostEntryVOComparator);

		totalCost = getCalculatedTotal();
		if(totalCost.compareTo(OneHundredThousand) < 0) {
			totalCost100k = false;
		} else {
			totalCost100k = true;
		}
	}
	
	private boolean calcFinalClientCostFlag(QuotationCapitalElement qce) {
		if(qce.getOnInvoice() != null) {
			if(qce.getOnInvoice().equalsIgnoreCase("Y")) {
				return true;
			} else {
				return false;
			}
		} else {
			if(qce.getCapitalElement().getOnInvoice().equalsIgnoreCase("Y")) {
				return true;
			} else {
				return false;
			}
		}
	}

	private void setOpenEndFlag() {
		if(quotationService.getLeaseType(incomingQmdId).equals(QuotationService.OPEN_END_LEASE) ) {
			openEndLease = true;
		} else {
			openEndLease = false;
		}
	}

	private void setCostEditFlag() {
		if(quotationModel.getOrderType().equals(MalConstants.ORDER_TYPE_LOCATE) && rentalCalculationService.isQuoteEditable(quotationModel)) {
			preventCostEdit = false;
		} else {
			preventCostEdit = true;
		}
	}
	
        private void setBaseEditFlag() {
    
        	if (rentalCalculationService.isQuoteEditable(quotationModel)) {
        	    if (rentalCalculationService.isUsedStock(quotationModel) || rentalCalculationService.isFormalExtension(quotationModel)) {
        		if (!quotationService.getLeaseType(incomingQmdId).equals(QuotationService.MAX_FLEET_SERVICES))
        		    preventBaseEdit = false;
        
        	    }
        	}
        }

	
    public void onCellEdit() {   
		totalCost = getCalculatedTotal();
		if(totalCost.compareTo(OneHundredThousand) < 0) {
			totalCost100k = false;
		} else {
			totalCost100k = true;
		}
		disabledSave = false;
    }

	private BigDecimal getCalculatedTotal() {
		BigDecimal total = baseCost == null ? BigDecimal.ZERO : baseCost ;
		total = total.add(factoryEquip);
		total = total.add(afterMarketEquip);
		for(CapitalCostEntryVO c : rowList) {
			if(c.getValue() != null) {
				total = total.add(c.getValue());				
			}
		}
		return total;
	}
	
	private void loadEquipmentCost() {
		factoryEquip = capitalCostService.getFactoryEquipmentCost(quotationModel);
		afterMarketEquip = capitalCostService.getDealerAccessoryCost(quotationModel);
	}
	
	public String cancel(){
	    return super.cancelPage();      	
	}

	public String save() {
		boolean costsChanged = false;
		boolean costErrors = false;
		boolean quoteError = false;
		boolean baseChanged = false;
		
		for (CapitalCostEntryVO capitalCostEntryVO : rowList) {
			if (capitalCostEntryVO.getValue() != null) {
				if (capitalCostEntryVO.getOriginalValue().compareTo(capitalCostEntryVO.getValue()) != 0) {
					updateQuotationCapitalElementCost(capitalCostEntryVO);
					costsChanged = true;

				}
			} else {
				if (capitalCostEntryVO.getOriginalValue().compareTo(BigDecimal.ZERO) != 0) {
					capitalCostEntryVO.setValue(BigDecimal.ZERO);
					updateQuotationCapitalElementCost(capitalCostEntryVO);
					costsChanged = true;
				}
			}
		}
		if (costsChanged) {
			quotationModel.setReCalcNeeded("Y");

		}
		BigDecimal oldBaseCost = quotationModel.getBasePrice() == null ? BigDecimal.ZERO : quotationModel.getBasePrice();
		baseCost = baseCost == null ? BigDecimal.ZERO : baseCost;
		if (baseCost.compareTo(oldBaseCost) != 0) {
			quotationModel.setReCalcNeeded("Y");
			quotationModel.setBasePrice(baseCost);
			quotationModel.setQuoteCapital(baseCost);
			quotationModel.setTotalPrice(baseCost);
			for (QuotationElement qe : quotationModel.getQuotationElements()) {
				if (qe.getLeaseElement().getCategoryCode().equalsIgnoreCase(LEASE_ELEMENT)) {
					qe.setCapitalCost(baseCost);
					break;
				}
			}
			baseChanged = true;
		}
		if (quotationModel.getReCalcNeeded().equalsIgnoreCase("Y")) {
			if (baseChanged) {
				super.addSuccessMessage("process.success", "Base Cost saved - ");
			}
		}
		try {
			rentalCalculationService.saveCapitalCostChanges(quotationModel);
			if (costsChanged) {
				super.addSuccessMessage("process.success","Capital Cost Values saved - ");
			}
			if (baseChanged) {
				super.addSuccessMessage("process.success", "Base Cost saved - ");
			}
			quoteError = false;
			
		} catch (Exception ex) {
			handleException("generic.error", new String[] { ex.getMessage() },ex, null);
			quoteError = true;
		}
		if ((!costErrors) && (!quoteError)) {
			return super.cancelPage();
		}

		return null;

	}
	private QuotationCapitalElement	updateQuotationCapitalElementCost(CapitalCostEntryVO capitalCostEntryVO){
		QuotationCapitalElement qce = capitalCostEntryVO.getQuotationCapitalElement();
    	BigDecimal value = scrubValue(capitalCostEntryVO.getValue());
		qce.setValue(value);
		return qce;
	}
   
    
	private BigDecimal scrubValue(BigDecimal value) {
		return value.setScale(2, BigDecimal.ROUND_HALF_UP);
	}
	
	
	Comparator<CapitalCostEntryVO> capitalCostEntryVOComparator = new Comparator<CapitalCostEntryVO>() {
		public int compare(CapitalCostEntryVO r1, CapitalCostEntryVO r2) {
			String s1 = r1.getQuotationCapitalElement().getCapitalElement().getDescription();
			String s2 = r2.getQuotationCapitalElement().getCapitalElement().getDescription();
			return s1.compareTo(s2);
		}
	};
		
	public String getOutputQuote() {
		return outputQuote;
	}

	public void setOutputQuote(String outputQuote) {
		this.outputQuote = outputQuote;
	}

	public QuotationModel getQuotationModel() {
		return quotationModel;
	}

	public void setQuotationModel(QuotationModel quotationModel) {
		this.quotationModel = quotationModel;
	}

	public boolean getDisabledSave() {
		return disabledSave;
	}

	public void setDisabledSave(boolean disabledSave) {
		this.disabledSave = disabledSave;
	}

	public List<CapitalCostEntryVO> getRowList() {
		return rowList;
	}

	public void setRowList(List<CapitalCostEntryVO> rowList) {
		this.rowList = rowList;
	}

	public List<QuotationCapitalElement> getInitialList() {
		return initialList;
	}

	public void setInitialList(List<QuotationCapitalElement> initialList) {
		this.initialList = initialList;
	}

	public BigDecimal getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(BigDecimal totalCost) {
		this.totalCost = totalCost;
	}

	public BigDecimal getBaseCost() {
		return baseCost;
	}

	public void setBaseCost(BigDecimal baseCost) {
		this.baseCost = baseCost;
	}

	public String getUnitDescription() {
		return unitDescription;
	}

	public void setUnitDescription(String unitDescription) {
		this.unitDescription = unitDescription;
	}

	public BigDecimal getFactoryEquip() {
		return factoryEquip;
	}

	public void setFactoryEquip(BigDecimal factoryEquip) {
		this.factoryEquip = factoryEquip;
	}

	public BigDecimal getAfterMarketEquip() {
		return afterMarketEquip;
	}

	public void setAfterMarketEquip(BigDecimal afterMarketEquip) {
		this.afterMarketEquip = afterMarketEquip;
	}

	public boolean isTotalCost100k() {
		return totalCost100k;
	}

	public void setTotalCost100k(boolean totalCost100k) {
		this.totalCost100k = totalCost100k;
	}

	public boolean isOpenEndLease() {
		return openEndLease;
	}

	public void setOpenEndLease(boolean openEndLease) {
		this.openEndLease = openEndLease;
	}

	public boolean isPreventCostEdit() {
		return preventCostEdit;
	}

	public void setPreventCostEdit(boolean preventCostEdit) {
		this.preventCostEdit = preventCostEdit;
	}

	public boolean isPreventBaseEdit() {
		return preventBaseEdit;
	}

	public void setPreventBaseEdit(boolean preventBaseEdit) {
		this.preventBaseEdit = preventBaseEdit;
	}






}