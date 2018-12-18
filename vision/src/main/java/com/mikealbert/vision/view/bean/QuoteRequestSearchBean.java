package com.mikealbert.vision.view.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.el.ValueExpression;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.data.PageEvent;
import org.primefaces.event.data.SortEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.mikealbert.data.entity.QuoteRequestStatus;
import com.mikealbert.data.entity.QuoteRequestType;
import com.mikealbert.data.enumeration.QuoteRequestStatusEnum;
import com.mikealbert.data.enumeration.QuoteRequestTypeEnum;
import com.mikealbert.data.vo.QuoteRequestSearchCriteriaVO;
import com.mikealbert.data.vo.QuoteRequestSearchResultVO;
import com.mikealbert.service.QuoteRequestSearchService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.view.ViewConstants;

@Component
@Scope("view")
public class QuoteRequestSearchBean extends StatefulBaseBean {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 464592921500001359L;
	
	static final String RESTORE_PARAM_CRITERIA = "QUOTE_REQUEST_SEARCH_CRITERIA";	
	static final String RESTORE_PARAM_SEARCH_REQUIRED = "SEARCH_REQUIRED";
	static final String DATA_TABLE_UI_ID = "DT_UI_ID";	
	
	private LazyDataModel<QuoteRequestSearchResultVO> lazyQuoteRequestSearchResults  = null;
	private QuoteRequestSearchCriteriaVO criteria = new QuoteRequestSearchCriteriaVO();
	private QuoteRequestSearchResultVO selectedQuoteRequestQueueVO;
	List<QuoteRequestType> dbRequestTypeList = null;
	
	private List<QuoteRequestStatus> quoteRequestStatusList = null;
	private List<QuoteRequestType> quoteRequestTypeList = new ArrayList<QuoteRequestType>();
	private Boolean isSearchRequired = false;
	
	private String client = null;
	private String requestor = null;
	private String requestorEmpNo = null;
	private String assignedTo = null;
	private String assignedToEmpNo = null;
	
	private int resultPerPage = ViewConstants.RECORDS_PER_PAGE_MEDIUM;
	private String dataTableMessage;	
	private Long scrollPosition = 0l;
	
	private String completedStatus = QuoteRequestStatusEnum.COMPLETED.getDescription();
	private String closedStatus = QuoteRequestStatusEnum.CLOSED.getDescription();
	
	private static final String  DT_UI_ID ="quoteRequestDataTable";
	
	@Resource private QuoteRequestSearchService quoteRequestSearchService;
	
	@PostConstruct
	public void init() {
		initializeDataTable(590, 850, new int[] {2, 3, 30, 6, 4, 4, 11, 11, 7}).setHeight(0);		
		openPage();				
		
		lazyQuoteRequestSearchResults = new LazyDataModel<QuoteRequestSearchResultVO>() {
			private static final long serialVersionUID = 1L;
				
			@Override
			public List<QuoteRequestSearchResultVO> load(int first, int pageSize, String sortBy, SortOrder sortOrder, Map<String, Object> arg4) {
				int pageIdx = (first == 0) ? 0 : (first / pageSize);
				PageRequest page = new PageRequest(pageIdx, pageSize);				
				Sort sort = null;
				
				if(MALUtilities.isNotEmptyString(sortBy)){
					if (sortOrder.name().equalsIgnoreCase(SortOrder.DESCENDING.toString())) {
						 sort = new Sort(Sort.Direction.DESC, quoteRequestSearchService.resolveSortByName(sortBy));
					}else{
						 sort = new Sort(Sort.Direction.ASC, quoteRequestSearchService.resolveSortByName(sortBy));
					}
				}
				
				List<QuoteRequestSearchResultVO> quoteRequestSearchResults  = null;
				quoteRequestSearchResults  = getLazySearchResultList(page ,sort);
				return quoteRequestSearchResults;
			}

			@Override
			public QuoteRequestSearchResultVO getRowData(String rowKey) {
				for (QuoteRequestSearchResultVO resultVO : lazyQuoteRequestSearchResults) {
					if (String.valueOf(resultVO.getQrqId()).equals(rowKey))
						return resultVO;
				}
				return null;
			}

			@Override
			public Object getRowKey(QuoteRequestSearchResultVO quoteRequestQueueVO) {
				return quoteRequestQueueVO.getQrqId();
			}
		};
		loadData();
		lazyQuoteRequestSearchResults.setPageSize(getResultPerPage());
	}
	
