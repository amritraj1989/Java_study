package com.mikealbert.vision.specs.fleet;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.mikealbert.common.MalConstants;
import com.mikealbert.data.DataConstants;
import com.mikealbert.data.dao.MaintenanceRequestTaskDAO;
import com.mikealbert.data.dao.WorkClassPermissionDAO;
import com.mikealbert.data.entity.MaintenanceCode;
import com.mikealbert.data.entity.MaintenanceRechargeCode;
import com.mikealbert.data.entity.MaintenanceRequest;
import com.mikealbert.data.entity.MaintenanceRequestTask;
import com.mikealbert.data.entity.ServiceProvider;
import com.mikealbert.data.entity.UomCode;
import com.mikealbert.data.entity.WorkClass;
import com.mikealbert.data.entity.WorkClassPermission;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.service.FleetMasterService;
import com.mikealbert.service.LookupCacheService;
import com.mikealbert.service.MaintenanceRequestService;
import com.mikealbert.service.OdometerService;
import com.mikealbert.service.ServiceProviderService;
import com.mikealbert.service.UserService;
import com.mikealbert.testing.BaseSpec;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.service.VehicleMaintenanceService;
import com.mikealbert.vision.view.ViewConstants;

public class DeletePOTest extends BaseSpec{
	
	
	@Resource OdometerService odometerService;
	@Resource MaintenanceRequestTaskDAO maintenanceRequestTaskDAO;
	@Resource LookupCacheService lookupCacheService;
	@Resource ServiceProviderService serviceProviderService;
	@Resource VehicleMaintenanceService vehicleMaintenanceService;
	@Resource FleetMasterService fleetMasterService;
	@Resource UserService userService;
	@Resource MaintenanceRequestService maintRequestService;
	@Resource WorkClassPermissionDAO workClassPermissionDao;	
    
    public static final String MAINT_REQUEST_STATUS_BOOKED_IN = "B";    
    public static final String USERNAME = "SAH_R";
    public static final String RESOURCE_NAME = "maintenancePO_delete";
    
//    private MaintenanceRequest po;
    private boolean maintRequestsDeleted;
    private boolean maintRequestTasksDeleted;
    private long mrqId = 0L;
    private boolean authorized;

