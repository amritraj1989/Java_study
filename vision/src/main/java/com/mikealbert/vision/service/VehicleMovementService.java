package com.mikealbert.vision.service;

import com.mikealbert.data.entity.PreCollectionCheck;
import com.mikealbert.data.entity.VehicleMovement;
import com.mikealbert.data.entity.VehicleMovementAddrLink;
import com.mikealbert.data.entity.VehicleMovementContact;
import com.mikealbert.data.entity.VehicleMovementNote;

/**
 * @author ravresh.kumar
 *
 */

public interface VehicleMovementService {	
	
	public VehicleMovementAddrLink getVehicleMovementAddrLinkByExtAccAddressId(Long eaaId);
	
	public VehicleMovementAddrLink addVehicleMovementAddrLink(VehicleMovementAddrLink vehicleMovementAddrLink);

	public PreCollectionCheck addPreCollectionCheck(PreCollectionCheck preCollectionCheck);

	public String getExtAccConsultantEmployeeNo(Long cId, String accountType, String accountCode, String csRoleType);

	public VehicleMovement addVehicleMovement(VehicleMovement vehicleMovement);

	public VehicleMovementNote addVehicleMovementNote(VehicleMovementNote vehicleMovementNote);

	public VehicleMovementContact addVehicleMovementContact(VehicleMovementContact vehicleMovementContact);
	
	public VehicleMovement getOpenTranportVehicleMovementByFmsId(Long fmsId);
	
	public void createTranport(String departureType, String dealerAccountCode, String accountCode, Long replacementFmsId, String tranTypeConfigValue, String transPriorityConfigValue, String vehicleReturnReson, String transportReason, String nonRunningCode, 
			String loggedInUserCode, String noteType, String mileageNotes, String contactName, String contactPhone, String diaryNote, String diaryDesc);

	public boolean checkOpenVehicleMovementByFmsId(Long replacementFmsId);

}
