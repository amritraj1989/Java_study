package com.mikealbert.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.jasperreports.engine.JasperExportManager;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.comparator.VendorInfoVOComparator;
import com.mikealbert.data.dao.ColourCodesDAO;
import com.mikealbert.data.dao.DealerAccessoryDAO;
import com.mikealbert.data.dao.DeliveryShipDetailDAO;
import com.mikealbert.data.dao.DocDAO;
import com.mikealbert.data.dao.DocLinkDAO;
import com.mikealbert.data.dao.DocPropertyDAO;
import com.mikealbert.data.dao.DocPropertyValueDAO;
import com.mikealbert.data.dao.DocSupplierDAO;
import com.mikealbert.data.dao.DriverAddressDAO;
import com.mikealbert.data.dao.ExternalAccountDAO;
import com.mikealbert.data.dao.MakeCountrySuppliersDAO;
import com.mikealbert.data.dao.ModelDAO;
import com.mikealbert.data.dao.OnbaseUploadedDocsDAO;
import com.mikealbert.data.dao.OrderProgressDAO;
import com.mikealbert.data.dao.PurchaseOrderReleaseQueueDAO;
import com.mikealbert.data.dao.PurchaseOrderReportDAO;
import com.mikealbert.data.dao.QuotationModelDAO;
import com.mikealbert.data.dao.SupplierDAO;
import com.mikealbert.data.dao.ThirdPartyPoQueueDAO;
import com.mikealbert.data.dao.TrimCodesDAO;
import com.mikealbert.data.entity.DeliveryShipDetail;
import com.mikealbert.data.entity.Doc;
import com.mikealbert.data.entity.DocPropertyValue;
import com.mikealbert.data.entity.DocSupplier;
import com.mikealbert.data.entity.Docl;
import com.mikealbert.data.entity.DriverAddress;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.Make;
import com.mikealbert.data.entity.MakeCountrySuppliers;
import com.mikealbert.data.entity.Model;
import com.mikealbert.data.entity.OnbaseUploadedDocs;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.Supplier;
import com.mikealbert.data.entity.ThirdPartyPoQueueV;
import com.mikealbert.data.enumeration.ClientRequestTypeEnum;
import com.mikealbert.data.enumeration.DocPropertyEnum;
import com.mikealbert.data.enumeration.DocumentNameEnum;
import com.mikealbert.data.enumeration.DocumentSourceEnum;
import com.mikealbert.data.enumeration.DocumentStatus;
import com.mikealbert.data.enumeration.VehicleOrderType;
import com.mikealbert.data.vo.DbProcParamsVO;
import com.mikealbert.data.vo.VendorInfoVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;
import com.mikealbert.service.enumeration.OnbaseDocTypeEnum;
import com.mikealbert.service.enumeration.OnbaseIndexEnum;
import com.mikealbert.service.reporting.JasperReportService;
import com.mikealbert.service.task.POArchieveTask;
import com.mikealbert.service.task.TaskResponse;
import com.mikealbert.service.vo.OnbaseKeywordVO;
import com.mikealbert.service.vo.VehiclePurchaseOrderVO;
import com.mikealbert.util.MALUtilities;

@Service("mainPurchaseOrderService")
@Transactional
public class PurchaseOrderServiceImpl implements PurchaseOrderService {
	public MalLogger logger = MalLoggerFactory.getLogger(this.getClass());
	
	
	
	
	@Resource PurchaseOrderReleaseQueueDAO purchaseOrderReleaseQueueDAO;
	@Resource QuotationModelDAO quotationModelDAO;
	@Resource FleetMasterService fleetMasterService;
	@Resource DocDAO docDAO;
	@Resource ColourCodesDAO colourCodesDAO;
	@Resource TrimCodesDAO trimCodesDAO;
	@Resource DocPropertyValueDAO docPropertyValueDAO;
	@Resource DocLinkDAO docLinkDAO;
	@Resource DeliveryShipDetailDAO deliveryShipDetailDAO;
	@Resource DriverAddressDAO driverAddressDAO;
	@Resource DocPropertyDAO docPropertyDAO;
	@Resource ModelDAO modelDAO;
	@Resource MakeCountrySuppliersDAO makeCountrySuppliersDAO;
	@Resource SupplierDAO supplierDAO;
	@Resource WillowConfigService willowConfigService;
	@Resource DocSupplierDAO docSupplierDAO;
	@Resource DocumentService documentService;
	@Resource QuoteModelPropertyValueService quoteModelPropertyValueService;
	@Resource ThirdPartyPoQueueDAO thirdPartyPoQueueDAO;
	@Resource DealerAccessoryDAO dealerAccessoryDAO;
	@Resource UpfitProgressService upfitProgressService;
	@Resource QuotationService quotationService;
	@Resource ExternalAccountDAO externalAccountDAO;
	@Resource OnbaseUploadedDocsDAO onbaseUploadedDocsDAO;
	@Resource OnbaseArchivalService onbaseArchivalService ;
	@Resource JasperReportService jasperReportService;	
	@Resource OrderProgressDAO orderProgressDAO;
	@Resource OnbaseRetrievalService onbaseRetrievalService;
	@Resource PurchaseOrderReportDAO purchaseOrderReportDAO;

	private final String PORT_INSTALLED_CATEGORY_CODE = "17";
	private final String POST_PRODUCTION_CATEGORY_CODE = "18";
	private final String DEALER_INSTALLED_CATEGORY_CODE = "4";
	private final String ACCESORY_TYPE_DEALER = "DEALER";
	
