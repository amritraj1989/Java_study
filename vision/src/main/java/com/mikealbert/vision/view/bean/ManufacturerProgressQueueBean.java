package com.mikealbert.vision.view.bean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleSelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.event.data.PageEvent;
import org.primefaces.event.data.SortEvent;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mikealbert.data.entity.Doc;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.ProcessStageObject;
import com.mikealbert.data.enumeration.LogBookTypeEnum;
import com.mikealbert.data.enumeration.OrderToDeliveryProcessStageEnum;
import com.mikealbert.data.vo.ManufacturerProgressQueueVO;
import com.mikealbert.data.vo.ManufacturerProgressSearchCriteriaVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.ClientPOCService;
import com.mikealbert.service.DocumentService;
import com.mikealbert.service.FleetMasterService;
import com.mikealbert.service.LogBookService;
import com.mikealbert.service.ProcessStageService;
import com.mikealbert.service.UpfitterService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.service.ManufacturerProgressQueueService;
import com.mikealbert.vision.service.OrderProgressService;
import com.mikealbert.vision.service.UnitProgressService;
import com.mikealbert.vision.util.XLSUtil;
import com.mikealbert.vision.view.ViewConstants;
import com.mikealbert.vision.vo.ContactInfo;
import com.mikealbert.vision.vo.LogBookTypeVO;

@Component
@Scope("view")
public class ManufacturerProgressQueueBean extends StatefulBaseBean {
	private static final long serialVersionUID = -6351179202239184328L;
	
	@Resource ManufacturerProgressQueueService manufacturerQueueService;
	@Resource FleetMasterService fleetMasterService;
	@Resource LogBookService logBookService;
	@Resource ClientPOCService clientPOCService;
	@Resource DocumentService documentService;
	@Resource ProcessStageService processStageService;
	@Resource OrderProgressService orderProgressService;
	@Resource UpfitterService upfitterService;
	@Resource UnitProgressService unitProgressService;
	@Resource DocumentService docService;
	
	private List<ManufacturerProgressQueueVO> filteredList = new ArrayList<ManufacturerProgressQueueVO>();
	private List<ManufacturerProgressQueueVO> masterList;
	private List<ManufacturerProgressQueueVO> selectedManufactureQueueList;
	private ManufacturerProgressQueueVO selectedManufactureQueue;
	private ManufacturerProgressQueueVO updatedManufactureQueue;
	private FleetMaster selectedFleetMaster;
	private String makeFilter;
	private String unitNoFilter;
	private String clientNameFilter;
	private String clientContactReason;	
	private boolean buttonDisabled;
	private String emptyDataTableMessage;
	private List<FleetMaster> selectedFleetMasterList;
	private List<ProcessStageObject> selectedProcessStageObjects;	
	private List<LogBookTypeVO> logBookType = null;
	private List<SortMeta> preSort = new ArrayList<SortMeta>();	
	private OrderToDeliveryProcessStageEnum targetProcessStage;
	private String toleranceMessage2 = null;
	private String toleranceMessage3 = null;
	private String selectedOrderingDealerName;
	private String selectedOrderingDealerAddressDetails;
	private ContactInfo  requestedContactInfo;

	private List<Map<String, Object>> downloadableRowsData = new ArrayList<Map<String, Object>>();;
	private List<String> downloadableColumns = new ArrayList<String>();
	ManufacturerProgressSearchCriteriaVO searchCriteria = new ManufacturerProgressSearchCriteriaVO();;

	private static final String DATA_TABLE_UI_ID = "DT_UI_ID";
	private static final String MAKE_UI_ID = "MAKE_UI_ID";
	private static final String UNIT_NO_UI_ID = "UNIT_NO_UI_ID";	
	private static final String FOLLOW_UP_DATE_UI_ID = "FOLLOW_UP_UI_ID";	
	private static final String DESIRED_TO_DLR__UI_ID = "DESIRED_TO_DLR_UI_ID";
	private static final String EXPECTED_DATE_UI_ID = "EXPECTED_DATE_UI_ID";
	private static final String TOLERANCE_MSG_UI_ID = "TOLERANCE_MSG_UI_ID";
	private static final String TOLERANCE_MSG_REG_EXP = "\\|\\|";
	
