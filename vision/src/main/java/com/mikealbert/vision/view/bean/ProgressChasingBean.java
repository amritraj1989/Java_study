package com.mikealbert.vision.view.bean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Component;

import com.mikealbert.data.DataConstants;
import com.mikealbert.data.dao.ProgressChasingDAO;
import com.mikealbert.data.entity.MaintenanceRequest;
import com.mikealbert.data.enumeration.LogBookTypeEnum;
import com.mikealbert.data.vo.ProcessQueueResultVO;
import com.mikealbert.data.vo.ProgressChasingQueueVO;
import com.mikealbert.data.vo.ProgressChasingVO;
import com.mikealbert.service.LogBookService;
import com.mikealbert.service.MaintenanceRequestService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.comparator.ProgressChasingRowComparator;
import com.mikealbert.vision.view.ViewConstants;
import com.mikealbert.vision.vo.LogBookTypeVO;

@Component
@Scope("view")
public class ProgressChasingBean extends StatefulBaseBean {

	private static final long serialVersionUID = 4997634815660180368L;
	
	@Resource ProgressChasingDAO progressChasingDAO;
	@Resource MaintenanceRequestService maintenanceRequestService;
	@Resource LogBookService logBookService;
	
	private final LogBookTypeEnum logBookType = LogBookTypeEnum.TYPE_ACTIVITY;
	private List<LogBookTypeVO> logBookTypes;		
	
	private LazyDataModel<ProgressChasingVO> progressChasingLazyList = null;
	private List<ProgressChasingVO> progressChasingList = null;
	private ProgressChasingVO selectedProgressChasingVO = null;
	private ProgressChasingQueueVO selectedProgressChasingQueueVO = null;
	private Long selectedMrqId;
	private Long mrqId;
	private String selectedPoStatus;
	private String selectedPoStatusDescription;	
	private ProgressChasingQueueVO completedNoVendorVO;
	
	private int selectionIndex = -1;	
	private static final String  DATA_TABLE_UI_ID ="progressChasingDataTable";
	private int resultsPerPage = ViewConstants.RECORDS_PER_PAGE_SMALL;	
	private boolean isSearchRequired = false;
	private boolean returnedFromNextPage = false; //used when determining whether or not to select first row in datatable
	private DataTable dt;	
	private String dataTableMessage;	
	private String lastUpdatedByFilter;
	private	List<ProgressChasingQueueVO> dataList;
	
	@PostConstruct
	public void init() {
		super.openPage();
		initializeDataTable(620, 830, new int[] { 5, 8, 7, 11, 12, 8, 8, 8, 8, 8, 8, 9 }).setHeight(0);
		
		setLogBookTypes(new ArrayList<LogBookTypeVO>());
    	getLogBookTypes().add(new LogBookTypeVO(logBookType, false));		
    	dataList =  maintenanceRequestService.getProgressChasingDataList();
    	completedNoVendorVO = new ProgressChasingQueueVO();
    	completedNoVendorVO.setMaintRequestStatus("C");
    	completedNoVendorVO.setPoStatusCount(0);
    	completedNoVendorVO.setMaintRequestStatusDescription("Completed");
		progressChasingLazyList = new LazyDataModel<ProgressChasingVO>() {
			private static final long serialVersionUID = 1L;
				
			@Override
			public List<ProgressChasingVO> load(int first, int pageSize, String inputSortField, SortOrder inputSortOrder, Map<String, Object> filters) {
								int pageIdx = (first == 0) ? 0 : (first / pageSize);
				PageRequest page = new PageRequest(pageIdx,pageSize);				
				

				Sort sort = null;								
				if (inputSortOrder.name().equalsIgnoreCase(SortOrder.ASCENDING.toString())) {
					 sort = new Sort(Sort.Direction.ASC, inputSortField);
				}else{
					 sort = new Sort(Sort.Direction.DESC, inputSortField);
				}

				
				progressChasingList  = getLazySearchResultList(page, sort);

				return progressChasingList;
			}

			@Override
			public ProgressChasingVO getRowData(String rowKey) {
				for (ProgressChasingVO pcVO : progressChasingList) {
					if (String.valueOf(pcVO.getMrqId()).equals(rowKey))
						return pcVO;
				}
				return null;
			}

			@Override
			public Object getRowKey(ProgressChasingVO progressChasingVO) {
				return progressChasingVO.getMrqId();
			}
		};
		progressChasingLazyList.setPageSize(resultsPerPage);
	}	
	
