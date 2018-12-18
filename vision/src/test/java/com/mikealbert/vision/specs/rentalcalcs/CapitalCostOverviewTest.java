package com.mikealbert.vision.specs.rentalcalcs;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.mikealbert.testing.BaseSpec;
import com.mikealbert.vision.service.CapitalCostOverviewService;
import com.mikealbert.vision.vo.CostElementVO;

public class CapitalCostOverviewTest extends BaseSpec {
	@Resource
	CapitalCostOverviewService capitalCostOverviewService;
	@PersistenceContext
	private EntityManager em;

	private String baseVehicle = "Base Vehicle";
	private String holdBack = "Holdback";
	private String advertising = "Advertising";
	private String financeAssistance1 = "Finance Assistance 1";
	private String malIncentiveMoney = "MAL Incentive Money";
	private String courtesyDeliveryFee = "Courtesy Delivery Fee";
	private String distantDeliveryCredit = "Distant Delivery Credit";
	private String fleetAllowance = "Fleet Allowance";
	private String retailRebate = "Retail Rebate";
	private String locateBaseCostVariance = "Locate Base Cost Variance";
	private String earlyOrderIncentiveOffInvoice = "Early Order Incentive (Off Invoice)";
	private String volumeRelatedBonus = "Volume Related Bonus";
	private String mikeAlbertPriceProtection = "Mike Albert Price Protection";
	private String yearEndCarryover = "Year-end Carryover";
	private String preDeliveryInspection = "Pre-delivery Inspection";
	private String fuelCredit = "Fuel Credit";
	private String lateInTransit = "Late In Transit";
	private String freight = "Freight";
	private String orderingDealerFee = "Ordering Dealer Fee";
	private String openEndInvoiceAdjustment = "Open-End Invoice Adjustment";
	private String customerAssociationDiscount = "Customer Association Discount";
	private String vrbReclaim = "VRB Reclaim";
	private String factoryEquipment = "Factory Equipment";
	private String afterMarketEquipment = "After Market Equipment";

	private BigDecimal baseVehicleStdDeal = new BigDecimal(0);
	private BigDecimal baseVehicleStdcust = new BigDecimal(0);
	private BigDecimal baseVehicleAccDeal = new BigDecimal(0);
	private BigDecimal baseVehicleAccCust = new BigDecimal(0);
	private BigDecimal baseVehicleInv = new BigDecimal(0);
	private BigDecimal baseVehicleFinDeal = new BigDecimal(0);
	private BigDecimal baseVehicleFinCust = new BigDecimal(0);

	private BigDecimal holdBackStdDeal = new BigDecimal(0);
	private BigDecimal holdBackStdcust = new BigDecimal(0);
	private BigDecimal holdBackAccDeal = new BigDecimal(0);
	private BigDecimal holdBackAccCust = new BigDecimal(0);
	private BigDecimal holdBackInv = new BigDecimal(0);
	private BigDecimal holdBackFinDeal = new BigDecimal(0);
	private BigDecimal holdBackFinCust = new BigDecimal(0);

	private BigDecimal advertisingStdDeal = new BigDecimal(0);
	private BigDecimal advertisingStdcust = new BigDecimal(0);
	private BigDecimal advertisingAccDeal = new BigDecimal(0);
	private BigDecimal advertisingAccCust = new BigDecimal(0);
	private BigDecimal advertisingInv = new BigDecimal(0);
	private BigDecimal advertisingFinDeal = new BigDecimal(0);
	private BigDecimal advertisingFinCust = new BigDecimal(0);

	private BigDecimal financeAssistance1StdDeal = new BigDecimal(0);
	private BigDecimal financeAssistance1Stdcust = new BigDecimal(0);
	private BigDecimal financeAssistance1AccDeal = new BigDecimal(0);
	private BigDecimal financeAssistance1AccCust = new BigDecimal(0);
	private BigDecimal financeAssistance1Inv = new BigDecimal(0);
	private BigDecimal financeAssistance1FinDeal = new BigDecimal(0);
	private BigDecimal financeAssistance1FinCust = new BigDecimal(0);

	private BigDecimal malIncentiveMoneyStdDeal = new BigDecimal(0);
	private BigDecimal malIncentiveMoneyStdcust = new BigDecimal(0);
	private BigDecimal malIncentiveMoneyAccDeal = new BigDecimal(0);
	private BigDecimal malIncentiveMoneyAccCust = new BigDecimal(0);
	private BigDecimal malIncentiveMoneyInv = new BigDecimal(0);
	private BigDecimal malIncentiveMoneyFinDeal = new BigDecimal(0);
	private BigDecimal malIncentiveMoneyFinCust = new BigDecimal(0);

	private BigDecimal courtesyDeliveryFeeStdDeal = new BigDecimal(0);
	private BigDecimal courtesyDeliveryFeeStdcust = new BigDecimal(0);
	private BigDecimal courtesyDeliveryFeeAccDeal = new BigDecimal(0);
	private BigDecimal courtesyDeliveryFeeAccCust = new BigDecimal(0);
	private BigDecimal courtesyDeliveryFeeInv = new BigDecimal(0);
	private BigDecimal courtesyDeliveryFeeFinDeal = new BigDecimal(0);
	private BigDecimal courtesyDeliveryFeeFinCust = new BigDecimal(0);

	private BigDecimal distantDeliveryCreditStdDeal = new BigDecimal(0);
	private BigDecimal distantDeliveryCreditStdcust = new BigDecimal(0);
	private BigDecimal distantDeliveryCreditAccDeal = new BigDecimal(0);
	private BigDecimal distantDeliveryCreditAccCust = new BigDecimal(0);
	private BigDecimal distantDeliveryCreditInv = new BigDecimal(0);
	private BigDecimal distantDeliveryCreditFinDeal = new BigDecimal(0);
	private BigDecimal distantDeliveryCreditFinCust = new BigDecimal(0);

	private BigDecimal fleetAllowanceStdDeal = new BigDecimal(0);
	private BigDecimal fleetAllowanceStdcust = new BigDecimal(0);
	private BigDecimal fleetAllowanceAccDeal = new BigDecimal(0);
	private BigDecimal fleetAllowanceAccCust = new BigDecimal(0);
	private BigDecimal fleetAllowanceInv = new BigDecimal(0);
	private BigDecimal fleetAllowanceFinDeal = new BigDecimal(0);
	private BigDecimal fleetAllowanceFinCust = new BigDecimal(0);

	private BigDecimal retailRebateStdDeal = new BigDecimal(0);
	private BigDecimal retailRebateStdcust = new BigDecimal(0);
	private BigDecimal retailRebateAccDeal = new BigDecimal(0);
	private BigDecimal retailRebateAccCust = new BigDecimal(0);
	private BigDecimal retailRebateInv = new BigDecimal(0);
	private BigDecimal retailRebateFinDeal = new BigDecimal(0);
	private BigDecimal retailRebateFinCust = new BigDecimal(0);

	private BigDecimal locateBaseCostVarianceStdDeal = new BigDecimal(0);
	private BigDecimal locateBaseCostVarianceStdcust = new BigDecimal(0);
	private BigDecimal locateBaseCostVarianceAccDeal = new BigDecimal(0);
	private BigDecimal locateBaseCostVarianceAccCust = new BigDecimal(0);
	private BigDecimal locateBaseCostVarianceInv = new BigDecimal(0);
	private BigDecimal locateBaseCostVarianceFinDeal = new BigDecimal(0);
	private BigDecimal locateBaseCostVarianceFinCust = new BigDecimal(0);

	private BigDecimal earlyOrderIncentiveOffInvoiceStdDeal = new BigDecimal(0);
	private BigDecimal earlyOrderIncentiveOffInvoiceStdcust = new BigDecimal(0);
	private BigDecimal earlyOrderIncentiveOffInvoiceAccDeal = new BigDecimal(0);
	private BigDecimal earlyOrderIncentiveOffInvoiceAccCust = new BigDecimal(0);
	private BigDecimal earlyOrderIncentiveOffInvoiceInv = new BigDecimal(0);
	private BigDecimal earlyOrderIncentiveOffInvoiceFinDeal = new BigDecimal(0);
	private BigDecimal earlyOrderIncentiveOffInvoiceFinCust = new BigDecimal(0);

	private BigDecimal volumeRelatedBonusStdDeal = new BigDecimal(0);
	private BigDecimal volumeRelatedBonusStdcust = new BigDecimal(0);
	private BigDecimal volumeRelatedBonusAccDeal = new BigDecimal(0);
	private BigDecimal volumeRelatedBonusAccCust = new BigDecimal(0);
	private BigDecimal volumeRelatedBonusInv = new BigDecimal(0);
	private BigDecimal volumeRelatedBonusFinDeal = new BigDecimal(0);
	private BigDecimal volumeRelatedBonusFinCust = new BigDecimal(0);

	private BigDecimal mikeAlbertPriceProtectionStdDeal = new BigDecimal(0);
	private BigDecimal mikeAlbertPriceProtectionStdcust = new BigDecimal(0);
	private BigDecimal mikeAlbertPriceProtectionAccDeal = new BigDecimal(0);
	private BigDecimal mikeAlbertPriceProtectionAccCust = new BigDecimal(0);
	private BigDecimal mikeAlbertPriceProtectionInv = new BigDecimal(0);
	private BigDecimal mikeAlbertPriceProtectionFinDeal = new BigDecimal(0);
	private BigDecimal mikeAlbertPriceProtectionFinCust = new BigDecimal(0);

	private BigDecimal yearEndCarryoverStdDeal = new BigDecimal(0);
	private BigDecimal yearEndCarryoverStdcust = new BigDecimal(0);
	private BigDecimal yearEndCarryoverAccDeal = new BigDecimal(0);
	private BigDecimal yearEndCarryoverAccCust = new BigDecimal(0);
	private BigDecimal yearEndCarryoverInv = new BigDecimal(0);
	private BigDecimal yearEndCarryoverFinDeal = new BigDecimal(0);
	private BigDecimal yearEndCarryoverFinCust = new BigDecimal(0);

	private BigDecimal preDeliveryInspectionStdDeal = new BigDecimal(0);
	private BigDecimal preDeliveryInspectionStdcust = new BigDecimal(0);
	private BigDecimal preDeliveryInspectionAccDeal = new BigDecimal(0);
	private BigDecimal preDeliveryInspectionAccCust = new BigDecimal(0);
	private BigDecimal preDeliveryInspectionInv = new BigDecimal(0);
	private BigDecimal preDeliveryInspectionFinDeal = new BigDecimal(0);
	private BigDecimal preDeliveryInspectionFinCust = new BigDecimal(0);

	private BigDecimal fuelCreditStdDeal = new BigDecimal(0);
	private BigDecimal fuelCreditStdcust = new BigDecimal(0);
	private BigDecimal fuelCreditAccDeal = new BigDecimal(0);
	private BigDecimal fuelCreditAccCust = new BigDecimal(0);
	private BigDecimal fuelCreditInv = new BigDecimal(0);
	private BigDecimal fuelCreditFinDeal = new BigDecimal(0);
	private BigDecimal fuelCreditFinCust = new BigDecimal(0);

	private BigDecimal lateInTransitStdDeal = new BigDecimal(0);
	private BigDecimal lateInTransitStdcust = new BigDecimal(0);
	private BigDecimal lateInTransitAccDeal = new BigDecimal(0);
	private BigDecimal lateInTransitAccCust = new BigDecimal(0);
	private BigDecimal lateInTransitInv = new BigDecimal(0);
	private BigDecimal lateInTransitFinDeal = new BigDecimal(0);
	private BigDecimal lateInTransitFinCust = new BigDecimal(0);

	private BigDecimal freightStdDeal = new BigDecimal(0);
	private BigDecimal freightStdcust = new BigDecimal(0);
	private BigDecimal freightAccDeal = new BigDecimal(0);
	private BigDecimal freightAccCust = new BigDecimal(0);
	private BigDecimal freightInv = new BigDecimal(0);
	private BigDecimal freightFinDeal = new BigDecimal(0);
	private BigDecimal freightFinCust = new BigDecimal(0);

	private BigDecimal orderingDealerFeeStdDeal = new BigDecimal(0);
	private BigDecimal orderingDealerFeeStdcust = new BigDecimal(0);
	private BigDecimal orderingDealerFeeAccDeal = new BigDecimal(0);
	private BigDecimal orderingDealerFeeAccCust = new BigDecimal(0);
	private BigDecimal orderingDealerFeeInv = new BigDecimal(0);
	private BigDecimal orderingDealerFeeFinDeal = new BigDecimal(0);
	private BigDecimal orderingDealerFeeFinCust = new BigDecimal(0);

	private BigDecimal openEndInvoiceAdjustmentStdDeal = new BigDecimal(0);
	private BigDecimal openEndInvoiceAdjustmentStdcust = new BigDecimal(0);
	private BigDecimal openEndInvoiceAdjustmentAccDeal = new BigDecimal(0);
	private BigDecimal openEndInvoiceAdjustmentAccCust = new BigDecimal(0);
	private BigDecimal openEndInvoiceAdjustmentInv = new BigDecimal(0);
	private BigDecimal openEndInvoiceAdjustmentFinDeal = new BigDecimal(0);
	private BigDecimal openEndInvoiceAdjustmentFinCust = new BigDecimal(0);

