package com.mikealbert.vision.view.bean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.component.UIInput;

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
import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleSelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.event.data.PageEvent;
import org.primefaces.event.data.SortEvent;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.ObjectLogBook;
import com.mikealbert.data.entity.queue.ClientFacingQueueV;
import com.mikealbert.data.enumeration.LogBookTypeEnum;
import com.mikealbert.data.vo.ClientContactVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.ClientPOCService;
import com.mikealbert.service.FleetMasterService;
import com.mikealbert.service.LogBookService;
import com.mikealbert.service.PurchaseOrderReportService;
import com.mikealbert.service.reporting.JasperReportService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.service.ClientFacingQueueService;
import com.mikealbert.vision.util.XLSUtil;
import com.mikealbert.vision.view.ViewConstants;
import com.mikealbert.vision.vo.ContactInfo;
import com.mikealbert.vision.vo.LogBookTypeVO;
import com.mikealbert.vision.vo.UnitInfo;

@Component
@Scope("view")
public class ClientFacingQueueBean extends StatefulBaseBean {
	private static final long serialVersionUID = -6351179202239184328L;
	
	@Resource ClientFacingQueueService clientFacingQueueService;
	@Resource FleetMasterService fleetMasterService;
	@Resource LogBookService logBookService;
	@Resource ClientPOCService clientPOCService;
	@Resource JasperReportService jasperReportService;
	@Resource JasperReportBean jasperReportBean;
	@Resource PurchaseOrderReportService purchaseOrderReportService;
	
	private List<ClientFacingQueueV> filteredList = new ArrayList<ClientFacingQueueV>();
	private List<ClientFacingQueueV> masterList;
	private List<ClientFacingQueueV> selectedClientFacingQueueList;
	private String selectedUnitNo;
	private ContactInfo requestedContactInfo;
	private String unitNoFilter;
	private String clientNameFilter;
	private String cssFilter;
	private List<FleetMaster> selectedFleetMasterList;
	private UnitInfo requestedUnitInfo;
	private List<LogBookTypeVO> logBookType = null;
	private String emptyDataTableMessage;
	private List<Map<String, Object>> downloadableRowsData = new ArrayList<Map<String, Object>>();;
	private List<String> downloadableColumns = new ArrayList<String>();
	private boolean buttonDisabled;
	private int selectedRecordCount;
	private String noteContactName;
	private Date noteContactDate;
	private String noteComment;

	private static final String DATA_TABLE_UI_ID = "clientFacingTable";
	private static final String FOLLOW_UP_DATE_UI_ID = "cellFollupDateLbl";
	private static final String NOTE_CONTACT_NAME_UI_ID = "noteContactNameTxt";
	private static final String NOTE_CONTACT_DATE_UI_ID = "noteContactDateCal";
	private static final String NOTE_CONTACT_NOTE_UI_ID = "completeContactNoteTA";	
	private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy");
	private static String EXCEL_DL_COOKIE = "client.excel.download";
	private static final String COMPLETE_CONTACT_DETAIL = "Contacted client %1s on date %2s. %3s";

