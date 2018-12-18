package com.mikealbert.vision.view.bean;


import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.primefaces.component.api.UIColumn;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.vo.DebitCreditTransactionVO;
import com.mikealbert.service.DebitCreditMemoService;
import com.mikealbert.vision.view.ViewConstants;

@Component
@Scope("view")
public class DebitCreditMemoFormatErrorsBean extends StatefulBaseBean {
	private static final long serialVersionUID = -8806821952042784558L;
	
	@Resource DebitCreditMemoService debitCreditMemoService;
	
	@Value("${url.debit.credit.processing}")
	private String debitCreditProcessingBaseURL;	
	
	MalLogger logger = MalLoggerFactory.getLogger(this.getClass());

	private String messageId;
	private List<DebitCreditTransactionVO> rows = new ArrayList<DebitCreditTransactionVO>();
	private DebitCreditTransactionVO selectedRow;
	private int rowCount;
	private boolean hasGeneralEditPermission = false;	
	private boolean hasProcessorPermission = false;
	private String dataTableMessage;
	private List<SortMeta> preSort = new ArrayList<SortMeta>();	
	private String headerMessage;
	private boolean enableReprocessBtn = false;

	private static final String DATA_TABLE_UI_ID = "formatErrorsDataTableId";
	private static final String SUBMITTED_DATE = "SUBMITTED_DATE";
	private static final String SUBMITTER = "SUBMITTER";
	
	@PostConstruct
	public void init() {
		super.openPage();
		initializeDataTable(460, 850, new int[] {1}).setHeight(0);
		loadRows();
		adjustDataTableHeight(getRows());
		sortDataMultipleSort();
		loadUserPermission();
		adjustBtnState();		
	}

	public String cancel() {
		return super.cancelPage();
	}

	private void loadRows() {
		List<DebitCreditTransactionVO> unfilteredRows = debitCreditMemoService.requestDebitCreditUploadErrors(debitCreditProcessingBaseURL + "/formatErrors");
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
	
		rowCount = rows.size();
	}
	
	private void loadUserPermission() {
		this.setHasGeneralEditPermission(true);		
	}

	public void delete() {
		if(selectedRow != null) {
			if(deleteRow(selectedRow)) {
				addSuccessMessage("custom.message", "Selected format error was deleted");
				loadRows();
				loadUserPermission();
				adjustBtnState();				
			}
		} else {
			addSimplErrorMessage("No row was selected for delete");
		}
	}
	
	private boolean deleteRow(DebitCreditTransactionVO error) {
		boolean isSuccessful = false;
		List<String> jmsIdList = new ArrayList<String>();
		jmsIdList.add(error.getJmsMessageId());
//		jmsIdList.add("ID:etltest-54669-1491585888430-5:7:1:1:1");    easy way to delete bad rows from bugs
		try {
			debitCreditMemoService.removeDebitCreditErrorsFromQueue(jmsIdList, debitCreditProcessingBaseURL  + "/formatErrors");			
			isSuccessful = true;
		} catch (Exception e) {
			super.addErrorMessage("generic.error", e.getMessage());
		}			
		return isSuccessful;
	}
	
	private  void adjustBtnState() {
		this.enableReprocessBtn = false;
		if (rowCount > 0 ){
			this.enableReprocessBtn = true;
		}
	}
	
	public String reprocess() {
		boolean isSuccessful = true;
		for(DebitCreditTransactionVO row : rows) {
			if(reprocessRow(row)) {
				if(!deleteRow(row)) {
					isSuccessful = false;
					addSimplErrorMessage("Problem reprocessing rows.  Contact Help Desk." );
					logger.info("Problem deleting format error with JMS Id of " + row.getJmsMessageId());
				}
			} else {
				addSimplErrorMessage("Problem reprocessing rows.  Contact Help Desk." );
				logger.info("Problem adding format error to reprocess queue with JMS Id of " + row.getJmsMessageId());
				isSuccessful = false;
			}
			if(!isSuccessful) {
				break;
			}
		}		
		if(isSuccessful) {
			addSuccessMessage("custom.message", "Format Errors have been submitted for reprocessing");
			return cancel();
		} else {
			return null;
		}
		
	}
	
	
	
