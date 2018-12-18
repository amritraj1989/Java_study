package com.mikealbert.data.vo;

import java.math.BigDecimal;

import com.mikealbert.data.entity.ServiceProviderAddress;

public class ServiceProviderAddressVO {
	
	private BigDecimal serviceProviderId;
	private ServiceProviderAddress serviceProviderAddress;
	
	public BigDecimal getServiceProviderId() {
		return serviceProviderId;
	}
	public void setServiceProviderId(BigDecimal serviceProviderId) {
		this.serviceProviderId = serviceProviderId;
	}
	public ServiceProviderAddress getServiceProviderAddress() {
		return serviceProviderAddress;
	}
	public void setServiceProviderAddress(ServiceProviderAddress serviceProviderAddress) {
		this.serviceProviderAddress = serviceProviderAddress;
	}

}
