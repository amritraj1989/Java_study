package com.mikealbert.vision.view.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mikealbert.data.dao.DocDAO;
import com.mikealbert.data.entity.Doc;
import com.mikealbert.data.entity.ExtAccAddress;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.ThirdPartyPoQueueV;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.data.enumeration.DocumentStatus;
import com.mikealbert.data.enumeration.EmailRequestEventEnum;
import com.mikealbert.data.enumeration.ReportNameEnum;
import com.mikealbert.data.util.DisplayFormatHelper;
import com.mikealbert.data.vo.DbProcParamsVO;
import com.mikealbert.data.vo.VendorInfoVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.DocumentService;
import com.mikealbert.service.ModelService;
import com.mikealbert.service.ProcessStageService;
import com.mikealbert.service.PurchaseOrderService;
import com.mikealbert.service.QuotationService;
import com.mikealbert.service.UpfitProgressService;
import com.mikealbert.service.UpfitterService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.service.PurchasingEmailService;
import com.mikealbert.vision.service.UnitProgressService;
import com.mikealbert.vision.util.XLSUtil;
import com.mikealbert.vision.view.ViewConstants;
import com.mikealbert.vision.vo.ContactInfo;
import com.mikealbert.vision.vo.DocumentListItemVO;
import com.mikealbert.vision.vo.UpfitterPurchaseOrderListItemVO;

@Component
@Scope("view")
public class ThirdPartyProgressQueueBean extends StatefulBaseBean {
	
	private static final long serialVersionUID = 8674993086443075577L;
	
	@Resource PurchaseOrderService purchaseOrderService;
	@Resource UpfitterService upfitterService; 
	@Resource UpfitProgressService upfitProgressService; 
	@Resource UnitProgressService unitProgressService;
	@Resource QuotationService quotationService;
	@Resource ProcessStageService processStageService;
	@Resource DocDAO docDAO; //TODO ANTI-PATTERN::: We should never call the DAO layer directly from the application layer. This call should go through the service layer.
	@Resource PurchasingEmailService purchasingEmailService;
	@Resource DocumentService documentService;
	@Resource ModelService modelService;
	
	private List<ThirdPartyPoQueueV> filteredList = new ArrayList<ThirdPartyPoQueueV>();
	private List<ThirdPartyPoQueueV> masterList;
	private ThirdPartyPoQueueV selectedThirdPartyPoQueueV;
	private String unitNoFilter;
	private String clientNameFilter;
	private String vendorNameFilter;
	private String emptyDataTableMessage;
	private boolean showAccessories;
	private String selectedVendorDealerAddressDetails;
	private ContactInfo  requestedContactInfo;
	private String  dealerVendorNameOfRequest;
	private String  requestedAccessoriesVendor;
	private Map<String, List<String>> requestedVendorQuoteNoAccessories;
	private List<String> requestedVendorQuoteNoList;
	private Long scrollPosition;
	private String unitNo;

	private List<Map<String, Object>> downloadableRowsData = new ArrayList<Map<String, Object>>();;
	private List<String> downloadableColumns = new ArrayList<String>();
	
	private static String EXCEL_DL_COOKIE = "client.excel.download";
	
	private QuotationModel selectedQuotationModel;
	private List<DocumentStatus> upfitPOStatuses;
	
	private List<DocumentListItemVO> documentListItems = new ArrayList<DocumentListItemVO>();	
	private boolean showReleaseDocuments = false;
	
	private String successfulReleaseMessage = null;
	
	@PostConstruct
	public void init() {
		initializeDataTable(640, 850, new int[] {2, 4, 15, 16, 4, 10 });		
		openPage();				
		//loadData();	
		clearFilter();
		
		setUpfitPOStatuses(new ArrayList<DocumentStatus>());
		getUpfitPOStatuses().add(DocumentStatus.PURCHASE_ORDER_STATUS_OPEN);
		getUpfitPOStatuses().add(DocumentStatus.PURCHASE_ORDER_STATUS_RELEASED);		
		setEmptyDataTableMessage(talMessage.getMessage("before.search.datattable.message"));
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
			//clearFilter();
			masterList = purchaseOrderService.findThirdPartyPoQueueList();			
			//filteredList.addAll(masterList);		
		} catch (Exception e) {
			logger.error(e);
			if(e instanceof MalBusinessException) {
				super.addErrorMessage(e.getMessage());
			} else {
				super.addErrorMessage("generic.error.occured.while", " fetching units..");
			}
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
			
//			thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_THIRD_PARTY_PROGRESS_QUEUE);
//			thisPage.setPageUrl(ViewConstants.THIRD_PARTY_PROGRESS_QUEUE);
			setEmptyDataTableMessage(talMessage.getMessage("before.search.datattable.message"));
		}
	
