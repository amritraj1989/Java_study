package com.mikealbert.vision.service;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.data.vo.VendorLovVO;
import com.mikealbert.exception.MalException;

@Transactional
public interface VendorService {
	
	public List<VendorLovVO> getVendors(Long cId, String vendorType, String vendorWorkshopType,Pageable page) throws MalException;
	public int  getVendorListCount(Long cId, String vendorType, String vendorWorkshopType) throws MalException;
	public List<VendorLovVO> getOrderingOrDeliveringVendors(String vendorNameOrCode, String vendorWorkshopType, Pageable page) throws MalException ;
	public int getOrderingOrDeliveringVendorsCount(String vendorNameOrCode, String vendorWorkshopType) throws MalException ;
	
	
}
