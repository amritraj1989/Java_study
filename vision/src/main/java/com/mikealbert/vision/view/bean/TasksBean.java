package com.mikealbert.vision.view.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.el.ValueExpression;
import javax.faces.model.SelectItem;

import org.primefaces.component.datatable.DataTable;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.entity.MaintenanceCategory;
import com.mikealbert.data.entity.ServiceTask;
import com.mikealbert.service.MaintenanceScheduleService;
import com.mikealbert.vision.view.ViewConstants;




@Component
@Scope("view")
public class TasksBean extends StatefulBaseBean {
	
	private static final long serialVersionUID = 5459137983854538998L;

	MalLogger logger = MalLoggerFactory.getLogger(this.getClass());

	@Resource
	private	MaintenanceScheduleService	maintenanceScheduleService;

	private List<ServiceTask> rowList = new ArrayList<ServiceTask>();
	private List<ServiceTask> rawRowList;
	private int totalRecords;
	private ServiceTask selectedTask;
	private String activeFilter;
	private String categoryFilter;
	private List<SelectItem> availableActiveFlags;
	private List<SelectItem> availableCategories;
	private Long selectedTaskId;
	private Map<String, Object> nextPageValues;
	private int scheduleCount;
	private long scrollPosition;
	
	private	boolean	hasEditPermission;
	
	@PostConstruct
	public void init() {
		super.openPage();

		initializeDataTable(490, 810, new int[] { 20, 42, 8, 14, 10});
		
		rawRowList = maintenanceScheduleService.getTaskList();
		
		initializeFilters();
		filterRowList();
		sortRowList();
		setSelectedRow();
		hasEditPermission	= hasPermission();
		totalRecords = rowList.size();

	}
	
	

	@Override
	protected void loadNewPage() {
		thisPage.setPageDisplayName("Schedule Tasks");
		thisPage.setPageUrl(ViewConstants.TASKS);
	}

	@Override
	protected void restoreOldPage() {
		activeFilter = (String) thisPage.getRestoreStateValues().get("ACTIVE_FILTER");
		categoryFilter = (String) thisPage.getRestoreStateValues().get("CATEGORY_FILTER");
		selectedTaskId = (Long) thisPage.getRestoreStateValues().get("SELECTED_TASK_ID");
		this.scrollPosition = (Long)thisPage.getRestoreStateValues().get("SCROLL_POSITION");
	}	
		
	
	private void sortRowList() {
		DataTable dt = (DataTable) getComponent("resultsTable");
		if(selectedTaskId != null) {
			dt.setSortOrder((String) thisPage.getRestoreStateValues().get("DEFAULT_SORT_ORDER"));
			dt.setValueExpression("sortBy", (ValueExpression) thisPage.getRestoreStateValues().get("DEFAULT_SORT_VE"));
		} else {
			dt.setValueExpression("sortBy", null);
		}
		
	}
	
	private void setSelectedRow() {
		if(selectedTaskId != null) {
			setLastSelectedTask();			
			scheduleCount = maintenanceScheduleService.getScheduleCount(maintenanceScheduleService.getTask(selectedTaskId));
		} else {
			selectedTask = null;
			if(rowList.size() > 0) {
				selectedTask = rowList.get(0);
				selectedTaskId = selectedTask.getSrvtId();
			}
		}
	}
	
	public int getScheduleCount() {
		return scheduleCount;
	}

	public void setScheduleCount(int scheduleCount) {
		this.scheduleCount = scheduleCount;
	}
	
	private void initializeFilters() {
		
		buildActiveList();
		buildCategoryList();

		if(activeFilter == null) {
			activeFilter = "Y";			
		}
		
		if(categoryFilter == null) {
			categoryFilter = "ALL";			
		}

	}
	
	private void buildActiveList() {
	    availableActiveFlags = new ArrayList<SelectItem>();
	    availableActiveFlags.add(new SelectItem("ALL", "--All--"));
	    availableActiveFlags.add(new SelectItem("Y", "Yes"));
	    availableActiveFlags.add(new SelectItem("N", "No"));
	}
	
	private void buildCategoryList() {
	    availableCategories = new ArrayList<SelectItem>();
	    availableCategories.add(new SelectItem("ALL", "--All--"));
	    
	    for(MaintenanceCategory category : maintenanceScheduleService.getMaintenanceCategories()) {
		    availableCategories.add(new SelectItem(category.getCode(), category.getCode()));
	    }
	    
	    
	}
	
	public void removeAllFilters() {
		activeFilter = "ALL";
		categoryFilter = "ALL";
		filterRowList();
	}
	
	public void filterRowList() {
		rowList.clear();
		for(ServiceTask task : rawRowList) {
			boolean add = true;
			if(!activeFilter.equalsIgnoreCase("ALL" ) && !task.getActiveFlag().equalsIgnoreCase(activeFilter)) {
				add = false;
			}
			if(!categoryFilter.equalsIgnoreCase("ALL" ) && !task.getMaintenanceCategory().getCode().equalsIgnoreCase(categoryFilter)) {
				add = false; 
			}

			if(add) {
				rowList.add(task);
			}
		}

		// filtering always resets selection to first
		if(rowList.size() > 0) {
			selectedTask = rowList.get(0);
		}

		// filtering removes sort
		DataTable dt = (DataTable) getComponent("resultsTable");
		dt.setValueExpression("sortBy", null);
		
		totalRecords = rowList.size();
	}

	
	public String cancel(){
    	return super.cancelPage();      	
    }