	/*
	 * Creates main and 3rd party purchase order for accepted quote based on supplied input.
	 * 
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public DbProcParamsVO createPurchaseOrder(Long qmdId, Long cId, String userId)
					throws MalBusinessException, MalException {
		
		DbProcParamsVO parameterVO = null;
		parameterVO = purchaseOrderReleaseQueueDAO.createPurchaseOrder(qmdId,cId, userId);

		return parameterVO;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public VehiclePurchaseOrderVO getMainPurchaseOrderDetails(Long qmdId, Long mainDocId) {
		
		
		Model model =  null;			
		VehiclePurchaseOrderVO vehiclePurchaseOrderVO = new VehiclePurchaseOrderVO();
		Doc doc = docDAO.findById(mainDocId).orElse(null);	
		
		if(qmdId  != null){
			
			QuotationModel quotationModel = quotationModelDAO.findById(qmdId).orElse(null);
			FleetMaster fleetMaster =  fleetMasterService.findByUnitNo(quotationModel.getUnitNo());
			model = quotationModel.getModel();						
			vehiclePurchaseOrderVO.setUnitNumber(quotationModel.getUnitNo());			
			vehiclePurchaseOrderVO.setFmsId(fleetMaster != null ? fleetMaster.getFmsId() : null);
			vehiclePurchaseOrderVO.setVin((fleetMaster != null && fleetMaster.getVin() != null) ? fleetMaster.getVin() : null);
			vehiclePurchaseOrderVO.setQmdId(quotationModel.getQmdId());
			vehiclePurchaseOrderVO.setQuoId(quotationModel.getQuotation().getQuoId());		
			vehiclePurchaseOrderVO.setColor(quotationModel.getColourCode() != null ? quotationModel.getColourCode().getDescription() : null);			
			vehiclePurchaseOrderVO.setReturningUnit(quotationModel.getReplacementFmsId() == null ? "No" : "Yes");		
			vehiclePurchaseOrderVO.setTrim(quotationModel.getTrimCode().getTrimDescription());
			vehiclePurchaseOrderVO.setOrderType(quotationModel.getOrderType());
			vehiclePurchaseOrderVO.setMakId(model.getMake().getMakId());
			
			if(quotationModel.getQuotationNotes().size() >0 && !MALUtilities.isEmpty(quotationModel.getQuotationNotes().get(0)))
				vehiclePurchaseOrderVO.setLogisticNotes(quotationModel.getQuotationNotes().get(0).getQuoteNotes());
			else
				vehiclePurchaseOrderVO.setLogisticNotes(null);
			
			if(!MALUtilities.isEmpty(quotationModel.getClientRequestType())){
				if(quotationModel.getClientRequestType().equalsIgnoreCase("OD")){
					vehiclePurchaseOrderVO.setRequestedDate(MALUtilities.getNullSafeDatetoString(quotationModel.getRequiredDate()));
				}else{
					vehiclePurchaseOrderVO.setRequestedDate(ClientRequestTypeEnum.getClientRequestTypeEnum(quotationModel.getClientRequestType()).getDescription());
				}
			}else{
				vehiclePurchaseOrderVO.setRequestedDate(null);
			}
		}else{//In case of released stock order 
	
			FleetMaster fleetMaster =  fleetMasterService.getFleetMasterByFmsId(Long.parseLong(doc.getDists().get(0).getCdbCode1()));
			vehiclePurchaseOrderVO.setFmsId(fleetMaster.getFmsId());
			model = fleetMaster.getModel();
			vehiclePurchaseOrderVO.setUnitNumber(fleetMaster.getUnitNo());			
			vehiclePurchaseOrderVO.setVin(fleetMaster.getVin());
			vehiclePurchaseOrderVO.setReturningUnit("No");	
			vehiclePurchaseOrderVO.setOrderType(VehicleOrderType.FACTORY.getCode());
			for (Docl docl  : doc.getDocls()) {
				if(docl.getUserDef4() != null && docl.getUserDef4().equals("MODEL")){				
					vehiclePurchaseOrderVO.setColor(docl.getUserDef1() != null ? colourCodesDAO.findById(docl.getUserDef1()).orElse(null).getDescription() : "" );
					vehiclePurchaseOrderVO.setTrim(docl.getUserDef2() != null ? trimCodesDAO.findByTrimCode(docl.getUserDef2()).getTrimDescription() : "");						
					vehiclePurchaseOrderVO.setRequestedDate(MALUtilities.getNullSafeDatetoString(docl.getRecDatePromised()));
					break;
				}
			}
		}
		
		vehiclePurchaseOrderVO.setVehicleDescription(model.getModelDescription());
		vehiclePurchaseOrderVO.setModelYear(Integer.parseInt(model.getModelMarkYear().getModelMarkYearDesc()));
		vehiclePurchaseOrderVO.setMakeDesc(model.getMake().getMakeDesc());
		vehiclePurchaseOrderVO.setMakId(model.getMake().getMakId());
		
		List<DocSupplier> docSupplierList = doc.getDocSuppliers();
		DeliveryShipDetail delivShipDetail = deliveryShipDetailDAO.findByDocId(mainDocId);

		if(docSupplierList != null){
			for (DocSupplier docSupplier : docSupplierList) {
				if(docSupplier.getWorkshopCapabilityCode() != null && docSupplier.getWorkshopCapabilityCode().equals(WORKSHOP_CAPABILITY_ORDERING) ){
					vehiclePurchaseOrderVO.setOrderingDealerCode(docSupplier.getSupplier().getSupplierCode());	
					vehiclePurchaseOrderVO.setOrderingDealerName(docSupplier.getSupplier().getSupplierName());
					vehiclePurchaseOrderVO.setOrderingDealerId(docSupplier.getSupplier().getSupId());
					
					List<Object[]> vendorContactsList;
					try {
						vendorContactsList = externalAccountDAO.getVendorContactDetailsList(1L, "S", docSupplier.getSupplier().getEaAccountCode());
						if(vendorContactsList != null && vendorContactsList.size() > 0) {
							Object[] vendorContactDetails = vendorContactsList.get(0);
							vehiclePurchaseOrderVO.setOrderingDealerEmail((String) (vendorContactDetails[3] != null ? vendorContactDetails[3] : vendorContactDetails[4]));
						}
					} catch (Exception e) {
						throw new MalException("Error while getting Vendor/Supplier Contact Info for account " + docSupplier.getSupplier().getEaAccountCode(), e);
					}
				}
				if(docSupplier.getWorkshopCapabilityCode() != null && docSupplier.getWorkshopCapabilityCode().equals(WORKSHOP_CAPABILITY_DELIVERING) ){
					vehiclePurchaseOrderVO.setDeliveringDealerCode(docSupplier.getSupplier().getSupplierCode());	
					vehiclePurchaseOrderVO.setDeliveringDealerName(docSupplier.getSupplier().getSupplierName());	
					vehiclePurchaseOrderVO.setDeliveringDealerId(docSupplier.getSupplier().getSupId());
					
					List<Object[]> contactsList;
					if(VehicleOrderType.LOCATE.getCode().equals(vehiclePurchaseOrderVO.getOrderType())){
						try {
							contactsList = externalAccountDAO.getDealerContactDetailsList(1L, "S", docSupplier.getSupplier().getEaAccountCode());
							if(contactsList != null && contactsList.size() > 0) {
								Object[] vendorContactDetails = contactsList.get(0);
								vehiclePurchaseOrderVO.setDeliveringDealerEmail((String) (vendorContactDetails[3] != null ? vendorContactDetails[3] : vendorContactDetails[4]));
							}
						} catch (Exception e) {
							throw new MalException("Error while fetching contact info for account " + docSupplier.getSupplier().getEaAccountCode(), e);
						}
					}
					if(MALUtilities.isEmpty(vehiclePurchaseOrderVO.getDeliveringDealerEmail())){
						try {
							contactsList = externalAccountDAO.getVendorContactDetailsList(1L, "S", docSupplier.getSupplier().getEaAccountCode());
							if(contactsList != null && contactsList.size() > 0) {
								Object[] vendorContactDetails = contactsList.get(0);

								vehiclePurchaseOrderVO.setDeliveringDealerEmail((String) (vendorContactDetails[3] != null ? vendorContactDetails[3] : vendorContactDetails[4]));
							}
						} catch (Exception e) {
							throw new MalException("Error while getting Vendor/Supplier Contact Info for account " + docSupplier.getSupplier().getEaAccountCode(), e);

						}
					}
				}				
			}
		}
		
		DocPropertyValue docPropertyValue = docPropertyValueDAO.findByNameDocId(DocPropertyEnum.DEALER_PRICE.getCode(), mainDocId);
		if(!MALUtilities.isEmpty(docPropertyValue)){
			vehiclePurchaseOrderVO.setTotalCost(docPropertyValue.getPropertyValue());
		}
		
		docPropertyValue = docPropertyValueDAO.findByNameDocId(DocPropertyEnum.ACQUISITION_TYPE.getCode(), mainDocId);
		if(!MALUtilities.isEmpty(docPropertyValue)){
			vehiclePurchaseOrderVO.setAcquisitionType(docPropertyValue.getPropertyValue());
		}
		
		docPropertyValue = docPropertyValueDAO.findByNameDocId(DocPropertyEnum.PO_VIN.getCode(), mainDocId);
		if(!MALUtilities.isEmpty(docPropertyValue) && ! doc.getDocStatus().equalsIgnoreCase(DocumentStatus.PURCHASE_ORDER_STATUS_RELEASED.getCode())){//read from doc Property  only if PO is not yet released.. Once released always get from FMS
			vehiclePurchaseOrderVO.setVin(docPropertyValue.getPropertyValue());
		}
		
		docPropertyValue = docPropertyValueDAO.findByNameDocId(DocPropertyEnum.PO_ORDERING_LEAD_TIME.getCode(), mainDocId);
		if(!MALUtilities.isEmpty(docPropertyValue)){
			vehiclePurchaseOrderVO.setOrderingLeadTime(Long.valueOf(docPropertyValue.getPropertyValue()));
		}else{			 
			if(MALUtilities.getNullSafeString(vehiclePurchaseOrderVO.getOrderType()).equals(VehicleOrderType.FACTORY.getCode()) 
					&& MALUtilities.isEmptyString(vehiclePurchaseOrderVO.getAcquisitionType()) ){			
					vehiclePurchaseOrderVO.setOrderingLeadTime(model.getStdDeliveryLeadTime());			
			}
		}
		
		if(MALUtilities.getNullSafeString(vehiclePurchaseOrderVO.getOrderType()).equals(VehicleOrderType.FACTORY.getCode())){			
			vehiclePurchaseOrderVO.setStandardDeliveryLeadTime(model.getStdDeliveryLeadTime() == null ? "" : String.valueOf(model.getStdDeliveryLeadTime()));			
		}
		
		vehiclePurchaseOrderVO.setDocId(doc.getDocId());
		vehiclePurchaseOrderVO.setPoNumber(doc.getDocNo() + "/" + doc.getRevNo());
		vehiclePurchaseOrderVO.setFactoryOrderNumber(doc.getExternalRefNo());
		vehiclePurchaseOrderVO.setStockYN(doc.getSourceCode().equals(DocumentSourceEnum.FLEET_QUOTE.getCode()) ? "N" : "Y");
		vehiclePurchaseOrderVO.setPoStatus(doc.getDocStatus());
		
		List<Doc> thdPtyDocList = docLinkDAO.findThirdPartyDocsByParentDocId(mainDocId);
		if(thdPtyDocList != null){
			vehiclePurchaseOrderVO.setNumOfThdPtyPos((long) thdPtyDocList.size());
		}
		if(doc.getDocStatus().equalsIgnoreCase(DocumentStatus.PURCHASE_ORDER_STATUS_RELEASED.getCode())){
			vehiclePurchaseOrderVO.setReleasedDate(doc.getPostedDate());
		}
		
		boolean updateDriverAddress = false;
		
		if(doc.getDocStatus().equalsIgnoreCase(DocumentStatus.PURCHASE_ORDER_STATUS_OPEN.getCode()) && qmdId  != null){
			QuotationModel qmd = quotationModelDAO.findById(qmdId).orElse(null);
			DriverAddress driverAdd = driverAddressDAO.findByDrvIdAndType(qmd.getQuotation().getDrvDrvId(), GARAGED);
			if(!MALUtilities.isEmpty(driverAdd) && !MALUtilities.isEmpty(delivShipDetail)){
				if(!MALUtilities.getNullSafeString(driverAdd.getAddressLine1()).equalsIgnoreCase(MALUtilities.getNullSafeString(delivShipDetail.getDelAddressLine1()))){
					updateDriverAddress = true;
				}
				if(!updateDriverAddress && !MALUtilities.getNullSafeString(driverAdd.getAddressLine2()).equalsIgnoreCase(MALUtilities.getNullSafeString(delivShipDetail.getDelAddressLine2()))){
					updateDriverAddress = true;
				}
				if(!updateDriverAddress && !MALUtilities.getNullSafeString(driverAdd.getTownCityCode().getTownDescription()).equalsIgnoreCase(MALUtilities.getNullSafeString(delivShipDetail.getTownCity()))){
					updateDriverAddress = true;
				}
				if(!updateDriverAddress && !MALUtilities.getNullSafeString(driverAdd.getRegionAbbreviation()).equalsIgnoreCase(MALUtilities.getNullSafeString(delivShipDetail.getRegion()))){
					updateDriverAddress = true;
				}
				if(!updateDriverAddress && !MALUtilities.getNullSafeString(driverAdd.getPostcode()).equalsIgnoreCase(MALUtilities.getNullSafeString(delivShipDetail.getPostalCode()))){
					updateDriverAddress = true;
				}
				if(!updateDriverAddress && !MALUtilities.getNullSafeString(driverAdd.getCountry().getCountryCode()).equalsIgnoreCase(MALUtilities.getNullSafeString(delivShipDetail.getCountry()))){
					updateDriverAddress = true;
				}
				if(updateDriverAddress){
					delivShipDetail.setDelAddressLine1(driverAdd.getAddressLine1());
					delivShipDetail.setDelAddressLine2(driverAdd.getAddressLine2());
					delivShipDetail.setTownCity(driverAdd.getTownCityCode().getTownDescription());
					delivShipDetail.setRegion(driverAdd.getRegionAbbreviation());
					delivShipDetail.setPostalCode(driverAdd.getPostcode());
					delivShipDetail.setCountry(driverAdd.getCountry().getCountryCode());
					
					deliveryShipDetailDAO.saveAndFlush(delivShipDetail);
				}
			}
		}
		
		vehiclePurchaseOrderVO.setDriverAddressUpdated(updateDriverAddress);
		
		if(!MALUtilities.isEmpty(delivShipDetail)){
			vehiclePurchaseOrderVO.setDeliveryAddressLine1(delivShipDetail.getDelAddressLine1());
			vehiclePurchaseOrderVO.setDeliveryAddressLine2(delivShipDetail.getDelAddressLine2());
			vehiclePurchaseOrderVO.setTownCity(delivShipDetail.getTownCity());
			vehiclePurchaseOrderVO.setRegion(delivShipDetail.getRegion());
			vehiclePurchaseOrderVO.setPostalCode(delivShipDetail.getPostalCode());
			vehiclePurchaseOrderVO.setCountry(delivShipDetail.getCountry());
		}
		
		if(doc.getDocStatus().equalsIgnoreCase(DocumentStatus.PURCHASE_ORDER_STATUS_RELEASED.getCode())){
			List<Object> objList = null;
			
			String optionalAccessories = docDAO.getOptionalAccessories(vehiclePurchaseOrderVO.getDocId());
			ArrayList<String> strList = new ArrayList<String>();
			if(optionalAccessories != null){
				for(String str : Arrays.asList(optionalAccessories.split("\\r?\\n"))){
					strList.add(str.replaceAll("^\\s+", "#:#"));
				}
			}
			vehiclePurchaseOrderVO.setOptionalAccessories(strList);
			
			objList = purchaseOrderReportDAO.getTypedInstalledAccessoriesPO(vehiclePurchaseOrderVO.getDocId(),
					ACCESORY_TYPE_DEALER, PORT_INSTALLED_CATEGORY_CODE);
			if (objList != null) {
				vehiclePurchaseOrderVO.setPortInstalledAccessories(new ArrayList<String>());
				for (Object access : objList) {
					vehiclePurchaseOrderVO.getPortInstalledAccessories().add((String) access);
				}
			}
			
			List<String> dealerInstalledList = new ArrayList<String>();
			List<Object> objPostProductionList = purchaseOrderReportDAO.getTypedInstalledAccessoriesPO(vehiclePurchaseOrderVO.getDocId(), ACCESORY_TYPE_DEALER,
					POST_PRODUCTION_CATEGORY_CODE);

			if (objList != null) {
				vehiclePurchaseOrderVO.setDealerInstalledAccessories(new ArrayList<String>());
				for (Object access : objPostProductionList) {
					dealerInstalledList.add((String) access);
				}
			}
			
			List<Object> objDealerInstalledList = purchaseOrderReportDAO.getTypedInstalledAccessoriesPO(vehiclePurchaseOrderVO.getDocId(),
					ACCESORY_TYPE_DEALER, DEALER_INSTALLED_CATEGORY_CODE);
			if (objDealerInstalledList != null) {
				for (Object access : objDealerInstalledList) {
					dealerInstalledList.add((String) access);
				}
			}
			
			String mainPoLogistics = purchaseOrderReportDAO.getDocNarratives(mainDocId, "SPEC_INSTR");
			vehiclePurchaseOrderVO.setPoLogisticNotes(mainPoLogistics == null ? "" : mainPoLogistics);
			if (qmdId  == null) {
				String docLLogistics = purchaseOrderReportDAO.getDoclNarratives(mainDocId, 1L);
				if (docLLogistics != null && docLLogistics.trim().length() > 0) {
					if (vehiclePurchaseOrderVO.getPoLogisticNotes() != null && vehiclePurchaseOrderVO.getPoLogisticNotes().trim().length() > 0) {
						vehiclePurchaseOrderVO.setPoLogisticNotes(vehiclePurchaseOrderVO.getPoLogisticNotes() + "\n");
					}
					vehiclePurchaseOrderVO.setPoLogisticNotes( vehiclePurchaseOrderVO.getPoLogisticNotes() + docLLogistics);
				}
			}
			
			if(dealerInstalledList != null && dealerInstalledList.size() > 0){
				Collections.sort(dealerInstalledList);
				vehiclePurchaseOrderVO.getDealerInstalledAccessories().addAll(dealerInstalledList);
			}
			Collections.sort(vehiclePurchaseOrderVO.getPortInstalledAccessories());
			vehiclePurchaseOrderVO.setPowertrainInfo(getPowertrainByQmdorDocId(vehiclePurchaseOrderVO.getQmdId(), vehiclePurchaseOrderVO.getDocId()));
		}
		return vehiclePurchaseOrderVO;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public DbProcParamsVO releaseMainPurchaseOrder(Long docId, Long cId, String userId)throws MalBusinessException, MalException {
	
		return purchaseOrderReleaseQueueDAO.releaseMainPurchaseOrder(docId, cId, userId);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public DbProcParamsVO confirmPurchaseOrder(Long docId, String userId, Date confirmDate ) throws MalBusinessException, MalException{
		return purchaseOrderReleaseQueueDAO.confirmPurchaseOrder(docId ,userId,confirmDate);
	} 
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public DbProcParamsVO releaseThirdPartyPurchaseOrders(Long docId, String userId) throws MalBusinessException, MalException{
		return purchaseOrderReleaseQueueDAO.releaseThirdPartyPurchaseOrders(docId , userId);
	}
	
	@Override	
	public void renderPurchaseOrderReleaseDocuments(Long doc_id, boolean isStock, boolean isEmail){
		
	}
	
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public DbProcParamsVO saveOrUpdatePO(VehiclePurchaseOrderVO maintainPoVO)	throws MalBusinessException, MalException {
		Doc doc = docDAO.findDocWithPropertiesByDocId(maintainPoVO.getDocId());
		if(maintainPoVO.isOrderingDealerChanged()){
				
				Supplier ord = supplierDAO.findById(maintainPoVO.getOrderingDealerId()).orElse(null);
				doc.setCId(ord.getEaCId().longValue());
				doc.setAccountType(ord.getEaAccountType());
				doc.setAccountCode(ord.getEaAccountCode());				
				doc.getDocSupplier(WORKSHOP_CAPABILITY_ORDERING).setSupplier(ord);
		}
		
		if(maintainPoVO.isDeliveringDealerChanged()){
			if (!MALUtilities.isEmpty(maintainPoVO.getDeliveringDealerCode())) {
				Supplier del = supplierDAO.findById(maintainPoVO.getDeliveringDealerId()).orElse(null);
				doc.setSubAccCId(del.getEaCId().longValue());
				doc.setSubAccType(del.getEaAccountType());
				doc.setSubAccCode(del.getEaAccountCode());
				DocSupplier deliveringDocSupplier = doc.getDocSupplier(WORKSHOP_CAPABILITY_DELIVERING);
				if(!MALUtilities.isEmpty(deliveringDocSupplier))
					doc.getDocSupplier(WORKSHOP_CAPABILITY_DELIVERING).setSupplier(del);
				else{
					deliveringDocSupplier = new DocSupplier();
					deliveringDocSupplier.setDoc(doc);
					deliveringDocSupplier.setSupplier(del);
					deliveringDocSupplier.setWorkshopCapabilityCode(WORKSHOP_CAPABILITY_DELIVERING);
					doc.getDocSuppliers().add(deliveringDocSupplier);
				}
			} else {
				doc.setSubAccCId(null);
				doc.setSubAccType(null);
				doc.setSubAccCode(null);
				DocSupplier deliveringDocSupplier = doc.getDocSupplier(WORKSHOP_CAPABILITY_DELIVERING);
				if(!MALUtilities.isEmpty(deliveringDocSupplier)){
					doc.getDocSuppliers().remove(deliveringDocSupplier);
				}
			}
		}
		
		
		doc.setExternalRefNo(maintainPoVO.getFactoryOrderNumber());
		doc.setDocPropertyValues(getUpdatedDocPropertyValuesList(maintainPoVO, doc));
		
		doc = docDAO.saveAndFlush(doc);
		if(DocumentStatus.PURCHASE_ORDER_STATUS_RELEASED.getCode().equals(maintainPoVO.getPoStatus()) && maintainPoVO.isVinChanged()){// if Fleet Master is already created
			
			FleetMaster fleetMaster = fleetMasterService.getFleetMasterByFmsId(maintainPoVO.getFmsId());
			fleetMaster.setVin(maintainPoVO.getVin());
			fleetMasterService.saveUpdateFleetMaster(fleetMaster);
			
		}
		
		DbProcParamsVO parameterVO = null;
		if(maintainPoVO.isOrderingDealerChanged()){
			parameterVO = purchaseOrderReleaseQueueDAO.peformPostOrderingDealerChangeUpdates(doc.getDocId(), maintainPoVO.getOrderingDealerId());
		}else{
			return new DbProcParamsVO();
		}
		return parameterVO;
	}
	
	/**
	 * Retrives all the 
	 * @param docId
	 * @return
	 */
	public List<Doc> getThirdPartyPurchaseOrders(Long docId) {
		List<Doc> thirdPartyPOs = new ArrayList<Doc>();
		thirdPartyPOs = docDAO.findThirdPartyPurchaseOrderByMainDocId(docId);	
		return thirdPartyPOs;
	}
	
	
	public List<Doc> getOpenThirdPartyPurchaseOrderWithAccessories(Long docId) {
		
		List<Doc> fianlThirdPartyPOList = new ArrayList<Doc>();			
		List<Doc> tpDocList =  getThirdPartyPurchaseOrders(docId);
		for (Doc doc : tpDocList) {
			if(doc.getDocStatus().equals(DocumentStatus.PURCHASE_ORDER_STATUS_OPEN.getCode())){
				for (Docl docl : doc.getDocls()) {
					if( docl.getUserDef4().equals("DEALER")){
						fianlThirdPartyPOList.add(doc);
						break;
					}
				}
			}
		}
		return fianlThirdPartyPOList;
	}
	
