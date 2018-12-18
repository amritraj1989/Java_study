package com.mikealbert.vision.view.bean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mikealbert.data.entity.AcceptanceQueueV;
import com.mikealbert.data.entity.EarlyTermQuote;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.RejectReason;
import com.mikealbert.data.enumeration.DocumentNameEnum;
import com.mikealbert.data.enumeration.ReportNameEnum;
import com.mikealbert.data.vo.QuoteVO;
import com.mikealbert.data.vo.RevisionVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.AcceptanceQueueService;
import com.mikealbert.service.FleetMasterService;
import com.mikealbert.service.LeaseFinalizationDocumentService;
import com.mikealbert.service.QuotationService;
import com.mikealbert.service.reporting.JasperReportService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.view.ViewConstants;
import com.mikealbert.vision.vo.DocumentListItemVO;
import com.mikealbert.vision.vo.RevisionSchAListItemVO;

@Component
@Scope("view")
public class AcceptanceQueueBean extends StatefulBaseBean {
	private static final long serialVersionUID = -6351179202239184328L;
	
	@Resource AcceptanceQueueService acceptanceQueueService;
	@Resource QuotationService quotationService;
	@Resource FleetMasterService fleetMasterService;
	@Resource JasperReportService jasperReportService;
	@Resource JasperReportBean jasperReportBean;
	@Resource LeaseFinalizationDocumentService leaseFinalizationDocumentService;
	
	private List<AcceptanceQueueV> filteredList = new ArrayList<AcceptanceQueueV>();
	private List<AcceptanceQueueV> masterList;
	private AcceptanceQueueV selectedAcceptanceQueueItem;
	private String quoteNumberFilter = "";
	private String clientNameFilter = "";
	private String acceptanceTypeFilter = "";
	private String emptyDataTableMessage;
	private static final String DATA_TABLE_UI_ID = "acceptanceTable";
	private boolean buttonDisabled = true;
	private boolean restoreView = false;
	private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yy");
	private List<RejectReason> rejectReasonList= new ArrayList<RejectReason>();
	private RejectReason selectedRejectReason;
	private Long scrollPosition;
	private int resultPerPage = ViewConstants.RECORDS_PER_PAGE;
	private Long miles;
	private String tmpVinNo;
	private String quoteNumber;
	private String selectedRowAcceptanceType;
	
	private List<RevisionVO> revisionAndAmendmentList;
	private List<EarlyTermQuote> etQuoteList;
	private List<QuoteVO> quoteList  = new ArrayList<QuoteVO>();
	private static List<String> quoteStatusCodes = Arrays.asList("9","10");
	private static final String OER_ACCEPTANCE_TYPE = "OER";
	private static final String ET_QUOTE_STATUS_CODE = "ET";
	private static final String ET_QUOTE_STATUS_DESCRIPTION = "Early Termination";
	private Map<String, Object> nextPageValues = new HashMap<String, Object>();
	private List<DocumentListItemVO> documentListItems = new ArrayList<DocumentListItemVO>();


	@PostConstruct
	public void init() {
		initializeDataTable(560, 850, new int[] {4, 3, 6, 6, 5, 8, 10, 6, 4, 7, 12, 4, 3});
		openPage();
		try {
			loadData();
			if(restoreView) {
				performFilter();
				buttonDisabled = false;
			}
		} catch (Exception e) {
			logger.error(e);
			if(e instanceof MalBusinessException) {
				super.addErrorMessage(e.getMessage());
			} else {
				super.addErrorMessage("generic.error.occured.while", " fetching units..");
			}
		}
		this.setEmptyDataTableMessage(talMessage.getMessage("no.records.found"));
	}

