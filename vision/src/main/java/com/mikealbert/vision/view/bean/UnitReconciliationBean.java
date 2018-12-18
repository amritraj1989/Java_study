package com.mikealbert.vision.view.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.DataConstants;
import com.mikealbert.data.dao.ContractLineDAO;
import com.mikealbert.data.dao.DocDAO;
import com.mikealbert.data.dao.QuotationModelDAO;
import com.mikealbert.data.entity.ContractLine;
import com.mikealbert.data.entity.Doc;
import com.mikealbert.data.enumeration.TimePeriodCalendarEnum;
import com.mikealbert.data.vo.DriverLOVVO;
import com.mikealbert.data.vo.ProcessQueueResultVO;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.service.InvoiceService;
import com.mikealbert.vision.service.UnitProgressService;
import com.mikealbert.service.ContractService;
import com.mikealbert.vision.view.ViewConstants;



@Component
@Scope("view")
public class UnitReconciliationBean extends StatefulBaseBean {
	
	private static final long serialVersionUID = -8806821953321781659L;
	MalLogger logger = MalLoggerFactory.getLogger(this.getClass());

	@Resource UnitProgressService unitProgressService; // Added for Bug 16598
	
	@Resource
	private QuotationModelDAO quotationModelDAO;
	
	@Resource
	private	InvoiceService	invoiceService;
	
	@Resource
	private	DocDAO	docDAO;
	
	@Resource
	private	ContractLineDAO	contractLineDAO;
	@Resource ContractService contractService;
	
	private static final String  DATA_TABLE_UI_ID = "resultsTable";
	private static final int PO_SEARCH = 1;
	@SuppressWarnings("unused")
	private static final int FINALIZE_SEARCH = 2;
	
	private int selectedRowIndex;
	private int searchType = PO_SEARCH;
	private List<ProcessQueueResultVO> results;
	private ProcessQueueResultVO selectedResult;
	private Long qmdId;
	private String unitNumber;
	private String vin6;
	private boolean processFlag=false;
	private String resultsMessage;
	private Integer	totalRecords	= 0;

	private final String PO_SOURCE_FLQUOTE = "FLQUOTE";
	private final String PO_SOURCE_FLORDER = "FLORDER";
	private static String CALENDER_UI_ID = "inServiceDateCal";
	private boolean	showPagination	= false; 
	private int resultPerPage = ViewConstants.RECORDS_PER_PAGE;
	private LazyDataModel<ProcessQueueResultVO> processQueueLazyList  = null;
	public static final String FIELD_ACCOUNT_NAME = "accountName";
	public static final String  FIELD_UNIT_NO = "unitNo";
	public static final String  FIELD_LAST_UPDATE = "lastUpdate";
	private boolean isSearchRequired = false;
	private boolean lazyLoad = false;
	private boolean executeCount= false;
	private	List<ProcessQueueResultVO> poResult;
	private int selectedRowIndexInPoQueue ;
	private boolean isRestore = false;
	private String	poNumber;
	private 	Date inservdate; // added for Bug 16598
	private boolean inservexist=false; // added for Bug 16598
	private boolean inServSaveSuccess=false; // added for Bug 16598
	private boolean  showMissingDeliDlrError = false;
	private boolean  showRevisedQuoteError = false;

