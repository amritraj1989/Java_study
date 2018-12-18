package com.mikealbert.vision.view.bean;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.el.ValueExpression;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.UploadedFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.mikealbert.data.DataConstants;
import com.mikealbert.data.entity.AnalysisCategory;
import com.mikealbert.data.entity.DebitCreditMemoTransaction;
import com.mikealbert.data.entity.DebitCreditStatusCode;
import com.mikealbert.data.entity.PersonnelBase;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.data.enumeration.DebitCreditStatusEnum;
import com.mikealbert.data.vo.DebitCreditMemoSearchCriteriaVO;
import com.mikealbert.data.vo.DebitCreditMemoSearchResultVO;
import com.mikealbert.data.vo.DocumentFileVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.CustomerAccountService;
import com.mikealbert.service.DebitCreditMemoService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.util.XLSUtil;
import com.mikealbert.vision.view.ViewConstants;



@Component
@Scope("view")
public class DebitCreditMemoSearchBean extends StatefulBaseBean {
	private static final long serialVersionUID = 1L;
	
	@Resource DebitCreditMemoService debitCreditMemoService;
	@Resource CustomerAccountService externalAccountService;
	
	@Value("${debit.credit.upload.dir:NOT_DEFINED}")
	private String debitCreditUploadDir;	

	@Value("${debit.credit.highlight.warnings}")
	private String highlightWarnings;	

	
	private LazyDataModel<DebitCreditMemoSearchResultVO> debitCreditMemoLazyList  = null;
	private List<DebitCreditMemoSearchResultVO> debitCreditMemoList  = null;
	private DebitCreditMemoSearchCriteriaVO searchCriteria;
	private List<DebitCreditMemoSearchResultVO> selectedSearchVOs  = null;
	private List<DebitCreditStatusCode> debitCreditStatusCodes;
	private List<AnalysisCategory> analysisCategories;  
	private List<PersonnelBase> userNameList;
	
	private List<DebitCreditMemoSearchResultVO> exportList = null;
	private List<Map<String, Object>> downloadableRowsData = new ArrayList<Map<String, Object>>();;
	private List<String> downloadableColumns = new ArrayList<String>();
	private static String EXCEL_DL_COOKIE = "client.excel.download";
	private Sort sort;
	
	private String selectedSubmitter;	
	private String selectedProcessors;	
	private String client;
	private boolean isSearchRequired = false; 
	private int selectionIndex = -1;
	private String dataTableMessage;

	private boolean enableEditBtn = false;
	private boolean enableApproveBtn = false;
	private boolean enableAllocateBtn = false;
	private boolean enableUploadFileBtn = false;	
	private boolean enableDialogUploadBtn = false;
	private boolean enableProcessBtn = false;
	private boolean hasAprrovePermission ;
	private boolean hasProcessPermission ;
	
	private DocumentFileVO documentFileVO = null;	
	private int recordsProcessed;
	
	private int resultPerPage = ViewConstants.RECORDS_PER_PAGE_SMALL;
	private static final String  DATA_TABLE_UI_ID ="dcMemoDataTable";
	private static final String ACCOUNT_TYPE_CUSTOMER = "C";
	static final String RESTORE_PARAM_CRITERIA = "SEARCH_CRITERIA";	
	static final String RESTORE_PARAM_SEARCH_REQUIRED = "SEARCH_REQUIRED";
	private static final String INVALID_DC_ID = "INVALID_DC_ID";
	private static final String RECORDS_PROCESSED = "RECORDS_PROCESSED_SUCCESSFULLY";
	private Long scrollPosition;
	private String warningStyle;
	
	private static final Map<String, String> SORT_BY_MAPPING = new HashMap<String, String>();
	
