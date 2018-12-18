package com.mikealbert.service;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;

import com.mikealbert.data.dao.FleetMasterDAO;
import com.mikealbert.data.dao.MaintScheduleRuleDAO;
import com.mikealbert.data.dao.MaintSchedulesProcessedDAO;
import com.mikealbert.data.dao.MaintenancePreferenceAccountDAO;
import com.mikealbert.data.dao.MasterScheduleDAO;
import com.mikealbert.data.dao.ServiceTaskDAO;
import com.mikealbert.data.dao.VehicleScheduleDAO;
import com.mikealbert.data.entity.ClientScheduleType;
import com.mikealbert.data.entity.ContractLine;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.FuelGroupCode;
import com.mikealbert.data.entity.LeaseElement;
import com.mikealbert.data.entity.MaintScheduleRule;
import com.mikealbert.data.entity.MaintSchedulesProcessed;
import com.mikealbert.data.entity.MaintenancePreferenceAccount;
import com.mikealbert.data.entity.MasterSchedule;
import com.mikealbert.data.entity.MasterScheduleInterval;
import com.mikealbert.data.entity.ServiceTask;
import com.mikealbert.data.entity.VehicleSchedule;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalDataValidationException;
import com.mikealbert.testing.BaseTest;
import com.mikealbert.util.MALUtilities;

public class MaintenanceScheduleServiceTest extends BaseTest {

	@Resource
	private MaintenanceScheduleService maintenanceScheduleService;
	@Resource
	private ServiceTaskDAO serviceTaskDAO;
	@Resource
	private MasterScheduleDAO masterScheduleDAO;
	@Resource 
	private MaintScheduleRuleDAO maintScheduleRuleDAO;
	@Resource
	private FleetMasterDAO fleetMasterDAO;	
	@Resource
	private MaintenancePreferenceAccountDAO maintenancePreferenceAccountDAO;	
	@Resource 
	private ContractService contractService;	
	@Resource 
	private FuelService fuelService;
	@Resource
	private VehicleScheduleService vehicleScheduleService;
	@Resource
	private VehicleScheduleDAO vehicleScheduleDAO;
	@Resource
	private MaintSchedulesProcessedDAO maintSchedulesProcessedDAO;

    @PersistenceContext
	private EntityManager em;
    
    private Query query;
	
	@Test
	public void testCopyMasterScheduleWithUniqueMasterCode() throws Exception {
		MasterSchedule masterSchedule = null;
		ClientScheduleType clientScheduleType = new ClientScheduleType();
		clientScheduleType.setCstId(1001L);
		MasterScheduleInterval masterScheduleInterval = null;
		// Get any existing service task from database
		List<ServiceTask> ServiceTaskList = serviceTaskDAO.findAll();
		ServiceTask serviceTask = null;
		if (ServiceTaskList != null && !ServiceTaskList.isEmpty()) {
			for (ServiceTask st : ServiceTaskList) {
				if ("Y".equals(st.getActiveFlag())) {
					serviceTask = st;
					break;
				}
			}

		}
		masterSchedule = new MasterSchedule();
		masterSchedule.setDescription("Automated Test");
		masterSchedule.setHiddenFlag("N");
		masterSchedule.setClientScheduleType(clientScheduleType);
		masterSchedule.setMonthFrequency(6);
		masterSchedule.setDistanceFrequency(120);
		masterSchedule.setMasterCode("12000001");
		masterScheduleInterval = new MasterScheduleInterval();
		if (masterSchedule.getMasterScheduleIntervals() == null) {
			masterSchedule.setMasterScheduleIntervals(new ArrayList<MasterScheduleInterval>());
		}
		masterScheduleInterval.setMasterSchedule(masterSchedule);
		masterScheduleInterval.setInterval(5);
		masterScheduleInterval.setOrder(1);
		masterScheduleInterval.setRepeatFlag("Y");
		masterScheduleInterval.setServiceTask(serviceTask);
		masterSchedule.getMasterScheduleIntervals().add(masterScheduleInterval);
		maintenanceScheduleService.saveSchedule(masterSchedule);
		maintenanceScheduleService.copyMasterSchedule(masterSchedule);
	}

