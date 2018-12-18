package com.mikealbert.vision.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.dao.SupplierDAO;
import com.mikealbert.data.dao.SupplierProgressHistoryDAO;
import com.mikealbert.data.entity.Doc;
import com.mikealbert.data.entity.Driver;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.Supplier;
import com.mikealbert.data.entity.SupplierProgressHistory;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.data.enumeration.UnitProgressCodeEnum;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;
import com.mikealbert.service.WillowConfigService;
import com.mikealbert.service.util.email.Email;
import com.mikealbert.service.util.email.EmailAddress;
import com.mikealbert.util.MALUtilities;

@Service("supplierService")
public class SupplierServiceImpl implements SupplierService {

	 @Resource WillowConfigService willowConfigService; 
	 @Resource SupplierProgressHistoryDAO supplierProgressHistoryDAO;
	 @Resource PurchasingEmailService purchasingEmailService;
	 @Resource SupplierDAO supplierDAO;
		
	 private MalLogger logger = MalLoggerFactory.getLogger(this.getClass());	 
	 
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void createSupplierProgress(Long docId, String progressType, String opCode, Date actionDate, Date enteredDate , String supplier) {
		
		if(supplier == null){			
			supplier =  willowConfigService.getConfigValue("DEFAULT_SUPPLIER");				
		}
		
		List<SupplierProgressHistory> supplierProgressHistoryList  = supplierProgressHistoryDAO.findSupplierProgressHistory(docId, progressType, actionDate, supplier);
		
	
		if(supplierProgressHistoryList == null || supplierProgressHistoryList.size() == 0){
			
			SupplierProgressHistory newSupplierProgressHistory = new SupplierProgressHistory();
			newSupplierProgressHistory.setDocId(docId);
			newSupplierProgressHistory.setProgressType(progressType);
			newSupplierProgressHistory.setEnteredDate(enteredDate);
			newSupplierProgressHistory.setActionDate(actionDate);
			newSupplierProgressHistory.setOpCode(opCode);
			newSupplierProgressHistory.setSupplier(supplier);
			
			supplierProgressHistoryDAO.save(newSupplierProgressHistory);
			
		 }
		
		
		}
		
