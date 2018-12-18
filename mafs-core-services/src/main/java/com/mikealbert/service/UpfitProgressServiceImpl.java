package com.mikealbert.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.comparator.UpfitterProgressComparator;
import com.mikealbert.data.comparator.VendorInfoVOComparator;
import com.mikealbert.data.dao.DocDAO;
import com.mikealbert.data.dao.DoclDAO;
import com.mikealbert.data.dao.ExternalAccountDAO;
import com.mikealbert.data.dao.FleetMasterDAO;
import com.mikealbert.data.dao.OrderProgressDAO;
import com.mikealbert.data.dao.ProcessStageDAO;
import com.mikealbert.data.dao.QuotationModelDAO;
import com.mikealbert.data.dao.SupplierProgressHistoryDAO;
import com.mikealbert.data.dao.UnitProgressDAO;
import com.mikealbert.data.dao.UpfitterProgressDAO;
import com.mikealbert.data.entity.DocPropertyValue;
import com.mikealbert.data.entity.ExternalAccountPK;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.ObjectLogBook;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.SupplierProgressHistory;
import com.mikealbert.data.entity.UpfitterProgress;
import com.mikealbert.data.enumeration.AcquisitionTypeEnum;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.data.enumeration.DocPropertyEnum;
import com.mikealbert.data.enumeration.DocumentStatus;
import com.mikealbert.data.enumeration.LogBookTypeEnum;
import com.mikealbert.data.enumeration.UnitProgressCodeEnum;
import com.mikealbert.data.vo.VendorInfoVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.util.MALUtilities;

/**
 * Implementation of {@link com.mikealbert.vision.service.UpfitProgressService}
 */
@Service("upfitProgressService")
public class UpfitProgressServiceImpl implements UpfitProgressService {
	@Resource QuotationModelDAO quotationModelDAO;
	@Resource UpfitterProgressDAO upfitterProgressDAO;
	@Resource QuotationService quotationService;
	@Resource PurchaseOrderService purchaseOrderService;
	@Resource ExternalAccountDAO externalAccountDAO;
	@Resource OrderProgressDAO orderProgressDAO;
	@Resource DocDAO docDAO;
	@Resource ProcessStageDAO processStageDAO;
	@Resource UnitProgressDAO unitProgressDAO;
	@Resource DocPropertyValueService docPropertyValueService;
	@Resource LogBookService logBookService;
	@Resource SupplierProgressHistoryDAO supplierPrgHistDAO;
	@Resource FleetMasterService fleetMasterService;
	@Resource DoclDAO doclDAO;
	@Resource SupplierProgressHistoryDAO supplierProgressHistoryDAO;
	@Resource FleetMasterDAO fleetMasterDAO;
	
	private MalLogger logger = MalLoggerFactory.getLogger(this.getClass());
	
