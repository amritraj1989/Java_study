package com.mikealbert.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.common.MalConstants;
import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.dao.ClientAgreementDAO;
import com.mikealbert.data.dao.ExternalAccountDAO;
import com.mikealbert.data.dao.FleetMasterDAO;
import com.mikealbert.data.dao.ProductDAO;
import com.mikealbert.data.dao.PurchaseOrderReportDAO;
import com.mikealbert.data.entity.ClientAgreement;
import com.mikealbert.data.entity.ContractLine;
import com.mikealbert.data.entity.Driver;
import com.mikealbert.data.entity.DriverAddress;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.Product;
import com.mikealbert.data.entity.QuotationElement;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.QuotationStepStructure;
import com.mikealbert.data.util.DisplayFormatHelper;
import com.mikealbert.data.vo.CnbvVO;
import com.mikealbert.data.vo.DeliveringDealerInfoVO;
import com.mikealbert.data.vo.OeConRevScheduleAVO;
import com.mikealbert.data.vo.QuotationStepStructureVO;
import com.mikealbert.data.vo.QuoteOEVO;
import com.mikealbert.data.vo.ServicesLeaseRateByPeriodVO;
import com.mikealbert.data.vo.VehicleInfoVO;
import com.mikealbert.data.vo.VehicleInformationVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;
import com.mikealbert.service.vo.QuoteCost;
import com.mikealbert.util.MALUtilities;

@Service("OeContractRevisionSchedAService")
@Transactional
public class OeContractRevisionSchedAServiceImpl implements OeContractRevisionSchedAService {
	
	@Resource QuotationService quotationService;	
	@Resource ContractService contractService;
	@Resource CostCenterService costCenterService;
	@Resource CapitalCostService capitalCostService;
	@Resource ProfitabilityService profitabilityService;
	@Resource RentalCalculationService rentalCalculationService;
	@Resource ProductDAO productDAO;
	@Resource ExternalAccountDAO externalAccountDAO;	
	@Resource FleetMasterDAO fleetMasterDAO;
	@Resource PurchaseOrderReportDAO purchaseOrderReportDAO;
	@Resource ClientAgreementDAO clientAgreementDAO;
	@Resource OpenEndService openEndService;
	
	private static final String RPT_NEW_LINE_CHAR = "<br/>";	
	private static final int DEPRECIATION_FACTOR_SCALE = 7;	
	private static final String OPEN_END_AGREEMENT_CODE = "Open End";
	private static final String REPORT_NAME = "OE_SCHEDULE_A";	
	
	private MalLogger logger = MalLoggerFactory.getLogger(this.getClass());	

	@Override
	@Transactional(readOnly = true)
	public List<OeConRevScheduleAVO> getOeConRevScheduleAReportVO(Long currentQmdId, Long revisionQmdId) throws MalException {
		List<OeConRevScheduleAVO> oeConRevScheduleAVOList = new ArrayList<OeConRevScheduleAVO>();
		
		oeConRevScheduleAVOList.add(populateOeConRevScheduleA(currentQmdId, revisionQmdId));		
		return oeConRevScheduleAVOList;
	}
	
