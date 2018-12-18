package com.mikealbert.service;

import static org.junit.Assert.*;

import java.util.Calendar;
import javax.annotation.Resource;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import com.mikealbert.testing.BaseTest;
import com.mikealbert.data.entity.ContractLine;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.data.enumeration.TimePeriodCalendarEnum;

public class ContractServiceTest  extends BaseTest{
	
	@Value("${generic.contractsMultiple.unitNo}")  String unitNoMultiContract;
	@Value("${generic.contractsMultiple.contractLineId}")  Long clnIdResultMultiContract;
	@Value("${generic.contractsSingle.unitNo}")  String unitNoSingleContract;
	@Value("${generic.contractsSingle.contractLineId}")  Long clnIdResultSingleContract;
	
    @Resource FleetMasterService fleetMasterService;
	@Resource ContractService contractService;
    
	//On Contract unit w/ multiple
	final String unitNoMultiAmendments = "00945056"; //TODO Dynamic: On Contract UnitNO w/ multi amendments 
    
	@Test
    public void testGetActiveContractLine(){
		FleetMaster fleetMaster;
		ContractLine contractLine;

		//Test a unit with multiple contracts with each containing multiple contract lines
		fleetMaster = fleetMasterService.findByUnitNo(unitNoMultiContract);       		
		contractLine = contractService.getLastActiveContractLine(fleetMaster, Calendar.getInstance().getTime());
        assertTrue("No contract lines found ", fleetMaster.getContractLineList().size() > 0);		
		assertTrue("Incorrect active contract line" + contractLine.getClnId(), contractLine.getClnId().equals(clnIdResultMultiContract));
		
		//Test a unit with a single contract with single contract line
		fleetMaster = fleetMasterService.findByUnitNo(unitNoSingleContract);       
		contractLine = contractService.getLastActiveContractLine(fleetMaster, Calendar.getInstance().getTime());  
        assertTrue("No contract lines found ", fleetMaster.getContractLineList().size() > 0);		
		assertTrue("Incorrect active contract line " + contractLine.getClnId(), contractLine.getClnId().equals(clnIdResultSingleContract));	
        		
	}
	
	@Test
	public void testGetOriginalContractLine(){
		FleetMaster fleetMaster;
		ContractLine contractLine;
		
		fleetMaster = fleetMasterService.findByUnitNo(unitNoMultiAmendments);  
		contractLine = contractService.getOriginalContractLine(fleetMaster);	
		
		assertNotNull("Original Contract Line not found", contractLine);
		assertTrue("Incorrect revision of the contract", contractLine.getContract().getRevNo() == 0);			
		assertTrue("Incorrect revision of the contract line", contractLine.getRevNo() == 1);		
		
	}
	
	@Test
	public void testGetOriginalContractLineOfCurrentAccount(){
		
		String unitPreviouslyAssignedToAnotherAccount = "00962627";
		int contractLineId = 169825;
		
		FleetMaster fleetMaster = fleetMasterService.findByUnitNo(unitPreviouslyAssignedToAnotherAccount);
		ExternalAccount currentAccount = contractService.getLastActiveContractLine(fleetMaster, Calendar.getInstance().getTime()).getContract().getExternalAccount();
		
		ContractLine originalContractLineCurrentAccount = contractService.getOriginalContractLine(fleetMaster, currentAccount);
		assertTrue("Incorrect contract line", originalContractLineCurrentAccount.getClnId() == contractLineId);
		
		ContractLine originalContractLine = contractService.getOriginalContractLine(fleetMaster);
		assertTrue(originalContractLine.getClnId() != originalContractLineCurrentAccount.getClnId());

	}
	

	public void testIsWithinTimePeriod(){
		boolean found = false;
		found = contractService.isWithinTimePeriod(
				CorporateEntity.MAL, TimePeriodCalendarEnum.POSTING, Calendar.getInstance().getTime());
		assertTrue("Time Period could not be found", found);
			
	}

}