	/**
	 * Compiles a list of UpfitterProgress based on currently saved UpfitterProgress records 
	 * and 3rd party POs generated for the quote. The two list will me reconciled into one.
	 * The generated list is not saved to the database.
	 * @param qmdId QuoationModel.qmdId
	 * @param List<DocumentStatus> The status of the 3rd party Purchase Orders
	 * @return A reconciled list of UpiftterProgress
	 */
	@Transactional(rollbackFor=Exception.class)
	public List<UpfitterProgress> generateUpfitProgressList(Long qmdId, String unitNo, List<DocumentStatus> documentStatuses, Long mainPoDocId) throws Exception{
		List<UpfitterProgress> upfitProgressList;
		List<String> upfitStatuses;
		Long unitPODocId;
		DocPropertyValue unitPOAcquisitionType;		
		upfitStatuses = convertDocumentStatusToStringList(documentStatuses);
		

		upfitProgressList = produceUpfitProgressList(qmdId, unitNo, upfitStatuses, true, true);
		for(UpfitterProgress upfitProgress : upfitProgressList) {
			
			//TODO Defaulting bailment unfitter. Please extract logic into a method for re-use
			if(upfitProgressList.size() == 1) {
				unitPODocId = mainPoDocId != null ? mainPoDocId : docDAO.getUnitPurchaseOrderDocIdFromQmdId(qmdId, DocumentStatus.PURCHASE_ORDER_STATUS_RELEASED.getCode());
				unitPOAcquisitionType = docPropertyValueService.findByNameDocId(DocPropertyEnum.ACQUISITION_TYPE, unitPODocId);				
				if(!MALUtilities.isEmpty(unitPOAcquisitionType) && unitPOAcquisitionType.getPropertyValue().equals(AcquisitionTypeEnum.BAIL.getCode())) {
					upfitProgressList.get(0).setBailmentYN("Y");
				}
			}
			
			//Lazy loading as advised by the OPU team. Found issues with parent reference with this approach, need to investigate later when time permits
			//upfitProgress.setChildrenUpfitterProgress(upfitterProgressDAO.findChildrenByParentUfpId(upfitProgress.getUfpId()));
			if(!MALUtilities.isEmpty(upfitProgress.getChildrenUpfitterProgress())) {
				upfitProgress.getChildrenUpfitterProgress().size();				
			}
			upfitProgress.setPersistedStartDate(upfitProgress.getStartDate());
			upfitProgress.setPersistedEndDate(upfitProgress.getEndDate());
			upfitProgress.setPersistedParentUpfitterProgress(upfitProgress.getPersistedParentUpfitterProgress());
		}	    		    	  
	    return upfitProgressList;	    
	}
	
	/**
	 * Generates the UpfitterProgress list. If the list size is 1, then it is saved to the DB.
	 * @param qmdId QuoationModel.qmdId
	 * @param List<DocumentStatus> The status of the 3rd party Purchase Orders
	 * @param String the Operator Code of the user that initiated this transaction
	 * @return A reconciled list of UpiftterProgress. The listed is persisted when it contains only one item.
	 */
	@Transactional
	public List<UpfitterProgress> generateAndSaveSingleItemUpfitterProgressList(Long qmdId, String unitNo, List<DocumentStatus> documentStatuses, String updatedBy, Long mainPoDocId, Long mdlId) throws Exception {
		List<UpfitterProgress> upfitProgressList;  
		
		upfitProgressList = generateUpfitProgressList(qmdId, unitNo, documentStatuses, mainPoDocId);
		
		if(upfitProgressList.size() == 1) {					
			upfitProgressList = saveOrUpdateUpfitterProgress(upfitProgressList, updatedBy, qmdId, unitNo, mainPoDocId, mdlId);				
		}
		
		return upfitProgressList;
	}

	/**
	 * 
	 */
	@Transactional(rollbackFor=Exception.class)	
	public List<UpfitterProgress> saveOrUpdateUpfitterProgress(List<UpfitterProgress> upfitterProgressList, String updatedBy, Long qmdId, String unitNo, Long mainPoDocId, Long mdlId) throws Exception{
		List<UpfitterProgress> persistedUpfitterProgressList;
		save(upfitterProgressList, updatedBy, qmdId, unitNo, mainPoDocId, mdlId);
		delete(upfitterProgressDAO.findByQmdId(qmdId), unitNo);
		
		persistedUpfitterProgressList = upfitterProgressDAO.findByQmdId(qmdId);
		Collections.sort(persistedUpfitterProgressList, new UpfitterProgressComparator());	
		return persistedUpfitterProgressList;
	}
	