	private void loadData() throws Exception {
		if(masterList != null)
			masterList.clear();
		if(filteredList != null)
			filteredList.clear();
		
		
		masterList = acceptanceQueueService.getAcceptanceQueueList();
		for (AcceptanceQueueV acceptanceQueueV : masterList) {
			if(acceptanceQueueV.getClientRequestDate() != null && 
					MALUtilities.isValidDate(acceptanceQueueV.getClientRequestDate())){
				 	Date dt  = MALUtilities.getDateObject(acceptanceQueueV.getClientRequestDate(), MALUtilities.DATE_PATTERN);
				    acceptanceQueueV.setClientRequestDate(dateFormatter.format(dt));
			}
		}
	
		AcceptanceQueueV newAcceptanceQueueV = null;
		for(AcceptanceQueueV clientFacingQueueV : masterList) {
			newAcceptanceQueueV = new AcceptanceQueueV();
			BeanUtils.copyProperties(clientFacingQueueV, newAcceptanceQueueV);
			filteredList.add(newAcceptanceQueueV);
		}
		
		setRejectReasonList(acceptanceQueueService.getRejectReasons());
	}

	protected void loadNewPage() {
		thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_ACCEPTANCE_QUEUE);
		thisPage.setPageUrl(ViewConstants.ACCEPTANCE_QUEUE);		
		
