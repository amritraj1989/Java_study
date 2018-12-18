package com.mikealbert.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.vo.AccessoryVO;
import com.mikealbert.data.vo.StockUnitsLovVO;
import com.mikealbert.exception.MalBusinessException;

/**
 * Public Interface implemented by {@link com.mikealbert.vision.service.FleetMasterServiceImpl} for interacting with business service methods concerning {@link com.mikealbert.data.entity.FleetMaster}(s).
 *
 * @see com.mikealbert.data.entity.FleetMaster
 * @see com.mikealbert.vision.service.FleetMasterServiceImpl
 **/
public interface FleetMasterService {
	
	static final String SORT_BY_UNIT_NO = "unitNo";
	static final String SORT_BY_VEH_DESC = "vehicleDescription";
	
	public List<FleetMaster> getFleetMasterFilterCurrentAllocaton(String unitNo, Long drvId);
	public List<FleetMaster> findFleetMasterByVinLastSix(String vin, boolean execCountQuery, PageRequest page);
	long getFleetMasterCountByVINLastSix(String vin);
	public FleetMaster getFleetMasterByFmsId(long fmsId);	
	public FleetMaster findByUnitNo(String unitNo);
	public List<FleetMaster> findRelatedFleetMasters(String vin);
	public FleetMaster getFleetMasterByDocId(Long docId);
	public boolean	getHighMileage(Long fmsId);
	public Long	getScheduleLatestOdoReading(Long fmsId)throws MalBusinessException;
	public void saveUpdateFleetMaster(FleetMaster fleetMaster);
	public void saveUpdateFleetMaster(List<FleetMaster> fleetMaster);
	public List<Long> findOnContractOrOnOrderFmsIdsByVinOrVehicleCostCenter(String vin, String vehicleCostCentre);
	public boolean	isTerminatedUnit(Long fmsId);
	public ExternalAccount getExternalAccountForUnit(FleetMaster fleetMaster);
	public List<StockUnitsLovVO> findStockUnitsList(String unitNo, String vehicleDesc, Pageable pageable, Sort sort);
	public int findStockUnitsListCount(String unitNo, String vehicleDesc);
	public List<String> getStandardEquipmentForFmsId(long fmsId);
	public List<String> getModelEquipmentForFmsId(long fmsId);
	public List<String> getDealerEquipmentForFmsId(long fmsId);
	
	public FleetMaster getFleetMasterByUnitNoWithDelayReason(String unitNo);
	public FleetMaster getFleetMasterByFmsIdWithOdoList(long fmsId);
	public String resolveStockUnitLovSortByName(String columnName);
	
	public List<AccessoryVO> getStandardAccessories(long fmsId);
	public List<AccessoryVO> getOptionalAccessories(long fmsId);
	public List<AccessoryVO> getDealerAccessories(long fmsId);	
	
	public BigDecimal getStockUnitCost(String unitNo, Long cId) throws MalBusinessException;
	
	public BigDecimal getLastOdometerReadingByFmsId(Long fmsId) throws MalBusinessException;
	
	public List<StockUnitsLovVO> stockUnitListFromCache();	
	
	public String checkVehicleStatus(long fmsId);
	
	public boolean isActivePOUnit(String unitNo, Long qprId, Long cId) throws MalBusinessException;
	public boolean isActivePOUnit(Long fmsId) throws MalBusinessException;
	
	public BigDecimal getUnpaidVehicleCapitalCostFromDocsByFmsId(Long fmsId) throws MalBusinessException;
	
	public List<Long> findOnContractOrInServiceByVin(String vin);	
	
	public BigDecimal getInventoryCostByFmsId(FleetMaster fleetMaster) throws MalBusinessException;
}
