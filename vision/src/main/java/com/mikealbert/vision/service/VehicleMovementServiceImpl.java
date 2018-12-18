package com.mikealbert.vision.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.mikealbert.data.dao.ExtAccConsultantDAO;
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
import com.mikealbert.data.entity.ExtAccConsultant;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.ExternalAccountPK;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.PreCollectionCheck;
import com.mikealbert.data.entity.VehicleMovement;
import com.mikealbert.data.entity.VehicleMovementAddrLink;
import com.mikealbert.data.entity.VehicleMovementContact;
import com.mikealbert.data.entity.VehicleMovementNote;
import com.mikealbert.service.ContractService;
import com.mikealbert.service.DiaryService;
import com.mikealbert.service.DriverService;
import com.mikealbert.service.UpfitterService;
import com.mikealbert.service.WillowConfigService;
import com.mikealbert.util.MALUtilities;

@Service("vehicleMovementService")
@Transactional
public class VehicleMovementServiceImpl implements VehicleMovementService {

	 @Resource WillowConfigService willowConfigService;
	 @Resource UpfitterService upfitterService;
	 @Resource DiaryService diaryService;
	 @Resource DriverService driverService;
	 @Resource ContractService contractService;
	 @Resource VehicleMovementAddrLinkDAO vehicleMovementAddrLinkDAO;
	 @Resource PreCollectionCheckDAO preCollectionCheckDAO;
	 @Resource ExtAccConsultantDAO extAccConsultantDAO;
	 @Resource VehicleMovementDAO vehicleMovementDAO;
	 @Resource VehicleMovementNoteDAO vehicleMovementNoteDAO;
	 @Resource VehicleMovementContactDAO vehicleMovementContactDAO;
	 @Resource FleetMasterDAO fleetMasterDAO;
	 @Resource QuotationModelDAO quotationModelDAO;
	 @Resource ExternalAccountDAO externalAccountDAO;
	 
	 public VehicleMovementAddrLink getVehicleMovementAddrLinkByExtAccAddressId(Long eaaId) {
		 VehicleMovementAddrLink vehicleMovementAddrLink = null;
		 List<VehicleMovementAddrLink> result = vehicleMovementAddrLinkDAO.findByExtAccountAddressId(eaaId);
		 
		 if(result != null && result.size() >0) {
			 vehicleMovementAddrLink = result.get(0);
		 }
		 return vehicleMovementAddrLink;
	 }
	 
	 public VehicleMovementAddrLink addVehicleMovementAddrLink(VehicleMovementAddrLink vehicleMovementAddrLink) {
		 return vehicleMovementAddrLinkDAO.save(vehicleMovementAddrLink);
	 }
	 
	 public PreCollectionCheck addPreCollectionCheck(PreCollectionCheck preCollectionCheck) {
		 return preCollectionCheckDAO.save(preCollectionCheck);
	 }
	 
	 public String getExtAccConsultantEmployeeNo(Long cId, String accountType, String accountCode, String csRoleType) {
		 String employeeNo = "";
		 ExtAccConsultant extAccConsultant = extAccConsultantDAO.findByExtAccountAndRoleType(cId, accountType, accountCode, csRoleType);
		 
		 if(!MALUtilities.isEmpty(extAccConsultant)) {
			 employeeNo = extAccConsultant.getEmployeeNo();
		 }
		 
		 return employeeNo;
	 }
	 
	 public VehicleMovement addVehicleMovement(VehicleMovement vehicleMovement) {
		 return vehicleMovementDAO.save(vehicleMovement);
	 }
	 
	 public VehicleMovementNote addVehicleMovementNote(VehicleMovementNote vehicleMovementNote) {
		 return vehicleMovementNoteDAO.save(vehicleMovementNote);
	 }
	 
	 public VehicleMovementContact addVehicleMovementContact(VehicleMovementContact vehicleMovementContact) {
		 return vehicleMovementContactDAO.save(vehicleMovementContact);
	 }
	 
	 public VehicleMovement getOpenTranportVehicleMovementByFmsId(Long fmsId) {
		 return vehicleMovementDAO.findOpenTranportByFmsId(fmsId);
	 }
	 
	 public boolean checkOpenVehicleMovementByFmsId(Long replacementFmsId) {
		 boolean openVmovFound = false;
		 List<VehicleMovement> vehicleMovementList = vehicleMovementDAO.findOpenVehicleMovementByFmsId(replacementFmsId);
		 
		 if(vehicleMovementList != null && vehicleMovementList.size() > 0) {
			 openVmovFound = true;
		 }
			 
		 return openVmovFound;
	 }
	 
