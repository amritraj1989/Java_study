package com.mikealbert.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.data.entity.UpfitterProgress;
import com.mikealbert.data.enumeration.DocumentStatus;
import com.mikealbert.data.vo.VendorInfoVO;

/**
* Public Interface implemented by {@link com.mikealbert.vision.service.UpfitProgressServiceImpl} 
* for interacting with business service methods concerning: 
* 
*  @see com.mikealbert.data.entity.ExternalAccount
*  @see com.mikealbert.data.entity.ExternalAccountPK
* */
public interface UpfitProgressService {
	//TODO Should handle saving single vendor and setting the bailment when appleciable 
	public List<UpfitterProgress> generateUpfitProgressList(Long qmdId, String unitNo, List<DocumentStatus> documentStatuses, Long mainPoDocId) throws Exception;
	public List<UpfitterProgress> generateAndSaveSingleItemUpfitterProgressList(Long qmdId, String unitNo, List<DocumentStatus> documentStatuses, String updatedBy, Long mainPoDocId, Long mdlId) throws Exception;	
	public List<UpfitterProgress> getUpfitProgressList(Long qmdId);
	public boolean hasUpfitProgressList(Long qmdId);
	public UpfitterProgress getUpfitterProgress(Long ufpId);
	public List<UpfitterProgress> saveOrUpdateUpfitterProgress(List<UpfitterProgress> upfitterProgressList, String updatedBy, Long qmdId, String unitNo, Long mainPoDocId, Long mdlId) throws Exception;	
	public List<VendorInfoVO> readonlyVendorList(Long qmdId, String unitNo, List<DocumentStatus> documentStatuses, boolean excludeZeroLeadTime, boolean reconcileDiff, boolean includeMainPO, Long mainPoDocId) throws Exception;
	public boolean isUpfitCompleted(Long qmdId, String upfitterAccountCode);@Transactional(rollbackFor=Exception.class)
	public void save(List<UpfitterProgress> upfitterProgressList, String updatedBy, Long qmdId, String unitNo, Long mainPoDocId, Long mdlId) throws Exception;
	public void delete(List<UpfitterProgress> upfitterProgressList, String unitNo) throws Exception;	
	

}
