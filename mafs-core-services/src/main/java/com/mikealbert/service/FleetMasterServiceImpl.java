package com.mikealbert.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.hibernate.Hibernate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.data.DataConstants;
import com.mikealbert.data.dao.DistDAO;
import com.mikealbert.data.dao.FleetMasterDAO;
import com.mikealbert.data.dao.QuotationModelDAO;
import com.mikealbert.data.entity.ContractLine;
import com.mikealbert.data.entity.Dist;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.data.enumeration.VehicleStatus;
import com.mikealbert.data.vo.AccessoryVO;
import com.mikealbert.data.vo.ProcessQueueResultVO;
import com.mikealbert.data.vo.StockUnitsLovVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;
import com.mikealbert.util.MALUtilities;

/**
 * Implementation of {@link com.mikealbert.vision.service.FleetMasterService}
 */
@Service("fleetMasterService")
@Transactional
public class FleetMasterServiceImpl implements FleetMasterService {
	@Resource FleetMasterDAO fleetMasterDAO;
	@Resource ContractService contractService;
	@Resource DriverService   driverService;
	@Resource DistDAO distDAO;
	@Resource WillowConfigService willowConfigService;
	@Resource QuotationService	quotationService;
	@Resource LookupCacheService lookupCacheService;
	
	private static final Map<String, String> SORT_BY_MAPPING = new HashMap<String, String>();
	static{
		SORT_BY_MAPPING.put(SORT_BY_UNIT_NO, DataConstants.STOCK_UNIT_LOV_SORT_UNIT_NO);
		SORT_BY_MAPPING.put(SORT_BY_VEH_DESC, DataConstants.STOCK_UNIT_LOV_SORT_VEH_DESC);
	}
	
	/**
	 * Retrieves the Fleet Master(s) based on the the parameters; Only those matching Fleet Masters that have a unit that is currently
	 * assigned to a driver will be returned; Used by Driver Allocation screen when retrieving the current allocated unit
	 * @param unitNo Unit Number
	 * @param drvId Primary Key of the driver
	 * @return List of Fleet Masters
	 */
	public List<FleetMaster> getFleetMasterFilterCurrentAllocaton(String unitNo, Long drvId){
		List<FleetMaster> fleetMasters = new ArrayList<FleetMaster>();
		FleetMaster fleetMaster = null;
		ContractLine contractLine = null;	//bug16363
			
		try{
			if((unitNo != null) && (!unitNo.isEmpty())){		
				fleetMaster = fleetMasterDAO.findByUnitNoAndFilterCurrentDriverAllocation(unitNo);				
				if(fleetMaster != null) {				
					fleetMasters = new ArrayList<FleetMaster>();
					fleetMasters.add(fleetMaster);
				}
			} else if (drvId != null) {			
				fleetMasters = fleetMasterDAO.findByDrvIdAndFilterCurrentDriverAllocation(drvId);					
			} else {				
				fleetMasters.clear();
			}
	
			for(FleetMaster fm : fleetMasters){
				contractLine = contractService.getPendingLiveContractLine(fm, Calendar.getInstance().getTime());//Bug16363 get contract line for pending live unit to allow allocate /deallocate driver 
				if(contractLine != null){
				    //fm.setContractLine(contractService.getPendingLiveContractLine(fm, Calendar.getInstance().getTime())); 
				    fm.setContractLine(contractLine);
				}
				else{				
				    fm.setContractLine(contractService.getLastActiveContractLine(fm, Calendar.getInstance().getTime()));
				}
				Hibernate.initialize(fm.getContractLine().getQuotationModel());
				fm.getLatestOdometerReading();
			}
				
			return fleetMasters;
		} catch(Exception ex) {	
			throw new MalException("generic.error.occured.while", new String[] { "getting the Fleet Master by Unit NO: " + unitNo }, ex);			
		}			
	}
		
	
	
