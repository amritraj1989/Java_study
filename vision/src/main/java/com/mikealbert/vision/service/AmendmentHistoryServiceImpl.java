package com.mikealbert.vision.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.common.CommonCalculations;
import com.mikealbert.common.MalConstants;
import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.dao.ContractLineDAO;
import com.mikealbert.data.dao.DealerAccessoryDAO;
import com.mikealbert.data.dao.DocDAO;
import com.mikealbert.data.dao.DocLinkDAO;
import com.mikealbert.data.dao.DoclDAO;
import com.mikealbert.data.dao.ExternalAccountDAO;
import com.mikealbert.data.dao.FleetMasterDAO;
import com.mikealbert.data.dao.InformalAmendmentDAO;
import com.mikealbert.data.dao.LeaseElementDAO;
import com.mikealbert.data.dao.MaintenanceRequestTaskDAO;
import com.mikealbert.data.dao.MalCapitalCostDAO;
import com.mikealbert.data.dao.QuotationModelDAO;
import com.mikealbert.data.entity.ContractLine;
import com.mikealbert.data.entity.Doc;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.ExternalAccountPK;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.FormulaParameter;
import com.mikealbert.data.entity.InformalAmendment;
import com.mikealbert.data.entity.LeaseElement;
import com.mikealbert.data.entity.MaintenanceRequestTask;
import com.mikealbert.data.entity.MalCapitalCost;
import com.mikealbert.data.entity.QuotationCapitalElement;
import com.mikealbert.data.entity.QuotationDealerAccessory;
import com.mikealbert.data.entity.QuotationElement;
import com.mikealbert.data.entity.QuotationElementStep;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.QuotationModelFinances;
import com.mikealbert.data.entity.QuoteModelPropertyValue;
import com.mikealbert.data.enumeration.QuoteModelPropertyEnum;
import com.mikealbert.data.vo.FinanceParameterVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.CapitalCostService;
import com.mikealbert.service.CapitalElementService;
import com.mikealbert.service.ContractService;
import com.mikealbert.service.FinanceParameterService;
import com.mikealbert.service.FleetMasterService;
import com.mikealbert.service.QuotationService;
import com.mikealbert.service.QuoteModelPropertyValueService;
import com.mikealbert.service.RentalCalculationService;
import com.mikealbert.service.vo.QuoteCost;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.vo.AmendmentHistoryVO;
import com.mikealbert.vision.vo.EleAmendmentDetailVO;

@Service
@Transactional
public class AmendmentHistoryServiceImpl implements AmendmentHistoryService {
	private MalLogger logger = MalLoggerFactory.getLogger(this.getClass());
	@Resource FleetMasterService fleetMasterService;
	@Resource ContractService contractService;
	@Resource QuotationModelDAO quotationModelDAO;
	@Resource InformalAmendmentDAO informalAmendmentDAO;
	@Resource LeaseElementDAO leaseElementDAO;
	@Resource FinanceParameterService financeParameterService;
	@Resource CapitalCostOverviewService capitalCostOverviewService;
	@Resource ContractLineDAO contractLineDAO;;
	@Resource RentalCalculationService rentalCalculationService;
	@Resource QuotationService quotationService;
	@Resource DealerAccessoryDAO dealerAccessoryDAO;
	@Resource CapitalCostService capitalCostService;
	@Resource ExternalAccountDAO externalAccountDAO;
	@Resource DocLinkDAO docLinkDao;
	@Resource DocDAO docDAO;
	@Resource DoclDAO doclDao;
	@Resource FleetMasterDAO fleetMasterDAO;
	@Resource MaintenanceRequestTaskDAO maintenanceRequestTaskDAO;
	@Resource MalCapitalCostDAO	malCapitalCostDAO;
	@Resource QuoteModelPropertyValueService quoteModelPropertyValueService;

	public String getContractSource(QuotationModel inComingQuotationModel, ContractLine contractLine) {
		if (inComingQuotationModel != null) {
			if (contractLine == null) {
				return null;
			}
			String contarctDesc = contractLine.getContract().getDescription();
			contarctDesc = MALUtilities.isEmpty(contarctDesc) ? "" : contarctDesc;
			// check for early termination
			if (contractLine.getEarlyTermQuoteId() != null) {
				// early termination
				return CONTRACT_SOURCE_EARLY_TERMINATE;
			} else if (contarctDesc.startsWith("Formal")) {
				if (inComingQuotationModel.getAmendmentEffectiveDate() != null) {
					List<QuotationModel> prevQuoteModels = quotationModelDAO.findPreviousQuotesByQuotation(
							inComingQuotationModel.getQmdId(), contractLine.getContract().getConId());
					if (prevQuoteModels != null && !prevQuoteModels.isEmpty()) {
						if (isQuoteIsRevised(prevQuoteModels.get(0) , inComingQuotationModel)) {
							// revision
							return CONTRACT_SOURCE_REVISION ;
						} else {
							// amendment
							return CONTRACT_SOURCE_AMENDMENT;
						}
					}
				} else {
					// formal extension
					return CONTRACT_SOURCE_FORMAL;
				}
			} else if (inComingQuotationModel.getAmendmentEffectiveDate() != null) {
				List<QuotationModel> prevQuoteModels = quotationModelDAO.findPreviousQuotesByQuotation(inComingQuotationModel.getQmdId(),
						contractLine.getContract().getConId());
				if (prevQuoteModels != null && !prevQuoteModels.isEmpty()) {
					if (isQuoteIsRevised(prevQuoteModels.get(0), inComingQuotationModel)) {
						// revision
						return CONTRACT_SOURCE_REVISION;
					} else {
						// amendment
						return CONTRACT_SOURCE_AMENDMENT;
					}
				}
			} else {
				return null;
			}

		}
		return null;
	}
	
	private boolean isQuoteIsRevised(QuotationModel prevQuoteModel,  QuotationModel currentQuotationModel){
		
		boolean isRevised = false;
		
		 if (currentQuotationModel.getContractPeriod().longValue() != prevQuoteModel.getContractPeriod().longValue()
				|| currentQuotationModel.getContractDistance().longValue() != prevQuoteModel.getContractDistance().longValue()){
			
			isRevised = true;
			
		}else{
			QuoteModelPropertyValue qmPropertyValue = quoteModelPropertyValueService.findByNameAndQmdId(QuoteModelPropertyEnum.QUOTE_TYPE.getName(), currentQuotationModel.getQmdId());			
		
			if(qmPropertyValue != null  && qmPropertyValue.getPropertyValue().equals("R")){		
				
				isRevised = true;
				
			 }
		}
		
		return isRevised;
	}
	
	private boolean isQuoteFinalized(FleetMaster fleetMaster, QuotationModel quotationModel) {
		boolean isFinalized = false;
		ContractLine originalContractLine = contractService.getOriginalContractLine(fleetMaster);
		if (originalContractLine != null && originalContractLine.getStartDate() != null) {
			if (quotationModel.getContractLine() != null) {
				if (originalContractLine.getActualEndDate() != null) {
					isFinalized = true;
				} else {
					isFinalized = false;
				}
			} else {
				isFinalized = true;
			}
		}
		return isFinalized;
	}

