package com.mikealbert.service;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.comparator.MaintScheduleRuleComparator;
import com.mikealbert.data.dao.ClientContactDAO;
import com.mikealbert.data.dao.ClientScheduleTypeDAO;
import com.mikealbert.data.dao.ContractLineDAO;
import com.mikealbert.data.dao.FleetMasterDAO;
import com.mikealbert.data.dao.FleetMasterVinDetailsDAO;
import com.mikealbert.data.dao.LeaseElementDAO;
import com.mikealbert.data.dao.MaintScheduleRuleDAO;
import com.mikealbert.data.dao.MaintSchedulesProcessedDAO;
import com.mikealbert.data.dao.MaintenanceCategoryDAO;
import com.mikealbert.data.dao.MaintenancePreferenceAccountDAO;
import com.mikealbert.data.dao.MasterScheduleDAO;
import com.mikealbert.data.dao.ModelTypeDAO;
import com.mikealbert.data.dao.QuotationModelDAO;
import com.mikealbert.data.dao.ServiceTaskDAO;
import com.mikealbert.data.dao.SupplierProgressHistoryDAO;
import com.mikealbert.data.dao.VehicleScheduleDAO;
import com.mikealbert.data.entity.ClientScheduleType;
import com.mikealbert.data.entity.Contract;
import com.mikealbert.data.entity.ContractLine;
import com.mikealbert.data.entity.Doc;
import com.mikealbert.data.entity.Driver;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.FleetMasterVinDetails;
import com.mikealbert.data.entity.FuelGroupCode;
import com.mikealbert.data.entity.LeaseElement;
import com.mikealbert.data.entity.MaintScheduleRule;
import com.mikealbert.data.entity.MaintSchedulesBatch;
import com.mikealbert.data.entity.MaintSchedulesProcessed;
import com.mikealbert.data.entity.MaintenanceCategory;
import com.mikealbert.data.entity.MaintenancePreferenceAccount;
import com.mikealbert.data.entity.MasterSchedule;
import com.mikealbert.data.entity.MasterScheduleInterval;
import com.mikealbert.data.entity.ModelType;
import com.mikealbert.data.entity.ServiceTask;
import com.mikealbert.data.entity.SupplierProgressHistory;
import com.mikealbert.data.entity.VehicleSchedule;
import com.mikealbert.data.enumeration.UnitProgressCodeEnum;
import com.mikealbert.data.vo.ClientContactVO;
import com.mikealbert.data.vo.VehicleOrderStatusSearchCriteriaVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalDataValidationException;
import com.mikealbert.exception.MalException;
import com.mikealbert.exception.VehicleScheduleError;
import com.mikealbert.service.vo.MaintenanceScheduleIntervalPrintVO;
import com.mikealbert.util.MALUtilities;

@Service("maintenanceScheduleService")
public class MaintenanceScheduleServiceImpl implements MaintenanceScheduleService {

	private MalLogger logger = MalLoggerFactory.getLogger(this.getClass());

	@Resource
	private ServiceTaskDAO serviceTaskDAO;
	@Resource
	private MaintScheduleRuleDAO maintScheduleRuleDAO;
	@Resource
	private MaintenanceCategoryDAO maintenanceCategoryDAO;
	@Resource
	private MasterScheduleDAO masterScheduleDAO;
	@Resource
	private ClientScheduleTypeDAO clientScheduleTypeDAO;
	@Resource
	private VehicleScheduleService vehicleScheduleService;
	@Resource
	private ModelTypeDAO modelTypeDAO;
	@Resource
	private FleetMasterDAO fleetMasterDAO;	
	@Resource
	private MaintenancePreferenceAccountDAO maintenancePreferenceAccountDAO;
	@Resource 
	private ContractService contractService;	
	@Resource 
	private WillowConfigService willowConfigService;
	@Resource 
	private FleetMasterService fleetMasterService;
	@Resource 
	private FuelService fuelService;
	@Resource
	private MaintSchedulesProcessedDAO maintSchedulesProcessedDAO;
	@Resource
	private ClientContactDAO clientContactDAO;	
	@Resource
	private QuotationService quotationService;
	@Resource
	private DocumentService documentService;
	@Resource
	private SupplierProgressHistoryDAO supplierProgressHistoryDAO;
	@Resource
	private ContractLineDAO contractLineDAO;
	@Resource
	private QuotationModelDAO quotationModelDAO;
	@Resource 
	private LeaseElementDAO leaseElementDAO;
	@Resource 
	private DriverService driverService;
	@Resource 
	private VehicleScheduleDAO vehicleScheduleDAO;
	@Resource
	private FleetMasterVinDetailsDAO fleetMasterVinDetailsDAO;
	
	public static final String FORMAL_EXT  = "Formal" ;

	@Override
	public List<FuelGroupCode> getFuelTypeGroupList() {
		String MssGroupKeyConfig = willowConfigService.getConfigValue(FUEL_GROUP_KEY_MSS);
		List<FuelGroupCode> fuelGroupCodes = maintScheduleRuleDAO.getFuelGroupList(MssGroupKeyConfig);
		
		//OPU-829 - Due to issue with Oracle Upgrade 12c (ORA-01791: not a SELECTed expression), moved order by here from MaintScheduleRuleDAO
		//Sorting the list of distinct fuelGroupCodes on fuel_group_description in ASC order
		Collections.sort(fuelGroupCodes, new Comparator<FuelGroupCode>() { 
			public int compare(FuelGroupCode f1, FuelGroupCode f2) { 
				return f1.getFuelGroupDescription().compareTo(f2.getFuelGroupDescription()); 
			}
		});	
		
		return fuelGroupCodes;
        		
	}
//
//	@Override
//	public String getFuelDescByFuelTypeGroup(String fuelType,String groupKey) {
//		return maintScheduleRuleDAO.getFuelDescByFuelTypeGroup(fuelType,groupKey);
//	}

	@Override
	public List<ServiceTask> getTaskList() {
		return serviceTaskDAO.getServiceTaskList();
	}

	@Override
	public List<ServiceTask> getTaskListByName() {
		return serviceTaskDAO.getServiceTaskListSortedByName();
	}

	@Override
	public List<MasterSchedule> getScheduleList() {
		Sort sort = new Sort(Sort.Direction.ASC, "clientScheduleType.scheduleType", "masterCode");
		return masterScheduleDAO.findAll(sort);
	}

	@Override
	public int getScheduleCount(ServiceTask task) {
		if(task != null)
		{
			List<MasterSchedule> msList = masterScheduleDAO.findByServiceTask(task.getSrvtId());
			if (msList != null) {
				return msList.size();
			}
		}
		return 0;
	}
	
	@Override
	public List<MaintSchedulesProcessed> getMaintSchedulesForVehSchedAssignment(){
		return maintSchedulesProcessedDAO.getAllForVehSchedAssignment();
	}
	