	private boolean reprocessRow(DebitCreditTransactionVO error) {
		boolean isSuccessful = false;
		
		String rowJmsId = error.getJmsMessageId();
		error.setJmsMessageId(null);
		try {
			RestTemplate restTemplate = new RestTemplate();			
			
			String res = restTemplate.postForObject(debitCreditProcessingBaseURL + "/reprocessError", error, String.class);			
			if(res.equalsIgnoreCase("OK")) {
				isSuccessful = true;				
			} else {
				isSuccessful = false;
			}
			error.setJmsMessageId(rowJmsId);
		} catch (Exception e) {
			super.addErrorMessage("generic.error", e.getMessage());
		} finally {
			error.setJmsMessageId(rowJmsId);
		}
		return isSuccessful;

	}

	protected void loadNewPage() {
		thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_DEBIT_CREDIT_MEMO_FORMAT_ERRORS);
		thisPage.setPageUrl(ViewConstants.DEBIT_CREDIT_MEMO_FORMAT_ERRORS);

	}

	protected void restoreOldPage() {
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

	
	public void showSelectedRowErrors() {		
    	if(selectedRow != null) {
    		for(String errMsg: selectedRow.getErrors()){
    			String[] errorParts = errMsg.split("-");
    			if(errorParts.length == 1) {
    				super.addSimplErrorMessage(errorParts[0]);
    			} else {
    				String errorText = "";
    				for(int i=1; i<errorParts.length; i++) {  // skip first part
    					errorText = errorText + errorParts[i];
    				}
    				super.addSimplErrorMessage(errorText);
    			}
    		}
    	}
	}

	private void adjustDataTableHeight(List<DebitCreditTransactionVO> list){
		if(list != null){
			if(list.isEmpty()){
				this.setDataTableMessage(talMessage.getMessage("no.records.found"));
				super.getDataTable().setHeight(30);
			}else{
				getDataTable().setMaximumHeight();
			}
		}else{
			this.setDataTableMessage(talMessage.getMessage("no.records.found"));
			super.getDataTable().setHeight(30);
		}
	}	

	
	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public List<DebitCreditTransactionVO> getRows() {
		return rows;
	}

	public void setRows(List<DebitCreditTransactionVO> rows) {
		this.rows = rows;
	}

	public DebitCreditTransactionVO getSelectedRow() {
		return selectedRow;
	}

	public void setSelectedRow(DebitCreditTransactionVO selectedRow) {
		this.selectedRow = selectedRow;
	}

	public int getRowCount() {
		return rowCount;
	}

	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}

	public String getDataTableMessage() {
		return dataTableMessage;
	}

	public void setDataTableMessage(String dataTableMessage) {
		this.dataTableMessage = dataTableMessage;
	}
	public List<SortMeta> getPreSort() {
		return preSort;
	}

	public void setPreSort(List<SortMeta> preSort) {
		this.preSort = preSort;
	}

	public boolean isHasProcessorPermission() {
		return hasProcessorPermission;
	}

	public void setHasProcessorPermission(boolean hasProcessorPermission) {
		this.hasProcessorPermission = hasProcessorPermission;
	}

	public boolean isHasGeneralEditPermission() {
		return hasGeneralEditPermission;
	}

	public void setHasGeneralEditPermission(boolean hasGeneralEditPermission) {
		this.hasGeneralEditPermission = hasGeneralEditPermission;
	}

	public String getHeaderMessage() {
		return headerMessage;
	}

	public void setHeaderMessage(String headerMessage) {
		this.headerMessage = headerMessage;
	}

	public boolean isEnableReprocessBtn() {
		return enableReprocessBtn;
	}

	public void setEnableReprocessBtn(boolean enableReprocessBtn) {
		this.enableReprocessBtn = enableReprocessBtn;
	}
}
