package com.mikealbert.vision.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.Query;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.mikealbert.common.MalConstants;
import com.mikealbert.data.TestQueryConstants;
import com.mikealbert.data.dao.FleetMasterDAO;
import com.mikealbert.data.dao.MaintenanceRequestDAO;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.FleetNotes;
import com.mikealbert.data.entity.MaintenancePreferenceAccount;
import com.mikealbert.data.entity.MaintenanceRechargeCode;
import com.mikealbert.data.entity.MaintenanceRequest;
import com.mikealbert.data.entity.MaintenanceRequestTask;
import com.mikealbert.data.entity.RegionCode;
import com.mikealbert.data.entity.ServiceProvider;
import com.mikealbert.data.entity.ServiceProviderMaintenanceCode;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.data.vo.MaintenanceContactsVO;
import com.mikealbert.data.vo.MaintenancePreferencesVO;
import com.mikealbert.data.vo.MaintenanceProgramVO;
import com.mikealbert.data.vo.MaintenanceServiceHistoryVO;
import com.mikealbert.data.vo.VehicleInformationVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;
import com.mikealbert.service.DriverService;
import com.mikealbert.service.FleetMasterService;
import com.mikealbert.service.MaintenanceCodeService;
import com.mikealbert.service.MaintenanceHistoryService;
import com.mikealbert.service.MaintenanceRequestService;
import com.mikealbert.service.OdometerService;
import com.mikealbert.service.ServiceProviderService;
import com.mikealbert.service.util.email.Email;
import com.mikealbert.testing.BaseTest;
import com.mikealbert.util.MALUtilities;

public class VehicleMaintenanceServiceTest extends BaseTest{
	
	@Resource VehicleMaintenanceService maintReqService;
	@Resource MaintenanceCodeService maintenanceCodeService;
	@Resource MaintenanceRequestService maintenanceRequestService;
	@Resource MaintenanceRequestDAO maintReqDAO;
	@Resource FleetMasterService fleetMasterService;
	@Resource OdometerService odometerService;
	@Resource FleetMasterDAO fleetMasterDAO;
	@Resource DriverService driverService;
	@Resource MaintenanceCodeService maintCodeService;
	@Resource MaintenanceHistoryService maintenanceHistoryService;
	@Resource ServiceProviderService serviceProviderService;
	
	@Value("${maintenanceRequest.vin}")		String vin;
	
	final String PO_STATUS_BOOKED_IN = "B";
	final String PO_STATUS_COMPLETED = "C";	
	final String PO_TYPE_DEFAULT = "MAINT";
	final String ODO_UOM_DEFAULT = "MILE";
	final String LINE_CATEGORY_DEFAULT = "MISC_MAINT";
	final String USERNAME = "SETUP";	
	final String SPL_SERVICE_PROVIDER_CODE = "MC-COMDATA";
	final String SPL_PAYEE_NO = "00160419";
	final String taxExemptedCustNo = "00024275";
	final long FMS_ID_WITH_MAINT_PROG = 976078;
	
	@Test
	public void findMaintReqByMrqId(){
		assertNotNull(maintenanceRequestService.getMaintenanceRequestByMrqId(25L));
	}
	
	@Test
	public void findMaintReqByFmsId(){
		assertNotNull(maintenanceRequestService.getMaintenanceRequestByFmsId(968351L));
	}
	
	@Test
	public void findMaintReqByJobNo(){
		assertNotNull(maintenanceRequestService.getMaintenanceRequestByJobNo("J00307393"));
	}
	
	@Test
	public void testGetVehicleInformationByMrqId(){
		final Long MRQ_ID = 586044L;
		
		VehicleInformationVO vehInfo = maintReqService.getVehicleInformationByMrqId(MRQ_ID);
		
		assertNotNull("Did not retreive vehicle information VO for MRQ_ID " + MRQ_ID, vehInfo);
		
	}
	
