package com.mikealbert.vision.view.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.mikealbert.data.DataConstants;
import com.mikealbert.data.entity.DriverGrade;
import com.mikealbert.data.entity.Quotation;
import com.mikealbert.data.vo.QuotationSearchVO;
import com.mikealbert.service.DriverAllocationService;
import com.mikealbert.service.DriverGradeService;
import com.mikealbert.service.DriverRelationshipService;
import com.mikealbert.service.QuotationService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.view.ViewConstants;

@Component
@Scope("view")
public class UpdateGradeGroupBean extends StatefulBaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -486846646110314611L;
	
	@Resource
	DriverGradeService driverGradeService;
	@Resource
	private DriverAllocationService driverAllocService;
	@Resource
	private DriverRelationshipService driverRelationshipService;
	@Resource
	private QuotationService quotationService;

	private LazyDataModel<QuotationSearchVO> quotationUnitLazyList = null;
	private List<QuotationSearchVO> quotationSearchVOList = null;
	private QuotationSearchVO selectedSearchVO = null;
	private int selectionIndex = -1;
	private static final String DATA_TABLE_UI_ID = "quotationDataTable";
	private static final String MANAGE_PERMISSION_RESOURCE_NAME  = "updateGradeGroup";
	private static final String GRADE_GROUP_DESC_UI_ID = "gradeGroupDescId";
	private static final String NEW_GRADE_GROUP_UI_ID = "newGradeGroupId";
	private boolean hasManageGradeGroupPermission = false;
	private int resultPerPage = ViewConstants.RECORDS_PER_PAGE;
	private boolean isGradeGroupEditDisallowed = true;
	private String unitNo;
	private String vin;
	private String client;
	private boolean isSearchRequired = false;
	private List<DriverGrade> driverGrades;
	private DriverGrade selectedDriverGrade;
	private DriverGrade existingDriverGrade;
	private Quotation quotation = null;
	// sort fields
	public static final String FIELD_ACCOUNT_NAME = "accountName";
	public static final String FIELD_UNIT_NO = "unitNo";
	
	private String dataTableMessage;
	private long scrollPosition;

	@PostConstruct
	public void init() {
		initializeDataTable(550, 850, new int[] { 4, 32, 18, 8, 8, 12, 18 }).setHeight(0);
		openPage();
		setHasManageGradeGroupPermission(hasPermission(MANAGE_PERMISSION_RESOURCE_NAME));
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

				if(quotationList != null && quotationList.size() > 0) {
					if(MALUtilities.isEmpty(selectedSearchVO))
						selectedSearchVO = quotationList.get(0);
				}
				
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
		thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_UPDATE_GRADE_GROUP);
		thisPage.setPageUrl(ViewConstants.UPDATE_GRADE_GROUP);

		this.isSearchRequired = false;
		this.isGradeGroupEditDisallowed = true;
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
		this.isGradeGroupEditDisallowed = true;
		this.selectionIndex = 0;
		this.selectedSearchVO = null;
		this.selectedDriverGrade = null;

	}
	
	public String cancel() {
		return super.cancelPage();
	}
	
	private List<QuotationSearchVO> getLazySearchResultList(PageRequest page, Sort sort) {
		
		if (isSearchRequired) {
			try {
				quotationSearchVOList = quotationService.searchQuotations(getLoggedInUser().getCorporateEntity(), this.client, null, unitNo, vin, null, page, sort);
			} catch (Exception  ex) {
				handleException("generic.error.occured.while", new String[] { "searching quotation" }, ex, null);
			}
			int count = quotationService.searchQuotationsCount(getLoggedInUser().getCorporateEntity(), this.client, null, unitNo, vin, null);
			selectionIndex = page.getPageSize() * page.getPageNumber();
			quotationUnitLazyList.setRowCount(count);
			quotationUnitLazyList.setWrappedData(quotationSearchVOList);

			DataTable dt = (DataTable) getComponent(DATA_TABLE_UI_ID);	
			if (quotationSearchVOList != null && quotationSearchVOList.size()> 0 &&  dt != null) {
				if(MALUtilities.isEmpty(selectedSearchVO))
					dt.setSelection(quotationSearchVOList.get(0));
				else
					dt.setSelection(selectedSearchVO);
				isGradeGroupEditDisallowed = false;
			} else {
				isGradeGroupEditDisallowed = true;
			}
			adjustDataTableAfterSearch(quotationSearchVOList);

		}
		return quotationSearchVOList;
	}

	public void setItemsOnDialog() {
		if (selectedSearchVO != null && selectedSearchVO.getQuoId() != null) {
			try {
				setQuotation(quotationService.getQuote(selectedSearchVO.getQuoId()));
			} catch (Exception ex) {
				handleException("generic.error.occured.while", new String[] { "searching quotation details" }, ex, null);
			}
			setDriverGrades(null);
			setSelectedDriverGrade(null);
			setDriverGrades(driverGradeService.getExternalAccountDriverGrades(quotation.getExternalAccount()));
		}
		saveRestoreStateValues(getCurrentPageRestoreStateValuesMap());
	}

	public void updateQuotationGradeGroup() {
		RequestContext context;
		context = RequestContext.getCurrentInstance();
		
		if (MALUtilities.isEmpty(selectedDriverGrade)) {
			super.addErrorMessageSummary(NEW_GRADE_GROUP_UI_ID, "custom.message", "Grade group must be selected");
			context.addCallbackParam("failure", true);
			
		} else if (!MALUtilities.isEmpty(selectedSearchVO.getGradeGroupCode()) &&  selectedSearchVO.getGradeGroupCode().equalsIgnoreCase(selectedDriverGrade.getGradeCode())) { 
			super.addErrorMessageSummary(NEW_GRADE_GROUP_UI_ID, "custom.message", "Please choose different grade group");
			context.addCallbackParam("failure", true);

			//Check Quote Status is not either in pending amendment (formal or informal), pending revision or formal lease extension. 
			//For formal lease extension, a status of "On Offer" is part of this requirement as well. Otherwise Update Quotation.
		} else if (quotationService.isQuoteStatusExistsInQmd(selectedSearchVO.getUnitNo())) {
			addErrorMessageSummary("custom.message", " You cannot change the Grade Group when the unit has either pending Amendment, Revision or Formal Lease Extension");
			context.addCallbackParam("failure", true);			

		} else {
			getQuotation().setDriverGradeGroup(selectedDriverGrade.getGradeCode());
			quotationService.updateQuotation(getQuotation());
			int index = -1;
			for(QuotationSearchVO quotationSearchVO : quotationSearchVOList) {
				index +=1;
				if(quotationSearchVO.getUnitNo().equals(selectedSearchVO.getUnitNo())) {
					break;
				}
			}
			selectedSearchVO.setGradeGroupCode(selectedDriverGrade.getGradeCode());
			selectedSearchVO.setGradeGroupDescription(selectedDriverGrade.getGradeDesc());
			context.execute("$('[id$=\\\\:" + index + "\\\\:"+ GRADE_GROUP_DESC_UI_ID +"]').text('" + selectedDriverGrade.getGradeDesc() + "')");
			addSuccessMessage("custom.message", "Grade Group updated successfully");
		}
	}

	public void onRowSelect(SelectEvent event) {
		if (selectedSearchVO.getUnitNo() != null) {
			isGradeGroupEditDisallowed = false;
		} else {
			isGradeGroupEditDisallowed = true;
		}
	}

	public void onRowUnSelect(UnselectEvent event) {
		isGradeGroupEditDisallowed = true;
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
		if (UpdateGradeGroupBean.FIELD_UNIT_NO.equalsIgnoreCase(sortField)) {
			return DataConstants.VEHICLE_SEARCH_SORT_FIELD_UNIT_NO;
		} else if (UpdateGradeGroupBean.FIELD_ACCOUNT_NAME.equalsIgnoreCase(sortField)) {
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

	public String getVin() {
		return vin;
	}

	public void setVin(String vin) {
		this.vin = vin;
	}

	public QuotationSearchVO getSelectedSearchVO() {
		return selectedSearchVO;
	}

	
	public void setSelectedSearchVO(QuotationSearchVO selectedSearchVO) {
		this.selectedSearchVO = selectedSearchVO;
	}

	
	public int getResultPerPage() {
		return resultPerPage;
	}

	public boolean isGradeGroupEditDisallowed() {
		if (hasManageGradeGroupPermission) {
			return isGradeGroupEditDisallowed;
		} else {
			return true;
		}		
	}

	public void setGradeGroupEditDisallowed(boolean isGradeGroupEditDisallowed) {
			this.isGradeGroupEditDisallowed = isGradeGroupEditDisallowed;
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

	public List<DriverGrade> getDriverGrades() {
		return driverGrades;
	}

	public void setDriverGrades(List<DriverGrade> driverGrades) {
		this.driverGrades = driverGrades;
	}

	public DriverGrade getSelectedDriverGrade() {
		return selectedDriverGrade;
	}

	public void setSelectedDriverGrade(DriverGrade selectedDriverGrade) {
		this.selectedDriverGrade = selectedDriverGrade;
	}

	public DriverGrade getExistingDriverGrade() {
		return existingDriverGrade;
	}

	public void setExistingDriverGrade(DriverGrade existingDriverGrade) {
		this.existingDriverGrade = existingDriverGrade;
	}

	public Quotation getQuotation() {
		return quotation;
	}

	public void setQuotation(Quotation quotation) {
		this.quotation = quotation;
	}

	public boolean isHasManageGradeGroupPermission() {
		return hasManageGradeGroupPermission;
	}

	public void setHasManageGradeGroupPermission(boolean hasManageGradeGroupPermission) {
		this.hasManageGradeGroupPermission = hasManageGradeGroupPermission;
	}

	public long getScrollPosition() {
		return scrollPosition;
	}

	public void setScrollPosition(long scrollPosition) {
		this.scrollPosition = scrollPosition;
	}

}