	@PostConstruct
	public void init() {
		initializeDataTable(560, 850, new int[] {2, 7, 10, 20, 19, 6, 6, 6, 6, 18 });
		openPage();
		setButtonDisabled(true);
		setSelectedRecordCount(0);
		setLogBookType(new ArrayList<LogBookTypeVO>());
		setSelectedFleetMasterList(new ArrayList<FleetMaster>());
		DataTable dt = (DataTable) getComponent(DATA_TABLE_UI_ID);
		dt.setFirst(0);

		this.clientNameFilter = "";
		this.unitNoFilter = "";
		this.cssFilter = "";

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
		this.setEmptyDataTableMessage(talMessage.getMessage("no.records.found"));
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

		getLogBookType().add(new LogBookTypeVO(LogBookTypeEnum.TYPE_CLIENT_FACING_NOTES, LogBookTypeEnum.TYPE_UPFIT_PRG_NOTES, ViewConstants.LABEL_PROGRESS_CHASING, false));
		getLogBookType().get(0).getRelatedLogBookTypes().add(LogBookTypeEnum.TYPE_IN_SERV_PRG_NOTES);
		getLogBookType().get(0).getRelatedLogBookTypes().add(LogBookTypeEnum.TYPE_BASE_VEH_ORDER_NOTES);
		getLogBookType().get(0).getRelatedLogBookTypes().add(LogBookTypeEnum.TYPE_PURCHASE_ORDER_NOTES);
		getLogBookType().get(0).setRenderEntrySource(true);
		getLogBookType().add(new LogBookTypeVO(LogBookTypeEnum.TYPE_EXTERNAL_TAL_FILE_NOTES, true));		
		
		Sort sort = new Sort(Sort.Direction.ASC, "toleranceYN", "unitNo");
		masterList = clientFacingQueueService.findClientFacingQueueList(sort);
		filteredList.addAll(masterList);
	}
	
	public int requestedDateCustomSort(Object o1, Object o2){
		ClientFacingQueueV cfq1 = (ClientFacingQueueV) o1;
		ClientFacingQueueV cfq2 = (ClientFacingQueueV) o2;
		
		if(MALUtilities.isEmpty(cfq1.getRequestedDate()) || MALUtilities.isEmpty(cfq2.getRequestedDate())) {
			return MALUtilities.isEmpty(cfq1.getRequestedDate()) ? 1 : -1;
		} else {
			if(MALUtilities.isValidDate(cfq1.getRequestedDate()) && MALUtilities.isValidDate(cfq2.getRequestedDate())){
				return MALUtilities.formatDate(cfq1.getRequestedDate()).compareTo(MALUtilities.formatDate(cfq2.getRequestedDate()));
			}else if(MALUtilities.isValidDate(cfq1.getRequestedDate()) && !MALUtilities.isValidDate(cfq2.getRequestedDate())){
				return -1;
			}else if(!MALUtilities.isValidDate(cfq1.getRequestedDate()) && MALUtilities.isValidDate(cfq2.getRequestedDate())){
				return 1;
			}else if(! MALUtilities.isValidDate(cfq1.getRequestedDate()) && !MALUtilities.isValidDate(cfq2.getRequestedDate())){
				return cfq1.getRequestedDate().compareTo(cfq2.getRequestedDate());
			}else{
				return 0;
			}
		}
	}
	
	protected void loadNewPage() {
		thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_CLIENT_FACING_QUEUE);
		thisPage.setPageUrl(ViewConstants.CLIENT_FACING_QUEUE);

