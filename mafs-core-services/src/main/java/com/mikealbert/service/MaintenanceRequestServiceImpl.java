package com.mikealbert.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.hibernate.Hibernate;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.common.MalConstants;
import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.DataConstants;
import com.mikealbert.data.dao.BuyerLimitDAO;
import com.mikealbert.data.dao.ClientContactDAO;
import com.mikealbert.data.dao.ContractLineDAO;
import com.mikealbert.data.dao.DocDAO;
import com.mikealbert.data.dao.DoclDAO;
import com.mikealbert.data.dao.DocumentNumberDAO;
import com.mikealbert.data.dao.ExternalAccountDAO;
import com.mikealbert.data.dao.FleetMasterDAO;
import com.mikealbert.data.dao.FleetNotesDAO;
import com.mikealbert.data.dao.MaintCodeFinParamMappingDAO;
import com.mikealbert.data.dao.MaintenanceCategoryPropertyDAO;
import com.mikealbert.data.dao.MaintenanceCategoryPropertyValueDAO;
import com.mikealbert.data.dao.MaintenanceCategoryUOMDAO;
import com.mikealbert.data.dao.MaintenanceCodeDAO;
import com.mikealbert.data.dao.MaintenanceInvoiceDAO;
import com.mikealbert.data.dao.MaintenanceRequestDAO;
import com.mikealbert.data.dao.MaintenanceRequestTaskDAO;
import com.mikealbert.data.dao.MaintenanceRequestUserDAO;
import com.mikealbert.data.dao.OdometerDAO;
import com.mikealbert.data.dao.ProgressChasingDAO;
import com.mikealbert.data.dao.ServiceProviderDAO;
import com.mikealbert.data.dao.ServiceProviderMaintenanceCodeDAO;
import com.mikealbert.data.dao.VehicleReplacementVDAO;
import com.mikealbert.data.dao.VehicleSearchDAO;
import com.mikealbert.data.entity.BuyerLimit;
import com.mikealbert.data.entity.ContractLine;
import com.mikealbert.data.entity.CostAvoidanceReason;
import com.mikealbert.data.entity.Doc;
import com.mikealbert.data.entity.Docl;
import com.mikealbert.data.entity.DoclPK;
import com.mikealbert.data.entity.DocumentNumber;
import com.mikealbert.data.entity.DocumentNumberPK;
import com.mikealbert.data.entity.DocumentTransactionType;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.FleetNotes;
import com.mikealbert.data.entity.LogBookEntry;
import com.mikealbert.data.entity.MaintenanceCategoryProperty;
import com.mikealbert.data.entity.MaintenanceCategoryPropertyValue;
import com.mikealbert.data.entity.MaintenanceCategoryUOM;
import com.mikealbert.data.entity.MaintenanceCode;
import com.mikealbert.data.entity.MaintenanceRechargeCode;
import com.mikealbert.data.entity.MaintenanceRequest;
import com.mikealbert.data.entity.MaintenanceRequestStatus;
import com.mikealbert.data.entity.MaintenanceRequestTask;
import com.mikealbert.data.entity.MaintenanceRequestType;
import com.mikealbert.data.entity.MaintenanceRequestUser;
import com.mikealbert.data.entity.OdometerReading;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.ServiceProvider;
import com.mikealbert.data.entity.ServiceProviderAddress;
import com.mikealbert.data.entity.ServiceProviderMaintenanceCode;
import com.mikealbert.data.entity.VehicleReplacementV;
import com.mikealbert.data.entity.VehicleReplacementVPK;
import com.mikealbert.data.entity.VehicleSchedule;
import com.mikealbert.data.entity.VehicleScheduleInterval;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.data.enumeration.DocumentStatus;
import com.mikealbert.data.enumeration.DocumentType;
import com.mikealbert.data.enumeration.LogBookTypeEnum;
import com.mikealbert.data.enumeration.MaintRequestUpdateReasonTypes;
import com.mikealbert.data.enumeration.MaintenanceRequestStatusEnum;
import com.mikealbert.data.enumeration.VehicleStatus;
import com.mikealbert.data.vo.ClientContactVO;
import com.mikealbert.data.vo.HistoricalMaintCatCodeVO;
import com.mikealbert.data.vo.InvoiceDateAndNumberVO;
import com.mikealbert.data.vo.MaintCodeFinParamMappingVO;
import com.mikealbert.data.vo.MaintenanceContactsVO;
import com.mikealbert.data.vo.MaintenanceInvoiceCreditVO;
import com.mikealbert.data.vo.ProgressChasingQueueVO;
import com.mikealbert.data.vo.ProgressChasingVO;
import com.mikealbert.data.vo.VehicleInformationVO;
import com.mikealbert.data.vo.VehicleSearchCriteriaVO;
import com.mikealbert.data.vo.VehicleSearchResultVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.util.SpringAppContext;

@Service("maintRequestService")
public class MaintenanceRequestServiceImpl implements MaintenanceRequestService  {
	
	public MalLogger logger = MalLoggerFactory.getLogger(this.getClass());
	
	@Resource MaintenanceRequestDAO maintenanceRequestDAO;
	@Resource DoclDAO doclDAO;
	@Resource OdometerService odometerService;
	@Resource MaintenanceRequestTaskDAO maintenanceRequestTaskDAO;
	@Resource ContractService contractService;
	@Resource VehicleReplacementVDAO vehicleReplacementVDAO;
	@Resource LookupCacheService lookupCacheService;
	@Resource DocumentNumberDAO documentNumberDAO;		
	@Resource WillowConfigService willowConfigService;
	@Resource ServiceProviderService serviceProviderService;
	@Resource FleetMasterDAO fleetMasterDAO;
	@Resource MaintenanceCodeDAO maintenanceCodeDAO;
	@Resource MaintenanceRequestUserDAO maintenanceRequestUserDAO;
	@Resource ProgressChasingDAO progressChasingDAO;
	@Resource QuotationService quotationService;
	@Resource BuyerLimitDAO buyerLimitDAO;
	@Resource ContractLineDAO contractLineDAO;
	@Resource DocDAO docDAO;	
	@Resource ServiceProviderDAO serviceProviderDAO;
	@Resource DocumentService documentService ;
	@Resource MaintenanceInvoiceDAO maintenanceInvoiceDAO;
	@Resource FleetNotesDAO fleetNotesDAO;
	@Resource OdometerDAO odometerDAO;
	@Resource MaintenanceInvoiceService maintInvoiceService;
	@Resource MaintenanceCategoryPropertyValueDAO maintenanceCategoryPropertyValueDAO;
	@Resource MaintenanceCategoryPropertyDAO maintenanceCategoryPropertyDAO;
	@Resource MaintenanceCategoryUOMDAO maintenanceCategoryUOMDAO;
	@Resource LogBookService logBookService;
	@Resource VehicleSearchService vehicleSearchService;
	@Resource MaintCodeFinParamMappingDAO maintCodeFinParamMappingDAO;
	@Resource ClientContactDAO clientContactDAO;
	@Resource CustomerAccountService customerAccountService;
	@Resource VehicleScheduleService vehicleScheduleService;
	@Resource ServiceProviderMaintenanceCodeDAO serviceProviderMaintenanceCodeDAO;
	@Resource ExternalAccountDAO externalAccountDAO;
	
	public static final String LEASEPLAN_MARKUP_PCT = "MAINT_LEASEPLAN_MARKUP_PCT";
	public static final String LEASEPLAN_MARKUP_CAP = "MAINT_LEASEPLAN_MARKUP_CAP";
	public static final String LEASEPLAN_MARKUP_MIN = "MAINT_LEASEPLAN_MARKUP_MIN";
	public static final String MAFS_MARKUP_PCT = "MAINT_MAFS_MARKUP_PCT";
	public static final String MAFS_MARKUP_CAP = "MAINT_MAFS_MARKUP_CAP";
	public static final String MAFS_MARKUP_MIN = "MAINT_MAFS_MARKUP_MIN";	
	
