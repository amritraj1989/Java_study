package com.mikealbert.service;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import javax.annotation.Resource;
import javax.persistence.Query;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import com.mikealbert.data.TestQueryConstants;
import com.mikealbert.data.dao.ClientContactDAO;
import com.mikealbert.data.dao.ClientPointAccountDAO;
import com.mikealbert.data.entity.ClientContact;
import com.mikealbert.data.entity.ClientPoint;
import com.mikealbert.data.entity.ClientPointAccount;
import com.mikealbert.data.entity.CostCentreCode;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.data.vo.ClientContactVO;
import com.mikealbert.data.vo.POCCostCenterVO;
import com.mikealbert.data.vo.PointOfCommunicationVO;
import com.mikealbert.testing.BaseTest;
import com.mikealbert.util.MALUtilities;


public class ClientPOCServiceTest extends BaseTest{
	@Resource ClientPOCService clientPOCService;
	@Resource CustomerAccountService customerAccountService;
	@Resource FleetMasterService fleetMasterService;
	@Resource CostCenterService costCenterService;
	@Resource ClientContactDAO clientContactDAO;
	@Resource ClientPointAccountDAO clientPointAccountDAO;
	
	static final String POC_NAME = "Exceeds Maintenance Authorization Limit";
	static final String SYSTEM_NAME = "MAINT";	
	static final Long CLIENT_POINT_ACCOUNT_ID = 5L;
	static final String CLIENT_ACCOUNT_NAME = "Channel Bio Corp.";
	static final String UNIT_NO = "00933833";
		
	PageRequest page = new PageRequest(0, 50);

	@Value("${generic.externalAccount.withCostCenters}")  String accountNoWithCostCenters;
	
	@Test
	public void testGetPointOfCommunicationVOs(){
		List<PointOfCommunicationVO> pocVOs;
		List<ExternalAccount> accounts;
		
		accounts = customerAccountService.findOpenCustomerAccountsByCode(accountNoWithCostCenters);		
		pocVOs = clientPOCService.getPointOfCommunicationVOs(accounts.get(0));
		
		assertTrue("POCs were not found for client", pocVOs.size() > 0);		
	}
	
	@Test
	public void testGetClientPoint(){
		ClientPoint clientPoint;
		Long clientPointId;

		Query query = em.createNativeQuery(TestQueryConstants.READ_CLIENT_POINT_ID);			
		clientPointId = ((BigDecimal)query.getSingleResult()).longValue();

		//TODO instead of skipping when id does not exist, create one.
		if(!MALUtilities.isEmpty(clientPointId)){
			clientPoint = clientPOCService.getClientPoint(clientPointId);
			
			assertNotNull("Client Point was not found, id = " + clientPointId, clientPoint);			
		} else {
			System.err.println("Skipped testGetClientPoint - database does not contain a client point");
		}
	}
	
	@Test
	public void testGetClientPOC(){
		ClientPointAccount clientPOC;
		Long clientPointAccountId;
		
		Query query = em.createNativeQuery(TestQueryConstants.READ_CLIENT_POINT_ACCOUNT_ID);			
		clientPointAccountId = ((BigDecimal)query.getSingleResult()).longValue();
		
		//TODO instead of skipping when id does not exist, create one.
		if(!MALUtilities.isEmpty(clientPointAccountId)){
			clientPOC = clientPOCService.getClientPOC(clientPointAccountId);
			
			assertNotNull("Client POC was not found, id = " + clientPointAccountId, clientPOC);			
		} else {
			System.err.println("Skipped testGetClientPOC - database does not contain a client point account");			
		}

	}
	
	@Test
	public void testGetSetupClientContactVOs(){
		List<ClientContactVO> ccVOs;
		ClientPointAccount clientPOC;
		Long clientPointAccountId;
		int ccVOCount;
		
		Query query = em.createNativeQuery(TestQueryConstants.READ_CLIENT_POINT_ACCOUNT_ID);			
		clientPointAccountId = ((BigDecimal)query.getSingleResult()).longValue();		
		
		clientPOC = getClientPointAccount(clientPointAccountId);
		
		ccVOs = clientPOCService.getSetupClientContactVOs(clientPOC, null, page, null);
		ccVOCount = clientPOCService.getSetupClientContactVOsCount(clientPOC, null);
		
		assertNotNull("Client POC setup contacts wrer not found, clientPointAccountId = " + clientPointAccountId, ccVOs);
		assertTrue("Client POC setup contacts count is incorrect", ccVOCount == ccVOs.size() );
	}
	