	@Transactional(propagation=Propagation.REQUIRES_NEW, rollbackFor=Exception.class)
	public void save(List<UpfitterProgress> upfitterProgressList, String updatedBy, Long qmdId, String unitNo, Long mainPoDocId, Long mdlId) throws Exception {
		Long unitDocId;
		List<SupplierProgressHistory> supplierProgressHistory;
		List<UnitProgressCodeEnum> progressTypesToInsert;
		SupplierProgressHistory newSupplierProgressHistory = new SupplierProgressHistory();
		String maxIntDlrSegment = null;
		String maxShipSegment = null;		
		boolean addStartDate = false;
		boolean addEndDate = false;
		boolean saveShipDate = false;
		FleetMaster fleetMaster;
		//Validations
		if(MALUtilities.isEmpty(upfitterProgressList.get(0).getUfpId()) || upfitterProgressList.get(0).getUfpId() == -1L) {
			if(upfitterProgressDAO.findByQmdId(qmdId).size() > 0) {
				throw new MalBusinessException("record.updated.by.another.user");					
			}
		}		
		for(UpfitterProgress ufp : upfitterProgressList) {
				
			ufp.setUpdatedBy(updatedBy);
			
			if(MALUtilities.isEmpty(ufp.getUfpId()) || ufp.getUfpId() < 0) {
				ufp.setUfpId(null);
			}			
			ufp.setQmdId(qmdId);
			ufp.setQuotationModel(null); // Did this as saving qmdId
			
			upfitterProgressDAO.saveAndFlush(ufp);
			//TODO Check for addition of date, if added, insert into supplier progress history starting with 09_XX.
			addStartDate = false;
			addEndDate = false;
			saveShipDate = false;
			if(!MALUtilities.isEmpty(ufp.getStartDate()) && MALUtilities.isEmpty(ufp.getPersistedStartDate())) {
				addStartDate = true;
			}
			if(!MALUtilities.isEmpty(ufp.getEndDate()) && MALUtilities.isEmpty(ufp.getPersistedEndDate())) {
				addEndDate = true;
			}	
			if(addStartDate || addEndDate) {
				progressTypesToInsert = new ArrayList<UnitProgressCodeEnum>();
				
				unitDocId = mainPoDocId != null ? mainPoDocId : docDAO.getUnitPurchaseOrderDocIdFromQmdId(qmdId, DocumentStatus.PURCHASE_ORDER_STATUS_RELEASED.getCode());
				fleetMaster = fleetMasterDAO.findByUnitNo(unitNo);				
				supplierProgressHistory = supplierProgressHistoryDAO.findSupplierProgressHistoryForDoc(unitDocId, new Sort(new Sort.Order(Direction.ASC, "progressType")));

				for(SupplierProgressHistory sph : supplierProgressHistory) {
					if(sph.getProgressTypeCode().getProgressType().equals(UnitProgressCodeEnum.INT_DLR.getCode())
							|| sph.getProgressTypeCode().getProgressType().equals(UnitProgressCodeEnum.INT_DLR_11.getCode())
							|| sph.getProgressTypeCode().getProgressType().equals(UnitProgressCodeEnum.INT_DLR_12.getCode())
							|| sph.getProgressTypeCode().getProgressType().equals(UnitProgressCodeEnum.INT_DLR_13.getCode()) ) {
						maxIntDlrSegment = sph.getProgressTypeCode().getProgressType().substring(0, 2);
					}
					if(sph.getProgressTypeCode().getProgressType().equals(UnitProgressCodeEnum.SHIP2.getCode())
							|| sph.getProgressTypeCode().getProgressType().equals(UnitProgressCodeEnum.SHIP3_11.getCode())
							|| sph.getProgressTypeCode().getProgressType().equals(UnitProgressCodeEnum.SHIP4_12.getCode())							
							|| sph.getProgressTypeCode().getProgressType().equals(UnitProgressCodeEnum.SHIP5_13.getCode()) ) {
						maxShipSegment = sph.getProgressTypeCode().getProgressType().substring(0, 2);
					}					
				}
				
				if(addStartDate) {
					if(MALUtilities.isEmpty(maxIntDlrSegment)) {
						progressTypesToInsert.add(UnitProgressCodeEnum.INT_DLR);					
					} else if (maxIntDlrSegment.equals("09")) {
							progressTypesToInsert.add(UnitProgressCodeEnum.INT_DLR_11);
					} else if(maxIntDlrSegment.equals("11")) {
						progressTypesToInsert.add(UnitProgressCodeEnum.INT_DLR_12);
					} else if(maxIntDlrSegment.equals("12")) {
						progressTypesToInsert.add(UnitProgressCodeEnum.INT_DLR_13);
					} else {
						logger.warning("Exceeded supplier progress history xx_INT_DLR progress type code segement");
					}
				}

				if(addEndDate) {
					if(MALUtilities.isEmpty(maxShipSegment)) {
						progressTypesToInsert.add(UnitProgressCodeEnum.SHIP2);	
						saveShipDate = true;
					} else if (maxShipSegment.equals("10")) {
						progressTypesToInsert.add(UnitProgressCodeEnum.SHIP3_11);
						saveShipDate = true;
					} else if(maxShipSegment.equals("11")) {
						progressTypesToInsert.add(UnitProgressCodeEnum.SHIP4_12);
						saveShipDate = true;
					} else if(maxShipSegment.equals("12")) {
						progressTypesToInsert.add(UnitProgressCodeEnum.SHIP5_13);
						saveShipDate = true;
					} else {
						logger.warning("Exceeded supplier progress history xx_SHIPn progress type code segement");
					}
					
				}								
		
				for(UnitProgressCodeEnum upc : progressTypesToInsert) {
				    newSupplierProgressHistory = new SupplierProgressHistory();	
					newSupplierProgressHistory.setDocId(unitDocId);
					newSupplierProgressHistory.setProgressType(upc.getCode());
					newSupplierProgressHistory.setEnteredDate(Calendar.getInstance().getTime());
					if(saveShipDate){
						newSupplierProgressHistory.setActionDate(ufp.getEndDate());
					}else{
						newSupplierProgressHistory.setActionDate(ufp.getStartDate());
					}
					newSupplierProgressHistory.setOpCode(ufp.getUpdatedBy());
					newSupplierProgressHistory.setSupplier(ufp.getUpfitter().getAccountName());					
					supplierProgressHistoryDAO.save(newSupplierProgressHistory);
					
					supplierProgressHistoryDAO.performPostSupplierUpdateJob("Y", "Y", ufp.getUpfitter().getExternalAccountPK().getCId(), upc.getCode(), 
							newSupplierProgressHistory.getEnteredDate(), newSupplierProgressHistory.getActionDate(), 
							fleetMaster.getFmsId(), mdlId, qmdId,
							unitDocId, newSupplierProgressHistory.getOpCode(), null);
				}				
			}				
		}			
		performPostUpfitCompleteTaskForUnit (upfitterProgressList, updatedBy, unitNo, mainPoDocId);		
	}
	
