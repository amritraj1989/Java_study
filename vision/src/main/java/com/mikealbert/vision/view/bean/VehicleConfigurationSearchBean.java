package com.mikealbert.vision.view.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.el.ValueExpression;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.mikealbert.data.dao.ExternalAccountDAO;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.vo.VehicleConfigurationSearchCriteriaVO;
import com.mikealbert.data.vo.VehicleConfigurationSearchResultVO;
import com.mikealbert.service.CustomerAccountService;
import com.mikealbert.service.UpfitterService;
import com.mikealbert.service.VehicleConfigurationService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.service.OrderProgressService;
import com.mikealbert.vision.view.ViewConstants;

@Component
@Scope("view")
public class VehicleConfigurationSearchBean extends StatefulBaseBean {
	private static final long serialVersionUID = 2648782845342677114L;
	
	@Resource private OrderProgressService orderProgressService;
	@Resource CustomerAccountService customerAccountService;
	@Resource UpfitterService upfitterService;
	@Resource VehicleConfigurationService vehicleConfigurationService;

	
	private VehicleConfigurationSearchCriteriaVO searchCriteria;
	private List<String> years;
	private List<String> makes;
	private LazyDataModel<VehicleConfigurationSearchResultVO> lazyVehicleConfigurationSearchResults = null;	
	private VehicleConfigurationSearchResultVO selectedSearchResultVO = null;
	private boolean isSearchRequired = false;
	  
	private String dataTableMessage;
	private int resultPerPage = ViewConstants.RECORDS_PER_PAGE_MEDIUM;
	private static final String DATA_TABLE_UI_ID ="searchResultDataTable";
	private static final String MANAGE_PERMISSION_RESOURCE_NAME  = "manageVehConfig";
	private boolean hasManageVehConfigPermission;
	
	private List<VehicleConfigurationSearchResultVO> vehicleConfigurationSearchResults  = null;

	@PostConstruct
	public void init() {
		setHasManageVehConfigPermission(hasPermission(MANAGE_PERMISSION_RESOURCE_NAME));
		initializeDataTable(620, 850, new int[] {10, 8, 5, 5, 4, 2, 14, 10, 2, 3}).setHeight(30);
		openPage();
			
		setYears(new ArrayList<String>());
		setMakes(new ArrayList<String>());
		setYears(vehicleConfigurationService.findDistinctYears(new VehicleConfigurationSearchCriteriaVO()));
		setMakes(vehicleConfigurationService.findDistinctMakes(new VehicleConfigurationSearchCriteriaVO()));
		
		setLazyVehicleConfigurationSearchResults(new LazyDataModel<VehicleConfigurationSearchResultVO>() {
			private static final long serialVersionUID = -7649338186972369712L;

			@Override
			public List<VehicleConfigurationSearchResultVO> load(int first, int pageSize, String sortBy, SortOrder sortOrder, Map<String, Object> arg4) {
				int pageIdx = (first == 0) ? 0 : (first / pageSize);
				PageRequest page = new PageRequest(pageIdx, pageSize);				
				Sort sort = null;
				
				if(MALUtilities.isNotEmptyString(sortBy)){
					if (sortOrder.name().equalsIgnoreCase(SortOrder.DESCENDING.toString())) {
						 sort = new Sort(Sort.Direction.DESC, vehicleConfigurationService.resolveSortByName(sortBy));
					}else{
						 sort = new Sort(Sort.Direction.ASC, vehicleConfigurationService.resolveSortByName(sortBy));
					}
				}
				
				vehicleConfigurationSearchResults  = getLazySearchResultList(page ,sort);
				return vehicleConfigurationSearchResults;
			}

			@Override
			public VehicleConfigurationSearchResultVO getRowData(String rowKey) {
				for (VehicleConfigurationSearchResultVO resultVO : vehicleConfigurationSearchResults) {
					if (String.valueOf(resultVO.getVehicleConfigId()).equals(rowKey))
						return resultVO;
				}
				return null;
			}

			@Override
			public Object getRowKey(VehicleConfigurationSearchResultVO vehicleConfigurationSearchResultVO) {
				return vehicleConfigurationSearchResultVO.getVehicleConfigId();
			}
		});
		getLazyVehicleConfigurationSearchResults().setPageSize(getResultPerPage());
	}
	
	public void changeYear(){
		setMakes(new ArrayList<String>());
		//setMakes(vehicleConfigurationService.findDistinctMakes(getSearchCriteria()));
	}
	
	protected void loadNewPage() {	
		thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_CONFIGURATION_SEARCH);
		thisPage.setPageUrl(ViewConstants.CONFIGURATION_SEARCH);
		
