package com.mikealbert.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;

import com.mikealbert.common.CommonCalculations;
import com.mikealbert.common.MalConstants;
import com.mikealbert.data.dao.ProductTypeCodeDAO;
import com.mikealbert.data.dao.QuotationElementDAO;
import com.mikealbert.data.dao.QuotationModelDAO;
import com.mikealbert.data.dao.WillowConfigDAO;
import com.mikealbert.data.entity.LeaseElement;
import com.mikealbert.data.entity.QuotationElement;
import com.mikealbert.data.entity.QuotationElementStep;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.QuotationModelAccessory;
import com.mikealbert.data.entity.QuotationStepStructure;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.data.vo.QuotationStepStructureVO;
import com.mikealbert.service.vo.QuoteCostElementVO;
import com.mikealbert.service.vo.QuoteVO;
import com.mikealbert.testing.BaseTest;
import com.mikealbert.util.MALUtilities;

public class RentalCalculationServiceTest extends BaseTest {
    @Resource RentalCalculationService rentalCalculationService;
    @Resource QuotationElementDAO quotationElementDAO;
    @Resource ProfitabilityService profitabilityService;    
    @Resource QuotationModelDAO quotationModelDAO;
    @Resource QuotationService quotationService;
    @Resource FleetMasterService fleetMasterService;
    @Resource CapitalCostService capitalCostService;
    @Resource ProductTypeCodeDAO  productTypeCodeDAO;
    @Resource WillowConfigDAO willowConfigDAO;
	
    private Long TEST_QMD_ID = 270805L;  // 270805
    
    @SuppressWarnings("unused")
    