	@Override
	public List<MaintSchedulesProcessed> getMaintSchedulesForDetermineExpPrintDate(){
		
		return maintSchedulesProcessedDAO.getAllForDetermineExpPrintDate();
	}
	
	@Override
	public List<MaintSchedulesProcessed> getMaintSchedulesQualifiedForPrint(){
		List<MaintSchedulesProcessed> maintSchedulesProcessedList =  maintSchedulesProcessedDAO.getAllQualifiedForSchedulePrint();
		//get a open batch and assign in MaintSchedulesProcessed
		MaintSchedulesBatch maintSchedulesBatch = vehicleScheduleService.getOpenBatch();
		for (MaintSchedulesProcessed maintSchedulesProcessed : maintSchedulesProcessedList) {
			maintSchedulesProcessed.setMaintSchedulesBatch(maintSchedulesBatch);
		}
		return maintSchedulesProcessedList;
	}

	@Override
	public List<MaintenanceCategory> getMaintenanceCategories() {
		return maintenanceCategoryDAO.getMaintenanceCategories();
	}

	@Override
	public MaintenanceCategory getMaintenanceCategory(String code) {
		return maintenanceCategoryDAO.findById(code).orElse(null);
	}

	@Override
	public Long getRulesByMasterIdCount(MasterSchedule ms) {
		return maintScheduleRuleDAO.getRulesByMasterIdCount(ms.getMschId());
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void saveRule(MaintScheduleRule rule) throws Exception {
		try {
			validateMaintScheduleRule(rule);
			maintScheduleRuleDAO.saveAndFlush(rule);
		} catch (MalBusinessException mbe) {
			logger.error(mbe);
			throw mbe;
		} catch (Exception ex) {
			logger.error(ex);
			throw new MalException("generic.error.occured.while", new String[] { "saving a maintenance schedule rule" }, ex);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void saveTask(ServiceTask task) throws Exception {
		try {
			validateServiceTask(task);
			serviceTaskDAO.saveAndFlush(task);
		} catch (MalBusinessException mbe) {
			logger.error(mbe);
			throw mbe;
		} catch (Exception ex) {
			logger.error(ex);
			throw new MalException("generic.error.occured.while", new String[] { "saving a service task" }, ex);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void saveSchedule(MasterSchedule schedule) throws Exception {
		try {
			validateMasterSchedule(schedule);
			masterScheduleDAO.saveAndFlush(schedule);
		} catch (MalBusinessException mbe) {
			logger.error(mbe);
			throw mbe;
		} catch (Exception ex) {
			logger.error(ex);
			throw new MalException("generic.error.occured.while", new String[] { "saving a master schedule" }, ex);
		}
	}

	@Override
	public ServiceTask getTask(Long taskId) {
		return serviceTaskDAO.findById(taskId).orElse(null);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteTask(ServiceTask task) throws Exception {
		try {
			validateTaskDelete(task);
			serviceTaskDAO.delete(task);
		} catch (MalBusinessException mbe) {
			logger.error(mbe);
			throw mbe;
		} catch (Exception ex) {
			logger.error(ex);
			throw new MalException("generic.error.occured.while", new String[] { "deleting a service task" }, ex);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteRule(MaintScheduleRule rule) throws Exception {
		try {
			maintScheduleRuleDAO.delete(rule);
		} catch (Exception ex) {
			logger.error(ex);
			throw new MalException("generic.error.occured.while", new String[] { "deleting a maintenance schedule rule" }, ex);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteMasterSchedule(MasterSchedule schedule) throws Exception {
		try {
			validateMasterScheduleDelete(schedule);
			masterScheduleDAO.delete(schedule);
		} catch (MalBusinessException mbe) {
			logger.error(mbe);
			throw mbe;
		} catch (Exception ex) {
			logger.error(ex);
			throw new MalException("generic.error.occured.while", new String[] { "deleting a master schedule" }, ex);
		}
	}

	@Override
	public MasterSchedule getMasterSchedule(Long id) {
		return masterScheduleDAO.findById(id).orElse(null);
	}

	@Override
	public List<ServiceTask> getServiceTaskByCode(String code) {
		return serviceTaskDAO.getServiceTaskByCode(code);
	}

	@Override
	public List<ClientScheduleType> getClientScheduleTypes() {
		return clientScheduleTypeDAO.getClientScheduleTypeList();
	}

	@Override
	public ClientScheduleType getClientScheduleTypeByType(String type) {
		return clientScheduleTypeDAO.getClientScheduleTypeByType(type);
	}

	@Override
	public List<MasterSchedule> getMasterScheduleByCode(String code) {
		return masterScheduleDAO.getMasterSchedulesByCode(code);
	}

	@Override
	public List<MasterSchedule> getMasterSchedulesByInterval(int interval) {
		return null;
	}

	@Override
	public List<MaintScheduleRule> getMaintScheduleRules() {
		return maintScheduleRuleDAO.findAll(getDefaultCategorySort());
	}

	@Override
	public ModelType getModelTypeByCode(Long code) {
		return modelTypeDAO.findById(code).orElse(null);
	}

	private Sort getDefaultCategorySort() {
		Order order;
		List<Order> sorts = new ArrayList<Order>();

		order = new Order(Sort.Direction.DESC, "year");
		sorts.add(order);
		order = new Order(Sort.Direction.ASC, "makeCode");
		sorts.add(order);
		order = new Order(Sort.Direction.ASC, "modelTypeDesc");
		sorts.add(order);
		order = new Order(Sort.Direction.ASC, "makeModelDesc");
		sorts.add(order);
//		order = new Order(Sort.Direction.ASC, "fuelGrouping.fuelGroupingPK.fuelType");
//		sorts.add(order);
		order = new Order(Sort.Direction.ASC, "masterSchedule.masterCode");
		sorts.add(order);

		Sort sort = new Sort(sorts);
		return sort;
	}

	private List<MasterScheduleInterval> copyMasterScheduleIntervals(MasterSchedule schedule) {

		List<MasterScheduleInterval> scheduleIntervals = new ArrayList<MasterScheduleInterval>();

		for (MasterScheduleInterval interval : schedule.getMasterScheduleIntervals()) {

			MasterScheduleInterval temp = new MasterScheduleInterval();
			temp.setInterval(interval.getIntervalMultiple());
			temp.setOrder(interval.getOrder());
			temp.setRepeatFlag(interval.getRepeatFlag());
			temp.setServiceTask(interval.getServiceTask());
			scheduleIntervals.add(temp);
		}
		return scheduleIntervals;
	}

	public MasterSchedule copyMasterSchedule(MasterSchedule schedule) {

		List<MasterScheduleInterval> intervals = new ArrayList<MasterScheduleInterval>();

		MasterSchedule newSchedule = new MasterSchedule();
		newSchedule.setHiddenFlag(schedule.getHiddenFlag());
		newSchedule.setClientScheduleType(schedule.getClientScheduleType());
		newSchedule.setDescription(null);
		newSchedule.setDistanceFrequency(schedule.getDistanceFrequency());
		newSchedule.setMonthFrequency(schedule.getMonthFrequency());

		for (MasterScheduleInterval interval : copyMasterScheduleIntervals(schedule)) {

			interval.setMasterSchedule(newSchedule);
			intervals.add(interval);

		}
		newSchedule.setMasterScheduleIntervals(intervals);
		return newSchedule;
	}

	private void validateTaskDelete(ServiceTask task) throws MalBusinessException {
		if (getScheduleCount(task) > 0) {
			throw new MalBusinessException("service.validation", new String[] { "Service Task cannot be deleted if assigned to a schedule" });
		}
	}

	private void validateMasterScheduleDelete(MasterSchedule schedule) throws MalBusinessException {
		if (getRulesByMasterIdCount(schedule) > 0) {
			throw new MalBusinessException("service.validation", new String[] { "Master Schedule cannot be deleted if assigned to a rule" });
		}
		if (vehicleScheduleService.getVehSchdByMasterIdCount(schedule) > 0) {
			throw new MalBusinessException("service.validation", new String[] { "Master Schedule cannot be deleted if assigned to a vehicle" });
		}
	}

	@Override
	public String getNextMasterScheduleCode(int interval) {
		String sequenceAsString;
		List<MasterSchedule> schedules = masterScheduleDAO.getMasterSchedulesByInterval(interval);
		if (schedules.size() > 0) {
			String code = schedules.get(0).getMasterCode();
			String sequence = code.substring(code.indexOf("-") + 1);
			int newSequence = Integer.parseInt(sequence) + 1;
			sequenceAsString = Integer.toString(newSequence + 10000).substring(1);
		} else {
			sequenceAsString = "0001";
		}

		return Integer.toString(interval) + "-" + sequenceAsString;
	}

	private void validateMaintScheduleRule(MaintScheduleRule rule) throws MalBusinessException {
		if (MALUtilities.isEmpty(rule.getMasterSchedule())) {
			throw new MalBusinessException("service.validation", new String[] { "Schedule Rule must have a Master Schedule" });
		}
		if (MALUtilities.isEmpty(rule.getYear())) {
			throw new MalBusinessException("service.validation", new String[] { "Schedule Rule must have a Model Year" });
		}

		if (!MALUtilities.isEmpty(rule.getMakeModelDesc())) {
			if (MALUtilities.isEmpty(rule.getModelTypeDesc())) {
				throw new MalBusinessException("service.validation", new String[] { "Schedule Rule with Model must have a Model Type" });
			}
			if (MALUtilities.isEmpty(rule.getMakeCode())) {
				throw new MalBusinessException("service.validation", new String[] { "Schedule Rule with Model must have a Make Code" });
			}

		}
	}

	private void validateServiceTask(ServiceTask task) throws MalBusinessException {
		if (MALUtilities.isEmpty(task.getMaintenanceCategory())) {
			throw new MalBusinessException("service.validation", new String[] { "Service Task must have a category" });
		}
		if (MALUtilities.isEmpty(task.getServiceCode())) {
			throw new MalBusinessException("service.validation", new String[] { "Service Task must have a name" });
		} else {
			if ((getServiceTaskByCode(task.getServiceCode()).size() > 1)
					|| (getServiceTaskByCode(task.getServiceCode()).size() == 1 && !getServiceTaskByCode(task.getServiceCode()).get(0).getSrvtId()
							.equals(task.getSrvtId()))) {
				throw new MalBusinessException("service.validation", new String[] { "Service Task name already exists" });
			}
		}
		if (MALUtilities.isEmpty(task.getTaskDescription())) {
			throw new MalBusinessException("service.validation", new String[] { "Service Task must have a description" });
		}
		if (MALUtilities.isEmpty(task.getCost())) {
			throw new MalBusinessException("service.validation", new String[] { "Service Task must have a cost" });
		}
		if (MALUtilities.isEmpty(task.getActiveFlag())) {
			throw new MalBusinessException("service.validation", new String[] { "Service Task must be active or inactive" });
		} else {
			if (!task.getActiveFlag().equals("Y") && !task.getActiveFlag().equals("N")) {
				throw new MalBusinessException("service.validation", new String[] { "Service Task must be active or inactive" });
			}
		}
	}

	private void validateMasterSchedule(MasterSchedule schedule) throws MalBusinessException {

		if (MALUtilities.isEmpty(schedule.getClientScheduleType())) {
			throw new MalBusinessException("service.validation", new String[] { "Master Schedule must have a type" });
		}
		if (MALUtilities.isEmpty(schedule.getDistanceFrequency()) || schedule.getDistanceFrequency() < 1) {
			throw new MalBusinessException("service.validation", new String[] { "Master Schedule must have a distance interval greater than 0" });
		}
		if (MALUtilities.isEmpty(schedule.getMonthFrequency()) || schedule.getMonthFrequency() < 1) {
			throw new MalBusinessException("service.validation", new String[] { "Master Schedule must have a month interval greater than 0" });
		}
		if (MALUtilities.isEmpty(schedule.getMasterCode())) {
			throw new MalBusinessException("service.validation", new String[] { "Master Schedule must have a code" });
		} else {
			if ((getMasterScheduleByCode(schedule.getMasterCode()).size() > 1)
					|| (getMasterScheduleByCode(schedule.getMasterCode()).size() == 1 && !getMasterScheduleByCode(schedule.getMasterCode()).get(0)
							.getMschId().equals(schedule.getMschId()))) {
				throw new MalBusinessException("service.validation", new String[] { "Master Schedule code already exists" });
			}
		}
		if (MALUtilities.isEmpty(schedule.getDescription())) {
			throw new MalBusinessException("service.validation", new String[] { "Master Schedule must have a description" });
		}
		if (MALUtilities.isEmpty(schedule.getHiddenFlag())) {
			throw new MalBusinessException("service.validation", new String[] { "Master Schedule must be hidden or visible" });
		} else {
			if (!schedule.getHiddenFlag().equals("Y") && !schedule.getHiddenFlag().equals("N")) {
				throw new MalBusinessException("service.validation", new String[] { "Master Schedule must be hidden or visible" });
			}
		}
	}

	public MaintScheduleRule getMaintScheduleRule(Long id) {
		return maintScheduleRuleDAO.findById(id).orElse(null);
	}

	@Override
	public List<MasterSchedule> findActiveByScheduleTypeAndInterval(String scheduleType, int miles) {
		return masterScheduleDAO.findActiveByScheduleTypeAndInterval(scheduleType, miles);
	}

	@Override
	public List<MasterSchedule> findActiveByScheduleType(String scheduleType) {
		return masterScheduleDAO.findActiveByScheduleType(scheduleType);
	}

	@Override
	public List<MasterSchedule> getMasterSchedulesByScheduleType(String scheduleType, Long cId, String accountCode, String accountType,
			boolean hiddenFlag) {
		String hiddenFlagStr = "N";
		if (hiddenFlag) {
			hiddenFlagStr = "Y";
		}
		if (MALUtilities.isEmpty(accountCode)) {
			return masterScheduleDAO.findByScheduleType(scheduleType, hiddenFlagStr);
		} else {
			return masterScheduleDAO.findByScheduleTypeAndClient(scheduleType, cId, accountCode, accountType, hiddenFlagStr);
		}

	}

	@Override
	public List<MasterSchedule> getMasterSchedulesByScheduleTypeAndInterval(String scheduleType, Long cId, String accountCode, String accountType,
			int miles, boolean hiddenFlag) {
		String hiddenFlagStr = "N";
		if (hiddenFlag) {
			hiddenFlagStr = "Y";
		}
		if (MALUtilities.isEmpty(accountCode)) {
			return masterScheduleDAO.findByScheduleTypeAndInterval(scheduleType, miles, hiddenFlagStr);
		} else {
			return masterScheduleDAO.findByScheduleTypeAndClientAndInterval(scheduleType, cId, accountCode, accountType, miles, hiddenFlagStr);
		}

	}

	public List<MaintScheduleRule> getMatchingRules(MaintScheduleRule rule, String scheduleType) {
		if (rule != null) {
			
			
			String year = rule.getYear();
			String makeModelDesc = rule.getMakeModelDesc();
			String fuelTypeGroup = rule.getFuelTypeGroup();
			String highMileage = rule.getHighMileage();
			Long cId = null;
			String accountCode = null;
			String accountType = null;
			
			String modelTypeDesc = rule.getModelTypeDesc();
			String makeCode = rule.getMakeCode();
			if(CLIENT_SCHEDULE_TYPE.equals(scheduleType)){
				cId = rule.getScheduleAccount() != null ? rule.getScheduleAccount().getExternalAccountPK().getCId() : null;
				accountCode = rule.getScheduleAccount() != null ? rule.getScheduleAccount().getExternalAccountPK().getAccountCode() : null;
				accountType = rule.getScheduleAccount() != null ? rule.getScheduleAccount().getExternalAccountPK().getAccountType() : null;
				
			}
			
			List<MaintScheduleRule> rulesList =null;
			if("Y".equals(highMileage)){
				//high mileage
				rulesList = maintScheduleRuleDAO.find(fuelTypeGroup, highMileage);
			}else{
				//non-high mileage
				rulesList = maintScheduleRuleDAO.findByScheduleType(year, makeModelDesc, fuelTypeGroup, modelTypeDesc, makeCode,cId,accountCode, accountType, scheduleType);
			}
			return rulesList;
		}
		return null;

	}

	@Override
	public List<MaintenanceScheduleIntervalPrintVO> getMasterSchedulePrint(Long masterScheduleId) {
		return getMasterSchedulePrint(masterScheduleId, 0, new BigDecimal(1));
	}

	
	@Override
	public List<MaintenanceScheduleIntervalPrintVO> getMasterSchedulePrint(Long masterScheduleId, int startingInterval, BigDecimal conversionFactor) {
		List<MaintenanceScheduleIntervalPrintVO> rows = new ArrayList<MaintenanceScheduleIntervalPrintVO>();

		rows.add(getMasterSchedulePrintHeader(masterScheduleId));
		rows.addAll(getMasterScheduleIntervalList(masterScheduleId, startingInterval, conversionFactor));
		return rows;
	}

	@Override
	public MaintenanceScheduleIntervalPrintVO getMasterSchedulePrintHeader(Long masterScheduleId) {

		MaintenanceScheduleIntervalPrintVO row = new MaintenanceScheduleIntervalPrintVO();
		MasterSchedule masterSchedule = masterScheduleDAO.findById(masterScheduleId).orElse(null);
		List<MasterScheduleInterval> intervals = masterSchedule.getMasterScheduleIntervals();

		row.setIntervalDescription("Interval");
		for (MasterScheduleInterval interval : intervals) {
			String methodName = "setColumn" + interval.getOrder();
			try {
				// TODO: consider using ObjectUtils
				Method method = row.getClass().getMethod(methodName, new Class[] { String.class });
				method.invoke(row, interval.getServiceTask().getTaskDescription());
			} catch (Exception e) {
				logger.error(e);
			}
		}

		return row;
	}
	
	//Used for MasterSchedulePrint
	@Override
	public List<MaintenanceScheduleIntervalPrintVO> getMasterScheduleIntervalList(Long masterScheduleId, int startingInterval, BigDecimal conversionFactor) {
		return getMasterScheduleIntervalList(masterScheduleId, startingInterval, conversionFactor, DEFAULT_SCHEDULE_ROWS, false);
	}
	
	
	@Override
	public List<MaintenanceScheduleIntervalPrintVO> getMasterScheduleIntervalList(Long masterScheduleId, int startingInterval, BigDecimal conversionFactor, int requestedRows) {
		return getMasterScheduleIntervalList(masterScheduleId, startingInterval, conversionFactor, requestedRows, false);
	}
	
	//Used in Vehicle Schedule Services
	@Override
	public List<MaintenanceScheduleIntervalPrintVO> getMasterScheduleIntervalList(Long masterScheduleId, int startingInterval, BigDecimal conversionFactor, int requestedRows, boolean ignoreNonMileageIntervals) {
		
		List<MaintenanceScheduleIntervalPrintVO> intervalList = new ArrayList<MaintenanceScheduleIntervalPrintVO>();		
		MasterSchedule masterSchedule = masterScheduleDAO.findById(masterScheduleId).orElse(null);
	
		// we need the distanceFrequency so we can compute the right IntervalDescription for display
		// this is the only reason why we need the masterSchedule
		int freqDistance = masterSchedule.getDistanceFrequency();
		
		// a holder for "value"
		String value = " ";
		// counter that track how many slots we have filled (up to SCHEDULE_ROWS)
		int totalAdded = 0;
		// a flag to indicate that we had at least 1 applicable interval for that index
		boolean hasApplicableInterval = false;
		
		int index = 1;
		List<MasterScheduleInterval> intervals = new ArrayList<MasterScheduleInterval>(masterSchedule.getMasterScheduleIntervals()); 
		List<MasterScheduleInterval> intervalsToRemove = new ArrayList<MasterScheduleInterval>();
		
		// if there is at least 1 interval
		if(intervals.size() > 0){
			// process until we either have a full schedule or we have processed all intervals (non-repeating interval tasks as all processed)
			int intervalValue = freqDistance*index;
			boolean processingTopRow = false;
			
			while( requestedRows > 0 ? totalAdded < requestedRows && intervals.size() > 0 && intervalValue < MAX_MILEAGE_TO_SEARCH_FOR_INTERVALS : intervalValue < MAX_MILEAGE_TO_SEARCH_FOR_INTERVALS ){
				
				MaintenanceScheduleIntervalPrintVO row = new MaintenanceScheduleIntervalPrintVO();
				
				intervalValue = freqDistance*index;
				
				// for each task in the list
				for(MasterScheduleInterval interval : intervals) {
					
					if(interval.getIntervalMultiple() == 0) {
						processingTopRow = true;
					}

					if(intervalValue >= startingInterval || interval.getIntervalMultiple() == 0) {
						// if it is not repeating, and it matches the current index
						if((interval.getRepeatFlag().equalsIgnoreCase("N") && (index == interval.getIntervalMultiple()) && !processingTopRow) 
								|| interval.getIntervalMultiple() == 0){
							// mark that at least 1 interval was applicable for the given index
							hasApplicableInterval = true;
							value = "X";
							
							// capture it for removal
							intervalsToRemove.add(interval);
							
							//intervals.remove(interval);
						// if it is repeating
						}else if((interval.getRepeatFlag().equalsIgnoreCase("Y") && (index % interval.getIntervalMultiple()) == 0) && !processingTopRow){
							// mark that at least 1 interval was applicable for the given index
							hasApplicableInterval = true;
							value = "X";
						}else{
							value = " ";
						}
						
						// we need to call the "setColumnX" method to set the " " or "X" in the right place in the VO (schedule)
						String methodName = "setColumn" + interval.getOrder();
						try {
							//TODO: consider using ObjectUtils
							Method method = row.getClass().getMethod(methodName, new Class[] {String.class});
							method.invoke(row, value);
						} catch (Exception e) {
							logger.error(e);
						}
						
					}
					
					
				}
				
				// if we processed it then "dump it" before the next loop
				intervals.removeAll(intervalsToRemove);
				
				
				if(hasApplicableInterval){
					if(conversionFactor.compareTo(BigDecimal.ONE) != 0) {
						intervalValue = conversionFactor.multiply(new BigDecimal(intervalValue)).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
					}
					if(processingTopRow) {
						row.setIntervalDescription(" ");
					} else {
						row.setIntervalDescription( Integer.toString(intervalValue) );
					}
					//If ignoreNonMileageIntervals flag is set to True that means we do not want non-mileage intervals like "PO Required"
					if ((ignoreNonMileageIntervals && MALUtilities.isNotEmptyString(row.getIntervalDescription().trim())) || (!ignoreNonMileageIntervals)){
						intervalList.add(row);
						totalAdded++;
					}
					hasApplicableInterval = false;
				}
				
				if(!processingTopRow) {   // processing the special "top" row should not increment index
					index++;
				}
				processingTopRow = false;
			
			}
		}

		return intervalList;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public VehicleSchedule createVehicleSchedule(MasterSchedule masterSchedule, FleetMaster fleetMaster) throws MalException {
		try {
			if (masterSchedule == null) {
				throw new MalException("required.field", new String[] {"master schedule"});
			}
			if (fleetMaster == null) {
				throw new MalException("required.field", new String[] {"fleet master"});
			}
			if(!vehicleScheduleService.hasActiveVehicleSchedule(fleetMaster)) {
				Long startOdoReading = fleetMasterService.getScheduleLatestOdoReading(fleetMaster.getFmsId());
				VehicleSchedule vehicleSchedule = new VehicleSchedule();
				vehicleSchedule.setFleetMaster(fleetMaster);
				vehicleSchedule.setMasterSchedule(masterSchedule);
				vehicleSchedule.setActiveFrom(new Date());
				vehicleSchedule.setActiveTo(null);
				vehicleSchedule.setVehSchSeq(vehicleScheduleService.getNextVehicleScheduleSequence());
				vehicleSchedule.setStartOdoReading(startOdoReading);
				return vehicleSchedule;
			} else {
				throw new MalException("service.validation", new String[] { "Vehicle cannot have two active vehicle maintenance schedules" });
			}
		} catch (Exception ex) {
			if (ex instanceof MalException)
				throw (MalException) ex;
			throw new MalException("generic.error.occured.while", new String[] {"saving vehicle schedule"}, ex);
		}		

	}
	
	@Override
	public MasterSchedule determineMasterScheduleByFmsId(Long fmsId) throws MalDataValidationException, MalBusinessException {
		String activeFlag = "Y"; 
		
		if (fmsId != null){
			FleetMaster fleetMaster = fleetMasterDAO.findById(fmsId).orElse(null);
			
			if (fleetMaster.getVin() != null){
				ContractLine contractLine = contractService.getCurrentOrFutureContractLine(fleetMaster);
				if (contractLine != null){
					ExternalAccount externalAccount = contractLine.getContract().getExternalAccount();
					ClientContactVO poc = null;
					try {
						poc =  getAllClientContactVOsForSchedules(externalAccount, fleetMaster);
						} catch (Exception e) {
							// Don't need to do anything, will be handled in following code
						}
					if(poc != null) {
						MaintenancePreferenceAccount  mpa = maintenancePreferenceAccountDAO.getMaintenancePreferenceAccountData(
																externalAccount.getExternalAccountPK().getCId(), 
																externalAccount.getExternalAccountPK().getAccountType(), 
																externalAccount.getExternalAccountPK().getAccountCode());
						if (!MALUtilities.isEmpty(mpa)){
							Long clientScheduleId = mpa.getClientScheduleId();
							
							if (clientScheduleId != null){
								Boolean highMileageExists = fleetMasterService.getHighMileage(fmsId);
								String highMileage = "N";
								
								if (highMileageExists){
									highMileage = "Y";
								} 
	
								if(!MALUtilities.isEmpty(fleetMaster.getVehicleVinDetails())){
									if(!MALUtilities.isEmpty(fleetMaster.getVehicleVinDetails().getVin())){
							
										if (fleetMaster.getVehicleVinDetails().getFuelType() != null  &&  (!fleetMaster.getVehicleVinDetails().getFuelType().equalsIgnoreCase("NOT FOUND"))){
											String groupKey = willowConfigService.getConfigValue(FUEL_GROUP_KEY_MSS);
											FuelGroupCode fuelGroupCode = fuelService.getFuelGroupCode(groupKey, fleetMaster);
							
											if (!MALUtilities.isEmpty(fuelGroupCode)){
												List<MaintScheduleRule> maintScheduleRules = getMaintSchedRulesByActiveAndHighMileageAndClientSchedType(activeFlag, highMileage, clientScheduleId, externalAccount);
												
												//mss-223 - get additional rules to account for the concept of base (current) schedules
												List<MaintScheduleRule> combinedMaintScheduleRules = getAdditionalRules(maintScheduleRules,fleetMaster);
											
												MasterSchedule masterSchedule = determineMasterSchedule(fleetMaster, combinedMaintScheduleRules, fuelGroupCode, highMileage);
											
												if (!MALUtilities.isEmpty(masterSchedule)){
													return masterSchedule;
												} else {
													if(highMileage.equalsIgnoreCase("Y")){
														throw new VehicleScheduleError(VehicleScheduleError.MASTER_SCHEDULED_NOT_MATCH_HIGH_MILES);
													}else{
														throw new VehicleScheduleError(VehicleScheduleError.MASTER_SCHEDULE_NOT_DETERMINED);
													}
												}
											} else {
												throw new VehicleScheduleError(VehicleScheduleError.FUEL_TYPE_NOT_MATCHED);
											}
										} else {
											throw new VehicleScheduleError(VehicleScheduleError.FUEL_TYPE_MISSING);
										}
									}else{
										throw new VehicleScheduleError(VehicleScheduleError.VIN_NOT_DECODED);
									}
								}else{
									throw new VehicleScheduleError(VehicleScheduleError.FMS_ID_MISSING);
								}
							} else {
								throw new VehicleScheduleError(VehicleScheduleError.CLIENT_SCHEDULE_TYPE_MISSING);
							}
						} else {
							throw new VehicleScheduleError(VehicleScheduleError.MAINT_PREF_MISSING);
						}
					} else {
						throw new VehicleScheduleError(VehicleScheduleError.POC_MISSING);
					}
				} else {
					throw new VehicleScheduleError(VehicleScheduleError.CONTRACT_LINE_MISSING);
				}
			} else {
				throw new VehicleScheduleError(VehicleScheduleError.VIN_MISSING);
			}
		} else {
			throw new VehicleScheduleError(VehicleScheduleError.FMS_ID_MISSING);
		}
	}

	@Override
	public List<MaintScheduleRule> getMaintSchedRulesByActiveAndHighMileageAndClientSchedType(String activeFlag, String highMileage, Long clientScheduleId, ExternalAccount externalAccount) {
		return maintScheduleRuleDAO.getRulesByActiveAndHighMileageAndClientSchedType(activeFlag,highMileage,clientScheduleId,externalAccount);
	}

	@Override
	public MasterSchedule determineMasterSchedule(FleetMaster fleetMaster, List<MaintScheduleRule> maintScheduleRules, FuelGroupCode fuelGroupCode, String highMileage) {	
		MasterSchedule masterSchedule = null;
		boolean matchFound = false; 
		
		try {
			if (highMileage.equals("N")){
				//mss-223 - sort order needs to be correct in order for this to work
				for(MaintScheduleRule rule : maintScheduleRules){
				    if(Double.parseDouble(rule.getYear()) == (Double.parseDouble(fleetMaster.getModel().getModelMarkYear().getModelMarkYearDesc())) 		    				
				    		&& (MALUtilities.isEmpty(rule.getMakeCode())      || rule.getMakeCode().equals(fleetMaster.getModel().getMake().getMakeCode()))
				    		&& (MALUtilities.isEmpty(rule.getModelTypeDesc()) || rule.getModelTypeDesc().equals(fleetMaster.getModel().getModelType().getDescription()))
				    		&& (MALUtilities.isEmpty(rule.getMakeModelDesc()) || rule.getMakeModelDesc().equals(fleetMaster.getModel().getMakeModelRange().getDescription()))
			    			&& (MALUtilities.isEmpty(rule.getFuelTypeGroup()) || rule.getFuelTypeGroup().equals(fuelGroupCode.getFuelGroupCode()))){			    	
				        matchFound = true;
				        masterSchedule = rule.getMasterSchedule();
				        break;
				    }
				}
			}
			else { //high mileage - does not follow the established hierarchy
				for(MaintScheduleRule rule : maintScheduleRules){
					if (rule.getFuelTypeGroup() != null){
			    		if(rule.getFuelTypeGroup().equals(fuelGroupCode.getFuelGroupCode())){	
					        matchFound = true;
					        masterSchedule = rule.getMasterSchedule();
					        break;
					    }
					}	
				}
				if (!matchFound){
					for(MaintScheduleRule rule : maintScheduleRules){
						if (rule.getFuelTypeGroup() == null){
					        matchFound = true;
					        masterSchedule = rule.getMasterSchedule();
					        break;
						}						
					}
				}
			}
		} catch(Exception ex) {
			throw new MalException("generic.error.occured.while", 
					new String[] { "determining master schedule to use for fms id: " +  fleetMaster.getFmsId() }, ex);				
		}	
		
		return masterSchedule;
	}	
	
	//mss-223 - create additional rules to account for the concept of base (current) schedules
	public List<MaintScheduleRule> getAdditionalRules(List<MaintScheduleRule> maintScheduleRules, FleetMaster fleetMaster) {
		List<MaintScheduleRule> newMaintScheduleRules = new ArrayList<MaintScheduleRule>();
	
		for(MaintScheduleRule rule : maintScheduleRules){
			if (rule.getBaseSchedule().equals("Y")){
				Double year = null;
				String endYear = maintScheduleRuleDAO.getEndYear(rule);
				
				if (endYear != null){
					year = Double.parseDouble(endYear);
				}
				else {
					year = Double.parseDouble(fleetMaster.getModel().getModelMarkYear().getModelMarkYearDesc()) + .5; //add .5 in order to create new rule for the year of the vehicle being checked
				}
				
				Double yearDiff = year - Double.parseDouble(rule.getYear());
				for(double i=.5; i < yearDiff; i+=.5){
					MaintScheduleRule newRule = new MaintScheduleRule();
					newRule.setYear(Double.toString(Double.parseDouble(rule.getYear()) + i));
					newRule.setMakeCode(rule.getMakeCode());
					newRule.setModelTypeDesc(rule.getModelTypeDesc());
					newRule.setMakeModelDesc(rule.getMakeModelDesc());
					newRule.setFuelTypeGroup(rule.getFuelTypeGroup());
					newRule.setMasterSchedule(rule.getMasterSchedule());
					newMaintScheduleRules.add(newRule);
				}
			}
		}
		
		maintScheduleRules.addAll(newMaintScheduleRules);
		
		if(maintScheduleRules != null && maintScheduleRules.size() > 0) {
			Collections.sort(maintScheduleRules, new MaintScheduleRuleComparator());
		}		
		
		return maintScheduleRules;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = MalException.class)
	public void saveScheduleProcessRecord(
			MaintSchedulesProcessed processedRecord) throws MalException {
		try {
			maintSchedulesProcessedDAO.saveAndFlush(processedRecord);
		} catch (Exception ex) {
			logger.error(ex);
			throw new MalException("generic.error.occured.while", new String[] { "saving a maint schedule processed record" }, ex);
		}
	}
	
	@Override
	public MaintSchedulesProcessed findProcessRecordByFmsId(Long id) {
		MaintSchedulesProcessed retVal = null;
		
		retVal = maintSchedulesProcessedDAO.findMaintScheduleProcessRecordByFmsId(id);
		
		return retVal;
	}
	
	@Override
	public List<MaintSchedulesProcessed> getNextPrintList() {
		return maintSchedulesProcessedDAO.getAllQualifiedForSchedulePrint();
	}

	@Override
	public long getNextPrintListCount() {
		return maintSchedulesProcessedDAO.getAllQualifiedForSchedulePrintCount();
	}

	@Override
	public List<MaintSchedulesProcessed> getErrorList() {
		return maintSchedulesProcessedDAO.getErrors();
	}

	@Override
	public long getErrorListCount() {
		return maintSchedulesProcessedDAO.getErrorsCount();
	}

	
	@Override
	public List<MaintSchedulesProcessed> getPreviousPrintList(long daysToGoBack) {
		return maintSchedulesProcessedDAO.getPreviousPrints(daysToGoBack);
	}

	@Override
	public long getPreviousPrintListCount(long daysToGoBack) {
		return maintSchedulesProcessedDAO.getPreviousPrintsCount(daysToGoBack);
	}
	
	@Override
	public ClientContactVO getAllClientContactVOsForSchedules(ExternalAccount externalAccount, FleetMaster fleetMaster) throws Exception {
		try {
			if(clientContactDAO.getAllClientContactVOs(fleetMaster, externalAccount, null, "Vehicle Schedule", "MAINT").size() > 0) {
				return clientContactDAO.getAllClientContactVOs(fleetMaster, externalAccount, null, "Vehicle Schedule", "MAINT").get(0);	
			} else {
				return null;
			}
		} catch(Exception ex) {
			throw new MalException("generic.error.occured.while", new String[] { "retrieving Schedules POC" }, ex);
		}
	}

	@Override
	public void setScheduleToPrintNext(MaintSchedulesProcessed schedule) throws MalException {
		Date today = Calendar.getInstance().getTime();
		schedule.setExpectedPrintDate(today);
		schedule.setDateGenerated(null);
		saveScheduleProcessRecord(schedule);
	}

	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteMaintSchedulesProcessed(MaintSchedulesProcessed maintScheduleProcessed) throws Exception {
		try {
			maintSchedulesProcessedDAO.delete(maintScheduleProcessed);
			vehicleScheduleService.deleteVehicleSchedule(maintScheduleProcessed.getVehicleSchedule());
		} catch (Exception ex) {
			logger.error(ex);
			throw new MalException("generic.error.occured.while", new String[] { "deleting a Maint Schedule Processed" }, ex);
		}
	}	
	
	@Override
	public MaintSchedulesProcessed determineExpectedPrintDate(MaintSchedulesProcessed processedRecord) throws MalDataValidationException, Exception {
		 VehicleOrderStatusSearchCriteriaVO searchCriteria = new VehicleOrderStatusSearchCriteriaVO();
		 Date expectedPrintDate = null;
		 
		 try {

			 if(processedRecord.getFleetMaster().getUnitNo() != null){
				ContractLine contractLine = contractService.getCurrentOrFutureContractLine(processedRecord.getFleetMaster());
				if(contractLine != null) {
					contractLine = contractService.getQuotationModelOnContractLine(contractLine.getClnId());
					if (contractLine.getContract().getDescription().startsWith(FORMAL_EXT)) {	
						expectedPrintDate = MALUtilities.clearTimeFromDate(contractLine.getStartDate());
						processedRecord.setExpectedPrintDate(expectedPrintDate);
					} else if (quotationService.getLeaseType(contractLine.getQuotationModel().getQmdId()).equals(QuotationService.MAX_FLEET_SERVICES)){
						if (contractLine.getInServDate().before(new Date())){
							expectedPrintDate = MALUtilities.clearTimeFromDate(new Date());
						} else {
							expectedPrintDate = MALUtilities.clearTimeFromDate(contractLine.getInServDate());
						}
						processedRecord.setExpectedPrintDate(expectedPrintDate);
					} else {
						searchCriteria.setUnitNo(processedRecord.getFleetMaster().getUnitNo());
						Doc doc = documentService.searchMainPurchaseOrder(searchCriteria);
						if( doc != null){
							List<SupplierProgressHistory> progressHistoryForInServiceList = supplierProgressHistoryDAO.findSupplierProgressHistoryForDocAndType(doc.getDocId(), UnitProgressCodeEnum.IN_SERV.getCode());
							SupplierProgressHistory progressHistoryForInService = null;
							if(progressHistoryForInServiceList!= null && progressHistoryForInServiceList.size() > 0){
								progressHistoryForInService = progressHistoryForInServiceList.get(0);
							}
							if (!MALUtilities.isEmpty(progressHistoryForInService) && progressHistoryForInService.getActionDate() != null){
								if (progressHistoryForInService.getActionDate().before(new Date())){
									expectedPrintDate = MALUtilities.clearTimeFromDate(new Date());
								} else {
									expectedPrintDate = MALUtilities.clearTimeFromDate(progressHistoryForInService.getActionDate());
								}
								processedRecord.setExpectedPrintDate(expectedPrintDate);
							}
						}
					}
					
				} else {
					throw new VehicleScheduleError(VehicleScheduleError.CONTRACT_LINE_MISSING);
				}
			}
		} catch (MalBusinessException mbe) {
			logger.error(mbe);
			throw mbe;
		}
		return processedRecord;
	}

	/**
	 * Determines next initial starting interval for a vehicle�s initial schedule and returns.
	 */
	public int getInitialStartingInterval(FleetMaster fleetMaster, VehicleSchedule vehicleSchedule) throws MalBusinessException {
		try {
			Long latestOdoReading = fleetMasterService.getScheduleLatestOdoReading(fleetMaster.getFmsId());
			int baseInterval = vehicleSchedule.getMasterSchedule().getDistanceFrequency();
			return calculateNextInterval(latestOdoReading, baseInterval);
		} catch (Exception ex) {
			if (ex instanceof MalBusinessException) {
				throw (MalBusinessException) ex;
			}
			throw new MalBusinessException("generic.error.occured.while", new String[] { "geting initial starting interval" }, ex);
		}
	}
	
	/**
	 * Determines next reprint starting interval for a vehicle�s initial schedule and returns.
	 */
	public int getReprintStartingInterval(FleetMaster fleetMaster, VehicleSchedule vehicleSchedule) throws MalBusinessException {
		try {
			//get last completed interval
			Integer intervalMultiple = vehicleScheduleService.getLastCompletedInterval(vehicleSchedule);
			Long lastCompletedInterval = intervalMultiple != null ? new Long(intervalMultiple):null;
			int baseInterval = vehicleSchedule.getMasterSchedule().getDistanceFrequency();
			return calculateNextInterval(lastCompletedInterval, baseInterval);
		} catch (Exception ex) {
			if (ex instanceof MalBusinessException) {
				throw (MalBusinessException) ex;
			}
			if(ex instanceof MalException){
				throw new MalBusinessException("plain.message",ex);
			}
			throw new MalBusinessException("generic.error.occured.while", new String[] { "geting reprint starting interval" }, ex);
		}
	}
	private static int	calculateNextInterval(Long lastCompletedInterval,int baseInterval){
		if(lastCompletedInterval != null ){
			BigDecimal odoReadingAsBig = new BigDecimal(lastCompletedInterval);
			BigDecimal	baseInrvlAsBig = new BigDecimal(baseInterval);
			BigDecimal  value = odoReadingAsBig.divide(baseInrvlAsBig,BigDecimal.ROUND_CEILING);
			BigDecimal nextInterval = baseInrvlAsBig.multiply(value);
			nextInterval = nextInterval.round(new MathContext(BigDecimal.ROUND_CEILING));
			return nextInterval.intValueExact();
		}
		return baseInterval;
	}
	
	public List<LeaseElement> getAllMaintenanceLeaseElements() {
		return leaseElementDAO.findAllMaintenanceLeaseElements();
	}


	/**
	 * The conversion factor is the value used to convert from a base of miles into kilometers.
	 * Currently this is only needed for vehicles that have a garaged address in Canada
	 */
	@Override
	public BigDecimal getConversionFactorForSchedule(MaintSchedulesProcessed maintSchedulesProcessed) {
		BigDecimal conversionFactor = new BigDecimal(1);
		Driver driver = driverService.getActiveDriverForUnit(maintSchedulesProcessed.getFleetMaster());
		if(driver != null) {
			if(driver.getGaragedAddress().getCountry().getCountryCode().equalsIgnoreCase("CN")) {
				conversionFactor = CONVERSION_FACTOR_FOR_KILOMETERS;	
			}
		}
		return conversionFactor;	
	}


	/**
	 * Given an interval code such as "MAFSA12345", this will return the sum of the max cost
	 * for all tasks associated with that interval.  If anything is incorrect with the incoming
	 * interval code, zero will be returned.
	 */
	@Override	
	public BigDecimal getMaxCostForInterval(String intervalCode) {
		BigDecimal maxCost = BigDecimal.ZERO;
		try{
			List<ServiceTask> tasks = getServiceTaskListForInterval(intervalCode);
			for(ServiceTask st : tasks) {
				maxCost = maxCost.add(st.getCost());
			}	
		}
		catch(Exception ex){
			logger.error(ex," Interval Code = "+intervalCode);
			throw new MalException("generic.error.occured.while", new String[] {" Get max cost for interval = "+intervalCode}, ex);
		}
		return maxCost;
	}

	/**
	 * Given a Driver Authorization code such as "MAFS00987654", this will return the driver 
	 * authorization limit for the account.  If anything is incorrect with the incoming
	 * code, zero will be returned.
	 */
	@Override	
	public BigDecimal getLimitForDriverAuthorizationCode(String authorizationCode) throws Exception {
		BigDecimal limit = BigDecimal.ZERO;

		String unitNo = authorizationCode.substring(4);
		FleetMaster fleetMaster = fleetMasterService.findByUnitNo(unitNo);
		if(fleetMaster != null) {
			ContractLine contractLine = contractService.getCurrentOrFutureContractLine(fleetMaster);
			if(contractLine != null) {
				if(contractLine.getContract().getExternalAccount() != null) {
					limit = quotationService.getDriverAuthorizationLimit(contractLine.getContract().getExternalAccount().getExternalAccountPK().getCId(), 
							contractLine.getContract().getExternalAccount().getExternalAccountPK().getAccountType(), 
							contractLine.getContract().getExternalAccount().getExternalAccountPK().getAccountCode(), 
							unitNo);
				}
			}
		}
		return limit != null ? limit : BigDecimal.ZERO;
	}

	
	/**
	 * Given an interval code such as "MAFSA12345", this will return the list of tasks
	 * associated with that interval.  If anything is incorrect with the incoming
	 * interval code, an empty task list will be returned.
	 */
	@Override
	public List<ServiceTask> getServiceTaskListForInterval(String intervalCode) {
		List<ServiceTask> taskList = new ArrayList<ServiceTask>();
		int intervalIndex = vehicleScheduleService.getIntervalIndex(intervalCode);
		String last5 = intervalCode.length() == 10 ? intervalCode.substring(5) : intervalCode.substring(6); 
		VehicleSchedule vehicleSchedule = vehicleScheduleDAO.getVehicleScheduleBySequence(Long.valueOf(last5));
		if(vehicleSchedule != null) {
			int intervalCount = 1;
			int i = 1;
			while(intervalCount <= intervalIndex) {
				if(vehicleSchedule.getMasterSchedule().getDistanceFrequency()*i >= vehicleSchedule.getStartOdoReading()) {
					taskList = getServiceTasksForInterval(i, vehicleSchedule.getMasterSchedule());
					if(taskList.size() > 0) {
						intervalCount++;
					}
				}
				i++;
				if(i>1000) {  //just making sure the loop ends if something is messed up with the data
					taskList.clear();
					break;
				}
			}
		}
		return taskList;
	}

	
	@Override	
	public List<ServiceTask> getServiceTasksForInterval(int intervalIndex, MasterSchedule masterSchedule) {
		List<ServiceTask> taskList = new ArrayList<ServiceTask>();
		for(MasterScheduleInterval msi : masterSchedule.getMasterScheduleIntervals()) {
			if(msi.getIntervalMultiple() == intervalIndex) {
				taskList.add(msi.getServiceTask());
			} else if(msi.getIntervalMultiple()!= 0 && (intervalIndex % msi.getIntervalMultiple() == 0) && (msi.getRepeatFlag().equalsIgnoreCase("Y"))) {
				taskList.add(msi.getServiceTask());
			}
		}
		return taskList;
	}
	
	@Override
	@Transactional	
	public void saveOrUpdateFleetMasterVinDetail(FleetMasterVinDetails fleetMasterVinDetails) throws Exception {
		
		try {
			fleetMasterVinDetailsDAO.saveAndFlush(fleetMasterVinDetails);
		} catch (Exception ex) {
			logger.error(ex);
			throw new MalException("generic.error.occured.while", new String[] { "saving vin details fuel type" }, ex);
		}
	}		
	
	
	// special "Top" intervals will not have an integer value for the interval description so they will not parse but because they are at the top, the value
	//   to return should be 0.

	/**
	 * Given an index and master schedule this will find the associate mileage value for that index.
	 * Special "Top" intervals do not have a mileage associated with them so will return a 0 as they are before any real miles on the schedule.
	 */
	@Override
	public int getIntervalIndexMileage(int index, MasterSchedule masterSchedule, int startingOdo) {
		int miles = 0;
		List<MaintenanceScheduleIntervalPrintVO> maintScheduleVOList = getMasterScheduleIntervalList(masterSchedule.getMschId(), startingOdo, new BigDecimal(1));

		if(MALUtilities.isEmptyString(maintScheduleVOList.get(0).getIntervalDescription().trim()) && maintScheduleVOList.size() > 1) {
			index++;   // bypass the first row which isn't a real interval
		}
		
		if(maintScheduleVOList.get(index) != null) {
			try {    
				miles = Integer.parseInt(maintScheduleVOList.get(index).getIntervalDescription());
			} catch(Exception e) {
				miles = 0;
			}
		}
		return miles;
	}
	
}
