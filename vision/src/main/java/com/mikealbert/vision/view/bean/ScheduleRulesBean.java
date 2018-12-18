package com.mikealbert.vision.view.bean;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.el.ValueExpression;
import javax.faces.model.SelectItem;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.data.SortEvent;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.dao.FuelGroupingDAO;
import com.mikealbert.data.entity.FuelGroupCode;
import com.mikealbert.data.entity.MaintScheduleRule;
import com.mikealbert.service.CustomerAccountService;
import com.mikealbert.service.MaintenanceScheduleService;
import com.mikealbert.service.MakeService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.view.ViewConstants;
import com.mikealbert.vision.vo.ScheduleRuleVO;




@Component
@Scope("view")
public class ScheduleRulesBean extends StatefulBaseBean {
	
	private static final long serialVersionUID = 1459137983844538998L;

	MalLogger logger = MalLoggerFactory.getLogger(this.getClass());

	@Resource
	private	MaintenanceScheduleService	maintenanceScheduleService;

	@Resource
	private	MakeService	makeService;

	@Resource
	private	CustomerAccountService	customerAccountService;
	@Resource
	private FuelGroupingDAO	FuelGroupingDAO;
	
	
	private List<ScheduleRuleVO> rowList = new ArrayList<ScheduleRuleVO>();
	private List<ScheduleRuleVO> rawRowList = new ArrayList<ScheduleRuleVO>();
	private int totalRecords;
	private ScheduleRuleVO selectedRule;
	private String activeFilter;
	private List<SelectItem> availableActiveFlags;
	private Long selectedRuleId;

	private Map<String, Object> nextPageValues;
	private String currentFilter;
	private List<SelectItem> availableCurrentFlags;
	private String yearFilter;
	private List<SelectItem> availableYears;
	private String makeFilter;
	private List<SelectItem> availableMakes;
	private String modelFilter;
	private List<SelectItem> availableModels;
	private String accountFilter;
	private List<SelectItem> availableAccounts;
	private String typeFilter;
	private List<SelectItem> availableTypes;
	private String nameFilter;
	private List<SelectItem> availableNames;
	private String fuelFilter;
	private List<SelectItem> availableFuelTypes;
	private Set<String> names = new HashSet<String>();
	private String scheduleTypeFilter;
	private List<SelectItem> availableScheduleTypes;
	private Set<String> scheduleTypes = new HashSet<String>();
	private Set<String> years = new HashSet<String>();
	private Set<String> models = new HashSet<String>();
	private Set<String> types = new HashSet<String>();
	private Set<String> accounts = new HashSet<String>();
	private Set<String> makes = new HashSet<String>();
	private Set<String> fuel = new HashSet<String>();
	private String highMileageFilter;
	private List<SelectItem> availableHighMileageFilters;
	private long scrollPosition;
	
	private boolean	isSortingApplied = false;
	private	boolean	hasEditPermission;
	@PostConstruct
	public void init() {
		super.openPage();

		initializeDataTable(480, 790, new int[] { 5, 6, 8, 9, 14, 20, 6, 6, 12, 10,7, 7});
		
		loadRows();
		//if(!"Y".equals(activeFilter)){
			initializeFilters();
		//}
		hasEditPermission = hasPermission();
		filterRowList();
		sortRowList();
		setSelectedRow();
		
		totalRecords = rowList.size();

	}

