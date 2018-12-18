/**
 * SuppServiceTypeCodeServiceImpl.java
 * mafs-core-services
 * Oct 7, 2013
 * 2:44:07 PM
 */
package com.mikealbert.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.mikealbert.data.dao.SupplierServiceTypeCodeDAO;
import com.mikealbert.data.entity.SupplierServiceTypeCodes;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.util.MALUtilities;

/**
 * @author anand.mohan
 * 
 */
@Service
public class SuppServiceTypeCodeServiceImpl implements SuppServiceTypeCodeService{
	@Resource
	private SupplierServiceTypeCodeDAO supplierServiceTypeCodeDAO;

	public List<SupplierServiceTypeCodes> getSuppServiceTypeCodes(String srvTypeCode) throws MalBusinessException {
		try {
			if (MALUtilities.isEmpty(srvTypeCode)) {
				srvTypeCode =  "%";
			} else {
				srvTypeCode = srvTypeCode + "%";
			}
			return supplierServiceTypeCodeDAO.find(srvTypeCode);
		} catch (Exception ex) {
			throw new MalBusinessException("generic.error.occured.while", new String[] { "Service Type Code" });
		}
	}
}
