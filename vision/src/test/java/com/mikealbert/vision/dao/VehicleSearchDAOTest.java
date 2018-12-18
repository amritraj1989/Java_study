package com.mikealbert.vision.dao;

import static org.junit.Assert.assertTrue;
import java.util.List;
import javax.annotation.Resource;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;

import com.mikealbert.testing.BaseTest;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.data.TestQueryConstants;
import com.mikealbert.data.dao.VehicleSearchDAO;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.data.vo.VehicleSearchCriteriaVO;
import com.mikealbert.data.vo.VehicleSearchResultVO;

public class VehicleSearchDAOTest extends BaseTest {

	@Value("${generic.unitNumber}")  String unitNo;	 
	
	@Resource VehicleSearchDAO vehicleSearchDAO;
	
	final PageRequest page = new PageRequest(0, 50); 	
		
	@Test
	public void testSearchByVIN(){
		final String VIN_VARIANT_1 = "010616"; //TODO: Randomly pull VIN.
		
		VehicleSearchCriteriaVO vehicleSearchCriteriaVO = new VehicleSearchCriteriaVO();
		vehicleSearchCriteriaVO.setVIN(VIN_VARIANT_1);
		vehicleSearchCriteriaVO.setCorporateEntity(CorporateEntity.MAL);
		vehicleSearchCriteriaVO.setUnitStatus(VehicleSearchDAO.VEHICLE_SEARCH_STATUS_BOTH);
		
		List<VehicleSearchResultVO> vehicleSearchResultsVO = vehicleSearchDAO.searchVehicles(vehicleSearchCriteriaVO, page, null);	
	
		assertTrue("Could not find units for VIN " + VIN_VARIANT_1, 
				vehicleSearchResultsVO.size() > 0 && vehicleSearchResultsVO.get(0).getVIN()
				.substring(vehicleSearchResultsVO.get(0).getVIN().length() - 6).equals(VIN_VARIANT_1));				
	}
	
	@Test
	public void testSearchByUnitNO(){	
		final String UNIT_NO_VARIANT_1 = (String) em.createNativeQuery(TestQueryConstants.READ_UNIT_NO_CURRENT_OR_FUTURE_ALLOCATION).getSingleResult();
		
		VehicleSearchCriteriaVO vehicleSearchCriteriaVO = new VehicleSearchCriteriaVO();
		vehicleSearchCriteriaVO.setUnitNo(UNIT_NO_VARIANT_1);
		vehicleSearchCriteriaVO.setCorporateEntity(CorporateEntity.MAL);
		vehicleSearchCriteriaVO.setUnitStatus(VehicleSearchDAO.VEHICLE_SEARCH_STATUS_BOTH);
		
		List<VehicleSearchResultVO> vehicleSearchResultsVO = vehicleSearchDAO.searchVehicles(vehicleSearchCriteriaVO, page, null);	
		
		assertTrue("Could not find units for Unit Number " + UNIT_NO_VARIANT_1, vehicleSearchResultsVO.size() == 1
				&& vehicleSearchResultsVO.get(0).getUnitNo().equals(UNIT_NO_VARIANT_1));			
		
	}
	
	@Test
	public void testContractVehicleSearchByUnitNo(){	
		final String UNIT_NO_VARIANT_1 = (String) em.createNativeQuery(TestQueryConstants.READ_UNIT_NO_CURRENT_OR_FUTURE_ALLOCATION).getSingleResult();
		
		VehicleSearchCriteriaVO vehicleSearchCriteriaVO = new VehicleSearchCriteriaVO();
		vehicleSearchCriteriaVO.setUnitNo(UNIT_NO_VARIANT_1);
		vehicleSearchCriteriaVO.setCorporateEntity(CorporateEntity.MAL);
		vehicleSearchCriteriaVO.setUnitStatus(VehicleSearchDAO.VEHICLE_SEARCH_STATUS_BOTH);
		
		vehicleSearchCriteriaVO.setContractVehicleSearch(true);
		
		List<VehicleSearchResultVO> vehicleSearchResultsVO = vehicleSearchDAO.searchVehicles(vehicleSearchCriteriaVO, page, null);	
		
		assertTrue("Could not find units for Unit Number " + UNIT_NO_VARIANT_1, vehicleSearchResultsVO.size() == 1
				&& vehicleSearchResultsVO.get(0).getUnitNo().equals(UNIT_NO_VARIANT_1));			
		
	}	
	
