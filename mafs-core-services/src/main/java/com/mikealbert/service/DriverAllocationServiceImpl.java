package com.mikealbert.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.data.dao.ContractLineDAO;
import com.mikealbert.data.dao.DriverAllocationDAO;
import com.mikealbert.data.dao.QuotationModelDAO;
import com.mikealbert.data.entity.ContractLine;
import com.mikealbert.data.entity.DriverAllocation;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;
import com.mikealbert.data.vo.DriverAllocationVO;

/**
 * Implementation of {@link com.mikealbert.vision.service.DriverAllocationService}
 */
@Service("driverAllocationService")
public class DriverAllocationServiceImpl implements DriverAllocationService {
	
	@Resource private ContractLineDAO contractLineDAO;
	@Resource private QuotationModelDAO quotationModelDAO;
	@Resource private OdometerService odometerService;	
	@Resource private DriverAllocationDAO driverAllocationDAO;
	@Resource private CustomerAccountService customerAccountService;
	@Resource private FleetMasterService fleetMasterService;

	/**
	 * Retrieve list of driver allocations for the provided fleet master unit.<br>
	 * Used By: <br>
	 * 1) Allocation History
	 * @param  fleetMaster Unit used to find driver allocations
	 * @return List of Driver Allocations
	 */
    @Transactional
	public List<DriverAllocation> getUnitAllocations(FleetMaster fleetMaster){
    	return driverAllocationDAO.findByFmsId(fleetMaster.getFmsId());
    }

    /**
	 * Retrieve list of driver allocations for the provided fleet master unit.<br>
	 * Used By: <br>
	 * 1)Allocation History
	 * @param  fmsId Unit used to find driver allocations
	 * @return List of Driver Allocations
	 */
    @Transactional
	public List<DriverAllocation> getUnitAllocations(long fmsId){
    	return driverAllocationDAO.findByFmsId(fmsId);
    }

    /**
	 * Saves the provided driver allocation; used when saving an changed allocation in Allocation History.
	 * @param  driverAllocation Driver Allocation to be saved
	 */
    @Transactional
    public void saveAllocation(DriverAllocation driverAllocation) {
    	driverAllocationDAO.saveAndFlush(driverAllocation);
    }

    /**
	 * Deletes the provided driver allocation; used when deleting an allocation in Allocation History.
	 * @param  driverAllocation
	 */
    @Transactional
    public void deleteAllocation(DriverAllocation driverAllocation) {
    	driverAllocationDAO.delete(driverAllocation);
    }

    
	/**
	 * Deallocates and Allocates drivers to a single unit; Validations will be performed, contract(s) will be updated to reflect
	 * the new driver, and the current driver will be deallocated and the new driver will be allocated to the unit; The old
	 * driver's Deallocation date will be derived from the new driver's allocation date; Used when saving a new allocation in
	 * Driver Allocation screen.
	 * @param currentDriverAllocation The current driver allocation for the unit
	 * @param newDriverAllocation The new driver allocation for the unit
	 * @throws MalBusinessException 
	 * @return List of driver allocations
	 */
	@Transactional
	public List<DriverAllocation> saveAndUpdateDriversAllocation(DriverAllocation currentDriverAllocation, DriverAllocation newDriverAllocation) throws MalBusinessException{
		List<QuotationModel> quotationModels;
		ContractLine contractLine = null;
		ContractLine futureContractLine = null;
		List<DriverAllocation> driverAllocations = new ArrayList<DriverAllocation>();
		
		try {								
			//TODO Followup on where the decision is on the fleetmastercostcenntre update			
							    
			//Validate deallocate/allocate
            this.validateDeallocationAllocation(currentDriverAllocation, newDriverAllocation);
            
			//Change driver on the contract(s)
	    	contractLine = currentDriverAllocation.getFleetMaster().getContractLine();		    
		    if(contractLine != null 
		    		&& (contractLine.getActualEndDate() == null 
		    		        || contractLine.getActualEndDate().compareTo(Calendar.getInstance().getTime()) >= 0)){
		    	contractLine.setDriver(newDriverAllocation.getDriver());
		    	contractLine = contractLineDAO.saveAndFlush(contractLine);	
		    	
		    	//Update future contract if one exist
		    	quotationModels = quotationModelDAO.findByClnId(contractLine.getClnId());
		    	for(QuotationModel qm : quotationModels){
		    		for(ContractLine cl : qm.getContractLineList()){
		    			if(cl.getStartDate() == null){
		    				futureContractLine = cl;
		    		    	futureContractLine.setDriver(newDriverAllocation.getDriver());		    				
		    				break;
		    			}
		    		}
		    	}		    	
		    } else {
		    	for(ContractLine cl : contractLineDAO.findByFmsId(currentDriverAllocation.getFleetMaster().getFmsId())){
		    		if(cl.getRevNo() == 1 && cl.getStartDate() != null && cl.getInServDate() != null 
		    				&& cl.getInServDate().compareTo(Calendar.getInstance().getTime()) <= 0){
		    			contractLine = cl;
		    		}
		    	}
		    }	    
		    
		    //Saving changes to the current and new driver allocations as well the vehicle odemeter reading, 
		    //and change that switches the drivers on the unit's contract.
			driverAllocations.add(driverAllocationDAO.saveAndFlush(currentDriverAllocation));				
		    driverAllocations.add(driverAllocationDAO.saveAndFlush(newDriverAllocation));
		    
		    odometerService.saveOdometerReading(currentDriverAllocation.getFleetMaster(), 
		    		"ESTODO", newDriverAllocation.getFromOdoReading(), newDriverAllocation.getOpCode());
		    
		    if(contractLine != null){
		    	futureContractLine = contractLineDAO.saveAndFlush(contractLine);
		    }
		    
		    if(futureContractLine != null){
		    	futureContractLine = contractLineDAO.saveAndFlush(futureContractLine);
		    }
		    
			return driverAllocations;
		} catch (MalBusinessException mbe) {
			throw mbe;
		} catch(Exception ex ) {
			throw new MalException("generic.error.occured.while", new String[] { "saving or updating a driver's allocation" }, ex);			
		}
	}