	@Test
	public void testGetVehicleInformationByFmsId(){
		final Long FMS_ID = 973691L;
		
		VehicleInformationVO vehInfo = maintReqService.getVehicleInformationByFmsId(FMS_ID);
		
		assertNotNull("Did not retreive vehicle information VO for FMS_ID " + FMS_ID, vehInfo);
		
	}	
	
	@Test
	public void testGetMaintenanceServiceHistoryByVIN() {
		Sort sort = null;
		PageRequest page = null;
		List<MaintenanceServiceHistoryVO> maintServHistory = maintenanceHistoryService.getMaintenanceServiceHistoryByVIN(vin, page, sort, null, null, null);
	
		assertNotNull("Maintenance Service History VO not found for VIN " + vin, maintServHistory);
	}	
	
	@Test
	public void testApplyMarkUp(){
		Long mrqId = 694489L;
		BigDecimal markUp = new BigDecimal(1.00).setScale(2);
		BigDecimal markUpTotal = new BigDecimal(0.00).setScale(2);
		
		MaintenanceRequest po = maintenanceRequestService.getMaintenanceRequestByMrqId(mrqId);
		po = maintenanceRequestService.applyMarkUp(po, markUp);
		
		for(MaintenanceRequestTask line : po.getMaintenanceRequestTasks()){
			markUpTotal = markUpTotal.add(line.getMarkUpAmount());	
		}
		
		assertTrue("Markup calculation is incorrect " + markUpTotal.toString(), markUp.compareTo(markUpTotal) == 0);		
		
	}
	
	@Test
	public void testCalculatePOSubTotal(){
		Long mrqId = 694489L;
		BigDecimal subtotal = null;
		MaintenanceRequest po = maintenanceRequestService.getMaintenanceRequestByMrqId(mrqId);		
		subtotal = maintenanceRequestService.calculatePOSubTotal(po);
	
		assertTrue("Subtotal was not calcluated", subtotal.toString().equals("1151.04"));
	}
	
	//TODO Replace hard coded total w/ sum of markup
	@Test
	public void testCalculateMarkUp(){
		Long mrqId = 720111L;
		BigDecimal markupTotal = null;
		
		MaintenanceRequest po = maintenanceRequestService.getMaintenanceRequestByMrqId(mrqId);
		markupTotal = maintenanceRequestService.calculateNonNetworkRechargeMarkup(po);
		
		assertTrue("Markup sub total calculation is incorrect" + markupTotal, markupTotal.toString().equals("18.96"));
				
	}	
	
	@Test
	public void testGetDefaultMaintRechargeFlagForMrqWithMaintPrograms() {
		long mrqId = 691598; 
		String maintRechargeFlag = "Y";
		
		MaintenanceRequest mr = maintenanceRequestService.getMaintenanceRequestByMrqId(mrqId);
		String flag = maintReqService.getDefaultMaintRechargeFlag(mr);
		
		assertEquals(maintRechargeFlag,flag);
	}
	
	@Test
	public void testGetDefaultMaintRechargeCodeForMrqWithMaintPrograms() {
		long mrqId = 691598;
		String maintRechargeCode = "MAINT_MGT";
		
		MaintenanceRequest mr = maintenanceRequestService.getMaintenanceRequestByMrqId(mrqId);
		MaintenanceRechargeCode mrc = maintReqService.getDefaultMaintRechargeCode(mr);

		assertEquals(maintRechargeCode,mrc.getCode());
	}
	
	@Test
	public void testGenerateJobNumber(){
		String sqlString = "select dnos.pre_fix||lpad(dnos.next_no, 8, 0) job_no from doc_nos dnos where dnos.c_id = " + CorporateEntity.MAL.getCorpId() + " and dnos.domain = 'FLMAINT'";
		String calculatedJobNo = (String) em.createNativeQuery(sqlString).getSingleResult();
		String jobNo = maintReqService.generateJobNumber(CorporateEntity.MAL);
		assertTrue(jobNo.length() == 9);
		assertEquals(jobNo, calculatedJobNo);
	}
	
