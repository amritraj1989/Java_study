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
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.mikealbert.data.DataConstants;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.MaintenanceRequest;
import com.mikealbert.data.enumeration.LogBookTypeEnum;
import com.mikealbert.data.enumeration.MaintenanceRequestStatusEnum;
import com.mikealbert.data.vo.MaintenanceServiceHistoryVO;
import com.mikealbert.service.FleetMasterService;
import com.mikealbert.service.LogBookService;
import com.mikealbert.service.MaintenanceHistoryService;
import com.mikealbert.service.MaintenanceRequestService;
import com.mikealbert.service.VehicleScheduleService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.service.VehicleMaintenanceService;
import com.mikealbert.vision.view.ViewConstants;
import com.mikealbert.vision.view.link.EditViewDriverLink;
import com.mikealbert.vision.vo.LogBookTypeVO;


@Component
@Scope("view")
public class MaintenanceServiceHistoryBean extends StatefulBaseBean implements EditViewDriverLink{
	private static final long serialVersionUID = 2239191665631929552L;
	
	@Resource VehicleMaintenanceService vehicleMaintenanceService;
	@Resource MaintenanceRequestService maintenanceRequestService;
	@Resource MaintenanceHistoryService maintenanceHistoryService;
	@Resource LogBookService logBookService;
	@Resource FleetMasterService fleetMasterService;
	@Resource	private VehicleScheduleService	vehicleScheduleService;
	
	private static final String  DATA_TABLE_UI_ID ="maintenanceServiceHistoryDataTable";
	private List<LogBookTypeVO> logBookTypes;	
	private int resultPerPage = 20; //special case: this will help performance for units with more than 20 records
	private FleetMaster fleetMaster;
	private LazyDataModel<MaintenanceServiceHistoryVO> purchaseOrdersLazyList  = null;
	private List<MaintenanceServiceHistoryVO> filteredPurchaseOrders;
	private String dataTableMessage;
	private MaintenanceServiceHistoryVO selectedMaintenanceServiceHistoryVO = null;
	private int selectionIndex = -1;
	private boolean returnedFromNextPage;
	private String driverId;
	private Long selectedMrqId;
	private Long fmsId;
	private String vin;
	private DataTable dt;
	private String providerFilter;
	private String maintCategoryFilter;
	private String maintCodeDescFilter;
	private List<MaintenanceServiceHistoryVO> serviceHistoryList  = null;
	
