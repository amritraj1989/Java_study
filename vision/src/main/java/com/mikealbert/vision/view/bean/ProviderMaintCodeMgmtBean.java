package com.mikealbert.vision.view.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.el.ValueExpression;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.mikealbert.data.DataConstants;
import com.mikealbert.data.entity.MaintenanceCategory;
import com.mikealbert.data.entity.MaintenanceCode;
import com.mikealbert.data.entity.ServiceProviderMaintenanceCode;
import com.mikealbert.data.vo.ProviderMaintCodeSearchCriteriaVO;
import com.mikealbert.data.vo.ProviderMaintCodeSearchResultLineVO;
import com.mikealbert.data.vo.ProviderMaintCodeSearchResultsVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;
import com.mikealbert.service.LookupCacheService;
import com.mikealbert.service.MaintCodeSearchService;
import com.mikealbert.service.MaintenanceCodeService;
import com.mikealbert.service.ServiceProviderService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.view.ViewConstants;

@Component
@Scope("view")
public class ProviderMaintCodeMgmtBean extends StatefulBaseBean{
	private static final long serialVersionUID = 5957629830511242337L;
	
	@Resource ServiceProviderService serviceProviderService;
	@Resource MaintenanceCodeService maintenanceCodeService;
	@Resource MaintCodeSearchService searchService;
	@Resource LookupCacheService lookupService;

	private static final String  DATA_TABLE_UI_ID ="maintenanceCodeMgmtDataTable";
	// for performance reasons this is capped at 150
	private int resultPerPage = 150;//ViewConstants.RECORDS_PER_PAGE;
	private int totalResultSet	= 0;
	private LazyDataModel<ProviderMaintCodeSearchResultLineVO> serviceProviderMaintCodeLazyList  = null;
	private List<ProviderMaintCodeSearchResultLineVO> serviceProviderMaintCodeList;
	private List<ProviderMaintCodeSearchResultLineVO> origProviderMaintCodeList;

	private ProviderMaintCodeSearchCriteriaVO searchCriteria;
	private List<MaintenanceCategory> maintCategories;
	private List<String> approvalStatuses;

	private String dataTableMessage;
	private boolean isSearchRequired = false; 
	
	final static String PROVIDER_MAINT_CODE_TABLE_UI_ID = "maintenanceCodeMgmtDataTable";
	final static String MAINT_CODE_IN_TABLE_UI_ID_TEMPLATE = "maintenanceCodeMgmtDataTable:%s:maintCode";
	final static String PROVIDER_DESC_IN_TABLE_UI_ID_TEMPLATE = "maintenanceCodeMgmtDataTable:%s:providerMaintDesc";
	final static String PROVIDER_CODE_IN_TABLE_UI_ID_TEMPLATE = "maintenanceCodeMgmtDataTable:%s:providerMaintCode";
	
	
	private int selectionIndex = -1;
	private boolean returnedFromNextPage;
	
	private ProviderMaintCodeSearchResultLineVO selectedVendorCode;

	private DataTable dt;
	private String providerFilter;
	private String maintCategoryFilter;
	private String maintCodeDescFilter;
	private String approvedStatus;
	
	private List<Integer> changeLineIndexes;
	

