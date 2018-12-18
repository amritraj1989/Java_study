package com.mikealbert.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import javax.annotation.Resource;
import javax.persistence.Query;
import junit.framework.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import com.mikealbert.data.DataConstants;
import com.mikealbert.data.TestQueryConstants;
import com.mikealbert.data.dao.BuyerLimitDAO;
import com.mikealbert.data.dao.DocDAO;
import com.mikealbert.data.dao.DoclDAO;
import com.mikealbert.data.dao.FleetMasterDAO;
import com.mikealbert.data.dao.MaintenanceInvoiceDAO;
import com.mikealbert.data.entity.BuyerLimit;
import com.mikealbert.data.entity.BuyerLimitPK;
import com.mikealbert.data.entity.Doc;
import com.mikealbert.data.entity.Docl;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.FleetNotes;
import com.mikealbert.data.entity.MaintenanceCode;
import com.mikealbert.data.entity.MaintenanceRequest;
import com.mikealbert.data.entity.MaintenanceRequestTask;
import com.mikealbert.data.entity.MaintenanceRequestUser;
import com.mikealbert.data.entity.ServiceProvider;
import com.mikealbert.data.entity.UomCode;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.data.enumeration.DocumentStatus;
import com.mikealbert.data.enumeration.MaintenanceRequestStatusEnum;
import com.mikealbert.data.vo.MaintenanceContactsVO;
import com.mikealbert.data.vo.MaintenanceInvoiceCreditVO;
import com.mikealbert.data.vo.ProgressChasingQueueVO;
import com.mikealbert.data.vo.ProgressChasingVO;
import com.mikealbert.data.vo.VehicleInformationVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.testing.BaseTest;
import com.mikealbert.util.MALUtilities;

public class MaintenanceRequestServiceTest extends BaseTest{
	@Resource MaintenanceRequestService maintRequestService;
	@Resource FleetMasterDAO fleetMasterDAO;
	@Resource DocDAO docDAO;
	@Resource BuyerLimitDAO buyerLimitDAO;
	@Resource FleetMasterService fleetMasterService;
	@Resource OdometerService odometerService;
	@Resource ServiceProviderService serviceProviderService;
	@Resource MaintenanceInvoiceService maintenanceInvoiceService;
	@Resource MaintenanceInvoiceDAO maintenanceInvoiceDAO;
	@Resource DoclDAO doclDAO;
	
	final String USERNAME = "SETUP";
	final String UPDATE_REASON_PRICE = "PRICE";
    static final String MAINT_REQUEST_STATUS_BOOKED_IN = "B";
    static final Long MRQ_ID_WTIH_CREDIT = 600256L;
    
	@Value("${maintenanceRequest.bookedIn.tasks}")  long oldBookedInMrqId;
    
	@Test
	public void testCreateMaintRequestUserLog(){
		MaintenanceRequest maintReq = null;
		MaintenanceRequestUser maintenanceRequestUserLog = null;
		String jobNo;
		
		Query query = em.createNativeQuery(TestQueryConstants.READ_MAINTENANCE_REQUEST_JOB_NO);		
		query.setParameter(1, MaintenanceRequestStatusEnum.MAINT_REQUEST_STATUS_BOOKED_IN.getCode());		
		while(MALUtilities.isEmpty(maintReq) 
				|| (MALUtilities.isEmpty(maintReq.getMaintenanceRequestTasks()) || maintReq.getMaintenanceRequestTasks().size() < 1)){
			jobNo = (String)query.getSingleResult();
			maintReq = maintRequestService.getMaintenanceRequestByJobNo(jobNo);
		}
	}
	
	@Test
	public void testValidateFleetMaintAuthBuyerLimit(){
		MaintenanceRequest mr = new MaintenanceRequest();
		List<MaintenanceRequestTask> mrtList = new ArrayList<MaintenanceRequestTask>();
		MaintenanceRequestTask mrt = new MaintenanceRequestTask();
		mrt.setTotalCost(new BigDecimal("1000000"));
		mrt.setMaintenanceCode(new MaintenanceCode());
		mrt.getMaintenanceCode().setCode("850-125");
		mrtList.add(mrt);
		mr.setMaintenanceRequestTasks(mrtList);
		mr.setFleetMaster(fleetMasterDAO.findById(1013691L).orElse(null));
		
		String message = maintRequestService.validateFleetMaintAuthBuyerLimit(mr, CorporateEntity.MAL, "GRAHAM_T");
		assertFalse(message.isEmpty());
	}
	
