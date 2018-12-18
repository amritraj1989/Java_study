package com.mikealbert.service;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.Query;
import javax.sql.DataSource;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.mikealbert.data.dao.QuotationElementDAO;
import com.mikealbert.data.entity.QuotationElement;
import com.mikealbert.rental.processors.AmortizationProcessor;
import com.mikealbert.rental.processors.ClosedEndProcessor;
import com.mikealbert.rental.processors.MaintenanceProcessor;
import com.mikealbert.rental.processors.OverheadProfitAdjustmentProcessor;
import com.mikealbert.rental.processors.OverheadProfitCostProcessor;
import com.mikealbert.rental.processors.RecapitalizeProcessor;
import com.mikealbert.rental.processors.VmpProcessor;
import com.mikealbert.rental.processors.ZeroRentalProcessor;
import com.mikealbert.rental.processors.inputoutput.AmortizationProcessorInput;
import com.mikealbert.rental.processors.inputoutput.ClosedEndProcessorInput;
import com.mikealbert.rental.processors.inputoutput.MaintenanceProcessorInput;
import com.mikealbert.rental.processors.inputoutput.OverheadProfitAdjustmentProcessorInput;
import com.mikealbert.rental.processors.inputoutput.OverheadProfitCostProcessorInput;
import com.mikealbert.rental.processors.inputoutput.ProcessorOutputType;
import com.mikealbert.rental.processors.inputoutput.RecapitalizeProcessorInput;
import com.mikealbert.rental.processors.inputoutput.VmpProcessorInput;
import com.mikealbert.rental.processors.inputoutput.ZeroRentalProcessorInput;
import com.mikealbert.service.RentalCalculationService;
import com.mikealbert.testing.BaseTest;

public class RentalCalculationProcessorsTest extends BaseTest {
	@Resource
	RentalCalculationService rentalCalculationService;
	@Resource
	QuotationElementDAO quotationElementDAO;
	@Resource
	private DataSource dataSource;
	@Resource
	RecapitalizeProcessor recapitalizeProcessor;
	@Resource
	AmortizationProcessor amortizationProcessor;
	@Resource
	OverheadProfitCostProcessor overheadProfitCostProcessor;
	@Resource
	OverheadProfitAdjustmentProcessor overheadProfitAdjustmentProcessor;	
	@Resource
	ZeroRentalProcessor zeroRentalProcessor;
	@Resource
	VmpProcessor vmpProcessor;
	@Resource
	ClosedEndProcessor closedEndProcessor;
	@Resource
	MaintenanceProcessor maintenanceProcessor;
	
	Connection connection = null;
	CallableStatement callableStatement = null;
	Long qelId = 3014677L; //1800514L;
	BigDecimal piAmount = new BigDecimal(100);
	BigDecimal piIntAdj = new BigDecimal(20);
	BigDecimal piProfAmt = new BigDecimal(10);
	BigDecimal piOverhead = new BigDecimal(100);
	BigDecimal piOverheadProfit = new BigDecimal(30);
	BigDecimal piOverheAdadjust = new BigDecimal(20);	
	BigDecimal piCost = new BigDecimal(10);
	BigDecimal piVmpFee = new BigDecimal(10);
	int piContractPeriod = 12;
	int piDistance	=	24000;

