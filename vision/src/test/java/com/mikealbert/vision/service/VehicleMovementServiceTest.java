package com.mikealbert.vision.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.util.Calendar;
import java.util.Date;
import javax.annotation.Resource;
import org.junit.Test;
import com.mikealbert.data.dao.DiaryEntryDAO;
import com.mikealbert.data.dao.ExternalAccountDAO;
import com.mikealbert.data.dao.FleetMasterDAO;
import com.mikealbert.data.dao.PreCollectionCheckDAO;
import com.mikealbert.data.dao.QuotationModelDAO;
import com.mikealbert.data.dao.VehicleMovementAddrLinkDAO;
import com.mikealbert.data.dao.VehicleMovementContactDAO;
import com.mikealbert.data.dao.VehicleMovementDAO;
import com.mikealbert.data.dao.VehicleMovementNoteDAO;
import com.mikealbert.data.entity.ContractLine;
import com.mikealbert.data.entity.DiaryEntry;
import com.mikealbert.data.entity.Driver;
import com.mikealbert.data.entity.ExtAccAddress;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.ExternalAccountPK;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.PreCollectionCheck;
import com.mikealbert.data.entity.VehicleMovement;
import com.mikealbert.data.entity.VehicleMovementAddrLink;
import com.mikealbert.data.entity.VehicleMovementContact;
import com.mikealbert.data.entity.VehicleMovementNote;
import com.mikealbert.service.ContractService;
import com.mikealbert.service.DriverService;
import com.mikealbert.service.UpfitterService;
import com.mikealbert.service.WillowConfigService;
import com.mikealbert.testing.BaseTest;
import com.mikealbert.util.MALUtilities;

public class VehicleMovementServiceTest extends BaseTest {
	@Resource VehicleMovementService vehicleMovementService;
	@Resource UpfitterService upfitterService;
	@Resource WillowConfigService willowConfigService;
	@Resource ContractService contractService;
	@Resource DriverService driverService;
	@Resource FleetMasterDAO fleetMasterDAO;
	@Resource VehicleMovementAddrLinkDAO vehicleMovementAddrLinkDAO;
	@Resource PreCollectionCheckDAO preCollectionCheckDAO;
	@Resource VehicleMovementDAO vehicleMovementDAO;
	@Resource VehicleMovementNoteDAO vehicleMovementNoteDAO;
	@Resource VehicleMovementContactDAO vehicleMovementContactDAO;
	@Resource QuotationModelDAO quotationModelDAO;
	@Resource DiaryEntryDAO diaryEntryDAO;
	@Resource ExternalAccountDAO externalAccountDAO;
	

	final static String departureType = "EAA";
	final static String dealerAccountCode = "00052970"; //Mike Albert Door to Door Delivery
	final static String accountCode = "Tech Electronics, Inc."; //Tech Electronics, Inc.
	final static Long replacementFmsId = 970889L; //Unit No - 00943899
	final static String tranTypeConfigValue = "TRANSPORT";
	final static String transPriorityConfigValue = "NORMAL";
	final static String vehicleReturnReson = "EOL";
	final static String transportReason = "EOL";
	final static String nonRunningCode = "MISC";
	final static String loggedInUserCode = "KUMAR_RA";
	final static String noteType = "NRN";
	final static String mileageNotes = "ODOMeter - 12000";
	final static String contactName = "Test Contact";
	final static String contactPhone = "111-222-3333";
	final static String diaryNote = "A Pre Collection Check has occurred and an Early Termination is required";
	final static String diaryDesc = "A Pre Collection Check has occurred and an Early Termination is required";

