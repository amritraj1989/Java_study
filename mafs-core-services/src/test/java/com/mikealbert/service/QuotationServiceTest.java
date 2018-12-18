package com.mikealbert.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.mikealbert.data.TestQueryConstants;
import com.mikealbert.data.dao.QuotationDealerAccessoryDAO;
import com.mikealbert.data.dao.QuotationModelDAO;
import com.mikealbert.data.dao.QuotationProfileDAO;
import com.mikealbert.data.dao.ReportDAO;
import com.mikealbert.data.dao.TimePeriodDAO;
import com.mikealbert.data.entity.DriverGradeGroupCode;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.Quotation;
import com.mikealbert.data.entity.QuotationDealerAccessory;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.QuotationProfile;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.data.enumeration.VehicleOrderType;
import com.mikealbert.data.vo.WillowReportParamVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.vo.FormalClosedEndQuoteInputVO;
import com.mikealbert.service.vo.FormalQuoteInput;
import com.mikealbert.testing.BaseTest;
import com.mikealbert.util.MALUtilities;

public class QuotationServiceTest extends BaseTest {
	@Resource QuotationService quotationService;
	@Resource QuotationDealerAccessoryDAO quotationDealerAccessoryDAO;
	@Resource TimePeriodDAO timePeriodDAO;
	@Resource CustomerAccountService customerAccountService;
	@Resource DriverGradeService driverGradeService;	
	@Resource QuotationProfileDAO quotationProfileDAO; //TODO Use Service
	@Resource ReportDAO reportDAO;
	@Resource QuotationModelDAO quotationModelDAO;	
	
	Long qmdIdOfUnitOnContract;		
	
	@Before
	public void init(){
		qmdIdOfUnitOnContract=  Long.parseLong(em.createNativeQuery(TestQueryConstants.READ_QMD_ID_FOR_ON_CONTRACT_UNIT).getSingleResult().toString());		
	}
	
	
	@Test
    public void testGetQuoteProgramDescription() throws MalBusinessException{
		String programDescription = quotationService.getQuoteProgramDescription(qmdIdOfUnitOnContract);
		assertNotNull("Could not find a program description for qmd id", programDescription);
	}
	
	
	@Test
	public void testGetMafsAuthorizationLimit() throws MalBusinessException{
		BigDecimal mafsAuthLimit = quotationService.getMafsAuthorizationLimit(1L, "C", "00027238", "00964425");
		Assert.assertNotNull(mafsAuthLimit);
		Assert.assertTrue(mafsAuthLimit.longValue() == 500L);
	}
	
	
	@Test
	public void testGetDriverAuthorizationLimit() throws MalBusinessException{
		BigDecimal driverAuthLimit = quotationService.getDriverAuthorizationLimit(1L, "C", "00027238", "00964425");
		Assert.assertNotNull(driverAuthLimit);
		Assert.assertTrue(driverAuthLimit.longValue() == 0L);
	}
	
	
	@Test
	public void testGetOrderingAndDeliveringDealers() throws MalBusinessException{
		ExternalAccount dealer;
		dealer = quotationService.getOrderingDealer(qmdIdOfUnitOnContract);
		Assert.assertNotNull("Ordering dealer is null", dealer);
		dealer = quotationService.getDeliveringDealer(qmdIdOfUnitOnContract);
		Assert.assertNotNull("Delivering dealer is null", dealer);
		Long standardOrderQmdId = 100333l;
		dealer = quotationService.getOrderingDealer(standardOrderQmdId);
		Assert.assertNull("Standard order ordering dealer is not null", dealer);
		dealer = quotationService.getDeliveringDealer(standardOrderQmdId);
		Assert.assertNull("Standard order delivering dealer is not null", dealer);
		
	}

	@Ignore 
	@Test
	public void deleteQuotationDealerAccessoryTest() throws Exception  {
		Long qmdId = 310336L;
		Long qdaId = 321728L;   // data not found		
		QuotationDealerAccessory quotationDealerAccessory = quotationDealerAccessoryDAO.findById(qdaId).orElse(null);
		
		quotationService.deleteQuotationDealerAccessoryFromQuote(qmdId, quotationDealerAccessory);
	}
	
	@Ignore
	@Test
	public void testCreateConractRevisionQuote() {
		String employeeNo ="KUMAR_R";
		Long qmdId = 305931L;   // billing out of date
		String revType = "R";		
		Long contractRevQmd;
		try {
			contractRevQmd = quotationService.createConractRevisionQuote(qmdId,revType, employeeNo);
			if (!MALUtilities.isEmpty(contractRevQmd)) {
				System.out.println("Revised Qmd Id: " + contractRevQmd);
			}
			
		} catch (Exception e) {
			System.out.println("Error Message: " + e.getMessage());
		}		
	}
	
