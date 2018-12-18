package com.mikealbert.vision.view.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.el.ValueExpression;

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
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.mikealbert.data.DataConstants;
import com.mikealbert.data.vo.RevisionVO;
import com.mikealbert.data.vo.VehicleSearchCriteriaVO;
import com.mikealbert.data.vo.VehicleSearchResultVO;
import com.mikealbert.service.QuotationService;
import com.mikealbert.service.VehicleSearchService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.util.XLSUtil;
import com.mikealbert.vision.view.ViewConstants;

@Component
@Scope("view")
public class ContractVehicleSearchBean extends StatefulBaseBean{

	private static final long serialVersionUID = -7955010902575469173L;
	
	@Resource private VehicleSearchService vehicleService;
	
	private LazyDataModel<VehicleSearchResultVO> vehicleLazyList  = null;	
	private VehicleSearchResultVO selectedSearchVO  = null;
	private int selectionIndex = -1;	
	private static final String  DATA_TABLE_UI_ID ="vehicleDataTable";
	private static String EXCEL_DL_COOKIE = "client.excel.download";
	
	// sort fields
	public static String FIELD_DRIVER_NAME = "driverForename";
	public static String FIELD_ACCOUNT_NAME = "accountName";
	public static String FIELD_UNIT_NO = "unitNo";
	private static List<String> REVISION_STATUS_CODE = Arrays.asList("10");
	
	private int resultPerPage = ViewConstants.RECORDS_PER_PAGE_SMALL;

	private VehicleSearchCriteriaVO searchCriteria = new VehicleSearchCriteriaVO();
	private String client;
	private boolean isSearchRequired = false; 
	private boolean returnedFromNextPage = false; //used when determining whether or not to select first row in datatable

	private String dataTableMessage;
	private Long scrollPosition;
	
	private List<VehicleSearchResultVO> vehicleList  = null;
	
	private List<Map<String, Object>> downloadableRowsData = new ArrayList<Map<String, Object>>();;
	private List<String> downloadableColumns = new ArrayList<String>();
	private Map<String, Object> nextPageValues = new HashMap<String, Object>();
	private Long revisionQmdId;
	private List<RevisionVO> revisionList;
	private RevisionVO selectedRevision;
	
	@Resource QuotationService quotationService;
	
	@PostConstruct
	public void init() {
		initializeDataTable(570, 850, new int[] {40, 24, 25, 40}).setHeight(0);
		openPage();
		
		// we always need the corporate entity of the logged in user for searching vehicles
		searchCriteria.setCorporateEntity(getLoggedInUser().getCorporateEntity());
		vehicleLazyList = new LazyDataModel<VehicleSearchResultVO>() {
			private static final long serialVersionUID = 1L;
				
			@Override
			public List<VehicleSearchResultVO> load(int first, int pageSize, String inputSortField, SortOrder inputSortOrder, Map<String, Object> arg4) {
								int pageIdx = (first == 0) ? 0 : (first / pageSize);
				PageRequest page = new PageRequest(pageIdx,pageSize);				
				Sort sort = null;
				String querySortField = getSortField(inputSortField);
				if(MALUtilities.isNotEmptyString(querySortField)){
					if (inputSortOrder.name().equalsIgnoreCase(SortOrder.DESCENDING.toString())) {
						 sort = new Sort(Sort.Direction.DESC, querySortField);
					}else{
						 sort = new Sort(Sort.Direction.ASC, querySortField);
					}
				}else{
					// for the UI if a sort is not set we want to sort by Unit Number, Driver Name and Client Name in ASC order
					sort = new Sort(Sort.Direction.ASC, DataConstants.VEHICLE_SEARCH_SORT_FIELD_UNIT_NO, DataConstants.VEHICLE_SEARCH_SORT_FIELD_DRIVER_NAME,DataConstants.VEHICLE_SEARCH_SORT_FIELD_ACCOUNT_NAME);
				}
				
				
				vehicleList  = getLazySearchResultList(page ,sort);
				return vehicleList;
			}

			@Override
			public VehicleSearchResultVO getRowData(String rowKey) {
				for (VehicleSearchResultVO searchVO : vehicleList) {
					if (String.valueOf(searchVO.getFmsId()).equals(rowKey))
						return searchVO;
				}
				return null;
			}

			@Override
			public Object getRowKey(VehicleSearchResultVO vehicleSearchResultVO) {
				return vehicleSearchResultVO.getFmsId();
			}
		};
		vehicleLazyList.setPageSize(ViewConstants.RECORDS_PER_PAGE);
	}