	public String add(){
		selectedTask = null;
		saveRestoreStateValues(getCurrentPageRestoreStateValuesMap());
		setNextPageValues();
		return ViewConstants.TASK_ADD_EDIT;
    }

	public String edit(){
		if(selectedTask == null) {
			super.addSimplErrorMessage("No row selected");
			return null;
		}
		saveRestoreStateValues(getCurrentPageRestoreStateValuesMap());
		setNextPageValues();
		return ViewConstants.TASK_ADD_EDIT;
    }
	
	
	public String delete(){
		
		if(selectedTask == null) {
			addSimplErrorMessage("No row selected");
			return null;
		}
		
		selectedTaskId = selectedTask.getSrvtId();
		scheduleCount = maintenanceScheduleService.getScheduleCount(maintenanceScheduleService.getTask(selectedTaskId));
				
		try {
			maintenanceScheduleService.deleteTask(selectedTask);
			addSuccessMessage("deleted.success", selectedTask.getServiceCode() + " - Task");
			forwardToURL(ViewConstants.TASKS);
			setSelectedTask(null);
			setSelectedTaskId(null);
		} catch (Exception ex) {
			handleException("generic.error", new String[] { "deleting task" }, ex, "delete");
			logger.error(ex);
			return null;
		}
		saveRestoreStateValues(getCurrentPageRestoreStateValuesMap());		
		setNextPageValues();
    	return cancel();      	
    }
	
	private Map<String, Object> getCurrentPageRestoreStateValuesMap() {
		Map<String, Object> restoreStateValues = new HashMap<String, Object>();
		restoreStateValues.put("ACTIVE_FILTER", activeFilter);
		restoreStateValues.put("CATEGORY_FILTER", categoryFilter);
		if(selectedTask != null) {
			restoreStateValues.put("SELECTED_TASK_ID", selectedTask.getSrvtId());			
		}

		DataTable dt = (DataTable) getComponent("resultsTable");
		if (dt != null) {
			ValueExpression ve = dt.getValueExpression("sortBy");
			restoreStateValues.put("DEFAULT_SORT_VE", ve);
			restoreStateValues.put("DEFAULT_SORT_ORDER", dt.getSortOrder());
		}
		restoreStateValues.put("SCROLL_POSITION", scrollPosition);
		return restoreStateValues;
	}
	
	private void setNextPageValues() {
		nextPageValues = new HashMap<String, Object>();
		if(selectedTask != null) {
			nextPageValues.put(ViewConstants.VIEW_PARAM_TASK_ID, selectedTask.getSrvtId());
		} else {
			nextPageValues.put(ViewConstants.VIEW_PARAM_TASK_ID, null);			
		}
		saveNextPageInitStateValues(nextPageValues);
	}

	private void setLastSelectedTask() {
		if(selectedTaskId != null) {
			for(ServiceTask task : rowList) {
				if(task.getSrvtId().equals(selectedTaskId)) {
					selectedTask = task;
					break;
				}
			}
		}
	}
	
	
	public void processDialogRequest() {}
	
		
	public List<ServiceTask> getRowList() {
		return rowList;
	}

	public void setRowList(List<ServiceTask> rowList) {
		this.rowList = rowList;
	}

	public int getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
	}

	public ServiceTask getSelectedTask() {
		return selectedTask;
	}

	public void setSelectedTask(ServiceTask selectedTask) {
		this.selectedTask = selectedTask;
	}

	public String getActiveFilter() {
		return activeFilter;
	}

	public void setActiveFilter(String activeFilter) {
		this.activeFilter = activeFilter;
	}

	public String getCategoryFilter() {
		return categoryFilter;
	}

	public void setCategoryFilter(String categoryFilter) {
		this.categoryFilter = categoryFilter;
	}

	public List<SelectItem> getAvailableCategories() {
		return availableCategories;
	}

	public void setAvailableCategories(List<SelectItem> availableCategories) {
		this.availableCategories = availableCategories;
	}

	public List<SelectItem> getAvailableActiveFlags() {
		return availableActiveFlags;
	}

	public void setAvailableActiveFlags(List<SelectItem> availableActiveFlags) {
		this.availableActiveFlags = availableActiveFlags;
	}

	public Long getSelectedTaskId() {
		return selectedTaskId;
	}

	public void setSelectedTaskId(Long selectedTaskId) {
		this.selectedTaskId = selectedTaskId;
	}

	public boolean isHasEditPermission() {
		return hasEditPermission;
	}

	public void setHasEditPermission(boolean hasEditPermission) {
		this.hasEditPermission = hasEditPermission;
	}
	public long getScrollPosition() {
		return scrollPosition;
	}
	public void setScrollPosition(long scrollPosition) {
		this.scrollPosition = scrollPosition;
	}


}