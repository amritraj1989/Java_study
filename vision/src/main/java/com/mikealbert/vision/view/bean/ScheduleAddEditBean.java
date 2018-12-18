package com.mikealbert.vision.view.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.dao.ExternalAccountDAO;
import com.mikealbert.data.entity.ClientScheduleType;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.MasterSchedule;
import com.mikealbert.data.entity.MasterScheduleInterval;
import com.mikealbert.data.entity.ServiceTask;
import com.mikealbert.service.MaintenanceScheduleService;
import com.mikealbert.service.VehicleScheduleService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.view.ViewConstants;


@Component
@Scope("view")
public class ScheduleAddEditBean extends StatefulBaseBean {
	
	private static final long serialVersionUID = 5459137983854538998L;

	MalLogger logger = MalLoggerFactory.getLogger(this.getClass());

	@Resource
	private	MaintenanceScheduleService	maintenanceScheduleService;

	@Resource
	private	VehicleScheduleService	vehicleScheduleService;
	
	@Resource
	private	ExternalAccountDAO	externalAccountDAO;

	
	private static final int DEFAULT_MONTH_FREQUENCY = 6;
	private static final int DEFAULT_INTERVAL_LIST_LENGTH = 100;
	private static final String NO_MILEAGE_INTERVAL_DESC = "No Interval";
	private static final int NO_MILEAGE_INTERVAL_VALUE = 999999999;
	private static final int DISPLAY_AT_TOP_INTERVAL_VALUE = 0;
	private static final String DISPLAY_AT_TOP_INTERVAL_DESC = "As Needed";
	
	private Long scheduleId;
	private MasterSchedule schedule;
	private boolean hiddenFlag;
	private List<SelectItem> availableTypes;
	private List<SelectItem> availableIntervals;
	private List<SelectItem> availableTasks;
	private String selectedType;
	private int originalDistance;
	private Integer months;
	private Integer miles = 2500;
	private MasterScheduleInterval selectedTaskInterval;
	private Integer selectedInterval;
	private Long selectedTask;
	private boolean selectedRepeatFlag;
	private boolean dirtyTable;
	private boolean showWarning;
	private List<String> duplicateTasks = new ArrayList<String>();
	private int selectedIndex;
	private boolean addMode;
	private Long ruleCount;
	private Long vehicleCount;
	private boolean copyMode;
	private	String	taskOrderAction;
	private Map<String, Object> nextPageValues;
	
	private String	TASK_ORDER_MOVEUP = "MOVEUP";
	private String	TASK_ORDER_MOVEDOWN = "MOVEDOWN";
	
	private	boolean hasEditPermission ;
	private	List<ExternalAccount> clientAccounts ;
	private boolean	enableClientAccounts= false;
	private	final	String TYPE_CLIENT = "CLIENT";
	private	String	clientAccountCode ;
	private List<SelectItem> availableClients;
	private	int	MAX_ALLOWED_TASKS = 12;
	private boolean	showTaskOverFlowError	= false;
	private	String	taskOverFlowErrorMsg	= null;
	@PostConstruct
	public void init() {
		logger.debug("init is called");
		
		super.openPage();
		hasEditPermission = hasPermission();
		initializeDataTable(600, 770, new int[] { 7, 6, 5, 16, 47, 7, 5,5});
		setClientAccounts(externalAccountDAO.findAccountsForClientScheduleType());
		setupSchedule();
		handleTypeSelect();
		availableClients = new ArrayList<SelectItem>();
		for (ExternalAccount externalAccount : clientAccounts) {
			SelectItem	item	= new SelectItem();
			String accountName = externalAccount.getAccountName() != null ? externalAccount.getAccountName(): "";
			//int nameLen = accountName.length();
			//accountName = accountName.substring(0, nameLen/2);
			item.setValue((String)externalAccount.getExternalAccountPK().getAccountCode());
			item.setLabel((String)externalAccount.getExternalAccountPK().getAccountCode()+"-"+accountName);
			availableClients.add(item);
		}
		
	}
	public void handleTypeSelect(){
		if(TYPE_CLIENT.equals(getSelectedType())){
			enableClientAccounts	= true;
		}else{
			enableClientAccounts	= false;
			setClientAccountCode(null);
		}
	}