	private void loadData() {
		 setQuoteRequestStatusList(quoteRequestSearchService.getAllRequestStatus());
		 this.dbRequestTypeList = quoteRequestSearchService.getAllRequestType();
		 setQuoteRequestTypeList(getDbRequestTypeList());
	}
	
	public void performSearch() {
		this.isSearchRequired = true;
		this.selectedQuoteRequestQueueVO = null;		
		DataTable pfDataTable = ((DataTable) getComponent(DATA_TABLE_UI_ID));
		if(pfDataTable != null)
			pfDataTable.setFirst(0);
		setClientCodeName();
	}
	
	private List<QuoteRequestSearchResultVO> getLazySearchResultList(PageRequest page, Sort sort){
		List<QuoteRequestSearchResultVO> quoteRequestSearchResult =  new ArrayList<QuoteRequestSearchResultVO>();
		
		if(isSearchRequired) {
			try {				
				logger.info(super.getLoggedInUser().getEmployeeNo() + "-- Quote request search start with criteria " + criteria);
				if(criteria.getRequestTypes() != null && criteria.getRequestTypes().size() > 0 && criteria.getRequestTypes().contains(QuoteRequestTypeEnum.IMM_NEED_ALL.getCode())){
					criteria.getRequestTypes().clear();
					for(QuoteRequestType quoteRequestType : getDbRequestTypeList()){
						criteria.getRequestTypes().add(quoteRequestType.getCode());
					}
				}
				quoteRequestSearchResult = quoteRequestSearchService.findQuoteRequests(criteria , page, sort);
				int count  = quoteRequestSearchService.findQuoteRequestsCount(criteria);
				
				setSubStatus(quoteRequestSearchResult);
				
				lazyQuoteRequestSearchResults.setRowCount(count);
				lazyQuoteRequestSearchResults.setWrappedData(quoteRequestSearchResult);
				
				adjustDataTableAfterSearch(quoteRequestSearchResult);
					
				logger.info(super.getLoggedInUser().getEmployeeNo() + "-- Quote request search end with criteria " + criteria);
			} catch (Exception ex) {
				super.addErrorMessage("generic.error", ex.getMessage());
			}
		}

		return quoteRequestSearchResult;
	}
	
	private void adjustDataTableAfterSearch(List<QuoteRequestSearchResultVO> quoteRequestSearchResults){
		adjustDataTableHeight(quoteRequestSearchResults);
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
	
	public String cancel() {
		return super.cancelPage();
	}

	@Override
	protected void loadNewPage() {
		thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_QUOTE_REQUEST_SEARCH);
		thisPage.setPageUrl(ViewConstants.QUOTE_REQUEST_SEARCH);
	}

	@Override
	protected void restoreOldPage() {
		DataTable dt = (DataTable) getComponent(DATA_TABLE_UI_ID);					
		this.isSearchRequired =  (Boolean) thisPage.getRestoreStateValues().get(RESTORE_PARAM_SEARCH_REQUIRED);		
		setCriteria((QuoteRequestSearchCriteriaVO) thisPage.getRestoreStateValues().get(RESTORE_PARAM_CRITERIA));
		this.scrollPosition = (Long)thisPage.getRestoreStateValues().get("SCROLL_POSITION");
		if(!MALUtilities.isEmpty(getCriteria().getRequestedClient())){
			setClient(getCriteria().getRequestedClient());
		}
		//TODO I don't believe this check is needed		
		if (!MALUtilities.isEmpty(dt)) {
			dt.setFirst((Integer) thisPage.getRestoreStateValues().get(ViewConstants.DT_SELECTD_PAGE_START_INDEX));
			dt.setSortOrder((String) thisPage.getRestoreStateValues().get(ViewConstants.DT_DEFAULT_SORT_ORDER));
			dt.setValueExpression("sortBy", (ValueExpression) thisPage.getRestoreStateValues().get(ViewConstants.DT_DEFAULT_SORT_VE));			
			setSelectedQuoteRequestQueueVO((QuoteRequestSearchResultVO) thisPage.getRestoreStateValues().get(ViewConstants.DT_SELECTED_ITEM));			
		}		
	}
	
