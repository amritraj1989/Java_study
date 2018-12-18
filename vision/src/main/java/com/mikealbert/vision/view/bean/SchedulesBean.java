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
import com.mikealbert.data.entity.ClientScheduleType;
import com.mikealbert.data.entity.MasterSchedule;
import com.mikealbert.service.MaintenanceScheduleService;
import com.mikealbert.service.VehicleScheduleService;
import com.mikealbert.vision.view.ViewConstants;
import com.mikealbert.vision.vo.MasterScheduleVO;




@Component
@Scope("view")
public class SchedulesBean extends StatefulBaseBean {
	
	private static final long serialVersionUID = 5459137983854538998L;

	MalLogger logger = MalLoggerFactory.getLogger(this.getClass());

	@Resource
	private	MaintenanceScheduleService	maintenanceScheduleService;
	
	@Resource
	private	VehicleScheduleService	vehicleScheduleService;

	private List<MasterScheduleVO> finalRowList = new ArrayList<MasterScheduleVO>();
	private List<MasterScheduleVO> tempRowList;
	private int totalRecords;
	private MasterScheduleVO selectedSchedule;
	private String hiddenFilter;
	private String typeFilter;
	private List<SelectItem> availableHiddenFlags;
	private List<SelectItem> availableScheduleTypes;
	private Long selectedScheduleId;
	private boolean copyMode;
	private boolean hasEditPermission ;
	private long scrollPosition;

	private Map<String, Object> nextPageValues;
	
	@SuppressWarnings("unused")
	private ValueExpression savedVE;	
	
	@PostConstruct
	public void init() {
		super.openPage();
		hasEditPermission	= hasPermission();
		initializeDataTable(490, 810, new int[] { 10, 32, 5, 6, 6, 5, 6, 8 });
		
		loadRows();
		
		initializeFilters(); 
		filterRowList();
		sortRowList();
		setSelectedRow();	
		
		DataTable dt = (DataTable) getComponent("resultsTable");
		savedVE = dt.getValueExpression("sortBy");

		totalRecords = finalRowList.size();

	}
	
	private void loadRows() {
		
		tempRowList = new ArrayList<MasterScheduleVO>();
		
		for(MasterSchedule ms : maintenanceScheduleService.getScheduleList()) {
			MasterScheduleVO vo = new MasterScheduleVO();	
			vo.setMasterSchedule(ms);
			vo.setRuleCount(maintenanceScheduleService.getRulesByMasterIdCount(ms));
			vo.setVehicleCount(vehicleScheduleService.getVehSchdByMasterIdCount(ms));	
			tempRowList.add(vo);
		}
	}

	@Override
	protected void loadNewPage() {
		thisPage.setPageDisplayName("Master Schedules");
		thisPage.setPageUrl(ViewConstants.MASTER_SCHEDULES);	
	}

	@Override
	protected void restoreOldPage() {
		hiddenFilter = (String) thisPage.getRestoreStateValues().get("HIDDEN_FILTER");
		typeFilter = (String) thisPage.getRestoreStateValues().get("CATEGORY_FILTER");
		selectedScheduleId = (Long) thisPage.getRestoreStateValues().get("SELECTED_SCHEDULE_ID");
		this.scrollPosition = (Long)thisPage.getRestoreStateValues().get("SCROLL_POSITION");
	}	
	
	private void sortRowList() {
		DataTable dt = (DataTable) getComponent("resultsTable");
		if(selectedScheduleId != null) {
			dt.setSortOrder((String) thisPage.getRestoreStateValues().get("DEFAULT_SORT_ORDER"));
			dt.setValueExpression("sortBy", (ValueExpression) thisPage.getRestoreStateValues().get("DEFAULT_SORT_VE"));
		} else {
			dt.setValueExpression("sortBy", null);
		}
		
	}
	
	private void setSelectedRow() {
		if(selectedScheduleId != null) {
			setLastSelectedSchedule();	
		} else {
			selectedSchedule = null;
			if(finalRowList.size() > 0) {
				selectedSchedule = finalRowList.get(0);
			}
		}
	}
	
	private void initializeFilters() {
		
		buildActiveList();
		buildScheduleTypeList();

		if(hiddenFilter == null) {
			hiddenFilter = "N";			
		}
		
		if(typeFilter == null) {
			typeFilter = "ALL";			
		}

	}
	
	private void buildActiveList() {
	    availableHiddenFlags = new ArrayList<SelectItem>();
	    availableHiddenFlags.add(new SelectItem("ALL", "--All--"));
	    availableHiddenFlags.add(new SelectItem("Y", "Yes"));
	    availableHiddenFlags.add(new SelectItem("N", "No"));
	}
	
	private void buildScheduleTypeList() {
	    availableScheduleTypes = new ArrayList<SelectItem>();
	    availableScheduleTypes.add(new SelectItem("ALL", "--All--"));
	    
	    for(ClientScheduleType type : maintenanceScheduleService.getClientScheduleTypes()) {
		    availableScheduleTypes.add(new SelectItem(type.getScheduleType(), type.getScheduleType()));
	    }
	}
	