	public void performFilter() {
		
		if(masterList == null || masterList.size() == 0) {
			loadData();
		}
		
		clearSelection();
		filteredList.clear();
		filteredList.addAll(masterList);
		
		if(MALUtilities.isNotEmptyString(unitNoFilter)) {
			ThirdPartyPoQueueV thirdPartyPoReleaseQueueV = null;
			for(Iterator<ThirdPartyPoQueueV> thirdPartyPoReleaseItr = filteredList.iterator(); thirdPartyPoReleaseItr.hasNext();) {
				thirdPartyPoReleaseQueueV = thirdPartyPoReleaseItr.next();
				if(thirdPartyPoReleaseQueueV.getUnitNo() == null || thirdPartyPoReleaseQueueV.getUnitNo().toUpperCase().indexOf(unitNoFilter.trim().toUpperCase()) < 0) {
					thirdPartyPoReleaseItr.remove();
				}
			}
		}
		if(MALUtilities.isNotEmptyString(clientNameFilter)) {
			ThirdPartyPoQueueV thirdPartyPoReleaseQueueV = null;
			for(Iterator<ThirdPartyPoQueueV> thirdPartyPoReleaseItr = filteredList.iterator(); thirdPartyPoReleaseItr.hasNext();) {
				thirdPartyPoReleaseQueueV = thirdPartyPoReleaseItr.next();
				if(thirdPartyPoReleaseQueueV.getClientAccountName() == null || thirdPartyPoReleaseQueueV.getClientAccountName().toUpperCase().indexOf(clientNameFilter.trim().toUpperCase()) < 0) {
					thirdPartyPoReleaseItr.remove();
				}
			}
		}
		
		if(MALUtilities.isNotEmptyString(vendorNameFilter)){				
			for (Iterator<ThirdPartyPoQueueV> thirdPartyPoReleaseItr = filteredList.iterator(); thirdPartyPoReleaseItr.hasNext();){
				ThirdPartyPoQueueV thirdPartyPoQueueV =  thirdPartyPoReleaseItr.next();
				boolean vendorFound = false;
				if(thirdPartyPoQueueV.getVendorInfoVOList() != null){
					for (VendorInfoVO vendorInfoVO : thirdPartyPoQueueV.getVendorInfoVOList()) {
						if(vendorInfoVO.getName() != null && vendorInfoVO.getName().toUpperCase().indexOf(vendorNameFilter.trim().toUpperCase()) >= 0){
							vendorFound = true;
							break;
						}
					  }	
					}
				
				if(! vendorFound){
					 thirdPartyPoReleaseItr.remove();
				}
				 
			}
		}
		
		if(filteredList == null || filteredList.size() == 0) {
			this.setEmptyDataTableMessage(talMessage.getMessage("no.records.found"));
		}
		
	}
	
	private boolean   isDataValid(){
		boolean  success =  true;
		if(selectedThirdPartyPoQueueV == null){
			addErrorMessage("custom.message", "Please select a unit");
			return false;
		}
		Doc doc = docDAO.findById(selectedThirdPartyPoQueueV.getDocId()).orElse(null);
		if(! doc.getDocStatus().equals("R")){
			processStageService.excludeStagedObject(selectedThirdPartyPoQueueV.getPsoId());
			addErrorMessage("custom.message", "The entry has been updated.  Please refresh the display to reflect the update.");
			success = false;
		}
		
		return success;
	}
	