	@PostConstruct
	public void init() {
		initializeDataTable(480, 770, new int[] { 35, 15, 36, 9}).setHeight(-70);		
		super.openPage();
		
		processQueueLazyList = new LazyDataModel<ProcessQueueResultVO>() {
			private static final long serialVersionUID = 1L;		
			@Override
			public List<ProcessQueueResultVO> load(int first, int pageSize, String inputSortField, SortOrder inputSortOrder, Map<String, Object> arg4) {
								int pageIdx = (first == 0) ? 0 : (first / pageSize);
				PageRequest page = new PageRequest(pageIdx,pageSize);				
				Sort sort = null;
				String querySortField = getSortField(inputSortField);				
				if (inputSortOrder.name().equalsIgnoreCase(SortOrder.DESCENDING.toString())) {
					 sort = new Sort(Sort.Direction.DESC, querySortField);
				}else{
					 sort = new Sort(Sort.Direction.ASC, querySortField);
				}
				List<ProcessQueueResultVO> pqList  = null;
				pqList  = getLazySearchResultList(page ,sort);

				return pqList;
			}

			// Note this is an unconventional implementation; it's completely based off of index not objects
			// so this implementation has almost nothing in it.
			@Override
			public ProcessQueueResultVO getRowData(String rowKey) {
				if(MALUtilities.isNotEmptyString(rowKey)){
					setSelectedRowIndex(Integer.parseInt(rowKey));
				}else{
					setSelectedRowIndex(0);
				}
				return null;
			}
			
		};
		
	}
	private String getSortField(String sortField) {

		if (FIELD_ACCOUNT_NAME.equalsIgnoreCase(sortField)) {
			return DataConstants.SEARCH_SORT_FIELD_ACCOUNT;
			
		} else if (FIELD_UNIT_NO.equalsIgnoreCase(sortField)) {
			return DataConstants.SEARCH_SORT_FIELD_UNIT_NO;
			
		} else if (FIELD_LAST_UPDATE.equalsIgnoreCase(sortField)) {
			return DataConstants.SEARCH_SORT_FIELD_LAST_UPDATE;
			
		} else {
			return DataConstants.SEARCH_SORT_FIELD_DEFAULT;
		}
	}
	 
	private List<ProcessQueueResultVO> getLazySearchResultList(PageRequest page ,Sort sort){
		List<ProcessQueueResultVO> pqVOList =  new ArrayList<ProcessQueueResultVO>(); 
		if(isSearchRequired){
			if(getSearchType() == 1){
				pqVOList =  quotationModelDAO.getReleaseOnlyPoResults(unitNumber, vin6, page, sort);
				if(executeCount){
					Integer count = quotationModelDAO.getCountOfReleasedOnlyPoResults(unitNumber, vin6);
					processQueueLazyList.setRowCount(count);
					totalRecords = count;  
				}else{
					if(!MALUtilities.isEmpty(unitNumber)){
						if(pqVOList !=  null){
							processQueueLazyList.setRowCount(pqVOList.size());
							totalRecords = pqVOList.size();  
						}						
					}
				}
			}else{
				//for finalized
				pqVOList =  quotationModelDAO.getUnitsToFinalizeQueueResults(unitNumber, vin6, page, sort);
				processQueueLazyList.setRowCount(pqVOList.size()); 
				totalRecords = pqVOList.size();
			}
			
			executeCount = false;
			if(!isRestore)
			selectedRowIndex =  page.getPageSize() *  page.getPageNumber();
			
			poResult = pqVOList;
			processQueueLazyList.setWrappedData(pqVOList);
			DataTable dt = (DataTable) getComponent(DATA_TABLE_UI_ID);	
			if (pqVOList != null && pqVOList.size()> 0 &&  dt != null) {
				if(!isRestore)
				dt.setSelection(pqVOList.get(0));				
				processFlag = true;
			}else{
				processFlag	= false;
			}		
			
			isRestore = false;
			adjustDataTableAfterSearch(pqVOList);
		}return pqVOList;
		
	}

	@Override
	protected void loadNewPage() {
		thisPage.setPageDisplayName("Unit Reconciliation");
		thisPage.setPageUrl(ViewConstants.UNIT_RECONCILIATION);
	}

	@Override
	protected void restoreOldPage() {
		unitNumber = (String) thisPage.getRestoreStateValues().get(ViewConstants.VIEW_PARAM_UNIT_NO);
		vin6 = (String) thisPage.getRestoreStateValues().get(ViewConstants.VIEW_PARAM_VIN);
		searchType = (Integer) thisPage.getRestoreStateValues().get(ViewConstants.VIEW_PARAM_MODE);
		selectedRowIndex = (Integer) thisPage.getRestoreStateValues().get(ViewConstants.VIEW_PARAM_INDEX);
		search();
		isRestore = true;
		DataTable dt = (DataTable) getComponent(DATA_TABLE_UI_ID);	
		Integer pageStartIndex = (Integer) thisPage.getRestoreStateValues().get("SELECTD_PAGE_START_INDEX");
		if(pageStartIndex != null){
			dt.setFirst(pageStartIndex);
			this.selectedResult = (ProcessQueueResultVO) thisPage.getRestoreStateValues().get(ViewConstants.DT_SELECTED_ITEM);
		}
	}
	