	@Test
	public void testMalActualMonthsBetween() throws Exception{
		 SimpleDateFormat dateformat2 = new SimpleDateFormat("dd-M-yyyy");
		  String strdate2 = "12-06-2017 ";
		  String strdate3 = "18-08-2018 ";
		  malActualMonthsBetween(dateformat2.parse(strdate2), dateformat2.parse(strdate3));
	}
	
	public double monthsBetween(Calendar date1, Calendar date2){
        double monthsBetween = 0;
        //difference in month for years
        monthsBetween = (date1.get(Calendar.YEAR)-date2.get(Calendar.YEAR))*12;
        //difference in month for months
        monthsBetween += date1.get(Calendar.MONTH)-date2.get(Calendar.MONTH);
        //difference in month for days
        monthsBetween += ((date1.get(Calendar.DAY_OF_MONTH)-date2.get(Calendar.DAY_OF_MONTH))/31d);
        return monthsBetween;
    }
  
	public BigDecimal malActualMonthsBetween(Date inputStartDate, Date inputEndDate){
		BigDecimal monthsBetween = BigDecimal.ZERO; 
		BigDecimal fractionStart = BigDecimal.ZERO;
		BigDecimal fractionLast = BigDecimal.ZERO;
		BigDecimal wholeMonth = BigDecimal.ZERO;
		Date startDate;
		Date endDate;
	  
		Calendar cal = Calendar.getInstance();
		cal.setTime(inputStartDate);
		 
		if(cal.get(Calendar.DATE) == 1){
			fractionStart = BigDecimal.ZERO;
			startDate = inputStartDate;
		}else{
			fractionStart = new BigDecimal(cal.getActualMaximum(Calendar.DATE) - cal.get(Calendar.DATE) + 1).divide(new BigDecimal(cal.getActualMaximum(Calendar.DATE)), 40 , RoundingMode.HALF_UP);
			cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
			cal.add(Calendar.DATE, 1);
			startDate = cal.getTime();
		}
	  
		cal.setTime(inputEndDate);
		if(cal.getActualMaximum(Calendar.DATE) == cal.get(Calendar.DATE)){
			fractionLast = BigDecimal.ZERO;
			cal.add(Calendar.DATE, 1);
			endDate = cal.getTime();
		}else{
			fractionLast = new BigDecimal(cal.get(Calendar.DATE)).divide(new BigDecimal(cal.getActualMaximum(Calendar.DATE)), 40 , RoundingMode.HALF_UP);
			cal.set(Calendar.DATE, cal.getMinimum(Calendar.DATE));
			endDate = cal.getTime();
		}
	  
		Calendar stCal = Calendar.getInstance();
		stCal.setTime(startDate);
		Calendar endCal = Calendar.getInstance();
		endCal.setTime(endDate);
	  
		wholeMonth = new BigDecimal(monthsBetween( endCal, stCal));
	  
		monthsBetween = (fractionStart.add(fractionLast).add(wholeMonth)).setScale(2, RoundingMode.HALF_UP); 
	  
		System.out.println("monthsBetween: " + monthsBetween);
	  
		return monthsBetween;
  	}	
	
	
	
	@Test
	public void testQuoteRevisionExpirationDate() throws Exception  {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Date revisionEffectiveDate = dateFormat.parse("04/01/2017");
		Date quoteExpiryDate = timePeriodDAO.findPreviousBillingDate(1L, revisionEffectiveDate);
		System.out.println(quoteExpiryDate);
	}

	@Test
	public void getContractEffectiveDateTest() throws Exception  {
		Date myDate = quotationService.getContractEffectiveDate(1L);
		System.out.print(myDate);
		
	}
	
