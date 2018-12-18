package com.mikealbert.vision.view.bean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.entity.ContractLine;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.FuelGroupCode;
import com.mikealbert.data.entity.MaintSchedulesProcessed;
import com.mikealbert.data.vo.ClientContactVO;
import com.mikealbert.service.ContractService;
import com.mikealbert.service.FleetMasterService;
import com.mikealbert.service.FuelService;
import com.mikealbert.service.MaintenanceScheduleService;
import com.mikealbert.service.QuotationService;
import com.mikealbert.service.VehicleScheduleService;
import com.mikealbert.service.WillowConfigService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.view.ViewConstants;
import com.mikealbert.vision.vo.MaintScheduleQueueVO;



@Component
@Scope("view")
public class MaintScheduleQueuesBean extends StatefulBaseBean {
	
	private static final long serialVersionUID = -8806821953331781659L;
	MalLogger logger = MalLoggerFactory.getLogger(this.getClass());

	@Resource
	private MaintenanceScheduleService maintenanceScheduleService;
	@Resource
	private FuelService fuelService;
	@Resource
	private QuotationService quotationService;
	@Resource 
	private WillowConfigService willowConfigService;
	@Resource 
	private ContractService contractService;
	
	@Resource
	private	VehicleScheduleService	vehicleScheduleService;
	@Resource
	private	FleetMasterService	fleetMasterService;

	
	
	private List<MaintScheduleQueueVO> rowList;
	private List<MaintScheduleQueueVO> selectedRows;
	private String queueName;
	private long nextPrint;
	private long errors;
	private long previousPrint;
	private long daysToGoBack = 30l;
	private boolean deleteDisabled = true;
	private boolean reprintDisabled = true;
	private boolean	reprintScheduleDisabled = true;
	private int activeQueue;
	private String fuelGroupKey;
	Map<String , FuelGroupCode> FuelGroupCodes;
	
	private static final int NEXT_TO_PRINT = 1;
	private static final int ERROR_QUEUE = 2;
	private static final int PREV_PRINT_QUEUE = 3;
	
	private boolean showDeleteButton;
	@PostConstruct
	public void init() {
		initializeDataTable(535, 770, new int[] { 15, 15, 15, 15, 11, 15 });		
		super.openPage();
		
		activeQueue = NEXT_TO_PRINT;
		queueName = "Next to Print";
		fuelGroupKey = willowConfigService.getConfigValue(MaintenanceScheduleService.FUEL_GROUP_KEY_MSS);
		FuelGroupCodes = fuelService.getAllFuelGroupings(fuelGroupKey);
		loadResults();
		
	}
	

	@Override
	protected void loadNewPage() {

		thisPage.setPageDisplayName("Schedule Queues");
		thisPage.setPageUrl(ViewConstants.SCHEDULE_QUEUES);
	}

	@Override
	protected void restoreOldPage() { }
	
	
	