	@Test
	public void testSearchByDriverName(){	
		final String DRIVER_NAME_VARIANT_1 = "Heslo";

		VehicleSearchCriteriaVO vehicleSearchCriteriaVO = new VehicleSearchCriteriaVO();
		vehicleSearchCriteriaVO.setDriverName(DRIVER_NAME_VARIANT_1);
		vehicleSearchCriteriaVO.setCorporateEntity(CorporateEntity.MAL);
		vehicleSearchCriteriaVO.setUnitStatus(VehicleSearchDAO.VEHICLE_SEARCH_STATUS_ACTIVE);
		
		List<VehicleSearchResultVO> vehicleSearchResultsVO = vehicleSearchDAO.searchVehicles(vehicleSearchCriteriaVO, page, null);		

		assertTrue("Could not find units for driver (Variant 1)  " + DRIVER_NAME_VARIANT_1, vehicleSearchResultsVO.size() == vehicleSearchDAO.searchVehiclesCount(vehicleSearchCriteriaVO));				
	}
	
	@Test
	public void testSearchByFleetRefNo(){	
		final String FLEET_REF_NO_VARIANT_1 = (String) em.createNativeQuery(TestQueryConstants.READ_CLIENT_FLEET_REFERENCE_NUMBER).getSingleResult();
		
		VehicleSearchCriteriaVO vehicleSearchCriteriaVO = new VehicleSearchCriteriaVO();
		vehicleSearchCriteriaVO.setClientFleetReferenceNumber(FLEET_REF_NO_VARIANT_1);
		vehicleSearchCriteriaVO.setCorporateEntity(CorporateEntity.MAL);
		vehicleSearchCriteriaVO.setUnitStatus(VehicleSearchDAO.VEHICLE_SEARCH_STATUS_BOTH); 
		
		List<VehicleSearchResultVO> vehicleSearchResultsVO = vehicleSearchDAO.searchVehicles(vehicleSearchCriteriaVO, page, null);	
	
		assertTrue("Could not find units for client's Fleet Ref Number " + FLEET_REF_NO_VARIANT_1, vehicleSearchResultsVO.size() > 0
				&& vehicleSearchResultsVO.get(0).getClientFleetReferenceNumber().toUpperCase().equals(FLEET_REF_NO_VARIANT_1.toUpperCase()));					
	}
	
	@Test
	public void testSearchByLicensePlate(){		
		final String LICENSE_PLATE_NO_VARIANT_1 = "CV3H305"; //TODO: Randomly pull license plate No..
		final String LICENSE_PLATE_NO_VARIANT_2 = "DF130087"; //Not in TAL but in FleetMaster.regNo
		
		List<VehicleSearchResultVO> vehicleSearchResultsVO;				
		VehicleSearchCriteriaVO vehicleSearchCriteriaVO = new VehicleSearchCriteriaVO();
		
		vehicleSearchCriteriaVO.setLicensePlateNo(LICENSE_PLATE_NO_VARIANT_1);
		vehicleSearchCriteriaVO.setCorporateEntity(CorporateEntity.MAL);		
		vehicleSearchCriteriaVO.setUnitStatus(VehicleSearchDAO.VEHICLE_SEARCH_STATUS_BOTH);
		
		vehicleSearchResultsVO = vehicleSearchDAO.searchVehicles(vehicleSearchCriteriaVO, page, null);	
		
		assertTrue("Could not find units for License Plate Number " + LICENSE_PLATE_NO_VARIANT_1, 
				vehicleSearchResultsVO.size() > 0 && vehicleSearchResultsVO.get(0).getLicensePlateNo().equals(LICENSE_PLATE_NO_VARIANT_1));	
		
		vehicleSearchCriteriaVO.setLicensePlateNo(LICENSE_PLATE_NO_VARIANT_2);	
		vehicleSearchResultsVO = vehicleSearchDAO.searchVehicles(vehicleSearchCriteriaVO, page, null);	
		
		assertTrue("Could not find units for License Plate Number " + LICENSE_PLATE_NO_VARIANT_2, 
				vehicleSearchResultsVO.size() > 0 && vehicleSearchResultsVO.get(0).getLicensePlateNo().equals(LICENSE_PLATE_NO_VARIANT_2));		
	}	
	