		this.setEmptyDataTableMessage(talMessage.getMessage("no.records.found"));
		restoreView = false;
	}

	protected void restoreOldPage() {
		this.quoteNumberFilter = (String) thisPage.getRestoreStateValues().get("QUOTE_NO_FILTER");
		this.acceptanceTypeFilter = (String) thisPage.getRestoreStateValues().get("ACCEPTANCE_TYPE_FILTER");
		this.clientNameFilter = (String) thisPage.getRestoreStateValues().get("CLIENT_NAME_FILTER");
		this.scrollPosition = Long.valueOf((String) thisPage.getRestoreStateValues().get("SCROLL_POSITION"));
		
		DataTable dt = (DataTable) getComponent(DATA_TABLE_UI_ID);	
		Integer pageStartIndex = (Integer) thisPage.getRestoreStateValues().get(ViewConstants.DT_SELECTD_PAGE_START_INDEX);
		//this case may happen if user  directly landed to driver add/edit page
		if(pageStartIndex != null){
			dt.setFirst(pageStartIndex);
			dt.setSortOrder((String) thisPage.getRestoreStateValues().get(ViewConstants.DT_DEFAULT_SORT_ORDER));
			dt.setValueExpression("sortBy", (ValueExpression) thisPage.getRestoreStateValues().get(ViewConstants.DT_DEFAULT_SORT_VE));
			this.selectedAcceptanceQueueItem = (AcceptanceQueueV) thisPage.getRestoreStateValues().get(ViewConstants.DT_SELECTED_ITEM);
			this.selectedRowAcceptanceType = this.selectedAcceptanceQueueItem.getQuoteAcceptanceTypeCode();
		}
		restoreView = true;
	}
	
	private Map<String, Object> getCurrentPageRestoreStateValuesMap() {
		
		Map<String, Object> restoreStateValues = new HashMap<String, Object>();
	
		restoreStateValues.put("QUOTE_NO_FILTER", quoteNumberFilter);
		restoreStateValues.put("ACCEPTANCE_TYPE_FILTER", acceptanceTypeFilter);
		restoreStateValues.put("CLIENT_NAME_FILTER", clientNameFilter);
		String scrollValue = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("hiddenScrollPosition");
		restoreStateValues.put("SCROLL_POSITION", scrollValue);
		
		DataTable dt = (DataTable) getComponent(DATA_TABLE_UI_ID);
		//this may happen because user may directly land to driver add/edit page
		if (dt != null) {
			ValueExpression ve = dt.getValueExpression("sortBy");
			restoreStateValues.put(ViewConstants.DT_DEFAULT_SORT_VE, ve);
			restoreStateValues.put(ViewConstants.DT_SELECTD_PAGE_START_INDEX, dt.getFirst());
			restoreStateValues.put(ViewConstants.DT_DEFAULT_SORT_ORDER, dt.getSortOrder());
			restoreStateValues.put(ViewConstants.DT_SELECTED_ITEM, selectedAcceptanceQueueItem);
		}
		return restoreStateValues;
		
	}
	
	
	public List<AcceptanceQueueV> getMasterList() {
		return masterList;
	}

	public void onRowSelect(SelectEvent event) {
		setButtonDisabled(false);
	}

	public void performFilter() {
		filteredList.clear();
		setButtonDisabled(true);

		if(!MALUtilities.isEmpty(masterList)) {
			AcceptanceQueueV newAcceptanceQueueV = null;
			for(AcceptanceQueueV clientFacingQueueV : masterList) {
				newAcceptanceQueueV = new AcceptanceQueueV();
				BeanUtils.copyProperties(clientFacingQueueV, newAcceptanceQueueV);
				filteredList.add(newAcceptanceQueueV);
			}
		}

		if(MALUtilities.isNotEmptyString(clientNameFilter)) {
			AcceptanceQueueV acceptanceQueueV = null;
			for(Iterator<AcceptanceQueueV> acceptanceQueueItr = filteredList.iterator(); acceptanceQueueItr.hasNext();) {
				acceptanceQueueV = acceptanceQueueItr.next();
				if(acceptanceQueueV.getClientAccountName() == null || acceptanceQueueV.getClientAccountName().toUpperCase().indexOf(clientNameFilter.trim().toUpperCase()) < 0) {
					acceptanceQueueItr.remove();
				}
			}
		}
		
		if(MALUtilities.isNotEmptyString(quoteNumberFilter)) {
			AcceptanceQueueV acceptanceQueueV = null;
			for(Iterator<AcceptanceQueueV> acceptanceQueueItr = filteredList.iterator(); acceptanceQueueItr.hasNext();) {
				acceptanceQueueV = acceptanceQueueItr.next();
				if(acceptanceQueueV.getQuoId() == null || acceptanceQueueV.getQuoId().toString().indexOf(quoteNumberFilter.trim()) < 0) {
					acceptanceQueueItr.remove();
				}
			}
		}
		
		if(MALUtilities.isNotEmptyString(acceptanceTypeFilter)) {
			AcceptanceQueueV acceptanceQueueV = null;
			for(Iterator<AcceptanceQueueV> acceptanceQueueItr = filteredList.iterator(); acceptanceQueueItr.hasNext();) {
				acceptanceQueueV = acceptanceQueueItr.next();
				if(acceptanceQueueV.getRequestForAcceptType() == null || acceptanceQueueV.getRequestForAcceptType().toUpperCase().indexOf(acceptanceTypeFilter.trim().toUpperCase())< 0) {
					acceptanceQueueItr.remove();
				}
			}
		}		
	}
	
	public int reqdByCustomSort(Object ob1, Object ob2){
		
			AcceptanceQueueV o1= (AcceptanceQueueV)ob1;
			AcceptanceQueueV o2= (AcceptanceQueueV)ob2;
			int compareValue = 0;
		    if(MALUtilities.isEmpty(o1.getClientRequestDate()) || MALUtilities.isEmpty(o2.getClientRequestDate())) {
		    	compareValue =  MALUtilities.isEmpty(o1.getClientRequestDate()) ? 1 : -1;
			} else {
				if(MALUtilities.isValidDate(o1.getClientRequestDate()) && MALUtilities.isValidDate(o2.getClientRequestDate())){
					compareValue =  MALUtilities.formatDate(o1.getClientRequestDate()).compareTo(MALUtilities.formatDate(o2.getClientRequestDate()));
				}else if(MALUtilities.isValidDate(o1.getClientRequestDate()) && !MALUtilities.isValidDate(o2.getClientRequestDate())){
					compareValue =  -1;
				}else if(!MALUtilities.isValidDate(o1.getClientRequestDate()) && MALUtilities.isValidDate(o2.getClientRequestDate())){
					compareValue =  1;
				}else if(! MALUtilities.isValidDate(o1.getClientRequestDate()) && !MALUtilities.isValidDate(o2.getClientRequestDate())){
					compareValue =  o1.getClientRequestDate().compareTo(o2.getClientRequestDate());
				}
		}
		    
		return compareValue ;
	}

    public void populateMilesVinQuoteNo(AcceptanceQueueV selectedElement) {
    	if (!MALUtilities.isEmpty(selectedElement)) {
			this.setMiles(selectedElement.getQuoteStartOdo());
			this.setTmpVinNo(selectedElement.getTmpVinNo());
			this.setQuoteNumber(selectedElement.getQuoteNumber());
		}
    }

	public void acceptQuotation() {

		if (MALUtilities.isEmpty(selectedAcceptanceQueueItem)) {
			addErrorMessage("custom.message", "Please select a quote");
			RequestContext context = RequestContext.getCurrentInstance();
			context.addCallbackParam("failure", true);
			
		} else {
			if (selectedAcceptanceQueueItem.getQuoteAcceptanceTypeCode().equals(OER_ACCEPTANCE_TYPE) &&
				activeQuotesExist(selectedAcceptanceQueueItem.getQmdId(), quoteStatusCodes)){					
					RequestContext.getCurrentInstance().execute("PF('activeQuotesDialogWidget').show();");
			} else {
				acceptQuote();
			}
		}
	}
	

	public void acceptQuote() {
		try {
			String successMessage = acceptanceQueueService.acceptQuoteFromAcceptanceQueue(selectedAcceptanceQueueItem, super.getLoggedInUser().getEmployeeNo());

			// To remove accepted quote from acceptance queue
			for (Iterator<AcceptanceQueueV> acceptanceQueueItr = masterList.iterator(); acceptanceQueueItr.hasNext();) {
				AcceptanceQueueV unitProgressSearchVO = acceptanceQueueItr.next();
				if (unitProgressSearchVO.getQmdId().equals(selectedAcceptanceQueueItem.getQmdId())) {
					acceptanceQueueItr.remove();
					break;
				}
			}
			filteredList.remove(selectedAcceptanceQueueItem);			
			RequestContext.getCurrentInstance().update("acceptanceTable");
			
			successMessage = successMessage.replaceAll("WARNING!", "");
			StringTokenizer st = new StringTokenizer(successMessage, "\n");
			String message = "";
			while (st.hasMoreElements()) {
				message = (String) st.nextElement();
				addSuccessMessage("custom.message", message);
			}
			
			// Archiving/Email OE Contract Revision documents
			if(OER_ACCEPTANCE_TYPE.equals(selectedAcceptanceQueueItem.getQuoteAcceptanceTypeCode())){			
				try {
							
					Boolean isReportIsSetupForEmail = leaseFinalizationDocumentService.isReportIsSetupForEmail(selectedAcceptanceQueueItem.getQmdId() , ReportNameEnum.SCHEDULE_A);					
					selectedAcceptanceQueueItem.setScheduleAIsSetupForEmail(isReportIsSetupForEmail);
					isReportIsSetupForEmail = leaseFinalizationDocumentService.isReportIsSetupForEmail(selectedAcceptanceQueueItem.getQmdId() , ReportNameEnum.AMORTIZATION_SCHEDULE);	
					selectedAcceptanceQueueItem.setAmortizationIsSetupForEmail(isReportIsSetupForEmail);			

					
					String deliveryMethod = selectedAcceptanceQueueItem.isScheduleAIsSetupForEmail() ? "E" : "P";//E Email ,  P printer
					leaseFinalizationDocumentService.archiveOERevisionDocument(selectedAcceptanceQueueItem.getRevisionQmdId(), selectedAcceptanceQueueItem.getQmdId(), DocumentNameEnum.SCHEDULE_A, deliveryMethod, getLoggedInUserName());					
									
					deliveryMethod = selectedAcceptanceQueueItem.isAmortizationIsSetupForEmail() ? "E" : "P";
					leaseFinalizationDocumentService.archiveOERevisionDocument(selectedAcceptanceQueueItem.getRevisionQmdId(), selectedAcceptanceQueueItem.getQmdId(), DocumentNameEnum.AMORTIZATION_SCHEDULE, deliveryMethod, getLoggedInUserName());					

					setDocumentListItems(new ArrayList<DocumentListItemVO>());
					DocumentListItemVO doc;
					if(!selectedAcceptanceQueueItem.isScheduleAIsSetupForEmail()) {
						doc = new RevisionSchAListItemVO(1l, ReportNameEnum.SCHEDULE_A, selectedAcceptanceQueueItem.getRevisionQmdId(), selectedAcceptanceQueueItem.getQmdId());
						getDocumentListItems().add(doc);
					}
					if(!selectedAcceptanceQueueItem.isAmortizationIsSetupForEmail()) {
						doc = new RevisionSchAListItemVO(1l, ReportNameEnum.AMORTIZATION_SCHEDULE, selectedAcceptanceQueueItem.getRevisionQmdId(), selectedAcceptanceQueueItem.getQmdId());
						getDocumentListItems().add(doc);
					}
					if(getDocumentListItems().size() > 0) {
						RequestContext.getCurrentInstance().execute("showConfirmDocuments()");
					}
					
				} catch (Exception e) {
					logger.error(e ,"Error in Archiving OE Contract Revision document in Onbase...");
					addErrorMessage("generic.error.occured.while", "Archiving OE Contract Revision document in Onbase");
				}	
			}
			
			setButtonDisabled(true);
			this.setSelectedAcceptanceQueueItem(null);

			
		} catch (Exception ex) {
			handleException("generic.error.occured.while", new String[] { "accepting quotation" }, ex, null);
		}

	}
	
	public boolean activeQuotesExist(Long qmdId, List<String> quoteStatusCodes){
		int i  = 0;
		quoteList.clear();
		
		try {
			revisionAndAmendmentList = quotationService.getQuotationModelsByQuoteStatus(qmdId, quoteStatusCodes);
			
			for(RevisionVO revisionVo : revisionAndAmendmentList){
				if (!revisionVo.getQmdId().equals(qmdId)){
					QuoteVO quoteVO = new QuoteVO();
					quoteVO.setRowKey(String.valueOf(i++));
					quoteVO.setQuoId(revisionVo.getQuoId());
					quoteVO.setQuoteNo(revisionVo.getQuoteNo());
					quoteVO.setRevisionNo(revisionVo.getRevisionNo());			
					quoteVO.setQuoteDate(revisionVo.getRevisionDate());
					quoteVO.setQmdId(revisionVo.getQmdId());
					quoteVO.setStatusCode(revisionVo.getStatusCode());
					quoteVO.setStatusDescription(revisionVo.getStatusDescription());
					quoteList.add(quoteVO);
				}
			}
			
			FleetMaster fleetMaster = fleetMasterService.findByUnitNo(selectedAcceptanceQueueItem.getUnitNo());
			etQuoteList = quotationService.getUnacceptedEtQuotesByFmsId(fleetMaster.getFmsId());
			
			for(EarlyTermQuote etQuote : etQuoteList){
				QuoteVO quoteVO = new QuoteVO();
				quoteVO.setRowKey(String.valueOf(i++));
				quoteVO.setQuoId(etQuote.getEtqId());
				quoteVO.setQuoteDate(etQuote.getQuoteDate());
				quoteVO.setStatusCode(ET_QUOTE_STATUS_CODE);
				quoteVO.setStatusDescription(ET_QUOTE_STATUS_DESCRIPTION);
				quoteList.add(quoteVO);			
			}
		} catch (Exception e) {
			handleException("generic.error.occured.while", new String[] { "fetching active quotes for "
					+ selectedAcceptanceQueueItem.getQuoId() + " from this queue" }, e, null);
		}

		if (quoteList.isEmpty()) {
			return false;
		} else {
			sortQuoteList();
			return true;
		}	
	}

	private void sortQuoteList(){
		if(this.quoteList != null && this.quoteList.size() > 0){
			Collections.sort(this.quoteList, new Comparator<QuoteVO>() {
				public int compare(QuoteVO qv1, QuoteVO qv2) {
					int dateComp = qv2.getQuoteDate().compareTo(qv1.getQuoteDate());
					return ((dateComp == 0) ? qv2.getQuoId().compareTo(qv1.getQuoId()) : dateComp);
				}
			});			
		}		
	}