	/**
	 * Initializes the bean
	 */
    @PostConstruct
    public void init(){
    	super.openPage();
    	initializeDataTable(550, 830, new int[] { 4, 11, 19, 11, 8, 4, 7, 6, 8, 10, 9, 4}).setHeight(0);
    	searchCriteria = new ProviderMaintCodeSearchCriteriaVO();
    	searchCriteria.setServiceProvider((String)thisPage.getRestoreStateValues().get("PROVIDER_FILTER"));
    	searchCriteria.setMaintenanceCategory((String)thisPage.getRestoreStateValues().get("MAINT_CATEGORY_FILTER"));
    	
    	if(approvedStatus == null)
    		searchCriteria.setApprovedStatus(ProviderMaintCodeSearchCriteriaVO.NEEDS_APPROVE_STATUS);
    	else
    		searchCriteria.setApprovedStatus((String)thisPage.getRestoreStateValues().get("MAINT_APPROVED_STATUS"));
    	
    	maintCategories = lookupService.getMaintenanceCategories();
    	approvalStatuses = ProviderMaintCodeSearchCriteriaVO.APPROVAL_STATUSES;
    	
    	// this is more of a "work queue" that a search, so it should initially be populated
    	isSearchRequired = true; 
    	
        setServiceProviderMaintCodeLazyList(new LazyDataModel<ProviderMaintCodeSearchResultLineVO>() {
			private static final long serialVersionUID = 1L;
				
			@Override
			public List<ProviderMaintCodeSearchResultLineVO> load(int first, int pageSize, String inputSortField, SortOrder inputSortOrder, Map<String,Object> filters) {
								int pageIdx = (first == 0) ? 0 : (first / pageSize);
				PageRequest page = new PageRequest(pageIdx,pageSize);				
				Sort sort = null;
				String querySortField = getSortField(inputSortField);				
				if (inputSortOrder.name().equalsIgnoreCase(SortOrder.ASCENDING.toString())) {
					 sort = new Sort(Sort.Direction.ASC, querySortField);
				}else{
					 sort = new Sort(Sort.Direction.DESC, querySortField);
				}
				
				serviceProviderMaintCodeList  = getLazySearchResultList(page ,sort);
				origProviderMaintCodeList = copyProviderMaintCodeResultVOList(serviceProviderMaintCodeList);
				return serviceProviderMaintCodeList;
			}

			@Override
			public ProviderMaintCodeSearchResultLineVO getRowData(String rowKey) {
				if(!MALUtilities.isEmpty(serviceProviderMaintCodeList)){
					for (ProviderMaintCodeSearchResultLineVO serviceProviderMaintCode : serviceProviderMaintCodeList) {
						if (String.valueOf(serviceProviderMaintCode.getProvideMaintCodeId()).equals(rowKey))
							return serviceProviderMaintCode;
					}
				}
				return null;
			}

			@Override
			public Object getRowKey(ProviderMaintCodeSearchResultLineVO serviceProviderMaintCode) {
				return serviceProviderMaintCode.getProvideMaintCodeId();
			}
		});
        serviceProviderMaintCodeLazyList.setPageSize(ViewConstants.RECORDS_PER_PAGE);
    }
    
    private List<ProviderMaintCodeSearchResultLineVO> getLazySearchResultList(PageRequest page, Sort sort){
    	serviceProviderMaintCodeList =  new ArrayList<ProviderMaintCodeSearchResultLineVO>();
    	
    	if(isSearchRequired){	
    		int rowCount = 0;
    		
    		
    		ProviderMaintCodeSearchResultsVO results = searchService.searchProviderMaintCodes(searchCriteria, page, sort);
    		serviceProviderMaintCodeList = results.getResultsLines();
    		rowCount = results.getResultCount();	
    		
    		selectionIndex =  page.getPageSize() *  page.getPageNumber();
    		serviceProviderMaintCodeLazyList.setRowCount(rowCount);
    		serviceProviderMaintCodeLazyList.setWrappedData(serviceProviderMaintCodeList);
    		adjustDataTableAfterSearch(serviceProviderMaintCodeList);
    		dt = (DataTable) getComponent(DATA_TABLE_UI_ID); //Due to commandLink datatable component needs to be set before clicking on link
    		
    	}
    	
    	return serviceProviderMaintCodeList;

	}

