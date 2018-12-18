package com.mikealbert.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.mikealbert.common.MalConstants;
import com.mikealbert.data.DataConstants;
import com.mikealbert.data.dao.ClientContactDAO;
import com.mikealbert.data.dao.ClientMethodDAO;
import com.mikealbert.data.dao.ClientPointAccountDAO;
import com.mikealbert.data.dao.ClientPointDAO;
import com.mikealbert.data.dao.ClientRuleDAO;
import com.mikealbert.data.dao.ContactDAO;
import com.mikealbert.data.dao.CostCenterDAO;
import com.mikealbert.data.entity.ClientContact;
import com.mikealbert.data.entity.ClientContactMethod;
import com.mikealbert.data.entity.ClientMethod;
import com.mikealbert.data.entity.ClientPoint;
import com.mikealbert.data.entity.ClientPointAccount;
import com.mikealbert.data.entity.ClientPointRule;
import com.mikealbert.data.entity.ClientRule;
import com.mikealbert.data.entity.Contact;
import com.mikealbert.data.entity.CostCentreCode;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.vo.ClientContactVO;
import com.mikealbert.data.vo.CostCenterHierarchicalVO;
import com.mikealbert.data.vo.POCCostCenterVO;
import com.mikealbert.data.vo.PointOfCommunicationVO;
import com.mikealbert.exception.MalException;
import com.mikealbert.util.MALUtilities;

/**
 * Implementation of {@link com.mikealbert.ClientPOCService.service.PointOfContactService}
 */
@Service("clientPOC")
public class ClientPOCServiceImpl implements ClientPOCService {
	@Resource ClientPointDAO clientPointDAO;
	@Resource ClientPointAccountDAO clientPointAccountDAO;	
	@Resource ClientContactDAO clientContactDAO;
	@Resource ContactDAO contactDAO;
	@Resource ClientRuleDAO clientRuleDAO;
	@Resource ClientMethodDAO clientMethodDAO;
	@Resource CostCenterDAO costCenterDAO;
	
	private static final String POC_RULE_MULTIPLE_RECIPIENT = "MULTIPLE_RECIPIENT";	
	private static final String POC_RULE_SEND_TO_DRIVER = "SEND_TO_DRIVER";
	private static final String POC_RULE_SEND_TO_CONTACT = "SEND_TO_CONTACT";
	private static final String POC_RULE_DELIVERY_OVERRIDE_MAIL = "DELIVERY_OVERRIDE_MAIL";
	private static final String POC_RULE_DELIVERY_OVERRIDE_PHONE = "DELIVERY_OVERRIDE_PHONE";
	private static final String POC_RULE_DELIVERY_OVERRIDE_EMAIL = "DELIVERY_OVERRIDE_EMAIL";	
	private static final String POC_RULE_DEFAULT_TO_MAIL = "DEFAULT_TO_MAIL";
	private static final String POC_RULE_DEFAULT_TO_EMAIL = "DEFAULT_TO_EMAIL";
	private static final String POC_RULE_DEFAULT_TO_PHONE = "DEFAULT_TO_PHONE";
	private static final String POC_RULE_ADDRESS_REQUIRED = "ADDRESS_REQUIRED";
	private static final String POC_RULE_PHONE_REQUIRED = "PHONE_REQUIRED";
	private static final String POC_RULE_EMAIL_REQUIRED = "EMAIL_REQUIRED";
	private static final String POC_RULE_PO_BOX_ALLOWED = "PO_BOX_ALLOWED";
	private static final String POC_RULE_CONTACT_REQUIRED = "CONTACT_REQUIRED";
	
	private static final Map<String, String> SORT_BY_MAPPING = new HashMap<String, String>();
	static{
		SORT_BY_MAPPING.put(SORT_BY_NAME, DataConstants.CLIENT_CONTACTS_SORT_NAME);
		SORT_BY_MAPPING.put(SORT_BY_ACCOUNT, DataConstants.CLIENT_CONTACTS_SORT_ACCOUNT);
		SORT_BY_MAPPING.put(SORT_BY_PARENT_ACCOUNT, DataConstants.CLIENT_CONTACTS_SORT_PARENT_ACCOUNT);
	}	
	