	@Test(expected=MalBusinessException.class)
	public void testSaveOrUpdatePOValidation() throws MalBusinessException {
		MaintenanceRequest po = new MaintenanceRequest();
		maintenanceRequestService.saveOrUpdateMaintnenacePO(po, "SETUP");
	}
	
	@Test
	public void testGetByParentServiceCode() {
		Long selectedProviderId = 1L;
		String servicecode = "PM-01-015";
		String maintcode = "100-109";
		List<Long> serviceProviderIds = new ArrayList<Long>();
		serviceProviderIds.add(selectedProviderId);
		List<ServiceProviderMaintenanceCode> codes =  maintenanceCodeService.getServiceProviderMaintenanceCode(servicecode, serviceProviderIds,false);
		
		assertEquals(maintcode,codes.get(0).getMaintenanceCode().getCode());
	}
	
	@Test
	public void testGetByParentMafsCode() {
		Long selectedProviderId = 1L;
		String servicecode = "PM-01-015";
		String maintcode = "100-109";
		List<Long> serviceProviderIds = new ArrayList<Long>();
		serviceProviderIds.add(selectedProviderId);
		List<ServiceProviderMaintenanceCode> codes =  maintenanceCodeService.getServiceProviderMaintenanceByMafsCode(maintcode, serviceProviderIds,false);
		
		assertEquals(servicecode,codes.get(0).getCode());
	}
	
	@Test
	public void testSaveOrUpdateDelete() throws MalBusinessException {
		final Long MRQ_ID = 720111L;
		int count = 0;
		Long deletedMrtId = 0L;
		
		MaintenanceRequest po = maintenanceRequestService.getMaintenanceRequestByMrqId(MRQ_ID);
		count = po.getMaintenanceRequestTasks().size();
		deletedMrtId = po.getMaintenanceRequestTasks().get(0).getMrtId();
		po.getMaintenanceRequestTasks().remove(0);		
		
		em.clear();  //<<< Needed to detach PO from JPA session
		
		po = maintenanceRequestService.saveOrUpdateMaintnenacePO(po, "SETUP");
		
		assertTrue("PO line item did not delete correctly ", po.getMaintenanceRequestTasks().size() < count);
		
		for(MaintenanceRequestTask line : po.getMaintenanceRequestTasks()){
			assertTrue("PO line item was not deleted MRT_ID = " + deletedMrtId, !line.getMrtId().equals(deletedMrtId));
		}
	}
	
	@Test
	public void testSaveOrUpdate() throws MalBusinessException{
		final String UNIT_NO = "00942157";
		
		MaintenanceRequest po = null;	
		
		po = createPO(UNIT_NO, 0);
	    po = maintenanceRequestService.saveOrUpdateMaintnenacePO(po, USERNAME);		    
	    assertNotNull("Purchase order was not saved", po.getMrqId());	    
	    	    
	    po = createPO(UNIT_NO, 5);	    
	    po = maintenanceRequestService.saveOrUpdateMaintnenacePO(po, USERNAME);	    
	    assertNotNull("Purchase order line items were not saved", po.getMaintenanceRequestTasks().get(0).getMrtId());
	    	    
	}
	
    @Test
    public void testSaveOrUpdateCompletedPO()throws MalBusinessException{
		MaintenanceRequest po = null;	    	
		Object[] record = null;		
		String currUnitNo = null;
		
		try {
			record = (Object[])em.createNativeQuery(TestQueryConstants.READ_UNIT_NO_WITH_REPLACEMENT_UNIT).getSingleResult();		
		} catch (Exception e) {
		
		}
		if(record != null && record.length > 0){
			currUnitNo = (String)record[0];			
			po = createPO(currUnitNo, 5);   
			po = maintenanceRequestService.saveOrUpdateMaintnenacePO(po, USERNAME);	
		}
	    
	    //TODO Rewrite the test query to exclude where replacement unit and orig unit account codes do not match
	    /*
	    em.clear();	    
	    
	    po = maintReqService.getMaintenanceRequestByMrqId(po.getMrqId());
	    po.setMaintReqStatus(PO_STATUS_COMPLETED);	
	    po = maintenanceRequestService.saveOrUpdateMaintnenacePO(po, USERNAME);		    
	    assertNotNull("Replacement unit information was not captured on Completed PO " + po.getJobNo(), po.getReplacementUnitNo());		
	    */
    }	
		
