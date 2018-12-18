package com.mikealbert.vision.view.bean;

import static com.mikealbert.vision.comparator.InServiceProgressDateComparator.REQ_BY_DATE_SORT;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.application.FacesMessage;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
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
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mikealbert.common.MalConstants;
import com.mikealbert.data.dao.CapitalElementDAO;
import com.mikealbert.data.dao.DocDAO;
import com.mikealbert.data.dao.DocPropertyValueDAO;
import com.mikealbert.data.dao.DriverAddressDAO;
import com.mikealbert.data.dao.ExternalAccountDAO;
import com.mikealbert.data.dao.FleetMasterDAO;
import com.mikealbert.data.entity.ContractLine;
import com.mikealbert.data.entity.DelayReason;
import com.mikealbert.data.entity.Doc;
import com.mikealbert.data.entity.DocPropertyValue;
import com.mikealbert.data.entity.DriverAddress;
import com.mikealbert.data.entity.ExtAccAddress;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.ExternalAccountPK;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.LatestOdometerReadingV;
import com.mikealbert.data.entity.ProcessStageObject;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.QuoteRequest;
import com.mikealbert.data.entity.SupplierProgressHistory;
import com.mikealbert.data.enumeration.DocumentStatus;
import com.mikealbert.data.enumeration.LogBookTypeEnum;
import com.mikealbert.data.enumeration.OrderToDeliveryProcessStageEnum;
import com.mikealbert.data.enumeration.TimePeriodCalendarEnum;
import com.mikealbert.data.enumeration.UnitProgressCodeEnum;
import com.mikealbert.data.enumeration.VehicleStatus;
import com.mikealbert.data.util.DisplayFormatHelper;
import com.mikealbert.data.vo.VendorInfoVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.ContractService;
import com.mikealbert.service.DelayReasonService;
import com.mikealbert.service.DocumentService;
import com.mikealbert.service.FleetMasterService;
import com.mikealbert.service.LogBookService;
import com.mikealbert.service.ProcessStageService;
import com.mikealbert.service.QuotationService;
import com.mikealbert.service.QuoteRequestService;
import com.mikealbert.service.TALTransactionService;
import com.mikealbert.service.UpfitProgressService;
import com.mikealbert.service.UpfitterService;
import com.mikealbert.service.WillowConfigService;
import com.mikealbert.service.vo.TALTransactionVO;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.service.OrderProgressService;
import com.mikealbert.vision.service.QuotingEmailService;
import com.mikealbert.vision.service.SupplierService;
import com.mikealbert.vision.service.UnitProgressService;
import com.mikealbert.vision.service.VehicleMovementService;
import com.mikealbert.vision.util.XLSUtil;
import com.mikealbert.vision.view.ViewConstants;
import com.mikealbert.vision.vo.ContactInfo;
import com.mikealbert.vision.vo.LogBookTypeVO;
import com.mikealbert.vision.vo.UnitInfo;
import com.mikealbert.vision.vo.UnitProgressSearchVO;

@Component
@Scope("view")
public class UpfitterInServiceQueueBean extends StatefulBaseBean {
	
	private static final long serialVersionUID = 8159335851834370766L;
	
	@Resource UnitProgressService unitProgressService;
	@Resource FleetMasterService fleetMasterService;
	@Resource LogBookService logBookService;
	@Resource CapitalElementDAO capitalElementDAO;
	@Resource FleetMasterDAO fleetMasterDAO;
	@Resource ContractService contractService;
	@Resource WillowConfigService willowConfigService;
	@Resource VehicleMovementService vehicleMovementService;
	@Resource UpfitterService upfitterService;
	@Resource QuotationService quotationService;
	@Resource DocumentService documentService;
	@Resource DocPropertyValueDAO docPropertyValueDAO;
	@Resource OrderProgressService orderProgressService;
	@Resource ProcessStageService processStageService;
	@Resource SupplierService supplierService;
	@Resource DocDAO docDAO;
	@Resource UpfitProgressService upfitProgressService;
	@Resource QuoteRequestService quoteRequestService;
	@Resource QuotingEmailService quotingEmailService;
	@Resource DelayReasonService delayReasonService;
	
	@Resource
	private TALTransactionService talTransactionService;
	
	@Resource
	private	DriverAddressDAO driverAddressDAO;
	
	private static final String UI_ID_IN_SERVICE_DATE = "InServiceDateCal";	
	private static final String UI_ID_IN_SERVICE_ODO = "inServiceOdoReading";
	private static final String UI_ID_CREATE_TRANPORT = "createTransport";
	private static final String UI_ID_ORDERING_CONTACT = "orderingContact";
	private static final String UI_ID_ORDERING_CONTACT_PHONE = "orderingContactPhone";
	private static final String UI_ID_OVERDUE_IMAGE = "overdueImg";	
	private static final String VENDOR_QUOTE_NO_STRING = "Vendor Quote :";
	private static final String UI_ID_VEHICLE_PICKED_UP = "vehiclePickedup";
	
	private static final String DEFAULT_SINGLE_ENTITY_WINDOW_TITLE = "Request Client Contact";
	private static final String DEFAULT_MULTIPLE_ENTITY_WINDOW_TITLE = "Multiple Selection";	
	
	private static long TAL_WILLOW_USER = 2;
	private static String TAL_REASON_CODE_PURCHASE = "PURCHASE";
	public static final String TXN_220 = "220";
	public static final String POST_INV_TRX_SOURCE = "FLPO002";
	public static final String GARAGED = "GARAGED";	
	public static final String NOPICK_DELAY_REASON = "NOPICK";

	
	private List<UnitProgressSearchVO> filteredList = new ArrayList<UnitProgressSearchVO>();
	private List<UnitProgressSearchVO> masterList;
	private String unitNameFilterValue;
	private Date invoiceProcessedDateFilterValue;
	private UnitProgressSearchVO selectedUnitProgress;
	private List<UnitProgressSearchVO> selectedUnitProgressList;
	private ContactInfo  requestedContactInfo;
	private String  dealerVendorNameOfRequest;
	private String  requestedAccessoriesVendor;
	private Map<String, List<String>> requestedVendorQuoteNoAccessories;
	private List<String> requestedVendorQuoteNoList;
	private String vendorNameFilter;
	private String delDealerNameFilter;
	private String clientNameFilter;
	private String manufacturerNameFilter;
	private String  unitNoFilter;
	private String delayReasonFilter;
	private String orderTypeFilter;
	private FleetMaster selectedFleetMaster;	
	private List<FleetMaster> selectedFleetMasterList;
	private List<LogBookTypeVO> logBookType = null;	
	private String emptyDataTableMessage;
	private List<Map<String, Object>> downloadableRowsData = new ArrayList<Map<String, Object>>();;
	private List<String> downloadableColumns = new ArrayList<String>();
	private boolean showAccessories;
	private UnitInfo requestedUnitInfo;
	private boolean showTransport;
	private boolean disableTransport; 
	private boolean openTransportFound;
	private String selectedVendorDealerAddressDetails;
	private List<SortMeta> preSort = new ArrayList<SortMeta>();
	private List<VendorInfoVO> vendorTaskList;
	private List<ProcessStageObject> selectedProcessStageObjects;	
	private String requestClientContactWindowTitle;
	
	private static final int UPFIT_SEARCH = 1;
	private static final int IN_SERV_DATE_SEARCH = 2;	
	private int searchType ;
	
	private static final String PERMISISON_MANAGE_UPFIT = "maintainUpfit";
	private static final String PERMISISON_MANAGE_INSERV = "maintainInService";
	private static final String DATA_TABLE_UI_ID = "unitProgress";
	private static final String GRD_SUPPLIER_CONFIG_NAME = "GRD_SUPPLIER";
	private static final String FOLLOW_UP_DATE_UI_ID = "cellFollupDateLbl";
	private static final String VIN_UI_ID = "vin";
	private static final String DLR_REC_DATE_UI_ID = "dlrRecDateLbl";
	private static final String CLIENT_ETA_DATE_UI_ID = "clientETADateLbl";
	private static final String INV_PROC_DATE_UI_ID = "invoiceProcessedDateLbl";
	private static final String READY_DATE_UI_ID = "readyDateLabel";
	private static final String FACT_SHIP_DATE_UI_ID = "factShipDateLbl";
	private static final String DELAY_REASON_UI_ID = "delayReasonLblId";
	
	private static final String DT_ROW_KEY_NAME = "UNIT_NO";
	
	private Long  capitalElementCDFeeId;
	private String  grdSupplierConfig = null;
	private boolean hasManagePermission = false;
	private boolean enquiryMode = false;
	private boolean disableInserviceDate = false; 
	private String unitNo;
	private List<DelayReason> delayReasonList= new ArrayList<DelayReason>();
	private DelayReason selectedDelayReason;
	
	//Upfit Progress Integration properties
	private QuotationModel selectedQuotationModel;
	private List<DocumentStatus> upfitPOStatuses;
	
	private String quoteRequestErrorMesage;
	
	private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yy");
	
	@PostConstruct
	public void init() {
		initializeDataTable(650, 850, new int[] { 8, 18, 20, 7, 7, 7, 7, 7, 7, 7 }).setHeight(10);
		openPage(); 
		setLogBookType(new ArrayList<LogBookTypeVO>());
		setGrdSupplierConfig(willowConfigService.getConfigValue(GRD_SUPPLIER_CONFIG_NAME));
		setCapitalElementCDFeeId(capitalElementDAO.findByCode("CD_FEE").getCelId());
		setSelectedProcessStageObjects(new ArrayList<ProcessStageObject>());
		
		setUpfitPOStatuses(new ArrayList<DocumentStatus>());
		getUpfitPOStatuses().add(DocumentStatus.PURCHASE_ORDER_STATUS_OPEN);
		getUpfitPOStatuses().add(DocumentStatus.PURCHASE_ORDER_STATUS_RELEASED);
		
		
		if(enquiryMode) {
			loadEnquiryData();
		}else {
			this.setEmptyDataTableMessage(talMessage.getMessage("before.search.datattable.message"));
		}
	}
	
	 private void clearFilter() {
		 
			this.vendorNameFilter = "";
			this.delDealerNameFilter = "";
			this.clientNameFilter = "";
			this.unitNoFilter = "";
			this.delayReasonFilter = "";
			this.manufacturerNameFilter = "";
			this.selectedUnitProgress = null;
			this.selectedUnitProgressList = null;
	 }
	 
