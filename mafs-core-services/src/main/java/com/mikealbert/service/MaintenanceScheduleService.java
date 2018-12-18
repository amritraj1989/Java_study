package com.mikealbert.service;


import java.math.BigDecimal;
import java.util.List;

import com.mikealbert.data.entity.ClientScheduleType;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.FleetMasterVinDetails;
import com.mikealbert.data.entity.FuelGroupCode;
import com.mikealbert.data.entity.LeaseElement;
import com.mikealbert.data.entity.MaintScheduleRule;
import com.mikealbert.data.entity.MaintSchedulesProcessed;
import com.mikealbert.data.entity.MaintenanceCategory;
import com.mikealbert.data.entity.MasterSchedule;
import com.mikealbert.data.entity.ModelType;
import com.mikealbert.data.entity.ServiceTask;
import com.mikealbert.data.entity.VehicleSchedule;
import com.mikealbert.data.vo.ClientContactVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalDataValidationException;
import com.mikealbert.exception.MalException;
import com.mikealbert.service.vo.MaintenanceScheduleIntervalPrintVO;



public interface MaintenanceScheduleService {	
	
//	public String getFuelDescByFuelTypeGroup(String fuelType,String strategy);
	public List<FuelGroupCode> getFuelTypeGroupList();
	public List<ServiceTask> getTaskList();
	public List<MasterSchedule> getScheduleList();
	public int getScheduleCount(ServiceTask task);
	public List<MaintenanceCategory> getMaintenanceCategories();
	public void saveTask(ServiceTask task) throws Exception;
	public void saveRule(MaintScheduleRule rule) throws Exception;
	public void saveSchedule(MasterSchedule schedule) throws Exception;
	public ServiceTask getTask(Long taskId);
	public MaintenanceCategory getMaintenanceCategory(String code);
	public void deleteTask(ServiceTask task) throws Exception;
	public void deleteRule(MaintScheduleRule rule) throws Exception;
	public List<ServiceTask> getServiceTaskByCode(String code);
	public List<MasterSchedule> getMasterScheduleByCode(String code);
	public List<ClientScheduleType> getClientScheduleTypes();
	public ClientScheduleType getClientScheduleTypeByType(String type);
	public MasterSchedule getMasterSchedule(Long id);
	public List<MasterSchedule> getMasterSchedulesByInterval(int interval);
	public String getNextMasterScheduleCode(int interval);
	public Long getRulesByMasterIdCount(MasterSchedule ms);
	public List<ServiceTask> getTaskListByName();
	public void deleteMasterSchedule(MasterSchedule schedule) throws Exception;
	public List<MaintScheduleRule> getMaintScheduleRules();
	public ModelType getModelTypeByCode(Long code);
	public MasterSchedule copyMasterSchedule(MasterSchedule schedule);
	public MaintScheduleRule getMaintScheduleRule(Long id);
	public List<MasterSchedule> findActiveByScheduleTypeAndInterval(String scheduleType, int miles);
	public List<MasterSchedule> findActiveByScheduleType(String scheduleType);
	public List<MasterSchedule> getMasterSchedulesByScheduleType(String scheduleType,Long cId,String accountCode,String accountType, boolean hiddenFlag) ;
	public List<MasterSchedule> getMasterSchedulesByScheduleTypeAndInterval(String scheduleType, Long cId,String accountCode,String accountType,int miles, boolean hiddenFlag) ;
	public List<MaintScheduleRule> getMatchingRules(MaintScheduleRule rule,String scheduleType) ;
	public MaintenanceScheduleIntervalPrintVO getMasterSchedulePrintHeader(Long masterScheduleId);
	public List<MaintenanceScheduleIntervalPrintVO> getMasterSchedulePrint(Long masterScheduleId);
	public List<MaintenanceScheduleIntervalPrintVO> getMasterSchedulePrint(Long masterScheduleId, int startingInterval, BigDecimal conversionFactor);
	public List<MaintenanceScheduleIntervalPrintVO> getMasterScheduleIntervalList(Long masterScheduleId, int startingInterval, BigDecimal conversionFactor);
	public List<MaintenanceScheduleIntervalPrintVO> getMasterScheduleIntervalList(Long masterScheduleId, int startingInterval, BigDecimal conversionFactor, int requestedRows);
	public List<MaintenanceScheduleIntervalPrintVO> getMasterScheduleIntervalList(Long masterScheduleId, int startingInterval, BigDecimal conversionFactor, int requestedRows, boolean ignoreNonMileageIntervals);
	public VehicleSchedule	createVehicleSchedule(MasterSchedule masterSchedule, FleetMaster fleetMaster) throws MalException; 
	public List<MaintSchedulesProcessed> getMaintSchedulesForVehSchedAssignment();
	public List<MaintSchedulesProcessed> getMaintSchedulesForDetermineExpPrintDate();
	public List<MaintSchedulesProcessed> getMaintSchedulesQualifiedForPrint();
	public MaintSchedulesProcessed findProcessRecordByFmsId(Long id);
	public void saveScheduleProcessRecord(MaintSchedulesProcessed processRecord) throws MalException;
	public static final int DEFAULT_SCHEDULE_ROWS = 35;
	public static final int MAX_MILEAGE_TO_SEARCH_FOR_INTERVALS = 2000000;    // this is to prevent run away loops
	public	static final String FUEL_GROUP_KEY_MSS = "FUEL_GROUP_KEY_MSS";
	public static final BigDecimal CONVERSION_FACTOR_FOR_KILOMETERS = new BigDecimal(1.61);
	
