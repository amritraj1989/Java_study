package com.mikealbert.vision.view.bean;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.mikealbert.data.dao.FleetMasterDAO;
import com.mikealbert.data.dao.FuelTypeValuesDAO;
import com.mikealbert.data.entity.ContractLine;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.FleetMasterVinDetails;
import com.mikealbert.data.entity.FuelTypeValues;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.ContractService;
import com.mikealbert.service.MaintenanceScheduleService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.view.ViewConstants;

@Component
@Scope("view")
public class VinDetailsBean extends StatefulBaseBean {
	private static final long serialVersionUID = 8701265392053287208L;
	
	@Resource FleetMasterDAO fleetMasterDAO;
	@Resource ContractService contractService;
	@Resource FuelTypeValuesDAO fuelTypeValuesDAO;
	@Resource MaintenanceScheduleService maintenanceScheduleService;
	
	private FleetMasterVinDetails fleetMasterVinDetails;
	private List<FuelTypeValues> fuelTypeValues;	
	private ExternalAccount externalAccount;
	private String unit;
	
	private String FUEL_TYPE_UI_ID = "fuelType";	
	
	@PostConstruct
	public void init() {
		super.openPage();
		try {
			loadData();
		} catch (Exception ex) {
			logger.error(ex);
			super.addErrorMessage("generic.error", ex.getMessage());
		}
	}
	
	public void loadData() {
		try {
			FleetMaster fleetMaster = fleetMasterDAO.findByUnitNo(getUnit());
	
			if(!MALUtilities.isEmpty(fleetMaster.getVehicleVinDetails())){
				setFleetMasterVinDetails(fleetMaster.getVehicleVinDetails());
				ContractLine contractLine = null;
				contractLine = contractService.getLastActiveContractLine(fleetMaster, Calendar.getInstance().getTime());
				
				if(MALUtilities.isEmpty(contractLine)){
					contractLine = contractService.getPendingLiveContractLine(fleetMaster, Calendar.getInstance().getTime());
				}
				
				if(contractLine != null){
					setExternalAccount(contractLine.getContract().getExternalAccount());
				}
				
			    setFuelTypeValues(fuelTypeValuesDAO.findAll(new Sort(Sort.Direction.ASC, "fuelType")));
			}
		} catch (Exception e) {
			logger.error(e);
			 if(e  instanceof MalBusinessException){				 
				 super.addErrorMessage(e.getMessage()); 
			 }else{
				 super.addErrorMessage("generic.error.occured.while", " building screen.");
			 }
		}
	}
	
	private boolean validToSave() {
		boolean valid = true;
		
		if (fleetMasterVinDetails.getFuelType() == null){
			addErrorMessage(FUEL_TYPE_UI_ID, "required.field", "Fuel Type");
			valid = false;
		}
		return valid;
	}	
	
	public void save() {
		try {
			if(validToSave()) {
				maintenanceScheduleService.saveOrUpdateFleetMasterVinDetail(fleetMasterVinDetails);
				loadData();
				super.addSuccessMessage("process.success","Save Fuel Type");
			}
		} catch (Exception ex) {
			logger.error(ex);		
			super.addErrorMessage("generic.error", ex.getMessage());
		}
	}
	
	public String cancel(){
		return cancelPage(); 
	}	
	
	@Override
	protected void loadNewPage() {
		Map<String, Object> map = super.thisPage.getInputValues();
		thisPage.setPageUrl(ViewConstants.VIN_DETAILS);		
		this.setUnit((String) map.get(ViewConstants.VIEW_PARAM_UNIT_NO));
		thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_VIN_DETAILS);
	}

	@Override
	protected void restoreOldPage() {
		Map<String, Object> map = super.thisPage.getRestoreStateValues();
		setUnit((String) map.get(ViewConstants.VIEW_PARAM_UNIT_NO));
	}

	public FleetMasterVinDetails getFleetMasterVinDetails() {
		return fleetMasterVinDetails;
	}

	public void setFleetMasterVinDetails(FleetMasterVinDetails fleetMasterVinDetails) {
		this.fleetMasterVinDetails = fleetMasterVinDetails;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public ExternalAccount getExternalAccount() {
		return externalAccount;
	}

	public void setExternalAccount(ExternalAccount externalAccount) {
		this.externalAccount = externalAccount;
	}

	public List<FuelTypeValues> getFuelTypeValues() {
		return fuelTypeValues;
	}

	public void setFuelTypeValues(List<FuelTypeValues> fuelTypeValues) {
		this.fuelTypeValues = fuelTypeValues;
	}

}