	/**
	 * Delete items that are no longer in the list. This will occur when an upfitter PO has been 
	 *	canceled, i.e. when the business user cancels the third party PO via "Create Change". 
	 * List 1 should contain the current state/produced unit progress list. List 2 should contain
	 * the persisted state. Each list should contain upfitters for 'O'pen and 'R'eleased POs.
	 * @param upfitterProgressList
	 * @throws Exception
	 */
	@Transactional(propagation=Propagation.REQUIRES_NEW, rollbackFor=Exception.class)
	public void delete(List<UpfitterProgress> upfitterProgressList, String unitNo) throws Exception {			
		List<UpfitterProgress> producedUpfitterProgressList;
		List<String> upfitPOStatuses;
		
		upfitPOStatuses = new ArrayList<String>();
		upfitPOStatuses.add(DocumentStatus.PURCHASE_ORDER_STATUS_OPEN.getCode());
		upfitPOStatuses.add(DocumentStatus.PURCHASE_ORDER_STATUS_RELEASED.getCode());
		
		producedUpfitterProgressList = produceUpfitProgressList(upfitterProgressList.get(0).getQmdId(), unitNo, upfitPOStatuses, true, true);
		
		for(UpfitterProgress ufp : upfitterProgressList){
			if(!producedUpfitterProgressList.contains(ufp) && MALUtilities.isEmpty(ufp.getStartDate()) && MALUtilities.isEmpty(ufp.getEndDate())){
				upfitterProgressDAO.deleteById(ufp.getUfpId());
			}			
		}
				
	}
	
