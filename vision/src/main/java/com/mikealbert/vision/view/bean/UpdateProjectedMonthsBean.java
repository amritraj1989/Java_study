package com.mikealbert.vision.view.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleSelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.mikealbert.data.DataConstants;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.vo.QuotationSearchVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.QuotationService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.view.ViewConstants;

@Component
@Scope("view")
public class UpdateProjectedMonthsBean extends StatefulBaseBean {

	private static final long serialVersionUID = -6792240632242641902L;

	@Resource
	private QuotationService quotationService;
	
	private int selectionIndex = -1;	
	private LazyDataModel<QuotationSearchVO> quotationUnitLazyList = null;
	private List<QuotationSearchVO> quotationSearchVOList = null;
	private List<QuotationSearchVO> selectedResultVOs = null; 
	private static final String DATA_TABLE_UI_ID = "quotationDataTable";
	private static final String MANAGE_PERMISSION_RESOURCE_NAME  = "updateProjectedMonths";
	private static final String NEW_PROJ_MONTHS_UI_ID = "newProjectedMonthsId";
	private boolean hasManageProjectedMonthsPermission = false;
	private int resultPerPage = ViewConstants.RECORDS_PER_PAGE;
	private String unitNo;
	private String vin;
	private String client;
	private Long newProjectedMonths;
	private Long projectedMonths;
	private boolean isSearchRequired = false;
	private boolean updateButtonDisabled = true;
	private List<QuotationModel> quotationModels = null;
	private static String PRODUCT_TYPE_OE = "O";
	private static String PRODUCT_TYPE_PUR = "A";
	private List<String> productTypes;
	private String dataTableMessage;
	private long scrollPosition;

	// sort fields
	public static final String FIELD_ACCOUNT_NAME = "accountName";
	public static final String FIELD_UNIT_NO = "unitNo";

	
	@PostConstruct
	public void init() {
		initializeDataTable(550, 850, new int[] {2, 40, 22, 12, 10, 6, 8 }).setHeight(0);
		openPage();
		setHasManageProjectedMonthsPermission(hasPermission(MANAGE_PERMISSION_RESOURCE_NAME));
		quotationUnitLazyList = new LazyDataModel<QuotationSearchVO>() {
			private static final long serialVersionUID = 1L;

			@Override
			public List<QuotationSearchVO> load(int first, int pageSize, String inputSortField,
					SortOrder inputSortOrder, Map<String, Object> arg4) {
				int pageIdx = (first == 0) ? 0 : (first / pageSize);
				PageRequest page = new PageRequest(pageIdx, pageSize);
				Sort sort = null;
				String querySortField = getSortField(inputSortField);
				if(MALUtilities.isNotEmptyString(querySortField)){
					if (inputSortOrder.name().equalsIgnoreCase(SortOrder.DESCENDING.toString())) {
						 sort = new Sort(Sort.Direction.DESC, querySortField);
					}else{
						 sort = new Sort(Sort.Direction.ASC, querySortField);
					}
				}else{
					// for the UI if a sort is not set we want to sort by Unit Number and Client Short Name in ASC order
					sort = new Sort(Sort.Direction.ASC, DataConstants.VEHICLE_SEARCH_SORT_FIELD_UNIT_NO, DataConstants.VEHICLE_SEARCH_SORT_FIELD_ACCOUNT_NAME);
				}
				
				List<QuotationSearchVO> quotationList = null;
				quotationList = getLazySearchResultList(page, sort);
				return quotationList;
			}

			@Override
			public QuotationSearchVO getRowData(String rowKey) {
				for (QuotationSearchVO searchVO : quotationSearchVOList) {
					if (String.valueOf(searchVO.getUnitNo()).equals(rowKey))
						return searchVO;
				}
				return null;
			}

			@Override
			public Object getRowKey(QuotationSearchVO quotationSearchVO) {
				return quotationSearchVO.getUnitNo();
			}
		};
		quotationUnitLazyList.setPageSize(ViewConstants.RECORDS_PER_PAGE);

	}