	private static final String DT_ROW_KEY_NAME = "UNIT_NO";
	private static final String PS_PROP_MAX_DAYS_TO_SHIP_MSG  = "TOLERANCE_MAX_ELAPSED_DAYS_TO_SHIP_MSG";
	private static final String PS_PROP_DLR_RECD_LD_EXD_EXP_MSG = "TOLERANCE_DLR_RECD_LD_TIME_EXCDS_EXP_DT";
	private static final String PS_PROP_NO_DLR_RECVD_MSG = "TOLERANCE_INV_PAID_NO_DLR_RECVD_MSG";
	private static final String PSP_MANUFACTURER = "MANUFACTURER";
	
	private int recordPerPage = ViewConstants.RECORDS_PER_PAGE_X_SMALL;
	
	private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yy");
	private static String EXCEL_DL_COOKIE = "client.excel.download";
	
	static final String DEFAULT_SINGLE_ENTITY_WINDOW_TITLE = "Request Client Contact";
	static final String DEFAULT_MULTIPLE_ENTITY_WINDOW_TITLE = "Multiple Selection";	
	

	@PostConstruct
	public void init() {
		initializeDataTable(660, 850, new int[] {2, 6, 7, 12, 11, 4, 3, 4, 5, 4, 20 });
		openPage();
		setButtonDisabled(true);
		setLogBookType(new ArrayList<LogBookTypeVO>());
		setSelectedFleetMasterList(new ArrayList<FleetMaster>());
		setSelectedProcessStageObjects(new ArrayList<ProcessStageObject>());		
		performSearch();	
	}
	
	public void performSearch(){
		
		try {	
			DataTable dt = (DataTable) getComponent(DATA_TABLE_UI_ID);
			dt.setFirst(0);
			if(masterList != null)
				masterList.clear();
			if(filteredList != null)
				filteredList.clear();
	
			this.clientNameFilter = "";
			this.unitNoFilter = "";
			this.makeFilter = "";
			this.selectedManufactureQueue = null;
			this.selectedManufactureQueueList = null;
			setEmptyDataTableMessage(talMessage.getMessage("before.search.datattable.message"));
		} catch (Exception e) {
			logger.error(e);
			if(e instanceof MalBusinessException) {
				super.addErrorMessage(e.getMessage());
			} else {
				super.addErrorMessage("generic.error.occured.while", " fetching units..");
			}
		}
	}

	private void loadData() throws Exception {
		if(downloadableRowsData != null)
			downloadableRowsData.clear();
		if(downloadableColumns != null)
			downloadableColumns.clear();
		if(masterList != null)
			masterList.clear();
		if(filteredList != null)
			filteredList.clear();
		if(getLogBookType() != null)
			getLogBookType().clear();

		getLogBookType().add(new LogBookTypeVO(LogBookTypeEnum.TYPE_BASE_VEH_ORDER_NOTES, LogBookTypeEnum.TYPE_UPFIT_PRG_NOTES, ViewConstants.LABEL_PROGRESS_CHASING, false));
		getLogBookType().get(0).getRelatedLogBookTypes().add(LogBookTypeEnum.TYPE_IN_SERV_PRG_NOTES);
		getLogBookType().get(0).getRelatedLogBookTypes().add(LogBookTypeEnum.TYPE_CLIENT_FACING_NOTES);
		getLogBookType().get(0).getRelatedLogBookTypes().add(LogBookTypeEnum.TYPE_PURCHASE_ORDER_NOTES);
		getLogBookType().get(0).getRelatedLogBookTypes().add(LogBookTypeEnum.TYPE_VEHICLE_STATUS_NOTES);
		getLogBookType().get(0).setRenderEntrySource(true);
		getLogBookType().add(new LogBookTypeVO(LogBookTypeEnum.TYPE_EXTERNAL_TAL_FILE_NOTES, true));	
		
		
		searchCriteria.setClient(clientNameFilter);
		searchCriteria.setUnitNo(unitNoFilter);
		searchCriteria.setMake(makeFilter);
		
		masterList = manufacturerQueueService.getManufacturerQueueSearchResults(searchCriteria);
		filteredList.addAll(masterList);
		sortDataMultipleSort();
	}
	
