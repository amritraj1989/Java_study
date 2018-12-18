package com.mikealbert.vision.aspect;

import static org.junit.Assert.*;

import javax.annotation.Resource;
import javax.persistence.Query;
import org.junit.Test;
import com.mikealbert.testing.BaseTest;
import com.mikealbert.vision.service.SupplierService;

public class TransactionAspectTest extends BaseTest {
	@Resource SupplierService supplierService;

	@Test
	public void test() {		
		String sql = "    SELECT param_value " +
                     "        FROM session_params " +
                     "        WHERE param_key = 'USER' ";

		supplierService.deleteSupplierProgressHistory(1L);
		
		Query query = em.createNativeQuery(sql);
		String val = (String)query.getSingleResult();
		
		assertEquals("Transient value was not found in DB", val, "UKNOWN");
	}

}
