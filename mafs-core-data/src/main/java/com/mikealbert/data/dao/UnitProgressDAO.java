package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.InServiceProgressQueueV;
import com.mikealbert.data.entity.QuotationModel;

/**
* DAO for UpfitProgressChasing
* @author ravresh
*/

public interface UnitProgressDAO extends UnitProgressDAOCustom, JpaRepository<QuotationModel, Long>  {
	@Query("SELECT ispq FROM InServiceProgressQueueV ispq WHERE ispq.unitNo = ?1")
	public InServiceProgressQueueV getInserviceQueueItem(String unitNo);	
	
}
