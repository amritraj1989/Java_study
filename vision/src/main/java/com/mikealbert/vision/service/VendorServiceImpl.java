package com.mikealbert.vision.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.data.dao.ExternalAccountDAO;
import com.mikealbert.data.dao.VendorSearchDAO;
import com.mikealbert.data.vo.VendorLovVO;
import com.mikealbert.exception.MalException;
import com.mikealbert.util.MALUtilities;

@Service("vendorService")
@Transactional(readOnly = true)
public class VendorServiceImpl implements VendorService {
	
	@Resource private VendorSearchDAO vendorSearchDAO;
	@Resource private ExternalAccountDAO externalAccountDAO;

	public List<VendorLovVO> getVendors(Long cId, String  vendorType, String vendorCodeOrName,Pageable page) throws MalException {
		try {
		
			if (!MALUtilities.isEmpty(vendorCodeOrName)) {
				vendorCodeOrName = "%"+vendorCodeOrName+"%"; 				
			} else {
				vendorCodeOrName = "%";				
			}

			return vendorSearchDAO.getVendorList(cId, vendorType, vendorCodeOrName,page);
			
		} catch (Exception ex) {
			if (ex instanceof MalException) {
				throw (MalException) ex;
			} else {
				throw new MalException("generic.error.occured.while", new String[] { "getting vendors" }, ex);
			}
		}
		
	}
	
	public int getVendorListCount(Long cId, String vendorType, String vendorCodeOrName) throws MalException {
		
		if (!MALUtilities.isEmpty(vendorCodeOrName)) {
			vendorCodeOrName = "%"+vendorCodeOrName+"%"; 				
		} else {
			vendorCodeOrName = "%";				
		}

		return vendorSearchDAO.getVendorListCount(cId, vendorType, vendorCodeOrName);
	}
	
	public List<VendorLovVO> getOrderingOrDeliveringVendors(String vendorNameOrCode , String vendorWorkshopType, Pageable page) throws MalException {
		try {		
				
			if (!MALUtilities.isEmpty(vendorNameOrCode)) {
				vendorNameOrCode = "%"+vendorNameOrCode+"%"; 				
			} else {
				vendorNameOrCode = "%";				
			}		
					
			return vendorSearchDAO.getOrderingOrDeliveringVendors( vendorNameOrCode, vendorWorkshopType,  page);
			
		} catch (Exception ex) {
			if (ex instanceof MalException) {
				throw (MalException) ex;
			} else {
				throw new MalException("generic.error.occured.while", new String[] { "getting vendors" }, ex);
			}
		}
		
	}
	
	public int  getOrderingOrDeliveringVendorsCount(String vendorNameOrCode , String vendorWorkshopType) throws MalException{
		try {
			
			if (!MALUtilities.isEmpty(vendorNameOrCode)) {
				vendorNameOrCode = "%"+vendorNameOrCode+"%"; 				
			} else {
				vendorNameOrCode = "%";				
			}	
					
			return vendorSearchDAO.getOrderingOrDeliveringVendorsCount(vendorNameOrCode, vendorWorkshopType);
			
		} catch (Exception ex) {
			if (ex instanceof MalException) {
				throw (MalException) ex;
			} else {
				throw new MalException("generic.error.occured.while", new String[] { "getting vendors" }, ex);
			}
		}
	}
}