	 /**
	 * Method to save automatic transport which is done manually from Pre Collection(DIT10) Tab in PO005
	 * Creates a Vehicle_Movement record with "OPEN" status which can be processed further form DIT02 or DIT13 
	 * 
	 * @param departureType - "Source" field on DIT10 
	 * @param dealerAccountCode - "Account" field on DIT10 
	 * @param accountCode - Customer account code
	 * @param replacementFmsId - FMS_ID of unit for which "Transport" is being created
	 * @param tranTypeConfigValue - Value form willow_config
	 * @param transPriorityConfigValue - Value form willow_config
	 * @param vehicleReturnReson - "Return Reason" field on DIT10
	 * @param transportReason - "Transport Reason" field on DIT10
	 * @param nonRunningCode - "Non Run Reason" field on DIT10
	 * @param loggedInUserCode - current logged in user EmployeeNo
	 * @param noteType - Note Type used to create VEHICLE_MOVEMENT_NOTES record
	 * @param mileageNotes - "Non running Note" field on DIT10
	 * @param contactName - Used to save VEH_MOVEMENT_CONTACTS record
	 * @param contactPhone - Used to save VEH_MOVEMENT_CONTACTS record
	 * @param diaryNote - Used to save DIARIES record
	 * @param diaryDesc - Used to save DIARIES record
	 */
	 @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	 public void createTranport(String departureType, String dealerAccountCode, String accountCode, Long replacementFmsId, String tranTypeConfigValue, String transPriorityConfigValue, String vehicleReturnReson, String transportReason, String nonRunningCode, 
				String loggedInUserCode, String noteType, String mileageNotes, String contactName, String contactPhone, String diaryNote, String diaryDesc) {
			
			VehicleMovementAddrLink movedFrom = null;
			if(departureType.equals("EAA")) {
				ExtAccAddress extAccAddress = upfitterService.getUpfitterDefaultPostAddress(dealerAccountCode, 1L);
				
				//Check and save VehicleMovementAddrLink if not found
				if(!MALUtilities.isEmpty(extAccAddress)) {
					movedFrom = getVehicleMovementAddrLinkByExtAccAddressId(extAccAddress.getEaaId());
					
					if(MALUtilities.isEmpty(movedFrom)) {
						movedFrom = new VehicleMovementAddrLink();
						movedFrom.setExtAccAddress(extAccAddress);
						movedFrom = addVehicleMovementAddrLink(movedFrom);
					}
				}
			}
			
			//Save pre_collection_checks
			FleetMaster replacementFms = fleetMasterDAO.findById(replacementFmsId).orElse(null);
			PreCollectionCheck preCollectionCheck = new PreCollectionCheck();
			preCollectionCheck.setFleetMaster(replacementFms);
			preCollectionCheck.setPccDate(Calendar.getInstance().getTime());
			preCollectionCheck.setVrrVehicleReturnReason(vehicleReturnReson);
			preCollectionCheck = addPreCollectionCheck(preCollectionCheck);
			
			//Save Vehicle_movement record
			Date custReqdDate = Calendar.getInstance().getTime();
			VehicleMovement vehicleMovement = new VehicleMovement();
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
			vehicleMovement = addVehicleMovement(vehicleMovement);
			
			//Save vehicle_movement_notes record
			if(!MALUtilities.isEmptyString(mileageNotes)) {
				VehicleMovementNote vehicleMovementNote = new VehicleMovementNote();
				vehicleMovementNote.setVehicleMovement(vehicleMovement);
				vehicleMovementNote.setNoteType(noteType);
				vehicleMovementNote.setNotes(mileageNotes);
				vehicleMovementNote.setEnteredBy(loggedInUserCode);
				vehicleMovementNote.setDateEntered(Calendar.getInstance().getTime());
				vehicleMovementNote.setNonRunningNnoteInd("Y");
				vehicleMovementNote = addVehicleMovementNote(vehicleMovementNote);
			}

			VehicleMovementContact vehicleMovementContact = new VehicleMovementContact();
			vehicleMovementContact.setVehicleMovement(vehicleMovement);
			vehicleMovementContact.setMovedFrom(movedFrom);
			vehicleMovementContact.setContactName(contactName);
			vehicleMovementContact.setContactNumber(contactPhone);
			vehicleMovementContact = addVehicleMovementContact(vehicleMovementContact);
			
			boolean employeeNoFromConfig = true;
			String productType = "";
			String employeeNo = "";
			ContractLine activeContractLine = contractService.getLastActiveContractLine(replacementFms, Calendar.getInstance().getTime());
			
			if(!MALUtilities.isEmpty(activeContractLine.getQuotationModel())) {
				productType = quotationModelDAO.getProductType(activeContractLine.getQuotationModel().getQmdId());
			}
			
			if("C".equals(productType)) {
				Long qmdId = quotationModelDAO.getQutationModelForTransportByFmsId(replacementFmsId);
				if(qmdId.longValue() != 0) {
					employeeNoFromConfig = false;
				}
			}
			
			if(employeeNoFromConfig) {
				String csRoleTypeConfigValue =  willowConfigService.getConfigValue("CS_ROLE_TYPE");
				employeeNo = getExtAccConsultantEmployeeNo(1L, "C", accountCode, csRoleTypeConfigValue);
			}
			
			Date endDate = activeContractLine.getEndDate();
			String etDaysConfigValue =  willowConfigService.getConfigValue("DAYS_BEFORE_END_TO_TERM");
			int etDays = Integer.valueOf(etDaysConfigValue);
			Date updatedDate = MALUtilities.addDaysToDateTime(custReqdDate, etDays);
			
			if(endDate != null && endDate.compareTo(updatedDate) > 0) {
				DiaryEntry diary = new DiaryEntry();
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
				diary = diaryService.addDiaryEntry(diary);
			}
		}
}
