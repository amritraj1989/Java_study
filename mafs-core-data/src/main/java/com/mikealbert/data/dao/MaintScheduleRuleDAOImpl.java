package com.mikealbert.data.dao;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.MaintScheduleRule;
import com.mikealbert.data.enumeration.ClientScheduleTypeCodes;

public class MaintScheduleRuleDAOImpl extends GenericDAOImpl<MaintScheduleRule, Long> implements MaintScheduleRuleDAOCustom {

	@Resource
	private MaintScheduleRuleDAO maintScheduleRuleDAO;
	private static final long serialVersionUID = 1L;

	/**
	 * Return a list of maintenance schedule rules by active, high mileage, and client schedule type
	 */
	@SuppressWarnings("unchecked")
	public List<MaintScheduleRule> getRulesByActiveAndHighMileageAndClientSchedType(String activeFlag, String highMileage, Long clientScheduleId, ExternalAccount externalAccount) {
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("SELECT msr FROM MaintScheduleRule msr, MasterSchedule ms");
		queryBuilder.append(" WHERE msr.masterSchedule.mschId = ms.mschId ");
		queryBuilder.append(" AND msr.activeFlag = :activeFlag ");
		queryBuilder.append(" AND msr.highMileage = :highMileage ");
		
		if(!"Y".equalsIgnoreCase(highMileage)){
			queryBuilder.append(" AND ms.clientScheduleType.cstId = :clientScheduleId");
			//Fix for MSS-1239, if high mileage no need to consider account condition for any client type
			if (clientScheduleId.equals(ClientScheduleTypeCodes.CLIENT.getCode())){
				queryBuilder.append(" AND msr.scheduleAccount.externalAccountPK.cId = :cId");
				queryBuilder.append(" AND msr.scheduleAccount.externalAccountPK.accountType = :accountType");
				queryBuilder.append(" AND msr.scheduleAccount.externalAccountPK.accountCode = :accountCode");
			} else {
				queryBuilder.append(" AND msr.scheduleAccount.externalAccountPK.accountCode is null");
			}
		}
		
		
		//mss-223 order by changed to accommodate the updated ordering preference.  This is actually not so important here because we sort the list again 
		//in the same manner when adding the base schedule records.
		//queryBuilder.append(" ORDER BY msr.year DESC, msr.makeCode ASC, msr.modelTypeDesc ASC, msr.makeModelDesc ASC, msr.fuelTypeGroup ASC");
		
		queryBuilder.append(" ORDER BY msr.year DESC, msr.makeCode ASC, " +
							"(CASE " +
							    "WHEN msr.makeCode IS NOT NULL and msr.makeModelDesc IS NULL THEN msr.fuelTypeGroup " + 
							    "ELSE msr.modelTypeDesc END) ASC, " + 
							    "msr.modelTypeDesc ASC, msr.makeModelDesc ASC, msr.fuelTypeGroup ASC");	
		
		Query query = entityManager.createQuery(queryBuilder.toString());
		
		query.setParameter("activeFlag", activeFlag);
		query.setParameter("highMileage", highMileage);
		
		if(!"Y".equalsIgnoreCase(highMileage)){
			query.setParameter("clientScheduleId", clientScheduleId);
			if (clientScheduleId.equals(ClientScheduleTypeCodes.CLIENT.getCode())){
				query.setParameter("cId", externalAccount.getExternalAccountPK().getCId());
				query.setParameter("accountType", externalAccount.getExternalAccountPK().getAccountType());
				query.setParameter("accountCode", externalAccount.getExternalAccountPK().getAccountCode());			
			}
		}
		
		return query.getResultList();
	}	
	
