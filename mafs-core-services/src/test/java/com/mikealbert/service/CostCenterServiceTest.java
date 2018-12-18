package com.mikealbert.service;

import static org.junit.Assert.*;
import java.util.List;
import javax.annotation.Resource;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import com.mikealbert.testing.BaseTest;
import com.mikealbert.data.entity.CostCentreCode;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.ExternalAccountPK;

public class CostCenterServiceTest extends BaseTest{
	@Resource CostCenterService costCenterService;
	@Value("${generic.externalAccount.withCostCenters}")  String accountWithCostCenters;
	
	/**
	 * Cost Centres will be returned for any client that has a list of them set up.
	 */
	@Test
	public void testCostCentersByAccount(){
		//blockbuster has cost centers set up
		final String accountType = "C";
		final long cId = 1;

		ExternalAccountPK eaPK = new ExternalAccountPK(cId, accountType, accountWithCostCenters);
		ExternalAccount ea = new ExternalAccount(eaPK);
		
		assertTrue(costCenterService.getCostCenters(ea).size() > 0);
        }
	
	
	/**
	 * Cost Centres of "None" will be filtered out of the list for the client (these should really be null/empty)
	 * and a first entry on "Null will be introduced for all clients
	 */
	@Test
	public void testCostCentersFilterNone(){
		// blockbuster has a none cost centre that should be filtered out and need a "Null" entry for UI selection
		final String accountType = "C";
		final long cId = 1;

		ExternalAccountPK eaPK = new ExternalAccountPK(cId, accountType, accountWithCostCenters);
		ExternalAccount ea = new ExternalAccount(eaPK);
		List<CostCentreCode> costCenters = costCenterService.getCostCenters(ea);
		
		assertEquals(costCenters.get(0).getCostCentreCodesPK().getCostCentreCode(),"");
		
		for(int i = 1; i < costCenters.size(); i++){
			if (costCenters.get(i).getCostCentreCodesPK().getCostCentreCode().equalsIgnoreCase("NONE")){
				fail();
			}
		}
	}




}
