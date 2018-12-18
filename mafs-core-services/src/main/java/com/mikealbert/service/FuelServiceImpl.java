package com.mikealbert.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.dao.FuelGroupCodeDAO;
import com.mikealbert.data.dao.FuelGroupingDAO;
import com.mikealbert.data.dao.FuelTypeValuesDAO;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.FuelGroupCode;
import com.mikealbert.data.entity.FuelGrouping;
import com.mikealbert.data.entity.FuelGroupingPK;
import com.mikealbert.data.entity.FuelTypeValues;
import com.mikealbert.exception.MalException;

@Service("fuelService")
public class FuelServiceImpl implements FuelService {

	private MalLogger logger = MalLoggerFactory.getLogger(this.getClass());
	
	@Resource
	FuelGroupingDAO fuelGroupingDAO;
	
	@Resource
	FuelTypeValuesDAO fuelTypeValuesDAO;
	@Resource
	FuelGroupCodeDAO fuelGroupCodeDAO;
	

	/**
	 * Gets the the fuel group for a specific fuel type within a given group code.
	 * @param groupKey - group key that represents the specific use of this grouping 
	 * @param fuelType - the fuel type 
	 * @return the specific fuel group code that this fuel type falls into for the given group key
	 *       or returns null if the method cannot determine a group (this is a situation that can occur naturally  
	 *       and not an exception in the business process)
	 */
	public FuelGroupCode getFuelGroupCode(String groupKey, FuelTypeValues fuelType) {
		FuelGroupingPK fuelGroupPK = new FuelGroupingPK();
		fuelGroupPK.setFuelType(fuelType);
		fuelGroupPK.setGroupKey(groupKey);
		FuelGrouping fuelGrouping = fuelGroupingDAO.findById(fuelGroupPK).orElse(null);
		if(fuelGrouping != null) {
			return fuelGrouping.getFuelGroupCode();
		} else {
			return null;
		}
	}

	
	/**
	 * Gets the the fuel group for a specific unit within a given group code.
	 * @param groupKey - group key that represents the specific use of this grouping 
	 * @param fleetMaster - fleet Master of the specific unit
	 * @return the specific fuel group code that this unit's fuel type falls into for the given group key
	 *       or returns null if the method cannot determine a group (this is a situation that can occur naturally  
	 *       and not an exception in the business process)
	 */
	public FuelGroupCode getFuelGroupCode(String groupKey, FleetMaster fleetMaster) {
		if(fleetMaster.getVehicleVinDetails() != null) {
			if(fleetMaster.getVehicleVinDetails().getFuelType() != null) {
				FuelTypeValues fuelType = fuelTypeValuesDAO.findByFuelType(fleetMaster.getVehicleVinDetails().getFuelType());
				return getFuelGroupCode(groupKey, fuelType);
			}
		}
		return null;
	}

	//TODO: Anand- Please Add a JavaDoc comment - per JAD 06/29/2015
	public Map<String, FuelGroupCode> getAllFuelGroupings(String groupKey) {
		Map<String, FuelGroupCode> groupings = new HashMap<String, FuelGroupCode>();
		List<FuelTypeValues> fuelTypes = fuelTypeValuesDAO.findAll();
		for (FuelTypeValues fuelType : fuelTypes) {
			groupings.put(groupKey+":"+fuelType.getFuelType(), getFuelGroupCode(groupKey, fuelType));
		}
		return groupings;
	}
	
	@Override
	public List<String> getDistinctFuelGroupKeys() {
		return fuelGroupingDAO.findDistinctFuelGroupingKeys();
	}

	@Override
	public List<FuelTypeValues> getAllFuelTypeValues() {
		Sort sort = new Sort(Sort.Direction.ASC, "fuelType");
		return fuelTypeValuesDAO.findAll(sort);
	}
	
	@Override
	public FuelGrouping getFuelGrouping(String groupKey, FuelTypeValues fuelType) {
		FuelGroupingPK fuelGroupingPK = new FuelGroupingPK();
		fuelGroupingPK.setGroupKey(groupKey);
		fuelGroupingPK.setFuelType(fuelType);
		return fuelGroupingDAO.findById(fuelGroupingPK).orElse(null);
	}

	public List<FuelGroupCode> getAllFuelGroupCodes() {
		return fuelGroupCodeDAO.getAllFuelGroupCodes();
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void saveFuelGrouping(FuelGrouping fuelGrouping) throws Exception {
		try {
			fuelGroupingDAO.saveAndFlush(fuelGrouping);
		} catch (Exception ex) {
			logger.error(ex);
			throw new MalException("generic.error.occured.while", new String[] { "saving a fuel grouping" }, ex);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void saveFuelGroupCode(FuelGroupCode fuelGroupCode) throws Exception {
		try {
			fuelGroupCodeDAO.saveAndFlush(fuelGroupCode);
		} catch (Exception ex) {
			logger.error(ex);
			throw new MalException("generic.error.occured.while", new String[] { "saving a fuel group code" }, ex);
		}
	}

	@Override
	public FuelGroupCode getFuelGroupCodeByCode(String fuelGroupCode) {
		return fuelGroupCodeDAO.findById(fuelGroupCode).orElse(null);
	}

	@Override
	public FuelGroupCode getFuelGroupCodeByDescription(String fuelGroupDescription) {
		return fuelGroupCodeDAO.getFuelGroupCodeByDescription(fuelGroupDescription);
	}
	
}
