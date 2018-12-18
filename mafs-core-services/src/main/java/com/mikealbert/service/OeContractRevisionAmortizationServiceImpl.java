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

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.dao.ContractDAO;
import com.mikealbert.data.dao.ContractLineDAO;
import com.mikealbert.data.dao.FleetMasterDAO;
import com.mikealbert.data.dao.ProductDAO;
import com.mikealbert.data.entity.Contract;
import com.mikealbert.data.entity.ContractLine;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.Product;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.QuoteModelPropertyValue;
import com.mikealbert.data.enumeration.QuoteModelPropertyEnum;
import com.mikealbert.data.vo.CnbvVO;
import com.mikealbert.data.vo.OeConRevAmortizationScheduleVO;
import com.mikealbert.data.vo.PeriodFinalNBVVO;
import com.mikealbert.data.vo.QuotationStepStructureVO;
import com.mikealbert.data.vo.QuoteOEVO;
import com.mikealbert.data.vo.VehicleInformationVO;
import com.mikealbert.exception.MalException;
import com.mikealbert.service.vo.QuoteCost;
import com.mikealbert.util.MALUtilities;

@Service("OeContractRevisionAmortizationService")
@Transactional
public class OeContractRevisionAmortizationServiceImpl implements OeContractRevisionAmortizationService{
	
	@Resource QuotationService quotationService;
	@Resource ContractService contractService;
	@Resource ContractDAO contractDAO;
	@Resource ContractLineDAO contractLineDAO;
	@Resource CostCenterService costCenterService;	
	@Resource RentalCalculationService rentalCalculationService;
	@Resource ProfitabilityService profitabilityService;	
	@Resource CapitalCostService capitalCostService;	
	@Resource FleetMasterDAO fleetMasterDAO;	
	@Resource ProductDAO productDAO;
	@Resource OpenEndService openEndService;
	@Resource QuoteModelPropertyValueService quoteModelPropertyValueService;
	
	private MalLogger logger = MalLoggerFactory.getLogger(this.getClass());	
	
	private static final int DEPRECIATION_FACTOR_SCALE = 7;		

	@Override
	@Transactional(readOnly = true)
	public List<OeConRevAmortizationScheduleVO> getOeConRevAmortizationReportVO(Long currentQmdId, Long revisionQmdId) throws MalException {
		List<OeConRevAmortizationScheduleVO> oeConRevAmortizationScheduleVOList = new ArrayList<OeConRevAmortizationScheduleVO>();
		Long lastAmendedOrOriginalQmdId = null; //Last Amended Quote prior to first Revised Quote, Or Original Accepted Quote for which VQ printed 
		
		try {
			Long conId = contractLineDAO.findByQmdId(revisionQmdId).get(0).getContract().getConId();

			
			Contract contract = contractDAO.findById(conId).orElse(null);
			List<ContractLine> contractLines = new ArrayList<ContractLine>();
			if(contract != null) {
				contractLines = contract.getContractLineList();
			}
						
			Collections.sort(contractLines, new Comparator<ContractLine>() {
			    public int compare(ContractLine cln1, ContractLine cln2) {
			        return cln1.getRevNo().compareTo(cln2.getRevNo());
			    }
			});
			
			for (int i = 0; i < contractLines.size(); i++) {
				QuoteModelPropertyValue qmpv = quoteModelPropertyValueService.findByNameAndQmdId(QuoteModelPropertyEnum.QUOTE_TYPE.getName() , contractLines.get(i).getQuotationModel().getQmdId());
				if(qmpv != null && "R".equals(qmpv.getPropertyValue())){
					lastAmendedOrOriginalQmdId = contractLines.get(i - 1).getQuotationModel().getQmdId();
					break;
				}
			}
			
		} catch (Exception ex) {
			if (MALUtilities.isEmpty(lastAmendedOrOriginalQmdId)) {
				lastAmendedOrOriginalQmdId = currentQmdId;
			}
		}
		
		oeConRevAmortizationScheduleVOList.add(populateOeConRevAmortizationSchedule(lastAmendedOrOriginalQmdId, revisionQmdId));		
		return oeConRevAmortizationScheduleVOList;
	}
	private OeConRevAmortizationScheduleVO populateOeConRevAmortizationSchedule(Long currentQmdId, Long revisionQmdId) throws MalException {
		OeConRevAmortizationScheduleVO oeConRevAmortizationScheduleVO = new OeConRevAmortizationScheduleVO();
		try {
			QuoteOEVO currentOEQuoteVO = loadCurrentQuote(currentQmdId);
			oeConRevAmortizationScheduleVO.setCurrentOEQuoteVO(currentOEQuoteVO);

			List<QuoteOEVO> revisionOEQuoteVOList = loadRevisionQuotes(currentOEQuoteVO, revisionQmdId);
			oeConRevAmortizationScheduleVO.setRevisionOEQuoteVOList(revisionOEQuoteVOList);
			populateFinalNBVVOList(oeConRevAmortizationScheduleVO, revisionQmdId);
			
		} catch (Exception ex) {
			logger.error(ex);
			throw new MalException("generic.error.occured.while", new String[] { "getting Open End Contract Revision Amoritization Schedule report data" }, ex);
		}
		
		return oeConRevAmortizationScheduleVO;
	}
	
