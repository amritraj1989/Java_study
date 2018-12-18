package com.mikealbert.service;

import java.util.List;
import java.util.Map;

import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.FuelGroupCode;
import com.mikealbert.data.entity.FuelGrouping;
import com.mikealbert.data.entity.FuelTypeValues;


public interface FuelService {

	public FuelGroupCode getFuelGroupCode(String groupKey, FuelTypeValues fuelType);
	public FuelGroupCode getFuelGroupCode(String groupKey, FleetMaster fleetMaster);
	public Map<String, FuelGroupCode> getAllFuelGroupings(String groupKey);
	public List<String> getDistinctFuelGroupKeys();
	public List<FuelTypeValues> getAllFuelTypeValues();
	public FuelGrouping getFuelGrouping(String groupKey, FuelTypeValues fuelType);
	public List<FuelGroupCode> getAllFuelGroupCodes();
	public void saveFuelGrouping(FuelGrouping fuelGrouping) throws Exception;
	public void saveFuelGroupCode(FuelGroupCode fuelGroupCode) throws Exception;
	public FuelGroupCode getFuelGroupCodeByCode(String fuelGroupCode);
	public FuelGroupCode getFuelGroupCodeByDescription(String fuelGroupDescription);
}