	@Value("${url.locator.internal.maintenance.supplier}")
	private String locatorURL;
	/**
	 * Initializes the bean
	 */
    @PostConstruct
    public void init(){
    	
    	logger.info("-- Method name: init start");
		
    	super.openPage();
    	
    	if(!MALUtilities.isEmpty(getFmsId())){
    		//OTD-835 Refactoring...
    		setFleetMaster(fleetMasterService.getFleetMasterByFmsId(getFmsId()));

    		//Log Book Integration
    		setLogBookTypes(new ArrayList<LogBookTypeVO>());
    		getLogBookTypes().add(new LogBookTypeVO(LogBookTypeEnum.TYPE_ACTIVITY, false));  
    		getLogBookTypes().add(new LogBookTypeVO(LogBookTypeEnum.TYPE_EXTERNAL_JOB_NOTES, true));
    		getLogBookTypes().get(1).setRenderZeroEntries(false);    	
    		getLogBookTypes().add(new LogBookTypeVO(LogBookTypeEnum.TYPE_FM_UNIT_NOTES, getFleetMaster(), true)); 
    		getLogBookTypes().get(2).setRenderZeroEntries(false);    	
    		getLogBookTypes().add(new LogBookTypeVO(LogBookTypeEnum.TYPE_EXTERNAL_FLEET_NOTES, getFleetMaster(), true)); 
    		getLogBookTypes().get(3).setRenderZeroEntries(false);
    	}
    	
    	initializeDataTable(620, 830, new int[] { 4, 11, 19, 11, 8, 4, 7, 6, 8, 10, 9, 4}).setHeight(0);
        setPurchaseOrdersLazyList(new LazyDataModel<MaintenanceServiceHistoryVO>() {
			private static final long serialVersionUID = 1L;
				
			@Override
			public List<MaintenanceServiceHistoryVO> load(int first, int pageSize, String inputSortField, SortOrder inputSortOrder, Map<String,Object> filters) {
								int pageIdx = (first == 0) ? 0 : (first / pageSize);
				PageRequest page = new PageRequest(pageIdx,pageSize);				
				Sort sort = null;
				String querySortField = getSortField(inputSortField);				
				if (inputSortOrder.name().equalsIgnoreCase(SortOrder.ASCENDING.toString())) {
					 sort = new Sort(Sort.Direction.ASC, querySortField);
				}else{
					 sort = new Sort(Sort.Direction.DESC, querySortField);
				}
				
				serviceHistoryList  = getLazySearchResultList(page ,sort);

				return serviceHistoryList;
			}

			@Override
			public MaintenanceServiceHistoryVO getRowData(String rowKey) {
				if(!MALUtilities.isEmpty(serviceHistoryList)){
					for (MaintenanceServiceHistoryVO purchaseOrderVO : serviceHistoryList) {
						if (String.valueOf(purchaseOrderVO.getMrqId()).equals(rowKey))
							return purchaseOrderVO;
					}
				}
				return null;
			}

			@Override
			public Object getRowKey(MaintenanceServiceHistoryVO purchaseOrderVO) {
				return purchaseOrderVO.getMrqId();
			}
		});
        purchaseOrdersLazyList.setPageSize(ViewConstants.RECORDS_PER_PAGE);
        
        logger.info("-- Method name: init end");
    }
    
    private List<MaintenanceServiceHistoryVO> getLazySearchResultList(PageRequest page, Sort sort){
    	logger.info("-- Method name: getLazySearchResultList start");
		List<MaintenanceServiceHistoryVO> maintenanceServiceHistoryVOList=  new ArrayList<MaintenanceServiceHistoryVO>();
		int rowCount = 0;
		
		if(MALUtilities.isEmpty(this.vin)){
			maintenanceServiceHistoryVOList =  maintenanceHistoryService.getMaintenanceServiceHistoryByFmsId(this.fmsId, page , sort, providerFilter, maintCategoryFilter, maintCodeDescFilter);
			rowCount = maintenanceHistoryService.getMaintenanceServiceHistoryByFmsIdCount(this.fmsId, this.providerFilter, this.maintCategoryFilter, this.maintCodeDescFilter);
		} else {
			maintenanceServiceHistoryVOList =  maintenanceHistoryService.getMaintenanceServiceHistoryByVIN(this.vin, page , sort, providerFilter, maintCategoryFilter, maintCodeDescFilter);	
			rowCount =maintenanceHistoryService.getMaintenanceServiceHistoryByVINCount(this.vin, this.providerFilter, this.maintCategoryFilter, this.maintCodeDescFilter);
		}
				
		purchaseOrdersLazyList.setRowCount(rowCount);
		purchaseOrdersLazyList.setWrappedData(maintenanceServiceHistoryVOList);
		adjustDataTableAfterSearch(maintenanceServiceHistoryVOList);
		dt = (DataTable) getComponent(DATA_TABLE_UI_ID); //Due to commandLink datatable component needs to be set before clicking on link

		logger.info("-- Method name: getLazySearchResultList end");
		return maintenanceServiceHistoryVOList;
	}
    
    private String getSortField(String sortField) {

		if (MaintenanceServiceHistoryVO.FIELD_START_DATE.equalsIgnoreCase(sortField)) {
			return DataConstants.SERVICE_HISTORY_SORT_FIELD_START_DATE;
		} else if (MaintenanceServiceHistoryVO.FIELD_ODO.equalsIgnoreCase(sortField)) {
			return DataConstants.SERVICE_HISTORY_SORT_FIELD_ODO;
		} else if (MaintenanceServiceHistoryVO.FIELD_STATUS.equalsIgnoreCase(sortField)){
			return DataConstants.SERVICE_HISTORY_SORT_FIELD_STATUS;
		} else{
			return DataConstants.SERVICE_HISTORY_SORT_DEFAULT;
		}
	}

