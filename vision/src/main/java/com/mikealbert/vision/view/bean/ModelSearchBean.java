package com.mikealbert.vision.view.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleSelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.mikealbert.data.entity.ModelType;
import com.mikealbert.data.entity.User;
import com.mikealbert.data.enumeration.LogBookTypeEnum;
import com.mikealbert.data.vo.ModelSearchCriteriaVO;
import com.mikealbert.data.vo.ModelSearchResultVO;
import com.mikealbert.service.FleetMasterService;
import com.mikealbert.service.LogBookService;
import com.mikealbert.service.LookupCacheService;
import com.mikealbert.service.MaintenanceRequestService;
import com.mikealbert.service.ModelSearchService;
import com.mikealbert.service.OdometerService;
import com.mikealbert.service.VehicleSearchService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.view.ViewConstants;

@Component
@Scope("view")
public class ModelSearchBean extends StatefulBaseBean{

	private static final long serialVersionUID = 1573153692578673107L;
	
	@Resource private LookupCacheService lookupCacheService;
	@Resource private ModelSearchService modelSearchService;
	
	static final String RESTORE_PARAM_CRITERIA = "TRIM_SEARCH_CRITERIA";
	static final String RESTORE_PARAM_SELECTED_INDICES = "TRIM_SEARCH_RESULT_INDICES";
	static final String CONTROL_RESULT_DATATABLE = "searchResultDataTable";
	
	private ModelSearchCriteriaVO criteria;	
	private List<String> years;
	private List<String> makes;
	private List<String> models;
	private List<ModelType> modelTypes;
	private LazyDataModel<ModelSearchResultVO> lazyModelSearchResults  = null;	
	private List<ModelSearchResultVO> selectedResultVOs  = null;
	private boolean modelPricingDisabled;
	
	@Resource private VehicleSearchService vehicleService;
	@Resource private MaintenanceRequestService maintenanceRequestService;
	@Resource private FleetMasterService fleetMasterService;
	@Resource private LogBookService logBookService;
	@Resource private OdometerService odometerService;
	
	private int year;
	private String make;
	
	

	private int selectionIndex = -1;	
	private static final String  DATA_TABLE_UI_ID ="searchResultDataTable";
	
	// sort fields
	public static String FIELD_DRIVER_NAME = "driverForename";
	public static String FIELD_ACCOUNT_NAME = "accountName";
	public static String FIELD_UNIT_NO = "unitNo";
	
	private int resultPerPage = ViewConstants.RECORDS_PER_PAGE_MEDIUM;

	private String client;
	private boolean isMaintHistoryDisallowed = true;
	private boolean isActive = true;
	private boolean isSearchRequired = false; 
	private boolean isCreatePurchaseOrderDisallowed = true;
	private boolean isViewPurchaseOrderDisallowed = true;
	private boolean isNotesLogDisallowed = true;
	private boolean returnedFromNextPage = false; //used when determining whether or not to select first row in datatable

	private String dataTableMessage;
	
	private final LogBookTypeEnum logBookType = LogBookTypeEnum.TYPE_FM_UNIT_NOTES;	
	
	private List<ModelSearchResultVO> modelSearchResults  = null;
	
		
	@PostConstruct
	public void init() {
		initializeDataTable(550, 850, new int[] { 2, 5, 10, 10, 35, 11, 12, 10}).setHeight(0);
		openPage();
			
		setYears(modelSearchService.findDistinctYears(getCriteria()));
		setMakes(modelSearchService.findDistinctMakes(getCriteria()));
		setModelTypes(modelSearchService.getModelTypes());
		setModelPricingDisabled(true);
		
		// we always need the corporate entity of the logged in user for searching vehicles
		lazyModelSearchResults = new LazyDataModel<ModelSearchResultVO>() {
			private static final long serialVersionUID = 1L;
				
			@Override
			public List<ModelSearchResultVO> load(int first, int pageSize, String sortBy, SortOrder sortOrder, Map<String, Object> arg4) {
				int pageIdx = (first == 0) ? 0 : (first / pageSize);
				PageRequest page = new PageRequest(pageIdx, pageSize);				
				Sort sort = null;
				
				if(MALUtilities.isNotEmptyString(sortBy)){
					if (sortOrder.name().equalsIgnoreCase(SortOrder.DESCENDING.toString())) {
						 sort = new Sort(Sort.Direction.DESC, modelSearchService.resolveSortByName(sortBy));
					}else{
						 sort = new Sort(Sort.Direction.ASC, modelSearchService.resolveSortByName(sortBy));
					}
				}
				
				modelSearchResults  = getLazySearchResultList(page ,sort);
				return modelSearchResults;
			}

			@Override
			public ModelSearchResultVO getRowData(String rowKey) {
				for (ModelSearchResultVO resultVO : modelSearchResults) {
					if (String.valueOf(resultVO.getMdlId()).equals(rowKey))
						return resultVO;
				}
				return null;
			}

			@Override
			public Object getRowKey(ModelSearchResultVO modelSearchResultVO) {
				return modelSearchResultVO.getMdlId();
			}
		};
		lazyModelSearchResults.setPageSize(getResultPerPage());
	}