	/**
	 * Checks if unit is currently allocated to a driver; Used in Driver Search to determine whether allocation
	 * button should be enabled.
	 * @param  fmsId Unit to check allocation
	 * @return Returns true if the unit is currently allocated to a driver.
	 */
	public boolean unitHasCurrentAllocation(Long fmsId) {
		boolean hasAlloc = false;
		DriverAllocation allocation = driverAllocationDAO.findByFmsIdCurrentDriverAllocation(fmsId);
		if(allocation != null){
			hasAlloc = true;
		}

		return hasAlloc;
	}

	/**
	 * Checks if unit has ever been allocated to a driver; Used to determine whether Allocation History button
	 * should be enabled.
	 * @param  fmsId Unit to check allocations
	 * @return Returns true if the unit have ever been allocated to a driver.
	 */
	public boolean unitHasHistoricAllocations(Long fmsId) {
		boolean hasAlloc = false;
		List<DriverAllocation> allocations = driverAllocationDAO.findByFmsId(fmsId);
		if(allocations.size() > 0){
			hasAlloc = true;
		}
		
		return hasAlloc;
	}
	
	/**
	 * Various validations to be checked before saving or updating driver allocation: <br>
	 * Verify that the deallocation and allocation dates exist for the current and new driver <br>
	 * Verify that the allocation date of the new driver is not on or before the deallocation date of the current driver<br>
	 * Verify that the driver's client accounts are related<br>
	 * Verify that the Odometer Reading has been captured<br>
	 * Verify that there are no other drivers currently allocated to the unit and the Allocation date is not before any of the previous allocation dates<br>
	 * Used by Driver Allocation to check whether driver can be saved.
	 * @param currentDriverAllocation The current driver allocation
	 * @param newDriverAllocation The new driver allocation
	 * @throws MalBusinessException
	 */
	public void validateDeallocationAllocation(DriverAllocation currentDriverAllocation, DriverAllocation newDriverAllocation) throws MalBusinessException{	
		List<DriverAllocation> driverAllocations = new ArrayList<DriverAllocation>();
				
		//Verify that the deallocation and allocation dates exist for the current and new driver.
		if(currentDriverAllocation.getDeallocationDate() == null ){
			throw new MalBusinessException("service.validation", new String[]{"Deallocation date is null"});
		}
		if(newDriverAllocation.getAllocationDate() == null){
			throw new MalBusinessException("service.validation", new String[]{"Allocation date is null"});
		}
		
		//Verify that the allocation date of the new driver is not on or before the deallocation date of the current driver
		if(!new SimpleDateFormat("dd/MM/yyyy").format(newDriverAllocation.getAllocationDate()).equals(new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime()))) {			
			throw new MalBusinessException("service.validation", new String[]{"New Driver allocation date is not today's date"});				
		}
		
