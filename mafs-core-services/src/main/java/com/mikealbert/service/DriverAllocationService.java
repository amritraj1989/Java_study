package com.mikealbert.service;

import java.util.Date;
import java.util.List;

import com.mikealbert.data.entity.DriverAllocation;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.data.vo.DriverAllocationVO;

/**
 * Public Interface implemented by {@link com.mikealbert.vision.service.DriverAllocationServiceImpl} for interacting with business service methods concerning {@link com.mikealbert.data.entity.DriverAllocation}(s).
 * 
 * @see com.mikealbert.data.entity.DriverAllocation
 * @see com.mikealbert.vision.service.DriverAllocationServiceImpl
 * */
public interface DriverAllocationService {		
	public List<DriverAllocation> getUnitAllocations(FleetMaster fleetMaster);
	public List<DriverAllocation> getUnitAllocations(long fmsId);
	public List<DriverAllocation> saveAndUpdateDriversAllocation(DriverAllocation currentDriverAllocation, DriverAllocation newDriverAllocation) throws MalBusinessException;	
	public void saveAllocation(DriverAllocation driverAllocation);
	public void deleteAllocation(DriverAllocation driverAllocation);
	public boolean unitHasCurrentAllocation(Long fmsId);
	public DriverAllocation getCurrentAllocation(FleetMaster fleetMaster);
	
	public boolean unitHasHistoricAllocations(Long fmsId);
	public void validateDeallocationAllocation(DriverAllocation currentDriverAllocation, DriverAllocation newDriverAllocation) throws MalBusinessException;
	public List<DriverAllocation> getDriverAllocationsByDrvId(long driverId);
	public List<DriverAllocation> getDriverAllocations(long driverId);
	public long getDriverAllocationsCount(long driverId);
	public DriverAllocation getDriverAllocationByDate(FleetMaster fleetMaster, Date date);
	public DriverAllocation getDriverAllocationByDate(String unitNo, Date date);
	
	public List<DriverAllocationVO> getCurrentDriverAllocationVOs(Long drvId);
	public List<DriverAllocationVO> getPreviousDriverAllocationVOs(Long drvId);
	
}