	public String defaultMarkUpMaintCode;
	public String defaultVehicleRentalFeeCode;
	public String defaultVehicleERSFeeCode;
	
	
	@Transactional(readOnly=true)
	public MaintenanceRequest getMaintenanceRequestByMrqId(long mrqId){
		try{
			MaintenanceRequest maintenanceRequest = maintenanceRequestDAO.getMaintenanceRequestByMrqId(mrqId);
			Hibernate.initialize(maintenanceRequest.getFleetMaster().getVehicleOdometerReadings());
			maintenanceRequest = initializeCostAvoidanceIndicators(maintenanceRequest);
			
			if(!MALUtilities.isEmpty(maintenanceRequest.getServiceProvider())){
				maintenanceRequest.getServiceProvider().getServiceProviderAddresses().size();
			}
			
			return maintenanceRequest;
			
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while", 
					new String[] { "finding a maintenance request by mrqqId " + mrqId }, ex);				
		}
	}
	
	@Transactional(readOnly=true)
	public List<MaintenanceRequest> getMaintenanceRequestByFmsId(long fmsId) {
		List<MaintenanceRequest> maintenanceRequests = null;
		try{
			maintenanceRequests = maintenanceRequestDAO.findByFmsId(fmsId);
			
			//Initializing transient properties at the request and task levels
			for(MaintenanceRequest request : maintenanceRequests){
				request = initializeCostAvoidanceIndicators(request);
			}
			
			return maintenanceRequests;
			
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while", 
					new String[] { "finding a maintenance request by fmsId " + fmsId }, ex);				
		}
	}
	
	@Transactional(readOnly=true)
	public MaintenanceRequest getMaintenanceRequestByJobNo(String jobNo) {
		MaintenanceRequest maintenanceRequest = null;
		
		try{
			maintenanceRequest = maintenanceRequestDAO.findByJobNo(jobNo);
			maintenanceRequest.setGoodwillIndicator(hasGoodwill(maintenanceRequest));
			
			if(!MALUtilities.isEmpty(maintenanceRequest.getMaintenanceRequestTasks())){
				for(MaintenanceRequestTask task : maintenanceRequest.getMaintenanceRequestTasks()){
					task.setCostAvoidanceIndicator(hasCostAvoidance(task));
				}
			}
						
			return maintenanceRequest;
			
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while", 
					new String[] { "finding a maintenance request by jobNo " + jobNo }, ex);				
		}
	}	
	
	/**
	 * Sums the total of the tasks that are flagged to be recharged to client.
	 * @param maintRequestTasks Tasks to sum
	 * @return sum of maintenance request tasks totals to be recharged to the client
	 */
	public BigDecimal sumTaskTotalToRecharge(List<MaintenanceRequestTask> maintRequestTasks){
		BigDecimal rechargeTotal = new BigDecimal("0.00");
		for(MaintenanceRequestTask task: maintRequestTasks){
			if(MALUtilities.convertYNToBoolean(task.getRechargeFlag())){
				rechargeTotal = rechargeTotal.add(task.getTotalCost());
			}			
		}
		return rechargeTotal;
	}
	
	/**
	 * Calculates the sum of the task total cost for the provided tasks
	 * @param maintenanceRequest maintRequestTasks to sum
	 * @return sum of "task total cost"
	 */
	@Transactional(readOnly=true)
	public BigDecimal sumTotalCost(MaintenanceRequest mrq){
		List<MaintenanceRequestTask> maintRequestTasks = mrq.getMaintenanceRequestTasks();
		
		BigDecimal totalCost = new BigDecimal("0.00");
		
		for(MaintenanceRequestTask task : maintRequestTasks){
			BigDecimal totalPrice = doclDAO.findTotalAmountForMaintReqTask(task.getMrtId());

			if(convertPOStatus(mrq.getMaintReqStatus()).equals("C") && totalPrice == null) {
				totalCost = totalCost.add(task.getTotalCost());
			}
			else if (task.getRechargeFlag().equals("Y") && totalPrice != null) {
				totalCost = totalCost.add(totalPrice);
			}			
		}
		return totalCost;
	}
	



	
	/**
	 * Evenly distributes the mark up dollar amount across all PO lines that are flagged to be marked up.
	 * The last mark up line, mark up amount, will be adjusted to correct rounding error.
	 * @param MaintenanceRequest Maintenance Purchase Order
	 * @param BigDecimal Mark up dollar amount 
	 */
	public MaintenanceRequest applyMarkUp(MaintenanceRequest po, BigDecimal markUp){		
		BigDecimal lineMarkUp;
		BigDecimal lineRechargeTotal;
		List<MaintenanceRequestTask> allLines;
		List<MaintenanceRequestTask> rechargeableLines = new ArrayList<MaintenanceRequestTask>();
		
		allLines = po.getMaintenanceRequestTasks();
		for(MaintenanceRequestTask line : allLines){
			if(MALUtilities.convertYNToBoolean(line.getRechargeFlag())){
				rechargeableLines.add(line);
			} else {
				line.setMarkUpAmount(null);
			}
		}
		
		for(MaintenanceRequestTask line : rechargeableLines){
			lineMarkUp = markUp.divide(new BigDecimal(rechargeableLines.size()), 2, BigDecimal.ROUND_HALF_UP);
			
			if((rechargeableLines.indexOf(line) == rechargeableLines.size() - 1)){
				lineMarkUp = lineMarkUp.add(markUp.subtract(lineMarkUp.multiply(new BigDecimal(rechargeableLines.size()))));									
			}
			
			line.setMarkUpAmount(lineMarkUp);			
			line.setRechargeUnitCost(line.getUnitCost());
			line.setRechargeQty(line.getTaskQty());
			
			lineRechargeTotal = line.getRechargeUnitCost();
			lineRechargeTotal = lineRechargeTotal.multiply(line.getRechargeQty()).setScale(2, BigDecimal.ROUND_HALF_UP);
			lineRechargeTotal = lineRechargeTotal.add(lineMarkUp);
			line.setRechargeTotalCost(lineRechargeTotal);
			
		}			
		
		return po;
	}
	
	/**
	 * This method will revert the evenly distributed the mark up dollar amount across all PO lines that are flagged to be marked up but only for non-completed PO.
	 * This is to reset the mark up amount to null for existing non completed PO implemented against FM-1532
	 * @param po
	 */
	public MaintenanceRequest revertLinesMarkUp(MaintenanceRequest po){
		if( !MALUtilities.isEmpty(po) && !po.getMaintReqStatus().equals(MalConstants.STATUS_COMPLETE_PO) ){
			List<MaintenanceRequestTask> allLines = po.getMaintenanceRequestTasks();
			for(MaintenanceRequestTask line : allLines){
				line.setMarkUpAmount(null);
			}
		}
		return po;
	}
	
	/**
	 * Adds additional mark up lines(task) to the maintenance request.
	 * The task(s) are added when the service provider is a network provider 
	 * but has a mark up or the service provider is non-network provider. 
	 * Willow config parameters are used to determine
	 * the percentage and fixed amount minimum and maximum caps. The recharge markup line is
	 * calculated first then the no recharge line last for network service provider and only recharge markup for non-network service provider. 
	 */
	public MaintenanceRequest createMarkupLine(MaintenanceRequest mrq, String username, boolean waiveNonNetworkMarkup){
		BigDecimal rechargeMarkupTotal = new BigDecimal(0.00);
		MaintenanceRequestTask task;
		String defaultMarkUpCategoryCode;
		String defaultMarkUpCode = "";
		String defaultMarkUpRechargeCode;
		Long maxLineNumber = nextMRTLineNumber(mrq);
		
//need to determine if this will be a new willow config or not		
		defaultMarkUpCategoryCode = willowConfigService.getConfigValue("MAINT_MARKUP_LN_CAT_CODE");
//		defaultMarkUpCode = willowConfigService.getConfigValue("MAINT_MARKUP_LN_CODE");
//need to determine if this will be a new willow config or not		
		defaultMarkUpRechargeCode = willowConfigService.getConfigValue("MAINT_MARKUP_LN_RECH_CODE");		
		
		for(Iterator<MaintenanceRequestTask> iter  = mrq.getMaintenanceRequestTasks().iterator(); iter.hasNext();){
			task = (MaintenanceRequestTask)iter.next();
			if(!MALUtilities.isEmpty(task.getMaintCatCode()) && task.getMaintCatCode().equals(defaultMarkUpCategoryCode)){				
				iter.remove();
			}
		}		

		if(!MALUtilities.convertYNToBoolean(mrq.getServiceProvider().getNetworkVendor())){
			//get willow config for 999-002
			defaultMarkUpCode = willowConfigService.getConfigValue("MAINT_NON_NETWORK_CODE");
			rechargeMarkupTotal = calculateNonNetworkRechargeMarkup(mrq);
		}
		
    	if(rechargeMarkupTotal.compareTo(new BigDecimal(0.00)) != 0){	
    		task = new MaintenanceRequestTask();
    		task.setMaintenanceRequest(mrq);
    		task.setMaintCatCode(defaultMarkUpCategoryCode);
    		task.setMaintenanceCode(convertMaintenanceCode(defaultMarkUpCode));
    		task.setMaintenanceCodeDesc(convertMaintenanceCode(defaultMarkUpCode).getDescription());  
    		task.setWorkToBeDone(task.getMaintenanceCodeDesc());
    		task.setTaskQty(new BigDecimal(1));
    		
    		
    		if(!MALUtilities.convertYNToBoolean(mrq.getServiceProvider().getNetworkVendor())){
        		if(waiveNonNetworkMarkup){
        			task.setUnitCost(new BigDecimal(0.00));
    	    		task.setTotalCost(new BigDecimal(0.00));
    	    		task.setCostAvoidanceAmount(rechargeMarkupTotal);
    	    		task.setCostAvoidanceIndicator(true);
    	    		task.setCostAvoidanceCode("WAIVED_ONF");
    	    		task.setCostAvoidanceDescription("Waived Out of Network Service Fee");
        		}else{
        			task.setUnitCost(new BigDecimal(0.00));
    	    		task.setTotalCost(new BigDecimal(0.00));
        		}
    		}
    		
    		if(waiveNonNetworkMarkup){
    			//TODO: is this correct? shouldn't it follow the customer's maintenance program (Y|N) ?
    			task.setRechargeFlag(MalConstants.FLAG_Y);
        		task.setRechargeCode(defaultMarkUpRechargeCode);
        		task.setRechargeQty(new BigDecimal(1));
        		task.setRechargeUnitCost(new BigDecimal(0.00));
        		task.setRechargeTotalCost(new BigDecimal(0.00));
    		}else{
    			task.setRechargeFlag(MalConstants.FLAG_Y);
        		task.setRechargeCode(defaultMarkUpRechargeCode);
        		task.setRechargeQty(new BigDecimal(1));
        		task.setRechargeUnitCost(rechargeMarkupTotal);
        		task.setRechargeTotalCost(task.getRechargeUnitCost().multiply(task.getRechargeQty()));
    		}

    		task.setDiscountFlag(MalConstants.FLAG_N);
    		task.setOutstanding(DataConstants.DEFAULT_N);
    		task.setWasOutstanding(DataConstants.DEFAULT_N); 
    		task.setAuthorizePerson(username);
    		task.setAuthorizeDate(Calendar.getInstance().getTime());
    		task.setLineNumber(maxLineNumber+=1);
    		mrq.getMaintenanceRequestTasks().add(task);    		
    	}		

		return mrq;
	}
	
	/**
	 * Sums up the lines' total to determine the PO's subtotal.
	 * The PO subtotal does not include markup.
	 * @param MaintenanceRequest Purchase order
	 * @return BigDecimal PO's subtotal, markup not included
	 */
	public BigDecimal calculatePOSubTotal(MaintenanceRequest po){
		BigDecimal subTotal = new BigDecimal(0.00).setScale(2);
		String defaultMarkUpCode = willowConfigService.getConfigValue("MAINT_NON_NETWORK_CODE");
		for(MaintenanceRequestTask line : po.getMaintenanceRequestTasks()){
			/** Excluding non-network mark up maintenance code 999-002 **/
			if(MALUtilities.isEmpty(defaultMarkUpCode) || !line.getMaintenanceCode().getCode().equals(defaultMarkUpCode)){
				subTotal = subTotal.add(line.getTotalCost().setScale(2, BigDecimal.ROUND_HALF_UP));
			}
		}
		
		return subTotal;
	}
	
	/**
	 * Determines the Markup amount by applying a predefined percentage
	 * to the total amount of the MRQ that is to be recharged.
	 */
	public BigDecimal calculateNonNetworkRechargeMarkup(MaintenanceRequest po){
		BigDecimal markupTotal = new BigDecimal(0.00).setScale(2);
		
		try {
			Long qmdId = quotationService.getQmdIdFromFmsId(po.getFleetMaster().getFmsId(), po.getActualStartDate());
			if(qmdId == null  || qmdId.compareTo(0L) <= 0){
			    return markupTotal;
			}
			if(qmdId == null  || qmdId.compareTo(0L) <= 0){
			    return markupTotal;
			}
			if(qmdId != null && !MALUtilities.isEmpty(qmdId)){
				QuotationModel quotationModel = quotationService.getQuotationModel(qmdId);
				BigDecimal markupPercent = new BigDecimal(Double.valueOf(quotationService.getFinanceParam(MAFS_MARKUP_PCT, quotationModel.getQmdId(),quotationModel.getQuotation().getQuotationProfile().getQprId(), po.getActualStartDate())) / 100).setScale(2, BigDecimal.ROUND_HALF_UP).setScale(2);
				BigDecimal markupCap = new BigDecimal(Double.valueOf(quotationService.getFinanceParam(MAFS_MARKUP_CAP, quotationModel.getQmdId(),quotationModel.getQuotation().getQuotationProfile().getQprId(), po.getActualStartDate()))).setScale(2, BigDecimal.ROUND_HALF_UP);
				BigDecimal markupMin = new BigDecimal(Double.valueOf(quotationService.getFinanceParam(MAFS_MARKUP_MIN, quotationModel.getQmdId(),quotationModel.getQuotation().getQuotationProfile().getQprId(), po.getActualStartDate()))).setScale(2, BigDecimal.ROUND_HALF_UP);
		
				if(!MALUtilities.isEmpty(po.getServiceProvider()) && !MALUtilities.convertYNToBoolean(po.getServiceProvider().getNetworkVendor())){
					markupTotal = sumTaskTotalToRecharge(po.getMaintenanceRequestTasks()).multiply(markupPercent).setScale(2, BigDecimal.ROUND_HALF_UP);
					markupTotal = markupTotal.compareTo(markupCap) > 0 ? markupCap : markupTotal;

					if (markupTotal.compareTo(BigDecimal.ZERO) > 0){
						markupTotal = markupTotal.compareTo(markupMin) < 0 ? markupMin : markupTotal;
					}
				}
			}
		} catch(Exception ex) {
			throw new MalException("generic.error.occured.while", 
					new String[] { "calculating mafs recharge markup for maintenance request :" +  po.getMrqId() }, ex);				
		}			
		
		return markupTotal;
	}
		
	/**
	 * Sums up the goodwill amount on all the maintenance request's tasks
	 * @return BigDecimal Goodwill will total for the maintenance request
	 */	
	public BigDecimal sumGoodwillTotal(MaintenanceRequest po){
		BigDecimal total = new BigDecimal(0.00).setScale(2);
		
		for(MaintenanceRequestTask task : po.getMaintenanceRequestTasks()){
			total = total.add(MALUtilities.isEmpty(task.getGoodwillCost()) ? new BigDecimal(0) : task.getGoodwillCost());
		}
		
		return total;		
	}
	
	/**
	 * Sums up the cost avoidance amount on all the maintenance request's tasks
	 * @return BigDecimal Cost avoidance will total for the maintenance request
	 */		
	public BigDecimal sumCostAvoidanceTotal(MaintenanceRequest po){
		BigDecimal total = new BigDecimal(0.00).setScale(2);
		
		for(MaintenanceRequestTask task : po.getMaintenanceRequestTasks()){
			total = total.add(MALUtilities.isEmpty(task.getCostAvoidanceAmount()) ? new BigDecimal(0) : task.getCostAvoidanceAmount());
		}
		
		return total;		
	}
	
	
	/**
	 * This method determines whether Task Items List modified. 
	 * @return boolean
	 */
	@Override
	public boolean isTaskItemListModified(List<MaintenanceRequestTask> originalRequestTasks, List<MaintenanceRequestTask> modifiedRequestTasks){
		
		if(modifiedRequestTasks.size() != originalRequestTasks.size()) return true;

		for(MaintenanceRequestTask maintenanceRequestTask : modifiedRequestTasks){
			for(MaintenanceRequestTask originalTask : originalRequestTasks){
				if(MALUtilities.isEmpty(maintenanceRequestTask.getMrtId())){
					return true;
				}else{
					if(maintenanceRequestTask.getMrtId().longValue() == originalTask.getMrtId().longValue()){
						if(maintenanceRequestTask.equals(originalTask) == false) return true;
						break;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * This method update the authorize person if the task item is modified. 
	 * @return boolean
	 */
	@Override
	public void updateAuthorizePersonForModifiedTaskItems(List<MaintenanceRequestTask> originalRequestTasks, List<MaintenanceRequestTask> modifiedRequestTasks, String logedInUser){					
		
		for(MaintenanceRequestTask maintenanceRequestTask : modifiedRequestTasks){
			for(MaintenanceRequestTask originalTask : originalRequestTasks){
				if(!MALUtilities.isEmpty(maintenanceRequestTask.getMrtId())){
					if(maintenanceRequestTask.getMrtId().longValue() == originalTask.getMrtId().longValue()){
						if(maintenanceRequestTask.equals(originalTask) == false){
							maintenanceRequestTask.setAuthorizePerson(logedInUser);
						}else{
							maintenanceRequestTask.setAuthorizePerson(originalTask.getAuthorizePerson());
						}
					}
				}
			}
		}
	}
	

	@Override
	public MaintenanceRequestTask getTaskItemById(Long mrtId) {
		MaintenanceRequestTask task = null;
	
		try{
			task = maintenanceRequestTaskDAO.findById(mrtId).orElse(null);
			task = initializeCostAvoidanceIndicators(task);
		} catch(Exception ex) {
			throw new MalException("generic.error.occured.while", 
					new String[] { "getting maintenance request task :" +  mrtId }, ex);				
		}
		return task;
	}
	
	/**
	 * Persists the new MaintenanceRequestTask entity
	 * @param Entity 
	 */	
	@Transactional
	public void saveItemTask(MaintenanceRequestTask maintenanceRequestTask){
		maintenanceRequestTaskDAO.save(maintenanceRequestTask);
	}
	
	@Transactional
	public MaintenanceRequest saveOrUpdateMaintnenacePO(MaintenanceRequest mrq, String username) throws MalBusinessException{
		MaintenanceRequest savedMRQ = null;
		BigDecimal lineMarkUp = null;
		CostAvoidanceReason costAvoidanceReason = null;	
		String nonNetworkMarkupCode = willowConfigService.getConfigValue("MAINT_NON_NETWORK_CODE");
		String vehicleRentalFeeCode = willowConfigService.getConfigValue("MAINT_RENTAL_FEE_CODE");
		String vehicleERSFeeCode = willowConfigService.getConfigValue("MAINT_ERS_FEE_CODE");//HPS-2946 get ERS Maint Code
		
		//Perform validation
		validatePurchaseOrder(mrq);				
		
		//Stamp the user who's creating or updating the PO
		mrq.setLastChangedBy(username);
		mrq.setLastChangedDate(Calendar.getInstance().getTime());
		mrq.setAuthBy(username);					
		//Defaulting the PO's planned start and actual end dates
		mrq.setPlannedStartDate(mrq.getActualStartDate());
		mrq.setActualEndDate(mrq.getPlannedEndDate());
		
		for(MaintenanceRequestTask line : mrq.getMaintenanceRequestTasks()){
			//DEFAULTS:
			//   - Assign the PO's planned end date to the line level planned date
			//   - Assign the fleet master to the line		
			//   - Assign the service provider's payee account info to the PO's line			
			line.setFmsFmsId(mrq.getFleetMaster().getFmsId());			
			line.setPayeeAccountCode(mrq.getServiceProvider().getPayeeAccount().getExternalAccountPK().getAccountCode());
			line.setPayeeAccountType(mrq.getServiceProvider().getPayeeAccount().getExternalAccountPK().getAccountType());
			line.setPayeeCorporateId(mrq.getServiceProvider().getPayeeAccount().getExternalAccountPK().getCId());
			
			//Assignments specific to new or existing PO lines
			if(MALUtilities.isEmpty(line.getMrtId())){
				//Assign maintenance repair reason code to NORMAL
				
				//line.setMaintenanceRepairReasonCode(MAINT_REQUEST_REASON_CODE);
				//line.setLineNumber(nextMRTLineNumber(mrq));
				
				//The line's plan date is the MRQ's AUTHN_CREATED date.
				//Otherwise, it should be assigned the current date.
				if(MALUtilities.isEmpty(mrq.getAuthnCreated())){
					line.setPlannedDate(Calendar.getInstance().getTime());
				} else {
					line.setPlannedDate(mrq.getAuthnCreated());
				}
				line.setPlannedDate(mrq.getPlannedEndDate());				
			} 
			
			//Set Cost Avoidance && Goodwill
			if(!line.isCostAvoidanceIndicator()){
				line.setCostAvoidanceCode(null);
				line.setCostAvoidanceDescription(null);
				line.setCostAvoidanceAmount(new BigDecimal(0));
				line.setGoodwillCost(new BigDecimal(0));
				line.setGoodwillPercent(new BigDecimal(0));
				line.setGoodwillReason(null);
			} else {
				costAvoidanceReason = convertCostAvoidanceReason(line.getCostAvoidanceCode());
				line.setCostAvoidanceDescription(MALUtilities.isEmpty(costAvoidanceReason) ? null : costAvoidanceReason.getDescription());
			}		
			
			//Apply the recharge amounts when applicable. Otherwise, set amounts to null			
			if(MALUtilities.convertYNToBoolean(line.getRechargeFlag())){
				//FM-1532: For the non-network markup line the line's total cost will be 0.00, but the recharge total will have the markup amount
				if(!line.getMaintenanceCode().getCode().equals(nonNetworkMarkupCode)){
					if(line.getMaintenanceCode().getCode().equals(vehicleERSFeeCode)){
						//HPS-2946 start get the cost of the ERS fee  
						lineMarkUp = MALUtilities.isEmpty(line.getMarkUpAmount()) ? new BigDecimal("0.00") : line.getMarkUpAmount();
						line.setRechargeQty(line.getTaskQty());
						line.setRechargeUnitCost(line.getRechargeUnitCost());
						line.setRechargeTotalCost(line.getRechargeTotalCost().add(lineMarkUp).setScale(2, BigDecimal.ROUND_HALF_UP));
					}/* HPS-2946 End */ else if (!line.getMaintenanceCode().getCode().equals(vehicleRentalFeeCode)){
						lineMarkUp = MALUtilities.isEmpty(line.getMarkUpAmount()) ? new BigDecimal("0.00") : line.getMarkUpAmount();
						line.setRechargeQty(line.getTaskQty());
						line.setRechargeUnitCost(line.getUnitCost());
						line.setRechargeTotalCost(line.getTotalCost().add(lineMarkUp).setScale(2, BigDecimal.ROUND_HALF_UP));
					}
				}
			} else {
				line.setRechargeQty(null);
				line.setRechargeUnitCost(null);
				line.setRechargeTotalCost(null);
			}
		}
		
		//Auth Limit check is only performed when the maintenance request 
		//does not have approval info and is not 'W'aiting on 'C'lient 'A'pproval.
		//When the check fails, the status of the maintenance request will switch to WCA
		//and the save PO process will continue. 
		if(!(convertPOStatus(mrq.getMaintReqStatus()).getCode().equals(MaintenanceRequestStatusEnum.MAINT_REQUEST_STATUS_WAITING_ON_CLIENT_APPROVAL.getCode())
				|| convertPOStatus(mrq.getMaintReqStatus()).getCode().equals(MaintenanceRequestStatusEnum.MAINT_REQUEST_STATUS_CLOSED_NO_PO.getCode()))){
			if(!isRechargeTotalWithinLimit(mrq)){		
				mrq.setMaintReqStatus(MaintenanceRequestStatusEnum.MAINT_REQUEST_STATUS_WAITING_ON_CLIENT_APPROVAL.getCode());
			} 			
		}		
				
		//Performing operations specific to new or existing POs
		if(MALUtilities.isEmpty(mrq.getMrqId())){		
			mrq.setCreatedBy(username);		
			mrq.setOrigController(username);
			mrq.setAuthnCreated(Calendar.getInstance().getTime());
			mrq.setMaintReqDate(Calendar.getInstance().getTime());
			mrq.setRequestClass("REQCLASS1"); //TODO: Remove hard code, i.e. make constant, enum, etc.
			mrq.setRevisionStatus("REVSTAT1");//TODO: Remove hard code, i.e. make constant, enum, etc. 
			mrq.setAuthMessage("xxxxx"); //TODO: Remove hard code, i.e. make constant, enum, etc. I have no clue as to what this is; this is the value for all POs
		} else {			
			captureReplacementUnitInfo(mrq);			
			createMaintRequestUserLog(mrq, username);
		}

		//save odometer reading only when there is a new/changed reading.
		// Added If condition for Bug 16247 (Odometer Reading Should only be saved to the odometers_readings table
		// when the sort order is >= 5 ( i.e.Odometer should not be saved for statuses Preauthorized,Scheduled ,Booked-In and Waiting on Client Approval)
		if(convertPOStatus(mrq.getMaintReqStatus()).getSortOrder() >= 5){
		  if(isChangedOdoReading(mrq)){
			odometerService.saveOrUpdateOdoReading(mrq, username);
		 }}

		// conditionally create, update or delete the vehicle schedule interval based upon the couponRefNo (Schedule Authorization Number) and Job No
		this.manageVehScheduleIntervals(mrq);
		
		//perform save and updates of maintenance request, insert/update or delete of maintenance task items and insert/update of maintenance request user.  
		savedMRQ = maintenanceRequestDAO.saveAndFlush(mrq);
		savedMRQ = initializeCostAvoidanceIndicators(savedMRQ);
		
		return savedMRQ;
	}
	
    private Doc getStubDocFromJobNo(String jobNo){
    	Doc doc = new Doc();
    	doc.setDocId(-1);
    	doc.setDocNo(jobNo);
    	return doc;
    }
    
    
    //TODO: there are other flows we might be able to call without saving
    
    private void manageVehScheduleIntervals(MaintenanceRequest mrq){
		if(MALUtilities.isEmptyString(mrq.getCouponBookReference())){
			//look to see if there are any vehicle schedule intervals for this JobNo
			VehicleScheduleInterval vehInterval = vehicleScheduleService.getVehicleScheduleIntervalForDocNo(mrq.getJobNo());
			
			//if there are then remove them
			if(!MALUtilities.isEmpty(vehInterval)){
				vehicleScheduleService.deleteVehicleScheduleInterval(vehInterval);
			}
		}else{
			// get the vehicle schedule from the Authorization Code (Coupon Ref)
			VehicleSchedule vehSched = vehicleScheduleService.getVehicleScheduleForVehSchedSeq(
						vehicleScheduleService.getVehicleSeqFromAuthNbr(mrq.getCouponBookReference()));
			
			// create the vehicle schedule interval for the looked up vehicle schedule, if applicable
			// check for existing PORDER doc no if so use it instead of this one
			if(!MALUtilities.isEmpty(vehSched)){
				Doc targetPORDERDoc = null;
				
				List<Doc> docs = docDAO.findReleasedPurchaseOrderForMaintReq(mrq.getMrqId());
				if(docs != null && docs.size() > 0){
					targetPORDERDoc = docs.get(0);
				}else{
					targetPORDERDoc = this.getStubDocFromJobNo(mrq.getJobNo());
				}
				
				vehicleScheduleService.saveVehicleScheduleInterval(vehSched, mrq.getCouponBookReference(), targetPORDERDoc);
			}
		}
    }
	

	/**
	 * Creates the goodwill maintenance request purchase order (PO) by: 
	 *     - Copying the passed in PO
	 *     - Setting the goodwill PO tasks' unit price and total amount to $0
	 *     - Setting the goodwill PO job no to the originating PO job no appended with 'CRn', e.g.  J00000000CR1
	 * @param MaintenanceRequest The PO that will be used to generate a goodwill PO
	 * @param The name of the user that has initiated this action
	 */
	public MaintenanceRequest createGoodwillMaintenanceRequest(MaintenanceRequest mrq, String username) throws MalBusinessException{
		MaintenanceRequest goodwillMaintenanceRequest = new MaintenanceRequest();
		MaintenanceRequestTask goodwillMaintenanceRequestTask = null;
		int numChildren = maintenanceRequestDAO.getMaintRequestByMrqMrqIdCount(mrq.getMrqId());
		mrq = maintenanceRequestDAO.findById(mrq.getMrqId()).orElse(null);	
		defaultMarkUpMaintCode = willowConfigService.getConfigValue("MAINT_NON_NETWORK_CODE");
		defaultVehicleRentalFeeCode = willowConfigService.getConfigValue("MAINT_RENTAL_FEE_CODE");
		defaultVehicleERSFeeCode = willowConfigService.getConfigValue("MAINT_ERS_FEE_CODE");
		
		//Copy
		BeanUtils.copyProperties(mrq, goodwillMaintenanceRequest, 
				new String[]{"mrqId", "maintenanceRequestTasks", "accessoryMaintJobActivations", "diaries", "fleetNotes", 
				"maintRequestUsers", "odometerReadings", "servicesDues", "versionts"});
		
		//Changes to the Maintenance Request
		goodwillMaintenanceRequest.setJobNo(mrq.getJobNo().concat("CR" + (numChildren + 1)));
		goodwillMaintenanceRequest.setMrqMrqId(mrq.getMrqId());
		goodwillMaintenanceRequest.setMaintReqStatus(MaintenanceRequestStatusEnum.MAINT_REQUEST_STATUS_GOODWILL_PENDING.getCode());
		
		//Changes to the Maintenance Request Tasks
		goodwillMaintenanceRequest.setMaintenanceRequestTasks(new ArrayList<MaintenanceRequestTask>());
		
		for(MaintenanceRequestTask task : mrq.getMaintenanceRequestTasks()){
			if (!task.getMaintenanceCode().getCode().equals(defaultMarkUpMaintCode)){
				if(!task.getMaintenanceCode().getCode().equals(defaultVehicleRentalFeeCode)){
					if(!task.getMaintenanceCode().getCode().equals(defaultVehicleERSFeeCode)){				
							goodwillMaintenanceRequestTask = new MaintenanceRequestTask();
							
							BeanUtils.copyProperties(task, goodwillMaintenanceRequestTask, new String[]{"mrtId", "maintenanceRequest", "maintenanceCategoryPropertyValues", "versionts"});
							goodwillMaintenanceRequestTask.setUnitCost(new BigDecimal(0));
							goodwillMaintenanceRequestTask.setTotalCost(new BigDecimal(0));
							goodwillMaintenanceRequestTask.setMarkUpAmount(new BigDecimal(0));
							goodwillMaintenanceRequestTask.setRechargeUnitCost(new BigDecimal(0));
							goodwillMaintenanceRequestTask.setRechargeTotalCost(new BigDecimal(0));
							goodwillMaintenanceRequestTask.setMaintenanceRequest(goodwillMaintenanceRequest);
							goodwillMaintenanceRequestTask.setGoodwillPercent(new BigDecimal(100));
							goodwillMaintenanceRequestTask.setGoodwillCost(null);
							goodwillMaintenanceRequestTask.setGoodwillReason(null);
						    goodwillMaintenanceRequestTask.setCostAvoidanceAmount(null);
							goodwillMaintenanceRequestTask.setCostAvoidanceCode(null);
							goodwillMaintenanceRequestTask.setCostAvoidanceDescription(null);
							
							goodwillMaintenanceRequest.getMaintenanceRequestTasks().add(goodwillMaintenanceRequestTask);								
						}
					}
				}
		}
		
		goodwillMaintenanceRequest = initializeCostAvoidanceIndicators(goodwillMaintenanceRequest);
		
		goodwillMaintenanceRequest = saveOrUpdateMaintnenacePO(goodwillMaintenanceRequest, username);
		
		return goodwillMaintenanceRequest;
	}
	
	
	/**
	 * Validates the purchase order based on business rules.
	 * @param po Purchase Order
	 * @throws MalBusinessException
	 */
	private void validatePurchaseOrder(MaintenanceRequest mrq) throws MalBusinessException{
		ArrayList<String> messages = new ArrayList<String>();
		BigDecimal amount = new BigDecimal(0.00);
				
		//Validate PO header required data
		if(MALUtilities.isEmptyString(mrq.getJobNo()))
			messages.add("Job No. is required");
		if(MALUtilities.isEmptyString(mrq.getMaintReqStatus()))
			messages.add("PO Status is required");
		if(MALUtilities.isEmptyString(mrq.getMaintReqType()))
			messages.add("PO Type is required");
		if(MALUtilities.isEmpty(mrq.getCurrentOdo()))
			messages.add("Odo is required");
		if(MALUtilities.isEmpty(mrq.getActualStartDate()))
			messages.add("Actual Start Date is required");
		if(MALUtilities.isEmpty(mrq.getPlannedEndDate()))
			messages.add("End date is required");
		if(MALUtilities.isEmpty(mrq.getServiceProvider()))
			messages.add("Service Provider is required");
		/* TODO Need to determine if this check is necessary. We do not have a hard requirement for this.
		if(!MALUtilities.isEmpty(po.getServiceProvider()) 
				&& (!MALUtilities.convertYNToBoolean(po.getServiceProvider().getNetworkVendor()) 
						&& this.calculateMarkUp(po).compareTo(new BigDecimal(0.00)) < 1))
			messages.add("Mark Up is required for out of network service providers");
	    */
		
		//Validate PO Line items (tasks) required data
		if(!MALUtilities.isEmpty(mrq.getMaintenanceRequestTasks())){
			for(MaintenanceRequestTask line : mrq.getMaintenanceRequestTasks()){
				if(MALUtilities.isEmptyString(line.getMaintCatCode()))
					messages.add("Line " + line.getIndex() + ": Maint Category is required");
				if(MALUtilities.isEmpty(line.getTaskQty()))
					messages.add("Line " + line.getIndex() + ": Qty is required");
				if(MALUtilities.isEmpty(line.getUnitCost()))
					messages.add("Line " + line.getIndex() + ": Unit Price is required");
				if(MALUtilities.isEmpty(line.getTotalCost())) 
					messages.add("Line " + line.getIndex() + ": Total Amount is required");								
				if(!(MALUtilities.isEmpty(line.getTaskQty()) && MALUtilities.isEmpty(line.getUnitCost()))){
					amount = line.getUnitCost().multiply(line.getTaskQty()).setScale(2, BigDecimal.ROUND_HALF_UP); 
					if( amount.compareTo(line.getTotalCost().setScale(2, BigDecimal.ROUND_HALF_UP)) != 0)
						messages.add("Line " + line.getIndex() + ": Total amount is incorrect");							
				}	
/** TODO This will not work well with goodwill POs as the user will not have the changes to add cost avoidance data to subsequent lines.				
				if(mrq.isGoodwillIndicator() && line.isCostAvoidanceIndicator()){
					if(MALUtilities.isEmpty(line.getCostAvoidanceCode()))
						messages.add("Line " + line.getIndex() + ": Cost Avoidance Reason is required");
					if(MALUtilities.isEmpty(line.getCostAvoidanceAmount()))
						messages.add("Line " + line.getIndex() + ": Cost Avoidance Amount is required");
					if(MALUtilities.isEmpty(line.getGoodwillReason()))
						messages.add("Line " + line.getIndex() + ": Goodwill Reason is required");
					if(MALUtilities.isEmpty(line.getGoodwillCost()) || line.getGoodwillCost().compareTo(new BigDecimal(0)) < 1)
						messages.add("Line " + line.getIndex() + ": Goodwill Amount is required");	
					if(MALUtilities.isEmpty(line.getGoodwillPercent()) || line.getGoodwillPercent().compareTo(new BigDecimal(0)) < 1)
						messages.add("Line " + line.getIndex() + ": Goodwill Percent is required");						
				}
				if(!mrq.isGoodwillIndicator() && line.isCostAvoidanceIndicator()){
					if(MALUtilities.isEmpty(line.getCostAvoidanceCode()))
						messages.add("Line " + line.getIndex() + ": Cost Avoidance Reason is required");
					if(MALUtilities.isEmpty(line.getCostAvoidanceAmount()))
						messages.add("Line " + line.getIndex() + ": Cost Avoidance Amount is required");					
				}
*/				
			}
		}
		
		if(messages.size() > 0)
			throw new MalBusinessException("service.validation", messages.toArray(new String[messages.size()]));		
		
	}
	
	/**
	 * As part of the save transaction, when a PO is in a "C"ompleted state, the replacement unit (when one exists) information
	 * should be captured and saved on the PO for historical reference.
	 * @param po MaintenanceRequest purchase order
	 */
	private void captureReplacementUnitInfo(MaintenanceRequest mrq){	
		ContractLine contractLine = null;
		
		if(mrq.getMaintReqStatus().equals(MaintenanceRequestStatusEnum.MAINT_REQUEST_STATUS_COMPLETE.getCode())){
			contractLine = contractService.getLastActiveContractLine(mrq.getFleetMaster(), mrq.getMaintReqDate());
			VehicleReplacementV replacementUnitInfo = null;
			if(!MALUtilities.isEmpty(contractLine)){
				replacementUnitInfo = vehicleReplacementVDAO.findById(
					new VehicleReplacementVPK(mrq.getFleetMaster().getUnitNo(), 
							contractLine.getContract().getExternalAccount().getExternalAccountPK().getCId(), 
							contractLine.getContract().getExternalAccount().getExternalAccountPK().getAccountType(), 
							contractLine.getContract().getExternalAccount().getExternalAccountPK().getAccountCode())).orElse(null);
			}
			if(!MALUtilities.isEmpty(replacementUnitInfo)){
				//mrq.setReplacementUnitNo(replacementUnitInfo.getVehicleReplacementVPK().getCurrentUnitNo()); Bug16623 commented instead of getting current unit no it should get replacement unit no.
				mrq.setReplacementUnitNo(replacementUnitInfo.getReplacementUnitNo());//Bug16623 Added to get replacement unit
				if(!MALUtilities.isEmpty(replacementUnitInfo.getInServiceDate())){
					mrq.setReplacementUnitDateType(DATE_TYPE_IN_SERVICE);
					mrq.setReplacementUnitDate(replacementUnitInfo.getInServiceDate());
				} else if(!MALUtilities.isEmpty(replacementUnitInfo.getDealerDeliverDate())){
					mrq.setReplacementUnitDateType(DATE_TYPE_DEALER_DELIVERY);
					mrq.setReplacementUnitDate(replacementUnitInfo.getDealerDeliverDate());// changed to dealerdeliverdate for Bug 16623
				} else if(!MALUtilities.isEmpty(replacementUnitInfo.getEtaDate())){
					mrq.setReplacementUnitDateType(DATE_TYPE_ETA);
					mrq.setReplacementUnitDate(replacementUnitInfo.getEtaDate());				
				} else {
					mrq.setReplacementUnitDateType(null);
					mrq.setReplacementUnitDate(null);				
				}
			}
		}		
	}	
		
	/**
	 * Converts a PO Status code to the respective entity
	 * @param code PO Status Code
	 * @return MaintenanceRequestStatus entity
	 */
	public MaintenanceRequestStatus convertPOStatus(String code){
		List<MaintenanceRequestStatus> statuses = lookupCacheService.getMaintenanceRequestStatuses();
		MaintenanceRequestStatus status = null;
		
		for(MaintenanceRequestStatus mrs : statuses){
			if(mrs.getCode().equals(code)){
				status = mrs;
				break;
			}
		}
		
		return status;
		
	}
	
	
	/**
	 * Converts a PO Type code to the respective entity
	 * @param cdoe PO Type
	 * @return MaintenanceRequestType entity
	 */
	public MaintenanceRequestType convertPOType(String code){
		
		List<MaintenanceRequestType> types = lookupCacheService.getMaintenanceRequestTypes();
		MaintenanceRequestType type = null;
		
		for(MaintenanceRequestType mrt : types){
			if(mrt.getCode().equals(code)){
				type = mrt;
				break;
			}
		}
		
		return type;				
	}
	

	
	/**
	 * Converts a maint recharge code to the respective entity
	 * @param cdoe Maintenance Recharge Code
	 * @return MaintenanceRechargeCode entity
	 */	
	public MaintenanceRechargeCode convertMaintenanceRechargeCode(String code){
		List<MaintenanceRechargeCode> maintRechargeCodes = lookupCacheService.getMaintenanceRechargeCodes();
		MaintenanceRechargeCode maintRechargeCode = null;
		
		for(MaintenanceRechargeCode mrc : maintRechargeCodes){
			if(mrc.getCode().equals(code)){
				maintRechargeCode = mrc;
				break;
			}
		}
		
		return maintRechargeCode;		
	}
	
	/**
	 * Converts a cost avoidance reason to the respective entity
	 * @param code Cost avoidance reason code
	 * @return CostAvoidanceReason
	 */	
	public CostAvoidanceReason convertCostAvoidanceReason(String code){
		List<CostAvoidanceReason> costAvoidanceReasons = lookupCacheService.getCostAvoidanceReasons();
		CostAvoidanceReason costAvoidanceReason = null;
		
		for(CostAvoidanceReason reason : costAvoidanceReasons){
			if(reason.getCode().equals(code)){
				costAvoidanceReason = reason;
				break;
			}
		}
		
		return costAvoidanceReason;		
	}	

	/**
	 * Converts a maint code to the respective entity
	 * @param cdoe Maintenance Code
	 * @return MaintenanceCode entity
	 */
	public MaintenanceCode convertMaintenanceCode(String code){				
		return maintenanceCodeDAO.findByCode(code);		
	}
	
	/**
	 * Based on the corporate entity, the maintenance PO's next job number is retrieved from the database.
	 * @param CorporateEntity Corporate entity
	 */
	@Transactional
	public String generateJobNumber(CorporateEntity corporateEntity){
		DocumentNumber docNo = null;
		String jobNumber = null;

		docNo = documentNumberDAO.findById(new DocumentNumberPK(corporateEntity.getCorpId(), DocumentNumberDAO.DOMAIN_FLEET_MAINTENANCE)).orElse(null);		
	
		jobNumber = docNo.getPreFix() + String.format("%08d", docNo.getNextNo());
		
		docNo.setNextNo(docNo.getNextNo() + 1);
		docNo = documentNumberDAO.saveAndFlush(docNo);	
		
		return jobNumber;
	}

	@Override
	@Transactional(readOnly=true)
	public List<MaintenanceRequest> findPlannedDateEndedMaintenanceRequests() {
		List<MaintenanceRequest> req = null;
		
		// find all in progress that have a planned end date later than (now!)
		req = maintenanceRequestDAO.findInProgressRequestsLessThanDate(new Date());
		
		return req;
	}

	@Override
	@Transactional(readOnly=true)
	public void updateToWaitOnInvoiceMaintenanceRequestStatus(MaintenanceRequest mrq) {
			mrq.setMaintReqStatus(MaintenanceRequestStatusEnum.MAINT_REQUEST_STATUS_WAIT_ON_INVOICE.getCode());
	}
	
	/**
	 * This method update the authorize person if the task item is modified. 
	 * @return boolean
	 */
	@Override
	public void updateModifiedTaskItems(List<MaintenanceRequestTask> originalRequestTasks, List<MaintenanceRequestTask> modifiedRequestTasks, String logedInUser){					
		
		for(MaintenanceRequestTask maintenanceRequestTask : modifiedRequestTasks){
			for(MaintenanceRequestTask originalTask : originalRequestTasks){
				if(!MALUtilities.isEmpty(maintenanceRequestTask.getMrtId())){
					if(maintenanceRequestTask.getMrtId().longValue() == originalTask.getMrtId().longValue()){
						if(maintenanceRequestTask.equals(originalTask) == false){
							originalTask.setDiscountFlag(maintenanceRequestTask.getDiscountFlag());
							originalTask.setMaintCatCode(maintenanceRequestTask.getMaintCatCode());
							originalTask.setMaintenanceCode(maintenanceRequestTask.getMaintenanceCode());
							originalTask.setMaintenanceCodeDesc(maintenanceRequestTask.getMaintenanceCodeDesc());
							originalTask.setRechargeCode(maintenanceRequestTask.getRechargeCode());
							originalTask.setRechargeFlag(maintenanceRequestTask.getRechargeFlag());
							originalTask.setTaskQty(maintenanceRequestTask.getTaskQty());
							originalTask.setUnitCost(maintenanceRequestTask.getUnitCost());
							originalTask.setTotalCost(maintenanceRequestTask.getTotalCost());
							originalTask.setServiceProviderMaintenanceCode(maintenanceRequestTask.getServiceProviderMaintenanceCode());
							originalTask.setAuthorizePerson(logedInUser);
						}else{
							originalTask.setAuthorizePerson(originalTask.getAuthorizePerson());
						}
					}
				}
			}
		}
		
		for(MaintenanceRequestTask maintenanceRequestTask : modifiedRequestTasks){
			if(MALUtilities.isEmpty(maintenanceRequestTask.getMrtId())){
				originalRequestTasks.add(maintenanceRequestTask);
			}
		}
	}
	
	/**
	 * For a specified vin, if previous work has been done for the passed in maintenance category before, a list
	 * of previous work is returned
	 */
	public List<HistoricalMaintCatCodeVO> getHistoricalMaintCatCodes(String vin, String maintCatCode, Long mrqId){
		try{
			List<Long> fmsIds = fleetMasterDAO.findFmsIdsByVIN(vin);
			return maintenanceRequestTaskDAO.getHistoricalMaintCatCode(fmsIds, maintCatCode, mrqId);
		}catch(Exception ex){
			throw new MalException("generic.error.occured.while", 
					new String[] { "finding a historical maintenance category code" }, ex);
		}
	}
	
	/**
	 * For a specified vin, if at least one historical task has been done before, the category is returned;
	 * Oil Changes, Tire Services, Misc. Maint, and Misc. Services will not be included in the returned list
	 */
	public List<String> getDistinctHistoricalCatCodes(String vin, Long mrqId){
		try{
			List<Long> fmsIds = fleetMasterDAO.findFmsIdsByVIN(vin);
			return maintenanceRequestTaskDAO.getDistinctHistoricalCatCodes(fmsIds, mrqId);
		}catch(Exception ex){
			throw new MalException("generic.error.occured.while", 
					new String[] { "finding distinct historical maintenance category code" }, ex);
		}
	}
	
	
	/**
	 * Sets the transient properties, goodwillIndicator and costAvoidanceIndicator, at the
	 * MaintenanceRequest and MaintenanceRequestTask entities, respectively.
	 * @param maintenanceRequest MaintenanceRequest
	 * @return MaintenanceRequest 
	 */
	private MaintenanceRequest initializeCostAvoidanceIndicators(MaintenanceRequest maintenanceRequest){
		maintenanceRequest.setGoodwillIndicator(hasGoodwill(maintenanceRequest));
		
		if(!MALUtilities.isEmpty(maintenanceRequest.getMaintenanceRequestTasks())){
			for(MaintenanceRequestTask task : maintenanceRequest.getMaintenanceRequestTasks()){
				task = initializeCostAvoidanceIndicators(task);
			}
		}
		
		return maintenanceRequest;
	}
	
	/**
	 * Sets the transient properties, costAvoidanceIndicator, on the
	 * MaintenanceRequestTask entity.
	 * @param maintenanceRequestTask MaintenanceRequestTask entity
	 * @return MaintenanceRequestTasks entity
	 */
	private MaintenanceRequestTask initializeCostAvoidanceIndicators(MaintenanceRequestTask maintenanceRequestTask){
		maintenanceRequestTask.setCostAvoidanceIndicator(hasCostAvoidance(maintenanceRequestTask));
		return maintenanceRequestTask;
	}
	
	/**
	 * Determines whether the passed in maintenance request is a goodwill PO. 
	 * A goodwill PO is defined as a maintenance request that has a task with a cost avoidance code
	 * and a goodwill reason.
	 */
	private boolean hasGoodwill(MaintenanceRequest maintenanceRequest){
		MaintenanceRequestTask task = null;
		boolean isGoodwill = false;
		
		if(!MALUtilities.isEmpty(maintenanceRequest.getMrqMrqId())){
			isGoodwill = true;
		}
				
		for (Iterator<MaintenanceRequestTask> mrt = maintenanceRequest.getMaintenanceRequestTasks().iterator(); mrt.hasNext() && !isGoodwill;){
			task = (MaintenanceRequestTask)mrt.next();
			if(!(MALUtilities.isEmpty(task.getGoodwillCost()) || task.getGoodwillCost().compareTo(new BigDecimal(0)) < 1)){
				isGoodwill = true;
				break;
			}
		}		
		
		return isGoodwill;
	}
	
	/**
	 * Determines whether the passed in task has cost avoidance. The rules are:
	 *     - If goodwill PO and cost avoidance amount is null, then is cost avoidance task
	 *     - If cost avoidance amount exist and is greater than 0, then is cost avoidance task
	 *     - Otherwise, the task does not have cost avoidance
	 * @param maintenanceRequestTask
	 * @return
	 */
	private boolean hasCostAvoidance(MaintenanceRequestTask maintenanceRequestTask){
		boolean isGoodwill = false;
		boolean isCostAvoidance = false;
		
		isGoodwill = hasGoodwill(maintenanceRequestTask.getMaintenanceRequest());	
		
		if(isGoodwill && !maintenanceRequestTask.getMaintenanceRequest().getMaintReqStatus().equals(MaintenanceRequestStatusEnum.MAINT_REQUEST_STATUS_COMPLETE.getCode()) 
				&& MALUtilities.isEmpty(maintenanceRequestTask.getCostAvoidanceAmount())){
			isCostAvoidance = true;
		} else if (!(MALUtilities.isEmpty(maintenanceRequestTask.getCostAvoidanceAmount()) 
				|| maintenanceRequestTask.getCostAvoidanceAmount().compareTo(new BigDecimal(0)) < 1)){
			isCostAvoidance = true;
		} else {
			isCostAvoidance = false;
		}			
		   
		return isCostAvoidance;
	}
	
	/**
	 * Determines whether or not the PO's odometer reading has changed
	 * @param po Maintenance Request Purchase Order
	 * @return boolean True indicates change
	 */
	private boolean isChangedOdoReading(MaintenanceRequest po){
		boolean isDiff = false;
		OdometerReading odoReading = null;
		
		if(!MALUtilities.isEmpty(po.getCurrentOdo())){
			odoReading = odometerService.getOdometerReading(po);			
			if(MALUtilities.isEmpty(odoReading)){
				isDiff = true;
			} else {
				if(po.getCurrentOdo() != odoReading.getReading() || !po.getActualStartDate().equals(odoReading.getReadingDate())){
					isDiff = true;
				}				
			}
		}
		
		return isDiff;
	}
	
	@Transactional
	public MaintenanceRequestUser createMaintRequestUserLog(MaintenanceRequest modifiedMaintRequest, String username) {
		MaintenanceRequest originalMaintRequest  = new MaintenanceRequest();
		originalMaintRequest = getMaintenanceRequestByMrqId(modifiedMaintRequest.getMrqId());
		MaintenanceRequestUser maintRequestUser = null;
		modifiedMaintRequest.setMaintRequestUsers(maintenanceRequestUserDAO.getMaintenanceRequestUserByMrqId(modifiedMaintRequest.getMrqId()));
		
		if (originalMaintRequest.getMaintenanceRequestTasks().size() < modifiedMaintRequest.getMaintenanceRequestTasks().size()) {
			maintRequestUser = new MaintenanceRequestUser();
			maintRequestUser.setUpdateReason(MaintRequestUpdateReasonTypes.UPDATE_REASON_ADD.getCode());
		} else if (!calculatePOSubTotal(originalMaintRequest).equals(calculatePOSubTotal(modifiedMaintRequest))) {
			maintRequestUser = new MaintenanceRequestUser();
			maintRequestUser.setUpdateReason(MaintRequestUpdateReasonTypes.UPDATE_REASON_PRICE_CHANGE.getCode());
			maintRequestUser.setOldCost(calculatePOSubTotal(originalMaintRequest));
			maintRequestUser.setNewCost(calculatePOSubTotal(modifiedMaintRequest));
		} else {
			if (isTaskItemListModified(originalMaintRequest.getMaintenanceRequestTasks(), modifiedMaintRequest.getMaintenanceRequestTasks())) {
				for (MaintenanceRequestTask originalTask : originalMaintRequest.getMaintenanceRequestTasks()) {
					for (MaintenanceRequestTask modifiedTask : modifiedMaintRequest.getMaintenanceRequestTasks()) {
						if (originalTask.getMrtId().equals(modifiedTask.getMrtId())) {
							if (!originalTask.getMaintenanceCode().getCode().equals(modifiedTask.getMaintenanceCode().getCode())) {
								maintRequestUser = new MaintenanceRequestUser();
								maintRequestUser.setUpdateReason(MaintRequestUpdateReasonTypes.UPDATE_REASON_CHANGE_MAINT_CODE.getCode());
								break;
							}
						}
					}
				}
			}
			
		}

		if (!MALUtilities.isEmpty(maintRequestUser)) {
			maintRequestUser.setMaintenanceRequest(modifiedMaintRequest);
			maintRequestUser.setUserId(username);
			maintRequestUser.setUpdateDate(Calendar.getInstance().getTime());
			if(MALUtilities.isEmpty(modifiedMaintRequest.getMaintRequestUsers())){
				modifiedMaintRequest.setMaintRequestUsers(new ArrayList<MaintenanceRequestUser>());
			}
			modifiedMaintRequest.getMaintRequestUsers().add(maintRequestUser);
		}
		
		return maintRequestUser;
	}
	
	/**
	 * Delete maintenance PO conditionally
	 * @param maintenanceRequest
	 */
	@Transactional
	public boolean deletePO(MaintenanceRequest mrq){
		if(mrq.getMaintReqStatus().equals(MalConstants.STATUS_COMPLETE_PO)){
			return false;
		}else{
			try{
				List<Doc> docs = docDAO.findInvoiceForQuoteByDocTypeAndSourceCode(mrq.getMrqId(), MalConstants.DOC_TYPE_PORDER, MalConstants.DOC_SOURCE_CODE_FLMAINT);
				if(docs != null && docs.size() > 0){
					Doc doc = docs.get(0);
					doc.setDocStatus(MalConstants.STATUS_CANCEL);
					List<Docl> docls = doc.getDocls();
					if(docls != null && docls.size() > 0){
						for(Docl docl : docls){
							docl.setLineStatus(MalConstants.STATUS_CANCEL);
						}
					}
					docDAO.save(doc);
				}
				// also remove the schedule interval record associated with this authorization code (coupon ref) if there is one.
				if(mrq != null && !MALUtilities.isEmpty(mrq.getCouponBookReference())){
					VehicleScheduleInterval vehicleScheduleInterval = null;
					try{
						vehicleScheduleInterval = vehicleScheduleService.getVehicleScheduleIntervalForAuthNumber(mrq.getCouponBookReference());
					}catch(MalException ex){
						logger.error(ex,"Error occured while retrieving schedule interval for Authorization Number = "+mrq.getCouponBookReference());
					}
					if(!MALUtilities.isEmpty(vehicleScheduleInterval)){
						vehicleScheduleService.deleteVehicleScheduleInterval(vehicleScheduleInterval);
					}
				}
				
				logBookService.deleteLogBook(mrq, LogBookTypeEnum.TYPE_ACTIVITY);
				maintenanceRequestDAO.deleteById(mrq.getMrqId());				
			}catch(Exception ex){
				throw new MalException("generic.error.occured.while", 
						new String[] { "deleting maintenance po : " +  mrq.getJobNo()}, ex);
			}
			return true;
		}
	}
	
	public List<ProgressChasingQueueVO> getProgressChasingDataList(){
	    List<ProgressChasingQueueVO> result = new ArrayList<ProgressChasingQueueVO>();
		try{
		    result = progressChasingDAO.getProgressChasing();

		    return result;	
	    
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while", 
					new String[] { "finding progress chasing data list" }, ex);				
		}
	}
	
	@Transactional
	public List<ProgressChasingVO> getProgressChasingByPoStatus(String poStatus, Pageable pageable, String lastUpdatedBy){
		List<ProgressChasingVO> progressChasingVOList =  new ArrayList<ProgressChasingVO>();
		
		try{		
			progressChasingVOList = progressChasingDAO.getProgressChasingByPoStatus(poStatus, pageable, lastUpdatedBy);
			
			for (ProgressChasingVO progressChasingVO : progressChasingVOList) {
				//Making use of the vehicle search service so that native queries are used to obtain vehicle info
				VehicleSearchCriteriaVO vsc = new VehicleSearchCriteriaVO();
				vsc.setUnitNo(progressChasingVO.getUnitNo());
				vsc.setCorporateEntity(CorporateEntity.MAL);
				vsc.setUnitStatus(VehicleSearchDAO.VEHICLE_SEARCH_STATUS_BOTH);
				List<VehicleSearchResultVO> vsr = vehicleSearchService.findBySearchCriteria(vsc, null, null);
				
				ServiceProvider serviceprovider = serviceProviderDAO.findById(progressChasingVO.getSupId().longValue()).orElse(null);				
				
				//Initializing the MRQ with the data obtained up to this point. No need to make DB calls for each iteration.
				MaintenanceRequest mrq = new MaintenanceRequest();
				mrq.setMrqId(progressChasingVO.getMrqId());
				mrq.setServiceProvider(serviceprovider);
				mrq.setServiceProviderContactInfo(progressChasingVO.getServiceProviderContactInfo());
				
//				//could refactor but waiting on performance issue analysis fm-1125
				if(serviceprovider != null){
					progressChasingVO.setServiceProviderName(serviceprovider.getServiceProviderName());
					progressChasingVO.setServiceProviderNumber(serviceprovider.getServiceProviderNumber());
					String serviceProviderDetail = null;
					List<ServiceProviderAddress> addressList = serviceprovider.getServiceProviderAddresses();
					serviceProviderDetail = serviceprovider.getServiceProviderName() + "<br>No: " + serviceprovider.getServiceProviderNumber();
					String address = null;
					if(addressList != null && addressList.size() > 0){
						for(ServiceProviderAddress serviceProviderAddress : addressList){
							if(serviceProviderAddress.getDefaultInd() != null && serviceProviderAddress.getDefaultInd().equals("Y")){
								address = getServiceProviderFormattedAddress(serviceProviderAddress);
								break;
							}
						}					
					}
					
					if(MALUtilities.isNotEmptyString(address)){
						serviceProviderDetail += address;
					}
					if(MALUtilities.isNotEmptyString(serviceprovider.getTelephoneNo())){
						serviceProviderDetail = serviceProviderDetail + "<br>" + serviceprovider.getTelephoneNo();
					}
					progressChasingVO.setServiceProviderDetails(serviceProviderDetail);
					progressChasingVO.setMaintReqServiceProviderDetail(getMaintReqServiceProvDetail(mrq));
				}
															
				progressChasingVO.setClientAccountNumber(vsr.get(0).getClientAccountNumber());
				progressChasingVO.setClientAccountName(vsr.get(0).getClientAccountName());
				
				LogBookEntry lbe = logBookService.getLatestLogBookEntryByDate(mrq, LogBookTypeEnum.TYPE_ACTIVITY);
				if(lbe.getLbeId() != null){
					progressChasingVO.setActivityNotesLastUpdatedBy(lbe.getEntryUser());
					progressChasingVO.setActivityNotesLastUpdatedDate(lbe.getEntryDate());
				}
				
			}
		
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while", 
					new String[] { "finding a progress chasing vo" }, ex);				
		}
		
		return progressChasingVOList;
	}
	
	/**
	 * Wrapper method to format a maintenance request's service provider contact information
	 * @param mrq Maintenance Request
	 */
	public String getMaintReqServiceProvDetail(MaintenanceRequest mrq){
		return mrqServiceProvDetail(mrq);
	}
	
	/**
	 * Wrapper method to format a maintenance request's service provider contact information
	 * @param Long Maintenance Request Id
	 */	
	public String getMaintReqServiceProvDetail (Long mrqId) {
		MaintenanceRequest mrq = maintenanceRequestDAO.getMaintenanceRequestByMrqId(mrqId);
		return mrqServiceProvDetail(mrq);
	}
		
	public String getServiceProviderFormattedAddress(ServiceProviderAddress serviceProviderAddress){
		StringBuffer addressString = new StringBuffer("");
		if(MALUtilities.isNotEmptyString(serviceProviderAddress.getAddressLine1())){
			addressString.append(serviceProviderAddress.getAddressLine1());
		}
		if(MALUtilities.isNotEmptyString(serviceProviderAddress.getAddressLine2())){
			addressString.append("<br />").append(serviceProviderAddress.getAddressLine2());
		}
		if(MALUtilities.isNotEmptyString(serviceProviderAddress.getAddressLine3())){
			addressString.append("<br />").append(serviceProviderAddress.getAddressLine3());
		}
		if(MALUtilities.isNotEmptyString(serviceProviderAddress.getAddressLine4())){
			addressString.append("<br />").append(serviceProviderAddress.getAddressLine4());
		}
		if(!MALUtilities.isEmpty(serviceProviderAddress.getTownCity())) {
			addressString.append("<br />").append(serviceProviderAddress.getTownCity());
			if(!MALUtilities.isEmpty(serviceProviderAddress.getRegion())){
				addressString.append(", ").append(serviceProviderAddress.getRegion()).append(" ");
			}
		}
		if(MALUtilities.isNotEmptyString(serviceProviderAddress.getPostcode())){
			addressString.append(serviceProviderAddress.getPostcode());
		}
		return addressString.toString();
	}	
	
	@Transactional
	public void authorizeMRQ(MaintenanceRequest mrq, CorporateEntity corporateEntity, String username) throws MalBusinessException{
		ArrayList<String> messages = new ArrayList<String>();
		String message = null;
		boolean isRechargeWithinLimits = true;
		
		//Auth Limit check is only performed when the
		//maintenance request does not have approval info.
		isRechargeWithinLimits = isRechargeTotalWithinLimit(mrq);
		if(!isRechargeWithinLimits){		
			mrq.setMaintReqStatus(MaintenanceRequestStatusEnum.MAINT_REQUEST_STATUS_WAITING_ON_CLIENT_APPROVAL.getCode());
		} 			
		
		//When Maintenance Request status is not 'W'aiting on 'C'lient 'A'pproval, continue
		//with the process of authorizing the PO
		if(isRechargeWithinLimits){
			//Validate core state change validations
			validateStateChange(mrq, MaintenanceRequestStatusEnum.MAINT_REQUEST_STATUS_INPROGRESS.getCode());
			
			//Begin Authorize specific validations
			message = validateFleetMaintAuthBuyerLimit(mrq, corporateEntity, username);
			if(!MALUtilities.isEmptyString(message)){
				messages.add(message);
			}
			
			//Check to make sure all validations have passed before moving on
			if(messages.isEmpty()) {
				//Set maintenance request status to 'I'n Progress
				mrq.setMaintReqStatus(MaintenanceRequestStatusEnum.MAINT_REQUEST_STATUS_INPROGRESS.getCode());
				mrq.setAuthBy(username);
				
				//Save the maintenance request
				mrq = saveOrUpdateMaintnenacePO(mrq, username);

				//Register web notification event
				maintenanceRequestDAO.createWebNotification(mrq);				
			} else {
				throw new MalBusinessException("service.validation", new String[] { messages.toString() });			
			}		
			
		} else {
			saveOrUpdateMaintnenacePO(mrq, username);			
		}
		
	}
	
	/**
	 * 
	 * @param maintenanceRequest
	 * @param corporateEntity
	 * @param user
	 * @throws MalBusinessException
	 */
	@Transactional
	public void completeMRQ(MaintenanceRequest mrq, CorporateEntity corporateEntity, String username) throws MalBusinessException{
		ArrayList<String> messages = new ArrayList<String>();
		String message = null;	

		//Auth Limit check is only performed when the
		//maintenance request does not have approval info.
		if(!isRechargeTotalWithinLimit(mrq)){			
			mrq.setMaintReqStatus(MaintenanceRequestStatusEnum.MAINT_REQUEST_STATUS_WAITING_ON_CLIENT_APPROVAL.getCode());
		} 			
		
		//When Maintenance Request status is not 'W'aiting on 'C'lient 'A'pproval, continue
		//with the process of completing the PO
		if(!mrq.getMaintReqStatus().equals(MaintenanceRequestStatusEnum.MAINT_REQUEST_STATUS_WAITING_ON_CLIENT_APPROVAL.getCode())){
			//TODO Validate core state change validations
			validateStateChange(mrq, MaintenanceRequestStatusEnum.MAINT_REQUEST_STATUS_COMPLETE.getCode());	

			//Begin Complete PO specific validations
			message = validateFleetMaintAuthBuyerLimit(mrq, corporateEntity, username);
			if(!MALUtilities.isEmptyString(message)){
				messages.add(message);
			}
			
		    message = validateTransactionType(mrq, corporateEntity);
			if(!MALUtilities.isEmptyString(message)){
				messages.add(message);
			}		

			if(messages.isEmpty()) {
				//Change the status of the maintenance request to 'C'omplete	
				mrq.setMaintReqStatus(MaintenanceRequestStatusEnum.MAINT_REQUEST_STATUS_COMPLETE.getCode());

				//Save the maintenance request
				mrq = saveOrUpdateMaintnenacePO(mrq, username);

				//Create/update respective PORDER DOC/DOCL 
				try{
					maintenanceRequestDAO.createPurchaseOrderDocument(mrq);

					// conditionally create, update or delete the vehicle schedule interval based upon the couponRefNo (Schedule Authorization Number) and Job No
					this.manageVehScheduleIntervals(mrq);
					
				}catch(Exception ex){
					throw new MalBusinessException("service.validation", new String[] { ex.getCause().getMessage() });
				}

			} else {
				throw new MalBusinessException("service.validation", new String[] { messages.toString() });
			}
		} else {
			//Save the maintenance request
			mrq = saveOrUpdateMaintnenacePO(mrq, username);
		}
	}

	@Transactional
	public void cancelAuthorization(MaintenanceRequest mrq, CorporateEntity corporateEntity, String username) throws MalBusinessException{		

		List<String> messages = validateToCancelAuthorization(mrq);
		boolean isInvoicePosted = maintInvoiceService.hasPostedInvoice(mrq);
		if(isInvoicePosted){
			messages.add("Can't cancel authorization, PO has been posted.");
		}
		if(messages.isEmpty()){

			//Change status to 'B'ooked-In
			List<MaintenanceRequestTask> allLines = mrq.getMaintenanceRequestTasks();
			for(MaintenanceRequestTask line : allLines){
				line.setMarkUpAmount(null);
			}
			mrq.setMaintReqStatus(MaintenanceRequestStatusEnum.MAINT_REQUEST_STATUS_BOOKED_IN.getCode());
			MaintenanceRequestService maintRequestService = (MaintenanceRequestService) SpringAppContext.getBean(MaintenanceRequestService.class);
			//mrq = maintRequestService.createMarkupLine(mrq, username); //SS-185 Moved this check to the maintenancePOBean like the rest of the markup recalculation calls
			mrq = maintRequestService.saveOrUpdateMaintnenacePO(mrq, username);
			
		}else{
			throw new MalBusinessException("service.validation", new String[] { messages.toString() });
		}
	}
	
	
	private List<String> validateToCancelAuthorization(MaintenanceRequest mrq){
		ArrayList<String> messages = new ArrayList<String>();
		if(convertPOStatus(mrq.getMaintReqStatus()).getSortOrder() < 5){
			messages.add("PO status must be in-progress or above to cancel authorization");
		}
		
		
		return messages;
	}
	
	//TODO Add method comment header FM-952
	//TODO Add unit test
	@Transactional
	public void closeJob(MaintenanceRequest mrq, CorporateEntity corporateEntity, String username) throws MalBusinessException{
		ArrayList<String> messages = new ArrayList<String>();
		String message;
		
		//Perform validations
		message = notNullEndDate(mrq);
		if(!MALUtilities.isEmptyString(message)){
			messages.add(message);
		}	
		if(mrq.getMaintenanceRequestTasks() == null || mrq.getMaintenanceRequestTasks().size() == 0){
			message = "You cannot close no job without any tasks";
		}
		
		if(!MALUtilities.isEmptyString(message)){
			messages.add(message);
		}		
		if(!messages.isEmpty()){
			throw new MalBusinessException("service.validation", new String[] { messages.toString() });
		}
				
		//Canceling POs(Docs/DOCLs) that have been released
		List<Doc> docs = docDAO.findReleasedPurchaseOrderForMaintReq(mrq.getMrqId());
		for(Doc doc : docs){			
			doc.setDocStatus(DocumentStatus.PURCHASE_ORDER_STATUS_CANCELED.getCode());

			for(Docl docl : doc.getDocls()){
				docl.setLineStatus(DocumentStatus.PURCHASE_ORDER_STATUS_CANCELED.getCode());
			}
			
			docDAO.saveAndFlush(doc);
		}
		
		//Update actual cost
		for(MaintenanceRequestTask task : mrq.getMaintenanceRequestTasks()){
			task.setActualTaskCost(task.getTotalCost());
			if(!(MALUtilities.isEmpty(task.getRechargeUnitCost()) || MALUtilities.isEmpty(task.getRechargeQty()))){
				task.setActualRechargeCost(task.getRechargeUnitCost().multiply(task.getRechargeQty()).setScale(2, BigDecimal.ROUND_HALF_UP));
			}
		}
		
		//Change status to 'B'ooked-In
		mrq.setMaintReqStatus(MaintenanceRequestStatusEnum.MAINT_REQUEST_STATUS_CLOSED_NO_PO.getCode());
		mrq = saveOrUpdateMaintnenacePO(mrq, username);	
	}
	
	/**
	 * 
	 * @param mr Maintenance Request
	 * @param status The status the maintenance request is trying to achieve
	 * @return
	 * @throws MalBusinessException
	 */
	private void validateStateChange(MaintenanceRequest mrq, String futureStatus) throws MalBusinessException{
		ArrayList<String> messages = new ArrayList<String>();
		String message;
		
		message = notNullActualStart(mrq);
		if(!MALUtilities.isEmptyString(message)){
			messages.add(message);
		}
		message = notNullEndDate(mrq);
		if(!MALUtilities.isEmptyString(message)){
			messages.add(message);
		}
		message = validateStatusBeforeFutureStatus(mrq, futureStatus);
		if(!MALUtilities.isEmptyString(message)){
			messages.add(message);
		}
		message = validateZeroTasks(mrq);
		if(!MALUtilities.isEmptyString(message)){
			messages.add(message);
		}
		message = validateOffContractNoRecharge(mrq);
		if(!MALUtilities.isEmptyString(message)){
			messages.add(message);
		}
		
		if(messages.size() > 0){
			throw new MalBusinessException("service.validation", new String[] { messages.toString() });			
		}
		
	}
	
	/**
	 * Check that actual start date has been entered
	 * @param mrq
	 * @return String of error message if actual start date is empty
	 */
	public String notNullActualStart(MaintenanceRequest mrq){
		String message = "";
		if(MALUtilities.isEmpty(mrq.getActualStartDate())){
			message = "Start Date is required";
		}
		return message;
	}
	
	/**
	 * Check that end date has been entered
	 * @param mrq
	 * @return String of error message if end date is empty
	 */
	public String notNullEndDate(MaintenanceRequest mrq){
		String message = "";
		if(MALUtilities.isEmpty(mrq.getPlannedEndDate())){
			message = "End Date is required";
		}
		return message;
	}
	
	/**
	 * Check to make sure the current status of the maintenance request is before
	 * the future status of the maintenance request
	 * @param mrq Maintenance Request
	 * @param status Future status of the request
	 * @return String of error message if the current status is the same or already past the passed in future status
	 */
	public String validateStatusBeforeFutureStatus(MaintenanceRequest mrq, String status){
		MaintenanceRequestStatus currentStatus = convertPOStatus(mrq.getMaintReqStatus());
		MaintenanceRequestStatus futureStatus = convertPOStatus(status);
		String message = "";
		
		if(currentStatus != null && futureStatus != null){
			if(currentStatus.getSortOrder() >= futureStatus.getSortOrder()){
				message = "This PO has already reached " + futureStatus.getDescription() + " status";
			}
		}
		return message;
	}
	
	/**
	 * This check will return error message if the maintenance request has zero tasks
	 * @param mrq
	 * @return error message if the maintenance request has zero tasks
	 */
	public String validateZeroTasks(MaintenanceRequest mrq){
		String message = "";
		if(mrq.getMaintenanceRequestTasks() == null || mrq.getMaintenanceRequestTasks().size() == 0){
			message = "You cannot authorize a job without any tasks";
		}
		return message;
	}
	
	
	/**
	 * Returns an error if the recharge total has exceeded the customer limit
	 * @param mrq
	 * @return String of error message if recharge total has exceeded the customer limit
	 * @throws MalBusinessException
	 */
	public boolean isRechargeTotalWithinLimit(MaintenanceRequest mrq) throws MalBusinessException{	
		BigDecimal rechargeTotal;
		BigDecimal limit;
		BigDecimal rechargeTotalInDB;
		boolean isWithinLimit = true;
		MaintenanceRequest mrqInDB;
		
		ContractLine contractLine = contractService.getLastActiveContractLine(mrq.getFleetMaster(), Calendar.getInstance().getTime());	
		if(contractLine != null){
			limit = quotationService.getMafsAuthorizationLimit(contractLine.getContract().getExternalAccount().getExternalAccountPK().getCId(), 
						contractLine.getContract().getExternalAccount().getExternalAccountPK().getAccountType(), 
						contractLine.getContract().getExternalAccount().getExternalAccountPK().getAccountCode(), 
						mrq.getFleetMaster().getUnitNo());
		
			if(mrq.getMaintenanceRequestTasks() != null && limit != null){
				rechargeTotal = sumRechargeTotal(mrq);
				if(MALUtilities.isEmptyString(mrq.getAuthReference())){
					if(rechargeTotal.compareTo(limit) > 0){
						isWithinLimit = false;
					}
				}else{
					//Check if passed in maintenance Request has been approved but recharge total has increased since last save.
					if(mrq.getMrqId() != null){
						mrqInDB = maintenanceRequestDAO.findById(mrq.getMrqId()).orElse(null);
						rechargeTotalInDB = sumRechargeTotal(mrqInDB);
						if(rechargeTotal.compareTo(rechargeTotalInDB) > 0 && rechargeTotal.compareTo(limit) > 0){
							isWithinLimit = false;
						}
					}
				}
			}
		}
		return isWithinLimit;
	}	
	
	
	/**
	 * Validates the user's authorize buyer limit; If the maintenance request total exceeds the authorize limit
	 * an error message is returned
	 * @param mrq Maintenance Request
	 * @param corporateEntity Corporate Entity
	 * @param user User to check buyer limits
	 * @return String of error message
	 */
	public String validateFleetMaintAuthBuyerLimit(MaintenanceRequest mrq, CorporateEntity corporateEntity, String user){
		String message = null;
		String docType = "PORDER";
		String tranType = willowConfigService.getConfigValue("MAINT_TRANSACTION_TYPE");
		BigDecimal requestTotal;
		BuyerLimit buyerLimit = buyerLimitDAO.findBuyerLimit(corporateEntity.getCorpId(), docType, user, tranType);
		
		if(mrq.getMaintenanceRequestTasks() != null){
			requestTotal = calculatePOSubTotal(mrq);
			if(buyerLimit == null){
				message = "Buyer limit has not been set up for user: " + user;
			}else if(buyerLimit.getAuthorizeAmount().compareTo(requestTotal) < 0){
				message = user + " has exceeded their " + buyerLimit.getAuthorizeAmount() + " authorization limit";
			}
		}
		return message;
	}
	
	public String validateOffContractNoRecharge(MaintenanceRequest mrq){
		ContractLine contractLine;
		final String error = "This vehicle is not on contract for the actual start date provided.  No items can be recharged.";
		String message = "";
		boolean rechargeTasks = false;
		
		if(notNullActualStart(mrq).isEmpty() || validateZeroTasks(mrq).isEmpty()){ //Skip logic if there is no date or zero tasks
			for(MaintenanceRequestTask task: mrq.getMaintenanceRequestTasks()){
				if(task.getRechargeFlag().equalsIgnoreCase("Y")){
					rechargeTasks = true;
					break;
				}
			}
			
			if(rechargeTasks == true){ // Skip logic if there are no recharged tasks
				contractLine = contractService.getCurrentContractLine(mrq.getFleetMaster(), mrq.getActualStartDate()); //Check for active contract
				if(contractLine == null){
					contractLine = contractLineDAO.findByFmsIdAndDate(mrq.getFleetMaster().getFmsId(), mrq.getActualStartDate()); // Retrieve Contract by actual start date
				}
				/* HPS-1973 authorize and complete PO only if PO start date lies between the in-service date and Actual end date of contract no matter out of service date is populated or not.
				if(contractLine != null ){
					if(contractLine.getOutOfServiceDate() == null || mrq.getActualStartDate().before(contractLine.getOutOfServiceDate())){
						return null; //validation pass
					}else{
						return error;
					}
				}*/
				if(contractLine == null){
					String status = fleetMasterDAO.getFleetStatus(mrq.getFleetMaster().getFmsId());
					if(status.equalsIgnoreCase(VehicleStatus.STATUS_PENDING_LIVE.getCode())){ // If pending live, compare in service date to maint actual start
						contractLine = contractLineDAO.findByFmdIdAndDateWithInService(mrq.getFleetMaster().getFmsId(), mrq.getActualStartDate());
						if(contractLine == null){/* Bug 16599(HPS-1973) removed not null condition */
							message = error;
						}
					}else if (status.equalsIgnoreCase(VehicleStatus.STATUS_VEHICLE_ON_ORDER.getCode())) {/* Bug 16599(HPS-1973) to check on order vehicle */
						      List<ContractLine> contractLines = contractLineDAO.findByFmsId(mrq.getFleetMaster().getFmsId());
								if(contractLines == null || contractLines.isEmpty()){
									message = error;
							  }						      
					}else{
						message = error;
					}
				}
			}
		}
		
		return message;
	}
	
	private String validateTransactionType(MaintenanceRequest mrq, CorporateEntity corporateEntity){
		final String ERROR_MESSAGE = "Could not create purchase order, Doc Type/Tran Type not set up correctly";
		
		String transactionTypeCode = null;
		DocumentTransactionType txnType = null;
		String message = "";
		
		transactionTypeCode = willowConfigService.getConfigValue("MAINT_TRANSACTION_TYPE");
		txnType = documentService.convertDocumentTransactionTypeCode(corporateEntity, DocumentType.PURCHASE_ORDER, transactionTypeCode);
		
		if(MALUtilities.isEmpty(txnType)){
			message = ERROR_MESSAGE;
		}
		
		return message;
	}

	@Override
	public BigDecimal sumRechargeTotal(MaintenanceRequest mrq) {
		BigDecimal rechargeTotal = new BigDecimal(0.00).setScale(2, BigDecimal.ROUND_HALF_UP);
		
		if(!MALUtilities.isEmpty(mrq.getMaintenanceRequestTasks())) {
			for(MaintenanceRequestTask task : mrq.getMaintenanceRequestTasks()){
				if(MALUtilities.convertYNToBoolean(task.getRechargeFlag()) && task.getRechargeTotalCost() != null){
					rechargeTotal = rechargeTotal.add(task.getRechargeTotalCost());
				}
			}
		}		
		
		return rechargeTotal;
	}

	@Override
	public BigDecimal sumMarkUpTotal(MaintenanceRequest mrq) {
		BigDecimal markUp = new BigDecimal(0.00).setScale(2);
		
		for(MaintenanceRequestTask task : mrq.getMaintenanceRequestTasks()){
			if(!MALUtilities.isEmpty(task.getMarkUpAmount())){
				markUp = markUp.add(task.getMarkUpAmount());
			}		
		}
		
		return markUp;
	}
	
	/**
	 * Retrieves a list of CreditAP docs based on the passed in maintenance request
	 * @param mrq Maintenance Request
	 * @return list of Doc records 
	 */
	public List<Doc> getMaintenanceCreditAP(MaintenanceRequest mrq){
		List<Doc> creditAPDocs = new ArrayList<Doc>();
		try{
			List<Long> creditAPDocIds = maintenanceInvoiceDAO.getMaintenanceCreditAPDocIds(mrq);
			for(Long creditAPDocId : creditAPDocIds){
				creditAPDocs.add(docDAO.findById(creditAPDocId).orElse(null));
			}
			return creditAPDocs;
		}catch(Exception ex){
			throw new MalException("generic.error.occured.while", 
					new String[] { "retrieving creditAP  for po: " +  mrq.getJobNo()}, ex);
		}
	}
	
	/**
	 *Retrieves all of the lines for a CreditAP that match up to maintenance request task lines
	 *@param mrq Maintenance Request used to search for creditAP
	 *@return List of MaintenanceInvoiceCredit Lines 
	 */
	public List<MaintenanceInvoiceCreditVO> getMaintenanceCreditAPLines(MaintenanceRequest mrq){
		try{
			return maintenanceInvoiceDAO.getMaintenanceCreditAPLines(mrq);
		}catch(Exception ex){
			throw new MalException("generic.error.occured.while", 
					new String[] { "retrieving creditAP lines for po: " +  mrq.getJobNo()}, ex);
		}
	}
	
	/**
	 * Based on the input maintenance request, a list of credit markup docl lines are returned
	 * @param mrq
	 * @return List of Markup Docls
	 */
	public List<Docl> getMaintenanceCreditARMarkupList(MaintenanceRequest mrq){
		List<Docl> markupDoclList = new ArrayList<Docl>();
		try{
			List<DoclPK> markupDoclPKList = maintenanceInvoiceDAO.getMaintenanceCreditARMarkupDoclPKs(mrq);
			for(DoclPK doclPK : markupDoclPKList){
				markupDoclList.add(doclDAO.findById(doclPK).orElse(null));
			}
			return markupDoclList;
		}catch(Exception ex){
			throw new MalException("generic.error.occured.while", 
					new String[] { "retrieving creditAR markup for purchase order number: " +  mrq.getJobNo()}, ex);
		}
	}
	
	/**
	 * Based on the input maintenance request, a list of credit tax docl lines are returned
	 * @param mrq
	 * @return List of Tax Docls
	 */
	public List<Docl> getMaintenanceCreditARTaxList(MaintenanceRequest mrq){
		List<Docl> taxDoclList = new ArrayList<Docl>();
		try{
			List<DoclPK> taxDoclPKList = maintenanceInvoiceDAO.getMaintenanceCreditARTaxDoclPKs(mrq);
			for(DoclPK doclPK : taxDoclPKList){
				taxDoclList.add(doclDAO.findById(doclPK).orElse(null));
			}
			return taxDoclList;
		}catch(Exception ex){
			throw new MalException("generic.error.occured.while", 
					new String[] { "retrieving creditAR tax for purchase order number: " +  mrq.getJobNo()}, ex);
		}
	}
	
	/**
	 * Based on the input maintenance request, a list of docl lines that do not include
	 * the tax line and markup line are returned
	 * @param mrq
	 * @return List of Docls with Markup and Tax Lines Removed
	 */
	public List<Docl> getMaintenanceCreditARLinesWithoutMarkupAndTaxList(MaintenanceRequest mrq){
		List<Docl> taxDoclList = new ArrayList<Docl>();
		try{
			List<DoclPK> taxDoclPKList = maintenanceInvoiceDAO.getMaintenanceCreditARDoclPKsWithoutTaxAndMarkupLines(mrq);
			for(DoclPK doclPK : taxDoclPKList){
				taxDoclList.add(doclDAO.findById(doclPK).orElse(null));
			}
			return taxDoclList;
		}catch(Exception ex){
			throw new MalException("generic.error.occured.while", 
					new String[] { "retrieving creditAR tax for purchase order number: " +  mrq.getJobNo()}, ex);
		}
	}
	
	@Transactional(readOnly = true)
	public String getPayeeInvoiceNo(Long mrqId){
		String payeeInvoiceNo = "";
		
		if (mrqId != null){
		InvoiceDateAndNumberVO payeeInvoiceVO = maintenanceInvoiceDAO.getMaintenanceRequestPayeeInvoiceData(mrqId);

			if (payeeInvoiceVO != null){
				payeeInvoiceNo = payeeInvoiceVO.getDocNo();
			}
		}
		
	    return payeeInvoiceNo;		
	}
	
	public String getMafsInvoiceNo(Long mrqId){
		String mafsInvoiceNo = "";
		
		if (mrqId != null){
			InvoiceDateAndNumberVO mafsInvoiceVO = maintenanceInvoiceDAO.getMaintenanceRequestMafsInvoiceNumber(mrqId);
			if(!MALUtilities.isEmpty(mafsInvoiceVO)){
				mafsInvoiceNo = mafsInvoiceVO.getDocNo();
			}
		}
		
		return mafsInvoiceNo;
	}
		
	public List<FleetNotes> mashUpFleetNotes(List<FleetMaster> fleetMasters){
		List<FleetNotes> fleetNotes = new ArrayList<FleetNotes>();

		for(FleetMaster fleetMaster : fleetMasters){
			fleetNotes.addAll(fleetNotesDAO.findFleetNotesByFmsId(fleetMaster.getFmsId()));
		}
		
		return fleetNotes;
		
	}
	
	public List<FleetNotes> getJobNotesByMrqId(Long mrqId){

		return fleetNotesDAO.findByMaintenanceRequestId(mrqId);
		
	}
	
	public MaintenanceRequestTask intializeMaintenanceCateogryProperties(MaintenanceRequestTask task){
		List<MaintenanceCategoryPropertyValue> propertyValues = null;
		List<MaintenanceCategoryProperty> properties = null;
		MaintenanceCategoryPropertyValue propertyValue = null;
		
		propertyValues = maintenanceCategoryPropertyValueDAO.findByIdAndMaintenanceCategoryCode(task.getMrtId(), task.getMaintCatCode());
		
		//First query for saved properties the task's maintenance category. If the properties do not exist
		// then instantiate a new list of properties and assign it to the task
		if(!MALUtilities.isEmpty(propertyValues) && propertyValues.size() > 0){
			task.setMaintenanceCategoryPropertyValues(propertyValues);
		} else {
			properties = maintenanceCategoryPropertyDAO.findByMaintenanceCategoryCode(task.getMaintCatCode());
			task.setMaintenanceCategoryPropertyValues(new ArrayList<MaintenanceCategoryPropertyValue>());
			for(MaintenanceCategoryProperty prop : properties){
				propertyValue = new MaintenanceCategoryPropertyValue(); 
				propertyValue.setMaintenanceCategoryProperty(prop);
				propertyValue.setMaintenanceRequestTask(task);
				task.getMaintenanceCategoryPropertyValues().add(propertyValue);
			}
			
		}
		
		return task;
		

	}
	
	public MaintenanceRequestTask resetMaintenanceCateogryPropertyValues(MaintenanceRequestTask task){
		List<MaintenanceCategoryPropertyValue> propertyValues = null;
		
		propertyValues = maintenanceCategoryPropertyValueDAO.findByIdAndMaintenanceCategoryCode(task.getMrtId(), task.getMaintCatCode());
		
		//First query for saved properties the task's maintenance category. If the properties do not exist
		// then clear the property value collection or restore the original collection from database
		if(MALUtilities.isEmpty(propertyValues) || propertyValues.size() == 0){
			task.setMaintenanceCategoryPropertyValues(null);
		} else {
			task.setMaintenanceCategoryPropertyValues(propertyValues);
		}
		
		return task;
	}

	public List<MaintenanceCategoryUOM> getMaintenanceCategoryUOM(MaintenanceRequestTask task) {
		
		return maintenanceCategoryUOMDAO.findByMaintenanceCategoryCode(task.getMaintCatCode());
		
	}
	
	/**
	 * Find the released purchase order and return it
	 * @param mrq
	 * @return List of Released Purchase Order Docs
	 */
	public Doc getReleasedPurchaseOrderForMaintReq(MaintenanceRequest mrq){
		try{
			return docDAO.findReleasedPurchaseOrderDocForMaintReq(mrq.getMrqId());
		}catch(Exception ex){
			throw new MalException("generic.error.occured.while", 
					new String[] { "retrieving released purchase order for Job No: " +  mrq.getJobNo()}, ex);
		}
	}
	
	/**
	 * Provides the next MRT line number based on max line number in the list of MRTs
	 * @param mrq
	 * @return Long Next Line number
	 */
	public Long nextMRTLineNumber(MaintenanceRequest mrq){
		Long maxLineNumber = 0l;
		
		for(MaintenanceRequestTask task : mrq.getMaintenanceRequestTasks()){
			
			maxLineNumber = MALUtilities.isEmpty(task.getLineNumber()) || maxLineNumber >= task.getLineNumber() ? maxLineNumber : task.getLineNumber();
		}
		
		return ++maxLineNumber;
	}
	
	/**
	 * Formats the maintenance request's data provider contact information
	 * @param mrq Maintenance request
	 * @return Formatted service provider contact information
	 */
	private String mrqServiceProvDetail (MaintenanceRequest mrq) {
		//MaintenanceRequest maintRequest = maintenanceRequestDAO.getMaintenanceRequestByMrqId(mrqId);
		MaintenanceRequest maintRequest = mrq;
		String servProvDetail = null;
		
		//ServiceProvider serviceprovider = serviceProviderService.getServiceProvider(maintRequest.getServiceProvider().getServiceProviderId());
		ServiceProvider serviceprovider = mrq.getServiceProvider();
		if(serviceprovider != null) {				
			List<ServiceProviderAddress> addressList = serviceprovider.getServiceProviderAddresses();
			servProvDetail = serviceprovider.getServiceProviderName() + "<br />No: " + serviceprovider.getServiceProviderNumber();
			String address = null;
			if(addressList != null && addressList.size() > 0){
				for(ServiceProviderAddress serviceProviderAddress : addressList){
					if(serviceProviderAddress.getDefaultInd() != null && serviceProviderAddress.getDefaultInd().equals("Y")){
						address = getServiceProviderFormattedAddress(serviceProviderAddress);
						break;
					}
				}					
			}
			
			if (MALUtilities.isNotEmptyString(maintRequest.getServiceProviderContactInfo())) {
				servProvDetail += "<br />" + maintRequest.getServiceProviderContactInfo().replaceAll("\\n", "<br />");
			} else {
				if (address != null) {
					servProvDetail += "<br />" + address;
				}
				if(MALUtilities.isNotEmptyString(serviceprovider.getTelephoneNo())){
					servProvDetail = servProvDetail + "<br>" + serviceprovider.getTelephoneNo();
				}
			}
		}
			return servProvDetail;
	}	
	
	public List<MaintCodeFinParamMappingVO> getMaintCodesFinParamsMapping(String maintCode)
	{
		return maintCodeFinParamMappingDAO.findMaintenanceCodeFinanceParameterValues(maintCode);
	}	
	
	public BigDecimal calculateVehicleRentalFee(MaintenanceRequest mrq)
	{
		BigDecimal financeFee = new BigDecimal(0.00).setScale(2, BigDecimal.ROUND_HALF_UP);
		
		try {
			//Long qmdId = quotationService.getQmdIdFromUnitNo(mrq.getFleetMaster().getUnitNo());
			Long qmdId = quotationService.getQmdIdFromFmsId(mrq.getFleetMaster().getFmsId(), mrq.getActualStartDate());
			if(qmdId == null  || qmdId.compareTo(0L) <= 0){
			    return financeFee;
			}
			if(qmdId != null && !MALUtilities.isEmpty(qmdId)){
				QuotationModel quotationModel = quotationService.getQuotationModel(qmdId);
				
				for(MaintenanceRequestTask task : mrq.getMaintenanceRequestTasks()){
					List<MaintCodeFinParamMappingVO> mcvoList = getMaintCodesFinParamsMapping(task.getMaintenanceCode().getCode());
					if(!MALUtilities.isEmpty(mcvoList) && !mcvoList.isEmpty()){
						if(!(MALUtilities.isEmpty(mcvoList.get(0).getMaintCodeFinParamMapId()))){
							financeFee = new BigDecimal(Double.valueOf(quotationService.getFinanceParam(mcvoList.get(0).getFinanceParameterKey(), quotationModel.getQmdId(),quotationModel.getQuotation().getQuotationProfile().getQprId(), mrq.getActualStartDate()))).setScale(2, BigDecimal.ROUND_HALF_UP);
						}
					}
				}
			}
			
		} catch(Exception ex) {
			throw new MalException("generic.error.occured.while", 
					new String[] { "retrieving rental fee for maintenance code :" +  mrq.getMaintenanceRequestTasks().get(0).getMaintenanceCode().getCode() }, ex);				
		}
		
		return financeFee;
		
	}
	
	/**
	 * Adds additional vehicle rental fee task to the maintenance request.
	 */
	public MaintenanceRequestTask createVehicleRentalFeeLine(MaintenanceRequest mrq, String username){
		BigDecimal vehicleRentalFee = new BigDecimal(0.00); 
		MaintenanceRequestTask task;
		MaintenanceRequestTask newFeeTask = new MaintenanceRequestTask();
		String defaultVehicleRentalFeeCategoryCode = willowConfigService.getConfigValue("MAINT_RENTAL_CAT_CODE");
		String defaultVehicleRentalFeeRechargeCode = willowConfigService.getConfigValue("MAINT_RENTAL_RECH_CODE");
		String defaultVehicleRentalFeeCode = willowConfigService.getConfigValue("MAINT_RENTAL_FEE_CODE");
		
		Long maxLineNumber = nextMRTLineNumber(mrq);
	
		for(Iterator<MaintenanceRequestTask> iter  = mrq.getMaintenanceRequestTasks().iterator(); iter.hasNext();){
			task = (MaintenanceRequestTask)iter.next();
			if(!MALUtilities.isEmpty(task.getMaintCatCode()) && task.getMaintCatCode().equals(defaultVehicleRentalFeeCategoryCode) &&
					task.getMaintenanceCode().getCode().equals(defaultVehicleRentalFeeCode)){				
				iter.remove();
			}
		}	
		
		vehicleRentalFee = calculateVehicleRentalFee(mrq);
		
    	if(vehicleRentalFee.compareTo(new BigDecimal(0.00)) != 0){	
    		newFeeTask.setMaintenanceRequest(mrq);
    		newFeeTask.setMaintCatCode(defaultVehicleRentalFeeCategoryCode);
    		newFeeTask.setMaintenanceCode(convertMaintenanceCode(defaultVehicleRentalFeeCode));
    		newFeeTask.setMaintenanceCodeDesc(convertMaintenanceCode(defaultVehicleRentalFeeCode).getDescription());  
    		newFeeTask.setWorkToBeDone(newFeeTask.getMaintenanceCodeDesc());
    		newFeeTask.setTaskQty(new BigDecimal(1));
    		newFeeTask.setUnitCost(new BigDecimal(0.00));
    		newFeeTask.setTotalCost(new BigDecimal(0.00));
    		newFeeTask.setRechargeFlag(MalConstants.FLAG_Y);
    		newFeeTask.setRechargeCode(defaultVehicleRentalFeeRechargeCode);
    		newFeeTask.setRechargeQty(new BigDecimal(1));
    		newFeeTask.setRechargeUnitCost(vehicleRentalFee);
    		newFeeTask.setRechargeTotalCost(newFeeTask.getRechargeUnitCost().multiply(newFeeTask.getRechargeQty()));
    		newFeeTask.setActualRechargeCost(newFeeTask.getRechargeTotalCost());
    		newFeeTask.setDiscountFlag(MalConstants.FLAG_N);
    		newFeeTask.setOutstanding(DataConstants.DEFAULT_N);
    		newFeeTask.setWasOutstanding(DataConstants.DEFAULT_N); 
    		newFeeTask.setAuthorizePerson(username);
    		newFeeTask.setAuthorizeDate(Calendar.getInstance().getTime());
    		newFeeTask.setLineNumber(maxLineNumber+=1);
    	}	

		return newFeeTask;
 
	}
	
	@Transactional(readOnly = true)		
	public List<MaintenanceContactsVO> getContacts(String pointOfCommunication, VehicleInformationVO vehicleInformationVO) throws MalBusinessException{
		List<MaintenanceContactsVO> maintenanceContactVOs = null;
		List<ClientContactVO> clientContactVOs;
		MaintenanceContactsVO maintenanceContactVO;
		ExternalAccount clientAccount;
		
		final String POC_MAINT_SYSTEM = "MAINT";
		
		try{
			if (!MALUtilities.isEmpty(vehicleInformationVO.getClnId())){			
				clientAccount = customerAccountService.getCustomerAccount(
						CorporateEntity.MAL, 
						vehicleInformationVO.getClientAccountType(), vehicleInformationVO.getClientAccountNumber());

				clientContactVOs = clientContactDAO.getContactVOsByAccountPOC(fleetMasterDAO.findById(vehicleInformationVO.getFmsId()).orElse(null), 
						clientAccount, null, pointOfCommunication, POC_MAINT_SYSTEM, true);

				maintenanceContactVOs = new ArrayList<MaintenanceContactsVO>();
				for(ClientContactVO clientContactVO : clientContactVOs){
					maintenanceContactVO = new MaintenanceContactsVO();

					maintenanceContactVO.setContactId(clientContactVO.getContactId());
					maintenanceContactVO.setContactType(clientContactVO.getContactType());
					maintenanceContactVO.setFirstName(clientContactVO.getFirstName());
					maintenanceContactVO.setLastName(clientContactVO.getLastName());
					maintenanceContactVO.setEmail(clientContactVO.getEmail());
					maintenanceContactVO.setCellAreaCode(clientContactVO.getCellAreaCode());
					//maintenanceContactVO.setCellCncCode("CELL");
					maintenanceContactVO.setCellNumber(clientContactVO.getCellNumber());
					maintenanceContactVO.setWorkAreaCode(clientContactVO.getWorkAreaCode());
					maintenanceContactVO.setWorkNumber(clientContactVO.getWorkNumber());
					maintenanceContactVO.setWorkExtension(clientContactVO.getWorkNumberExtension());
					
					maintenanceContactVO.setHomeAreaCode(clientContactVO.getHomeAreaCode());
					maintenanceContactVO.setHomeNumber(clientContactVO.getHomeNumber());
					maintenanceContactVO.setHomeExtension(clientContactVO.getHomeNumberExtension());
					maintenanceContactVO.setDriverInd(!MALUtilities.isEmpty(clientContactVO.getDriverId())? true:false );
					if(maintenanceContactVO.isDriverInd()){
						if("WORK".equals(clientContactVO.getPreferredNumber())){
							maintenanceContactVO.setWorkNumberPref(true);
						}else if("CELL".equals(clientContactVO.getPreferredNumber())){
							maintenanceContactVO.setCellNumberPref(true);
						}else if("HOME".equals(clientContactVO.getPreferredNumber())){
							maintenanceContactVO.setHomeNumberPref(true);
						}
					}
					
					maintenanceContactVOs.add(maintenanceContactVO);
				}
			}
			
		} catch(Exception ex) {
			throw new MalBusinessException("generic.error.occured.while", 
					new String[] { "getting maintenance contacts for unit:" +  vehicleInformationVO.getUnitNo()}, ex);				
		}
		
		return maintenanceContactVOs;
	}
	@Transactional(readOnly = true)		
	public List<MaintenanceContactsVO> getAllClientContactVOs(String pointOfCommunication, VehicleInformationVO vehicleInformationVO) throws MalBusinessException{
		List<MaintenanceContactsVO> maintenanceContactVOs = null;
		List<ClientContactVO> clientContactVOs;
		MaintenanceContactsVO maintenanceContactVO;
		ExternalAccount clientAccount;
		
		final String POC_MAINT_SYSTEM = "MAINT";
		
		try{
			if (!MALUtilities.isEmpty(vehicleInformationVO.getClnId())){			
				clientAccount = customerAccountService.getCustomerAccount(
						CorporateEntity.MAL, 
						vehicleInformationVO.getClientAccountType(), vehicleInformationVO.getClientAccountNumber());

				clientContactVOs = clientContactDAO.getAllClientContactVOs(fleetMasterDAO.findById(vehicleInformationVO.getFmsId()).orElse(null), 
						clientAccount, null, pointOfCommunication, POC_MAINT_SYSTEM);

				maintenanceContactVOs = new ArrayList<MaintenanceContactsVO>();
				for(ClientContactVO clientContactVO : clientContactVOs){
					maintenanceContactVO = new MaintenanceContactsVO();
					if(clientContactVO.getContactId() != null)
						maintenanceContactVO.setContactId( clientContactVO.getContactId());
					maintenanceContactVO.setContactType(clientContactVO.getContactType());
					maintenanceContactVO.setFirstName(clientContactVO.getFirstName());
					maintenanceContactVO.setLastName(clientContactVO.getLastName());
					maintenanceContactVO.setEmail(clientContactVO.getEmail());
					maintenanceContactVO.setCellAreaCode(clientContactVO.getCellAreaCode());
					//maintenanceContactVO.setCellCncCode("CELL");
					maintenanceContactVO.setCellNumber(clientContactVO.getCellNumber());
					maintenanceContactVO.setWorkAreaCode(clientContactVO.getWorkAreaCode());
					maintenanceContactVO.setWorkNumber(clientContactVO.getWorkNumber());
					maintenanceContactVO.setWorkExtension(clientContactVO.getWorkNumberExtension());
					
					maintenanceContactVO.setHomeAreaCode(clientContactVO.getHomeAreaCode());
					maintenanceContactVO.setHomeNumber(clientContactVO.getHomeNumber());
					maintenanceContactVO.setHomeExtension(clientContactVO.getHomeNumberExtension());
					//maintenanceContactVO.setDriverInd(!MALUtilities.isEmpty(clientContactVO.getDriverId())? true:false );
					maintenanceContactVO.setDriverInd(clientContactVO.isDriver() );
					if(maintenanceContactVO.isDriverInd()){
						if("WORK".equals(clientContactVO.getPreferredNumber())){
							maintenanceContactVO.setWorkNumberPref(true);
						}else if("CELL".equals(clientContactVO.getPreferredNumber())){
							maintenanceContactVO.setCellNumberPref(true);
						}else if("HOME".equals(clientContactVO.getPreferredNumber())){
							maintenanceContactVO.setHomeNumberPref(true);
						}
					}
					
					maintenanceContactVOs.add(maintenanceContactVO);
				}
			}
			
		} catch(Exception ex) {
			throw new MalBusinessException("generic.error.occured.while", 
					new String[] { "getting maintenance contacts for unit:" +  vehicleInformationVO.getUnitNo()}, ex);				
		}
		
		return maintenanceContactVOs;
	}

	/**
	 * Determine if the supplier provides a discount for this task.
	 * If non-network discount is always false
	 * If network and not in table the discount is true (only codes explicitly marked in mapping table as N will be discount false) 
	 * if network and found in table the discount is based on mapping table.  
	 * NOTE - It was specified that all supplier codes mapping to a single MAFS code will share the same 
	 *      discount flag even though multiple supplier codes map to a single MAFS code.
	 */
	@Override
	public boolean isTaskDiscountedBySupplier(ServiceProvider serviceProvider, MaintenanceRequestTask task) {
		boolean discountFlag = true;
		String networkFlag = MALUtilities.isEmptyString(serviceProvider.getNetworkVendor()) ? "N" : serviceProvider.getNetworkVendor();
		if (networkFlag.equalsIgnoreCase("N")){
			discountFlag = false;
		} else {
			List<ServiceProviderMaintenanceCode> list = serviceProviderMaintenanceCodeDAO.getServiceProviderMaintCodesByMaintenanceCodeIdAndProvider(
					task.getMaintenanceCode().getMcoId(), serviceProvider.getServiceProviderId());
			if(list.size() > 0 && list.get(0).getDiscountFlag().equalsIgnoreCase("N")) {
					discountFlag = false;
			}
		}
		return discountFlag;
	}
	
	//HPS-2946 starts, to create a new ERS fee line automatically
	@Override
	public MaintenanceRequestTask createERSFeeLine(
			MaintenanceRequest mrq, String username) {
		
		List<String> listSupplierCodes = new ArrayList<String>();
		BigDecimal miscERSFee = new BigDecimal(0.00); 		
		MaintenanceRequestTask task;
		MaintenanceRequestTask newFeeTask = new MaintenanceRequestTask();
		String defaultMaintERSFeeCategoryCode = willowConfigService.getConfigValue("MAINT_ERS_CAT_CODE");
		String defaultMaintERSFeeRechargeCode = willowConfigService.getConfigValue("MAINT_ERS_RECH_CODE");
		String defaultMaintERSFeeCode = willowConfigService.getConfigValue("MAINT_ERS_FEE_CODE");		
		String defualtERSSupplierCodes = willowConfigService.getConfigValue("MAINT_ERS_VENDOR_CODE");
		List<String> selectedRechargeFlagList = new ArrayList<String>(); 
		
		Long maxLineNumber = nextMRTLineNumber(mrq);
		
		if(defualtERSSupplierCodes != null)
		{
			listSupplierCodes = new ArrayList<String>(Arrays.asList(defualtERSSupplierCodes.split(",")));
		}
		
		for(Iterator<MaintenanceRequestTask> iter  = mrq.getMaintenanceRequestTasks().iterator(); iter.hasNext();){
			task = (MaintenanceRequestTask)iter.next();
			if(!MALUtilities.isEmpty(task.getMaintCatCode()) && task.getMaintCatCode().equals(defaultMaintERSFeeCategoryCode) &&
					task.getMaintenanceCode().getCode().equals(defaultMaintERSFeeCode)){				
				iter.remove();
			}
			if(!MALUtilities.isEmpty(task.getMaintCatCode()) && task.getMaintCatCode().equals(defaultMaintERSFeeCategoryCode)&&
					!task.getMaintenanceCode().getCode().equals(defaultMaintERSFeeCode) ){
				selectedRechargeFlagList.add(task.getRechargeFlag());
			}
		}	
		
		miscERSFee = calculateVehicleRentalFee(mrq);
		
    	if(miscERSFee.compareTo(new BigDecimal(0.00)) != 0 && listSupplierCodes.contains(mrq.getServiceProvider().getServiceProviderNumber()) ){	
    		newFeeTask.setMaintenanceRequest(mrq);
    		newFeeTask.setMaintCatCode(defaultMaintERSFeeCategoryCode);
    		newFeeTask.setMaintenanceCode(convertMaintenanceCode(defaultMaintERSFeeCode));
    		newFeeTask.setMaintenanceCodeDesc(convertMaintenanceCode(defaultMaintERSFeeCode).getDescription());  
    		newFeeTask.setWorkToBeDone(newFeeTask.getMaintenanceCodeDesc());
    		newFeeTask.setTaskQty(new BigDecimal(1));
    		newFeeTask.setUnitCost(new BigDecimal(0.00));
    		newFeeTask.setTotalCost(new BigDecimal(0.00));
    		if(selectedRechargeFlagList!= null && selectedRechargeFlagList.size()>0){
	    		if(selectedRechargeFlagList.contains("Y")){
	    			newFeeTask.setRechargeFlag(MalConstants.FLAG_Y);  
	    		}else{
	    			newFeeTask.setRechargeFlag(MalConstants.FLAG_N);}
    		}
    		newFeeTask.setRechargeCode(defaultMaintERSFeeRechargeCode);
    		newFeeTask.setRechargeQty(new BigDecimal(1));
    		newFeeTask.setRechargeUnitCost(miscERSFee);
    		newFeeTask.setRechargeTotalCost(newFeeTask.getRechargeUnitCost().multiply(newFeeTask.getRechargeQty()));
    		newFeeTask.setActualRechargeCost(newFeeTask.getRechargeTotalCost());
    		newFeeTask.setDiscountFlag(MalConstants.FLAG_N);
    		newFeeTask.setOutstanding(DataConstants.DEFAULT_N);
    		newFeeTask.setWasOutstanding(DataConstants.DEFAULT_N); 
    		newFeeTask.setAuthorizePerson(username);
    		newFeeTask.setAuthorizeDate(Calendar.getInstance().getTime());
    		newFeeTask.setLineNumber(maxLineNumber+=1);
    	}	

		return newFeeTask;
	}
	//HPS-2946 ends

	@Override
	public boolean hasWaivedOutOfNetworkSurcharge(MaintenanceRequest mrq) {
		boolean retVal = false;
		String defaultMarkUpCode = willowConfigService.getConfigValue("MAINT_NON_NETWORK_CODE");
		
		for(MaintenanceRequestTask task : mrq.getMaintenanceRequestTasks()){
			if(task.getMaintenanceCode().getCode().equalsIgnoreCase(defaultMarkUpCode) && task.isCostAvoidanceIndicator() && task.getCostAvoidanceCode().equalsIgnoreCase("WAIVED_ONF")){
				retVal = true;
				break;
			}	
		}
		
		return retVal;
	}

	@Override
	public boolean isMaintenanceRequestEditable(MaintenanceRequest mrq) {
		if(this.convertPOStatus(mrq.getMaintReqStatus()).getSortOrder() > 4
				&& this.convertPOStatus(mrq.getMaintReqStatus()).getSortOrder() != 8 ){ 
			return false;
		}else{
			return true;
		}
	}

	@Override
	public ExternalAccount getPayeeAccount(String accountCode, String accountType,Long cId) {
		return externalAccountDAO.findByAccountCodeAndAccountTypeAndCId(accountCode, accountType, cId);
	}
	
}
