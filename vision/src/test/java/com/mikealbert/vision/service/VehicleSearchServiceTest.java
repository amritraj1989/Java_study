package com.mikealbert.vision.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;


import com.mikealbert.data.TestQueryConstants;
import com.mikealbert.data.dao.VehicleSearchDAO;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.data.vo.VehicleSearchCriteriaVO;
import com.mikealbert.data.vo.VehicleSearchResultVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.VehicleSearchService;
import com.mikealbert.testing.BaseTest;

public class VehicleSearchServiceTest extends BaseTest{
	
	@Value("${generic.unitNumber}")  String unitNo;	 
	
    @Resource VehicleSearchService vehicleSearchService;
    
	final PageRequest page = new PageRequest(0, 10);    
	final PageRequest largePage = new PageRequest(0, 100);    
    
    
	@Test
	public void testSearchByDriverName() throws MalBusinessException{
		final String DRIVER_NAME_VARIANT_1 = "Bryan";
		final String DRIVER_NAME_VARIANT_2 = "Bryan,Sus%";
		final String DRIVER_NAME_VARIANT_3 = ",Susan%";
		final String DRIVER_NAME_VARIANT_4 = "Bry%,";		

		VehicleSearchCriteriaVO vehicleSearchCriteriaVO = new VehicleSearchCriteriaVO();
		vehicleSearchCriteriaVO.setDriverName(DRIVER_NAME_VARIANT_1);
		vehicleSearchCriteriaVO.setCorporateEntity(CorporateEntity.MAL);
		vehicleSearchCriteriaVO.setUnitStatus(VehicleSearchDAO.VEHICLE_SEARCH_STATUS_ACTIVE);
		
		List<VehicleSearchResultVO> vehicleSearchResultsVO = vehicleSearchService.findBySearchCriteria(vehicleSearchCriteriaVO, largePage, null);
		assertTrue("Could not find units for driver (Variant 1)", vehicleSearchResultsVO.size() > 0);
		int vehicleCount = vehicleSearchService.findBySearchCriteriaCount(vehicleSearchCriteriaVO);
		assertEquals(vehicleSearchResultsVO.size(), vehicleCount);
		
		vehicleSearchCriteriaVO.setDriverName(DRIVER_NAME_VARIANT_2);
		vehicleSearchResultsVO = vehicleSearchService.findBySearchCriteria(vehicleSearchCriteriaVO, largePage, null);
		assertTrue("Could not find units for driver name search (Variant 2)", vehicleSearchResultsVO.size() > 0);	
		vehicleCount = vehicleSearchService.findBySearchCriteriaCount(vehicleSearchCriteriaVO);
		assertEquals(vehicleSearchResultsVO.size(), vehicleCount);
		
		vehicleSearchCriteriaVO.setDriverName(DRIVER_NAME_VARIANT_3);
		vehicleSearchResultsVO = vehicleSearchService.findBySearchCriteria(vehicleSearchCriteriaVO, largePage, null);
		assertTrue("Could not find units for driver (Variant 3)", vehicleSearchResultsVO.size() > 1);
		vehicleCount = vehicleSearchService.findBySearchCriteriaCount(vehicleSearchCriteriaVO);
		assertEquals(vehicleSearchResultsVO.size(), vehicleCount);
		
		vehicleSearchCriteriaVO.setDriverName(DRIVER_NAME_VARIANT_4);
		vehicleSearchResultsVO = vehicleSearchService.findBySearchCriteria(vehicleSearchCriteriaVO, largePage, null);
		assertTrue("Could not find units for driver (Variant 4)", vehicleSearchResultsVO.size() > 1);
		vehicleCount = vehicleSearchService.findBySearchCriteriaCount(vehicleSearchCriteriaVO);
		assertEquals(vehicleSearchResultsVO.size(), vehicleCount);
				
	}  
	
	@Test
	public void testSearchByUnitNo() throws MalBusinessException{		
		VehicleSearchCriteriaVO vehicleSearchCriteriaVO = new VehicleSearchCriteriaVO();
		vehicleSearchCriteriaVO.setUnitNo(this.unitNo);	
		vehicleSearchCriteriaVO.setCorporateEntity(CorporateEntity.MAL);
		vehicleSearchCriteriaVO.setUnitStatus(VehicleSearchDAO.VEHICLE_SEARCH_STATUS_BOTH);		
		List<VehicleSearchResultVO> vehicleSearchResultsVO = vehicleSearchService.findBySearchCriteria(vehicleSearchCriteriaVO, page, null);		
		assertTrue("Could not find unit based on UnitNo", vehicleSearchResultsVO.size() == 1);		
	}
	