	@Test
	public void testCopyMasterScheduleWithDuplicateMasterCode() throws Exception {
		try {
			MasterSchedule masterSchedule = null;
			ClientScheduleType clientScheduleType = new ClientScheduleType();
			clientScheduleType.setCstId(1001L);
			MasterScheduleInterval masterScheduleInterval = null;
			// Get any existing service task from database
			List<ServiceTask> ServiceTaskList = serviceTaskDAO.findAll();
			ServiceTask serviceTask = null;
			if (ServiceTaskList != null && !ServiceTaskList.isEmpty()) {
				for (ServiceTask st : ServiceTaskList) {
					if ("Y".equals(st.getActiveFlag())) {
						serviceTask = st;
						break;
					}
				}

			}
			masterSchedule = new MasterSchedule();
			masterSchedule.setDescription("Automated Test");
			masterSchedule.setHiddenFlag("N");
			masterSchedule.setClientScheduleType(clientScheduleType);
			masterSchedule.setMonthFrequency(6);
			masterSchedule.setDistanceFrequency(120);
			masterSchedule.setMasterCode("120-001");
			masterScheduleInterval = new MasterScheduleInterval();
			if (masterSchedule.getMasterScheduleIntervals() == null) {
				masterSchedule.setMasterScheduleIntervals(new ArrayList<MasterScheduleInterval>());
			}
			masterScheduleInterval.setMasterSchedule(masterSchedule);
			masterScheduleInterval.setInterval(5);
			masterScheduleInterval.setOrder(1);
			masterScheduleInterval.setRepeatFlag("Y");
			masterScheduleInterval.setServiceTask(serviceTask);
			masterSchedule.getMasterScheduleIntervals().add(masterScheduleInterval);
			maintenanceScheduleService.saveSchedule(masterSchedule);
			maintenanceScheduleService.copyMasterSchedule(masterSchedule);
		} catch (Exception ex) {
			Assert.assertEquals("Master Schedule code already exists", ex.getMessage());
		}

	}

	@Test
	public void testGetNextMasterScheduleCode() {
		String nextSchedule = maintenanceScheduleService.getNextMasterScheduleCode(12000);
		List<MasterSchedule> schedules = masterScheduleDAO.getMasterSchedulesByCode(nextSchedule);
		if (schedules != null && !schedules.isEmpty()) {
			Assert.assertEquals(0, schedules.size());
		}

	}

	@Ignore
	public void	testCreateVehicleSchedule(){
		MasterSchedule masterSchedule = masterScheduleDAO.findById(6L).orElse(null);
		FleetMaster fleetMaster = new FleetMaster(1050475L);
		// delete any previous vehicle schedules for this unit
		for(VehicleSchedule vs : vehicleScheduleDAO.getVehSchdByFmsId(fleetMaster.getFmsId())) {
			vehicleScheduleDAO.deleteById(vs.getVschId());
		}
		VehicleSchedule vehicleSchedule = maintenanceScheduleService.createVehicleSchedule(masterSchedule, fleetMaster);
		Assert.assertNotNull(vehicleSchedule);
	}
	
	@Ignore
	public void testGetMaintSchedRulesByActiveAndHighMileageAndClientSchedType(){
		String activeFlag = "Y";
		String highMileage = "N";
		FleetMaster fleetMaster = fleetMasterDAO.findById(1051170L).orElse(null);	
				
		ContractLine contractLine = contractService.getLastActiveContractLine(fleetMaster, Calendar.getInstance().getTime());
		ExternalAccount externalAccount = contractLine.getContract().getExternalAccount();
		
		MaintenancePreferenceAccount  mpa = maintenancePreferenceAccountDAO.getMaintenancePreferenceAccountData(externalAccount.getExternalAccountPK().getCId(), externalAccount.getExternalAccountPK().getAccountType(), externalAccount.getExternalAccountPK().getAccountCode());
		List<MaintScheduleRule> maintScheduleRules = maintenanceScheduleService.getMaintSchedRulesByActiveAndHighMileageAndClientSchedType(activeFlag,highMileage,mpa.getClientScheduleId(),externalAccount);
		
		Assert.assertTrue(maintScheduleRules.size() > 0);
	}	
	