	/**
	 * This procedure will setup the VIN last six characters with 11 wildcard characters(_) <br> 
	 * A wildcard(%) character will be added to the end of the string in case the full six VIN characters were not included <br>
	 * ex: Last Six with wildcards = '___________012345%' <br> 
	 * Once the VIN has wildcard characters setup, a list of matching vins are retrieved<br>
	 * Used by Vin LOV
	 * @param vin Last six or less of vehicle VIN
	 * @param execCountQuery Parameter does nothing
	 * @param page Used for paging
	 * @return List of fleet master record that match the provided vin
	 */
	public List<FleetMaster> findFleetMasterByVinLastSix(String vin, boolean execCountQuery, PageRequest page)			
					throws MalException {
		List<FleetMaster> fleetMaster = null;
				try {
					// Add wildcard to return all results if no value was entered
					if (vin.isEmpty()) {
						vin = "%";
					} 
					else{
						if(!vin.endsWith("%") && !vin.matches("\\%") && vin.length() < 6){
							vin = vin + "%";
						}

						//Append 11 wildcard characters before the last 6 digit of the vin.
						if(vin.length() <= 6){
							vin = "___________" + vin;
						}
					}
					
					fleetMaster = fleetMasterDAO.findFleetMasterByVIN(vin, page).getContent();
					
					for(FleetMaster fm : fleetMaster){
						fm.setContractLine(contractService.getLastActiveContractLine(fm, Calendar.getInstance().getTime()));
					}
					
					return fleetMaster;
				} catch (Exception ex) {
					if (ex instanceof MalException) {
						throw (MalException) ex;
					}
					throw new MalException("generic.error.occured.while", new String[] { "retrieving Unit Details" }, ex);
				}
		
	}
	