	@Override
	protected void loadNewPage() {
		thisPage.setPageDisplayName("Schedule Rules");
		thisPage.setPageUrl(ViewConstants.SCHEDULE_RULES);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void restoreOldPage() {
		activeFilter = (String) thisPage.getRestoreStateValues().get("ACTIVE_FILTER");
		//if(!MALUtilities.isEmpty(activeFilter) && "Y".equals(activeFilter)){
			if(thisPage.getRestoreStateValues().get("selectedFilters")!= null){
				Map<String,String> selectedFilters =(Map) thisPage.getRestoreStateValues().get("selectedFilters");
				try{
					
					for (String key : selectedFilters.keySet()) {
						Method method = this.getClass().getMethod("set"+StringUtils.capitalize(key), new Class[] {String.class});
					    method.invoke(this, selectedFilters.get(key) != null ? selectedFilters.get(key):"ALL"); 
					}
				}catch(Exception ex){
					handleException("generic.error.occured.while", new String[]{"appying filters"}, ex, null);
				}
				
			}
		//}
		selectedRuleId = (Long) thisPage.getRestoreStateValues().get("SELECTED_RULE_ID");
		isSortingApplied =thisPage.getRestoreStateValues().get("APPLY_SORTING") != null ? (Boolean)thisPage.getRestoreStateValues().get("APPLY_SORTING"):false;
		this.scrollPosition = (Long)thisPage.getRestoreStateValues().get("SCROLL_POSITION");
	}	
	
	
	public  void sortEventListener(SortEvent  sortEvent ){
		isSortingApplied = true;
	}
	
	
	private void loadRows() {
		ScheduleRuleVO rule;
		if(rawRowList != null) {
			rawRowList.clear();
		}
		for(MaintScheduleRule msc : maintenanceScheduleService.getMaintScheduleRules()) {
			rule = new ScheduleRuleVO();
			rule.setMaintScheduleRule(msc);
			rule.setMasterScheduleDescription(msc.getMasterSchedule().getClientScheduleType().getScheduleType() + ": " + msc.getMasterSchedule().getMasterCode());
			names.add(msc.getMasterSchedule().getMasterCode());
			scheduleTypes.add(msc.getMasterSchedule().getClientScheduleType().getScheduleType());
			years.add(msc.getYear());
			if(msc.getMakeModelDesc() != null) {
				models.add(msc.getMakeModelDesc());
			}
			if(msc.getModelTypeDesc() != null) {
				types.add(msc.getModelTypeDesc());
			}
			if(msc.getScheduleAccount() != null) {
				accounts.add(msc.getScheduleAccount().getExternalAccountPK().getAccountCode());
			}
			if(msc.getMakeCode() != null) {
				makes.add(msc.getMakeCode());
				rule.setMakeDescription(makeService.getMakesByCode(msc.getMakeCode()).get(0).getMakeDesc());
			}

			if (!MALUtilities.isEmpty(msc.getFuelTypeGroup())) {
				FuelGroupCode fuelGroupCode = FuelGroupingDAO.findByFuelGroupCode(msc.getFuelTypeGroup());
				fuel.add(fuelGroupCode.getFuelGroupDescription());
				rule.setFuelDescription(fuelGroupCode.getFuelGroupDescription());
			}
			rawRowList.add(rule);
		}
		buildFilterLists();
	}

	private void buildFilterLists() {
		availableNames = buildFilterList(names);
		availableScheduleTypes = buildFilterList(scheduleTypes);
		availableActiveFlags = buildYesNoList();
		availableCurrentFlags = buildYesNoList();
		availableYears = buildFilterList(years);
		availableModels = buildFilterList(models);
		availableTypes = buildFilterList(types);
		availableAccounts = buildFilterListForAccounts(accounts);
		availableMakes = buildFilterListForMakes(makes);
		availableFuelTypes = buildFilterList(fuel);
		availableHighMileageFilters	=  buildYesNoList();
	}
	
	private List<SelectItem> buildFilterList(Set<String> set) {
		ArrayList<String> sorted = new ArrayList<String>(set);
		Collections.sort(sorted);
		List<SelectItem> list = new ArrayList<SelectItem>();
		list.add(new SelectItem("ALL", "--All--"));
		for(String s : sorted) {
			list.add(new SelectItem(s));
		}
		return list;
	}

	private List<SelectItem> buildYesNoList() {
		List<SelectItem> list = new ArrayList<SelectItem>();
		list.add(new SelectItem("ALL", "--All--"));
	    list.add(new SelectItem("Y", "Yes"));
	    list.add(new SelectItem("N", "No"));
	    return list;
	}

	private List<SelectItem> buildFilterListForAccounts(Set<String> set) {
		ArrayList<String> sorted = new ArrayList<String>(set);
		Collections.sort(sorted);
		List<SelectItem> list = new ArrayList<SelectItem>();
		list.add(new SelectItem("ALL", "--All--"));
		for(String s : sorted) {
	    	String description = customerAccountService.getOpenCustomerAccountByCode(s).getAccountName();
	    	list.add(new SelectItem(s,s + " - " + description));
		}
		return list;
	}

	private List<SelectItem> buildFilterListForMakes(Set<String> set) {
		ArrayList<String> sorted = new ArrayList<String>(set);
		Collections.sort(sorted);
		List<SelectItem> list = new ArrayList<SelectItem>();
		list.add(new SelectItem("ALL", "--All--"));
		for(String s : sorted) {
	    	String description = makeService.getMakesByCode(s).get(0).getMakeDesc();
	    	list.add(new SelectItem(s,description));
		}
		return list;
	}
	
	private void sortRowList() {
		DataTable dt = (DataTable) getComponent("resultsTable");
		/*if(selectedRuleId != null) {
		dt.setSortOrder((String) thisPage.getRestoreStateValues().get("DEFAULT_SORT_ORDER"));
		dt.setValueExpression("sortBy", (ValueExpression) thisPage.getRestoreStateValues().get("DEFAULT_SORT_VE"));
	} else {
		dt.setValueExpression("sortBy", null);
	}*/
	//For MSS-207, sorting was not restored when add screen returns. 
	//Above code was restoring sorting if an existing schedule rule
	if(isSortingApplied){
		dt.setSortOrder((String) thisPage.getRestoreStateValues().get("DEFAULT_SORT_ORDER"));
		dt.setValueExpression("sortBy", (ValueExpression) thisPage.getRestoreStateValues().get("DEFAULT_SORT_VE"));
			
	}
		
	}
	
	private void setSelectedRow() {
		if(selectedRuleId != null) {
			setLastSelectedRule();
		} else {
			selectedRule = null;
			if(rowList.size() > 0) {
				selectedRule = rowList.get(0);
				selectedRuleId = selectedRule.getMaintScheduleRule().getMscId();
			}
		}
	}
	
	private void initializeFilters() {
		if(activeFilter == null) {
			activeFilter = "Y";			
		}
		if(MALUtilities.isEmpty(currentFilter))
			currentFilter = "ALL";
		if(MALUtilities.isEmpty(yearFilter))
			yearFilter = "ALL";
		if(MALUtilities.isEmpty(makeFilter))
			makeFilter = "ALL";
		if(MALUtilities.isEmpty(modelFilter))
			modelFilter = "ALL";
		if(MALUtilities.isEmpty(accountFilter))
			accountFilter = "ALL";
		if(MALUtilities.isEmpty(typeFilter))
			typeFilter = "ALL";
		if(MALUtilities.isEmpty(nameFilter))
			nameFilter = "ALL";
		if(MALUtilities.isEmpty(scheduleTypeFilter))
			scheduleTypeFilter = "ALL";
		if(MALUtilities.isEmpty(fuelFilter))
			fuelFilter = "ALL";
		if (MALUtilities.isEmpty(highMileageFilter)) {
			highMileageFilter = "ALL";
		}
	}
	
	
	public void removeAllFilters() {
		activeFilter = "ALL";
		currentFilter = "ALL";
		yearFilter = "ALL";
		makeFilter = "ALL";
		modelFilter = "ALL";
		accountFilter = "ALL";
		typeFilter = "ALL";
		nameFilter = "ALL";
		scheduleTypeFilter = "ALL";
		fuelFilter = "ALL";
		highMileageFilter = "ALL";

		rowList.clear();
		rowList.addAll(rawRowList);
		if(rowList.size() > 0) {
			selectedRule = rowList.get(0);
		}
		DataTable dt = (DataTable) getComponent("resultsTable");
		dt.setValueExpression("sortBy", null);
		
		totalRecords = rowList.size();

	}
	
	public void filterRowList() {
		rowList.clear();
		for(ScheduleRuleVO rule : rawRowList) {
			boolean add = true;
			if(!activeFilter.equalsIgnoreCase("ALL" ) && !rule.getMaintScheduleRule().getActiveFlag().equalsIgnoreCase(activeFilter)) {
				add = false;
			}
			if(!currentFilter.equalsIgnoreCase("ALL" ) && !rule.getMaintScheduleRule().getBaseSchedule().equalsIgnoreCase(currentFilter)) {
				add = false;
			}
			if(!yearFilter.equalsIgnoreCase("ALL" ) && !rule.getMaintScheduleRule().getYear().equalsIgnoreCase(yearFilter)) {
				add = false;
			}
			if(!scheduleTypeFilter.equalsIgnoreCase("ALL" ) && !rule.getMaintScheduleRule().getMasterSchedule().getClientScheduleType().getScheduleType().equalsIgnoreCase(scheduleTypeFilter)) {
				add = false;
			}
			if(!nameFilter.equalsIgnoreCase("ALL" ) && !rule.getMaintScheduleRule().getMasterSchedule().getMasterCode().equalsIgnoreCase(nameFilter)) {
				add = false;
			}
			if(!fuelFilter.equalsIgnoreCase("ALL")) {
				if(rule.getFuelDescription() == null) {
					add = false;
				}else if(!rule.getFuelDescription().equalsIgnoreCase(fuelFilter)) {
					add = false;
				}
			}
			if(!makeFilter.equalsIgnoreCase("ALL")) {
				if(rule.getMaintScheduleRule().getMakeCode() == null) {
					add = false;
				} else if(!rule.getMaintScheduleRule().getMakeCode().equals(makeFilter)) {
					add = false;
				}
			}
			if(!modelFilter.equalsIgnoreCase("ALL")) {
				if(rule.getMaintScheduleRule().getMakeModelDesc() == null) {
					add = false;
				} else if(!rule.getMaintScheduleRule().getMakeModelDesc().equals(modelFilter)) {
					add = false;
				}
			}
			if(!accountFilter.equalsIgnoreCase("ALL")) {
				if(rule.getMaintScheduleRule().getScheduleAccount() == null) {
					add = false;
				} else if(!rule.getMaintScheduleRule().getScheduleAccount().getExternalAccountPK().getAccountCode().equalsIgnoreCase(accountFilter)) {
					add = false;
				}
			}
			if(!typeFilter.equalsIgnoreCase("ALL")) {
				if(rule.getMaintScheduleRule().getModelTypeDesc() == null) {
					add = false;
				} else if(!rule.getMaintScheduleRule().getModelTypeDesc().equalsIgnoreCase(typeFilter)) {
					add = false;
				}
			}
			if (!highMileageFilter.equalsIgnoreCase("ALL") && !highMileageFilter.equalsIgnoreCase(rule.getMaintScheduleRule().getHighMileage())) {
				add = false;
			}
			
			if(add) {
				rowList.add(rule);
			}
		}

		// filtering always resets selection to first
		if(rowList.size() > 0) {
			selectedRule = rowList.get(0);
		}

		// filtering removes sort
		DataTable dt = (DataTable) getComponent("resultsTable");
		if(dt != null) {
			dt.setValueExpression("sortBy", null);
		}
		
		totalRecords = rowList.size();
	}

	
	public String cancel(){
    	return super.cancelPage();      	
    }


	public String add(){
		selectedRule = null;
		saveRestoreStateValues(getCurrentPageRestoreStateValuesMap());
		setNextPageValues();
		return ViewConstants.SCHEDULE_RULE_ADD_EDIT;
    }

	public String edit(){
		if(selectedRule == null) {
			super.addSimplErrorMessage("No row selected");
			return null;
		}
		saveRestoreStateValues(getCurrentPageRestoreStateValuesMap());
		setNextPageValues();
		return ViewConstants.SCHEDULE_RULE_ADD_EDIT;
    }
	

	public void prepDelete() {
		// this method is here to ensure the selected row gets set before we process the delete action from the confirmation button
		;
	}
	
	
	public String delete(){
		
		if(selectedRule == null) {
			super.addSimplErrorMessage("No row selected");
			return null;
		}

		selectedRuleId = selectedRule.getMaintScheduleRule().getMscId();
		try {
			maintenanceScheduleService.deleteRule(selectedRule.getMaintScheduleRule());
			addSuccessMessage("deleted.success", "Maintenance Schedule Rule");
			forwardToURL(ViewConstants.SCHEDULE_RULES);
			setSelectedRule(null);
			setSelectedRuleId(null);
		} catch (Exception ex) {
			handleException("generic.error", new String[] { "deleting rule" }, ex, "delete");
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
		//if("Y".equals(activeFilter)){
			Map<String, String> selectedFilters = new HashMap<String, String>();
			selectedFilters.put("currentFilter", currentFilter);
			selectedFilters.put("yearFilter", yearFilter);
			selectedFilters.put("scheduleTypeFilter", scheduleTypeFilter);
			selectedFilters.put("nameFilter", nameFilter);
			selectedFilters.put("accountFilter", accountFilter);
			selectedFilters.put("typeFilter", typeFilter);
			selectedFilters.put("makeFilter", makeFilter);
			selectedFilters.put("modelFilter", modelFilter);
			selectedFilters.put("fuelFilter", fuelFilter);
			selectedFilters.put("highMileageFilter", highMileageFilter);
			restoreStateValues.put("selectedFilters", selectedFilters);
					
		//}
		if(selectedRule != null) {
			restoreStateValues.put("SELECTED_RULE_ID", selectedRule.getMaintScheduleRule().getMscId());			
		}
		restoreStateValues.put("APPLY_SORTING",isSortingApplied);

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
		if(selectedRule != null) {
			nextPageValues.put(ViewConstants.VIEW_PARAM_SCHEDULE_RULE_ID, selectedRule.getMaintScheduleRule().getMscId());
			if(selectedRule.getMaintScheduleRule().getMasterSchedule() != null)
				nextPageValues.put(ViewConstants.VIEW_PARAM_SCHEDULE_ID, selectedRule.getMaintScheduleRule().getMasterSchedule().getMschId());
		} else {
			nextPageValues.put(ViewConstants.VIEW_PARAM_SCHEDULE_RULE_ID, null);			
		}
		saveNextPageInitStateValues(nextPageValues);
	}

	private void setLastSelectedRule() {
		if(selectedRuleId != null) {
			for(ScheduleRuleVO rule : rowList) {
				if(rule.getMaintScheduleRule().getMscId().equals(selectedRuleId)) {
					selectedRule = rule;
					break;
				}
			}
		}
	}
	
	
	public void processFilterRequest() {}
	
		
	public List<ScheduleRuleVO> getRowList() {
		return rowList;
	}

	public void setRowList(List<ScheduleRuleVO> rowList) {
		this.rowList = rowList;
	}

	public int getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
	}

	public String getActiveFilter() {
		return activeFilter;
	}

	public void setActiveFilter(String activeFilter) {
		this.activeFilter = activeFilter;
	}

	public List<SelectItem> getAvailableActiveFlags() {
		return availableActiveFlags;
	}

	public void setAvailableActiveFlags(List<SelectItem> availableActiveFlags) {
		this.availableActiveFlags = availableActiveFlags;
	}

	public ScheduleRuleVO getSelectedRule() {
		return selectedRule;
	}

	public void setSelectedRule(ScheduleRuleVO selectedRule) {
		this.selectedRule = selectedRule;
	}

	public Long getSelectedRuleId() {
		return selectedRuleId;
	}

	public void setSelectedRuleId(Long selectedRuleId) {
		this.selectedRuleId = selectedRuleId;
	}

	public List<SelectItem> getAvailableCurrentFlags() {
		return availableCurrentFlags;
	}

	public void setAvailableCurrentFlags(List<SelectItem> availableCurrentFlags) {
		this.availableCurrentFlags = availableCurrentFlags;
	}

	public String getCurrentFilter() {
		return currentFilter;
	}

	public void setCurrentFilter(String currentFilter) {
		this.currentFilter = currentFilter;
	}

	public String getYearFilter() {
		return yearFilter;
	}

	public void setYearFilter(String yearFilter) {
		this.yearFilter = yearFilter;
	}

	public List<SelectItem> getAvailableYears() {
		return availableYears;
	}

	public void setAvailableYears(List<SelectItem> availableYears) {
		this.availableYears = availableYears;
	}

	public String getMakeFilter() {
		return makeFilter;
	}

	public void setMakeFilter(String makeFilter) {
		this.makeFilter = makeFilter;
	}

	public List<SelectItem> getAvailableMakes() {
		return availableMakes;
	}

	public void setAvailableMakes(List<SelectItem> availableMakes) {
		this.availableMakes = availableMakes;
	}

	public String getModelFilter() {
		return modelFilter;
	}

	public void setModelFilter(String modelFilter) {
		this.modelFilter = modelFilter;
	}

	public List<SelectItem> getAvailableModels() {
		return availableModels;
	}

	public void setAvailableModels(List<SelectItem> availableModels) {
		this.availableModels = availableModels;
	}

	public String getAccountFilter() {
		return accountFilter;
	}

	public void setAccountFilter(String accountFilter) {
		this.accountFilter = accountFilter;
	}

	public List<SelectItem> getAvailableAccounts() {
		return availableAccounts;
	}

	public void setAvailableAccounts(List<SelectItem> availableAccounts) {
		this.availableAccounts = availableAccounts;
	}

	public String getTypeFilter() {
		return typeFilter;
	}

	public void setTypeFilter(String typeFilter) {
		this.typeFilter = typeFilter;
	}

	public List<SelectItem> getAvailableTypes() {
		return availableTypes;
	}

	public void setAvailableTypes(List<SelectItem> availableTypes) {
		this.availableTypes = availableTypes;
	}

	public String getNameFilter() {
		return nameFilter;
	}

	public void setNameFilter(String nameFilter) {
		this.nameFilter = nameFilter;
	}

	public List<SelectItem> getAvailableNames() {
		return availableNames;
	}

	public void setAvailableNames(List<SelectItem> availableNames) {
		this.availableNames = availableNames;
	}

	public String getScheduleTypeFilter() {
		return scheduleTypeFilter;
	}

	public void setScheduleTypeFilter(String scheduleTypeFilter) {
		this.scheduleTypeFilter = scheduleTypeFilter;
	}

	public List<SelectItem> getAvailableScheduleTypes() {
		return availableScheduleTypes;
	}

	public void setAvailableScheduleTypes(List<SelectItem> availableScheduleTypes) {
		this.availableScheduleTypes = availableScheduleTypes;
	}
	
	public String getFuelFilter() {
		return fuelFilter;
	}

	public void setFuelFilter(String fuelFilter) {
		this.fuelFilter = fuelFilter;
	}
	
	public List<SelectItem> getAvailableFuelTypes() {
		return availableFuelTypes;
	}

	public void setAvailableFuelTypes(List<SelectItem> availableFuelTypes) {
		this.availableFuelTypes = availableFuelTypes;
	}

	public boolean isHasEditPermission() {
		return hasEditPermission;
	}

	public void setHasEditPermission(boolean hasEditPermission) {
		this.hasEditPermission = hasEditPermission;
	}

	public String getHighMileageFilter() {
		return highMileageFilter;
	}

	public void setHighMileageFilter(String highMileageFilter) {
		this.highMileageFilter = highMileageFilter;
	}

	public List<SelectItem> getAvailableHighMileageFilters() {
		return availableHighMileageFilters;
	}

	public void setAvailableHighMileageFilters(List<SelectItem> availableHighMileageFilters) {
		this.availableHighMileageFilters = availableHighMileageFilters;
	}
	public long getScrollPosition() {
		return scrollPosition;
	}
	public void setScrollPosition(long scrollPosition) {
		this.scrollPosition = scrollPosition;
	}

}