	@Ignore
	public void testDetermineMasterSchedule(){
		Long fmsId = 1051170L;
		FleetMaster fleetMaster = fleetMasterDAO.findById(fmsId).orElse(null);	
		String activeFlag = "Y";
		String groupKey = "MSS";
		FuelGroupCode fuelGroupCode = fuelService.getFuelGroupCode(groupKey, fleetMaster);
		
		ContractLine contractLine = contractService.getLastActiveContractLine(fleetMaster, Calendar.getInstance().getTime());
		ExternalAccount externalAccount = contractLine.getContract().getExternalAccount();
		
		MaintenancePreferenceAccount  mpa = maintenancePreferenceAccountDAO.getMaintenancePreferenceAccountData(externalAccount.getExternalAccountPK().getCId(), externalAccount.getExternalAccountPK().getAccountType(), externalAccount.getExternalAccountPK().getAccountCode());
		
		String highMileage = "N";
		List<MaintScheduleRule> maintScheduleRules = maintScheduleRuleDAO.getRulesByActiveAndHighMileageAndClientSchedType(activeFlag,highMileage,mpa.getClientScheduleId(),externalAccount);
		MasterSchedule masterSchedule = maintenanceScheduleService.determineMasterSchedule(fleetMaster, maintScheduleRules, fuelGroupCode, highMileage);

		assertNotNull("Could not find master schedule for fmsId " + fmsId, masterSchedule.getMschId());
		
		highMileage = "Y";
		maintScheduleRules = maintScheduleRuleDAO.getRulesByActiveAndHighMileageAndClientSchedType(activeFlag,highMileage,mpa.getClientScheduleId(),externalAccount);
		masterSchedule = maintenanceScheduleService.determineMasterSchedule(fleetMaster, maintScheduleRules, fuelGroupCode, highMileage);

		assertNotNull("Could not find master schedule for fmsId " + fmsId, masterSchedule.getMschId());		
		
	}	
	
	@Ignore
	public void testDetermineMasterScheduleByFmsId() throws MalBusinessException, MalDataValidationException{
		Long fmsId = 1051170L;	
		MasterSchedule masterSchedule = maintenanceScheduleService.determineMasterScheduleByFmsId(fmsId);

		assertNotNull("Could not find master schedule for fmsId " + fmsId, masterSchedule.getMschId());
	}

