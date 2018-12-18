package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.mikealbert.data.vo.VendorLovVO;

public interface VendorSearchDAOCustom {

	public  List<VendorLovVO> getVendorList(Long cId, String vendorType, String vendorCodeOrName,Pageable pageable);
	public int getVendorListCount(Long cId, String vendorType, String vendorCodeOrName);//lov count query
	public List<VendorLovVO> getOrderingOrDeliveringVendors(String vendorCodeOrName, String vendorWorkshopType, Pageable pageable);
	public int getOrderingOrDeliveringVendorsCount(String vendorCodeOrName, String vendorWorkshopType);//lov count query
	
}
