package com.mikealbert.vision.specs.fleet;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.mikealbert.common.MalConstants;
import com.mikealbert.data.DataConstants;
import com.mikealbert.data.dao.MaintenanceRequestDAO;
import com.mikealbert.data.dao.MaintenanceRequestStatusDAO;
import com.mikealbert.data.entity.MaintenanceCode;
import com.mikealbert.data.entity.MaintenanceRequest;
import com.mikealbert.data.entity.MaintenanceRequestTask;
import com.mikealbert.data.entity.Odometer;
import com.mikealbert.data.entity.ServiceProvider;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.data.vo.MaintenanceServiceHistoryVO;
import com.mikealbert.data.vo.ServiceProviderLOVVO;
import com.mikealbert.service.FleetMasterService;
import com.mikealbert.service.LookupCacheService;
import com.mikealbert.service.MaintenanceHistoryService;
import com.mikealbert.service.MaintenanceRequestService;
import com.mikealbert.service.OdometerService;
import com.mikealbert.service.ServiceProviderService;
import com.mikealbert.testing.BaseSpec;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.service.VehicleMaintenanceService;

public class CreatePOTest extends BaseSpec{
	
	@Resource VehicleMaintenanceService vehicleMaintenanceService;
	@Resource MaintenanceRequestService maintenanceRequestService;	
	@Resource ServiceProviderService serviceProviderService;
	@Resource LookupCacheService lookupCacheService;
	@Resource MaintenanceRequestStatusDAO maintenanceRequestStatusDAO;
	@Resource OdometerService odometerService;
	@Resource FleetMasterService fleetMasterService;
	@Resource MaintenanceRequestDAO maintRequestDAO;
	@Resource MaintenanceHistoryService maintenanceHistoryService;
	
	MaintenanceRequest maintReq;
	List<MaintenanceRequestTask> maintReqTasks;
	MaintenanceServiceHistoryVO maintServiceHistVO;
	
	int serviceProviderLovSize;
	String serviceProviderName;
	String serviceProviderNumber;
	String payeeName;
	String contactName;
	String workOrderNumber;
	String poStatus;
	String poType;
	String authorizedBy;
	String sysDate;
	BigDecimal poTotal = new BigDecimal(0);
	BigDecimal poMarkUp = new BigDecimal(0);
	BigDecimal poRecharge = new BigDecimal(0);
	BigDecimal rechTotal = new BigDecimal(0);
	String generatedPO;
	String allCategories;
	int taskCount;
	BigDecimal totalAmount = new BigDecimal(0);
		
	DecimalFormat decFormat = new DecimalFormat("################.00");
	SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
	
	BigDecimal rechargeTotal = new BigDecimal(decFormat.format(0.00));
	BigDecimal updatedMarkupAmount = new BigDecimal(decFormat.format(0.00));
	
	void setDefaults() {
		getMaintReq().setLastChangedBy(getAuthorizedBy());		
		getMaintReq().setAuthBy(getAuthorizedBy());
		getMaintReq().setCreatedBy(getAuthorizedBy());
		getMaintReq().setMaintReqDate(Calendar.getInstance().getTime());
		getMaintReq().setRequestClass("REQCLASS1");
		getMaintReq().setRevisionStatus("REVSTAT1"); 
		getMaintReq().setAuthMessage("xxxxx");
	}
	