	private BigDecimal customerAssociationDiscountStdDeal = new BigDecimal(0);
	private BigDecimal customerAssociationDiscountStdcust = new BigDecimal(0);
	private BigDecimal customerAssociationDiscountAccDeal = new BigDecimal(0);
	private BigDecimal customerAssociationDiscountAccCust = new BigDecimal(0);
	private BigDecimal customerAssociationDiscountInv = new BigDecimal(0);
	private BigDecimal customerAssociationDiscountFinDeal = new BigDecimal(0);
	private BigDecimal customerAssociationDiscountFinCust = new BigDecimal(0);

	private BigDecimal vrbReclaimStdDeal = new BigDecimal(0);
	private BigDecimal vrbReclaimStdcust = new BigDecimal(0);
	private BigDecimal vrbReclaimAccDeal = new BigDecimal(0);
	private BigDecimal vrbReclaimAccCust = new BigDecimal(0);
	private BigDecimal vrbReclaimInv = new BigDecimal(0);
	private BigDecimal vrbReclaimFinDeal = new BigDecimal(0);
	private BigDecimal vrbReclaimFinCust = new BigDecimal(0);;
	private BigDecimal factoryEquipmentStdDeal = new BigDecimal(0);
	private BigDecimal factoryEquipmentStdcust = new BigDecimal(0);
	private BigDecimal factoryEquipmentAccDeal = new BigDecimal(0);
	private BigDecimal factoryEquipmentAccCust = new BigDecimal(0);
	private BigDecimal factoryEquipmentInv = new BigDecimal(0);
	private BigDecimal factoryEquipmentFinDeal = new BigDecimal(0);
	private BigDecimal factoryEquipmentFinCust = new BigDecimal(0);

	private BigDecimal afterMarketEquipmentStdDeal = new BigDecimal(0);
	private BigDecimal afterMarketEquipmentStdcust = new BigDecimal(0);
	private BigDecimal afterMarketEquipmentAccDeal = new BigDecimal(0);
	private BigDecimal afterMarketEquipmentAccCust = new BigDecimal(0);
	private BigDecimal afterMarketEquipmentInv = new BigDecimal(0);
	private BigDecimal afterMarketEquipmentFinDeal = new BigDecimal(0);
	private BigDecimal afterMarketEquipmentFinCust = new BigDecimal(0);
	//Not in use
	@SuppressWarnings("rawtypes")
	public boolean testCapitalCostOverview(String unitNumber) {
		boolean result = true;

//		String queryForStandardCostDealForFactory = "select sum(total_price) - sum(recharge_amount) total_price "
//				+ " from quotation_model_accessories where qmd_qmd_id = (select qmd_id "
//				+ " from quotation_models where quo_quo_id = (select quo_quo_id "
//				+ " from quotation_models where unit_no = :unitNumber and quote_status = 6) "
//				+ " and reference_qmd_id is not null)";
//
//		String queryForStandardCostDealForDealer = "select sum(total_price) - sum(recharge_amount) total_price "
//				+ " from quotation_dealer_accessories where qmd_qmd_id = (select qmd_id "
//				+ " from quotation_models where quo_quo_id = (select quo_quo_id "
//				+ " from quotation_models where unit_no = :unitNumber "
//				+ " and quote_status = 6) and reference_qmd_id is not null)";
//
//		String queryForAcceptedQuoteCostDealForFactory = "select sum(total_price) - sum(recharge_amount) total_price "
//				+ " from quotation_model_accessories where qmd_qmd_id = (select qmd_id  "
//				+ " from quotation_models where unit_no =:unitNumber and quote_status = 6)";
//
//		String queryForAcceptedQuoteCostDealForDealer = "select sum(total_price) - sum(recharge_amount) total_price "
//				+ " from quotation_dealer_accessories where qmd_qmd_id = (select qmd_id  "
//				+ " from quotation_models where unit_no =:unitNumber and quote_status = 6)";
//
//		String queryForInvoiceAmount = "SELECT sum(b.total_price) "
//				+ " FROM doc a, docl b	WHERE a.doc_type = 'INVOICEAP' "
//				+ " AND a.generic_ext_id = (select qmd_id from quotation_models where unit_no = :unitNumber and quote_status = 6) "
//				+ " AND a.doc_id = b.doc_id AND b.user_def4 = :equipmentType";
//
//		String queryForFinalizedQuoteCostDealForFactory = "select sum(total_price) - sum(recharge_amount) total_price "
//				+ " from quotation_model_accessories where qmd_qmd_id = ( select distinct orig_qmd_id "
//				+ " from quotation_models where unit_no = :unitNumber and quote_status = 6)";
//
//		String queryForFinalizedQuoteCostDealForDealer = "select sum(total_price) - sum(recharge_amount) total_price "
//				+ " from quotation_dealer_accessories where qmd_qmd_id = ( select distinct orig_qmd_id "
//				+ " from quotation_models where unit_no = :unitNumber and quote_status = 6)";
//
//		String queryForStandardQuoteDealForElements = "select  value from quotation_capital_elements qce,  CAPITAL_ELEMENTS cel "
//				+ " where qce.cel_cel_id = cel.cel_id and qmd_qmd_id = (select qmd_id  from quotation_models where quo_quo_id = (select quo_quo_id "
//				+ " from quotation_models where unit_no = :unitNumber and quote_status = 6) and reference_qmd_id is not null) and cel_cel_id = :cel_id";
//
//		String queryForAcceptedQuoteDealForElements = "select  value from quotation_capital_elements qce,  CAPITAL_ELEMENTS cel "
//				+ " where qce.cel_cel_id = cel.cel_id and qmd_qmd_id = (select qmd_id from quotation_models "
//				+ " where unit_no = :unitNumber and quote_status = 6) and cel_cel_id = :cel_id";
//
//		String queryForFinalizedQuoteDealForElements = "select  value from quotation_capital_elements qce,  CAPITAL_ELEMENTS cel "
//				+ " where qce.cel_cel_id = cel.cel_id and qmd_qmd_id = ( select distinct orig_qmd_id from quotation_models where unit_no = :unitNumber "
//				+ " and quote_status = 6) and cel_cel_id = :cel_id";
//
//		String queryForInvoiceAmtForElements = "SELECT b.total_price    FROM doc a, docl b WHERE a.doc_type = 'INVOICEAP' "
//				+ " AND a.generic_ext_id = (select qmd_id from quotation_models where unit_no = :unitNumber and quote_status = 6) "
//				+ " AND a.doc_id = b.doc_id AND b.user_def4 = 'CAPITAL' and b.generic_ext_id = :cel_id";
//
//		try {
//			List<CostElementVO> costElementList = capitalCostOverviewService.getCapitalCost(unitNumber);
//			for (CostElementVO costElementVO : costElementList) {
//				// verify factory elements total
//				if ("Factory Equipment".equals(costElementVO.getElementName())) {
//					Query query1 = em.createNativeQuery(queryForStandardCostDealForFactory);
//					query1.setParameter("unitNumber", unitNumber);
//					List totalSumFactoryEquipmentsObj = query1.getResultList();
//					if (totalSumFactoryEquipmentsObj != null && totalSumFactoryEquipmentsObj.size() > 0) {
//						BigDecimal totalSumFactoryEquipments = (BigDecimal) totalSumFactoryEquipmentsObj.get(0);
//						if (costElementVO.getOriginalOrderDeal().doubleValue() == (totalSumFactoryEquipments != null ? totalSumFactoryEquipments
//								.doubleValue() : 0.00)) {
//							result = true;
//						} else {
//
//							return false;
//						}
//					}
//
//					Query query2 = em.createNativeQuery(queryForAcceptedQuoteCostDealForFactory);
//					query2.setParameter("unitNumber", unitNumber);
//					List totalSumFactoryEquipmentsAcceptedQuoteObj = query2.getResultList();
//					if (totalSumFactoryEquipmentsAcceptedQuoteObj != null
//							&& totalSumFactoryEquipmentsAcceptedQuoteObj.size() > 0) {
//						BigDecimal totalSumFactoryEquipmentsAcceptedQuote = (BigDecimal) totalSumFactoryEquipmentsAcceptedQuoteObj
//								.get(0);
//						if (costElementVO.getAcceptedQuoteDeal().doubleValue() == (totalSumFactoryEquipmentsAcceptedQuote != null ? totalSumFactoryEquipmentsAcceptedQuote
//								.doubleValue() : 0.00)) {
//							result = true;
//						} else {
//
//							return false;
//						}
//					}
//
//					Query query3 = em.createNativeQuery(queryForInvoiceAmount);
//					query3.setParameter("unitNumber", unitNumber);
//					query3.setParameter("equipmentType", "FACTORY");
//					List totalSumFactoryEquipmentsInvoiceAmountObj = query3.getResultList();
//					if (totalSumFactoryEquipmentsInvoiceAmountObj != null
//							&& totalSumFactoryEquipmentsInvoiceAmountObj.size() > 0) {
//						BigDecimal totalSumFactoryEquipmentsInvoiceAmount = (BigDecimal) totalSumFactoryEquipmentsInvoiceAmountObj
//								.get(0);
//						if (costElementVO.getInvoiceAmount().doubleValue() == (totalSumFactoryEquipmentsInvoiceAmount != null ? totalSumFactoryEquipmentsInvoiceAmount
//								.doubleValue() : 0.00)) {
//							result = true;
//						} else {
//
//							return false;
//						}
//					}
//
//					Query query4 = em.createNativeQuery(queryForFinalizedQuoteCostDealForFactory);
//					query4.setParameter("unitNumber", unitNumber);
//					List totalSumFactoryEquipmentsFinalizedQuoteObj = query4.getResultList();
//					if (totalSumFactoryEquipmentsFinalizedQuoteObj != null
//							&& totalSumFactoryEquipmentsFinalizedQuoteObj.size() > 0) {
//						BigDecimal totalSumFactoryEquipmentsFinalizedQuote = (BigDecimal) totalSumFactoryEquipmentsFinalizedQuoteObj
//								.get(0);
//						if (costElementVO.getFinalizedQuoteDeal().doubleValue() == (totalSumFactoryEquipmentsFinalizedQuote != null ? totalSumFactoryEquipmentsFinalizedQuote
//								.doubleValue() : 0.00)) {
//							result = true;
//						} else {
//
//							return false;
//						}
//					}
//
//				}
//				// verify After market equipments total
//				if ("After Market Equipment".equals(costElementVO.getElementName())) {
//					Query query1 = em.createNativeQuery(queryForStandardCostDealForDealer);
//					query1.setParameter("unitNumber", unitNumber);
//					List totalSumAfterMarketEquipmentsObj = query1.getResultList();
//					if (totalSumAfterMarketEquipmentsObj != null && totalSumAfterMarketEquipmentsObj.size() > 0) {
//						BigDecimal totalSumAfterMarketEquipments = (BigDecimal) totalSumAfterMarketEquipmentsObj.get(0);
//						if (costElementVO.getOriginalOrderDeal().doubleValue() == (totalSumAfterMarketEquipments != null ? totalSumAfterMarketEquipments
//								.doubleValue() : 0.00)) {
//							result = true;
//						} else {
//
//							return false;
//						}
//					}
//
//					Query query2 = em.createNativeQuery(queryForAcceptedQuoteCostDealForDealer);
//					query2.setParameter("unitNumber", unitNumber);
//					List totalSumDealerEquipmentsAcceptedQuoteObj = query2.getResultList();
//					if (totalSumDealerEquipmentsAcceptedQuoteObj != null
//							&& totalSumDealerEquipmentsAcceptedQuoteObj.size() > 0) {
//						BigDecimal totalSumDealerEquipmentsAcceptedQuote = (BigDecimal) totalSumDealerEquipmentsAcceptedQuoteObj
//								.get(0);
//						if (costElementVO.getAcceptedQuoteDeal().doubleValue() == (totalSumDealerEquipmentsAcceptedQuote != null ? totalSumDealerEquipmentsAcceptedQuote
//								.doubleValue() : 0.00)) {
//							result = true;
//						} else {
//
//							return false;
//						}
//
//					}
//
//					Query query3 = em.createNativeQuery(queryForInvoiceAmount);
//					query3.setParameter("unitNumber", unitNumber);
//					query3.setParameter("equipmentType", "DEALER");
//					List totalSumDealerEquipmentsInvoiceAmountObj = query3.getResultList();
//					if (totalSumDealerEquipmentsInvoiceAmountObj != null
//							&& totalSumDealerEquipmentsInvoiceAmountObj.size() > 0) {
//						BigDecimal totalSumDealerEquipmentsInvoiceAmount = (BigDecimal) totalSumDealerEquipmentsInvoiceAmountObj
//								.get(0);
//						if (costElementVO.getInvoiceAmount().doubleValue() == (totalSumDealerEquipmentsInvoiceAmount != null ? totalSumDealerEquipmentsInvoiceAmount
//								.doubleValue() : 0.00)) {
//							result = true;
//						} else {
//
//							return false;
//						}
//					}
//
//					Query query4 = em.createNativeQuery(queryForFinalizedQuoteCostDealForDealer);
//					query4.setParameter("unitNumber", unitNumber);
//					List totalSumDealerEquipmentsFinalizedQuoteObj = query4.getResultList();
//					if (totalSumDealerEquipmentsFinalizedQuoteObj != null
//							&& totalSumDealerEquipmentsFinalizedQuoteObj.size() > 0) {
//						BigDecimal totalSumDealerEquipmentsFinalizedQuote = (BigDecimal) totalSumDealerEquipmentsFinalizedQuoteObj
//								.get(0);
//						if (costElementVO.getFinalizedQuoteDeal().doubleValue() == (totalSumDealerEquipmentsFinalizedQuote != null ? totalSumDealerEquipmentsFinalizedQuote
//								.doubleValue() : 0.00)) {
//							result = true;
//						} else {
//
//							return false;
//						}
//					}
//
//				}
//				if (!costElementVO.getElementName().equals("After Market Equipment")
//						&& !costElementVO.getElementName().equals("Factory Equipment")
//						&& !"Base Vehicle".equals(costElementVO.getElementName())
//						&& !"Capital Contribution".equals(costElementVO.getElementName())) {
//					Query queryForStdDeal = em.createNativeQuery(queryForStandardQuoteDealForElements);
//					queryForStdDeal.setParameter("unitNumber", unitNumber);
//					queryForStdDeal.setParameter("cel_id", costElementVO.getElementId());
//					List amtForStdDealObj = queryForStdDeal.getResultList();
//					if (amtForStdDealObj != null && amtForStdDealObj.size() > 0) {
//						BigDecimal amtForStdDeal = (BigDecimal) amtForStdDealObj.get(0);
//						if (costElementVO.getOriginalOrderDeal().doubleValue() == (amtForStdDeal != null ? amtForStdDeal
//								.doubleValue() : 0.00)) {
//							result = true;
//						} else {
//
//							return false;
//						}
//					}
//
//					Query queryForAcceptedDeal = em.createNativeQuery(queryForAcceptedQuoteDealForElements);
//					queryForAcceptedDeal.setParameter("unitNumber", unitNumber);
//					queryForAcceptedDeal.setParameter("cel_id", costElementVO.getElementId());
//					List amtForAcceptedDealObj = queryForAcceptedDeal.getResultList();
//					if (amtForAcceptedDealObj != null && amtForAcceptedDealObj.size() > 0) {
//						BigDecimal amtForAcceptedDeal = (BigDecimal) amtForAcceptedDealObj.get(0);
//						if (costElementVO.getAcceptedQuoteDeal().doubleValue() == (amtForAcceptedDeal != null ? amtForAcceptedDeal
//								.doubleValue() : 0.00)) {
//							result = true;
//						} else {
//
//							return false;
//						}
//					}
//					Query queryForFinalizedDeal = em.createNativeQuery(queryForFinalizedQuoteDealForElements);
//					queryForFinalizedDeal.setParameter("unitNumber", unitNumber);
//					queryForFinalizedDeal.setParameter("cel_id", costElementVO.getElementId());
//					List amtForFinalizedDealObj = queryForFinalizedDeal.getResultList();
//					if (amtForFinalizedDealObj != null && amtForFinalizedDealObj.size() > 0) {
//						BigDecimal amtForFinalizedDeal = (BigDecimal) amtForFinalizedDealObj.get(0);
//						if (costElementVO.getFinalizedQuoteDeal().doubleValue() == (amtForFinalizedDeal != null ? amtForFinalizedDeal
//								.doubleValue() : 0.00)) {
//							result = true;
//						} else {
//
//							return false;
//						}
//					}
//					Query queryInvoiceAmtForElements = em.createNativeQuery(queryForInvoiceAmtForElements);
//					queryInvoiceAmtForElements.setParameter("unitNumber", unitNumber);
//					queryInvoiceAmtForElements.setParameter("cel_id", costElementVO.getElementId());
//					List amtInvoicedObj = queryInvoiceAmtForElements.getResultList();
//					if (amtInvoicedObj != null && amtInvoicedObj.size() > 0) {
//						BigDecimal amtInvoiced = (BigDecimal) amtInvoicedObj.get(0);
//						if (costElementVO.getInvoiceAmount().doubleValue() == (amtInvoiced != null ? amtInvoiced
//								.doubleValue() : 0.00)) {
//							result = true;
//						} else {
//
//							return false;
//						}
//					}
//				}
//			}
//		} catch (Exception e) {
//			result = false;
//		}
		return result;
	}
	//Not in use
	@SuppressWarnings({ "unchecked" })
	public boolean testCapitalCostOverviewDealValuesFromViewData(String unitNumber) {
		boolean result = true;
//		String queryStringForView = "select  description, original_quote, accepted_quote, amount_inv, final_quote "
//				+ " from flt_inv_reconcilation_v where unit_no = :unitNumber";
//		try {
//			Query queryForElements = em.createNativeQuery(queryStringForView);
//			queryForElements.setParameter("unitNumber", unitNumber);
//			List<Object[]> listOfElements = (List<Object[]>) queryForElements.getResultList();
//			List<CostElementVO> costElementList = capitalCostOverviewService.getCapitalCost(unitNumber);
//			if (listOfElements != null && listOfElements.size() > 0) {
//				for (Object[] object : listOfElements) {
//					String eleNameInView = (String) object[0];
//					BigDecimal eleStdAmt = (object[1] != null ? (BigDecimal) object[1] : new BigDecimal(0));
//					BigDecimal eleAcceptedAmt = (object[2] != null ? (BigDecimal) object[2] : new BigDecimal(0));
//					BigDecimal eleInvAmt = (object[3] != null ? (BigDecimal) object[3] : new BigDecimal(0));
//					BigDecimal eleFinalAmt = (object[4] != null ? (BigDecimal) object[4] : new BigDecimal(0));
//					for (CostElementVO costElementVO : costElementList) {
//						if (eleNameInView.equals(costElementVO.getElementName())) {
//							if (eleStdAmt.doubleValue() != costElementVO.getOriginalOrderDeal().doubleValue()) {
//								return false;
//							}
//							if (eleAcceptedAmt.doubleValue() != costElementVO.getAcceptedQuoteDeal().doubleValue()) {
//								return false;
//							}
//							if (eleInvAmt.doubleValue() != costElementVO.getInvoiceAmount().doubleValue()) {
//								return false;
//							}
//							if (eleFinalAmt.doubleValue() != costElementVO.getFinalizedQuoteDeal().doubleValue()) {
//								return false;
//							}
//						}
//					}
//				}
//			}
//		} catch (Exception ex) {
//			return false;
//		}

		return result;
	}
	//validating against values known from willow form.
	@SuppressWarnings({ "unchecked" })
	public boolean testCapitalCostOverviewCustomerValuesOfAcceptedAndFinalizedQuote(String unitNumber) {
		boolean result = true;
//		String queryString = "select   description, decode(oe_cust_cap, 'Y', accepted_quote, 0) accepted_quote_cust,"
//				+ " decode(oe_cust_cap, 'Y', final_quote, 0) final_quote_cust "
//				+ " from(SELECT quotation1.get_product_type(qmd.qmd_id, quo.qpr_qpr_id) product_type,"
//				+ " firv.description, firv.accepted_quote, firv.final_quote,NVL((select  nvl(qce.on_invoice, cel.on_invoice) on_invoice "
//				+ " from quotation_capital_elements qce,CAPITAL_ELEMENTS cel where qce.cel_cel_id = cel.cel_id "
//				+ " and qmd_qmd_id =  qmd.qmd_id and cel.description = firv.description), 'Y') oe_cust_cap "
//				+ " FROM quotation_models qmd,quotations quo,flt_inv_reconcilation_v firv "
//				+ " WHERE qmd.quo_quo_id = quo.quo_id and qmd.unit_no = firv.unit_no "
//				+ " and qmd.quote_status = 6 and qmd.unit_no = :unitNumber AND FIRV.DETAIL_TYPE <> 'Accessory' )";
//		try {
//			Query queryForElements = em.createNativeQuery(queryString);
//			queryForElements.setParameter("unitNumber", unitNumber);
//			List<Object[]> listOfElements = (List<Object[]>) queryForElements.getResultList();
//			List<CostElementVO> costElementList = capitalCostOverviewService.getCapitalCost(unitNumber);
//			if (listOfElements != null && listOfElements.size() > 0) {
//				for (Object[] object : listOfElements) {
//					String eleNameInView = (String) object[0];
//					BigDecimal eleAcceptedAmt = (object[1] != null ? (BigDecimal) object[1] : new BigDecimal(0));
//					BigDecimal eleFinalAmt = (object[2] != null ? (BigDecimal) object[2] : new BigDecimal(0));
//
//					for (CostElementVO costElementVO : costElementList) {
//						if (eleNameInView.equals(costElementVO.getElementName())) {
//
//							if (eleAcceptedAmt.doubleValue() != costElementVO.getAcceptedQuoteCustomer().doubleValue()) {
//								return false;
//							}
//							if (eleFinalAmt.doubleValue() != costElementVO.getFinalizedQuoteCustomer().doubleValue()) {
//								return false;
//							}
//						}
//					}
//				}
//			}
//		} catch (Exception ex) {
//			return false;
//		}

		return result;
	}