	/**
	 * Return the first year found greater than the year passed in given the specified criteria
	 */	
	public String getEndYear(MaintScheduleRule rule) {
		
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("SELECT min(msr.year) FROM MaintScheduleRule msr, MasterSchedule ms");
		queryBuilder.append(" WHERE msr.masterSchedule.mschId = ms.mschId ");
		queryBuilder.append(" AND msr.activeFlag = :activeFlag ");
		queryBuilder.append(" AND msr.highMileage = :highMileage ");
		queryBuilder.append(" AND ms.clientScheduleType.cstId = :clientScheduleId ");
		queryBuilder.append(" AND msr.year > :year ");
		
		if (rule.getMakeCode() != null){
			queryBuilder.append(" AND msr.makeCode = :makeCode");
		} else {
			queryBuilder.append(" AND msr.makeCode is null");
		}
		
		if (rule.getModelTypeDesc() != null){
			queryBuilder.append(" AND msr.modelTypeDesc = :modelTypeDesc");
		} else {
			queryBuilder.append(" AND msr.modelTypeDesc is null");
		}
		
		if (rule.getMakeModelDesc() != null){
			queryBuilder.append(" AND msr.makeModelDesc = :makeModelDesc");
		} else {
			queryBuilder.append(" AND msr.makeModelDesc is null");
		}
		
		if (rule.getFuelTypeGroup() != null){
			queryBuilder.append(" AND msr.fuelTypeGroup = :fuelTypeGroup");
		} else {
			queryBuilder.append(" AND msr.fuelTypeGroup is null");
		}
		
		if (rule.getMasterSchedule().getClientScheduleType().getCstId().equals(ClientScheduleTypeCodes.CLIENT.getCode())){
			queryBuilder.append(" AND msr.scheduleAccount.externalAccountPK.cId = :cId");
			queryBuilder.append(" AND msr.scheduleAccount.externalAccountPK.accountType = :accountType");
			queryBuilder.append(" AND msr.scheduleAccount.externalAccountPK.accountCode = :accountCode");
		} else {
			queryBuilder.append(" AND msr.scheduleAccount.externalAccountPK.accountCode is null");
		}
		
		queryBuilder.append(" ORDER BY msr.year DESC, msr.makeCode ASC, " +
							"(CASE " +
							    "WHEN msr.makeCode IS NOT NULL and msr.makeModelDesc IS NULL THEN msr.fuelTypeGroup " + 
							    "ELSE msr.modelTypeDesc END) ASC, " + 
							    "msr.modelTypeDesc ASC, msr.makeModelDesc ASC, msr.fuelTypeGroup ASC");	
		
		Query query = entityManager.createQuery(queryBuilder.toString());
		
		query.setParameter("activeFlag", rule.getActiveFlag());
		query.setParameter("highMileage", rule.getHighMileage());
		query.setParameter("clientScheduleId", rule.getMasterSchedule().getClientScheduleType().getCstId());

		if (rule.getMasterSchedule().getClientScheduleType().getCstId().equals(ClientScheduleTypeCodes.CLIENT.getCode())){
			query.setParameter("cId", rule.getScheduleAccount().getExternalAccountPK().getCId());
			query.setParameter("accountType", rule.getScheduleAccount().getExternalAccountPK().getAccountType());
			query.setParameter("accountCode", rule.getScheduleAccount().getExternalAccountPK().getAccountCode());			
		}

		query.setParameter("year", rule.getYear());
		
		if (rule.getMakeCode() != null){
			query.setParameter("makeCode", rule.getMakeCode());
		}
		
		if (rule.getModelTypeDesc() != null){
			query.setParameter("modelTypeDesc", rule.getModelTypeDesc());
		}
		
		if (rule.getMakeModelDesc() != null){
			query.setParameter("makeModelDesc", rule.getMakeModelDesc());
		}
		
		if (rule.getFuelTypeGroup() != null){
			query.setParameter("fuelTypeGroup", rule.getFuelTypeGroup());
		}
		
		String foundYear = null;
		try{
			foundYear = (String) query.getSingleResult();
		} catch (NoResultException nre){

		}

		return foundYear;
	}	

}
