package com.mikealbert.vision.specs.fleet;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.common.MalConstants;
import com.mikealbert.data.DataConstants;
import com.mikealbert.data.dao.MaintenanceRequestDAO;
import com.mikealbert.data.dao.OdometerDAO;
import com.mikealbert.data.dao.OdometerReadingDAO;
import com.mikealbert.data.entity.MaintenanceCode;
import com.mikealbert.data.entity.MaintenanceRechargeCode;
import com.mikealbert.data.entity.MaintenanceRequest;
import com.mikealbert.data.entity.MaintenanceRequestTask;
import com.mikealbert.data.entity.Odometer;
import com.mikealbert.data.entity.OdometerReading;
import com.mikealbert.data.entity.ServiceProvider;
import com.mikealbert.data.entity.UomCode;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.service.FleetMasterService;
import com.mikealbert.service.LookupCacheService;
import com.mikealbert.service.MaintenanceRequestService;
import com.mikealbert.service.OdometerService;
import com.mikealbert.service.ServiceProviderService;
import com.mikealbert.testing.BaseSpec;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.service.VehicleMaintenanceService;

public class MaintPODetailsTest extends BaseSpec{
	
	@Resource VehicleMaintenanceService vehicleMaintenanceService;
	@Resource MaintenanceRequestService maintenanceRequestService;	
	@Resource FleetMasterService fleetMasterService;
	@Resource LookupCacheService lookupCacheService;
	@Resource ServiceProviderService serviceProviderService;
	@Resource OdometerService odometerService;
	@Resource OdometerDAO odometerDAO;	
	@Resource OdometerReadingDAO odometerReadingDAO;	
	@Resource private MaintenanceRequestDAO maintenanceRequestDAO;

	final String DATE_FORMAT_HHmm = "MM/dd/yyyy HH:mm"; 
	
	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@Rollback(true)
	public boolean testCreatePOTaskItem(String unitNo){

		String username = "SAH_R";
		
		try{
			MaintenanceRequest po = buildPO(unitNo);
			MaintenanceRequestTask maintReqTask = buildPOTaskItem(po, username);
			maintReqTask.setMaintCatCode("MISC_MAINT");
			
			String sqlString = "select * from MAINTENANCE_CODES where MAINT_CODE = '100-209'";
			List<MaintenanceCode> maintenanceCodes = em.createNativeQuery(sqlString, MaintenanceCode.class).getResultList(); 
			
			maintReqTask.setMaintenanceCode(maintenanceCodes.get(0));
			maintReqTask.setTaskQty(new BigDecimal(1));
			maintReqTask.setUnitCost(new BigDecimal(10.50));
			maintReqTask.setTotalCost(new BigDecimal(10.50));
			
			po.getMaintenanceRequestTasks().add(maintReqTask);
			
			
			MaintenanceRequest savedPO = null;
			BigDecimal lineMarkUp = null;
			
			//Stamp the user who's creating the PO
			po.setLastChangedBy(username);
			
			//apply recharge total (total cost + mark up) per line
			for(MaintenanceRequestTask line : po.getMaintenanceRequestTasks()){
				if(MALUtilities.convertYNToBoolean(line.getRechargeFlag())){
					lineMarkUp = MALUtilities.isEmpty(line.getMarkUpAmount()) ? new BigDecimal("0.00") : line.getMarkUpAmount();
					line.setRechargeTotalCost(line.getTotalCost().add(lineMarkUp));
				}
			}
			
			//Performing operations specific to new or existing POs
			if(MALUtilities.isEmpty(po.getMrqId())){
				po.setAuthBy(username);			
				po.setCreatedBy(username);			
				po.setMaintReqDate(Calendar.getInstance().getTime());
				po.setRequestClass("REQCLASS1"); //TODO: Remove hard code, i.e. make constant, enum, etc.
				po.setRevisionStatus("REVSTAT1");//TODO: Remove hard code, i.e. make constant, enum, etc. 
				po.setAuthMessage("xxxxx"); //TODO: Remove hard code, i.e. make constant, enum, etc. I have no clue as to what this is; this is the value for all POs
			}	
			
			//perform save and updates
			savedPO = maintenanceRequestDAO.save(po);
			
			
			Odometer odometer = null;
			OdometerReading odometerReading = null;			
			odometer = odometerDAO.findMaxByFmsId(savedPO.getFleetMaster().getFmsId());
			odometerReading = new OdometerReading();
			odometerReading.setOdometer(odometer);
			odometerReading.setReadingCode("SERVICE");
			odometerReading.setReading(savedPO.getCurrentOdo());
			odometerReading.setUserId(username);
			odometerReading.setReadingDate(Calendar.getInstance().getTime());
			odometerReading.setStmntInd("N");
			odometerReading = odometerReadingDAO.save(odometerReading);		

			SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT_HHmm);  
			String currentDate = formatter.format(new Date());		
					
			if(!MALUtilities.isEmpty(savedPO.getMrqId())){
				MaintenanceRequestTask savedMaintReqTask = savedPO.getMaintenanceRequestTasks().get(0);
				if(!MALUtilities.isEmpty(savedMaintReqTask.getMrtId())){
					String versionTs = formatter.format(savedMaintReqTask.getVersionts());
					if( username.equals(savedMaintReqTask.getAuthorizePerson()) && currentDate.equals(versionTs) ){
						return true;
					}
				}
			}		
		}catch(Exception ex){
			ex.printStackTrace();
			return false;
		}	
		