	public MaintenanceRequest testCreatePO(String inUnitNo, String inServiceProviderName, String inPhoneNumber, String inContactName, String inWorkOrderNo, String inPOStatus,
										   String inPOType, String inZipCode){
		
		setMaintReq(new MaintenanceRequest());
		setMaintReqTasks(new ArrayList<MaintenanceRequestTask>());
		ServiceProvider serviceProvider;
		List<ServiceProviderLOVVO> serviceProviderLOVVO;
		Odometer odometer = new Odometer();
		PageRequest page = new PageRequest(0,1);
		Calendar calendar = Calendar.getInstance();
		
		setGeneratedPO(null);
		
		serviceProviderLOVVO = serviceProviderService.getAllServiceProviders(inServiceProviderName, null, null, null, null,false,null);
		
		setServiceProviderLovSize(serviceProviderLOVVO.size());
		
		serviceProviderLOVVO = serviceProviderService.getAllServiceProviders(inServiceProviderName, null, null, inPhoneNumber, null,false,null);
		
		serviceProviderLOVVO = serviceProviderService.getAllServiceProviders(inServiceProviderName, null, inZipCode, null, null, false, null);
		
		if (serviceProviderLOVVO.size() > 0) {
			serviceProvider = serviceProviderService.getServiceProvider((serviceProviderLOVVO.get(0).getServiceProviderId()).longValue());
		} else {
			serviceProvider = (serviceProviderService.getServiceProviderByName(inServiceProviderName, page).get(0));
		}
		
		setServiceProviderName(serviceProvider.getServiceProviderName());
		setServiceProviderNumber(serviceProvider.getServiceProviderNumber());
		getMaintReq().setServiceProvider(serviceProvider);
		
		getMaintReq().setFleetMaster(fleetMasterService.findByUnitNo(inUnitNo));
		
		odometer = odometerService.getCurrentOdometer(getMaintReq().getFleetMaster());
		getMaintReq().setUnitofMeasureCode(odometerService.convertOdoUOMCode(odometer.getUomCode()));
		
		getMaintReq().setMaintReqStatus(MALUtilities.isEmptyString(inPOStatus) ? "B" : inPOStatus);
    	getMaintReq().setMaintReqType(MALUtilities.isEmptyString(inPOType) ? "MAINT" : inPOType);
    	getMaintReq().setActualStartDate(calendar.getTime());
    	calendar.add(Calendar.DAY_OF_MONTH, 1);    	
    	getMaintReq().setPlannedEndDate(calendar.getTime()); 
    	getMaintReq().setJobNo(vehicleMaintenanceService.generateJobNumber(CorporateEntity.MAL));
    	getMaintReq().setServiceProviderMarkupInd(MalConstants.FLAG_N);
    	
		setPayeeName(serviceProvider.getPayeeAccount().getExternalAccountPK().getAccountCode() + " - " + serviceProvider.getPayeeAccount().getAccountName());
		getMaintReq().setServiceProviderContactName(inContactName);
		getMaintReq().setPayeeInvoiceNumber(inWorkOrderNo);
		getMaintReq().setMarkUpAmount(new BigDecimal(0));
		setPoStatus(maintenanceRequestStatusDAO.findById(getMaintReq().getMaintReqStatus()).orElse(null).getDescription());
		setPoType(getMaintReq().getMaintReqType());
		setPoTotal(new BigDecimal(0.00));
		setPoMarkUp(new BigDecimal(0.00));
		setPoRecharge(new BigDecimal(0));
		setRechTotal(new BigDecimal(0));
		getMaintReq().setCurrentOdo(getMaintReq().getFleetMaster().getLatestOdometerReading().getOdoReading().longValue());
		return getMaintReq();
	}
	
	@SuppressWarnings("unchecked")
	public MaintenanceRequestTask testCreateMaintReqTask(String inMaintCategory, String inMaintCode, int inQty, BigDecimal inUnitPrice, String inServiceCode) {
		MaintenanceRequestTask maintReqTask = new MaintenanceRequestTask();
		List<MaintenanceCode> maintenanceCodes = new ArrayList<MaintenanceCode>();
		String sqlString = "select mnt.* from maintenance_codes mnt where mnt.maint_code = '" + inMaintCode + "'";
		
		if (!MALUtilities.isEmptyString(inServiceCode)){
//			List<ServiceProviderMaintenanceCode> serviceProviderMaintCodes = vehicleMaintenanceService.getServiceProviderMaintenanceByMafsCode(inServiceCode, getMaintReq().getServiceProvider().getServiceProviderId());
//			maintReqTask.setServiceProviderMaintenanceCode(serviceProviderMaintCodes.get(0));
		}
				
		maintenanceCodes = (em.createNativeQuery(sqlString, MaintenanceCode.class)).getResultList();
		
		maintReqTask.setMaintenanceRequest(getMaintReq());
		maintReqTask.setMaintCatCode(inMaintCategory);
		maintReqTask.setMaintenanceCode(maintenanceCodes.get(0));
		
		maintReqTask.setTotalCost(new BigDecimal(0));
		maintReqTask.setRechargeTotalCost(new BigDecimal(0));
		maintReqTask.setMarkUpAmount(new BigDecimal(0));

		maintReqTask.setTaskQty(new BigDecimal(inQty));
		maintReqTask.setUnitCost(inUnitPrice);
		
				
		
		
		maintReqTask.setRechargeFlag(vehicleMaintenanceService.getDefaultMaintRechargeFlag(getMaintReq()));
		
		maintReqTask.setWasOutstanding(DataConstants.DEFAULT_N);
		maintReqTask.setOutstanding(DataConstants.DEFAULT_N);
		
		maintReqTask.setTotalCost(maintReqTask.getUnitCost().multiply(maintReqTask.getTaskQty()));
		maintReqTask.setTotalCost(new BigDecimal(decFormat.format(maintReqTask.getTotalCost())));
		
		maintReqTask.setDiscountFlag(getMaintReq().getServiceProvider().getNetworkVendor());
		setAuthorizedBy("SHARMA_R");
		setSysDate(dateFormat.format(new Date()));
		
		maintReqTask.setAuthorizePerson(getAuthorizedBy());

		setPoTotal(new BigDecimal(decFormat.format(getPoTotal().add(maintReqTask.getTotalCost()))));

		maintReqTask.setMarkUpAmount(maintReqTask.getTotalCost().multiply(new BigDecimal(0.10)));
		
		setPoMarkUp(getPoMarkUp().add(maintReqTask.getMarkUpAmount()));
		
		getMaintReq().setMarkUpAmount(new BigDecimal(decFormat.format(getPoMarkUp())));
		
		if (getPoMarkUp().compareTo(new BigDecimal(35.00)) == 1) {
			setPoMarkUp(new BigDecimal(35.00));
			getMaintReq().setMarkUpAmount(getPoMarkUp());
		}
		
		if (maintReqTask.getRechargeFlag().equals("Y")) {
			maintReqTask.setRechargeTotalCost(maintReqTask.getTotalCost().add(maintReqTask.getMarkUpAmount()));
		}
		
		setPoRecharge(new BigDecimal(decFormat.format(getPoTotal().add(getMaintReq().getMarkUpAmount()))));
		getMaintReq().setMaintenanceRequestTasks(getMaintReqTasks());
		getMaintReq().getMaintenanceRequestTasks().add(maintReqTask);
		
		return maintReqTask;
	}
	
