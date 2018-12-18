package com.mikealbert.data.dao;

import java.math.BigDecimal;

import javax.persistence.Query;

import com.mikealbert.data.entity.OnbaseUploadedDocs;
import com.mikealbert.exception.MalException;

public  class OnbaseUploadedDocsDAOImpl extends GenericDAOImpl<OnbaseUploadedDocs, Long> implements OnbaseUploadedDocsDAOCustom {
	private static final long serialVersionUID = 1L;

	@Override
	public Long getNextPK() throws MalException {		
		
		StringBuilder sqlStmt = new StringBuilder("SELECT OBD_SEQ.nextval FROM DUAL");
	
		Query query = entityManager.createNativeQuery(sqlStmt.toString());
		return ((BigDecimal)query.getSingleResult()).longValue();
	}
}