	public void performPostUpfitCompleteTaskForUnit (List<UpfitterProgress> upfitterProgressList, String updatedBy, String unitNo, Long mainPoDocId) {
		
		if(upfitterProgressList.size() > 0){
			
			boolean allTaskCompleted = true;			
			for (UpfitterProgress upfitterProgress : upfitterProgressList) {				
				if(upfitterProgress.getEndDate() == null && upfitterProgress.getParentUpfitterProgress() == null){
					allTaskCompleted = false;
				}
			}
			
			if(allTaskCompleted){
				
				FleetMaster fleetMaster = fleetMasterService.findByUnitNo(unitNo);
				SupplierProgressHistory sph = new SupplierProgressHistory();
				Long docId = mainPoDocId != null ? mainPoDocId : unitProgressDAO.getMainPoDocIdByFmsId(fleetMaster.getFmsId());
				String supplierNote = "All jobs completed";
				sph.setDocId(docId);
				sph.setProgressType(UnitProgressCodeEnum.UFCMPLT.getCode());
				sph.setSupplier(supplierNote);
				sph.setOpCode(updatedBy);
				sph.setActionDate(getLastEndDate(upfitterProgressList));
				sph.setEnteredDate(new Date());
				sph = supplierPrgHistDAO.saveAndFlush(sph);
		        ObjectLogBook olb = logBookService.createObjectLogBook(fleetMaster, LogBookTypeEnum.TYPE_UPFIT_PRG_NOTES);			
				logBookService.addEntry(olb, updatedBy,  "Upfits complete. Pending In Service.", null, false);
				
			}
		}
	}
	
	private Date getLastEndDate(List<UpfitterProgress> upfitterProgressList){
		Date dt = null;
		for(int i = upfitterProgressList.size()-1; i>=0; i--){
			if(!MALUtilities.isEmpty(upfitterProgressList.get(i).getEndDate())){
				dt = upfitterProgressList.get(i).getEndDate();
				break;
			}
		}
		
		return dt;
	}
	
	/**
	 * 
	 */
	public List<UpfitterProgress> getUpfitProgressList(Long qmdId) {
		List<UpfitterProgress> upfitterProgressList;
		upfitterProgressList = upfitterProgressDAO.findByQmdId(qmdId);		
		return upfitterProgressList;
	}
	
	/**
	 * 
	 */
	public boolean hasUpfitProgressList(Long qmdId) {
		boolean isListGenerated = false;
		List<UpfitterProgress> upfitProgressList;
		
		upfitProgressList = upfitterProgressDAO.findByQmdId(qmdId);
		if(!MALUtilities.isEmpty(upfitProgressList) && upfitProgressList.size() > 0) {
			isListGenerated = true;
		}
		
		return isListGenerated;
	}
	
	/**
	 * 
	 */
	public UpfitterProgress getUpfitterProgress(Long ufpId) {
		UpfitterProgress upfitterProgress;
		upfitterProgress = upfitterProgressDAO.findById(ufpId).orElse(null);
		return upfitterProgress;
	}
	