		setSearchCriteria(new VehicleConfigurationSearchCriteriaVO());
		getSearchCriteria().setClientCid(getLoggedInUser().getCorporateEntity().getCorpId());
		getSearchCriteria().setClientAccountType(ExternalAccountDAO.ACCOUNT_TYPE_CUSTOMER);
		getSearchCriteria().setVendorCid(getLoggedInUser().getCorporateEntity().getCorpId());
		getSearchCriteria().setVendorAccountType(ExternalAccountDAO.ACCOUNT_TYPE_SUPPLIER);
		this.isSearchRequired = false;
	}

	protected void restoreOldPage() {
		this.isSearchRequired =  (Boolean) thisPage.getRestoreStateValues().get("SEARCH_REQUIRED");
		setSearchCriteria((VehicleConfigurationSearchCriteriaVO) thisPage.getRestoreStateValues().get("SEARCH_CRITERIA"));
		DataTable dt = (DataTable) getComponent(DATA_TABLE_UI_ID);	
		Integer pageStartIndex = (Integer) thisPage.getRestoreStateValues().get(ViewConstants.DT_SELECTD_PAGE_START_INDEX);
		//this case may happen if user  directly landed to driver add/edit page
		if(pageStartIndex != null){
			dt.setFirst(pageStartIndex);
			dt.setSortOrder((String) thisPage.getRestoreStateValues().get(ViewConstants.DT_DEFAULT_SORT_ORDER));
			dt.setValueExpression("sortBy", (ValueExpression) thisPage.getRestoreStateValues().get(ViewConstants.DT_DEFAULT_SORT_VE));
			this.selectedSearchResultVO = (VehicleConfigurationSearchResultVO) thisPage.getRestoreStateValues().get(ViewConstants.DT_SELECTED_ITEM);
		}
	}
	
	public String cancel() {
		return super.cancelPage();
	}
	
	public List<String> autoCompleteClientListener(String criteria){
		List<ExternalAccount> clientList = new ArrayList<ExternalAccount>();
		List<String> clients = new ArrayList<String>();
				
		clientList = vehicleConfigurationService.findVehicleConfigAccountByNameOrCode(getLoggedInUser().getCorporateEntity().getCorpId(), ExternalAccountDAO.ACCOUNT_TYPE_CUSTOMER, criteria,  new PageRequest(0,50));
		
		for (ExternalAccount ea : clientList) {
			String client = ea.getAccountName().concat(" - ").concat(ea.getExternalAccountPK().getAccountCode());
			clients.add(client);
		}
		
		if(!clients.isEmpty()){
			Collections.sort(clients);	    	
	    }
		
	    return clients;
	}
	
	public List<String> autoCompleteVendorsListener(String criteria){
		List<ExternalAccount> vendorList = new ArrayList<ExternalAccount>();
		List<String> vendors = new ArrayList<String>();
		
		vendorList = vehicleConfigurationService.findVehicleConfigAccountByNameOrCode(getLoggedInUser().getCorporateEntity().getCorpId(), ExternalAccountDAO.ACCOUNT_TYPE_SUPPLIER, criteria,  new PageRequest(0,50));
		
		for (ExternalAccount ea : vendorList) {
			String vendor = ea.getAccountName().concat(" - ").concat(ea.getExternalAccountPK().getAccountCode());
			vendors.add(vendor);
		}
		
		if(!vendors.isEmpty()){
			Collections.sort(vendors);	    	
	    }
		
		return vendors;
	}
	
	public List<String> autoCompleteMfgCodeListener(String criteria){
		List<String> mfgCodes = new ArrayList<String>();
		
		mfgCodes = vehicleConfigurationService.getVehicleConfigMfgCodes(criteria, new PageRequest(0,50));
	    if(!mfgCodes.isEmpty()){
			Collections.sort(mfgCodes);	    	
	    }
		
		return mfgCodes;
	}
	
	public List<String> autoCompleteTrimListener(String criteria){
		List<String> trims = new ArrayList<String>();
		
		trims = vehicleConfigurationService.getVehicleConfigTrims(criteria, new PageRequest(0,50));
	    if(!trims.isEmpty()){
			Collections.sort(trims);	    	
	    }
		
		return trims;
	}
	
	public void performSearch() {
		this.isSearchRequired = true;
		this.selectedSearchResultVO = null;
		DataTable pfDataTable = ((DataTable) getComponent(DATA_TABLE_UI_ID));
		if(pfDataTable != null)
			pfDataTable.setFirst(0);
	}
	
	private List<VehicleConfigurationSearchResultVO> getLazySearchResultList(PageRequest page, Sort sort){
		List<VehicleConfigurationSearchResultVO> vehicleConfigurationSearchResult =  new ArrayList<VehicleConfigurationSearchResultVO>();
		if(isSearchRequired) {
			try {
				if (!MALUtilities.isEmpty(searchCriteria.getClientAccountCode())) {
					getSearchCriteria().setClientAccountCode(searchCriteria.getClientAccountCode().substring(searchCriteria.getClientAccountCode().length()-8, searchCriteria.getClientAccountCode().length()));
				}
				
				if (!MALUtilities.isEmpty(searchCriteria.getVendorAccountCode())) {
					getSearchCriteria().setVendorAccountCode(searchCriteria.getVendorAccountCode().substring(searchCriteria.getVendorAccountCode().length()-8, searchCriteria.getVendorAccountCode().length()));
				}
				
				vehicleConfigurationSearchResult = vehicleConfigurationService.findBySearchCriteria(searchCriteria, page, sort);
				int count  = vehicleConfigurationService.findBySearchCriteriaCount(searchCriteria);
				getLazyVehicleConfigurationSearchResults().setRowCount(count);
				getLazyVehicleConfigurationSearchResults().setWrappedData(vehicleConfigurationSearchResult);
				adjustDataTableAfterSearch(vehicleConfigurationSearchResult);
				
			} catch (Exception ex) {
				super.addErrorMessage("generic.error", ex.getMessage());
			}	
		}
		return vehicleConfigurationSearchResult;
	}
	      
    private void adjustDataTableAfterSearch(List<VehicleConfigurationSearchResultVO> modelSearchResults){
		initializeDataTable(620, 850, new int[] {10, 8, 5, 5, 4, 2, 14, 10, 2, 3});
		adjustDataTableHeight(modelSearchResults);
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
	public void addConfiguration(){		
		navigateToAddEditConfiguration(null);		
	}
	
	 public void editConfiguration(){
		if(selectedSearchResultVO != null && selectedSearchResultVO.getVehicleConfigId() != null){
			navigateToAddEditConfiguration(selectedSearchResultVO.getVehicleConfigId());
		}
    }    
    
    private void navigateToAddEditConfiguration(Long vehicleConfigId){
    	saveRestoreStateValues(getCurrentPageRestoreStateValuesMap());
    	Map<String, Object> nextPageValues = new HashMap<String, Object>();
    	if(vehicleConfigId != null) {
    		nextPageValues.put(ViewConstants.VIEW_PARAM_VEHICLE_CONFIG_ID, String.valueOf(vehicleConfigId));
    	}
    	saveNextPageInitStateValues(nextPageValues);
		forwardToURL(ViewConstants.VEHICLE_CONFIGURATION_ADD);
    }

    private Map<String, Object> getCurrentPageRestoreStateValuesMap() {
		
		Map<String, Object> restoreStateValues = new HashMap<String, Object>();
		restoreStateValues.put("SEARCH_CRITERIA", searchCriteria);
		restoreStateValues.put("SEARCH_REQUIRED", isSearchRequired);
		
		DataTable dt = (DataTable) getComponent(DATA_TABLE_UI_ID);
		//this may happen because user may directly land to driver add/edit page
		if (dt != null) {
			ValueExpression ve = dt.getValueExpression("sortBy");
			restoreStateValues.put(ViewConstants.DT_DEFAULT_SORT_VE, ve);
			restoreStateValues.put(ViewConstants.DT_SELECTD_PAGE_START_INDEX, dt.getFirst());
			restoreStateValues.put(ViewConstants.DT_DEFAULT_SORT_ORDER, dt.getSortOrder());
			restoreStateValues.put(ViewConstants.DT_SELECTED_ITEM, selectedSearchResultVO);
		}
		return restoreStateValues;
		
	}
	

	public VehicleConfigurationSearchCriteriaVO getSearchCriteria() {
		return searchCriteria;
	}

	public void setSearchCriteria(VehicleConfigurationSearchCriteriaVO searchCriteria) {
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

	public LazyDataModel<VehicleConfigurationSearchResultVO> getLazyVehicleConfigurationSearchResults() {
		return lazyVehicleConfigurationSearchResults;
	}

	public void setLazyVehicleConfigurationSearchResults(
			LazyDataModel<VehicleConfigurationSearchResultVO> lazyVehicleConfigurationSearchResults) {
		this.lazyVehicleConfigurationSearchResults = lazyVehicleConfigurationSearchResults;
	}

	public VehicleConfigurationSearchResultVO getSelectedSearchResultVO() {
		return selectedSearchResultVO;
	}

	public void setSelectedSearchResultVO(VehicleConfigurationSearchResultVO selectedSearchResultVO) {
		this.selectedSearchResultVO = selectedSearchResultVO;
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

	public boolean isHasManageVehConfigPermission() {
		return hasManageVehConfigPermission;
	}

	public void setHasManageVehConfigPermission(boolean hasManageVehConfigPermission) {
		this.hasManageVehConfigPermission = hasManageVehConfigPermission;
	}

}