	private void sortDataMultipleSort() {
		
		if(preSort != null) {
			preSort.clear();
		}
		
		SortMeta sortMeta = new SortMeta();
		UIColumn column = null;

		column = (UIColumn) getComponent(DATA_TABLE_UI_ID+":"+MAKE_UI_ID);
		sortMeta.setSortField("make");
		sortMeta.setSortBy(column);
		sortMeta.setSortOrder(SortOrder.ASCENDING);
		preSort.add(sortMeta);
		
		sortMeta = new SortMeta();
		column = (UIColumn) getComponent(DATA_TABLE_UI_ID+":"+UNIT_NO_UI_ID);
		sortMeta.setSortBy(column);
		sortMeta.setSortField("unitNo");
		sortMeta.setSortOrder(SortOrder.ASCENDING);
		preSort.add(sortMeta);
		
	}
	
	public void navigateToUpfitterQueue(){
		Map<String, Object> nextPageValues = new HashMap<String, Object>();
		nextPageValues.put("searchType", String.valueOf(1));
		super.pageList.remove(super.pageList.size()-1);
		saveNextPageInitStateValues(nextPageValues);
		forwardToURL(ViewConstants.UPFITTER_IN_SERVICE_QUEUE);
	}
	
	public void navigateToInServiceQueue(){
		Map<String, Object> nextPageValues = new HashMap<String, Object>();
		nextPageValues.put("searchType", String.valueOf(2));
		super.pageList.remove(super.pageList.size()-1);
		saveNextPageInitStateValues(nextPageValues);
		forwardToURL(ViewConstants.UPFITTER_IN_SERVICE_QUEUE);
	}
	
	public void navigateToPOReleaseQueue(){
		Map<String, Object> nextPageValues = new HashMap<String, Object>();
    	nextPageValues.put("searchType", String.valueOf(0));
    	super.pageList.remove(super.pageList.size()-1);
	    saveNextPageInitStateValues(nextPageValues);
		forwardToURL(ViewConstants.PURCHASE_ORDER_RELEASE_QUEUE);
	}
	
	public void navigateToThirdPartyQueue(){
		Map<String, Object> nextPageValues = new HashMap<String, Object>();
    	nextPageValues.put("searchType", String.valueOf(0));
    	super.pageList.remove(super.pageList.size()-1);
	    saveNextPageInitStateValues(nextPageValues);
		forwardToURL(ViewConstants.THIRD_PARTY_PROGRESS_QUEUE);
	}
	