	/**
	 * TODO Consider using the TPOList approach. IT will ensure list is consistent.
	 */
	@Transactional(readOnly=true)
	public List<VendorInfoVO> readonlyVendorList(Long qmdId, String unitNo, List<DocumentStatus> documentStatuses, boolean excludeZeroLeadTime, boolean reconcileDiff, boolean includeMainPO, Long mainPoDocId) throws Exception{
		List<VendorInfoVO> vendorInfoVOs = null;;
		List<UpfitterProgress> upfitProgressList;
		VendorInfoVO vendorInfoVO;
		Long mainDocId, upfitDocId = null;
		List<Long> upfitDocIds;
		ExternalAccountPK externalAccountPK;
		Long leadTime =0L;
		List<String> upfitDocStatuses;

		upfitDocStatuses = convertDocumentStatusToStringList(documentStatuses);
		
		vendorInfoVOs = new ArrayList<VendorInfoVO>();	 
		
		if(reconcileDiff) {
			upfitProgressList = produceUpfitProgressList(qmdId, unitNo, upfitDocStatuses, excludeZeroLeadTime, includeMainPO);			
		} else {
			upfitProgressList = upfitterProgressDAO.findByQmdId(qmdId);
		}
		
		for(UpfitterProgress ufp : upfitProgressList) {
			externalAccountPK = ufp.getUpfitter().getExternalAccountPK();
			mainDocId = mainPoDocId != null ? mainPoDocId : docDAO.getUnitPurchaseOrderDocIdFromQmdId(qmdId, DocumentStatus.PURCHASE_ORDER_STATUS_RELEASED.getCode());
			upfitDocIds = null;
			upfitDocIds = docDAO.getUpfitPurchaseOrderDocIdsFromQmdIdAndAccount(qmdId, upfitDocStatuses, externalAccountPK.getCId(), externalAccountPK.getAccountCode(), externalAccountPK.getAccountType(), includeMainPO);
			leadTime = 0L;					
				
			
			//It is possible to have multiple POs for the same vendor; very rare but could happen
			if(!MALUtilities.isEmpty(upfitDocIds) && upfitDocIds.size() > 0){
				
				//TODO Need to handle Main PO lead time
				for(Long docId : upfitDocIds) {
					leadTime += purchaseOrderService.getPurchaseOrderLeadTimeByDocId(docId);
				}

				//HACK: Treat multiple POs for the same Vendor as one
				upfitDocId = upfitDocIds.get(0);
			}
			
			vendorInfoVO = new VendorInfoVO(ufp.getUpfitter().getAccountName(), externalAccountPK.getAccountCode(), externalAccountPK.getCId(), mainDocId, upfitDocId, leadTime.toString());
			vendorInfoVO.setSequenceNo(ufp.getSequenceNo());
			vendorInfoVO.setLinked(MALUtilities.isEmpty(ufp.getParentUpfitterProgress()) ? false : true);
			vendorInfoVO.setVendorTaskCompleted(
					(!MALUtilities.isEmpty(ufp.getParentUpfitterProgress()) && !MALUtilities.isEmpty(ufp.getParentUpfitterProgress().getEndDate()))
					|| (MALUtilities.isEmpty(ufp.getParentUpfitterProgress()) && !MALUtilities.isEmpty(ufp.getEndDate())) );
			vendorInfoVOs.add(vendorInfoVO);
		}	
		
		Collections.sort(vendorInfoVOs, new VendorInfoVOComparator());		
	    
		return vendorInfoVOs;
	}
	
	
	/**
	 * 
	 */
	public boolean isUpfitCompleted(Long qmdId, String upfitterAccountCode) {
		boolean isCompleted = false;
		
		List<UpfitterProgress> upfitterProgressList = upfitterProgressDAO.findByQmdId(qmdId);
		for(UpfitterProgress ufp : upfitterProgressList) {
			if(ufp.getUpfitter().getExternalAccountPK().getAccountCode().equals(upfitterAccountCode)
					&& !MALUtilities.isEmpty(ufp.getEndDate())) {
				isCompleted = true;
				break;
			}
		}
		
		return isCompleted;
	}
		