//TODO:	Currently implemented as a separate transaction from the ACCEPT process.  This is OK with the business.
	public void rejectOutstandingQuotes() {
		try {
			for(QuoteVO quote : quoteList){
				if (quote.getStatusDescription().equals(ET_QUOTE_STATUS_DESCRIPTION)){
					acceptanceQueueService.rejectOutstandingOeEtQuoteFromAcceptanceQueue(quote.getQuoId());
				} else {
					acceptanceQueueService.rejectOutstandingOeQuoteFromAcceptanceQueue(quote.getQmdId());
				}
			}
			acceptQuote();
		} catch (Exception e) {
			handleException("generic.error.occured.while", new String[] { "removing outstanding quote "
			+ selectedAcceptanceQueueItem.getQuoteNumber() + " from this queue" }, e, null);
		}
	}
	
	public void rejectQuote() {
		try {
			if (MALUtilities.isEmpty(selectedAcceptanceQueueItem)) {
				addErrorMessage("custom.message", "Please select a quote");
				RequestContext context = RequestContext.getCurrentInstance();
				context.addCallbackParam("failure", true);
				return;
			}
			
			if (MALUtilities.isEmpty(this.getSelectedRejectReason())) {
				addErrorMessage("custom.message", "Please select a reason from the list");
				RequestContext context = RequestContext.getCurrentInstance();
				context.addCallbackParam("failure", true);
				return;
			}
			
			acceptanceQueueService.rejectQuoteFromAcceptanceQueue(selectedAcceptanceQueueItem.getQmdId(), this.getSelectedRejectReason().getRejectReasonCode());
			
			// To remove rejected quote from acceptance queue
			for (Iterator<AcceptanceQueueV> acceptanceQueueItr = masterList.iterator(); acceptanceQueueItr.hasNext();) {
				AcceptanceQueueV unitProgressSearchVO = acceptanceQueueItr.next();
				if (unitProgressSearchVO.getQmdId().equals(selectedAcceptanceQueueItem.getQmdId())) {
					acceptanceQueueItr.remove();
					break;
				}
			}
			filteredList.remove(selectedAcceptanceQueueItem);
			RequestContext.getCurrentInstance().update("acceptanceTable");

			addSuccessMessage("custom.message", "Quote number " + selectedAcceptanceQueueItem.getQuoteNumber()
					+ " has been removed from this queue");
			setButtonDisabled(true);
			this.setSelectedAcceptanceQueueItem(null);
			this.setSelectedRejectReason(null);
			
		} catch (Exception e) {
			handleException("generic.error.occured.while", new String[] { "removing quote number "
					+ selectedAcceptanceQueueItem.getQuoteNumber() + " from this queue" }, e, null);
		}
	}
	
	public String  quoteOverviewAction(){
		
		if(MALUtilities.isEmpty(selectedAcceptanceQueueItem)) {
			addErrorMessage("custom.message", "Please select a quote");
			return null;
		}
		
		saveRestoreStateValues(getCurrentPageRestoreStateValuesMap());
		
		Map<String, Object> nextPageValues = new HashMap<String, Object>();		
		nextPageValues.put(ViewConstants.VIEW_PARAM_QUOTE_MODEL_ID, selectedAcceptanceQueueItem.getQmdId());
		nextPageValues.put(ViewConstants.VIEW_PARAM_FORWARDED_PAGE, true);
		saveNextPageInitStateValues(nextPageValues);
		
		if(selectedAcceptanceQueueItem.getProductCode().indexOf("OE") >= 0){
			forwardToURL(ViewConstants.QUOTE_OVERVIEW_OE);
		}else{
			forwardToURL(ViewConstants.QUOTE_OVERVIEW);
		}
		
		return null;
	}
	
	public void navigateToOpenEndRevisionPage() {
		saveRestoreStateValues(getCurrentPageRestoreStateValuesMap());
		nextPageValues.put(ViewConstants.VIEW_PARAM_QUOTE_MODEL_ID, selectedAcceptanceQueueItem.getRevisionQmdId());
		nextPageValues.put(ViewConstants.VIEW_REV_PARAM_QUOTE_MODEL_ID, selectedAcceptanceQueueItem.getQmdId());
		saveNextPageInitStateValues(nextPageValues);
		forwardToURL(ViewConstants.OPEN_END_REVISION);
	}
	
	public void rowSelectlistner(){			
		setSelectedRowAcceptanceType(selectedAcceptanceQueueItem.getQuoteAcceptanceTypeCode());
	}
	
	public String cancel() {	
		 return super.cancelPage(); 
	}
	
	public void onLoadRejectReason() {
		this.selectedRejectReason = null;
	}
	
	public List<AcceptanceQueueV> getFilteredList() {
		return filteredList;
	}

	public void setFilteredList(List<AcceptanceQueueV> filteredList) {
		this.filteredList = filteredList;
	}

	public String getClientNameFilter() {
		return clientNameFilter;
	}

	public void setClientNameFilter(String clientNameFilter) {
		this.clientNameFilter = clientNameFilter;
	}

	public String getEmptyDataTableMessage() {
		return emptyDataTableMessage;
	}

	public void setEmptyDataTableMessage(String emptyDataTableMessage) {
		this.emptyDataTableMessage = emptyDataTableMessage;
	}

	public void setMasterList(List<AcceptanceQueueV> masterList) {
		this.masterList = masterList;
	}

	public String getAcceptanceTypeFilter() {
		return acceptanceTypeFilter;
	}

	public void setAcceptanceTypeFilter(String acceptanceTypeFilter) {
		this.acceptanceTypeFilter = acceptanceTypeFilter;
	}

	public String getQuoteNumberFilter() {
		return quoteNumberFilter;
	}

	public void setQuoteNumberFilter(String quoteNumberFilter) {
		this.quoteNumberFilter = quoteNumberFilter;
	}

	public AcceptanceQueueV getSelectedAcceptanceQueueItem() {
		return selectedAcceptanceQueueItem;
	}

	public void setSelectedAcceptanceQueueItem(
			AcceptanceQueueV selectedAcceptanceQueueItem) {
		this.selectedAcceptanceQueueItem = selectedAcceptanceQueueItem;
	}

	public boolean isButtonDisabled() {
		return buttonDisabled;
	}

	public void setButtonDisabled(boolean buttonDisabled) {
		this.buttonDisabled = buttonDisabled;
	}

	public RejectReason getSelectedRejectReason() {
		return selectedRejectReason;
	}

	public void setSelectedRejectReason(RejectReason selectedRejectReason) {
		this.selectedRejectReason = selectedRejectReason;
	}

	public List<RejectReason> getRejectReasonList() {
		return rejectReasonList;
	}

	public void setRejectReasonList(List<RejectReason> rejectReasonList) {
		this.rejectReasonList = rejectReasonList;
	}

	public Long getScrollPosition() {
		return scrollPosition;
	}

	public void setScrollPosition(Long scrollPosition) {
		this.scrollPosition = scrollPosition;
	}

	public int getResultPerPage() {
		return resultPerPage;
	}

	public void setResultPerPage(int resultPerPage) {
		this.resultPerPage = resultPerPage;
	}

	public Long getMiles() {
		return miles;
	}

	public void setMiles(Long miles) {
		this.miles = miles;
	}

	public String getTmpVinNo() {
		return tmpVinNo;
	}

	public void setTmpVinNo(String tmpVinNo) {
		this.tmpVinNo = tmpVinNo;
	}

	public String getQuoteNumber() {
		return quoteNumber;
	}

	public void setQuoteNumber(String quoteNumber) {
		this.quoteNumber = quoteNumber;
	}

	public List<RevisionVO> getRevisionAndAmendmentList() {
		return revisionAndAmendmentList;
	}

	public void setRevisionAndAmendmentList(List<RevisionVO> revisionAndAmendmentList) {
		this.revisionAndAmendmentList = revisionAndAmendmentList;
	}

	public List<EarlyTermQuote> getEtQuoteList() {
		return etQuoteList;
	}

	public void setEtQuoteList(List<EarlyTermQuote> etQuoteList) {
		this.etQuoteList = etQuoteList;
	}

	public List<QuoteVO> getQuoteList() {
		return quoteList;
	}

	public void setQuoteList(List<QuoteVO> quoteList) {
		this.quoteList = quoteList;
	}

	public String getSelectedRowAcceptanceType() {
		return selectedRowAcceptanceType;
	}

	public void setSelectedRowAcceptanceType(String selectedRowAcceptanceType) {
		this.selectedRowAcceptanceType = selectedRowAcceptanceType;
	}

	public List<DocumentListItemVO> getDocumentListItems() {
		return documentListItems;
	}

	public void setDocumentListItems(List<DocumentListItemVO> documentListItems) {
		this.documentListItems = documentListItems;
	}


}