	@Test
	public void testValidateNotNullActualStart(){
		MaintenanceRequest mr = new MaintenanceRequest();
		mr.setActualStartDate(null);
		
		String message = maintRequestService.notNullActualStart(mr);
		System.out.println(message);
		assertFalse(message.isEmpty());
	}
	
	@Test
	public void testValidateNotNullActualEnd(){
		MaintenanceRequest mr = new MaintenanceRequest();
		mr.setActualEndDate(null);
		
		String message = maintRequestService.notNullEndDate(mr);
		System.out.println(message);
		assertFalse(message.isEmpty());
	}
	
	@Test
	public void testValidateStatusBeforeFutureStatus(){
		MaintenanceRequest mr = new MaintenanceRequest();
		mr.setMaintReqStatus(MaintenanceRequestStatusEnum.MAINT_REQUEST_STATUS_BOOKED_IN.getCode());
		
		String message = maintRequestService.validateStatusBeforeFutureStatus(mr, MaintenanceRequestStatusEnum.MAINT_REQUEST_STATUS_INPROGRESS.getCode());
		assertTrue(message.isEmpty());
	}
	
	@Test
	public void testValidateZeroTasks(){
		MaintenanceRequest mr = new MaintenanceRequest();
		
		String message = maintRequestService.validateZeroTasks(mr);
		assertFalse(message.isEmpty());
	}
	
	@Test
	public void testValidateOffContractNoRecharge(){
		MaintenanceRequest mr = new MaintenanceRequest();
		List<MaintenanceRequestTask> mrtList = new ArrayList<MaintenanceRequestTask>();
		MaintenanceRequestTask mrt = new MaintenanceRequestTask();
		mrt.setRechargeFlag("Y");
		mrtList.add(mrt);
		mr.setMaintenanceRequestTasks(mrtList);
		mr.setFleetMaster(fleetMasterDAO.findById(16662L).orElse(null));
		mr.setActualStartDate(Calendar.getInstance().getTime());
		
		String message = maintRequestService.validateOffContractNoRecharge(mr);
		assertFalse(message.isEmpty());
	}
	
	//@Test
	@Ignore
	public void testCompletePO() throws MalBusinessException{
		MaintenanceRequest mrq = null;
		Doc releasedDoc = null;
		String jobNo = null;
		
		Query query = em.createNativeQuery(TestQueryConstants.READ_MAINTENANCE_REQUEST_JOB_NO);		
		query.setParameter(1, MaintenanceRequestStatusEnum.MAINT_REQUEST_STATUS_BOOKED_IN.getCode()); 
		
		while(MALUtilities.isEmpty(mrq) 
				|| MALUtilities.isEmpty(mrq.getMaintenanceRequestTasks()) 
				|| mrq.getMaintenanceRequestTasks().size() < 1
				|| MALUtilities.isEmpty(mrq.getActualStartDate())
				|| MALUtilities.isEmpty(mrq.getPlannedEndDate())){
			jobNo = (String)query.getSingleResult();
			mrq = maintRequestService.getMaintenanceRequestByJobNo(jobNo);
		}
		
		setupBuyerLimits(1000000D);
		
		maintRequestService.completeMRQ(mrq, CorporateEntity.MAL, USERNAME);				
		
		for(Doc doc : docDAO.findPurchaseOrderForMaintReq(mrq.getMrqId())){
			if(doc.getDocStatus().equals(DocumentStatus.PURCHASE_ORDER_STATUS_RELEASED.getCode())){
				releasedDoc = doc;
				break;
			}
		}		
			
		assertTrue("Purchase Order document was not created for job " + jobNo, releasedDoc != null && releasedDoc.getDocNo().equals(jobNo));		
		
						
	}
	
	private void setupBuyerLimits(Double amount){
		BuyerLimitPK buyerLimitPK = new BuyerLimitPK();
		buyerLimitPK.setEmployeeNo(USERNAME);
		buyerLimitPK.setcId(1L);
		buyerLimitPK.setDocType("PORDER");
		buyerLimitPK.setTranType("FLMAINT");
		
		BuyerLimit buyerLimit = new BuyerLimit(buyerLimitPK);
		buyerLimit.setAuthorizeAmount(new BigDecimal(amount));
		buyerLimit.setChangePercent(null);
		buyerLimit.setOriginateAmount(null);
		buyerLimit.setReleaseAmount(new BigDecimal(amount));
		
		buyerLimitDAO.saveAndFlush(buyerLimit);
	}
	
	@Test
	public void testValidateRechargeExceedsLimit() throws MalBusinessException{
		MaintenanceRequest po = buildPO("00963979");
		//String message = maintRequestService.validateRechargeExceedsLimit(po);
		boolean isWithinLimit = maintRequestService.isRechargeTotalWithinLimit(po);
		//Assert.assertTrue(!isWithinLimit);
	}
	
