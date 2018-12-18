package com.mikealbert.vision.view.bean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.component.UIInput;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleSelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.event.data.PageEvent;
import org.primefaces.event.data.SortEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.mikealbert.data.dao.DoclDAO;
import com.mikealbert.data.entity.DocPropertyValue;
import com.mikealbert.data.entity.Docl;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.SupplierProgressHistory;
import com.mikealbert.data.enumeration.DocPropertyEnum;
import com.mikealbert.data.enumeration.UnitProgressCodeEnum;
import com.mikealbert.data.vo.OrderProgressSearchCriteriaVO;
import com.mikealbert.data.vo.OrderProgressSearchResultVO;
import com.mikealbert.exception.MalException;
import com.mikealbert.service.CustomerAccountService;
import com.mikealbert.service.DocPropertyValueService;
import com.mikealbert.service.PurchaseOrderService;
import com.mikealbert.service.QuotationService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.service.OrderProgressService;
import com.mikealbert.vision.service.SupplierService;
import com.mikealbert.vision.view.ViewConstants;

@Component
@Scope("view")
public class OrderProgressSearchBean extends StatefulBaseBean{

	private static final long serialVersionUID = 1573153692578673107L;
	
	@Resource private OrderProgressService orderProgressService;
	@Resource PurchaseOrderService purchaseOrderService;
	@Resource QuotationService quotationService;
	@Resource CustomerAccountService customerAccountService;
	@Resource SupplierService supplierService;
	@Resource DocPropertyValueService docPropertyValueService;
	@Resource DoclDAO doclDAO;

	
	private OrderProgressSearchCriteriaVO searchCriteria;
	private List<String> years;
	private List<String> makes;
	private LazyDataModel<OrderProgressSearchResultVO> lazyOrderProgressSearchResults = null;	
	private List<OrderProgressSearchResultVO> selectedResultVOs = null;
	private List<OrderProgressSearchResultVO> orderProgressSearchResults =  new ArrayList<OrderProgressSearchResultVO>();
	private boolean isSearchRequired = false;
	private boolean equipmentSearch;
	private List<String> selectedUnitStandardEquipments = null;
	private String selectedUnitFactoryOptionalEquipments;
	private boolean noEquipment = true;
	private Integer daysAdjusted = null;
	private String adjustmentNote;
	private boolean stockOrder = false;
	  
	private String dataTableMessage;
	private int resultPerPage = ViewConstants.RECORDS_PER_PAGE_MEDIUM;
	private static final String DATA_TABLE_UI_ID ="searchResultDataTable";
	private static final String DAYS_UI_ID = "daysAdjusted";
	private static final String NOTE_UI_ID = "noteTextArea";
	private static final String ETA_DATE_UI_ID = "clientETADateLbl";
	private static final String PO_ORDERING_LEAD_TIME = "PO_ORDERING_LEAD_TIME";
	private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy");
	public final String ACQUISITION_TYPE = "ACQUISITION_TYPE";

	@PostConstruct
	public void init() {
		initializeDataTable(620, 850, new int[] { 2, 5, 20, 6, 3, 4, 8, 38, 8, 6});
		openPage();
			
		setYears(orderProgressService.findDistinctYears(getSearchCriteria()));
		setMakes(orderProgressService.findDistinctMakes(getSearchCriteria()));
		
		setLazyOrderProgressSearchResults(new LazyDataModel<OrderProgressSearchResultVO>() {
			private static final long serialVersionUID = -7649338186972369712L;

			@Override
			public List<OrderProgressSearchResultVO> load(int first, int pageSize, String sortBy, SortOrder sortOrder, Map<String, Object> arg4) {
				int pageIdx = (first == 0) ? 0 : (first / pageSize);
				PageRequest page = new PageRequest(pageIdx, pageSize);				
				Sort sort = null;
				
				if(MALUtilities.isNotEmptyString(sortBy)){
					if (sortOrder.name().equalsIgnoreCase(SortOrder.DESCENDING.toString())) {
						 sort = new Sort(Sort.Direction.DESC, orderProgressService.resolveSortByName(sortBy));
					}else{
						 sort = new Sort(Sort.Direction.ASC, orderProgressService.resolveSortByName(sortBy));
					}
				}
				
				orderProgressSearchResults  = getLazySearchResultList(page ,sort);
				return orderProgressSearchResults;
			}

			@Override
			public OrderProgressSearchResultVO getRowData(String rowKey) {
				for (OrderProgressSearchResultVO resultVO : orderProgressSearchResults) {
					if (String.valueOf(resultVO.getQmdId()).equals(rowKey))
						return resultVO;
				}
				return null;
			}

			@Override
			public Object getRowKey(OrderProgressSearchResultVO orderProgressSearchResultVO) {
				return orderProgressSearchResultVO.getQmdId();
			}
		});
		getLazyOrderProgressSearchResults().setPageSize(getResultPerPage());
	}
	
