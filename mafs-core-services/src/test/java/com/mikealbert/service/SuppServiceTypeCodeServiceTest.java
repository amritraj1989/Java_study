
/**
 * SuppServiceTypeCodeServiceTest.java
 * mafs-core-services
 * Oct 7, 2013
 * 3:06:21 PM
 */
package com.mikealbert.service;

import java.util.List;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Test;

import com.mikealbert.data.entity.SupplierServiceTypeCodes;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.testing.BaseTest;

/**
 * @author anand.mohan
 *
 */
public class SuppServiceTypeCodeServiceTest extends BaseTest {
	@Resource
	private SuppServiceTypeCodeService	suppServiceTypeCodeService;
	@Test
	public void	getSuppServiceTypeCodes() throws MalBusinessException{
		String	srvTypeCode = null;
		List<SupplierServiceTypeCodes> list = suppServiceTypeCodeService.getSuppServiceTypeCodes(srvTypeCode);
		Assert.assertNotNull(list);
	}

}