	@Ignore
	public void testZeroRentalProcessor() {
		try {
			QuotationElement quotationElement = quotationElementDAO.findById((long) qelId).orElse(null);

			ZeroRentalProcessorInput processorInputType = new ZeroRentalProcessorInput();
			processorInputType = new ZeroRentalProcessorInput();
			
			processorInputType.setQuotationElement(quotationElement);
			ProcessorOutputType processorOutputType = zeroRentalProcessor.process(processorInputType);
			QuotationElement outPutQuotationElement = processorOutputType.getQuotationElement();

			assertTrue(outPutQuotationElement.getRental().compareTo(new BigDecimal(0)) == 0);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
	@Ignore
	public void testVmpProcessor() {
		try {
			QuotationElement quotationElement = quotationElementDAO.findById((long) qelId).orElse(null);
			// Calling Database procedure to get the outcome.
			connection = DataSourceUtils.getConnection(dataSource);
			connection.setAutoCommit(false);

			callableStatement = connection
					.prepareCall("{CALL WILLOW2K.MAL_RENTAL_CALCS_TEST.vmp_calc(?, ?, ? , ?, ?)}");

			callableStatement.registerOutParameter(5, Types.VARCHAR);
			callableStatement.setLong(1, qelId);

			if (quotationElement.getQuotationModelAccessory()!=null) {
				callableStatement.setLong(2, quotationElement.getQuotationModelAccessory().getQmaId());
			} else {
				callableStatement.setNull(2, Types.INTEGER);
			}
			if (quotationElement.getQuotationDealerAccessory()!=null) {
				callableStatement.setLong(3, quotationElement.getQuotationDealerAccessory().getQdaId());
			} else {
				callableStatement.setNull(3, Types.INTEGER);
			}
			
			callableStatement.setBigDecimal(4, piVmpFee);
			
			callableStatement.execute();

			String stmt = "SELECT rental, overhead_amt, profit_amt, element_cost, capital_cost, residual_value, depreciation, no_rentals, interest, final_payment, apr FROM quotation_elements where qel_id = ?";		
			Query query = em.createNativeQuery(stmt);
			query.setParameter(1, qelId);
			@SuppressWarnings("unchecked")
			List<Object[]> list = query.getResultList(); 
			Object objArr[] = list.get(0); 
			
			connection.rollback(); 
			connection.close();

			VmpProcessorInput processorInputType = new VmpProcessorInput();
			processorInputType = new VmpProcessorInput();
			processorInputType.setQuotationElement(quotationElement);
			if (quotationElement.getQuotationModelAccessory()!=null) {
				processorInputType.setQmaId(quotationElement.getQuotationModelAccessory().getQmaId());
			}
			if (quotationElement.getQuotationDealerAccessory()!=null) {
				processorInputType.setQdaId(quotationElement.getQuotationDealerAccessory().getQdaId());
			}
			processorInputType.setVmpFee(piVmpFee);
			
			ProcessorOutputType processorOutputType = vmpProcessor.process(processorInputType);
			QuotationElement outPutQuotationElement = processorOutputType.getQuotationElement();
			// Comparing with the DB output		
			if (objArr[0]!=null) {
				assertTrue(outPutQuotationElement.getRental().compareTo((BigDecimal) objArr[0]) == 0);
			}
			if (objArr[1]!=null) {
				assertTrue(outPutQuotationElement.getOverheadAmt().compareTo((BigDecimal) objArr[1]) == 0);
			}
			if (objArr[2]!=null) {
				assertTrue(outPutQuotationElement.getProfitAmt().compareTo((BigDecimal) objArr[2]) == 0);
			}
			if (objArr[3]!=null) {
				assertTrue(outPutQuotationElement.getElementCost().compareTo((BigDecimal) objArr[3]) == 0);
			}
			if (objArr[4]!=null) {
				assertTrue(outPutQuotationElement.getCapitalCost().compareTo((BigDecimal) objArr[4]) == 0);
			}
			if (objArr[5]!=null) {
				assertTrue(outPutQuotationElement.getResidualValue().compareTo((BigDecimal) objArr[5]) == 0);
			}
			if (objArr[6]!=null) {
				assertTrue(outPutQuotationElement.getDepreciation().compareTo((BigDecimal) objArr[6]) == 0);
			}
			if (objArr[7]!=null) {
				assertTrue(outPutQuotationElement.getNoRentals().compareTo((BigDecimal) objArr[7]) == 0);
			}
			if (objArr[8]!=null) {
				assertTrue(outPutQuotationElement.getInterest().compareTo((BigDecimal) objArr[8]) == 0);
			}
			if (objArr[9]!=null) {
				assertTrue(outPutQuotationElement.getFinalPayment().compareTo((BigDecimal) objArr[9]) == 0);
			}
			if (objArr[10]!=null) {
				assertTrue(outPutQuotationElement.getApr().compareTo((BigDecimal) objArr[10]) == 0);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	

	@Ignore
	public void testOverheadProfitCostProcessor() {
		try {
			// Calling Database procedure to get the outcome.
			connection = DataSourceUtils.getConnection(dataSource);
			connection.setAutoCommit(false);

			callableStatement = connection.prepareCall("{CALL WILLOW2K.WILLOW_RENTAL_CALCS.setup_base_service3(?, ?, ? , ?, ?, ?)}");

			callableStatement.registerOutParameter(6, Types.VARCHAR);

			callableStatement.setLong(1, qelId);
			callableStatement.setBigDecimal(2, piOverhead);
			callableStatement.setBigDecimal(3, piOverheadProfit);
			callableStatement.setBigDecimal(4, piCost);
			callableStatement.setInt(5, piContractPeriod);
			
			callableStatement.execute();

			String stmt = "SELECT rental, overhead_amt, profit_amt, element_cost FROM quotation_elements where qel_id = ?";		
			Query query = em.createNativeQuery(stmt);
			query.setParameter(1, qelId);
			@SuppressWarnings("unchecked")
			List<Object[]> list = query.getResultList(); 
			Object objArr[] = list.get(0); 
			
			connection.rollback(); 
			connection.close();		
			// Calling Java Methods
			OverheadProfitCostProcessorInput processorInputType = new OverheadProfitCostProcessorInput();

			processorInputType = new OverheadProfitCostProcessorInput();
			QuotationElement quotationElement = quotationElementDAO.findById((long) qelId).orElse(null);
			processorInputType.setQuotationElement(quotationElement);
			processorInputType.setOverhead(piOverhead);
			processorInputType.setOverheadProfit(piOverheadProfit);
			processorInputType.setCost(piCost);
			processorInputType.setPeriod((long) piContractPeriod);

			ProcessorOutputType processorOutputType = overheadProfitCostProcessor.process(processorInputType);
			QuotationElement outPutQuotationElement = processorOutputType.getQuotationElement();
			// Comparing with the DB output
			if (objArr[0]!=null) {
				assertTrue(outPutQuotationElement.getRental().compareTo((BigDecimal) objArr[0]) == 0);
			}
			if (objArr[1]!=null) {
				assertTrue(outPutQuotationElement.getOverheadAmt().compareTo((BigDecimal) objArr[1]) == 0);
			}
			if (objArr[2]!=null) {
				assertTrue(outPutQuotationElement.getProfitAmt().compareTo((BigDecimal) objArr[2]) == 0);
			}
			if (objArr[3]!=null) {
				assertTrue(outPutQuotationElement.getElementCost().compareTo((BigDecimal) objArr[3]) == 0);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Ignore
	public void testOverheadProfitAdjustmentProcessor() {
		try {
			// Calling Database procedure to get the outcome.
			connection = DataSourceUtils.getConnection(dataSource);
			connection.setAutoCommit(false);

			callableStatement = connection
					.prepareCall("{CALL WILLOW2K.WILLOW_RENTAL_CALCS.setup_base_service4(?, ?, ? , ?, ?, ?)}");

			callableStatement.registerOutParameter(6, Types.VARCHAR);

			callableStatement.setLong(1, qelId);
			callableStatement.setBigDecimal(2, piOverhead);
			callableStatement.setBigDecimal(3, piOverheadProfit);
			callableStatement.setBigDecimal(4, piOverheAdadjust);
			callableStatement.setInt(5, piContractPeriod);
			
			callableStatement.execute();

			String stmt = "SELECT rental, overhead_amt, profit_amt, element_cost, accepted_ind FROM quotation_elements where qel_id = ?";		
			Query query = em.createNativeQuery(stmt);
			query.setParameter(1, qelId);
			@SuppressWarnings("unchecked")
			List<Object[]> list = query.getResultList(); 
			Object objArr[] = list.get(0); 
			
			connection.rollback(); 
			connection.close();		

			OverheadProfitAdjustmentProcessorInput processorInputType = new OverheadProfitAdjustmentProcessorInput();
			processorInputType = new OverheadProfitAdjustmentProcessorInput();
			QuotationElement quotationElement = quotationElementDAO.findById((long) qelId).orElse(null);
			processorInputType.setQuotationElement(quotationElement);
			processorInputType.setOverhead(piOverhead);
			processorInputType.setOverheadProfit(piOverheadProfit);
			processorInputType.setOverheadAdjust(piOverheAdadjust);
			processorInputType.setPeriod((long) piContractPeriod);

			ProcessorOutputType processorOutputType = overheadProfitAdjustmentProcessor.process(processorInputType);
			QuotationElement outPutQuotationElement = processorOutputType.getQuotationElement();
			// Comparing with the DB output
			if (objArr[0]!=null) {
				assertTrue(outPutQuotationElement.getRental().compareTo((BigDecimal) objArr[0]) == 0);
			}
			if (objArr[1]!=null) {
				assertTrue(outPutQuotationElement.getOverheadAmt().compareTo((BigDecimal) objArr[1]) == 0);
			}
			if (objArr[2]!=null) {
				assertTrue(outPutQuotationElement.getProfitAmt().compareTo((BigDecimal) objArr[2]) == 0);
			}
			if (objArr[3]!=null) {
				assertTrue(outPutQuotationElement.getElementCost().compareTo((BigDecimal) objArr[3]) == 0);
			}
			if (objArr[4]!=null) {
				assertTrue(outPutQuotationElement.getAcceptedInd().compareTo((String) objArr[4]) == 0);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Ignore
	public void testRecapitalizeProcessor() {
		try {
			// Calling Database procedure to get the outcome.
			connection = DataSourceUtils.getConnection(dataSource);
			connection.setAutoCommit(false);

			callableStatement = connection
					.prepareCall("{CALL WILLOW2K.MAL_RENTAL_CALCS_TEST.recap_calc(?, ?, ? , ?, ?, ?)}");

			callableStatement.registerOutParameter(6, Types.VARCHAR);

			callableStatement.setLong(1, qelId);
			callableStatement.setBigDecimal(2, piAmount);
			callableStatement.setBigDecimal(3, piIntAdj);
			callableStatement.setBigDecimal(4, piProfAmt);
			callableStatement.setLong(5, piContractPeriod);
			callableStatement.execute();
			String stmt = "SELECT rental, overhead_amt, profit_amt, element_cost, no_rentals FROM quotation_elements where qel_id = ?";
			Query query = em.createNativeQuery(stmt);
			query.setParameter(1, qelId);
			
			@SuppressWarnings("unchecked")
			List<Object[]> list = query.getResultList(); 
			Object objArr[] = list.get(0); 
			
			connection.rollback(); 
			connection.close();

			// Now calling the Java Method
			RecapitalizeProcessorInput processorInputType = new RecapitalizeProcessorInput();
			processorInputType = new RecapitalizeProcessorInput();
			QuotationElement quotationElement = quotationElementDAO.findById((long) qelId).orElse(null);
			processorInputType.setQuotationElement(quotationElement);
			processorInputType.setAmount(piAmount);
			processorInputType.setAdjustment(piIntAdj);
			processorInputType.setProfitAmount(piProfAmt);
			processorInputType.setPeriod((long) piContractPeriod);
			ProcessorOutputType processorOutputType = recapitalizeProcessor.process(processorInputType);
			QuotationElement outPutQuotationElement = processorOutputType.getQuotationElement();
			
			// Comparing with the DB output
			if (objArr[0]!=null) {
				assertTrue(outPutQuotationElement.getRental().compareTo((BigDecimal) objArr[0]) == 0);
			}
			if (objArr[1]!=null) {
				assertTrue(outPutQuotationElement.getOverheadAmt().compareTo((BigDecimal) objArr[1]) == 0);
			}
			if (objArr[2]!=null) {
				assertTrue(outPutQuotationElement.getProfitAmt().compareTo((BigDecimal) objArr[2]) == 0);
			}
			if (objArr[3]!=null) {
				assertTrue(outPutQuotationElement.getElementCost().compareTo((BigDecimal) objArr[3]) == 0);
			}
			if (objArr[4]!=null) {
				assertTrue(outPutQuotationElement.getNoRentals().compareTo((BigDecimal) objArr[4]) == 0);
			}			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Ignore
	public void testAmortizationProcessor() {
		try {
			// Calling Database procedure to get the outcome.
			connection = DataSourceUtils.getConnection(dataSource);
			connection.setAutoCommit(false);

			callableStatement = connection.prepareCall("{CALL WILLOW2K.MAL_RENTAL_CALCS_TEST.amortization_calc(?, ?, ? , ?)}");

			callableStatement.registerOutParameter(4, Types.VARCHAR);		
			callableStatement.setLong(1, qelId);
			callableStatement.setBigDecimal(2, piAmount);
			callableStatement.setLong(3, piContractPeriod);

			callableStatement.execute();

			String stmt = "SELECT rental, overhead_amt, profit_amt, element_cost, no_rentals FROM quotation_elements where qel_id = ?";
			Query query = em.createNativeQuery(stmt);
			query.setParameter(1, qelId);
			@SuppressWarnings("unchecked")
			List<Object[]> list = query.getResultList(); 
			Object objArr[] = list.get(0); 
			
			connection.rollback(); 
			connection.close();

			// Now calling the Java Method			
			AmortizationProcessorInput processorInputType = new AmortizationProcessorInput();

			processorInputType = new AmortizationProcessorInput();
			QuotationElement quotationElement = quotationElementDAO.findById((long) qelId).orElse(null);			
			processorInputType.setQuotationElement(quotationElement);
			processorInputType.setAmount(piAmount);
			processorInputType.setPeriod((long) piContractPeriod);

			ProcessorOutputType processorOutputType = amortizationProcessor.process(processorInputType);
			QuotationElement outPutQuotationElement = processorOutputType.getQuotationElement();
			
			// Comparing with the DB output
			if (objArr[0]!=null) {
				assertTrue(outPutQuotationElement.getRental().compareTo((BigDecimal) objArr[0]) == 0);
			}
			if (objArr[1]!=null) {
				assertTrue(outPutQuotationElement.getOverheadAmt().compareTo((BigDecimal) objArr[1]) == 0);
			}
			if (objArr[2]!=null) {
				assertTrue(outPutQuotationElement.getProfitAmt().compareTo((BigDecimal) objArr[2]) == 0);
			}
			if (objArr[3]!=null) {
				assertTrue(outPutQuotationElement.getElementCost().compareTo((BigDecimal) objArr[3]) == 0);
			}
			if (objArr[4]!=null) {
				assertTrue(outPutQuotationElement.getNoRentals().compareTo((BigDecimal) objArr[4]) == 0);
			}			

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	@Ignore
	@Test
	public void testClosedEndProcessor() {
		try {
			QuotationElement quotationElement = quotationElementDAO.findById((long) qelId).orElse(null);
			// Calling Database procedure to get the outcome.
			connection = DataSourceUtils.getConnection(dataSource);
			connection.setAutoCommit(false);

			callableStatement = connection.prepareCall("{CALL WILLOW2K.MAL_RENTAL_CALCS_TEST.closed_end_calc(?, ?, ? , ?)}");

			callableStatement.registerOutParameter(4, Types.VARCHAR);		
			callableStatement.setLong(1, qelId);
			if (quotationElement.getQuotationModelAccessory()!=null) {
				callableStatement.setLong(2, quotationElement.getQuotationModelAccessory().getQmaId());
			} else {
				callableStatement.setNull(2, Types.INTEGER);
			}
			if (quotationElement.getQuotationDealerAccessory()!=null) {
				callableStatement.setLong(3, quotationElement.getQuotationDealerAccessory().getQdaId());
			} else {
				callableStatement.setNull(3, Types.INTEGER);
			}
			callableStatement.execute();

			String stmt = "SELECT rental, overhead_amt, profit_amt, element_cost, capital_cost, residual_value, depreciation, no_rentals, interest, final_payment, apr FROM quotation_elements where qel_id = ?";
			Query query = em.createNativeQuery(stmt);
			query.setParameter(1, qelId);
			@SuppressWarnings("unchecked")
			List<Object[]> list = query.getResultList(); 
			Object objArr[] = list.get(0); 
			
			connection.rollback(); 
			connection.close();
			
			// Now calling the Java Method
			ClosedEndProcessorInput processorInputType = new ClosedEndProcessorInput();

			processorInputType = new ClosedEndProcessorInput();
						
			processorInputType.setQuotationElement(quotationElement);
			
			if (quotationElement.getQuotationModelAccessory()!=null) {
				processorInputType.setQmaId(quotationElement.getQuotationModelAccessory().getQmaId());
			}
			if (quotationElement.getQuotationDealerAccessory()!=null) {
				processorInputType.setQdaId(quotationElement.getQuotationDealerAccessory().getQdaId());
			}
			
			ProcessorOutputType processorOutputType = closedEndProcessor.process(processorInputType);
			QuotationElement outPutQuotationElement = processorOutputType.getQuotationElement();
			/*
			// Comparing with the DB output		
			if (objArr[0]!=null) {
				assertTrue(outPutQuotationElement.getRental().compareTo((BigDecimal) objArr[0]) == 0);
			}
			if (objArr[1]!=null) {
				assertTrue(outPutQuotationElement.getOverheadAmt().compareTo((BigDecimal) objArr[1]) == 0);
			}
			if (objArr[2]!=null) {
				assertTrue(outPutQuotationElement.getProfitAmt().compareTo((BigDecimal) objArr[2]) == 0);
			}
			if (objArr[3]!=null) {
				assertTrue(outPutQuotationElement.getElementCost().compareTo((BigDecimal) objArr[3]) == 0);
			}
			if (objArr[4]!=null) {
				assertTrue(outPutQuotationElement.getCapitalCost().compareTo((BigDecimal) objArr[4]) == 0);
			}
			if (objArr[5]!=null) {
				assertTrue(outPutQuotationElement.getResidualValue().compareTo((BigDecimal) objArr[5]) == 0);
			}
			if (objArr[6]!=null) {
				assertTrue(outPutQuotationElement.getDepreciation().compareTo((BigDecimal) objArr[6]) == 0);
			}
			if (objArr[7]!=null) {
				assertTrue(outPutQuotationElement.getNoRentals().compareTo((BigDecimal) objArr[7]) == 0);
			}
			if (objArr[8]!=null) {
				assertTrue(outPutQuotationElement.getInterest().compareTo((BigDecimal) objArr[8]) == 0);
			}
			if (objArr[9]!=null) {
				assertTrue(outPutQuotationElement.getFinalPayment().compareTo((BigDecimal) objArr[9]) == 0);
			}
			if (objArr[10]!=null) {
				assertTrue(outPutQuotationElement.getApr().compareTo((BigDecimal) objArr[10]) == 0);
			}*/

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Ignore
	public void testMaintenanceProcessor() {
		try {
			// Calling Database procedure to get the outcome.
			connection = DataSourceUtils.getConnection(dataSource);
			connection.setAutoCommit(false);

			callableStatement = connection.prepareCall("{CALL WILLOW2K.WILLOW_RENTAL_CALCS.setup_maint(?, ?, ? , ?, ?, ?)}");

			callableStatement.registerOutParameter(6, Types.VARCHAR);		
			callableStatement.setLong(1, qelId);
			callableStatement.setBigDecimal(2, piOverhead);
			callableStatement.setBigDecimal(3, piOverheadProfit);
			callableStatement.setInt(4, piContractPeriod);
			callableStatement.setInt(5, piDistance);

			callableStatement.execute();

			String stmt = "SELECT rental, overhead_amt, profit_amt, element_cost, no_rentals FROM quotation_elements where qel_id = ?";
			Query query = em.createNativeQuery(stmt);
			query.setParameter(1, qelId);
			@SuppressWarnings("unchecked")
			List<Object[]> list = query.getResultList(); 
			Object objArr[] = list.get(0); 
			
			connection.rollback(); 
			connection.close();
						
			// Now calling the Java Method
			MaintenanceProcessorInput processorInputType = new MaintenanceProcessorInput();

			processorInputType = new MaintenanceProcessorInput();
			QuotationElement quotationElement = quotationElementDAO.findById((long) qelId).orElse(null);			
			processorInputType.setQuotationElement(quotationElement);
			processorInputType.setOverhead(piOverhead);
			processorInputType.setOverheadProfit(piOverheadProfit);
			processorInputType.setPeriod( (long) piContractPeriod);
			processorInputType.setDistance((long) piDistance);

			ProcessorOutputType processorOutputType = maintenanceProcessor.process(processorInputType);
			QuotationElement outPutQuotationElement = processorOutputType.getQuotationElement();
			
			// Comparing with the DB output
			if (objArr[0]!=null) {
				assertTrue(outPutQuotationElement.getRental().compareTo((BigDecimal) objArr[0]) == 0);
			}
			if (objArr[1]!=null) {
				assertTrue(outPutQuotationElement.getOverheadAmt().compareTo((BigDecimal) objArr[1]) == 0);
			}
			if (objArr[2]!=null) {
				assertTrue(outPutQuotationElement.getProfitAmt().compareTo((BigDecimal) objArr[2]) == 0);
			}
			if (objArr[3]!=null) {
				assertTrue(outPutQuotationElement.getElementCost().compareTo((BigDecimal) objArr[3]) == 0);
			}
			if (objArr[4]!=null) {
				assertTrue(outPutQuotationElement.getNoRentals().compareTo((BigDecimal) objArr[4]) == 0);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
}