		return false;
	}
	
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@Rollback(true)
	public boolean testUpdatePOTaskItem(String jobNo, boolean modified){

		String username = "SAH_R";
		String originalAuthorizedPerson = null;
		Date originalDateTime = null;
		
		try{
			MaintenanceRequest po = maintenanceRequestService.getMaintenanceRequestByJobNo(jobNo);
			MaintenanceRequestTask maintReqTask = null;
			if(po.getMaintenanceRequestTasks() != null && po.getMaintenanceRequestTasks().size() > 0){
				maintReqTask = po.getMaintenanceRequestTasks().get(0);
				originalAuthorizedPerson = maintReqTask.getAuthorizePerson();
				originalDateTime = maintReqTask.getVersionts();
			}
			
			if(modified){
				if(!MALUtilities.isEmpty(maintReqTask)){
					maintReqTask.setTaskQty(new BigDecimal(1));
					maintReqTask.setUnitCost(new BigDecimal(10.50));
					maintReqTask.setTotalCost(new BigDecimal(10.50));
					maintReqTask.setAuthorizePerson(username);
				}
				
				BigDecimal lineMarkUp = null;
				po.setLastChangedBy(username);
				
				//apply recharge total (total cost + mark up) per line
				for(MaintenanceRequestTask line : po.getMaintenanceRequestTasks()){
					if(MALUtilities.convertYNToBoolean(line.getRechargeFlag())){
						lineMarkUp = MALUtilities.isEmpty(line.getMarkUpAmount()) ? new BigDecimal("0.00") : line.getMarkUpAmount();
						line.setRechargeTotalCost(line.getTotalCost().add(lineMarkUp));
					}
				}
			}

			MaintenanceRequest savedPO = maintenanceRequestDAO.saveAndFlush(po);
			
			if(!MALUtilities.isEmpty(savedPO.getMrqId())){
				MaintenanceRequestTask savedMaintReqTask = savedPO.getMaintenanceRequestTasks().get(0);
				if(!MALUtilities.isEmpty(savedMaintReqTask.getMrtId())){				
					if( !originalAuthorizedPerson.equals(savedMaintReqTask.getAuthorizePerson()) && MALUtilities.compateDates(savedMaintReqTask.getVersionts(), originalDateTime) != 0){						
						return true;
					}
				}
			}		
		}catch(Exception ex){
			ex.printStackTrace();
			return false;
		}
		
		return false;		
	}	
	
	private MaintenanceRequest buildPO(String unitNo){
		PageRequest page = new PageRequest(0,1);
    	Calendar calendar = Calendar.getInstance();
    	MaintenanceRequest po = new MaintenanceRequest();
    	po.setMaintenanceRequestTasks(new ArrayList<MaintenanceRequestTask>());
    	po.setFleetMaster(fleetMasterService.findByUnitNo(unitNo));
    	po.setMaintReqStatus("B");
    	po.setMaintReqType("MAINT");
    	po.setActualStartDate(calendar.getTime());
    	calendar.add(Calendar.DAY_OF_MONTH, 1);    	
    	po.setPlannedEndDate(calendar.getTime()); 
    	po.setJobNo(vehicleMaintenanceService.generateJobNumber(CorporateEntity.MAL));    	
    	List<ServiceProvider> serviceProviders = serviceProviderService.getServiceProviderByNameOrCode("00086050",page);
    	po.setServiceProvider(serviceProviders.get(0));
    	po.setCurrentOdo(34162L);
    	UomCode uomCode = odometerService.convertOdoUOMCode("MILE");
    	po.setUnitofMeasureCode(uomCode);
    	po.setServiceProviderMarkupInd(MalConstants.FLAG_N);
    	
    	return po;
	}
	
	
	private MaintenanceRequestTask buildPOTaskItem(MaintenanceRequest po, String username){
		
		MaintenanceRequestTask maintReqTask = new MaintenanceRequestTask(); 
		Long lineNumber = 0l;
		lineNumber = MALUtilities.isEmpty(po.getMaintenanceRequestTasks()) ? 1l : po.getMaintenanceRequestTasks().size() + 1l;
		
		maintReqTask = new MaintenanceRequestTask();
		maintReqTask.setMaintenanceRequest(po);		
		maintReqTask.setRechargeFlag(vehicleMaintenanceService.getDefaultMaintRechargeFlag(po));		
		MaintenanceRechargeCode rechargeCode = vehicleMaintenanceService.getDefaultMaintRechargeCode(po);
		if(!MALUtilities.isEmpty(rechargeCode)){
			maintReqTask.setRechargeCode(rechargeCode.getCode());	
		}
		maintReqTask.setOutstanding(DataConstants.DEFAULT_N);
		maintReqTask.setWasOutstanding(DataConstants.DEFAULT_N);
		maintReqTask.setTaskQty(new BigDecimal(1));
		maintReqTask.setTotalCost(new BigDecimal("0.00"));
		maintReqTask.setLineNumber(lineNumber);
		maintReqTask.setIndex(lineNumber.intValue());				
		maintReqTask.setAuthorizePerson(username);
		if (!MALUtilities.isEmptyString(po.getServiceProvider().getNetworkVendor())){
			maintReqTask.setDiscountFlag(po.getServiceProvider().getNetworkVendor());
		}else{
			maintReqTask.setDiscountFlag(DataConstants.DEFAULT_N);
		}
		
		return maintReqTask;
	}

}
