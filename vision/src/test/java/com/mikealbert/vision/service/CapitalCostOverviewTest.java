package com.mikealbert.vision.service;

import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;

import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.CapitalCostService;
import com.mikealbert.service.QuotationService;
import com.mikealbert.service.vo.CapitalCostModeValuesVO;
import com.mikealbert.service.vo.QuoteCost;
import com.mikealbert.service.vo.QuoteCostElementVO;
import com.mikealbert.service.vo.QuoteVO;
import com.mikealbert.testing.BaseTest;
import com.mikealbert.util.MALUtilities;

public class CapitalCostOverviewTest extends BaseTest {
	@Resource CapitalCostOverviewService capitalCostOverviewService;
	@Resource QuotationService quotationService;
	@Resource CapitalCostService capitalCostService;
	
	@Test
	public void getModeValuesForStandardOrderQuote() {
		try {
			
			Long qmdId = 21l;
			QuotationModel quotationModel = quotationService.getQuotationModel(qmdId);
			CapitalCostModeValuesVO modeVO = capitalCostService.getModeValues(quotationModel);
		
			assertTrue(modeVO.getStandardOrderQuoteModel() != null);
			assertTrue(modeVO.getFirstQuoteModel() == null);
			assertTrue(modeVO.getFinalQuoteModel() == null);
			assertTrue(modeVO.getMode().equals(CapitalCostService.STANDARD_ORDER_MODE));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void getModeValuesForFinalizedQuote() {
		try {
			Long qmdId = 1662l;
			QuotationModel quotationModel = quotationService.getQuotationModel(qmdId);
			CapitalCostModeValuesVO modeVO = capitalCostService.getModeValues(quotationModel);
		
			assertTrue(modeVO.getFirstQuoteModel() != null);
			assertTrue(modeVO.getFinalQuoteModel() != null);
			assertTrue(modeVO.getMode().equals(CapitalCostService.FINALIZED_MODE));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//Inventory - QM001 - 438690
	@Test
	public void compareInventoryFactoryQuoteCapitalCosts() throws MalBusinessException{
		Long qmdId = 438690l;
		QuotationModel quotationModel = quotationService.getQuotationModelWithCostAndAccessories(qmdId);
		QuoteCost quote1 = capitalCostOverviewService.getQuoteCost(quotationModel);
		QuoteCost quote2 = capitalCostOverviewService.getQuoteCost2(quotationModel);
		
		Assert.assertEquals(quote1.getCustomerCost(), quote2.getCustomerCost());
		Assert.assertEquals(quote1.getDealCost(), quote2.getDealCost());
		
	}	
	
	//Inventory - QM148 - 438785
	@Test
	public void compareInventoryLocateQuoteCapitalCosts() throws MalBusinessException{
		Long qmdId = 438785l;
		QuotationModel quotationModel = quotationService.getQuotationModelWithCostAndAccessories(qmdId);
		QuoteCost quote1 = capitalCostOverviewService.getQuoteCost(quotationModel);
		QuoteCost quote2 = capitalCostOverviewService.getQuoteCost2(quotationModel);
		
		Assert.assertEquals(quote1.getCustomerCost(), quote2.getCustomerCost());
		Assert.assertEquals(quote1.getDealCost(), quote2.getDealCost());
		
	}	
	
	//Manual Rent - QM001 - 438689
	@Test
	public void compareManualFactoryQuoteCapitalCosts() throws MalBusinessException{
		Long qmdId = 438689l;
		QuotationModel quotationModel = quotationService.getQuotationModelWithCostAndAccessories(qmdId);
		QuoteCost quote1 = capitalCostOverviewService.getQuoteCost(quotationModel);
		QuoteCost quote2 = capitalCostOverviewService.getQuoteCost2(quotationModel);
		
		Assert.assertEquals(quote1.getCustomerCost(), quote2.getCustomerCost());
		Assert.assertEquals(quote1.getDealCost(), quote2.getDealCost());
		
	}
	
	//Manual Rent - QM148 - 438786
	@Test
	public void compareManualLocateQuoteCapitalCosts() throws MalBusinessException{
		Long qmdId = 438786l;
		QuotationModel quotationModel = quotationService.getQuotationModelWithCostAndAccessories(qmdId);
		QuoteCost quote1 = capitalCostOverviewService.getQuoteCost(quotationModel);
		QuoteCost quote2 = capitalCostOverviewService.getQuoteCost2(quotationModel);
		
		Assert.assertEquals(quote1.getCustomerCost(), quote2.getCustomerCost());
		Assert.assertEquals(quote1.getDealCost(), quote2.getDealCost());
		
	}

	//MAX - QM001 - 438688
	@Test
	public void compareMAXFactoryQuoteCapitalCosts() throws MalBusinessException{
		Long qmdId = 438745l;
		QuotationModel quotationModel = quotationService.getQuotationModelWithCostAndAccessories(qmdId);
		QuoteCost quote1 = capitalCostOverviewService.getQuoteCost(quotationModel);
		QuoteCost quote2 = capitalCostOverviewService.getQuoteCost2(quotationModel);
		
		Assert.assertEquals(quote1.getCustomerCost(), quote2.getCustomerCost());
		Assert.assertEquals(quote1.getDealCost(), quote2.getDealCost());
		
	}
	
	//MAX - QM148 - 438745
	@Test
	public void compareMAXLocateQuoteCapitalCosts() throws MalBusinessException{
		Long qmdId = 438745l;
		QuotationModel quotationModel = quotationService.getQuotationModelWithCostAndAccessories(qmdId);
		QuoteCost quote1 = capitalCostOverviewService.getQuoteCost(quotationModel);
		QuoteCost quote2 = capitalCostOverviewService.getQuoteCost2(quotationModel);
		
		Assert.assertEquals(quote1.getCustomerCost(), quote2.getCustomerCost());
		Assert.assertEquals(quote1.getDealCost(), quote2.getDealCost());
		
	}
	
	//AM - QM001 - 438685
	@Test
	public void compareAMFactoryQuoteCapitalCosts() throws MalBusinessException{
		Long qmdId = 438685l;
		QuotationModel quotationModel = quotationService.getQuotationModelWithCostAndAccessories(qmdId);
		QuoteCost quote1 = capitalCostOverviewService.getQuoteCost(quotationModel);
		QuoteCost quote2 = capitalCostOverviewService.getQuoteCost2(quotationModel);
		
		//Assert.assertEquals(quote1.getCustomerCost().doubleValue(), quote2.getCustomerCost().doubleValue());
		Assert.assertEquals(quote1.getDealCost().doubleValue(), quote2.getDealCost().doubleValue(), 300.00);
	}
	
	//Closed End - QM001 - 438686
	@Test
	public void compareCEFactoryQuoteCapitalCosts() throws MalBusinessException{
		Long qmdId = 438686l;
		QuotationModel quotationModel = quotationService.getQuotationModelWithCostAndAccessories(qmdId);
		QuoteCost quote1 = capitalCostOverviewService.getQuoteCost(quotationModel);
		QuoteCost quote2 = capitalCostOverviewService.getQuoteCost2(quotationModel);
		
		Assert.assertEquals(quote1.getCustomerCost(), quote2.getCustomerCost());
		Assert.assertEquals(quote1.getDealCost(), quote2.getDealCost());
		
	}
	
	//Closed End - QM148 - 438787
	@Test
	public void compareCELocateQuoteCapitalCosts() throws MalBusinessException{
		Long qmdId = 438787l;
		QuotationModel quotationModel = quotationService.getQuotationModelWithCostAndAccessories(qmdId);
		QuoteCost quote1 = capitalCostOverviewService.getQuoteCost(quotationModel);
		QuoteCost quote2 = capitalCostOverviewService.getQuoteCost2(quotationModel);
		
		Assert.assertEquals(quote1.getCustomerCost(), quote2.getCustomerCost());
		Assert.assertEquals(quote1.getDealCost(), quote2.getDealCost());
		
	}
	
	
	//Opened End - QM001 - 438687
	@Test
	public void compareOEFactoryQuoteCapitalCosts() throws MalBusinessException{
		Long qmdId = 438687l;
		QuotationModel quotationModel = quotationService.getQuotationModelWithCostAndAccessories(qmdId);
		QuoteCost quote1 = capitalCostOverviewService.getQuoteCost(quotationModel);
		QuoteCost quote2 = capitalCostOverviewService.getQuoteCost2(quotationModel);
		
		Assert.assertEquals(quote1.getCustomerCost(), quote2.getCustomerCost());
		Assert.assertEquals(quote1.getDealCost(), quote2.getDealCost());
		
	}
	
	//Opened End - QM148 - 438765
	@Test
	public void compareOELocateQuoteCapitalCosts() throws MalBusinessException{
		Long qmdId = 438765l;
		QuotationModel quotationModel = quotationService.getQuotationModelWithCostAndAccessories(qmdId);
		QuoteCost quote1 = capitalCostOverviewService.getQuoteCost(quotationModel);
		QuoteCost quote2 = capitalCostOverviewService.getQuoteCost2(quotationModel);
		
		Assert.assertEquals(quote1.getCustomerCost(), quote2.getCustomerCost());
		Assert.assertEquals(quote1.getDealCost(), quote2.getDealCost());
		
	}
	
	//Opened End - QM001 - 438805
	@Test
	public void compareCEStdOrderQuoteCapitalCosts() throws MalBusinessException{
		Long qmdId = 438805l;
		QuotationModel quotationModel = quotationService.getQuotationModelWithCostAndAccessories(qmdId);
		QuoteCost quote1 = capitalCostOverviewService.getQuoteCost(quotationModel);
		QuoteCost quote2 = capitalCostOverviewService.getQuoteCost2(quotationModel);
		
		Assert.assertEquals(quote1.getCustomerCost(), quote2.getCustomerCost());
		Assert.assertEquals(quote1.getDealCost(), quote2.getDealCost());
		
	}
	
	//Opened End - QM001 - 438825
	@Test
	public void compareOEStdOrderQuoteCapitalCosts() throws MalBusinessException{
		Long qmdId = 438825l;
		QuotationModel quotationModel = quotationService.getQuotationModelWithCostAndAccessories(qmdId);
		QuoteCost quote1 = capitalCostOverviewService.getQuoteCost(quotationModel);
		QuoteCost quote2 = capitalCostOverviewService.getQuoteCost2(quotationModel);
		
		Assert.assertEquals(quote1.getCustomerCost(), quote2.getCustomerCost());
		Assert.assertEquals(quote1.getDealCost(), quote2.getDealCost());
		
	}
	
	//438967
	@Test
	public void compareDifferentHeadersQuoteCapitalCosts() throws MalBusinessException{
		Long qmdId = 438967l;
		QuotationModel quotationModel = quotationService.getQuotationModelWithCostAndAccessories(qmdId);
		QuoteCost quote1 = capitalCostOverviewService.getQuoteCost(quotationModel);
		QuoteCost quote2 = capitalCostOverviewService.getQuoteCost2(quotationModel);
		
		Assert.assertEquals(quote1.getCustomerCost(), quote2.getCustomerCost());
		Assert.assertEquals(quote1.getDealCost(), quote2.getDealCost());
		
	}

	//438965
	@Test
	public void calcOpenEndNewQuoteCapitalCosts() throws MalBusinessException{
		Long qmdId = 438965l;
		QuotationModel quotationModel = quotationService.getQuotationModelWithCostAndAccessories(qmdId);
		QuoteCost quote1 = capitalCostOverviewService.getQuoteCost(quotationModel);

		Assert.assertTrue(quote1.getCustomerCost().doubleValue() > 0);
	}
	
	//413248
	@Test
	public void calcVRBOffOnFinalizedQuote() throws MalBusinessException{
		Long qmdId = 413248l;
		QuotationModel quotationModel = quotationService.getQuotationModelWithCostAndAccessories(qmdId);
		
		//QuoteCost quoteCost1 = capitalCostOverviewService.getQuoteCost(quotationModel);
		//QuoteCost quoteCost2 = capitalCostOverviewService.getQuoteCost2(quotationModel);
		
		Long finalizeQmd  = quotationService.getFinalizeQmd(quotationModel.getQmdId());
		QuotationModel finalQuote = quotationService.getQuotationModelWithCostAndAccessories(finalizeQmd);
		QuotationModel firstQuote = quotationService.getQuotationModelWithCostAndAccessories(finalQuote.getOriginalQmdId());
		
	    QuoteVO priorQuote1 = capitalCostService.getQuoteCapitalCosts(firstQuote, false, null, false);
	    QuoteVO quote1 = capitalCostService.getQuoteCapitalCosts(finalQuote, true, priorQuote1, false);
	    QuoteVO priorQuote2 = capitalCostService.getQuoteCapitalCosts2(firstQuote, false, null, false);
	    QuoteVO quote2 = capitalCostService.getQuoteCapitalCosts2(finalQuote, true, priorQuote2, false);
	    
		QuoteCostElementVO vrb1 = this.findElementByCode("VRB", quote1.getCostElements());
		QuoteCostElementVO priorVrb1 = this.findElementByCode("VRB", priorQuote1.getCostElements());
		QuoteCostElementVO vrb2 =  this.findElementByCode("VRB", quote2.getCostElements());
		QuoteCostElementVO priorVrb2 =  this.findElementByCode("VRB", priorQuote2.getCostElements());
		Assert.assertEquals(vrb1.getCustomerCost(), vrb2.getCustomerCost());
		Assert.assertEquals(vrb1.getDealCost(), vrb2.getDealCost());
		
		Assert.assertEquals(priorVrb1.getCustomerCost(), priorVrb2.getCustomerCost());
		Assert.assertEquals(priorVrb1.getDealCost(), priorVrb2.getDealCost());
		
	}
	
	
	//404966
	@Test
	public void calcOEOffOnFinalizedQuote() throws MalBusinessException{
		Long qmdId = 404966l;
		QuotationModel quotationModel = quotationService.getQuotationModelWithCostAndAccessories(qmdId);
		
		//QuoteCost quoteCost1 = capitalCostOverviewService.getQuoteCost(quotationModel);
		//QuoteCost quoteCost2 = capitalCostOverviewService.getQuoteCost2(quotationModel);
		
		Long finalizeQmd  = quotationService.getFinalizeQmd(quotationModel.getQmdId());
		QuotationModel finalQuote = quotationService.getQuotationModelWithCostAndAccessories(finalizeQmd);
		QuotationModel firstQuote = quotationService.getQuotationModelWithCostAndAccessories(finalQuote.getOriginalQmdId());
		
	    QuoteVO priorQuote1 = capitalCostService.getQuoteCapitalCosts(firstQuote, false, null, false);
	    QuoteVO quote1 = capitalCostService.getQuoteCapitalCosts(finalQuote, true, priorQuote1, false);
	    QuoteVO priorQuote2 = capitalCostService.getQuoteCapitalCosts2(firstQuote, false, null, false);
	    QuoteVO quote2 = capitalCostService.getQuoteCapitalCosts2(finalQuote, true, priorQuote2, false);
	    
		QuoteCostElementVO cd1 = this.findElementByCode("CD_FEE", quote1.getCostElements());
		QuoteCostElementVO cd2 =  this.findElementByCode("CD_FEE", quote2.getCostElements());
		Assert.assertEquals(cd1.getRechargeAmt(), cd2.getRechargeAmt());
		
		QuoteCostElementVO hb1 = this.findElementByCode("HOLDBACK", quote1.getCostElements());
		QuoteCostElementVO hb2 =  this.findElementByCode("HOLDBACK", quote2.getCostElements());
		Assert.assertEquals(hb1.getDealCost(), hb2.getDealCost());
		
		
	}
	
	private QuoteCostElementVO findElementByCode(String name, List<QuoteCostElementVO> list){
		QuoteCostElementVO retVal = null;
		for(QuoteCostElementVO item : list){
			if(MALUtilities.isNotEmptyString(item.getElementcode()) && item.getElementcode().equalsIgnoreCase(name)){
				retVal = item;
				break;
			}
		}
		return retVal;
	}
	
}
