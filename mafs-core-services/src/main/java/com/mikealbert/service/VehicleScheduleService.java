package com.mikealbert.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.mikealbert.data.entity.Doc;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.MaintSchedulesBatch;
import com.mikealbert.data.entity.MasterSchedule;
import com.mikealbert.data.entity.VehicleSchedule;
import com.mikealbert.data.entity.VehicleScheduleInterval;
import com.mikealbert.data.vo.ArCreationVO;
import com.mikealbert.exception.MalDataValidationException;
import com.mikealbert.exception.MalException;
import com.mikealbert.service.vo.MaintenanceScheduleIntervalPrintVO;


public interface VehicleScheduleService {	
	
	public Long getVehSchdByMasterIdCount(MasterSchedule ms);
	public void	saveVehicleSchedule(VehicleSchedule vehicleSchedule) throws MalDataValidationException, MalException;
	 
	public Long	getNextVehicleScheduleSequence();
	
	public String	getAuthorizationNumber(Long vehicleSchSequence, String interval);
	public Long getVehicleSeqFromAuthNbr(String authorizationNumber);
	
	public boolean	isValidAuthorizationNumber(String authNumber);
	public boolean	isValidAuthNumberForFmsId(String authNumber,long fmsId); 
	public boolean hasActiveVehicleSchedule(FleetMaster fleetMaster);
	public String	AUTHORIZATION_NUMBER_PREFIX	= "MAFS"; 
	public  String calculateIntervalCode(Integer interval) ;

	public Integer	getLastCompletedInterval(VehicleSchedule vehicleSchedule)throws MalException;
	public 	MaintSchedulesBatch	getOpenBatch() throws MalException;
	public MaintSchedulesBatch findBatchById(Long id);
	public MaintSchedulesBatch	updateMaintScheduleBatch(Long maintSchedulesBatchId, String status,Date completionDate) throws MalException;
	public int getIntervalIndex(String interval);
	public MaintSchedulesBatch	saveMaintSchedulesBatch(MaintSchedulesBatch maintSchedulesBatch);
	public Boolean	isAuthCodeUsed(String authCode);

	public VehicleSchedule getVehicleScheduleById(long Id) throws MalException;
	public List<VehicleSchedule> getVehicleScheduleByFmsId(long fmsId) throws MalException;
	public VehicleSchedule getVehicleScheduleForAuthNumber(String authNumber);
	public List<VehicleSchedule> getVehicleScheduleByUnitNumber(String unitNumber) throws MalException;
	public VehicleSchedule getVehicleScheduleForVehSchedSeq(Long vehicleScheduleSeq);

	
	public void setupRePrintVehicleSchedule(FleetMaster fleetMaster, String userName);
	
	final String MAINT_SCH_BATCH_STATUS_OPEN = "O";
	final String MAINT_SCH_BATCH_STATUS_INPROGESS = "I";
	final String MAINT_SCH_BATCH_STATUS_COMPLETE = "C";
	
	static final List<String> INTERVAL_CODES =
	        Arrays.asList("A","C","E","F","G","H","J","K","L","M","N","P","R","T","U","V","W","X","Y");
	public List<VehicleScheduleInterval>	getCompletedIntervalsWithPO(VehicleSchedule vehicleSchedule) throws MalException;
	
	public void saveVehicleScheduleInterval(VehicleSchedule vehicleSchedule, String authNumber, Doc doc) throws MalException;
	public void saveVehicleScheduleInterval(VehicleScheduleInterval vehicleScheduleInterval) throws MalException;
	public VehicleScheduleInterval getVehicleScheduleIntervalForAuthNumber(String authNumber) throws MalException;
	public void deleteVehicleScheduleInterval(VehicleScheduleInterval vehicleScheduleInterval) throws MalException;
	public VehicleScheduleInterval getVehicleScheduleIntervalForDocNo(String docNo);
	public VehicleScheduleInterval	getLastUsedVehSchInterval(Long fmsId) throws MalException;
	
	public MaintenanceScheduleIntervalPrintVO getNextIntervalAndMileage(Long fmsId) throws MalException ;
	public MaintenanceScheduleIntervalPrintVO getNextIntervalAndMileage(Long fmsId, int odo) throws MalException ;
	public MaintenanceScheduleIntervalPrintVO getPreviousIntervalAndMileage(Long fmsId, int odo) throws MalException;
	public MaintenanceScheduleIntervalPrintVO getIndexIntervalAndMileage(Long fmsId, int index) throws MalException;
	
	public BigDecimal	getConversionFactor(FleetMaster fleetMaster)throws MalException;
	public void deleteVehicleSchedule(VehicleSchedule vehicleSchedule)throws MalException;
	public ArCreationVO getReprintChargeValues(FleetMaster fleetMaster) throws Exception;
	public VehicleSchedule findActiveScheduleByFmsId(Long fmsId);

}