	@Test
	public void testGetClientContactVOs(){
		List<ClientContactVO> ccVOs;
		List<ExternalAccount> accounts = customerAccountService.findOpenCustomerAccounts(CLIENT_ACCOUNT_NAME);
		
		FleetMaster fms = fleetMasterService.findByUnitNo(UNIT_NO);	
		ccVOs = clientPOCService.getClientContactVOs(fms, accounts.get(0), null, POC_NAME, SYSTEM_NAME, false);
		
		assertTrue("No contacts were found", ccVOs.size() > 0);
	}

	
	
	@Test
	public void testGetDriverClientContactVO(){
		ClientContactVO driverCCVO;		
		ClientContact costCenterDriverContact;
			
		costCenterDriverContact = readClientContactAsCostCenterDriver();
		if(!MALUtilities.isEmpty(costCenterDriverContact)){
			driverCCVO = clientPOCService.getDriverClientContactVO(costCenterDriverContact.getClientPointAccount(), costCenterDriverContact.getCostCentreCode());
			
			assertNotNull("Did not retrieve the Driver's client contact VO clientContactId = " + costCenterDriverContact.getClientContactId(), driverCCVO);
			assertTrue("Client contact VO driver indicator is not set clientContactId = " + costCenterDriverContact.getClientContactId(), driverCCVO.isDriver());
		}
	}
	
	@Test
	public void testIsDriverAssignedToPOC(){
		ClientContact driverContact;
		boolean found;
			
		driverContact = readClientContactAsPOCDriver();		
		if(!MALUtilities.isEmpty(driverContact)){		
			found = clientPOCService.isDriverAssigned(driverContact.getClientPointAccount(), null);	
			
			assertTrue("Did not recognize client contact as an assigned POC driver. cconId = " + driverContact.getClientContactId(), found);			
		} 
		
		driverContact = readClientContactAsCostCenterDriver();		
		if(!MALUtilities.isEmpty(driverContact)){		
			found = clientPOCService.isDriverAssigned(driverContact.getClientPointAccount(), driverContact.getCostCentreCode());	
			
			assertTrue("Did not recognize client contact as an assigned Cost Center driver. cconId = " + driverContact.getClientContactId(), found);			
		} 		
	}
	
	@Test
	public void testGetSetupPOCCostCenterVOs(){
		List<POCCostCenterVO> pocCCVOs;
		ClientPointAccount clientPOC;
		PageRequest page;
		Sort sort;
		int count;
		
		clientPOC = readClientPOCWithCostCenter();
		if(!MALUtilities.isEmpty(clientPOC)){
			sort = new Sort(Sort.Direction.ASC, "costCentreCodePK.costCentreCode");		
			page = new PageRequest(0, 1, sort);
			pocCCVOs = clientPOCService.getSetupPOCCostCenterVOs(clientPOC, page);
			count = clientPOCService.getSetupPOCCostCenterVOCount(clientPOC);
			
			assertTrue("Did not find the POC's Cost Center(s)", pocCCVOs.size() > 0);
			assertTrue("The POC's Cost Centers count is in correct", count >= pocCCVOs.size());
		}
	}
	
	@Ignore
	@Test
	public void testSaveOrUpdateClientPOC(){
		ExternalAccount client;
		ClientPoint poc;
		ClientPointAccount clientPOC;
		
		client = readClientWithNoPOC();
		poc = readClientPoint();
		
		clientPOC = clientPOCService.saveOrUpdateClientPOC(client, poc);
		
		assertNotNull("Client POC was not created for account " + client.toString(), clientPOC.getClientPointAccountId());	
	}
	
	@Test
	public void testSaveOrUpdateClientPOCDelete(){
		ClientPointAccount clientPOC;
		
		clientPOC = readClientPOCWithCostCenter();
		clientPOCService.saveOrUpdateClientPOC(clientPOC, ClientPOCService.SAVE_MODE_DELETE);
		clientPOC = clientPOCService.getClientPOC(clientPOC.getClientPointAccountId());
		
		assertNull("Client POC was not removed cpntaId=", clientPOC);
	}
	