	private void setClientCodeName() {
		getCriteria().setRequestedClientCode(null);
		getCriteria().setRequestedClient(null);
		if (MALUtilities.isNumber(this.client)) {
			getCriteria().setRequestedClientCode(this.client);
		} else {
			getCriteria().setRequestedClient(this.client);
		}
	}
	
	public void navigateToAddQuoteRequest(){
		Map<String, Object> nextPageValues = new HashMap<String, Object>();	
		saveRestoreStateValues(getCurrentPageRestoreStateValuesMap());	 	
		nextPageValues.put(ViewConstants.VIEW_PARAM_MODE, ViewConstants.VIEW_MODE_ADD);		
	    saveNextPageInitStateValues(nextPageValues);   
		forwardToURL(ViewConstants.QUOTE_REQUEST_ADD_EDIT);
	}
	
	private Map<String, Object> getCurrentPageRestoreStateValuesMap() {
		Map<String, Object> restoreStateValues = new HashMap<String, Object>();
		DataTable dt = (DataTable) getComponent(DATA_TABLE_UI_ID);		
		
		restoreStateValues.put(RESTORE_PARAM_CRITERIA, getCriteria());  
		restoreStateValues.put(RESTORE_PARAM_SEARCH_REQUIRED, this.isSearchRequired);
		restoreStateValues.put("SCROLL_POSITION", scrollPosition);

		//TODO I don't believe this check is needed
		if (!MALUtilities.isEmpty(dt)) {
			restoreStateValues.put(ViewConstants.DT_DEFAULT_SORT_VE, dt.getValueExpression("sortBy"));			
			restoreStateValues.put(ViewConstants.DT_DEFAULT_SORT_ORDER, dt.getSortOrder());				
			restoreStateValues.put(ViewConstants.DT_SELECTD_PAGE_START_INDEX, dt.getFirst());
			restoreStateValues.put(ViewConstants.DT_SELECTED_ITEM, getSelectedQuoteRequestQueueVO());			
		}		
				
		return restoreStateValues;		
	}	
	
	public void navigateToEditViewQuoteRequest() {
		Map<String, Object> nextPageValues = new HashMap<String, Object>();
		saveRestoreStateValues(getCurrentPageRestoreStateValuesMap());			
		nextPageValues.put(ViewConstants.VIEW_PARAM_QUOTE_REQUEST_ID, getSelectedQuoteRequestQueueVO().getQrqId());
		nextPageValues.put(ViewConstants.VIEW_PARAM_MODE, ViewConstants.VIEW_MODE_EDIT);		
	    saveNextPageInitStateValues(nextPageValues);
		forwardToURL(ViewConstants.QUOTE_REQUEST_ADD_EDIT);		
	}
	
	public void onPageChange(PageEvent event) {
		setSelectedQuoteRequestQueueVO(null);
    }
	
	public void onSortOperation(SortEvent event) {
		setSelectedQuoteRequestQueueVO(null);
	 }

	private void setSubStatus(List<QuoteRequestSearchResultVO> list) {
		for(QuoteRequestSearchResultVO vo : list) {
			if(vo.getRequestStatus().equalsIgnoreCase("In Progress")) {
				if(vo.isPoPaid() && vo.isQuoteAccepted()) {
					vo.setRequestSubStatus("PO Released");
					vo.setPercentCompleted(75);
				} else if(vo.isVqAccepted()) {
					vo.setRequestSubStatus("Quote Accepted");
					vo.setPercentCompleted(50);
				} else if(vo.isVqPrinted()) {
					vo.setRequestSubStatus("Vehicle Located");
					vo.setPercentCompleted(25);
				} else {
					vo.setRequestSubStatus("In Progress");
					vo.setPercentCompleted(0);
				}
			}
		}
	}
	