	@Test
	public void testValidateRechargeExceedsLimitAndDBAmount() throws MalBusinessException{
		MaintenanceRequest po = buildPO("00963979");
		po.setAuthReference("xx");
		po.setMrqId(633806L);
		List<MaintenanceRequestTask> mrtList = po.getMaintenanceRequestTasks();
		MaintenanceRequestTask mrt = new MaintenanceRequestTask();
		mrt.setRechargeTotalCost(new BigDecimal("100000"));
		mrtList.add(mrt);
		po.setMaintenanceRequestTasks(mrtList);
		
		boolean isWithinLimit = maintRequestService.isRechargeTotalWithinLimit(po);
		
		//Assert.assertTrue(!isWithinLimit);
	}
	
	private MaintenanceRequest buildPO(String unitNo){
    	Calendar calendar = Calendar.getInstance();
    	MaintenanceRequest po = new MaintenanceRequest();
    	po.setMaintenanceRequestTasks(new ArrayList<MaintenanceRequestTask>());
    	po.setFleetMaster(fleetMasterService.findByUnitNo(unitNo));
    	po.setMaintReqStatus("B");
    	po.setMaintReqType("MAINT");
    	po.setActualStartDate(calendar.getTime());
    	calendar.add(Calendar.DAY_OF_MONTH, 1);    	
    	po.setPlannedEndDate(calendar.getTime()); 
    	po.setJobNo(maintRequestService.generateJobNumber(CorporateEntity.MAL));    	
    	PageRequest page = new PageRequest(0,1);
    	List<ServiceProvider> serviceProviders = serviceProviderService.getServiceProviderByNameOrCode("00086050",page);
    	po.setServiceProvider(serviceProviders.get(0));
    	po.setCurrentOdo(34162L);
    	UomCode uomCode = odometerService.convertOdoUOMCode("MILE");
    	po.setUnitofMeasureCode(uomCode);
		po.setMaintReqStatus(MAINT_REQUEST_STATUS_BOOKED_IN);
		
		buildPOTaskItem(po, USERNAME);
		
    	return po;
	}
	
	private void buildPOTaskItem(MaintenanceRequest po, String username){
		
		MaintenanceRequestTask maintReqTask = new MaintenanceRequestTask(); 
		Long lineNumber = 0l;
		lineNumber = MALUtilities.isEmpty(po.getMaintenanceRequestTasks()) ? 1l : po.getMaintenanceRequestTasks().size() + 1l;
		
		maintReqTask = new MaintenanceRequestTask();
		maintReqTask.setMaintenanceRequest(po);		
		maintReqTask.setRechargeFlag("Y");		
		maintReqTask.setRechargeCode("MAINT_MGT");	
		maintReqTask.setRechargeQty(new BigDecimal(1));
		maintReqTask.setRechargeUnitCost(new BigDecimal(1000));
		maintReqTask.setRechargeTotalCost(new BigDecimal(1000));
		maintReqTask.setOutstanding(DataConstants.DEFAULT_N);
		maintReqTask.setWasOutstanding(DataConstants.DEFAULT_N);
		maintReqTask.setLineNumber(lineNumber);
		maintReqTask.setIndex(lineNumber.intValue());				
		maintReqTask.setAuthorizePerson(username);
		if (!MALUtilities.isEmptyString(po.getServiceProvider().getNetworkVendor())){
			maintReqTask.setDiscountFlag(po.getServiceProvider().getNetworkVendor());
		}else{
			maintReqTask.setDiscountFlag(DataConstants.DEFAULT_N);
		}
		String sqlString = "select * from MAINTENANCE_CODES where MAINT_CODE = '500-102'";
		List<MaintenanceCode> maintenanceCodes = em.createNativeQuery(sqlString, MaintenanceCode.class).getResultList(); 
		maintReqTask.setMaintenanceCode(maintenanceCodes.get(0));
		maintReqTask.setTaskQty(new BigDecimal(1));
		maintReqTask.setUnitCost(new BigDecimal(1500));
		maintReqTask.setTotalCost(new BigDecimal(1500));
		maintReqTask.setMaintCatCode("A/C");
		
		po.getMaintenanceRequestTasks().add(maintReqTask);
				
	}
	
	
	@Test
	public void testGetProgressChasingDataList(){
		List<ProgressChasingQueueVO> progressChasingQueueVO =  maintRequestService.getProgressChasingDataList();
		
		assertNotNull("No progress chasing records found",progressChasingQueueVO);
	}	
	
