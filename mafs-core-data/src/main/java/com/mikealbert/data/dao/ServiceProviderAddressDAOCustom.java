package com.mikealbert.data.dao;

import java.math.BigDecimal;
import java.util.List;

import com.mikealbert.data.vo.ServiceProviderAddressVO;

public interface ServiceProviderAddressDAOCustom  {	
	public List<ServiceProviderAddressVO> findServiceProviderAddressesByListOfIds(List<BigDecimal> ids);
}
