package com.mikealbert.vision.view.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.el.ValueExpression;

import org.primefaces.component.api.UIColumn;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mikealbert.data.vo.DebitCreditTransactionVO;
import com.mikealbert.service.DebitCreditMemoService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.view.ViewConstants;

@Component
@Scope("view")
public class DebitCreditMemoErrorSearchBean extends StatefulBaseBean {
	private static final long serialVersionUID = 1L;
	
	@Resource DebitCreditMemoService debitCreditMemoService;
	
	@Value("${url.debit.credit.processing}")
	private String debitCreditProcessingBaseURL;	
	
	private List<SortMeta> preSort = new ArrayList<SortMeta>();	
	private static final String DATA_TABLE_UI_ID = "DT_UI_ID";
	private static final String SUBMITTED_DATE = "SUBMITTED_DATE";
	private static final String SUBMITTER = "SUBMITTER";		
	private List<DebitCreditTransactionVO> debitCreditTransactionVoErrorList  = null;
	private List<DebitCreditTransactionVO> selectedSearchVOs  = null;
	private List<DebitCreditTransactionVO> rows = new ArrayList<DebitCreditTransactionVO>();
	private String headerMessage;	
	private Integer	totalRecords = 0;
	private boolean enableEditBtn = false;	
	private boolean enableDeleteBtn = false;	
	private String dataTableMessage;
	private boolean hasProcessPermission;	
	private boolean hasApprovePermission;	
	private Long scrollPosition;
	
	@PostConstruct
	public void init() {
		setSelectedSearchVOs(null);
		initializeDataTable(450, 850, new int[] {6,6,4,4,4,7,7,7,8,7,7,6,8,6,13}).setHeight(0);
		openPage();
		try {
			loadData();
			adjustDataTableHeight(getDebitCreditTransactionVoErrorList());
			loadUserPermission();
			adjustBtnState();			
			sortDataMultipleSort();
		} catch (Exception e) {
			logger.error(e);
			super.addErrorMessage("generic.error.occured.while", " fetching errors.");
		}
	}
	
	private void loadData() {

		try {	
			List<DebitCreditTransactionVO> unfilteredRows = debitCreditMemoService.requestDebitCreditUploadErrors(debitCreditProcessingBaseURL + "/businessErrors");			
			rows.clear();
			
			if (hasPermission("aprroveDebitCreditMemo") || hasPermission("processDebitCreditMemo")){
				rows.addAll(unfilteredRows);
			} else {
				for (DebitCreditTransactionVO dct : unfilteredRows){ 
					if (dct.getSubmitter() != null && getLoggedInUser().getEmployeeNo().equals(dct.getSubmitter())) {
						rows.add(dct);
					}
				}
				setHeaderMessage("(Displaying only debit/credit memos submitted by logged in user.)");
			}			
			
			setDebitCreditTransactionVoErrorList(rows);
			setTotalRecords(rows.size());
		} catch (Exception e) {
			logger.error(e);
			super.addErrorMessage("generic.error.occured.while", " building screen.");
		}
	}
	
    private void loadUserPermission() {
        this.hasProcessPermission =  hasPermission("processDebitCreditMemo");
        this.hasApprovePermission = hasPermission("aprroveDebitCreditMemo"); 
	}		
	
	public void deleteDebitCreditMemos(){
		List<String> jmsIdList = new ArrayList<String>();
		if(selectedSearchVOs.size() >= 1){
			for (DebitCreditTransactionVO selectedVO : selectedSearchVOs) {
				jmsIdList.add(selectedVO.getJmsMessageId());
			}
		}
		
		try {
			debitCreditMemoService.removeDebitCreditErrorsFromQueue(jmsIdList,debitCreditProcessingBaseURL + "/businessErrors");
			super.addSuccessMessage("custom.message","Selected record(s) has been successfully deleted");
			loadData();
			adjustDataTableHeight(getDebitCreditTransactionVoErrorList());
			loadUserPermission();
			setSelectedSearchVOs(null);
			adjustBtnState();			
			sortDataMultipleSort();
		} catch (Exception e) {
			super.addErrorMessage("generic.error", e.getMessage());
		}
	}
	
	private  void adjustBtnState() {
		this.enableEditBtn = false;
		this.enableDeleteBtn = false;

		if( this.selectedSearchVOs != null){
			if(this.selectedSearchVOs.size() == 0 ){
				this.enableEditBtn = false;
				this.enableDeleteBtn = false;
			} else if (this.selectedSearchVOs.size() > 1 ){	
				this.enableDeleteBtn = true;
			} else { 
				this.enableEditBtn = true;
				this.enableDeleteBtn = true;
			}
		}		
	}	
	