	static{
		SORT_BY_MAPPING.put("dcNumber", DataConstants.DEBIT_CREDIT_MEMO_SEARCH_SORT_DC_NO);
		SORT_BY_MAPPING.put("clientCode", DataConstants.DEBIT_CREDIT_MEMO_SEARCH_SORT_CLIENT_CODE);
		SORT_BY_MAPPING.put("unitNo", DataConstants.DEBIT_CREDIT_MEMO_SEARCH_SORT_UNIT_NO);
		SORT_BY_MAPPING.put("status", DataConstants.DEBIT_CREDIT_MEMO_SEARCH_SORT_STATUS);
		SORT_BY_MAPPING.put("type", DataConstants.DEBIT_CREDIT_MEMO_SEARCH_SORT_ENTRY_TYPE);
		SORT_BY_MAPPING.put("categoryCode", DataConstants.DEBIT_CREDIT_MEMO_SEARCH_SORT_ANALYSIS_CAT);
		SORT_BY_MAPPING.put("analysisCode", DataConstants.DEBIT_CREDIT_MEMO_SEARCH_SORT_ANALYSIS_CODE);
		SORT_BY_MAPPING.put("newAmount", DataConstants.DEBIT_CREDIT_MEMO_SEARCH_SORT_NET_AMOUNT);
		SORT_BY_MAPPING.put("totalAmount", DataConstants.DEBIT_CREDIT_MEMO_SEARCH_SORT_TOTAL_AMOUNT);
		SORT_BY_MAPPING.put("submitter", DataConstants.DEBIT_CREDIT_MEMO_SEARCH_SORT_SUBMITTED_BY);	
		SORT_BY_MAPPING.put("selectedApprover", DataConstants.DEBIT_CREDIT_MEMO_SEARCH_SORT_SELECTED_APPROVED_BY);	
		SORT_BY_MAPPING.put("approver", DataConstants.DEBIT_CREDIT_MEMO_SEARCH_SORT_APPROVED_BY);
		SORT_BY_MAPPING.put("processor", DataConstants.DEBIT_CREDIT_MEMO_SEARCH_SORT_PROCESSED_BY);
		SORT_BY_MAPPING.put("allocator", DataConstants.DEBIT_CREDIT_MEMO_SEARCH_SORT_ALLOCATED_BY);
		SORT_BY_MAPPING.put("driverAddressState", DataConstants.DEBIT_CREDIT_MEMO_SEARCH_SORT_TXN_STATE_BY);		
		SORT_BY_MAPPING.put("invoiceNo", DataConstants.DEBIT_CREDIT_MEMO_SEARCH_SORT_INVOICE_NO);
		SORT_BY_MAPPING.put("netAmount", DataConstants.DEBIT_CREDIT_MEMO_SEARCH_SORT_NET_AMOUNT);
		SORT_BY_MAPPING.put("lineDescription", DataConstants.DEBIT_CREDIT_MEMO_SEARCH_SORT_LINE_DESC);
		SORT_BY_MAPPING.put("source", DataConstants.DEBIT_CREDIT_MEMO_SEARCH_SORT_SOURCE);
		SORT_BY_MAPPING.put("ticketNo", DataConstants.DEBIT_CREDIT_MEMO_SEARCH_SORT_TICKET_NO);  
	}
	
	
	@PostConstruct
    public void init() {
		
		warningStyle = highlightWarnings.equalsIgnoreCase("Y") ? "background-color: #FADB7E;font-weight: bold;" : "";
		
		initializeDataTable(660, 850, new int[] {3, 8, 4, 3, 5, 5, 6, 4 , 4, 5, 5, 5, 8, 8, 8}).setHeight(0);
		setSearchCriteria(new DebitCreditMemoSearchCriteriaVO());
		openPage();
		loadData();
    	
        debitCreditMemoLazyList = new LazyDataModel<DebitCreditMemoSearchResultVO>() {
			private static final long serialVersionUID = 1L;
				
			@Override
			public List<DebitCreditMemoSearchResultVO> load(int first, int pageSize, String inputSortField, SortOrder inputSortOrder, Map<String, Object> arg4) {
								int pageIdx = (first == 0) ? 0 : (first / pageSize);
				PageRequest page = new PageRequest(pageIdx,pageSize);				
				sort = null;
				String querySortField = SORT_BY_MAPPING.get(inputSortField);
				if(MALUtilities.isNotEmptyString(querySortField)){
					if (inputSortOrder.name().equalsIgnoreCase(SortOrder.DESCENDING.toString())) {
						 sort = new Sort(Sort.Direction.DESC, querySortField);
					}else{
						 sort = new Sort(Sort.Direction.ASC, querySortField);
					}
				}else{
					sort = new Sort(Sort.Direction.ASC, DataConstants.DEBIT_CREDIT_MEMO_SEARCH_SORT_DC_NO);
				}
			
				debitCreditMemoList  = getLazySearchResultList(page ,sort);
				return debitCreditMemoList;
			}

			@Override
			public DebitCreditMemoSearchResultVO getRowData(String rowKey) {
				for (DebitCreditMemoSearchResultVO searchVO : debitCreditMemoList) {
					if (String.valueOf(searchVO.getDcNumber()).equals(rowKey))
						return searchVO;
				}
				return null;
			}

			@Override
			public Object getRowKey(DebitCreditMemoSearchResultVO debitCreditMemoSearchResultVO) {
				return debitCreditMemoSearchResultVO.getDcNumber();
			}
		};
		debitCreditMemoLazyList.setPageSize(ViewConstants.RECORDS_PER_PAGE);        
        
    } 
	
	public void loadData() {
		try {
			loadDebitCreditStatuses();
			loadUserNameList();
			loadAnalysisCategory();
			loadUserPermission();
			adjustBtnState();			
		} catch (Exception e) {
			logger.error(e);
			if(e  instanceof MalBusinessException){				 
				super.addErrorMessage(e.getMessage()); 
			}else{
				super.addErrorMessage("generic.error.occured.while", " building screen.");
			}
		}
	}	
	
    private void loadDebitCreditStatuses() {
    	debitCreditStatusCodes = debitCreditMemoService.getDebitCreditStatusList(); 
	}
    
    private void loadUserNameList(){
    	userNameList = new ArrayList<PersonnelBase>();
    }  
    
    private void loadAnalysisCategory() {
    	analysisCategories = debitCreditMemoService.getAnalysisCategories(); 
	}  
    
    private void loadUserPermission() {
        this.hasAprrovePermission = hasPermission("aprroveDebitCreditMemo");
        this.hasProcessPermission =  hasPermission("processDebitCreditMemo");
	}	
    