	public LazyDataModel<QuoteRequestSearchResultVO> getLazyQuoteRequestSearchResults() {
		return lazyQuoteRequestSearchResults;
	}

	public void setLazyQuoteRequestSearchResults(
			LazyDataModel<QuoteRequestSearchResultVO> lazyQuoteRequestSearchResults) {
		this.lazyQuoteRequestSearchResults = lazyQuoteRequestSearchResults;
	}

	public int getResultPerPage() {
		return resultPerPage;
	}

	public void setResultPerPage(int resultPerPage) {
		this.resultPerPage = resultPerPage;
	}

	public String getDataTableMessage() {
		return dataTableMessage;
	}

	public void setDataTableMessage(String dataTableMessage) {
		this.dataTableMessage = dataTableMessage;
	}
	
	public QuoteRequestSearchResultVO getSelectedQuoteRequestQueueVO() {
		return selectedQuoteRequestQueueVO;
	}

	public void setSelectedQuoteRequestQueueVO(
			QuoteRequestSearchResultVO selectedQuoteRequestQueueVO) {
		this.selectedQuoteRequestQueueVO = selectedQuoteRequestQueueVO;
	}

	public List<QuoteRequestStatus> getQuoteRequestStatusList() {
		return quoteRequestStatusList;
	}

	public void setQuoteRequestStatusList(List<QuoteRequestStatus> quoteRequestStatusList) {
		this.quoteRequestStatusList = quoteRequestStatusList;
	}

	public List<QuoteRequestType> getQuoteRequestTypeList() {
		return quoteRequestTypeList;
	}

	public void setQuoteRequestTypeList(List<QuoteRequestType> dbQuoteRequestTypeList) {
		this.quoteRequestTypeList.addAll(dbQuoteRequestTypeList);
		QuoteRequestType allQuoteRequestType = new QuoteRequestType();
		allQuoteRequestType.setCode(QuoteRequestTypeEnum.IMM_NEED_ALL.getCode());
		allQuoteRequestType.setName(QuoteRequestTypeEnum.IMM_NEED_ALL.getDescription());
		this.quoteRequestTypeList.add(0, allQuoteRequestType);
	}

	public QuoteRequestSearchCriteriaVO getCriteria() {
		return criteria;
	}

	public void setCriteria(QuoteRequestSearchCriteriaVO criteria) {
		this.criteria = criteria;
	}

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}

	public String getRequestor() {
		return requestor;
	}

	public void setRequestor(String requestor) {
		this.requestor = requestor;
	}

	public String getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(String assignedTo) {
		this.assignedTo = assignedTo;
	}

	public String getRequestorEmpNo() {
		return requestorEmpNo;
	}

	public void setRequestorEmpNo(String requestorEmpNo) {
		this.requestorEmpNo = requestorEmpNo;
	}

	public String getAssignedToEmpNo() {
		return assignedToEmpNo;
	}

	public void setAssignedToEmpNo(String assignedToEmpNo) {
		this.assignedToEmpNo = assignedToEmpNo;
	}

	public List<QuoteRequestType> getDbRequestTypeList() {
		return dbRequestTypeList;
	}

	public void setDbRequestTypeList(List<QuoteRequestType> dbRequestTypeList) {
		this.dbRequestTypeList = dbRequestTypeList;
	}

	public Long getScrollPosition() {
		return scrollPosition;
	}

	public void setScrollPosition(Long scrollPosition) {
		this.scrollPosition = scrollPosition;
	}

	public String getCompletedStatus() {
		return completedStatus;
	}

	public void setCompletedStatus(String completedStatus) {
		this.completedStatus = completedStatus;
	}

	public String getClosedStatus() {
		return closedStatus;
	}

	public void setClosedStatus(String closedStatus) {
		this.closedStatus = closedStatus;
	}

}