	private void getCounts() {
		nextPrint = maintenanceScheduleService.getNextPrintListCount();
		errors = maintenanceScheduleService.getErrorListCount();
		previousPrint = maintenanceScheduleService.getPreviousPrintListCount(daysToGoBack);
	}
	private FuelGroupCode getFuelGroupCode(String groupKey,String fuelType){
		if(FuelGroupCodes != null && !FuelGroupCodes.isEmpty()){
			String key = groupKey+":"+fuelType;
			return FuelGroupCodes.get(key);
		}else{
			return null;
		}
	}
	private void loadResults() {
		rowList = new ArrayList<MaintScheduleQueueVO>();
		List<MaintSchedulesProcessed> preliminaryList = new ArrayList<MaintSchedulesProcessed>(); 
		MaintScheduleQueueVO maintScheduleQueueVO;
		ExternalAccount client;
		
		getCounts();
		if(activeQueue == NEXT_TO_PRINT) {
			preliminaryList = maintenanceScheduleService.getNextPrintList();
			setShowDeleteButton(false);
		} else if(activeQueue == ERROR_QUEUE) {
			preliminaryList = maintenanceScheduleService.getErrorList();
			setShowDeleteButton(true);
		} else {
			preliminaryList = maintenanceScheduleService.getPreviousPrintList(daysToGoBack);
			setShowDeleteButton(false);
		}
		
		for(MaintSchedulesProcessed msp : preliminaryList) {
			maintScheduleQueueVO = new MaintScheduleQueueVO();
			if(activeQueue == NEXT_TO_PRINT) {
				String expectedPrintDateStr = MALUtilities.getNullSafeDatetoString(msp.getExpectedPrintDate());
				String currentDateStr = MALUtilities.getNullSafeDatetoString(Calendar.getInstance().getTime());
				if(MALUtilities.compareDates(expectedPrintDateStr, currentDateStr)< 0){
					maintScheduleQueueVO.setOverDueFlag(true);
				}
			}
			maintScheduleQueueVO.setMaintScheduleProcessed(msp);
			maintScheduleQueueVO.setFmsId(msp.getFleetMaster().getFmsId());
			maintScheduleQueueVO.setUnitDescription(msp.getFleetMaster().getModel().getModelMarkYear().getModelMarkYearDesc() + " " +
													msp.getFleetMaster().getModel().getMake().getMakeDesc()   + " " +
													msp.getFleetMaster().getModel().getMakeModelRange().getDescription());
			/*FuelGroupCode fuelGroupCode = fuelService.getFuelGroupCode(fuelGroupKey, 
																			msp.getFleetMaster());*/
			FuelGroupCode fuelGroupCode = null;
			if (msp.getFleetMaster().getVehicleVinDetails()!= null) {
				if(msp.getFleetMaster().getVehicleVinDetails().getFuelType() != null){
					fuelGroupCode = getFuelGroupCode(fuelGroupKey, msp.getFleetMaster().getVehicleVinDetails().getFuelType());
				}
				
			}
			if(fuelGroupCode != null) {
				maintScheduleQueueVO.setFuelTypeGroupDescription(fuelGroupCode.getFuelGroupDescription());
			} else {
				maintScheduleQueueVO.setFuelTypeGroupDescription("");
			}
			client = fleetMasterService.getExternalAccountForUnit(msp.getFleetMaster());
			if(client != null) {
				maintScheduleQueueVO.setAccountCode(client.getExternalAccountPK().getAccountCode());
				maintScheduleQueueVO.setAccountName(client.getAccountName());
				ClientContactVO poc = null;
				try {
					poc = maintenanceScheduleService.getAllClientContactVOsForSchedules(client, msp.getFleetMaster());
				} catch(Exception e) {
					// do nothing since the POC isn't important
				}
				if(poc != null) {
					maintScheduleQueueVO.setClientContactVO(poc);
				}
				
				if(maintScheduleQueueVO.getClientContactVO() != null) {
					maintScheduleQueueVO.setFormattedName(maintScheduleQueueVO.getClientContactVO().formattedName());
				}
			}

			if(msp.getVehicleSchedule() != null) {
				maintScheduleQueueVO.setFormattedSchedule(formatSchedule(msp));
			}
			String batchName = msp.getMaintSchedulesBatch()!= null ? msp.getMaintSchedulesBatch().getBatchName():"";
			if(activeQueue == NEXT_TO_PRINT) {
				maintScheduleQueueVO.setStatus(batchName+" to be sent");
			} else if(activeQueue == ERROR_QUEUE) {
				maintScheduleQueueVO.setStatus(msp.getErrorCode().getErrorDescription());
			} else {
				maintScheduleQueueVO.setStatus(batchName +" transmitted on "+new SimpleDateFormat("MM/dd/yyyy").format(msp.getDateGenerated()));
			}
			
			rowList.add(maintScheduleQueueVO);
			
		}

	}


	public void getNextPrintList() {
		activeQueue = NEXT_TO_PRINT;
		queueName = "Next to Print";
		setShowDeleteButton(false);
		loadResults();
	}
	

	public void getErrorList() {
		activeQueue = ERROR_QUEUE;
		queueName = "Errors";
		setShowDeleteButton(true);
		loadResults();
	}

	public void getPreviousPrintList() {
		activeQueue = PREV_PRINT_QUEUE;
		queueName = "Printed Previous 30 Days";
		setShowDeleteButton(false);
		loadResults();
	}

	public void reprint() {

		for(MaintScheduleQueueVO msqVO : selectedRows) {
			try {
				maintenanceScheduleService.setScheduleToPrintNext(msqVO.getMaintScheduleProcessed());
				addSuccessMessage("plain.message", "Unit " + msqVO.getMaintScheduleProcessed().getFleetMaster().getUnitNo() + " scheduled for reprint");
			} catch (Exception ex) {
				super.addErrorMessage("generic.error", ex.getMessage());
			}
		}
		selectedRows = null;
		loadResults();
		
	}