	public Boolean isAmendmentExistsOnQuote(QuotationModel quotationModel, ContractLine contractLine) {
		if (quotationModel.getUnitNo() != null) {
			List<QuotationModel> allQuotes = quotationModelDAO.findQuotesByUnitNoAndContract(quotationModel.getUnitNo(), contractLine
					.getContract().getConId());
			for (QuotationModel quotationModel2 : allQuotes) {
				String contarctSource = getContractSource(quotationModel2, contractLine);
				contarctSource = MALUtilities.isEmptyString(contarctSource) ? "" : contarctSource;
				if (contarctSource.equals(CONTRACT_SOURCE_AMENDMENT) || contarctSource.equals(CONTRACT_SOURCE_FORMAL)) {
					return true;
				}
			}
		}
		return false;
	}

	public String getAmendmentSourceType(QuotationModel quotationModel, ContractLine contractLine) {
		if (quotationModel.getUnitNo() != null) {
			List<QuotationModel> allQuotes = quotationModelDAO.findQuotesByUnitNoAndContract(quotationModel.getUnitNo(), contractLine
					.getContract().getConId());
			for (QuotationModel quotationModel2 : allQuotes) {
				String contarctSource = getContractSource(quotationModel2, contractLine);
				contarctSource = MALUtilities.isEmptyString(contarctSource) ? "" : contarctSource;
				return contarctSource;
			}
		}
		return null;
	}

	public List<EleAmendmentDetailVO> getElementsFromInformalAmendment(Long quotationModelId) throws MalBusinessException {
		List<EleAmendmentDetailVO> list = new ArrayList<EleAmendmentDetailVO>();
		try {
			if (quotationModelId != null) {
				List<InformalAmendment> ifaList = informalAmendmentDAO.findByQmdId(quotationModelId);
				if (ifaList != null) {
					EleAmendmentDetailVO eleAmendmentDetailVO = null;
					for (InformalAmendment informalAmendment : ifaList) {
						eleAmendmentDetailVO = new EleAmendmentDetailVO();
						String indicator = CONTRACT_SOURCE_AMENDMENT.equals(informalAmendment.getAddRemove()) ? AMEND_IND_ADDED
								: CONTRACT_SOURCE_REVISION.equals(informalAmendment.getAddRemove()) ? AMEND_IND_REMOVED : null;
						eleAmendmentDetailVO.setAmendmentTypeInd(indicator);
						eleAmendmentDetailVO.setRental(informalAmendment.getBillingAmt());
						eleAmendmentDetailVO.setTotalRental(informalAmendment.getRental());
						eleAmendmentDetailVO.setEffectiveDate(informalAmendment.getEffectiveFrom());
						eleAmendmentDetailVO.setElementType(ELEMENT_TYPE_SERVICE);
						LeaseElement leaseElement = leaseElementDAO.findById(informalAmendment.getLelLelId()).orElse(null);
						if (leaseElement != null) {
							eleAmendmentDetailVO.setElementDesc(leaseElement.getDescription());
						}
						list.add(eleAmendmentDetailVO);
					}
				}
			}
		} catch (Exception ex) {
			logger.error(ex);
			throw new MalBusinessException("generic.error.occured.while", new String[] { "getting informal amendment history" }, ex);
		}
		return list;
	}

	private EleAmendmentDetailVO compareAndReturnAfterMarketAmendmentDetail(QuotationDealerAccessory targetElement,
			List<QuotationDealerAccessory> listToCampare, Boolean seeIfRemoved, String contractSource, Long contractPeriod) {
		boolean isMatchFound = false;
		boolean isFormalExt = CONTRACT_SOURCE_FORMAL.equals(contractSource) ? true : false;
		if (isFormalExt) {
			return null;
		}
		EleAmendmentDetailVO eleAmendmentDetailVO = new EleAmendmentDetailVO();
		for (QuotationDealerAccessory quotationDealerAccessory : listToCampare) {
			if (targetElement.getDealerAccessory().getDacId().longValue() == quotationDealerAccessory.getDealerAccessory().getDacId()
					.longValue()) {
				BigDecimal targetPrice = targetElement.getTotalPrice();
				BigDecimal comparePrice = quotationDealerAccessory.getTotalPrice();
				if (targetPrice.compareTo(comparePrice) == 0) {
					isMatchFound = true;
					break;
				} else {
					// cost change only
					if (!seeIfRemoved && !isFormalExt) {
						isMatchFound = false;
						break;

					}
				}
			}
		}
		eleAmendmentDetailVO.setElementId(targetElement.getDealerAccessory().getDacId());
		eleAmendmentDetailVO.setElementDesc(targetElement.getDealerAccessory().getDealerAccessoryCode().getDescription());
		eleAmendmentDetailVO.setElementType(ELEMENT_TYPE_DEALER);
		boolean isQuotationElementFound = false;
		for (QuotationElement quoElement : targetElement.getQuotationModel().getQuotationElements()) {
			if (quoElement.getQuotationDealerAccessory() != null) {
				if (quoElement.getQuotationDealerAccessory().getQdaId().longValue() == targetElement.getQdaId().longValue()) {
					isQuotationElementFound = true;
					List<QuotationElementStep> qesList = quoElement.getQuotationElementSteps();
					BigDecimal rental = BigDecimal.ZERO;
					if (qesList != null && !qesList.isEmpty()) {
						for (QuotationElementStep quotationElementStep : qesList) {
        // Bug 16259 Fix Start							
							if(quotationElementStep.getManualRental() != null ){
								rental = rental.add(quotationElementStep.getManualRental(), CommonCalculations.MC);
							}else{                                    //Bug 16259 Fix End
								
								if (quotationElementStep.getRentalValue() != null) {
									//Bug 16434
									if("Y".equals(quoElement.getQuotationModel().getSteppedCalc())) {
										if(quotationElementStep.getFromPeriod().compareTo(BigDecimal.ONE) == 0) {
											rental = rental.add(quotationElementStep.getRentalValue(), CommonCalculations.MC);
										}
									} else {
										rental = rental.add(quotationElementStep.getRentalValue(), CommonCalculations.MC);
									}
								}
							}
							
						}
						eleAmendmentDetailVO.setRental(rental);
					}
					eleAmendmentDetailVO.setRechargeAmt(quoElement.getQuotationDealerAccessory().getRechargeAmount());
					eleAmendmentDetailVO.setNoRentals(quoElement.getNoRentals());
					eleAmendmentDetailVO.setTotalRental(quoElement.getCapitalCost());
					break;
				}
			}
		}
		
		if (!isQuotationElementFound) {
			eleAmendmentDetailVO.setTotalRental(targetElement.getRentalAmt() != null ? targetElement.getRentalAmt() : BigDecimal.ZERO);
			eleAmendmentDetailVO.setRental(eleAmendmentDetailVO.getTotalRental().divide(new BigDecimal(contractPeriod),
					CommonCalculations.MC));
			eleAmendmentDetailVO.setRechargeAmt(targetElement.getRechargeAmount());

		}
		//For RC-1963
		//set deal cost and client cost
		BigDecimal recharge = eleAmendmentDetailVO.getRechargeAmt() != null ?  eleAmendmentDetailVO.getRechargeAmt(): BigDecimal.ZERO;
		BigDecimal	discPrice	= targetElement.getDiscPrice() != null ?  targetElement.getDiscPrice() :BigDecimal.ZERO;
		eleAmendmentDetailVO.setClientCost(discPrice.subtract(recharge,CommonCalculations.MC));
		MalCapitalCost mcc = malCapitalCostDAO.findByQmdIdAndElementId(targetElement.getQuotationModel().getQmdId(), targetElement
				.getDealerAccessory().getDacId());
		if(mcc != null){
			eleAmendmentDetailVO.setDealCost(mcc.getTotalPrice());
		}
		if(eleAmendmentDetailVO.getClientCost().compareTo(BigDecimal.ZERO) == 0){
			eleAmendmentDetailVO.setRental(BigDecimal.ZERO);
		}
		//end RC-1963 change
		if (eleAmendmentDetailVO.getRental() != null && eleAmendmentDetailVO.getRental().compareTo(BigDecimal.ZERO) == 0) {
			if (eleAmendmentDetailVO.getRechargeAmt() != null) {
				// set string as recharged
				eleAmendmentDetailVO.setRechargeText(RECHARGED_TEXT);
			}
		}
		if (!seeIfRemoved && !isMatchFound && !isFormalExt) {
			if (eleAmendmentDetailVO != null) {
				eleAmendmentDetailVO.setAmendmentTypeInd(AMEND_IND_ADDED);
				return eleAmendmentDetailVO;
			}
		} else {
			if (seeIfRemoved && !isMatchFound && !isFormalExt) {
				if (eleAmendmentDetailVO != null) {
					eleAmendmentDetailVO.setAmendmentTypeInd(AMEND_IND_REMOVED);
					return eleAmendmentDetailVO;
				}
			}
		}
		return null;

	}