	@Test
	public void testSearchByClient(){	
		Object[] record = null;
		String accountCode = "";
		String accountName = "";
		
		record = (Object[])em.createNativeQuery(TestQueryConstants.READ_CLIENT_ACCOUNT_WITH_ACTIVE_UNITS).getSingleResult();		
		accountCode = (String)record[0];
		accountName  = (String)record[1];

		VehicleSearchCriteriaVO vehicleSearchCriteriaVO = new VehicleSearchCriteriaVO();
		vehicleSearchCriteriaVO.setClientAccountNumber(accountCode);
		vehicleSearchCriteriaVO.setClientAccountName(accountName);
		vehicleSearchCriteriaVO.setCorporateEntity(CorporateEntity.MAL);
		vehicleSearchCriteriaVO.setUnitStatus(VehicleSearchDAO.VEHICLE_SEARCH_STATUS_BOTH);
		
		List<VehicleSearchResultVO> vehicleSearchResultsVO = vehicleSearchDAO.searchVehicles(vehicleSearchCriteriaVO, page, null);		

		assertTrue("Could not find units for Client Account: " + accountCode + "-" + accountName, vehicleSearchResultsVO.size() > 1
				&& vehicleSearchResultsVO.get(0).getClientAccountNumber().equals(accountCode));				
	}	
	
	@Test
	public void testSearchByPONo(){			
		final String PO_NO_VARIANT_1 = (String)em.createNativeQuery(TestQueryConstants.READ_MAINTENANCE_PO_NUMBER).getSingleResult();				
		
		VehicleSearchCriteriaVO vehicleSearchCriteriaVO = new VehicleSearchCriteriaVO();
		vehicleSearchCriteriaVO.setPurchaseOrderNumber(PO_NO_VARIANT_1);
		vehicleSearchCriteriaVO.setCorporateEntity(CorporateEntity.MAL);
		vehicleSearchCriteriaVO.setUnitStatus(VehicleSearchDAO.VEHICLE_SEARCH_STATUS_BOTH);
		
		List<VehicleSearchResultVO> vehicleSearchResultsVO = vehicleSearchDAO.searchVehicles(vehicleSearchCriteriaVO, page, null);	
	
		assertTrue("Could not find units for Purchase Order Number " + PO_NO_VARIANT_1, vehicleSearchResultsVO.size() == 1
				&& vehicleSearchResultsVO.get(0).getPurchaseOrderNumber().equals(PO_NO_VARIANT_1));		
	}
	
	@Test
	public void testSearchByServiceProviderInvoice(){	
		Object[] record = null;
		String accountCode = "";
		String invoiceNo = "";
		
		record = (Object[])em.createNativeQuery(TestQueryConstants.READ_MAINTENANCE_SERVICE_PROVIDER_INVOICE).getSingleResult();		
		accountCode = (String)record[0];
		invoiceNo  = (String)record[1];
		
		VehicleSearchCriteriaVO vehicleSearchCriteriaVO = new VehicleSearchCriteriaVO();	
		vehicleSearchCriteriaVO.setServiceProviderInvoiceNumber(invoiceNo);
		vehicleSearchCriteriaVO.setCorporateEntity(CorporateEntity.MAL);
		vehicleSearchCriteriaVO.setUnitStatus(VehicleSearchDAO.VEHICLE_SEARCH_STATUS_BOTH);
		
		List<VehicleSearchResultVO> vehicleSearchResultsVO = vehicleSearchDAO.searchVehicles(vehicleSearchCriteriaVO, page, null);
		
		assertTrue("Could not find units for Service Provider/Invoice Number " + accountCode + "/" + invoiceNo, 
				vehicleSearchResultsVO.size() == 1 && vehicleSearchResultsVO.get(0).getServiceProviderInvoiceNumber().equals(invoiceNo));		
	}
	