    //TODO Rewrite the test query to exclude where replacement unit and orig unit account codes do not match
    @Ignore
	@Test
	public void testReplacementUnit(){
		Object[] record = null;		
		String currUnitNo = null;
		
		record = (Object[])em.createNativeQuery(TestQueryConstants.READ_UNIT_NO_WITH_REPLACEMENT_UNIT).getSingleResult();		
		currUnitNo = (String)record[0];	
		
		VehicleInformationVO vehInfo = maintReqService.getVehicleInformationByFmsId(fleetMasterService.findByUnitNo(currUnitNo).getFmsId());
		
		assertNotNull("Replacement vehicle was not found for unit: " + currUnitNo, vehInfo.getReplacementUnitNo());
	}
	
	@Test
	public void testIsTaskItemListModified(){
		MaintenanceRequest maintReq = maintenanceRequestService.getMaintenanceRequestByJobNo("J00307393");
		List<MaintenanceRequestTask> maintReqTasks = maintReq.getMaintenanceRequestTasks();
		//List<MaintenanceRequestTask> modifiedMaintReqTasks = maintReqService.copyList(maintReqTasks);
		List<MaintenanceRequestTask> modifiedMaintReqTasks = maintReqService.copyMaintenanceRequest(maintReq).getMaintenanceRequestTasks();
		if (modifiedMaintReqTasks.size() > 0) {
			modifiedMaintReqTasks.get(0).setUnitCost(new BigDecimal(500));
		}
		assertTrue(maintReqService.isTaskItemListModified(maintReqTasks, modifiedMaintReqTasks));
	}
	
	@Test
	public void testSearchByMaintenanceRequestId() {
		List<FleetNotes> fleetNotesList = maintReqService.getFleetNotesByMaintReqId(531861L);
		assertTrue(fleetNotesList.size() > 0);
	}
	
	@SuppressWarnings("deprecation")
	private MaintenanceRequest createPO(String unitNo, int numberOfLines){
		final String MAFS_SERVICE_CODE = "100-101";
		final Long SUPPLIER_ID = 20676L;
		
		em.clear();
		
		List<MaintenanceRequestTask> lines = new ArrayList<MaintenanceRequestTask>();				
		MaintenanceRequest po = new MaintenanceRequest();				
		
		po.setFleetMaster(fleetMasterService.findByUnitNo(unitNo));
		po.setJobNo(maintReqService.generateJobNumber(CorporateEntity.MAL));
		po.setServiceProvider(serviceProviderService.getServiceProvider(SUPPLIER_ID));
		po.setMaintReqStatus(PO_STATUS_BOOKED_IN);
		po.setMaintReqType(PO_TYPE_DEFAULT);
	    po.setCurrentOdo(100L);
	    po.setActualStartDate(Calendar.getInstance().getTime());
	    po.setPlannedEndDate(Calendar.getInstance().getTime());
	    po.setUnitofMeasureCode(odometerService.convertOdoUOMCode(ODO_UOM_DEFAULT));
	    po.setMaintenanceRequestTasks(new ArrayList<MaintenanceRequestTask>());	
	    po.setLastChangedDate(Calendar.getInstance().getTime());
	    po.setServiceProviderMarkupInd(MalConstants.FLAG_N);
	    
	    for(int i = 0; i < numberOfLines; i++){
	    	MaintenanceRequestTask line = new MaintenanceRequestTask();
	    	line.setMaintCatCode(LINE_CATEGORY_DEFAULT);
	    	line.setMaintenanceCode(maintCodeService.getExactMaintenanceCodeByNameOrCode(MAFS_SERVICE_CODE));
	    	line.setLineNumber(i + 1L);	    	
	    	line.setTaskQty(new BigDecimal(2));
	    	line.setUnitCost(new BigDecimal("1.00"));
	    	line.setTotalCost(new BigDecimal("2.00"));
	    	line.setRechargeTotalCost(new BigDecimal("0.00"));
	    	line.setMarkUpAmount(new BigDecimal("0.00"));
	    	line.setDiscountFlag(MalConstants.FLAG_Y);
	    	line.setRechargeFlag(MalConstants.FLAG_N);
	    	line.setOutstanding(MalConstants.FLAG_N);
	    	line.setWasOutstanding(MalConstants.FLAG_N);
	    	line.setMaintenanceRequest(po);
	    	line.setAuthorizePerson(USERNAME);
	    	line.setAuthorizeDate(Calendar.getInstance().getTime());
	    	
	    	lines.add(line);	    	
	    }
	    
	    //TODO Need to discuss copyList with the team. The only way I can
	    //save lines is to make use of copyList. Otherwise, I get a stale object
	    //exception. Not sure this is the approach we want to take.	    
	    po.setMaintenanceRequestTasks(lines);  
	    	    
		return po;
	}
		
