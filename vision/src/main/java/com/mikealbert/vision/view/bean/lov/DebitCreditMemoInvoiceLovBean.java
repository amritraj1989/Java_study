package com.mikealbert.vision.view.bean.lov;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.primefaces.event.SelectEvent;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.dao.FleetMasterDAO;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.vo.DebitCreditInvoiceSearchCriteriaVO;
import com.mikealbert.data.vo.DebitCreditInvoiceSearchResultVO;
import com.mikealbert.service.DebitCreditMemoService;
import com.mikealbert.service.WillowConfigService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.view.ViewConstants;
import com.mikealbert.vision.view.bean.BaseBean;

@Component("debitCreditMemoInvoiceLovBean")
@Scope("view")
public class DebitCreditMemoInvoiceLovBean extends BaseBean  {

	MalLogger logger = MalLoggerFactory.getLogger(this.getClass());

	private static final long serialVersionUID = 1L;
	
	private String headerMessage;// RE-1100
	private DebitCreditInvoiceSearchResultVO selectedInvoice;
	private DebitCreditInvoiceSearchCriteriaVO searchCriteria;
	private int rowsPerPage = ViewConstants.LOV_RECORD_SIZE;
	private List<DebitCreditInvoiceSearchResultVO> resultList;

	@Resource DebitCreditMemoService debitCreditMemoService;
	@Resource FleetMasterDAO fleetMasterDAO;
	@Resource WillowConfigService willowConfigService;

	@PostConstruct
	public void init() {
		try{
			String configValue = willowConfigService.getConfigValue("DC_INVOICE_SEARCH_MONTHS");// RE-1100
			headerMessage = "(Only billed/unbilled invoices from the last "+configValue +" months will display.)";
			resultList = new ArrayList<DebitCreditInvoiceSearchResultVO>();
			logger.info("Method name: Init, DebitCreditMemoInvoiceLovBean:");
		}catch(Exception ex){
			handleException("generic.error.occured.while", new String[] { "loading LOV" }, ex, "init");
		}
		
 }


	public void fetchLOVData() {
		
		resultList.clear();	
		searchCriteria = new DebitCreditInvoiceSearchCriteriaVO();
		searchCriteria.setInvoiceOldInMonth(Integer.parseInt(willowConfigService.getConfigValue("DC_INVOICE_SEARCH_MONTHS")));
		
		String unitNoParam = (String) getRequestParameter("UNIT_NO_INPUT_PARAM");		
		if (!MALUtilities.isEmptyString(unitNoParam)) {			
			String unitNo = (String) getRequestParameter(unitNoParam);
			FleetMaster fleetMaster = fleetMasterDAO.findByUnitNo(unitNo);
			if(fleetMaster == null){							
				return;
			}
			searchCriteria.setFmsId(fleetMaster.getFmsId());
		}
		
		String analysisCategoryIdParam = (String) getRequestParameter("ANALYSIS_CATEGORY_ID_INPUT_PARAM");
		if (!MALUtilities.isEmptyString(analysisCategoryIdParam)) {
			String analysisCategoryId = (String) getRequestParameter(analysisCategoryIdParam);
			searchCriteria.setAnalysisCategoryId(Long.parseLong(analysisCategoryId));
		}
		
		String analysisCategoryParam = (String) getRequestParameter("ANALYSIS_CATEGORY_CODE_INPUT_PARAM");
		if (!MALUtilities.isEmptyString(analysisCategoryParam)) {
			String analysisCategory = (String) getRequestParameter(analysisCategoryParam);
			searchCriteria.setAnalysisCategory(analysisCategory);
		}		
		
		String analysisCodeParam = (String) getRequestParameter("ANALYSIS_CODE_INPUT_PARAM");
		if (!MALUtilities.isEmptyString(analysisCodeParam)) {
			String analysisCode = (String) getRequestParameter(analysisCodeParam);
			if(MALUtilities.isNotEmptyString(analysisCode)){
				searchCriteria.setAnalysisCode(analysisCode);
			}
		}
		
		String accountCodeParam = (String) getRequestParameter("ACCOUNT_CODE_INPUT_PARAM");
		if (!MALUtilities.isEmptyString(accountCodeParam)) {
			String accountCode = (String) getRequestParameter(accountCodeParam);
			searchCriteria.setAccountCode(accountCode);
		}
		
		String clientUnit = (String) getRequestParameter("IS_UNIT_BELONG_TO_ACCOUNT_INPUT_PARAM");
		String clientUnitFlag =  null;
		if (!MALUtilities.isEmptyString(clientUnit)) {
			 clientUnitFlag =  getRequestParameter(clientUnit);	
		}
		
		if(! MALUtilities.isEmptyString(clientUnit) && MALUtilities.isEmptyString(clientUnitFlag)){//mean its not selected
			searchCriteria.setAccountCode(null);
		}	
	
		setSelectedInvoice(null);
		logger.debug("Searched input: " + searchCriteria);
		loadInvoiceList();
		
	}

	private void loadInvoiceList() {
		try{
			resultList.clear();
			int i  = 0;
			List<DebitCreditInvoiceSearchResultVO>  tempResultList = debitCreditMemoService.searchInvoices(searchCriteria);
			for (DebitCreditInvoiceSearchResultVO debitCreditInvoiceSearchResultVO : tempResultList) {
				if(! resultList.contains(debitCreditInvoiceSearchResultVO)){
					debitCreditInvoiceSearchResultVO.setRowKey(String.valueOf(i++));
					resultList.add(debitCreditInvoiceSearchResultVO);
				}
			}
			
		}catch(Exception ex){
			logger.error(ex);
			handleException("generic.error.occured.while", new String[] { "loading LOV" }, ex, "loadInvoiceList");
		}
	}


	public void onRowSelect(SelectEvent event) {
		DebitCreditInvoiceSearchResultVO selectedInvoice = (DebitCreditInvoiceSearchResultVO) event.getObject();
		setSelectedInvoice(selectedInvoice);		
	}

	public int getRowsPerPage() {
		return rowsPerPage;
	}

	public void setRowsPerPage(int rowsPerPage) {
		this.rowsPerPage = rowsPerPage;
	}

	public DebitCreditInvoiceSearchResultVO getSelectedInvoice() {
		return selectedInvoice;
	}

	public void setSelectedInvoice(
			DebitCreditInvoiceSearchResultVO selectedInvoice) {
		this.selectedInvoice = selectedInvoice;
	}

	public List<DebitCreditInvoiceSearchResultVO> getResultList() {
		return resultList;
	}

	public void setResultList(List<DebitCreditInvoiceSearchResultVO> resultList) {
		this.resultList = resultList;
	}


	public String getHeaderMessage() {
		return headerMessage;
	}


	public void setHeaderMessage(String headerMessage) {
		this.headerMessage = headerMessage;
	}


}
