package com.mikealbert.vision.view.bean;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.entity.MaintenanceCategory;
import com.mikealbert.data.entity.ServiceTask;
import com.mikealbert.service.MaintenanceScheduleService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.view.ViewConstants;


@Component
@Scope("view")
public class TaskAddEditBean extends StatefulBaseBean {
	
	private static final long serialVersionUID = 5459137983854538998L;

	MalLogger logger = MalLoggerFactory.getLogger(this.getClass());

	@Resource
	private	MaintenanceScheduleService	maintenanceScheduleService;

	private Long taskId;
	private ServiceTask task;
	private List<SelectItem> availableCategories;
	private String selectedCategory;
	private boolean activeFlag;
	private int scheduleCount;
	private String serviceCode;
	private	boolean	hasEditPermission;
	
	@PostConstruct
	public void init() {
		logger.debug("init is called");
		super.openPage();
		hasEditPermission = hasPermission();
		setupTask();
		
	}

	@Override
	protected void loadNewPage() {
		thisPage.setPageDisplayName("Add/Edit Task");
		thisPage.setPageUrl(ViewConstants.TASK_ADD_EDIT);
		if (thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_TASK_ID) != null) {
			setTaskId((Long) thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_TASK_ID));
		}

	}

	@Override
	protected void restoreOldPage() {}	
	
	private boolean validToSave() {
		boolean valid = true;
		
		if(selectedCategory == null) {
			addErrorMessage("category", "custom.message", "Category is required");
			valid = false;
		}
		if(task.getServiceCode() == null || MALUtilities.isEmpty(task.getServiceCode())) {
			addErrorMessage("name", "custom.message", "Name is required");
			valid = false;
		}
		if(task.getTaskDescription() == null || MALUtilities.isEmpty(task.getTaskDescription())) {
			addErrorMessage("description", "custom.message", "Description is required");
			valid = false;
		}
		if(task.getCost() == null || MALUtilities.isEmpty(task.getCost())) {
			addErrorMessage("cost", "custom.message", "Cost is required");
			valid = false;
		}
		
		return valid;
	}	
	
	public String cancel(){
    	return super.cancelPage();      	
    }

	public void save(){

		try {
			
			if(validToSave()) {
				
				if(saveTask()) {
					
					forwardToURL(ViewConstants.TASKS);
					super.cancelPage();
				} 
			}
			
		} catch (Exception ex) {
			
			super.addErrorMessage("generic.error", ex.getMessage());
		}		
	}
		
	private boolean saveTask() {
		try {
			prepFields();
			maintenanceScheduleService.saveTask(task);
			addSuccessMessage("saved.success", task.getServiceCode() + " - Task");
			return true;
		} catch (Exception ex) {
    		super.addErrorMessage("generic.error", ex.getMessage());
    		return false;
		}
	}
	
	
	private void prepFields() {
		task.setServiceCode(task.getServiceCode().trim());
		task.setTaskDescription(task.getTaskDescription().trim());
		task.setActiveFlag(activeFlag ? "Y" : "N");
		task.setMaintenanceCategory(maintenanceScheduleService.getMaintenanceCategory(selectedCategory));
	}
	

	private void setupTask() {
		if(taskId != null) {
			task = maintenanceScheduleService.getTask(taskId);
			serviceCode = task.getServiceCode().trim();
			selectedCategory = task.getMaintenanceCategory().getCode();
			activeFlag = task.getActiveFlag().equalsIgnoreCase("Y") ? true : false;
			scheduleCount = maintenanceScheduleService.getScheduleCount(task);
		} else {
			task = new ServiceTask();
			activeFlag = true;
			selectedCategory = null;
			serviceCode = null;
			scheduleCount = 0;
		}
		buildCategoryList();
	
	}
	
		
	private void buildCategoryList() {
		availableCategories = new ArrayList<SelectItem>();
		if(taskId == null) {
		    availableCategories.add(new SelectItem(null, "--Select One--"));	    	
	    }

		for(MaintenanceCategory category : maintenanceScheduleService.getMaintenanceCategories()) {
		    availableCategories.add(new SelectItem(category.getCode(), category.getCode()));
	    }
	    
	    
	}

			
	public MaintenanceScheduleService getMaintenanceScheduleService() {
		return maintenanceScheduleService;
	}

	public void setMaintenanceScheduleService(
			MaintenanceScheduleService maintenanceScheduleService) {
		this.maintenanceScheduleService = maintenanceScheduleService;
	}

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public ServiceTask getTask() {
		return task;
	}

	public void setTask(ServiceTask task) {
		this.task = task;
	}

	public List<SelectItem> getAvailableCategories() {
		return availableCategories;
	}

	public void setAvailableCategories(List<SelectItem> availableCategories) {
		this.availableCategories = availableCategories;
	}

	public String getSelectedCategory() {
		return selectedCategory;
	}

	public void setSelectedCategory(String selectedCategory) {
		this.selectedCategory = selectedCategory;
	}

	public boolean isActiveFlag() {
		return activeFlag;
	}

	public void setActiveFlag(boolean activeFlag) {
		this.activeFlag = activeFlag;
	}

	public int getScheduleCount() {
		return scheduleCount;
	}

	public void setScheduleCount(int scheduleCount) {
		this.scheduleCount = scheduleCount;
	}

	public boolean isHasEditPermission() {
		return hasEditPermission;
	}

	public void setHasEditPermission(boolean hasEditPermission) {
		this.hasEditPermission = hasEditPermission;
	}





}