	private Map<String, Object> getCurrentPageRestoreStateValuesMap() {
		Map<String, Object> restoreStateValues = new HashMap<String, Object>();
		restoreStateValues.put(ViewConstants.VIEW_PARAM_MODE, searchType);
		restoreStateValues.put(ViewConstants.VIEW_PARAM_UNIT_NO, unitNumber);
		restoreStateValues.put(ViewConstants.VIEW_PARAM_VIN, vin6);
		restoreStateValues.put(ViewConstants.VIEW_PARAM_INDEX, selectedRowIndex);
		
		DataTable dt = (DataTable) getComponent(DATA_TABLE_UI_ID);
		
		if (dt != null) {
			restoreStateValues.put("DEFAULT_SELECTION", poResult.get(selectedRowIndex));
			restoreStateValues.put(ViewConstants.VIEW_PARAM_INDEX, selectedRowIndexInPoQueue);
			restoreStateValues.put("SELECTD_PAGE_START_INDEX", dt.getFirst());
			restoreStateValues.put(ViewConstants.DT_SELECTED_ITEM, selectedResult);
			}
		
		return restoreStateValues;
	}
	
	public void search(){
		isRestore = false;
		results = new ArrayList<ProcessQueueResultVO>();
		isSearchRequired = true;
		if(!MALUtilities.isEmpty(vin6) && vin6.length() < 6){
			isSearchRequired = false;
			addErrorMessage("vin","required_minimum_characters",new String[]{"VIN","six"});
			return ;
		}
		loadResults();
		if(!MALUtilities.isEmpty(unitNumber)) {
			executeCount	= false;
			
		}
		showMissingDeliDlrError = false;
		showRevisedQuoteError = false;
    }
	//Add for Bug 16598
	public void populateInservDate(){
		int index = selectedRowIndex;
		selectedRowIndexInPoQueue = index;
		int actualIndex = index;
		if(index >= resultPerPage){
			actualIndex = index%resultPerPage;
			
		}
		Date stardate = null;	
		setSelectedResult(poResult.get(actualIndex));
		setSelectedRowIndex(actualIndex);
		if(selectedResult != null){
			ContractLine contractline = contractLineDAO.findByFmsIdOnly(selectedResult.getFmsId());
			if(contractline != null){
				inservdate = contractline.getInServDate();
				stardate = contractline.getStartDate();
				inservexist = true;
			}else{
				inservdate = null;
				inservexist = false;
			}
		}else{
			inservdate = null;
			inservexist = false;
		}
		if(showMissingDeliDlrError){
			addErrorMessage("po_missing_dealer_error", getSelectedResult().getPoNumber());
			showMissingDeliDlrError = false;
		}
		if(showRevisedQuoteError){
			addErrorMessage("plain.message", "Unit was paid in Willow so no finalized quote exists. Please reconcile costs for this unit in PO026.");
			showRevisedQuoteError = false;
		}
		
		if(stardate != null || inservdate == null){
			inservexist = false;
			RequestContext context = RequestContext.getCurrentInstance();
			context.addCallbackParam("failure", true);
		}
		
	}
	public void updateInSrvDate(){
		boolean isWithinTimePeriod = false;
		RequestContext context = RequestContext.getCurrentInstance();
		if(inservdate == null){
			super.addErrorMessageSummary(CALENDER_UI_ID,"required.field", "In Service Date ");			
			context.addCallbackParam("failure", true);
		} 
		else{
			isWithinTimePeriod = contractService.isWithinTimePeriod(
					getLoggedInUser().getCorporateEntity(), TimePeriodCalendarEnum.POSTING, inservdate);	
			if(!isWithinTimePeriod){
				super.addErrorMessageSummary(CALENDER_UI_ID, "custom.message", "Time period sequence not setup for In Service Date");	
				context.addCallbackParam("failure", true);						
			}
			else{
				if(getprevActualEndDate()!= null && (MALUtilities.compateDates( getprevActualEndDate(),inservdate) > 0 ))
				{super.addErrorMessageSummary(CALENDER_UI_ID,"custom.message","The in service date cannot be before the previous contract end date for this unit.");
				context.addCallbackParam("failure", true);
				}
				else{
			unitProgressService.updateInService(selectedResult.getFmsId(), inservdate);
			addSuccessMessage("saved.success", "In Service Date");
			inServSaveSuccess = true;
			inservdate = null;
				}
			}

		}
	}
	
