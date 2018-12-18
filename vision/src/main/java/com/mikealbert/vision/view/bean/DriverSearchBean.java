package com.mikealbert.vision.view.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.mikealbert.data.DataConstants;
import com.mikealbert.data.vo.DriverSearchVO;
import com.mikealbert.data.vo.VehicleSearchResultVO;
import com.mikealbert.service.DriverAllocationService;
import com.mikealbert.service.DriverRelationshipService;
import com.mikealbert.service.DriverService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.view.ViewConstants;

@Component
@Scope("view")
public class DriverSearchBean extends StatefulBaseBean{

	private static final long serialVersionUID = -7955010902575469173L;
	
	@Resource private DriverService driverService;
	@Resource private DriverAllocationService driverAllocService;
	@Resource private DriverRelationshipService driverRelationshipService;
	
	private LazyDataModel<DriverSearchVO> driverUnitLazyList  = null;	
	private DriverSearchVO selectedSearchVO  = null;
	private int selectionIndex = -1;	
	private static final String  DATA_TABLE_UI_ID ="driverDataTable";	
	private int resultPerPage = ViewConstants.RECORDS_PER_PAGE;
	private boolean isDriverEditDisallowed = true;
	private boolean isDriverAllocateDisallowed = true;
	private boolean isAllocateHistoryDisallowed = true;
	private boolean isDriverOverviewDisallowed = true;
	private boolean isRelationshipDisallowed = true;
	private String driverName;
	private String unitNo;
	private String vin;
	private String fleetRefNo;
	private String licensePlate;
	private String client;
	private boolean isActive = true;
	private boolean isSearchRequired = false; 
	private  String selectedDriverActiveIndicatorFlag = null; 
	private Long scrollPosition;
	
	private List<DriverSearchVO> driverList  = null;
	
	private String dataTableMessage;
	
	private String driverId; 
	
	@PostConstruct
	public void init() {
		initializeDataTable(550, 850, new int[] { 4, 14, 14, 14, 18, 10, 9, 10 ,7}).setHeight(0);
		openPage();
		
		driverUnitLazyList = new LazyDataModel<DriverSearchVO>() {
			private static final long serialVersionUID = 1L;
				
			@Override
			public List<DriverSearchVO> load(int first, int pageSize, String inputSortField, SortOrder inputSortOrder, Map<String, Object> arg4) {
								int pageIdx = (first == 0) ? 0 : (first / pageSize);
				PageRequest page = new PageRequest(pageIdx,pageSize);				
				Sort sort = null;
				String querySortField = getSortField(inputSortField);				
				if (inputSortOrder.name().equalsIgnoreCase(SortOrder.DESCENDING.toString())) {
					 sort = new Sort(Sort.Direction.DESC, querySortField);
				}else{
					 sort = new Sort(Sort.Direction.ASC, querySortField);
				}
				
				driverList  = getLazySearchResultList(page ,sort);

				return driverList;
			}

			@Override
			public DriverSearchVO getRowData(String rowKey) {
				for (DriverSearchVO searchVO : driverList) {
					if (String.valueOf(searchVO.getUniqueRowId()).equals(rowKey))
						return searchVO;
				}
				return null;
			}

			@Override
			public Object getRowKey(DriverSearchVO driverSearchVO) {
				return driverSearchVO.getUniqueRowId();
			}
		};
		driverUnitLazyList.setPageSize(ViewConstants.RECORDS_PER_PAGE);
		
	}

