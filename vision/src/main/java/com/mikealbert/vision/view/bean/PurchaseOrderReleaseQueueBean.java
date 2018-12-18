package com.mikealbert.vision.view.bean;

import static com.mikealbert.vision.comparator.PurchaseOrderReleaseQueueComparator.PO_STATUS_SORT;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mikealbert.data.dao.QuotationModelDAO;
import com.mikealbert.data.entity.Doc;
import com.mikealbert.data.entity.DocSupplier;
import com.mikealbert.data.entity.ExtAccAddress;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.ProcessStageObject;
import com.mikealbert.data.entity.PurchaseOrderReleaseQueueV;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.QuoteModelPropertyValue;
import com.mikealbert.data.entity.Supplier;
import com.mikealbert.data.enumeration.DocumentStatus;
import com.mikealbert.data.enumeration.LogBookTypeEnum;
import com.mikealbert.data.enumeration.OrderToDeliveryProcessStageEnum;
import com.mikealbert.data.enumeration.VehicleOrderType;
import com.mikealbert.data.util.DisplayFormatHelper;
import com.mikealbert.data.vo.DbProcParamsVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.DocumentService;
import com.mikealbert.service.FleetMasterService;
import com.mikealbert.service.ProcessStageService;
import com.mikealbert.service.PurchaseOrderService;
import com.mikealbert.service.QuotationService;
import com.mikealbert.service.QuoteModelPropertyValueService;
import com.mikealbert.service.UpfitterService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.service.PurchaseOrderReleaseQueueService;
import com.mikealbert.vision.service.UnitProgressService;
import com.mikealbert.vision.util.XLSUtil;
import com.mikealbert.vision.view.ViewConstants;
import com.mikealbert.vision.vo.ContactInfo;
import com.mikealbert.vision.vo.LogBookTypeVO;

@Component
@Scope("view")
public class PurchaseOrderReleaseQueueBean extends StatefulBaseBean {
	
	private static final long serialVersionUID = 8132043271690568678L;
	
	@Resource PurchaseOrderReleaseQueueService poReleaseQueueService;
	@Resource PurchaseOrderService purchaseOrderService;
	@Resource QuoteModelPropertyValueService quoteModelPropertyValueService;
	@Resource UpfitterService upfitterService;
	@Resource UnitProgressService unitProgressService;
	@Resource DocumentService docService;
	//TODO: this should be a service and a service method
	@Resource QuotationModelDAO quotationModelDAO;
	@Resource FleetMasterService fleetMasterService;
	@Resource ProcessStageService processStageService;
	
	private List<PurchaseOrderReleaseQueueV> filteredList = new ArrayList<PurchaseOrderReleaseQueueV>();
	private List<PurchaseOrderReleaseQueueV> masterList;
	private PurchaseOrderReleaseQueueV selectedPurchaseOrderReleaseQueueV;
	private String makeFilter;
	private String orderTypeFilter; 
	private String unitNoFilter;
	private String clientNameFilter;
	private String quoteCreatorFilter;
	private String poStatusFilter;
	private String orderingDealerFilter;
	private String releasedByFilter;	
	private boolean restoreView = false;
	private boolean initCalled = false;
	private boolean buttonDisabled = true;
	private Long scrollPosition;
	private String emptyDataTableMessage;
	private String selectedOrderingDealerName;
	private String selectedOrderingDealerAddressDetails;
	private ContactInfo  requestedContactInfo;
	private List<FleetMaster> selectedFleetMasterList;
	private List<LogBookTypeVO> logBookType = null;
	private List<ProcessStageObject> selectedProcessStageObjects;	
	private OrderToDeliveryProcessStageEnum targetProcessStage;
	
	private List<Map<String, Object>> downloadableRowsData = new ArrayList<Map<String, Object>>();;
	private List<String> downloadableColumns = new ArrayList<String>();
	
	static final String DELIVERING_WORKSHOP =  "DELIVERING";
	static final String ORDERING_WORKSHOP =  "ORDERING";

	private static final String DATA_TABLE_UI_ID = "DT_UI_ID";
	private int recordPerPage = ViewConstants.RECORDS_PER_PAGE_X_SMALL;
	private final String OPEN_PO = "Open";
	
	private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy");
	private static String EXCEL_DL_COOKIE = "client.excel.download";
	private boolean hasVehOrderStatusPermission = false;
	
