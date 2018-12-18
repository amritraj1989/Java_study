package com.mikealbert.data.dao;

import java.util.List;

import com.mikealbert.exception.MalException;

public interface SupplierDAOCustom {
	
	public List<Object[]> getSupplierAddressByType(Long cId, String accountType,
			String accountCode, String addressType) throws MalException;
	
	public List<Object> getSupplierByMakeCodeOrName(String makeCode, String manufacturer) throws MalException; 
}