	/**
	 * Sets variables on entry to the page
	 */
	protected void loadNewPage() {	
		thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_DRIVER_SEARCH);
		thisPage.setPageUrl(ViewConstants.DRIVER_SEARCH);
		driverId = (String)thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_DRIVER_ID);
		driverName = (String)thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_DRIVER_NAME);
		unitNo = (String)thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_UNIT_NO);
		if(thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_DRIVER_SEARCH_REQ) != null){
			if(thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_DRIVER_SEARCH_REQ).equals("true")){
				isSearchRequired = true;
			}
			else{
				isSearchRequired = false;
			}
		}
	}

	protected void restoreOldPage() {

		this.isSearchRequired = true;
		this.driverId = (String) thisPage.getRestoreStateValues().get(ViewConstants.DRIVER_ID);
		this.driverName = (String) thisPage.getRestoreStateValues().get("DRIVER_NAME");
		this.unitNo = (String) thisPage.getRestoreStateValues().get("UNIT_NO");
		this.fleetRefNo = (String) thisPage.getRestoreStateValues().get("FLEET_REF_NO");
		this.licensePlate = (String) thisPage.getRestoreStateValues().get("LICENSE_PLATE");
		this.vin = (String) thisPage.getRestoreStateValues().get("VIN");
		this.client = (String) thisPage.getRestoreStateValues().get("CLIENT");
		this.selectionIndex = (Integer) thisPage.getRestoreStateValues().get("SELECTD_INDEX");
		this.selectedDriverActiveIndicatorFlag = (String) thisPage.getRestoreStateValues().get("DRIVER_ACTIVE_INDICATOR_FLAG");
		if(thisPage.getRestoreStateValues().get("SCROLL_POSITION") != null){
			this.setScrollPosition(Long.valueOf((String) thisPage.getRestoreStateValues().get("SCROLL_POSITION")));
		}
				
		DataTable dt = (DataTable) getComponent(DATA_TABLE_UI_ID);	
		Integer pageStartIndex = (Integer) thisPage.getRestoreStateValues().get("SELECTD_PAGE_START_INDEX");
		//this case may happen if user  directly landed to driver add/edit page
		if(pageStartIndex != null){
			dt.setFirst(pageStartIndex);
			dt.setSortOrder((String) thisPage.getRestoreStateValues().get("DEFAULT_SORT_ORDER"));
			dt.setValueExpression("sortBy", (ValueExpression) thisPage.getRestoreStateValues().get("DEFAULT_SORT_VE"));
			this.selectedSearchVO = (DriverSearchVO) thisPage.getRestoreStateValues().get(ViewConstants.DT_SELECTED_ITEM);
			this.onRowSelect(null);
		}
		
	}
	
	//  We don't need to perform explicit search. The Primeface's LazyDataModel's load method will get call automatically 
	//	and will populate data.
	 
	public void performSearch() {
		
		this.isSearchRequired = true;
		this.isDriverEditDisallowed = true;
		this.isDriverAllocateDisallowed = true;
		this.isAllocateHistoryDisallowed = true;
		this.isDriverOverviewDisallowed = true;
		this.isRelationshipDisallowed = true;
		this.selectionIndex = 0;
		this.selectedSearchVO = null;		
		DataTable pfDataTable = ((DataTable) getComponent(DATA_TABLE_UI_ID));
		if(pfDataTable != null)
			pfDataTable.setFirst(0);

	}
	
	private List<DriverSearchVO> getLazySearchResultList(PageRequest page, Sort sort){
		List<DriverSearchVO> driverSearchVOList =  new ArrayList<DriverSearchVO>();
		if(isSearchRequired){
			Long lDriverId = null;
			lDriverId = isNotNull(driverId)? Long.parseLong(driverId) : null ;
			
			// if the user enter unit related criteria then we can to include recent units; otherwise we don't
			boolean showMostRecentUnits = hasUnitCriteriaBeenSet();
			
			driverSearchVOList =  driverService.searchDriver(driverName, lDriverId, getCustomerName(), getCustomerCode(),unitNo, vin, fleetRefNo, licensePlate,showMostRecentUnits,selectedDriverActiveIndicatorFlag, page , sort);
			int count  = 	driverService.searchDriverCount(driverName, lDriverId, getCustomerName(), getCustomerCode(),unitNo, vin,fleetRefNo, licensePlate,showMostRecentUnits , selectedDriverActiveIndicatorFlag);
			selectionIndex =  page.getPageSize() *  page.getPageNumber();
			driverUnitLazyList.setRowCount(count);
			driverUnitLazyList.setWrappedData(driverSearchVOList);
			adjustDataTableAfterSearch(driverSearchVOList);
			
		}
		return driverSearchVOList;
	}
	
	private boolean hasUnitCriteriaBeenSet(){
		if(isNull(unitNo) && isNull(vin) && isNull(fleetRefNo) && isNull(licensePlate)){
			return false;
		}else{
			return true;
		}
	}
	
    public void editDriver(){
		if(selectedSearchVO != null && selectedSearchVO.getDrvId() != null){
			navigateToAddEditDriver();
		}
    }    
    
    private void navigateToAddEditDriver(){
    	saveRestoreStateValues(getCurrentPageRestoreStateValuesMap());
    	
		Map<String, Object> nextPageValues = new HashMap<String, Object>();
		
		nextPageValues.put(ViewConstants.VIEW_PARAM_DRIVER_ID, String.valueOf(selectedSearchVO.getDrvId()));
		saveNextPageInitStateValues(nextPageValues);
		forwardToURL(ViewConstants.DRIVER_ADD);
    }
    
    public void editAllocation(){
    	
		if(selectedSearchVO != null && selectedSearchVO.getUnitNo()!= null){
			saveRestoreStateValues(getCurrentPageRestoreStateValuesMap());
			
			Map<String, Object> nextPageValues = new HashMap<String, Object>();
			nextPageValues.put(ViewConstants.VIEW_PARAM_UNIT_NO, selectedSearchVO.getUnitNo());
			saveNextPageInitStateValues(nextPageValues);
			forwardToURL(ViewConstants.DRIVER_ALLOCATION);
		}
    }    
   
    public void viewAllocationHistory(){
    	
		if(selectedSearchVO != null && selectedSearchVO.getFmsId() != null){
			
			saveRestoreStateValues(getCurrentPageRestoreStateValuesMap());

			Map<String, Object> nextPageValues = new HashMap<String, Object>();
			nextPageValues.put("FMS_ID", selectedSearchVO.getFmsId());
			saveNextPageInitStateValues(nextPageValues);
			forwardToURL(ViewConstants.DRIVER_ALLOCATION_HISTORY);
		}
    }  

    public void viewDriverOverview(){
    	
		if(selectedSearchVO != null){
			
			saveRestoreStateValues(getCurrentPageRestoreStateValuesMap());

			Map<String, Object> nextPageValues = new HashMap<String, Object>();
			nextPageValues.put(ViewConstants.VIEW_PARAM_DRIVER_ID, selectedSearchVO.getDrvId());
			saveNextPageInitStateValues(nextPageValues);
			forwardToURL(ViewConstants.DRIVER_OVERVIEW);
		}
    }  

    
    public void viewRelationship(){
    		
		if(selectedSearchVO != null){
			
			saveRestoreStateValues(getCurrentPageRestoreStateValuesMap());

			Map<String, Object> nextPageValues = new HashMap<String, Object>();
			nextPageValues.put(ViewConstants.VIEW_PARAM_DRIVER_ID, selectedSearchVO.getDrvId());
			saveNextPageInitStateValues(nextPageValues);
			forwardToURL(ViewConstants.DRIVER_RELATIONSHIP);
		}
    }     
    
    public void onRowSelect(SelectEvent event) {
    	
    	if(selectedSearchVO != null){
	    	if(null != selectedSearchVO.getFmsId() && driverAllocService.unitHasCurrentAllocation(selectedSearchVO.getFmsId())){
				isDriverAllocateDisallowed = false;
			}else{
				isDriverAllocateDisallowed = true;
			}
			
			if(null != selectedSearchVO.getFmsId() && driverAllocService.unitHasHistoricAllocations(selectedSearchVO.getFmsId())){//&& (!selectedSearchVO.getVehicleStatus().equalsIgnoreCase("Pending Live"))){ Bug16363 commented to enable Drivers by Unit button for Pending live vehicle too								
				isAllocateHistoryDisallowed = false;
			}else{
				isAllocateHistoryDisallowed = true;
			}
			
			if(selectedSearchVO.getDrvId() != null){
				isDriverEditDisallowed = false;
				isDriverOverviewDisallowed = false;
			}else{
				isDriverOverviewDisallowed = true;
				isDriverEditDisallowed = true;
			}
			
			// if you are a driver (driver row selected) and you are not related you are allowed to related other drivers to yourself
			if (selectedSearchVO.getDrvId() != null 
				 && !driverRelationshipService.isRelatedDriver(selectedSearchVO.getDrvId())){	
				
				isRelationshipDisallowed = false;
			}else{
				isRelationshipDisallowed = true;
			}
    	}
	}
	
	public void onRowUnSelect(UnselectEvent event) {
		isDriverAllocateDisallowed = true;
		isDriverEditDisallowed = true;
		isAllocateHistoryDisallowed = true;
		isDriverOverviewDisallowed = true;
		isRelationshipDisallowed = true;
	}
	
	public void cancel() {
		forwardToURL(ViewConstants.DASHBOARD_PAGE);
	}
	
	public int getSelectionIndex() {
		return selectionIndex;
	}

	public void setSelectionIndex(int selectionIndex) {
		this.selectionIndex = selectionIndex;
	}
	
	private void adjustDataTableAfterSearch(List<DriverSearchVO> driverSearchVOList){
		if (driverSearchVOList.size() > 0) {
			super.getDataTable().setMaximumHeight();
		} else {
			this.setDataTableMessage(talMessage.getMessage("no.records.found"));
			super.getDataTable().setHeight(30);
		}
	}	
	
	@SuppressWarnings("unchecked")
	public void setSelectedSearchVO(DriverSearchVO selectedSearchVO) {
		this.selectedSearchVO = selectedSearchVO;
		selectionIndex = ((List<DriverSearchVO>)driverUnitLazyList.getWrappedData()).indexOf(this.selectedSearchVO);		
		selectionIndex = selectionIndex + ((DataTable) getComponent(DATA_TABLE_UI_ID)).getFirst();	
	}


	private String getSortField(String sortField) {

		if (DriverSearchVO.FIELD_DRIVER_NAME.equalsIgnoreCase(sortField)) {
			return DataConstants.DRIVER_SEARCH_SORT_FIELD_DRIVER_NAME;
		} else if (DriverSearchVO.FIELD_ACCOUNT_NAME.equalsIgnoreCase(sortField)) {
			return DataConstants.DRIVER_SEARCH_SORT_FIELD_ACCOUNT;
		} else if (DriverSearchVO.FIELD_UNIT_NO.equalsIgnoreCase(sortField)) {
			return DataConstants.DRIVER_SEARCH_SORT_FIELD_UNIT_NO;
		} else if (DriverSearchVO.FIELD_VEHICLE_STATUS.equalsIgnoreCase(sortField)) {
			return DataConstants.DRIVER_SEARCH_SORT_FIELD_VEHICLE_STATUS;
		} else {
			return DataConstants.DRIVER_SEARCH_SORT_FIELD_DEFAULT;
		}
	}
	
	private Map<String, Object> getCurrentPageRestoreStateValuesMap() {
		
		Map<String, Object> restoreStateValues = new HashMap<String, Object>();
		restoreStateValues.put(ViewConstants.DRIVER_ID, this.driverId);		
		restoreStateValues.put("DRIVER_NAME", this.driverName);
		restoreStateValues.put("UNIT_NO", this.unitNo);
		restoreStateValues.put("VIN", this.vin);
		restoreStateValues.put("FLEET_REF_NO", this.fleetRefNo);
		restoreStateValues.put("LICENSE_PLATE", this.licensePlate);
		restoreStateValues.put("CLIENT", this.client);
		restoreStateValues.put("SELECTD_INDEX", this.selectionIndex);
		restoreStateValues.put("DRIVER_ACTIVE_INDICATOR_FLAG", this.selectedDriverActiveIndicatorFlag);	
		String scrollValue = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("hiddenScrollPosition");
		restoreStateValues.put("SCROLL_POSITION", scrollValue);
		
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
	
	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public String getDriverId() {
		return driverId;
	}

	public void setDriverId(String driverId) {
		this.driverId = driverId;
	}

	public String getUnitNo() {
		return unitNo;
	}

	public void setUnitNo(String unitNo) {
		this.unitNo = unitNo;
	}

	public String getVin() {
		return vin;
	}

	public void setVin(String vin) {
		this.vin = vin;
	}

	public String getFleetRefNo() {
		return fleetRefNo;
	}

	public void setFleetRefNo(String fleetRefNo) {
		this.fleetRefNo = fleetRefNo;
	}

	public String getLicensePlate() {
		return licensePlate;
	}

	public void setLicensePlate(String licensePlate) {
		this.licensePlate = licensePlate;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	public DriverSearchVO getSelectedSearchVO() {
		return selectedSearchVO;
	}	
	
	public int getResultPerPage() {
		return resultPerPage;
	}
	public boolean isDriverEditDisallowed() {
		return isDriverEditDisallowed;
	}

	public void setDriverEditDisallowed(boolean isDriverEditDisallowed) {
		this.isDriverEditDisallowed = isDriverEditDisallowed;
	}

	public boolean isDriverAllocateDisallowed() {
		return isDriverAllocateDisallowed;
	}

	public void setDriverAllocateDisallowed(boolean isDriverAllocateDisallowed) {
		this.isDriverAllocateDisallowed = isDriverAllocateDisallowed;
	}

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}

	public String getDataTableMessage() {
		return dataTableMessage;
	}

	public void setDataTableMessage(String dataTableMessage) {
		this.dataTableMessage = dataTableMessage;
	}
	public LazyDataModel<DriverSearchVO> getDriverUnitLazyList() {
		return driverUnitLazyList;
	}

	public void setDriverUnitLazyList(
			LazyDataModel<DriverSearchVO> driverUnitLazyList) {
		this.driverUnitLazyList = driverUnitLazyList;
	}
	private String getCustomerCode(){	
		String customerCode = null ;
		if (MALUtilities.isNumber(this.client)) {
			customerCode =   this.client;
		}
		return customerCode;
	}
	
	private String getCustomerName(){	
		String customerName = null ;
		if (! MALUtilities.isNumber(this.client)) {
			customerName =   this.client;
		}
		return customerName;
	}

	public boolean isAllocateHistoryDisallowed() {
		return isAllocateHistoryDisallowed;
	}

	public void setAllocateHistoryDisallowed(boolean isAllocateHistoryDisallowed) {
		this.isAllocateHistoryDisallowed = isAllocateHistoryDisallowed;
	}

	public boolean isRelationshipDisallowed() {
		return isRelationshipDisallowed;
	}

	public void setRelationshipDisallowed(boolean isRelationshipDisallowed) {
		this.isRelationshipDisallowed = isRelationshipDisallowed;
	}

	public String getSelectedDriverActiveIndicatorFlag() {
		return selectedDriverActiveIndicatorFlag;
	}

	public void setSelectedDriverActiveIndicatorFlag(String selectedDriverActiveIndicatorFlag) {
		this.selectedDriverActiveIndicatorFlag = selectedDriverActiveIndicatorFlag;
	}

	public boolean isDriverOverviewDisallowed() {
		return isDriverOverviewDisallowed;
	}

	public void setDriverOverviewDisallowed(boolean isDriverOverviewDisallowed) {
		this.isDriverOverviewDisallowed = isDriverOverviewDisallowed;
	}

	public Long getScrollPosition() {
		return scrollPosition;
	}

	public void setScrollPosition(Long scrollPosition) {
		this.scrollPosition = scrollPosition;
	}

}