	@Test
	public void testGetTaxExemptedRegions(){
		try {
			List<RegionCode> regions = maintReqService.getTaxExemptedRegions(CorporateEntity.MAL.getCorpId(), "C", taxExemptedCustNo);

			assertNotNull(regions);
			assertTrue("No. of tax exempted regions for this customer should be greater than zero", regions.size() > 0);
			assertTrue("No. of tax exempted regions for this customer should be equal to 4", regions.size() == 4);
			
		} catch (MalException malEx) {
			malEx.printStackTrace();
		}
	}
	
	@Test
	public void testCreateGoodwillPurchaseOrder() throws MalBusinessException{
		MaintenanceRequest po = null;
		MaintenanceRequest goodwillPO = null;	    		
		String jobNo;
		
		Query query = em.createNativeQuery(TestQueryConstants.READ_MAINTENANCE_REQUEST_JOB_NO);
		query.setParameter(1, PO_STATUS_COMPLETED);
		jobNo = (String)query.getSingleResult();
		
		po = maintenanceRequestService.getMaintenanceRequestByJobNo(jobNo);
		goodwillPO = maintenanceRequestService.createGoodwillMaintenanceRequest(po, USERNAME);
		
		//PO level checks
		assertNotNull("Goodwill PO was not created", goodwillPO.getMrqId());
		assertFalse("Goodwill PO was not created from orignal PO", po.getJobNo().equals(goodwillPO.getJobNo()));
		assertNotNull("Link between originating and goodwill PO does not exist", goodwillPO.getMrqMrqId());
		
		//Task level checks
		for(MaintenanceRequestTask task : goodwillPO.getMaintenanceRequestTasks()){
			assertTrue("Goodwill PO task's unit price is not $0 mrtId:" + task.getMrtId(), task.getUnitCost().equals(new BigDecimal(0)));
			assertTrue("Goodwill PO task's total price is not $0 mrtId:" + task.getMrtId(), task.getTotalCost().equals(new BigDecimal(0)));	
		}				
				
	}	
	
	@Test
	public void testGetServiceCodesByCodeOrDesc(){
		List<ServiceProviderMaintenanceCode> serivceCodes = null;
    	String serviceCode = "OC-01-110";
    	//String maintenanceCode = "110-301";
    	//String maintCatCode = "OIL_CHANGE";
    	Long serviceProviderId = 1L;
    	ServiceProviderMaintenanceCode selectedServiceMaintCode = null;
    	
    	if(!MALUtilities.isEmpty(serviceProviderId) && serviceProviderId != 0){
    		serivceCodes = maintReqService.getServiceCodesByCodeOrDesc(serviceCode, null, null, serviceProviderId);
    		//serivceCodes = maintReqService.getServiceCodesByCodeOrDesc(serviceCode, maintenanceCode, maintCatCode, serviceProviderId);
    	}
    	if(serivceCodes != null && serivceCodes.size() == 1){
    		selectedServiceMaintCode = serivceCodes.get(0);
		}
    	
    	Assert.assertNotNull(selectedServiceMaintCode);
   
	}	
	
