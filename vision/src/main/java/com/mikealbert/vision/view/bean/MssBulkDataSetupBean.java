package com.mikealbert.vision.view.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mikealbert.data.dao.ClientScheduleTypeDAO;
import com.mikealbert.data.dao.FleetMasterDAO;
import com.mikealbert.data.dao.MasterScheduleDAO;
import com.mikealbert.data.dao.ServiceTaskDAO;
import com.mikealbert.data.dao.VehicleScheduleDAO;
import com.mikealbert.data.entity.ClientScheduleType;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.MasterSchedule;
import com.mikealbert.data.entity.MasterScheduleInterval;
import com.mikealbert.data.entity.ServiceTask;
import com.mikealbert.data.entity.VehicleSchedule;
import com.mikealbert.util.MALUtilities;

@Component
@Scope("view")
public class MssBulkDataSetupBean extends StatefulBaseBean {

	private static final long serialVersionUID = 1L;

	@Resource
	private MasterScheduleDAO masterScheduleDAO;
	@Resource
	private ServiceTaskDAO serviceTaskDAO;
	@Resource
	private VehicleScheduleDAO vehicleScheduleDAO;
	@Resource
	private FleetMasterDAO fleetMasterDAO;
	
	@Resource
	private	ClientScheduleTypeDAO	clientScheduleTypeDAO;

	private Integer scheduleInsertCount = 10;
	private String masterScheduleDesc = "Automated Test";
	private String masterCode;
	private Integer monthFrequency;
	private Integer distanceFrequency;
	
	private Integer vehScheduleInsertCount = 10;
	private String unitNumber;
	private Date activeFrom;
	private Date activeTo;
	private String masterCodeToVehSch;

	@Override
	protected void loadNewPage() {
		super.thisPage.setPageDisplayName("Mss Data Setup");
		super.thisPage.setPageUrl("/view/maintSchedules/mssBulkDataSetup");
		Map<String, Object> map = super.thisPage.getInputValues();
	}

	@Override
	protected void restoreOldPage() {
		Map<String, Object> map = super.thisPage.getRestoreStateValues();

	}

	public void addMasterScheduleData() {
		try {
			if (MALUtilities.isEmpty(masterCode)) {
				addErrorMessage("required.field", "Master Code");
				return;
			}
			if (MALUtilities.isEmpty(masterScheduleDesc)) {
				addErrorMessage("required.field", "Master Schedule Description");
				return;
			}
			if (MALUtilities.isEmpty(scheduleInsertCount)) {
				addErrorMessage("required.field", "Number of records");
				return;
			}
			if (MALUtilities.isEmpty(monthFrequency)) {
				addErrorMessage("required.field", "Month Frequency");
				return;
			}
			if (MALUtilities.isEmpty(distanceFrequency)) {
				addErrorMessage("required.field", "Distance Frequency");
				return;
			}
			MasterSchedule masterSchedule = null;
			ClientScheduleType clientScheduleType = new ClientScheduleType();
			List<ClientScheduleType> csList = clientScheduleTypeDAO.findAll();
			if(csList != null && !csList.isEmpty()){
				clientScheduleType= csList.get(0);
			}else{
				addErrorMessage("plain.message", "No Schedule Type found in database");
			}
			
			MasterScheduleInterval masterScheduleInterval = null;
			//Get any existing service task from database
			List<ServiceTask>	ServiceTaskList	= serviceTaskDAO.findAll();
			ServiceTask serviceTask = null;
			if(ServiceTaskList != null && !ServiceTaskList.isEmpty()){
				for (ServiceTask st : ServiceTaskList) {
					if("Y".equals(st.getActiveFlag())){
						serviceTask = st;
						break;
					}
				}
				
			}
			if(serviceTask == null){
				addErrorMessage("plain.message", "No service task found in database");
			}

			List<MasterSchedule> masterSchedules = new ArrayList<MasterSchedule>();
			for (int i = 0; i < scheduleInsertCount; i++) {
				masterSchedule = new MasterSchedule();
				masterSchedule.setDescription(masterScheduleDesc);
				masterSchedule.setHiddenFlag("N");
				masterSchedule.setClientScheduleType(clientScheduleType);
				masterSchedule.setMonthFrequency(monthFrequency);
				masterSchedule.setDistanceFrequency(distanceFrequency);
				masterSchedule.setMasterCode(masterCode);
				for (int j = 0; j < 10; j++) {
					masterScheduleInterval = new MasterScheduleInterval();
					if (masterSchedule.getMasterScheduleIntervals() == null) {
						masterSchedule.setMasterScheduleIntervals(new ArrayList<MasterScheduleInterval>());
					}
					masterScheduleInterval.setMasterSchedule(masterSchedule);
					masterScheduleInterval.setInterval(5);
					masterScheduleInterval.setOrder(j + 1);
					masterScheduleInterval.setRepeatFlag("Y");
					masterScheduleInterval.setServiceTask(serviceTask);
					masterSchedule.getMasterScheduleIntervals().add(masterScheduleInterval);
					masterSchedules.add(masterSchedule);
				}
			}
			masterScheduleDAO.saveAll(masterSchedules);
			addSuccessMessage("process.success", " Add Master Schedule : " + masterScheduleDesc);
			masterScheduleDesc = null;
			monthFrequency = null;
			distanceFrequency = null;
			masterCode = null;

		} catch (Exception ex) {
			handleException("generic.error", new String[] { ex.getMessage() }, ex, null);
		}

	}

