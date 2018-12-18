package com.mikealbert.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.dao.ExternalAccountDAO;
import com.mikealbert.data.dao.MaintSchedulesBatchDAO;
import com.mikealbert.data.dao.MaintSchedulesProcessedDAO;
import com.mikealbert.data.dao.VehicleScheduleDAO;
import com.mikealbert.data.dao.VehicleScheduleIntervalDAO;
import com.mikealbert.data.entity.ContractLine;
import com.mikealbert.data.entity.Doc;
import com.mikealbert.data.entity.Driver;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.MaintSchedulesBatch;
import com.mikealbert.data.entity.MaintSchedulesProcessed;
import com.mikealbert.data.entity.MasterSchedule;
import com.mikealbert.data.entity.VehicleSchedule;
import com.mikealbert.data.entity.VehicleScheduleInterval;
import com.mikealbert.data.vo.ArCreationVO;
import com.mikealbert.exception.MalDataValidationException;
import com.mikealbert.exception.MalException;
import com.mikealbert.service.vo.MaintenanceScheduleIntervalPrintVO;
import com.mikealbert.util.MALUtilities;

@Service("vehicleScheduleService")
public class VehicleScheduleServiceImpl implements VehicleScheduleService {

	private MalLogger logger = MalLoggerFactory.getLogger(this.getClass());

	@Resource
	private VehicleScheduleDAO vehicleScheduleDAO;
	
	@Resource
	private	VehicleScheduleIntervalDAO	vehicleScheduleIntervalDAO;
	
	@Resource
	private	MaintSchedulesBatchDAO	maintSchedulesBatchDAO;
	
	@Resource
	private	MaintSchedulesProcessedDAO	maintSchedulesProcessedDAO;	
	@Resource
	private MaintenanceScheduleService	maintenanceScheduleService;
	
	@Resource
	private	DriverService	driverService;
	@Resource
	private	QuotationService quotationService;
	@Resource
	private	ExternalAccountDAO externalAccountDAO;
	@Resource
	private	ArService aRService;
	@Resource
	private	ContractService contractService;

	
	@Override
	public Long getVehSchdByMasterIdCount(MasterSchedule ms) {
		return vehicleScheduleDAO.getVehSchdCountByMasterSchd(ms.getMschId());
	}
 
	/**
	 * This method saves a VehicleSchedule object in database. It does not treat
	 * VehicleScheduleIntervals specially. VehicleScheduleIntervals will be
	 * saved or updated based on its state.If new VehicleScheduleIntervals are
	 * assigned in VehicleSchedule,it will be saved else update. Transaction
	 * will be rolled back in case any exception occurs.
	 */
	@Transactional(rollbackFor = Exception.class)
	public void saveVehicleSchedule(VehicleSchedule vehicleSchedule) throws MalDataValidationException, MalException {
		try {		
			vehicleScheduleDAO.save(vehicleSchedule);			
		} catch (Exception ex) {
			logger.error(ex,vehicleSchedule != null ? "FMS_ID = "+vehicleSchedule.getFleetMaster().getFmsId():"Vehicle Schedule is NULL" );
			throw new MalException("generic.error.occured.while", new String[] { "saving vehicle schedule" }, ex);
		}
	}

	/**
	 * This method saves a VehicleSchedule object in
	 * database.VehicleScheduleIntervalList is being treated specially to
	 * save,update or delete based on its value through cascade setting.
	 * VehicleScheduleIntervals will be saved or updated based on its state.If
	 * new VehicleScheduleIntervals are assigned in VehicleSchedule,it will be
	 * saved else update. Transaction will be rolled back in case any exception
	 * occurs.
	 */


	/**
	 * This method fetch next vehicle schedule sequence from data base by calling a sequence.
	 */
	public Long getNextVehicleScheduleSequence() {
		return vehicleScheduleDAO.getNextVehicleScheduleSequence().longValue();
	}

	@Override
	public boolean hasActiveVehicleSchedule(FleetMaster fleetMaster) {
		boolean found = false;
		Date today = Calendar.getInstance().getTime();
		List<VehicleSchedule> list = vehicleScheduleDAO.getVehSchdByFmsId(fleetMaster.getFmsId());
		for(VehicleSchedule vs : list) {
			if(vs.getActiveTo() == null || !vs.getActiveTo().before(today) ) {
				found = true;
			}
		}
		return found;
	}
	