	@Test
	public void testSearchByMAFSInvoice(){		
		final String MAFS_INV_VARIANT_1 = (String)em.createNativeQuery(TestQueryConstants.READ_MAINTENANCE_INTERNAL_INVOICE).getSingleResult();			
		
		VehicleSearchCriteriaVO vehicleSearchCriteriaVO = new VehicleSearchCriteriaVO();
		vehicleSearchCriteriaVO.setInternalnvoiceNumber(MAFS_INV_VARIANT_1);
		vehicleSearchCriteriaVO.setCorporateEntity(CorporateEntity.MAL);	
		vehicleSearchCriteriaVO.setUnitStatus(VehicleSearchDAO.VEHICLE_SEARCH_STATUS_BOTH);
		
		List<VehicleSearchResultVO> vehicleSearchResultsVO = vehicleSearchDAO.searchVehicles(vehicleSearchCriteriaVO, page, null);
		
		assertTrue("Could not find units for MAFS Invoice Number " + MAFS_INV_VARIANT_1, 
				vehicleSearchResultsVO.size() == 1 && vehicleSearchResultsVO.get(0).getInternalInvoiceNumber().equals(MAFS_INV_VARIANT_1));		
	}	
	
	@Test
	public void testVehicleStatusDate(){
		VehicleSearchCriteriaVO vehicleSearchCriteriaVO = new VehicleSearchCriteriaVO();
		vehicleSearchCriteriaVO.setUnitNo("00184243");
		vehicleSearchCriteriaVO.setCorporateEntity(CorporateEntity.MAL);
		vehicleSearchCriteriaVO.setUnitStatus(VehicleSearchDAO.VEHICLE_SEARCH_STATUS_BOTH);
		String expectedDate = "12/20/2013";
		
		List<VehicleSearchResultVO> vehicleSearchResultsVO = vehicleSearchDAO.searchVehicles(vehicleSearchCriteriaVO, page, null);
		
		assertTrue("Could not find units for Unit Number: 00184243" , 
				vehicleSearchResultsVO.size() == 1 && vehicleSearchResultsVO.get(0).getUnitNo().equals("00184243"));
		System.out.println(expectedDate +""+MALUtilities.getNullSafeDatetoString(vehicleSearchResultsVO.get(0).getUnitStatusDate()));
		assertTrue(expectedDate.equalsIgnoreCase(MALUtilities.getNullSafeDatetoString(vehicleSearchResultsVO.get(0).getUnitStatusDate())));
	}	
	@Test
	public void testSearchByScheduleNumber(){	
		
		
		VehicleSearchCriteriaVO vehicleSearchCriteriaVO = new VehicleSearchCriteriaVO();
		vehicleSearchCriteriaVO.setVehSchSeq("1004");
		vehicleSearchCriteriaVO.setCorporateEntity(CorporateEntity.MAL);
		vehicleSearchCriteriaVO.setUnitStatus(VehicleSearchDAO.VEHICLE_SEARCH_STATUS_BOTH); 
		
		List<VehicleSearchResultVO> vehicleSearchResultsVO = vehicleSearchDAO.searchVehicles(vehicleSearchCriteriaVO, page, null);	
	
		assertTrue("Could not find units for Schedule Number 1" , vehicleSearchResultsVO.size() > 0);					
	}
	
}