		this.setEmptyDataTableMessage(talMessage.getMessage("no.records.found"));
	}

	protected void restoreOldPage() {

	}

	public String cancel() {
		return super.cancelPage();
	}

	public void onRowSelect(ToggleSelectEvent event) {
		setButtonDisabled(getSelectedClientFacingQueueList().size() > 0 ? false : true);
	}

	public void onRowSelect(SelectEvent event) {
		setButtonDisabled(getSelectedClientFacingQueueList().size() > 0 ? false : true);
	}

	public void onRowUnselect(UnselectEvent event) {
		setButtonDisabled(getSelectedClientFacingQueueList().size() > 0 ? false : true);
	}
	
	public void onPageChange(PageEvent event) {
		getSelectedClientFacingQueueList().clear();
		setButtonDisabled(true);
    }
	
	public void onSortOperation(SortEvent event) {
		getSelectedClientFacingQueueList().clear();
		setButtonDisabled(true);
	 }

	public void performFilter() {
		filteredList.clear();
		filteredList.addAll(masterList);
		
		getSelectedClientFacingQueueList().clear();
		setButtonDisabled(true);
		
		if(MALUtilities.isNotEmptyString(cssFilter)) {
			ClientFacingQueueV clientFacingQueueV = null;
			for(Iterator<ClientFacingQueueV> clientFacingQueueItr = filteredList.iterator(); clientFacingQueueItr.hasNext();) {
				clientFacingQueueV = clientFacingQueueItr.next();
				if(clientFacingQueueV.getCssOrTm() == null || clientFacingQueueV.getCssOrTm().toUpperCase().indexOf(cssFilter.trim().toUpperCase()) < 0) {
					clientFacingQueueItr.remove();
				}
			}
		}
		
		if(MALUtilities.isNotEmptyString(unitNoFilter)) {
			ClientFacingQueueV clientFacingQueueV = null;
			for(Iterator<ClientFacingQueueV> clientFacingQueueItr = filteredList.iterator(); clientFacingQueueItr.hasNext();) {
				clientFacingQueueV = clientFacingQueueItr.next();
				if(clientFacingQueueV.getUnitNo() == null || clientFacingQueueV.getUnitNo().indexOf(unitNoFilter.trim()) < 0) {
					clientFacingQueueItr.remove();
				}
			}
		}

		if(MALUtilities.isNotEmptyString(clientNameFilter)) {
			ClientFacingQueueV clientFacingQueueV = null;
			for(Iterator<ClientFacingQueueV> clientFacingQueueItr = filteredList.iterator(); clientFacingQueueItr.hasNext();) {
				clientFacingQueueV = clientFacingQueueItr.next();
				if(clientFacingQueueV.getAccountName() == null || clientFacingQueueV.getAccountName().toUpperCase().indexOf(clientNameFilter.trim().toUpperCase()) < 0) {
					clientFacingQueueItr.remove();
				}
			}
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

			for(ClientFacingQueueV clientFacingQueueV : masterList) {
				if(clientFacingQueueV.getUnitNo().equals(selectedFleetMaster.getUnitNo())) {
					clientFacingQueueV.setFollowUpDate(followUpDate);
					break;
				}
			}

			for(ClientFacingQueueV clientFacingQueueV : filteredList) {
				index += 1;
				if(clientFacingQueueV.getUnitNo().equals(selectedFleetMaster.getUnitNo())) {
					clientFacingQueueV.setFollowUpDate(followUpDate);
					break;
				}
			}

			if(!MALUtilities.isEmpty(followUpDate)) {
				formattedFollowUpDate = dateFormatter.format(followUpDate);
			} else {
				formattedFollowUpDate = "";
			}

			context.execute("$('[id$=\\\\:" + index + "\\\\:" + FOLLOW_UP_DATE_UI_ID + "]').text('" + formattedFollowUpDate + "')");
		}
	}
	
	public void getSelectedClientContact(ClientFacingQueueV clientFacingQueueV) {
		ContactInfo contactInfo = null;
		ExternalAccount externalAccount = new ExternalAccount(clientFacingQueueV.getCId(), clientFacingQueueV.getAccountType(), clientFacingQueueV.getAccountCode());
		ClientContactVO clientContactVO = clientPOCService.getDefaultPOCContact(externalAccount);
		
		if(!MALUtilities.isEmpty(clientContactVO)) {
			contactInfo = new ContactInfo();
			contactInfo.setName(clientContactVO.formattedName());
			contactInfo.setEmail(clientContactVO.getEmail());
			contactInfo.setPhone(clientContactVO.getPhoneWorkDisplay().isEmpty() ? clientContactVO.getPhoneCellDisplay() :  clientContactVO.getPhoneWorkDisplay());
		} 
		this.setRequestedContactInfo(contactInfo);
	}

	public void showUnitDetails(ClientFacingQueueV clientFacingQueueV) {
		String unitNo = clientFacingQueueV.getUnitNo();
		Long fmsId = fleetMasterService.findByUnitNo(unitNo).getFmsId();
		UnitInfo unitInfo = clientFacingQueueService.getSelectedUnitDetails(fmsId);
		setSelectedUnitNo(unitNo);
		this.setRequestedUnitInfo(unitInfo);
	}
	
	public void setSelectedFleetMasterVO(ClientFacingQueueV clientFacingQueueV) {
		if(!MALUtilities.isEmpty(clientFacingQueueV)) {
			selectedFleetMasterList = new ArrayList<FleetMaster>();
			selectedFleetMasterList.add(fleetMasterService.findByUnitNo(clientFacingQueueV.getUnitNo()));
		}
	}
	
	public void setSelectedFleetMasterVOs() {
		if(selectedClientFacingQueueList != null) {
			selectedFleetMasterList = new ArrayList<FleetMaster>();
			for(ClientFacingQueueV clientFacingQueueV : selectedClientFacingQueueList) {
				selectedFleetMasterList.add(fleetMasterService.findByUnitNo(clientFacingQueueV.getUnitNo()));
			}
		}
	}
	
	public void completeContactListener() {
		setSelectedRecordCount(getSelectedClientFacingQueueList().size());

		setNoteContactName(null);
		setNoteContactDate(null);		
		setNoteComment(null);
		
		((UIInput)getComponent(NOTE_CONTACT_NAME_UI_ID)).setValid(true);
		((UIInput)getComponent(NOTE_CONTACT_DATE_UI_ID)).setValid(true);
		((UIInput)getComponent(NOTE_CONTACT_NOTE_UI_ID)).setValid(true);			

		getSelectedFleetMasterList().clear();
		for(ClientFacingQueueV clientFacingQueueV : getSelectedClientFacingQueueList()){
			getSelectedFleetMasterList().add(fleetMasterService.findByUnitNo(clientFacingQueueV.getUnitNo()));
		}
		
		RequestContext.getCurrentInstance().update("completeContactDialog");
	}
	
	public void saveCompleteContactListener() {
		ObjectLogBook olb;
		String detail; 
		List<Long> queueItemIds;
		
		if(isValidCompleteContactInput()){
			detail = String.format(COMPLETE_CONTACT_DETAIL, noteContactName, 
					new SimpleDateFormat(MALUtilities.DATE_PATTERN).format(noteContactDate), 
					noteComment);

			//TODO Need to investigate how selected fleet master list is maintained
			for(FleetMaster unit : getSelectedFleetMasterList()){
				olb = logBookService.createObjectLogBook(unit, LogBookTypeEnum.TYPE_CLIENT_FACING_NOTES);			
				logBookService.addEntry(olb, super.getLoggedInUser().getEmployeeNo(),  detail, null, false);
			}
		
			queueItemIds = new ArrayList<Long>();		
			for(ClientFacingQueueV queueItem : getSelectedClientFacingQueueList()){
				queueItemIds.add(queueItem.getPsoId());
			}
			clientFacingQueueService.updateProcessStageObjectsIncludeFlag(queueItemIds, "N");	
			
			masterList.removeAll(getSelectedClientFacingQueueList());
			filteredList.removeAll(getSelectedClientFacingQueueList());
			
			setButtonDisabled(true);
			getSelectedFleetMasterList().clear();
			super.addSuccessMessage("process.success","Contact");
			
		}else{
			RequestContext context = RequestContext.getCurrentInstance();
			context.addCallbackParam("failure", true);
		}
				
	}
	
	public void cancelCompleteContactListener(){}
	
	private boolean isValidCompleteContactInput(){
		boolean isValid = true;
		
		if(MALUtilities.isEmpty(getNoteContactName())){
			super.addErrorMessageSummary(NOTE_CONTACT_NAME_UI_ID, "required.field", "Contact Name");
			((UIInput)getComponent(NOTE_CONTACT_NAME_UI_ID)).setValid(false);			
			isValid = false;
		}		
		if(MALUtilities.isEmpty(getNoteContactDate())){
			super.addErrorMessageSummary(NOTE_CONTACT_DATE_UI_ID, "required.field", "Contact Date");
			((UIInput)getComponent(NOTE_CONTACT_DATE_UI_ID)).setValid(false);			
			isValid = false;
		} else {
			if(getNoteContactDate().compareTo(Calendar.getInstance().getTime()) > 0) {
				super.addErrorMessageSummary(NOTE_CONTACT_DATE_UI_ID, "future.date.error", "Contact Date");				
				((UIInput)getComponent(NOTE_CONTACT_DATE_UI_ID)).setValid(false);			
				isValid = false;				
			}
		}
		if(MALUtilities.isEmpty(getNoteComment())){
			super.addErrorMessageSummary(NOTE_CONTACT_NOTE_UI_ID, "required.field", "Note");
			((UIInput)getComponent(NOTE_CONTACT_NOTE_UI_ID)).setValid(false);			
			isValid = false;
		}			
		
		return isValid;
	}	
	
	public void updateDoneFlag() {
		try {
			List<Long> processStageObjectIds = new ArrayList<Long>();
			for(ClientFacingQueueV clientFacingQueueV : selectedClientFacingQueueList) {
				processStageObjectIds.add(clientFacingQueueV.getPsoId());
			}
			clientFacingQueueService.updateProcessStageObjectsIncludeFlag(processStageObjectIds, "N");

			masterList.removeAll(selectedClientFacingQueueList);
			filteredList.removeAll(selectedClientFacingQueueList);
			
			setButtonDisabled(true);
			addSuccessMessage("custom.message", "Task(s) completed successfully");
			RequestContext.getCurrentInstance().update("clientFacingTable");
		} catch (Exception e) {
			logger.error(e);
			addErrorMessage("generic.error.occured.while", " updating units..");
		}
	}
	
	public void preProcessDownloadXLS(Object document) {
		downloadableRowsData.clear();
		downloadableColumns.clear();
		downloadableColumns.add("CSS/TM");
		downloadableColumns.add("Unit No");
		downloadableColumns.add("VIN");
		downloadableColumns.add("Trim");
		downloadableColumns.add("Exterior Color");
		downloadableColumns.add("Client Name");
		downloadableColumns.add("Client Contact");
		downloadableColumns.add("Requested Date");
		downloadableColumns.add("Expected Date");
		downloadableColumns.add("ETA Date");
		downloadableColumns.add("Follow Up Date");
		downloadableColumns.add("Reason");

		// setup dynamic data below
		ContactInfo clientContactInfo = null;
		ExternalAccount externalAccount = null;
		ClientContactVO clientContactVO = null;
		Long fmsId = 0L;
		UnitInfo unitInfo = null;
		Map<String, Object> row = null;
		for(ClientFacingQueueV clientFacingQueueV : filteredList) {
			clientContactInfo = null;
			externalAccount = new ExternalAccount(clientFacingQueueV.getCId(), clientFacingQueueV.getAccountType(), clientFacingQueueV.getAccountCode());
			clientContactVO = clientPOCService.getDefaultPOCContact(externalAccount);
			
			if(!MALUtilities.isEmpty(clientContactVO)) {
				clientContactInfo = new ContactInfo();
				clientContactInfo.setName(clientContactVO.formattedName());
				clientContactInfo.setEmail(clientContactVO.getEmail());
				clientContactInfo.setPhone(clientContactVO.getPhoneWorkDisplay().isEmpty() ? clientContactVO.getPhoneCellDisplay() : clientContactVO.getPhoneWorkDisplay());
			} 
			
			fmsId = fleetMasterService.findByUnitNo(clientFacingQueueV.getUnitNo()).getFmsId();
			unitInfo = clientFacingQueueService.getSelectedUnitDetails(fmsId);

			row = new HashMap<String, Object>();
			row.put("CSS/TM", clientFacingQueueV.getCssOrTm() != null ? clientFacingQueueV.getCssOrTm() : "NO CSS/TM ASSIGNED");
			row.put("Unit No", clientFacingQueueV.getUnitNo());
			row.put("VIN", unitInfo != null ? unitInfo.getVin() : "");
			row.put("Trim", clientFacingQueueV.getModelDesc());
			row.put("Exterior Color", unitInfo != null ? unitInfo.getColor() : "");
			row.put("Client Name", clientFacingQueueV.getAccountName());
			row.put("Client Contact", clientContactInfo == null ? "" : clientContactInfo.getExcelFormatedData());
			row.put("Requested Date", clientFacingQueueV.getRequestedDate() != null ? clientFacingQueueV.getRequestedDate() : "");
			row.put("Expected Date", MALUtilities.getNullSafeDatetoString(clientFacingQueueV.getExpectedDate()));
			row.put("ETA Date", MALUtilities.getNullSafeDatetoString(clientFacingQueueV.getClientETADate()));
			row.put("Follow Up Date", MALUtilities.getNullSafeDatetoString(clientFacingQueueV.getFollowUpDate()));
			row.put("Reason", clientFacingQueueV.getReason() != null ? clientFacingQueueV.getReason() : "");

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
		titleCell1.setCellValue("Client Facing");
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
	
	public List<ClientFacingQueueV> getMasterList() {
		return masterList;
	}

	public void setMasterList(List<ClientFacingQueueV> masterList) {
		this.masterList = masterList;
	}

	public List<ClientFacingQueueV> getFilteredList() {
		return filteredList;
	}

	public void setFilteredList(List<ClientFacingQueueV> filteredList) {
		this.filteredList = filteredList;
	}

	public List<ClientFacingQueueV> getSelectedClientFacingQueueList() {
		return selectedClientFacingQueueList;
	}

	public void setSelectedClientFacingQueueList(List<ClientFacingQueueV> selectedClientFacingQueueList) {
		this.selectedClientFacingQueueList = selectedClientFacingQueueList;
	}

	public String getSelectedUnitNo() {
		return selectedUnitNo;
	}

	public void setSelectedUnitNo(String selectedUnitNo) {
		this.selectedUnitNo = selectedUnitNo;
	}

	public ContactInfo getRequestedContactInfo() {
		return requestedContactInfo;
	}

	public void setRequestedContactInfo(ContactInfo requestedContactInfo) {
		this.requestedContactInfo = requestedContactInfo;
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

	public String getCssFilter() {
		return cssFilter;
	}

	public void setCssFilter(String cssFilter) {
		this.cssFilter = cssFilter;
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

	public String getEmptyDataTableMessage() {
		return emptyDataTableMessage;
	}

	public void setEmptyDataTableMessage(String emptyDataTableMessage) {
		this.emptyDataTableMessage = emptyDataTableMessage;
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

	public UnitInfo getRequestedUnitInfo() {
		return requestedUnitInfo;
	}

	public void setRequestedUnitInfo(UnitInfo requestedUnitInfo) {
		this.requestedUnitInfo = requestedUnitInfo;
	}
	
	public boolean isButtonDisabled() {
		return buttonDisabled;
	}

	public void setButtonDisabled(boolean buttonDisabled) {
		this.buttonDisabled = buttonDisabled;
	}

	public int getSelectedRecordCount() {
		return selectedRecordCount;
	}

	public void setSelectedRecordCount(int selectedRecordCount) {
		this.selectedRecordCount = selectedRecordCount;
	}

	public String getNoteContactName() {
		return noteContactName;
	}

	public void setNoteContactName(String noteContactname) {
		this.noteContactName = noteContactname;
	}

	public Date getNoteContactDate() {
		return noteContactDate;
	}

	public void setNoteContactDate(Date noteContactDate) {
		this.noteContactDate = noteContactDate;
	}

	public String getNoteComment() {
		return noteComment;
	}

	public void setNoteComment(String noteComment) {
		this.noteComment = noteComment;
	}

}
