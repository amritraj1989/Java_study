package com.mikealbert.vision.service;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.List;
import javax.annotation.Resource;
import org.junit.Test;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import com.mikealbert.data.entity.SupplierProgressHistory;
import com.mikealbert.testing.BaseTest;

public class SupplierServiceTest extends BaseTest{
		
    @Resource SupplierService supplierService;
    
	
	@Test
	public void testGetEmptySupplierProgressHistory() {
		final Long docId = 1l;		
		List<SupplierProgressHistory> sph = supplierService.getSupplierProgressHistory(docId, new Sort(new Sort.Order(Direction.ASC, "progressType"), new Sort.Order(Direction.ASC, "sphId")));		
		assertEquals(sph.size(), 0);
	}	
	
	@Test
	public void testGetSupplierProgressHistoryWithOneEntry() {
		final Long docId = 2161012l;		
		List<SupplierProgressHistory> sph = supplierService.getSupplierProgressHistory(docId, new Sort(new Sort.Order(Direction.ASC, "progressType"), new Sort.Order(Direction.ASC, "sphId")));	
		assertEquals(sph.size(), 1);
		assertTrue(sph.get(0).getProgressType().equalsIgnoreCase("14_ETA"));
	}	

	
	@Test
	public void testGetSupplierProgressHistoryWithDuplicates() {
		final Long docId = 2160374l;		
		List<SupplierProgressHistory> sph = supplierService.getSupplierProgressHistory(docId, new Sort(new Sort.Order(Direction.ASC, "progressType"), new Sort.Order(Direction.ASC, "sphId")));			
		assertEquals(sph.size(), 5);
	}	

	@Test
	public void testGetSupplierProgressHistoryWithoutDuplicates() {
		final Long docId = 1117l;		
		List<SupplierProgressHistory> sph = supplierService.getSupplierProgressHistory(docId, new Sort(new Sort.Order(Direction.ASC, "progressType"), new Sort.Order(Direction.ASC, "sphId")));		
		assertEquals(sph.size(), 11);
	}	


	
}