	Comparator<EleAmendmentDetailVO> eleAmendmentDetailVOComparator = new Comparator<EleAmendmentDetailVO>() {
		public int compare(EleAmendmentDetailVO r1, EleAmendmentDetailVO r2) {
			int compareResult = 0;
			String desc1 = r1.getElementDesc();
			String desc2 = r2.getElementDesc();
			if (desc2 == null) {
				compareResult = 1;
			} else {
				compareResult = desc1.compareTo(desc2);
			}
			if (compareResult != 0) {
				return compareResult;
			} else {
				String symbol1 = r1.getAmendmentTypeInd();
				String symbol2 = r2.getAmendmentTypeInd();
				compareResult = symbol2.compareTo(symbol1);
				if (compareResult != 0) {
					return compareResult;
				} else {
					BigDecimal rental1 = r1.getTotalRental();
					BigDecimal rental2 = r2.getTotalRental();
					if (rental1 != null && rental2 != null) {
						compareResult = rental1.compareTo(rental2);
					}
				}
			}
			return compareResult;
		}
	};

	Comparator<ContractLine> contractLineComparator = new Comparator<ContractLine>() {
		public int compare(ContractLine r1, ContractLine r2) {
			Long clnId1 = r1.getClnId();
			Long clnId2 = r2.getClnId();
			return clnId1.compareTo(clnId2);
		}
	};
	
	Comparator<InformalAmendment> informalAmendmentComparator = new Comparator<InformalAmendment>(){
		public int compare(InformalAmendment r1, InformalAmendment r2) {
			Date effFromDate1 = r1.getEffectiveFrom();
			Date effFromDate2 = r2.getEffectiveFrom();
			return effFromDate1.compareTo(effFromDate2);
		}
	};

	private EleAmendmentDetailVO compareAndReturnServiceAmendmentDetails(QuotationElement targetServiceElement,
			List<QuotationElement> listToCampare, Boolean seeIfRemoved,String contractSource) {
		boolean isMatchFound = false;
		EleAmendmentDetailVO eleAmendmentDetailVO = new EleAmendmentDetailVO();
		for (QuotationElement quotationElement : listToCampare) {
			// compare for element type
			if (quotationElement.getLeaseElement() != null
					&& !quotationElement.getLeaseElement().getElementType().equals(MalConstants.FINANCE_ELEMENT)) {
				if (targetServiceElement.getLeaseElement().getLelId() == quotationElement.getLeaseElement().getLelId()) {
					if(CONTRACT_SOURCE_FORMAL.equals(contractSource)){
						//no need to check for price change if formal ext
						return null;
					}
					BigDecimal targetElementCost = targetServiceElement.getRental() != null ? targetServiceElement.getRental()
							: BigDecimal.ZERO;
					BigDecimal compareElementCost = quotationElement.getRental() != null ? quotationElement.getRental() : BigDecimal.ZERO;

					BigDecimal targetEleRental = BigDecimal.ZERO;
					if (targetElementCost.compareTo(BigDecimal.ZERO) != 0) {
						BigDecimal rentalPeriods;
						if (targetServiceElement.getNoRentals() != null && targetServiceElement.getNoRentals() != BigDecimal.ZERO) {
							rentalPeriods = targetServiceElement.getNoRentals();
						} else {
							rentalPeriods = new BigDecimal(targetServiceElement.getQuotationModel().getContractPeriod());
						}
						targetEleRental = targetElementCost.divide(rentalPeriods, 3, BigDecimal.ROUND_HALF_UP);
					}
					BigDecimal compEleRental = BigDecimal.ZERO;
					if (compareElementCost.compareTo(BigDecimal.ZERO) != 0) {
						BigDecimal rentalPeriods;
						if (quotationElement.getNoRentals() != null && quotationElement.getNoRentals() != BigDecimal.ZERO) {
							rentalPeriods = quotationElement.getNoRentals();
						} else {
							rentalPeriods = new BigDecimal(quotationElement.getQuotationModel().getContractPeriod());
						}
						compEleRental = compareElementCost.divide(rentalPeriods, 3, BigDecimal.ROUND_HALF_UP);
					}

					if (targetEleRental.compareTo(compEleRental) == 0) {
						isMatchFound = true;
						break;
					} else {
						// cost change only
						if (!seeIfRemoved) {
							isMatchFound = false;
							break;
						}
					}

				}
			}
		}
		if (!seeIfRemoved && !isMatchFound) {
			eleAmendmentDetailVO = prepareEleAmendmentVO(targetServiceElement);
			if (eleAmendmentDetailVO != null) {
				eleAmendmentDetailVO.setAmendmentTypeInd(AMEND_IND_ADDED);
				return eleAmendmentDetailVO;
			}
		} else {
			if (seeIfRemoved && !isMatchFound) {
				eleAmendmentDetailVO = prepareEleAmendmentVO(targetServiceElement);
				if (eleAmendmentDetailVO != null) {
					eleAmendmentDetailVO.setAmendmentTypeInd(AMEND_IND_REMOVED);
					return eleAmendmentDetailVO;
				}
			}
		}
		return null;

	}