	@Test
	public void testCreateVehicleMovement() {
		boolean openTransportFound = vehicleMovementService.checkOpenVehicleMovementByFmsId(replacementFmsId);
		boolean createNew = true;
		if(openTransportFound) {
			System.out.println("transport already exists");
			createNew = false;
		}
		
		VehicleMovementAddrLink movedFrom = null;
		PreCollectionCheck preCollectionCheck = null;
		VehicleMovement vehicleMovement = null;
		VehicleMovementNote vehicleMovementNote = null;
		VehicleMovementContact vehicleMovementContact = null;
		DiaryEntry diary = null;
		if(createNew) {
			System.out.println("New transport save process test started");
			if(departureType.equals("EAA")) {
				ExtAccAddress extAccAddress = upfitterService.getUpfitterDefaultPostAddress(dealerAccountCode, 1L);

				// Check and save VehicleMovementAddrLink if not found
				if(!MALUtilities.isEmpty(extAccAddress)) {
					movedFrom = vehicleMovementService.getVehicleMovementAddrLinkByExtAccAddressId(extAccAddress.getEaaId());

					if(MALUtilities.isEmpty(movedFrom)) {
						movedFrom = new VehicleMovementAddrLink();
						movedFrom.setExtAccAddress(extAccAddress);
						movedFrom = vehicleMovementAddrLinkDAO.saveAndFlush(movedFrom);
					}
				}
			}
			System.out.println("vehicleMovementAddrLink Id: "+movedFrom.getVmalId());
			
			// Save pre_collection_checks
			FleetMaster replacementFms = fleetMasterDAO.findById(replacementFmsId).orElse(null);
			preCollectionCheck = new PreCollectionCheck();
			preCollectionCheck.setFleetMaster(replacementFms);
			preCollectionCheck.setPccDate(Calendar.getInstance().getTime());
			preCollectionCheck.setVrrVehicleReturnReason(vehicleReturnReson);
			preCollectionCheck = preCollectionCheckDAO.save(preCollectionCheck);
			System.out.println("preCollectionCheck Id: "+preCollectionCheck.getPccId());
			
			// Save Vehicle_movement record
			Date custReqdDate = Calendar.getInstance().getTime();
			vehicleMovement = new VehicleMovement();
			vehicleMovement.setVehTranTypeCode(tranTypeConfigValue);
			vehicleMovement.setVehStatusCode("OPEN");
			vehicleMovement.setMovedFrom(movedFrom);
			vehicleMovement.setFleetMaster(replacementFms);
			vehicleMovement.setEmployeeNo(loggedInUserCode);
			vehicleMovement.setDateCreated(Calendar.getInstance().getTime());
			vehicleMovement.setTransPriority(transPriorityConfigValue);
			vehicleMovement.setTransportReason(transportReason);
			vehicleMovement.setPreCollectionCheck(preCollectionCheck);
			vehicleMovement.setNonRunningCode(nonRunningCode);
			vehicleMovement.setGroupInd("N");
			vehicleMovement.setCustomerRequiredDate(custReqdDate);
			vehicleMovement.setLocalInd("N");
			vehicleMovement = vehicleMovementDAO.save(vehicleMovement);
			System.out.println("vehicleMovement Id: "+vehicleMovement.getVmovId());
			
			// Save vehicle_movement_notes record
			vehicleMovementNote = new VehicleMovementNote();
			vehicleMovementNote.setVehicleMovement(vehicleMovement);
			vehicleMovementNote.setNoteType(noteType);
			vehicleMovementNote.setNotes(mileageNotes);
			vehicleMovementNote.setEnteredBy(loggedInUserCode);
			vehicleMovementNote.setDateEntered(Calendar.getInstance().getTime());
			vehicleMovementNote.setNonRunningNnoteInd("Y");
			vehicleMovementNote = vehicleMovementNoteDAO.save(vehicleMovementNote);
			System.out.println("vehicleMovementNote Id: "+vehicleMovementNote.getVmnId());
			
			vehicleMovementContact = new VehicleMovementContact();
			vehicleMovementContact.setVehicleMovement(vehicleMovement);
			vehicleMovementContact.setMovedFrom(movedFrom);
			vehicleMovementContact.setContactName(contactName);
			vehicleMovementContact.setContactNumber(contactPhone);
			vehicleMovementContact = vehicleMovementContactDAO.save(vehicleMovementContact);
			System.out.println("vehicleMovementContact Id: "+vehicleMovementContact.getVmcId());
			
			
			boolean createDiary = true;
			String productType = "";
			ContractLine activeContractLine = contractService.getLastActiveContractLine(replacementFms, Calendar.getInstance().getTime());
			
			System.out.println("activeContractLine Id: "+activeContractLine.getClnId());
			
			if(!MALUtilities.isEmpty(activeContractLine.getQuotationModel())) {
				System.out.println("activeContractLine.getQuotationModel().getQmdId() Id: "+activeContractLine.getQuotationModel().getQmdId());
				productType = quotationModelDAO.getProductType(activeContractLine.getQuotationModel().getQmdId());
			}
			
			if("C".equals(productType)) {
				Long qmdId = quotationModelDAO.getQutationModelForTransportByFmsId(replacementFmsId);
				if(qmdId.longValue() != 0) {
					createDiary = false;
				}
			}
			
			String employeeNo = "";
			if(createDiary) {
				String csRoleTypeConfigValue =  willowConfigService.getConfigValue("CS_ROLE_TYPE");
				employeeNo = vehicleMovementService.getExtAccConsultantEmployeeNo(1L, "C", accountCode, csRoleTypeConfigValue);
			}
			
			Date endDate = activeContractLine.getEndDate();
			String etDaysConfigValue =  willowConfigService.getConfigValue("DAYS_BEFORE_END_TO_TERM");
			int etDays = Integer.valueOf(etDaysConfigValue);
			Date updatedDate = MALUtilities.addDaysToDateTime(custReqdDate, etDays);
			
			if(endDate != null && endDate.compareTo(updatedDate) > 0) {
				diary = new DiaryEntry();
				diary.setEntryDate(Calendar.getInstance().getTime());
				diary.setEnteredBy(loggedInUserCode);
				diary.setDescription(diaryDesc);
				diary.setNote(diaryNote);
				ExternalAccountPK externalAccountPK = new ExternalAccountPK(1, "C", accountCode);
				ExternalAccount externalAccount = externalAccountDAO.findById(externalAccountPK).orElse(null);
				diary.setExternalAccount(externalAccount);
				diary.setFleetMaster(replacementFms);
				Driver driver = driverService.getActiveDriverForUnit(replacementFms);
				diary.setDriver(driver);
				diary.setEntryType("TA");
				diary.setActionDate(Calendar.getInstance().getTime());
				diary.setActionFor(employeeNo);
				diary = diaryEntryDAO.save(diary);
				
				System.out.println("diary Id: "+diary.getDryId());
			}
		}
		
		System.out.println("Done");
		
		assertTrue("vehicleMovement already exists", createNew);
		assertNotNull("vehicleMovementAddrLink was not saved successfully", movedFrom);
		assertNotNull("preCollectionCheck was not saved successfully", preCollectionCheck);
		assertNotNull("vehicleMovement was not saved successfully", vehicleMovement);
		assertNotNull("vehicleMovementNote was not saved successfully", vehicleMovementNote);
		assertNotNull("vehicleMovementContact was not saved successfully", vehicleMovementContact);
		assertNotNull("diary was not saved successfully", diary);
	}

}
