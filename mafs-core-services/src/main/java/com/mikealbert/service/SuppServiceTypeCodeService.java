
/**
 * SuppServiceTypeCodeService.java
 * mafs-core-services
 * Oct 7, 2013
 * 2:43:53 PM
 */
package com.mikealbert.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mikealbert.data.entity.SupplierServiceTypeCodes;
import com.mikealbert.exception.MalBusinessException;

/**
 * @author anand.mohan
 *
 */

public interface SuppServiceTypeCodeService {
	public List<SupplierServiceTypeCodes> getSuppServiceTypeCodes(String srvTypeCode) throws MalBusinessException; 
}