	protected void loadNewPage() {
		thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_UPDATE_PROJECTED_MONTHS);
		thisPage.setPageUrl(ViewConstants.UPDATE_PROJECTED_MONTHS);

		this.isSearchRequired = false;
		this.setUpdateButtonDisabled(true);
	}

	@Override
	protected void restoreOldPage() {
		this.scrollPosition = (Long)thisPage.getRestoreStateValues().get("SCROLL_POSITION");
	}
	private Map<String, Object> getCurrentPageRestoreStateValuesMap() {
		Map<String, Object> restoreStateValues = new HashMap<String, Object>();
		restoreStateValues.put("SCROLL_POSITION", scrollPosition);
		return restoreStateValues;
	}

	public void performSearch() {
		this.isSearchRequired = true;
		this.selectionIndex = 0;
		this.setSelectedResultVOs(null);
		this.setUpdateButtonDisabled(true);
		DataTable pfDataTable = ((DataTable) getComponent(DATA_TABLE_UI_ID));
		if(pfDataTable != null)
			pfDataTable.setFirst(0);		
	}	
	
	private List<QuotationSearchVO> getLazySearchResultList(PageRequest page, Sort sort) {
		
		if (isSearchRequired) {
			setProductTypes(new ArrayList<String>());
			getProductTypes().clear();
			getProductTypes().add(PRODUCT_TYPE_OE);
			getProductTypes().add(PRODUCT_TYPE_PUR);
			
			try {
				quotationSearchVOList = quotationService.searchQuotations(getLoggedInUser().getCorporateEntity(), this.client, productTypes, unitNo, vin, projectedMonths, page, sort);
			} catch (Exception  ex) {
				handleException("generic.error.occured.while", new String[] { "searching quotation" }, ex, null);
			}
			int count = quotationService.searchQuotationsCount(getLoggedInUser().getCorporateEntity(), this.client, productTypes, unitNo, vin, projectedMonths);
			selectionIndex = page.getPageSize() * page.getPageNumber();
			quotationUnitLazyList.setRowCount(count);
			adjustDataTableAfterSearch(quotationSearchVOList);

		}
		return quotationSearchVOList;
	}

	public void setItemsOnDialog() {
		setNewProjectedMonths(null);
		saveRestoreStateValues(getCurrentPageRestoreStateValuesMap());
	}

	public void updateRecords() {

		if (MALUtilities.isEmpty(this.getNewProjectedMonths())) {
			super.addErrorMessageSummary(NEW_PROJ_MONTHS_UI_ID, "required.field", "New Proj. Repl. Month");
			RequestContext.getCurrentInstance().addCallbackParam("failure", true);
		} else if (this.getNewProjectedMonths() <= 0 ) {
			super.addErrorMessageSummary(NEW_PROJ_MONTHS_UI_ID, "custom.message", "New Proj. Repl. Month must be greater than 0");
			RequestContext.getCurrentInstance().addCallbackParam("failure", true);
		} else {

			for (QuotationSearchVO selectedQuote : selectedResultVOs) {
				try {
					quotationService.updateQmdProjectedMonth(selectedQuote.getQuoId(), selectedQuote.getQmdId(), this.getNewProjectedMonths());
				} catch (Exception e) {
					handleException("generic.error.occured.while", new String[] { "updating projected replacement month" }, e, null);
				}
			}
			RequestContext.getCurrentInstance().update(DATA_TABLE_UI_ID);
			addSuccessMessage("custom.message", "Projected Replacement Month updated successfully");
			this.setSelectedResultVOs(null);
		}
	}

	public void onRowSelect(SelectEvent event) {
		setUpdateButtonDisabled(getSelectedResultVOs().size() == 1 ? true : false);
	}

	public void onRowUnSelect(UnselectEvent event) {
		setUpdateButtonDisabled(getSelectedResultVOs().size() == 1 ? false : true);		
	}
	
	public void onRowSelect(ToggleSelectEvent event) {
		setUpdateButtonDisabled(getSelectedResultVOs().size() == 1 ? true : false);
	}	

	public int getSelectionIndex() {
		return selectionIndex;
	}

	public void setSelectionIndex(int selectionIndex) {
		this.selectionIndex = selectionIndex;
	}

	private void adjustDataTableAfterSearch(List<QuotationSearchVO> quotationSearchVOList) {
		if (quotationSearchVOList.size() > 0) {
			super.getDataTable().setMaximumHeight();
		} else {
			this.setDataTableMessage(talMessage.getMessage("no.records.found"));
			super.getDataTable().setHeight(30);
		}
	}

	
	private String getSortField(String sortField) {
		if (UpdateProjectedMonthsBean.FIELD_UNIT_NO.equalsIgnoreCase(sortField)) {
			return DataConstants.VEHICLE_SEARCH_SORT_FIELD_UNIT_NO;
		} else if (UpdateProjectedMonthsBean.FIELD_ACCOUNT_NAME.equalsIgnoreCase(sortField)) {
			return DataConstants.VEHICLE_SEARCH_SORT_FIELD_ACCOUNT_NAME;
		} else {
			return null;
		}
	}


	public String getUnitNo() {
		return unitNo;
	}

	public void setUnitNo(String unitNo) {
		this.unitNo = unitNo;
	}

	public Long getProjectedMonths() {
		return projectedMonths;
	}

	public void setProjectedMonths(Long projectedMonths) {
		this.projectedMonths = projectedMonths;
	}

	public Long getNewProjectedMonths() {
		return newProjectedMonths;
	}

	public void setNewProjectedMonths(Long newProjectedMonths) {
		this.newProjectedMonths = newProjectedMonths;
	}

	public String getVin() {
		return vin;
	}

	public void setVin(String vin) {
		this.vin = vin;
	}

	public int getResultPerPage() {
		return resultPerPage;
	}

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}

	public String getDataTableMessage() {
		return dataTableMessage;
	}

	public void setDataTableMessage(String dataTableMessage) {
		this.dataTableMessage = dataTableMessage;
	}

	public LazyDataModel<QuotationSearchVO> getQuotationUnitLazyList() {
		return quotationUnitLazyList;
	}

	public void setQuotationUnitLazyList(LazyDataModel<QuotationSearchVO> quotationUnitLazyList) {
		this.quotationUnitLazyList = quotationUnitLazyList;
	}

	public boolean isHasManageProjectedMonthsPermission() {
		return hasManageProjectedMonthsPermission;
	}

	public boolean isUpdateButtonDisabled() {
		if (hasManageProjectedMonthsPermission && (!MALUtilities.isEmpty(getSelectedResultVOs()) && getSelectedResultVOs().size() > 0)) {
			return true;
		} else {
			return false;
		}
	}

	public void setUpdateButtonDisabled(boolean updateButtonDisabled) {
		this.updateButtonDisabled = updateButtonDisabled;
	}

	public void setHasManageProjectedMonthsPermission(boolean hasManageProjectedMonthsPermission) {
		this.hasManageProjectedMonthsPermission = hasManageProjectedMonthsPermission;
	}

	public long getScrollPosition() {
		return scrollPosition;
	}

	public void setScrollPosition(long scrollPosition) {
		this.scrollPosition = scrollPosition;
	}

	public List<QuotationSearchVO> getQuotationSearchVOList() {
		return quotationSearchVOList;
	}

	public void setQuotationSearchVOList(List<QuotationSearchVO> quotationSearchVOList) {
		this.quotationSearchVOList = quotationSearchVOList;
	}

	public List<QuotationSearchVO> getSelectedResultVOs() {
		return selectedResultVOs;
	}

	public void setSelectedResultVOs(List<QuotationSearchVO> selectedResultVOs) {
		this.selectedResultVOs = selectedResultVOs;
	}

	public List<QuotationModel> getQuotationModels() {
		return quotationModels;
	}

	public void setQuotationModels(List<QuotationModel> quotationModels) {
		this.quotationModels = quotationModels;
	}

	public List<String> getProductTypes() {
		return productTypes;
	}

	public void setProductTypes(List<String> productTypes) {
		this.productTypes = productTypes;
	}

}