	@Override
	protected void loadNewPage() {
		thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_SEARCH_DEBIT_CREDIT_MEMO);
		thisPage.setPageUrl(ViewConstants.SEARCH_DEBIT_CREDIT_MEMO);
	}

	@Override
	protected void restoreOldPage() {		 
		DataTable dt = (DataTable) getComponent(DATA_TABLE_UI_ID);		
		Map<String,Object> restoreMap = thisPage.getRestoreStateValues();
		this.isSearchRequired =  (Boolean)restoreMap.get(RESTORE_PARAM_SEARCH_REQUIRED);		
		this.searchCriteria = (DebitCreditMemoSearchCriteriaVO)restoreMap.get(RESTORE_PARAM_CRITERIA);
		this.scrollPosition = (Long)thisPage.getRestoreStateValues().get("SCROLL_POSITION");
	
		if (!MALUtilities.isEmpty(dt)) {
			dt.setFirst((Integer)restoreMap.get(ViewConstants.DT_SELECTD_PAGE_START_INDEX));
			dt.setSortOrder((String)restoreMap.get(ViewConstants.DT_DEFAULT_SORT_ORDER));
			dt.setValueExpression("sortBy", (ValueExpression)restoreMap.get(ViewConstants.DT_DEFAULT_SORT_VE));			
			DebitCreditMemoSearchResultVO selectedDebitCreditMemoSearchResultVO = (DebitCreditMemoSearchResultVO)restoreMap.get(ViewConstants.DT_SELECTED_ITEM);	
			if(selectedDebitCreditMemoSearchResultVO != null){
				setSelectedSearchVOs(Arrays.asList(selectedDebitCreditMemoSearchResultVO));
			}
		}
		restoreMap.clear();
	}	
 
	private void  updateSearchCriteria(){		
		
		if(MALUtilities.isEmpty(searchCriteria.getSelectedApprover())){
			searchCriteria.setSelectedApprover(null);
			if(searchCriteria.getSelectedApproverNameOrEmpNo() != null && ! (searchCriteria.getSelectedApproverNameOrEmpNo().indexOf(",") >= 0) ){	
				searchCriteria.setSelectedApprover(searchCriteria.getSelectedApproverNameOrEmpNo());	
			}	
		}
		
		if(MALUtilities.isEmpty(searchCriteria.getSubmitter())){
			searchCriteria.setSubmitter(null);
			if(searchCriteria.getSubmitterNameOrEmpNo() != null && ! (searchCriteria.getSubmitterNameOrEmpNo().indexOf(",") >= 0) ){	
				searchCriteria.setSubmitter(searchCriteria.getSubmitterNameOrEmpNo());	
			}	
		}
		
		if(MALUtilities.isEmpty(searchCriteria.getApprover())){
			searchCriteria.setApprover(null);
			if(searchCriteria.getApproverNameOrEmpNo() != null && ! (searchCriteria.getApproverNameOrEmpNo().indexOf(",") >= 0) ){			
				searchCriteria.setApprover(searchCriteria.getApproverNameOrEmpNo());			
			} 
		}
		
		if(MALUtilities.isEmpty(searchCriteria.getProcessor())){
			searchCriteria.setProcessor(null);
			if( searchCriteria.getProcessorNameOrEmpNo() != null && ! (searchCriteria.getProcessorNameOrEmpNo().indexOf(",") >= 0) ){
				searchCriteria.setProcessor(searchCriteria.getProcessorNameOrEmpNo());			
			} 
		}
		if(MALUtilities.isEmpty(searchCriteria.getAllocator())){
			searchCriteria.setAllocator(null);	
			if( searchCriteria.getAllocatorNameOrEmpNo() != null && ! (searchCriteria.getAllocatorNameOrEmpNo().indexOf(",") >= 0) ){			
				searchCriteria.setAllocator(searchCriteria.getAllocatorNameOrEmpNo());			
			} 
		}
	}
	//  We don't need to perform explicit search. The Primeface's LazyDataModel's load method will get call automatically and will populate data.
	public void performSearch() {
		this.isSearchRequired = true;		
		this.selectionIndex = 0;
		this.setSelectedSearchVOs(null);
		setClientNameOrCode();
		
		if(searchCriteria.isProcessedTransactionSearch()){
			initializeDataTable(660, 850, new int[] {5, 20, 6, 10, 10, 6, 8, 8, 20});
		}else{
			initializeDataTable(660, 850, new int[] {3, 8, 5, 3, 5, 5, 6, 4 , 4, 5, 5, 5, 8, 8, 8});
		}
		
		DataTable pfDataTable = ((DataTable) getComponent(DATA_TABLE_UI_ID));
		if(pfDataTable != null){
			pfDataTable.setFirst(0);
		}
	}    
	
	private List<DebitCreditMemoSearchResultVO> getLazySearchResultList(PageRequest page, Sort sort){
		List<DebitCreditMemoSearchResultVO> debitCreditMemoSearchResultVOList =  new ArrayList<DebitCreditMemoSearchResultVO>();
		if(isSearchRequired){
			try {
				updateSearchCriteria();	
				logger.info("-- Debit Credit Memo search start with criteria"+searchCriteria);
				
				debitCreditMemoSearchResultVOList = debitCreditMemoService.getDebitCreditMemoTransactions(searchCriteria , page, sort);
				int count  = debitCreditMemoService.getDebitCreditMemoTransactionsCount(searchCriteria);
				selectionIndex =  (page.getPageSize() *  page.getPageNumber())+(selectionIndex % page.getPageSize());
				debitCreditMemoLazyList.setRowCount(count);
				debitCreditMemoLazyList.setWrappedData(debitCreditMemoSearchResultVOList);
				adjustDataTableAfterSearch(debitCreditMemoSearchResultVOList);				
				
				logger.info("-- Debit Credit Memo search end with criteria"+searchCriteria);
			} catch (Exception ex) {
				super.addErrorMessage("generic.error", ex.getMessage());
			}			
		}

		return debitCreditMemoSearchResultVOList;
	}
	
	public void setClientNameOrCode() {
		//Empty the SearchCriteria before each search
		searchCriteria.setClientAccountCode("");
		searchCriteria.setClientAccountName("");
		if(MALUtilities.isNumber(this.client)){
			searchCriteria.setClientAccountCode(this.client);
			searchCriteria.setClientAccountType(ACCOUNT_TYPE_CUSTOMER);
			searchCriteria.setClientCid(CorporateEntity.MAL.getCorpId());
		}
		else if(!MALUtilities.isNumber(this.client) && !MALUtilities.isEmptyString(this.client)){
			searchCriteria.setClientAccountName(this.client);
			searchCriteria.setClientAccountType(ACCOUNT_TYPE_CUSTOMER);
			searchCriteria.setClientCid(CorporateEntity.MAL.getCorpId());
		}
	}
	
	public void getClientNameOrCode(){
		if(!MALUtilities.isEmpty(searchCriteria.getClientAccountCode())){
			this.client = searchCriteria.getClientAccountCode();
		}
		else{
			this.client = searchCriteria.getClientAccountName();
		}
	}
		
    public List<DebitCreditMemoSearchResultVO> getSelectedSearchVOs() {
		return selectedSearchVOs;
	}

	public void setSelectedSearchVOs(List<DebitCreditMemoSearchResultVO> selectedRecords) {
		this.selectedSearchVOs = selectedRecords;
		if(selectedSearchVOs == null){
			this.selectedSearchVOs = new ArrayList<DebitCreditMemoSearchResultVO>();
		}	
		adjustBtnState();
	}
	
	  private  void adjustBtnState() {
		  
		if( this.selectedSearchVOs != null){
			
			if(this.selectedSearchVOs.size() == 0 ){
				
				this.enableEditBtn = false;
				this.enableApproveBtn = false;
				this.enableAllocateBtn = false;
				this.enableProcessBtn = false;
				
			}else{	
				
				this.enableEditBtn = true;
				if (hasAprrovePermission || hasProcessPermission){
					this.enableApproveBtn = true;
				}
				this.enableAllocateBtn = hasProcessPermission;
				this.enableProcessBtn = hasProcessPermission;
				
				for (DebitCreditMemoSearchResultVO debitCreditResultVO : this.selectedSearchVOs) {
					
					if(!debitCreditResultVO.getStatusCode().equalsIgnoreCase(DebitCreditStatusEnum.STATUS_SUBMITTED.getCode())){
						this.enableApproveBtn = false;
					}
					
					if(!debitCreditResultVO.getStatusCode().equalsIgnoreCase(DebitCreditStatusEnum.STATUS_APPROVED.getCode())){
						this.enableProcessBtn = false;
					}					
					
					if(!debitCreditResultVO.getStatusCode().equalsIgnoreCase(DebitCreditStatusEnum.STATUS_PROCESSED.getCode())){
						this.enableAllocateBtn = false;
					}
				}
				
				if(this.selectedSearchVOs.size() > 1 ){
					this.enableEditBtn = false;	
				}
			}
		}
		
		this.enableUploadFileBtn = hasProcessPermission;
	}

	
	
	public void addDebitCreditMemo(){		
		navigateToDebitCreditAddEdit(true);		
	}
	
	public void viewEditDebitCreditMemo(){
		if(selectedSearchVOs.size() == 1){
			navigateToDebitCreditAddEdit(false);		
		}
	}
	
	public void approveDebitCreditMemos(){
		Map<String,Long> invalidRecords = new HashMap<String,Long>();
		if(selectedSearchVOs.size() >= 1){
			 List<Long> dcNoList = new ArrayList<Long>();
			 for (DebitCreditMemoSearchResultVO selectedVO : selectedSearchVOs) {
				 dcNoList.add(selectedVO.getDcNumber()); 
			}
			try {
				invalidRecords = debitCreditMemoService.approveDebitCreditMemoTransactionList(dcNoList, getLoggedInUser().getEmployeeNo());	
				if(invalidRecords == null){
					super.addSuccessMessage("custom.message","Selected record(s) has been successfully approved");
					this.enableApproveBtn = false;
				}else{
					super.addSimplErrorMessage("DC #"+invalidRecords.get(INVALID_DC_ID)+" cannot be approved. The Approver cannot be the same as the submitter.");
					super.addErrorMessage("custom.message","Records approved successfully: " + invalidRecords.get(RECORDS_PROCESSED) + " of " + selectedSearchVOs.size());
					ListIterator<DebitCreditMemoSearchResultVO> voItr = selectedSearchVOs.listIterator();
					while(voItr.hasNext()){
						if(!voItr.next().getDcNumber().equals(invalidRecords.get(INVALID_DC_ID))){
							voItr.remove();
						}else{
							break;
						}
					}
				}
			 } catch (Exception e) {
				 logger.error(e, "Error in approve action:");
				 super.addErrorMessage("generic.error.occured.while"," approving debit credit memo transactions");
			}
		}
	}
	
	public void allocateDebitCreditMemos(){
		if(selectedSearchVOs.size() >= 1){
			 List<Long> dcNoList = new ArrayList<Long>();
			 for (DebitCreditMemoSearchResultVO selectedVO : selectedSearchVOs) {
				 dcNoList.add(selectedVO.getDcNumber());
			}
			try {
				debitCreditMemoService.allocateDebitCreditMemoTransactionList(dcNoList, getLoggedInUser().getEmployeeNo());			
				super.addSuccessMessage("custom.message","Selected record(s) has been successfully allocated");
				this.enableAllocateBtn = false;
			 } catch (Exception e) {
				 logger.error(e, "Error in allocate action:");
				 super.addErrorMessage("generic.error.occured.while"," allocating debit credit memo transactions"); 	
			}
		}
	}
	
	public void processDebitCreditTransactions(){//process 1 at a time and stop at first failure
		if(selectedSearchVOs.size() >= 1){
			DebitCreditMemoTransaction dcmt= null; 
			recordsProcessed = 0;
			try {
				for (DebitCreditMemoSearchResultVO debitCreditResultVO : this.selectedSearchVOs) {
					dcmt = debitCreditMemoService.getDebitCreditMemoTransaction(debitCreditResultVO.getDcNumber());
	    			debitCreditMemoService.processDebitCreditMemo(getLoggedInUser().getEmployeeNo(), dcmt);
	    			recordsProcessed = recordsProcessed + 1;
				}
			} catch (Exception e) {
				logger.error(e, "Unable to create doc/docl entry for dc no: "+ dcmt.getDcNo());
				super.addErrorMessage("custom.message","Process Error for DC# "+ dcmt.getDcNo() + " - " + e.getMessage()); 
				super.addErrorMessage("custom.message","Records processed successfully: " + recordsProcessed + " of " + selectedSearchVOs.size());
			}	
			if (recordsProcessed == selectedSearchVOs.size()){
				super.addSuccessMessage("custom.message","Selected record(s) has been successfully processed.");
			}
			this.enableProcessBtn = false;
		}	
	}	
	
    private void navigateToDebitCreditAddEdit(boolean isAddMode){
    	saveRestoreStateValues(getCurrentPageRestoreStateValuesMap());
    	Map<String, Object> nextPageValues = new HashMap<String, Object>();
    	if(isAddMode){
    		nextPageValues.put(ViewConstants.VIEW_MODE, ViewConstants.VIEW_MODE_ADD);
    	}else{
    		nextPageValues.put(ViewConstants.VIEW_MODE, ViewConstants.VIEW_MODE_EDIT);//view/edit
    	}
   	   if(selectedSearchVOs != null && selectedSearchVOs.size() > 0) {
    		nextPageValues.put(ViewConstants.VIEW_PARAM_DC_MEMO_DC_NO, selectedSearchVOs.get(0).getDcNumber());
    	}
    	saveNextPageInitStateValues(nextPageValues);
		forwardToURL(ViewConstants.DEBIT_CREDIT_MEMO_ADD_EDIT);
    }

    private Map<String, Object> getCurrentPageRestoreStateValuesMap() {
		
		Map<String, Object> restoreStateValues = new HashMap<String, Object>();
		restoreStateValues.put(RESTORE_PARAM_CRITERIA , searchCriteria);
		restoreStateValues.put(RESTORE_PARAM_SEARCH_REQUIRED, isSearchRequired);
		
		DataTable dt = (DataTable) getComponent(DATA_TABLE_UI_ID);
		if (dt != null) {
			ValueExpression ve = dt.getValueExpression("sortBy");
			restoreStateValues.put(ViewConstants.DT_DEFAULT_SORT_VE, ve);
			restoreStateValues.put(ViewConstants.DT_SELECTD_PAGE_START_INDEX, dt.getFirst());
			restoreStateValues.put(ViewConstants.DT_DEFAULT_SORT_ORDER, dt.getSortOrder());
			if(selectedSearchVOs != null && selectedSearchVOs.size() > 0 ){
				restoreStateValues.put(ViewConstants.DT_SELECTED_ITEM, selectedSearchVOs.get(0));
			}
		}
		restoreStateValues.put("SCROLL_POSITION", scrollPosition);
		return restoreStateValues;
    }	
    
    public String cancel(){     
        return super.cancelPage();  
       }


	private void adjustDataTableAfterSearch(List<DebitCreditMemoSearchResultVO> debitCreditMemoSearchResultVOList){
		adjustDataTableHeight(debitCreditMemoSearchResultVOList);
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

	public void clearDialog() {
		setDocumentFileVO(null);
		this.enableDialogUploadBtn = false;
	}
	
	public void fileUploadListener(FileUploadEvent event) {
		UploadedFile file = event.getFile();	
		String fileName  = file.getFileName();
		
		logger.info("getFileName--"+fileName);
		
		String fileExtn = null;
		if (fileName.lastIndexOf(".") > 0) {
			fileExtn = fileName.substring(fileName.lastIndexOf(".") + 1);
		}
		
		if (fileExtn != null && !fileExtn.equalsIgnoreCase("csv") ) {			
			addErrorMessageSummary("custom.message", "File upload failed. File must be in csv format.");
			return;
		}	
		if (fileName.lastIndexOf("\\") > 0) {
			fileName = fileName.substring(fileName.lastIndexOf("\\") + 1);
		}

		setDocumentFileVO(new DocumentFileVO());
		documentFileVO.setFileName(fileName);
		documentFileVO.setFileExt(fileExtn);
		documentFileVO.setFileData(file.getContents());
		documentFileVO.setFileType(fileExtn);
	
		setDocumentFileVO(documentFileVO);

		this.enableDialogUploadBtn = true;
	}
	 
    public String copyFile() {
     	try {
        	DocumentFileVO documentFileVO = getDocumentFileVO();
    		debitCreditMemoService.copyDebitCreditUploadFile(documentFileVO, debitCreditUploadDir);
    		
	        FacesMessage msg = new FacesMessage("File " + documentFileVO.getFileName() + " has uploaded successfully and is awaiting processing.");  
	        FacesContext.getCurrentInstance().addMessage(null, msg);   
		} catch (Exception e) {
			logger.error(e, "Could not upload file: " + documentFileVO.getFileName() + e.getMessage());
			super.addErrorMessage("custom.message","File upload failed."); 
		}
		return null;    	
    }
	

	public void  deleteUploadedFile(DocumentFileVO documentFileVO){
		setDocumentFileVO(null);
		this.enableDialogUploadBtn = false;
	}	
	
	public void preProcessDownloadXLS(Object document) {
		SimpleDateFormat format = new SimpleDateFormat(MALUtilities.DATE_PATTERN);
		downloadableRowsData.clear();
		downloadableColumns.clear();
		downloadableColumns.add("Type");
		downloadableColumns.add("Status");
		downloadableColumns.add("Ticket No.");
		downloadableColumns.add("DC No.");
		downloadableColumns.add("Reason");
		downloadableColumns.add("Line Description");
		downloadableColumns.add("Submitted By");
		downloadableColumns.add("Submitted Date");
		downloadableColumns.add("Approved By");
		downloadableColumns.add("Approved Date");
		downloadableColumns.add("Processed By");
		downloadableColumns.add("Processed Date");
		downloadableColumns.add("Allocated By");
		downloadableColumns.add("Allocated Date");
		downloadableColumns.add("Alerts");
		downloadableColumns.add("Client No.");
		downloadableColumns.add("Client Name");
		downloadableColumns.add("Unit No.");
		downloadableColumns.add("Unit Belongs to Client");
		downloadableColumns.add("Analysis Category");
		downloadableColumns.add("Analysis Category Description");
		downloadableColumns.add("Analysis Code");
		downloadableColumns.add("Analysis Code Description");
		downloadableColumns.add("Net Amount");
		downloadableColumns.add("Tax Amount");
		downloadableColumns.add("Total Amount");
		downloadableColumns.add("Invoice Note");
		downloadableColumns.add("Transaction Date");
		downloadableColumns.add("Invoice No.");
		downloadableColumns.add("Rent Applicable Date");
		downloadableColumns.add("Requested Approver");
		downloadableColumns.add("GL Code");
		downloadableColumns.add("File Name");
		downloadableColumns.add("State");
		
		setClientNameOrCode();
		updateSearchCriteria();
		exportList = debitCreditMemoService.getDebitCreditMemoTransactions(searchCriteria , null, sort);

		for (DebitCreditMemoSearchResultVO DebitCreditMemoSearchResultVO : exportList) {
			Map<String, Object> row = new HashMap<String, Object>();
			row.put("Type",DebitCreditMemoSearchResultVO.getType());
			row.put("Status",DebitCreditMemoSearchResultVO.getStatus());
			row.put("Ticket No.",DebitCreditMemoSearchResultVO.getTicketNo());
			row.put("DC No.",DebitCreditMemoSearchResultVO.getDcNumber());
			row.put("Reason",DebitCreditMemoSearchResultVO.getReason());
			row.put("Line Description",DebitCreditMemoSearchResultVO.getLineDescription());
			row.put("Submitted By",DebitCreditMemoSearchResultVO.getSubmitter());
			row.put("Submitted Date",DebitCreditMemoSearchResultVO.getSubmittedDate()!= null ? format.format(DebitCreditMemoSearchResultVO.getSubmittedDate()) : null);
			row.put("Approved By",DebitCreditMemoSearchResultVO.getApprover());
			row.put("Approved Date",DebitCreditMemoSearchResultVO.getAllocatedDate() != null ? format.format(DebitCreditMemoSearchResultVO.getAllocatedDate()) : null);
			row.put("Processed By",DebitCreditMemoSearchResultVO.getProcessor());
			row.put("Processed Date",DebitCreditMemoSearchResultVO.getProcessedDate() != null ? format.format(DebitCreditMemoSearchResultVO.getProcessedDate()) : null);
			row.put("Allocated By",DebitCreditMemoSearchResultVO.getAllocator());
			row.put("Allocated Date",DebitCreditMemoSearchResultVO.getAllocatedDate() != null ? format.format(DebitCreditMemoSearchResultVO.getAllocatedDate()) : null);
			row.put("Alerts",DebitCreditMemoSearchResultVO.isWarningInd()?'Y':'N');
			row.put("Client No.",DebitCreditMemoSearchResultVO.getClientCode());
			row.put("Client Name",DebitCreditMemoSearchResultVO.getClientName());
			row.put("Unit No.",DebitCreditMemoSearchResultVO.getUnitNo());
			row.put("Unit Belongs to Client",DebitCreditMemoSearchResultVO.isClientUnit());
			row.put("Analysis Category",DebitCreditMemoSearchResultVO.getCategoryCode());
			row.put("Analysis Category Description",DebitCreditMemoSearchResultVO.getCategoryDesc());
			row.put("Analysis Code",DebitCreditMemoSearchResultVO.getAnalysisCode());
			row.put("Analysis Code Description",DebitCreditMemoSearchResultVO.getAnalysisDesc());
			row.put("Net Amount",DebitCreditMemoSearchResultVO.getNetAmount()!= null ? DebitCreditMemoSearchResultVO.getNetAmount().setScale(2, BigDecimal.ROUND_HALF_UP): null);
			row.put("Tax Amount",DebitCreditMemoSearchResultVO.getTaxAmount()!= null ? DebitCreditMemoSearchResultVO.getTaxAmount().setScale(2, BigDecimal.ROUND_HALF_UP): null);
			row.put("Total Amount",DebitCreditMemoSearchResultVO.getTotalAmount() != null ? DebitCreditMemoSearchResultVO.getTotalAmount().setScale(2, BigDecimal.ROUND_HALF_UP): null);
			row.put("Invoice Note",DebitCreditMemoSearchResultVO.getInvoiceNote());
			row.put("Transaction Date",DebitCreditMemoSearchResultVO.getTransactionDate() != null ? format.format(DebitCreditMemoSearchResultVO.getTransactionDate()) : null);
			row.put("Invoice No.",DebitCreditMemoSearchResultVO.getInvoiceNo());
			row.put("Rent Applicable Date",DebitCreditMemoSearchResultVO.getRentApplicableDate()!= null ? format.format(DebitCreditMemoSearchResultVO.getRentApplicableDate()) : null);
			row.put("Requested Approver",DebitCreditMemoSearchResultVO.getSelectedApprover());
			row.put("GL Code",DebitCreditMemoSearchResultVO.getGlCode());
			row.put("File Name",DebitCreditMemoSearchResultVO.getSource());
			row.put("State",DebitCreditMemoSearchResultVO.getDriverAddressState());
			downloadableRowsData.add(row);
		}
	}

	public void postProcessDownloadXLS(Object document) {
		
		HSSFWorkbook wb = (HSSFWorkbook) document;
		HSSFSheet sheet = wb.getSheetAt(0);
		sheet.shiftRows(0, sheet.getLastRowNum(), 1);
		Double zeroAmt = new Double(0.00);
		
		Row titleRow = sheet.createRow(0);
		titleRow.setHeightInPoints(45);
		Cell titleCell1 = titleRow.createCell(0);
		titleCell1.setCellValue("Debit Credit Memos");
		titleCell1.setCellStyle(XLSUtil.createStyle(wb, XLSUtil.TITLE));
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, downloadableColumns.size() - 1));
		Font boldFont = wb.createFont();
		boldFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		
		HSSFRow row;
		HSSFCell cell;
		CellStyle cellStyleTitle = XLSUtil.createStyle(wb, XLSUtil.TITLE);
		CellStyle cellStyleHeader = XLSUtil.createStyle(wb, XLSUtil.HEADER);
		CellStyle cellStyleCell = XLSUtil.createStyle(wb, XLSUtil.CELL);
		CellStyle cellStyleDateCell = XLSUtil.createStyle(wb, XLSUtil.CELL_DATE);
		CellStyle cellStyleAmountCell = XLSUtil.createStyle(wb, XLSUtil.CELL_AMOUNT);
		
		for(int i = 0; i <= sheet.getLastRowNum(); i++) {
			boolean autoResize = false;
			row = sheet.getRow(i);
			if(i == 0) {
				autoResize = false;
			} else if(i == 1) {
				autoResize = true;
			} else {
				autoResize = false;
			}
			
			/* we had to re-enter the values in amount column because when we enter value in amount column 
			** regardless the data type we enter in the column it start treating it as text. 
			** So when try to set the format of the column in post method it gives us illegalStateException 
			** stating that text value cannot be converted into Numeric. So to overcome this issue we are 
			** reentering the value and setting it type and format at the same time.*/
			
			for(int j = 0; j < row.getPhysicalNumberOfCells(); j++) {
				cell = row.getCell(j);
				if(i==0){
					cell.setCellStyle(cellStyleTitle);
				}else if(i==1){
					cell.setCellStyle(cellStyleHeader);
				}else if(MALUtilities.isValidDate(cell.getStringCellValue())){
					cell.setCellStyle(cellStyleDateCell);
				}else{
					if(j==23 || j==24 || j==25){
						if(MALUtilities.isEmptyString(cell.getStringCellValue()))
							cell.setCellValue(zeroAmt);
						else
							cell.setCellValue(Double.parseDouble(cell.getStringCellValue()));
						cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						cell.setCellStyle(cellStyleAmountCell);
					}else{
						cell.setCellStyle(cellStyleCell);
					}
				}
				if(autoResize) {
					sheet.autoSizeColumn(j);
				}
			}
		}

		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("path", "/");
		getFaceExternalContext().addResponseCookie(EXCEL_DL_COOKIE, "true", properties);
	}
	
	public DebitCreditMemoSearchCriteriaVO getSearchCriteria() {
		return searchCriteria;
	}

	public void setSearchCriteria(DebitCreditMemoSearchCriteriaVO searchCriteria) {
		this.searchCriteria = searchCriteria;
	}

	public List<DebitCreditStatusCode> getDebitCreditStatusCodes() {
		return debitCreditStatusCodes;
	}

	public void setDebitCreditStatusCodes(List<DebitCreditStatusCode> debitCreditStatusCodes) {
		this.debitCreditStatusCodes = debitCreditStatusCodes;
	}
	
	public List<PersonnelBase> getUserNameList() {
		return userNameList;
	}

	public void setUserNameList(List<PersonnelBase> userNameList) {
		this.userNameList = userNameList;
	}

	public String getSelectedSubmitter() {
		return selectedSubmitter;
	}

	public void setSelectedSubmitter(String selectedSubmitter) {
		this.selectedSubmitter = selectedSubmitter;
	}

	public String getSelectedProcessors() {
		return selectedProcessors;
	}

	public void setSelectedProcessors(String selectedProcessors) {
		this.selectedProcessors = selectedProcessors;
	}

	public List<AnalysisCategory> getAnalysisCategories() {
		return analysisCategories;
	}

	public void setAnalysisCategories(List<AnalysisCategory> analysisCategories) {
		this.analysisCategories = analysisCategories;
	}	

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}

	public int getSelectionIndex() {
		return selectionIndex;
	}

	public void setSelectionIndex(int selectionIndex) {
		this.selectionIndex = selectionIndex;
	}

	public LazyDataModel<DebitCreditMemoSearchResultVO> getDebitCreditMemoLazyList() {
		return debitCreditMemoLazyList;
	}

	public void setDebitCreditMemoLazyList(LazyDataModel<DebitCreditMemoSearchResultVO> debitCreditMemoLazyList) {
		this.debitCreditMemoLazyList = debitCreditMemoLazyList;
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
	public boolean canEnableEditBtn() {
		return enableEditBtn;
	}

	public void setEnableEditBtn(boolean enableEditBtn) {
		this.enableEditBtn = enableEditBtn; 
	}

	public boolean canEnableApproveBtn() {
		return enableApproveBtn;
	}

	public void setEnableApproveBtn(boolean enableApproveBtn) {
		this.enableApproveBtn = enableApproveBtn;
	}

	public boolean canEnableAllocateBtn() {
		return enableAllocateBtn;
	}

	public void setEnableAllocateBtn(boolean enableAllocateBtn) {
		this.enableAllocateBtn = enableAllocateBtn;
	}

	public boolean isEnableUploadFileBtn() {
		return enableUploadFileBtn;
	}

	public void setEnableUploadFileBtn(boolean enableUploadFileBtn) {
		this.enableUploadFileBtn = enableUploadFileBtn;
	}

	public DocumentFileVO getDocumentFileVO() {
		return documentFileVO;
	}

	public void setDocumentFileVO(DocumentFileVO documentFileVO) {
		this.documentFileVO = documentFileVO;
	}

	public boolean isEnableDialogUploadBtn() {
		return enableDialogUploadBtn;
	}

	public void setEnableDialogUploadBtn(boolean enableDialogUploadBtn) {
		this.enableDialogUploadBtn = enableDialogUploadBtn;
	}

	public boolean isEnableProcessBtn() {
		return enableProcessBtn;
	}

	public void setEnableProcessBtn(boolean enableProcessBtn) {
		this.enableProcessBtn = enableProcessBtn;
	}
	
	public Long getScrollPosition() {
		return scrollPosition;
	}

	public void setScrollPosition(Long scrollPosition) {
		this.scrollPosition = scrollPosition;
	} 	

	public String getWarningStyle() {
		return warningStyle;
	}

	public void setWarningStyle(String warningStyle) {
		this.warningStyle = warningStyle;
	}

	public List<DebitCreditMemoSearchResultVO> getExportList() {
		return exportList;
	}

	public void setExportList(List<DebitCreditMemoSearchResultVO> exportList) {
		this.exportList = exportList;
	}

	public List<Map<String, Object>> getDownloadableRowsData() {
		return downloadableRowsData;
	}

	public void setDownloadableRowsData(List<Map<String, Object>> downloadableRowsData) {
		this.downloadableRowsData = downloadableRowsData;
	}

	public List<String> getDownloadableColumns() {
		return downloadableColumns;
	}

	public void setDownloadableColumns(List<String> downloadableColumns) {
		this.downloadableColumns = downloadableColumns;
	}

}