	public  String calculateIntervalCode(Integer interval) {
		if (MALUtilities.isEmpty(interval)) {
			return null;
		}
		interval = interval - 1;
		CharSequence css = "ACEFGHJKLMNPRTUVWXY";
		// We will not use the following letters: B D I O Q S Z due to the
		// possibility of them being confused with numeric digits. Skip these
		// values.
		if (interval < 19) {
			return css.charAt(interval) + "";
		} else {
			Integer subIntervalForDecode = interval - 19;
			if (subIntervalForDecode >= 19) {
				subIntervalForDecode = subIntervalForDecode % 19;

			}
			Integer actualIntervalForDecode = (interval - 19) / 19;
			//System.out.println("MainIndex:" + actualIntervalForDecode + " SubIndex:" + subIntervalForDecode);
			return css.charAt(actualIntervalForDecode) + "" + css.charAt(subIntervalForDecode) + "";
		}

	}
	
	public String getAuthorizationNumber(Long vehicleSchSequence, String interval) {
		StringBuffer authNumBuffer = new StringBuffer("");
		authNumBuffer.append(AUTHORIZATION_NUMBER_PREFIX);
		authNumBuffer.append(interval);
		authNumBuffer.append(vehicleSchSequence);
		return authNumBuffer.toString();
	}
	
	/**
	 * This method validates authorization number by validating each part individually
	 * return true if valid code other wise false
	 */
	
	public boolean isValidAuthorizationNumber(String authorizationNumber) {
		if (MALUtilities.isEmpty(authorizationNumber)) {
			return false;
		}
		if (authorizationNumber.length() < 10 || authorizationNumber.length() > 11) {
			return false;
		}
		String prefixPart = authorizationNumber.substring(0, 4);
		String intervalPart = authorizationNumber.substring(4, authorizationNumber.length() == 10 ? 5 : 6);
		String vehSeqPart = authorizationNumber.substring(authorizationNumber.length() == 10 ? 5 : 6);
		if (!AUTHORIZATION_NUMBER_PREFIX.equals(prefixPart)) {
			return false;
		}
		// perform a case-insensitive ASCII match against the reserved characters
		if (!Pattern.matches("(?i)[ACEFGHJKLMNPRTUVWXY]+", intervalPart)) {
			return false;
		}

		if (!MALUtilities.isNumber(vehSeqPart)) {
			return false;
		}
		return true;
	}
	
	/**
	 * This method will store a vehicle schedule interval for respective vehicle
	 * schedule. It will add new one with existing list of vehicle schedule
	 * interval.
	 * Returns updated VehicleSchedule
	 */

	
	
	public List<VehicleSchedule>	getVehicleScheduleByUnitNumber(String unitNumber) throws MalException{
		try{
			return vehicleScheduleDAO.getVehSchdByUnitNumber(unitNumber);
		}catch(Exception ex){
			logger.error(ex,"Unit Number = "+unitNumber);
			throw new MalException("generic.error.occured.while", new String[] { " getting vehicle schedule" }, ex);
		}
		
	}
	