	public boolean testCapitalCostEnquiryScreenOpenEnd(String unitNumber) {
//		 intAmounts();
//		try {
//			List<CostElementVO> costElementList = capitalCostOverviewService.getCapitalCost(unitNumber);
//			for (CostElementVO costElementVO : costElementList) {
//				if (baseVehicle.equals(costElementVO.getElementName())) {
//					baseVehicleStdDeal = new BigDecimal(27958.5);
//					baseVehicleStdcust = new BigDecimal(27958.5);
//					baseVehicleAccDeal = new BigDecimal(27993.5);
//					baseVehicleAccCust = new BigDecimal(27993.5);
//					baseVehicleInv = new BigDecimal(0);
//					baseVehicleFinDeal = new BigDecimal(27971.7);
//					baseVehicleFinCust = new BigDecimal(27971.7);
//					if (!(baseVehicleStdDeal.doubleValue() == costElementVO.getOriginalOrderDeal().doubleValue()
//							&& baseVehicleStdcust.doubleValue() == costElementVO.getOriginalOrderCustomer().doubleValue()
//							&& baseVehicleAccDeal.doubleValue() == costElementVO.getAcceptedQuoteDeal().doubleValue()
//							&& baseVehicleAccCust.doubleValue() == costElementVO.getAcceptedQuoteCustomer().doubleValue()
//							//&& baseVehicleInv.doubleValue() == costElementVO.getInvoiceAmount().doubleValue()
//							&& baseVehicleFinDeal.doubleValue() == costElementVO.getFinalizedQuoteDeal().doubleValue() && baseVehicleFinCust
//								.doubleValue() == costElementVO.getFinalizedQuoteCustomer().doubleValue())) {
//						return false;
//					}
//				}
//				if (holdBack.equals(costElementVO.getElementName())) {
//					holdBackStdDeal = new BigDecimal(-887.85);
//					holdBackStdcust = new BigDecimal(0);
//					holdBackAccDeal = new BigDecimal(-885.45);
//					holdBackAccCust = new BigDecimal(0);
//					holdBackInv = new BigDecimal(0);
//					holdBackFinDeal = new BigDecimal(-880.8);
//					holdBackFinCust = new BigDecimal(0);
//					if (!(holdBackStdDeal.doubleValue() == costElementVO.getOriginalOrderDeal().doubleValue()
//							&& holdBackStdcust.doubleValue() == costElementVO.getOriginalOrderCustomer().doubleValue()
//							&& holdBackAccDeal.doubleValue() == costElementVO.getAcceptedQuoteDeal().doubleValue()
//							&& holdBackAccCust.doubleValue() == costElementVO.getAcceptedQuoteCustomer().doubleValue()
//							//&& holdBackInv.doubleValue() == costElementVO.getInvoiceAmount().doubleValue()
//							&& holdBackFinDeal.doubleValue() == costElementVO.getFinalizedQuoteDeal().doubleValue() && holdBackFinCust
//								.doubleValue() == costElementVO.getFinalizedQuoteCustomer().doubleValue())) {
//						return false;
//					}
//				}
//				if (advertising.equals(costElementVO.getElementName())) {
//					if (!(advertisingStdDeal.doubleValue() == costElementVO.getOriginalOrderDeal().doubleValue()
//							&& advertisingStdcust.doubleValue() == costElementVO.getOriginalOrderCustomer().doubleValue()
//							&& advertisingAccDeal.doubleValue() == costElementVO.getAcceptedQuoteDeal().doubleValue()
//							&& advertisingAccCust.doubleValue() == costElementVO.getAcceptedQuoteCustomer().doubleValue()
//							//&& advertisingInv.doubleValue() == costElementVO.getInvoiceAmount().doubleValue()
//							&& advertisingFinDeal.doubleValue() == costElementVO.getFinalizedQuoteDeal().doubleValue() && advertisingFinCust
//								.doubleValue() == costElementVO.getFinalizedQuoteCustomer().doubleValue())) {
//						return false;
//					}
//				}
//				if (financeAssistance1.equals(costElementVO.getElementName())) {
//					financeAssistance1StdDeal = new BigDecimal(-555);
//					financeAssistance1Stdcust = new BigDecimal(0);
//					financeAssistance1AccDeal = new BigDecimal(-555);
//					financeAssistance1AccCust = new BigDecimal(0);
//					financeAssistance1Inv = new BigDecimal(0);
//					financeAssistance1FinDeal = new BigDecimal(-545);
//					financeAssistance1FinCust = new BigDecimal(0);
//					if (!(financeAssistance1StdDeal.doubleValue() == costElementVO.getOriginalOrderDeal().doubleValue()
//							&& financeAssistance1Stdcust.doubleValue() == costElementVO.getOriginalOrderCustomer()
//									.doubleValue()
//							&& financeAssistance1AccDeal.doubleValue() == costElementVO.getAcceptedQuoteDeal().doubleValue()
//							&& financeAssistance1AccCust.doubleValue() == costElementVO.getAcceptedQuoteCustomer()
//									.doubleValue()
//							//&& financeAssistance1Inv.doubleValue() == costElementVO.getInvoiceAmount().doubleValue()
//							&& financeAssistance1FinDeal.doubleValue() == costElementVO.getFinalizedQuoteDeal()
//									.doubleValue() && financeAssistance1FinCust.doubleValue() == costElementVO
//							.getFinalizedQuoteCustomer().doubleValue())) {
//						return false;
//					}
//				}
//				if (malIncentiveMoney.equals(costElementVO.getElementName())) {
//					if (!(malIncentiveMoneyStdDeal.doubleValue() == costElementVO.getOriginalOrderDeal().doubleValue()
//							&& malIncentiveMoneyStdcust.doubleValue() == costElementVO.getOriginalOrderCustomer()
//									.doubleValue()
//							&& malIncentiveMoneyAccDeal.doubleValue() == costElementVO.getAcceptedQuoteDeal().doubleValue()
//							&& malIncentiveMoneyAccCust.doubleValue() == costElementVO.getAcceptedQuoteCustomer()
//									.doubleValue()
//							//&& malIncentiveMoneyInv.doubleValue() == costElementVO.getInvoiceAmount().doubleValue()
//							&& malIncentiveMoneyFinDeal.doubleValue() == costElementVO.getFinalizedQuoteDeal().doubleValue() && malIncentiveMoneyFinCust
//								.doubleValue() == costElementVO.getFinalizedQuoteCustomer().doubleValue())) {
//						return false;
//					}
//				}
//				if (courtesyDeliveryFee.equals(costElementVO.getElementName())) {
//					if (!(courtesyDeliveryFeeStdDeal.doubleValue() == costElementVO.getOriginalOrderDeal().doubleValue()
//							&& courtesyDeliveryFeeStdcust.doubleValue() == costElementVO.getOriginalOrderCustomer()
//									.doubleValue()
//							&& courtesyDeliveryFeeAccDeal.doubleValue() == costElementVO.getAcceptedQuoteDeal()
//									.doubleValue()
//							&& courtesyDeliveryFeeAccCust.doubleValue() == costElementVO.getAcceptedQuoteCustomer()
//									.doubleValue()
//							//&& courtesyDeliveryFeeInv.doubleValue() == costElementVO.getInvoiceAmount().doubleValue()
//							&& courtesyDeliveryFeeFinDeal.doubleValue() == costElementVO.getFinalizedQuoteDeal()
//									.doubleValue() && courtesyDeliveryFeeFinCust.doubleValue() == costElementVO
//							.getFinalizedQuoteCustomer().doubleValue())) {
//						return false;
//					}
//				}
//				if (distantDeliveryCredit.equals(costElementVO.getElementName())) {
//					if (!(distantDeliveryCreditStdDeal.doubleValue() == costElementVO.getOriginalOrderDeal().doubleValue()
//							&& distantDeliveryCreditStdcust.doubleValue() == costElementVO.getOriginalOrderCustomer()
//									.doubleValue()
//							&& distantDeliveryCreditAccDeal.doubleValue() == costElementVO.getAcceptedQuoteDeal()
//									.doubleValue()
//							&& distantDeliveryCreditAccCust.doubleValue() == costElementVO.getAcceptedQuoteCustomer()
//									.doubleValue()
//							//&& distantDeliveryCreditInv.doubleValue() == costElementVO.getInvoiceAmount().doubleValue()
//							&& distantDeliveryCreditFinDeal.doubleValue() == costElementVO.getFinalizedQuoteDeal()
//									.doubleValue() && distantDeliveryCreditFinCust.doubleValue() == costElementVO
//							.getFinalizedQuoteCustomer().doubleValue())) {
//						return false;
//					}
//				}
//				if (fleetAllowance.equals(costElementVO.getElementName())) {
//					if (!(fleetAllowanceStdDeal.doubleValue() == costElementVO.getOriginalOrderDeal().doubleValue()
//							&& fleetAllowanceStdcust.doubleValue() == costElementVO.getOriginalOrderCustomer().doubleValue()
//							&& fleetAllowanceAccDeal.doubleValue() == costElementVO.getAcceptedQuoteDeal().doubleValue()
//							&& fleetAllowanceAccCust.doubleValue() == costElementVO.getAcceptedQuoteCustomer().doubleValue()
//							//&& fleetAllowanceInv.doubleValue() == costElementVO.getInvoiceAmount().doubleValue()
//							&& fleetAllowanceFinDeal.doubleValue() == costElementVO.getFinalizedQuoteDeal().doubleValue() && fleetAllowanceFinCust
//								.doubleValue() == costElementVO.getFinalizedQuoteCustomer().doubleValue())) {
//						return false;
//					}
//				}
//				if (retailRebate.equals(costElementVO.getElementName())) {
//					if (!(retailRebateStdDeal.doubleValue() == costElementVO.getOriginalOrderDeal().doubleValue()
//							&& retailRebateStdcust.doubleValue() == costElementVO.getOriginalOrderCustomer().doubleValue()
//							&& retailRebateAccDeal.doubleValue() == costElementVO.getAcceptedQuoteDeal().doubleValue()
//							&& retailRebateAccCust.doubleValue() == costElementVO.getAcceptedQuoteCustomer().doubleValue()
//							//&& retailRebateInv.doubleValue() == costElementVO.getInvoiceAmount().doubleValue()
//							&& retailRebateFinDeal.doubleValue() == costElementVO.getFinalizedQuoteDeal().doubleValue() && retailRebateFinCust
//								.doubleValue() == costElementVO.getFinalizedQuoteCustomer().doubleValue())) {
//						return false;
//					}
//				}
//				if (locateBaseCostVariance.equals(costElementVO.getElementName())) {
//					if (!(locateBaseCostVarianceStdDeal.doubleValue() == costElementVO.getOriginalOrderDeal().doubleValue()
//							&& locateBaseCostVarianceStdcust.doubleValue() == costElementVO.getOriginalOrderCustomer()
//									.doubleValue()
//							&& locateBaseCostVarianceAccDeal.doubleValue() == costElementVO.getAcceptedQuoteDeal()
//									.doubleValue()
//							&& locateBaseCostVarianceAccCust.doubleValue() == costElementVO.getAcceptedQuoteCustomer()
//									.doubleValue()
//							//&& locateBaseCostVarianceInv.doubleValue() == costElementVO.getInvoiceAmount().doubleValue()
//							&& locateBaseCostVarianceFinDeal.doubleValue() == costElementVO.getFinalizedQuoteDeal()
//									.doubleValue() && locateBaseCostVarianceFinCust.doubleValue() == costElementVO
//							.getFinalizedQuoteCustomer().doubleValue())) {
//						return false;
//					}
//				}
//				if (earlyOrderIncentiveOffInvoice.equals(costElementVO.getElementName())) {
//					if (!(earlyOrderIncentiveOffInvoiceStdDeal.doubleValue() == costElementVO.getOriginalOrderDeal()
//							.doubleValue()
//							&& earlyOrderIncentiveOffInvoiceStdcust.doubleValue() == costElementVO
//									.getOriginalOrderCustomer().doubleValue()
//							&& earlyOrderIncentiveOffInvoiceAccDeal.doubleValue() == costElementVO.getAcceptedQuoteDeal()
//									.doubleValue()
//							&& earlyOrderIncentiveOffInvoiceAccCust.doubleValue() == costElementVO
//									.getAcceptedQuoteCustomer().doubleValue()
//							//&& earlyOrderIncentiveOffInvoiceInv.doubleValue() == costElementVO.getInvoiceAmount().doubleValue()
//							&& earlyOrderIncentiveOffInvoiceFinDeal.doubleValue() == costElementVO.getFinalizedQuoteDeal()
//									.doubleValue() && earlyOrderIncentiveOffInvoiceFinCust.doubleValue() == costElementVO
//							.getFinalizedQuoteCustomer().doubleValue())) {
//						return false;
//					}
//				}
//				if (volumeRelatedBonus.equals(costElementVO.getElementName())) {
//					volumeRelatedBonusStdDeal = new BigDecimal(-2300);
//					volumeRelatedBonusStdcust = new BigDecimal(-2300);
//					volumeRelatedBonusAccDeal = new BigDecimal(-2300);
//					volumeRelatedBonusAccCust = new BigDecimal(-2300);
//					volumeRelatedBonusInv = new BigDecimal(0);
//					volumeRelatedBonusFinDeal = new BigDecimal(-2300);
//					volumeRelatedBonusFinCust = new BigDecimal(-2300);
//					if (!(volumeRelatedBonusStdDeal.doubleValue() == costElementVO.getOriginalOrderDeal().doubleValue()
//							&& volumeRelatedBonusStdcust.doubleValue() == costElementVO.getOriginalOrderCustomer()
//									.doubleValue()
//							&& volumeRelatedBonusAccDeal.doubleValue() == costElementVO.getAcceptedQuoteDeal().doubleValue()
//							&& volumeRelatedBonusAccCust.doubleValue() == costElementVO.getAcceptedQuoteCustomer()
//									.doubleValue()
//							//&& volumeRelatedBonusInv.doubleValue() == costElementVO.getInvoiceAmount().doubleValue()
//							&& volumeRelatedBonusFinDeal.doubleValue() == costElementVO.getFinalizedQuoteDeal()
//									.doubleValue() && volumeRelatedBonusFinCust.doubleValue() == costElementVO
//							.getFinalizedQuoteCustomer().doubleValue())) {
//						return false;
//					}
//				}
//				if (mikeAlbertPriceProtection.equals(costElementVO.getElementName())) {
//					if (!(mikeAlbertPriceProtectionStdDeal.doubleValue() == costElementVO.getOriginalOrderDeal()
//							.doubleValue()
//							&& mikeAlbertPriceProtectionStdcust.doubleValue() == costElementVO.getOriginalOrderCustomer()
//									.doubleValue()
//							&& mikeAlbertPriceProtectionAccDeal.doubleValue() == costElementVO.getAcceptedQuoteDeal()
//									.doubleValue()
//							&& mikeAlbertPriceProtectionAccCust.doubleValue() == costElementVO.getAcceptedQuoteCustomer()
//									.doubleValue()
//							//&& mikeAlbertPriceProtectionInv.doubleValue() == costElementVO.getInvoiceAmount().doubleValue()
//							&& mikeAlbertPriceProtectionFinDeal.doubleValue() == costElementVO.getFinalizedQuoteDeal()
//									.doubleValue() && mikeAlbertPriceProtectionFinCust.doubleValue() == costElementVO
//							.getFinalizedQuoteCustomer().doubleValue())) {
//						return false;
//					}
//				}
//				if (yearEndCarryover.equals(costElementVO.getElementName())) {
//					if (!(yearEndCarryoverStdDeal.doubleValue() == costElementVO.getOriginalOrderDeal().doubleValue()
//							&& yearEndCarryoverStdcust.doubleValue() == costElementVO.getOriginalOrderCustomer()
//									.doubleValue()
//							&& yearEndCarryoverAccDeal.doubleValue() == costElementVO.getAcceptedQuoteDeal().doubleValue()
//							&& yearEndCarryoverAccCust.doubleValue() == costElementVO.getAcceptedQuoteCustomer()
//									.doubleValue()
//							//&& yearEndCarryoverInv.doubleValue() == costElementVO.getInvoiceAmount().doubleValue()
//							&& yearEndCarryoverFinDeal.doubleValue() == costElementVO.getFinalizedQuoteDeal().doubleValue() && yearEndCarryoverFinCust
//								.doubleValue() == costElementVO.getFinalizedQuoteCustomer().doubleValue())) {
//						return false;
//					}
//				}
//				if (preDeliveryInspection.equals(costElementVO.getElementName())) {
//					if (!(preDeliveryInspectionStdDeal.doubleValue() == costElementVO.getOriginalOrderDeal().doubleValue()
//							&& preDeliveryInspectionStdcust.doubleValue() == costElementVO.getOriginalOrderCustomer()
//									.doubleValue()
//							&& preDeliveryInspectionAccDeal.doubleValue() == costElementVO.getAcceptedQuoteDeal()
//									.doubleValue()
//							&& preDeliveryInspectionAccCust.doubleValue() == costElementVO.getAcceptedQuoteCustomer()
//									.doubleValue()
//							//&& preDeliveryInspectionInv.doubleValue() == costElementVO.getInvoiceAmount().doubleValue()
//							&& preDeliveryInspectionFinDeal.doubleValue() == costElementVO.getFinalizedQuoteDeal()
//									.doubleValue() && preDeliveryInspectionFinCust.doubleValue() == costElementVO
//							.getFinalizedQuoteCustomer().doubleValue())) {
//						return false;
//					}
//				}
//				if (fuelCredit.equals(costElementVO.getElementName())) {
//					if (!(fuelCreditStdDeal.doubleValue() == costElementVO.getOriginalOrderDeal().doubleValue()
//							&& fuelCreditStdcust.doubleValue() == costElementVO.getOriginalOrderCustomer().doubleValue()
//							&& fuelCreditAccDeal.doubleValue() == costElementVO.getAcceptedQuoteDeal().doubleValue()
//							&& fuelCreditAccCust.doubleValue() == costElementVO.getAcceptedQuoteCustomer().doubleValue()
//							//&& fuelCreditInv.doubleValue() == costElementVO.getInvoiceAmount().doubleValue()
//							&& fuelCreditFinDeal.doubleValue() == costElementVO.getFinalizedQuoteDeal().doubleValue() && fuelCreditFinCust
//								.doubleValue() == costElementVO.getFinalizedQuoteCustomer().doubleValue())) {
//						return false;
//					}
//				}
//				if (lateInTransit.equals(costElementVO.getElementName())) {
//					if (!(lateInTransitStdDeal.doubleValue() == costElementVO.getOriginalOrderDeal().doubleValue()
//							&& lateInTransitStdcust.doubleValue() == costElementVO.getOriginalOrderCustomer().doubleValue()
//							&& lateInTransitAccDeal.doubleValue() == costElementVO.getAcceptedQuoteDeal().doubleValue()
//							&& lateInTransitAccCust.doubleValue() == costElementVO.getAcceptedQuoteCustomer().doubleValue()
//							//&& lateInTransitInv.doubleValue() == costElementVO.getInvoiceAmount().doubleValue()
//							&& lateInTransitFinDeal.doubleValue() == costElementVO.getFinalizedQuoteDeal().doubleValue() && lateInTransitFinCust
//								.doubleValue() == costElementVO.getFinalizedQuoteCustomer().doubleValue())) {
//						return false;
//					}
//				}
//				if (freight.equals(costElementVO.getElementName())) {
//					freightStdDeal = new BigDecimal(825);
//					freightStdcust = new BigDecimal(825);
//					freightAccDeal = new BigDecimal(810);
//					freightAccCust = new BigDecimal(810);
//					freightInv = new BigDecimal(0);
//					freightFinDeal = new BigDecimal(810);
//					freightFinCust = new BigDecimal(810);
//					if (!(freightStdDeal.doubleValue() == costElementVO.getOriginalOrderDeal().doubleValue()
//							&& freightStdcust.doubleValue() == costElementVO.getOriginalOrderCustomer().doubleValue()
//							&& freightAccDeal.doubleValue() == costElementVO.getAcceptedQuoteDeal().doubleValue()
//							&& freightAccCust.doubleValue() == costElementVO.getAcceptedQuoteCustomer().doubleValue()
//							//&& freightInv.doubleValue() == costElementVO.getInvoiceAmount().doubleValue()
//							&& freightFinDeal.doubleValue() == costElementVO.getFinalizedQuoteDeal().doubleValue() && freightFinCust
//								.doubleValue() == costElementVO.getFinalizedQuoteCustomer().doubleValue())) {
//						return false;
//					}
//				}
//				if (orderingDealerFee.equals(costElementVO.getElementName())) {
//					orderingDealerFeeStdDeal = new BigDecimal(60);
//					orderingDealerFeeStdcust = new BigDecimal(0);
//					orderingDealerFeeAccDeal = new BigDecimal(60);
//					orderingDealerFeeAccCust = new BigDecimal(0);
//					orderingDealerFeeInv = new BigDecimal(0);
//					orderingDealerFeeFinDeal = new BigDecimal(50);
//					orderingDealerFeeFinCust = new BigDecimal(0);
//					if (!(orderingDealerFeeStdDeal.doubleValue() == costElementVO.getOriginalOrderDeal().doubleValue()
//							&& orderingDealerFeeStdcust.doubleValue() == costElementVO.getOriginalOrderCustomer()
//									.doubleValue()
//							&& orderingDealerFeeAccDeal.doubleValue() == costElementVO.getAcceptedQuoteDeal().doubleValue()
//							&& orderingDealerFeeAccCust.doubleValue() == costElementVO.getAcceptedQuoteCustomer()
//									.doubleValue()
//							//&& orderingDealerFeeInv.doubleValue() == costElementVO.getInvoiceAmount().doubleValue()
//							&& orderingDealerFeeFinDeal.doubleValue() == costElementVO.getFinalizedQuoteDeal().doubleValue() && orderingDealerFeeFinCust
//								.doubleValue() == costElementVO.getFinalizedQuoteCustomer().doubleValue())) {
//						return false;
//					}
//				}
//				if (openEndInvoiceAdjustment.equals(costElementVO.getElementName())) {
//					if (!(openEndInvoiceAdjustmentStdDeal.doubleValue() == costElementVO.getOriginalOrderDeal()
//							.doubleValue()
//							&& openEndInvoiceAdjustmentStdcust.doubleValue() == costElementVO.getOriginalOrderCustomer()
//									.doubleValue()
//							&& openEndInvoiceAdjustmentAccDeal.doubleValue() == costElementVO.getAcceptedQuoteDeal()
//									.doubleValue()
//							&& openEndInvoiceAdjustmentAccCust.doubleValue() == costElementVO.getAcceptedQuoteCustomer()
//									.doubleValue()
//							//&& openEndInvoiceAdjustmentInv.doubleValue() == costElementVO.getInvoiceAmount().doubleValue()
//							&& openEndInvoiceAdjustmentFinDeal.doubleValue() == costElementVO.getFinalizedQuoteDeal()
//									.doubleValue() && openEndInvoiceAdjustmentFinCust.doubleValue() == costElementVO
//							.getFinalizedQuoteCustomer().doubleValue())) {
//						return false;
//					}
//				}
//				if (customerAssociationDiscount.equals(costElementVO.getElementName())) {
//					if (!(customerAssociationDiscountStdDeal.doubleValue() == costElementVO.getOriginalOrderDeal()
//							.doubleValue()
//							&& customerAssociationDiscountStdcust.doubleValue() == costElementVO.getOriginalOrderCustomer()
//									.doubleValue()
//							&& customerAssociationDiscountAccDeal.doubleValue() == costElementVO.getAcceptedQuoteDeal()
//									.doubleValue()
//							&& customerAssociationDiscountAccCust.doubleValue() == costElementVO.getAcceptedQuoteCustomer()
//									.doubleValue()
//							//&& customerAssociationDiscountInv.doubleValue() == costElementVO.getInvoiceAmount().doubleValue()
//							&& customerAssociationDiscountFinDeal.doubleValue() == costElementVO.getFinalizedQuoteDeal()
//									.doubleValue() && customerAssociationDiscountFinCust.doubleValue() == costElementVO
//							.getFinalizedQuoteCustomer().doubleValue())) {
//						return false;
//					}
//				}
//				if (vrbReclaim.equals(costElementVO.getElementName())) {
//					if (!(vrbReclaimStdDeal.doubleValue() == costElementVO.getOriginalOrderDeal().doubleValue()
//							&& vrbReclaimStdcust.doubleValue() == costElementVO.getOriginalOrderCustomer().doubleValue()
//							&& vrbReclaimAccDeal.doubleValue() == costElementVO.getAcceptedQuoteDeal().doubleValue()
//							&& vrbReclaimAccCust.doubleValue() == costElementVO.getAcceptedQuoteCustomer().doubleValue()
//							//&& vrbReclaimInv.doubleValue() == costElementVO.getInvoiceAmount().doubleValue()
//							&& vrbReclaimFinDeal.doubleValue() == costElementVO.getFinalizedQuoteDeal().doubleValue() && vrbReclaimFinCust
//								.doubleValue() == costElementVO.getFinalizedQuoteCustomer().doubleValue())) {
//						return false;
//					}
//				}
//				if (factoryEquipment.equals(costElementVO.getElementName())) {
//					factoryEquipmentStdDeal = new BigDecimal(-118.8);
//					factoryEquipmentStdcust = new BigDecimal(-118.8);
//					factoryEquipmentAccDeal = new BigDecimal(-118.8);
//					factoryEquipmentAccCust = new BigDecimal(-118.8);
//					factoryEquipmentInv = new BigDecimal(0);
//					factoryEquipmentFinDeal = new BigDecimal(-118.8);
//					factoryEquipmentFinCust = new BigDecimal(-118.8);
//					if (!(factoryEquipmentStdDeal.doubleValue() == costElementVO.getOriginalOrderDeal().doubleValue()
//							&& factoryEquipmentStdcust.doubleValue() == costElementVO.getOriginalOrderCustomer()
//									.doubleValue()
//							&& factoryEquipmentAccDeal.doubleValue() == costElementVO.getAcceptedQuoteDeal().doubleValue()
//							&& factoryEquipmentAccCust.doubleValue() == costElementVO.getAcceptedQuoteCustomer()
//									.doubleValue()
//							//&& factoryEquipmentInv.doubleValue() == costElementVO.getInvoiceAmount().doubleValue()
//							&& factoryEquipmentFinDeal.doubleValue() == costElementVO.getFinalizedQuoteDeal().doubleValue() && factoryEquipmentFinCust
//								.doubleValue() == costElementVO.getFinalizedQuoteCustomer().doubleValue())) {
//						return false;
//					}
//				}
//				if (afterMarketEquipment.equals(costElementVO.getElementName())) {
//					if (!(afterMarketEquipmentStdDeal.doubleValue() == costElementVO.getOriginalOrderDeal().doubleValue()
//							&& afterMarketEquipmentStdcust.doubleValue() == costElementVO.getOriginalOrderCustomer()
//									.doubleValue()
//							&& afterMarketEquipmentAccDeal.doubleValue() == costElementVO.getAcceptedQuoteDeal()
//									.doubleValue()
//							&& afterMarketEquipmentAccCust.doubleValue() == costElementVO.getAcceptedQuoteCustomer()
//									.doubleValue()
//							//&& afterMarketEquipmentInv.doubleValue() == costElementVO.getInvoiceAmount().doubleValue()
//							&& afterMarketEquipmentFinDeal.doubleValue() == costElementVO.getFinalizedQuoteDeal()
//									.doubleValue() && afterMarketEquipmentFinCust.doubleValue() == costElementVO
//							.getFinalizedQuoteCustomer().doubleValue())) {
//						return false;
//					}
//				}
//			}
//		} catch (Exception ex) {
//			ex.printStackTrace();
//			return false;
//		}
		return true;
	}
	public boolean testCapitalCostEnquiryScreenClosedEnd(String unitNumber) {
//		 intAmounts();
//		try {
//			List<CostElementVO> costElementList = capitalCostOverviewService.getCapitalCost(unitNumber);
//			for (CostElementVO costElementVO : costElementList) {
//				if (baseVehicle.equals(costElementVO.getElementName())) {
//					baseVehicleStdDeal = new BigDecimal(23018);
//					baseVehicleStdcust = new BigDecimal(23018);
//					baseVehicleAccDeal = new BigDecimal(23003);
//					baseVehicleAccCust = new BigDecimal(23003);
//					baseVehicleInv = new BigDecimal(23003);
//					baseVehicleFinDeal = new BigDecimal(23003);
//					baseVehicleFinCust = new BigDecimal(23003);
//					if (!(baseVehicleStdDeal.doubleValue() == costElementVO.getOriginalOrderDeal().doubleValue()
//							&& baseVehicleStdcust.doubleValue() == costElementVO.getOriginalOrderCustomer().doubleValue()
//							&& baseVehicleAccDeal.doubleValue() == costElementVO.getAcceptedQuoteDeal().doubleValue()
//							&& baseVehicleAccCust.doubleValue() == costElementVO.getAcceptedQuoteCustomer().doubleValue()
//							//&& baseVehicleInv.doubleValue() == costElementVO.getInvoiceAmount().doubleValue()
//							&& baseVehicleFinDeal.doubleValue() == costElementVO.getFinalizedQuoteDeal().doubleValue() && baseVehicleFinCust
//								.doubleValue() == costElementVO.getFinalizedQuoteCustomer().doubleValue())) {
//						return false;
//					}
//				}
//				if (holdBack.equals(costElementVO.getElementName())) {
//					holdBackStdDeal = new BigDecimal(-790);
//					holdBackStdcust = new BigDecimal(-790);
//					holdBackAccDeal = new BigDecimal(-790);
//					holdBackAccCust = new BigDecimal(-790);
//					holdBackInv = new BigDecimal(-790);
//					holdBackFinDeal = new BigDecimal(-790);
//					holdBackFinCust = new BigDecimal(-790);
//					if (!(holdBackStdDeal.doubleValue() == costElementVO.getOriginalOrderDeal().doubleValue()
//							&& holdBackStdcust.doubleValue() == costElementVO.getOriginalOrderCustomer().doubleValue()
//							&& holdBackAccDeal.doubleValue() == costElementVO.getAcceptedQuoteDeal().doubleValue()
//							&& holdBackAccCust.doubleValue() == costElementVO.getAcceptedQuoteCustomer().doubleValue()
//							//&& holdBackInv.doubleValue() == costElementVO.getInvoiceAmount().doubleValue()
//							&& holdBackFinDeal.doubleValue() == costElementVO.getFinalizedQuoteDeal().doubleValue() && holdBackFinCust
//								.doubleValue() == costElementVO.getFinalizedQuoteCustomer().doubleValue())) {
//						return false;
//					}
//				}
//				if (advertising.equals(costElementVO.getElementName())) {
//					if (!(advertisingStdDeal.doubleValue() == costElementVO.getOriginalOrderDeal().doubleValue()
//							&& advertisingStdcust.doubleValue() == costElementVO.getOriginalOrderCustomer().doubleValue()
//							&& advertisingAccDeal.doubleValue() == costElementVO.getAcceptedQuoteDeal().doubleValue()
//							&& advertisingAccCust.doubleValue() == costElementVO.getAcceptedQuoteCustomer().doubleValue()
//							//&& advertisingInv.doubleValue() == costElementVO.getInvoiceAmount().doubleValue()
//							&& advertisingFinDeal.doubleValue() == costElementVO.getFinalizedQuoteDeal().doubleValue() && advertisingFinCust
//								.doubleValue() == costElementVO.getFinalizedQuoteCustomer().doubleValue())) {
//						return false;
//					}
//				}
//				if (financeAssistance1.equals(costElementVO.getElementName())) {
//					financeAssistance1StdDeal = new BigDecimal(-295);
//					financeAssistance1Stdcust = new BigDecimal(-295);
//					financeAssistance1AccDeal = new BigDecimal(-295);
//					financeAssistance1AccCust = new BigDecimal(-295);
//					financeAssistance1Inv = new BigDecimal(-295);
//					financeAssistance1FinDeal = new BigDecimal(-295);
//					financeAssistance1FinCust = new BigDecimal(-295);
//					if (!(financeAssistance1StdDeal.doubleValue() == costElementVO.getOriginalOrderDeal().doubleValue()
//							&& financeAssistance1Stdcust.doubleValue() == costElementVO.getOriginalOrderCustomer()
//									.doubleValue()
//							&& financeAssistance1AccDeal.doubleValue() == costElementVO.getAcceptedQuoteDeal().doubleValue()
//							&& financeAssistance1AccCust.doubleValue() == costElementVO.getAcceptedQuoteCustomer()
//									.doubleValue()
//							//&& financeAssistance1Inv.doubleValue() == costElementVO.getInvoiceAmount().doubleValue()
//							&& financeAssistance1FinDeal.doubleValue() == costElementVO.getFinalizedQuoteDeal()
//									.doubleValue() && financeAssistance1FinCust.doubleValue() == costElementVO
//							.getFinalizedQuoteCustomer().doubleValue())) {
//						return false;
//					}
//				}
//				if (malIncentiveMoney.equals(costElementVO.getElementName())) {
//					if (!(malIncentiveMoneyStdDeal.doubleValue() == costElementVO.getOriginalOrderDeal().doubleValue()
//							&& malIncentiveMoneyStdcust.doubleValue() == costElementVO.getOriginalOrderCustomer()
//									.doubleValue()
//							&& malIncentiveMoneyAccDeal.doubleValue() == costElementVO.getAcceptedQuoteDeal().doubleValue()
//							&& malIncentiveMoneyAccCust.doubleValue() == costElementVO.getAcceptedQuoteCustomer()
//									.doubleValue()
//							//&& malIncentiveMoneyInv.doubleValue() == costElementVO.getInvoiceAmount().doubleValue()
//							&& malIncentiveMoneyFinDeal.doubleValue() == costElementVO.getFinalizedQuoteDeal().doubleValue() && malIncentiveMoneyFinCust
//								.doubleValue() == costElementVO.getFinalizedQuoteCustomer().doubleValue())) {
//						return false;
//					}
//				}
//				if (courtesyDeliveryFee.equals(costElementVO.getElementName())) {
//					//200
//					courtesyDeliveryFeeStdDeal = new BigDecimal(200);
//					courtesyDeliveryFeeStdcust = new BigDecimal(200);
//					courtesyDeliveryFeeAccDeal = new BigDecimal(200);
//					courtesyDeliveryFeeAccCust = new BigDecimal(200);
//					courtesyDeliveryFeeInv = new BigDecimal(0);
//					courtesyDeliveryFeeFinDeal = new BigDecimal(0);
//					courtesyDeliveryFeeFinCust = new BigDecimal(200);
//					if (!(courtesyDeliveryFeeStdDeal.doubleValue() == costElementVO.getOriginalOrderDeal().doubleValue()
//							&& courtesyDeliveryFeeStdcust.doubleValue() == costElementVO.getOriginalOrderCustomer()
//									.doubleValue()
//							&& courtesyDeliveryFeeAccDeal.doubleValue() == costElementVO.getAcceptedQuoteDeal()
//									.doubleValue()
//							&& courtesyDeliveryFeeAccCust.doubleValue() == costElementVO.getAcceptedQuoteCustomer()
//									.doubleValue()
//							//&& courtesyDeliveryFeeInv.doubleValue() == costElementVO.getInvoiceAmount().doubleValue()
//							&& courtesyDeliveryFeeFinDeal.doubleValue() == costElementVO.getFinalizedQuoteDeal()
//									.doubleValue() && courtesyDeliveryFeeFinCust.doubleValue() == costElementVO
//							.getFinalizedQuoteCustomer().doubleValue())) {
//						return false;
//					}
//				}
//				if (distantDeliveryCredit.equals(costElementVO.getElementName())) {
//					if (!(distantDeliveryCreditStdDeal.doubleValue() == costElementVO.getOriginalOrderDeal().doubleValue()
//							&& distantDeliveryCreditStdcust.doubleValue() == costElementVO.getOriginalOrderCustomer()
//									.doubleValue()
//							&& distantDeliveryCreditAccDeal.doubleValue() == costElementVO.getAcceptedQuoteDeal()
//									.doubleValue()
//							&& distantDeliveryCreditAccCust.doubleValue() == costElementVO.getAcceptedQuoteCustomer()
//									.doubleValue()
//							//&& distantDeliveryCreditInv.doubleValue() == costElementVO.getInvoiceAmount().doubleValue()
//							&& distantDeliveryCreditFinDeal.doubleValue() == costElementVO.getFinalizedQuoteDeal()
//									.doubleValue() && distantDeliveryCreditFinCust.doubleValue() == costElementVO
//							.getFinalizedQuoteCustomer().doubleValue())) {
//						return false;
//					}
//				}
//				if (fleetAllowance.equals(costElementVO.getElementName())) {
//					if (!(fleetAllowanceStdDeal.doubleValue() == costElementVO.getOriginalOrderDeal().doubleValue()
//							&& fleetAllowanceStdcust.doubleValue() == costElementVO.getOriginalOrderCustomer().doubleValue()
//							&& fleetAllowanceAccDeal.doubleValue() == costElementVO.getAcceptedQuoteDeal().doubleValue()
//							&& fleetAllowanceAccCust.doubleValue() == costElementVO.getAcceptedQuoteCustomer().doubleValue()
//							//&& fleetAllowanceInv.doubleValue() == costElementVO.getInvoiceAmount().doubleValue()
//							&& fleetAllowanceFinDeal.doubleValue() == costElementVO.getFinalizedQuoteDeal().doubleValue() && fleetAllowanceFinCust
//								.doubleValue() == costElementVO.getFinalizedQuoteCustomer().doubleValue())) {
//						return false;
//					}
//				}
//				if (retailRebate.equals(costElementVO.getElementName())) {
//					if (!(retailRebateStdDeal.doubleValue() == costElementVO.getOriginalOrderDeal().doubleValue()
//							&& retailRebateStdcust.doubleValue() == costElementVO.getOriginalOrderCustomer().doubleValue()
//							&& retailRebateAccDeal.doubleValue() == costElementVO.getAcceptedQuoteDeal().doubleValue()
//							&& retailRebateAccCust.doubleValue() == costElementVO.getAcceptedQuoteCustomer().doubleValue()
//							//&& retailRebateInv.doubleValue() == costElementVO.getInvoiceAmount().doubleValue()
//							&& retailRebateFinDeal.doubleValue() == costElementVO.getFinalizedQuoteDeal().doubleValue() && retailRebateFinCust
//								.doubleValue() == costElementVO.getFinalizedQuoteCustomer().doubleValue())) {
//						return false;
//					}
//				}
//				if (locateBaseCostVariance.equals(costElementVO.getElementName())) {
//					if (!(locateBaseCostVarianceStdDeal.doubleValue() == costElementVO.getOriginalOrderDeal().doubleValue()
//							&& locateBaseCostVarianceStdcust.doubleValue() == costElementVO.getOriginalOrderCustomer()
//									.doubleValue()
//							&& locateBaseCostVarianceAccDeal.doubleValue() == costElementVO.getAcceptedQuoteDeal()
//									.doubleValue()
//							&& locateBaseCostVarianceAccCust.doubleValue() == costElementVO.getAcceptedQuoteCustomer()
//									.doubleValue()
//							//&& locateBaseCostVarianceInv.doubleValue() == costElementVO.getInvoiceAmount().doubleValue()
//							&& locateBaseCostVarianceFinDeal.doubleValue() == costElementVO.getFinalizedQuoteDeal()
//									.doubleValue() && locateBaseCostVarianceFinCust.doubleValue() == costElementVO
//							.getFinalizedQuoteCustomer().doubleValue())) {
//						return false;
//					}
//				}
//				if (earlyOrderIncentiveOffInvoice.equals(costElementVO.getElementName())) {
//					if (!(earlyOrderIncentiveOffInvoiceStdDeal.doubleValue() == costElementVO.getOriginalOrderDeal()
//							.doubleValue()
//							&& earlyOrderIncentiveOffInvoiceStdcust.doubleValue() == costElementVO
//									.getOriginalOrderCustomer().doubleValue()
//							&& earlyOrderIncentiveOffInvoiceAccDeal.doubleValue() == costElementVO.getAcceptedQuoteDeal()
//									.doubleValue()
//							&& earlyOrderIncentiveOffInvoiceAccCust.doubleValue() == costElementVO
//									.getAcceptedQuoteCustomer().doubleValue()
//							//&& earlyOrderIncentiveOffInvoiceInv.doubleValue() == costElementVO.getInvoiceAmount().doubleValue()
//							&& earlyOrderIncentiveOffInvoiceFinDeal.doubleValue() == costElementVO.getFinalizedQuoteDeal()
//									.doubleValue() && earlyOrderIncentiveOffInvoiceFinCust.doubleValue() == costElementVO
//							.getFinalizedQuoteCustomer().doubleValue())) {
//						return false;
//					}
//				}
//				if (volumeRelatedBonus.equals(costElementVO.getElementName())) {
//					volumeRelatedBonusStdDeal = new BigDecimal(-6000);
//					volumeRelatedBonusStdcust = new BigDecimal(-6000);
//					volumeRelatedBonusAccDeal = new BigDecimal(-6000);
//					volumeRelatedBonusAccCust = new BigDecimal(-6000);
//					volumeRelatedBonusInv = new BigDecimal(-6000);
//					volumeRelatedBonusFinDeal = new BigDecimal(-6000);
//					volumeRelatedBonusFinCust = new BigDecimal(-6000);
//					if (!(volumeRelatedBonusStdDeal.doubleValue() == costElementVO.getOriginalOrderDeal().doubleValue()
//							&& volumeRelatedBonusStdcust.doubleValue() == costElementVO.getOriginalOrderCustomer()
//									.doubleValue()
//							&& volumeRelatedBonusAccDeal.doubleValue() == costElementVO.getAcceptedQuoteDeal().doubleValue()
//							&& volumeRelatedBonusAccCust.doubleValue() == costElementVO.getAcceptedQuoteCustomer()
//									.doubleValue()
//							//&& volumeRelatedBonusInv.doubleValue() == costElementVO.getInvoiceAmount().doubleValue()
//							&& volumeRelatedBonusFinDeal.doubleValue() == costElementVO.getFinalizedQuoteDeal()
//									.doubleValue() && volumeRelatedBonusFinCust.doubleValue() == costElementVO
//							.getFinalizedQuoteCustomer().doubleValue())) {
//						return false;
//					}
//				}
//				if (mikeAlbertPriceProtection.equals(costElementVO.getElementName())) {
//					if (!(mikeAlbertPriceProtectionStdDeal.doubleValue() == costElementVO.getOriginalOrderDeal()
//							.doubleValue()
//							&& mikeAlbertPriceProtectionStdcust.doubleValue() == costElementVO.getOriginalOrderCustomer()
//									.doubleValue()
//							&& mikeAlbertPriceProtectionAccDeal.doubleValue() == costElementVO.getAcceptedQuoteDeal()
//									.doubleValue()
//							&& mikeAlbertPriceProtectionAccCust.doubleValue() == costElementVO.getAcceptedQuoteCustomer()
//									.doubleValue()
//							//&& mikeAlbertPriceProtectionInv.doubleValue() == costElementVO.getInvoiceAmount().doubleValue()
//							&& mikeAlbertPriceProtectionFinDeal.doubleValue() == costElementVO.getFinalizedQuoteDeal()
//									.doubleValue() && mikeAlbertPriceProtectionFinCust.doubleValue() == costElementVO
//							.getFinalizedQuoteCustomer().doubleValue())) {
//						return false;
//					}
//				}
//				if (yearEndCarryover.equals(costElementVO.getElementName())) {
//					if (!(yearEndCarryoverStdDeal.doubleValue() == costElementVO.getOriginalOrderDeal().doubleValue()
//							&& yearEndCarryoverStdcust.doubleValue() == costElementVO.getOriginalOrderCustomer()
//									.doubleValue()
//							&& yearEndCarryoverAccDeal.doubleValue() == costElementVO.getAcceptedQuoteDeal().doubleValue()
//							&& yearEndCarryoverAccCust.doubleValue() == costElementVO.getAcceptedQuoteCustomer()
//									.doubleValue()
//							//&& yearEndCarryoverInv.doubleValue() == costElementVO.getInvoiceAmount().doubleValue()
//							&& yearEndCarryoverFinDeal.doubleValue() == costElementVO.getFinalizedQuoteDeal().doubleValue() && yearEndCarryoverFinCust
//								.doubleValue() == costElementVO.getFinalizedQuoteCustomer().doubleValue())) {
//						return false;
//					}
//				}
//				if (preDeliveryInspection.equals(costElementVO.getElementName())) {
//					if (!(preDeliveryInspectionStdDeal.doubleValue() == costElementVO.getOriginalOrderDeal().doubleValue()
//							&& preDeliveryInspectionStdcust.doubleValue() == costElementVO.getOriginalOrderCustomer()
//									.doubleValue()
//							&& preDeliveryInspectionAccDeal.doubleValue() == costElementVO.getAcceptedQuoteDeal()
//									.doubleValue()
//							&& preDeliveryInspectionAccCust.doubleValue() == costElementVO.getAcceptedQuoteCustomer()
//									.doubleValue()
//							//&& preDeliveryInspectionInv.doubleValue() == costElementVO.getInvoiceAmount().doubleValue()
//							&& preDeliveryInspectionFinDeal.doubleValue() == costElementVO.getFinalizedQuoteDeal()
//									.doubleValue() && preDeliveryInspectionFinCust.doubleValue() == costElementVO
//							.getFinalizedQuoteCustomer().doubleValue())) {
//						return false;
//					}
//				}
//				if (fuelCredit.equals(costElementVO.getElementName())) {
//					fuelCreditStdDeal = new BigDecimal(0);
//					fuelCreditStdcust = new BigDecimal(0);
//					fuelCreditAccDeal = new BigDecimal(0);
//					fuelCreditAccCust = new BigDecimal(0);
//					fuelCreditInv = new BigDecimal(17.94);
//					fuelCreditFinDeal = new BigDecimal(17.94);
//					fuelCreditFinCust = new BigDecimal(0);
//					if (!(fuelCreditStdDeal.doubleValue() == costElementVO.getOriginalOrderDeal().doubleValue()
//							&& fuelCreditStdcust.doubleValue() == costElementVO.getOriginalOrderCustomer().doubleValue()
//							&& fuelCreditAccDeal.doubleValue() == costElementVO.getAcceptedQuoteDeal().doubleValue()
//							&& fuelCreditAccCust.doubleValue() == costElementVO.getAcceptedQuoteCustomer().doubleValue()
//							//&& fuelCreditInv.doubleValue() == costElementVO.getInvoiceAmount().doubleValue()
//							&& fuelCreditFinDeal.doubleValue() == costElementVO.getFinalizedQuoteDeal().doubleValue() && fuelCreditFinCust
//								.doubleValue() == costElementVO.getFinalizedQuoteCustomer().doubleValue())) {
//						return false;
//					}
//				}
//				if (lateInTransit.equals(costElementVO.getElementName())) {
//					if (!(lateInTransitStdDeal.doubleValue() == costElementVO.getOriginalOrderDeal().doubleValue()
//							&& lateInTransitStdcust.doubleValue() == costElementVO.getOriginalOrderCustomer().doubleValue()
//							&& lateInTransitAccDeal.doubleValue() == costElementVO.getAcceptedQuoteDeal().doubleValue()
//							&& lateInTransitAccCust.doubleValue() == costElementVO.getAcceptedQuoteCustomer().doubleValue()
//							//&& lateInTransitInv.doubleValue() == costElementVO.getInvoiceAmount().doubleValue()
//							&& lateInTransitFinDeal.doubleValue() == costElementVO.getFinalizedQuoteDeal().doubleValue() && lateInTransitFinCust
//								.doubleValue() == costElementVO.getFinalizedQuoteCustomer().doubleValue())) {
//						return false;
//					}
//				}
//				if (freight.equals(costElementVO.getElementName())) {
//					freightStdDeal = new BigDecimal(870);
//					freightStdcust = new BigDecimal(870);
//					freightAccDeal = new BigDecimal(870);
//					freightAccCust = new BigDecimal(870);
//					freightInv = new BigDecimal(870);
//					freightFinDeal = new BigDecimal(870);
//					freightFinCust = new BigDecimal(870);
//					if (!(freightStdDeal.doubleValue() == costElementVO.getOriginalOrderDeal().doubleValue()
//							&& freightStdcust.doubleValue() == costElementVO.getOriginalOrderCustomer().doubleValue()
//							&& freightAccDeal.doubleValue() == costElementVO.getAcceptedQuoteDeal().doubleValue()
//							&& freightAccCust.doubleValue() == costElementVO.getAcceptedQuoteCustomer().doubleValue()
//							//&& freightInv.doubleValue() == costElementVO.getInvoiceAmount().doubleValue()
//							&& freightFinDeal.doubleValue() == costElementVO.getFinalizedQuoteDeal().doubleValue() && freightFinCust
//								.doubleValue() == costElementVO.getFinalizedQuoteCustomer().doubleValue())) {
//						return false;
//					}
//				}
//				if (orderingDealerFee.equals(costElementVO.getElementName())) {
//					orderingDealerFeeStdDeal = new BigDecimal(150);
//					orderingDealerFeeStdcust = new BigDecimal(150);
//					orderingDealerFeeAccDeal = new BigDecimal(150);
//					orderingDealerFeeAccCust = new BigDecimal(150);
//					orderingDealerFeeInv = new BigDecimal(65);
//					orderingDealerFeeFinDeal = new BigDecimal(65);
//					orderingDealerFeeFinCust = new BigDecimal(150);
//					if (!(orderingDealerFeeStdDeal.doubleValue() == costElementVO.getOriginalOrderDeal().doubleValue()
//							&& orderingDealerFeeStdcust.doubleValue() == costElementVO.getOriginalOrderCustomer()
//									.doubleValue()
//							&& orderingDealerFeeAccDeal.doubleValue() == costElementVO.getAcceptedQuoteDeal().doubleValue()
//							&& orderingDealerFeeAccCust.doubleValue() == costElementVO.getAcceptedQuoteCustomer()
//									.doubleValue()
//							//&& orderingDealerFeeInv.doubleValue() == costElementVO.getInvoiceAmount().doubleValue()
//							&& orderingDealerFeeFinDeal.doubleValue() == costElementVO.getFinalizedQuoteDeal().doubleValue() && orderingDealerFeeFinCust
//								.doubleValue() == costElementVO.getFinalizedQuoteCustomer().doubleValue())) {
//						return false;
//					}
//				}
//				if (openEndInvoiceAdjustment.equals(costElementVO.getElementName())) {
//					if (!(openEndInvoiceAdjustmentStdDeal.doubleValue() == costElementVO.getOriginalOrderDeal()
//							.doubleValue()
//							&& openEndInvoiceAdjustmentStdcust.doubleValue() == costElementVO.getOriginalOrderCustomer()
//									.doubleValue()
//							&& openEndInvoiceAdjustmentAccDeal.doubleValue() == costElementVO.getAcceptedQuoteDeal()
//									.doubleValue()
//							&& openEndInvoiceAdjustmentAccCust.doubleValue() == costElementVO.getAcceptedQuoteCustomer()
//									.doubleValue()
//							//&& openEndInvoiceAdjustmentInv.doubleValue() == costElementVO.getInvoiceAmount().doubleValue()
//							&& openEndInvoiceAdjustmentFinDeal.doubleValue() == costElementVO.getFinalizedQuoteDeal()
//									.doubleValue() && openEndInvoiceAdjustmentFinCust.doubleValue() == costElementVO
//							.getFinalizedQuoteCustomer().doubleValue())) {
//						return false;
//					}
//				}
//				if (customerAssociationDiscount.equals(costElementVO.getElementName())) {
//					if (!(customerAssociationDiscountStdDeal.doubleValue() == costElementVO.getOriginalOrderDeal()
//							.doubleValue()
//							&& customerAssociationDiscountStdcust.doubleValue() == costElementVO.getOriginalOrderCustomer()
//									.doubleValue()
//							&& customerAssociationDiscountAccDeal.doubleValue() == costElementVO.getAcceptedQuoteDeal()
//									.doubleValue()
//							&& customerAssociationDiscountAccCust.doubleValue() == costElementVO.getAcceptedQuoteCustomer()
//									.doubleValue()
//							//&& customerAssociationDiscountInv.doubleValue() == costElementVO.getInvoiceAmount().doubleValue()
//							&& customerAssociationDiscountFinDeal.doubleValue() == costElementVO.getFinalizedQuoteDeal()
//									.doubleValue() && customerAssociationDiscountFinCust.doubleValue() == costElementVO
//							.getFinalizedQuoteCustomer().doubleValue())) {
//						return false;
//					}
//				}
//				if (vrbReclaim.equals(costElementVO.getElementName())) {
//					if (!(vrbReclaimStdDeal.doubleValue() == costElementVO.getOriginalOrderDeal().doubleValue()
//							&& vrbReclaimStdcust.doubleValue() == costElementVO.getOriginalOrderCustomer().doubleValue()
//							&& vrbReclaimAccDeal.doubleValue() == costElementVO.getAcceptedQuoteDeal().doubleValue()
//							&& vrbReclaimAccCust.doubleValue() == costElementVO.getAcceptedQuoteCustomer().doubleValue()
//							//&& vrbReclaimInv.doubleValue() == costElementVO.getInvoiceAmount().doubleValue()
//							&& vrbReclaimFinDeal.doubleValue() == costElementVO.getFinalizedQuoteDeal().doubleValue() && vrbReclaimFinCust
//								.doubleValue() == costElementVO.getFinalizedQuoteCustomer().doubleValue())) {
//						return false;
//					}
//				}
//				if (factoryEquipment.equals(costElementVO.getElementName())) {
//					factoryEquipmentStdDeal = new BigDecimal(444);
//					factoryEquipmentStdcust = new BigDecimal(444);
//					factoryEquipmentAccDeal = new BigDecimal(444);
//					factoryEquipmentAccCust = new BigDecimal(444);
//					factoryEquipmentInv = new BigDecimal(444);
//					factoryEquipmentFinDeal = new BigDecimal(444);
//					factoryEquipmentFinCust = new BigDecimal(444);
//					if (!(factoryEquipmentStdDeal.doubleValue() == costElementVO.getOriginalOrderDeal().doubleValue()
//							&& factoryEquipmentStdcust.doubleValue() == costElementVO.getOriginalOrderCustomer()
//									.doubleValue()
//							&& factoryEquipmentAccDeal.doubleValue() == costElementVO.getAcceptedQuoteDeal().doubleValue()
//							&& factoryEquipmentAccCust.doubleValue() == costElementVO.getAcceptedQuoteCustomer()
//									.doubleValue()
//							//&& factoryEquipmentInv.doubleValue() == costElementVO.getInvoiceAmount().doubleValue()
//							&& factoryEquipmentFinDeal.doubleValue() == costElementVO.getFinalizedQuoteDeal().doubleValue() && factoryEquipmentFinCust
//								.doubleValue() == costElementVO.getFinalizedQuoteCustomer().doubleValue())) {
//						return false;
//					}
//				}
//				if (afterMarketEquipment.equals(costElementVO.getElementName())) {//8500
//					afterMarketEquipmentStdDeal = new BigDecimal(8500);
//					afterMarketEquipmentStdcust = new BigDecimal(8500);
//					afterMarketEquipmentAccDeal = new BigDecimal(8500);
//					afterMarketEquipmentAccCust = new BigDecimal(8500);
//					afterMarketEquipmentInv = new BigDecimal(8500);
//					afterMarketEquipmentFinDeal = new BigDecimal(8500);
//					afterMarketEquipmentFinCust = new BigDecimal(8500);
//					if (!(afterMarketEquipmentStdDeal.doubleValue() == costElementVO.getOriginalOrderDeal().doubleValue()
//							&& afterMarketEquipmentStdcust.doubleValue() == costElementVO.getOriginalOrderCustomer()
//									.doubleValue()
//							&& afterMarketEquipmentAccDeal.doubleValue() == costElementVO.getAcceptedQuoteDeal()
//									.doubleValue()
//							&& afterMarketEquipmentAccCust.doubleValue() == costElementVO.getAcceptedQuoteCustomer()
//									.doubleValue()
//							//&& afterMarketEquipmentInv.doubleValue() == costElementVO.getInvoiceAmount().doubleValue()
//							&& afterMarketEquipmentFinDeal.doubleValue() == costElementVO.getFinalizedQuoteDeal()
//									.doubleValue() && afterMarketEquipmentFinCust.doubleValue() == costElementVO
//							.getFinalizedQuoteCustomer().doubleValue())) {
//						return false;
//					}
//				}
//			}
//		} catch (Exception ex) {
//			ex.printStackTrace();
//			return false;
//		}
		return true;
	}
	private void intAmounts(){
		 baseVehicleStdDeal = new BigDecimal(0);
		 baseVehicleStdcust = new BigDecimal(0);
		 baseVehicleAccDeal = new BigDecimal(0);
		 baseVehicleAccCust = new BigDecimal(0);
		 baseVehicleInv = new BigDecimal(0);
		 baseVehicleFinDeal = new BigDecimal(0);
		 baseVehicleFinCust = new BigDecimal(0);

		 holdBackStdDeal = new BigDecimal(0);
		 holdBackStdcust = new BigDecimal(0);
		 holdBackAccDeal = new BigDecimal(0);
		 holdBackAccCust = new BigDecimal(0);
		 holdBackInv = new BigDecimal(0);
		 holdBackFinDeal = new BigDecimal(0);
		 holdBackFinCust = new BigDecimal(0);

		 advertisingStdDeal = new BigDecimal(0);
		 advertisingStdcust = new BigDecimal(0);
		 advertisingAccDeal = new BigDecimal(0);
		 advertisingAccCust = new BigDecimal(0);
		 advertisingInv = new BigDecimal(0);
		 advertisingFinDeal = new BigDecimal(0);
		 advertisingFinCust = new BigDecimal(0);

		 financeAssistance1StdDeal = new BigDecimal(0);
		 financeAssistance1Stdcust = new BigDecimal(0);
		 financeAssistance1AccDeal = new BigDecimal(0);
		 financeAssistance1AccCust = new BigDecimal(0);
		 financeAssistance1Inv = new BigDecimal(0);
		 financeAssistance1FinDeal = new BigDecimal(0);
		 financeAssistance1FinCust = new BigDecimal(0);

		 malIncentiveMoneyStdDeal = new BigDecimal(0);
		 malIncentiveMoneyStdcust = new BigDecimal(0);
		 malIncentiveMoneyAccDeal = new BigDecimal(0);
		 malIncentiveMoneyAccCust = new BigDecimal(0);
		 malIncentiveMoneyInv = new BigDecimal(0);
		 malIncentiveMoneyFinDeal = new BigDecimal(0);
		 malIncentiveMoneyFinCust = new BigDecimal(0);

		 courtesyDeliveryFeeStdDeal = new BigDecimal(0);
		 courtesyDeliveryFeeStdcust = new BigDecimal(0);
		 courtesyDeliveryFeeAccDeal = new BigDecimal(0);
		 courtesyDeliveryFeeAccCust = new BigDecimal(0);
		 courtesyDeliveryFeeInv = new BigDecimal(0);
		 courtesyDeliveryFeeFinDeal = new BigDecimal(0);
		 courtesyDeliveryFeeFinCust = new BigDecimal(0);

		 distantDeliveryCreditStdDeal = new BigDecimal(0);
		 distantDeliveryCreditStdcust = new BigDecimal(0);
		 distantDeliveryCreditAccDeal = new BigDecimal(0);
		 distantDeliveryCreditAccCust = new BigDecimal(0);
		 distantDeliveryCreditInv = new BigDecimal(0);
		 distantDeliveryCreditFinDeal = new BigDecimal(0);
		 distantDeliveryCreditFinCust = new BigDecimal(0);

		 fleetAllowanceStdDeal = new BigDecimal(0);
		 fleetAllowanceStdcust = new BigDecimal(0);
		 fleetAllowanceAccDeal = new BigDecimal(0);
		 fleetAllowanceAccCust = new BigDecimal(0);
		 fleetAllowanceInv = new BigDecimal(0);
		 fleetAllowanceFinDeal = new BigDecimal(0);
		 fleetAllowanceFinCust = new BigDecimal(0);

		 retailRebateStdDeal = new BigDecimal(0);
		 retailRebateStdcust = new BigDecimal(0);
		 retailRebateAccDeal = new BigDecimal(0);
		 retailRebateAccCust = new BigDecimal(0);
		 retailRebateInv = new BigDecimal(0);
		 retailRebateFinDeal = new BigDecimal(0);
		 retailRebateFinCust = new BigDecimal(0);

		 locateBaseCostVarianceStdDeal = new BigDecimal(0);
		 locateBaseCostVarianceStdcust = new BigDecimal(0);
		 locateBaseCostVarianceAccDeal = new BigDecimal(0);
		 locateBaseCostVarianceAccCust = new BigDecimal(0);
		 locateBaseCostVarianceInv = new BigDecimal(0);
		 locateBaseCostVarianceFinDeal = new BigDecimal(0);
		 locateBaseCostVarianceFinCust = new BigDecimal(0);

		 earlyOrderIncentiveOffInvoiceStdDeal = new BigDecimal(0);
		 earlyOrderIncentiveOffInvoiceStdcust = new BigDecimal(0);
		 earlyOrderIncentiveOffInvoiceAccDeal = new BigDecimal(0);
		 earlyOrderIncentiveOffInvoiceAccCust = new BigDecimal(0);
		 earlyOrderIncentiveOffInvoiceInv = new BigDecimal(0);
		 earlyOrderIncentiveOffInvoiceFinDeal = new BigDecimal(0);
		 earlyOrderIncentiveOffInvoiceFinCust = new BigDecimal(0);

		 volumeRelatedBonusStdDeal = new BigDecimal(0);
		 volumeRelatedBonusStdcust = new BigDecimal(0);
		 volumeRelatedBonusAccDeal = new BigDecimal(0);
		 volumeRelatedBonusAccCust = new BigDecimal(0);
		 volumeRelatedBonusInv = new BigDecimal(0);
		 volumeRelatedBonusFinDeal = new BigDecimal(0);
		 volumeRelatedBonusFinCust = new BigDecimal(0);

		 mikeAlbertPriceProtectionStdDeal = new BigDecimal(0);
		 mikeAlbertPriceProtectionStdcust = new BigDecimal(0);
		 mikeAlbertPriceProtectionAccDeal = new BigDecimal(0);
		 mikeAlbertPriceProtectionAccCust = new BigDecimal(0);
		 mikeAlbertPriceProtectionInv = new BigDecimal(0);
		 mikeAlbertPriceProtectionFinDeal = new BigDecimal(0);
		 mikeAlbertPriceProtectionFinCust = new BigDecimal(0);

		 yearEndCarryoverStdDeal = new BigDecimal(0);
		 yearEndCarryoverStdcust = new BigDecimal(0);
		 yearEndCarryoverAccDeal = new BigDecimal(0);
		 yearEndCarryoverAccCust = new BigDecimal(0);
		 yearEndCarryoverInv = new BigDecimal(0);
		 yearEndCarryoverFinDeal = new BigDecimal(0);
		 yearEndCarryoverFinCust = new BigDecimal(0);

		 preDeliveryInspectionStdDeal = new BigDecimal(0);
		 preDeliveryInspectionStdcust = new BigDecimal(0);
		 preDeliveryInspectionAccDeal = new BigDecimal(0);
		 preDeliveryInspectionAccCust = new BigDecimal(0);
		 preDeliveryInspectionInv = new BigDecimal(0);
		 preDeliveryInspectionFinDeal = new BigDecimal(0);
		 preDeliveryInspectionFinCust = new BigDecimal(0);

		 fuelCreditStdDeal = new BigDecimal(0);
		 fuelCreditStdcust = new BigDecimal(0);
		 fuelCreditAccDeal = new BigDecimal(0);
		 fuelCreditAccCust = new BigDecimal(0);
		 fuelCreditInv = new BigDecimal(0);
		 fuelCreditFinDeal = new BigDecimal(0);
		 fuelCreditFinCust = new BigDecimal(0);

		 lateInTransitStdDeal = new BigDecimal(0);
		 lateInTransitStdcust = new BigDecimal(0);
		 lateInTransitAccDeal = new BigDecimal(0);
		 lateInTransitAccCust = new BigDecimal(0);
		 lateInTransitInv = new BigDecimal(0);
		 lateInTransitFinDeal = new BigDecimal(0);
		 lateInTransitFinCust = new BigDecimal(0);

		 freightStdDeal = new BigDecimal(0);
		 freightStdcust = new BigDecimal(0);
		 freightAccDeal = new BigDecimal(0);
		 freightAccCust = new BigDecimal(0);
		 freightInv = new BigDecimal(0);
		 freightFinDeal = new BigDecimal(0);
		 freightFinCust = new BigDecimal(0);

		 orderingDealerFeeStdDeal = new BigDecimal(0);
		 orderingDealerFeeStdcust = new BigDecimal(0);
		 orderingDealerFeeAccDeal = new BigDecimal(0);
		 orderingDealerFeeAccCust = new BigDecimal(0);
		 orderingDealerFeeInv = new BigDecimal(0);
		 orderingDealerFeeFinDeal = new BigDecimal(0);
		 orderingDealerFeeFinCust = new BigDecimal(0);

		 openEndInvoiceAdjustmentStdDeal = new BigDecimal(0);
		 openEndInvoiceAdjustmentStdcust = new BigDecimal(0);
		 openEndInvoiceAdjustmentAccDeal = new BigDecimal(0);
		 openEndInvoiceAdjustmentAccCust = new BigDecimal(0);
		 openEndInvoiceAdjustmentInv = new BigDecimal(0);
		 openEndInvoiceAdjustmentFinDeal = new BigDecimal(0);
		 openEndInvoiceAdjustmentFinCust = new BigDecimal(0);

		 customerAssociationDiscountStdDeal = new BigDecimal(0);
		 customerAssociationDiscountStdcust = new BigDecimal(0);
		 customerAssociationDiscountAccDeal = new BigDecimal(0);
		 customerAssociationDiscountAccCust = new BigDecimal(0);
		 customerAssociationDiscountInv = new BigDecimal(0);
		 customerAssociationDiscountFinDeal = new BigDecimal(0);
		 customerAssociationDiscountFinCust = new BigDecimal(0);

		 vrbReclaimStdDeal = new BigDecimal(0);
		 vrbReclaimStdcust = new BigDecimal(0);
		 vrbReclaimAccDeal = new BigDecimal(0);
		 vrbReclaimAccCust = new BigDecimal(0);
		 vrbReclaimInv = new BigDecimal(0);
		 vrbReclaimFinDeal = new BigDecimal(0);
		 vrbReclaimFinCust = new BigDecimal(0);;
		 factoryEquipmentStdDeal = new BigDecimal(0);
		 factoryEquipmentStdcust = new BigDecimal(0);
		 factoryEquipmentAccDeal = new BigDecimal(0);
		 factoryEquipmentAccCust = new BigDecimal(0);
		 factoryEquipmentInv = new BigDecimal(0);
		 factoryEquipmentFinDeal = new BigDecimal(0);
		 factoryEquipmentFinCust = new BigDecimal(0);

		 afterMarketEquipmentStdDeal = new BigDecimal(0);
		 afterMarketEquipmentStdcust = new BigDecimal(0);
		 afterMarketEquipmentAccDeal = new BigDecimal(0);
		 afterMarketEquipmentAccCust = new BigDecimal(0);
		 afterMarketEquipmentInv = new BigDecimal(0);
		 afterMarketEquipmentFinDeal = new BigDecimal(0);
		 afterMarketEquipmentFinCust = new BigDecimal(0);
	}
}