	public MasterSchedule determineMasterScheduleByFmsId(Long fmsId) throws MalDataValidationException, MalBusinessException;
	public List<MaintScheduleRule> getMaintSchedRulesByActiveAndHighMileageAndClientSchedType(String activeFlag, String highMileage, Long clientScheduleId, ExternalAccount externalAccount);
	public MasterSchedule determineMasterSchedule(FleetMaster fleetmaster, List<MaintScheduleRule> maintScheduleRules, FuelGroupCode fuelGroupCode, String highMileage);
	public List<MaintSchedulesProcessed> getNextPrintList();
	public long getNextPrintListCount();
	public List<MaintSchedulesProcessed> getErrorList();
	public long getErrorListCount();
	public List<MaintSchedulesProcessed> getPreviousPrintList(long daysToGoBack);
	public long getPreviousPrintListCount(long daysToGoBack);
	public ClientContactVO getAllClientContactVOsForSchedules(ExternalAccount externalAccount, FleetMaster fleetMaster) throws Exception;
	public void setScheduleToPrintNext(MaintSchedulesProcessed schedule) throws MalException;
	public void deleteMaintSchedulesProcessed(MaintSchedulesProcessed maintScheduleProcessed) throws Exception;
	public MaintSchedulesProcessed determineExpectedPrintDate(MaintSchedulesProcessed processedRecord) throws MalDataValidationException, Exception;
	public static final String CLIENT_SCHEDULE_TYPE = "CLIENT";
	public int getInitialStartingInterval(FleetMaster fleetMaster, VehicleSchedule vehicleSchedule) throws MalBusinessException;
	public int getReprintStartingInterval(FleetMaster fleetMaster, VehicleSchedule vehicleSchedule) throws MalBusinessException ;
	public List<LeaseElement> getAllMaintenanceLeaseElements();
	public BigDecimal getConversionFactorForSchedule(MaintSchedulesProcessed maintSchedulesProcessed);
	public BigDecimal getMaxCostForInterval(String intervalCode);
	public List<ServiceTask> getServiceTaskListForInterval(String intervalCode);
	public List<ServiceTask> getServiceTasksForInterval(int intervalIndex, MasterSchedule masterSchedule);
	public BigDecimal getLimitForDriverAuthorizationCode(String authorizationCode) throws Exception;
	public void saveOrUpdateFleetMasterVinDetail(FleetMasterVinDetails fleetMasterVinDetails) throws Exception;	
	public int getIntervalIndexMileage(int index, MasterSchedule masterSchedule, int startingOdo);
}
