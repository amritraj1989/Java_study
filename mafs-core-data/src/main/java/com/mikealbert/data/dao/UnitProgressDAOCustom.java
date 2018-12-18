package com.mikealbert.data.dao;

import java.util.Date;
import java.util.List;

import com.mikealbert.data.enumeration.OrderToDeliveryProcessStageEnum;
import com.mikealbert.exception.MalException;


/**
* Custom DAO for UpfitProgressChasing 
* @author ravresh
*/

public interface UnitProgressDAOCustom {
	
	public List<Object[]> getBasicAcceptedQuotesDetailForUpfitProgressChasing() throws MalException;
	public List<Object[]> getPendingInServiceUnit(String unitNo) throws MalException;
	public List<Object[]> getPendingInServiceUnitsList() throws MalException;	
	public List<Object[]> getUpfitTPTPOList() throws MalException ; 
	public List<Object[]> getUpfitTPTPOListByQmdId(Long qmdId) throws MalException ; 	
	public List<Object[]> getReconciledUpfitListByQmdId(Long qmdId, List<String> statuses, Boolean includeMainPO) throws MalException;
	public List<Object[]> getSelectedUnitDetails(Long fmsId) throws MalException;
	
	public Long getLatestOdoReading(Long fmsId) throws MalException;
	public Long getVehicleOdoReadingByQmdId(Long qmdId);
	
	public Long getMainPoDocIdByFmsId(Long fmsId);
	
	public void putUnitInService(String unitNo, Long contextId, Date dealerReceivedDate, Date inServiceDate,Long odoReading,
			 					Date odoReadingDate, String odoReadingType,String userId) throws MalException;
	
	//Added for Bug 16598
	public void updateInService(Long fmsId,Date inServiceDate) throws MalException;

	public String getGrdStatus(Long cId, String accountType, String accountCode, Long celId, Long docId);
	
	public Object[] getUpdatedSupllierProgressData(Long docId);
	
	public String grnCreatedYn(Long fmsId);
	
	public boolean getItemToleranceFlag(Long docId, OrderToDeliveryProcessStageEnum processStage) throws MalException;	
}
