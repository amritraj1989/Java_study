package com.mikealbert.service;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.mikealbert.data.entity.DiaryEntry;
import com.mikealbert.data.entity.Driver;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.data.entity.User;
import com.mikealbert.util.MALUtilities;

/**
 * Implementation of {@link com.mikealbert.vision.service.NotificationService}
 */
@Service("notificationService")
public class NotificationServiceImpl implements NotificationService {
	@Resource DiaryService diaryService;
	@Resource UserService userService;
	@Resource FleetMasterService fleetMasterService;
	@Resource DriverService driverService;
	
	private static final long serialVersionUID = 3733719745042460695L;
	
	// the group name for purchasing supervisors
	private static String ROLE_PURCHASE_SUPER = "ROLE_PURCHASE_SUPER";
	
	/**
	 * This procedure will notify purchasing via a diary action when a driver's
	 * address changes while a unit is 'On Order';
	 * Future use by CA results in this method assuming a MAL Corporate Entity;
	 * Used when saving a driver on Driver Add Edit screen
	 * @see com.mikealbert.vision.service.NotificationService#notifyPurchasingOfDriverAddressChgIfUnitOnOrder(long,String)
	 * @param driverId Driver's Primary Key
	 * @param userEmployeeNo User to notify
	 */

	public void notifyPurchasingOfDriverAddressChgIfUnitOnOrder(long driverId, String userEmployeeNo) {
		Driver driver = driverService.getDriver(driverId);
		// for each unit on order
		// NOTE: per the current business rules we want to include Stock/Inventory vehicles from this list that are in or complete with the GRD process
		List<String> unitNumbers = driverService.getOnOrderUnitNumbersByDriverId(driverId,false);
		for(String unitNumber : unitNumbers){
			FleetMaster fleetMaster = fleetMasterService.findByUnitNo(unitNumber);
			
			// for each member of purchasing
			List<User> purchasingMembers = userService.getUsersForRole(ROLE_PURCHASE_SUPER, CorporateEntity.MAL);
			for(User member : purchasingMembers){
				// add a diary entry
				// pass the logged in user
				diaryService.addDiaryEntry(createPurchaseNotificationEntry(userEmployeeNo,member.getEmployeeNo(),unitNumber,driver,fleetMaster));
			}
		}		
	}
	
	/**
	 * Creates the purchasing notification message.
	 * @param unitNumber Unit that is on order
	 * @param driverFirstName Driver allocated to the unit - First Name
	 * @param driverLastName Driver allocated to the unit - Last Name
	 * @return Purchasing notification message as String
	 */
	private String createPurchaseNotifyNote(String unitNumber, String driverFirstName, String driverLastName){
		String note = "Driver address change for " + driverLastName + ", " + driverFirstName + " on " + unitNumber + " unit .";
		
		return note;
	}
	
	/**
	 * This method sets up the diaryEntry entity.
	 * @param enteredBy Logged in User
	 * @param actionFor User to notify
	 * @param unitNumber Unit Number
	 * @param driver Driver
	 * @param fleetMaster Fleet Master
	 * @return Set up Diary Entry
	 */
	private DiaryEntry createPurchaseNotificationEntry(String enteredBy, String actionFor, String unitNumber, Driver driver, FleetMaster fleetMaster){
		DiaryEntry diaryEntry = new DiaryEntry();
		diaryEntry.setActionComplete("N");
		GregorianCalendar cal = new GregorianCalendar();
		cal.add(GregorianCalendar.MINUTE, 1);
		diaryEntry.setActionDate(cal.getTime());
		diaryEntry.setActionFor(actionFor);
		diaryEntry.setDescription(createPurchaseNotifyNote(unitNumber,driver.getDriverForename(),driver.getDriverSurname()));
		diaryEntry.setEnteredBy(enteredBy);
		diaryEntry.setEntryDate(new Date());
		diaryEntry.setEntryType("TA");
		diaryEntry.setNote(createPurchaseNotifyNote(unitNumber,driver.getDriverForename(),driver.getDriverSurname()));
		diaryEntry.setExternalAccount(driver.getExternalAccount());
		diaryEntry.setDriver(driver);
		if(!MALUtilities.isEmpty(fleetMaster)){
			diaryEntry.setFleetMaster(fleetMaster);
		}
		
		return diaryEntry;
	}

}