	private OeConRevScheduleAVO populateOeConRevScheduleA(Long currentQmdId, Long revisionQmdId) throws MalException {
		OeConRevScheduleAVO oeConRevScheduleAVO = new OeConRevScheduleAVO();
		try {
			QuoteOEVO currentOEQuoteVO = loadCurrentQuote(currentQmdId);
			FleetMaster fleetMaster = fleetMasterDAO.findByUnitNo(currentOEQuoteVO.getUnitNo());
			ContractLine contractLine = contractService.getCurrentContractLine(fleetMaster, new Date());

			oeConRevScheduleAVO.setVehicleInformationVO(getVehicleInformation(fleetMaster, contractLine));
			QuoteOEVO revisionOEQuoteVO = loadRevisionQuote(revisionQmdId, currentOEQuoteVO, contractLine);
			oeConRevScheduleAVO.setQuoteOEVO(revisionOEQuoteVO);	
			
			Date inServiceDate = contractLine.getInServDate();
			if (contractLine.getInServDate() == null){// in service date is null on formal extensions so use original contract
				if (contractLine.getContract().getDescription().contains("Formal")) {
					ContractLine originalContractLine = contractService.getOriginalContractLine(fleetMaster);
					if(originalContractLine != null && originalContractLine.getInServDate() != null){
						inServiceDate = originalContractLine.getInServDate();
					}
				}
			}
			oeConRevScheduleAVO.setInServiceDate(inServiceDate);
			
			oeConRevScheduleAVO.setServicesLeaseRateVOList(getServicesLeaseRate(revisionOEQuoteVO));

			oeConRevScheduleAVO.getQuoteOEVO().getQuotationModel().setContractLine(contractLine);			
			oeConRevScheduleAVO.setAddress(openEndService.getClientAddress(revisionOEQuoteVO));
			oeConRevScheduleAVO.setDriverAddress(getDriverAddress(contractLine.getDriver()));
			
			oeConRevScheduleAVO.setQuotationStepStructureVOList(getRevStepsForUi(currentOEQuoteVO, revisionOEQuoteVO, contractLine));
			
			if (MALUtilities.isEmpty(contractLine.getDriver().getRechargeCode())) {
				oeConRevScheduleAVO.setRechargeCode(" ");	
			}else {
				oeConRevScheduleAVO.setRechargeCode(contractLine.getDriver().getRechargeCode());
			}
			
			oeConRevScheduleAVO.setSecurityDeposit(revisionOEQuoteVO.getQuotationModel().getQuotation().getExternalAccount().getRiskDepositAmt());
			oeConRevScheduleAVO.setExcessMileage(revisionOEQuoteVO.getQuotationModel().getQuotation().getQuotationProfile().getExcessMileage().getExcessMileName());
			Product product	= productDAO.findById(revisionOEQuoteVO.getQuotationModel().getQuotation().getQuotationProfile().getPrdProductCode()).orElse(null);
			oeConRevScheduleAVO.setLogo(openEndService.getLogoName(product));
			oeConRevScheduleAVO.setOeContractRevisionScheduleATitle("Open-End Contract Revision Schedule \"A\"");		
			oeConRevScheduleAVO.setInterestComponent("Indexed to the " + revisionOEQuoteVO.getInterestIndex() + " Rate plus an adjustment");	
/*
			ClientAgreement clientAgreement = clientAgreementDAO.findByContractAgreementAndClient(OPEN_END_AGREEMENT_CODE, 
					revisionOEQuoteVO.getQuotationModel().getQuotation().getExternalAccount().getExternalAccountPK().getcId(), 
					revisionOEQuoteVO.getQuotationModel().getQuotation().getExternalAccount().getExternalAccountPK().getAccountType(), 
					revisionOEQuoteVO.getQuotationModel().getQuotation().getExternalAccount().getExternalAccountPK().getAccountCode());
			oeConRevScheduleAVO.setMasterLeaseAgreementNo(clientAgreement.getAgreementNumber());
			*/
			
			oeConRevScheduleAVO.setReferenceQuote(getVehicleReferenceQuote(revisionOEQuoteVO));
			String summaryText = purchaseOrderReportDAO.getReportText(revisionOEQuoteVO.getQuotationModel().getQmdId(), REPORT_NAME, "2");			
			if (!MALUtilities.isEmptyString(summaryText)) {
				summaryText = summaryText 
								+ "<br/>" + "<p>" + purchaseOrderReportDAO.getReportText(revisionOEQuoteVO.getQuotationModel().getQmdId(), REPORT_NAME, "3")
								+ "<br/>" + "<p>" + purchaseOrderReportDAO.getReportText(revisionOEQuoteVO.getQuotationModel().getQmdId(), REPORT_NAME, "4");
			}
			oeConRevScheduleAVO.setSummaryText(summaryText);
			
		/*	
			oeConRevScheduleAVO.setSummaryText(
					"Reference is hereby made to Vehicle Quotation #" + oeConRevScheduleAVO.getReferenceQuote() + " (the \"VQ\") and Quote Reference #" + revisionOEQuoteVO.getQuote() + " and Open-End  <br/>" +
					"Commercial Motor Vehicle Master Lease Agreement #" + oeConRevScheduleAVO.getMasterLeaseAgreementNo() + " (as amended, modified or supplemented from time to time, the  <br/>" +
					"\"Master Lease Agreement\"), between Mike Albert, Ltd and Lessee, the terms of which are hereby incorporated by reference. <br/>" +  
					"<p>" +
					"The Vehicle described herein on this Schedule A of the Master Lease Agreement is incorporated into and made a part of  <br/>" +
					"the Master Lease Agreement. By accepting delivery of the vehicle described herein (either directly or through a designee,  <br/>" +
					"including any designated driver), Lessee hereby acknowledges and agrees (and no separate writing by the Lessee shall be necessary),  <br/>" +
					"that the terms of this Schedule A, the related VQ and the Master Lease Agreement and any other agreements identified on the VQ shall  <br/>" +
					"apply to the vehicle described by this Schedule A. <br/>" +
					"<p>" +
					"Any Specified Services/Items are provided by and the obligation of Mike Albert Leasing, Inc.");
					*/
			
			VehicleInfoVO vehicleInfoVO = populateVehicleInfoVO(revisionOEQuoteVO);
			oeConRevScheduleAVO.setVehicleInfoVO(vehicleInfoVO);
	
			DeliveringDealerInfoVO deliveringDealerInfoVO = populateDeliveringDealerInfoVO(revisionOEQuoteVO);
			oeConRevScheduleAVO.setDeliveringDealerInfoVO(deliveringDealerInfoVO);

		
			
		} catch (Exception ex) {
			logger.error(ex);
			throw new MalException("generic.error.occured.while", new String[] { "getting Open End Contract Revision Schedule A report data" }, ex);
		}
		
		return oeConRevScheduleAVO;
	}