	public void removeAllFilters() {
		hiddenFilter = "ALL";
		typeFilter = "ALL";
		filterRowList();
	}
	
	public void filterRowList() {
		
		finalRowList.clear();
		for(MasterScheduleVO schedule : tempRowList) {
			boolean add = true;
			if(!hiddenFilter.equalsIgnoreCase("ALL" ) && !schedule.getMasterSchedule().getHiddenFlag().equalsIgnoreCase(hiddenFilter)) {
				add = false;
			}
			if(!typeFilter.equalsIgnoreCase("ALL" ) && !schedule.getMasterSchedule().getClientScheduleType().getScheduleType().equalsIgnoreCase(typeFilter)) {
				add = false; 
			}

			if(add) {
				finalRowList.add(schedule);
			}
		}

		// filtering always resets selection to first
		if(finalRowList.size() > 0) {
			selectedSchedule = finalRowList.get(0);
		}

		// filtering removes sort
		DataTable dt = (DataTable) getComponent("resultsTable");
		dt.setValueExpression("sortBy", null);
		
		totalRecords = finalRowList.size();
	}

	
	public String cancel(){
    	return super.cancelPage();      	
    }


	public String add(){
		selectedSchedule = null;
		saveRestoreStateValues(getCurrentPageRestoreStateValuesMap());
		setNextPageValues();
		return ViewConstants.SCHEDULE_ADD_EDIT;
    }

	public String edit(){
		if(selectedSchedule == null) {
			super.addSimplErrorMessage("No row selected");
			return null;
		}
		saveRestoreStateValues(getCurrentPageRestoreStateValuesMap());
		setNextPageValues();
		return ViewConstants.SCHEDULE_ADD_EDIT;
    }	

	public String copy(){
		copyMode = true;
		if(selectedSchedule == null) {
			super.addSimplErrorMessage("No row selected");
			return null;
		}
		saveRestoreStateValues(getCurrentPageRestoreStateValuesMap());
		setNextPageValues();
		return ViewConstants.SCHEDULE_ADD_EDIT;
    }
	
	private Map<String, Object> getCurrentPageRestoreStateValuesMap() {
		Map<String, Object> restoreStateValues = new HashMap<String, Object>();
		restoreStateValues.put("HIDDEN_FILTER", hiddenFilter);
		restoreStateValues.put("CATEGORY_FILTER", typeFilter);
		if(selectedSchedule != null) {
			restoreStateValues.put("SELECTED_SCHEDULE_ID", selectedSchedule.getMasterSchedule().getMschId());			
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
		if(selectedSchedule != null) {
			nextPageValues.put(ViewConstants.VIEW_PARAM_SCHEDULE_ID, selectedSchedule.getMasterSchedule().getMschId());
			if(copyMode) {
				nextPageValues.put(ViewConstants.VIEW_PARAM_COPY_SCHEDULE, selectedSchedule.getMasterSchedule().getMschId());
			}
		} else {
			nextPageValues.put(ViewConstants.VIEW_PARAM_SCHEDULE_ID, null);			
		}
		saveNextPageInitStateValues(nextPageValues);
	}

	private void setLastSelectedSchedule() {
		if(selectedScheduleId != null) {
			for(MasterScheduleVO schedule : finalRowList) {
				if(schedule.getMasterSchedule().getMschId().equals(selectedScheduleId)) {
					selectedSchedule = schedule;
					break;
				}
			}
		}
	}
	
	
	public void processDialogRequest() {}
	
		
	public List<MasterScheduleVO> getFinalRowList() {
		return finalRowList;
	}

	public void setFinalRowList(List<MasterScheduleVO> finalRowList) {
		this.finalRowList = finalRowList;
	}

	public int getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
	}

	public MasterScheduleVO getSelectedSchedule() {
		return selectedSchedule;
	}

	public void setSelectedSchedule(MasterScheduleVO selectedSchedule) {
		this.selectedSchedule = selectedSchedule;
	}

	public String getHiddenFilter() {
		return hiddenFilter;
	}

	public void setHiddenFilter(String hiddenFilter) {
		this.hiddenFilter = hiddenFilter;
	}

	public String getTypeFilter() {
		return typeFilter;
	}

	public void setTypeFilter(String typeFilter) {
		this.typeFilter = typeFilter;
	}

	public List<SelectItem> getAvailableScheduleTypes() {
		return availableScheduleTypes;
	}

	public void setAvailableScheduleTypes(List<SelectItem> availableScheduleTypes) {
		this.availableScheduleTypes = availableScheduleTypes;
	}

	
	public List<SelectItem> getAvailableHiddenFlags() {
		return availableHiddenFlags;
	}

	public void setAvailableHiddenFlags(List<SelectItem> availableHiddenFlags) {
		this.availableHiddenFlags = availableHiddenFlags;
	}

	public Long getSelectedScheduleId() {
		return selectedScheduleId;
	}

	public void setSelectedScheduleId(Long selectedScheduleId) {
		this.selectedScheduleId = selectedScheduleId;
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