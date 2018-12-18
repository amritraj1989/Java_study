package com.mikealbert.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.annotation.Resource;
import javax.validation.constraints.AssertFalse;

import org.junit.Ignore;
import org.junit.Test;

import com.mikealbert.data.entity.ContractLine;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.Product;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.vo.ServiceElementsVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.testing.BaseTest;
import com.mikealbert.util.MALUtilities;

public class ServiceElementServiceTest extends BaseTest {
	@Resource ServiceElementService serviceElementService;
	@Resource QuotationService quotationService;
	@Resource FleetMasterService fleetMasterService;
	@Resource ContractService contractService;
	
	@Test
	public void testGetRootParent() {
		ExternalAccount parent = new ExternalAccount();
		
		parent = serviceElementService.getRootParentAccount(1L, "C", "00004168");
		
		assertTrue(!MALUtilities.isEmpty(parent));		
	}
	
	@Test
	public void testGetAssignedServiceElements() throws MalBusinessException {
		List<ServiceElementsVO> assignedElements = serviceElementService.getAssignedServiceElements( quotationService.getQuotationModel(309199L));
		//List<ServiceElementsVO> assignedElements = serviceElementService.getAssignedServiceElements( quotationService.getQuotationModel(298910L));
		
		assertTrue(assignedElements.size() > 0);		
	}
	
	@Ignore
	@Test
	public void testGetAvailableServiceElements() throws MalBusinessException {
		List<ServiceElementsVO> availableElements = serviceElementService.getAvailableServiceElements(quotationService.getQuotationModel(309199L));
		//List<ServiceElementsVO> availableElements = serviceElementService.getAvailableServiceElements(quotationService.getQuotationModel(249244L));
		
		assertTrue(availableElements.size() > 0);		
	}
	
	@Test
	public void testDetermineElementsWithChanges() throws MalBusinessException {
		//List<ServiceElementsVO> availableElements = serviceElementService.determineElementsWithChanges(quotationService.getQuotationModel(309199L));
		List<ServiceElementsVO> availableElements = serviceElementService.determineElementsWithChanges(quotationService.getQuotationModel(249244L));
		
		assertTrue(availableElements.size() > 0);		
	}
	
	@Ignore
	public void testHasGradeGroupOverrides() throws MalBusinessException {
		boolean hasOverrides = false;
		
		hasOverrides = serviceElementService.isGradeGroupOverrides(1L, "C", "00000017");
		
		assertTrue(hasOverrides);		
	}

	@Test
	public void testNoGradeGroupOverrides() throws MalBusinessException {
		boolean hasOverrides = false;
		
		hasOverrides = serviceElementService.isGradeGroupOverrides(1L, "C", "00024040");
		
		assertTrue(!hasOverrides);			
	}

	@Ignore
	@Test
	public void testGetAvailableElementsByAccountAndGradeGroup() throws MalBusinessException {
		
		QuotationModel quotationModel = quotationService.getQuotationModel(309199L); 
				
		List<ServiceElementsVO> availableElements = serviceElementService.getAvailableServiceElements(quotationModel,quotationModel.getQuotation().getExternalAccount(), "U",quotationModel.getQuotation().getQuotationProfile());
		
		assertTrue(availableElements.size() > 0);			
	}
	
	@Ignore
	@Test
	//test already being ignored, added excludeQuoteLvlOverrides flag 'false' to hasServiceElementsChanged() just to compile 
	public void testHasServiceElementsChangedNoGradeGroup() throws MalBusinessException {
		boolean hasChanged = serviceElementService.hasServiceElementsChanged(310899L, 1L, "C", "00004173", null, false);
		QuotationModel quotationModel = quotationService.getQuotationModel(310899L); 
		List<ServiceElementsVO> availableElements =  serviceElementService.getAvailableServiceElements(quotationModel,quotationModel.getQuotation().getExternalAccount(),"U",quotationModel.getQuotation().getQuotationProfile());
		
		assertTrue(hasChanged);			
	}
	
	@Test
	public void testGetAssignedServiceElementsWInformalAmmend() throws MalBusinessException {
		List<ServiceElementsVO> assignedElements = serviceElementService.getAssignedServiceElements( quotationService.getQuotationModel(198351L));
		// there are 6 service elements
		assertTrue(assignedElements.size() == 6);
		
		boolean foundAutoTal = false;
		boolean foundAutotag = false;
		// they include AUTO_TAL
		for(ServiceElementsVO elem: assignedElements){
			if(elem.getName().equalsIgnoreCase("AUTO_TAL")){
				foundAutoTal = true;
			}
		}
		assertTrue(foundAutoTal);
		
		
		// they exclude AUTOTAG
		for(ServiceElementsVO elem: assignedElements){
			if(elem.getName().equalsIgnoreCase("AUTOTAG")){
				foundAutotag = true;
			}
		}
		
		assertFalse(foundAutotag);
		
	}
	
	@Test
	public void testFindActiveClientProductList() throws MalBusinessException {
		
		List<Product> activeClientProductList = serviceElementService.findActiveClientProductList();

		assertTrue(activeClientProductList.size() > 0);		
	}	
	
	
	@Test
	public void testHasMaintenance() throws MalBusinessException {
		String accountCode =  "00028527";
				
		assertTrue(serviceElementService.hasMaintenance(accountCode));		
	}	
	
	
	@Test
	public void testHasRisk() throws MalBusinessException {
		String accountCode =  "00001959";
		
		assertTrue(serviceElementService.hasRisk(accountCode));			 
	}	
	
	@Test
	public void testHasERRoadside() throws MalBusinessException {
		String accountCode =  "00028527";

		assertTrue(serviceElementService.hasEmergencyRoadSide(accountCode));			
	}	
	
	@Test
	public void testHasMaintenanceOnVehicle() throws MalBusinessException {
		Long fmsId =  1168580l;
		FleetMaster fleetMaster = fleetMasterService.getFleetMasterByFmsId(fmsId);

		ContractLine currContractLine = contractService.getCurrentOrFutureContractLine(fleetMaster);
		ContractLine vehicleContractLine = contractService.getQuotationModelOnContractLine(currContractLine.getClnId());
		
		assertTrue(serviceElementService.hasMaintenance(vehicleContractLine.getQuotationModel()));			
	}
	
	
	
}