	public void releasePO() {	
		String email = null;
		List<Doc> openTPDocList = null;
		boolean  success =  isDataValid();
		
		if(! success){			
			return;
		}
		
		int upfitPOCount = 0;
		
		if(selectedThirdPartyPoQueueV.getVendorInfoVOList() != null ) {			
			for (VendorInfoVO vendorInfoVO : selectedThirdPartyPoQueueV.getVendorInfoVOList() ) {
				if(Integer.parseInt(vendorInfoVO.getLeadTime()) > 0){
					upfitPOCount++;
				}
		     }
		 }

		if(upfitPOCount == 1){
			
			try{
				Long modelId = modelService.getModelIdByQmdId(selectedThirdPartyPoQueueV.getQmdId());
				if(modelId == null)
					throw new Exception("Unable to get Model Id."); 
				//HD-19 (HPS-3904) replaced getLoggedInUserName() with getLoggedInUser().getEmployeeNo()
				upfitProgressService.generateAndSaveSingleItemUpfitterProgressList(selectedThirdPartyPoQueueV.getQmdId(), selectedThirdPartyPoQueueV.getUnitNo() , getUpfitPOStatuses(), getLoggedInUser().getEmployeeNo(), selectedThirdPartyPoQueueV.getDocId(), modelId);								
				
			} catch (Exception e) {	
				success = false;
				logger.error(e ,"Failed to save logistics while releasing PO...");
				addErrorMessage("generic.error.occured.while", "saving default logistics");

			}
		
		}
		
		if(success){
			
			DbProcParamsVO parameterVO = null;
			try {			
				
				openTPDocList =  purchaseOrderService.getOpenThirdPartyPurchaseOrderWithAccessories(selectedThirdPartyPoQueueV.getDocId());
				
				parameterVO = purchaseOrderService.releaseThirdPartyPurchaseOrders(selectedThirdPartyPoQueueV.getDocId(), getLoggedInUser().getEmployeeNo());
				if(MALUtilities.isNotEmptyString(parameterVO.getMessage())){
					throw new Exception(parameterVO.getMessage());				
				}
			
				
				try {
					for (Doc doc : openTPDocList) {
						//If a TP is associated with a quote then it is not stock otherwise it is stock. This flag is irrespective of unit's stock flag.
						String stockYN = MALUtilities.isEmpty(doc.getGenericExtId()) ?  "Y" : "N" ;
						purchaseOrderService.archiveThirdPartyPurchaseOrder(doc.getDocId(), stockYN);
					}
					
				} catch (Exception e) {
					logger.error(e ,"Archiving Third Party PO Doc in Onbase...");
					addErrorMessage("generic.error.occured.while", "Archiving Third Party PO Doc in Onbase ");
				}
				
				
				for (Iterator<ThirdPartyPoQueueV> unitRowItr = masterList.iterator(); unitRowItr.hasNext();){
					ThirdPartyPoQueueV thirdPartyPoQueueV =  unitRowItr.next();
						if(thirdPartyPoQueueV.getUnitNo().equals(selectedThirdPartyPoQueueV.getUnitNo())){
							unitRowItr.remove();
							break;
					}
				}
				
				filteredList.remove(selectedThirdPartyPoQueueV);
				processStageService.excludeStagedObject(selectedThirdPartyPoQueueV.getPsoId());
				setSuccessfulReleaseMessage("Purchase order for Unit No " + selectedThirdPartyPoQueueV.getUnitNo() + " released  successfully");
				if(selectedThirdPartyPoQueueV.getVendorInfoVOList().size() > 1){
					  setSuccessfulReleaseMessage("Purchase orders for Unit No " + selectedThirdPartyPoQueueV.getUnitNo() + " released  successfully");
				}	
				
				//TODO Not sure if this is the right place at this point in the process; need to revisit 
				getDocumentListItems().clear();
				for(Doc thirdPartyPO : openTPDocList) {
					String stockYN = MALUtilities.isEmpty(thirdPartyPO.getGenericExtId()) ?  "Y" : "N" ;
					getDocumentListItems().add(new UpfitterPurchaseOrderListItemVO(thirdPartyPO.getDocId(), ReportNameEnum.THIRD_PARTY_PURHCASE_ORDER, stockYN));						
					//If applicable, submit a scheduled email request for the PO. Otherwise, the PO will be required for viewing.						
					email = documentService.getThirdPartyPOEmail(thirdPartyPO);
					if(!MALUtilities.isEmpty(email)) {
						purchasingEmailService.requestEmail(thirdPartyPO, EmailRequestEventEnum.THIRD_PARTY_PO, true, super.getLoggedInUser().getEmployeeNo());
						getDocumentListItems().get(getDocumentListItems().size() - 1).setRequired(false);
					} 

					getDocumentListItems().get(getDocumentListItems().size() - 1).setLabel(
							upfitterService.getUpfitterAccount(thirdPartyPO.getAccountCode(), CorporateEntity.fromCorpId(thirdPartyPO.getCId())).getAccountName() + " Purchase Order");
				}
				
				if(!getDocumentListItems().isEmpty()) {
					setShowReleaseDocuments(true);
				}
				setUnitNo(selectedThirdPartyPoQueueV.getUnitNo());
				clearSelection();			
			} catch (Exception e) {		
				if(MALUtilities.isNotEmptyString(parameterVO.getMessage())){
					addErrorMessage("custom.message", parameterVO.getMessage());				
				}else{
					logger.error(e ,"Release PO...");
					addErrorMessage("generic.error.occured.while", "releasing purchase order");
				
				}
			}	
			
		}
	
	}
	