	private AmendmentHistoryVO compareQuotesAndReturnDetails(QuotationModel firstQuote, QuotationModel secondQuote,
			Boolean showDealAmendment, Boolean showServiceEleAmendment, String contractSource) {
		List<QuotationElement> quotationElementsToCompareFrom = firstQuote.getQuotationElements();
		List<QuotationElement> targetQuotationElements = secondQuote.getQuotationElements();
		EleAmendmentDetailVO eleAmendmentDetailVO = null;
		AmendmentHistoryVO amendmentHistoryVO = new AmendmentHistoryVO();
		amendmentHistoryVO.setQmdId(secondQuote.getQmdId());
		amendmentHistoryVO.setQuote(Long.toString(secondQuote.getQuotation().getQuoId()) + "/" + Long.toString(secondQuote.getQuoteNo())
				+ "/" + Long.toString(secondQuote.getRevisionNo()));

		amendmentHistoryVO.setEffectivePeriod(secondQuote.getContractChangeEventPeriod());
		amendmentHistoryVO.setCreatedBy(secondQuote.getLastAmendedUser());

		if (showDealAmendment) {
			// added
			for (QuotationDealerAccessory quotationDealerAccessory : secondQuote.getQuotationDealerAccessories()) {
				eleAmendmentDetailVO = compareAndReturnAfterMarketAmendmentDetail(quotationDealerAccessory,
						firstQuote.getQuotationDealerAccessories(), false, contractSource, secondQuote.getContractPeriod());
				if (eleAmendmentDetailVO != null)
					amendmentHistoryVO.getAfterMarketEquipments().add(eleAmendmentDetailVO);
			}
			// removed case
			for (QuotationDealerAccessory quotationDealerAccessory : firstQuote.getQuotationDealerAccessories()) {
				eleAmendmentDetailVO = compareAndReturnAfterMarketAmendmentDetail(quotationDealerAccessory,
						secondQuote.getQuotationDealerAccessories(), true, contractSource, firstQuote.getContractPeriod());
				if (eleAmendmentDetailVO != null)
					amendmentHistoryVO.getAfterMarketEquipments().add(eleAmendmentDetailVO);
			}

		}
		if (showServiceEleAmendment) {
			for (QuotationElement mainQuotationElement : targetQuotationElements) {
				// compare service elements
				if (mainQuotationElement.getLeaseElement() != null
						&& !mainQuotationElement.getLeaseElement().getElementType().equals(MalConstants.FINANCE_ELEMENT)) {
					eleAmendmentDetailVO = compareAndReturnServiceAmendmentDetails(mainQuotationElement, quotationElementsToCompareFrom,
							false,contractSource);
					if (eleAmendmentDetailVO != null)
						amendmentHistoryVO.getServiceElements().add(eleAmendmentDetailVO);
				}

			}
			// check if removed
			eleAmendmentDetailVO = null;
			for (QuotationElement mainQuotationElement : quotationElementsToCompareFrom) {
				// compare service elements
				if (mainQuotationElement.getLeaseElement() != null
						&& !mainQuotationElement.getLeaseElement().getElementType().equals(MalConstants.FINANCE_ELEMENT)) {
					eleAmendmentDetailVO = compareAndReturnServiceAmendmentDetails(mainQuotationElement, targetQuotationElements, true,contractSource);
					if (eleAmendmentDetailVO != null)
						amendmentHistoryVO.getServiceElements().add(eleAmendmentDetailVO);
				}

			}
		}
		return amendmentHistoryVO;
	}

	@Transactional(readOnly = true)
	public List<AmendmentHistoryVO> getAmendedQuotesWithAmendmentDetail(Long qmdId, Boolean showDealAmendment,
			Boolean showServiceEleAmendment) throws MalBusinessException {
		List<AmendmentHistoryVO> amendmentHistoryList = new ArrayList<AmendmentHistoryVO>();
		try {
			if (qmdId != null) {
				showDealAmendment = showDealAmendment != null ? showDealAmendment : false;
				showServiceEleAmendment = showServiceEleAmendment != null ? showServiceEleAmendment : false;
				List<AmendmentHistoryVO> amendedQuotes = getAmendedQuotes(qmdId, showDealAmendment, showServiceEleAmendment);
				if (amendedQuotes != null && !amendedQuotes.isEmpty()) {
					amendmentHistoryList.addAll(amendedQuotes);
				}
			}
		} catch (Exception ex) {
			if (ex instanceof MalBusinessException) {
				throw (MalBusinessException) ex;

			}
			throw new MalBusinessException("generic.error.occured.while", new String[] { "getting amendment history" }, ex);
		}
		return amendmentHistoryList;

	}

	public List<ContractLine> getApplicableContractLinesForHistory(QuotationModel inComingQuotationModel) {
		String FORMAL_EXT  = "Formal" ;
		Long fmsId = null;
		String unitNo = null;
		if (MALUtilities.isEmpty(inComingQuotationModel.getUnitNo())) {
			List<QuotationModel> list = quotationModelDAO.findByQuoteId(inComingQuotationModel.getQuotation().getQuoId());
			for (QuotationModel quotationModel : list) {
				if (!MALUtilities.isEmpty(quotationModel.getUnitNo())) {
					unitNo = quotationModel.getUnitNo();
					break;

				}
			}
		} else {
			unitNo = inComingQuotationModel.getUnitNo();
		}
		FleetMaster fleetMaster = fleetMasterService.findByUnitNo(unitNo);
		if (fleetMaster != null) {
			fmsId = fleetMaster.getFmsId();
		}
		if (fmsId == null) {
			return null;
		}
		List<ContractLine> applicableContractLines = new ArrayList<ContractLine>();
		List<ContractLine> contractLines = contractLineDAO.findByFmsId(fleetMaster.getFmsId());
		if (contractLines == null || contractLines.isEmpty())
			return null;
		Collections.sort(contractLines, contractLineComparator);
		List<String> processedContractNos = new ArrayList<String>();
		boolean isDifferentContract = false;
		for (ContractLine contractLine : contractLines) {
			if (contractLine.getEarlyTermQuoteId() != null) {
				// a ET, exclude it
				continue;
			}
			if (processedContractNos.contains(contractLine.getContract().getContractNo())) {
				continue;
			} else {
				processedContractNos.add(contractLine.getContract().getContractNo());
				if (processedContractNos.size() > 1) {
					isDifferentContract = true;
				}
				if( !isDifferentContract ){
					applicableContractLines.add(contractLine);
				}

			}
			if(isDifferentContract){
				List<QuotationModel> allQuotes = quotationModelDAO.findQuotesByUnitNoAndContract(fleetMaster.getUnitNo(), contractLine
						.getContract().getConId());
				String contarctDesc = contractLine.getContract().getDescription();
				contarctDesc = MALUtilities.isEmpty(contarctDesc) ? "" : contarctDesc;
				if(contarctDesc.startsWith(FORMAL_EXT)){
					//formal extension
					for (QuotationModel quotationModel : allQuotes) {
						if (quotationModel.getQmdId().compareTo(inComingQuotationModel.getQmdId()) == 0) {
							// one of the quotation models from formal extension
							// quote
							if(!applicableContractLines.contains(contractLine))
								applicableContractLines.add(contractLine);
						} else {
							if (quotationModel.getContractLine() != null) {
								ContractLine prevContractLine = applicableContractLines.get(applicableContractLines.size() - 1);
								if (quotationModel.getContractLine().getContract().getContractNo()
										.equals(prevContractLine.getContract().getContractNo())) {
									if(!applicableContractLines.contains(contractLine))
										applicableContractLines.add(contractLine);
								}
							}
						}
					}
				}else{
					//re-lease,Wipe off all and start off fresh from here
					for (QuotationModel quotationModel : allQuotes) {
						if (quotationModel.getQmdId().compareTo(inComingQuotationModel.getQmdId()) == 0) {
							//one of the quotation models from released quote
							applicableContractLines.clear();
							processedContractNos.clear();
							processedContractNos.add(contractLine.getContract().getContractNo());
							applicableContractLines.add(contractLine);
						}else{
							List<ContractLine> lines = contractLineDAO.findByQmdId(inComingQuotationModel.getQmdId());
							for (ContractLine contractLine2 : lines) {
								if(contractLine2.getClnId().compareTo(contractLine.getClnId())>= 0){
									//one of the quotation models from released quote
									applicableContractLines.clear();
									processedContractNos.clear();
									processedContractNos.add(contractLine.getContract().getContractNo());
									applicableContractLines.add(contractLine);
									break;
								}
								
							}
						}
					}
				}
			}
		}
		return applicableContractLines;

	}