	@Test
	public void testGenerateQuoId() {
		Long quoId;
		
		quoId = quotationService.generateQuoId();
		
		assertNotNull("QuoId was not generated ", quoId);
	}
	@Ignore
	@Test
	public void testCreateQuotation() {
		final Long C_ID = 1L;
		final String ACCOUNT_CODE = "00000423";
		final Long QPR_ID = 4560L;
		final Long TERM = 24L;
		final Long DISTANCE = 60000L;
		final Long CFG_ID = 142L;		
		final String DRIVER_GRADE_GROUP_CODE = "U";
		final String EMPLOYEE_NO = "SIBLEY_W";
		final String UNIT_NO = null;
		final Long ODO_READING = 0L;
		final VehicleOrderType ORDER_TYPE = VehicleOrderType.FACTORY;	
		final Long DESIRED_QUO_ID = quotationService.generateQuoId();
				
		ExternalAccount client = customerAccountService.getCustomerAccount(ACCOUNT_CODE, CorporateEntity.fromCorpId(C_ID));
		QuotationProfile profile = quotationProfileDAO.findById(QPR_ID).orElse(null);
		DriverGradeGroupCode gradeGroupCode = driverGradeService.getDriverGrade(DRIVER_GRADE_GROUP_CODE).getGradeGroup();
		
		FormalQuoteInput formalQuoteInputVO = new FormalClosedEndQuoteInputVO(client, profile, gradeGroupCode, ORDER_TYPE, TERM, DISTANCE, CFG_ID, 
				UNIT_NO, ODO_READING, EMPLOYEE_NO, DESIRED_QUO_ID);
		
		Quotation quote = quotationService.createQuote(formalQuoteInputVO);		
		
		assertTrue("Quotation what not created", !(MALUtilities.isEmpty(quote) || MALUtilities.isEmpty(quote.getQuoId())));
		assertTrue("Quotation Multi Quote Elements were not created", !quote.getMulQuoteElems().isEmpty());		
		assertTrue("Quotation Model what not created", !MALUtilities.isEmpty(quote.getQuotationModels()) && !quote.getQuotationModels().isEmpty());
		assertTrue("Quotation Standard Accessories were not created", !quote.getQuotationModels().get(0).getQuotationStandardAccessories().isEmpty());
		assertTrue("Quotation Model Accessories were not created", !quote.getQuotationModels().get(0).getQuotationModelAccessories().isEmpty());
		
	}	
	
	@Test
	public void testGetWillowReportURL() {

		List<WillowReportParamVO> params = new ArrayList<WillowReportParamVO>();
		
		params.add(new WillowReportParamVO("QUOID", String.valueOf(165510)));
		params.add(new WillowReportParamVO("QMDID", String.valueOf(224556)));
		params.add(new WillowReportParamVO("CID", String.valueOf(1)));
		params.add(new WillowReportParamVO("QPRID", String.valueOf(25746)));
		params.add(new WillowReportParamVO("REPORT_NAME", "OE_VLO"));
		params.add(new WillowReportParamVO("EFFECT_FROM_DATE", new SimpleDateFormat("dd-MMM-yyyy").format(new Date())));
		params.add(new WillowReportParamVO("MODULE", "FLQMR536"));
		
		String reportURL = null;
		
		try {
			reportURL = quotationService.getWillowReportUrl(params);
		} catch (MalBusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertNotNull("Report URL is null", reportURL);
	}
	
	@Test
	public void testValidateVehicleQuotationBeforePrinting() throws Exception {
		// Max Quote
		QuotationModel quotationModel = quotationService.getQuotationModel(224556l);
		List<String> errorList = quotationService.validateVehicleQuotationBeforePrinting(quotationModel);
		
		assertTrue("VQ/VLO cannot be generated for this product code.", errorList!= null && errorList.size() > 0);
		errorList.clear();
		
		quotationModel = quotationService.getQuotationModel(496707l);
		errorList = quotationService.validateVehicleQuotationBeforePrinting(quotationModel);
		assertTrue("is a new quote.", errorList== null || errorList.size() == 0);
		
	}
	

//	@Test
//	public void testGetVLOReport () throws Exception {
//		
//		QuotationModel quotationModel = null;
//		quotationModel = quotationService.getQuotationModel(310336l);
//		
//		byte[] buffer = quotationService.getVLOReport(quotationModel);
//		assertNotNull("Report  is null", buffer);
//	}
//	
//
//	@Test
//	public void testGetVQReport () throws Exception {
//		
//		QuotationModel quotationModel = null;
//		quotationModel = quotationService.getQuotationModel(310336l);
//		
//		byte[] buffer = quotationService.getVQReport(quotationModel);
//		assertNotNull("Report  is null", buffer);
//	}
//	
//
//	@Test
//	public void testGetVQSummaryReport () throws Exception {
//		
//		QuotationModel quotationModel = null;
//		quotationModel = quotationService.getQuotationModel(310336l);
//		List<QuotationModel> list = new ArrayList<QuotationModel>();
//		list.add(quotationModel);
//		byte[] buffer = quotationService.getVQSummaryReport(list);
//		assertNotNull("Report  is null", buffer);
//	}	

}