    private List<ProviderMaintCodeSearchResultLineVO> copyProviderMaintCodeResultVOList(List<ProviderMaintCodeSearchResultLineVO> sourceList){
    	List<ProviderMaintCodeSearchResultLineVO> retList = new ArrayList<ProviderMaintCodeSearchResultLineVO>();
    	
    	for(ProviderMaintCodeSearchResultLineVO sourceVO : sourceList){
    		ProviderMaintCodeSearchResultLineVO copyVO = new ProviderMaintCodeSearchResultLineVO();
    		BeanUtils.copyProperties(sourceVO, copyVO);
    		retList.add(copyVO);
    	}
    	
    	
    	return retList;
    }
    
    private List<ServiceProviderMaintenanceCode> copyServiceProviderMaintCodeFromVOList(List<ProviderMaintCodeSearchResultLineVO> sourceList){
    	List<ServiceProviderMaintenanceCode> retList = new ArrayList<ServiceProviderMaintenanceCode>();
    	
    	for(ProviderMaintCodeSearchResultLineVO sourceVO : sourceList){
    		ServiceProviderMaintenanceCode copyProviderCode = new ServiceProviderMaintenanceCode();
    		MaintenanceCode copyMaintCode = new MaintenanceCode();
    		
    		copyProviderCode.setSmlId(sourceVO.getProvideMaintCodeId());
    		copyProviderCode.setCode(sourceVO.getProvideMaintCode());
    		copyProviderCode.setDescription(sourceVO.getProvideMaintCodeDesc().trim());
    		if(sourceVO.isApproved()){
    			copyProviderCode.setApprovedBy(this.getLoggedInUser().getEmployeeNo());
        		copyProviderCode.setApprovedDate(new Date());	
    		}else{
    			copyProviderCode.setApprovedBy(null);
        		copyProviderCode.setApprovedDate(null);
    		}
    		
    		// if the user emptied out the maintenance code field then they don't want one set right now.
    		if(!MALUtilities.isEmpty(sourceVO.getMafsMaintCode())){
    			copyMaintCode = new MaintenanceCode();
        		copyMaintCode.setMcoId(sourceVO.getMafsMaintCodeId());
        		copyMaintCode.setCode(sourceVO.getMafsMaintCode());
        		copyMaintCode.setDescription(sourceVO.getMafsMaintCodeDesc());
        		copyMaintCode.setMaintCatCode(sourceVO.getServiceProvideMaintCategory());
        		copyProviderCode.setMaintenanceCode(copyMaintCode);
    		}else{
    			copyProviderCode.setMaintenanceCode(null);
    		}
    		
    		retList.add(copyProviderCode);
    	}
    	
    	
    	return retList;
    }
    

    private String getSortField(String sortField) {

		if (ProviderMaintCodeSearchResultLineVO.FILTER_MAFS_CODE.equalsIgnoreCase(sortField)) {
			return DataConstants.PROVIDER_MAINT_CODE_SORT_MAFS_CODE;
		} else if (ProviderMaintCodeSearchResultLineVO.FILTER_MAFS_DESC.equalsIgnoreCase(sortField)) {
			return DataConstants.PROVIDER_MAINT_CODE_SORT_MAFS_DESC;
		} else if (ProviderMaintCodeSearchResultLineVO.FILTER_SERVICE_PROVIDER.equalsIgnoreCase(sortField)) {
			return DataConstants.PROVIDER_MAINT_CODE_SORT_SERVICE_PROVIDER;
		} else if (ProviderMaintCodeSearchResultLineVO.FILTER_SERVICE_PROVIDER_CODE.equalsIgnoreCase(sortField)) {
			return DataConstants.PROVIDER_MAINT_CODE_SORT_SERVICE_PROVIDER_CODE;
		} else if (ProviderMaintCodeSearchResultLineVO.FILTER_SERVICE_PROVIDER_DESC.equalsIgnoreCase(sortField)) {
			return DataConstants.PROVIDER_MAINT_CODE_SORT_SERVICE_PROVIDER_DESC;
		} else{
			return DataConstants.PROVIDER_MAINT_CODE_SORT_SERVICE_PROVIDER;
		}
	}