	/* 
	 * Updated for OTD-1001 : Sort parameter added 
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public List<SupplierProgressHistory> getSupplierProgressHistory(Long docId, Sort sort) {
		
		List<SupplierProgressHistory> supplierProgressHistoryList  = new ArrayList<SupplierProgressHistory>() ;
		List<SupplierProgressHistory> originalSupplierProgressHistoryList  = supplierProgressHistoryDAO.findSupplierProgressHistoryForDoc(docId, sort);
 		if (originalSupplierProgressHistoryList != null && originalSupplierProgressHistoryList.size() > 0) {
 			SupplierProgressHistory lastProgressEntry = originalSupplierProgressHistoryList.get(0);

 			for(SupplierProgressHistory sph : originalSupplierProgressHistoryList) {
 				if(! sph.getProgressType().equalsIgnoreCase(lastProgressEntry.getProgressType())) {
 					supplierProgressHistoryList.add(lastProgressEntry);
 				}
 				lastProgressEntry = sph;
 			}
 			supplierProgressHistoryList.add(lastProgressEntry);
 		}
		
		return supplierProgressHistoryList;
	}
	
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public List<SupplierProgressHistory> getSupplierProgressHistory(Long docId, boolean fetchAll) {	
		if(fetchAll){
			return supplierProgressHistoryDAO.findSupplierProgressHistoryForDoc(docId, new Sort(new Sort.Order(Direction.ASC, "progressType"), new Sort.Order(Direction.DESC, "sphId")));
		}else{
			return getSupplierProgressHistory(docId, new Sort(new Sort.Order(Direction.ASC, "progressType"), new Sort.Order(Direction.ASC, "sphId")));
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public void saveSupplierProgressHistory(SupplierProgressHistory supplierProgressHistory) throws MalBusinessException, MalException {
		supplierProgressHistoryDAO.save(supplierProgressHistory);
	}
	
	/**
	 * A two part transaction (separate transaction boundaries) that saves or updates supplier progress history status and/or
	 * email the vehicle ready notification to the client's POC 
	 * 
	 * When adding a VEHRDY status, business rule mandates that an email be sent to the client's POC notifying
	 * them that the vehicle is ready.
	 *  
	 * @param SupplierProgressHistory Supplier progress history record
	 * @param String Client session user
	 * @exception MalException 
	 */
	@Transactional(rollbackFor = Exception.class)
	public void saveVehicleReadyAndSendNotification(SupplierProgressHistory supplierProgressHistory,  Doc doc, FleetMaster fleetMaster, QuotationModel quotationModel ,Driver driver, String username, CorporateEntity coproateEntity) throws MalBusinessException, MalException {
		supplierProgressHistoryDAO.save(supplierProgressHistory);
		purchasingEmailService.emailVehicleReadyNotification(supplierProgressHistory, doc ,fleetMaster , quotationModel, driver , username, coproateEntity);
	}
		
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteSupplierProgressHistory(Long sphId){
		supplierProgressHistoryDAO.deleteById(sphId);
	}
	
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void performPostSupplierUpdateJob(String checkWillowConfig, String checkRemainder, Long cId,
			String progressType, Date enteredDate, Date actionDate, Long fmsId,
			Long makeId, Long qmdId, Long docId, String enteredBy, String supplier) throws MalBusinessException {
		
		try {
			supplierProgressHistoryDAO.performPostSupplierUpdateJob(checkWillowConfig, checkRemainder, cId,
																	progressType, enteredDate, actionDate, fmsId, makeId, qmdId,
																	docId, enteredBy,supplier);
		} catch (Exception ex) {
			throw new MalBusinessException("generic.error.occured.while", new String[] { "saving Supplier Progress History of type 14_ETA" }, ex);
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public List<SupplierProgressHistory>  getSupplierProgress(Long docId, String progressType, Date actionDate) {
		return supplierProgressHistoryDAO.findSupplierProgressHistory(docId, progressType, actionDate);
	}

	@Override
	public SupplierProgressHistory getSupplierProgressHistoryForDocAndType(Long docId, String progressType) {
		return supplierProgressHistoryDAO.findSupplierProgressHistoryForDocAndTypeById(docId, progressType);
	}
	
	@Override
	public SupplierProgressHistory getSupplierProgressHistoryByDocAndTypeExcludeSphId(SupplierProgressHistory sph, String progressType){
		return supplierProgressHistoryDAO.findByDocIdAndTypeByIdAndExcludedSphId(sph.getSphId(), sph.getDocId(), progressType);
	}
	
	@Override
	public boolean hasProgressStatus(Doc purchaseOrder, UnitProgressCodeEnum progressType) {		
		return hasProgressStatus(purchaseOrder.getDocId(), progressType);
	}
	
	@Override
	public List<Supplier> getSuppliers(CorporateEntity corprateEntity, String accountCode){
		return supplierDAO.getSuppliersByCidAndAccountCode(corprateEntity.getCorpId(), accountCode);		
	}

	/*
	 * Determines whether a supplier progress history status exist for a given 
	 * purchase order.
	 * @param Long Doc id 
	 * @param UnitProgressCodeEnum The progress type
	 * @return boolean Indicating whether progress type exists or not
	 */
	private boolean hasProgressStatus(Long docId, UnitProgressCodeEnum progressType) {
		boolean hasProgressStatus;
		SupplierProgressHistory history;
		
		history = getSupplierProgressHistoryForDocAndType(docId, progressType.getCode());
		hasProgressStatus = MALUtilities.isEmpty(history) ? false : true;
		
		return hasProgressStatus;
	}	
		
}
