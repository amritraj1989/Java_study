package com.mikealbert.vision.view.bean;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.primefaces.context.RequestContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mikealbert.data.entity.ContractLine;
import com.mikealbert.data.entity.Driver;
import com.mikealbert.data.entity.DriverAddressHistory;
import com.mikealbert.data.entity.DriverAllocation;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.vo.AllocationVO;
import com.mikealbert.data.vo.DriverAddressVO;
import com.mikealbert.service.ContractService;
import com.mikealbert.service.DriverAllocationService;
import com.mikealbert.service.DriverService;
import com.mikealbert.service.FleetMasterService;
import com.mikealbert.service.LookupCacheService;
import com.mikealbert.service.OdometerService;
import com.mikealbert.vision.util.AddressStartDateComparator;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.view.ViewConstants;

@Component
@Scope("view")
public class DriverAllocationHistoryBean extends StatefulBaseBean {
	private static final long serialVersionUID = -8806821952041781658L;
	

	@Resource FleetMasterService fleetMasterService;
	@Resource ContractService contractService;
	@Resource DriverService driverService;
	@Resource LookupCacheService lookupCacheService;
	@Resource OdometerService odometerService;
	@Resource DriverAllocationService allocationService;
	
	private FleetMaster selectedUnit;
	private List<DriverAllocation> allocations;
	private List<AllocationVO> allocationList;
	private DriverAllocation latestAllocation;
	private Driver selectedDriver;
	private long targetFmsId;
	private boolean showEditButton;
	
	
	/**
	 * Initializes the bean
	 */
    @PostConstruct
    public void init(){
    	super.openPage();

        try {  
    		super.initializeDataTable(500, 850, new int[] { 5, 40, 15, 15, 10, 25 });
    		super.getDataTable().setMaximumHeight();
    		if(!MALUtilities.isEmpty(selectedUnit)){
    			this.setAllocations(allocationService.getUnitAllocations(selectedUnit));
    		}
        	if(!MALUtilities.isEmpty(this.getAllocations()) && this.getAllocations().size() == 0){
        		super.getDataTable().setHeight(0);
        	} else {
        		super.getDataTable().setMaximumHeight();
        	}
        	
		} catch(Exception ex) {
			logger.error(ex);
			super.addErrorMessage("generic.error.occured.while", ex.getMessage());
		}
    }
    
    /**
     * Handles page save button click event
     * @return The calling view
     */
    public String save(){
    	return super.cancelPage();      	
    }
    
    /**
     * Handles page cancel button click event
     * @return The calling view
     */
    public String cancel(){
    	return super.cancelPage();      	
    }
    	
	/**
	 * Navigation code
	 */
	protected void loadNewPage() {      
		Map<String, Object> map = super.thisPage.getInputValues();

		super.thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_DRIVER_ALLOCATION_HISTORY);
		super.thisPage.setPageUrl(ViewConstants.DRIVER_ALLOCATION_HISTORY);	