    // This test work for already calculated quote as well as new quote also . 
    // If quote is new then it assume only one step as willow does and process calculations.
    @Test
    @Ignore
    public void testOECalculation() {
	try {
	    	
	    	//long INPUT_QMD_ID = 256149L;
	    	//long INPUT_QMD_ID = 256214;
		
			//long INPUT_QMD_ID =256156L;
			//long INPUT_QMD_ID = 256157L;
			//long INPUT_QMD_ID = 256166L;
			long INPUT_QMD_ID = 256265L;
			QuotationModel rowQuotationModel = quotationModelDAO.findById(INPUT_QMD_ID).orElse(null);
			//em.clear();
			
			//rentalCalculationService.createStepsForOE(rowQuotationModel, 12);
	    	QuotationModel calcQuotationModel = rentalCalculationService.getCalculatedQuotationModel(INPUT_QMD_ID, true,null);
			em.clear();	
			
			List<BigDecimal> stepCapCosts = new ArrayList<>();
			for(QuotationElement qel : calcQuotationModel.getQuotationElements()) {
				if(qel.getLeaseElement().getElementType().equals(MalConstants.FINANCE_ELEMENT)
						&& MALUtilities.convertYNToBoolean(qel.getIncludeYn())) {
					stepCapCosts.add(qel.getCapitalCost());
				}
			}
	    
	    	long qprId =  calcQuotationModel.getQuotation().getQuotationProfile().getQprId();
	    	long qmdId =  calcQuotationModel.getQmdId();
	    	BigDecimal mainElementResidual =   calcQuotationModel.getResidualValue(); 
	    	BigDecimal equipmentResidual =  rentalCalculationService.getEquipmentResidual(calcQuotationModel);
	    	BigDecimal finalResidual =  mainElementResidual.add(equipmentResidual); 
	    	
	    	Double adminFactorFinV = quotationService.getFinanceParam("ADMIN_FACT", qmdId, qprId);
	    	
		
	    	BigDecimal customerCost = capitalCostService.resolveAndCalcCapitalCosts(calcQuotationModel).getTotalCostToPlaceInServiceCustomer();
	    	
	    	
	    	BigDecimal period = BigDecimal.valueOf(calcQuotationModel.getContractPeriod());
	    	
	    	BigDecimal interestRate = quotationService.getInterestRate(qmdId);
	    	interestRate = interestRate.divide(new BigDecimal(1200), CommonCalculations.MC);  	// divide by 1200 to willow data UI
	    	BigDecimal adminFactor =  new BigDecimal(adminFactorFinV);				// as it is of willow UI	

		//residual_percentage := 100 * ( 1 / contract_period ) * ( 1 - (total_residual / total_capital_cost));             willow code		
	    	BigDecimal depreciationFactor =  BigDecimal.valueOf(1).divide(period , CommonCalculations.MC) .multiply(
			 BigDecimal.ONE.subtract( (finalResidual .divide(customerCost,  CommonCalculations.MC) ))); 
		
	    	List<QuotationStepStructure>  quotationStepStructureList = quotationService.getQuotationModelStepStructure(qmdId);
        	
        	List<QuotationStepStructureVO>  stepStructureVOList = new ArrayList<QuotationStepStructureVO>();
        	if(quotationStepStructureList.size() == 0){
        	    	QuotationStepStructureVO  stepStructureVO = new QuotationStepStructureVO();
        	    	stepStructureVO.setFromPeriod(1L);
        	    	stepStructureVO.setToPeriod(calcQuotationModel.getContractPeriod());
        	    	stepStructureVOList.add(stepStructureVO);
        		
        		customerCost =  calcQuotationModel.getCalculatedCapCost(); //this need to be done for brand new quote
        	}else{
        	    for (QuotationStepStructure stepStructure : quotationStepStructureList) {
        		
        		QuotationStepStructureVO  stepStructureVO = new QuotationStepStructureVO();
        	    	stepStructureVO.setFromPeriod(stepStructure.getId().getFromPeriod());
        	    	stepStructureVO.setToPeriod(stepStructure.getToPeriod().longValue());
        	    	stepStructureVOList.add(stepStructureVO);
			
		    } 
        	}
		System.out.println("customerCost>>" +customerCost);
		System.out.println("depreciationFactor>>" +depreciationFactor);
		System.out.println("interestRate>>" +interestRate);		
		System.out.println("adminFactor>>" +adminFactor);
		System.out.println("customerCost>>" +customerCost);
		
		List<QuotationStepStructureVO> quotationStepResponseList;
//		quotationStepResponseList = profitabilityService.calculateOEStepLease(customerCost, depreciationFactor, interestRate, 
//			 adminFactor, stepStructureVOList);
		
		quotationStepResponseList = profitabilityService.calculateOEStepLease(depreciationFactor, finalResidual , interestRate, 
				 adminFactor, stepStructureVOList);		
		
		System.out.println("++++++++++++++++++++++OUTPUT++++++++++++++++++++++");
		
                for (QuotationStepStructureVO quotationStep : quotationStepResponseList) {
                
                    System.out.println("Step ==  " + quotationStep.getFromPeriod());
                    System.out.println("netBookValue==  " + quotationStep.getNetBookValue());
                    System.out.println("finalRental==  " + quotationStep.getLeaseRate());
                }
                
                BigDecimal costToCompany = capitalCostService.resolveAndCalcCapitalCosts(calcQuotationModel).getTotalCostToPlaceInServiceDeal();
            	BigDecimal adminFee = profitabilityService.getFinanceParameter(MalConstants.CLOSED_END_LEASE_ADMIN, calcQuotationModel.getQmdId(), calcQuotationModel.getQuotation().getQuotationProfile().getQprId());
                BigDecimal calculatedIrr = profitabilityService.calculateIrrFromOEStep(quotationStepResponseList, costToCompany,adminFee);
                // we have to do save after getting all these calc done mainly during  UI integration
                
                System.out.println("================== calculatedIrr ==  " + calculatedIrr);

               // rentalCalculationService.saveCalculatedQuoteOE(calcQuotationModel, true,quotationStepResponseList, mainElementResidual, calculatedIrr, null, null, null, true);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
    
    @Ignore
    @Test
    public void testCalculateRental() {
	
	try {

	   QuotationModel quotationModel = rentalCalculationService.getCalculatedQuotationModel(255138L,true,null);
	   BigDecimal cost =  capitalCostService.resolveAndCalcCapitalCosts(quotationModel).getTotalCostToPlaceInServiceDeal();	
	 
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    
    private BigDecimal getTotalCost(QuoteVO quoteVO, String type) {
	BigDecimal cost = new BigDecimal(0);
	if(type.equals("DEAL") || type.equals("CUSTOMER")) {
		List<QuoteCostElementVO> costElements = quoteVO.getCostElements();
		for(QuoteCostElementVO element : costElements) {
			if(type.equals("DEAL")) {
				cost = cost.add(element.getDealCost());
			} else {
				cost = cost.add(element.getCustomerCost());
			}
		}
	}
	return cost;
}
    @Test
    public void testgetCapitalCostAmount() throws MalBusinessException{
    	QuotationModel quotationModel = quotationModelDAO.findById(TEST_QMD_ID).orElse(null);
    	if(quotationModel != null){
    		BigDecimal amount = rentalCalculationService.getCapitalCostAmount(quotationModel);
    		Assert.assertNotNull("Capital cost is null", amount);
    	}
    }
    @Test
    public void testIsVQPrinted(){
    	boolean res = rentalCalculationService.isVQPrinted(TEST_QMD_ID);
    	Assert.assertTrue(res);
    	
    }
    
    @Test
    public void	testIsQuoteStatusValidForEdit(){
    	QuotationModel quotationModel = quotationModelDAO.findById(TEST_QMD_ID).orElse(null);
    	//boolean res = rentalCalculationService.isQuoteStatusValidForEdit(quotationModel);
    	//Assert.assertTrue(res);
    }
    @Test
    public void testIsQuoteEditable(){
    	QuotationModel quotationModel = quotationModelDAO.findById(TEST_QMD_ID).orElse(null);
    	boolean res = rentalCalculationService.isQuoteEditable( quotationModel);
    	Assert.assertFalse(res);
    	
    }
    @Test
    public void	 testGetMainQuotationModelElement(){
    	QuotationModel quotationModel = quotationModelDAO.findById(TEST_QMD_ID).orElse(null);
    	QuotationElement quotationModelMain = rentalCalculationService.getMainQuotationModelElement( quotationModel);
    }
    @Test
    public void	testGetEquipmentResidual() throws MalBusinessException{
    	final Long QUOTE = 236556l;   // quote which has a "free of charge" piece of equipment so the residual will be affected
    	final BigDecimal EXPECTED_VALUE = new BigDecimal(3776.05).setScale(2, BigDecimal.ROUND_HALF_UP); 
    	
    	QuotationModel quotationModel = quotationModelDAO.findById(QUOTE).orElse(null);
    	BigDecimal	ecost= rentalCalculationService.getEquipmentResidual(quotationModel);
    	Assert.assertEquals(ecost, EXPECTED_VALUE);
    }
    
    @Test
    public void testGetLeaseFactor() {
    	BigDecimal payment;
    	BigDecimal depreciationFactor;
    	BigDecimal adminFactor;
    	BigDecimal customerCost;
    	BigDecimal result;
    	BigDecimal expected;

    	payment = new BigDecimal(429.05);
    	depreciationFactor = new BigDecimal(1.6667);
    	adminFactor = new BigDecimal(.1);
    	customerCost = new BigDecimal(20968);
    	result = profitabilityService.getLeaseFactor(payment, depreciationFactor, adminFactor, customerCost).setScale(4, BigDecimal.ROUND_HALF_UP);
    	expected = new BigDecimal(2.0462).setScale(4, BigDecimal.ROUND_HALF_UP);
    	assertEquals(result,expected);
    	
    	payment = new BigDecimal(377.46);
    	depreciationFactor = new BigDecimal(1.6667);
    	adminFactor = new BigDecimal(.1);
    	customerCost = new BigDecimal(20968);
    	result = profitabilityService.getLeaseFactor(payment, depreciationFactor, adminFactor, customerCost).setScale(4, BigDecimal.ROUND_HALF_UP);
    	expected = new BigDecimal(1.8002).setScale(4, BigDecimal.ROUND_HALF_UP);
    	assertEquals(result,expected);

    	payment = new BigDecimal(733.70);
    	depreciationFactor = new BigDecimal(2.7778);
    	adminFactor = new BigDecimal(.1);
    	customerCost = new BigDecimal(23121);
    	result = profitabilityService.getLeaseFactor(payment, depreciationFactor, adminFactor, customerCost).setScale(4, BigDecimal.ROUND_HALF_UP);
    	expected = new BigDecimal(3.1733).setScale(4, BigDecimal.ROUND_HALF_UP);
    	assertEquals(result,expected);

    	payment = new BigDecimal(561.93);
    	depreciationFactor = new BigDecimal(2.0000);
    	adminFactor = new BigDecimal(.1);
    	customerCost = new BigDecimal(23121);
    	result = profitabilityService.getLeaseFactor(payment, depreciationFactor, adminFactor, customerCost).setScale(4, BigDecimal.ROUND_HALF_UP);
    	expected = new BigDecimal(2.4304).setScale(4, BigDecimal.ROUND_HALF_UP);
    	assertEquals(result,expected);
    	
    	payment = new BigDecimal(698.61);
    	depreciationFactor = new BigDecimal(2.2222);
    	adminFactor = new BigDecimal(.065);
    	customerCost = new BigDecimal(28347.82);
    	result = profitabilityService.getLeaseFactor(payment, depreciationFactor, adminFactor, customerCost).setScale(4, BigDecimal.ROUND_HALF_UP);
    	expected = new BigDecimal(2.4644).setScale(4, BigDecimal.ROUND_HALF_UP);
    	assertEquals(result,expected);

    	payment = new BigDecimal(469.53);
    	depreciationFactor = new BigDecimal(1.3889);
    	adminFactor = new BigDecimal(.065);
    	customerCost = new BigDecimal(28347.82);
    	result = profitabilityService.getLeaseFactor(payment, depreciationFactor, adminFactor, customerCost).setScale(4, BigDecimal.ROUND_HALF_UP);
    	expected = new BigDecimal(1.6563).setScale(4, BigDecimal.ROUND_HALF_UP);
    	assertEquals(result,expected);

    }
    
    @Ignore
	@Test
	public void testMonthlyPaymentDeleteWhenDone() throws Exception{
		Long qmdId = 481165L;
		QuotationModel quotationModel, calcQuotationModel;
		BigDecimal monthlyRental = BigDecimal.valueOf(0);		
		
		//quotationModel = quotationService.getQuotationModelWithCostAndAccessories(qmdId);
		calcQuotationModel = rentalCalculationService.getCalculatedQuotationModel(qmdId, false, new BigDecimal("0"));
		calcQuotationModel.setCalculatedMontlyRental(BigDecimal.TEN); //TODO What sets this during recalc; or will it be set		

		monthlyRental = profitabilityService.calculateMonthlyRental(calcQuotationModel,
				calcQuotationModel.getCalculatedCapCost(), calcQuotationModel.getResidualValue(),
				new BigDecimal("6.8"));					

		for(QuotationElement qe : calcQuotationModel.getQuotationElements()) {
			if (qe.getLeaseElement().getElementType().equals(MalConstants.FINANCE_ELEMENT)) {					
				System.out.println("Per element calc: " + qe.getQelId() 
				        + " " + (MALUtilities.isEmpty(qe.getQuotationModelAccessory()) ? "Base " : qe.getQuotationModelAccessory().getOptionalAccessory().getAccessoryCode().getDescription()) 
				        + " - QE Rental: " + qe.getRental().toString() 
				        + " - QE Cap Cost: " + qe.getCapitalCost().toString()
				        + " - QE Element Cost: " + qe.getElementCost().toString());										}
		}				
							
				
		System.out.println(" - Monthly Rental " + monthlyRental);
		    
		
//		QuotationProfitability quotationProfitability = new QuotationProfitability();
//		quotationProfitability.setProfitAmount(new BigDecimal("6.8"));
//		
//		calcQuotationModel.setCalculatedMontlyRental(monthlyRental);
//		calcQuotationModel = rentalCalculationService.distributeRentalAmount(calcQuotationModel, quotationProfitability);		
//		for(QuotationElement qe : calcQuotationModel.getQuotationElements()) {
//			if (qe.getLeaseElement().getElementType().equals(MalConstants.FINANCE_ELEMENT)) {
//				System.out.println("Relative calc: " + qe.getQelId() 
//				        + " " + (MALUtilities.isEmpty(qe.getQuotationModelAccessory()) ? "Base " : qe.getQuotationModelAccessory().getOptionalAccessory().getAccessoryCode().getDescription()) 
//				        + " - " + qe.getRental().toString() 
//				        + " - " + qe.getCapitalCost().toString());
//			}
//		}						
	}
	  
	@Test
	public void testCalculateFromIRR() throws Exception{
		double irr = 6.8921;
		int period = 36;
		double capCost = 38230.20;
		double residual = 13350.0;
		long qmdId = 481165;
		long qprId = 4560;
		BigDecimal rental;
		BigDecimal disposalCost = BigDecimal.ZERO;
		BigDecimal adminFees = BigDecimal.ZERO;		
		
		adminFees = new BigDecimal(quotationService.getFinanceParam(willowConfigDAO.findById(MalConstants.CLOSED_END_LEASE_ADMIN).orElse(null).getConfigValue(), qmdId, qprId));
		disposalCost = new BigDecimal(quotationService.getFinanceParam(willowConfigDAO.findById(MalConstants.CLOSED_END_DISP_COST).orElse(null).getConfigValue(), qmdId, qprId));
		
		rental = BigDecimal.valueOf( CommonCalculations.calculateRentalfromIRR(
				irr, period, capCost, adminFees.doubleValue(), residual, disposalCost.doubleValue() ) );
		
		  //(irr, contractPeriod, CapitalCost, adminFees, residual, disposal) );		
		System.out.println("Monthly Rental: " + rental);
		System.out.println("Total Rental: " + (rental.multiply(new BigDecimal(period))));
		
	}
	
	@Test
	public void testOEStepMonthlyPmtDeleteWhenDone(){
		double interestRate = 4.520d / 1200d;
		double deprFactor = 4.1666667d / 100d;
		double monthsInPeriod = 6d;
		double monthsToEnd = 6d;		
		double customerCapCost = 40650d; //44616.0
		double stepCustomerCapCost = 30487d; //44616.0
		double nbv;		
		double adminFactor = 0.90d;
		double  tempRental;
				
//		nbv = customerCapCost - (customerCapCost * deprFactor * monthsToEnd);
//		System.out.println(" OE NBV " + nbv);
//		System.out.println(" OE stepCustomerCapCost " + stepCustomerCapCost);		
//		
//	    if(monthsToEnd <= 6) {
//	    	tempRental = CommonCalculations.pmt(interestRate, monthsInPeriod, -1 * customerCapCost, nbv, false);
//	    } else {
//	    	tempRental = CommonCalculations.pmt(interestRate, monthsInPeriod, -1 * stepCustomerCapCost, nbv, false);	    	
//	    }
//	    
//		System.out.println(" OE Step Monthly Rental " + tempRental);
//		
//		double adminCost = (customerCapCost / 1000d) * adminFactor;
//		System.out.println(" OE Step Monthly Rental + admin " + (tempRental + adminCost));		
		
	    tempRental = CommonCalculations.pmt(4.960d / 1200d, 6d, -2985, 2238.74999, false);
	    tempRental = tempRental +  (new BigDecimal(2985).divide(new BigDecimal("1000"), CommonCalculations.MC).multiply(new BigDecimal("0.90"), CommonCalculations.MC)).doubleValue();
		System.out.println(" OE Step Monthly Rental " + tempRental);
				
	}
		
	@Test
	public void testPopulateQuotationElementSteps() {
		List<QuotationStepStructureVO> stepVOs;		
		List<QuotationElement> elements;
		BigDecimal interestRate; 
		BigDecimal adminFactor; 
		BigDecimal depreciationFactor;
		BigDecimal rental;
		
		String[][] elementCostMatrix = {{"40800", "2985", "709", "272"}};
		long[][] stepsMatrix = {{1, 6}, {7, 12}, {13, 18}, {19, 24}};
		String[][] elementRentalValueMatrix ={ {"1887.88", "138.12", "32.81", "12.59"},
				                               {"1845.72", "135.04", "32.07", "12.30"},
				                               {"1803.56", "131.95", "31.34", "12.02"},
				                               {"1761.40", "128.87", "30.61", "11.74"} };			
		
		stepVOs = new ArrayList<>();
		elements = new ArrayList<>();
		interestRate = new BigDecimal("4.960").divide(new BigDecimal("1200"), CommonCalculations.MC); 
		adminFactor = new BigDecimal("0.90"); 
		depreciationFactor = new BigDecimal("4.1666667").divide(new BigDecimal("100"), CommonCalculations.MC);
		
		for(int i = 0; i < stepsMatrix.length; i++) {
			stepVOs.add(new QuotationStepStructureVO());
			stepVOs.get(stepVOs.size() - 1).setLeaseRate(BigDecimal.ZERO);
			stepVOs.get(stepVOs.size() - 1).setNetBookValue(BigDecimal.ZERO);		
			stepVOs.get(stepVOs.size() - 1).setFromPeriod(stepsMatrix[i][0]);
			stepVOs.get(stepVOs.size() - 1).setToPeriod(stepsMatrix[i][1]);			
		}
				
		for(int i = 0; i < elementCostMatrix.length; i++) {
			for(int j = 0; j < elementCostMatrix[i].length; j++) {
				elements.add(new QuotationElement());
				elements.get(elements.size() - 1).setLeaseElement(new LeaseElement());
				elements.get(elements.size() - 1).getLeaseElement().setElementType(MalConstants.FINANCE_ELEMENT);
				elements.get(elements.size() - 1).setIncludeYn("Y");
				elements.get(elements.size() - 1).setRental(BigDecimal.ZERO);
				elements.get(elements.size() - 1).setCapitalCost(new BigDecimal(elementCostMatrix[i][j]));
				elements.get(elements.size() - 1).setQuotationElementSteps(new ArrayList<QuotationElementStep>());
				elements.get(elements.size() - 1).setResidualValue(BigDecimal.ZERO);
				
				if(j != 0) {
					elements.get(elements.size() - 1).setQuotationModelAccessory(new QuotationModelAccessory());					
				}
			}
		}
							
		//rentalCalculationService.populateQuotationElementStep(stepVOs, elements, interestRate, adminFactor, depreciationFactor);
		
		for(int i = 0; i < elementRentalValueMatrix.length; i++) {
			for(int j = 0; j < elementRentalValueMatrix[i].length; j++) {
				if(elements.get(j).getQuotationElementSteps() != null && elements.get(j).getQuotationElementSteps().size() > 0){
					rental = elements.get(j).getQuotationElementSteps().get(i).getRentalValue();
					System.out.println("Step Rental = " + rental.toString());
					assertEquals("Incorrect OE Step Rental", new BigDecimal(elementRentalValueMatrix[i][j]), rental);
				}
			}
		}
		
	}
	
	@Ignore
	@Test
	public void testCalculateOEStepLease() throws Exception {
		BigDecimal depreciationFactor, interestRate, adminFactor;
	
		List<BigDecimal> capCosts;
		String[] expectedStepdRentals = {"1424.33", "1325.03", "29.48"}; 			
		
		depreciationFactor = new BigDecimal("0.027777778"); 
		interestRate = new BigDecimal("0.0043416666666667"); 
		adminFactor = new BigDecimal("0.65");	
		BigDecimal finalNBV  = new BigDecimal("0.00"); 
//		capCosts = new ArrayList<>();
//		capCosts.add(new BigDecimal("40800"));
//		capCosts.add(new BigDecimal("2985"));	
//		capCosts.add(new BigDecimal("709"));
//		capCosts.add(new BigDecimal("272"));		
//
//		inputStepStructureVOList = new ArrayList<>();
//		inputStepStructureVOList.add(new QuotationStepStructureVO());
//		inputStepStructureVOList.get(0).setFromPeriod(1L);
//		inputStepStructureVOList.get(0).setToPeriod(6L);
//				
//		inputStepStructureVOList.add(new QuotationStepStructureVO());
//		inputStepStructureVOList.get(1).setFromPeriod(7L);
//		inputStepStructureVOList.get(1).setToPeriod(12L);
//		
//		inputStepStructureVOList.add(new QuotationStepStructureVO());
//		inputStepStructureVOList.get(2).setFromPeriod(13L);
//		inputStepStructureVOList.get(2).setToPeriod(18L);		
		
		QuotationModel quotationModel = quotationModelDAO.findById(446387L).orElse(null);   //484405L);
		
		List<QuotationStepStructureVO> inputStepStructureVOList = rentalCalculationService.getQuotationStepStructureVOs(quotationModel);
		
		inputStepStructureVOList = profitabilityService.calculateOEStepLease(depreciationFactor, finalNBV , interestRate, adminFactor, inputStepStructureVOList);		
		
		for(int i=0; i < expectedStepdRentals.length; i++) {
			assertEquals("Step lease payment is incorrect", new BigDecimal(expectedStepdRentals[i]).doubleValue(), inputStepStructureVOList.get(i).getLeaseRate().doubleValue(), 0);			
			
//			System.out.println("Step lease payment is incorrect=="+ inputStepStructureVOList.get(i).getLeaseRate().doubleValue());
		}
		

	}
	
	@Test
	public void testPmt() {
		BigDecimal clientCost, interestRate, period, nbv, depreciationFactor, adminCost, adminFactor, cPmt, runningTotal;
		String[] elementCostMatrix = {"40800", "2985", "709", "272"};
		
		clientCost = new BigDecimal("272");
		interestRate = new BigDecimal("0.004133333333333333333333333333333333333333"); 
		period = new BigDecimal("6");
		depreciationFactor = new BigDecimal("0.041666667");	
		adminFactor = new BigDecimal("0.9");			
		nbv = clientCost.subtract(  (clientCost.multiply(depreciationFactor, CommonCalculations.MC).multiply(period, CommonCalculations.MC)));
		adminCost = clientCost.divide(new BigDecimal(1000) , CommonCalculations.MC).multiply(adminFactor, CommonCalculations.MC);		
		cPmt = BigDecimal.valueOf(CommonCalculations.pmt(interestRate.doubleValue(), period.doubleValue(), clientCost.negate().doubleValue(), nbv.doubleValue(), false));
		cPmt = cPmt.add(adminCost);
		
		System.out.println("Pmt = " + cPmt);		
		System.out.println("Total Payment  = " + cPmt.add(adminCost));	
		
		runningTotal = BigDecimal.ZERO;
		for(int i=0; i<elementCostMatrix.length; i++) {
			clientCost = new BigDecimal(elementCostMatrix[i]);
			nbv = clientCost.subtract(  (clientCost.multiply(depreciationFactor, CommonCalculations.MC).multiply(period, CommonCalculations.MC)));
			adminCost = clientCost.divide(new BigDecimal(1000) , CommonCalculations.MC).multiply(adminFactor, CommonCalculations.MC);			
			cPmt = BigDecimal.valueOf(CommonCalculations.pmt(interestRate.doubleValue(), period.doubleValue(), clientCost.negate().doubleValue(), nbv.doubleValue(), false));
			cPmt = CommonCalculations.getRoundedValue(cPmt.add(adminCost), 2);
			runningTotal = runningTotal.add(cPmt);
			System.out.println(i + " Admin Cost = " + adminCost);				
			System.out.println(i + " Element Rental = " + cPmt);			
		}
		System.out.println("Running Total = " + runningTotal);		
	}
	
//	@Test
//	public void testLoadQuoteElementParameters() {
//		final Long QMD_ID = 451505L;
//		QuotationModel qmd;		
//		qmd = quotationModelDAO.findOne(QMD_ID);
//		qmd = rentalCalculationService.loadQuoteElementParameters(qmd);
//		qmd = quotationModelDAO.saveAndFlush(qmd);
//		for(QuotationElement qe : qmd.getQuotationElements()){
//			if(qe.getLeaseElement().getElementType().equals(MalConstants.FINANCE_ELEMENT)) {
//				assertTrue("Quotation Element is missing Quotation Element Parameter(s)", qe.getQuoteElementParameters().size() > 0);;
//			}			
//		}		
//	}
//	
//	@Test
//	public void testSaveCalculatedQuote() throws Exception {
//		final Long QMD_ID = 451505L;
//		QuotationModel dbQmd, calcQmd;
//		
//		dbQmd = quotationModelDAO.findOne(QMD_ID);
//		calcQmd = rentalCalculationService.getCalculatedQuotationModel(QMD_ID, false, BigDecimal.ZERO); 
//		
//		rentalCalculationService.saveCalculatedQuote(dbQmd, calcQmd, dbQmd.getResidualValue(), dbQmd.getQuotationProfitabilities().get(0), false);
//		
//		for(QuotationElement qe : dbQmd.getQuotationElements()){
//			if(qe.getLeaseElement().getElementType().equals(MalConstants.FINANCE_ELEMENT)) {
//				assertTrue("Quotation Element is missing Quotation Element Parameter(s)", qe.getQuoteElementParameters().size() > 0);;
//			}			
//		}
//		
//	}
	
	
//	@Test
//	public void testgetCalculatedQuotationModel() throws Exception{
//		final Long QMD_ID = 451505L;
//		QuotationModel qmd;
//		
//		qmd = rentalCalculationService.getCalculatedQuotationModel(QMD_ID, false, BigDecimal.ZERO);
//		
//		for(QuotationElement qe : qmd.getQuotationElements()) {
//			if(qe.getLeaseElement().getElementType().equals(MalConstants.FINANCE_ELEMENT)) {
//				assertTrue("Quotation Element is missing Quotation Element Parameter(s)", qe.getQuoteElementParameters().size() > 0);;
//			}
//		}
//	}
	
	@Test
	public void testGetLeaseElementByTypeAndProfile(){
		
		List<LeaseElement> list = rentalCalculationService.getLeaseElementByTypeAndProfile("FINANCE", 38350l);
		
		assertTrue("Finance Element does not exists for this profile 383501", list.size() > 0);
	}
}