	public List<Doc> getThirdPartyPurchaseOrderWithAccessories(Long docId) {
	
		List<Doc> fianlThirdPartyPOList = new ArrayList<Doc>();			
		List<Doc> tpDocList =  getThirdPartyPurchaseOrders(docId);
		for (Doc doc : tpDocList) {
			for (Docl docl : doc.getDocls()) {
				if( docl.getUserDef4().equals("DEALER")){
					fianlThirdPartyPOList.add(doc);
					break;
				}
			}
		}
		return fianlThirdPartyPOList;
	}

	private List<DocPropertyValue> mergeDocPropertyValues(List<DocPropertyValue> dpvList, String propertyValue, String propertyName, Doc doc) {
		Boolean found = false;
		for(Iterator<DocPropertyValue> docPropertyValueItr = dpvList.iterator(); docPropertyValueItr.hasNext();) {
			DocPropertyValue docPropertyValue = docPropertyValueItr.next();
			if(docPropertyValue.getDocProperty().getName().equals(propertyName)) {
				found = true;
				if(!MALUtilities.isEmpty(propertyValue)){
					docPropertyValue.setPropertyValue(propertyValue);
				}else{
					docPropertyValueItr.remove();
				}
				break;
			}
		}
		if(!found && !MALUtilities.isEmpty(propertyValue)){
			DocPropertyValue dp = new DocPropertyValue();
			dp.setPropertyValue(propertyValue);
			dp.setDocProperty(docPropertyDAO.findByName(propertyName));
			dp.setDoc(doc);
			dpvList.add(dp);
		}
		return dpvList;
	}

