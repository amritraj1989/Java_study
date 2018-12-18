package com.mikealbert.vision.view.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.el.ValueExpression;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.mikealbert.data.DataConstants;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.enumeration.LogBookTypeEnum;
import com.mikealbert.data.enumeration.VehicleStatus;
import com.mikealbert.data.vo.DriverSearchVO;
import com.mikealbert.data.vo.VehicleSearchCriteriaVO;
import com.mikealbert.data.vo.VehicleSearchResultVO;
import com.mikealbert.service.FleetMasterService;
import com.mikealbert.service.LogBookService;
import com.mikealbert.service.MaintenanceRequestService;
import com.mikealbert.service.OdometerService;
import com.mikealbert.service.VehicleScheduleService;
import com.mikealbert.service.VehicleSearchService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.view.ViewConstants;
import com.mikealbert.vision.vo.LogBookTypeVO;

@Component
@Scope("view")
public class VehicleSearchBean extends StatefulBaseBean{

	private static final long serialVersionUID = -7955010902575469173L;
	
	@Resource private VehicleSearchService vehicleService;
	@Resource private MaintenanceRequestService maintenanceRequestService;
	@Resource private FleetMasterService fleetMasterService;
	@Resource private LogBookService logBookService;
	@Resource private OdometerService odometerService;
	@Resource	private VehicleScheduleService	vehicleScheduleService;
	
	private LazyDataModel<VehicleSearchResultVO> vehicleLazyList  = null;	
	private VehicleSearchResultVO selectedSearchVO  = null;
	private int selectionIndex = -1;	
	private static final String  DATA_TABLE_UI_ID ="vehicleDataTable";
	
	// sort fields
	public static final String FIELD_DRIVER_NAME = "driverForename";
	public static final String FIELD_ACCOUNT_NAME = "accountName";
	public static final String FIELD_UNIT_NO = "unitNo";
	
	private int resultPerPage = ViewConstants.RECORDS_PER_PAGE;

	private VehicleSearchCriteriaVO searchCriteria = new VehicleSearchCriteriaVO();
	private String client;
	private boolean isMaintHistoryDisallowed = true;
	private boolean isActive = true;
	private boolean isSearchRequired = false; 
	private boolean isCreatePurchaseOrderDisallowed = true;
	private boolean isViewPurchaseOrderDisallowed = true;
	private boolean isNotesLogDisallowed = true;
	private boolean returnedFromNextPage = false; //used when determining whether or not to select first row in datatable

	private String dataTableMessage;
	
	private List<LogBookTypeVO> logBookTypes;
	private boolean	preVehSchDisallowed = true;
	private	boolean	rePriVehSchDisallowed = true;
	private	boolean	vinDetailsDisallowed = true;
	@Value("${url.locator.internal.maintenance.supplier}")
	private String locatorURL;
	
	private List<VehicleSearchResultVO> vehicleList  = null;
		
