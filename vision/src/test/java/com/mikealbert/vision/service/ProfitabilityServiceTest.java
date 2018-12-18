package com.mikealbert.vision.service;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.mikealbert.common.CommonCalculations;
import com.mikealbert.data.dao.QuotationModelDAO;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.CapitalCostService;
import com.mikealbert.service.ProfitabilityService;
import com.mikealbert.service.QuotationService;
import com.mikealbert.service.RentalCalculationService;
import com.mikealbert.service.vo.CapitalCostModeValuesVO;
import com.mikealbert.service.vo.QuoteCostElementVO;
import com.mikealbert.service.vo.QuoteVO;
import com.mikealbert.testing.BaseTest;
import com.mikealbert.util.SpringAppContext;

public class ProfitabilityServiceTest extends BaseTest {
    
    @Resource ProfitabilityService profitabilityService;
    @Resource CapitalCostOverviewService  capitalCostOverviewService;
    @Resource CapitalCostService  capitalCostService;
    @Resource QuotationService quotationService;
    @Resource RentalCalculationService rentalCalculationService;
    
    @Resource QuotationModelDAO quotationModelDAO;
    private Long TEST_QMD_ID = 255871L;
    
    @Test
    public void calculateRentalfromIRR1() {
	try {

	    double monthlyRental = CommonCalculations.calculateRentalfromIRR(6.99D, 24, 35444.85D, 15.00D, 17500.00D, 375.00D);
	    System.out.println("monthlyRental->" + monthlyRental);
	    assertEquals("934.8970093270327", String.valueOf(monthlyRental));

	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    @Test
    public void calculateRentalfromIRR2() {
	try {

	    double monthlyRental = CommonCalculations.calculateRentalfromIRR(5.88D, 24, 27436.65D, 15.00D, 5000.00D, 375.00D);
	    System.out.println("monthlyRental->" + monthlyRental);
	   assertEquals("1047.4557411580604", String.valueOf(monthlyRental));

	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
    
    @Test
    public void testGetResidualAmount() throws MalBusinessException{
    	QuotationModel quotationModel = quotationModelDAO.findById(TEST_QMD_ID).orElse(null);
    	if(quotationModel != null){
    		Double resAmount = profitabilityService.getResidualAmount(quotationModel);
    		Assert.assertNotNull(resAmount);
    	//	Assert.assertEquals("1000", String.valueOf(resAmount.doubleValue()));
    	}
    }
    
    @Test
    public void testGetFinanceParameter() throws MalBusinessException{
    	BigDecimal disposalCost = profitabilityService.getFinanceParameter("EOL_DISP_COST", TEST_QMD_ID, 3L);
    	
    	Assert.assertEquals("375.0", String.valueOf(disposalCost));
    }
    
    @Ignore
    	/**
	 * Added test to calc margin to cross verify if margin to IRR script conversion are correct.
	 * this test is not needed to execute always as part of build because it was created to verify  if
	 *  conversion script is populating correct data.
	 */
    public void reportOfMarginVsIRRConversion() {

	try {

	    DataSource dataSource = (DataSource) SpringAppContext.getBean("dataSource");
	    Connection connection = dataSource.getConnection();
	    connection.setAutoCommit(false);

	    Long qmdId = 0L;
	    BigDecimal approvedIRR = BigDecimal.ZERO;
	    BigDecimal approvedMargin = BigDecimal.ZERO;
	    BigDecimal dealCost = BigDecimal.ZERO;
	    BigDecimal equipmentResidual;

	    PreparedStatement selectStatement = connection
		    .prepareStatement(" Select Qp.Qmd_Qmd_Id , Qp. IRR_APPROVED_LIMIT , Qmf.NVALUE,Qp.IRR_APPROVED_USER, Qmf.parameter_key"
			    + " FROM  Quotation_Profitability Qp , Quotation_Model_Finances Qmf "
			    + " WHERE  Qp.Qmd_Qmd_Id = Qmf.Qmd_Qmd_Id  " + " AND Qmf.Parameter_Key ='CE_MIN_PROFIT' "
			    + " AND Qmf.nvalue > 0 " + " AND Qp.Profit_Type = 'P'  " + " AND  Qp.QMD_QMD_ID IN (SELECT qmd_id "
			    + " FROM quotation_models qmd, quotations q " + " WHERE quote_status in (15, 18)  "
			    + " AND qmd.quo_quo_id = q.quo_id " + " AND Q.Quote_Exp_Date >= Sysdate)");

	    ResultSet rs = selectStatement.executeQuery();
	    
	    while (rs.next()) {
		
		qmdId = rs.getLong(1);
		approvedIRR = rs.getBigDecimal(2);
		approvedMargin = rs.getBigDecimal(3);

		QuotationModel quotationModel = quotationService.getQuotationModel(qmdId);
		QuoteVO targetQuote = null;
		targetQuote = getQuoteVOWithCalculatedCost(quotationModel, false);
		
		if (targetQuote != null) {
		    dealCost = BigDecimal.valueOf(getTotalCost(targetQuote, "DEAL").doubleValue());

		}
		BigDecimal interestRate = quotationService.getInterestRate(qmdId);
		equipmentResidual = rentalCalculationService.getEquipmentResidual(quotationModel);
		
		BigDecimal financeValue = profitabilityService.calculateMonthlyRental(quotationModel, dealCost,
			equipmentResidual.add(quotationModel.getResidualValue()), approvedIRR);

		BigDecimal totalMargin = profitabilityService.calcMargin(dealCost, equipmentResidual.add(quotationModel.getResidualValue()), quotationModel
			.getContractPeriod().intValue(), new BigDecimal(interestRate.doubleValue() / 100), financeValue, new BigDecimal(
			375.00), new BigDecimal(15.00));

		System.out.println("qmdId=>>>" + qmdId + "    approvedMargin>> " + approvedMargin + "     CalcMargin>>  "
				+ totalMargin.intValue() + "   approvedIRR>> " + approvedIRR);

	    }

	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
    
    //TODO Ideally below thwo private method should be in some service so that it can used inside QuoteOverBean and Junit.
    private QuoteVO getQuoteVOWithCalculatedCost(QuotationModel qm, Boolean includeTempEquipments) throws MalBusinessException {
	QuoteVO targetQuote = null;
	QuoteVO priorQuote;
	CapitalCostModeValuesVO capitalCostModeValuesVO = capitalCostService.getModeValues(qm);
	String mode = capitalCostModeValuesVO.getMode();
	if (mode.equals(CapitalCostService.STANDARD_ORDER_MODE)) {
	    targetQuote = capitalCostService.getQuoteCapitalCosts(capitalCostModeValuesVO.getStandardOrderQuoteModel(), false, null,
		    capitalCostModeValuesVO.getIsStockOrder());
	} else if (mode.equals(CapitalCostService.FIRST_MODE)) {
	    targetQuote = capitalCostService.getQuoteCapitalCosts(capitalCostModeValuesVO.getFirstQuoteModel(), false, null,
		    capitalCostModeValuesVO.getIsStockOrder());
	} else if (mode.equals(CapitalCostService.FINALIZED_MODE)) {
	    priorQuote = capitalCostService.getQuoteCapitalCosts(capitalCostModeValuesVO.getFirstQuoteModel(), false, null,
		    capitalCostModeValuesVO.getIsStockOrder());
	    targetQuote = capitalCostService.getQuoteCapitalCosts(capitalCostModeValuesVO.getFinalQuoteModel(), true, priorQuote,
		    capitalCostModeValuesVO.getIsStockOrder());
	}
	return targetQuote;
    }

    private BigDecimal getTotalCost(QuoteVO quoteVO, String type) {
	BigDecimal cost = new BigDecimal(0);
	if (type.equals("DEAL") || type.equals("CUSTOMER")) {
	    List<QuoteCostElementVO> costElements = quoteVO.getCostElements();
	    for (QuoteCostElementVO element : costElements) {
		if (type.equals("DEAL")) {
		    cost = cost.add(element.getDealCost());
		} else {
		    cost = cost.add(element.getCustomerCost());
		}
	    }
	}
	return cost;
    }

}