	@Test
	public void testGetMaintenancePreferenceAccount(){
		FleetMaster fm = fleetMasterDAO.findById(FMS_ID_WITH_MAINT_PROG).orElse(null);
		VehicleInformationVO vehInfo = maintReqService.getVehicleInformationByFmsId(fm.getFmsId());			
		
		MaintenancePreferenceAccount maintPrefAcct = maintReqService.getMaintenancePreferenceAccount(vehInfo);
		assertNotNull("No maintenance preference account data found",maintPrefAcct);
	}
	
	@Test
	public void testGetMaintenancePrograms(){
		FleetMaster fm = fleetMasterDAO.findById(FMS_ID_WITH_MAINT_PROG).orElse(null);
		VehicleInformationVO vehInfo = maintReqService.getVehicleInformationByFmsId(fm.getFmsId());		
		
		List<MaintenanceProgramVO> maintPrograms = maintReqService.getMaintenancePrograms(vehInfo);
		assertNotNull("No maintenance programs found",maintPrograms);
	}
	
	@Test
	public void testGetMaintenancePreferences(){
		FleetMaster fm = fleetMasterDAO.findById(FMS_ID_WITH_MAINT_PROG).orElse(null);
		VehicleInformationVO vehInfo = maintReqService.getVehicleInformationByFmsId(fm.getFmsId());		
		
		List<MaintenancePreferencesVO> maintPreferences = maintReqService.getMaintenancePreferences(vehInfo);
		assertNotNull("No maintenance preferences found",maintPreferences);
	}
		
	@Test
	public void testGenerateExceededAuthMsg(){
		FleetMaster fm = fleetMasterDAO.findById(FMS_ID_WITH_MAINT_PROG).orElse(null);
		VehicleInformationVO vehInfo = maintReqService.getVehicleInformationByFmsId(fm.getFmsId());
		
		String msg = "";
		try {
			msg = maintReqService.generateExceededAuthMsg(vehInfo);
		} catch (MalBusinessException e) {
			e.printStackTrace();
		}
		assertNotNull("Message failed to generate", msg);
	}	
	
	@Test
	public void testGenerateExceededAuthEmailSubject(){
		FleetMaster fm = fleetMasterDAO.findById(FMS_ID_WITH_MAINT_PROG).orElse(null);
		
		String msg = maintReqService.generateExceededAuthEmailSubject(fm);
		assertNotNull("Message failed to generate",msg);
	}	
	
	@Test
	public void testGenerateExceededAuthEmailBody(){
		MaintenanceRequest mrq = maintenanceRequestService.getMaintenanceRequestByJobNo("J00307393");

		String msg = maintReqService.generateExceededAuthEmailBody(mrq);
		System.out.println("msg: " + msg);
		assertNotNull("Message failed to generate",msg);
	}	
	
	@Test
	public void testGenerateExceededAuthEmail(){
		MaintenanceRequest mrq = maintenanceRequestService.getMaintenanceRequestByJobNo("J00349778");
		
		Email email = maintReqService.generateExceededAuthEmail(mrq,1L);
		assertNotNull("Message failed to generate",email.getMessage());
	
	
	}
	
	@Test
	public void testGenerateExceededAuthEmailBodyNotOnContract(){
		MaintenanceRequest mrq = maintenanceRequestService.getMaintenanceRequestByJobNo("J00349778");

		String msg = maintReqService.generateExceededAuthEmailBody(mrq);
		System.out.println("msg: " + msg);
		assertNotNull("Message failed to generate",msg);
	
	
	}	
	
	

	@Test
	public void testGenerateExceededAuthSummary(){
		
		FleetMaster fm = fleetMasterDAO.findById(FMS_ID_WITH_MAINT_PROG).orElse(null);
		
		String summary = maintReqService.generateExceededAuthSummary(CorporateEntity.MAL, "C", "00023509", fm);
		System.out.println(summary);
		assertNotNull("Summary failed to generate",summary);
	}
	
}