	/**
	 * Sets variables on entry to the page
	 */
	protected void loadNewPage() {	
		thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_CONTRACT_MGMT_VEH_SEARCH);
		thisPage.setPageUrl(ViewConstants.CONTRACT_MGMT_VEH_SEARCH);
		if(thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_DRIVER_SEARCH_REQ) != null){
			if(thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_DRIVER_SEARCH_REQ).equals("true")){
				isSearchRequired = true;
			}
			else{
				isSearchRequired = false;
			}
		}
	}

	public String cancel() {
		return super.cancelPage();
	}
	
	protected void restoreOldPage() {

		this.isSearchRequired = true;
		this.searchCriteria = (VehicleSearchCriteriaVO) thisPage.getRestoreStateValues().get("VEHICLE_SEARCH_CRITERIA");
		this.selectionIndex = (Integer) thisPage.getRestoreStateValues().get("SELECTD_INDEX");
		this.scrollPosition = (Long)thisPage.getRestoreStateValues().get("SCROLL_POSITION");
		this.setReturnedFromNextPage(true);
		this.getClientNameOrCode();

		DataTable dt = (DataTable) getComponent(DATA_TABLE_UI_ID);	
		Integer pageStartIndex = (Integer) thisPage.getRestoreStateValues().get("SELECTD_PAGE_START_INDEX");

		if(pageStartIndex != null){
			dt.setFirst(pageStartIndex);
			dt.setSortOrder((String) thisPage.getRestoreStateValues().get("DEFAULT_SORT_ORDER"));
			dt.setValueExpression("sortBy", (ValueExpression) thisPage.getRestoreStateValues().get("DEFAULT_SORT_VE"));
			this.selectedSearchVO = (VehicleSearchResultVO) thisPage.getRestoreStateValues().get(ViewConstants.DT_SELECTED_ITEM);
		}
	}
	
	//  We don't need to perform explicit search. The Primeface's LazyDataModel's load method will get call automatically 
	//	and will populate data.
	 
	public void performSearch() {
		
		this.isSearchRequired = true;
		this.selectionIndex = 0;
		this.selectedSearchVO = null;
		this.returnedFromNextPage = false;
		setClientNameOrCode();
		DataTable pfDataTable = ((DataTable) getComponent(DATA_TABLE_UI_ID));
		if(pfDataTable != null)
			pfDataTable.setFirst(0);
		scrollPosition = 0l;
	}
	
	private boolean validateSearchCriteria(){
		boolean valid = true;
		if(!MALUtilities.isEmpty(this.searchCriteria.getVIN())){
			if(this.searchCriteria.getVIN().length() <= 5){
				super.addErrorMessage("vin_minimum_characters");
				valid = false;
			}
		}
		
		return valid;
	}
	
	private List<VehicleSearchResultVO> getLazySearchResultList(PageRequest page, Sort sort){
		List<VehicleSearchResultVO> vehicleSearchResultsVOList =  new ArrayList<VehicleSearchResultVO>();
		if(isSearchRequired){
			if(validateSearchCriteria() == false){
				return vehicleSearchResultsVOList;
			}
			try {
					
				logger.info("-- Vehicle search start with criteria"+searchCriteria);
				
				searchCriteria.setContractVehicleSearch(true);
				vehicleSearchResultsVOList = vehicleService.findBySearchCriteria(searchCriteria , page, sort);
				int count  = vehicleService.findBySearchCriteriaCount(searchCriteria);
				selectionIndex =  (page.getPageSize() *  page.getPageNumber())+(selectionIndex % page.getPageSize());
				vehicleLazyList.setRowCount(count);
				vehicleLazyList.setWrappedData(vehicleSearchResultsVOList);
				adjustDataTableAfterSearch(vehicleSearchResultsVOList);
				
				logger.info("-- Vehicle search end with criteria"+searchCriteria);
			} catch (Exception ex) {
				super.addErrorMessage("generic.error", ex.getMessage());
			}			
		}

		return vehicleSearchResultsVOList;
	}
	
	public int getSelectionIndex() {
		return selectionIndex;
	}

	public void setSelectionIndex(int selectionIndex) {
		this.selectionIndex = selectionIndex;
	}
	
	private void adjustDataTableAfterSearch(List<VehicleSearchResultVO> vehicleSearchResultsVOList){
		adjustDataTableHeight(vehicleSearchResultsVOList);
	}	
	
	@SuppressWarnings("unchecked")
	public void setSelectedSearchVO(VehicleSearchResultVO selectedSearchVO) {
		this.selectedSearchVO = selectedSearchVO;
		selectionIndex = vehicleList.indexOf(this.selectedSearchVO);
		if(getComponent(DATA_TABLE_UI_ID) != null){
			selectionIndex = selectionIndex + ((DataTable) getComponent(DATA_TABLE_UI_ID)).getFirst();
		}
	}

	private String getSortField(String sortField) {
		if (VehicleSearchBean.FIELD_UNIT_NO.equalsIgnoreCase(sortField)) {
			return DataConstants.VEHICLE_SEARCH_SORT_FIELD_UNIT_NO;
		} else if (VehicleSearchBean.FIELD_DRIVER_NAME.equalsIgnoreCase(sortField)) {
			return DataConstants.VEHICLE_SEARCH_SORT_FIELD_DRIVER_NAME;
		} else if (VehicleSearchBean.FIELD_ACCOUNT_NAME.equalsIgnoreCase(sortField)) {
			return DataConstants.VEHICLE_SEARCH_SORT_FIELD_ACCOUNT_NAME;
		} else {
			return null;
		}
	}
	
	private Map<String, Object> getCurrentPageRestoreStateValuesMap() {
		
		Map<String, Object> restoreStateValues = new HashMap<String, Object>();
		if(MALUtilities.isEmpty(this.getClient()) ){
			this.searchCriteria.setClientAccountName("");
			this.searchCriteria.setClientAccountNumber("");
		}
		restoreStateValues.put("VEHICLE_SEARCH_CRITERIA", this.searchCriteria);
		restoreStateValues.put("SELECTED_VEHICLE", this.selectedSearchVO);
		restoreStateValues.put("SELECTD_INDEX", this.selectionIndex);
		restoreStateValues.put("SCROLL_POSITION", scrollPosition);
		
		DataTable dt = (DataTable) getComponent(DATA_TABLE_UI_ID);
		//this may happen because user may directly land to page
		if (dt != null) {
			ValueExpression ve = dt.getValueExpression("sortBy");
			restoreStateValues.put("DEFAULT_SORT_VE", ve);
			restoreStateValues.put("SELECTD_PAGE_START_INDEX", dt.getFirst());
			restoreStateValues.put("DEFAULT_SORT_ORDER", dt.getSortOrder());
			restoreStateValues.put(ViewConstants.DT_SELECTED_ITEM, selectedSearchVO);
		}
		return restoreStateValues;
		
	}
	
	public void setClientNameOrCode() {
		//Empty the SearchCriteria before each search
		searchCriteria.setClientAccountNumber("");
		searchCriteria.setClientAccountName("");
		if(MALUtilities.isNumber(this.client)){
			searchCriteria.setClientAccountNumber(this.client);
		}
		else if(!MALUtilities.isNumber(this.client) && !MALUtilities.isEmptyString(this.client)){
			searchCriteria.setClientAccountName(this.client);
		}
	}
	
	public void getClientNameOrCode(){
		if(!MALUtilities.isEmpty(searchCriteria.getClientAccountNumber())){
			this.client = searchCriteria.getClientAccountNumber();
		}
		else{
			this.client = searchCriteria.getClientAccountName();
		}
	}
	
	public void preProcessDownloadXLS(Object document) {

		downloadableRowsData.clear();
		downloadableColumns.clear();
		downloadableColumns.add("Unit No");
		downloadableColumns.add("Vehicle Description");
		downloadableColumns.add("VIN");
		downloadableColumns.add("Fleet Ref No");
		
		downloadableColumns.add("Driver Name");
		downloadableColumns.add("Driver Address");
		downloadableColumns.add("Driver Phone");
		
		downloadableColumns.add("Client Name");
		downloadableColumns.add("Client No");
		
		downloadableColumns.add("Unit Status");
		downloadableColumns.add("Product");
		downloadableColumns.add("Term");
		downloadableColumns.add("Miles");
		downloadableColumns.add("Contract Start Date");
		downloadableColumns.add("Contract End Date");
		
		try {
			List<VehicleSearchResultVO> vehicleSearchResultsVOListExcel =  new ArrayList<VehicleSearchResultVO>();
			PageRequest p = new PageRequest(0, Integer.MAX_VALUE);
			Sort s = new Sort(Sort.Direction.ASC, DataConstants.VEHICLE_SEARCH_SORT_FIELD_UNIT_NO, DataConstants.VEHICLE_SEARCH_SORT_FIELD_DRIVER_NAME,DataConstants.VEHICLE_SEARCH_SORT_FIELD_ACCOUNT_NAME);
	
			vehicleSearchResultsVOListExcel = vehicleService.findBySearchCriteria(searchCriteria, p, s);
	
			for (VehicleSearchResultVO object : vehicleSearchResultsVOListExcel) {
				Map<String, Object> row = new HashMap<String, Object>();
	
				row.put("Unit No", object.getUnitNo());
				row.put("Vehicle Description", object.getUnitDescription());
				row.put("VIN", object.getVIN());
				row.put("Fleet Ref No", object.getClientFleetReferenceNumber());
				
				row.put("Driver Name", object.getDriverFullNameDisplay());
				row.put("Driver Address", object.getDriverAddressDisplay().replaceAll("<br/>", "\n"));
				row.put("Driver Phone", object.getDriverPhoneDisplay());
				
				
				row.put("Client Name", object.getClientAccountName());
				row.put("Client No", object.getClientAccountNumber());
				
				row.put("Unit Status", object.getUnitStatus());
				row.put("Product", object.getProductName());
				row.put("Term", object.getContractTerm());
				row.put("Miles", object.getContractDistance());
				row.put("Contract Start Date", object.getContractStartDate());
				row.put("Contract End Date", object.getContractEndDate());
				downloadableRowsData.add(row);
			}
		} catch (Exception ex) {
			super.addErrorMessage("generic.error", ex.getMessage());
		}	
	}
	
	public void postProcessDownloadXLS(Object document) {
		HSSFWorkbook wb = (HSSFWorkbook) document;
		HSSFSheet sheet = wb.getSheetAt(0);
		sheet.shiftRows(0, sheet.getLastRowNum(), 1);

		Row titleRow = sheet.createRow(0);
		titleRow.setHeightInPoints(45);
		Cell titleCell1 = titleRow.createCell(0);
		titleCell1.setCellValue("Contract Vehicle Search");
		titleCell1.setCellStyle(XLSUtil.createStyle(wb, XLSUtil.TITLE));
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, downloadableColumns.size() - 1));
		Font boldFont = wb.createFont();
		boldFont.setBoldweight(Font.BOLDWEIGHT_BOLD);

		HSSFRow row;
		HSSFCell cell;
		CellStyle cellStyle;
		CellStyle cellStyleTitle = XLSUtil.createStyle(wb, XLSUtil.TITLE);
		CellStyle cellStyleHeader = XLSUtil.createStyle(wb, XLSUtil.HEADER);
		CellStyle cellStyleCell = XLSUtil.createStyle(wb, XLSUtil.CELL);
		for(int i = 0; i <= sheet.getLastRowNum(); i++) {
			boolean autoResize = false;
			row = sheet.getRow(i);
			if(i == 0) {
				cellStyle = cellStyleTitle;
				autoResize = false;
			} else if(i == 1) {
				cellStyle = cellStyleHeader;
				autoResize = true;
			} else {
				cellStyle = cellStyleCell;
				autoResize = false;
			}

			for(int j = 0; j < row.getPhysicalNumberOfCells(); j++) {
				cell = row.getCell(j);
				cell.setCellStyle(cellStyle);

				if(MALUtilities.isValidDate(cell.getStringCellValue())) {
					cell.setCellStyle(XLSUtil.createStyle(wb, XLSUtil.CELL_DATE));
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
	
	public String navigateToOpenEndRevision(){
		revisionQmdId = null;
		if(selectedSearchVO != null){
			
			if(quotationService.getLeaseType(selectedSearchVO.getQmdId()).equalsIgnoreCase(QuotationService.OPEN_END_LEASE)) {
				revisionList = quotationService.getQuotationModelsByQuoteStatus(selectedSearchVO.getQmdId(), REVISION_STATUS_CODE); 

				if(revisionList.size() == 0) {
					super.addErrorMessage("custom.message", "No revisions found");
				} else if (revisionList.size() == 1){
					revisionQmdId = revisionList.get(0).getQmdId();
					goToRevisionPage();
				} else {
					selectedRevision = revisionList.get(0);
					RequestContext.getCurrentInstance().execute("showDialog('revisionDialogVar');");
				}				
			} else {
				super.addErrorMessage("custom.message", "This unit is on a closed end contract and cannot be revised in Vision.");				
			}
			
		} else{
			super.addErrorMessage("custom.message", "Please select a unit");
		}
		
		return null;
	}

	public String navigateToCreateOpenEndRevision(){

		if(selectedSearchVO != null){
			try {
				quotationService.isQuoteEligibleForRevision(selectedSearchVO.getQmdId(), selectedSearchVO.getFmsId(), "R", getLoggedInUser().getEmployeeNo());
			} catch (Exception e) {
				super.addErrorMessage("custom.message", e.getMessage());
				return null;
			}

			saveRestoreStateValues(getCurrentPageRestoreStateValuesMap());

			nextPageValues.put(ViewConstants.VIEW_PARAM_QUOTE_MODEL_ID, selectedSearchVO.getQmdId());
			saveNextPageInitStateValues(nextPageValues);
			forwardToURL(ViewConstants.OPEN_END_REVISION);

		}else{
			super.addErrorMessage("custom.message", "Please select a unit.");
		}
		return null;

	}
		
	public void setRevisionAndForward() {
		if(selectedRevision != null) {
			revisionQmdId = selectedRevision.getQmdId();
			goToRevisionPage();			
		}
	}

	public void goToRevisionPage() {
		saveRestoreStateValues(getCurrentPageRestoreStateValuesMap());
		nextPageValues.put(ViewConstants.VIEW_PARAM_QUOTE_MODEL_ID, selectedSearchVO.getQmdId());
		nextPageValues.put(ViewConstants.VIEW_REV_PARAM_QUOTE_MODEL_ID, revisionQmdId);
		saveNextPageInitStateValues(nextPageValues);
		forwardToURL(ViewConstants.OPEN_END_REVISION);
		
	}
	
	
	public VehicleSearchResultVO getSelectedSearchVO() {
		return selectedSearchVO;
	}	
	
	public int getResultPerPage() {
		return resultPerPage;
	}

	public String getDataTableMessage() {
		return dataTableMessage;
	}

	public void setDataTableMessage(String dataTableMessage) {
		this.dataTableMessage = dataTableMessage;
	}
	public LazyDataModel<VehicleSearchResultVO> getVehicleLazyList() {
		return vehicleLazyList;
	}

	public void setVehicleLazyList(
			LazyDataModel<VehicleSearchResultVO> vehicleLazyList) {
		this.vehicleLazyList = vehicleLazyList;
	}
	
	public VehicleSearchCriteriaVO getSearchCriteria() {
		return searchCriteria;
	}

	public void setSearchCriteria(VehicleSearchCriteriaVO searchCriteria) {
		this.searchCriteria = searchCriteria;
	}

	public void setClient(String client) {
		this.client = client.trim();
	}
	
	public String getClient(){
		return client;
	}
	
	public boolean isReturnedFromNextPage() {
		return returnedFromNextPage;
	}

	public void setReturnedFromNextPage(boolean returnedFromNextPage) {
		this.returnedFromNextPage = returnedFromNextPage;
	}

	@SuppressWarnings("rawtypes")
	private void adjustDataTableHeight(List list){
		if(list != null){
			if(list.isEmpty()){
				this.setDataTableMessage(talMessage.getMessage("no.records.found"));
				super.getDataTable().setHeight(30);
			}else{
				if(list.size() > 0) {
					super.getDataTable().setMaximumHeight();
				}
			}
		}else{
			this.setDataTableMessage(talMessage.getMessage("no.records.found"));
			super.getDataTable().setHeight(30);
		}
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

	public List<VehicleSearchResultVO> getVehicleList() {
		return vehicleList;
	}

	public void setVehicleList(List<VehicleSearchResultVO> vehicleList) {
		this.vehicleList = vehicleList;
	}

	public Long getScrollPosition() {
		return scrollPosition;
	}

	public void setScrollPosition(Long scrollPosition) {
		this.scrollPosition = scrollPosition;
	}

	public Long getRevisionQmdId() {
		return revisionQmdId;
	}

	public void setRevisionQmdId(Long revisionQmdId) {
		this.revisionQmdId = revisionQmdId;
	}

	public List<RevisionVO> getRevisionList() {
		return revisionList;
	}

	public void setRevisionList(List<RevisionVO> revisionList) {
		this.revisionList = revisionList;
	}

	public RevisionVO getSelectedRevision() {
		return selectedRevision;
	}

	public void setSelectedRevision(RevisionVO selectedRevision) {
		this.selectedRevision = selectedRevision;
	}



}
