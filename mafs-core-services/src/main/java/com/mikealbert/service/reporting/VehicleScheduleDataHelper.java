package com.mikealbert.service.reporting;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.swing.table.DefaultTableModel;

import org.springframework.stereotype.Component;

import com.mikealbert.data.dao.ContractReportLanguageDAO;
import com.mikealbert.data.dao.MaintenancePreferencesDAO;
import com.mikealbert.data.entity.ContractLine;
import com.mikealbert.data.entity.ContractReportLanguage;
import com.mikealbert.data.entity.Driver;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.MaintSchedulesProcessed;
import com.mikealbert.data.entity.MaintenancePreferenceAccount;
import com.mikealbert.data.vo.ClientContactVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;
import com.mikealbert.service.ContractService;
import com.mikealbert.service.MaintenanceScheduleService;
import com.mikealbert.service.QuotationService;
import com.mikealbert.service.vo.MaintenanceScheduleIntervalPrintVO;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.util.ObjectUtils;

@Component
public class VehicleScheduleDataHelper {
	@Resource
	QuotationService quoteService;
	@Resource
	ContractService contractService;
	@Resource
	MaintenanceScheduleService maintenanceScheduleService;
	@Resource
	ContractReportLanguageDAO contractReportLanguageDAO;
	@Resource
	MaintenancePreferencesDAO maintenancePreferencesDAO;
	
	public HashMap<String, String> getVehicleSchedReportParams(MaintSchedulesProcessed maintSchedule) throws MalBusinessException {
		HashMap<String, String> retVal = new HashMap<String, String>();
		try {
			FleetMaster fleetMaster = maintSchedule.getFleetMaster();
			String sendToContactName = null;
			String sendToContactAddress = null;
			BigDecimal driverAuthorizationLimit = null;
			Date quoteDate = null;
			String driverText = null;
			String phoneNumber = null;
			String authorizationText = null;
			String driverNameText = null;
			
			String unitNumber = fleetMaster.getUnitNo();
			String fleetRefNumber = fleetMaster.getFleetReferenceNumber();
			String vin = fleetMaster.getVin();
			String driverAuthorizationNumber = "MAFS" + fleetMaster.getUnitNo();
			String unitDescription = (fleetMaster.getModel().getModelMarkYear().getModelMarkYearDesc() + " " + fleetMaster.getModel().getMake().getMakeDesc()
					+ " " + fleetMaster.getModel().getMakeModelRange().getDescription());
			ContractLine contractLine = contractService.getCurrentOrFutureContractLine(fleetMaster);
			contractLine = contractService.getDriverOnContractLine(contractLine.getClnId());
			retVal.put("unitNumber", unitNumber);
			retVal.put("fleetRef", fleetRefNumber);
			retVal.put("vin", vin);
			retVal.put("unitDescription", unitDescription);
			retVal.put("authorizationNumber", driverAuthorizationNumber);

			if (contractLine != null) {
				ExternalAccount externalAccount = contractLine.getContract().getExternalAccount();
				ClientContactVO clientContact = maintenanceScheduleService.getAllClientContactVOsForSchedules(externalAccount, fleetMaster);
				if (clientContact != null) {
					StringBuilder formattedName;
					formattedName = new StringBuilder();
					formattedName.append(MALUtilities.isEmpty(clientContact.getFirstName()) ? "" : clientContact.getFirstName());
					formattedName.append(MALUtilities.isEmpty(clientContact.getLastName()) ? "" : " " + clientContact.getLastName());
					sendToContactName = formattedName.toString();
					sendToContactAddress = clientContact.getAddressDisplay();
					if(!MALUtilities.isEmpty(sendToContactAddress)){
						List<String> addressList = new ArrayList<String>();
						if(!MALUtilities.isEmpty(clientContact.getBusinessAddressLine())) {
							addressList.add(clientContact.getBusinessAddressLine());
						}
						if(!MALUtilities.isEmpty(clientContact.getAddressLine1())) {
							addressList.add(clientContact.getAddressLine1());
						}
						if(!MALUtilities.isEmpty(clientContact.getAddressLine2())) {
							addressList.add(clientContact.getAddressLine2());
						}
						addressList.add(clientContact.getCity() + ", " + clientContact.getState() + "  " + clientContact.getPostCode());
						for(int i=1; i<=addressList.size(); i++) {
							retVal.put("sendToLine" + i, addressList.get(i-1));
						}
					}
				}
				retVal.put("accountName", externalAccount.getAccountName());
				retVal.put("accountCode", externalAccount.getExternalAccountPK().getAccountCode());
				ContractReportLanguage contractReportLanguage = contractReportLanguageDAO.findUSAText(new Date());
				Driver driver = contractLine.getDriver();
				if(driver != null) {
					driverNameText = "Attn: " + driver.getDriverForename() + " " + driver.getDriverSurname();
					if(!driver.getGaragedAddress().getCountry().getCountryCode().equalsIgnoreCase("USA")) {
						contractReportLanguage = contractReportLanguageDAO.findNonUSAText(new Date());
					}
				}
				if (contractReportLanguage != null) {
					authorizationText = contractReportLanguage.getCalculationText();
				}
				
				driverAuthorizationLimit = quoteService.getDriverAuthorizationLimit(externalAccount.getExternalAccountPK().getCId(), externalAccount
						.getExternalAccountPK().getAccountType(), externalAccount.getExternalAccountPK().getAccountCode(), fleetMaster.getUnitNo());
				
				if(driverAuthorizationLimit != null) {
					retVal.put("driverLimit", "$" + driverAuthorizationLimit.toString());	
				} else {
					retVal.put("driverLimit", "$0.00");
				}

				MaintenancePreferenceAccount maintenancePreferenceAccount = maintenancePreferencesDAO.findByAccount(externalAccount.getExternalAccountPK()
						.getCId(), externalAccount.getExternalAccountPK().getAccountCode(), externalAccount.getExternalAccountPK().getAccountType());
				if (maintenancePreferenceAccount != null) {
					driverText = maintenancePreferenceAccount.getClientDriverInstructions();
					if(!MALUtilities.isEmptyString(maintenancePreferenceAccount.getClientDedicatedPhone())){
						phoneNumber = maintenancePreferenceAccount.getClientDedicatedPhone().replace("-", ".");
					}
				}
				if(MALUtilities.isEmpty(driverText)){
					//Confirmed with Joanne to use sysdate here
					quoteDate = new Date();
					contractReportLanguage = contractReportLanguageDAO.findDefaultDriverInstructions(quoteDate);
					if (contractReportLanguage != null) {
						driverText = contractReportLanguage.getCalculationText();
					}
				}
				
				retVal.put("driverText", driverText);
				retVal.put("phoneNumber", phoneNumber);
				retVal.put("sendToName", sendToContactName);
				retVal.put("authorizationText", authorizationText);
				retVal.put("driverNameText", driverNameText);

				
			}
		} catch (MalBusinessException e) {
			throw e;
			}
		catch(Exception ex){
			throw new MalBusinessException("generic.error.occured.while", new String[]{"getting Vehicle schedule report data" }, ex);
		}

		return retVal;
	}
	