	@Transactional(readOnly = true)
	public List<AmendmentHistoryVO> getAmendedQuotes(Long qmdId, Boolean showDealAmendment, Boolean showServiceEleAmendment)
			throws MalBusinessException {
		List<AmendmentHistoryVO> preparedList = new ArrayList<AmendmentHistoryVO>();
		QuotationModel finalizedQuotationModel = null;
		boolean isFinalizedQuoteProcessed = false;
		QuotationModel firstQuote = null;
		QuotationModel inComingQuotationModel = quotationModelDAO.findById(qmdId).orElse(null);

		List<ContractLine> contractLines = getApplicableContractLinesForHistory(inComingQuotationModel);
		if (contractLines == null || contractLines.isEmpty())
			return null;
		
		for (ContractLine contractLine : contractLines) {
			List<QuotationModel> allQuotes = quotationModelDAO.findQuotesByUnitNoAndContract(inComingQuotationModel.getUnitNo(),
					contractLine.getContract().getConId());
			if (allQuotes.isEmpty()) {
				continue;
			}
			try {
				if (finalizedQuotationModel == null) {
					for (QuotationModel quotationModel : allQuotes) {
						if (quotationModel.getUnitNo() != null) {
							FleetMaster fleetMasterTemp = fleetMasterService.findByUnitNo(quotationModel.getUnitNo());
							if (fleetMasterTemp != null) {
								if (isQuoteFinalized(fleetMasterTemp, quotationModel)) {
									finalizedQuotationModel = quotationModel;
									break;
								}
							}
						}
					}
				}
				if (finalizedQuotationModel == null) {
					return null;
				}
				allQuotes = null;
				allQuotes = quotationModelDAO.findNewerQuotesByQuotation(finalizedQuotationModel.getQmdId(), contractLine.getContract()
						.getConId());
				
				AmendmentHistoryVO amendmentHistoryVO = null;
				if (!isFinalizedQuoteProcessed) {
					EleAmendmentDetailVO eleAmendmentDetailVO = null;
					amendmentHistoryVO = new AmendmentHistoryVO();
					amendmentHistoryVO.setQmdId(finalizedQuotationModel.getQmdId());
					amendmentHistoryVO.setQuote(Long.toString(finalizedQuotationModel.getQuotation().getQuoId()) + "/"
							+ Long.toString(finalizedQuotationModel.getQuoteNo()) + "/"
							+ Long.toString(finalizedQuotationModel.getRevisionNo()));
					amendmentHistoryVO.setAmendmentSource("O");
					amendmentHistoryVO.setEffectivePeriod(finalizedQuotationModel.getContractChangeEventPeriod());
					amendmentHistoryVO.setCreatedBy(finalizedQuotationModel.getQuotation().getUsername());
					
					for (QuotationElement quotationElement : finalizedQuotationModel.getQuotationElements()) {
						if (quotationElement.getQuotationDealerAccessory() != null) {
							eleAmendmentDetailVO = prepareEleAmendmentVO(quotationElement);
							// set re charge amount
							eleAmendmentDetailVO.setRechargeAmt(quotationElement.getQuotationDealerAccessory().getRechargeAmount());
							eleAmendmentDetailVO.setAmendmentTypeInd(AMEND_IND_ADDED);
							amendmentHistoryVO.getAfterMarketEquipments().add(eleAmendmentDetailVO);
						} else if (quotationElement.getLeaseElement() != null
								&& !quotationElement.getLeaseElement().getElementType().equals(MalConstants.FINANCE_ELEMENT)) {
							eleAmendmentDetailVO = prepareEleAmendmentVO(quotationElement);
							eleAmendmentDetailVO.setAmendmentTypeInd(AMEND_IND_ADDED);
							amendmentHistoryVO.getServiceElements().add(eleAmendmentDetailVO);
						}
					}
					if (showDealAmendment) {
						setCostLeaseRateAndInvoice(finalizedQuotationModel, amendmentHistoryVO);
						//QuoteCost quoteCost = capitalCostOverviewService.getQuoteCost(finalizedQuotationModel);
						//amendmentHistoryVO.setDealCost(quoteCost.getDealCost());
						//amendmentHistoryVO.setCustomerCost(quoteCost.getCustomerCost());
					}
					amendmentHistoryVO.setEffectiveDate(contractLine.getStartDate());
					Collections.sort(amendmentHistoryVO.getAfterMarketEquipments(), eleAmendmentDetailVOComparator);
					Collections.sort(amendmentHistoryVO.getServiceElements(), eleAmendmentDetailVOComparator);
					if (!showDealAmendment) {
						preparedList.add(amendmentHistoryVO);
					}
					isFinalizedQuoteProcessed = true;
					firstQuote = finalizedQuotationModel;
					if(showServiceEleAmendment){
						List<AmendmentHistoryVO> ifaList = getInformalAmendments(finalizedQuotationModel);
						if(ifaList != null && !ifaList.isEmpty())
							preparedList.addAll(ifaList);
					}
					
				}

				for (QuotationModel quotationModel : allQuotes) {
					String contractSource = getContractSource(quotationModel, contractLine);
					boolean isAmendmentExistsOnQuote = MALUtilities.isEmpty(contractSource) ? false : contractSource
							.equals(CONTRACT_SOURCE_AMENDMENT) || contractSource.equals(CONTRACT_SOURCE_FORMAL) ? true : false;
					boolean isRevisionOnQuote = MALUtilities.isEmpty(contractSource) ? false : contractSource.equals(CONTRACT_SOURCE_REVISION) ;
					if (isAmendmentExistsOnQuote || isRevisionOnQuote) {

						amendmentHistoryVO = null;
						amendmentHistoryVO = new AmendmentHistoryVO();

						amendmentHistoryVO = compareQuotesAndReturnDetails(firstQuote, quotationModel, showDealAmendment,showServiceEleAmendment, contractSource);
						amendmentHistoryVO.setAmendmentSource(contractSource);
						
						if (showDealAmendment) {
							setCostLeaseRateAndInvoice(quotationModel, amendmentHistoryVO);
						}

						
						if (CONTRACT_SOURCE_AMENDMENT.equals(contractSource)) {
							amendmentHistoryVO.setEffectiveDate(quotationModel.getAmendmentEffectiveDate());
						}else if (CONTRACT_SOURCE_FORMAL.equals(contractSource)) {
							List<ContractLine> contractLinesFormalExt = contractLineDAO.findByQmdId(quotationModel.getQmdId());
							amendmentHistoryVO.setEffectiveDate(contractLinesFormalExt.get(0).getStartDate());
						}else if (CONTRACT_SOURCE_REVISION.equals(contractSource)) {
							
							String leaseType = quotationService.getLeaseType(quotationModel.getQmdId());
							 if (leaseType.equals(QuotationService.OPEN_END_LEASE)) {	
								 List<EleAmendmentDetailVO> list = new ArrayList<EleAmendmentDetailVO>();
								 EleAmendmentDetailVO vo = null;
								 
								 for(QuotationCapitalElement qce : quotationModel.getQuotationCapitalElements()){
									 if(qce.getCapitalElement().getCode().equalsIgnoreCase(CapitalElementService.OE_REV_ASSMNT) 
											 && qce.getValue() != null && qce.getValue().compareTo(BigDecimal.ZERO) != 0){
										 vo = new EleAmendmentDetailVO();
										 vo.setElementDesc(DISPLAY_NAME_REV_ASSESSMENT);
										 vo.setDealCost(BigDecimal.ZERO);
										 vo.setClientCost(qce.getValue());
										 list.add(vo);
									 }else if(qce.getCapitalElement().getCode().equalsIgnoreCase(CapitalElementService.OE_REV_INT_ADJ) 
											 && qce.getValue() != null && qce.getValue().compareTo(BigDecimal.ZERO) != 0){
										 vo = new EleAmendmentDetailVO();
										 vo.setElementDesc(DISPLAY_NAME_REV_INT_ADJ);
										 vo.setDealCost(BigDecimal.ZERO);
										 vo.setClientCost(qce.getValue());
										 list.add(vo);
									 }else if(qce.getCapitalElement().getCode().equalsIgnoreCase(CapitalElementService.OE_INV_ADJ) 
											 && qce.getValue() != null && qce.getValue().compareTo(BigDecimal.ZERO) != 0){
										 vo = new EleAmendmentDetailVO();
										 vo.setElementDesc(DISPLAY_NAME_REV_INVOICE_ADJ);
										 vo.setDealCost(BigDecimal.ZERO);
										 vo.setClientCost(qce.getValue());
										 list.add(vo);
									 }
								 }
								 
								 if(quotationModel.getCapitalContribution() != null && quotationModel.getCapitalContribution().compareTo(BigDecimal.ZERO) != 0){
									 vo = new EleAmendmentDetailVO();
									 vo.setElementDesc(DISPLAY_NAME_REV_CAP_CONTR);								
									 vo.setClientCost(quotationModel.getCapitalContribution().negate());
									 vo.setDealCost(vo.getClientCost());
									 list.add(vo);
								 }
								 amendmentHistoryVO.setRevisionElements(list);
								 amendmentHistoryVO.getServiceElements().clear();
								 
								 amendmentHistoryVO.setOERevision(true);;
									
							 }
							 
							amendmentHistoryVO.getAfterMarketEquipments().clear();
							amendmentHistoryVO.setEffectiveDate(quotationModel.getAmendmentEffectiveDate());
							
							// get modified service elements for revision
							List<QuotationElement> quotationElementsToCompareFrom = firstQuote.getQuotationElements();
							List<QuotationElement> targetQuotationElements = quotationModel.getQuotationElements();
							EleAmendmentDetailVO eleAmendmentDetailVO = null;
							
							for (QuotationElement mainQuotationElement : targetQuotationElements) {
								// compare service elements
								if (mainQuotationElement.getLeaseElement() != null
										&& !mainQuotationElement.getLeaseElement().getElementType().equals(MalConstants.FINANCE_ELEMENT)) {
									eleAmendmentDetailVO = compareAndReturnServiceAmendmentDetails(mainQuotationElement, quotationElementsToCompareFrom,
											false,contractSource);
									if (eleAmendmentDetailVO != null)
										amendmentHistoryVO.getServiceElements().add(eleAmendmentDetailVO);
								}
							}
						}
						
						Collections.sort(amendmentHistoryVO.getAfterMarketEquipments(), eleAmendmentDetailVOComparator);
						Collections.sort(amendmentHistoryVO.getServiceElements(), eleAmendmentDetailVOComparator);
						firstQuote = null;
						firstQuote = quotationModel;
						preparedList.add(amendmentHistoryVO);
						if(showServiceEleAmendment){
							//check if informal amendment is done on this quotation model
							List<AmendmentHistoryVO> ifaList = getInformalAmendments(quotationModel);
							if(ifaList != null && !ifaList.isEmpty())
								preparedList.addAll(ifaList);
						}
						
					}else{
						//may be revision quote or informal amendment
						if(showServiceEleAmendment){
							if(!CONTRACT_SOURCE_EARLY_TERMINATE.equals(contractSource)){
								List<AmendmentHistoryVO> ifaList = getInformalAmendments(quotationModel);
								if(ifaList != null && !ifaList.isEmpty())
									preparedList.addAll(ifaList);
							}
						}
						
					}
				}
			} catch (Exception ex) {
				logger.error(ex);
				throw new MalBusinessException("generic.error.occured.while", new String[] { "getting amendment history" }, ex);
			}
		}
		
		return preparedList;
	}
	