    public void testAutorizationToDelete(){
    	WorkClass  selectedWorkClass = userService.findWorkClass("WILLOW", CorporateEntity.MAL);
    	List<WorkClassPermission> workClassPermissions = userService.getWorkClassPermissions(selectedWorkClass);
    	Iterator<WorkClassPermission> iterator = workClassPermissions.iterator();
    	while(iterator.hasNext()){
    		WorkClassPermission wcp = iterator.next();
    		if(wcp.getPermissionSet().getPermissionSetName().equals("FLEET_MAINT_DELETE_PO")){
    			iterator.remove();
    		}
    	}
    	setAuthorized(determineResourceAccess(RESOURCE_NAME));
    }
    
    
	public void testDeletePO(String unitNo){
		maintRequestsDeleted = false;
		maintRequestTasksDeleted = false;
		try{
			MaintenanceRequest po = buildPO(unitNo);

			MaintenanceRequest savedPO = maintRequestService.saveOrUpdateMaintnenacePO(po, USERNAME);

			em.clear();
			
			mrqId = savedPO.getMrqId();
			savedPO = null;
			MaintenanceRequest fromDBPO1 = maintRequestService.getMaintenanceRequestByMrqId(mrqId);
			
			maintRequestService.deletePO(fromDBPO1);
			fromDBPO1 = null;
			confirmDeletePO();			
			em.clear();
			mrqId = 0;
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void testUpdateStatusAndDeletePO(String unitNo,  String status){
		maintRequestsDeleted = false;
		maintRequestTasksDeleted = false;
		try{
			MaintenanceRequest po = buildPO(unitNo);

			MaintenanceRequest savedPO = maintRequestService.saveOrUpdateMaintnenacePO(po, USERNAME);

			em.clear();
			
			mrqId = savedPO.getMrqId();
			savedPO = null;
			MaintenanceRequest fromDBPO1 = maintRequestService.getMaintenanceRequestByMrqId(mrqId);
			
			fromDBPO1.setMaintReqStatus(status);
			maintRequestService.saveOrUpdateMaintnenacePO(fromDBPO1, USERNAME);
			em.clear();
			
			MaintenanceRequest fromDBPO2 = maintRequestService.getMaintenanceRequestByMrqId(mrqId);
			
			maintRequestService.deletePO(fromDBPO2);
			fromDBPO1 = null;
			fromDBPO2 = null;
			confirmDeletePO();
			em.clear();
			mrqId = 0;
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void confirmDeletePO(){

		try{
		if(mrqId > 0){
			List<MaintenanceRequestTask>  taskList = maintenanceRequestTaskDAO.getMaintRequestTasksByMrqId(mrqId);
			if(taskList == null || taskList.size() == 0){
				maintRequestTasksDeleted = true;
			}
			maintRequestService.getMaintenanceRequestByMrqId(mrqId);
		}
		}catch(Exception ex){
			maintRequestsDeleted = true;
		}
	}
	
	private ServiceProvider getServiceProvider(String nameOrCode){
		ServiceProvider serviceProvider = null;
		PageRequest page = new PageRequest(0,2);
		if(!"".equals(nameOrCode)){
			List<ServiceProvider> providers = serviceProviderService.getServiceProviderByNameOrCode(nameOrCode,page);
			if(providers.size() == 1){
				if(providers.get(0) != null ){
					serviceProvider = providers.get(0);
				}
			}
		}
		return serviceProvider;
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
    	
		ServiceProvider serviceProvider = getServiceProvider("CSC32");
		po.setServiceProvider(serviceProvider);
		po.setMaintReqStatus(MAINT_REQUEST_STATUS_BOOKED_IN);
		po.setServiceProviderMarkupInd(MalConstants.FLAG_N);
		
		buildPOTaskItem(po, USERNAME);
		
    	return po;
	}
	
	
	private void buildPOTaskItem(MaintenanceRequest po, String username){
		
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
		maintReqTask.setUnitCost(new BigDecimal(20));
		maintReqTask.setTotalCost(new BigDecimal(20));
		maintReqTask.setMaintCatCode("A/C");
		
		po.getMaintenanceRequestTasks().add(maintReqTask);
				
	}
	
	private boolean determineResourceAccess(String resourceId){
		boolean permitted = false;
		boolean readonly = false;
		
		Collection<GrantedAuthority> userAuthorities = new ArrayList<GrantedAuthority>();
		List<WorkClassPermission> workClassToPermissions = workClassPermissionDao.findByWorkClass("WILLOW", CorporateEntity.MAL.getCorpId());
		for(WorkClassPermission permission: workClassToPermissions){
			userAuthorities.add(new SimpleGrantedAuthority(permission.getPermissionSet().getPermissionSetName()));
		}
		userAuthorities.add(new SimpleGrantedAuthority("USER"));		
		
		Map<String, List<String>> resourceMap = null;
		List<String> pageRoles;

		for(GrantedAuthority ga : userAuthorities){
			if(ga.getAuthority().equals(ViewConstants.USER_READ_ONLY_ROLE))
				readonly = true;			
		}
						
		if(!readonly) {
			resourceMap = lookupCacheService.getResourceRoleMap();
			pageRoles = resourceMap.get(resourceId);	
			
			for(GrantedAuthority ga : userAuthorities){
				if(pageRoles != null && pageRoles.contains(ga.getAuthority())){
					permitted = true;
					break;
				}
			}
			 
		} else {
			permitted = false;
		}
		 
		return permitted;		
	}
    
    public boolean isMaintRequestsDeleted() {
		return maintRequestsDeleted;
	}

	public void setMaintRequestsDeleted(boolean maintRequestsDeleted) {
		this.maintRequestsDeleted = maintRequestsDeleted;
	}

	public boolean isMaintRequestTasksDeleted() {
		return maintRequestTasksDeleted;
	}

	public void setMaintRequestTasksDeleted(boolean maintRequestTasksDeleted) {
		this.maintRequestTasksDeleted = maintRequestTasksDeleted;
	}


	public boolean isAuthorized() {
		return authorized;
	}


	public void setAuthorized(boolean authorized) {
		this.authorized = authorized;
	}

		
}