	private void populateFinalNBVVOList(OeConRevAmortizationScheduleVO oeConRevAmortizationScheduleVO, Long revisionQmdId) {
		Integer	cutOffPeriod = 0;
		List<CnbvVO> cnbvs =  quotationService.getCnbv(revisionQmdId);

		if (!MALUtilities.isEmpty(cnbvs)) {
			List<PeriodFinalNBVVO> currentNBVVOList = new ArrayList<PeriodFinalNBVVO>();
			for (CnbvVO cnbv : cnbvs) {
				if (oeConRevAmortizationScheduleVO.getCurrentOEQuoteVO().getQuotationModel().getQmdId() >= cnbv.getQmdId()) {
					PeriodFinalNBVVO currentPeriodFinalVO = new PeriodFinalNBVVO();
					currentPeriodFinalVO.setPeriod(cnbv.getPeriod());
					currentPeriodFinalVO.setFinalNBV(cnbv.getCnbv());
					currentNBVVOList.add(currentPeriodFinalVO);					
				}
			}
			cutOffPeriod = currentNBVVOList.size();
			for(int j=currentNBVVOList.size() + 1; j<=90; j++){
				currentNBVVOList.add(new PeriodFinalNBVVO());
			}			
			oeConRevAmortizationScheduleVO.getCurrentOEQuoteVO().setFinalNBVVOList(currentNBVVOList);
	
			oeConRevAmortizationScheduleVO.getCurrentOEQuoteVO().setQuoteComment(
					"Contract was revised effective period " + (cutOffPeriod.intValue() + 1) + ". Updated contract information appears below:"
				);			
			
			for (int i = 0; i < oeConRevAmortizationScheduleVO.getRevisionOEQuoteVOList().size(); i++) {
				QuoteOEVO revisedQuote = new QuoteOEVO();
				List<PeriodFinalNBVVO> revisedNBVVOList = new ArrayList<PeriodFinalNBVVO>();
				List<PeriodFinalNBVVO> fillRevisionPeriodFinalNBVVOList = new ArrayList<PeriodFinalNBVVO>();				
				revisedQuote = oeConRevAmortizationScheduleVO.getRevisionOEQuoteVOList().get(i);
				revisedQuote.setFinalNBVVOList(fillRevisionPeriodFinalNBVVOList);
				for(int j=1; j <= cutOffPeriod; j++){
					revisedNBVVOList.add(new PeriodFinalNBVVO());
				}				
				for (CnbvVO cnbv : cnbvs) {
					if ((oeConRevAmortizationScheduleVO.getRevisionOEQuoteVOList().size() -1 ) == i) {
						if (revisedQuote.getQuotationModel().getQmdId() <= cnbv.getQmdId()) {
							PeriodFinalNBVVO revisionPeriodFinalVO = new PeriodFinalNBVVO();
							revisionPeriodFinalVO.setPeriod(cnbv.getPeriod());
							revisionPeriodFinalVO.setFinalNBV(cnbv.getCnbv());
							revisedNBVVOList.add(revisionPeriodFinalVO);					
						}						
					} else {
						if (cnbv.getQmdId() >= revisedQuote.getQuotationModel().getQmdId() && (cnbv.getQmdId() < oeConRevAmortizationScheduleVO.getRevisionOEQuoteVOList().get(i+1).getQuotationModel().getQmdId())) {
							PeriodFinalNBVVO revisionPeriodFinalVO = new PeriodFinalNBVVO();
							revisionPeriodFinalVO.setPeriod(cnbv.getPeriod());
							revisionPeriodFinalVO.setFinalNBV(cnbv.getCnbv());
							revisedNBVVOList.add(revisionPeriodFinalVO);					
						}
					}
				}
				cutOffPeriod = revisedNBVVOList.size();
				for(int j=cutOffPeriod + 1; j<=90; j++){
					revisedNBVVOList.add(new PeriodFinalNBVVO());
				}				
				revisedQuote.getFinalNBVVOList().addAll(revisedNBVVOList);

				oeConRevAmortizationScheduleVO.getRevisionOEQuoteVOList().get(i).setFinalNBVVOList(revisedQuote.getFinalNBVVOList());
				if (i < (oeConRevAmortizationScheduleVO.getRevisionOEQuoteVOList().size() - 1)) { //Don't print this on last page
					oeConRevAmortizationScheduleVO.getRevisionOEQuoteVOList().get(i).setQuoteComment(
							"Contract was revised effective period " + (cutOffPeriod.intValue() + 1) + ". Updated contract information appears below:"
						);
				}
			}
		}			
	}