	@Test
	public void testSaveOrUpdateClientPOCContact(){
		PointOfCommunicationVO pocVO;
		ClientPointAccount clientPOC;
		List<ClientContactVO> setupContactVOs;
		boolean success = true;
		
		clientPOC = readClientPOCWithCostCenter();
		pocVO = clientPOCService.getPointOfCommunicationVO(clientPOC.getClientPointAccountId());
		
		setupContactVOs = clientPOCService.getSetupClientContactVOs(clientPOC, 
				clientPOC.getClientContacts().get(0).getCostCentreCode(), page, null);
		for(ClientContactVO contactVO : setupContactVOs){
			if(!contactVO.isAssigned()){
				contactVO.setContactMethodEmail(true);
				contactVO.setContactMethodMail(true);
				contactVO.setContactMethodPhone(true);				
				clientPOCService.saveOrUpdateClientPOCContact(pocVO, 
						clientPOC.getClientContacts().get(0).getCostCentreCode(), contactVO, ClientPOCService.SAVE_MODE_ADD);
				em.clear();
			} 
		}
		
		//clientPOC.setLastClientContactUpdate(Calendar.getInstance().getTime());
		//clientPOC = clientPOCService.saveOrUpdateClientPOC(clientPOC, ClientPOCService.SAVE_MODE_UPDATE);
		
		setupContactVOs = clientPOCService.getSetupClientContactVOs(clientPOC, 
				clientPOC.getClientContacts().get(0).getCostCentreCode(), page, null);		
		for(ClientContactVO contactVO : setupContactVOs){
			if(!contactVO.isAssigned()){
				success = false;	
				break;
			} 
		}				
		
		assertTrue("Contact could not be assigned to client's POC cpntaId=" + clientPOC.getClientPointAccountId(), success);
		
	}	
	
	private ClientPoint getClientPoint(Long clientPointId){
		return clientPOCService.getClientPoint(clientPointId);		
	}
	
	private ClientPointAccount getClientPointAccount(Long clientPointAccountId){
		return clientPOCService.getClientPOC(clientPointAccountId);		
	}
	
	private ClientContact readClientContactAsPOCDriver(){
		ClientContact driverContact;		
		List<Object[]> clientContacts;
		
		Query query = em.createNativeQuery(TestQueryConstants.READ_CLIENT_CONTACT_AS_POC_DRIVER);			
		clientContacts= query.getResultList();
		
		if(clientContacts.size() > 0){
			driverContact = clientContactDAO.findById(((BigDecimal)clientContacts.get(0)[0]).longValue()).orElse(null);
		} else {
			driverContact = null;
		}
		
		return driverContact;
	}
	
	private ClientContact readClientContactAsCostCenterDriver(){
		ClientContact driverContact;		
		List<Object[]> clientContacts;
		
		Query query = em.createNativeQuery(TestQueryConstants.READ_CLIENT_CONTACT_AS_COST_CENTER_DRIVER);			
		clientContacts= query.getResultList();
		
		if(clientContacts.size() > 0){
			driverContact = clientContactDAO.findById(((BigDecimal)clientContacts.get(0)[0]).longValue()).orElse(null);
		} else {
			driverContact = null;
		}
		
		return driverContact;
	}
	
	private ClientPointAccount readClientPOCWithCostCenter(){
		ClientPointAccount clientPOC;
		List<Object[]> clientPOCs;
		
		Query query = em.createNativeQuery(TestQueryConstants.READ_CLIENT_POINT_ACCOUNT_WITH_COST_CENTER);			
		clientPOCs= query.getResultList();
		
		if(clientPOCs.size() > 0){
			clientPOC = clientPOCService.getClientPOC(((BigDecimal)clientPOCs.get(0)[0]).longValue());
		} else {
			clientPOC = null;
		}
		
		return clientPOC;
	}
	
	private ExternalAccount readClientWithNoPOC(){
		Query query;
		String accountCode;
		List<ExternalAccount> accounts;
		
		query = em.createNativeQuery(TestQueryConstants.READ_ACCOUNT_WITH_NO_POC);
		accountCode = (String)query.getSingleResult();
		
		accounts = customerAccountService.findOpenCustomerAccountsByCode(accountCode, CorporateEntity.MAL);
		
		return accounts.get(0);
	}
	
	private ClientPoint readClientPoint(){
		Query query;
		Long clientPointId;
		ClientPoint poc;
		
		query = em.createNativeQuery(TestQueryConstants.READ_CLIENT_POINT_ID);
		clientPointId = ((BigDecimal)query.getSingleResult()).longValue();
		poc = clientPOCService.getClientPoint(clientPointId);
		
		return poc;
	}
}