	public MaintenanceRequest testUpdatePO(BigDecimal inMarkupAmount) {
		maintReq = new MaintenanceRequest();
		maintReqTasks = new ArrayList<MaintenanceRequestTask>();

		setPoRecharge(new BigDecimal(0));
		setPoMarkUp(new BigDecimal(0));
		
		setMaintReq(maintenanceRequestService.getMaintenanceRequestByJobNo(getGeneratedPO()));
		
		getMaintReq().setMarkUpAmount(new BigDecimal(decFormat.format(inMarkupAmount)));
		
		setUpdatedMarkupAmount(getMaintReq().getMarkUpAmount());
		
		for(MaintenanceRequestTask mrt : getMaintReq().getMaintenanceRequestTasks()) {
			setPoRecharge(getPoRecharge().add(mrt.getTotalCost()));
		}

		setPoRecharge(new BigDecimal(decFormat.format(getMaintReq().getMarkUpAmount().add(getPoRecharge()))));
		setMaintReq(maintReq);
		return getMaintReq();
	}
	
	public void actionSave() {
		setDefaults();
		//TODO: Below call needs to be replaced by below commented code using service to save or update the records. Due to Optimistic locking issue this approach has been used.
		setMaintReq(maintRequestDAO.save(getMaintReq()));
/*		try {
			setMaintReq(vehicleMaintenanceService.saveOrUpdateMaintnenacePO(getMaintReq(), getAuthorizedBy()));
			} catch (MalBusinessException ex) {
				System.out.println("Error in Saving Maintenance PO");
		}
*/
		
		setGeneratedPO(getMaintReq().getJobNo());
		setMaintReq(null);
		setMaintReqTasks(null);
		System.gc();
	}
	
	public void actionCancel() {
		
	}
	
	public MaintenanceServiceHistoryVO testVerifyMaintServiceHistory() {
		
		setMaintServiceHistVO(new MaintenanceServiceHistoryVO());
		setMaintReq(maintenanceRequestService.getMaintenanceRequestByJobNo(getGeneratedPO()));
		setMaintServiceHistVO(maintenanceHistoryService.getMaintenanceServiceHistoryByVIN(getMaintReq().getFleetMaster().getVin(), null, new Sort(Sort.Direction.DESC, DataConstants.SERVICE_HISTORY_SORT_DEFAULT), null, null, null).get(0));
		setGeneratedPO(getMaintServiceHistVO().getPoNumber());
		setPoRecharge(new BigDecimal(0));
		
		for(MaintenanceRequestTask mrq : getMaintReq().getMaintenanceRequestTasks()) {
			setPoRecharge(getPoRecharge().add(mrq.getTotalCost()));
			if (getMaintReq().getMaintenanceRequestTasks().indexOf(mrq) == 0) {
				setAllCategories(mrq.getMaintCatCode());
			} else
			{
				setAllCategories(getAllCategories() + ", " + mrq.getMaintCatCode());
			}
		}

		setPoRecharge(new BigDecimal(decFormat.format(getPoRecharge().add(getMaintReq().getMarkUpAmount()))));
		return getMaintServiceHistVO();
	}
	