	public void preProcessDownloadXLS(Object document) {

		downloadableRowsData.clear();
		downloadableColumns.clear();
		downloadableColumns.add("Unit No");
		downloadableColumns.add("Client");
		downloadableColumns.add("Lead Time");
		downloadableColumns.add("Vendor Name");
		downloadableColumns.add("Vendor Contact");
		downloadableColumns.add("Vendor Accessories");
		
		downloadableColumns.add("Order Type");
		downloadableColumns.add("Acquisition Type");

		for (ThirdPartyPoQueueV thirdPartyPoQueueV : filteredList) {
			Map<String, Object> row = new HashMap<String, Object>();
			
			Map<String, List<String>> vendorQuoteNoAccessories = new HashMap<String, List<String>>();
			List<String> vendorQuoteNoList;
			StringBuilder accessoriesValue;
			boolean unitAdded = false;
			for (VendorInfoVO vendorInfoVO : thirdPartyPoQueueV.getVendorInfoVOList()) {
				 
				ContactInfo vendorContactInfo = unitProgressService.getVendorSupplierContactInfo(vendorInfoVO.getAccountCode());
				
				Long docId = MALUtilities.isEmpty(vendorInfoVO.getTpDocId()) ? vendorInfoVO.getMainPoDocId() : vendorInfoVO.getTpDocId();
				vendorQuoteNoAccessories = purchaseOrderService.getDealerAccessoriesWithVendorQuoteNumber(thirdPartyPoQueueV.getQmdId(), docId ,vendorInfoVO.getAccountCode());
				 
				vendorQuoteNoList = new ArrayList<String>(vendorQuoteNoAccessories.keySet());
				Collections.sort(vendorQuoteNoList, new Comparator<String>() {
					@Override
					public int compare(String o1, String o2) {
						if(MALUtilities.isEmptyString(o1) || MALUtilities.isEmptyString(o2)) {
							return MALUtilities.isEmptyString(o1) ? 1 : -1;
						} else {
							return o1.compareTo(o2);
						}
					}
				});
				
				accessoriesValue = new StringBuilder("");
				for(String vendorQuoteNo : vendorQuoteNoList) {
					accessoriesValue.append("Vendor Quote : ").append(MALUtilities.isEmptyString(vendorQuoteNo) ? "Not Provided" : vendorQuoteNo).append("\n");
						
					for (String access  : vendorQuoteNoAccessories.get(vendorQuoteNo)) {
						accessoriesValue = accessoriesValue.append("   ").append(access).append("\n");
					}
				}
						
				row =  new HashMap<String, Object>();
				if (!unitAdded) {
					row.put("Unit No", thirdPartyPoQueueV.getUnitNo());
					row.put("Client", thirdPartyPoQueueV.getClientAccountName());
				}
				row.put("Lead Time", vendorInfoVO.getLeadTime());
				row.put("Vendor Name", vendorInfoVO.getName());
				row.put("Vendor Contact",vendorContactInfo == null ? "" : vendorContactInfo.getExcelFormatedData());
				row.put("Vendor Accessories", accessoriesValue);
					
				if (!unitAdded) {
					row.put("Order Type", thirdPartyPoQueueV.getOrderType());
					row.put("Acquisition Type", thirdPartyPoQueueV.getAcquisitionType());
				}
				unitAdded = true;
				downloadableRowsData.add(row);
	 		}
		}
	}
	