	/**
	 * Produces a list of upfitters and their work progress information. The list source is the UNIT_PROGRESS table
	 * and 3rd party POs from the quotation model. When there are differences between the UNIT_PROGRESS and the list
	 * of 3rd party POs. the UNIT_PROGRESS list will be reconciled based on the following logic:
	 *     - When 3rd party PO is added, it will be added to UPFIT_PROGRESS
	 *     - When 3rd party PO is removed, provided that work has not been started or completed, it will be removed from UPFIT_PROGRESS
	 *     - When 3rd party PO exist in UPFIT_PROGRESS, it remains in UPFIT_PROGRESS
	 * @param qmdId Active quote
	 * @param updatedBy User that initiated the transaction
	 * @return Upfitter Progress Up-to-date upfiter progress list
	 * @throws MalBusinessException
	 */
	private List<UpfitterProgress> produceUpfitProgressList(Long qmdId, String unitNo, List<String> statuses, Boolean excludeZeroLeadTime, Boolean includeMainPO) throws Exception{
		List<UpfitterProgress> oldUpfitProgressList;			
		List<UpfitterProgress> newUpfitProgressList;					
		UpfitterProgress upfitterProgress;
		QuotationModel quotationModel;	
		Long dummyUfpId = 0L;
		List<Object[]> results;
		String vendorCode;	
		Long leadTime;		
		Set<String> poVendorSet;
		poVendorSet = new HashSet<String>();			
		oldUpfitProgressList = upfitterProgressDAO.findByQmdId(qmdId);
		newUpfitProgressList = new ArrayList<UpfitterProgress>();
		results = unitProgressDAO.getReconciledUpfitListByQmdId(qmdId, statuses, includeMainPO);
		quotationModel = quotationModelDAO.findById(qmdId).orElse(null);		
		
		quotationModel = new QuotationModel();
		quotationModel.setQmdId(qmdId);
		quotationModel.setUnitNo(unitNo);
		
		//Creates a distinct list of vendor accounts that need to be added
		//to the Upiftter Progreess List
		for(Object[] record : results) {
			vendorCode = (String) record[2];		    	
			leadTime = record[5] != null ? ((BigDecimal) record[5]).longValue() : 0L;				
			
			if(excludeZeroLeadTime) {
				if(leadTime > 0) {
					poVendorSet.add(vendorCode);							
				}
			} else {
				poVendorSet.add(vendorCode);						
			}							    		
			 	    			
		}
		
		//Handles scenario where PO was canceled.
		//Only add Upfitter Progress that are associated with a PO or when work has been started. 
		for(UpfitterProgress ufp : oldUpfitProgressList) {
			if(!MALUtilities.isEmpty(ufp.getStartDate())
					|| poVendorSet.contains(ufp.getUpfitter().getExternalAccountPK().getAccountCode())) {
				newUpfitProgressList.add(ufp);
				poVendorSet.remove(ufp.getUpfitter().getExternalAccountPK().getAccountCode());
			}
		}
		resequenceUpfitterProgressList(newUpfitProgressList);
		
		for(String poVendorCode : poVendorSet){
			upfitterProgress = new UpfitterProgress();	    			
			upfitterProgress.setUfpId(--dummyUfpId);
			upfitterProgress.setBailmentYN("N");
			upfitterProgress.setSequenceNo(nextSequenceNo(newUpfitProgressList));
			upfitterProgress.setQmdId(qmdId);
			upfitterProgress.setUpfitter(externalAccountDAO.findByAccountCodeAndAccountTypeAndCId(poVendorCode, "S", CorporateEntity.MAL.getCorpId()));
			newUpfitProgressList.add(upfitterProgress);				
		}
		
		
		Collections.sort(newUpfitProgressList, new UpfitterProgressComparator());
	    return newUpfitProgressList;			    
	}
	
	/**
	 * Resequences the Upfitter Progress List
	 * @param upfitterProgressList
	 * @return List Resequenced List
	 */
	private List<UpfitterProgress> resequenceUpfitterProgressList(List<UpfitterProgress> upfitterProgressList) {
		List<UpfitterProgress> resequencedList;
		Long sequenceNo = 0L;
		
		resequencedList = new ArrayList<UpfitterProgress>(upfitterProgressList);
		Collections.sort(resequencedList, new UpfitterProgressComparator());
		
		for(UpfitterProgress ufp : resequencedList) {
			if(MALUtilities.isEmpty(ufp.getParentUpfitterProgress())){
				ufp.setSequenceNo(++sequenceNo);
			} else {
				ufp.setSequenceNo(sequenceNo);				
			}
		}
		
		return resequencedList;
	}
	/**
	 * Retuns the next sequence number based on max sequence no in list
	 * @param upfitterProgressList
	 * @return Long max sequence no + 1
	 */
	private Long nextSequenceNo(List<UpfitterProgress> upfitterProgressList) {
		Long maxSeqNo = 0L;
		
		if(!MALUtilities.isEmpty(upfitterProgressList) && upfitterProgressList.size() > 0) {		
			Collections.sort(upfitterProgressList, new UpfitterProgressComparator());
		}
		
		for(UpfitterProgress ufp : upfitterProgressList){
			if(ufp.getSequenceNo() > 1000) {
				break;
			} else {
				maxSeqNo = ufp.getSequenceNo();
			}
		}
		
		return ++maxSeqNo;
	}
	
	private List<String> convertDocumentStatusToStringList(List<DocumentStatus> documentStatuses) {
		List<String> listOfStatuses = new ArrayList<String>();
		for(DocumentStatus docStatus : documentStatuses) {
			listOfStatuses.add(docStatus.getCode());
		}
		return listOfStatuses;
	}	

}