	public void deleteMasterScheduleData() {
		try {
			if (!MALUtilities.isEmpty(masterScheduleDesc)) {
				List<MasterSchedule> masterSchedules = masterScheduleDAO.getMasterScheduleByDescription(masterScheduleDesc);
				if (masterSchedules != null && !masterSchedules.isEmpty())
					masterScheduleDAO.deleteAll(masterSchedules);
				addSuccessMessage("process.success", " Delete Master Schedule : " + masterScheduleDesc);
				masterScheduleDesc = null;
			} else {
				addErrorMessage("required.field", "Master Schedule Description");
				return;

			}

		} catch (Exception ex) {
			handleException("generic.error", new String[] { ex.getMessage() }, ex, null);
		}

	}

	public void addVehileScheduleData() {
		try {
			if (MALUtilities.isEmpty(vehScheduleInsertCount)) {
				addErrorMessage("required.field", "Number of records");
				return;
			}
			if (MALUtilities.isEmpty(unitNumber)) {
				addErrorMessage("required.field", "Unit Number");
				return;
			}
			if (MALUtilities.isEmpty(masterCodeToVehSch)) {
				addErrorMessage("required.field", "Master Schedule Code");
				return;
			}
			
			if (MALUtilities.isEmpty(activeFrom)) {
				addErrorMessage("required.field", "Active From");
				return;
			}
			if (MALUtilities.isEmpty(activeTo)) {
				addErrorMessage("required.field", "Active To");
				return;
			}
			FleetMaster fleetMaster = fleetMasterDAO.findByUnitNo(unitNumber);
			if (fleetMaster == null) {
				addErrorMessage("notfound", "Unit Number");
				return;
			}
			List<MasterSchedule> masterSchedules = masterScheduleDAO.getMasterSchedulesByCode(masterCodeToVehSch);
			if (masterSchedules == null || masterSchedules.isEmpty()) {
				addErrorMessage("notfound", "Master Schedule");
				return;
			}
			VehicleSchedule vehicleSchedule = null;
			MasterSchedule masterSchedule = masterSchedules.get(0);
			List<VehicleSchedule> vehicleSchedules = new ArrayList<VehicleSchedule>();
			for (int i = 0; i < vehScheduleInsertCount; i++) {
				vehicleSchedule = new VehicleSchedule();
				vehicleSchedule.setActiveFrom(activeFrom);
				vehicleSchedule.setActiveTo(activeTo);
				vehicleSchedule.setFleetMaster(fleetMaster);
				vehicleSchedule.setMasterSchedule(masterSchedule);
				vehicleSchedules.add(vehicleSchedule);

			}
			vehicleScheduleDAO.saveAll(vehicleSchedules);
			addSuccessMessage("process.success", " Add Vehicle Master Schedule : " + unitNumber);
			masterCodeToVehSch = null;
			masterCode = null;
			activeFrom = null;
			activeTo	=	null;
		} catch (Exception ex) {
			handleException("generic.error", new String[] { ex.getMessage() }, ex, null);
		}
	}
	public void deleteVehicleScheduleData() {
		try {
			if (!MALUtilities.isEmpty(unitNumber)) {
				List<VehicleSchedule> vehicleSchedules = vehicleScheduleDAO.getVehSchdByUnitNumber(unitNumber);
				if(vehicleSchedules != null && !vehicleSchedules.isEmpty()){
					vehicleScheduleDAO.deleteAll(vehicleSchedules);
				}
				addSuccessMessage("process.success", " Delete Vehicle Schedule : " + unitNumber);
				masterScheduleDesc = null;
				unitNumber = null;
			} else {
				addErrorMessage("required.field", "Unit Number");
				return;

			}

		} catch (Exception ex) {
			handleException("generic.error", new String[] { ex.getMessage() }, ex, null);
		}

	}

	public Integer getScheduleInsertCount() {
		return scheduleInsertCount;
	}

	public void setScheduleInsertCount(Integer scheduleInsertCount) {
		this.scheduleInsertCount = scheduleInsertCount;
	}

	public Integer getMonthFrequency() {
		return monthFrequency;
	}

	public void setMonthFrequency(Integer monthFrequency) {
		this.monthFrequency = monthFrequency;
	}

	public Integer getDistanceFrequency() {
		return distanceFrequency;
	}

	public void setDistanceFrequency(Integer distanceFrequency) {
		this.distanceFrequency = distanceFrequency;
	}

	public String getMasterScheduleDesc() {
		return masterScheduleDesc;
	}

	public void setMasterScheduleDesc(String masterScheduleDesc) {
		this.masterScheduleDesc = masterScheduleDesc;
	}

	public String getMasterCode() {
		return masterCode;
	}

	public void setMasterCode(String masterCode) {
		this.masterCode = masterCode;
	}

	public String getUnitNumber() {
		return unitNumber;
	}

	public void setUnitNumber(String unitNumber) {
		this.unitNumber = unitNumber;
	}

	public Date getActiveFrom() {
		return activeFrom;
	}

	public void setActiveFrom(Date activeFrom) {
		this.activeFrom = activeFrom;
	}

	public Date getActiveTo() {
		return activeTo;
	}

	public void setActiveTo(Date activeTo) {
		this.activeTo = activeTo;
	}

	public String getMasterCodeToVehSch() {
		return masterCodeToVehSch;
	}

	public void setMasterCodeToVehSch(String masterCodeToVehSch) {
		this.masterCodeToVehSch = masterCodeToVehSch;
	}

	public Integer getVehScheduleInsertCount() {
		return vehScheduleInsertCount;
	}

	public void setVehScheduleInsertCount(Integer vehScheduleInsertCount) {
		this.vehScheduleInsertCount = vehScheduleInsertCount;
	}
	

}