	@PostConstruct
	public void init() {
		initializeDataTable(640, 850, new int[] {2, 4, 3, 3, 15, 14, 5, 7, 4, 4 });
		setButtonDisabled(true);
		initCalled = true;
		this.setEmptyDataTableMessage(talMessage.getMessage("no.records.found"));
		openPage();	
		setLogBookType(new ArrayList<LogBookTypeVO>());
		setSelectedFleetMasterList(new ArrayList<FleetMaster>());
		hasVehOrderStatusPermission = hasPermission("manage_veh_order_status");
		if(restoreView) {
			performSearch();
			this.selectedPurchaseOrderReleaseQueueV = (PurchaseOrderReleaseQueueV) thisPage.getRestoreStateValues().get(ViewConstants.DT_SELECTED_ITEM);
			thisPage.getRestoreStateValues().remove(ViewConstants.DT_SELECTED_ITEM);
		}else{
			clearFilter();
			this.setEmptyDataTableMessage(talMessage.getMessage("before.search.datattable.message"));
		}
		restoreView = false;
	}
	
	public void loadData(){		
		try {	
			
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

			getLogBookType().add(new LogBookTypeVO(LogBookTypeEnum.TYPE_PURCHASE_ORDER_NOTES, LogBookTypeEnum.TYPE_UPFIT_PRG_NOTES, ViewConstants.LABEL_PROGRESS_CHASING, false));
			getLogBookType().get(0).getRelatedLogBookTypes().add(LogBookTypeEnum.TYPE_IN_SERV_PRG_NOTES);
			getLogBookType().get(0).getRelatedLogBookTypes().add(LogBookTypeEnum.TYPE_CLIENT_FACING_NOTES);
			getLogBookType().get(0).getRelatedLogBookTypes().add(LogBookTypeEnum.TYPE_VEHICLE_STATUS_NOTES);
			getLogBookType().get(0).setRenderEntrySource(true);
			getLogBookType().add(new LogBookTypeVO(LogBookTypeEnum.TYPE_EXTERNAL_TAL_FILE_NOTES, true));
			
			masterList = poReleaseQueueService.findPurchaseOrderQueueList();
			filteredList.addAll(masterList);
			if(!initCalled){
				clearSelection();
				thisPage.getRestoreStateValues().clear();
				scrollPosition = 0l;
				DataTable dt = (DataTable) getComponent(DATA_TABLE_UI_ID);	
				dt.setFirst(0);
				dt.setValueExpression("sortBy", null);
			}
			initCalled = false;
		} catch (Exception e) {
			logger.error(e);
			if(e instanceof MalBusinessException) {
				super.addErrorMessage(e.getMessage());
			} else {
				super.addErrorMessage("generic.error.occured.while", " fetching units..");
			}
		}
	}	
	
	public int poStatusCustomSort(Object o1, Object o2){
		 return PO_STATUS_SORT.compare((PurchaseOrderReleaseQueueV)o1, (PurchaseOrderReleaseQueueV)o2);		
	}
	