		//Verify that the driver's client accounts are related
		if(!customerAccountService.isAccountsReleated(currentDriverAllocation.getDriver().getExternalAccount(), newDriverAllocation.getDriver().getExternalAccount())) {
			throw new MalBusinessException("service.validation", 
					new String[]{"New Driver belongs to an unrealted client account"});			
		}
		
		//Verify that the Odometer Reading has been captured
		if(newDriverAllocation.getFromOdoReading() == null) {
			throw new MalBusinessException("service.validation", new String[]{"An odometer reading is required"});				
		}
		
		//Verify that there are no other drivers currently allocated to the unit and the Allocation date is not before any of
		//of the previous allocation dates.
		driverAllocations = driverAllocationDAO.findByFmsId(currentDriverAllocation.getFleetMaster().getFmsId());
		for(DriverAllocation da : driverAllocations){				
			if(da.getDeallocationDate() == null && !da.getDalId().equals(currentDriverAllocation.getDalId())){
				throw new MalBusinessException("service.validation", new String[]{"The unit is currently allocated to a driver"});					
			}
			if(da.getDeallocationDate() != null && newDriverAllocation.getAllocationDate().compareTo(da.getDeallocationDate()) <= 0){
				throw new MalBusinessException("service.validation", new String[]{"The new allocation date is between a previous allocation"});					
			}
		}
		
		if(currentDriverAllocation.getDeallocationDate() != null && currentDriverAllocation.getDeallocationDate().compareTo(currentDriverAllocation.getAllocationDate()) <= 0){				
			throw new MalBusinessException("service.validation", new String[]{"The deallocation date of the current driver must be later than their Allocation date"});						
		}
				
	}
	
	/**
	 * Finds current driver allocations for a provided driver; Used by Driver Add Edit to see if driver can be inactivated.
	 * @param  driverId Driver to find current driver allocations
	 * @return List of current driver allocations.
	 */
	public List<DriverAllocation> getDriverAllocations(long driverId){
		return  driverAllocationDAO.findByDrvIdCurrentDriverAllocations(driverId);
	}
	
	/**
	 * Finds current driver allocations for a provided driver; Used by Driver Add Edit to see if driver can be inactivated.
	 * @param  driverId Driver to find current driver allocations
	 * @return List of current driver allocations.
	 */
	public long getDriverAllocationsCount(long driverId){
		return  driverAllocationDAO.findByDrvIdCurrentDriverAllocationsCount(driverId);
	}
	
	/**
	 * Gets currently allocation to a unit; Used in Fleet Maintenance Authorization Email to include driver info in the email
	 * @param  fmsId Unit to check allocation
	 * @return Returns the current allocation
	 */
	public DriverAllocation getCurrentAllocation(FleetMaster fms) {
		DriverAllocation alloc = null;
		DriverAllocation allocation = driverAllocationDAO.findByFmsIdCurrentDriverAllocation(fms.getFmsId());
		if(allocation != null){
			alloc = allocation;
		}

		return alloc;
	}
	
	/**
	 * Finds current driver allocations for a provided driver; Used by Driver Add Edit to see if driver can be inactivated.
	 * @param  driverId Driver to find current driver allocations
	 * @return List of current driver allocations.
	 */
	public List<DriverAllocation> getDriverAllocationsByDrvId(long driverId){
		return driverAllocationDAO.getCurrentDriverAllocationByDrvId(driverId);
	}

	public DriverAllocation getDriverAllocationByDate(String unitNo, Date date) {
		FleetMaster fleetMaster = fleetMasterService.findByUnitNo(unitNo);
		return getDriverAllocationByDate(fleetMaster, date);
	}

	
	public DriverAllocation getDriverAllocationByDate(FleetMaster fleetMaster, Date date) {
		DriverAllocation driverAllocation = null;
		if (fleetMaster != null) {	
			List<DriverAllocation> allocations = getUnitAllocations(fleetMaster);
			
			for (DriverAllocation da : allocations){
				if ((date.compareTo(da.getAllocationDate()) >= 0)
					&& (da.getDeallocationDate() == null ||(date.compareTo(da.getDeallocationDate()) <= 0))) {					
					driverAllocation = da;
					break;
				}
			}
		}		
		
		return driverAllocation;

	}

	@Override
	public List<DriverAllocationVO> getCurrentDriverAllocationVOs(Long drvId) {
		return driverAllocationDAO.getCurrentDriverAllocationVOs(drvId);
	}

	@Override
	public List<DriverAllocationVO> getPreviousDriverAllocationVOs(Long drvId) {
		return driverAllocationDAO.getPreviousDriverAllocationVOs(drvId);
	}
}