	private List<DocPropertyValue> getUpdatedDocPropertyValuesList(VehiclePurchaseOrderVO maintainPoVO, Doc doc) {
		List<DocPropertyValue> dpvList = new ArrayList<DocPropertyValue>();
		
		dpvList = mergeDocPropertyValues(doc.getDocPropertyValues(), maintainPoVO.getTotalCost(), "DEALER_PRICE", doc);
		
		dpvList = mergeDocPropertyValues(doc.getDocPropertyValues(), maintainPoVO.getAcquisitionType(), "ACQUISITION_TYPE", doc);
		
		dpvList = mergeDocPropertyValues(doc.getDocPropertyValues(), !MALUtilities.isEmpty(maintainPoVO.getOrderingLeadTime()) ? maintainPoVO.getOrderingLeadTime().toString() : null, "PO_ORDERING_LEAD_TIME", doc);
		//Do not save vin in Doc Property table if PO is released(Fleet master is created) rather than It should get updated in fleet masters it self.
		if(! DocumentStatus.PURCHASE_ORDER_STATUS_RELEASED.getCode().equals(maintainPoVO.getPoStatus())){
			dpvList = mergeDocPropertyValues(doc.getDocPropertyValues(), maintainPoVO.getVin(), "PO_VIN", doc);
		}	
		return dpvList;
	}