	public void performSearch() {
		clearSelection();
		filteredList.clear();
		setButtonDisabled(true);
		
		if(masterList == null || masterList.size() == 0) {
			loadData();
		}else{
			filteredList.addAll(masterList);
		}
		
		String docStatus = null;
		PurchaseOrderReleaseQueueV purchaseOrderQueueV = null;
		for(Iterator<PurchaseOrderReleaseQueueV> purchaseOrderReleaseItr = filteredList.iterator(); purchaseOrderReleaseItr.hasNext();) {
			purchaseOrderQueueV = purchaseOrderReleaseItr.next();
			
			if(MALUtilities.isNotEmptyString(makeFilter)) {
				if(purchaseOrderQueueV.getMake() == null || purchaseOrderQueueV.getMake().toUpperCase().indexOf(makeFilter.trim().toUpperCase()) < 0) {
					purchaseOrderReleaseItr.remove();
					continue;
				}
			}
			
			if(MALUtilities.isNotEmptyString(orderTypeFilter)) {
				if(purchaseOrderQueueV.getOrderType() == null || purchaseOrderQueueV.getOrderType().toUpperCase().indexOf(orderTypeFilter.trim().toUpperCase()) < 0) {
					purchaseOrderReleaseItr.remove();
					continue;
				}
			}
			
			if(MALUtilities.isNotEmptyString(unitNoFilter)) {
				if(purchaseOrderQueueV.getUnitNo() == null || purchaseOrderQueueV.getUnitNo().toUpperCase().indexOf(unitNoFilter.trim().toUpperCase()) < 0) {
					purchaseOrderReleaseItr.remove();
					continue;
				}
			}
			if(MALUtilities.isNotEmptyString(clientNameFilter)) {
				if(purchaseOrderQueueV.getClientAccountName() == null ){
					purchaseOrderReleaseItr.remove();
					continue;
				}
				if(purchaseOrderQueueV.getClientAccountName().toUpperCase().indexOf(clientNameFilter.trim().toUpperCase()) < 0){
					if(purchaseOrderQueueV.getClientAccountCode().toUpperCase().indexOf(clientNameFilter.trim().toUpperCase()) < 0){
						purchaseOrderReleaseItr.remove();
						continue;
					}
				}
			}
			
			if(MALUtilities.isNotEmptyString(quoteCreatorFilter)) {
				if(purchaseOrderQueueV.getQuoteCreator() == null || purchaseOrderQueueV.getQuoteCreator().toUpperCase().indexOf(quoteCreatorFilter.trim().toUpperCase()) < 0) {
					purchaseOrderReleaseItr.remove();
					continue;
				}
			}
			
			if(MALUtilities.isNotEmptyString(poStatusFilter)) {
				if (purchaseOrderQueueV.getPoStatus() == null) {
					docStatus = DocumentStatus.PURCHASE_ORDER_STATUS_OPEN.getDescription();
				} else {
					docStatus = purchaseOrderQueueV.getPoStatus();
				}
				if(docStatus == null || docStatus.toUpperCase().indexOf(poStatusFilter.trim().toUpperCase()) < 0) {
					purchaseOrderReleaseItr.remove();
					continue;
				}
			}
		}
		
		if(filteredList == null || filteredList.size() == 0) {
			this.setEmptyDataTableMessage(talMessage.getMessage("no.records.found"));
		}
	}
	
	public void loadTableWithoutData(){
		
		if(downloadableRowsData != null)
			downloadableRowsData.clear();
		if(downloadableColumns != null)
			downloadableColumns.clear();
		if(masterList != null)
			masterList.clear();
		if(filteredList != null)
			filteredList.clear();	
		
		clearSelection();
		clearFilter();
		setButtonDisabled(true);
		this.setEmptyDataTableMessage(talMessage.getMessage("before.search.datattable.message"));
	}
	