	@Ignore
	public void testGetProgressChasingByPoStatus(){
		PageRequest page = null;
		String poStatus = MAINT_REQUEST_STATUS_BOOKED_IN;

		List<ProgressChasingVO> progressChasingVOList = maintRequestService.getProgressChasingByPoStatus(poStatus, page, null);
		assertNotNull("Progress Chasing not found for PO Status " + poStatus, progressChasingVOList);
	}
	
	@Test
	public void testGetMafsInvoiceNo() {
		long mrqId = 558305;		
		
		String mafsInvoiceNumber = maintRequestService.getMafsInvoiceNo(mrqId);
		assertNotNull("Could not find mafs invoice number for mrqId " + mrqId, mafsInvoiceNumber);
	}	
	
	@Test 
	public void testGetPayeeInvoiceNo() {
		long mrqId = 691598;
		
		String payeeInvoiceNumber = maintRequestService.getPayeeInvoiceNo(mrqId);
		assertNotNull("Could not find Payee Invoice Number for mrqId " + mrqId, payeeInvoiceNumber);
	}	
	
	@Test
	public void testGetFleetNotesByFmsId(){
		long fmsId = 222812;
		List<FleetMaster> fleetMasters = new ArrayList<FleetMaster>();
		fleetMasters.add(new FleetMaster());
		fleetMasters.get(0).setFmsId(fmsId);
		
		List<FleetNotes> fleetNotesList = maintRequestService.mashUpFleetNotes(fleetMasters);
		assertTrue(fleetNotesList.size() > 0);
	}
	