	private void adjustDataTableHeight(List<DebitCreditTransactionVO> list){
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
	
	private void sortDataMultipleSort() {
		
		if(preSort != null) {
			preSort.clear();
		}
		
		SortMeta sortMeta = new SortMeta();
		UIColumn column = null;

		column = (UIColumn) getComponent(DATA_TABLE_UI_ID+":"+SUBMITTED_DATE);
		sortMeta.setSortField("SUBMITTED_DATE");
		sortMeta.setSortBy(column);
		sortMeta.setSortOrder(SortOrder.DESCENDING);
		preSort.add(sortMeta);
		
		sortMeta = new SortMeta();
		column = (UIColumn) getComponent(DATA_TABLE_UI_ID+":"+SUBMITTER);
		sortMeta.setSortBy(column);
		sortMeta.setSortField("SUBMITTER");
		sortMeta.setSortOrder(SortOrder.ASCENDING);
		preSort.add(sortMeta);
		
	}	
  
	@Override
	protected void loadNewPage() {
		thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_ERROR_SEARCH_DEBIT_CREDIT_MEMO);
		thisPage.setPageUrl(ViewConstants.ERROR_SEARCH_DEBIT_CREDIT_MEMO);
	}

	@Override
	protected void restoreOldPage() {		 
		DataTable dt = (DataTable) getComponent(DATA_TABLE_UI_ID);		
		Map<String,Object> restoreMap = thisPage.getRestoreStateValues();
		this.scrollPosition = (Long)thisPage.getRestoreStateValues().get("SCROLL_POSITION");
	
		if (!MALUtilities.isEmpty(dt)) {
			dt.setFirst((Integer)restoreMap.get(ViewConstants.DT_SELECTD_PAGE_START_INDEX));
			dt.setSortOrder((String)restoreMap.get(ViewConstants.DT_DEFAULT_SORT_ORDER));
			dt.setValueExpression("sortBy", (ValueExpression)restoreMap.get(ViewConstants.DT_DEFAULT_SORT_VE));	
			DebitCreditTransactionVO selectedDebitCreditTransactionVO = (DebitCreditTransactionVO)restoreMap.get(ViewConstants.DT_SELECTED_ITEM);	
			if(selectedDebitCreditTransactionVO != null){
				setSelectedSearchVOs(Arrays.asList(selectedDebitCreditTransactionVO));
			}			
		}
		restoreMap.clear();
	}	

	public void viewEditDebitCreditMemo(){
		if(selectedSearchVOs.size() == 1){
			navigateToDebitCreditAddEdit(false);		
		}
	}

    private void navigateToDebitCreditAddEdit(boolean isAddMode){
    	saveRestoreStateValues(getCurrentPageRestoreStateValuesMap());
    	Map<String, Object> nextPageValues = new HashMap<String, Object>();
    	nextPageValues.put(ViewConstants.VIEW_MODE, ViewConstants.VIEW_MODE_EDIT);//view/edit
   	   if(selectedSearchVOs != null && selectedSearchVOs.size() > 0) {
   		   nextPageValues.put(ViewConstants.VIEW_PARAM_DC_MEMO_ERROR_JMS_ID, selectedSearchVOs.get(0).getJmsMessageId());
    	}
    	saveNextPageInitStateValues(nextPageValues);
		forwardToURL(ViewConstants.DEBIT_CREDIT_MEMO_ADD_EDIT);
    }	
    
    private Map<String, Object> getCurrentPageRestoreStateValuesMap() {
	
		Map<String, Object> restoreStateValues = new HashMap<String, Object>();
		
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

	public List<DebitCreditTransactionVO> getDebitCreditTransactionVoErrorList() {
		return debitCreditTransactionVoErrorList;
	}

	public void setDebitCreditTransactionVoErrorList(
			List<DebitCreditTransactionVO> debitCreditTransactionVoErrorList) {
		this.debitCreditTransactionVoErrorList = debitCreditTransactionVoErrorList;
	}

	public String getDataTableMessage() {
		return dataTableMessage;
	}

	public void setDataTableMessage(String dataTableMessage) {
		this.dataTableMessage = dataTableMessage;
	}

	public List<DebitCreditTransactionVO> getSelectedSearchVOs() {
		return selectedSearchVOs;
	}

	public void setSelectedSearchVOs(List<DebitCreditTransactionVO> selectedRecords) {
		this.selectedSearchVOs = selectedRecords;
		if(selectedSearchVOs == null){
			this.selectedSearchVOs = new ArrayList<DebitCreditTransactionVO>();
		}
		adjustBtnState();
	}

	public boolean isEnableEditBtn() {
		return enableEditBtn;
	}

	public void setEnableEditBtn(boolean enableEditBtn) {
		this.enableEditBtn = enableEditBtn;
	}

	public boolean isEnableDeleteBtn() {
		return enableDeleteBtn;
	}

	public void setEnableDeleteBtn(boolean enableDeleteBtn) {
		this.enableDeleteBtn = enableDeleteBtn;
	}

	public List<SortMeta> getPreSort() {
		return preSort;
	}

	public void setPreSort(List<SortMeta> preSort) {
		this.preSort = preSort;
	}

	public Integer getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(Integer totalRecords) {
		this.totalRecords = totalRecords;
	}

	public Long getScrollPosition() {
		return scrollPosition;
	}

	public void setScrollPosition(Long scrollPosition) {
		this.scrollPosition = scrollPosition;
	}

	public String getHeaderMessage() {
		return headerMessage;
	}

	public void setHeaderMessage(String headerMessage) {
		this.headerMessage = headerMessage;
	}  
	
}