	@Test
	public void testSearchByPONo() throws MalBusinessException{
		final String PO_NO_VARIANT_1 = "J00285742";
		
		VehicleSearchCriteriaVO vehicleSearchCriteriaVO = new VehicleSearchCriteriaVO();
		vehicleSearchCriteriaVO.setPurchaseOrderNumber(PO_NO_VARIANT_1);
		vehicleSearchCriteriaVO.setCorporateEntity(CorporateEntity.MAL);	
		vehicleSearchCriteriaVO.setUnitStatus(VehicleSearchDAO.VEHICLE_SEARCH_STATUS_BOTH);		
		List<VehicleSearchResultVO> vehicleSearchResultsVO = vehicleSearchService.findBySearchCriteria(vehicleSearchCriteriaVO, page, null);	
	
		assertTrue("Could not find units for Purchase Order Number (Variant 1) " + PO_NO_VARIANT_1, vehicleSearchResultsVO.size() == 1);		
	}	
	
	@Test
	public void testSearchByServiceProviderInvoice() throws MalBusinessException{
		final String SERVICE_PROVIDER_INV_VARIANT_1 = "2462092";
		final String SERVICE_PROVIDER_NAME_VARIANT_2 = "Express Oil Change";
		
		VehicleSearchCriteriaVO vehicleSearchCriteriaVO = new VehicleSearchCriteriaVO();
		vehicleSearchCriteriaVO.setServiceProviderInvoiceNumber(SERVICE_PROVIDER_INV_VARIANT_1);
		vehicleSearchCriteriaVO.setServiceProviderName(SERVICE_PROVIDER_NAME_VARIANT_2);
		vehicleSearchCriteriaVO.setCorporateEntity(CorporateEntity.MAL);
		vehicleSearchCriteriaVO.setUnitStatus(VehicleSearchDAO.VEHICLE_SEARCH_STATUS_BOTH);		
		List<VehicleSearchResultVO> vehicleSearchResultsVO = vehicleSearchService.findBySearchCriteria(vehicleSearchCriteriaVO, page, null);	
		assertTrue("Could not find units for Service Provider Invoice Number (Variant 1)", 
				vehicleSearchResultsVO.size() == 1 && vehicleSearchResultsVO.get(0).getServiceProviderInvoiceNumber().equals("2462092"));		
	}
	
	@Ignore
	public void testSearchResultHavingOpenInvoices() throws MalBusinessException{
		String unitHavingOpenPo = (String)em.createNativeQuery(TestQueryConstants.MAINT_REQUEST_OPEN_UNIT_NO).getSingleResult().toString();

		VehicleSearchCriteriaVO vehicleSearchCriteriaVO = new VehicleSearchCriteriaVO();
		vehicleSearchCriteriaVO.setUnitNo(unitHavingOpenPo);
		vehicleSearchCriteriaVO.setCorporateEntity(CorporateEntity.MAL);
		vehicleSearchCriteriaVO.setUnitStatus(VehicleSearchDAO.VEHICLE_SEARCH_STATUS_BOTH);		
		List<VehicleSearchResultVO> vehicleSearchResultsVO = vehicleSearchService.findBySearchCriteria(vehicleSearchCriteriaVO, page, null);	
		assertTrue("Could not find Open PO for unit:" + unitHavingOpenPo, 
				vehicleSearchResultsVO.size() == 1 && vehicleSearchResultsVO.get(0).getNumOfOpenMaintPOs() > 0);		
	}
	
/*	
	@Test(expected=MalBusinessException.class)	
	public void testValidations() throws MalBusinessException{
		final String VENDOR_INV_VARIANT_1 = "10000";
		
		VehicleSearchCriteriaVO vehicleSearchCriteriaVO = new VehicleSearchCriteriaVO();
		vehicleSearchCriteriaVO.setVendorInvoiceNumber(VENDOR_INV_VARIANT_1);
		List<VehicleInformationVO> vehicleSearchResultsVO = vehicleSearchService.findBySearchCriteria(vehicleSearchCriteriaVO, page, null);
	}
*/	
}