	private String getVehicleReferenceQuote(QuoteOEVO revisionOEQuoteVO) throws MalBusinessException {
		Long referenceQmdId = quotationService.getOriginalQmdIdOnCurrentContract(revisionOEQuoteVO.getQuotationModel().getQmdId());
		QuotationModel referenceQuotationModel = quotationService.getQuotationModelWithCostAndAccessories(referenceQmdId);
		
		return Long.toString(referenceQuotationModel.getQuotation().getQuoId()) + "/"
				+ Long.toString(referenceQuotationModel.getQuoteNo()) + "/" + Long.toString(referenceQuotationModel.getRevisionNo());
	}

	private QuoteOEVO loadRevisionQuote(Long revisionQmdId, QuoteOEVO currentOEQuoteVO, ContractLine contractLine) {
		QuotationModel quotationModel;
		QuoteOEVO revisionOEQuoteVO = new QuoteOEVO();
		try {
			quotationModel = quotationService.getQuotationModelWithCostAndAccessories(revisionQmdId);
			
		    if (quotationModel instanceof HibernateProxy) {
		    	quotationModel = (QuotationModel) ((HibernateProxy) quotationModel).getHibernateLazyInitializer().getImplementation();
				Hibernate.initialize(quotationModel.getQuotationCapitalElements());
				Hibernate.initialize(quotationModel.getQuotationCapitalElementsBackup());
				Hibernate.initialize(quotationModel.getQuotationDealerAccessories());
				Hibernate.initialize(quotationModel.getQuotationModelAccessories());
				Hibernate.initialize(quotationModel.getQuotationModelFinances());
				Hibernate.initialize(quotationModel.getQuoteModelPropertyValues());
		    }			
			
			
			revisionOEQuoteVO.setQuotationModel(quotationModel);
		
			revisionOEQuoteVO.setAccountCode(quotationModel.getQuotation().getExternalAccount().getExternalAccountPK().getAccountCode());
			revisionOEQuoteVO.setAccountName(quotationModel.getQuotation().getExternalAccount().getAccountName());
			revisionOEQuoteVO.setQuote(Long.toString(quotationModel.getQuotation().getQuoId()) + "/"
					+ Long.toString(quotationModel.getQuoteNo()) + "/" + Long.toString(quotationModel.getRevisionNo()));
			revisionOEQuoteVO.setUnitNo(quotationModel.getUnitNo());
			revisionOEQuoteVO.setUnitDesc(quotationModel.getModel().getModelDescription());
			revisionOEQuoteVO.setTerm(quotationModel.getContractPeriod());
			revisionOEQuoteVO.setDistance(quotationModel.getContractDistance());
			revisionOEQuoteVO.setCapitalContribution(quotationModel.getCapitalContribution() == null ? BigDecimal.ZERO
					: quotationModel.getCapitalContribution());	
			revisionOEQuoteVO.setInterestIndex(quotationModel.getQuotation().getQuotationProfile().getItcInterestType());
			Double invoiceAdjustment = quotationService.getFinanceParam(MalConstants.FIN_PARAM_OE_INV_ADJ, quotationModel.getQmdId(),
					quotationModel.getQuotation().getQuotationProfile().getQprId());
			revisionOEQuoteVO.setInvoiceAdjustment(BigDecimal.valueOf(invoiceAdjustment));			
			BigDecimal effNBV = openEndService.calculateEffDateNBV(currentOEQuoteVO, revisionOEQuoteVO, contractLine);
			revisionOEQuoteVO.setEffDateNBV(effNBV);					
			/*
			for (QuoteModelPropertyValue quoteModelPropertyValue : revisionOEQuoteVO.getQuotationModel().getQuoteModelPropertyValues()) {			
				String propertyName = quoteModelPropertyValue.getQuoteModelProperty().getName();			
				if(propertyName.equals(OE_REV_ASSMT)){		
					revisionOEQuoteVO.setRevisedAssessment(new BigDecimal(quoteModelPropertyValue.getPropertyValue()));					
				}else if(propertyName.equals(OE_REV_INT_ADJ)){				
					revisionOEQuoteVO.setRevisionInterestAdjustment(new BigDecimal(quoteModelPropertyValue.getPropertyValue()));					
				}else if(propertyName.equals(OE_REV_ASSMT_INRATE_YN)){				
					revisionOEQuoteVO.setRevisedAssessmentType(quoteModelPropertyValue.getPropertyValue().equals("Y") ? IN_RATE_TYPE  : ONE_TIME_CHARGE_TYPE);	
				}else if(propertyName.equals(OE_REV_INT_ADJ_INRATE_YN)){
					revisionOEQuoteVO.setRevisedIntAdjType(quoteModelPropertyValue.getPropertyValue().equals("Y") ? IN_RATE_TYPE  : ONE_TIME_CHARGE_TYPE);
				}
			}			

			revisionOEQuoteVO.setCustomerCost(openEndService.getRevCustomerCostTotal(revisionOEQuoteVO));
			*/
			List<CnbvVO> cnbvList =  quotationService.getCnbvForQuotationModel(revisionQmdId, revisionOEQuoteVO.getQuotationModel().getQmdId());
			CnbvVO cnbv = cnbvList.get(cnbvList.size() - 1);
			revisionOEQuoteVO.setCustomerCost(cnbv.getCustCapCost());
			revisionOEQuoteVO.setFinalNBV(cnbv.getResidualValue());
			
			if(quotationModel.getDepreciationFactor() != null){ 
				revisionOEQuoteVO.setDepreciationFactor(quotationModel.getDepreciationFactor().setScale(DEPRECIATION_FACTOR_SCALE, RoundingMode.HALF_UP));
			} else {
				revisionOEQuoteVO.setDepreciationFactor(openEndService.getCalculatedDepreciationFactor(revisionOEQuoteVO));
			}	
			
			Double adminFactorFinV = quotationService.getFinanceParam(MalConstants.FIN_PARAM_ADMIN_FACT,
					quotationModel.getQmdId(), quotationModel.getQuotation().getQuotationProfile().getQprId());
			revisionOEQuoteVO.setAdminFactor(BigDecimal.valueOf(adminFactorFinV));
			
			revisionOEQuoteVO.setActualLeaseRate(getRevisionSteps(revisionOEQuoteVO).get(0).getLeaseRate());
			
			if(revisionOEQuoteVO.getQuotationModel().getAmendmentEffectiveDate() == null) {
				revisionOEQuoteVO.getQuotationModel().setAmendmentEffectiveDate(new Date());
			}		

			
		} catch (Exception ex) {
			logger.error(ex);
			throw new MalException("generic.error.occured.while", new String[] { "getting Schedule A revision quote data" }, ex);
		}
		
		return revisionOEQuoteVO;
	}
	
	
	private QuoteOEVO loadCurrentQuote(Long currentQmdId) {
		QuoteOEVO currentOEQuoteVO = new QuoteOEVO();
		try {
			QuotationModel quotationModel = quotationService.getQuotationModelWithCostAndAccessories(currentQmdId);
			
			currentOEQuoteVO.setQuotationModel(quotationModel);
			
			currentOEQuoteVO.setQuote(Long.toString(quotationModel.getQuotation().getQuoId()) + "/"
					+ Long.toString(quotationModel.getQuoteNo()) + "/" + Long.toString(quotationModel.getRevisionNo()));			
			currentOEQuoteVO.setUnitNo(quotationModel.getUnitNo());
			QuoteCost quoteCost = openEndService.getQuoteCost(quotationModel);
			currentOEQuoteVO.setCustomerCost(quoteCost.getCustomerCost());	
			
			BigDecimal finalResidual = BigDecimal.ZERO;
			BigDecimal mainElementResidual = quotationModel.getResidualValue();
			BigDecimal equipmentResidual = rentalCalculationService.getEquipmentResidual(quotationModel);
			finalResidual = mainElementResidual.add(equipmentResidual);

			currentOEQuoteVO.setMainElementResidual(mainElementResidual);

			finalResidual = openEndService.getModifiedNBV(finalResidual);
			currentOEQuoteVO.setFinalResidual(finalResidual);
			currentOEQuoteVO.setFinalNBV(finalResidual);
			if(quotationModel.getDepreciationFactor() != null){ 
				currentOEQuoteVO.setDepreciationFactor(quotationModel.getDepreciationFactor().setScale(DEPRECIATION_FACTOR_SCALE, RoundingMode.HALF_UP));
			} else {
				currentOEQuoteVO.setDepreciationFactor(openEndService.getCalculatedDepreciationFactor(currentOEQuoteVO));
			}	
			
			Double adminFactorFinV = quotationService.getFinanceParam(MalConstants.FIN_PARAM_ADMIN_FACT,
					quotationModel.getQmdId(), quotationModel.getQuotation().getQuotationProfile().getQprId());
			currentOEQuoteVO.setAdminFactor(BigDecimal.valueOf(adminFactorFinV));
			
			List<QuotationStepStructureVO> currentSteps = quotationService.getAllQuoteSteps(currentQmdId);		
			
			for (QuotationStepStructureVO stepStructureVO : currentSteps) {//this may have multiple quote's steps
				if(stepStructureVO.getAssociatedQmdId().equals(currentQmdId)){
					currentOEQuoteVO.setFinalResidual(currentSteps.get(currentSteps.size()-1).getNetBookValue());
					currentOEQuoteVO.setFinalNBV(currentSteps.get(currentSteps.size()-1).getNetBookValue());
					currentOEQuoteVO.setActualLeaseRate(currentSteps.get(0).getLeaseRate());
					break;
				}
			}		

			
		} catch (Exception ex) {
			logger.error(ex);
			throw new MalException("generic.error.occured.while", new String[] { "getting Schedule A original quote data" }, ex);
		}
		return currentOEQuoteVO;
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
	
	private VehicleInformationVO getVehicleInformation(FleetMaster fleetMaster, ContractLine contractLine){
		VehicleInformationVO vehicleInformationVO = new VehicleInformationVO();	

		vehicleInformationVO.setVin(fleetMaster.getVin());	
		vehicleInformationVO.setClientFleetReferenceNumber(fleetMaster.getFleetReferenceNumber());
		vehicleInformationVO.setDriverForeName(contractLine.getDriver().getDriverForename()); 
		vehicleInformationVO.setDriverSurname(contractLine.getDriver().getDriverSurname());
		vehicleInformationVO.setDriverCostCenter(MALUtilities.isEmpty(contractLine.getDriver().getDriverCurrentCostCenter()) ? null : contractLine.getDriver().getDriverCurrentCostCenter().getCostCenterCode());			
		vehicleInformationVO.setDriverCostCenterName(MALUtilities.isEmpty(contractLine.getDriver().getDriverCurrentCostCenter()) ? null : costCenterService.getCostCenterDescription(contractLine.getDriver().getDriverCurrentCostCenter()));
		
		return vehicleInformationVO;
	}	

	private List<QuotationStepStructureVO> loadSteps(QuoteOEVO quoteOEVO) {
		List<QuotationStepStructure> quotationStepStructureList = quoteOEVO.getQuotationModel().getQuotationStepStructure();

		return quotationService.getCalculateQuotationStepStructure(quotationStepStructureList, quoteOEVO.getQuotationModel(), quoteOEVO.getDepreciationFactor(), 
				quoteOEVO.getAdminFactor(), quoteOEVO.getCustomerCost());
	}	
	
	private List<ServicesLeaseRateByPeriodVO> getServicesLeaseRate(QuoteOEVO quoteOEVO){
		List<ServicesLeaseRateByPeriodVO> servicesLeaseRateByPeriodVOList = new ArrayList<ServicesLeaseRateByPeriodVO>();
		
		for (QuotationElement quotationElement : quoteOEVO.getQuotationModel().getQuotationElements()) {
			if (!quotationElement.getLeaseElement().getElementType().equals(MalConstants.FINANCE_ELEMENT)){
				ServicesLeaseRateByPeriodVO servicesLeaseRateByPeriodVO = new ServicesLeaseRateByPeriodVO();
				servicesLeaseRateByPeriodVO.setDescription(quotationElement.getLeaseElement().getDescription());
				
				if(quotationElement.getRental() != null) {
					BigDecimal cost = quotationElement.getRental().divide(openEndService.getRentalPeriods(quotationElement, quoteOEVO), 2, BigDecimal.ROUND_HALF_UP);
					servicesLeaseRateByPeriodVO.setMonthlyCost(cost);				
				}
				servicesLeaseRateByPeriodVO.setMonthlyCost(servicesLeaseRateByPeriodVO.getMonthlyCost() != null ? servicesLeaseRateByPeriodVO.getMonthlyCost() : BigDecimal.ZERO);			
				servicesLeaseRateByPeriodVOList.add(servicesLeaseRateByPeriodVO);
			}
		}
		
		if(!MALUtilities.isEmpty(servicesLeaseRateByPeriodVOList)){
			Collections.sort(servicesLeaseRateByPeriodVOList, new Comparator<ServicesLeaseRateByPeriodVO>() {
				public int compare(ServicesLeaseRateByPeriodVO s1, ServicesLeaseRateByPeriodVO s2) {
					String desc1 = s1.getDescription();
					String desc2 = s2.getDescription();
					return desc1.toLowerCase().compareTo(desc2.toLowerCase());						
				}
			});			
		}	
		
		return servicesLeaseRateByPeriodVOList;
	
	}

	private List<QuotationStepStructureVO> getRevisionSteps(QuoteOEVO revisionOEQuoteVO) {
		List<QuotationStepStructureVO> revisionSteps = new ArrayList<QuotationStepStructureVO>();		
		revisionSteps = loadSteps(revisionOEQuoteVO);

		return revisionSteps;
	}	

	private List<QuotationStepStructureVO> getRevStepsForUi(QuoteOEVO currentOEQuoteVO, QuoteOEVO revisionOEQuoteVO, ContractLine contractLine) throws Exception{
		List<QuotationStepStructureVO> revStepsForUi = new ArrayList<QuotationStepStructureVO>();
		revStepsForUi = openEndService.getCurrentContractStepsforRevisionSteps(currentOEQuoteVO, revisionOEQuoteVO, contractLine);
		revStepsForUi.addAll(loadSteps(revisionOEQuoteVO));
		
		return revStepsForUi;
	}	

	private VehicleInfoVO populateVehicleInfoVO(QuoteOEVO quoteOEVO) {	
		VehicleInfoVO vehicleInfoVO = new VehicleInfoVO();
		StringBuilder sbSE = new StringBuilder("");
		StringBuilder sbOE = new StringBuilder("");		
	
		if (quoteOEVO.getQuotationModel().getQmdId() != null) {
			List<Object> objListSE = purchaseOrderReportDAO.getStandardAccessories(quoteOEVO.getQuotationModel().getQmdId());
			if (objListSE != null) {
				for (Object objSE : objListSE) {
					sbSE.append((String) objSE).append(RPT_NEW_LINE_CHAR);
				}
			}
		}
		vehicleInfoVO.setStandardEquipments(sbSE.toString());	

		if (quoteOEVO.getQuotationModel().getQmdId() != null) {
			List<Object> objListOE = purchaseOrderReportDAO.getModelAccessories(quoteOEVO.getQuotationModel().getQmdId());
			if (objListOE != null) {
				for (Object objOE : objListOE) {
					sbOE.append((String) objOE).append(RPT_NEW_LINE_CHAR);
				}
			}
		}
		vehicleInfoVO.setOptionalEquipments(sbOE.toString());	
		
		return vehicleInfoVO;
	}
	
	private DeliveringDealerInfoVO populateDeliveringDealerInfoVO(QuoteOEVO quoteOEVO) {
		DeliveringDealerInfoVO deliveringDealerInfoVO = new DeliveringDealerInfoVO();
		StringBuilder sbDD = new StringBuilder("");		

		if (quoteOEVO.getQuotationModel().getQmdId() != null) {
			List<Object> objListDD = purchaseOrderReportDAO.getDealerAccessories(quoteOEVO.getQuotationModel().getQmdId());
			if (objListDD != null) {
				for (Object objDD : objListDD) {
					sbDD.append((String) objDD).append(RPT_NEW_LINE_CHAR);
				}
			}
		}
		deliveringDealerInfoVO.setDealerInstalledOptions(sbDD.toString());
		
		return deliveringDealerInfoVO;	
	}

}