	private QuoteOEVO loadCurrentQuote(Long currentQmdId) {
		QuoteOEVO currentOEQuoteVO = new QuoteOEVO();

		try {
			QuotationModel quotationModel = quotationService.getQuotationModelWithCostAndAccessories(currentQmdId);
			currentOEQuoteVO.setQuotationModel(quotationModel);
			currentOEQuoteVO.setUnitNo(quotationModel.getUnitNo());
			currentOEQuoteVO.setUnitDesc(quotationModel.getModel().getModelDescription());			
			FleetMaster fleetMaster = fleetMasterDAO.findByUnitNo(currentOEQuoteVO.getUnitNo());
			ContractLine contractLine = contractService.getCurrentContractLine(fleetMaster, new Date());
			currentOEQuoteVO.setContractStartDate(contractLine.getStartDate());
			currentOEQuoteVO.getQuotationModel().setContractLine(contractLine);
			currentOEQuoteVO.setQuote(Long.toString(quotationModel.getQuotation().getQuoId()) + "/"
					+ Long.toString(quotationModel.getQuoteNo()) + "/" + Long.toString(quotationModel.getRevisionNo()));			
			currentOEQuoteVO.setTerm(quotationModel.getContractPeriod());			
			currentOEQuoteVO.setProjectedReplacementMonths(quotationModel.getProjectedMonths());			
			Product product	= productDAO.findById(currentOEQuoteVO.getQuotationModel().getQuotation().getQuotationProfile().getPrdProductCode()).orElse(null);
			currentOEQuoteVO.setLogo(openEndService.getLogoName(product));
			currentOEQuoteVO.setVehicleInformationVO(getVehicleInformation(fleetMaster, contractLine));
			currentOEQuoteVO.setOeConRevAmortizationScheduleTitle("Vehicle Lease Amortization Schedule"); //HD-492
			currentOEQuoteVO.setAccountCode(quotationModel.getQuotation().getExternalAccount().getExternalAccountPK().getAccountCode());
			currentOEQuoteVO.setAccountName(quotationModel.getQuotation().getExternalAccount().getAccountName());
			
			QuoteCost quoteCost = openEndService.getQuoteCost(quotationModel);
			currentOEQuoteVO.setCustomerCost(quoteCost.getCustomerCost());				
			BigDecimal finalResidual = BigDecimal.ZERO;
			BigDecimal mainElementResidual = quotationModel.getResidualValue();
			BigDecimal equipmentResidual = rentalCalculationService.getEquipmentResidual(quotationModel);
			finalResidual = openEndService.getModifiedNBV(finalResidual);
			currentOEQuoteVO.setMainElementResidual(mainElementResidual);			
			finalResidual = mainElementResidual.add(equipmentResidual);
			currentOEQuoteVO.setFinalResidual(finalResidual);
			currentOEQuoteVO.setFinalNBV(finalResidual);
			if(quotationModel.getDepreciationFactor() != null){ 
				currentOEQuoteVO.setDepreciationFactor(quotationModel.getDepreciationFactor().setScale(DEPRECIATION_FACTOR_SCALE, RoundingMode.HALF_UP));
			} else {
				currentOEQuoteVO.setDepreciationFactor(openEndService.getCalculatedDepreciationFactor(currentOEQuoteVO));
			}
			
			BigDecimal currentFinalNBV = profitabilityService.getFinalNBV(currentOEQuoteVO.getCustomerCost(), currentOEQuoteVO.getDepreciationFactor(), BigDecimal.ONE);
			BigDecimal currentMonthlyDepreciation = currentOEQuoteVO.getCustomerCost().subtract(currentFinalNBV);
			currentOEQuoteVO.setMonthlyDepreciation(currentMonthlyDepreciation);	

			List<QuotationStepStructureVO> currentSteps = quotationService.getAllQuoteSteps(currentQmdId);		
			
			for (QuotationStepStructureVO stepStructureVO : currentSteps) {//this may have multiple quote's steps
				if(stepStructureVO.getAssociatedQmdId().equals(currentQmdId)){
					currentOEQuoteVO.setFinalResidual(currentSteps.get(currentSteps.size()-1).getNetBookValue());
					currentOEQuoteVO.setFinalNBV(currentSteps.get(currentSteps.size()-1).getNetBookValue());
					currentOEQuoteVO.setActualLeaseRate(currentSteps.get(0).getLeaseRate());
					break;
				}
			}			

			currentOEQuoteVO.setNoteSection(
					"\"Book Value\" reflects Depreciated Vehicle Balance at conclusion of the period assuming payment has been kept current.  <br/>" +
							"This \"Book Value\" does not constitute the lease settlement amounts due at any given period. At the end of Full Lease <br/>" +
							"Term, any remaining book value will be debited or credited to the last full month's rental invoice or will be used in the  <br/>" +
							"cap cost calculation for a formal or informal extension."
						);
		} catch (Exception ex) {
			logger.error(ex);
			throw new MalException("generic.error.occured.while", new String[] { "getting Amortization Report original quote data" }, ex);
		}
		return currentOEQuoteVO;
	}
	private List<QuoteOEVO> loadRevisionQuotes(QuoteOEVO currentOEQuoteVO, Long revisionQmdId) {
		List<QuoteOEVO> revisionOEQuoteList = new ArrayList<QuoteOEVO>();
		//List<ContractLine> currentContractLines = contractService.getContractLinesOfLastestContract(currentOEQuoteVO.getQuotationModel().getContractLine().getFleetMaster().getFmsId());
		Contract contract = contractDAO.findById(currentOEQuoteVO.getQuotationModel().getContractLine().getContract().getConId()).orElse(null);
		List<ContractLine> currentContractLines = new ArrayList<ContractLine>();
		if(contract != null) {
			currentContractLines = contract.getContractLineList();
		}
		
		Collections.sort(currentContractLines, new Comparator<ContractLine>() {
		    public int compare(ContractLine cln1, ContractLine cln2) {
		        return cln1.getRevNo().compareTo(cln2.getRevNo());
		    }
		});		
		
		for (int i = 0; i < currentContractLines.size(); i++) {
			Long qmdId = currentContractLines.get(i).getQuotationModel().getQmdId(); 
			QuoteModelPropertyValue qmpv = quoteModelPropertyValueService.findByNameAndQmdId(QuoteModelPropertyEnum.QUOTE_TYPE.getName() , qmdId);
			if(qmpv != null && "R".equals(qmpv.getPropertyValue())){
				
				try {
					QuoteOEVO revisionOEQuoteVO = new QuoteOEVO();
					revisionOEQuoteVO.setLogo(currentOEQuoteVO.getLogo());
					revisionOEQuoteVO.setOeConRevAmortizationScheduleTitle(currentOEQuoteVO.getOeConRevAmortizationScheduleTitle());
					revisionOEQuoteVO.setVehicleInformationVO(currentOEQuoteVO.getVehicleInformationVO());
					QuotationModel quotationModel = quotationService.getQuotationModelWithCostAndAccessories(qmdId);
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
					revisionOEQuoteVO.getQuotationModel().setContractLine(currentContractLines.get(i));
					revisionOEQuoteVO.setContractStartDate(currentContractLines.get(i).getStartDate());
					revisionOEQuoteVO.setAccountCode(quotationModel.getQuotation().getExternalAccount().getExternalAccountPK().getAccountCode());
					revisionOEQuoteVO.setAccountName(quotationModel.getQuotation().getExternalAccount().getAccountName());
					revisionOEQuoteVO.setQuote(Long.toString(quotationModel.getQuotation().getQuoId()) + "/"
							+ Long.toString(quotationModel.getQuoteNo()) + "/" + Long.toString(quotationModel.getRevisionNo()));			
					revisionOEQuoteVO.setUnitNo(quotationModel.getUnitNo());
					revisionOEQuoteVO.setUnitDesc(quotationModel.getModel().getModelDescription());					
					revisionOEQuoteVO.setTerm(quotationModel.getContractPeriod());
					revisionOEQuoteVO.setProjectedReplacementMonths(quotationModel.getProjectedMonths());
					
					List<CnbvVO> cnbvList =  quotationService.getCnbvForQuotationModel(revisionQmdId, revisionOEQuoteVO.getQuotationModel().getQmdId());
					CnbvVO cnbv = cnbvList.get(cnbvList.size() - 1);
					revisionOEQuoteVO.setCustomerCost(cnbv.getCustCapCost());
					revisionOEQuoteVO.setFinalNBV(cnbv.getResidualValue());
					
					if(quotationModel.getDepreciationFactor() != null){ 
						revisionOEQuoteVO.setDepreciationFactor(quotationModel.getDepreciationFactor().setScale(DEPRECIATION_FACTOR_SCALE, RoundingMode.HALF_UP));
					} else {
						revisionOEQuoteVO.setDepreciationFactor(openEndService.getCalculatedDepreciationFactor(revisionOEQuoteVO));
					}	
					
					BigDecimal revisionFinalNBV = profitabilityService.getFinalNBV(revisionOEQuoteVO.getCustomerCost(), revisionOEQuoteVO.getDepreciationFactor(), BigDecimal.ONE);
					BigDecimal revisionMonthlyDepreciation = revisionOEQuoteVO.getCustomerCost().subtract(revisionFinalNBV);
					revisionOEQuoteVO.setMonthlyDepreciation(revisionMonthlyDepreciation);	
					
					revisionOEQuoteVO.setOeConRevAmortizationScheduleSubTitle("REVISED");
					revisionOEQuoteVO.setNoteSection(currentOEQuoteVO.getNoteSection());
					revisionOEQuoteList.add(revisionOEQuoteVO);
				} catch (Exception ex) {
					logger.error(ex);
					throw new MalException("generic.error.occured.while", new String[] { "getting Amortization Report revision quote data" }, ex);
				}				
			}
		 }
				
		return revisionOEQuoteList;
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
	
}