	protected void loadNewPage() {
		thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_MANUFACTURER_PROGRESS_QUEUE);
		thisPage.setPageUrl(ViewConstants.MANUFACTURER_PROGRESS_QUEUE);		
	}

	protected void restoreOldPage() {

	}

	public String cancel() {
		return super.cancelPage();
	}

	public void onRowSelect(ToggleSelectEvent event) {
		setButtonDisabled(getSelectedManufactureQueueList().size() > 0 ? false : true);
	}

	public void onRowSelect(SelectEvent event) {
		setButtonDisabled(getSelectedManufactureQueueList().size() > 0 ? false : true);
	}

	public void onRowUnselect(UnselectEvent event) {
		setButtonDisabled(getSelectedManufactureQueueList().size() > 0 ? false : true);
	}
	
	public void onPageChange(PageEvent event) {
		clearSelection();
		setButtonDisabled(true);
    }
	
	public void onSortOperation(SortEvent event) {
		clearSelection();
		setButtonDisabled(true);
	 }
	public void clearSelection() {
		setSelectedManufactureQueue(null);
		if(getSelectedManufactureQueueList() != null){
			getSelectedManufactureQueueList().clear();
		}
	}
	public void performFilter() {
		clearSelection();
		if(masterList != null)
			masterList.clear();
		filteredList.clear();
		
		try {
			loadData();
		} catch (Exception e) {
			logger.error(e);
			if(e instanceof MalBusinessException) {
				super.addErrorMessage(e.getMessage());
			} else {
				super.addErrorMessage("generic.error.occured.while", " fetching units..");
			}
		}
		
		getSelectedManufactureQueueList().clear();
		setButtonDisabled(true);
		ManufacturerProgressQueueVO manufactureQueueVo = null;
		for(Iterator<ManufacturerProgressQueueVO> manufactureQueueItr = filteredList.iterator(); manufactureQueueItr.hasNext();) {
			manufactureQueueVo = manufactureQueueItr.next();
			
			if(MALUtilities.isNotEmptyString(makeFilter)) {
				if(manufactureQueueVo.getMake() == null || manufactureQueueVo.getMake().toUpperCase().indexOf(makeFilter.trim().toUpperCase()) < 0) {
					manufactureQueueItr.remove();
					continue;
				}
			}
			
			if(MALUtilities.isNotEmptyString(unitNoFilter)) {
				if(manufactureQueueVo.getUnitNo() == null || manufactureQueueVo.getUnitNo().indexOf(unitNoFilter.trim()) < 0) {
					manufactureQueueItr.remove();
					continue;
				}
			}
			
			if(MALUtilities.isNotEmptyString(clientNameFilter)) {
				if(manufactureQueueVo.getClientAccountName() == null ){
					manufactureQueueItr.remove();
					continue;
				}
				if(manufactureQueueVo.getClientAccountName().toUpperCase().indexOf(clientNameFilter.trim().toUpperCase()) < 0){
					if(manufactureQueueVo.getClientAccountCode().toUpperCase().indexOf(clientNameFilter.trim().toUpperCase()) < 0){
						manufactureQueueItr.remove();
						continue;
					}
				}
			}
		}
		
		if(filteredList == null || filteredList.size() == 0) {
			this.setEmptyDataTableMessage(talMessage.getMessage("no.records.found"));
		}
	}
	
	public void populateFollowUpDate() {
		RequestContext context = RequestContext.getCurrentInstance();		
		int index = -1;
		String formattedFollowUpDate;
		Date followUpDate = null;
		for (FleetMaster selectedFleetMaster : getSelectedFleetMasterList()) {
			index = -1;
			followUpDate = logBookService.getFollowUpDate(selectedFleetMaster, getLogBookType().get(0).getLogBookType());

			if(!MALUtilities.isEmpty(followUpDate)) {
				formattedFollowUpDate = dateFormatter.format(followUpDate);
			} else {
				formattedFollowUpDate = "";
			}
			
			for(ManufacturerProgressQueueVO manufactureQueueVo : masterList) {
				if(manufactureQueueVo.getUnitNo().equals(selectedFleetMaster.getUnitNo())) {
					manufactureQueueVo.setFollowUpDate(followUpDate);
					break;
				}
			}

			for(ManufacturerProgressQueueVO manufactureQueueVo : filteredList) {
				index += 1;
				if(manufactureQueueVo.getUnitNo().equals(selectedFleetMaster.getUnitNo())) {
					manufactureQueueVo.setFollowUpDate(followUpDate);
					break;
				}
			}

			

			
			context.execute("$('[id$=\\\\:" + index + "\\\\:" + FOLLOW_UP_DATE_UI_ID + "]').text('" + formattedFollowUpDate + "')");
		}
	}
	
	//set Selected VO through remote command
	public void setSelectedVORC() {
	    String unitNos = getRequestParameter(DT_ROW_KEY_NAME);
	    selectedManufactureQueueList = new ArrayList<ManufacturerProgressQueueVO>();
	    if(unitNos != null && unitNos.trim().length() > 0){
	    	for(ManufacturerProgressQueueVO manufactureQueueVo : filteredList) {
	    		for(String unitNo : unitNos.split(",")) {
					if(manufactureQueueVo.getUnitNo().equals(unitNo)){					
						selectedManufactureQueueList.add(manufactureQueueVo);					
						break;
					}
	    		}	    		
	    	}
	    	setSelectedManufactureQueueList(selectedManufactureQueueList);
	    }
	}
	
	public void refreshManufacturerProgressRecord(){
		int index = -1;
		String desiredToDlr;
		String expectedDate;
				
		if(selectedManufactureQueue != null ){
			selectedFleetMaster = fleetMasterService.findByUnitNo(selectedManufactureQueue.getUnitNo());
		
			if( selectedFleetMaster != null) {
				Long docId = selectedManufactureQueue.getDocId();
				
				if( docId != null ){
					ManufacturerProgressQueueVO updatedManufactureQueueVo = manufacturerQueueService.getUpdatedManufactureQueueSearch(selectedManufactureQueue);
					setUpdatedManufactureQueue(updatedManufactureQueueVo);
					setToleranceMessage2(null);
					setToleranceMessage3(null);
					RequestContext context = RequestContext.getCurrentInstance();
					
					if( isUnitNeedToRemoveFromQueue() ){
						for(Iterator<ManufacturerProgressQueueVO> manufactureQueueItr = masterList.iterator(); manufactureQueueItr.hasNext();) {
							ManufacturerProgressQueueVO manufactureQueueVO = manufactureQueueItr.next();
							if(manufactureQueueVO.getUnitNo().equals(selectedManufactureQueue.getUnitNo())) {
								manufactureQueueItr.remove();
								break;
							}
						}
						filteredList.remove(selectedManufactureQueue);						
						processStageService.excludeStagedObject(selectedManufactureQueue.getPsoId());
						clearSelection();
						context.update(DATA_TABLE_UI_ID);
					}else {
						
						updatedManufactureQueueVo = getUpdatedManufactureQueue();
						
						for(ManufacturerProgressQueueVO manufactureQueueVO : masterList) {
							if(manufactureQueueVO.getUnitNo().equals(getSelectedFleetMaster().getUnitNo())) {
								manufactureQueueVO.setDesiredToDealer(updatedManufactureQueueVo.getDesiredToDealer());
								manufactureQueueVO.setExpectedDate(updatedManufactureQueueVo.getExpectedDate());
								manufactureQueueVO.setToleranceMessage(updatedManufactureQueueVo.getToleranceMessage());
								break;
							}
						}
		
						for(ManufacturerProgressQueueVO manufactureQueueVO : filteredList) {
							index +=1;
							if(manufactureQueueVO.getUnitNo().equals(getSelectedFleetMaster().getUnitNo())) {
								manufactureQueueVO.setDesiredToDealer(updatedManufactureQueueVo.getDesiredToDealer());
								manufactureQueueVO.setExpectedDate(updatedManufactureQueueVo.getExpectedDate());
								manufactureQueueVO.setToleranceMessage(updatedManufactureQueueVo.getToleranceMessage());
								break;
							}
						}
						
						desiredToDlr = MALUtilities.isEmpty(updatedManufactureQueueVo.getDesiredToDealer()) ? "" : dateFormatter.format(updatedManufactureQueueVo.getDesiredToDealer());
						expectedDate = MALUtilities.isEmpty(updatedManufactureQueueVo.getExpectedDate()) ? "" : dateFormatter.format(updatedManufactureQueueVo.getExpectedDate());
						processStageService.updateStagedObjectReason(updatedManufactureQueueVo.getPsoId(), updatedManufactureQueueVo.getToleranceMessage());
						
						context.execute("$('[id$=\\\\:" + index + "\\\\:"+ DESIRED_TO_DLR__UI_ID +"]').text('" + desiredToDlr + "')");
						context.execute("$('[id$=\\\\:" + index + "\\\\:"+ EXPECTED_DATE_UI_ID +"]').text('" + expectedDate + "')");
						
						context.execute("$('[id$=\\\\:" + index + "\\\\:"+ TOLERANCE_MSG_UI_ID +"]').html('" + formatReasonForDisplay(updatedManufactureQueueVo.getToleranceMessage()) + "')");						
						
						
					} 
				}
			}
		}
	}
	
	public void refreshRequestClientContactListener(){
		addSuccessMessage("custom.message", "Unit is added to client facing queue");
	}
	
	
	public  String formatReasonForDisplay(String input){
		String formattedHMTLReason = "";
		if(MALUtilities.isNotEmptyString(input)){
			String[] strArray = input.split(TOLERANCE_MSG_REG_EXP);			
			for (int i = 0; i < strArray.length; i++) {					
				formattedHMTLReason = formattedHMTLReason + ViewConstants.HTML_BULLET_CODE +  strArray[i];				
				if( i < strArray.length -1){
					formattedHMTLReason = formattedHMTLReason +ViewConstants.HTML_BREAK_LINE;
				}
			}
		}
		
		return formattedHMTLReason;
	}
		
	/**
	 * Handles the row selection event.
	 */
	public void rowSelectionListener(){//TODO We need to change in queue to populate stock in in view itself
		Doc po;
		boolean isStockOrderUnit = false;
		RequestContext context = RequestContext.getCurrentInstance();		
		
		setSelectedVORC();		
		for(ManufacturerProgressQueueVO queueItemVo : getSelectedManufactureQueueList()){
			po = documentService.getDocumentByDocId(queueItemVo.getDocId());
			
			if(MALUtilities.isEmpty(po.getGenericExtId())){
				isStockOrderUnit = true;
				queueItemVo.setStockOrder(true);
				//	break; We want to set for all selected unit if they are stock so that veh order status screen can be displayed for stock/non stock
			}
		}
		
		context.execute(isStockOrderUnit || !hasPermission("manage_veh_order_status") ? "PF('requestClientContactBtnVar').disable()" : "PF('requestClientContactBtnVar').enable()");
	}
	
	private boolean isUnitNeedToRemoveFromQueue(){
		return manufacturerQueueService.isItemWithinTolerance(getSelectedManufactureQueue().getPsoId());	
	}
	
	public void setSelectedFleetMasterVO(ManufacturerProgressQueueVO manufactureQueueVo) {
		setSelectedManufactureQueue(manufactureQueueVo);
		if(!MALUtilities.isEmpty(manufactureQueueVo)) {
			selectedFleetMasterList = new ArrayList<FleetMaster>();
			selectedFleetMasterList.add(fleetMasterService.findByUnitNo(manufactureQueueVo.getUnitNo()));
		}
	}
	
	public void setSelectedFleetMasterVOs() {
		if(selectedManufactureQueueList != null) {
			selectedFleetMasterList = new ArrayList<FleetMaster>();
			for(ManufacturerProgressQueueVO manufactureQueueVo : selectedManufactureQueueList) {
				selectedFleetMasterList.add(fleetMasterService.findByUnitNo(manufactureQueueVo.getUnitNo()));
			}
		}
	}
	
	public void captureSelectedProcessStageObjects(){
		if(selectedManufactureQueueList != null) {
			setSelectedProcessStageObjects(new ArrayList<ProcessStageObject>());
			for(ManufacturerProgressQueueVO manufactureQueueVo : selectedManufactureQueueList) {
				getSelectedProcessStageObjects().add(processStageService.getStagedObject(manufactureQueueVo.getPsoId()));
			}
		}		
	}
	
	public void requestClientContactListner() {
		setTargetProcessStage(OrderToDeliveryProcessStageEnum.CLIENT_FACING);
		captureSelectedProcessStageObjects();			
	}
		
	
	public void preProcessDownloadXLS(Object document) {

		downloadableRowsData.clear();
		downloadableColumns.clear();
		downloadableColumns.add("Make");
		downloadableColumns.add("Unit No");
		downloadableColumns.add("Trim");
		downloadableColumns.add("Client");
		downloadableColumns.add("PO Date");
		downloadableColumns.add("Lead Time");
		downloadableColumns.add("Desired To Dlr");
		downloadableColumns.add("Expected Date");
		downloadableColumns.add("Follow Up");
		downloadableColumns.add("Reason");

		for (ManufacturerProgressQueueVO manufactureQueueVo : filteredList) {
			Map<String, Object> row = new HashMap<String, Object>();
			row.put("Make", manufactureQueueVo.getMake());
			row.put("Unit No", manufactureQueueVo.getUnitNo());
			row.put("Trim", manufactureQueueVo.getTrim());
			row.put("Client", manufactureQueueVo.getClientAccountName());
			row.put("PO Date", manufactureQueueVo.getPoDate());
			row.put("Lead Time", manufactureQueueVo.getLeadTime());
			row.put("Desired To Dlr", manufactureQueueVo.getDesiredToDealer());
			row.put("Expected Date", manufactureQueueVo.getExpectedDate());
			row.put("Follow Up", manufactureQueueVo.getFollowUpDate());
			row.put("Reason", manufactureQueueVo.getToleranceMessage().replaceAll("\\|\\|","\n"));
			downloadableRowsData.add(row);
		}
	}

	public void postProcessDownloadXLS(Object document) {
		
		HSSFWorkbook wb = (HSSFWorkbook) document;
		HSSFSheet sheet = wb.getSheetAt(0);
		sheet.shiftRows(0, sheet.getLastRowNum(), 1);

		Row titleRow = sheet.createRow(0);
		titleRow.setHeightInPoints(45);
		Cell titleCell1 = titleRow.createCell(0);
		titleCell1.setCellValue("Manufacturer Progress");
		titleCell1.setCellStyle(XLSUtil.createStyle(wb, XLSUtil.TITLE));
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, downloadableColumns.size() - 1));
		Font boldFont = wb.createFont();
		boldFont.setBoldweight(Font.BOLDWEIGHT_BOLD);

		HSSFRow row;
		HSSFCell cell;
		CellStyle cellStyle;
		for(int i = 0; i <= sheet.getLastRowNum(); i++) {
			boolean autoResize = false;
			row = sheet.getRow(i);
			if(i == 0) {
				cellStyle = XLSUtil.createStyle(wb, XLSUtil.TITLE);
				autoResize = false;
			} else if(i == 1) {
				cellStyle = XLSUtil.createStyle(wb, XLSUtil.HEADER);
				autoResize = true;
			} else {
				cellStyle = XLSUtil.createStyle(wb, XLSUtil.CELL);
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
	
	/**
	 * Determines the window title of the Request Client Contact dialog.
	 * Title is based on single or multiple row item selection
	 * @return String the determined window title
	 */
	public String requestClientContactWindowTitle(){
		StringBuilder titleBuilder = new StringBuilder();
		
		if(!MALUtilities.isEmpty(getSelectedManufactureQueueList())){
			if(getSelectedManufactureQueueList().size() > 1) {
				titleBuilder.append(DEFAULT_MULTIPLE_ENTITY_WINDOW_TITLE);
			} else {
				titleBuilder.append(DEFAULT_SINGLE_ENTITY_WINDOW_TITLE).append(" Unit No.: ").append(getSelectedManufactureQueue().getUnitNo());			
			}
		}
		
		return titleBuilder.toString();
	}	
	
	public List<ManufacturerProgressQueueVO> getMasterList() {
		return masterList;
	}

	public void setMasterList(List<ManufacturerProgressQueueVO> masterList) {
		this.masterList = masterList;
	}

	public List<ManufacturerProgressQueueVO> getFilteredList() {
		return filteredList;
	}

	public void setFilteredList(List<ManufacturerProgressQueueVO> filteredList) {
		this.filteredList = filteredList;
	}

	public List<ManufacturerProgressQueueVO> getSelectedManufactureQueueList() {
		return selectedManufactureQueueList;
	}

	public void setSelectedManufactureQueueList(List<ManufacturerProgressQueueVO> selectedManufactureQueueList) {
		this.selectedManufactureQueueList = selectedManufactureQueueList;
		if(selectedManufactureQueueList != null && selectedManufactureQueueList.size() == 1){
			selectedManufactureQueue = selectedManufactureQueueList.get(0);
		}else{
			selectedManufactureQueue = null;
		}
	}

	public String getUnitNoFilter() {
		return unitNoFilter;
	}

	public void setUnitNoFilter(String unitNoFilter) {
		this.unitNoFilter = unitNoFilter;
	}

	public String getClientNameFilter() {
		return clientNameFilter;
	}

	public void setClientNameFilter(String clientNameFilter) {
		this.clientNameFilter = clientNameFilter;
	}
	
	public String getMakeFilter() {
		return makeFilter;
	}

	public void setMakeFilter(String makeFilter) {
		this.makeFilter = makeFilter;
	}

	public List<FleetMaster> getSelectedFleetMasterList() {
		return selectedFleetMasterList;
	}

	public void setSelectedFleetMasterList(List<FleetMaster> selectedFleetMasterList) {
		this.selectedFleetMasterList = selectedFleetMasterList;
	}

	public List<LogBookTypeVO> getLogBookType() {
		return logBookType;
	}

	public void setLogBookType(List<LogBookTypeVO> logBookType) {
		this.logBookType = logBookType;
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
	
	public boolean isButtonDisabled() {
		return buttonDisabled;
	}

	public void setButtonDisabled(boolean buttonDisabled) {
		this.buttonDisabled = buttonDisabled;
	}
	
	public String getClientContactReason() {
		return clientContactReason;
	}

	public void setClientContactReason(String clientContactReason) {
		this.clientContactReason = clientContactReason;
	}

	public String getEmptyDataTableMessage() {
		return emptyDataTableMessage;
	}

	public void setEmptyDataTableMessage(String emptyDataTableMessage) {
		this.emptyDataTableMessage = emptyDataTableMessage;
	}
	
	public List<SortMeta> getPreSort() {
		return preSort;
	}

	public void setPreSort(List<SortMeta> preSort) {
		this.preSort = preSort;
	}
	public ManufacturerProgressQueueVO getSelectedManufactureQueue() {
		return selectedManufactureQueue;
	}

	public void setSelectedManufactureQueue(
			ManufacturerProgressQueueVO selectedManufactureQueue) {
		this.selectedManufactureQueue = selectedManufactureQueue;
	}

	public FleetMaster getSelectedFleetMaster() {
		return selectedFleetMaster;
	}

	public void setSelectedFleetMaster(FleetMaster selectedFleetMaster) {
		this.selectedFleetMaster = selectedFleetMaster;
	}

	public List<ProcessStageObject> getSelectedProcessStageObjects() {
		return selectedProcessStageObjects;
	}

	public void setSelectedProcessStageObjects(
			List<ProcessStageObject> selectedProcessStageObjects) {
		this.selectedProcessStageObjects = selectedProcessStageObjects;
	}

	public OrderToDeliveryProcessStageEnum getTargetProcessStage() {
		return targetProcessStage;
	}

	public void setTargetProcessStage(OrderToDeliveryProcessStageEnum targetProcessStage) {
		this.targetProcessStage = targetProcessStage;
	}

	public ManufacturerProgressQueueVO getUpdatedManufactureQueue() {
		return updatedManufactureQueue;
	}

	public void setUpdatedManufactureQueue(
			ManufacturerProgressQueueVO updatedManufactureQueue) {
		this.updatedManufactureQueue = updatedManufactureQueue;
	}

	public String getToleranceMessage2() {
		return toleranceMessage2;
	}

	public void setToleranceMessage2(String toleranceMessage2) {
		this.toleranceMessage2 = toleranceMessage2;
	}

	public String getToleranceMessage3() {
		return toleranceMessage3;
	}

	public void setToleranceMessage3(String toleranceMessage3) {
		this.toleranceMessage3 = toleranceMessage3;
	}

	public String getSelectedOrderingDealerName() {
		return selectedOrderingDealerName;
	}

	public void setSelectedOrderingDealerName(String selectedOrderingDealerName) {
		this.selectedOrderingDealerName = selectedOrderingDealerName;
	}

	public String getSelectedOrderingDealerAddressDetails() {
		return selectedOrderingDealerAddressDetails;
	}

	public void setSelectedOrderingDealerAddressDetails(String selectedOrderingDealerAddressDetails) {
		this.selectedOrderingDealerAddressDetails = selectedOrderingDealerAddressDetails;
	}

	public ContactInfo getRequestedContactInfo() {
		return requestedContactInfo;
	}

	public void setRequestedContactInfo(ContactInfo requestedContactInfo) {
		this.requestedContactInfo = requestedContactInfo;
	}

	public int getRecordPerPage() {
		return recordPerPage;
	}

	public void setRecordPerPage(int recordPerPage) {
		this.recordPerPage = recordPerPage;
	}

}
