package com.mikealbert.data.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.mikealbert.data.entity.DriverAddress;
import com.mikealbert.data.entity.DriverAddressHistory;
import com.mikealbert.data.entity.DriverAllocation;
import com.mikealbert.data.entity.ExternalAccount;

public class AllocationVO implements Serializable {

	private static final long serialVersionUID = -7987938010119570579L;
	private ExternalAccount customer;
	private List<DriverAddressVO> driverAddresses = new ArrayList<DriverAddressVO>();
	private DriverAllocation allocation;
	
	public ExternalAccount getCustomer() {
		return customer;
	}
	public void setCustomer(ExternalAccount customer) {
		this.customer = customer;
	}
	public List<DriverAddressVO> getDriverAddressesVO() {
		return driverAddresses;
	}
	public void setDriverAddresses(List<DriverAddressVO> driverAddresses) {
		this.driverAddresses = driverAddresses;
	}
	public DriverAllocation getAllocation() {
		return allocation;
	}
	public void setAllocation(DriverAllocation allocation) {
		this.allocation = allocation;
	}
	
	
	
}