	public void changeMfgCode(){
		setYears(modelSearchService.findDistinctYears(getCriteria()));
		setMakes(modelSearchService.findDistinctMakes(getCriteria()));
	}

	public void changeYear(){
		setMakes(modelSearchService.findDistinctMakes(getCriteria()));
	}
	
	public String makeColumnName(){
		return ModelSearchService.SORT_BY_MAKE;
	}
	
	public String modelColumnName(){
		return ModelSearchService.SORT_BY_MODEL;
	}
	
	public String trimColumnName(){
		return ModelSearchService.SORT_BY_TRIM;
	}
	
	public String mfgCodeColumnName(){
		return ModelSearchService.SORT_BY_MFG_CODE;
	}	
	
	/**
	 * PATTERN: Initializes the view based on the incoming parameters.
	 */
	protected void loadNewPage() {	
		thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_TRIM_SEARCH);
		thisPage.setPageUrl(ViewConstants.TRIM_SEARCH);
		setCriteria(new ModelSearchCriteriaVO());
		
		//TODO Determine if this is needed
		if(thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_DRIVER_SEARCH_REQ) != null){
			if(thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_DRIVER_SEARCH_REQ).equals("true")){
				isSearchRequired = true;
			}
			else{
				isSearchRequired = false;
			}
		}
	}

	/**
	 * PATTERN: Initializes the view back to its initial state.
	 * NOTE: We don't need to perform explicit search. The Primeface's LazyDataModel's 
	 * load method will get call automatically and will populate data.
	 */
	protected void restoreOldPage() {

		this.isSearchRequired = true;
		setCriteria((ModelSearchCriteriaVO) thisPage.getRestoreStateValues().get(RESTORE_PARAM_CRITERIA));
		this.setReturnedFromNextPage(true);		
	}
	
	public List<String> getYears() {
		return years;
	}

	public void setYears(List<String> years) {
		this.years = years;
	}

	public ModelSearchCriteriaVO getCriteria() {
		return criteria;
	}

	public void setCriteria(ModelSearchCriteriaVO criteria) {
		this.criteria = criteria;
	}

	public void performSearch() {
				
		this.isSearchRequired = true;
		this.isCreatePurchaseOrderDisallowed = true;
		this.isViewPurchaseOrderDisallowed = true;
		this.isNotesLogDisallowed = true;
		setMaintHistoryDisallowed(true);
		this.selectionIndex = 0;
		this.selectedResultVOs = null;
		this.returnedFromNextPage = false;
		DataTable pfDataTable = ((DataTable) getComponent(DATA_TABLE_UI_ID));
		if(pfDataTable != null)
			pfDataTable.setFirst(0);
	}
	
	public void maintainModelPricing(){		
		Map<String, Object> nextPageValues = new HashMap<String, Object>();

		saveRestoreStateValues(getCurrentPageRestoreStateValuesMap());
		
		nextPageValues.put(ViewConstants.VIEW_PARAM_MDL_ID, getSelectedResultVOs().get(0).getMdlId());		
		saveNextPageInitStateValues(nextPageValues);		
		
		forwardToURL(ViewConstants.MODEL_PRICING);		
	}
	
	private List<ModelSearchResultVO> getLazySearchResultList(PageRequest page, Sort sort){
		List<ModelSearchResultVO> modelSearchResult =  new ArrayList<ModelSearchResultVO>();
		if(isSearchRequired){
			
			try {
				String userName = "";

				Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
				if(authentication != null && authentication.getPrincipal() != null){
					userName = ((User)authentication.getPrincipal()).getUsername();
				}				
				logger.info(userName+"-- Model search start with criteria " + criteria);
				
				modelSearchResult = modelSearchService.findModels(criteria , page, sort);
				int count  = modelSearchService.findModelsCount(criteria);
				selectionIndex =  page.getPageSize() *  page.getPageNumber();
				lazyModelSearchResults.setRowCount(count);
				lazyModelSearchResults.setWrappedData(modelSearchResult);
				adjustDataTableAfterSearch(modelSearchResult);
				
				logger.info(userName+"-- Model search end with criteria " + criteria);
			} catch (Exception ex) {
				super.addErrorMessage("generic.error", ex.getMessage());
			}			
		}

		return modelSearchResult;
	}
	      
    public void onRowSelect(ToggleSelectEvent event) {
    	setModelPricingDisabled(getSelectedResultVOs().size() == 1 ? false : true);
    }
    
    public void onRowSelect(SelectEvent event) {
    	setModelPricingDisabled(getSelectedResultVOs().size() == 1 ? false : true);
    }
	
    public void onRowUnselect(UnselectEvent event) {
    	setModelPricingDisabled(getSelectedResultVOs().size() == 1 ? false : true);
    }    
		
	public int getSelectionIndex() {
		return selectionIndex;
	}

	public void setSelectionIndex(int selectionIndex) {
		this.selectionIndex = selectionIndex;
	}
	
	private void adjustDataTableAfterSearch(List<ModelSearchResultVO> modelSearchResults){
		adjustDataTableHeight(modelSearchResults);
	}	
		
	@SuppressWarnings("unchecked")
	private Map<String, Object> getCurrentPageRestoreStateValuesMap() {
		List<Integer> indices = new ArrayList<Integer>();
		Map<String, Object> restoreStateValues = new HashMap<String, Object>();
		restoreStateValues.put(RESTORE_PARAM_CRITERIA, getCriteria()); 
		
		for(ModelSearchResultVO modelSearchResultVO : getSelectedResultVOs()){
			indices.add((modelSearchResults).indexOf(modelSearchResultVO));
		}
		
		restoreStateValues.put(RESTORE_PARAM_SELECTED_INDICES, indices);
				
		return restoreStateValues;
		
	}
	
	public String cancel() {	
		 return super.cancelPage(); 
	}
		
	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
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

	public LogBookTypeEnum getLogBookType() {
		return logBookType;
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

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getMake() {
		return make;
	}

	public void setMake(String make) {
		this.make = make;
	}

	public List<String> getMakes() {
		return makes;
	}

	public void setMakes(List<String> makes) {
		this.makes = makes;
	}

	public List<String> getModels() {
		return models;
	}

	public void setModels(List<String> models) {
		this.models = models;
	}

	public List<ModelType> getModelTypes() {
		return modelTypes;
	}

	public void setModelTypes(List<ModelType> modelTypes) {
		this.modelTypes = modelTypes;
	}

	public LazyDataModel<ModelSearchResultVO> getLazyModelSearchResults() {
		return lazyModelSearchResults;
	}

	public void setLazyModelSearchResults(LazyDataModel<ModelSearchResultVO> lazyModelSearchResults) {
		this.lazyModelSearchResults = lazyModelSearchResults;
	}

	public List<ModelSearchResultVO> getSelectedResultVOs() {
		return selectedResultVOs;
	}

	public void setSelectedResultVOs(List<ModelSearchResultVO> selectedResultVOs) {
		this.selectedResultVOs = selectedResultVOs;
	}

	public boolean isModelPricingDisabled() {
		return modelPricingDisabled;
	}

	public void setModelPricingDisabled(boolean modelPricingDisabled) {
		this.modelPricingDisabled = modelPricingDisabled;
	}

}