	@PostConstruct
	public void init() {
		initializeDataTable(660, 850, new int[] {2, 40, 24, 25, 15}).setHeight(0);
		openPage();
		
    	//Log Book integration		
		setLogBookTypes(new ArrayList<LogBookTypeVO>());
		getLogBookTypes().add(new LogBookTypeVO(LogBookTypeEnum.TYPE_FM_UNIT_NOTES, false));
		getLogBookTypes().add(new LogBookTypeVO(LogBookTypeEnum.TYPE_EXTERNAL_FLEET_NOTES, true));	
		getLogBookTypes().get(1).setRenderZeroEntries(false);		
		
		// we always need the corporate entity of the logged in user for searching vehicles
		searchCriteria.setCorporateEntity(getLoggedInUser().getCorporateEntity());
		vehicleLazyList = new LazyDataModel<VehicleSearchResultVO>() {
			private static final long serialVersionUID = 1L;
				
			@Override
			public List<VehicleSearchResultVO> load(int first, int pageSize, String inputSortField, SortOrder inputSortOrder, Map<String, Object> arg4) {
								int pageIdx = (first == 0) ? 0 : (first / pageSize);
				PageRequest page = new PageRequest(pageIdx,pageSize);				
				Sort sort = null;
				String querySortField = getSortField(inputSortField);
				if(MALUtilities.isNotEmptyString(querySortField)){
					if (inputSortOrder.name().equalsIgnoreCase(SortOrder.DESCENDING.toString())) {
						 sort = new Sort(Sort.Direction.DESC, querySortField);
					}else{
						 sort = new Sort(Sort.Direction.ASC, querySortField);
					}
				}else{
					// for the UI if a sort is not set we want to sort by Unit Number, Driver Name and Client Name in ASC order
					sort = new Sort(Sort.Direction.ASC, DataConstants.VEHICLE_SEARCH_SORT_FIELD_UNIT_NO, DataConstants.VEHICLE_SEARCH_SORT_FIELD_DRIVER_NAME,DataConstants.VEHICLE_SEARCH_SORT_FIELD_ACCOUNT_NAME);
				}
				
				
				vehicleList  = getLazySearchResultList(page ,sort);
				return vehicleList;
			}

			@Override
			public VehicleSearchResultVO getRowData(String rowKey) {
				for (VehicleSearchResultVO searchVO : vehicleList) {
					if (String.valueOf(searchVO.getFmsId()).equals(rowKey))
						return searchVO;
				}
				return null;
			}

			@Override
			public Object getRowKey(VehicleSearchResultVO vehicleSearchResultVO) {
				return vehicleSearchResultVO.getFmsId();
			}
		};
		vehicleLazyList.setPageSize(ViewConstants.RECORDS_PER_PAGE);
	}

