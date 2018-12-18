package com.mikealbert.data.dao;

import java.math.BigDecimal;

import javax.persistence.Query;

import com.mikealbert.data.entity.ReportsSpool;

public class ReportsSpoolDAOImpl extends GenericDAOImpl<ReportsSpool, Long> implements ReportsSpoolDAOCustom {


	/**
	 * 
	 */
	private static final long serialVersionUID = 5290974531795300706L;

	@Override
	public Long generateId() {
		String stmt;
		Long runId;
		Query query;
		
		stmt = "SELECT run_seq.NEXTVAL FROM dual";
		
		query = entityManager.createNativeQuery(stmt.toString());
		runId = ((BigDecimal) query.getSingleResult()).longValue();		
		
		return runId;
	}
	
	
}