	@Transactional
	public List<PointOfCommunicationVO> getPointOfCommunicationVOs(ExternalAccount clientAccount){
		List<ClientPoint> pocs;
		List<PointOfCommunicationVO> pocVOs;
		PointOfCommunicationVO pocVO;
		
		pocVOs = new ArrayList<PointOfCommunicationVO>();
	
		pocs = clientPointDAO.findAll();
		for(ClientPoint poc : pocs){	                        
			pocVO = new PointOfCommunicationVO();
			pocVO.setPoc(poc);
			
			pocVO.setClientPOC(clientPointAccountDAO.findByAccountCodePOC(clientAccount.getExternalAccountPK().getAccountCode(), clientAccount.getExternalAccountPK().getAccountType(), clientAccount.getExternalAccountPK().getCId(), poc.getClientPointId()));
			
			applyPOCRules(pocVO);
			
			pocVOs.add(pocVO);
		}
		
		return pocVOs;		
	}
	
	@Transactional
	public PointOfCommunicationVO getPointOfCommunicationVO(Long clientPointAccountId){
		PointOfCommunicationVO clientPOCVO;
				
		clientPOCVO = new PointOfCommunicationVO();
		clientPOCVO.setClientPOC(clientPointAccountDAO.findById(clientPointAccountId).orElse(null));
		clientPOCVO.setPoc(clientPOCVO.getClientPOC().getClientPoint());
		
		applyPOCRules(clientPOCVO);
		
		return clientPOCVO;
	}
	
	@Transactional
	public List<PointOfCommunicationVO> getPointOfCommunicationVOs(ExternalAccount clientAccount, Long clientSystemId){
		List<ClientPoint> pocs;
		List<PointOfCommunicationVO> pocVOs;
		PointOfCommunicationVO pocVO;
		
		pocVOs = new ArrayList<PointOfCommunicationVO>();
		
		//get only POC's that that are associated with the selectedClientSystem
		pocs = clientPointDAO.findByAccountCodeClientSystem(clientAccount.getExternalAccountPK().getAccountCode(), clientSystemId);
		
		for(ClientPoint poc : pocs){						
			pocVO = new PointOfCommunicationVO();			
			pocVO.setPoc(poc);
			
			pocVO.setClientPOC(clientPointAccountDAO.findByAccountCodePOC(clientAccount.getExternalAccountPK().getAccountCode(), clientAccount.getExternalAccountPK().getAccountType(), clientAccount.getExternalAccountPK().getCId(), poc.getClientPointId()));	
			
			applyPOCRules(pocVO);
			
			pocVOs.add(pocVO);
		}
		
		return pocVOs;	
	}
	