	public void delete() {

		String unit;
		for(MaintScheduleQueueVO msqVO : selectedRows) {
			try {
				unit = msqVO.getMaintScheduleProcessed().getFleetMaster().getUnitNo();
				maintenanceScheduleService.deleteMaintSchedulesProcessed(msqVO.getMaintScheduleProcessed());
				addSuccessMessage("plain.message", "Unit " + unit + " deleted from the queue");
			} catch (Exception ex) {
				super.addErrorMessage("generic.error", ex.getMessage());
			}
		}
		selectedRows = null;
		loadResults();
		
	}

	private String formatSchedule(MaintSchedulesProcessed maintSchedulesProcessed) {
		StringBuilder display = new StringBuilder();
		display.append(maintSchedulesProcessed.getVehicleSchedule().getMasterSchedule().getClientScheduleType().getScheduleType());
		display.append("<br/>");
		display.append(maintSchedulesProcessed.getVehicleSchedule().getMasterSchedule().getDescription());
		return display.toString();
	}
	
	
	public void checkButtons( javax.faces.event.AjaxBehaviorEvent event) {
		setDeleteDisabled(selectedRows.size() > 0 ? false : true);
		setReprintDisabled(selectedRows.size() > 0 ? false : true);
		setReprintScheduleDisabled(selectedRows.size() > 0 ? false : true);
	}
	
	
	public void	rePrintSchedule(){
		boolean processedOne = false;
		String unitNumber;
		try{	
			for(MaintScheduleQueueVO msqVO : selectedRows) {
				unitNumber = null;
				try {
					FleetMaster fleetMaster = msqVO.getMaintScheduleProcessed().getFleetMaster();
					unitNumber = msqVO.getMaintScheduleProcessed().getFleetMaster().getUnitNo();
					vehicleScheduleService.setupRePrintVehicleSchedule(fleetMaster, getLoggedInUser().getEmployeeNo());
					processedOne = true;
				} catch (Exception ex) {
					super.addErrorMessage("generic.error", ex.getMessage() + " for unit " + unitNumber);
				}
			}
			if(processedOne) {
				addSuccessMessage("process.success", "Reprint Schedule");
			}
			
		}catch(Exception ex){
			handleException("generic.error", new String[]{"Error while reprint vehicle schedules"}, ex, null);
		}
	}
	
	public String cancel() {	
		 return super.cancelPage(); 
	}
	
	public List<MaintScheduleQueueVO> getRowList() {
		return rowList;
	}


	public void setRowList(List<MaintScheduleQueueVO> rowList) {
		this.rowList = rowList;
	}

	public String getQueueName() {
		return queueName;
	}


	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}


	public List<MaintScheduleQueueVO> getSelectedRows() {
		return selectedRows;
	}


	public void setSelectedRows(List<MaintScheduleQueueVO> selectedRows) {
		this.selectedRows = selectedRows;
	}


	public long getErrors() {
		return errors;
	}


	public void setErrors(long errors) {
		this.errors = errors;
	}


	public long getPreviousPrint() {
		return previousPrint;
	}


	public void setPreviousPrint(long previousPrint) {
		this.previousPrint = previousPrint;
	}


	public long getNextPrint() {
		return nextPrint;
	}


	public void setNextPrint(long nextPrint) {
		this.nextPrint = nextPrint;
	}


	public boolean isDeleteDisabled() {
		return deleteDisabled;
	}


	public void setDeleteDisabled(boolean deleteDisabled) {
		this.deleteDisabled = deleteDisabled;
	}


	public boolean isReprintDisabled() {
		return reprintDisabled;
	}


	public void setReprintDisabled(boolean reprintDisabled) {
		this.reprintDisabled = reprintDisabled;
	}


	public int getActiveQueue() {
		return activeQueue;
	}


	public void setActiveQueue(int activeQueue) {
		this.activeQueue = activeQueue;
	}


	public boolean isReprintScheduleDisabled() {
		return reprintScheduleDisabled;
	}


	public void setReprintScheduleDisabled(boolean reprintScheduleDisabled) {
		this.reprintScheduleDisabled = reprintScheduleDisabled;
	}


	public boolean isShowDeleteButton() {
		return showDeleteButton;
	}


	public void setShowDeleteButton(boolean showDeleteButton) {
		this.showDeleteButton = showDeleteButton;
	}


	
	

}