	public MaintenanceRequest testViewPO() {
		setMaintReq(maintenanceRequestService.getMaintenanceRequestByJobNo(getGeneratedPO()));
		setTaskCount(getMaintReq().getMaintenanceRequestTasks().size());
		setTotalAmount(new BigDecimal(0));
		setPoRecharge(new BigDecimal(0));
		for(MaintenanceRequestTask mrt : getMaintReq().getMaintenanceRequestTasks()) {
			setTotalAmount(getTotalAmount().add(mrt.getTotalCost()));
			if (mrt.getRechargeFlag().equals("Y")) {
				setPoRecharge(getPoRecharge().add(mrt.getTotalCost()));
				
			}
		}
		
		setTotalAmount(new BigDecimal(decFormat.format(getTotalAmount())));
		setPoRecharge(getPoRecharge().add(getMaintReq().getMarkUpAmount()));
		return getMaintReq();
	}
	
	public MaintenanceRequest getMaintReq() {
		return maintReq;
	}

	public void setMaintReq(MaintenanceRequest maintReq) {
		this.maintReq = maintReq;
	}

	public List<MaintenanceRequestTask> getMaintReqTasks() {
		return maintReqTasks;
	}

	public void setMaintReqTasks(List<MaintenanceRequestTask> maintReqTasks) {
		this.maintReqTasks = maintReqTasks;
	}

	public MaintenanceRequestTask createMaintenanceTaskTest() {
		MaintenanceRequestTask maintReqTask = null;
		return maintReqTask;
	}

	public int getServiceProviderLovSize() {
		return serviceProviderLovSize;
	}

	public void setServiceProviderLovSize(int serviceProviderLovSize) {
		this.serviceProviderLovSize = serviceProviderLovSize;
	}

	public String getServiceProviderName() {
		return serviceProviderName;
	}

	public void setServiceProviderName(String serviceProviderName) {
		this.serviceProviderName = serviceProviderName;
	}

	public String getServiceProviderNumber() {
		return serviceProviderNumber;
	}

	public void setServiceProviderNumber(String serviceProviderNumber) {
		this.serviceProviderNumber = serviceProviderNumber;
	}

	public String getPayeeName() {
		return payeeName;
	}

	public void setPayeeName(String payeeName) {
		this.payeeName = payeeName;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getWorkOrderNumber() {
		return workOrderNumber;
	}

	public void setWorkOrderNumber(String workOrderNumber) {
		this.workOrderNumber = workOrderNumber;
	}

	public String getPoStatus() {
		return poStatus;
	}

	public void setPoStatus(String poStatus) {
		this.poStatus = poStatus;
	}

	public String getPoType() {
		return poType;
	}

	public void setPoType(String poType) {
		this.poType = poType;
	}

	public String getAuthorizedBy() {
		return authorizedBy;
	}

	public void setAuthorizedBy(String authorizedBy) {
		this.authorizedBy = authorizedBy;
	}

	public String getSysDate() {
		return sysDate;
	}

	public void setSysDate(String sysDate) {
		this.sysDate = sysDate;
	}

	public BigDecimal getPoTotal() {
		return poTotal;
	}

	public void setPoTotal(BigDecimal poTotal) {
		this.poTotal = poTotal;
	}

	public BigDecimal getRechTotal() {
		return rechTotal;
	}

	public void setRechTotal(BigDecimal rechTotal) {
		this.rechTotal = rechTotal;
	}

	public String getGeneratedPO() {
		return generatedPO;
	}

	public void setGeneratedPO(String generatedPO) {
		this.generatedPO = generatedPO;
	}

	public BigDecimal getRechargeTotal() {
		return rechargeTotal;
	}

	public void setRechargeTotal(BigDecimal rechargeTotal) {
		this.rechargeTotal = rechargeTotal;
	}

	public BigDecimal getUpdatedMarkupAmount() {
		return updatedMarkupAmount;
	}

	public void setUpdatedMarkupAmount(BigDecimal updatedMarkupAmount) {
		this.updatedMarkupAmount = updatedMarkupAmount;
	}

	public MaintenanceServiceHistoryVO getMaintServiceHistVO() {
		return maintServiceHistVO;
	}

	public void setMaintServiceHistVO(MaintenanceServiceHistoryVO maintServiceHistVO) {
		this.maintServiceHistVO = maintServiceHistVO;
	}

	public String getAllCategories() {
		return allCategories;
	}

	public void setAllCategories(String allCategories) {
		this.allCategories = allCategories;
	}

	public int getTaskCount() {
		return taskCount;
	}

	public void setTaskCount(int taskCount) {
		this.taskCount = taskCount;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public BigDecimal getPoMarkUp() {
		return poMarkUp;
	}

	public void setPoMarkUp(BigDecimal poMarkUp) {
		this.poMarkUp = poMarkUp;
	}

	public BigDecimal getPoRecharge() {
		return poRecharge;
	}

	public void setPoRecharge(BigDecimal poRecharge) {
		this.poRecharge = poRecharge;
	}
}