	/**
	 * Sets variables on entry to the page
	 */
	protected void loadNewPage() {	
		thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_PROGRESS_CHASING);
		thisPage.setPageUrl(ViewConstants.PROGRESS_CHASING);
		Map<String, Object> map = super.thisPage.getInputValues();		
		setSelectedPoStatus((String) map.get(ViewConstants.VIEW_PARAM_PO_STATUS));
	}	
	
	protected void restoreOldPage() {

		Map<String, Object> map = super.thisPage.getRestoreStateValues();
		setSelectedPoStatus((String) map.get(ViewConstants.VIEW_PARAM_PO_STATUS));
		setMrqId((Long) map.get(ViewConstants.VIEW_PARAM_MRQ_ID));
		this.isSearchRequired = true;
		this.selectionIndex = (Integer) thisPage.getRestoreStateValues().get("SELECTD_INDEX");
		
		this.setReturnedFromNextPage((Boolean) thisPage.getRestoreStateValues().get("NEXT_PAGE"));
		
		this.lastUpdatedByFilter = (String)thisPage.getRestoreStateValues().get("LAST_UPDATED_BY_FILTER");	
		this.selectedPoStatusDescription = (String)thisPage.getRestoreStateValues().get("SELECTED_PO_STATUS_DESC");

		dt = (DataTable) getComponent(DATA_TABLE_UI_ID);	
		Integer pageStartIndex = (Integer) thisPage.getRestoreStateValues().get("SELECTD_PAGE_START_INDEX");

		if(pageStartIndex != null){
			dt.setFirst(pageStartIndex);
			dt.setSortOrder((String) thisPage.getRestoreStateValues().get("DEFAULT_SORT_ORDER"));
			dt.setValueExpression("sortBy", (ValueExpression) thisPage.getRestoreStateValues().get("DEFAULT_SORT_VE"));
		}

	}	
	
	//  We don't need to perform explicit search. The Primeface's LazyDataModel's load method will get call automatically 
	//	and will populate data.
	 
	public void performSearch(ProgressChasingQueueVO selectedPoStatus) {

		if (selectedPoStatus != null){
			this.setSelectedProgressChasingQueueVO(selectedPoStatus);
			this.selectedPoStatus = selectedPoStatus.getMaintRequestStatus();
			this.selectedPoStatusDescription = selectedPoStatus.getMaintRequestStatusDescription();
		}
		this.isSearchRequired = true;
		this.selectionIndex = 0;
		this.returnedFromNextPage = false;
		
		this.selectedProgressChasingVO = null;

		DataTable pfDataTable = ((DataTable) getComponent(DATA_TABLE_UI_ID));
		if(pfDataTable != null) {
			pfDataTable.setFirst(0);

			
			ELContext elContext = getFacesContext().getELContext();
			ExpressionFactory elFactory = getFacesContext().getApplication().getExpressionFactory();

			ValueExpression valueExpression = elFactory.createValueExpression(elContext, "#{poStatus.actualStartDate}", Date.class);
			pfDataTable.setValueExpression("sortBy", valueExpression);		

			valueExpression = elFactory.createValueExpression(elContext, "ascending", String.class);
			pfDataTable.setValueExpression("sortOrder", valueExpression);		
			
		}
	}
	
	
	
	private List<ProgressChasingVO> getLazySearchResultList(PageRequest page, Sort sort){
		List<ProgressChasingVO> progressChasingResultsVOList =  new ArrayList<ProgressChasingVO>();
		if(isSearchRequired){
			try {
				progressChasingResultsVOList = maintenanceRequestService.getProgressChasingByPoStatus(this.selectedPoStatus, page, this.lastUpdatedByFilter);
				sortList(progressChasingResultsVOList, sort);
				selectionIndex =  page.getPageSize() *  page.getPageNumber();
				progressChasingLazyList.setRowCount(progressChasingDAO.getProgressChasingByPoStatusCount(this.selectedPoStatus,this.lastUpdatedByFilter));
				progressChasingLazyList.setWrappedData(progressChasingResultsVOList);
				adjustDataTableAfterSearch(progressChasingResultsVOList);
				dt = (DataTable) getComponent(DATA_TABLE_UI_ID); //Due to commandLink datatable component needs to be set before clicking on link
			} catch (Exception ex) {
				super.addErrorMessage("generic.error", ex.getMessage());
			}			
		}

		return progressChasingResultsVOList;
	}	
	
	public List<ProgressChasingQueueVO> getDataList(){
		return dataList;
	} 	
	
	private void adjustDataTableAfterSearch(List<ProgressChasingVO> progressChasingResultsVOList){
		if (progressChasingResultsVOList.size() > 0) {
			super.getDataTable().setMaximumHeight();
		} else {
			this.setDataTableMessage(talMessage.getMessage("no.records.found"));
			super.getDataTable().setHeight(30);
		}
	}
	
	private void sortList(List<ProgressChasingVO> list, Sort sort) {
		Order order = sort.iterator().next();
		String sortOrder = "DESC";
		if(order.isAscending()){
			sortOrder = "ASC";
		}
		
		Collections.sort(list, new ProgressChasingRowComparator(order.getProperty(), sortOrder));
	}
	
	
	public void viewPurchaseOrder(ProgressChasingVO progressChasingVO){
		if(progressChasingVO == null){
			progressChasingVO = selectedProgressChasingVO;
		}
		
		if(progressChasingVO != null){
			setSelectedMrqId(progressChasingVO.getMrqId());
			setSelectedProgressChasingVO(progressChasingVO);
			Map<String, Object> restoreStateValues = getCurrentPageRestoreStateValuesMap();		
			saveRestoreStateValues(restoreStateValues);
	  		
			Map<String, Object> nextPageValues = new HashMap<String, Object>();
			nextPageValues.put(ViewConstants.VIEW_PARAM_MODE, ViewConstants.VIEW_MODE_EDIT);				
	  		nextPageValues.put(ViewConstants.VIEW_PARAM_MRQ_ID, getSelectedMrqId());
	  		saveNextPageInitStateValues(nextPageValues);
	  		this.forwardToURL(ViewConstants.FLEET_MAINT_PURCHASE_ORDER);
		}
	}	
	
	public void cancel(){
		forwardToURL(ViewConstants.DASHBOARD_PAGE);
	}	
	
	private Map<String, Object> getCurrentPageRestoreStateValuesMap() {
		
		Map<String, Object> restoreStateValues = new HashMap<String, Object>();
		restoreStateValues.put(ViewConstants.VIEW_PARAM_PO_STATUS, this.selectedPoStatus);
		restoreStateValues.put(ViewConstants.VIEW_PARAM_MRQ_ID, this.selectedMrqId);
		restoreStateValues.put("SELECTD_INDEX", this.selectionIndex);
		restoreStateValues.put("NEXT_PAGE", true);
		restoreStateValues.put("LAST_UPDATED_BY_FILTER", this.lastUpdatedByFilter);
		restoreStateValues.put("SELECTED_PO_STATUS_DESC", this.selectedPoStatusDescription);
		
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
	
	public ProgressChasingVO getSelectedProgressChasingVO() {
		return selectedProgressChasingVO;
	}	
	
	@SuppressWarnings("unchecked")
	public void setSelectedProgressChasingVO(ProgressChasingVO selectedProgressChasingVO) {
		this.selectedProgressChasingVO = selectedProgressChasingVO;
		selectionIndex = progressChasingList.indexOf(this.selectedProgressChasingVO);

		if (dt != null){
			selectionIndex = selectionIndex + dt.getFirst();
		}
	}

	@SuppressWarnings("unchecked")
	public BigDecimal getPoTotalSum(){
		BigDecimal poTotalSum = new BigDecimal(0.00).setScale(2);
	        List<ProgressChasingVO> voList = progressChasingList;
	        if((!voList.isEmpty()) && (!MALUtilities.isEmpty(voList.get(0).getSumTaskTotalCost()))){
	        	poTotalSum = voList.get(0).getSumTaskTotalCost();
	        }

	        return poTotalSum;
	}
	
	@SuppressWarnings("unchecked")
	public BigDecimal getRechargeTotalSum(){
		BigDecimal rechargeTotal = new BigDecimal(0.00).setScale(2);
	        List<ProgressChasingVO> voList = progressChasingList;
	        if((!voList.isEmpty()) && (!MALUtilities.isEmpty(voList.get(0).getSumRechargeTotal()))){
	        	rechargeTotal = voList.get(0).getSumRechargeTotal();
	        }	        

		return rechargeTotal;
	}
	
	public MaintenanceRequest getSelectedMRQ(){
		return maintenanceRequestService.getMaintenanceRequestByMrqId(getSelectedProgressChasingVO().getMrqId());
	}
	
	public boolean isLogNotesDisabled(){
		boolean isDisabled = true;
		if(!MALUtilities.isEmpty(getSelectedProgressChasingVO())){
			isDisabled = false;
		}
		return isDisabled;
	}
	
	/**
	 * When log entries exist for the unit, this method will determine what styleClass to use
	 * to make the button noticeable.
	 * @return
	 */
	public String notesLogStyleClass(){
		String styleClass = "";
		
		if(!MALUtilities.isEmpty(getSelectedProgressChasingVO())){
			if(logBookService.hasLogs(getSelectedMRQ().getFleetMaster(), new ArrayList<LogBookTypeEnum>(Arrays.asList(LogBookTypeEnum.TYPE_FM_UNIT_NOTES)))){
				styleClass = ViewConstants.BUTTON_INDICATOR_STYLE_CLASS;
			}
		}
		
		return styleClass;
	}	
	
	public String getDataTableMessage() {
		return dataTableMessage;
	}
	
	public void setDataTableMessage(String dataTableMessage) {
		this.dataTableMessage = dataTableMessage;
	}
	
	public LazyDataModel<ProgressChasingVO> getProgressChasingLazyList() {
		return progressChasingLazyList;
	}
	
	public int getResultsPerPage() {
		return resultsPerPage;
	}
	
	public void setResultsPerPage(int resultsPerPage) {
		this.resultsPerPage = resultsPerPage;
	}	
	
	public boolean isReturnedFromNextPage() {
		return returnedFromNextPage;
	}

	public void setReturnedFromNextPage(boolean returnedFromNextPage) {
		this.returnedFromNextPage = returnedFromNextPage;
	}	
	
	public Long getSelectedMrqId() {
		return selectedMrqId;
	}

	public void setSelectedMrqId(Long selectedMrqId) {
		this.selectedMrqId = selectedMrqId;
	}

	public Long getMrqId() {
		return mrqId;
	}

	public void setMrqId(Long mrqId) {
		this.mrqId = mrqId;
	}

	public String getSelectedPoStatus() {
		return selectedPoStatus;
	}

	public void setSelectedPoStatus(String selectedPoStatus) {
		this.selectedPoStatus = selectedPoStatus;
	}

	public String getSelectedPoStatusDescription() {
		return selectedPoStatusDescription;
	}

	public void setSelectedPoStatusDescription(String selectedPoStatusDescription) {
		this.selectedPoStatusDescription = selectedPoStatusDescription;
	}
	
	public int getSelectionIndex() {
		return selectionIndex;
	}

	public void setSelectionIndex(int selectionIndex) {
		this.selectionIndex = selectionIndex;
	}

	public ProgressChasingQueueVO getSelectedProgressChasingQueueVO() {
		return selectedProgressChasingQueueVO;
	}

	public void setSelectedProgressChasingQueueVO(
			ProgressChasingQueueVO selectedProgressChasingQueueVO) {
		this.selectedProgressChasingQueueVO = selectedProgressChasingQueueVO;
	}

	public String getLastUpdatedByFilter() {
		return lastUpdatedByFilter;
	}

	public void setLastUpdatedByFilter(String lastUpdatedByFilter) {
		this.lastUpdatedByFilter = lastUpdatedByFilter;
	}

	public LogBookTypeEnum getLogBookType() {
		return logBookType;
	}

	public List<LogBookTypeVO> getLogBookTypes() {
		return logBookTypes;
	}

	public void setLogBookTypes(List<LogBookTypeVO> logBookTypes) {
		this.logBookTypes = logBookTypes;
	}

	public ProgressChasingQueueVO getCompletedNoVendorVO() {
		return completedNoVendorVO;
	}

	public void setCompletedNoVendorVO(ProgressChasingQueueVO completedNoVendorVO) {
		this.completedNoVendorVO = completedNoVendorVO;
	}
	
}