	@Test
	public void testUpdateMaintenancePO(){
		MaintenanceRequest fromDBPO1 = maintRequestService.getMaintenanceRequestByMrqId(714775L);
		fromDBPO1.setCurrentOdo(120L);
		try{
			maintRequestService.saveOrUpdateMaintnenacePO(fromDBPO1, USERNAME);
			em.clear();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		MaintenanceRequest fromDBPO2 = maintRequestService.getMaintenanceRequestByMrqId(714775L);
		Assert.assertTrue(fromDBPO2.getOdometerReadings().get(0).getReading() == 120);
	}
	
	@Test
	public void testCancelAuthorization(){
		String jobNo;
		MaintenanceRequest fromDBPO1 = null;
		
		Query query = em.createNativeQuery(TestQueryConstants.READ_MAINTENANCE_REQUEST_JOB_NO);		
		query.setParameter(1, MaintenanceRequestStatusEnum.MAINT_REQUEST_STATUS_COMPLETE.getCode());				
		
		while(MALUtilities.isEmpty(fromDBPO1) 
				|| MALUtilities.isEmpty(fromDBPO1.getMaintenanceRequestTasks()) 
				|| fromDBPO1.getMaintenanceRequestTasks().size() < 1
				|| MALUtilities.isEmpty(fromDBPO1.getActualStartDate())
				|| MALUtilities.isEmpty(fromDBPO1.getPlannedEndDate())
				|| maintenanceInvoiceService.hasPostedInvoice(fromDBPO1)){
			jobNo = (String)query.getSingleResult();
			fromDBPO1 = maintRequestService.getMaintenanceRequestByJobNo(jobNo);
		}
		
		
		try{
			maintRequestService.cancelAuthorization(fromDBPO1, CorporateEntity.MAL, USERNAME);
		}catch(Exception ex){
			Assert.fail("Could not cancel authorization. " + ex.getMessage());
		}
	}
	
	@Ignore
	public void testCloseNoPO() throws MalBusinessException{
		MaintenanceRequest mrq = null;				
		
		mrq = maintRequestService.getMaintenanceRequestByMrqId(oldBookedInMrqId);
		maintRequestService.closeJob(mrq, CorporateEntity.MAL, USERNAME);
		
		assertTrue("Failed to close Maintenance Request", mrq.getMaintReqStatus().equals(MaintenanceRequestStatusEnum.MAINT_REQUEST_STATUS_CLOSED_NO_PO.getCode()));
	}
	
	@Test
	public void testGetMaintenanceCreditAP(){
		MaintenanceRequest mrq = maintRequestService.getMaintenanceRequestByMrqId(MRQ_ID_WTIH_CREDIT);		
		List<Doc> creditAPDocs = maintRequestService.getMaintenanceCreditAP(mrq);
		
		assertTrue("Could not find creditap docs for mrqId " + MRQ_ID_WTIH_CREDIT, creditAPDocs.size() > 0);
	}
	
	@Test
	public void testGetMaintenanceCreditAPLines(){
		MaintenanceRequest mrq = maintRequestService.getMaintenanceRequestByMrqId(MRQ_ID_WTIH_CREDIT);
		List<MaintenanceInvoiceCreditVO> invoiceCreditApList = maintRequestService.getMaintenanceCreditAPLines(mrq);
		
		assertTrue("Could not find creditap lines for mrqId " + MRQ_ID_WTIH_CREDIT, invoiceCreditApList.size() > 0);
	}
	
	@Test
	public void testGetMaintenanceCreditARMarkupList(){
		MaintenanceRequest mrq = maintRequestService.getMaintenanceRequestByMrqId(MRQ_ID_WTIH_CREDIT);
		List<Docl> markupDoclList = maintRequestService.getMaintenanceCreditARMarkupList(mrq);
		
		assertTrue("Could not find creditar markup lines for mrqId " + MRQ_ID_WTIH_CREDIT, markupDoclList.size() > 0);
	}
	
	@Test
	public void testintializeMaintenanceCateogryProperties() throws MalBusinessException{
		MaintenanceRequest mrq = maintRequestService.getMaintenanceRequestByMrqId(128802L);
		for(MaintenanceRequestTask mrt : mrq.getMaintenanceRequestTasks()){
			mrt = maintRequestService.intializeMaintenanceCateogryProperties(mrt);
		}
		mrq = maintRequestService.saveOrUpdateMaintnenacePO(mrq, USERNAME);
		
		for(MaintenanceRequestTask mrt : mrq.getMaintenanceRequestTasks()){
			if(mrt.getMaintCatCode().equals("TIRE_SVCS")){
				assertNotNull("Maintenance category property values were not created and saved for mrt " + mrq.getMrqId(), 
						mrt.getMaintenanceCategoryPropertyValues().get(0).getMpvId());				
			}
		}

	}
	
	@Ignore
	public void testCreateVehicleRentalFeeLine()
	{
		//modify test as development continues
		String username = "OPITZ";
		Calendar cal = new GregorianCalendar();
		cal.set(2014, 4, 7);
		
		MaintenanceRequest mrq = new MaintenanceRequest();
		List<MaintenanceRequestTask> mrtList = new ArrayList<MaintenanceRequestTask>();
		MaintenanceRequestTask mrt = new MaintenanceRequestTask();
		mrt.setTotalCost(new BigDecimal("100"));
		mrt.setMaintenanceCode(new MaintenanceCode());
		mrt.getMaintenanceCode().setCode("700-100");
		mrtList.add(mrt);
		mrq.setMaintenanceRequestTasks(mrtList);
		mrq.setFleetMaster(fleetMasterDAO.findById(1013691L).orElse(null));
		mrq.setActualStartDate(cal.getTime());

		MaintenanceRequestTask mrt2 = new MaintenanceRequestTask();
		mrt2 = maintRequestService.createVehicleRentalFeeLine(mrq,username);

		assertNotNull("Could not find maint code fee for " + mrt2.getMaintenanceCode().getCode(), mrt2.getMaintenanceCode().getCode());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGetContacts(){
		List<MaintenanceContactsVO> maintenanceContactVOs;
	    Query query;		
	    List<Object[]> resultSinglRecord;
	    VehicleInformationVO vehicleInformationVO;	    
	    
	    query = em.createNativeQuery(TestQueryConstants.READ_FLEET_MASTER_AND_ACCOUNT_ON_CONTRACT);		
	    resultSinglRecord = query.getResultList();

		vehicleInformationVO = new VehicleInformationVO();
		vehicleInformationVO.setFmsId(((BigDecimal)resultSinglRecord.get(0)[0]).longValue());
		vehicleInformationVO.setClientCorporateId(((BigDecimal)resultSinglRecord.get(0)[1]).longValue());
		vehicleInformationVO.setClientAccountType((String)resultSinglRecord.get(0)[2]);
		vehicleInformationVO.setClientAccountNumber((String)resultSinglRecord.get(0)[3]);
		vehicleInformationVO.setClnId(0L);
		
		try {
			maintenanceContactVOs = maintRequestService.getContacts(ClientPOCService.POC_NAME_MAINT_EXCEED_AUTH_LIMIT, vehicleInformationVO);
			assertTrue("Could not find maintenance request contacts", !MALUtilities.isEmpty(maintenanceContactVOs));
			if(!MALUtilities.isEmpty(maintenanceContactVOs)){
				assertTrue("Contact was not retrieve", !MALUtilities.isEmpty(maintenanceContactVOs.get(0).getContactId()));			
			}
		} catch (MalBusinessException e) {
			e.printStackTrace();
		}
	}
}
