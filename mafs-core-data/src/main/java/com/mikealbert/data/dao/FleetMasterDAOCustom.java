package com.mikealbert.data.dao;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.mikealbert.data.vo.AccessoryVO;
import com.mikealbert.data.vo.StockUnitsLovVO;
import com.mikealbert.exception.MalBusinessException;

public interface FleetMasterDAOCustom  {	
	public String getFleetStatus(Long fmsId);
	public List<Long> findOnContractOrOnOrderFmsIdsByVinOrVehicleCostCenter(String Vin, String VehicleCostCentre);
	public List<StockUnitsLovVO> findStockUnits(String unitNo, String vehicleDesc, Pageable pageable, Sort sort);
	public int findStockUnitCount(String unitNo, String vehicleDesc);
	public List<String> getStandardEquipmentForFmsId(long fmsId);
	public List<String> getModelEquipmentForFmsId(long fmsId);
	public List<String> getDealerEquipmentForFmsId(long fmsId);
	
	public List<AccessoryVO> getStandardAccessoriesByFmsId(long fmsId);
	public List<AccessoryVO> getOptionalAccessoriesByFmsId(long fmsId);
	public List<AccessoryVO> getDealerAccessoriesByFmsId(long fmsId);	
	
	public BigDecimal getStockUnitCost(String unitNo, Long cId) throws MalBusinessException;
	
	public BigDecimal getLastOdoMeterReadingByFmsId(Long fmsId);
	
	public boolean isActivePOUnit(String unitNo, Long qprId, Long cId) throws MalBusinessException;
	
	public boolean isActivePOUnit(Long fmsId) throws MalBusinessException;
	
	public BigDecimal getUnpaidVehicleCapitalCostFromDocsByFmsId(Long fmsId) throws MalBusinessException;
	public BigDecimal getUnpaidDealerAccessoryCostByFmsId(Long fmsId) throws MalBusinessException;
	
	public List<Long> findOnContractOrInServiceByVin(String vin);
}