	public void postProcessDownloadXLS(Object document) {
		
		HSSFWorkbook wb = (HSSFWorkbook) document;
		HSSFSheet sheet = wb.getSheetAt(0);
		sheet.shiftRows(0, sheet.getLastRowNum(), 1);

		Row titleRow = sheet.createRow(0);
		titleRow.setHeightInPoints(45);
		Cell titleCell1 = titleRow.createCell(0);
		titleCell1.setCellValue("Third Party Progress");
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
	
	public void getSelectedVendorContactAccessories(ThirdPartyPoQueueV thirdPartyPoQueueV, VendorInfoVO vendorInfoVO) {
		setSelectedThirdPartyPoQueueV(thirdPartyPoQueueV);
		setShowAccessories(true);
		getSelectedVendorAccessories(vendorInfoVO);
		getSelectedVendorContact(vendorInfoVO);
	}
	
	public void getSelectedVendorAccessories(VendorInfoVO  vendorInfoVO)  {
		requestedVendorQuoteNoAccessories = new HashMap<String, List<String>>();
		
		if(!MALUtilities.isEmpty(vendorInfoVO.getTpDocId())){
			this.setRequestedVendorQuoteNoAccessories(purchaseOrderService.getDealerAccessoriesWithVendorQuoteNumber(getSelectedThirdPartyPoQueueV().getQmdId(), vendorInfoVO.getTpDocId(), vendorInfoVO.getAccountCode()));
		}else{
			this.setRequestedVendorQuoteNoAccessories(purchaseOrderService.getDealerAccessoriesWithVendorQuoteNumber(getSelectedThirdPartyPoQueueV().getQmdId(), vendorInfoVO.getMainPoDocId(), vendorInfoVO.getAccountCode()));
		}
		requestedVendorQuoteNoList = new ArrayList<String>(requestedVendorQuoteNoAccessories.keySet());
		Collections.sort(requestedVendorQuoteNoList, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				if(MALUtilities.isEmptyString(o1) || MALUtilities.isEmptyString(o2)) {
					return MALUtilities.isEmptyString(o1) ? 1 : -1;
				} else {
					return o1.compareTo(o2);
				}
			}
		});
		this.setRequestedAccessoriesVendor(vendorInfoVO.getName());
	}
	
	public void getSelectedVendorContact(VendorInfoVO  vendorInfoVO)  {
		ContactInfo contactInfo = unitProgressService.getVendorSupplierContactInfo(vendorInfoVO.getAccountCode());
		
		ExtAccAddress vendorAddress = upfitterService.getUpfitterDefaultPostAddress(vendorInfoVO.getAccountCode(), super.getLoggedInUser().getCorporateEntity().getCorpId());
		this.setSelectedVendorDealerAddressDetails(DisplayFormatHelper.formatAddressForTable(null, vendorAddress.getAddressLine1(), vendorAddress.getAddressLine2(), null, null, vendorAddress.getCityDescription(), vendorAddress.getRegionAbbreviation(), vendorAddress.getPostcode(), "\n"));
		this.setRequestedContactInfo(contactInfo);
		this.setDealerVendorNameOfRequest(vendorInfoVO.getName());
	}
	
	public List<String> getSelectedVendorQuoteNumberAccessories(String vendorQuoteNo) {
		List<String> accessDescList = new ArrayList<String>();
		if(requestedVendorQuoteNoAccessories != null && requestedVendorQuoteNoAccessories.size() > 0 ) {
			accessDescList = requestedVendorQuoteNoAccessories.get(vendorQuoteNo);
		}
		return accessDescList;
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
	
	public void navigateToPOReleaseQueue(){
		Map<String, Object> nextPageValues = new HashMap<String, Object>();
    	nextPageValues.put("searchType", String.valueOf(0));
    	super.pageList.remove(super.pageList.size()-1);
	    saveNextPageInitStateValues(nextPageValues);
		forwardToURL(ViewConstants.PURCHASE_ORDER_RELEASE_QUEUE);
	}
	
	public void upfitLogisticsListener(){
		try {
			setSelectedQuotationModel(quotationService.getQuotationModel(getSelectedThirdPartyPoQueueV().getQmdId()));
		} catch (MalBusinessException e) {
			logger.debug(e.getMessage());
			super.addErrorMessage("generic.error", e.getMessage());
		}
	}
	
	public void refreshUnitVendorList(){
		List<VendorInfoVO> vendorInfoVO;
		List<DocumentStatus> vendorListPOStatuses;
		
		try{
			vendorListPOStatuses = new ArrayList<DocumentStatus>();
			vendorListPOStatuses.add(DocumentStatus.PURCHASE_ORDER_STATUS_OPEN);
			vendorInfoVO = upfitProgressService.readonlyVendorList(getSelectedQuotationModel().getQmdId(), selectedThirdPartyPoQueueV.getUnitNo(), vendorListPOStatuses, false, true, false, selectedThirdPartyPoQueueV.getDocId());
			getSelectedThirdPartyPoQueueV().setVendorInfoVOList(vendorInfoVO);	
		} catch(Exception e) {
			logger.error(e);
			super.addErrorMessage("generic.error", e.getMessage());
		}
	}
	
	public void upfitProgressOnCloseListener(){
		Map<String, String> params;
		List<VendorInfoVO> vendorInfoVO;
		List<DocumentStatus> queueVendorListUpfitPOStatuses;
		
		try{		
			params = super.getFaceExternalContext().getRequestParameterMap();
			
			if(!MALUtilities.isEmpty(params.get("success")) && MALUtilities.convertYNToBoolean(params.get("success"))){
				queueVendorListUpfitPOStatuses = new ArrayList<DocumentStatus>();
				queueVendorListUpfitPOStatuses.add(DocumentStatus.PURCHASE_ORDER_STATUS_OPEN);
				vendorInfoVO = upfitProgressService.readonlyVendorList(getSelectedQuotationModel().getQmdId(), selectedThirdPartyPoQueueV.getUnitNo(), queueVendorListUpfitPOStatuses, false, true, false, selectedThirdPartyPoQueueV.getDocId());
				getSelectedThirdPartyPoQueueV().setVendorInfoVOList(vendorInfoVO);			
				super.addSuccessMessage("process.success", "Vendor Sequence");
			} 
			
		} catch(Exception e) {
			logger.error(e);
			super.addErrorMessage("generic.error", e.getMessage());
		}		
		
	}
	
	public void documentListOnCloseListener() {
		addSuccessMessage("custom.message", getSuccessfulReleaseMessage());
		setSuccessfulReleaseMessage(null);
	}
	
	
	protected void loadNewPage() {
		thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_THIRD_PARTY_PROGRESS_QUEUE);
		thisPage.setPageUrl(ViewConstants.THIRD_PARTY_PROGRESS_QUEUE);		
	}

	protected void restoreOldPage() {
		
	}
	
	public String cancel() {
		return super.cancelPage();
	}

	public void clearFilter() {
		this.clientNameFilter = "";
		this.unitNoFilter = "";
		this.vendorNameFilter = "";
	}
	
	public void clearSelection() {
		setSelectedThirdPartyPoQueueV(null);;
	}

	public List<ThirdPartyPoQueueV> getFilteredList() {
		return filteredList;
	}

	public void setFilteredList(List<ThirdPartyPoQueueV> filteredList) {
		this.filteredList = filteredList;
	}

	public List<ThirdPartyPoQueueV> getMasterList() {
		return masterList;
	}

	public void setMasterList(List<ThirdPartyPoQueueV> masterList) {
		this.masterList = masterList;
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

	public String getEmptyDataTableMessage() {
		return emptyDataTableMessage;
	}

	public void setEmptyDataTableMessage(String emptyDataTableMessage) {
		this.emptyDataTableMessage = emptyDataTableMessage;
	}

	public List<Map<String, Object>> getDownloadableRowsData() {
		return downloadableRowsData;
	}

	public void setDownloadableRowsData(
			List<Map<String, Object>> downloadableRowsData) {
		this.downloadableRowsData = downloadableRowsData;
	}

	public List<String> getDownloadableColumns() {
		return downloadableColumns;
	}

	public void setDownloadableColumns(List<String> downloadableColumns) {
		this.downloadableColumns = downloadableColumns;
	}

	public ThirdPartyPoQueueV getSelectedThirdPartyPoQueueV() {
		return selectedThirdPartyPoQueueV;
	}

	public void setSelectedThirdPartyPoQueueV(
			ThirdPartyPoQueueV selectedThirdPartyPoQueueV) {
		this.selectedThirdPartyPoQueueV = selectedThirdPartyPoQueueV;
	}

	public String getVendorNameFilter() {
		return vendorNameFilter;
	}

	public void setVendorNameFilter(String vendorNameFilter) {
		this.vendorNameFilter = vendorNameFilter;
	}

	public boolean isShowAccessories() {
		return showAccessories;
	}

	public void setShowAccessories(boolean showAccessories) {
		this.showAccessories = showAccessories;
	}

	public String getSelectedVendorDealerAddressDetails() {
		return selectedVendorDealerAddressDetails;
	}

	public void setSelectedVendorDealerAddressDetails(
			String selectedVendorDealerAddressDetails) {
		this.selectedVendorDealerAddressDetails = selectedVendorDealerAddressDetails;
	}

	public ContactInfo getRequestedContactInfo() {
		return requestedContactInfo;
	}

	public void setRequestedContactInfo(ContactInfo requestedContactInfo) {
		this.requestedContactInfo = requestedContactInfo;
	}

	public String getDealerVendorNameOfRequest() {
		return dealerVendorNameOfRequest;
	}

	public void setDealerVendorNameOfRequest(String dealerVendorNameOfRequest) {
		this.dealerVendorNameOfRequest = dealerVendorNameOfRequest;
	}

	public String getRequestedAccessoriesVendor() {
		return requestedAccessoriesVendor;
	}

	public void setRequestedAccessoriesVendor(String requestedAccessoriesVendor) {
		this.requestedAccessoriesVendor = requestedAccessoriesVendor;
	}

	public Map<String, List<String>> getRequestedVendorQuoteNoAccessories() {
		return requestedVendorQuoteNoAccessories;
	}

	public void setRequestedVendorQuoteNoAccessories(
			Map<String, List<String>> requestedVendorQuoteNoAccessories) {
		this.requestedVendorQuoteNoAccessories = requestedVendorQuoteNoAccessories;
	}

	public List<String> getRequestedVendorQuoteNoList() {
		return requestedVendorQuoteNoList;
	}

	public void setRequestedVendorQuoteNoList(
			List<String> requestedVendorQuoteNoList) {
		this.requestedVendorQuoteNoList = requestedVendorQuoteNoList;
	}

	public QuotationModel getSelectedQuotationModel() {
		return selectedQuotationModel;
	}

	public void setSelectedQuotationModel(QuotationModel selectedQuotationModel) {
		this.selectedQuotationModel = selectedQuotationModel;
	}

	public List<DocumentStatus> getUpfitPOStatuses() {
		return upfitPOStatuses;
	}

	public void setUpfitPOStatuses(List<DocumentStatus> upfitPOStatuses) {
		this.upfitPOStatuses = upfitPOStatuses;
	}

	public Long getScrollPosition() {
		return scrollPosition;
	}

	public void setScrollPosition(Long scrollPosition) {
		this.scrollPosition = scrollPosition;
	}

	public List<DocumentListItemVO> getDocumentListItems() {
		return documentListItems;
	}

	public void setDocumentListItems(List<DocumentListItemVO> documentListItems) {
		this.documentListItems = documentListItems;
	}

	public boolean isShowReleaseDocuments() {
		return showReleaseDocuments;
	}

	public void setShowReleaseDocuments(boolean showReleaseDocuments) {
		this.showReleaseDocuments = showReleaseDocuments;
	}

	public String getUnitNo() {
		return unitNo;
	}

	public void setUnitNo(String unitNo) {
		this.unitNo = unitNo;
	}

	public String getSuccessfulReleaseMessage() {
		return successfulReleaseMessage;
	}

	public void setSuccessfulReleaseMessage(String successfulReleaseMessage) {
		this.successfulReleaseMessage = successfulReleaseMessage;
	}
	
}
