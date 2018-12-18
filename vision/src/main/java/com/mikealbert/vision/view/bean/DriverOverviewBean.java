package com.mikealbert.vision.view.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


import com.mikealbert.data.entity.Driver;
import com.mikealbert.data.entity.DriverAddressHistory;
import com.mikealbert.data.entity.DriverAllocation;
import com.mikealbert.data.entity.DriverCostCenter;
import com.mikealbert.data.vo.DriverAddressVO;
import com.mikealbert.data.vo.DriverCostCenterVO;
import com.mikealbert.service.CostCenterService;
import com.mikealbert.service.DriverService;
import com.mikealbert.vision.util.AddressStartDateComparator;
import com.mikealbert.vision.util.AllocationStartDateComparator;
import com.mikealbert.vision.util.CostCenterAllocatedDateComparator;
import com.mikealbert.vision.view.ViewConstants;

@Component
@Scope("view")
public class DriverOverviewBean extends StatefulBaseBean {
	private static final long serialVersionUID = -8806821952043784558L;
	
	@Resource DriverService driverService;
	@Resource CostCenterService costCenterService;
	
	private Driver driver;
	private long drvId;

	private List<DriverCostCenter> driverCostCenters;
	private List<DriverCostCenterVO> costCenters = new ArrayList<DriverCostCenterVO>();
	private List<DriverAllocation> allocations;
	private List<DriverAddressHistory> driverAddresses;
	private List<DriverAddressVO> garagedAddresses = new ArrayList<DriverAddressVO>();
	private List<DriverAddressVO> postAddresses = new ArrayList<DriverAddressVO>();
	
	/**
	 * Initializes the bean
	 */
    @PostConstruct
    public void init(){
    	super.openPage();

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

		super.thisPage.setPageDisplayName("Driver Overview");
		super.thisPage.setPageUrl("driverOverview");	

		if(map.containsKey(ViewConstants.VIEW_PARAM_DRIVER_ID)){
			drvId = ((Long) map.get(ViewConstants.VIEW_PARAM_DRIVER_ID));
			driver = driverService.getDriver(drvId);
			loadData();
		}	
	} 
	
	// new navigation code
	protected void restoreOldPage() {}

	

	private void loadData() {
		driverCostCenters = driver.getDriverCostCenterList();
		Comparator<DriverCostCenter> costCenterAllocatedDateComparator = new CostCenterAllocatedDateComparator();
		Collections.sort(driverCostCenters, Collections.reverseOrder(costCenterAllocatedDateComparator));
		loadCostCenters();
		
		allocations = driver.getDriverAllocationList();
		Collections.sort(allocations, Collections.reverseOrder(new AllocationStartDateComparator()));

		driverAddresses = driverService.getDriverAddressesByType(drvId, DriverService.GARAGED_ADDRESS_TYPE);
		Collections.sort(driverAddresses, Collections.reverseOrder(new AddressStartDateComparator()));
		garagedAddresses = driverService.getAddressVOList(driverAddresses);

		driverAddresses = driverService.getDriverAddressesByType(drvId, DriverService.POST_ADDRESS_TYPE);
		Collections.sort(driverAddresses, Collections.reverseOrder(new AddressStartDateComparator()));
		postAddresses = driverService.getAddressVOList(driverAddresses);

	}
	
	/**
	 * Loads the existing cost center list into a VO that will also contain the cost center description for each entry
	 */
	private void loadCostCenters() {
		for(DriverCostCenter dcc : driverCostCenters) {
			String desc = costCenterService.getCostCenterDescription(dcc);
			DriverCostCenterVO dccVO = new DriverCostCenterVO();
			dccVO.setDriverCostCenter(dcc);
			dccVO.setCostCenterDescription(desc);
			costCenters.add(dccVO);
		}
	}


	
	public Driver getDriver() {
		return driver;
	}

	public void setDriver(Driver driver) {
		this.driver = driver;
	}

	public long getDrvId() {
		return drvId;
	}

	public void setDrvId(long drvId) {
		this.drvId = drvId;
	}

	public List<DriverAllocation> getAllocations() {
		return allocations;
	}

	public void setAllocations(List<DriverAllocation> allocations) {
		this.allocations = allocations;
	}

	public List<DriverCostCenterVO> getCostCenters() {
		return costCenters;
	}

	public void setCostCenters(List<DriverCostCenterVO> costCenters) {
		this.costCenters = costCenters;
	}

	public List<DriverCostCenter> getDriverCostCenters() {
		return driverCostCenters;
	}

	public void setDriverCostCenters(List<DriverCostCenter> driverCostCenters) {
		this.driverCostCenters = driverCostCenters;
	}

	public List<DriverAddressHistory> getDriverAddresses() {
		return driverAddresses;
	}

	public void setDriverAddresses(List<DriverAddressHistory> driverAddresses) {
		this.driverAddresses = driverAddresses;
	}


	public List<DriverAddressVO> getGaragedAddresses() {
		return garagedAddresses;
	}


	public void setGaragedAddresses(List<DriverAddressVO> garagedAddresses) {
		this.garagedAddresses = garagedAddresses;
	}


	public List<DriverAddressVO> getPostAddresses() {
		return postAddresses;
	}


	public void setPostAddresses(List<DriverAddressVO> postAddresses) {
		this.postAddresses = postAddresses;
	}

	
}