	@Override
	protected void loadNewPage() {
		super.thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_PROVIDER_MAINT_CODE);
		super.thisPage.setPageUrl(ViewConstants.PROVIDER_MAINT_CODE);	
	}


	@Override
	protected void restoreOldPage() {
		Map<String, Object> map = super.thisPage.getRestoreStateValues();
		// fix for FM-1468; added "empty/null" checking due to NullPointerExceptions in the log.
		if(MALUtilities.isEmpty(thisPage.getRestoreStateValues().get("NEXT_PAGE"))){
			this.setReturnedFromNextPage(false);
		}else{
			this.setReturnedFromNextPage((Boolean) thisPage.getRestoreStateValues().get("NEXT_PAGE"));
		}
		this.providerFilter = (String)thisPage.getRestoreStateValues().get("PROVIDER_FILTER");
		this.maintCategoryFilter = (String)thisPage.getRestoreStateValues().get("MAINT_CATEGORY_FILTER");
		this.maintCodeDescFilter = (String)thisPage.getRestoreStateValues().get("MAINT_CODE_DESC_FILTER");
		this.approvedStatus = (String)thisPage.getRestoreStateValues().get("MAINT_APPROVED_STATUS");
			
		dt = (DataTable) getComponent(DATA_TABLE_UI_ID);
		Integer pageStartIndex = (Integer) thisPage.getRestoreStateValues().get("SELECTD_PAGE_START_INDEX");
		//this case may happen if user  directly landed to driver add/edit page
		if(pageStartIndex != null && dt != null){
			dt.setFirst(pageStartIndex);
			dt.setSortOrder((String) thisPage.getRestoreStateValues().get("DEFAULT_SORT_ORDER"));
			dt.setValueExpression("sortBy", (ValueExpression) thisPage.getRestoreStateValues().get("DEFAULT_SORT_VE"));
		}
	}
		
	public String cancel(){
		return cancelPage(); 
	}
	
	private Map<String, Object> getCurrentPageRestoreStateValuesMap() {
		
		Map<String, Object> restoreStateValues = new HashMap<String, Object>();
		restoreStateValues.put("SELECTD_INDEX", this.selectionIndex);
		restoreStateValues.put("NEXT_PAGE", true);
		if (dt != null) {
			ValueExpression veSort = dt.getValueExpression("sortBy");
			ValueExpression veFilter = dt.getValueExpression("filterBy");
			restoreStateValues.put("DEFAULT_SORT_VE", veSort);
			restoreStateValues.put("DEFAULT_FILTER_VE", veFilter);
			restoreStateValues.put("SELECTD_PAGE_START_INDEX", dt.getFirst());
			restoreStateValues.put("DEFAULT_SORT_ORDER", dt.getSortOrder());
		}	
			restoreStateValues.put("PROVIDER_FILTER", this.getSearchCriteria().getServiceProvider());
			restoreStateValues.put("MAINT_CATEGORY_FILTER", this.getSearchCriteria().getMaintenanceCategory());
			restoreStateValues.put("MAINT_APPROVED_STATUS", this.getSearchCriteria().getApprovedStatus());
			restoreStateValues.put("MAINT_CODE_DESC_FILTER", this.getSearchCriteria().getApprovedStatus());

		return restoreStateValues;
		
	}
	
	public void performFilter() {
		this.isSearchRequired = true;
		this.selectedVendorCode = null;
		this.returnedFromNextPage = false;
		DataTable pfDataTable = ((DataTable) getComponent(DATA_TABLE_UI_ID));
		if(pfDataTable != null)
			pfDataTable.setFirst(0);

	}
	
	public void refreshMaintCodeCache() {
		try{
			lookupService.refreshMaintCodeCache();
			super.addSuccessMessage("process.success"," Refreshed Maint Code(s) Cache");
		}catch(MalException ex){
			handleException("generic.error",new String[]{ex.getMessage()}, ex, null);
			RequestContext context = RequestContext.getCurrentInstance();
			context.addCallbackParam("failure", true);
		}

	}
	
	public void save() {
		boolean allSaved = true;
		
		if(!MALUtilities.isEmpty(changeLineIndexes)){
			for(Integer i : changeLineIndexes){
				String htmlId = String.format(MAINT_CODE_IN_TABLE_UI_ID_TEMPLATE, String.valueOf(i));
	    		String javaScriptFunction = "unMarkRedForError('"+htmlId+"')";
	    		RequestContext requestContext = RequestContext.getCurrentInstance();  
	    		requestContext.execute(javaScriptFunction);

	    		htmlId = String.format(PROVIDER_DESC_IN_TABLE_UI_ID_TEMPLATE, String.valueOf(i));
	    		javaScriptFunction = "unMarkRedForError('"+htmlId+"')";
	    		requestContext = RequestContext.getCurrentInstance();  
	    		requestContext.execute(javaScriptFunction);				
			}
		}	
		
		List<ProviderMaintCodeSearchResultLineVO> changeLines = new ArrayList<ProviderMaintCodeSearchResultLineVO>();
		changeLineIndexes = new ArrayList<Integer>();
		// determine a change to the rows
		for(int i=0; i <= (origProviderMaintCodeList.size()-1); i++){
			if(!origProviderMaintCodeList.get(i).equals(serviceProviderMaintCodeList.get(i))){
				changeLines.add(serviceProviderMaintCodeList.get(i));
				changeLineIndexes.add(new Integer(i));
			}
		}
	
		if(isValidate(changeLines,changeLineIndexes)){
			
			List<ServiceProviderMaintenanceCode> changedCodes = copyServiceProviderMaintCodeFromVOList(changeLines);
			// call a new "update" method to update the provided
			for(ServiceProviderMaintenanceCode code : changedCodes){
				try{
					maintenanceCodeService.updateServiceProviderMaintCode(code);
				} catch (MalBusinessException e) {
					addSimplErrorMessage(e.getMessage());
					RequestContext context = RequestContext.getCurrentInstance();
					context.addCallbackParam("failure", true);
					allSaved = false;
					break;
				}catch(MalException ex){
					handleException("generic.error",new String[]{ex.getMessage()}, ex, null);
					RequestContext context = RequestContext.getCurrentInstance();
					context.addCallbackParam("failure", true);
					allSaved = false;
					break;
				}
			}
			
			if(allSaved){
				RequestContext.getCurrentInstance().update(PROVIDER_MAINT_CODE_TABLE_UI_ID);
				super.addSuccessMessage("process.success","Save Provider Maint Code(s) ");
			}
		}
		else{
			RequestContext context = RequestContext.getCurrentInstance();
			context.addCallbackParam("failure", true);
		}
	}
	
	public boolean isValidate(List<ProviderMaintCodeSearchResultLineVO> changedLines, List<Integer> changeLineIndexes){
		boolean isValid = true;
		boolean isRowInvalid = false;
		String 	elementForFocus = null;
		int cnt = 0;

		for (ProviderMaintCodeSearchResultLineVO changedLine : changedLines) {
			isRowInvalid = false;
			
			// validate each row; put error on screen
			// -- if my MAFS code is null/empty and I am trying to approve the code.
			if (MALUtilities.isEmpty(changedLine.getMafsMaintCode()) && changedLine.isApproved()) {
				String htmlId = String.format(MAINT_CODE_IN_TABLE_UI_ID_TEMPLATE, String.valueOf(changeLineIndexes.get(cnt)));
        		String javaScriptFunction = "markRedForError('"+htmlId+"')";
        		RequestContext requestContext = RequestContext.getCurrentInstance();  
        		requestContext.execute(javaScriptFunction);
        		
        		if(MALUtilities.isEmptyString(elementForFocus)){
        			elementForFocus = htmlId;
        		}
        		addErrorMessage("Validation.Error.Occurred","Validation Error(s) Occurred : ");
				addErrorMessage("required.field", "Provider Maint Code " +changedLine.getProvideMaintCode()+ " : MAFS maintenance code " + changedLine.getMafsMaintCode());
				isValid = false;
				isRowInvalid = true;
			}
			// -- if maint code description is null/empty
			if(MALUtilities.isEmpty(changedLine.getProvideMaintCodeDesc())){
				String htmlId = String.format(PROVIDER_DESC_IN_TABLE_UI_ID_TEMPLATE, String.valueOf(changeLineIndexes.get(cnt)));
        		String javaScriptFunction = "markRedForError('"+htmlId+"')";
        		RequestContext requestContext = RequestContext.getCurrentInstance();  
        		requestContext.execute(javaScriptFunction);
				
        		if(MALUtilities.isEmptyString(elementForFocus)){
        			elementForFocus = htmlId;
        		}
        		addErrorMessage("Validation.Error.Occurred","Validation Error(s) Occurred : ");
				addErrorMessage("required.field", "Provider Maint Code " +changedLine.getProvideMaintCode() + " : Provider maintenance code description " + changedLine.getProvideMaintCodeDesc());
				isValid = false;
				isRowInvalid = true;
			}
			// -- if my service provider maint code is null/empty and I am trying to approve the code.
			if (MALUtilities.isEmpty(changedLine.getProvideMaintCode()) && changedLine.isApproved()) {
				String htmlId = String.format(PROVIDER_CODE_IN_TABLE_UI_ID_TEMPLATE, String.valueOf(changeLineIndexes.get(cnt)));
        		String javaScriptFunction = "markRedForError('"+htmlId+"')";
        		RequestContext requestContext = RequestContext.getCurrentInstance();  
        		requestContext.execute(javaScriptFunction);
        		
        		if(MALUtilities.isEmptyString(elementForFocus)){
        			elementForFocus = htmlId;
        		}
        		addErrorMessage("Validation.Error.Occurred","Validation Error(s) Occurred : ");
				addErrorMessage("required.field", "Provider Maint Code " +changedLine.getProvideMaintCode());
				isValid = false;
				isRowInvalid = true;
			}
			// if the row is not otherwise invalid (the DB call is expensive and if the fields are empty we have bigger issues
			// validate "full maint code" / maint code "mis-match"
			// lookup maint code from what was updated in the UI (if partial or mis-match)
			if(!isRowInvalid){
				// if the mafs maint code is empty (if the user cleared it out) do not decode
				if(!MALUtilities.isEmpty(changedLine.getMafsMaintCode())){
					ProviderMaintCodeSearchResultLineVO decodedCode = doDecodeMaintCodeByNameOrCode(changedLine,changedLine.getMafsMaintCode(),changeLineIndexes.get(cnt));
					if(!MALUtilities.isEmpty(decodedCode.getMafsMaintCodeId())){
						changedLine.setMafsMaintCodeId(decodedCode.getMafsMaintCodeId());
						changedLine.setMafsMaintCode(decodedCode.getMafsMaintCode());
						changedLine.setMafsMaintCodeDesc(decodedCode.getMafsMaintCodeDesc());
					}else{
						isValid = false;
						isRowInvalid = true;
						
		        		if(MALUtilities.isEmptyString(elementForFocus)){
		        			String htmlId = String.format(MAINT_CODE_IN_TABLE_UI_ID_TEMPLATE, String.valueOf(changeLineIndexes.get(cnt)));
		        			elementForFocus = htmlId;
		        		}
					}
				}
			}
						
			cnt++;
		 
		}

    	if(!isValid && elementForFocus != null){
    		RequestContext requestContext = RequestContext.getCurrentInstance();  
    		String setFocusFunction = "setElementFocus('"+elementForFocus+"')";
    		requestContext.execute(setFocusFunction);
    	}
		
		return isValid;
	}
	
	
	
		
	public void decodeMaintCodeByNameOrCode(String nameOrCode, String provideMaintCodeId) {
		//TODO: is was never prioritized to do inline decode.. but if it is then this is a starting point
//		ProviderMaintCodeSearchResultLineVO decodingCode = null;
//		Long providerCodeId = Long.parseLong(provideMaintCodeId);
//		for(ProviderMaintCodeSearchResultLineVO serviceProviderCode : serviceProviderMaintCodeList){
//			if(providerCodeId.equals(serviceProviderCode.getProvideMaintCodeId())){
//				decodingCode = serviceProviderCode;
//				break;
//			}
//		}
	}
	
	private ProviderMaintCodeSearchResultLineVO doDecodeMaintCodeByNameOrCode(ProviderMaintCodeSearchResultLineVO changedLine,String nameOrCode, int index){
		// if there is more than one then set an error
		// if there is none then set an error
		
		//lookup the maintenance code by nameOrCode
		PageRequest page1 = new PageRequest(0,2);
		List<MaintenanceCode> maintCodes = maintenanceCodeService.findMaintenanceCodesByNameOrCode(nameOrCode,page1);
		// if there is only one then set it in the list
		ProviderMaintCodeSearchResultLineVO decodingCode = new ProviderMaintCodeSearchResultLineVO();
		if(maintCodes.size() == 1){
			
			if(!MALUtilities.isEmpty(decodingCode)){
				decodingCode.setMafsMaintCodeId(maintCodes.get(0).getMcoId());
				decodingCode.setMafsMaintCode(maintCodes.get(0).getCode());
				decodingCode.setMafsMaintCodeDesc(maintCodes.get(0).getDescription());
			}

		}else if (maintCodes.size() == 0) {
			if(!MALUtilities.isEmpty(decodingCode)){
				String htmlId = String.format(MAINT_CODE_IN_TABLE_UI_ID_TEMPLATE, String.valueOf(index));
        		String javaScriptFunction = "markRedForError('"+htmlId+"')";
        		RequestContext requestContext = RequestContext.getCurrentInstance();  
        		requestContext.execute(javaScriptFunction);
        		addErrorMessage("Validation.Error.Occurred","Validation Error(s) Occurred : ");
				addErrorMessage("decode.noMatchFound.msg", "Provider Maint Code " +changedLine.getProvideMaintCode() + " : MAFS Maintenance Code " + nameOrCode);
			}
		}else {
			if(!MALUtilities.isEmpty(decodingCode)){
				String htmlId = String.format(MAINT_CODE_IN_TABLE_UI_ID_TEMPLATE, String.valueOf(index));
        		String javaScriptFunction = "markRedForError('"+htmlId+"')";
        		RequestContext requestContext = RequestContext.getCurrentInstance();  
        		requestContext.execute(javaScriptFunction);
				
				addErrorMessage("decode.multipleMatchesFound.msg", "Provider Maint Code " +changedLine.getProvideMaintCode() + " : MAFS Maintenance Code " + nameOrCode);
			}
		}	
		
		return decodingCode;
	}
	
	public void addServiceProviderMaintCode() {

	    saveRestoreStateValues(getCurrentPageRestoreStateValuesMap());

		Map<String, Object> nextPageValues = new HashMap<String, Object>();

		nextPageValues.put(ViewConstants.VIEW_PARAM_MODE, ViewConstants.VIEW_MODE_ADD);
		nextPageValues.put(ViewConstants.VIEW_PARAM_CALLERS_NAME, ViewConstants.PROVIDER_MAINT_CODE);			
		
		super.saveNextPageInitStateValues(nextPageValues);

		super.forwardToURL(ViewConstants.ADD_PROVIDER_MAINT_CODE);

	}
	
	public void maintainDiscounts() {
	    saveRestoreStateValues(getCurrentPageRestoreStateValuesMap());
		Map<String, Object> nextPageValues = new HashMap<String, Object>();

		nextPageValues.put(ViewConstants.VIEW_PARAM_CALLERS_NAME, ViewConstants.PROVIDER_MAINT_CODE);			
	
		super.saveNextPageInitStateValues(nextPageValues);
		super.forwardToURL(ViewConstants.MAINTAIN_SUPPLIER_DISCOUNTS);
	}
	

	@SuppressWarnings("unchecked")
	public void setServiceProviderMaintenanceCode(ProviderMaintCodeSearchResultLineVO selectedVendorCode) {
		if(!MALUtilities.isEmpty(selectedVendorCode)){
			this.selectedVendorCode = selectedVendorCode;
		}
	}
	

	
	private void adjustDataTableAfterSearch(List<ProviderMaintCodeSearchResultLineVO> serviceProviderMaintCodeList){
		adjustDataTableHeight(serviceProviderMaintCodeList);
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

	
	public String getDataTableMessage() {
		return dataTableMessage;
	}

	public void setDataTableMessage(String dataTableMessage) {
		this.dataTableMessage = dataTableMessage;
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
	
	public LazyDataModel<ProviderMaintCodeSearchResultLineVO> getServiceProviderMaintCodeLazyList() {
		return serviceProviderMaintCodeLazyList;
	}

	public void setServiceProviderMaintCodeLazyList(
			LazyDataModel<ProviderMaintCodeSearchResultLineVO> serviceProviderMaintCodeLazyList) {
		this.serviceProviderMaintCodeLazyList = serviceProviderMaintCodeLazyList;
	}
	
	public ProviderMaintCodeSearchResultLineVO getSelectedVendorCode() {
		return selectedVendorCode;
	}
	
	public void setSelectedVendorCode(ProviderMaintCodeSearchResultLineVO selectedVendorCode) {
		this.selectedVendorCode = selectedVendorCode;
	}

	public boolean isReturnedFromNextPage() {
		return returnedFromNextPage;
	}

	public void setReturnedFromNextPage(boolean returnedFromNextPage) {
		this.returnedFromNextPage = returnedFromNextPage;
	}

	public int getTotalResultSet() {
		List list = serviceProviderMaintCodeLazyList.getWrappedData()!= null ? (List)serviceProviderMaintCodeLazyList.getWrappedData(): null;
		if (list != null && list.size() < resultPerPage){
			totalResultSet = serviceProviderMaintCodeLazyList.getRowCount();
		}else{
			int currPage = dt.getPage();
			totalResultSet	=( currPage+1)*resultPerPage;
		}
		
		return totalResultSet;
	}

	public ProviderMaintCodeSearchCriteriaVO getSearchCriteria() {
		return searchCriteria;
	}

	public void setSearchCriteria(ProviderMaintCodeSearchCriteriaVO searchCriteria) {
		this.searchCriteria = searchCriteria;
	}
	
	public List<MaintenanceCategory> getMaintCategories() {
		return maintCategories;
	}

	public void setMaintCategories(List<MaintenanceCategory> maintCategories) {
		this.maintCategories = maintCategories;
	}
	public List<String> getApprovalStatuses() {
		return approvalStatuses;
	}

	public void setApprovalStatuses(List<String> approvalStatuses) {
		this.approvalStatuses = approvalStatuses;
	}

	public String getMaintCategoryFilter() {
		return maintCategoryFilter;
	}

	public void setMaintCategoryFilter(String maintCategoryFilter) {
		this.maintCategoryFilter = maintCategoryFilter;
	}

	public String getProviderFilter() {
		return providerFilter;
	}

	public void setProviderFilter(String providerFilter) {
		this.providerFilter = providerFilter;
	}

	public String getMaintCodeDescFilter() {
		return maintCodeDescFilter;
	}

	public void setMaintCodeDescFilter(String maintCodeDescFilter) {
		this.maintCodeDescFilter = maintCodeDescFilter;
	}

	
}