	private List<AmendmentHistoryVO>	getInformalAmendments(QuotationModel quotationModel){
		List<AmendmentHistoryVO>	list = new ArrayList<AmendmentHistoryVO>();
		List<InformalAmendment>   informalAmendments =  informalAmendmentDAO.findByQmdId(quotationModel.getQmdId());
		if(informalAmendments == null || informalAmendments.isEmpty()){
			return null;
		}
		Collections.sort(informalAmendments,informalAmendmentComparator);
		
		Date inProcessEffDate = null;
		AmendmentHistoryVO processedVO = new AmendmentHistoryVO();
		AmendmentHistoryVO processedVOToCompare =  new AmendmentHistoryVO();
		boolean addToList = false;
		for (InformalAmendment informalAmendment : informalAmendments) {
			if(inProcessEffDate == null){
				inProcessEffDate	= informalAmendment.getEffectiveFrom();
			}
			LeaseElement leaseElement = leaseElementDAO.findById(informalAmendment.getLelLelId()).orElse(null);
			if(informalAmendment.getEffectiveFrom().compareTo(inProcessEffDate) != 0){
				if(!processedVO.getServiceElements().isEmpty()){
					processedVO.setQmdId(quotationModel.getQmdId());
					processedVO.setAmendmentSource("I");
					processedVO.setQuote(Long.toString(quotationModel.getQuotation().getQuoId()) + "/" + Long.toString(quotationModel.getQuoteNo())
							+ "/" + Long.toString(quotationModel.getRevisionNo()));
					list.add(processedVO);
					processedVO.setEffectiveDate(inProcessEffDate);
					processedVOToCompare = processedVO;
					addToList	= false;
					processedVO = new AmendmentHistoryVO();
				}
			}					
				boolean foundInPreviousAmendment = false;
				for (EleAmendmentDetailVO eleAmendmentDetailVOProcessed : processedVOToCompare.getServiceElements()) {
					String addRemoveIndInV0 = eleAmendmentDetailVOProcessed.getAmendmentTypeInd().equals("+") ? "A" : "R";
					if (leaseElement.getDescription().equals(eleAmendmentDetailVOProcessed.getElementDesc())
							&& informalAmendment.getAddRemove().equals(addRemoveIndInV0)) {
						foundInPreviousAmendment = true;
					}
				}
				if(!foundInPreviousAmendment){
					String indicator = INFORMAL_AMEND_ADD.equals(informalAmendment.getAddRemove()) ? AMEND_IND_ADDED
							: INFORMAL_AMEND_REMOVE.equals(informalAmendment.getAddRemove()) ? AMEND_IND_REMOVED
									: null;
					//check previous VOs
					boolean notExistInAnyPreviousVos = false;
					for (AmendmentHistoryVO amendmentHistoryVOTemp : list) {
						for (EleAmendmentDetailVO eleAmendmentDetailVOTemp : amendmentHistoryVOTemp.getServiceElements()) {
							if(eleAmendmentDetailVOTemp.getElementDesc().equals(leaseElement.getDescription()) &&
									eleAmendmentDetailVOTemp.getAmendmentTypeInd().equals(indicator)){
								notExistInAnyPreviousVos = true;
								break;
							}
						}
					}
					if(!notExistInAnyPreviousVos){
						EleAmendmentDetailVO eleAmendmentDetailVO = new EleAmendmentDetailVO();
						
						eleAmendmentDetailVO.setElementDesc(leaseElement.getDescription());
						eleAmendmentDetailVO.setAmendmentTypeInd(indicator);
						if(AMEND_IND_ADDED.equals(indicator)){
							eleAmendmentDetailVO.setRental(informalAmendment.getBillingAmt());
							eleAmendmentDetailVO.setTotalRental(informalAmendment.getRental());
						}
						eleAmendmentDetailVO.setEffectiveDate(informalAmendment.getEffectiveFrom());
						eleAmendmentDetailVO.setElementType(ELEMENT_TYPE_SERVICE);
						processedVO.getServiceElements().add(eleAmendmentDetailVO);
						addToList	= true;
					}
					
				}
			inProcessEffDate	= informalAmendment.getEffectiveFrom();
		}
		if(addToList){
			processedVO.setQmdId(quotationModel.getQmdId());
			processedVO.setAmendmentSource("I");
			processedVO.setQuote(Long.toString(quotationModel.getQuotation().getQuoId()) + "/" + Long.toString(quotationModel.getQuoteNo())
					+ "/" + Long.toString(quotationModel.getRevisionNo()));
			processedVO.setEffectiveDate(inProcessEffDate);
			list.add(processedVO);
		}
		return list;
	}