	public void cancelInService(){
		UIComponent comp = getComponent(CALENDER_UI_ID);
   	 	if(comp!= null) ((UIInput) comp).setValid(true); 
		inservdate = null;
		if(inServSaveSuccess)
			addSuccessMessage("saved.success", "In Service Date");
		inServSaveSuccess = false;
	}
	
	public Date getprevActualEndDate(){
		List<ContractLine> contractlinelist = contractLineDAO.findByFmsId(selectedResult.getFmsId());
		Date maxActualEndDate = null;
		for (ContractLine contractLine : contractlinelist) {
			if(maxActualEndDate == null){
				maxActualEndDate = contractLine.getActualEndDate();
			}
			if(maxActualEndDate != null && contractLine.getActualEndDate()!= null){
				if(MALUtilities.compateDates( contractLine.getActualEndDate(),maxActualEndDate) > 0 ){
					maxActualEndDate =  contractLine.getActualEndDate();
				}
			}
		}
		
		return maxActualEndDate;
	}
	public void setSelectedPo(){
		String rowIndex = Integer.toString(getSelectedRowIndex()); 
		int index = Integer.parseInt(rowIndex);

		selectedRowIndexInPoQueue = index;
		int actualIndex = index;
		if(index >= resultPerPage){
			actualIndex = index%resultPerPage;
			
		}
			
		System.out.println("Actual index ="+actualIndex);
		setSelectedResult(poResult.get(actualIndex));
		setSelectedRowIndex(actualIndex);
	}
	public void process(){
		String rowIndex = Integer.toString(getSelectedRowIndex());
		int index = Integer.parseInt(rowIndex);

		selectedRowIndexInPoQueue = index;
		int actualIndex = index;
		if(index >= resultPerPage){
			actualIndex = index%resultPerPage;
			
		}
			
		setSelectedResult(poResult.get(actualIndex));
		setSelectedRowIndex(actualIndex);
		showMissingDeliDlrError = false;
		qmdId = selectedResult.getQmdId();
		poNumber = selectedResult.getPoNumber();
		
	    // changed getVendorCode to getSubAccountCode for Bug 16467
		//    Don't check for delivering dealer if the doc/invoice has already been posted
		if(getSearchType() == PO_SEARCH) {
			Long docId = Long.parseLong(getSelectedResult().getDocId());
			if (MALUtilities.isEmpty(getSelectedResult().getSubAccountCode())) {
				Doc doc = docDAO.findById(docId).orElse(null);
				if ((doc.getOrderType() == null) || !doc.getOrderType().equalsIgnoreCase("T")) {
				
					if (PO_SOURCE_FLORDER.equals(getSelectedResult().getPoSourceCode())
							|| PO_SOURCE_FLQUOTE.equals(getSelectedResult().getPoSourceCode())) {
					
						addErrorMessage("po_missing_dealer_error", getSelectedResult().getPoNumber());
						showMissingDeliDlrError = true;
						return;	
					}
				}
			}
		}
		
		
		forward();
     	
    }
	
	public String cancel(){
    	return super.cancelPage();      	
    }

	private void forward() {
		saveRestoreStateValues(getCurrentPageRestoreStateValuesMap());
		Map<String, Object> nextPageValues = new HashMap<String, Object>();
		if(getSearchType()== PO_SEARCH){
			nextPageValues.put(ViewConstants.VIEW_PARAM_RELEASED_PO_DOC_NO, poNumber);
			nextPageValues.put(ViewConstants.VIEW_PARAM_QUOTE_MODEL_ID, qmdId);
			saveNextPageInitStateValues(nextPageValues);
			forwardToURL(ViewConstants.INVOICE_ENTRY);
		}else{
			//for RC-1770
			showRevisedQuoteError = false;
			Long revisedQmdID = quotationModelDAO.getRevisedQmd(qmdId);
			if(revisedQmdID == null){
				addErrorMessage("plain.message", "Unit was paid in Willow so no finalized quote exists. Please reconcile costs for this unit in PO026.");
				showRevisedQuoteError = true;
				return;
			}
			
			nextPageValues.put(ViewConstants.VIEW_PARAM_MODE, ViewConstants.VIEW_MODE_FINALIZE);			
			nextPageValues.put(ViewConstants.VIEW_PARAM_QUOTE_MODEL_ID, qmdId);
			saveNextPageInitStateValues(nextPageValues);		
			forwardToURL(ViewConstants.CAPITAL_COSTS);
		
		}
		
		
	}
	
	
	private void loadResults() {
		if (searchType == PO_SEARCH) {
			showPagination = true;
			isSearchRequired = true;
			lazyLoad = true;
			executeCount = true;
		} else {
			showPagination = true;
			isSearchRequired = true;
			lazyLoad = true;
			executeCount = true;
		}
		DataTable dt = (DataTable) getComponent(DATA_TABLE_UI_ID);
		
		if (dt != null) {
			dt.setFirst(0);
		}
		
	}
	public void	deleteInvoice(){
		try{
			if(selectedResult != null){
				qmdId = selectedResult.getQmdId();
				poNumber = selectedResult.getPoNumber();
				
				invoiceService.deleteInvoice(poNumber,null);
				addSuccessMessage("process.success", "Delete invoice");
				setSelectedResult(null);	
				setSelectedRowIndex(0);
			}
			
			
		}catch(Exception ex){
			handleException("generic.error.occured.while", new String[]{"deleting invoice"}, ex, null);
		}
	}
	