	/**
	 * This procedure will setup the VIN last six characters with 11 wildcard characters(_) <br> 
	 * A wildcard(%) character will be added to the end of the string in case the full six VIN characters were not included <br>
	 * ex: Last Six with wildcards = '___________012345%' <br> 
	 * Once the VIN has wildcard characters setup, a count of the number of returned records is returned<br>
	 * Used by Vin LOV
	 * @param vin Last six or less of vehicle VIN
	 * @return Count of retrieved records with VIN
	 */
	public long getFleetMasterCountByVINLastSix(String vin) {
		try {
			// Add wildcard to return all results if no value was entered
			if (vin.isEmpty()) {
				vin = "%";
			} 
			else{
				if(!vin.endsWith("%") && !vin.matches("\\%") && vin.length() < 6){
					vin = vin + "%";
				}
				
				//Append 11 wildcard characters before the last 6 digit of the vin.
				if(vin.length() <= 6){
					vin = "___________" + vin;
				}
			}
			return fleetMasterDAO.countFleetMasterByVIN(vin);
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while", 
					new String[] { "finding Accounts by Account Code and Account Status" }, ex);			
		}
	}

	/**
	 * Retrieves the Fleet Master record for the provided fms Id primary key; Used by Driver Allocation History and 
	 * Driver Search to retrieve unit number
	 * @param fmsId Primary Key for Fleet Master
	 * @return Fleet master record for provided primary key
	 */
	public FleetMaster getFleetMasterByFmsId(long fmsId) {
		return fleetMasterDAO.findById(fmsId).orElse(null);
	}

	/**
	 * Retrieves the Fleet Master record for the provided unit number; Used as helper method for notifying purchasing
	 * when a unit is 'on order' and the driver's address changes
	 * @param unitNo Primary Key for Fleet Master
	 * @return Fleet master record for provided unit number
	 */
	public FleetMaster findByUnitNo(String unitNo) {
		return fleetMasterDAO.findByUnitNo(unitNo);
	}
	
	/**
	 * Retrieves a list of Fleet Masters that have the same VIN.
	 * @param VIN
	 * @return List of Fleet Master entities sorted in DESC on fmsId
	 */
	public List<FleetMaster> findRelatedFleetMasters(String vin){
		return fleetMasterDAO.findByVIN(vin);
	}



	@Override
	public FleetMaster getFleetMasterByDocId(Long docId) {
		FleetMaster fleetMaster = null;
		
		List<Dist> distList = distDAO.findDistByDocId(docId);
		for(Dist dist : distList) {
			if(dist.getCdbCode1() != null) {
				fleetMaster = getFleetMasterByFmsId(Long.parseLong(dist.getCdbCode1()));
				break;
			}
		}
		
		return fleetMaster;
	}
	
	/**
	 * Determines if this fleet masters falls under high mileage category
	 * @param fmsId
	 * @return true if latest odo reading reached high mileage threshold else false
	 */
	public boolean	getHighMileage(Long fmsId){
		try{
			String	willowConfig = willowConfigService.getConfigValue("HIGH_MILEAGE_THRESHOLD");
			Long latestOdoMeterReading = getScheduleLatestOdoReading(fmsId);
			if(Long.parseLong(willowConfig) <= latestOdoMeterReading){
				return true;
			}else{
				return false;
			}
		}catch(Exception ex){
			throw new MalException("generic.error.occured.while", 
					new String[] { "high mileage" }, ex);			
		}
		
	}

	public Long getScheduleLatestOdoReading(Long fmsId)
			throws MalBusinessException {
		try {
			Long latestOdoMeterReading = fleetMasterDAO.getHighMileage(fmsId);
			if (latestOdoMeterReading == null) {

				QuotationModel quotationModel = quotationService
						.getQuotationModel(quotationService
								.getQmdIdFromUnitNo(fleetMasterDAO.findById(
										fmsId).orElse(null).getUnitNo()));
				latestOdoMeterReading = quotationModel.getQuoteStartOdo();
			}
			if (latestOdoMeterReading == null) {
				latestOdoMeterReading = 0L;
			}
			return latestOdoMeterReading;
		} catch (Exception ex) {
			if (ex instanceof MalBusinessException) {
				throw (MalBusinessException) ex;
			}
			throw new MalException("generic.error.occured.while",
					new String[] { "Schedule Latest OdoReading" }, ex);
		}

	}
	
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void saveUpdateFleetMaster(FleetMaster fleetMaster) {
		fleetMasterDAO.save(fleetMaster);
	}
	
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void saveUpdateFleetMaster(List<FleetMaster> fleetMasterList) {
		fleetMasterDAO.saveAll(fleetMasterList);
	}

	public List<Long> findOnContractOrOnOrderFmsIdsByVinOrVehicleCostCenter(String vin, String vehicleCostCentre) {
		try {
			return fleetMasterDAO.findOnContractOrOnOrderFmsIdsByVinOrVehicleCostCenter(vin, vehicleCostCentre);
		} catch (Exception ex) {
			throw new MalException("generic.error", new String[] { ex.getMessage() }, ex);
		}
	}

	public boolean isTerminatedUnit(Long fmsId) {
		String status = fleetMasterDAO.getFleetStatus(fmsId);
		if (VehicleStatus.STATUS_TERMINATED.getCode().equals(status)) {
			return true;

		} else {
			return false;
		}
	}

	public ExternalAccount getExternalAccountForUnit(FleetMaster fleetMaster) {
		ContractLine contractLine = null;
		Calendar calendar = Calendar.getInstance();    
		calendar.setTime(new Date());
		calendar.add(Calendar.DATE, 365);
		contractLine = contractService.getLastActiveContractLine(fleetMaster, calendar.getTime());
		
		if(contractLine != null) {
			return contractLine.getContract().getExternalAccount();
		} else {
			return null;
		}
	}
	
	@Override
	public List<StockUnitsLovVO> findStockUnitsList(String unitNo, String vehicleDesc, Pageable pageable, Sort sort) {
		return fleetMasterDAO.findStockUnits(unitNo, vehicleDesc, pageable, sort);

	}

	@Override
	public int findStockUnitsListCount(String unitNo, String vehicleDesc) {
		return fleetMasterDAO.findStockUnitCount(unitNo, vehicleDesc);
	}
	
	@Override
	public List<String> getStandardEquipmentForFmsId(long fmsId) {
		return fleetMasterDAO.getStandardEquipmentForFmsId(fmsId);
	}
	
	@Override
	public List<String> getModelEquipmentForFmsId(long fmsId) {
		return fleetMasterDAO.getModelEquipmentForFmsId(fmsId);
	}

	@Override
	public List<String> getDealerEquipmentForFmsId(long fmsId) {
		return fleetMasterDAO.getDealerEquipmentForFmsId(fmsId);
	}



	@Override
	public String resolveStockUnitLovSortByName(String columnName) {
		return SORT_BY_MAPPING.get(columnName);		
	}

	@Transactional
	public FleetMaster getFleetMasterByUnitNoWithDelayReason(String unitNo) {
		FleetMaster fm = fleetMasterDAO.findByUnitNo(unitNo); 
		Hibernate.initialize(fm.getDelayReason());
		return fm;
	}



	@Override
	@Transactional
	public FleetMaster getFleetMasterByFmsIdWithOdoList(long fmsId) {
		FleetMaster fm =  fleetMasterDAO.findById(fmsId).orElse(null);
		Hibernate.initialize(fm.getVehicleOdometerReadings());
		return fm;
	}
	
	@Override
	@Transactional(readOnly=true)	
	public List<AccessoryVO> getStandardAccessories(long fmsId) {
		List<AccessoryVO> accessories;
		accessories = fleetMasterDAO.getStandardAccessoriesByFmsId(fmsId);
		return accessories;
	}

	@Override
	@Transactional(readOnly=true)
	public List<AccessoryVO> getOptionalAccessories(long fmsId) {
		List<AccessoryVO> accessories;
		accessories = fleetMasterDAO.getOptionalAccessoriesByFmsId(fmsId);
		return accessories;		
	}
	
	@Override
	@Transactional(readOnly=true)
	public List<AccessoryVO> getDealerAccessories(long fmsId){
		List<AccessoryVO> accessories;
		accessories = fleetMasterDAO.getDealerAccessoriesByFmsId(fmsId);
		return accessories;		
	}


	@Override
	public BigDecimal getStockUnitCost(String unitNo, Long cId) throws MalBusinessException {

		return fleetMasterDAO.getStockUnitCost(unitNo, cId);
	}



	@Override
	public BigDecimal getLastOdometerReadingByFmsId(Long fmsId) throws MalBusinessException {
		
		return fleetMasterDAO.getLastOdoMeterReadingByFmsId(fmsId);
	}

	//TODO Need to refactor existing code that retrieves stock unit LOV to pull out of cache.
	@Override
	public List<StockUnitsLovVO> stockUnitListFromCache() {
		return lookupCacheService.getStockUnits();
	}


	/**
	 * Re-written stored procedure FL_CONTRACT.check_vehicle_status
	 * @param FMS_ID
	 * @return Business Error message. Returns NULL if it passes validations.
	 */
	public String checkVehicleStatus(long fmsId) {
		FleetMaster fm = fleetMasterDAO.findById(fmsId).orElse(null);
		if (!MALUtilities.isEmpty(fm.getSubStatus()) && fm.getSubStatus().equalsIgnoreCase("W")) {
			return "This vehicle is a writeoff and cannot therefore be revised";
		}
		
		String fleetStatus = fleetMasterDAO.getFleetStatus(fmsId);		
		if (!MALUtilities.isEmpty(fleetStatus) && fleetStatus.equals(VehicleStatus.STATUS_PENDING_LIVE_UNCHECKED.getCode())) {
			return "This vehicle has a status of " + VehicleStatus.STATUS_PENDING_LIVE_UNCHECKED.getDescription() +" and therefore it is not possible to proceed.";
		}
		
		return null;
	}



	@Override
	public boolean isActivePOUnit(String unitNo, Long qprId, Long cId) throws MalBusinessException {
		return fleetMasterDAO.isActivePOUnit(unitNo, qprId, cId);
	}

	@Override
	public boolean isActivePOUnit(Long fmsId) throws MalBusinessException {
		return fleetMasterDAO.isActivePOUnit(fmsId);
	}

	@Override
	public BigDecimal getUnpaidVehicleCapitalCostFromDocsByFmsId(Long fmsId) throws MalBusinessException {
		return fleetMasterDAO.getUnpaidVehicleCapitalCostFromDocsByFmsId(fmsId);
	}

	@Override
	public List<Long> findOnContractOrInServiceByVin(String vin) {
		
		return fleetMasterDAO.findOnContractOrInServiceByVin(vin);
	}

	@Override
	public BigDecimal getInventoryCostByFmsId(FleetMaster fleetMaster) throws MalBusinessException {
		BigDecimal capitalCost;
		
		boolean isActivePO = isActivePOUnit(fleetMaster.getFmsId());
		
		if(isActivePO){
			capitalCost = getUnpaidVehicleCapitalCostFromDocsByFmsId(fleetMaster.getFmsId());
			if(fleetMaster.getAdjustment() != null){
				return capitalCost.add(fleetMaster.getAdjustment()); 
			}
		}else{
			capitalCost = getStockUnitCost(fleetMaster.getUnitNo(), CorporateEntity.MAL.getCorpId());
		}
		return capitalCost;
	}

}