	private void setCostLeaseRateAndInvoice(QuotationModel quotationModel, AmendmentHistoryVO amendmentHistoryVO)
			throws MalBusinessException {
		// get total cost
		String leaseType = quotationService.getLeaseType(quotationModel.getQmdId());
		QuoteCost quoteCost = capitalCostOverviewService.getQuoteCost(quotationModel);
		amendmentHistoryVO.setDealCost(quoteCost.getDealCost());
		amendmentHistoryVO.setCustomerCost(quoteCost.getCustomerCost());
		// get lease rate
		BigDecimal leaseRate = null;
		if (leaseType.equals(QuotationService.CLOSE_END_LEASE)) {
			leaseRate = rentalCalculationService.getFinanceLeaseElementCostForCE(quotationModel);
		} else if (leaseType.equals(QuotationService.OPEN_END_LEASE)) {
			leaseRate = getLeaseRateOE(quotationModel);
		}
		amendmentHistoryVO.setLeaseRate(leaseRate);
		setInvoiceAndPoDetails(quotationModel, amendmentHistoryVO);
	}

	public BigDecimal getLeaseRateOE(QuotationModel quotationModel) {
		BigDecimal leaseRate = BigDecimal.ZERO;
		if (quotationModel != null) {
			for (QuotationElement qe : quotationModel.getQuotationElements()) {
				if (qe.getQuotationElementSteps() != null && !qe.getQuotationElementSteps().isEmpty()) {
					Collections.sort(qe.getQuotationElementSteps(), quotationElementStepComparator);
					BigDecimal tempRental = qe.getQuotationElementSteps().get(0).getRentalValue();
					if (tempRental != null) {
						leaseRate = leaseRate.add(tempRental, CommonCalculations.MC);
					}

				}
			}
		}
		return leaseRate;
	}

	Comparator<QuotationElementStep> quotationElementStepComparator = new Comparator<QuotationElementStep>() {
		public int compare(QuotationElementStep r1, QuotationElementStep r2) {
			if (r1.getFromPeriod() == null && r2.getFromPeriod() == null) {
				return 0;
			} else if (r1.getFromPeriod() == null && r2.getFromPeriod() != null) {
				return -1;
			} else if (r1.getFromPeriod() != null && r2.getFromPeriod() == null) {
				return 1;
			} else {
				return (r1.getFromPeriod().compareTo(r2.getFromPeriod()));
			}
		}
	};


