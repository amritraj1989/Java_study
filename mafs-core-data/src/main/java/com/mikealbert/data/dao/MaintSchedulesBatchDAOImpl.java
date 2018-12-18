package com.mikealbert.data.dao;

import java.math.BigDecimal;

import javax.annotation.Resource;
import javax.persistence.Query;

import com.mikealbert.data.entity.MaintSchedulesBatch;

public class MaintSchedulesBatchDAOImpl  extends GenericDAOImpl<MaintSchedulesBatch, Long> implements MaintSchedulesBatchDAOCustom{
	@Resource
	private MaintSchedulesBatchDAO maintSchedulesBatchDAO;
	
	private static final long serialVersionUID = 1L;

	public BigDecimal	getNextBatchId(){
		String queryStr = "select WILLOW2K.BATCH_ID_SEQ.NEXTVAL FROM DUAL";
		Query  query = entityManager.createNativeQuery(queryStr);
		return (BigDecimal)query.getSingleResult();
	}
	
}