	@Override
	protected void loadNewPage() {
		thisPage.setPageDisplayName("Add/Edit Schedule");
		thisPage.setPageUrl(ViewConstants.SCHEDULE_ADD_EDIT);
		if (thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_SCHEDULE_ID) != null) {
			scheduleId = ((Long) thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_SCHEDULE_ID));
		}
		if (thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_COPY_SCHEDULE) != null) {
			copyMode = true;		
			if(scheduleId != null) {
				addSuccessMessage("plain.message", "Copy of [" + maintenanceScheduleService.getMasterSchedule(scheduleId).getMasterCode() + " - " + maintenanceScheduleService.getMasterSchedule(scheduleId).getDescription() + "]");
			}
		}
	}

	@Override
	protected void restoreOldPage() {}	
	
    public void onRowSelect() {
    	addMode = false;
		selectedTaskInterval = schedule.getMasterScheduleIntervals().get(getSelectedIndex());
		selectedInterval = selectedTaskInterval.getIntervalMultiple();
		selectedRepeatFlag = selectedTaskInterval.getRepeatFlag().equalsIgnoreCase("Y") ? true : false;
		selectedTask = selectedTaskInterval.getServiceTask().getSrvtId();
    }
    
    
    public void addTask() {    	
    	addMode = true;
    	selectedTaskInterval = new MasterScheduleInterval();
    	selectedTaskInterval.setOrder(getNextPrintOrderValue());
    	selectedRepeatFlag = true;
    	selectedTask = null;
    	selectedInterval = null;
    	buildIntervalList();
    }

    public void updateTask() {
    	ServiceTask st = maintenanceScheduleService.getTask(selectedTask);
    	selectedTaskInterval.setInterval(getSelectedInterval());
    	selectedTaskInterval.setServiceTask(st);
    	selectedTaskInterval.setRepeatFlag(selectedRepeatFlag ? "Y" : "N");
    	selectedTaskInterval.setMasterSchedule(schedule);
    	if(addMode) {
        	schedule.getMasterScheduleIntervals().add(selectedTaskInterval);
        	//Validation for number of tasks added in master schedule
        	if(schedule.getMasterScheduleIntervals().size() > MAX_ALLOWED_TASKS){
        		showTaskOverFlowError = true;
        		addErrorMessage( "schedule.err.msg.maxtask", new String[]{MAX_ALLOWED_TASKS+""});
        	}else{
        		showTaskOverFlowError	= false;
        	}
    	}
    	dirtyTable = true;
    }

    public void removeTask() {
    	schedule.getMasterScheduleIntervals().remove(selectedIndex);
    	resetPrintOrder();
    	dirtyTable = true;
    	if(schedule.getMasterScheduleIntervals().size() <= MAX_ALLOWED_TASKS){
    		showTaskOverFlowError = false;
    	}
    }

    
	public void save(String stopOnDuplicateTasksFlag){
		try {
			duplicateTasks.clear();
			boolean stopOnDuplicateTasks = stopOnDuplicateTasksFlag.equalsIgnoreCase("Y") ? true : false;
			
			schedule.setMonthFrequency(months == null ? 0 : months.intValue());
			schedule.setDistanceFrequency(miles == null ? 0 : miles.intValue());
			if(validToSave()) {
				if(stopOnDuplicateTasks) {
					if(getDuplicatesInTaskList().size() > 0) {
						stopOnDuplicateTasks = true;
						duplicateTasks.addAll(getDuplicatesInTaskList());
					} else {
						stopOnDuplicateTasks = false;
					}
					 
				}
				if(!stopOnDuplicateTasks) {
					prepFields();
					maintenanceScheduleService.saveSchedule(schedule);
					addSuccessMessage("saved.success", "Schedule");
					schedule = maintenanceScheduleService.getMasterSchedule(schedule.getMschId());
					originalDistance = schedule.getDistanceFrequency();
					dirtyTable = false;
					showWarning = false;
					duplicateTasks.clear();
					copyMode = false;
				} else {
					showWarning = true;
				}
				scheduleId = schedule.getMschId();
			}
		} catch (Exception ex) {
    		super.addErrorMessage("generic.error", ex.getMessage());
		}
    }

	public String delete(){
		try {
			maintenanceScheduleService.deleteMasterSchedule(schedule);
			addSuccessMessage("deleted.success", schedule.getMasterCode() + " - Schedule");
			forwardToURL(ViewConstants.MASTER_SCHEDULES);
		} catch (Exception ex) {
			handleException("generic.error", new String[] { "deleting schedule" }, ex, "delete");
			logger.error(ex);
			return null;
		}
		
    	return super.cancelPage();      	
    }
		
	private void setNextPageValues() {
		nextPageValues = new HashMap<String, Object>();
		if(schedule != null) {
			nextPageValues.put(ViewConstants.VIEW_PARAM_SCHEDULE_ID, schedule.getMschId());
		}
		saveNextPageInitStateValues(nextPageValues);
	}

	public void reOrderTask() {
		int currentIndex = this.selectedIndex;
		if (!MALUtilities.isEmpty(taskOrderAction)) {
			List<MasterScheduleInterval> oldSchedules = schedule.getMasterScheduleIntervals();
			MasterScheduleInterval	targetMsInterval = oldSchedules.get(currentIndex);
			if (taskOrderAction.equals(TASK_ORDER_MOVEUP)) {
				MasterScheduleInterval	msIntervalToSwap = oldSchedules.get(currentIndex-1);
				targetMsInterval.setOrder(targetMsInterval.getOrder()-1);
				msIntervalToSwap.setOrder(msIntervalToSwap.getOrder()+1);
				oldSchedules.set(currentIndex-1, targetMsInterval);
				oldSchedules.set(currentIndex, msIntervalToSwap);
			}
			if (taskOrderAction.equals(TASK_ORDER_MOVEDOWN)) {
				MasterScheduleInterval	msIntervalToSwap = oldSchedules.get(currentIndex+1);
				targetMsInterval.setOrder(targetMsInterval.getOrder()+1);
				msIntervalToSwap.setOrder(msIntervalToSwap.getOrder()-1);
				oldSchedules.set(currentIndex+1, targetMsInterval);
				oldSchedules.set(currentIndex, msIntervalToSwap);
			}
		}
	}


	public String cancel(){
    	return super.cancelPage();      	
    }


	private Integer getNextPrintOrderValue() {
		Integer highest = 0;
		if(schedule.getMasterScheduleIntervals() != null) {
			for(MasterScheduleInterval msi : schedule.getMasterScheduleIntervals()) {
				if(msi.getOrder() > highest) {
					highest = msi.getOrder();
				}
			}
		}
		return highest + 1;
	}
	
	private void resetPrintOrder() {
		for(int i=0; i<schedule.getMasterScheduleIntervals().size(); i++) {
			schedule.getMasterScheduleIntervals().get(i).setOrder(i+1);
		}
	}
	
	private boolean validToSave() {
		boolean valid = true;
		
		if(selectedType == null) {
			addErrorMessage("type", "custom.message", "Type is required");
			valid = false;
		}
		if(selectedType != null && TYPE_CLIENT.equals(selectedType)) {
			if(MALUtilities.isEmpty(clientAccountCode)){
				addErrorMessage("client", "custom.message", "Client is required");
				valid = false;
			}
			
		}
		if(schedule.getDistanceFrequency() < 1) {
			addErrorMessage("miles", "custom.message", "Miles must be greater than 0");
			valid = false;
		}
		if(schedule.getMonthFrequency() < 1) {
			addErrorMessage("months", "custom.message", "Months must be greater than 0");
			valid = false;
		}
		if(schedule.getDescription().trim().isEmpty()) {
			addErrorMessage("description", "custom.message", "Description is required");
			valid = false;
		}
		if(showTaskOverFlowError){
			valid	= false;
			addErrorMessage( "schedule.err.msg.maxtask", new String[]{MAX_ALLOWED_TASKS+""});
		}
		return valid;
	}
	
	private Set<String> getDuplicatesInTaskList() {
		Set<String> duplicates = new HashSet<String>();
		if(schedule.getMasterScheduleIntervals().size() > 1) {
			Set<String> temp = new HashSet<String>();

			for(MasterScheduleInterval msi : schedule.getMasterScheduleIntervals()) {
				if (!temp.add(msi.getServiceTask().getServiceCode())) {
				    duplicates.add(msi.getServiceTask().getServiceCode());
				}
			}
			
		}

		  
		return duplicates;
	}
	
	private void setupSchedule() {
		
		if(scheduleId != null) {			
			if(copyMode) {
				schedule = maintenanceScheduleService.copyMasterSchedule(maintenanceScheduleService.getMasterSchedule(scheduleId));
				scheduleId = schedule.getMschId();
				hiddenFlag = schedule.getHiddenFlag().equalsIgnoreCase("Y") ? true : false;
				clientAccountCode	 = schedule.getClientAccount() != null ? schedule.getClientAccount().getExternalAccountPK().getAccountCode(): null;
				selectedType = schedule.getClientScheduleType().getScheduleType();
				months = Integer.valueOf(schedule.getMonthFrequency());
				miles = Integer.valueOf(schedule.getDistanceFrequency());
				originalDistance = 0;
				ruleCount = 0l;
				vehicleCount = 0l;
				
			} else {
				schedule = maintenanceScheduleService.getMasterSchedule(scheduleId);
				scheduleId = schedule.getMschId();
				clientAccountCode	 = schedule.getClientAccount() != null ? schedule.getClientAccount().getExternalAccountPK().getAccountCode(): null;
				originalDistance = schedule.getDistanceFrequency();
				hiddenFlag = schedule.getHiddenFlag().equalsIgnoreCase("Y") ? true : false;
				selectedType = schedule.getClientScheduleType().getScheduleType();
				months = Integer.valueOf(schedule.getMonthFrequency());
				miles = Integer.valueOf(schedule.getDistanceFrequency());
				ruleCount = maintenanceScheduleService.getRulesByMasterIdCount(schedule);
				vehicleCount = vehicleScheduleService.getVehSchdByMasterIdCount(schedule);
			}			
			
		} else {
			schedule = new MasterSchedule();
			schedule.setMasterScheduleIntervals(new ArrayList<MasterScheduleInterval>());
			hiddenFlag = false;
			selectedType = null;
			months = DEFAULT_MONTH_FREQUENCY;
			originalDistance = 0;
			ruleCount = 0l;
			vehicleCount = 0l;
			clientAccountCode = null;
		}
		buildTypesList();
		buildIntervalList();
		buildTaskList();
	}

	
	private void buildTypesList() {
		availableTypes = new ArrayList<SelectItem>();
		if(scheduleId == null && (!copyMode)) {
		    availableTypes.add(new SelectItem(null, "--Select One--"));	    	
	    }

		for(ClientScheduleType type : maintenanceScheduleService.getClientScheduleTypes()) {
		    availableTypes.add(new SelectItem(type.getScheduleType()));
	    }
	}

	private void buildIntervalList() {
		availableIntervals = new ArrayList<SelectItem>();
		for(int i=1; i<=DEFAULT_INTERVAL_LIST_LENGTH; i++) {
			Integer mileTotal = i*miles;
			availableIntervals.add(new SelectItem(i, mileTotal.toString()));
		}
		availableIntervals.add(new SelectItem(DISPLAY_AT_TOP_INTERVAL_VALUE, DISPLAY_AT_TOP_INTERVAL_DESC));
		availableIntervals.add(new SelectItem(NO_MILEAGE_INTERVAL_VALUE, NO_MILEAGE_INTERVAL_DESC));
	}

	private void buildTaskList() {
		availableTasks = new ArrayList<SelectItem>();
		for(ServiceTask task :  maintenanceScheduleService.getTaskListByName()) {
			if(task.getActiveFlag().equalsIgnoreCase("Y")) {
				availableTasks.add(new SelectItem(task.getSrvtId(),task.getServiceCode()));
			}
		}
	
	}

	
	private void prepFields() {
		schedule.setDescription(schedule.getDescription().trim());
		schedule.setHiddenFlag(hiddenFlag ? "Y" : "N");
		schedule.setClientScheduleType(maintenanceScheduleService.getClientScheduleTypeByType(selectedType));
		if(schedule.getDistanceFrequency() != originalDistance) {
			schedule.setMasterCode(maintenanceScheduleService.getNextMasterScheduleCode(schedule.getDistanceFrequency()));
		}
		if (MALUtilities.isEmpty(getClientAccountCode())) {
			schedule.setClientAccount(null);
		} else {
			for (ExternalAccount externalAccount : clientAccounts) {
				if (externalAccount.getExternalAccountPK().getAccountCode().equals(getClientAccountCode())) {
					schedule.setClientAccount(externalAccountDAO.findById(externalAccount.getExternalAccountPK()).orElse(null));
					break;
				}
			}
		}
	}

	public MasterSchedule getSchedule() {
		return schedule;
	}

	public void setSchedule(MasterSchedule schedule) {
		this.schedule = schedule;
	}

	

	public boolean isHiddenFlag() {
		return hiddenFlag;
	}

	public void setHiddenFlag(boolean hiddenFlag) {
		this.hiddenFlag = hiddenFlag;
	}

	public List<SelectItem> getAvailableTypes() {
		return availableTypes;
	}

	public void setAvailableTypes(List<SelectItem> availableTypes) {
		this.availableTypes = availableTypes;
	}

	public String getSelectedType() {
		return selectedType;
	}

	public void setSelectedType(String selectedType) {
		this.selectedType = selectedType;
	}

	public Integer getMonths() {
		return months;
	}

	public void setMonths(Integer months) {
		this.months = months;
	}
	
	public Integer getMiles() {
		return miles;
	}

	public void setMiles(Integer miles) {
		this.miles = miles;
	}

	public MasterScheduleInterval getSelectedTaskInterval() {
		return selectedTaskInterval;
	}

	public void setSelectedTaskInterval(MasterScheduleInterval selectedTaskInterval) {
		this.selectedTaskInterval = selectedTaskInterval;
	}

	public List<SelectItem> getAvailableIntervals() {
		return availableIntervals;
	}

	public void setAvailableIntervals(List<SelectItem> availableIntervals) {
		this.availableIntervals = availableIntervals;
	}

	public Integer getSelectedInterval() {
		return selectedInterval;
	}

	public void setSelectedInterval(Integer selectedInterval) {
		this.selectedInterval = selectedInterval;
	}

	public List<SelectItem> getAvailableTasks() {
		return availableTasks;
	}

	public void setAvailableTasks(List<SelectItem> availableTasks) {
		this.availableTasks = availableTasks;
	}

	public Long getSelectedTask() {
		return selectedTask;
	}

	public void setSelectedTask(Long selectedTask) {
		this.selectedTask = selectedTask;
	}

	public boolean isSelectedRepeatFlag() {
		return selectedRepeatFlag;
	}

	public void setSelectedRepeatFlag(boolean selectedRepeatFlag) {
		this.selectedRepeatFlag = selectedRepeatFlag;
	}

	public boolean isDirtyTable() {
		return dirtyTable;
	}

	public void setDirtyTable(boolean dirtyTable) {
		this.dirtyTable = dirtyTable;
	}	

	public boolean isShowWarning() {
		return showWarning;
	}

	public void setShowWarning(boolean showWarning) {
		this.showWarning = showWarning;
	}

	public List<String> getDuplicateTasks() {
		return duplicateTasks;
	}

	public void setDuplicateTasks(List<String> duplicateTasks) {
		this.duplicateTasks = duplicateTasks;
	}

	public int getSelectedIndex() {
		return selectedIndex;
	}

	public void setSelectedIndex(int selectedIndex) {
		this.selectedIndex = selectedIndex;
	}

	public boolean isAddMode() {
		return addMode;
	}

	public void setAddMode(boolean addMode) {
		this.addMode = addMode;
	}

	public Long getVehicleCount() {
		return vehicleCount;
	}

	public void setVehicleCount(Long vehicleCount) {
		this.vehicleCount = vehicleCount;
	}

	public Long getRuleCount() {
		return ruleCount;
	}

	public void setRuleCount(Long ruleCount) {
		this.ruleCount = ruleCount;
	}

	public Long getScheduleId() {
		return scheduleId;
	}

	public void setScheduleId(Long scheduleId) {
		this.scheduleId = scheduleId;
	}
	
	public boolean getCopyMode() {
		return copyMode;
	}

	public void setCopyMode(boolean copyMode) {
		this.copyMode = copyMode;
	}

	public String getTaskOrderAction() {
		return taskOrderAction;
	}

	public void setTaskOrderAction(String taskOrderAction) {
		this.taskOrderAction = taskOrderAction;
	}

	public String getTASK_ORDER_MOVEUP() {
		return TASK_ORDER_MOVEUP;
	}

	public void setTASK_ORDER_MOVEUP(String tASK_ORDER_MOVEUP) {
		TASK_ORDER_MOVEUP = tASK_ORDER_MOVEUP;
	}

	public String getTASK_ORDER_MOVEDOWN() {
		return TASK_ORDER_MOVEDOWN;
	}

	public void setTASK_ORDER_MOVEDOWN(String tASK_ORDER_MOVEDOWN) {
		TASK_ORDER_MOVEDOWN = tASK_ORDER_MOVEDOWN;
	}

	public boolean isHasEditPermission() {
		return hasEditPermission;
	}

	public void setHasEditPermission(boolean hasEditPermission) {
		this.hasEditPermission = hasEditPermission;
	}

	public List<ExternalAccount> getClientAccounts() {
		return clientAccounts;
	}

	public void setClientAccounts(List<ExternalAccount> clientAccounts) {
		this.clientAccounts = clientAccounts;
	}

	public boolean isEnableClientAccounts() {
		return enableClientAccounts;
	}

	public void setEnableClientAccounts(boolean enableClientAccounts) {
		this.enableClientAccounts = enableClientAccounts;
	}
	public String getClientAccountCode() {
		return clientAccountCode;
	}
	public void setClientAccountCode(String clientAccountCode) {
		this.clientAccountCode = clientAccountCode;
	}
	public List<SelectItem> getAvailableClients() {
		return availableClients;
	}
	public void setAvailableClients(List<SelectItem> availableClients) {
		this.availableClients = availableClients;
	}
	public boolean isShowTaskOverFlowError() {
		return showTaskOverFlowError;
	}
	public void setShowTaskOverFlowError(boolean showTaskOverFlowError) {
		this.showTaskOverFlowError = showTaskOverFlowError;
	}
	public String getTaskOverFlowErrorMsg() {
		return taskOverFlowErrorMsg;
	}
	public void setTaskOverFlowErrorMsg(String taskOverFlowErrorMsg) {
		this.taskOverFlowErrorMsg = taskOverFlowErrorMsg;
	}
	
	
}