	@Ignore
	public void testDetermineExpectedPrintDateWithInServiceDate() throws Exception {
		String stmt = "select msp.msp_id from supplier_progress_history sph, dist dst, fleet_masters fm, maint_schedules_processed msp, doc d " +
		"where sph.progress_type = '24_IN-SERV' " + 
		"and sph.doc_id = dst.doc_id " +
		"and dst.cdb_code_1 = fm.fms_id " +
		"and fm.fms_id = msp.fms_fms_id " +
		"and dst.doc_id = d.doc_id " +
		"and d.doc_type = 'PORDER' " +
		"and d.source_code in('FLQUOTE','FLORDER') " +
		"and d.doc_status = 'R' " +
		"and NVL(order_type,'M') != 'T' " +
		"and rownum=1";
		try {
			query = em.createNativeQuery(stmt);
			BigDecimal id = (BigDecimal) query.getSingleResult();
			MaintSchedulesProcessed processRecord = maintSchedulesProcessedDAO.findById(id.longValue()).orElse(null);
			processRecord.setExpectedPrintDate(null);
			maintenanceScheduleService.determineExpectedPrintDate(processRecord);
			assertEquals(MALUtilities.clearTimeFromDate(processRecord.getExpectedPrintDate()), 
						MALUtilities.clearTimeFromDate(new Date()));
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	@Ignore
	public void testDetermineExpectedPrintDateWithOnlyReadyDate() throws Exception {
		String stmt = "select msp.msp_id from supplier_progress_history sph, dist dst, fleet_masters fm, maint_schedules_processed msp, doc d " +
		"where sph.progress_type = '15_VEHRDY' " + 
		"and sph.doc_id not in (select doc_id from supplier_progress_history sph2 where sph2.progress_type = '24_IN-SERV') " +
		"and sph.doc_id = dst.doc_id " +
		"and dst.cdb_code_1 = fm.fms_id " +
		"and fm.fms_id = msp.fms_fms_id " +
		"and dst.doc_id = d.doc_id " +
		"and d.doc_type = 'PORDER' " +
		"and d.source_code in('FLQUOTE','FLORDER') " +
		"and d.doc_status = 'R' " +
		"and NVL(order_type,'M') != 'T' " +
		"and rownum=1";
		try {
			query = em.createNativeQuery(stmt);
			BigDecimal id = (BigDecimal) query.getSingleResult();
			MaintSchedulesProcessed processRecord = maintSchedulesProcessedDAO.findById(id.longValue()).orElse(null);
			processRecord.setExpectedPrintDate(null);
			maintenanceScheduleService.determineExpectedPrintDate(processRecord);
			assertEquals(processRecord.getExpectedPrintDate(), null);

		} catch (Exception e) {
			System.out.println(e);
		}

	}


	
	
	
	@Test
	public void testFindAllMaintenanceLeaseElements() throws Exception {
		List<LeaseElement> leaseElementsList = maintenanceScheduleService.getAllMaintenanceLeaseElements();
		
		Assert.assertTrue(leaseElementsList.size() > 0);
		
	}
	
	
	@Test
	public void testGetIntervalIndexMileage() throws Exception {

		MasterSchedule ms = masterScheduleDAO.findById(42l).orElse(null);
		
		assertEquals(maintenanceScheduleService.getIntervalIndexMileage(0, ms, 1), 5000); 
		assertEquals(maintenanceScheduleService.getIntervalIndexMileage(1, ms, 1), 10000); 
		

		
	}

	

	
//	@Test
//	public void testAssignment() throws Exception {
//
//		System.out.println("started");
//		int total=0;
//		int matches=0;
//		int nonMatches =0;
//		int errors = 0;
//		List<VehicleSchedule> list = vehicleScheduleDAO.getNoNConversionVehicleSchedules();
//		total= list.size();
//		for(VehicleSchedule vs : list) {
//			Long id = vs.getMasterSchedule().getMschId();
//			try {
//				MasterSchedule ms = maintenanceScheduleService.determineMasterScheduleByFmsId(vs.getFleetMaster().getFmsId());
//				if(ms.getMschId() != id) {
//					System.out.println("MSP ID not matching: " + vs.getVschId() + " Table has: " + id + " and the assignment was: " + ms.getMschId());
//					nonMatches++;
//				} else {
//					matches++;
//					System.out.println(vs.getVschId() + " matches");
//				}
//				
//			} catch (Exception e) {
//				errors++;
//				System.out.println(e);
//			}
//			
//				
//		}
//		
//		
//		System.out.println("total: " + total);
//		System.out.println("matches: " + matches);
//		System.out.println("non matches: " + nonMatches);
//		System.out.println("errors: " + errors);
//
//		
//	}	

	
	
	@Test
	public void testLimits() throws Exception {
	
		BigDecimal output = maintenanceScheduleService.getLimitForDriverAuthorizationCode("MAFS00976845");

		System.out.println(output);
		
	}	
	
	
}