	public List<VehicleSchedule> getVehicleScheduleByFmsId(long fmsId) throws MalException{
		try{
			return vehicleScheduleDAO.getVehSchdByFmsId(fmsId);
		}catch(Exception ex){
			logger.error(ex,"FMS_ID = "+fmsId);
			throw new MalException("generic.error.occured.while", new String[] { " getting vehicle schedule" }, ex);
		}
		
	}

		
	public Integer	getLastCompletedInterval(VehicleSchedule vehicleSchedule)throws MalException{
		Integer lastCompletedInterval = null;
		try{
			if(vehicleSchedule != null){
				List<VehicleScheduleInterval> intervals = vehicleScheduleIntervalDAO.findByVehicleSchedule(vehicleSchedule.getVschId());
				if(intervals != null && !intervals.isEmpty()){
					//VehicleScheduleInterval latestVehicleScheduleInterval = intervals.get(0);
					for (VehicleScheduleInterval vehicleScheduleInterval : intervals) {
						int lastCompletedIntervalTemp = getIntervalIndex(vehicleScheduleInterval.getAuthorizationNo());
						Integer compareInteger = Integer.valueOf(!MALUtilities.isEmpty(lastCompletedInterval) ? lastCompletedInterval : 0);
						if(lastCompletedIntervalTemp >  compareInteger.intValue() ){
							lastCompletedInterval = lastCompletedIntervalTemp;
						}
						 
					}
					
				}
			}
		}catch(Exception ex){
			logger.error(ex,vehicleSchedule!=null ? "Vehicle Schedule ID = "+vehicleSchedule.getVschId():"Vehicle Schedule is Null");
			throw new MalException("generic.error.occured.while", new String[] { " getting last completed interval" }, ex);
		}
		return lastCompletedInterval;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public 	MaintSchedulesBatch	getOpenBatch() throws MalException{
		try{
			MaintSchedulesBatch maintSchedulesBatch = maintSchedulesBatchDAO.findOpenBatch();
			if(maintSchedulesBatch == null){
				//No current open batch is found, create a new batch as open batch
				maintSchedulesBatch = new MaintSchedulesBatch();
				Long batchId = maintSchedulesBatchDAO.getNextBatchId().longValue();
				maintSchedulesBatch.setBatchId(batchId);
				maintSchedulesBatch.setStatus(MAINT_SCH_BATCH_STATUS_OPEN);
				maintSchedulesBatch.setBatchName("VehicleScheduleBatch"+batchId); 
				maintSchedulesBatch.setDescription("Vehicle Schedule Batch"+batchId); 
				maintSchedulesBatchDAO.save(maintSchedulesBatch);
			}
			return maintSchedulesBatch;
		}catch(Exception ex){
			logger.error(ex);
			throw new MalException("generic.error.occured.while", new String[] { " getting open batch" }, ex); 
		}
		
	}
	
	@Transactional(rollbackFor = Exception.class)
	public MaintSchedulesBatch	updateMaintScheduleBatch(Long maintSchedulesBatchId, String status,Date completionDate) throws MalException{
		try{
			if(MALUtilities.isEmpty(maintSchedulesBatchId)){
				throw new MalException("required.field", new String[] { "Maint Schedules Id" });
			}
			if(MALUtilities.isEmpty(status)){
				throw new MalException("required.field", new String[] { "status" });
			}
			if(MAINT_SCH_BATCH_STATUS_COMPLETE.equals(status) && MALUtilities.isEmpty(completionDate)){
				throw new MalException("required.field", new String[] { "completion date" });
			}
			MaintSchedulesBatch maintSchedulesBatch = maintSchedulesBatchDAO.findById(maintSchedulesBatchId).orElse(null);
			if(maintSchedulesBatch != null){
				if(!MALUtilities.isEmpty(status))
					maintSchedulesBatch.setStatus(status);
				if(!MALUtilities.isEmpty(completionDate)){
					if(MAINT_SCH_BATCH_STATUS_COMPLETE.equals(status)){
						maintSchedulesBatch.setCompletionDate(completionDate);
					}
				}
				maintSchedulesBatch = maintSchedulesBatchDAO.save(maintSchedulesBatch);
				return maintSchedulesBatch;
			}else{
				throw new MalException("notfound", new String[] { "Maint Schedules Batch" });
			}
		}
		catch(Exception ex){
			logger.error(ex,"Maintain Schedule Batch Id = "+maintSchedulesBatchId);
			throw new MalException("generic.error.occured.while", new String[] { "updating maint schedules batch" }, ex); 
		}
		
	}
	
	@Transactional(rollbackFor = Exception.class)
	public MaintSchedulesBatch	saveMaintSchedulesBatch(MaintSchedulesBatch maintSchedulesBatch){
		if(maintSchedulesBatch != null){
			return maintSchedulesBatchDAO.save(maintSchedulesBatch);
		}else{
			return null;
		}
	}

	
	/**
	 * This method will use the given list to calculate the interval index represented by the incoming interval code
	 * It bases this off the interval_codes list which is 19 entries long.  So an A by itself is the first entry
	 * and will return 1 (index+1).  Y will return 19 (index+1).  AA will return 20 because the first A represents 
	 * having gone through the list once (so 19 entries) and the second A represents the first entry after starting
	 * over.
	 */
	@Override
	public  int   getIntervalIndex(String interval) {
		if(interval.length() == 10) {
			return INTERVAL_CODES.indexOf(interval.substring(4, 5))+1;
		} else {
			return ((INTERVAL_CODES.indexOf(interval.substring(4, 5))+1)*19) + (INTERVAL_CODES.lastIndexOf(interval.substring(5, 6))+1);
		}
		
	}
	
	@Override
	public MaintSchedulesBatch findBatchById(Long id) {
		return maintSchedulesBatchDAO.findById(id).orElse(null);
	}
	
	public Boolean	isAuthCodeUsed(String authCode){
		List<VehicleScheduleInterval> list = vehicleScheduleIntervalDAO.findByAuthCode(authCode);
		if(list != null && !list.isEmpty()){
			return true;
		}else{
			return false;
		}
	}

	@Override
	public boolean isValidAuthNumberForFmsId(String authorizationNumber, long fmsId) {
		boolean retVal = false;
		// if the format is valid
		boolean x = this.isValidAuthorizationNumber(authorizationNumber);
		if(x){
			// and the unit has this schedule (even if it's in the past)
			List<VehicleSchedule> vehSchdList= this.getVehicleScheduleByFmsId(fmsId);
			for(VehicleSchedule vehSchd : vehSchdList){
				if(vehSchd.getVehSchSeq().longValue() == getVehicleSeqFromAuthNbr(authorizationNumber)){
					retVal = true;
				}
			}
		}

		return retVal;
	}
	@Override
	public Long getVehicleSeqFromAuthNbr(String authorizationNumber){
		Long retVal = null;
		if(this.isValidAuthorizationNumber(authorizationNumber)){
			String vehSeqPart = authorizationNumber.substring(authorizationNumber.length() == 10 ? 5 : 6);
			retVal = Long.parseLong(vehSeqPart);
		}
		return retVal;
	}

	@Override
	public VehicleSchedule getVehicleScheduleForAuthNumber(String authNumber) {
		
		VehicleSchedule retVal = null;
		
		try{
			Long vehSeq = this.getVehicleSeqFromAuthNbr(authNumber);
			
			retVal = vehicleScheduleDAO.getVehicleScheduleBySequence(vehSeq);
			
		}catch(Exception ex){
			logger.error(ex, "Authorization Number = "+authNumber);
			throw new MalException("generic.error.occured.while", new String[] { " getting vehicle schedule" }, ex);
		}
		
		
		return retVal;
	}

	@Transactional(rollbackFor = Exception.class)
	public void setupRePrintVehicleSchedule(FleetMaster fleetMaster, String userName) {
		try {
			MaintSchedulesProcessed maintSchedulesProcessed = maintSchedulesProcessedDAO.findMaintScheduleProcessRecordByFmsId(fleetMaster.getFmsId());
			if(maintSchedulesProcessed != null){
				maintSchedulesProcessed.setDateGenerated(null);
				maintSchedulesProcessed.setMaintSchedulesBatch(null);
				maintSchedulesProcessedDAO.save(maintSchedulesProcessed);
				
				ArCreationVO aRVO = getReprintChargeValues(fleetMaster);
				aRVO.setUserName(userName);
				Long docId = aRService.createInvoiceARForType(ArService.MSS_REPRINT, aRVO);
				logger.debug("Created INVOICEAR with Doc Id: " + docId);
			}
		} catch (MalException ex) {
			throw ex;
		} catch (Exception ex) {
			logger.error(ex,fleetMaster!=null ? "FMS_ID = "+fleetMaster.getFmsId():"Fleet Master is Null");
			throw new MalException("generic.error.occured.while", new String[] { " setting up reprint vehicle schedule" }, ex);
		}

	}
	
	@Override
	public ArCreationVO getReprintChargeValues(FleetMaster fleetMaster) throws Exception {
		
		ArCreationVO aRVO = new ArCreationVO();
		Date date = new Date();
		ExternalAccount externalAccount;
		
		aRVO.setcId(1l);
		aRVO.setDocDate(date);
		aRVO.setLineDescription("Unit No " + fleetMaster.getUnitNo() + " - Replacement maintenance schedule fee");

		ContractLine contractLine = contractService.getCurrentOrFutureContractLine(fleetMaster);
		if (contractLine != null){
			externalAccount = contractLine.getContract().getExternalAccount();
		} else {
			throw new MalException("Error occurred trying to set up reprint - No contract line found");
		}
		ContractLine contractLineWithQmd = contractService.getQuotationModelOnContractLine(contractLine.getClnId());
		aRVO.setExternalAccount(externalAccount);
		aRVO.setDriver(driverService.getActiveDriverForUnit(fleetMaster));
		aRVO.setFleetMaster(fleetMaster);
		// using this method and passing qmd id and quote profile id because the stored procedure in Willow is broken and will only use the 
		// external account found through the qmd lookup and ignores the passed in external account.
		Double amt = quotationService.getFinanceParam(ArService.MSS_REPRINT_PARAMETER_KEY, contractLineWithQmd.getQuotationModel().getQmdId(), 
				contractLineWithQmd.getQuotationModel().getQuotation().getQuotationProfile().getQprId(), date, true, externalAccount);
		aRVO.setAmount(new BigDecimal(amt));
		return aRVO;
		
	}
	
	public List<VehicleScheduleInterval>	getCompletedIntervalsWithPO(VehicleSchedule vehicleSchedule) throws MalException{
		List<VehicleScheduleInterval> completedIntervals = new ArrayList<VehicleScheduleInterval>();
		try{
			if(vehicleSchedule != null){
				List<VehicleScheduleInterval> intervals = vehicleScheduleIntervalDAO.findByVehicleSchedule(vehicleSchedule.getVschId());
				if(intervals != null && !intervals.isEmpty()){
					for (VehicleScheduleInterval vehicleScheduleInterval : intervals) {
						//if(!MALUtilities.isEmpty(vehicleScheduleInterval.getDocId())){
							completedIntervals.add(vehicleScheduleInterval);
						//}
					}
					Collections.sort(completedIntervals,intervalComparator);
				}
			}
		}catch(Exception ex){
			logger.error(ex,vehicleSchedule!=null ? "Vehicle Schedule Id = "+vehicleSchedule.getVschId()+" FMS_ID = "+vehicleSchedule.getFleetMaster().getFmsId():"Vehicle Schedule is Null");
			throw new MalException("generic.error.occured.while", new String[] { " getting completed interval with PO" }, ex);
		}
		return completedIntervals;
	}
	
	Comparator<VehicleScheduleInterval> intervalComparator = new Comparator<VehicleScheduleInterval>() {
		public int compare(VehicleScheduleInterval interval1, VehicleScheduleInterval interval2) {
			if(interval1 == null && interval2 == null)
				return 0;
			Integer intervalIndex1 = getIntervalIndex(interval1.getAuthorizationNo());
			Integer intervalIndex2 = getIntervalIndex(interval2.getAuthorizationNo());
			return intervalIndex1.compareTo(intervalIndex2);
		}
	};

	@Override
	public VehicleSchedule getVehicleScheduleById(long id) throws MalException {
		try{
			return vehicleScheduleDAO.findById(id).orElse(null);
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while", new String[] { " finding VehicleSchedule by Id" + id }, ex);
		}
	}

	@Override
	public VehicleSchedule getVehicleScheduleForVehSchedSeq(
			Long vehicleScheduleSeq) {
		
		VehicleSchedule retVal = null;
		
		try{
			if (vehicleScheduleSeq != null){
				retVal = vehicleScheduleDAO.getVehicleScheduleBySequence(vehicleScheduleSeq);
			}
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while", new String[] { " finding VehicleSchedule by vehicleScheduleSeq" + vehicleScheduleSeq }, ex);
		}
		
		return retVal;
	}

	@Transactional(rollbackFor = Exception.class)
	public void saveVehicleScheduleInterval(VehicleSchedule vehicleSchedule,
			String authNumber, Doc doc) throws MalException {

		try {
			
			VehicleScheduleInterval vehicleScheduleInterval = null;
					
			// find and update if it's already there; it can be "found" by authNumber or alternatively by docNo
			for(VehicleScheduleInterval interval: vehicleSchedule.getVehicleScheduleIntervals()){
				if(interval.getAuthorizationNo().equalsIgnoreCase(authNumber) || interval.getDocNo().equalsIgnoreCase(doc.getDocNo())){
					vehicleScheduleInterval = interval;
					break;
				}
			}
			
			if(MALUtilities.isEmpty(vehicleScheduleInterval)){
				vehicleScheduleInterval = new VehicleScheduleInterval();
				vehicleScheduleInterval.setVehicleSchedule(vehicleSchedule);
				vehicleSchedule.addVehicleScheduleIntervals(vehicleScheduleInterval);
			}
			
			vehicleScheduleInterval.setAuthorizationNo(authNumber);			
			vehicleScheduleInterval.setDocId(doc.getDocId());
			vehicleScheduleInterval.setDocNo(doc.getDocNo());	
			
			saveVehicleSchedule(vehicleSchedule);
			
		} catch (Exception ex) {
			logger.error(ex,vehicleSchedule!=null?"FMS_ID = "+vehicleSchedule.getFleetMaster().getFmsId():"Vehicle Schedule is Null");
			throw new MalException("generic.error.occured.while", new String[] { " saving vehicle schedule intervals" }, ex);
		}
		
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void saveVehicleScheduleInterval(
			VehicleScheduleInterval vehicleScheduleInterval)
			throws MalException {
		try {
			vehicleScheduleIntervalDAO.saveAndFlush(vehicleScheduleInterval);
		} catch (Exception ex) {
			logger.error(ex,vehicleScheduleInterval!=null ? "Vehicle Schedule ID = "+vehicleScheduleInterval.getVehicleSchedule().getVschId()+" FMS_ID = "+vehicleScheduleInterval.getVehicleSchedule().getFleetMaster().getFmsId():"Vehicle Schedule Intervals is NULL");
			throw new MalException("generic.error.occured.while", new String[] { " saving vehicle schedule intervals" }, ex);
		}			
	}

	@Override
	public VehicleScheduleInterval getVehicleScheduleIntervalForAuthNumber(
			String authNumber) throws MalException{
		VehicleScheduleInterval retVal = null;
		if(!MALUtilities.isEmpty(authNumber)){
			try {
				//TODO: why a list?!
				retVal = vehicleScheduleIntervalDAO.findByAuthCode(authNumber).get(0);
				return retVal;
			} catch (Exception ex) {
				logger.error(ex,"Authorization Number = "+authNumber);
				throw new MalException("Error while getting VehicleScheduleInterval for authNumber: "+ authNumber , ex);
			}
		}
		
		throw new MalException("Method getVehicleScheduleIntervalForAuthNumber received NULL authNumber as Input.");
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void deleteVehicleScheduleInterval(
			VehicleScheduleInterval vehicleScheduleInterval)
			throws MalException {
		try {
			List<VehicleScheduleInterval> inList = new ArrayList<VehicleScheduleInterval>();
			inList.add(vehicleScheduleInterval);
			vehicleScheduleIntervalDAO.deleteInBatch(inList);
		} catch (Exception ex) {
			logger.error(ex,vehicleScheduleInterval!=null ? "Vehicle Schedule Interval = "+vehicleScheduleInterval.getVehSchIntervalId():"Vehicle Schedule Interval is Null");
			throw new MalException("generic.error.occured.while", new String[] { " deleting vehicle schedule intervals" }, ex);
		}
	}
	@Transactional(rollbackFor = Exception.class)
	@Override
	public void deleteVehicleSchedule(VehicleSchedule vehicleSchedule)throws MalException {
		try {
			if(vehicleSchedule != null){
				List<VehicleScheduleInterval> inList = vehicleSchedule.getVehicleScheduleIntervals();
				if(inList != null && !inList.isEmpty()){
					vehicleScheduleIntervalDAO.deleteInBatch(inList);
				}
				vehicleScheduleDAO.delete(vehicleSchedule);
			}
			
		} catch (Exception ex) {
			logger.error(ex,vehicleSchedule!=null ?"Vehicle Schedule Id = "+vehicleSchedule.getVschId():"Vehicle Schedule is NULL");
			throw new MalException("generic.error.occured.while", new String[] { " deleting vehicle schedule" }, ex);
		}
	}

	
	
	@Override
	public VehicleScheduleInterval getVehicleScheduleIntervalForDocNo(
			String docNo) {
		VehicleScheduleInterval retVal = null;
		
		try {
			//TODO: why a list?!
			List<VehicleScheduleInterval> intervals = vehicleScheduleIntervalDAO.findByDocNo(docNo);
			if(intervals != null && !intervals.isEmpty()){
				retVal = intervals.get(0);
			}
			
		} catch (Exception ex) {
			logger.error(ex,"Doc No = "+docNo);
		}

		return retVal;
	}
	/**
	 * Method to get last used vehicle schedule interval for given fmsId
	 * @param fmsId
	 * @return VehicleScheduleInterval, last used vehicle schedule interval
	 * @throws MalException
	 */
	public VehicleScheduleInterval	getLastUsedVehSchInterval(Long fmsId) throws MalException{
		try{
			VehicleScheduleInterval lastUsedInterval = null;
			List<VehicleSchedule> vehScheduleList =  getVehicleScheduleByFmsId(fmsId);
			for (VehicleSchedule vehicleSchedule : vehScheduleList) {
				Date activeToDate = vehicleSchedule.getActiveTo();
				if (activeToDate != null && activeToDate.before(new Date())) {
					continue;
				}
				List<VehicleScheduleInterval> intervals = vehicleScheduleIntervalDAO.findByVehicleSchedule(vehicleSchedule.getVschId());
				if(intervals != null && !intervals.isEmpty()){
					Integer lastCompletedInterval = 0;
					for (VehicleScheduleInterval vehicleScheduleInterval : intervals) {
						int lastCompletedIntervalTemp = getIntervalIndex(vehicleScheduleInterval.getAuthorizationNo());;
						if(lastCompletedIntervalTemp > lastCompletedInterval.intValue() ){
							lastCompletedInterval = lastCompletedIntervalTemp;
							lastUsedInterval =  vehicleScheduleInterval;
						} 
					}
					
				}
			}
			return lastUsedInterval;
		}catch (Exception ex) {
			logger.error(ex,"FMS_ID = "+fmsId);
			throw new MalException("generic.error.occured.while", new String[] { " getting last used vehicle schedule interval" }, ex);
		}
	}
	
	public BigDecimal getConversionFactor(FleetMaster fleetMaster)throws MalException{
		try{
			BigDecimal conversionFactor = new BigDecimal(1);
			Driver driver = driverService.getActiveDriverForUnit(fleetMaster);
			if (driver != null) {
				if (driver.getGaragedAddress().getCountry().getCountryCode().equalsIgnoreCase("CN")) {
					conversionFactor = MaintenanceScheduleService.CONVERSION_FACTOR_FOR_KILOMETERS;
				}
			}
			return conversionFactor;
		}catch(Exception ex){
			logger.error(ex,fleetMaster!=null ? "FMS_ID = "+fleetMaster.getFmsId():"Fleet Master is Null");
			throw new MalException("generic.error.occured.while", new String[] { " getting conversion factor" }, ex);
		}
		
	}
		
	private List<MaintenanceScheduleIntervalPrintVO> getIntervalsAndMileage(Long fmsId, boolean ignoreNonMileageIntervals, int lastIndex) throws MalException {
		try {
			List<MaintenanceScheduleIntervalPrintVO> retVal = new ArrayList<MaintenanceScheduleIntervalPrintVO>();
			VehicleSchedule vehicleSchedule	= findActiveScheduleByFmsId(fmsId);
			int startingOdo = this.findStartingInterval(vehicleSchedule);
			BigDecimal conversionFactor = getConversionFactor(vehicleSchedule.getFleetMaster());
			if(lastIndex > 0)
				retVal = maintenanceScheduleService.getMasterScheduleIntervalList(vehicleSchedule.getMasterSchedule()
						.getMschId(), startingOdo, conversionFactor, lastIndex+1, ignoreNonMileageIntervals);
			else
				retVal = maintenanceScheduleService.getMasterScheduleIntervalList(vehicleSchedule.getMasterSchedule()
						.getMschId(), startingOdo, conversionFactor, lastIndex, ignoreNonMileageIntervals);			

			return retVal;
		} catch (Exception ex) {
			logger.error(ex,"FMS_ID = "+fmsId);
			throw new MalException("generic.error.occured.while", new String[] { " getting next vehicle schedule interval" }, ex);
		}
			
	}

	public VehicleSchedule findActiveScheduleByFmsId(Long fmsId){
		VehicleSchedule retVal = null;
		List<VehicleSchedule> vehScheduleList = getVehicleScheduleByFmsId(fmsId);
		for (VehicleSchedule vehicleSchedule : vehScheduleList) {
			Date activeToDate = vehicleSchedule.getActiveTo();
			if (activeToDate != null && activeToDate.before(new Date())) {
				continue;
			}else{
				retVal = vehicleSchedule;
			}
		}
		return retVal;
	}
	
	private int findStartingInterval(VehicleSchedule vehicleSchedule){
		return vehicleSchedule.getStartOdoReading() != null ? vehicleSchedule.getStartOdoReading().intValue() : 0;
	}
	
	/**
	 * Method to get next vehicle schedule interval and associated mileage for given fmsId
	 * @param fmsId
	 * @return MaintenanceScheduleIntervalPrintVO, holding next authorization number and mileage value
	 * @throws MalException
	 */
	public MaintenanceScheduleIntervalPrintVO getNextIntervalAndMileage(Long fmsId) throws MalException {
		MaintenanceScheduleIntervalPrintVO retValMap = new MaintenanceScheduleIntervalPrintVO();
		
		try {
			VehicleSchedule vehicleSchedule	= findActiveScheduleByFmsId(fmsId);
			Integer lastCompletedInterval = getLastCompletedInterval(vehicleSchedule);
			//Passing in True to ignore non-mileage intervals
			lastCompletedInterval = lastCompletedInterval != null ? lastCompletedInterval : 0;
			List<MaintenanceScheduleIntervalPrintVO> list = this.getIntervalsAndMileage(fmsId, true, lastCompletedInterval);
			
			if (list != null && !list.isEmpty()) {
				int nextInterval = !MALUtilities.isEmpty(lastCompletedInterval) ? lastCompletedInterval + 1 : 1;
				// interval numbers (getLastCompletedInterval and getIntervalIndex) are 1 based indexes are 0 based
				retValMap =  list.get(nextInterval - 1);
				String nextIntervalAuthCode = getAuthorizationNumber(vehicleSchedule.getVehSchSeq(), calculateIntervalCode(nextInterval));
				retValMap.setAuthorizationNumber(nextIntervalAuthCode);
			}

			return retValMap;
		} catch (Exception ex) {
			logger.error(ex,"FMS_ID = "+fmsId);
			throw new MalException("generic.error.occured.while", new String[] { " getting next vehicle schedule interval" }, ex);
		}
	}

	/**
	 * Method to get next vehicle schedule interval and associated mileage for given fmsId
	 * @param fmsId
	 * @param odo
	 * @return MaintenanceScheduleIntervalPrintVO, holding next authorization number and mileage value
	 * @throws MalException
	 */
	@Override
	public MaintenanceScheduleIntervalPrintVO getNextIntervalAndMileage(
			Long fmsId, int odo) throws MalException {
		MaintenanceScheduleIntervalPrintVO retVal = new MaintenanceScheduleIntervalPrintVO();
		
		try {
			VehicleSchedule vehicleSchedule	= findActiveScheduleByFmsId(fmsId);
			//Passing in True to ignore non-mileage intervals
			List<MaintenanceScheduleIntervalPrintVO> list = this.getIntervalsAndMileage(fmsId, true, 0);
			
			for(int i=0; i<list.size(); i++){
				if(Integer.parseInt(list.get(i).getIntervalDescription()) >= odo){
					retVal = list.get(i);
					String nextIntervalAuthCode = getAuthorizationNumber(vehicleSchedule.getVehSchSeq(), calculateIntervalCode(i + 1));
					retVal.setAuthorizationNumber(nextIntervalAuthCode);
					break;
				}
			}
			
			return retVal;
		} catch (Exception ex) {
			logger.error(ex,"FMS_ID = "+fmsId);
			throw new MalException("generic.error.occured.while", new String[] { " getting next vehicle schedule interval" }, ex);
		}
		
	}

	/**
	 * Method to get previous vehicle schedule interval and associated mileage for given fmsId
	 * @param fmsId
	 * @param odo
	 * @return MaintenanceScheduleIntervalPrintVO, holding next authorization number and mileage value
	 * @throws MalException
	 */
	@Override
	public MaintenanceScheduleIntervalPrintVO getPreviousIntervalAndMileage(
			Long fmsId, int odo) throws MalException {
		MaintenanceScheduleIntervalPrintVO retVal = new MaintenanceScheduleIntervalPrintVO();
		
		try {
			VehicleSchedule vehicleSchedule	= findActiveScheduleByFmsId(fmsId);
			//Passing in True to ignore non-mileage intervals
			List<MaintenanceScheduleIntervalPrintVO> list = this.getIntervalsAndMileage(fmsId, true, 0);
			for(int i=list.size(); i>=1; i--){
				if(Integer.parseInt(list.get(i-1).getIntervalDescription()) <= odo){
					retVal = list.get(i-1);
					String nextIntervalAuthCode = getAuthorizationNumber(vehicleSchedule.getVehSchSeq(), calculateIntervalCode(i));
					retVal.setAuthorizationNumber(nextIntervalAuthCode);
					break;
				}
			}

			return retVal;
		} catch (Exception ex) {
			logger.error(ex,"FMS_ID = "+fmsId);
			throw new MalException("generic.error.occured.while", new String[] { " getting next vehicle schedule interval" }, ex);
		}
		
	}
	/**
	 * Method to get vehicle schedule interval and associated mileage for given fmsId and interval index(example First Index, Second index etc)
	 * @param fmsId
	 * @param index
	 * @return MaintenanceScheduleIntervalPrintVO, holding next authorization number and mileage value
	 * @throws MalException
	 */
	@Override
	public MaintenanceScheduleIntervalPrintVO getIndexIntervalAndMileage(
			Long fmsId, int index) throws MalException {
		MaintenanceScheduleIntervalPrintVO retVal = new MaintenanceScheduleIntervalPrintVO();
		
		try {
			VehicleSchedule vehicleSchedule	= findActiveScheduleByFmsId(fmsId);
			//Passing in True to ignore non-mileage intervals
			List<MaintenanceScheduleIntervalPrintVO> list = this.getIntervalsAndMileage(fmsId, true, index);
			
			//Check if list came back with some elements
			if (list != null && !list.isEmpty()) {
				//Check to see list has more elements than index
				if (list.size() >= index) {
					//Basically returning index value from list 
					retVal = list.get(index-1); //Have to do minus 1 because index value starts from 1 where as first element of list starts from 0
					String nextIntervalAuthCode = getAuthorizationNumber(vehicleSchedule.getVehSchSeq(), calculateIntervalCode(index));
					retVal.setAuthorizationNumber(nextIntervalAuthCode);								
				}
			}
			return retVal;	
		} catch (Exception ex) {
			logger.error(ex,"FMS_ID = "+fmsId);
			throw new MalException("generic.error.occured.while", new String[] { " getting next vehicle schedule interval" }, ex);
		}
		
	}
}
