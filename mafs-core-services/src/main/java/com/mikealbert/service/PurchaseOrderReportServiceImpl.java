package com.mikealbert.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.common.CommonCalculations;
import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.dao.DocDAO;
import com.mikealbert.data.dao.DocLinkDAO;
import com.mikealbert.data.dao.DocPropertyDAO;
import com.mikealbert.data.dao.DocPropertyValueDAO;
import com.mikealbert.data.dao.DoclDAO;
import com.mikealbert.data.dao.DriverAddressDAO;
import com.mikealbert.data.dao.DriverDAO;
import com.mikealbert.data.dao.ExternalAccountDAO;
import com.mikealbert.data.dao.FleetMasterDAO;
import com.mikealbert.data.dao.MakeCountrySuppliersDAO;
import com.mikealbert.data.dao.ModelDAO;
import com.mikealbert.data.dao.PurchaseOrderReportDAO;
import com.mikealbert.data.dao.QuotationCapitalElementDAO;
import com.mikealbert.data.dao.QuotationModelDAO;
import com.mikealbert.data.dao.SupplierDAO;
import com.mikealbert.data.dao.SupplierProgressHistoryDAO;
import com.mikealbert.data.dao.WillowConfigDAO;
import com.mikealbert.data.entity.Doc;
import com.mikealbert.data.entity.DocProperty;
import com.mikealbert.data.entity.DocPropertyValue;
import com.mikealbert.data.entity.DocSupplier;
import com.mikealbert.data.entity.Docl;
import com.mikealbert.data.entity.Driver;
import com.mikealbert.data.entity.DriverAddress;
import com.mikealbert.data.entity.ExtAccAddress;
import com.mikealbert.data.entity.ExtAccAffiliate;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.MakeCountrySuppliers;
import com.mikealbert.data.entity.Model;
import com.mikealbert.data.entity.PhoneNumber;
import com.mikealbert.data.entity.QuotationCapitalElement;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.SupplierProgressHistory;
import com.mikealbert.data.entity.WillowConfig;
import com.mikealbert.data.enumeration.AcquisitionTypeEnum;
import com.mikealbert.data.enumeration.DocPropertyEnum;
import com.mikealbert.data.util.DisplayFormatHelper;
import com.mikealbert.data.vo.AccountTaxExemptVO;
import com.mikealbert.data.vo.CourtesyDeliveryInstructionVO;
import com.mikealbert.data.vo.CustomerOrderConfirmationVO;
import com.mikealbert.data.vo.DeliveringDealerInfoVO;
import com.mikealbert.data.vo.EquipmentVO;
import com.mikealbert.data.vo.MainPoVO;
import com.mikealbert.data.vo.PurchaseOrderCoverSheetVO;
import com.mikealbert.data.vo.PurchaseOrderVO;
import com.mikealbert.data.vo.ThirdPartyPoVO;
import com.mikealbert.data.vo.VehicleInfoVO;
import com.mikealbert.data.vo.VehicleOrderSummaryVO;
import com.mikealbert.data.vo.VendorInfoVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;
import com.mikealbert.util.MALUtilities;

@Service("purchaseOrderReportService")
@Transactional
public class PurchaseOrderReportServiceImpl implements PurchaseOrderReportService {
	public MalLogger logger = MalLoggerFactory.getLogger(this.getClass());
	private final String RPT_NEW_LINE_CHAR = "<br/>";
	private final String PORT_INSTALLED_CATEGORY_CODE = "17";
	private final String POST_PRODUCTION_CATEGORY_CODE = "18";
	private final String DEALER_INSTALLED_CATEGORY_CODE = "4";
	private final String ACCESORY_TYPE_DEALER = "DEALER";
	private final String FOOTER_CONFIG_VALUE = "DISPLAY_PURCHASE_FOOTER"; //HD-514
	private final String RPT_LTD_LOG_NAME = "report_ltd_logo.png";
	private final String RPT_LEASING_LOG_NAME = "report_leasing_logo.png";
	private final String RPT_LLC_LOG_NAME = "report_am_logo.jpg";
	private final String DOC_NARRATIVE_SPEC_INSTR_TYPE = "SPEC_INSTR";
	public final String REV_NO_SEPARATOR = "/";
	public final String CONTACT_CLIENT_SUPPORT_TEXT = "Please Contact Your Client Support Specialist";
	public final String PO_EMAIL = "vehiclepurchasing@mikealbert.com";
	public final String REPORT_NAME = "MAINPO_RPT";
	public final String THD_PTY_PO_REPORT_NAME = "THDPTYPO_RPT";
	private final String WORKSHOP_CAPABILITY_ORDERING = "ORDERING";	
	private final String PO_FLORDER = "FLORDER";	
	private final String CLIENT_REQUEST_TYPE_AS = "AS";
	private final String CLIENT_REQUEST_TYPE_AS_DESC = "ASAP";
	private final String CLIENT_REQUEST_TYPE_NS = "NS";
	private final String CLIENT_REQUEST_TYPE_NS_DESC = "Not Specified";
	private final String ADDRESS_TYPE_POST = "POST";
	private final String ADDRESS_TYPE_GARAGE = "GARAGED";
	private static final String AM_INVOICE_ADJUSTMENT = "PUR_INV_ADJ";

	// private final String ACCESORY_TYPE_FACTORY ="FACTORY";

	@Resource PurchaseOrderReportDAO purchaseOrderReportDAO;
	@Resource DocDAO docDAO;
	@Resource ExternalAccountDAO externalAccountDAO;
	@Resource FleetMasterDAO fleetMasterDAO;
	@Resource DriverDAO driverDAO;
	@Resource DriverAllocationService driverAllocationService;
	@Resource WillowConfigDAO willowConfigDAO;
	@Resource DocPropertyValueDAO docPropertyValueDAO;
	@Resource MakeCountrySuppliersDAO makeCountrySuppliersDAO;
	@Resource ModelDAO modelDAO;
	@Resource DocumentService documentService;	
	@Resource DocPropertyValueService docPropertyValueService;
	@Resource DocPropertyDAO docPropertyDAO;
	@Resource SupplierProgressHistoryDAO supplierProgressHistoryDAO;
	@Resource SupplierDAO supplierDAO;
	@Resource QuotationModelDAO quotationModelDAO;
	@Resource DriverAddressDAO driverAddressDAO;
	@Resource DriverService driverService;
	@Resource DoclDAO doclDAO;
	@Resource QuotationCapitalElementDAO quotationCapitalElementDAO;
	@Resource CapitalCostService capitalCostService;
	@Resource DocLinkDAO docLinkDAO;
	@Resource WillowConfigService willowConfigService;