	 public void loadTableWithoutData(int searchType){
		this.searchType = searchType;
		
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
		//setButtonDisabled(true);
		sortDataMultipleSort();
		
		if( searchType == UPFIT_SEARCH){
			thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_UPFIT_PROGRESS_QUEUE);
		}else{
			thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_IN_SERVICE_DATE_PROGRESS_QUEUE);
		}
		thisPage.getInputValues().put("searchType", String.valueOf(searchType));
		thisPage.setPageUrl(ViewConstants.UPFITTER_IN_SERVICE_QUEUE);
		this.setEmptyDataTableMessage(talMessage.getMessage("before.search.datattable.message"));
	}
	 
	public void performSearch(){
		
		try {
			
			((DataTable) getComponent(DATA_TABLE_UI_ID)).setFirst(0);
			
			if(masterList == null || masterList.size() == 0) {
				loadData();
			}
			
			performFilter();
			
			if(filteredList == null || filteredList.size() == 0) {
				this.setEmptyDataTableMessage(talMessage.getMessage("no.records.found"));
			}
			
		} catch (Exception e) {
			logger.error(e);
			if(e instanceof MalBusinessException) {
				super.addErrorMessage(e.getMessage());
			} else {
				super.addErrorMessage("generic.error.occured.while", " fetching units..");
			}
		}
		
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
	
	public void navigateToThirdPartyQueue(){
		Map<String, Object> nextPageValues = new HashMap<String, Object>();
    	nextPageValues.put("searchType", String.valueOf(0));
    	super.pageList.remove(super.pageList.size()-1);
	    saveNextPageInitStateValues(nextPageValues);
		forwardToURL(ViewConstants.THIRD_PARTY_PROGRESS_QUEUE);
	}
	
	private void loadEnquiryData() {
		this.searchType = IN_SERV_DATE_SEARCH;
		if(downloadableRowsData != null) downloadableRowsData.clear();
		if(downloadableColumns != null)  downloadableColumns.clear();
		if(masterList != null) masterList.clear();
		if(filteredList != null) filteredList.clear();
		if(getLogBookType() != null) getLogBookType().clear();
		
		getLogBookType().add(new LogBookTypeVO(LogBookTypeEnum.TYPE_IN_SERV_PRG_NOTES, LogBookTypeEnum.TYPE_UPFIT_PRG_NOTES, ViewConstants.LABEL_PROGRESS_CHASING, true));
		getLogBookType().get(0).getRelatedLogBookTypes().add(LogBookTypeEnum.TYPE_CLIENT_FACING_NOTES);
		getLogBookType().get(0).getRelatedLogBookTypes().add(LogBookTypeEnum.TYPE_BASE_VEH_ORDER_NOTES);
		getLogBookType().get(0).getRelatedLogBookTypes().add(LogBookTypeEnum.TYPE_VEHICLE_STATUS_NOTES);
		getLogBookType().get(0).getRelatedLogBookTypes().add(LogBookTypeEnum.TYPE_PURCHASE_ORDER_NOTES);
		getLogBookType().get(0).setRenderEntrySource(true);
		getLogBookType().add(new LogBookTypeVO(LogBookTypeEnum.TYPE_EXTERNAL_TAL_FILE_NOTES, true));				
		
		masterList = new ArrayList<UnitProgressSearchVO>();
		UnitProgressSearchVO unitProgress = unitProgressService.getPendingInServiceUnit(getUnitNo());
		if(!MALUtilities.isEmpty(unitProgress)) {
			masterList.add(unitProgress);
			filteredList = masterList;
		}
		
		if(filteredList == null || filteredList.size() == 0) {
			this.setEmptyDataTableMessage(talMessage.getMessage("no.records.found"));
		}
		
		initializeDataTable(650, 850, new int[] {12,10, 1, 4, 4, 6, 4, 8, 4, 6 });//few cols will not render
	}
	
	private void loadData() throws Exception {
			
			if(downloadableRowsData != null) downloadableRowsData.clear();
			if(downloadableColumns != null)  downloadableColumns.clear();
			if(masterList != null) masterList.clear();
			if(filteredList != null) filteredList.clear();
			if(getLogBookType() != null) getLogBookType().clear();
			
			if(searchType  == UPFIT_SEARCH){
				getLogBookType().add(
						new LogBookTypeVO(
								LogBookTypeEnum.TYPE_UPFIT_PRG_NOTES, LogBookTypeEnum.TYPE_IN_SERV_PRG_NOTES, ViewConstants.LABEL_PROGRESS_CHASING, false));
				getLogBookType().get(0).getRelatedLogBookTypes().add(LogBookTypeEnum.TYPE_CLIENT_FACING_NOTES);
				getLogBookType().get(0).getRelatedLogBookTypes().add(LogBookTypeEnum.TYPE_BASE_VEH_ORDER_NOTES);
				getLogBookType().get(0).getRelatedLogBookTypes().add(LogBookTypeEnum.TYPE_VEHICLE_STATUS_NOTES);
				getLogBookType().get(0).getRelatedLogBookTypes().add(LogBookTypeEnum.TYPE_PURCHASE_ORDER_NOTES);
				getLogBookType().get(0).setRenderEntrySource(true);
				getLogBookType().add(
						new LogBookTypeVO(
								LogBookTypeEnum.TYPE_EXTERNAL_TAL_FILE_NOTES, true));				
				masterList = unitProgressService.getUpfitDetailsList();	 			
				
				Collections.sort(masterList, unitNoComparator);
				
				hasManagePermission = hasPermission(PERMISISON_MANAGE_UPFIT);
				initializeDataTable(650, 850, new int[] { 13, 18, 20, 7, 7, 7, 7, 7, 7, 7 });
			}else if(searchType  == IN_SERV_DATE_SEARCH){
				setDelayReasonList(unitProgressService.getDelayReasons());
				getLogBookType().add(
						new LogBookTypeVO(
								LogBookTypeEnum.TYPE_IN_SERV_PRG_NOTES, LogBookTypeEnum.TYPE_UPFIT_PRG_NOTES, ViewConstants.LABEL_PROGRESS_CHASING, false));
				getLogBookType().get(0).getRelatedLogBookTypes().add(LogBookTypeEnum.TYPE_CLIENT_FACING_NOTES);
				getLogBookType().get(0).getRelatedLogBookTypes().add(LogBookTypeEnum.TYPE_BASE_VEH_ORDER_NOTES);
				getLogBookType().get(0).getRelatedLogBookTypes().add(LogBookTypeEnum.TYPE_VEHICLE_STATUS_NOTES);
				getLogBookType().get(0).getRelatedLogBookTypes().add(LogBookTypeEnum.TYPE_PURCHASE_ORDER_NOTES);
				getLogBookType().get(0).setRenderEntrySource(true);
				getLogBookType().add(
						new LogBookTypeVO(
								LogBookTypeEnum.TYPE_EXTERNAL_TAL_FILE_NOTES, true));				
				masterList = unitProgressService.getPendingInServiceDateUnitsList();			
				hasManagePermission = hasPermission(PERMISISON_MANAGE_INSERV);
				initializeDataTable(650, 850, new int[] {12,10, 1, 4, 4, 6, 4, 8, 4, 6 });//few cols will not render
				
			}
			
			for (UnitProgressSearchVO unitProgressSearchVO : masterList) {
				UnitProgressSearchVO  newUnitProgressSearchVO  = new UnitProgressSearchVO();
				BeanUtils.copyProperties(unitProgressSearchVO, newUnitProgressSearchVO);
				filteredList.add(newUnitProgressSearchVO);
			}

	}	
	
	Comparator<UnitProgressSearchVO> unitNoComparator = new Comparator<UnitProgressSearchVO>() {		
		public int compare(UnitProgressSearchVO upsVO1, UnitProgressSearchVO upsVO2) {
			return  upsVO1.getUnitNo().compareTo(upsVO2.getUnitNo());
		}
	};
	
	private void sortDataMultipleSort() {
		if(preSort != null) {
			preSort.clear();
		}
		
		SortMeta sortMeta = new SortMeta();
		UIColumn column = null;
		if(searchType == UPFIT_SEARCH) {
			column = (UIColumn) getComponent("unitProgress:unitNoCol");
	        sortMeta.setSortField("unitNo");
	        sortMeta.setSortBy(column);
			sortMeta.setSortOrder(SortOrder.ASCENDING);
			preSort.add(sortMeta);
		} else if(searchType == IN_SERV_DATE_SEARCH) {
			
			column = (UIColumn) getComponent("unitProgress:followUpDate");
			sortMeta.setSortField("followUpDate");
			sortMeta.setSortBy(column);
			sortMeta.setSortOrder(SortOrder.ASCENDING);
			preSort.add(sortMeta);
		}
	}
	
	public int reqdByCustomSort(Object o1, Object o2){
		 return REQ_BY_DATE_SORT.compare((UnitProgressSearchVO)o1, (UnitProgressSearchVO)o2);		
	}
	
	protected void loadNewPage() {
		if (thisPage.getInputValues().get("searchType") != null){
			searchType = Integer.valueOf((String) thisPage.getInputValues().get("searchType"));
			if( searchType == UPFIT_SEARCH ){
				thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_UPFIT_PROGRESS_QUEUE);
				thisPage.setPageUrl(ViewConstants.UPFITTER_IN_SERVICE_QUEUE);
			}else if( searchType == IN_SERV_DATE_SEARCH ) {
				thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_IN_SERVICE_DATE_PROGRESS_QUEUE);
				thisPage.setPageUrl(ViewConstants.UPFITTER_IN_SERVICE_QUEUE);
			}
		}
		Map<String, Object> map = super.thisPage.getInputValues();		
		String readMode = (String)map.get(ViewConstants.VIEW_MODE_READ);
		setEnquiryMode(Boolean.valueOf(readMode));
		if(enquiryMode) {
			setUnitNo((String)map.get(ViewConstants.VIEW_PARAM_UNIT_NO));
			thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_IN_SERVICE_DATE_PROGRESS_QUEUE);
			thisPage.setPageUrl(ViewConstants.UPFITTER_IN_SERVICE_QUEUE);
		}		
		
	}

	protected void restoreOldPage() {
		
	}
	
	public String cancel(){    	
    	return super.cancelPage();		
    }
	
	public List<UnitProgressSearchVO> getMasterList() {
		return masterList;
	}
	
	public void performFilter(){
		clearSelection();
		filteredList.clear();
		
		//Bug OTD-580: No need to continue when filter have not been defined
//		if(MALUtilities.isEmpty(clientNameFilter)
//				|| MALUtilities.isEmpty(delDealerNameFilter)
//				|| MALUtilities.isEmpty(vendorNameFiler)){
//			return;
//		}
		
		if(!MALUtilities.isEmpty(masterList)){
			for (UnitProgressSearchVO unitProgressSearchVO : masterList) {
				UnitProgressSearchVO  newUnitProgressSearchVO  = new UnitProgressSearchVO();
				BeanUtils.copyProperties(unitProgressSearchVO, newUnitProgressSearchVO);
				filteredList.add(newUnitProgressSearchVO);
			}
		}
		
		if(MALUtilities.isNotEmptyString(unitNoFilter)){
			for (Iterator<UnitProgressSearchVO> unitProgressSearchItr = filteredList.iterator(); unitProgressSearchItr.hasNext();){
				UnitProgressSearchVO unitProgressSearchVO =  unitProgressSearchItr.next();
				if(unitProgressSearchVO.getUnitNo()  == null || unitProgressSearchVO.getUnitNo().indexOf(unitNoFilter.trim()) < 0){ 
					unitProgressSearchItr.remove();
				}
			}
		}
			
		if(MALUtilities.isNotEmptyString(clientNameFilter)){
			for (Iterator<UnitProgressSearchVO> unitProgressSearchItr = filteredList.iterator(); unitProgressSearchItr.hasNext();){
				UnitProgressSearchVO unitProgressSearchVO =  unitProgressSearchItr.next();
				if(unitProgressSearchVO.getAccountName()  == null || unitProgressSearchVO.getAccountName().toUpperCase().indexOf(clientNameFilter.trim().toUpperCase()) < 0){
					unitProgressSearchItr.remove();
				}
			}
		}
		
		if(MALUtilities.isNotEmptyString(delDealerNameFilter)){
			for (Iterator<UnitProgressSearchVO> unitProgressSearchItr = filteredList.iterator(); unitProgressSearchItr.hasNext();){
				UnitProgressSearchVO unitProgressSearchVO =  unitProgressSearchItr.next();
					if(unitProgressSearchVO.getDeliveringDealerName() == null || unitProgressSearchVO.getDeliveringDealerName().toUpperCase().indexOf(delDealerNameFilter.trim().toUpperCase()) < 0){
						unitProgressSearchItr.remove();
				}
			}
			
		}
		
		if (!MALUtilities.isEmptyString(delayReasonFilter)) {
			for (Iterator<UnitProgressSearchVO> unitProgressSearchItr = filteredList.iterator(); unitProgressSearchItr.hasNext();){
				UnitProgressSearchVO unitProgressSearchVO =  unitProgressSearchItr.next();
				if(MALUtilities.isEmptyString(unitProgressSearchVO.getDelayReason()) || unitProgressSearchVO.getDelayReason().indexOf(delayReasonFilter) < 0){ 
					unitProgressSearchItr.remove();
				}
			}
		}
		
		if(MALUtilities.isNotEmptyString(manufacturerNameFilter)){
			for (Iterator<UnitProgressSearchVO> unitProgressSearchItr = filteredList.iterator(); unitProgressSearchItr.hasNext();){
				UnitProgressSearchVO unitProgressSearchVO =  unitProgressSearchItr.next();
				if(unitProgressSearchVO.getManufacturerName()  == null || unitProgressSearchVO.getManufacturerName().toUpperCase().indexOf(manufacturerNameFilter.trim().toUpperCase()) < 0){
					unitProgressSearchItr.remove();
				}
			}
		}
		
		if(MALUtilities.isNotEmptyString(vendorNameFilter)){				
			for (Iterator<UnitProgressSearchVO> unitProgressSearchItr = filteredList.iterator(); unitProgressSearchItr.hasNext();){
				UnitProgressSearchVO unitProgressSearchVO =  unitProgressSearchItr.next();
				boolean vendorFound = false;
				if(unitProgressSearchVO.getVendorInfoList() != null){
					for (VendorInfoVO vendorInfoVO : unitProgressSearchVO.getVendorInfoList()) {
						if(vendorInfoVO.getName() != null && vendorInfoVO.getName().toUpperCase().indexOf(vendorNameFilter.trim().toUpperCase()) >= 0){
							vendorFound = true;
							break;
						}
					  }	
					}
				
				if(! vendorFound){
					 unitProgressSearchItr.remove();
				}
				 
			}
		}
		
		if (!MALUtilities.isEmptyString(orderTypeFilter)) {
			for (Iterator<UnitProgressSearchVO> unitProgressSearchItr = filteredList.iterator(); unitProgressSearchItr.hasNext();){
				UnitProgressSearchVO unitProgressSearchVO =  unitProgressSearchItr.next();
				if(MALUtilities.isEmptyString(unitProgressSearchVO.getOrderType()) || unitProgressSearchVO.getOrderType().indexOf(orderTypeFilter) < 0){ 
					unitProgressSearchItr.remove();
				}
			}
		}
	}
	
	public void populateFollowUpDate(){
		
		String formattedFollowUpDate;
	   for (FleetMaster fleetMaster : getSelectedFleetMasterList()) {
			
		    int index = -1;
			Date followUpDate = logBookService.getFollowUpDate(fleetMaster, getLogBookType().get(0).getLogBookType());
			
			for (UnitProgressSearchVO unitProgressSearchVO : masterList) {
				if(unitProgressSearchVO.getUnitNo().equals(fleetMaster.getUnitNo())){
					unitProgressSearchVO.setFollowUpDate(followUpDate);
					break;
				}
			}

			for (UnitProgressSearchVO unitProgressSearchVO : filteredList) {
				index += 1;
				if(unitProgressSearchVO.getUnitNo().equals(fleetMaster.getUnitNo())){
					unitProgressSearchVO.setFollowUpDate(followUpDate);
					break;
				}
			}
			
			if(!MALUtilities.isEmpty(followUpDate)){				
				formattedFollowUpDate = dateFormatter.format(followUpDate);							
			} else {
				formattedFollowUpDate = "";				
			}
			
			
			RequestContext.getCurrentInstance().execute("$('[id$=\\\\:" + index + "\\\\:"+ FOLLOW_UP_DATE_UI_ID +"]').text('" + formattedFollowUpDate + "')");			
						
		}
	}
	
	public void refreshRequestClientContactListener(){
		addSuccessMessage("custom.message", "Unit is added to client facing queue");
	}
	
	
	public void preProcessDownloadXLS(Object document) {
		
		downloadableRowsData.clear();
		downloadableColumns.clear();
		//setup dynamic column below
		downloadableColumns.add("Unit No");
		downloadableColumns.add("VIN");
		downloadableColumns.add("Trim");
		downloadableColumns.add("Driver Name");
		downloadableColumns.add("Client Name");
		downloadableColumns.add("Delivering Dealer Name");
		downloadableColumns.add("Delivering Dealer Contact");
		if(searchType == UPFIT_SEARCH){
			downloadableColumns.add("Lead Time");
			downloadableColumns.add("Vendor Name");
			downloadableColumns.add("Vendor Contact");
			downloadableColumns.add("Vendor Accessories");
		}
		
		downloadableColumns.add("Required By Date");
		downloadableColumns.add("Follow Up Date");
		if(searchType == UPFIT_SEARCH){
			downloadableColumns.add("ETA Date");		
			downloadableColumns.add("Invoice Processed Date");
			downloadableColumns.add("Factory Ship Date");
			downloadableColumns.add("Int Dlr Rcvd/ Stock Accept");
		} else {
			downloadableColumns.add("Plate No");
			downloadableColumns.add("Plate Expiration Date");
			downloadableColumns.add("ETA Date");
			downloadableColumns.add("Ready at Dlr");
			downloadableColumns.add("Upfit");
			downloadableColumns.add("Invoice Processed Date");
			downloadableColumns.add("Dlr Rcvd/ Stock Accept");
		}
		
		downloadableColumns.add("Returning Unit No");
		downloadableColumns.add("Returning Unit VIN Last 8");
		downloadableColumns.add("Returning Unit Trim");
		downloadableColumns.add("Returning Unit Exterior Color");
		
		if (searchType == IN_SERV_DATE_SEARCH) {
			downloadableColumns.add("Reason for Delay");
		}
		
		//setup dynamic data below 
		for (UnitProgressSearchVO unitProgressSearchVO : filteredList) {
			
			ContactInfo dealerContactInfo = null;
			if(unitProgressSearchVO.getOrderType().equals("L")) {
				dealerContactInfo = unitProgressService.getDealerContactInfo(unitProgressSearchVO.getDeliveringDealerCode());
			}
			if(MALUtilities.isEmpty(dealerContactInfo) ) {
				dealerContactInfo = unitProgressService.getVendorSupplierContactInfo(unitProgressSearchVO.getDeliveringDealerCode());
			}
			
			UnitInfo returnedUnitInfo = null;
			if(!MALUtilities.isEmpty(unitProgressSearchVO.getReplacementFmsId())) {
				returnedUnitInfo = unitProgressService.getSelectedUnitDetails(unitProgressSearchVO.getReplacementFmsId());
			}
			
			Map<String,Object> row  = null;
			if(searchType == UPFIT_SEARCH ){
				 boolean unitAdded = false;
				 Map<String, List<String>> vendorQuoteNoAccessories = new HashMap<String, List<String>>();
				 List<String> vendorQuoteNoList;
				 StringBuilder accessoriesValue;
				 if(unitProgressSearchVO.getVendorInfoList() != null && unitProgressSearchVO.getVendorInfoList().size() > 0){
					 for (VendorInfoVO vendorInfoVO : unitProgressSearchVO.getVendorInfoList()) {
						ContactInfo vendorContactInfo = unitProgressService.getVendorSupplierContactInfo(vendorInfoVO.getAccountCode());
						 
						if(unitProgressSearchVO.isStockUnit()) {
							vendorQuoteNoAccessories = unitProgressService.getDealerAccessoriesWithVendorQuoteNumber(unitProgressSearchVO.getQmdId(), vendorInfoVO.getTpDocId(), true, vendorInfoVO.getAccountCode());
						} else {
							Long docId = MALUtilities.isEmpty(vendorInfoVO.getTpDocId()) ? vendorInfoVO.getMainPoDocId() : vendorInfoVO.getTpDocId();
							vendorQuoteNoAccessories = unitProgressService.getDealerAccessoriesWithVendorQuoteNumber(unitProgressSearchVO.getQmdId(), docId , false, null);
						} 
						 
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
							row.put("Unit No", unitProgressSearchVO.getUnitNo());
							row.put("VIN", unitProgressSearchVO.getVin());
							row.put("Trim", unitProgressSearchVO.getModelDesc());
							row.put("Driver Name", unitProgressSearchVO.getDriver());
							row.put("Client Name",unitProgressSearchVO.getAccountName());
							row.put("Delivering Dealer Name",unitProgressSearchVO.getDeliveringDealerName());
							row.put("Delivering Dealer Contact",dealerContactInfo == null ? "" : dealerContactInfo.getExcelFormatedData());
						}
						
						row.put("Lead Time", vendorInfoVO.getLeadTime());
						row.put("Vendor Name", vendorInfoVO.getName());
						row.put("Vendor Contact",vendorContactInfo == null ? "" : vendorContactInfo.getExcelFormatedData());
						row.put("Vendor Accessories", accessoriesValue);
						
						if (!unitAdded) {
							row.put("Required By Date", unitProgressSearchVO.getReqdBy() != null ? unitProgressSearchVO.getReqdBy() : "");
							row.put("Follow Up Date", MALUtilities.getNullSafeDatetoString(unitProgressSearchVO.getFollowUpDate()));
							row.put("ETA Date", MALUtilities.getNullSafeDatetoString(unitProgressSearchVO.getClientETADate()));
							row.put("Invoice Processed Date", MALUtilities.getNullSafeDatetoString(unitProgressSearchVO.getInvoiceProcessedDate()));
							row.put("Factory Ship Date", MALUtilities.getNullSafeDatetoString(unitProgressSearchVO.getFactoryShipDate()));
							row.put("Int Dlr Rcvd/ Stock Accept", MALUtilities.getNullSafeDatetoString(unitProgressSearchVO.getDealerReceivedDate()));
							
							row.put("Returning Unit No", unitProgressSearchVO.getReplacementUnitNo() != null ? unitProgressSearchVO.getReplacementUnitNo() : "");
							row.put("Returning Unit VIN Last 8", returnedUnitInfo != null ? returnedUnitInfo.getVin() : "");
							row.put("Returning Unit Trim", returnedUnitInfo != null ? returnedUnitInfo.getTrim() : "");
							row.put("Returning Unit Exterior Color", returnedUnitInfo != null ? returnedUnitInfo.getColor() : "");
						}	
						
						unitAdded = true;
						downloadableRowsData.add(row);
		 			}
				}else{
					row =  new HashMap<String, Object>();
					row.put("Unit No", unitProgressSearchVO.getUnitNo());
					row.put("VIN", unitProgressSearchVO.getVin());
					row.put("Trim", unitProgressSearchVO.getModelDesc());
					row.put("Driver Name", unitProgressSearchVO.getDriver());
					row.put("Client Name",unitProgressSearchVO.getAccountName());
					row.put("Delivering Dealer Name",unitProgressSearchVO.getDeliveringDealerName());
					row.put("Delivering Dealer Contact",dealerContactInfo == null ? "" : dealerContactInfo.getExcelFormatedData());
					row.put("Lead Time", "");
					row.put("Vendor Name", "");
					row.put("Vendor Contact","");
					row.put("Vendor Accessories", "");
					
					row.put("Required By Date", unitProgressSearchVO.getReqdBy() != null ? unitProgressSearchVO.getReqdBy() : "");
					row.put("Follow Up Date", MALUtilities.getNullSafeDatetoString(unitProgressSearchVO.getFollowUpDate()));
					row.put("ETA Date", MALUtilities.getNullSafeDatetoString(unitProgressSearchVO.getClientETADate()));
					row.put("Invoice Processed Date", MALUtilities.getNullSafeDatetoString(unitProgressSearchVO.getInvoiceProcessedDate()));
					row.put("Factory Ship Date", MALUtilities.getNullSafeDatetoString(unitProgressSearchVO.getFactoryShipDate()));
					row.put("Int Dlr Rcvd/ Stock Accept", MALUtilities.getNullSafeDatetoString(unitProgressSearchVO.getDealerReceivedDate()));
					
					row.put("Returning Unit No", unitProgressSearchVO.getReplacementUnitNo() != null ? unitProgressSearchVO.getReplacementUnitNo() : "");
					row.put("Returning Unit VIN Last 8", returnedUnitInfo != null ? returnedUnitInfo.getVin() : "");
					row.put("Returning Unit Trim", returnedUnitInfo != null ? returnedUnitInfo.getTrim() : "");
					row.put("Returning Unit Exterior Color", returnedUnitInfo != null ? returnedUnitInfo.getColor() : "");
					
					downloadableRowsData.add(row);
				}
			}else{
				 	row =  new HashMap<String, Object>();
				 	
					row.put("Unit No", unitProgressSearchVO.getUnitNo());
					row.put("VIN", unitProgressSearchVO.getVin());
					row.put("Trim", unitProgressSearchVO.getModelDesc());
					row.put("Driver Name", unitProgressSearchVO.getDriver());
					row.put("Client Name",unitProgressSearchVO.getAccountName());
					row.put("Delivering Dealer Name",unitProgressSearchVO.getDeliveringDealerName());
					row.put("Delivering Dealer Contact",dealerContactInfo == null ? "" : dealerContactInfo.getExcelFormatedData());
					
					row.put("Required By Date", unitProgressSearchVO.getReqdBy() != null ? unitProgressSearchVO.getReqdBy() : "");
					row.put("Follow Up Date", MALUtilities.getNullSafeDatetoString(unitProgressSearchVO.getFollowUpDate()));
					row.put("Plate No", unitProgressSearchVO.getPlateNo());
					row.put("Plate Expiration Date", MALUtilities.getNullSafeDatetoString(unitProgressSearchVO.getPlateExpirationDate()));
					row.put("ETA Date", MALUtilities.getNullSafeDatetoString(unitProgressSearchVO.getClientETADate()));
					row.put("Ready at Dlr", MALUtilities.getNullSafeDatetoString(unitProgressSearchVO.getVehicleReadyDate()));
					row.put("Upfit", unitProgressSearchVO.isHasUpfit() ? "Upfit" : "");
					row.put("Invoice Processed Date", MALUtilities.getNullSafeDatetoString(unitProgressSearchVO.getInvoiceProcessedDate()));
					row.put("Dlr Rcvd/ Stock Accept", MALUtilities.getNullSafeDatetoString(unitProgressSearchVO.getDealerReceivedDate()));
					
					row.put("Returning Unit No", unitProgressSearchVO.getReplacementUnitNo() != null ? unitProgressSearchVO.getReplacementUnitNo() : "");
					row.put("Returning Unit VIN Last 8", returnedUnitInfo != null ? returnedUnitInfo.getVin() : "");
					row.put("Returning Unit Trim", returnedUnitInfo != null ? returnedUnitInfo.getTrim() : "");
					row.put("Returning Unit Exterior Color", returnedUnitInfo != null ? returnedUnitInfo.getColor() : "");
					row.put("Reason for Delay", unitProgressSearchVO.getDelayReason() != null ? unitProgressSearchVO.getDelayReason() : "");
					
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
	    if(searchType  == UPFIT_SEARCH){
	    	titleCell1.setCellValue("Upfit Unit Sheet ");
	    }else{
	    	titleCell1.setCellValue("Pending In Service Date Unit Sheet ");
	    }
	    titleCell1.setCellStyle(XLSUtil.createStyle(wb, XLSUtil.TITLE));	    
	    
	    sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, downloadableColumns.size()-1));
	    
		Font boldFont = wb.createFont();
		boldFont.setBoldweight(Font.BOLDWEIGHT_BOLD);	
		
		HSSFRow row;
		HSSFCell cell;
		CellStyle cellStyle;
		for (int i = 0; i <= sheet.getLastRowNum(); i++) {
			boolean autoResize = false;
			row = sheet.getRow(i);
			if( i == 0){
				cellStyle = XLSUtil.createStyle(wb, XLSUtil.TITLE);
				autoResize = false;
			}else if ( i == 1) {
				cellStyle = XLSUtil.createStyle(wb, XLSUtil.HEADER);
				autoResize = true;				
			}else{				
				cellStyle = XLSUtil.createStyle(wb, XLSUtil.CELL);
				autoResize = false;
			}
			
			for (int j = 0; j < row.getPhysicalNumberOfCells(); j++) {
				cell = row.getCell(j);
				cell.setCellStyle(cellStyle);
				
				//Begin OTD-1254: If date data is detected, change cell to date format
				if(MALUtilities.isValidDate(cell.getStringCellValue())){
					cell.setCellStyle(XLSUtil.createStyle(wb, XLSUtil.CELL_DATE));
				} //End OTD-1254
				
				if(i > 1 && j == 10) {
					cell.setCellValue(formatStringValue(cell.getRichStringCellValue(), boldFont, 0));					
				}
				if(autoResize){				
					sheet.autoSizeColumn(j);
				}
			}
		}
	}
	
	private HSSFRichTextString formatStringValue(HSSFRichTextString richStringCellValue, Font font, int defaultIndex) {
		int startIndex = richStringCellValue.getString().indexOf(VENDOR_QUOTE_NO_STRING, defaultIndex);
		if(startIndex != -1){
			int endIndex = startIndex + VENDOR_QUOTE_NO_STRING.length();
			richStringCellValue.applyFont(startIndex, endIndex, font);
			formatStringValue(richStringCellValue, font, endIndex);
		}
		return richStringCellValue;
	}
	
	/**
	 * Initializes bean to support placing unit in service 
	 * @param unitProgressSearchVO Selected Unit to be placed in service
	 */
	public void initPlaceInService(UnitProgressSearchVO unitProgressSearchVO){
		setSelectedUnitProgress(unitProgressSearchVO);		
		populateOdoReading(unitProgressSearchVO);
		populateDealerReceivedDate(unitProgressSearchVO);
//		unitProgressSearchVO.setInServiceDateInput(null);
		
		if(unitProgressSearchVO.isStockUnit()) {
			setShowTransport(false);
			setDisableTransport(false);
		} else {
			if(unitProgressSearchVO.getReplacementFmsId() != 0) {
				boolean openTransportFound = checkOpenTransport();
				setOpenTransportFound(openTransportFound);
				if (openTransportFound || (fleetMasterDAO.getFleetStatus(unitProgressSearchVO.getReplacementFmsId())).equalsIgnoreCase(VehicleStatus.STATUS_ON_CONTRACT.getCode())) {
					setShowTransport(true);
					populateDealerContactNamePhone(unitProgressSearchVO);
					if(openTransportFound) {
						unitProgressSearchVO.setCreateTransport("N");
						setDisableTransport(true);
					} else {
						unitProgressSearchVO.setCreateTransport("");
						setDisableTransport(false);
					}
				} else {
					setShowTransport(false);
					setDisableTransport(false);
				}
			} else {
				setShowTransport(false);
				setDisableTransport(false);
			}
		}
		FleetMaster fleetMaster = fleetMasterService.findByUnitNo(unitProgressSearchVO.getUnitNo());
		if(fleetMaster != null){
			ContractLine cl = contractService.getActiveContractLine(fleetMaster,new Date());
			if(cl == null || MALUtilities.isEmpty(cl.getInServDate())){
				cl = contractService.getPendingLiveContractLine(fleetMaster,new Date());
			}
			
			if(cl != null && ! MALUtilities.isEmpty(cl.getInServDate())){
				unitProgressSearchVO.setInServiceDateInput(cl.getInServDate());
				if(MALUtilities.isEmpty(selectedUnitProgress.getVehiclePickedUp()) || !MALUtilities.convertYNToBoolean(selectedUnitProgress.getVehiclePickedUp())){
					getSelectedUnitProgress().setVehiclePickedUp("N");
				}
			}else{
				unitProgressSearchVO.setInServiceDateInput(null);
			}
		}
		if(!MALUtilities.isEmpty(unitProgressSearchVO.getInServiceDateInput())){
			disableInserviceDate = true;
		}else{
			disableInserviceDate = false;
		}
	}
	
	@Deprecated
	public void initCompleteVendorTask() {
		
		setVendorTaskList(getSelectedUnitProgress().getVendorInfoList());
		
		for (VendorInfoVO vendorInfoVO : getVendorTaskList()) {
			if(!MALUtilities.isEmpty(vendorInfoVO.getTpDocId())){
				DocPropertyValue dpv = docPropertyValueDAO.findByNameDocIdVendor("VENDOR_TASK_COMPLETED", vendorInfoVO.getTpDocId(), new ExternalAccountPK(vendorInfoVO.getVendorContext(), ExternalAccountDAO.ACCOUNT_TYPE_SUPPLIER, vendorInfoVO.getAccountCode()));
				
				if (!MALUtilities.isEmpty(dpv)) {
					if (!MALUtilities.isEmpty(dpv.getPropertyValue()) && dpv.getPropertyValue().equals("Y")) {
						vendorInfoVO.setVendorTaskCompleted(true);
					} else {
						vendorInfoVO.setVendorTaskCompleted(false);
					}
				}
		    }
		}
	}
	
	public void saveOrUpdateVendorTasks() {
		try {
			Boolean allTaskCompleted = unitProgressService.saveOrUpdateVendorTasks(getSelectedUnitProgress(), getSelectedFleetMaster(), getLoggedInUser().getEmployeeNo());
			String unitNo = getSelectedUnitProgress().getUnitNo();
			if (allTaskCompleted) {
				for (Iterator<UnitProgressSearchVO> unitProgressSearchItr = masterList.iterator(); unitProgressSearchItr.hasNext();){
					UnitProgressSearchVO unitProgressSearchVO =  unitProgressSearchItr.next();
						if(unitProgressSearchVO.getUnitNo().equals(selectedUnitProgress.getUnitNo())){
							unitProgressSearchItr.remove();
							break;
					}
				}
				filteredList.remove(selectedUnitProgress);
				clearSelection();
			}
			
			addSuccessMessage("custom.message", "Vendor task list for unit " + unitNo + " updated successfully");
			RequestContext.getCurrentInstance().update("unitProgress");
		} catch (Exception e) {
			logger.error(e , "Error while completing the Vendor Task");
			//RequestContext context = RequestContext.getCurrentInstance();
			//context.addCallbackParam("failure", true);
			handleException("custom.message", new String[]{"Error while completing the Vendor Task"}, e, null);
		}
	}
	
	public void upfitProgressOnCloseListener(){
		Map<String, String> params;	
		List<VendorInfoVO> vendorInfoVO;	
		UnitProgressSearchVO selectedUnitProgressSearchVO;
		try{		
			params = super.getFaceExternalContext().getRequestParameterMap();
			
			if(!MALUtilities.isEmpty(params.get("success")) && MALUtilities.convertYNToBoolean(params.get("success"))){				
				Long unitPODocId = docDAO.getUnitPurchaseOrderDocIdFromQmdId(selectedUnitProgress.getQmdId(), DocumentStatus.PURCHASE_ORDER_STATUS_RELEASED.getCode());//This need to be done 
				SupplierProgressHistory supplierProgressHistory = supplierService.getSupplierProgressHistoryForDocAndType(unitPODocId ,  UnitProgressCodeEnum.UFCMPLT.getCode());
				if (supplierProgressHistory != null) {
					for (Iterator<UnitProgressSearchVO> unitProgressSearchItr = masterList.iterator(); unitProgressSearchItr.hasNext();){
						UnitProgressSearchVO unitProgressSearchVO =  unitProgressSearchItr.next();
							if(unitProgressSearchVO.getUnitNo().equals(selectedUnitProgress.getUnitNo())){
								unitProgressSearchItr.remove();
								break;
						}
					}
					
					processStageService.excludeStagedObject(selectedUnitProgress.getPsoId());
					filteredList.remove(selectedUnitProgress);
					clearSelection();
				} else {
					selectedUnitProgressSearchVO = getSelectedUnitProgressList().get(0); 
					vendorInfoVO = upfitProgressService.readonlyVendorList(selectedUnitProgressSearchVO.getQmdId(), selectedUnitProgressSearchVO.getUnitNo(), getUpfitPOStatuses(), true, false, true, selectedUnitProgressSearchVO.getDocId());
					selectedUnitProgressSearchVO.setVendorInfoList(vendorInfoVO);
					// Update eta date.
					String clientETADate;
					int index = -1;
					SupplierProgressHistory etaSph = supplierService.getSupplierProgressHistoryForDocAndType(unitPODocId ,  UnitProgressCodeEnum.ETA.getCode());
					if(!MALUtilities.isEmpty(etaSph)){
						selectedUnitProgressSearchVO.setClientETADate(etaSph.getActionDate());
						RequestContext context = RequestContext.getCurrentInstance();
						index = filteredList.indexOf(selectedUnitProgressSearchVO);
						clientETADate = MALUtilities.isEmpty(etaSph.getActionDate()) ? "" : dateFormatter.format(etaSph.getActionDate());	
						context.execute("$('[id$=\\\\:" + index + "\\\\:"+ CLIENT_ETA_DATE_UI_ID +"]').text('" + clientETADate + "')");
					}
					//TODO HACK need to implement equals and hashcode to compare VO
					for(UnitProgressSearchVO  unitProgressSearchVO  : getMasterList()) {
						if(unitProgressSearchVO.getUnitNo().equals(selectedUnitProgress.getUnitNo())){
							unitProgressSearchVO.setVendorInfoList(vendorInfoVO);
							if(!MALUtilities.isEmpty(etaSph)){
								unitProgressSearchVO.setClientETADate(etaSph.getActionDate());
							}
							break;
						}						
					}
										
				}
				
				super.addSuccessMessage("process.success", "Vendor Sequence");
			} 
		} catch(Exception e) {
			logger.error(e);
			super.addErrorMessage("generic.error", e.getMessage());
		}		
		
	}
	
	private void populateDealerContactNamePhone(UnitProgressSearchVO unitProgressSearchVO) {
		ContactInfo dealerContactInfo = unitProgressService.getVendorSupplierContactInfo(unitProgressSearchVO.getDeliveringDealerCode());
		if(!MALUtilities.isEmpty(dealerContactInfo)) {
			unitProgressSearchVO.setOrderingContactName(dealerContactInfo.getName());
			if(dealerContactInfo.getPhone().equals("NO PHONE AVAILABLE")) {
				unitProgressSearchVO.setOrderingContactPhone("");
			} else {
				unitProgressSearchVO.setOrderingContactPhone(dealerContactInfo.getPhone());
			}
		} 
	}


	private void populateDealerReceivedDate(UnitProgressSearchVO unitProgressSearchVO){
	
		Date dealerReceivedDate = unitProgressService.getDealerReceivedDate(unitProgressSearchVO.getDocId(), UnitProgressCodeEnum.RECEIVD);
		getSelectedUnitProgress().setDealerReceivedDateInput(dealerReceivedDate);		
	}
	
	private void populateOdoReading(UnitProgressSearchVO unitProgressSearchVO) {
		Long odoReading = 0L; 
		if(unitProgressSearchVO != null && unitProgressSearchVO.getUnitNo() != null){
			selectedFleetMaster = fleetMasterService.findByUnitNo(unitProgressSearchVO.getUnitNo());
			LatestOdometerReadingV latestOdoReadingV = selectedFleetMaster.getLatestOdometerReading();
			if(latestOdoReadingV != null) {
				odoReading =  latestOdoReadingV.getOdoReading();
				if(MALUtilities.isEmpty(odoReading) || odoReading.intValue() == 0) {
					odoReading =  unitProgressService.getVehicleOdoReadingByQmdId(unitProgressSearchVO.getQmdId());
				}
			}
		}
		
		getSelectedUnitProgress().setOdoMeterReading(odoReading);			
	}
	
	public void updateServiceDate() {
		if(selectedUnitProgress.isStockUnit()) {
			updateServiceDateStock();
		} else {
			boolean isValid = true;
			boolean isWithinTimePeriod = false;
			
			if (selectedUnitProgress == null) {
				addErrorMessage("custom.message", "Please select a unit");
				isValid = false;
			} else {
				if (selectedUnitProgress.getInServiceDateInput() == null) {
					addErrorMessageSummary(UI_ID_IN_SERVICE_DATE, "required.field", "In Service Date");
					isValid = false;
				} else {
					isWithinTimePeriod = contractService.isWithinTimePeriod(
							getLoggedInUser().getCorporateEntity(), TimePeriodCalendarEnum.POSTING, selectedUnitProgress.getInServiceDateInput());	
					if(!isWithinTimePeriod){
						addErrorMessageSummary(UI_ID_IN_SERVICE_DATE, "custom.message", "Time period sequence not setup for In Service Date");	
						isValid = false;						
					}					
				}
				if (selectedUnitProgress.getOdoMeterReading() == null) {
					addErrorMessageSummary(UI_ID_IN_SERVICE_ODO, "required.field", "Odometer Reading");
					isValid = false;
				}			
			}
				
			
			if(getGrdSupplierConfig() != null){
				
				String grdStatus = unitProgressService.getGrdStatus(getLoggedInUser().getCorporateEntity().getCorpId(), "S", getGrdSupplierConfig() , getCapitalElementCDFeeId(),selectedUnitProgress.getDocId());
				if(grdStatus != null && grdStatus.equals("N")) {
					addErrorMessageSummary("custom.message", "The unit cannot be put into service until GRD processing is complete");
					isValid = false;
				}	
			}
			
			if(MALUtilities.isEmptyString(selectedUnitProgress.getVehiclePickedUp())) {
				addErrorMessageSummary(UI_ID_VEHICLE_PICKED_UP, "custom.message", "Please select whether vehicle is picked or not");
				isValid = false;
			}
			if(showTransport) {
				if(MALUtilities.convertYNToBoolean(selectedUnitProgress.getVehiclePickedUp()) && MALUtilities.isEmptyString(selectedUnitProgress.getCreateTransport())) {
					addErrorMessageSummary(UI_ID_CREATE_TRANPORT, "custom.message", "Please select whether to create transport or not");
					isValid = false;
				} else {
					if(MALUtilities.convertYNToBoolean(selectedUnitProgress.getVehiclePickedUp()) && "Y".equals(selectedUnitProgress.getCreateTransport())) {
						if(MALUtilities.isEmpty(selectedUnitProgress.getOrderingContactName())) {
							addErrorMessageSummary(UI_ID_ORDERING_CONTACT, "required.field", "Contact Name");
							isValid = false;
						}
						
						if(MALUtilities.isEmpty(selectedUnitProgress.getOrderingContactPhone())) {
							addErrorMessageSummary(UI_ID_ORDERING_CONTACT_PHONE, "required.field", "Contact Phone");
							isValid = false;
						}
					}
				}
			}
			
			try {	
				if (isValid) {	
					if(selectedUnitProgress.getDealerReceivedDateInput() == null) {
						selectedUnitProgress.setDealerReceivedDateInput(selectedUnitProgress.getInServiceDateInput());
					}
					
					if(MALUtilities.convertYNToBoolean(selectedUnitProgress.getVehiclePickedUp()) && "Y".equals(selectedUnitProgress.getCreateTransport())) {
						String tranTypeConfigValue =  willowConfigService.getConfigValue("INITIAL_TRANS_TYPE");
						String transPriorityConfigValue =  willowConfigService.getConfigValue("INITIAL_TRANS_PRIORITY");
						unitProgressService.putUnitInServiceWithTransport(selectedUnitProgress, getLoggedInUser(), tranTypeConfigValue, transPriorityConfigValue);
					} else {
						unitProgressService.putUnitInService(selectedUnitProgress, getLoggedInUser().getCorporateEntity().getCorpId(),
								 new Date(),"GRNODO", getLoggedInUser().getEmployeeNo());
					}
					
					if(MALUtilities.convertYNToBoolean(selectedUnitProgress.getVehiclePickedUp())){
						for (Iterator<UnitProgressSearchVO> unitProgressSearchItr = masterList.iterator(); unitProgressSearchItr.hasNext();){
							UnitProgressSearchVO unitProgressSearchVO =  unitProgressSearchItr.next();
								if(unitProgressSearchVO.getUnitNo().equals(selectedUnitProgress.getUnitNo())){
									unitProgressSearchItr.remove();
									break;
							}
						}
						
						filteredList.remove(selectedUnitProgress);
					}
					
					//OTD-6536 Completing Quote Request associated with the unit's quote.
					completeQuoteRequest();
					
					if(MALUtilities.isEmpty(quoteRequestErrorMesage)) {
						super.addSuccessMessage("saved.success", "In Service Date");					
					} else {
						super.addWarnMessage("saved.success", "In Service Date");					
		    	    	super.addWarnMessage("custom.message", quoteRequestErrorMesage);					
					} 
					
					//TODO if purch then create TAL trans of 220
					
					//if(poOrderdoc.getGenericExtId() != null && 
					if("PUR".equalsIgnoreCase(quotationService.getLeaseType(selectedUnitProgress.getQmdId()))){       
						
						QuotationModel quotationModel = quotationService.getQuotationModel(selectedUnitProgress.getQmdId());
						ExternalAccount externalAccount = quotationModel.getQuotation().getExternalAccount();
						
						String autoTagLeaseElementName = willowConfigService.getConfigValue("LETAUTOTAG");
						boolean autoTagPresent = quotationService.hasLeaseElementInQuote(quotationModel ,autoTagLeaseElementName);         
						if(autoTagPresent == true){//return if autotag service element is not present for AM 
							TALTransactionVO transactionVO = new TALTransactionVO();
							
							transactionVO.setReasonCode(TAL_REASON_CODE_PURCHASE);
							transactionVO.setUserId(TAL_WILLOW_USER);
							transactionVO.setUnitNumber(selectedUnitProgress.getUnitNo());
							transactionVO.setCheckIfExist(true);
							transactionVO.setTxnOriginPGM(POST_INV_TRX_SOURCE);
							transactionVO.setTransactionCode(TXN_220);
							
							if (externalAccount != null) {
								transactionVO.setAccountCode(externalAccount.getExternalAccountPK().getAccountCode());
								transactionVO.setAccountType(externalAccount.getExternalAccountPK().getAccountType());
								transactionVO.setContextId(externalAccount.getExternalAccountPK().getCId());
							}
							
							if(quotationModel.getQuotation().getDrvDrvId() != null){

								transactionVO.setDriverId(quotationModel.getQuotation().getDrvDrvId());	
								transactionVO.setFetchDriverAddress(true);	
								DriverAddress driverAddress = driverAddressDAO.findByDrvIdAndType(quotationModel.getQuotation().getDrvDrvId(), GARAGED);
								if(driverAddress != null && driverAddress.getDefaultInd().equals(MalConstants.FLAG_Y)){
									transactionVO.setRegionCode(driverAddress.getTownCityCode().getRegionCode().getRegionCodesPK().getRegionCode());								
									transactionVO.setCountryCode(driverAddress.getTownCityCode().getCountyCode().getCountyCodesPK().getCountryCode());
								}							
							}
							
							talTransactionService.createTransaction(transactionVO, true);
						}	
						
					}	
					if(!MALUtilities.convertYNToBoolean(selectedUnitProgress.getVehiclePickedUp())){
						List<String> unitNoList = new ArrayList<String>();
						unitNoList.add(selectedUnitProgress.getUnitNo());
						DelayReason delayReasonCode = delayReasonService.getDelayReasonByCode(NOPICK_DELAY_REASON);
						unitProgressService.updateDelayReason(unitNoList, delayReasonCode);
						setSelectedDelayReason(delayReasonCode);
						
						for (UnitProgressSearchVO unitProgressSearchVO : filteredList) {					
							if(unitProgressSearchVO.getUnitNo().equals(selectedUnitProgress.getUnitNo())){							
								unitProgressSearchVO.setDelayReason(selectedDelayReason.getDelayReasonDescription());
								RequestContext.getCurrentInstance().execute("$('[id$=\\\\:" + filteredList.indexOf(unitProgressSearchVO) + "\\\\:"+ DELAY_REASON_UI_ID +"]').text('" + selectedDelayReason.getDelayReasonDescription() + "')");
							}
						}
						
						for (UnitProgressSearchVO unitProgressSearchVO : masterList) {					
							if(unitProgressSearchVO.getUnitNo().equals(selectedUnitProgress.getUnitNo())){
								unitProgressSearchVO.setDelayReason(selectedDelayReason.getDelayReasonDescription());
								break;
							}
						}
					}else{
						RequestContext.getCurrentInstance().update("unitProgress");
					}
				}
					
					
						
			} catch (Exception e) {
				logger.error(e , "Error while putting in service date");
				getFacesContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getCause().getMessage(), ""));
				isValid = false;
			}
			
			if (!isValid) {
				RequestContext context = RequestContext.getCurrentInstance();
				context.addCallbackParam("failure", true);
			}
		}
	}
	
	public void updateServiceDateStock() {
		boolean isValid = true;
		
		if (selectedUnitProgress == null) {
			addErrorMessage("custom.message", "Please select a unit");
			isValid = false;
		} else {
			if (selectedUnitProgress.getInServiceDateInput() == null) {
				addErrorMessageSummary(UI_ID_IN_SERVICE_DATE, "required.field", "In Service Date");
				isValid = false;
			} 
			if (selectedUnitProgress.getOdoMeterReading() == null) {
				addErrorMessageSummary(UI_ID_IN_SERVICE_ODO, "required.field", "Odometer Reading");
				isValid = false;
			}
			if(MALUtilities.isEmptyString(selectedUnitProgress.getVehiclePickedUp())) {
				addErrorMessageSummary(UI_ID_VEHICLE_PICKED_UP, "custom.message", "Please select whether vehicle is picked or not");
				isValid = false;
			}
			try {
				unitProgressService.stockValidityCheck(selectedUnitProgress.getQmdId(), selectedFleetMaster.getFmsId());
			} catch (MalBusinessException e) {
				addErrorMessageSummary("custom.message", e.getMessage());
				isValid = false;
			} 
		}
		
		if (!unitProgressService.isGrnCreated(selectedFleetMaster.getFmsId())) {
			addErrorMessageSummary("custom.message", "Please do Delivery Acceptance on this unit prior to putting it into service");
			isValid = false;
		} 
		
		try {
			if(isValid) {
				unitProgressService.saveStockFinalAccept(selectedUnitProgress, selectedFleetMaster.getFmsId(), "GRNODO", getLoggedInUser().getEmployeeNo());
				
//				unitProgressService.saveStockFinalAccept(selectedUnitProgress.getQmdId(), selectedFleetMaster.getFmsId(), selectedUnitProgress.getInServiceDateInput(), 
//						selectedUnitProgress.getOdoMeterReading(), selectedUnitProgress.getInServiceDateInput(),"GRNODO", getLoggedInUser().getEmployeeNo());

				if(MALUtilities.convertYNToBoolean(selectedUnitProgress.getVehiclePickedUp())){
					for(Iterator<UnitProgressSearchVO> unitProgressSearchItr = masterList.iterator(); unitProgressSearchItr.hasNext();) {
						UnitProgressSearchVO unitProgressSearchVO = unitProgressSearchItr.next();
						if(unitProgressSearchVO.getUnitNo().equals(selectedUnitProgress.getUnitNo())) {
							unitProgressSearchItr.remove();
							break;
						}
					}
					
					filteredList.remove(selectedUnitProgress);
				}
				
				//OTD-6536 Completing Quote Request associated with the unit's quote.
				completeQuoteRequest();
				
				if(MALUtilities.isEmpty(quoteRequestErrorMesage)) {
					super.addSuccessMessage("saved.success", "In Service Date");					
				} else {
					super.addWarnMessage("saved.success", "In Service Date");					
	    	    	super.addWarnMessage("custom.message", quoteRequestErrorMesage);					
				} 
				
				if(!MALUtilities.convertYNToBoolean(selectedUnitProgress.getVehiclePickedUp())){
					List<String> unitNoList = new ArrayList<String>();
					unitNoList.add(selectedUnitProgress.getUnitNo());
					DelayReason delayReasonCode = delayReasonService.getDelayReasonByCode(NOPICK_DELAY_REASON);
					unitProgressService.updateDelayReason(unitNoList, delayReasonCode);
					setSelectedDelayReason(delayReasonCode);
					
					for (UnitProgressSearchVO unitProgressSearchVO : filteredList) {					
						if(unitProgressSearchVO.getUnitNo().equals(selectedUnitProgress.getUnitNo())){							
							unitProgressSearchVO.setDelayReason(selectedDelayReason.getDelayReasonDescription());
							RequestContext.getCurrentInstance().execute("$('[id$=\\\\:" + filteredList.indexOf(unitProgressSearchVO) + "\\\\:"+ DELAY_REASON_UI_ID +"]').text('" + selectedDelayReason.getDelayReasonDescription() + "')");
						}
					}
					
					for (UnitProgressSearchVO unitProgressSearchVO : masterList) {					
						if(unitProgressSearchVO.getUnitNo().equals(selectedUnitProgress.getUnitNo())){
							unitProgressSearchVO.setDelayReason(selectedDelayReason.getDelayReasonDescription());
							break;
						}
					}
				}else{
					RequestContext.getCurrentInstance().update("unitProgress");
				}
			}

		} catch (Exception e) {
			logger.error(e , "Error while putting in service date");
			addErrorMessageSummary("custom.message", e.getMessage());
//			getFacesContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getCause().getMessage(), ""));
			isValid = false;
		}
		
		if (!isValid) {
			RequestContext context = RequestContext.getCurrentInstance();
			context.addCallbackParam("failure", true);
		}
	}
	
	/**
	 * OTD-6536 Completing Quote Request associated with the unit's quote.
	 */
	private void completeQuoteRequest() {
		try {
			List<QuoteRequest> completedQuoteRequests = quoteRequestService.complete(selectedUnitProgress.getQmdId(), super.getLoggedInUser().getEmployeeNo());
			for(QuoteRequest completedQuoteRequest : completedQuoteRequests) {
				quotingEmailService.emailQuoteRequestStatusChange(completedQuoteRequest);
			}
		} catch(Exception e) {
			logger.error(e, "Attempting to complete and email qmdId " + getSelectedUnitProgress().getQmdId() + " associated Quote Request(s)");
			quoteRequestErrorMesage = e.getMessage();
		}	
		
		quoteRequestErrorMesage = null;		
	}
	
	/**
	 * Determines the window title of the Request Client Contact dialog.
	 * Title is based on single or multiple row item selection
	 * @return String the determined window title
	 */
	public String buildRequestClientContactWindowTitle(){
		StringBuilder titleBuilder = new StringBuilder();
		
		if(!MALUtilities.isEmpty(getSelectedUnitProgressList())){
			if(getSelectedUnitProgressList().size() > 1) {
				titleBuilder.append(DEFAULT_MULTIPLE_ENTITY_WINDOW_TITLE);
			} else {
				titleBuilder.append(DEFAULT_SINGLE_ENTITY_WINDOW_TITLE).append(" Unit No.: ").append(getSelectedUnitProgressList().get(0).getUnitNo());			
			}
		}
		
		return titleBuilder.toString();
	}
		
	private boolean checkOpenTransport() {
		return vehicleMovementService.checkOpenVehicleMovementByFmsId(selectedUnitProgress.getReplacementFmsId());
	}
	
	public void setMasterList(List<UnitProgressSearchVO> masterList) {
		this.masterList = masterList;
	}

	public List<UnitProgressSearchVO> getFilteredList() {
		return filteredList;
	}

	public void setFilteredList(List<UnitProgressSearchVO> filteredList) {
		this.filteredList = filteredList;
	}
	
	public String getUnitNameFilterValue() {
		return unitNameFilterValue;
	}

	public void setUnitNameFilterValue(String unitNameFilterValue) {
		this.unitNameFilterValue = unitNameFilterValue;
	}

	public Date getInvoiceProcessedDateFilterValue() {
		return invoiceProcessedDateFilterValue;
	}

	public void setInvoiceProcessedDateFilterValue(Date invoiceProcessedDateFilterValue) {
		this.invoiceProcessedDateFilterValue = invoiceProcessedDateFilterValue;
	}

	public UnitProgressSearchVO getSelectedUnitProgress() {
		return selectedUnitProgress;
	}

	private void setSelectedUnitProgress(UnitProgressSearchVO selectedUnitProgress) {
		this.selectedUnitProgress = selectedUnitProgress;
	}
	
	
	public List<UnitProgressSearchVO> getSelectedUnitProgressList() {
		return selectedUnitProgressList;
	}

	// We set value in selectedUnitProgress , if user selected only one record from ui. 
	public void setSelectedUnitProgressList(List<UnitProgressSearchVO> selectedUnitProgressList) {
		this.selectedUnitProgressList = selectedUnitProgressList;
		if(selectedUnitProgressList != null && selectedUnitProgressList.size() == 1){
			selectedUnitProgress = selectedUnitProgressList.get(0);
		}else{
			selectedUnitProgress = null;
		}
	}

	public void showDriverAccountForUnitNumber(UnitProgressSearchVO unitProgressSearchVO) {
		setSelectedUnitProgress(unitProgressSearchVO);
			
	}
	
	public void getSelectedVendorAccessories(VendorInfoVO  vendorInfoVO)  {
		requestedVendorQuoteNoAccessories = new HashMap<String, List<String>>();
		if(getSelectedUnitProgress().isStockUnit()) {
			this.setRequestedVendorQuoteNoAccessories(unitProgressService.getDealerAccessoriesWithVendorQuoteNumber(getSelectedUnitProgress().getQmdId(), vendorInfoVO.getTpDocId(), true, vendorInfoVO.getAccountCode()));
		} else {
			if(!MALUtilities.isEmpty(vendorInfoVO.getTpDocId())){
				this.setRequestedVendorQuoteNoAccessories(unitProgressService.getDealerAccessoriesWithVendorQuoteNumber(getSelectedUnitProgress().getQmdId(), vendorInfoVO.getTpDocId(), false, null));
			}else{
				this.setRequestedVendorQuoteNoAccessories(unitProgressService.getDealerAccessoriesWithVendorQuoteNumber(getSelectedUnitProgress().getQmdId(), vendorInfoVO.getMainPoDocId(), false, null));
			}
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
	
	
	public List<String> getSelectedVendorQuoteNumberAccessories(String vendorQuoteNo) {
		List<String> accessDescList = new ArrayList<String>();
		if(requestedVendorQuoteNoAccessories != null && requestedVendorQuoteNoAccessories.size() > 0 ) {
			accessDescList = requestedVendorQuoteNoAccessories.get(vendorQuoteNo);
		}
		return accessDescList;
	}
	
	public void getSelectedVendorContact(VendorInfoVO  vendorInfoVO)  {
		ContactInfo contactInfo = unitProgressService.getVendorSupplierContactInfo(vendorInfoVO.getAccountCode());
		
		ExtAccAddress vendorAddress = upfitterService.getUpfitterDefaultPostAddress(vendorInfoVO.getAccountCode(), super.getLoggedInUser().getCorporateEntity().getCorpId());
		this.setSelectedVendorDealerAddressDetails(DisplayFormatHelper.formatAddressForTable(null, vendorAddress.getAddressLine1(), vendorAddress.getAddressLine2(), null, null, vendorAddress.getCityDescription(), vendorAddress.getRegionAbbreviation(), vendorAddress.getPostcode(), "\n"));
		this.setRequestedContactInfo(contactInfo);
		this.setDealerVendorNameOfRequest(vendorInfoVO.getName());
	}
	
	public void getSelectedVendorContactAccessories(UnitProgressSearchVO unitProgressSearchVO, VendorInfoVO vendorInfoVO) {
		setSelectedUnitProgress(unitProgressSearchVO);
		setShowAccessories(true);
		getSelectedVendorAccessories(vendorInfoVO);
		getSelectedVendorContact(vendorInfoVO);
	}
	
	public void getSelectedDeliveringDealerContact(UnitProgressSearchVO unitProgressSearchVO)  {
		setSelectedUnitProgress(unitProgressSearchVO);
		setShowAccessories(false);
		ContactInfo contactInfo = null;
		if(unitProgressSearchVO.getOrderType().equals("L")) {
			contactInfo = unitProgressService.getDealerContactInfo(unitProgressSearchVO.getDeliveringDealerCode());
		}
		
		if(MALUtilities.isEmpty(contactInfo) ) {
			contactInfo = unitProgressService.getVendorSupplierContactInfo(unitProgressSearchVO.getDeliveringDealerCode());
		}
		Object[] record = null;
		List<Object[]> dealerAddressList = upfitterService.getUpfitterDealerPostAddress(super.getLoggedInUser().getCorporateEntity().getCorpId(), unitProgressSearchVO.getDeliveringDealerCode());
		record = null;
		if (dealerAddressList != null && dealerAddressList.size() > 0) {
			record = dealerAddressList.get(0);			
			String addressLine1 = (String) record[2];			
			String addressLine2 = (String) record[3];
			String city = (String) record[4];
			String state = (String) record[5];
			String zip =(String) record[6];			
			this.setSelectedVendorDealerAddressDetails(DisplayFormatHelper.formatAddressForTable(null, addressLine1, addressLine2, null, null, city, state, zip, "\n"));	
		}				
		this.setRequestedContactInfo(contactInfo);
		this.setDealerVendorNameOfRequest(unitProgressSearchVO.getDeliveringDealerName());
	}
	
	public void showReplacedUnitDetails(UnitProgressSearchVO unitProgressSearchVO) {
		setSelectedUnitProgress(unitProgressSearchVO);
		UnitInfo returnedUnitInfo = unitProgressService.getSelectedUnitDetails(unitProgressSearchVO.getReplacementFmsId());
		
		this.setRequestedUnitInfo(returnedUnitInfo);
	}
	
	public void clearSelection() {
		setSelectedUnitProgress(null);
		if(selectedUnitProgressList != null){
			selectedUnitProgressList.clear();
		}
	}
	
	public void onLoadDealyReason() {
		this.selectedDelayReason = null;
	}

	public void  updateDelayReason(){
		try {
			
			List<String> unitNoList = null; 
			for (UnitProgressSearchVO selectedUnitProgressSearchVO : selectedUnitProgressList) {
				if(unitNoList == null){
					unitNoList = new ArrayList<String>();
				}				
				unitNoList.add(selectedUnitProgressSearchVO.getUnitNo());
			}
			if(unitNoList != null){
				unitProgressService.updateDelayReason(unitNoList, selectedDelayReason);
				setSelectedDelayReason(fleetMasterService.getFleetMasterByUnitNoWithDelayReason(selectedUnitProgressList.get(0).getUnitNo()).getDelayReason());
				for (UnitProgressSearchVO unitProgressSearchVO : filteredList) {					
					for (UnitProgressSearchVO selectedUnitProgressSearchVO : selectedUnitProgressList) {						
						if(unitProgressSearchVO.getUnitNo().equals(selectedUnitProgressSearchVO.getUnitNo())){							
							if (!MALUtilities.isEmpty(selectedDelayReason)) {
								unitProgressSearchVO.setDelayReason(selectedDelayReason.getDelayReasonDescription());
							}else {
								unitProgressSearchVO.setDelayReason("");
							}
						}
						String updatedText = selectedDelayReason == null ?  "" :  selectedDelayReason.getDelayReasonDescription();
						RequestContext.getCurrentInstance().execute("$('[id$=\\\\:" + filteredList.indexOf(selectedUnitProgressSearchVO) + "\\\\:"+ DELAY_REASON_UI_ID +"]').text('" + updatedText + "')");
					}
				}
				
				
				for (UnitProgressSearchVO unitProgressSearchVO : masterList) {					
					for (UnitProgressSearchVO selectedUnitProgressSearchVO : selectedUnitProgressList) {						
						if(unitProgressSearchVO.getUnitNo().equals(selectedUnitProgressSearchVO.getUnitNo())){
							if (!MALUtilities.isEmpty(selectedDelayReason)) {
								unitProgressSearchVO.setDelayReason(selectedDelayReason.getDelayReasonDescription());
							}else {
								unitProgressSearchVO.setDelayReason("");
							}
						}
					}
				}
				
				
			 addSuccessMessage("saved.success", "Reason for Delay");
			}
		} catch (Exception e) {
			addErrorMessage("custom.message", "Unable to update Delay Reason for the vehicle.");
			return;
		}
		
	}
	
	public void refreshUpfitProgressRecord() {
		int index = -1;
		String clientETADate;
		String invProDate;
		String factoryShipDate;
		String vehReadyDate;
		String dlrRecDate;
		if(selectedUnitProgress != null && selectedFleetMaster != null) {
			String vinNumber = fleetMasterService.getFleetMasterByFmsId(getSelectedFleetMaster().getFmsId()).getVin();
			Long docId = selectedUnitProgress.getDocId();
			if(selectedUnitProgress.isStockUnit()) {
				try {
					Doc stockOriginalMainPO = documentService.getMainPODocOfStockUnit(selectedFleetMaster.getFmsId());
					docId = stockOriginalMainPO.getDocId();
				} catch (MalBusinessException e) {
					super.addErrorMessage("generic.error", e.getMessage());
				}
			}

			UnitProgressSearchVO updatedUnitProgres = unitProgressService.getUpdatedUnitProgressSearch(docId, searchType);
			updatedUnitProgres.setDocId(docId);    //Doc id is needed by further use of the VO
			updatedUnitProgres.setTolerance(true); //Must default the tolerance to a value. Otherwise, the UI will evaluate and render incorrectly
						
			RequestContext context = RequestContext.getCurrentInstance();
			if(searchType == 1) {
				if(!selectedUnitProgress.isStockUnit()){
					updatedUnitProgres = unitProgressService.refreshToleranceFlag(updatedUnitProgres, OrderToDeliveryProcessStageEnum.UPFIT);
				}
				
				if(updatedUnitProgres.getVehicleReadyDate() != null) {
					for(Iterator<UnitProgressSearchVO> unitProgressSearchItr = masterList.iterator(); unitProgressSearchItr.hasNext();) {
						UnitProgressSearchVO unitProgressSearchVO = unitProgressSearchItr.next();
						if(unitProgressSearchVO.getUnitNo().equals(selectedUnitProgress.getUnitNo())) {
							unitProgressSearchItr.remove();
							break;
						}
					}
					processStageService.excludeStagedObject(selectedUnitProgress.getPsoId());
					filteredList.remove(selectedUnitProgress);
					
					context.update("unitProgress");
				} else {
					for(UnitProgressSearchVO unitProgressSearchVO : masterList) {
						if(unitProgressSearchVO.getUnitNo().equals(getSelectedFleetMaster().getUnitNo())) {
							unitProgressSearchVO.setVin(vinNumber);
							unitProgressSearchVO.setClientETADate(updatedUnitProgres.getClientETADate());
							unitProgressSearchVO.setInvoiceProcessedDate(updatedUnitProgres.getInvoiceProcessedDate());
							unitProgressSearchVO.setFactoryShipDate(updatedUnitProgres.getFactoryShipDate());
							if(!selectedUnitProgress.isStockUnit()) {
								unitProgressSearchVO.setDealerReceivedDate(updatedUnitProgres.getDealerReceivedDate());
							}
							break;
						}
					}

					for(UnitProgressSearchVO unitProgressSearchVO : filteredList) {
						index +=1;
						if(unitProgressSearchVO.getUnitNo().equals(getSelectedFleetMaster().getUnitNo())) {
							unitProgressSearchVO.setVin(vinNumber);
							unitProgressSearchVO.setClientETADate(updatedUnitProgres.getClientETADate());
							unitProgressSearchVO.setInvoiceProcessedDate(updatedUnitProgres.getInvoiceProcessedDate());
							unitProgressSearchVO.setFactoryShipDate(updatedUnitProgres.getFactoryShipDate());
							unitProgressSearchVO.setTolerance(updatedUnitProgres.isTolerance());
							if(!selectedUnitProgress.isStockUnit()) {
								unitProgressSearchVO.setDealerReceivedDate(updatedUnitProgres.getDealerReceivedDate());
							}
							break;
						}
					}
					
					clientETADate = MALUtilities.isEmpty(updatedUnitProgres.getClientETADate()) ? "" : dateFormatter.format(updatedUnitProgres.getClientETADate());							
					invProDate = MALUtilities.isEmpty(updatedUnitProgres.getInvoiceProcessedDate()) ? "" : dateFormatter.format(updatedUnitProgres.getInvoiceProcessedDate());
					factoryShipDate = MALUtilities.isEmpty(updatedUnitProgres.getFactoryShipDate()) ? "" : dateFormatter.format(updatedUnitProgres.getFactoryShipDate());
					if(!selectedUnitProgress.isStockUnit()) {
						dlrRecDate = MALUtilities.isEmpty(updatedUnitProgres.getDealerReceivedDate()) ? "" : dateFormatter.format(updatedUnitProgres.getDealerReceivedDate());
						context.execute("$('[id$=\\\\:" + index + "\\\\:"+ DLR_REC_DATE_UI_ID +"]').text('" + dlrRecDate + "')");
					}
					
					context.execute("$('[id$=\\\\:" + index + "\\\\:"+ VIN_UI_ID +"]').text('" + (vinNumber !=null ? vinNumber : "") + "')");
					context.execute("$('[id$=\\\\:" + index + "\\\\:"+ CLIENT_ETA_DATE_UI_ID +"]').text('" + clientETADate + "')");
					context.execute("$('[id$=\\\\:" + index + "\\\\:"+ INV_PROC_DATE_UI_ID +"]').text('" + invProDate + "')");
					context.execute("$('[id$=\\\\:" + index + "\\\\:"+ FACT_SHIP_DATE_UI_ID +"]').text('" + factoryShipDate + "')");
					context.execute("$('[id$=\\\\:" + index + "\\\\:"+ UI_ID_OVERDUE_IMAGE +"]').css('visibility', " + (updatedUnitProgres.isTolerance() ? "'hidden'" : "'visible'") + ")");					
					
				}
			} else {
				for(UnitProgressSearchVO unitProgressSearchVO : masterList) {
					if(unitProgressSearchVO.getUnitNo().equals(getSelectedFleetMaster().getUnitNo())) {
						unitProgressSearchVO.setVin(vinNumber);
						unitProgressSearchVO.setClientETADate(updatedUnitProgres.getClientETADate());
						unitProgressSearchVO.setVehicleReadyDate(updatedUnitProgres.getVehicleReadyDate());
						unitProgressSearchVO.setInvoiceProcessedDate(updatedUnitProgres.getInvoiceProcessedDate());
						if(!selectedUnitProgress.isStockUnit()) {
							unitProgressSearchVO.setDealerReceivedDate(updatedUnitProgres.getDealerReceivedDate());
						}
						break;
					}
				}

				for(UnitProgressSearchVO unitProgressSearchVO : filteredList) {
					index +=1;
					if(unitProgressSearchVO.getUnitNo().equals(getSelectedFleetMaster().getUnitNo())) {
						unitProgressSearchVO.setVin(vinNumber);
						unitProgressSearchVO.setClientETADate(updatedUnitProgres.getClientETADate());
						unitProgressSearchVO.setVehicleReadyDate(updatedUnitProgres.getVehicleReadyDate());
						unitProgressSearchVO.setInvoiceProcessedDate(updatedUnitProgres.getInvoiceProcessedDate());
						if(!selectedUnitProgress.isStockUnit()) {
							unitProgressSearchVO.setDealerReceivedDate(updatedUnitProgres.getDealerReceivedDate());
						}
						break;
					}
				}
				
				clientETADate = MALUtilities.isEmpty(updatedUnitProgres.getClientETADate()) ? "" : dateFormatter.format(updatedUnitProgres.getClientETADate());							
				vehReadyDate = MALUtilities.isEmpty(updatedUnitProgres.getVehicleReadyDate()) ? "" : dateFormatter.format(updatedUnitProgres.getVehicleReadyDate());
				invProDate = MALUtilities.isEmpty(updatedUnitProgres.getInvoiceProcessedDate()) ? "" : dateFormatter.format(updatedUnitProgres.getInvoiceProcessedDate());
				if(!selectedUnitProgress.isStockUnit()) {
					dlrRecDate = MALUtilities.isEmpty(updatedUnitProgres.getDealerReceivedDate()) ? "" : dateFormatter.format(updatedUnitProgres.getDealerReceivedDate());
					context.execute("$('[id$=\\\\:" + index + "\\\\:"+ DLR_REC_DATE_UI_ID +"]').text('" + dlrRecDate + "')");
				}
				
				context.execute("$('[id$=\\\\:" + index + "\\\\:"+ VIN_UI_ID +"]').text('" + vinNumber + "')");
				context.execute("$('[id$=\\\\:" + index + "\\\\:"+ CLIENT_ETA_DATE_UI_ID +"]').text('" + clientETADate + "')");
				context.execute("$('[id$=\\\\:" + index + "\\\\:"+ READY_DATE_UI_ID +"]').text('" + vehReadyDate + "')");
				context.execute("$('[id$=\\\\:" + index + "\\\\:"+ INV_PROC_DATE_UI_ID +"]').text('" + invProDate + "')");
			}
		}
	}
	
	
	//set Selected VO through remote command
	public void setSelectedVORC() {
	    String unitNos = getRequestParameter(DT_ROW_KEY_NAME);
	    selectedUnitProgressList = new ArrayList<UnitProgressSearchVO>();
	    if(unitNos != null && unitNos.trim().length() > 0){
	    	for(UnitProgressSearchVO unitProgressSearchVO : filteredList) {
	    		for(String unitNo : unitNos.split(",")) {
					if(unitProgressSearchVO.getUnitNo().equals(unitNo)){					
						selectedUnitProgressList.add(unitProgressSearchVO);					
						break;
					}
	    		}
	    	}
	    	setSelectedUnitProgressList(selectedUnitProgressList);
	    }	
	}
	
	public void setSelectedProcessStageObjectVORC() {
	    String unitNos = getRequestParameter(DT_ROW_KEY_NAME);
	    setSelectedProcessStageObjects(new ArrayList<ProcessStageObject>());
	    if(unitNos != null && unitNos.trim().length() > 0){
	    	for(UnitProgressSearchVO unitProgressSearchVO : filteredList) {
	    		for(String unitNo : unitNos.split(",")) {
					if(unitProgressSearchVO.getUnitNo().equals(unitNo)){	
						selectedProcessStageObjects.add(processStageService.getStagedObject(unitProgressSearchVO.getPsoId()));
						break;
					}
	    		}
	    	}
	    	setRequestClientContactWindowTitle(buildRequestClientContactWindowTitle());
	    }	
	}	
	
	public void setSelectedFleetMastersRC(){
		setSelectedVORC();
		if(getSelectedUnitProgressList() != null && getSelectedUnitProgressList().size() > 0){
			selectedFleetMasterList = new ArrayList<FleetMaster>();
			for (UnitProgressSearchVO unitProgressSearchVO : getSelectedUnitProgressList()) {
				selectedFleetMasterList.add(fleetMasterService.findByUnitNo(unitProgressSearchVO.getUnitNo()));
			}
			
			if (selectedFleetMasterList.size() == 1) {
				setSelectedFleetMaster(getSelectedFleetMasterList().get(0));
			}
		}
	}
	
	public void setSelectedQuotationModelRC() {
		
		try {
			if(getSelectedUnitProgressList().size() == 1) {
				setSelectedQuotationModel(quotationService.getQuotationModel(getSelectedUnitProgressList().get(0).getQmdId()));
			}
		} catch(Exception e) {
			logger.error(e);
			super.addErrorMessage("generic.error", e.getMessage());			
		}
	}
	
	public void upfitLogisticsListener() {
		
	}
	
	public UnitProgressService getUnitProgressService() {
		return unitProgressService;
	}

	public void setUnitProgressService(UnitProgressService unitProgressService) {
		this.unitProgressService = unitProgressService;
	}

	public String getVendorNameFilter() {
		return vendorNameFilter;
	}

	public void setVendorNameFilter(String vendorNameFilter) {
		this.vendorNameFilter = vendorNameFilter;
	}

	public String getDelDealerNameFilter() {
		return delDealerNameFilter;
	}

	public void setDelDealerNameFilter(String delDealerNameFilter) {
		this.delDealerNameFilter = delDealerNameFilter;
	}

	public String getClientNameFilter() {
		return clientNameFilter;
	}

	public void setClientNameFilter(String clientNameFilter) {
		this.clientNameFilter = clientNameFilter;
	}


	public String getUnitNoFilter() {
		return unitNoFilter;
	}


	public void setUnitNoFilter(String unitNoFilter) {
		this.unitNoFilter = unitNoFilter;
	}


	public ContactInfo getRequestedContactInfo() {
		return requestedContactInfo;
	}

	public void setRequestedContactInfo(ContactInfo requestedContactInfo) {
		this.requestedContactInfo = requestedContactInfo;
	}

	public String getRequestedAccessoriesVendor() {
		return requestedAccessoriesVendor;
	}

	public void setRequestedAccessoriesVendor(String requestedAccessoriesVendor) {
		this.requestedAccessoriesVendor = requestedAccessoriesVendor;
	}

	public String getDealerVendorNameOfRequest() {
		return dealerVendorNameOfRequest;
	}

	public void setDealerVendorNameOfRequest(String dealerVendorNameOfRequest) {
		this.dealerVendorNameOfRequest = dealerVendorNameOfRequest;
	}

	public FleetMaster getSelectedFleetMaster() {
		return selectedFleetMaster;
	}
	public void setSelectedFleetMasterVO(UnitProgressSearchVO unitProgressSearchVO) {
		setSelectedUnitProgress(unitProgressSearchVO);
		if(unitProgressSearchVO != null && unitProgressSearchVO.getUnitNo() != null){
			this.selectedFleetMaster  = fleetMasterService.findByUnitNo(unitProgressSearchVO.getUnitNo());
		}else{
			this.selectedFleetMaster  = null;
		}
	
	}

	public List<FleetMaster> getSelectedFleetMasterList() {
		return selectedFleetMasterList;
	}

	public void setSelectedFleetMasterList(List<FleetMaster> selectedFleetMasterList) {
		this.selectedFleetMasterList = selectedFleetMasterList;
	}

	public void setSelectedFleetMaster(FleetMaster selectedFleetMaster) {
		this.selectedFleetMaster = selectedFleetMaster;
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


	public boolean isShowAccessories() {
		return showAccessories;
	}

	public void setShowAccessories(boolean showAccessories) {
		this.showAccessories = showAccessories;
	}


	public UnitInfo getRequestedUnitInfo() {
		return requestedUnitInfo;
	}


	public void setRequestedUnitInfo(UnitInfo requestedUnitInfo) {
		this.requestedUnitInfo = requestedUnitInfo;
	}


	public int getSearchType() {
		return searchType;
	}


	public void setSearchType(int searchType) {
		this.searchType = searchType;
	}

	public boolean hasManagePermission(){
		return hasManagePermission;
	}


	public String getPermissionResourceName() {
		
		if(searchType == UPFIT_SEARCH )
			return PERMISISON_MANAGE_UPFIT;
		else if(searchType ==  IN_SERV_DATE_SEARCH )
			return	PERMISISON_MANAGE_INSERV;
		else 
			return "";
		
	}


	public boolean isShowTransport() {
		return showTransport;
	}


	public void setShowTransport(boolean showTransport) {
		this.showTransport = showTransport;
	}


	public boolean isDisableTransport() {
		return disableTransport;
	}


	public void setDisableTransport(boolean disableTransport) {
		this.disableTransport = disableTransport;
	}


	public String getGrdSupplierConfig() {
		return grdSupplierConfig;
	}


	public void setGrdSupplierConfig(String grdSupplierConfig) {
		this.grdSupplierConfig = grdSupplierConfig;
	}


	public Long getCapitalElementCDFeeId() {
		return capitalElementCDFeeId;
	}


	public void setCapitalElementCDFeeId(Long capitalElementCDFeeId) {
		this.capitalElementCDFeeId = capitalElementCDFeeId;
	}

	public String getSelectedVendorDealerAddressDetails() {
		return selectedVendorDealerAddressDetails;
	}


	public void setSelectedVendorDealerAddressDetails(String selectedVendorDealerAddressDetails) {
		this.selectedVendorDealerAddressDetails = selectedVendorDealerAddressDetails;
	}

	public List<SortMeta> getPreSort() {
		return preSort;
	}

	public void setPreSort(List<SortMeta> preSort) {
		this.preSort = preSort;
	}

	public List<VendorInfoVO> getVendorTaskList() {
		return vendorTaskList;
	}

	public void setVendorTaskList(List<VendorInfoVO> vendorTaskList) {
		this.vendorTaskList = vendorTaskList;
	}

	public boolean isEnquiryMode() {
		return enquiryMode;
	}

	public void setEnquiryMode(boolean enquiryMode) {
		this.enquiryMode = enquiryMode;
	}

	public String getUnitNo() {
		return unitNo;
	}

	public void setUnitNo(String unitNo) {
		this.unitNo = unitNo;
	}

	public Map<String, List<String>> getRequestedVendorQuoteNoAccessories() {
		return requestedVendorQuoteNoAccessories;
	}

	public void setRequestedVendorQuoteNoAccessories(Map<String, List<String>> requestedVendorQuoteNoAccessories) {
		this.requestedVendorQuoteNoAccessories = requestedVendorQuoteNoAccessories;
	}

	public List<String> getRequestedVendorQuoteNoList() {
		return requestedVendorQuoteNoList;
	}

	public void setRequestedVendorQuoteNoList(List<String> requestedVendorQuoteNoList) {
		this.requestedVendorQuoteNoList = requestedVendorQuoteNoList;
	}

	public List<DelayReason> getDelayReasonList() {
		return delayReasonList;
	}

	public void setDelayReasonList(List<DelayReason> delayReasonList) {
		this.delayReasonList = delayReasonList;
	}

	public DelayReason getSelectedDelayReason() {
		return selectedDelayReason;
	}

	public void setSelectedDelayReason(DelayReason selectedDelayReason) {
		this.selectedDelayReason = selectedDelayReason;
	}

	public String getDelayReasonFilter() {
		return delayReasonFilter;
	}

	public void setDelayReasonFilter(String delayReasonFilter) {
		this.delayReasonFilter = delayReasonFilter;
	}

	public List<ProcessStageObject> getSelectedProcessStageObjects() {
		return selectedProcessStageObjects;
	}

	public void setSelectedProcessStageObjects(
			List<ProcessStageObject> selectedProcessStageObjects) {
		this.selectedProcessStageObjects = selectedProcessStageObjects;
	}

	public String getRequestClientContactWindowTitle() {
		return requestClientContactWindowTitle;
	}

	public void setRequestClientContactWindowTitle(
			String requestClientContactWindowTitle) {
		this.requestClientContactWindowTitle = requestClientContactWindowTitle;
	}
	
	public Date getEmptyDate() {
		return new GregorianCalendar(1900, 0, 1).getTime();
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

	public String getOrderTypeFilter() {
		return orderTypeFilter;
	}

	public void setOrderTypeFilter(String orderTypeFilter) {
		this.orderTypeFilter = orderTypeFilter;
	}

	public boolean isOpenTransportFound() {
		return openTransportFound;
	}

	public void setOpenTransportFound(boolean openTransportFound) {
		this.openTransportFound = openTransportFound;
	}

	public boolean isDisableInserviceDate() {
		return disableInserviceDate;
	}

	public void setDisableInserviceDate(boolean disableInserviceDate) {
		this.disableInserviceDate = disableInserviceDate;
	}

	public String getManufacturerNameFilter() {
		return manufacturerNameFilter;
	}

	public void setManufacturerNameFilter(String manufacturerNameFilter) {
		this.manufacturerNameFilter = manufacturerNameFilter;
	}
	
}