	public String getSendToName(FleetMaster fleetMaster) throws MalException {
		try {
			ContractLine contractLine = contractService.getCurrentOrFutureContractLine(fleetMaster);
			contractLine = contractService.getDriverOnContractLine(contractLine.getClnId());
			if (contractLine != null) {
				ExternalAccount externalAccount = contractLine.getContract().getExternalAccount();
				ClientContactVO clientContact = maintenanceScheduleService.getAllClientContactVOsForSchedules(externalAccount, fleetMaster);
				StringBuilder formattedName;
				formattedName = new StringBuilder();
				formattedName.append(MALUtilities.isEmpty(clientContact.getFirstName()) ? "" : clientContact.getFirstName().trim());
				formattedName.append(MALUtilities.isEmpty(clientContact.getLastName()) ? "" : " " + clientContact.getLastName().trim());
				String sendToName = formattedName.toString().replaceAll("[^A-Za-z0-9 ]", "");
				Long sendToId = clientContact.getContactId()!= null ? clientContact.getContactId() : clientContact.getDriverId();
				return sendToId+"_"+sendToName;
			}
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while", new String[] { "getting Send To Name" }, ex);
		}
		return null;
	}
	
	
	public DefaultTableModel getHdrTableModelData(
			List<MaintenanceScheduleIntervalPrintVO> maintScheduleVOs) {
		
		MaintenanceScheduleIntervalPrintVO  maintScheduleVO = maintScheduleVOs.get(0);
    	String[] columnNames = {"COL1", "COL2", "COL3", "COL4", "COL5", "COL6", "COL7", "COL8", "COL9", "COL10", "COL11", "COL12"};
        //TODO: reorganize the VO to make it cleaner to work with.. we should source the report directly off
    	// of the VO
    	
    	//TODO: dynamic code to get this data out.
    	String [][] data = new String[1][12];
    	
    	//TODO: rework this and add a patterned method to object utils
    	for(int a=0; a<1; a++){
        	for(int b=0; b<12; b++){
        		data[a][b] = (String)ObjectUtils.getProperty(maintScheduleVO, "Column" + (b+1));
        	}
    	}
    	
        return new DefaultTableModel(data, columnNames);
	}

	public DefaultTableModel getDtlTableModelData(
			List<MaintenanceScheduleIntervalPrintVO> maintScheduleVOs) {
    	String[] columnNames = {"INT_DESC","AUTH_DESC","COL1", "COL2", "COL3", "COL4", "COL5", "COL6", "COL7", "COL8", "COL9", "COL10", "COL11", "COL12"};
    	
    	String [][] data = new String[maintScheduleVOs.size()-1][14];
    	    	
    	// start with second row
    	for(int a=1; a < maintScheduleVOs.size(); a++){
        	for(int b=0; b<14; b++){
        		if(b==0){
        			data[a-1][b] = maintScheduleVOs.get(a).getAuthorizationNumber();
        		}else if(b==1){
        			data[a-1][b] = maintScheduleVOs.get(a).getIntervalDescription();        			
        		}else{
        			data[a-1][b] = (String)ObjectUtils.getProperty(maintScheduleVOs.get(a), "Column" + (b-1));
        		}
        		
        	}
    	}

        return new DefaultTableModel(data, columnNames);
	}

}