	@Override
	public String getBailmentDealerCode(VehiclePurchaseOrderVO maintainPoVO) {
		
		Doc doc = docDAO.findById(maintainPoVO.getDocId()).orElse(null);
		Long suppId = null;
		String makeCode = null;
		
		Supplier supplier =  supplierDAO.findById(maintainPoVO.getOrderingDealerId()).orElse(null);
		
		if(!MALUtilities.isEmpty(supplier)){
			suppId = supplier.getSupId();
		}else{
			return null;
		}

		for(Docl docl : doc.getDocls()){
			if(docl.getUserDef4().equals("MODEL")){
				if(!MALUtilities.isEmpty(docl.getGenericExtId())){
					Model model = modelDAO.findById(docl.getGenericExtId()).orElse(null);
					if(!MALUtilities.isEmpty(model)){
						makeCode = model.getMake().getMakeCode();
						break;
					}
				}
			}
		}
		if(!MALUtilities.isEmpty(makeCode) && !MALUtilities.isEmpty(maintainPoVO.getCountry())){
			MakeCountrySuppliers makeCountrySuppliers = makeCountrySuppliersDAO.findByMakeCountrySupplier(makeCode, maintainPoVO.getCountry(),suppId);
			return !MALUtilities.isEmpty(makeCountrySuppliers) ? makeCountrySuppliers.getBailmentDealerCode() : null;
		}
	return null;
	}
	
	@Override
	public int getNonAutoDataDealerAccWithoutVendorCount(Long qmdId) {
		
		String autodataAccCode = willowConfigService.getConfigValue(WillowConfigService.AUTODATA_DEALER_ACCESSORIES_CATEGORIES);
		return purchaseOrderReleaseQueueDAO.getNonAutoDataDealerAccWithoutVendorCount(qmdId, autodataAccCode);
	}

	@Override
	public boolean isOrdeDelDealerExistInDocSupplier(Long docId) {

		Doc doc = docDAO.findById(docId).orElse(null);
		DocSupplier docSupOrdering = doc.getDocSupplier(WORKSHOP_CAPABILITY_ORDERING);
		DocSupplier docSupDelivering = doc.getDocSupplier(WORKSHOP_CAPABILITY_DELIVERING);
		
		if(MALUtilities.isEmpty(docSupOrdering)){
			return false;
		}
		if(MALUtilities.isEmpty(docSupDelivering) && !MALUtilities.isEmpty(doc.getSubAccCode())){
			return false;
		}
		
		return true;
	}
	
	/**
	 * Evaluates the PO lines to determine whether it is soley for
	 * capital element(s).
	 * @param docId Long The DOC.DOC_ID
	 */
	@Override
	@Transactional(readOnly = true)
	public boolean isCapitalElementOnlyPurchaseOrder(Long docId) {
		boolean isPOForCapitalElement = true;
		Doc thirdPartyPO = documentService.getDocumentByDocId(docId);
		
		for(Docl line : thirdPartyPO.getDocls()) {
		    if(MALUtilities.isEmpty(line.getUserDef4()) || !line.getUserDef4().equals("CAPITAL")){
		    	isPOForCapitalElement = false;
		    	break;
		    } 	
		}
		
		return isPOForCapitalElement;
	}
	
	@Override
	public List<ExternalAccount> getPreferredVendorByQmdId(Long qmdId){
		return purchaseOrderReleaseQueueDAO.getPreferredVendorByQmdId(qmdId);
	}
	
	@Override
	public List<Supplier> getPreferredSupplierListByQmdId(Long qmdId) {
		
		return purchaseOrderReleaseQueueDAO.getPreferredSupplierByQmdId(qmdId); 
	}
	