	private void setInvoiceAndPoDetails(QuotationModel quotationModel, AmendmentHistoryVO amendmentHistoryVO) {

		String unitNo = null;
		Long fmsId = null;
		if (quotationModel != null) {
			unitNo = quotationModel.getUnitNo();
		}
		if (unitNo != null) {
			FleetMaster fm = fleetMasterDAO.findByUnitNo(unitNo);
			if (fm != null) {
				fmsId = fm.getFmsId();
			}
		}

		if (fmsId != null) {

			List<Doc> docList = docDAO.getReleasedMaintenancePODocByFmsId(fmsId);
			List<Long> mrqList = new ArrayList<Long>();
			for (Doc doc : docList) {
				mrqList.add(doc.getGenericExtId());
			}
			
			List<Long> parentDocIdList =  new ArrayList<Long>();
			for (Doc doc : docList) {
				parentDocIdList.add(doc.getDocId());
			}		
			List<Long> childDocIdList = new ArrayList<Long>();
			if(parentDocIdList != null && parentDocIdList.size() > 0){
				childDocIdList = docLinkDao.findByParentDocIds(parentDocIdList);
			}
			
			if (mrqList.size() > 0) {
				List<MaintenanceRequestTask> maintenanceRequestTaskList = maintenanceRequestTaskDAO.getMaintRequestTasksByMrqIdList(mrqList);

				for (MaintenanceRequestTask maintenanceRequestTask : maintenanceRequestTaskList) {
					for (EleAmendmentDetailVO vo : amendmentHistoryVO.getAfterMarketEquipments()) {
						// Pull invoice info  start
						if(childDocIdList != null && childDocIdList.size() > 0){
							vo.setInvoiceAmt(doclDao.findInvoiceForAmendedEquipmenet(fmsId, vo.getElementId().longValue(), childDocIdList));
						}						
						// Pull PO info start
						if (maintenanceRequestTask.getDacDacId() != null && vo.getElementId().longValue() == maintenanceRequestTask.getDacDacId().longValue()) {
							for (Doc doc : docList) {
								if (doc.getGenericExtId().longValue() == maintenanceRequestTask.getMaintenanceRequest().getMrqId().longValue()) {
									vo.setPoNumber(doc.getDocNo());
									vo.setPoOrderDate(doc.getPostedDate());
									if (doc.getEaCId() != null && doc.getAccountType() != null && doc.getAccountCode() != null) {
										ExternalAccountPK extPK = new ExternalAccountPK(doc.getEaCId().longValue(), doc.getAccountType(),doc.getAccountCode());
										ExternalAccount externalAccount = externalAccountDAO.findById(extPK).orElse(null);
										if (externalAccount != null) {
											vo.setAccountInfo(externalAccount.getExternalAccountPK().getAccountCode() + " "+ externalAccount.getAccountName());

										}
									}
									break;

								}
							}
							break;
						}// Pull PO info end
						
					}
				}
			}
		}

	}

	private EleAmendmentDetailVO prepareEleAmendmentVO(QuotationElement quotationElement) {
		EleAmendmentDetailVO eleAmendmentDetailVO = null;
		if (quotationElement.getQuotationDealerAccessory() != null) {
			eleAmendmentDetailVO = new EleAmendmentDetailVO();
			eleAmendmentDetailVO.setElementType(ELEMENT_TYPE_DEALER);
			List<QuotationElementStep> qesList = quotationElement.getQuotationElementSteps();
			BigDecimal rental = BigDecimal.ZERO;
			if (qesList != null && !qesList.isEmpty()) {

				for (QuotationElementStep quotationElementStep : qesList) {
					if (quotationElementStep.getRentalValue() != null) {
						rental = rental.add(quotationElementStep.getRentalValue(), CommonCalculations.MC);
					}
				}
				eleAmendmentDetailVO.setRental(rental);
			}
			if (eleAmendmentDetailVO.getRental() != null && eleAmendmentDetailVO.getRental().compareTo(BigDecimal.ZERO) == 0) {
				if (quotationElement.getQuotationDealerAccessory().getRechargeAmount() != null) {
					// set string as recharged
					eleAmendmentDetailVO.setRechargeText(RECHARGED_TEXT);
				}
			}
			eleAmendmentDetailVO.setRechargeAmt(quotationElement.getQuotationDealerAccessory().getRechargeAmount());
			eleAmendmentDetailVO.setNoRentals(quotationElement.getNoRentals());
			eleAmendmentDetailVO.setTotalRental(quotationElement.getCapitalCost());
			eleAmendmentDetailVO.setElementId(quotationElement.getQuotationDealerAccessory().getDealerAccessory().getDacId());
			eleAmendmentDetailVO.setElementDesc(quotationElement.getQuotationDealerAccessory().getDealerAccessory()
					.getDealerAccessoryCode().getDescription());
			eleAmendmentDetailVO.setInRateTreatment(MALUtilities.convertYNToBoolean(quotationElement.getLeaseElement().getInRateTreatmentYn()));
			return eleAmendmentDetailVO;

		} else if (quotationElement.getLeaseElement() != null
				&& !quotationElement.getLeaseElement().getElementType().equals(MalConstants.FINANCE_ELEMENT)) {
			eleAmendmentDetailVO = new EleAmendmentDetailVO();
			eleAmendmentDetailVO.setElementType(ELEMENT_TYPE_SERVICE);
			BigDecimal rental = BigDecimal.ZERO;
			if (quotationElement.getRental() != null) {
				BigDecimal rentalPeriods;
				if (quotationElement.getNoRentals() != null && quotationElement.getNoRentals() != BigDecimal.ZERO) {
					rentalPeriods = quotationElement.getNoRentals();
				} else {
					rentalPeriods = new BigDecimal(quotationElement.getQuotationModel().getContractPeriod());
				}
				rental = quotationElement.getRental().divide(rentalPeriods, 2, BigDecimal.ROUND_HALF_UP);
			}
			eleAmendmentDetailVO.setRental(rental);
			eleAmendmentDetailVO.setNoRentals(quotationElement.getNoRentals());
			eleAmendmentDetailVO.setTotalRental(quotationElement.getRental());
			eleAmendmentDetailVO.setElementDesc(quotationElement.getLeaseElement().getDescription());
			eleAmendmentDetailVO.setInRateTreatment(MALUtilities.convertYNToBoolean(quotationElement.getLeaseElement().getInRateTreatmentYn()));
			// get overridden finance parameters and set in VO
			List<FinanceParameterVO> finPlist = null;
			List<FormulaParameter> fpList = financeParameterService.getFormulaParametersForElement(quotationElement, "F");
			finPlist = new ArrayList<FinanceParameterVO>();
			for (FormulaParameter formulaParameter : fpList) {
				if (!MALUtilities.isEmpty(formulaParameter.getParameterName()) && quotationElement.getQuotationModel() != null) {
					FinanceParameterVO financeParameterVO = new FinanceParameterVO();
					QuotationModelFinances qmf = financeParameterService.getQuotationModelFinances(quotationElement.getQuotationModel()
							.getQmdId(), formulaParameter.getParameterName());
					financeParameterVO.setQuotationModelFinances(qmf);
					if (qmf != null) {
						financeParameterVO.setValue(qmf.getnValue());
					}
					finPlist.add(financeParameterVO);
				}

			}
			eleAmendmentDetailVO.setFinanceParameters(finPlist);
			return eleAmendmentDetailVO;
		}
		return null;
	}

	// RC-37 re factor end

}