	/**
	 * Sets variables on entry to the page
	 */
	protected void loadNewPage() {	
		thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_FLEET_MAINT_VEHICLE_SEARCH);
		thisPage.setPageUrl(ViewConstants.FLEET_MAINT_VEHICLE_SEARCH);
		if(thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_DRIVER_SEARCH_REQ) != null){
			if(thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_DRIVER_SEARCH_REQ).equals("true")){
				isSearchRequired = true;
			}
			else{
				isSearchRequired = false;
			}
		}
	}

	public String cancel() {
		return super.cancelPage();
	}
	
	protected void restoreOldPage() {

		this.isSearchRequired = true;
		this.searchCriteria = (VehicleSearchCriteriaVO) thisPage.getRestoreStateValues().get("VEHICLE_SEARCH_CRITERIA");
		this.selectionIndex = (Integer) thisPage.getRestoreStateValues().get("SELECTD_INDEX");
		this.setReturnedFromNextPage(true);
		this.getClientNameOrCode();

		DataTable dt = (DataTable) getComponent(DATA_TABLE_UI_ID);	
		Integer pageStartIndex = (Integer) thisPage.getRestoreStateValues().get("SELECTD_PAGE_START_INDEX");

		if(pageStartIndex != null){
			dt.setFirst(pageStartIndex);
			dt.setSortOrder((String) thisPage.getRestoreStateValues().get("DEFAULT_SORT_ORDER"));
			dt.setValueExpression("sortBy", (ValueExpression) thisPage.getRestoreStateValues().get("DEFAULT_SORT_VE"));
			this.selectedSearchVO = (VehicleSearchResultVO) thisPage.getRestoreStateValues().get(ViewConstants.DT_SELECTED_ITEM);
		}
	}
	
	//  We don't need to perform explicit search. The Primeface's LazyDataModel's load method will get call automatically 
	//	and will populate data.
	 
	public void performSearch() {
		
		this.isSearchRequired = true;
		this.isCreatePurchaseOrderDisallowed = true;
		this.isViewPurchaseOrderDisallowed = true;
		this.isNotesLogDisallowed = true;
		setMaintHistoryDisallowed(true);
		this.selectionIndex = 0;
		this.selectedSearchVO = null;
		this.returnedFromNextPage = false;
		setClientNameOrCode();
		DataTable pfDataTable = ((DataTable) getComponent(DATA_TABLE_UI_ID));
		if(pfDataTable != null)
			pfDataTable.setFirst(0);
		preVehSchDisallowed = true;
		rePriVehSchDisallowed = true;
		vinDetailsDisallowed = true;

	}
	
	private boolean validateSearchCriteria(){
		boolean valid = true;
		if(!MALUtilities.isEmpty(this.searchCriteria.getVIN())){
			if(this.searchCriteria.getVIN().length() <= 5){
				super.addErrorMessage("vin_minimum_characters");
				valid = false;
			}
		}
		if(!MALUtilities.isEmptyString(this.searchCriteria.getServiceProviderName()) || !MALUtilities.isEmptyString(this.searchCriteria.getServiceProviderInvoiceNumber())){
			if(MALUtilities.isEmptyString(this.searchCriteria.getServiceProviderName()) || MALUtilities.isEmptyString(this.searchCriteria.getServiceProviderInvoiceNumber())){
				super.addErrorMessage("provider_needed_w_invoice_vehicle_search");
				valid = false;
			}
		}
		
		if(!MALUtilities.isEmpty(this.searchCriteria.getVehSchSeq())){
			if(this.searchCriteria.getVehSchSeq().length() > 5){
				super.addErrorMessage("max.length.error",new String[]{"Schedule number","5"});
				valid = false;
			}else{
				if(!MALUtilities.isNumber(this.searchCriteria.getVehSchSeq())){
					super.addErrorMessage("plain.message",new String[]{"Schedule number must be a number"});
					valid = false;
				}
			}
		}
		
		return valid;
	}
	
	private List<VehicleSearchResultVO> getLazySearchResultList(PageRequest page, Sort sort){
		List<VehicleSearchResultVO> vehicleSearchResultsVOList =  new ArrayList<VehicleSearchResultVO>();
		if(isSearchRequired){
			if(validateSearchCriteria() == false){
				return vehicleSearchResultsVOList;
			}
			try {
					
				logger.info("-- Vehicle search start with criteria"+searchCriteria);
				
				vehicleSearchResultsVOList = vehicleService.findBySearchCriteria(searchCriteria , page, sort);
				int count  = vehicleService.findBySearchCriteriaCount(searchCriteria);
				selectionIndex =  (page.getPageSize() *  page.getPageNumber())+(selectionIndex % page.getPageSize());
				vehicleLazyList.setRowCount(count);
				vehicleLazyList.setWrappedData(vehicleSearchResultsVOList);
				adjustDataTableAfterSearch(vehicleSearchResultsVOList);
				
				logger.info("-- Vehicle search end with criteria"+searchCriteria);
			} catch (Exception ex) {
				super.addErrorMessage("generic.error", ex.getMessage());
			}			
		}

		return vehicleSearchResultsVOList;
	}
	      
    public void onRowSelect(SelectEvent event) {
    	
    	if (selectedSearchVO != null){
    		isViewPurchaseOrderDisallowed = (!MALUtilities.isEmpty(selectedSearchVO.getPurchaseOrderNumber())) ? false : true;
    		isNotesLogDisallowed = (!MALUtilities.isEmpty(selectedSearchVO.getFmsId())) ? false : true;
    	}
    	
    	if(selectedSearchVO != null && selectedSearchVO.getFmsId() != null){

    		if(selectedSearchVO.getUnitStatus().equals(VehicleStatus.STATUS_CANCELLED_ORDER.getDescription()) && 
    				odometerService.getCurrentOdometer(selectedSearchVO.getFmsId()) == null){
    			//No Odometer readings exist for this unit. Therefore, a PO cannot be created and has never been created.
    			// The first odometer reading gets entered when the Vehicle Purchase Order is released in Willow's PO001 screen.
    			isCreatePurchaseOrderDisallowed = true;
    			isMaintHistoryDisallowed = true;
    		}else{
    			isCreatePurchaseOrderDisallowed = false;
    		}
    		preVehSchDisallowed = false;
    		rePriVehSchDisallowed	= false;
    		vinDetailsDisallowed = false;
    	}
	}
	
	public void onRowUnSelect(UnselectEvent event) {
		isCreatePurchaseOrderDisallowed = true;
	}
	
	public int getSelectionIndex() {
		return selectionIndex;
	}

	public void setSelectionIndex(int selectionIndex) {
		this.selectionIndex = selectionIndex;
	}
	
	private void adjustDataTableAfterSearch(List<VehicleSearchResultVO> vehicleSearchResultsVOList){

		adjustDataTableHeight(vehicleSearchResultsVOList);
	}	
	
	@SuppressWarnings("unchecked")
	public void setSelectedSearchVO(VehicleSearchResultVO selectedSearchVO) {
		this.selectedSearchVO = selectedSearchVO;
		selectionIndex = vehicleList.indexOf(this.selectedSearchVO);
		if(getComponent(DATA_TABLE_UI_ID) != null){
			selectionIndex = selectionIndex + ((DataTable) getComponent(DATA_TABLE_UI_ID)).getFirst();
		}
		if(this.selectedSearchVO != null){
			setMaintHistoryDisallowed(false);
		}else{
			setMaintHistoryDisallowed(true);
		}
	}

	private String getSortField(String sortField) {
		if (VehicleSearchBean.FIELD_UNIT_NO.equalsIgnoreCase(sortField)) {
			return DataConstants.VEHICLE_SEARCH_SORT_FIELD_UNIT_NO;
		} else if (VehicleSearchBean.FIELD_DRIVER_NAME.equalsIgnoreCase(sortField)) {
			return DataConstants.VEHICLE_SEARCH_SORT_FIELD_DRIVER_NAME;
		} else if (VehicleSearchBean.FIELD_ACCOUNT_NAME.equalsIgnoreCase(sortField)) {
			return DataConstants.VEHICLE_SEARCH_SORT_FIELD_ACCOUNT_NAME;
		} else {
			return null;
		}
	}
	
	public void createPurchaseOrder(){
		if(selectedSearchVO != null && selectedSearchVO.getFmsId() != null){

			saveRestoreStateValues(getCurrentPageRestoreStateValuesMap());
			
			Map<String, Object> nextPageValues = new HashMap<String, Object>();
			
			nextPageValues.put(ViewConstants.VIEW_PARAM_FMS_ID, selectedSearchVO.getFmsId());
			nextPageValues.put(ViewConstants.VIEW_PARAM_MODE, ViewConstants.VIEW_MODE_ADD);		
			
			saveNextPageInitStateValues(nextPageValues);	
			
			forwardToURL(ViewConstants.FLEET_MAINT_PURCHASE_ORDER);
		}
    }
	
	public void viewPurchaseOrder(){
		if(selectedSearchVO != null && selectedSearchVO.getFmsId() != null){

			saveRestoreStateValues(getCurrentPageRestoreStateValuesMap());
			
			Map<String, Object> nextPageValues = new HashMap<String, Object>();
			
			nextPageValues.put(ViewConstants.VIEW_PARAM_MODE, ViewConstants.VIEW_MODE_EDIT);			
			nextPageValues.put(ViewConstants.VIEW_PARAM_MRQ_ID, selectedSearchVO.getMaintenanceRequestId());							
			nextPageValues.put(ViewConstants.VIEW_PARAM_FMS_ID, selectedSearchVO.getFmsId());	
			
			saveNextPageInitStateValues(nextPageValues);	
			
			forwardToURL(ViewConstants.FLEET_MAINT_PURCHASE_ORDER);
		}
    }
	public void	rePrintSchedule(){
		try{
			if(selectedSearchVO != null && selectedSearchVO.getFmsId() != null){
				vehicleScheduleService.setupRePrintVehicleSchedule(getSelectedUnit(), getLoggedInUser().getEmployeeNo());
				addSuccessMessage("process.success", "Reprint Schedule");
			}else{
				
			}
		}catch(Exception ex){
			handleException("generic.error", new String[]{"Error while reprint vehicle schedules"}, ex, null);
		}
	}
	
	private Map<String, Object> getCurrentPageRestoreStateValuesMap() {
		
		Map<String, Object> restoreStateValues = new HashMap<String, Object>();
		if(MALUtilities.isEmpty(this.getClient()) ){
			this.searchCriteria.setClientAccountName("");
			this.searchCriteria.setClientAccountNumber("");
		}
		restoreStateValues.put("VEHICLE_SEARCH_CRITERIA", this.searchCriteria);
		restoreStateValues.put("SELECTED_VEHICLE", this.selectedSearchVO);
		restoreStateValues.put("SELECTD_INDEX", this.selectionIndex);
		
		DataTable dt = (DataTable) getComponent(DATA_TABLE_UI_ID);
		//this may happen because user may directly land to driver add/edit page
		if (dt != null) {
			ValueExpression ve = dt.getValueExpression("sortBy");
			restoreStateValues.put("DEFAULT_SORT_VE", ve);
			restoreStateValues.put("SELECTD_PAGE_START_INDEX", dt.getFirst());
			restoreStateValues.put("DEFAULT_SORT_ORDER", dt.getSortOrder());
			restoreStateValues.put(ViewConstants.DT_SELECTED_ITEM, selectedSearchVO);
		}
		return restoreStateValues;
		
	}
	
	public void setClientNameOrCode() {
		//Empty the SearchCriteria before each search
		searchCriteria.setClientAccountNumber("");
		searchCriteria.setClientAccountName("");
		if(MALUtilities.isNumber(this.client)){
			searchCriteria.setClientAccountNumber(this.client);
		}
		else if(!MALUtilities.isNumber(this.client) && !MALUtilities.isEmptyString(this.client)){
			searchCriteria.setClientAccountName(this.client);
		}
	}
	
	public void getClientNameOrCode(){
		if(!MALUtilities.isEmpty(searchCriteria.getClientAccountNumber())){
			this.client = searchCriteria.getClientAccountNumber();
		}
		else{
			this.client = searchCriteria.getClientAccountName();
		}
	}
	
	/**
	 * This method is used in multiple places; If a user navigates to Service History
	 * from a commandLink(icon) a parameter is passed; If a user navigates to Service
	 * History from the button, a parameter is not passed to this method.
	 * @param vehicle Param passed when accessing method through commandLink
	 */
	public void serviceHistory(VehicleSearchResultVO vehicle){
		if(vehicle != null){
			setSelectedSearchVO(vehicle);
		}
		
		Map<String, Object> restoreStateValues = getCurrentPageRestoreStateValuesMap();		
		saveRestoreStateValues(restoreStateValues);
		
		Map<String, Object> nextPageInitValues = new HashMap<String, Object>();
		
		nextPageInitValues.put(ViewConstants.VIEW_PARAM_FMS_ID, selectedSearchVO.getFmsId());
		nextPageInitValues.put(ViewConstants.VIEW_PARAM_VIN, selectedSearchVO.getVIN());
		
		saveNextPageInitStateValues(nextPageInitValues);
		forwardToURL(ViewConstants.FLEET_MAINT_SERVICE_HISTORY);		
	}
	
	/**
	 * When log entries exist for the unit, this method will determine what styleClass to use
	 * to make the button noticeable.
	 * @return
	 */
	public String notesLogStyleClass(){
		String styleClass = "";
		
		if(!MALUtilities.isEmpty(getSelectedSearchVO())){
			if(logBookService.hasLogs(getSelectedUnit(), new ArrayList<LogBookTypeEnum>(Arrays.asList(LogBookTypeEnum.TYPE_FM_UNIT_NOTES)))){
				styleClass = ViewConstants.BUTTON_INDICATOR_STYLE_CLASS;
			}
		}
		
		return styleClass;
	}
	
	public void vinDetails(){
		
		Map<String, Object> restoreStateValues = getCurrentPageRestoreStateValuesMap();		
		saveRestoreStateValues(restoreStateValues);
		
		Map<String, Object> nextPageInitValues = new HashMap<String, Object>();
		
		nextPageInitValues.put(ViewConstants.VIEW_PARAM_UNIT_NO, selectedSearchVO.getUnitNo());
		
		saveNextPageInitStateValues(nextPageInitValues);
		forwardToURL(ViewConstants.VIN_DETAILS);		
	}	
	
	public FleetMaster getSelectedUnit(){
		return fleetMasterService.getFleetMasterByFmsId(getSelectedSearchVO().getFmsId()); 
	}
	
	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	public VehicleSearchResultVO getSelectedSearchVO() {
		return selectedSearchVO;
	}	
	
	public int getResultPerPage() {
		return resultPerPage;
	}

	public String getDataTableMessage() {
		return dataTableMessage;
	}

	public void setDataTableMessage(String dataTableMessage) {
		this.dataTableMessage = dataTableMessage;
	}
	public LazyDataModel<VehicleSearchResultVO> getVehicleLazyList() {
		return vehicleLazyList;
	}

	public void setVehicleLazyList(
			LazyDataModel<VehicleSearchResultVO> vehicleLazyList) {
		this.vehicleLazyList = vehicleLazyList;
	}
	
	public VehicleSearchCriteriaVO getSearchCriteria() {
		return searchCriteria;
	}

	public void setSearchCriteria(VehicleSearchCriteriaVO searchCriteria) {
		this.searchCriteria = searchCriteria;
	}

	public void setClient(String client) {
		this.client = client.trim();
	}
	
	public String getClient(){
		return client;
	}
	
	public boolean isCreatePurchaseOrderDisallowed() {
		return isCreatePurchaseOrderDisallowed;
	}

	public void setCreatePurchaseOrderDisallowed(boolean isCreatePurchaseOrderDisallowed) {
		this.isCreatePurchaseOrderDisallowed = isCreatePurchaseOrderDisallowed;
	}

	public boolean isViewPurchaseOrderDisallowed() {
		return isViewPurchaseOrderDisallowed;
	}

	public void setViewPurchaseOrderDisallowed(boolean isViewPurchaseOrderDisallowed) {
		this.isViewPurchaseOrderDisallowed = isViewPurchaseOrderDisallowed;
	}

	public boolean isMaintHistoryDisallowed() {
		return isMaintHistoryDisallowed;
	}

	public void setMaintHistoryDisallowed(boolean isMaintHistoryDisallowed) {
		this.isMaintHistoryDisallowed = isMaintHistoryDisallowed;
	}

	public boolean isReturnedFromNextPage() {
		return returnedFromNextPage;
	}

	public void setReturnedFromNextPage(boolean returnedFromNextPage) {
		this.returnedFromNextPage = returnedFromNextPage;
	}

	public boolean isNotesLogDisallowed() {
		return isNotesLogDisallowed;
	}

	public void setNotesLogDisallowed(boolean isNotesLogDisallowed) {
		this.isNotesLogDisallowed = isNotesLogDisallowed; 
	}
	@SuppressWarnings("rawtypes")
	private void adjustDataTableHeight(List list){
		if(list != null){
			if(list.isEmpty()){
				this.setDataTableMessage(talMessage.getMessage("no.records.found"));
				super.getDataTable().setHeight(30);
			}else{
				if(list.size() < 6){
					getDataTable().setHeight(list.size()*25);
				}else{
					getDataTable().setMaximumHeight();
				}
			}
		}else{
			this.setDataTableMessage(talMessage.getMessage("no.records.found"));
			super.getDataTable().setHeight(30);
		}
	}

	public List<LogBookTypeVO> getLogBookTypes() {
		return logBookTypes;
	}

	public void setLogBookTypes(List<LogBookTypeVO> logBookTypes) {
		this.logBookTypes = logBookTypes;
	}

	public boolean isPreVehSchDisallowed() {
		return preVehSchDisallowed;
	}

	public void setPreVehSchDisallowed(boolean preVehSchDisallowed) {
		this.preVehSchDisallowed = preVehSchDisallowed;
	}

	public boolean isRePriVehSchDisallowed() {
		return rePriVehSchDisallowed;
	}

	public void setRePriVehSchDisallowed(boolean rePriVehSchDisallowed) {
		this.rePriVehSchDisallowed = rePriVehSchDisallowed;
	}

	public boolean isVinDetailsDisallowed() {
		return vinDetailsDisallowed;
	}

	public void setVinDetailsDisallowed(boolean vinDetailsDisallowed) {
		this.vinDetailsDisallowed = vinDetailsDisallowed;
	}

	public String getLocatorURL() {
		return locatorURL;
	}

	public void setLocatorURL(String locatorURL) {
		this.locatorURL = locatorURL;
	}
	

}
