package com.mikealbert.data.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import com.mikealbert.data.entity.MaintenancePreferenceAccount;
import com.mikealbert.data.vo.MaintenancePreferencesVO;

public class MaintenancePreferencesDAOImpl extends GenericDAOImpl<MaintenancePreferenceAccount, Long> implements MaintenancePreferencesDAOCustom {

	/**
	 * Find maintenance preferences for account
	 */
	private static final long serialVersionUID = 1L;	
	
	@SuppressWarnings("unchecked")
	public List<MaintenancePreferencesVO> getMaintenancePreferencesByAccount(Long cId, String accountType, String accountCode) {
		List<MaintenancePreferencesVO> maintenancePreferencesList	= new ArrayList<MaintenancePreferencesVO>();
		Query query = generateMaintenancePreferencesByAccountDataQuery(cId, accountType, accountCode);
		
		List<Object[]>resultList = (List<Object[]>)query.getResultList();
		if(resultList != null){
			for(Object[] record : resultList){
				int i = 0;
				
				MaintenancePreferencesVO maintenancePreferencesVO = new MaintenancePreferencesVO();				
				maintenancePreferencesVO.setPreferenceName((String)record[i]);
				maintenancePreferencesVO.setPreferenceDescription((String)record[i+=1]);
				maintenancePreferencesVO.setApproval((String)record[i+=1]);
				maintenancePreferencesVO.setMaintPack((String)record[i+=1]);
				maintenancePreferencesVO.setSpecialInstructions((String)record[i+=1]);
				
				maintenancePreferencesList.add(maintenancePreferencesVO);
			}				
		}

		return maintenancePreferencesList;
	}
	
	public Query generateMaintenancePreferencesByAccountDataQuery(Long cId, String accountType, String accountCode) {
		
		Query query =  null;
		StringBuilder sqlStmt = new StringBuilder("");
		
		sqlStmt.append("SELECT " +
				       " op.preference_name, op.description," + 
		               " decode(mpn.approval_ind,'Y','Yes','N','No','A','Approval Needed',null) approval," + 
		               " decode(mpn.maint_pack_ind,'D','Driver','F','Fleet Admin','O','Other',null) maint_pack," +
		               " mpn.spec_instr" +
		               " FROM other_prefs op, maint_pref_notes mpn" +  
		               " WHERE op.opr_id = mpn.opr_opr_id" +
		               " AND mpn.opr_opr_id IS NOT NULL" +
		               " AND mpn.mpa_mpa_id = (SELECT mpa_id" +
		                                       " FROM maint_pref_acct" +
											   " WHERE ea_c_id = :cId" +
											   " AND ea_account_type = :accountType" +
											   " AND ea_account_code = :accountCode" +
		                                       " AND active_to_date is null)" +
		               " ORDER BY mpn.seq NULLS LAST");                            
		
		query = entityManager.createNativeQuery(sqlStmt.toString());
		query.setParameter("cId", cId);
		query.setParameter("accountType", accountType);
		query.setParameter("accountCode", accountCode);
		
		return query;
	}
}