	@Override
	@Transactional(readOnly = true)
	public List<MainPoVO> getMainPoReportVO(Long docId, String stockYn) {

		List<MainPoVO> mainPoVOList = new ArrayList<MainPoVO>();
		mainPoVOList.add(populateMainPoHeader(docId, stockYn));
		return mainPoVOList;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ThirdPartyPoVO> getThirdPartyPoReportVO(Long docId, String stockYn) {

		List<ThirdPartyPoVO> thirdPartyPoList = new ArrayList<ThirdPartyPoVO>();
		thirdPartyPoList.add(populateThirdPartyPo(docId, stockYn));
		return thirdPartyPoList;
	}

	public List<VehicleOrderSummaryVO> getVehicleOrderSummaryReportVO(Long docId, String stockYn) {

		List<VehicleOrderSummaryVO> vehicleOrderSummaryVOList = new ArrayList<VehicleOrderSummaryVO>();
		vehicleOrderSummaryVOList.add(populateVehicleOrderSummary(docId, stockYn));
		return vehicleOrderSummaryVOList;
	}

	@Override
	@Transactional(readOnly = true)
	public List<CustomerOrderConfirmationVO> getClientConfirmationOrderReportVO(Long docId) throws MalException {

		List<CustomerOrderConfirmationVO> clientConfirmationOrderVOList = new ArrayList<CustomerOrderConfirmationVO>();
		clientConfirmationOrderVOList.add(populateClientConfirmationOrder(docId));
		return clientConfirmationOrderVOList;
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<CourtesyDeliveryInstructionVO> getCourtesyDeliveryInstructionVO(Long docId) throws MalException {
		List<CourtesyDeliveryInstructionVO> courtesyDeliveryInstructionVOList = new ArrayList<CourtesyDeliveryInstructionVO>();
		courtesyDeliveryInstructionVOList.add(populateCourtesyDeliveryInstructionVO(docId));
		return courtesyDeliveryInstructionVOList;
	}

	/**
	 * Checks prevous revision(s) of the PO and determines whether
	 * the client order confirmation has been generated.
	 * 
	 * Note: When previous rev(s) does have a delivering dealer
	 *       it is assumed that client order confirmation had been 
	 *       
	 */
	@Override
	@Transactional(readOnly = true)	
	public boolean hasClientConfirmationBeenGenerated(Long docId){
		boolean isGeneratedOrEmailed = false;
		Doc mainPO;	
		List<Doc> previousRevisionPOs;		
		
		try {
			
			isGeneratedOrEmailed = isReleasedPriorToPurchaseOrderQueue(docId);
			mainPO = documentService.getDocumentByDocId(docId);
			
			//THe properties will be here at rev 0 when PO was released via PO001
			if(!isGeneratedOrEmailed && mainPO.getRevNo() == 0) {
				if(!isGeneratedOrEmailed && !MALUtilities.isEmpty(docPropertyValueService.findByNameDocId(DocPropertyEnum.CONFIRM_DOC_EMAIL_DATE, docId))){
					isGeneratedOrEmailed = true;				
				}			
			}
			
			if(!isGeneratedOrEmailed) { 
				previousRevisionPOs = docDAO.findByDocNo(mainPO.getDocNo());
				for(Doc prevousRevisionPO : previousRevisionPOs){
					if(!MALUtilities.isEmpty(prevousRevisionPO.getSubAccCode()) && mainPO.getDocId() != prevousRevisionPO.getDocId()) {
						isGeneratedOrEmailed = true;
						break;
					}
				}							
			}
			
		} catch(Exception e) {
			logger.error(e, "doc " + docId);			
		}
		
		return isGeneratedOrEmailed;
	}
	
	public void logClientOrderConfirmationGenerated(Long docId){
		SimpleDateFormat dateFormatter = new SimpleDateFormat(MALUtilities.DATE_PATTERN_TIMESTAMP);		
		docPropertyValueService.saveOrUpdateDocumentPropertyValue(docId,DocPropertyEnum.CONFIRM_DOC_DATE, dateFormatter.format(new Date()));
	}
	
	public void logClientOrderConfirmationEmailed(Long docId){
		SimpleDateFormat dateFormatter = new SimpleDateFormat(MALUtilities.DATE_PATTERN_TIMESTAMP);		
		docPropertyValueService.saveOrUpdateDocumentPropertyValue(docId, DocPropertyEnum.CONFIRM_DOC_EMAIL_DATE, dateFormatter.format(new Date()));		
	}
	
	private MainPoVO populateMainPoHeader(Long docId, String stockYn) throws MalException {

		MainPoVO mainPoVO = new MainPoVO();
		PurchaseOrderVO purchaseOrderVO = getPurchaseOrderVO(docId, stockYn);
		
		DocPropertyValue poPurchasePrice;
		mainPoVO.setPurchaseOrderTitle("Purchase Order");
		mainPoVO.setPurchaseOrderContactUsText("For questions, etc. call 888-368-8697," + RPT_NEW_LINE_CHAR
				+ "fax 800-956-2822, or email" + RPT_NEW_LINE_CHAR + PO_EMAIL);
		if (displayFooterText(purchaseOrderVO.getLeaseType())) {
			mainPoVO.setPurchaseOrderFooterText(
					" "); //HD-505 Saket Removed like kind exchange verbage. Left this code in case we have to add a footer later.
		} else {
			mainPoVO.setPurchaseOrderFooterText("");
		}
		
		poPurchasePrice = docPropertyValueDAO.findByNameDocId(DocPropertyEnum.DEALER_PRICE.getCode(), docId);

		if (!MALUtilities.isEmpty(poPurchasePrice)) {
			purchaseOrderVO.setPoPurchasePrice(
					(CommonCalculations.getRoundedValue(new BigDecimal(poPurchasePrice.getPropertyValue()), 2)));
		}

		if (!MALUtilities.isEmpty(purchaseOrderVO.getQmdId())) {
			DocPropertyValue acquisitionTypeValue = docPropertyValueDAO.findByNameDocId(DocPropertyEnum.ACQUISITION_TYPE.getCode(), docId);
			if (!MALUtilities.isEmpty(acquisitionTypeValue)) {
				mainPoVO.setAcquisitionType(acquisitionTypeValue.getPropertyValue());
			}
			
			if (!MALUtilities.isEmpty(mainPoVO.getAcquisitionType()) && mainPoVO.getAcquisitionType().equals(AcquisitionTypeEnum.BAIL.getDescription())) {
				List<DocSupplier> docSupplierList = docDAO.findById(docId).orElse(null).getDocSuppliers();
				try {
					if (docSupplierList != null) {
						for (DocSupplier docSupplier : docSupplierList) {
							if (docSupplier.getWorkshopCapabilityCode() != null && docSupplier.getWorkshopCapabilityCode().equals(WORKSHOP_CAPABILITY_ORDERING)) {
								Model mdl = modelDAO.findById(purchaseOrderVO.getMdlId()).orElse(null);
								Driver drv = driverDAO.findById(purchaseOrderVO.getDrvId()).orElse(null);
								String driverCountry = "";
								for (DriverAddress driverAddress : drv.getDriverAddressList()) {
									if (driverAddress.getAddressType().getAddressType().equals("GARAGED") && driverAddress.getDefaultInd().equalsIgnoreCase("Y")) {
										driverCountry = driverAddress.getCountry().getCountryCode();
										MakeCountrySuppliers mcs = makeCountrySuppliersDAO.findByMakeCountrySupplier(mdl.getMake().getMakeCode(), driverCountry, docSupplier.getSupplier().getSupId());
										if (!MALUtilities.isEmpty(mcs)) {
											String bailmentText = purchaseOrderReportDAO.getReportText(purchaseOrderVO.getQmdId(), REPORT_NAME, "1");
											if (!MALUtilities.isEmpty(mcs.getBailmentDealerCode())) {
												bailmentText = bailmentText + " " + mcs.getBailmentDealerCode();
											}
											mainPoVO.setBailmentInstructions(bailmentText);
											bailmentText = purchaseOrderReportDAO.getReportText(purchaseOrderVO.getQmdId(), REPORT_NAME, "2");
											mainPoVO.setBailmentInstructions(mainPoVO.getBailmentInstructions() + "\n" + bailmentText);
										}
										break;
									}
								}
								
								break;
							}
						}
					}

				} catch (MalBusinessException e) {
					mainPoVO.setBailmentInstructions(null);
				}
			}
		}

		mainPoVO.setPurchaseOrderVO(purchaseOrderVO);
		mainPoVO.setLogo(getLogoName(purchaseOrderVO.getProductCode(), purchaseOrderVO.getDocContext()));
		mainPoVO.setLogistics(getLogistics(docId, null, stockYn));

		if (purchaseOrderVO.getDrvId() != null) {
			Driver driver = driverDAO.findById(purchaseOrderVO.getDrvId()).orElse(null);
			mainPoVO.setDriverName(driver.getDriverForename() + " " + driver.getDriverSurname());
			mainPoVO.setDriverAddress(getDriverAddress(driver));
			mainPoVO.setDriverPhone(getDriverPhone(driver));
			mainPoVO.setReturningVehicle(purchaseOrderVO.getReplacementFmsId() != null ? "Yes" : "No");
		} else {
			mainPoVO.setDriverName("");
			mainPoVO.setDriverAddress("");
			mainPoVO.setDriverPhone("");
			mainPoVO.setReturningVehicle("");
		}
		if(purchaseOrderVO.getLeaseType().equalsIgnoreCase("PUR")){
			List<Long> docIdList = new ArrayList<Long>();
			docIdList.add(docId);
		
			// get third party PO's
			List<Long> thirdDocIdList = docLinkDAO.findThirdPartyDocsIdByParentDocId(docId);	
			if(thirdDocIdList != null && thirdDocIdList.size() > 0){
				docIdList.addAll(thirdDocIdList);
			}
			
			BigDecimal totalDealerAccCost = BigDecimal.ZERO;
			for(Long doc : docIdList){
				List<Docl> list = doclDAO.findDoclByDocIdAndUserDef4(doc, "DEALER");
				if(list != null && list.size() > 0){
					for(Docl docl : list){
						totalDealerAccCost = totalDealerAccCost.add(docl.getUnitCost());
					}
				}
			}
			String accCost = new java.text.DecimalFormat("$#,##0.00").format(totalDealerAccCost);
			String purchaseAdjustment = "";
			QuotationCapitalElement quotationCapitalElement = quotationCapitalElementDAO.findByQmdIDAndCapitalElementCode(purchaseOrderVO.getQmdId(), AM_INVOICE_ADJUSTMENT);
			if(!MALUtilities.isEmpty(quotationCapitalElement)){
				purchaseAdjustment = new java.text.DecimalFormat("$#,##0.00").format(quotationCapitalElement.getValue());
			}
			
			if(!MALUtilities.isEmpty(purchaseOrderVO.getQmdId())){
				QuotationModel qmd = quotationModelDAO.findById(purchaseOrderVO.getQmdId()).orElse(null);
				if(!MALUtilities.isEmpty(qmd)){
					String capitalCostCalcType = capitalCostService.findQuoteCapitalCostsCalcType(qmd);
					
					if(!MALUtilities.isEmpty(capitalCostCalcType) && capitalCostCalcType.equalsIgnoreCase("CapitalCosts")){
						mainPoVO.setTotalTaxableText("Total Taxable Value = Mike Albert's Total Cost + Dealer Accessories ("+accCost + ") + Purchase Adjustment (" + purchaseAdjustment + ")");
					}else{
						mainPoVO.setTotalTaxableText("Total Taxable Value = Base Invoice + Factory Options + Freight + Dealer Accessories ("+accCost + ") + Purchase Adjustment (" + purchaseAdjustment + ")");
					}
				}
			}
		}

		VehicleInfoVO vehicleInfoVO = populateVehicleInfoVO(purchaseOrderVO);
		mainPoVO.setVehicleInfoVO(vehicleInfoVO);
		DeliveringDealerInfoVO deliveringDealerInfoVO = populateDeliveringDealerInfoVO(purchaseOrderVO);
		mainPoVO.setDeliveringDealerInfoVO(deliveringDealerInfoVO);
		List<VendorInfoVO> vendorUpfitList = getVendorUpfitList(purchaseOrderVO);
		mainPoVO.setVendorInfoVOList(vendorUpfitList);

		return mainPoVO;
	}

	private ThirdPartyPoVO populateThirdPartyPo(Long thpDocId, String stockYn) {

		ThirdPartyPoVO thirdPartyPoVO = new ThirdPartyPoVO();
		PurchaseOrderVO purchaseOrderVO = getThirdPartyPurchaseOrderVO(thpDocId, stockYn);
		
		thirdPartyPoVO.setPurchaseOrderTitle("Third Party Purchase Order");
		thirdPartyPoVO.setPurchaseOrderContactUsText("For questions, etc. call 888-368-8697," + RPT_NEW_LINE_CHAR
				+ "fax 800-956-2822, or email" + RPT_NEW_LINE_CHAR + PO_EMAIL);
		if (displayFooterText(purchaseOrderVO.getLeaseType())) {
			thirdPartyPoVO.setPurchaseOrderFooterText(
					"This Third Party Purchase Order (this \"Purchase Order\") is subject in all respects to the Purchase Order Terms and Conditions available at " + 
			        "https://www.mikealbert.com/terms-conditions/ (the \"Terms\"), which are hereby incorporated by reference and made a part hereof. " + 
				    "Vendor acknowledges that they have accessed online or otherwise received a copy of the Terms, has reviewed the Terms, and consents and agrees to the Terms. " + 
			        "The Terms published at the above URL as of the date of this Purchase Order shall apply to Vendor, regardless of any subsequently published updates thereto. "+ 
				    "Vendor acknowledges that it is the obligation of Vendor to maintain a copy of the Terms effective as of the date of this Purchase Order."); //HD-505 Saket Replaced like kind exchange verbage
		} else {
			thirdPartyPoVO.setPurchaseOrderFooterText("");
		}

		thirdPartyPoVO.setLogo(getLogoName(purchaseOrderVO.getProductCode(), purchaseOrderVO.getDocContext()));
		thirdPartyPoVO.setPurchaseOrderVO(purchaseOrderVO);
		thirdPartyPoVO.setLogistics(getLogistics(purchaseOrderVO.getMainPoId(), thpDocId, stockYn));

		if (purchaseOrderVO.getDrvId() != null) {
			Driver driver = driverDAO.findById(purchaseOrderVO.getDrvId()).orElse(null);
			thirdPartyPoVO.setDriverName(driver.getDriverForename() + " " + driver.getDriverSurname());
			thirdPartyPoVO.setDriverAddress(getDriverAddress(driver));
			thirdPartyPoVO.setDriverPhone(getDriverPhone(driver));
			thirdPartyPoVO.setReturningVehicle(purchaseOrderVO.getReplacementFmsId() != null ? "Yes" : "No");
		} else {
			thirdPartyPoVO.setDriverName("");
			thirdPartyPoVO.setDriverAddress("");
			thirdPartyPoVO.setDriverPhone("");
			thirdPartyPoVO.setReturningVehicle("");
		}
		
		VehicleInfoVO vehicleInfoVO = populateVehicleInfoVO(purchaseOrderVO);
		thirdPartyPoVO.setVehicleInfoVO(vehicleInfoVO);
		DeliveringDealerInfoVO deliveringDealerInfoVO = populateDeliveringDealerInfoVO(purchaseOrderVO);
		thirdPartyPoVO.setDeliveringDealerInfoVO(deliveringDealerInfoVO);
		List<VendorInfoVO> vendorUpfitList = getVendorUpfitList(purchaseOrderVO);
		thirdPartyPoVO.setVendorInfoVOList(vendorUpfitList);

		Doc thdPtyDoc = docDAO.findById(thpDocId).orElse(null);
		if(!MALUtilities.isEmpty(purchaseOrderVO.getQmdId()) && !MALUtilities.isEmpty(thdPtyDoc)){
			String bailmentIndicator = purchaseOrderReportDAO.getBailmentIndicatorByQmdIdAndAccountInfo(purchaseOrderVO.getQmdId(), 
																										thdPtyDoc.getAccountCode(),
																										thdPtyDoc.getAccountType(),
																										thdPtyDoc.getEaCId());
			if(MALUtilities.convertYNToBoolean(bailmentIndicator) && !MALUtilities.isEmpty(purchaseOrderVO.getMainPoId())){
				Doc mainDoc = docDAO.findById(purchaseOrderVO.getMainPoId()).orElse(null);
				DocSupplier docSupplierOrdering = mainDoc.getDocSupplier(WORKSHOP_CAPABILITY_ORDERING);
				
				if(!MALUtilities.isEmpty(docSupplierOrdering) && !MALUtilities.isEmpty(docSupplierOrdering.getSupplier())){
					Model mdl = modelDAO.findById(purchaseOrderVO.getMdlId()).orElse(null);
					Driver drv = driverDAO.findById(purchaseOrderVO.getDrvId()).orElse(null);
					String driverCountry = "";
					try{
						for (DriverAddress driverAddress : drv.getDriverAddressList()) {
							if (driverAddress.getAddressType().getAddressType().equals("GARAGED") && driverAddress.getDefaultInd().equalsIgnoreCase("Y")) {
								driverCountry = driverAddress.getCountry().getCountryCode();
								MakeCountrySuppliers mcs = makeCountrySuppliersDAO.findByMakeCountrySupplier(mdl.getMake().getMakeCode(), driverCountry, docSupplierOrdering.getSupplier().getSupId());
								if (!MALUtilities.isEmpty(mcs)) {
									String bailmentText = purchaseOrderReportDAO.getReportText(purchaseOrderVO.getQmdId(), THD_PTY_PO_REPORT_NAME, "1");
									if (!MALUtilities.isEmpty(mcs.getBailmentDealerCode())) {
										bailmentText = bailmentText + " " + mcs.getBailmentDealerCode();
									}
									thirdPartyPoVO.setBailmentInstructions(bailmentText);
									bailmentText = purchaseOrderReportDAO.getReportText(purchaseOrderVO.getQmdId(), THD_PTY_PO_REPORT_NAME, "2");
									thirdPartyPoVO.setBailmentInstructions(thirdPartyPoVO.getBailmentInstructions() + "\n" + bailmentText);
								}
								break;
							}
						}
					} catch (MalBusinessException e) {
						thirdPartyPoVO.setBailmentInstructions(null);
					}
				}
			}
		}
		return thirdPartyPoVO;
	}

	private VehicleOrderSummaryVO populateVehicleOrderSummary(Long docId, String stockYn) {

		VehicleOrderSummaryVO vehicleOrderSummaryVO = new VehicleOrderSummaryVO();
		PurchaseOrderVO purchaseOrderVO = getPurchaseOrderVO(docId, stockYn);
		
		vehicleOrderSummaryVO.setTitle("Vehicle Order Summary");
		vehicleOrderSummaryVO.setContactUsText("For questions, etc. call 888-368-8697," + RPT_NEW_LINE_CHAR
				+ "fax 800-956-2822, or email" + RPT_NEW_LINE_CHAR + PO_EMAIL);
		vehicleOrderSummaryVO.setVehicleOrderSummaryHeaderText(
				"The vehicle described below has been ordered and will be drop shipped to your dealership for courtesy delivery.   "
						+ "An instruction packet will be sent closer to the time of arrival.");
		
		if (displayFooterText(purchaseOrderVO.getLeaseType())) {
				vehicleOrderSummaryVO.setFooterText(
						" ");//HD-505 Saket Removed like kind exchange verbage. Left this code in case we have to add a footer later.
		} else {
			vehicleOrderSummaryVO.setFooterText("");
		}
		
		vehicleOrderSummaryVO.setETA(purchaseOrderVO.getDesiredArrivalDateToDealerOrVendor());
		vehicleOrderSummaryVO.setLogo(getLogoName(purchaseOrderVO.getProductCode(), purchaseOrderVO.getDocContext()));
		vehicleOrderSummaryVO.setPurchaseOrderVO(purchaseOrderVO);
		vehicleOrderSummaryVO.setLogistics(getLogistics(docId, null, stockYn));
		
		if (purchaseOrderVO.getDrvId() != null) {
			Driver driver = driverDAO.findById(purchaseOrderVO.getDrvId()).orElse(null);
			vehicleOrderSummaryVO.setDriverName(driver.getDriverForename() + " " + driver.getDriverSurname());
			vehicleOrderSummaryVO.setDriverAddress(getDriverAddress(driver));
			vehicleOrderSummaryVO.setDriverPhone(getDriverPhone(driver));

			vehicleOrderSummaryVO.setReturningVehicle(purchaseOrderVO.getReplacementFmsId() != null ? "Yes" : "No");
		} else {
			vehicleOrderSummaryVO.setDriverName("");
			vehicleOrderSummaryVO.setDriverAddress("");
			vehicleOrderSummaryVO.setDriverPhone("");
			vehicleOrderSummaryVO.setReturningVehicle("");
		}

		VehicleInfoVO vehicleInfoVO = populateVehicleInfoVO(purchaseOrderVO);
		vehicleOrderSummaryVO.setVehicleInfoVO(vehicleInfoVO);
		DeliveringDealerInfoVO deliveringDealerInfoVO = populateDeliveringDealerInfoVO(purchaseOrderVO);
		vehicleOrderSummaryVO.setDeliveringDealerInfoVO(deliveringDealerInfoVO);
		List<VendorInfoVO> vendorUpfitList = getVendorUpfitList(purchaseOrderVO);
		vehicleOrderSummaryVO.setVendorInfoVOList(vendorUpfitList);

		return vehicleOrderSummaryVO;

	}

	private CustomerOrderConfirmationVO populateClientConfirmationOrder(Long docId) {

		CustomerOrderConfirmationVO view = new CustomerOrderConfirmationVO();

		PurchaseOrderVO purchaseOrderVO = getPurchaseOrderVO(docId, "N");
		if(!MALUtilities.isEmptyString(purchaseOrderVO.getLeaseType()) && purchaseOrderVO.getLeaseType().equalsIgnoreCase("PUR")){
			view.setHeaderText("Thank you for your recent order with Mike Albert, LLC.  We are happy to confirm your order has been placed.");
		}else{
			view.setHeaderText("Thank you for your recent order with Mike Albert Fleet Solutions.  We are happy to confirm your order has been placed.");
		}
		
		view.setLogo(getLogoName(purchaseOrderVO.getProductCode(), purchaseOrderVO.getDocContext()));

		view.setMailToName(purchaseOrderVO.getClient());
		view.setClientAccountCode(purchaseOrderVO.getClientCode());
		view.setClientAccountName(purchaseOrderVO.getClient());
		List<Object[]> resultList = purchaseOrderReportDAO.getAccountAddressInfo(purchaseOrderVO.getClientCode());
		Object[] record = null;
		if (resultList != null && resultList.size() > 0) {
			int j = 0;
			record = resultList.get(0);
			String addressLine1 = record[j += 1] != null ? (String) record[j] : "";
			String addressLine2 = record[j += 1] != null ? (String) record[j] : "";
			String city = record[j += 1] != null ? (String) record[j] : "";
			String state = record[j += 1] != null ? (String) record[j] : "";
			String zip = record[j += 1] != null ? (String) record[j] : "";
			view.setMailToCompleteAddress(DisplayFormatHelper.formatAddressForTable(null, addressLine1, addressLine2,
					null, null, city, state, zip, RPT_NEW_LINE_CHAR));
		}

		if (purchaseOrderVO.getDrvId() != null) {
			Driver driver = driverDAO.findById(purchaseOrderVO.getDrvId()).orElse(null);
			view.setDriverName(driver.getDriverForename() + " " + driver.getDriverSurname());
			view.setDriverCompleteAddress(getDriverAddress(driver));
			view.setDriverPhoneNumber(getDriverPhone(driver));
			if (driver.getDriverCurrentCostCenter() != null) {
				view.setDriverCostCenter(driver.getDriverCurrentCostCenter().getCostCenterCode());
			}
		} else {
			view.setDriverName("");
			view.setDriverCompleteAddress("");
			view.setDriverPhoneNumber("");
			view.setDriverCostCenter("");
		}

		VehicleInfoVO vehicleInfoVO = populateVehicleInfoVO(purchaseOrderVO);
		DeliveringDealerInfoVO deliveringDealerInfoVO = populateDeliveringDealerInfoVO(purchaseOrderVO);
		List<Object[]> resultListnew = supplierDAO.getSupplierAddressByType((long) 1, "S", purchaseOrderVO.getDeliveringDealerCode(), ADDRESS_TYPE_POST);
		Object[] rec = null;
		if (resultListnew != null && resultListnew.size() > 0) {
			int j = 0;
			rec = resultListnew.get(0);
			deliveringDealerInfoVO.setName((String) rec[j]);
			deliveringDealerInfoVO.setAddressLine1(rec[j += 2] != null ? (String) rec[j] : "");
			deliveringDealerInfoVO.setAddressLine2(rec[j += 1] != null ? (String) rec[j] : "");
			deliveringDealerInfoVO.setCity(rec[j += 1] != null ? (String) rec[j] : "");
			deliveringDealerInfoVO.setState(rec[j += 1] != null ? (String) rec[j] : "");
			deliveringDealerInfoVO.setZip(rec[j += 1] != null ? (String) rec[j] : "");
			deliveringDealerInfoVO.setCityStateZip(deliveringDealerInfoVO.getCity() + " "
					+ deliveringDealerInfoVO.getState() + "  " + deliveringDealerInfoVO.getZip());
			deliveringDealerInfoVO.setAddress(DisplayFormatHelper.formatAddressForTable(null, deliveringDealerInfoVO.getAddressLine1(),
							deliveringDealerInfoVO.getAddressLine2(), null, null, deliveringDealerInfoVO.getCity(),
							deliveringDealerInfoVO.getState(), deliveringDealerInfoVO.getZip(), RPT_NEW_LINE_CHAR));
		view.setDeliveryCompleteAddress(deliveringDealerInfoVO.getAddress());
		view.setDeliveryName(deliveringDealerInfoVO.getName()); // added for HD-313
		}

		view.setUnitNo(purchaseOrderVO.getUnitNo());
		view.setTrimDescription(purchaseOrderVO.getModelDesc());
		view.setExteriorColor(vehicleInfoVO.getBolyColor());
		view.setInteriorColor(vehicleInfoVO.getInteriorColor());

		if (purchaseOrderVO.getRevNo() == 0) {
			view.setOrderDateStr(purchaseOrderVO.getPOReleaseDate());
		} else {
			view.setOrderDateStr(MALUtilities
					.getNullSafeDatetoString(purchaseOrderReportDAO.getFirstPOPostedDate(purchaseOrderVO.getDocNo())));
		}

		view.setRequestDateStr(purchaseOrderVO.getClientRequestedInfo());
		view.setFleetRefNo(purchaseOrderVO.getFleetRefNo());
		if (purchaseOrderVO.getOrderType() != null && purchaseOrderVO.getOrderType().equals("F")) {
			SupplierProgressHistory sph = supplierProgressHistoryDAO.findOldestSupplierProgressHistoryForDocAndTypeById(docId, "14_ETA");
			if(!MALUtilities.isEmpty(sph) && !MALUtilities.isEmpty(sph.getActionDate())){
				view.setEtaDateStr(MALUtilities.getNullSafeDatetoString(sph.getActionDate()));
			}else{
				view.setEtaDateStr(CONTACT_CLIENT_SUPPORT_TEXT);
			}
		} else {
			view.setEtaDateStr(CONTACT_CLIENT_SUPPORT_TEXT);
		}

		Long fmsId = purchaseOrderVO.getReplacementFmsId();
		FleetMaster fleetMaster = null;
		if (fmsId != null) {
			fleetMaster = fleetMasterDAO.findById(fmsId).orElse(null);
			view.setReplacesUnitNo(fleetMaster.getUnitNo());
			view.setReplacesTrimDescription(fleetMaster.getModel().getModelDescription());
		} else {
			view.setReplacesUnitNo("");
			view.setReplacesTrimDescription("");
		}

		if(purchaseOrderVO.getLeaseType().equalsIgnoreCase("PUR") && (!MALUtilities.isEmpty(fleetMaster))){
			List<QuotationModel> qmds = quotationModelDAO.findByUnitNo(fleetMaster.getUnitNo());	
			QuotationModel qmd = null;
			for(QuotationModel quotatonModel : qmds){
				if(quotatonModel.getQuoteStatus() == (QuotationService.STATUS_ACCEPTED) || 
						quotatonModel.getQuoteStatus() == (QuotationService.STATUS_ON_CONTRACT) || 
						quotatonModel.getQuoteStatus() == (QuotationService.STATUS_ALLOCATED_TO_GRD) || 
						quotatonModel.getQuoteStatus() == (QuotationService.STATUS_GRD_COMPLETE)){
					qmd = quotatonModel;
					break;
				}
			}
			if(!MALUtilities.isEmpty(qmd)){
				String replacementUnitLeaseType = quotationModelDAO.getLeaseType(qmd.getQmdId());
				if(!(replacementUnitLeaseType.equalsIgnoreCase("PUR") || replacementUnitLeaseType.equalsIgnoreCase("MAX"))){
					view.setLeaseExpirationDateStr(purchaseOrderVO.getLeaseExpirationDate());
				}else{
					view.setLeaseExpirationDateStr("");
				}
			}
		}else{
			view.setLeaseExpirationDateStr(purchaseOrderVO.getLeaseExpirationDate());
		}

		StringBuilder sb = new StringBuilder("");
		List<Object> objList = purchaseOrderReportDAO.getStandardAccessories(purchaseOrderVO.getQmdId());

		if (objList != null) {
			for (Object access : objList) {
				sb.append((String) access).append(RPT_NEW_LINE_CHAR);
			}
		}
		view.setStandardEquipments(sb.toString());

		objList = purchaseOrderReportDAO.getModelAccessories(purchaseOrderVO.getQmdId());
		sb = new StringBuilder("");
		if (objList != null) {
			for (Object access : objList) {
				sb.append((String) access).append(RPT_NEW_LINE_CHAR);
			}
		}
		view.setFactoryEquipments(sb.toString());
		objList = purchaseOrderReportDAO.getDealerAccessories(purchaseOrderVO.getQmdId());
		sb = new StringBuilder("");
		if (objList != null) {
			for (Object access : objList) {
				sb.append((String) access).append(RPT_NEW_LINE_CHAR);
			}
		}
		view.setDealerEquipments(sb.toString());

		return view;
	}

	// based on main po and thpDocId
	private PurchaseOrderVO getThirdPartyPurchaseOrderVO(Long thpDocId, String stockYn) {

		PurchaseOrderVO purchaseOrderVO = new PurchaseOrderVO();

		Long mainPoDocId = purchaseOrderReportDAO.getMainDocIdForNonStock(thpDocId);
		Doc mainPo = docDAO.findById(mainPoDocId).orElse(null);
		Doc thpDoc = docDAO.findById(thpDocId).orElse(null);
		String mainPOStockYN = mainPo.getSourceCode().equals(PO_FLORDER) ? "Y" : "N";
		
		purchaseOrderVO.setMainPoId(mainPoDocId);
		purchaseOrderVO.setThPoId(thpDocId);
		purchaseOrderVO.setStockYn(stockYn);
		//populate base line PO details. May be need to remove this generic method call and fetch data which is really needed for 3rd party PO 
		purchaseOrderVO = getPurchaseOrderVO(mainPoDocId, mainPOStockYN);

		if(! MALUtilities.convertYNToBoolean(stockYn)){//has quote
			QuotationModel quotationModel = quotationModelDAO.findById(thpDoc.getGenericExtId()).orElse(null);
			String clientRequestType = quotationModel.getClientRequestType() ;
			String clientRequestInfo = "" ;
			if(clientRequestType != null){
				if(clientRequestType.equalsIgnoreCase(CLIENT_REQUEST_TYPE_AS)){
					clientRequestInfo = CLIENT_REQUEST_TYPE_AS_DESC;
				}else if(clientRequestType.equalsIgnoreCase(CLIENT_REQUEST_TYPE_NS)){
					clientRequestInfo = CLIENT_REQUEST_TYPE_NS_DESC;
				} else {
					clientRequestInfo = MALUtilities.getNullSafeDatetoString(quotationModel.getRequiredDate());
				}
			}
			SupplierProgressHistory sph = supplierProgressHistoryDAO.findOldestSupplierProgressHistoryForDocAndTypeById(mainPoDocId, "15_DSMFGDV");			
			purchaseOrderVO.setClientRequestedInfo(clientRequestInfo);
			purchaseOrderVO.setClient(quotationModel.getQuotation().getExternalAccount().getAccountName());
			purchaseOrderVO.setDesiredArrivalDateToDealerOrVendor(MALUtilities.getNullSafeDatetoString(sph != null ? sph.getActionDate() : null ));
			purchaseOrderVO.setDrvId(quotationModel.getQuotation().getDrvDrvId());
			purchaseOrderVO.setQmdId(quotationModel.getQmdId());
		}else{
			purchaseOrderVO.setQmdId(null);
		}
		
		
		List<Object[]> resultList = purchaseOrderReportDAO.getThirdPartyPODetails(thpDocId);
		Object[] record = null;
		if (resultList != null && resultList.size() > 0) {
			int j = 0;
			record = resultList.get(0);
			Date POReleaseDate = record[j] != null ? (Date) record[j] : null;
			purchaseOrderVO.setPOReleaseDate(MALUtilities.getNullSafeDatetoString(POReleaseDate));
			purchaseOrderVO.setPONO((String) record[j += 1]);
			purchaseOrderVO.setVendorName(record[j += 3] != null ? (String) record[j] : "");
			purchaseOrderVO.setPoPurchasePrice(record[j += 1] != null ? (CommonCalculations.getRoundedValue((BigDecimal) record[j], 2)) : null);
		}

		return purchaseOrderVO;
	}

	private PurchaseOrderVO getPurchaseOrderVO(Long docId, String stockYn) {

		PurchaseOrderVO purchaseOrderVO = new PurchaseOrderVO();
		purchaseOrderVO.setMainPoId(docId);
		purchaseOrderVO.setStockYn(stockYn);

		List<Object[]> resultList = purchaseOrderReportDAO.getPurchaseOrderDetailForReport(docId, stockYn);
		Object[] record = null;
		if (resultList != null && resultList.size() > 0) {
			int j = 0;
			record = resultList.get(0);
			Date POReleaseDate = record[j] != null ? (Date) record[j] : null;
			purchaseOrderVO.setPOReleaseDate(MALUtilities.getNullSafeDatetoString(POReleaseDate));
			purchaseOrderVO.setDocNo((String) record[j += 1]);
			purchaseOrderVO.setRevNo(((BigDecimal) record[j += 1]).intValue());
			purchaseOrderVO.setPONO(purchaseOrderVO.getDocNo() + REV_NO_SEPARATOR + purchaseOrderVO.getRevNo());
			purchaseOrderVO.setDeliveringDealerCode(record[j += 1] != null ? (String) record[j] : "");
			purchaseOrderVO.setDocContext(record[j += 1] != null ? (String.valueOf((BigDecimal) record[j])) : null);
			purchaseOrderVO.setClient(record[j += 1] != null ? (String) record[j] : "");
			purchaseOrderVO.setClientCode(record[j += 1] != null ? (String) record[j] : "");
			purchaseOrderVO.setCompetitiveFleetProgram(record[j += 1] != null ? (String) record[j] : "");
			purchaseOrderVO.setIncentiveProgram(record[j += 1] != null ? (String) record[j] : "");
			purchaseOrderVO.setFleetRefNo(record[j += 1] != null ? (String) record[j] : "");
			purchaseOrderVO.setUnitNo(record[j += 1] != null ? (String) record[j] : "");
			purchaseOrderVO.setFactoryOrderNo(record[j += 1] != null ? (String) record[j] : "");
			purchaseOrderVO.setVin(record[j += 1] != null ? (String) record[j] : "");
			purchaseOrderVO.setDesiredArrivalDateToDealerOrVendor(
					record[j += 1] != null ? MALUtilities.getNullSafeDatetoString((Date) record[j]) : "");
			purchaseOrderVO.setFINOrFANNo(record[j += 1] != null ? (String) record[j] : "");
			purchaseOrderVO.setClientRequestedInfo(record[j += 1] != null ? (String) record[j] : "");
			purchaseOrderVO.setVendorName(record[j += 1] != null ? (String) record[j] : "");
			purchaseOrderVO.setQmdId(record[j += 1] != null ? ((BigDecimal) record[j]).longValue() : null);
			purchaseOrderVO.setOrderType(record[j += 1] != null ? String.valueOf(record[j]) : "");
			purchaseOrderVO.setProductCode(record[j += 1] != null ? (String) record[j] : "");
			purchaseOrderVO.setDrvId(record[j += 1] != null ? ((BigDecimal) record[j]).longValue() : null);
			purchaseOrderVO.setReplacementFmsId(record[j += 1] != null ? ((BigDecimal) record[j]).longValue() : null);
			purchaseOrderVO.setMdlId(((BigDecimal) record[j += 1]).longValue());
			purchaseOrderVO.setModelDesc(record[j += 1] != null ? (String) record[j] : "");
			purchaseOrderVO.setBodyColor(record[j += 1] != null ? (String) record[j] : "");
			purchaseOrderVO.setInteriorColor(record[j += 1] != null ? (String) record[j] : "");
			purchaseOrderVO.setLeaseExpirationDate(
					record[j += 1] != null ? MALUtilities.getNullSafeDatetoString((Date) record[j]) : "");
			purchaseOrderVO.setLeaseType(record[j += 1] != null ? (String) record[j] : "");
		}

		return purchaseOrderVO;

	}

	private String getLogoName(String productCode, String docContext) {
		// 1 leasing , 2 LTD
		String logoName = RPT_LEASING_LOG_NAME;
		if (docContext != null && docContext.equals("2")) {
			logoName = RPT_LTD_LOG_NAME;
		}

		String reportOverridenLogoContext = purchaseOrderReportDAO.getReportOverridenLogoContext(productCode);
		if (reportOverridenLogoContext != null) {
			if (reportOverridenLogoContext.equals("2")) {
				logoName = RPT_LTD_LOG_NAME;
			} else if(reportOverridenLogoContext.equals("3")){
				logoName = RPT_LLC_LOG_NAME;
			} else {
				logoName = RPT_LEASING_LOG_NAME;
			}
		}

		return logoName;
	}

	private String getDriverAddress(Driver driver) {
		String address = "";
		for (DriverAddress driverAddress : driver.getDriverAddressList()) {
			if (driverAddress.getAddressType().getAddressType().equals("GARAGED")) { // &&
																						// driverAddress.getDefaultInd().equals(("Y")
				address = DisplayFormatHelper.formatAddressForTable(driverAddress.getBusinessAddressLine(),
						driverAddress.getAddressLine1(), driverAddress.getAddressLine2(), null, null,
						driverAddress.getCityDescription(),
						driverAddress.getRegionCode().getRegionCodesPK().getRegionCode(), driverAddress.getPostcode(),
						RPT_NEW_LINE_CHAR);
			}
		}
		return address;
	}

	private String getDriverPhone(Driver driver) {

		String phoneNo = "";
		for (PhoneNumber phoneNumber : driver.getPhoneNumbers()) {
			if (phoneNumber.getPreferredInd() != null && phoneNumber.getPreferredInd().equals("Y")) {

				phoneNo = phoneNumber.getAreaCode() + "-" + phoneNumber.getNumber();
				if (phoneNumber.getExtensionNumber() != null) {
					phoneNo = phoneNo + " Ext " + phoneNumber.getExtensionNumber();
				}
			}
		}
		return phoneNo;

	}

	private VehicleInfoVO populateVehicleInfoVO(PurchaseOrderVO purchaseOrderVO) {

		VehicleInfoVO vehicleInfoVO = new VehicleInfoVO();
		vehicleInfoVO.setModelDesc(purchaseOrderVO.getModelDesc());
		vehicleInfoVO.setBolyColor(purchaseOrderVO.getBodyColor());
		vehicleInfoVO.setInteriorColor(purchaseOrderVO.getInteriorColor());
		List<Object> objList = null;
		if (purchaseOrderVO.getQmdId() != null) {
			objList = purchaseOrderReportDAO.getPowertrainInfo(purchaseOrderVO.getQmdId());
		} else {
			objList = purchaseOrderReportDAO.getPowertrainInfoForDoc(purchaseOrderVO.getMainPoId());
		}
		StringBuilder sb = new StringBuilder("");
		if (objList != null) {
			for (Object access : objList) {
				sb.append((String) access).append(RPT_NEW_LINE_CHAR);
			}
		}

		vehicleInfoVO.setPowertrain(sb.toString());

		String optionalAccessories = docDAO.getOptionalAccessories(purchaseOrderVO.getMainPoId());
		vehicleInfoVO.setOptionalEquipments(optionalAccessories != null ? optionalAccessories : "");

		sb = new StringBuilder("");
		List<String> portInstalledList = new ArrayList<String>();
		objList = purchaseOrderReportDAO.getTypedInstalledAccessoriesPO(purchaseOrderVO.getMainPoId(),
				ACCESORY_TYPE_DEALER, PORT_INSTALLED_CATEGORY_CODE);
		if (objList != null) {
			for (Object access : objList) {
				portInstalledList.add((String) access);
			}
		}
		if (purchaseOrderReportDAO.isPostProductionIsPortInstalledForMake(purchaseOrderVO.getMdlId())) {
			portInstalledList.addAll(getPostProductionInstalledOptions(purchaseOrderVO.getMainPoId()));
		}

		Collections.sort(portInstalledList);
		for (String access : portInstalledList) {
			sb.append(access).append(RPT_NEW_LINE_CHAR);
		}

		vehicleInfoVO.setPortInstalledEquipments(sb.toString());

		sb = new StringBuilder("");
		if (purchaseOrderVO.getQmdId() != null) {
			objList = purchaseOrderReportDAO.getStandardAccessories(purchaseOrderVO.getQmdId());
			if (objList != null) {
				for (Object access : objList) {
					sb.append((String) access).append(RPT_NEW_LINE_CHAR);
				}
			}
		}

		vehicleInfoVO.setStandardEquipments(sb.toString());

		return vehicleInfoVO;
	}

	private DeliveringDealerInfoVO populateDeliveringDealerInfoVO(PurchaseOrderVO purchaseOrderVO) {

		DeliveringDealerInfoVO deliveringDealerInfoVO = new DeliveringDealerInfoVO();

		if (MALUtilities.isNotEmptyString(purchaseOrderVO.getDeliveringDealerCode())) {

			List<Object[]> resultList = purchaseOrderReportDAO
					.getAccountAddressInfo(purchaseOrderVO.getDeliveringDealerCode());
			Object[] record = null;
			if (resultList != null && resultList.size() > 0) {
				int j = 0;
				record = resultList.get(0);
				deliveringDealerInfoVO.setName((String) record[j]);
				deliveringDealerInfoVO.setAddressLine1(record[j += 1] != null ? (String) record[j] : "");
				deliveringDealerInfoVO.setAddressLine2(record[j += 1] != null ? (String) record[j] : "");
				deliveringDealerInfoVO.setCity(record[j += 1] != null ? (String) record[j] : "");
				deliveringDealerInfoVO.setState(record[j += 1] != null ? (String) record[j] : "");
				deliveringDealerInfoVO.setZip(record[j += 1] != null ? (String) record[j] : "");
				deliveringDealerInfoVO.setCityStateZip(deliveringDealerInfoVO.getCity() + " "
						+ deliveringDealerInfoVO.getState() + "  " + deliveringDealerInfoVO.getZip());
				deliveringDealerInfoVO.setAddress(
						DisplayFormatHelper.formatAddressForTable(null, deliveringDealerInfoVO.getAddressLine1(),
								deliveringDealerInfoVO.getAddressLine2(), null, null, deliveringDealerInfoVO.getCity(),
								deliveringDealerInfoVO.getState(), deliveringDealerInfoVO.getZip(), RPT_NEW_LINE_CHAR));
				List<Object[]> contactsList = null;
				if (purchaseOrderVO.getOrderType() != null && purchaseOrderVO.getOrderType().equals("L")) {
					contactsList = purchaseOrderReportDAO.getDealerContactDetailsList(1L, "S",
							purchaseOrderVO.getDeliveringDealerCode());
				} else {
					contactsList = purchaseOrderReportDAO.getVendorContactDetailsList(1L, "S",
							purchaseOrderVO.getDeliveringDealerCode());
				}

				if (contactsList != null && contactsList.size() > 0) {
					Object[] dealerContactDetails = contactsList.get(0);
					deliveringDealerInfoVO
							.setContactName(dealerContactDetails[1] != null ? (String) dealerContactDetails[1] : "");
					deliveringDealerInfoVO
							.setPhoneNo(dealerContactDetails[2] != null ? (String) dealerContactDetails[2] : "");
					deliveringDealerInfoVO.setEmail((String) (dealerContactDetails[3] != null ? dealerContactDetails[3]
							: dealerContactDetails[4]));
					String phoneEmail = "";
					if (deliveringDealerInfoVO.getPhoneNo() != null
							&& deliveringDealerInfoVO.getPhoneNo().trim().length() > 0) {
						phoneEmail = deliveringDealerInfoVO.getPhoneNo();
						if (deliveringDealerInfoVO.getEmail() != null
								&& deliveringDealerInfoVO.getEmail().trim().length() > 0) {
							phoneEmail = phoneEmail + RPT_NEW_LINE_CHAR + deliveringDealerInfoVO.getEmail();
						}
					} else {
						if (deliveringDealerInfoVO.getEmail() != null
								&& deliveringDealerInfoVO.getEmail().trim().length() > 0) {
							phoneEmail = deliveringDealerInfoVO.getEmail();
						}
					}
					deliveringDealerInfoVO.setPhoneNoEmail(phoneEmail);
				} else {
					deliveringDealerInfoVO.setContactName("");
					deliveringDealerInfoVO.setPhoneNo("");
					deliveringDealerInfoVO.setEmail("");
					deliveringDealerInfoVO.setPhoneNoEmail("");
				}
			}
		}

		List<String> dealerInstalledList = new ArrayList<String>();
		if (!purchaseOrderReportDAO.isPostProductionIsPortInstalledForMake(purchaseOrderVO.getMdlId())) {
			dealerInstalledList = getPostProductionInstalledOptions(purchaseOrderVO.getMainPoId());
		}
		List<Object> objList = purchaseOrderReportDAO.getTypedInstalledAccessoriesPO(purchaseOrderVO.getMainPoId(),
				ACCESORY_TYPE_DEALER, DEALER_INSTALLED_CATEGORY_CODE);
		if (objList != null) {
			for (Object access : objList) {
				dealerInstalledList.add((String) access);
			}
		}
		Collections.sort(dealerInstalledList);
		StringBuilder sb = new StringBuilder("");
		for (String access : dealerInstalledList) {
			sb.append(access).append(RPT_NEW_LINE_CHAR);
		}
		deliveringDealerInfoVO.setDealerInstalledOptions(sb.toString());

		return deliveringDealerInfoVO;
	}

	private List<String> getPostProductionInstalledOptions(Long mainPO) {

		List<String> postProductionInstalled = new ArrayList<String>();
		List<Object> objList = purchaseOrderReportDAO.getTypedInstalledAccessoriesPO(mainPO, ACCESORY_TYPE_DEALER,
				POST_PRODUCTION_CATEGORY_CODE);

		if (objList != null) {
			for (Object access : objList) {
				postProductionInstalled.add((String) access);
			}
		}

		return postProductionInstalled;
	}

	private List<VendorInfoVO> getVendorUpfitList(PurchaseOrderVO purchaseOrderVO) {
		StringBuilder sb = new StringBuilder();
		List<Object> objList = null;
		List<VendorInfoVO> vendorUpfitList = new ArrayList<VendorInfoVO>();
		List<Object[]> resultList =  null;
		if(MALUtilities.isEmpty(purchaseOrderVO.getQmdId())){
			resultList =  purchaseOrderReportDAO.getVendorInfo(purchaseOrderVO.getMainPoId());
		}else{
			resultList =  purchaseOrderReportDAO.getVendorInfo(purchaseOrderVO.getMainPoId() , purchaseOrderVO.getQmdId());
		}
		if (resultList != null && resultList.size() > 0) {
			for (Object[] record1 : resultList) {
				int j = 0;
				VendorInfoVO vendorInfoVO = new VendorInfoVO();
				vendorInfoVO.setTpDocId(((BigDecimal) record1[j]).longValue());
				vendorInfoVO.setAccountCode((String) record1[j += 1]);
				vendorInfoVO.setName((String) record1[j += 1]);
				vendorInfoVO.setAddressLine1(record1[j += 1] != null ? (String) record1[j] : "");
				vendorInfoVO.setAddressLine2(record1[j += 1] != null ? (String) record1[j] : "");
				vendorInfoVO.setCity(record1[j += 1] != null ? (String) record1[j] : "");
				vendorInfoVO.setState(record1[j += 1] != null ? (String) record1[j] : "");
				vendorInfoVO.setZip(record1[j += 1] != null ? (String) record1[j] : "");
				vendorInfoVO.setLeadTime(record1[j += 1] != null ? String.valueOf((BigDecimal) record1[j]) : null);
				vendorInfoVO.setCityStateZip(
						vendorInfoVO.getCity() + " " + vendorInfoVO.getState() + "  " + vendorInfoVO.getZip());
				vendorInfoVO.setAddress(DisplayFormatHelper.formatAddressForTable(null, vendorInfoVO.getAddressLine1(),
						vendorInfoVO.getAddressLine2(), null, null, vendorInfoVO.getCity(), vendorInfoVO.getState(),
						vendorInfoVO.getZip(), RPT_NEW_LINE_CHAR));

				List<Object[]> vendorContactsList = purchaseOrderReportDAO.getVendorContactDetailsList(1L, "S",
						vendorInfoVO.getAccountCode());
				if (vendorContactsList != null && vendorContactsList.size() > 0) {
					Object[] vendorContactDetails = vendorContactsList.get(0);
					vendorInfoVO
							.setContactName(vendorContactDetails[1] != null ? (String) vendorContactDetails[1] : "");
					vendorInfoVO.setPhoneNo(vendorContactDetails[2] != null ? (String) vendorContactDetails[2] : "");
					vendorInfoVO.setEmail((String) (vendorContactDetails[3] != null ? vendorContactDetails[3]
							: vendorContactDetails[4]));

					String phoneEmail = "";
					if (vendorInfoVO.getPhoneNo() != null && vendorInfoVO.getPhoneNo().trim().length() > 0) {
						phoneEmail = vendorInfoVO.getPhoneNo();
						if (vendorInfoVO.getEmail() != null && vendorInfoVO.getEmail().trim().length() > 0) {
							phoneEmail = phoneEmail + RPT_NEW_LINE_CHAR + vendorInfoVO.getEmail();
						}
					} else {
						if (vendorInfoVO.getEmail() != null && vendorInfoVO.getEmail().trim().length() > 0) {
							phoneEmail = vendorInfoVO.getEmail();
						}
					}
					vendorInfoVO.setPhoneAndEmail(phoneEmail);

				} else {
					vendorInfoVO.setContactName("");
					vendorInfoVO.setPhoneNo("");
					vendorInfoVO.setEmail("");
					vendorInfoVO.setPhoneAndEmail("");
				}

				Date eta = record1[j += 3] != null ? (Date) record1[j] : null;
				vendorInfoVO.setETA(MALUtilities.getNullSafeDatetoString(eta));
				sb = new StringBuilder("");
				if (purchaseOrderVO.getQmdId() != null) {
					objList = purchaseOrderReportDAO.getVendorQuoteNumbers(purchaseOrderVO.getQmdId(),
							vendorInfoVO.getTpDocId());
					if (objList != null) {
						for (Object quoteNumber : objList) {
							if (sb.length() >= 1) {
								sb.append(",");
							}
							sb.append((String) quoteNumber);
						}
					}
				}
				vendorInfoVO.setVendorQuoteNo(sb.toString());

				// populate accessories for third party doc
				vendorInfoVO.setAccessoriesList(new ArrayList<EquipmentVO>());
				objList = purchaseOrderReportDAO.getDealerAccessoriesForPO(vendorInfoVO.getTpDocId());
				if (objList != null) {
					for (Object access : objList) {
						vendorInfoVO.getAccessoriesList().add(new EquipmentVO((String) access));
					}
				}

				// add only those vendor/upfits, which has lead time
				if (vendorInfoVO.getLeadTime() != null && Long.parseLong(vendorInfoVO.getLeadTime()) > 0) {
					vendorUpfitList.add(vendorInfoVO);
				}
			}
		}

		return vendorUpfitList;
	}

	private String getLogistics(Long mainPOId, Long thPoId, String stockYn) {

		String logistics = "";

		if (mainPOId != null) {
			String mainPoLogistics = purchaseOrderReportDAO.getDocNarratives(mainPOId, DOC_NARRATIVE_SPEC_INSTR_TYPE);
			logistics = mainPoLogistics == null ? "" : mainPoLogistics;
			if (stockYn.equals("Y")) {
				String docLLogistics = purchaseOrderReportDAO.getDoclNarratives(mainPOId, 1L);
				if (docLLogistics != null && docLLogistics.trim().length() > 0) {
					if (logistics != null && logistics.trim().length() > 0) {
						logistics = logistics + RPT_NEW_LINE_CHAR;
					}
					logistics = logistics + docLLogistics;
				}
			}
		}

		if (thPoId != null) {
			String thpLogistics = purchaseOrderReportDAO.getDocNarratives(thPoId, DOC_NARRATIVE_SPEC_INSTR_TYPE);
			if (thpLogistics != null && thpLogistics.trim().length() > 0) {
				if (logistics != null && logistics.trim().length() > 0) {
					logistics = logistics + RPT_NEW_LINE_CHAR;
				}
				logistics = logistics + thpLogistics;
			}
		}

		if (logistics != null && logistics.trim().length() == 0) {
			logistics = null; // this is needed to hide section completely from
								// UI
		}

		return logistics;
	}

	private boolean displayFooterText(String leaseType) {

		boolean displayFooter = false;
		//if(!leaseType.equalsIgnoreCase("PUR")){  Commented for HD-506. As per bug we need to display new footer text on 3rd-Party (Product Purchase) PO's as well.  
			WillowConfig willowConfig = willowConfigDAO.findById(FOOTER_CONFIG_VALUE).orElse(null);
			if (willowConfig != null && willowConfig.getConfigValue().equals("Y")) {
				displayFooter = true;
			}
		//}
		
		return displayFooter;
	}

	@Override
	public PurchaseOrderCoverSheetVO getPurchaseOrderCoverSheetReportVO(Long docId) throws MalException {

		PurchaseOrderCoverSheetVO view = new PurchaseOrderCoverSheetVO();

		PurchaseOrderVO purchaseOrderVO = getPurchaseOrderVO(docId, "N");

		List<Object[]> resultList = purchaseOrderReportDAO
				.getAccountAddressInfo(purchaseOrderVO.getDeliveringDealerCode());
		Object[] record = null;
		if (resultList != null && resultList.size() > 0) {
			int j = 0;
			record = resultList.get(0);
			String deliveringDealerName = (String) record[j];
			String addressLine1 = record[j += 1] != null ? (String) record[j] : "";
			String addressLine2 = record[j += 1] != null ? (String) record[j] : "";
			String city = record[j += 1] != null ? (String) record[j] : "";
			String state = record[j += 1] != null ? (String) record[j] : "";
			String zip = record[j += 1] != null ? (String) record[j] : "";

			view.setFullAddress(DisplayFormatHelper.formatAddressForTable(deliveringDealerName, addressLine1,
					addressLine2, null, null, city, state, zip, RPT_NEW_LINE_CHAR));
		}

		return view;
	}
	
	
	/**
	 * Determine whether the PO was relased prior to the deployment of the PO Released Queue or after.
	 * Per the business, a PO deemed to be released prior to the deployment of the PO Released Queue 
	 * when it has a Delivering Dealer assigned and it's release date is prior to the installation date
	 * of the Queue.
	 * 
	 * @param docId Doc Purchase Order
	 * @return boolean indicating whether the PO was released prior to the deployment of the PO Release Queue or not
	 */
	@Transactional(readOnly=true)
	public boolean isReleasedPriorToPurchaseOrderQueue(Long docId) {
		boolean isReleasedPriorToPOQueue = false;
		DocProperty docProperty = docPropertyDAO.findByName(DocPropertyEnum.CONFIRM_DOC_DATE.getCode());
		Doc doc = documentService.getDocumentByDocId(docId);
		
		if(!MALUtilities.isEmpty(doc.getSubAccCode()) && doc.getPostedDate().compareTo(docProperty.getVersionts()) < 1) {
			isReleasedPriorToPOQueue = true;
		}
		
		return isReleasedPriorToPOQueue; 
	}	
	
	private CourtesyDeliveryInstructionVO populateCourtesyDeliveryInstructionVO(Long docId) throws MalException {
		
		CourtesyDeliveryInstructionVO cDInstructionVO = new CourtesyDeliveryInstructionVO();
		String clientFedralId = "";
		String clientAddress = "";
		String clientName = "";
		if(!MALUtilities.isEmpty(docId)){
			
			Doc doc = docDAO.findById(docId).orElse(null);
			cDInstructionVO.setLogo(RPT_LLC_LOG_NAME);
			QuotationModel quotationModel = quotationModelDAO.findById(doc.getGenericExtId()).orElse(null);
			
			cDInstructionVO.setUnitNo(quotationModel.getUnitNo());
			if(!MALUtilities.isEmpty(quotationModel.getReplacementFmsId())){
				FleetMaster fms = fleetMasterDAO.findById(quotationModel.getReplacementFmsId()).orElse(null);
				if(!MALUtilities.isEmpty(fms)){
					cDInstructionVO.setReturningVehicleUnitNo(fms.getUnitNo());
					cDInstructionVO.setReturningVehicleVIN(fms.getVin());
				}
			}

			VehicleInfoVO vehicleInfoVo =  new VehicleInfoVO();
			vehicleInfoVo.setModelDesc(quotationModel.getModel().getModelDescription());
			cDInstructionVO.setVehicleInfoVO(vehicleInfoVo);
			
			// get the purchaser info based on account used for quote
			Object[] record = null;
			List<Object[]> purchaseAddressList = externalAccountDAO.getAccountAddressByType(quotationModel.getQuotation().getExternalAccount().getExternalAccountPK().getcId(),
																								quotationModel.getQuotation().getExternalAccount().getExternalAccountPK().getAccountType(),
																								quotationModel.getQuotation().getExternalAccount().getExternalAccountPK().getAccountCode(), 
																								ADDRESS_TYPE_POST);
			
			if (purchaseAddressList != null && purchaseAddressList.size() > 0) {
				int j = 0;
				record = purchaseAddressList.get(0);
				clientName = ((String) record[j]);
				clientFedralId = record[j += 1] != null ? (String) record[j] : "";
				
				String addressLine1 = record[j += 1] != null ? (String) record[j] : "";
				String addressLine2 = record[j += 1] != null ? (String) record[j] : "";
				String city = record[j += 1] != null ? (String) record[j] : "";
				String state = record[j += 1] != null ? (String) record[j] : "";
				String zip = record[j += 1] != null ? (String) record[j] : "";
				
				clientAddress = DisplayFormatHelper.formatAddressForTable(null, addressLine1, addressLine2,
						null, null, city, state, zip, RPT_NEW_LINE_CHAR);
			}
			
			ExtAccAffiliate extAccAffiliate = quotationModel.getExtAccAffiliate();
			if(!MALUtilities.isEmpty(extAccAffiliate)){
				cDInstructionVO.setPurchaserName(extAccAffiliate.getAffiliateName());
				cDInstructionVO.setPurchaserFedralId(extAccAffiliate.getTaxRegNo());
				
				ExtAccAddress extAccAddress = extAccAffiliate.getExternalAccountAddress();
				if(!MALUtilities.isEmpty(extAccAddress)){
					String addressLine1 = extAccAddress.getAddressLine1();
					String addressLine2 = extAccAddress.getAddressLine2();
					String city = extAccAddress.getCityDescription();
					String state = extAccAddress.getRegionAbbreviation();
					String zip = extAccAddress.getPostcode();
					
					String purchaseAddress = DisplayFormatHelper.formatAddressForTable(null, addressLine1, addressLine2,
							null, null, city, state, zip, RPT_NEW_LINE_CHAR);
					cDInstructionVO.setPurchaserAddress(purchaseAddress);
				}
			}else{
				cDInstructionVO.setPurchaserName(clientName);
				cDInstructionVO.setPurchaserAddress(clientAddress);
				cDInstructionVO.setPurchaserFedralId(clientFedralId);
			}
			
			
		
			DocSupplier orderingDealer = doc.getDocSupplier(WORKSHOP_CAPABILITY_ORDERING);
			
			List<Object[]> suppAddressList = supplierDAO.getSupplierAddressByType(orderingDealer.getSupplier().getEaCId(), 
																							orderingDealer.getSupplier().getEaAccountType(), 
																							orderingDealer.getSupplier().getEaAccountCode(),
																							ADDRESS_TYPE_POST);
			record = null;
			if (suppAddressList != null && suppAddressList.size() > 0) {
				int j = 0;
				record = suppAddressList.get(0);
				cDInstructionVO.setOrderingDealer((String) record[j]);
				cDInstructionVO.setOrderingDealerPhone(record[j += 1] != null ? (String) record[j] : "");
				
				String addressLine1 = record[j += 1] != null ? (String) record[j] : "";
				String addressLine2 = record[j += 1] != null ? (String) record[j] : "";
				String city = record[j += 1] != null ? (String) record[j] : "";
				String state = record[j += 1] != null ? (String) record[j] : "";
				String zip = record[j += 1] != null ? (String) record[j] : "";
				
				String orderingDealerAddress = DisplayFormatHelper.formatAddressForTable(null, addressLine1, addressLine2,
						null, null, city, state, zip, RPT_NEW_LINE_CHAR);
				cDInstructionVO.setOrderingDealerAddress(orderingDealerAddress.concat(RPT_NEW_LINE_CHAR).concat(cDInstructionVO.getOrderingDealerPhone()));
			}
			
			if(!MALUtilities.isEmpty(clientFedralId) && !MALUtilities.isEmpty(cDInstructionVO.getPurchaserFedralId())
					&& clientFedralId.equalsIgnoreCase(cDInstructionVO.getPurchaserFedralId())){
				// get tax exempt info for selected client
				List<Object[]> taxExemptlist = externalAccountDAO.getTaxExemptListByAccountInfo(quotationModel.getQuotation().getExternalAccount().getExternalAccountPK().getcId(),
						quotationModel.getQuotation().getExternalAccount().getExternalAccountPK().getAccountType(),
						quotationModel.getQuotation().getExternalAccount().getExternalAccountPK().getAccountCode());
	
				List<AccountTaxExemptVO> accTaxExemptVOList = new ArrayList<AccountTaxExemptVO>();
				AccountTaxExemptVO accExcVO = null;
				if(taxExemptlist != null && taxExemptlist.size() > 0){
					for(Object[] rec : taxExemptlist){
						accExcVO = new AccountTaxExemptVO(rec[0] != null ? (String) rec[0] : "", rec[1] != null ? (String) rec[1] : "");
						accTaxExemptVOList.add(accExcVO);
					}
				}
				cDInstructionVO.setAccTaxExemptVOList(accTaxExemptVOList);
			}
			
			// fetch driver info.
			Driver drv = driverService.getDriver(quotationModel.getQuotation().getDrvDrvId());
			if(!MALUtilities.isEmpty(drv)){
				DriverAddress da = driverService.getDriverAddress(drv.getDrvId(), ADDRESS_TYPE_GARAGE);
				if(!MALUtilities.isEmpty(da)){
					String driverAddress = DisplayFormatHelper.formatAddressForTable(da.getBusinessAddressLine(), da.getAddressLine1(), da.getAddressLine2(),
							null, null, da.getCityDescription(), da.getRegionAbbreviation(), da.getPostcode(), RPT_NEW_LINE_CHAR);
					cDInstructionVO.setDriverAddress(driverAddress);
					cDInstructionVO.setDriverName(da.getDriver().getDriverForename() + " " + da.getDriver().getDriverSurname());
					cDInstructionVO.setDriverEmail(da.getDriver().getEmail());
					cDInstructionVO.setDriverPhone(getDriverPhone(drv));
				}
			}
			
			List<String> responsibilityList = new ArrayList<String>();
			responsibilityList.add("<li>Perform the pre-delivery inspection including installing any port or manufacturer options.  These may be included in the trunk of the vehicle or shipped directly to your parts department.</li>");
			responsibilityList.add("<li>Perform any state-required inspections.</li>");
			responsibilityList.add("<li>Process the title and license plates.  Title should always be sent to Purchaser listed above.</li>");
			responsibilityList.add("<li>Contact the driver at the preferred phone number to schedule delivery.</li>");
			responsibilityList.add("<li>Collect insurance information from the driver at time of delivery.</li>");
			responsibilityList.add("<li>Report the delivery immediately. Complete the delivery receipt below and send with copy of the registration via email vehiclepurchasing@mikealbert.com, fax 513-956-2822, or call 888-368-8697.</li>");
			
			cDInstructionVO.setCdResponsibilityList(responsibilityList);
			cDInstructionVO.setSpecialInstr("You are required to obtain prior approval by Mike Albert for any equipment additions.  These will be paid only on a Mike Albert-approved purchase order.");
			
		}
		return cDInstructionVO;
		
	}
}