	/**
	 * Retrieves a list of Client Contact VOs based on the Client Contacts business logic.
	 * @param FleetMaster is neccessary to retrieve the driver's, if on is assigned, contact info
	 * @param ExternalAccount the client's account
	 * @param DriverCostCenter the driver's cost center TODO May be a problem in the scenario where driver is not available
	 */
	@Transactional(readOnly=true)
	public List<ClientContactVO> getClientContactVOs(FleetMaster fleetMaster, ExternalAccount account, CostCentreCode costCenter, String pocName, String system, boolean orderByContactType){
		List<ClientContactVO> contactVOs = null;

		try {
			contactVOs = clientContactDAO.getContactVOsByAccountPOC(fleetMaster, account, costCenter, pocName, system, orderByContactType);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return contactVOs;
	}
	
	@Transactional
	public ClientPoint getClientPoint(Long clientPointId){
		ClientPoint clientPoint = clientPointDAO.findById(clientPointId).orElse(null);
		return clientPoint;
	}
	
	@Transactional
	public ClientPointAccount getClientPOC(Long clientPointAccountId){
		ClientPointAccount clientPOC = clientPointAccountDAO.findById(clientPointAccountId).orElse(null);
		return clientPOC;
	}
	
	@Transactional(readOnly = true)
	public Contact getContact(Long contactId){
		return contactDAO.findById(contactId).orElse(null);
	}
	
	@Transactional(readOnly = true)
	public ClientContactVO getClientContactVO(PointOfCommunicationVO pocVO, Contact contact){
		ClientContactVO clientContactVO;
		
		clientContactVO = clientContactDAO.getContactVOByClientPOC(pocVO.getClientPOC(), contact);
		
		if(MALUtilities.isEmpty(clientContactVO)
				|| MALUtilities.isEmpty(clientContactVO.getContactId())){
			clientContactVO = new ClientContactVO();
			clientContactVO.setContactId(contact.getContactId());
			clientContactVO.setFirstName(contact.getFirstName());
			clientContactVO.setLastName(contact.getLastName());
			clientContactVO.setJobTitle(contact.getJobTitle());
			clientContactVO.setContactType(contact.getContactType());			
		}
		
		return clientContactVO;
	}
	
	@Transactional(readOnly = true)
	public List<ClientContactVO> getSetupClientContactVOs(ClientPointAccount clientPOC, CostCentreCode costCenter, Pageable page, Sort sort){
		List<ClientContactVO> clientContactVOs = null;
		StringBuilder message;		
		String ruleName;
		String ruleValue;
		ClientPoint clientPoint;
		
		clientContactVOs = clientContactDAO.getContactVOsByPOC(clientPOC, costCenter, page, sort);
		clientPoint = clientPointDAO.findById(clientPOC.getClientPoint().getClientPointId()).orElse(null);
		
		for(ClientContactVO contactVO : clientContactVOs){
			message = new StringBuilder();
			
			for(ClientPointRule clientPointRule : clientPoint.getClientPointRules()){
	    		ruleName = clientPointRule.getClientRule().getRuleName();
	    		ruleValue = clientPointRule.getRuleInd();    		
				
				if(ruleName.equals(POC_RULE_ADDRESS_REQUIRED)){
					if(MALUtilities.convertYNToBoolean(ruleValue) && !contactVO.isAddressAvaialble()){				
						message.append("Address");
					}
				}
				if(ruleName.equals(POC_RULE_PHONE_REQUIRED)){
					if(MALUtilities.convertYNToBoolean(ruleValue) && !contactVO.isPhoneAvailable()){				
						message.append(message.length() > 0 ? ", Phone Number" : " Phone Number");
					}
				}
				if(ruleName.equals(POC_RULE_EMAIL_REQUIRED)){ 
					if(MALUtilities.convertYNToBoolean(ruleValue) && !contactVO.isEmailAvailable()){				
						message.append(message.length() > 0 ? ", Email " : " Email");
					}
				}																	
			}					
			contactVO.setAssignable(message.length() > 0 ? false : true);							
			contactVO.setMessage(message.length() > 0 ? "Missing: " + message.toString().trim() : "");
			if(containsPOBox(contactVO.getAddressLine1())){
				contactVO.setPoBoxAvailable(true);
			} else if(containsPOBox(contactVO.getAddressLine2())){
				contactVO.setPoBoxAvailable(true);
			} else if(containsPOBox(contactVO.getGrdAddressLine1())){
				contactVO.setPoBoxAvailable(true);
			} else {
				contactVO.setPoBoxAvailable(containsPOBox(contactVO.getGrdAddressLine2()));
			}
			
			contactVO.setMarkAssigned(contactVO.isAssigned());
			contactVO.setContactMethodEmailMarked(contactVO.isContactMethodEmail());
			contactVO.setContactMethodMailMarked(contactVO.isContactMethodMail());
			contactVO.setContactMethodPhoneMarked(contactVO.isContactMethodPhone());
		}

		return clientContactVOs;		
	}
	
	public int getSetupClientContactVOsCount(ClientPointAccount poc, CostCentreCode costCenter){
		return clientContactDAO.getContactVOsByPOCCount(poc, costCenter);
	}
	
	private boolean containsPOBox(String address){

		if(!MALUtilities.isEmpty(address)){
			if (address.toUpperCase().startsWith("PO ")) return true;
			if (address.toUpperCase().startsWith("P O ")) return true;
			if (address.toUpperCase().startsWith("P.O. ")) return true;		
			if (address.toUpperCase().startsWith("BOX ")) return true;
			if (address.toUpperCase().startsWith("P.O.BOX ")) return true;
			if (address.toUpperCase().indexOf(" PO ") >= 0 ) return true;
			if (address.toUpperCase().indexOf(" P O ") >= 0 ) return true;
			if (address.toUpperCase().indexOf(" P.O. ") >= 0 ) return true;		
			if (address.toUpperCase().indexOf(" BOX ") >= 0 ) return true;
			if (address.toUpperCase().indexOf(" P.O.BOX ") >= 0 ) return true;
		}
		return false;
	}

	/**
	 * Retrieves the driver client contact VO that is directly assigned to 
	 * the passed in clientPOC or the clientPOC's Cost Center. 
	 * at the client POC level
	 * @param ClientPointAccount 
	 * @return ClientContactVO The driver client contact that is assigned to the POC or the POC's Cost Center
	 */
	public ClientContactVO getDriverClientContactVO(ClientPointAccount clientPOC, CostCentreCode costCenter){
		ClientContactVO driverClientContactVO = null;
		ClientContact driverClientContact;
		
		if(MALUtilities.isEmpty(costCenter)){
			driverClientContact = clientContactDAO.findDriverByClientPOC(clientPOC.getClientPointAccountId());
		} else {
			driverClientContact = clientContactDAO.findDriverByClientPOCCostCenter(clientPOC.getClientPointAccountId(), 
					costCenter.getCostCentreCodesPK().getCostCentreCode(), 
					costCenter.getCostCentreCodesPK().getEaCId(), 
					costCenter.getCostCentreCodesPK().getEaAccountType(), 
					costCenter.getCostCentreCodesPK().getEaAccountCode());
		}
		
		if(!MALUtilities.isEmpty(driverClientContact)){
			driverClientContactVO = new ClientContactVO();
			driverClientContactVO.setClientContactId(driverClientContact.getClientContactId());		
			driverClientContactVO.setDriver(true);
			driverClientContactVO.setClientContactId(driverClientContact.getClientContactId());
			
			if(!MALUtilities.isEmpty(costCenter)){
				driverClientContactVO.setCostCenterCode(driverClientContact.getCostCentreCode().getCostCentreCodesPK().getCostCentreCode());			
			}					
		}

		return driverClientContactVO;
	}
	
	/**
	 * Determines whether the a driver is assigned to the POC or Cost Center 
	 * @param ClientPointAccount The client's assigned point of communication
	 * @param CostCentreCode (Optional) The cost center
	 * @return boolean indicating whether a driver is assigned or not
	 */	
	public boolean isDriverAssigned(ClientPointAccount clientPOC, CostCentreCode costCenter){
		boolean isAssigned = false;
		
		if(MALUtilities.isEmpty(costCenter)){
			if(!clientPointAccountDAO.countDrivers(clientPOC.getClientPointAccountId()).equals(0L)){
				isAssigned = true;
			}			
		} else {
			if(!clientPointAccountDAO.countDrivers(clientPOC.getClientPointAccountId(), 
					costCenter.getCostCentreCodesPK().getCostCentreCode(), 
					Long.valueOf((costCenter.getCostCentreCodesPK().getEaCId())), 
					costCenter.getCostCentreCodesPK().getEaAccountType(), 
					costCenter.getCostCentreCodesPK().getEaAccountCode()).equals(0L)){
				isAssigned = true;
			}			
		}

		return isAssigned;		
	}
	
	public List<POCCostCenterVO> getSetupPOCCostCenterVOs(ClientPointAccount clientPOC, Pageable page){
		List<POCCostCenterVO> pocCostCenterVOs = new ArrayList<POCCostCenterVO>();
		List<CostCenterHierarchicalVO> costCenterHVOs;
		
		costCenterHVOs = costCenterDAO.findCostCenterHierarchicalVOByAccount(clientPOC.getExternalAccount(), page, null);
		for(CostCenterHierarchicalVO costCenterHVO : costCenterHVOs) {
			POCCostCenterVO pocCostCenterVO = new POCCostCenterVO();
			pocCostCenterVO.setLevel(costCenterHVO.getLevel());
			pocCostCenterVO.setCode(costCenterHVO.getCode());
			pocCostCenterVO.setDescription(costCenterHVO.getDescription());
			pocCostCenterVO.setParentCode(costCenterHVO.getParentCode());
			pocCostCenterVO.setClientPointAccountId(clientPOC.getClientPointAccountId());
			
			pocCostCenterVO.setClientContacts(
					clientContactDAO.findByCostCenter(
							clientPOC.getClientPointAccountId(), costCenterHVO.getCode(), costCenterHVO.getCorporateEntityId(), 
							costCenterHVO.getAccountType(), costCenterHVO.getAccountCode()));
			
			pocCostCenterVOs.add(pocCostCenterVO);
		}		
		
//		costCenters = costCenterDAO.findByAccount(clientPOC.getExternalAccount().getExternalAccountPK().getAccountCode(), 
//				clientPOC.getExternalAccount().getExternalAccountPK().getAccountType(), 
//				clientPOC.getExternalAccount().getExternalAccountPK().getCId(), page);
		
//		for(CostCentreCode ccc : costCenters) {
//			POCCostCenterVO pocCostCenterVO = new POCCostCenterVO();
//			pocCostCenterVO.setCode(ccc.getCostCentreCodesPK().getCostCentreCode());
//			pocCostCenterVO.setDescription(ccc.getDescription());
//			pocCostCenterVO.setClientPointAccountId(clientPOC.getClientPointAccountId());
//			
//			pocCostCenterVO.setClientContacts(
//					clientContactDAO.findByCostCenter(
//							clientPOC.getClientPointAccountId(), ccc.getCostCentreCodesPK().getCostCentreCode(),ccc.getCostCentreCodesPK().getEaCId(), 
//							ccc.getCostCentreCodesPK().getEaAccountType(), ccc.getCostCentreCodesPK().getEaAccountCode()));
//			
//			pocCostCenterVOs.add(pocCostCenterVO);
//		}
		
		return pocCostCenterVOs;
	}
	
	public int getSetupPOCCostCenterVOCount(ClientPointAccount clientPOC){
		return costCenterDAO.findByAccountCount(clientPOC.getExternalAccount().getExternalAccountPK().getAccountCode(), 
				clientPOC.getExternalAccount().getExternalAccountPK().getAccountType(), 
				clientPOC.getExternalAccount().getExternalAccountPK().getCId()).intValue();
	}
	
	
	/**
	 * Iterate through the list of POC contacts and determines the
	 * last update date as the max effective from date.
	 * @param clientPOC ClientPointAccount the client's POC
	 * @return Date The max effective from date in the client contact's list
	 */
	public Date getDateOfLastUpdate(ClientPointAccount clientPOC){
		Date date = null;
		
		if(!MALUtilities.isEmpty(clientPOC.getClientContacts()) 
				&& clientPOC.getClientContacts().size() > 0){
			
			Collections.sort(clientPOC.getClientContacts(), new Comparator<ClientContact>() { 
				public int compare(ClientContact c1, ClientContact c2) { 
					return c2.getEffectiveFrom().compareTo(c1.getEffectiveFrom()); 
				}
			});
			
			date = clientPOC.getClientContacts().get(0).getEffectiveFrom();
		}
		
		return date;
	}
	
	/**
	 * Iterate through the list of POC's cost center contacts and 
	 * determines the last update date as the max effective from date.
	 * @param clientPOC ClientPointAccount the client's POC
	 * @param cosCenter client's cost center
	 * @return Date The max effective from date in the client contact's list
	 */
	public Date getDateOfLastUpdate(ClientPointAccount clientPOC, String costCentercode){
		Date date = null;	
		
		if(!MALUtilities.isEmpty(clientPOC.getClientContacts())){
			//Sorting the list of distinct contracts on rev date in ASC order
			Collections.sort(clientPOC.getClientContacts(), new Comparator<ClientContact>() { 
				public int compare(ClientContact c1, ClientContact c2) { 
					return c2.getEffectiveFrom().compareTo(c1.getEffectiveFrom()); 
				}
			});
			
			for(ClientContact cc : clientPOC.getClientContacts()){
				if(!MALUtilities.isEmpty(cc.getCostCentreCode()) 
						&& cc.getCostCentreCode().getCostCentreCodesPK().getCostCentreCode().equals(costCentercode)){
					date = cc.getEffectiveFrom();
					break;
				}
			}			
		}		
			
		return date;		
	}
	
	@Transactional
	public ClientPointAccount saveOrUpdateClientPOC(ClientPointAccount clientPOC, int mode) throws MalException {
		ClientPointAccount clientPointAccount = null;
		
		try {
			if(mode == ClientPOCService.SAVE_MODE_DELETE){
				removeClientPOC(clientPOC);			
			} else {
				clientPointAccount = clientPointAccountDAO.saveAndFlush(clientPOC);
			}
		} catch (Exception e) {
			throw new MalException("generic.error.occured.while", 
					new String[] { "adding or removing the account's POC" }, e);
		}
		
		return clientPointAccount;
	}

	@Transactional
	public ClientPointAccount saveOrUpdateClientPOC(ExternalAccount account, ClientPoint clientPoint) throws MalException {
		ClientPoint poc;
		ClientPointAccount clientPOC;
		
		try {
			poc = clientPointDAO.findById(clientPoint.getClientPointId()).orElse(null);
			
			clientPOC = new ClientPointAccount();
			clientPOC.setClientPoint(poc);			
			clientPOC.setExternalAccount(account);

			clientPOC = clientPointAccountDAO.saveAndFlush(clientPOC);
		} catch (Exception e) {
			throw new MalException("generic.error.occured.while", 
					new String[] { "adding or removing the account's POC" }, e);
		}
		
		return clientPOC;
	}
	
	@Transactional
	public ClientContactVO saveOrUpdateClientPOCContact(PointOfCommunicationVO pocVO, CostCentreCode costCenter, ClientContactVO contactVO, int mode) throws MalException {
		ClientContactVO pocContactVO;
       
		try {
			pocContactVO = contactVO;
			
			if(mode == ClientPOCService.SAVE_MODE_ADD){
				if(pocContactVO.isDriver()){
					pocContactVO = addClientContactDriver(pocVO.getClientPOC(), costCenter, pocContactVO);
				} else {
					pocContactVO = applyDefaultDeliveryMethods(pocVO, pocContactVO);
					pocContactVO = addClientPOCContact(pocVO, costCenter, pocContactVO);
					pocContactVO = updateClientPOCContact(pocContactVO);
				}
				timeStampClientPOC(pocVO);
			} else if(mode == ClientPOCService.SAVE_MODE_UPDATE){
				pocContactVO = updateClientPOCContact(pocContactVO);		
				timeStampClientPOC(pocVO);
			}else {
				timeStampClientPOC(pocVO);
				pocContactVO = removeClientPOCContact(pocVO.getClientPOC(), pocContactVO);
			}
			
			
			
		} catch (Exception e) {
			throw new MalException("generic.error.occured.while", 
					new String[] { "assigning or unassigning contact" }, e);
		}
		
		return pocContactVO;
	}
	
	public String resolveSortByName(String columnName){
		return SORT_BY_MAPPING.get(columnName);		
	}	
	
	/**
	 * Initializes the PointOfCommunicationVO properties that represent the POC rules.
	 * @param pocVO PointOfCommunication VO
	 */
	private void applyPOCRules(PointOfCommunicationVO pocVO) {
		ClientRule rule;
		
		if(!MALUtilities.isEmpty(pocVO.getClientPOC())){
			for(ClientPointRule cpr : pocVO.getClientPOC().getClientPoint().getClientPointRules()){
				rule = cpr.getClientRule();			
				if(rule.getRuleName().equals(POC_RULE_MULTIPLE_RECIPIENT)){
					pocVO.setMultipleRecipientsAssignable(MALUtilities.convertYNToBoolean(cpr.getRuleInd()));					
				} else if(rule.getRuleName().equals(POC_RULE_SEND_TO_DRIVER)){
					pocVO.setDriverAssignable(MALUtilities.convertYNToBoolean(cpr.getRuleInd()));
				} else if (rule.getRuleName().equals(POC_RULE_SEND_TO_CONTACT)) {
					pocVO.setContactAssignable(MALUtilities.convertYNToBoolean(cpr.getRuleInd()));					
				} else if(rule.getRuleName().equals(POC_RULE_DELIVERY_OVERRIDE_MAIL)){
					pocVO.setDeliveryMethodMailUpdatable(MALUtilities.convertYNToBoolean(cpr.getRuleInd()));
				} else if(rule.getRuleName().equals(POC_RULE_DELIVERY_OVERRIDE_PHONE)){
					pocVO.setDeliveryMethodPhoneUpdatable(MALUtilities.convertYNToBoolean(cpr.getRuleInd()));
				} else if(rule.getRuleName().equals(POC_RULE_DELIVERY_OVERRIDE_EMAIL)){
					pocVO.setDeliveryMethodEmailUpdatable(MALUtilities.convertYNToBoolean(cpr.getRuleInd()));					
				} else if(rule.getRuleName().equals(POC_RULE_DEFAULT_TO_MAIL)){
					pocVO.setDefaultMail(MALUtilities.convertYNToBoolean(cpr.getRuleInd()));
				} else if(rule.getRuleName().equals(POC_RULE_DEFAULT_TO_EMAIL)){
					pocVO.setDefaultEmail(MALUtilities.convertYNToBoolean(cpr.getRuleInd()));
				} else if(rule.getRuleName().equals(POC_RULE_DEFAULT_TO_PHONE)){
					pocVO.setDefaultPhone(MALUtilities.convertYNToBoolean(cpr.getRuleInd()));
				} else if(rule.getRuleName().equals(POC_RULE_PO_BOX_ALLOWED)){
					pocVO.setPoBoxAllowed(MALUtilities.convertYNToBoolean(cpr.getRuleInd()));
				} else if(rule.getRuleName().equals(POC_RULE_CONTACT_REQUIRED)){
					pocVO.setContactRequired(MALUtilities.convertYNToBoolean(cpr.getRuleInd()));
				}
			}
		}
	}
	
	/**
	 * Initializes the contact's delivery methods based on the POC's rules
	 * @param pocVO PointOfCommunicationVO has properties that represents the POC rules
	 * @param contactVO ContactVO represents the contact and contains properties for delivery method 
	 * @return ContactVO with the delivery method properties initialized based on the POC rules
	 */
	private ClientContactVO applyDefaultDeliveryMethods(PointOfCommunicationVO pocVO, ClientContactVO contactVO){		
		if(pocVO.isDefaultMail()){
			contactVO.setContactMethodMail(true);
		}

		if(pocVO.isDefaultPhone()){
			contactVO.setContactMethodPhone(true);
		} 
		
		if(pocVO.isDefaultEmail()){
			contactVO.setContactMethodEmail(true); 			
		} 
			
		return contactVO;
	}	

	private ClientContactVO addClientPOCContact(PointOfCommunicationVO pocVO, CostCentreCode costCenter, ClientContactVO contactVO){
		ClientContact newPOCContact;
		ClientContactVO pocContactVO;
		Contact contact;		
		
		pocContactVO = contactVO;
		contact = contactDAO.findById(pocContactVO.getContactId()).orElse(null);
		
		newPOCContact = new ClientContact();
		newPOCContact.setClientPointAccount(pocVO.getClientPOC());
		newPOCContact.setDrvInd(MalConstants.FLAG_N);
		newPOCContact.setContact(contact);
		newPOCContact.setEffectiveFrom(Calendar.getInstance().getTime());
		
		if(!MALUtilities.isEmpty(costCenter)){			
			newPOCContact.setCostCentreCode(costCenter);
		}
			
		newPOCContact = clientContactDAO.saveAndFlush(newPOCContact);
		
		pocContactVO.setClientContactId(newPOCContact.getClientContactId());
				
		return pocContactVO;		
	}
	
	private ClientContactVO addClientContactDriver(ClientPointAccount clientPOC, CostCentreCode costCenter, ClientContactVO contactVO){
		ClientContact newPOCContact;
		ClientContactVO pocContactVO;		
		
		pocContactVO = contactVO;
		
		newPOCContact = new ClientContact();
		newPOCContact.setClientPointAccount(clientPOC);
		newPOCContact.setDrvInd(MalConstants.FLAG_Y);
		newPOCContact.setEffectiveFrom(Calendar.getInstance().getTime());
		
		if(!MALUtilities.isEmpty(costCenter)){
			newPOCContact.setCostCentreCode(costCenter);
		}
		
		newPOCContact = clientContactDAO.saveAndFlush(newPOCContact);
		
		pocContactVO.setClientContactId(newPOCContact.getClientContactId());
		
		return pocContactVO;			
	}
	
	private void removeClientPOC(ClientPointAccount clientPOC) {
		clientPointAccountDAO.deleteById(clientPOC.getClientPointAccountId());
	}

	
	private ClientContactVO removeClientPOCContact(ClientPointAccount clientPOC, ClientContactVO contactVO){
		ClientContactVO unassignedContactVO = contactVO;
		
		clientContactDAO.delete(clientContactDAO.findById(unassignedContactVO.getClientContactId()).orElse(null));
		
		unassignedContactVO.setAssigned(false);
		unassignedContactVO.setContactMethodMail(false);
		unassignedContactVO.setContactMethodEmail(false);
		unassignedContactVO.setContactMethodPhone(false);
		unassignedContactVO.setClientContactId(null);
		
		//TODO If POC does not have any contacts, remove its association from the account.
		
		return unassignedContactVO;
	}
	
	private ClientContactVO updateClientPOCContact(ClientContactVO pocContactVO){	
		ClientContact pocContact;
		ClientContactMethod clientContactMethod;
		ClientMethod clientMethod;		

		//Remove all contact methods from assigned to the POC contact
		pocContact = clientContactDAO.findById(pocContactVO.getClientContactId()).orElse(null);
		if(!MALUtilities.isEmpty(pocContact.getClientContactMethod())){
			pocContact.getClientContactMethod().removeAll(pocContact.getClientContactMethod());			
		} else {
			pocContact.setClientContactMethod(new ArrayList<ClientContactMethod>());			
		}
		
		//Add contact methods where they have been assigned
		if(pocContactVO.isContactMethodMail()){			
			clientMethod = clientMethodDAO.findByName("MAIL");
			clientContactMethod = new ClientContactMethod();
			clientContactMethod.setClientContact(pocContact);
			clientContactMethod.setClientMethod(clientMethod);
			pocContact.getClientContactMethod().add(clientContactMethod);
		}
		if(pocContactVO.isContactMethodPhone()){			
			clientMethod = clientMethodDAO.findByName("PHONE");
			clientContactMethod = new ClientContactMethod();
			clientContactMethod.setClientContact(pocContact);
			clientContactMethod.setClientMethod(clientMethod);
			pocContact.getClientContactMethod().add(clientContactMethod);			
		}
		if(pocContactVO.isContactMethodEmail()){			
			clientMethod = clientMethodDAO.findByName("EMAIL");
			clientContactMethod = new ClientContactMethod();
			clientContactMethod.setClientContact(pocContact);
			clientContactMethod.setClientMethod(clientMethod);
			pocContact.getClientContactMethod().add(clientContactMethod);			
		}		
				
		pocContact = clientContactDAO.saveAndFlush(pocContact);
		
		return pocContactVO;
	}
	
	private void timeStampClientPOC(PointOfCommunicationVO clientPOCVO){
		ClientPointAccount clientPOC;

		clientPOC = clientPointAccountDAO.findById(clientPOCVO.getClientPOC().getClientPointAccountId()).orElse(null);
		clientPOC.setLastClientContactUpdate(Calendar.getInstance().getTime());
		clientPointAccountDAO.saveAndFlush(clientPOC);
	}
	
	@Override
	public ClientContactVO getDefaultPOCContact(ExternalAccount externalAccount) {
		ClientContactVO clientContactVO = null;
		
		List<ClientPointAccount> clientPointAccounts = clientPointAccountDAO.findByAccountCode(externalAccount.getExternalAccountPK().getAccountCode(), externalAccount.getExternalAccountPK().getAccountType(), externalAccount.getExternalAccountPK().getCId());
		
		ClientPointAccount defaultPOC = null;
		for(ClientPointAccount clientPointAccount : clientPointAccounts) {
			if (clientPointAccount.getClientPoint().getName().contains("DEFAULT")){
    			defaultPOC = clientPointAccount;
    			break;
    		}
		}
		
		if(defaultPOC != null) {
			 List<ClientContact> clientContacts = defaultPOC.getClientContacts();
			 if(clientContacts != null && clientContacts.size() > 0) {
				 ClientContact clientContact = clientContacts.get(0);
				 if(clientContact.getDrvInd().equals("N")) {
					 clientContactVO = clientContactDAO.getContactVOByClientPOC(defaultPOC, clientContact.getContact());
				 }
			 }
		}
		return clientContactVO;
	}
	
}