	@Override
	protected void loadNewPage() {
		logger.info("-- Method name: loadNewPage start");
		
		super.thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_FLEET_MAINT_SERVICE_HISTORY);
		super.thisPage.setPageUrl(ViewConstants.FLEET_MAINT_SERVICE_HISTORY);	
		Map<String, Object> map = super.thisPage.getInputValues();
		setFmsId((Long) map.get(ViewConstants.VIEW_PARAM_FMS_ID));
		setVin((String) map.get(ViewConstants.VIEW_PARAM_VIN));
		setMaintCategoryFilter((String) map.get(ViewConstants.VIEW_PARAM_MAINT_CATEGORY));
		logger.info("-- Method name: loadNewPage end");
	}


	@Override
	protected void restoreOldPage() {
		logger.info("-- Method name: restoreOldPage start");
		Map<String, Object> map = super.thisPage.getRestoreStateValues();
		setFmsId((Long) map.get(ViewConstants.VIEW_PARAM_FMS_ID));
		setVin((String) map.get(ViewConstants.VIEW_PARAM_VIN));
		this.selectionIndex = (Integer) thisPage.getRestoreStateValues().get("SELECTD_INDEX");
		// fix for FM-1468; added "empty/null" checking due to NullPointerExceptions in the log.
		if(MALUtilities.isEmpty(thisPage.getRestoreStateValues().get("NEXT_PAGE"))){
			this.setReturnedFromNextPage(false);
		}else{
			this.setReturnedFromNextPage((Boolean) thisPage.getRestoreStateValues().get("NEXT_PAGE"));
		}
		this.providerFilter = (String)thisPage.getRestoreStateValues().get("PROVIDER_FILTER");
		this.maintCategoryFilter = (String)thisPage.getRestoreStateValues().get("MAINT_CATEGORY_FILTER");
		this.maintCodeDescFilter = (String)thisPage.getRestoreStateValues().get("MAINT_CODE_DESC_FILTER");
			
		dt = (DataTable) getComponent(DATA_TABLE_UI_ID);
		Integer pageStartIndex = (Integer) thisPage.getRestoreStateValues().get("SELECTD_PAGE_START_INDEX");
		//this case may happen if user  directly landed to driver add/edit page
		if(pageStartIndex != null && dt != null){
			dt.setFirst(pageStartIndex);
			dt.setSortOrder((String) thisPage.getRestoreStateValues().get("DEFAULT_SORT_ORDER"));
			dt.setValueExpression("sortBy", (ValueExpression) thisPage.getRestoreStateValues().get("DEFAULT_SORT_VE"));
		}
		logger.info("-- Method name: restoreOldPage end");
	}
	
	public String cancel(){
		return cancelPage(); 
	}
	
	@Override
	public void editViewDriver(String driverId) {
		logger.info("-- Method name: editViewDriver start");
		Map<String, Object> restoreStateValues = getCurrentPageRestoreStateValuesMap();
		if(selectedMaintenanceServiceHistoryVO != null){
			setSelectedMrqId(selectedMaintenanceServiceHistoryVO.getMrqId());			
		}else{
			restoreStateValues.put("NEXT_PAGE", false);
		}
		saveRestoreStateValues(restoreStateValues);
				
		Map<String, Object> nextPageValues = new HashMap<String, Object>();				
		nextPageValues.put(ViewConstants.VIEW_PARAM_DRIVER_ID, driverId);
		nextPageValues.put(ViewConstants.VIEW_PARAM_CALLERS_NAME, ViewConstants.FLEET_MAINT_PURCHASE_ORDER);			
		super.saveNextPageInitStateValues(nextPageValues);
		
		logger.info("-- Method name: editViewDriver end");
		
		super.forwardToURL(ViewConstants.DRIVER_ADD);
    }
	
	public void viewPurchaseOrder(MaintenanceServiceHistoryVO maintServiceHistoryVO){
		
		logger.info("-- Method name: viewPurchaseOrder start");
		
		if(maintServiceHistoryVO == null){
			maintServiceHistoryVO = selectedMaintenanceServiceHistoryVO;
		}
		
		if(maintServiceHistoryVO != null){
			setSelectedMrqId(maintServiceHistoryVO.getMrqId());
			setSelectedMaintenanceServiceHistoryVO(maintServiceHistoryVO);
			Map<String, Object> restoreStateValues = getCurrentPageRestoreStateValuesMap();		
			saveRestoreStateValues(restoreStateValues);
	  		
			Map<String, Object> nextPageValues = new HashMap<String, Object>();
			nextPageValues.put(ViewConstants.VIEW_PARAM_MODE, ViewConstants.VIEW_MODE_EDIT);				
	  		nextPageValues.put(ViewConstants.VIEW_PARAM_MRQ_ID, getSelectedMrqId());
	  		saveNextPageInitStateValues(nextPageValues);
	  		this.forwardToURL(ViewConstants.FLEET_MAINT_PURCHASE_ORDER);
		}
		logger.info("-- Method name: viewPurchaseOrder end");
		
	}
	
	/**
	 * Copies the selected maintenance request as a goodwill PO, then navigates the user
	 * to the maintenance PO view displaying the details of the goodwill PO.
	 */
	public void createGoodwillPurchaseOrder(){	
		try{
			logger.info("-- Method name: createGoodwillPurchaseOrder start");
			Map<String, Object> restoreStateValues = getCurrentPageRestoreStateValuesMap();		
			saveRestoreStateValues(restoreStateValues);

			MaintenanceRequest goodwillMaintenanceRequest = maintenanceRequestService.createGoodwillMaintenanceRequest(
					maintenanceRequestService.getMaintenanceRequestByMrqId(getSelectedMaintenanceServiceHistoryVO().getMrqId()), getLoggedInUser().getEmployeeNo());

			Map<String, Object> nextPageValues = new HashMap<String, Object>();
			nextPageValues.put(ViewConstants.VIEW_PARAM_MODE, ViewConstants.VIEW_MODE_EDIT);				
			nextPageValues.put(ViewConstants.VIEW_PARAM_MRQ_ID, goodwillMaintenanceRequest.getMrqId());
			saveNextPageInitStateValues(nextPageValues);
			logger.info("-- Method name: createGoodwillPurchaseOrder end");
			
			this.forwardToURL(ViewConstants.FLEET_MAINT_PURCHASE_ORDER);	
		} catch (Exception ex) {
			super.addErrorMessage("generic.error", ex.getMessage());
		}   		
		
	}

	private Map<String, Object> getCurrentPageRestoreStateValuesMap() {
		
		Map<String, Object> restoreStateValues = new HashMap<String, Object>();
		restoreStateValues.put(ViewConstants.VIEW_PARAM_FMS_ID, this.fmsId);
		restoreStateValues.put(ViewConstants.VIEW_PARAM_VIN, this.vin);
		restoreStateValues.put("SELECTD_INDEX", this.selectionIndex);
		restoreStateValues.put("NEXT_PAGE", true);
		restoreStateValues.put("PROVIDER_FILTER", this.providerFilter);
		restoreStateValues.put("MAINT_CATEGORY_FILTER", this.maintCategoryFilter);
		restoreStateValues.put("MAINT_CODE_DESC_FILTER", this.maintCodeDescFilter);
		
		if (dt != null) {
			ValueExpression veSort = dt.getValueExpression("sortBy");
			ValueExpression veFilter = dt.getValueExpression("filterBy");
			restoreStateValues.put("DEFAULT_SORT_VE", veSort);
			restoreStateValues.put("DEFAULT_FILTER_VE", veFilter);
			restoreStateValues.put("SELECTD_PAGE_START_INDEX", dt.getFirst());
			restoreStateValues.put("DEFAULT_SORT_ORDER", dt.getSortOrder());
		}
		return restoreStateValues;
		
	}
	
	public void performFilter() {
		this.selectedMaintenanceServiceHistoryVO = null;
		this.returnedFromNextPage = false;
		DataTable pfDataTable = ((DataTable) getComponent(DATA_TABLE_UI_ID));
		if(pfDataTable != null)
			pfDataTable.setFirst(0);

	}
	
	public MaintenanceRequest getSelectedMRQ(){
		MaintenanceRequest mrq = null;
		
		if(!MALUtilities.isEmpty(getSelectedMaintenanceServiceHistoryVO()))
			mrq = maintenanceRequestService.getMaintenanceRequestByMrqId(getSelectedMaintenanceServiceHistoryVO().getMrqId());
		
		return mrq;
	}
	
	@SuppressWarnings("unchecked")
	public void setSelectedMaintenanceServiceHistoryVO(MaintenanceServiceHistoryVO selectedMaintenanceServiceHistoryVO) {
		if(!MALUtilities.isEmpty(selectedMaintenanceServiceHistoryVO)){
			this.selectedMaintenanceServiceHistoryVO = selectedMaintenanceServiceHistoryVO;
			selectionIndex = serviceHistoryList.indexOf(this.selectedMaintenanceServiceHistoryVO);
			selectionIndex = selectionIndex + dt.getFirst();
		}
	}
	
	public boolean isGoodwillButton(){
		boolean isDisabled = true;
		
		if(MALUtilities.isEmpty(getSelectedMaintenanceServiceHistoryVO())){
			isDisabled = true;
		} else {
			if(!maintenanceRequestService.getMaintenanceRequestByMrqId(getSelectedMaintenanceServiceHistoryVO().getMrqId()).isGoodwillIndicator()
					&& getSelectedMaintenanceServiceHistoryVO().getMaintRequestStatus().equals(MaintenanceRequestStatusEnum.MAINT_REQUEST_STATUS_COMPLETE.getDescription())){
				isDisabled = false;		
			}
		}

		return isDisabled;
	}
	
	public boolean isLogNotesDisabled(){
		boolean isDisabled = true;
		if(!MALUtilities.isEmpty(getSelectedMaintenanceServiceHistoryVO())){
			isDisabled = false;
		}
		return isDisabled;
	}
	
	private void adjustDataTableAfterSearch(List<MaintenanceServiceHistoryVO> maintenanceServiceHistoryVOList){
		adjustDataTableHeight(maintenanceServiceHistoryVOList);
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
	public void createPurchaseOrder() {
		logger.info("-- Method name: createPurchaseOrder start");
		saveRestoreStateValues(getCurrentPageRestoreStateValuesMap());

		Map<String, Object> nextPageValues = new HashMap<String, Object>();

		nextPageValues.put(ViewConstants.VIEW_PARAM_FMS_ID, this.fmsId);
		nextPageValues.put(ViewConstants.VIEW_PARAM_MODE,
				ViewConstants.VIEW_MODE_ADD);

		saveNextPageInitStateValues(nextPageValues);
		logger.info("-- Method name: createPurchaseOrder end");
		forwardToURL(ViewConstants.FLEET_MAINT_PURCHASE_ORDER);

	}
	
	/**
	 * When log entries exist for the unit, this method will determine what styleClass to use
	 * to make the button noticeable.
	 * @return
	 */
	public String notesLogStyleClass(){
		String styleClass = "";
		
		if(!MALUtilities.isEmpty(getSelectedMRQ())){
			if(logBookService.hasLogs(getSelectedMRQ().getFleetMaster(), new ArrayList<LogBookTypeEnum>(Arrays.asList(LogBookTypeEnum.TYPE_FM_UNIT_NOTES)))){
				styleClass = ViewConstants.BUTTON_INDICATOR_STYLE_CLASS;
			}
		}
		
		return styleClass;
	}
	public void	rePrintSchedule(){
		try{
			if(getFleetMaster() != null){
				vehicleScheduleService.setupRePrintVehicleSchedule(fleetMaster, getLoggedInUser().getEmployeeNo());
				addSuccessMessage("process.success", "Reprint Schedule");
			}else{
				addErrorMessage("generic.error", "Unit number is not selected for reprint schedule");
			}
		}catch(Exception ex){
			handleException("generic.error", new String[]{"Error while reprint vehicle schedules"}, ex, null);
		}
	}

	public FleetMaster getFleetMaster() {
		return fleetMaster;
	}

	public void setFleetMaster(FleetMaster fleetMaster) {
		this.fleetMaster = fleetMaster;
	}

	public List<MaintenanceServiceHistoryVO> getFilteredPurchaseOrders() {
		return filteredPurchaseOrders;
	}

	public void setFilteredPurchaseOrders(List<MaintenanceServiceHistoryVO> filteredPurchaseOrders) {
		this.filteredPurchaseOrders = filteredPurchaseOrders;
	}

	public String getDataTableMessage() {
		return dataTableMessage;
	}

	public void setDataTableMessage(String dataTableMessage) {
		this.dataTableMessage = dataTableMessage;
	}

	public Long getSelectedMrqId() {
		return selectedMrqId;
	}

	public void setSelectedMrqId(Long selectedMrqId) {
		this.selectedMrqId = selectedMrqId;
	}

	public Long getFmsId() {
		return fmsId;
	}

	public void setFmsId(Long fmsId) {
		this.fmsId = fmsId;
	}

	public String getVin() {
		return vin;
	}

	public void setVin(String vin) {
		this.vin = vin;
	}

	public LazyDataModel<MaintenanceServiceHistoryVO> getPurchaseOrdersLazyList() {
		return purchaseOrdersLazyList;
	}

	public void setPurchaseOrdersLazyList(LazyDataModel<MaintenanceServiceHistoryVO> purchaseOrdersLazyList) {
		this.purchaseOrdersLazyList = purchaseOrdersLazyList;
	}

	public int getResultPerPage() {
		return resultPerPage;
	}

	public void setResultPerPage(int resultPerPage) {
		this.resultPerPage = resultPerPage;
	}

	public int getSelectionIndex() {
		return selectionIndex;
	}

	public void setSelectionIndex(int selectionIndex) {
		this.selectionIndex = selectionIndex;
	}

	public MaintenanceServiceHistoryVO getSelectedMaintenanceServiceHistoryVO() {
		return selectedMaintenanceServiceHistoryVO;
	}

	public boolean isReturnedFromNextPage() {
		return returnedFromNextPage;
	}

	public void setReturnedFromNextPage(boolean returnedFromNextPage) {
		this.returnedFromNextPage = returnedFromNextPage;
	}

	public String getDriverId() {
		return driverId;
	}

	public void setDriverId(String driverId) {
		this.driverId = driverId;
	}

	public String getProviderFilter() {
		return providerFilter;
	}

	public void setProviderFilter(String providerFilter) {
		this.providerFilter = providerFilter;
	}

	public String getMaintCategoryFilter() {
		return maintCategoryFilter;
	}

	public void setMaintCategoryFilter(String maintCategoryFilter) {
		this.maintCategoryFilter = maintCategoryFilter;
	}

	public String getMaintCodeDescFilter() {
		return maintCodeDescFilter;
	}

	public void setMaintCodeDescFilter(String maintCodeDescFilter) {
		this.maintCodeDescFilter = maintCodeDescFilter;
	}

	public List<LogBookTypeVO> getLogBookTypes() {
		return logBookTypes;
	}

	public void setLogBookTypes(List<LogBookTypeVO> logBookTypes) {
		this.logBookTypes = logBookTypes;
	}

	public String getLocatorURL() {
		return locatorURL;
	}

	public void setLocatorURL(String locatorURL) {
		this.locatorURL = locatorURL;
	}
}