	public void changeMfgCode(){
		setYears(orderProgressService.findDistinctYears(getSearchCriteria()));
		setMakes(orderProgressService.findDistinctMakes(getSearchCriteria()));
	}

	public void changeYear(){
		setMakes(orderProgressService.findDistinctMakes(getSearchCriteria()));
	}
	
	protected void loadNewPage() {	
		thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_ORDER_PROGRESS_SEARCH);
		thisPage.setPageUrl(ViewConstants.ORDER_PROGRESS_SEARCH);
		
		setSearchCriteria(new OrderProgressSearchCriteriaVO());

		this.isSearchRequired = false;
	}

	protected void restoreOldPage() {
		
	}
	
	public String cancel() {	
		 return super.cancelPage(); 
	}
	
	public List<ExternalAccount> autoCompleteClientListener(String criteria){
		List<ExternalAccount> clients = new ArrayList<ExternalAccount>();
				
		clients = customerAccountService.findOpenCustomerAccountsByNameOrCode(criteria, getLoggedInUser().getCorporateEntity() , new PageRequest(0,50));
	    if(!clients.isEmpty()){
			Collections.sort(clients, new Comparator<ExternalAccount>() { 
				public int compare(ExternalAccount uf1, ExternalAccount uf2) { 
					return uf1.getAccountName().toLowerCase().compareTo(uf2.getAccountName().toLowerCase()); 
				}
			});		    	
	    }
		
		return clients;
	}
	
	public void performSearch() {
		this.isSearchRequired = true;
		this.selectedResultVOs = null;
		DataTable pfDataTable = ((DataTable) getComponent(DATA_TABLE_UI_ID));
		if(pfDataTable != null)
			pfDataTable.setFirst(0);
	}
	
	private List<OrderProgressSearchResultVO> getLazySearchResultList(PageRequest page, Sort sort){
		if(isSearchRequired) {
			try {
				if(MALUtilities.isNotEmptyString(searchCriteria.getFactoryEquipment())) {
					setEquipmentSearch(true);
				} else {
					setEquipmentSearch(false);
				}
				
				if(MALUtilities.isEmptyString(searchCriteria.getOrderType())) {
					getSearchCriteria().setOrderType("F");
				}
				
				orderProgressSearchResults = orderProgressService.findUnits(searchCriteria , page, sort);
				int count  = orderProgressService.findUnitsCount(searchCriteria);
				getLazyOrderProgressSearchResults().setRowCount(count);
				getLazyOrderProgressSearchResults().setWrappedData(orderProgressSearchResults);
				adjustDataTableAfterSearch(orderProgressSearchResults);
				
			} catch (Exception ex) {
				super.addErrorMessage("generic.error", ex.getMessage());
			}	
		}
		return orderProgressSearchResults;
	}
	      
    public void onRowSelect(ToggleSelectEvent event) {
    }
    
    public void onRowSelect(SelectEvent event) {
    }
	
    public void onRowUnselect(UnselectEvent event) {
    }    
    
    public void onPageChange(PageEvent event) {
		getSelectedResultVOs().clear();
    }
	
	public void onSortOperation(SortEvent event) {
		getSelectedResultVOs().clear();
	 }
    
    public void populateSelectedUnitEquipments(OrderProgressSearchResultVO orderProgress) {
    	setSelectedUnitStandardEquipments(new ArrayList<String>());
    	if(!MALUtilities.isEmpty(orderProgress.getQmdId())){
			setSelectedUnitStandardEquipments(quotationService.getStandardAccessories(orderProgress.getQmdId()));
			Collections.sort(getSelectedUnitStandardEquipments());
			setStockOrder(false);
    	}else{
    		// standard equipments for stock orders
    		setSelectedUnitStandardEquipments(orderProgressService.getPossibleStandardAccessoriesByDocId(orderProgress.getDocId()));
			Collections.sort(getSelectedUnitStandardEquipments());
			setStockOrder(true);
    	}
		setSelectedUnitFactoryOptionalEquipments(orderProgressService.getOptionalAccessories(orderProgress.getDocId()));
		if((getSelectedUnitStandardEquipments() != null && getSelectedUnitStandardEquipments().size() > 0) || !MALUtilities.isEmptyString(getSelectedUnitFactoryOptionalEquipments())) {
			setNoEquipment(false);
		}
    }

    public void updateETAListener() {
		setDaysAdjusted(null);
		setAdjustmentNote(null);		
		((UIInput)getComponent(DAYS_UI_ID)).setValid(true);
		((UIInput)getComponent(NOTE_UI_ID)).setValid(true);
		
		RequestContext.getCurrentInstance().update("updateETADialog");
	}
    
    public void updateETAandNotes() {
    	boolean updateNoIssue = true;
    	boolean isValid = true;
		
		if(MALUtilities.isEmpty(daysAdjusted) || daysAdjusted.intValue() == 0){
			addErrorMessageSummary(DAYS_UI_ID, "required.field", "Days");
			isValid = false;
		}		
		if(MALUtilities.isEmptyString(adjustmentNote)){
			addErrorMessageSummary(NOTE_UI_ID, "required.field", "Notes");
			isValid = false;
		}	
    	
    	if(isValid) {
    		String supplierNote = "ETA adjusted by "+ String.valueOf(daysAdjusted) + " days";
        	String employeeNo = getLoggedInUser().getEmployeeNo();
        	StringBuilder unitsNotUpdated = new StringBuilder("");
        	StringBuilder unitsOlderETA = new StringBuilder("");
        	StringBuilder unitsOlderClientDate = new StringBuilder("");
        	StringBuilder unitsWithAcquisitionType = new StringBuilder("");
        	
        	SupplierProgressHistory confirmSPH = null;
        	SupplierProgressHistory etaSPH = null;
        	Date etaActionDate = null;
        	String formattedETADate;
        	try {
    	    	for(OrderProgressSearchResultVO orderProgress : getSelectedResultVOs()) {
    	    		confirmSPH = supplierService.getSupplierProgressHistoryForDocAndType(orderProgress.getDocId(), UnitProgressCodeEnum.CONFIRM.getCode());
    	    		if(confirmSPH != null) {
    	    			DocPropertyValue ordLeadTime = docPropertyValueService.findByNameDocId(DocPropertyEnum.PO_ORDERING_LEAD_TIME, orderProgress.getDocId());
    	    			int mfgTime;
    	    			if (!MALUtilities.isEmpty(ordLeadTime)) {
    	    				mfgTime = Integer.valueOf(ordLeadTime.getPropertyValue());
    	    			} else {
    	    				mfgTime = orderProgressService.getManufacturerLeadTime(orderProgress.getFmsId()).intValue();
    	    			}
    	    			int dealerAccLeadTime = purchaseOrderService.getUnitLeadTimeByDocId(orderProgress.getDocId()).intValue(); 
    	    			int pdiLeadTime = orderProgressService.getPDILeadTime(orderProgress.getFmsId()).intValue();
    	    			int totalDaysAdjusted = daysAdjusted + mfgTime + dealerAccLeadTime + pdiLeadTime;
    	    			etaActionDate = MALUtilities.addDays(confirmSPH.getActionDate(), totalDaysAdjusted);
    	    	        
    	    			QuotationModel quotationModel = null;
    	    			Date requiredDate = null;
    	    			if(!MALUtilities.isEmpty(orderProgress.getQmdId())){
	    	    			try {
								quotationModel = quotationService.getQuotationModel(orderProgress.getQmdId());
							} catch (Exception e) {
								addErrorMessage("generic.error", e.getMessage());
							}
    	    			}else{
    	    				List<Docl> doclList = doclDAO.findDoclByDocIdAndUserDef4(orderProgress.getDocId(), "MODEL");
        	    			if(!MALUtilities.isEmpty(doclList) && doclList.size() == 1){
        	    				requiredDate = doclList.get(0).getRecDatePromised();
        	    			}
    	    			}
    	    			
    	    			
    	    			DocPropertyValue acquisitionTypeValue = docPropertyValueService.findByNameDocId(DocPropertyEnum.ACQUISITION_TYPE, orderProgress.getDocId());
    	    			
    	    			//If the calculated ETA date is less than or equal to the client requested "On Date" then do not add a new supplier status with the calculated ETA.
    	    			if (!MALUtilities.isEmpty(quotationModel) && (quotationModel.getClientRequestType().equalsIgnoreCase("OD")) && 
    	    					(MALUtilities.compateDates(quotationModel.getRequiredDate(), etaActionDate) >= 0)) {
    	    				updateNoIssue = false;
    	    	        	if(unitsOlderClientDate.length() >=1) {
    	    	        		unitsOlderClientDate.append(",  ");
        	    			}
    	    	        	unitsOlderClientDate.append(orderProgress.getUnitNo());
						}
    	    			else if(MALUtilities.isEmpty(quotationModel) && !MALUtilities.isEmpty(requiredDate) && 
    	    					(MALUtilities.compateDates(requiredDate, etaActionDate) >= 0)){
    	    				updateNoIssue = false;
    	    	        	if(unitsOlderClientDate.length() >=1) {
    	    	        		unitsOlderClientDate.append(",  ");
        	    			}
    	    	        	unitsOlderClientDate.append(orderProgress.getUnitNo());
    	    			}
    	    	        else if (MALUtilities.compateDates(confirmSPH.getActionDate(), etaActionDate) > 0) {
    	    	        	updateNoIssue = false;
    	    	        	if(unitsOlderETA.length() >=1) {
    	    	        		unitsOlderETA.append(",  ");
        	    			}
    	    	        	unitsOlderETA.append(orderProgress.getUnitNo()); 
						}else if(!MALUtilities.isEmpty(acquisitionTypeValue) && !MALUtilities.isEmpty(acquisitionTypeValue.getPropertyValue())){
	    	    				updateNoIssue = false;
	    	    	        	if(unitsWithAcquisitionType.length() >=1) {
	    	    	        		unitsWithAcquisitionType.append(",  ");
	        	    			}
	    	    	        	unitsWithAcquisitionType.append(orderProgress.getUnitNo());
						} else {
    	    	        	etaSPH = new SupplierProgressHistory();
        	    			etaSPH.setDocId(orderProgress.getDocId());
        	    			etaSPH.setProgressType(UnitProgressCodeEnum.ETA.getCode());
        	    			etaSPH.setSupplier(supplierNote);
        	    			etaSPH.setOpCode(employeeNo);
        	    			etaSPH.setActionDate(etaActionDate);
        	    			etaSPH.setEnteredDate(new Date());
        	    			orderProgressService.saveETAandNotes(etaSPH, orderProgress.getFmsId(), super.getLoggedInUser().getUsername(), getAdjustmentNote());
        	    			
        	    			int index = -1;
        	    			etaSPH = supplierService.getSupplierProgressHistoryForDocAndType(orderProgress.getDocId(), UnitProgressCodeEnum.ETA.getCode());
        	    			for (OrderProgressSearchResultVO orderProgressVO : orderProgressSearchResults) {
        	    				index += 1;
        	    				if(orderProgressVO.getDocId().equals(etaSPH.getDocId())){
        	    					break;
        	    				}
        	    			}
        	    			
        	    			if(!MALUtilities.isEmpty(etaSPH.getActionDate())){				
        	    				formattedETADate = dateFormatter.format(etaSPH.getActionDate());							
        	    			} else {
        	    				formattedETADate = "";				
        	    			}
        	    			RequestContext.getCurrentInstance().execute("$('[id$=\\\\:" + index + "\\\\:"+ ETA_DATE_UI_ID +"]').text('" + formattedETADate + "')");

						}
    	    	        	
    	    		} else {
    	    			updateNoIssue = false;
    	    			if(unitsNotUpdated.length() >=1) {
    	    				unitsNotUpdated.append(",  ");
    	    			}
    	    			unitsNotUpdated.append(orderProgress.getUnitNo()); 
    	    		}
    			}
    	    	
    	    	if(updateNoIssue) {
    	    		addSuccessMessage("custom.message", "Update ETA done successfully");
    	    	} else {
    	    		if(unitsNotUpdated.length() > 1) {    				
        	    		addErrorMessage("custom.message", "Order Confirmation status does not exist so ETA was not updated for the following units: " + unitsNotUpdated);
        	    	}
    	    		if(unitsOlderClientDate.length() > 1) {    				
        	    		addErrorMessage("custom.message", "Calculated ETA is less than or equal to Client Requested Date so ETA was not updated for the following units " + unitsOlderClientDate);
        	    	}
    	    		if(unitsOlderETA.length() > 1) {    				
        	    		addErrorMessage("custom.message", "Calculated ETA is prior to Order Confirmation status so ETA was not updated for the following units: " + unitsOlderETA);
        	    	}
    	    		if(unitsWithAcquisitionType.length() > 1) {
    	    			addErrorMessage("custom.message", "The ETA for the following bailment, pool or financing-only unit(s) was not updated: " + unitsWithAcquisitionType);
    	    	}
    	    	}
        	} catch (MalException ex) {
        		isValid = false;
        		logger.error(ex , "Error while Update ETA operation");
        		addErrorMessageSummary("custom.message", ex.getMessage());
    		}
    	}
    	
    	if (!isValid) {
			RequestContext context = RequestContext.getCurrentInstance();
			context.addCallbackParam("failure", true);
		}
    }
    
	private void adjustDataTableAfterSearch(List<OrderProgressSearchResultVO> orderProgressSearchResults){
		if(isEquipmentSearch()) {
			initializeDataTable(620, 850, new int[] { 2, 5, 14, 6, 3, 4, 8, 20, 32, 6});
		} else {
			initializeDataTable(620, 850, new int[] { 2, 5, 20, 6, 3, 4, 8, 38, 8, 6});
		}
		
		adjustDataTableHeight(orderProgressSearchResults);
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

	public OrderProgressSearchCriteriaVO getSearchCriteria() {
		return searchCriteria;
	}

	public void setSearchCriteria(OrderProgressSearchCriteriaVO searchCriteria) {
		this.searchCriteria = searchCriteria;
	}

	public List<String> getYears() {
		return years;
	}

	public void setYears(List<String> years) {
		this.years = years;
	}

	public List<String> getMakes() {
		return makes;
	}

	public void setMakes(List<String> makes) {
		this.makes = makes;
	}

	public LazyDataModel<OrderProgressSearchResultVO> getLazyOrderProgressSearchResults() {
		return lazyOrderProgressSearchResults;
	}

	public void setLazyOrderProgressSearchResults(LazyDataModel<OrderProgressSearchResultVO> lazyOrderProgressSearchResults) {
		this.lazyOrderProgressSearchResults = lazyOrderProgressSearchResults;
	}

	public List<OrderProgressSearchResultVO> getSelectedResultVOs() {
		return selectedResultVOs;
	}

	public void setSelectedResultVOs(List<OrderProgressSearchResultVO> selectedResultVOs) {
		this.selectedResultVOs = selectedResultVOs;
	}

	public boolean isEquipmentSearch() {
		return equipmentSearch;
	}

	public void setEquipmentSearch(boolean equipmentSearch) {
		this.equipmentSearch = equipmentSearch;
	}

	public List<String> getSelectedUnitStandardEquipments() {
		return selectedUnitStandardEquipments;
	}

	public void setSelectedUnitStandardEquipments(List<String> selectedUnitStandardEquipments) {
		this.selectedUnitStandardEquipments = selectedUnitStandardEquipments;
	}

	public String getSelectedUnitFactoryOptionalEquipments() {
		return selectedUnitFactoryOptionalEquipments;
	}

	public void setSelectedUnitFactoryOptionalEquipments(String selectedUnitFactoryOptionalEquipments) {
		this.selectedUnitFactoryOptionalEquipments = selectedUnitFactoryOptionalEquipments;
	}

	public boolean isNoEquipment() {
		return noEquipment;
	}

	public void setNoEquipment(boolean noEquipment) {
		this.noEquipment = noEquipment;
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

	public Integer getDaysAdjusted() {
		return daysAdjusted;
	}

	public void setDaysAdjusted(Integer daysAdjusted) {
		this.daysAdjusted = daysAdjusted;
	}

	public String getAdjustmentNote() {
		return adjustmentNote;
	}

	public void setAdjustmentNote(String adjustmentNote) {
		this.adjustmentNote = adjustmentNote;
	}

	public boolean isStockOrder() {
		return stockOrder;
	}

	public void setStockOrder(boolean stockOrder) {
		this.stockOrder = stockOrder;
	}


}