	public void preProcessDownloadXLS(Object document) {

		downloadableRowsData.clear();
		downloadableColumns.clear();
		downloadableColumns.add("Make");
		downloadableColumns.add("Order Type");
		downloadableColumns.add("Unit No");
		downloadableColumns.add("Trim");
		downloadableColumns.add("Client");
		downloadableColumns.add("Quote No");
		downloadableColumns.add("Quote Creator");
		downloadableColumns.add("Reqd By");
		downloadableColumns.add("PO Status");
		downloadableColumns.add("PO Number");
		downloadableColumns.add("PO Released");
		downloadableColumns.add("Vendor");

		for (PurchaseOrderReleaseQueueV purchaseOrderReleaseQueueV : filteredList) {
			Map<String, Object> row = new HashMap<String, Object>();
			row.put("Make", purchaseOrderReleaseQueueV.getMake());
			row.put("Order Type", purchaseOrderReleaseQueueV.getOrderType());
			row.put("Unit No", purchaseOrderReleaseQueueV.getUnitNo());
			row.put("Trim", purchaseOrderReleaseQueueV.getTrim());
			row.put("Client", purchaseOrderReleaseQueueV.getClientAccountName());
			row.put("Quote No", purchaseOrderReleaseQueueV.getQuoteNumber());
			row.put("Quote Creator", purchaseOrderReleaseQueueV.getQuoteCreator());
			row.put("Reqd By", purchaseOrderReleaseQueueV.getQuoteRequiredDate());
			row.put("PO Status", purchaseOrderReleaseQueueV.getPoStatus());
			row.put("PO Number", purchaseOrderReleaseQueueV.getPoNumber());
			row.put("PO Released", MALUtilities.isEmpty(purchaseOrderReleaseQueueV.getPoReleaseDate()) ?  null : dateFormatter.format(purchaseOrderReleaseQueueV.getPoReleaseDate()));
			row.put("Vendor", purchaseOrderReleaseQueueV.getVendorName());
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
		titleCell1.setCellValue("Purchase Order Release");
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
	
	public void navigateToManufacturerQueue(){
		
		Map<String, Object> nextPageValues = new HashMap<String, Object>();
    	nextPageValues.put("searchType", String.valueOf(0));
    	super.pageList.remove(super.pageList.size()-1);
	    saveNextPageInitStateValues(nextPageValues);
		forwardToURL(ViewConstants.MANUFACTURER_PROGRESS_QUEUE);
	}
	
	public void navigateToThirdPartyQueue(){
		Map<String, Object> nextPageValues = new HashMap<String, Object>();
    	nextPageValues.put("searchType", String.valueOf(0));
    	super.pageList.remove(super.pageList.size()-1);
	    saveNextPageInitStateValues(nextPageValues);
		forwardToURL(ViewConstants.THIRD_PARTY_PROGRESS_QUEUE);
	}
	
	public String navigateToMaintainPOScreen(){
		if(getSelectedPurchaseOrderReleaseQueueV() == null){
			addErrorMessage("custom.message", "Please select a unit");		
			return null;
		}
		
		boolean isPoCreated = getSelectedPurchaseOrderReleaseQueueV().getDocId() == null ? false : true;
		boolean isValid = true;
		
		if(MALUtilities.isEmpty(getSelectedPurchaseOrderReleaseQueueV().getPoStatus()) && !MALUtilities.isEmpty(getSelectedPurchaseOrderReleaseQueueV().getQmdId())){
			isValid = validateQuoteStatus(getSelectedPurchaseOrderReleaseQueueV().getQmdId());
		}else{
			isValid = validatePoStatus(getSelectedPurchaseOrderReleaseQueueV().getDocId());
		}
		
		if(isValid && (! isPoCreated || getSelectedPurchaseOrderReleaseQueueV().getPoStatus().equals(OPEN_PO))){
			isValid = validatePoRequirements(getSelectedPurchaseOrderReleaseQueueV());
		}
		if(!isValid){
			return null;
		}
			
		if (! isPoCreated) {
			isPoCreated = createPurchaseOrder(getSelectedPurchaseOrderReleaseQueueV().getQmdId());
		} 
			
		if (isPoCreated) {
			saveRestoreStateValues(getCurrentPageRestoreStateValuesMap());
			Map<String, Object> nextPageValues = new HashMap<String, Object>();
	    	nextPageValues.put("qmdId", getSelectedPurchaseOrderReleaseQueueV().getQmdId());
	    	nextPageValues.put("docId", getSelectedPurchaseOrderReleaseQueueV().getDocId());
	    	nextPageValues.put("psoId", getSelectedPurchaseOrderReleaseQueueV().getPsoId());
		    saveNextPageInitStateValues(nextPageValues);
			    
			    
			forwardToURL(ViewConstants.MAINTAIN_PURCHASE_ORDER);
		}
		return null;
	}
	
	
	private boolean validateQuoteStatus(Long qmdId) {
		boolean isValid = true;
		//TODO: this should be a service and a service method
		QuotationModel qmd = quotationModelDAO.findById(qmdId).orElse(null);
		if(MALUtilities.isEmpty(qmd) || (QuotationService.STATUS_ACCEPTED != qmd.getQuoteStatus())){
			isValid = false;
			super.addErrorMessage("custom.message", "The entry has been updated. Please refresh the display to reflect the update.");
		}
		return isValid;
	}
	
	private boolean validatePoStatus(Long docId){
		boolean isValid = true;
		Doc doc = docService.getDocumentByDocId(docId);
		
		if(!getSelectedPurchaseOrderReleaseQueueV().getPoStatus().equals(MALUtilities.getNullSafeString(DocumentStatus.getDescriptionByCode(doc.getDocStatus())))){
			isValid = false;
			super.addErrorMessage("custom.message", "The entry has been updated. Please refresh the display to reflect the update.");
		}
		return isValid;
	}

	private Map<String, Object> getCurrentPageRestoreStateValuesMap() {
		
		Map<String, Object> restoreStateValues = new HashMap<String, Object>();
	
		restoreStateValues.put("MAKE_FILTER", makeFilter);
		restoreStateValues.put("ORDER_TYPE_FILTER", orderTypeFilter);
		restoreStateValues.put("UNIT_FILTER", unitNoFilter);
		restoreStateValues.put("CLIENT_NAME_FILTER", clientNameFilter);
		restoreStateValues.put("QUOTE_PO_CREATOR_FILTER", quoteCreatorFilter);
		restoreStateValues.put("PO_STATUS_FILTER", poStatusFilter);
		restoreStateValues.put("SCROLL_POSITION", scrollPosition);
		
		DataTable dt = (DataTable) getComponent(DATA_TABLE_UI_ID);
		if (dt != null) {
			ValueExpression ve = dt.getValueExpression("sortBy");
			restoreStateValues.put(ViewConstants.DT_DEFAULT_SORT_VE, ve);
			restoreStateValues.put(ViewConstants.DT_SELECTD_PAGE_START_INDEX, dt.getFirst());
			restoreStateValues.put(ViewConstants.DT_DEFAULT_SORT_ORDER, dt.getSortOrder());
			restoreStateValues.put(ViewConstants.DT_SELECTED_ITEM, getSelectedPurchaseOrderReleaseQueueV());
		}
		return restoreStateValues;
		
	}
	
	private boolean createPurchaseOrder(Long qmdId) {
	
		DbProcParamsVO parameterVO =  null;
		try {
			String  employeeNo = getLoggedInUser().getEmployeeNo();
			Long  corpId = getLoggedInUser().getCorporateEntity().getCorpId();
			 parameterVO = purchaseOrderService.createPurchaseOrder(qmdId, corpId, employeeNo);
			if(MALUtilities.isNotEmptyString(parameterVO.getMessage())){
				throw new Exception(parameterVO.getMessage());
				
			}else{
				getSelectedPurchaseOrderReleaseQueueV().setDocId(parameterVO.getReturnId().longValue());//set created doc id in current selected object
				addSuccessMessage("custom.message", "Purchase order has been created successfully");
				if(getSelectedPurchaseOrderReleaseQueueV().getOrderType().equals(VehicleOrderType.FACTORY.getDescription())){
					updateOrderingDealerCode(qmdId, getSelectedPurchaseOrderReleaseQueueV().getDocId());
				}
				return true;
			}
			
		} catch (Exception e) {
			if(MALUtilities.isNotEmptyString(parameterVO.getMessage())){
				addErrorMessage("custom.message", parameterVO.getMessage());	
			}else{
				addErrorMessage("generic.error.occured.while", "creating purchase order");
			}
		}
		return false;
	}
	
	private void updateOrderingDealerCode(Long qmdId, Long docId) {
		
		try {			
		
			List<ExternalAccount> prefferedVendorList = purchaseOrderService.getPreferredVendorByQmdId(qmdId);
			int prefVendorCount = !MALUtilities.isEmpty(prefferedVendorList) ? prefferedVendorList.size() : 0;
			
			Supplier prefVendor = null;
			
			int orderingCapabilityCount = 0;
			String vendorCodeAndName = "";
			if(prefVendorCount == 1 ){
				List<Supplier> supList = purchaseOrderService.getPreferredSupplierListByQmdId(qmdId);
				for(Supplier supPref : supList){
					if(supPref.isOrderingWorkShopCapability()){
						orderingCapabilityCount = orderingCapabilityCount + 1;
						prefVendor = supPref;
					}
					if(MALUtilities.isNotEmptyString(vendorCodeAndName)){
						vendorCodeAndName = vendorCodeAndName + ", ";
					}
					vendorCodeAndName = vendorCodeAndName + supPref.getSupplierName() + "-" + supPref.getSupplierCode();
				}
				if(orderingCapabilityCount == 1 && !MALUtilities.isEmpty(prefVendor)){
					// replace default with preferred vendor.
					boolean success = updatePreferredVendor(docId, prefVendor);
					if(success){
						addSuccessMessage("custom.message", "Purchase order was created using client’s preferred vendor");
					}
					
				}else{
					addErrorMessage("custom.message", "Client has preferred vendor "+ vendorCodeAndName + " but it is not set up correctly with ORDERING capability");
				}
			}else if(prefVendorCount > 1 ){
				// more than 1 preferred vendors
				for(ExternalAccount extAccount : prefferedVendorList){
					if(MALUtilities.isNotEmptyString(vendorCodeAndName)){
						vendorCodeAndName = vendorCodeAndName + ", ";
					}
					vendorCodeAndName = vendorCodeAndName + extAccount.getAccountName() + "-" + extAccount.getExternalAccountPK().getAccountCode();
				}
				addErrorMessage("custom.message", "Client has the following preferred vendors "+ vendorCodeAndName + ". Select one of these or choose an ordering dealer from the list");
			}
			
		} catch (Exception e) {
			logger.error(e ,"Unable to update preferred vendors");
			addErrorMessage("custom.message", "Unable to update preferred vendors");
		}
	}
	
	public Supplier getDefaultSupplier(Long qmdId, Long docId){
		return purchaseOrderService.getDefaultSupplier(qmdId, docId);
	}
	
	private boolean updatePreferredVendor(Long docId , Supplier prefVendor){
		DbProcParamsVO parameterVO = null;
		try {
			parameterVO = purchaseOrderService.updatePreferredVendor(docId, prefVendor.getSupId());
			if(MALUtilities.isNotEmptyString(parameterVO.getMessage())){
				throw new Exception(parameterVO.getMessage());
				
			}else{
				return true;
			}
			
		} catch (Exception e) {
			if(parameterVO != null && MALUtilities.isNotEmptyString(parameterVO.getMessage())){
				addErrorMessage("custom.message", parameterVO.getMessage());	
			}else{
				logger.error(e, "updating client preferred vendor");
				addErrorMessage("generic.error.occured.while", "updating client's preferred vendor");
			}
		}
		return false;
	}
	
	public void getSelectedOrderingDealerContact(PurchaseOrderReleaseQueueV purchaseOrderReleaseQueueV)  {
		setSelectedPurchaseOrderReleaseQueueV(purchaseOrderReleaseQueueV);
		ContactInfo contactInfo = null;
		setSelectedOrderingDealerAddressDetails(null);
		setRequestedContactInfo(null);
		setSelectedOrderingDealerName(null);
		
		DocSupplier orderingDealer = purchaseOrderService.getOrderingDealerByDocId(purchaseOrderReleaseQueueV.getDocId());
		
		if (!MALUtilities.isEmpty(orderingDealer)) {
			if (purchaseOrderReleaseQueueV.getOrderType().equals(VehicleOrderType.LOCATE.getDescription())) {
				contactInfo = unitProgressService.getDealerContactInfo(orderingDealer.getSupplier().getSupplierCode());
			}
			
			if(MALUtilities.isEmpty(contactInfo)) {
				contactInfo = unitProgressService.getVendorSupplierContactInfo(orderingDealer.getSupplier().getSupplierCode());
			}
			
			ExtAccAddress orderingDealerAddress = upfitterService.getUpfitterDefaultPostAddress(orderingDealer.getSupplier().getSupplierCode(), super.getLoggedInUser().getCorporateEntity().getCorpId());
			setSelectedOrderingDealerAddressDetails(DisplayFormatHelper.formatAddressForTable(null, orderingDealerAddress.getAddressLine1(), orderingDealerAddress.getAddressLine2(), null, null, orderingDealerAddress.getCityDescription(), orderingDealerAddress.getRegionAbbreviation(), orderingDealerAddress.getPostcode(), "\n"));
			this.setRequestedContactInfo(contactInfo);
			setSelectedOrderingDealerName(orderingDealer.getSupplier().getSupplierName());
		}
		
	}
	
	protected void loadNewPage() {
		thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_PURCHASE_ORDER_RELEASE_QUEUE);
		thisPage.setPageUrl(ViewConstants.PURCHASE_ORDER_RELEASE_QUEUE);
		this.setEmptyDataTableMessage(talMessage.getMessage("no.records.found"));
		this.restoreView = false;
	}

	protected void restoreOldPage() {	
		
		boolean isMainPOReleased = !MALUtilities.isEmpty(thisPage.getRestoreStateValues().get("IS_MAIN_PO_RELEASED")) ? (Boolean)thisPage.getRestoreStateValues().get("IS_MAIN_PO_RELEASED") : false; 
	
		
		this.makeFilter = (String) thisPage.getRestoreStateValues().get("MAKE_FILTER");		
		this.orderTypeFilter = (String) thisPage.getRestoreStateValues().get("ORDER_TYPE_FILTER");
		this.unitNoFilter = (String) thisPage.getRestoreStateValues().get("UNIT_FILTER");
		this.clientNameFilter = (String) thisPage.getRestoreStateValues().get("CLIENT_NAME_FILTER");
		this.quoteCreatorFilter = (String) thisPage.getRestoreStateValues().get("QUOTE_PO_CREATOR_FILTER");
		this.poStatusFilter = (String) thisPage.getRestoreStateValues().get("PO_STATUS_FILTER");
		this.scrollPosition = (Long)thisPage.getRestoreStateValues().get("SCROLL_POSITION");
		
		thisPage.getRestoreStateValues().remove("MAKE_FILTER");
		thisPage.getRestoreStateValues().remove("ORDER_TYPE_FILTER");
		thisPage.getRestoreStateValues().remove("UNIT_FILTER");
		thisPage.getRestoreStateValues().remove("CLIENT_NAME_FILTER");
		thisPage.getRestoreStateValues().remove("QUOTE_PO_CREATOR_FILTER");
		thisPage.getRestoreStateValues().remove("PO_STATUS_FILTER");
		thisPage.getRestoreStateValues().remove("SCROLL_POSITION");
		
		DataTable dt = (DataTable) getComponent(DATA_TABLE_UI_ID);	
		Integer pageStartIndex = (Integer) thisPage.getRestoreStateValues().get(ViewConstants.DT_SELECTD_PAGE_START_INDEX);
		if(pageStartIndex != null){
			dt.setFirst(pageStartIndex);
			dt.setSortOrder((String) thisPage.getRestoreStateValues().get(ViewConstants.DT_DEFAULT_SORT_ORDER));
			dt.setValueExpression("sortBy", (ValueExpression) thisPage.getRestoreStateValues().get(ViewConstants.DT_DEFAULT_SORT_VE));
			this.selectedPurchaseOrderReleaseQueueV = (PurchaseOrderReleaseQueueV) thisPage.getRestoreStateValues().get(ViewConstants.DT_SELECTED_ITEM);
			if(isMainPOReleased){
				this.selectedPurchaseOrderReleaseQueueV.setPoStatus("Released");
			}
			
		}
		thisPage.getRestoreStateValues().remove(ViewConstants.DT_DEFAULT_SORT_VE);
		thisPage.getRestoreStateValues().remove(ViewConstants.DT_DEFAULT_SORT_ORDER);
		thisPage.getRestoreStateValues().remove(ViewConstants.DT_SELECTD_PAGE_START_INDEX);
		this.restoreView = true;
	}
	
	public String cancel() {
		return super.cancelPage();
	}

	public void clearFilter() {
		this.clientNameFilter = "";
		this.unitNoFilter = "";
		this.makeFilter = "";
		this.orderTypeFilter = "";
		this.quoteCreatorFilter= "";
		this.poStatusFilter= "";
	}
	
	/**
	 * Validate and restrict if Po process needs to be
	 * completed from PO001 in few conditions as,
	 * quote or po has missing information.
	 */
	private boolean validatePoRequirements(PurchaseOrderReleaseQueueV poReleaseQueueV){
		boolean isValid = true;
		
		if(MALUtilities.isEmpty(getSelectedPurchaseOrderReleaseQueueV().getDocId())){
			int nonAutoDataDlrAccWithoutVendor = purchaseOrderService.getNonAutoDataDealerAccWithoutVendorCount(poReleaseQueueV.getQmdId());
			QuoteModelPropertyValue qmpv = quoteModelPropertyValueService.findByNameAndQmdId("SUPPLIER", poReleaseQueueV.getQmdId());
			
			if((nonAutoDataDlrAccWithoutVendor > 0)
					|| (poReleaseQueueV.getOrderType().equals(VehicleOrderType.LOCATE.getDescription()) && MALUtilities.isEmpty(qmpv))
					|| (!MALUtilities.isEmpty(qmpv) && MALUtilities.isEmpty(qmpv.getPropertyValue()))){
				
				addErrorMessage("custom.message", "Quote does not have enough information to create purchase orders. Please use PO001 to create and release this order.");
				isValid = false;
				return false;
			}
		}
		
		if(!MALUtilities.isEmpty(poReleaseQueueV.getPoStatus()) && poReleaseQueueV.getPoStatus().equals(OPEN_PO)){
			boolean isOrdDelDealerExist = purchaseOrderService.isOrdeDelDealerExistInDocSupplier(poReleaseQueueV.getDocId());
			if(!isOrdDelDealerExist){
				addErrorMessage("custom.message", "Purchase order does not have enough information to continue. Please use PO001 to release this order.");
				isValid = false;
				return false;
			}
		}
		
		return isValid;
	}
	
	public void captureSelectedProcessStageObjects(){
		if(selectedPurchaseOrderReleaseQueueV != null) {
			setSelectedProcessStageObjects(new ArrayList<ProcessStageObject>());
			getSelectedProcessStageObjects().add(processStageService.getStagedObject(selectedPurchaseOrderReleaseQueueV.getPsoId()));
		}
	}
	
	public void requestClientContactListner() {
		setTargetProcessStage(OrderToDeliveryProcessStageEnum.CLIENT_FACING);
		captureSelectedProcessStageObjects();			
	}
	
	public void refreshRequestClientContactListener(){
		addSuccessMessage("custom.message", "Unit is added to client facing queue");
	}
	
	public void clearSelection() {
		setSelectedPurchaseOrderReleaseQueueV(null);
	}
	
	public List<PurchaseOrderReleaseQueueV> getMasterList() {
		return masterList;
	}

	public void setMasterList(List<PurchaseOrderReleaseQueueV> masterList) {
		this.masterList = masterList;
	}

	public List<PurchaseOrderReleaseQueueV> getFilteredList() {
		return filteredList;
	}

	public void setFilteredList(List<PurchaseOrderReleaseQueueV> filteredList) {
		this.filteredList = filteredList;
	}
	
	public PurchaseOrderReleaseQueueV getSelectedPurchaseOrderReleaseQueueV() {
		return selectedPurchaseOrderReleaseQueueV;
	}

	public void setSelectedPurchaseOrderReleaseQueueV(PurchaseOrderReleaseQueueV selectedPurchaseOrderReleaseQueueV) {
		this.selectedPurchaseOrderReleaseQueueV = selectedPurchaseOrderReleaseQueueV;
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

	public String getOrderingDealerFilter() {
		return orderingDealerFilter;
	}

	public void setOrderingDealerFilter(String orderingDealerFilter) {
		this.orderingDealerFilter = orderingDealerFilter;
	}

	public String getReleasedByFilter() {
		return releasedByFilter;
	}

	public void setReleasedByFilter(String releasedByFilter) {
		this.releasedByFilter = releasedByFilter;
	}

	public String getEmptyDataTableMessage() {
		return emptyDataTableMessage;
	}

	public void setEmptyDataTableMessage(String emptyDataTableMessage) {
		this.emptyDataTableMessage = emptyDataTableMessage;
	}
	
	public String getOrderTypeFilter() {
		return orderTypeFilter;
	}

	public void setOrderTypeFilter(String orderTypeFilter) {
		this.orderTypeFilter = orderTypeFilter;
	}

	public String getQuoteCreatorFilter() {
		return quoteCreatorFilter;
	}

	public void setQuoteCreatorFilter(String quoteCreatorFilter) {
		this.quoteCreatorFilter = quoteCreatorFilter;
	}

	public String getPoStatusFilter() {
		return poStatusFilter;
	}

	public void setPoStatusFilter(String poStatusFilter) {
		this.poStatusFilter = poStatusFilter;
	}

	public Long getScrollPosition() {
		return scrollPosition;
	}

	public void setScrollPosition(Long scrollPosition) {
		this.scrollPosition = scrollPosition;
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

	public List<FleetMaster> getSelectedFleetMasterList() {
		return selectedFleetMasterList;
	}

	public void setSelectedFleetMasterList(List<FleetMaster> selectedFleetMasterList) {
		if(selectedPurchaseOrderReleaseQueueV != null) {
			selectedFleetMasterList = new ArrayList<FleetMaster>();
			selectedFleetMasterList.add(fleetMasterService.findByUnitNo(selectedPurchaseOrderReleaseQueueV.getUnitNo()));
		}
	}
	public void setSelectedFleetMasterVOs() {
		if(selectedPurchaseOrderReleaseQueueV != null) {
			selectedFleetMasterList = new ArrayList<FleetMaster>();
			selectedFleetMasterList.add(fleetMasterService.findByUnitNo(selectedPurchaseOrderReleaseQueueV.getUnitNo()));
		}
	}

	public List<LogBookTypeVO> getLogBookType() {
		return logBookType;
	}

	public void setLogBookType(List<LogBookTypeVO> logBookType) {
		this.logBookType = logBookType;
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

	public void setTargetProcessStage(
			OrderToDeliveryProcessStageEnum targetProcessStage) {
		this.targetProcessStage = targetProcessStage;
	}

	public boolean isHasVehOrderStatusPermission() {
		return hasVehOrderStatusPermission;
	}

	public void setHasVehOrderStatusPermission(boolean hasVehOrderStatusPermission) {
		this.hasVehOrderStatusPermission = hasVehOrderStatusPermission;
	}

	public int getRecordPerPage() {
		return recordPerPage;
	}

	public void setRecordPerPage(int recordPerPage) {
		this.recordPerPage = recordPerPage;
	}

}