	@Override
	public DocSupplier getOrderingDealerByDocId(Long docId) {
		Doc doc = docDAO.findById(docId).orElse(null);
		DocSupplier docSupplier = new DocSupplier();
		Hibernate.initialize(doc.getDocSuppliers());
		docSupplier = doc.getDocSupplier(WORKSHOP_CAPABILITY_ORDERING);
		return docSupplier;
	}

	
	private Make getMakeByDocId(Long docId) {
		
		Doc doc = docDAO.findById(docId).orElse(null);
		Make make =  null; 
		
		for(Docl docl : doc.getDocls()){
			if(docl.getUserDef4().equals("MODEL")){
				if(!MALUtilities.isEmpty(docl.getGenericExtId())){
					Model model = modelDAO.findById(docl.getGenericExtId()).orElse(null);
					if(!MALUtilities.isEmpty(model)){
						make = model.getMake();
						break;
					}
				}
			}
		}
		return make;
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public DbProcParamsVO updatePreferredVendor(Long docId, Long orderingSupId) throws MalBusinessException, MalException {
		
		Supplier orderingDealer = supplierDAO.findById(orderingSupId).orElse(null);
		Doc doc  = docDAO.findById(docId).orElse(null);
		doc.setCId(orderingDealer.getEaCId());
		doc.setAccountType(orderingDealer.getEaAccountType());
		doc.setAccountCode(orderingDealer.getEaAccountCode());
		doc.getDocSupplier(WORKSHOP_CAPABILITY_ORDERING).setSupplier(orderingDealer);//We will always have ordering doc supplier entry
		
		docDAO.save(doc);
		docDAO.flush();

		return purchaseOrderReleaseQueueDAO.peformPostOrderingDealerChangeUpdates(docId, orderingDealer.getSupId());
	}

	@Override
	public Supplier getDefaultSupplier(Long qmdId, Long docId) {
		
		QuotationModel qmd = quotationModelDAO.findById(qmdId).orElse(null);
		DriverAddress driverAdd = driverAddressDAO.findByDrvIdAndType(qmd.getQuotation().getDrvDrvId(), GARAGED);
		String makeCode = getMakeByDocId(docId).getMakeCode();
		
		Supplier defaultSupplier = null;
		
		if(!MALUtilities.isEmpty(makeCode) && !MALUtilities.isEmpty(driverAdd)){
			MakeCountrySuppliers makeCountrySuppliers = makeCountrySuppliersDAO.findByMakeAndCountry(makeCode, driverAdd.getCountry().getCountryCode());
			if(!MALUtilities.isEmpty(makeCountrySuppliers)){
				defaultSupplier = supplierDAO.findById(makeCountrySuppliers.getSupId()).orElse(null);
			}
		}
		return defaultSupplier;
	}

	@Override
	
	@Transactional(readOnly = true)
	@SuppressWarnings("unused")
	public List<ThirdPartyPoQueueV> findThirdPartyPoQueueList() throws Exception { 
		
		Map<Long, List<VendorInfoVO>> vendorNameMap = new HashMap<Long, List<VendorInfoVO>>();

		Long qmdId = null;
		String vendorName = null;
		String vendorCode = null;
		Long vendorContext = null;
		Long mainThpyDocId = null;
		BigDecimal leadTime = null;
		List<Object[]> poVendorsList = thirdPartyPoQueueDAO.getThirdPartyUpfitList();
		VendorInfoVO vendorInfoVO = null;
		List<VendorInfoVO> vendorInfoVOs = null;
		List<DocumentStatus> upfitPOStatuses;
		QuotationModel qmd;		
		String taskCompleted = null;		
		String orderType = null;	
		boolean isStock;	
		boolean isLinked;
		Long sequenceNo;		
		
		for(Object[] record : poVendorsList) {

			qmdId = ((BigDecimal) record[0]).longValue();
			vendorName = (String) record[1];
			vendorCode = (String) record[2];
			vendorContext = ((BigDecimal) record[3]).longValue();
			mainThpyDocId = record[4] != null ? ((BigDecimal) record[4]).longValue() : null;
			leadTime = ((BigDecimal) record[5]);
			orderType = (String) record[6];	
			taskCompleted = record[7] != null ? (String) record[7] : null;
			isStock = MALUtilities.convertYNToBoolean((String)record[8]);
			isLinked = MALUtilities.convertYNToBoolean((String)record[9]);
			sequenceNo = ((BigDecimal) record[10]).longValue();					
			
			vendorInfoVO = new VendorInfoVO(vendorName, vendorCode, vendorContext, qmdId, mainThpyDocId, String.valueOf(leadTime.longValue()));
			vendorInfoVO.setLinked(isLinked);
			vendorInfoVO.setSequenceNo(sequenceNo);			
				
			if(vendorNameMap.containsKey(qmdId)) {
				vendorNameMap.get(qmdId).add(vendorInfoVO);
			} else {
				List<VendorInfoVO> vendorNameInfoList = new ArrayList<VendorInfoVO>();
				vendorNameInfoList.add(vendorInfoVO);
				vendorNameMap.put(qmdId, vendorNameInfoList);
			}
		}
		
		List<ThirdPartyPoQueueV> thirdPartyPoList = thirdPartyPoQueueDAO.findAll();
		ThirdPartyPoQueueV thirdPartyPoQueueV = null;
		for(Iterator<ThirdPartyPoQueueV> thirdPartyPoQueueItr = thirdPartyPoList.iterator(); thirdPartyPoQueueItr.hasNext();){
			thirdPartyPoQueueV = thirdPartyPoQueueItr.next();
			
			//TODO This line assumes that there will always be a 3rd party po for each vendor. This 
			//     is not the case for stock quote accessory POs created prior to OTD-24 release.
			//     There would only be one PO, which we will have to look at the QDA to pull out the vendors
			thirdPartyPoQueueV.setVendorInfoVOList(vendorNameMap.get(thirdPartyPoQueueV.getQmdId()));
			
			//TODO Remove, as there should be at least one upfit vendor. I believe there is bad data in DEV2 which warrants this check
			if(!MALUtilities.isEmpty(thirdPartyPoQueueV.getVendorInfoVOList()) && thirdPartyPoQueueV.getVendorInfoVOList().size() > 0) {
				Collections.sort(thirdPartyPoQueueV.getVendorInfoVOList(), new VendorInfoVOComparator());					
			}
		}
		
		return thirdPartyPoList;
	}

	@Override
	public Map<String, List<String>> getDealerAccessoriesWithVendorQuoteNumber(Long qmdId, Long thpyDocId, String accountCode) {
		
		List<Object[]> accessoriesList = null;
		accessoriesList = dealerAccessoryDAO.getStockQuotationDealerAccessoryByQmdIdDocIdAndAccountCode(qmdId, thpyDocId, accountCode);
		
		Map<String, List<String>> vendorQuoteNoAccessories = new HashMap<String, List<String>>();
		List<String> accessoryDescList = null;
		String accessoryDesc;
		String vendorQuoteNumber;
		for(Object[] record : accessoriesList) {
			vendorQuoteNumber = record[0] != null ? (String) record[0] : "";
			accessoryDesc = (String) record[1];
			if(vendorQuoteNoAccessories.containsKey(vendorQuoteNumber)) {
				vendorQuoteNoAccessories.get(vendorQuoteNumber).add(accessoryDesc);
			} else {
				accessoryDescList = new ArrayList<String>();
				accessoryDescList.add(accessoryDesc);
				vendorQuoteNoAccessories.put(vendorQuoteNumber, accessoryDescList);
			}
		}

		return vendorQuoteNoAccessories;
	}
	


	public void archiveVehicleOrderSummary(Long mainPODocId) throws MalBusinessException, MalException {
		POArchieveTask task = new POArchieveTask();
		task.setTaskId(mainPODocId);
		task.setDocId(mainPODocId);
		task.setDocNameEnumType(DocumentNameEnum.VEHICLE_ORDER_SUMMARY);
		
		requestArchivePurchaseOrder(task);
	}
	
	public void archiveClientOrderConfirmation(Long mainPODocId) throws MalBusinessException, MalException{
		POArchieveTask task = new POArchieveTask();
		task.setTaskId(mainPODocId);
		task.setDocId(mainPODocId);
		task.setDocNameEnumType(DocumentNameEnum.CLIENT_ORDER_CONFIRMATION);
		
		requestArchivePurchaseOrder(task);
	}
	
	public void archiveMainPurchaseOrderDoc(Long mainPODocId, String stockYn) throws MalBusinessException, MalException{
		POArchieveTask task = new POArchieveTask();
		task.setTaskId(mainPODocId);
		task.setDocId(mainPODocId);
		task.setStockYn(stockYn);
		task.setDocNameEnumType(DocumentNameEnum.MAIN_PURCHASE_ORDER);
		
		requestArchivePurchaseOrder(task);
	}	
	
	public void archiveCourtesyDeliveryInstructionDoc(Long mainPODocId) throws MalBusinessException, MalException{
		POArchieveTask task = new POArchieveTask();
		task.setTaskId(mainPODocId);
		task.setDocId(mainPODocId);
		task.setDocNameEnumType(DocumentNameEnum.COURTESY_DELIVERY_INSTRUCTION);
		
		requestArchivePurchaseOrder(task);
	}
	
	public void archiveThirdPartyPurchaseOrder(Long tpDocId, String stockYn) throws MalBusinessException, MalException{		
		POArchieveTask task = new POArchieveTask();
		task.setTaskId(tpDocId);
		task.setDocId(tpDocId);
		task.setStockYn(stockYn);
		task.setDocNameEnumType(DocumentNameEnum.THIRD_PARTY_PURCHASE_ORDER);
		
		requestArchivePurchaseOrder(task);			
	}
	
	public void requestArchivePurchaseOrder(POArchieveTask archieveTask) throws MalBusinessException, MalException{
		
		try {
			
			logger.info("Received requested to archive PO  doc "+archieveTask.getDocId() +" : doc Type:"+ archieveTask.getDocNameEnumType().getName());	
			
			String taskProcessorWebSvcURL = willowConfigService.getConfigValue("TASK_PROCSR_WEBSVC_URL");
			
			if(taskProcessorWebSvcURL == null ){				
					throw new MalBusinessException("Task Processor WebSvc URL Willow Config is not configured" );
			}
			TaskResponse result = new RestTemplate().postForObject( taskProcessorWebSvcURL+ "/tasks/create/pOArchieveTask", archieveTask, TaskResponse.class);
		
			logger.info("Successfully requested to archive PO  doc "+archieveTask.getDocId() +" : doc Type:"+archieveTask.getDocNameEnumType().getName() +" : requestId :"+ result.getRequestId());
		
		} catch (Exception e) {
			logger.error(e ,"Unable to submit Onbase archieve request : Error :"+e.getMessage());
			throw new MalException("Unable to submit Onbase archieve request: Error :"+e.getMessage());
		}
	}
	
	
	@Override
	public void process(POArchieveTask archieveTask) throws MalException, MalBusinessException {
				
		byte[] data = null;
		
		try {
			
			if(archieveTask.getDocNameEnumType().getName().equals(DocumentNameEnum.VEHICLE_ORDER_SUMMARY.getName())){
				
				data = JasperExportManager.exportReportToPdf(jasperReportService.getVehicleOrderSummaryReport(archieveTask.getDocId(), "N"));	
					
			} else if(archieveTask.getDocNameEnumType().getName().equals(DocumentNameEnum.MAIN_PURCHASE_ORDER.getName())){
				
				data = JasperExportManager.exportReportToPdf(jasperReportService.getMainPurchaseOrderReport(archieveTask.getDocId(), archieveTask.getStockYn()));
					
			}else if(archieveTask.getDocNameEnumType().getName().equals(DocumentNameEnum.THIRD_PARTY_PURCHASE_ORDER.getName())){
				
				data = JasperExportManager.exportReportToPdf(jasperReportService.getThirdPartyPurchaseOrderReport(archieveTask.getDocId(), archieveTask.getStockYn()));
				
			}else if(archieveTask.getDocNameEnumType().getName().equals(DocumentNameEnum.CLIENT_ORDER_CONFIRMATION.getName())){		
				
				data = JasperExportManager.exportReportToPdf(jasperReportService.getClientOrderConfirmationReport(archieveTask.getDocId()));
						
			}else if(archieveTask.getDocNameEnumType().getName().equals(DocumentNameEnum.COURTESY_DELIVERY_INSTRUCTION.getName())){
				
				data = JasperExportManager.exportReportToPdf(jasperReportService.getCourtesyDeliveryInstructionReport(archieveTask.getDocId()));
				
			}
		
		} catch (Exception e) {				
			logger.error(e ,"Unable to generate document while PO archieve process");
			throw new MalException("Unable to generate document while PO archieve process :Error:"+e.getMessage());
		}
		
		archivePurchaseOrderDoc(archieveTask.getDocId(), data, archieveTask.getDocNameEnumType());
		
	}
	
	public void  archivePurchaseOrderDoc(Long docId, byte[] archivalData, DocumentNameEnum docNameEnum) throws MalBusinessException, MalException{
		
		Doc doc  = docDAO.findById(docId).orElse(null);
		FleetMaster fleetMaster  = fleetMasterService.getFleetMasterByDocId(docId);
		ExternalAccount externalAccount  =  null;
		String vin  = fleetMaster.getVin() == null ? "" : fleetMaster.getVin();
		if(doc.getGenericExtId() != null){		
			externalAccount  = quotationModelDAO.findById(doc.getGenericExtId()).orElse(null).getQuotation().getExternalAccount();
		}
			
		Long pkId = onbaseUploadedDocsDAO.getNextPK();
		
		List<OnbaseKeywordVO>  keywordVOList  =  null;
		if(docNameEnum.getName().equals(DocumentNameEnum.VEHICLE_ORDER_SUMMARY.getName())  || docNameEnum.getName().equals(DocumentNameEnum.MAIN_PURCHASE_ORDER.getName()) 
				|| docNameEnum.getName().equals(DocumentNameEnum.THIRD_PARTY_PURCHASE_ORDER.getName()) || docNameEnum.getName().equals(DocumentNameEnum.COURTESY_DELIVERY_INSTRUCTION.getName()) ){	
			keywordVOList =  onbaseArchivalService.getOnbaseKeywords(OnbaseDocTypeEnum.TYPE_PURCHASE_ORDER);
		}else if(docNameEnum.getName().equals(DocumentNameEnum.CLIENT_ORDER_CONFIRMATION.getName())){
			keywordVOList =  onbaseArchivalService.getOnbaseKeywords(OnbaseDocTypeEnum.TYPE_CLIENT_ORDER_CONFIRMATION);
		}
		
		
		for (OnbaseKeywordVO onbaseKeywordVO :keywordVOList) {				
			if(onbaseKeywordVO.getKeywordName().equals(OnbaseIndexEnum.UPLOAD_ID.getName())){
				onbaseKeywordVO.setKeywordValue(String.valueOf(pkId)) ;
			}else if(onbaseKeywordVO.getKeywordName().equals(OnbaseIndexEnum.FILE_EXT.getName())){
				onbaseKeywordVO.setKeywordValue("PDF");
			}else if(onbaseKeywordVO.getKeywordName().equals(OnbaseIndexEnum.UNIT_NO.getName())){
				onbaseKeywordVO.setKeywordValue(fleetMaster.getUnitNo() );				
			}else if(onbaseKeywordVO.getKeywordName().equals(OnbaseIndexEnum.VIN_LAST_8.getName())){	
				onbaseKeywordVO.setKeywordValue(vin.length() > 0  ?  vin.substring(vin.length()-8, vin.length()) : "" );
			}else if(onbaseKeywordVO.getKeywordName().equals(OnbaseIndexEnum.CUSTOMER_NO.getName())){
				onbaseKeywordVO.setKeywordValue(externalAccount != null ? externalAccount.getExternalAccountPK().getAccountCode() : "");
			}else if(onbaseKeywordVO.getKeywordName().equals(OnbaseIndexEnum.CUSTOMER_NAME.getName())){
				onbaseKeywordVO.setKeywordValue(externalAccount != null ?  externalAccount.getAccountName() : "");
			}else if(onbaseKeywordVO.getKeywordName().equals(OnbaseIndexEnum.VIN.getName())){
				onbaseKeywordVO.setKeywordValue(vin);
			}			
		}		
	
		OnbaseUploadedDocs onbaseUploadedDocs = new OnbaseUploadedDocs();
		
		onbaseUploadedDocs.setObdId(pkId);
		onbaseUploadedDocs.setObjectId(String.valueOf(docId));
		onbaseUploadedDocs.setObjectType("Doc");
		onbaseUploadedDocs.setIndexKey(onbaseArchivalService.generateOnbaseKeywordsIndex(keywordVOList));
		onbaseUploadedDocs.setObsoleteYn("N");
		onbaseUploadedDocs.setFileType("PDF");
		
		if(docNameEnum.getName().equals(DocumentNameEnum.VEHICLE_ORDER_SUMMARY.getName())){	
			
			onbaseUploadedDocs.setFileName(DocumentNameEnum.VEHICLE_ORDER_SUMMARY.getName());
			onbaseUploadedDocs.setDocType(OnbaseDocTypeEnum.TYPE_PURCHASE_ORDER.getValue());
			onbaseUploadedDocs.setDocSubType(OnbaseDocTypeEnum.TYPE_SUB_VEHICLE_ORDER_SUMMARY.getValue());
			
		} else if(docNameEnum.getName().equals(DocumentNameEnum.MAIN_PURCHASE_ORDER.getName())){
			
			onbaseUploadedDocs.setFileName(DocumentNameEnum.MAIN_PURCHASE_ORDER.getName());
			onbaseUploadedDocs.setDocType(OnbaseDocTypeEnum.TYPE_PURCHASE_ORDER.getValue());
			onbaseUploadedDocs.setDocSubType(OnbaseDocTypeEnum.TYPE_SUB_MAIN_PURCHASE_ORDER.getValue());
			
		}else if(docNameEnum.getName().equals(DocumentNameEnum.THIRD_PARTY_PURCHASE_ORDER.getName())){
			
			onbaseUploadedDocs.setFileName(DocumentNameEnum.THIRD_PARTY_PURCHASE_ORDER.getName());
			onbaseUploadedDocs.setDocType(OnbaseDocTypeEnum.TYPE_PURCHASE_ORDER.getValue());
			onbaseUploadedDocs.setDocSubType(OnbaseDocTypeEnum.TYPE_SUB_THIRD_PARTY_PURCHASE_ORDER.getValue());
			
		}else if(docNameEnum.getName().equals(DocumentNameEnum.CLIENT_ORDER_CONFIRMATION.getName())){
			
			onbaseUploadedDocs.setFileName(DocumentNameEnum.CLIENT_ORDER_CONFIRMATION.getName());
			onbaseUploadedDocs.setDocType(OnbaseDocTypeEnum.TYPE_CLIENT_ORDER_CONFIRMATION.getValue());
		}else if(docNameEnum.getName().equals(DocumentNameEnum.COURTESY_DELIVERY_INSTRUCTION.getName())){		
			
			onbaseUploadedDocs.setFileName(DocumentNameEnum.COURTESY_DELIVERY_INSTRUCTION.getName());
			onbaseUploadedDocs.setDocType(OnbaseDocTypeEnum.TYPE_PURCHASE_ORDER.getValue());
			onbaseUploadedDocs.setDocSubType(OnbaseDocTypeEnum.TYPE_SUB_COURTESY_DELIVERY_INSTRUCTION.getValue());
		}
		
		
		onbaseUploadedDocsDAO.save(onbaseUploadedDocs);
		onbaseUploadedDocs.setFileData(archivalData);			
		onbaseArchivalService.archiveDocumentInOnBase(onbaseUploadedDocs);
	
	}
	
	
	/**
	 * Total lead tme for the accessories on a specific purchase order
	 * @param Main or 3rd party PO DOC.DOC_ID
	 */
	public Long getPurchaseOrderLeadTimeByDocId(Long docId) {
		return orderProgressDAO.getPurchaseOrderLeadTimeByDocId(docId);
	}
	
	/**
	 * Total lead tme for the accessories on the Main PO and all of its 
	 * child POs. 
	 * @param Main PO DOC.DOC_ID
	 */	
	public Long getUnitLeadTimeByDocId(Long docId) {
		return orderProgressDAO.getUnitLeadTimeByDocId(docId);		
		
	}	
	
	/*
	 * The docNo should have always associated release PO otherwise it will return null data. 
	 */
	public byte[] getPurchaseOrderDocument(String docNo, DocumentNameEnum documentNameEnum) throws MalBusinessException, MalException{
		
		byte[] byteArray = null;		
		List<Doc> docList = docDAO.findByDocNo(docNo);
		for (Doc doc : docList) {
			if(doc.getDocStatus().equals(DocumentStatus.PURCHASE_ORDER_STATUS_RELEASED.getCode())){
				byteArray =  getPurchaseOrderDocument(doc.getDocId(),  documentNameEnum) ;
				break;
			}
		}
		
		return byteArray;
	}
	
	/*
	 * The doc status should be release otherwise it will return null data.We upload doc in onbase only if doc is released/confirm. 
	 */
	public byte[] getPurchaseOrderDocument(Long docId, DocumentNameEnum documentNameEnum) throws MalBusinessException, MalException{
		
		byte[] byteArray = null;
		OnbaseUploadedDocs onbaseUploadedDoc = null;
		
		List<OnbaseUploadedDocs> onbaseDocList = onbaseUploadedDocsDAO.getOnBaseUploadedDocsByObjectIdAndType(String.valueOf(docId), "Doc");
		for (OnbaseUploadedDocs uploadedDoc : onbaseDocList) {
			if(uploadedDoc.getDocSubType() != null && uploadedDoc.getDocSubType().equals(documentNameEnum.getName())){
				onbaseUploadedDoc = uploadedDoc;
				break;
			}
		}
		
		if(onbaseUploadedDoc != null){
			OnbaseKeywordVO onbaseKeywordVO =  new OnbaseKeywordVO(OnbaseIndexEnum.UPLOAD_ID.getName() ,String.valueOf(onbaseUploadedDoc.getObdId()));
			List<OnbaseKeywordVO> keyWordVOList = new ArrayList<OnbaseKeywordVO>();
			keyWordVOList.add(onbaseKeywordVO);
			try {
				byteArray = onbaseRetrievalService.getDoc(OnbaseDocTypeEnum.TYPE_PURCHASE_ORDER , keyWordVOList);
			} catch (Exception e) {
				logger.info("Not able to get docuement from onbase for "+documentNameEnum+" doc type. keyword: "+keyWordVOList + " : Api Error :"+e.getMessage());
			}
			
		}
		
		return byteArray;	
	}

	@Override
	public List<Doc> getMultipleMainPO(Long qmdId) {
		String docType = "PORDER";
		String sourceCode = "FLQUOTE";
		return docDAO.findMultipleQuoteMainPo(qmdId,docType,sourceCode);
	}

	@Override
	public List<String> getPowertrainByQmdorDocId(Long qmdId, Long docId) {
		List<Object> powertrainList = null;
		List<String> powertrainInfoList = new ArrayList<String>();
		if(qmdId != null){
			powertrainList = purchaseOrderReportDAO.getPowertrainInfo(qmdId);
		}else{
			powertrainList = purchaseOrderReportDAO.getPowertrainInfoForDoc(docId);
		}
		
		if(powertrainList != null && powertrainList.size() > 0){
			for(Object obj : powertrainList){
				powertrainInfoList.add((String)obj);
			}
		}
		return powertrainInfoList;
	}                             

	
		
}