		if(map.containsKey("FMS_ID")){
				setTargetFmsId ((Long) map.get("FMS_ID"));
				loadUnitData(getTargetFmsId());
				loadData(getTargetFmsId());
		}	
	} 
	
	// new navigation code
	protected void restoreOldPage() {}

	public FleetMaster getSelectedUnit() {
		return selectedUnit;
	}


	public void setSelectedUnit(FleetMaster selectedUnit) {
		this.selectedUnit = selectedUnit;
	}


	public List<DriverAllocation> getAllocations() {
		return allocations;
	}


	public void setAllocations(List<DriverAllocation> allocations) {
		this.allocations = allocations;
	}
	
	private void loadUnitData(long fmsId) {
		setSelectedUnit(fleetMasterService.getFleetMasterByFmsId(fmsId));
	}

	private void loadData(long fmsId) {
		allocations = allocationService.getUnitAllocations(fmsId);
		AllocationVO allocationVO;
		ContractLine contractLine = null;	//bug16363
		for(DriverAllocation da : allocations){
			allocationVO = new AllocationVO();
			Date compareDate;
			if(da.getDeallocationDate() != null) {
				compareDate = da.getDeallocationDate();
			}
			else {
				compareDate = new Date();
			}
			contractLine = contractService.getPendingLiveContractLine(getSelectedUnit(), Calendar.getInstance().getTime());//Bug16363 get contract line for pending live unit to view allocation/deallocation details by unit 
			if(contractLine != null){
				allocationVO.setCustomer(contractLine.getContract().getExternalAccount());				
			}
			else{
			    allocationVO.setCustomer(contractService.getLastActiveContractLine(getSelectedUnit(), compareDate).getContract().getExternalAccount());
			} //Bug16363			    			
			allocationVO.setAllocation(da);
			allocationVO.getDriverAddressesVO().addAll(getAddresses(da.getDriver().getDrvId(), da.getAllocationDate(), da.getDeallocationDate()));
			getAllocationList().add(allocationVO);
		}
	}


	private List<DriverAddressVO> getAddresses(long drvId, Date allocationFromDate, Date allocationToDate) {
		List<DriverAddressHistory> addresses = 
				driverService.getDriverAddressesByType(drvId, DriverService.GARAGED_ADDRESS_TYPE);
		addresses.addAll(driverService.getDriverAddressesByType(drvId, DriverService.POST_ADDRESS_TYPE));

		// filter addresses by date range of allocation
		List<DriverAddressHistory> validAddresses = new ArrayList<DriverAddressHistory>();
		for(DriverAddressHistory dah : addresses) {
			if(goodAddress(dah.getStartDate(), dah.getEndDate(), allocationFromDate, allocationToDate)) {
				validAddresses.add(dah);
			}
		}
		
		Collections.sort(validAddresses, Collections.reverseOrder(new AddressStartDateComparator()));

		return driverService.getAddressVOList(validAddresses);		
	}

	private boolean goodAddress(Date addressFromDate, Date addressToDate, Date allocationFromDate, Date allocationToDate ) {
		allocationToDate = allocationToDate == null ? new Date() : allocationToDate;
		Date compareFromDate = addressFromDate == null ? MALUtilities.formatDate("12/31/1950"): addressFromDate;
		Date compareToDate = addressToDate == null ? new Date(): addressToDate; 
		if((MALUtilities.compateDates(compareFromDate, allocationToDate) > 0) ||   
				   (MALUtilities.compateDates(compareToDate, allocationFromDate) < 0)) {	
						return false;
				}
		return true;
	}
	
	
	public void processSaveDialog() {
		if(validDateForDeallocation()) {
			allocationService.saveAllocation(getLatestAllocation());
			refreshData();
		}
		else {
			RequestContext context = RequestContext.getCurrentInstance();
			context.addCallbackParam("failure", true);
		}
	}

	public void processDeleteDialog() {
		allocationService.deleteAllocation(getLatestAllocation());
		refreshData();
	}
	
	private void refreshData() {
		allocationList.clear();
		loadData(getTargetFmsId());
	}

	private boolean validDateForDeallocation() {
		if(!MALUtilities.isEmpty(getLatestAllocation().getDeallocationDate())) {
			if(getLatestAllocation().getDeallocationDate().before(getLatestAllocation().getAllocationDate())) {
				addErrorMessageSummary("deallocation.date.prior");
		        return false;
			}
		}
		return true;
	}
	
	public void editDriver(){
	  	if(selectedDriver != null && selectedDriver.getDrvId() != null){
	  		Map<String, Object> nextPageValues = new HashMap<String, Object>();
	  		nextPageValues.put(ViewConstants.VIEW_PARAM_DRIVER_ID, String.valueOf(selectedDriver.getDrvId()));
	  		saveNextPageInitStateValues(nextPageValues);
			forwardToURL("driverAddEdit");
		}
	} 
	
	public List<AllocationVO> getAllocationList() {
		if (allocationList == null) {
			allocationList = new ArrayList<AllocationVO>();
		}
		return allocationList;
	}

	public void setAllocationList(List<AllocationVO> allocationList) {
		this.allocationList = allocationList;
	}

	public DriverAllocation getLatestAllocation() {
		return allocations.isEmpty() ? null:allocations.get(0);
	}
	
	public void setLatestAllocation(DriverAllocation latestAllocation) {
		this.latestAllocation = latestAllocation;
	}
	
	public Driver getSelectedDriver() {
		return selectedDriver;
	}
	
	public void setSelectedDriver(Driver selectedDriver) {
		this.selectedDriver = selectedDriver;
	}

	public long getTargetFmsId() {
		return targetFmsId;
	}

	public void setTargetFmsId(long targetFmsId) {
		this.targetFmsId = targetFmsId;
	}

	public boolean isShowEditButton() {
		return !getAllocationList().isEmpty();
	}

	public void setShowEditButton(boolean showEditButton) {
		this.showEditButton = showEditButton;
	}

	
	
}
