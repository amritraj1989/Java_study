package com.mikealbert.data.dao;

import java.util.List;

import javax.persistence.Query;

import com.mikealbert.data.entity.GeotabDatabaseSync;

public class GeotabDatabaseSyncDAOImpl extends
		GenericDAOImpl<GeotabDatabaseSync, Long> implements
		GeotabDatabaseSyncDAOCustom {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public List<String> getGeoAccount() {
		Query query = generateGeotabAccountsQuery();
		List<String> resultList = (List<String>)query.getResultList();
		
		return resultList;
	}
	
	private Query generateGeotabAccountsQuery() {
		Query query = null;
		StringBuilder sqlStmt = new StringBuilder("");

		sqlStmt.append("select distinct EA.ACCOUNT_CODE "
					      +" FROM QUOTATION_ELEMENTS QE, LEASE_ELEMENTS LE, CONTRACT_LINES CL, CONTRACTS CON, EXTERNAL_ACCOUNTS EA "
					     +" WHERE QE.LEL_LEL_ID = LE.LEL_ID "
					       +" AND qe.qmd_qmd_id = cl.qmd_qmd_id "
					       +" AND ea.account_code = con.ea_account_code "
					       +" AND con.con_id = cl.con_con_id "
					       +" and LE.ELEMENT_NAME in ('GEOTAB_BAS','GEOTAB_HOS','GEOTAB_PP','GEOTAB_PRO')");

		query = entityManager.createNativeQuery(sqlStmt.toString());

		return query;
	}
}