	private void adjustDataTableAfterSearch(List results){
		if (results.size() > 0) {
			if(results.size() < 5){
				getDataTable().setHeight(70);
			}else{
				super.getDataTable().setMaximumHeight();
			}
			resultsMessage = null;
		} else {
			resultsMessage	= "No Records Found";
			super.getDataTable().setHeight(-70);
		}
	}	

    public void onRowSelect(SelectEvent event) {
    	populateInservDate();
    }

	
	public int getSelectedRowIndex() {
		return selectedRowIndex;
	}

	public void setSelectedRowIndex(int selectedRowIndex) {
		this.selectedRowIndex = selectedRowIndex;
	}

	public int getSearchType() {
		return searchType;
	}

	public void setSearchType(int searchType) {
		this.searchType = searchType;
	}

	public List<ProcessQueueResultVO> getResults() {
		return results;
	}

	public void setResults(List<ProcessQueueResultVO> results) {
		this.results = results;
	}

	public ProcessQueueResultVO getSelectedResult() {
		return selectedResult;
	}

	public void setSelectedResult(ProcessQueueResultVO selectedResult) {
		this.selectedResult = selectedResult;
	}

	public Long getQmdId() {
		return qmdId;
	}

	public void setQmdId(Long qmdId) {
		this.qmdId = qmdId;
	}

	public String getUnitNumber() {
		return unitNumber;
	}

	public void setUnitNumber(String unitNumber) {
		this.unitNumber = unitNumber;
	}

	public String getVin6() {
		return vin6;
	}

	public void setVin6(String vin6) {
		this.vin6 = vin6;
	}

	public boolean isProcessFlag() {
		return processFlag;
	}

	public void setProcessFlag(boolean processFlag) {
		this.processFlag = processFlag;
	}

	public String getResultsMessage() {
		return resultsMessage;
	}

	public void setResultsMessage(String resultsMessage) {
		this.resultsMessage = resultsMessage;
	}

	public Integer getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(Integer totalRecords) {
		this.totalRecords = totalRecords;
	}

	public boolean isShowPagination() {
		return showPagination;
	}

	public void setShowPagination(boolean showPagination) {
		this.showPagination = showPagination;
	}

	public int getResultPerPage() {
		return resultPerPage;
	}

	public void setResultPerPage(int resultPerPage) {
		this.resultPerPage = resultPerPage;
	}
	public LazyDataModel<ProcessQueueResultVO> getProcessQueueLazyList() {
		return processQueueLazyList;
	}
	public void setProcessQueueLazyList(LazyDataModel<ProcessQueueResultVO> processQueueLazyList) {
		this.processQueueLazyList = processQueueLazyList;
	}
	public boolean isLazyLoad() {
		return lazyLoad;
	}
	public void setLazyLoad(boolean lazyLoad) {
		this.lazyLoad = lazyLoad;
	}
	public Date getInservdate() {
		return inservdate;
	}
	public void setInservdate(Date inservdate) {
		this.inservdate = inservdate;
	}
	public boolean isInservexist() {
		return inservexist;
	}
	public void setInservexist(boolean inservexist) {
		this.inservexist = inservexist;
	}

	

}